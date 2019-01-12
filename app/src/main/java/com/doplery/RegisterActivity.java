package com.doplery;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mFirstName;
    private EditText mLastName;
    private Button mFinishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mFirstName = findViewById(R.id.user_FirstName);
        mLastName = findViewById(R.id.user_FamilyName);
        mFinishButton = findViewById(R.id.finishButton);

        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegisterClicked();
            }
        });
    }
    private void onRegisterClicked() {
        mAuth.createUserWithEmailAndPassword(mEmailView.getText().toString(), mPasswordView.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            User user = new User(mFirstName.getText().toString(),mLastName.getText().toString());
                            db.collection("users").document(mAuth.getUid())
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                        }
                                    });
                            Intent data = new Intent(); //not needed
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });
    }
}
