package com.example.miz.mizgram;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    Button btn_register, btn_login;
    FirebaseUser firebaseUser;




      protected void onStart() {

         super.onStart();

         firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

         if(firebaseUser != null){
         Intent intent = new Intent(StartActivity.this, MainActivity.class);
         startActivity(intent);
         finish();
         }
}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btn_register = (Button) findViewById(R.id.btn_registerr);
        btn_login = (Button) findViewById(R.id.btn_loginn);


        btn_register.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, Register.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, Login.class));
            }
        });
    }
}
