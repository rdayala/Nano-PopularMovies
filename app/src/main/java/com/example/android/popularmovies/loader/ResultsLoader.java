package com.example.android.popularmovies.loader;

import android.content.Context;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.http.HttpUtil;
import com.example.android.popularmovies.model.MovieData;
import com.example.android.popularmovies.model.MovieResults;
import com.example.android.popularmovies.util.Constants;
import com.example.android.popularmovies.util.PreferenceUtil;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.Response;

/**
 * Task retrieves data from the web server
 * Created by rdayala
 */
public class ResultsLoader extends CommonTaskLoader {
    private static final String TAG = "ResultsLoader";

    public ResultsLoader(Context context) {
        super(context);
    }

    // A task that performs the load
    // This method is called on a background thread and should generate a
    // new set of data to be delivered back to the client.
    @Override
    public List<MovieData> loadInBackground() {
        Call<MovieResults> createdCall = HttpUtil.getService().getMovieResults(
                                            BuildConfig.TMDB_API_KEY,
                                            PreferenceUtil.getPrefs(getContext(),
                                                    Constants.MODE_VIEW,
                                                    Constants.SORT_BY_POPULARITY_DESC)
                                        );
        try {
            Response<MovieResults> result = createdCall.execute();
            return result.body().results;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "IOException occurred in loadInBackground()");
        }
        return null;
    }
}