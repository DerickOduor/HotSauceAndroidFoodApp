package com.derickoduor.hotsauce;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

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

public class OrderStatus extends AppCompatActivity {

    RecyclerView listOrders;

    HttpURLConnection conn;
    URL url;

    List<Order> orderList;
    Context context;
    OrderStatusAdapter adapter;

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
        setContentView(R.layout.activity_order_status);

        loginPreferences=getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
        editor=loginPreferences.edit();

        if(loginPreferences.getBoolean(PREFREMEMBER,true)){
            login_name=loginPreferences.getString(PREFUSERNAME,"abc");
            login_phone=loginPreferences.getInt(PREFPHONE,1);
            login_id=loginPreferences.getInt(PREFID,1);
        }

        listOrders=(RecyclerView)findViewById(R.id.listOrders);

        new OrderStatusTask().execute("get_orders",Integer.toString(login_id));
    }

    public class OrderStatusTask extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            try{
                url=new URL("http://derickoduor.000webhostapp.com/getOrders.php");
                conn=(HttpURLConnection)url.openConnection();
                conn.setConnectTimeout(45000);
                conn.setReadTimeout(15000);
                conn.setDoOutput(true);
                conn.setDoInput(true);

                Uri.Builder builder=new Uri.Builder().appendQueryParameter("get_orders",strings[0])
                        .appendQueryParameter("customer_id",strings[1]);

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
                    JSONArray ordersArray=jsonObject.getJSONArray("orders");
                    orderList=new ArrayList<>();
                    for(int i=0;i<ordersArray.length();i++){
                        count++;
                        JSONObject orderItem=ordersArray.getJSONObject(i);
                        orderList.add(new Order(orderItem.getInt("id"),orderItem.getInt("quantity"),
                                orderItem.getString("order_name"),orderItem.getDouble("price"),
                                orderItem.getDouble("discount"),orderItem.getInt("phone"),
                                orderItem.getString("address"),orderItem.getString("order_status"),
                                orderItem.getString("order_id"))
                        );
                    }
                    adapter=new OrderStatusAdapter(orderList,context);
                    listOrders.setLayoutManager(new LinearLayoutManager(OrderStatus.this));
                    listOrders.hasFixedSize();
                    listOrders.setAdapter(adapter);
                }else{
                    String msg=jsonObject.getString("orders");
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
