package com.example.ethan.share01;

import java.util.List;

/**
 * Created by ethan on 16. 6. 17..
 */
public class ContentsListLoad {

    private List<ContentsListObject> mContentItem;
    public ContentsListAdapter mAdapter;

    public ContentsListLoad (List<ContentsListObject> ContentItem, ContentsListAdapter ListAdapter) {
        this.mContentItem = ContentItem;
        this.mAdapter = ListAdapter;
    }

    public int LoadFromApi(int ListIndex, int flag) {
        int id = ListIndex + 1;

        this.mContentItem.add(new ContentsListObject(id, "https://i.ytimg.com/vi/OMB4TS6sCYo/hqdefault.jpg", "George Orwell"));
        this.mContentItem.add(new ContentsListObject(id+1, "http://i.kinja-img.com/gawker-media/image/upload/dqhwzcryunyv6tznrqjy.jpg", "George Orwell"));
        this.mContentItem.add(new ContentsListObject(id+2, "http://cars.mclaren.com/files/live/sites/mclaren/files/cars-mclaren-com-Main/McLaren%20Model%20Section/650S%20Coupe/Design/650S_Hero_Design_Height4.jpg?t=w1440", "George Orwell"));
        this.mContentItem.add(new ContentsListObject(id+3, "http://cars.mclaren.com/files/live/sites/mclaren/files/cars-mclaren-com-Main/McLaren%20Model%20Section/GT%20Sprint%20650S/McLaren_650SGTSprint_front3q_2f-Edit.jpg", "George Orwell"));
        this.mContentItem.add(new ContentsListObject(id+4, "http://images.cdn.autocar.co.uk/sites/autocar.co.uk/files/styles/gallery_slide/public/mclaren-650s-rt-325_0.jpg", "George Orwell"));
        this.mContentItem.add(new ContentsListObject(id+5, "http://www.telegraph.co.uk/cars/images/2015/12/30/McLaren-650S-front-large_trans++_emuMSIF920-7wOLsuHQL5-4UITJmy5HzCeWHAP_eZw.jpg", "George Orwell"));
        this.mContentItem.add(new ContentsListObject(id+6, "http://ag-spots-2014.o.auroraobjects.eu/2014/06/23/mclaren-650s-c489723062014153820_2.jpg", "George Orwell"));
        this.mContentItem.add(new ContentsListObject(id+7, "http://cdn9.themanual.com/wp-content/uploads/2014/04/2015-McLaren-650S-22.jpg", "George Orwell"));
        this.mContentItem.add(new ContentsListObject(id+8, "http://pullzone1.motoringresearch.netdna-cdn.com/wp-content/uploads/2014/03/McLaren-650S-orange.jpg", "George Orwell"));
        this.mContentItem.add(new ContentsListObject(id+9, "http://bitcast-r.v1.sjc1.bitgravity.com/wapitltd/images2/550000/9000/900/559912.jpg", "George Orwell"));

        int curSize = mAdapter.getItemCount();
        mAdapter.notifyItemRangeInserted(curSize, mContentItem.size() - 1);

        return 0;
    }
}
