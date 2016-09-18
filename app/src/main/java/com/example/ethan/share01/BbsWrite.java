package com.example.ethan.share01;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpConnection;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;




public class BbsWrite extends AppCompatActivity implements View.OnClickListener {

    private EditText bbs_msg;
    private ImageView bbs_photo;

    private double mLat;
    private double mLon;

    private Button photo_select;
    private Button bbs_save;

    private String getBbs_msg;
    private String getBbs_photo_url;
    private String selected_Image_path;

    public Bitmap user_photo_bm;
    public static RbPreference mPref;
    private final String getmy_url = "https://toycom96.iptime.org:1443/bbs_getmy";
    private final String write_url = "https://toycom96.iptime.org:1443/bbs_write";
    private static final int GET_PICTURE_URI = 101;


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbs_write);

        Intent intent = getIntent();
        mLat = intent.getDoubleExtra("Lat", 0.0);
        mLon = intent.getDoubleExtra("Lon", 0.0);

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }*/

        init();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //ImageChooseUtil chooseImage = new ImageChooseUtil(data, getApplicationContext());

        switch (requestCode) {
            case GET_PICTURE_URI :
                if (resultCode == RESULT_OK) {

                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getContentResolver().query(data.getData(), filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    selected_Image_path = cursor.getString(columnIndex);
                    cursor.close();

                    user_photo_bm = BitmapFactory.decodeFile(selected_Image_path);
                    //bbs_photo.setImageBitmap(user_photo_bm);

                    UploadBbsImgTask bbs_photo_upload = new UploadBbsImgTask();
                    bbs_photo_upload.execute();
                }
        }
    }


    private void init(){
        bbs_msg = (EditText) findViewById(R.id.bbs_write_msg);
        bbs_photo = (ImageView) findViewById(R.id.bbs_write_photo);

        photo_select = (Button) findViewById(R.id.bbs_write_select);
        bbs_save = (Button) findViewById(R.id.bbs_write_save);

        photo_select.setOnClickListener(this);
        bbs_save.setOnClickListener(this);

        mPref = new RbPreference(BbsWrite.this);
        GetMyBbsThread info = new GetMyBbsThread();
        info.execute(getmy_url, mPref.getValue("auth", ""));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BbsWrite.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.bbs_write_save:

                String getComent = bbs_msg.getText().toString();
                BbsWriteThread bbs_save_thrd = new BbsWriteThread();
                bbs_save_thrd.execute(write_url, getComent, getBbs_photo_url);
                break;

            case R.id.bbs_write_select:

                int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE );
                }

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GET_PICTURE_URI);

                break;
        }
    }


    //이미지 파일 올리는 쓰레드
    class UploadBbsImgTask extends AsyncTask<Void, Void, String> {
        private String webAddressToPost = "https://toycom96.iptime.org:1443/up_file";

        private ProgressDialog dialog = new ProgressDialog(BbsWrite.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Uploading...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            //Log.e("response String before", s);
            getBbs_photo_url = s;
            //getPhotoPath = s.replace("[", "").replace("]", "");

            Log.e("response String after", getBbs_photo_url);
            Toast.makeText(getApplicationContext(), "file uploaded",
                    Toast.LENGTH_LONG).show();


            if (getBbs_photo_url != null && !getBbs_photo_url.equals("")) {
                try {
                    //Picasso.with(getApplicationContext()).load(getBbs_photo_url).error(R.drawable.ic_menu_noprofile).into(bbs_photo);
                    Picasso.with(getApplicationContext()).load(getBbs_photo_url).resize(640, 0).into(bbs_photo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else{
                bbs_photo.setImageResource(R.drawable.ic_menu_noprofile);
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection conn = null;
            MultipartEntity entity = null;
            ByteArrayOutputStream bos = null;
            ByteArrayBody bab = null;
            OutputStream os = null;

            try {
                URL url = new URL(webAddressToPost);
                conn = (HttpURLConnection) url.openConnection();
                //Http 접속
                conn.setConnectTimeout(10000);
                //접속 timeuot시간 설정
                conn.setReadTimeout(10000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.addRequestProperty("Cookie", mPref.getValue("auth", ""));

                entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);


                //File file = new File(selected_Image_path);
                //byte[] fileData = new byte[(int) file.length()];

                FileInputStream fis = new FileInputStream(selected_Image_path);
                byte[] fileData = new byte[(int) fis.available()];
                DataInputStream dis = new DataInputStream(fis);
                dis.readFully(fileData);
                dis.close();
                bab = new ByteArrayBody(fileData, "bbsimg.jpg");
                entity.addPart("imgfiles", bab);
                fis.close();


                /*bos = new ByteArrayOutputStream();
                user_photo_bm.compress(Bitmap.CompressFormat.JPEG, 85, bos);
                byte[] data = bos.toByteArray();
                bab = new ByteArrayBody(data, "bbsimg.jpg");
                entity.addPart("imgfiles", bab);*/


                conn.addRequestProperty("Content-length", entity.getContentLength() + "");
                conn.addRequestProperty(entity.getContentType().getName(), entity.getContentType().getValue());

                os = conn.getOutputStream();
                entity.writeTo(conn.getOutputStream());
                os.close();
                conn.connect();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.e("HTTP OK", "HTTP OK");
                    String result = readStream(conn.getInputStream());

                    //int i = 0;
                    JSONObject responseJSON = new JSONArray(result).getJSONObject(0);

                    String filename = responseJSON.get("Filename").toString();
                    //Log.e("result", filename);
                    return filename;
                } else {
                    Log.e("HTTP CODE", "HTTP CONN FAILED");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            entity = null;
            conn = null;
            bab = null;

            return null;
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuilder builder = new StringBuilder();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return builder.toString();
        }

    }

    /*
     * auth 값을 http header로 보내 사용자 정보를 받아오는 Thread
     */
    class GetMyBbsThread extends AsyncTask<String, Void, Void> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(BbsWrite.this);
            loading.setTitle("게시판 글쓰기");
            loading.setMessage("회원님의 이전 글을 조회 중이에요...");
            loading.setCancelable(false);
            loading.show();
            //loading = ProgressDialog.show(SignupActivity.this, "회원가입 중...", null,true,true);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //Toast.makeText(UserInfoEditActivity.this, "정보 확인", Toast.LENGTH_SHORT).show();

            bbs_msg.setText(getBbs_msg);
            if (getBbs_photo_url != null && !getBbs_photo_url.equals("")) {
                try {
                    Picasso.with(getApplicationContext()).load(getBbs_photo_url).error(R.drawable.ic_menu_noprofile).into(bbs_photo);
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
                    Log.i("Response Age Value", responseJSON.get("Msg").toString());
                    Log.i("Response Age Value", responseJSON.get("Media").toString());

                    getBbs_msg = responseJSON.get("Msg").toString();
                    getBbs_photo_url = responseJSON.get("Media").toString();
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
    class BbsWriteThread extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(BbsWrite.this, "게시글 저장 완료", Toast.LENGTH_SHORT).show();
            //mPref.put("user_nick", user_nick.getText().toString());
            Intent intent = new Intent(BbsWrite.this, MainActivity.class);
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
            String user_photo = value[2];


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
                job.put("media", user_photo);
                job.put("lat", mLat);
                job.put("long", mLon);

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
