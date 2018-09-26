package msu.roadsafety.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import msu.roadsafety.Model.Notification;
import msu.roadsafety.R;

public class NotificationAdapter extends BaseAdapter {
    ArrayList<Notification> notifications;
    Context context;

    public NotificationAdapter(ArrayList<Notification> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Object getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        convertView = LayoutInflater.from(context).inflate(R.layout.x_notification_item, null);
        viewHolder.textView = (TextView) convertView.findViewById(R.id.notification_content);
        Notification notification = (Notification) getItem(position);
        if (notification.getType() == 0) {
            viewHolder.textView.setText(notification.getUsername_notification() + " liked your post.");
        } else {
            viewHolder.textView.setText(notification.getUsername_notification() + " commented your post.");
        }
        return convertView;
    }

    public class ViewHolder {
        TextView textView;
    }
}
