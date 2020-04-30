package edu.psu.lionconnect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity...";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        recyclerView = (RecyclerView) findViewById(R.id.search_results);
        Log.d(TAG, "Inside Search activity");

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) findViewById(R.id.search_widget);
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if(getIntent().hasExtra("search_result")){
            Log.d(TAG, "Got some results");
            ArrayList<String> search_result = getIntent().getStringArrayListExtra("search_result");
            if(search_result!=null)
                displaySearchResult(search_result);
        }
    }

    public void displaySearchResult(ArrayList result){
        Log.d(TAG, Integer.toString(result.size()));
        mAdapter = new SearchAdapter(result);
        recyclerView.setAdapter(mAdapter);
    }
}
