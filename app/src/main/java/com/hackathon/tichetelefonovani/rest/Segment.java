package com.hackathon.tichetelefonovani.rest;

import com.google.gson.annotations.SerializedName;

/**
 * Created by povolny on 1.12.2017.
 */

public class Segment {
    @SerializedName("channel_id")
    private int channelId;
    @SerializedName("score")
    private float score;
    @SerializedName("confidence")
    private float confidence;
    @SerializedName("start")
    private int start;
    @SerializedName("end")
    private int end;
    @SerializedName("word")
    private String word;

    public String getWord() {
        return word;
    }
}
