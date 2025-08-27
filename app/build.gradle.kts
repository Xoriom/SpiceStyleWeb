plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt") // correct kapt plugin id
}

android {
    namespace = "com.example.spicestyle"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.spicestyle"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // Enables Retrofit/OkHttp logging, etc., if you want
        }
    }

    // You’re using XML layouts; keep compose off unless you need it.
    buildFeatures {
        viewBinding = true
        // compose = true  // <- turn on only if you’re using Compose screens
    }

    // If you later enable Compose, also add:
    // composeOptions {
    //     kotlinCompilerExtensionVersion = "1.5.14"
    // }
    // kotlinOptions { jvmTarget = "17" }
}

dependencies {
    // --- AndroidX base UI ---
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.browser:browser:1.8.9") // Custom Tabs

    // --- Lifecycle / coroutines ---
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    // --- Networking (Retrofit + OkHttp) ---
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.google.code.gson:gson:2.11.0")

    // --- Image loading (pick ONE, both are fine if you use both XML & Compose) ---
    // Glide for XML:
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    // Coil for Compose:
    // implementation("io.coil-kt:coil-compose:2.6.0")

    // --- (Optional) Jetpack Compose — enable only if you’re using Compose screens ---
    // implementation(platform("androidx.compose:compose-bom:2024.09.02"))
    // implementation("androidx.compose.ui:ui")
    // implementation("androidx.compose.material3:material3")
    // implementation("androidx.compose.ui:ui-tooling-preview")
    // debugImplementation("androidx.compose.ui:ui-tooling")
    // implementation("androidx.activity:activity-compose:1.9.2")
}
