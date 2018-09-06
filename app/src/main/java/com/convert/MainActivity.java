package com.convert;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.convert.manager.HomePageManager;
import com.convert.manager.Respond;
import com.convert.manager.SearchBookListAdapter;
import com.convert.ui.SpaceItemDecoration;
import com.convert.manager.Book;
import com.convert.manager.BookAdapter;

import java.util.ArrayList;

import static com.convert.manager.Book.BOOK;

public class MainActivity extends AppCompatActivity implements Respond {

    private final String TAG = "Track_MainActivity";
    private final int INIT_BOOKS = 0;
    private final int INIT_SEARCH_BOOKS = 1;
    private final int UPDATE_BOOKS = 2;
    private int REQUEST_CODE = 1;
    private int mPosition = 0;
    private final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private boolean isSearching = false;
    private HomePageManager mHomePageManager;
    private BookAdapter mAdapter;

    private Toolbar mToolbar;
    private LinearLayout mTopBar;
    private ListView mListView;
    private SearchBookListAdapter mSearchBookListAdapter;

    //查找界面
    private ImageView mBack;
    private EditText mInput;
    private TextView mSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate");

        //初始化控件
        initViewUI();

        //检查权限，没有权限再申请
        checkPermission();

        //初始化
        mHomePageManager = HomePageManager.getInstance(MainActivity.this);

