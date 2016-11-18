package com.example.ethan.share01;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ethan on 16. 6. 17..
 */
public class ContentsListLoad {

    static List<ContentsListObject> mContentItem = new ArrayList<>();
    static ContentsListAdapter mAdapter;
    private String mDistFlag = "0";
    private final Lock _mutex = new ReentrantLock(true);
    private GpsInfo mGps;
    private double mLat;
    private double mLon;
    private int refresh_data_flag = 0;
    private int curSize = 0;


    public ContentsListLoad (List<ContentsListObject> ContentItem, ContentsListAdapter ListAdapter, GpsInfo gps) {
        this.mContentItem = ContentItem;
        this.mAdapter = ListAdapter;
        this.mGps = gps;

        mGps.GpsInfoRefresh();
        // GPS 사용유무 가져오기
        if (mGps.isGetLocation()) {

            mLat = mGps.getLatitude();
            mLon = mGps.getLongitude();

        } else {
            // GPS 를 사용할수 없으므로
            mGps.showSettingsAlert();
        }

    }

    public int loadFromApi(int ListIndex, int dist_flag, String cateStr, String auth, RecyclerView recyclerView, Context context) {
        if (GlobalVar.loading_flag == 1) {
            return 0;
        }
        GlobalVar.loading_flag = 1;

        getBbsList BbsList = new getBbsList(mContentItem, mAdapter,recyclerView, context);

        String IdxStr = String.valueOf(ListIndex);
        String DistStr = String.valueOf(dist_flag);

        BbsList.execute(IdxStr, DistStr, cateStr, auth);

        return 0;
    }


    class getBbsList extends AsyncTask<String, Void, Void> {
        private List<ContentsListObject> mContentItem;
        public ContentsListAdapter mAdapter;
        private RecyclerView mRecyclerView;
        private Context mContext;
        private StaggeredGridLayoutManager _sGridLayoutManager;
        private MainActivity activity;
        ProgressDialog loading;

        public getBbsList (List<ContentsListObject> ContentItem, ContentsListAdapter ListAdapter,RecyclerView recyclerView, Context context) {
            this.mContentItem = ContentItem;
            this.mAdapter = ListAdapter;
            this.mRecyclerView = recyclerView;
            this.mContext = context;
            //this.activity = activity;
        }

        /*@Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(mContext);
            loading.setTitle("게시글 받아오는중");
            loading.setMessage("조금만 기다려 주세요~~");
            loading.setCancelable(false);
            loading.show();
        }*/

        @Override
        protected void onPostExecute(Void aVoid) {
            if (refresh_data_flag == 0 ) {
                //int curSize = mAdapter.getItemCount();
                Log.e("~~reload", String.valueOf(curSize)+ "/ " + String.valueOf(mContentItem.size()));
                mAdapter.notifyItemRangeInserted(curSize+1, mContentItem.size());
            } else {
                Log.e("~~refresh"," err");
                mAdapter = new ContentsListAdapter(mContext, mContentItem);
                mRecyclerView.setAdapter(mAdapter);
                _sGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(_sGridLayoutManager);
            }
            refresh_data_flag = 0;

            GlobalVar.loading_flag = 0;
        }

        @Override
        protected Void doInBackground(String... value) {
            HttpURLConnection conn = null;
            OutputStream os = null;
            InputStream is = null;
            ByteArrayOutputStream baos = null;
            String response = null;

            curSize = mAdapter.getItemCount();
            if ( Integer.parseInt(value[0]) <= 0 ) {
                mContentItem.clear();
                refresh_data_flag = 1;
            }

            try {
                IgnoreHttpSertification.ignoreSertificationHttps();
                URL obj = new URL("https://toycom96.iptime.org:1443/bbs_list");
                conn = (HttpURLConnection) obj.openConnection();
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(5000);
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Cookie", value[3]);
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject job = new JSONObject();
                job.put("long", mLon);
                job.put("lat", mLat);
                job.put("cate", value[2]);
                job.put("dist", Integer.parseInt(value[1]));
                job.put("lidx", Integer.parseInt(value[0]));

                os = conn.getOutputStream();
                os.write(job.toString().getBytes("utf-8"));
                os.flush();

                int responseCode = conn.getResponseCode();
                //request code를 받음

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
                    Log.e("ItemResponse", response.toString());
                } else {
                    return null;
                }

                String result = "";
                if (response.isEmpty() || response.equals("null")) {
                    return null;
                }
                JSONArray ja = new JSONArray(response);

                for (int i = 0; i < ja.length(); i++) {
                    String mediaPath = "";
                    JSONObject order = ja.getJSONObject(i);
                    Log.e("ID", order.getString("User_name"));

                    try {
                        JSONObject mediaJson = new JSONObject(order.getString("Media"));
                        mediaPath = mediaJson.getString("img0");
                    } catch (Exception e) {
                        mediaPath = order.getString("Media");
                    }
                    this.mContentItem.add(new ContentsListObject(order.getInt("Id"), order.getInt("User_id"), order.getString("User_name"), mediaPath, order.getString("Term"), "ETC", order.getString("Title"), order.getString("Msg"), order.getString("User_sex"), order.getInt("User_age"), order.getInt("Dist")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }

            return null;
        }
    }
}
