package com.convert.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.convert.R;

import java.util.ArrayList;

public class SearchBookListAdapter extends ArrayAdapter {
    private final int resourceId;
    private Context context;

    public SearchBookListAdapter(Context context, int textViewResourceId, ArrayList<Book> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(resourceId, null);//实例化一个对象
        Book book = (Book) getItem(position); // 获取当前项的 Book 实例
        if (book != null) {

            TextView searchBookName = (TextView) view.findViewById(R.id.search_book_name);
            TextView searchAuthorName = (TextView) view.findViewById(R.id.search_author_name);
            TextView searchBookState = (TextView) view.findViewById(R.id.search_book_state);

            searchBookName.setText(book.getName());
            searchAuthorName.setText(book.getAuthor());
            int state = book.getState();
            String stateStr = "连载";
            if (state == 1) {
                stateStr = "完成";
            }
            searchBookState.setText(stateStr);
        }
        return view;
    }
}