        //初始化书架
        initBookListByFile();
        //测试文件位置
        //Log.i(TAG,this.getCacheDir().toString());///data/user/0/com.convert/cache
        //Log.i(TAG,this.getFilesDir().toString());///data/user/0/com.convert/file
        //Environment.getExternalStorageDirectory().getAbsolutePath()///storage/emulated/0

    }

    //设置 search list view
    private void initSearchList() {
        ArrayList<Book> bookList = mHomePageManager.getBookSearchList();
        Log.i(TAG, "initSearchList: BookList().size() is " + bookList.size());

        if (bookList == null || bookList.isEmpty()) {
            Toast.makeText(MainActivity.this, "查找不到", Toast.LENGTH_SHORT).show();
            return;
        }

        //初始化
        mSearchBookListAdapter = new SearchBookListAdapter(this, R.layout.search_book_item, bookList);
        mListView.setAdapter(mSearchBookListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int index = position;

                new Thread() {
                    @Override
                    public void run() {
                        //保存书本，打开 read
                        Book book = mHomePageManager.getBookSearchList().get(index);
                        Log.i(TAG, "initSearchList " + book.getName());
                        //详细 book
                        mHomePageManager.enrichBook(book);
                        //保存到文件
                        saveBookToFile(book);
                        //添加到书单
                        updateBookList(book);

                    }
                }.start();

                //强制隐藏键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
    }

    //保存到文件
    private void saveBookToFile(Book book) {
        if (book == null) {
            Log.w(TAG, "saveBookToFile: book is null");
            return;
        }
        //保存到文件
        Log.i(TAG, "saveBookToFile");
        mHomePageManager.saveBookToFile(book);
    }


    public void updateBookList(Book book) {
        if (book == null) {
            Log.w(TAG, "updateBookList: book is null");
            return;
        }
        mHomePageManager.updateBookList(book);
        Log.i(TAG, "updateBookList: books size is " + mHomePageManager.getBookList().size());
        Message msg1 = handler1.obtainMessage(UPDATE_BOOKS);
        handler1.sendMessage(msg1);
    }

    //初始化控件
    private void initViewUI() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mListView = (ListView) findViewById(R.id.listView);
        mTopBar = (LinearLayout) findViewById(R.id.topbar);

        mBack = (ImageView) findViewById(R.id.imageBack);
        mInput = (EditText) findViewById(R.id.searchLine);
        mSearch = (TextView) findViewById(R.id.searchButton);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查找界面消失
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                }
                endSearching();
            }
        });

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查找小说，生成小说列表
                String bookName = mInput.getText().toString();
                if (bookName == null || bookName.isEmpty()) {
                    //没输入书名
                    Toast.makeText(MainActivity.this, "请输入书名", Toast.LENGTH_SHORT).show();
                } else {
                    //搜索书本
                    mHomePageManager.searchNovel(bookName, MainActivity.this);

                }
            }
        });

        //查找界面消失
        endSearching();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //返回的数据，更新当前阅读章节id
        if (data == null) {
            Log.w(TAG, "onActivityResult: data == null");
            Log.i(TAG, "onActivityResult: requestCode = " + requestCode);
            Log.i(TAG, "onActivityResult: resultCode = " + resultCode);
            return;
        }
        int result = data.getIntExtra("result", 0);
        Log.i(TAG, "onActivityResult: result = " + result);
        ArrayList<Book> bookList = mHomePageManager.getBookList();
        if (bookList == null || bookList.isEmpty()) {
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

    //初始化书籍列表
    private void initView() {
        ArrayList<Book> bookList = mHomePageManager.getBookList();
        if (bookList == null || bookList.isEmpty()) {
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
                if (bookList == null || bookList.isEmpty()) {
                    Log.w(TAG, "bookList onClick: bookList == null || bookList.isEmpty()");
                    return;
                }
                Book book = bookList.get(position);
                //判空
                if (book == null) {
                    Log.w(TAG, "bookList onClick: book == null");
                    return;
                }
                //开始阅读，需要传入 Book
                intent.putExtra(BOOK, book);
                mPosition = position;
                startActivityForResult(intent, REQUEST_CODE);
                Log.i(TAG, "bookList onClick: " + "点击了" + position + "行");
            }

            @Override
            public void onLongClick(int position) {
                Log.i(TAG, "bookList onLongClick: " + "点击了" + position + "行");
                //Toast.makeText(MainActivity.this, "您长按点击了" + position + "行", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(MainActivity.this, TestActivity.class);
//                startActivity(intent);
                //删除
                showDeleteDialog(position);
            }
        });
    }

    private void showDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除")
                .setMessage("\n" + mHomePageManager.getBookList().get(position).getName())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //定义自己想要做的操作
                        Log.i(TAG, "showDeleteDialog: sure");
                        mHomePageManager.deleteBook(position);
                        Log.i(TAG, "updateBookList: books size is " + mHomePageManager.getBookList().size());
                        Message msg1 = handler1.obtainMessage(UPDATE_BOOKS);
                        handler1.sendMessage(msg1);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "showDeleteDialog: deny");
                    }
        });

        builder.create().show();
    }

    //初始化收藏书单
    private void initBookListByFile() {
        new Thread() {
            @Override
            public void run() {
                //panduan shifou weikong
                //第三次改动2018.7.7，增加 print 的 log.i。改变msg的传输格式，book的image保存方式Bitmap代替String
                //正在进行第四次结构改动 2018.09.05

                //初始化收藏书单
                mHomePageManager.getBooks(MainActivity.this);
            }
        }.start();
    }

    private Handler handler1 = new Handler() {
        public void handleMessage(Message msg1) {
            switch (msg1.what) {
                case INIT_BOOKS:
                    //初始化界面
                    initView();
                    break;
                case INIT_SEARCH_BOOKS:
                    //设置 list view
                    initSearchList();
                    break;
                case UPDATE_BOOKS:
                    //更新界面
                    mAdapter.notifyDataSetChanged();
                    //使查找界面消失
                    endSearching();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    //toolbar
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            //点击搜索按钮
            case R.id.search:
                //打开查找界面
                beginSearching();
                Log.i(TAG, "点击搜索图标");
                break;
            default:
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //监听返回键
        if (isSearching) {
            //查找界面消失
            endSearching();
        } else {
            super.onBackPressed();
        }
    }

    //打开查找界面
    private void beginSearching() {
        mTopBar.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.VISIBLE);
        isSearching = true;
    }

    //查找界面消失
    private void endSearching() {
        mTopBar.setVisibility(View.GONE);
        mListView.setVisibility(View.GONE);
        isSearching = false;
        //清空list view
        mSearchBookListAdapter = new SearchBookListAdapter(this, R.layout.search_book_item, new ArrayList<Book>());
        mListView.setAdapter(mSearchBookListAdapter);
        //清空搜索框
        mInput.setText("");
    }

    @Override
    public void initBookList() {
        Log.i(TAG, "updateBookList");
        //初始化界面
        Message msg1 = handler1.obtainMessage(INIT_BOOKS);
        handler1.sendMessage(msg1);
    }

    @Override
    public void report() {
        Log.i(TAG, "report");
        //设置 list view
        Message msg1 = handler1.obtainMessage(INIT_SEARCH_BOOKS);
        handler1.sendMessage(msg1);
    }

    @Override
    public void updatePage(String page) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        mHomePageManager.clear();
    }
}
