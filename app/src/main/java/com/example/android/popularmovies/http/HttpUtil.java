package com.example.android.popularmovies.http;

import com.example.android.popularmovies.util.Constants;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by rdayala
 * http://inthecheesefactory.com/blog/retrofit-2.0/en
 * Retrofit 2.0 - RestAdapter is now also renamed to Retrofit
 * In Retrofit 2.0, Converter is not included in the package anymore.
 * You need to plug a Converter in yourself. Plug it in through addConverterFactory
 */

// Service Creation
public class HttpUtil {

    private static MovieDBService service;
    static {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.TMDB_BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(MovieDBService.class);
    }

    public static MovieDBService getService() {
        return service;
    }
}
