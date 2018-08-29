package com.example.android.footballnews;

import android.app.LoaderManager;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<NewsItem>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String QUERY_URL = "http://content.guardianapis.com/search?q=football&section=football&order-by=newest&show-tags=contributor&api-key=6074cebf-b0f3-44b0-b7c5-4554f1f0d43c";
    private static final int NEWS_LOADER_ID = 1;
    private ListView list;
    private TextView emptyList;
    private NewsCustomAdapter adapter;
    private LoaderManager loaderManager;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emptyList = (TextView) findViewById(R.id.no_items);
        list = (ListView) findViewById(R.id.news_list);
        adapter = new NewsCustomAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<NewsItem>());
        list.setAdapter(adapter);

        list.setEmptyView(emptyList);
        loaderManager = getLoaderManager();
        loaderManager.initLoader(NEWS_LOADER_ID, null, this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object item = list.getItemAtPosition(i);
                NewsItem newsItem = (NewsItem) item;
                Uri newsUrl = Uri.parse(newsItem.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, newsUrl);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(getString(R.string.number_of_articles)) ||
                key.equals(getString(R.string.origin))) {
            adapter.clear();

            emptyList.setVisibility(View.GONE);

            View loadingIndicator = findViewById(R.id.progress);
            loadingIndicator.setVisibility(View.VISIBLE);

            loaderManager.restartLoader(NEWS_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<List<NewsItem>> onCreateLoader(int i, Bundle bundle) {
        String pageSize = getString(R.string.page_size);
        String small = getString(R.string.small_number);
        String medium = getString(R.string.medium_number);
        String big = getString(R.string.big_number);
        String productionOffice = getString(R.string.production);
        String uk = "uk";
        String usa = "us";
        String australia = "aus";
        Uri baseUri = Uri.parse(QUERY_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String numberOfArticles = sharedPrefs.getString(getString(R.string.number_of_articles), "-1");
        if (numberOfArticles.equals(small)) {
            uriBuilder.appendQueryParameter(pageSize, small);
        } else if (numberOfArticles.equals(medium)) {
            uriBuilder.appendQueryParameter(pageSize, medium);
        } else if (numberOfArticles.equals(big)) {
            uriBuilder.appendQueryParameter(pageSize, big);
        }

        String origin = sharedPrefs.getString(getString(R.string.origin), "-1");

        if (origin.equals(getString(R.string.united_kingdom))) {
            uriBuilder.appendQueryParameter(productionOffice, uk);
        } else if (origin.equals(getString(R.string.united_states))) {
            uriBuilder.appendQueryParameter(productionOffice, usa);
        } else if (origin.equals(getString(R.string.australia))) {
            uriBuilder.appendQueryParameter(productionOffice, australia);
        }
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<NewsItem>> loader, List<NewsItem> newsItems) {
        emptyList.setText(R.string.empty);
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        boolean isConnected = network != null && network.isConnectedOrConnecting();
        if (!isConnected) {
            emptyList.setText(R.string.no_internet);
        }

        ProgressBar bar = (ProgressBar) findViewById(R.id.progress);
        bar.setVisibility(View.INVISIBLE);

        adapter.clear();

        if (newsItems != null && !newsItems.isEmpty()) {
            adapter.addAll(newsItems);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader) {
        adapter.clear();
    }
}
