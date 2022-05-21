package com.example.accompl.Search;

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

import com.example.accompl.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nex3z.flowlayout.FlowLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class FilterPage extends AppCompatActivity {

    private FlowLayout list_layout, choosen_layout;
    private ImageView mini_profile_image, choosenFilter_psychology, choosenFilter_business,choosenFilter_tennis,choosenFilter_writing,choosenFilter_blogging, psychology, business,tennis,writing,blogging, save_button;

    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference, userRef;
    StorageReference mStorageRef;

    ArrayList<Integer> interest = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_page);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        list_layout = findViewById(R.id.list_layout);
        choosen_layout = findViewById(R.id.choosen_layout);
        mini_profile_image = findViewById(R.id.mini_profile_image);
        choosenFilter_psychology = findViewById(R.id.choosenFilter_psychology);
        choosenFilter_business = findViewById(R.id.choosenFilter_business);
        choosenFilter_tennis = findViewById(R.id.choosenFilter_tennis);
        choosenFilter_writing = findViewById(R.id.choosenFilter_writing);
        choosenFilter_blogging = findViewById(R.id.choosenFilter_blogging);
        psychology = findViewById(R.id.filter_psychology);
        business = findViewById(R.id.filter_business);
        tennis = findViewById(R.id.filter_tennis);
        writing = findViewById(R.id.filter_writing);
        blogging = findViewById(R.id.filter_blogging);
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
                Intent intent = new Intent(FilterPage.this, SearchPage.class);
                intent.putExtra("interests", Arrays.toString(interest.toArray()).replace("[", "").replace("]", "").replace(" ", ""));
                startActivity(intent);
            }
        });

        psychology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                psychology.setVisibility(View.GONE);
                choosenFilter_psychology.setVisibility(View.VISIBLE);
                interest.add(1);
            }
        });

        business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                business.setVisibility(View.GONE);
                choosenFilter_business.setVisibility(View.VISIBLE);
                interest.add(2);
            }
        });

        tennis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tennis.setVisibility(View.GONE);
                choosenFilter_tennis.setVisibility(View.VISIBLE);
                interest.add(3);
            }
        });

        writing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writing.setVisibility(View.GONE);
                choosenFilter_writing.setVisibility(View.VISIBLE);
                interest.add(4);
            }
        });

        blogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blogging.setVisibility(View.GONE);
                choosenFilter_blogging.setVisibility(View.VISIBLE);
                interest.add(5);
            }
        });

        choosenFilter_psychology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosenFilter_psychology.setVisibility(View.GONE);
                psychology.setVisibility(View.VISIBLE);
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

        choosenFilter_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosenFilter_business.setVisibility(View.GONE);
                business.setVisibility(View.VISIBLE);
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

        choosenFilter_tennis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosenFilter_tennis.setVisibility(View.GONE);
                tennis.setVisibility(View.VISIBLE);
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

        choosenFilter_writing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosenFilter_writing.setVisibility(View.GONE);
                writing.setVisibility(View.VISIBLE);
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

        choosenFilter_blogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosenFilter_blogging.setVisibility(View.GONE);
                blogging.setVisibility(View.VISIBLE);
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
                    Toast.makeText(FilterPage.this, "Error Occured", Toast.LENGTH_LONG).show();
                }
            });
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}