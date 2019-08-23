package com.diamond.diamond.chat;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.diamond.diamond.chat.Model.Chat;
import com.diamond.diamond.chat.Model.User;
import com.diamond.diamond.chat.fragment.ChatsFragment;
import com.diamond.diamond.chat.fragment.ProfileFragment;
import com.diamond.diamond.chat.fragment.UsersFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Main2Activity extends AppCompatActivity {
    CircleImageView circleImageView;
    private TextView textView;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        NavigationView navigationView = findViewById(R.id.nav_view);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

//        circleImageView= findViewById(R.id.profileimage);
//        textView= findViewById(R.id.nameuser);



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User user= dataSnapshot.getValue(User.class);
//                textView.setText(user.getUsername());
//
//                if (user.getImageURL().equals("default")){
//                    circleImageView.setImageResource(R.mipmap.ic_launcher);
//                } else {
//                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(circleImageView);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

       final TabLayout tabLayout = findViewById(R.id.tab_layout);
       final ViewPager viewPager= findViewById(R.id.view_pager);



        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                int unread = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()){
                        unread++;
                    }
                }
                if (unread == 0){
                    viewPagerAdapter.addFragment(new ChatsFragment(),"Trò chuyện");
                }else {
                    viewPagerAdapter.addFragment(new ChatsFragment(),"("+unread+") Chats");
                }
                viewPagerAdapter.addFragment(new UsersFragment(),"Người dùng");
                viewPagerAdapter.addFragment(new ProfileFragment(),"Cá nhân");
                viewPager.setAdapter(viewPagerAdapter);

                tabLayout.setupWithViewPager(viewPager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();

            startActivity(new Intent(Main2Activity.this,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")



    class ViewPagerAdapter extends FragmentPagerAdapter{
private ArrayList<Fragment> fragments;
private ArrayList<String >titles;
        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles= new ArrayList<>();
        }
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();

        }

        public  void addFragment(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }


    private void Status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Status("offline");
    }
}
