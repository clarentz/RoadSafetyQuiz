package msu.roadsafety.Activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import msu.roadsafety.R;
import msu.roadsafety.Server.Server;

public class ProfileActivity extends AppCompatActivity {
    private static final String SHARED_PREFRERENCES_NAME = "Info";
    CircleImageView profile_avatar_image_view;
    Button btnUpdate;
    RadioGroup radioGroup;
    RadioButton btnMale, btnFemale;
    EditText profile_dob_edit;
    EditText profile_email_edit;
    int profile_sex = 0;
    int profiel_sex_edit = 0;
    String profile_avatar_url = "";
    String profile_avatar_url_edit = "";
    String profile_email = "";
    String profile_dob = "";
    int id_user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        bumbum();
    }

    private void bumbum() {
        profile_avatar_url_edit = getIntent().getStringExtra("image");
        btnMale = (RadioButton) findViewById(R.id.profile_button_male);
        btnFemale = (RadioButton) findViewById(R.id.profile_button_female);
        profile_avatar_image_view = (CircleImageView) findViewById(R.id.profile_avatar);
        id_user = getIntent().getIntExtra("id_user", -1);
        if (getIntent().getStringExtra("image") != null) {
            if (getIntent().getStringExtra("image").length() > 0) {
                Picasso.with(getApplicationContext()).load(Server.ImageURL +getIntent().getStringExtra("image")).placeholder(R.drawable.ic_avatar).into(profile_avatar_image_view);
            }
        } else {
            pushData(id_user);
        }
        profile_avatar_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ImageActivity.class);
                intent.putExtra("id_user", getIntent().getIntExtra("id_user", -1    ));
                startActivity(intent);
                finish();
            }
        });
        btnUpdate = (Button) findViewById(R.id.profile_button_update);
        profile_dob_edit = (EditText) findViewById(R.id.profile_dob);
        profile_email_edit = (EditText) findViewById(R.id.profile_email);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(id_user);
            }
        });
    }

    private void updateProfile(final int id_user) {
        final String email = profile_email_edit.getText().toString();
        final String dateofbirth = profile_dob_edit.getText().toString();

        if (btnMale.isChecked()) {
            profiel_sex_edit = 1;
        } else if (btnFemale.isChecked()) {
            profiel_sex_edit = 2;
        }
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Profile_UpdateURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response == null) {
                    Toast.makeText(ProfileActivity.this, R.string.update_pro_failed, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, R.string.loading, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProfileActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id_user", String.valueOf(id_user));
                hashMap.put("avatar", profile_avatar_url_edit);
                //hashMap.put("email", email);
                hashMap.put("gender", String.valueOf(profiel_sex_edit));
                hashMap.put("date", dateofbirth);
                Log.i("Update:", Server.ImageURL + profile_avatar_url_edit + id_user);
                return hashMap;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void pushData(final int id_user) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Profile_GetURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response == null) {
                    Toast.makeText(ProfileActivity.this, R.string.no_profile_data, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, R.string.loading, Toast.LENGTH_SHORT).show();
                    try {
                        readData(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProfileActivity.this, "Error: " + error + ". Please select your photo!", Toast.LENGTH_SHORT).show();
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

    private void readData(String s) throws JSONException {
        if (s != null) {
            JSONObject jsonObject =  new JSONObject(s);
            profile_sex = jsonObject.getInt("gender");
            profile_avatar_url = jsonObject.getString("avatar");
            profile_dob = jsonObject.getString("date");
           // profile_email = jsonObject.getString("email");
            if (profile_sex == 1) {
                btnMale.setChecked(true);
            } else if (profile_sex == 2) {
                btnFemale.setChecked(true);
            } else {
                btnFemale.setChecked(false);
                btnMale.setChecked(false);
            }
            if (profile_avatar_url.length() > 0) {
                Picasso.with(getApplicationContext()).load(Server.ImageURL + profile_avatar_url).placeholder(R.mipmap.ic_image_thumbnail).into(profile_avatar_image_view);
                profile_avatar_url_edit = profile_avatar_url;
            }
            profile_email_edit.setText(profile_email);
            profile_dob_edit.setText(profile_dob);
        }
    }


}
