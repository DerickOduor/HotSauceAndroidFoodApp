package com.derickoduor.hotsauce;

import android.app.ProgressDialog;
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

public class SignUp extends AppCompatActivity {

    EditText username,password,txtPhone;
    Button signUpBtn;
    String user,pass,phone;

    SharedPreferences loginPreferences;
    SharedPreferences.Editor editor;
    static final String PREFNAME="LoginPrefs";
    static final String PREFUSERNAME="LoginUser";
    static final String PREFPASSWORD="LoginPassword";
    static final String PREFID="LoginId";
    static final String PREFREMEMBER="LoginRemember";

    String register_user="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        txtPhone=(EditText) findViewById(R.id.phone);
        signUpBtn=(Button)findViewById(R.id.signUpBtn);
        Typeface typeface=Typeface.createFromAsset(getAssets(),"fonts/Caviar_Dreams_Bold.ttf");
        signUpBtn.setTypeface(typeface);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user=username.getText().toString();
                pass=password.getText().toString();
                phone=txtPhone.getText().toString();

                if(user.equalsIgnoreCase("")||pass.equalsIgnoreCase("")||phone.length()!=10){
                    Toast.makeText(getApplicationContext(),"Fill in all spaces!",Toast.LENGTH_LONG).show();
                }else{
                    new SignUpTask().execute(user,pass,phone);
                }
            }
        });
    }

    public class SignUpTask extends AsyncTask<String,String,String>{

        HttpURLConnection conn;
        URL url=null;
        //ProgressDialog pd=new ProgressDialog(getApplicationContext());

        @Override
        protected void onPreExecute() {
            //pd.setMessage("\tLoading...");
            //pd.setCancelable(false);
            //pd.show();
            super.onPreExecute();
            Toast.makeText(getApplicationContext(),"Started",Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                //http://10.0.2.2
                //http://derickoduor.000webhostapp.com
                url=new URL("http://derickoduor.000webhostapp.com/registerFood.php");
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
                Uri.Builder builder=new Uri.Builder().appendQueryParameter("username",strings[0]).appendQueryParameter("password",strings[1])
                        .appendQueryParameter("phone",strings[2]);

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

        }

        @Override
        protected void onPostExecute(String s) {
            //pd.dismiss();
            super.onPostExecute(s);
            try{
                JSONObject jsonObject=new JSONObject(s);
                int success=jsonObject.getInt("success");
                if(success==1){
                    startActivity(new Intent(SignUp.this,SignIn.class));
                }else{
                    /*errorMsg=(TextView)findViewById(R.id.errorMsg);
                    errorMsg.setText(s);*/
                    Toast.makeText(getApplicationContext(),"Sign Up Failed!\n"+s,Toast.LENGTH_LONG).show();
                }
            }catch(JSONException e){}
        }
    }
}
