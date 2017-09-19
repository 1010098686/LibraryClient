package com.frederic.project.libraryclient;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.frederic.project.libraryclient.Util.Configuration;
import com.frederic.project.libraryclient.httpServices.bookService;
import com.frederic.project.libraryclient.httpServices.infoService;
import com.frederic.project.libraryclient.models.Administrator;
import com.frederic.project.libraryclient.models.Book;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String ADMIN = "admin";

    private Administrator administrator = null;

    private Button addBookBtn;
    private Button deleteBookBtn;
    private Button modifyBookBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        administrator = (Administrator)getIntent().getBundleExtra("data").getSerializable(ADMIN);

        initUI();
    }

    private void initUI(){
        addBookBtn = (Button) findViewById(R.id.addBookBtn);
        deleteBookBtn = (Button) findViewById(R.id.deleteBookBtn);
        modifyBookBtn = (Button) findViewById(R.id.modifyBookBtn);
        addBookBtn.setOnClickListener(this);
        deleteBookBtn.setOnClickListener(this);
        modifyBookBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.addBookBtn){
            Intent i = new Intent(AdminActivity.this,BookActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt(BookActivity.MODE,BookActivity.ADD_MODE);
            i.putExtra("data",bundle);
            startActivity(i);
        }else if(id == R.id.modifyBookBtn){
            final EditText editText = new EditText(this);
            editText.setHint("ISBN");
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("book ISBN");
            builder.setView(editText);
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    int isbn = Integer.parseInt(editText.getText().toString());
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(Configuration.ServerIp).addConverterFactory(GsonConverterFactory.create()).build();
                    infoService service = retrofit.create(infoService.class);
                    service.queryBook(isbn).enqueue(new Callback<Book>() {
                        @Override
                        public void onResponse(Call<Book> call, Response<Book> response) {
                            if(response.code() != 200){
                                Toast.makeText(AdminActivity.this,"query book fail",Toast.LENGTH_SHORT).show();
                            }else{
                                Book book = response.body();
                                Intent i = new Intent(AdminActivity.this,BookActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt(BookActivity.MODE,BookActivity.MODIFY_MODE);
                                bundle.putSerializable(BookActivity.BOOK,book);
                                i.putExtra("data",bundle);
                                startActivity(i);
                            }
                        }

                        @Override
                        public void onFailure(Call<Book> call, Throwable t) {
                            Toast.makeText(AdminActivity.this,"network error",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            builder.create().show();
        }else if(id == R.id.deleteBookBtn){
            final EditText editText = new EditText(this);
            editText.setHint("ISBN");
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("book ISBN");
            builder.setView(editText);
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    int isbn = Integer.parseInt(editText.getText().toString());
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(Configuration.ServerIp).addConverterFactory(GsonConverterFactory.create()).build();
                    bookService service = retrofit.create(bookService.class);
                    service.deleteBook(isbn).enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            if(response.code() != 200){
                                Toast.makeText(AdminActivity.this,"no such book to delete",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(AdminActivity.this,"delete success",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            Toast.makeText(AdminActivity.this,"network error",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            builder.create().show();
        }
    }
}
