package com.infosys.stocktake.inventory.profile;

import com.infosys.stocktake.models.Membership;

public class OutgoingRequestDataModel {
    String clubId;
    Membership userType;

    public OutgoingRequestDataModel(String clubId, Membership userType) {
        this.clubId = clubId;
        this.userType = userType;
    }

    public String getClubId() {return clubId;}

    public Membership getUserType() {return userType;}
}
