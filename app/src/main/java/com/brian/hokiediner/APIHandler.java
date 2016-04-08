package com.brian.hokiediner;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Created by brian on 4/8/16.
 */
class APITask extends AsyncTask<String, Integer, String>
{
    protected void onPreExecute (){
        Log.d("PreExceute", "On pre Exceute......");
    }

    protected String doInBackground(String... urls) {
        StringBuffer stringBuffer = new StringBuffer("");
        BufferedReader bufferedReader = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet();

            URI uri = new URI("http://172.30.58.105:8280/phoneverify/1.0.0/CheckPhoneNumber?PhoneNumber=7033363133&LicenseKey=0");
            httpGet.setURI(uri);
            httpGet.addHeader("Authorization","Bearer b76abab895c740c1c44dc3f9f02ebb47");

            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String readLine = bufferedReader.readLine();
            while (readLine != null) {
                stringBuffer.append(readLine);
                stringBuffer.append("\n");
                readLine = bufferedReader.readLine();
            }
            return readLine;
        } catch (Exception e) {
            return null;
        }
    }

    protected void onPostExecute(String result) {
        Log.d(""+result, "0");
    }
}
