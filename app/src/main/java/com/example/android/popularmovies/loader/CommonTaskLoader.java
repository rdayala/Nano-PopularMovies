package com.example.android.popularmovies.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by rdayala
 * http://www.androiddesignpatterns.com/2012/08/implementing-loaders.html
 *
 * To ensure that loads are done on a separate thread, subclasses should extend AsyncTaskLoader<D>.
 * AsyncTaskLoader<D> is an abstract Loader which provides an AsyncTask to do its work.
 * When subclassed, implementing the asynchronous task is as simple as implementing the abstract
 * loadInBackground() method, which is called on a worker thread to perform the data load.
 */

public abstract class CommonTaskLoader<MovieData> extends AsyncTaskLoader<List<MovieData>> {

    // We hold a reference to the loader's data here
    private List<MovieData> mData;

    public CommonTaskLoader(Context context) {
        // Loaders may be used across multiple Activitys (assuming they aren't
        // bound to the LoaderManager), so NEVER hold a reference to the context
        // directly. Doing so will cause you to leak an entire Activity's context.
        // The superclass constructor will store a reference to the Application
        // Context instead, and can be retrieved with a call to getContext().
        super(context);
    }

    // Deliver the results to the registered listener
    // The LoaderManager will act as the "listener" and will forward
    // any results that the Loader delivers to the LoaderCallbacks#onLoadFinished method.
    // In this case, the results will be delivered to MainFragment#onLoadFinished method.
    @Override
    public void deliverResult(List<MovieData> data) {
        mData = data;
        if (isStarted()) {
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(mData);
        }

        if (takeContentChanged() || mData == null) {
            // When the observer detects a change, it should call onContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // The Loader is in a stopped state, so we should attempt to cancel the
        // current load (if there is one).
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        // Ensure the loader has been stopped.
        onStopLoading();
    }
}
