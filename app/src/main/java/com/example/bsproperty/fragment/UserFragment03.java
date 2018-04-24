package com.example.bsproperty.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.bumptech.glide.Glide;
import com.example.bsproperty.MyApplication;
import com.example.bsproperty.R;
import com.example.bsproperty.bean.UserObjBean;
import com.example.bsproperty.net.ApiManager;
import com.example.bsproperty.net.BaseCallBack;
import com.example.bsproperty.net.OkHttpTools;
import com.example.bsproperty.ui.MySongActivity;
import com.example.bsproperty.utils.LQRPhotoSelectUtils;
import com.example.bsproperty.utils.SpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

/**
 * Created by wdxc1 on 2018/3/21.
 */

public class UserFragment03 extends BaseFragment {
    @BindView(R.id.ibtn_head)
    ImageView ibtnHead;
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

    private EventManager asr;
    private String mStr;

    private LQRPhotoSelectUtils mLqrPhotoSelectUtils;
    private Bitmap selectBitmap;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void loadData() {
        tvName.setText(MyApplication.getInstance().getUserBean().getUserName());
        String head = MyApplication.getInstance().getUserBean().getImg();
        if (!TextUtils.isEmpty(head)) {
            Glide.with(mContext).load(ApiManager.HEAD_PATH +
                    head).into(ibtnHead);
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        tvTitle.setText("我的");
        btnBack.setVisibility(View.GONE);

        asr = EventManagerFactory.create(mContext, "asr");

        asr.registerListener(new EventListener() {
            @Override
            public void onEvent(String s, String s1, byte[] bytes, int i, int i1) {
                if (s.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
                    showToast("请说出命令");
                }
                if (s.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
                    if (TextUtils.isEmpty(mStr)) {
                        showToast("未知命令");
                        return;
                    }
                    if (mStr.equals("拍照")) {
                        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                        builder.setItems(new String[]{
                                "拍照选择", "本地相册选择", "取消"
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        mLqrPhotoSelectUtils.takePhoto();
                                        break;
                                    case 1:
                                        mLqrPhotoSelectUtils.selectPhoto();
                                        break;
                                    case 2:
                                        break;
                                }
                            }
                        }).show();
                    } else {
                        showToast("未知命令");
                    }
                }
                if (s.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
                    try {
                        JSONObject jsonObject = new JSONObject(s1);
                        JSONArray array = jsonObject.getJSONArray("results_recognition");
                        mStr = array.get(0).toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        ibtnHead.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        String json = "{\"accept-audio-data\":true," +
                                "\"disable-punctuation\":false," +
                                "\"accept-audio-volume\":true,\"pid\":1536}";
                        asr.send(SpeechConstant.ASR_START, json, null, 0, 0);
                        break;
                    case MotionEvent.ACTION_UP:
                        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
                        break;
                }
                return true;
            }
        });

        mLqrPhotoSelectUtils = new LQRPhotoSelectUtils((Activity) mContext, new LQRPhotoSelectUtils.PhotoSelectListener() {
            @Override
            public void onFinish(File outputFile, Uri outputUri) {
                selectBitmap = BitmapFactory.decodeFile(outputFile.getAbsolutePath());
                OkHttpTools.postFile(mContext, ApiManager.HEAD_ADD, "file",
                        outputFile)
                        .addParams("uid", MyApplication.getInstance().getUserBean().getId() + "")
                        .build()
                        .execute(new BaseCallBack<UserObjBean>(mContext, UserObjBean.class) {
                            @Override
                            public void onResponse(UserObjBean userObjBean) {
                                ibtnHead.setImageBitmap(selectBitmap);
                                MyApplication.getInstance().setUserBean(userObjBean.getData());
                                SpUtils.setUserBean(mContext, userObjBean.getData());
                            }
                        });


            }
        }, false);
    }

    public interface OnPhoto {
        void onPhoto();
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLqrPhotoSelectUtils.attachToActivityForResult(requestCode, resultCode, data);
    }
}
