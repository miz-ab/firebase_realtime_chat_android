package com.example.miz.mizgram.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.MimeTypeFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.miz.mizgram.Models.User;
import com.example.miz.mizgram.R;
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
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    TextView userName;
    CircleImageView profileImage;

    public static  final  int IMAGE_REQUEST = 1;
    private Uri image_url;
    StorageTask uploadTask;

    //Context context = ProfileFragment.getA;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_profile2, container, false);
        userName = view.findViewById(R.id.profile_username);
        profileImage = view.findViewById(R.id.profile_picture);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getImageURL().equals("default")){
                    profileImage.setImageResource(R.mipmap.ic_launcher);
                }else {
                    if(getActivity() == null){
                        return;
                    }
                    //Glide.with(ProfileFragment.this).load(user.getImageURL()).into(profileImage);
                    Glide.with(ProfileFragment.this)
                            .load(user.getImageURL())
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(profileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        profileImage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                openImage();
            }
        });



        return view;
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtentions(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if(image_url != null){
            final StorageReference filereference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtentions(image_url));

            uploadTask = filereference.putFile(image_url);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }

                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloaduri = task.getResult();
                        String muri = downloaduri.toString();

                        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL",muri);
                        databaseReference.updateChildren(map);
                        progressDialog.dismiss();
                    }else {
                        Toast.makeText(getContext(),"Failed",Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("Error Message",e.getMessage());
                }
            });

        }else {
            Toast.makeText(getContext(),"No Image Selected",Toast.LENGTH_LONG).show();
        }
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Toast.makeText(getContext(),"hdghg",Toast.LENGTH_LONG).show();
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
                image_url = data.getData();
                if(uploadTask != null && uploadTask.isInProgress()){
                    Toast.makeText(getContext(),"Upload Inprogress",Toast.LENGTH_LONG).show();
                }else {
                    uploadImage();
                }
        }
    }
}
