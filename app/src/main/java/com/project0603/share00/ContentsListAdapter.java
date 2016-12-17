package com.project0603.share00;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project0603.share00.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ethan on 16. 6. 16..
 */
public class ContentsListAdapter  extends RecyclerView.Adapter<ContentsListAdapter.ViewHolder> {
    private List<ContentsListObject> ContentsList;
    private Context mContext;
    static int picWidth = 470;
    private String photo_path;

    public ContentsListAdapter(Context context, List<ContentsListObject> ContentItem) {
        this.mContext = context;
        this.ContentsList = ContentItem;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        picWidth = (int)((float)dm.widthPixels / 2.0 * 0.97);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(ContentsListAdapter.ViewHolder holder, final int position) {
        int unix_sec = 0;
        String getTime;

        //Picasso.with(mContext).load(ContentsList.get(position).getPicUrl()).resize(476,0).into(holder.Pic);
        photo_path = ContentsList.get(position).getPicUrl();
        if (photo_path != null && !photo_path.equals("")) {
            try {
                Picasso.with(mContext).load(photo_path).resize(picWidth, 0).error(R.drawable.ic_menu_noprofile).into(holder.Photo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            holder.Photo.setImageResource(R.drawable.ic_menu_noprofile);
        }
        //Picasso.with(mContext).load(ContentsList.get(position).getPicUrl()).resize(picWidth, 0).into(holder.Photo);
        Toast.makeText(mContext, ContentsList.get(position).getPicUrl(), 1);
        //Picasso.with(mContext).load(ContentsList.get(position).getPicUrl()).into(holder.Pic);

        if (ContentsList.get(position).getTitle().equals("")) {
            holder.Msg.setText(ContentsList.get(position).getMsg());
        } else {
            holder.Msg.setText(ContentsList.get(position).getTitle());
        }
        holder.ContentId = ContentsList.get(position).getId();
        holder.UserId = ContentsList.get(position).getUserId();
        unix_sec = ContentsList.get(position).getTime();

        if ( unix_sec > (6 * 30 * 24 * 60 * 60) ) { getTime = "반년이상"; }
        else if ( unix_sec > ( 30 * 24 * 60 * 60) ) { getTime = (unix_sec / ( 30 * 24 * 60 * 60)) + "개월전"; }
        else if ( unix_sec > ( 24 * 60 * 60) ) { getTime = (unix_sec / ( 24 * 60 * 60)) + "일전"; }
        else if ( unix_sec > ( 60 * 60) ) { getTime = (unix_sec / ( 60 * 60)) + "시간전"; }
        else if ( unix_sec > ( 60) ) { getTime = (unix_sec / (60)) + "분전"; }
        else { getTime = unix_sec + "초전"; }
        //getTime = order.get("Sended").toString();


        if (ContentsList.get(position).getSex().equals("M")) {
            holder.Basic.setText(getTime + " / " + ContentsList.get(position).getUser() + " (남, " + String.valueOf(ContentsList.get(position).getAge()) + "세)");
        } else {
            holder.Basic.setText(getTime + " / " + ContentsList.get(position).getUser() + " (여, " + String.valueOf(ContentsList.get(position).getAge()) + "세)");
        }
        holder.Dist.setText(String.valueOf(ContentsList.get(position).getDist()) + " km");

        holder.Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PictureDetailViewActivity.class);
                intent.putExtra("photo_path", ContentsList.get(position).getPicUrl());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public ContentsListAdapter.ViewHolder onCreateViewHolder(ViewGroup vGroup, int i) {

        View view = LayoutInflater.from(vGroup.getContext()).inflate(R.layout.content_main, vGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return ContentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView Photo;
        private TextView Basic;
        private TextView Dist;
        private TextView Msg;
        private int ContentId;
        private int UserId;

        public ViewHolder(View ContentView) {
            super(ContentView);

            ContentView.setOnClickListener(this);
            Photo = (ImageView) ContentView.findViewById(R.id.bbslist_photo);
            Basic = (TextView) ContentView.findViewById(R.id.bbslist_basic);
            Dist = (TextView) ContentView.findViewById(R.id.bbslist_dist);
            Msg = (TextView) ContentView.findViewById(R.id.bbslist_msg);
            ContentId = 0;
            UserId = 0;

            /*Photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //여기에 사진 키우는거 처리



                }
            });*/
        }

        @Override
        public void onClick(View ContentView)
        {
            /*//클릭했을 때의 해당 화면의 context를 받아온다.
            final Context context = ContentView.getContext();

            //쪽지 보내기 Dialog 생성, 상대방의 정보를 같이 보낸다.
            //쪽지를 보내는 버튼에 대한 이벤트는 MessageDialogUtil 클래스 내부에 구현되어 있다.
            final MessageDialogUtil messageUtil =
                    new MessageDialogUtil(context, ViewHolder.this.User.getText().toString(), ViewHolder.this.Etc.getText().toString(), ViewHolder.this.Msg.getText().toString(), ViewHolder.this.UserId);

            //쪽지 보내기 Dialog가 화면에 보여졌을 때의 기본 셋팅
            messageUtil.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    messageUtil.setTitle();
                }
            });

            messageUtil.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    return;
                }
            });

            messageUtil.show();*/

            Intent intent = new Intent(mContext, BbsDetailActivity.class);
            intent.putExtra("bbs_id", ViewHolder.this.ContentId);
            mContext.startActivity(intent);

        }
    }
}