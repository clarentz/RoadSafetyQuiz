package msu.olive.Adapter;

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
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import msu.olive.Activity.CommentActivity;
import msu.olive.Activity.UploadActivity;
import msu.olive.Model.Newsfeed;
import msu.olive.R;
import msu.olive.Server.Server;

public class NewsfeedAdapter extends BaseAdapter {

    ArrayList<Newsfeed> newsfeeds;
    Context context;
    int id_user;

    public NewsfeedAdapter(ArrayList<Newsfeed> newsfeeds, Context context, int id_user) {
        this.newsfeeds = newsfeeds;
        this.context = context;
        this.id_user = id_user;
    }

    @Override
    public int getCount() {
        return newsfeeds.size();
    }

    @Override
    public Object getItem(int position) {
        return newsfeeds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        convertView = LayoutInflater.from(context).inflate(R.layout.x_newsfeed_item, null);

        viewHolder.avatar = (CircleImageView) convertView.findViewById(R.id.newsfeed_avatar);
        viewHolder.image = (ImageView) convertView.findViewById(R.id.newsfeed_image);
        viewHolder.like = convertView.findViewById(R.id.newsfeed_like);
        viewHolder.comment = (ImageView) convertView.findViewById(R.id.newsfeed_comment);
        viewHolder.status = (TextView) convertView.findViewById(R.id.newsfeed_status);
        viewHolder.username = (TextView) convertView.findViewById(R.id.newsfeed_username);
        viewHolder.address = (TextView) convertView.findViewById(R.id.newsfeed_area);
        viewHolder.issue = (TextView) convertView.findViewById(R.id.newsfeed_issue);
        viewHolder.like_count = (TextView) convertView.findViewById(R.id.newsfeed_likecount);
        final Newsfeed newsfeed = (Newsfeed) getItem(position); //from the Models

        //If there is usable data, replace with the default ones
        viewHolder.username.setText(newsfeed.getUsername_newsfeed().toString());
        if (newsfeed.getAvatar_newsfeed().length() > 0) {
            Picasso.with(context).load(Server.ImageURL + newsfeed.getAvatar_newsfeed()).placeholder(R.drawable.ic_avatar).into(viewHolder.avatar);
        }
        // the if below will decide the text is visible or not, based on the data is null or not
        if (!TextUtils.isEmpty(newsfeed.getStatus_newsfeed())) {
            viewHolder.status.setText(newsfeed.getStatus_newsfeed().toString());
            viewHolder.status.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.status.setVisibility(View.GONE);
        }
        if (newsfeed.getImage_newsfeed().length() > 0) {
            Picasso.with(context).load(Server.ImageURL + newsfeed.getImage_newsfeed()).placeholder(R.mipmap.ic_image_thumbnail).into(viewHolder.image);
        }
        if (!TextUtils.isEmpty(newsfeed.getRoadname_newsfeed()) || !TextUtils.isEmpty(newsfeed.getAdminarea_newsfeed()) || !TextUtils.isEmpty(newsfeed.getSubadminarea_newsfeed())) {
            viewHolder.address.setText(newsfeed.getRoadname_newsfeed().toString() + ", " + newsfeed.getSubadminarea_newsfeed()  + ", " + newsfeed.getAdminarea_newsfeed());
            viewHolder.address.setVisibility(View.VISIBLE);
        } else {
            viewHolder.address.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(newsfeed.getIssue_newsfeed())) {
            viewHolder.issue.setText(newsfeed.getIssue_newsfeed().toString());
            viewHolder.issue.setVisibility(View.VISIBLE);
        } else {
            viewHolder.issue.setVisibility(View.GONE);
        }
        if (newsfeed.getLike_newsfeed() != 0) {
            viewHolder.like_count.setText(String.valueOf(newsfeed.getLike_newsfeed()));
            viewHolder.like_count.setVisibility(View.VISIBLE);
        } else {
            viewHolder.like_count.setVisibility(View.GONE);
        }
        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoticeNotification(newsfeed.getId_newsfeed(), 1);
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("id_newsfeed", newsfeed.getId_newsfeed());
                intent.putExtra("id_user", id_user);
                //Toast.makeText(context," ", Toast.LENGTH_SHORT).show();
                context.startActivity(intent);
            }
        });
        viewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //when press like button, "like" and add a notification
                v.setSelected(true);
                //Toast.makeText(context, "CLICK LIKE", Toast.LENGTH_SHORT).show();
                Like(newsfeed);
                NoticeNotification(newsfeed.getId_newsfeed(), 0);
            }
        });

        return convertView;
    }
    private void NoticeNotification(int id_newsfeed, final int type) {
        final String noti_id_user = String.valueOf(id_user);
        final String noti_newsfeed = String.valueOf(id_newsfeed);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Notification_CreateURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response == null) {
                    //Toast.makeText(context, "Like failed!", Toast.LENGTH_SHORT).show();
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
                hashMap.put("id_newsfeed", noti_newsfeed);
                hashMap.put("id_user", noti_id_user);
                hashMap.put("type", String.valueOf(type));
                return hashMap;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void Like(Newsfeed newsfeed) {
        final String like_id_user = String.valueOf(id_user);
        final String like_newsfeed = String.valueOf(newsfeed.getId_newsfeed());
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Like_CreateURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response == null) {
                    Toast.makeText(context, "Like Fail!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, response , Toast.LENGTH_SHORT).show();
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
                hashMap.put("id_newsfeed", like_newsfeed);
                hashMap.put("id_user", like_id_user);
                return hashMap;
            }
        };
        requestQueue.add(stringRequest);
    }

    public class ViewHolder {
        CircleImageView avatar;
        ImageView image, comment;
        LinearLayout like;
        TextView address, issue, status, username, like_count;
    }
}
