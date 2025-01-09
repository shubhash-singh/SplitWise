package com.ragnar.splitwise.GroupFragment;

import android.app.AlertDialog;
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


        // Show options dialog on long press
        groupAdapter = new GroupAdapter(groupList, getContext(), this::showCustomOptionsDialog);
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
        List<String> balances = new ArrayList<>();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        members.add(userId);
        // Default values for totalAmount and amountToBePaid
        Double amountToBePaid = 0.0;  // Set amount to be paid  0 initially
        Group group = new Group(groupId, groupName, members, amountToBePaid, balances);

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
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("groups")
                .whereArrayContains("members", currentUserId) // Filter groups where current user is a member
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    groupList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Group group = document.toObject(Group.class);
                        groupList.add(group);
                    }
                    groupAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to fetch groups", Toast.LENGTH_SHORT).show()
                );
    }

    private void updateUserGroups(String userId, String groupId) {
        db.collection("users").document(userId)
                .update("groupIds", FieldValue.arrayUnion(groupId))
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(getContext(), "User's group list updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to update user's group list", Toast.LENGTH_SHORT).show());
    }



    private void showCustomOptionsDialog(Group group, int position) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_group_long_press, null);

        // Create the dialog
        AlertDialog customDialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        // Find views in the custom layout
        TextView updateGroup = dialogView.findViewById(R.id.update_group);
        TextView deleteGroup = dialogView.findViewById(R.id.delete_group);

        // Set click listeners for options
        updateGroup.setOnClickListener(v -> {
            customDialog.dismiss();
            showUpdateGroupDialog(group, position); // Your method to update the group name
        });

        deleteGroup.setOnClickListener(v -> {
            customDialog.dismiss();
            deleteGroup(group, position); // Your method to delete the group
        });

        // Show the dialog
        customDialog.show();
    }

    private void showUpdateGroupDialog(Group group, int position) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_rename_group, null);

        // Create the dialog
        AlertDialog customDialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        EditText inputEditText = dialogView.findViewById(R.id.groupNameEditText);
        inputEditText.setText(group.getName());
        TextView cancelButton = dialogView.findViewById(R.id.cancelButton);
        TextView updateButton = dialogView.findViewById(R.id.updateButton);

        updateButton.setOnClickListener(v -> {
            String newGroupName = inputEditText.getText().toString().trim();
            if (!newGroupName.isEmpty()) {
                updateGroupName(group, newGroupName, position);
            } else {
                Toast.makeText(getContext(), "Group name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> customDialog.dismiss());
    }

    private void updateGroupName(Group group, String newGroupName, int position) {
        db.collection("groups").document(group.getId())
                .update("name", newGroupName)
                .addOnSuccessListener(aVoid -> {
                    group.setName(newGroupName);
                    groupAdapter.notifyItemChanged(position);
                    Toast.makeText(getContext(), "Group name updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update group name", Toast.LENGTH_SHORT).show());
    }

    private void deleteGroup(Group group, int position) {
        // Fetch members from the group document
        db.collection("groups").document(group.getId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> members = (List<String>) documentSnapshot.get("members");

                        if (members != null) {
                            // Iterate through the members and remove group ID from their groupIds array
                            for (String userId : members) {
                                db.collection("users").document(userId)
                                        .update("groupIds", FieldValue.arrayRemove(group.getId()))
                                        .addOnFailureListener(e ->
                                                Toast.makeText(getContext(), "Failed to update user: " + userId, Toast.LENGTH_SHORT).show()
                                        );
                            }
                        }

                        // Delete the group document
                        db.collection("groups")
                                .document(group.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    groupList.remove(position);
                                    groupAdapter.notifyItemRemoved(position);
                                    Toast.makeText(getContext(), "Group deleted", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(getContext(), "Failed to delete group", Toast.LENGTH_SHORT).show()
                                );
                    } else {
                        Toast.makeText(getContext(), "Group not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to fetch group members", Toast.LENGTH_SHORT).show()
                );
    }

}