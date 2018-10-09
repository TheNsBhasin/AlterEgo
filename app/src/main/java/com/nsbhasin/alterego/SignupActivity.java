package com.nsbhasin.alterego;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

public class SignupActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private NonSwipeableViewPager mViewPager;

    private FloatingActionButton mNextFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_signup);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24px);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mNextFab = findViewById(R.id.fab_next);
        mNextFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = mViewPager.getCurrentItem();
                if (position + 1 < mSectionsPagerAdapter.getCount()) {
                    mViewPager.setCurrentItem(position + 1);
                } else {
                    Log.d(this.getClass().getSimpleName(), "Intent to MainActivity");
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
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

    public static class SignupFragment extends Fragment {

        public SignupFragment() {
        }

        public static SignupFragment newInstance() {
            return new SignupFragment();
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_signup, container, false);
        }
    }

    public static class DetailsFragment extends Fragment {

        private ImageView mUserImage;
        private ToggleButton mMaleButton, mFemaleButton;

        public DetailsFragment() {
        }

        public static DetailsFragment newInstance() {
            return new DetailsFragment();
        }

        void selectMaleGender() {
            mMaleButton.setChecked(true);
            mFemaleButton.setChecked(false);
            mUserImage.setImageDrawable(this.getActivity().getDrawable(R.drawable.face_male));
            mMaleButton.setBackgroundColor(this.getActivity().getResources().getColor(R.color.colorPrimaryDark));
            mFemaleButton.setBackgroundColor(this.getActivity().getResources().getColor(R.color.gray));
        }

        void selectFemaleGender() {
            mFemaleButton.setChecked(true);
            mMaleButton.setChecked(false);
            mUserImage.setImageDrawable(this.getActivity().getDrawable(R.drawable.face_female));
            mFemaleButton.setBackgroundColor(this.getActivity().getResources().getColor(R.color.colorPrimaryDark));
            mMaleButton.setBackgroundColor(this.getActivity().getResources().getColor(R.color.gray));
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_details, container, false);
            mUserImage = view.findViewById(R.id.img_user);
            mMaleButton = view.findViewById(R.id.btn_male);
            mFemaleButton = view.findViewById(R.id.btn_female);

            if (mMaleButton.isChecked()) {
                selectMaleGender();
            } else {
                selectFemaleGender();
            }

            mMaleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selectMaleGender();
                    } else {
                        selectFemaleGender();
                    }
                }
            });

            mFemaleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selectFemaleGender();
                    } else {
                        selectMaleGender();
                    }
                }
            });

            return view;
        }
    }

    public static class InterestsFragment extends Fragment {

        private RecyclerView mInterests;
        private RecyclerView.Adapter mAdapter;
        private RecyclerView.LayoutManager mLayoutManager;


        public InterestsFragment() {

        }

        public static InterestsFragment newInstance() {
            return new InterestsFragment();
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_interests, container, false);
            Context context = inflater.getContext();
            mInterests = view.findViewById(R.id.rv_interests);

            mLayoutManager = new GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false);
            mInterests.setLayoutManager(mLayoutManager);

            final String[] dataset = context.getResources().getStringArray(R.array.interests);
            mAdapter = new InterestsAdapter(dataset);
            mInterests.setAdapter(mAdapter);
            return view;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return SignupFragment.newInstance();
                case 1:
                    return DetailsFragment.newInstance();
                case 2:
                    return InterestsFragment.newInstance();
                default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
