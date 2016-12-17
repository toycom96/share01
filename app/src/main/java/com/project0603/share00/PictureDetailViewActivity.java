package com.project0603.share00;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.project0603.share00.R;
import com.squareup.picasso.Picasso;

public class PictureDetailViewActivity extends AppCompatActivity {

    private ImageView mImageView;
    private ImageButton mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_detail_view);
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);*/

        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        int picWidth = (int)((float)dm.widthPixels / 2.0 * 0.97);

        mImageView = (ImageView) findViewById(R.id.picture_detail_image);
        mCancelButton = (ImageButton) findViewById(R.id.picture_detail_cancel);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String photo_path = getIntent().getStringExtra("photo_path");

        if (photo_path != null && !photo_path.equals("")) {
            try {
                //Picasso.with(this).load(photo_path).fit().into(mImageView);
                Picasso.with(this).load(photo_path).error(R.drawable.ic_menu_noprofile).into(mImageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else{
            mImageView.setImageResource(R.drawable.ic_menu_noprofile);
        }


    }
}
