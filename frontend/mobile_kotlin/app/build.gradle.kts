import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose.compiler)
    id("kotlin-parcelize")
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
        vectorDrawables {
            useSupportLibrary = true
        }

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    packagingOptions {
        exclude("META-INF/LICENSE")
        exclude("META-INF/NOTICE")
        exclude("META-INF/DEPENDENCIES")
    }
    buildFeatures {
        buildConfig = true
    }
    buildFeatures {
        viewBinding = true
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

    // Jetpack Compose
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")

    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Swipe to Refresh
    implementation("com.google.accompanist:accompanist-swiperefresh:0.34.0")

    // For JSON parsing
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // For Instant serialization
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

    // Google GenAI SDK
    implementation("com.google.ai.client.generativeai:generativeai:0.7.0")

    // iText for PDF text extraction
    implementation("com.itextpdf:itextg:5.5.10")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // Coroutines for async calls
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Lifecycle scope
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    // Unit Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    // Retrofit for API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")

    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    // PDF extraction
    implementation("org.apache.pdfbox:pdfbox:2.0.27")
// Alternative pour iText (si vous préférez)
    implementation("com.itextpdf:itext7-core:7.2.5")
}
// Cache-busting comment to force a full project sync
