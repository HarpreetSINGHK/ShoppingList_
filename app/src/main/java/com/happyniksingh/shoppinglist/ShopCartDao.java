package com.happyniksingh.shoppinglist;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ShopCartDao
{
    @Insert
    void insertitemsname(ShopsCart shopsCart);


    @Delete
    void  deleteitems(ShopsCart shopsCart);


    @Update
    void Updateitemname(ShopsCart shopsCart);


    @Query("SELECT * FROM shopcart")
    List<ShopsCart>getallitems();

    @Query("SELECT * FROM shopcart WHERE id =:itemid")
    ShopsCart getitemnamebyid(int itemid);

    @Query("DELETE  FROM shopcart WHERE id =:itemid")
    void deleteitembyid(int itemid);
}
