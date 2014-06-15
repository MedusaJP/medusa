package jp.co.stheno.medusa;

import android.os.Bundle;
import android.R.string;
import android.app.Activity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.widget.EditText;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		TelephonyManager manager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		String imei = manager.getDeviceId();
		EditText eText = (EditText)findViewById(R.id.nicname);
		eText.setHint(imei);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
