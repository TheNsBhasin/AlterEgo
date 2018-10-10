package com.nsbhasin.alterego.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.nsbhasin.alterego.R;
import com.nsbhasin.alterego.data.Interest;

import java.util.ArrayList;
import java.util.List;

public class InterestsAdapter extends RecyclerView.Adapter<InterestsAdapter.InterestsViewHolder> {
    private List<Interest> mInterests = new ArrayList<>();

    public InterestsAdapter() {
    }

    public InterestsAdapter(final List<Interest> interests) {
        mInterests = interests;
    }

    @NonNull
    @Override
    public InterestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interest, parent, false);
        return new InterestsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final InterestsViewHolder holder, final int position) {
        holder.bind(position);
    }

    public List<Interest> getInterests() {
        return mInterests;
    }

    public void setInterests(List<Interest> interests) {
        mInterests = interests;
    }

    @Override
    public int getItemCount() {
        return mInterests == null ? 0 : mInterests.size();
    }

    class InterestsViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        private ToggleButton mInterest;
        private Context mContext;

        public InterestsViewHolder(@NonNull View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mInterest = itemView.findViewById(R.id.tb_interest);
            mInterest.setOnCheckedChangeListener(this);
        }

        void bind(final int position) {
            mInterest.setText(mInterests.get(position).getInterest());
            mInterest.setTextOn(mInterests.get(position).getInterest());
            mInterest.setTextOff(mInterests.get(position).getInterest());
            mInterest.setChecked(mInterests.get(position).isChecked());
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mInterests.get(getAdapterPosition()).setChecked(isChecked);
        }
    }
}
