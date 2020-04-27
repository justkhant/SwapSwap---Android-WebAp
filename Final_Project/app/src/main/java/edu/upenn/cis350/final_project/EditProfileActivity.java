package edu.upenn.cis350.final_project;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.util.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;
import java.util.concurrent.ThreadLocalRandom;

public class EditProfileActivity extends AppCompatActivity {
    static final int PROFILE_ACTIVITY_ID = 50;
    private static final int GET_FROM_GALLERY = 51;
    private static final int CAMERA_ACTION_PICK_REQUEST_CODE = 52;
    private static final int ONLY_CAMERA_REQUEST_CODE = 53;
    private static final int ONLY_STORAGE_REQUEST_CODE = 54;
    private static final int CAMERA_STORAGE_REQUEST_CODE = 55;

    private TextView bio;
    private TextView rank;
    private TextView name;
    private TextView phoneNumber;
    private TextView email;
    private TextView points;
    private TextView school;
    private String user_email;
    private ImageView profilePic;

    String currentPhotoPath = "";
    boolean isCamera = true;
    Map<String, String> valueMap = new HashMap<>();
    private int randomNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent curr_intent = getIntent();
        user_email = SingletonVariableStorer.getCurrUserInstance();

       //fill out info

        bio = findViewById(R.id.edit_about_me_body);
        bio.setText(curr_intent.getStringExtra("bio"));

        email = findViewById(R.id.edit_email_body);
        email.setText(user_email);

        school = findViewById(R.id.edit_school_body);
        school.setText(curr_intent.getStringExtra("school"));

        name = findViewById(R.id.edit_full_name);
        name.setText(curr_intent.getStringExtra("name"));

        phoneNumber = findViewById(R.id.edit_phone_num_body);
        phoneNumber.setText(curr_intent.getStringExtra("phoneNumber"));

