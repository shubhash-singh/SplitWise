package com.ragnar.splitwise.GroupFragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ragnar.splitwise.R;

import java.util.ArrayList;
import java.util.List;

public class GroupDetailActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String groupId;
    private TextView groupNameTextView, amountToBePaidTextView;
    private RecyclerView membersRecyclerView;
    private List<String> membersList;
    private EditText addMemberInput, billAmountInput;
    private Button addMemberButton, addBillButton;
    private MembersAdapter membersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        db = FirebaseFirestore.getInstance();

        // Get group details passed via Intent
        groupId = getIntent().getStringExtra("group_id");
        String groupName = getIntent().getStringExtra("group_name");

        // Initialize views
        groupNameTextView = findViewById(R.id.group_name_text);
        amountToBePaidTextView = findViewById(R.id.amount_to_be_paid_text);
        membersRecyclerView = findViewById(R.id.members_recycler_view);
        addMemberInput = findViewById(R.id.add_member_input);
        addMemberButton = findViewById(R.id.add_member_button);
        addBillButton = findViewById(R.id.add_bill_button);
        billAmountInput = findViewById(R.id.bill_amount_input);

        groupNameTextView.setText(groupName);
        membersList = new ArrayList<>();
        membersAdapter = new MembersAdapter(membersList);
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        membersRecyclerView.setAdapter(membersAdapter);

        // Add member button logic
        addMemberButton.setOnClickListener(v -> {
            String newMember = addMemberInput.getText().toString().trim();
            if (!newMember.isEmpty()) {
                addMember(newMember);
            }
        });

        // Add bill button logic
        addBillButton.setOnClickListener(v -> {
            String billAmount = billAmountInput.getText().toString().trim();
            if (!billAmount.isEmpty()) {
                addBill(Double.parseDouble(billAmount));
            }
        });

        // Fetch group members from Firestore
        fetchGroupDetails();
    }

    private void fetchGroupDetails() {
        db.collection("groups").document(groupId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Group group = documentSnapshot.toObject(Group.class);
                        if (group != null) {
                            membersList.clear();
                            membersList.addAll(group.getMembers());
                            membersAdapter.notifyDataSetChanged();

                            // Update the amount to be paid
                            double amount = group.getAmountToBePaid() != null ? group.getAmountToBePaid() : 0.0;
                            amountToBePaidTextView.setText("Amount to be paid: " + amount);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error if fetching group details fails
                    Log.e("GroupDetailActivity", "Error fetching group details", e);
                });
    }


    private void addMember(String member) {
        db.collection("groups").document(groupId)
                .update("members", FieldValue.arrayUnion(member))
                .addOnSuccessListener(aVoid -> {
                    membersList.add(member);
                    membersAdapter.notifyDataSetChanged();
                    Toast.makeText(GroupDetailActivity.this, "Member added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(GroupDetailActivity.this, "Failed to add member", Toast.LENGTH_SHORT).show());
    }

    private void addBill(double amount) {
        // Update the total amount for the group
        db.collection("groups").document(groupId)
                .update("totalAmount", FieldValue.increment(amount))
                .addOnSuccessListener(aVoid -> {
                    // Calculate the amount per person (split the bill equally)
                    int numMembers = membersList.size();
                    double amountPerPerson = amount / numMembers;

                    // Update the amount to be paid by each member
                    updateAmountToBePaid(amountPerPerson);

                    // Notify the user that the bill was added
                    Toast.makeText(GroupDetailActivity.this, "Bill added and split equally", Toast.LENGTH_SHORT).show();

                    // Refresh the group details
                    fetchGroupDetails();  // Refresh group details to reflect updated amount to be paid
                })
                .addOnFailureListener(e -> Toast.makeText(GroupDetailActivity.this, "Failed to add bill", Toast.LENGTH_SHORT).show());
    }

    private void updateAmountToBePaid(double amountPerPerson) {
        db.collection("groups").document(groupId)
                .update("amountToBePaid", amountPerPerson)
                .addOnSuccessListener(aVoid -> {
                    // Amount to be paid updated successfully
                    Log.d("GroupDetailActivity", "Amount to be paid updated to " + amountPerPerson);
                })
                .addOnFailureListener(e -> {
                    // Error updating the amount to be paid
                    Log.e("GroupDetailActivity", "Error updating amount to be paid", e);
                });
    }

}
