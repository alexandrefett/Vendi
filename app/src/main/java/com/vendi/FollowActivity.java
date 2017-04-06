package com.vendi;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.vendi.FragmentFollow.FollowObjectListener;

import java.util.ArrayList;
import java.util.List;

//import com.google.android.gms.appindexing.AppIndex;

public class FollowActivity extends AppCompatActivity  {

    protected MyApplication app;
    private DatabaseReference myRef;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyApplication.getInstance().setContext(this);

        myRef = FirebaseDatabase.getInstance().getReference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Prefs.getUser().getUsername());
        getSupportActionBar().setSubtitle(Prefs.getUser().getLocation());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }
    public static FragmentFollow newInstance(boolean flag)
    {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        FragmentFollow fragment = new FragmentFollow();
        Query followQuery;
        if(!flag)
            followQuery = myRef.child("users").orderByChild("followme/"+Prefs.getUser().getUsername()).startAt(true);
        else
            followQuery = myRef.child("users").orderByChild("ifollow/"+Prefs.getUser().getUsername()).startAt(true);
        fragment.setQuery(followQuery);
        Bundle bundle = new Bundle();
        bundle.putBoolean("flag", flag);
        fragment.setArguments(bundle);
        return fragment ;
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Bundle b = new Bundle();
        b.putBoolean("flag", false);
        adapter.addFragment(newInstance(false), "Seguidores");
        adapter.addFragment(newInstance(true), "Seguindo");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<FragmentFollow> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private final FragmentManager manager;

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
            this.manager = manager;
        }

        @Override
        public FragmentFollow getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(FragmentFollow fragment, String title) {
            fragment.setTitle(title);
            fragment.setOnFragmentFollow(new FollowObjectListener() {
                @Override
                public void onFollow() {
                    notifyDataSetChanged();
                    Log.d("--->>>", "onFollow()");
                }
            });
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentList.get(position).getTitle();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Activity.RESULT_OK);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
