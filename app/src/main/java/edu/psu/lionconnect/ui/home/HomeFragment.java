package edu.psu.lionconnect.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.psu.lionconnect.R;

/* renamed from: edu.psu.lionconnect.ui.home.HomeFragment */
public class HomeFragment extends Fragment {
    /* access modifiers changed from: private */
    public HomeViewModel homeViewModel;
    Parcelable mListState;
    int prevTs = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        TextView textView = (TextView) root.findViewById(R.id.text_home);
        this.homeViewModel = new HomeViewModel(root, getActivity());
        this.homeViewModel.recList.addOnScrollListener(new EndlessRecyclerViewScrollListener(homeViewModel) {
            @Override
            public void onLoadMore(HomeViewModel model) {
                model.adapter.addLoaderToList();
                getData();
            }
        });
        getData();
        return root;
    }

    public void getData() {
        FirebaseFirestore instance = FirebaseFirestore.getInstance();
        FirebaseStorage instance2 = FirebaseStorage.getInstance();
        String currUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HashMap<String, Object> user = new HashMap<>();
        user.put("user", currUser);
        if(prevTs != 0){
            user.put("prevTs", prevTs);
        }
        FirebaseFunctions.getInstance().getHttpsCallable("getPostInfo").
                call(user).addOnSuccessListener((Activity) getActivity(), new OnSuccessListener<HttpsCallableResult>() {
            public void onSuccess(HttpsCallableResult httpsCallableResult) {
                if (httpsCallableResult.getData() != null) {
                    PrintStream printStream = System.out;
                    StringBuilder sb = new StringBuilder();
                    sb.append(httpsCallableResult.getData());
                    sb.append(" ");
                    sb.append(httpsCallableResult.getData().getClass());
                    printStream.println(sb.toString());
                    JsonArray feedValueArray = ((JsonObject) new JsonParser().parse(httpsCallableResult.getData().toString())).getAsJsonArray("data");
                    ArrayList<feedDataStructure> returnList = new ArrayList<>();
                    Iterator it = feedValueArray.iterator();
                    int a = 0;
                    while (it.hasNext()) {
                        JsonObject postInfo = (JsonObject) it.next();
                        returnList.add(new feedDataStructure(postInfo.get("photos").getAsString()
                                , postInfo.get("description").getAsString()
                                , postInfo.get("user").getAsString()
                                , postInfo.get("userId").getAsString()
                                , postInfo.get("timestamp").getAsString()));
                        a = a + 1;
                    }
                    prevTs += a;
                    Log.i("Inside not null successListener", "In success");
                    homeViewModel.makeFeed(returnList);
                    return;
                }
                Log.i("Inside null successListener", "No data recieved");
            }
        }).addOnFailureListener((Activity) getActivity(), (OnFailureListener) new OnFailureListener() {
            public void onFailure(Exception e) {
                Log.e("In failure error", e.toString());
            }
        });
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(homeViewModel.llm != null){
            mListState = homeViewModel.llm.onSaveInstanceState();
            outState.putParcelable("list", mListState);
        }
    }

//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mListState = homeViewModel.llm.onSaveInstanceState();
//        outState.putParcelable("list", mListState);
//    }


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
