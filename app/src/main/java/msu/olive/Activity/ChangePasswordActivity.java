package msu.olive.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import msu.olive.R;
import msu.olive.Server.Server;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText changepassword_oldpassword, changepassword_newpassword, changepassword_retype;
    Button btnChange;
    private String changepassword_password;
    ProgressDialog loading;
    int id_user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id_user = getIntent().getIntExtra("id_user", -1);
        setContentView(R.layout.activity_change_password);
        bumbum();
    }

    private void bumbum() { // add some controls
        changepassword_oldpassword = (EditText) findViewById(R.id.changepassword_old_password);
        changepassword_newpassword = (EditText) findViewById(R.id.changepassword_new_password);
        changepassword_retype = (EditText) findViewById(R.id.changepass_retype);
        btnChange = (Button) findViewById(R.id.changepass_button_change);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushData(id_user);
            }
        });
    }

    private void pushData(final int id_user) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        loading = ProgressDialog.show(ChangePasswordActivity.this,
                getApplicationContext().getResources().getString(R.string.loading),
                                                    null,
                                                    true,
                                                    true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Profile_GetURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                if (response == null) {
                    Toast.makeText(ChangePasswordActivity.this, R.string.no_profile, Toast.LENGTH_SHORT).show();
                } else {
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
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "Error:" + error, Toast.LENGTH_SHORT).show();
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
            JSONObject jsonObject = new JSONObject(s);
            changepassword_password = jsonObject.getString("password");
            if (changepassword_oldpassword.getText().toString().length() < 0) {
                Toast.makeText(this, R.string.insert_old_pw, Toast.LENGTH_SHORT).show();
            } else if (changepassword_newpassword.getText().toString().length() < 0) {
                Toast.makeText(this, R.string.insert_new_pw, Toast.LENGTH_SHORT).show();
            } else if (changepassword_retype.getText().toString().length() < 0) {
                Toast.makeText(this, R.string.retype_new_pw, Toast.LENGTH_SHORT).show();
            } else if (changepassword_oldpassword.getText().toString().equals(changepassword_password)) {
                if (changepassword_retype.getText().toString().equals(changepassword_newpassword.getText().toString())) {
                    changePassword(changepassword_newpassword.getText().toString());
                } else {
                    Toast.makeText(this, R.string.password_unmatched, Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Toast.makeText(this, R.string.change_pass_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void changePassword(final String s) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        loading = ProgressDialog.show(ChangePasswordActivity.this,
                getApplicationContext().getResources().getString(R.string.loading),
                null,
                true,
                true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Password_UpdateURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                if (response == null) {
                    Toast.makeText(ChangePasswordActivity.this, R.string.update_pw_failed, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "Error:" + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id_user", String.valueOf(id_user));
                hashMap.put("password", s);
                return hashMap;
            }
        };
        requestQueue.add(stringRequest);
    }

}
