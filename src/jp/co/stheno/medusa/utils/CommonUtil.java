/**
 * @author rjin
 */
package jp.co.stheno.medusa.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;

/**
 * 共通メソッドクラス
 * @author rjin
 *
 */
public class CommonUtil {

	
	/**
	 * 携帯のIMEIコード取得する
	 * @param application Context
	 * @return
	 */
	public static String getIMEIString(Context application) {
		TelephonyManager manager = (TelephonyManager)application.getSystemService(Context.TELEPHONY_SERVICE);
		return manager.getDeviceId();
	}
}
