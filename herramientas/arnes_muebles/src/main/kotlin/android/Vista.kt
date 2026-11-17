// Ver Android.kt.
@file:Suppress("unused", "PackageDirectoryMismatch")
package android.view

class Window

open class View {
    /** En el arnés todo cuenta como «vista previa»: así Tema.kt no intenta tocar la barra de estado. */
    val isInEditMode: Boolean = true
    val context: android.content.Context = android.content.Context()
}
