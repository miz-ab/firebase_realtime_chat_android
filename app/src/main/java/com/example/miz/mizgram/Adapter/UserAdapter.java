package com.example.miz.mizgram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.miz.mizgram.Message;
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

import java.util.List;

/**
 * Created by miz on 24/1/2019.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context context;
    private List<User> mUser;
    boolean ischat;

    String theLastMessage ;

    public UserAdapter(Context context, List<User> mUser, boolean ischat){
        this.context = context;
        this.mUser = mUser;
        this.ischat = ischat;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User user = mUser.get(position);
        holder.username.setText(user.getUsername());
        if(user.getImageURL().equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else{
            //Glide.with(context).load(user.getImageURL()).into(holder.profile_image);
            Glide.with(context)
                    .load(user.getImageURL())
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(holder.profile_image);
        }

        /*

        if (ischat){
            if(user.getStatus().equals("online")){
                holder.profile_image_on.setVisibility(View.VISIBLE);
                holder.Profile_image_off.setVisibility(View.GONE);
            }else{
                holder.Profile_image_off.setVisibility(View.VISIBLE);
                holder.profile_image_on.setVisibility(View.GONE);
            }
        }else {
            holder.Profile_image_off.setVisibility(View.GONE);
            holder.profile_image_on.setVisibility(View.GONE);
        }

        */

        if(user.getStatus().equals("online")){
            holder.profile_image_on.setVisibility(View.VISIBLE);
            holder.Profile_image_off.setVisibility(View.GONE);
        }else{
            holder.Profile_image_off.setVisibility(View.VISIBLE);
            holder.profile_image_on.setVisibility(View.GONE);
        }

        lastMessage(user.getId(), holder.last_message);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Message.class);
                intent.putExtra("userid",user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;
        public ImageView profile_image_on;
        public ImageView Profile_image_off;
        public TextView last_message;


        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username_fragment);
            profile_image = itemView.findViewById(R.id.user_image);
            profile_image_on = itemView.findViewById(R.id.user_status_on);
            Profile_image_off = itemView.findViewById(R.id.user_status_off);
            last_message = itemView.findViewById(R.id.last_message);
        }
    }

    //check for last message

    private void lastMessage(final String userid, final TextView lasmsg){
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chats = snapshot.getValue(Chat.class);
                    if(chats.getReciver().equals(firebaseUser.getUid()) && chats.getSender().equals(userid) ||
                            chats.getReciver().equals(userid) && chats.getSender().equals(firebaseUser.getUid())){
                        if(chats.getMessage() != null) {
                            theLastMessage = chats.getMessage();
                        }else {
                            theLastMessage = chats.getFileURL();
                        }
                    }
                }



                switch (theLastMessage){
                    case "default":
                        lasmsg.setText("No Message yet");
                        break;
                    default:
                        lasmsg.setText(theLastMessage);
                        break;
                }

                theLastMessage = "default";

                //lasmsg.setText(theLastMessage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

   }




