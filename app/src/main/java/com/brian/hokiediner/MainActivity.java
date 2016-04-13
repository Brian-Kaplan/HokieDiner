package com.brian.hokiediner;


import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.json.JSONObject;
import org.wso2.mobile.idp.proxy.IDPProxyActivity;
import org.wso2.mobile.idp.proxy.IdentityProxy;
import org.wso2.mobile.idp.proxy.beans.Token;
import org.wso2.mobile.idp.proxy.callbacks.AccessTokenCallBack;
import org.wso2.mobile.idp.proxy.handlers.AccessTokenHandler;
import org.wso2.mobile.idp.proxy.handlers.RefreshTokenHandler;
import org.wso2.mobile.idp.proxy.utils.ServerUtilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

public class MainActivity extends IDPProxyActivity implements AccessTokenCallBack {
    private static String TAG = "MainActivity";
    Button button;
    Context context;
    Token token;
    Menu menu;

    private TextView accessTokenLabel = null;
    private TextView tokenValidLabel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accessTokenLabel = (TextView) findViewById(R.id.accessTokenLabel);
        tokenValidLabel = (TextView) findViewById(R.id.tokenValidLabel);

        token = getSavedToken();
        validateAccessToken();
        accessTokenLabel.setText(getAccessToken());
        if (getAccessToken() != null) {
            Log.d("MainActivity", getAccessToken());
        }

        Button searchButton = (Button) findViewById(R.id.validateTokenButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tokenValidLabel.setText("Checking...");
                validateAccessToken();
            }
        });

        Button refreshTokenButton = (Button) findViewById(R.id.refreshTokenButton);
        refreshTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefreshTokenHandler e = new RefreshTokenHandler(OauthConstants.CLIENT_ID, OauthConstants.CLIENT_SECRET, token);
                try {
                    setSSL();
                    IdentityProxy.getInstance().init(OauthConstants.CLIENT_ID, OauthConstants.CLIENT_SECRET, MainActivity.this);
                    IdentityProxy.getInstance().setAccessTokenURL(getAccessTokenURL());
                    e.obtainNewAccessToken();
                } catch (InterruptedException | ExecutionException | TimeoutException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void validateAccessToken() {
        APITask apiTask = (APITask) new APITask(new APITask.AsyncResponse() {
            @Override
            public void processFinish(Boolean output) {
                if (output) {
                    tokenValidLabel.setText("Valid Token");
                } else
                    tokenValidLabel.setText("Invalid Token");
            }
        }, getAccessToken()).execute();
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
                init(OauthConstants.CLIENT_ID,OauthConstants.CLIENT_SECRET,this);
                break;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    public void onNewTokenReceived() {
        try {
            token = IdentityProxy.getInstance().getToken();
            storeToken(token);
            String accessToken = IdentityProxy.getInstance().getToken().getAccessToken();
            String refreshToken = IdentityProxy.getInstance().getToken().getRefreshToken();
            accessTokenLabel.setText(accessToken);
            storeUserData(accessToken, refreshToken, getUserName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onTokenReceived() {
        try {
            token = IdentityProxy.getInstance().getToken();
            storeToken(token);
            String jstring = IdentityProxy.getInstance().getToken().getIdToken();
            String accessToken = IdentityProxy.getInstance().getToken().getAccessToken();
            String refreshToken = IdentityProxy.getInstance().getToken().getRefreshToken();
            String[] strings = jstring.split("\\}");
            JSONObject payload = new JSONObject(strings[1]+"}");
            String userName = payload.getString("sub");
            int index = userName.indexOf('@');
            if(index>0){
                userName = userName.substring(0, index);
            }
            Log.v("Subject",userName);
            accessTokenLabel.setText(accessToken);
            storeUserData(accessToken, refreshToken, userName);
            MenuItem item = menu.findItem(R.id.action_settings);
            //item.setVisible(false);
            item.setTitle("Welcome : " + userName);
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

    private void storeToken(Token token) {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(token); // myObject - instance of MyObject
        prefsEditor.putString("token", json);
        prefsEditor.commit();
    }

    private Token getSavedToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("token", null);
        Token token = gson.fromJson(json, Token.class);
        return token;
    }


    private void storeUserData(String accessToken, String refreshToken, String userName) {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ACCESS_TOKEN", accessToken);
        editor.putString("REFRESH_TOKEN", refreshToken);
        editor.putString("USERNAME", userName);
        editor.apply();
    }

    public String getAccessToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("ACCESS_TOKEN", null);
        return accessToken;
    }

    public String getRefreshToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        String refreshToken = sharedPreferences.getString("REFRESH_TOKEN", null);
        return refreshToken;
    }

    private String getUserName() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("USERNAME", null);
        return userName;
    }

    private String getAccessTokenURL() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        String accessTokenUrl = sharedPreferences.getString("ACCESS_TOKEN_URL", null);
        return accessTokenUrl;
    }

    void setSSL(){
        InputStream inputStream = MainActivity.this.getResources().openRawResource(R.raw.truststore);
        ServerUtilities.enableSSL(inputStream, OauthConstants.TRUSTSTORE_PASSWORD);
    }
}