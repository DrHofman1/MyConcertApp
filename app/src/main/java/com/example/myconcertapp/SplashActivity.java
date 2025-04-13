package com.example.myconcertapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // A stílus betöltése (Splash theme), a Manifestben is be van állítva
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Rövid késleltetés, pl. 2 mp
        // Ehhez használhatunk Handler, de API 30 óta deprecate. Inkább Runnable + postDelayed:
        findViewById(R.id.splashRoot).postDelayed(() -> {
            // Ellenőrizzük, hogy be van-e jelentkezve a user
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                // Nincs user -> Login
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            } else {
                // Van user -> Main
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
            finish();
        }, 2000);
    }
}

