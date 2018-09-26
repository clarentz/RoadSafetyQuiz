package msu.roadsafety.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import msu.roadsafety.Activity.CommentActivity;
import msu.roadsafety.Model.Userfeed;
import msu.roadsafety.R;
import msu.roadsafety.Server.Server;

public class UserfeedAdapter extends BaseAdapter {
    ArrayList<Userfeed> userfeeds;
    Context context;
    int id_user;

    public UserfeedAdapter(ArrayList<Userfeed> userfeeds, Context context, int id_user) {
        this.userfeeds = userfeeds;
        this.context = context;
        this.id_user = id_user;
    }

    @Override
    public int getCount() {
        return userfeeds.size();
    }

    @Override
    public Object getItem(int position) {
        return userfeeds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        convertView = LayoutInflater.from(context).inflate(R.layout.x_userfeed_item, null);
        viewHolder.status = (TextView) convertView.findViewById(R.id.userfeed_status);
        viewHolder.image = (ImageView) convertView.findViewById(R.id.userfeed_image);
        viewHolder.like = convertView.findViewById(R.id.userfeed_like);
        viewHolder.comment = (ImageView) convertView.findViewById(R.id.userfeed_comment);
        viewHolder.address = (TextView) convertView.findViewById(R.id.userfeed_area);
        viewHolder.issue = (TextView) convertView.findViewById(R.id.userfeed_issue);
        viewHolder.like_count = (TextView) convertView.findViewById(R.id.userfeed_likecount);
        final Userfeed userfeed = (Userfeed) getItem(position);
        // Making an item
        if (!TextUtils.isEmpty(userfeed.getStatus_newsfeed())) {
            viewHolder.status.setText(userfeed.getStatus_newsfeed().toString());
            viewHolder.status.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.status.setVisibility(View.GONE);
        }
        if (userfeed.getImage_newsfeed().length() > 0) {
            Picasso.with(context).load(Server.ImageURL + userfeed.getImage_newsfeed()).placeholder(R.mipmap.ic_image_thumbnail).into(viewHolder.image);
        }
        if (!TextUtils.isEmpty(userfeed.getRoadname_newsfeed()) || !TextUtils.isEmpty(userfeed.getAdminarea_newsfeed()) || !TextUtils.isEmpty(userfeed.getSubadminarea_newsfeed())) {
            viewHolder.address.setText(userfeed.getRoadname_newsfeed().toString() + ", " + userfeed.getSubadminarea_newsfeed()  + ", " + userfeed.getAdminarea_newsfeed());
            viewHolder.address.setVisibility(View.VISIBLE);
        } else {
            viewHolder.address.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(userfeed.getIssue_newsfeed())) {
            viewHolder.issue.setText(userfeed.getIssue_newsfeed().toString());
            viewHolder.issue.setVisibility(View.VISIBLE);
        } else {
            viewHolder.issue.setVisibility(View.GONE);
        }
        if (userfeed.getLike_newsfeed() != 0) {
            viewHolder.like_count.setText(String.valueOf(userfeed.getLike_newsfeed()));
            viewHolder.like_count.setVisibility(View.VISIBLE);
        } else {
            viewHolder.like_count.setVisibility(View.GONE);
        }
        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoticeNotification(userfeed.getId_newsfeed(), 1);
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("id_newsfeed", userfeed.getId_newsfeed());
                intent.putExtra("id_user", id_user);
                context.startActivity(intent);
            }
        });
        viewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(true);
                //Toast.makeText(context, "CLICK LIKE", Toast.LENGTH_SHORT).show();
                Like(userfeed);
                NoticeNotification(userfeed.getId_newsfeed(), 0); // the int 0 will tell the notification it is a notice about like or a comment
            }
        });
        return convertView;
    }

    private void NoticeNotification(int userfeed, final int type) {
        final String noti_id_user = String.valueOf(id_user);
        final String noti_userFeed = String.valueOf(userfeed);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Notification_CreateURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response == null) {
                    Toast.makeText(context, "Like Fail!", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>(); //an array with key and value, while the value are id of newsfeed, id of user and the status
                hashMap.put("id_newsfeed", noti_userFeed);
                hashMap.put("id_user", noti_id_user);
                hashMap.put("type", String.valueOf(type));
                return hashMap;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void Like(Userfeed userfeed) {
        final String like_id_user = String.valueOf(id_user);
        final String like_userfeed = String.valueOf(userfeed.getId_newsfeed());
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Like_CreateURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response == null) {
                    Toast.makeText(context, "Like Fail!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>(); //an array with key and value, while the value are id of newsfeed, id of user and the status
                hashMap.put("id_newsfeed", like_userfeed);
                hashMap.put("id_user", like_id_user);
                return hashMap;
            }
        };
        requestQueue.add(stringRequest);
    }

    public class ViewHolder {
        ImageView image, comment;
        LinearLayout like;
        TextView status, address, issue, like_count;
    }
}
