package com.happyniksingh.shoppinglist;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements ShpCartEventListener {

    RecyclerView recyclerViewe;
    Customrecyer_MainAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton btnadditems;
    ShopsCart shopsCart;
    List<ShopsCart>cartList;
    ShopCartDao shopCartDao;
    TextView textViewplaceholder;
    TextView mainactivitytextview_toolbar;
    private MainActionModeCallback actionModeCallback;
    private int chackedCount = 0;
    private AdView mAdView;
    int noofitems;
    InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this,
                "ca-app-pub-6871345560581067~3973174530");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        prepareinterstialad();


        ScheduledExecutorService scheduledExecutorService=Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mInterstitialAd.isLoaded()){
                            mInterstitialAd.show();
                        }
                        else {
                            Log.d("TAG","eRROR add is not loaded");
                        }
                        prepareinterstialad();
                    }
                });
            }
        },20,20, TimeUnit.SECONDS);

        btnadditems=findViewById(R.id.additems);
        textViewplaceholder=findViewById(R.id.tvplaceholder);
        recyclerViewe=findViewById(R.id.mainactivityrecyclerview);
        adapter=new Customrecyer_MainAdapter(this);
        layoutManager=new LinearLayoutManager(this);
        recyclerViewe.setLayoutManager(layoutManager);
        recyclerViewe.setAdapter(adapter);
        mainactivitytextview_toolbar=findViewById(R.id.mainactivitytextview_toolbar);
        AssetManager assetManager=this.getAssets();
        Typeface typeface=Typeface.createFromAsset(assetManager,"fonts/Arkhip_font.ttf");
        mainactivitytextview_toolbar.setTypeface(typeface);



        shopCartDao=ShopCartDB.getShopsCartinstance(this).shopCartDao();
        loadnotes();
        btnadditems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Enter item name");
                View view= LayoutInflater.from(v.getContext()).inflate(R.layout.builderitems,null,false);
                builder.setView(view);
                final EditText editText=view.findViewById(R.id.etitem);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name=editText.getText().toString();
                        shopsCart=new ShopsCart(name);
                        shopCartDao.insertitemsname(shopsCart);
                        loadnotes();
                    }
                });
                builder.show();
            }
        });
    }

    private void loadnotes()
    {

        this.cartList=new ArrayList<>();
        List<ShopsCart>list=shopCartDao.getallitems();
        cartList.addAll(list);
        if (cartList.size()>0) {
            recyclerViewe.setVisibility(View.VISIBLE);
            textViewplaceholder.setVisibility(View.GONE);
        }
        else {
            recyclerViewe.setVisibility(View.GONE);
            textViewplaceholder.setVisibility(View.VISIBLE);
        }
        adapter.setListener(this);
        adapter.setNotes(this.cartList);
        SwipetodeleteHelper.attachToRecyclerView(recyclerViewe);
    }

    private ItemTouchHelper SwipetodeleteHelper =new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
            0,ItemTouchHelper.RIGHT
    ) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if (cartList!=null) {
                ShopsCart shopsCart = cartList.get(viewHolder.getAdapterPosition());
                if (shopsCart != null) {
                    swipetodelete(shopsCart, viewHolder);
                }
            }
        }
    });

    private void swipetodelete(final ShopsCart shopsCart, final RecyclerView.ViewHolder viewHolder) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("Delete Items?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO: 28/09/2018 delete note
                        shopCartDao.deleteitembyid(shopsCart.getId());
                        cartList.remove(shopsCart);
                        adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        showEmptyView();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO: 28/09/2018  Undo swipe and restore swipedNote
                        recyclerViewe.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());


                    }
                })
                .setCancelable(false)
                .create().show();

    }

    private void showEmptyView() {
        if (cartList.size() == 0) {
            this.recyclerViewe.setVisibility(View.GONE);
            findViewById(R.id.tvplaceholder).setVisibility(View.VISIBLE);

        } else {
            this.recyclerViewe.setVisibility(View.VISIBLE);
            findViewById(R.id.tvplaceholder).setVisibility(View.GONE);
        }
    }


    @Override
    public void onNoteClick(ShopsCart note) {
        Intent intent=new Intent(MainActivity.this,ItemsActivity.class);
        intent.putExtra("idkey",note.getId());
        intent.putExtra("itemname",note.getNameOfItem());
        startActivity(intent);

    }


    @Override
    public void onNoteLongClick(ShopsCart note) {
        // TODO: 22/07/2018 note long clicked : delete , share ..
        note.setChecked(true);
        chackedCount = 1;
        adapter.setMultiCheckMode(true);

        // set new listener to adapter intend off MainActivity listener that we have implement
        adapter.setListener(new ShpCartEventListener() {
            @Override
            public void onNoteClick(ShopsCart note) {
                note.setChecked(!note.isChecked()); // inverse selected
                if (note.isChecked())
                    chackedCount++;
                else chackedCount--;

                if (chackedCount > 1) {
                    actionModeCallback.changeShareItemVisible(false);
                } else actionModeCallback.changeShareItemVisible(true);

                if (chackedCount == 0) {
                    //  finish multi select mode wen checked count =0
                    actionModeCallback.getAction().finish();
                }

                actionModeCallback.setCount(chackedCount + "/" + cartList.size());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNoteLongClick(ShopsCart note) {

            }

            @Override
            public void onoptionclik(ShopsCart shopsCar, TextView tvoption) {

            }
        });

        actionModeCallback = new MainActionModeCallback() {
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_delete_notes)
                    onDeleteMultiNotes();
                else if (menuItem.getItemId() == R.id.action_share_note)
                    onShareNote();

                actionMode.finish();
                return false;
            }

        };

        // start action mode
        startActionMode(actionModeCallback);
        // hide fab button
        btnadditems.setVisibility(View.GONE);
        actionModeCallback.setCount(chackedCount + "/" + cartList.size());
    }

    @Override
    public void onoptionclik(final ShopsCart shopsCart, TextView tvoption) {
        PopupMenu popupMenu=new PopupMenu(MainActivity.this
                ,tvoption,0);
        popupMenu.inflate(R.menu.popupmenu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.eteditpopupmenu:
                        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(MainActivity.this);
                        View view =LayoutInflater.from(MainActivity.this).inflate(R.layout.builderitems,null,false);
                        builder.setView(view);
                        final EditText editText=view.findViewById(R.id.etitem);
                        editText.setHint(shopsCart.getNameOfItem());
                        builder.setTitle("Want to Edit "+shopsCart.getNameOfItem() );
                        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String nameedit=editText.getText().toString();
                                ShopsCart shopsCart1=new ShopsCart(shopsCart.getId(),nameedit);
                                shopCartDao.Updateitemname(shopsCart1);
                                loadnotes();
                                adapter.notifyDataSetChanged();
                            }
                        });

                        builder.show();
                        break;
                    case R.id.etdelte:
                        AlertDialog.Builder builder1=new AlertDialog.Builder(MainActivity.this);
                        builder1.setTitle("Want to Delete it ?");
                        builder1.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                shopCartDao.deleteitembyid(shopsCart.getId());
                                adapter.notifyDataSetChanged();
                                loadnotes();

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


    private void onShareNote() {
        // TODO: 22/07/2018  we need share just one Note not multi

        ShopsCart note = adapter.getCheckedNotes().get(0);
        // TODO: 22/07/2018 do your logic here to share note ; on social or something else
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        String notetext = note.getNameOfItem() + "\n\n Create on : " +
                getString(R.string.app_name);
        share.putExtra(Intent.EXTRA_TEXT, notetext);
        startActivity(share);


    }

    private void onDeleteMultiNotes() {
        // TODO: 22/07/2018 delete multi notes

        List<ShopsCart> chackedNotes = adapter.getCheckedNotes();
        if (chackedNotes.size() != 0) {
            for (ShopsCart note : chackedNotes) {
                shopCartDao.deleteitems(note);
            }
            // refresh Notes
            loadnotes();
            Toast.makeText(this, chackedNotes.size() + " Item(s) Delete successfully !", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(this, "No Item(s) selected", Toast.LENGTH_SHORT).show();

        //adapter.setMultiCheckMode(false);
    }
    @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);

        adapter.setMultiCheckMode(false); // uncheck the notes
        adapter.setListener(this); // set back the old listener
        btnadditems.setVisibility(View.VISIBLE);
    }

    private void prepareinterstialad(){
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6871345560581067/4090339003");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }
}
