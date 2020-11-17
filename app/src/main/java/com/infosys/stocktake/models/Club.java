package com.infosys.stocktake.models;

public class Club {
    private String clubID;
    private String clubName;
    //list of club admins' student IDs who have approval
    private String[] adminList;

    public String getClubID() {
        return clubID;
    }

    public void setClubID(String clubID) {
        this.clubID = clubID;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String[] getAdminList() {
        return adminList;
    }

    public void setAdminList(String[] adminList) {
        this.adminList = adminList;
    }
}
