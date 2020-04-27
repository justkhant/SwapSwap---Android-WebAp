package edu.upenn.cis350.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
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
    private ImageView profilePic;

    private String user_email;

    private JSONObject user;
    public static final int EDIT_ACTIVITY_ID = 9;
    public static final int POSTS_ACTIVITY_ID = 10;
    static final int HOME_ACTIVITY_ID = 29;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //find user to display their information
        user_email = SingletonVariableStorer.getCurrUserInstance();
        user = getUserProfile(user_email);

        try {
            //fill out info
            bio = findViewById(R.id.about_me_body);
            if (user.getString("bio").length() > 0) {
                bio.setText(user.getString("bio"));
            }

            email = findViewById(R.id.email_body);
            email.setText(user_email);

            school = findViewById(R.id.school_body);
            school.setText(user.getString("school"));

            name = findViewById(R.id.full_name);
            name.setText(user.getString("name"));

            phoneNumber = findViewById(R.id.phone_num_body);
            if (user.getString("phoneNumber").length() > 0) {
                phoneNumber.setText(user.getString("phoneNumber"));
            }

            //set prof pic
            profilePic = findViewById(R.id.profile_pic);
            String base64Image = user.getString("profilePic");
            if (base64Image.length() == 0) {
                profilePic.setImageResource(R.drawable.profile_pic);
            } else {
                profilePic.setImageBitmap(base64StringToBitmap(base64Image));
            }

        } catch (Exception e) {
            Toast.makeText(this, "error displaying profile data",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap base64StringToBitmap(String base64Image) throws JSONException {
        String cleanImage = base64Image.replace("data:image/png;base64,", "")
                .replace("data:image/jpeg;base64,", "");
        byte[] decodedString = Base64.decode(cleanImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
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

    public void goToPostingsListPage(View v) {
        Intent intent = new Intent(UserProfileActivity.this, PostingsListActivity.class);
        startActivityForResult(intent, POSTS_ACTIVITY_ID);
    }

    public void onEditClick(View view) {
        Intent i = new Intent(this, EditProfileActivity.class);

        //pass on user information
        try {
            i.putExtra("name", user.getString("name"));
            i.putExtra("bio", user.getString("bio"));
            i.putExtra("points", user.getInt("points"));
            i.putExtra("rank", user.getInt("rank"));
            i.putExtra("phoneNumber", user.getString("phoneNumber"));
            i.putExtra("school", user.getString("school"));
            i.putExtra("profilePic", user.getString("profilePic"));

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
                conn.setConnectTimeout(10000);
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

        AlertDialog.Builder altDialog = new AlertDialog.Builder(UserProfileActivity.this);
        altDialog.setMessage("Are you sure you want to delete your profile?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUser(user_email);

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
        alert.setTitle("Delete User");
        alert.show();
    }

    public void onHomeClick(View view) {
        Intent i = new Intent(this, HomeActivity.class);
        startActivityForResult(i, HOME_ACTIVITY_ID);
    }
}
