package com.hossein.mvvmtvshows.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hossein.mvvmtvshows.R;
import com.hossein.mvvmtvshows.adapters.WatchlistAdapter;
import com.hossein.mvvmtvshows.databinding.ActivityWatchListBinding;
import com.hossein.mvvmtvshows.listeners.WatchlistListener;
import com.hossein.mvvmtvshows.models.TVShow;
import com.hossein.mvvmtvshows.utilities.TempDataHolder;
import com.hossein.mvvmtvshows.viewmodels.WatchListViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class WatchListActivity extends AppCompatActivity implements WatchlistListener {

    private ActivityWatchListBinding activityWatchListBinding;
    private WatchListViewModel watchListViewModel;
    private WatchlistAdapter watchlistAdapter;
    private List<TVShow> watchlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityWatchListBinding = DataBindingUtil.setContentView(this, R.layout.activity_watch_list);
        doInitializing();
    }

    private void doInitializing() {
        watchListViewModel = new ViewModelProvider(this).get(WatchListViewModel.class);
//        watchListViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(WatchListViewModel.class);
        activityWatchListBinding.backButton.setOnClickListener(v -> onBackPressed());
        watchlist = new ArrayList<>();
        loadWatchList();
    }

    private void loadWatchList() {
        activityWatchListBinding.setIsLoading(true);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(watchListViewModel.loadWatchList()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                tvShows -> {
                                    activityWatchListBinding.setIsLoading(false);
                                    if (watchlist.size() > 0) watchlist.clear();
                                    watchlist.addAll(tvShows);
                                    watchlistAdapter = new WatchlistAdapter(watchlist, this);
                                    activityWatchListBinding.watchlistRecyclerView.setAdapter(watchlistAdapter);
                                    activityWatchListBinding.watchlistRecyclerView.setVisibility(View.VISIBLE);
                                    compositeDisposable.dispose();
                                }
                        )
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TempDataHolder.IS_WATCHLIST_UPDATED) {
            loadWatchList();
            TempDataHolder.IS_WATCHLIST_UPDATED = false;
        }
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = new Intent(getApplicationContext(), TVShowDetailsActivity.class);
        intent.putExtra("tvShow", tvShow);
        startActivity(intent);
    }

    @Override
    public void removeTVShowFromWatchlist(TVShow tvShow, int position) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(
                watchListViewModel
                        .removeTVShowFromWatchlist(tvShow)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    watchlist.remove(position);
                                    watchlistAdapter.notifyItemRemoved(position);
                                    watchlistAdapter.notifyItemRangeChanged(
                                            position, watchlistAdapter.getItemCount()
                                    );
                                    compositeDisposable.dispose();
                                }
                        )
        );
    }
}