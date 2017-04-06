package com.vendi;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vendi.model.Category;
import com.vendi.model.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;

//import com.google.android.gms.appindexing.AppIndex;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {

    protected static final int REQUEST_AUTH = 1;
    protected static final int REQUEST_POST = 2;
    protected static final int REQUEST_REGISTER = 3;
    protected static final int REQUEST_POST_VIEW = 4;
    protected static final int REQUEST_STORE = 5;
    protected static final int REQUEST_FOLLOW = 6;
    protected static final String TAG = "--->>>";
    protected User user;
    protected ArrayList<Category> categories;

    protected MyApplication app;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser fbuser;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }

        MyApplication.getInstance().setContext(this);

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        fbuser = mAuth.getCurrentUser();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                fbuser = firebaseAuth.getCurrentUser();
            }
        };

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        User u = Prefs.getUser();
        if (fbuser != null) {
            Log.d(TAG, "fbuser:" + fbuser.getUid());
            View h = navigationView.getHeaderView(0);
            CircleImageView nav_pic = (CircleImageView)h.findViewById(R.id.user_pic);
            ImageView blur = (ImageView)h.findViewById(R.id.user_blur);
            TextView nav_fullname = (TextView)h.findViewById(R.id.user_fullname);
            TextView nav_user = (TextView)h.findViewById(R.id.user_email);
            nav_fullname.setText(u.getFullname());
            nav_user.setText(u.getUsername());
            Glide.with(this)
                    .load(u.getPicture())
                    .placeholder(R.drawable.ic_action_user)
                    .dontAnimate()
                    .into(nav_pic);
            Glide.with(this)
                    .load(u.getPicture())
                    .bitmapTransform(new BlurTransformation(this, 70))
                    .into(blur);
            getCategories();
        } else {
            if (Prefs.getStatus() == User.USER_NULL) {
                Log.d(TAG, "user not exist:");
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivityForResult(intent, REQUEST_REGISTER);
            } else {
                Log.d(TAG, "login:" + u.getEmail());
                signIn(u.getEmail(), u.getPhone());
            }
        }
    }
    public static FragmentList newInstance(String cat_id)
    {
        FragmentList fragment = new FragmentList();
        Bundle bundle = new Bundle();
        bundle.putString("category", cat_id);
        fragment.setArguments(bundle);
        return fragment ;
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(newInstance(categories.get(0).getCategory_uid()),categories.get(0).getCategory());
        adapter.addFragment(newInstance(categories.get(1).getCategory_uid()),categories.get(1).getCategory());
        adapter.addFragment(newInstance(categories.get(2).getCategory_uid()),categories.get(2).getCategory());
        adapter.addFragment(newInstance(categories.get(3).getCategory_uid()),categories.get(3).getCategory());
        viewPager.setAdapter(adapter);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(R.drawable.ic_action_tshirt);
        }
        tabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                Prefs.setCategory(categories.get(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(Tab tab) {

            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            doAlert(getResources().getString(R.string.error_invalid_password));
                        }
                    }
                });
    }

    private void getCategories(){
        Log.d(TAG, "getCategories");
        myRef.child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "dataSnapshot");
                if(dataSnapshot==null)
                    Log.d(TAG, "dataSnapshot==null");
                else {
                    Log.d(TAG, "onDataChange size:" + dataSnapshot.getChildrenCount());
                    categories = new ArrayList<Category>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Category cat = new Category(child.getValue());
                        cat.setCategory_uid(child.getKey());
                        categories.add(cat);
                        Log.d(TAG, "Cat:" + cat.getCategory());
                    }
                    setupViewPager(viewPager);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "showCategories Canceled");
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case REQUEST_REGISTER:
                if (resultCode == RESULT_OK) {
                    Intent i = new Intent(this, LoginActivity.class);
                    startActivityForResult(i, REQUEST_AUTH);
                } else {
                    finish();
                }
                break;
            case REQUEST_AUTH:
                if (resultCode == RESULT_OK) {
                    getCategories();
                } else {
                    finish();
                }
                break;
            case REQUEST_POST:
                break;
            case REQUEST_POST_VIEW:
                if (resultCode == RESULT_OK) {
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.action_post: {
                intent = new Intent(getBaseContext(), PostActivity.class);
                startActivityForResult(intent, REQUEST_POST);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch(id) {
            case R.id.my_posts: {
                Log.d(TAG, "call my account: "+Prefs.getUser().getEmail());
                intent = new Intent(this, StoreActivity.class);
                intent.putExtra(User.EMAIL,Prefs.getUser().getEmail());
                startActivityForResult(intent, REQUEST_STORE);
                return true;
            }
            case R.id.my_notification: {
                return true;
            }
            case R.id.my_follow: {
                Log.d(TAG, "call my follow "+Prefs.getUser().getEmail());
                intent = new Intent(this, FollowActivity.class);
                startActivityForResult(intent, REQUEST_FOLLOW);
                return true;
            }
            case R.id.my_likes: {
                return true;
            }
            case R.id.my_share: {
                return true;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void doAlert(String msg) {
        android.support.v7.app.AlertDialog alertDialog = new Builder(MainActivity.this).create();
        alertDialog.setMessage(msg);
        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private final FragmentManager manager;

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
            this.manager = manager;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
