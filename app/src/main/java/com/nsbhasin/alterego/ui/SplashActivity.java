package com.nsbhasin.alterego.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nsbhasin.alterego.R;
import com.nsbhasin.alterego.ui.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_splash);

        Intent intent = new Intent(getApplicationContext(),
                LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
