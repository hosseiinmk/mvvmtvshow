package com.hossein.mvvmtvshows.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.hossein.mvvmtvshows.R;
import com.hossein.mvvmtvshows.databinding.ItemEpisodeBinding;
import com.hossein.mvvmtvshows.models.Episode;

import java.util.List;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.ItemViewHolder> {

    private final List<Episode> episodes;
    private LayoutInflater layoutInflater;

    public EpisodesAdapter(List<Episode> episodes) {
        this.episodes = episodes;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ItemEpisodeBinding itemEpisodeBinding = DataBindingUtil.inflate(
                layoutInflater, R.layout.item_episode, parent, false
        );
        return new ItemViewHolder(itemEpisodeBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bindEpisode(episodes.get(position));
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final ItemEpisodeBinding itemEpisodeBinding;

        public ItemViewHolder(ItemEpisodeBinding itemEpisodeBinding) {
            super(itemEpisodeBinding.getRoot());
            this.itemEpisodeBinding = itemEpisodeBinding;
        }

        public void bindEpisode(Episode episode) {
            String title = "S";
            String season = episode.getSeason();
            if (season.length() == 1) season = "0".concat(season);
            String episodeNumber = episode.getEpisode();
            if (episodeNumber.length() == 1) episodeNumber = "0".concat(episodeNumber);
            episodeNumber = "E".concat(episodeNumber);
            title = title.concat(season).concat(episodeNumber);
            itemEpisodeBinding.setTitle(title);
            itemEpisodeBinding.setName(episode.getName());
            itemEpisodeBinding.setAirDate(episode.getAirDate());
        }
    }
}
