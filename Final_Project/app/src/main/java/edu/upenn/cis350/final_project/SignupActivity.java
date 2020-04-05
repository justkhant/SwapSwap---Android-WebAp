package edu.upenn.cis350.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.io.UnsupportedEncodingException;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    static final int IMAGE_PICK_ID = 500;
    static final int REGISTER_ID = 501;
    static final int LOGIN_ACTIVITY_ID = 2;

    SignupActivity activity = this;
    ImageView picView;
    Button profPic;

    EditText name;
    EditText school;
    EditText email;
    EditText password;

    Button signup;

    Uri picUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Set up views
        picView = findViewById(R.id.viewPicture);
        name = findViewById(R.id.inputName);

        school = findViewById(R.id.inputSchool);
        email = findViewById(R.id.inputEmail);
        password = findViewById(R.id.inputPassword);
        profPic = findViewById(R.id.inputPic);
        signup = findViewById(R.id.inputSignup);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode,resultCode, intent);
        switch(requestCode) {
            case IMAGE_PICK_ID:
                if (resultCode == RESULT_OK) {
                    picUri = intent.getData();
                    picView.setImageURI(picUri);
                }
        }
    }

    public void onProfPicButtonClick(View v) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, IMAGE_PICK_ID);
    }

    public void onSignupButtonClick(View v) throws IOException {
        boolean valid = validateData();
        if (valid) {
            registerUser();
        }
    }

    void registerUser() throws IOException {

        final RequestQueue queue = MySingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();
        final String url = "http://localhost:3000/createNewUser"; // your URL
        queue.start();

       StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                            Toast.makeText(activity, response, Toast.LENGTH_SHORT).show();
                           // DisplayText.setText(response.getString("message"));
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                parseVolleyError(error);
                Toast.makeText(activity, "That didn't work!", Toast.LENGTH_SHORT).show();
                //DisplayText.setText("That didn't work!");
            }
        }) {
           @Override
           protected Map<String, String> getParams()
           {
               Map<String, String>  params = new HashMap<String, String>();
               params.put("username", email.getText().toString()); // the entered data as the JSON body.
               params.put("password", password.getText().toString());
               return params;
           }
       };

        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
/*
        //for now just returns back to login
        Intent i = new Intent(this, LoginActivity.class);
        startActivityForResult(i, LOGIN_ACTIVITY_ID);*/
    }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            JSONArray errors = data.getJSONArray("errors");
            JSONObject jsonMessage = errors.getJSONObject(0);
            String message = jsonMessage.getString("message");
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }

    boolean validateData() {
        boolean valid = true;

        if (isEmpty(name)) {
            valid = false;
            name.setError("You must input a name!");
        }

        if (isEmpty(school)) {
            valid = false;
            school.setError("You must input a school!");
        }

        if (!isEmail(email)) {
            valid = false;
            email.setError("You must input a valid email!");
        }

        if (isEmpty(password)) {
            valid = false;
            password.setError("You must input a desired password!");
        }

        return valid;
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

