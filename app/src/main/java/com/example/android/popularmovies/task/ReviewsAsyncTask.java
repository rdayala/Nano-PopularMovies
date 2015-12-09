package com.example.android.popularmovies.task;

import android.util.Log;
import android.widget.ProgressBar;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.http.HttpUtil;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.ReviewResults;

import java.io.IOException;
import java.util.ArrayList;

import retrofit.Call;
import retrofit.Response;

/**
 * Created by rdayala
 */

public class ReviewsAsyncTask extends CommonAsyncTask<Review> {

    private static final String TAG = "ReviewsAsyncTask";
    private final long mMovieId;

    public ReviewsAsyncTask(long movieId, ProgressBar mProgressBar, FetchDataListener mListener) {
        super(mProgressBar, mListener);
        this.mMovieId = movieId;
    }

    @Override
    protected ArrayList<Review> doInBackground(Void... params) {
        Call<ReviewResults> createdCall = HttpUtil.getService().getReviewsResults(mMovieId, BuildConfig.TMDB_API_KEY);
        try {
            Response<ReviewResults> result = createdCall.execute();
            return result.body().results;
        } catch (IOException e) {
            Log.e(TAG, "IOException occurred in doInBackground()");
        }
        return null;
    }
}
