package com.example.bsproperty.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bsproperty.MyApplication;
import com.example.bsproperty.R;
import com.example.bsproperty.adapter.BaseAdapter;
import com.example.bsproperty.bean.BaseResponse;
import com.example.bsproperty.bean.SongBean;
import com.example.bsproperty.bean.SongListBean;
import com.example.bsproperty.net.ApiManager;
import com.example.bsproperty.net.BaseCallBack;
import com.example.bsproperty.net.OkHttpTools;
import com.example.bsproperty.ui.AccompanimentActivity;
import com.example.bsproperty.ui.BoysListActivity;
import com.example.bsproperty.ui.GirlsListActivity;
import com.example.bsproperty.utils.Player;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by wdxc1 on 2018/3/21.
 */

public class UserFragment01 extends BaseFragment {
    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_q)
    Button btnQ;
    @BindView(R.id.et_q)
    EditText etQ;
    @BindView(R.id.btn_right)
    Button btnRight;
    @BindView(R.id.btn_nan)
    Button btnNan;
    @BindView(R.id.btn_nv)
    Button btnNv;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.sl_list)
    SwipeRefreshLayout slList;

    private ArrayList<SongBean> mData;
    private MyAdapter adapter;
    private Player player;
    private int currPosition = -1;

    @Override
    public void onResume() {
        super.onResume();
        loadWebData();
    }

    private void loadWebData() {
        mData.clear();
        long id;
        if (MyApplication.getInstance().getUserBean() == null) {
            id = -1;
        } else {
            id = MyApplication.getInstance().getUserBean().getId();
        }
        OkHttpTools.sendGet(mContext, ApiManager.SONG_LIST)
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

    @Override
    protected void loadData() {

    }

    @OnClick({R.id.btn_q, R.id.btn_right, R.id.btn_nan, R.id.btn_nv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_right:
                startActivity(new Intent(mContext, AccompanimentActivity.class));
                break;
            case R.id.btn_q:
                long id;
                if (MyApplication.getInstance().getUserBean() == null) {
                    id = -1;
                } else {
                    id = MyApplication.getInstance().getUserBean().getId();
                }
                String key = etQ.getText().toString().trim();
                OkHttpTools.sendGet(mContext, ApiManager.SONG_SEARCH)
                        .addParams("uid", id + "")
                        .addParams("key", key)
                        .build()
                        .execute(new BaseCallBack<SongListBean>(mContext, SongListBean.class) {
                            @Override
                            public void onResponse(SongListBean songListBean) {
                                mData = songListBean.getData();
                                adapter.notifyDataSetChanged(mData);
                            }
                        });
                break;
            case R.id.btn_nan:
                startActivity(new Intent(mContext, BoysListActivity.class));
                break;
            case R.id.btn_nv:
                startActivity(new Intent(mContext, GirlsListActivity.class));
                break;
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        tvTitle.setText("声音广场");
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

    }

    @Override
    public int getRootViewId() {
        return R.layout.fragment_user01;
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
                player.release();
            }
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
