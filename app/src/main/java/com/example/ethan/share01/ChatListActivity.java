package com.example.ethan.share01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.ethan.share01.adapter.ChattingRoomAdapter;
import com.example.ethan.share01.model.ChattingRoom;

import java.util.ArrayList;

public class ChatListActivity extends AppCompatActivity {

    private ChattingRoomAdapter mChatListAdapter;
    private ListView chatting_room_lv;
    private ArrayList<ChattingRoom> mChatRooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
    }

    private void init(){
        chatting_room_lv = (ListView) findViewById(R.id.chattingroom_listview);
        mChatRooms = new ArrayList<>();

        /*
         * 이부분에 서버에서 데이터를 가져오는 부분 구현
         */
        mChatListAdapter = new ChattingRoomAdapter(this, mChatRooms);


    }
}
