package com.infosys.stocktake.inventory.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class ProfileFragment extends Fragment implements  OutgoingRequestAdapter.EventListener {
    final String TAG = "PROFILE FRAGMENT";
    FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db;
    private User currentUser;
    private TextView tvUsername, tvRole, tvTelegram;
    private Button btnToAdminRequests;
    public static ListView outgoingRequestLv;
    private StockTakeFirebase<Request> requestStockTakeFirebase;
    private StockTakeFirebase<User> userStockTakeFirebase;
    private ArrayList<OutgoingRequestDataModel> listOfRequests;
    private OutgoingRequestAdapter adapter;
    private String userId;
    SwipeRefreshLayout outgoingRequestsSwipeRefreshLayout;
    HashMap<String, Membership> userMemberships;
    ArrayList<String> listOfClubsUserIsAdmin;

    // TODO: DELETE BELOW BUTTON WHEN FINISH TESTING
    private Button testCreateRequestButton;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_profile_page, container, false);
        requestStockTakeFirebase = new StockTakeFirebase<Request>(Request.class, "requests");
        userStockTakeFirebase = new StockTakeFirebase<User>(User.class, "users");
        db = FirebaseFirestore.getInstance();
        userId = fbAuth.getCurrentUser().getUid();
        listOfClubsUserIsAdmin = new ArrayList<String>();
        listOfRequests = new ArrayList<OutgoingRequestDataModel>();
        outgoingRequestsSwipeRefreshLayout = view.findViewById(R.id.outgoingRequestsSwipeRefresh);
        outgoingRequestsSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                loadOutgoingRequests();
            }
        });

        // 1. Populate fields with profile data
        tvUsername = view.findViewById(R.id.profileUsername);
        tvRole = view.findViewById(R.id.profileRole);
        tvTelegram = view.findViewById(R.id.profileTelegram);
        outgoingRequestLv = view.findViewById(R.id.outgoingRequestsListView);
        btnToAdminRequests = view.findViewById(R.id.btnToAdminRequests);
        btnToAdminRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext() , AdminRequests.class);
                startActivity(intent);
            }
        });

        currentUser = (User) getArguments().getSerializable("user");
        tvUsername.setText(currentUser.getFullName().toString());
        tvRole.setText(currentUser.isSuperAdmin() ? "Super Admin" : "Normal User");
        tvTelegram.setText(currentUser.getTelegramHandle());

        loadOutgoingRequests();

        return view;
    }



    private void loadOutgoingRequests() {
        if (listOfRequests != null) {
            listOfRequests.clear();
        }

        // 1. Check what are the clubs that the current user is in
        Task<User> userTask = userStockTakeFirebase.query(userId);
        userTask.addOnSuccessListener(new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                userMemberships = user.getClubMembership();
                Log.d(TAG , "Size of user memberships: " + userMemberships.size());
                if (userMemberships.size() != 0) {
                    for (Map.Entry<String, Membership> entry : userMemberships.entrySet()) {
                        listOfRequests.add(new OutgoingRequestDataModel(entry.getKey(), entry.getValue()));
                    }
                    Log.d(TAG, "Size of listOfRequests: " + listOfRequests.size());


                    if (listOfRequests != null) {
                        // Initial adapter creation
                        if (adapter == null) {
                            Log.d(TAG, "Initial Adapter Creation");
                            adapter = new OutgoingRequestAdapter(listOfRequests, getActivity().getApplicationContext(), ProfileFragment.this);
                            outgoingRequestLv.setAdapter(adapter);
                        } else {
                            adapter.refresh(listOfRequests);
                            outgoingRequestLv.setAdapter(adapter);

                        }

                    }
                }
                outgoingRequestsSwipeRefreshLayout.setRefreshing(false);


            }
        });

    }

    @Override
    public void refreshData() {
        Log.d(TAG ,"Calling refresh data on listener");
        loadOutgoingRequests();
    }

//    private void loadRequests() {
//        if (listOfRequests != null) {
//            listOfRequests.clear();
//
//        }
//
////        Task<ArrayList<Request>> requestTask = requestStockTakeFirebase.compoundQuery("status", RequestStatus.PENDING);
////        Task<Request> requestTask2 = requestStockTakeFirebase.query("d0NJMGkWHKjUgctWMGIw");
//
//        // Get the list of clubs where the current user is an admin of
////        CollectionReference userCollectionRef = db.collection("users");
////        userCollectionRef.get()
//
//        Task<User> userTask = userStockTakeFirebase.query(userId);
//        userTask.addOnSuccessListener(new OnSuccessListener<User>() {
//            @Override
//            public void onSuccess(User user) {
//                userMemberships = user.getClubMembership();
//                if (userMemberships.size() != 0) {
//                    for (Map.Entry<String, Membership> entry : userMemberships.entrySet()) {
//                        Object value = entry.getValue();
//                        if (value.equals(Membership.ADMIN)) {
//                            listOfClubsUserIsAdmin.add(entry.getKey());
//                        }
//                        Log.d(TAG, "Size of listOfClubsUserIsAdmin: " + listOfClubsUserIsAdmin.size());
//
//
//                    }
//                    // Only return the requests where the clubId is in the list of clubs
//                    CollectionReference reqCollectionRef = db.collection("requests");
//                    Query requestQuery;
//                    if (listOfClubsUserIsAdmin.size() > 0) {
//                        requestQuery = reqCollectionRef.whereIn("clubId", listOfClubsUserIsAdmin).whereEqualTo("status", RequestStatus.PENDING.toString());
//
//                        requestQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                                                    @Override
//                                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                                                        if (!queryDocumentSnapshots.isEmpty()) {
//                                                                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
//                                                                                listOfRequests.add(snapshot.toObject(Request.class));
//
//                                                                        }
//                                                                        // -- DEBUGGING --
////
//                                                                        if (listOfRequests != null) {
//                                                                            Log.d(TAG, "Number of Pending Requests: " + listOfRequests.size());
//                                                                            for (int i = 0; i < listOfRequests.size(); i++) {
//                                                                                // TODO: UPDATE THIS METHOD
//                                                                                Log.d(TAG, "Retrieved: " + listOfRequests.get(i).getClubId());
//                                                                            }
//                                                                        }
////
////                                // -- END DEBUGGING --
//
//                                                                        if (listOfRequests != null) {
//                                                                            // Initial adapter creation
//                                                                            if (adapter == null) {
//                                                                                Log.d(TAG, "Initial Adapter Creation");
//                                                                                adapter = new IncomingRequestAdapter(listOfRequests, getActivity().getApplicationContext());
//                                                                                outgoingRequestLv.setAdapter(adapter);
//                                                                            } else {
//                                                                                Log.d(TAG, "Refreshing Adapter");
//                                                                                adapter.refresh(listOfRequests);
//                                                                            }
//
//                                                                        }
//
////                                                                        profileSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
////                                                                            @Override
////                                                                            public void onRefresh() {
////                                                                                profileSwipeRefreshLayout.setRefreshing(false);
////                                                                                loadRequests();
////                                                                            }
////                                                                        });
//                                                                    }
//                                                                }
//
////                            }
//                        );
//                    }
//
//
//                }
//            }
//        });
//
//
//    }
}
