package com.hackathon.tichetelefonovani.recorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.hackathon.tichetelefonovani.rest.SPEInterface;
import com.hackathon.tichetelefonovani.rest.SPEResponse;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by povolny on 1.12.2017.
 */

public class AudioRecorder {
    private static final String TAG = AudioRecord.class.getSimpleName();

    public static final int SAMPLE_RATE = 8000;
    private static final short AUDIO_FORMAT = AudioFormat.CHANNEL_IN_MONO;
    private static final short AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord mRecord = null;
    private Thread mRecordingThread = null;
    private boolean mIsRecording = false;
    private int mBufferSize = 1024;
    private Queue<byte[]> mDataQueue = null;
    private Thread mSendingThread = null;

    public boolean startRecording(SPEInterface spe, String streamId) {
        if (mIsRecording) {
            return false;
        }
        mBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AUDIO_FORMAT, AUDIO_ENCODING);
        mDataQueue = new ConcurrentLinkedQueue<>();
        mRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AUDIO_FORMAT, AUDIO_ENCODING, mBufferSize);
        mRecord.startRecording();

        mIsRecording = true;

        // write to queue
        mRecordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] readBuffer = new byte[mBufferSize];
                while (mIsRecording) {
                    int bytes = mRecord.read(readBuffer, 0, mBufferSize);
                    if (bytes > 0) {
                        Log.d(TAG, "recorded " + bytes + " bytes");
                        mDataQueue.offer(readBuffer);
                    }
                }
            }
        });
        mRecordingThread.start();

        // read from queue and send to SPE
        mSendingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // this should be changed so it ends sometimes
                while (true) {
                    byte[] bytes = mDataQueue.poll();
                    if (bytes == null)
                        continue;
//                    while (bytes == null) {
//                        try {
//                            Thread.sleep(10);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        bytes = mDataQueue.poll();
//                    }
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);
                    Call<SPEResponse> call = spe.sendChunks(streamId, requestBody);
                    Response<SPEResponse> response = null;
                    try {
                         response = call.execute();
                        if (response.isSuccessful()) {
//                            Log.d(TAG, "Success!");
                        } else {
                            Log.e(TAG, response.toString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
//                    new Callback<SPEResponse>() {
//                        @Override
//                        public void onResponse(Call<SPEResponse> call, Response<SPEResponse> response) {
//                            if (response.isSuccessful()) {
//                                Log.d(TAG, "Success!");
//                            } else {
//                                Log.e(TAG, response.toString());
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<SPEResponse> call, Throwable t) {
//                            Log.e(TAG, t.toString());
//                        }
//                    });
                }
            }
        });
        mSendingThread.start();
        return true;
    }

    public boolean stopRecording() {
        mIsRecording = false;
        if (mRecord != null) {
            mRecord.stop();
            try {
                mRecordingThread.join(5000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (mRecordingThread.isAlive()) {
                mRecordingThread.interrupt();
            }
            mRecordingThread = null;
            mRecord.release();
            mRecord = null;
        }
        return true;
    }
}
