package com.example.bsproperty.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.bsproperty.MyApplication;
import com.example.bsproperty.R;
import com.example.bsproperty.adapter.BaseAdapter;
import com.example.bsproperty.bean.BaseResponse;
import com.example.bsproperty.bean.SongBean;
import com.example.bsproperty.bean.SongListBean;
import com.example.bsproperty.net.ApiManager;
import com.example.bsproperty.net.BaseCallBack;
import com.example.bsproperty.net.OkHttpTools;
import com.example.bsproperty.utils.Player;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GirlsListActivity extends BaseActivity {

    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.sl_list)
    SwipeRefreshLayout slList;
    @BindView(R.id.btn_back)
    ImageButton btnBack;

    private ArrayList<SongBean> mData;
    private MyAdapter adapter;
    private Player player;
    private int currPosition = -1;

    @Override
    protected void initView(Bundle savedInstanceState) {
        rvList.setLayoutManager(new LinearLayoutManager(mContext));
        mData = new ArrayList<>();
        adapter = new MyAdapter(mContext, R.layout.item_voice, mData);
        rvList.setAdapter(adapter);
        slList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                slList.setRefreshing(false);
            }
        });

    }

    @Override
    protected int getRootViewId() {
        return R.layout.activity_girlllist;
    }

    @Override
    protected void loadData() {
        long id;
        if (MyApplication.getInstance().getUserBean() == null) {
            id = -1;
        } else {
            id = MyApplication.getInstance().getUserBean().getId();
        }
        OkHttpTools.sendGet(mContext, ApiManager.SONG_RANK)
                .addParams("type", "0")
                .addParams("uid", id + "")
                .build()
                .execute(new BaseCallBack<SongListBean>(mContext, SongListBean.class) {
                    @Override
                    public void onResponse(SongListBean songListBean) {
                        mData = songListBean.getData();
                        adapter.notifyDataSetChanged(mData);
                    }
                });
    }

    @OnClick(R.id.btn_back)
    public void onViewClicked() {
        finish();
    }

    private class MyAdapter extends BaseAdapter<SongBean> {

        public MyAdapter(Context context, int layoutId, ArrayList<SongBean> data) {
            super(context, layoutId, data);
        }

        @Override
        public void initItemView(BaseViewHolder holder, final SongBean songBean, final int position) {
            holder.setText(R.id.tv_name, songBean.getName());
            holder.setText(R.id.tv_total, "时间：" + MyApplication.formatTime.format(songBean.getLength()) + "s");
            holder.setText(R.id.tv_username, "用户名：" + songBean.getUname());
            ImageView like = (ImageView) holder.getView(R.id.iv_like);
            if (songBean.isLike()) {
                like.setImageResource(R.drawable.ic_favorite_red_300_24dp);
            } else {
                like.setImageResource(R.drawable.ic_favorite_white_24dp);
            }
            holder.setText(R.id.tv_like, "(" + songBean.getLikeSum() + ")");
            holder.setText(R.id.tv_sex, "女声");
            ImageButton play = (ImageButton) holder.getView(R.id.btn_play);
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playVoice(ApiManager.VOICE_PATH + songBean.getAddr()
                            , position);
                }
            });
            if (songBean.isPlay()) {
                play.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
            } else {
                play.setImageResource(R.drawable.ic_play_circle_outline_white_24dp);
            }
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MyApplication.getInstance().getUserBean() != null) {
                        if (songBean.isLike()) {
                            OkHttpTools.sendPost(mContext, ApiManager.LIKE_DEL)
                                    .addParams("uid", MyApplication.getInstance().getUserBean().getId() + "")
                                    .addParams("sid", songBean.getId() + "")
                                    .build()
                                    .execute(new BaseCallBack<BaseResponse>(mContext, BaseResponse.class) {
                                        @Override
                                        public void onResponse(BaseResponse baseResponse) {
                                            mData.get(position).setLikeSum(mData.get(position)
                                                    .getLikeSum() - 1);
                                            mData.get(position).setLike(false);
                                            adapter.notifyItemChanged(position, "one");
                                        }
                                    });
                        } else {
                            OkHttpTools.sendPost(mContext, ApiManager.LIKE_ADD)
                                    .addParams("uid", MyApplication.getInstance().getUserBean().getId() + "")
                                    .addParams("sid", songBean.getId() + "")
                                    .build()
                                    .execute(new BaseCallBack<BaseResponse>(mContext, BaseResponse.class) {
                                        @Override
                                        public void onResponse(BaseResponse baseResponse) {
                                            mData.get(position).setLikeSum(mData.get(position)
                                                    .getLikeSum() + 1);
                                            mData.get(position).setLike(true);
                                            adapter.notifyItemChanged(position, "one");
                                        }
                                    });
                        }

                    }

                }
            });
        }
    }

    private void playVoice(String s, final int position) {
        if (position == currPosition) {
            if (player.getMediaPlayer().isPlaying()) {
                player.pause();
                mData.get(position).setPlay(false);
            } else {
                mData.get(position).setPlay(true);
                player.play(true);
            }
            adapter.notifyItemChanged(position, "one");
        } else {
            if (player != null) {
                player.stop();
                mData.get(currPosition).setPlay(false);
                adapter.notifyItemChanged(currPosition, "one");
                player.release();
            }
            currPosition = position;
            player = new Player(s, null, new Player.OnPlayListener() {
                @Override
                public void onLoad(int duration) {
                }

                @Override
                public void onProgress(int position) {
                }

                @Override
                public void onCompletion() {
                    player = null;
                    mData.get(position).setPlay(false);
                    adapter.notifyItemChanged(position, "one");
                    currPosition = -1;
                }
            });
            player.play(true);
            mData.get(position).setPlay(true);
            adapter.notifyItemChanged(position, "one");
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }
}
