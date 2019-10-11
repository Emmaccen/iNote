package com.echo.iNote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class WelcomeScreenViewPager extends PagerAdapter {
ArrayList<WelcomeScreenContract> screenList;
Context context;

    public WelcomeScreenViewPager(ArrayList<WelcomeScreenContract> screenList, Context context) {
        this.screenList = screenList;
        this.context = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layout = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layout.inflate(R.layout.welcome_screen_layout,container,false);

        ImageView image = view.findViewById(R.id.welcome_image);
        TextView title = view.findViewById(R.id.welcome_title_text);
        TextView description = view.findViewById(R.id.welcome_details_text);
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
