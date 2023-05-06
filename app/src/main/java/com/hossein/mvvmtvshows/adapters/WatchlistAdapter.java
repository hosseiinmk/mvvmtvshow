package com.hossein.mvvmtvshows.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.hossein.mvvmtvshows.R;
import com.hossein.mvvmtvshows.databinding.ItemTvShowBinding;
import com.hossein.mvvmtvshows.listeners.WatchlistListener;
import com.hossein.mvvmtvshows.models.TVShow;

import java.util.List;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.TVShowsHolder> {

    private final List<TVShow> tvShows;
    private LayoutInflater layoutInflater;
    private final WatchlistListener watchlistListener;

    public WatchlistAdapter(List<TVShow> tvShows, WatchlistListener watchlistListener) {
        this.tvShows = tvShows;
        this.watchlistListener = watchlistListener;
    }

    @NonNull
    @Override
    public TVShowsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ItemTvShowBinding itemTvShowBinding = DataBindingUtil.inflate(
                layoutInflater, R.layout.item_tv_show, parent, false
        );
        return new TVShowsHolder(itemTvShowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TVShowsHolder holder, int position) {
        holder.bindTVShow(tvShows.get(position));
    }

    @Override
    public int getItemCount() {
        return tvShows.size();
    }

    class TVShowsHolder extends RecyclerView.ViewHolder {

        private final ItemTvShowBinding itemTVShowBinding;

        public TVShowsHolder(ItemTvShowBinding itemTvShowBinding) {
            super(itemTvShowBinding.getRoot());
            this.itemTVShowBinding = itemTvShowBinding;
        }

        public void bindTVShow(TVShow tvShow) {
            itemTVShowBinding.setTvShow(tvShow);
            itemTVShowBinding.executePendingBindings();
            itemTVShowBinding.getRoot().setOnClickListener(v -> watchlistListener.onTVShowClicked(tvShow));
            itemTVShowBinding.deleteButton.setOnClickListener(v ->
                    watchlistListener.removeTVShowFromWatchlist(tvShow, getAdapterPosition())
            );
            itemTVShowBinding.deleteButton.setVisibility(View.VISIBLE);
        }
    }
}
