package edu.psu.lionconnect.ui.notifications;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executor;

import edu.psu.lionconnect.MainActivity;
import edu.psu.lionconnect.R;
import io.paperdb.Paper;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private Button button, logout;
    private FirebaseStorage fbsInstance;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUser;
    private ImageView profileImage;
    private StorageReference storageReference;
    private TextView main_fullname, main_campus, main_city, main_about_me, main_degree, main_major;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fAuth = FirebaseAuth.getInstance();
        mCurrentUser = fAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        Paper.init(getContext());

        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        main_fullname =(TextView) root.findViewById(R.id.tv_profile_name);
        main_campus = (TextView) root.findViewById(R.id.tv_profile_campus);
        main_city = (TextView) root.findViewById(R.id.tv_profile_city);
        main_about_me = (TextView) root.findViewById(R.id.tv_profile_bio);
        main_degree = (TextView) root.findViewById(R.id.tv_profile_degree);
        main_major = (TextView) root.findViewById(R.id.tv_profile_major);

        DocumentReference documentReference = fStore.collection("users").document(mCurrentUser.getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    main_fullname.setText(documentSnapshot.getString("name"));
                    main_campus.setText(documentSnapshot.getString("campus"));
                    main_city.setText(documentSnapshot.getString("city"));
                    main_about_me.setText(documentSnapshot.getString("about_me"));
                    main_degree.setText(documentSnapshot.getString("degree"));
                    main_major.setText(documentSnapshot.getString("major"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });




//        button = (Button) root.findViewById(R.id.profile_edit_button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                profileEditClick(v);
//            }
//
//            private void profileEditClick(View v) {
//                Intent intent;
//                   //                intent.putExtra("name",)
//                intent = new Intent(v.getContext(), EditProfile.class);
//
//                //pass the data to Edit Profile to show already existing data
//                intent.putExtra("name",main_fullname.getText().toString());
//                intent.putExtra("campus", main_campus.getText().toString());
//                intent.putExtra("city",main_city.getText().toString());
//                intent.putExtra("about_me",main_about_me.getText().toString());
//                intent.putExtra("degree",main_degree.getText().toString());
//                intent.putExtra("major",main_major.getText().toString());
//                startActivity(intent);
//            }
//        });

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        profileImage = root.findViewById(R.id.profile_page_image);
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

//        logout = (Button) root.findViewById(R.id.profile_logout_button);
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                profileLogout(v);
//            }
//
//            private void profileLogout(View v){
//                Paper.book().destroy();
//                fAuth.getInstance().signOut();
//                getActivity().finish();
//                startActivity(new Intent(getActivity(), MainActivity.class));
//
//            }
//        });

        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.settings_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_edit:
                editProfile();
                Toast.makeText(getContext(), "Edit Profile", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item_logout:
                logout();
                Toast.makeText(getContext(), "Logout", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {Paper.book().destroy();
        fAuth.getInstance().signOut();
        getActivity().finish();
        startActivity(new Intent(getActivity(), MainActivity.class));
    }

    private void editProfile() {
        Intent intent;
        intent = new Intent(getActivity(), EditProfile.class);
        //pass the data to Edit Profile to show already existing data
        intent.putExtra("name",main_fullname.getText().toString());
        intent.putExtra("campus", main_campus.getText().toString());
        intent.putExtra("city",main_city.getText().toString());
        intent.putExtra("about_me",main_about_me.getText().toString());
        intent.putExtra("degree",main_degree.getText().toString());
        intent.putExtra("major",main_major.getText().toString());
        startActivity(intent);
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
////        MenuInflater inflater = getMenuInflater();
////        inflater.inflate(R.menu.settings_menu, menu);
////        return true;
//    }


}
