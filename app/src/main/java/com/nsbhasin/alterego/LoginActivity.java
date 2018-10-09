package com.nsbhasin.alterego;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class LoginActivity extends AppCompatActivity {

    RelativeLayout mLoginLayout, mSignupLayout;
    Button mLoginButton, mSignupButton;

    Handler mHandler = new Handler();
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mLoginLayout.setVisibility(View.VISIBLE);
            mSignupLayout.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);

        mLoginLayout = findViewById(R.id.login_layout);
        mSignupLayout = findViewById(R.id.signup_layout);

        mLoginButton = findViewById(R.id.btn_login);
        mSignupButton = findViewById(R.id.btn_signup);

        mHandler.postDelayed(mRunnable, 2000);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(signupIntent);
            }
        });

        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(signupIntent);
            }
        });
    }
}
