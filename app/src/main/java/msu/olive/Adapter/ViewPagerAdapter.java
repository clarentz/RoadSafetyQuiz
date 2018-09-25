package msu.olive.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import msu.olive.Fragment.GamesFragment;
import msu.olive.Fragment.NewsfeedFragment;
import msu.olive.Fragment.SettingFragment;
import msu.olive.Fragment.UserfeedFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    String data;
    int id_user;
    String username;

    public ViewPagerAdapter(FragmentManager fragmentManager, int id_user, String username) {
        super(fragmentManager);
        this.data = data;
        this.id_user = id_user;
        this.username = username;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new NewsfeedFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("id_user", id_user);
                fragment.setArguments(bundle);
                break;
            case 1:
                fragment = new GamesFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putInt("id_user", id_user);
                fragment.setArguments(bundle1);
                break;
            case 2:
                fragment = new UserfeedFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putInt("id_user", id_user);
                fragment.setArguments(bundle2);
                break;
            case 3:
                fragment = new SettingFragment();
                Bundle bundle3 = new Bundle();
                bundle3.putInt("id_user", id_user);
                bundle3.putString("username", username);
                fragment.setArguments(bundle3);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
