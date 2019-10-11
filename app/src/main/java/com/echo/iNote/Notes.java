package com.echo.iNote;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Notes implements Parcelable {
    String title;
    String textBody;
    static Notes noteClass;
    static List<Notes> notesArray;
    String date;
    int colors;
    protected Notes(Parcel in) {
        title = in.readString();
        textBody = in.readString();
        date = in.readString();
        colors = in.readInt();
    }

    public static final Creator<Notes> CREATOR = new Creator<Notes>() {
        @Override
        public Notes createFromParcel(Parcel in) {
            return new Notes(in);
        }

        @Override
        public Notes[] newArray(int size) {
            return new Notes[size];
        }
    };

    public Notes(String title, String textBody, String date,int colors) {
        this.title = title;
        this.textBody = textBody;
        this.date = date;
        this.colors = colors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTextBody() {
        return textBody;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }

  /*  public static Notes getNoteClass() {
        return noteClass;
    }

    public static void setNoteClass(Notes noteClass) {
        Notes.noteClass = noteClass;
    }

    public static List<Notes> getNotesArray() {
        return notesArray;
    }

    public static void setNotesArray(List<Notes> notesArray) {
        Notes.notesArray = notesArray;
    }*/

    @Override
    public int describeContents() {
        return 0;
    }
public void setDate(String date){
        this.date = date;
}
public void setColors(int colors){
        this.colors = colors;
}
public int getColors(){
        return colors;
}
public String getDate(){
        return date;
}
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(textBody);
        dest.writeString(date);
        dest.writeInt(colors);
    }


   /* public void addNotesToArray(String title, String textBody,String date,int colors){
        this.title = title;
        this.textBody = textBody;
        this.date = date;
        this.colors = colors;

        notesArray.add(new Notes(title,textBody,date,colors));
    }*/
}