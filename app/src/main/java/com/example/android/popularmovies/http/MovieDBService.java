package com.example.android.popularmovies.http;

import com.example.android.popularmovies.model.MovieResults;
import com.example.android.popularmovies.model.ReviewResults;
import com.example.android.popularmovies.model.TrailersResults;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by rdayala
 * http://inthecheesefactory.com/blog/retrofit-2.0/en
 * Base URL: always ends with /
 * @Url: DO NOT start with /
 */

// Interface for API calls
// Service Declaration
public interface MovieDBService {

    @GET("3/discover/movie")
    Call<MovieResults> getMovieResults(@Query("api_key") String apiKey, @Query("sort_by") String sortBy);

    @GET("3/movie/{id}/videos")
    Call<TrailersResults> getTrailersResults(@Path("id") long movieId, @Query("api_key") String apiKey);

    @GET("3/movie/{id}/reviews")
    Call<ReviewResults> getReviewsResults(@Path("id") long movieId, @Query("api_key") String apiKey);

}
