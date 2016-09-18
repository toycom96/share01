package com.example.ethan.share01.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ethan.share01.R;
import com.example.ethan.share01.model.ChattingRoom;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Lsi.OH on 2016-07-27.
 */
public class ChattingRoomAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ChattingRoom> mChatRoomList;

    public ChattingRoomAdapter(Context context, ArrayList<ChattingRoom> chatRoomList) {
        mContext = context;
        mChatRoomList = chatRoomList;
    }

    @Override
    public int getCount() {
        return mChatRoomList.size();
    }

    @Override
    public Object getItem(int position) {
        return mChatRoomList.get(position);
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
        TextView etc = (TextView) v.findViewById(R.id.chatlist_etc);
        TextView badge = (TextView) v.findViewById(R.id.chatlist_msgcnt);

        ChattingRoom item = mChatRoomList.get(position);

        title.setText(item.getRecv_name());
        if (item.getSex().equals("F")) { title.setTextColor(Color.parseColor("#FF0000")); }
        else { title.setTextColor(Color.parseColor("#0000FF")); }
        msg.setText(item.getMsg());
        time.setText(item.getSended());
        etc.setText(item.getEtcInfo());
        if (!item.getPhoto().toString().isEmpty()) {
            CircleImageView photo = (CircleImageView) v.findViewById(R.id.chatlist_photo);
            Picasso.with(mContext).load(item.getPhoto()).resize(72, 72).into(photo);
        }
        if (item.getBadgeCnt() > 0) {
            badge.setBackgroundResource(R.drawable.ic_badge_new);

        //} else {
        //    badge.setBackground("");
        }

        return v;
    }
}
