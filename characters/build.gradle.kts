plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.davidups.characters"
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
            enableAndroidTestCoverage = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    flavorDimensions += "environment"
    productFlavors {
        create("preproduction") {
            dimension = "environment"
        }
        create("production") {
            dimension = "environment"
        }
    }
}

dependencies {

    implementation(project(":core"))

    //Hilt
    implementation("com.google.dagger:hilt-android:2.56.2")
    kapt("com.google.dagger:hilt-android-compiler:2.56.2")

    //Room
    implementation("androidx.room:room-runtime:2.7.1")
    annotationProcessor("androidx.room:room-compiler:2.7.1")
    kapt("androidx.room:room-compiler:2.7.1")

    testImplementation("junit:junit:4.13.2") // No change
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    testImplementation("org.mockito:mockito-core:5.18.0")
    // org.mockito:mockito-inline is removed
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
}

kapt {
    correctErrorTypes = true
}