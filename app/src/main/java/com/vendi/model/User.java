package com.vendi.model;

import java.util.HashMap;

/**
 * Created by Alexandre on 03/07/2016.
 */
public class User {

    private String fullname;
    private String username;
    private String phone;
    private String email;
    private String picture;
    private String location;
    private String token;
    private HashMap<String, Object> followme = new HashMap<String, Object>();
    private HashMap<String, Object> ifollow = new HashMap<String, Object>();

    public static final String  FULLNAME = "fullname";
    public static final String  EMAIL = "email";
    public static final String  PHONE = "phone";
    public static final String  PICTURE = "picture";
    public static final String  LOCATION = "location";
    public static final String  TOKEN = "token";
    public static final String  FOLLOWME = "followme";
    public static final String  IFOLLOW = "ifollow";

    public static final int USER_NULL = 0;
    public static final int CONFIRMED = 1;
    public static final int USER_EMAIL_PENDING = 2;
    public static final int USER_CODE_PENDING = 3;
    public static final int USER_CODE_EMAIL_PENDING = 4;

    public User(){
    }

    public User(Object O){
        HashMap<String, Object> o = (HashMap<String, Object>)O;
        this.email  = (String)o.get(EMAIL);
        this.phone = (String)o.get(PHONE);
        this.fullname = (String)o.get(FULLNAME);
        this.location = (String)o.get(LOCATION);
        this.picture = (String)o.get(PICTURE);
        this.token = (String)o.get(TOKEN);
        this.followme = (HashMap<String, Object>) o.get(FOLLOWME);
        this.ifollow = (HashMap<String, Object>) o.get(IFOLLOW);
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> o = new HashMap<>();
        o.put(FULLNAME, this.fullname);
        o.put(EMAIL, this.email);
        o.put(PHONE, this.phone);
        o.put(PICTURE, this.picture);
        o.put(LOCATION, this.location);
        return o;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        String[] names = fullname.split(" ");
        if(names.length==1)
            this.username = fullname;
        else{
            this.username = names[0]+" "+names[names.length-1].substring(1,1)+".";
        }
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public HashMap<String, Object> getFollowme() {
        return followme;
    }

    public void setFollowme(HashMap<String, Object> followme) {
        this.followme = followme;
    }

    public HashMap<String, Object> getIfollow() {
        return ifollow;
    }

    public void setIfollow(HashMap<String, Object> ifollow) {
        this.ifollow = ifollow;
    }

}
