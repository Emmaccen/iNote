package com.echo.iNote;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
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
import com.google.i18n.phonenumbers.PhoneNumberUtil;

public class LoginOrSignUp extends AppCompatActivity {
ConnectivityManager connection;
    FirebaseFirestore firestore;
    EditText forgotPasswordText;
FirebaseAuth firebaseAuth;
FirebaseUser user;
EditText email,password;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_sign_up);
        connection = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = firebaseAuth.getCurrentUser();

    }

public void signInUser(View view){
    HideKeyboard.hideKeyboard(this);
    progressDialog = new ProgressDialog(this);
    progressDialog.setTitle("Processing...");
    progressDialog.setCancelable(true);
     String emailInput = email.getText().toString().trim();
      String  passwordInput = password.getText().toString().trim();
      if(connection != null){
          NetworkInfo info = connection.getActiveNetworkInfo();
          if(info != null && info.isConnected()){
              if(user == null){
                  if(emailInput.isEmpty() || passwordInput.isEmpty()){
                      Snackbar.make(view, getString(R.string.all_fields_are_required), Snackbar.LENGTH_LONG);
                  }else{
                      progressDialog.show();
                      firebaseAuth.signInWithEmailAndPassword(emailInput,passwordInput).addOnCompleteListener(
                              new OnCompleteListener<AuthResult>() {
                                  @Override
                                  public void onComplete(@NonNull Task<AuthResult> task) {
                                      if(task.isSuccessful()){
                                          progressDialog.cancel();
                                          startActivity(new Intent(LoginOrSignUp.this,HomePage.class)
                                                  .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                      }
                                  }
                              }
                      ).addOnFailureListener(new OnFailureListener() {
                          @Override
                          public void onFailure(@NonNull Exception e) {
                              progressDialog.cancel();
                              Toast.makeText(LoginOrSignUp.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                          }
                      });
                  }
              }
          }else{
              Snackbar.make(view, getString(R.string.please_check_your_internet_connection), Snackbar.LENGTH_LONG).show();
          }
      } else {
          Snackbar.make(view, getString(R.string.please_check_your_internet_connection), Snackbar.LENGTH_LONG).show();
      }

}
    public void moveToRegistrationActivity(View view){
        startActivity(new Intent(this,Register.class));
    }
    public int getCountryCode(){
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String countryIso = telephonyManager.getSimCountryIso().toUpperCase();
        return PhoneNumberUtil.getInstance().getCountryCodeForRegion(countryIso);
    }
    public void skipToHome(View view) {
        startActivity(new Intent(LoginOrSignUp.this, HomePage.class).setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void requestPasswordReset(View view) {
        if (connection != null) {
            NetworkInfo info = connection.getActiveNetworkInfo();
            if (info != null) {
                View alertLayout = getLayoutInflater().inflate(R.layout.login_forgot_password_layout, null);
                forgotPasswordText = alertLayout.findViewById(R.id.forgot_email_update);
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Password Reset");
                alert.setView(alertLayout);
                alert.setIcon(getDrawable(R.drawable.private_note_reset_password_icon_24dp));
                alert.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        Toast.makeText(LoginOrSignUp.this, "Processing ...", Toast.LENGTH_LONG).show();
                        final String email = forgotPasswordText.getText().toString().trim();
                        if (!email.isEmpty()) {
                            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                    .addOnCompleteListener(
                                            new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        dialogInterface.dismiss();
                                                        Toast.makeText(LoginOrSignUp.this, "Password Reset Email Sent", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }
                                    ).addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialogInterface.dismiss();
                                            Toast.makeText(LoginOrSignUp.this, "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                            );

                        } else {
                            Toast.makeText(LoginOrSignUp.this, "No Input Observed ...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alert.create();
                alert.show();
            } else {
                Toast.makeText(this, getString(R.string.please_check_your_internet_connection), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.please_check_your_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }
}
