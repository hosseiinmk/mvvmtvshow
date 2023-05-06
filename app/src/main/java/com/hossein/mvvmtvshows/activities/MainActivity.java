package com.hossein.mvvmtvshows.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.hossein.mvvmtvshows.R;
import com.hossein.mvvmtvshows.adapters.TVShowsAdapter;
import com.hossein.mvvmtvshows.databinding.ActivityMainBinding;
import com.hossein.mvvmtvshows.listeners.TVShowsListener;
import com.hossein.mvvmtvshows.models.TVShow;
import com.hossein.mvvmtvshows.viewmodels.MostPopularTVShowsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TVShowsListener {

    private ActivityMainBinding activityMainBinding;
    private MostPopularTVShowsViewModel mostPopularTVShowsViewModel;
    private final List<TVShow> tvShows = new ArrayList<>();
    private TVShowsAdapter tvShowsAdapter;
    private int totalPages = 1;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        doInitialization();
    }

    private void doInitialization() {
        activityMainBinding.tvShowsRecyclerView.setHasFixedSize(true);
        mostPopularTVShowsViewModel = new ViewModelProvider(this).get(MostPopularTVShowsViewModel.class);
        tvShowsAdapter = new TVShowsAdapter(tvShows, this);
        activityMainBinding.tvShowsRecyclerView.setAdapter(tvShowsAdapter);
        activityMainBinding.tvShowsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!activityMainBinding.tvShowsRecyclerView.canScrollVertically(1)) {
                    if (currentPage <= totalPages) {
                        currentPage += 1;
                        getMostPopularTVShows();
                    }
                }
            }
        });
        activityMainBinding.watchlistButton.setOnClickListener(v -> startActivity(new Intent(this, WatchListActivity.class)));
        activityMainBinding.searchButton.setOnClickListener(v -> startActivity(new Intent(this, SearchTVShowActivity.class)));
        getMostPopularTVShows();
    }

    private void getMostPopularTVShows() {
        toggleLoading();
        mostPopularTVShowsViewModel.getMostPopularTVShows(currentPage).observe(this, tvShowsResponse -> {
            toggleLoading();
            if (tvShowsResponse != null) {
                totalPages = tvShowsResponse.getPages();
                if (tvShowsResponse.getTvShows() != null) {
                    int oldTVShowsSize = tvShows.size();
                    tvShows.addAll(tvShowsResponse.getTvShows());
                    tvShowsAdapter.notifyItemRangeInserted(oldTVShowsSize, tvShows.size());
                }
            }
        });
    }

    private void toggleLoading() {
        if (currentPage == 1) {
            if (activityMainBinding.getIsLoading() != null) activityMainBinding.setIsLoading(!activityMainBinding.getIsLoading());
            else activityMainBinding.setIsLoading(true);
        } else {
            if (activityMainBinding.getIsLoadingMore() != null) activityMainBinding.setIsLoadingMore(!activityMainBinding.getIsLoadingMore());
            else activityMainBinding.setIsLoadingMore(true);
        }
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = new Intent(getApplicationContext(), TVShowDetailsActivity.class);
        intent.putExtra("tvShow", tvShow);
        startActivity(intent);
    }
}