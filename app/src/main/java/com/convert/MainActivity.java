package com.convert;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.convert.ui.SpaceItemDecoration;
import com.convert.manager.Book;
import com.convert.manager.BookAdapter;
import com.convert.manager.Test_Search_Biquge;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String TAG = "qq_MainActivity";
    private static int REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG,"Start");
        //检查权限，没有权限再申请
        checkPermission();

        getdwrm();
        //测试文件位置
        //Log.i(TAG,this.getCacheDir().toString());///data/user/0/com.convert/cache
        //Log.i(TAG,this.getFilesDir().toString());///data/user/0/com.convert/file
        //Environment.getExternalStorageDirectory().getAbsolutePath()///storage/emulated/0

    }


    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通储存权限，否则会耗费更多流量！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);

        } else {
            //Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "checkPermission: 已经授权！write");
        }
    }

    //test
    private void initData() {

        for (int i = 0; i < 1; i++) {
            Log.i(TAG,Test_Search_Biquge.getChapterList("大王饶命").get(0).toString());
            Book dwrm = new Book("大王饶命", Test_Search_Biquge.getImageBitmap("大王饶命"), Test_Search_Biquge.getChapterList("大王饶命"));
            //bookList.add(dwrm);
        }
    }

    private void initView(ArrayList<Book> bookList){
        BookAdapter adapter = new BookAdapter(bookList);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.BookList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //设置偏移量
        recyclerView.addItemDecoration(new SpaceItemDecoration(6));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BookAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(MainActivity.this,"您点击了"+position+"行",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onLongClick(int position) {
                Toast.makeText(MainActivity.this,"您长按点击了"+position+"行",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //test use network
    private void getdwrm(){
        new Thread("url...#大王饶命"){
            private String bookShop;
            private String bookName;
            @Override
            public void run(){
                //panduan shifou weikong
                //第三次改动2018.7.7，增加print的log.i。改变msg的传输格式，book的image保存方式Bitmap代替String
                bookName = currentThread().getName().split("#")[1];
                Log.i(TAG,"getdwrm start " + bookName);
                Book dwrm = new Book(bookName, Test_Search_Biquge.getImageBitmap(bookName), Test_Search_Biquge.getChapterList(bookName));
                Log.i(TAG,dwrm.getChapterList().get(0).getName());

                ArrayList<Book> bookList = new ArrayList<Book>();
                if(dwrm != null)
                    bookList.add(dwrm);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bookList",bookList);
                Message msg1 = new Message();
                msg1.what = 211;
                msg1.setData(bundle);
                handler1.sendMessage(msg1);
            }
        }.start();
    }

    private Handler handler1 = new Handler() {
        public void handleMessage(Message msg1) {
            switch (msg1.what) {
                case 211:
                    ArrayList<Book> bookList = null;
                    Bundle bundle = msg1.getData();
                    bookList = (ArrayList<Book>) bundle.getSerializable("bookList");
                    initView(bookList);
                    break;
                default:
                    break;
            }
        }

        ;
    };
}
