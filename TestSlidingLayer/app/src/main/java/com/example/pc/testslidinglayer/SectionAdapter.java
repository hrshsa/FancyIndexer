package com.example.pc.testslidinglayer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * 必须实现OnScrollListener，否则滑动时头部错位
 */
public class SectionAdapter extends BaseAdapter implements PinnedHeaderListView.PinnedHeaderAdapter,
        SectionIndexer, AbsListView.OnScrollListener {

    private Context context;
    private List<String> mDatas;
    // 首字母集
    private List<String> mSections;
    //首字母所在的位置
    private List<Integer> mSectionPositions;
    private int mLocationPosition = -1;

    /**
     * @param context
     * @param datas             保存所有的名称 从a到z
     * @param mSections         首字母集合
     * @param mSectionPositions 首字母所在位置集
     */
    public SectionAdapter(Context context, List<String> datas, List<String> mSections, List<Integer> mSectionPositions) {
        this.context = context;
        this.mDatas = datas;
        this.mSections = mSections;
        this.mSectionPositions = mSectionPositions;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //计算当前的首字母
        int section = getSectionForPosition(position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_item, null);
            holder = new ViewHolder();
            holder.mHeadTv = (TextView) convertView
                    .findViewById(R.id.friends_item_header_text);
            holder.mHeadLayout = (LinearLayout) convertView
                    .findViewById(R.id.friends_item_header_parent);
            holder.mContentTv = (TextView) convertView
                    .findViewById(R.id.friends_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //判断当前位置是否为首字母所在的位置
        if (getPositionForSection(section) == position) {
            holder.mHeadLayout.setVisibility(View.VISIBLE);
            holder.mHeadTv.setText(mSections.get(section));
        } else {
            holder.mHeadLayout.setVisibility(View.GONE);
        }
        holder.mContentTv.setText(mDatas.get(position));
        return convertView;
    }

    class ViewHolder {
        TextView mHeadTv;
        TextView mContentTv;
        LinearLayout mHeadLayout;
    }

    @Override
    public int getPinnedHeaderState(int position) {
        int realPosition = position;
        if (realPosition < 0
                || (mLocationPosition != -1 && mLocationPosition == realPosition)) {
            return PINNED_HEADER_GONE;
        }
        mLocationPosition = -1;
        //当前的首字母的index
        int section = getSectionForPosition(realPosition);
        //下一个首字母的位置
        int nextSectionPosition = getPositionForSection(section + 1);
        if (nextSectionPosition != -1
                && realPosition == nextSectionPosition - 1) {
            return PINNED_HEADER_PUSHED_UP;
        }
        return PINNED_HEADER_VISIBLE;
    }

    @Override
    public void configurePinnedHeader(View header, int position, int alpha) {
        if (mDatas != null && mDatas.size() > 0) {
            int realPosition = position;
            int section = getSectionForPosition(realPosition);
            String title = (String) getSections()[section];
            Log.e("configurePinnedHeader: ", title + "  section: " + section);
            ((TextView) header.findViewById(R.id.friends_list_header_text))
                    .setText(title);
        }
    }

    @Override
    public Object[] getSections() {
        return mSections.toArray();
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        if (sectionIndex < 0 || sectionIndex > mSectionPositions.size())
            return -1;
        return mSectionPositions.get(sectionIndex);
    }

    /**
     *  重写该方法根据位置计算对应的section
     *  返回的是0，1，2这些数字
     */
    @Override
    public int getSectionForPosition(int position) {
        if (position < 0 || position >= getCount())
            return -1;
        if (position == 0)
            return 0;
        //二分查找之前必须先对元素排序，否则结果不确定
        //如果找到了目标，Arrays.binarySearch() 如果搜索结果在数组中，则返回它在数组中的索引，
        // 如果不在，则返回第一个比它大的索引的负数-1.
        int index = Arrays.binarySearch(mSectionPositions.toArray(), position);
        return index > 0 ? index : -index - 2;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view instanceof PinnedHeaderListView) {
            ((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);
        }
    }
}
