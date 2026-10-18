package io.github.ruddy2003.vestigium.ui

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * El teclado del diario: teclas con el papel, la tinta y la letra del tema (piedra y papiro, teclas redondas de máquina de escribir,
 * pantalla de fósforo…). Español con ñ, acentos al dejar presionada la vocal, @ y # a la mano para personas y temas.
 * Todo pasa dentro de la app: lo que escribes no sale a ningún otro teclado.
 */
object Teclado {
    /** Escribe [s] donde está el cursor (reemplazando lo seleccionado). */
    fun insertar(v: TextFieldValue, s: String): TextFieldValue {
        val a = v.selection.min.coerceIn(0, v.text.length); val b = v.selection.max.coerceIn(0, v.text.length)
        val t = v.text.substring(0, a) + s + v.text.substring(b)
        return TextFieldValue(t, TextRange(a + s.length))
    }

    /** Borra lo seleccionado o el carácter antes del cursor (un emoji cuenta como uno). */
    fun borrar(v: TextFieldValue): TextFieldValue {
        val a = v.selection.min.coerceIn(0, v.text.length); val b = v.selection.max.coerceIn(0, v.text.length)
        if (a != b) return TextFieldValue(v.text.substring(0, a) + v.text.substring(b), TextRange(a))
        if (a == 0) return v
        val bi = java.text.BreakIterator.getCharacterInstance(); bi.setText(v.text)
        val ini = bi.preceding(a).takeIf { it != java.text.BreakIterator.DONE } ?: (a - 1)
        return TextFieldValue(v.text.substring(0, ini) + v.text.substring(a), TextRange(ini))
    }

    /** Borra la palabra antes del cursor (al dejar presionado el borrar un rato). */
    fun borrarPalabra(v: TextFieldValue): TextFieldValue {
        val a = v.selection.min.coerceIn(0, v.text.length)
        if (a == 0) return v
        var i = a
        while (i > 0 && v.text[i - 1].isWhitespace()) i--
        while (i > 0 && !v.text[i - 1].isWhitespace()) i--
        return TextFieldValue(v.text.substring(0, i) + v.text.substring(a), TextRange(i))
    }

    /** ¿Empieza una oración? (al principio, tras punto, signo o salto de línea). */
    fun inicioOracion(v: TextFieldValue): Boolean {
        val antes = v.text.substring(0, v.selection.min.coerceIn(0, v.text.length)).trimEnd(' ')
        return antes.isEmpty() || antes.last() in ".!?¡¿\n" || v.text.substring(0, v.selection.min.coerceIn(0, v.text.length)).endsWith("\n")
    }

    val ACENTOS = mapOf(
        "a" to listOf("á", "à", "ä", "â", "ª"), "e" to listOf("é", "è", "ë", "ê", "€"), "i" to listOf("í", "ì", "ï", "î"),
        "o" to listOf("ó", "ò", "ö", "ô", "º"), "u" to listOf("ú", "ü", "ù", "û"), "n" to listOf("ñ"), "c" to listOf("ç"),
        "." to listOf("…", "¿", "?", "¡", "!", ";", ":"), "," to listOf("\"", "«", "»", "'", "—"), "s" to listOf("ß"),
    )
}

/** Cómo se ven las teclas de cada tema. */
private class EstiloTeclado(val fondo: Color, val tecla: Color, val especial: Color, val letra: Color, val letraEspecial: Color, val borde: Color?,
                            val forma: Shape, val sombra: Boolean, val mayus: Boolean)

