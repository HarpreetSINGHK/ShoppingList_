package com.happyniksingh.shoppinglist;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


@Database(entities = ShopsCart.class,version = 1)
public abstract class ShopCartDB extends RoomDatabase {
    public abstract ShopCartDao shopCartDao();

    private static ShopCartDB shopsCartinstance;
    public static final String DATABSE_NAME = "shopcartdb";


    public static ShopCartDB getShopsCartinstance(Context context) {
        if (shopsCartinstance==null){
            shopsCartinstance= Room.databaseBuilder(context,ShopCartDB.class
            ,DATABSE_NAME)
                    .allowMainThreadQueries()
                    .build();

        }

        return shopsCartinstance;
    }
}
