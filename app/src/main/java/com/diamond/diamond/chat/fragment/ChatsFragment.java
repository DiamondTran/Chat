package com.diamond.diamond.chat.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.diamond.diamond.chat.Adapter.AdapterUser;
import com.diamond.diamond.chat.Model.Chat;
import com.diamond.diamond.chat.Model.ChatList;
import com.diamond.diamond.chat.Model.User;
import com.diamond.diamond.chat.Notifications.Token;
import com.diamond.diamond.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;


public class ChatsFragment extends Fragment {

private RecyclerView recyclerView;
private AdapterUser adapterUser;

private List<User> users;

FirebaseUser firebaseUser;
DatabaseReference reference;

private List<ChatList> userList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_chats,container,false);
        recyclerView= view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        userList= new ArrayList<>();

reference= FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());
reference.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        userList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
            ChatList chatList = snapshot.getValue(ChatList.class);
            userList.add(chatList);
            
        }
        
        chatlist();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});

updateToken(FirebaseInstanceId.getInstance().getToken());
        return view;
    }

  private void updateToken(String token){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);

    }

    private void chatlist() {
     users= new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
         reference.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        users.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
            User user = snapshot.getValue(User.class);
            for (ChatList chatList : userList){
                if (user.getId().equals(chatList.getId())){
                    users.add(user);
                }
            }
        }
        adapterUser = new AdapterUser(getContext(), users, true);
        recyclerView.setAdapter(adapterUser);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});

    }


}
