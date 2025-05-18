package com.example.myconcertapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private RecyclerView rvConcerts;
    private ConcertAdapter concertAdapter;
    private FirebaseFirestore db;
    private ArrayList<Concert> concertList = new ArrayList<>();
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvWelcome = findViewById(R.id.tvWelcome);
        rvConcerts = findViewById(R.id.rvConcerts);
        db = FirebaseFirestore.getInstance();

        // Fade-in animáció a root layout-ra
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        findViewById(R.id.mainRoot).startAnimation(fadeIn);

        // Felhasználó email kiírása
        String userEmail = "Ismeretlen felhasználó";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }
        tvWelcome.setText("Üdv, " + userEmail + "!");

        // RecyclerView setup
        rvConcerts.setLayoutManager(new LinearLayoutManager(this));

        // Koncertek lekérése Firestore-ból
        loadConcertsFromFirestore();

        // Kijelentkezés gomb
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadConcertsFromFirestore() {
        db.collection("concerts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    concertList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Concert concert = doc.toObject(Concert.class);
                        concertList.add(concert);
                    }
                    concertAdapter = new ConcertAdapter(concertList, concert -> {
                        ConcertDetailsActivity.start(MainActivity.this, concert);
                    });
                    rvConcerts.setAdapter(concertAdapter);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Hiba a koncertek lekérésekor: ", e);
                    Toast.makeText(this, "Nem sikerült betölteni a koncerteket", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Koncertek frissítése…");
        loadConcertsFromFirestore(); // újratölti a koncerteket
    }

}
