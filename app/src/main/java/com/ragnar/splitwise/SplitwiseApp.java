package com.ragnar.splitwise;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class SplitwiseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
    }
}
