package com.example.accompl.Search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.accompl.EditProfile.Account;
import com.example.accompl.Chat.ChatActivity;
import com.example.accompl.MainActivity;
import com.example.accompl.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SearchPage extends AppCompatActivity {

    private ImageView filters, mini_profile_image, psychology, business, tennis, writing, blogging, btn_main, profileSet, btn_search;
    private TextView welcome_text;
    private RecyclerView mResultList;
    private EditText search_username;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private TextView popup_username, popup_name, popup_bio, popup_city;
    private ImageView popup_profile_pic, btn_chat_with_person, exit_popup;

    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference, userRef;
    StorageReference mStorageRef;
    FirebaseUser user;
    String interests_filter;
    String[] strSplit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("users");
        userRef = reference.child(user.getUid());
        mStorageRef = FirebaseStorage.getInstance().getReference("Images/" + user.getUid());

        mini_profile_image = findViewById(R.id.mini_profile_image);
        welcome_text = findViewById(R.id.welcome_text);
        psychology = findViewById(R.id.psychology);
        business = findViewById(R.id.business);
        tennis = findViewById(R.id.tennis);
        writing = findViewById(R.id.writing);
        blogging = findViewById(R.id.blogging);
        btn_main = findViewById(R.id.btn_main);
        profileSet = findViewById(R.id.profileSet);
        mResultList = findViewById(R.id.chat_recycler_view);
        btn_search = findViewById(R.id.btn_search);
        filters = findViewById(R.id.filters);
        search_username = findViewById(R.id.search_username);


        mResultList.hasFixedSize();
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        showAllUserData();

        filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchPage.this, FilterPage.class);
                startActivity(intent);
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query = FirebaseDatabase.getInstance()
                        .getReference()
                        .child("users")
                        .orderByChild("name")
                        .startAt(search_username.getText().toString())
                        .endAt(search_username.getText().toString() + "\uf8ff")
                        .limitToLast(50);

                FirebaseRecyclerOptions<Users> options =
                        new FirebaseRecyclerOptions.Builder<Users>()
                                .setQuery(query, Users.class)
                                .build();
                Log.d("/////////////////////", "Search button start");
                firebaseUserSearch(options);
                Log.d("/////////////////////", "Search button end");
            }
        });

        profileSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchPage.this, Account.class);
                startActivity(intent);
            }
        });

        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchPage.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void firebaseUserSearch(FirebaseRecyclerOptions<Users> options) {
        Log.d("/////////////////////", "Inside method");
        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                Log.d("/////////////////////", "View holder created");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_layout, parent, false);
                return new UsersViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {
                Log.d("/////////////////////", "View holder bound");
                holder.setDetailsName(getApplicationContext(), model.getProfilePic(), model.getName(), model.getUid(), model.getInterests());
                Log.d("user uid", user.getUid());
                Log.d("model uid", model.getUid() + "message");
                if(model.getUid().equals(user.getUid()) || model.getName().equals("null")){
                    holder.setIsRecyclable(false);
                    holder.mView.setVisibility(View.GONE);
                }

                if(interests_filter != null && !interests_filter.equals(" ")){
                    strSplit = interests_filter.split(",");
                    for(String i : strSplit){
                        if(model.getInterests() != null){
                            if(!model.getInterests().contains(i)){
                                holder.setIsRecyclable(false);
                                holder.mView.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }
        };
        firebaseRecyclerAdapter.startListening();
        mResultList.setAdapter(firebaseRecyclerAdapter);
        Log.d("/////////////////////", "Adapter set");

    }

    private void showAllUserData() {
        StorageReference imageRef = mStorageRef.child("profilePic");

        try {
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
                    Toast.makeText(SearchPage.this, "Error Occured", Toast.LENGTH_LONG).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        FirebaseUser user = mAuth.getCurrentUser();
        userRef = firebaseDatabase.getReference().child("users").child(user.getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                welcome_text.setText(Html.fromHtml("Hi, " + dataSnapshot.child("name").getValue(String.class)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        Intent intent = getIntent();
        interests_filter = intent.getStringExtra("interests");
        if(interests_filter != null && !interests_filter.equals(" ")){
            strSplit = interests_filter.split(",");
            for(String i : strSplit){
                if(i.equals("1")){
                    psychology.setVisibility(View.VISIBLE);
                }else if(i.equals("2")){
                    business.setVisibility(View.VISIBLE);
                }else if(i.equals("3")){
                    tennis.setVisibility(View.VISIBLE);
                }else if(i.equals("4")){
                    writing.setVisibility(View.VISIBLE);
                }else if(i.equals("5")){
                    blogging.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }
        public void setDetailsName(Context ctx, String userImage, String userName, String userUid, String userInterests){
            TextView user_name = (TextView) mView.findViewById(R.id.result_name);

            user_name.setText(userName);
            Log.d("/////////////////////", "Name set");

            ImageView user_image = (ImageView) mView.findViewById(R.id.result_image);
            user_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createNewContactDialog(userUid);
                }
            });

            Glide.with(ctx).load(userImage).into(user_image);
            Log.d("/////////////////////", "Image set");

            if(userInterests != null && !userInterests.equals(" ")){
                String[] strSplit = userInterests.split(",");
                for(String i : strSplit){
                    if(i.equals("1")){
                        ImageView list_psychology = (ImageView) mView.findViewById(R.id.psychology);
                        list_psychology.setVisibility(View.VISIBLE);
                    }else if(i.equals("2")){
                        ImageView list_business = (ImageView) mView.findViewById(R.id.business);
                        list_business.setVisibility(View.VISIBLE);
                    }else if(i.equals("3")){
                        ImageView list_tennis = (ImageView) mView.findViewById(R.id.tennis);
                        list_tennis.setVisibility(View.VISIBLE);
                    }else if(i.equals("4")){
                        ImageView list_writing = (ImageView) mView.findViewById(R.id.writing);
                        list_writing.setVisibility(View.VISIBLE);
                    }else if(i.equals("5")){
                        ImageView list_blogging = (ImageView) mView.findViewById(R.id.blogging);
                        list_blogging.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    private void createNewContactDialog(String userUid) {
        dialogBuilder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        final View contactPopUpView = getLayoutInflater().inflate(R.layout.user_popup_menu, null);

        popup_profile_pic = (ImageView) contactPopUpView.findViewById(R.id.popup_profile_pic);
        btn_chat_with_person = (ImageView) contactPopUpView.findViewById(R.id.btn_chat_with_person);
        exit_popup = (ImageView) contactPopUpView.findViewById(R.id.exit_popup);
        popup_name = (TextView) contactPopUpView.findViewById(R.id.popup_name);
        popup_bio = (TextView) contactPopUpView.findViewById(R.id.popup_bio);
        popup_username = (TextView) contactPopUpView.findViewById(R.id.popup_username);
        popup_city = (TextView) contactPopUpView.findViewById(R.id.popup_city);

        StorageReference imageRef = FirebaseStorage.getInstance().getReference("Images/" + userUid).child("profilePic");

        try{
            File localFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), "jpg");
            imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    (popup_profile_pic).setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SearchPage.this, "Error Occured", Toast.LENGTH_LONG).show();
                }
            });
        }catch(IOException e){
            e.printStackTrace();
        }

        DatabaseReference popup_user_ref = firebaseDatabase.getReference().child("users").child(userUid);

        popup_user_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                popup_username.setText(dataSnapshot.child("username").getValue(String.class));
                popup_name.setText(dataSnapshot.child("name").getValue(String.class));
                popup_bio.setText(dataSnapshot.child("about").getValue(String.class));
                popup_city.setText(dataSnapshot.child("city").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        btn_chat_with_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchPage.this, ChatActivity.class);
                intent.putExtra("uid", userUid);
                startActivity(intent);
            }
        });

        exit_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialogBuilder.setView(contactPopUpView);
        dialog = dialogBuilder.create();
        dialog.show();

    }
}