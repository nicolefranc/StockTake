package com.infosys.stocktake.inventory.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.models.Membership;
import com.infosys.stocktake.models.Request;
import com.infosys.stocktake.models.RequestStatus;
import com.infosys.stocktake.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminRequests extends AppCompatActivity implements IncomingRequestAdapter.EventListener {
    final String TAG = "ADMIN REQUESTS";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static ListView incomingRequestLv;
    ArrayList<String> listOfClubsUserIsAdmin;
    private TextView placeholderEmptyRequests;
    private ArrayList<Request> listOfRequests;
    private StockTakeFirebase<Request> requestStockTakeFirebase;
    private StockTakeFirebase<User> userStockTakeFirebase;
    private IncomingRequestAdapter adapter;
    HashMap<String, Membership> userMemberships;
    private String userId;
    SwipeRefreshLayout incomingRequestsSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_requests);
        requestStockTakeFirebase = new StockTakeFirebase<Request>(Request.class, "requests");
        userStockTakeFirebase = new StockTakeFirebase<User>(User.class, "users");
        incomingRequestLv = findViewById(R.id.incomingRequestsListView);
        listOfClubsUserIsAdmin = new ArrayList<String>();
        listOfRequests = new ArrayList<Request>();
        placeholderEmptyRequests = findViewById(R.id.placeholderEmptyRequests);
        incomingRequestsSwipeRefreshLayout = findViewById(R.id.incomingRequestsSwipeRefreshLayout);
        incomingRequestsSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadIncomingRequests();
            }
        });
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        loadIncomingRequests();
    }

    @Override
    public void refreshData() {
        Log.d(TAG , "Listener Refresh Data");
        loadIncomingRequests();
    }


    private void loadIncomingRequests() {
        if (listOfRequests != null) {
            listOfRequests.clear();

        }
        if (listOfClubsUserIsAdmin != null) {
            listOfClubsUserIsAdmin.clear();
        }

//        Task<ArrayList<Request>> requestTask = requestStockTakeFirebase.compoundQuery("status", RequestStatus.PENDING);
//        Task<Request> requestTask2 = requestStockTakeFirebase.query("d0NJMGkWHKjUgctWMGIw");

        // Get the list of clubs where the current user is an admin of
//        CollectionReference userCollectionRef = db.collection("users");
//        userCollectionRef.get()

        Task<User> userTask = userStockTakeFirebase.query(userId);
        userTask.addOnSuccessListener(new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                userMemberships = user.getClubMembership();
                if (userMemberships.size() != 0) {
                    for (Map.Entry<String, Membership> entry : userMemberships.entrySet()) {
                        Object value = entry.getValue();
                        if (value.equals(Membership.ADMIN)) {
                            listOfClubsUserIsAdmin.add(entry.getKey());
                        }
                        Log.d(TAG, "Size of listOfClubsUserIsAdmin: " + listOfClubsUserIsAdmin.size());


                    }
                    // Only return the requests where the clubId is in the list of clubs
                    CollectionReference reqCollectionRef = db.collection("requests");
                    Query requestQuery;
                    if (listOfClubsUserIsAdmin.size() > 0) {
                        requestQuery = reqCollectionRef.whereIn("clubId", listOfClubsUserIsAdmin).whereEqualTo("status", RequestStatus.PENDING.toString());

                        requestQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                        if (!queryDocumentSnapshots.isEmpty()) {
                                                                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                                                                listOfRequests.add(snapshot.toObject(Request.class));

                                                                        }


                                                                        if (listOfRequests != null) {
                                                                            if (listOfRequests.size() == 0) {
                                                                                placeholderEmptyRequests.setVisibility(View.VISIBLE);

                                                                            } else {
                                                                                placeholderEmptyRequests.setVisibility(View.INVISIBLE);
                                                                                // Initial adapter creation
                                                                                if (adapter == null) {
                                                                                    Log.d(TAG, "Initial Adapter Creation");
                                                                                    adapter = new IncomingRequestAdapter(listOfRequests, getApplicationContext(), AdminRequests.this);
                                                                                } else {
                                                                                    Log.d(TAG, "Refreshing Adapter");
                                                                                    adapter.refresh(listOfRequests);

                                                                                }
                                                                                incomingRequestLv.setAdapter(adapter);
                                                                            }


                                                                        }




//
                                                                    }
                                                                }

//                            }
                        );
                    }
                    incomingRequestsSwipeRefreshLayout.setRefreshing(false);
                    placeholderEmptyRequests.setVisibility(View.VISIBLE);


                } else {
                    incomingRequestsSwipeRefreshLayout.setRefreshing(false);
                    placeholderEmptyRequests.setVisibility(View.VISIBLE);

                }

            }
        });


    }


}