package com.ragnar.splitwise.Database;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ragnar.splitwise.Callbacks.Callback;
import com.ragnar.splitwise.Callbacks.SignUpCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InitializeDatabase {

    private FirebaseAuth userAuth;
    private FirebaseFirestore db;
    FirebaseUser user;

    public InitializeDatabase() {
        userAuth = FirebaseAuth.getInstance();
        user = userAuth.getCurrentUser();
    }

    public void performLogin(String email, String password, Callback callback){

        userAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                callback.onSuccess("Login Successful");
            }else {
                Exception exception = task.getException();
                if (exception instanceof FirebaseAuthInvalidUserException) {
                    // User does not exist
                    callback.onFailure("User does not exist. Please sign up.");
                } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                    // Incorrect password
                    callback.onFailure("Incorrect password. Please try again.");
                } else {
                    // Other errors
                    assert exception != null;
                    callback.onFailure("Login failed: " + exception.getMessage());
                }
            }

        });
    }

    public void performSignUp(String email, String password, String name, String phoneNumber, String gender, SignUpCallback callback) {
        // Create user with email and password
        userAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get the current user
                        FirebaseUser user = userAuth.getCurrentUser();

                        if (user != null) {
                            // Prepare user data
                            Map<String, String> userData = new HashMap<>();
                            userData.put("name", name);
                            userData.put("userId", user.getUid());
                            userData.put("email", email);
                            userData.put("phone", phoneNumber);
                            userData.put("gender", gender);



                            // Save user data to Firestore with UID as document ID
                            db = FirebaseFirestore.getInstance();
                            db.collection("users")
                                    .document(user.getUid())
                                    .set(userData)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            // Callback on success
                                            callback.onSuccess(userData);
                                        } else {
                                            // Callback on failure during Firestore write
                                            callback.onFailure(task1.getException() != null ? task1.getException().getMessage() : "Failed to save user data to Firestore");
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        // Callback on Firestore failure
                                        callback.onFailure(e.getMessage());
                                    });
                        } else {
                            // If user is null after successful creation
                            callback.onFailure("Failed to retrieve user after sign up");
                        }
                    } else {
                        // Callback on failure during authentication
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Sign Up Failed");
                    }
                });
    }

}
