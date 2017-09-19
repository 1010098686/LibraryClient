package com.frederic.project.libraryclient;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.frederic.project.libraryclient.Util.Configuration;
import com.frederic.project.libraryclient.httpServices.bookService;
import com.frederic.project.libraryclient.models.Book;

import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BookActivity extends AppCompatActivity implements View.OnClickListener {


    public static final String MODE = "mode";
    public static final int ADD_MODE= 1;
    public static final int MODIFY_MODE = 2;

    public static final String BOOK = "book";

    private EditText isbnView;
    private EditText nameView;
    private EditText authorView;
    private EditText publisherView;
    private Button publishTimeBtn;
    private EditText stateView;
    private EditText positionView;
    private EditText remainNumView;
    private Button okBtn;
    private DatePickerDialog dialog = null;

    private Book book = null;
    private int mode ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        Bundle bundle = getIntent().getBundleExtra("data");
        mode = bundle.getInt(MODE);
        if(mode == MODIFY_MODE){
            book = (Book) bundle.getSerializable(BOOK);
        }

        initUI();
    }

    private void initUI(){
        isbnView = (EditText) findViewById(R.id.book_isbn);
        nameView = (EditText) findViewById(R.id.book_name);
        authorView = (EditText) findViewById(R.id.book_author);
        publisherView = (EditText) findViewById(R.id.book_publisher);
        publishTimeBtn = (Button) findViewById(R.id.book_publish_time);
        stateView = (EditText) findViewById(R.id.book_state);
        positionView = (EditText) findViewById(R.id.book_position);
        remainNumView = (EditText) findViewById(R.id.book_remainingNum);
        okBtn = (Button) findViewById(R.id.okBtn);

        if(mode == MODIFY_MODE){
            LinearLayout layout = (LinearLayout) findViewById(R.id.isbnLayout);
            layout.setVisibility(View.INVISIBLE);
            isbnView.setText(String.valueOf(book.getIsbn()));
            nameView.setText(book.getName());
            authorView.setText(book.getAuthor());
            publisherView.setText(book.getPublisher());
            stateView.setText(book.getState());
            positionView.setText(book.getPosition());
            remainNumView.setText(String.valueOf(book.getRemainingNum()));
        }

        publishTimeBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.book_publish_time){
            Calendar c = Calendar.getInstance();
            if(mode == MODIFY_MODE){
                c.setTimeInMillis(book.getPublishTime());
            }else if(mode == ADD_MODE){
                c.setTimeInMillis(System.currentTimeMillis());
            }
            dialog = new DatePickerDialog(this,null,c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        }else if(id == R.id.okBtn){
            Book book = new Book();
            String str = isbnView.getText().toString();
            book.setIsbn(Integer.parseInt(str));
            book.setName(nameView.getText().toString());
            book.setAuthor(authorView.getText().toString());
            book.setPublisher(publisherView.getText().toString());
            book.setState(stateView.getText().toString());
            book.setPosition(positionView.getText().toString());
            book.setRemainingNum(Integer.parseInt(remainNumView.getText().toString()));
            int year = dialog.getDatePicker().getYear();
            int month = dialog.getDatePicker().getMonth();
            int day = dialog.getDatePicker().getDayOfMonth();
            Calendar c = Calendar.getInstance();
            c.set(year,month,day);
            book.setPublishTime(c.getTimeInMillis());

            Retrofit retrofit = new Retrofit.Builder().baseUrl(Configuration.ServerIp).addConverterFactory(GsonConverterFactory.create()).build();
            bookService service = retrofit.create(bookService.class);
            if(mode == MODIFY_MODE){
                service.modifyBook(book.getIsbn(),book).enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if(response.code() != 200){
                            Toast.makeText(BookActivity.this,"modify fail",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(BookActivity.this,"modify success",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        Toast.makeText(BookActivity.this,"network error",Toast.LENGTH_SHORT).show();
                    }
                });
            }else if(mode == ADD_MODE){
                service.addBook(book).enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if(response.code() != 200){
                            Toast.makeText(BookActivity.this,"add book fail",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(BookActivity.this,"add book success", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        Toast.makeText(BookActivity.this,"network error",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
