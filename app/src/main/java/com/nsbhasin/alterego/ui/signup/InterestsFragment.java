package com.nsbhasin.alterego.ui.signup;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nsbhasin.alterego.R;
import com.nsbhasin.alterego.database.entity.Interest;
import com.nsbhasin.alterego.ui.InterestsAdapter;

import java.util.ArrayList;
import java.util.List;

public class InterestsFragment extends Fragment implements InterfaceCommunicator {
    private static final String TAG = InterfaceCommunicator.class.getSimpleName();

    private RecyclerView mInterests;
    private InterestsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<Interest> mDataset;

    private UserViewModel mViewModel;

    public InterestsFragment() {

    }

    public static InterestsFragment newInstance() {
        return new InterestsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interests, container, false);
        Context context = inflater.getContext();
        mInterests = view.findViewById(R.id.rv_interests);

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
                mViewModel.setInterests(interests);
            }
        });

        mInterests.setAdapter(mAdapter);

        return view;
    }

    private boolean validate() {
        String interests = mAdapter.getInterests();
        int count = 0;
        for (int i = 0; i < interests.length(); ++i) {
            if (interests.charAt(i) == '1') {
                ++count;
            }
        }

        Log.d("InterestsFragment", "Count: " + count);

        if (count >= 5) {
            return true;
        }

        Snackbar.make(this.getView(), "Select at least 5 interests", Snackbar.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onPageChanged() {
        boolean isDataValid = validate();
        if (isDataValid) {
            mViewModel.setInterests(mAdapter.getInterests());
        }
        return isDataValid;
    }
}