package com.vendi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.vendi.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Alexandre on 22/07/2016.
 */
public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final Context context;
    public TextView name;
    public ImageView image;
    public CircleImageView picture;
    public TextView description;
    public TextView post_price;
    public ToggleButton like;
    public TextView text_like;
    public TextView time;
    public TextView location;

    public PostViewHolder (Context context, View itemView){
        super(itemView);
        this.context = context;
        name = (TextView)itemView.findViewById(R.id.item_post_name);
        description = (TextView) itemView.findViewById(R.id.item_post_desc);
        image = (ImageView) itemView.findViewById(R.id.item_post_image);
        picture = (CircleImageView) itemView.findViewById(R.id.user_pic);
        time = (TextView) itemView.findViewById(R.id.item_post_time);
        location = (TextView) itemView.findViewById(R.id.item_post_location);
    }

    @Override
    public void onClick(View v){
          // Log.d("--->>>","getId: "+v.getId());
    }
}
