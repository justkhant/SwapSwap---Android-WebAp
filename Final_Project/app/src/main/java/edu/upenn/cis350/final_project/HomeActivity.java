package edu.upenn.cis350.final_project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HomeActivity extends AppCompatActivity implements PostingsAdapter.OnPostListener {

    private static final String TAG = "MainActivity";
    public static final int INFO_ACTIVITY_ID = 5;
    public static final int SEARCH_ACTIVITY_ID = 6;
    public static final int POSTS_ACTIVITY_ID = 7;
    public static final int PROFILE_ACTIVITY_ID = 8;
    public static final int NEW_POST_ID = 10;
    public static final int VIEW_OTHER_POST_ACTIVITY_ID = 11;


    private String curr_user;

    private List<String> titles = new ArrayList<>();
    private List<String> descriptions = new ArrayList<>();
    private List<String> post_ids = new ArrayList<>();


    //images in drawable folder
    private int image_icons[] = {R.drawable.art_icon, R.drawable.blackboard_icon, R.drawable.book_icon,
            R.drawable.books_icon, R.drawable.computer_icon, R.drawable.deskchair_icon,
            R.drawable.art_icon, R.drawable.blackboard_icon};

    private RecyclerView recyclerView;
    private RecyclerView.Adapter postAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: Started.");

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        curr_user = SingletonVariableStorer.getCurrUserInstance();
        getAllPosts();

        // specify an adapter (see also next example)
        postAdapter = new PostingsAdapter(titles, descriptions, image_icons, this);
        recyclerView.setAdapter(postAdapter);

    }

    @Override
    public void onPostClick(int position) {
        Intent i = new Intent(this, ViewOtherPostActivity.class);
        i.putExtra("_id", post_ids.get(position));
        startActivityForResult(i, VIEW_OTHER_POST_ACTIVITY_ID);
    }


    // inner class used to access the web
    public class AccessWebTask extends AsyncTask<URL, String, JSONArray> {

        /*
        This method is called in background when this object's "execute" method is invoked.
        The arguments passed to "execute" are passed to this method.
         */
        protected JSONArray doInBackground(URL... urls) {
            try {
                // get the first URL from the array
                URL url = urls[0];
                // create connection and send HTTP request
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET"); // send HTTP request
                conn.connect();
                // read the first line of data that is returned
                Scanner in = new Scanner(url.openStream());

                String msg = in.nextLine();

                // use Android JSON library to parse JSON
                JSONArray jo = new JSONArray(msg);
                // pass the JSON object to the foreground that called this method
                return jo;

            } catch (Exception e) {
                e.printStackTrace();
                return new JSONArray(); // should empty JSONObject upon encountering an exception
            }
        }

        //This method is called in foreground after doInBackground finishes.
        protected void onPostExecute(String msg) {
            // not implemented but you can use this if you’d like//
        }

    }

    // This helper method gathers the user data to be parsed when a login attempt is made.
    public void getAllPosts() {
        try {
            // 10.0.2.2 is the host machine as represented by Android Virtual Device

            URL url = new URL("http://10.0.2.2:3000/allPosts");
            AccessWebTask task = new AccessWebTask();
            JSONArray posts = task.execute(url).get();

            try {
                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = posts.getJSONObject(i);
                    String title = post.getString("title");
                    String description = post.getString("details");

                    //filter out your own
                    if (!post.getString("owner").equals(curr_user)) {
                        titles.add(title);
                        descriptions.add(description);
                        post_ids.add(post.getString("_id"));
                    }

                }
            } catch (Exception e) {
                Toast.makeText(this, "Error Retrieving Posts", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error Retrieving Posts", Toast.LENGTH_SHORT).show();

        }
    }

    public void onInfoClick(View view) {
        Intent i = new Intent(this, InstructionsActivity.class);
        startActivityForResult(i, INFO_ACTIVITY_ID);
    }

    public void onHomeClick(View view) {
        //Intent i = new Intent(this, HomeActivity.class);
        //startActivityForResult(i, HOME_ACTIVITY_ID);
    }

    public void onMyPostsClick(View view) {
        //not implemented yet
        Intent i = new Intent(this, PostingsListActivity.class);
        i.putExtra("otherUser", false);
        startActivityForResult(i, POSTS_ACTIVITY_ID);
    }

    public void onAddClick(View view) {
        Intent i = new Intent(this, NewPostActivity.class);
        startActivityForResult(i, NEW_POST_ID);
    }

    public void onProfileClick(View view) {
        Intent i = new Intent(this, UserProfileActivity.class);
        startActivityForResult(i, PROFILE_ACTIVITY_ID);
    }

    public void onLogoutClick(View view) {
        AlertDialog.Builder altDial = new AlertDialog.Builder(this);
        altDial.setMessage("Are you sure you want to log out?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // finish(); this option will just destroy the stack
                        // This just returns the user to the login page
                        Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivityForResult(i, 100);
                    }
                })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                         });

        AlertDialog alert = altDial.create();
        alert.setTitle("Logout");
        alert.show();



    }
}



