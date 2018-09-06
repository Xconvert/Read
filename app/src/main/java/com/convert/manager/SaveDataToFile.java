package com.convert.manager;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SaveDataToFile {

    private final static String TAG = "Track_SaveDataToFile";

    public final static String AppPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/青青阅读";

    public static void initPath() {
        File file = new File(AppPath);
        if (!file.exists())
            file.mkdir();
    }

    public static void saveDataToFile(String str, String fileName) {
        try {
            OutputStreamWriter mFileWriter = new OutputStreamWriter(new FileOutputStream(fileName), "utf-8");
            mFileWriter.append(str);
            mFileWriter.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void saveChapterToFile(ArrayList<Chapter> chpt, String fileName) {
        try {
            OutputStreamWriter mFileWriter = new OutputStreamWriter(new FileOutputStream(fileName), "utf-8");
            mFileWriter.write("");
            for (int i = 0; i < chpt.size(); i++) {
                String str = chpt.get(i).getName() + "#" + chpt.get(i).getUrl() + "\n";
                mFileWriter.append(str);
            }
            mFileWriter.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static StringBuilder getPageData(String path) {
        if (path.equals(null)) return null;
        StringBuilder temp = new StringBuilder();
        File filename = new File(path);
        if (!filename.exists()) return null;
        InputStreamReader reader;
        String line = null;
        BufferedReader br = null;
        try {
            reader = new InputStreamReader(new FileInputStream(filename));// 建立一个输入流对象reader
            br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言

            try {
                while ((line = br.readLine()) != null) {
                    temp.append(line).append('\n');
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                reader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                br.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return temp;

    }

    public static ArrayList<Chapter> getChapterUrl(String path) {
        if (path.equals(null)) return null;
        ArrayList<Chapter> temp = new ArrayList<Chapter>();
        File filename = new File(path);
        InputStreamReader reader;
        String line = null;
        BufferedReader br = null;
        try {
            reader = new InputStreamReader(new FileInputStream(filename));// 建立一个输入流对象reader
            br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言

            try {
                while ((line = br.readLine()) != null) {
                    String l[] = line.split("#");
                    Chapter t = new Chapter(l[1], l[0]);
                    temp.add(t);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                reader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                br.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return temp;

    }

    public static int getCurrentCptNum(String NovelName) {
        if (NovelName == null || NovelName.length() == 0) return 0;

        String filename = SaveDataToFile.AppPath + (String) "/Novel-" + NovelName + "/" + "currentCptNum.txt";
        File file = new File(filename);
        if (file.exists()) {
            StringBuilder stringBuilder = SaveDataToFile.getPageData(filename);
            if (stringBuilder != null) {
                return Integer.parseInt(stringBuilder.toString().replace("\n", ""));
            }
        }
        //默认
        return 0;
    }

    public static void saveCurrentCptNum(String NovelName, int currentCptNum) {
        //currentCptNum >= 0
        if (NovelName == null || NovelName.length() == 0) return;

        String filename = SaveDataToFile.AppPath + (String) "/Novel-" + NovelName + "/" + "currentCptNum.txt";
        SaveDataToFile.saveDataToFile(String.valueOf(currentCptNum), filename);
    }

    public static ArrayList<String> getBooks() {
        ArrayList<String> bookNames = new ArrayList<>();
        initPath();
        File file = new File(AppPath);
        File[] files = file.listFiles();
        if(files.length > 0){
            for(File f : files){
                //格式 Novel-XXX
                String bookName = f.getName().split("-")[1];
                bookNames.add(bookName);
            }
        }
        return bookNames;
    }

    //保存基本信息到文件
    public static void saveBookIfo(Book book){
        if (book != null){
            Log.i(TAG, "saveBookIfo");
            String data = "";
            data = book.getName() + "\n" + book.getAuthor() + "\n" + book.getAddress() + "\n" + book.getState();
            //创建文件夹
            File file = new File(AppPath + "/Novel-" + book.getName());
            if (!file.exists()) {
                file.mkdir();
            }
            //保存文件
            saveDataToFile(data, AppPath + "/Novel-" + book.getName() + "/" + book.getName() + ".txt");
        }
        else {
            Log.w(TAG, "saveBookIfo: book is null");
        }
    }

    //获取基本信息
    public static HashMap<String,String> getBookIfo(String bookName){
        HashMap<String,String> bookIfo = new HashMap<>();
        if (bookName != null){
            StringBuilder sb = getPageData(AppPath + "/Novel-" + bookName + "/" + bookName + ".txt");

            if (sb != null){
                Log.i(TAG, sb.toString());
                String [] ifo = sb.toString().split("\n");
                bookIfo.put("author", ifo[1]);
                bookIfo.put("address", ifo[2]);
                bookIfo.put("state", ifo[3]);
            }
        }
        return bookIfo;
    }

    //删除书本
    public static void deleteBook(String name){
        //文件夹
        File dirFile = new File(AppPath + "/Novel-" + name);
        deleteFile(dirFile);
    }

    private static boolean deleteFile(File dirFile) {
        // 如果dir对应的文件不存在，则退出
        if (!dirFile.exists()) {
            return false;
        }
        // 如果不是文件夹，删除
        if (dirFile.isFile()) {
            return dirFile.delete();

        } else {

            for (File file : dirFile.listFiles()) {
                deleteFile(file);
            }
        }
        return dirFile.delete();
    }
}