package com.example.s2o_mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.s2o_mobile.data.model.User;

public class SessionManager {

    private static final String PREF_NAME = "s2o_session";

    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_AVATAR = "avatar";
    private static final String KEY_ROLE = "role";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveAuthToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token == null ? "" : token).apply();
    }

    public String getAuthToken() {
        String t = prefs.getString(KEY_TOKEN, "");
        return t == null ? "" : t;
    }

    public void saveUser(User user) {
        if (user == null) return;

        prefs.edit()
                .putInt(KEY_USER_ID, user.getId())
                .putString(KEY_FULL_NAME, safe(user.getFullName()))
                .putString(KEY_USERNAME, safe(user.getUsername()))
                .putString(KEY_EMAIL, safe(user.getEmail()))
                .putString(KEY_PHONE, safe(user.getPhone()))
                .putString(KEY_AVATAR, safe(user.getAvatarUrl()))
                .putString(KEY_ROLE, safe(user.getRole()))
                .apply();
    }

    public User getUser() {
        int id = prefs.getInt(KEY_USER_ID, -1);
        if (id == -1) return null;

        User u = new User();
        u.setId(id);
        u.setFullName(prefs.getString(KEY_FULL_NAME, ""));
        u.setUsername(prefs.getString(KEY_USERNAME, ""));
        u.setEmail(prefs.getString(KEY_EMAIL, ""));
        u.setPhone(prefs.getString(KEY_PHONE, ""));
        u.setAvatarUrl(prefs.getString(KEY_AVATAR, ""));
        u.setRole(prefs.getString(KEY_ROLE, ""));
        return u;
    }

    public boolean isLoggedIn() {
        return getUserId() != -1 && !getAuthToken().trim().isEmpty();
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
