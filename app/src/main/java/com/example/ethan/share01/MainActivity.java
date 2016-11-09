package com.example.ethan.share01;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static int dist = 20;
    public static String cate1 = "";
    public static int user_id_num = 0;

    private StaggeredGridLayoutManager _sGridLayoutManager;
    public List<ContentsListObject> mContentsList = new ArrayList<ContentsListObject>();
    public ContentsListAdapter mAdapter;
    public ContentsListLoad mContentsLoader;
    //public static Context mContext;
    public GpsInfo mGps;
    public String mGcmRegId;

    RecyclerView mRecyclerView;


    public List<ContentsListObject> getItems(){
        return mContentsList;
    }

    private TextView user_login_tv;
    private ImageView user_profile_iv;
    private TextView user_nick_tv;

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
        mGcmRegId = mPref.getValue("gcm_reg_id","");
        if (mGcmRegId == null || mGcmRegId.equals("")) {
            GcmRegThread GcmRegObj = new GcmRegThread();
            GcmRegObj.start();
        }
        this.registerReceiver(this.mainActivityNewBadgeReceiver, new IntentFilter("mainActivityNewBadge"));


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, BbsWrite.class);

                //intent.putExtra("OBJECT", MainActivity.this.mGps);
                if (mGps.isGetLocation()) {
                    intent.putExtra("Lat", mGps.getLatitude());
                    intent.putExtra("Lon", mGps.getLongitude());
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
            /*@Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                Log.e("~~ toolbar : ", "close");
            }*/

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
                String badge_count = mPref.getValue("badge_chatcnt", "");
                if (badge_count == null || badge_count.equals("")) {
                    //예외처리
                } else if (Integer.parseInt(badge_count) > 0) {
                    mChatListNewBadge = (TextView) findViewById(R.id.chatlist_badge);
                    mChatListNewBadge.setBackgroundResource(R.drawable.ic_badge_new);
                }
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        /*//Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);
        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();*/





        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        user_login_tv = (TextView) header.findViewById(R.id.navigation_user_login);
        user_nick_tv = (TextView) header.findViewById(R.id.navigation_user_nick);
        user_profile_iv = (ImageView) header.findViewById(R.id.navigation_user_profile_imageView);

        navigation_view = (LinearLayout) header.findViewById(R.id.navigation_view);


        navigation_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSettingDialog();
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
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);

        mContentsLoader = new ContentsListLoad(mContentsList, mAdapter, mGps);
        mContentsLoader.loadFromApi(0, dist, cate1, mPref.getValue("auth",""), mRecyclerView, this);

        //회원가입 유무 확인
        checkForLogin();
    }

    BroadcastReceiver mainActivityNewBadgeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Integer.parseInt(mPref.getValue("badge_chatcnt", "").toString()) > 0) {
                mMainNewBadge = (TextView) findViewById(R.id.main_badge);
                mMainNewBadge.setBackgroundResource(R.drawable.ic_badge_new);
                mChatListNewBadge = (TextView) findViewById(R.id.chatlist_badge);
                mChatListNewBadge.setBackgroundResource(R.drawable.ic_badge_new);
            }
        }
    };

    private void userSettingDialog(){
        /*
         * 로그인이 되어있는 경우 navigation header를 눌러 원하는 action을 구분하는 함수
         *
         * 로그아웃 : 이미 회원가입을 한 상태이므로 login값을 logout으로 바꿔준다.
         * 마이페이지 : 마이페이지 화면으로 화면 전환
         */
        openBottomSheet(R.string.bottom_sheet_title_mypage, R.string.bottom_sheet_mypage, R.string.bottom_sheet_logout,BOTTOM_CASEVAL1);
    }

    private void checkForLogin(){

        /*
         * 회원가입 유무에 따른 action 설정 함수
         * 로그인 상태 ("login","login")
         * 로그아웃 상태 ("login","logout")
         * 미가입 회원 ("login","")
         *
         * 각 상태를 확인 후 dialog를 띄워 해당 action을 수행
         */
        MainActivity.user_id_num = Integer.parseInt(mPref.getValue("user_num", ""));
        String getLoginCheck = mPref.getValue("login","");
        if(getLoginCheck.equals("")){
            //기존 회원이 아닌경우

            openBottomSheet(R.string.bottom_sheet_title_member, R.string.bottom_sheet_login, R.string.bottom_sheet_signup,BOTTOM_CASEVAL2);

        } else if (getLoginCheck.equals("logout")) {

            openBottomSheet(R.string.bottom_sheet_title_member, R.string.bottom_sheet_login, R.string.bottom_sheet_signup,BOTTOM_CASEVAL2);

        } else if (getLoginCheck.equals("login")){
            //현재 로그인 되어있는 경우
            new CreateAuthUtil(getApplicationContext()).execute(mPref.getValue("user_num", ""), mPref.getValue("device_id", ""), mPref.getValue("gcm_reg_id", ""));
            user_login_tv.setText(mPref.getValue("user_id", ""));
            user_nick_tv.setText(mPref.getValue("user_nick", ""));
            //Picasso.with(getApplicationContext()).load(mPref.getValue("User_Photo","")).error(R.drawable.ic_menu_noprofile).into(user_profile_iv);
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
            MainActivity.dist = 5;
        } else if (id == R.id.action_dist2) {
            MainActivity.dist = 20;
        } else if (id == R.id.action_dist3) {
            MainActivity.dist = 50;
        } else if (id == R.id.action_distall) {
            MainActivity.dist = 0;
        }
        mContentsLoader.loadFromApi(0, MainActivity.dist, MainActivity.cate1, mPref.getValue("auth",""), mRecyclerView, this);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mContentsLoader.loadFromApi(0, MainActivity.dist, MainActivity.cate1, mPref.getValue("auth",""), mRecyclerView, this);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.etc_share) {
            MainActivity.cate1 = "기타";
            mContentsLoader.loadFromApi(0, MainActivity.dist, MainActivity.cate1, mPref.getValue("auth",""), mRecyclerView, this);
        } else if (id == R.id.time_share) {
            MainActivity.cate1 = "시간";
            mContentsLoader.loadFromApi(0, MainActivity.dist, MainActivity.cate1, mPref.getValue("auth",""), mRecyclerView, this);
        } else if (id == R.id.talent_share) {
            //mContentsLoader = new ContentsListLoad(mContentsList, mAdapter);
            MainActivity.cate1 = "재능";
            mContentsLoader.loadFromApi(0, MainActivity.dist, MainActivity.cate1, mPref.getValue("auth",""), mRecyclerView, this);
        } else if (id == R.id.goods_share) {
            MainActivity.cate1 = "물건";
            mContentsLoader.loadFromApi(0, MainActivity.dist, MainActivity.cate1, mPref.getValue("auth",""), mRecyclerView, this);
        } else if (id == R.id.think_share) {
            MainActivity.cate1 = "고민";
            mContentsLoader.loadFromApi(0, MainActivity.dist, MainActivity.cate1, mPref.getValue("auth",""), mRecyclerView, this);
        } else if (id == R.id.all_share) {
            MainActivity.cate1 = "";
            mContentsLoader.loadFromApi(0,  MainActivity.dist, MainActivity.cate1, mPref.getValue("auth",""), mRecyclerView, this);
        } else if (id == R.id.nav_chatlist) {
            Intent intent = new Intent(MainActivity.this, ChatListActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            startActivity(intent);
            finish();
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
                mMainNewBadge.setBackgroundResource(R.drawable.ic_badge_new);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mainActivityNewBadgeReceiver);
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
                        mPref.put("login","logout");
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

    class GcmRegThread extends Thread {
        public void run() {
            try {
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                //String regId = gcm.register("chat01-140505");
                String regId = gcm.register("1028702649415");

                mPref.put("gcm_reg_id",regId);
                mGcmRegId = regId;
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
