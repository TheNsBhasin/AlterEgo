package com.nsbhasin.alterego.ui.chat;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nsbhasin.alterego.R;
import com.nsbhasin.alterego.database.entity.Messages;
import com.nsbhasin.alterego.ui.profile.ProfileActivity;
import com.nsbhasin.alterego.utils.Constants;
import com.nsbhasin.alterego.utils.GetTimeAgo;
import com.nsbhasin.alterego.utils.RSAAlgorithm;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = ChatActivity.class.getSimpleName();

    private static final int MESSAGES_TO_LOAD = 40;

    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mPublicKeyRef;

    private String mSenderUid, mReceiverUid;
    private String mReceiverUsername;

    private String mReceiverPublicKeyString;
    private String mSenderPublicKeyString;
    private String mPrivateKeyString;

    private int mCrypted = 0;
    private int mCurrentPage = 1;
    private int mRefreshed = 0;

    private Toolbar mToolbar;
    private TextView mEncryptMessage;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeLayout;

    private LinearLayoutManager mLayoutManager;

    private ImageButton mAddButton, mSendButton;
    private EditText mMessageText;

    private TextView mUsername, mLastSeen;
    private CircleImageView mUserDp;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currUser = mAuth.getCurrentUser();
        if (currUser != null) {
            mRootRef.child("Users").child(currUser.getUid()).child("online").setValue("true");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser currUser = mAuth.getCurrentUser();
        if (currUser != null) {
            mRootRef.child("Users").child(currUser.getUid()).child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_chat);

        mToolbar = findViewById(R.id.chat_appbar);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mSenderUid = mAuth.getCurrentUser().getUid();

        mLastSeen = findViewById(R.id.chat_user_last_seen);
        mUsername = findViewById(R.id.chat_user_name);
        mUserDp = findViewById(R.id.chat_user_dp);

        mAddButton = findViewById(R.id.chat_add_but);
        mSendButton = findViewById(R.id.chat_send_but);
        mMessageText = findViewById(R.id.chat_message_text);

        mEncryptMessage = findViewById(R.id.encrypt_message);

        mSwipeLayout = findViewById(R.id.swipe_layout);
        mRecyclerView = findViewById(R.id.rv_chats);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mReceiverUid = getIntent().getStringExtra("from_user_id");
        mReceiverUsername = getIntent().getStringExtra("from_username");

        Log.d(TAG, "mChatUserId: " + mReceiverUid);
        Log.d(TAG, "mChatUsername: " + mReceiverUsername);

        mPublicKeyRef = FirebaseDatabase.getInstance().getReference().child("PublicKey");

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("online").setValue("true");

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(view);

        mRootRef.child("messages").child(mSenderUid).child(mReceiverUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                if (count < 10) {
                    mEncryptMessage.setVisibility(View.VISIBLE);
                } else {
                    mEncryptMessage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mUsername.setText(mReceiverUsername);
        mLastSeen.setVisibility(View.GONE);

        mUserDp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(ChatActivity.this, ProfileActivity.class);
                profileIntent.putExtra("from_user_id", mReceiverUid);
                startActivity(profileIntent);
            }
        });

        mPublicKeyRef.child(mReceiverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("public_key")) {
                    mReceiverPublicKeyString = dataSnapshot.child("public_key").getValue().toString();
                    mCrypted = 1;
                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mPublicKeyRef.child(mSenderUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("public_key")) {
                    mSenderPublicKeyString = dataSnapshot.child("public_key").getValue().toString();
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mPublicKeyRef.keepSynced(true);

        loadMessages();

        mRootRef.child("Users").child(mReceiverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("online").getValue().toString();
                String image = "";
                if (dataSnapshot.child("thumb_image").getValue() != null) {
                    image = dataSnapshot.child("thumb_image").getValue().toString();
                }

                if (online.equals("true")) {
                    mLastSeen.setText("online");
                    mLastSeen.setVisibility(View.VISIBLE);

                } else {
                    GetTimeAgo gta = new GetTimeAgo();
                    long lasTime = Long.parseLong(online);
                    String lastSeenTime = gta.getTimeAgo(lasTime);
                    mLastSeen.setText(lastSeenTime);
                    mLastSeen.setVisibility(View.VISIBLE);
                }

                if (!image.isEmpty()) {
                    Picasso.get().load(image)
                            .placeholder(R.drawable.face_male)
                            .into(mUserDp);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage += 1;
                mRefreshed = 1;
                loadMessages();
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendMessage();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }
            }
        });

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendImage();
            }
        });
    }

    private void sendImage() {
        CropImage.activity()
                .start(ChatActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                uploadImage(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadImage(Uri resultUri) {
        Random random = new Random();

        Map receiverMap = new HashMap();
        receiverMap.put("Chat/" + mReceiverUid + "/"
                + mSenderUid + "/timestamp", ServerValue.TIMESTAMP);
        receiverMap.put("Chat/" + mSenderUid + "/" + mReceiverUid + "/timestamp", ServerValue.TIMESTAMP);

        mRootRef.updateChildren(receiverMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d(TAG, databaseError.getMessage());
                }
            }
        });

        StorageReference filepath = mStorageRef.child("image_messages").child(mSenderUid).child(mReceiverUid).child(random.nextInt(10000000) + ".jpg");
        File image_file = new File(resultUri.getPath());

        Bitmap compressedImageBitmap = null;
        try {
            compressedImageBitmap = new Compressor(this)
                    .setMaxHeight(400)
                    .setMaxWidth(400)
                    .setQuality(2)
                    .compressToBitmap(image_file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] image_data = baos.toByteArray();
        UploadTask uploadTask = filepath.putBytes(image_data);

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                String download_url = task.getResult().getMetadata().getReference().getDownloadUrl().toString();
                if (task.isSuccessful()) {
                    String senderRef = "messages/" + mSenderUid + "/" + mReceiverUid;
                    String receiverRef = "messages/" + mReceiverUid + "/" + mSenderUid;

                    DatabaseReference usermessage_push = mRootRef.child("messages")
                            .child(mSenderUid).child(mReceiverUid).push();

                    String push_id = usermessage_push.getKey();

                    Map messageMap = new HashMap();
                    messageMap.put("message", download_url);
                    messageMap.put("seen", false);
                    messageMap.put("type", "image");
                    messageMap.put("time", ServerValue.TIMESTAMP);
                    messageMap.put("from", mSenderUid);


                    Map messageUserMap = new HashMap();
                    messageUserMap.put(senderRef + "/" + push_id, messageMap);
                    messageUserMap.put(receiverRef + "/" + push_id, messageMap);


                    mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d(TAG, databaseError.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    private void sendMessage() throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        mRefreshed = 0;
        final String message = mMessageText.getText().toString();
        mMessageText.setText("");
        final String Uid = mAuth.getCurrentUser().getUid();


        RSAAlgorithm rsaAlgo = new RSAAlgorithm();
        String receiverMessage = message;
        String senderMessage = message;
        if (mCrypted == 1) {
            Log.d(TAG, "Receiver Public Key: " + mReceiverPublicKeyString);
            Log.d(TAG, "Sender Public Key: " + mSenderPublicKeyString);
            receiverMessage = rsaAlgo.Encrypt(message, mReceiverPublicKeyString);
            senderMessage = rsaAlgo.Encrypt(message, mSenderPublicKeyString);
        }
        final String encryptedReceiverMessage = receiverMessage;
        final String encryptedSenderMessage = senderMessage;

        Log.d(TAG, "encryptedReceiverMessage: " + encryptedReceiverMessage);
        Log.d(TAG, "encryptedSenderMessage: " + encryptedSenderMessage);

        Map chatUserMap = new HashMap();
        chatUserMap.put("Chat/" + mReceiverUid + "/"
                + Uid + "/timestamp", ServerValue.TIMESTAMP);
        chatUserMap.put("Chat/" + Uid + "/" + mReceiverUid + "/timestamp", ServerValue.TIMESTAMP);

        mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d(TAG, databaseError.getMessage());
                }
            }
        });

        if (!TextUtils.isEmpty(message)) {

            mRootRef.child("Chat").child(mReceiverUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(Uid)) {
                        Map chatAddMap = new HashMap();
                        chatAddMap.put("seen", "false");
                        chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                        Map chatUserMap = new HashMap();
                        chatUserMap.put("Chat/" + mReceiverUid + "/"
                                + Uid, chatAddMap);

                        mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Log.d(TAG, databaseError.getMessage());
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            String senderRef = "messages/" + Uid + "/" + mReceiverUid;
            String receiverRef = "messages/" + mReceiverUid + "/" + Uid;

            DatabaseReference usermessage_push = mRootRef.child("messages")
                    .child(Uid).child(mReceiverUid).push();

            String push_id = usermessage_push.getKey();

            Map receiverMessageMap = new HashMap();
            receiverMessageMap.put("message", encryptedReceiverMessage);
            receiverMessageMap.put("seen", false);
            receiverMessageMap.put("type", "text");
            receiverMessageMap.put("time", ServerValue.TIMESTAMP);
            receiverMessageMap.put("from", Uid);

            Map senderMessageMapOwn = new HashMap();
            senderMessageMapOwn.put("message", encryptedSenderMessage);
            senderMessageMapOwn.put("seen", false);
            senderMessageMapOwn.put("type", "text");
            senderMessageMapOwn.put("time", ServerValue.TIMESTAMP);
            senderMessageMapOwn.put("from", Uid);

            Map messageUserMap = new HashMap();
            messageUserMap.put(senderRef + "/" + push_id, senderMessageMapOwn);
            messageUserMap.put(receiverRef + "/" + push_id, receiverMessageMap);


            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(final DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null) {
                        Log.d(TAG, databaseError.getMessage());
                    } else {


                        mRootRef.child("Users").child(mReceiverUid).child("online").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String online = dataSnapshot.getValue().toString();

                                if (!online.equals("true")) {

                                    Map notimap = new HashMap();
                                    notimap.put("from", Uid);
                                    notimap.put("type", "message");
                                    notimap.put("message", message);

                                    mRootRef.child("MessageNotification").child(mReceiverUid).push().setValue(notimap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            });
        }
    }

    private void loadMessages() {
        SharedPreferences prefs = getSharedPreferences(Constants.ALTER_EGO_PREF, MODE_PRIVATE);
        mPrivateKeyString = prefs.getString("private_key" + mSenderUid, null);

        final Query messagesRef = mRootRef.child("messages").child(mSenderUid)
                .child(mReceiverUid).limitToLast(MESSAGES_TO_LOAD * mCurrentPage);

        final FirebaseRecyclerAdapter<Messages, MessageViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Messages, MessageViewHolder>(
                new FirebaseRecyclerOptions
                        .Builder<Messages>()
                        .setQuery(messagesRef, Messages.class)
                        .build()) {
            @Override
            protected void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position, @NonNull final Messages model) {
                RSAAlgorithm rsaAlgorithm = new RSAAlgorithm();
                String message = model.getMessage();
                String type = model.getType();

                Log.d(TAG, "Message: " + message);
                Log.d(TAG, "Type: " + type);

                String decryptedMessage = message;
                if (mCrypted == 1 && type.equals("text")) {

                    if (decryptedMessage.length() > 100 && !decryptedMessage.contains(" ")) {

                        try {
                            decryptedMessage = rsaAlgorithm.Decrypt(message, mPrivateKeyString);
                            Log.d(TAG, "Decrypted Message: " + decryptedMessage);
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        } catch (IllegalBlockSizeException e) {
                            e.printStackTrace();
                        } catch (BadPaddingException e) {
                            e.printStackTrace();
                        } catch (InvalidKeySpecException e) {
                            e.printStackTrace();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }

                holder.setMessage(decryptedMessage, type);
                holder.setTime(model.getTime(), model.getType());
                holder.setPosition(mSenderUid, model.getFrom());
                holder.setType(model.getType());

                holder.mMessageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        CharSequence options[] = new CharSequence[]{"Copy", "Delete"};


                        final String message_key = getRef(position).getKey();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChatActivity.this);


                        if (model.getType().equals("image")) {
                            alertDialogBuilder.setTitle("Delete Image?");
                            alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String senderRef = "messages/" + mSenderUid + "/" + mReceiverUid;
                                    String receiverRef = "messages/" + mReceiverUid + "/" + mSenderUid;

                                    Map deleteMap = new HashMap();
                                    deleteMap.put(senderRef + "/" + message_key, null);
                                    deleteMap.put(receiverRef + "/" + message_key, null);

                                    mRootRef.updateChildren(deleteMap).addOnSuccessListener(new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            Toast.makeText(ChatActivity.this, "Message Deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                        } else {
                            alertDialogBuilder.setTitle(holder.mTextView.getText().toString());
                            alertDialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (which == 0) {
                                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText("TEXT", holder.mTextView.getText().toString());
                                        clipboard.setPrimaryClip(clip);
                                        Toast.makeText(ChatActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                                    }
                                    if (which == 1) {
                                        String senderRef = "messages/" + mSenderUid + "/" + mReceiverUid;
                                        String receiverRef = "messages/" + mReceiverUid + "/" + mSenderUid;

                                        Map deleteMap = new HashMap();
                                        deleteMap.put(senderRef + "/" + message_key, null);
                                        deleteMap.put(receiverRef + "/" + message_key, null);

                                        mRootRef.updateChildren(deleteMap).addOnSuccessListener(new OnSuccessListener() {
                                            @Override
                                            public void onSuccess(Object o) {
                                                Toast.makeText(ChatActivity.this, "Message Deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });
                            alertDialogBuilder.show();
                        }

                        return true;
                    }
                });
            }

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
                Log.d(TAG, "Position: " + position);
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
                return new MessageViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                                       int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {

                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.scrollToPosition(firebaseRecyclerAdapter.getItemCount());
                        }
                    }, 100);
                }


            }
        });


        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                mRecyclerView.scrollToPosition(positionStart);
                if (mRefreshed == 1) {
                    mRecyclerView.scrollToPosition(itemCount - 1);
                }
                mLayoutManager.setReverseLayout(false);
                mLayoutManager.setStackFromEnd(true);
            }
        });


        mSwipeLayout.setRefreshing(false);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        private TextView mTimeView, mTextView, mImageTimeView;
        private ImageView mImageView;
        private LinearLayout mMessageView, mMessageLayout, mImageLayout;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            mTimeView = itemView.findViewById(R.id.message_item_time);
            mImageTimeView = itemView.findViewById(R.id.image_item_time);

            mTextView = itemView.findViewById(R.id.message_item_text);
            mImageView = itemView.findViewById(R.id.chat_image);

            mMessageView = itemView.findViewById(R.id.message_item_view);
            mMessageLayout = itemView.findViewById(R.id.message_item_layout);
            mImageLayout = itemView.findViewById(R.id.image_item_layout);
        }

        public void setType(String type) {

            if (type.equals("text")) {
                mParams.height = 0;
                mMessageLayout.setVisibility(View.VISIBLE);

            } else if (type.equals("image")) {
                mParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                mMessageLayout.setVisibility(View.GONE);
            }

            mImageLayout.setLayoutParams(mParams);

        }

        public void setTime(long time, String type) {
            GetTimeAgo gta = new GetTimeAgo();
            String lastSeenTime = gta.getTimeAgo(time);


            if (type.equals("text")) {
                mTimeView.setText(lastSeenTime);
                mImageTimeView.setVisibility(View.INVISIBLE);
                mTimeView.setVisibility(View.VISIBLE);
            } else if (type.equals("image")) {
                mImageTimeView.setText(lastSeenTime);
                mImageTimeView.setVisibility(View.VISIBLE);
                mTimeView.setVisibility(View.INVISIBLE);
            }
        }

        public void setMessage(String text, String type) {
            if (type.equals("text")) {
                mTextView.setText(text);

            } else if (type.equals("image")) {
                Picasso.get().load(text).placeholder(R.drawable.image_load_anim)
                        .into(mImageView);
            }
        }

        public void setPosition(String Uid, String from) {
            if (Uid.equals(from)) {
                mMessageView.setGravity(Gravity.END);
                mImageTimeView.setBackgroundResource(R.drawable.message_bg_light);
                mImageLayout.setBackgroundResource(R.drawable.message_bg_light);
                mTextView.setTextColor(Color.WHITE);
                mImageTimeView.setTextColor(Color.DKGRAY);
                mTimeView.setTextColor(Color.DKGRAY);
            } else {
                mMessageView.setGravity(Gravity.START);
                mImageTimeView.setTextColor(Color.LTGRAY);
                mTimeView.setTextColor(Color.LTGRAY);
                mMessageLayout.setBackgroundResource(R.drawable.message_bg_dark);
                mImageLayout.setBackgroundResource(R.drawable.message_bg_dark);
                mTextView.setTextColor(Color.WHITE);
            }
        }
    }
}
