package com.infosys.stocktake.inventory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.infosys.stocktake.R;
import com.infosys.stocktake.inventory.items.ClubRecyclerViewAdapter;
import com.infosys.stocktake.inventory.items.ItemRecyclerViewAdapter;

import java.util.ArrayList;

//import info.androidhive.viewpager2.R;

public class ClubFragment extends Fragment {

    private TextView textView;
    private ArrayList<String> mItemNames = new ArrayList<>();
    private ArrayList<String> mItemDescriptions= new ArrayList<>();
    private ArrayList<String> mImages= new ArrayList<>();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.inventory_list,container,false);
    String message = getArguments().getString("message");
    return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        populateItems();
    }

    private void populateItems(){
        mImages.add("SDF");
        mItemNames.add("Vertex");

        mImages.add("SDF");
        mItemNames.add("is");

        mImages.add("SDF");
        mItemNames.add("noise");

        mImages.add("SDF");
        mItemNames.add("pollution");

        mImages.add("SDF");
        mItemNames.add("Vertex");

        mImages.add("SDF");
        mItemNames.add("is");

        mImages.add("SDF");
        mItemNames.add("noise");

        mImages.add("SDF");
        mItemNames.add("pollution");

        mImages.add("SDF");
        mItemNames.add("Vertex");

        mImages.add("SDF");
        mItemNames.add("is");

        mImages.add("SDF");
        mItemNames.add("noise");

        mImages.add("SDF");
        mItemNames.add("pollution");

        initRecyclerView();
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
        ClubRecyclerViewAdapter recyclerAdapter = new ClubRecyclerViewAdapter(mItemNames,mImages, getActivity());
        recyclerView.setAdapter(recyclerAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
    }
}