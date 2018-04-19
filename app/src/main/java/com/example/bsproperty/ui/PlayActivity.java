package com.example.bsproperty.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.bsproperty.R;
import com.example.bsproperty.adapter.BaseAdapter;
import com.example.bsproperty.utils.AudioRecoderUtils;
import com.example.bsproperty.utils.Player;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;

public class PlayActivity extends BaseActivity {

    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_right)
    Button btnRight;
    @BindView(R.id.sb_bar)
    SeekBar sbBar;
    @BindView(R.id.iv_cd)
    ImageView ivCd;
    @BindView(R.id.iv_v)
    ImageView ivV;
    @BindView(R.id.btn_play)
    Button btnPlay;
    @BindView(R.id.rv_list)
    RecyclerView rvList;

    private Player player;
    private int mProgress;
    private AudioRecoderUtils audioRecoderUtils;
    private boolean isStart;
    private MyAdapter adapter;
    private String mPath;

    @Override
    protected void initView(Bundle savedInstanceState) {
        tvTitle.setText("录制");
        btnRight.setText("完成");
        btnRight.setVisibility(View.VISIBLE);

        sbBar.setEnabled(false);

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
        final File file = (File) getIntent().getSerializableExtra("file");
        player = new Player(file.getAbsolutePath(), sbBar, new Player.OnPlayListener() {
            @Override
            public void onLoad(int duration) {
            }

            @Override
            public void onProgress(int position) {
            }

            @Override
            public void onCompletion() {
                sbBar.setProgress(0);
                audioRecoderUtils.stopRecord(true);
            }
        });

        audioRecoderUtils = new AudioRecoderUtils();
        audioRecoderUtils.setOnAudioStatusUpdateListener(new AudioRecoderUtils.OnAudioStatusUpdateListener() {

            @Override
            public void onUpdate(double db, long time) {
                int dbint = (int) ((int) db * 1.5);
                int v = dbint / 25;
                try {
                    if (dbint > 0 && v <= 1) {
                        ivV.getDrawable().setLevel(1);
                    } else {
                        ivV.getDrawable().setLevel(v);
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onStop(String filePath, long time) {
                mPath = filePath;
            }
        });

        rvList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        if (audioRecoderUtils != null) {
            audioRecoderUtils.stopRecord(false);
        }
    }

    @Override
    protected int getRootViewId() {
        return R.layout.activity_play;
    }

    @Override
    protected void loadData() {
        String[] word = getIntent().getStringExtra("word").split("\\n");
        ArrayList<String> list = new ArrayList<>(Arrays.asList(word));
        adapter = new MyAdapter(mContext, R.layout.item_word, list);
        rvList.setAdapter(adapter);
    }

    @OnClick({R.id.btn_back, R.id.btn_right, R.id.btn_play})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_right:
                if (isStart) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("提示")
                            .setMessage("是否立即完成录制？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    isStart = false;

                                    player.stop();
                                    player.release();
                                    player = null;

                                    audioRecoderUtils.stopRecord(true);

                                    Intent intent = new Intent(mContext, SubmitActivity.class);
                                    intent.putExtra("path", mPath);
                                    File file = (File) getIntent().getSerializableExtra("file");
                                    String name = file.getName();
                                    try {
                                        name = file.getName().split("\\.")[0];
                                    } catch (Exception e) {

                                    }
                                    intent.putExtra("back", file.getAbsolutePath());
                                    intent.putExtra("name", name);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                } else {
                    showToast("请先开始录制");
                }
                break;
            case R.id.btn_play:
                isStart = true;
                player.play(true);
                audioRecoderUtils.startRecord();
                btnPlay.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private class MyAdapter extends BaseAdapter<String> {

        public MyAdapter(Context context, int layoutId, ArrayList<String> data) {
            super(context, layoutId, data);
        }

        @Override
        public void initItemView(BaseViewHolder holder, String s, int position) {
            holder.setText(R.id.tv_word, s);
        }
    }
}
