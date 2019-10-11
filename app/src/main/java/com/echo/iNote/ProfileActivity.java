package com.echo.iNote;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    Uri imageUri;
    CircleImageView profileImage;
    FirebaseUser user;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    EditText profileEmail,profilePhoneNumber;
    TextView profileEmalText;
    DocumentReference document;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileImage = findViewById(R.id.profile_image);
        profileEmail = findViewById(R.id.profile_email_edit_text_view);
        profilePhoneNumber = findViewById(R.id.profile_phone_edit_text_view);
        profileEmalText = findViewById(R.id.profile_email_text_view);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        loadProfileDetails();

    }
    public void loadProfileDetails(){
        if(user != null){
            document = firestore.collection("Users").document(user.getUid());
            String email = user.getEmail();
            profileEmail.setText(email);
            profileEmalText.setText(email);
            document.get().addOnSuccessListener(
                    new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            UserContract userValues = documentSnapshot.toObject(UserContract.class);
                            assert userValues != null;
                            String phoneNumber = userValues.getPhoneNumber();
                            profilePhoneNumber.setText(phoneNumber);
                            if(userValues.getImage().equals("default")){
                                profileImage.setImageDrawable(getDrawable(R.drawable.user_profile_picture));
                            }

                        }
                    }
            );

        }else {

            profileImage.setImageDrawable(getDrawable(R.drawable.user_profile_picture));
        }
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
