// El arnés de escritorio de los muebles: un proyecto aparte, fuera del build de la app (no lo incluye el settings de la raíz).
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}
rootProject.name = "arnes_muebles"
