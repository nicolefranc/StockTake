package com.infosys.stocktake.models;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Item implements Serializable {
    // Constants
    private static final String ITEM_ID = "itemID";
    private static final String ITEM_NAME = "itemName";
    private static final String ITEM_DESC = "itemDescription";
    private static final String ITEM_PICTURE = "itemPicture";
    private static final String QTY_STATUS = "qtyStatus";
    private static final String LOANEE_ID = "loaneeID";
    private static final String CLUB_ID = "clubID";
    // Variables
    private String itemID;
    private String itemName;
    private String itemDescription;
    private String itemPicture;
    private String encodedQr;
    private Map<String, Integer> qtyStatus;
    private String loaneeID;
    private String clubID;

    // Constructor when inserting item
    // Accepts integer qty automatically indicating ALL AVAILABLE as its status
    public Item(String itemName, String itemDescription, String itemPicture, int qtyAvailable, String loaneeID, String clubID) {
        // ItemID: clubID-<8d-uuid>
        this.itemID = clubID.concat("-").concat(UUID.randomUUID().toString().substring(0, 8));
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemPicture = itemPicture;

        // Generate QR
        QrCode qr = new QrCode(200, 200, getItemID());
        Bitmap bitmap = qr.generateQrCode();
        this.encodedQr = qr.bitmapToString(bitmap);

        // Qty Status Map
        this.qtyStatus = new HashMap<String, Integer>();
        this.qtyStatus.put(ItemStatus.AVAILABLE.toString(), qtyAvailable);
        this.qtyStatus.put(ItemStatus.BROKEN.toString(), 0);
        this.qtyStatus.put(ItemStatus.ON_LOAN.toString(), 0);
        this.qtyStatus.put(ItemStatus.ON_REPAIR.toString(), 0);

        this.loaneeID = loaneeID;
        this.clubID = clubID;
    }

//    public Map<String, Object> insertItem() {
//        Map<String, Object> item = new HashMap<>();
//        item.put(ITEM_ID, UUID.randomUUID());
//        item.put(ITEM_NAME, this.itemName);
//        item.put(ITEM_DESC, this.itemDescription);
//        item.put(ITEM_PICTURE, this.itemPicture);
//        item.put(QTY_STATUS, this.qtyStatus);
//        item.put(LOANEE_ID, this.loaneeID);
//        item.put(CLUB_ID, this.clubID);
//
//        return item;
//    }

    public String getItemID() {
        return itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public String getItemPicture() {
        return itemPicture;
    }

    public Map<String, Integer> getQtyStatus() {
        return qtyStatus;
    }

    public String getLoaneeID() {
        return loaneeID;
    }

    public String getClubID() {
        return clubID;
    }

    public String getEncodedQr() {
        return encodedQr;
    }
}
