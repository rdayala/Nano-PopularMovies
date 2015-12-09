package com.example.android.popularmovies.provider;

import android.content.Context;
import android.net.Uri;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.model.MovieData;

import java.util.List;

import nl.littlerobots.cupboard.tools.provider.CupboardContentProvider;
import nl.littlerobots.cupboard.tools.provider.UriHelper;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by rdayala
 */

public class FavoriteMovieContentProvider extends CupboardContentProvider {

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";
    public static final String DB_NAME = "movies.db";

    static {
        cupboard().register(MovieData.class);
    }

    public FavoriteMovieContentProvider() {
        super(AUTHORITY, DB_NAME, 1);
    }

    public static MovieData getMovieData(Context context, long id) {
        UriHelper uriHelper = UriHelper.with(FavoriteMovieContentProvider.AUTHORITY);
        Uri moviesUri = uriHelper.getUri(MovieData.class);
        return cupboard().withContext(context).query(moviesUri, MovieData.class).withSelection("id = ?", "" + id).get();
    }

    public static void deleteMovieData(Context context, long id) {
        UriHelper uriHelper = UriHelper.with(FavoriteMovieContentProvider.AUTHORITY);
        Uri moviesUri = uriHelper.getUri(MovieData.class);
        cupboard().withContext(context).delete(moviesUri, "id = ?", id + "");
    }

    public static void putMovieData(Context context, MovieData mMovieData) {
        UriHelper uriHelper = UriHelper.with(FavoriteMovieContentProvider.AUTHORITY);
        Uri movieUri = uriHelper.getUri(MovieData.class);
        cupboard().withContext(context).put(movieUri, mMovieData);
    }

    public static List<MovieData> getFavorites(Context context){
        UriHelper uriHelper = UriHelper.with(FavoriteMovieContentProvider.AUTHORITY);
        Uri movieUri = uriHelper.getUri(MovieData.class);
       return cupboard().withContext(context).query(movieUri, MovieData.class).list();
    }
}
