package com.diamond.diamond.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class FogotPassActivity extends AppCompatActivity {
EditText send_email;
Button btn_email;

FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fogot_pass);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Quên mật khẩu");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        send_email= findViewById(R.id.send_email);
        btn_email= findViewById(R.id.btn_email);


        firebaseAuth= FirebaseAuth.getInstance();

        btn_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email= send_email.getText().toString();
                if (email.equals("")){
                    Toast.makeText(FogotPassActivity.this, "Bạn phải nhập email", Toast.LENGTH_SHORT).show();
                }else {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(FogotPassActivity.this, "Hãy kiểm tra eamil", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(FogotPassActivity.this,LoginActivity.class));
                            }else {
                                String err = task.getException().getMessage();
                                Toast.makeText(FogotPassActivity.this, err, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });


    }
}
