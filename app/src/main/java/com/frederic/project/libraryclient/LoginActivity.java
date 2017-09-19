package com.frederic.project.libraryclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.frederic.project.libraryclient.Util.Configuration;
import com.frederic.project.libraryclient.Util.RequestBodyBuilder;
import com.frederic.project.libraryclient.httpServices.loginService;
import com.frederic.project.libraryclient.models.Administrator;
import com.frederic.project.libraryclient.models.Reader;

import org.json.JSONException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http1.Http1Codec;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener, AdapterView.OnItemSelectedListener {

    //UI Reference
    private EditText userIdText;
    private EditText userPasswordText;
    private Spinner loginTypeSpinner;
    private Button loginBtn;

    private int loginType = 0;
    private int userId;
    private String userPassword;

    private boolean userIdValidation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();
    }

    private void initUI() {
        userIdText = (EditText) findViewById(R.id.userId);
        userPasswordText = (EditText) findViewById(R.id.userPassword);
        loginTypeSpinner = (Spinner) findViewById(R.id.loginType);
        loginBtn = (Button) findViewById(R.id.loginBtn);

        userIdText.addTextChangedListener(this);
        loginTypeSpinner.setOnItemSelectedListener(this);
        loginBtn.setOnClickListener(this);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String str = s.toString();
        if (str.equals("")) return;
        try {
            userId = Integer.parseInt(str);
            userIdValidation = true;
        } catch (Exception e) {
            Toast.makeText(this, "user id must be a number", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        if (!userIdValidation) {
            Toast.makeText(this, "make sure the user id is validate", Toast.LENGTH_SHORT).show();
            return;
        }
        userPassword = userPasswordText.getText().toString();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Configuration.ServerIp).addConverterFactory(GsonConverterFactory.create()).build();
        loginService service = retrofit.create(loginService.class);
        RequestBodyBuilder builder = new RequestBodyBuilder();
        try {
            builder.addValue("id",userId);
            builder.addValue("password",userPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = builder.build();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),json);
        if(loginType == 0) { // user mode
            service.loginAsReader(body).enqueue(new Callback<Reader>() {
                @Override
                public void onResponse(Call<Reader> call, Response<Reader> response) {
                    int code = response.code();
                    if(code == 401){
                        Toast.makeText(LoginActivity.this,"no such user or wrong password",Toast.LENGTH_SHORT).show();
                    }else if(code == 200){
                        Reader reader = response.body();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(UserActivity.USER, reader);
                        Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                        intent.putExtra("data", bundle);
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<Reader> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "login fail", Toast.LENGTH_SHORT).show();
                }
            });
        }else if(loginType == 1){//admin mode
            service.loginAsAdmin(body).enqueue(new Callback<Administrator>() {
                @Override
                public void onResponse(Call<Administrator> call, Response<Administrator> response) {
                    int code = response.code();
                    if(code == 401){
                        Toast.makeText(LoginActivity.this,"no such administrator or wrong password",Toast.LENGTH_SHORT).show();
                    }else if(code == 200){
                        Administrator admin = response.body();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(AdminActivity.ADMIN, admin);
                        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                        intent.putExtra("data", bundle);
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<Administrator> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "login fail", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        loginType = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
