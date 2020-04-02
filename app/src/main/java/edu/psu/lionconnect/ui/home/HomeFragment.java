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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

//        getData();

        return root;
    }

    private class feedDataGet extends AsyncTask<String, Void, feedDataStructure[]>{


        @Override
        protected feedDataStructure[] doInBackground(String... strings) {
            return new feedDataStructure[0];
        }
    }


    public void getData(){
        FirebaseFirestore fsInstance = FirebaseFirestore.getInstance();
        FirebaseStorage fbsInstance = FirebaseStorage.getInstance();
        final ArrayList<String> friendList = new ArrayList<>();
        DocumentReference friends = fsInstance.collection("friends")
                                                .document(FirebaseAuth.getInstance()
                                                        .getCurrentUser().getUid());

        friends.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> a = documentSnapshot.getData();

                for(String friend: a.keySet()){
                    friendList.add(friend);
                }
                new feedDataGet().execute();
            }
        });

    }

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