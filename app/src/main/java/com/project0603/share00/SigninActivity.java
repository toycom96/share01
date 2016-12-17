package com.project0603.share00;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.project0603.share00.R;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener{

    private Button signin_button;
    private EditText user_id_edt;
    private EditText user_pass_edt;

    private String getUserEmail;
    private String getUserPass;
    private String getUserNick;
    private String getUserAuth;
    private String getUserNum;
    private String getUserDeviceId;

    private String getPermissionPhone;
    private String getPermissionDevice;

    private RbPreference mPref = new RbPreference(SigninActivity.this);
    private Boolean shgnin_complete = false;
    private final String SIGNIN_URL = GlobalVar.https_api1 + "/auth_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        init();
    }

    private void init(){
        signin_button = (Button) findViewById(R.id.signin_button);
        user_id_edt = (EditText) findViewById(R.id.signin_userid);
        user_pass_edt = (EditText) findViewById(R.id.signin_user_password);
        phoneInfo();
        signin_button.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signin_button :
                signin_button.setEnabled(false);
                String userid = user_id_edt.getText().toString();
                String userpass = user_pass_edt.getText().toString();

                SigninTread signin = new SigninTread();
                signin.execute(SIGNIN_URL, userid, userpass);
                break;
        }
    }

    private void phoneInfo() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        1122);
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        1122);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1122: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    TelephonyManager manager=(TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
                    getPermissionDevice=manager.getDeviceId();
                    getPermissionPhone =manager.getLine1Number();
                } else {
                    Toast.makeText(this, "폰의 정보를 얻을 수 있어야 회원 가입이 가능합니다.", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SigninActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    class SigninTread extends AsyncTask<String, Void, Void> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(SigninActivity.this);
            loading.setTitle("로그인");
            loading.setMessage("로그인 중이에요...");
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(shgnin_complete){
                Toast.makeText(SigninActivity.this, "로그인 완료", Toast.LENGTH_SHORT).show();

                CreateAuthUtil auth = new CreateAuthUtil(getApplicationContext());
                auth.execute();

                loading.dismiss();

            } else {

                Toast.makeText(SigninActivity.this, "로그인중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }

            signin_button.setEnabled(true);
            //Intent intent = new Intent(SigninActivity.this, MainActivity.class);
            //startActivity(intent);
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
            getUserEmail = value[1];
            getUserPass = getMD5Hash(value[2]);


            try {
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                String regId = gcm.register("1028702649415");

                IgnoreHttpSertification.ignoreSertificationHttps();
                URL obj = new URL(connUrl);
                conn = (HttpURLConnection) obj.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");

                conn.setDoOutput(true);
                conn.setDoInput(true);


                JSONObject job = new JSONObject();

                job.put("email", getUserEmail);
                job.put("passwd", getUserPass);
                job.put("device_id", getPermissionDevice);
                job.put("phone_num", getPermissionPhone);
                job.put("gcm_id", regId);

                os = conn.getOutputStream();
                os.write(job.toString().getBytes("utf-8"));
                os.flush();

                int responseCode = conn.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK) {

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
                    JSONObject responseJSON = new JSONObject(response);

                    Profile.user_id = Integer.parseInt(responseJSON.get("Id").toString());
                    Profile.device_id = responseJSON.get("Device_id").toString();
                    Profile.gcm_id = regId;
                    shgnin_complete = true;
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
     * Create Lai.OH
     * String 값을 받아와 MD5 형식으로 바꾼 뒤 Return 하는 함수
     */
    public static String getMD5Hash(String s) {
        MessageDigest m = null;
        String hash = null;

        try {
            m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(),0,s.length());
            hash = new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hash;
    }
}
