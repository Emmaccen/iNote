package com.example.iNote;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    Uri imageUri;
    CircleImageView profileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    public void setProfilePicture(View view){
        startActivityForResult(new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI),300);{
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 300){
            imageUri = data.getData();
            profileImage = findViewById(R.id.profile_image);
            profileImage.setImageURI(imageUri);
//            Toast.makeText(this,imageUri.getPath(),Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show();
        }
    }
}
