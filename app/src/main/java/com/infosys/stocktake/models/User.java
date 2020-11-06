package com.infosys.stocktake.models;

import com.google.firebase.auth.FirebaseUser;

public class User {
    private FirebaseUser currentUser;

    public User(FirebaseUser currentUser) {
        this.currentUser = currentUser;
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }
}
