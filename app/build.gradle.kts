plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.ayaan.abhaya"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ayaan.abhaya"
        minSdk = 28
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.espresso.core)
    implementation(libs.firebase.messaging)
    implementation(libs.play.services.location)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.firebase.storage.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation (libs.hilt.android)
    ksp(libs.google.hilt.compiler)
    //Canary Leaks (for checking memory leaks)
    debugImplementation ("com.squareup.leakcanary:leakcanary-android:2.14")
    // For instrumentation tests
    androidTestImplementation  (libs.dagger.hilt.android.testing)
    kspAndroidTest(libs.google.hilt.compiler)

    // For local unit tests
    testImplementation (libs.dagger.hilt.android.testing)
    kspTest(libs.google.hilt.compiler)

    //GMAPS
    implementation(libs.play.services.maps)
    implementation(libs.maps.compose) // Jetpack Compose support
    //For XML App
    implementation("io.coil-kt:coil:2.7.0")
    //For Compose App
    implementation("io.coil-kt:coil-compose:2.7.0")
    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("androidx.glance:glance-appwidget:1.1.1")
    implementation ("androidx.glance:glance-material3:1.1.1")
}