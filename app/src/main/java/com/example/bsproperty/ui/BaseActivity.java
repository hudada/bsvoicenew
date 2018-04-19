package com.example.bsproperty.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bsproperty.view.ProgressDialog;

import java.io.Serializable;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by yezi on 2018/1/27.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private static Toast mToast;
    private Unbinder unbinder;
    private ProgressDialog progressDialog;
    public Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getRootViewId());
        unbinder = ButterKnife.bind(this);
        mContext = this;
        initView(savedInstanceState);
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    protected abstract void initView(Bundle savedInstanceState);

    protected abstract int getRootViewId();

    protected abstract void loadData();

    public void showToast(String str) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, str, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(str);
        }
        mToast.show();
    }

    public void showToast(Context context, String str) {
        if (mToast == null) {
            mToast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(str);
        }
        mToast.show();
    }

    public void showProgress(Context context) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
        }
        progressDialog.show();
    }

    public void dismissDialog() {
        progressDialog.dismiss();
    }

    public void jumpAct(Class toAct, Object... obj) {
        Intent intent = new Intent(mContext, toAct);
        for (int i = 0; i < obj.length; i++) {
            Object o = obj[i];
            if (o instanceof Integer) {
                intent.putExtra(i + "", (Integer) o);
            } else if (o instanceof String) {
                intent.putExtra(i + "", (String) o);
            } else if (o instanceof Serializable) {
                intent.putExtra(i + "", (Serializable) o);
            }
        }
        startActivity(intent);
    }

    public boolean checkEditEmpty(EditText... editTexts) {
        for (EditText editText : editTexts) {
            String str = editText.getText().toString().trim();
            if (TextUtils.isEmpty(str)) {
                showToast(editText.getHint().toString());
                return true;
            }
        }
        return false;
    }
}
