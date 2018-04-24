package com.example.bsproperty.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bsproperty.MyApplication;
import com.example.bsproperty.R;
import com.example.bsproperty.adapter.BaseAdapter;
import com.example.bsproperty.bean.BaseResponse;
import com.example.bsproperty.bean.SongBean;
import com.example.bsproperty.bean.SongListBean;
import com.example.bsproperty.fragment.UserFragment02;
import com.example.bsproperty.net.ApiManager;
import com.example.bsproperty.net.BaseCallBack;
import com.example.bsproperty.net.OkHttpTools;
import com.example.bsproperty.utils.Player;

import java.util.ArrayList;

import butterknife.BindView;

public class MySongActivity extends BaseActivity {

    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_right)
    Button btnRight;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.sl_list)
    SwipeRefreshLayout slList;


    private ArrayList<SongBean> mData;
    private MySongActivity.MyAdapter adapter;
    private Player player;
    private int currPosition = -1;


    @Override
    public void onResume() {
        super.onResume();
        loadWebData();
    }

    private void loadWebData() {
        mData.clear();
        OkHttpTools.sendGet(mContext, ApiManager.MY_SONG + MyApplication.getInstance().getUserBean().getId())
                .build()
                .execute(new BaseCallBack<SongListBean>(mContext, SongListBean.class) {
                    @Override
                    public void onResponse(SongListBean songListBean) {
                        mData = songListBean.getData();
                        adapter.notifyDataSetChanged(mData);
                    }
                });
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        tvTitle.setText("我的声音");
        btnBack.setVisibility(View.GONE);
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
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public int getRootViewId() {
        return R.layout.fragment_user02;
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
            like.setImageResource(R.drawable.ic_favorite_white_24dp);
            holder.setText(R.id.tv_like, "(" + songBean.getLikeSum() + ")");
            if (songBean.getType() == 1) {
                holder.setText(R.id.tv_sex, "男声");
            } else {
                holder.setText(R.id.tv_sex, "女声");
            }
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
