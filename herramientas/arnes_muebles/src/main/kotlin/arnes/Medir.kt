// La medida del «peso» del dibujo de los muebles y de las piezas comunes, en esta máquina y sin Android (19 nov 2026).
//
//   medir.sh [grupo,grupo,...] [tema,tema,...]
//
// Para cada grupo (biblioteca, ideario, vida, comun, decoracion) y cada variante (Psicodelia en sus dos caras, claro y oscuro,
// con intensidad 0, 0.5 y 1; Helénico y Laboratorio, y los otros cuatro temas del lote 10, en claro y oscuro):
// - **grabar**: lo que tarda en ejecutarse el código de dibujo de la app y grabarse en una lista de operaciones de Skia (un
//   `Picture`). Es lo que en el teléfono pasa en el hilo de la interfaz cada vez que algo obliga a volver a dibujar la pieza.
// - **pintar**: lo que tarda en pintarse esa lista en píxeles, con Skia en la CPU. En el teléfono lo hace la GPU (mucho más
//   rápido en números absolutos), pero el orden entre temas es una pista de lo que cuesta cada cuadro que se repite.
// - **ops** y **KB**: cuántas operaciones lleva la lista y cuánto ocupa (los caminos con muchos puntos ocupan más).
// Cada medida es la mediana de hasta 200 repeticiones, después de un calentamiento. Los dibujos que guardan en caché
// (`PiezasPsicodelia.enCache`) se miden ya con la caché llena, como en el uso normal; «1.ª» es la primera vez (con la caché vacía).
//
// Los mismos datos de ejemplo que `dibujar.sh`: el estante con las 35 entradas, el Ideario con 9 temas, los tres muebles de
// Vida y una mezcla de piezas comunes como la de una pantalla de Hoy o de Ajustes. La decoración (emblema, banda bajo el
// título, subrayado, filete y fondo vivo) es solo de Psicodelia: la de los otros temas vive en archivos que el arnés no compila.
package arnes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asComposeCanvas
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.use
import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Cara
import io.github.ruddy2003.vestigium.ui.EstadoTema
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.PiezasPsicodelia
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.biblioteca.DisenoBiblioteca
import io.github.ruddy2003.vestigium.ui.biblioteca.DisposicionEstante
import io.github.ruddy2003.vestigium.ui.biblioteca.EstanteTema
import io.github.ruddy2003.vestigium.ui.biblioteca.GrupoEstante
import io.github.ruddy2003.vestigium.ui.biblioteca.Volumen
import io.github.ruddy2003.vestigium.ui.biblioteca.disenoBiblioteca
import io.github.ruddy2003.vestigium.ui.comun.ComunArcontes
import io.github.ruddy2003.vestigium.ui.comun.ComunFrente
import io.github.ruddy2003.vestigium.ui.comun.ComunGalia
import io.github.ruddy2003.vestigium.ui.comun.ComunHeleno
import io.github.ruddy2003.vestigium.ui.comun.ComunLaboratorio
import io.github.ruddy2003.vestigium.ui.comun.ComunNouveau
import io.github.ruddy2003.vestigium.ui.comun.ComunPsicodelia
import io.github.ruddy2003.vestigium.ui.comun.DisenoComun
import io.github.ruddy2003.vestigium.ui.comun.EstadoAviso
import io.github.ruddy2003.vestigium.ui.comun.EstadoBotonTexto
import io.github.ruddy2003.vestigium.ui.comun.EstadoCifra
import io.github.ruddy2003.vestigium.ui.comun.EstadoFila
import io.github.ruddy2003.vestigium.ui.comun.EstadoMarco
import io.github.ruddy2003.vestigium.ui.comun.EstadoSeparador
import io.github.ruddy2003.vestigium.ui.comun.EstadoTarjeta
import io.github.ruddy2003.vestigium.ui.comun.EstadoVacio
import io.github.ruddy2003.vestigium.ui.comun.TipoTarjeta
import io.github.ruddy2003.vestigium.ui.esquemaDe
import io.github.ruddy2003.vestigium.ui.ideario.Disposicion
import io.github.ruddy2003.vestigium.ui.ideario.EstadoCajon
import io.github.ruddy2003.vestigium.ui.ideario.disenoIdeas
import io.github.ruddy2003.vestigium.ui.pantallas.cajones
import io.github.ruddy2003.vestigium.ui.tipoDe
import io.github.ruddy2003.vestigium.ui.vida.EstadoMueble
import io.github.ruddy2003.vestigium.ui.vida.disenoVida
import org.jetbrains.skia.PictureRecorder
import org.jetbrains.skia.Surface
import kotlin.system.exitProcess

