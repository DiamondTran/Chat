package com.diamond.diamond.chat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.diamond.diamond.chat.MessegaActivity;
import com.diamond.diamond.chat.Model.Chat;
import com.diamond.diamond.chat.Model.User;
import com.diamond.diamond.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.ViewHolder> {
    private Context context;
    private List<User> users;
    private boolean ischat;
String thelastMessage;
    public AdapterUser(Context context, List<User> users, boolean ischat) {
        this.context = context;
        this.users = users;
        this.ischat =ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = users.get(position);
        holder.username.setText(user.getUsername());
        if (user.getImageURL().equals("default")) {
            holder.profileimge.setImageResource(R.mipmap.ic_launcher);

        } else {
            Glide.with(context).load(user.getImageURL()).into(holder.profileimge);
        }
if (ischat){
    lastMessage(user.getId(), holder.last_msg);

}else {
    holder.last_msg.setVisibility(View.GONE);
}
        if(ischat){
            if (user.getStatus().equals("online")){
                holder.imgon.setVisibility(View.VISIBLE);
                holder.imgoff.setVisibility(View.GONE);
            }else {
                holder.imgon.setVisibility(View.GONE);
                holder.imgoff.setVisibility(View.VISIBLE);
            }
        }else {
            holder.imgon.setVisibility(View.GONE);
            holder.imgoff.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MessegaActivity.class);
                intent.putExtra("userid", user.getId());
                intent.putExtra("username", user.getUsername());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public ImageView profileimge;
        private ImageView imgon,imgoff;
private TextView last_msg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.name_user);
            profileimge = itemView.findViewById(R.id.profile_image);
            imgon= itemView.findViewById(R.id.image_on);
            imgoff= itemView.findViewById(R.id.image_off);
            last_msg= itemView.findViewById(R.id.message_new);
        }
    }

    private void lastMessage(final String userid, final TextView last_msg){
thelastMessage= "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=  FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat=  snapshot.getValue(Chat.class);
                    if (firebaseUser != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            thelastMessage = chat.getMessage();

                        }
                    }
                }
                switch (thelastMessage){
                    case "default":
                        last_msg.setText("No Message");

                        break;

                        default:
                            last_msg.setText(thelastMessage);

                            break;
                }
                thelastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
