package com.wc.oftenutils;


import com.wc.utils.ToastUtils;

public class MainActivity extends BaseActivity {

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        setStateBarColor();
        ToastUtils.showToast(this, "页面启动成功");
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_main;
    }
}
