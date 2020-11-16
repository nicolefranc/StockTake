package com.infosys.stocktake.models;

import java.util.Map;

public class Item {
    private String itemID;
    private String itemName;
    private String itemDescription;
    private Map<ItemStatus, Integer> qtyStatus;
    private String loaneeID;
    private String clubID;
    private String itemPicture;

    public Item(String itemID, String itemName, String itemDescription, Map<ItemStatus, Integer> qtyStatus, String loaneeID, String clubID, String itemPicture) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.qtyStatus = qtyStatus;
        this.loaneeID = loaneeID;
        this.clubID = clubID;
        this.itemPicture = itemPicture;
    }
}
