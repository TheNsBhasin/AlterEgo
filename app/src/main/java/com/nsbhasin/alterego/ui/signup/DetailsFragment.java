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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.nsbhasin.alterego.R;
import com.nsbhasin.alterego.database.entity.User;

public class DetailsFragment extends Fragment implements InterfaceCommunicator {
    private static final String TAG = DetailsFragment.class.getSimpleName();

    private ImageView mUserImage;
    private ToggleButton mMaleButton, mFemaleButton;
    private TextInputEditText mAge, mTagline;

    private UserViewModel mViewModel;

    public DetailsFragment() {
    }

    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        mUserImage = view.findViewById(R.id.img_user);
        mMaleButton = view.findViewById(R.id.btn_male);
        mFemaleButton = view.findViewById(R.id.btn_female);
        mAge = view.findViewById(R.id.et_age);
        mTagline = view.findViewById(R.id.et_tagline);

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

        mViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    Log.d(TAG, "onChanged -> Age: " + user.getAge() + ", Gender: " + user.getGender() + ", Tagline: " + user.getTagline());
                    if (user.getGender().equals(User.MALE)) {
                        selectMaleGender();
                    } else {
                        selectFemaleGender();
                    }

                    if (mAge != null) {
                        mAge.setText(String.valueOf(user.getAge()));
                    }

                    if (mTagline != null) {
                        mTagline.setText(user.getTagline());
                    }
                }
            }
        });

        return view;
    }

    String getGender() {
        if (mMaleButton.isChecked()) {
            return User.MALE;
        } else {
            return User.FEMALE;
        }
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

    private int getAge() {
        if (mAge.getText() != null) {
            return Integer.parseInt(mAge.getText().toString());
        }
        return 18;
    }

    private String getTagline() {
        if (!TextUtils.isEmpty(mTagline.getText())) {
            return mTagline.getText().toString().trim();
        }
        return "";
    }

    public boolean validate() {
        if (TextUtils.isEmpty(getTagline())) {
            mTagline.setError("Empty Field");
        } else if (getAge() < 16 && getAge() > 100) {
            mTagline.setError("Are you sure?");
        } else {
            return true;
        }
        return false;
    }

    @Override
    public boolean onPageChanged() {
        boolean isDataValid = validate();
        if (isDataValid) {
            int age = getAge();
            String gender = getGender();
            String tagline = getTagline();
            mViewModel.setAge(age);
            mViewModel.setGender(gender);
            mViewModel.setTagline(tagline);
            Log.d(TAG, "onPageChanged -> Age: " + age + ", Gender: " + gender + ", Tagline: " + tagline);
        }
        return isDataValid;
    }
}