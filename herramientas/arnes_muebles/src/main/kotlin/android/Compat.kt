// Ver Android.kt.
@file:Suppress("unused", "PackageDirectoryMismatch")
package androidx.core.view

object WindowCompat {
    class Controlador {
        var isAppearanceLightStatusBars: Boolean = false
        var isAppearanceLightNavigationBars: Boolean = false
    }
    fun getInsetsController(window: android.view.Window, view: android.view.View) = Controlador()
}
