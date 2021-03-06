package com.convert;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.convert.manager.Book;
import com.convert.manager.CatalogueAdapter;
import com.convert.manager.ReadPageManager;
import com.convert.manager.Respond;

import static com.convert.manager.Book.BOOK;

public class ReadActivity extends AppCompatActivity implements Respond {

    private final static String TAG = "Track_ReadActivity";
    //点击事件
    private final int DEFAULT = 0;
    private final int NEXT_PAGE = 1;
    private final int PRE_PAGE = 2;
    private final int MENU = 3;

    private final int MAG_INIT_PAGE = 0;
    private final int MAG_UPDATE_PAGE = 1;

    private ReadPageManager mReadPageManager;
    private CatalogueAdapter mCatalogueAdapter;

    //控件
    private TextView mTextView;
    private TextView mCatalogueBackground;
    private ListView mCatalogue;
    private TextView mCatalogueTitle;
    private LinearLayout mCatalogueLinearLayout;

    private int mScreenWidth = 0; // 屏幕宽
    private int mScreenHeight = 0; // 屏幕高
    private int mPadding = 20; //TextView 的 padding
    private int mTouchX = 0;
    private int mTouchY = 0;
    private int mTouchType = DEFAULT;
    private boolean isIdle = true;
    private boolean isCatalogue = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        //全屏沉浸模式
        hideSystemUI();
        //控件
        mTextView = (TextView) findViewById(R.id.page);
        mCatalogueLinearLayout = (LinearLayout) findViewById(R.id.catalogue_linearLayout);
        mCatalogue = (ListView) findViewById(R.id.catalogue);
        mCatalogueBackground = (TextView) findViewById(R.id.catalogue_background);
        mCatalogueTitle = (TextView) findViewById(R.id.catalogue_title);

        //隐藏目录
        hideCatalogue();

        initData();

