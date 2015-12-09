package com.example.android.popularmovies.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.fragment.MainFragment;
import com.example.android.popularmovies.model.MovieData;
import com.example.android.popularmovies.util.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This is a RecyclerView adapter which controls grid data and reusing view holders.
 * Created by rdayala
 * http://code.tutsplus.com/tutorials/getting-started-with-recyclerview-and-cardview-on-android--cms-23465
 *
 */
public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.ViewHolder> {

    private List<MovieData> movieList = new ArrayList<>();
    private MainFragment.Callback mCallback;

    public MovieGridAdapter(ArrayList<MovieData> mMovieLists, MainFragment.Callback mCallback) {
        this.movieList = mMovieLists;
        this.mCallback = mCallback;
    }

    @Override
    public MovieGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the grid item UI
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item_column, parent, false);

        // the ViewHolder will refer to the grid view item UI
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MovieGridAdapter.ViewHolder holder, final int position) {
        // Update the view holder UI with the data
        final MovieData movieData = movieList.get(position);
        String imageUrl = Constants.IMAGE_MOVIE_URL + Constants.IMAGE_SIZE_W185 + movieData.posterPath;
        Picasso.with(holder.mMovieImageView.getContext()).load(imageUrl).placeholder(R.drawable.ic_movie_placeholder).
                into(holder.mMovieImageView);

        holder.mMovieImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // get the movie poster Drawable bitmap which is used in MovieDetail Fragment
                Bitmap posterBitmap = ((BitmapDrawable) holder.mMovieImageView.getDrawable()).getBitmap();
                mCallback.onItemSelected(movieData, posterBitmap, holder.mMovieImageView, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList == null ? 0 : movieList.size();
    }

    public void addMovies(List<MovieData> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        movieList = data;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.grid_item_poster_image_view)
        ImageView mMovieImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
