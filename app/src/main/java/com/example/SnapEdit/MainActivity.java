package com.example.SnapEdit;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;

public class MainActivity extends AppCompatActivity {

    //Initialize variable
    Button btpPick;
    ImageView imageView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Assign variable
        btpPick = findViewById(R.id.btnPick);
        imageView = findViewById(R.id.imageView);

        btpPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create method
                checkPermission();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void checkPermission() {
        //Initialize permission
        int permission = ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //Check condition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //When device version is greater that equal to version 10
            //Create method
            pickImage();
        } else {
            //When device version is below version 10
            //Check condition
            if (permission != PackageManager.PERMISSION_GRANTED) {
                //When permission is not granted
                //Request permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        100);
            } else {
                //When permission is granted
                //Call method;
                pickImage();
            }
        }
    }

    private void pickImage() {
        //Initialize intent
        Intent intent = new Intent(Intent.ACTION_PICK);
        //set type
        intent.setType("image/*");
        //Start activity for result
        startActivityForResult(intent, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Check condition
        if (requestCode == 100 && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            //When permission is denied
            //Call method
            pickImage();
        }else {
            //When permission is denied
            //Display toast
            Toast.makeText(getApplicationContext(),
                    "Permission Denied", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check if the result is okay and if the request code matches
        if (resultCode == RESULT_OK && data != null) {
            // Get the URI of the selected image
            Uri uri = data.getData();
            // Check the request code
            switch (requestCode) {
                case 100:
                    // If the request code is for picking an image
                    // Start the DsPhotoEditorActivity
                    startPhotoEditorActivity(uri);
                    break;
                case 101:
                    // If the request code is for editing with DsPhotoEditor
                    // Set the edited image to the ImageView
                    imageView.setImageURI(uri);
                    // Display a toast message
                    Toast.makeText(getApplicationContext(), "Photo Saved", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


    private void startPhotoEditorActivity(Uri uri) {
        // Create an intent to start the DsPhotoEditorActivity
        Intent intent = new Intent(MainActivity.this, DsPhotoEditorActivity.class);
        // Set the URI of the image to be edited
        intent.setData(uri);
        // Set the output directory name
        intent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "Images");
        // Set toolbar color (optional)
        // Start the DsPhotoEditorActivity for result
        startActivityForResult(intent, 101);
    }
}

