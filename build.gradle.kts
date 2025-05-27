import com.android.build.gradle.internal.scope.ProjectInfo.Companion.getBaseName

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.3.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.25" apply false
    id("com.android.library") version "8.3.2" apply false
    id("org.jetbrains.kotlin.jvm") version "1.9.25" apply false
    id("com.google.dagger.hilt.android") version "2.56.2" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
}