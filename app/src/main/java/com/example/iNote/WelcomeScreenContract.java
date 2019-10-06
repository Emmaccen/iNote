package com.example.iNote;

import android.widget.ImageView;

public class WelcomeScreenContract {

    String title,description;
    int welcomeImage;

   public WelcomeScreenContract(String title, String description,int welcomeImage){
        this.title = title;
        this.description = description;
        this.welcomeImage = welcomeImage;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWelcomeImage() {
        return welcomeImage;
    }

    public void setWelcomeImage(int welcomeImage) {
        this.welcomeImage = welcomeImage;
    }
}
