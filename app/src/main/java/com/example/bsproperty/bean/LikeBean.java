package com.example.bsproperty.bean;

public class LikeBean {

    private Long id;
    private Long uid;
    private Long likeUid;
    private int type; //0=1关注2，1=2关注1

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getLikeUid() {
        return likeUid;
    }

    public void setLikeUid(Long likeUid) {
        this.likeUid = likeUid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
