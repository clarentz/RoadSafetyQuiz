package msu.olive.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
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

import java.lang.reflect.Array;
import java.util.ArrayList;

import msu.olive.Model.HighScore;
import msu.olive.R;
import msu.olive.Server.Server;

public class HighScoreYNActivity extends AppCompatActivity {

    private String username;
    private int score;

    private ListView lvHighScoreYN;
    private ArrayList<HighScore> highScoreYNs;
    private ArrayAdapter<HighScore> adapterHighScoreYN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score_yn);
        addControls();
        getData();
    }

    private void addControls() {
        lvHighScoreYN = findViewById(R.id.lvHighScoreYN);
        highScoreYNs = new ArrayList<>();
        adapterHighScoreYN = new ArrayAdapter<HighScore>(this, R.layout.x_custom_list_item,highScoreYNs);
        //adapterHighScoreYN = new ArrayAdapter<HighScore>(this, android.R.layout.simple_list_item_1,highScoreYNs);
        lvHighScoreYN.setAdapter(adapterHighScoreYN);

    }

    private void getData() {


        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.GetHighScoreYNURL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response != null) {
                    for (int i = 0; i < response.length(); i++) {

                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            username = jsonObject.getString("username");
                            score = jsonObject.getInt("score");
                            highScoreYNs.add(new HighScore(username, score));
                            adapterHighScoreYN.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("error", e.toString());
                        }

                    }
                } else {
                    Toast.makeText(HighScoreYNActivity.this, "No data", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        requestQueue.add(jsonArrayRequest);
    }

}
