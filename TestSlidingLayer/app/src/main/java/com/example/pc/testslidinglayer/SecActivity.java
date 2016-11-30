package com.example.pc.testslidinglayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HuangRuiShu on 2016/10/20.
 */
public class SecActivity extends AppCompatActivity {
    private static final String FORMAT = "^[a-z,A-Z].*$";

    private PinnedHeaderListView listView;
    // 首字母集
    private List<String> mSection;
    // 首字母位置集
    private List<Integer> mSectionPosition;
    // 首字母对应的位置
    private Map<String, Integer> mIndexer;
    // 根据首字母存放数据,key为首字母，value为具体数据
    private Map<String, List<String>> mMap;
    private List<String> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sec_layout);
        listView = (PinnedHeaderListView) findViewById(R.id.pinned_lv);
        initData();

    }

    private void initData() {
        mMap = new HashMap<>();
        mData = new ArrayList<>();
        mSection = new ArrayList<>();
        mSectionPosition = new ArrayList<>();
        mIndexer = new HashMap<>();
        String array[] = getResources().getStringArray(R.array.country);
        for (int i = 0; i < array.length; i++) {
            mData.add(array[i]);
            String firstName = array[i].substring(0, 1);
            if (firstName.matches(FORMAT)) {
                if (mSection.contains(firstName)) {
                    //不是该首字母下的第一个字符串
                    mMap.get(firstName).add(array[i]);
                } else {
                    //是该首字母下的第一个字符串
                    mSection.add(firstName);
                    List<String> list = new ArrayList<>();
                    list.add(array[i]);
                    mMap.put(firstName, list);
                }
            }
            else {
                if (mSection.contains("#")) {
                    mMap.get("#").add(array[i]);
                } else {
                    mSection.add("#");
                    List<String> list = new ArrayList<>();
                    list.add(array[i]);
                    mMap.put("#", list);
                }
            }
        }
        Collections.sort(mSection);
        int position = 0;
        for (int i = 0; i < mSection.size(); i++) {
            mIndexer.put(mSection.get(i), position);//存入map中，key为首字母字符串，value为首字母在listview中位置
            mSectionPosition.add(position);// 首字母在listview中位置，存入list中
            //mMap.get(mSection.get(i))返回第i个首字母里面的数据list
            position += mMap.get(mSection.get(i)).size();// 计算下一个首字母在listview的位置
        }
        SectionAdapter adapter = new SectionAdapter(this, mData, mSection, mSectionPosition);
        listView.setAdapter(adapter);
        listView.setPinnedHeaderView(LayoutInflater.from(this).inflate(
                R.layout.listview_head, listView, false));
        listView.setOnScrollListener(adapter);

    }
}
