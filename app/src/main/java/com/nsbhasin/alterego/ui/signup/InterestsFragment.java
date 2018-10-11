package com.nsbhasin.alterego.ui.signup;

import android.arch.lifecycle.Observer;
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
import com.nsbhasin.alterego.data.Interest;
import com.nsbhasin.alterego.data.User;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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

        mAdapter = new InterestsAdapter(mDataset);
        mInterests.setAdapter(mAdapter);

        mViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null && mAdapter != null && user.getInterests() != null) {
                    mAdapter.setInterests(user.getInterests());
                    mAdapter.notifyDataSetChanged();
                } else {
                    mAdapter.setInterests(mDataset);
                }
            }
        });

        return view;
    }

    private boolean validate() {
        List<Interest> interestList = mAdapter.getInterests();
        int count = 0;
        for (int i = 0; i < interestList.size(); ++i) {
            if (interestList.get(i).isChecked()) {
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