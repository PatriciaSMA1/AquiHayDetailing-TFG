plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}


android {
    namespace = "com.example.aquihaydetailing"
    compileSdk = 36


    defaultConfig {
        applicationId = "com.example.aquihaydetailing"
        minSdk = 24
        targetSdk = 36
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
        isCoreLibraryDesugaringEnabled = true
    }
}


dependencies {
    // ✅ Firebase BOM para mantener versiones sincronizadas
    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))


    // ✅ Firebase módulos
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage") // 🔥 añadido para subir imágenes


    // ✅ Material Components
    implementation("com.google.android.material:material:1.10.0")
    //implementation("com.prolificinteractive:material-calendarview:1.4.3")


    // ✅ Desugaring para APIs modernas
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")


    // ✅ ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.0.0")


    // ✅ Glide para cargar imágenes desde Firebase Storage
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")


    // ✅ Material (otra versión, puedes dejar solo la más reciente)
    implementation("com.google.android.material:material:1.9.0")


    // ✅ Librerías AndroidX
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)


    // ✅ Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}






