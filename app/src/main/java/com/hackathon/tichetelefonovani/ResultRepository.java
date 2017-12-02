package com.hackathon.tichetelefonovani;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.hackathon.tichetelefonovani.rest.SPEInterface;
import com.hackathon.tichetelefonovani.rest.SPEResponse;
import com.hackathon.tichetelefonovani.rest.SPEResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by povolny on 30.11.2017.
 */

public class ResultRepository {
    public SPEInterface spe;

//    public LiveData<SPEResult> getResponse() {
//        final MutableLiveData<SPEResult> data = new MutableLiveData<>();
//        spe.login().enqueue(new Callback<SPEResponse>() {
//            @Override
//            public void onResponse(Call<SPEResponse> call, Response<SPEResponse> response) {
//                data.setValue(response.body().getResult());
//            }
//
//            @Override
//            public void onFailure(Call<SPEResponse> call, Throwable t) {
//
//            }
//        });
//        return data;
//    }
}
