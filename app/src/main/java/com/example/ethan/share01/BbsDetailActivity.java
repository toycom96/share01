package com.example.ethan.share01;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.example.ethan.share01.model.BbsMemo;
import com.example.ethan.share01.adapter.BbsMemoAdapter;

public class BbsDetailActivity extends AppCompatActivity {

    private TextView bbs_title;
    private TextView bbs_msg;
    private TextView bbs_opt;
    private TextView bbs_etc;
    private ImageView bbs_photo;

    private int getBbs_user_id;
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
    private int edit_mode = 0;

    ////////////////////////////////////////////////////////
    private ListView BbsMemo_lv;
    private ArrayList<BbsMemo> mBbsMemo = null;
    private BbsMemoAdapter mBbsMemoAdapter;
    private Button BbsMemo_write;
    private EditText BbsMemo_msg;

    //int bbs_id, int memo_id, int user_id, String user_name, int user_age, String user_sex, String user_photo, String memo, String date)
    private int getBbs_id;
    private int getBbsMemo_userid;
    private String getBbsMemo_username;
    private int getBbsMemo_userage;
    private String getBbsMemo_usersex;
    private String getBbsMemo_userphoto;
    private String getBbsMemo_memo;
    private String getBbsMemo_date;
    private String getBbsMemo_term;

    private final String bbs_detail_url = "https://toycom96.iptime.org:1443/bbs_view";
    private final String bbs_detail_delete_url = "https://toycom96.iptime.org:1443/bbs_delete";
    private final String bbsmemo_list_url = "https://toycom96.iptime.org:1443/bbs_memo_list";
    private final String bbsmemo_save_url = "https://toycom96.iptime.org:1443/bbs_memo_write";
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
        mBbsMemo = new ArrayList<>();

        if (mGps.isGetLocation()) {
            mLat = mGps.getLatitude();
            mLon = mGps.getLongitude();
        } else {
            mLat = 0.0;
            mLon = 0.0;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.bbs_detail_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        bbs_title = (TextView) findViewById(R.id.bbs_detail_title);
        bbs_etc = (TextView) findViewById(R.id.bbs_detail_etc);
        bbs_msg = (TextView) findViewById(R.id.bbs_detail_msg);
        bbs_opt = (TextView) findViewById(R.id.bbs_detail_pay);
        bbs_photo = (ImageView) findViewById(R.id.bbs_detail_photo);
        BbsMemo_lv = (ListView)  findViewById(R.id.bbs_detail_memo);
        BbsMemo_msg = (EditText) findViewById(R.id.bbs_detail_memo_msg);
        BbsMemo_write = (Button) findViewById(R.id.bbs_detail_memo_write);

        BbsMemo_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BbsMemoSaveThread BbsMemo = new BbsMemoSaveThread();
                BbsMemo.execute(bbsmemo_save_url, mPref.getValue("auth", ""));
            }
        });

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


        BbsMemo_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //클릭했을 때의 해당 화면의 context를 받아온다.
                final Context context = view.getContext();

                //쪽지 보내기 Dialog 생성, 상대방의 정보를 같이 보낸다.
                //쪽지를 보내는 버튼에 대한 이벤트는 MessageDialogUtil 클래스 내부에 구현되어 있다.
                //MessageDialogUtil(Context context, String recvName, String recvSex, String recvMsg, int recvId
                final MessageDialogUtil messageUtil =
                        new MessageDialogUtil(context, mBbsMemo.get(position).getUser_name(), mBbsMemo.get(position).getUser_sex(), mBbsMemo.get(position).getMemo() ,mBbsMemo.get(position).getUser_id() );
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
            }
        });

        GetBbsDetailThread info = new GetBbsDetailThread();
        info.execute(bbs_detail_url, mPref.getValue("auth", ""));

        BbsMemoLoadThread BbsMemo = new BbsMemoLoadThread();
        BbsMemo.execute(bbsmemo_list_url, mPref.getValue("auth", ""));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        edit_mode = 0;

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.bbs_detail_share:
                String subject = getBbs_title;
                String text = "http://www.daum.net";

                List<Intent> targetedShareIntents = new ArrayList<Intent>();

// 페이스북
                Intent facebookIntent = getShareIntent("facebook", subject, text);
                if(facebookIntent != null)
                    targetedShareIntents.add(facebookIntent);
// 트위터
                Intent twitterIntent = getShareIntent("twitter", subject, text);
                if(twitterIntent != null)
                    targetedShareIntents.add(twitterIntent);
// 구글 플러스
                Intent googlePlusIntent = getShareIntent("com.google.android.apps.plus", subject, text);
                if(googlePlusIntent != null)
                    targetedShareIntents.add(googlePlusIntent);
// Gmail
                Intent gmailIntent = getShareIntent("gmail", subject, text);
                if(gmailIntent != null)
                    targetedShareIntents.add(gmailIntent);
