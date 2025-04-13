package com.example.myconcertapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private RecyclerView rvConcerts;
    private ConcertAdapter concertAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvWelcome = findViewById(R.id.tvWelcome);
        rvConcerts = findViewById(R.id.rvConcerts);

        // Fade-in animáció a root layout-ra
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        findViewById(R.id.mainRoot).startAnimation(fadeIn);

        // Felhasználó email
        String userEmail = "Ismeretlen felhasználó";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }
        tvWelcome.setText("Üdv, " + userEmail + "!");

        // Dummy adatok
        ArrayList<Concert> concertList = new ArrayList<>();
        concertList.add(new Concert("Rock Fesztivál", "2025.05.10.", "Igazi Rock-buli", "5000 Ft"));
        concertList.add(new Concert("Pop Show", "2025.06.15.", "Pop sztárok nagy színpadon", "6500 Ft"));
        concertList.add(new Concert("Jazz Est", "2025.07.01.", "Lazulós jazz session", "4000 Ft"));


        concertAdapter = new ConcertAdapter(concertList, concert -> {
            // Kattintásra ConcertDetailsActivity
            ConcertDetailsActivity.start(MainActivity.this, concert);
        });
        rvConcerts.setLayoutManager(new LinearLayoutManager(this));
        rvConcerts.setAdapter(concertAdapter);
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            // Firebase kijelentkezés
            FirebaseAuth.getInstance().signOut();
            // Vissza a LoginActivity-re
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
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
            // Indítsuk a ProfileActivity-t
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
