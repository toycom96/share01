package com.project0603.share00;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.project0603.share00.adapter.ChatAdapter;
import com.project0603.share00.R;
import com.project0603.share00.model.ChatMessage;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Lai.OH on 2016-07-27.
 */

public class ChatActivity extends AppCompatActivity {

    private EditText message_edt;
    public ListView chat_listview;
    private Button send_btn;
    private ChatAdapter mAdapter;
    private ArrayList<ChatMessage> mChatMessages;
    public Context mContext = ChatActivity.this;

    private int mGetSendId;
    private int mGetChatRoomId = -1;
    private String mGetSenderPhoto = "";

    private final String SERVER_URL_SEND = GlobalVar.https_api1 + "/chat_send";
    private final String SERVER_URL_QUERY = GlobalVar.https_api1 + "/chat_msg_list";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chat_listview = (ListView) findViewById(R.id.chat_listview);
        message_edt = (EditText) findViewById(R.id.chat_message_edt);
        send_btn = (Button) findViewById(R.id.chat_sendbutton);

        mChatMessages = new ArrayList<>();
        init();
        //receiver 등록
        this.registerReceiver(this.appendChatScreenMsgReceiver, new IntentFilter("appendChatScreenMsg"));

    }

    @Override
    protected void onResume() {
        super.onResume();
        QueryMessageThread query = new QueryMessageThread(mContext, chat_listview);
        query.execute(SERVER_URL_QUERY, String.valueOf(mGetChatRoomId), mGetSenderPhoto, Profile.auth);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(appendChatScreenMsgReceiver);
    }

    //GcmBroadcastReceiver에서 보내는걸 받는 receiver
    BroadcastReceiver appendChatScreenMsgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            String RoomId = b.getString("Room_id");
            if (b != null && RoomId.equals(String.valueOf(mGetChatRoomId))) {
                QueryMessageThread query = new QueryMessageThread(mContext, chat_listview);
                query.execute(SERVER_URL_QUERY, String.valueOf(mGetChatRoomId), mGetSenderPhoto, Profile.auth);
            }
        }
    };

    private void init() {


        /*
         * 새로운 채팅 확인해보기
         */
        mGetChatRoomId = getIntent().getIntExtra("room_id", -1);
        mGetSendId = getIntent().getIntExtra("sender_id", -1);
        mGetSenderPhoto = getIntent().getStringExtra("sender_photo");
        switch (mGetChatRoomId) {
            case -1 :
                Toast.makeText(getApplicationContext(), "새로운 채팅", Toast.LENGTH_SHORT).show();
                break;
            default:
                QueryMessageThread query = new QueryMessageThread(this, chat_listview);
                query.execute(SERVER_URL_QUERY, String.valueOf(mGetChatRoomId), mGetSenderPhoto, Profile.auth);
        }

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = message_edt.getText().toString();
                //사용자 입력 메세지 내용을 받아옴
                String auth = Profile.auth;
                if (TextUtils.isEmpty(messageText)) {
                    //공란일 경우 보내기 방지
                    return;
                }
                message_edt.setText("");
                SendMessageThread send = new SendMessageThread();
                send.execute(SERVER_URL_SEND,String.valueOf(mGetSendId),messageText,auth);
            }
        });

    }


    class SendMessageThread extends AsyncTask<String, Void, Void> {

        /*
         * Create by Lai.OH on 2016-08-01
         * 채팅 메세지 보내는 쓰레드
         * param(server_url, recv_id, message, auth)
         *
         * recv_id에 대한 사용자에게 메세지를 보내는 함수
         *
         * 메세지들을 불러와 ArrayList<ChatMessage>에 저장
         * Adapter를 이용해 ListView에 등록
         * 등록하기전 중복을 제거하기 위해서 ListView Clear 진행
         */

        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(ChatActivity.this);
            loading.setMessage("보내는중");
            loading.setCancelable(false);
            loading.show();
            //loading = ProgressDialog.show(SignupActivity.this, "회원가입 중...", null,true,true);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(ChatActivity.this, "보내기 완료", Toast.LENGTH_SHORT).show();
            loading.dismiss();


            QueryMessageThread qeury = new QueryMessageThread(mContext, chat_listview);
            qeury.execute(SERVER_URL_QUERY,String.valueOf(mGetChatRoomId), mGetSenderPhoto, Profile.auth);
            //메세지 보낸 뒤 대화내용 최신화를 위해 메세지 내용 검색 쓰레드 호출
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
            int recv_id = Integer.parseInt(value[1]);
            String message = value[2];
            String auth = value[3];

            try {
                IgnoreHttpSertification.ignoreSertificationHttps();
                URL obj = new URL(connUrl);
                conn = (HttpURLConnection) obj.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.addRequestProperty("Cookie", auth);

                conn.setDoOutput(true);
                conn.setDoInput(true);


                JSONObject job = new JSONObject();
                job.put("recv_id", recv_id);
                job.put("msg", message);

                os = conn.getOutputStream();
                os.write(job.toString().getBytes("utf-8"));
                os.flush();
                //Buffer에 있는 모든 정보를 보냄

                int responseCode = conn.getResponseCode();
                //request code를 받음

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
                    JSONObject responseJSON = new JSONObject(response);
                    mGetChatRoomId = Integer.parseInt(responseJSON.get("chat_id").toString());

                }else {
                    Log.e("HTTP_ERROR", "NOT CONNECTED HTTP");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ChatActivity.this, ChatListActivity.class);
        startActivity(intent);
        finish();
    }
}
