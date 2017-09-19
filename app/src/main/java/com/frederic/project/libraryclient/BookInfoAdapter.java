package com.frederic.project.libraryclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.frederic.project.libraryclient.models.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fk101 on 2017/09/13.
 */

public class BookInfoAdapter extends BaseAdapter {

    private Context context;
    private List<Book> list = new ArrayList<>();

    public void setList(List<Book> list){
        this.list = list;
    }

    public void add(Book book){
        this.list.add(book);
    }

    public void clear(){
        list.clear();
    }

    public BookInfoAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Book getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.simple_book_info,null);
        }
        TextView nameView = (TextView) convertView.findViewById(R.id.bookName);
        TextView authorView = (TextView) convertView.findViewById(R.id.bookAuthor);
        TextView isbnView = (TextView) convertView.findViewById(R.id.bookIsbn);
        TextView stateView = (TextView) convertView.findViewById(R.id.bookState);
        Book book = list.get(position);
        nameView.setText(book.getName());
        authorView.setText(book.getAuthor());
        isbnView.setText(String.valueOf(book.getIsbn()));
        stateView.setText(book.getState());
        return convertView;
    }
}
