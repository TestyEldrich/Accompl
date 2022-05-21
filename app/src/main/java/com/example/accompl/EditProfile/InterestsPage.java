package com.example.accompl.EditProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.accompl.MainActivity;
import com.example.accompl.R;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class InterestsPage extends AppCompatActivity {

    private FlowLayout list_layout, choosen_layout;
    private ImageView mini_profile_image, choosen_psychology, choosen_business,choosen_tennis,choosen_writing,choosen_blogging, list_psychology, list_business,list_tennis,list_writing,list_blogging, save_button;

    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference, userRef;
    StorageReference mStorageRef;

    ArrayList<Integer> interest = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests_page);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        list_layout = findViewById(R.id.list_layout);
        choosen_layout = findViewById(R.id.choosen_layout);
        mini_profile_image = findViewById(R.id.mini_profile_image);
        choosen_psychology = findViewById(R.id.choosen_psychology);
        choosen_business = findViewById(R.id.choosen_business);
        choosen_tennis = findViewById(R.id.choosen_tennis);
        choosen_writing = findViewById(R.id.choosen_writing);
        choosen_blogging = findViewById(R.id.choosen_blogging);
        list_psychology = findViewById(R.id.list_psychology);
        list_business = findViewById(R.id.list_business);
        list_tennis = findViewById(R.id.list_tennis);
        list_writing = findViewById(R.id.list_writing);
        list_blogging = findViewById(R.id.list_blogging);
        save_button = findViewById(R.id.btn_save);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("users");
        userRef = reference.child(user.getUid());
        mStorageRef = FirebaseStorage.getInstance().getReference("Images/" + user.getUid());

        showAllUserData();

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRef.child("interests").setValue(Arrays.toString(interest.toArray()).replace("[", "").replace("]", "").replace(" ", ""));
                Intent intent = new Intent(InterestsPage.this, MainActivity.class);
                startActivity(intent);
            }
        });

        list_psychology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list_psychology.setVisibility(View.GONE);
                choosen_psychology.setVisibility(View.VISIBLE);
                interest.add(1);
            }
        });

        list_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list_business.setVisibility(View.GONE);
                choosen_business.setVisibility(View.VISIBLE);
                interest.add(2);
            }
        });

        list_tennis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list_tennis.setVisibility(View.GONE);
                choosen_tennis.setVisibility(View.VISIBLE);
                interest.add(3);
            }
        });

        list_writing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list_writing.setVisibility(View.GONE);
                choosen_writing.setVisibility(View.VISIBLE);
                interest.add(4);
            }
        });

        list_blogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list_blogging.setVisibility(View.GONE);
                choosen_blogging.setVisibility(View.VISIBLE);
                interest.add(5);
            }
        });

        choosen_psychology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosen_psychology.setVisibility(View.GONE);
                list_psychology.setVisibility(View.VISIBLE);
                int count = 0;
                for(Integer one_interest : interest){
                    if(one_interest == 1) {
                        interest.remove(count);
                        break;
                    }
                    count++;
                }
            }
        });

        choosen_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosen_business.setVisibility(View.GONE);
                list_business.setVisibility(View.VISIBLE);
                int count = 0;
                for(Integer one_interest : interest){
                    if(one_interest == 2) {
                        interest.remove(count);
                        break;
                    }
                    count++;
                }
            }
        });

        choosen_tennis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosen_tennis.setVisibility(View.GONE);
                list_tennis.setVisibility(View.VISIBLE);
                int count = 0;
                for(Integer one_interest : interest){
                    if(one_interest == 3) {
                        interest.remove(count);
                        break;
                    }
                    count++;
                }
            }
        });

        choosen_writing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosen_writing.setVisibility(View.GONE);
                list_writing.setVisibility(View.VISIBLE);
                int count = 0;
                for(Integer one_interest : interest){
                    if(one_interest == 4) {
                        interest.remove(count);
                        break;
                    }
                    count++;
                }
            }
        });

        choosen_blogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosen_blogging.setVisibility(View.GONE);
                list_blogging.setVisibility(View.VISIBLE);
                int count = 0;
                for(Integer one_interest : interest){
                    if(one_interest == 5) {
                        interest.remove(count);
                        break;
                    }
                    count++;
                }
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
                    (mini_profile_image).setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(InterestsPage.this, "Error Occured", Toast.LENGTH_LONG).show();
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
                            list_psychology.setVisibility(View.GONE);
                            choosen_psychology.setVisibility(View.VISIBLE);
                            interest.add(1);
                        }else if(i.equals("2")){
                            list_business.setVisibility(View.GONE);
                            choosen_business.setVisibility(View.VISIBLE);
                            interest.add(2);
                        }else if(i.equals("3")){
                            list_tennis.setVisibility(View.GONE);
                            choosen_tennis.setVisibility(View.VISIBLE);
                            interest.add(3);
                        }else if(i.equals("4")){
                            list_writing.setVisibility(View.GONE);
                            choosen_writing.setVisibility(View.VISIBLE);
                            interest.add(4);
                        }else if(i.equals("5")){
                            list_blogging.setVisibility(View.GONE);
                            choosen_blogging.setVisibility(View.VISIBLE);
                            interest.add(5);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}