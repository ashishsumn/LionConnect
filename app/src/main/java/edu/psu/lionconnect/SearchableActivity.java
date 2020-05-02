package edu.psu.lionconnect;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Map;

public class SearchableActivity extends AppCompatActivity {

    private static final String TAG = "SearchTAG";
    private FirebaseFirestore fsInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        Log.d(TAG, "Inside Searchable activity");

        Intent intent = getIntent();

        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, query);

            if(query.indexOf('@')!=-1){
                String[] query_split_on_at = query.split("@",2);

                if(query_split_on_at[1].equalsIgnoreCase("psu.edu")){
                    // Search by emailId
                    searchFriends(query, "email");
                }
            }
            else{
                // Search by UserName
                searchFriends(query, "UserName");
            }
        }
    }

    // Search users with userId
    private void searchFriends(String query, String mode){

        final Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("search_result","True");

        fsInstance = FirebaseFirestore.getInstance();
        final CollectionReference usrRef = fsInstance.collection("users");
        Log.d(TAG, mode+" "+query);
        usrRef.whereEqualTo(mode,query).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //no results returned
                            if(task.getResult().size()==0){
                                Toast.makeText(SearchableActivity.this, "No search results found!!!", Toast.LENGTH_LONG).show();
                                intent.putExtra("DisplayButtons", false);
                                startActivity(intent);
                            }

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                    Map<String, Object> userDetails = document.getData();
                                intent.putExtra("UID",document.getId());
//                                int rec_index = 0;

                                for(Map.Entry<String,Object> entry:userDetails.entrySet()){
//                                    intent.putExtra(Integer.toString(rec_index), entry.getValue().toString());
//                                    rec_index++;
                                    intent.putExtra(entry.getKey(), entry.getValue().toString());
                                }
                                Log.d(TAG,"forwarding to display...");
                                startActivity(intent);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
}
