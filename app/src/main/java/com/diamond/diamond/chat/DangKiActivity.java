package com.diamond.diamond.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DangKiActivity extends AppCompatActivity {
private EditText edtnameuser,edtpassword,edtemail;
private Button btndangki;

FirebaseAuth auth;
DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ki);

        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Đăng kí");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtnameuser= findViewById(R.id.username);
        edtemail= findViewById(R.id.email);
        edtpassword= findViewById(R.id.password);
        btndangki= findViewById(R.id.btndangki);
       auth = FirebaseAuth.getInstance();


       btndangki.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String txtname= edtnameuser.getText().toString();
               String txtemai= edtemail.getText().toString();
               String txtpass= edtpassword.getText().toString();

               if (TextUtils.isEmpty(txtname) || TextUtils.isEmpty(txtemai) || TextUtils.isEmpty(txtpass)){
                   Toast.makeText(DangKiActivity.this, "Bạn phải nhập đúng thông tin", Toast.LENGTH_SHORT).show();
               } else if (txtpass.length()<6){
                   Toast.makeText(DangKiActivity.this, "Mật khẩu phải lớn hơn 6 kí tự", Toast.LENGTH_SHORT).show();
               }else {
                   dangki(txtname,txtemai,txtpass);
               }
           }
       });

    }

    private void dangki(final String username, String email, String password){
auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()){
            FirebaseUser firebaseUser= auth.getCurrentUser();
            assert firebaseUser != null;
            String userid = firebaseUser.getUid();

            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
            HashMap<String, String > hashMap = new HashMap<>();
            hashMap.put("id", userid);
            hashMap.put("username", username);
            hashMap.put("imageURL","default");
            hashMap.put("status","offline");
            hashMap.put("search",username.toLowerCase());

            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Intent intent= new Intent(DangKiActivity.this,Main2Activity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();;
                    }
                }
            });

        }
        else {
            Toast.makeText(DangKiActivity.this, "Đăng kí thất bại, bạn phải nhập đúng định dạng email", Toast.LENGTH_SHORT).show();
        }
    }
});
    }
}
