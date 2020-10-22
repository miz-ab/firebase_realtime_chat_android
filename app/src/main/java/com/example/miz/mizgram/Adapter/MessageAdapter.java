package com.example.miz.mizgram.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.miz.mizgram.Message;
import com.example.miz.mizgram.Models.Chat;
import com.example.miz.mizgram.Models.User;
import com.example.miz.mizgram.OnItemClickListener;
import com.example.miz.mizgram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by miz on 25/1/2019.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private List<Chat> chat;
    private OnItemClickListener clickListener;

    FirebaseUser firebaseUser;


    public MessageAdapter(Context context, List<Chat> chat){
        this.context = context;
        this.chat = chat;
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(MessageAdapter.ViewHolder holder, int position) {
            Chat chats = chat.get(position);


            if(chats.getMessage() != null){
                holder.show_message.setVisibility(View.VISIBLE);
                holder.show_message.setText(chats.getMessage());
            }else{
                holder.show_message.setVisibility(View.GONE);
            }
        //Log.i("fileuri",chats.getFileURL());
            if(chats.getFileURL() != null){
                holder.attached_file.setVisibility(View.VISIBLE);
                holder.attached_file.setText(chats.getFileName());

            }else{
                holder.attached_file.setVisibility(View.GONE);
            }

        /*

        if(position == chat.size() - 1){ // target the last message
            if(chats.isseen()){
                holder.text_seen.setText("seen");
            }else {
                holder.text_seen.setText("Delivered");
            }
        }else { // if it is not last message
            holder.text_seen.setVisibility(View.GONE);
        }

        */
    }

    @Override
    public int getItemCount() {
        return chat.size();
    }

    public void setClickListener(OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }




    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView show_message;
        //public ImageView profile_image;
        TextView attached_file;


        public ViewHolder(View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message_message_section);
            attached_file = itemView.findViewById(R.id.attached_file);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(clickListener != null)
                clickListener.onClick(view, getAdapterPosition());
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(chat.get(position).getSender().equals(firebaseUser.getUid())){
            return  MSG_TYPE_LEFT;
        }else {
            return MSG_TYPE_RIGHT;
        }
    }
}
