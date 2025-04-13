package com.example.myconcertapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserData, tvPurchasedTickets;
    private Button btnBackToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Keresd meg az UI elemeket
        Button btnBackToMain = findViewById(R.id.btnBackToMain);
        TextView tvUserData = findViewById(R.id.tvUserData);
        TextView tvTicketsTitle = findViewById(R.id.tvTicketsTitle);
        TextView tvPurchasedTickets = findViewById(R.id.tvPurchasedTickets);

        // Vissza gomb
        btnBackToMain.setOnClickListener(v -> finish());

        // Email beállítása
        String email = "Ismeretlen felhasználó";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }
        tvUserData.setText("Email: " + email);

        // Dummy jegyek
        List<String> purchasedTickets = new ArrayList<>();
        purchasedTickets.add("Jegy #1: Rock Fesztivál");
        purchasedTickets.add("Jegy #2: Jazz Est");
        purchasedTickets.add("Jegy #3: Pop Show");

        // Összefűzzük egy stringbe
        StringBuilder sb = new StringBuilder();
        for (String ticket : purchasedTickets) {
            sb.append(ticket).append("\n");
        }

        // Beírjuk a TextView-ba
        tvPurchasedTickets.setText(sb.toString());
    }
}

