package com.example.bsproperty.bean;

/**
 * Created by wdxc1 on 2018/1/28.
 */

public class UserObjBean extends BaseResponse {
    private UserBean data;

    public UserBean getData() {
        return data;
    }

    public void setData(UserBean data) {
        this.data = data;
    }
}
