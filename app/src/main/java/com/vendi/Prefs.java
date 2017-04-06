package com.vendi;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.vendi.model.Category;
import com.vendi.model.User;

public class Prefs {
    private static String TAG="--->>>";
    private static String STATUS="status";

    public static int getStatus()  {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance().getContext());
        return sharedPref.getInt(STATUS,0);
    }

    public static void setStatus(int status)  {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance().getContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(STATUS, status);
        editor.commit();
    }

    public static String getPassword()  {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance().getContext());
        return sharedPref.getString("password","");
    }

    public static void setPassword(String pass)  {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance().getContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("password", pass);
        editor.commit();
    }
    public static Category getCategory()  {
        Category u = new Category();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance().getContext());
        u.setCategory_uid(sharedPref.getString(Category.CATEGORY_UID,""));
        u.setCategory(sharedPref.getString(Category.CATEGORY,""));
        u.setCategory_desc(sharedPref.getString(Category.CATEGORY_DESC,""));
        return u;
    }

    public static void setCategory(Category u)  {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance().getContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Category.CATEGORY, u.getCategory());
        editor.putString(Category.CATEGORY_UID, u.getCategory_uid());
        editor.putString(Category.CATEGORY_DESC, u.getCategory_desc());
        editor.commit();
    }

    public static User getUser()  {
        User u = new User();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance().getContext());
        u.setEmail(sharedPref.getString(User.EMAIL,""));
        u.setPhone(sharedPref.getString(User.PHONE,""));
        u.setPicture(sharedPref.getString(User.PICTURE,""));
        u.setFullname(sharedPref.getString(User.FULLNAME,""));
        u.setToken(sharedPref.getString(User.TOKEN,""));
        u.setLocation(sharedPref.getString(User.LOCATION,""));
        Log.d(TAG,"GET USER:"+u.getEmail());
        return u;
    }

    public static void setUser(User u)  {
        Log.d(TAG,"PUT USER:"+u.getEmail());
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance().getContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(User.PHONE, u.getPhone());
        editor.putString(User.EMAIL, u.getEmail());
        editor.putString(User.PICTURE, u.getPicture());
        editor.putString(User.FULLNAME, u.getFullname());
        editor.putString(User.TOKEN, u.getToken());
        editor.putString(User.LOCATION, u.getLocation());
        editor.commit();
    }
}
