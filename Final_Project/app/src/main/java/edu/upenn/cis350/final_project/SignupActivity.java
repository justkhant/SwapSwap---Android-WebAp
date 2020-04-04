package edu.upenn.cis350.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class SignupActivity extends AppCompatActivity {
    static final int IMAGE_PICK_ID = 500;
    static final int REGISTER_ID = 501;
    static final int LOGIN_ACTIVITY_ID = 2;

    ImageView picView;
    Button profPic;

    EditText name;
    EditText school;
    EditText email;
    EditText password;

    Button signup;

    Uri picUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Set up views
        picView = findViewById(R.id.viewPicture);
        name = findViewById(R.id.inputName);

        school = findViewById(R.id.inputSchool);
        email = findViewById(R.id.inputEmail);
        password = findViewById(R.id.inputPassword);
        profPic = findViewById(R.id.inputPic);
        signup = findViewById(R.id.inputSignup);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode,resultCode, intent);
        switch(requestCode) {
            case IMAGE_PICK_ID:
                if (resultCode == RESULT_OK) {
                    picUri = intent.getData();
                    picView.setImageURI(picUri);
                }
        }
    }

    public void onProfPicButtonClick(View v) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, IMAGE_PICK_ID);
    }

    public void onSignupButtonClick(View v) {
        boolean valid = validateData();
        if (valid) {
            registerUser();
        }
    }

    void registerUser() {
        //for now just returns back to login
        Intent i = new Intent(this, LoginActivity.class);
        startActivityForResult(i, LOGIN_ACTIVITY_ID);
    }

    boolean validateData() {
        boolean valid = true;

        if (isEmpty(name)) {
            valid = false;
            name.setError("You must input a name!");
        }

        if (isEmpty(school)) {
            valid = false;
            school.setError("You must input a school!");
        }

        if (!isEmail(email)) {
            valid = false;
            email.setError("You must input a valid email!");
        }

        if (isEmpty(password)) {
            valid = false;
            password.setError("You must input a desired password!");
        }

        return valid;
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

