package com.example.myconcertapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditConcertActivity extends AppCompatActivity {

    private EditText etName, etDate, etInfo, etPrice;
    private Button btnSave, btnDelete, btnBack;
    private FirebaseFirestore db;
    private String concertId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_concert);

        etName = findViewById(R.id.etConcertName);
        etDate = findViewById(R.id.etConcertDate);
        etInfo = findViewById(R.id.etConcertInfo);
        etPrice = findViewById(R.id.etConcertPrice);
        btnSave = findViewById(R.id.btnSaveChanges);
        btnDelete = findViewById(R.id.btnDeleteConcert);
        btnBack = findViewById(R.id.btnBack);

        db = FirebaseFirestore.getInstance();
        concertId = getIntent().getStringExtra("concertId");

        loadConcertData();

        btnSave.setOnClickListener(v -> updateConcert());
        btnDelete.setOnClickListener(v -> deleteConcert());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadConcertData() {
        db.collection("concerts").document(concertId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        etName.setText(doc.getString("name"));
                        etDate.setText(doc.getString("date"));
                        etInfo.setText(doc.getString("info"));
                        etPrice.setText(doc.getString("price"));
                    }
                });
    }

    private void updateConcert() {
        String name = etName.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String info = etInfo.getText().toString().trim();
        String price = etPrice.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(date) || TextUtils.isEmpty(info) || TextUtils.isEmpty(price)) {
            Toast.makeText(this, "Minden mezőt ki kell tölteni", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("concerts").document(concertId)
                .update("name", name, "date", date, "info", info, "price", price)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Mentés sikeres", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void deleteConcert() {
        db.collection("concerts").document(concertId)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Koncert törölve", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}

