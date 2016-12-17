package com.project0603.share00;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.project0603.share00.R;

public class ContentDetailActivity extends AppCompatActivity {

    private Button chat_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_detail);

        final int getUserid = getIntent().getIntExtra("user_num", -1);
        chat_btn = (Button) findViewById(R.id.detail_chat_button);
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getUserid != -1) {
                    Intent intent = new Intent(ContentDetailActivity.this, ChatActivity.class);
                    intent.putExtra("sender_id", getUserid);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ContentDetailActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
