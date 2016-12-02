package com.project0603.share01;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.project0603.share01.adapter.ChattingRoomAdapter;
import com.project0603.share01.model.ChattingRoom;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Lai.OH on 2016-07-27.
 */

public class ChatListActivity extends AppCompatActivity {

    private ChattingRoomAdapter mChatListAdapter;
    private ListView chatting_room_lv;
    private ArrayList<ChattingRoom> mChatRooms = null;
    //public Context mContext = ChatListActivity.this;

    private TextView recv_id_tv;
    private TextView msg_tv;
    private TextView time_tv;

    private RbPreference mPref = new RbPreference(ChatListActivity.this);
    private int totalChatBadgeCnt;

    private final String SERVER_URL = GlobalVar.https_api1 + "/chat_list";
    private static int load_flag = 0;

    private int getChatroomId;
    private int getRecvId;
    private String getRecvName;
    private String getMsg;
    private String getTime;
    private String getEtcInfo;
    private String getSex;
    private String getPhoto;
    private int getBadgeCnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_chat_list);
        setContentView(R.layout.activity_chat_list);
        init();

        this.registerReceiver(this.refreshChatRoomListReceiver, new IntentFilter("refreshChatRoomList"));
    }

    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(refreshChatRoomListReceiver);
    }

    //GcmBroadcastReceiver에서 보내는걸 받는 receiver
    BroadcastReceiver refreshChatRoomListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (load_flag == 0) {
                ChatListLoadThread chatlist = new ChatListLoadThread();
                chatlist.execute(SERVER_URL, Profile.auth);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if ( load_flag == 0 ) {
            ChatListLoadThread chatlist = new ChatListLoadThread();
            chatlist.execute(SERVER_URL, Profile.auth);
        }
    }

    private void init(){

        load_flag = 0;
        Toolbar toolbar = (Toolbar) findViewById(R.id.chatlist_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        chatting_room_lv = (ListView) findViewById(R.id.chattingroom_listview);

        mChatRooms = new ArrayList<>();

        if ( load_flag == 0) {
            ChatListLoadThread chatlist = new ChatListLoadThread();
            chatlist.execute(SERVER_URL, Profile.auth);
        }

        chatting_room_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (load_flag == 0 ) {
                    ChattingRoom chat_room = mChatRooms.get(position);
                    int chat_room_id = chat_room.getChatRoomID();
                    int chat_room_user_id = chat_room.getRecv_id();
                    Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
                    intent.putExtra("room_id", chat_room_id);
                    intent.putExtra("sender_id", chat_room_user_id);
                    intent.putExtra("sender_photo", chat_room.getPhoto());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ChatListActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /*
     * auth 값을 http header로 보내 사용자 정보를 받아오는 Thread
     */
    class ChatListLoadThread extends AsyncTask<String, Void, Void> {

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            load_flag = 1;
            super.onPreExecute();
            loading = new ProgressDialog(ChatListActivity.this);
            loading.setTitle("채팅방 리스트");
            loading.setMessage("채팅방 리스트를 받는 중이에요...");
            loading.setCancelable(false);
            loading.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mPref.put("badge_chatcnt",  String.valueOf(totalChatBadgeCnt));
            GcmBroadcastReceiver.updateIconBadge(ChatListActivity.this, totalChatBadgeCnt);

            mChatListAdapter = new ChattingRoomAdapter(ChatListActivity.this, mChatRooms);

            chatting_room_lv.setAdapter(mChatListAdapter);
            loading.dismiss();
            load_flag = 0;
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

            totalChatBadgeCnt = 0;

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


                os = conn.getOutputStream();
                os.flush();

                int responseCode = conn.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK) {
                    int unix_sec = 0;

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


                    if (response.isEmpty() || response.equals("null") ) {
                        return null;
                    }
                    mChatRooms.clear();

                    JSONArray ja = new JSONArray(response);
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject order = ja.getJSONObject(i);

                        getChatroomId = Integer.parseInt(order.get("Id").toString());
                        getRecvId = Integer.parseInt(order.get("Recv_id").toString());
                        getRecvName = order.get("User_name").toString();
                        getMsg = order.get("Msg").toString();
                        getPhoto = order.get("User_photo").toString();
                        getBadgeCnt = Integer.parseInt(order.get("Badge_cnt").toString());
                        unix_sec = Integer.parseInt(order.get("Term").toString());

                        if ( unix_sec > (6 * 30 * 24 * 60 * 60) ) { getTime = "반년이상"; }
                        else if ( unix_sec > ( 30 * 24 * 60 * 60) ) { getTime = (unix_sec / ( 30 * 24 * 60 * 60)) + "개월전"; }
                        else if ( unix_sec > ( 24 * 60 * 60) ) { getTime = (unix_sec / ( 24 * 60 * 60)) + "일전"; }
                        else if ( unix_sec > ( 60 * 60) ) { getTime = (unix_sec / ( 60 * 60)) + "시간전"; }
                        else if ( unix_sec > ( 60) ) { getTime = (unix_sec / (60)) + "분전"; }
                        else { getTime = unix_sec + "초전"; }
                        //getTime = order.get("Sended").toString();

                        getSex = order.get("User_sex").toString();

                        if (getSex == "F") {
                            getEtcInfo = "여, " + order.get("User_age").toString() + "세";
                        } else {
                            getEtcInfo = "남, " + order.get("User_age").toString() + "세";
                        }

                        mChatRooms.add(new ChattingRoom(getChatroomId,getRecvId, getRecvName, getMsg, getTime, getSex, getEtcInfo, getPhoto, getBadgeCnt));
                        totalChatBadgeCnt = totalChatBadgeCnt + getBadgeCnt;
                    }
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
