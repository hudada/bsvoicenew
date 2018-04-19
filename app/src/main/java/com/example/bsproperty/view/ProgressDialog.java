package com.example.bsproperty.view;

import android.app.Dialog;
import android.content.Context;

import com.example.bsproperty.R;

/**
 * Created by yezi on 2018/1/27.
 */

public class ProgressDialog extends Dialog {
    public ProgressDialog(Context context) {
        super(context, R.style.MyDialog);
        setContentView(R.layout.dialog_progress);
    }
}
