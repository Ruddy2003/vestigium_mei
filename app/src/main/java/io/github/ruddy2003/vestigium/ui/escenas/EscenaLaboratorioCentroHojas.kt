package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar

/**
 * Las hojas del verde del centro, una por una (`docs/diseno/ESCENAS.md`, «Vegetación»: masa oscura detrás, hojas una por
 * una, fruta, flores): la hoja de judía de tres folíolos y la de tomatera, compuesta y dentada; cada una con su tamaño, su
 * giro y su tono (oscura por dentro de la mata, media, clara donde le da la luz del vacío, alguna amarillenta) y, de vez en
 * cuando, mordida. Y la fruta: los tomates (verdes, pintones o rojos), las vainas y las flores de la judía.
 */
internal object HojasCentro {
    private val C = ColorLaboratorio

    /** Los tonos de una hoja: 0 lo hondo de la mata, 1 el verde de la hoja, 2 la que da al vacío (clara), 3 la que se seca. */
    fun tono(t: Int): Color = TONOS[t.coerceIn(0, TONOS.size - 1)]
    private val TONOS = listOf(
        lerp(C.HOJA_OSC, C.NEGRO, 0.15f), lerp(C.GRAMA, C.HOJA_OSC, 0.3f), lerp(C.GRAMA, C.PAJA, 0.18f), lerp(C.GRAMA, C.AMARILLO, 0.5f),
    )

    /** Un tono al azar para una hoja de la mata: más oscuras detrás ([delante] falso), más claras delante. */
    fun tonoAlAzar(az: Azar, delante: Boolean): Int { val r = az.real(); return if (!delante) (if (r < 0.7f) 0 else 1) else if (r < 0.08f) 3 else if (r < 0.5f) 2 else 1 }

    /** Un folíolo: una hoja ovalada que acaba en punta, centrada en [c], de largo 2 × [r]; [ancho] 1 normal, menos más estrecha. */
    fun foliolo(c: Offset, r: Float, ancho: Float = 1f): Path = Path().apply {
        val a = r * ancho
        moveTo(c.x, c.y + r)
        cubicTo(c.x - a * 0.95f, c.y + r * 0.55f, c.x - a * 0.75f, c.y - r * 0.7f, c.x, c.y - r * 1.15f)
        cubicTo(c.x + a * 0.75f, c.y - r * 0.7f, c.x + a * 0.95f, c.y + r * 0.55f, c.x, c.y + r)
        close()
    }

    /**
     * Una hoja de judía en [w]: tres folíolos en punta desde el rabillo, girada [angulo] grados, de tamaño [tam] (1 ≈ 9 cm),
     * con el tono [t] y la luz del sitio ([n], hacia dónde mira). [mordida]: un folíolo con un bocado.
     */
    fun DrawScope.hojaJudia(v: VistaCentro, w: V3, angulo: Float, tam: Float, t: Int, n: V3, az: Azar, mordida: Boolean = false) {
        val q = CamCentro.p(w); val k = CamCentro.k(w) * 0.085f * tam
        if (k < 0.35f) { drawCircle(v.c(w, tono(t), n), (k * 1.6f).coerceAtLeast(0.5f), q); return }
        val col = v.c(w, tono(t), n)
        val nervio = lerp(col, Color.White, 0.18f)
        rotate(angulo, q) {
            for ((i, f) in listOf(Triple(0f, -1.15f, 0f), Triple(-0.85f, -0.35f, -55f), Triple(0.85f, -0.35f, 55f)).withIndex()) {
                val (dx, dy, rot) = f
                val c = q + Offset(dx * k, dy * k)
                val r = k * (if (i == 0) 0.68f else 0.58f) * az.entre(0.85f, 1.12f)
                rotate(rot + az.entre(-12f, 12f), c) {
                    drawPath(foliolo(c, r, az.entre(0.8f, 1.05f)), col)
                    if (mordida && i == 1) drawCircle(v.c(w, tono(0), n).copy(alpha = 0.85f), r * 0.32f, c + Offset(r * 0.55f, -r * 0.1f))
                    if (k > 1.2f) drawLine(nervio.copy(alpha = 0.55f), c + Offset(0f, r * 0.9f), c + Offset(0f, -r * 0.9f), (0.06f * k).coerceAtLeast(0.25f))
                }
            }
            drawLine(lerp(col, C.NEGRO, 0.2f), q, q + Offset(0f, -0.9f * k), (0.08f * k).coerceAtLeast(0.3f), StrokeCap.Round)
        }
    }

    /**
     * Una hoja de tomatera en [w]: un nervio con sus folíolos de dos en dos, dentados, y uno en la punta, girada [angulo]
     * grados, de tamaño [tam] (1 ≈ 15 cm).
     */
    fun DrawScope.hojaTomate(v: VistaCentro, w: V3, angulo: Float, tam: Float, t: Int, n: V3, az: Azar) {
        val q = CamCentro.p(w); val k = CamCentro.k(w) * 0.15f * tam
        if (k < 0.5f) { drawCircle(v.c(w, tono(t), n), (k * 0.9f).coerceAtLeast(0.5f), q); return }
        val col = v.c(w, lerp(tono(t), C.HOJA_OSC, 0.12f), n)
        rotate(angulo, q) {
            drawLine(lerp(col, C.TIERRA, 0.3f), q, q + Offset(0f, -k), (0.05f * k).coerceAtLeast(0.3f), StrokeCap.Round)
            for (i in 0..2) {
                val y = -k * (0.25f + 0.28f * i); val r = k * (0.2f - 0.03f * i) * az.entre(0.85f, 1.15f)
                for (s in listOf(-1f, 1f)) {
                    val c = q + Offset(s * r * 0.95f, y)
                    rotate(s * (60f + az.entre(-10f, 10f)), c) { drawPath(dentado(c, r), col) }
                }
            }
            drawPath(dentado(q + Offset(0f, -k * 1.05f), k * 0.2f), col)
        }
    }