        //加个动画 2s
    }

    @Override
    protected void onStart() {
        super.onStart();
        hideSystemUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    public void onBackPressed() {
        if (isCatalogue) {
            //目录界面
            hideCatalogue();
        } else {
            //返回键监听
            Intent intent = new Intent();
            //把返回数据存入Intent
            intent.putExtra("result", mReadPageManager.getCptNum());
            //设置返回数据
            setResult(RESULT_OK, intent);
            super.onBackPressed();//注释掉这行,back键不退出activity
            Log.i(TAG, "onBackPressed");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        mReadPageManager.saveCurrentCptNum();
        mReadPageManager.clear();
        mReadPageManager = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN && !isCatalogue) {
            if (mTouchX != x && mTouchY != y) {
                Log.i(TAG, "x = " + x + "y = " + y);
                mTouchX = x;
                mTouchY = y;
            }
            mTouchType = getTouchType(x, y);
            if (mTouchType == MENU) {
                //菜单
                Log.i(TAG, "MENU");
                //展示目录
                showCatalogue();

            } else if (mTouchType == NEXT_PAGE) {
                //下一页
                if (isIdle) {
                    isIdle = false;
                    nextPage();
                    Log.i(TAG, "NEXT_PAGE");
                    isIdle = true;
                }

            } else if (mTouchType == PRE_PAGE) {
                //上一页
                if (isIdle) {
                    isIdle = false;
                    prePage();
                    Log.i(TAG, "PRE_PAGE");
                    isIdle = true;
                }
            } else {
                Log.i(TAG, "unknown TouchType");
            }
        }
        return true;
    }

    int getTouchType(int x, int y) {
        //判断触摸区域
        if (x > mScreenWidth / 3 && y > mScreenHeight * 2 / 3) {
            return NEXT_PAGE;
        } else if (x < mScreenWidth * 2 / 3 && y < mScreenHeight / 3) {
            return PRE_PAGE;
        } else if (x > mScreenWidth * 2 / 3) {
            return NEXT_PAGE;
        } else if (x < mScreenWidth / 3) {
            return PRE_PAGE;
        } else {
            return MENU;
        }
    }

    private void initView() {
        Log.i(TAG, "initView");
        //保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //初始化第一页
        if (mReadPageManager != null) {
            String page = mReadPageManager.getPage();
            if (page == null) {
                Log.w(TAG, "initView: page is null");
                finish();
            }
            mTextView.setText(page);
        }
    }

    private void getScreenSize() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;

        int virtualKeyHeight = 0;
        Resources res = getResources();
        int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0)
            virtualKeyHeight = res.getDimensionPixelSize(resourceId);
        float density = getResources().getDisplayMetrics().density;
        int screenWidthDp = (int) (mScreenWidth / density + 0.5f);
        int screenHeighDp = (int) (mScreenHeight / density + 0.5f);
        Log.i(TAG, "屏幕宽:" + mScreenWidth + "px,屏幕高:" + mScreenHeight + "px,虚拟键高:" + virtualKeyHeight + "px");
        Log.i(TAG, "屏幕宽:" + screenWidthDp + "dp,屏幕高:" + screenHeighDp + "dp,density:" + density);

        Log.i(TAG, "getLineSpacingExtra " + mTextView.getLineSpacingExtra() + "px");

        int lineHeight = mTextView.getLineHeight();
        Log.i(TAG, "getLineHeight " + lineHeight + "px");

        float textSize = mTextView.getTextSize();
        Log.i(TAG, "getTextSize" + textSize);

        //计算行数
        int lineNum = (int) ((mScreenHeight - 2 * mPadding * density) / lineHeight + 0.5);
        //设置行数
        mReadPageManager.setLineNum(lineNum);
        //计算每行字数
        int numPerLine = (int) ((mScreenWidth - 2 * mPadding * density) / textSize);
        //设置每行字数
        mReadPageManager.setNumPerLine(numPerLine);

        Log.i(TAG, "lineNum is " + lineNum + " numPerLine is " + numPerLine);
    }

    private void initData() {
        Log.i(TAG, "initData");
        //初始化
        mReadPageManager = ReadPageManager.getInstance(ReadActivity.this);
        //获取书本
        Intent intent = getIntent();
        Book book = (Book) intent.getSerializableExtra(BOOK);
        Log.i(TAG, "initData: get book " + book.getName());
        mReadPageManager.setBook(this, book);
        //设置页面行数
        getScreenSize();
    }

    //全屏沉浸模式
    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    private void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    //跳转下一页
    private void nextPage() {
        if (mReadPageManager != null) {
            String page = mReadPageManager.nextPage();
            if (page == null) {
                Log.w(TAG, "nextPage: page is null");
                finish();
            }
            mTextView.setText(page);
        }
    }

    //跳转上一页
    private void prePage() {
        if (mReadPageManager != null) {
            //判断当前页是否是第一页
            if (!mReadPageManager.isFirstPage()) {
                String page = mReadPageManager.prePage();
                if (page == null) {
                    Log.w(TAG, "nextPage: page is null");
                    finish();
                }
                mTextView.setText(page);
            }
        }
    }

    @Override
    public void initBookList() {
        Log.i(TAG, "updateBookList");

    }

    @Override
    public void report() {
        Log.i(TAG, "report");
        //设置 list view
        Message msg1 = handler2.obtainMessage(MAG_INIT_PAGE);
        handler2.sendMessage(msg1);
    }

    @Override
    public void updatePage(String page) {
        Log.i(TAG, "updatePage");
        if (page == null) {
            Log.w(TAG, "nextPage: page is null");
            finish();
        }
        //设置 list view
        Message msg1 = handler2.obtainMessage(MAG_UPDATE_PAGE);
        msg1.obj = page;
        handler2.sendMessage(msg1);
    }

    //展示目录
    private void showCatalogue() {
        isCatalogue = true;
        mCatalogueLinearLayout.setVisibility(View.VISIBLE);
        mCatalogue.setVisibility(View.VISIBLE);
        mCatalogueTitle.setVisibility(View.VISIBLE);
        mCatalogueBackground.setVisibility(View.VISIBLE);

        //设置目录数据
        mReadPageManager.setCptList();
        mCatalogueAdapter = new CatalogueAdapter(this, R.layout.catalogue_chapter_item, mReadPageManager.getCptList());

        mCatalogue.setAdapter(mCatalogueAdapter);
        // 向右边移入
        mCatalogueLinearLayout.setAnimation(AnimationUtils.makeInAnimation(this, true));
        //属性动画，颜色渐深
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), getColor(R.color.Black0), getColor(R.color.Black2));
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mCatalogueBackground.setBackgroundColor((Integer) animator.getAnimatedValue());
            }
        });
        colorAnimation.setDuration(800);
        colorAnimation.start();

        //List view 设置焦点
        mCatalogue.setSelection(mReadPageManager.getCptNum());

        mCatalogue.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int index = position;
                new Thread() {
                    @Override
                    public void run() {
                        //获取章节
                        Log.i(TAG, "run: 获取章节" + index);
                        mReadPageManager.getChapter(index, ReadActivity.this);
                    }
                }.start();
                //隐藏目录
                hideCatalogue();
            }
        });

    }

    //隐藏目录
    private void hideCatalogue() {
        isCatalogue = false;
        mCatalogue.setVisibility(View.GONE);
        mCatalogueTitle.setVisibility(View.GONE);
        mCatalogueBackground.setVisibility(View.GONE);
        mCatalogueLinearLayout.setVisibility(View.GONE);
        // 向左边移出
        //mCatalogueLinearLayout.setAnimation(AnimationUtils.makeOutAnimation(this, false));
    }


    private Handler handler2 = new Handler() {
        public void handleMessage(Message msg1) {
            switch (msg1.what) {
                case MAG_INIT_PAGE:
                    //初始化界面
                    initView();
                    break;
                case MAG_UPDATE_PAGE:
                    //更新界面
                    String page = msg1.obj.toString();
                    mTextView.setText(page);
                    break;
                default:
                    break;
            }
        }

        ;
    };
}
