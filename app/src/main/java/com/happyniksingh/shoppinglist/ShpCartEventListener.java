package com.happyniksingh.shoppinglist;


import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by ixi.Dv on 22/07/2018.
 */
public interface ShpCartEventListener {
    /**
     * call wen note clicked.
     *
     * @param note: note item
     */
    void onNoteClick(ShopsCart note);

    /**
     * call wen long Click to note.
     *
     * @param note : item
     */
    void onNoteLongClick(ShopsCart note);

    void onoptionclik(ShopsCart shopsCar, TextView tvoption);

}