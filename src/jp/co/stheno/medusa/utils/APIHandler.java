/**
 * @author rjin
 * API呼び出すためのクラス
 */
package jp.co.stheno.medusa.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import jp.co.stheno.medusa.utils.Const;
/**
 * @author rjin
 * APIHandlerクラス
 */
public class APIHandler {
	/** ログ出力用TAG */
	private static String TAG = APIHandler.class.getSimpleName();
	/** API認証用ID */
    private static String APIID;
    /** API呼び出し時のユーザーエージェント */
    private static String USER_AGENT;
    /** 開発状態フラグ */
    private static boolean IS_DEVELOPMENT;
    /** APIベースURL */
    private static String API_BASE_URL;

    /**
     * コンストラクタ
     */
    public APIHandler () {
        this( Const.DEFAULT_USER_AGENT );
    }
    
    /**
     * コンストラクタ
     * @param userAgent
     */
    public APIHandler(String userAgent) {
        IS_DEVELOPMENT = Const.IS_DEVELOPMENT;
        USER_AGENT = userAgent;
        APIID = Const.API_ID;
        API_BASE_URL = Const.API_BASE_URL;
    }
    
    /**
     * 
     * @param sUrl
     * @return
     */
    public String getData(String sUrl) {
        HttpClient objHttp = new DefaultHttpClient();
        //objHttp.getParams().setParameter("http.useragent", USER_AGENT);
        HttpParams params = objHttp.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 30000); //接続のタイムアウト
        HttpConnectionParams.setSoTimeout(params, 30000); //データ取得のタイムアウト
        String sReturn = "";
        try {
            HttpGet objGet   = new HttpGet(sUrl);
            objGet.setHeader("Accept-Encoding", "gzip");
            HttpResponse objResponse = objHttp.execute(objGet);
            
            if (objResponse.getStatusLine().getStatusCode() < 400){
                InputStream objStream = objResponse.getEntity().getContent();
                Header contentEncoding = objResponse.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    objStream = new GZIPInputStream(objStream);
                }

                InputStreamReader objReader = new InputStreamReader(objStream);
                BufferedReader objBuf = new BufferedReader(objReader);
                StringBuilder objJson = new StringBuilder();
                String sLine;
                while((sLine = objBuf.readLine()) != null){
                    objJson.append(sLine);
                }
                sReturn = objJson.toString();
                objStream.close();
            }
        } catch (IOException e) {
            return null;
        }   
        return sReturn;
    }
    /**
     * API認証用IDを取得
     * @return
     */
    public String getAppId() {
        return APIID;
    }
    
    public Response postIMEI (String imei) {
    	String path = API_BASE_URL + "/index.php/user/quick_apply";
    	ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
    	params.add(new BasicNameValuePair("device_id", imei));
    	return postAPI(path, params);
    }
    
    /**
     * 必須パラメータくっつけてAPIコール
     * @param path
     * @param params
     * @return
     */
    private Response getAPI(String path, List<NameValuePair> params) {
        params.add(new BasicNameValuePair("appId", APIID));
        return getAPI(path, buildQuery(params));
    }
    
    /**
     * 指定uriとクエリでpostでAPIを叩きカスタマイズレスポンスオブジェクトを取得
     * @param path
     * @param params
     * @return
     */
    private Response postAPI(String path, ArrayList<NameValuePair> params) {
    	params.add(new BasicNameValuePair("appId", APIID));
    	return new Response(parseJson(postRequest(path, params)));
	}
    
    /**
     * 指定uriとクエリでgetでAPIを叩きカスタマイズレスポンスオブジェクトを取得
     * @param path 請求uri
     * @param query
     * @return レスポンスクラスのインスタンス
     */
    private Response getAPI(String path, String query) {
        String uri = path;
        return new Response(parseJson(getRequest(uri, query)));
    }
    
    /**
     * クエリListをクエリ文字列に変換
     * @param params List<NameValuePair>
     * @return UTF-8でエンコードしたクエリ文字列
     */
    private String buildQuery(List<NameValuePair> params) {
        return URLEncodedUtils.format(params, "utf-8");
    }
    
    /**
     * GETするメソッド
     * @param uri
     * @param query
     * @return getした文字列（UTF-8）
     */
    private String getRequest(String uri, String query) {
        if (IS_DEVELOPMENT) {
            Log.v(TAG, ">uri: " + uri + "\n>query: " + query);
        }

        String json = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpParams params = httpClient.getParams();
            httpClient.getParams().setParameter("http.useragent", USER_AGENT);
            //POSTで一部端末でHeaderExceptionが発生するのを無視する設定
            params.setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            //接続タイムアウト
            HttpConnectionParams.setConnectionTimeout(params, 30000);
            //データ取得タイムアウト
            HttpConnectionParams.setSoTimeout(params, 30000);
            uri += ("?" + query);
            HttpGet method   = new HttpGet(uri);

            HttpResponse httpResponse = httpClient.execute(method);

            if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity httpEntity = httpResponse.getEntity();
                json = EntityUtils.toString(httpEntity, "UTF-8");
                httpEntity.consumeContent();
            }
            httpClient.getConnectionManager().shutdown();
        }
        catch (ClientProtocolException e) {
            if (IS_DEVELOPMENT) {
                Log.v(TAG, e.toString());
            }
            return null;
        }
        catch (ParseException e) {
            if (IS_DEVELOPMENT) {
                Log.v(TAG, e.toString());
            }
            return null;
        }
        catch (IOException e){
            if (IS_DEVELOPMENT) {
                Log.v(TAG, e.toString());
            }
            return null;
        }
        if (IS_DEVELOPMENT) {
            Log.v(TAG, "json: " + json);
        }

        return json;
    }
    
    /**
     * POSTするメソッド
     * @param uri
     * @param query　クエリArrayList<NameValuePair>
     * @return
     */
    private String postRequest(String uri, ArrayList<NameValuePair> query) {
    	if (IS_DEVELOPMENT) {
    		Log.v(TAG, ">uri: " + uri + "\n>query: " + query);
    	}
    	String json = null;
    	try {
    		DefaultHttpClient httpClient = new DefaultHttpClient();
    		HttpParams params = httpClient.getParams();
    		//POSTで一部端末でHeaderExceptionが発生するのを無視する設定
    		params.setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
    		//接続タイムアウト
    		HttpConnectionParams.setConnectionTimeout(params, 30000);
    		//データ取得タイムアウト
    		HttpConnectionParams.setSoTimeout(params, 30000);

    		HttpPost httpPost = new HttpPost(uri);
    		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

    		httpPost.setEntity(new UrlEncodedFormEntity(query, HTTP.UTF_8));
    		final HttpResponse response = httpClient.execute(httpPost);
    		if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
    			HttpEntity httpEntity = response.getEntity();
    			json = EntityUtils.toString(httpEntity, "UTF-8");
    			httpEntity.consumeContent();
    		}
    		httpClient.getConnectionManager().shutdown();

    	}catch (ClientProtocolException e) {
    		if (IS_DEVELOPMENT) {
    			Log.v(TAG, e.toString());
    		}
    		return null;
    	}
    	catch (ParseException e) {
    		if (IS_DEVELOPMENT) {
    			Log.v(TAG, e.toString());
    		}
    		return null;
    	}
    	catch (IOException e){
    		if (IS_DEVELOPMENT) {
    			Log.v(TAG, e.toString());
    		}
    		return null;
    	}
    	if (IS_DEVELOPMENT) {
    		Log.v(TAG, "json: " + json);
    	}
    	return json;
    }
    
    /**
     * JSON文字列をJSONObjectに変換
     * @param json
     * @return JSONObject 
     */
    private JSONObject parseJson(String json) {
        if (json == null) {
            return null;
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        }
        catch(JSONException e) {
            if (IS_DEVELOPMENT) {
                Log.e(TAG, e.toString());
            }
        }
        return jsonObject;
    }
    
    /**
     * レスポンス共通クラス
     * @author rjin
     *　レスポンスの結果とステータス判断を簡単にできるようにするクラス
     */
    public class Response {
        private String mStatus = null;
        private JSONObject mData = null;
        private JSONArray mArray = null;
        private JSONArray mErrorCodes = null;
        private String mCode = null;

        public Response(JSONObject jsonObject) {
            if (jsonObject != null) {
                try {
                    mStatus = jsonObject.getString("Status");
                    mData = jsonObject;
                }
                catch (JSONException e) {
                    Log.v(TAG, e.toString());
                }
            }
        }

        public Response(JSONArray jsonArray) {
            if (jsonArray != null) {
                JSONArray res = jsonArray;
                mArray = res;
            }
        }

        public boolean isSuccess() {
            if(mStatus.equals("200")) {
                return true;
            }
            else {
                return false;
            }
        }

        public String getStatus() {
            return mStatus;
        }

        public JSONObject getData() {
            return mData;
        }

        public JSONArray getArray() {
            return mArray;
        }

        public String getCode() {
            return mCode;
        }

        public JSONArray getErrorCodes() {
            return (mErrorCodes != null) ? mErrorCodes : null;
        }
    }
}
