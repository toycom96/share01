package com.example.ethan.share01;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by Lai.OH on 16. 7. 11..
 */
public class UserInfoEditActivity extends AppCompatActivity implements View.OnClickListener{


    private EditText user_id;
    private EditText user_nick;
    private EditText user_age;
    private EditText user_coment;

    private Button infoedit_button;

    private String getUserComent;
    private String getUserNick;
    private String getUserAge;


    public static RbPreference mPref;
    private final String info_url = "https://toycom96.iptime.org:1443/user_info";
    private final String edit_url = "https://toycom96.iptime.org:1443/user_edit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_edit);

        init();

    }

    private void init(){
        user_id = (EditText) findViewById(R.id.infoedit_id);
        user_nick = (EditText) findViewById(R.id.infoedit_nick);
        user_age = (EditText) findViewById(R.id.infoedit_age);
        user_coment = (EditText) findViewById(R.id.infoedit_coment);
        infoedit_button = (Button) findViewById(R.id.infoedit_button);


        infoedit_button.setOnClickListener(this);

        mPref = new RbPreference(UserInfoEditActivity.this);
        GetUserInfoThread info = new GetUserInfoThread();
        info.execute(info_url, mPref.getValue("auth", ""));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UserInfoEditActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.infoedit_button :
                String getComent = user_coment.getText().toString();
                EditUserInfoThread editInfo = new EditUserInfoThread();
                editInfo.execute(edit_url,getComent);
        }
    }


    /*
     * auth 값을 http header로 보내 사용자 정보를 받아오는 Thread
     */
    class GetUserInfoThread extends AsyncTask<String, Void, Void> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(UserInfoEditActivity.this);
            loading.setTitle("회원정보수정");
            loading.setMessage("회원님의 정보를 받는 중이에요...");
            loading.setCancelable(false);
            loading.show();
            //loading = ProgressDialog.show(SignupActivity.this, "회원가입 중...", null,true,true);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //Toast.makeText(UserInfoEditActivity.this, "정보 확인", Toast.LENGTH_SHORT).show();

            user_id.setText(mPref.getValue("user_id", ""));
            user_nick.setText(getUserNick);
            user_age.setText(getUserAge);
            user_coment.setText(getUserComent);
            loading.dismiss();
        }

        @Override
        protected Void doInBackground(String... value) {
            HttpURLConnection conn = null;
            OutputStream os = null;
            InputStream is = null;
            ByteArrayOutputStream baos = null;
            String response = null;
            /*
            http통신 부분 설정 변수들
             */
            String connUrl = value[0];
            String user_auth = value[1];

            try {
                IgnoreHttpSertification.ignoreSertificationHttps();
                //String url = "https://toycom96.iptime.org:1443/user_join";
                URL obj = new URL(connUrl);
                //접속 Server URL 설정
                conn = (HttpURLConnection) obj.openConnection();
                //Http 접속
                conn.setConnectTimeout(10000);
                //접속 timeuot시간 설정
                conn.setReadTimeout(10000);
                //read timeout 시간 설정
                conn.setRequestMethod("POST");
                //통신 방식 : POST

                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                //데이터 주고 받는 형식 : json 설정
                Log.e("user_auth", user_auth);
                conn.addRequestProperty("Cookie", user_auth);
                //Cookie값 설정(auth)
                conn.setDoOutput(true);
                conn.setDoInput(true);


                os = conn.getOutputStream();
                //Output Stream 생성
                os.flush();
                //Buffer에 있는 모든 정보를 보냄

                int responseCode = conn.getResponseCode();

                //int responseCode = conn.getResponseCode();
                //request code를 받음

                if(responseCode == HttpURLConnection.HTTP_OK) {

                    Log.e("HTTP_OK", "HTTP OK RESULT");
                    is = conn.getInputStream();
                    baos = new ByteArrayOutputStream();
                    byte[] byteBuffer = new byte[1024];
                    byte[] byteData = null;
                    int nLength = 0;
                    while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                        baos.write(byteBuffer, 0, nLength);
                    }
                    byteData = baos.toByteArray();

                    response = new String(byteData);
                    //Json 문자열로 온 데이터값을 저장함( ex.> {"key":value} )
                    Log.i("Response Data", response);
                    JSONObject responseJSON = new JSONObject(response);
                    //JSONObject를 생성해 key값 설정으로 result값을 받음.
                    Log.i("Response Nick Value", responseJSON.get("Name").toString());
                    Log.i("Response Age Value", responseJSON.get("Age").toString());
                    Log.i("Response Age Value", responseJSON.get("Msg").toString());

                    getUserNick = responseJSON.get("Name").toString();
                    getUserAge = responseJSON.get("Age").toString();
                    getUserComent = responseJSON.get("Msg").toString();
                }else {
                    Log.e("HTTP_ERROR", "NOT CONNECTED HTTP");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /*
     * 사용자 정보 수정 완료 Thread
     */
    class EditUserInfoThread extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(UserInfoEditActivity.this, "정보수정 완료", Toast.LENGTH_SHORT).show();
            mPref.put("user_nick", user_nick.getText().toString());
            Intent intent = new Intent(UserInfoEditActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        protected Void doInBackground(String... value) {
            HttpURLConnection conn = null;
            OutputStream os = null;
            InputStream is = null;
            ByteArrayOutputStream baos = null;
            String response = null;
            /*
            http통신 부분 설정 변수들
             */
            String connUrl = value[0];
            String user_coment = value[1];


            try {
                IgnoreHttpSertification.ignoreSertificationHttps();
                //String url = "https://toycom96.iptime.org:1443/user_join";
                URL obj = new URL(connUrl);
                //접속 Server URL 설정
                conn = (HttpURLConnection) obj.openConnection();
                //Http 접속
                conn.setConnectTimeout(10000);
                //접속 timeuot시간 설정
                conn.setReadTimeout(10000);
                //read timeout 시간 설정
                conn.setRequestMethod("POST");
                //통신 방식 : POST

                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                //데이터 주고 받는 형식 : json 설정
                conn.addRequestProperty("Cookie", mPref.getValue("auth",""));
                //Cookie값 설정(auth)
                conn.setDoOutput(true);
                conn.setDoInput(true);


                JSONObject job = new JSONObject();
                //JSONObject 생성 후 input
                job.put("msg", user_coment);

                os = conn.getOutputStream();
                //Output Stream 생성
                os.write(job.toString().getBytes("utf-8"));
                os.flush();
                //Buffer에 있는 모든 정보를 보냄

                int responseCode = conn.getResponseCode();
                //request code를 받음

                if(responseCode == HttpURLConnection.HTTP_OK) {

                    Log.e("HTTP_OK", "HTTP OK RESULT");
                    is = conn.getInputStream();
                    baos = new ByteArrayOutputStream();
                    byte[] byteBuffer = new byte[1024];
                    byte[] byteData = null;
                    int nLength = 0;
                    while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                        baos.write(byteBuffer, 0, nLength);
                    }
                    byteData = baos.toByteArray();

                    response = new String(byteData);
                    //Json 문자열로 온 데이터값을 저장함( ex.> {"key":value} )
                    Log.i("Response Data", response);
                    JSONObject responseJSON = new JSONObject(response);
                    //JSONObject를 생성해 key값 설정으로 result값을 받음.
                    Log.i("Response ID Value", responseJSON.get("result").toString());
                    String result = responseJSON.get("result").toString();
                    //Toast.makeText(this, "Your id value : : " + result, Toast.LENGTH_SHORT);
                    Log.i("responese value", "DATA response = " + result);
                }else {
                    Log.e("HTTP_ERROR", "NOT CONNECTED HTTP");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
