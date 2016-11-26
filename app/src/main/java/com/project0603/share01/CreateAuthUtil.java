package com.project0603.share01;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by OHRok on 2016-07-11.
 */

class CreateAuthUtil extends AsyncTask<String, Void, Void> {
    private final String request_auth_url = GlobalVar.https_dns1 + "/auth_update";
    private RbPreference mPref;
    private Context context;

    public CreateAuthUtil(Context context) {
        this.context = context;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        //context.sendBroadcast(new Intent("com.project0603.share01.AuthFinish"));
        context.sendBroadcast(new Intent("AuthFinish"));
    }

    @Override
    protected Void doInBackground(String... value) {
        HttpURLConnection conn = null;
        OutputStream os = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        String response = null;

        try {
            /*GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
            String regId = gcm.register("1028702649415");

            Profile.gcm_id = regId;*/

            IgnoreHttpSertification.ignoreSertificationHttps();
            URL obj = new URL(request_auth_url);
            conn = (HttpURLConnection) obj.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            conn.setDoOutput(true);
            conn.setDoInput(true);


            JSONObject job = new JSONObject();
            //JSONObject 생성 후 input
            job.put("id", Profile.user_id);
            job.put("device_id", Profile.device_id);
            //job.put("gcm_id", Profile.gcm_id);

            os = conn.getOutputStream();
            os.write(job.toString().getBytes("utf-8"));
            os.flush();

            int responseCode = conn.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK) {

                Log.e("HTTP_OK", "HTTP OK RESULT");
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

                String result = responseJSON.get("result").toString();
                Log.i("auth_result_code", result);

                List<String> cookies = conn.getHeaderFields().get("Set-Cookie");
                if (cookies != null) {
                    for (String cookie : cookies) {
                        String getAuth = cookie.split(";")[0];
                        mPref = new RbPreference(context);

                        mPref.put("user_id", String.valueOf(Profile.user_id));
                        mPref.put("device_id", Profile.device_id);
                        mPref.put("gcm_id", Profile.gcm_id);

                        Profile.auth = getAuth;
                        Profile.auth_finish = 1;
                    }

                } else {
                    Log.e("cookies", "cookie null");
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