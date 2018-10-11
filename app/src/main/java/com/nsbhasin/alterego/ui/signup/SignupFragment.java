package com.nsbhasin.alterego.ui.signup;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nsbhasin.alterego.R;
import com.nsbhasin.alterego.data.User;

public class SignupFragment extends Fragment implements InterfaceCommunicator {
    private static final String TAG = SignupFragment.class.getSimpleName();

    private TextInputEditText mNameEditText, mEmailEditText, mPasswordEditText;

    private UserViewModel mViewModel;

    public SignupFragment() {
    }

    public static SignupFragment newInstance() {
        return new SignupFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        mNameEditText = view.findViewById(R.id.et_name);
        mEmailEditText = view.findViewById(R.id.et_email);
        mPasswordEditText = view.findViewById(R.id.et_password);

        mViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    Log.d(TAG, "onChanged -> Name: " + user.getName() + ", Email: " + user.getEmail() + ", Password: " + user.getPassword());
                    if (mNameEditText != null) {
                        mNameEditText.setText(user.getName());
                    }
                    if (mEmailEditText != null) {
                        mEmailEditText.setText(user.getEmail());
                    }
                    if (mPasswordEditText != null) {
                        mPasswordEditText.setText(user.getPassword());
                    }
                }
            }
        });

        return view;
    }

    private String getName() {
        if (mNameEditText != null && !TextUtils.isEmpty(mNameEditText.getText())) {
            return mNameEditText.getText().toString().trim();
        }
        return "";
    }

    private String getEmail() {
        if (mEmailEditText != null && !TextUtils.isEmpty(mEmailEditText.getText())) {
            return mEmailEditText.getText().toString().trim();
        }
        return "";
    }

    private String getPassword() {
        if (mPasswordEditText != null && !TextUtils.isEmpty(mPasswordEditText.getText())) {
            return mPasswordEditText.getText().toString().trim();
        }
        return "";
    }

    public boolean validate() {
        String name = getName();
        String email = getEmail();
        String password = getPassword();

        int count = password.length();

        Log.d(TAG, "validate -> Name: " + name + ", Email: " + email);

        if (TextUtils.isEmpty(name)) {
            mNameEditText.setError("Empty Field");
        } else if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError("Empty Field");
        } else if (TextUtils.isEmpty(password)) {
            mPasswordEditText.setError("Empty Field");
        } else if (count < 6) {
            mPasswordEditText.setError("Minimum 6 characters");
        } else {
            return true;
        }

        return false;
    }

    @Override
    public boolean onPageChanged() {
        boolean isDataValid = validate();
        if (isDataValid) {
            String name = getName();
            String email = getEmail();
            String password = getPassword();
            mViewModel.setName(name);
            mViewModel.setEmail(email);
            mViewModel.setPassword(password);
            Log.d(TAG, "onPageChanged -> Name: " + name + ", Email: " + email + ", Password: " + password);
        }
        return isDataValid;
    }
}