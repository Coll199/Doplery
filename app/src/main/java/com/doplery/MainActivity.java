package com.doplery;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int RC_LoginActivity = 0;
    private static final int RC_AdvertActivity = 100;

    private Context context;


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private FirestoreRecyclerAdapter<Advert, CardViewHolder> firestoreRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        //firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAdvertActivityIntent();
            }
        });

        //adverts in ascending time order (first is oldest)
        CollectionReference advertsRef = db.collection("adverts");
        final Query query = advertsRef.orderBy("timeCreated",Query.Direction.DESCENDING).limit(5);

        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        advertsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                FirestoreRecyclerOptions<Advert> options = new FirestoreRecyclerOptions.Builder<Advert>()
                        .setQuery(query, new SnapshotParser<Advert>() {
                            @NonNull
                            @Override
                            public Advert parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                                Advert finalAdvert = new Advert();
                                finalAdvert = snapshot.toObject(Advert.class);
                                ArrayList<String> finalPaths = new ArrayList<>();
                                for(String file:finalAdvert.getFiles()){
                                    Log.d(TAG,"file="+file);
                                    finalPaths.add(snapshot.getId()+"/"+file);
                                }
                                finalAdvert.setFiles(finalPaths);

                                Log.d(TAG,"snapshotId="+snapshot.getId());
                                return finalAdvert;
                            }
                        })
                        .build();
                firestoreRecyclerAdapter =
                        new FirestoreRecyclerAdapter<Advert, CardViewHolder>(options) {
                            @Override
                            protected void onBindViewHolder(@NonNull CardViewHolder cardViewHolder, int i, @NonNull Advert advert) {
                                cardViewHolder.setCard(context,advert);
                                for(String file:advert.getFiles()){
                                    Log.d(TAG,"file(in OnBindViewHolder)="+file);
                                }
                            }

                            @NonNull
                            @Override
                            public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_card, parent, false);
                                return new CardViewHolder(view);
                            }

                            @Override
                            public int getItemCount(){
                                return super.getItemCount();
                            }
                        };
                recyclerView.setAdapter(firestoreRecyclerAdapter);
                firestoreRecyclerAdapter.startListening();

            }
        });


        //check if user is signed in if not start LoginActivity
        if (mAuth.getCurrentUser() == null) {
            createLoginActivityIntent();
        }
        else
            updateUI(mAuth.getCurrentUser());


    }
    private void createLoginActivityIntent(){
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivityForResult(loginIntent, RC_LoginActivity);
    }

    private void createAdvertActivityIntent(){
        Intent advertIntent = new Intent(this, AdvertActivity.class);
        startActivityForResult(advertIntent, RC_AdvertActivity);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(firestoreRecyclerAdapter != null)
            firestoreRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(firestoreRecyclerAdapter != null)
            firestoreRecyclerAdapter.stopListening();
    }

    public void updateUI(FirebaseUser user){
        if(user != null){
            //Change UI if user is logged in
            Log.d(TAG,mAuth.getCurrentUser().getEmail());
            DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getEmail());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            User user = document.toObject(User.class);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == RC_LoginActivity) {
            if (resultCode == RESULT_OK) {
                updateUI(mAuth.getCurrentUser());
            }
            else{
                finish();
            }
        }
        if (requestCode == RC_AdvertActivity && resultCode == RESULT_OK) {
            Log.d(TAG, "returned from AdvertActivity (images uploaded ok)");
        }
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            Log.d(TAG,"Long pressed back button");
            mAuth.signOut();
            finish();
        }
        return super.onKeyLongPress(keyCode, event);
    }

}
