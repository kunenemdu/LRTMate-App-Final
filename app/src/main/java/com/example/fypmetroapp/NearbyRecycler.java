package com.example.fypmetroapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NearbyRecycler extends RecyclerView.Adapter<NearbyRecycler.MyViewHolder> {
    private final ArrayList<Station> stations;
    private final Context context;

    public NearbyRecycler(ArrayList<Station> stations, Context context) {
        this.stations = stations;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView station_name, station_distance;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            station_name = itemView.findViewById(R.id.name_station);
            station_distance = itemView.findViewById(R.id.distance_station);
        }
    }

    @NonNull
    @Override
    public NearbyRecycler.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.nearby_bus_item, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyRecycler.MyViewHolder holder, int position) {
        Station station = stations.get(position);
        holder.station_name.setText(station.name);
        holder.station_distance.setText(station.distance);
        holder.station_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }
}