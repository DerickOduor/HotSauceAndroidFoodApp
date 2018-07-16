package com.derickoduor.hotsauce;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.derickoduor.hotsauce.Interface.ItemClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {

    RecyclerView rvFood;
    LinearLayoutManager llm;
    List<Food> foodList;
    Context context;
    FoodAdapter1 foodAdapter;
    String menu_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        if(getIntent()!=null){
            menu_id=getIntent().getStringExtra("menu_id");
            new FoodTask().execute(menu_id);

        }else{
            Toast.makeText(getApplicationContext(),"Empty menu id",Toast.LENGTH_LONG).show();
        }

        rvFood=(RecyclerView)findViewById(R.id.recycler_food);
        llm=new LinearLayoutManager(this);

    }

    private class FoodTask extends AsyncTask<String,String,String> {

        HttpURLConnection conn;
        URL url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                url=new URL("http://derickoduor.000webhostapp.com/getFoods.php");
                conn=(HttpURLConnection)url.openConnection();
                conn.setConnectTimeout(45000);
                conn.setReadTimeout(15000);
                conn.setDoOutput(true);
                conn.setDoInput(true);

                Uri.Builder builder=new Uri.Builder().appendQueryParameter("menu_id",strings[0]);

                String query=builder.build().getEncodedQuery();
                OutputStream os=conn.getOutputStream();
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();

                int responseCode=conn.getResponseCode();
                if(responseCode==HttpURLConnection.HTTP_OK){
                    InputStream is=conn.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(is));
                    StringBuilder result=new StringBuilder();
                    String line=null;

                    while ((line=reader.readLine())!=null){
                        result.append(line);
                    }
                    return result.toString();
                }else{
                    return "unsuccessful";
                }
            }catch (Exception e){return e.getMessage();}

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                JSONObject jsonObject=new JSONObject(s);
                int success=jsonObject.getInt("success");
                int count=0;
                if(success==1){
                    JSONArray foodArray=jsonObject.getJSONArray("foods");
                    foodList=new ArrayList<>();
                    for(int i=0;i<foodArray.length();i++){
                        count++;
                        JSONObject foodItem=foodArray.getJSONObject(i);
                        foodList.add(new Food(foodItem.getInt("id"),foodItem.getInt("menu_id"),
                                foodItem.getString("name"),foodItem.getString("description"),
                                "http://derickoduor.000webhostapp.com/androidTest/images/"+foodItem.getString("image"),
                                foodItem.getDouble("price"),foodItem.getDouble("discount")));
                    }

                    rvFood.setLayoutManager(llm);
                    foodAdapter=new FoodAdapter1(foodList,context);
                    rvFood.hasFixedSize();
                    rvFood.setAdapter(foodAdapter);
                }else{
                    Toast.makeText(getApplicationContext()," Failed "+success,Toast.LENGTH_LONG).show();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public class FoodAdapter1 extends RecyclerView.Adapter<FoodAdapter1.MyViewHolder>{
        private Context context;
        List<Food> foodList;

        public FoodAdapter1(List<Food> foodList,Context context){
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
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final Food food=foodList.get(position);
            holder.food_name.setText(food.getName());
            Picasso.with(context).
                    load(food.getImage()).
                    into(holder.food_image);
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    //Toast.makeText(context," "+food.getName()+" Kshs."+food.getPrice(),Toast.LENGTH_LONG).show();
                    String s=Integer.toString(food.getId());
                    Intent i=new Intent(FoodList.this,FoodDetail.class);
                    i.putExtra("food_id",s);
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return foodList.size();
        }
    }

}
