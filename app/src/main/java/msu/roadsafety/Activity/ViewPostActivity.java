package msu.roadsafety.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import msu.roadsafety.Adapter.CommentAdapter;
import msu.roadsafety.Model.Comment;
import msu.roadsafety.Model.Newsfeed;
import msu.roadsafety.R;
import msu.roadsafety.Server.Server;

public class ViewPostActivity extends AppCompatActivity {
    ImageView viewpost_image, viewpost_like, viewpost_comment, viewpost_avatar;
    TextView viewpost_status, viewpost_username, viewpost_address, viewpost_issue,  viewpost_likecount;
    Newsfeed newsfeed;
    int id_newsfeed;
    ListView listView;
    ArrayList<Comment> comments;
    private CommentAdapter commentAdapter1;
    int id_comment = 0;
    int id_commentator = 0;
    String comment_username = "";
    String comment_content = "";

    //Comment comment = new Comment(12,3,"jay", "hehe");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        bumbum();
    }

    private void bumbum() {
        listView = (ListView) findViewById(R.id.viewpost_comment_list);
        comments = new ArrayList<>();
        commentAdapter1 = new CommentAdapter(comments, getApplicationContext());
        listView.setAdapter(commentAdapter1);
        viewpost_image = (ImageView) findViewById(R.id.viewpost_image);
        viewpost_avatar = (ImageView) findViewById(R.id.viewpost_avatar);
        viewpost_status = (TextView) findViewById(R.id.viewpost_status);
        viewpost_username = (TextView) findViewById(R.id.viewpost_username);
        viewpost_address = (TextView) findViewById(R.id.viewpost_area);
        viewpost_issue = (TextView) findViewById(R.id.viewpost_issue);
        viewpost_likecount = (TextView) findViewById(R.id.viewpost_like_count);
        // buidling a post
        newsfeed = (Newsfeed) getIntent().getBundleExtra("key").getSerializable("data");
        id_newsfeed = newsfeed.getId_newsfeed();
        if (!TextUtils.isEmpty(newsfeed.getStatus_newsfeed())) {
            viewpost_status.setText(newsfeed.getStatus_newsfeed().toString());
            viewpost_status.setVisibility(View.VISIBLE);
        } else {
            viewpost_status.setVisibility(View.GONE);
        }
        viewpost_username.setText(newsfeed.getUsername_newsfeed().toString());
        if (!TextUtils.isEmpty(newsfeed.getRoadname_newsfeed()) || !TextUtils.isEmpty(newsfeed.getAdminarea_newsfeed()) || !TextUtils.isEmpty(newsfeed.getSubadminarea_newsfeed())) {
            viewpost_address.setText(newsfeed.getRoadname_newsfeed().toString() + ", " + newsfeed.getSubadminarea_newsfeed() + ", " + newsfeed.getAdminarea_newsfeed());
            viewpost_address.setVisibility(View.VISIBLE);
        } else {
            viewpost_address.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(newsfeed.getIssue_newsfeed())) {
            viewpost_issue.setText(newsfeed.getIssue_newsfeed().toString());
            viewpost_issue.setVisibility(View.VISIBLE);
        } else {
            viewpost_issue.setVisibility(View.GONE);
        }
        //viewpost_status.setText(newsfeed.getStatus_newsfeed());
        //viewpost_address.setText(newsfeed.getRoadname_newsfeed().toString() + ", " + newsfeed.getSubadminarea_newsfeed()  + ", " + newsfeed.getAdminarea_newsfeed());
        //viewpost_issue.setText(newsfeed.getIssue_newsfeed().toString());
        //viewpost_likecount.setText(newsfeed.getLike_newsfeed());
        if (newsfeed.getLike_newsfeed() != 0) {
            viewpost_likecount.setText(String.valueOf(newsfeed.getLike_newsfeed()));
            viewpost_likecount.setVisibility(View.VISIBLE);
        } else {
            viewpost_likecount.setVisibility(View.GONE);
        }
        if (newsfeed.getAvatar_newsfeed().length() > 0) {
            Picasso.with(getApplicationContext()).load(Server.ImageURL + newsfeed.getAvatar_newsfeed()).placeholder(R.drawable.ic_avatar).into(viewpost_avatar);
        }
        if (newsfeed.getImage_newsfeed().length() > 0) {
            Picasso.with(getApplicationContext()).load(Server.ImageURL + newsfeed.getImage_newsfeed()).placeholder(R.mipmap.ic_image_thumbnail).into(viewpost_image);
        }
        pushData(id_newsfeed);
    }

    private void pushData(final int id_newsfeed) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Comment_GetURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response == null) {
                    Toast.makeText(ViewPostActivity.this, R.string.no_comment_available, Toast.LENGTH_SHORT).show();
                } else {
                    readData(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewPostActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();
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
                commentAdapter1.notifyDataSetChanged();
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