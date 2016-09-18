package com.example.ethan.share01;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Lai.OH on 2016-08-16.
 */
public class ImageChooseUtil {
    private Context context;
    private Intent data;
    private String webAddressToPost = "https://192.168.0.14:1443/up_file";
    private Bitmap bitmap;
    private RbPreference mPref;

    private String getPath;
    public ImageChooseUtil(Intent data, Context context) {
        this.data = data;
        this.context = context;
        mPref = new RbPreference(this.context);
    }

    public String getRealPath() {

        Uri selectedImage = this.data.getData();
        if( this.data == null ) {
            return null;
        }

        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(selectedImage, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }

        return selectedImage.getPath();
    }

}
