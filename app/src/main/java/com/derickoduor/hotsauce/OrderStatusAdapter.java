package com.derickoduor.hotsauce;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.derickoduor.hotsauce.Interface.ItemClickListener;

import java.util.List;

/**
 * Created by Derick Oduor on 4/1/2018.
 */

public class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.MyViewHolder> {

    List<Order> orderList;
    Context context;

    public OrderStatusAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Order order=orderList.get(position);
        holder.orderAddress.setText(order.getAddress());
        holder.orderId.setText(order.getOrderId());
        holder.orderName.setText(order.getName());
        holder.orderPhone.setText(Integer.toString(order.getPhone()));
        holder.orderStatus.setText(order.getStatus());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView orderId,orderName,orderStatus,orderPhone,orderAddress;
        private ItemClickListener itemClickListener;

        public MyViewHolder(View itemView) {
            super(itemView);

            orderAddress=(TextView)itemView.findViewById(R.id.order_address);
            orderId=(TextView)itemView.findViewById(R.id.order_id);
            orderName=(TextView)itemView.findViewById(R.id.order_name);
            orderPhone=(TextView)itemView.findViewById(R.id.order_phone);
            orderStatus=(TextView)itemView.findViewById(R.id.order_status);

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
}
