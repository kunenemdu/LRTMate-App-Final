package com.example.fypmetroapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

public class Computed_RecyclerAdapter extends RecyclerView.Adapter<Computed_RecyclerAdapter.MyViewHolder> {
    private final ArrayList<Route> routes;
    private final Context context;
    private final View view;

    public Computed_RecyclerAdapter(ArrayList<Route> routes, Context context, View view) {
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
    public Computed_RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_summary_item, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull Computed_RecyclerAdapter.MyViewHolder holder, int position) {
        Route route = routes.get(position);
        try {
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

            holder.distance.setText(route.getTotal_route_length() + " km");
            holder.full_text.setText(summary);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MapsFragmentExtras.setPrevious(routes.get(position));
                    MapsFragmentExtras.setRouteAdapter(route, context);
                }
            });
        }
        catch (Exception e) {
            Toast.makeText(context, "Error with Computed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }
}
