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
            signIn(v);
        } else if (v.getId() == R.id.signup_button) {
            Intent intent = new Intent(this, SignUpPage.class);
            startActivity(intent);
        }
    }

    /**
     * signIn sends a login request to the server based on the email and password editTexts
     */
    private void signIn(View v) {
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
        }

        Log.d(DEBUG, request.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, request, response -> {
                    Log.d(DEBUG, response.toString());
                    lookupUserAndGo(email);
                }, error -> {
                    if(error.networkResponse != null) {
                        Log.e("Error code", String.valueOf(error.networkResponse.statusCode));
                    }
                    Toast.makeText(v.getContext(), "Invalid Login", Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * lookupUserAndGo is a janky function to query /get-user, populate a parcelable User object
     * and finally switch to the Main Activity (map). It is janky because all of the logic for this
     * is performed inside of a callback method for what I think is an asynchronous request...
     * because I could not figure out how to get this to work blocking.
     * @param email the identifying user email (ex: greg4@johnson.com)
     */
    private void lookupUserAndGo(String email) {
        String finalEmail = email.replace('.', ',');
        String url ="http://10.0.2.2:5000/get-user/" + finalEmail;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(DEBUG, response.toString());

                            String firstName = response.getString("first_name");
                            String lastName = response.getString("last_name");
                            String bio = response.getString("bio");
                            int distanceShoveled = response.getInt("distance_shoveled");
                            int peopleImpacted = response.getInt("people_impacted");
                            User u = new User(finalEmail, firstName, lastName, bio,
                                    distanceShoveled, peopleImpacted);
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            intent.putExtra("active_user", u);
                            startActivity(intent);

                        } catch (JSONException e) {
                            Log.e("JSON Exception", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error code", String.valueOf(error.networkResponse.statusCode));
                    }
                });

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

}
