package com.example.ethan.share01;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Created by Lai.OH on 2016-08-29.
 *
 * 쪽지보내는 Dialog를 생성하는 클래스
 *
 * MessageDialogUtil(param...)
 *
 * @param... context    (해당 위치의 context)
 * @param... recvName   (게시글을 올린 사람의 이름)
 * @param... recvSex    (게시글을 올린 사람의 성별)
 * @param... recvMsg    (게시글을 올린 사람에게 보낼 메세지)
 * @param... recvId     (게시글을 올린 사람의 id)
 *
 * ContentListAdapter 클래스 내부에서 해당 게시글 클릭 이벤트 발생시
 * 해당 게시글을 올린 user의 정보를 받아온다.
 * 보내기 버튼 클릭시 상대방에게 보낼 메세지를 보낸다.
 * 메세지를 보내는 쓰레드는 MessageSendUtil 클래스를 호출한다.
 *
 */
public class MessageDialogUtil extends Dialog{
    private Context mContext;
    private String mRecvName;
    private String mRecvSex;
    private String mRecvMsg;
    private int mRecvId;

    private RbPreference mPref;

    private final String SERVER_URL_SEND = "https://toycom96.iptime.org:1443/chat_send";

    private TextView dialog_title_tv;
    private TextView dialog_recvmsg_tv;
    private EditText dialog_message_edt;
    private Button dialog_send_btn;
    private Button dialog_cancel_btn;

    public MessageDialogUtil(Context context, String recvName, String recvSex, String recvMsg, int recvId) {
        super(context);
        this.mContext = context;
        this.mRecvName = recvName;
        this.mRecvSex = recvSex;
        this.mRecvMsg = recvMsg;
        this.mRecvId = recvId;

        mPref = new RbPreference(mContext);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_message_dialog);

        dialog_title_tv = (TextView) findViewById(R.id.dialog_title);
        dialog_recvmsg_tv = (TextView) findViewById(R.id.dialog_recvmsg);
        dialog_message_edt = (EditText) findViewById(R.id.dialog_message);
        dialog_send_btn = (Button) findViewById(R.id.dialog_send);
        dialog_cancel_btn = (Button) findViewById(R.id.dialog_cancel);

        dialog_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageSendUtil sendMsgTask = new MessageSendUtil(mContext);
                sendMsgTask.execute(SERVER_URL_SEND, String.valueOf(mRecvId), dialog_message_edt.getText().toString(), mPref.getValue("auth", ""));
                dismiss();
            }
        });

        dialog_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "보내기", Toast.LENGTH_SHORT);
                dismiss();
            }
        });

    }

    public void setTitle() {
        dialog_title_tv.setText(mRecvName + "(" + mRecvSex + ")");
        dialog_recvmsg_tv.setText( mRecvMsg);
    }

}
