package edu.upenn.cis350.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class EditPostActivity extends AppCompatActivity {
    static final int VIEW_POST_ACTIVITY_ID = 30;

    private Intent curr_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        curr_intent = getIntent();
    }

    public void passOnEmail(Intent i, String email) {
        //pass on user information
        try {
            i.putExtra("email", email);
        } catch (Exception e) {
            Toast.makeText(this, "error passing on values", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSaveButtonClick(View view) {
        Intent i = new Intent(this, ViewPostActivity.class);
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
        startActivityForResult(i, VIEW_POST_ACTIVITY_ID);
    }

    public void onCancelButtonClick(View view) {
        Intent i = new Intent(this, ViewPostActivity.class);
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
        startActivityForResult(i, VIEW_POST_ACTIVITY_ID);
    }
}
