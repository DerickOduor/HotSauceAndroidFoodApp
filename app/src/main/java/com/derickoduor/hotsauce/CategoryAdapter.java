package com.derickoduor.hotsauce;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.derickoduor.hotsauce.Interface.ItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Derick Oduor on 3/24/2018.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    List<CategoryClass> categoryList;
    Context context;

    public CategoryAdapter(List<CategoryClass> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ItemClickListener itemClickListener;
        TextView textMenuName;
        ImageView imageView;

        public MyViewHolder(View itemView) {
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

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final CategoryClass categoryClass=categoryList.get(position);
        holder.textMenuName.setText(categoryClass.getName());
        Picasso.with(context).load(categoryClass.getImageUrl()).
                into(holder.imageView);
        //CategoryClass clickItem=
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                //Toast.makeText(context,categoryClass.getName(),Toast.LENGTH_LONG).show();
                Intent i=new Intent(context,FoodList.class);
                i.putExtra("menu_id",categoryClass.getId());
                //startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

}
