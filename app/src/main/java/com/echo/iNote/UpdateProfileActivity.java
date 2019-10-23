package com.echo.iNote;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileActivity extends AppCompatActivity {
    // update clickers
    TextView emailUpdate, phoneNumberUpdate, passwordUpdate, forgotPasswordUpdate;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    DocumentReference document;
    FirebaseFirestore firestore;
    CircleImageView profileImage;
    ConnectivityManager connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        connection = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        profileImage = findViewById(R.id.profile_image);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();


        emailUpdate = findViewById(R.id.email_update);
        phoneNumberUpdate = findViewById(R.id.phone_number_update);
        passwordUpdate = findViewById(R.id.password_update);
        forgotPasswordUpdate = findViewById(R.id.forgot_password_reset);

        loadProfileDetails();
    }

    public void moveToProcessingActivity(View view) {
        int id = view.getId();
        Intent intent = new Intent(this, ProcessUpdate.class);

        if (id == R.id.email_update) {
            intent.putExtra("email", "email");
            startActivity(intent);
        } else if (id == R.id.phone_number_update) {
            intent.putExtra("number", "number");
            startActivity(intent);
        } else if (id == R.id.password_update) {
            intent.putExtra("password", "password");
            startActivity(intent);
        } else if (id == R.id.forgot_password_reset) {
            if (connection != null) {
                NetworkInfo info = connection.getActiveNetworkInfo();
                if (info != null) {
                    if (user != null) {
                        final ProgressDialog progressDialog = new ProgressDialog(this);
                        progressDialog.setTitle("Executing Command ...");
                        progressDialog.show();
                        firebaseAuth.sendPasswordResetEmail(user.getEmail()).addOnCompleteListener(
                                new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.cancel();
                                            startActivity(new Intent(UpdateProfileActivity.this, HomePage.class)
                                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                            Toast.makeText(UpdateProfileActivity.this, getString(R.string.email_sent), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                        ).addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.cancel();
                                        Toast.makeText(UpdateProfileActivity.this, "Error : " + "Unable To Process Request", Toast.LENGTH_LONG).show();
                                    }
                                }
                        );
                    } else {
                        Toast.makeText(this, getString(R.string.no_user_account_detected), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.please_check_your_internet_connection), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.please_check_your_internet_connection), Toast.LENGTH_LONG).show();

            }
        }
    }

    public void loadProfileDetails() {
        if (user != null) {
            document = firestore.collection("Users").document(user.getUid());
            document.get().addOnSuccessListener(
                    new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            UserContract userValues = documentSnapshot.toObject(UserContract.class);
                            assert userValues != null;
                            if (userValues.getImage().equals("default")) {
                                profileImage.setImageDrawable(getDrawable(R.drawable.user_profile_picture));
                            } else {
                                Glide.with(getApplicationContext())
                                        .load(userValues.getImage()).placeholder(getDrawable(R.drawable.user_profile_picture))
                                        .into(profileImage);
                            }

                        }
                    }
            );

        } else {
            profileImage.setImageDrawable(getDrawable(R.drawable.user_profile_picture));
        }
    }

    public void moveToHome(View view) {
        startActivity(new Intent(UpdateProfileActivity.this, HomePage.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
