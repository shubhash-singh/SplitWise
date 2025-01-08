package com.ragnar.splitwise.FriendFragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ragnar.splitwise.Database.SessionManager;
import com.ragnar.splitwise.GroupFragment.User;
import com.ragnar.splitwise.GroupFragment.UserAdapter;
import com.ragnar.splitwise.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FriendsFragment extends Fragment {

    private FriendAdapter friendsAdapter;
    private List<Friends> friendsList;
    private EditText friendNameEditText;
    private FirebaseFirestore db;
    private String userId;
    private RecyclerView userSearchRecycleView;
    private UserAdapter userAdapter;
    private List<User> userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        SessionManager sessionManager = new SessionManager(getContext());
        userId = sessionManager.getUserDetail("userId");
        db = FirebaseFirestore.getInstance();
        userSearchRecycleView = view.findViewById(R.id.userSearchRecyclerView);
        RecyclerView friendsListRecyclerView = view.findViewById(R.id.friendListRecycleView);
        friendNameEditText = view.findViewById(R.id.friendNameEditText);
        TextView addFriendButton = view.findViewById(R.id.addFriendButton);

        friendsList = new ArrayList<>();
        friendsAdapter = new FriendAdapter(friendsList, getContext());

        friendsListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        friendsListRecyclerView.setAdapter(friendsAdapter);

        addFriendButton.setOnClickListener(v -> {
            String friendNumber = friendNameEditText.getText().toString().trim();
            friendNameEditText.setText("");
            if (!friendNumber.isEmpty()) {
                getFriendID(friendNumber);
            } else {
                Toast.makeText(getContext(), "Friend number cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, clickedUser -> {
            // Set clicked user's name to EditText
            friendNameEditText.setText(clickedUser.getName());
            userSearchRecycleView.setVisibility(View.GONE);
        });

        userSearchRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        userSearchRecycleView.setAdapter(userAdapter);
        // Fetch users (but keep RecyclerView hidden initially)
        fetchUsers();

        friendNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        fetchFriends();
        return view;
    }

    private void getFriendID(String friendPhoneNumber){
        db.collection("users")
                .whereEqualTo("phone", friendPhoneNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String friendId = document.getId();
                            Log.d("GetFriendID", "Friend Id found");
                            addFriend(friendId);
                        }
                    }
                    else {
                        Log.e("GetFriendID", "Task failed");
                    }
                })
                .addOnFailureListener(e->
                    Log.e("GetFriendID", Objects.requireNonNull(e.getMessage()))
                );
    }
    private void addFriend(String friendId) {
        db.collection("users").document(userId)
                .update("friends", FieldValue.arrayUnion(friendId))
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(getContext(), "Friend added successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to add friend", Toast.LENGTH_SHORT).show());

        db.collection("users")
                .document(friendId)
                .update("friends",FieldValue.arrayUnion(userId))
                .addOnCompleteListener(aVoid -> Toast.makeText(getContext(), "Friend added successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add friend", Toast.LENGTH_SHORT).show());
    }

    private void fetchFriends() {
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (!document.contains("friends")) {
                        db.collection("users")
                                .document(userId)
                                .update("friends", new ArrayList<>())
                                .addOnSuccessListener(aVoid -> {
                                    friendsList.clear();
                                    friendsAdapter.notifyDataSetChanged();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(getContext(), "Failed to create friends field", Toast.LENGTH_SHORT).show()
                                );
                    } else {
                        List<String> friendIds = (List<String>) document.get("friends");
                        if (friendIds != null) {
                            friendsList.clear();
                            for (String id : friendIds) {
                                fetchFriendDetails(id);
                            }
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to fetch friends", Toast.LENGTH_SHORT).show()
                );
    }

    private void fetchFriendDetails(String friendId) {
        db.collection("users")
                .document(friendId)
                .get()
                .addOnSuccessListener(friendDoc -> {
                    if (friendDoc.exists()) {
                        Friends friend = new Friends(
                                friendId,
                                friendDoc.getString("name"),
                                friendDoc.getString("email"),
                                friendDoc.getString("phone")
                        );
                        friendsList.add(friend);
                        friendsAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to fetch friend details", Toast.LENGTH_SHORT).show()
                );
    }

    // method to search user
    private void fetchUsers() {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            if (name != null) {
                                userList.add(new User(name));
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to fetch users", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterUsers(String query) {
        if (query.isEmpty()) {
            userSearchRecycleView.setVisibility(View.GONE);
            return;
        }

        List<User> filteredList = new ArrayList<>();
        for (User user : userList) {
            if (user.getPhoneNumber().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }

        if (!filteredList.isEmpty()) {
            userSearchRecycleView.setVisibility(View.VISIBLE);
            userAdapter.setFilteredList(filteredList);
        } else {
            userSearchRecycleView.setVisibility(View.GONE);
        }
    }
}
