package com.echo.iNote;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    ConnectivityManager connectivityManager;
    NetworkStateUpdater networkReceiver;

    private ProgressDialog progressDialog;

    TextView registrationEmail, registrationPhoneNumber, registrationPassword;
    Toast toast;
    FirebaseAuth firebaseAuth;

    @Override
    public void onBackPressed() {
        if (progressDialog != null) {
            progressDialog.cancel();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        /*Don't forget to implement the networking part in the Async background task later
         * if you do? am gonna kill me */


        //Sigh :( time to start coding the networking part mehn!
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        networkReceiver = new NetworkStateUpdater();
        registerReceiver(networkReceiver, intentFilter);
//hate the four line code above, should i just wrap it in a method... its confusing me !

        registrationEmail = findViewById(R.id.registration_email);
        registrationPhoneNumber = findViewById(R.id.registration_number);
        registrationPassword = findViewById(R.id.registration_password);
        firebaseAuth = FirebaseAuth.getInstance();
    }


    public void moveToLoginActivity(View view) {
        startActivity(new Intent(this, LoginOrSignUp.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public class NetworkStateUpdater extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                //We don't care if they've connected to an alien network, we just wanna know if they've connected to any damn network ( :# mean face )

              /*  boolean isWifiAvailable = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
                boolean isMobileDataEnabled = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();*/
                if (networkInfo.isConnected()) {
//                    Toast.makeText(context, "Connection Retrieved", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "No Internet Connection...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void registerNewUser(final View view) {
        HideKeyboard.hideKeyboard(this);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Processing...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                final String email = registrationEmail.getText().toString().trim();
                final String phoneNumber = registrationPhoneNumber.getText().toString().trim();
                final String password = registrationPassword.getText().toString().trim();
                if (email.isEmpty() || password.isEmpty() || phoneNumber.isEmpty()) {
                    progressDialog.cancel();
                    Snackbar.make(view, getString(R.string.all_fields_are_required), Snackbar.LENGTH_SHORT).show();
                } else if ((password.length() <= 9 && !(password.length() >= 6))) {
                    progressDialog.cancel();
                    Snackbar.make(view, "Password must be at least 6 characters long", Snackbar.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        if (user != null) {
                                            String userId = user.getUid();
                                            FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
                                            Map<String, String> values = new HashMap<>();
                                            values.put("email", email);
                                            values.put("phoneNumber", phoneNumber);
                                            values.put("userId", userId);
                                            values.put("image", "default");
                                            fireStore.collection("Users").document(userId).set(values)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                progressDialog.cancel();
                                                                toast = Toast.makeText(Register.this, "Account Created Successfully", Toast.LENGTH_SHORT);
                                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                                toast.show();
                                                                startActivity(new Intent(Register.this, HomePage.class)
                                                                        .putExtra("email", email).putExtra("phoneNumber", phoneNumber)
                                                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                                            }
                                                        }
                                                    }).addOnFailureListener(
                                                    new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            progressDialog.cancel();
                                                            Snackbar.make(view, "Something Went Wrong", Snackbar.LENGTH_LONG).show();
                                                            Toast.makeText(Register.this, "Error : " + e, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                            );
                                        }
                                    } else {
                                        //If unable to register user
                                        progressDialog.cancel();
                                        Snackbar.make(view, "Something Went Wrong, please review filled details", Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            }
                    );
                }

            } else {
                Snackbar.make(view, "Please check your internet connection", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public void skipToHomePage(View view) {
        startActivity(new Intent(Register.this, HomePage.class).setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
