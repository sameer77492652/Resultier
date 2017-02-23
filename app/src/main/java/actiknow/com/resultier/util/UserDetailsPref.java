package actiknow.com.resultier.util;


import android.content.Context;
import android.content.SharedPreferences;

public class UserDetailsPref {
    public static String MOBILE = "mobile";

    private static UserDetailsPref userDetailsPref;
    private String USER_DETAILS = "USER_DETAILS";
    public static String USER_FIREBASE_ID = "user_firebase_id";
    public static String USER_ID = "user_id";
    public static String USER_OTP = "otp";

    public static UserDetailsPref getInstance () {
        if (userDetailsPref == null)
            userDetailsPref = new UserDetailsPref ();
        return userDetailsPref;
    }

    private SharedPreferences getPref (Context context) {
        return context.getSharedPreferences (USER_DETAILS, Context.MODE_PRIVATE);
    }

    public String getStringPref (Context context, String key) {
        return getPref (context).getString (key, "");
    }

    public int getIntPref (Context context, String key) {
        return getPref (context).getInt (key, 0);
    }

    public void putStringPref (Context context, String key, String value) {
        SharedPreferences.Editor editor = getPref (context).edit ();
        editor.putString (key, value);
        editor.apply ();
    }

    public void putIntPref (Context context, String key, int value) {
        SharedPreferences.Editor editor = getPref (context).edit ();
        editor.putInt (key, value);
        editor.apply ();
    }
}
