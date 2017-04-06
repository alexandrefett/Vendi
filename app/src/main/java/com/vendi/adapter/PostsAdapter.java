package com.vendi.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.vendi.MyApplication;
import com.vendi.model.Post;
import com.vendi.PostViewActivity;
import com.vendi.R;

import java.util.ArrayList;

/**
 * Created by Alexandre on 22/07/2016.
 */
public class PostsAdapter extends RecyclerView.Adapter<PostViewHolder>{
    
    private Context context;
    private ArrayList<Post> array;
    private Activity a;
    private int position;

    public PostsAdapter(Activity a, Context c, ArrayList<Post> p){
        this.a = a;
        this.context = c;
        this.array = p;

    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        PostViewHolder viewHolder = new PostViewHolder(context, view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PostViewHolder viewHolder, final int position){
        final Post post = array.get(position);

        Glide.with(context)
            .load(post.getImage())
            .crossFade()
            .into(viewHolder.image);

        Glide.with(context)
                .load(post.getPicture())
                .placeholder(R.drawable.ic_action_user)
                .dontAnimate()
                .into(viewHolder.picture);

        viewHolder.image.setOnClickListener(null);
        viewHolder.description.setText(post.getDescription());
        viewHolder.name.setText(post.getUser());
        viewHolder.time.setText(DateUtils.getRelativeTimeSpanString(post.getTimestamp(), System.currentTimeMillis(),1000*60));
        viewHolder.image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, PostViewActivity.class);
                MyApplication.getInstance().setPost(post);
                int result = 4;
                a.startActivityForResult(i, result);
            }
        });
        this.position = position;
    }

    public int getPosition(){
        return position;
    }

    @Override
    public int getItemCount(){
        return array.size();
    }

    public ArrayList<Post> getData(){
        return array;
    }
}
