package com.ragnar.splitwise;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ragnar.splitwise.Callbacks.Callback;
import com.ragnar.splitwise.Database.InitializeDatabase;
import com.ragnar.splitwise.Database.SessionManager;

public class LoginPage extends AppCompatActivity {
    EditText emailEditText, passwordEditText;
    Button loginButton,sendOTPButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);


        // Setting the status bar to black
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.background_color));



        // initialize the Database
        InitializeDatabase db = new InitializeDatabase();
        SessionManager session = new SessionManager(getApplicationContext());

        if(session.isLoggedIn()){
            Intent intent = new Intent(LoginPage.this, LoadFragments.class);
            startActivity(intent);
            finish();
        }

        loginButton = findViewById(R.id.loginButton);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);




        loginButton.setOnClickListener(i -> {

            String emailString = emailEditText.getText().toString().trim();
            String passwordString = passwordEditText.getText().toString().trim();

            if (validateInputs(emailString, passwordString)){
                db.performLogin(emailString, passwordString, new Callback() {
                    @Override
                    public void onSuccess(String message) {
                        session.getDataFromServer();
                        Intent intent = new Intent(LoginPage.this, LoadFragments.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(LoginPage.this, message, Toast.LENGTH_LONG).show();
                        emailEditText.setText("");
                        passwordEditText.setText("");
                    }
                });
            }
        });
    }

    private boolean validateInputs(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            return false;
        }
        return true;
    }
}