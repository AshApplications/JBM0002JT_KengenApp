package com.water.alkaline.kengen.model.feedback;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FeedbackResponse {

@SerializedName("flag")
@Expose
public Boolean flag;
@SerializedName("message")
@Expose
public String message;
@SerializedName("code")
@Expose
public Integer code;
@SerializedName("feedbacks")
@Expose
public List<Feedback> feedbacks = new ArrayList<>();

}