    /** Un folíolo dentado de tomatera: el óvalo con cuatro mellas por lado. */
    private fun dentado(c: Offset, r: Float): Path = Path().apply {
        moveTo(c.x, c.y + r)
        val n = 4
        for (i in 1..n) { val f = i / (n + 1f); val y = c.y + r - 2f * r * f; val a = r * 0.55f * (1f - (f - 0.45f) * (f - 0.45f) * 2.2f)
            lineTo(c.x - a * 1.15f, y + r * 0.08f); lineTo(c.x - a * 0.8f, y - r * 0.08f) }
        lineTo(c.x, c.y - r * 1.1f)
        for (i in n downTo 1) { val f = i / (n + 1f); val y = c.y + r - 2f * r * f; val a = r * 0.55f * (1f - (f - 0.45f) * (f - 0.45f) * 2.2f)
            lineTo(c.x + a * 0.8f, y - r * 0.08f); lineTo(c.x + a * 1.15f, y + r * 0.08f) }
        close()
    }

    /**
     * Un tomate en [w], de radio [r] metros: [madurez] 0 verde, 1 rojo (los de en medio, pintones), con su brillo del lado
     * del vacío y el cáliz verde arriba.
     */
    fun DrawScope.tomate(v: VistaCentro, w: V3, r: Float, madurez: Float) {
        val q = CamCentro.p(w); val rr = (r * CamCentro.k(w)).coerceAtLeast(0.6f)
        val propio = lerp(lerp(C.GRAMA, C.AMARILLO, 0.3f), C.TOMATE, madurez)
        drawCircle(lerp(v.c(w, lerp(propio, C.NEGRO, 0.25f), V3(0f, 0f, -1f)), lerp(propio, C.NEGRO, 0.4f), 0.3f), rr, q)
        drawCircle(lerp(v.c(w, propio, V3(0.4f, 0.6f, -1f)), propio, 0.4f), rr * 0.82f, q + Offset(-rr * 0.12f, -rr * 0.12f))
        if (rr > 1.2f) {
            drawCircle(Color.White.copy(alpha = 0.35f), rr * 0.12f, q + Offset(-rr * 0.4f, -rr * 0.4f))
            drawLine(v.c(w, C.HOJA_OSC, V3(0f, 1f, 0f)), q + Offset(-rr * 0.4f, -rr * 0.85f), q + Offset(rr * 0.4f, -rr * 0.85f), (rr * 0.18f).coerceAtLeast(0.3f), StrokeCap.Round)
        }
    }

    /** Una vaina de judía colgando, algo curva. */
    fun DrawScope.vaina(v: VistaCentro, w: V3, az: Azar) {
        val q = CamCentro.p(w); val k = CamCentro.k(w)
        val largo = 0.12f * k; val lado = az.entre(-0.3f, 0.3f) * largo
        drawPath(Path().apply { moveTo(q.x, q.y); cubicTo(q.x + lado, q.y + largo * 0.4f, q.x + lado, q.y + largo * 0.8f, q.x + lado * 0.4f, q.y + largo) },
            v.c(w, lerp(C.GRAMA, C.PAJA, 0.3f), V3(0.5f, 0.3f, 1f)), style = Stroke((0.012f * k).coerceAtLeast(0.6f), cap = StrokeCap.Round))
    }

    /** Una flor de judía, blanca con un toque violeta. */
    fun DrawScope.flor(v: VistaCentro, w: V3) {
        val q = CamCentro.p(w); val k = CamCentro.k(w)
        drawCircle(v.c(w, C.TELA, V3(0.5f, 0.3f, 1f)), (0.014f * k).coerceAtLeast(0.5f), q)
        drawCircle(v.c(w, lerp(C.VIOLETA, C.TELA, 0.4f), V3(0.5f, 0.3f, 1f)), (0.007f * k).coerceAtLeast(0.3f), q + Offset(0.006f * k, -0.004f * k))
    }

    /** Lo hondo de una mata: una mancha oscura que hace de masa detrás de las hojas. */
    fun DrawScope.masa(v: VistaCentro, w: V3, ancho: Float, alto: Float, n: V3) {
        val q = CamCentro.p(w); val k = CamCentro.k(w)
        mancha(q, ancho * k, alto * k, v.c(w, tono(0), n), 0.85f)
    }

    /** Una hoja vista de lejos: un óvalo con su tono y la luz del sitio (para las cascadas y las columnas del otro lado). */
    fun DrawScope.hojaLejos(v: VistaCentro, w: V3, tam: Float, t: Int, n: V3, lejos: Float, az: Azar) = hojaLejos(w, tam, v.c(w, tono(t), n, lejos), az)

    /** Lo mismo con el color ya hecho (para las matas que lo piden una vez por hebra). */
    fun DrawScope.hojaLejos(w: V3, tam: Float, col: Color, az: Azar) {
        val q = CamCentro.p(w); val r = (tam * CamCentro.k(w)).coerceAtLeast(0.45f)
        drawOval(col, q - Offset(r * az.entre(0.8f, 1.2f), r * 0.6f), Size(r * 2f, r * 1.25f))
    }
}
