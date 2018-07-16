package com.wc.oftenutils;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wc.utils.DisplayUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayUtil.init(this);
        setContentView(R.layout.activity_main);
    }
}
