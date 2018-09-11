package msu.olive.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import msu.olive.Model.Comment;
import msu.olive.R;

public class CommentAdapter extends BaseAdapter {
    ArrayList<Comment> comments;
    Context context;

    public CommentAdapter(ArrayList<Comment> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.x_comment_item, null);
            viewHolder = new ViewHolder();
            viewHolder.username = (TextView) convertView.findViewById(R.id.comment_username);
            viewHolder.comment = (TextView) convertView.findViewById(R.id.comment_comment);
            Comment comment = (Comment) getItem(position);
            viewHolder.username.setText(comment.getUsername_comment());
            viewHolder.comment.setText(comment.getContent_comment());
        }
        return convertView;
    }

    public class ViewHolder {
        TextView username;
        TextView comment;
    }
}
