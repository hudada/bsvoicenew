package com.example.bsproperty.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bsproperty.ui.BaseActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by yezi on 2018/1/27.
 */

public abstract class BaseFragment extends Fragment {

    protected Context mContext;
    protected View mRootView;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(getRootViewId(), container, false);
        unbinder = ButterKnife.bind(this, mRootView);
        mContext = getActivity();
        initView(savedInstanceState);
        loadData();
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void showToast(String str) {
        ((BaseActivity) mContext).showToast(str);
    }

    protected abstract void loadData();

    protected abstract void initView(Bundle savedInstanceState);

    public abstract int getRootViewId();
}
