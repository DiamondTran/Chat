package com.diamond.diamond.chat.Adapter;

import android.content.Context;
import android.content.Intent;
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

import java.util.List;

public class AdapterMessage extends RecyclerView.Adapter<AdapterMessage.ViewHolder> {

    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;

    private Context context;

    private String imageurl;

    private List<Chat> chats;
FirebaseUser fUser;
    public AdapterMessage(Context context, List<Chat> chats, String imageurl) {
        this.context = context;
        this.chats = chats;
        this.imageurl= imageurl;
    }

    @NonNull
    @Override
    public AdapterMessage.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);

            return new AdapterMessage.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);

            return new AdapterMessage.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMessage.ViewHolder holder, int position) {
Chat chat = chats.get(position);
holder.show_message.setText(chat.getMessage());
if (imageurl.equals("default")){
    holder.profileimge.setImageResource(R.mipmap.ic_launcher);

}else {
    Glide.with(context).load(imageurl).into(holder.profileimge);
}

if (position == chats.size()-1){
    if (chat.isIsseen()){
        holder.txt_seen.setText("Đã xem");
    }else{
        holder.txt_seen.setText("Đã gửi");
    }
}else {
    holder.txt_seen.setVisibility(View.GONE);
}
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public ImageView profileimge;
        public TextView txt_seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
          txt_seen= itemView.findViewById(R.id.txtseen);
            show_message= itemView.findViewById(R.id.show_message);
            profileimge= itemView.findViewById(R.id.profile_image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        if (chats.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
}

