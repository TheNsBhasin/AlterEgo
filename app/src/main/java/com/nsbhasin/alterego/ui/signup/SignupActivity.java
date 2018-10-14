package com.nsbhasin.alterego.ui.signup;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nsbhasin.alterego.R;
import com.nsbhasin.alterego.database.entity.User;
import com.nsbhasin.alterego.ui.NonSwipeableViewPager;
import com.nsbhasin.alterego.ui.login.LoginActivity;
import com.nsbhasin.alterego.ui.main.MainActivity;

import java.util.HashMap;

interface InterfaceCommunicator {
    boolean onPageChanged();
}

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = SignupActivity.class.getSimpleName();

    private SignupPagerAdapter mSignupPagerAdapter;

    private NonSwipeableViewPager mViewPager;

    private FloatingActionButton mNextFab;
    public InterfaceCommunicator mCallback;
    private UserViewModel mViewModel;
    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_signup);

        mViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24px);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();

        mSignupPagerAdapter = new SignupPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSignupPagerAdapter);

        final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixel) {

            }

            @Override
            public void onPageSelected(int position) {
                mCallback = (InterfaceCommunicator) mSignupPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
            }

            @Override
            public void onPageScrollStateChanged(int position) {

            }
        };

        mViewPager.addOnPageChangeListener(onPageChangeListener);

        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                onPageChangeListener.onPageSelected(mViewPager.getCurrentItem());
            }
        });

        mNextFab = findViewById(R.id.fab_next);
        mNextFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextPage();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mViewPager.getCurrentItem();
                if (position > 0) {
                    mViewPager.setCurrentItem(position - 1);
                } else {
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void registerUser() {
        final User user = mViewModel.getUser().getValue();
        Log.d(TAG, "Email: " + user.getEmail() + ", Password: " + user.getPassword());
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = null;
                            if (currentUser != null) {
                                uid = currentUser.getUid();
                                mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                                HashMap<String, String> userMap = new HashMap<String, String>();
                                userMap.put("name", user.getName());
                                userMap.put("age", String.valueOf(user.getAge()));
                                userMap.put("gender", user.getGender());
                                userMap.put("tagline", user.getTagline());
                                userMap.put("interests", user.getInterests());
                                userMap.put("device_token", FirebaseInstanceId.getInstance().getToken());

                                mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "Registration successful");
                                            Intent mainintent = new Intent(SignupActivity.this, MainActivity.class);
                                            mainintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(mainintent);
                                            finish();
                                        }
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(SignupActivity.this, "Registration faild", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void nextPage() {
        if (mCallback != null && mCallback.onPageChanged()) {
            int position = mViewPager.getCurrentItem();
            if (position + 1 < mSignupPagerAdapter.getCount()) {
                Log.d("SignupActivity", "Next position");
                mViewPager.setCurrentItem(position + 1);
            } else {
                Log.d("SignupActivity", "startMainActivity");
                registerUser();
            }
        }
    }
}