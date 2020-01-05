package com.happyniksingh.shoppinglist;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ItemsDao {

    @Insert
    void insert(ItemsEntity itemsEntity);


    @Delete
    void delete(ItemsEntity itemsEntity);


    @Update
    void update(ItemsEntity itemsEntity);



    @Query("SELECT * FROM items WHERE parentid =:idkey")
    List<ItemsEntity>returnallitems(int idkey);


    @Query("SELECT * FROM ITEMS WHERE parentid =:idkeyparent")
        ItemsEntity getitemsbyid(int idkeyparent);
    @Query("DELETE  FROM items WHERE itemid =:itemid")
    void deleteitembyid(int itemid);
}
