package com.example.accompl.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.accompl.EditProfile.Account;
import com.example.accompl.MainActivity;
import com.example.accompl.R;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ChatActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference, userRef, personRef;
    StorageReference mStorageRef, personStorageRef, userStorageRef;
    String personUid;

    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private FirebaseFirestore database;
    private FirebaseUser user;

    private ImageView chat_person_image, btn_backToSearch, profileSet, btn_main, homeicon, btn_sendmessage, no_messages;
    private TextView person_name;
    private EditText message_input;

    private RecyclerView chat_recycler_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("users");
        userRef = reference.child(user.getUid());
        mStorageRef = FirebaseStorage.getInstance().getReference("Images/");
        userStorageRef = mStorageRef.child(user.getUid());

        chat_person_image = findViewById(R.id.chat_person_image);
        person_name = findViewById(R.id.person_name);
        btn_backToSearch = findViewById(R.id.btn_backToSearch);
        profileSet = findViewById(R.id.profileSet);
        btn_main = findViewById(R.id.btn_main);
        homeicon = findViewById(R.id.homeicon);
        chat_recycler_view = findViewById(R.id.chat_recycler_view);
        message_input = findViewById(R.id.message_input);
        btn_sendmessage = findViewById(R.id.btn_sendmessage);
        no_messages = findViewById(R.id.no_messages);

        showAllUserData();
        setListeners();
        init();
        listenMessages();

        btn_backToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, SearchPage.class);
                startActivity(intent);
            }
        });

        profileSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, Account.class);
                startActivity(intent);
            }
        });

        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        homeicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, SearchPage.class);
                startActivity(intent);
            }
        });

    }

    private void init(){
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, personUid);
        chat_recycler_view.setAdapter(chatAdapter);
        chat_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        database = FirebaseFirestore.getInstance();
    }

    private void sendMessage(){
        if(!message_input.getText().toString().isEmpty()){
            HashMap<String, Object> message = new HashMap<>();
            message.put("senderId", user.getUid());
            message.put("receiverId", personUid);
            message.put("message", message_input.getText().toString());
            message.put("timestamp", new Date());
            database.collection("chat").add(message);
            message_input.setText(null);
            no_messages.setVisibility(View.GONE);
        }
    }

    private void listenMessages(){
        database.collection("chat")
                .whereEqualTo("senderId", user.getUid())
                .whereEqualTo("receiverId", personUid)
                .addSnapshotListener(eventListener);
        database.collection("chat")
                .whereEqualTo("senderId", personUid)
                .whereEqualTo("receiverId", user.getUid())
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null){
            return;
        }
        if(value != null){
            int count = chatMessages.size();
            Log.d("eventlistener", "Count: " + count);
            for(DocumentChange documentChange : value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED){
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString("senderId");
                    chatMessage.receiverId = documentChange.getDocument().getString("receiverId");
                    chatMessage.message = documentChange.getDocument().getString("message");
                    chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate("timestamp"));
                    chatMessages.add(chatMessage);
                }
            }
            if(!chatMessages.isEmpty()){
                Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateTime.compareTo(obj2.dateTime));

            }
            if(count == 0){
                chatAdapter.notifyDataSetChanged();
            }else{
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                chat_recycler_view.smoothScrollToPosition(chatMessages.size() - 1);
                no_messages.setVisibility(View.GONE);
            }
            chat_recycler_view.setVisibility(View.VISIBLE);
        }
    };

    private void setListeners(){
        btn_sendmessage.setOnClickListener(v -> sendMessage());
    }

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void showAllUserData() {
        Intent intent = getIntent();
        personUid = intent.getStringExtra("uid");
        personStorageRef = FirebaseStorage.getInstance().getReference("Images/" + personUid);

        StorageReference imageRef = personStorageRef.child("profilePic");

        try {
            File localFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), "jpg");
            imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    (chat_person_image).setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChatActivity.this, "Error Occured", Toast.LENGTH_LONG).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        personRef = firebaseDatabase.getReference().child("users").child(personUid);

        personRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                person_name.setText(dataSnapshot.child("name").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
}
}