package jp.co.stheno.medusa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class CommonActivity extends Activity {
	/** ログ出力用TAG */
	protected String TAG = "";
	private String activityTitle = "";
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        Intent intent = getIntent();
        if ( intent != null ) {
            activityTitle  = intent.getStringExtra("title");
        }
    }
}
