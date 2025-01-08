package com.ragnar.splitwise.FriendFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ragnar.splitwise.R;
import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private List<Friends> friendsList;
    private Context context;

    public FriendAdapter(List<Friends> friendsList, Context context) {
        this.friendsList = friendsList;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friends friend = friendsList.get(position);
        holder.friendNameTextView.setText(friend.getName());
        holder.friendEmailTextView.setText(friend.getEmail());
        holder.friendPhoneNumberTextView.setText(friend.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView friendNameTextView, friendEmailTextView, friendPhoneNumberTextView;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            friendNameTextView = itemView.findViewById(R.id.friendTextView);
            friendEmailTextView = itemView.findViewById(R.id.emailEditText);
            friendPhoneNumberTextView = itemView.findViewById(R.id.phoneNumberTextView);
        }
    }
}
