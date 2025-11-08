import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"
}

// Read the local.properties file
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.example.jobify"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.jobify"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Expose the API key from local.properties as a build configuration field
        val apiKey = localProperties.getProperty("GEMINI_API_KEY", "").replace("\"", "")
        buildConfigField("String", "GEMINI_API_KEY", "\"$apiKey\"")
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
    packagingOptions {
        exclude("META-INF/LICENSE")
        exclude("META-INF/NOTICE")
        exclude("META-INF/DEPENDENCIES")
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.filament.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Flexbox
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("androidx.cardview:cardview:1.0.0")

    // (Optional) Google GenAI SDK
    implementation("com.google.ai.client.generativeai:generativeai:0.7.0")

    // Kotlinx Serialization for JSON parsing
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // NEW: iText for PDF text extraction (replaces pdfbox)
    implementation("com.itextpdf:itextg:5.5.10")

    // OkHttp (used elsewhere if needed)
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // Coroutines for async calls
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // lifecycle scope
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
}
