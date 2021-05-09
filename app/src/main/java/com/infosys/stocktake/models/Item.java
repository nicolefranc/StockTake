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
    public static final String ITEM_COLLECTION = "items";
    public static final String ITEM_ID = "itemID";
    public static final String ITEM_NAME = "itemName";
    public static final String ITEM_DESC = "itemDescription";
    public static final String ITEM_PICTURE = "itemPicture";
    public static final String QTY_STATUS = "qtyStatus";
    public static final String ENCODED_QR = "encodedQr";
    public static final String LOANEE_ID = "loaneeID";
    public static final String CLUB_ID = "clubID";
    public static final String PUBLIC_STATUS = "isPublic";
    // Variables
    private String itemID;
    private String itemName;
    private String itemDescription;
    private String itemPicture;
    private String encodedQr;
    private Boolean isPublic;
    // Change to ENUM
    private Map<String, Integer> qtyStatus;
    private String loaneeID;
    private String clubID;

    public Item() {}

    // Constructor when retrieving item from Firebase
    public Item(Map<String, Object> item) {
        this.itemID = (String) item.get(ITEM_ID);
        this.itemName = (String) item.get(ITEM_NAME);
        this.itemDescription = (String) item.get(ITEM_DESC);
        this.itemPicture = (String) item.get(ITEM_PICTURE);
        this.encodedQr = (String) item.get(ENCODED_QR);
        this.qtyStatus = (Map<String, Integer>) item.get(QTY_STATUS);
        this.loaneeID = (String) item.get(LOANEE_ID);
        this.clubID = (String) item.get(CLUB_ID);
        this.isPublic = (Boolean) item.get(PUBLIC_STATUS);
    }

    // Constructor when inserting item
    // Accepts integer qty automatically indicating ALL AVAILABLE as its status
    public Item(String itemName, String itemDescription, String itemPicture, int qtyAvailable, String loaneeID, String clubID, Boolean isPublic) {
        // ItemID: clubID-<8d-uuid>
        this.itemID = clubID.concat("-").concat(UUID.randomUUID().toString().substring(0, 8));
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemPicture = itemPicture;
        this.isPublic = isPublic;

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

    public Boolean getIsPublic() { return isPublic;}

    public Map<String, Integer> getQtyStatus() {
        return qtyStatus;
    }

    public void setQtyStatus(String status, int quantity) {this.qtyStatus.put(status,quantity);}

    public void setIsPublic(Boolean isPublic) {this.isPublic = isPublic;}

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
