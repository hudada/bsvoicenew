package com.example.bsproperty.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bsproperty.MyApplication;
import com.example.bsproperty.R;
import com.example.bsproperty.bean.UserObjBean;
import com.example.bsproperty.net.ApiManager;
import com.example.bsproperty.net.BaseCallBack;
import com.example.bsproperty.net.OkHttpTools;
import com.example.bsproperty.utils.SpUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_user)
    EditText etUser;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.btn_0)
    Button btn0;
    @BindView(R.id.btn_1)
    Button btn1;

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Override
    protected int getRootViewId() {
        return R.layout.activity_login;
    }

    @Override
    protected void loadData() {
    }


    @OnClick({R.id.btn_0, R.id.btn_1})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.btn_0:
                String user = etUser.getText().toString().trim();
                String pwd = etPwd.getText().toString().trim();
                if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pwd)) {
                    showToast("请输入完整信息");
                    return;
                }

                OkHttpTools.sendPost(mContext, ApiManager.LOGIN)
                        .addParams("name", user)
                        .addParams("pwd", pwd)
                        .build()
                        .execute(new BaseCallBack<UserObjBean>(mContext, UserObjBean.class) {
                            @Override
                            public void onResponse(UserObjBean userObjBean) {
                                SpUtils.setUserBean(mContext, userObjBean.getData());
                                MyApplication.getInstance().setUserBean(userObjBean.getData());
                                        startActivity(new Intent(mContext, UserMainActivity.class));
                                        finish();
                            }
                        });
                break;
            case R.id.btn_1:
                jumpAct(RgActivity.class);
                break;
        }
    }
}
