package com.nsbhasin.alterego;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class InterestsAdapter extends RecyclerView.Adapter<InterestsAdapter.InterestsViewHolder> {
    private List<Interest> mDataset = new ArrayList<>();

    InterestsAdapter(final String[] dataset) {
        for (String data : dataset) {
            mDataset.add(new Interest(data));
        }
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

    @Override
    public int getItemCount() {
        return mDataset == null ? 0 : mDataset.size();
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
            mInterest.setText(mDataset.get(position).getInterest());
            mInterest.setTextOn(mDataset.get(position).getInterest());
            mInterest.setTextOff(mDataset.get(position).getInterest());
            mInterest.setChecked(mDataset.get(position).isChecked());
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mDataset.get(getAdapterPosition()).setChecked(isChecked);
        }
    }
}
