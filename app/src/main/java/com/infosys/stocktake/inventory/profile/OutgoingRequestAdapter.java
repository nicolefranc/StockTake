package com.infosys.stocktake.inventory.profile;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.infosys.stocktake.R;
import com.infosys.stocktake.auth.ClubDataModel;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.inventory.clubs.ViewClubActivity;
import com.infosys.stocktake.models.Club;
import com.infosys.stocktake.models.Membership;
import com.infosys.stocktake.models.Request;
import com.infosys.stocktake.models.RequestStatus;
import com.infosys.stocktake.models.User;

import org.w3c.dom.Text;

import java.lang.reflect.Member;
import java.util.ArrayList;

public class OutgoingRequestAdapter extends ArrayAdapter<OutgoingRequestDataModel> {
    private final String  TAG = "OutgoingRequestAdapter";
    private ArrayList<OutgoingRequestDataModel> dataSet;
    Context mContext;
    private  String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StockTakeFirebase<Request> requestStockTakeFirebase;
    EventListener listener;

    // View lookup cache
        private class ViewHolder {
                TextView clubName;
                TextView userType;
        }
    public interface EventListener {
        void refreshData();
    }

        public OutgoingRequestAdapter(ArrayList<OutgoingRequestDataModel> data, Context context, EventListener listener) {
                super(context, R.layout.request_outgoing_admin_item, data);
                this.dataSet = data;
                this.mContext = context;
                this.listener = listener;
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            // TODO: OUT OF BOUNDS EXCEPTION HERE
                OutgoingRequestDataModel dataModel = getItem(position);
                Membership userType = dataModel.getUserType();
                String clubId = dataModel.getClubId();



                ViewHolder viewHolder;

                if (convertView == null) {
                        viewHolder = new ViewHolder();
                        LayoutInflater inflater = LayoutInflater.from(getContext());
                        convertView = inflater.inflate(R.layout.request_outgoing_admin_item, parent, false);
                        viewHolder.clubName = (TextView) convertView.findViewById(R.id.outgoingRequestClubName);
                        viewHolder.userType = (TextView)  convertView.findViewById(R.id.outgoingRequestUserType);

                        Button promoteDemoteButton = (Button) convertView.findViewById(R.id.btnOutgoingAdminRequest);
                        promoteDemoteButton.setTag(position);
                        processButtonProperties(promoteDemoteButton, userType, clubId);



                        convertView.setTag(viewHolder);

                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                viewHolder.userType.setText(userType.toString());
               setClubNameFromId(clubId, viewHolder);

            return convertView;
        }


        public void processButtonProperties(Button promoteDemoteButton, Membership userType, String clubId) {
            // 1. Is the current user a Member or Admin?
            if (userType.equals(Membership.MEMBER)) {
                // 1a. Has the user already sent a request before?
                checkPendingRequests(clubId, new PendingRequestCallback() {
                    @Override
                    public void hasPendingRequests(boolean exists) {
                        if (exists) {
                            promoteDemoteButton.setEnabled(false);
                            promoteDemoteButton.setTextColor(mContext.getResources().getColor(R.color.gray));
                            promoteDemoteButton.setText("Request Pending");

                        } else {
                            // 1b. If no existing requests, keep default button visuals
                            promoteDemoteButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Create a pending admin request to the specified club
                                    // Request needs: 1) clubId 2) requesterId 3) Request ID
                                    Toast.makeText(mContext, "Sending Admin Request...", Toast.LENGTH_SHORT).show();
                                    sendAdminRequest(clubId);
                                    listener.refreshData();
                                }
                            });
                        }
                    }
                });


            } else if (userType.equals(Membership.ADMIN)) {
                promoteDemoteButton.setTextColor(mContext.getResources().getColor(R.color.red));
                promoteDemoteButton.setText("Demote Myself");
                promoteDemoteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        demoteUser(clubId);
                    }
                });


            }
        }

    private void demoteUser(String clubId) {
        StockTakeFirebase requestTask = new StockTakeFirebase<Request>(Request.class, "requests");
        db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                user.setClubMembership(clubId , Membership.MEMBER);
                StockTakeFirebase userStockTakeFirebase = new StockTakeFirebase<User>(User.class, "users");
                Task<Void> userTask = userStockTakeFirebase.update(user, userId);
                Toast.makeText(mContext, "User set to member", Toast.LENGTH_SHORT).show();
                listener.refreshData();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Error demoting user", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void sendAdminRequest(String clubId) {
        requestStockTakeFirebase = new StockTakeFirebase<Request>(Request.class, "requests");

        requestStockTakeFirebase.getCollection().addOnSuccessListener(new OnSuccessListener<ArrayList<Request>>() {
            @Override
            public void onSuccess(ArrayList<Request> requests) {
                int count;
                if (requests != null) {
                    count = requests.size();
                } else {
                    count = 0;
                }
                String reqId = "REQ-" + count;

                Request newRequest = new Request(reqId, clubId, userId, RequestStatus.PENDING);
                Log.d(TAG, "Sending admin request...");

                db.collection("requests").document(reqId).set(newRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Admin Request DocumentSnapshot successfully written! ");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Admin Request Error ", e);
                    }
                });



            }
        });
    }

    public void setClubNameFromId(String clubId,  ViewHolder viewHolder) {
        StockTakeFirebase clubStockTakeFirebase = new StockTakeFirebase<Club>(Club.class, "clubs");
        Task<Club> clubTask = clubStockTakeFirebase.query(clubId);
        clubTask.addOnSuccessListener(new OnSuccessListener<Club>() {
            @Override
            public void onSuccess(Club club) {
                Log.d(TAG, "Got Club Name: " + club.getClubName());
                viewHolder.clubName.setText(club.getClubName());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "setClubNameFromId: ERROR", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void refresh(ArrayList<OutgoingRequestDataModel> newRequestData ) {
            Log.d(TAG, "Refreshing Adapter");

//            this.dataSet.clear();
            this.dataSet = newRequestData;
            Log.d(TAG, "Length of new dataset: " + newRequestData.size());

            notifyDataSetChanged();

    }






    private void checkPendingRequests(String clubId, PendingRequestCallback pendingRequestCallback ) {
        Log.d(TAG, "Checking pending requests");
        // 1. Check the requests collection for a document which has the current user ID, current club and status is set to PENDING
        db.collection("requests").whereEqualTo("requesterId" , userId)
                .whereEqualTo("clubId", clubId)
                .whereEqualTo("status", RequestStatus.PENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                Log.d(TAG, "User does NOT have pending requests");
                                pendingRequestCallback.hasPendingRequests(false);
                            }
                            else {
                                Log.d(TAG, "User has pending requests");

                                pendingRequestCallback.hasPendingRequests(true);
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });

    }

    interface PendingRequestCallback {
        void hasPendingRequests(boolean exists);
    }
}


