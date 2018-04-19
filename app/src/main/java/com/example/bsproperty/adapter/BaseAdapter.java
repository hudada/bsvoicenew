package com.example.bsproperty.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.bsproperty.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by John on 2017/10/21.
 * 通用adapter
 */

public abstract class BaseAdapter<T extends Object> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final static int TYPE_ITEM = 0;
    public final static int TYPE_FOOTER = 1;
    public final static int TYPE_HEAD = 2;
    public Context mContext;
    private int mLayoutId;
    public ArrayList<T> mData;
    private LayoutInflater mInflater;
    private OnLoadMoreListener onLoadMoreListener;
    private OnItemClickListener onItemClickListener;
    private boolean isLoading;
    private boolean isFooter;
    private boolean isNoMore;
    private TextView tv_load_msg;
    private int mHeadViewId;
    private boolean hasHead;
    private View mHeadView;
    private HashMap<Integer, Integer> mLayoutIds;
    private OnInitHead onInitHead;

    public BaseAdapter(Context context, int layoutId, ArrayList<T> data) {
        mContext = context;
        mLayoutId = layoutId;
        mData = data;
        mInflater = LayoutInflater.from(mContext);
    }

    public BaseAdapter(Context context, HashMap<Integer, Integer> layoutIds, ArrayList<T> data) {
        mContext = context;
        mLayoutIds = layoutIds;
        mData = data;
        mInflater = LayoutInflater.from(mContext);
    }

    @SuppressLint("ResourceType")
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (mData.size() == 0 && !hasHead) {
            holder = new BaseViewHolder(mInflater.inflate(R.layout.item_empty, parent, false));
        } else {
            if (viewType == TYPE_ITEM) {
                holder = new BaseViewHolder(mInflater.inflate(mLayoutId, parent, false));
            } else if (viewType == TYPE_HEAD) {
                mHeadView = mInflater.inflate(mHeadViewId, parent, false);
                holder = new BaseViewHolder(mHeadView);
            } else if (viewType == TYPE_FOOTER) {
                holder = new BaseFooterViewHolder(mInflater.inflate(R.layout.item_footer, parent, false));
            } else {
                holder = new BaseViewHolder(mInflater.inflate(mLayoutIds.get(viewType), parent, false));
            }
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (mData.size() == 0 && !hasHead) {

        } else {
            if (holder instanceof BaseAdapter.BaseFooterViewHolder) {
                tv_load_msg = ((BaseFooterViewHolder) holder).msg;
            } else if (holder instanceof BaseAdapter.BaseViewHolder) {
                if (position == 0 && hasHead && onInitHead != null) {
                    if (mData.size() > 0) {
                        onInitHead.onInitHeadData(mHeadView, mData.get(position));
                    } else {
                        onInitHead.onInitHeadData(mHeadView, null);
                    }
                } else {
                    if (onItemClickListener != null) {
                        ((BaseViewHolder) holder).rootView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (hasHead) {
                                    onItemClickListener.onItemClick(v, mData.get(position - 1), position - 1);
                                } else {
                                    onItemClickListener.onItemClick(v, mData.get(position), position);
                                }
                            }
                        });
                    }
                    if (hasHead) {
                        initItemView((BaseViewHolder) holder, mData.get(position - 1), position - 1);
                    } else {
                        initItemView((BaseViewHolder) holder, mData.get(position), position);
                    }
                }
            }
        }
    }

    public abstract void initItemView(BaseViewHolder holder, T t, int position);

    @Override
    public int getItemCount() {
        if (mData.size()==0){
            return 1;
        }else{
            return mData.size() + (isFooter ? 1 : 0) + (hasHead ? 1 : 0);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && hasHead) {
            return TYPE_HEAD;
        } else if (position > mData.size() + (hasHead ? 1 : 0) - 1) {
            return TYPE_FOOTER;
        }
        return getOnlyItemViewType(position - (hasHead ? 1 : 0));
    }

    public int getOnlyItemViewType(int position) {
        return TYPE_ITEM;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnItemClickListener<T> {
        void onItemClick(View v, T item, int position);
    }

    public interface OnInitHead<T> {
        void onInitHeadData(View headView, T t);
    }

    public OnLoadMoreListener getOnLoadMoreListener() {
        return onLoadMoreListener;
    }

    public void setOnLoadMoreListener(RecyclerView recyclerView, OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
        initScrollListner(recyclerView);
        isFooter = true;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void initScrollListner(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int totalItemCount = linearLayoutManager.getItemCount();
                    int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                    if (!isNoMore) {
                        if (!isLoading && totalItemCount <= lastVisibleItemPosition + (hasHead ? 1 : 0) + 1) {  //暂时设置滑到最后才触发
                            isLoading = true;
                            if (onLoadMoreListener != null) {
                                onLoadMoreListener.onLoadMore();
                            }
                        }
                    }
                }
            });
        }
    }

    public void setLoadComplete(boolean flag) {
        isLoading = false;
        isNoMore = flag;
        if (flag) {
            tv_load_msg.setText("没有更多了");
        } else {
            tv_load_msg.setText("正在加载");
        }
    }

    public void setmHeadView(int mHeadView, OnInitHead onInitHead) {
        this.mHeadViewId = mHeadView;
        this.onInitHead = onInitHead;
        hasHead = true;
    }

    /**
     * 通用viewholder
     */
    public class BaseViewHolder extends RecyclerView.ViewHolder {

        private Map<Integer, View> viewMap;
        private View rootView;

        public BaseViewHolder(View itemView) {
            super(itemView);
            viewMap = new HashMap<>();
            rootView = itemView;
        }

        public View getView(int viewId) {
            if (viewMap.containsKey(viewId)) {
                return viewMap.get(viewId);
            } else {
                View view = rootView.findViewById(viewId);
                viewMap.put(viewId, view);
                return view;
            }
        }

        public BaseViewHolder setText(int viewId, String str) {
            if (TextUtils.isEmpty(str)) {
                ((TextView) getView(viewId)).setText("");
            } else {
                ((TextView) getView(viewId)).setText(str);
            }
            return this;
        }
    }

    public class BaseFooterViewHolder extends RecyclerView.ViewHolder {

        private TextView msg;

        public BaseFooterViewHolder(View itemView) {
            super(itemView);
            msg = (TextView) itemView.findViewById(R.id.tv_load_msg);
        }
    }

    public void notifyDataSetChanged(ArrayList<T> data) {
        mData = data;
        notifyDataSetChanged();
    }
}
