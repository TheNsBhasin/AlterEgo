package com.nsbhasin.alterego.database;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {

    String mToken;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onNewToken(String token) {
        mToken = token;
        super.onNewToken(token);
    }
}
