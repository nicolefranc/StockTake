package com.infosys.stocktake.inventory.clubs;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.infosys.stocktake.R;
import com.infosys.stocktake.inventory.dependencies.SquareCardView;
import com.infosys.stocktake.models.Club;

import java.util.ArrayList;

public class ClubRecyclerViewAdapter extends RecyclerView.Adapter<ClubRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

//    private ArrayList<String> mClubNames = new ArrayList<>();
    private ArrayList<Club> mClubs = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private Context mContext;

//    #TODO: Change this method to populate the arraylists from a single itemID
//    public ClubRecyclerViewAdapter(ArrayList<String> clubNames, ArrayList<String> images, Context context){
    public ClubRecyclerViewAdapter(ArrayList<Club> clubs, Context context){
//        mClubNames = clubNames;
//        mImages = images;
        mClubs = clubs;
        mContext = context;
        Log.d(TAG, "recycler adapter initiated");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.club_item,parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        #TODO: insert method to get images from database from itemID
        if(getItemCount() != 0) {
//Temporarily putting in placeholders
            holder.clubImage.setImageResource(R.drawable.ic_launcher_foreground);
            holder.clubName.setText((mClubs.get(position).getClubName()));
        }

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ViewClubActivity.class);
                intent.putExtra("ClubIntent", mClubs.get(position));
                mContext.startActivity(intent);
                Log.d(TAG, "tapped");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mClubs.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView clubImage;
        TextView clubName;
        SquareCardView parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clubImage = itemView.findViewById(R.id.club_image);
            clubName = itemView.findViewById(R.id.club_name);
            parentLayout = itemView.findViewById(R.id.club_view_parent);



        }
    }
}
