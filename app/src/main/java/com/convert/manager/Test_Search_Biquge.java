package com.convert.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.os.SystemClock.sleep;

public class Test_Search_Biquge {
    private static String NOVELNAME = "大王饶命";
    private static String TAG = "Track_Test_Search_Biquge";
    private static String NOcoverUrl = "http://www.biquge.com.tw/images/nocover.jpg";
	/*
	public static void main(String[] args){
		//首页
		//String html = ConnectionUtil.Connect("http://www.biquge.com.tw/", "gbk");
		//@brief 这个网站的小说是由三部分组成，例：http://www.biquge.com.tw/18_18727/8570927.html
		//		 1首页。2小说id。3章节id，章节id递增
		//		   首先获取小说id，获取章节id和名字，进入小说阅读页
		
		//print(html);
		//ArrayList<Chapter> a = getChapterList(NOVELNAME);
		
		
		//get and save page of every chapter
		//for(int i=0; i<a.size(); i++){
			//print(a.get(i).getName());
			//print(a.get(i).getUrl());
			
		//}
		
		//通过这个方法，可以访问小说名字及其章节，其他小说还没来得及搞，日后升级
		//其他小说可以改良getNovelHomePage获取
		String temp = getNovelOnePage(NOVELNAME,4);
		print(temp);
		
		//test
		//SaveDataToFile.getChapterUrl("Novel-大王饶命/chapterURL.txt");
		//getChapterList(NOVELNAME);
	}
	*/

    /***
     * @brief input novel name to get the novel home page
     * @param NovelName
     * @return
     */
    private static String getNovelHomePage(String address, String NovelName) {
        //String html = ConnectionUtil.Connect("http://www.biquge.com.tw/18_18727/", "gbk");
        String html = ConnectionUtil.Connect(address, "gbk");
        if (html == null || html.equals("")) {
            print("html is null in getNovelHomePage");
            return null;
        }
        //build a new file to save novel data
        String name = SaveDataToFile.AppPath + (String) "/Novel-" + NovelName;
        File file = new File(name);
        if (!file.exists()) {
            file.mkdirs();
            //save this novel brief UI
            //SaveDataToFile.saveDataToFile(html, name + "/" + NovelName + ".txt");
            //SaveDataToFile.saveDataToFile("http://www.biquge.com.tw/18_18727/", name + "/" + NovelName + ".txt");

        }
        return html;
    }

    /***
     * @brief input Novel name and dijizhang to get the novel a page
     *        通过这个方法，可以访问小说名字及其章节
     * @param //Url
     * @return
     */
    public static String getNovelOnePage(Book book, int chapterN) {
        if (book == null) {
            print("book is null");
            return null;
        }
        String Novel = book.getName();
        if (Novel.equals("")) {
            print("novel name is null");
            return null;
        }
        //build a new file to save novel data
        String name = SaveDataToFile.AppPath + (String) "/Novel-" + Novel + "/" + "Chapter-" + chapterN + ".txt";
        File file = new File(name);

        StringBuilder pageValue = null;
        if (!file.exists()) {
            //本地文件没有数据，联网抓取
            ArrayList<Chapter> chptList = getChapterList(book.getAddress(), Novel);
            if (chptList == null) {
                print("chptList is null");
                return "未更新";
            }
            if (chapterN > chptList.size()) {
                print("chptList is null");
                return null;
            }
            String url = chptList.get(chapterN - 1).getUrl();
            String html = ConnectionUtil.Connect(url, "gbk");

            if (html == null || html.equals("")) {
                //Log.i(TAG,"html = null");
                print("html is null in getNovelOnePage");
                return null;
            }

            //save this novel brief UI
            //SaveDataToFile.saveDataToFile(html, name + "/" + "Chapter-" + chapterN + ".txt");

            Pattern pagePattern = Pattern.compile("&nbsp;&nbsp;&nbsp;&nbsp;.*<");
            Matcher pageMatcher = pagePattern.matcher(html);

            pageValue = new StringBuilder();
            while (pageMatcher.find()) {
                String seg = pageMatcher.group();
                //get page value
                seg = seg.substring(24, seg.length() - 1);
                //print(seg);
                pageValue.append(seg).append("\n");
            }
            SaveDataToFile.saveDataToFile(pageValue.toString(), name);
        } else {
            //本地有数据，直接读取
            pageValue = SaveDataToFile.getPageData(name);
        }

        return pageValue.toString();
    }

    /***
     * @brief print
     * @param s
     */
    public static void print(String s) {
        Log.i(TAG, "print: " + s);
    }

