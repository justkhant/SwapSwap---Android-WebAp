package edu.upenn.cis350.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    public static final int SIGNUP_ACTIVITY_ID = 1;
    public static final int EDIT_ACTIVITY_ID = 3;
    public static final int HOME_ACTIVITY_ID = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onLoginClick(View view) {

        // temporarily store usernamee and password inputs
        EditText usernameEditText = (EditText) findViewById(R.id.username);
        String usernameInput = usernameEditText.getText().toString();

        EditText passwordEditText = (EditText) findViewById(R.id.password);
        String passwordInput = passwordEditText.getText().toString();

        // check to see if username exists in the database
        // for now check if there is anything in text
        if (!usernameInput.equals("")) {
            // check to see if password matches the respective username
            if (!passwordInput.equals("")) {
                // go to 'homepage' activity
                Intent i = new Intent(this, HomeActivity.class);
                startActivityForResult(i, HOME_ACTIVITY_ID);
                //for now go to edit profile page
               // Intent i = new Intent(this, EditProfileActivity.class);
               // startActivityForResult(i, EDIT_ACTIVITY_ID);



            } else {
                // if password doesn't match, print out "Invalid password."
                Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show();
            }

        } else {
            // if username doesn't exist, print out "Invalid Username."
            Toast.makeText(this, "Invalid Username", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSignupClick(View view) {
        // redirect to sign-up Activity
        Intent i = new Intent(this, SignupActivity.class);
        startActivityForResult(i, SIGNUP_ACTIVITY_ID);
    }


}
