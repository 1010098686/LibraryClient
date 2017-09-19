package com.frederic.project.libraryclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.frederic.project.libraryclient.models.Reader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fk on 17-9-19.
 */

public class ReaderInfoAdapter extends BaseAdapter {

    private Context context;
    private List<Reader> list = new ArrayList<>();

    public ReaderInfoAdapter(Context context){
        this.context = context;
    }

    public void clear(){
        list.clear();
    }

    public void setList(List<Reader> list){
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Reader getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.simple_reader_info,null);
        }
        Reader reader = list.get(i);
        TextView name = (TextView) view.findViewById(R.id.simple_reader_name);
        TextView department = (TextView) view.findViewById(R.id.simple_reader_department);
        name.setText(reader.getName());
        department.setText(reader.getDepartment());
        return view;
    }
}
