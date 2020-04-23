package edu.upenn.cis350.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class InstructionsActivity extends AppCompatActivity {
    static final int HOME_ACTIVITY_ID = 29;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
    }

    public void onHomeClick(View view) {
        Intent i = new Intent(this, HomeActivity.class);
        startActivityForResult(i, HOME_ACTIVITY_ID);
    }
}
