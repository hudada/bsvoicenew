package com.example.bsproperty.net;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.example.bsproperty.bean.BaseResponse;
import com.example.bsproperty.ui.BaseActivity;
import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Yezi521 on 2017/11/10.
 */

public abstract class BaseCallBack<T> extends Callback<BaseResponse> {

    private Context mContext;
    private Class mClass;

    public BaseCallBack(Context context, Class mClass) {
        mContext = context;
        this.mClass = mClass;
    }

    @Override
    public BaseResponse parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();
        Log.e("okhttp", "json: " + string);
        return (BaseResponse) new Gson().fromJson(string, mClass);
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        if (mContext instanceof BaseActivity) {
            ((BaseActivity) mContext).dismissDialog();
        }
        if (e == null) {
            ((BaseActivity) mContext).showToast(mContext, "服务异常");
        } else {
            String msg = e.getMessage();
            if (TextUtils.isEmpty(msg)) {
                ((BaseActivity) mContext).showToast(mContext, "服务异常");
            } else {
                ((BaseActivity) mContext).showToast(mContext, msg);
            }
        }
    }

    @Override
    public void onResponse(BaseResponse response, int id) {
        if (mContext instanceof BaseActivity) {
            ((BaseActivity) mContext).dismissDialog();
        }
        if (response != null) {
            if (response.getCode() == 0) {
                onResponse((T) response);
            } else {
                if (TextUtils.isEmpty(response.getMessage())) {
                    ((BaseActivity) mContext).showToast(mContext, "网络异常，请稍候再试");
                } else {
                    ((BaseActivity) mContext).showToast(mContext, response.getMessage());
                }
            }
        } else {
            ((BaseActivity) mContext).showToast(mContext, "网络异常，请稍候再试");
        }
    }

    public abstract void onResponse(T t);
}
