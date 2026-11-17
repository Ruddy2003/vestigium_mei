// Sustitutos mínimos de clases de Android que Tema.kt nombra (el color de la barra de estado y el modo oscuro del teléfono).
// En el arnés no hay ventana ni teléfono: no hacen nada. Solo existen para que Tema.kt compile entero.
@file:Suppress("unused", "PackageDirectoryMismatch")
package android.content.res

class Configuration {
    var uiMode: Int = UI_MODE_NIGHT_NO
    companion object {
        const val UI_MODE_NIGHT_MASK = 0x30
        const val UI_MODE_NIGHT_NO = 0x10
        const val UI_MODE_NIGHT_YES = 0x20
    }
}

class Resources {
    val configuration = Configuration()
}
