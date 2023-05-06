package com.hossein.mvvmtvshows.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hossein.mvvmtvshows.repositories.SearchTVShowRepository;
import com.hossein.mvvmtvshows.responses.TVShowsResponse;

public class SearchTVShowViewModel extends ViewModel {

    private final SearchTVShowRepository searchTVShowRepository;

    public SearchTVShowViewModel() {
        searchTVShowRepository = new SearchTVShowRepository();
    }

    public LiveData<TVShowsResponse> searchTVShow(String query, int page) {
        return searchTVShowRepository.searchTVShow(query, page);
    }
}
