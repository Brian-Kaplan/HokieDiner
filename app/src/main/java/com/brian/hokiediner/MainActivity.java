package com.brian.hokiediner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import org.wso2.mobile.idp.proxy.IDPProxyActivity;
import org.wso2.mobile.idp.proxy.IdentityProxy;
import org.wso2.mobile.idp.proxy.callbacks.AccessTokenCallBack;
import org.wso2.mobile.idp.proxy.utils.ServerUtilities;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends IDPProxyActivity implements AccessTokenCallBack {
    private static String TAG = "MainActivity";
    ArrayAdapter<String> adapter;
    Button button;
    Context context;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new APITask().execute();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;

        String userName = getUserName();
        if (userName != null) {
            MenuItem item = menu.findItem(R.id.action_settings);
            item.setTitle("Welcome : " + userName);
        }

        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_settings :
                Log.v("Menu Clicked", "Menu Setting Clicked");
                setSSL();
                init(OauthCostants.CLIENT_ID,OauthCostants.CLIENT_SECRET,this);
                break;
        }

        return super.onMenuItemSelected(featureId, item);
    }
    public void onTokenReceived() {
        try {

            String jstring = IdentityProxy.getInstance().getToken().getIdToken();
            String accessToken = IdentityProxy.getInstance().getToken().getAccessToken();
            String refreshToken = IdentityProxy.getInstance().getToken().getRefreshToken();
            String[] strings = jstring.split("\\}");
            JSONObject payload = new JSONObject(strings[1]+"}");
            String userName = payload.getString("sub");
            int index = userName.indexOf('@');
            if(index>0){
                userName = userName.substring(2, index);
            }
            Log.v("Username", userName);
            storeUserData(accessToken, refreshToken, userName);
            MenuItem item = menu.findItem(R.id.action_settings);
            //item.setVisible(false);
            item.setTitle("Welcome : " + userName);
            String[] diningList = {"Au Bon Pain - GLC",
                    "Au Bon Pain - Squires Cafe",
                    "Au Bon Pain - Squires Kiosk",
                    "Au Bon Pain at Goodwin Hall",
                    "Burger '37",
                    "D2",
                    "Deet's Place",
                    "Dunkin Donuts",
                    "DXpress",
                    "Hokie Grill & Co.",
                    "Owens Food Court",
                    "Turner Place - 1872 Fire Grill",
                    "Turner Place - Atomic Pizzeria",
                    "Turner Place - Bruegger's Bagels",
                    "Turner Place - Dolci e Caffe",
                    "Turner Place - Jamba Juice",
                    "Turner Place - Origami Grill",
                    "Turner Place - Origami Sushi",
                    "Turner Place - Qdoba Mexican Grill",
                    "Turner Place - Soup Garden",
                    "Vet Med Cafe",
                    "West End Market"};

            ListView lv = (ListView) findViewById(R.id.diningListView);
            adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.dining_cell_model, R.id.cellTitleLabel, diningList);
            lv.setAdapter(adapter);

            EditText searchEditText = (EditText) findViewById(R.id.searchEditText);
            searchEditText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    // When user changed the Text
                    MainActivity.this.adapter.getFilter().filter(cs);
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                }
            });
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void storeUserData(String accessToken, String refreshToken, String userName) {
        SharedPreferences sharedPreferences = getSharedPreferences("tokens", Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("ACCESS_TOKEN", accessToken);
        editor.putString("REFRESH_TOKEN", refreshToken);
        editor.putString("USERNAME", userName);
        editor.apply();
    }

    private String getAccessToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("tokens", Context.MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("ACCESS_TOKEN", null);
        return accessToken;
    }

    private String getRefreshToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("tokens", Context.MODE_PRIVATE);
        String refreshToken = sharedPreferences.getString("REFRESH_TOKEN", null);
        return refreshToken;
    }

    private String getUserName() {
        SharedPreferences sharedPreferences = getSharedPreferences("tokens", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("USERNAME", null);
        return userName;
    }

    void setSSL(){
        InputStream inputStream = MainActivity.this.getResources().openRawResource(R.raw.truststore);
        ServerUtilities.enableSSL(inputStream, OauthCostants.TRUSTSTORE_PASSWORD);
    }
}