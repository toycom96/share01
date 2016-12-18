package com.project0603.share00;

import android.*;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by leedongkwang on 2016. 12. 18..
 */

public class ProfileDialogUtil extends Dialog {
    private Context mContext;
    private int mUserId;

    private EditText user_name;
    private EditText user_etc;
    private EditText user_msg;
    private EditText user_openchat;
    private ImageView user_photo;

    private Button chat_button;
    private Button link_button;
    private Button close_button;

    private String getUserMsg;
    private String getUserSex;
    private String getUserSexStr;
    private String getUserEtc;
    private String getUserOpenchat;
    private String getUserName;
    private int getUserAge;
    private String getUserPhoto = null;

    public static Bitmap user_photo_bm;
    private final String info_url = GlobalVar.https_api1 + "/user_info2";

    public ProfileDialogUtil(Context context, int userId) {
        super(context);
        this.mContext = context;
        this.mUserId = userId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_profile_view);

        init();
    }

    private void init() {

        user_photo = (ImageView) findViewById(R.id.dialog_user_photo);
        user_name = (EditText) findViewById(R.id.dialog_user_name);
        user_etc = (EditText) findViewById(R.id.dialog_user_etc);
        user_msg = (EditText) findViewById(R.id.dialog_user_msg);
        chat_button = (Button) findViewById(R.id.dialog_user_chat);
        link_button = (Button) findViewById(R.id.dialog_user_link);
        close_button = (Button) findViewById(R.id.dialog_user_close);

        chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MessageDialogUtil messageUtil =
                        new MessageDialogUtil(mContext, getUserName, getUserSexStr, "", mUserId);
                //new MessageDialogUtil(context, ContentsListAdapter.ViewHolder.this.User.getText().toString(), ContentsListAdapter.ViewHolder.this.Etc.getText().toString(), ContentsListAdapter.ViewHolder.this.Msg.getText().toString(), ContentsListAdapter.ViewHolder.this.UserId);

                //쪽지 보내기 Dialog가 화면에 보여졌을 때의 기본 셋팅
                messageUtil.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        messageUtil.setTitle();
                    }
                });

                messageUtil.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        return;
                    }
                });

                messageUtil.show();
                dismiss();
            }
        });
        link_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "오픈 채팅", Toast.LENGTH_SHORT);
                dismiss();
            }
        });
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "닫기", Toast.LENGTH_SHORT);
                dismiss();
            }
        });

        GetUserInfoThread info = new GetUserInfoThread();
        info.execute(info_url, Profile.auth);
    }

    class GetUserInfoThread extends AsyncTask<String, Void, Void> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(mContext);
            loading.setTitle("회원정보수정");
            loading.setMessage("회원님의 정보를 받는 중이에요...");
            loading.setCancelable(false);
            loading.show();
            //loading = ProgressDialog.show(SignupActivity.this, "회원가입 중...", null,true,true);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            user_name.setText(getUserName);
            user_etc.setText(getUserEtc);
            user_msg.setText(getUserMsg);
            if (getUserPhoto != null && !getUserPhoto.equals("")) {
                try {
                    Picasso.with(mContext).load(getUserPhoto).error(R.drawable.ic_menu_noprofile).into(user_photo);
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
                conn.addRequestProperty("Cookie", user_auth);
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject job = new JSONObject();
                job.put("id", mUserId);
                job.put("long", Profile.gpslong);
                job.put("lat", Profile.gpslat);

                os = conn.getOutputStream();
                //Output Stream 생성
                os.write(job.toString().getBytes("utf-8"));
                os.flush();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {

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
                    JSONObject responseJSON = new JSONObject(response);

                    getUserName = responseJSON.get("Name").toString();
                    getUserAge = Integer.parseInt(responseJSON.get("Age").toString());
                    getUserSex = responseJSON.get("Sex").toString();
                    if (getUserSex.equals("F")) {
                        getUserEtc = getUserAge + " / " + "여";
                        getUserSexStr = "여";
                    } else {
                        getUserEtc = getUserAge + " / " + "남";
                        getUserSexStr = "남";
                    }
                    getUserMsg = responseJSON.get("Msg").toString();
                    getUserOpenchat = responseJSON.get("Openchat").toString();
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
}