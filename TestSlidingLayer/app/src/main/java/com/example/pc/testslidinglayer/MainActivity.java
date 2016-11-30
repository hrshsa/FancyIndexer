package com.example.pc.testslidinglayer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private SlidingLayerManager manager;
    private FancyIndexer indexer;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = new SlidingLayerManager(this);
        indexer = (FancyIndexer) findViewById(R.id.fancy_indexer);
//        manager.setSlidingView(getLayoutInflater().inflate(R.layout.item, null), 400
//        );
        fragment = new MyFragment();
        manager.setSlidingView(fragment, getSupportFragmentManager());
//        manager.openLayer();
        manager.setOnInteractListener(new SlidingLayer.OnInteractListener() {
            @Override
            public void onOpen() {

            }

            @Override
            public void onClose() {

            }

            @Override
            public void onOpened() {

            }

            @Override
            public void onClosed() {

            }
        });

        indexer.setOnTouchLetterChangedListener(new FancyIndexer.OnTouchLetterChangedListener() {
            @Override
            public void onTouchLetterChanged(String s, int index) {
                Log.e("FancyIndexer: ", "String :" + s + " index: " + index);
            }

            @Override
            public void onTouchActionUp(String s) {
                Log.e("onTouchActionUp: ", " String: " + s);
            }
        });
    }

    public void click(View view) {
        manager.openLayer();
    }
    public void clicks(View view) {
        startActivity(new Intent(this,SecActivity.class));
    }
}
