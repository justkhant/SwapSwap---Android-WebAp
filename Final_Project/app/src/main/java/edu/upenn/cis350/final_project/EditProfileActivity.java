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

    private Intent curr_intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        curr_intent = getIntent();

       //fill out info
        bio = findViewById(R.id.edit_about_me_body);
        bio.setText(curr_intent.getStringExtra("bio"));

        email = findViewById(R.id.edit_email_body);
        email.setText(curr_intent.getStringExtra("email"));

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
                conn.setRequestMethod(method); // send HTTP GET request
                conn.connect();
                int statusCode = conn.getResponseCode(); // should be 200

                // BELOW not used unless task.get() is called
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

    // This helper method gathers the user data to be parsed when a login attempt is made.
    public void updateUserProfile(String schoolInput, String bioInput, String pnInput, String s_email) {
        try {
            // 10.0.2.2 is the host machine as represented by Android Virtual Device

            URL url = new URL("http://10.0.2.2:3000/update_profile?" +
                    "email=" + s_email + "&" +
                    "bio=" + bioInput + "&" +
                    "phoneNumber=" + pnInput + "&" +
                    "school=" + schoolInput);
            AccessWebTask task = new AccessWebTask("POST");
            Toast.makeText(this, "Update successful", LENGTH_LONG).show();
            task.execute(url);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Update error", LENGTH_LONG).show();
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

        if (validateData(pnEditText)) {

            // JSONObject user = getUserProfile(s_email);

            // This method call should end up uploading the information to the database
            updateUserProfile(schoolInput, bioInput, pnInput, s_email);

            Intent i = new Intent(this, UserProfileActivity.class);

            try {
                //i.putExtra("name", curr_intent.getStringExtra("name"));
                i.putExtra("email", s_email);
                i.putExtra("name", name.getText().toString());
                i.putExtra("bio", bioInput);
                i.putExtra("points", points.getText().toString());
                i.putExtra("rank", rank.getText().toString());
                i.putExtra("phoneNumber", pnInput);
                i.putExtra("school", school.getText().toString());

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "error passing on values", Toast.LENGTH_SHORT).show();
            }

            //pass Intent to activity using specified code
            startActivityForResult(i, PROFILE_ACTIVITY_ID);
        }
    }

    boolean validateData(EditText phone) {
        boolean valid = true;

        if (!isPhone(phone)) {
            valid = false;
            phoneNumber.setError("You must input a valid phone number format.");
        }

        return valid;
    }

    boolean isPhone(EditText phoneNumberEditText) {
        String phone = phoneNumberEditText.getText().toString();
        return Patterns.PHONE.matcher(phone).matches();
    }


    public void onCancelButtonClick(View v) {
        // Let the user know the attempt to edit the profile was cancelled
        Toast.makeText(this, "Profile edit cancelled", LENGTH_LONG).show();

        //create Intent using the current Activity
        //and the Class to be created
        Intent i = new Intent(this, UserProfileActivity.class);

        //pass Intent to activity using specified code
        startActivityForResult(i, PROFILE_ACTIVITY_ID);
    }
}
