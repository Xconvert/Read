package com.convert.manager;

import android.graphics.Bitmap;
import java.io.Serializable;
import java.util.ArrayList;

public class Book implements Serializable {

    public static String BOOK = "book";

    //书名
    private String Name = null;
    //封面
    private transient Bitmap Image = null;
    //地址
    private String Address;
    //作者
    private String Author;

    //状态，0连载，1完结
    private int State;

    //当前阅读的章节，下标从零开始
    private int CurrentChapterNum = 0;

    //章节总数
    private int ChapterNum = 0;

    //章节列表
    private ArrayList<Chapter> ChapterList = null;

    /*2018.7.7 第三次改版时候删掉不用
    public Book(String _B_){
        if(_B_ == null) return;
        String strbuf [] = _B_.split("_B_");
        if(strbuf.length < 5) return;
        Name = strbuf[0];
        Image = strbuf[1];
        CurrentChapterNum = Integer.parseInt(strbuf[2]);
        ChapterNum = Integer.parseInt(strbuf[3]);
        ChapterList = new ArrayList<Chapter>();
        for(int i=4; i<strbuf.length; i++){
            if(strbuf[i] != null)
                ChapterList.add(new Chapter(strbuf[i]));
        }
    }*/

    public Book(String name, Bitmap image, ArrayList<Chapter> chapterList, int currentChapterNum,
                String author, String address, int state) {
        Name = name;
        Image = image;
        ChapterList = new ArrayList<>();
        ChapterList.addAll(chapterList);
        if (ChapterList != null)
            ChapterNum = ChapterList.size();
        CurrentChapterNum = currentChapterNum;
        Author = author;
        Address = address;
        State = state;
    }

    public Book(Book book) {
        Name = book.getName();
        Image = book.getImage();
        ChapterList = new ArrayList<>();
        ChapterList.addAll(book.getChapterList());
        if (ChapterList != null)
            ChapterNum = ChapterList.size();
        CurrentChapterNum = book.getCurrentChapterNum();
        Author = book.getAuthor();
        Address = book.getAddress();
        State = book.getState();
    }

    public String getName() {
        return Name;
    }

    public Bitmap getImage() {
        return Image;
    }

    public Chapter getCurrentChapter() {
        if (ChapterList == null) {
            return null;
        }
        return ChapterList.get(CurrentChapterNum);
    }

    public int getCurrentChapterNum() {
        return CurrentChapterNum;
    }

    public String getCurrentChapterName() {
        if (ChapterList != null)
            return ChapterList.get(CurrentChapterNum).getName();
        return null;
    }

    public int getChapterNum() {
        return ChapterNum;
    }

    public ArrayList<Chapter> getChapterList() {
        return ChapterList;
    }

    public String getAddress(){
        return Address;
    }

    public String getAuthor(){
        return Author;
    }

    public int getState(){
        return State;
    }

    public void setCurrentChapterNum(int chapter) {
        CurrentChapterNum = chapter;
    }

    public void setChapterNum(int num) {
        ChapterNum = num;
    }

    public void setChapterList(ArrayList<Chapter> list) {
        ChapterList = list;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setImage(Bitmap image) {
        Image = image;
    }

    public void setAddress(String s) {
        Address = s;
    }

    public void setAuthor(String s) {
        Author = s;
    }

    public void setState(int state) {
        State = state;
    }

    @Override
    public String toString() {
        String ts = "";
        ts += reStr(Name);
        ts += reStr("Image");
        ts += reStr(String.valueOf(CurrentChapterNum));
        ts += reStr(String.valueOf(ChapterNum));
        if (ChapterList != null) {
            for (int i = 0; i < ChapterNum; i++) {
                ts += reStr(ChapterList.get(i).toString());
            }
        }
        return ts;//以_B_结尾
    }

    private String reStr(String tmp) {
        if (tmp == null)
            return "_B_";
        else return tmp + "_B_";
    }

}
