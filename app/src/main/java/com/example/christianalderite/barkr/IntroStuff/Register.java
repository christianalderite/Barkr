package com.example.christianalderite.barkr.IntroStuff;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.christianalderite.barkr.HomeActivity;
import com.example.christianalderite.barkr.R;
import com.example.christianalderite.barkr.UserModel;
import com.example.christianalderite.barkr.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class Register extends AppCompatActivity {

    private Button btnRegister, btnCancel;
    private EditText editEmail, editPassword, editConfirm;
    private String userName, userBio, userGender, userBirthDate;
    Uri userImageUri;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent fromInitAccount = getIntent();
        if(fromInitAccount.getExtras() != null) {
            Bundle bundle = fromInitAccount.getExtras();
            userName = bundle.getString("registerName","");
            userBio = bundle.getString("registerBio","");
            userBirthDate = bundle.getString("registerBirthDate","");
            userGender = bundle.getString("registerGender","");
            userImageUri = Uri.parse(bundle.getString("registerImageUri",""));
        }

        this.setUpButtons();
    }

    protected void setUpButtons(){

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        editEmail = (EditText) findViewById(R.id.loginEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editConfirm = (EditText) findViewById(R.id.editConfirmPassword);

                btnRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    if(!TextUtils.isEmpty(editEmail.getText().toString())
                        && !TextUtils.isEmpty(editPassword.getText().toString())
                        && !TextUtils.isEmpty(editConfirm.getText().toString())
                        && editConfirm.getText().toString().equals(editPassword.getText().toString())){

                    Utilities.showLoadingDialog(Register.this);
                    final String email = editEmail.getText().toString();
                    final String password = editPassword.getText().toString();

                    auth = FirebaseAuth.getInstance();
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Utilities.dismissDialog();

                                        try {
                                            throw task.getException();
                                        } catch(FirebaseAuthWeakPasswordException e) {
                                            Toast.makeText(Register.this, "Your password is weak.", Toast.LENGTH_LONG).show();
                                        } catch(FirebaseAuthInvalidCredentialsException e) {
                                            Toast.makeText(Register.this, "Your credentials are invalid.", Toast.LENGTH_LONG).show();
                                        } catch(FirebaseAuthUserCollisionException e) {
                                            Toast.makeText(Register.this, "An account with that email already exists.", Toast.LENGTH_LONG).show();
                                        } catch(Exception e) {
                                            Toast.makeText(Register.this, "Something went wrong.", Toast.LENGTH_LONG).show();
                                        }
                                    
                                    } else {
                                        Toast.makeText(Register.this, "Registered account! Uploading user info.", Toast.LENGTH_LONG).show();
                                        user = auth.getCurrentUser();
                                        uploadImage();
                                    }

                                }
                            });

                }else{
                    String append = ".";
                    if(!editConfirm.getText().toString().equals(editPassword.getText().toString())){
                        append=" and confirm password.";
                    }
                    Toast.makeText(Register.this, "Please fill all fields"+append, Toast.LENGTH_LONG).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void uploadImage(){
        Utilities.showLoadingDialog(Register.this);
        StorageReference filePath = storage.getReference("images").child(UUID.randomUUID().toString());
        filePath.putFile(userImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Utilities.dismissDialog();
                        Uri uri = taskSnapshot.getDownloadUrl();
                        uploadUserInfo(uri.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Utilities.dismissDialog();
                        Toast.makeText(Register.this, "Image upload error.", Toast.LENGTH_SHORT).show();
                        uploadUserInfo("");
                    }
                });
    }


    public void uploadUserInfo(String uri){
        final DatabaseReference refUsers = database.getReference("users").child(user.getUid());

        UserModel you = new UserModel(user.getUid(), user.getEmail(),
                userName, userBirthDate,
                userGender, userBio, uri);

        refUsers.setValue(you);
        goToMainActivity();
    }

    public void goToMainActivity(){
        Intent toMain = new Intent(this, HomeActivity.class);
        startActivity(toMain);
        finish();
    }

}
