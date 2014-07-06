package jp.co.stheno.medusa;

import jp.co.stheno.medusa.utils.AlertDialogManager;
import jp.co.stheno.medusa.utils.ConnectionDetector;
import jp.co.stheno.medusa.utils.Const;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class CommonActivity extends Activity {
	/** ログ出力用TAG */
	protected String TAG = "";
	private String activityTitle = "";
    // Shared Preferences
    private SharedPreferences sharedPreferences;
    // Internet Connection detector
    private ConnectionDetector cd;
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        Intent intent = getIntent();
        if ( intent != null ) {
            activityTitle  = intent.getStringExtra("title");
        }
        
        if (getsharedPreferences() == null) {
        	// Shared Preferences
    		setsharedPreferences(getApplicationContext().getSharedPreferences(Const.LOGIN_USER, Context.MODE_PRIVATE));
        }
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		// インタネット接続状態をチェック
		if (!checkInternetStatus(CommonActivity.this)) {
			Log.e(TAG, "インターネットに接続されていません");
			return;
		}
	}
	
	/**
	 * デバイスのインタネット接続状態をチェック
	 * @param appliContext
	 */
	public boolean checkInternetStatus(final Activity context) {
		// インタネット接続状態チェック
		cd = new ConnectionDetector(context);
		
		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			AlertDialog alertDialog = alert.getAlertDialog(context);
			// Setting OK Button
	        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	context.finish();
	            }
	        });
			alert.showAlertDialog(context, "Internet Connection Error", "Please connect to working Internet connection", false);
			// stop executing code by return
			return false;
		}
		return true;
	}
	
    /**
     * Login情報取得
     * @param mContext Context
     * @return ログイン結果
     */
    public boolean getLoginStatus(Context application) {
         return getsharedPreferences().getBoolean(Const.AUTO_LOGIN_STATUS, false);
    }
    
    /**
     * Login状態設定
     * @param mContext Context
     * @param LoginStatus ログイン状態
     */
    public void setLoginStatus(Context application, boolean LoginStatus) {
        Editor editor = getsharedPreferences().edit();
        editor.putBoolean(Const.AUTO_LOGIN_STATUS, LoginStatus);
        editor.commit();
    }
    
    public void setUserId(Context application, String user_id) {
    	Editor editor = getsharedPreferences().edit();
        editor.putString(Const.USER_ID, user_id);
        editor.commit();
	}
	
	/**
	 * getter & setter
	 * @return
	 */
	public SharedPreferences getsharedPreferences() {
		return sharedPreferences;
	}
	public void setsharedPreferences(SharedPreferences mSharedPreferences) {
		this.sharedPreferences = mSharedPreferences;
	}
}
