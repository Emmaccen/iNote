package com.echo.iNote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class WelcomeScreenViewPager extends PagerAdapter {
ArrayList<WelcomeScreenContract> screenList;
Context context;
    Animation imageAnimation,titleAnimation;


    public WelcomeScreenViewPager(ArrayList<WelcomeScreenContract> screenList, Context context) {
        this.screenList = screenList;
        this.context = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layout = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layout.inflate(R.layout.welcome_screen_layout,container,false);
        imageAnimation = AnimationUtils.loadAnimation(view.getContext(),R.anim.welcome_image_animation);
        titleAnimation = AnimationUtils.loadAnimation(view.getContext(),R.anim.welcome_title_animation);
        ImageView image = view.findViewById(R.id.welcome_image);
        image.setAnimation(imageAnimation);
        TextView title = view.findViewById(R.id.welcome_title_text);
        TextView description = view.findViewById(R.id.welcome_details_text);
        title.setAnimation(titleAnimation);
        description.setAnimation(titleAnimation);


        image.setImageResource(screenList.get(position).getWelcomeImage());
        title.setText(screenList.get(position).getTitle());
        description.setText(screenList.get(position).getDescription());

        container.addView(view);
        return view;

    }

    @Override
    public int getCount() {
        return screenList.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
