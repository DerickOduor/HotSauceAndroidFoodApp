package com.derickoduor.hotsauce;

import android.content.Context;
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
 * Created by Derick Oduor on 3/27/2018.
 */

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.MyViewHolder>{
    private Context context;
    List<Food> foodList;

    public FoodAdapter(List<Food> foodList,Context context){
        this.foodList=foodList;
        this.context=context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ItemClickListener itemClickListener;
        TextView food_name;
        ImageView food_image;

        public MyViewHolder(View itemView) {
            super(itemView);
            food_image=(ImageView)itemView.findViewById(R.id.food_image);
            food_name=(TextView)itemView.findViewById(R.id.food_name);

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
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FoodAdapter.MyViewHolder holder, int position) {
        final Food food=foodList.get(position);
        holder.food_name.setText(food.getName());
        Picasso.with(context).
                load(food.getImage()).
                into(holder.food_image);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Toast.makeText(context," "+food.getName()+" Kshs."+food.getPrice(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }
}
