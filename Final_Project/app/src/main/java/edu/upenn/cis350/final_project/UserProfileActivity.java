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
    private String user_email;

    private JSONObject user;
    private Button viewPostings;
    public static final int EDIT_ACTIVITY_ID = 9;
    public static final int POSTS_ACTIVITY_ID = 10;


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

        //find user to display their information
        Intent curr_intent = getIntent();
        user_email = curr_intent.getStringExtra("email");

        user = getUserProfile(user_email);

        try {
            //fill out info
            bio = findViewById(R.id.about_me_body);
            bio.setText(user.getString("bio"));

            email = findViewById(R.id.email_body);
            email.setText(user_email);

            rank = findViewById(R.id.rank);
            rank.setText("" + user.getInt("rank"));

            points = findViewById(R.id.points);
            points.setText("" + user.getInt("points"));

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

    private void goToPostingsListPage() {
        Intent intent = new Intent(UserProfileActivity.this, PostingsListActivity.class);
        try {
            intent.putExtra("email", user.getString("email"));
        } catch (Exception e){
            Toast.makeText(this, "error passing on values", Toast.LENGTH_SHORT).show();
        }
        startActivity(intent);
    }

    public void onEditClick(View view) {
        Intent i = new Intent(this, EditProfileActivity.class);

        //pass on user information
        try {
            i.putExtra("name", user.getString("name"));
            i.putExtra("email", user.getString("email"));
            i.putExtra("bio", user.getString("bio"));
            i.putExtra("points", user.getInt("points"));
            i.putExtra("rank", user.getInt("rank"));
            i.putExtra("phoneNumber", user.getString("phoneNumber"));
            i.putExtra("school", user.getString("school"));

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

    public void passOnEmail(Intent i, String email) {
        //pass on user information
        try {
            i.putExtra("email", email);
        } catch (Exception e) {
            Toast.makeText(this, "error passing on values", Toast.LENGTH_SHORT).show();
        }
    }

    public void onPostsClick(View view) {
        //not implemented yet
        Intent i = new Intent(this, PostingsListActivity.class);
        passOnEmail(i, user_email);
        startActivityForResult(i, POSTS_ACTIVITY_ID);
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
