package com.example.ethan.share01;

import android.content.Intent;
import android.os.AsyncTask;
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
import java.util.List;

/**
 * Created by ethan on 16. 6. 17..
 */
public class ContentsListLoad {

    static List<ContentsListObject> mContentItem;
    static ContentsListAdapter mAdapter;


    public ContentsListLoad (List<ContentsListObject> ContentItem, ContentsListAdapter ListAdapter) {
        this.mContentItem = ContentItem;
        this.mAdapter = ListAdapter;
    }

    public int loadFromApi(int ListIndex, int flag) {
        getBbsList BbsList = new getBbsList(mContentItem, mAdapter);
        BbsList.execute("0", "1");

        return 0;
    }

    static int updateUi() {
        int curSize = mAdapter.getItemCount();
        mAdapter.notifyItemRangeInserted(curSize, mContentItem.size() - 1);
        return 0;
    }


    class getBbsList extends AsyncTask<String, Void, Void> {
        private List<ContentsListObject> mContentItem;
        public ContentsListAdapter mAdapter;


        public getBbsList (List<ContentsListObject> ContentItem, ContentsListAdapter ListAdapter) {
            this.mContentItem = ContentItem;
            this.mAdapter = ListAdapter;
        }

        protected void onPostExecute(Void aVoid) {
            ContentsListLoad.updateUi();
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
                URL obj = new URL("https://toycom96.iptime.org:1443/bbs_list");
                conn = (HttpURLConnection) obj.openConnection();
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(5000);
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Cookie", "auth=NtUMVRdHefRNbYut82ALIz0kE6a6Q80D13krg/xdxWfi8E3H+nqv5+54Xxo49D4n8tNaTQ==");
                conn.setDoOutput(true);
                conn.setDoInput(true);


                JSONObject job = new JSONObject();
                job.put("long", 126.7459979);
                job.put("lat", 37.259485);
                job.put("dist", 10000);
                job.put("lidx", 0);

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
                }

                String result = "";
                JSONArray ja = new JSONArray(response);
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject order = ja.getJSONObject(i);

                    this.mContentItem.add(new ContentsListObject(order.getInt("Id"), order.getString("Media"), order.getString("Title") + "\n" + order.getString("Title") ));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
