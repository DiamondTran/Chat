package com.diamond.diamond.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.diamond.diamond.chat.Adapter.AdapterMessage;
import com.diamond.diamond.chat.Model.Chat;
import com.diamond.diamond.chat.Model.User;
import com.diamond.diamond.chat.Notifications.Client;
import com.diamond.diamond.chat.Notifications.Data;
import com.diamond.diamond.chat.Notifications.MyRespones;
import com.diamond.diamond.chat.Notifications.Sender;
import com.diamond.diamond.chat.Notifications.Token;
import com.diamond.diamond.chat.fragment.APIService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessegaActivity extends AppCompatActivity {
    CircleImageView profile_imgae;
    TextView user_name;
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    AdapterMessage message;
    List<Chat> chats;

    RecyclerView recyclerView;
    String userid;
    ImageButton btnsend;
    EditText edtsend;
    private ImageView img_on, img_off;
    Intent intent;
    ValueEventListener seenListener;

    APIService apiService;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messega);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessegaActivity.this, Main2Activity.class));
            }
        });

        apiService = Client.getClient("https://fcm.googleapis.com").create(APIService.class);


        btnsend = findViewById(R.id.btn_send);
        edtsend = findViewById(R.id.edt_send);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_imgae = findViewById(R.id.profile_image);
        user_name = findViewById(R.id.user_name);

        intent = getIntent();
        userid = intent.getStringExtra("userid");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = edtsend.getText().toString();
                if (!msg.equals("")) {
                    sendmessage(firebaseUser.getUid(), userid, msg);
                    edtsend.setText("");
                } else {
                    Toast.makeText(MessegaActivity.this, "Bạn không thể gửi tin trống", Toast.LENGTH_SHORT).show();
                }
            }
        });


        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                user_name.setText(user.getUsername());
                if (user.getImageURL().equals("default")) {
                    profile_imgae.setImageResource(R.mipmap.ic_launcher);

                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_imgae);
                }
                readmessage(firebaseUser.getUid(), userid, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        seenMessage(userid);
    }

    private void sendmessage(final String sender, final String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);

        reference.child("Chats").push().setValue(hashMap);
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(userid);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userid);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg = message;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify =true) {
                    sendNotifiaction(receiver, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotifiaction(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username + ":  " + message, "Tin nhắn mới",
                            userid);

                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender).enqueue(new Callback<MyRespones>() {
                        @Override
                        public void onResponse(Call<MyRespones> call, Response<MyRespones> response) {
                            if (response.code() == 200) {
                                if (response.body().sucsses  != 1) {

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyRespones> call, Throwable t) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readmessage(final String myid, final String userid, final String imageurl) {
        chats = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        chats.add(chat);
                    }
                    message = new AdapterMessage(MessegaActivity.this, chats, imageurl);
                    recyclerView.setAdapter(message);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void seenMessage(final String userid) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void Status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);

    }

    private void currentUser(String usersid){
        SharedPreferences.Editor editor= getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser",usersid);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Status("online");
        currentUser(userid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        Status("offline");
        currentUser("none");
    }
}
