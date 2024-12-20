package com.ragnar.splitwise.GroupFragment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ragnar.splitwise.R;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> groupList;
    private Context context;

    public GroupAdapter(List<Group> groupList, Context context) {
        this.groupList = groupList;
        this.context = context;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.groupNameTextView.setText(group.getName());
        // Set the amount to be paid below group name if it's available
        String amountToBePaid = group.getAmountToBePaid() != null ?
                "Amount to be paid: " + group.getAmountToBePaid() : "";
        holder.amountToBePaidTextView.setText(amountToBePaid);

        // Handle group click to show group details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, GroupDetailActivity.class);
            intent.putExtra("group_id", group.getId());
            intent.putExtra("group_name", group.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupNameTextView, amountToBePaidTextView;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupNameTextView = itemView.findViewById(R.id.group_name_text);
            amountToBePaidTextView = itemView.findViewById(R.id.amount_to_be_paid_text);
        }
    }
}
