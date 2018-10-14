package com.nsbhasin.alterego.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nsbhasin.alterego.R;
import com.nsbhasin.alterego.database.entity.Friends;
import com.nsbhasin.alterego.ui.chat.ChatActivity;

public class ChatFragment extends Fragment {

    private static final String TAG = ChatFragment.class.getSimpleName();

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private DatabaseReference mFrinedsRef;

    private RecyclerView mFrineds;
    private FirebaseRecyclerAdapter<Friends, UserViewHolder> mAdapter;

    public ChatFragment() {
    }

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        mFrineds = view.findViewById(R.id.rv_friends);
        mFrineds.setHasFixedSize(true);
        mFrineds.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();

        Log.d(TAG, "Current user id: " + uid);

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mFrinedsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(uid);

        mUserRef.keepSynced(true);
        mFrinedsRef.keepSynced(true);

        mAdapter = new FirebaseRecyclerAdapter<Friends, UserViewHolder>(new FirebaseRecyclerOptions.Builder<Friends>().setQuery(mFrinedsRef, Friends.class).build()) {
            @Override
            protected void onBindViewHolder(@NonNull final UserViewHolder holder, int position, @NonNull Friends model) {
                final String friendUserId = getRef(position).getKey();

                Log.d(TAG, "Friends user id: " + friendUserId);

                mUserRef.child(friendUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String name = dataSnapshot.child("name").getValue().toString();

                        holder.setName(name);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
                                chatIntent.putExtra("from_user_id", friendUserId);
                                chatIntent.putExtra("from_username", name);
                                Log.d(TAG, "from_user_id: " + friendUserId);
                                Log.d(TAG, "from_username: " + name);
                                startActivity(chatIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_user, viewGroup, false);

                return new UserViewHolder(view);
            }
        };

        mFrineds.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mAdapter != null) {
            mAdapter.startListening();
        }

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mUserRef.child(user.getUid()).child("online").setValue("true");
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        private ImageView mDisplayPicture;
        private TextView mName;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mDisplayPicture = itemView.findViewById(R.id.img_user);
            mName = itemView.findViewById(R.id.tv_name);
        }

        public void setName(String name) {
            mName.setText(name);
        }
    }
}

