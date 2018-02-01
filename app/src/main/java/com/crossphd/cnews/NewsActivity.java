package com.crossphd.cnews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>> {

    public static final String LOG_TAG = NewsActivity.class.getSimpleName();
    private static String BUILT_URL ="https://newsapi.org/v2/top-headlines?country=us&sortBy=popularity&language=en&apiKey=9a6a77f0dd3c44bab9d3dcfaab5a7f42";
    private static String mTopic = "android";
    private static String newsSources = "abc-news, al-jazeera-english,ars-technica,bbc-news,bleacher-report,business-insider,buzzfeed,cbs-news," +
            "cnbc,cnn,crypto-coin-news,daily-mail,engadget,entertainment-weekly,espn,financial-post,financial-times,fortune,four-four-two," +
            "hacker-news,ign,independent,info-money,mashable,msnbc,mtv-news,national-geographic,nbc-news,newsweek,new-york-magazine," +
            "nfl-news,politico,recode,reddit-r-all,reuters,techcrunch,techradar,the-economist,the-guardian-uk,the-huffington-post,the-new-york-times," +
            "the-verge,the-wall-street-journal,the-washington-post,time,usa-today,wired";
    private ArticleAdapter adapter;
    private TextView mEmptyStateTextView;
    private ProgressBar mProgressBar;
    private static final int ARTICLE_LOADER_ID = 1;
//    SwipeRefreshLayout mSwipeRefreshLayout;
    EditText mSearchText;
    Button mSearchButton;
    Date mToday;
    Date mPastDate;
    String mTodayString;
    String mPastString;
    Calendar mCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        mSearchText = findViewById(R.id.search_text);

        mToday = Calendar.getInstance().getTime();
        mCal = Calendar.getInstance();
        mCal.setTime(mToday);
        mCal.add(Calendar.DATE, -7);
        mPastDate = mCal.getTime();
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        mTodayString = DATE_FORMAT.format(mToday);
        mPastString = DATE_FORMAT.format(mPastDate);


        ListView articleListView = findViewById(R.id.list);

        mEmptyStateTextView = findViewById(R.id.empty_view);
        articleListView.setEmptyView(mEmptyStateTextView);

        mProgressBar = findViewById(R.id.loading_spinner);

        mSearchButton = findViewById(R.id.search_button);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mTopic = mSearchText.getText().toString();
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("newsapi.org")
                        .appendPath("/v2/everything/")
                        .appendQueryParameter("q", mTopic)
//                        .appendQueryParameter("country", "us")
//                        .appendQueryParameter("sortBy", "relevance")
                        .appendQueryParameter("sources", newsSources)
                        .appendQueryParameter("from", mPastString)
                        .appendQueryParameter("to", mTodayString)
                        .appendQueryParameter("language", "en")
                        .appendQueryParameter("apiKey", "9a6a77f0dd3c44bab9d3dcfaab5a7f42");
                BUILT_URL = builder.build().toString();
                Log.v(LOG_TAG,"the BUILT_URL = " + BUILT_URL);
//                Toast.makeText(NewsActivity.this, BUILT_URL,Toast.LENGTH_LONG).show();
                update();
            }
        });


        // Create a new {@link ArrayAdapter} of earthquakes
        adapter = new ArticleAdapter(NewsActivity.this, new ArrayList<Article>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        articleListView.setAdapter(adapter);

//        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
//        mSwipeRefreshLayout.setOnRefreshListener(
//                new SwipeRefreshLayout.OnRefreshListener() {
//                    @Override
//                    public void onRefresh() {
//                        Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
//
//                        // This method performs the actual data-refresh operation.
//                        // The method calls setRefreshing(false) when it's finished.
//                        update();
//                    }
//                }
//        );

        //set onItemClickListener to open the website with the quake details
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Find the current earthquake that was clicked on
                Article currentArticle = adapter.getItem(position);


                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri articleUri = Uri.parse(currentArticle.getmUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });


//        check network connectivity before calling loaderManager
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
        } else {
//            remove loading spinner
            mProgressBar.setVisibility(View.GONE);
//            set error text
            mEmptyStateTextView.setText(R.string.no_internet_connection);

        }
    }

    private void update() {
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.restartLoader(ARTICLE_LOADER_ID, null, this);
//        mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        Log.v(LOG_TAG, "onCreateLoader called");
        return new ArticleLoader(this, BUILT_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        // Clear the adapter of previous earthquake data
        mProgressBar.setVisibility(View.GONE);
        adapter.clear();
        Log.v(LOG_TAG, "OnLoadFinished called");
        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (articles != null && !articles.isEmpty()) {
            adapter.addAll(articles);
        }

        // Set empty state text to display "No earthquakes found."
//        set here instead of onCreate so initial loading of app doesnt display message
        mEmptyStateTextView.setText(R.string.no_articles);
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        // Loader reset, so we can clear out our existing data.
        adapter.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Check if user triggered a refresh:
            case R.id.menu_refresh:
                Log.i(LOG_TAG, "Refresh menu item selected");

                // Signal SwipeRefreshLayout to start the progress indicator
//                mSwipeRefreshLayout.setRefreshing(true);

                // Start the refresh background task.
                // This method calls setRefreshing(false) when it's finished.
                update();

                return true;
        }

        // User didn't trigger a refresh, let the superclass handle this action
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
}
