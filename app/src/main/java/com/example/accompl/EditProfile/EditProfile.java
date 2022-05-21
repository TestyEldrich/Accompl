package com.example.accompl.EditProfile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class EditProfile extends AppCompatActivity {

    private EditText edit_nickname, edit_name, edit_about, edit_gender, edit_birthdate, edit_city;
    private ImageView profileSet, btn_edit, back, profile_image, mini_profile_image;
    private String email;

    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference, userRef;
    StorageReference mStorageRef;
    public Uri imguri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("users");
        userRef = reference.child(user.getUid());
        mStorageRef = FirebaseStorage.getInstance().getReference("Images/" + user.getUid());

        edit_nickname = findViewById(R.id.edit_nickname);
        edit_name = findViewById(R.id.edit_name);
        edit_about = findViewById(R.id.edit_about);
        edit_nickname = findViewById(R.id.edit_nickname);
        edit_gender = findViewById(R.id.edit_gender);
        edit_birthdate = findViewById(R.id.edit_birthdate);
        edit_city = findViewById(R.id.edit_city);
        btn_edit = findViewById(R.id.btn_edit);
        profileSet = findViewById(R.id.profileSet);
        back = findViewById(R.id.back);
        profile_image = findViewById(R.id.result_image);
        mini_profile_image = findViewById(R.id.mini_profile_image);

        showAllUserData();

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRef.child("username").setValue(edit_nickname.getText().toString());
                userRef.child("name").setValue(edit_name.getText().toString());
                userRef.child("email").setValue(email);
                userRef.child("about").setValue(edit_about.getText().toString());
                userRef.child("gender").setValue(edit_gender.getText().toString());
                userRef.child("birthdate").setValue(edit_birthdate.getText().toString());
                userRef.child("city").setValue(edit_city.getText().toString());
                if(imguri != null){
                    FileUploader();
                }else{
                    Intent intent = new Intent(EditProfile.this, MainActivity.class);
                    startActivity(intent);
                }
                }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfile.this, MainActivity.class);
                startActivity(intent);
            }
        });

        profileSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfile.this, Account.class);
                startActivity(intent);
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileChooser();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imguri = data.getData();
            profile_image.setImageURI(imguri);
        }
    }

    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void FileUploader(){
        StorageReference ref = mStorageRef.child("profilePic");

        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });

        ref.putFile(imguri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(Uri downloadUrl)
                    {
                        userRef.child("profilePic").setValue(downloadUrl.toString());
                    }
                });
                Intent intent = new Intent(EditProfile.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void FileChooser(){
        Intent intent = new Intent();
        intent.setType("image/'");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
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
                }
            });
        }catch(IOException e){
            e.printStackTrace();
        }



        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                email = dataSnapshot.child("email").getValue(String.class);
                edit_nickname.setText(dataSnapshot.child("username").getValue(String.class));
                edit_name.setText(dataSnapshot.child("name").getValue(String.class));
                edit_about.setText(dataSnapshot.child("about").getValue(String.class));
                edit_gender.setText(dataSnapshot.child("gender").getValue(String.class));
                edit_birthdate.setText(dataSnapshot.child("birthdate").getValue(String.class));
                edit_city.setText(dataSnapshot.child("city").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}