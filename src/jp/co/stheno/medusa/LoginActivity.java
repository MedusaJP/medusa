package jp.co.stheno.medusa;

import org.json.JSONException;
import org.json.JSONObject;

import jp.co.stheno.medusa.utils.APIHandler;
import jp.co.stheno.medusa.utils.APIHandler.Response;
import jp.co.stheno.medusa.utils.AlertDialogManager;
import jp.co.stheno.medusa.utils.CommonUtil;
import jp.co.stheno.medusa.utils.ConnectionDetector;
import jp.co.stheno.medusa.utils.Const;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends CommonActivity {
	/** ログ出力用TAG */
	private static String TAG = LoginActivity.class.getSimpleName();

	// Login button
    Button btnLoginTwitter;
    // Update status button
    Button btnUpdateStatus;
    // Logout button
    Button btnLogoutTwitter;
    // EditText for update
    EditText txtUpdate;
    // lbl update
    TextView lblUpdate;
    TextView lblUserName;
    
    private Handler mHandler;
    
    private AccessToken accessToken;
 
    // Progress dialog
    ProgressDialog pDialog;
    
    private User user;
 
    // Twitter
    private Twitter twitter;
    private RequestToken requestToken;
     
    // Shared Preferences
    private static SharedPreferences mSharedPreferences;
     
    // Internet Connection detector
    private ConnectionDetector cd;
    
    private JSONObject resultJsonObject;
    
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		cd = new ConnectionDetector(getApplicationContext());
		
		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(LoginActivity.this, "Internet Connection Error", "Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}
		
		// ALL UI elements
		btnLoginTwitter = (Button)findViewById(R.id.btnLoginTwitter);
		btnUpdateStatus = (Button)findViewById(R.id.btnUpdateStatus);
		btnLogoutTwitter = (Button)findViewById(R.id.btnLogoutTwitter);
		txtUpdate = (EditText)findViewById(R.id.txtUpdateStatus);
		lblUpdate = (TextView)findViewById(R.id.lblUpdate);
		lblUserName = (TextView)findViewById(R.id.lblUserName);
		
		mHandler = new Handler();
		
		// Shared Preferences
		mSharedPreferences = getApplicationContext().getSharedPreferences("LoginUser", 0);
		
		btnLoginTwitter.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Call login twitter function
				loginToTwitter();
			}
		});
		
		EditText eText = (EditText)findViewById(R.id.nicname);
		eText.setHint(CommonUtil.getIMEIString(getApplicationContext()));
		
		quickSinin(CommonUtil.getIMEIString(getApplicationContext()));
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
	  super.onNewIntent(intent);
	  initTwitter(intent);
	}
	
	private void quickSinin(String imei) {
		final String iMEI = imei;
		new Thread(new Runnable() {
			@Override
			public void run() {
				APIHandler apiHandler = new APIHandler();
				final APIHandler.Response response = apiHandler.postIMEI(iMEI);
				
				if (response.isSuccess()) {
					resultJsonObject = (JSONObject)response.getData();
					
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							String id;
							try {
								id = resultJsonObject.getString("user_id");
								lblUserName.setText(Html.fromHtml("<b>Welcome " + id + "</b>"));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
					});
				}
			}
		}).start();
	}
	
	private void initTwitter(Intent intent) {
		if (!isTwitterLoggedInAlready()) {
			Uri uri = intent.getData();
			if (uri != null && uri.toString().startsWith(Const.TWITTER_CALLBACK_URL)) {
				// oAuth verifier
				final String verifier = uri.getQueryParameter(Const.URL_TWITTER_OAUTH_VERIFIER);
				Toast.makeText(getApplicationContext(), verifier, Toast.LENGTH_LONG).show();
				try {
					// Get the access token
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
								 final long userID = accessToken.getUserId();
								 user = twitter.showUser(userID);
							} catch (TwitterException e) {
								e.printStackTrace();
							}
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									// Shared Preferences
									Editor editor = mSharedPreferences.edit();
									
									// After getting access token, access token secret
									// store them in application preferences
									Toast.makeText(getApplicationContext(), accessToken.getToken(), Toast.LENGTH_LONG).show();
									editor.putString(Const.PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
									editor.putString(Const.PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
									// Store login status - true
									editor.putBoolean(Const.PREF_KEY_TWITTER_LOGIN, true);
									editor.commit();
									
									Log.e("Twitter OAuth Token", "> " + accessToken.getToken());
									
									// Hide login button
									btnLoginTwitter.setVisibility(View.GONE);
									
									// Show Update Twitter
					                lblUpdate.setVisibility(View.VISIBLE);
					                txtUpdate.setVisibility(View.VISIBLE);
					                btnUpdateStatus.setVisibility(View.VISIBLE);
					                btnLogoutTwitter.setVisibility(View.VISIBLE);
					                
					                // Getting user details from twitter
					                String username = user.getName();
					                
					                lblUserName.setText(Html.fromHtml("<b>Welcome " + username + "</b>"));
								}
							});
							
						}
					}).start();

				} catch (Exception e) {
					//Check log for login errors
					Log.e("Twitter Login Error", "> " + e.getMessage());
				}
			}
		}
	}

	private boolean isTwitterLoggedInAlready() {
		// return twitter login status from Shared Preferences
        return mSharedPreferences.getBoolean(Const.PREF_KEY_TWITTER_LOGIN, false);
	}

	private void loginToTwitter() {
		// Check if already logged in
		if (!isTwitterLoggedInAlready()) {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(Const.TWITTER_CONSUMER_KEY);
			builder.setOAuthConsumerSecret(Const.TWITTER_CONSUMER_SECRET);
			Configuration configuration = builder.build();
			
			TwitterFactory factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();
			
			//TwitterOAuthRequestTokenCallbacks oAuthRequestTokenCallbacks = new TwitterOAuthRequestTokenCallbacks(this, twitter);
			//getLoaderManager().initLoader(0, null, oAuthRequestTokenCallbacks);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						requestToken = twitter.getOAuthRequestToken(Const.TWITTER_CALLBACK_URL);
					} catch (TwitterException e) {
						e.printStackTrace();
					}
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
						}
					});
				}
			}).start();
		} else {
			// user already logged into twitter
			Toast.makeText(getApplicationContext(), "Already Logged into twitter", Toast.LENGTH_LONG).show();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
