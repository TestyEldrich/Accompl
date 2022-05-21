package com.example.accompl.Login_Registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.accompl.MainActivity;
import com.example.accompl.R;
import com.example.accompl.EditProfile.UserHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText username_register;
    private EditText email_register;
    private EditText password_register;
    private EditText password_register_repeat;
    private ImageView btn_register;
    private ImageView login_btn;

    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    StorageReference mStorageRef;
    public Uri imgurii;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        username_register = findViewById(R.id.username_register);
        email_register = findViewById(R.id.email_register);
        password_register = findViewById(R.id.password_register);
        password_register_repeat = findViewById(R.id.password_register_repeat);
        btn_register = findViewById(R.id.btn_register);
        login_btn = findViewById(R.id.login_btn);

        mAuth = FirebaseAuth.getInstance();

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email_register.getText().toString().isEmpty() || password_register.getText().toString().isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Empty Fields", Toast.LENGTH_LONG).show();
                }else if(!password_register.getText().toString().equals(password_register_repeat.getText().toString())){
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                }else {
                    mAuth.createUserWithEmailAndPassword(email_register.getText().toString(), password_register.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        firebaseDatabase = FirebaseDatabase.getInstance();
                                        reference = firebaseDatabase.getReference("users");
                                        assert user != null;
                                        UserHelper userHelper = new UserHelper(username_register.getText().toString(), "null", email_register.getText().toString(), "null", "null", "null", "null");
                                        reference.child(user.getUid()).setValue(userHelper);
                                        reference.child(user.getUid()).child("interests").setValue(" ");
                                        reference.child(user.getUid()).child("uid").setValue(user.getUid());

                                        StorageReference ref = FirebaseStorage.getInstance().getReference("Images/" + user.getUid()).child("profilePic");
                                        imgurii = Uri.parse("android.resource://com.example.accompl/" + R.drawable.default_profile);

                                        ref.putFile(imgurii).addOnFailureListener(new OnFailureListener() {
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
                                                        reference.child(user.getUid()).child("profilePic").setValue(downloadUrl.toString());
                                                    }
                                                });
                                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "You have some errors", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}