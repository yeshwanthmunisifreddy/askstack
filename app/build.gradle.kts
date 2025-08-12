import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.ksp)

}

val keystorePropertiesFile = rootProject.file("local.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    namespace = "com.thesubgraph.askstack"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.thesubgraph.askstack"
        minSdk = 27
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        val openaiApiKey = keystoreProperties["OPENAI_API_KEY"] as String
        buildConfigField("String", "OPENAI_API_KEY", "\"$openaiApiKey\"")
        val assistantId = keystoreProperties["OPENAI_ASSISTANT_ID"] as String? ?: ""
        buildConfigField("String", "OPENAI_ASSISTANT_ID", "\"$assistantId\"")
        buildConfigField("boolean", "ENABLE_MOCK_STREAMING", "false")
        buildConfigField("boolean", "ENABLE_SMOOTH_TYPING", "true")
        buildConfigField("float", "TYPING_SPEED_MULTIPLIER", "4.0f")
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
        buildConfig = true
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
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)


    /**------------Hilt----------------------------- */
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    testImplementation(libs.junit)
    ksp(libs.hilt.android.compiler)

    /**------------Retrofit----------------------------- */
    implementation(libs.bundles.retrofit.network)

    /**------------Coroutines----------------------------- */
    implementation(libs.bundles.coroutines)

    implementation(libs.jetbrains.kotlinx.datetime)

    /**------------Chat Assistant----------------------------- */
    implementation(libs.bundles.chat.assistant)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.kotlinx.coroutines.test)
}