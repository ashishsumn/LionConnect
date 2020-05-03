package edu.psu.lionconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchTAG";
    private FirebaseFirestore fsInstance;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseAuth mAuth;
    private TextView userName;
    private String UID;
    private Button follow;
    private Button unfollow;
    private Button viewProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mAuth = FirebaseAuth.getInstance();
        final String[] userAttributes = {"UserName", "about_me", "campus", "degree", "email", "friends", "major", "name", "posts"};

        recyclerView = (RecyclerView) findViewById(R.id.search_results);
        Log.d(TAG, "Inside Search activity");

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) findViewById(R.id.search_widget);
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Buttons
        follow = findViewById(R.id.follow);
        unfollow = findViewById(R.id.unfollow);
        viewProfile = findViewById(R.id.view_profile);

        // If search results needs to be displayed
        if(getIntent().hasExtra("search_result")){

            // Hide all buttons on unsuccessful results
            if(getIntent().hasExtra("DisplayButtons")){
                Log.d(TAG,"hiding buttons");
                follow.setVisibility(View.GONE);
                unfollow.setVisibility(View.GONE);
                viewProfile.setVisibility(View.GONE);
            }
            else{
                UID = getIntent().getStringExtra("UID");
                check_following(UID, 0);
                HashMap<String, String> searchedUserDetails = new HashMap<>();
                for(String attr: userAttributes){
                    searchedUserDetails.put(attr, getIntent().getStringExtra(attr));
                }
                Log.d(TAG, searchedUserDetails.toString());
                displaySearchResult(searchedUserDetails);
            }
        }
        else{
            // hiding button on search page
            follow.setVisibility(View.GONE);
            unfollow.setVisibility(View.GONE);
            viewProfile.setVisibility(View.GONE);
        }
    }

    // If the following status
    // id deleteFlag == 1, unfollow the user
    private void check_following(String targetUserId, final int deleteFlag){

        final FirebaseUser user = mAuth.getCurrentUser();

        final String fin_targetUserId = targetUserId;
        final String fin_currentUserName = user.getDisplayName();

        fsInstance = FirebaseFirestore.getInstance();
        final CollectionReference usrRef = fsInstance.collection("users");
        usrRef.whereEqualTo("UserName",fin_currentUserName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                            Map<String, Object> myDetails = document.getData();
                        Object myFollowings = myDetails.get("friends");
                        Map<String, Object> myFollowigs = (Map<String, Object>) myFollowings;
                        Log.d(TAG, myFollowings.toString()); // {testUser=true, BT0kXnKZBOWabDlGp9iaDbHvhWI3=true}
                        if(myFollowigs.containsKey(fin_targetUserId)){
                            Log.d(TAG,"following found");
                            if(deleteFlag==1){
                                myFollowigs.remove(fin_targetUserId);
                                myDetails.put("friends",myFollowigs);
                                usrRef.document(user.getUid()).set(myDetails);
                                follow.setVisibility(View.VISIBLE);
                                unfollow.setVisibility(View.GONE);
                                viewProfile.setVisibility(View.VISIBLE);
                            }
                            else if(deleteFlag==0){
                                follow.setVisibility(View.GONE);
                                unfollow.setVisibility(View.VISIBLE);
                                viewProfile.setVisibility(View.VISIBLE);
                            }
                        }
                        else {
                            Log.d(TAG,"following not found");
                            follow.setVisibility(View.VISIBLE);
                            unfollow.setVisibility(View.GONE);
                            viewProfile.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    // Display search results
    public void displaySearchResult(Map<String,String> search_result){
        mAdapter = new SearchAdapter(search_result);
        recyclerView.setAdapter(mAdapter);
    }

    // Follow user
    private void follow_user(String target_userID){
        final FirebaseUser user = mAuth.getCurrentUser();

        if(user!=null){
            fsInstance = FirebaseFirestore.getInstance();
            final CollectionReference usrRef = fsInstance.collection("users");
            Map<String,Map<String,Object>> friendData = new HashMap<>();
            Map<String,Object> entry = new HashMap<>();
            entry.put(target_userID,true);
            friendData.put("friends",entry);
            usrRef.document(user.getUid()).set(friendData, SetOptions.merge());
        }

        follow.setVisibility(View.GONE);
        unfollow.setVisibility(View.VISIBLE);
        viewProfile.setVisibility(View.VISIBLE);
    }

    // Unfollow user
    private void unfollow_user(String target_userID){
        check_following(target_userID,1);
    }

    // Display user details
    private void displayProfileDetails(String target_userName){
        Toast.makeText(SearchActivity.this, "trying to view "+target_userName, Toast.LENGTH_LONG).show();
    }

    // Respond to button clicks
    public void onClick(View v) {
        userName = findViewById(R.id.feedText);
        int i = v.getId();
        if (i == R.id.follow) {
            follow_user(UID);
        }else if (i == R.id.unfollow) {
            unfollow_user(UID);
        } else if (i == R.id.view_profile) {
            displayProfileDetails(UID);
        } else if(i == R.id.BackToHome){
            Intent intent = new Intent(this, bottomNavActivity.class);
            startActivity(intent);
        }
    }
}
