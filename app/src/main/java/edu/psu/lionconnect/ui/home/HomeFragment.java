package edu.psu.lionconnect.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import edu.psu.lionconnect.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView textView = root.findViewById(R.id.text_home);

//        homeViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        RecyclerView recList = (RecyclerView) root.findViewById(R.id.recyclerview_feed);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);


        String tempUsername = "";





        ArrayList data = new ArrayList<feedDataStructure>();
        data.add(new feedDataStructure(R.mipmap.image1, "A"));
        data.add(new feedDataStructure(R.mipmap.image1, "B"));
        data.add(new feedDataStructure(R.drawable.image1_background, "C"));
        data.add(new feedDataStructure(R.drawable.image1_background, "D"));
        data.add(new feedDataStructure(R.drawable.image1_background, "E"));
        data.add(new feedDataStructure(R.drawable.image1_background, "F"));



        RecyclerFeedDataAdapter adapter = new RecyclerFeedDataAdapter((data));
        recList.setAdapter(adapter);

        return root;
    }
}