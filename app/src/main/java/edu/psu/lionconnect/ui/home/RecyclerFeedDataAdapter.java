package edu.psu.lionconnect.ui.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

import edu.psu.lionconnect.GlideApp;
import edu.psu.lionconnect.R;

public class RecyclerFeedDataAdapter extends RecyclerView.Adapter<RecyclerFeedDataAdapter.CardViewHolder>{

    private Context context;
    private List<feedDataStructure> partFeed;

    public class CardViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView text;
        public TextView timeStamp;
        public TextView user;
        public CardViewHolder(View itemView){
            super(itemView);
            this.image = (ImageView) itemView.findViewById(R.id.feedImage);
            this.text = (TextView) itemView.findViewById(R.id.feedText);
            this.user = (TextView) itemView.findViewById(R.id.feedUser);
            this.timeStamp = (TextView) itemView.findViewById(R.id.feedTimestamp);
        }
    }

    public RecyclerFeedDataAdapter(List<feedDataStructure> feed){
        partFeed = feed;
    }

    @Override
    public RecyclerFeedDataAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
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
        feedDataStructure listItem = (feedDataStructure) this.partFeed.get(position);
        final ImageView im = viewHolder.image;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;
        viewHolder.text.setText(listItem.getText().toString());

        GlideApp.with(context).asBitmap()
                .load(listItem.getPhotoPath())
                .placeholder(R.drawable.image1_foreground)
                .error(R.drawable.image1_background)
                .fallback(R.drawable.image1_foreground)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        float scaler = (((float) height) / 2.0f) / ((float) resource.getHeight());
                        float scalerWidth = ((float) width) / ((float) resource.getWidth());
                        System.out.println(scalerWidth);
                        if (scalerWidth < 1.0f) {
                            scaler = scalerWidth;
                        }
                        if (scaler < 1.0f) {
                            im.setImageBitmap(Bitmap.createScaledBitmap(resource, (int) (((float) resource.getWidth()) * scaler), (int) (((float) resource.getHeight()) * scaler), true));
                        } else {
                            im.setImageBitmap(Bitmap.createScaledBitmap(resource, resource.getWidth(), resource.getHeight(), true));
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
        viewHolder.user.setText(listItem.getUser());
        viewHolder.timeStamp.setText(listItem.getTimeStamp());
    }
    @Override
    public int getItemCount() {
        return partFeed.size();
    }


}

