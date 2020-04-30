package edu.psu.lionconnect;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class SearchableActivity extends AppCompatActivity {

    private static final String TAG = "SearchTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        Log.d(TAG, "Inside Searchable activity");

        Intent intent = getIntent();

        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchFriends(query);
        }
    }

    private void searchFriends(String query){
        Toast.makeText(SearchableActivity.this,query,
                Toast.LENGTH_SHORT).show();

        ArrayList<String> result = new ArrayList<>();
        result.add("Ashish");
        result.add("Suman");

        Intent intent = new Intent(this, SearchActivity.class);
        intent.putStringArrayListExtra("search_result", result);
        startActivity(intent);
    }
}
