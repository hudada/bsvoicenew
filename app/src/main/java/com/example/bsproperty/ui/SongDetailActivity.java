package com.example.bsproperty.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.bsproperty.MyApplication;
import com.example.bsproperty.R;
import com.example.bsproperty.adapter.BaseAdapter;
import com.example.bsproperty.bean.BaseResponse;
import com.example.bsproperty.bean.LikeObjBean;
import com.example.bsproperty.bean.ReplyBean;
import com.example.bsproperty.bean.SongBean;
import com.example.bsproperty.net.ApiManager;
import com.example.bsproperty.net.BaseCallBack;
import com.example.bsproperty.net.OkHttpTools;
import com.example.bsproperty.utils.Player;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SongDetailActivity extends BaseActivity {


    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_right)
    Button btnRight;
    @BindView(R.id.tv_pro)
    TextView tvPro;
    @BindView(R.id.sb_bar)
    SeekBar sbBar;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.btn_play)
    ImageButton btnPlay;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.rg_list)
    RadioGroup rgList;

    private Player player;
    private int mProgress;

    private String mPath;
    private boolean isPlay;
    private int mType;
    private long mDuration;

    @Override
    protected void initView(Bundle savedInstanceState) {

        sbBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mProgress = progress * player.mediaPlayer.getDuration()
                        / seekBar.getMax();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.mediaPlayer.seekTo(mProgress);
            }
        });

        rgList.check(rgList.getChildAt(0).getId());
        mType = 1;
        rgList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (findViewById(checkedId).getTag().equals(1)) {
                    mType = 1;
                } else {
                    mType = 0;
                }
            }
        });

        btnRight.setVisibility(View.VISIBLE);
        btnRight.setText("发布");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    @Override
    protected int getRootViewId() {
        return R.layout.activity_song_detail;
    }

    @Override
    protected void loadData() {
        mPath = getIntent().getStringExtra("path");
        tvTitle.setText("新的声音");
        player = new Player(mPath, sbBar, new Player.OnPlayListener() {
            @Override
            public void onLoad(int duration) {
                mDuration = duration;
                tvTotal.setText(MyApplication.formatTime.format(duration));
            }

            @Override
            public void onProgress(int position) {
                tvPro.setText(MyApplication.formatTime.format(position));
            }

            @Override
            public void onCompletion() {
                tvPro.setText("00:00");
                sbBar.setProgress(0);
                btnPlay.setImageResource(R.drawable.ic_play_circle_outline_white_24dp);
            }
        });
    }


    @OnClick({R.id.btn_back, R.id.btn_right, R.id.btn_play})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_right:
                String name = etName.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    showToast("请输入声音名称");
                    return;
                }
                OkHttpTools.postFile(mContext, ApiManager.SONG_ADD, "file", new File(mPath))
                        .addParams("uid", MyApplication.getInstance().getUserBean().getId() + "")
                        .addParams("name", name)
                        .addParams("length", mDuration + "")
                        .addParams("type", mType + "")
                        .build()
                        .execute(new BaseCallBack<BaseResponse>(mContext, BaseResponse.class) {
                            @Override
                            public void onResponse(BaseResponse baseResponse) {
                                showToast("发布成功");
                                finish();
                            }
                        });
                break;
            case R.id.btn_play:
                if (isPlay) {
                    isPlay = false;
                    player.pause();
                    btnPlay.setImageResource(R.drawable.ic_play_circle_outline_white_24dp);
                } else {
                    isPlay = true;
                    player.play(true);
                    btnPlay.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
                }
                break;
        }
    }

}
