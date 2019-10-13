package com.echo.iNote;

public class UserContract {
    String email;
    String phoneNumber;
    String image;
    String userId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
public void setUserId(String userId){
        this.userId = userId;
}
public String getUserId(){
        return userId ;
    }
}
