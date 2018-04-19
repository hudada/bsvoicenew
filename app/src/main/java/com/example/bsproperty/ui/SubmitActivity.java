package com.example.bsproperty.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.bsproperty.MyApplication;
import com.example.bsproperty.R;
import com.example.bsproperty.bean.BaseResponse;
import com.example.bsproperty.net.ApiManager;
import com.example.bsproperty.net.BaseCallBack;
import com.example.bsproperty.net.OkHttpTools;
import com.example.bsproperty.utils.Player;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SubmitActivity extends BaseActivity {

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
    Button btnPlay;

    private Player player;
    private Player player1;
    private int mProgress;
    private String mPath;
    private String mBack;
    private MediaPlayer mediaPlayer;
    private int length;
    private Handler handler = new Handler();

    @Override
    protected void initView(Bundle savedInstanceState) {
        tvTitle.setText("发布");
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

        mPath = getIntent().getStringExtra("path");
        mBack = getIntent().getStringExtra("back");
        try {
            player = new Player(mPath, null, new Player.OnPlayListener() {
                @Override
                public void onLoad(int duration) {

                }

                @Override
                public void onProgress(int position) {

                }

                @Override
                public void onCompletion() {

                }
            });
            player1 = new Player(mBack, sbBar, new Player.OnPlayListener() {
                @Override
                public void onLoad(int duration) {
                    length = duration;
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
                }
            });
            player.play(true);
            player1.play(false);
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    player1.play();
//                }
//            }, 100);


        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaPlayer = new MediaPlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }

        if (player1 != null) {
            player1.stop();
            player1.release();
            player1 = null;
        }
    }

    @Override
    protected int getRootViewId() {
        return R.layout.activity_submit;
    }

    @Override
    protected void loadData() {

    }

    @OnClick({R.id.btn_back, R.id.btn_play})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("提示")
                        .setMessage("是否放弃本次录制？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File file = new File(mPath);
                                file.delete();
                                finish();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.btn_play:
                showProgress(mContext);
                File file = new File(mPath);
                File back = new File(mBack);
                OkHttpUtils.post()
                        .addFile("file", file.getName(), file)
                        .addFile("file1", back.getName(), back)
                        .url(ApiManager.SONG_ADD)
                        .addParams("uid", MyApplication.getInstance().getUserBean().getId() + "")
                        .addParams("name", getIntent().getStringExtra("name"))
                        .addParams("length", length + "")
                        .build()
                        .execute(new BaseCallBack<BaseResponse>(mContext, BaseResponse.class) {
                            @Override
                            public void onResponse(BaseResponse baseResponse) {
                                startActivity(new Intent(mContext, UserMainActivity.class));
                                finish();
                            }
                        });
                break;
        }
    }

    public void uniteAMRFile(String[] partsPaths, String unitedFilePath) {
        try {
            File unitedFile = new File(unitedFilePath);
            FileOutputStream fos = new FileOutputStream(unitedFile);
            RandomAccessFile ra = null;
            for (int i = 0; i < partsPaths.length; i++) {
                ra = new RandomAccessFile(partsPaths[i], "r");
                if (i != 0) {
                    ra.seek(6);
                }
                byte[] buffer = new byte[1024 * 8];
                int len = 0;
                while ((len = ra.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
            }
            ra.close();
            fos.close();
        } catch (Exception e) {
        }
    }
}
