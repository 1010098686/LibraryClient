package com.frederic.project.libraryclient;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.frederic.project.libraryclient.Util.Configuration;
import com.frederic.project.libraryclient.Util.RequestBodyBuilder;
import com.frederic.project.libraryclient.httpServices.bookService;
import com.frederic.project.libraryclient.httpServices.infoService;
import com.frederic.project.libraryclient.httpServices.readerService;
import com.frederic.project.libraryclient.models.Book;
import com.frederic.project.libraryclient.models.Reader;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lumenghz.com.pullrefresh.PullToRefreshView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.frederic.project.libraryclient.R.id.return_time_layout;

public class UserActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    public static final String USER = "user";

    private PullToRefreshView mPullToRefreshView;
    private ListView listView;
    private Button borrowBookBtn;
    private Button borrowHistoryBtn;

    private Reader reader = null;
    private int currentView = 1;
    private BookInfoAdapter adapter = new BookInfoAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        reader = (Reader)getIntent().getBundleExtra("data").getSerializable(USER);
        initUI();
    }

    private Callback<List<Book>> callback = new Callback<List<Book>>() {
        @Override
        public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
            if(response.code() != 200){
                Toast.makeText(UserActivity.this,"network error",Toast.LENGTH_SHORT).show();
                mPullToRefreshView.setRefreshing(false);
            }else{
                List<Book> list = response.body();
                adapter.setList(list);
                adapter.notifyDataSetChanged();
                mPullToRefreshView.setRefreshing(false);
            }
        }

        @Override
        public void onFailure(Call<List<Book>> call, Throwable t) {
            Toast.makeText(UserActivity.this,"network error",Toast.LENGTH_SHORT).show();
            mPullToRefreshView.setRefreshing(false);
        }
    };

    private void initUI(){
        borrowBookBtn = (Button) findViewById(R.id.borrowbookBtn);
        borrowHistoryBtn = (Button) findViewById(R.id.borrowhistoryBtn);
        borrowBookBtn.setSelected(true);
        borrowHistoryBtn.setSelected(false);
        borrowBookBtn.setOnClickListener(this);
        borrowHistoryBtn.setOnClickListener(this);

        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pullToRefresh);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);


        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Retrofit retrofit = new Retrofit.Builder().baseUrl(Configuration.ServerIp).addConverterFactory(GsonConverterFactory.create()).build();
                readerService service = retrofit.create(readerService.class);
                if(currentView == 1){
                    service.getBorrowBooks(reader.getId()).enqueue(callback);
                }else if(currentView == 2){
                    service.getBorrowedBooks(reader.getId()).enqueue(callback);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_search_book){
            Intent intent = new Intent(UserActivity.this, SearchActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt(SearchActivity.SEARCH_MODE,SearchActivity.SEARCH_BOOK);
            bundle.putSerializable("reader", reader);
            intent.putExtra("data",bundle);
            startActivity(intent);
        }else if(id == R.id.menu_update_info){
            final View view = View.inflate(this,R.layout.update_reader_info,null);
            final EditText nameView = (EditText) view.findViewById(R.id.update_reader_name);
            final EditText departmentView = (EditText) view.findViewById(R.id.update_reader_department);
            final Spinner sexSpinner = (Spinner) view.findViewById(R.id.update_reader_sex);
            final Button btn = (Button) view.findViewById(R.id.update_reader_birthday);
            nameView.setText(reader.getName());
            departmentView.setText(reader.getDepartment());
            sexSpinner.setSelection(reader.getSex().equals("male")?0:1);
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(reader.getBirthday());
            final DatePickerDialog dialog = new DatePickerDialog(this,null,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.show();
                }
            });
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("update information");
            builder.setView(view);
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String name = nameView.getText().toString();
                    String department = departmentView.getText().toString();
                    String sex = sexSpinner.getSelectedItemPosition()==0?"male":"female";
                    DatePicker picker = dialog.getDatePicker();
                    calendar.set(picker.getYear(),picker.getMonth(),picker.getDayOfMonth());
                    long time = calendar.getTimeInMillis();
                    final Reader r = new Reader();
                    r.setId(reader.getId());
                    r.setName(name);
                    r.setDepartment(department);
                    r.setSex(sex);
                    r.setPassword(reader.getPassword());
                    r.setBirthday(time);
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(Configuration.ServerIp).addConverterFactory(GsonConverterFactory.create()).build();
                    infoService service = retrofit.create(infoService.class);
                    service.changeReaderInfo(reader.getId(),r).enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            if(response.code() != 200){
                                Toast.makeText(UserActivity.this,"modify information fail",Toast.LENGTH_LONG).show();
                            }else{
                                reader = r;
                                Toast.makeText(UserActivity.this,"modify information success",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            Toast.makeText(UserActivity.this,"network error",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            builder.create().show();
        }else if(id == R.id.menu_change_passwd){
            final View view = View.inflate(this,R.layout.change_password,null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("change password");
            builder.setView(view);
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EditText oldPasswdEdit = (EditText) view.findViewById(R.id.oldpassword);
                    EditText newpasswdEdit = (EditText) view.findViewById(R.id.newpassword);
                    EditText verifypasswdEdit = (EditText) view.findViewById(R.id.verifypassword);
                    String oldpasswd = oldPasswdEdit.getText().toString();
                    final String newpasswd = newpasswdEdit.getText().toString();
                    String verifypasswd = verifypasswdEdit.getText().toString();
                    if(!verifypasswd.equals(newpasswd)){
                        Toast.makeText(UserActivity.this,"the new password you input twice is not consistent", Toast.LENGTH_LONG).show();
                    }else{
                        RequestBodyBuilder requestBodyBuilder = new RequestBodyBuilder();
                        try {
                            requestBodyBuilder.addValue("oldpassword",oldpasswd);
                            requestBodyBuilder.addValue("password",newpasswd);
                            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),requestBodyBuilder.build());
                            Retrofit retrofit = new Retrofit.Builder().baseUrl(Configuration.ServerIp).addConverterFactory(GsonConverterFactory.create()).build();
                            infoService service = retrofit.create(infoService.class);
                            service.changeReaderPassword(reader.getId(),requestBody).enqueue(new Callback<Integer>() {
                                @Override
                                public void onResponse(Call<Integer> call, Response<Integer> response) {
                                    if(response.code() != 200){
                                        Toast.makeText(UserActivity.this,"modify password fails",Toast.LENGTH_LONG).show();
                                    }else{
                                        reader.setPassword(newpasswd);
                                        Toast.makeText(UserActivity.this,"modify password success",Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Integer> call, Throwable t) {
                                    Toast.makeText(UserActivity.this,"network error",Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            builder.setNegativeButton("cancel",null);
            builder.create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.borrowbookBtn){
            if(currentView == 2){
                adapter.clear();
                adapter.notifyDataSetChanged();
                currentView = 1;
                borrowBookBtn.setSelected(true);
                borrowHistoryBtn.setSelected(false);

            }
        }else if(id == R.id.borrowhistoryBtn){
            if(currentView == 1){
                adapter.clear();
                adapter.notifyDataSetChanged();
                currentView = 2;
                borrowBookBtn.setSelected(false);
                borrowHistoryBtn.setSelected(true);

            }
        }
    }

    public static void setBookInfo(View view, Book book, int mode){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TextView isbn = (TextView) view.findViewById(R.id.detail_book_isbn);
        TextView name = (TextView) view.findViewById(R.id.detail_book_name);
        TextView author = (TextView) view.findViewById(R.id.detail_book_author);
        TextView publisher = (TextView) view.findViewById(R.id.detail_book_publisher);
        TextView publishTime = (TextView) view.findViewById(R.id.detail_book_publish_time);
        TextView position = (TextView) view.findViewById(R.id.detail_book_position);
        TextView remainingNum = (TextView) view.findViewById(R.id.detail_book_remaingNum);
        TextView borrowTime = (TextView) view.findViewById(R.id.detail_book_borrow_time);
        TextView returnTIme = (TextView) view.findViewById(R.id.detail_book_return_time);
        TextView penalty = (TextView) view.findViewById(R.id.detail_book_penalty);
        LinearLayout borrowTimeLayout = (LinearLayout) view.findViewById(R.id.borrow_time_layout);
        LinearLayout returnTimeLayout = (LinearLayout) view.findViewById(R.id.return_time_layout);
        LinearLayout penaltyLayout = (LinearLayout) view.findViewById(R.id.penalty_layout);
        isbn.setText(String.valueOf(book.getIsbn()));
        name.setText(book.getName());
        author.setText(book.getAuthor());
        publisher.setText(book.getPublisher());
        publishTime.setText(df.format(new Date(book.getPublishTime())));
        position.setText(book.getPosition());
        remainingNum.setText(String.valueOf(book.getRemainingNum()));
        borrowTime.setText(df.format(new Date(book.getBorrowTime())));
        returnTIme.setText(df.format(new Date(book.getReturnTime())));
        DecimalFormat ndf = new DecimalFormat("######0.00");
        penalty.setText(ndf.format(book.getPenalty()));
        if(mode == 0) {//borrow
            returnTimeLayout.setVisibility(View.INVISIBLE);
            penaltyLayout.setVisibility(View.INVISIBLE);
        }else if(mode == 1){//borrow history
            //do nothing
        }else if(mode == 2){//info
            borrowTimeLayout.setVisibility(View.INVISIBLE);
            returnTimeLayout.setVisibility(View.INVISIBLE);
            penaltyLayout.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        View displayView = View.inflate(this,R.layout.detail_book_info,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("book detail information");
        Book book = adapter.getItem(i);
        if(currentView == 1){
            setBookInfo(displayView, book, 0);
        }else if(currentView == 2){
            setBookInfo(displayView, book, 1);
        }
        builder.setView(displayView);
        builder.create().show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int index, long l) {
        if(currentView == 2) return true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Operations");
        String[] operations = new String[]{"reborrow","return"};
        builder.setItems(operations, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Retrofit retrofit = new Retrofit.Builder().baseUrl(Configuration.ServerIp).addConverterFactory(GsonConverterFactory.create()).build();
                bookService service = retrofit.create(bookService.class);
                if(i == 0){
                    service.reborrowBook(reader.getId(), adapter.getItem(index).getIsbn()).enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            if(response.code() == 401){
                                Toast.makeText(UserActivity.this,"your borrow is out of date",Toast.LENGTH_LONG).show();
                            }else if(response.code() == 200){
                                Toast.makeText(UserActivity.this,"reborrow ok",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            Toast.makeText(UserActivity.this, "network error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else if(i == 1){
                    service.returnBook(reader.getId(), adapter.getItem(index).getIsbn()).enqueue(new Callback<Double>() {
                        @Override
                        public void onResponse(Call<Double> call, Response<Double> response) {
                            if(response.code() != 200){
                                Toast.makeText(UserActivity.this,"network error", Toast.LENGTH_SHORT).show();
                            }else{
                                double penalty = response.body();
                                Toast.makeText(UserActivity.this, "return successfully and the penalty is:"+penalty, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Double> call, Throwable t) {
                            Toast.makeText(UserActivity.this,"network error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        builder.create().show();
        return true;
    }
}
