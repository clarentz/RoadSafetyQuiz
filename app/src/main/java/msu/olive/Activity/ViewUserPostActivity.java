package msu.olive.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import msu.olive.Adapter.CommentAdapter;
import msu.olive.Model.Comment;
import msu.olive.Model.Userfeed;
import msu.olive.R;
import msu.olive.Server.Server;

public class ViewUserPostActivity extends AppCompatActivity {
    ImageView viewpost_image, viewpost_like, viewpost_comment, viewpost_avatar;
    TextView viewpost_status, viewpost_username, viewpost_address, viewpost_issue,  viewpost_likecount;
    Userfeed userfeed;
    int id_newsfeed;
    ListView listView;
    ArrayList<Comment> comments;
    private CommentAdapter commentAdapter;
    int id_comment = 0;
    int id_commentator = 0;
    String comment_username = "";
    String comment_content = "";

    Comment comment = new Comment(12,3,"jay", "hehe");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_post);
        bumbum();
    }

    private void bumbum() {
        listView = (ListView) findViewById(R.id.viewuserpost_comment_list);
        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(comments, getApplicationContext());
        listView.setAdapter(commentAdapter);
        viewpost_image = (ImageView) findViewById(R.id.viewuserpost_image);
        //viewpost_avatar = (ImageView) findViewById(R.id.viewuserpost_avatar);
        viewpost_status = (TextView) findViewById(R.id.viewuserpost_status);
        //viewpost_username = (TextView) findViewById(R.id.viewuserpost_username);
        viewpost_address = (TextView) findViewById(R.id.viewuserpost_area);
        viewpost_issue = (TextView) findViewById(R.id.viewuserpost_issue);
        viewpost_likecount = (TextView) findViewById(R.id.viewuserpost_like_count);
        // buidling a post
        userfeed = (Userfeed) getIntent().getBundleExtra("key").getSerializable("data");
        id_newsfeed = userfeed.getId_newsfeed();
        if (!TextUtils.isEmpty(userfeed.getStatus_newsfeed())) {
            viewpost_status.setText(userfeed.getStatus_newsfeed().toString());
            viewpost_status.setVisibility(View.VISIBLE);
        } else {
            viewpost_status.setVisibility(View.GONE);
        }

//        if (userfeed.getAvatar_newsfeed().length() > 0) {
//            Picasso.with(getApplicationContext()).load(Server.ImageURL + userfeed.getAvatar_newsfeed()).placeholder(R.drawable.ic_avatar).into(viewpost_avatar);
//        }
//        viewpost_username.setText(userfeed.getUsername_newsfeed().toString());
//        if (!TextUtils.isEmpty(userfeed.getRoadname_newsfeed()) || !TextUtils.isEmpty(userfeed.getAdminarea_newsfeed()) || !TextUtils.isEmpty(userfeed.getSubadminarea_newsfeed())) {
//            viewpost_address.setText(userfeed.getRoadname_newsfeed().toString() + ", " + userfeed.getSubadminarea_newsfeed() + ", " + userfeed.getAdminarea_newsfeed());
//            viewpost_address.setVisibility(View.VISIBLE);
//        } else {
//            viewpost_address.setVisibility(View.GONE);
//        }

        if (!TextUtils.isEmpty(userfeed.getIssue_newsfeed())) {
            viewpost_issue.setText(userfeed.getIssue_newsfeed().toString());
            viewpost_issue.setVisibility(View.VISIBLE);
        } else {
            viewpost_issue.setVisibility(View.GONE);
        }
        //viewpost_status.setText(newsfeed.getStatus_newsfeed());
        //viewpost_address.setText(newsfeed.getRoadname_newsfeed().toString() + ", " + newsfeed.getSubadminarea_newsfeed()  + ", " + newsfeed.getAdminarea_newsfeed());
        //viewpost_issue.setText(newsfeed.getIssue_newsfeed().toString());
        //viewpost_likecount.setText(newsfeed.getLike_newsfeed());
        if (userfeed.getLike_newsfeed() != 0) {
            viewpost_likecount.setText(String.valueOf(userfeed.getLike_newsfeed()));
            viewpost_likecount.setVisibility(View.VISIBLE);
        } else {
            viewpost_likecount.setVisibility(View.GONE);
        }
        if (userfeed.getImage_newsfeed().length() > 0) {
            Picasso.with(getApplicationContext()).load(Server.ImageURL + userfeed.getImage_newsfeed()).placeholder(R.mipmap.ic_image_thumbnail).into(viewpost_image);
        }
        pushData(id_newsfeed);
    }

    private void pushData(final int id_newsfeed) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Comment_GetURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response == null) {
                    Toast.makeText(ViewUserPostActivity.this, "No comment available", Toast.LENGTH_SHORT).show();
                } else {
                    readData(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewUserPostActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id", Integer.toString(id_newsfeed));
                return hashMap;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void readData(String response) {
        String data = response;
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                id_comment = jsonObject.getInt("id_comment");
                comment_username = jsonObject.getString("username");
                comment_content = jsonObject.getString("comment_content");
                id_commentator = jsonObject.getInt("id_user");
                Comment comment = new Comment(id_comment, id_commentator, comment_username, comment_content);
                comments.add(comment);
                commentAdapter.notifyDataSetChanged();
                // Toast.makeText(getApplicationContext(), "bop", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

//    private void readLike(String response) {
//        String data = response;
//        try {
//            JSONObject jsonObject = new JSONObject(data);
//            id_comment = jsonObject.getInt("id");
//            int count = 0;
//            count = jsonObject.getInt("like_count");
//            viewpost_likecount.setText(" " + count + "people liked this post");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }