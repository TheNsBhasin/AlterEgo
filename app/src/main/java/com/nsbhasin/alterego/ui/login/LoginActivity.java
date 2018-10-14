package com.nsbhasin.alterego.ui.login;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nsbhasin.alterego.R;
import com.nsbhasin.alterego.ui.main.MainActivity;
import com.nsbhasin.alterego.ui.signup.SignupActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private RelativeLayout mLoginLayout, mSignupLayout;
    private Button mLoginButton, mSignupButton;
    private TextInputEditText mEmailText, mPasswordText;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;

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

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mLoginLayout = findViewById(R.id.login_layout);
        mSignupLayout = findViewById(R.id.signup_layout);

        mLoginButton = findViewById(R.id.btn_login);
        mSignupButton = findViewById(R.id.btn_signup);

        mEmailText = findViewById(R.id.et_email);
        mPasswordText = findViewById(R.id.et_password);

        mHandler.postDelayed(mRunnable, 2000);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValid = validate();
                if (!isValid) {
                    return;
                }

                String email = getEmail();
                String password = getPassword();
                login(email, password);
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

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String uid = mAuth.getCurrentUser().getUid();
                    String token = FirebaseInstanceId.getInstance().getToken();

                    mUserRef.child(uid).child("device_token").setValue(token).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent mainintent = new Intent(LoginActivity.this, MainActivity.class);
                            mainintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(mainintent);
                            finish();
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "Login faild", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getEmail() {
        if (mEmailText != null && !TextUtils.isEmpty(mEmailText.getText())) {
            return mEmailText.getText().toString().trim();
        }
        return "";
    }

    private String getPassword() {
        if (mPasswordText != null && !TextUtils.isEmpty(mPasswordText.getText())) {
            return mPasswordText.getText().toString().trim();
        }
        return "";
    }

    private boolean validate() {
        String email = getEmail();
        String password = getPassword();

        if (TextUtils.isEmpty(email)) {
            mEmailText.setError("Empty Field");
        } else if (TextUtils.isEmpty(password)) {
            mPasswordText.setError("Empty Field");
        } else {
            return true;
        }

        return false;
    }
}
