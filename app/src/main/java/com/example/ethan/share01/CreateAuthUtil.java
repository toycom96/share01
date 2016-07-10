package com.example.ethan.share01;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by OHRok on 2016-07-11.
 */

class CreateAuthUtil extends AsyncTask<String, Void, Void> {
    private final String request_auth_url = "https://toycom96.iptime.org:1443/auth_update";
    private RbPreference mPref;
    private Context context;

    public CreateAuthUtil(Context context) {
        this.context = context;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Toast.makeText(SignupActivity.this, "회원가입 완료", Toast.LENGTH_SHORT).show();
        //mPref.put("is_login",true);

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
        int user_num = Integer.parseInt(value[0]);
        String user_device_id = value[1];
        Log.e("user_num", String.valueOf(user_num));
        Log.e("userDevice", user_device_id);


        try {
            IgnoreHttpSertification.ignoreSertificationHttps();
            //String url = "https://toycom96.iptime.org:1443/user_join";
            URL obj = new URL(request_auth_url);
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
            //conn.setRequestProperty("Cookie", "auth=NtUMVRdHefRNbYut82ALIz0hFKOyRM4D13krg/xdxWfrhThgkmDJTAbs7A3fbhd4lu4cIg==");
            //Cookie값 설정(auth)
            conn.setDoOutput(true);
            conn.setDoInput(true);


            JSONObject job = new JSONObject();
            //JSONObject 생성 후 input
            job.put("id", user_num);
            job.put("device_id", user_device_id);

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

                List<String> cookies = conn.getHeaderFields().get("Set-Cookie");
                Log.e("cookies.get(0)",cookies.get(0));
                if (cookies != null) {
                    for (String cookie : cookies) {
                        String getAuth = cookie.split(";")[0];
                        Log.e("@COOKIE", getAuth);
                        mPref = new RbPreference(context);
                        mPref.put("auth", getAuth);
                    }

                } else {
                    Log.e("cookies", "cookie null");
                }


            }else {
                Log.e("HTTP_ERROR", "NOT CONNECTED HTTP");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}