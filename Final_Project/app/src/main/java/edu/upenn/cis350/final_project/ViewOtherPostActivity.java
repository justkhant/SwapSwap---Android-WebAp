package edu.upenn.cis350.final_project;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ViewOtherPostActivity extends AppCompatActivity {
    static final int PROFILE_ACTIVITY_ID = 21;
    static final int HOME_ACTIVITY_ID = 29;
    private Intent curr_intent;
    private String user_email;
    private TextView title;
    private TextView details;
    private TextView posted_by;
    private ImageView availability;
    private RadioGroup category;
    private RadioButton cat;
    private JSONObject post;
    private String _id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_other_post);
        curr_intent = getIntent();

        _id = curr_intent.getStringExtra("_id");

        post = getPost(_id);

        try {
            //fill out info
            title = findViewById(R.id.title_body);
            title.setText(post.getString("title"));

            //category buttons
            category = findViewById(R.id.category_body);
            String passedCat = post.getString("category");

            switch(passedCat) {
                case "Trade":
                    cat = findViewById(R.id.button_trade);
                    category.check(R.id.button_trade);
                    //cat.setChecked(true);
                    break;
                case "Donation":
                    cat = findViewById(R.id.button_donation);
                    category.check(R.id.button_donation);
                    //cat.setChecked(true);
                    break;
                case "Donation Request":
                    cat = findViewById(R.id.button_donation_req);
                    category.check(R.id.button_donation_req);
                    //cat.setChecked(true);
                    break;
            }

            //details
            details = findViewById(R.id.details_body);
            details.setText(post.getString("details"));


            //completed switch
            availability = findViewById(R.id.avail_icon);
            if (post.getBoolean("completed")) {
                availability.setImageResource(R.drawable.red_x);
            } else {
                availability.setImageResource(R.drawable.green_check);
            }

            posted_by = findViewById(R.id.post_by_body);
            posted_by.setText(post.getString("owner_name"));

            user_email = post.getString("owner");

        } catch (Exception e) {
            Toast.makeText(this, "error displaying profile data",
                    Toast.LENGTH_SHORT).show();
        }

    }

        // This helper method gathers the user data to be parsed when a login attempt is made.
    public JSONObject getPost(String _id) {
        try {
            // 10.0.2.2 is the host machine as represented by Android Virtual Device
            URL url = new URL("http://10.0.2.2:3000/getPost?_id=" + _id);
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

    public void passOnEmail(Intent i, String email) {
        //pass on user information
        try {
            i.putExtra("email", email);
        } catch (Exception e) {
            Toast.makeText(this, "error passing on values", Toast.LENGTH_SHORT).show();
        }
    }

    public void onProfileButtonClick(View view) {
        Intent i = new Intent(this, OtherUserProfileActivity.class);
        passOnEmail(i, user_email);
        startActivityForResult(i, PROFILE_ACTIVITY_ID);
    }

    public void onHomeClick(View view) {
        Intent i = new Intent(this, HomeActivity.class);
        startActivityForResult(i, HOME_ACTIVITY_ID);
    }


}
