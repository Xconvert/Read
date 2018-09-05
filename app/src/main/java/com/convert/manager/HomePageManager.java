package com.convert.manager;

import android.content.Context;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class HomePageManager {

    private final static String TAG = "Track_HomePageManager";
    private Context mContext;
    private static HomePageManager sHomePageManager;
    public ArrayList<Book> mBookList = new ArrayList<>();

    private HomePageManager(Context context) {
        mContext = context;
    }

    public static HomePageManager getInstance(Context context) {
        if (sHomePageManager == null) {
            sHomePageManager = new HomePageManager(context);
        }
        return sHomePageManager;
    }

    //书籍列表
    public void setBookList(ArrayList<Book> bookList) {
        mBookList.addAll(bookList);
    }

    public ArrayList<Book> getBookList() {
        return mBookList;
    }


}
