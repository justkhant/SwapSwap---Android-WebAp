package edu.upenn.cis350.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class UserProfileActivity extends AppCompatActivity {
    private TextView bio;
    private TextView rank;
    private TextView name;
    private TextView phoneNumber;
    private TextView email;
    private TextView points;
    private TextView school;

    private Intent curr_intent;
    private Button viewPostings;
    public static final int EDIT_ACTIVITY_ID = 9;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        viewPostings = findViewById(R.id.view_user_listings_button);
        viewPostings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPostingsListPage();
            }
        });

        curr_intent = getIntent();

        //fill out info
        bio = findViewById(R.id.about_me_body);
        bio.setText(curr_intent.getStringExtra("bio"));

        email = findViewById(R.id.email_body);
        email.setText(curr_intent.getStringExtra("email"));

        rank = findViewById(R.id.rank);
        rank.setText(String.valueOf(curr_intent.getIntExtra("rank", 0)));

        points = findViewById(R.id.points);
        points.setText(String.valueOf(curr_intent.getIntExtra("points", 0)));

        school = findViewById(R.id.school_body);
        school.setText(curr_intent.getStringExtra("school"));

        name = findViewById(R.id.full_name);
        name.setText(curr_intent.getStringExtra("name"));

        phoneNumber = findViewById(R.id.phone_num_body);
        phoneNumber.setText(curr_intent.getStringExtra("phoneNumber"));

        //points = findViewById(R.id.points);
        //points.setText(curr_intent.getIntExtra("points", 0));
    }

    private void goToPostingsListPage() {
        Intent intent = new Intent(UserProfileActivity.this, PostingsListActivity.class);
        startActivity(intent);
    }

    public void onEditClick(View view) {
        Intent i = new Intent(this, EditProfileActivity.class);

        //pass on user information
        try {
            i.putExtra("name", curr_intent.getStringExtra("name"));
            i.putExtra("email", curr_intent.getStringExtra("email"));
            i.putExtra("bio", curr_intent.getStringExtra("bio"));
            i.putExtra("points", curr_intent.getIntExtra("points", 0));
            i.putExtra("rank", curr_intent.getIntExtra("rank", 0));
            i.putExtra("phoneNumber", curr_intent.getStringExtra("phoneNumber"));
            i.putExtra("school", curr_intent.getStringExtra("school"));

        } catch (Exception e) {
            Toast.makeText(this, "error passing on values", Toast.LENGTH_SHORT).show();
        }
        startActivityForResult(i, EDIT_ACTIVITY_ID);
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

    // This helper method passes the strings to node and runs createNewUser to add new user info.
    public void deleteUser(String email) {
        try {
            // 10.0.2.2 is the host machine as represented by Android Virtual Device
            URL url = new URL("http://10.0.2.2:3000/deleteUser?" +
                    "userToDelete=" + email);
            AccessWebTask task = new AccessWebTask("POST");
            task.execute(url);
        } catch (Exception e) {
            // empty return upon encountering an exception
            e.printStackTrace();
        }
    }

    public void onDeleteUserClick(View view) {

        Button deleteUserButton = (Button) findViewById(R.id.delete_user_button);
        deleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder altDialog = new AlertDialog.Builder(UserProfileActivity.this);
                altDialog.setMessage("Are you sure you want to delete your profile?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                deleteUser(email.getText().toString());

                                // Go back to the login page after deleting the profile from the database
                                Intent i = new Intent(UserProfileActivity.this, LoginActivity.class);
                                startActivityForResult(i, 100);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = altDialog.create();
                alert.setTitle("Logout");
                alert.show();
            }
        });
    }
}
