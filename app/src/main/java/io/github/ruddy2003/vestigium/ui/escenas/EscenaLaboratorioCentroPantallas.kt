package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.sin

/**
 * La pared de pantallas de la Vigía, en el comedor grande (`composicion_laboratorio_centro.md`, «Enfrente»): un tramo
 * entero de la pared curva, de dos pisos de alto, hecho de muchas pantallas viejas con sus juntas, con **las ruinas de
 * arriba en directo, a su hora de verdad** («Casi nadie las mira. Los niños, sí»). Es el cielo de este cuadro (regla 2):
 * cambia con la hora de arriba, con las nubes del día, con la lluvia y el sol de la subida, y tiñe el hall.
 *
 * - **La imagen grande**, de muchas pantallas: el cielo de arriba a su hora y las ruinas en tres filas, como la pantalla del
 *   laboratorio (`PantallaLab`), pero en la pared curva y en grande. Arriba, en ella, el campamento de la doctora.
 * - **Las pantallas sueltas** de los lados y de abajo: otras cámaras (una calle de escombros, una torre, la caseta del
 *   destello), alguna apagada o de otro tono.
 * - **El humo** del 8 de abril de la temporada 1, «en una de arriba a la derecha»; **la mancha verde** desde `verde`, «en una
 *   de abajo», que crece con `superficie`.
 */
internal object PantallasCentro {
    private val C = ColorLaboratorio

    /** Las pantallas: columnas y filas. */
    const val COLUMNAS = 10
    const val FILAS = 6

    /** La pantalla de abajo con la mancha verde (columna, fila) y la de arriba a la derecha con el humo. */
    const val VERDE_COL = 0
    const val VERDE_FILA = 2
    const val HUMO_COL = 8
    const val HUMO_FILA = 1

    /** Un punto de la pared: [u] de 0 (su borde izquierdo en el cuadro) a 1 (el derecho), [w] de 0 (arriba) a 1 (abajo). */
    fun enLaPared(u: Float, w: Float, metido: Float = 0.03f): V3 =
        GeoCentro.aro(GeoCentro.RW - metido, GeoCentro.PY1 + (GeoCentro.PY0 - GeoCentro.PY1) * w, GeoCentro.PA1 + (GeoCentro.PA0 - GeoCentro.PA1) * u)

    fun q(u: Float, w: Float): Offset = CamCentro.p(enLaPared(u, w))

    /** Cuántas unidades del lienzo mide una unidad de [u] en [u]. */
    fun k(u: Float): Float = CamCentro.k(enLaPared(u, 0.5f)) * GeoCentro.RW * GeoCentro.rad(GeoCentro.PA1 - GeoCentro.PA0)

    private fun forma(ps: List<Pair<Float, Float>>): Path = poligono(ps.map { q(it.first, it.second) })

    /** Un rectángulo de la pared, de ([u0], [w0]) a ([u1], [w1]), con los lados curvos (en [n] pasos). */
    fun rect(u0: Float, w0: Float, u1: Float, w1: Float, n: Int = 6): Path =
        poligono((0..n).map { q(u0 + (u1 - u0) * it / n, w0) } + (0..n).map { q(u1 - (u1 - u0) * it / n, w1) })

    /** La pantalla de la columna [c] y la fila [f], sin su junta. */
    fun pantalla(c: Int, f: Int, junta: Float = 0.006f): Path = rect(c / COLUMNAS.toFloat() + junta, f / FILAS.toFloat() + junta * 1.4f, (c + 1) / COLUMNAS.toFloat() - junta, (f + 1) / FILAS.toFloat() - junta * 1.4f, 2)

    /** El trozo de la imagen grande: las columnas 1 a 8, las filas 0 a 4. */
    private const val U0 = 1f / COLUMNAS
    private const val U1 = 9f / COLUMNAS
    private const val W0 = 0f
    private const val W1 = 5f / FILAS

    /** Lo que ve una cámara: el punto ([s], [t]) de su imagen (de 0 a 1) en el lienzo, y cuánto mide en él una unidad de [s]. */
    class Camara(val u0: Float, val w0: Float, val u1: Float, val w1: Float) {
        fun q(s: Float, t: Float): Offset = PantallasCentro.q(u0 + (u1 - u0) * s, w0 + (w1 - w0) * t)
        fun k(s: Float): Float = PantallasCentro.k(u0 + (u1 - u0) * s) * (u1 - u0)
        fun forma(ps: List<Pair<Float, Float>>): Path = poligono(ps.map { q(it.first, it.second) })
    }

