package com.example.android.popularmovies.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.fragment.MovieDetailFragment;

import butterknife.ButterKnife;

/**
 * The activity which is used for setting a detail fragment and showing movie detail data.
 * Created by rdayala
 */
public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            // if it is the first time, add MovieDetailFragment to the activity
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Bundle bundle = new Bundle(getIntent().getExtras());
            fragmentTransaction.add(R.id.movie_detail_container, MovieDetailFragment.newInstance(bundle)).commit();
        }
    }

    public void setToolbar(Toolbar mToolbar, boolean showHomeUp, boolean showTitle) {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(showHomeUp);
            getSupportActionBar().setDisplayShowTitleEnabled(showTitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
