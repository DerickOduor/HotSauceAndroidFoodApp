package com.derickoduor.hotsauce;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.derickoduor.hotsauce.Interface.FoodOrderDetails;
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

public class FoodDetail extends AppCompatActivity implements FoodOrderDetails{

    String food_id="";
    TextView food_name,food_price,food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    EditText qty;

    RecyclerView rvFood;
    FoodAdapter foodAdapter;
    FoodOrderDetails foodOrderDetails;

    List<Food> foodDetail;
    List<Order> orderFoodDetail;
    Context context;
    Order currentFood;

    HttpURLConnection conn;
    URL url;

    String login_name="",login_password="";
    int login_id=0,login_phone=0;

    SharedPreferences loginPreferences;
    SharedPreferences.Editor editor;
    static final String PREFNAME="LoginPrefs";
    static final String PREFUSERNAME="LoginUser";
    static final String PREFPASSWORD="LoginPassword";
    static final String PREFID="LoginId";
    static final String PREFPHONE="LoginPhone";
    static final String PREFREMEMBER="LoginRemember";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        loginPreferences=getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
        editor=loginPreferences.edit();

        if(loginPreferences.getBoolean(PREFREMEMBER,true)){
            login_name=loginPreferences.getString(PREFUSERNAME,"abc");
            login_phone=loginPreferences.getInt(PREFPHONE,1);
            login_id=loginPreferences.getInt(PREFID,1);
        }

        if(getIntent()!=null){
            food_id=getIntent().getStringExtra("food_id");
            new FoodTask().execute(food_id);

        }

        //rvFood=(RecyclerView)findViewById(R.id.)

        food_description=(TextView)findViewById(R.id.food_description);
        food_price=(TextView)findViewById(R.id.food_price);
        food_name=(TextView)findViewById(R.id.food_name);
        food_image=(ImageView)findViewById(R.id.img_food);

        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        btnCart=(FloatingActionButton)findViewById(R.id.cartBtn);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FoodOrderDetailsTask1(FoodDetail.this).execute(food_id);
            }
        });
    }

    @Override
    public void onFoodOrderDetails(Order order) {
        new DatabaseHelper(getBaseContext()).addCart(order,login_id );
        Toast.makeText(getApplicationContext(),"Added to cart",Toast.LENGTH_SHORT).show();
    }

    private class FoodTask extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                url=new URL("http://derickoduor.000webhostapp.com/getFood.php");
                conn=(HttpURLConnection)url.openConnection();
                conn.setConnectTimeout(45000);
                conn.setReadTimeout(15000);
                conn.setDoOutput(true);
                conn.setDoInput(true);

                Uri.Builder builder=new Uri.Builder().appendQueryParameter("food_id",strings[0]);

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
                    //Toast.makeText(getApplicationContext(),s+" "+success,Toast.LENGTH_LONG).show();
                    JSONArray foodArray=jsonObject.getJSONArray("food");
                    foodDetail=new ArrayList<>();
                    for(int i=0;i<foodArray.length();i++){
                        count++;
                        JSONObject foodItem=foodArray.getJSONObject(i);
                        foodDetail.add(new Food(foodItem.getInt("id"),
                                foodItem.getInt("menu_id"),
                                foodItem.getString("name"),
                                foodItem.getString("description"),
                                "http://derickoduor.000webhostapp.com/androidTest/images/"+foodItem.getString("image"),
                                foodItem.getDouble("price"),foodItem.getDouble("discount")));

                        currentFood=new Order(foodItem.getInt("id"),2
                                /*Integer.parseInt(qty.getText().toString())*/,
                                foodItem.getDouble("price"),foodItem.getDouble("discount"),
                                foodItem.getString("name")
                                );
                        //Toast.makeText(getApplicationContext(),foodDetail.toString(),Toast.LENGTH_SHORT).show();
                        collapsingToolbarLayout.setTitle(foodItem.getString("name"));
                        food_description.setText(foodItem.getString("description"));
                        food_price.setText(Double.toString(foodItem.getDouble("price")));
                        food_name.setText(foodItem.getString("name"));
                        Picasso.with(getBaseContext()).load("http://derickoduor.000webhostapp.com/androidTest/images/"+foodItem.getString("image")).
                                into(food_image);
                    }
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

    public class FoodOrderDetailsTask1 extends AsyncTask<String,String,String> {

        Context mContext;
        Order orderFood;

        HttpURLConnection conn;
        URL url;
        //FoodOrderDetails foodOrderDetails;
        EditText qty;
        FoodDetail fd;

        public FoodOrderDetailsTask1(Context context){
            this.mContext=context;
            foodOrderDetails=(FoodOrderDetails)mContext;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(getApplicationContext(),"-started-",Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                url=new URL("http://derickoduor.000webhostapp.com/getFood.php");
                conn=(HttpURLConnection)url.openConnection();
                conn.setConnectTimeout(45000);
                conn.setReadTimeout(15000);
                conn.setDoOutput(true);
                conn.setDoInput(true);

                Uri.Builder builder=new Uri.Builder().appendQueryParameter("food_id",strings[0]);

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
            finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Toast.makeText(getApplicationContext(),"-end-",Toast.LENGTH_LONG).show();
            qty=(EditText)findViewById(R.id.qty);
            fd=new FoodDetail();
            try{
                JSONObject jsonObject=new JSONObject(s);
                int success=jsonObject.getInt("success");
                int count=0;
                if(success==1){
                    //Toast.makeText(getContext(),s+" "+success,Toast.LENGTH_LONG).show();
                    JSONArray foodArray=jsonObject.getJSONArray("food");
                    orderFoodDetail=new ArrayList<>();
                    for(int i=0;i<foodArray.length();i++){
                        count++;
                        JSONObject foodItem=foodArray.getJSONObject(i);
                        if((qty.getText().toString()).equalsIgnoreCase("")||(qty.getText().toString()).equalsIgnoreCase("0")){
                            Toast.makeText(getApplicationContext(),"Enter valid quantity",Toast.LENGTH_LONG).show();
                        }else{
                            int quantity=Integer.parseInt(qty.getText().toString());
                            orderFood=new Order(foodItem.getInt("id"),quantity
                            /*Integer.parseInt(qty.getText().toString())*/,
                                    foodItem.getDouble("price"),foodItem.getDouble("discount"),
                                    foodItem.getString("name")
                            );
                            //foodOrderDetails.onFoodOrderDetails(orderFood);
                            orderFoodDetail.add(new Order(foodItem.getInt("id"),quantity
                            /*Integer.parseInt(qty.getText().toString())*/,
                                    foodItem.getDouble("price"),foodItem.getDouble("discount"),
                                    foodItem.getString("name")
                            ));
                            foodOrderDetails.onFoodOrderDetails(orderFood);
                        }
                    }
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

}
