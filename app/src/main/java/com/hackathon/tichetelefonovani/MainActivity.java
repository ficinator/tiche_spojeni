package com.hackathon.tichetelefonovani;

import android.content.Context;
import android.media.AudioRecord;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hackathon.tichetelefonovani.recorder.AudioRecorder;
import com.hackathon.tichetelefonovani.rest.SPEClient;
import com.hackathon.tichetelefonovani.rest.SPEInterface;
import com.hackathon.tichetelefonovani.rest.SPEResponse;
import com.hackathon.tichetelefonovani.rest.SPEResult;
import com.hackathon.tichetelefonovani.rest.Segment;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int DICTATE_RESULT_PERIOD = 1000;

    private SPEInterface mSpe = null;
    private String mTaskId = null;
    private AudioRecorder mRecorder = null;
    private Timer mPollTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = this.findViewById(R.id.start_button);
        Button stopButton = this.findViewById(R.id.stop_button);

        mSpe = SPEClient.getClient().create(SPEInterface.class);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHttpStream();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
            }
        });
    }

    private void openHttpStream() {
        String path = "/" + getResources().getResourceEntryName(R.raw.test1) + ".wav";
        Call<SPEResponse> call = mSpe.openHttpStream(AudioRecorder.SAMPLE_RATE, path);
        call.enqueue(new Callback<SPEResponse>() {
            @Override
            public void onResponse(Call<SPEResponse> call, Response<SPEResponse> response) {
                if (response.isSuccessful()) {
                    String streamId = response.body().getResult().getStreamId();
                    attachDictateToStream(streamId);
                }
                else {
                    Log.e(TAG, response.toString());
                }
            }

            @Override
            public void onFailure(Call<SPEResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void attachDictateToStream(String streamId) {
        Call<SPEResponse> call = mSpe.attachDictateToStream(streamId, getString(R.string.model));
        call.enqueue(new Callback<SPEResponse>() {
            @Override
            public void onResponse(Call<SPEResponse> call, Response<SPEResponse> response) {
                if (response.isSuccessful()) {
                    SPEResult result = response.body().getResult();
                    String taskId = result.getTaskId();
                    startDictate(streamId, taskId);
                }
                else {
                    Log.e(TAG, response.toString());
                }
            }

            @Override
            public void onFailure(Call<SPEResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private boolean startDictate(String streamId, String taskId) {
        mRecorder = new AudioRecorder();
        if (mRecorder.startRecording(mSpe, streamId)) {
            Log.d(TAG, "recording started");
        }

        mPollTimer = new Timer();
        mPollTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Call<SPEResponse> call = mSpe.getText(taskId);
                call.enqueue(new Callback<SPEResponse>() {
                    @Override
                    public void onResponse(Call<SPEResponse> call, Response<SPEResponse> response) {
                        if (response.isSuccessful()) {
                            SPEResult result = response.body().getResult();
                            if (result.isLast()) {
                                Log.d(TAG, "last result received");
                                mPollTimer.cancel();
                                mPollTimer = null;
                            }
                            String sentence = "";
                            for (Segment segment : result.getSegmentation()) {
                                String word = segment.getWord();
                                switch (word) {
                                    case "<s>":
                                        break;
                                    case "<sil/>":
                                        sentence += ".";
                                        break;
                                    default:
                                        sentence += " " + word;
                                }
                            }
                            Log.d(TAG, sentence);
                        }
                        else {
                            Log.e(TAG, response.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<SPEResponse> call, Throwable t) {
                        Log.e(TAG, t.toString());
                    }
                });
            }
        }, 0, DICTATE_RESULT_PERIOD);

        return true;
    }

    private boolean stopRecording() {
        if (mRecorder.stopRecording()) {
            Log.d(TAG, "recording stopped");
            return true;
        }
        return false;
    }

}
