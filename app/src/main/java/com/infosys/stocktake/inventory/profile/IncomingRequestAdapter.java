package com.infosys.stocktake.inventory.profile;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.infosys.stocktake.R;
import com.infosys.stocktake.firebase.StockTakeFirebase;
import com.infosys.stocktake.models.Club;
import com.infosys.stocktake.models.Membership;
import com.infosys.stocktake.models.Request;
import com.infosys.stocktake.models.RequestStatus;
import com.infosys.stocktake.models.User;

import java.util.ArrayList;

public class IncomingRequestAdapter extends ArrayAdapter<Request> {
    private final String TAG = "IncomingRequestAdapter";
    private ArrayList<Request> requestdata;
    Context mContext;
    private  String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    EventListener listener;
//    @Override
//    public void onClick(View view) {
//        Toast.makeText(mContext, "clicked", Toast.LENGTH_SHORT).show();
//
//    }

    private static class ViewHolder {
        TextView requestName;
        TextView requestClub;
    }

    public interface EventListener {
        void refreshData();
    }
    public IncomingRequestAdapter(ArrayList<Request> data, Context context, IncomingRequestAdapter.EventListener listener) {
        super(context, R.layout.request_incoming_admin_item, data);
        this.requestdata = data;
        this.mContext = context;
        this.listener = listener;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Request request = getItem(position);
        String clubId = request.getClubId();

        ViewHolder viewHolder;
//        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.request_incoming_admin_item, parent, false);
            viewHolder.requestName = (TextView) convertView.findViewById(R.id.requestName);
            viewHolder.requestClub = (TextView) convertView.findViewById(R.id.requestClub);
            ImageButton acceptButton = (ImageButton) convertView.findViewById(R.id.requestAcceptButton);
            ImageButton rejectButton = (ImageButton) convertView.findViewById(R.id.requestRejectButton);

            acceptButton.setTag(request);
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Toast.makeText(mContext, "ACCEPT BUTTON: " + position + "!", Toast.LENGTH_SHORT).show();



                    requestdata.remove(position);
                    notifyDataSetChanged();
                    StockTakeFirebase requestTask = new StockTakeFirebase<Request>(Request.class, "requests");


                    db.collection("users").document(request.getRequesterId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Log.d(TAG, "User's ID: " + request.getRequesterId());
                            User user = documentSnapshot.toObject(User.class);
                            user.setClubMembership(clubId , Membership.ADMIN);
                            StockTakeFirebase userStockTakeFirebase = new StockTakeFirebase<User>(User.class, "users");
                            Task<Void> userTask = userStockTakeFirebase.update(user, request.getRequesterId());
                            Toast.makeText(mContext, "User set to admin", Toast.LENGTH_SHORT).show();
                            request.setStatus(RequestStatus.CLOSED);
                            requestTask.update(request, request.getUuid());
                            listener.refreshData();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext, "Error retrieving credentials", Toast.LENGTH_SHORT).show();

                        }
                    });




                }
            });

            rejectButton.setTag(request);
            rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, "REJECT BUTTON: " + position, Toast.LENGTH_SHORT).show();

                    requestdata.remove(position);
                    notifyDataSetChanged();

                    StockTakeFirebase requestTask = new StockTakeFirebase<Request>(Request.class, "requests");
                    request.setStatus(RequestStatus.CLOSED);


                    requestTask.update(request, request.getUuid() );
                    listener.refreshData();
//                    ProfileFragment.requestLv.invalidateViews();
                }
            });


//            result = convertView;
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
//            result = convertView;
        }
        setClubNameFromId(clubId, viewHolder);
        setUsernameFromId(request.getRequesterId(), viewHolder);



        return convertView;
    }


    public void setClubNameFromId(String clubId, ViewHolder viewHolder) {
       StockTakeFirebase clubStockTakeFirebase = new StockTakeFirebase<Club>(Club.class, "clubs");
       Task<Club> clubTask = clubStockTakeFirebase.query(clubId);
       clubTask.addOnSuccessListener(new OnSuccessListener<Club>() {
           @Override
           public void onSuccess(Club club) {
               viewHolder.requestClub.setText(club.getClubName());
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Toast.makeText(mContext, "setClubNameFromId: ERROR", Toast.LENGTH_SHORT).show();
           }
       });
    }

    public void setUsernameFromId(String userId, ViewHolder viewHolder) {
        StockTakeFirebase userStockTakeFirebase = new StockTakeFirebase<User>(User.class, "users");
        Task<User> userTask = userStockTakeFirebase.query(userId);
        userTask.addOnSuccessListener(new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                viewHolder.requestName.setText(user.getFullName());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "setUsernameFromId: ERROR", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void refresh(ArrayList<Request> newRequestData) {
//        this.requestdata.clear();
        this.requestdata = newRequestData;
        notifyDataSetChanged();
    }

    public void addRequest(Request newRequest) {
        this.requestdata.add(newRequest);
        notifyDataSetChanged();
    }

}
