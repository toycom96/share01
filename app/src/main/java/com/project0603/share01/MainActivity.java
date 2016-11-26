package com.project0603.share01;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.okhttp.OkHttpClient;
//import com.squareup.picasso.Cache;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.okhttp.Cache;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private StaggeredGridLayoutManager _sGridLayoutManager;
    public List<ContentsListObject> mContentsList = new ArrayList<ContentsListObject>();
    public ContentsListAdapter mAdapter;
    public ContentsListLoad mContentsLoader;
    //public static Context mContext;
    public GpsInfo mGps;

    RecyclerView mRecyclerView;

    public List<ContentsListObject> getItems(){
        return mContentsList;
    }

    private TextView user_email_tv;
    private ImageView user_profile_iv;
    private TextView user_nick_tv;
    private TextView user_infoedit;
    private TextView user_logout;

    private LinearLayout navigation_view;


    private int BOTTOM_CASEVAL1 = 1;
    private int BOTTOM_CASEVAL2 = 2;

    public RbPreference mPref = new RbPreference(MainActivity.this);
    public TextView mChatListNewBadge;
    public TextView mMainNewBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("MainActivity", "MainActivity");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        mGps = new GpsInfo(this);
        if (mGps.isGetLocation()) {
            Profile.gpslat = mGps.getLatitude();
            Profile.gpslong = mGps.getLongitude();
        } else {
            // GPS 를 사용할수 없으므로
            mGps.showSettingsAlert();
        }

        this.registerReceiver(this.mainActivityNewBadgeReceiver, new IntentFilter("mainActivityNewBadge"));
        this.registerReceiver(this.mainActivityAuthFinishReceiver, new IntentFilter("AuthFinish"));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, BbsWriteActivity.class);

                //intent.putExtra("OBJECT", MainActivity.this.mGps);
                if (mGps.isGetLocation()) {
                    intent.putExtra("Lat", Profile.gpslat);
                    intent.putExtra("Lon", Profile.gpslong);
                } else {
                    // GPS 를 사용할수 없으므로
                    mGps.showSettingsAlert();
                }
                startActivity(intent);
                finish();
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
                String badge_count = mPref.getValue("badge_chatcnt", "");
                if (badge_count == null || badge_count.equals("")) {
                    //예외처리
                } else if (Integer.parseInt(badge_count) > 0) {
                    mChatListNewBadge = (TextView) findViewById(R.id.chatlist_badge);
                    mChatListNewBadge.setBackgroundResource(R.drawable.badge_circle);
                    mChatListNewBadge.setVisibility(TextView.VISIBLE);
                }
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();



        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        user_email_tv = (TextView) header.findViewById(R.id.navigation_user_email);
        user_nick_tv = (TextView) header.findViewById(R.id.navigation_user_nick);
        user_profile_iv = (ImageView) header.findViewById(R.id.navigation_user_profile_imageView);

        navigation_view = (LinearLayout) header.findViewById(R.id.navigation_view);

        user_infoedit = (TextView) header.findViewById(R.id.navigation_user_infoedit);
        user_logout = (TextView) header.findViewById(R.id.navigation_user_logout);

        user_infoedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(MainActivity.this, UserInfoEditActivity.class);
                startActivity(intent);
                finish();
            }
        });
        user_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPref.removeAllValue();
                new Profile("removeAll");
                GcmBroadcastReceiver.updateIconBadge(MainActivity.this, 0);
                finish();
            }
        });

        // michael adding

        /*
         * revised Lai.OH on 2016.08.13
         *
         * RecyclerView에 대한 Manager 및 Listener 설정만 MainActivity에서 구현
         * 나머지 카드뷰를 뿌리기 위한 모든 코드는 ContentListLoad 클래스에서 구현
         *
         * 이를 구현하기 위해서 recyclerView를 전역변수로 설정 한 뒤 Listener 및 loadFromApi에 parameter로 넘겨줌(MainActivity Context도 함께 넘겨줌)
         *
         */

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        _sGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(_sGridLayoutManager);
        mRecyclerView.addOnScrollListener(new ContentsListListener(this, _sGridLayoutManager, mRecyclerView, getApplicationContext()));
        //mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);

        //mContentsLoader = new ContentsListLoad(mContentsList, mAdapter, mGps);
        mAdapter = new ContentsListAdapter(getApplicationContext(), mContentsList);
        mRecyclerView.setAdapter(mAdapter);
        //mContentsLoader = new ContentsListLoad(mContentsList, mAdapter, mRecyclerView, _sGridLayoutManager, getApplicationContext(), mGps);
        mContentsLoader = new ContentsListLoad(mContentsList, mAdapter, mRecyclerView, _sGridLayoutManager, MainActivity.this, mGps);

        //회원 가입 유무 확인
        checkForLogin();

    }

    BroadcastReceiver mainActivityAuthFinishReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){

            getUserProfile user = new getUserProfile();
            user.execute();

            mContentsLoader.loadFromApi(0, GlobalVar.dist, GlobalVar.cate1, Profile.auth);
        }
    };

    BroadcastReceiver mainActivityNewBadgeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Integer.parseInt(mPref.getValue("badge_chatcnt", "").toString()) > 0) {
                mMainNewBadge = (TextView) findViewById(R.id.main_badge);
                mMainNewBadge.setVisibility(TextView.VISIBLE);
                mChatListNewBadge = (TextView) findViewById(R.id.chatlist_badge);
                mChatListNewBadge.setVisibility(TextView.VISIBLE);
            }
        }
    };

    private void checkForLogin(){

        /*
         * 회원가입 유무에 따른 action 설정 함수
         * user_id가 존재 하면 로그인 상태, 아니면 미로그인 상태
         * 각 상태를 확인 후 dialog를 띄워 해당 action을 수행
         */

        if (Profile.auth_finish == 1) {
            return;
        }

        String user_id_str = mPref.getValue("user_id", "");
        String device_id_str = mPref.getValue("device_id", "");
        if (user_id_str.equals("") || device_id_str.equals("")) {
            //Profile.user_id = 0;
        } else {
            Profile.user_id = Integer.parseInt(user_id_str);
            Profile.device_id = device_id_str;
        }

        if(Profile.user_id == 0){
            //기존 회원이 아닌경우
            openBottomSheet(R.string.bottom_sheet_title_member, R.string.bottom_sheet_login, R.string.bottom_sheet_signup,BOTTOM_CASEVAL2);
        } else {
            //현재 로그인 되어있는 경우
            CreateAuthUtil auth = new CreateAuthUtil(getApplicationContext());
            auth.execute();
        }
    }

    private void setupPicasso()
    {
        try {
            Cache diskCache = new Cache(getDir("foo", Context.MODE_PRIVATE), 100000000);
            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setCache(diskCache);

            Picasso picasso = new Picasso.Builder(this)
                    .memoryCache(new LruCache(100000000)) // Maybe something fishy here?
                    .downloader(new OkHttpDownloader(okHttpClient))
                    .build();

            picasso.setIndicatorsEnabled(true); // For debugging

            Picasso.setSingletonInstance(picasso);
        } catch (Exception e){

        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id <= 0){
            return super.onOptionsItemSelected(item);
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_dist1) {
            GlobalVar.dist = 5;
        } else if (id == R.id.action_dist2) {
            GlobalVar.dist = 20;
        } else if (id == R.id.action_dist3) {
            GlobalVar.dist = 50;
        } else if (id == R.id.action_distall) {
            GlobalVar.dist = 0;
        }
        mContentsLoader.loadFromApi(0, GlobalVar.dist, GlobalVar.cate1, Profile.auth);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        user_email_tv.setText(Profile.email);
        user_nick_tv.setText(Profile.name);

        if (Profile.photo != null && !Profile.photo.equals("")) {
            try {
                Picasso.with(getApplicationContext()).load(Profile.photo).error(R.drawable.ic_menu_noprofile).into(user_profile_iv);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else{
            user_profile_iv.setImageResource(R.drawable.ic_menu_noprofile);
        }

        if (GlobalVar.detail_enter_flag == 0 && !Profile.auth.equals("")) {
            mContentsLoader.loadFromApi(0, GlobalVar.dist, GlobalVar.cate1, Profile.auth);
        }
        GlobalVar.detail_enter_flag = 0;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.etc_share) {
            GlobalVar.cate1 = "기타";
            mContentsLoader.loadFromApi(0, GlobalVar.dist, GlobalVar.cate1, Profile.auth);
        } else if (id == R.id.time_share) {
            GlobalVar.cate1 = "시간";
            mContentsLoader.loadFromApi(0, GlobalVar.dist, GlobalVar.cate1, Profile.auth);
        } else if (id == R.id.talent_share) {
            //mContentsLoader = new ContentsListLoad(mContentsList, mAdapter);
            GlobalVar.cate1 = "재능";
            mContentsLoader.loadFromApi(0, GlobalVar.dist, GlobalVar.cate1, Profile.auth);
        } else if (id == R.id.goods_share) {
            GlobalVar.cate1 = "물건";
            mContentsLoader.loadFromApi(0, GlobalVar.dist, GlobalVar.cate1, Profile.auth);
        } else if (id == R.id.think_share) {
            GlobalVar.cate1 = "고민";
            mContentsLoader.loadFromApi(0, GlobalVar.dist, GlobalVar.cate1, Profile.auth);
        } else if (id == R.id.all_share) {
            GlobalVar.cate1 = "";
            mContentsLoader.loadFromApi(0,  GlobalVar.dist, GlobalVar.cate1, Profile.auth);
        } else if (id == R.id.nav_chatlist) {
            Intent intent = new Intent(MainActivity.this, ChatListActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.notice) {
            Intent intent = new Intent(MainActivity.this, NoticeActivity.class);
            startActivity(intent);
            //finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus == true) {
            String badge_count = mPref.getValue("badge_chatcnt","");
            if (badge_count == null || badge_count.equals("")) {
                //예외처리
            } else if (Integer.parseInt(badge_count) > 0) {
                mMainNewBadge = (TextView) findViewById(R.id.main_badge);
                mMainNewBadge.setVisibility(TextView.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mainActivityNewBadgeReceiver);
        this.unregisterReceiver(mainActivityAuthFinishReceiver);
    }

    private void openBottomSheet(int titleVal, int cateVal1, int cateVal2, final int caseVal){
        Log.e("openBottomSheet", "Open");
        /*
         * Create by Lai.OH 2016.07.27
         *
         * 밑에서 올라오는 화면 구성 함수
         * 로그인, 회원가입, 로그아웃, 마이페이지 메뉴를 관리 할 수 있는 서랍형식의 레이아웃생성
         * params (BottomSheet Title, First Button text, Second Button text, BottomSheet Caseval)
         * BottomSheet Case :
         *      1 : 기존의 회원인경우 마이페이지 및 로그아웃 설정
         *      2 : 기존의 회원이거나 회원이 아닌경우 로그인 및 회원가입 설정
         */

        View view = getLayoutInflater().inflate(R.layout.custom_bottom_sheet, null);
        TextView title_tv = (TextView) view.findViewById(R.id.bottomsheet_title);
        TextView cate1_tv = (TextView) view.findViewById(R.id.bottomsheet_cate1);
        TextView cate2_tv = (TextView) view.findViewById(R.id.bottomsheet_cate2);
        TextView calcel_tv = (TextView) view.findViewById(R.id.bottomsheet_cancel);

        title_tv.setText(titleVal);
        cate1_tv.setText(cateVal1);
        cate2_tv.setText(cateVal2);

        final Dialog mBottomSheetDialog = new Dialog(MainActivity.this, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView (view);
        mBottomSheetDialog.setCancelable (false);
        mBottomSheetDialog.getWindow ().setLayout (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow ().setGravity (Gravity.BOTTOM);
        mBottomSheetDialog.show ();

        cate1_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (caseVal) {
                    case 1 :
                        intent = new Intent(MainActivity.this, UserInfoEditActivity.class);
                        startActivity(intent);
                        mBottomSheetDialog.dismiss();
                        finish();

                        break;
                    case 2 :
                        intent = new Intent(MainActivity.this, SigninActivity.class);
                        startActivity(intent);
                        mBottomSheetDialog.dismiss();
                        finish();
                        break;
                }
            }
        });

        cate2_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (caseVal) {
                    case 1 :
                        mPref.removeAllValue();
                        new Profile("removeAll");
                        //mPref.put("login","logout");
                        finish();
                        break;
                    case 2 :
                        Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                        startActivity(intent);
                        mBottomSheetDialog.dismiss();
                        finish();
                        break;
                }
            }
        });

        calcel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (caseVal) {
                    case 2 :
                        mBottomSheetDialog.dismiss();
                        finish();
                        break;
                    default:
                        mBottomSheetDialog.dismiss();
                }
            }
        });
    }

    class getUserProfile extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            user_email_tv.setText(Profile.email);
            user_nick_tv.setText(Profile.name);

            if (Profile.photo != null && !Profile.photo.equals("")) {
                try {
                    Picasso.with(getApplicationContext()).load(Profile.photo).error(R.drawable.ic_menu_noprofile).into(user_profile_iv);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else{
                user_profile_iv.setImageResource(R.drawable.ic_menu_noprofile);
            }
        }

        @Override
        protected Void doInBackground(String... value) {
            HttpURLConnection conn = null;
            OutputStream os = null;
            InputStream is = null;
            ByteArrayOutputStream baos = null;
            String response = null;

            try {
                IgnoreHttpSertification.ignoreSertificationHttps();
                URL obj = new URL(GlobalVar.https_dns1 + "/user_info");
                conn = (HttpURLConnection) obj.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.addRequestProperty("Cookie", Profile.auth);
                conn.setDoOutput(true);
                conn.setDoInput(true);

                os = conn.getOutputStream();
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

                    new Profile(response);
                    JSONObject responseJSON = new JSONObject(response);
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
