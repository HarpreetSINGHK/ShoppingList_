package com.happyniksingh.shoppinglist;

import android.content.DialogInterface;
import android.content.Entity;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ItemsActivity extends AppCompatActivity implements ItemsInterface {

    ItemsDao itemsDao;
    ArrayList<ItemsEntity> cartList;
    int idkey;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ItemsAdapter itemsAdapter;
    TextView tvitems;
    FloatingActionButton fab;
    public int noofitems;
    TextView itemtoolbartetxiew;
    String name;
    private TextView tvplaceholder;
    private AdView mAdView;
    ItemsEntity entity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        idkey=getIntent().getExtras().getInt("idkey");
        name=getIntent().getExtras().getString("itemname");
        MobileAds.initialize(this,
                "ca-app-pub-6871345560581067~3973174530");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        tvitems=findViewById(R.id.tvitems);
        tvplaceholder=findViewById(R.id.tvplaceholder);
        fab=findViewById(R.id.btnitemadd);
        recyclerView=findViewById(R.id.itemsrecyclerview);
        layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        itemsAdapter=new ItemsAdapter(this);
        itemtoolbartetxiew=findViewById(R.id.itemstoolbartv);
        Typeface typeface=Typeface.createFromAsset(this.getAssets(),"fonts/Arkhip_font.ttf");
        itemtoolbartetxiew.setTypeface(typeface);
        itemsDao=ItemsDB.getShopsCartinstance(this).itemsDao();
        itemtoolbartetxiew.setText(name);
        tvplaceholder.setText("Add "+ name + " Items");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ItemsActivity.this);
                builder.setTitle("Add items");
                View view= LayoutInflater.from(v.getContext()).inflate(R.layout.builderitems,null,false);
                final EditText editText=view.findViewById(R.id.etitem);
                builder.setView(view);
                builder.setPositiveButton("add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text=editText.getText().toString();
                        ItemsEntity entity=new ItemsEntity(idkey,text);
                        itemsDao.insert(entity);
                        loaditems();
                    }
                });

                builder.show();
            }
        });
        loaditems();


    }

    private void loaditems()
    {
        this.cartList=new ArrayList<>();
        List<ItemsEntity>list=itemsDao.returnallitems(idkey);
        cartList.addAll(list);
        itemsAdapter.setNotes(cartList);
        recyclerView.setAdapter(itemsAdapter);

        swipetodeletehelper.attachToRecyclerView(recyclerView);
        showEmptyView();
        itemsAdapter.setListener(this);
        if (cartList.size()>0){
            noofitems=cartList.size();
        }
    }

    private ItemTouchHelper swipetodeletehelper=new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if (cartList != null) {
                ItemsEntity itemsEntity = cartList.get(viewHolder.getAdapterPosition());
                if (itemsEntity!=null){
                    swipeteodelete(itemsEntity,viewHolder);
                }
            }
        }
    });

    private void swipeteodelete(ItemsEntity itemsEntity, RecyclerView.ViewHolder viewHolder) {
        itemsDao.deleteitembyid(itemsEntity.getItemid());
        cartList.remove(itemsEntity);
        itemsAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
        showEmptyView();
    }

    private void showEmptyView() {
        if (cartList.size() == 0) {
            this.recyclerView.setVisibility(View.GONE);
            findViewById(R.id.tvplaceholder).setVisibility(View.VISIBLE);

        } else {
            this.recyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.tvplaceholder).setVisibility(View.GONE);
        }
    }

    @Override
    public void onoptioncliked(final ItemsEntity itemsEntity, TextView tvitems) {
        PopupMenu popupMenu=new PopupMenu(ItemsActivity.this
                ,tvitems,0);
        popupMenu.inflate(R.menu.popupmenu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.eteditpopupmenu:
                        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(ItemsActivity.this);
                        View view =LayoutInflater.from(ItemsActivity.this).inflate(R.layout.builderitems,null,false);
                        builder.setView(view);
                        final EditText editText=view.findViewById(R.id.etitem);
                        editText.setHint(itemsEntity.getItems());
                        builder.setTitle("Want to Edit "+itemsEntity.getItems() );
                        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String nameedit=editText.getText().toString();
                                ItemsEntity shopsCart1=new ItemsEntity(itemsEntity.getParentid(),itemsEntity.getItemid(),nameedit);
                                itemsDao.update(shopsCart1);
                                loaditems();
                                itemsAdapter.notifyDataSetChanged();
                            }
                        });

                        builder.show();
                        break;
                    case R.id.etdelte:
                        AlertDialog.Builder builder1=new AlertDialog.Builder(ItemsActivity.this);
                        builder1.setTitle("Want to Delete it ?");
                        builder1.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                itemsDao.deleteitembyid(itemsEntity.getItemid());
                                itemsAdapter.notifyDataSetChanged();
                                loaditems();

                            }
                        });
                        builder1.show();
                        break;

                }
                return false;

            }

        });

        popupMenu.show();

    }
}
