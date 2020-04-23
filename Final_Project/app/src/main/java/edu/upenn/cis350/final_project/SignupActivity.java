package edu.upenn.cis350.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import static android.widget.Toast.LENGTH_LONG;

public class SignupActivity extends AppCompatActivity {
    static final int LOGIN_ACTIVITY_ID = 2;

    EditText name;
    EditText school;
    EditText email;
    EditText password;
    Button signup;
    Map<String, String> valueMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Set up views
        name = findViewById(R.id.inputName);
        school = findViewById(R.id.inputSchool);
        email = findViewById(R.id.inputEmail);
        password = findViewById(R.id.inputPassword);
        signup = findViewById(R.id.inputSignup);
    }

    // inner GET class used to access the web by the login method
    public class AccessWebTask extends AsyncTask<URL, String, JSONObject> {
        String method;
        String jsonInputString;

        public AccessWebTask(String method) {
            this.method = method;
            this.jsonInputString = "";
        }

        public AccessWebTask(String method, String jsonInputString) {
            this.method = method;
            this.jsonInputString = jsonInputString;
        }
        /*
        This method is called in background when this object's "execute" method is invoked.
        The arguments passed to "execute" are passed to this method.
         */
        protected JSONObject doInBackground(URL... urls) {
            try {
                // get the first URL from the array
                URL url = urls[0];
                // create connection and send HTTP request
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(method); // send HTTP request

                if (method == "GET") {
                    conn.connect();

                    // read the first line of data that is returned
                    Scanner in = new Scanner(url.openStream());
                    String msg = in.nextLine();
                    // use Android JSON library to parse JSON
                    JSONObject jo = new JSONObject(msg);
                    in.close();
                    // pass the JSON object to the foreground that called this method
                    return jo;

                } else {
                    conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);

                    try(OutputStream os = conn.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    String msg = "{}";
                    try(BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        msg = response.toString();
                    }
                    Log.d("RESPONSE", msg);
                    // use Android JSON library to parse JSON
                    JSONObject jo = new JSONObject(msg);
                    // pass the JSON object to the foreground that called this method
                    return jo;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return new JSONObject(); // should empty JSONObject upon encountering an exception
            }
        }
    }

    // This helper method passes the strings to node and runs createNewUser to add new user info.
    public void postUserProfile(String jsonInputString) {
        try {
            // 10.0.2.2 is the host machine as represented by Android Virtual Device
            URL url = new URL("http://10.0.2.2:3000/createNewUser");
            AccessWebTask task = new AccessWebTask("POST", jsonInputString);
            task.execute(url);
            return;
        } catch (Exception e) {
            // empty return upon encountering an exception
            Toast.makeText(this, "Signup successful", LENGTH_LONG).show();
            e.printStackTrace();
            return;
        }
    }

    public void onSignupButtonClick(View v) throws IOException, JSONException {
        boolean valid = validateData();
        if (valid) {

            String nameInput = name.getText().toString();
            valueMap.put("name", nameInput);
            String schoolInput = school.getText().toString();
            valueMap.put("school", schoolInput);
            String emailInput = email.getText().toString();
            valueMap.put("email", emailInput);
            String passwordInput = password.getText().toString();
            valueMap.put("password", passwordInput);

            //set anonymous profile pic
            Random random = new Random();
            int randomNum = random.nextInt(4);
            Bitmap bm;
            switch(randomNum) {
                case 0:
                    bm = BitmapFactory.decodeResource(getResources(), R.drawable.anonymous_user1);
                    break;
                case 1:
                    bm = BitmapFactory.decodeResource(getResources(), R.drawable.anonymous_user2);
                    break;
                case 2:
                    bm = BitmapFactory.decodeResource(getResources(), R.drawable.anonymous_user3);
                    break;
                default:
                    bm = BitmapFactory.decodeResource(getResources(), R.drawable.anonymous_user4);
            }
            bm = Bitmap.createScaledBitmap(bm, 500, 500, true);
            String base64Pic = bitMapToBase64String(bm);
            valueMap.put("profilePic", base64Pic);

            String jsonInputString = convertToJsonString(valueMap);

            // This method call should end up uploading the information to the database
            postUserProfile(jsonInputString);

            Toast.makeText(this, "Signup successful", LENGTH_LONG).show();

            // just go back to Login Activity
            Intent i = new Intent(this, LoginActivity.class);
            startActivityForResult(i, LOGIN_ACTIVITY_ID);
        }
    }

    public String bitMapToBase64String(Bitmap profPicBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        profPicBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        return encodedImage;
    }

    public String convertToJsonString(Map<String, String> valueMap) throws JSONException {
        JSONObject jsonInput = new JSONObject();
        for (String key : valueMap.keySet()) {
            jsonInput.put(key, valueMap.get(key));
        }
        return jsonInput.toString();
    }


    // This helper method gathers the user data to be parsed when a login attempt is made.
    public JSONObject getUserProfile(String email) {
        try {
            // 10.0.2.2 is the host machine as represented by Android Virtual Device
            URL url = new URL("http://10.0.2.2:3000/search_user?email=" + email);
            AccessWebTask task = new AccessWebTask("GET");
            task.execute(url);
            return task.get(); // waits for doInBackground to finish, then gets the return value
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject(); // return empty JSON Object upon encountering an exception
        }
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
        } else {
            JSONObject check_for_user = getUserProfile(email.getText().toString());
            if (check_for_user.length() !=  0) {
                valid = false;
                email.setError("This email has already been used!");
            }
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

