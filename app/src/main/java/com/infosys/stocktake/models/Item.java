package com.infosys.stocktake.models;

import java.util.HashMap;
import java.util.LinkedList;

public class Item {
    private String itemName;
    private String itemDescription;
    private LinkedList<HashMap<Status,Integer>> qtyStatus;
    private String loaneeID;
    private String clubID;

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

    public LinkedList<HashMap<Status, Integer>> getQtyStatus() {
        return qtyStatus;
    }

    public void setQtyStatus(LinkedList<HashMap<Status, Integer>> qtyStatus) {
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    //how to upload/download the images: create a StorageReference with the imagePath as the path
    private String imagePath;
}
