package com.uxmen.communityshovel;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpPage extends AppCompatActivity implements View.OnClickListener {
    private static String DEBUG = "DEBUG";

    private Button submitButton;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText bioEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(this);

        emailEditText = (EditText) findViewById(R.id.editTextEmailAddressSignUp);
        passwordEditText = (EditText) findViewById(R.id.editTextPasswordSignUp);
        confirmPasswordEditText = (EditText) findViewById(R.id.editTextConfirmPassword);
        firstNameEditText = (EditText) findViewById(R.id.editTextFirstName);
        lastNameEditText = (EditText) findViewById(R.id.editTextLastName);
        bioEditText = (EditText) findViewById(R.id.editTextBio);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submit_button) {
            Toast.makeText(this, "Submit", Toast.LENGTH_SHORT).show();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();
            String firstName = firstNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String bio = bioEditText.getText().toString();
            Log.d(DEBUG, "First Name: " + firstName + " Last Name: " + lastName);
            Log.d(DEBUG, "Email: " + email + " Password: " + password + " Confirm Password: "
                    + confirmPassword);
            Log.d(DEBUG, "Bio: " + bio);
            // TODO: Submit account creation request and navigate back to log in screen
        }
    }
}
