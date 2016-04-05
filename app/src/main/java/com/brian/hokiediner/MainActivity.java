package com.brian.hokiediner;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.json.JSONObject;
import org.wso2.mobile.idp.proxy.IDPProxyActivity;
import org.wso2.mobile.idp.proxy.IdentityProxy;
import org.wso2.mobile.idp.proxy.callbacks.AccessTokenCallBack;
import org.wso2.mobile.idp.proxy.utils.ServerUtilities;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
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
            JSONObject mainObject = new JSONObject(IdentityProxy.getInstance().getToken().getIdToken().substring(27));
            String subject = mainObject.getString("sub");
            int index = subject.indexOf('@');
            if(index>0){
                subject = subject.substring(0, index);
            }
            Log.v("Subject", subject);
            MenuItem item = menu.findItem(R.id.action_settings);
            //item.setVisible(false);
            item.setTitle("Welcome : " + subject);
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

    void setSSL(){
        InputStream inputStream = MainActivity.this.getResources().openRawResource(R.raw.truststore);
        ServerUtilities.enableSSL(inputStream, OauthCostants.TRUSTSTORE_PASSWORD);
    }

}