package com.ragnar.splitwise.FriendFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ragnar.splitwise.R;

public class FriendsFragment extends Fragment {

    FirebaseFirestore db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);



        return view;
    }

    private void addFriend(String userId, String friendId) {
        db.collection("users").document(userId)
                .update("friendIds", FieldValue.arrayUnion(friendId))
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(getContext(), "Friend added successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to add friend", Toast.LENGTH_SHORT).show());
    }

}