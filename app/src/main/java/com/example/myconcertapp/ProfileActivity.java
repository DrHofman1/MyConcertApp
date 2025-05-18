package com.example.myconcertapp;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvEmail;
    private EditText etName;
    private Spinner spinnerRole;
    private Button btnSaveChanges, btnBackToMain, btnManageConcerts;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String userId;

    private RecyclerView rvTickets;
    private TicketAdapter adapter;
    private List<Ticket> ticketList = new ArrayList<>();

    private int currentRole = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        tvEmail = findViewById(R.id.tvEmail);
        etName = findViewById(R.id.etName);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnBackToMain = findViewById(R.id.btnBackToMain);
        btnManageConcerts = findViewById(R.id.btnManageConcerts);
        rvTickets = findViewById(R.id.rvTickets);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() != null) {
            FirebaseUser currentUser = auth.getCurrentUser();
            userId = currentUser.getUid();

            String email = currentUser.getEmail();
            tvEmail.setText("Email: " + email);

            ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(this,
                    R.array.roles_array, android.R.layout.simple_spinner_item);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerRole.setAdapter(adapterSpinner);

            loadUserData();
        }

        btnSaveChanges.setOnClickListener(v -> saveUserData());
        btnBackToMain.setOnClickListener(v -> finish());
        btnManageConcerts.setOnClickListener(v -> startActivity(new Intent(this, ManageConcertsActivity.class)));

        rvTickets.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TicketAdapter(this, ticketList);
        rvTickets.setAdapter(adapter);

        createNotificationChannel();
    }

    private void loadUserData() {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                Long role = documentSnapshot.getLong("role");

                etName.setText(name);
                if (role != null && role >= 1 && role <= 3) {
                    spinnerRole.setSelection(role.intValue() - 1);
                    currentRole = role.intValue();
                }

                btnManageConcerts.setVisibility((currentRole == 2 || currentRole == 3) ? Button.VISIBLE : Button.GONE);
            }
        });
    }

    private void saveUserData() {
        String newName = etName.getText().toString().trim();
        int selectedRoleIndex = spinnerRole.getSelectedItemPosition();
        int newRole = selectedRoleIndex + 1;

        if (TextUtils.isEmpty(newName)) {
            Toast.makeText(this, "A név nem lehet üres", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", newName);
        updates.put("role", newRole);

        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Mentve", Toast.LENGTH_SHORT).show();
                    loadUserData();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Nem sikerült menteni", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserTickets();
    }

    private void loadUserTickets() {
        db.collection("tickets")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ticketList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Ticket ticket = doc.toObject(Ticket.class);
                        if (ticket != null) {
                            ticket.setId(doc.getId());
                            ticketList.add(ticket);
                            scheduleAlarm(ticket.getConcertName());
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Nem sikerült betölteni a jegyeket.", Toast.LENGTH_SHORT).show());
    }

    private void scheduleAlarm(String concertName) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Kérlek engedélyezd a pontos ébresztéseket a Beállításokban!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                return;
            }
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("concertName", concertName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                concertName.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 10);

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            showNotification("Értesítés beállítva", "A koncert előtt értesítést fogsz kapni: " + concertName);
        }
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ConcertAppChannel";
            String description = "Csatorna koncert értesítésekhez";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("concert_channel", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "concert_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Értesítések engedélyezve", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Az értesítések le vannak tiltva", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
