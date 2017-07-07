package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * Created by arakik on 6/26/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    Context context;
    private List<Tweet> mTweets;
    TwitterClient client;

    // pass in Tweets array in constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }
    // for each row, inflate layout and pass into the viewHolder class

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);

        client = TwitterApp.getRestClient();
        return viewHolder;

    }

    // bind values based on position of element

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get data according to position
        Tweet tweet = mTweets.get(position);
        // populate views according to this data
        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvScreenName.setText("@" + tweet.user.screenName);
        holder.tvTimeStamp.setText(getRelativeTimeAgo(tweet.createdAt));
        if(tweet.favorited) {
            holder.ivFavorite.setColorFilter(ContextCompat.getColor(context, R.color.bright_red));
        } else {
            holder.ivFavorite.setColorFilter(ContextCompat.getColor(context, R.color.icon_grey));
        }
        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }




    // create ViewHolder class

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvScreenName;
        public TextView tvTimeStamp;
        public ImageView ivReply;
        public ImageView ivFavorite;

        private final int REQUEST_CODE = 20;

        public ViewHolder(View itemView) {
            super(itemView);

            // perform findViewById lookups
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImageD);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserNameD);
            tvBody = (TextView) itemView.findViewById(R.id.tvBodyD);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenNameD);
            tvTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            ivReply = (ImageView) itemView.findViewById(R.id.ivReply);
            ivFavorite = (ImageView) itemView.findViewById(R.id.ivFavorite);

            itemView.setOnClickListener(this);
            ivReply.setOnClickListener(this);
            ivProfileImage.setOnClickListener(this);
            ivFavorite.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Tweet tweet = mTweets.get(position);
            //long id = v.getId();
            if (position != RecyclerView.NO_POSITION) {
                if (v.getId() == R.id.ivReply) {
                    Intent i = new Intent(context, ComposeActivity.class);
                    i.putExtra("tweet", Parcels.wrap(tweet));

                    ((TimelineActivity)context).startActivityForResult(i, REQUEST_CODE);
                } else if (v.getId() == R.id.ivProfileImageD) {
                    // create intent for the new activity
                    Intent i = new Intent(context, ProfileActivity.class);
                    // serialize the movie using parceler, use its short name as a key
                    i.putExtra("tweet", Parcels.wrap(tweet));
                    // show the activity
                    context.startActivity(i);
                } else if (v.getId() == R.id.ivFavorite) {
                    if (!tweet.favorited) {
                        client.favoriteTweet(tweet.uid, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                ivFavorite.setColorFilter(ContextCompat.getColor(context, R.color.bright_red));
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Log.e("MySimpleTweet", "Failed to favorite a tweet" + errorResponse.toString());
                            }
                        });
                    } else {
                        client.unFavoriteTweet(tweet.uid, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                ivFavorite.setColorFilter(ContextCompat.getColor(context, R.color.icon_grey));
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                Log.e("MySimpleTweet", "Failed to unfavorite a tweet" + errorResponse.toString());
                            }
                        });
                    }

                } else {
                    // create intent for the new activity
                    Intent intent = new Intent(context, TweetDetails.class);
                    // serialize the movie using parceler, use its short name as a key
                    intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    // show the activity
                    context.startActivity(intent);
                }


            }
        }

    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    /* Within the RecyclerView.Adapter class */

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(JSONArray list) {
        for (int i = 0; i < list.length(); i++) {
            try {
                mTweets.add(Tweet.fromJSON(list.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        notifyDataSetChanged();
    }
}
