package edu.upenn.cis350.myapplication;

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
    static final int INST_TEST_ID = 400;

    ImageView picView;
    Button profPic;

    EditText name;
    EditText school;
    EditText email;
    EditText username;
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
        username = findViewById(R.id.inputUsername);
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
            Intent i = new Intent();
            i.putExtra("name", name.getText().toString());
            i.putExtra("school", school.getText().toString());
            i.putExtra("email", email.getText().toString());
            i.putExtra("username", username.getText().toString());
            i.putExtra("password", password.getText().toString());
            i.putExtra("profPic", picUri);

            setResult(RESULT_OK, i);
            finish();
        }
    }

    public void onTestButtonClick(View v) {
        Intent i = new Intent(this, InstructionsActivity.class);
        startActivity(i);
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

        if (isEmpty(username)) {
            valid = false;
            username.setError("You must input a desired username!");
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

