package com.example.android.popularmovies.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.activity.DetailActivity;
import com.example.android.popularmovies.model.MovieData;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.Trailer;
import com.example.android.popularmovies.provider.FavoriteMovieContentProvider;
import com.example.android.popularmovies.task.CommonAsyncTask;
import com.example.android.popularmovies.task.ReviewsAsyncTask;
import com.example.android.popularmovies.task.TrailersAsyncTask;
import com.example.android.popularmovies.util.Constants;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This class is used for fetch and show movie details
 * Created by rdayala
 */
public class MovieDetailFragment extends Fragment {

    public static final String TAG = "MovieDetailFragment";

    @Nullable
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.movie_detail_title_text_view)
    TextView mDetailMovieTitle;

    @Bind(R.id.movie_detail_poster_image_view)
    ImageView mPosterMovie;

    @Bind(R.id.movie_detail_year_text_view)
    TextView mDetailMovieYear;

    @Bind(R.id.movie_detail_rate_value_text_view)
    TextView mDetailRateTextView;

    @Bind(R.id.favorite_fab)
    FloatingActionButton mFavoriteFab;

    @Bind(R.id.movie_detail_synopsys_data_text_view)
    TextView mDetailMovieSynopsis;

    @Bind(R.id.empty_trailer_list)
    TextView mDetailMovieEmptyTrailers;

    @Bind(R.id.empty_review_list)
    TextView mDetailMovieEmptyReviews;

    @Bind(R.id.inc_movie_detail)
    List<View> viewContainers;

    @Bind(R.id.inc_no_selected_movie)
    View noSelectedView;

    @Bind(R.id.movie_detail_trailer_progress_bar)
    ProgressBar mTrailersProgressBar;

    @Bind(R.id.movie_detail_review_progress_bar)
    ProgressBar mReviewsProgressBar;

    @Bind(R.id.movie_detail_trailer_container)
    LinearLayout mTrailerLinearLayout;

    @Bind(R.id.detail_movie_reviews_container)
    LinearLayout mReviewLinearLayout;

    private MovieData mMovieData;
    private Bitmap mPosterImage;
    private boolean mAddedInFavorite;

    // List of movie trailers
    private ArrayList<Trailer> mTrailers;

    // List of reviews
    private ArrayList<Review> mReviews;

    // this is to keep track of first trailer to be used with share intent
    private Trailer mMainTrailer;

    private TrailersAsyncTask trailersAsyncTask;
    private ReviewsAsyncTask reviewsAsyncTask;

    public static MovieDetailFragment newInstance(Bundle bundle) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public MovieDetailFragment() {
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.FAVORITE, mAddedInFavorite);
        outState.putParcelableArrayList(Constants.TRAILERS, mTrailers);
        outState.putParcelableArrayList(Constants.REVIEWS, mReviews);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mPosterImage = getArguments().getParcelable(Constants.POSTER_IMAGE_KEY);
            Parcelable wrapped = getArguments().getParcelable(Constants.MOVIE_DETAIL_KEY);
            mMovieData = Parcels.unwrap(wrapped);
            if (mMovieData != null) {
                // check if the movie data is already in db
                mAddedInFavorite = FavoriteMovieContentProvider.getMovieData(getActivity(), mMovieData.id) != null;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_detail_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.share_movie:
                openShareIntent(mMainTrailer);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, view);

        if (getActivity() instanceof DetailActivity) {
            DetailActivity detailActivity = (DetailActivity) getActivity();
            detailActivity.setToolbar(mToolbar, true, true);
        }
        if (savedInstanceState != null) {
            mTrailers = savedInstanceState.getParcelableArrayList(Constants.TRAILERS);
            mReviews = savedInstanceState.getParcelableArrayList(Constants.REVIEWS);
            mAddedInFavorite = savedInstanceState.getBoolean(Constants.FAVORITE);
            mMainTrailer = savedInstanceState.getParcelable(Constants.MAIN_TRAILER);
            addTrailerViews(mTrailers);
            addReviewViews(mReviews);
        } else {
            executeTasks(mMovieData);
        }
        initView(mMovieData);
        return view;
    }

    /**
     * Runs tasks in background and retrieves movie trailers and reviews
     * @param mMovieData current MovieData
     */
    private void executeTasks(MovieData mMovieData) {

        if (mMovieData == null) {
            return;
        }

        trailersAsyncTask = new TrailersAsyncTask(mMovieData.id, mTrailersProgressBar, new CommonAsyncTask.FetchDataListener<Trailer>() {
            @Override
            public void onFetchData(ArrayList<Trailer> resultList) {
                mTrailers = resultList;
                addTrailerViews(mTrailers);
            }
        });

        reviewsAsyncTask = new ReviewsAsyncTask(mMovieData.id, mReviewsProgressBar, new CommonAsyncTask.FetchDataListener<Review>() {
            @Override
            public void onFetchData(ArrayList<Review> resultList) {
                mReviews = resultList;
                addReviewViews(mReviews);
            }
        });

        trailersAsyncTask.execute();
        reviewsAsyncTask.execute();

    }


    /**
     * Dynamically added trailers views in the view container
     * @param resultList list of Trailer
     */
    private void addTrailerViews(List<Trailer> resultList) {

        final LayoutInflater inflater = LayoutInflater.from(getActivity());

        boolean emptyList = resultList == null || resultList.isEmpty();

        if (resultList != null && !resultList.isEmpty()) {
            mMainTrailer = resultList.get(0);
            for (Trailer trailer : resultList) {
                final String key = trailer.key;
                final View trailerView = inflater.inflate(R.layout.list_item_trailer, mTrailerLinearLayout, false);
                ImageView trailerImage = ButterKnife.findById(trailerView, R.id.trailer_poster_image_view);
                ImageView playImage = ButterKnife.findById(trailerView, R.id.play_trailer_image_view);
                playImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openYouTubeIntent(key);
                    }
                });

                Picasso.with(getActivity())
                        .load(String.format(Constants.YOU_TUBE_IMG_URL, trailer.key))
                        .placeholder(R.drawable.ic_movie_placeholder)
                        .error(R.drawable.ic_movie_placeholder)
                        .into(trailerImage);
                mTrailerLinearLayout.addView(trailerView);
            }
        }
        mDetailMovieEmptyTrailers.setVisibility(emptyList ? View.VISIBLE : View.GONE);

    }

    /**
     * Dynamically added reviews views in the view container
     * @param resultList list of Review
     */
    private void addReviewViews(List<Review> resultList) {

        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        boolean emptyList = resultList == null || resultList.isEmpty();

        if (!emptyList) {
            for (Review review : resultList) {
                final View reviewView = inflater.inflate(R.layout.list_item_review, mReviewLinearLayout, false);
                TextView reviewAuthor = ButterKnife.findById(reviewView, R.id.list_item_review_author_text_view);
                TextView reviewContent = ButterKnife.findById(reviewView, R.id.list_item_review_content_text_view);
                reviewAuthor.setText(review.author);
                reviewContent.setText(review.content);
                mReviewLinearLayout.addView(reviewView);
            }
        }
        mDetailMovieEmptyReviews.setVisibility(emptyList ? View.VISIBLE : View.GONE);
    }


    private void initView(MovieData movieData) {
        if (movieData == null) {
            toggleNonSelectedView(true);
            return;
        }

        toggleNonSelectedView(false);
        switchFabIcon();

        mPosterMovie.setImageBitmap(mPosterImage);

        mDetailMovieTitle.setText(movieData.originalTitle);

        mDetailRateTextView.setText(String.format("%d/10", Math.round(movieData.voteAverage)));

        mDetailMovieSynopsis.setText(movieData.overview);

        if (movieData.getFormattedDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(movieData.getFormattedDate().getTime());
            mDetailMovieYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        }
    }

    private void switchFabIcon() {
        mFavoriteFab.setImageResource(mAddedInFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_outline);
    }


    /**
     * Show/hide empty message and containers depending on movie data
     * @param noMovieData
     */
    private void toggleNonSelectedView(boolean noMovieData) {
        toggleVisibleFab(!noMovieData);
        noSelectedView.bringToFront();
        noSelectedView.setVisibility(noMovieData ? View.VISIBLE : View.GONE);
        for (View view : viewContainers) {
            view.setVisibility(noMovieData ? View.GONE : View.VISIBLE);
        }
    }

    private void toggleVisibleFab(boolean showFab) {
        if (mFavoriteFab != null) {
            mFavoriteFab.setVisibility(showFab ? View.VISIBLE : View.GONE);
        }
    }

    @OnClick(R.id.favorite_fab)
    public void toggleFavorite() {
        int resultMsg;
        if (!mAddedInFavorite) {
            FavoriteMovieContentProvider.putMovieData(getActivity(), mMovieData);
            resultMsg = R.string.added_to_favorite;
            Log.d(TAG, "toggleFavorite() called to add favorite movie");
        } else {
            FavoriteMovieContentProvider.deleteMovieData(getActivity(), mMovieData.id);
            resultMsg = R.string.removed_from_favorite;
            Log.d(TAG, "toggleFavorite() called to delete from favorite movie list");
        }
        mAddedInFavorite = !mAddedInFavorite;
        Snackbar.make(getView(), resultMsg, Snackbar.LENGTH_SHORT).show();
        switchFabIcon();
    }

    private void openYouTubeIntent(String key) {
        Intent youTubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.YOU_TUBE_VIDEO_URL + key));
        youTubeIntent.putExtra("force_fullscreen", true);
        startActivity(youTubeIntent);
    }

    /**
     * Share trailer
     * @param trailer
     */
    private void openShareIntent(Trailer trailer) {
        if (trailer != null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, Constants.YOU_TUBE_VIDEO_URL + trailer.key);
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, mMovieData.originalTitle);
            startActivity(Intent.createChooser(intent, getActivity().getString(R.string.share)));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (trailersAsyncTask != null) {
            trailersAsyncTask.cancel(true);
            trailersAsyncTask = null;
        }

        if (reviewsAsyncTask != null) {
            reviewsAsyncTask.cancel(true);
            reviewsAsyncTask = null;
        }
    }
}