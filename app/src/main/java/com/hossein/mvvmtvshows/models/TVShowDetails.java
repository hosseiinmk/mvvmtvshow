package com.hossein.mvvmtvshows.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TVShowDetails {

    private String url;

    private String description;

    @SerializedName("runtime")
    private String runTime;

    @SerializedName("image_path")
    private String imagePath;

    private String rating;

    private String[] genres;

    private String[] pictures;

    private List<Episode> episodes;

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public String getRunTime() {
        return runTime;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getRating() {
        return rating;
    }

    public String[] getGenres() {
        return genres;
    }

    public String[] getPictures() {
        return pictures;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }
}
