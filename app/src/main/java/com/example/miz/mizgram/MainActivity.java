package com.example.miz.mizgram;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.support.annotation.DrawableRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.miz.mizgram.Fragments.ChatFragment;
import com.example.miz.mizgram.Fragments.ProfileFragment;
import com.example.miz.mizgram.Fragments.UsersFragment;
import com.example.miz.mizgram.Models.Chat;
import com.example.miz.mizgram.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;
    TextView lastseen;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    boolean last_seen_bool = true;


    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();
    String formated_date = simpleDateFormat.format(date);

    DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
    String dateString2 = dateFormat2.format(new Date()).toString();
    	//System.out.println("Current date and time in AM/PM: "+dateString2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profile_image = (CircleImageView) findViewById(R.id.profile_image_main);
        username = (TextView) findViewById(R.id.user_name_main);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.keepSynced(true);

        //if(haveNetworkConnection()){
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    username.setText(user.getUsername());
                    if(user.getImageURL().equals("default")){
                        profile_image.setImageResource(R.mipmap.ic_launcher);
                    }else {
                        //Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                        Glide.with(getApplicationContext())
                                .load(user.getImageURL())
                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                .into(profile_image);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }


            });
            /*
        }else {

        }
        */

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);



        viewpagerAdapter viewpagerAdapter = new viewpagerAdapter(getSupportFragmentManager());
        viewpagerAdapter.addFragment(new UsersFragment(),"Users");
        viewpagerAdapter.addFragment(new ChatFragment(),"chats");
        viewpagerAdapter.addFragment(new ProfileFragment(),"Profile");

        viewPager.setAdapter(viewpagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    } // end of oncreate


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                //finish();
                return true;
        }

        return false;
    }

    class viewpagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;


        public viewpagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();

        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    private void status(String status){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        databaseReference.updateChildren(hashMap);
    }

    private void last_seen(String last_seen){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("lastseen",last_seen);
        databaseReference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");

    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
        DateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
        String dateString = dateFormat.format(new Date()).toString();
        last_seen(dateString);
    }






    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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

}
