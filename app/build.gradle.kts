plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    // Capturas de pantalla en la computadora (sin teléfono) para revisar el diseño de cada tema.
    id("app.cash.paparazzi")
}

android {
    namespace = "io.github.ruddy2003.vestigium"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.github.ruddy2003.vestigium"
        minSdk = 30
        targetSdk = 35
        versionCode = (System.getenv("GITHUB_RUN_NUMBER") ?: "1").toInt()
        versionName = "1.0." + (System.getenv("GITHUB_RUN_NUMBER") ?: "0")
    }

    // Llave fija: permite instalar cada versión nueva encima de la anterior sin perder datos.
    signingConfigs {
        create("vestigium") {
            storeFile = file("vestigium.jks")
            storePassword = "vestigium-mei-2026"
            keyAlias = "vestigium"
            keyPassword = "vestigium-mei-2026"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("vestigium")
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("vestigium")
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
    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }
    packaging {
        resources.excludes += setOf("META-INF/AL2.0", "META-INF/LGPL2.1", "META-INF/*.kotlin_module")
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.biometric:biometric:1.1.0")
    implementation("androidx.work:work-runtime-ktx:2.10.0")
    implementation("androidx.documentfile:documentfile:1.0.1")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")

    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("androidx.health.connect:connect-client:1.1.0-alpha10")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("org.osmdroid:osmdroid-android:6.1.20")

    testImplementation("junit:junit:4.13.2")
}

// En CI, cada prueba deja en el registro cuándo empieza: si la máquina se apaga a mitad (sin informe de JUnit),
// la última línea «STARTED» dice qué prueba fue.
tasks.withType<Test>().configureEach {
    // Las capturas muy altas (6000-8000 px) no caben en los 512 MB que Gradle da por omisión a las pruebas.
    maxHeapSize = "2g"
    testLogging { events("started", "failed") }
}
