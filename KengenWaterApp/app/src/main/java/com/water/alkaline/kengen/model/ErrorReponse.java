package com.water.alkaline.kengen.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ErrorReponse {
    @SerializedName("error")
    @Expose
    public Error error;

    public static class Error {

        @SerializedName("errors")
        @Expose
        public List<Error__1> errors = null;
        @SerializedName("code")
        @Expose
        public Integer code;
        @SerializedName("message")
        @Expose
        public String message;

        public class Error__1 {

            @SerializedName("domain")
            @Expose
            public String domain;
            @SerializedName("reason")
            @Expose
            public String reason;
            @SerializedName("message")
            @Expose
            public String message;
            @SerializedName("locationType")
            @Expose
            public String locationType;
            @SerializedName("location")
            @Expose
            public String location;

        }

    }
}
