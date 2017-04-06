package com.vendi.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.Query;
import com.vendi.MyApplication;
import com.vendi.model.Post;
import com.vendi.PostViewActivity;
import com.vendi.R;

import java.util.ArrayList;

/**
 * Created by Alexandre on 22/07/2016.
 */
public class FirebaseMyPostsAdapter extends FirebaseRecyclerAdapter<FirebaseMyPostsAdapter.PostViewHolder, Post>{

    public static class PostViewHolder extends RecyclerView.ViewHolder{
        public ImageView image;
        public TextView description;
        public TextView time;

        public PostViewHolder (View itemView){
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.item_post_desc);
            image = (ImageView) itemView.findViewById(R.id.item_post_image);
            time = (TextView) itemView.findViewById(R.id.item_card_time);
        }
    }

    private Context context;
    private Activity activity;

    public FirebaseMyPostsAdapter(Query query, Class<Post> itemClass, @Nullable ArrayList<Post> items,
                                  @Nullable ArrayList<String> keys) {
        super(query, itemClass, items, keys);
    }

    public void setContext(Context c, Activity a ){
        this.context = c;
        this.activity =  a;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_profile, parent, false);
        PostViewHolder viewHolder = new PostViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PostViewHolder viewHolder, final int position){
        final Post post = getItem(position);
        Glide.with(context)
            .load(post.getImage().get("img1"))
            .crossFade()
            .into(viewHolder.image);

        viewHolder.image.setOnClickListener(null);
        viewHolder.description.setText(post.getDescription());
        viewHolder.time.setText(DateUtils.getRelativeTimeSpanString(post.getTimestamp()*-1, System.currentTimeMillis(),1000));
        viewHolder.image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, PostViewActivity.class);
                MyApplication.getInstance().setPost(post);
                int result = 4;
                activity.startActivityForResult(i, result);
            }
        });
    }


    @Override
    protected void itemAdded(Post item, String key, int position) {

    }

    @Override
    protected void itemChanged(Post oldItem, Post newItem, String key, int position) {

    }

    @Override
    protected void itemRemoved(Post item, String key, int position) {

    }

    @Override
    protected void itemMoved(Post item, String key, int oldPosition, int newPosition) {

    }
}