// 카카오 톡
                Intent katalkIntent = getShareIntent("com.kakao.talk", subject, text);
                if(gmailIntent != null)
                    targetedShareIntents.add(katalkIntent);
// 카카오 스토리
                Intent storyIntent = getShareIntent("com.kakao.story", subject, text);
                if(gmailIntent != null)
                    targetedShareIntents.add(storyIntent);

                Intent chooser = Intent.createChooser(targetedShareIntents.remove(0), "SNS로 공유하기");
                //Intent chooser = Intent.createChooser(targetedShareIntents.get(0), "타이틀" );
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
                startActivity(chooser);
                //Toast.makeText(getApplicationContext(),"share",Toast.LENGTH_SHORT).show();
                break;
            case R.id.bbs_detail_edit:
                if (getBbs_user_id != MainActivity.user_id_num) {
                    Toast.makeText(BbsDetailActivity.this, "작성자가 아니면 수정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    break;
                }
                Intent intent = new Intent(BbsDetailActivity.this, BbsWrite.class);
                intent.putExtra("Bbs_id", bbs_id);
                intent.putExtra("Lat", mLat);
                intent.putExtra("Lon", mLon);
                startActivity(intent);
                finish();
                break;
            case R.id.bbs_detail_delete:
                if (getBbs_user_id != MainActivity.user_id_num) {
                    Toast.makeText(BbsDetailActivity.this, "작성자가 아니면 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    break;
                }
                new AlertDialog.Builder(BbsDetailActivity.this)
                        .setTitle("게시물 삭제")
                        .setMessage("정말로 게시물을 삭제 하시겠습니까?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                BbsDetailDeleteThread BbsDelete = new BbsDetailDeleteThread();
                                BbsDelete.execute(bbs_detail_delete_url, mPref.getValue("auth", ""));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                Toast.makeText(BbsDetailActivity.this, "삭제 요청이 취소 되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sns_share, menu);
        return true;
    }

/*    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId = R.id.bbs_detail_memo_write) {
            BbsMemoSaveThread BbsMemo = new BbsMemoSaveThread();
            BbsMemo.execute(bbsmemo_save_url, mPref.getValue("auth", ""));
        }
    }*/

    private Intent getShareIntent(String type, String subject, String text)
    {
        boolean found = false;
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");

        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(share, 0);
        System.out.println("resinfo: " + resInfo);
        if (!resInfo.isEmpty()){
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains(type) ||
                        info.activityInfo.name.toLowerCase().contains(type) ) {
                    share.putExtra(Intent.EXTRA_SUBJECT,  subject);
                    share.putExtra(Intent.EXTRA_TEXT,     text);
                    share.setPackage(info.activityInfo.packageName);
                    found = true;
                    break;
                }
            }
            if (!found)
                return null;

            return share;
        }
        return null;
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
            bbs_etc.setText(getBbs_name + " (" + getBbs_sex + ", " + getBbs_age + ") " + getBbs_dist + "km");
            /*if (getBbs_sex.equals("여")) {
                bbs_etc.setTextColor(Color.parseColor("#FF0000"));
            } else {
                bbs_etc.setTextColor(Color.parseColor("#0000FF"));
            }*/

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

                    getBbs_user_id = Integer.parseInt(responseJSON.get("User_id").toString());
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
                    getBbs_pay = "품앗이 : " + optionJson.getString("pay");

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


    class BbsMemoLoadThread extends AsyncTask<String, Void, Void> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(BbsDetailActivity.this);
            loading.setTitle("댓글 리스트");
            loading.setMessage("댓글 리스트를 받는 중이에요...");
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mBbsMemoAdapter = new BbsMemoAdapter(BbsDetailActivity.this, mBbsMemo);

            BbsMemo_lv.setAdapter(mBbsMemoAdapter);
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
            //mBbsMemo.clear();
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
                conn.addRequestProperty("Cookie", user_auth);
                //Cookie값 설정(auth)
                conn.setDoOutput(true);
                conn.setDoInput(true);


                JSONObject job = new JSONObject();
                //JSONObject 생성 후 input

                job.put("bbs_id", bbs_id);
                job.put("long", mLon);
                job.put("lat", mLat);


                os = conn.getOutputStream();
                //Output Stream 생성
                os.write(job.toString().getBytes("utf-8"));
                os.flush();
                //Buffer에 있는 모든 정보를 보냄

                int responseCode = conn.getResponseCode();

                //int responseCode = conn.getResponseCode();
                //request code를 받음

                if(responseCode == HttpURLConnection.HTTP_OK) {
                    int unix_sec = 0;

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


                    if (response.isEmpty() || response.equals("null") ) {
                        Log.e("ChatListNull", response.toString());
                        return null;
                    }
                    mBbsMemo.clear();

                    JSONArray ja = new JSONArray(response);
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject order = ja.getJSONObject(i);

                        getBbs_id = Integer.parseInt(order.get("Bbs_id").toString());
                        getBbsMemo_userid = Integer.parseInt(order.get("User_id").toString());
                        getBbsMemo_userage = Integer.parseInt(order.get("User_age").toString());
                        getBbsMemo_username = order.get("User_name").toString();
                        getBbsMemo_usersex = order.get("User_sex").toString();
                        getBbsMemo_userphoto = order.get("User_photo").toString();
                        getBbsMemo_memo = order.get("Bbs_memo").toString();
                        getBbsMemo_date = order.get("Created").toString();
                        getBbsMemo_term = order.get("Term").toString();
                        unix_sec = Integer.parseInt(order.get("Term").toString());


                        if ( unix_sec > (6 * 30 * 24 * 60 * 60) ) { getBbsMemo_term = "반년이상"; }
                        else if ( unix_sec > ( 30 * 24 * 60 * 60) ) { getBbsMemo_term = (unix_sec / ( 30 * 24 * 60 * 60)) + "개월전"; }
                        else if ( unix_sec > ( 24 * 60 * 60) ) { getBbsMemo_term = (unix_sec / ( 24 * 60 * 60)) + "일전"; }
                        else if ( unix_sec > ( 60 * 60) ) { getBbsMemo_term = (unix_sec / ( 60 * 60)) + "시간전"; }
                        else if ( unix_sec > ( 60) ) { getBbsMemo_term = (unix_sec / (60)) + "분전"; }
                        else { getBbsMemo_term = unix_sec + "초전"; }
                        //getTime = order.get("Sended").toString();

                        /*
                        if (getBbsMemo_usersex == "F") {
                            getEtcInfo = "여 " + order.get("User_age").toString() + "세";
                        } else {
                            getEtcInfo = "남 " + order.get("User_age").toString() + "세";
                        }*/
                        mBbsMemo.add(new BbsMemo(getBbs_id, 0, getBbsMemo_userid, getBbsMemo_username, getBbsMemo_userage,  getBbsMemo_usersex, getBbsMemo_userphoto, getBbsMemo_memo, getBbsMemo_term));
                    }
                    Log.i("Response Data", response);
                    //JSONObject responseJSON = new JSONObject(response);
                    //JSONObject를 생성해 key값 설정으로 result값을 받음.



                }else {
                    Log.e("HTTP_ERROR", "NOT CONNECTED HTTP");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class BbsMemoSaveThread extends AsyncTask<String, Void, Void> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(BbsDetailActivity.this);
            loading.setTitle("댓글 저장");
            loading.setMessage("댓글을 저장 중이에요...");
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            loading.dismiss();

            if ( BbsMemo_msg.getText().toString().toString().length() < 5) {
                Toast.makeText(BbsDetailActivity.this, "댓글 내용이 없거나 5자 이하입니다.", Toast.LENGTH_SHORT).show();
            } else {
                BbsMemo_msg.setText("");
                Toast.makeText(BbsDetailActivity.this, "댓글 저장 완료", Toast.LENGTH_SHORT).show();

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(BbsMemo_msg.getWindowToken(), 0);

                BbsMemoLoadThread BbsMemo = new BbsMemoLoadThread();
                BbsMemo.execute(bbsmemo_list_url, mPref.getValue("auth", ""));
            }
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
            //mBbsMemo.clear();
            String connUrl = value[0];
            String user_auth = value[1];

            if (BbsMemo_msg.getText().toString().toString().length() < 5) {
                return null;
            }

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
                conn.addRequestProperty("Cookie", user_auth);
                //Cookie값 설정(auth)
                conn.setDoOutput(true);
                conn.setDoInput(true);


                JSONObject job = new JSONObject();
                //JSONObject 생성 후 input

                job.put("bbs_id", bbs_id);
                job.put("bbs_memo", BbsMemo_msg.getText().toString());
                job.put("long", mLon);
                job.put("lat", mLat);


                os = conn.getOutputStream();
                //Output Stream 생성
                os.write(job.toString().getBytes("utf-8"));
                os.flush();
                //Buffer에 있는 모든 정보를 보냄

                int responseCode = conn.getResponseCode();

                //int responseCode = conn.getResponseCode();
                //request code를 받음

                if (responseCode == HttpURLConnection.HTTP_OK) {
                } else {
                    Log.e("HTTP_ERROR", "NOT CONNECTED HTTP");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class BbsDetailDeleteThread extends AsyncTask<String, Void, Void> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(BbsDetailActivity.this);
            loading.setTitle("게시물 삭제");
            loading.setMessage("게시물 삭제 요청중입니다.");
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            loading.dismiss();
            Toast.makeText(BbsDetailActivity.this, "게시글 삭제 완료", Toast.LENGTH_SHORT).show();
            onBackPressed();
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
            //mBbsMemo.clear();
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
                job.put("id", bbs_id);


                os = conn.getOutputStream();
                os.write(job.toString().getBytes("utf-8"));
                os.flush();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
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