private const val ANCHO_M = 1080
private const val DENS = 2.625f
private val densidad = Density(DENS, 1f)

/** Una llamada de dibujo con el tamaño de su lienzo. */
private class Llamada(val w: Float, val h: Float, val dibujo: DrawScope.() -> Unit)

/** Una variante: el tema, claro u oscuro y (en Psicodelia) la cara y la intensidad. */
private class Variante(val tema: TemaId, val oscuro: Boolean, val cara: Cara = Cara.BASE, val i: Float = 0f) {
    val nombre: String get() = if (!tema.conCaras) "${tema.id} ${if (oscuro) "oscuro" else "claro"}"
        else "${tema.id} ${if (cara == Cara.BASE) "ciudad" else "tinta"} ${if (oscuro) "oscuro" else "claro"}" + (if (cara == Cara.INTENSA) " i=$i" else "")
}

private class Resultado(val grabarUs: Double, val pintarUs: Double, val primeraMs: Double, val ops: Int, val kb: Double, val n: Int)

private fun poner(v: Variante) {
    Repo.config = Config(tema = v.tema.id, modo = if (v.oscuro) "oscuro" else "claro", animaciones = false, bienvenida_vista = true, widget_cifra = "racha")
    EstadoTema.forzar(if (v.tema.conCaras) v.cara else null, if (v.tema.conCaras) v.i else null)
}

private fun grabar(llamadas: List<Llamada>): org.jetbrains.skia.Picture {
    val w = llamadas.maxOf { it.w }; val h = llamadas.maxOf { it.h }
    val rec = PictureRecorder()
    val lienzo = rec.beginRecording(org.jetbrains.skia.Rect.makeWH(w, h), null).asComposeCanvas()
    val ds = CanvasDrawScope()
    llamadas.forEach { l -> ds.draw(densidad, LayoutDirection.Ltr, lienzo, Size(l.w, l.h)) { l.dibujo(this) } }
    return rec.finishRecordingAsPicture()
}

private fun mediana(v: List<Long>): Double = v.sorted().let { s -> if (s.isEmpty()) 0.0 else s[s.size / 2] / 1000.0 }

/** Mide [llamadas]: la primera vez, el calentamiento y hasta 200 repeticiones (o 4 s) de grabar y de pintar. */
private fun medir(llamadas: List<Llamada>): Resultado {
    val t0 = System.nanoTime()
    val primera = grabar(llamadas)
    val primeraMs = (System.nanoTime() - t0) / 1e6
    val ops = primera.approximateOpCount; val kb = primera.approximateBytesUsed / 1024.0
    val w = llamadas.maxOf { it.w }.toInt().coerceAtLeast(1); val h = llamadas.maxOf { it.h }.toInt().coerceAtLeast(1)
    val sup = Surface.makeRasterN32Premul(w, h)
    // Calentamiento: 30 vueltas o 1.5 s.
    val finCal = System.nanoTime() + 1_500_000_000L
    var k = 0
    while (k < 30 && System.nanoTime() < finCal) { grabar(llamadas).also { sup.canvas.clear(0); it.playback(sup.canvas) }.close(); k++ }
    val tg = ArrayList<Long>(); val tp = ArrayList<Long>()
    val fin = System.nanoTime() + 4_000_000_000L
    while (tg.size < 200 && System.nanoTime() < fin) {
        val a = System.nanoTime()
        val p = grabar(llamadas)
        val b = System.nanoTime()
        sup.canvas.clear(0); p.playback(sup.canvas)
        val c = System.nanoTime()
        p.close()
        tg += b - a; tp += c - b
    }
    primera.close(); sup.close()
    return Resultado(mediana(tg), mediana(tp), primeraMs, ops, kb, tg.size)
}

// ---------------------------------------------------------------- los grupos

