package edu.psu.lionconnect.ui.notifications;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import edu.psu.lionconnect.R;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private Button button;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        button = (Button) root.findViewById(R.id.profile_edit_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileEditClick(v);
            }

            private void profileEditClick(View v) {
                Intent intent;
                intent = new Intent(v.getContext(), EditProfile.class);
                startActivity(intent);
            }
        });


        final TextView textView = root.findViewById(R.id.tv_profile_email);
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });





        return root;
    }


}