private fun estiloTeclado(t: TemaId, p: Pagina): EstiloTeclado {
    val R = RoundedCornerShape(5.dp)
    return when (t) {
        // Máquina de escribir: teclas redondas negras con aro de metal.
        TemaId.DISTOPIA, TemaId.URSS, TemaId.NAPOLEON -> EstiloTeclado(lerp(p.color, Color.Black, 0.12f), Color(0xFF1C1B1A), Color(0xFF3A3530), Color(0xFFF3EEDF), p.acento,
            Color(0xFFB9B2A2), RoundedCornerShape(50), true, true)
        // Pantallas: teclas planas con contorno de luz.
        TemaId.CIBER, TemaId.REFUGIO, TemaId.ESPACIO, TemaId.ORDEN -> EstiloTeclado(lerp(p.color, Color.Black, 0.25f), lerp(p.color, p.tinta, 0.06f), lerp(p.color, p.acento, 0.14f),
            p.tinta, p.acento, p.acento.copy(alpha = 0.55f), RoundedCornerShape(if (t == TemaId.ESPACIO) 10.dp else 2.dp), false, t != TemaId.ESPACIO)
        TemaId.LABORATORIO -> EstiloTeclado(Color(0xFFE6EEF1), Color.White, Color(0xFFD4E4EA), p.tinta, p.acento, Color(0xFFC5D5DC), RoundedCornerShape(10.dp), false, false)
        TemaId.SEDA -> EstiloTeclado(lerp(p.color, Color.Black, 0.3f), lerp(p.color, Color.White, 0.08f), lerp(p.color, p.acento, 0.3f), p.tinta, p.acento, p.acento.copy(alpha = 0.5f), R, true, false)
        // Papel: teclas como fichas de madera o de hueso sobre el escritorio.
        else -> EstiloTeclado(lerp(p.color, p.tinta, 0.14f), lerp(p.color, Color.White, 0.35f), lerp(p.color, p.acento, 0.18f), p.tinta, p.acento,
            p.tenue.copy(alpha = 0.45f), RoundedCornerShape(if (t == TemaId.BOTANICO || t == TemaId.NAVIDAD) 9.dp else 4.dp), true, false)
    }
}

private val FILAS = listOf("qwertyuiop", "asdfghjklñ", "zxcvbnm")
private val SIMBOLOS = listOf("1234567890", "-/:;()$&\"'", "¿?¡!*%+=")

