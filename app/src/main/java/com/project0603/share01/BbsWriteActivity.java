package com.project0603.share01;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;




public class BbsWriteActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner bbs_cate1;
    private EditText bbs_title;
    private EditText bbs_msg;
    private EditText bbs_pay;
    private ImageView main_photo;
    private ImageView sub_photo1;
    private ImageView sub_photo2;
    private ImageView sub_photo3;
    private ImageView sub_photo4;
    private String saved_image_url[] = {"","","","",""};



    //private double mLat;
    //private double mLon;

    private Button photo_select;
    private Button bbs_save;

    private int getBbs_cate1;
    private String getBbs_title;
    private String getBbs_msg;
    private String getBbs_pay;
    private String getBbs_photo_url;
    private String selected_Image_path;

    private int getBbs_id;
    private int getEdit_flag;   //0은 새로운 글쓰기 모드, 1은 수정 모드

    public Bitmap main_photo_bm;
    public Bitmap sub_photo_bm1;
    public Bitmap sub_photo_bm2;
    public Bitmap sub_photo_bm3;
    public Bitmap sub_photo_bm4;
    //private final String getmy_url = GlobalVar.https_api1 + "/bbs_getmy";
    private final String getmy_url = GlobalVar.https_api1 + "/bbs_view";
    private final String write_url = GlobalVar.https_api1 + "/bbs_write";
    private final String update_url = GlobalVar.https_api1 + "/bbs_edit";
    private static final int GET_PICTURE_URI = 101;
    private int photo_info_flag = 0;


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
        //mLat = intent.getDoubleExtra("Lat", 0.0);
        //mLon = intent.getDoubleExtra("Lon", 0.0);
        getBbs_id = intent.getIntExtra("Bbs_id", 0);
        if (getBbs_id == 0) {
            getEdit_flag = 0;
        } else {
            getEdit_flag = 1;
        }
        GlobalVar.detail_enter_flag = 0;

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }*/

        init();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap loadedBitmap = BitmapFactory.decodeFile(picturePath);

            ExifInterface exif = null;
            try {
                File pictureFile = new File(picturePath);
                exif = new ExifInterface(pictureFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            int orientation = ExifInterface.ORIENTATION_NORMAL;

            if (exif != null)
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    loadedBitmap = rotateBitmap(loadedBitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    loadedBitmap = rotateBitmap(loadedBitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    loadedBitmap = rotateBitmap(loadedBitmap, 270);
                    break;
            }

            File file = new File(getExternalCacheDir(), "unlimited_share_image.jpg");
            try {
                FileOutputStream out = new FileOutputStream(file);
                loadedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                Log.e("Image", "Convert");
            }

            UploadBbsImgTask bbs_photo_upload = new UploadBbsImgTask();
            bbs_photo_upload.execute();


        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    private void init(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.bbs_write_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        bbs_cate1 = (Spinner) findViewById(R.id.bbs_write_cate1);
        bbs_title = (EditText) findViewById(R.id.bbs_write_title);
        bbs_pay = (EditText) findViewById(R.id.bbs_write_pay);
        bbs_msg = (EditText) findViewById(R.id.bbs_write_msg);
        main_photo = (ImageView) findViewById(R.id.bbs_main_photo);
        sub_photo1 = (ImageView) findViewById(R.id.bbs_sub_photo1);
        sub_photo2 = (ImageView) findViewById(R.id.bbs_sub_photo2);
        sub_photo3 = (ImageView) findViewById(R.id.bbs_sub_photo3);
        sub_photo4 = (ImageView) findViewById(R.id.bbs_sub_photo4);
        saved_image_url[0] = ""; saved_image_url[1] =""; saved_image_url[2]=""; saved_image_url[3]=""; saved_image_url[4]="";


        //photo_select = (Button) findViewById(R.id.bbs_write_select);
        bbs_save = (Button) findViewById(R.id.bbs_write_save);

        main_photo.setOnClickListener(this);
        sub_photo1.setOnClickListener(this);
        sub_photo2.setOnClickListener(this);
        sub_photo3.setOnClickListener(this);
        sub_photo4.setOnClickListener(this);
        bbs_save.setOnClickListener(this);

        if (getEdit_flag == 1) {
            GetMyBbsThread info = new GetMyBbsThread();
            info.execute(getmy_url, Profile.auth);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BbsWriteActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();


        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE );
        }

        switch (viewId) {

            case R.id.bbs_main_photo:
                photo_info_flag = 0;
                break;
            case R.id.bbs_sub_photo1:
                photo_info_flag = 1;
                break;
            case R.id.bbs_sub_photo2:
                photo_info_flag = 2;
                break;
            case R.id.bbs_sub_photo3:
                photo_info_flag = 3;
                break;
            case R.id.bbs_sub_photo4:
                photo_info_flag = 4;
                break;
            case R.id.bbs_write_save:
                photo_info_flag = -1;
                break;
        }

        /*case R.id.bbs_write_photo;

            String getComent = bbs_msg.getText().toString();
            BbsWriteThread bbs_save_thrd = new BbsWriteThread();
            bbs_save_thrd.execute(write_url, getComent, getBbs_photo_url);
            break;*/
        if (photo_info_flag < 0) {
            bbs_save.setEnabled(false);
            String getComent = bbs_msg.getText().toString();
            String getTitle = bbs_title.getText().toString();
            String getPay = bbs_pay.getText().toString();

            if (bbs_cate1.getSelectedItemPosition() == 0) {
                Toast.makeText(getApplicationContext(), "공유할 종류를 선택해 주세요.", Toast.LENGTH_SHORT).show();
                bbs_save.setEnabled(true);
                return;
            } else {
                getBbs_cate1 = bbs_cate1.getSelectedItemPosition();
            }


            if (getEdit_flag == 0 ) {
                BbsWriteThread bbs_save_thrd = new BbsWriteThread();
                bbs_save_thrd.execute(write_url, getTitle, getComent, getPay);
            } else {
                BbsWriteThread bbs_save_thrd = new BbsWriteThread();
                bbs_save_thrd.execute(update_url, getTitle, getComent, getPay);
            }
        } else {
            if (photo_info_flag > 0 && saved_image_url[photo_info_flag - 1].equals("")) {
                Toast.makeText(getApplicationContext(), "이미지를 순서대로 선택해 주세요.", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GET_PICTURE_URI);
            }
        }

    }


    //이미지 파일 올리는 쓰레드
    class UploadBbsImgTask extends AsyncTask<Void, Void, String> {
        private String webAddressToPost = GlobalVar.https_api1 + "/up_file";

        private ProgressDialog dialog = new ProgressDialog(BbsWriteActivity.this);
        private int req_code = 0;

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
                    saved_image_url[photo_info_flag]=getBbs_photo_url;
                    if (photo_info_flag == 0) {
                        Picasso.with(getApplicationContext()).load(getBbs_photo_url).resize(640, 0).into(main_photo);
                    } else if (photo_info_flag == 1) {
                        Picasso.with(getApplicationContext()).load(getBbs_photo_url).resize(640, 0).into(sub_photo1);
                    } else if (photo_info_flag == 2) {
                        Picasso.with(getApplicationContext()).load(getBbs_photo_url).resize(640, 0).into(sub_photo2);
                    } else if (photo_info_flag == 3) {
                        Picasso.with(getApplicationContext()).load(getBbs_photo_url).resize(640, 0).into(sub_photo3);
                    } else if (photo_info_flag == 4) {
                        Picasso.with(getApplicationContext()).load(getBbs_photo_url).resize(640, 0).into(sub_photo4);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else{
                saved_image_url[photo_info_flag]="";
                if (photo_info_flag == 0) {
                    main_photo.setImageResource(R.drawable.ic_menu_noprofile);
                } else if (photo_info_flag == 1) {
                    sub_photo1.setImageResource(R.drawable.ic_menu_noprofile);
                } else if (photo_info_flag == 2) {
                    sub_photo2.setImageResource(R.drawable.ic_menu_noprofile);
                } else if (photo_info_flag == 3) {
                    sub_photo3.setImageResource(R.drawable.ic_menu_noprofile);
                } else if (photo_info_flag == 4) {
                    sub_photo4.setImageResource(R.drawable.ic_menu_noprofile);
                }
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
                conn.addRequestProperty("Cookie", Profile.auth);

                entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);


                //File file = new File(selected_Image_path);
                //byte[] fileData = new byte[(int) file.length()];

                File file = new File(getExternalCacheDir(), "unlimited_share_image.jpg");
                FileInputStream fis = new FileInputStream(file);
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
            loading = new ProgressDialog(BbsWriteActivity.this);
            loading.setTitle("게시물 수정하기");
            loading.setMessage("회원님의 이전 글을 조회 중이에요...");
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
            bbs_pay.setText(getBbs_pay);


            if (saved_image_url[0] != null && !saved_image_url[0].equals("")) {
                try {
                    Picasso.with(getApplicationContext()).load(saved_image_url[0]).error(R.drawable.ic_menu_noprofile).into(main_photo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else{
                main_photo.setImageResource(R.drawable.ic_menu_noprofile);
            }
            if (saved_image_url[1] != null && !saved_image_url[1].equals("")) {
                try {
                    Picasso.with(getApplicationContext()).load(saved_image_url[1]).error(R.drawable.ic_menu_noprofile).into(sub_photo1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else{
                sub_photo1.setImageResource(R.drawable.ic_menu_noprofile);
            }
            if (saved_image_url[2] != null && !saved_image_url[2].equals("")) {
                try {
                    Picasso.with(getApplicationContext()).load(saved_image_url[2]).error(R.drawable.ic_menu_noprofile).into(sub_photo2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else{
                sub_photo2.setImageResource(R.drawable.ic_menu_noprofile);
            }
            if (saved_image_url[3] != null && !saved_image_url[3].equals("")) {
                try {
                    Picasso.with(getApplicationContext()).load(saved_image_url[3]).error(R.drawable.ic_menu_noprofile).into(sub_photo3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else{
                sub_photo3.setImageResource(R.drawable.ic_menu_noprofile);
            }
            if (saved_image_url[4] != null && !saved_image_url[4].equals("")) {
                try {
                    Picasso.with(getApplicationContext()).load(saved_image_url[4]).error(R.drawable.ic_menu_noprofile).into(sub_photo4);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else{
                sub_photo4.setImageResource(R.drawable.ic_menu_noprofile);
            }

            bbs_cate1.setSelection(getBbs_cate1);

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
                URL obj = new URL(connUrl);
                conn = (HttpURLConnection) obj.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.addRequestProperty("Cookie", user_auth);
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject job = new JSONObject();
                //JSONObject 생성 후 input
                job.put("id", getBbs_id);
                job.put("lat", Profile.gpslat);
                job.put("long", Profile.gpslong);

                os = conn.getOutputStream();
                //Output Stream 생성
                os.write(job.toString().getBytes("utf-8"));
                os.flush();


                int responseCode = conn.getResponseCode();

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

                    JSONObject optionJson = new JSONObject(responseJSON.getString("Option"));
                    getBbs_pay = optionJson.getString("pay");

                    if (responseJSON.get("Cate").toString().equals("기타")) {
                        getBbs_cate1 = 1;
                    } else if (responseJSON.get("Cate").toString().equals("시간")) {
                        getBbs_cate1 = 2;
                    } else if (responseJSON.get("Cate").toString().equals("재능")) {
                        getBbs_cate1 = 3;
                    } else if (responseJSON.get("Cate").toString().equals("물건")) {
                        getBbs_cate1 = 4;
                    } else if (responseJSON.get("Cate").toString().equals("고민")) {
                        getBbs_cate1 = 5;
                    } else {
                        getBbs_cate1 = 0;
                    }

                    JSONObject mediaJson = new JSONObject(responseJSON.getString("Media"));
                    saved_image_url[0] = mediaJson.getString("img0");
                    saved_image_url[1] = mediaJson.getString("img1");
                    saved_image_url[2] = mediaJson.getString("img2");
                    saved_image_url[3] = mediaJson.getString("img3");
                    saved_image_url[4] = mediaJson.getString("img4");
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

    /*
     * 사용자 정보 수정 완료 Thread
     */
    class BbsWriteThread extends AsyncTask<String, Void, Void> {
        ProgressDialog loading;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(BbsWriteActivity.this);
            loading.setTitle("게시글 작성");
            loading.setMessage("게시글을 올리고 있으니 잠시만 기다려주세요~");
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            bbs_save.setEnabled(true);
            Toast.makeText(BbsWriteActivity.this, "게시글 저장 완료", Toast.LENGTH_SHORT).show();
            loading.dismiss();
            Intent intent = new Intent(BbsWriteActivity.this, MainActivity.class);
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
            String user_title = value[1];
            String user_comment = value[2];
            String user_pay = value[3];
            String user_photo = "";
            String user_opt = "";

            try {
                JSONObject media_json = new JSONObject();

                if (saved_image_url[0].equals("")) {
                } else {
                    media_json.put("img0", saved_image_url[0]);

                    if (!saved_image_url[1].equals("")) {
                        media_json.put("img1", saved_image_url[1]);
                    }
                    if (!saved_image_url[2].equals("")) {
                        media_json.put("img2", saved_image_url[2]);
                    }
                    if (!saved_image_url[3].equals("")) {
                        media_json.put("img3", saved_image_url[3]);
                    }
                    if (!saved_image_url[4].equals("")) {
                        media_json.put("img4", saved_image_url[4]);
                    }
                    user_photo = media_json.toString();//.getBytes("utf-8");
                }

                JSONObject opt_json = new JSONObject();
                opt_json.put("pay", user_pay );
                user_opt = opt_json.toString();

                IgnoreHttpSertification.ignoreSertificationHttps();
                URL obj = new URL(connUrl);
                conn = (HttpURLConnection) obj.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.addRequestProperty("Cookie", Profile.auth);
                conn.setDoOutput(true);
                conn.setDoInput(true);


                JSONObject job = new JSONObject();
                //JSONObject 생성 후 input
                if (getEdit_flag == 1) {
                    job.put("id", getBbs_id);
                }
                job.put("title",user_title);
                job.put("msg", user_comment);
                job.put("media", user_photo);
                if (getBbs_cate1 == 1) {
                    job.put("cate", "기타");
                } else if (getBbs_cate1 == 2) {
                    job.put("cate", "시간");
                } else if (getBbs_cate1 == 3) {
                    job.put("cate", "재능");
                } else if (getBbs_cate1 == 4) {
                    job.put("cate", "물건");
                } else if (getBbs_cate1 == 5) {
                    job.put("cate", "고민");
                }
                job.put("option", user_opt);
                job.put("lat", Profile.gpslat);
                job.put("long", Profile.gpslong);

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
