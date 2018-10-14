package com.nsbhasin.alterego.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.nsbhasin.alterego.R;
import com.nsbhasin.alterego.database.entity.Interest;
import com.nsbhasin.alterego.ui.InterestsAdapter;
import com.nsbhasin.alterego.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();

    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;

    private ScrollView mScrollView;
    private TextView mName, mTagline, mAge, mGender;
    private RecyclerView mInterests;
    private ImageView mDisplayPic;
    private Button mLogout;

    private RecyclerView.LayoutManager mLayoutManager;
    private InterestsAdapter mAdapter;

    private List<Interest> mDataset;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mScrollView = view.findViewById(R.id.scroll_view);
        mName = view.findViewById(R.id.tv_name);
        mTagline = view.findViewById(R.id.tv_tagline);
        mAge = view.findViewById(R.id.tv_age);
        mGender = view.findViewById(R.id.tv_gender);
        mDisplayPic = view.findViewById(R.id.img_user);
        mInterests = view.findViewById(R.id.rv_interests);
        mLogout = view.findViewById(R.id.btn_logout);

        Context context = inflater.getContext();

        mLayoutManager = new GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false);
        mInterests.setLayoutManager(mLayoutManager);

        final String[] dataset = context.getResources().getStringArray(R.array.interests);
        mDataset = new ArrayList<>();
        for (String interest : dataset) {
            mDataset.add(new Interest(interest));
        }

        mAdapter = new InterestsAdapter(mDataset, new InterestsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick() {
                String interests = mAdapter.getInterests();
                Log.d(TAG, "Interests:" + interests);
                mUserDatabase.child("interests").setValue(interests);
            }
        });

        mInterests.setAdapter(mAdapter);

        mAuth = FirebaseAuth.getInstance();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String tagline = dataSnapshot.child("tagline").getValue().toString();
                String age = dataSnapshot.child("age").getValue().toString();
                String gender = dataSnapshot.child("gender").getValue().toString();
                String interests = dataSnapshot.child("interests").getValue().toString();

                mName.setText(name);
                mTagline.setText(tagline);
                mAge.setText(age);
                mGender.setText(gender);
                mAdapter.setInterests(interests);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserDatabase.child("online").setValue(ServerValue.TIMESTAMP);
                mAuth.signOut();
                sendToStart();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mScrollView != null) {
            mScrollView.smoothScrollTo(0, 0);
        }
    }

    private void sendToStart() {
        Intent startIntent = new Intent(getActivity(), LoginActivity.class);
        startActivity(startIntent);
        getActivity().finish();
    }

    @Override
    public void onPause() {
        super.onPause();

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            mUserDatabase.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
}