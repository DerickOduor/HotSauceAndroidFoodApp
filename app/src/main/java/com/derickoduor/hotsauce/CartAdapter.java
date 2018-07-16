package com.derickoduor.hotsauce;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.derickoduor.hotsauce.Interface.ItemClickListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Derick Oduor on 3/29/2018.
 */


class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txt_cart_name,txt_price;
    public TextView img_cart_count;
    private ItemClickListener itemClickListener;

    public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }

    public CartViewHolder(View itemView) {
        super(itemView);
        txt_cart_name=(TextView)itemView.findViewById(R.id.cart_item_name);
        txt_price=(TextView)itemView.findViewById(R.id.cart_item_price);
        img_cart_count=(TextView)itemView.findViewById(R.id.cart_item_count);
    }

    @Override
    public void onClick(View v) {

    }
}

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{

    private List<Order> orderList=new ArrayList<>();
    Context context;

    public CartAdapter(List<Order> orderList,Context context){
        this.context=context;
        this.orderList=orderList;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.cart_item,parent,false);

        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        final Order order=orderList.get(position);
        holder.img_cart_count.setText(Integer.toString(order.getQty()));
        Locale locale=new Locale("en","US");
        NumberFormat format=NumberFormat.getCurrencyInstance(locale);
        double price=(order.getPrice()*order.getQty());
        //holder.txt_price.setText(format.format(price));
        holder.txt_price.setText(Double.toString(price));
        holder.txt_cart_name.setText(order.getName());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
