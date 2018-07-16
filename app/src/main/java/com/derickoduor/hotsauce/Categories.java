package com.derickoduor.hotsauce;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.derickoduor.hotsauce.Interface.ItemClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Categories extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView fullName;
    RecyclerView rvMenu;
    List<CategoryClass> categoryClassList;
    Context context;
    CategoryAdapter1 categoryAdapter;

    SharedPreferences loginPreferences;
    SharedPreferences.Editor editor;
    static final String PREFNAME="LoginPrefs";
    static final String PREFUSERNAME="LoginUser";
    static final String PREFPASSWORD="LoginPassword";
    static final String PREFID="LoginId";
    static final String PREFREMEMBER="LoginRemember";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        rvMenu=(RecyclerView)findViewById(R.id.rvMenu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        loginPreferences=getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
        editor=loginPreferences.edit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                startActivity(new Intent(Categories.this,Cart.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView=navigationView.getHeaderView(0);
        fullName=(TextView)headerView.findViewById(R.id.fullName);
        fullName.setText(loginPreferences.getString(PREFUSERNAME,""));

        new CategoriesTask().execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.categories, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        } else if (id == R.id.nav_cart) {
            startActivity(new Intent(Categories.this,Cart.class));
        } else if (id == R.id.nav_orders) {
            startActivity(new Intent(Categories.this,OrderStatus.class));
        } else if (id == R.id.nav_sign_out) {
            Intent i=new Intent(Categories.this,SignIn.class);
            editor.clear();
            editor.commit();
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class CategoriesTask extends AsyncTask<String,String,String>{

        HttpURLConnection conn;
        URL url;
        Context c;
        RecyclerView rv;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                url=new URL("http://derickoduor.000webhostapp.com/getCategories.php");
                conn=(HttpURLConnection)url.openConnection();
                conn.setConnectTimeout(45000);
                conn.setReadTimeout(15000);
                conn.setDoOutput(true);
                conn.setDoInput(true);

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
                    //Toast.makeText(getContext(),s+" "+success,Toast.LENGTH_LONG).show();
                    JSONArray messagesArray=jsonObject.getJSONArray("categories");
                    categoryClassList=new ArrayList<>();
                    for(int i=0;i<messagesArray.length();i++){
                        count++;
                        JSONObject categoryItem=messagesArray.getJSONObject(i);

                        categoryClassList.add(new CategoryClass(categoryItem.getInt("id"),
                                categoryItem.getString("name"),
                                "http://derickoduor.000webhostapp.com/androidTest/images/"+categoryItem.getString("image")));
                    }
                    categoryAdapter=new CategoryAdapter1(categoryClassList,getApplicationContext());
                    rvMenu.setHasFixedSize(true);
                    LinearLayoutManager llm =new LinearLayoutManager(getApplicationContext());
                    rvMenu.setLayoutManager(llm);
                    rvMenu.setAdapter(categoryAdapter);
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

    public class CategoryAdapter1 extends RecyclerView.Adapter<CategoryAdapter1.MyViewHolder> {

        List<CategoryClass> categoryList;
        Context context;

        public CategoryAdapter1(List<CategoryClass> categoryList, Context context) {
            this.categoryList = categoryList;
            this.context = context;
        }
        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            ItemClickListener itemClickListener;
            TextView textMenuName;
            ImageView imageView;

            public MyViewHolder(View itemView) {
                super(itemView);
                textMenuName=(TextView)itemView.findViewById(R.id.menu_name);
                imageView=(ImageView)itemView.findViewById(R.id.menu_image);

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
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final CategoryClass categoryClass=categoryList.get(position);
            holder.textMenuName.setText(categoryClass.getName());
            Picasso.with(context).load(categoryClass.getImageUrl()).
                    into(holder.imageView);
            //CategoryClass clickItem=
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    String s= ((Integer) categoryClass.getId()).toString();
                    //Toast.makeText(context,categoryClass.getName(),Toast.LENGTH_LONG).show();
                    Intent i=new Intent(Categories.this,FoodList.class);
                    i.putExtra("menu_id",s);
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }

    }
}