    fun DrawScope.pared(v: VistaCentro) {
        val img = PantallaLab.imagen(v.m)
        val todo = rect(0f, 0f, 1f, 1f, 10)
        // El fondo: el marco de metal viejo de toda la pared y las juntas, oscuras.
        drawPath(rect(-0.012f, -0.02f, 1.012f, 1.03f, 10), v.c(enLaPared(0.5f, 1f), lerp(C.ACERO, C.NEGRO, 0.35f), V3(-1f, 0f, -0.2f)))
        drawPath(todo, lerp(C.NEGRO, C.SOMBRA, 0.2f))
        if (!img.encendida) { apagada(v); return }
        val grande = Camara(U0, W0, U1, W1)
        clipPath(rect(U0, W0, U1, W1, 8)) {
            with(RuinasCentro(grande)) { cielo(img); ruinas(img); if (img.arriba) campamento(img) }
            if (img.humo) humo(grande, img)
        }
        sueltas(v, img)
        juntas(v)
        efectos(v, img)
    }

    /** Apagadas (el tercer día de las jornadas): el vidrio negro de cada pantalla, con el reflejo del hall. */
    private fun DrawScope.apagada(v: VistaCentro) {
        for (c in 0 until COLUMNAS) for (f in 0 until FILAS) drawPath(pantalla(c, f), lerp(C.NEGRO, C.SOMBRA, 0.15f + 0.04f * ((c * 3 + f) % 4)))
        drawPath(forma(listOf(0.2f to 0f, 0.4f to 0f, 0.7f to 1f, 0.5f to 1f)), v.c(enLaPared(0.5f, 0.5f), C.HORMIGON, V3(-1f, 0f, 0f)).copy(alpha = 0.1f))
        juntas(v)
    }

    /** La luz de arriba en la piedra de las otras cámaras: [cara] 1 al sol, 0 de frente, −1 a la sombra; [lejos] la lleva a la bruma. */
    private fun luz(l: Luces, c: Color, lejos: Float, cara: Int): Color {
        val dia = (1f - l.noche).coerceIn(0f, 1f)
        val base = Color(c.red * l.ambiente.red * 0.8f, c.green * l.ambiente.green * 0.8f, c.blue * l.ambiente.blue * 0.8f)
        val col = when {
            cara > 0 -> lerp(base, Color((base.red * l.solColor.red * 1.3f).coerceIn(0f, 1f), (base.green * l.solColor.green * 1.25f).coerceIn(0f, 1f), (base.blue * l.solColor.blue * 1.1f).coerceIn(0f, 1f)), 0.7f * dia)
            cara < 0 -> lerp(base, Color(base.red * 0.52f, base.green * 0.56f, base.blue * 0.74f), 0.4f + 0.5f * dia)
            else -> base
        }
        return lerp(col, l.bruma, (lejos * l.brumaFuerza).coerceIn(0f, 0.92f))
    }

    /** El humo de las ruinas, «una columna fina y gris que sale de entre las ruinas, lejos, y se dobla con el viento», en la pantalla de arriba a la derecha. */
    private fun DrawScope.humo(cam: Camara, img: PantallaLab.Imagen) {
        // La pantalla del humo es la (HUMO_COL, HUMO_FILA): en la imagen grande, su centro.
        val s = ((HUMO_COL + 0.3f) / COLUMNAS - U0) / (U1 - U0); val base = 0.53f
        for (j in 0..19) { val t = base - j * 0.024f; val ss = s - j * 0.002f + j * j * 0.0009f
            val gris = lerp(Color(0xFF34322F), img.l.medio, 0.03f + 0.03f * j)
            mancha(cam.q(ss, t), (0.014f + j * 0.005f) * cam.k(ss), (0.018f + j * 0.0035f) * cam.k(ss), gris, (0.97f - j * 0.035f).coerceAtLeast(0.2f)) }
    }

    // ---------------------------------------------------------------- Las pantallas sueltas

