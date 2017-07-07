package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by arakik on 6/26/17.
 */
@org.parceler.Parcel
public class Tweet {

    // list out attributes
    public String body;
    public long uid;
    public String createdAt;
    public boolean favorited;

    // We can also include child Parcelable objects. Assume MySubParcel is such a Parcelable:
    public User user;

    public Tweet() {}

    // deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        // extract all values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        tweet.favorited = jsonObject.getBoolean("favorited");

        return tweet;
    }

    public long getUid() {
        return uid;
    }
}
