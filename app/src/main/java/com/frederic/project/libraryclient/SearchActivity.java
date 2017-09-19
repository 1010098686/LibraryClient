package com.frederic.project.libraryclient;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.frederic.project.libraryclient.Util.Configuration;
import com.frederic.project.libraryclient.httpServices.bookService;
import com.frederic.project.libraryclient.models.Book;
import com.frederic.project.libraryclient.models.Reader;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String SEARCH_MODE = "search_mode";
    public static final int SEARCH_BOOK = 1;
    public static final int SEARCH_READER = 2;

    private int searchMode;
    private Reader reader = null;
    private BookInfoAdapter bookAdapter = new BookInfoAdapter(this);
    private ReaderInfoAdapter readerInfoAdapter = new ReaderInfoAdapter(this);

    private EditText searchContent;
    private ListView listView;
    private Button searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Bundle bundle = getIntent().getBundleExtra("data");
        searchMode = bundle.getInt(SEARCH_MODE);
        reader = (Reader) bundle.getSerializable("reader");
        initUI();
    }

    private void initUI(){
        searchContent = (EditText) findViewById(R.id.searchBox);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        listView = (ListView) findViewById(R.id.searchList);
        if(searchMode == SEARCH_BOOK){
            listView.setAdapter(bookAdapter);
        }else if(searchMode == SEARCH_READER){
            listView.setAdapter(readerInfoAdapter);
        }
        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(this);

        searchBtn.setOnClickListener(this);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int index, long l) {
        if(searchMode != SEARCH_BOOK) return true;
        String[] operations = new String[]{"borrow"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Operations");
        builder.setItems(operations, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0){
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(Configuration.ServerIp).addConverterFactory(GsonConverterFactory.create()).build();
                    bookService service = retrofit.create(bookService.class);
                    service.borrowBook(reader.getId(), bookAdapter.getItem(index).getIsbn()).enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            if(response.code() != 200){
                                Toast.makeText(SearchActivity.this,"no remaining or you have borrowed the book",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(SearchActivity.this,"borrow success",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            Toast.makeText(SearchActivity.this,"network error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        builder.create().show();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(searchMode == SEARCH_BOOK){
            View bookview = View.inflate(this,R.layout.detail_book_info,null);
            Book book = bookAdapter.getItem(i);
            UserActivity.setBookInfo(bookview,book,2);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("book information");
            builder.setView(bookview);
            builder.create().show();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.searchBtn){
            String queryString = searchContent.getText().toString();
            if(searchMode == SEARCH_BOOK){
                Retrofit retrofit = new Retrofit.Builder().baseUrl(Configuration.ServerIp).addConverterFactory(GsonConverterFactory.create()).build();
                bookService service = retrofit.create(bookService.class);
                service.query(queryString).enqueue(new Callback<List<Book>>() {
                    @Override
                    public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                        if(response.code() != 200){
                            Toast.makeText(SearchActivity.this,"query failed",Toast.LENGTH_SHORT).show();
                        }else{
                            bookAdapter.setList(response.body());
                            bookAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Book>> call, Throwable t) {
                        Toast.makeText(SearchActivity.this,"network error",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
