package edu.upenn.cis350.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class LoginActivity extends AppCompatActivity {
    public static final int SIGNUP_ACTIVITY_ID = 1;
    public static final int EDIT_ACTIVITY_ID = 3;
    public static final int HOME_ACTIVITY_ID = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    // inner class used to access the web by the login method
    public class AccessWebTask extends AsyncTask<URL, String, JSONObject> {
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
                conn.setRequestMethod("GET"); // send HTTP GET request
                conn.connect();

                // read the first line of data that is returned
                Scanner in = new Scanner(url.openStream());
                String msg = in.nextLine();
                // use Android JSON library to parse JSON
                JSONObject jo = new JSONObject(msg);
                in.close();
                // pass the JSON object to the foreground that called this method
                return jo;

            } catch (Exception e) {
                e.printStackTrace();
                return new JSONObject(); // should empty JSONObject upon encountering an exception
            }
        }
    }

    // This helper method gathers the user data to be parsed when a login attempt is made.
    public JSONObject getUserProfile(String email) {
        try {
            // 10.0.2.2 is the host machine as represented by Android Virtual Device
            URL url = new URL("http://10.0.2.2:3000/search_user?email=" + email);
            AccessWebTask task = new AccessWebTask();
            task.execute(url);
            return task.get(); // waits for doInBackground to finish, then gets the return value
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject(); // return empty JSON Object upon encountering an exception
        }
    }

    public void onLoginClick(View view) throws JSONException {

        // temporarily store usernamee and password inputs
        EditText usernameEditText = (EditText) findViewById(R.id.username);
        String usernameInput = usernameEditText.getText().toString();

        EditText passwordEditText = (EditText) findViewById(R.id.password);
        String passwordInput = passwordEditText.getText().toString();

        JSONObject user = getUserProfile(usernameInput);

        // Default values to check against, assuming the user does not exist in the database.
        String usernameActual = "";
        String passwordActual = "";

        // After getting the user from the database, import its verification information to Android
        // Only proceed to call JSONObject methods if the user is not an empty JSONObject
        if (user.has("email") && user.has("password")) {
            try {
                usernameActual = user.getString("email");
                // go to 'homepage' activity
                passwordActual = user.getString("password");
            } catch (Exception e) {
                // ah shit, do nothing
            }
        }

        // 1. Check to see if username input box is empty
        // 2. Check to see if username exists in the database (returns empty string if nonexistent)
        if (!usernameInput.equals("") && !usernameActual.equals("")) {
            // check to see if password matches the password on file under the username
            if (passwordInput.equals(passwordActual)) {
                // go to 'homepage' activity
                Intent i = new Intent(this, HomeActivity.class);

                SingletonVariableStorer.setCurrUserInstance(usernameActual);
                Toast.makeText(this, "Welcome, " + user.getString("name") + "!", Toast.LENGTH_LONG).show();
                startActivityForResult(i, HOME_ACTIVITY_ID);

            } else {
                // if password doesn't match, print out "Invalid password."
                Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show();
            }

        } else if (usernameInput.equals("")) {
            // if username field is empty, print out "Please enter a username"
            Toast.makeText(this, "Please enter a email", Toast.LENGTH_SHORT).show();
        } else {
            // if username doesn't exist, print out "Invalid Username."
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSignupClick(View view) {
        // redirect to sign-up Activity
        Intent i = new Intent(this, SignupActivity.class);
        startActivityForResult(i, SIGNUP_ACTIVITY_ID);
    }
}
