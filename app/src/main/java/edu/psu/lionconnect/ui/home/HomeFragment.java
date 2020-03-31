package edu.psu.lionconnect.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import edu.psu.lionconnect.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    String tempUsername = "testUser1";
    Parcelable mListState;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView textView = root.findViewById(R.id.text_home);

        homeViewModel = new HomeViewModel(root, getActivity());

        homeViewModel.makeFeed();

        return root;
    }

//    private class fillFeed extends AsyncTask<,Void,Void>{
//
//        @Override
//        protected Object doInBackground(Object[] objects) {
//            return null;
//        }
//    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mListState = homeViewModel.llm.onSaveInstanceState();
        outState.putParcelable("list", mListState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null){
            mListState = savedInstanceState.getParcelable("list");
            homeViewModel.llm.onRestoreInstanceState(mListState);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListState != null) {
            homeViewModel.llm.onRestoreInstanceState(mListState);
        }
    }
}