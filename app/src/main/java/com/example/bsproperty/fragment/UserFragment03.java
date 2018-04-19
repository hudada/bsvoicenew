package com.example.bsproperty.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bsproperty.MyApplication;
import com.example.bsproperty.R;
import com.example.bsproperty.ui.MySongActivity;
import com.example.bsproperty.ui.SongDetailActivity;
import com.example.bsproperty.utils.SpUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by wdxc1 on 2018/3/21.
 */

public class UserFragment03 extends BaseFragment {
    @BindView(R.id.ibtn_head)
    ImageButton ibtnHead;
    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_right)
    Button btnRight;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.rl_my_song)
    RelativeLayout rl_my_song;
    @BindView(R.id.btn_out)
    Button btnOut;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void loadData() {
        tvName.setText(MyApplication.getInstance().getUserBean().getUserName());
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        tvTitle.setText("我的");
        btnBack.setVisibility(View.GONE);
    }

    @Override
    public int getRootViewId() {
        return R.layout.fragment_user03;
    }

    @OnClick({R.id.btn_right, R.id.btn_out, R.id.rl_my_song})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_right:
                break;
            case R.id.btn_out:
                if (SpUtils.cleanUserBean(mContext)) {
                    System.exit(0);
                }
                break;
            case R.id.rl_my_song:
                // 我的作品
                Intent intent = new Intent(mContext, MySongActivity.class);
                startActivity(intent);
                break;
        }
    }
}
