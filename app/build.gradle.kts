plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("androidx.room")
    id("kotlin-parcelize")
}

android {
    namespace = "com.texttomp3.texttospeech"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.texttomp3.texttospeech"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildFeatures {
        viewBinding = true
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.firebase.crashlytics.buildtools)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // UI
    implementation(libs.android.flag.kit)
    implementation(libs.dotsindicator)
    implementation (libs.flexbox)
    implementation (libs.andratingbar)
    implementation (libs.cupertinoswitch)
    implementation(libs.coil)

    // Network
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // Viewmodel
    implementation(libs.koin.android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Ads
    implementation(libs.play.services.ads)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.user.messaging.platform)
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // Billing
    implementation(libs.billing.ktx)

    // Others
    implementation (libs.ffmpeg.kit.full)
    implementation(libs.sdp.android)

    implementation (libs.androidx.core.splashscreen)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(project(":edgettslib"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}