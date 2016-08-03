package com.example.ethan.share01.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ethan.share01.R;
import com.example.ethan.share01.RbPreference;
import com.example.ethan.share01.model.ChatMessage;

import java.util.ArrayList;

/**
 * Created by Lai.OH on 2016-07-28.
 */
public class ChatAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ChatMessage> mChatList;
    private RbPreference mPref;
    public ChatAdapter(Context context, ArrayList<ChatMessage> chatList) {
        mContext = context;
        mChatList = chatList;
        mPref = new RbPreference(mContext);
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

        ChatMessage item = mChatList.get(position);
        View v;
        //user_num이 자기 자신일 경우 메세지 오른쪽 배치 layout
        if(mPref.getValue("user_num","").equals(String.valueOf(item.getSender_id()))){
            v = View.inflate(mContext, R.layout.custom_chatbubble_right, null);

            TextView msg = (TextView) v.findViewById(R.id.chat_message);
            TextView time = (TextView) v.findViewById(R.id.chat_time);

            msg.setText(item.getMessage());
            time.setText(item.getTime());

            msg.setBackgroundColor(Color.LTGRAY);
            msg.invalidate();

            return v;
        } else {
            //user_num이 상대방일 경우
            v = View.inflate(mContext, R.layout.custom_chatbubble_left, null);

            TextView title = (TextView) v.findViewById(R.id.chat_title);
            TextView msg = (TextView) v.findViewById(R.id.chat_message);
            TextView time = (TextView) v.findViewById(R.id.chat_time);

            title.setText(item.getSender_name());
            msg.setText(item.getMessage());
            time.setText(item.getTime());

            return v;
        }
    }
}
