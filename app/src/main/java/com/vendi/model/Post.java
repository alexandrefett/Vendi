package com.vendi.model;

import java.util.HashMap;
import java.util.Map;

public class Post{
    public static final String  POST_ID = "post_id";
    public static final String  USER = "user";
    public static final String  DESCRIPTION = "description";
    public static final String  PRICE = "price";
    public static final String  CATEGORY_ID = "category_id";
    public static final String  IMAGE = "image";
    public static final String  STATUS = "status";
    public static final String  TIMESTAMP = "timestamp";
    public static final String  BRANDS = "brands";
    public static final String  LIKES = "likes";
    public static final String  VIEWS = "views";
    public static final String  PICTURE = "picture";
    public static final String  TITLE = "title";
    private String title;
    private String user;
    private String post_id;
    private String description;
    private HashMap<String, Object>  image = new HashMap<String, Object>();
    private String picture;
    private String brands;
    private Double price;
    private String category_id;
    private long status = 0;
    private HashMap<String, Object>  likes = new HashMap<String, Object>();
    private HashMap<String, Object> views = new HashMap<String, Object>();
    private long timestamp;

    public Post(){
    }

    public Post(Object O){
        HashMap<String, Object> o = (HashMap<String, Object>)O;
        this.title = (String)o.get(TITLE);
        this.user = (String)o.get(USER);
        this.post_id = (String)o.get(POST_ID);
        this.description = (String)o.get(DESCRIPTION);
        this.image = (HashMap<String, Object>) o.get(IMAGE);;
        this.picture = (String)o.get(PICTURE);
        this.brands = (String)o.get(BRANDS);
        this.category_id = (String)o.get(CATEGORY_ID);
        this.status = (long)o.get(STATUS);
        this.price = (Double)o.get(PRICE);
        this.timestamp = (long)o.get(TIMESTAMP);
        this.likes = (HashMap<String, Object>) o.get(LIKES);
        this.views = (HashMap<String, Object>) o.get(VIEWS);
    }
    public Map<String, Object> toMap(){
        HashMap<String, Object> o = new HashMap<>();
        o.put(POST_ID, this.post_id);
        o.put(DESCRIPTION, this.description);
        o.put(IMAGE, this.image);
        o.put(CATEGORY_ID, this.category_id);
        o.put(STATUS, this.status);
        o.put(PRICE, this.price);
        o.put(USER, this.user);
        o.put(TIMESTAMP, this.timestamp);
        o.put(BRANDS, this.brands);
        o.put(User.PICTURE, this.picture);
        o.put(LIKES, this.likes);
        o.put(VIEWS, this.views);
        o.put(TITLE, this.title);
        return o;
    }

    public Map<String, Object> getLikes() {
        if(likes==null)
            this.likes = new HashMap<String, Object>();
        return likes;
    }

    public void setLikes(HashMap<String, Object> likes){
        this.likes = likes;
    }

    public Map<String, Object> getViews() {
        if(views==null)
            this.views = new HashMap<String, Object>();
        return views;
    }

    public void setViews(HashMap<String, Object> views) {
        this.views = views;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getImage() {
        if(image==null)
            this.image = new HashMap<String, Object>();
        return image;
    }

    public void setImage(HashMap<String, Object> image) {
        this.image = image;
    }

    public void addImage(String key, String image) {
        this.image.put(key, image);
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getBrands() {
        return brands;
    }

    public void setBrands(String brands) {
        this.brands = brands;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
