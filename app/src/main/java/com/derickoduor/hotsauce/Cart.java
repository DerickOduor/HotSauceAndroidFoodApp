package com.derickoduor.hotsauce;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager llm;
    TextView totalPrice;
    Button placeOrder;

    List<Order> cart;
    CartAdapter cartAdapter;
    Order order;

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
        setContentView(R.layout.activity_cart);

        loginPreferences=getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
        editor=loginPreferences.edit();

        if(loginPreferences.getBoolean(PREFREMEMBER,true)){
            login_name=loginPreferences.getString(PREFUSERNAME,"abc");
            login_phone=loginPreferences.getInt(PREFPHONE,1);
            login_id=loginPreferences.getInt(PREFID,1);
        }

        recyclerView=(RecyclerView)findViewById(R.id.listCart);
        totalPrice=(TextView)findViewById(R.id.total);
        llm=new LinearLayoutManager(this);
        placeOrder=(Button)findViewById(R.id.btnPlaceOrder);


        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(llm);

        loadCart(login_id);

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialog=new AlertDialog.Builder(Cart.this);
                alertDialog.setTitle("One more step");
                alertDialog.setMessage("\nEnter your address:");

                final EditText address=new EditText(Cart.this);
                LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                address.setLayoutParams(lp);
                alertDialog.setView(address);
                alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),address.getText().toString(),Toast.LENGTH_LONG).show();
                        cart=new DatabaseHelper(Cart.this).getCart(login_id);
                        Date now=new Date();
                        DateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
                        String newDate=format.format(now);
                        int a=0;
                        for(int i=0;i<cart.size();i++){
                            order=cart.get(i);
                            a+=i;
                            new PlaceOrderTask().execute("ok",
                                    order.getName(),
                                    Integer.toString(order.getId()),
                                    Double.toString(order.getPrice()),
                                    Double.toString(order.getDiscount()),
                                    address.getText().toString(),
                                    login_name,
                                    Integer.toString(login_id),
                                    Integer.toString(login_phone),
                                    "HS-"+newDate,
                                    Integer.toString(order.getQty()));
                            //Toast.makeText(getApplicationContext(),i,Toast.LENGTH_SHORT).show();
                        }
                        new DatabaseHelper(Cart.this).cleanCart();
                        loadCart(login_id);
                        Toast.makeText(getApplicationContext(),"Your order has been placed!",Toast.LENGTH_SHORT).show();
                        //new PlaceOrderTask().execute("ok");
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

    }

    public void loadCart(int login_id){
        cart=new DatabaseHelper(this).getCart(login_id);
        cartAdapter=new CartAdapter(cart,this);
        recyclerView.setAdapter(cartAdapter);
        double total=0;

        Locale locale=new Locale("en","US");
        NumberFormat format=NumberFormat.getCurrencyInstance(locale);
        for (Order order:cart){
            total+=order.getPrice()*order.getQty();
        }
        totalPrice.setText(format.format(total));
    }

    public class PlaceOrderTask extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            try{
                url=new URL("http://derickoduor.000webhostapp.com/placeOrder.php");
                conn=(HttpURLConnection)url.openConnection();
                conn.setConnectTimeout(45000);
                conn.setReadTimeout(15000);
                conn.setDoOutput(true);
                conn.setDoInput(true);

                Uri.Builder builder=new Uri.Builder().appendQueryParameter("place_order",strings[0]).
                        appendQueryParameter("order_id",strings[9]).
                        appendQueryParameter("customer_id",strings[7]).
                        appendQueryParameter("customer_name",strings[6]).
                        appendQueryParameter("phone",strings[8]).
                        appendQueryParameter("food_id",strings[2]).
                        appendQueryParameter("food_name",strings[1]).
                        appendQueryParameter("price",strings[3]).
                        appendQueryParameter("quantity",strings[10]).
                        appendQueryParameter("address",strings[5]);

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
                    JSONArray foodArray=jsonObject.getJSONArray("order_placed");
                    //foodDetail=new ArrayList<>();
                    for(int i=0;i<foodArray.length();i++){
                        count++;
                        JSONObject foodItem=foodArray.getJSONObject(i);

                    }
                }else{
                    String msg=jsonObject.getString("order_placed");
                    Toast.makeText(getApplicationContext()," Failed "+success+" "+msg,Toast.LENGTH_LONG).show();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
/*
* address,foods,name,phone,total
* */