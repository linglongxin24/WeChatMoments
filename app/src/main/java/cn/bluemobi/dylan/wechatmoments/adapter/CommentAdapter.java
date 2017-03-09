package cn.bluemobi.dylan.wechatmoments.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.bluemobi.dylan.wechatmoments.R;
import cn.bluemobi.dylan.wechatmoments.entity.TweetsEntity;

/**
 * Created by yuandl on 2017-03-09.
 */

public class CommentAdapter extends BaseAdapter {
    private List<TweetsEntity.CommentsEntity> commentsEntities;

    private Context mContext;
    private LayoutInflater inflater;

    public CommentAdapter(List<TweetsEntity.CommentsEntity> commentsEntities, Context mContext) {
        this.commentsEntities = commentsEntities;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return commentsEntities == null ? 0 : commentsEntities.size();
    }

    @Override
    public TweetsEntity.CommentsEntity getItem(int position) {
        return commentsEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_commit, null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        TweetsEntity.CommentsEntity item = getItem(position);
        viewHolder.tvname.setText(item.getSender().getNick() + ":");
        viewHolder.tvreply.setText(item.getContent());
        return convertView;
    }

    public class ViewHolder {
        public final TextView tvname;
        public final TextView tvreply;
        public final View root;

        public ViewHolder(View root) {
            tvname = (TextView) root.findViewById(R.id.tv_name);
            tvreply = (TextView) root.findViewById(R.id.tv_reply);
            this.root = root;
        }
    }
}
