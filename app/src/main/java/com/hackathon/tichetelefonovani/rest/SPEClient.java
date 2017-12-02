package com.hackathon.tichetelefonovani.rest;

/**
 * Created by povolny on 30.11.2017.
 */

import android.util.Base64;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SPEClient {

    public static final String BASE_URL = "http://10.18.0.196:8600";
    public static final String USER = "tichetelefonovani";
    public static final String PASSWORD = "hackathon";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
            httpClientBuilder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    String credentials = USER + ":" + PASSWORD;
                    String base = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                    Request request = original.newBuilder()
                            .header("Authorization", "Basic " + base)
                            .build();
                    return chain.proceed(request);
                }
            });
            OkHttpClient httpClient = httpClientBuilder.build();


            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
//                    .addConverterFactory(new ChunkingConverterFactory())
                    .addConverterFactory(new NullOnEmptyConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build();
        }
        return retrofit;
    }
}
