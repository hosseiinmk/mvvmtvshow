package com.hossein.mvvmtvshows.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;

import com.hossein.mvvmtvshows.R;
import com.hossein.mvvmtvshows.adapters.TVShowsAdapter;
import com.hossein.mvvmtvshows.databinding.ActivitySearchTvshowBinding;
import com.hossein.mvvmtvshows.listeners.TVShowsListener;
import com.hossein.mvvmtvshows.models.TVShow;
import com.hossein.mvvmtvshows.viewmodels.SearchTVShowViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchTVShowActivity extends AppCompatActivity implements TVShowsListener {

    private ActivitySearchTvshowBinding activitySearchTVshowBinding;
    private SearchTVShowViewModel searchTVShowViewModel;
    private final List<TVShow> tvShows = new ArrayList<>();
    private TVShowsAdapter tvShowsAdapter;
    private int currentPage = 1;
    private int totalAvailablePages = 1;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySearchTVshowBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_tvshow);
        doInitializing();
    }

    private void doInitializing() {
        activitySearchTVshowBinding.backButton.setOnClickListener(v -> onBackPressed());
        activitySearchTVshowBinding.searchTVShowRecyclerView.setHasFixedSize(true);
        searchTVShowViewModel = new ViewModelProvider(this).get(SearchTVShowViewModel.class);
//        searchTVShowViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(SearchTVShowViewModel.class);
        tvShowsAdapter = new TVShowsAdapter(tvShows, this);
        activitySearchTVshowBinding.searchTVShowRecyclerView.setAdapter(tvShowsAdapter);
        activitySearchTVshowBinding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer != null) timer.cancel();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty()) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                currentPage = 1;
                                totalAvailablePages = 1;
                                searchTVShow(s.toString());
                            });
                        }
                    }, 1000);
                } else {
                    tvShows.clear();
                    tvShowsAdapter.notifyDataSetChanged();
                }
            }
        });
        activitySearchTVshowBinding.searchTVShowRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!activitySearchTVshowBinding.searchTVShowRecyclerView.canScrollVertically(1)) {
                    if (!activitySearchTVshowBinding.inputSearch.getText().toString().isEmpty()) {
                        if (currentPage <= totalAvailablePages) {
                            currentPage += 1;
                            searchTVShow(activitySearchTVshowBinding.inputSearch.getText().toString().trim());
                        }
                    }
                }
            }
        });
        activitySearchTVshowBinding.inputSearch.requestFocus();
    }

    private void searchTVShow(String query) {
        toggleLoading();
        searchTVShowViewModel.searchTVShow(query, currentPage).observe(this, tvShowsResponse -> {
            toggleLoading();
            if (tvShowsResponse != null) {
                totalAvailablePages = tvShowsResponse.getPages();
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
            if (activitySearchTVshowBinding.getIsLoading() != null)
                activitySearchTVshowBinding.setIsLoading(!activitySearchTVshowBinding.getIsLoading());
            else {
                activitySearchTVshowBinding.setIsLoading(true);
            }
        } else {
            if (activitySearchTVshowBinding.getIsLoadingMore() != null)
                activitySearchTVshowBinding.setIsLoadingMore(!activitySearchTVshowBinding.getIsLoadingMore());
            else
                activitySearchTVshowBinding.setIsLoadingMore(true);
        }
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = new Intent(getApplicationContext(), TVShowDetailsActivity.class);
        intent.putExtra("tvShow", tvShow);
        startActivity(intent);
    }
}