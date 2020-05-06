package edu.psu.lionconnect.ui.dashboard;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import edu.psu.lionconnect.R;

public class DashboardViewModel extends ViewModel {

    public Context context;
    final String currUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    final FirebaseStorage fbsInstance = FirebaseStorage.getInstance();
    public LinearLayoutManager llm;
    public RecyclerView recList;
    public MyPostRecyclerDataAdapter adapter;
    public View root;
    public boolean isLoading = false, done = false;

    public DashboardViewModel(View view, Context context) {
        this.root = view;
        this.context = context;
        this.recList = (RecyclerView) view.findViewById(R.id.recyclerview_feed_dash);
        this.llm = new LinearLayoutManager(context);
        this.recList.setHasFixedSize(true);
        this.llm.setOrientation(RecyclerView.VERTICAL);
        this.recList.setLayoutManager(this.llm);
        makeAdapterOnCreate();
        makeRecInVis();
    }

    private void makeAdapterOnCreate(){
        adapter = new MyPostRecyclerDataAdapter(new ArrayList<myPostDataStructure>());
        this.recList.setAdapter(adapter);
    }

    public void makeFeed(ArrayList<myPostDataStructure> data) {
        if(data.size() < 10){
            done = true;
            adapter.setDone(done);
            data.add(new myPostDataStructure());
        }
        adapter.removeLoaderFromList(done);
        adapter.addDataToList(data);
        makeRecVis();
    }

    public void makeRecInVis() {
        this.recList.setVisibility(View.GONE);
        this.root.findViewById(R.id.progress_circular_dash).setVisibility(View.VISIBLE);
    }

    public void makeRecVis() {
        this.recList.setVisibility(View.VISIBLE);
        this.root.findViewById(R.id.progress_circular_dash).setVisibility(View.GONE);
    }
    private void optional(ArrayList<myPostDataStructure> data){
        for (int i = 0; i < data.size(); i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(((myPostDataStructure) data.get(i)).getUser());
            String str = " ";
            sb.append(str);
            sb.append(((myPostDataStructure) data.get(i)).getTimeStamp());
            sb.append(str);
            sb.append(((myPostDataStructure) data.get(i)).getPhotoPath());
            System.out.println(sb);
        }
        System.out.println("Is it done : "+done);
    }
}