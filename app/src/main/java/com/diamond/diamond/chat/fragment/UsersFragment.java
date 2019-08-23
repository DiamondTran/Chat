package com.diamond.diamond.chat.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.diamond.diamond.chat.Adapter.AdapterUser;
import com.diamond.diamond.chat.Model.User;
import com.diamond.diamond.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {
private RecyclerView recyclerView;

private AdapterUser adapterUser;
private List<User> users;
private EditText edt_search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view= inflater.inflate(R.layout.fragment_users, container, false);
edt_search= view.findViewById(R.id.seach_user);
       recyclerView= view.findViewById(R.id.recycler);
       recyclerView.setHasFixedSize(true);
       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
       users= new ArrayList<>();
       readUser();


       edt_search.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            searchUser(charSequence.toString());
           }

           @Override
           public void afterTextChanged(Editable editable) {

           }
       });


        return view;
    }

    private void searchUser(String s) {

        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        Query query= FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
                .startAt(s)
                .endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    users.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        assert user != null;
                        assert firebaseUser != null;
                        if (!user.getId().equals(firebaseUser.getUid())) {
                            users.add(user);
                        }
                    }
                    adapterUser = new AdapterUser(getContext(), users, true);
                    recyclerView.setAdapter(adapterUser);


            }

                @Override
                public void onCancelled (@NonNull DatabaseError databaseError){

                }

        });


    }

    private void readUser() {
        final FirebaseUser  firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (edt_search.getText().toString().equals("")) {
                users.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user= snapshot.getValue(User.class);
                     assert user!=null;
                      assert firebaseUser !=null;
                    if (!user.getId().equals(firebaseUser.getUid())){
                        users.add(user);
                    }
                }
                adapterUser= new AdapterUser(getContext(), users,true);
                recyclerView.setAdapter(adapterUser);
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
