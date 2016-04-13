package com.brian.hokiediner;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.wso2.mobile.idp.proxy.utils.ServerUtilities;

import java.io.BufferedReader;
import java.net.URI;

/**
 * Created by brian on 4/8/16.
 */
class APITask extends AsyncTask<String, Integer, Boolean>
{

    private String accessToken;
    public AsyncResponse delegate = null;

    public APITask(AsyncResponse delegate, String accessToken) {
        this.delegate = delegate;
        this.accessToken = accessToken;
    }

    public interface AsyncResponse {
        void processFinish(Boolean output);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        delegate.processFinish(aBoolean);
    }

    protected void onPreExecute (){
        Log.d("PreExceute", "On pre Exceute......");
    }

    protected Boolean doInBackground(String... urls) {
        StringBuffer stringBuffer = new StringBuffer("");
        BufferedReader bufferedReader = null;
        try {
            HttpClient httpClient = new ServerUtilities().getCertifiedHttpClient();
            HttpGet httpGet = new HttpGet();

            URI uri = new URI("http://172.31.83.248:8280/phoneverify2/1.0.0/CheckPhoneNumber?PhoneNumber=7033363133&LicenseKey=0");
            httpGet.setURI(uri);
            httpGet.addHeader("Authorization","Bearer " + accessToken);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            String json_string = EntityUtils.toString(httpResponse.getEntity());
            int code = httpResponse.getStatusLine().getStatusCode();
            Log.d("APITask", "Http Response Code: " + String.valueOf(code));
//            Log.d("APITask", json_string);
            if (code == HttpStatus.SC_OK) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

}
