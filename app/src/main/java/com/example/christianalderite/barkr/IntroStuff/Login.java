package com.example.christianalderite.barkr.IntroStuff;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.christianalderite.barkr.HomeActivity;
import com.example.christianalderite.barkr.R;
import com.example.christianalderite.barkr.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Christian Alderite on 3/15/2018.
 */

public class Login extends AppCompatActivity {

    private Button btnRegister, btnLogin;
    private EditText editEmail, editPassword;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setTheme(R.style.AppTheme_NoActionBar);

        try{
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }catch (Exception e){

        }

        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = auth.getCurrentUser();
                if (user != null) {
                    goToMainActivity();
                }
            }
        });

        this.setUpButtons();
    }

    public void setUpButtons() {
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        editEmail = (EditText) findViewById(R.id.loginEmail);
        editPassword = (EditText) findViewById(R.id.loginPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(editEmail.getText().toString())
                        && !TextUtils.isEmpty(editPassword.getText().toString())) {

                    Utilities.showLoadingDialog(Login.this);
                    final String email = editEmail.getText().toString();
                    final String password = editPassword.getText().toString();

                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Utilities.dismissDialog();
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(Login.this, "Login failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(Login.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toRegister = new Intent(Login.this, InitAccount.class);
                startActivity(toRegister);
                finish();
            }
        });
    }

    public void goToMainActivity() {
        Toast.makeText(this, "Welcome to Barkr!", Toast.LENGTH_SHORT);
        Intent toMain = new Intent(this, HomeActivity.class);
        startActivity(toMain);
        finish();
    }
}
