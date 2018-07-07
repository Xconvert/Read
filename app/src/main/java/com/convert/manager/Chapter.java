package com.convert.manager;

import java.io.Serializable;

public class Chapter implements Serializable {
    private String url = null;
    private String name = null;

    //String url, String name
    public Chapter(String url, String name){
        this.url = url;
        this.name = name;
    }

    public Chapter(String url_C_name){
        String [] temp = url_C_name.split("_C_");
        if(temp != null && temp.length == 2){
            this.url = temp[0];
            this.name = temp[1];
        }
    }

    public String getUrl(){
        return url;
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString(){
        if(url == null || name == null) return null;
        else return url + "_C_" + name;
    }

}

