package msu.olive.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import msu.olive.Adapter.NotificationAdapter;
import msu.olive.Model.Notification;
import msu.olive.R;
import msu.olive.Server.Server;

public class NotificationActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<Notification> notifications;
    NotificationAdapter notificationAdapter;
    int id_user;
    ImageView notification_delete;
    String notification_username = "";
    int notification_type = 0;
    int id_newsfeed = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        id_user = getIntent().getIntExtra("id_owner", -1);
        Log.i("Post owner id: ", "" + id_user);
        bumbum();
        pushData(id_user);
    }

    private void pushData(final int id_user) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Notification_GetURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    readData(response);
                } else {
                    Toast.makeText(NotificationActivity.this, R.string.no_noti, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NotificationActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id_owner", String.valueOf(id_user));
                return hashMap;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void readData(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                notification_username = jsonObject.getString("username");
                id_newsfeed = jsonObject.getInt("id_newsfeed");
                notification_type = jsonObject.getInt("type");
                notifications.add(new Notification(notification_username, id_newsfeed, notification_type));
                notificationAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void bumbum() {
        listView = (ListView) findViewById(R.id.notification_list);
        notifications = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(notifications, getApplicationContext());
        listView.setAdapter(notificationAdapter);
        notification_delete = (ImageView) findViewById(R.id.notification_button_delete);
        notification_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNotification();
            }
        });
    }

    private void deleteNotification() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Notification_DeleteURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response == null) {
                    Toast.makeText(NotificationActivity.this, R.string.delete_fail, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NotificationActivity.this, R.string.loading, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("delete_notification", "delete");
                hashMap.put("id_owner", String.valueOf(id_user));
                return hashMap;
            }
        };
        requestQueue.add(stringRequest);
    }

}
