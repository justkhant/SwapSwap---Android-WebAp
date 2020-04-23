package edu.upenn.cis350.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static android.widget.Toast.LENGTH_LONG;

public class EditPostActivity extends AppCompatActivity {
    static final int VIEW_POST_ACTIVITY_ID = 30;

    private Intent curr_intent;
    String email;
    String _id;
    JSONObject post;

    EditText title;
    RadioGroup category;
    RadioButton cat;
    EditText details;
    Switch avail_switch;
    Boolean completed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        curr_intent = getIntent();
        email = SingletonVariableStorer.getCurrUserInstance();
        _id = curr_intent.getStringExtra("_id");

        post = getPost(_id);

        try {
            //fill out info
            title = findViewById(R.id.title_body);
            title.setText(post.getString("title"));

            details = findViewById(R.id.details_body);
            details.setText(post.getString("details"));

            //category button group

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

            category = (RadioGroup)findViewById(R.id.category_body);
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


            // AVAILABILITY SWITCH CODE
            completed = post.getBoolean("completed");

            avail_switch = (Switch) findViewById(R.id.avail_switch);
            avail_switch.setChecked(completed);


            avail_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked == true) {
                    } else {
                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "error displaying profile data",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // inner class used to access the web
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

        //This method is called in foreground after doInBackground finishes.
        protected void onPostExecute(String msg) {
            // not implemented but you can use this if you’d like//
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


    // This helper method gathers the user data to be parsed when a login attempt is made.
    public void updatePost(String titleInput, String catInput, Boolean compInput, String imgURLInput,
                           String detailInput, String email) {
       // Toast.makeText(this, "entered update", Toast.LENGTH_SHORT).show();
        try {
            // 10.0.2.2 is the host machine as represented by Android Virtual Device

            URL url = new URL("http://10.0.2.2:3000/updatePost?" +
                    "_id=" + _id + "&" +
                    "title=" + titleInput + "&" +
                    "category=" + catInput + "&" +
                    "completed=" + compInput + "&" +
                    "imgURL=" + imgURLInput + "&" +
                    "details=" + detailInput + "&" +
                    "owner=" + email);
            AccessWebTask task = new AccessWebTask("POST");
            task.execute(url).get();
          //  Toast.makeText(this, "Update successful", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Update error", Toast.LENGTH_SHORT).show();
        }
    }

    public void passOnID(Intent i, String _id) {
        //pass on post information
        try {
            i.putExtra("_id", _id);
        } catch (Exception e) {
            Toast.makeText(this, "error passing on values", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSaveButtonClick(View view) {

        EditText titleET = findViewById(R.id.title_body);
        String titleInput = titleET.getText().toString();

        EditText detailET= findViewById(R.id.details_body);
        String detailInput = detailET.getText().toString();

        Boolean compInput = avail_switch.isChecked();
        String catInput = cat.getText().toString();

        updatePost(titleInput, catInput, compInput, "tempEmpty", detailInput, email);

        Toast.makeText(this, "Update Post successful", LENGTH_LONG).show();

        // just go back to Login Activity
        Intent i = new Intent(this, ViewPostActivity.class);
        passOnID(i, _id);

        startActivityForResult(i, VIEW_POST_ACTIVITY_ID);
    }

    public void onCancelButtonClick(View view) {
        Intent i = new Intent(this, ViewPostActivity.class);
        passOnID(i, _id);
        startActivityForResult(i, VIEW_POST_ACTIVITY_ID);
    }
}
