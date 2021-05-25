package com.infosys.stocktake.auth;

public class ClubDataModel {
    String clubName;
    String userType;


    public ClubDataModel(String clubName, String userType) {
        this.clubName=clubName;
        this.userType=userType;

    }

    public String getClubName() {
        return clubName;
    }

    public String getUserType() {
        return userType;
    }

}
