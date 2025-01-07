package com.ragnar.splitwise;

import android.annotation.SuppressLint;
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
import com.ragnar.splitwise.FriendFragment.FriendsFragment;
import com.ragnar.splitwise.GroupFragment.GroupsFragment;
import com.ragnar.splitwise.ProfileFragments.ProfileFragment;

import org.w3c.dom.Text;

public class LoadFragments extends AppCompatActivity {
    ImageButton addFriendButton;

    BottomNavigationView nav_bar;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_fragments);

        // Setting the status bar to black
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));


        nav_bar = findViewById(R.id.bottom_navigation);
        TextView fragmentNameTextView = findViewById(R.id.fragmentName);
        fragmentNameTextView.setText("Groups");
        loadFragment(new GroupsFragment());
        nav_bar.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if(id == R.id.nav_groups){
                loadFragment(new GroupsFragment());
                fragmentNameTextView.setText("Groups");
            }
            else if(id == R.id.nav_friends){
                loadFragment(new com.ragnar.splitwise.FriendFragment.FriendsFragment());
                fragmentNameTextView.setText("Friends");
            }
            else if (id == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
                fragmentNameTextView.setText("Profile");
            }
            return true;
        });

    }



    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,fragment).commit();

    }
}