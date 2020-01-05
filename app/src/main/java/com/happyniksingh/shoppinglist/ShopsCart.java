package com.happyniksingh.shoppinglist;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "shopcart")
public class ShopsCart
{
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "nametext")
    private String NameOfItem;

    @Ignore // we don't want to store this value on database so ignore it
    private boolean checked = false;

    public ShopsCart(String nameOfItem) {
        NameOfItem = nameOfItem;
    }

    public ShopsCart(int id, String nameOfItem) {
        this.id = id;
        NameOfItem = nameOfItem;
    }

    public ShopsCart() {
    }


    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameOfItem() {
        return NameOfItem;
    }

    public void setNameOfItem(String nameOfItem) {
        NameOfItem = nameOfItem;
    }

}
