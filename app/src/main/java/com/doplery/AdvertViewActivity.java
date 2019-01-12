package com.doplery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AdvertViewActivity extends AppCompatActivity {

    private static final String TAG = "AdvertViewActivity";

    private TextView title;
    private TextView price;
    private TextView description;
    private TextView owner;

    private ImageView image;
    private RecyclerView recyclerView;
    private ImageAdapterFirebaseUI adapter;

    //firebase
    private FirebaseStorage storage;
    private FirebaseFirestore db;

    private Advert advert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advert_view);

        //textviews
        title = findViewById(R.id.AdvertViewTextViewTitle);
        price = findViewById(R.id.AdvertViewTextViewPrice);
        description = findViewById(R.id.AdvertViewTextViewDescription);
        owner = findViewById(R.id.AdvertViewTextViewCreatedBy);

        //image = findViewById(R.id.AdvertViewActivityImage);
        recyclerView = findViewById(R.id.AdvertViewActivityRecycler);

        //firebase
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        advert = (Advert) getIntent().getSerializableExtra("advertModel");

        title.setText("Title:"+advert.getTitle());
        price.setText("Price:"+String.valueOf(advert.getPrice()));
        description.setText("Description:"+advert.getDescription());

        adapter = new ImageAdapterFirebaseUI(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setData(advert.getFiles());

        //load image
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference pathReference = storage.getReference().child(advert.getFiles().get(0));
//        Glide.with(this)
//                .load(pathReference)
//                .apply(new RequestOptions().centerCrop().placeholder(R.drawable.image_placeholder).error(R.drawable.image_placeholder))
//                .into(image);



        //get creator name
        DocumentReference docRef = db.collection("users").document(advert.getUserId());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        User user = new User();
                        user = document.toObject(User.class);
                        owner.setText("Posted by:"+user.getFirstName()+" "+user.getLastName());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }
}
