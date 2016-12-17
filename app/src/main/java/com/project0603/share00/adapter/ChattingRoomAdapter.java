package com.project0603.share00.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.project0603.share00.model.ChattingRoom;
import com.project0603.share00.R;
import com.squareup.picasso.Picasso;

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

        TextView profile = (TextView) v.findViewById(R.id.chatlist_profile);
        TextView msg = (TextView) v.findViewById(R.id.chatlist_msg);
        TextView dist = (TextView) v.findViewById(R.id.chatlist_dist);
        TextView badge = (TextView) v.findViewById(R.id.chatlist_msgcnt);

        Log.e("~~chatrool list : ", String.valueOf(position));
        ChattingRoom item = mChatRoomList.get(position);

        profile.setText(item.getSended() + " / " + item.getRecv_name() + " (" + item.getEtcInfo() + ")");
        msg.setText(item.getMsg());
        if (!item.getPhoto().toString().isEmpty()) {
            CircleImageView photo = (CircleImageView) v.findViewById(R.id.chatlist_photo);

            if (item.getPhoto() != null && !item.getPhoto().equals("")) {
                try {
                    Picasso.with(mContext).load(item.getPhoto()).error(R.drawable.ic_menu_noprofile).into(photo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else{
                photo.setImageResource(R.drawable.ic_menu_noprofile);
            }

        }
        if (item.getBadgeCnt() > 0) {
            badge.setVisibility(TextView.VISIBLE);
        } else {
            badge.setVisibility(TextView.INVISIBLE);
        }

        return v;
    }
}
