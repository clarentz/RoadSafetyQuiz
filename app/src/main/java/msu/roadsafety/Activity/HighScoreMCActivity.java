package msu.roadsafety.Activity;

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

import java.util.ArrayList;

import msu.roadsafety.Model.HighScore;
import msu.roadsafety.R;
import msu.roadsafety.Server.Server;

public class HighScoreMCActivity extends AppCompatActivity {

    private String username;
    private int score;

    private ListView lvHighScoreMC;
    private ArrayAdapter<HighScore> highScoreArrayAdapter;
    private ArrayList<HighScore> highScoresList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score_mc);

        addControls();
        addEvents();
        getData();

    }

    private void getData() {


        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Server.GetHighScoreMCURL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response != null) {
                    for (int i = 0; i < response.length(); i++) {

                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            username = jsonObject.getString("username");
                            score = jsonObject.getInt("score");
                            highScoresList.add(new HighScore(username, score));
                            highScoreArrayAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("error", e.toString());
                        }

                    }
                } else {
                    Toast.makeText(HighScoreMCActivity.this, "No data", Toast.LENGTH_SHORT).show();
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

    private void addEvents() {

    }

    private void addControls() {
        lvHighScoreMC = findViewById(R.id.lvHighScoreMC);
        highScoresList = new ArrayList<>();
        //highScoreArrayAdapter = new ArrayAdapter<HighScore>(HighScoreMCActivity.this,android.R.layout.simple_list_item_1,highScoresList);
        highScoreArrayAdapter = new ArrayAdapter<HighScore>(HighScoreMCActivity.this, R.layout.x_custom_list_item, highScoresList);
        lvHighScoreMC.setAdapter(highScoreArrayAdapter);
    }
}
