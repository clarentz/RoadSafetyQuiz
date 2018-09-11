package msu.olive.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import msu.olive.Activity.ViewPostActivity;
import msu.olive.Adapter.NewsfeedAdapter;
import msu.olive.Model.Newsfeed;
import msu.olive.R;
import msu.olive.Server.Server;

public class NewsfeedFragment extends Fragment {
    //SwipeRefreshLayout refreshLayout;
    ListView listView;
    ArrayList<Newsfeed> newsfeeds;
    NewsfeedAdapter newsfeedAdapter;
    int id_user;
    int id_newsfeed = 0;
    String newsfeed_avatar = "";
    String newsfeed_username = "";
    String newsfeed_status = "";
    String newsfeed_image = "";
    String newsfeed_roadname = "";
    String newsfeed_adminarea = "";
    String newsfeed_subadminarea = "";
    String newsfeed_issue = "";
    int like_count = 0;

    View footer; //for a progress bar
    Boolean loading;

    FloatingActionButton fab;

    public NewsfeedFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        footer = LayoutInflater.from(getContext()).inflate(R.layout.x_progress_bar, null);

        id_user = getArguments().getInt("id_user");
        listView = (ListView) view.findViewById(R.id.newsfeed_list);
        //refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.newsfeed_refresh);
        newsfeeds = new ArrayList<>();
        newsfeedAdapter = new NewsfeedAdapter(newsfeeds, getContext(), id_user);
        listView.setAdapter(newsfeedAdapter);
        getData();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //getting data cross-activities with Intent
                Intent intent = new Intent(getContext(), ViewPostActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", (Serializable) parent.getAdapter().getItem(position));
                intent.putExtra("key", bundle);
                startActivity(intent);
            }
        });
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_user = getArguments().getInt("id_user");
                newsfeeds = new ArrayList<>();
                getData();
                newsfeedAdapter = new NewsfeedAdapter(newsfeeds, getContext(), id_user);
                listView.setAdapter(newsfeedAdapter);
            }
        });
        /*refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        id_user = getArguments().getInt("id_user");
                        newsfeeds = new ArrayList<>();
                        getData();
                        newsfeedAdapter = new NewsfeedAdapter(newsfeeds, getContext(), id_user);
                        listView.setAdapter(newsfeedAdapter);
                        refreshLayout.setRefreshing(false);
                    }
                }, 2500);
            }
        });
        */
        return view;
    }

    public void getData() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.Newsfeed_GetURL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response != null) { //if there is a response, get them
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            id_newsfeed = jsonObject.getInt("id");
                            newsfeed_avatar = jsonObject.getString("avatar");
                            newsfeed_username = jsonObject.getString("username");
                            newsfeed_status = jsonObject.getString("status");
                            newsfeed_image = jsonObject.getString("url_image");
                            newsfeed_roadname = jsonObject.getString("road_name");
                            newsfeed_adminarea = jsonObject.getString("admin_area");
                            newsfeed_subadminarea = jsonObject.getString("sub_admin_area");
                            newsfeed_issue = jsonObject.getString("issue");
                            like_count = jsonObject.getInt("like_count");
                            newsfeeds.add(new Newsfeed(id_newsfeed, newsfeed_avatar, newsfeed_username, newsfeed_status, newsfeed_image, newsfeed_roadname, newsfeed_adminarea, newsfeed_subadminarea, newsfeed_issue, like_count));
                            //Toast.makeText(getContext(), "status: " + newsfeed_status, Toast.LENGTH_SHORT).show();
                            newsfeedAdapter.notifyDataSetChanged(); //inherit from the BaseAdapter class
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "No Data!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }




}