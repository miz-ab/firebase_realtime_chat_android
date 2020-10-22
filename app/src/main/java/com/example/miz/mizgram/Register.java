package com.example.miz.mizgram;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    MaterialEditText username , password, email;
    Button btn_register;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = (MaterialEditText) findViewById(R.id.username);
        password = (MaterialEditText) findViewById(R.id.password);
        email = (MaterialEditText) findViewById(R.id.email);
        btn_register = (Button) findViewById(R.id.btn_register);

        //init firebase auth
        auth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String uname = username.getText().toString();
                String pword = password.getText().toString();
                String emailAdd = email.getText().toString();

                if(TextUtils.isEmpty(uname) || TextUtils.isEmpty(pword) || TextUtils.isEmpty(emailAdd)) {
                    Toast.makeText(Register.this, "All fields are required", Toast.LENGTH_LONG).show();
                }else {
                     register(uname, pword, emailAdd);
                }
            }
        });
    }


    private void register(final String username, String password, String email){
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                String userID = firebaseUser.getUid();

                                reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

                                HashMap<String, String>  hashMap = new HashMap<>();
                                hashMap.put("id",userID);
                                hashMap.put("username", username);
                                hashMap.put("status","offline");
                                hashMap.put("imageURL", "default");


                                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){
                                            Intent intent = new Intent(Register.this,MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                            } else {
                                //Toast.makeText(Register.this, "meg", Toast.LENGTH_LONG).show();
                                Log.e("msg",task.getException().toString());
                            }
                        }
                    });
    }
}
