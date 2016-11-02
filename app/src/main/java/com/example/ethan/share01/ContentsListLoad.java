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
        private  Context mContext;
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(mContext);
            loading.setTitle("게시글 받아오는중");
            loading.setMessage("조금만 기다려 주세요~~");
            loading.setCancelable(false);
            loading.show();
        }

        protected void onPostExecute(Void aVoid) {

            /*
             * doInBackground에서 모든 데이터를 add한 뒤 adapter에 연결 후 recyclerView에 뿌려준다.
             */
            Log.e("ContentLoadTask", "onPostExecute");
            Log.e("mContentItem", mContentItem.toString());
            if (mRecyclerView != null) {
                mAdapter = new ContentsListAdapter(mContext, mContentItem);
                mRecyclerView.setAdapter(mAdapter);
                _sGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(_sGridLayoutManager);
                loading.dismiss();
                loading = null;
            } else {
                Log.e("recyclerView", "null");
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
                Integer not_internet = 1;
                if(not_internet==0) {
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
                } else {
                    String result = "";
                    response = "[{\"Id\":97,\"Cate\":\"고민\",\"User_id\":102,\"User_name\":\"test_s7\",\"User_age\":35,\"User_sex\":\"M\",\"Title\":\"맥북 프로 15? 13\",\"Msg\":\"맥북 프로 15인치가 화면이 크고 좋는데\\n가지고 다니기엔 13인치가 좋은것 같고\\n현재는 15인치를 쓰느데 새로살건 뭘로 살까여?\",\"Media\":\"{\\\"img0\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/410340.19019g68d38hf.0.bbsimg.jpg\\\"}\",\"Option\":\"{\\\"pay\\\":\\\"댓글 놀이\\\"}\",\"Lat\":126.95767,\"Long\":37.50114,\"Dist\":1277,\"Lidx\":0,\"Term\":821994},{\"Id\":96,\"Cate\":\"고민\",\"User_id\":102,\"User_name\":\"test_s7\",\"User_age\":35,\"User_sex\":\"M\",\"Title\":\"아이폰 7 살까요 말까요\",\"Msg\":\"아이폰 5s가 있는데\\n아이폰 7이 나왔다니 살고 싶은 생각이 드네요.\\n아직 느린건 없고 불편함은 없는데...\",\"Media\":\"{\\\"img0\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/410340.190199idopl0a.0.bbsimg.jpg\\\"}\",\"Option\":\"{\\\"pay\\\":\\\"댓글 놀이 ㅋ\\\"}\",\"Lat\":126.95801,\"Long\":37.50131,\"Dist\":1277,\"Lidx\":0,\"Term\":822221},{\"Id\":95,\"Cate\":\"시간\",\"User_id\":103,\"User_name\":\"전지현\",\"User_age\":35,\"User_sex\":\"F\",\"Title\":\"카페에서 수다\",\"Msg\":\"심심해요. 같이 카페에서 수다 떠실분\\n커피는 제가 쏩니다\",\"Media\":\"{\\\"img0\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/410222.18vl5qfmho0mm.0.bbsimg.jpg\\\"}\",\"Option\":\"{\\\"pay\\\":\\\"아메리카노 1잔\\\"}\",\"Lat\":127.108604,\"Long\":37.401814,\"Dist\":1275,\"Lidx\":0,\"Term\":1248237},{\"Id\":94,\"Cate\":\"물건\",\"User_id\":103,\"User_name\":\"전지현\",\"User_age\":35,\"User_sex\":\"F\",\"Title\":\"전동 드릴 구합니다\",\"Msg\":\"좀 큰걸로 구해봅니다.\\n지금 당장 필요한데 살수도 없고..\",\"Media\":\"{\\\"img0\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/410222.18vl5l5qdavav.0.bbsimg.jpg\\\"}\",\"Option\":\"{\\\"pay\\\":\\\"아메리카노 1잔\\\"}\",\"Lat\":127.108604,\"Long\":37.401814,\"Dist\":1275,\"Lidx\":0,\"Term\":1248371},{\"Id\":93,\"Cate\":\"물건\",\"User_id\":103,\"User_name\":\"전지현\",\"User_age\":35,\"User_sex\":\"F\",\"Title\":\"국사 족보 구합니다\",\"Msg\":\"교양 국사 과목의 족보를 구합니다\\n복사해 주시면 아메리카노 한잔 드리겠습니다\",\"Media\":\"{\\\"img0\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/410222.18vl5j9m1u8b6.0.bbsimg.jpg\\\"}\",\"Option\":\"{\\\"pay\\\":\\\"아메리카노 1잔\\\"}\",\"Lat\":127.108604,\"Long\":37.401814,\"Dist\":1275,\"Lidx\":0,\"Term\":1248496},{\"Id\":92,\"Cate\":\"시간\",\"User_id\":103,\"User_name\":\"전지현\",\"User_age\":35,\"User_sex\":\"F\",\"Title\":\"점심 같이해여\",\"Msg\":\"혼밥에 익숙한데\\n오늘따라 같이 점심을 먹고 싶네요\\n커피는 제가 살게요\",\"Media\":\"{\\\"img0\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/410222.18vl5b8dkfsd2.0.bbsimg.jpg\\\"}\",\"Option\":\"{\\\"pay\\\":\\\"아메리카노 1잔\\\"}\",\"Lat\":127.108604,\"Long\":37.401814,\"Dist\":1275,\"Lidx\":0,\"Term\":1248774},{\"Id\":91,\"Cate\":\"\",\"User_id\":102,\"User_name\":\"test_s7\",\"User_age\":35,\"User_sex\":\"M\",\"Title\":\"심심해서 같이 시간 때우실분\",\"Msg\":\"심심해요.\\n같이 시간 때우실분 계신가요?\",\"Media\":\"{\\\"img0\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/410220.18vkukgvlaqs1.0.bbsimg.jpg\\\",\\\"img1\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/410220.18vkuksqqrnlu.0.bbsimg.jpg\\\"}\",\"Option\":\"{\\\"pay\\\":\\\"아메리카노 한잔\\\"}\",\"Lat\":127.10864,\"Long\":37.402485,\"Dist\":1276,\"Lidx\":0,\"Term\":1256138},{\"Id\":90,\"Cate\":\"\",\"User_id\":102,\"User_name\":\"test_s7\",\"User_age\":35,\"User_sex\":\"M\",\"Title\":\"\",\"Msg\":\"\",\"Media\":\"{\\\"img0\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/410148.18vdki6kttg8b.0.bbsimg.jpg\\\",\\\"img1\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/410148.18vdkid6t9t0r.0.bbsimg.jpg\\\",\\\"img2\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/410148.18vdkiupohobe.0.bbsimg.jpg\\\"}\",\"Option\":\"{\\\"pay\\\":\\\"\\\"}\",\"Lat\":126.95759,\"Long\":37.50117,\"Dist\":1277,\"Lidx\":0,\"Term\":1513478},{\"Id\":89,\"Cate\":\"\",\"User_id\":102,\"User_name\":\"test_s7\",\"User_age\":35,\"User_sex\":\"M\",\"Title\":\"점심 같이해요\",\"Msg\":\"점심 사주시면 커피는 제가 쏠게여\",\"Media\":\"{\\\"img0\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/409887.18uit00uj4b6c.0.bbsimg.jpg\\\",\\\"img1\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/409887.18uit0558f84v.0.bbsimg.jpg\\\"}\",\"Option\":\"{\\\"pay\\\":\\\"스벅 아메리카노\\\"}\",\"Lat\":127.10864,\"Long\":37.402657,\"Dist\":1276,\"Lidx\":0,\"Term\":2454223},{\"Id\":88,\"Cate\":\"\",\"User_id\":102,\"User_name\":\"test_s7\",\"User_age\":35,\"User_sex\":\"M\",\"Title\":\"점심 같이 먹어요\",\"Msg\":\"점심 사주시면 커피는 제가 쏠게요\",\"Media\":\"{\\\"img0\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/409887.18uisrudrhg6i.0.bbsimg.jpg\\\",\\\"img1\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/409887.18uiss3ch52mf.0.bbsimg.jpg\\\"}\",\"Option\":\"\",\"Lat\":127.10862,\"Long\":37.40268,\"Dist\":1276,\"Lidx\":0,\"Term\":2454362},{\"Id\":87,\"Cate\":\"\",\"User_id\":102,\"User_name\":\"test_s7\",\"User_age\":35,\"User_sex\":\"M\",\"Title\":\"같이 밥먹어요\",\"Msg\":\"오피스텔 생활에 혼밥은 기본이지만\\n오늘은 함께 먹고 싶네요\",\"Media\":\"{\\\"img0\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/409886.18uir5u5m4s0q.0.bbsimg.jpg\\\",\\\"img1\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/409886.18uir68geajls.0.bbsimg.jpg\\\"}\",\"Option\":\"\",\"Lat\":127.10867,\"Long\":37.402637,\"Dist\":1276,\"Lidx\":0,\"Term\":2456210},{\"Id\":86,\"Cate\":\"\",\"User_id\":103,\"User_name\":\"전지현\",\"User_age\":35,\"User_sex\":\"F\",\"Title\":\"한국사 족보를 구합니다\",\"Msg\":\"테스트로 작성한 글입니다\",\"Media\":\"{\\\"img0\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/409696.18tvckhkfcfm1.0.bbsimg.jpg\\\",\\\"img1\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/409696.18tvckq8pv5i7.0.bbsimg.jpg\\\"}\",\"Option\":\"\",\"Lat\":126.95744,\"Long\":37.501137,\"Dist\":1277,\"Lidx\":0,\"Term\":3140707},{\"Id\":85,\"Cate\":\"\",\"User_id\":102,\"User_name\":\"test_s7\",\"User_age\":35,\"User_sex\":\"M\",\"Title\":\"\",\"Msg\":\"차량 공유\",\"Media\":\"{\\\"img0\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/409672.18tssf7d2m0a5.0.bbsimg.jpg\\\",\\\"img1\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/409672.18tssfcmv9cu2.0.bbsimg.jpg\\\",\\\"img2\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/409672.18tssfk7ard1r.0.bbsimg.jpg\\\"}\",\"Option\":\"\",\"Lat\":126.95746,\"Long\":37.501167,\"Dist\":1277,\"Lidx\":0,\"Term\":3228809},{\"Id\":84,\"Cate\":\"\",\"User_id\":102,\"User_name\":\"test_s7\",\"User_age\":35,\"User_sex\":\"M\",\"Title\":\"\",\"Msg\":\"차량 공유\",\"Media\":\"{\\\"img0\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/409672.18tssf7d2m0a5.0.bbsimg.jpg\\\",\\\"img1\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/409672.18tssfcmv9cu2.0.bbsimg.jpg\\\",\\\"img2\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/409672.18tssfk7ard1r.0.bbsimg.jpg\\\"}\",\"Option\":\"\",\"Lat\":126.95746,\"Long\":37.501167,\"Dist\":1277,\"Lidx\":0,\"Term\":3228809},{\"Id\":83,\"Cate\":\"\",\"User_id\":102,\"User_name\":\"test_s7\",\"User_age\":35,\"User_sex\":\"M\",\"Title\":\"\",\"Msg\":\"차량 공유\",\"Media\":\"{\\\"img0\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/409672.18tssf7d2m0a5.0.bbsimg.jpg\\\",\\\"img1\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/409672.18tssfcmv9cu2.0.bbsimg.jpg\\\",\\\"img2\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/409672.18tssfk7ard1r.0.bbsimg.jpg\\\"}\",\"Option\":\"\",\"Lat\":126.95746,\"Long\":37.501167,\"Dist\":1277,\"Lidx\":0,\"Term\":3228814},{\"Id\":82,\"Cate\":\"\",\"User_id\":102,\"User_name\":\"test_s7\",\"User_age\":35,\"User_sex\":\"M\",\"Title\":\"\",\"Msg\":\"차량 공유\",\"Media\":\"{\\\"img0\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/409672.18tssf7d2m0a5.0.bbsimg.jpg\\\",\\\"img1\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/409672.18tssfcmv9cu2.0.bbsimg.jpg\\\",\\\"img2\\\":\\\"http:\\\\/\\\\/toycom96.iptime.org\\\\/img\\\\/409672.18tssfk7ard1r.0.bbsimg.jpg\\\"}\",\"Option\":\"\",\"Lat\":126.95746,\"Long\":37.501167,\"Dist\":1277,\"Lidx\":0,\"Term\":3228819},{\"Id\":81,\"Cate\":\"\",\"User_id\":102,\"User_name\":\"test_s7\",\"User_age\":35,\"User_sex\":\"M\",\"Title\":\"\",\"Msg\":\"차량공유\",\"Media\":\"[B@1d8fbcc\",\"Option\":\"\",\"Lat\":126.9575,\"Long\":37.501156,\"Dist\":1277,\"Lidx\":0,\"Term\":3228967},{\"Id\":80,\"Cate\":\"\",\"User_id\":102,\"User_name\":\"test_s7\",\"User_age\":35,\"User_sex\":\"M\",\"Title\":\"\",\"Msg\":\"차량 공유\",\"Media\":\"[B@895efa8\",\"Option\":\"\",\"Lat\":126.957504,\"Long\":37.501156,\"Dist\":1277,\"Lidx\":0,\"Term\":3229290},{\"Id\":79,\"Cate\":\"\",\"User_id\":111,\"User_name\":\"호로록\",\"User_age\":25,\"User_sex\":\"M\",\"Title\":\"\",\"Msg\":\"알려주실분\",\"Media\":\"http://toycom96.iptime.org/img/409194.18sbv93t1vrnm.0.bbsimg.jpg\",\"Option\":\"\",\"Lat\":127.136154,\"Long\":35.794064,\"Dist\":1186,\"Lidx\":0,\"Term\":4949796},{\"Id\":78,\"Cate\":\"\",\"User_id\":111,\"User_name\":\"호로록\",\"User_age\":25,\"User_sex\":\"M\",\"Title\":\"\",\"Msg\":\"책 팔아여~~\",\"Media\":\"http://toycom96.iptime.org/img/409193.18sbsqigvsit3.0.bbsimg.jpg\",\"Option\":\"\",\"Lat\":127.13613,\"Long\":35.794064,\"Dist\":1186,\"Lidx\":0,\"Term\":4952497}]";
                    if (response.isEmpty() || response.equals("null")) {
                    /*
                     * response가 null인 경우, 즉 반경내의 게시글이 없을경우
                     * item을 clear 해준다
                     * 안해줄시 그전의 내용이 남아있음.
                     */
                        mContentItem.clear();
                        return null;
                    }
                    JSONArray ja = new JSONArray(response);
                /*
                 * 반경에 따른 response값을 adding 해주기전 clear를 해야 item들이 겹치지 않는다.
                 */
                    mContentItem.clear();

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
                        //this.mContentItem.add(new ContentsListObject(order.getInt("Id"), order.getInt("User_id"), order.getString("User_name"), order.getString("Media"), order.getString("Term"), "ETC",order.getString("Msg"), order.getString("User_sex"), order.getInt("User_age"), order.getInt("Dist")));
                        this.mContentItem.add(new ContentsListObject(order.getInt("Id"), order.getInt("User_id"), order.getString("User_name"), mediaPath, order.getString("Term"), "ETC", order.getString("Title"), order.getString("Msg"), order.getString("User_sex"), order.getInt("User_age"), order.getInt("Dist")));
                        //mediaJson = null;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }

            return null;
        }
    }
}
