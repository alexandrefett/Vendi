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
import com.vendi.model.Post;
import com.vendi.PostViewActivity;
import com.vendi.R;

import java.util.ArrayList;

/**
 * Created by Alexandre on 22/07/2016.
 */
public class ItemPostsAdapter extends RecyclerView.Adapter<ItemPostViewHolder>{

    private Context context;
    private ArrayList<Post> array;
    private Activity a;

    public ItemPostsAdapter(Activity a, Context c, ArrayList<Post> p){
        this.a = a;
        this.context = c;
        this.array = p;

    }

    @Override
    public ItemPostViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.item_card_profile, parent, false);
        ItemPostViewHolder viewHolder = new ItemPostViewHolder(context, view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemPostViewHolder viewHolder, final int position){
        final Post post = array.get(position);
        viewHolder.image.setOnClickListener(null);
        Glide.with(context)
                .load(post.getImage())
                .crossFade()
                .into(viewHolder.image);

        viewHolder.description.setText(post.getDescription());
        viewHolder.time.setText(DateUtils.getRelativeTimeSpanString(post.getTimestamp(), System.currentTimeMillis(),1000*60));
        viewHolder.image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, PostViewActivity.class);
                i.putExtra(Post.POST_ID, post.getPost_id());
                int result = 4;
                a.startActivityForResult(i, result);
            }
        });

    }

    public void morePosts(ArrayList<Post> ps){
        array.addAll(array.size()-1,ps);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount(){
        return array.size();
    }
}
