package com.example.accompl.EditProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.accompl.Login_Registration.LoginActivity;
import com.example.accompl.MainActivity;
import com.example.accompl.R;
import com.example.accompl.Search.SearchPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Account extends AppCompatActivity {

    private TextView username, name, email;
    private ImageView btn_changePass, btn_logOut, btn_deleteAcc, btn_main, profile_image, mini_profile_image, homeicon;

    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference, userRef;
    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("users");
        userRef = reference.child(user.getUid());
        mStorageRef = FirebaseStorage.getInstance().getReference("Images/" + user.getUid());

        username = findViewById(R.id.username);
        name = findViewById(R.id.name);
        email = findViewById(R.id.accountEmail);
        btn_changePass = findViewById(R.id.btn_changePass);
        btn_logOut = findViewById(R.id.btn_logOut);
        btn_deleteAcc = findViewById(R.id.btn_deleteAcc);
        btn_main = findViewById(R.id.btn_main);
        profile_image = findViewById(R.id.result_image);
        mini_profile_image = findViewById(R.id.mini_profile_image);
        homeicon = findViewById(R.id.homeicon);

        showAllUserData();

        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Account.this, MainActivity.class);
                startActivity(intent);
            }
        });

        homeicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Account.this, SearchPage.class);
                startActivity(intent);
            }
        });

        btn_logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(Account.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btn_deleteAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthCredential credential = EmailAuthProvider
                        .getCredential("user@example.com", "password1234");

                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                userRef.removeValue();
                                user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(Account.this, "Your account has been deleted", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(Account.this, LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                            }
                        });


            }
        });
    }

    private void showAllUserData() {
        StorageReference imageRef = mStorageRef.child("profilePic");

        try{
            File localFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), "jpg");
            imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    (profile_image).setImageBitmap(bitmap);
                    (mini_profile_image).setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Account.this, "Error Occured", Toast.LENGTH_LONG).show();
                }
            });
        }catch(IOException e){
            e.printStackTrace();
        }

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username.setText(dataSnapshot.child("username").getValue(String.class));
                name.setText(dataSnapshot.child("name").getValue(String.class));
                email.setText(Html.fromHtml("<b>Email: </b>" + dataSnapshot.child("email").getValue(String.class)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}