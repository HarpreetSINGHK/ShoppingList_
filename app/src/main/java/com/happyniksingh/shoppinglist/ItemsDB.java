package com.happyniksingh.shoppinglist;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = ItemsEntity.class,version = 1,exportSchema = false)
public abstract class ItemsDB extends RoomDatabase {
    public abstract ItemsDao itemsDao();

    private static ItemsDB shopsCartinstance;
    public static final String DATABSE_NAME = "itemsdb";


    public static ItemsDB getShopsCartinstance(Context context) {
        if (shopsCartinstance==null){
            shopsCartinstance= Room.databaseBuilder(context,ItemsDB.class
                    ,DATABSE_NAME)
                    .allowMainThreadQueries()
                    .build();

        }

        return shopsCartinstance;

    }
}

