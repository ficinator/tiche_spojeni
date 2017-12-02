package com.hackathon.tichetelefonovani.rest;

import com.google.gson.annotations.SerializedName;

/**
 * Created by povolny on 30.11.2017.
 */

public class SPEResponse {
    @SerializedName("result")
    private SPEResult result;

    public SPEResult getResult() {
        return result;
    }
}
