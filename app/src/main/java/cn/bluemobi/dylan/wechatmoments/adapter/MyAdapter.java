package cn.bluemobi.dylan.wechatmoments.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private List<TweetsEntity> tweetsEntities;
    private Context mContext;
    private LayoutInflater inflater;

    public MyAdapter(List<TweetsEntity> tweetsEntities, Context mContext) {
        this.tweetsEntities = tweetsEntities;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_wechat, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TweetsEntity item = getItem(position);
        Glide.with(mContext).load(item.getSender().getAvatar()).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(viewHolder.ivhead);
        viewHolder.tvname.setText(item.getSender().getNick());
        if (TextUtils.isEmpty(item.getContent())) {
            viewHolder.tvcontent.setVisibility(View.GONE);
        } else {
            viewHolder.tvcontent.setVisibility(View.VISIBLE);
            viewHolder.tvcontent.setText(item.getContent());
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
        public final NineGridLayout layoutninegrid;
        public final MyListView lv;
        public final View root;

        public ViewHolder(View root) {
            ivhead = (ImageView) root.findViewById(R.id.iv_head);
            tvname = (TextView) root.findViewById(R.id.tv_name);
            tvcontent = (TextView) root.findViewById(R.id.tv_content);
            layoutninegrid = (NineGridLayout) root.findViewById(R.id.layout_nine_grid);
            lv = (MyListView) root.findViewById(R.id.lv);
            this.root = root;
        }
    }
}
