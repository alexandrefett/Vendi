package com.vendi.model;

import java.util.HashMap;

/**
 * Created by Alexandre on 14/08/2016.
 */
public class Message {

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    private String to;
    private String from;
    private String msg;
    private String timestamp;

    public static final String  TO = "to";
    public static final String  FROM = "from";
    public static final String  MSG = "msg";
    public static final String  TIMESTAMP = "timestamp";


    public Message(){
    }

    public Message(Object O){
        HashMap<String, Object> o = (HashMap<String, Object>)O;
        this.to = (String)o.get(TO);
        this.from  = (String)o.get(FROM);
        this.msg = (String)o.get(MSG);
        this.timestamp = (String)o.get(TIMESTAMP);
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> o = new HashMap<String, Object>();
        o.put(TO, this.to);
        o.put(FROM, this.from);
        o.put(MSG, this.msg);
        o.put(TIMESTAMP, this.timestamp);
        return o;
    }
}
