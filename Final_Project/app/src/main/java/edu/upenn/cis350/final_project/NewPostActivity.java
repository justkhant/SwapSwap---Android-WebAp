package edu.upenn.cis350.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import edu.upenn.cis350.final_project.R;

import static android.widget.Toast.LENGTH_LONG;

public class NewPostActivity extends AppCompatActivity {

    int HOME_ACTIVITY_ID = 12;
    int VIEW_POST_ACTIVITY_ID = 13;

    EditText title;
    RadioButton cat;
    EditText details;
    EditText imgURL;
    Switch avail_switch;
    Boolean avail_bool;
    String email;

    private Intent curr_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        curr_intent = getIntent();

        /*
        // AVAILABILITY SWITCH CODE
        avail_switch = (Switch) findViewById(R.id.avail_switch);
        avail_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked == true) {
                    avail_bool = true;
                } else {
                    avail_bool = false;
                }
            }
        });
    */

        //Category code
        RadioGroup category = (RadioGroup)findViewById(R.id.category_body);
        category.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                switch(checkedId) {
                    case R.id.button_trade:
                        cat = findViewById(R.id.button_trade);
                        break;
                    case R.id.button_donation:
                        cat = findViewById(R.id.button_donation);
                        break;
                    case R.id.button_donation_req:
                        cat = findViewById(R.id.button_donation_req);
                        break;
                }
            }
        });


        // Set up views
        title = findViewById(R.id.title_body);
        details = findViewById(R.id.details_body);
        //avail = findViewById(R.id.avail_switch);
        //imgURL = findViewById(R.id.inputSignup);
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
    public void createNewPost(String titleInput, String catInput, Boolean avail_bool, String imgURLInput,
                              String detailInput, String email) {
        try {
            // 10.0.2.2 is the host machine as represented by Android Virtual Device
            URL url = new URL("http://10.0.2.2:3000/createNewPost?" +
                    "title=" + titleInput + "&" +
                    "category=" + catInput + "&" +
                    "completed=" + avail_bool + "&" +
                    "imgURL=" + imgURLInput + "&" +
                    "details=" + detailInput + "&" +
                    "owner=" + email);
            AccessWebTask task = new AccessWebTask("POST");
            task.execute(url);
        } catch (Exception e) {
            // empty return upon encountering an exception
            e.printStackTrace();
        }
    }

    public void passOnEmail(Intent i, String email) {
        //pass on user information
        try {
            i.putExtra("email", email);
        } catch (Exception e) {
            Toast.makeText(this, "error passing on values", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSaveButtonClick(View v) throws IOException {

        String titleInput = title.getText().toString();
        String catInput = cat.getText().toString();
        //Boolean availInput = avail_switch.toBoolean();
        String detailInput = details.getText().toString();
        //String imgURLInput = imgURL.getText().toString();
        String email = curr_intent.getStringExtra("email");

        // This method call should end up uploading the information to the database
        createNewPost(titleInput, catInput, false, "tempEmpty", detailInput, email);

        Toast.makeText(this, "New Post successful", LENGTH_LONG).show();

        // just go back to Login Activity
        Intent i = new Intent(this, ViewPostActivity.class);

        passOnEmail(i, curr_intent.getStringExtra("email"));
        startActivityForResult(i, VIEW_POST_ACTIVITY_ID);
    }

    public void onCancelButtonClick(View view) {
        Intent i = new Intent(this, HomeActivity.class);

        passOnEmail(i, curr_intent.getStringExtra("email"));
        startActivityForResult(i, HOME_ACTIVITY_ID);
    }

}
