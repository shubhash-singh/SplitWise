package com.ragnar.splitwise.GroupFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private final OnUserClickListener clickListener;

    public interface OnUserClickListener {
        void onUserClick(User clickedUser);
    }

    public UserAdapter(List<User> userList, OnUserClickListener clickListener) {
        this.userList = userList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameTextView.setText(user.getPhoneNumber());
        holder.nameTextView.setTextColor(android.graphics.Color.BLACK);

        // Handle item click
        holder.itemView.setOnClickListener(v -> clickListener.onUserClick(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setFilteredList(List<User> filteredList) {
        this.userList = filteredList;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(android.R.id.text1);
        }
    }
}
