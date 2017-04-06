package com.vendi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vendi.adapter.FirebaseFollowAdapter;
import com.vendi.model.User;

import java.util.ArrayList;


public class FragmentFollow extends Fragment {
    public interface FollowObjectListener {
        public void onFollow();
    }
    private FollowObjectListener listener;

    private static String TAG="--->>>";
    private DatabaseReference myRef;
    private RecyclerView list;
    private String uid;
    private boolean flag=false;
    private String title;
    private long count;
    private Query query;

    public void setQuery(Query query){
        this.query = query;
    }
    public void setOnFragmentFollow(FollowObjectListener followObjectListener){
        this.listener = followObjectListener;
    }

    public FragmentFollow(){
    }

    public String getTitle(){
        return title+" ("+count+")";
    }
    public void setTitle(String title){
        this.title = title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args!=null) {
            flag = args.getBoolean("flag", false);
        }

        myRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_fragment_list, container, false);
        list = (RecyclerView) view.findViewById(R.id.list);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        getFollowMe();

    }

    private void getFollowMe(){
        ArrayList<User> users = new ArrayList<User>();
        ArrayList<String> usersKeys = new ArrayList<String>();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                count = dataSnapshot.getChildrenCount();
                if (listener != null)
                    listener.onFollow();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseFollowAdapter postAdapter = new FirebaseFollowAdapter(query, User.class, users, usersKeys);
        postAdapter.setContext(this.getContext(), this.getActivity());
        list.setItemAnimator(new DefaultItemAnimator());
        list.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        list.setAdapter(postAdapter);

    }
}
