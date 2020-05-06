package edu.psu.lionconnect;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Map;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private static final String TAG = "CustomAdapter";
    private Map<String,String> mDataset;

    // Provide a reference to the views for each data item
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final TextView emailView;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            textView = (TextView) v.findViewById(R.id.feedText);
            emailView = (TextView) v.findViewById(R.id.feedEmailText);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SearchAdapter(Map<String,String> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View v = inflater.inflate(R.layout.listview_feed, parent, false);

        // Return a new holder instance
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position
//        String[] listItem = mDataset.get(position);

        //"UserName", "about_me", "campus", "degree", "email", "follows", "followedBy", "major", "name", "posts"
        // Set item views based on your views and data model
        (holder.textView).setText(mDataset.get("name"));
        (holder.emailView).setText(mDataset.get("about_me"));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return 1;
    }
}