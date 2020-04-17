package edu.upenn.cis350.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import edu.upenn.cis350.final_project.R;

public class NewPostActivity extends AppCompatActivity {

    Switch avail_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        // AVAILABILITY SWITCH CODE
        avail_switch = (Switch) findViewById(R.id.avail_switch);
        avail_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked == true) {

                } else {

                }
            }
        });
    }
}
