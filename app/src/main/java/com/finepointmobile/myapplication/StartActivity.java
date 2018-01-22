package com.finepointmobile.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.util.VKUtil;

public class StartActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(StartActivity.this);
        Log.d("AmyLog", "onCreate: ");
        ImageButton signIn = findViewById(R.id.signVK);
        if(VKSdk.isLoggedIn()){
            Intent intent = new Intent(StartActivity.this,MainActivity.class);
            intent.putExtra("sign","true");
            startActivity(intent);
        }
        if(!sharedPreferences.getString("skip","1").equals("1")){
            Intent intent = new Intent(StartActivity.this,MainActivity.class);
            intent.putExtra("sign","true");
            startActivity(intent);
        }
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!VKSdk.isLoggedIn()) {
                    VKSdk.login(StartActivity.this, "friend", "wall");
                    String[] fingerprints = VKUtil.getCertificateFingerprint(StartActivity.this, StartActivity.this.getPackageName());
                    Log.d("FINGERPRINT : ", fingerprints[0]);
                }
                else {
                    Intent intent = new Intent(StartActivity.this,MainActivity.class);
                    intent.putExtra("sign","true");
                    startActivity(intent);
                }
            }
        });
        Toolbar mToolbar = findViewById(R.id.toolbarStart);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Время делать");
        EditText editText = findViewById(R.id.editName);
        Button buttonSkip = findViewById(R.id.buttonSkip);
        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editText.getText().equals("")){
                    Intent intent = new Intent(StartActivity.this,MainActivity.class);
                    SavePreferences("skip", String.valueOf(editText.getText()));
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Intent intent = new Intent(StartActivity.this,MainActivity.class);
                intent.putExtra("sign","true");
                startActivity(intent);
            }
            @Override
            public void onError(VKError error) {
                Intent intent = new Intent(StartActivity.this,MainActivity.class);
                intent.putExtra("sign","false");
                startActivity(intent);
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void SavePreferences(String key, String value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.commit();
    }
}
