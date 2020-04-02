package edu.psu.lionconnect.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import edu.psu.lionconnect.R;

public class RecyclerFeedDataAdapter extends RecyclerView.Adapter<RecyclerFeedDataAdapter.CardViewHolder>{


    private List<feedDataStructure> partFeed;

    public RecyclerFeedDataAdapter(List<feedDataStructure> feed){
        partFeed = feed;
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView text;
        public TextView user;
        public CardViewHolder(View itemView){
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.feedImage);
            text = (TextView) itemView.findViewById(R.id.feedText);
            user = (TextView) itemView.findViewById(R.id.feedUser);
        }
    }

    @Override
    public RecyclerFeedDataAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View cardView = inflater.inflate(R.layout.cardview_feed, parent, false);

        // Return a new holder instance
        CardViewHolder viewHolder = new CardViewHolder(cardView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerFeedDataAdapter.CardViewHolder viewHolder, int position) {
        // Get the data model based on position
        feedDataStructure listItem = partFeed.get(position);

        // Set item views based on your views and data model
        (viewHolder.text).setText(listItem.getText().toString());
        (viewHolder.image).setImageResource(listItem.getImage_path());
        (viewHolder.user).setText(listItem.getUser());
    }
    @Override
    public int getItemCount() {
        return partFeed.size();
    }


}

