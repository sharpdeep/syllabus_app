package com.example.stu_nwad.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.stu_nwad.syllabus.R;

/**
 * Created by STU_nwad on 2015/10/1.
 */
public class ListViewAdapter extends BaseAdapter {

    // 存放近四年的课表
    public static final int COUNT = 4;

    private  String[] syllabus_data = new String[4];

    class ViewHolder{
        TextView year_text;
        TextView spring_text;
        TextView summer_text;
        TextView autumn_text;
    }

    private void init(){
        for(int i = 0 ; i < syllabus_data.length ; ++i){
            // 2015-2016 2014-2015
            syllabus_data[i] = (2015 - i) + "\n" + (2015 - i + 1);
        }
    }

    private LayoutInflater layoutInflater;

    public ListViewAdapter(Context context){
        super();
        this.layoutInflater = LayoutInflater.from(context);
        init();
    }

    @Override
    public int getCount() {
        return syllabus_data.length;
    }

    @Override
    public Object getItem(int position) {
        return syllabus_data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.list_item, null);
            holder.year_text = (TextView) convertView.findViewById(R.id.year_text_view);
            holder.spring_text = (TextView) convertView.findViewById(R.id.spring_text_view);
            holder.summer_text = (TextView) convertView.findViewById(R.id.summer_text_view);
            holder.autumn_text = (TextView) convertView.findViewById(R.id.autumn_text_view);
            convertView.setTag(holder);
        }else{  // 即之前缓存过的
            holder = (ViewHolder) convertView.getTag();
        }

        // 设置数据
        holder.year_text.setText(syllabus_data[position]);
        holder.spring_text.setText("春季学期\n一共25学分");

        return convertView;
    }
}
