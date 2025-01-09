package com.ragnar.splitwise.GroupFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ragnar.splitwise.R;

import java.util.List;

public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.BalanceViewHolder> {
    private List<String> balanceList;

    public BalanceAdapter(List<String> balanceList) {
        this.balanceList = balanceList;
    }

    @NonNull
    @Override
    public BalanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_balance, parent, false);
        return new BalanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BalanceViewHolder holder, int position) {
        String balanceText = balanceList.get(position);
        holder.balanceText.setText(balanceText);
    }

    @Override
    public int getItemCount() {
        return balanceList.size();
    }

    static class BalanceViewHolder extends RecyclerView.ViewHolder {
        TextView balanceText;

        public BalanceViewHolder(@NonNull View itemView) {
            super(itemView);
            balanceText = itemView.findViewById(R.id.balance_text);
        }
    }
}
