package com.hossein.mvvmtvshows.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hossein.mvvmtvshows.repositories.MostPopularTVShowsRepository;
import com.hossein.mvvmtvshows.responses.TVShowsResponse;

public class MostPopularTVShowsViewModel extends ViewModel {

    private final MostPopularTVShowsRepository repository;

    public MostPopularTVShowsViewModel() {
        repository = new MostPopularTVShowsRepository();
    }

    public LiveData<TVShowsResponse> getMostPopularTVShows(int page) {
        return repository.getMostPopularTVShows(page);
    }
}
