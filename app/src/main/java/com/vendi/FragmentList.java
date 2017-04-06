package com.vendi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vendi.adapter.FirebaseMyPostsAdapter;
import com.vendi.model.Post;

import java.util.ArrayList;


public class FragmentList extends Fragment {
    private static String TAG="--->>>";
    private DatabaseReference myRef;
    private RecyclerView listPost;
    private String category="";

    public FragmentList(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if(args!=null) {
            category = args.getString("category", "");
        }
        myRef = FirebaseDatabase.getInstance().getReference().child("categories/"+category+"/posts");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_fragment_list, container, false);
        listPost = (RecyclerView) view.findViewById(R.id.list);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        getPosts(category);

    }

    private void getPosts(String cat){
        ArrayList<Post> posts = new ArrayList<Post>();
        ArrayList<String> postsKeys = new ArrayList<String>();
        Log.d(TAG, "Query start at:"+cat);
        Query recentPostsQuery = myRef.orderByChild(Post.TIMESTAMP);
        recentPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "getPosts : "+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseMyPostsAdapter postAdapter = new FirebaseMyPostsAdapter(recentPostsQuery, Post.class, posts, postsKeys);
        postAdapter.setContext(this.getContext(), this.getActivity());
        listPost.setItemAnimator(new DefaultItemAnimator());
        StaggeredGridLayoutManager stag = new StaggeredGridLayoutManager(2,1);
        listPost.setLayoutManager(stag);
        listPost.setAdapter(postAdapter);
    }
}
