package com.convert.manager;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

class SaveDataToFile {
    public final static String AppPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/青青阅读";

    public void initPath(){
        File file = new File(AppPath);
        if(!file.exists())
            file.mkdir();
    }

    public static void saveDataToFile(String str, String fileName){
        try {
            OutputStreamWriter mFileWriter = new OutputStreamWriter(new FileOutputStream(fileName), "utf-8");
            mFileWriter.append(str);
            mFileWriter.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void saveChapterToFile(ArrayList<Chapter> chpt, String fileName){
        try {
            OutputStreamWriter mFileWriter = new OutputStreamWriter(new FileOutputStream(fileName), "utf-8");
            mFileWriter.write("");
            for(int i=0; i<chpt.size(); i++){
                String str = chpt.get(i).getName() + "#" + chpt.get(i).getUrl() + "\n";
                mFileWriter.append(str);
            }
            mFileWriter.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static StringBuilder getPageData(String path){
        if(path.equals(null)) return null;
        StringBuilder temp = new StringBuilder();
        File filename = new File(path);
        InputStreamReader reader;
        String line = null;
        BufferedReader br = null;
        try {
            reader = new InputStreamReader(new FileInputStream(filename));// 建立一个输入流对象reader
            br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言

            try {
                while((line = br.readLine()) != null){
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

    public static ArrayList<Chapter> getChapterUrl(String path){
        if(path.equals(null)) return null;
        ArrayList<Chapter> temp = new ArrayList<Chapter>();
        File filename = new File(path);
        InputStreamReader reader;
        String line = null;
        BufferedReader br = null;
        try {
            reader = new InputStreamReader(new FileInputStream(filename));// 建立一个输入流对象reader
            br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言

            try {
                while((line = br.readLine()) != null){
                    String l[] =line.split("#");
                    Chapter t = new Chapter(l[1],l[0]);
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
}