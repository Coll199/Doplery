package com.doplery;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int RC_LoginActivity = 0;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView mLoggedInTextView;
    private Button mSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoggedInTextView = findViewById(R.id.LoggedIn);
        mSignOut = findViewById(R.id.sign_out_button);

        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                createLoginActivityIntent();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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


    @Override
    public void onStart() {
        super.onStart();
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
                            mLoggedInTextView.setText("Welcome, "+user.getFirstName());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
            mLoggedInTextView.setVisibility(View.VISIBLE);
            mSignOut.setVisibility(View.VISIBLE);
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
    }



}
