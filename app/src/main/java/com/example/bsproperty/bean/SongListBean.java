package com.example.bsproperty.bean;

import java.util.ArrayList;

/**
 * Created by wdxc1 on 2018/1/28.
 */

public class SongListBean extends BaseResponse {
    private ArrayList<SongBean> data;

    public ArrayList<SongBean> getData() {
        return data;
    }

    public void setData(ArrayList<SongBean> data) {
        this.data = data;
    }
}
