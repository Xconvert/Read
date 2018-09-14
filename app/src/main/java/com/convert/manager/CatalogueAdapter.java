package com.convert.manager;

import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.convert.R;
import com.convert.ReadActivity;

import java.util.ArrayList;

public class CatalogueAdapter extends ArrayAdapter {
    private final int resourceId;
    private Context context;

    public CatalogueAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(resourceId, null);//实例化一个对象
        String cptName = (String) getItem(position); // 获取当前项的 chapter 实例
        TextView cpt = (TextView) view.findViewById(R.id.search_book_name);
        if (position == ReadPageManager.getInstance(context).getCptNum()){
            //当前章节，字体变粗
            TextPaint tp = cpt.getPaint();
            tp.setFakeBoldText(true);
            tp.setFakeBoldText(true);
            cpt.setTextColor(context.getColor(R.color.Black));
            cpt.setText(cptName);
        }
        else {
            cpt.setText(cptName);
        }

        return view;
    }
}

