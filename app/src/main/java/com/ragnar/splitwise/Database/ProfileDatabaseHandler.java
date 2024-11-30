package com.ragnar.splitwise.Database;

import com.google.firebase.auth.FirebaseAuth;
import com.ragnar.splitwise.Callbacks.Callback;

public class ProfileDatabaseHandler {
    FirebaseAuth userAuth;
    public ProfileDatabaseHandler(){
        userAuth = FirebaseAuth.getInstance();
    }

    public void resetPassword(String emailAddress, Callback callback){
        userAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                         String message = "Reset email sent. Check your inbox.";
                         callback.onSuccess(message);
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Failed to send reset email.";
                        callback.onSuccess(errorMessage);
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}
