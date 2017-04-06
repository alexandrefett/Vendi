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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.Query;
import com.vendi.MyApplication;
import com.vendi.PostViewActivity;
import com.vendi.R;
import com.vendi.model.Post;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Alexandre on 22/07/2016.
 */
public class FirebasePostsAdapter extends FirebaseRecyclerAdapter<FirebasePostsAdapter.PostViewHolder, Post>{

    public static class PostViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public ImageView image;
        public CircleImageView picture;
        public TextView description;
        public TextView time;
        public TextView location;

        public PostViewHolder (View itemView){
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.item_post_name);
            description = (TextView) itemView.findViewById(R.id.item_post_desc);
            image = (ImageView) itemView.findViewById(R.id.item_post_image);
            picture = (CircleImageView) itemView.findViewById(R.id.user_pic);
            time = (TextView) itemView.findViewById(R.id.item_post_time);
            location = (TextView) itemView.findViewById(R.id.item_post_location);
        }
    }

    private Context context;
    private Activity activity;

    public FirebasePostsAdapter(Query query, Class<Post> itemClass, @Nullable ArrayList<Post> items,
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
                .inflate(R.layout.item_card, parent, false);
        PostViewHolder viewHolder = new PostViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PostViewHolder viewHolder, final int position){
        final Post post = getItem(position);
        Glide.with(context)
            .load((String)post.getImage().get("img1"))
            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
            .dontAnimate()
            .into(viewHolder.image);

        Glide.with(context)
                .load(post.getPicture())
                .placeholder(R.drawable.ic_action_user)
                .dontAnimate()
                .into(viewHolder.picture);

        viewHolder.image.setOnClickListener(null);
        viewHolder.description.setText(post.getDescription());
        viewHolder.name.setText(post.getUser());
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
