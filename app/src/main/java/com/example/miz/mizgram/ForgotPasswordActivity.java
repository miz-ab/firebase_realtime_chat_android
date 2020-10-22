package com.example.miz.mizgram;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ForgotPasswordActivity extends AppCompatActivity {

    MaterialEditText resetpassword;
    Button btn_reset;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        resetpassword = (MaterialEditText)findViewById(R.id.forgot_pword);
        btn_reset = (Button)findViewById(R.id.btnreset);

        firebaseAuth = FirebaseAuth.getInstance();


        btn_reset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String emailll = resetpassword.getText().toString();
                Toast.makeText(getApplicationContext(),emailll.toLowerCase(),Toast.LENGTH_LONG).show();

                    firebaseAuth.sendPasswordResetEmail(emailll).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"PLS check UR Email",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(),Login.class));
                            }else {
                                String err = task.getException().getMessage();
                                Toast.makeText(getApplicationContext(),err,Toast.LENGTH_LONG).show();
                            }
                        }
                    });




                        }



        });
    }


}
