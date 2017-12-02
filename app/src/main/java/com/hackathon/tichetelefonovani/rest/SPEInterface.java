package com.hackathon.tichetelefonovani.rest;

/**
 * Created by povolny on 30.11.2017.
 */

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Streaming;


public interface SPEInterface {

    @GET("technologies")
    Call<SPEResponse> getTechnologies();

    @POST("stream/http")
    Call<SPEResponse> openHttpStream(@Query("frequency") int frequency, @Query("path") String path);

    @POST("technologies/dictate")
    Call<SPEResponse> attachDictateToStream(
            @Query("stream") String streamId, @Query("model") String model);

    @PUT("stream/http")
    Call<SPEResponse> sendChunks(@Query("stream") String stream, @Body RequestBody data);

    @GET("technologies/dictate")
    Call<SPEResponse> getText(@Query("task") String taskId);
}
