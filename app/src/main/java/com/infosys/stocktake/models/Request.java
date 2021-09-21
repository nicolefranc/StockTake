package com.infosys.stocktake.models;

import java.io.Serializable;

public class Request implements Serializable {
    private final String TAG = "REQUEST Operations";

    public static String REQUEST_COLLECTION = "requests";
    public static final String CLUB_ID = "clubID";
    public static final String REQUESTER_ID = "requesterId";
    public static final String STATUS = "status";



    private String uuid;
    private String clubId;
    private String requesterId;
    private RequestStatus status;
    public Request(){}
    public Request(String uuid, String clubId, String requesterId, RequestStatus status) {
        this.uuid = uuid;
        this.clubId = clubId;
        this.requesterId = requesterId;
        this.status = status;
    }
    /***
     ACCESSORS
     ***/


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}
