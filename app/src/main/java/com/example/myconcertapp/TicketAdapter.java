package com.example.myconcertapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private List<Ticket> tickets;
    private Context context;

    public TicketAdapter(Context context, List<Ticket> tickets) {
        this.context = context;
        this.tickets = tickets;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = tickets.get(position);
        holder.tvTicketInfo.setText("🎫 " + ticket.getConcertName() + " - " + ticket.getConcertDate() + " - " + ticket.getConcertPrice());

        holder.itemView.setOnLongClickListener(v -> {
            FirebaseFirestore.getInstance()
                    .collection("tickets")
                    .document(ticket.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        tickets.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Visszatérítés elindult", Toast.LENGTH_SHORT).show();
                    });
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvTicketInfo;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTicketInfo = itemView.findViewById(R.id.tvTicketInfo);
        }
    }
}

