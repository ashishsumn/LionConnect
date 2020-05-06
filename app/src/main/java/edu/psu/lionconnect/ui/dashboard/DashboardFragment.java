package edu.psu.lionconnect.ui.dashboard;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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
import edu.psu.lionconnect.ui.home.EndlessRecyclerViewScrollListener;
import edu.psu.lionconnect.ui.home.HomeViewModel;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    public int prevTs = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        TextView textView = (TextView) root.findViewById(R.id.text_dash);
        this.dashboardViewModel = new DashboardViewModel(root, getActivity());
        this.dashboardViewModel.recList.addOnScrollListener(new dashBoardEndlessScrollListener(dashboardViewModel) {
            @Override
            public void onLoadMore(DashboardViewModel model) {
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
        String uname = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        HashMap<String, Object> user = new HashMap<>();
        user.put("user", currUser);
        user.put("userName", uname);
        if(prevTs != 0){
            user.put("prevTs", prevTs);
        }
        FirebaseFunctions.getInstance().getHttpsCallable("getMyFeed").
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
                    ArrayList<myPostDataStructure> returnList = new ArrayList<>();
                    Iterator it = feedValueArray.iterator();
                    int a = 0;
                    while (it.hasNext()) {
                        JsonObject postInfo = (JsonObject) it.next();
                        returnList.add(new myPostDataStructure(postInfo.get("photos").getAsString()
                                , postInfo.get("description").getAsString()
                                , postInfo.get("user").getAsString()
                                , postInfo.get("userId").getAsString()
                                , postInfo.get("timestamp").getAsString()));
                        a = a + 1;
                    }
                    prevTs += a;
                    Log.i("Inside not null successListener", "In success");
                    dashboardViewModel.makeFeed(returnList);
                    return;
                }
                Log.i("Inside null successListener", "No data received");
            }
        }).addOnFailureListener((Activity) getActivity(), (OnFailureListener) new OnFailureListener() {
            public void onFailure(Exception e) {
                Log.e("In failure error", e.toString());
            }
        });
    }


}