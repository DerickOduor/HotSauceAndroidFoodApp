package com.derickoduor.hotsauce.ViewHolder;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.derickoduor.hotsauce.Interface.ItemClickListener;
import com.derickoduor.hotsauce.R;

/**
 * Created by Derick Oduor on 3/24/2018.
 */




public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView textMenuName;
    ImageView imageView;

    ItemClickListener itemClickListener;

    public MenuViewHolder(View itemView) {
        super(itemView);
        textMenuName=(TextView)itemView.findViewById(R.id.menu_name);
        imageView=(ImageView)itemView.findViewById(R.id.menu_image);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}
