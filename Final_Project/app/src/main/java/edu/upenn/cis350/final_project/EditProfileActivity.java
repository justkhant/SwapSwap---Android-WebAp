package edu.upenn.cis350.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static android.widget.Toast.LENGTH_LONG;

public class EditProfileActivity extends AppCompatActivity {
    static final int PROFILE_ACTIVITY_ID = 50;

    private TextView bio;
    private TextView rank;
    private TextView name;
    private TextView phoneNumber;
    private TextView email;
    private TextView points;
    private TextView school;
    private String user_email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent curr_intent = getIntent();
        user_email = curr_intent.getStringExtra("email");

       //fill out info
        bio = findViewById(R.id.edit_about_me_body);
        bio.setText(curr_intent.getStringExtra("bio"));

        email = findViewById(R.id.edit_email_body);
        email.setText(user_email);

        rank = findViewById(R.id.edit_rank);
        rank.setText(String.valueOf(curr_intent.getIntExtra("rank", 0)));

        points = findViewById(R.id.edit_points);
        points.setText(String.valueOf(curr_intent.getIntExtra("points", 0)));

        school = findViewById(R.id.edit_school_body);
        school.setText(curr_intent.getStringExtra("school"));

        name = findViewById(R.id.edit_full_name);
        name.setText(curr_intent.getStringExtra("name"));

        phoneNumber = findViewById(R.id.edit_phone_num_body);
        phoneNumber.setText(curr_intent.getStringExtra("phoneNumber"));;

    }

    // inner class used to access the web
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
                conn.setRequestMethod("POST"); // send HTTP request
                conn.connect();
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

        //This method is called in foreground after doInBackground finishes.
        protected void onPostExecute(String msg) {
            // not implemented but you can use this if you’d like//
        }

    }

    // This helper method gathers the user data to be parsed when a login attempt is made.
    public void updateUserProfile(String schoolInput, String bioInput, String pnInput, String s_email) {
        try {
            // 10.0.2.2 is the host machine as represented by Android Virtual Device

            URL url = new URL("http://10.0.2.2:3000/update_profile?" +
                    "email=" + s_email + "&" +
                    "bio=" + bioInput + "&" +
                    "phoneNumber=" + pnInput + "&" +
                    "school=" + schoolInput);
            AccessWebTask task = new AccessWebTask();
            task.execute(url).get();
            Toast.makeText(this, "Update successful", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Update error", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSaveButtonClick(View v) {

        EditText schoolEditText = (EditText) findViewById(R.id.edit_school_body);
        String schoolInput = schoolEditText.getText().toString();

        EditText bioEditText = (EditText) findViewById(R.id.edit_about_me_body);
        String bioInput = bioEditText.getText().toString();

        EditText pnEditText = (EditText) findViewById(R.id.edit_phone_num_body);
        String pnInput = pnEditText.getText().toString();

        String s_email = email.getText().toString();

        if (validateData(pnInput) || pnInput.length() == 0) {
            // This method call should end up uploading the information to the database
            updateUserProfile(schoolInput, bioInput, pnInput, s_email);

            Intent i = new Intent(this, UserProfileActivity.class);
            passOnEmail(i, s_email);
            //pass Intent to activity using specified code
            startActivityForResult(i, PROFILE_ACTIVITY_ID);
        }


    }

    boolean validateData(String pnInput) {
        boolean valid = true;

        if (!Patterns.PHONE.matcher(pnInput).matches()) {
            valid = false;
            phoneNumber.setError("You must input a valid phone number format.");
        }
        return valid;
    }

    public void passOnEmail(Intent i, String email) {
        //pass on user information
        try {
            i.putExtra("email", email);
        } catch (Exception e) {
            Toast.makeText(this, "error passing on values", Toast.LENGTH_SHORT).show();
        }
    }

    public void onCancelButtonClick(View v) {
        // Let the user know the attempt to edit the profile was cancelled
        Toast.makeText(this, "Profile edit cancelled", LENGTH_LONG).show();

        //create Intent using the current Activity
        //and the Class to be created
        Intent i = new Intent(this, UserProfileActivity.class);

        passOnEmail(i, user_email);

        //pass Intent to activity using specified code
        startActivityForResult(i, PROFILE_ACTIVITY_ID);
    }
}
