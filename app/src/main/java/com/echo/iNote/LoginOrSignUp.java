package com.echo.iNote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

public class LoginOrSignUp extends AppCompatActivity {
ConnectivityManager connection;
FirebaseFirestore firestore;
FirebaseAuth firebaseAuth;
FirebaseUser user;
EditText email,password;
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
     String emailInput = email.getText().toString().trim();
      String  passwordInput = password.getText().toString().trim();
      if(connection != null){
          NetworkInfo info = connection.getActiveNetworkInfo();
          if(info != null && info.isConnected()){
              if(user == null){
                  if(emailInput.isEmpty() || passwordInput.isEmpty()){
                      Toast.makeText(this, getString(R.string.all_fields_are_required), Toast.LENGTH_SHORT).show();
                  }else{
                      firebaseAuth.signInWithEmailAndPassword(emailInput,passwordInput).addOnCompleteListener(
                              new OnCompleteListener<AuthResult>() {
                                  @Override
                                  public void onComplete(@NonNull Task<AuthResult> task) {
                                      if(task.isSuccessful()){
                                          startActivity(new Intent(LoginOrSignUp.this,HomePage.class)
                                                  .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                      }
                                  }
                              }
                      ).addOnFailureListener(new OnFailureListener() {
                          @Override
                          public void onFailure(@NonNull Exception e) {
                              Toast.makeText(LoginOrSignUp.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                          }
                      });
                  }
              }
          }else{
              Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
          }
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
}
