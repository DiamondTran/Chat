package com.diamond.diamond.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null){


            Intent intent = new Intent(MainActivity.this,Main2Activity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void dangkiii(View view) {
        startActivity(new Intent(MainActivity.this, DangKiActivity.class));
    }

    public void dangnhap(View view) {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }
}
