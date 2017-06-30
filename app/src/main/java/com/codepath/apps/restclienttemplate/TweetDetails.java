package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TweetDetails extends AppCompatActivity {
    Tweet tweet;
    private ImageView ivProfileImageD;
    private TextView tvUserNameD;
    private TextView tvScreenNameD;
    private TextView tvBodyD;
    private TextView tvTimeStampD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        // unwrap tweet
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        ivProfileImageD = (ImageView) findViewById(R.id.ivProfileImageD);
        tvUserNameD = (TextView) findViewById(R.id.tvUserNameD);
        tvScreenNameD = (TextView) findViewById(R.id.tvScreenNameD);
        tvBodyD = (TextView) findViewById(R.id.tvBodyD);
        tvTimeStampD = (TextView) findViewById(R.id.tvTimeStampD);

        tvUserNameD.setText(tweet.user.name);
        tvScreenNameD.setText("@ " + tweet.user.screenName);
        tvBodyD.setText(tweet.body);
        tvTimeStampD.setText(formatTime(tweet.createdAt));

        getSupportActionBar().setTitle("Tweet");

        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .into(ivProfileImageD);

    }

    private String formatTime(String time) {
        String newTime = "";

        for (int i = 0; i < time.length(); i++) {
            if(time.charAt(i) != '+') {
                newTime += time.charAt(i);
            } else {
                return newTime;
            }
        }
        return newTime;
    }

}
