package jp.co.stheno.medusa.utils;

import twitter4j.Twitter;
import twitter4j.auth.RequestToken;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;


public class TwitterOAuthRequestTokenCallbacks implements LoaderCallbacks<RequestToken> {

    private Context mContext;
    private Twitter mTwitter;

    public TwitterOAuthRequestTokenCallbacks(Context context, Twitter twitter) {
        mContext = context;
        mTwitter = twitter;
    }

    @Override
    public Loader<RequestToken> onCreateLoader(int id, Bundle args) {
        TwitterOAuthRequestTokenLoader loader = new TwitterOAuthRequestTokenLoader(mContext, mTwitter);
        loader.forceLoad();
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<RequestToken> arg0, RequestToken requestToken) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthorizationURL()));
        mContext.startActivity(intent);
    }

	@Override
	public void onLoaderReset(Loader<RequestToken> loader) {
		// TODO Auto-generated method stub
		
	}
}
