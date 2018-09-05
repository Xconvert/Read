package com.convert;

import android.Manifest;
import android.content.Intent;
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

import com.convert.manager.HomePageManager;
import com.convert.ui.SpaceItemDecoration;
import com.convert.manager.Book;
import com.convert.manager.BookAdapter;
import com.convert.manager.Test_Search_Biquge;

import java.util.ArrayList;

import static com.convert.manager.Book.BOOK;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "Track_MainActivity";
    private final int INIT_BOOKS = 211;
    private int REQUEST_CODE = 1;
    private int mPosition = 0;
    private static int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private HomePageManager mHomePageManager;
    private BookAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "onCreate");
        //检查权限，没有权限再申请
        checkPermission();

        //初始化
        mHomePageManager = HomePageManager.getInstance(MainActivity.this);

        //初始化书架
        initBookList();
        //测试文件位置
        //Log.i(TAG,this.getCacheDir().toString());///data/user/0/com.convert/cache
        //Log.i(TAG,this.getFilesDir().toString());///data/user/0/com.convert/file
        //Environment.getExternalStorageDirectory().getAbsolutePath()///storage/emulated/0

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //返回的数据，更新当前阅读章节id
        if (data == null) {
            Log.w(TAG, "onActivityResult: data == null");
            Log.i(TAG, "onActivityResult: requestCode = " + requestCode);
            Log.i(TAG, "onActivityResult: resultCode = " + resultCode);
            return ;
        }
        int result = data.getIntExtra("result", 0);
        Log.i(TAG, "onActivityResult: result = " + result);
        ArrayList<Book> bookList = mHomePageManager.getBookList();
        if (bookList == null || bookList.isEmpty()){
            Log.w(TAG, "onActivityResult: bookList onClick: bookList == null || bookList.isEmpty()");
            return;
        }
        Book book = bookList.get(mPosition);
        book.setCurrentChapterNum(result);
        mAdapter.notifyItemChanged(mPosition);
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

    private void initView(ArrayList<Book> bookList) {
        if (bookList == null || bookList.isEmpty()){
            Log.w(TAG, "initView: bookList == null || bookList.isEmpty()");
            return;
        }
        //初始化书籍列表
        mAdapter = new BookAdapter(MainActivity.this, bookList);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.BookList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //设置偏移量
        recyclerView.addItemDecoration(new SpaceItemDecoration(6));
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new BookAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(MainActivity.this, ReadActivity.class);
                ArrayList<Book> bookList = mHomePageManager.getBookList();
                //判空
                if (bookList == null || bookList.isEmpty()){
                    Log.w(TAG, "bookList onClick: bookList == null || bookList.isEmpty()");
                    return;
                }
                Book book = bookList.get(position);
                //判空
                if (book == null){
                    Log.w(TAG, "bookList onClick: book == null");
                    return;
                }
                //开始阅读，需要传入 Book
//                String bookName = book.getName();
//                int currentChapter = book.getCurrentChaterNum();
                intent.putExtra(BOOK, book);
                mPosition = position;
                startActivityForResult(intent, REQUEST_CODE);
                Log.i(TAG, "bookList onClick: " + "点击了" + position + "行");
            }

            @Override
            public void onLongClick(int position) {
                Log.i(TAG, "bookList onLongClick: " + "点击了" + position + "行");
                //Toast.makeText(MainActivity.this, "您长按点击了" + position + "行", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });
    }

    //test use network
    private void initBookList() {
        new Thread() {
            private String bookName;

            @Override
            public void run() {
                //panduan shifou weikong
                //第三次改动2018.7.7，增加 print 的 log.i。改变msg的传输格式，book的image保存方式Bitmap代替String
                //正在进行第四次结构改动 2018.09.05
                bookName = "大王饶命";
                Log.i(TAG, "getdwrm start " + bookName);
                Book dwrm = new Book(bookName, Test_Search_Biquge.getImageBitmap(bookName),
                        Test_Search_Biquge.getChapterList(bookName), Test_Search_Biquge.getCurrentCptNum(bookName),
                        null,null,0);
                Log.i(TAG, dwrm.getChapterList().get(0).getName());

                ArrayList<Book> bookList = new ArrayList<Book>();
                if (dwrm != null) {
                    bookList.add(dwrm);
                }
                //初始化 book list
                mHomePageManager.setBookList(bookList);
                //初始化界面
                Message msg1 = handler1.obtainMessage(INIT_BOOKS);
                handler1.sendMessage(msg1);

                //搜索
                ArrayList<Book> books = Test_Search_Biquge.searchNovel("斗罗大陆");
            }
        }.start();
    }

    public Handler handler1 = new Handler() {
        public void handleMessage(Message msg1) {
            switch (msg1.what) {
                case INIT_BOOKS:
                    ArrayList<Book> bookList = mHomePageManager.getBookList();
                    //初始化界面
                    initView(bookList);
                    break;
                default:
                    break;
            }
        }

        ;
    };
}
