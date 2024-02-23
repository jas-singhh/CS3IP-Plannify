package uk.ac.aston.cs3ip.plannify.shared_prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    // reference 1: https://developer.android.com/training/data-storage/shared-preferences
    // reference 2: https://stackoverflow.com/questions/19612993/writing-singleton-class-to-manage-android-sharedpreferences


    // preference file key
    private static final String PREFERENCE_FILE_KEY = "preference_file_key";

    // data keys
    public static final String IS_NOT_FIRST_ACCESS = "is_not_first_access";
    public static final String ARE_NOTIFICATIONS_ENABLED = "are_notifications_enabled";
    public static final String ARE_NOTIFICATIONS_SETUP = "are_notifications_setup";
    public static final String IS_MOTIVATIONAL_QUOTE_DISABLED = "is_motivational_quote_disabled";
    public static final String HOME_SELECTED_CHIP_TAG = "home_selected_chip_tag";

    // private constructor to avoid initialisation
    private SharedPreferencesManager() {}

    // synchronized prevents the method from being accessed by multiple threads at the same time
    private static synchronized SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
    }

    public static boolean readBoolean(Context context, String key) {
        // default value is false
        return getSharedPreferences(context).getBoolean(key, false);
    }

    public static void writeBoolean(Context context, String key, boolean value) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static String readString(Context context, String key) {
        // default value is null
        return getSharedPreferences(context).getString(key, null);
    }

    public static void writeString(Context context, String key, String value) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, value);
        editor.apply();
    }
}
