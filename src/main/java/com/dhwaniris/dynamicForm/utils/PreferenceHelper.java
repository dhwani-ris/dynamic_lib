
package com.dhwaniris.dynamicForm.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.dhwaniris.dynamicForm.NetworkModule.AppConfing;

import java.util.Map;
import java.util.Set;


public class PreferenceHelper {

    private SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private SharedPreferences pref_language;

    public PreferenceHelper(Context ctx, String FileName) {
        prefs = ctx.getSharedPreferences(FileName, Context.MODE_PRIVATE);
        pref_language = ctx.getSharedPreferences(AppConfing.DELTA_LANGUAGE_NAME, Context.MODE_PRIVATE);
    }

    public SharedPreferences getSharePrefs(Context ctx,String fileName)
    {
        SharedPreferences sharedPreferencs=ctx.getSharedPreferences(fileName,Context.MODE_PRIVATE);

        return sharedPreferencs;
    }
    public void clearAllPrefs() {
        prefs.edit().clear().apply();
    }

    public void SaveStringPref(String key, String value) {
        editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void clearAllPref_Language() {
        pref_language.edit().clear().apply();
    }

    public void saveBooleanPref_Language(String key, boolean value) {
        editor = pref_language.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public Boolean loadLanguageBooleanPref_Language(String key, boolean defaultValue) {
        return pref_language.getBoolean(key, defaultValue);
    }

    public void saveStringPref_Language(String key, String value) {
        editor = pref_language.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String loadStringPref_Language(String key, String defaultValue) {
        return pref_language.getString(key, defaultValue);
    }

    // end of the methods added by anil

    public String LoadStringPref(String key, String DefaultValue) {
        return prefs.getString(key, DefaultValue);
    }

    public  String getUserLanguage(){
        return prefs.getString(AppConfing.LANGUAGE, "en");
    }

    public void SaveIntPref(String key, int value) {
        editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();

    }

    public int LoadIntPref(String key, int DefaultValue) {
        return prefs.getInt(key, DefaultValue);
    }

    public void SaveBooleanPref(String key, boolean value) {
        editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean LoadBooleanPref(String key, boolean DefaultValue) {
        return prefs.getBoolean(key, DefaultValue);
    }

    public static void copySharedPreference(SharedPreferences fromPreferences, SharedPreferences toPreferences, boolean clear) {

        SharedPreferences.Editor editor = toPreferences.edit();
        if (clear) {
            editor.clear();
        }
        copySharedPreferences(fromPreferences, editor);
        editor.commit();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static void copySharedPreferences(SharedPreferences fromPreferences, SharedPreferences.Editor toEditor) {

        for (Map.Entry<String, ?> entry : fromPreferences.getAll().entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();
            if (value instanceof String) {
                toEditor.putString(key, ((String) value));
            } else if (value instanceof Set) {
                toEditor.putStringSet(key, (Set<String>) value); // EditorImpl.putStringSet already creates a copy of the set
            } else if (value instanceof Integer) {
                toEditor.putInt(key, (Integer) value);
            } else if (value instanceof Long) {
                toEditor.putLong(key, (Long) value);
            } else if (value instanceof Float) {
                toEditor.putFloat(key, (Float) value);
            } else if (value instanceof Boolean) {
                toEditor.putBoolean(key, (Boolean) value);
            }
        }
    }
}