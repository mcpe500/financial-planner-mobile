plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose) // Corrected alias
    alias(libs.plugins.google.devtools.ksp) // Added KSP plugin
    alias(libs.plugins.google.dagger.hilt.android) // Added Hilt plugin
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
        // Define BASE_URL for BuildConfig
        buildConfigField("String", "BASE_URL", "\"YOUR_API_BASE_URL_HERE\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders["usesCleartextTraffic"] = "false"
        }
        debug {
            manifestPlaceholders["usesCleartextTraffic"] = "true"
            // Optionally override for debug builds if needed
            // buildConfigField("String", "BASE_URL", "\"http://debug.example.com/api/\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders["usesCleartextTraffic"] = "false"
            // Ensure BASE_URL is also defined for release or inherits from defaultConfig
            // buildConfigField("String", "BASE_URL", "\"https://release.example.com/api/\"")
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
        buildConfig = true // Enable BuildConfig generation
        viewBinding = true // Enable ViewBinding
    }
    // packagingOptions is deprecated, renamed to packaging
    packaging {
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

    // ConstraintLayout for XML layouts
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // Material Components for XML layouts
    implementation("com.google.android.material:material:1.11.0")
    
    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")

    // Navigation Compose
    implementation(libs.androidx.navigation.compose) // Will need definition in TOML

    // ViewModel Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose) // Will need definition in TOML
    implementation(libs.androidx.lifecycle.runtime.compose) // Will need definition in TOML

    // ConstraintLayout Compose
    implementation(libs.androidx.constraintlayout.compose) // Will need definition in TOML

    // Room
    implementation(libs.androidx.room.runtime) // Will need definition in TOML
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

    // Glide
    implementation(libs.bumptech.glide) // Will need definition in TOML (e.g., com.github.bumptech.glide:glide:4.16.0)
    ksp(libs.bumptech.glide.ksp) // Will need definition in TOML (e.g., com.github.bumptech.glide:ksp:4.16.0)

    // Testing
    testImplementation(libs.junit) // Already in TOML
    androidTestImplementation(libs.androidx.junit) // Already in TOML
    androidTestImplementation(libs.androidx.espresso.core) // Already in TOML
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Already in TOML
    androidTestImplementation(libs.androidx.ui.test.junit4) // Will need definition in TOML
    debugImplementation(libs.androidx.ui.tooling) // Will need definition in TOML
    debugImplementation(libs.androidx.ui.test.manifest) // Will need definition in TOML

    // Hilt
    implementation(libs.google.dagger.hilt.android) // Will need definition in TOML
    ksp(libs.google.dagger.hilt.compiler)          // Will need definition in TOML (use KSP)
    implementation(libs.androidx.hilt.navigation.compose) // Will need definition in TOML
}