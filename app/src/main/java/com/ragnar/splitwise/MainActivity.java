package com.ragnar.splitwise;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.ragnar.splitwise.Database.SessionManager;

public class MainActivity extends AppCompatActivity {
    TextView signup, login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        if(sessionManager.isLoggedIn()){
            Intent intent = new Intent(MainActivity.this, LoadFragments.class);
            startActivity(intent);
        }
        signup = findViewById(R.id.signUpButton);
        login = findViewById(R.id.loginButton);

        signup.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SignUpPage.class);
            startActivity(intent);
        });

        login.setOnClickListener(view ->{
            Intent intent = new Intent(MainActivity.this, LoginPage.class);
            startActivity(intent);
            finish();
        });


    }
}