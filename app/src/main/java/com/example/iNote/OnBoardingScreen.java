package com.example.iNote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class OnBoardingScreen extends AppCompatActivity {
ArrayList<WelcomeScreenContract> list;
WelcomeScreenViewPager adapter;
ViewPager viewPager;
LinearLayout linear;
Button next,prev;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_screen);
        list = new ArrayList<>();
        viewPager  = findViewById(R.id.welcome_screen_view_pager);
        linear = findViewById(R.id.dots_layout);
        next = findViewById(R.id.welcome_next_button);
        prev = findViewById(R.id.welcome_prev_button);
        prev.setVisibility(View.INVISIBLE);
        initializeDots(0);
        list.add(new WelcomeScreenContract("Synchronization","this is where the description goes" +
                "this notepad is created by oriola emmmanuel and ive got a co founder",R.drawable.app_drawer_book));list.add(new WelcomeScreenContract("Realtime DataBase","this is where the description goes" +
                "this notepad is created by oriola emmmanuel and ive got a co founder",R.drawable.app_drawer_book));list.add(new WelcomeScreenContract("Chat","this is where the description goes" +
                "this notepad is created by oriola emmmanuel and ive got a co founder",R.drawable.app_drawer_book));
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
                                   next.setVisibility(View.INVISIBLE);
                                }else{
                                   next.setVisibility(View.VISIBLE);
                               }
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                            }
                        }
                );
        }


    public void initializeDots(int position){
        TextView [] dots  = new TextView[3];
        linear.removeAllViews();
        for(int i = 0; i<dots.length ; i ++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextColor(getResources().getColor(R.color.colorAccent));
            dots[i].setTextSize(30);
            linear.addView(dots[i]);
    }
        dots[position].setTextColor(Color.parseColor("#000000"));
    }
    public void onNextButtonSelected(View view){
        if(viewPager.getCurrentItem() <2){
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }if(viewPager.getCurrentItem() == 2){
            next.setVisibility(View.INVISIBLE);
        }if(prev.getVisibility() == View.INVISIBLE){
            prev.setVisibility(View.VISIBLE);
        }
    }
    public void onPrevButtonPressed(View view){
        if(viewPager.getCurrentItem() > 0){
            viewPager.setCurrentItem(viewPager.getCurrentItem() -1);
        }if(viewPager.getCurrentItem() == 0){
            prev.setVisibility(View.INVISIBLE);
        }if(next.getVisibility() == View.INVISIBLE){
            next.setVisibility(View.VISIBLE);
        }
    }

}