    /**
     * Las pantallas de los lados y de abajo, cada una con su cámara: una calle de escombros, una torre de cerca, la caseta
     * del destello, el cielo solo; alguna apagada, alguna de otro tono (más vieja). Abajo, la de la mancha verde.
     */
    private fun DrawScope.sueltas(v: VistaCentro, img: PantallaLab.Imagen) {
        val l = img.l
        val az = Azar(Semilla.de("laboratorio", "centro", "sueltas"))
        val celdas = (0 until FILAS).flatMap { f -> listOf(0 to f, COLUMNAS - 1 to f) } + (1 until COLUMNAS - 1).map { it to FILAS - 1 }
        for ((c, f) in celdas) {
            val forma = pantalla(c, f, 0f)
            val tipo = (c * 7 + f * 3) % 6
            val cam = Camara(c / COLUMNAS.toFloat(), f / FILAS.toFloat(), (c + 1) / COLUMNAS.toFloat(), (f + 1) / FILAS.toFloat())
            clipPath(forma) {
                if (c == VERDE_COL && f == VERDE_FILA) { calle(cam, l, az); verdeDeCerca(cam, img); return@clipPath }
                when (tipo) {
                    0 -> drawPath(forma, lerp(C.NEGRO, C.SOMBRA, 0.12f))                     // apagada
                    1, 4 -> calle(cam, l, az)
                    2 -> torreDeCerca(cam, l)
                    3 -> { drawPath(forma, Brush.verticalGradient(listOf(l.alto, l.medio, l.bajo), startY = cam.q(0.5f, 0f).y, endY = cam.q(0.5f, 1f).y)); drawPath(cam.forma(listOf(0f to 0.8f, 0.3f to 0.72f, 0.5f to 0.78f, 0.8f to 0.7f, 1f to 0.76f, 1f to 1f, 0f to 1f)), luz(l, Color(0xFF6E6A62), 0.3f, 0)) }
                    else -> casetaDeCerca(cam, l)
                }
            }
        }
    }

    /** Una calle de escombros vista de cerca: el suelo, dos muros rotos en fuga y un hierro. */
    private fun DrawScope.calle(cam: Camara, l: Luces, az: Azar) {
        val piedra = Color(0xFF8E887E); val lado = if (l.solX > 0.5f) 1 else -1
        drawPath(cam.forma(listOf(0f to 0f, 1f to 0f, 1f to 0.45f, 0f to 0.45f)), Brush.verticalGradient(listOf(l.alto, l.bajo), startY = cam.q(0.5f, 0f).y, endY = cam.q(0.5f, 0.45f).y))
        drawPath(cam.forma(listOf(0f to 0.45f, 1f to 0.45f, 1f to 1f, 0f to 1f)), luz(l, lerp(piedra, C.TIERRA, 0.4f), 0.1f, 0))
        val x = az.entre(0.3f, 0.6f)
        drawPath(cam.forma(listOf(0f to 0.05f, x - 0.1f to 0.38f, x - 0.1f to 0.5f, 0f to 1f)), luz(l, piedra, 0.05f, lado))
        drawPath(cam.forma(listOf(1f to 0.15f, x + 0.12f to 0.4f, x + 0.12f to 0.5f, 1f to 0.95f)), luz(l, piedra, 0.05f, -lado))
        repeat(5) { val s = az.entre(0.2f, 0.8f); val t = az.entre(0.6f, 0.95f); mancha(cam.q(s, t), cam.k(s) * 0.06f, cam.k(s) * 0.025f, luz(l, piedra, 0f, lado), 0.9f) }
    }

    private fun DrawScope.torreDeCerca(cam: Camara, l: Luces) {
        val piedra = Color(0xFF8E887E); val lado = if (l.solX > 0.5f) 1 else -1
        drawPath(cam.forma(listOf(0f to 0f, 1f to 0f, 1f to 1f, 0f to 1f)), Brush.verticalGradient(listOf(l.alto, l.medio, l.bajo), startY = cam.q(0.5f, 0f).y, endY = cam.q(0.5f, 1f).y))
        drawPath(cam.forma(listOf(0.2f to 1f, 0.2f to 0.25f, 0.35f to 0.18f, 0.48f to 0.3f, 0.6f to 0.22f, 0.6f to 1f)), luz(l, piedra, 0.15f, 0))
        drawPath(cam.forma(listOf(0.6f to 1f, 0.6f to 0.22f, 0.72f to 0.27f, 0.72f to 1f)), luz(l, piedra, 0.15f, lado))
        for (f in 0..5) for (c in 0..2) drawPath(cam.forma(listOf(0.24f + c * 0.12f to 0.36f + f * 0.11f, 0.3f + c * 0.12f to 0.36f + f * 0.11f, 0.3f + c * 0.12f to 0.42f + f * 0.11f, 0.24f + c * 0.12f to 0.42f + f * 0.11f)), luz(l, Color(0xFF26262A), 0.1f, 0))
    }

