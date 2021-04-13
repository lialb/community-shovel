package com.uxmen.communityshovel;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignInPage extends AppCompatActivity implements View.OnClickListener {
    private static String DEBUG = "DEBUG";

    private Button signInButton;
    private TextView signUpButton;

    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        signInButton = (Button) findViewById(R.id.signin_button);
        signUpButton = (TextView) findViewById(R.id.signup_button);
        emailEditText = (EditText) findViewById(R.id.editTextEmailAddressSignIn);
        passwordEditText = (EditText) findViewById(R.id.editTextPasswordSignIn);

        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signin_button) {
            Toast.makeText(this, "Sign In", Toast.LENGTH_SHORT).show();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            Log.d(DEBUG, "Email: " + email + " Password: " + password);
            // TODO: hash password and send login request
        } else if (v.getId() == R.id.signup_button) {
            switchActivity(SignUpPage.class);
        }
    }

    public void switchActivity(final Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }
}
