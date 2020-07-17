package com.example.todoapplication.common;
import android.content.Context;
import android.content.SharedPreferences;

public class TodoPreferences {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static final String KEY_SIGNUP = "Signup";
    private static String auth_token;

    public static void initialize(Context context) {
        sharedPreferences = context.getSharedPreferences("TODO", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public static void storeSignupDone(Boolean isSignedUp) {
        editor.putBoolean(KEY_SIGNUP, isSignedUp);
        editor.commit();
    }

    public static Boolean isSignUpDone() {
        return sharedPreferences.getBoolean (KEY_SIGNUP, false);
    }

    public static void storeAuthToken(String token) {
        auth_token = token;
    }

    public static String getAuthToken() {
        return auth_token;
    }
}
