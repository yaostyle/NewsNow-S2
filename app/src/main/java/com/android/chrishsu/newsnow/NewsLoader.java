package com.android.chrishsu.newsnow;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.List;

// Create a async loader
public class NewsLoader extends AsyncTaskLoader<List<News>> {
    // Create current LOG_TAG name
    private static final String LOG_TAG = News.class.getName();

    // Create global url vars
    private String mUrl;

    // Create loader constructor with: context & url params
    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    // Override onStartLoading
    @Override
    protected void onStartLoading() {
        // Force to run the loader when app starts
        forceLoad();
    }

    // Override loadInBackground
    @Override
    public List<News> loadInBackground() {
        // If there's no url, return null first
        if (mUrl == null) {
            return null;
        }

        // Otherwise, use QueryUtils and fetch json data from the url
        List<News> news = QueryUtils.fetchData(mUrl);

        // Return the news list
        return news;
    }
}
