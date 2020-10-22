package com.example.miz.mizgram;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.miz.mizgram.Adapter.MessageAdapter;
import com.example.miz.mizgram.Models.Chat;
import com.example.miz.mizgram.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class Message extends AppCompatActivity implements OnItemClickListener{

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
     //Uri downloadUrl = taskSnapshot.getDownloadUrl();
    @SuppressWarnings("VisibleForTests") Uri file_url;
    @SuppressWarnings("VisibleForTests") Uri file_url_download;
    StorageTask uploadTask;
    CircleImageView profile;
    TextView username;
    TextView last_seen_status;
    ImageButton btn_send;
    ImageButton send_file;
    EditText text_message;
    Intent intent;
    List<Chat> mchat;
    MessageAdapter messageAdapter;
    RecyclerView recyclerView;

    ValueEventListener seenListener;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();
    String formated_date = simpleDateFormat.format(date);

    private Timer timer = new Timer();
    private final long DELAY = 2000;
    public static  final  int FILE_REQUEST = 1;
    private boolean typing_ = false;

    private String DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory
            (Environment.DIRECTORY_DOWNLOADS).getPath();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                Intent intent = new Intent(Message.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_messagebody);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile = (CircleImageView) findViewById(R.id.profile_image_main);
        username = (TextView) findViewById(R.id.user_name_main);
        last_seen_status = (TextView) findViewById(R.id.lastseen_status);
        btn_send = (ImageButton) findViewById(R.id.send_btn);
        send_file = (ImageButton) findViewById(R.id.send_file);
        text_message = (EditText) findViewById(R.id.text_box);
        intent = getIntent();
        final String userID = intent.getStringExtra("userid");
        String userID_from_ = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);



        //btn_send onclick listener
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = Message.this;
                String message = text_message.getText().toString();
                SendMessage(userID, firebaseUser.getUid(),message);
                text_message.setText("");
                hideKeyboard((Activity)context);
            }
        });

        send_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });
        //btn_send focus listener
        text_message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                /*
                if (hasFocus) {
                    btn_send.setVisibility(view.VISIBLE);
                } else {
                    btn_send.setVisibility(view.GONE);
                }
                */
            }
        });

        text_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    send_file.setVisibility(View.GONE);
                    btn_send.setVisibility(View.VISIBLE);
                    typing_ = true;
                    typing("typing");
                if(timer != null){
                    timer.cancel();
                }
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                if(editable.length() > 0){
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            typing_ = false;
                            typing("not_typing");
                            Log.i("tag","end");
                            //serviceConnection.getStopPoints(editable.toString());
                        }
                    },DELAY);
                }
                if(editable.length() == 0){
                    send_file.setVisibility(View.VISIBLE);
                    btn_send.setVisibility(View.GONE);
                    typing_ = false;
                    typing("not_typing");
                }
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    profile.setImageResource(R.mipmap.ic_launcher);
                }else {
                    //Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile);
                    Glide.with(getApplicationContext())
                            .load(user.getImageURL())
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(profile);
                }

                /*
                * if(user.getStatus().equals("offline")){

                    last_seen(dateString);
                    last_seen_status.setText(user.getLastseen());
                }else {
                    last_seen("def");
                    last_seen_status.setText("");
                }*/

                if(user.getStatus().equals("offline")){
                    last_seen_status.setText(user.getLastseen());
                }else if(user.getStatus().equals("online")){
                    if(user.getTyping() != null && user.getTyping().equals("not_typing")){

                    }else if(user.getTyping() != null && user.getTyping().equals("typing")){
                        last_seen_status.setText("...typing");
                    }else {
                        last_seen_status.setText("online");
                    }

                }
                readMesages(firebaseUser.getUid(), userID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        status("online");
        //seenMessage(userID);
    } // end of oncreate

    //method for seen the message
    private void seenMessage(final String useid){
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");

        seenListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReciver().equals(firebaseUser.getUid()) && chat.getSender().equals(useid)){
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        /*
                        Log.i("chat reciver ID ",chat.getReciver());
                        Log.i("chat sender ID ",chat.getSender());
                        Log.i(" reciver ID ",useid);
                        Log.i(" sender ID ",firebaseUser.getUid());
                        */
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void SendMessage(String sender, String reciver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("reciver", reciver);
        hashMap.put("message", message);

        reference.child("Chats").push().setValue(hashMap);
    }

    private void readMesages(final String myid, final String userid){
        mchat = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mchat.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReciver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReciver().equals(userid) && chat.getSender().equals(myid)){
                        mchat.add(chat);
                    }
                    messageAdapter = new MessageAdapter(Message.this,mchat);
                    recyclerView.setAdapter(messageAdapter);
                    messageAdapter.setClickListener(Message.this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //seenMessage(userid);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void status(String status){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        databaseReference.updateChildren(hashMap);
    }

    private void typing(String typing_status){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("typing",typing_status);
        databaseReference.updateChildren(hashMap);
    }

    private void last_seen(String last_seen){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("lastseen",last_seen);
        databaseReference.updateChildren(hashMap);
    }

     void uploadFile(){
         if (file_url != null) {
             final ProgressDialog progressDialog = new ProgressDialog(this);
             progressDialog.setTitle("Uploading");
             progressDialog.show();

             /*
             final StorageReference filereference = storageReference.child("File_Uploads/" + System.currentTimeMillis()
                     + "." + getFileExtentions(file_url));
            */

             final StorageReference filereference = storageReference.child("File_Uploads/" + getFileName(file_url));
             final String fileName = getFileName(file_url);

             filereference.putFile(file_url)
                     .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                         @Override
                         public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                             @SuppressWarnings("VisibleForTests") Uri download_file_uri = taskSnapshot.getDownloadUrl();
                             String path_uri = download_file_uri.toString();
                             final String userID = intent.getStringExtra("userid");
                             String userID_from_ = FirebaseAuth.getInstance().getCurrentUser().getUid();
                             databaseReference = FirebaseDatabase.getInstance().getReference();
                             HashMap<String, Object> map = new HashMap<>();
                             map.put("sender", userID);
                             map.put("reciver", userID_from_);
                             map.put("filename", fileName);
                             map.put("fileURL", path_uri);
                             databaseReference.child("Chats").push().setValue(map);
                             progressDialog.dismiss();
                             Toast.makeText(getApplicationContext(), "File Uploaded !", Toast.LENGTH_LONG).show();
                         }
                     })
                     .addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             progressDialog.dismiss();
                             Toast.makeText(getApplicationContext(), "File Error !", Toast.LENGTH_LONG).show();
                         }
                     })
                     .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                         @Override
                         public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                             @SuppressWarnings("VisibleForTests") double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                             progressDialog.setMessage("Uploaded    " + (int) progress + "..");

                         }
                     });
         }
     }

     void download_file(Uri file_url_downloaded){
         String file_name_full = getFileName(file_url_downloaded);
         StorageReference storageReference = firebaseStorage.getReference();
         StorageReference ref = firebaseStorage.getReferenceFromUrl("gs://mizgram-3701b.appspot.com/File_Uploads").child(file_name_full);
         //StorageReference downloadref = storageReference.child(file_url_download.toString());
         Log.i("don url", ref.toString());

         File file_on_device = new File(DOWNLOAD_DIR + "/" + file_name_full);

         ref.getFile(file_on_device).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
             @Override
             public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                 Log.i("don url", "file downloaded");
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Log.i("don url", "file not ...");
             }
         }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
             @Override
             public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                 @SuppressWarnings("VisibleForTests") double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                 Log.i("task complated", String.valueOf((int)progress) + " " + " %");
             }
         });

         //String extention = file_name_full.substring(file_name_full.lastIndexOf("."));
         //String file_name = file_name_full.substring(0,file_name_full.lastIndexOf("."));
         //




         /*

         if(file_name != null && extention != null){
             Log.i("file name ", file_name);
             Log.i("extention ", extention);
         }
         */

     }

    @Override
    public void onClick(View view, int position) {
        Chat chat = mchat.get(position);
        file_url_download = Uri.parse(chat.getFileURL());
        download_file(Uri.parse(chat.getFileURL()));
        //Log.i("file clicked ", file_url_download.toString());
        //Toast.makeText(getApplicationContext(), chat.getFileURL(),Toast.LENGTH_LONG).show();
    }


    private void openImage() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select a file"), FILE_REQUEST);
        //startActivityForResult(intent, FILE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Toast.makeText(getContext(),"hdghg",Toast.LENGTH_LONG).show();
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            file_url = data.getData();
            if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getApplicationContext(),"Upload Inprogress",Toast.LENGTH_LONG).show();
            }else {

                uploadFile();
            }
        }
    }


    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private String getFileExtentions(Uri uri){
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //databaseReference.removeEventListener(seenListener);
        status("offline");
        DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
        String dateString = dateFormat.format(new Date()).toString();
        last_seen(dateString);
    }


}
