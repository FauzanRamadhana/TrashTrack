// Tambahkan configuration resolver untuk memastikan konsistensi versi Kotlin
configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            useVersion("1.9.22")
        }
    }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp") version "1.9.22-1.0.18"

}

android {
    namespace = "com.greentech.monitorsampah"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.greentech.monitorsampah"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"  // Versi yang kompatibel dengan Kotlin 1.9.22
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Kotlin stdlib dengan versi yang konsisten (1.9.22)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.stdlib.common)
    implementation(libs.kotlin.reflect)

    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // Firebase Realtime Database
    implementation(libs.firebase.database.ktx)

    // Firebase Analytics
    implementation(libs.firebase.analytics.ktx)

    // Dependensi lainnya
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.accompanist.permissions)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.play.services.location)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation("io.coil-kt:coil-compose:2.1.0")
    implementation("com.google.firebase:firebase-messaging:24.1.1")
    implementation(libs.androidx.work.runtime.ktx)
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation(libs.androidx.room.common.jvm)
    implementation(libs.androidx.room.runtime.android)
    implementation("androidx.compose.material3:material3:1.3.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation ("com.google.accompanist:accompanist-permissions:0.30.1")
    implementation ("com.google.android.gms:play-services-location:21.3.0")



    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}