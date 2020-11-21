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

import com.bumptech.glide.Glide;
import com.infosys.stocktake.R;
import com.infosys.stocktake.inventory.InventoryFragment;

import java.util.ArrayList;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mItemNames = new ArrayList<>();
    private ArrayList<String> mItemDescriptions = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private Context mContext;

//    #TODO: Change this method to populate the arraylists from a single itemID
    public ItemRecyclerViewAdapter(ArrayList<String> itemNames, ArrayList<String> itemDescriptions, ArrayList<String> images, Context context){
        mItemNames = itemNames;
        mItemDescriptions = itemDescriptions;
        mImages = images;
        mContext = context;
        Log.d(TAG, "recycler adapter initiated");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_item,parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
//        #TODO: insert method to get images from database from itemID
        String imageURL = mImages.get(position);
        Glide.with(mContext)
                .load(imageURL)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_background)
                .into(holder.itemImage);
//        holder.itemImage.setImageResource(R.drawable.ic_launcher_foreground);
        holder.itemDescription.setText(mItemDescriptions.get(position));
        holder.itemName.setText((mItemNames.get(position)));

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
        return mItemNames.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView itemImage;
        TextView itemName;
        TextView itemDescription;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemName = itemView.findViewById(R.id.item_name);
            itemDescription = itemView.findViewById(R.id.item_description);
            parentLayout = itemView.findViewById(R.id.item_parent_layout);



        }
    }
}
