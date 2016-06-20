package com.example.ethan.share01;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private  DrawerLayout dlDrawer;
    private ActionBarDrawerToggle dtToggle;
    Toolbar toolbar;

    private StaggeredGridLayoutManager _sGridLayoutManager;
    public List<ContentsListObject> mContentsList = new ArrayList<ContentsListObject>();
    public ContentsListAdapter mAdapter;
    public ContentsListLoad mContentsLoader;
    //public static Context mContext;

    public List<ContentsListObject> getItems(){
        return mContentsList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        dtToggle = new ActionBarDrawerToggle(
                this, dlDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dlDrawer.setDrawerListener(dtToggle);
        dtToggle.syncState();

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);

        // michael adding
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        _sGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(_sGridLayoutManager);

        //mContentsLoader = new ContentsListLoad(mContentsList, mAdapter);
        //mContentsLoader.LoadFromApi(0, 1);

        recyclerView.addOnScrollListener(new ContentsListListener(this, _sGridLayoutManager));

        //mAdapter = new ContentsListAdapter(MainActivity.this.getBaseContext(), mContentsList);
        mAdapter = new ContentsListAdapter(getApplicationContext(), mContentsList);
        recyclerView.setAdapter(mAdapter);

        mContentsLoader = new ContentsListLoad(mContentsList, mAdapter);
        mContentsLoader.LoadFromApi(0, 1);
        //Toast.makeText(recyclerView.getContext() , "aa", 5);
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
}
