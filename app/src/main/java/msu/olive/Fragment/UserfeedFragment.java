package msu.olive.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msu.olive.Activity.ViewPostActivity;
import msu.olive.Activity.ViewUserPostActivity;
import msu.olive.Adapter.UserfeedAdapter;
import msu.olive.Model.User;
import msu.olive.Model.Userfeed;
import msu.olive.R;
import msu.olive.Server.Server;

public class UserfeedFragment extends Fragment {
//    SwipeRefreshLayout refreshLayout1;
    //  If implement Swipe refresh remember to edit the layout
    ListView listView;
    ArrayList<Userfeed> userfeeds;
    UserfeedAdapter userfeedAdapter;
    int id_newsfeed = 0;
    int id_user = 0;
    int id_userfeed = 0;
    String userfeed_status = "";
    String userfeed_image = "";
    String userfeed_roadname = "";
    String userfeed_adminarea = "";
    String userfeed_subadminarea = "";
    String userfeed_issue = "";
    int like_count = 0;

    public UserfeedFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userfeed, container, false);
        id_user = getArguments().getInt("id_user");
        listView = (ListView) view.findViewById(R.id.userfeed_list);
//        refreshLayout1 = (SwipeRefreshLayout) view.findViewById(R.id.newsfeed_refresh);
//        refreshLayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        id_user = getArguments().getInt("id_user");
//                        userfeeds = new ArrayList<>();
//                        pushData();
//                        userfeedAdapter = new UserfeedAdapter(userfeeds, getContext(), id_user);
//                        listView.setAdapter(userfeedAdapter);
//                        refreshLayout1.setRefreshing(false);
//                    }
//                }, 2500);
//            }
//        });
        userfeeds = new ArrayList<>();
        userfeedAdapter = new UserfeedAdapter(userfeeds, getContext(), id_user);
        pushData();
        listView.setAdapter(userfeedAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //getting data cross-activities with Intent
                Intent intent = new Intent(getContext(), ViewUserPostActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", (Serializable) parent.getAdapter().getItem(position));
                intent.putExtra("key", bundle);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final String[] strings =  new String[] {"Delete", "Edit"};
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item, strings);
                builder.setTitle("Options");
                builder.setIcon(R.drawable.ic_editor);
                builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Userfeed userfeed = (Userfeed) parent.getAdapter().getItem(position);
                            deleteData(userfeed.getId_newsfeed());
                        } else {
                            Userfeed userfeed = (Userfeed) parent.getAdapter().getItem(position);
                            updateData(userfeed.getId_newsfeed(), userfeed.getStatus_newsfeed());
                        }
                    }
                });
                builder.show();
                return true;
            }
        });
        return view;
    }

    private void updateData(final int id_newsfeed, final String s) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setTitle("Post editor");
        dialog.setContentView(R.layout.x_post_editor_dialog);
        dialog.show();
        final EditText userfeed_status_update = (EditText) dialog.findViewById(R.id.userfeed_content_update);
        userfeed_status_update.setText(s);
        Button button = (Button) dialog.findViewById(R.id.userfeed_button_update);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Userfeed_UpdateURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response == null) {
                            Toast.makeText(getContext(), "Update post failed! Please try again!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Loading...", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id_newsfeed", String.valueOf(id_newsfeed));
                        hashMap.put("status", userfeed_status_update.getText().toString());
                        return hashMap;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });
    }

    private void deleteData(final int id_newsfeed) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest =  new StringRequest(Request.Method.POST, Server.Userfeed_DeleteURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response == null) {
                    Toast.makeText(getContext(), "Delete post failed! Please try again!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Deleting...", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id_newsfeed", Integer.toString(id_newsfeed));
                return hashMap;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void pushData() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Userfeed_GetURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("0")) {
                    Toast.makeText(getContext(), "No data available", Toast.LENGTH_SHORT).show();
                } else {
                    readData(response, id_user);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id_user", String.valueOf(id_user));
                return hashMap;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void readData(String response, int id_user) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                id_newsfeed = jsonObject.getInt("id");
                id_userfeed = jsonObject.getInt("id_user");
                userfeed_status = jsonObject.getString("status");
                userfeed_image = jsonObject.getString("url_image");
                userfeed_roadname = jsonObject.getString("road_name");
                userfeed_adminarea = jsonObject.getString("admin_area");
                userfeed_subadminarea = jsonObject.getString("sub_admin_area");
                userfeed_issue = jsonObject.getString("issue");
                like_count = jsonObject.getInt("like_count");
                Log.d("tagconvertstr", "["+response +"]");
                userfeeds.add(new Userfeed(id_newsfeed, id_userfeed, userfeed_status, userfeed_image, userfeed_roadname, userfeed_adminarea, userfeed_subadminarea, userfeed_issue, like_count));
                userfeedAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}