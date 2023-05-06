package com.hossein.mvvmtvshows.models;

import com.google.gson.annotations.SerializedName;

public class Episode {

    private String season;

    private String episode;

    private String name;

    @SerializedName("air_date")
    private String airDate;

    public String getSeason() {
        return season;
    }

    public String getEpisode() {
        return episode;
    }

    public String getName() {
        return name;
    }

    public String getAirDate() {
        return airDate;
    }
}
