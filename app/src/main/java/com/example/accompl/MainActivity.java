package com.example.accompl;

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

import com.example.accompl.EditProfile.Account;
import com.example.accompl.EditProfile.EditProfile;
import com.example.accompl.EditProfile.InterestsPage;
import com.example.accompl.Search.SearchPage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.nex3z.flowlayout.FlowLayout;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FlowLayout flowLayout;
    private TextView username, name, about, gender, birthdate, city;
    private ImageView profileSet, btn_edit, profile_image, mini_profile_image, main_psychology, main_business, main_tennis, main_writing, main_blogging, homeicon;
    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference, userRef;
    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("users");
        userRef = reference.child(user.getUid());
        mStorageRef = FirebaseStorage.getInstance().getReference("Images/" + user.getUid());

        profileSet = findViewById(R.id.profileSet);
        btn_edit = findViewById(R.id.btn_edit);
        mAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.username);
        name = findViewById(R.id.name);
        about = findViewById(R.id.edit_about);
        gender = findViewById(R.id.edit_gender);
        birthdate = findViewById(R.id.edit_birthdate);
        city = findViewById(R.id.edit_city);
        profile_image = findViewById(R.id.result_image);
        mini_profile_image = findViewById(R.id.mini_profile_image);
        main_psychology = findViewById(R.id.main_psychology);
        main_business = findViewById(R.id.main_business);
        main_tennis = findViewById(R.id.main_tennis);
        main_writing = findViewById(R.id.main_writing);
        main_blogging = findViewById(R.id.main_blogging);
        flowLayout = findViewById(R.id.flow_layout);
        homeicon = findViewById(R.id.homeicon);

        showAllUserData();

        profileSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, Account.class);

                intent.putExtra("username", username.getText().toString().trim());
                startActivity(intent);
            }
        });

        homeicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchPage.class);
                startActivity(intent);
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditProfile.class);
                startActivity(intent);
            }
        });

        flowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InterestsPage.class);
                startActivity(intent);
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
                    Toast.makeText(MainActivity.this, "Error Occured", Toast.LENGTH_LONG).show();
                }
            });
        }catch(IOException e){
            e.printStackTrace();
        }

        FirebaseUser user = mAuth.getCurrentUser();
        userRef = firebaseDatabase.getReference().child("users").child(user.getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String interests = dataSnapshot.child("interests").getValue(String.class);
                if(interests != null && !interests.equals(" ")){
                    String[] strSplit = interests.split(",");
                    for(String i : strSplit){
                        if(i.equals("1")){
                            main_psychology.setVisibility(View.VISIBLE);
                        }else if(i.equals("2")){
                            main_business.setVisibility(View.VISIBLE);
                        }else if(i.equals("3")){
                            main_tennis.setVisibility(View.VISIBLE);
                        }else if(i.equals("4")){
                            main_writing.setVisibility(View.VISIBLE);
                        }else if(i.equals("5")){
                            main_blogging.setVisibility(View.VISIBLE);
                        }
                    }
                }
                username.setText(dataSnapshot.child("username").getValue(String.class));
                name.setText(dataSnapshot.child("name").getValue(String.class));
                about.setText(Html.fromHtml("<b>About: </b>" + dataSnapshot.child("about").getValue(String.class)));
                gender.setText(Html.fromHtml("<b>Gender: </b>" + dataSnapshot.child("gender").getValue(String.class)));
                birthdate.setText(Html.fromHtml("<b>Birthdate: </b>" + dataSnapshot.child("birthdate").getValue(String.class)));
                city.setText(Html.fromHtml("<b>City: </b>" + dataSnapshot.child("city").getValue(String.class)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

//        Intent intent = getIntent();
//
//        username.setText(intent.getStringExtra("username"));
//        name.setText(intent.getStringExtra("name"));
//        interests.setText(intent.getStringExtra("interests"));
//        about.setText(Html.fromHtml("<b>About: </b>" + intent.getStringExtra("about")));
////        about.setText(intent.getStringExtra("about"));
//        gender.setText(Html.fromHtml("<b>Gender: </b>" + intent.getStringExtra("gender")));
//        birthdate.setText(Html.fromHtml("<b>Birthdate: </b>" + intent.getStringExtra("birthdate")));
//        city.setText(Html.fromHtml("<b>City: </b>" + intent.getStringExtra("city")));
    }
}