package com.convert.manager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//HttpURLConnection
class ConnectionUtil {
    public static String Connect(String address, String htmlcode){
        if(address == null)
            return null;
        HttpURLConnection conn = null;
        URL url = null;
        InputStream in = null;
        BufferedReader reader = null;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            url = new URL(address);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setDoInput(true);
            conn.connect();
            in = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in,htmlcode));
            String line = null;
            while((line = reader.readLine()) != null){
                stringBuffer.append(line).append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            conn.disconnect();
            try {
                in.close();
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return stringBuffer.toString();
    }
}
//
////1.0.0版本。18.7.4 第二次修改结构
////HttpURLConnection
//class ConnectionUtil {
//    private static String TAG = "ConnectionUtil";
//    public static String mHTML = null;
//
//    public static void Connect(String address, String htmlcode) {
//        mHTML = null;
//        if (address == null)
//            return;
//
//        String TreadName = address + "@" + htmlcode;
//        ConnectionThread thread = new ConnectionThread(TreadName);
//        thread.start();
//    }
//}
