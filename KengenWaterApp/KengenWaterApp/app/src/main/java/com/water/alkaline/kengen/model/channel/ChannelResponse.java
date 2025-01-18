package com.water.alkaline.kengen.model.channel;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChannelResponse {

    @SerializedName("nextPageToken")
    @Expose
    @Nullable
    public String nextPageToken = "";

    @SerializedName("pageInfo")
    @Expose
    @Nullable
    public PageInfo pageInfo;

    public class PageInfo {
        @SerializedName("totalResults")
        @Expose
        @Nullable
        public Integer totalResults;
        @SerializedName("resultsPerPage")
        @Expose
        @Nullable
        public Integer resultsPerPage;
    }

    @SerializedName("items")
    @Expose
    @Nullable
    public List<Item> items = null;


    public class Item {

        @SerializedName("id")
        @Expose
        @Nullable
        public Id id;

        public class Id {

            @SerializedName("kind")
            @Expose
            @Nullable
            public String kind;
            @SerializedName("videoId")
            @Expose
            @Nullable
            public String videoId;

        }

        @SerializedName("snippet")
        @Expose
        @Nullable
        public Snippet snippet;


        public class Snippet {

            @SerializedName("publishedAt")
            @Expose
            @Nullable
            public String publishedAt;
            @SerializedName("channelId")
            @Expose
            @Nullable
            public String channelId;
            @SerializedName("title")
            @Expose
            @Nullable
            public String title;
            @SerializedName("description")
            @Expose
            @Nullable
            public String description;
            @SerializedName("thumbnails")
            @Expose
            @Nullable
            public Thumbnails thumbnails;

            public class Thumbnails {

                @SerializedName("default")
                @Expose
                @Nullable
                public Default _default;

                public class Default {

                    @SerializedName("url")
                    @Expose
                    @Nullable
                    public String url;
                    @SerializedName("width")
                    @Expose
                    @Nullable
                    public Integer width;
                    @SerializedName("height")
                    @Expose
                    @Nullable
                    public Integer height;

                }
            }
        }


    }

}