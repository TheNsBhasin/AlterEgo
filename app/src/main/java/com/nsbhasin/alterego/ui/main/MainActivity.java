package com.nsbhasin.alterego.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.nsbhasin.alterego.R;
import com.nsbhasin.alterego.ui.login.LoginActivity;
import com.nsbhasin.alterego.utils.Constants;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TabLayout mTabLayout;

    private MainPagerAdapter mMainPagerAdapter;

    private ViewPager mViewPager;

    private FirebaseAuth mAuth;

    private DatabaseReference mUserRef;
    private DatabaseReference mPublicKeyRef;

    private KeyPairGenerator mGenerator;
    private KeyPair mKeyPair;
    private PublicKey mPublicKey;
    private PrivateKey mPrivateKey;

    private String mPrivateKeyString;
    private String mPublicKeyString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        mTabLayout = findViewById(R.id.tab_layout);

        mAuth = FirebaseAuth.getInstance();

        mPublicKeyRef = FirebaseDatabase.getInstance().getReference().child("PublicKey");

        if (mAuth.getCurrentUser() != null) {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(mAuth.getCurrentUser().getUid());

            try {
                generateKeys();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        mMainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mMainPagerAdapter);
        mViewPager.setCurrentItem(1);

        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int position) {

            }
        });

    }

    private void generateKeys() throws NoSuchAlgorithmException {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.ALTER_EGO_PREF, MODE_PRIVATE);
        if (mAuth.getCurrentUser() != null) {
            mPrivateKeyString = sharedPreferences.getString("private_key" + mAuth.getCurrentUser().getUid(), null);
            mPublicKeyString = sharedPreferences.getString("public_key" + mAuth.getCurrentUser().getUid(), null);
        }

        if (mPrivateKeyString == null || mPublicKeyString == null) {
            mGenerator = KeyPairGenerator.getInstance("RSA");
            mGenerator.initialize(1024);

            mKeyPair = mGenerator.genKeyPair();
            mPrivateKey = mKeyPair.getPrivate();
            mPublicKey = mKeyPair.getPublic();

            if (mPrivateKey != null) {
                mPrivateKeyString = Base64.encodeToString(mPrivateKey.getEncoded(), Base64.DEFAULT);
            }

            if (mPublicKey != null) {
                mPublicKeyString = Base64.encodeToString(mPublicKey.getEncoded(), Base64.DEFAULT);
            }

            mPublicKeyRef.child(mAuth.getCurrentUser().getUid())
                    .child("public_key").setValue(mPublicKeyString)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            SharedPreferences.Editor editor = getSharedPreferences(Constants.ALTER_EGO_PREF, MODE_PRIVATE).edit();
                            editor.putString("private_key" + mAuth.getCurrentUser().getUid(), mPrivateKeyString);
                            editor.putString("public_key" + mAuth.getCurrentUser().getUid(), mPublicKeyString);
                            editor.apply();
                        }
                    });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            sendToLogin();
        }
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
