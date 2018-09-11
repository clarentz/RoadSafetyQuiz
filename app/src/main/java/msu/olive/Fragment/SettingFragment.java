package msu.olive.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import msu.olive.Activity.ChangePasswordActivity;
import msu.olive.Activity.ProfileActivity;
import msu.olive.R;


public class SettingFragment extends Fragment {
    Button btnChangePassword, btnProfile;

    public SettingFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        btnChangePassword = (Button) view.findViewById(R.id.setting_button_changepassword);
        btnProfile = (Button) view.findViewById(R.id.setting_button_profile);
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
                intent.putExtra("id_user", getArguments().getInt("id_user", -1));
                startActivity(intent);
            }
        });
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("id_user", getArguments().getInt("id_user", -1));
                startActivity(intent);
            }
        });
        return view;
    }
}
