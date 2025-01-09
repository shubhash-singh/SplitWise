package com.ragnar.splitwise.GroupFragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ragnar.splitwise.R;

import java.util.ArrayList;
import java.util.List;

public class GroupDetailActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String groupId;
    private TextView amountToBePaidTextView, amountPerPersonTextView;
    private List<String> membersList;
    private EditText addMemberInput, billAmountInput;
    private MembersAdapter membersAdapter;
    private RecyclerView userSearchRecycleView;
    private UserAdapter userAdapter;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        db = FirebaseFirestore.getInstance();

        // Setting the status bar to black
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));


        // Get group details passed via Intent
        groupId = getIntent().getStringExtra("group_id");
        String groupName = getIntent().getStringExtra("group_name");

        userSearchRecycleView = findViewById(R.id.userSearchRecyclerView);

        // Initialize views
        TextView groupNameTextView = findViewById(R.id.group_name_text);
        amountToBePaidTextView = findViewById(R.id.amount_to_be_paid_text);
        RecyclerView membersRecyclerView = findViewById(R.id.members_recycler_view);
        addMemberInput = findViewById(R.id.add_member_input);
        TextView addMemberButton = findViewById(R.id.add_member_button);
        TextView addBillButton = findViewById(R.id.add_bill_button);
        billAmountInput = findViewById(R.id.bill_amount_input);
        amountPerPersonTextView = findViewById(R.id.amount_per_person);

        TextView settleUpButton = findViewById(R.id.settle_up_button);
        settleUpButton.setOnClickListener(v -> showSettleUpDialog());

        groupNameTextView.setText(groupName);
        membersList = new ArrayList<>();
        membersAdapter = new MembersAdapter(membersList);
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        membersRecyclerView.setAdapter(membersAdapter);

        // Add member button logic
        addMemberButton.setOnClickListener(v -> {
            String newMemberPhone = addMemberInput.getText().toString().trim();
            addMemberInput.setText("");
            if (!newMemberPhone.isEmpty()) {
                // Search for the user by phone number in the "users" collection
                db.collection("users")
                        .whereEqualTo("phone", newMemberPhone)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult().isEmpty()) {
                                    Toast.makeText(GroupDetailActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Assuming there is only one user with the given phone number
                                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                    String userId = documentSnapshot.getId();  // This is the user's ID

                                    // Now add the user to the group by updating the group members
                                    addMember(userId);
                                }
                            } else {
                                Toast.makeText(GroupDetailActivity.this, "Failed to fetch user", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(GroupDetailActivity.this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            }
        });


        // Add bill button logic
        addBillButton.setOnClickListener(v -> {
            String billAmount = billAmountInput.getText().toString().trim();
            billAmountInput.setText("");
            if (!billAmount.isEmpty()) {
                addBill(Double.parseDouble(billAmount));
            }
        });
        // to search user
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, clickedUser -> {
            // Set clicked user's name to EditText
            addMemberInput.setText(clickedUser.getName());
            userSearchRecycleView.setVisibility(View.GONE);
        });

        userSearchRecycleView.setLayoutManager(new LinearLayoutManager(this));
        userSearchRecycleView.setAdapter(userAdapter);
        // Fetch users (but keep RecyclerView hidden initially)
        fetchUsers();

        addMemberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        // Fetch group members from Firestore
        fetchGroupDetails();
    }

    private void fetchGroupDetails() {
        membersList.clear();
        db.collection("groups").document(groupId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Group group = documentSnapshot.toObject(Group.class);
                        if (group != null) {
                            List<String> userIds = group.getMembers();
                            fetchMemberNames(userIds);

                            // Update the amount to be paid
                            double amount = group.getAmountToBePaid() != null ? group.getAmountToBePaid() : 0.0;
                            String amountWithPrecision = String.format("%.2f", amount);
                            String amountToBePaid = "Total Bill: " + amountWithPrecision;

                            amountToBePaidTextView.setText(amountToBePaid);
                            setAmountPerPerson(amount);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("GroupDetailActivity", "Error fetching group details", e);
                });
    }

    private void fetchMemberNames(List<String> userIds) {
        membersList.clear();
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        for (String userId : userIds) {
            tasks.add(db.collection("users").document(userId).get());
        }

        Tasks.whenAllSuccess(tasks)
                .addOnSuccessListener(result -> {
                    for (Object document : result) {
                        DocumentSnapshot snapshot = (DocumentSnapshot) document;
                        String name = snapshot.getString("name");
                        if (name != null) {
                            membersList.add(name);
                        }
                    }
                    membersAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("fetchMemberNames", "Error fetching user details", e));
    }


    private void addBill(double amount) {
        // Fetch the current amountToBePaid from Firestore
        db.collection("groups").document(groupId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Double currentAmount = documentSnapshot.getDouble("amountToBePaid");
                        double totalAmount = (currentAmount != null ? currentAmount : 0.0) + amount;
                        // Update the amountToBePaid in Firestore
                        updateAmountToBePaid(totalAmount);
                        // Split Bill to each person
//                        fetchGroupDetails();

                    }
                })
                .addOnFailureListener(e -> Toast.makeText(GroupDetailActivity.this, "Failed to fetch current amount", Toast.LENGTH_SHORT).show());
    }
    private void updateAmountToBePaid(double amountPerPerson) {
        db.collection("groups").document(groupId)
                .update("amountToBePaid", amountPerPerson)
                .addOnSuccessListener(aVoid -> Log.d("GroupDetailActivity", "Amount to be paid updated to " + amountPerPerson))
                .addOnFailureListener(e -> {
                    // Error updating the amount to be paid
                    Log.e("GroupDetailActivity", "Error updating amount to be paid", e);
                });
        fetchGroupDetails();
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
                        Toast.makeText(GroupDetailActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
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

    // add member after search
    private void addMember(String newMember) {
        db.collection("groups").document(groupId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> currentMembers = (List<String>) documentSnapshot.get("members");

                        if (currentMembers != null && currentMembers.contains(newMember)) {
                            Toast.makeText(this, "Member already exists in the group", Toast.LENGTH_SHORT).show();
                        } else {
                            // Add the new member to the group using arrayUnion
                            db.collection("groups").document(groupId)
                                    .update("members", FieldValue.arrayUnion(newMember))
                                    .addOnSuccessListener(aVoid -> {
                                        updateUserGroups(newMember, groupId);
                                        fetchGroupDetails();

                                        Toast.makeText(this, "Member added successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("AddMemberError", "Error adding member: " + e.getMessage());
                                        Toast.makeText(this, "Failed to add member", Toast.LENGTH_SHORT).show();
                                    });

                        }
                    } else {
                        Toast.makeText(this, "Group not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AddMemberError", "Error fetching group details: " + e.getMessage());
                    Toast.makeText(this, "Error fetching group details", Toast.LENGTH_SHORT).show();
                });
    }

    private void setAmountPerPerson(double totalAmount){
        db.collection("groups")
                .document(groupId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()){
                            List<String> members = (List<String>) document.get("members");
                            if (members != null) {
                                int memberCount = members.size();
                                double amountPerPersonDouble = totalAmount / memberCount;
                                String amountPerPersonString = "Bill per person: " + String.format("%.2f", amountPerPersonDouble);
                                amountPerPersonTextView.setText(amountPerPersonString);
                            }
                        }
                    }
                    else{
                        Log.e("GroupDetailActivity", "Error while getting member count", task.getException());
                    }
                })
                .addOnFailureListener(e -> {

                });
    }

    private void updateUserGroups(String userId, String groupId) {
        db.collection("users").document(userId)
                .update("groupIds", FieldValue.arrayUnion(groupId))
                .addOnSuccessListener(aVoid -> Log.d("UserList", "User list updated"))
                .addOnFailureListener(e ->Log.e("UserGroupList", "Failed to update user's group list"));
    }

    private void showSettleUpDialog() {
        // Create a dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_settle_up, null);
        builder.setView(dialogView);

        EditText payerInput = dialogView.findViewById(R.id.payer_input);
        EditText receiverInput = dialogView.findViewById(R.id.receiver_input);
        EditText amountInput = dialogView.findViewById(R.id.amount_input);
        TextView settleButton = dialogView.findViewById(R.id.settle_button);

        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        // Settle button logic
        settleButton.setOnClickListener(v -> {
            String payer = payerInput.getText().toString().trim();
            String receiver = receiverInput.getText().toString().trim();
            String amountText = amountInput.getText().toString().trim();

            if (!payer.isEmpty() && !receiver.isEmpty() && !amountText.isEmpty()) {
                double amount = Double.parseDouble(amountText);
                recordPayment(payer, receiver, amount);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void recordPayment(String payer, String receiver, double amount) {
        // Fetch the group's current balances
        db.collection("groups").document(groupId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Group group = documentSnapshot.toObject(Group.class);
                        if (group != null) {
                            List<String> members = group.getMembers();

                            if (members.contains(payer) && members.contains(receiver)) {
                                // Update balances in Firestore
                                String key = payer + "_to_" + receiver; // Key format: "payer_to_receiver"

                                db.collection("groups").document(groupId)
                                        .update(key, FieldValue.increment(-amount))
                                        .addOnSuccessListener(aVoid -> {
                                            fetchGroupDetails(); // Update UI
                                            Toast.makeText(this, "Payment recorded successfully", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("SettleUp", "Error updating balances", e);
                                            Toast.makeText(this, "Failed to record payment", Toast.LENGTH_SHORT).show();
                                        });

                            } else {
                                Toast.makeText(this, "Payer or Receiver not in group", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("SettleUp", "Error fetching group details", e);
                    Toast.makeText(this, "Failed to fetch group details", Toast.LENGTH_SHORT).show();
                });
    }


}
