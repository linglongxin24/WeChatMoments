package cn.bluemobi.dylan.wechatmoments.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.bluemobi.dylan.wechatmoments.R;
import cn.bluemobi.dylan.wechatmoments.entity.TweetsEntity;
import cn.bluemobi.dylan.wechatmoments.view.MyListView;
import cn.bluemobi.dylan.wechatmoments.view.NineGridLayout;

/**
 * 朋友圈适配器
 * Created by yuandl on 2017-03-09.
 */

public class MyAdapter extends BaseAdapter {
    private final int MAX_LINE_COUNT = 6;

    private final int STATE_UNKNOW = -1;

    private final int STATE_NOT_OVERFLOW = 1;//文本行数不能超过限定行数

    private final int STATE_COLLAPSED = 2;//文本行数超过限定行数，进行折叠

    private final int STATE_EXPANDED = 3;//文本超过限定行数，被点击全文展开

    private SparseArray<Integer> mTextStateList;

    private List<TweetsEntity> tweetsEntities;
    private Context mContext;
    private LayoutInflater inflater;

    public MyAdapter(List<TweetsEntity> tweetsEntities, Context mContext) {
        this.tweetsEntities = tweetsEntities;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        mTextStateList = new SparseArray<>();

    }

    @Override
    public int getCount() {
        return tweetsEntities == null ? 0 : tweetsEntities.size();
    }

    @Override
    public TweetsEntity getItem(int position) {
        return tweetsEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void notifyDataSetChanged(List<TweetsEntity> tweetsEntities) {
        this.tweetsEntities = tweetsEntities;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_wechat, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final TweetsEntity item = getItem(position);
        Glide.with(mContext).load(item.getSender().getAvatar()).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(viewHolder.ivhead);
        viewHolder.tvname.setText(item.getSender().getNick());
        if (TextUtils.isEmpty(item.getContent())) {
            viewHolder.tvcontent.setVisibility(View.GONE);
            viewHolder.tv_more.setVisibility(View.GONE);
        } else {

            viewHolder.tvcontent.setVisibility(View.VISIBLE);
            viewHolder.tvcontent.setText(item.getContent());

            int state = mTextStateList.get(position, STATE_UNKNOW);
//        如果该itme是第一次初始化，则取获取文本的行数
            if (state == STATE_UNKNOW) {
                viewHolder.tvcontent.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        viewHolder.tvcontent.getViewTreeObserver().removeOnPreDrawListener(this);
//                    如果内容显示的行数大于限定显示行数
                        if (viewHolder.tvcontent.getLineCount() > MAX_LINE_COUNT) {
                            viewHolder.tvcontent.setMaxLines(MAX_LINE_COUNT);//设置最大显示行数
                            viewHolder.tv_more.setVisibility(View.VISIBLE);//让其显示全文的文本框状态为显示
                            viewHolder.tv_more.setText("全文");//设置其文字为全文
                            mTextStateList.put(position, STATE_COLLAPSED);
                        } else {
                            viewHolder.tv_more.setVisibility(View.GONE);//显示全文隐藏
                            mTextStateList.put(position, STATE_NOT_OVERFLOW);//让其不能超过限定的行数
                        }
                        return true;
                    }
                });

                viewHolder.tvcontent.setMaxLines(Integer.MAX_VALUE);//设置文本的最大行数，为整数的最大数值
                viewHolder.tvcontent.setText(item.getContent());//用Util中的getContent方法获取内容
            } else {
//            如果之前已经初始化过了，则使用保存的状态，无需在获取一次
                switch (state) {
                    case STATE_NOT_OVERFLOW:
                        viewHolder.tv_more.setVisibility(View.GONE);
                        break;
                    case STATE_COLLAPSED:
                        viewHolder.tvcontent.setMaxLines(MAX_LINE_COUNT);
                        viewHolder.tv_more.setVisibility(View.VISIBLE);
                        viewHolder.tv_more.setText("全文");
                        break;
                    case STATE_EXPANDED:
                        viewHolder.tvcontent.setMaxLines(Integer.MAX_VALUE);
                        viewHolder.tv_more.setVisibility(View.VISIBLE);
                        viewHolder.tv_more.setText("收起");
                        break;
                }
                viewHolder.tvcontent.setText(item.getContent());
            }


//        设置显示和收起的点击事件
            viewHolder.tv_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int state = mTextStateList.get(position, STATE_UNKNOW);
                    if (state == STATE_COLLAPSED) {
                        viewHolder.tvcontent.setMaxLines(Integer.MAX_VALUE);
                        viewHolder.tv_more.setText("收起");
                        mTextStateList.put(position, STATE_EXPANDED);
                    } else if (state == STATE_EXPANDED) {
                        viewHolder.tvcontent.setMaxLines(MAX_LINE_COUNT);
                        viewHolder.tv_more.setText("全文");
                        mTextStateList.put(position, STATE_COLLAPSED);
                    }
                }
            });


        }
        if (item.getImages() != null) {
            viewHolder.layoutninegrid.setUrlList(item.getImages());
        }
        if (item.getComments() != null) {
            viewHolder.lv.setAdapter(new CommentAdapter(item.getComments(), mContext));

        }

        return convertView;
    }

    public class ViewHolder {
        public final ImageView ivhead;
        public final TextView tvname;
        public final TextView tvcontent;
        public final TextView tv_more;
        public final NineGridLayout layoutninegrid;
        public final MyListView lv;
        public final View root;

        public ViewHolder(View root) {
            ivhead = (ImageView) root.findViewById(R.id.iv_head);
            tvname = (TextView) root.findViewById(R.id.tv_name);
            tvcontent = (TextView) root.findViewById(R.id.tv_content);
            tv_more = (TextView) root.findViewById(R.id.tv_more);
            layoutninegrid = (NineGridLayout) root.findViewById(R.id.layout_nine_grid);
            lv = (MyListView) root.findViewById(R.id.lv);
            this.root = root;
        }
    }
}
