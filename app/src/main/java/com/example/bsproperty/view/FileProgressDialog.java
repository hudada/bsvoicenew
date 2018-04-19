package com.example.bsproperty.view;

import android.app.Dialog;
import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bsproperty.R;

import butterknife.BindView;

/**
 * Created by yezi on 2018/1/27.
 */

public class FileProgressDialog extends Dialog {


    ProgressBar pbBar;
    TextView tvTotal;

    public FileProgressDialog(Context context) {
        super(context, R.style.MyDialog);
        setContentView(R.layout.dialog_file_progress);

        pbBar = (ProgressBar) findViewById(R.id.pb_bar);
        tvTotal = (TextView) findViewById(R.id.tv_total);

        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }


    public void setPor(float progress) {
        if (!isShowing()) {
            show();
        }
        int pg = (int) (progress * 100);
        pbBar.setProgress(pg);
        tvTotal.setText(pg + "%");
    }
}
