package com.nsbhasin.alterego.ui.signup;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.nsbhasin.alterego.database.entity.User;

public class UserViewModel extends ViewModel {
    private MutableLiveData<User> mLiveUser;
    private User mUser = new User();

    public LiveData<User> getUser() {
        if (mLiveUser == null) {
            mLiveUser = new MutableLiveData<>();
        }
        return mLiveUser;
    }

    public void setUser(User user) {
        mLiveUser.setValue(user);
    }

    public void setName(String name) {
        mUser.setName(name);
        setUser(mUser);
    }

    public void setEmail(String email) {
        mUser.setEmail(email);
        setUser(mUser);
    }

    public void setPassword(String password) {
        mUser.setPassword(password);
        setUser(mUser);
    }

    public void setAge(int age) {
        mUser.setAge(age);
        setUser(mUser);
    }

    public void setTagline(String tagline) {
        mUser.setTagline(tagline);
        setUser(mUser);
    }

    public void setGender(String gender) {
        mUser.setGender(gender);
        setUser(mUser);
    }

    public void setInterests(String interests) {
        mUser.setInterests(interests);
        setUser(mUser);
    }

}
