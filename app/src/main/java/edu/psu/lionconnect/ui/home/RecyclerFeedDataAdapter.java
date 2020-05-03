package edu.psu.lionconnect.ui.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

import edu.psu.lionconnect.GlideApp;
import edu.psu.lionconnect.R;

public class RecyclerFeedDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;

    private List<feedDataStructure> partFeed;
    private int VIEW_LOADER = 0, VIEW_CARD = 1, VIEW_END = 2;
    private int loaderPosition = 0;
    private boolean done = false;

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

    public class LoadingCardView extends RecyclerView.ViewHolder{
        public ProgressBar pb;

        public LoadingCardView(@NonNull View itemView) {
            super(itemView);
            this.pb = itemView.findViewById(R.id.endFeedTv);
        }
    }

    public class NoMoreFeedCard extends RecyclerView.ViewHolder{
        public TextView tx;

        public NoMoreFeedCard(@NonNull View itemView) {
            super(itemView);
            this.tx = itemView.findViewById(R.id.progressBarLoader);
        }
    }


    public RecyclerFeedDataAdapter(List<feedDataStructure> feed){
        partFeed = feed;
    }

    public void addDataToList(ArrayList<feedDataStructure> data){
        partFeed.addAll(data);
//        notifyItemRangeInserted( page, partFeed.size());
        notifyItemRangeInserted( loaderPosition, partFeed.size());
        Log.i("Length of data ", ""+partFeed.size());
        Log.i("Status ", ""+done);
    }

    public void addLoaderToList(){
        loaderPosition = getItemCount();
        partFeed.add(new feedDataStructure());
        this.notifyItemInserted(loaderPosition);
    }

    public void removeLoaderFromList(boolean status){
        if(status && getItemCount() != 0){
            done = status;
            partFeed.remove(getItemCount());
            notifyItemRemoved(getItemCount());
        }
    }

    public void setDone(boolean status){
        done = status;
    }

    @Override
    public int getItemViewType(int position) {
        if( position == partFeed.size() - 1 && !done){
            return VIEW_LOADER;
        }else if(position == partFeed.size() -1 && done){
            return VIEW_END;
        }else{
            return VIEW_CARD;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == VIEW_CARD) {
            View view = inflater.inflate(R.layout.cardview_feed, parent, false);
            return new CardViewHolder(view);
        }else if(viewType == VIEW_END){
            View view = inflater.inflate(R.layout.cardview_endfeed, parent, false);
            return new NoMoreFeedCard(view);
        }
        else {
            View view = inflater.inflate(R.layout.cardview_loading, parent, false);
            return new LoadingCardView(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        // Get the data model based on position

        if( viewHolder instanceof CardViewHolder){
            feedDataStructure listItem = (feedDataStructure) this.partFeed.get(position);
            final ImageView im = ((CardViewHolder)viewHolder).image;
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            final int height = displayMetrics.heightPixels;
            final int width = displayMetrics.widthPixels;
            ((CardViewHolder)viewHolder).text.setText(listItem.getText().toString());

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
            ((CardViewHolder)viewHolder).user.setText(listItem.getUser());
            ((CardViewHolder)viewHolder).timeStamp.setText(listItem.getTimeStamp());
        }else if( viewHolder instanceof LoadingCardView){

        }else if(viewHolder instanceof NoMoreFeedCard){

        }
    }
    @Override
    public int getItemCount() {
        if(partFeed.size() == 0){
            return 0;
        }else{
            return partFeed.size();
        }
    }


}

