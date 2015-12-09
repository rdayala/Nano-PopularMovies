package com.example.android.popularmovies.loader;

import android.content.Context;

import com.example.android.popularmovies.model.MovieData;
import com.example.android.popularmovies.provider.FavoriteMovieContentProvider;

import java.util.List;

/**
 * Task retrieves data from content provider
 * Created by rdayala
 */
public class FavoriteResultsLoader extends CommonTaskLoader {
    private static final String TAG = "FavoriteResultsLoader";

    public FavoriteResultsLoader(Context context) {
        super(context);
    }

    // A task that performs the asynchronous load
    // This method is called on a background thread and should generate a
    // new set of data to be delivered back to the client.
    @Override
    public List<MovieData> loadInBackground() {
        return FavoriteMovieContentProvider.getFavorites(getContext());
    }
}