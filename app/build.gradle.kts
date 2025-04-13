plugins {
    // Ha nem adtad meg a verziót a settings.gradle.kts pluginManagement blokkban,
    // akkor itt írd: id("com.android.application") version "8.1.0"
    // Ha pluginManagement kezeli, akkor verzió nélkül:
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.myconcertapp"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.myconcertapp"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }
}

// Kényszerítsük a Kotlin 1.8.10 verziót, ha valami library régebbit húzna be
configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.10")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.10")
    }
}

dependencies {
    // Alap AndroidX + Material
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.0")

    // Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:32.2.0"))
    // Példa: Firebase Authentication
    implementation("com.google.firebase:firebase-auth")
    // Példa: Firestore
    implementation("com.google.firebase:firebase-firestore")

    // Multidex
    implementation("androidx.multidex:multidex:2.0.1")
}


