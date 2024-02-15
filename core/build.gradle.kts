import java.util.Properties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

val apikeyPropertiesFile = file("../apikeys.properties")
val apikeyProperties = Properties()
apikeyProperties.load(apikeyPropertiesFile.inputStream())

android {
    namespace = "com.davidups.core"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        buildConfig = true
    }

    flavorDimensions += "environment"
    productFlavors {
        create("preproduction") {
            dimension = "environment"
            buildConfigField("String", "ENVIRONMENT", "\"PRE\"")
            buildConfigField("String", "DATA_BASE", "\"PRE-MARVEL\"")
            buildConfigField("String", "BASE_URL", "\"https://gateway.marvel.com/\"")
            buildConfigField("String", "PUBLIC_KEY", apikeyProperties["PUBLIC_KEY"].toString())
            buildConfigField("String", "PRIVATE_KEY", apikeyProperties["PRIVATE_KEY"].toString())
        }
        create("production") {
            dimension = "environment"
            buildConfigField("String", "ENVIRONMENT", "\"PRO\"")
            buildConfigField("String", "DATA_BASE", "\"PRO-MARVEL\"")
            buildConfigField("String", "BASE_URL", "\"https://gateway.marvel.com/\"")
            buildConfigField("String", "PUBLIC_KEY", apikeyProperties["PUBLIC_KEY"].toString())
            buildConfigField("String", "PRIVATE_KEY", apikeyProperties["PRIVATE_KEY"].toString())
        }
    }
}

dependencies {

    api("androidx.core:core-ktx:1.12.0")

    // Retrofit
    api("com.squareup.retrofit2:retrofit:2.9.0")
    api("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    //Hilt
    implementation("com.google.dagger:hilt-android:2.46")
    kapt("com.google.dagger:hilt-android-compiler:2.46")

    //Room
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
}

kapt {
    correctErrorTypes = true
}