@Composable
fun TecladoTema(tema: TemaId, p: Pagina, valor: TextFieldValue, onCambio: (TextFieldValue) -> Unit, sistema: () -> Unit, ocultar: () -> Unit) {
    val e = estiloTeclado(tema, p)
    val vista = LocalView.current
    val ctx = LocalContext.current
    var mayus by remember { mutableStateOf(0) }          // 0 no, 1 una letra, 2 fijas
    var simbolos by remember { mutableStateOf(false) }
    var ultimoMayus by remember { mutableStateOf(0L) }
    var ultimoEspacio by remember { mutableStateOf(0L) }
    val auto = mayus == 0 && Teclado.inicioOracion(valor)
    val activa = mayus > 0 || auto
    val letraFuente = p.texto

    fun toque() {
        vista.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        Sentidos.tecla(ctx, tema)
    }
    fun escribir(s: String) {
        toque()
        val txt = if (activa && s.length == 1 && s[0].isLetter()) s.uppercase() else s
        onCambio(Teclado.insertar(valor, txt))
        if (mayus == 1) mayus = 0
    }

    // Más arriba que el borde de la pantalla: abajo queda la barra de gestos y la última fila se alcanza sin estirar el dedo.
    Column(Modifier.fillMaxWidth().background(e.fondo).navigationBarsPadding().padding(start = 3.dp, end = 3.dp, top = 6.dp, bottom = 26.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        val filas = if (simbolos) SIMBOLOS else FILAS
        filas.forEachIndexed { i, fila ->
            Row(Modifier.fillMaxWidth().height(46.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (i == 1 && !simbolos) Spacer(Modifier.weight(0.01f))
                if (i == 2) Tecla(if (simbolos) "…" else when (mayus) { 2 -> "⇪"; 1 -> "⬆"; else -> if (auto) "⬆" else "⇧" }, e, letraFuente, Modifier.weight(1.5f), especial = true,
                    resaltada = !simbolos && mayus > 0, onTap = {
                        if (simbolos) { escribir("…"); return@Tecla }
                        toque()
                        val ahora = System.currentTimeMillis()
                        mayus = when { mayus == 0 && ahora - ultimoMayus < 350 -> 2; mayus == 0 -> 1; mayus == 1 && ahora - ultimoMayus < 350 -> 2; else -> 0 }
                        ultimoMayus = ahora
                    })
                fila.forEach { ch ->
                    val s = ch.toString()
                    Tecla(if (activa && !simbolos) s.uppercase() else s, e, letraFuente, Modifier.weight(1f), alternas = Teclado.ACENTOS[s].orEmpty(),
                        onAlterna = { a -> escribir(a) }, onTap = { escribir(s) })
                }
                if (i == 2) TeclaBorrar(e, letraFuente, Modifier.weight(1.5f), toque = ::toque, borrar = { onCambio(Teclado.borrar(it)) }, borrarPalabra = { onCambio(Teclado.borrarPalabra(it)) }, valor = { valor })
            }
        }
        Row(Modifier.fillMaxWidth().height(46.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Tecla(if (simbolos) "abc" else "?123", e, letraFuente, Modifier.weight(1.4f), especial = true, onTap = { toque(); simbolos = !simbolos })
            Tecla("⌨", e, letraFuente, Modifier.weight(1f), especial = true, onTap = { toque(); sistema() }, onLargo = { ocultar() })
            Tecla("@", e, letraFuente, Modifier.weight(1f), especial = true, onTap = { escribir("@") })
            Tecla(",", e, letraFuente, Modifier.weight(1f), alternas = Teclado.ACENTOS[","].orEmpty(), onAlterna = { escribir(it) }, onTap = { escribir(",") })
            Tecla(" ", e, letraFuente, Modifier.weight(4f), onTap = {
                // Dos espacios seguidos ponen punto, como en los teclados del teléfono.
                val ahora = System.currentTimeMillis()
                val t = valor.text; val c = valor.selection.min
                if (ahora - ultimoEspacio < 450 && c >= 2 && t[c - 1] == ' ' && t[c - 2].isLetterOrDigit()) {
                    toque(); onCambio(Teclado.insertar(TextFieldValue(t, TextRange(c - 1, c)), ". ")); ultimoEspacio = 0
                } else { escribir(" "); ultimoEspacio = ahora }
            })
            Tecla(".", e, letraFuente, Modifier.weight(1f), alternas = Teclado.ACENTOS["."].orEmpty(), onAlterna = { escribir(it) }, onTap = { escribir(".") })
            Tecla("#", e, letraFuente, Modifier.weight(1f), especial = true, onTap = { escribir("#") })
            Tecla("⏎", e, letraFuente, Modifier.weight(1.4f), especial = true, onTap = { escribir("\n") })
        }
    }
}

/** Lo que dice la barra espaciadora en cada tema. */
private val etiquetaEspacio: String get() = when (Estilo.tema) {
    TemaId.ROMANO -> "SPATIVM"; TemaId.HELENO -> "ΔΙΑΣΤΗΜΑ"; TemaId.CIBER -> "[ space ]"; TemaId.ANDALUS -> "فراغ"; TemaId.MONGOL -> "зай"
    TemaId.RENACIMIENTO -> "spazio"; TemaId.DISTOPIA, TemaId.URSS -> "ESPACIO"; TemaId.REFUGIO -> "> espacio"; else -> "espacio"
}

@Composable
private fun Tecla(texto: String, e: EstiloTeclado, fuente: androidx.compose.ui.text.font.FontFamily, modifier: Modifier, especial: Boolean = false, resaltada: Boolean = false,
                  alternas: List<String> = emptyList(), onAlterna: (String) -> Unit = {}, onLargo: (() -> Unit)? = null, onTap: () -> Unit) {
    var apretada by remember { mutableStateOf(false) }
    var abiertas by remember { mutableStateOf(false) }
    // Los gestos viven más que una composición: siempre llaman a la versión más nueva (con el texto de ahora).
    val tap by rememberUpdatedState(onTap)
    val alterna by rememberUpdatedState(onAlterna)
    val largo by rememberUpdatedState(onLargo)
    val fondo = when { resaltada -> e.letraEspecial; especial -> e.especial; else -> e.tecla }
    val color = when { resaltada -> e.tecla; especial -> e.letraEspecial; else -> e.letra }
    Box(modifier.fillMaxHeight()
        .graphicsLayer { val s = if (apretada) 0.92f else 1f; scaleX = s; scaleY = s }
        .then(if (e.sombra) Modifier.shadow(if (apretada) 0.dp else 1.5.dp, e.forma, clip = false) else Modifier)
        .background(if (apretada) lerp(fondo, e.letraEspecial, 0.25f) else fondo, e.forma)
        .then(if (e.borde != null) Modifier.border(1.dp, e.borde, e.forma) else Modifier)
        .pointerInput(texto, alternas) {
            detectTapGestures(
                onPress = { apretada = true; tryAwaitRelease(); apretada = false },
                onLongPress = { if (alternas.isNotEmpty()) abiertas = true else largo?.invoke() },
                onTap = { tap() },
            )
        }, contentAlignment = Alignment.Center) {
        if (texto == " ") Text(etiquetaEspacio, style = TextStyle(fontFamily = fuente, fontSize = 12.sp, color = color.copy(alpha = 0.45f)))
        else Text(texto, style = TextStyle(fontFamily = if (texto.length == 1 && texto[0].isLetterOrDigit()) fuente else null, fontSize = if (texto.length > 2) 13.sp else 19.sp,
            color = color, textAlign = TextAlign.Center))
        if (alternas.isNotEmpty() && !especial) Text("·", style = TextStyle(fontSize = 10.sp, color = color.copy(alpha = 0.45f)), modifier = Modifier.align(Alignment.TopEnd).padding(end = 3.dp))
        if (abiertas) Popup(alignment = Alignment.BottomCenter, offset = IntOffset(0, -150), onDismissRequest = { abiertas = false }) {
            Row(Modifier.shadow(6.dp, e.forma).background(e.tecla, e.forma).then(if (e.borde != null) Modifier.border(1.dp, e.borde, e.forma) else Modifier).padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                alternas.forEach { a ->
                    Box(Modifier.width(40.dp).height(46.dp).background(e.especial, e.forma).pointerInput(a) { detectTapGestures(onTap = { alterna(a); abiertas = false }) },
                        contentAlignment = Alignment.Center) {
                        Text(a, style = TextStyle(fontFamily = fuente, fontSize = 20.sp, color = e.letra))
                    }
                }
            }
        }
    }
}

/** Borrar: uno al tocar; al dejarlo presionado repite, y después de un rato borra palabras enteras. */
@Composable
private fun TeclaBorrar(e: EstiloTeclado, fuente: androidx.compose.ui.text.font.FontFamily, modifier: Modifier, toque: () -> Unit,
                        borrar: (TextFieldValue) -> Unit, borrarPalabra: (TextFieldValue) -> Unit, valor: () -> TextFieldValue) {
    val scope = rememberCoroutineScope()
    var apretada by remember { mutableStateOf(false) }
    val actual by rememberUpdatedState(valor)
    val uno by rememberUpdatedState(borrar)
    val palabra by rememberUpdatedState(borrarPalabra)
    val sentir by rememberUpdatedState(toque)
    Box(modifier.fillMaxHeight()
        .graphicsLayer { val s = if (apretada) 0.92f else 1f; scaleX = s; scaleY = s }
        .background(if (apretada) lerp(e.especial, e.letraEspecial, 0.25f) else e.especial, e.forma)
        .then(if (e.borde != null) Modifier.border(1.dp, e.borde, e.forma) else Modifier)
        .pointerInput(Unit) {
            detectTapGestures(onPress = {
                apretada = true
                sentir(); uno(actual())
                val j: Job = scope.launch {
                    delay(420)
                    var n = 0
                    while (true) {
                        if (n < 14) uno(actual()) else palabra(actual())
                        n++; delay(if (n < 14) 55 else 140)
                    }
                }
                tryAwaitRelease(); j.cancel(); apretada = false
            })
        }, contentAlignment = Alignment.Center) {
        Text("⌫", style = TextStyle(fontSize = 19.sp, color = e.letraEspecial))
    }
}
