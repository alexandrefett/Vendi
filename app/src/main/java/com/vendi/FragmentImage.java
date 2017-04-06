package com.vendi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


public class FragmentImage extends Fragment {
    private static String TAG="--->>>";
    private String url="";
    private ImageView imageView;

    public FragmentImage(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if(args!=null) {
            url = args.getString("url", "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.layout_image, container, false);
        imageView = (ImageView)view.findViewById(R.id.imageView);
        Glide.with(getContext())
                .load(url)
                .dontAnimate()
                .into(imageView);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
    }
}
