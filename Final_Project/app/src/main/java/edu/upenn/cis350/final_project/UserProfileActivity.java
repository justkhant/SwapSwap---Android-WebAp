package edu.upenn.cis350.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class UserProfileActivity extends AppCompatActivity {
    private TextView bio;
    private TextView rank;
    private TextView name;
    private TextView phoneNumber;
    private TextView email;
    private TextView points;
    private TextView school;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Intent curr_intent = getIntent();

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
        phoneNumber.setText(curr_intent.getStringExtra("phoneNum"));;

        //points = findViewById(R.id.points);
        //points.setText(curr_intent.getIntExtra("points", 0));
    }
}
