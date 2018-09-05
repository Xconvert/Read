package com.convert.manager;

import java.io.Serializable;

public class Chapter implements Serializable {

    //章节内容地址
    private String Url = null;

    //章节名字
    private String Name = null;

    //String Url, String Name
    public Chapter(String url, String name){
        this.Url = url;
        this.Name = name;
    }

    public Chapter(String url_C_name){
        String [] temp = url_C_name.split("_C_");
        if(temp != null && temp.length == 2){
            this.Url = temp[0];
            this.Name = temp[1];
        }
    }

    public String getUrl(){
        return Url;
    }

    public String getName(){
        return Name;
    }

    public void setName(String name){
        Name = name;
    }

    public void setUrl(String url){
        Url = url;
    }

    @Override
    public String toString(){
        if(Url == null || Name == null) return null;
        else return Url + "_C_" + Name;
    }

}

