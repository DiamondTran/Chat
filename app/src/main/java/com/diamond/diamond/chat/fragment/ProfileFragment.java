package com.diamond.diamond.chat.fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.storage.StorageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.diamond.diamond.chat.Model.User;
import com.diamond.diamond.chat.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {
CircleImageView profile;
TextView username;

DatabaseReference reference;
FirebaseUser firebaseUser;

StorageReference  storageReference;
private static final int IMAGE_REQUEST = 1;
private Uri imuri;
private StorageTask uploadtask;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profile =  view.findViewById(R.id.profile_image);
        username= view.findViewById(R.id.username);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");




        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user= dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")){
                    profile.setImageResource(R.mipmap.ic_launcher);

                }else {
                    Glide.with(getContext()).load(user.getImageURL()).into(profile);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 openImage();
            }
        });

        return view;
    }

    private void openImage() {
        Intent intent= new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, IMAGE_REQUEST);
  }

  private String getFileExtention( Uri uri){
      ContentResolver contentResolver  = getActivity().getContentResolver();
      MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
      return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
  }

   private void updateImage(){
        final ProgressDialog pd= new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if (imuri !=null){
            final StorageReference feference = storageReference.child(System.currentTimeMillis()+
                    "." + getFileExtention(imuri));

            uploadtask = feference.putFile(imuri);
            uploadtask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task>() {
                @Override
                    public Task then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                if (!task.isSuccessful()){
                    throw task.getException();

                }
                return feference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                 if (task.isSuccessful()){
                     Uri downloaduri = task.getResult();
                     String mUri= downloaduri.toString();

                     reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                     HashMap<String, Object> map = new HashMap<>();
                     map.put("imageURL", mUri);
                     reference.updateChildren(map);

                     pd.dismiss();
                 }else {
                     Toast.makeText(getContext(), "Upload faile!", Toast.LENGTH_SHORT).show();
                     pd.dismiss();
                 }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else {
            Toast.makeText(getContext(), "Không có ảnh được chọn", Toast.LENGTH_SHORT).show();
        }
   }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
         && data != null && data.getData() != null){
            imuri = data.getData();

            if (uploadtask != null && uploadtask.isInProgress()){

                Toast.makeText(getContext(), "Upload thành công!", Toast.LENGTH_SHORT).show();
            }else {
                updateImage();
            }
        }
    }
}