    private fun DrawScope.casetaDeCerca(cam: Camara, l: Luces) {
        val lado = if (l.solX > 0.5f) 1 else -1
        drawPath(cam.forma(listOf(0f to 0f, 1f to 0f, 1f to 0.55f, 0f to 0.55f)), Brush.verticalGradient(listOf(l.medio, l.bajo), startY = cam.q(0.5f, 0f).y, endY = cam.q(0.5f, 0.55f).y))
        drawPath(cam.forma(listOf(0f to 0.55f, 1f to 0.55f, 1f to 1f, 0f to 1f)), luz(l, lerp(Color(0xFF8E887E), C.TIERRA, 0.5f), 0.05f, 0))
        val caseta = lerp(C.HORMIGON, C.TIERRA, 0.45f)
        drawPath(cam.forma(listOf(0.25f to 0.8f, 0.65f to 0.8f, 0.65f to 0.4f, 0.25f to 0.4f)), luz(l, caseta, 0f, 0))
        drawPath(cam.forma(listOf(0.65f to 0.8f, 0.78f to 0.74f, 0.78f to 0.36f, 0.65f to 0.4f)), luz(l, caseta, 0f, lado))
        drawPath(cam.forma(listOf(0.38f to 0.8f, 0.5f to 0.8f, 0.5f to 0.56f, 0.38f to 0.56f)), luz(l, Color(0xFF26262A), 0f, 0))
    }

    /** La mancha verde de cerca, en la pantalla de abajo: desde `verde`, crece con `superficie`. */
    private fun DrawScope.verdeDeCerca(cam: Camara, img: PantallaLab.Imagen) {
        if (img.verde <= 0.0005f) return
        val l = img.l; val k0 = (img.verde / 0.5f).coerceIn(0.08f, 1f)
        val col = lerp(C.GRAMA, Color(0xFF9CD45C), 0.3f).let { Color(it.red * l.ambiente.red, it.green * l.ambiente.green, it.blue * l.ambiente.blue) }
        val az = Azar(Semilla.de("laboratorio", "centro", "verde de cerca"))
        repeat((6 + 26 * k0).toInt()) { val s = 0.5f + az.entre(-0.45f, 0.45f) * k0; val t = 0.75f + az.entre(-0.18f, 0.2f) * (0.4f + 0.6f * k0)
            mancha(cam.q(s, t), cam.k(s) * az.entre(0.04f, 0.09f), cam.k(s) * az.entre(0.02f, 0.04f), if (az.real() > 0.4f) col else lerp(col, C.HOJA_OSC, 0.5f), 0.95f) }
    }

    // ---------------------------------------------------------------- Las juntas y el vidrio

    /** Las juntas entre pantallas: líneas oscuras, más gruesas cerca; y algunas pantallas de otro tono (más viejas o cansadas). */
    private fun DrawScope.juntas(v: VistaCentro) {
        val junta = lerp(C.NEGRO, C.ACERO, 0.15f)
        for (c in 0..COLUMNAS) { val u = c / COLUMNAS.toFloat(); drawLine(junta, q(u, 0f), q(u, 1f), (0.06f * k(u) / 11f).coerceIn(0.5f, 2.6f)) }
        for (f in 0..FILAS) { val w = f / FILAS.toFloat(); drawPath(Path().apply { val a = q(0f, w); moveTo(a.x, a.y); for (i in 1..10) { val b = q(i / 10f, w); lineTo(b.x, b.y) } }, junta, style = Stroke((0.05f * k(0.5f) / 11f).coerceIn(0.5f, 2.2f))) }
        // Cada pantalla con lo suyo: un poco más clara o más apagada que la de al lado, y el borde oscurecido (la viñeta del panel viejo).
        val az = Azar(Semilla.de("laboratorio", "centro", "paneles"))
        for (c in 0 until COLUMNAS) for (f in 0 until FILAS) {
            val forma = pantalla(c, f)
            val t = az.real()
            if (t < 0.3f) drawPath(forma, Color.Black.copy(alpha = 0.05f + 0.1f * t)) else if (t > 0.8f) drawPath(forma, Color.White.copy(alpha = 0.05f * (t - 0.8f) * 5f))
            drawPath(forma, Color.Black.copy(alpha = 0.22f), style = Stroke((0.06f * k(c / COLUMNAS.toFloat()) / 11f).coerceIn(0.5f, 2f)))
        }
        // Las pantallas cansadas: una más verde, una más magenta, una más oscura.
        for ((cf, tinte) in listOf((3 to 1) to Color(0xFF7FE0A0), (6 to 3) to Color(0xFFE08AD0), (2 to 4) to Color(0xFF101828), (7 to 0) to Color(0xFFFFE0A0))) {
            drawPath(pantalla(cf.first, cf.second), tinte.copy(alpha = if (tinte.red < 0.2f) 0.4f else 0.14f))
        }
    }

