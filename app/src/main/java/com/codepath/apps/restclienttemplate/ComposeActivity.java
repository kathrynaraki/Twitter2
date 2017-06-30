package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {
    Button btnTweet;
    TwitterClient client;
    TextView tvCharCount;
    EditText etTweetText;
    TextView tvAt;
    Boolean reply;
    String atName;

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            tvCharCount.setText(String.valueOf(140 - s.length()));
        }

        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        client = TwitterApp.getRestClient();
        tvCharCount = (TextView) findViewById(R.id.tvCharCount);
        etTweetText = (EditText) findViewById(R.id.etTweetText);
        tvAt = (TextView) findViewById(R.id.tvAt);

        atName = "";

        if (getIntent().getExtras() != null){
            Tweet tweet;
            tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
            atName = "@" + tweet.user.screenName + " ";
        }

        tvAt.setText(atName);
        btnTweet = (Button) findViewById(R.id.btnTweet);
        btnTweet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSubmit(v);
            }
        });

        etTweetText.addTextChangedListener(mTextEditorWatcher);
    }

    public void onSubmit(View v) {
        etTweetText = (EditText) findViewById(R.id.etTweetText);

        client.sendTweet(atName + etTweetText.getText().toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet tweet = null;
                try {
                    tweet = Tweet.fromJSON(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent data = new Intent();
                // Pass relevant data back as a result
                data.putExtra("tweet", Parcels.wrap(tweet));
                // Activity finished ok, return the data
                setResult(RESULT_OK, data); // set result code and bundle data for response
                finish(); // closes the activity, pass data to parent
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("MySimpleTweet", "Failed to send a tweet" + errorResponse.toString());
            }
        });

    }

}
