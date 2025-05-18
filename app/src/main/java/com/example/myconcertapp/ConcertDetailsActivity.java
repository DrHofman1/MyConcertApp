package com.example.myconcertapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ConcertDetailsActivity extends AppCompatActivity {

    private static final String EXTRA_NAME = "extra_concert_name";
    private static final String EXTRA_DATE = "extra_concert_date";
    private static final String EXTRA_INFO = "extra_concert_info";
    private static final String EXTRA_PRICE = "extra_concert_price";

    private TextView tvConcertName, tvConcertInfo;
    private Button btnBuyTicket;

    public static void start(Context context, Concert concert) {
        Intent intent = new Intent(context, ConcertDetailsActivity.class);
        intent.putExtra(EXTRA_NAME, concert.getName());
        intent.putExtra(EXTRA_DATE, concert.getDate());
        intent.putExtra(EXTRA_INFO, concert.getInfo());
        // adjuk hozzá:
        intent.putExtra(EXTRA_PRICE, concert.getPrice());
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concert_details);

        tvConcertName = findViewById(R.id.tvConcertName);
        tvConcertInfo = findViewById(R.id.tvConcertInfo);
        btnBuyTicket = findViewById(R.id.btnBuyTicket);

        String name = getIntent().getStringExtra(EXTRA_NAME);
        String date = getIntent().getStringExtra(EXTRA_DATE);
        String info = getIntent().getStringExtra(EXTRA_INFO);
        String price = getIntent().getStringExtra(EXTRA_PRICE);

        tvConcertName.setText(name);
        tvConcertInfo.setText(info + "\nDátum: " + date);

        btnBuyTicket.setOnClickListener(v -> {
            Animation scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale_up);
            btnBuyTicket.startAnimation(scaleAnim);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Map<String, Object> ticket = new HashMap<>();
            ticket.put("userId", userId);
            ticket.put("concertName", name);
            ticket.put("concertDate", date);
            ticket.put("concertPrice", price);

            db.collection("tickets")
                    .add(ticket)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Jegyvásárlás sikeres!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Hiba történt a jegyvásárláskor.", Toast.LENGTH_SHORT).show();
                    });
        });


        TextView tvConcertPrice = findViewById(R.id.tvConcertPrice);
        tvConcertPrice.setText("Ár: " + price);

        Button btnBackToList = findViewById(R.id.btnBackToList);
        btnBackToList.setOnClickListener(v -> {
            // Egyszerűen visszalépünk az előző képernyőre
            finish();
        });

    }

}

