package com.ragnar.splitwise.FriendFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ragnar.splitwise.R;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private List<Friends> friendsList;
    private Context context;
    public FriendAdapter(List<Friends> friendsList, Context context){
        this.friendsList = friendsList;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position){
        Friends friends = friendsList.get(position);
        holder.friendTextView.setText(friends.getName());
        holder.emailEditText.setText(friends.getEmail());
        holder.phoneNumberEditText.setText(friends.getPhoneNumber());

    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView friendTextView, emailEditText, phoneNumberEditText;
        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }
}
