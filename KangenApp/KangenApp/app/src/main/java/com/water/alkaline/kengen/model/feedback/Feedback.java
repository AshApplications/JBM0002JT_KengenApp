package com.water.alkaline.kengen.model.feedback;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Feedback {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("userid")
    @Expose
    public String userid;
    @SerializedName("star")
    @Expose
    public String star;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("reply")
    @Expose
    public String reply;
    @SerializedName("feedtime")
    @Expose
    public String feedtime;

}