package msu.olive.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import msu.olive.Adapter.ViewPagerAdapter;
import msu.olive.R;
import msu.olive.Server.Server;

public class MainActivity extends AppCompatActivity {
    ImageView main_notification, main_newpost;
    ViewPager pager;
    TabLayout tabLayout;
    TextView main_username;
    CircleImageView main_avatar;
    FragmentManager fragmentManager;
    int id_user;
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        data = getIntent().getStringExtra("data");
        bumbum();
        requestLocationPermission();
        readData(id_user);
    }

    private void requestLocationPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1234);
        }
    }

    private void readData(final int id_user) {
        fragmentManager = getSupportFragmentManager();
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(fragmentManager, id_user);
        pager.setAdapter(viewPagerAdapter);
        viewPagerAdapter.notifyDataSetChanged();
        tabLayout.setupWithViewPager(pager);
        tabLayout.setTabsFromPagerAdapter(viewPagerAdapter);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_newsfeed);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_games);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_userfeed);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_settings);
        main_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                intent.putExtra("id_owner", id_user);
                startActivity(intent);
            }
        });
    }


    private void bumbum() {
        main_newpost = (ImageView) findViewById(R.id.main_newpost);
        main_newpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                intent.putExtra("id_user", id_user);
                startActivity(intent);
            }
        });
        main_notification = (ImageView) findViewById(R.id.main_notification);
        pager = (ViewPager) findViewById(R.id.main_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.main_tablayout);
        main_username = (TextView) findViewById(R.id.main_username);
        main_avatar = (CircleImageView) findViewById(R.id.main_avatar);

        try {
            JSONObject jsonObject = new JSONObject(data);
            id_user = jsonObject.getInt("id");
            main_username.setText(jsonObject.getString("username"));
            if (jsonObject.getString("avatar").length() != 0) {
                Log.i("Image URL: ", Server.ImageURL + jsonObject.getString("avatar"));
                Glide.with(getApplicationContext()).load(Server.ImageURL + jsonObject.getString("avatar")).placeholder(R.mipmap.ic_image_thumbnail).into(main_avatar);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


}
