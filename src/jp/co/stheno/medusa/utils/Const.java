package jp.co.stheno.medusa.utils;

import android.R.integer;

public class Const {
	public static final String API_ID = "demo";
	public static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36";
	public static final boolean IS_DEVELOPMENT = true;
    public static final String APP_NAME = "medusa";
    //public static final String API_BASE_URL = "http://medusa.stheno.co.jp/v1/";
    public static final String API_BASE_URL = "http://chenfeng.mobi/medusa/";
    // Constants
    public static final String TWITTER_CONSUMER_KEY    = "5Wrb3kYeLTYmGv7fgyBzT4yql";
    public static final String TWITTER_CONSUMER_SECRET = "g68bdeGY0XTlahJTQ3Hbpr6gJzHaHGhdCbtkzZxSuKAmfsNflP";
    public static final String TWITTER_CALLBACK_URL = "oauth://twitterlogin";
    
    // Preference Constants
    public static final String PREFERENCE_NAME = "twitter_oauth";
    public static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    public static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    public static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
    public static final String LOGIN_USER = "login_user";
    public static final String AUTO_LOGIN_STATUS = "auto_login_status";
    public static final String USER_ID = "user_id";
    
    // flags
    public static final int LOGGEDIN = 1;
    public static final int NOT_LOGGEDIN = 0;
    
    // Twitter oauth urls
    public static final String URL_TWITTER_AUTH = "auth_url";
    public static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    public static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
    
}
