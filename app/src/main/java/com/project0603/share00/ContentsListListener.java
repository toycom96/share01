package com.project0603.share00;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by ethan on 16. 6. 16..
 */
public class ContentsListListener extends RecyclerView.OnScrollListener {
    StaggeredGridLayoutManager mLayoutManager;
    public int lastVisibleItemPosition = 0;        //화면에 리스트의 마지막 아이템이 보여지는지 체크
    public int visibleThreshold = 0;
    public View view;
    private Context mContext = null;
    private RecyclerView mRecyclerView;
    MainActivity mActivity;
    //private ContentsListObject mContentsList;

    public ContentsListListener(MainActivity activity, StaggeredGridLayoutManager layoutManager, RecyclerView recyclerView, Context context) {
        this.mActivity = activity;
        this.mLayoutManager = layoutManager;
        this.mContext = context;
        this.mRecyclerView = recyclerView;
        visibleThreshold = visibleThreshold * mLayoutManager.getSpanCount();
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        if (GlobalVar.loading_flag == 1) {
            return;
        }
        int visibleItemCount = view.getChildCount();
        int totalItemCount = view.getLayoutManager().getItemCount();
        int[] firstVisibleItem = ((StaggeredGridLayoutManager) view.getLayoutManager()).findFirstVisibleItemPositions(null);

        //if (totalItemCount != 0 && lastVisibleItemPositions[0] + 2 >= totalItemCount) {
        if ( totalItemCount > 0 && (visibleItemCount + firstVisibleItem[0]) >= totalItemCount - 5) {
            int offset = this.mActivity.mContentsList.get(totalItemCount -1).getId();

            this.mActivity.mContentsLoader.loadFromApi(offset, GlobalVar.dist, GlobalVar.cate1, Profile.auth);
        }

    }
}
