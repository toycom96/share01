package com.example.ethan.share01;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.ethan.share01.adapter.ChattingRoomAdapter;
import com.example.ethan.share01.model.ChattingRoom;

import java.util.ArrayList;

/**
 * Created by Lai.OH on 2016-07-27.
 */

public class ChatListActivity extends AppCompatActivity {

    private ChattingRoomAdapter mChatListAdapter;
    private ListView chatting_room_lv;
    private ArrayList<ChattingRoom> mChatRooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        init();
    }

    private void init(){
        chatting_room_lv = (ListView) findViewById(R.id.chattingroom_listview);
        mChatRooms = new ArrayList<>();

        /*
         * 이부분에 서버에서 데이터를 가져오는 부분 구현
         */

        mChatRooms.add(new ChattingRoom("!","2", "안녕하세요~", "2016-07-28 00:32:44"));
        mChatRooms.add(new ChattingRoom("!","3", "다 팔렸나요??~", "2016-07-25 00:32:44"));
        mChatRooms.add(new ChattingRoom("!","2", "안녕하세요~", "2016-07-28 00:32:44"));
        mChatRooms.add(new ChattingRoom("!","3", "다 팔렸나요??~", "2016-07-25 00:32:44"));
        mChatRooms.add(new ChattingRoom("!","2", "안녕하세요~", "2016-07-28 00:32:44"));
        mChatRooms.add(new ChattingRoom("!","3", "다 팔렸나요??~", "2016-07-25 00:32:44"));
        mChatRooms.add(new ChattingRoom("!","2", "안녕하세요~", "2016-07-28 00:32:44"));
        mChatRooms.add(new ChattingRoom("!","3", "다 팔렸나요??~", "2016-07-25 00:32:44"));
        mChatRooms.add(new ChattingRoom("!","2", "안녕하세요~", "2016-07-28 00:32:44"));
        mChatRooms.add(new ChattingRoom("!","3", "다 팔렸나요??~", "2016-07-25 00:32:44"));

        mChatListAdapter = new ChattingRoomAdapter(this, mChatRooms);

        chatting_room_lv.setAdapter(mChatListAdapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ChatListActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