private val medidor: TextMeasurer by lazy { TextMeasurer(createFontFamilyResolver(), densidad, LayoutDirection.Ltr, 64) }
private fun dp(x: Float) = x * DENS

/** El estante con las 35 entradas: se arma la escena una vez con un espía que anota cada llamada (el mueble y cada volumen). */
private fun biblioteca(v: Variante): List<Llamada> {
    val anotadas = ArrayList<Llamada>()
    var anotando = true
    val real = disenoBiblioteca(v.tema)
    val espia = object : DisenoBiblioteca by real {
        override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
            if (anotando) anotadas += Llamada(size.width, size.height) { with(real) { estante(d, medidor, oscuro, noche) } }
            with(real) { estante(d, medidor, oscuro, noche) }
        }
        override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
            if (anotando) anotadas += Llamada(size.width, size.height) { with(real) { volumen(v, medidor, oscuro) } }
            with(real) { volumen(v, medidor, oscuro) }
        }
    }
    // ARNES_SUENOS=1: el estante de los Sueños, con los mismos 14 sueños que dibuja `dibujar.sh`.
    Repo.dias = if (conSuenos) suenosEjemplo(diasEjemplo()) else diasEjemplo()
    val todas = Repo.dias.values.filter { if (conSuenos) it.sueno_texto.isNotBlank() else it.nota.isNotBlank() }.sortedByDescending { it.fecha }
    val porMes = todas.groupBy { it.fecha.take(7) }.toSortedMap(compareByDescending { it })
    val grupos = porMes.entries.take(6).map { (mes, lista) -> GrupoEstante(Estilo.mesConComun(mes.drop(5).toInt(), mes.take(4).toInt()), lista) }
    val o = v.tema.soloOscuro || v.oscuro
    ImageComposeScene(ANCHO_M, 9000, densidad) {
        MaterialTheme(colorScheme = esquemaDe(v.tema, o), typography = tipoDe(v.tema, o)) {
            Box(Modifier.padding(horizontal = 16.dp)) { EstanteTema(espia, grupos, conSuenos, {}) }
        }
    }.use { it.render(0) }
    anotando = false
    return anotadas
}

/** El Ideario con 9 temas, con la misma cuenta de lugares que `MuebleIdeas`. */
private fun ideario(v: Variante): List<Llamada> {
    val d = disenoIdeas(v.tema)
    val o = v.tema.soloOscuro || v.oscuro
    val caj = cajones(ideasConTemas(9))
    val ancho = ANCHO_M - dp(32f)
    val lado = ancho * d.lado; val sep = ancho * d.hueco
    val anchoH = (ancho - lado * 2 - sep * (d.cols - 1)) / d.cols
    val altoH = anchoH / d.proporcion
    val filas = ((caj.size + d.cols - 1) / d.cols).coerceAtLeast(1)
    val altoCorona = ancho * d.corona; val altoBase = ancho * d.base
    val alto = altoCorona + sep + altoH * filas + sep * (filas - 1) + sep + altoBase
    val huecos = filas * d.cols
    fun x(i: Int) = lado + (anchoH + sep) * (i % d.cols)
    fun y(i: Int) = altoCorona + sep + (altoH + sep) * (i / d.cols)
    val disp = Disposicion(Rect(0f, 0f, ancho, altoCorona), (0 until filas).map { f -> Rect(0f, y(f * d.cols), ancho, y(f * d.cols) + altoH) },
        (0 until huecos).map { i -> Rect(x(i), y(i), x(i) + anchoH, y(i) + altoH) }, Rect(0f, alto - altoBase, ancho, alto), sep, 0)
    return listOf(Llamada(ancho, alto) { with(d) { mueble(disp, o) } }) + (0 until huecos).map { i ->
        val c = caj.getOrNull(i)
        val e = if (c == null) EstadoCajon(0, false, 0f, 7919 * (i + 1), vacio = true, fila = i / d.cols, col = i % d.cols, filas = filas, cols = d.cols)
            else EstadoCajon(c.n, c.tema.isEmpty(), 0f, c.tema.hashCode(), fila = i / d.cols, col = i % d.cols, filas = filas, cols = d.cols)
        Llamada(anchoH, altoH) { with(d) { compartimento(e, o) } }
    }
}

