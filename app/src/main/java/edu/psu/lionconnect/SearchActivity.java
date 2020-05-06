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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private FirebaseAuth mAuth;
    private Button follow;
    private Button unFollow;
    private Button viewProfile;
    private String target_userID;
    private String target_userName;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        final String[] userAttributes = {"UserName", "about_me", "campus", "degree", "email", "friends", "major", "name", "posts"};

        recyclerView = (RecyclerView) findViewById(R.id.search_results);
        Log.d(TAG, "Inside Search activity");

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) findViewById(R.id.search_widget);
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Buttons
        follow = findViewById(R.id.follow);
        unFollow = findViewById(R.id.unfollow);
        viewProfile = findViewById(R.id.view_profile);

        // If search performed
        if(getIntent().hasExtra("search_result")){

            // Unsuccessful search
            if(getIntent().hasExtra("HideButtons")){
                follow.setVisibility(View.GONE);
                unFollow.setVisibility(View.GONE);
                viewProfile.setVisibility(View.GONE);
            }
            //Successful search
            else{
                target_userID = getIntent().getStringExtra("UID");
                //target_userName = findViewById(R.id.feedText).toString();

                if(user.getUid().equalsIgnoreCase(target_userID)) {
                    // user trying to follow own acc
                    // code to deactivate follow button
                    Log.d(TAG, "no recursion");
                    follow.setVisibility(View.VISIBLE);
                    follow.setEnabled(false);
                }

                check_following(target_userID, 0);

                HashMap<String, String> searchedUserDetails = new HashMap<>();

                // creating a map with searched user details
                for(String attr: userAttributes){
                    searchedUserDetails.put(attr, getIntent().getStringExtra(attr));
                }

                // display the search results
                displaySearchResult(searchedUserDetails);
            }
        }
        // Search not yet performed
        else{
            // hiding button on search page: initial state
            follow.setVisibility(View.GONE);
            unFollow.setVisibility(View.GONE);
            viewProfile.setVisibility(View.GONE);
        }
    }

    private void check_following(String targetUserId, final int deleteFlag){
        /*
            If deleteFlag == 1, un-follow the targetUserId
         */

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
                        Object myFollowings = myDetails.get("follows");
                        Map<String, Object> myFollowigs = (Map<String, Object>) myFollowings;
                        Log.d(TAG, myFollowings.toString()); // {testUser=true, BT0kXnKZBOWabDlGp9iaDbHvhWI3=true}
                        if(myFollowigs.containsKey(fin_targetUserId)){
                            Log.d(TAG,"following found");
                            if(deleteFlag==1){
                                myFollowigs.remove(fin_targetUserId);
                                myDetails.put("follows",myFollowigs);
                                usrRef.document(user.getUid()).set(myDetails);

                                // removing current user from target user followed by - start
                                final DocumentReference targetRef = fsInstance.collection("users").document(target_userID);
                                targetRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            Map<String, Object> target_details = (Map<String, Object>) task.getResult().getData();
                                            Map<String, Object> target_followedBy = (Map<String, Object>) task.getResult().get("followedBy");
                                            target_followedBy.remove(user.getUid());
                                            target_details.put("followedBy", target_followedBy);
                                            targetRef.set(target_details);
                                        }
                                    }
                                });
                                // end
                                follow.setVisibility(View.VISIBLE);
                                unFollow.setVisibility(View.GONE);
                                viewProfile.setVisibility(View.VISIBLE);
                            }
                            else if(deleteFlag==0){
                                follow.setVisibility(View.GONE);
                                unFollow.setVisibility(View.VISIBLE);
                                viewProfile.setVisibility(View.VISIBLE);
                            }
                        }
                        else {
                            Log.d(TAG,"following not found");
                            follow.setVisibility(View.VISIBLE);
                            unFollow.setVisibility(View.GONE);
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

        RecyclerView.Adapter mAdapter = new SearchAdapter(search_result);
        recyclerView.setAdapter(mAdapter);
    }

    // Follow user
    private void follow_user(String target_userID){
        final FirebaseUser user = mAuth.getCurrentUser();

            fsInstance = FirebaseFirestore.getInstance();
            final CollectionReference usrRef = fsInstance.collection("users");
            Map<String, Map<String, Object>> followData = new HashMap<>();
            Map<String, Map<String, Object>> followedByData = new HashMap<>();
            Map<String, Object> follow_map = new HashMap<>();
            Map<String, Object> followedBy = new HashMap<>();
            follow_map.put(target_userID, true);
            followedBy.put(user.getUid(), true);
            followData.put("follows", follow_map);
            followedByData.put("followedBy", followedBy);
            usrRef.document(user.getUid()).set(followData, SetOptions.merge());
            usrRef.document(target_userID).set(followedByData, SetOptions.merge());

            follow.setVisibility(View.GONE);
            unFollow.setVisibility(View.VISIBLE);
            viewProfile.setVisibility(View.VISIBLE);
    }

    // Unfollow user
    private void unfollow_user(String target_userID){
        check_following(target_userID, 1);
    }

    // Display user details
    private void displayProfileDetails(String target_userID){

        final Intent intent = new Intent(this, ViewProfileActivity.class);
        intent.putExtra("targetUID",target_userID);
        startActivity(intent);
    }

    // Respond to button clicks
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.follow) {
            follow_user(target_userID);
        }else if (i == R.id.unfollow) {
            unfollow_user(target_userID);
        } else if (i == R.id.view_profile) {
            displayProfileDetails(target_userID);
        } else if(i == R.id.BackToHome){
            Intent intent = new Intent(this, bottomNavActivity.class);
            startActivity(intent);
        }
    }
}
