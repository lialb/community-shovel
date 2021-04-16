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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

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
            boolean success = signIn();
        } else if (v.getId() == R.id.signup_button) {
            switchActivity(SignUpPage.class);
        }
    }

    /**
     * signIn sends a login request to the server based on the email and password editTexts
     * @return
     */
    private boolean signIn() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        Log.d(DEBUG, "Email: " + email + " Password: " + password);

        String url ="http://10.0.2.2:5000/login";

        JSONObject request = new JSONObject();
        try{
            request.put("email", email);
            request.put("password", password);
        }catch(JSONException e){
            Log.e("JSONObject Error", e.getMessage());
            return false;
        }

        Log.d(DEBUG, request.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d(DEBUG, response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.getMessage());
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        return true;
    }

    public void switchActivity(final Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }
}
