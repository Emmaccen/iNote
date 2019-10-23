package com.echo.iNote;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class HideKeyboard {

    public static void hideKeyboard(Activity activity) {
        InputMethodManager keyboard = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
