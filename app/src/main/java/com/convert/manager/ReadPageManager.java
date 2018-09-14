package com.convert.manager;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import static android.os.SystemClock.sleep;

public class ReadPageManager {

    private final static String TAG = "Track_ReadPageManager";
    //页面段首空格
    private final String PARA_BEGIN = "        ";
    private Context mContext;
    private static ReadPageManager sReadPageManager;
    private Book mBook;
    //当前章节内容
    private String mCurrentChapterContent;

    //当前页面下标
    private int mCurrentPage = 0;
    //页面列表
    private ArrayList<Page> mPageList = new ArrayList<>();
    //行列表
    private ArrayList<String> mLines = new ArrayList<>();
    //段落列表
    private String[] mParaList ;

    //章节列表
    private ArrayList<String> mCptList = new ArrayList<>();

    //一页行数
    private int mLineNum = 15;
    //一行字数
    private int mNumPerLine = 17;


    private ReadPageManager(Context context) {
        mContext = context;
    }

    public static ReadPageManager getInstance(Context context) {
        if (sReadPageManager == null) {
            sReadPageManager = new ReadPageManager(context);
        }
        return sReadPageManager;
    }

    public void setBook(Respond respond, Book book) {
        mBook = new Book(book);
        //获取当前章节内容
        getCurrentChapter(respond);
    }

    private void getCurrentChapter(final Respond respond) {
        new Thread() {
            @Override
            public void run() {
                //获取当前章节内容
                if (mBook != null) {
                    mCurrentChapterContent = Test_Search_Biquge.getNovelOnePage(mBook,
                            mBook.getCurrentChapterNum() + 1);
                } else {
                    Log.w(TAG, "getCurrentChapter: mBook is null");
                }

                //用 mCurrentChapterContent 更新 page 列表
                createPageList();
                if (respond != null){
                    respond.report();
                }
            }
        }.start();
    }

    //获取章节
    public void getChapter(int chapterId, Respond respond){
        mBook.setCurrentChapterNum(chapterId);
        getCurrentChapter(respond);
    }

    private void createPageList() {
        //生成段落列表
        mParaList = mCurrentChapterContent.split("\n");
        //更新行列表
        mLines.clear();
        for (String para : mParaList){
            int begin = 0;
            int end = Math.min(mNumPerLine - 2, para.length());
            //一行信息
            String lineTemp = "";
            lineTemp = PARA_BEGIN + para.substring(begin, end);
            mLines.add(lineTemp);
            Log.i(TAG, lineTemp);
            begin = end;
            end += mNumPerLine;
            end = Math.min(end, para.length());
            while( begin < para.length()) {
                lineTemp = para.substring(begin, end);
                mLines.add(lineTemp);
                Log.i(TAG, lineTemp);
                begin += mNumPerLine;
                end += mNumPerLine;
                end = Math.min(end, para.length());
            }
        }
        //更新 page 列表
        mPageList.clear();
        int lineNum = mLines.size();
        int begin = 0;
        int end = Math.min(mLineNum, lineNum);
        while(begin < lineNum){
            StringBuilder pageTemp = new StringBuilder();
            for (int i = begin; i < end; i++){
                pageTemp.append(mLines.get(i));
                pageTemp.append("\n");
            }
            Page page = new Page(pageTemp);
            mPageList.add(page);
            begin += mLineNum;
            end += mLineNum;
            end = Math.min(end, lineNum);
        }
        //删除冗余信息
        mParaList = null;
    }

    public String getPage() {
        Log.i(TAG, "mCurrentPage : " + mCurrentPage);
        if (mPageList != null && !mPageList.isEmpty() && mCurrentPage >= 0 && mCurrentPage < mPageList.size()){
            return mPageList.get(mCurrentPage).getPage();
        }
        //待改善
        sleep(200);
        if (mPageList != null && !mPageList.isEmpty() && mCurrentPage >= 0 && mCurrentPage < mPageList.size()){
            return mPageList.get(mCurrentPage).getPage();
        }
        sleep(200);
        if (mPageList != null && !mPageList.isEmpty() && mCurrentPage >= 0 && mCurrentPage < mPageList.size()){
            return mPageList.get(mCurrentPage).getPage();
        }
        else {
            Log.w(TAG, "getPage: mPageList is null");
            return null;
        }
    }

    //跳转下一页
    public String nextPage(){
        mCurrentPage++;
        //判断是否最后一页
        if(mCurrentPage == (mPageList.size() - 1)){
            //是，加载下一个章节
            String result = getPage();
            int chapterNum = mBook.getCurrentChapterNum() + 1;
            Log.i(TAG, "nextPage: chapterNum " + chapterNum);
            mBook.setCurrentChapterNum(chapterNum);
            getCurrentChapter(null);
            //进入下一章
            mCurrentPage = -1;
            return result;
        }
        else if(mCurrentPage == mPageList.size()){
            //刚刚进入上一章，就返回下一章
            int chapterNum = mBook.getCurrentChapterNum() + 1;
            Log.i(TAG, "nextPage: chapterNum " + chapterNum);
            mBook.setCurrentChapterNum(chapterNum);
            getCurrentChapter(null);
            //进入下一章
            mCurrentPage = 0;
        }
        sleep(100);
        return getPage();
    }

    //跳转上一页
    public String prePage(){
        mCurrentPage--;
        //判断是否最后一页
        if(mCurrentPage == 0){
            //是，加载上一个章节
            String result = getPage();
            int chapterNum = mBook.getCurrentChapterNum() - 1;
            if (chapterNum != -1) {
                mBook.setCurrentChapterNum(chapterNum);
                getCurrentChapter(null);
                //进入上一章,以后改进
                sleep(100);
                mCurrentPage = mPageList.size();
            }
            return result;
        }
        else if(mCurrentPage == -1){
            //刚刚进入下一章，就返回上一章
            int chapterNum = mBook.getCurrentChapterNum() - 1;
            if (chapterNum != -1){
                mBook.setCurrentChapterNum(chapterNum);
                getCurrentChapter(null);
                //进入上一章,以后改进
                sleep(100);
                mCurrentPage = mPageList.size() - 1;
            }
            else {
                return null;
            }
        }
        else {
            sleep(100);
        }
        return getPage();
    }

    //判断当前页是否是第一页
    public boolean isFirstPage(){
        if (mBook.getCurrentChapterNum() == 0 && (mCurrentPage == 0 || mCurrentPage == -1)){
            return true;
        }
        else {
            return false;
        }
    }

    public void setLineNum(int num) {
        //每页行数
        mLineNum = num;
    }

    public void setNumPerLine(int num) {
        //每行字数
        mNumPerLine = num;
    }

    public String getBookName(){
        if (mBook == null){
            return null;
        }
        return mBook.getName();
    }

    public int getCptNum(){
        if (mBook == null){
            return 0;
        }
        return mBook.getCurrentChapterNum();
    }

    public void saveCurrentCptNum(){
        SaveDataToFile.saveCurrentCptNum(getBookName(), getCptNum());
    }

    public void setCptList(){
        //目录数据
        ArrayList<Chapter> arrayList = mBook.getChapterList();
        for (Chapter cpt : arrayList){
            mCptList.add(cpt.getName());
        }
    }

    public ArrayList<String> getCptList() {
        return mCptList;
    }

    public void clear(){
        mPageList.clear();
        mLines.clear();
        mCptList.clear();
        sReadPageManager = null;
    }

}
