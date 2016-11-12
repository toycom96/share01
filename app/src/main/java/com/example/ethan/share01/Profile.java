package com.example.ethan.share01;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by leedongkwang on 2016. 11. 9..
 */
//로컬에는 user_id, device_id, auth만 저장하도록
public class Profile {
    public static int user_id=0;
    public static String device_id="";
    public static String email="";
    public static String name="";
    public static String photo="";
    public static String auth="";
    public static int age=0;
    public static String sex="";

    public static String gcm_id="";

    public static double gpslat=0.0;
    public static double gpslong=0.0;

    public Profile(String jsonstr) {
        if (jsonstr.equals("removeAll")) {
            removeAll();
            return;
        }
        try {
            JSONObject responseJSON = new JSONObject(jsonstr);

            if (!responseJSON.get("Id").toString().equals(""))
                user_id = Integer.parseInt(responseJSON.get("Id").toString());
            if (!responseJSON.get("Device_id").toString().equals(""))
                device_id = responseJSON.get("Device_id").toString();
            if (!responseJSON.get("Email").toString().equals(""))
                email = responseJSON.get("Email").toString();
            if (!responseJSON.get("Name").toString().equals(""))
                name = responseJSON.get("Name").toString();
            if (!responseJSON.get("Photo").toString().equals(""))
                photo = responseJSON.get("Photo").toString();
            if (!responseJSON.get("Age").toString().equals(""))
                age = Integer.parseInt(responseJSON.get("Age").toString());
            if (!responseJSON.get("Sex").toString().equals(""))
                sex = responseJSON.get("Sex").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    public void removeAll(){
        user_id = 0;
        device_id = "";
        email="";
        name="";
        photo="";
        auth="";
        age = 0;
        sex = "";
        gcm_id="";
        gpslat = 0.0;
        gpslong = 0.0;
    }
}
