package edu.upenn.cis350.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class EditProfileActivity extends AppCompatActivity {
    static final int PROFILE_ACTIVITY_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
    }

    public void onSaveButtonClick(View v) {
        //create Intent using the current Activity
        //and the Class to be created
        Intent i = new Intent(this, UserProfileActivity.class);

        //pass Intent to activity using specified code
        startActivityForResult(i, PROFILE_ACTIVITY_ID);
    }

    public void onCancelButtonClick(View v) {
        //create Intent using the current Activity
        //and the Class to be created
        Intent i = new Intent(this, UserProfileActivity.class);

        //pass Intent to activity using specified code
        startActivityForResult(i, PROFILE_ACTIVITY_ID);
    }
}
