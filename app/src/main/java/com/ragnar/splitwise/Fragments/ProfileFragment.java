package com.ragnar.splitwise.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ragnar.splitwise.Callbacks.Callback;
import com.ragnar.splitwise.Database.ProfileDatabaseHandler;
import com.ragnar.splitwise.Database.SessionManager;
import com.ragnar.splitwise.LoginPage;
import com.ragnar.splitwise.MainActivity;
import com.ragnar.splitwise.R;


public class ProfileFragment extends Fragment {
    SessionManager sessionManager;
    LinearLayout logoutButton, resetPasswordButton;
    TextView nameTextView, emailTextView, phoneTextView;
    private ProfileDatabaseHandler db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        db = new ProfileDatabaseHandler();

        sessionManager = new SessionManager(getActivity());


        nameTextView = view.findViewById(R.id.profile_name);
        emailTextView = view.findViewById(R.id.profile_email);
        phoneTextView = view.findViewById(R.id.profile_phone);

        resetPasswordButton = view.findViewById(R.id.passwordSettingButton);
        logoutButton = view.findViewById(R.id.logoutButton);

        setProfileDetail();
        resetPasswordButton.setOnClickListener(item -> resetPassword());
        logoutButton.setOnClickListener(item -> showLogoutConfirmationDialog());
        return view;
    }

    public void setProfileDetail(){
        nameTextView.setText(sessionManager.getUserDetail("name"));
        emailTextView.setText(sessionManager.getUserDetail("email"));
        phoneTextView.setText(sessionManager.getUserDetail("phone"));

    }

    private void resetPassword() {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_reset_password);

        // Set the dialog width to be wider
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        EditText emailEditText = dialog.findViewById(R.id.emailEditText);
        TextView sendButton = dialog.findViewById(R.id.sendButton);

        sendButton.setOnClickListener(v -> {
            String emailAddress = emailEditText.getText().toString().trim();
            if (!emailAddress.isEmpty()) {
                db.resetPassword(emailAddress, new Callback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        dialog.dismiss(); // Dismiss only on success
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                emailEditText.setError("Email address cannot be empty!");
            }
        });
        dialog.show();
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to log out?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            sessionManager.logoutUser();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            // Dismiss the dialog on negative button click
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}