    /** Las líneas de barrido, la viñeta, el reflejo del hall en el vidrio y, el día del sol, el deslumbre. */
    private fun DrawScope.efectos(v: VistaCentro, img: PantallaLab.Imagen) {
        var w = 0f
        while (w < 1f) { drawPath(Path().apply { val a = q(0f, w); moveTo(a.x, a.y); val b = q(0.5f, w); lineTo(b.x, b.y); val c = q(1f, w); lineTo(c.x, c.y) }, Color.Black.copy(alpha = 0.05f), style = Stroke(0.35f)); w += 0.012f }
        drawPath(rect(0f, 0f, 1f, 1f, 10), Color(0xFF7FD8E0).copy(alpha = 0.04f))
        // El reflejo de la bóveda clara en la parte de arriba de la pared, y unas franjas al sesgo.
        drawPath(forma(listOf(0.15f to 0f, 0.3f to 0f, 0.18f to 1f, 0.05f to 1f)), Color.White.copy(alpha = 0.035f + 0.02f * (1f - v.noche)))
        drawPath(forma(listOf(0.62f to 0f, 0.68f to 0f, 0.58f to 1f, 0.53f to 1f)), Color.White.copy(alpha = 0.03f))
        if (EscenaLaboratorio.deslumbra(v.m)) {
            drawPath(rect(0f, 0f, 1f, 1f, 10), Color(0xFFFFF4D6).copy(alpha = 0.2f))
            val p = q(img.l.solX.coerceIn(0.1f, 0.8f), 0.12f)
            drawCircle(Brush.radialGradient(listOf(Color(0xFFFFFAEA).copy(alpha = 0.85f), Color(0xFFFFF0C8).copy(alpha = 0.35f), Color.Transparent), p, k(0.5f) * 0.6f), k(0.5f) * 0.6f, p)
        }
    }

    // ---------------------------------------------------------------- Lo que se mueve

    /** Una nube que cruza la imagen grande, el humo que se dobla, la lluvia de arriba y una pantalla que parpadea. */
    fun DrawScope.vivo(v: VistaCentro, reloj: Float) {
        val img = PantallaLab.imagen(v.m); if (!img.encendida) return
        val cam = Camara(U0, W0, U1, W1)
        clipPath(rect(U0, W0, U1, W1, 8)) {
            val s = ((reloj * 0.004f + 0.3f) % 1.3f) - 0.15f
            mancha(cam.q(s, 0.2f), 0.05f * cam.k(s.coerceIn(0f, 1f)), 0.014f * cam.k(s.coerceIn(0f, 1f)), lerp(Color.White, img.l.medio, 0.4f + 0.4f * img.l.noche), 0.3f)
            if (img.humo) for (j in 0..4) { val f = ((reloj * 0.05f + j / 5f) % 1f); val ss = ((HUMO_COL + 0.4f) / COLUMNAS - U0) / (U1 - U0) + 0.12f * f * f
                mancha(cam.q(ss + 0.008f * sin(reloj + j), 0.5f - 0.3f * f), (0.008f + 0.02f * f) * cam.k(ss), (0.008f + 0.012f * f) * cam.k(ss), Color(0xFF8A8A88), 0.3f * (1f - f)) }
            if (img.clima == Clima.LLUVIA || img.clima == Clima.TORMENTA) {
                val az = Azar(Semilla.de("laboratorio", "centro", "lluvia"))
                repeat(40) { val uu = az.real(); val f = ((az.real() + reloj * 1.2f) % 1f); val p = cam.q(uu, f); drawLine(Color.White.copy(alpha = 0.28f), p, p + Offset(-0.3f, 2f), 0.35f) }
            }
        }
        // Una pantalla vieja que parpadea.
        val parpadeo = (sin(reloj * 9f) * sin(reloj * 2.3f)).coerceAtLeast(0f)
        if (parpadeo > 0.3f) drawPath(pantalla(COLUMNAS - 1, 2), Color.White.copy(alpha = 0.08f * parpadeo))
    }
}
