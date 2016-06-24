package com.example.ethan.share01;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText user_name_edt;
    private EditText user_age_edt;
    private EditText user_phone_edt;

    private Button sign_up_btn;

    private String getUserName;
    private String getUserAge;
    private String getUserPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();
        sign_up_btn.setOnClickListener(this);
    }

    private void init(){
        user_name_edt = (EditText) findViewById(R.id.signup_user_name);
        user_age_edt = (EditText) findViewById(R.id.signup_user_age);
        user_phone_edt = (EditText) findViewById(R.id.signup_user_phonenum);
        sign_up_btn = (Button) findViewById(R.id.signup_button);


    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup_button :
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
