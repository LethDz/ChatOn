package com.lethdz.onlinechatdemo;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class Common {

    public static void closeSoftKeyboard(final Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
