package com.example.bsproperty.ui;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bsproperty.MyApplication;
import com.example.bsproperty.R;
import com.example.bsproperty.bean.MusicBean;
import com.example.bsproperty.bean.MusicListBean;
import com.example.bsproperty.bean.MusicObjBean;
import com.example.bsproperty.net.ApiManager;
import com.example.bsproperty.net.BaseCallBack;
import com.example.bsproperty.net.OkHttpTools;
import com.example.bsproperty.utils.DenstityUtils;
import com.example.bsproperty.utils.LQRPhotoSelectUtils;
import com.example.bsproperty.view.FileProgressDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import okhttp3.Call;
import okhttp3.Request;

public class AccompanimentActivity extends BaseActivity {

    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_right)
    Button btnRight;
    @BindView(R.id.ll_my)
    LinearLayout llMy;
    @BindView(R.id.ll_web)
    LinearLayout llWeb;

    private LayoutInflater mInflater;
    private FileProgressDialog dialog;
    private MediaPlayer mediaPlayer;
    private File selectFile;
    private MusicBean downMusic;
    private MusicBean selectMusic;

    @Override
    protected void initView(Bundle savedInstanceState) {
        tvTitle.setText("选择伴奏");
        mInflater = LayoutInflater.from(mContext);
        dialog = new FileProgressDialog(mContext);
        mediaPlayer = new MediaPlayer();
    }

    @Override
    protected int getRootViewId() {
        return R.layout.activity_accompaniment;
    }

    @Override
    protected void loadData() {
        loadLocalData();

        loadWebData();
    }

    private void loadLocalData() {
        llMy.removeAllViews();
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/bsktv");
        if (file == null || file.listFiles() == null || file.listFiles().length <= 0) {
            return;
        }
        for (final File file1 : file.listFiles()) {
            View view = mInflater.inflate(R.layout.item_accom, null, true);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    DenstityUtils.dp2px(mContext, 50));
            ((TextView) view.findViewById(R.id.tv_name)).setText(file1.getName());

            ((ImageButton) view.findViewById(R.id.btn_act))
                    .setImageResource(R.drawable.ic_play_arrow_grey_400_24dp);
            view.findViewById(R.id.btn_act).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectFile = file1;
                    PermissionGen.with((Activity) mContext)
                            .addRequestCode(521)
                            .permissions(Manifest.permission.RECORD_AUDIO,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ).request();
                }
            });
            try {
                mediaPlayer.setDataSource(file1.getAbsolutePath());
                mediaPlayer.prepare();
                int time = mediaPlayer.getDuration();
                ((TextView) view.findViewById(R.id.tv_time)).setText("时长：" +
                        MyApplication.formatTime.format(time));
                mediaPlayer.reset();

            } catch (Exception e) {
                ((TextView) view.findViewById(R.id.tv_time)).setText("");
            }
            llMy.addView(view, params);
        }
    }

    private void loadWebData() {
    }

    @PermissionSuccess(requestCode = 521)
    private void ok() {
        String name = selectFile.getName();
        try {
            name = selectFile.getName().split("\\.")[0];
        } catch (Exception e) {

        }
    }

    @PermissionSuccess(requestCode = 522)
    private void ok1() {
    }

    @PermissionFail(requestCode = 521)
    private void showTip1() {
        showDialog();
    }

    @PermissionFail(requestCode = 522)
    private void showTip2() {
        showDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("权限申请");
        builder.setMessage("在设置-应用-权限 中开启相关权限");

        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @OnClick(R.id.btn_back)
    public void onViewClicked() {
        finish();
    }
}
