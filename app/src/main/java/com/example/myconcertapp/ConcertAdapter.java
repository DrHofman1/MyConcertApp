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
    private final OnConcertClickListener clickListener;

    public ConcertAdapter(List<Concert> concertList, OnConcertClickListener clickListener) {
        this.concertList = concertList;
        this.clickListener = clickListener;
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
        holder.bind(concertList.get(position));
    }

    @Override
    public int getItemCount() {
        return concertList.size();
    }

    class ConcertViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvDate;
        private final TextView tvPrice; // új mező

        public ConcertViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvConcertItemName);
            tvDate = itemView.findViewById(R.id.tvConcertItemDate);
            tvPrice = itemView.findViewById(R.id.tvConcertItemPrice); // init
        }

        public void bind(Concert concert) {
            tvName.setText(concert.getName());
            tvDate.setText("Dátum: " + concert.getDate());
            tvPrice.setText("Ár: " + concert.getPrice());

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onConcertClick(concert);
                }
            });
        }
    }

}
