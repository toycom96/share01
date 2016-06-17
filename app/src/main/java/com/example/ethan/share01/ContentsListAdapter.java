package com.example.ethan.share01;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ethan on 16. 6. 16..
 */
public class ContentsListAdapter  extends RecyclerView.Adapter<ContentsListAdapter.ViewHolder> {
    private List<ContentsListObject> ContentsList;
    private Context mContext;

    public ContentsListAdapter(Context context, List<ContentsListObject> ContentItem) {
        this.mContext = context;
        this.ContentsList = ContentItem;
    }

    @Override
    public void onBindViewHolder(ContentsListAdapter.ViewHolder holder, int position) {
        Picasso.with(mContext).load(ContentsList.get(position).getPicUrl()).resize(476,0).into(holder.Pic);
        Toast.makeText(mContext, ContentsList.get(position).getPicUrl(), 1);
        //Picasso.with(mContext).load(ContentsList.get(position).getPicUrl()).into(holder.Pic);
        holder.Title.setText(ContentsList.get(position).getTitle());
        holder.ContentId = ContentsList.get(position).getId();
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
        private ImageView Pic;
        private TextView Title;
        private int ContentId;

        public ViewHolder(View ContentView) {
            super(ContentView);

            ContentView.setOnClickListener(this);
            Pic = (ImageView) ContentView.findViewById(R.id.ContentPic);
            Title = (TextView) ContentView.findViewById(R.id.ContentTitle);
            ContentId = 0;
        }

        @Override
        public void onClick(View ContentView)
        {
            Toast.makeText(ContentView.getContext(),
                    "Clicked ContentId = " + ContentId, Toast.LENGTH_SHORT)
                    .show();
        }
    }

}