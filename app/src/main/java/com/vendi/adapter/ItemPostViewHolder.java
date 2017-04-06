package com.vendi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vendi.R;

/**
 * Created by Alexandre on 22/07/2016.
 */
public class ItemPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final Context context;
    public ImageView image;
    public TextView description;
    public TextView time;

    public ItemPostViewHolder(Context context, View itemView){
        super(itemView);
        this.context = context;
        time = (TextView) itemView.findViewById(R.id.item_card_time);
        description = (TextView) itemView.findViewById(R.id.item_post_desc);
        image = (ImageView) itemView.findViewById(R.id.item_post_image);
    }

    @Override
    public void onClick(View v){
           //Log.d("--->>>","getId: "+v.getId());
    }
}