        //find related image views
        profilePic = findViewById(R.id.profile_pic);
        String base64Image = curr_intent.getStringExtra("profilePic");
        if (base64Image.length() == 0) {
            profilePic.setImageResource(R.drawable.profile_pic);
        } else {
            try {
                profilePic.setImageBitmap(base64StringToBitmap(base64Image));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public Bitmap base64StringToBitmap(String base64Image) throws JSONException {
        String cleanImage = base64Image.replace("data:image/png;base64,", "")
                .replace("data:image/jpeg;base64,", "");
        byte[] decodedString = Base64.decode(cleanImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    // inner class used to access the web
    public class AccessWebTask extends AsyncTask<URL, String, JSONObject> {
        String jsonInputString;

        public AccessWebTask(String jsonInputString) {
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
                conn.setRequestMethod("POST"); // send HTTP request
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

            } catch (Exception e) {
                e.printStackTrace();
                return new JSONObject(); // should empty JSONObject upon encountering an exception
            }
        }

        //This method is called in foreground after doInBackground finishes.
        protected void onPostExecute(String msg) {
            // not implemented but you can use this if you’d like//
        }

    }

    // This helper method gathers the user data to be parsed when a login attempt is made.
    public void updateUserProfile(String jsonInputString) {
        try {
            // 10.0.2.2 is the host machine as represented by Android Virtual Device

            URL url = new URL("http://10.0.2.2:3000/update_profile/");
            AccessWebTask task = new AccessWebTask(jsonInputString);
            task.execute(url).get();
            Toast.makeText(this, "Update successful", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Update error", Toast.LENGTH_SHORT).show();
        }
    }

    public String bitMapToBase64String(Bitmap profPicBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        profPicBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        return encodedImage;
    }

    public void onSaveButtonClick(View v) throws IOException, JSONException {

        EditText schoolEditText = (EditText) findViewById(R.id.edit_school_body);
        String schoolInput = schoolEditText.getText().toString();
        valueMap.put("school", schoolInput);

        EditText bioEditText = (EditText) findViewById(R.id.edit_about_me_body);
        String bioInput = bioEditText.getText().toString();
        valueMap.put("bio", bioInput);

        EditText pnEditText = (EditText) findViewById(R.id.edit_phone_num_body);
        String pnInput = pnEditText.getText().toString();
        valueMap.put("phoneNumber", pnInput);

        String s_email = email.getText().toString();
        valueMap.put("email", s_email);

        profilePic.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) profilePic.getDrawable();
        Bitmap profPicBitmap = drawable.getBitmap();

        //set POST json input to send
        if (profPicBitmap != null) {
            String encodedImage = bitMapToBase64String(profPicBitmap);
            valueMap.put("profilePic", encodedImage);
        }

        String jsonInputString = convertToJsonString(valueMap);
        Log.d("JSON INPUT STRING", jsonInputString);

        if (validateData(pnInput) || pnInput.length() == 0) {
            // This method call should end up uploading the information to the database
            updateUserProfile(jsonInputString);

            Intent i = new Intent(this, UserProfileActivity.class);
            //pass Intent to activity using specified code
            startActivityForResult(i, PROFILE_ACTIVITY_ID);
        }
    }

    public String convertToJsonString(Map<String, String> valueMap) throws JSONException {
        JSONObject jsonInput = new JSONObject();
        for (String key : valueMap.keySet()) {
            jsonInput.put(key, valueMap.get(key));
        }
        return jsonInput.toString();
    }

    boolean validateData(String pnInput) {
        boolean valid = true;

        if (!Patterns.PHONE.matcher(pnInput).matches()) {
            valid = false;
            phoneNumber.setError("You must input a valid phone number format.");
        }
        return valid;
    }

    public void onCancelButtonClick(View v) {
        // Let the user know the attempt to edit the profile was cancelled
        Toast.makeText(this, "Profile edit cancelled", LENGTH_LONG).show();

        Intent i = new Intent(this, UserProfileActivity.class);

        //pass Intent to activity using specified code
        startActivityForResult(i, PROFILE_ACTIVITY_ID);
    }

    public void onUploadButtonClick(View v) {
        isCamera = false;
        if(checkSelfPermissions(this)) {
            openImagesDocument();
        }
    }

    public void onCameraButtonClick(View v) throws IOException {
        isCamera = true;
        if (checkSelfPermissions(this)) {
            openCamera();
        }
    }

    public void onRenewPicButtonClick(View v) {
        randomNum = (randomNum + 1) % 4;
        profilePic.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) profilePic.getDrawable();
        Bitmap profPicBitmap = drawable.getBitmap();

        switch(randomNum) {
            case 0:
                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.anonymous_user1);
                if (!profPicBitmap.equals(bm)) {
                    profilePic.setImageResource(R.drawable.anonymous_user1);
                } else {
                    randomNum++;
                    profilePic.setImageResource(R.drawable.anonymous_user2);
                }
                break;
            case 1:
                profilePic.setImageResource(R.drawable.anonymous_user2);
                break;
            case 2:
                profilePic.setImageResource(R.drawable.anonymous_user3);
                break;
            default:
                profilePic.setImageResource(R.drawable.anonymous_user4);
        }
        profilePic.invalidate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "This app needs Storage access in order to store your profile picture.", LENGTH_LONG);
                finish();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "This app needs Camera access in order to take profile picture.", LENGTH_LONG);
                finish();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "This app needs needs Camera and Storage access in order to take profile picture.", LENGTH_LONG);
                finish();
            }
        } else if (requestCode == ONLY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "This app needs Camera access in order to take profile picture.", LENGTH_LONG);
                finish();
            }
        } else if (requestCode == ONLY_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "This app needs Storage access in order to store your profile picture.", LENGTH_LONG);
                finish();
            }
        }

        if (isCamera) {
            try {
                openCamera();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            openImagesDocument();
        }
    }


    public boolean checkSelfPermissions(@NonNull Activity activity) {
        if (activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CAMERA_STORAGE_REQUEST_CODE);
            return false;
        } else if (activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity, new String[]{Manifest.permission.CAMERA}, ONLY_CAMERA_REQUEST_CODE);
            return false;
        } else if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ONLY_STORAGE_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri sourceUri = data.getData();
            File file = null;
            try {
                file = getImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Uri destinationUri = Uri.fromFile(file);
            openCropActivity(sourceUri, destinationUri);
        } else if (requestCode == CAMERA_ACTION_PICK_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = Uri.parse(currentPhotoPath);
            openCropActivity(uri, uri);
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            Uri uri = UCrop.getOutput(data);
            try {
                showImage(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void showImage(Uri imageUri) throws FileNotFoundException {
        File file = new File(FileUtils.getPath(this, imageUri));
        InputStream inputStream = new FileInputStream(file);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        profilePic.setImageBitmap(bitmap);

    }


    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(profilePic.getMaxWidth(), profilePic.getMaxHeight())
                .withAspectRatio(5f, 5f)
                .start(this);
    }

    private void openImagesDocument() {
        Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pictureIntent.setType("image/*");
        pictureIntent.addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] mimeTypes = new String[]{"image/jpeg", "image/png"};
            pictureIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }
        startActivityForResult(Intent.createChooser(pictureIntent,"Select Picture"), GET_FROM_GALLERY);  // 4
    }

    private void openCamera() throws IOException {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getImageFile();
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID.concat(".provider"), file);
        else
            uri = Uri.fromFile(file);
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(pictureIntent, CAMERA_ACTION_PICK_REQUEST_CODE);
    }

    private File getImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM).getAbsolutePath()
        );

        File file = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        currentPhotoPath = "file:" + file.getAbsolutePath();
        return file;
    }


}
