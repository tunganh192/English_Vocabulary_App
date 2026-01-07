plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.honda_english"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.honda_english"
        minSdk = 24
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.cardview:cardview:1.0.0")

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    // Gson
    //implementation ("com.google.code.gson:gson:2.10.1'")
    // OkHttp Logging Interceptor
    implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0")

    //LocalDate trong UserResponse cần thêm dependency Jackson để parse JSON:
    implementation ("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")

    //Sử dụng thư viện ThreeTenABP (backport của Java 8 Time API) để hỗ trợ LocalDate trên API level thấp hơn.
    //ThreeTenABP cung cấp các class như LocalDate, DateTimeFormatter tương thích với API level thấp.
    implementation ("com.jakewharton.threetenabp:threetenabp:1.3.0")

    implementation ("androidx.security:security-crypto:1.1.0-alpha03")

    implementation ("com.google.android.material:material:1.11.0")


}
