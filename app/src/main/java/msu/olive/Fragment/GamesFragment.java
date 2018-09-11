package msu.olive.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import msu.olive.Activity.MultipleChoicesGameActivity;
import msu.olive.Activity.YesNoGameActivity;
import msu.olive.Model.Image;
import msu.olive.R;
import msu.olive.Server.Server;


public class GamesFragment extends Fragment {
    ImageView mcGame, ynGame, hiScore;
    int id_user;

    ArrayList<Image> listImages;

    public GamesFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_games, container, false);
        mcGame = (ImageView) view.findViewById(R.id.games_mcgame);
        ynGame = (ImageView) view.findViewById(R.id.games_yngame);
        id_user = getArguments().getInt("id_user");
        listImages = new ArrayList<>();
        getData();
        init();
        return view;
    }

    private void getData() {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Server.Get_ImageURL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Toast.makeText(getContext(),"Data syncing", Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String address = jsonObject.getString("address");
                        String road_name = jsonObject.getString("road_name");
                        String country = jsonObject.getString("country");
                        String sub_admin_area = jsonObject.getString("sub_admin_area");
                        String admin_area = jsonObject.getString("admin_area");
                        String issue = jsonObject.getString("issue");
                        String url = jsonObject.getString("url_image");
                        listImages.add(new Image(address, road_name, sub_admin_area, admin_area, country, issue, url));
                        String a = " " + response.length();
                        String b = " " + listImages.size();
                        //Toast.makeText(getApplicationContext() , a + "\n" + b , Toast.LENGTH_SHORT).show();


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Failed to get data", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);

    }


    private void init() {


        mcGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listImages.size() > 0) {
                    Intent intent = new Intent(getContext(), MultipleChoicesGameActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list question", listImages);
                    intent.putExtra("data", bundle);
                    intent.putExtra("id_user", id_user);
                    startActivity(intent);
                }

                else
                {
                    Toast.makeText(getContext(), "Getting Data", Toast.LENGTH_SHORT).show();
                }

            }
        });
        ynGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listImages.size() > 0)
                {
                    Intent intent = new Intent(getContext(), YesNoGameActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list question", listImages);
                    intent.putExtra("data", bundle);
                    intent.putExtra("id_user", id_user);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getContext(), "Getting Data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
