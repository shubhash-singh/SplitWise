package com.ragnar.splitwise;

import android.app.Dialog;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ragnar.splitwise.Fragments.FriendsFragment;
import com.ragnar.splitwise.GroupFragment.GroupsFragment;
import com.ragnar.splitwise.Fragments.ProfileFragment;

public class LoadFragments extends AppCompatActivity {
    ImageButton addFriendButton;

    BottomNavigationView nav_bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_fragments);

        // Setting the status bar to black
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.background_color));

        addFriendButton = findViewById(R.id.addFriendButton);
        nav_bar = findViewById(R.id.bottom_navigation);

        nav_bar.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if(id == R.id.nav_groups){
                loadFragment(new GroupsFragment());
            }
            else if(id == R.id.nav_friends){
                loadFragment(new FriendsFragment());
            }
            else if (id == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
            }
            return true;
        });

        String selectedItem = String.valueOf(nav_bar.getSelectedItemId());
    }

    private void setAddFriendDialog(){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_add_friend);
        EditText phoneNumberEditText = dialog.findViewById(R.id.phoneNumberEditText);
        TextView addButton = dialog.findViewById(R.id.addButton);

        // Set the dialog width to be wider
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        addButton.setOnClickListener(v -> {
            String phoneNumber = phoneNumberEditText.getText().toString().trim();
            if (!phoneNumber.isEmpty()) {




                dialog.dismiss();
            } else {
                phoneNumberEditText.setError("Phone number cannot be empty!");
            }
        });
        dialog.show();
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,fragment).commit();

    }
}