    /***
     * @brief get chapter list from homeHtml
     * @param //html
     * @return
     */
    public static ArrayList<Chapter> getChapterList(String address, String NovelName) {
        if (NovelName == null || NovelName.equals(""))
            return null;

        ArrayList<Chapter> chapterList = null;
        //build a new file to save novel data
        String filename = SaveDataToFile.AppPath + (String) "/Novel-" + NovelName + "/" + "chapterURL.txt";
        File file = new File(filename);

        if (!file.exists()) {
            //本地没有文件，联网获取数据
            Log.i(TAG, "get chapterList data link to Internet");
            String html = getNovelHomePage(address, NovelName);
            if (html == null || html.equals("")) {
                print("html is null in getChapterList");
                return null;
            }

            //create file name
            //String name = (String)"Novel-" + NovelName;
            //SaveDataToFile.saveDataToFile(html, name + "/" + NovelName + ".txt");

            Pattern novelPattern = Pattern.compile("<dd><a href=\".*html\">.*</a></dd>");
            Matcher novelMatcher = novelPattern.matcher(html);

            //one chapter data in this list
            chapterList = new ArrayList<Chapter>();
            //find
            //print("getChapterList");
            while (novelMatcher.find()) {
                //print("getChapterList - find");
                String chapter = novelMatcher.group();
                //print(chapter);
                String chapterListStr[] = chapter.split("<dd><a href=\"");
                for (int i = 0; i < chapterListStr.length; i++) {
                    if (chapterListStr[i].length() > 9) {
                        //delete the end "</a></dd>"
                        chapterListStr[i] = chapterListStr[i].substring(0, chapterListStr[i].length() - 9);

                        //split partUrl and name
                        String tempStr[] = chapterListStr[i].split("\">");
                        //get chapter and store
                        String url = "http://www.biquge.com.tw" + tempStr[0];
                        String name = tempStr[1];
                        Chapter tempChapter = new Chapter(url, name);

                        //add to list
                        chapterList.add(tempChapter);
                    }
                    //print(chapterListStr[i]);
                }
            }
            //save chapter to file
            SaveDataToFile.saveChapterToFile(chapterList, filename);
        } else {
            //从本地读取
            Log.i(TAG, "get chapterList data from file");
            chapterList = SaveDataToFile.getChapterUrl(filename);
        }
        return chapterList;
    }

    public static Bitmap getImageBitmap(String address, String NovelName) {
        if (NovelName == null || NovelName.length() == 0) return null;
        Bitmap bitmap = null;
        //build a new file to save novel Image
        String filename = SaveDataToFile.AppPath + (String) "/Novel-" + NovelName + "/" + "cover.jpg";
        File file = new File(filename);

        if (!file.exists()) {
            //本地没有文件，联网获取数据
            Log.i(TAG, "get Image data link to Internet");
            String html = getNovelHomePage(address, NovelName);
            if (html == null || html.equals("")) {
                print("html is null in getImage . address is " + address);
                //Log.i(TAG,"html is null in getImage");
                return null;
            }

            //<img alt="三寸人间" src="/files/article/image/14/14055/14055s.jpg" onerror="src='/images/nocover.jpg'">
            Pattern novelPattern = Pattern.compile("<img alt=.*jpg\" onerror");
            Matcher novelMatcher = novelPattern.matcher(html);
            String ImgUrl = null;
            //find
            while (novelMatcher.find()) {
                //print("getChapterList - find");
                String Img = novelMatcher.group();
                //print(chapter);
                String ListStr[] = Img.split("src=\"/");
                if (ListStr != null && ListStr.length == 2) {
                    String tmp[] = ListStr[1].split("\" ");
                    if (tmp != null)
                        ImgUrl = "http://www.biquge.com.tw/" + tmp[0];
                }
            }
            print("get image in internet");
            //根据image url获取图片，如果无效，不保存，取默认图片
            //save image to file
            if (ImgUrl != null) {
                print("ImgURL is not null");
                try {
                    URL imgUrl = new URL(ImgUrl);
                    HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    //设置bitmap
                    bitmap = BitmapFactory.decodeStream(is);
                    //保存起来
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    is.close();
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                //网上没图片，本地默认
                //String mrfilename = SaveDataToFile.AppPath + (String)"/Novel-" + NovelName + "/" + "mrCover.jpg";
                //File mrfile=new File(mrfilename);
                try {
                    URL imgUrl = new URL("http://www.biquge.com.tw/images/nocover.jpg");
                    HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    //设置bitmap
                    bitmap = BitmapFactory.decodeStream(is);
                    //保存起来
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    is.close();
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            //...
            //SaveDataToFile.saveChapterToFile(chapterList, filename);
        } else {
            //从本地读取图片
            Log.i(TAG, "get Image data from file");
            //Uri uri = Uri.fromFile(file);
            //String img_url = uri.getPath();//这是本机的图片路径
            try {
                FileInputStream fis = new FileInputStream(filename);
                bitmap = BitmapFactory.decodeStream(fis);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return bitmap;
    }


    //url编码
    private static String getURLEncoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(str, "gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    //搜索书本
    public static ArrayList<Book> searchNovel(String NovelName) {
        Log.i(TAG, "searchNovel: NovelName is " + NovelName);
        ArrayList<Book> books = new ArrayList<>();
        if (NovelName == null || NovelName.isEmpty()) return books;

        String address = "http://www.biquge.com.tw/modules/article/soshu.php?searchkey=" + getURLEncoderString(NovelName);
        String html = ConnectionUtil.Connect(address, "gbk");
        //Log.d(TAG, "html: " + html);
        //6 个信息为一部小说
        Pattern novelPattern = Pattern.compile("<td class=.*</td>");
        Matcher novelMatcher = novelPattern.matcher(html);
        while (novelMatcher.find()) {
            //1地址和书名
            String[] temp1 = novelMatcher.group().split("<a href=\"")[1].split("\">");
            String addr = temp1[0];
            String name = temp1[1].split("</a>")[0];
            //2最新章节，不要
            novelMatcher.find();
            //3作者
            novelMatcher.find();
            String[] temp3 = novelMatcher.group().split("\"odd\">")[1].split("<");
            String author = temp3[0];
            //4,5不要
            novelMatcher.find();
            novelMatcher.find();
            novelMatcher.find();
            //6状态
            String[] temp6 = novelMatcher.group().split("center\">")[1].split("<");
            String stateStr = temp6[0];
            //默认连载
            int state = 0;
            if("完成".equals(stateStr)){
                state = 1;
            }
            Log.i(TAG, addr + name + author + stateStr);
            Book book = new Book(name, null, new ArrayList<Chapter>(), 0, author, addr, state);
            books.add(book);
        }
        Log.i(TAG, "searchNovel: " + books.size());
        return books;
    }

}
