package com.example.myconcertapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ConcertAdapter extends RecyclerView.Adapter<ConcertAdapter.ConcertViewHolder> {

    public interface OnConcertClickListener {
        void onConcertClick(Concert concert);
    }

    private final List<Concert> concertList;
    private final OnConcertClickListener listener;

    public ConcertAdapter(List<Concert> concertList, OnConcertClickListener listener) {
        this.concertList = concertList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConcertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_concert, parent, false);
        return new ConcertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConcertViewHolder holder, int position) {
        Concert concert = concertList.get(position);
        holder.tvName.setText(concert.getName());
        holder.tvDate.setText("Dátum: " + concert.getDate());
        holder.tvPrice.setText("Ár: " + concert.getPrice());

        holder.itemView.setOnClickListener(v -> listener.onConcertClick(concert));
    }

    @Override
    public int getItemCount() {
        return concertList.size();
    }

    public static class ConcertViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDate, tvPrice;

        public ConcertViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvConcertName);
            tvDate = itemView.findViewById(R.id.tvConcertDate);
            tvPrice = itemView.findViewById(R.id.tvConcertPrice);
        }
    }
}
