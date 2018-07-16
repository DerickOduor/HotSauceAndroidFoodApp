package com.derickoduor.hotsauce;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Derick Oduor on 3/29/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="HotSauceCart";
    private static final String ORDER_DETAILS="OrderDetails";
    private static final String ORDER_ID="id";
    private static final String PRODUCT_ID="product_id";
    private static final String PRODUCT_NAME="product_name";
    private static final String PRODUCT_PRICE="price";
    private static final String PRODUCT_DISCOUNT="discount";
    private static final String PRODUCT_QTY="qty";
    private static final String CUSTOMER_ID="customer_id";

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String Create_Table_OrderDetails="CREATE TABLE "+ORDER_DETAILS+"("+
                ORDER_ID+" INTEGER PRIMARY KEY,"+PRODUCT_ID+" INTEGER,"+PRODUCT_NAME+" TEXT,"+
                PRODUCT_PRICE+" DOUBLE,"+PRODUCT_DISCOUNT+" DOUBLE,"+PRODUCT_QTY+" INTEGER,"+
                CUSTOMER_ID+" TEXT"+")";
        db.execSQL(Create_Table_OrderDetails);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ORDER_DETAILS);

        onCreate(db);
    }

    public List<Order> getCart(int login_id){
        SQLiteDatabase db=this.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder=new SQLiteQueryBuilder();
        String[] sqlSelect={PRODUCT_ID,PRODUCT_QTY,PRODUCT_NAME,PRODUCT_PRICE,PRODUCT_DISCOUNT};

        queryBuilder.setTables(ORDER_DETAILS);
        Cursor c=queryBuilder.query(db,sqlSelect,null,null,null,null,null);

        final List<Order> result=new ArrayList<>();
        if(c.moveToFirst()){
            do{
                result.add(new Order(c.getInt(c.getColumnIndex(PRODUCT_ID)),
                        c.getInt(c.getColumnIndex(PRODUCT_QTY)),
                        c.getDouble(c.getColumnIndex(PRODUCT_PRICE)),
                        c.getDouble(c.getColumnIndex(PRODUCT_DISCOUNT)),
                        c.getString(c.getColumnIndex(PRODUCT_NAME))));
            }while (c.moveToNext());
        }

        return result;
    }

    public void addCart(Order order,int login_id){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(PRODUCT_ID,order.getId());
        values.put(PRODUCT_NAME,order.getName());
        values.put(PRODUCT_PRICE,order.getPrice());
        values.put(PRODUCT_DISCOUNT,order.getPrice());
        values.put(PRODUCT_QTY,order.getQty());
        values.put(CUSTOMER_ID,login_id);

        db.insert(ORDER_DETAILS,null,values);
        db.close();
    }

    public void cleanCart(){
        SQLiteDatabase db=this.getReadableDatabase();

        db.execSQL("DELETE FROM "+ORDER_DETAILS);

    }

}
