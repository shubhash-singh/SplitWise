package com.ragnar.splitwise.GroupFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ragnar.splitwise.R;

import java.util.ArrayList;
import java.util.List;


public class GroupsFragment extends Fragment {
    private FirebaseFirestore db;
    private List<Group> groupList;
    private GroupAdapter groupAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        EditText groupNameInput = view.findViewById(R.id.group_name_input);
        TextView addGroupButton = view.findViewById(R.id.add_group_button);
        RecyclerView groupsRecyclerView = view.findViewById(R.id.groups_recycler_view);
        db = FirebaseFirestore.getInstance();

        groupList = new ArrayList<>();
        groupAdapter = new GroupAdapter(groupList, getContext());
        groupsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        groupsRecyclerView.setAdapter(groupAdapter);

        // Add group button click listener
        addGroupButton.setOnClickListener(v -> {
            String groupName = groupNameInput.getText().toString().trim();
            groupNameInput.setText("");
            if (!groupName.isEmpty()) {
                addGroup(groupName);
            } else {
                Toast.makeText(getContext(), "Group name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch groups from Firestore
        fetchGroups();

        return view;
    }

    private void addGroup(String groupName) {
        String groupId = db.collection("groups").document().getId();
        List<String> members = new ArrayList<>();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        members.add(userId);
        // Default values for totalAmount and amountToBePaid
        Double totalAmount = 0.0;  // Set total amount to 0 initially
        Double amountToBePaid = 0.0;  // Set amount to be paid by each member to 0 initially
        Group group = new Group(groupId, groupName, members, totalAmount, amountToBePaid);

        db.collection("groups").document(groupId)
                .set(group)
                .addOnSuccessListener(aVoid -> {
                    groupList.add(group);
                    updateUserGroups(userId, groupId);
                    groupAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Group added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add group", Toast.LENGTH_SHORT).show());
    }

    private void fetchGroups() {
        db.collection("groups")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    groupList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Group group = document.toObject(Group.class);
                        groupList.add(group);
                    }
                    groupAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to fetch groups", Toast.LENGTH_SHORT).show());
    }
    private void updateUserGroups(String userId, String groupId) {
        db.collection("users").document(userId)
                .update("groupIds", FieldValue.arrayUnion(groupId))
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(getContext(), "User's group list updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to update user's group list", Toast.LENGTH_SHORT).show());
    }
}