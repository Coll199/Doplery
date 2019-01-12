package com.doplery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import java.io.File;
import java.util.ArrayList;

public class AdvertActivity extends AppCompatActivity {

    private static final String TAG = "AdvertActivity";

    private EditText title;
    private EditText price;
    private EditText description;


    private Button pickImageButton;
    private Button finishButton;

    private RecyclerView recyclerView;
    private ImageAdapter adapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private ArrayList<Image> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advert);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        title = findViewById(R.id.textInputText_title);
        price = findViewById(R.id.textInputText_price);
        description = findViewById(R.id.textInputText_description);

        pickImageButton = findViewById(R.id.button_pickImage);
        finishButton = findViewById(R.id.button_finish);
        recyclerView = findViewById(R.id.recyclerView);

        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchImagePicker();
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAdvertToDatabase();
            }
        });

        adapter = new ImageAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void launchImagePicker(){
        ImagePicker.with(this)
                .setFolderMode(false)
                .setCameraOnly(false)
                .setImageTitle("Gallery")
                .setMultipleMode(true)
                .setSelectedImages(images)
                .setMaxSize(4)
                .setSavePath("Doplery")
                .setBackgroundColor("#212121")
                .setAlwaysShowDoneButton(true)
                .setRequestCode(100)
                .setKeepScreenOn(true)
                .start();
    }

    private void addAdvertToDatabase(){
        Advert advert = new Advert(title.getText().toString().trim(),
                Integer.parseInt(price.getText().toString()),
                description.getText().toString().trim(),
                mAuth.getUid(),
                System.currentTimeMillis() / 1000L);

        ArrayList<String> fileNames = new ArrayList<>();
        for (Image img:images) {
            fileNames.add(img.getName());
        }
        advert.setFiles(fileNames);

        db.collection("adverts")
                .add(advert)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        uploadImages(documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    private void uploadImages(String documentId){
        StorageReference storageRef = storage.getReference();
        UploadTask uploadTask;
        for (Image img:images) {
            Uri file = Uri.fromFile(new File(img.getPath()));
            StorageReference imageRef = storageRef.child(documentId+"/"+file.getLastPathSegment());
            uploadTask = imageRef.putFile(file);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG, "failed to upload:"+exception.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    Log.d(TAG, "successfully uploaded:"+taskSnapshot.getMetadata().getName());
                    if(taskSnapshot.getMetadata().getName().equals(images.get(images.size() - 1).getName())){
                        setResult(RESULT_OK);
                        finish();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            adapter.setData(images);
            Log.d(TAG, "returnValue(references to images)="+images.toString());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
