package com.example.ethan.share01.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ethan.share01.R;
import com.example.ethan.share01.model.BbsMemo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by leedongkwang on 2016. 10. 12..
 */


public class BbsMemoAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<BbsMemo> mBbsMemoList;

    public BbsMemoAdapter(Context context, ArrayList<BbsMemo> bbsMemoList) {
        mContext = context;
        mBbsMemoList = bbsMemoList;
    }

    @Override
    public int getCount() {
        return mBbsMemoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBbsMemoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = View.inflate(mContext, R.layout.custom_chat_listview, null);
        TextView title = (TextView) v.findViewById(R.id.chatlist_title);
        TextView msg = (TextView) v.findViewById(R.id.chatlist_msg);
        TextView time = (TextView) v.findViewById(R.id.chatlist_time);
        TextView etc = (TextView) v.findViewById(R.id.chatlist_etc); //거리
        //TextView badge = (TextView) v.findViewById(R.id.chatlist_msgcnt);

        BbsMemo item = mBbsMemoList.get(position);

        title.setText(item.getUser_name());
        if (item.getUser_sex().equals("여")) {
            title.setTextColor(Color.parseColor("#FF0000"));
        } else {
            title.setTextColor(Color.parseColor("#0000FF"));
        }
        etc.setText(item.getUser_sex() + " / "  + String.valueOf(item.getUser_age()) + "세" );
        msg.setText(item.getMemo());
        msg.setMaxLines(5);

        time.setText(item.getTerm());
        if (!item.getUser_photo().toString().isEmpty()) {
            CircleImageView photo = (CircleImageView) v.findViewById(R.id.chatlist_photo);
            Picasso.with(mContext).load(item.getUser_photo().toString()).resize(72, 72).into(photo);
        }
        /*if (item.getBadgeCnt() > 0) {
            badge.setBackgroundResource(R.drawable.ic_badge_new);

            //} else {
            //    badge.setBackground("");
        }*/

        return v;
    }
}
