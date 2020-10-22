package com.example.miz.mizgram.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.miz.mizgram.Adapter.UserAdapter;
import com.example.miz.mizgram.Models.Chat;
import com.example.miz.mizgram.Models.User;
import com.example.miz.mizgram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> muser;
    private List<String> userList;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat,container,false);
        recyclerView = view.findViewById(R.id.recyclerview_chat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.keepSynced(true);
        userList = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getSender().equals(firebaseUser.getUid())){
                        userList.add(chat.getReciver());
                    }
                    if(chat.getReciver().equals(firebaseUser.getUid())){
                        userList.add(chat.getSender());
                    }
                }

                readChat();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //View view = inflater.inflate(R.layout.fragment_chat,container,false);
        return view;
    }

    private void readChat(){
        muser = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                muser.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    for(String id : userList){
                        if(user.getId().equals(id)){

                            /*
                            *for(User user1 : new ArrayList<User>(muser)){

                                }

                                /*
                                for(int i = 0; i < muser.size(); i++){
                                    if(!user.getId().equals(muser.get(i).getId())){
                                        muser.add(user);
                                    }
                                }
                                */



                            if(muser.size() != 0){

                                boolean IsExit = false;
                                for(User user1: muser){
                                    if(user.getId().equals(user1.getId())){
                                        IsExit = true;
                                    }
                                }

                                if(!IsExit)
                                    muser.add(user);

                            }else{
                                muser.add(user);
                            }
                        }
                    }
                }

                userAdapter = new UserAdapter(getContext(),muser,true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        //Context cts = (Context) ChatFragment.this;

        ConnectivityManager cm = (ConnectivityManager) Context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkinfo = cm.getActiveNetworkInfo();
        if(activeNetworkinfo != null){
            if(activeNetworkinfo.getType() == ConnectivityManager.TYPE_WIFI){
                haveConnectedWifi = true;
                //Log.i("network info", "true");

            }
            if(activeNetworkinfo.getType() == ConnectivityManager.TYPE_MOBILE){
                haveConnectedMobile = true;
            }

            return haveConnectedMobile || haveConnectedWifi;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
*/
}
