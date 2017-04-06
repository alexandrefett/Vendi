package com.vendi.model;

import java.util.HashMap;

public class Category {
    public static final String  CATEGORY = "category";
    public static final String  CATEGORY_UID = "category_uid";
    public static final String  CATEGORY_DESC = "category_desc";

    public String getCategory_uid() {
        return category_uid;
    }

    public void setCategory_uid(String category_uid) {
        this.category_uid = category_uid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory_desc() {
        return category_desc;
    }

    public void setCategory_desc(String category_desc) {
        this.category_desc = category_desc;
    }

    private String category_uid;
    private String category;
    private String category_desc;

    public Category(){
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> o = new HashMap<>();
        o.put(CATEGORY, this.category);
        o.put(CATEGORY_UID, this.category_uid);
        o.put(CATEGORY_DESC, this.category_desc);
        return o;
    }

    public Category(Object O){
        HashMap<String, Object> o = (HashMap<String, Object>)O;
        this.category = (String)o.get(CATEGORY);
        this.category_uid = (String)o.get(CATEGORY_UID);
        this.category_desc = (String)o.get(CATEGORY_DESC);
    }
}
