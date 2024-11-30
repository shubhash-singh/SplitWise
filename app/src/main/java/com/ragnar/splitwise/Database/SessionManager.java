package com.ragnar.splitwise.Database;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class SessionManager {


    private static final String PREF_NAME = "UserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_NAME = "name";
    private static final String KEY_USERID = "userId";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_GENDER = "gender";


    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // Save user data on login
    public void createLoginSession(String name, String userId, String email, String phone, String gender) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_USERID, userId);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_GENDER, gender);

        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Get stored user data
    public String getUserDetail(String key) {
        return pref.getString(key, "Error");
    }

    // Clear user data on logout
    public void logoutUser() {
        editor.clear();
        editor.commit();
    }

    public void getDataFromServer(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null) {
            DocumentReference documentReference = db.collection("users").document(user.getUid());

            documentReference.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot != null && documentSnapshot.exists()){
                        String name = documentSnapshot.getString("name");
                        String userId = documentSnapshot.getString("userId");
                        String phoneNo = documentSnapshot.getString("phone");
                        String email = documentSnapshot.getString("email");
                        String gender = documentSnapshot.getString("gender");


                        createLoginSession(name, userId, email, phoneNo, gender);
                        Log.e("Task if", getUserDetail("KEY_NAME"));
                    } else {
                        Log.e("Task else", "Document does not exist.");
                    }
                } else {
                    // Handle task failure
                    System.out.println("Task failed with exception: " + task.getException());
                    Log.e("TASK on Sucess", task.getException().getMessage());
                }
            }).addOnFailureListener(e -> {
                Log.e("TASK on Fail", e.getMessage());
            });
        } else {
            System.out.println("No authenticated user.");
            Log.e("Task", "No authenticated user.");
        }
    }
}