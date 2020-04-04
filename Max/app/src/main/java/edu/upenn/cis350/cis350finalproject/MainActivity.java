package edu.upenn.cis350.cis350finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onLoginClick(View view) {

        // temporarily store usernamee and password inputs
        EditText usernameEditText = (EditText) findViewById(R.id.username);
        String usernameInput = usernameEditText.getText().toString();

        EditText passwordEditText = (EditText) findViewById(R.id.password);
        String passwordInput = passwordEditText.getText().toString();

        // check to see if username exists in the database
        if (true) {
            // check to see if password matches the respective username
            if (true) {
                // go to 'homepage' activity

                // Intent i = new Intent(this, HomepageActivity.class);
                // startActivityForResult(i, 1);

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
        // redirect to sign-up Activity... KK

        // Intent i = new Intent(this, SignupActivity.class);
        // startActivityForResult(i, 2);
    }


}
