package de.blocklink.pigrid.Helper;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class Helper {

    //Helper function to hide keyboard
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    //Helper function to check text obtained from edit text is empty or not
    public static boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }


}
