package com.diamond.diamond.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
private EditText emaillog,edtpasslog;
private Button btnlogin;

FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

emaillog= findViewById(R.id.emaillog);
edtpasslog= findViewById(R.id.passwordlog);
btnlogin= findViewById(R.id.btnlogin);
        auth= FirebaseAuth.getInstance();


        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtemail= emaillog.getText().toString();
                String txtpass= edtpasslog.getText().toString();

                if (TextUtils.isEmpty(txtemail) || TextUtils.isEmpty(txtpass)){
                    Toast.makeText(LoginActivity.this, "Không dược nhập trống", Toast.LENGTH_SHORT).show();
                }else {
                    auth.signInWithEmailAndPassword(txtemail,txtpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Intent intent= new Intent(LoginActivity.this,Main2Activity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(LoginActivity.this, "Sai Email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void dangki(View view) {
        startActivity(new Intent(LoginActivity.this,DangKiActivity.class));
    }

    public void fogotpass(View view) {
         startActivity(new Intent(LoginActivity.this,FogotPassActivity.class));

    }
}
