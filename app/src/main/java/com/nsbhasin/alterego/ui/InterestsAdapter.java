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
import com.nsbhasin.alterego.database.entity.Interest;

import java.util.ArrayList;
import java.util.List;

public class InterestsAdapter extends RecyclerView.Adapter<InterestsAdapter.InterestsViewHolder> {
    private List<Interest> mInterests = new ArrayList<>();
    private OnItemClickListener mListener;

    public InterestsAdapter() {
    }

    public InterestsAdapter(final List<Interest> interests, OnItemClickListener onItemClickListener) {
        mInterests = interests;
        mListener = onItemClickListener;
    }

    @NonNull
    @Override
    public InterestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int position) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interest, parent, false);
        return new InterestsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final InterestsViewHolder holder, final int position) {
        holder.bind(position);
    }

    public String getInterests() {
        char values[] = new char[mInterests.size()];
        for (int i = 0; i < mInterests.size(); ++i) {
            values[i] = (mInterests.get(i).isChecked()) ? '1' : '0';
        }
        return new String(values);
    }

    public void setInterests(String interests) {
        for (int i = 0; i < interests.length(); ++i) {
            mInterests.get(i).setChecked(interests.charAt(i) == '1');
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mInterests == null ? 0 : mInterests.size();
    }

    public interface OnItemClickListener {
        void onItemClick();
    }

    class InterestsViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        private ToggleButton mInterest;
        private Context mContext;
        private boolean onBind;

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
            mListener.onItemClick();
        }
    }
}
