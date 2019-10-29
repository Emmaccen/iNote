package com.echo.iNote;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void mailUs(View view){
        String [] email = {"techcentered@gmail.com"};
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra(Intent.EXTRA_EMAIL,email);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT,"Contact From iNote");
        if(intent.resolveActivity(getPackageManager())!= null){
            startActivity(intent);
        };
    }
    public void webServices(View view){
        Uri webPage = Uri.parse("wonderful-mclean-9eb859.netlify.com");
        Intent intent = new Intent(Intent.ACTION_VIEW,webPage);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }
    public void share(View view){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/*");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.check_out_iNote) + "market://details?id=" + getPackageName());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void rateOrCheckForUpdates(View view){
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getPackageName())));
        }catch (ActivityNotFoundException notFound){
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/app/details?id=" +getPackageName())));
        }
    }
    public void openInstagramPage(View view){
        Toast.makeText(this, "Insta Page Note Ready", Toast.LENGTH_SHORT).show();
    }
}

