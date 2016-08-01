package com.example.ethan.share01.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ethan.share01.R;
import com.example.ethan.share01.model.ChatMessage;
import com.example.ethan.share01.model.ChattingRoom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lai.OH on 2016-07-28.
 */
public class ChatAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ChatMessage> mChatList;

    public ChatAdapter(Context context, ArrayList<ChatMessage> chatList) {
        mContext = context;
        mChatList = chatList;
    }

    @Override
    public int getCount() {
        return mChatList.size();
    }

    @Override
    public Object getItem(int position) {
        return mChatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = View.inflate(mContext, R.layout.custom_chatbubble, null);
        TextView title = (TextView) v.findViewById(R.id.chat_title);
        TextView msg = (TextView) v.findViewById(R.id.chat_message);
        TextView time = (TextView) v.findViewById(R.id.chat_time);

        ChatMessage item = mChatList.get(position);

        title.setText(item.getSender_id());
        msg.setText(item.getMessage());
        time.setText(item.getTime());

        return v;
    }
}
