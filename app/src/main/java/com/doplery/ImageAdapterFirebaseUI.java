package com.doplery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ImageAdapterFirebaseUI extends RecyclerView.Adapter<ImageAdapterFirebaseUI.ImageViewHolder> {

    private Context context;
    private List<String> images;
    private LayoutInflater inflater;

    public ImageAdapterFirebaseUI(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        images = new ArrayList<>();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(inflater.inflate(R.layout.item_image_larger, parent, false));
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        final String image = images.get(position);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference pathReference = storage.getReference().child(image);
        Glide.with(context)
                .load(pathReference)
                .apply(new RequestOptions().centerInside().placeholder(R.drawable.image_placeholder).error(R.drawable.image_placeholder))
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void setData(List<String> images) {
        this.images.clear();
        if (images != null) {
            this.images.addAll(images);
        }
        notifyDataSetChanged();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_thumbnail_larger);
        }
    }
}