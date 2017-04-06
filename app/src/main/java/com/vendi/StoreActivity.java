package com.vendi;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vendi.adapter.FirebaseMyPostsAdapter;
import com.vendi.model.Post;
import com.vendi.model.User;
import com.vendi.task.SendMsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class StoreActivity extends AppCompatActivity implements OnClickListener{

    private static String TAG = "--->>>";
    private User user;
    private TextView sale;
    private TextView pos;
    private TextView neg;
    private ImageView pic;
    private RecyclerView list;
    private RecyclerView.LayoutManager mLayoutManager;
    private StaggeredGridLayoutManager ml;
    private ArrayList<Post> posts;
    private ArrayList<String> postsKeys;
    private FirebaseMyPostsAdapter postAdapter;
    private DatabaseReference myRef;
    private ToggleButton button;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        //user = MyApplication.getInstance().getUser(uid);

        Toolbar toolbar = (Toolbar) findViewById(R.id.store_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myRef = FirebaseDatabase.getInstance().getReference();

        sale = (TextView)findViewById(R.id.store_sales);
        list = (RecyclerView)findViewById(R.id.store_list);
        pic = (ImageView)findViewById(R.id.user_pic);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_store, menu);
        final MenuItem toggle = menu.findItem(R.id.menu_follow);
        button = (ToggleButton) MenuItemCompat.getActionView(toggle);

        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Bundle extras = getIntent().getExtras();
        String uid = extras.getString(User.EMAIL);

        getUser(uid);
        getPosts(uid);

        return true;
    }

    private void getUser(String username){
        Log.d(TAG, "username: " + username);
        myRef.child("users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {
                    Log.d(TAG, "getUserPost: " + dataSnapshot.toString());
                    user = dataSnapshot.getValue(User.class);
                    initView();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getPosts(String uid){
        posts = new ArrayList<Post>();
        postsKeys = new ArrayList<String>();
        Query recentPostsQuery = myRef.child("/user-posts/"+uid).orderByChild(Post.TIMESTAMP);
        recentPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                    sale.setText(""+dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        postAdapter = new FirebaseMyPostsAdapter(recentPostsQuery, Post.class, posts, postsKeys);
        postAdapter.setContext(this, this);
        list.setItemAnimator(new DefaultItemAnimator());
        list.setLayoutManager(new StaggeredGridLayoutManager(2,1));
        list.setAdapter(postAdapter);
    }

    private void addFollow(){
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/"+user.getUsername()+"/ifollow/"+Prefs.getUser().getUsername(), true);
        childUpdates.put("/users/"+Prefs.getUser().getUsername()+"/followme/"+user.getUsername(), true);

        myRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Log.d(TAG, "sddfollow: " + task.isSuccessful());
            }
        });

    }

    private void removeFollow(){
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/"+user.getUsername()+"/ifollow/"+Prefs.getUser().getUsername(), null);
        childUpdates.put("/users/"+Prefs.getUser().getUsername()+"/followme/"+user.getUsername(), null);

        myRef.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "sddfollow: " + task.isSuccessful());
            }
        });
    }


    private void initView(){
        Log.d(TAG, "getProfile: ");

        TextView name = (TextView)findViewById(R.id.user_fullname);
        name.setText(user.getFullname());

        TextView location = (TextView)findViewById(R.id.user_email);
        location.setText(user.getLocation());

        TextView followme = (TextView)findViewById(R.id.store_followme);
        followme.setText(""+user.getIfollow().size());

        TextView ifollow = (TextView)findViewById(R.id.store_ifollow);
        ifollow.setText(""+user.getFollowme().size());

        Glide.with(this)
                .load(user.getPicture())
                .placeholder(R.drawable.ic_action_user)
                .dontAnimate()
                .into(pic);

        ImageView blur = (ImageView)findViewById(R.id.user_blur);

        Glide.with(this)
                .load(user.getPicture())
                .bitmapTransform(new BlurTransformation(this, 70))
                .into(blur);

        if(user.getEmail().equals(Prefs.getUser().getEmail()))
            button.setVisibility(View.GONE);
        else
            button.setVisibility(View.VISIBLE);

        if(user.getIfollow().containsKey(Prefs.getUser().getUsername()))
            button.setChecked(true);

        button.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (button.isChecked()) {
                    addFollow();
                    SendMsg task = new SendMsg(user.getToken(),"Mensagem pra você",Prefs.getUser().getUsername()+" está seguindo você.");
                    task.execute();
                } else {
                    removeFollow();
                }
            }
        });


        //pos.setText(success.getInt("positive"));
        //neg.setText(success.getInt("negative"));
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public void onClick(View view) {
    }
}
