package com.example.bsproperty.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bsproperty.R;
import com.example.bsproperty.utils.DenstityUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by yezi on 2018/1/27.
 */

public class ModifyItemDialog extends Dialog {
    TextView tvTitle;
    EditText etModify;
    Button btnCancel;
    Button btnOk;

    public ModifyItemDialog(Context context) {
        super(context, R.style.MyDialog);
        setContentView(R.layout.dialog_change_item);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        etModify = (EditText) findViewById(R.id.et_modify);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnOk = (Button) findViewById(R.id.btn_ok);

        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DenstityUtils.screenWidth((Activity) context) - DenstityUtils.dp2px(context, 20);
        window.setAttributes(params);
    }

    public ModifyItemDialog setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        return this;
    }

    public ModifyItemDialog setMessage(String message) {
        if (!TextUtils.isEmpty(message)) {
            etModify.setHint(message);
        } else {
            etModify.setHint("");
        }
        return this;
    }

    public ModifyItemDialog setCancelClick(String text, final View.OnClickListener onClickListener) {
        if (!TextUtils.isEmpty(text)) {
            btnCancel.setText(text);
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onClickListener != null) {
                    btnCancel.setOnClickListener(onClickListener);
                }
            }
        });
        return this;
    }


    public ModifyItemDialog setOkClick(String text, final OnOkClickListener onClickListener) {
        if (!TextUtils.isEmpty(text)) {
            btnOk.setText(text);
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onClickListener != null) {
                    onClickListener.onOkClick(etModify.getText().toString());
                }
            }
        });
        return this;
    }

    public interface OnOkClickListener{
        void onOkClick(String etStr);
    }
}
