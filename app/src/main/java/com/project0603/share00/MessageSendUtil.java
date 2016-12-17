package com.project0603.share00;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Lai.OH on 2016-08-29.
 */
public class MessageSendUtil extends AsyncTask<String, Void, Void>{

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

    private Context context;
    private String mGetChatRoomId;



    public MessageSendUtil(Context context) {
        this.context = context;
    }

    ProgressDialog loading;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loading = new ProgressDialog(context);
        loading.setMessage("보내는중");
        loading.setCancelable(false);
        loading.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Toast.makeText(context, "보내기 완료", Toast.LENGTH_SHORT).show();
        loading.dismiss();

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
                JSONObject responseJSON = new JSONObject(response);
                mGetChatRoomId = responseJSON.get("chat_id").toString();
            }else {
                Log.e("HTTP_ERROR", "NOT CONNECTED HTTP");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
