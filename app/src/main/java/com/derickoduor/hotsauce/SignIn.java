package com.derickoduor.hotsauce;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SignIn extends AppCompatActivity {

    EditText username,password;
    Button signIn;

    String user=null,pass=null;
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
        setContentView(R.layout.activity_sign_in);

        username=(EditText)findViewById(R.id.usernameL);
        password=(EditText)findViewById(R.id.passwordL);
        signIn=(Button)findViewById(R.id.signInBtn);
        Typeface typeface=Typeface.createFromAsset(getAssets(),"fonts/Caviar_Dreams_Bold.ttf");
        signIn.setTypeface(typeface);

        loginPreferences=getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
        editor=loginPreferences.edit();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user=username.getText().toString();
                pass=password.getText().toString();
                if(user.equalsIgnoreCase("")||pass.equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(),"Fill in all spaces!",Toast.LENGTH_LONG).show();
                }else{
                    new SignInTask().execute(user,pass);
                }
            }
        });
    }

    private class SignInTask extends AsyncTask<String,String,String> {

        HttpURLConnection conn;
        URL url=null;
        TextView errorMsg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(),"Logging in...",Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                url=new URL("http://derickoduor.000webhostapp.com/loginFood.php");
            }catch(MalformedURLException e){
                return e.getMessage();
            }
            try{
                conn=(HttpURLConnection)url.openConnection();
                conn.setConnectTimeout(45000);
                conn.setReadTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                Uri.Builder builder=new Uri.Builder().appendQueryParameter("username",strings[0]).appendQueryParameter("password",strings[1]);

                String query=builder.build().getEncodedQuery();
                OutputStream os=conn.getOutputStream();
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
            }catch(IOException e){return e.getMessage();}

            try{
                int responseCode=conn.getResponseCode();
                if(responseCode==HttpURLConnection.HTTP_OK){
                    InputStream is=conn.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(is));
                    StringBuilder result=new StringBuilder();
                    String line;
                    while ((line=reader.readLine())!=null){
                        result.append(line);
                    }
                    return result.toString();
                }else{
                    return "unsuccessful";
                }
            }catch (IOException e){
                return e.getMessage();
            }finally {
                conn.disconnect();
            }

            //return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                JSONObject jsonObject=new JSONObject(s);
                int success=jsonObject.getInt("success");
                if(success==1){
                    Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SignIn.this,Categories.class));
                    JSONArray loginDetails=jsonObject.getJSONArray("login_details");
                    for(int i=0;i<loginDetails.length();i++){
                        JSONObject detail=loginDetails.getJSONObject(i);
                        login_id=detail.getInt("id");
                        login_name=detail.getString("username");
                        login_password=detail.getString("password");
                        login_phone=detail.getInt("phone");
                    }
                    /*//if(login_name.equalsIgnoreCase("")){
                        editor.putString(PREFUSERNAME,login_name);
                        editor.putString(PREFPASSWORD,login_password);
                        editor.putInt(PREFID,login_id);
                        editor.putBoolean(PREFREMEMBER,true);
                        editor.putInt(PREFPHONE,login_phone);
                        editor.apply();
                        //User user=new User(login_name,login_password,login_id,login_phone);
                        //Common.currentUser=user;
                        Toast.makeText(getApplicationContext(),"Loggged in as: "+login_name,Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SignIn.this,Categories.class));
                    //}else{
                      //  Toast.makeText(getApplicationContext(),"Login Failed!",Toast.LENGTH_LONG).show();
                    //}*/

                }else{
                    /*errorMsg=(TextView)findViewById(R.id.errorMsg);
                    errorMsg.setText(s);*/
                    Toast.makeText(getApplicationContext(),"Login Failed!",Toast.LENGTH_LONG).show();
                }
            }catch(JSONException e){}
        }
    }
}
