package edu.upenn.cis350.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/*
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.StringRequest;
*/
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static android.widget.Toast.LENGTH_LONG;

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

    // inner class used to access the web by the login method
    public class AccessWebTask extends AsyncTask<URL, String, JSONObject> {
        private String method;

        public AccessWebTask(String method) {
            this.method = method;
        }
        /*
        This method is called in background when this object's "execute" method is invoked.
        The arguments passed to "execute" are passed to this method.
         */
        protected JSONObject doInBackground(URL... urls) {
            try {
                // get the first URL from the array
                URL url = urls[0];
                // create connection and send HTTP request
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(method); // send HTTP request
                conn.connect();
                int statusCode = conn.getResponseCode(); // should be 200
                // read the first line of data that is returned
                Scanner in = new Scanner(url.openStream());
                String msg = in.nextLine();

                // use Android JSON library to parse JSON
                JSONObject jo = new JSONObject(msg);
                // pass the JSON object to the foreground that called this method
                return jo;

            } catch (Exception e) {
                e.printStackTrace();
                return new JSONObject(); // should empty JSONObject upon encountering an exception
            }
        }
    }

    // This helper method passes the strings to node and runs createNewUser to add new user info.
    public void postUserProfile(String email, String password,
                               String name, String school) {
        try {
            // 10.0.2.2 is the host machine as represented by Android Virtual Device
            URL url = new URL("http://10.0.2.2:3000/createNewUser?" +
                    "email=" + email + "&" +
                    "password=" + password + "&" +
                    "name=" + name + "&" +
                    "school=" + school);
            AccessWebTask task = new AccessWebTask("POST");
            task.execute(url);
        } catch (Exception e) {
            // empty return upon encountering an exception
            e.printStackTrace();
        }
    }

    public void onSignupButtonClick(View v) throws IOException {
        boolean valid = validateData();
        if (valid) {

            // FIND A WAY TO PASS THIS DATA TO NODE.
            // I'm guessing I have to run something like: localhost:3000/createNewUser?...
            // with the data to pass in

            // HELP
            String nameInput = name.getText().toString();
            String schoolInput = school.getText().toString();
            String emailInput = email.getText().toString();
            String passwordInput = password.getText().toString();

            // This method call should end up uploading the information to the database
            postUserProfile(emailInput, passwordInput, nameInput, schoolInput);

            Toast.makeText(this, "Signup successful", LENGTH_LONG).show();

            // just go back to Login Activity
            Intent i = new Intent(this, LoginActivity.class);
            startActivityForResult(i, LOGIN_ACTIVITY_ID);
            //registerUser(); leftover from failed volley attempt
        }
    }


    // This helper method gathers the user data to be parsed when a login attempt is made.
    public JSONObject getUserProfile(String email) {
        try {
            // 10.0.2.2 is the host machine as represented by Android Virtual Device
            URL url = new URL("http://10.0.2.2:3000/search_user?email=" + email);
            AccessWebTask task = new AccessWebTask("GET");
            task.execute(url);
            return task.get(); // waits for doInBackground to finish, then gets the return value
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject(); // return empty JSON Object upon encountering an exception
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
        } else {
            JSONObject check_for_user = getUserProfile(email.getText().toString());
            if (check_for_user.length() !=  0) {
                valid = false;
                email.setError("This email has already been used!");
            }
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

