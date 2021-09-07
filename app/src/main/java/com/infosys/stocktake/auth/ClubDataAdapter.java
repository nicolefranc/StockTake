package com.infosys.stocktake.auth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.infosys.stocktake.Profile;
import com.infosys.stocktake.R;

import java.util.ArrayList;
import java.util.Collections;

public class ClubDataAdapter extends ArrayAdapter<ClubDataModel> implements View.OnClickListener{

    private ArrayList<ClubDataModel> dataSet;
    Context mContext;

    @Override
    public void onClick(View v) {
        Toast.makeText(mContext, "clicked", Toast.LENGTH_SHORT).show();
    }

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtType;
    }

    public ClubDataAdapter(ArrayList<ClubDataModel> data, Context context) {
        super(context, R.layout.clubdata_row_item, data);
        this.dataSet = data;
        this.mContext=context;

    }



    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ClubDataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.clubdata_row_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.clubname);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.usertype);

            Button delete =(Button)convertView.findViewById(R.id.delete);
            delete.setTag(position);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer index = (Integer) v.getTag();
                    //items.remove(index.intValue());
                    ProfileSetupAddClubActivity.clubArrayList.add(dataSet.get(position).clubName);
                    Collections.sort(ProfileSetupAddClubActivity.clubArrayList);
                    ProfileSetupAddClubActivity.clubdataAdapter.notifyDataSetChanged();
                    dataSet.remove(position);
                    notifyDataSetChanged();
                }
            });

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }


        viewHolder.txtName.setText(dataModel.getClubName());
        viewHolder.txtType.setText(dataModel.getUserType());
        // Return the completed view to render on screen
        return convertView;
    }
}