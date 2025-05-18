package com.example.myconcertapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageConcertsActivity extends AppCompatActivity {

    private EditText etName, etDate, etInfo, etPrice;
    private Button btnAddConcert, btnBackToMain;
    private RecyclerView rvOwnConcerts;

    private FirebaseFirestore db;
    private String userId;
    private int currentRole = 1;
    private List<Concert> concertList = new ArrayList<>();
    private ConcertAdapter concertAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_concerts);

        etName = findViewById(R.id.etConcertName);
        etDate = findViewById(R.id.etConcertDate);
        etInfo = findViewById(R.id.etConcertInfo);
        etPrice = findViewById(R.id.etConcertPrice);
        btnAddConcert = findViewById(R.id.btnAddConcert);
        btnBackToMain = findViewById(R.id.btnBackToMain);
        rvOwnConcerts = findViewById(R.id.rvOwnConcerts);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        rvOwnConcerts.setLayoutManager(new LinearLayoutManager(this));
        concertAdapter = new ConcertAdapter(concertList, concert -> {
            Intent intent = new Intent(this, EditConcertActivity.class);
            intent.putExtra("concertId", concert.getId());
            startActivity(intent);
        });
        rvOwnConcerts.setAdapter(concertAdapter);

        btnAddConcert.setOnClickListener(v -> addConcert());
        btnBackToMain.setOnClickListener(v -> finish());

        // Szerepkör lekérdezése, majd koncertek betöltése
        db.collection("users").document(userId).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists() && snapshot.contains("role")) {
                        Long role = snapshot.getLong("role");
                        if (role != null) {
                            currentRole = role.intValue();
                        }
                    }
                    loadOwnConcerts();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOwnConcerts(); // Frissítés az Edit után
    }

    private void addConcert() {
        String name = etName.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String info = etInfo.getText().toString().trim();
        String price = etPrice.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(date) || TextUtils.isEmpty(info) || TextUtils.isEmpty(price)) {
            Toast.makeText(this, "Minden mezőt ki kell tölteni", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> concertMap = new HashMap<>();
        concertMap.put("name", name);
        concertMap.put("date", date);
        concertMap.put("info", info);
        concertMap.put("price", price);
        concertMap.put("createdBy", userId);

        db.collection("concerts")
                .add(concertMap)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Koncert hozzáadva", Toast.LENGTH_SHORT).show();
                    clearInputs();
                    loadOwnConcerts();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Hiba történt", Toast.LENGTH_SHORT).show());
    }

    private void loadOwnConcerts() {
        concertList.clear();

        if (currentRole == 3) {
            // Admin: minden koncertet lekér
            db.collection("concerts")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        for (DocumentSnapshot doc : querySnapshot) {
                            Concert concert = doc.toObject(Concert.class);
                            if (concert != null) {
                                concert.setId(doc.getId());
                                concertList.add(concert);
                            }
                        }
                        concertAdapter.notifyDataSetChanged();
                    });
        } else {
            // Rendező: csak saját koncertek
            db.collection("concerts")
                    .whereEqualTo("createdBy", userId)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        for (DocumentSnapshot doc : querySnapshot) {
                            Concert concert = doc.toObject(Concert.class);
                            if (concert != null) {
                                concert.setId(doc.getId());
                                concertList.add(concert);
                            }
                        }
                        concertAdapter.notifyDataSetChanged();
                    });
        }
    }

    private void clearInputs() {
        etName.setText("");
        etDate.setText("");
        etInfo.setText("");
        etPrice.setText("");
    }
}
