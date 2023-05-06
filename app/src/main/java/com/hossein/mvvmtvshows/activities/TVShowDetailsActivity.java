package com.hossein.mvvmtvshows.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hossein.mvvmtvshows.R;
import com.hossein.mvvmtvshows.adapters.EpisodesAdapter;
import com.hossein.mvvmtvshows.adapters.ImageSliderAdapter;
import com.hossein.mvvmtvshows.databinding.ActivityTvshowDetailsBinding;
import com.hossein.mvvmtvshows.databinding.LayoutEpisodesBottomSheetBinding;
import com.hossein.mvvmtvshows.models.TVShow;
import com.hossein.mvvmtvshows.utilities.TempDataHolder;
import com.hossein.mvvmtvshows.viewmodels.TVShowDetailsViewModel;

import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TVShowDetailsActivity extends AppCompatActivity {

    private ActivityTvshowDetailsBinding activityTVShowDetailsBinding;
    private TVShowDetailsViewModel tvShowDetailsViewModel;
    private BottomSheetDialog episodesBottomSheetDialog;
    private LayoutEpisodesBottomSheetBinding layoutEpisodesBottomSheetBinding;
    private TVShow tvShow;
    private boolean isTVShowAvailableInWatchlist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTVShowDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_tvshow_details);
        doInitialization();
    }

    private void doInitialization() {
//        tvShowDetailsViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(TVShowDetailsViewModel.class);
        tvShowDetailsViewModel = new ViewModelProvider(this).get(TVShowDetailsViewModel.class);
        activityTVShowDetailsBinding.backBtn.setOnClickListener(v -> onBackPressed());
        tvShow = (TVShow) getIntent().getSerializableExtra("tvShow");
        checkTVShowInWatchlist();
        getTVShowDetails();
    }

    private void checkTVShowInWatchlist() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(tvShowDetailsViewModel.getTVShowFromWatchlist(String.valueOf(tvShow.getId()))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tvShow -> {
                    isTVShowAvailableInWatchlist = true;
                    activityTVShowDetailsBinding.addToWatchListButton.setImageResource(R.drawable.ic_added);
                    compositeDisposable.dispose();
                })
        );
    }

    private void getTVShowDetails() {
        activityTVShowDetailsBinding.setIsLoading(true);
        String tvShowId = String.valueOf(tvShow.getId());
        tvShowDetailsViewModel.getTVShowDetails(tvShowId).observe(this, tvShowDetailsResponse -> {
            activityTVShowDetailsBinding.setIsLoading(false);
            if (tvShowDetailsResponse.getTvShowDetails() != null) {
                if (tvShowDetailsResponse.getTvShowDetails().getPictures() != null) {
                    loadImageSlider(tvShowDetailsResponse.getTvShowDetails().getPictures());
                }
                activityTVShowDetailsBinding.setTvShowImageURL(tvShowDetailsResponse.getTvShowDetails().getImagePath());
                activityTVShowDetailsBinding.imageTVShow.setVisibility(View.VISIBLE);
                activityTVShowDetailsBinding.setDescription(String.valueOf(HtmlCompat.fromHtml(tvShowDetailsResponse.getTvShowDetails().getDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY)));
                activityTVShowDetailsBinding.textDescription.setVisibility(View.VISIBLE);
                activityTVShowDetailsBinding.readMoreBtn.setVisibility(View.VISIBLE);
                activityTVShowDetailsBinding.readMoreBtn.setOnClickListener(v -> {
                    if (activityTVShowDetailsBinding.readMoreBtn.getText().toString().equals("Read More")) {
                        activityTVShowDetailsBinding.textDescription.setEllipsize(null);
                        activityTVShowDetailsBinding.textDescription.setMaxLines(Integer.MAX_VALUE);
                        activityTVShowDetailsBinding.readMoreBtn.setText(R.string.read_less);
                    } else {
                        activityTVShowDetailsBinding.textDescription.setEllipsize(TextUtils.TruncateAt.END);
                        activityTVShowDetailsBinding.textDescription.setMaxLines(4);
                        activityTVShowDetailsBinding.readMoreBtn.setText(R.string.read_more);
                    }
                });
                activityTVShowDetailsBinding.setRate(String.format(Locale.getDefault(), "%.2f", Double.parseDouble(tvShowDetailsResponse.getTvShowDetails().getRating())));
                if (tvShowDetailsResponse.getTvShowDetails().getGenres() != null) {
                    activityTVShowDetailsBinding.setGenre(tvShowDetailsResponse.getTvShowDetails().getGenres()[0]);
                } else {
                    activityTVShowDetailsBinding.textGenre.setText("N/A");
                }
                activityTVShowDetailsBinding.setRunTime(tvShowDetailsResponse.getTvShowDetails().getRunTime());
                activityTVShowDetailsBinding.dividerLine1.setVisibility(View.VISIBLE);
                activityTVShowDetailsBinding.layoutMisc.setVisibility(View.VISIBLE);
                activityTVShowDetailsBinding.dividerLine2.setVisibility(View.VISIBLE);
                activityTVShowDetailsBinding.episodesBtn.setVisibility(View.VISIBLE);
                activityTVShowDetailsBinding.episodesBtn.setOnClickListener(v -> {
                    if (episodesBottomSheetDialog == null) {
                        episodesBottomSheetDialog = new BottomSheetDialog(this);
                        layoutEpisodesBottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.layout_episodes_bottom_sheet, findViewById(R.id.episodesContainer), false);
                        episodesBottomSheetDialog.setContentView(layoutEpisodesBottomSheetBinding.getRoot());
                        layoutEpisodesBottomSheetBinding.episodesRecyclerView.setAdapter(new EpisodesAdapter(tvShowDetailsResponse.getTvShowDetails().getEpisodes()));
                        layoutEpisodesBottomSheetBinding.textTitle.setText(String.format("Episode | %s", tvShow.getName()));
                        layoutEpisodesBottomSheetBinding.closeBtn.setOnClickListener(view -> episodesBottomSheetDialog.cancel());
                    }
                    FrameLayout frameLayout = episodesBottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                    if (frameLayout != null) {
                        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
                        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                    episodesBottomSheetDialog.show();
                });
                activityTVShowDetailsBinding.addToWatchListButton.setOnClickListener(v -> {
                    CompositeDisposable compositeDisposable = new CompositeDisposable();
                    if (isTVShowAvailableInWatchlist) {
                        compositeDisposable.add(tvShowDetailsViewModel.removeTVShowFromWatchlist(tvShow)
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    isTVShowAvailableInWatchlist = false;
                                    TempDataHolder.IS_WATCHLIST_UPDATED = true;
                                    activityTVShowDetailsBinding.addToWatchListButton.setImageResource(R.drawable.ic_watchlist);
                                    Toast.makeText(getApplicationContext(), "removed from watchlist", Toast.LENGTH_SHORT).show();
                                    compositeDisposable.dispose();
                                }));
                    } else {
                        compositeDisposable.add(tvShowDetailsViewModel.addToWatchList(tvShow).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
                            TempDataHolder.IS_WATCHLIST_UPDATED = true;
                            isTVShowAvailableInWatchlist = true;
                            activityTVShowDetailsBinding.addToWatchListButton.setImageResource(R.drawable.ic_added);
                            Toast.makeText(getApplicationContext(), "Added to watchlist", Toast.LENGTH_SHORT).show();
                            compositeDisposable.dispose();
                        }));
                    }
                });
                activityTVShowDetailsBinding.addToWatchListButton.setVisibility(View.VISIBLE);
                loadBasicTVShowDetails();
            }
        });
    }

    private void loadImageSlider(String[] sliderImages) {
        activityTVShowDetailsBinding.sliderViewPager.setOffscreenPageLimit(1);
        activityTVShowDetailsBinding.sliderViewPager.setAdapter(new ImageSliderAdapter(sliderImages));
        activityTVShowDetailsBinding.sliderViewPager.setVisibility(View.VISIBLE);
        activityTVShowDetailsBinding.viewFadingEdge.setVisibility(View.VISIBLE);
        setupSliderIndicator(sliderImages.length);
        activityTVShowDetailsBinding.sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                setCurrentSliderIndicator(position);
            }
        });
    }

    private void setupSliderIndicator(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.background_slider_indicator_inactive));
            indicators[i].setLayoutParams(layoutParams);
            activityTVShowDetailsBinding.layoutSliderIndicators.addView(indicators[i]);
        }
        activityTVShowDetailsBinding.layoutSliderIndicators.setVisibility(View.VISIBLE);
        setCurrentSliderIndicator(0);
    }

    private void setCurrentSliderIndicator(int position) {
        int childCount = activityTVShowDetailsBinding.layoutSliderIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) activityTVShowDetailsBinding.layoutSliderIndicators.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.background_slider_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.background_slider_indicator_inactive));
            }
        }
    }

    private void loadBasicTVShowDetails() {
        activityTVShowDetailsBinding.setTvShowName(tvShow.getName());
        activityTVShowDetailsBinding.setTvShowNetworkCountry(tvShow.getNetwork() + " (" + tvShow.getCountry() + ")");
        activityTVShowDetailsBinding.setTvShowStatus(tvShow.getStatus());
        activityTVShowDetailsBinding.setTvShowStartDate(tvShow.getStartDate());
        activityTVShowDetailsBinding.textName.setVisibility(View.VISIBLE);
        activityTVShowDetailsBinding.textNetworkCountry.setVisibility(View.VISIBLE);
        activityTVShowDetailsBinding.textStatus.setVisibility(View.VISIBLE);
        activityTVShowDetailsBinding.textStartDate.setVisibility(View.VISIBLE);
    }
}