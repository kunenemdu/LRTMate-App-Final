package com.example.fypmetroapp;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Route_RecyclerAdapter extends RecyclerView.Adapter<Route_RecyclerAdapter.MyViewHolder> {
    private final ArrayList<String> instructions;
    private final Context context;
    private final Route route;

    public Route_RecyclerAdapter(ArrayList<String> instructions, Route route, Context context) {
        this.instructions = instructions;
        this.context = context;
        this.route = route;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView route_step_text;
        private Dialog ride;
        private ImageView more;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            route_step_text = itemView.findViewById(R.id.route_step_text);
            ride = new Dialog(context);
            more = itemView.findViewById(R.id.more_info_ride);
        }
    }

    @NonNull
    @Override
    public Route_RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_step_layout, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull Route_RecyclerAdapter.MyViewHolder holder, int position) {
        String instruction = instructions.get(position);
        holder.route_step_text.setText(instruction);
        if (instruction.contains("Ride")) {
            if (route.getRidingList() != null) {
                holder.ride = Maps_Full_Access.ride_stops_dialog;
                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MapsFragmentExtras.show_ride_stops_buses(context, route.getRidingList(), instruction, route.origin);
                    }
                };
                holder.route_step_text
                        .setOnClickListener(onClickListener);
                holder.more.setVisibility(View.VISIBLE);
                holder.more.setOnClickListener(onClickListener);
            }
        }
        else
            holder.more.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return instructions.size();
    }
}