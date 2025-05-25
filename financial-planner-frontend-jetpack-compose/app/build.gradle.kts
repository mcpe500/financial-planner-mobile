plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose) // Corrected alias
    alias(libs.plugins.kapt)             // Corrected alias
}

android {
    namespace = "com.example.financialplannerapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.financialplannerapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui) // Will need definition in TOML
    implementation(libs.androidx.ui.graphics) // Will need definition in TOML
    implementation(libs.androidx.ui.tooling.preview) // Will need definition in TOML
    implementation(libs.androidx.material3) // Will need definition in TOML
    implementation(libs.androidx.compose.material.icons.core) // Will need definition in TOML
    implementation(libs.androidx.compose.material.icons.extended) // Will need definition in TOML

    // Navigation Compose
    implementation(libs.androidx.navigation.compose) // Will need definition in TOML

    // ViewModel Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose) // Will need definition in TOML
    implementation(libs.androidx.lifecycle.runtime.compose) // Will need definition in TOML

    // ConstraintLayout Compose
    implementation(libs.androidx.constraintlayout.compose) // Will need definition in TOML

    // Room
    implementation(libs.androidx.room.runtime) // Will need definition in TOML
    kapt(libs.androidx.room.compiler) // Will need definition in TOML
    implementation(libs.androidx.room.ktx) // Will need definition in TOML

    // Retrofit & Gson
    implementation(libs.squareup.retrofit) // Will need definition in TOML
    implementation(libs.squareup.retrofit.converter.gson) // Will need definition in TOML
    implementation(libs.squareup.okhttp3.logging.interceptor) // Will need definition in TOML

    // Coroutines
    implementation(libs.jetbrains.kotlinx.coroutines.android) // Will need definition in TOML
    implementation(libs.jetbrains.kotlinx.coroutines.core) // Will need definition in TOML

    // Google Sign-In
    implementation(libs.google.android.gms.play.services.auth) // Will need definition in TOML

    // UI ViewBinding (Check if needed, will need definition in TOML)
    implementation(libs.androidx.compose.ui.viewbinding) // Will need definition in TOML

    // Biometric
    implementation(libs.androidx.biometric.ktx) // Will need definition in TOML

    // Coil
    implementation(libs.coil.compose) // Will need definition in TOML

    // Testing
    testImplementation(libs.junit) // Already in TOML
    androidTestImplementation(libs.androidx.junit) // Already in TOML
    androidTestImplementation(libs.androidx.espresso.core) // Already in TOML
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Already in TOML
    androidTestImplementation(libs.androidx.ui.test.junit4) // Will need definition in TOML
    debugImplementation(libs.androidx.ui.tooling) // Will need definition in TOML
    debugImplementation(libs.androidx.ui.test.manifest) // Will need definition in TOML

    // Hilt (Commented)
    // implementation(libs.google.dagger.hilt.android)
    // kapt(libs.google.dagger.hilt.compiler)
    // implementation(libs.androidx.hilt.navigation.compose)
}