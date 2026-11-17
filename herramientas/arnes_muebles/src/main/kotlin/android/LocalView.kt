// Ver Android.kt: la vista de Android que Compose da en el teléfono; en escritorio no existe.
@file:Suppress("unused", "PackageDirectoryMismatch")
package androidx.compose.ui.platform

import androidx.compose.runtime.staticCompositionLocalOf

val LocalView = staticCompositionLocalOf { android.view.View() }
