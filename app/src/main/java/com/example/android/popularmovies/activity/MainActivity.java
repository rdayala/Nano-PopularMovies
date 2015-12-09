package com.example.android.popularmovies.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.fragment.MainFragment;
import com.example.android.popularmovies.fragment.MovieDetailFragment;
import com.example.android.popularmovies.model.MovieData;
import com.example.android.popularmovies.util.Constants;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * The main activity is used for loading fragments depends on device's size.
 * It adds a detail fragment if detects a device as tablet.
 * Because fragments don't talk to each other, the activity works as handler for control and
 * transfer data for selected movie from the movie fragment to the detail fragment.
 *
 * Created by rdayala
 */
public class MainActivity extends AppCompatActivity implements MainFragment.Callback {

    private static final String TAG = "MainActivity";

    @Nullable
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private boolean mTwoPane = true;
    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        if (findViewById(R.id.movie_detail_container) != null) {
            // android has loaded tablet view
            mTwoPane = true;
            if (savedInstanceState == null) {
                // since this is the first time, replace the movie detail container
                // with movie detail fragment having empty data
                replaceMovieDetailFragment(MovieDetailFragment.newInstance(new Bundle()));
            }
        } else {
            mTwoPane = false;
        }

        if((savedInstanceState != null)) {
            // it is just an orientation change
            selectedPosition = savedInstanceState.getInt(Constants.POSITION_KEY);
        }
    }

    private void replaceMovieDetailFragment(MovieDetailFragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.movie_detail_container, fragment, MovieDetailFragment.TAG).commit();
    }

    @Override
    public void onItemSelected(MovieData movieData, final Bitmap posterBitmap, View view, int position) {
        selectedPosition = position;
        Parcelable wrapped = Parcels.wrap(movieData);
        if (mTwoPane) {
            // this is a tablet view
            // replace the movie detail container with the detail fragment
            // use the item selected from the recycler view
            Bundle args = new Bundle();
            args.putParcelable(Constants.MOVIE_DETAIL_KEY, wrapped);
            args.putParcelable(Constants.POSTER_IMAGE_KEY, posterBitmap);
            MovieDetailFragment fragment = MovieDetailFragment.newInstance(args);
            replaceMovieDetailFragment(fragment);
        } else {
            // open detail activity
            Intent openDetailIntent = new Intent(this, DetailActivity.class);
            openDetailIntent.putExtra(Constants.MOVIE_DETAIL_KEY, wrapped);
            openDetailIntent.putExtra(Constants.POSTER_IMAGE_KEY, posterBitmap);
            startActivity(openDetailIntent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.POSITION_KEY, selectedPosition);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}
