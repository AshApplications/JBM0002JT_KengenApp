package com.water.alkaline.kengen.model.channel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PlaylistResponse implements Serializable {
    @SerializedName("kind")
    @Expose
    public String kind;
    @SerializedName("nextPageToken")
    @Expose
    public String nextPageToken;
    @SerializedName("etag")
    @Expose
    public String etag;
    @SerializedName("items")
    @Expose
    public List<Item> items = null;
    @SerializedName("pageInfo")
    @Expose
    public PageInfo pageInfo;

    public class PageInfo {

        @SerializedName("totalResults")
        @Expose
        public Integer totalResults;
        @SerializedName("resultsPerPage")
        @Expose
        public Integer resultsPerPage;

    }

    public class Item {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("snippet")
        @Expose
        public Snippet snippet;

        public class Snippet {

            @SerializedName("channelId")
            @Expose
            public String channelId;
            @SerializedName("title")
            @Expose
            public String title;
            @SerializedName("description")
            @Expose
            public String description;
            @SerializedName("thumbnails")
            @Expose
            public Thumbnails thumbnails;
            @SerializedName("channelTitle")
            @Expose
            public String channelTitle;
            @SerializedName("playlistId")
            @Expose
            public String playlistId;
            @SerializedName("position")
            @Expose
            public Integer position;
            @SerializedName("resourceId")
            @Expose
            public ResourceId resourceId;

            public class ResourceId {

                @SerializedName("kind")
                @Expose
                public String kind;
                @SerializedName("videoId")
                @Expose
                public String videoId;

            }

            public class Thumbnails {

                @SerializedName("default")
                @Expose
                public Default _default;

                public class Default {

                    @SerializedName("url")
                    @Expose
                    public String url;
                    @SerializedName("width")
                    @Expose
                    public Integer width;
                    @SerializedName("height")
                    @Expose
                    public Integer height;

                }
            }
        }
    }

}
