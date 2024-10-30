plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.mastercoding.thriftly"
    compileSdk = 34
    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
    }

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

    implementation("com.squareup.picasso:picasso:2.71828") {
        exclude(group = "com.android.support")
        exclude(module = "exifinterface")
        exclude(module = "support-annotations")
    }

    implementation ("com.google.android.gms:play-services-auth:20.6.0")
    implementation("com.google.firebase:firebase-database:19.2.1")
    implementation("com.google.firebase:firebase-firestore:24.3.1")
    implementation("com.google.firebase:firebase-auth:21.0.8")
    implementation("com.google.firebase:firebase-storage:20.0.2")
    implementation(platform("com.google.firebase:firebase-bom:32.0.0"))

    implementation ("com.android.volley:volley:1.2.1")


    implementation("com.google.auth:google-auth-library-oauth2-http:1.3.0")
    implementation("com.google.api-client:google-api-client:1.32.1")
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")



    dependencies {
        implementation ("com.facebook.android:facebook-login:16.0.0")
    }

    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("androidx.appcompat:appcompat:1.3.1")
    implementation("com.squareup.picasso:picasso:2.71828") {
        exclude(group = "com.android.support")
        exclude(module = "exifinterface")
        exclude(module = "support-annotations")
    }
    implementation ("com.google.android.gms:play-services-base:17.6.0")
    implementation("com.firebaseui:firebase-ui-firestore:8.0.0")

    implementation(libs.glide)
    implementation(libs.media3.common)
    implementation(libs.firebase.messaging)
    // Thêm Glide
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


