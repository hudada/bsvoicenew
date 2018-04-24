package com.example.bsproperty.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.bsproperty.MyApplication;
import com.example.bsproperty.R;
import com.example.bsproperty.utils.SpUtils;

public class LoadingActivity extends BaseActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Override
    protected int getRootViewId() {
        return R.layout.activity_loading;
    }

    @Override
    protected void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (SpUtils.getUserBean(mContext) != null) {
                    MyApplication.getInstance().setUserBean(SpUtils.getUserBean(mContext));
                    startActivity(new Intent(mContext, UserMainActivity.class));
                } else {
                    startActivity(new Intent(mContext, LoginActivity.class));
                }
                finish();
            }
        }).start();
    }
}
