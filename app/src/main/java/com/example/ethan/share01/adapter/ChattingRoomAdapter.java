package com.example.ethan.share01.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ethan.share01.R;
import com.example.ethan.share01.model.ChattingRoom;

import org.w3c.dom.Text;

import java.util.ArrayList;

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

        ChattingRoom item = mChatRoomList.get(position);

        title.setText(item.getRecv_name());
        msg.setText(item.getMsg());
        time.setText(item.getSended());

        return v;
    }
}
