package com.echo.iNote;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

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
    StorageReference storageReference;
    StorageTask uploadTask;
    DocumentReference reference;
    ProgressDialog progressDialog;
    ConnectivityManager connection;

    private AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        progressDialog = new ProgressDialog(this);
        profileImage = findViewById(R.id.profile_image);
        profileEmail = findViewById(R.id.profile_email_edit_text_view);
        profilePhoneNumber = findViewById(R.id.profile_phone_edit_text_view);
        profileEmalText = findViewById(R.id.profile_email_text_view);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        connection = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        loadProfileDetails();

    }

    public void updateProfile(View view) {
        startActivity(new Intent(this, UpdateProfileActivity.class));
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
                            } else {
                                Glide.with(getApplicationContext())
                                        .load(userValues.getImage()).placeholder(getDrawable(R.drawable.user_profile_picture))
                                        .into(profileImage);
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
            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(this, "Previous Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.setTitle("Uploading...");
                progressDialog.show();
                uploadImage();
            }
//            Toast.makeText(this,imageUri.getPath(),Toast.LENGTH_LONG).show();

        }else{
            Toast.makeText(this, "Unable to Process request...", Toast.LENGTH_SHORT).show();
        }
    }

    public String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    public void uploadImage() {
        if (imageUri != null) {
           /* if(connection != null){
                NetworkInfo info = connection.getActiveNetworkInfo();
                if(info != null ){
                    if(!info.isConnected())
                Toast.makeText(this, getString(R.string.please_check_your_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }*/
            if (user != null) {
                final StorageReference fileReference = storageReference.child(user.getUid()
                        + "." + getFileExtension(imageUri));
                uploadTask = fileReference.putFile(imageUri);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return fileReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            String mUri = downloadUri.toString();

                            reference = firestore.collection("Users").document(user.getUid());
                            reference.update("image", mUri);
                            progressDialog.cancel();
                            Toast.makeText(ProfileActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(ProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(this, getString(R.string.no_user_account_detected), Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
            }
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            progressDialog.cancel();
        }

    }
}
