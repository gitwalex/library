import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    // Dokka for Documents in html
    alias(libs.plugins.dokka)
    id("maven-publish")
}
kotlin {
    compilerOptions {
        languageVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_3
        freeCompilerArgs =
            listOf(
                "-Xjavac-arguments='-Xlint:unchecked -Xlint:deprecation'",
                "-opt-in=kotlin.RequiresOptIn"
            )
        jvmTarget = JvmTarget.JVM_17
    }
}

android {
    namespace = "com.gerwalex.library"
    compileSdk = 36

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        // Compile für unchecked und deprecation
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
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
    implementation(libs.bundles.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.review.ktx)
    implementation(libs.androidx.work.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // GoogleSignIn
    implementation(libs.bundles.credentials)

    // Ktor
    implementation(libs.bundles.ktor)


}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "github.gitwalex.com"
            artifactId = "library"
            version = "0.0.1"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}