plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.mastercoding.thriftly"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mastercoding.thriftly"
        minSdk = 27
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation ("androidx.appcompat:appcompat:1.3.1")
    implementation("com.squareup.picasso:picasso:2.71828") {
        exclude(group = "com.android.support")
        exclude(module = "exifinterface")
        exclude(module = "support-annotations")
    }
    implementation ("com.google.android.gms:play-services-base:17.6.0")

    implementation(libs.glide)
    implementation(libs.media3.common)            // Thêm Glide
    annotationProcessor(libs.glide.compiler) // Thêm Glide Compiler
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

