package com.infosys.stocktake.inventory.items;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.infosys.stocktake.R;

import java.util.ArrayList;

public class ClubRecyclerViewAdapter extends RecyclerView.Adapter<ClubRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mClubNames = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private Context mContext;

//    #TODO: Change this method to populate the arraylists from a single itemID
    public ClubRecyclerViewAdapter(ArrayList<String> clubNames, ArrayList<String> images, Context context){
        mClubNames = clubNames;
        mImages = images;
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
        Log.d(TAG, "onBindViewHolder: called");

//        #TODO: insert method to get images from database from itemID
//Temporarily putting in placeholders
        holder.itemImage.setImageResource(R.drawable.ic_launcher_foreground);
        holder.itemName.setText((mClubNames.get(position)));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                #TODO: create intent to bring it to the itemView
                Toast.makeText(mContext, "IDENTITY THEFT IS NOT A JOKE", Toast.LENGTH_SHORT);
                Log.d(TAG, "tapped");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mClubNames.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView itemImage;
        TextView itemName;
        TextView itemDescription;
        SquareCardView parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.club_image);
            itemName = itemView.findViewById(R.id.club_name);
            parentLayout = itemView.findViewById(R.id.club_view_parent);



        }
    }
}
