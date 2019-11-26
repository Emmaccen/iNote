package com.echo.iNote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class OnBoardingScreen extends AppCompatActivity {
ArrayList<WelcomeScreenContract> list;
WelcomeScreenViewPager adapter;
static SharedPreferences preferences;
static final String WELCOME_SCREEN_KEY = "welcome_screen_key";
static final String WELCOME_PREFERENCE_KEY = "welcome_pref_key";
ViewPager viewPager;
LinearLayout linear;
Button next,prev;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_screen);
        ShownOnboardingScreen();

        list = new ArrayList<>();
        viewPager  = findViewById(R.id.welcome_screen_view_pager);
        linear = findViewById(R.id.dots_layout);
        next = findViewById(R.id.welcome_next_button);
        prev = findViewById(R.id.welcome_prev_button);
        prev.setVisibility(View.INVISIBLE);
        initializeDots(0);
        list.add(new WelcomeScreenContract("Cloud Storage", getString(R.string.cloud_discription), R.drawable.welcome_cloud_sync));
        list.add(new WelcomeScreenContract("In-App Note Sharing", getString(R.string.in_app_note_discription), R.drawable.welcome_in_app_messaging));
        list.add(new WelcomeScreenContract("Log In", getString(R.string.Log_in_discription), R.drawable.welcome_log_in));
                adapter = new WelcomeScreenViewPager(list,this);
                viewPager.setAdapter(adapter);
                viewPager.addOnPageChangeListener(
                        new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                            }

                            @Override
                            public void onPageSelected(int position) {
                               initializeDots(position);
                               if(position == 0){
                                   prev.setVisibility(View.INVISIBLE);

                               }else{
                                   prev.setVisibility(View.VISIBLE);
                               }
                               if(position == 2){
//                                   next.setVisibility(View.INVISIBLE);
                                   next.setText("Finish");
                                }else{
                                   next.setText("Next");
                               }
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                            }
                        }
                );
        }

    private void ShownOnboardingScreen() {
        preferences = getSharedPreferences(WELCOME_PREFERENCE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(WELCOME_SCREEN_KEY,true);
        editor.apply();
    }


    public void initializeDots(int position){
        TextView [] dots  = new TextView[3];
        linear.removeAllViews();
        for(int i = 0; i<dots.length ; i ++){
            dots[i] = new TextView(this);
            /*TODO
            *  remember to set a flag on the Html,fromHtml code below(remove deprecation)*/
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextColor(getResources().getColor(R.color.colorAccent, Resources.getSystem().newTheme()));
            dots[i].setTextSize(30);
            linear.addView(dots[i]);
    }
        dots[position].setTextColor(Color.parseColor("#000000"));
    }
    public void onNextButtonSelected(View view){
        if(viewPager.getCurrentItem() <2){
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }else{
            startActivity(new Intent(this,LoginOrSignUp.class)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        if(viewPager.getCurrentItem() == 2){
            next.setText("Finish");
        }else {
            next.setText("Next");
        }
        if(prev.getVisibility() == View.INVISIBLE){
                prev.setVisibility(View.VISIBLE);
            }
        }
    public void onPrevButtonPressed(View view){
        if(viewPager.getCurrentItem() == 2){
            next.setText("Next");
        }if(viewPager.getCurrentItem() > 0){
            viewPager.setCurrentItem(viewPager.getCurrentItem() -1);
        }if(viewPager.getCurrentItem() == 0){
            prev.setVisibility(View.INVISIBLE);
        }
    }

}
