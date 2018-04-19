package com.example.bsproperty.bean;

import java.util.ArrayList;

/**
 * Created by wdxc1 on 2018/1/28.
 */

public class MusicListBean extends BaseResponse {
    private ArrayList<MusicBean> data;

    public ArrayList<MusicBean> getData() {
        return data;
    }

    public void setData(ArrayList<MusicBean> data) {
        this.data = data;
    }
}
