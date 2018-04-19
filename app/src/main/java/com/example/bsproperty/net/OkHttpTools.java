package com.example.bsproperty.net;

import android.content.Context;

import com.example.bsproperty.ui.BaseActivity;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.OtherRequestBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.builder.PostStringBuilder;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by yezi on 2018/1/27.
 */

public class OkHttpTools {
    public static GetBuilder sendGet(Context context, String url) {
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).showProgress(context);
        }
        return OkHttpUtils
                .get()
                .url(url);
    }

    public static GetBuilder sendGet(Context context, String url, boolean b) {
        if (b) {
            if (context instanceof BaseActivity) {
                ((BaseActivity) context).showProgress(context);
            }
        }
        return OkHttpUtils
                .get()
                .url(url);
    }

    public static OtherRequestBuilder sendPut(Context context, String url) {
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).showProgress(context);
        }
        return OkHttpUtils.put()
                .requestBody(RequestBody.create(null, ""))
                .url(url);
    }

    public static PostStringBuilder postJson(Context context, String url, Object bean) {
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).showProgress(context);
        }
        return OkHttpUtils
                .postString()
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .url(url)
                .content(new Gson().toJson(bean));
    }

    public static PostFormBuilder sendPost(Context context, String url) {

            if (context instanceof BaseActivity) {
                ((BaseActivity) context).showProgress(context);
            }

        return OkHttpUtils
                .post()
                .url(url);
    }

    public static PostFormBuilder postFile(Context context, String url, String key, File file){
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).showProgress(context);
        }
        return OkHttpUtils.post()
                .addFile(key, file.getName(), file)
                .url(url);
    }
}
