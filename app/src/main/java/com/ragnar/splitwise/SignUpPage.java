package com.ragnar.splitwise;

import android.content.Intent;
import android.os.Bundle;
import android.se.omapi.Session;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ragnar.splitwise.Callbacks.Callback;
import com.ragnar.splitwise.Callbacks.SignUpCallback;
import com.ragnar.splitwise.Database.InitializeDatabase;
import com.ragnar.splitwise.Database.SessionManager;

import java.util.Map;

public class SignUpPage extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText, nameEditText, phoneEditText;
    Button signUpButton;
    RadioGroup genderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up_page);

        // Setting the status bar to black
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.background_color));

        // initialize the Database
        InitializeDatabase db = new InitializeDatabase();
        SessionManager session = new SessionManager(getApplicationContext());



        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        nameEditText = findViewById(R.id.namEditText);
        phoneEditText = findViewById(R.id.phoneNumberEditText);
        genderButton = findViewById(R.id.selectGender);

        signUpButton = findViewById(R.id.buttonSignUp);

        signUpButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            String name = nameEditText.getText().toString().trim();
            String phoneNumber = phoneEditText.getText().toString().trim();

            int selectedGenderId = genderButton.getCheckedRadioButtonId();
            String gender;

            if (selectedGenderId != -1) {
                RadioButton selectedGenderButton = findViewById(selectedGenderId);
                gender = selectedGenderButton.getText().toString();
            } else {
                gender = "Not Selected";
            }
            if (validateInputs(email, password, confirmPassword, name, phoneNumber, gender)) {
                db.performSignUp(email, password, name, phoneNumber, gender, new SignUpCallback() {
                    @Override
                    public void onSuccess(Map<String, String> userData) {

                        session.createLoginSession(userData.get("name"), userData.get("userId"), userData.get("email"), userData.get("phone"), userData.get("gender"));
                        Toast.makeText(SignUpPage.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(SignUpPage.this, LoginPage.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(SignUpPage.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private boolean validateInputs(String email, String password, String confirmPassword, String name, String phoneNumber, String gender) {
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
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return false;
        }
        if (TextUtils.isEmpty(name)){
            nameEditText.setError("Name is required");
            return false;
        }
        if(TextUtils.isEmpty(phoneNumber)){
            phoneEditText.setError("Phone number is required");
            return false;
        }
        if(phoneNumber.length() != 10){
            phoneEditText.setError("Phone number must be 10 digit");
            return false;
        }
        if(gender.equals("Not Selected")){
            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}