/** Los tres muebles de la pestaña Vida (Social, Mapas y Otras vidas) a lo ancho de la pantalla. */
private fun vida(v: Variante): List<Llamada> {
    val d = disenoVida(v.tema)
    val o = v.tema.soloOscuro || v.oscuro
    val cs = esquemaDe(v.tema, o)
    val w = ANCHO_M - dp(32f)
    return listOf(
        Llamada(w, dp(230f)) { with(d) { muebleSocial(EstadoMueble(cs.surface, cs.primary, cs.secondary, cs.onSurface, 5), o) } },
        Llamada(w, dp(220f)) { with(d) { muebleMapas(EstadoMueble(cs.surface, cs.primary, cs.secondary, cs.onSurface, 9), o) } },
        Llamada(w, dp(190f)) { with(d) { muebleOtrasVidas(EstadoMueble(cs.surface, cs.primary, cs.secondary, cs.onSurface, 7), o) } },
    )
}

private fun comunDe(t: TemaId): DisenoComun? = when (t) {
    TemaId.PSICODELIA -> ComunPsicodelia
    TemaId.HELENO -> ComunHeleno
    TemaId.LABORATORIO -> ComunLaboratorio
    TemaId.NOUVEAU -> ComunNouveau
    TemaId.GALIA -> ComunGalia
    TemaId.ARCONTES -> ComunArcontes
    TemaId.FRENTE -> ComunFrente
    else -> null
}

/**
 * Una mezcla de piezas comunes como la de una pantalla: seis tarjetas de Hoy (cuatro cifras, una destacada, «te falta hoy»),
 * la noche y una de Vida, seis filas de menú con su flecha, tres campos, cuatro separadores, dos botones de texto, dos
 * avisos, tres cifras y un vacío con su dibujo.
 */
private fun comun(v: Variante): List<Llamada> {
    val d = comunDe(v.tema) ?: return emptyList()
    val o = v.tema.soloOscuro || v.oscuro
    val W = dp(380f)
    val l = ArrayList<Llamada>()
    fun pon(w: Float, h: Float, f: DrawScope.() -> Unit) { l += Llamada(w, h, f) }
    repeat(4) { s -> pon(dp(170f), dp(96f)) { with(d) { tarjeta(EstadoTarjeta(TipoTarjeta.METRICA, false, false, s), o) } } }
    pon(dp(170f), dp(96f)) { with(d) { tarjeta(EstadoTarjeta(TipoTarjeta.METRICA, true, false, 4), o) } }
    pon(W, dp(80f)) { with(d) { tarjeta(EstadoTarjeta(TipoTarjeta.PENDIENTE, false, false, 5), o) } }
    pon(W, dp(120f)) { with(d) { tarjeta(EstadoTarjeta(TipoTarjeta.NOCHE, false, false, 6), o) } }
    pon(W, dp(110f)) { with(d) { tarjeta(EstadoTarjeta(TipoTarjeta.VIDA, false, false, 7), o) } }
    repeat(6) { s ->
        pon(W, dp(52f)) { with(d) { fila(EstadoFila(false, s % 2 == 0, s), o) } }
        pon(dp(20f), dp(20f)) { with(d) { flechaFila(o) } }
    }
    pon(W, dp(54f)) { with(d) { marcoCampo(EstadoMarco(false, false, true, false, true, 0), o) } }
    pon(W, dp(54f)) { with(d) { marcoCampo(EstadoMarco(true, false, true, false, false, 1), o) } }
    pon(W, dp(110f)) { with(d) { marcoCampo(EstadoMarco(false, false, true, true, false, 2), o) } }
    repeat(4) { s -> pon(W, dp(12f)) { with(d) { separador(EstadoSeparador(s % 2 == 1, s), o) } } }
    repeat(2) { s -> pon(dp(120f), dp(48f)) { with(d) { botonTexto(EstadoBotonTexto(false, true, s), o) } } }
    repeat(2) { s -> pon(W, dp(56f)) { with(d) { aviso(EstadoAviso(s), o) } } }
    repeat(3) { s -> pon(dp(110f), dp(62f)) { with(d) { cifra(EstadoCifra(s), o) } } }
    pon(W, dp(150f)) { with(d) { marcoVacio(EstadoVacio(1), o) } }
    pon(dp(48f), dp(48f)) { with(d) { dibujoVacio(EstadoVacio(1), o) } }
    return l
}

