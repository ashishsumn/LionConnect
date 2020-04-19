package edu.psu.lionconnect.ui.home;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.psu.lionconnect.R;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    public RecyclerView recList;
    public LinearLayoutManager llm;
    public View root;
    public Context context;

    public HomeViewModel(View view, Context context) {
        this.root = view;
        this.context = context;
        this.recList = (RecyclerView) root.findViewById(R.id.recyclerview_feed);
        this.llm = new LinearLayoutManager(context);
    }

    public void makeFeed(){
        recList.setHasFixedSize(true);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        ArrayList data = new ArrayList<feedDataStructure>();

        for(int i =0;i<10;i++){
            data.add(new feedDataStructure(R.drawable.image1_background, "----------","----------"));
        }

        RecyclerFeedDataAdapter adapter = new RecyclerFeedDataAdapter((data));
        recList.setAdapter(adapter);
    }
}