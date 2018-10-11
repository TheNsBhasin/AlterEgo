package com.nsbhasin.alterego.data;

import android.support.annotation.NonNull;

public class Interest {
    private String interest;
    private boolean isChecked;

    public Interest() {

    }

    public Interest(String interest) {
        this.interest = interest;
        this.isChecked = false;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @NonNull
    @Override
    public String toString() {
        return interest;
    }
}
