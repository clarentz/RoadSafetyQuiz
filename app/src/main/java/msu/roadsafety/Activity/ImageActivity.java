package msu.roadsafety.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
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

import msu.roadsafety.Adapter.ImageAdapter;
import msu.roadsafety.Model.Image;
import msu.roadsafety.R;
import msu.roadsafety.Server.Server;

public class ImageActivity extends AppCompatActivity {
    ListView listView;
    String image_data;
    private String image_item = "";
    ImageAdapter imageAdapter;
    ArrayList<Image> images;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        image_data = getIntent().getStringExtra("data");
        listView = (ListView) findViewById(R.id.image_list);
        images = new ArrayList<>();
        imageAdapter = new ImageAdapter(images, getApplicationContext());
        listView.setAdapter(imageAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra("image", images.get(position).getUrl());
                intent.putExtra("id_user", getIntent().getIntExtra("id_user", -1));
                startActivity(intent);
                finish();
            }
        });
        pushData(getIntent().getIntExtra("id_user", -1));
    }

    private void pushData(final int id_user) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Image_GetURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response == null) {
                    Toast.makeText(ImageActivity.this, R.string.no_img_available, Toast.LENGTH_SHORT).show();
                } else {
                    readData(response, images);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ImageActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
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

    private void readData(String response, ArrayList<Image> images) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                image_item = jsonObject.getString("image");
                images.add(new Image(image_item));
                imageAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
