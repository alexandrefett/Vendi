package com.vendi;

import android.app.Application;
import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;
import com.vendi.model.Post;
import com.vendi.model.User;

import java.util.HashMap;

/**
 * Created by Alexandre on 02/08/2016.
 */
public class MyApplication extends Application {
    private static MyApplication ourInstance = new MyApplication();
    private User user;
    private Post post;
    private Context context;


    public HashMap<String, Object> getUsers() {
        if(users == null)
            users = new HashMap<String, Object>();
        return users;
    }

    public void setUsers(HashMap<String, Object> users) {
        this.users = users;
    }

    public User getUser(String username){
        return (User)users.get(username);
    }
    private HashMap<String, Object> users;

    public static MyApplication getInstance() {
        return ourInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }

    public User getUser(){
        return user;
    }
    public void setUser(User u){
        this.user = u;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


}