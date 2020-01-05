package com.happyniksingh.shoppinglist;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Query;

@Entity(tableName = "items")
public class ItemsEntity {

    private int parentid;

    @PrimaryKey(autoGenerate = true)
    private int itemid;

    private String items;

    public ItemsEntity(int parentid, String items) {
        this.parentid = parentid;
        this.items = items;
    }

    @Ignore
    public ItemsEntity(int parentid, int itemid, String items) {
        this.parentid = parentid;
        this.itemid = itemid;
        this.items = items;
    }

    public int getParentid() {
        return parentid;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    public int getItemid() {
        return itemid;
    }

    public void setItemid(int itemid) {
        this.itemid = itemid;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }
}
