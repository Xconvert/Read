package com.convert.manager;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class HomePageManager {

    private final String TAG = "Track_HomePageManager";
    private Context mContext;
    private static HomePageManager sHomePageManager;
    private ArrayList<Book> mBookSearchList = new ArrayList<>();
    private ArrayList<Book> mBookList = new ArrayList<>();

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

    public ArrayList<Book> getBookSearchList() {
        return mBookSearchList;
    }


    public void searchNovel(final String NovelName, final Respond respond) {
        new Thread(NovelName) {
            @Override
            public void run() {
                mBookSearchList = Test_Search_Biquge.searchNovel(NovelName);
                Log.i(TAG, "searchNovel: mBookSearchList size is " + mBookSearchList.size());
                //获取到数据，通知下一步操作
                respond.report();
            }
        }.start();
    }

    //初始化
    public void getBooks(Respond respond){
        ArrayList<String> bookNames = SaveDataToFile.getBooks();
        for (String bookName : bookNames){
            Log.i(TAG, "get book: " + bookName);

            HashMap<String,String> bookIfo = SaveDataToFile.getBookIfo(bookName);
            String author = null;
            String address = null;
            int state = 0;
            if (!bookIfo.isEmpty()){
                author = bookIfo.get("author");
                address = bookIfo.get("address");
                state = Integer.parseInt(bookIfo.get("state"));
            }

            Book book = new Book(bookName, Test_Search_Biquge.getImageBitmap(address, bookName),
                    Test_Search_Biquge.getChapterList(address, bookName), SaveDataToFile.getCurrentCptNum(bookName),
                    author, address, state);

            Log.i(TAG, book.getChapterList().get(0).getName());

            mBookList.add(book);
        }
        //初始化界面
        respond.initBookList();
    }

    //保存到文件
    public void saveBookToFile(Book book) {
        if (book == null){
            Log.w(TAG, "saveBookToFile: book is null");
            return;
        }
        //保存到文件
        Log.i(TAG, "saveBookToFile");
        SaveDataToFile.saveBookIfo(book);
    }

    public void updateBookList(Book book) {
        if (book == null){
            Log.w(TAG, "updateBookList: book is null");
            return;
        }

        mBookList.add(0,book);
    }

    //完善书籍信息
    public void enrichBook(Book book){
        if (book == null){
            Log.w(TAG, "enrichBook: book is null");
            return;
        }
        book.setImage(Test_Search_Biquge.getImageBitmap(book.getAddress(), book.getName()));
        book.setChapterList(Test_Search_Biquge.getChapterList(book.getAddress(), book.getName()));
        book.setCurrentChapterNum(SaveDataToFile.getCurrentCptNum(book.getName()));
        Log.i(TAG, "enrichBook");
    }

    public void clear(){
        mBookList.clear();
        mBookSearchList.clear();
    }

    //长按删除书本
    public void deleteBook(int index){

        //清除文件
        String bookName = mBookList.get(index).getName();
        SaveDataToFile.deleteBook(bookName);

        //清除缓存的
        mBookList.remove(index);
    }
}
