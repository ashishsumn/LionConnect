package edu.psu.lionconnect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchTAG";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private TextView userName;

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
            Log.d(TAG, "Search Successful");
            String UserName = getIntent().getStringExtra("0");
//            String posts = getIntent().getStringExtra("1");
            String email = getIntent().getStringExtra("2");
//            String friends = getIntent().getStringExtra("3");
            String[] user_det = {UserName,email};
            if(user_det[0]!=null)
                displaySearchResult(user_det);
        }
    }

    public void displaySearchResult(String[] search_result){
        ArrayList<String[]> res = new ArrayList<>();
        res.add(search_result);
        mAdapter = new SearchAdapter(res);
        recyclerView.setAdapter(mAdapter);
    }

    private void follow_user(String target_userName){
        Toast.makeText(SearchActivity.this, "trying to follow "+target_userName, Toast.LENGTH_LONG).show();
    }

    private void displayProfileDetails(String target_userName){
        Toast.makeText(SearchActivity.this, "trying to view "+target_userName, Toast.LENGTH_LONG).show();
    }

    // Respond to button clicks
    public void onClick(View v) {
        userName = findViewById(R.id.feedText);
        int i = v.getId();
        if (i == R.id.follow) {
            follow_user(userName.getText().toString());
        } else if (i == R.id.view_profile) {
            displayProfileDetails(userName.getText().toString());
        }
    }
}
