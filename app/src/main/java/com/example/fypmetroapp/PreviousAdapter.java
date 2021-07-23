package com.example.fypmetroapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

public class PreviousAdapter extends RecyclerView.Adapter<PreviousAdapter.MyViewHolder> {
    private final ArrayList<Route> routes;
    private final Context context;
    private final View view;

    public PreviousAdapter(ArrayList<Route> routes, Context context, View view) {
        this.routes = routes;
        this.context = context;
        this.view = view;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView duration_text, full_text, distance;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            duration_text = itemView.findViewById(R.id.duration_text);
            full_text = itemView.findViewById(R.id.full_text);
            distance = itemView.findViewById(R.id.length_text);
        }
    }

    @NonNull
    @Override
    public PreviousAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.previous_trip_home, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviousAdapter.MyViewHolder holder, int position) {
        Route route = routes.get(position);
        ArrayList<String> summary_al = new ArrayList<>(route.getSummary());
        String summary = "";
        int total_duration = route.getTotal_route_duration();
        for (String ins: summary_al)
            summary += ins;

        int h = total_duration / 60; //since both are ints, you get an int
        int m = total_duration % 60;
        String duration = "";
        if (h != 0) {
            if (h > 1)
                duration += h + "h ";
            if (m != 0)
                duration += m + " mins";

            holder.duration_text.setText(duration);
        }
        else
            holder.duration_text.setText(total_duration + " mins");

//        if (route.getDestination_walking().routes[0].legs[0].distance != null)
//            if (route.getOrigin_walking().routes[0].legs[0].distance != null)
//                holder.distance.setText((int) ((route.getDestination_walking().routes[0].legs[0].distance.inMeters + route.getOrigin_walking().routes[0].legs[0].distance.inMeters)) / 1000);

        holder.full_text.setText(summary);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment_User.start_routing(context, route);
            }
        });
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }
}