/** La decoración de Psicodelia: el emblema (30 dp), la banda bajo el título, el subrayado y el filete. */
private fun decoracion(v: Variante): List<Llamada> {
    if (v.tema != TemaId.PSICODELIA) return emptyList()
    val o = v.oscuro
    return listOf(
        Llamada(dp(30f), dp(30f)) { with(PiezasPsicodelia) { emblema(center, size.minDimension / 2, 0.3f, o) } },
        Llamada(dp(380f), dp(13f)) { with(PiezasPsicodelia) { bandaTitulo(o) } },
        Llamada(dp(250f), dp(30f)) { with(PiezasPsicodelia) { subrayado(size.width * 0.8f, size.height - 2.4f * density, colores(o)) } },
        Llamada(dp(380f), dp(12f)) { with(PiezasPsicodelia) { filete(3 * density, size.width - 3 * density, size.height / 2, colores(o)) } },
    )
}

/** El fondo vivo de Psicodelia a pantalla completa (un cuadro de los ~30 por segundo que dibuja mientras se anima). */
private fun fondo(v: Variante): List<Llamada> {
    if (v.tema != TemaId.PSICODELIA) return emptyList()
    return listOf(Llamada(ANCHO_M.toFloat(), 2200f) { with(PiezasPsicodelia) { fondoVivo(0.3f, v.oscuro, 1f) } })
}

private val GRUPOS: Map<String, (Variante) -> List<Llamada>> = linkedMapOf(
    "biblioteca" to ::biblioteca, "ideario" to ::ideario, "vida" to ::vida, "comun" to ::comun, "decoracion" to ::decoracion, "fondo" to ::fondo,
)

private fun variantes(temas: List<TemaId>): List<Variante> = temas.flatMap { t ->
    if (t.conCaras) listOf(
        Variante(t, false, Cara.BASE), Variante(t, true, Cara.BASE),
        Variante(t, false, Cara.INTENSA, 0f), Variante(t, true, Cara.INTENSA, 0f),
        Variante(t, false, Cara.INTENSA, 0.5f), Variante(t, true, Cara.INTENSA, 0.5f),
        Variante(t, false, Cara.INTENSA, 1f), Variante(t, true, Cara.INTENSA, 1f),
    ) else listOf(Variante(t, false), Variante(t, true))
}

fun main(args: Array<String>) {
    val grupos = args.getOrNull(0)?.split(',')?.filter { it.isNotBlank() } ?: GRUPOS.keys.toList()
    val temas = (args.getOrNull(1)?.split(',')?.filter { it.isNotBlank() } ?: listOf("psicodelia", "heleno", "laboratorio", "nouveau", "galia", "arcontes", "frente"))
        .map { id -> TemaId.entries.firstOrNull { it.id == id } ?: run { System.err.println("Tema desconocido: $id"); exitProcess(2) } }
    println("grupo\tvariante\tgrabar_us\tpintar_us\tprimera_ms\tops\tKB\tn")
    for (g in grupos) {
        val f = GRUPOS[g] ?: run { System.err.println("Grupo desconocido: $g (hay ${GRUPOS.keys})"); exitProcess(2) }
        for (v in variantes(temas)) {
            poner(v)
            val llamadas = f(v)
            if (llamadas.isEmpty()) continue
            // MEDIR_DETALLE=1: además, cada llamada por separado (el mueble y cada volumen, cada pieza…), una vez.
            if (System.getenv("MEDIR_DETALLE") == "1") llamadas.forEachIndexed { j, l ->
                val t0 = System.nanoTime(); val p = grabar(listOf(l)); val us = (System.nanoTime() - t0) / 1000
                println("  · $j ${l.w.toInt()}×${l.h.toInt()}\t${p.approximateOpCount} ops\t${"%.1f".format(p.approximateBytesUsed / 1024.0)} KB\t$us µs")
                p.close()
            }
            val r = medir(llamadas)
            println("$g\t${v.nombre}\t${"%.0f".format(r.grabarUs)}\t${"%.0f".format(r.pintarUs)}\t${"%.1f".format(r.primeraMs)}\t${r.ops}\t${"%.1f".format(r.kb)}\t${r.n}")
        }
    }
    exitProcess(0)
}
