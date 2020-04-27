package edu.upenn.cis350.final_project;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class OtherUserProfileActivity extends AppCompatActivity {

    private TextView bio;
    private TextView name;
    private TextView phoneNumber;
    private TextView email;
    private TextView school;
    private String user_email;
    private Intent curr_intent;
    private JSONObject user;

    static final int HOME_ACTIVITY_ID = 29;
    private static final int VIEW_POST_ACTIVITY_ID = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);

        //find user to display their information
        curr_intent = getIntent();
        user_email = curr_intent.getStringExtra("email");

        user = getUserProfile(user_email);

        try {
            //fill out info
            bio = findViewById(R.id.about_me_body);
            bio.setText(user.getString("bio"));

            email = findViewById(R.id.email_body);
            email.setText(user_email);

            school = findViewById(R.id.school_body);
            school.setText(user.getString("school"));

            name = findViewById(R.id.full_name);
            name.setText(user.getString("name"));

            phoneNumber = findViewById(R.id.phone_num_body);
            phoneNumber.setText(user.getString("phoneNumber"));

        } catch (Exception e) {
            Toast.makeText(this, "error displaying profile data",
                    Toast.LENGTH_SHORT).show();
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
                return new JSONObject();
            }
        }
    }

    public void backToAllPostsClick(View view) {
        Intent i = new Intent(this, HomeActivity.class);
        startActivityForResult(i, HOME_ACTIVITY_ID);
    }

    public void viewUserPostsClick(View view) throws JSONException {
        Intent i = new Intent(this, PostingsListActivity.class);
        i.putExtra("otherUser", true);
        i.putExtra("email", user_email);
        i.putExtra("name", user.getString("name"));
        startActivityForResult(i, VIEW_POST_ACTIVITY_ID);
    }

    public void passOnEmail(Intent i, String email) {
        //pass on user information
        try {
            i.putExtra("email", email);
        } catch (Exception e) {
            Toast.makeText(this, "error passing on values", Toast.LENGTH_SHORT).show();
        }
    }

}
