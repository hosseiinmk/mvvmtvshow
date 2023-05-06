package com.hossein.mvvmtvshows.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.hossein.mvvmtvshows.R;
import com.hossein.mvvmtvshows.databinding.ItemTvShowBinding;
import com.hossein.mvvmtvshows.listeners.TVShowsListener;
import com.hossein.mvvmtvshows.models.TVShow;

import java.util.List;

public class TVShowsAdapter extends RecyclerView.Adapter<TVShowsAdapter.TVShowsHolder> {

    private final List<TVShow> tvShows;
    private LayoutInflater layoutInflater;
    private final TVShowsListener tvShowsListener;

    public TVShowsAdapter(List<TVShow> tvShows, TVShowsListener tvShowsListener) {
        this.tvShows = tvShows;
        this.tvShowsListener = tvShowsListener;
    }

    @NonNull
    @Override
    public TVShowsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) layoutInflater = LayoutInflater.from(parent.getContext());
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

        private final ItemTvShowBinding itemTvShowBinding;

        public TVShowsHolder(ItemTvShowBinding itemTvShowBinding) {
            super(itemTvShowBinding.getRoot());
            this.itemTvShowBinding = itemTvShowBinding;
        }

        public void bindTVShow(TVShow tvShow) {
            itemTvShowBinding.setTvShow(tvShow);
            itemTvShowBinding.executePendingBindings();
            itemTvShowBinding.getRoot().setOnClickListener(v -> tvShowsListener.onTVShowClicked(tvShow));
        }
    }
}
