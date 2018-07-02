package com.android.chrishsu.newsnow;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;

import java.util.ArrayList;
import java.util.List;

// NewsActivity - Main & entry activity with loader callbacks
public class NewsActivity extends AppCompatActivity implements LoaderCallbacks<List<News>> {
    // Setting vars
    public static final String LOG_TAG = NewsActivity.class.getName();
    private NewsAdapter newsAdapter;
    private TextView emptyStateTextView;
    private static final int NEWS_LOADER_ID = 1;

    // Separate API paramters vars
    private static final String API_KEY = "61214367-5a82-4571-a662-7561b8d0d6dc";
    private static final String FROM_DATE_VAL = "2015-01-01";
    private static final String SHOW_TAG_VAL = "contributor";
    private static final String GUARDIAN_NEWS_REQUEST_URL = "https://content.guardianapis.com/search";

    // Override onCreateLoader so it initiates the loader when app starts
    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String searchKeyword = sharedPrefs.getString(
                getString(R.string.settings_search_keyword_key),
                getString(R.string.settings_search_keyword_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.setting_order_by_default));


        Uri baseUri = Uri.parse(GUARDIAN_NEWS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("api-key", API_KEY);
        uriBuilder.appendQueryParameter("q", searchKeyword);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("from-date", FROM_DATE_VAL);
        uriBuilder.appendQueryParameter("show-tags", SHOW_TAG_VAL);

        // Return a new NewsLoader instance and pass uriBuilder
        return new NewsLoader(this, uriBuilder.toString());
    }

    // Override onLoadFinished method for loader
    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        // First clean everything from adapter
        newsAdapter.clear();
        // Also, change the empty state's text for no feeds condition.
        emptyStateTextView.setText(R.string.no_news_feeds);

        // Hide the loading indicator since the loader process is done
        View loadingInidicator = findViewById(R.id.loading_indicator);
        loadingInidicator.setVisibility(View.GONE);

        // If the process fetched any data, attach them to the adapter
        if (news != null && !news.isEmpty()) {
            newsAdapter.addAll(news);
        }
    }

    // Override onLoaderRest
    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // If reset state occurred, clean up all data in the adapter
        newsAdapter.clear();
    }

    // Override app's onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the init content layout activity
        setContentView(R.layout.news_activity);

        // Create ListView var and connect to ListView layout
        ListView newsListView = (ListView) findViewById(R.id.list);

        // Create an adapter for News
        newsAdapter = new NewsAdapter(this, new ArrayList<News>());

        // Set the ListView to NewsAdapter
        newsListView.setAdapter(newsAdapter);

        // Create OnItemClickListener to do Intent function
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // Override the onItemClick function
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Get the current news data via [i] (index position)
                News currentNews = newsAdapter.getItem(i);

                // Create an uri object from getNewsUrl function
                Uri newsUri = Uri.parse(currentNews.getNewsUrl());

                // Create Intent and send the Uri object
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Start Intent
                startActivity(websiteIntent);
            }
        });

        // Set and connect layout for EmptyView
        emptyStateTextView = findViewById(R.id.empty_view);
        newsListView.setEmptyView(emptyStateTextView);

        // Create ConnectivityManager and get the current connection state
        // This is to validate if there's no connection or Airplane mode is enabled
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get the current network information
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there's network info or it's connected, initialize loader manager
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Else, hide the loading indicator and set the warning text
            // with "No internet connection".
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            emptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
