package edu.upenn.cis350.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

}
