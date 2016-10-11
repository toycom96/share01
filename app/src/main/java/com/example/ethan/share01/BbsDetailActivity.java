package com.example.ethan.share01;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BbsDetailActivity extends AppCompatActivity {

    private TextView bbs_title;
    private TextView bbs_msg;
    private TextView bbs_opt;
    private TextView bbs_etc;
    private ImageView bbs_photo;


    private String getBbs_title;
    private String getBbs_msg;
    private String getBbs_pay;
    private String getBbs_name;
    private String getBbs_age;
    private String getBbs_sex;
    private int getBbs_dist;
    private int getBbs_term;
    private String getBbs_cate1;
    private String getBbs_photo_url[] = {"","","","",""};

    private int bbs_id;
    private double mLat;
    private double mLon;
    private int photo_idx = 0;

    private final String bbs_detail_url = "https://toycom96.iptime.org:1443/bbs_view";
    public static RbPreference mPref;
    public GpsInfo mGps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbs_detail);

        Intent intent = getIntent();
        bbs_id = intent.getIntExtra("bbs_id", 0);

        init();
    }

    private void init(){
        mPref = new RbPreference(BbsDetailActivity.this);
        mGps = new GpsInfo(this);
        if (mGps.isGetLocation()) {
            mLat = mGps.getLatitude();
            mLon = mGps.getLongitude();
        } else {
            mLat = 0.0;
            mLon = 0.0;
        }

        bbs_title = (TextView) findViewById(R.id.bbs_detail_title);
        bbs_etc = (TextView) findViewById(R.id.bbs_detail_etc);
        bbs_msg = (TextView) findViewById(R.id.bbs_detail_msg);
        bbs_opt = (TextView) findViewById(R.id.bbs_detail_pay);
        bbs_photo = (ImageView) findViewById(R.id.bbs_detail_photo);

        bbs_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int viewId = v.getId();

                switch (viewId) {

                    case R.id.bbs_detail_photo:
                        photo_idx++;

                        if (getBbs_photo_url[photo_idx] != null && !getBbs_photo_url[photo_idx].equals("")) {
                            try {
                                Picasso.with(getApplicationContext()).load(getBbs_photo_url[photo_idx]).error(R.drawable.ic_menu_noprofile).into(bbs_photo);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (getBbs_photo_url[0] != null && !getBbs_photo_url[0].equals("")) {
                            photo_idx = 0;

                            try {
                                Picasso.with(getApplicationContext()).load(getBbs_photo_url[photo_idx]).error(R.drawable.ic_menu_noprofile).into(bbs_photo);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                                photo_idx = 0;
                                bbs_photo.setImageResource(R.drawable.ic_menu_noprofile);
                        }
                        break;
                }
            }
        });

        GetBbsDetailThread info = new GetBbsDetailThread();
        info.execute(bbs_detail_url, mPref.getValue("auth", ""));
    }

    class GetBbsDetailThread extends AsyncTask<String, Void, Void> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(BbsDetailActivity.this);
            loading.setTitle("공유 조회");
            loading.setMessage("회원님이 선택하신 공유물을 조회 중입니...");
            loading.setCancelable(false);
            loading.show();
            //loading = ProgressDialog.show(SignupActivity.this, "회원가입 중...", null,true,true);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //Toast.makeText(UserInfoEditActivity.this, "정보 확인", Toast.LENGTH_SHORT).show();

            bbs_title.setText(getBbs_title);
            bbs_msg.setText(getBbs_msg);
            bbs_opt.setText(getBbs_pay);
            bbs_etc.setText(getBbs_name + " / " + getBbs_sex + " " + getBbs_age + " / " + getBbs_dist + "km");
            if (getBbs_sex.equals("여")) {
                bbs_etc.setTextColor(Color.parseColor("#FF0000"));
            } else {
                bbs_etc.setTextColor(Color.parseColor("#0000FF"));
            }

            if (getBbs_photo_url[0] != null && !getBbs_photo_url[0].equals("")) {
                try {
                    Picasso.with(getApplicationContext()).load(getBbs_photo_url[0]).error(R.drawable.ic_menu_noprofile).into(bbs_photo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else{
                bbs_photo.setImageResource(R.drawable.ic_menu_noprofile);
            }

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




                JSONObject job = new JSONObject();
                //JSONObject 생성 후 input

                job.put("id", bbs_id);
                job.put("long", mLon);
                job.put("lat", mLat);

                os = conn.getOutputStream();
                //Output Stream 생성
                os.write(job.toString().getBytes("utf-8"));
                os.flush();



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
                    Log.i("Response Age Value", responseJSON.get("Msg").toString());
                    Log.i("Response Age Value", responseJSON.get("Media").toString());


                    getBbs_title = responseJSON.get("Title").toString();
                    getBbs_msg = responseJSON.get("Msg").toString();
                    getBbs_name = responseJSON.get("User_name").toString();
                    getBbs_age = responseJSON.get("User_age").toString() + "세";
                    if (responseJSON.get("User_sex").toString().equals("F")){
                        getBbs_sex = "여";
                    } else {
                        getBbs_sex = "남";
                    }
                    getBbs_dist = Integer.parseInt(responseJSON.get("Dist").toString());
                    getBbs_term = Integer.parseInt(responseJSON.get("Term").toString());

                    JSONObject optionJson = new JSONObject(responseJSON.getString("Option"));
                    getBbs_pay = "공유 댓가 : " + optionJson.getString("pay");

                    //getBbs_cate1 = responseJSON.get("Msg").toString();

                    JSONObject mediaJson = new JSONObject(responseJSON.getString("Media"));
                    getBbs_photo_url[0] = mediaJson.getString("img0");
                    getBbs_photo_url[1] = mediaJson.getString("img1");
                    getBbs_photo_url[2] = mediaJson.getString("img2");
                    getBbs_photo_url[3] = mediaJson.getString("img3");
                    getBbs_photo_url[4] = mediaJson.getString("img4");
                    //getBbs_photo_url = responseJSON.get("Media").toString();
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
