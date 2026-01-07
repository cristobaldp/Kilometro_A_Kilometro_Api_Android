plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.ejemplo.kilometro_a_kilometro"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ejemplo.kilometro_a_kilometro"
        minSdk = 26
        targetSdk = 34
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
}

dependencies {

    // ANDROID CORE
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // UI
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // ACTIVITY
    implementation(libs.androidx.activity)
    implementation(libs.volley)


    // TESTING
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


}
