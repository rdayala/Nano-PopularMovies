package com.example.android.popularmovies.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.activity.MainActivity;
import com.example.android.popularmovies.adapter.MovieGridAdapter;
import com.example.android.popularmovies.loader.FavoriteResultsLoader;
import com.example.android.popularmovies.loader.ResultsLoader;
import com.example.android.popularmovies.model.MovieData;
import com.example.android.popularmovies.util.Constants;
import com.example.android.popularmovies.util.PreferenceUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * The class is responsible for loading movie posters
 * Created by rdayala
 * http://www.androiddesignpatterns.com/2012/07/understanding-loadermanager.html
 */

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<MovieData>> {

    public static final String TAG = "MainFragment";

    public interface Callback {
        void onItemSelected(MovieData movieData, Bitmap posterBitmap, View view, int position);
    }

    @Bind(R.id.main_grid_empty_container)
    LinearLayout mNoInternetContainer;

    @Bind(R.id.inc_no_movies)
    View noMoviesView;

    // DATA to populate in recycler view
    private ArrayList<MovieData> mMovieLists;

    // ADAPTER recycler view adapter
    private MovieGridAdapter mMovieAdapter;

    // VIEW - recycler view
    @Bind(R.id.main_movie_grid_recycle_view)
    RecyclerView mPopularGridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // once the activity UI is completely created, start the loader to populate the data
        getLoaderManager().initLoader(0, null, this);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String sortType;
        boolean result;

        switch (item.getItemId()) {
            case R.id.show_favorites:
                sortType = Constants.SHOW_FAVORITES;
                result = true;
                break;
            case R.id.sort_by_popularity_desc:
                sortType = Constants.SORT_BY_POPULARITY_DESC;
                result = true;
                break;
            case R.id.sort_by_rates_desc:
                sortType = Constants.SORT_BY_RATING_DESC;
                result = true;
                break;
            default:
                sortType = Constants.SORT_BY_POPULARITY_DESC;
                result = super.onOptionsItemSelected(item);
                break;
        }

        item.setChecked(true);
        // save the menu option chosen
        PreferenceUtil.savePrefs(getActivity(), Constants.MODE_VIEW, sortType);
        // whenever a new menu option is selected we need to reload the data,
        // based on menu option selected. So, restart the loader
        restartLoader();
        return result;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the fragment UI
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        int gridColumns = getResources().getInteger(R.integer.grid_columns);

        // Unlike, ListView or GridView, RecyclerView requires a layout manager
        final GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), gridColumns);
        mPopularGridView.setLayoutManager(mLayoutManager);
        mPopularGridView.setHasFixedSize(true);

        // dummy data
        mMovieLists = new ArrayList<>();
        // create recycler view adapter
        mMovieAdapter = new MovieGridAdapter(mMovieLists, (Callback) getActivity());
        // attach adapter with recycler view
        mPopularGridView.setAdapter(mMovieAdapter);

        return view;
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    // initLoader calls this method, if the loader with this id is not existing
    @Override
    public Loader<List<MovieData>> onCreateLoader(int id, Bundle args) {
        String preference = PreferenceUtil.getPrefs(getActivity(),
                                            Constants.MODE_VIEW,
                                            Constants.SORT_BY_POPULARITY_DESC);
        // Create loader based on the user choice
        if(preference.equals(Constants.SHOW_FAVORITES)) {
            return new FavoriteResultsLoader(getActivity());
        } else {
            return new ResultsLoader(getActivity());
        }
    }

    @Override
    public void onLoadFinished(Loader<List<MovieData>> loader, List<MovieData> data) {

        mMovieLists = (ArrayList<MovieData>) data;
        mMovieAdapter.addMovies(data);

        // if loader failed to get data, check if internet connection is available or not.
        if (data == null || data.isEmpty()) {
            if (!isOnline(getActivity())) {
                mNoInternetContainer.setVisibility(View.VISIBLE);
            } else {
                toggleShowEmptyMovie(false);
            }
        } else {
            toggleShowEmptyMovie(true);
        }

        MainActivity mainActivity = (MainActivity)getActivity();

        if(mainActivity != null && mainActivity.getSelectedPosition() != -1) {
            mPopularGridView.scrollToPosition(mainActivity.getSelectedPosition());
        }

        String preference = PreferenceUtil.getPrefs(getActivity(),
                Constants.MODE_VIEW,
                Constants.SORT_BY_POPULARITY_DESC);
        // Create loader based on the user choice
        if(preference.equals(Constants.SHOW_FAVORITES) && (data == null || data.isEmpty())) {
            Snackbar.make(getView(), R.string.favorite_movies_not_found, Snackbar.LENGTH_LONG).show();
        } else
            Snackbar.make(getView(), data == null ? R.string.movies_not_found : R.string.movies_data_loaded, Snackbar.LENGTH_LONG).show();
    }

    public static boolean isOnline(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected());
    }

    @Override
    public void onLoaderReset(Loader<List<MovieData>> loader) {
        mMovieAdapter.addMovies(null);
    }

    private void toggleShowEmptyMovie(boolean showMovieGrid) {
        noMoviesView.setVisibility(showMovieGrid ? View.GONE : View.VISIBLE);
        mNoInternetContainer.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
