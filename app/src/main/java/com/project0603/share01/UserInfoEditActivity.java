package com.project0603.share01;

import android.*;
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
import android.os.SystemClock;
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
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
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
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by Lai.OH on 16. 7. 11..
 */
public class UserInfoEditActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText user_email;
    private EditText user_nick;
    private EditText user_age;
    private EditText user_coment;
    private EditText user_openchat;
    private ImageView user_photo;

    private Button infoedit_button;

    private String getUserComent;
    private String getUserOpenchat;
    private String getUserNick;
    private int getUserAge;
    private String getUserPhoto = null;

    private String selectedImagePath;

    private String getPhotoPath;

    public Bitmap user_photo_bm;
    private final String info_url = GlobalVar.https_api1 + "/user_info";
    private final String edit_url = GlobalVar.https_api1 + "/user_edit";
    private static final int GET_PICTURE_URI = 101;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_edit);

        init();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            selectedImagePath = cursor.getString(columnIndex);
            cursor.close();

            user_photo_bm = BitmapFactory.decodeFile(selectedImagePath);
            user_photo.setImageBitmap(user_photo_bm);

            ExifInterface exif = null;
            try {
                File pictureFile = new File(selectedImagePath);
                exif = new ExifInterface(pictureFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            int orientation = ExifInterface.ORIENTATION_NORMAL;

            if (exif != null)
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    user_photo_bm = rotateBitmap(user_photo_bm, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    user_photo_bm = rotateBitmap(user_photo_bm, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    user_photo_bm = rotateBitmap(user_photo_bm, 270);
                    break;
            }

            File file = new File(getExternalCacheDir(), "unlimited_share_image.jpg");
            try {
                FileOutputStream out = new FileOutputStream(file);
                user_photo_bm.compress(Bitmap.CompressFormat.JPEG, 85, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                Log.e("Image", "Convert");
            }

            UploadImageTask editInfo = new UploadImageTask();
            editInfo.execute();
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.user_info_edit_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        user_email = (EditText) findViewById(R.id.infoedit_email);
        user_nick = (EditText) findViewById(R.id.infoedit_nick);
        user_age = (EditText) findViewById(R.id.infoedit_age);
        user_coment = (EditText) findViewById(R.id.infoedit_coment);
        user_openchat = (EditText) findViewById(R.id.infoedit_openchat);
        infoedit_button = (Button) findViewById(R.id.infoedit_button);
        user_photo = (ImageView) findViewById(R.id.infoedit_photo);

        infoedit_button.setOnClickListener(this);
        user_photo.setOnClickListener(this);

        GetUserInfoThread info = new GetUserInfoThread();
        info.execute(info_url, Profile.auth);
    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
    }*/

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

        Intent intent = new Intent(UserInfoEditActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.infoedit_button:
                getUserComent = user_coment.getText().toString();
                getUserOpenchat = user_openchat.getText().toString();
                getUserNick = user_nick.getText().toString();
                getUserPhoto = getPhotoPath;
                getUserAge = Integer.parseInt(user_age.getText().toString());
                EditUserInfoThread editInfo = new EditUserInfoThread();
                editInfo.execute(edit_url);
                Profile.photo = getPhotoPath;
                break;

            case R.id.infoedit_photo:
                int permission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE );
                } else {
                    /*Intent photoPickerIntent = new Intent();
                    photoPickerIntent.setType("image/*");
                    photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Picture"), SELECT_PHOTO);*/


                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                    intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, GET_PICTURE_URI);
                }
                break;
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("UserInfoEdit Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    //이미지 파일 올리는 쓰레드
    class UploadImageTask extends AsyncTask<Void, Void, String> {
        private String webAddressToPost = GlobalVar.https_api1 + "/up_file";

        private ProgressDialog dialog = new ProgressDialog(UserInfoEditActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Uploading...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            Log.e("response String before", s);
            getPhotoPath = s;
            //getPhotoPath = s.replace("[", "").replace("]", "");

            Log.e("response String after", getPhotoPath);
            Toast.makeText(getApplicationContext(), "file uploaded",
                    Toast.LENGTH_LONG).show();
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

                bos = new ByteArrayOutputStream();
                user_photo_bm.compress(Bitmap.CompressFormat.JPEG, 85, bos);
                byte[] data = bos.toByteArray();
                bab = new ByteArrayBody(data, "test.jpg");
                entity.addPart("imgfiles", bab);


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

            user_email.setText(Profile.email);
            user_nick.setText(getUserNick);
            user_age.setText(String.valueOf(getUserAge));
            user_coment.setText(getUserComent);
            user_openchat.setText(getUserOpenchat);
            if (getUserPhoto != null && !getUserPhoto.equals("")) {
                try {
                    Picasso.with(getApplicationContext()).load(getUserPhoto).error(R.drawable.ic_menu_noprofile).into(user_photo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                user_photo.setImageResource(R.drawable.ic_menu_noprofile);
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
                URL obj = new URL(connUrl);
                conn = (HttpURLConnection) obj.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                Log.e("user_auth", user_auth);
                conn.addRequestProperty("Cookie", user_auth);
                conn.setDoOutput(true);
                conn.setDoInput(true);


                os = conn.getOutputStream();
                os.flush();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    Log.e("HTTP_OK", "HTTP OK RESULT");
                    is = conn.getInputStream();
                    baos = new ByteArrayOutputStream();
                    byte[] byteBuffer = new byte[1024];
                    byte[] byteData = null;
                    int nLength = 0;
                    while ((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
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
                    Log.i("Response Age Value", responseJSON.get("Photo").toString());

                    getUserNick = responseJSON.get("Name").toString();
                    getUserAge = Integer.parseInt(responseJSON.get("Age").toString());
                    getUserComent = responseJSON.get("Msg").toString();
                    getUserOpenchat = responseJSON.get("openchat").toString();
                    getUserPhoto = responseJSON.get("Photo").toString();
                } else {
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

            try {
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
                job.put("msg", getUserComent);
                job.put("openchat", getUserOpenchat);
                job.put("photo", getUserPhoto);
                job.put("name", getUserNick);
                job.put("age", getUserAge);


                os = conn.getOutputStream();
                os.write(job.toString().getBytes("utf-8"));
                os.flush();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    Log.e("HTTP_OK", "HTTP OK RESULT");
                    is = conn.getInputStream();
                    baos = new ByteArrayOutputStream();
                    byte[] byteBuffer = new byte[1024];
                    byte[] byteData = null;
                    int nLength = 0;
                    while ((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
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
                } else {
                    Log.e("HTTP_ERROR", "NOT CONNECTED HTTP");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
