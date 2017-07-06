package com.codepath.apps.restclienttemplate.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.ComposeActivity;
import com.codepath.apps.restclienttemplate.ProfileActivity;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetAdapter;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_OK;

/**
 * Created by arakik on 7/3/17.
 */

public class TweetsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    ActionBar actionBar;
    SwipeRefreshLayout swipeLayout;
    TwitterClient client;
    String name;
    String screenName;

    private final int REQUEST_CODE = 20;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate layout
        View v = inflater.inflate(R.layout.fragments_tweets_list, container, false);

        // find recycler view
        rvTweets = (RecyclerView) v.findViewById(R.id.rvTweet);
        // init the arrayList (data source)
        tweets = new ArrayList<>();
        // construct adapter from datasource
        tweetAdapter = new TweetAdapter(tweets);
        // setup recycler view (layout manager, to use adapter)
        rvTweets.setLayoutManager(new LinearLayoutManager(getContext()));
        // set adapter
        rvTweets.setAdapter(tweetAdapter);
        // set client
        client = TwitterApp.getRestClient();

        LinearLayoutManager lmTweet = new LinearLayoutManager(getContext());
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvTweets.getContext(),
                lmTweet.getOrientation());
        rvTweets.addItemDecoration(mDividerItemDecoration);

        setHasOptionsMenu(true);

        // swipe refresh
        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.twitter_logo_blue));
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public void addItems(JSONArray response) {
        try {
            for(int i = 0; i < response.length(); i++) {
                Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                tweets.add(tweet);
                tweetAdapter.notifyItemInserted(tweets.size() - 1);
            }
        } catch (JSONException e) {
                e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.miCompose) {
            Intent i = new Intent(getContext(), ComposeActivity.class);
            startActivityForResult(i, REQUEST_CODE);
        } else {
            Intent i = new Intent(getContext(), ProfileActivity.class);
            startActivity(i);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Reply
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            Log.i("sendTweet", tweet.body);
            tweets.add(0, tweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);
        }
    }

    @Override
    public void onRefresh() {
        fetchTimelineAsync(0);
    }

    public void fetchTimelineAsync(int page) {
        if(name.equals("home")) {
            client.getHomeTimeline(new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    // Remember to CLEAR OUT old items before appending in the new ones
                    tweetAdapter.clear();
                    // ...the data has come back, add new items to your adapter...
                    tweetAdapter.addAll(response);
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.d("DEBUG", "Fetch timeline error: " + errorResponse.toString());
                }
            });
        } else if(name.equals("mentions")) {
            client.getMentionsTimeline(new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    // Remember to CLEAR OUT old items before appending in the new ones
                    tweetAdapter.clear();
                    // ...the data has come back, add new items to your adapter...
                    tweetAdapter.addAll(response);
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.d("DEBUG", "Fetch timeline error: " + errorResponse.toString());
                }
            });
        } else {
            client.getUserTimeline(screenName, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    // Remember to CLEAR OUT old items before appending in the new ones
                    tweetAdapter.clear();
                    // ...the data has come back, add new items to your adapter...
                    tweetAdapter.addAll(response);
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.d("DEBUG", "Fetch timeline error: " + errorResponse.toString());
                }
            });
        }


    }
}
