package com.vendi.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.Query;
import com.vendi.R;
import com.vendi.StoreActivity;
import com.vendi.model.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Alexandre on 22/07/2016.
 */
public class FirebaseFollowAdapter extends FirebaseRecyclerAdapter<FirebaseFollowAdapter.FollowViewHolder, User>{

    public static class FollowViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView image;
        public TextView username;

        public FollowViewHolder (View itemView){
            super(itemView);
            image = (CircleImageView) itemView.findViewById(R.id.user_pic);
            username = (TextView) itemView.findViewById(R.id.user_email);
        }
    }

    private Context context;
    private Activity activity;

    public FirebaseFollowAdapter(Query query, Class<User> itemClass, @Nullable ArrayList<User> items,
                                 @Nullable ArrayList<String> keys) {
        super(query, itemClass, items, keys);
    }

    public void setContext(Context c, Activity a ){
        this.context = c;
        this.activity =  a;
    }

    @Override
    public FollowViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_follow, parent, false);
        FollowViewHolder viewHolder = new FollowViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final FollowViewHolder viewHolder, final int position){
        final User user = getItem(position);

        Glide.with(context)
            .load(user.getPicture())
            .crossFade()
            .into(viewHolder.image);

        viewHolder.image.setOnClickListener(null);
        viewHolder.username.setText(user.getUsername());
        viewHolder.image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, StoreActivity.class);
                intent.putExtra(User.EMAIL, user.getEmail());
                activity.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void itemAdded(User item, String key, int position) {
    }

    @Override
    protected void itemChanged(User oldItem, User newItem, String key, int position) {

    }

    @Override
    protected void itemRemoved(User item, String key, int position) {

    }

    @Override
    protected void itemMoved(User item, String key, int oldPosition, int newPosition) {

    }
}
