package com.hossein.mvvmtvshows.listeners;

import com.hossein.mvvmtvshows.models.TVShow;

public interface WatchlistListener {
    void onTVShowClicked(TVShow tvShow);

    void removeTVShowFromWatchlist(TVShow tvShow, int position);
}
