package edu.upenn.cis350.final_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PostingsListActivity extends AppCompatActivity implements PostingsAdapter.OnPostListener {

    private static final int VIEW_POST_ACTIVITY_ID = 80;
    static final int HOME_ACTIVITY_ID = 81;
    private static final int VIEW_OTHER_POST_ACTIVITY_ID = 82;

    private List<String> titles = new ArrayList<>();
    private List<String> descriptions = new ArrayList<>();
    private List<String> post_ids = new ArrayList<>();
    private String curr_user;

    //images in drawable folder
    private int image_icons[] = {R.drawable.art_icon, R.drawable.blackboard_icon, R.drawable.book_icon,
            R.drawable.books_icon, R.drawable.computer_icon, R.drawable.deskchair_icon,
            R.drawable.art_icon, R.drawable.blackboard_icon};

    private RecyclerView recyclerView;
    private RecyclerView.Adapter postAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView title;
    private Intent curr_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postings_list);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        curr_intent = getIntent();
        title = (TextView) findViewById(R.id.post_text);

        if(curr_intent.getBooleanExtra("otherUser", false)) {
            curr_user = curr_intent.getStringExtra("email");
            title.setText(curr_intent.getStringExtra("name") + "'s Posts");
        } else {
            title.setText("My Posts");
            curr_user = SingletonVariableStorer.getCurrUserInstance();
        }
        getUserPosts(curr_user);
        // specify an adapter (see also next example)
        postAdapter = new PostingsAdapter(titles, descriptions, image_icons, this);
        recyclerView.setAdapter(postAdapter);

    }

    @Override
    public void onPostClick(int position) {
        if (curr_intent.getBooleanExtra("otherUser", false)) {
            Intent i = new Intent(this, ViewOtherPostActivity.class);
            i.putExtra("_id", post_ids.get(position));
            startActivityForResult(i, VIEW_OTHER_POST_ACTIVITY_ID);
        } else {
            Intent i = new Intent(this, ViewPostActivity.class);
            i.putExtra("_id", post_ids.get(position));
            startActivityForResult(i, VIEW_POST_ACTIVITY_ID);
        }
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
    public void getUserPosts(String s_email) {
        try {
            // 10.0.2.2 is the host machine as represented by Android Virtual Device

            URL url = new URL("http://10.0.2.2:3000/findUserPosts?" +
                    "owner=" + s_email);
            AccessWebTask task = new AccessWebTask();
            JSONArray posts = task.execute(url).get();

            Toast.makeText(this, "Retrieved Posts", Toast.LENGTH_SHORT).show();

            try {
                for (int i = 0; i < posts.length(); i++) {
                    String title = posts.getJSONObject(i).getString("title");
                    String description = posts.getJSONObject(i).getString("details");

                    titles.add(title);
                    descriptions.add(description);
                    post_ids.add(posts.getJSONObject(i).getString("_id"));

                }
            } catch (Exception e) {
                Toast.makeText(this, "Error Retrieving Posts", Toast.LENGTH_SHORT).show();

            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error Retrieving Posts", Toast.LENGTH_SHORT).show();
        }
    }

    public void onHomeClick(View view) {
        Intent i = new Intent(this, HomeActivity.class);
        startActivityForResult(i, HOME_ACTIVITY_ID);
    }



}