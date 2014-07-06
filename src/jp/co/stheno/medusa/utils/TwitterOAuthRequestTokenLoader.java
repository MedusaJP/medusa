package jp.co.stheno.medusa.utils;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;
import android.content.AsyncTaskLoader;
import android.content.Context;


public class TwitterOAuthRequestTokenLoader extends AsyncTaskLoader<RequestToken> {

    private Twitter mTwitter;

    public TwitterOAuthRequestTokenLoader(Context context, Twitter twitter) {
        super(context);
        mTwitter = twitter;
    }

    @Override
    public RequestToken loadInBackground() {
        RequestToken requestToken = null;
        try {
            requestToken = mTwitter.getOAuthRequestToken();
        } catch (TwitterException e) {
            requestToken = null;
        }
        return requestToken;
    }
}