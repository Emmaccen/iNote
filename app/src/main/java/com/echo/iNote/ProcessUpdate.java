package com.echo.iNote;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class ProcessUpdate extends AppCompatActivity {
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    DocumentReference document;
    FirebaseFirestore firestore;
    //profile update handlers
    TextView finalEmail, finalPhoneNumber, oldPassword, newPassword;
    //the cardViews for processing
    CardView card1, card2, card3, card4;
    ConnectivityManager connectivityManager;
    NetworkInfo info;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_update);
        intent = getIntent();

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();


        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);

        finalEmail = findViewById(R.id.final_email_update);
        finalPhoneNumber = findViewById(R.id.final_phone_number);
        newPassword = findViewById(R.id.final_new_password);
        oldPassword = findViewById(R.id.final_old_password);
        hideViews(intent);

    }

    public void hideViews(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (Objects.equals(bundle.get("email"), "email")) {
                card2.setVisibility(View.GONE);
                card3.setVisibility(View.GONE);
                card4.setVisibility(View.GONE);
            } else if (Objects.equals(bundle.get("number"), "number")) {
                card1.setVisibility(View.GONE);
                card3.setVisibility(View.GONE);
                card4.setVisibility(View.GONE);


            } else if (Objects.equals(bundle.get("password"), "password")) {
                card1.setVisibility(View.GONE);
                card2.setVisibility(View.GONE);


            }
        }
    }

    public void validateAction(View view) {
        Bundle bundle = intent.getExtras();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Processing...");

        if (connectivityManager != null) {
            info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {


                if (bundle != null) {
                    if (Objects.equals(bundle.get("email"), "email")) {
                        card2.setVisibility(View.GONE);
                        card3.setVisibility(View.GONE);
                        card4.setVisibility(View.GONE);
                        if (user != null) {
                            progressDialog.show();
                            if (!finalEmail.getText().toString().isEmpty()) {
                                String email = finalEmail.getText().toString().trim();
                                user.updateEmail(email).addOnCompleteListener(
                                        new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    progressDialog.cancel();
                                                    startActivity(new Intent(ProcessUpdate.this, HomePage.class)
                                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                    Snackbar.make(Objects.requireNonNull(getCurrentFocus()), "Email Updated Successfully", Snackbar.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                ).addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.cancel();
                                                Toast.makeText(ProcessUpdate.this, "Error : " + e.toString(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                );
                            } else {
                                progressDialog.cancel();
                                Snackbar.make(Objects.requireNonNull(getCurrentFocus()), getString(R.string.all_fields_are_required), Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            progressDialog.cancel();
                            Snackbar.make(Objects.requireNonNull(getCurrentFocus()), getString(R.string.no_user_account_detected), Snackbar.LENGTH_LONG).show();
                        }

                    } else if (Objects.equals(bundle.get("number"), "number")) {
                        card1.setVisibility(View.GONE);
                        card3.setVisibility(View.GONE);
                        card4.setVisibility(View.GONE);

                        if (finalPhoneNumber.getText().toString().isEmpty() || finalPhoneNumber.getText().length() < 4) {
                            Snackbar.make(getCurrentFocus(), "Please Enter A Valid Number", Snackbar.LENGTH_LONG).show();
                        } else {
                            if (user != null) {
                                progressDialog.show();

                                DocumentReference documentReference = firestore.collection("Users").document(user.getUid());
                                documentReference.get().addOnCompleteListener(
                                        new OnCompleteListener<DocumentSnapshot>() {

                                            private String myNumber;

                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    progressDialog.setTitle("Fetching Number From Database ...");
                                                    myNumber = task.getResult().get("phoneNumber").toString();
                                                    progressDialog.cancel();
                                                    Toast.makeText(ProcessUpdate.this, "Update Started", Toast.LENGTH_LONG).show();
                                                    progressDialog.setTitle("Updating...");
                                                    progressDialog.show();
                                                    CollectionReference collectionReference = firestore.collection("Users");
                                                    collectionReference.get().addOnCompleteListener(
                                                            new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        for (QueryDocumentSnapshot docs : task.getResult()) {
                                                                            UserContract iNoteUsers = docs.toObject(UserContract.class);
                                                                            if (iNoteUsers.getPhoneNumber().equals(myNumber)) {
                                                                                String number = finalPhoneNumber.getText().toString().trim();
                                                                                docs.getReference().update("phoneNumber", number);
                                                                                progressDialog.cancel();
                                                                                startActivity(new Intent(ProcessUpdate.this, HomePage.class)
                                                                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                                                Toast.makeText(ProcessUpdate.this, "Phone Number Updated Successfully", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    } else {
                                                                        progressDialog.cancel();
                                                                        Toast.makeText(ProcessUpdate.this, "Please Try Again Later...", Toast.LENGTH_LONG).show();

                                                                    }
                                                                }
                                                            }
                                                    ).addOnFailureListener(
                                                            new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    progressDialog.cancel();
                                                                    Toast.makeText(ProcessUpdate.this, "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                    );
                                                }
                                            }

                                        }
                                ).addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.cancel();
                                                Snackbar.make(Objects.requireNonNull(getCurrentFocus()), "Error : " + e.toString(), Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                );
                            } else {
                                Snackbar.make(Objects.requireNonNull(getCurrentFocus()), getString(R.string.no_user_account_detected), Snackbar.LENGTH_LONG).show();
                            }

                        }

                    } else if (Objects.equals(bundle.get("password"), "password")) {
                        card1.setVisibility(View.GONE);
                        card2.setVisibility(View.GONE);

                        if (user != null) {
                            final String newPass = newPassword.getText().toString().trim();
                            String oldPass = oldPassword.getText().toString().trim();

                            if (newPass.length() < 6 || oldPass.length() < 6) {
                                Snackbar.make(Objects.requireNonNull(getCurrentFocus()), getString(R.string.passowrd_must_be), Snackbar.LENGTH_LONG).show();
                            } else {
                                String email = user.getEmail();
                                AuthCredential credential = EmailAuthProvider.getCredential(email, oldPass);
                                user.reauthenticate(credential).addOnCompleteListener(
                                        new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressDialog.setTitle("Verifying ...");
                                                progressDialog.show();
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ProcessUpdate.this, "OldPassword Accepted", Toast.LENGTH_LONG).show();
                                                    Objects.requireNonNull(firebaseAuth.getCurrentUser()).updatePassword(newPass).addOnCompleteListener(
                                                            new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(ProcessUpdate.this, "Done, Password Updated", Toast.LENGTH_LONG).show();
                                                                        progressDialog.cancel();
                                                                        startActivity(new Intent(ProcessUpdate.this, HomePage.class)
                                                                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                                    }
                                                                }
                                                            }
                                                    ).addOnFailureListener(
                                                            new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    progressDialog.cancel();
                                                                    Toast.makeText(ProcessUpdate.this, "Error : " + "Unable To Verify Old Password", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                    );
                                                }/*
                                                    progressDialog.setTitle("Processing Update ...");
                                                    Objects.requireNonNull(firebaseAuth.getCurrentUser()).updatePassword(newPass).addOnCompleteListener(
                                                            new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        Toast.makeText(ProcessUpdate.this, "Done, Password Updated", Toast.LENGTH_LONG).show();
                                                                        progressDialog.cancel();
                                                                    }
                                                                }
                                                            }
                                                    ).addOnFailureListener(
                                                            new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    progressDialog.cancel();
                                                                    Toast.makeText(ProcessUpdate.this, "Error : " + e.toString(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                    );*/
                                            }

                                        }
                                ).addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.cancel();
                                                Toast.makeText(ProcessUpdate.this, "Error : " + e.toString(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                );
                            }

                        } else {
                            progressDialog.cancel();
                            Snackbar.make(Objects.requireNonNull(getCurrentFocus()), getString(R.string.no_user_account_detected), Snackbar.LENGTH_LONG).show();
                        }

                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.please_check_your_internet_connection), Toast.LENGTH_LONG).show();
            }
        }

    }

    public void moveToHome(View view) {
        startActivity(new Intent(ProcessUpdate.this, HomePage.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
