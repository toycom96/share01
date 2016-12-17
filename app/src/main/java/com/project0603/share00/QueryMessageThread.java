package com.project0603.share00;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.project0603.share00.adapter.ChatAdapter;
import com.project0603.share00.model.ChatMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Lai.OH on 2016-09-06.
 */
public class QueryMessageThread extends AsyncTask<String, Void, Void> {

    private ChatAdapter mAdapter;
    private ArrayList<ChatMessage> mChatMessages;
    private ListView chat_listview;
    private Context mContext;
    private ChatActivity activity;
    public QueryMessageThread(Context context, ListView listview) {
        //this.mAdapter = adapter;
        //this.mChatMessages = chatMessages;
        this.mContext = context;
        this.chat_listview = listview;

        mChatMessages = new ArrayList<>();
    }

    /*
         * Create by Lai.OH on 2016-08-01
         * 채팅 메세지 조회 쓰레드
         * param(server_url, chat_room_id, auth)
         *
         * 채팅방에서 사용자간의 채팅 메세지를 불러오는 쓰레드
         *
         * 메세지들을 불러와 ArrayList<ChatMessage>에 저장
         * Adapter를 이용해 ListView에 등록
         * 등록하기전 중복을 제거하기 위해서 ListView Clear 진행
         */
    ProgressDialog loading;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        mAdapter = new ChatAdapter(this.mContext, mChatMessages);
        chat_listview.setAdapter(mAdapter);

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
        int chatroom_id = Integer.parseInt(value[1]);
        String UserPhoto = value[2];
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
            job.put("id", chatroom_id);

            os = conn.getOutputStream();
            os.write(job.toString().getBytes("utf-8"));
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

                JSONArray ja = new JSONArray(response);
                mChatMessages.clear();
                //중복제거를 위해 ArrayList값들을 제거함


                for (int i = ja.length() - 1; i >= 0; i--) {
                    //최신 메세지먼저 파싱하기 때문에 거꾸로 ArrayList에 입력
                    JSONObject order = ja.getJSONObject(i);

                    int getMsgId = Integer.parseInt(order.get("Msg_id").toString());
                    int mGetChatroomId = Integer.parseInt(order.get("Id").toString());
                    int getSendId = Integer.parseInt(order.get("Send_id").toString());

                    String getSenderName = order.get("User_name").toString();
                    String getMsg = order.get("Msg").toString();
                    String getTime = order.get("Sended").toString();

                    mChatMessages.add(new ChatMessage(getMsgId,mGetChatroomId, getSendId, UserPhoto, getSenderName, getMsg, getTime));
                    //메세지에 대한 내용 ArrayList에 저장
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
