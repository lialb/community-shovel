package com.uxmen.communityshovel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpPage extends AppCompatActivity implements View.OnClickListener {
    private static final String DEBUG = "DEBUG";

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
            boolean success = signUp(v);
            if (success) {
                Intent intent = new Intent(this, SignInPage.class);
                startActivity(intent);
            }
        }
    }

    /**
     * signUp sends a create-account request to the server based on what is
     * currently entered in the editText fields.
     * @return true if account creation is successful; false otherwise
     */
    private boolean signUp(View v) {
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

        if (!password.equals(confirmPassword)) {
            // passwords do not match, do not continue trying to create account
            Toast.makeText(this, "Password Mismatch", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password too short", Toast.LENGTH_SHORT).show();
            return false;
        }

        String url ="http://10.0.2.2:5000/create-account";

        JSONObject request = new JSONObject();
        try{
            request.put("firstName", firstName);
            request.put("lastName", lastName);
            request.put("password", password);
            request.put("email", email);
            request.put("bio", bio);
        }catch(JSONException e){
            Log.e("JSONObject Error", e.getMessage());
            return false;
        }

        Log.d(DEBUG, request.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(DEBUG, response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            Log.e("Error code", String.valueOf(error.networkResponse.statusCode));
                        }
                        Toast.makeText(v.getContext(), "Invalid Email/Pass", Toast.LENGTH_SHORT).show();
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        return true;
    }

}
