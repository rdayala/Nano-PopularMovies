package com.example.android.popularmovies.model;

import android.text.TextUtils;
import android.util.Log;

import com.example.android.popularmovies.util.Constants;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents movie data retrieved from the web server or local database
 */

@Parcel
public class MovieData {

    public Long _id;
    public long id;
    @SerializedName("original_title")
    public String originalTitle;
    public String overview;
    @SerializedName("release_date")
    public String releaseDate;
    @SerializedName("poster_path")
    public String posterPath;
    @SerializedName("backdrop_path")
    public String backdropPath;
    @SerializedName("vote_average")
    public double voteAverage;
    @SerializedName("vote_count")
    public int voteCount;
    public double popularity;

    public MovieData() {
    }

    public Date getFormattedDate() {
        if (!TextUtils.isEmpty(releaseDate)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.MOVIE_DATE_FORMAT);
            try {
                return simpleDateFormat.parse(releaseDate);
            } catch (ParseException e) {
                Log.e("MovieData", "getFormattedDate() returned error: " + e);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "MovieData{" +
                "_id=" + _id +
                ", id=" + id +
                ", originalTitle='" + originalTitle + '\'' +
                ", overview='" + overview + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", backdropPath='" + backdropPath + '\'' +
                ", voteAverage=" + voteAverage +
                ", voteCount=" + voteCount +
                ", popularity=" + popularity +
                '}';
    }
}
