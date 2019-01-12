package com.doplery;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CardViewHolder extends RecyclerView.ViewHolder {
    TextView title,price;
    ImageView thumbnail;
    String TAG = "CardViewHolder";

    public CardViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.textView_title);
        price = itemView.findViewById(R.id.textView_price);
        thumbnail = itemView.findViewById(R.id.image_card_thumbnail);
    }

    public void setCard(Context context, final Advert advertModel){
        title.setText(advertModel.getTitle());
        price.setText(String.valueOf(advertModel.getPrice()));
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference pathReference = storage.getReference().child(advertModel.getFiles().get(0));
        Glide.with(context)
                .load(pathReference)
                .apply(new RequestOptions().centerCrop().placeholder(R.drawable.image_placeholder).error(R.drawable.image_placeholder))
                .into(thumbnail);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"Item "+advertModel.getTitle());
                Intent intent = new Intent(view.getContext(), AdvertViewActivity.class);
                intent.putExtra("advertModel", advertModel);
                view.getContext().startActivity(intent);
            }
        });
    }


}
