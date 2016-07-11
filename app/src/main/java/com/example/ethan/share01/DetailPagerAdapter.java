package com.example.ethan.share01;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ATIV Book 6 2014ED on 2016-06-30.
 */
public class DetailPagerAdapter extends PagerAdapter {

    private static LayoutInflater inflater;
    private static String route;
    private Context mContext;
    //private int picWidth = 470;

    public DetailPagerAdapter(LayoutInflater inflater, String route, Context context) {
        this.inflater = inflater;
        this.route = route;
        this.mContext = context;
        //DisplayMetrics dm = context.getResources().getDisplayMetrics();
       // picWidth = (int)((float)dm.widthPixels);
    }

    @Override
    public int getCount() {
        return 1;
    } //pagerAdapter에서 관리할 page의 갯수, 추후 여러장의 사진일 경우 그에 대한 length를 구해서 return값 변경

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = null;
        view = inflater.inflate(R.layout.viewpager_childview, null);
        ImageView img = (ImageView) view.findViewById(R.id.iv_viewpager_childimg);
        Picasso.with(mContext).load(route).fit().error(R.drawable.ic_menu_camera).into(img);
        //image를 확인한 후 fit으로 자동 resize를 하고 imageView에 연결, error값으로 임의의 camera사진 넣어놈, 추후 error image 변경
        //img.setImageResource(Integer.parseInt(route));
        container.addView(view);
        //Toast.makeText(container.getContext(),"route = " + route,Toast.LENGTH_SHORT).show();
        return view;
    } // viewPager에서 사용할 view객체 생성 및 등록하는 메소드

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    } // view객체 삭제, ex) 2번에서 3번의 image로 넘어갈 경우 1번 image 삭제, data의 낭비를 막아줌

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    //instantiateItem메소드에서 생성한 객체를 이용할 것인지 여부 반환, true의 경우에만 이용
}
