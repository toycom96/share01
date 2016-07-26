package com.example.ethan.share01;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private StaggeredGridLayoutManager _sGridLayoutManager;
    public List<ContentsListObject> mContentsList = new ArrayList<ContentsListObject>();
    public ContentsListAdapter mAdapter;
    public ContentsListLoad mContentsLoader;
    //public static Context mContext;

    public List<ContentsListObject> getItems(){
        return mContentsList;
    }

    private TextView user_login_tv;
    private ImageView user_profile_iv;
    private TextView user_nick_tv;

    private LinearLayout navigation_view;

    private RbPreference mPref = new RbPreference(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

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
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        _sGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(_sGridLayoutManager);

        recyclerView.addOnScrollListener(new ContentsListListener(this, _sGridLayoutManager));

        mAdapter = new ContentsListAdapter(getApplicationContext(), mContentsList);
        recyclerView.setAdapter(mAdapter);

        mContentsLoader = new ContentsListLoad(mContentsList, mAdapter);
        mContentsLoader.loadFromApi(0, 1);
        //회원가입 유무 확인
        checkForLogin();
    }

    private void userSettingDialog(){
        /*
         * 로그인이 되어있는 경우 navigation header를 눌러 원하는 action을 구분하는 함수
         *
         * 로그아웃 : 이미 회원가입을 한 상태이므로 login값을 logout으로 바꿔준다.
         * 마이페이지 : 마이페이지 화면으로 화면 전환
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("마이페이지&로그아웃")
                .setMessage("이동하고 싶은 화면을 선택하세요~")
                .setCancelable(true)
                .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPref.put("login","logout");
                        finish();
                    }
                })
                .setNegativeButton("마이페이지", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, UserInfoEditActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
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
        String getLoginCheck = mPref.getValue("login","");
        if(getLoginCheck.equals("")){
            //기존 회원이 아닌경우
            user_login_tv.setEnabled(true);
            user_profile_iv.setEnabled(true);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("회원가입이 필요합니다")
                    .setMessage("이 어플리케이션은 회원가입을 필요해요\n회원가입하러 가실래요??")
                    .setCancelable(false)
                    .setPositiveButton("할래요!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("안할래요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (getLoginCheck.equals("logout")) {
            // 기존회원이 로그아웃 했을 경우
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("로그인하기")
                    .setMessage("기존 회원이시군요 !!\n다시 로그인 하시겠어요??")
                    .setCancelable(false)
                    .setPositiveButton("할래요!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new CreateAuthUtil(getApplicationContext()).execute(mPref.getValue("user_num", ""), mPref.getValue("device_id", ""));
                            user_login_tv.setText(mPref.getValue("user_id", ""));
                            user_nick_tv.setText(mPref.getValue("user_nick", ""));
                            //기존 회원일 경우 login 상태로 바꿔줘야지만 계속해서 dialog가 뜨지 않음.
                            mPref.put("login", "login");
                        }
                    })
                    .setNegativeButton("안할래요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (getLoginCheck.equals("login")){
            //현재 로그인 되어있는 경우
            new CreateAuthUtil(getApplicationContext()).execute(mPref.getValue("user_num", ""), mPref.getValue("device_id", ""));
            user_login_tv.setText(mPref.getValue("user_id", ""));
            user_nick_tv.setText(mPref.getValue("user_nick", ""));
            //user_login_tv.setEnabled(false);
            //user_profile_iv.setEnabled(false);
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.time_share) {
            // Handle the camera action
        } else if (id == R.id.talent_share) {

        } else if (id == R.id.goods_share) {

        } else if (id == R.id.setting) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
