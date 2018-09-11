package msu.olive.Activity;

import  android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msu.olive.Adapter.CommentAdapter;
import msu.olive.Model.Comment;
import msu.olive.R;
import msu.olive.Server.RequestHandler;
import msu.olive.Server.Server;

//add, reply, update, delete comments
public class CommentActivity extends AppCompatActivity {
    Button btnSend;
    EditText comment_textbox;
    ListView listView;
    ArrayList<Comment> comments;
    private CommentAdapter commentAdapter;
    int id_user;
    int id_newsfeed;
    int id_comment = 0;
    int id_commentator = 0;
    String comment_username = "";
    String comment_content = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        bumbum();
        pushData();
        executeComment();
    }

    private void executeComment() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = comment_textbox.getText().toString();
                if (content.length() != 0) {
                    sendComment(content);
                }
            }
        });
    }

    private void NoticeNotification(int newsfeed, final int status) { //add a notification for the new action on the post
        final String noti_id_user = String.valueOf(id_user);
        final String noti_id_newsfeed = String.valueOf(newsfeed);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Notification_CreateURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response == null) {
                    Toast.makeText(getApplicationContext(), "Comment failed!", Toast.LENGTH_SHORT).show();
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
                hashMap.put("id_newsfeed", noti_id_newsfeed);
                hashMap.put("id_user", noti_id_user);
                hashMap.put("status", String.valueOf(status));
                return hashMap;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void sendComment(final String content) {
        class Push extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;
            RequestHandler requestHandler = new RequestHandler();

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String, String> data = new HashMap<>();
                data.put("comment", content);
                data.put("id_newsfeed", Integer.toString(id_newsfeed));
                //data.put("id_newsfeed", "17");
                data.put("id_user", String.valueOf(id_user));
                String result = requestHandler.sendPostRequest(Server.Comment_CreateURL, data);
                //Toast.makeText(CommentActivity.this, String.valueOf(id_newsfeed), Toast.LENGTH_SHORT).show();
                return result;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading =  ProgressDialog.show(CommentActivity.this, "Loading...", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if (s == null) {
                    Toast.makeText(CommentActivity.this, "Comment failed! Please try again!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CommentActivity.this, "Comment posted!", Toast.LENGTH_SHORT).show();
                    NoticeNotification(id_newsfeed, 1);
                    commentAdapter.notifyDataSetChanged(); //inherit from the BaseAdapter class
                    finish();
                }
            }
        }
        Push push = new Push();
        push.execute();
    }

    private void pushData() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Comment_GetURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response == null) {
                    Toast.makeText(CommentActivity.this, "No comment available", Toast.LENGTH_SHORT).show();
                } else {
                    readData(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CommentActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();
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
                commentAdapter.notifyDataSetChanged();
                // Toast.makeText(getApplicationContext(), "bop", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void bumbum () {
        id_user = getIntent().getIntExtra("id_user", -1);
        id_newsfeed = getIntent().getIntExtra("id_newsfeed", -1);
        btnSend = (Button) findViewById(R.id.comment_button_send);
        comment_textbox = (EditText) findViewById(R.id.comment_content);
        listView = (ListView) findViewById(R.id.comment_list);
        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(comments, getApplicationContext());
        listView.setAdapter(commentAdapter); // link the listView to the Adapter
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // when hold
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);
                if (id_user == id_commentator) { // only the owner of the comment can update or delete the comment
                    final String[] strings = new String[] {"Delete", "Edit"};
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(CommentActivity.this, android.R.layout.select_dialog_item, strings);
                    builder.setTitle("Comment options");
                    builder.setIcon(R.drawable.ic_editor);
                    builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                Comment comment = (Comment) parent.getAdapter().getItem(position);
                                deleteData(comment.getId_comment());
                                finish();
                            } else {
                                Comment comment = (Comment) parent.getAdapter().getItem(position);
                                updateData(comment.getId_comment(), comment.getContent_comment());
                            }
                        }
                    });
                    builder.show();
                } else {
                    final String[] strings = new String[] {"Delete"};
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(CommentActivity.this, android.R.layout.select_dialog_item, strings);
                    builder.setTitle("Comment option");
                    builder.setIcon(R.drawable.ic_editor);
                    builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                Comment comment = (Comment) parent.getAdapter().getItem(position);
                                deleteData(comment.getId_comment());
                                finish();
                            }
                        }
                    });
                    builder.show();
                }
                return true;
            }
        });
    }

    private void deleteData (final int id_comment) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Comment_DeleteURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response == null) {
                    Toast.makeText(CommentActivity.this, "Delete comment failed! Please try again!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CommentActivity.this, "Deleting...", Toast.LENGTH_SHORT).show();
                    // readData(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CommentActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id_comment", String.valueOf(id_comment));
                return hashMap;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void updateData (final int id_comment, final String s) {
        final Dialog dialog = new Dialog(CommentActivity.this);
        dialog.setTitle("Comment editor");
        dialog.setContentView(R.layout.x_comment_editor_dialog);
        dialog.show();
        final EditText comment_content_update = (EditText) dialog.findViewById(R.id.comment_content_update);
        comment_content_update.setText(s);
        Button btnUpdate = (Button) dialog.findViewById(R.id.comment_button_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Server.Comment_UpdateURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response == null) {
                            Toast.makeText(CommentActivity.this, "Update comment failed! Please try again!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CommentActivity.this, "Updating...", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                            finish();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CommentActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id_comment", String.valueOf(id_comment));
                        hashMap.put("comment_content", comment_content_update.getText().toString());
                        return hashMap;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });
    }


}
