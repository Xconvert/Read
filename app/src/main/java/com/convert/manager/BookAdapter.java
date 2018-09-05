package com.convert.manager;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.convert.R;
import com.convert.ReadActivity;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private ArrayList<Book> mBookList;
    private OnItemClickListener mOnItemClickListener;
    private Context mContext;

    public BookAdapter(Context context, ArrayList<Book> mBookList) {
        this.mBookList = mBookList;
        mContext = context;
    }

    //加载item 的布局  创建ViewHolder实例
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //对RecyclerView子项数据进行赋值
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Book book = mBookList.get(position);

        holder.imageView.setImageBitmap(book.getImage());
        holder.bookName.setText(book.getName());
        holder.curChapter.setText("读至：" + book.getCurrentChapterName());

        //点击事件
        if( mOnItemClickListener!= null){
            holder.itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position);
                    //notifyItemChanged(position);//刷新当前点击item
                }
            });

            holder. itemView.setOnLongClickListener( new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onLongClick(position);
                    return false;
                }
            });
        }
    }

    //返回子项个数
    @Override
    public int getItemCount() {
        return mBookList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView bookName;
        TextView curChapter;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.BookImage);
            bookName = (TextView) view.findViewById(R.id.BookName);
            curChapter = (TextView) view.findViewById(R.id.Info);
        }
    }

    //click
    public interface OnItemClickListener{
        void onClick( int position);
        void onLongClick( int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        this. mOnItemClickListener = onItemClickListener;
    }

}
