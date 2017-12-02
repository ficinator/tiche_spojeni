package com.hackathon.tichetelefonovani.rest;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by povolny on 30.11.2017.
 */

public class SPEResult {
    @SerializedName("name")
    private String name;
    @SerializedName("stream")
    private String stream;
    @SerializedName("stream_task_info")
    private StreamTaskInfo streamTaskInfo;
    @SerializedName("one_best_result")
    private OneBestResult oneBestResult;

    public String getName() {
        return name;
    }

    public String getStreamId() {
        return stream;
    }

    public String getTaskId() {
        return streamTaskInfo.getId();
    }

    public String getTaskStreamId() {
        return streamTaskInfo.getStreamId();
    }

    public List<Segment> getSegmentation() {
        return oneBestResult.getSegmentation();
    }

    public boolean isLast() {
        return oneBestResult.isLast();
    }

    private class StreamTaskInfo {
        @SerializedName("id")
        private String id;
        @SerializedName("state")
        private String state;
        @SerializedName("stream_id")
        private String streamId;

        private String getId() {
            return id;
        }

        private String getState() {
            return state;
        }

        private String getStreamId() {
            return streamId;
        }
    }

    private class OneBestResult {
        @SerializedName("is_last")
        private boolean isLast;
        @SerializedName("segmentation")
        private List<Segment> segmentation;

        public boolean isLast() {
            return isLast;
        }

        public List<Segment> getSegmentation() {
            return segmentation;
        }
    }
}
