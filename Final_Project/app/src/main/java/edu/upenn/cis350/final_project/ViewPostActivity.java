package edu.upenn.cis350.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class ViewPostActivity extends AppCompatActivity {
    static final int EDIT_POST_ACTIVITY_ID = 20;
    static final int PROFILE_ACTIVITY_ID = 21;
    private Intent curr_intent;
    private String user_email;
    private TextView title;
    private TextView details;
    private ImageView availability;
    private RadioGroup category;
    private RadioButton cat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        curr_intent = getIntent();
        Intent curr_intent = getIntent();

        user_email = curr_intent.getStringExtra("email");

        //fill out info
        title = findViewById(R.id.title_body);
        //title.setText(curr_intent.getStringExtra("title"));

        details = findViewById(R.id.details_body);
        //email.setText(user_email);

        availability = findViewById(R.id.avail_icon);
        //rank.setText(String.valueOf(curr_intent.getIntExtra("rank", 0)));

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
                    default:
                        //if (
                }
            }
        });
    }

    public void passOnEmail(Intent i, String email) {
        //pass on user information
        try {
            i.putExtra("email", email);
        } catch (Exception e) {
            Toast.makeText(this, "error passing on values", Toast.LENGTH_SHORT).show();
        }
    }

    public void onEditButtonClick(View view) {
        Intent i = new Intent(this, EditPostActivity.class);
/*
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
        }*/

        passOnEmail(i, curr_intent.getStringExtra("email"));
        startActivityForResult(i, EDIT_POST_ACTIVITY_ID);
    }

    public void onProfileButtonClick(View view) {
        Intent i = new Intent(this, UserProfileActivity.class);
/*
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
        }*/

        passOnEmail(i, curr_intent.getStringExtra("email"));
        startActivityForResult(i, PROFILE_ACTIVITY_ID);
    }


}
