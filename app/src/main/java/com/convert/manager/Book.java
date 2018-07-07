package com.convert.manager;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

public class Book implements Serializable {

    private String Name = null;
    private Bitmap Image = null;

    //当前阅读的章节
    private int CurrentChater = 0;
    //章节总数
    private int ChapterNum = 0;
    private ArrayList<Chapter> ChapterList = null;

    /*2018.7.7 第三次改版时候删掉不用
    public Book(String _B_){
        if(_B_ == null) return;
        String strbuf [] = _B_.split("_B_");
        if(strbuf.length < 5) return;
        Name = strbuf[0];
        Image = strbuf[1];
        CurrentChater = Integer.parseInt(strbuf[2]);
        ChapterNum = Integer.parseInt(strbuf[3]);
        ChapterList = new ArrayList<Chapter>();
        for(int i=4; i<strbuf.length; i++){
            if(strbuf[i] != null)
                ChapterList.add(new Chapter(strbuf[i]));
        }
    }*/

    public Book(String name, Bitmap image, ArrayList<Chapter> chapterList){
        Name = name;
        Image = image;
        ChapterList = chapterList;
        if(ChapterList != null)
            ChapterNum = ChapterList.size();
    }

    public void saveBook(String name, Bitmap image, ArrayList<Chapter> chapterList){
        Name = name;
        Image = image;
        ChapterList = chapterList;
        if(ChapterList != null)
            ChapterNum = ChapterList.size();
    }

    public String getName(){
        return Name;
    }

    public Bitmap getImage(){
        return Image;
    }

    public int getCurrentChaterNum(){
        return CurrentChater;
    }


    public String getCurrentChaterName(){
        if(ChapterList != null)
            return ChapterList.get(CurrentChater).getName();
        return null;
    }

    public int getChapterNum(){
        return ChapterNum;
    }

    public ArrayList<Chapter> getChapterList(){
        return ChapterList;
    }

    public void setCurrentChaterNum(int chater){
        CurrentChater = chater;
    }

    public void setChapterNum(int num){
        ChapterNum = num;
    }

    public void setChapterList(ArrayList<Chapter> list){
        ChapterList = list;
    }


    @Override
    public String toString(){
        String ts = "";
        ts += reStr(Name);
        ts += reStr("Image");
        ts += reStr(String.valueOf(CurrentChater));
        ts += reStr(String.valueOf(ChapterNum));
        if(ChapterList != null) {
            for (int i = 0; i < ChapterNum; i++) {
                ts += reStr(ChapterList.get(i).toString());
            }
        }
        return ts;//以_B_结尾
    }

    private String reStr(String tmp){
        if(tmp == null)
            return "_B_";
        else return tmp + "_B_";
    }
}
