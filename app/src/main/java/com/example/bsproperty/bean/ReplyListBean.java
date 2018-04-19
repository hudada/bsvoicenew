package com.example.bsproperty.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by wdxc1 on 2018/4/3.
 */

public class ReplyListBean extends BaseResponse {
    private ArrayList<ReplyBean> data;

    public ArrayList<ReplyBean> getData() {
        return data;
    }

    public void setData(ArrayList<ReplyBean> data) {
        this.data = data;
    }
}
