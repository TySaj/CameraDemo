package com.example.camerademo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Required result codes specific to this app
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    // Objects for layout
    Button btnCapture;
    ImageView imagePreview;

    // Stores the image file information. Includes a URI and fragment.
    Uri image_uri;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Associated objects with elements on design layout.
        imagePreview = findViewById(R.id.imagePreview);
        btnCapture = findViewById(R.id.btnCapture);

        // Eventlistener when the capture button is clicked.
        btnCapture.setOnClickListener(view -> {
            // Check if permissions are enabled for camera and writing to external storage.
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            {
                // Request permissions be enabled with popup.
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            }
            else
            {
                // Permissions are already enabled, open the camera!
                openCamera();
            }
        });
    }

    // Handles the operating of the camera.
    private void openCamera()
    {
        // Create values that will be used to establish the image URI
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");

        // Set the uniform resource identifier, acts as an address to refer to the image.
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // Camera intent uses MediaStore class, allows access to media available on external storage.
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Add extended data to the intent in the form of the image uri.
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);

        // Use the camera intent along with the use case for capturing an image to start the activity.
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    // Determines whether a permission is granted or denied.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Switch case is necessary for when there are multiple request codes. In this case there is only one.
        switch (requestCode)
        {
            case PERMISSION_CODE:{
                // Using the results from checkPermissions, determine if the user selected to allow them or not.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                {
                    // Permissions were accepted from popup.
                    openCamera();
                }
                else
                {
                    // Permissions were declined from popup.
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Called when photo is taken from camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check to ensure the requestCode didn't fail.
        if (resultCode == RESULT_OK)
        {
            // Sets photo captured to the image view.
            imagePreview.setImageURI(image_uri);
        }
    }
}