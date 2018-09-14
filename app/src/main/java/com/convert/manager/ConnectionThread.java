package com.convert.manager;

import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

////1.0.0版本。18.7.4，第二次修改结构。该类废弃
public class ConnectionThread extends Thread {
    private String TAG = "Track_ConnectionThread";
    private String address = null;
    private String htmlcode = null;

    public ConnectionThread(String name) {
        String[] tmp = name.split("@");
        address = tmp[0];
        htmlcode = tmp[1];
    }

    @Override
    public void run() {
        HttpURLConnection conn = null;
        URL url = null;
        InputStream in = null;
        BufferedReader reader = null;
        StringBuffer stringBuffer = null;
        try {
            url = new URL(address);
            Log.i(TAG, address);
            Log.i(TAG, htmlcode);
            conn = (HttpURLConnection) url.openConnection();
            Log.i(TAG, conn.toString());
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            //conn.setDoInput(true);
            //conn.connect();
            conn.setRequestMethod("GET");
            in = conn.getInputStream();
            Log.i(TAG, in.toString());
            reader = new BufferedReader(new InputStreamReader(in, htmlcode));
            stringBuffer = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line).append('\n');
                //Log.i(TAG,line);
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            conn.disconnect();
            try {
                in.close();
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (stringBuffer == null) {
            Log.i(TAG, "stringBuffer is null , get no data");
            return;

        }
        /*
        Message msg1 = new Message();
        msg1.what = 211;
        msg1.obj = stringBuffer.toString();
        Log.i(TAG,msg1.obj.toString());
        ConnectionUtil.handler1.sendMessage(msg1);*/
        //ConnectionUtil.mHTML=stringBuffer.toString();
    }
}
