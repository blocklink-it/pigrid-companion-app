package de.blocklink.pigrid.Helper;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.Toast;

import de.blocklink.pigrid.R;

public class ValidIPAddressInputFilter implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {
        if (end > start) {
            String destTxt = dest.toString();
            String resultingTxt = destTxt.substring(0, dStart)
                    + source.subSequence(start, end)
                    + destTxt.substring(dEnd);
            if (!resultingTxt
                    .matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                return "";
            } else {
                String[] splits = resultingTxt.split("\\.");
                for (int i = 0; i < splits.length; i++) {
                    if (Integer.valueOf(splits[i]) > 255) {
                        return "";
                    }
                }
            }
        }
        return null;
    }
}
