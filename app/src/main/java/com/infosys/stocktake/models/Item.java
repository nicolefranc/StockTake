package com.infosys.stocktake.models;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Item {
    private String itemName;
    private String itemDescription;
    private Map<String, Integer> qtyStatus;
    private String loaneeID;
    private String clubID;
    private String itemPicture;

    /**
     ACCESSORs
     */
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public Map<String, Integer> getQtyStatus() {
        return qtyStatus;
    }

    public void setQtyStatus(Map<String, Integer> qtyStatus) {
        this.qtyStatus = qtyStatus;
    }

    public String getLoaneeID() {
        return loaneeID;
    }

    public void setLoaneeID(String loaneeID) {
        this.loaneeID = loaneeID;
    }

    public String getClubID() {
        return clubID;
    }

    public void setClubID(String clubID) {
        this.clubID = clubID;
    }

    public String getItemPicture() {
        return itemPicture;
    }

    public void setItemPicture(String itemPicture) {
        this.itemPicture = itemPicture;
    }

    //how to upload/download the images: create a StorageReference with the imagePath as the path
}
