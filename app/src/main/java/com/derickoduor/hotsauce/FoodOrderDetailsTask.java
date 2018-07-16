package com.derickoduor.hotsauce;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.EditText;
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

/**
 * Created by Derick Oduor on 4/1/2018.
 */

public class FoodOrderDetailsTask extends AsyncTask<String,String,String> {

    Context context;
    Order currentFood;

    HttpURLConnection conn;
    URL url;
    FoodOrderDetails foodOrderDetails;
    EditText qty;

    public FoodOrderDetailsTask(Context context){
        this.context=context;
        this.foodOrderDetails=(FoodOrderDetails)context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(context,"-started-",Toast.LENGTH_LONG).show();
    }

    @Override
    protected String doInBackground(String... strings) {
        try{
            url=new URL("http://derickoduor.000webhostapp.com/androidTest/getFood.php");
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
        Toast.makeText(context,"-end-",Toast.LENGTH_LONG).show();
        try{
            JSONObject jsonObject=new JSONObject(s);
            int success=jsonObject.getInt("success");
            int count=0;
            if(success==1){
                //Toast.makeText(getContext(),s+" "+success,Toast.LENGTH_LONG).show();
                JSONArray foodArray=jsonObject.getJSONArray("food");
                for(int i=0;i<foodArray.length();i++){
                    count++;
                    JSONObject foodItem=foodArray.getJSONObject(i);
                    currentFood=new Order(foodItem.getInt("id"),0
                            /*Integer.parseInt(qty.getText().toString())*/,
                            foodItem.getDouble("price"),foodItem.getDouble("discount"),
                            foodItem.getString("name")
                    );
                }
                foodOrderDetails.onFoodOrderDetails(currentFood);
                Toast.makeText(context,currentFood.getName(),Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context," Failed "+success,Toast.LENGTH_LONG).show();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
