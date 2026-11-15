package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaDeco.Puerta
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tuboNeon
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * La sala del club: una antigua carbonera de techo bajo, de 5.1 m de ancho y 8 m de fondo desde los ojos, con su caja,
 * su techo, sus paredes y su suelo, el estrado, la escalera de la calle y las ventanas altas. Todo en metros (la cámara
 * es [CamDeco]).
 *
 * **[Diseño propio]**: el cuento dice «un sótano que antes era una carbonera y que ahora tiene paredes pintadas de rojo,
 * mesas pequeñas, una pista del tamaño de un colchón y un piano vertical casi nuevo», con su escalera hacia abajo y su
 * portero; las medidas, el techo de chapa prensada, el friso, el estrado y dónde va cada cosa son míos.
 */
internal object SalaDeco {
    private val C = ColorDeco
    private val FRENTE = V3(0f, 0f, -1f)

    // ---- La caja.
    const val IZQ = -3.4f
    const val DER = 1.7f
    const val FONDO = 8f
    const val TECHO = 2.5f
    /** El techo acaba en la viga de x = [POZO]: a su derecha está el hueco de la escalera, que sube hasta la calle. */
    const val POZO = 0.5f
    const val POZO_ALTO = 4.8f
    const val ZOCALO = 0.95f
    const val FRISO_Y0 = 2.0f
    const val FRISO_Y1 = 2.36f

    // ---- El estrado, a lo largo de la pared de la izquierda.
    const val ESTRADO_X = -1.9f
    const val ESTRADO_Z0 = 4.5f
    const val ESTRADO_ALTO = 0.26f

    // ---- La pista, «del tamaño de un colchón» (algo más).
    const val PISTA_X0 = -1.8f
    const val PISTA_X1 = -0.3f
    const val PISTA_Z0 = 3.9f
    const val PISTA_Z1 = 6.3f

    // ---- La escalera de la calle, contra la pared derecha, y su puerta arriba.
    const val ESC_X0 = 0.62f
    const val ESC_Z0 = 5.4f
    const val ESC_Z1 = 7.95f
    const val ESC_ALTO = 2f
    const val PELDANOS = 11
    const val PUERTA_X0 = 0.8f
    const val PUERTA_X1 = 1.5f
    const val PUERTA_Y1 = 4.05f

    // ---- Las ventanas altas de la pared derecha, a ras de acera: de [VY0] a [VY1] de alto.
    val VENTANAS = listOf(4.0f to 4.85f, 5.45f to 6.3f)
    const val VY0 = 1.48f
    const val VY1 = 2.4f
    /** El fondo del muro: el vidrio está a esta distancia de la cara de dentro. */
    const val MURO = 0.12f

    // ---- La barra, contra la pared del fondo, y la puerta del camerino.
    const val BARRA_X0 = -1.75f
    const val BARRA_X1 = -0.4f
    const val BARRA_Z = 7.42f
    const val BARRA_ALTO = 1.04f
    const val CAMERINO_X0 = -0.22f
    const val CAMERINO_X1 = 0.34f

    /** Dónde cuelgan las dos lámparas déco (la cercana, que hace de marco, y su pareja del fondo) y los dos apliques. */
    val LAMPARA_CERCA = V3(-0.72f, TECHO, 2.45f)
    val LAMPARA_FONDO = V3(-0.95f, TECHO, 5.9f)
    val APLIQUES = listOf(V3(IZQ + 0.02f, 1.72f, 4.7f), V3(IZQ + 0.02f, 1.72f, 6.7f))
    /** La lámpara de la barra, colgada sobre el mostrador. */
    val LAMPARA_BARRA = V3(-1.05f, TECHO, 7.6f)
    /** La bombilla de guardia, enjaulada, en la pared del hueco de la escalera. */
    val GUARDIA = V3(DER - 0.04f, 2.95f, 6.9f)

    /** El cuadro del suelo en la sala: de los ojos al fondo. */
    private const val CERCA = 2.3f

    private fun p(x: Float, y: Float, z: Float) = CamDeco.p(x, y, z)
    private fun forma(vararg ps: Float): Path { val l = ArrayList<Offset>(); var i = 0; while (i < ps.size) { l += p(ps[i], ps[i + 1], ps[i + 2]); i += 3 }; return poligono(l) }

    /** La forma del suelo en el lienzo (sin el estrado). */
    fun formaDelSuelo(): Path = forma(IZQ, 0f, CERCA, DER, 0f, CERCA, DER, 0f, FONDO, IZQ, 0f, FONDO)
    fun formaParedFondo(): Path = forma(IZQ, 0f, FONDO, DER, 0f, FONDO, DER, POZO_ALTO, FONDO, POZO, POZO_ALTO, FONDO, POZO, TECHO, FONDO, IZQ, TECHO, FONDO)
    fun formaParedIzquierda(): Path = forma(IZQ, 0f, CERCA, IZQ, 0f, FONDO, IZQ, TECHO, FONDO, IZQ, TECHO, CERCA)
    fun formaParedDerecha(): Path = forma(DER, 0f, CERCA, DER, 0f, FONDO, DER, POZO_ALTO, FONDO, DER, POZO_ALTO, CERCA)
    fun formaDelTecho(): Path = forma(IZQ, TECHO, 1.4f, POZO, TECHO, 1.4f, POZO, TECHO, FONDO, IZQ, TECHO, FONDO)

    // =================================================================
    //  El techo
    // =================================================================

    /**
     * El techo bajo de la antigua carbonera: chapa prensada pintada de oscuro entre vigas que van hacia el fondo, el
     * hollín sobre las lámparas, la viga gruesa del hueco de la escalera, y la tubería de la calefacción que lo cruza en
     * diagonal con sus abrazaderas, su junta y su llave.
     */
    fun DrawScope.techo(v: VistaDeco) {
        val chapa = lerp(lerp(C.MARFIL, C.ORO, 0.4f), C.CAOBA, 0.42f); val abajo = V3(0f, -1f, 0f)
        drawPath(formaDelTecho(), Brush.verticalGradient(0f to v.s(V3(-1f, TECHO, 2.6f), chapa, abajo), 1f to v.s(V3(-1f, TECHO, 7.5f), chapa, abajo), startY = 0f, endY = 77f))
        clipPath(formaDelTecho()) {
            // Las placas de chapa: una cuadrícula con su florón en relieve, que coge un poco de luz.
            var z = 2.4f
            while (z < FONDO) {
                var x = IZQ; while (x < POZO) {
                    val c = p(x + 0.3f, TECHO, z + 0.3f); val k = CamDeco.k(z + 0.3f)
                    drawOval(v.s(V3(x + 0.3f, TECHO, z + 0.3f), lerp(chapa, C.MARFIL, 0.3f), abajo).copy(alpha = 0.4f), c - Offset(0.1f * k, 0.018f * k), Size(0.2f * k, 0.036f * k), style = Stroke(0.35f))
                    x += 0.6f }
                drawLine(v.s(V3(-1f, TECHO, z), lerp(chapa, C.LACA, 0.5f), abajo).copy(alpha = 0.7f), p(IZQ, TECHO, z), p(POZO, TECHO, z), 0.5f)
                z += 0.6f
            }
            var x = IZQ + 0.6f; while (x < POZO) { drawLine(v.s(V3(x, TECHO, 5f), lerp(chapa, C.LACA, 0.5f), abajo).copy(alpha = 0.7f), p(x, TECHO, 2.2f), p(x, TECHO, FONDO), 0.5f); x += 0.6f }
            // El hollín de años de humo encima de las lámparas y de la barra.
            for (l in listOf(LAMPARA_FONDO, LAMPARA_BARRA, V3(-2.4f, TECHO, 6.4f))) mancha(p(l.x, TECHO, l.z), 0.9f * CamDeco.k(l.z), 0.25f * CamDeco.k(l.z), lerp(C.LACA, C.CAOBA, 0.3f), 0.35f)
        }
        // Las vigas: la cara de abajo y la que mira a quien mira (la de la derecha, en las de la izquierda del eje).
        for (x in listOf(-2.75f, -1.75f, -0.75f)) viga(v, x, 0.14f, 0.16f, lerp(C.CAOBA, C.LACA, 0.35f))
        viga(v, POZO - 0.1f, 0.2f, 0.24f, lerp(C.CAOBA, C.LACA, 0.25f))
        // Las vigas que van de pared a pared, por encima de las de arriba: la del fondo, junto a la pared.
        val travesano = forma(IZQ, TECHO - 0.16f, FONDO - 0.15f, POZO, TECHO - 0.16f, FONDO - 0.15f, POZO, TECHO, FONDO - 0.15f, IZQ, TECHO, FONDO - 0.15f)
        drawPath(travesano, v.s(V3(-1.5f, TECHO, FONDO), lerp(C.CAOBA, C.LACA, 0.3f), FRENTE))
        tuberia(v)
    }

    /** Una viga que va de los ojos al fondo, de [ancho] metros y [canto] de alto. */
    private fun DrawScope.viga(v: VistaDeco, x: Float, ancho: Float, canto: Float, madera: Color) {
        val y = TECHO - canto; val x0 = x - ancho / 2f; val x1 = x + ancho / 2f
        val ladoX = if (x < 0f) x1 else x0; val n = V3(if (x < 0f) 1f else -1f, 0f, 0f)
        drawPath(forma(ladoX, y, 1.6f, ladoX, y, FONDO, ladoX, TECHO, FONDO, ladoX, TECHO, 1.6f), Brush.verticalGradient(0f to v.s(V3(ladoX, y, 2.6f), madera, n), 1f to v.s(V3(ladoX, y, 7f), madera, n), startY = 0f, endY = 80f))
        drawPath(forma(x0, y, 1.6f, x1, y, 1.6f, x1, y, FONDO, x0, y, FONDO), Brush.verticalGradient(0f to v.s(V3(x, y, 2.6f), lerp(madera, C.LACA, 0.25f), V3(0f, -1f, 0f)), 1f to v.s(V3(x, y, 7f), lerp(madera, C.LACA, 0.25f), V3(0f, -1f, 0f)), startY = 0f, endY = 80f))
        // El canto de abajo coge la luz, y las vetas.
        drawLine(v.s(V3(ladoX, y, 4f), lerp(madera, C.MARFIL, 0.35f), n).copy(alpha = 0.6f), p(ladoX, y, 1.6f), p(ladoX, y, FONDO), 0.6f)
        val az = Azar(Semilla.de("deco", "viga", x))
        repeat(4) { val yy = y + az.entre(0.02f, canto - 0.02f); drawLine(v.s(V3(ladoX, yy, 4f), lerp(madera, C.LACA, 0.4f), n).copy(alpha = 0.4f), p(ladoX, yy, az.entre(1.6f, 3f)), p(ladoX, yy, az.entre(4f, FONDO)), 0.4f) }
    }

    /** La tubería de la calefacción, de la pared de la izquierda a la de la derecha, en diagonal bajo el techo. */
    private fun DrawScope.tuberia(v: VistaDeco) {
        val a = V3(-2.6f, TECHO - 0.24f, 2.5f); val b = V3(DER, TECHO - 0.24f, 6.6f); val r = 0.05f
        val n = 20
        val puntos = (0..n).map { i -> val t = i / n.toFloat(); V3(a.x + (b.x - a.x) * t, a.y, a.z + (b.z - a.z) * t) }
        // Su sombra en el techo, de las lámparas de abajo: un trazo difuso por encima.
        for (i in 0 until n) { val q0 = puntos[i]; val q1 = puntos[i + 1]; if (q0.x > POZO) break
            drawLine(C.SOMBRA.copy(alpha = 0.3f), p(q0.x, TECHO, q0.z + 0.12f), p(q1.x, TECHO, q1.z + 0.12f), 2.2f * r * CamDeco.k(q0.z) * 2f, blendMode = BlendMode.Multiply) }
        for (i in 0 until n) {
            val q0 = puntos[i]; val q1 = puntos[i + 1]; val k = CamDeco.k((q0.z + q1.z) / 2f); val g = 2 * r * k
            val m = V3((q0.x + q1.x) / 2f, q0.y, (q0.z + q1.z) / 2f)
            drawLine(v.s(m, C.HIERRO, V3(0f, -0.5f, -1f)), p(q0.x, q0.y, q0.z), p(q1.x, q1.y, q1.z), g + 0.4f, StrokeCap.Butt)
            // El filo de abajo, que recibe la luz de la sala, y el de arriba, en sombra.
            drawLine(v.s(m, lerp(C.HIERRO, C.MARFIL, 0.4f), V3(0f, -1f, -0.4f)).copy(alpha = 0.75f), p(q0.x, q0.y - r * 0.55f, q0.z), p(q1.x, q1.y - r * 0.55f, q1.z), g * 0.22f, StrokeCap.Butt)
        }
        // Las abrazaderas que la cuelgan del techo, una junta con sus tornillos y la llave de paso.
        for (t in listOf(0.18f, 0.42f, 0.66f, 0.9f)) { val q = V3(a.x + (b.x - a.x) * t, a.y, a.z + (b.z - a.z) * t); if (q.x > POZO + 0.1f) continue
            drawLine(v.s(q, C.HIERRO, FRENTE), p(q.x, TECHO, q.z), p(q.x, q.y + r, q.z), 0.012f * CamDeco.k(q.z)) }
        val j = V3(a.x + (b.x - a.x) * 0.3f, a.y, a.z + (b.z - a.z) * 0.3f); val kj = CamDeco.k(j.z)
        drawLine(v.s(j, lerp(C.HIERRO, C.ORO, 0.3f), FRENTE), p(j.x - 0.03f, j.y - 0.08f, j.z - 0.02f), p(j.x + 0.03f, j.y + 0.08f, j.z + 0.02f), 0.06f * kj)
        val llave = V3(a.x + (b.x - a.x) * 0.55f, a.y - r, a.z + (b.z - a.z) * 0.55f)
        drawLine(v.s(llave, C.HIERRO, FRENTE), p(llave.x, llave.y, llave.z), p(llave.x, llave.y - 0.08f, llave.z), 0.02f * CamDeco.k(llave.z))
        drawOval(v.s(llave, C.CORAL, V3(0f, -1f, -0.5f)), p(llave.x, llave.y - 0.08f, llave.z) - Offset(0.06f * CamDeco.k(llave.z), 0.015f * CamDeco.k(llave.z)), Size(0.12f * CamDeco.k(llave.z), 0.03f * CamDeco.k(llave.z)), style = Stroke(0.012f * CamDeco.k(llave.z)))
        // Al llegar a la pared derecha baja por ella (el codo).
        drawLine(v.s(b, C.HIERRO, V3(-1f, 0f, -0.5f)), p(b.x - 0.05f, b.y, b.z), p(b.x - 0.05f, 1.2f, b.z), 2 * r * CamDeco.k(b.z))
    }

    // =================================================================
    //  Las paredes
    // =================================================================

    /** Las tres paredes: rojas, con su zócalo de madera, su friso y su cornisa; en el hueco de la escalera, ladrillo. */
    fun DrawScope.paredes(v: VistaDeco) {
        val pared = if (v.nuevo) C.PARED_NUEVA else C.PARED
        val izq = V3(1f, 0f, 0f); val der = V3(-1f, 0f, 0f)
        // ---- La pared de la izquierda.
        val pi = formaParedIzquierda(); val z0 = 4.1f
        drawPath(pi, Brush.horizontalGradient(0f to v.s(V3(IZQ, 1.4f, 4.2f), pared, izq), 0.5f to v.s(V3(IZQ, 1.4f, 5.5f), pared, izq), 1f to v.s(V3(IZQ, 1.4f, FONDO), pared, izq), startX = 0f, endX = 119f))
        // ---- La pared del fondo (con el hueco de la escalera, que sigue hacia arriba en ladrillo).
        val pf = formaParedFondo()
        drawPath(pf, Brush.horizontalGradient(0f to v.s(V3(IZQ, 1.4f, FONDO), pared, FRENTE), 0.5f to v.s(V3(-1f, 1.4f, FONDO), pared, FRENTE), 1f to v.s(V3(POZO, 1.4f, FONDO), pared, FRENTE), startX = 118f, endX = 265f))
        // ---- La pared derecha (con las ventanas) y el hueco: ladrillo encalado.
        val pd = formaParedDerecha()
        drawPath(pd, Brush.horizontalGradient(0f to v.s(V3(DER, 1.6f, FONDO), pared, der), 1f to v.s(V3(DER, 1.6f, 3.8f), pared, der), startX = 310f, endX = 380f))
        ladrilloDelHueco(v)
        // Las manchas de humedad y los roces del enlucido viejo.
        val az = Azar(Semilla.de("deco", "paredes"))
        clipPath(pi) { repeat(10) { val w = V3(IZQ, az.entre(0.2f, 2.4f), az.entre(4f, FONDO)); mancha(p(w.x, w.y, w.z), az.entre(4f, 12f), az.entre(6f, 16f), v.s(w, lerp(pared, C.LACA, 0.5f), izq), 0.16f) } }
        clipPath(pf) { repeat(10) { val w = V3(az.entre(IZQ, POZO), az.entre(0.2f, 2.4f), FONDO); mancha(p(w.x, w.y, w.z), az.entre(3f, 8f), az.entre(4f, 10f), v.s(w, lerp(pared, C.LACA, 0.5f), FRENTE), 0.16f) }
            // Grietas finas que bajan del techo.
            for (x0 in listOf(-2.6f, -0.1f)) { var q = p(x0, TECHO, FONDO); repeat(6) { val r = q + Offset(az.entre(-1.5f, 1.5f), az.entre(2.5f, 5f)); drawLine(v.s(V3(x0, 2f, FONDO), C.LACA, FRENTE).copy(alpha = 0.4f), q, r, 0.35f); q = r } } }
        // ---- El zócalo de madera, con sus paneles, en las tres paredes.
        zocalo(v, pared)
        // ---- El friso déco pintado: en la pared de la izquierda y en la del fondo, hasta el hueco.
        friso(v, true); friso(v, false)
        // ---- La cornisa de arriba: un junquillo dorado bajo el techo.
        drawLine(v.s(V3(IZQ, TECHO, 6f), C.ORO, izq), p(IZQ, TECHO - 0.06f, CERCA), p(IZQ, TECHO - 0.06f, FONDO), 0.9f)
        drawLine(v.s(V3(-1f, TECHO, FONDO), C.ORO, FRENTE), p(IZQ, TECHO - 0.06f, FONDO), p(POZO, TECHO - 0.06f, FONDO), 0.7f)
        // ---- Lo que hay en las paredes.
        cartel(v)
        reloj(v)
        camerino(v)
        trasBarra(v)
        for (ap in APLIQUES) with(PiezasDeco) { aplique(v, ap, v.enc.deco) }
        // La penumbra de los rincones, que da hondura a la caja.
        val sombra = C.SOMBRA.copy(alpha = 0.3f)
        clipPath(pf) { drawRect(Brush.horizontalGradient(0f to sombra, 1f to Color.Transparent, startX = 118f, endX = 134f), Offset(118f, 70f), Size(16f, 110f), blendMode = BlendMode.Multiply) }
        clipPath(pi) { drawRect(Brush.verticalGradient(0f to sombra, 1f to Color.Transparent, startY = 40f, endY = 90f), Offset(0f, 30f), Size(120f, 60f), blendMode = BlendMode.Multiply) }
    }

    /** El hueco de la escalera: ladrillo encalado a trozos, con el hollín de la bombilla. */
    private fun DrawScope.ladrilloDelHueco(v: VistaDeco) {
        val der = V3(-1f, 0f, 0f)
        val hueco = forma(DER, TECHO, 3f, DER, TECHO, FONDO, DER, POZO_ALTO, FONDO, DER, POZO_ALTO, 3f)
        val fondo = forma(POZO, TECHO, FONDO, DER, TECHO, FONDO, DER, POZO_ALTO, FONDO, POZO, POZO_ALTO, FONDO)
        val ladrillo = lerp(C.LADRILLO, C.PARED, 0.2f)
        drawPath(hueco, Brush.horizontalGradient(0f to v.s(V3(DER, 3.2f, FONDO), ladrillo, der), 1f to v.s(V3(DER, 3.2f, 4f), ladrillo, der), startX = 310f, endX = 380f))
        drawPath(fondo, v.s(V3(1.1f, 3f, FONDO), ladrillo, FRENTE))
        val az = Azar(Semilla.de("deco", "ladrillo"))
        clipPath(hueco) {
            var y = TECHO; var fila = 0
            while (y < POZO_ALTO) { drawLine(v.s(V3(DER, y, 5f), lerp(ladrillo, C.MARFIL, 0.4f), der).copy(alpha = 0.5f), p(DER, y, 3f), p(DER, y, FONDO), 0.4f)
                var z = 3f + (fila % 2) * 0.11f; while (z < FONDO) { drawLine(v.s(V3(DER, y, z), lerp(ladrillo, C.MARFIL, 0.4f), der).copy(alpha = 0.4f), p(DER, y, z), p(DER, y + 0.075f, z), 0.35f); z += 0.22f }
                y += 0.075f; fila++ }
            // La cal, que se cae a trozos.
            repeat(6) { val w = V3(DER, az.entre(2.6f, 4.4f), az.entre(4f, 7.8f)); mancha(p(w.x, w.y, w.z), az.entre(6f, 14f), az.entre(5f, 10f), v.s(w, lerp(C.MARFIL, C.ACERA, 0.3f), der), 0.5f) }
        }
        clipPath(fondo) { var y = TECHO; while (y < POZO_ALTO) { drawLine(v.s(V3(1f, y, FONDO), lerp(ladrillo, C.MARFIL, 0.4f), FRENTE).copy(alpha = 0.45f), p(POZO, y, FONDO), p(DER, y, FONDO), 0.35f); y += 0.075f } }
        // El hollín sobre la bombilla de guardia.
        mancha(p(GUARDIA.x, GUARDIA.y + 0.35f, GUARDIA.z), 10f, 18f, lerp(C.LACA, C.CAOBA, 0.3f), 0.3f)
    }

    /** El zócalo de madera oscura, de un metro, con paneles moldurados y el roce de las sillas. */
    private fun DrawScope.zocalo(v: VistaDeco, pared: Color) {
        val madera = lerp(C.CAOBA, C.LACA, 0.3f); val izq = V3(1f, 0f, 0f)
        val zi = forma(IZQ, 0f, CERCA, IZQ, 0f, FONDO, IZQ, ZOCALO, FONDO, IZQ, ZOCALO, CERCA)
        drawPath(zi, Brush.horizontalGradient(0f to v.s(V3(IZQ, 0.5f, 4.2f), madera, izq), 1f to v.s(V3(IZQ, 0.5f, FONDO), madera, izq), startX = 0f, endX = 119f))
        val zf = forma(IZQ, 0f, FONDO, ESC_X0, 0f, FONDO, ESC_X0, ZOCALO, FONDO, IZQ, ZOCALO, FONDO)
        drawPath(zf, v.s(V3(-1.4f, 0.5f, FONDO), madera, FRENTE))
        val zd = forma(DER, 0f, CERCA, DER, 0f, FONDO, DER, ZOCALO, FONDO, DER, ZOCALO, CERCA)
        drawPath(zd, v.s(V3(DER, 0.5f, 4.5f), madera, V3(-1f, 0f, 0f)))
        // Los paneles: rectángulos moldurados, con su filo de luz arriba.
        var z = 4f; while (z < FONDO - 0.3f) { drawPath(forma(IZQ, 0.12f, z, IZQ, 0.12f, z + 0.6f, IZQ, ZOCALO - 0.1f, z + 0.6f, IZQ, ZOCALO - 0.1f, z), v.s(V3(IZQ, 0.5f, z), lerp(madera, C.ORO, 0.25f), izq).copy(alpha = 0.6f), style = Stroke(0.6f)); z += 0.75f }
        var x = IZQ + 0.1f; while (x < ESC_X0 - 0.5f) { drawPath(forma(x, 0.12f, FONDO, x + 0.5f, 0.12f, FONDO, x + 0.5f, ZOCALO - 0.1f, FONDO, x, ZOCALO - 0.1f, FONDO), v.s(V3(x, 0.5f, FONDO), lerp(madera, C.ORO, 0.25f), FRENTE).copy(alpha = 0.55f), style = Stroke(0.45f)); x += 0.62f }
        // La moldura de arriba, dorada.
        drawLine(v.s(V3(IZQ, ZOCALO, 6f), C.ORO, izq), p(IZQ, ZOCALO, CERCA), p(IZQ, ZOCALO, FONDO), 1f)
        drawLine(v.s(V3(-1f, ZOCALO, FONDO), C.ORO, FRENTE), p(IZQ, ZOCALO, FONDO), p(ESC_X0, ZOCALO, FONDO), 0.7f)
        drawLine(v.s(V3(DER, ZOCALO, 4f), C.ORO, V3(-1f, 0f, 0f)), p(DER, ZOCALO, CERCA), p(DER, ZOCALO, ESC_Z0), 1f)
        // El roce de los respaldos en la pared, a la altura de las sillas.
        val az = Azar(Semilla.de("deco", "roces"))
        repeat(12) { val w = V3(az.entre(IZQ, ESC_X0), az.entre(0.92f, 1.05f), FONDO); drawLine(v.s(w, lerp(pared, C.LACA, 0.4f), FRENTE).copy(alpha = 0.35f), p(w.x, w.y, w.z), p(w.x + az.entre(0.05f, 0.2f), w.y + az.entre(-0.02f, 0.02f), w.z), 0.5f) }
    }

    /**
     * El friso déco pintado a lo largo de la pared ([izquierda] o la del fondo): sobre fondo de laca, abanicos de rayos de
     * oro, zigzags y escalones, con toques de jade y de coral. Es el «fresco» del tema: la luz que cae en él lo enciende
     * (los colores suben con el sol y con los apliques) y en la penumbra queda apagado.
     */
    private fun DrawScope.friso(v: VistaDeco, izquierda: Boolean) {
        val n = if (izquierda) V3(1f, 0f, 0f) else FRENTE
        val y0 = FRISO_Y0; val y1 = FRISO_Y1; val ym = (y0 + y1) / 2f
        fun q(s: Float, y: Float): V3 = if (izquierda) V3(IZQ + 0.001f, y, s) else V3(s, y, FONDO - 0.001f)
        val s0 = if (izquierda) CERCA + 1.5f else IZQ; val s1 = if (izquierda) FONDO else POZO
        val banda = CamDeco.forma(listOf(q(s0, y0), q(s1, y0), q(s1, y1), q(s0, y1)))
        drawPath(banda, Brush.linearGradient(0f to v.s(q(s0, ym), C.LACA, n), 1f to v.s(q(s1, ym), C.LACA, n), start = CamDeco.p(q(s0, ym)), end = CamDeco.p(q(s1, ym))))
        clipPath(banda) {
            val paso = 0.62f; var s = s0 + 0.1f; var i = 0
            while (s < s1) {
                val c = q(s + paso / 2f, y0 + 0.02f)
                // El abanico: rayos de oro que se abren desde abajo, con un sol de coral en el centro.
                for (r in 0..8) { val a = (PI * (r + 0.5f) / 9).toFloat(); val punta = q(s + paso / 2f - 0.26f * cos(a), y0 + 0.02f + 0.3f * sin(a))
                    drawLine(v.s(punta, if (r % 2 == 0) C.ORO else lerp(C.ORO, C.MARFIL, 0.3f), n), CamDeco.p(c), CamDeco.p(punta), 0.5f) }
                val sol = if (izquierda) V3(IZQ + 0.001f, y0 + 0.02f, s + paso / 2f) else V3(s + paso / 2f, y0 + 0.02f, FONDO - 0.001f)
                val pc = CamDeco.p(sol); val kk = CamDeco.k(sol.z)
                drawArc(v.s(sol, if (i % 2 == 0) C.CORAL else C.JADE, n), 180f, 180f, true, pc - Offset(0.07f * kk * (if (izquierda) 0.5f else 1f), 0.07f * kk), Size(0.14f * kk * (if (izquierda) 0.5f else 1f), 0.14f * kk))
                // Entre abanico y abanico, un escalonado de jade y el zigzag.
                val e = s + paso
                for (k in 0..2) { val h = 0.08f + k * 0.06f; val w = 0.06f - k * 0.015f
                    drawPath(CamDeco.forma(listOf(q(e - w, y0), q(e + w, y0), q(e + w, y0 + h), q(e - w, y0 + h))), v.s(q(e, y0 + h), if (k == 1) C.JADE else lerp(C.JADE, C.ORO, 0.4f), n)) }
                i++; s += paso
            }
            // Los dos filetes de oro, arriba y abajo, y el zigzag de arriba.
            val zz = ArrayList<Offset>(); var t = s0; var arriba = true
            while (t < s1) { zz += CamDeco.p(q(t, if (arriba) y1 - 0.015f else y1 - 0.055f)); t += 0.07f; arriba = !arriba }
            for (k in 0 until zz.size - 1) drawLine(v.s(q(s0, y1), C.ORO, n), zz[k], zz[k + 1], 0.45f)
        }
        for (y in listOf(y0, y1)) drawLine(v.s(q(s0, y), C.ORO, n), CamDeco.p(q(s0, y)), CamDeco.p(q(s1, y)), 0.7f)
    }

    /**
     * Un cartel enmarcado en la pared de la izquierda, sobre el estrado: una copa de cóctel y una nota dentro de un abanico,
     * sin letras (otra imagen dentro de la imagen, que se enciende con los apliques).
     */
    private fun DrawScope.cartel(v: VistaDeco) {
        val z0 = 5.45f; val z1 = 6.15f; val y0 = 1.02f; val y1 = 1.58f; val n = V3(1f, 0f, 0f)
        fun q(z: Float, y: Float) = V3(IZQ + 0.02f, y, z)
        val marco = CamDeco.forma(listOf(q(z0 - 0.04f, y0 - 0.04f), q(z1 + 0.04f, y0 - 0.04f), q(z1 + 0.04f, y1 + 0.04f), q(z0 - 0.04f, y1 + 0.04f)))
        drawPath(marco, v.s(q((z0 + z1) / 2f, y0), C.ORO, n))
        val lam = CamDeco.forma(listOf(q(z0, y0), q(z1, y0), q(z1, y1), q(z0, y1)))
        drawPath(lam, v.s(q((z0 + z1) / 2f, (y0 + y1) / 2f), lerp(C.JADE, C.LACA, 0.3f), n))
        clipPath(lam) {
            val c = q((z0 + z1) / 2f, y0 + 0.08f)
            for (r in 0..10) { val a = (PI * (r + 0.5f) / 11).toFloat(); drawLine(v.s(c, if (r % 2 == 0) C.ORO else C.CORAL, n), CamDeco.p(c), CamDeco.p(q((z0 + z1) / 2f - 0.5f * cos(a), y0 + 0.08f + 0.6f * sin(a))), 0.6f) }
            // La copa de cóctel, en marfil.
            val m = (z0 + z1) / 2f
            drawPath(CamDeco.forma(listOf(q(m - 0.14f, 1.42f), q(m + 0.14f, 1.42f), q(m + 0.01f, 1.27f), q(m - 0.01f, 1.27f))), v.s(q(m, 1.35f), C.MARFIL, n))
            drawLine(v.s(q(m, 1.2f), C.MARFIL, n), CamDeco.p(q(m, 1.27f)), CamDeco.p(q(m, 1.12f)), 0.6f)
            drawLine(v.s(q(m, 1.12f), C.MARFIL, n), CamDeco.p(q(m - 0.07f, 1.12f)), CamDeco.p(q(m + 0.07f, 1.12f)), 0.6f)
            drawCircle(v.s(q(m + 0.1f, 1.48f), C.CORAL, n), 0.03f * CamDeco.k(m), CamDeco.p(q(m + 0.1f, 1.47f)))
        }
        drawPath(lam, C.LACA.copy(alpha = 0.5f), style = Stroke(0.4f))
    }

    /**
     * El reloj de pared de la derecha, entre las dos ventanas: esfera de marfil con su aro de latón y doce rayas, sin
     * cifras; las agujas marcan la hora del cuadro (la del club se mira para saber cuándo empieza el pase).
     */
    private fun DrawScope.reloj(v: VistaDeco) {
        val c = V3(DER - 0.005f, 1.12f, 5.15f); val r = 0.17f; val n = V3(-1f, 0f, 0f)
        fun q(a: Float, rr: Float) = p(c.x, c.y + rr * cos(a), c.z + rr * sin(a))
        val aro = (0..24).map { q((2 * PI * it / 24).toFloat(), r + 0.03f) }
        val esfera = (0..24).map { q((2 * PI * it / 24).toFloat(), r) }
        drawPath(poligono(aro), v.s(c, C.ORO, n)); drawPath(poligono(esfera), v.s(c, C.MARFIL, n))
        for (h in 0 until 12) { val a = (2 * PI * h / 12).toFloat(); drawLine(v.s(c, C.LACA, n), q(a, r * 0.78f), q(a, r * 0.92f), if (h % 3 == 0) 0.8f else 0.4f) }
        val hora = v.m.hora.toFloat()
        val aH = (2 * PI * ((hora % 12f) / 12f)).toFloat(); val aM = (2 * PI * (hora % 1f)).toFloat()
        drawLine(v.s(c, C.LACA, n), p(c.x, c.y, c.z), q(aH, r * 0.5f), 0.9f, StrokeCap.Round)
        drawLine(v.s(c, C.LACA, n), p(c.x, c.y, c.z), q(aM, r * 0.78f), 0.55f, StrokeCap.Round)
        drawCircle(v.s(c, C.CORAL, n), 0.5f, p(c.x, c.y, c.z))
    }

    /** La puerta del camerino, en la pared del fondo: un vano con su cortina de terciopelo a medio correr y lo oscuro detrás. */
    private fun DrawScope.camerino(v: VistaDeco) {
        val x0 = CAMERINO_X0; val x1 = CAMERINO_X1; val y1 = 1.98f
        drawPath(forma(x0, 0f, FONDO, x1, 0f, FONDO, x1, y1, FONDO, x0, y1, FONDO), v.s(V3((x0 + x1) / 2f, 1f, FONDO + 0.6f), lerp(C.LACA, C.PANO, 0.3f), FRENTE))
        // Una rendija de luz del pasillo de atrás, y la jamba que se ve de lado.
        drawPath(forma(x1 - 0.02f, 0f, FONDO, x1, 0f, FONDO, x1, y1, FONDO, x1 - 0.02f, y1, FONDO), v.s(V3(x1, 1f, FONDO), lerp(C.PARED, C.LACA, 0.4f), V3(-1f, 0f, 0f)))
        // La cortina: cae del dintel, recogida a un lado con un cordón dorado.
        val tela = lerp(C.JADE, C.LACA, 0.25f)
        val cortina = Path().apply {
            val a = p(x0 - 0.02f, y1 + 0.02f, FONDO - 0.02f); val b = p(x1 - 0.12f, y1 + 0.02f, FONDO - 0.02f); val c = p(x0 + 0.16f, 1.05f, FONDO - 0.02f); val d = p(x0 + 0.1f, 0f, FONDO - 0.02f); val e = p(x0 - 0.02f, 0f, FONDO - 0.02f)
            moveTo(a.x, a.y); lineTo(b.x, b.y); cubicTo(b.x - 2f, b.y + 18f, c.x + 5f, c.y - 6f, c.x, c.y); cubicTo(c.x - 2f, c.y + 6f, d.x + 1f, d.y - 8f, d.x, d.y); lineTo(e.x, e.y); close()
        }
        drawPath(cortina, Brush.horizontalGradient(0f to v.s(V3(x0, 1f, FONDO), tela, FRENTE), 0.6f to v.s(V3(x0 + 0.1f, 1f, FONDO), lerp(tela, C.MARFIL, 0.15f), V3(0.6f, 0f, -1f)), 1f to v.s(V3(x1, 1f, FONDO), lerp(tela, C.LACA, 0.4f), FRENTE), startX = p(x0, 1f, FONDO).x, endX = p(x1 - 0.1f, 1f, FONDO).x))
        clipPath(cortina) { for (i in 0..3) { val x = x0 + 0.02f + i * 0.06f; drawLine(C.SOMBRA.copy(alpha = 0.35f), p(x, y1, FONDO), p(x + 0.02f, 0f, FONDO), 0.9f, blendMode = BlendMode.Multiply) } }
        val cordon = p(x0 + 0.15f, 1.05f, FONDO - 0.03f); drawCircle(v.s(V3(x0, 1f, FONDO), C.ORO, FRENTE), 1.1f, cordon)
        // El dintel con su moldura en escalón.
        drawPath(forma(x0 - 0.06f, y1, FONDO - 0.01f, x1 + 0.06f, y1, FONDO - 0.01f, x1 + 0.03f, y1 + 0.08f, FONDO - 0.01f, x0 - 0.03f, y1 + 0.08f, FONDO - 0.01f), v.s(V3(0f, y1, FONDO), C.ORO, FRENTE))
    }

    /**
     * Lo de detrás de la barra, en la pared del fondo: el espejo biselado con su remate escalonado, que devuelve las luces
     * de la sala, y a los lados los estantes con las tazas, las teteras y, a medio esconder tras una cortinilla, las
     * botellas sin etiqueta legible.
     */
    private fun DrawScope.trasBarra(v: VistaDeco) {
        val x0 = BARRA_X0; val x1 = BARRA_X1; val y0 = 1.18f; val y1 = 1.98f
        // El espejo: un rectángulo con el remate en escalones, con su bisel claro.
        val xm0 = -1.42f; val xm1 = -0.72f
        val espejo = forma(xm0, y0, FONDO - 0.01f, xm1, y0, FONDO - 0.01f, xm1, y1 - 0.12f, FONDO - 0.01f, xm1 - 0.08f, y1 - 0.12f, FONDO - 0.01f, xm1 - 0.08f, y1 - 0.04f, FONDO - 0.01f, xm1 - 0.18f, y1 - 0.04f, FONDO - 0.01f, xm1 - 0.18f, y1, FONDO - 0.01f,
            xm0 + 0.18f, y1, FONDO - 0.01f, xm0 + 0.18f, y1 - 0.04f, FONDO - 0.01f, xm0 + 0.08f, y1 - 0.04f, FONDO - 0.01f, xm0 + 0.08f, y1 - 0.12f, FONDO - 0.01f, xm0, y1 - 0.12f, FONDO - 0.01f)
        val vidrio = lerp(C.PANO, C.NOCHE, 0.25f)
        drawPath(espejo, Brush.verticalGradient(0f to v.s(V3(-1f, y1, FONDO), lerp(vidrio, C.MARFIL, 0.25f), FRENTE), 1f to v.s(V3(-1f, y0, FONDO), vidrio, FRENTE), startY = p(0f, y1, FONDO).y, endY = p(0f, y0, FONDO).y))
        clipPath(espejo) {
            // Lo que el espejo devuelve: la sala en penumbra, las lámparas encendidas como lunas y el rojo de la pared de enfrente.
            drawRect(v.s(V3(-1f, 1.5f, FONDO), lerp(C.PARED, C.LACA, 0.5f), FRENTE).copy(alpha = 0.35f), p(xm0, y1, FONDO), Size(p(xm1, 0f, FONDO).x - p(xm0, 0f, FONDO).x, 8f))
            if (v.enc.deco > 0.05f) { mancha(p(-0.95f, 1.75f, FONDO), 4f, 3f, C.LAMPARA, 0.7f * v.enc.deco); mancha(p(-1.25f, 1.62f, FONDO), 2.5f, 2f, C.LAMPARA, 0.5f * v.enc.deco) }
            if (v.enc.mesas > 0.05f) for (x in listOf(-1.3f, -1.1f, -0.85f)) mancha(p(x, 1.3f, FONDO), 1.4f, 1.1f, C.LAMPARA, 0.5f * v.enc.mesas)
            // Las vetas del azogue gastado y dos reflejos en diagonal.
            for (k in 0..1) { val x = xm0 + 0.15f + k * 0.3f; drawLine(Color.White.copy(alpha = 0.12f + 0.06f * v.noche), p(x, y0 + 0.05f, FONDO), p(x + 0.18f, y1 - 0.15f, FONDO), 1.2f) }
        }
        drawPath(espejo, v.s(V3(-1f, y1, FONDO), lerp(C.MARFIL, C.ORO, 0.3f), FRENTE), style = Stroke(1.1f))
        drawPath(espejo, v.s(V3(-1f, y1, FONDO), C.ORO, FRENTE), style = Stroke(0.4f))
        // Los estantes, a cada lado del espejo.
        val madera = lerp(C.CAOBA, C.LACA, 0.2f)
        for ((a, b) in listOf(x0 to xm0 - 0.04f, xm1 + 0.04f to x1)) for ((i, y) in listOf(1.32f, 1.62f, 1.92f).withIndex()) {
            drawPath(forma(a, y, FONDO - 0.18f, b, y, FONDO - 0.18f, b, y - 0.03f, FONDO - 0.18f, a, y - 0.03f, FONDO - 0.18f), v.s(V3((a + b) / 2f, y, FONDO), madera, FRENTE))
            val az = Azar(Semilla.de("deco", "estante", a, i))
            var x = a + 0.03f
            while (x < b - 0.05f) {
                val o = V3(x, y, FONDO - 0.1f)
                when ((az.entero(5) + i) % 5) {
                    0, 1 -> with(PiezasDeco) { taza(v, o) }
                    2 -> { val alto = az.entre(0.18f, 0.24f); with(PiezasDeco) { palo(v, o, V3(x, y + alto, o.z), 0.055f, if (az.real() > 0.5f) lerp(C.JADE, C.LACA, 0.3f) else lerp(C.CAOBA, C.ORO, 0.3f)) } }
                    3 -> { val kk = CamDeco.k(o.z); val q = CamDeco.p(o); drawOval(v.s(o, C.CROMO, FRENTE), Offset(q.x - 0.06f * kk, q.y - 0.12f * kk), Size(0.12f * kk, 0.12f * kk)); drawLine(v.s(o, C.CROMO, FRENTE), q + Offset(0.05f * kk, -0.08f * kk), q + Offset(0.1f * kk, -0.1f * kk), 0.6f) }
                    else -> { val kk = CamDeco.k(o.z); val q = CamDeco.p(o); drawRect(v.s(o, lerp(C.MARFIL, C.CORAL, 0.2f), FRENTE), q - Offset(0.05f * kk, 0.09f * kk), Size(0.1f * kk, 0.09f * kk)) }
                }
                x += az.entre(0.09f, 0.14f)
            }
        }
        // La cortinilla de cuadros que tapa las botellas del estante de abajo de la derecha.
        val cx0 = xm1 + 0.04f
        drawPath(forma(cx0, 1.36f, FONDO - 0.16f, x1, 1.36f, FONDO - 0.16f, x1, 1.06f, FONDO - 0.16f, cx0 + 0.22f, 1.08f, FONDO - 0.16f), v.s(V3(-0.5f, 1.2f, FONDO), lerp(C.MARFIL, C.CORAL, 0.35f), FRENTE))
    }

    // =================================================================
    //  El suelo y la pista
    // =================================================================

    /**
     * El suelo de tablas de roble encerado, que corre hacia el fondo, más oscuro y más brillante de cerca, con la senda de
     * años de pasos de la escalera a la barra; y en él la pista, de parqué en rombos con su filete de latón y las marcas de
     * los tacones del baile.
     */
    fun DrawScope.suelo(v: VistaDeco) {
        val forma = formaDelSuelo(); val arriba = V3(0f, 1f, 0f)
        val roble = lerp(C.ROBLE, C.CAOBA, 0.25f)
        drawPath(forma, Brush.verticalGradient(0f to v.s(V3(-1f, 0f, FONDO), lerp(roble, C.MARFIL, 0.1f), arriba), 0.5f to v.s(V3(-1f, 0f, 4.5f), roble, arriba), 1f to v.s(V3(-1f, 0f, 2.5f), lerp(roble, C.CAOBA, 0.35f), arriba), startY = 170f, endY = 285f))
        val az = Azar(Semilla.de("deco", "suelo"))
        clipPath(forma) {
            // Las tablas: cada una de su tono, con sus juntas de testa al tresbolillo.
            var x = IZQ; var i = 0
            while (x < DER) {
                val w = 0.13f; val tono = lerp(roble, if (i % 3 == 0) C.ROBLE else C.CAOBA, az.entre(0f, 0.22f))
                var z = CERCA + az.entre(0f, 1.2f)
                drawPath(CamDeco.forma(listOf(V3(x + 0.004f, 0f, CERCA), V3(x + w - 0.004f, 0f, CERCA), V3(x + w - 0.004f, 0f, FONDO), V3(x + 0.004f, 0f, FONDO))), v.s(V3(x, 0f, 4.5f), tono, arriba).copy(alpha = 0.45f))
                while (z < FONDO) { drawLine(v.s(V3(x, 0f, z), lerp(roble, C.LACA, 0.5f), arriba).copy(alpha = 0.6f), p(x, 0f, z), p(x + w, 0f, z), 0.4f); z += az.entre(1.1f, 2.2f) }
                drawLine(v.s(V3(x, 0f, 5f), lerp(roble, C.LACA, 0.5f), arriba).copy(alpha = 0.55f), p(x, 0f, CERCA), p(x, 0f, FONDO), 0.4f)
                x += w; i++
            }
            // La senda de los pasos: de la escalera a la barra y alrededor de la pista, más mate y más clara.
            for (k in 0..7) { val z = 5.6f + k * 0.3f; val xx = 0.4f - k * 0.25f; val o = p(xx, 0f, z); mancha(o, 0.6f * CamDeco.k(z), 0.12f * CamDeco.k(z), v.s(V3(xx, 0f, z), lerp(roble, C.MARFIL, 0.3f), arriba), 0.18f) }
            // La pista.
            pista(v)
            // Arañazos, colillas pisadas y los cercos del cubo.
            repeat(30) { val w = V3(az.entre(-1.6f, 1.4f), 0f, az.entre(2.6f, 7.6f)); val q = p(w.x, w.y, w.z); drawLine(v.s(w, lerp(roble, C.MARFIL, 0.4f), arriba).copy(alpha = 0.3f), q, q + Offset(az.entre(-3f, 3f), az.entre(-0.4f, 0.4f)), 0.35f) }
            repeat(10) { val w = V3(az.entre(-1.4f, 1.2f), 0f, az.entre(2.6f, 7f)); val q = p(w.x, w.y, w.z); drawLine(v.s(w, C.MARFIL, arriba).copy(alpha = 0.5f), q, q + Offset(1.2f, 0.2f), 0.6f, StrokeCap.Round) }
            // El brillo de la cera: una franja clara hacia el fondo, que devuelve la luz de la escalera y de la barra.
            mancha(p(-0.4f, 0f, 7f), 70f, 9f, v.s(V3(-0.4f, 0.5f, 7f), lerp(C.MARFIL, roble, 0.3f), FRENTE), 0.2f)
            // El agua del día de la inundación: charcos que lo cubren casi todo y devuelven las luces.
            if (EscenaDeco.inundado(v.m)) { val agua = lerp(C.NOCHE, C.PANO, 0.4f)
                for (k in 0..5) { val w = V3(az.entre(-2f, 1f), 0f, az.entre(3f, 7.5f)); mancha(p(w.x, 0f, w.z), az.entre(40f, 80f), az.entre(6f, 12f), v.s(w, agua, arriba), 0.55f) } }
            // Hacia los ojos el suelo se oscurece: queda en la sombra de la mesa cercana y del piano.
            drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to C.SOMBRA.copy(alpha = 0.32f), startY = 220f, endY = 285f), Offset(0f, 220f), Size(ANCHO, 65f), blendMode = BlendMode.Multiply)
        }
    }

    /** La pista de baile: parqué en rombos claros y oscuros, con su borde de nogal y su filete de latón. */
    private fun DrawScope.pista(v: VistaDeco) {
        val arriba = V3(0f, 1f, 0f); val x0 = PISTA_X0; val x1 = PISTA_X1; val z0 = PISTA_Z0; val z1 = PISTA_Z1
        val borde = CamDeco.forma(listOf(V3(x0, 0f, z0), V3(x1, 0f, z0), V3(x1, 0f, z1), V3(x0, 0f, z1)))
        drawPath(borde, v.s(V3((x0 + x1) / 2f, 0f, (z0 + z1) / 2f), lerp(C.CAOBA, C.LACA, 0.2f), arriba))
        val m = 0.12f
        val dentro = CamDeco.forma(listOf(V3(x0 + m, 0f, z0 + m), V3(x1 - m, 0f, z0 + m), V3(x1 - m, 0f, z1 - m), V3(x0 + m, 0f, z1 - m)))
        clipPath(dentro) {
            val paso = 0.3f; var j = 0; var z = z0 + m
            while (z < z1) {
                var x = x0 + m - (if (j % 2 == 0) 0f else paso / 2f); var i = 0
                while (x < x1) {
                    val c = V3(x + paso / 2f, 0f, z + paso / 2f)
                    val rombo = CamDeco.forma(listOf(V3(c.x, 0f, z), V3(x + paso, 0f, c.z), V3(c.x, 0f, z + paso), V3(x, 0f, c.z)))
                    drawPath(rombo, v.s(c, if ((i + j) % 2 == 0) lerp(C.ROBLE, C.MARFIL, 0.2f) else lerp(C.ROBLE, C.CAOBA, 0.45f), arriba))
                    x += paso; i++
                }
                z += paso / 2f; j++
            }
            // Las marcas de los tacones y de los zapatos de baile: arcos grises que giran.
            val az = Azar(Semilla.de("deco", "baile"))
            repeat(22) { val c = V3(az.entre(x0, x1), 0f, az.entre(z0, z1)); val pc = p(c.x, 0f, c.z); val k = CamDeco.k(c.z)
                drawArc(v.s(c, lerp(C.ACERA, C.LACA, 0.3f), arriba).copy(alpha = 0.35f), az.entre(0f, 360f), az.entre(40f, 140f), false, pc - Offset(0.12f * k, 0.03f * k), Size(0.24f * k, 0.06f * k), style = Stroke(0.4f)) }
        }
        drawPath(dentro, v.s(V3(-1f, 0f, 5f), C.ORO, arriba).copy(alpha = 0.85f), style = Stroke(0.6f))
    }

    // =================================================================
    //  El estrado
    // =================================================================

    /** El estrado de tablas, a lo largo de la pared de la izquierda: su frente con un faldón de laca y chevrones de oro. */
    fun DrawScope.estrado(v: VistaDeco) {
        val h = ESTRADO_ALTO; val x1 = ESTRADO_X; val z0 = ESTRADO_Z0
        val tapa = forma(IZQ, h, z0, x1, h, z0, x1, h, FONDO, IZQ, h, FONDO)
        val lado = forma(x1, 0f, z0, x1, 0f, FONDO, x1, h, FONDO, x1, h, z0)
        val frente = forma(IZQ, 0f, z0, x1, 0f, z0, x1, h, z0, IZQ, h, z0)
        drawPath(tapa, v.s(V3(-2.6f, h, 5.5f), lerp(C.ROBLE, C.CAOBA, 0.5f), V3(0f, 1f, 0f)))
        clipPath(tapa) { var x = IZQ; while (x < x1) { drawLine(v.s(V3(x, h, 5f), lerp(C.CAOBA, C.LACA, 0.4f), V3(0f, 1f, 0f)).copy(alpha = 0.6f), p(x, h, z0), p(x, h, FONDO), 0.4f); x += 0.14f } }
        for ((f, n) in listOf(lado to V3(1f, 0f, 0f), frente to FRENTE)) {
            drawPath(f, v.s(V3(x1, h / 2f, 5f), C.LACA, n))
            clipPath(f) {
                // Los chevrones de oro del faldón.
                var s = if (n.x > 0f) z0 else IZQ; val fin = if (n.x > 0f) FONDO else x1
                while (s < fin) { val a = if (n.x > 0f) V3(x1, 0.05f, s) else V3(s, 0.05f, z0); val b = if (n.x > 0f) V3(x1, h - 0.05f, s + 0.12f) else V3(s + 0.12f, h - 0.05f, z0); val c = if (n.x > 0f) V3(x1, 0.05f, s + 0.24f) else V3(s + 0.24f, 0.05f, z0)
                    drawLine(v.s(b, C.ORO, n), CamDeco.p(a), CamDeco.p(b), 0.5f); drawLine(v.s(b, C.ORO, n), CamDeco.p(b), CamDeco.p(c), 0.5f); s += 0.24f }
            }
        }
        // El canto de latón del borde.
        drawLine(v.s(V3(x1, h, 5f), C.LATON, V3(0.5f, 1f, -0.5f)), p(x1, h, z0), p(x1, h, FONDO), 1f)
        drawLine(v.s(V3(-2.6f, h, z0), C.LATON, V3(0f, 1f, -1f)), p(IZQ, h, z0), p(x1, h, z0), 1.2f)
    }

    // =================================================================
    //  La escalera de la calle
    // =================================================================

    /**
     * La escalera que baja de la calle, contra la pared derecha: los peldaños gastados en el centro, su zanca de madera y
     * el hueco de debajo cerrado con tablas (y su puertecilla), el barandal de hierro con sus barrotes y su bola de latón,
     * el rellano y la puerta de arriba, cerrada, entornada o abierta, con su mirilla. La bombilla de guardia en la pared.
     */
    fun DrawScope.escalera(v: VistaDeco) {
        val x0 = ESC_X0; val x1 = DER; val n = PELDANOS; val sube = ESC_ALTO / n; val huella = (ESC_Z1 - ESC_Z0) / n
        val madera = lerp(C.ROBLE, C.CAOBA, 0.45f); val arriba = V3(0f, 1f, 0f)
        // El rellano y la puerta.
        drawPath(forma(x0, ESC_ALTO, ESC_Z1, x1, ESC_ALTO, ESC_Z1, x1, ESC_ALTO, FONDO, x0, ESC_ALTO, FONDO), v.s(V3(1.1f, ESC_ALTO, 7.98f), madera, arriba))
        puerta(v)
        // El lado de la escalera (la zanca y el tabique de tablas de debajo): un triángulo que mira a la sala.
        val lado = forma(x0, 0f, ESC_Z0, x0, 0f, FONDO, x0, ESC_ALTO, FONDO, x0, ESC_ALTO, ESC_Z1, x0, sube, ESC_Z0)
        drawPath(lado, Brush.verticalGradient(0f to v.s(V3(x0, 1.6f, 7.5f), madera, V3(-1f, 0f, 0f)), 1f to v.s(V3(x0, 0.2f, 6f), lerp(madera, C.LACA, 0.3f), V3(-1f, 0f, 0f)), startY = 95f, endY = 200f))
        clipPath(lado) {
            var z = ESC_Z0 + 0.05f; while (z < FONDO) { drawLine(v.s(V3(x0, 0.5f, z), lerp(madera, C.LACA, 0.5f), V3(-1f, 0f, 0f)).copy(alpha = 0.6f), p(x0, 0f, z), p(x0, ESC_ALTO, z), 0.45f); z += 0.16f }
            // La puertecilla del hueco de debajo, con su picaporte, y las rayas de las fregonas.
            drawPath(forma(x0, 0.05f, 6.7f, x0, 0.05f, 7.4f, x0, 0.95f, 7.4f, x0, 0.95f, 6.7f), v.s(V3(x0, 0.5f, 7f), lerp(madera, C.LACA, 0.2f), V3(-1f, 0f, 0f)).copy(alpha = 0.7f), style = Stroke(0.6f))
            drawCircle(v.s(V3(x0, 0.5f, 6.8f), C.LATON, V3(-1f, 0f, 0f)), 0.6f, p(x0, 0.5f, 6.78f))
        }
        // La zanca: una tabla gruesa que sube en diagonal, con su canto de luz.
        val zanca = forma(x0 - 0.02f, sube * 0.3f, ESC_Z0 - 0.02f, x0 - 0.02f, ESC_ALTO + 0.05f, ESC_Z1, x0 - 0.02f, ESC_ALTO - 0.22f, ESC_Z1, x0 - 0.02f, -0.15f, ESC_Z0 + 0.25f)
        drawPath(zanca, v.s(V3(x0, 1f, 6.5f), lerp(madera, C.CAOBA, 0.4f), V3(-1f, 0.3f, -0.3f)))
        drawLine(v.s(V3(x0, 1f, 6.5f), lerp(madera, C.MARFIL, 0.4f), V3(-1f, 1f, -0.5f)).copy(alpha = 0.7f), p(x0 - 0.02f, sube * 0.3f, ESC_Z0 - 0.02f), p(x0 - 0.02f, ESC_ALTO + 0.05f, ESC_Z1), 0.6f)
        // Los peldaños, de arriba abajo: la tabica (que mira a los ojos) y la huella (que se ve mientras queda por debajo de ellos).
        for (i in n - 1 downTo 0) {
            val y0 = i * sube; val y1 = (i + 1) * sube; val z = ESC_Z0 + i * huella
            val tabica = forma(x0, y0, z, x1, y0, z, x1, y1, z, x0, y1, z)
            drawPath(tabica, v.s(V3((x0 + x1) / 2f, (y0 + y1) / 2f, z), lerp(madera, C.LACA, 0.15f), FRENTE))
            if (y1 < CamDeco.OJO) {
                val h = forma(x0, y1, z - 0.03f, x1, y1, z - 0.03f, x1, y1, z + huella, x0, y1, z + huella)
                drawPath(h, v.s(V3((x0 + x1) / 2f, y1, z + huella / 2f), lerp(madera, C.MARFIL, 0.12f), arriba))
                // El desgaste del centro de la huella, más claro, y la mampera.
                mancha(p((x0 + x1) / 2f, y1, z + huella / 2f), 0.25f * CamDeco.k(z), 0.04f * CamDeco.k(z), v.s(V3(1.1f, y1, z), lerp(madera, C.MARFIL, 0.4f), arriba), 0.35f)
            }
            drawLine(v.s(V3((x0 + x1) / 2f, y1, z), lerp(C.LATON, madera, 0.3f), V3(0f, 0.5f, -1f)).copy(alpha = 0.8f), p(x0, y1, z - 0.03f), p(x1, y1, z - 0.03f), 0.45f)
        }
        // La nieve que bajan los zapatos, la noche de la nevada: huellas mojadas en los peldaños de abajo.
        if (EscenaDeco.nevada(v.m) && !v.prov) for (i in 0..4) { val z = ESC_Z0 + i * huella + huella / 2f; val y = (i + 1) * sube; mancha(p(1.05f + (i % 2) * 0.2f, y, z), 3f, 1f, v.s(V3(1f, y, z), C.MARFIL, arriba), 0.45f) }
        barandal(v)
        // La bombilla de guardia, enjaulada, en la pared del hueco.
        val g = CamDeco.p(GUARDIA); val k = CamDeco.k(GUARDIA.z)
        drawLine(v.s(GUARDIA, C.HIERRO, V3(-1f, 0f, 0f)), g, g + Offset(0f, -0.25f * k), 0.5f)
        val encendida = v.enc.guardia.coerceAtLeast(if (v.fase == EscenaDeco.Fase.ABIERTO || v.fase == EscenaDeco.Fase.DESPUES) 1f else 0f)
        drawCircle(lerp(v.s(GUARDIA, C.MARFIL, V3(-1f, 0f, 0f)), lerp(C.LAMPARA, Color.White, 0.5f), encendida), 0.05f * k, g)
        for (i in -1..1) drawLine(v.s(GUARDIA, C.HIERRO, V3(-1f, 0f, 0f)), g + Offset(i * 0.035f * k, -0.07f * k), g + Offset(i * 0.035f * k, 0.06f * k), 0.35f)
    }

    /** El barandal de hierro: el pasamanos, los barrotes, el poste de abajo con su bola de latón. */
    private fun DrawScope.barandal(v: VistaDeco) {
        val x = ESC_X0 - 0.04f; val n = PELDANOS; val sube = ESC_ALTO / n; val huella = (ESC_Z1 - ESC_Z0) / n; val alto = 0.92f
        fun pasamanos(z: Float): Float = ((z - ESC_Z0) / huella).coerceIn(0f, n.toFloat()) * sube + alto
        val z0 = ESC_Z0 + 0.1f
        // Los barrotes, uno por peldaño.
        for (i in 1 until n) { val z = ESC_Z0 + i * huella + huella / 2f; val y0 = (i + 1) * sube
            with(PiezasDeco) { palo(v, V3(x, y0, z), V3(x, pasamanos(z), z), 0.016f, C.HIERRO, filo = false) } }
        // El pasamanos, que sube hasta el rellano y gira.
        with(PiezasDeco) {
            palo(v, V3(x, pasamanos(z0), z0), V3(x, ESC_ALTO + alto, ESC_Z1), 0.045f, C.HIERRO)
            palo(v, V3(x, ESC_ALTO + alto, ESC_Z1), V3(x, ESC_ALTO + alto, FONDO - 0.05f), 0.045f, C.HIERRO)
            // El poste de abajo, con su voluta y su bola de latón.
            palo(v, V3(x, 0f, z0), V3(x, pasamanos(z0) + 0.05f, z0), 0.07f, C.HIERRO)
        }
        val bola = V3(x, pasamanos(z0) + 0.1f, z0); val k = CamDeco.k(z0)
        drawCircle(Brush.radialGradient(listOf(lerp(v.s(bola, C.LATON, V3(0.5f, 0.5f, -1f)), C.MARFIL, 0.4f), v.s(bola, C.LATON, FRENTE), v.s(bola, lerp(C.LATON, C.CAOBA, 0.5f), V3(-0.5f, 0f, 0f))), CamDeco.p(bola) + Offset(0.01f * k, -0.015f * k), 0.05f * k), 0.05f * k, CamDeco.p(bola))
    }

    /**
     * La puerta de la calle, arriba de la escalera, en el fondo del hueco: de tablas, con su mirilla de rejilla. Cerrada,
     * se ve la luz de fuera por las rendijas; entornada, una franja; abierta, el hueco entero con la calle (el cielo, el
     * neón de enfrente). El día de la redada, con un candado y un papel pegado (sin letras).
     */
    private fun DrawScope.puerta(v: VistaDeco) {
        val x0 = PUERTA_X0; val x1 = PUERTA_X1; val y0 = ESC_ALTO; val y1 = PUERTA_Y1; val z = FONDO
        val hueco = forma(x0, y0, z, x1, y0, z, x1, y1, z, x0, y1, z)
        // Lo de fuera: el cielo de la calle, el resplandor del letrero, la nieve.
        val ext = v.ext; val dia = 1f - ext.noche
        val fuera = lerp(lerp(ext.bajo, ext.medio, 0.5f), Color(0xFF1A1C3A), v.noche * 0.6f)
        drawPath(hueco, Brush.verticalGradient(0f to lerp(fuera, ext.alto, 0.4f), 1f to lerp(fuera, if (v.neon > 0.1f) C.NEON else ext.bajo, 0.25f * v.neon + 0.2f * dia), startY = p(0f, y1, z).y, endY = p(0f, y0, z).y))
        clipPath(hueco) {
            // El arranque de la acera y la fachada de enfrente, al fondo.
            drawRect(lerp(ext.tono(C.LADRILLO), C.LACA, 0.4f * v.noche), p(x0, y0 + 1.1f, z), Size(p(x1, 0f, z).x - p(x0, 0f, z).x, p(0f, y0 + 0.2f, z).y - p(0f, y0 + 1.1f, z).y))
            if (v.neon > 0.05f) tuboNeon(Path().apply { val a = p(x0 + 0.15f, y0 + 1.55f, z); val b = p(x1 - 0.1f, y0 + 1.55f, z); moveTo(a.x, a.y); lineTo(b.x, b.y) }, C.NEON, v.neon, 0.9f)
            if (EscenaDeco.nevada(v.m)) drawRect(lerp(ext.tono(C.MARFIL), C.NOCHE, 0.4f * v.noche), p(x0, y0 + 0.25f, z), Size(p(x1, 0f, z).x - p(x0, 0f, z).x, 6f))
        }
        // La hoja: abierta hacia dentro y contra la pared de la derecha; entornada, casi cerrada; cerrada, del todo.
        val abre = when (v.puerta) { Puerta.CERRADA -> 0f; Puerta.ENTORNADA -> 0.18f; Puerta.ABIERTA -> 0.85f }
        val ang = (PI / 2 * abre).toFloat(); val ancho = x1 - x0
        val xb = x1 - ancho * cos(ang); val zb = z - ancho * sin(ang)
        val hoja = forma(xb, y0, zb, x1, y0, z, x1, y1, z, xb, y1, zb)
        val madera = lerp(C.CAOBA, C.JADE, 0.25f)
        drawPath(hoja, v.s(V3((xb + x1) / 2f, (y0 + y1) / 2f, (z + zb) / 2f), madera, V3(-sin(ang) * 0.3f, 0f, -1f)))
        clipPath(hoja) {
            for (k in 1..3) { val t = k / 4f; drawLine(C.LACA.copy(alpha = 0.4f), p(xb + (x1 - xb) * t, y0, zb + (z - zb) * t), p(xb + (x1 - xb) * t, y1, zb + (z - zb) * t), 0.4f) }
            // La mirilla del portero: una rejilla de hierro en su ventanuco.
            val my = y0 + 1.5f; val mx0 = xb + (x1 - xb) * 0.35f; val mx1 = xb + (x1 - xb) * 0.65f; val mz0 = zb + (z - zb) * 0.35f; val mz1 = zb + (z - zb) * 0.65f
            drawPath(forma(mx0, my, mz0, mx1, my, mz1, mx1, my + 0.22f, mz1, mx0, my + 0.22f, mz0), lerp(C.LACA, C.NEON, 0.35f * v.neon))
            for (k in 1..3) { val t = k / 4f; drawLine(C.HIERRO, p(mx0 + (mx1 - mx0) * t, my, mz0 + (mz1 - mz0) * t), p(mx0 + (mx1 - mx0) * t, my + 0.22f, mz0 + (mz1 - mz0) * t), 0.4f) }
        }
        // Las rendijas de luz alrededor de la puerta cerrada.
        if (abre < 0.05f) { val luz = lerp(lerp(ext.medio, Color.White, 0.4f), C.NEON, v.neon * 0.7f).copy(alpha = 0.35f + 0.4f * maxOf(dia, v.neon))
            drawLine(luz, p(x0, y0, z), p(x0, y1, z), 0.6f); drawLine(luz, p(x0, y0 + 0.01f, z), p(x1, y0 + 0.01f, z), 0.5f) }
        // El candado y el papel pegado de la redada.
        if (EscenaDeco.redada(v.m)) { val c = p(x0 + 0.1f, y0 + 1.0f, z); drawRect(v.s(V3(x0, 3f, z), C.MARFIL, FRENTE), p(x0 + 0.2f, y0 + 1.25f, z), Size(6f, 7f)); drawRect(v.s(V3(x0, 3f, z), C.LATON, FRENTE), c, Size(2.4f, 2f)); drawArc(v.s(V3(x0, 3f, z), C.CROMO, FRENTE), 180f, 180f, false, c + Offset(0.3f, -1.4f), Size(1.8f, 2.8f), style = Stroke(0.4f)) }
        // El marco de la puerta.
        drawPath(hueco, v.s(V3(1.1f, 3f, z), lerp(C.CAOBA, C.LACA, 0.3f), FRENTE), style = Stroke(1.4f))
    }

    // =================================================================
    //  Las ventanas altas
    // =================================================================

    /**
     * Las dos ventanas altas de la pared derecha, a ras de acera: el derrame del muro, el vidrio con la calle que se ve
     * desde abajo (la acera con su bordillo, la boca de riego, las ruedas de un coche parado, la fachada de enfrente con su
     * escalera de incendios y, de noche, su letrero de neón de formas), la reja y el bastidor. Con lluvia, el agua por los
     * cristales; con nieve, la nieve amontonada contra ellos; con calor, la hoja de abajo abierta.
     */
    fun DrawScope.ventanas(v: VistaDeco) {
        for ((i, z) in VENTANAS.withIndex()) ventana(v, i, z.first, z.second)
    }

    private fun DrawScope.ventana(v: VistaDeco, i: Int, z0: Float, z1: Float) {
        val x = DER; val xv = DER + MURO
        val izq = V3(-1f, 0f, 0f)
        // El derrame: la cara del fondo del hueco (que mira a los ojos) y el intradós de arriba (que se ve desde abajo).
        val fondo = forma(x, VY0, z1, xv, VY0, z1, xv, VY1, z1, x, VY1, z1)
        val techo = forma(x, VY1, z0, xv, VY1, z0, xv, VY1, z1, x, VY1, z1)
        val muro = lerp(lerp(C.MARFIL, C.ACERA, 0.5f), C.PARED, 0.45f)
        drawPath(fondo, v.s(V3(x + 0.15f, 2f, z1), muro, FRENTE)); drawPath(techo, v.s(V3(x + 0.15f, VY1, (z0 + z1) / 2f), lerp(muro, C.PARED, 0.3f), V3(0f, -1f, 0f)))
        // El vidrio, con la calle detrás.
        val vidrio = listOf(V3(xv, VY0, z0), V3(xv, VY0, z1), V3(xv, VY1, z1), V3(xv, VY1, z0))
        val hueco = CamDeco.forma(vidrio)
        clipPath(hueco) { calle(v, i, z0, z1) }
        // El agua de la lluvia por los cristales y la nieve contra ellos.
        if (EscenaDeco.lluvia(v.m)) clipPath(hueco) { val az = Azar(Semilla.de("deco", "lluvia_vidrio", i)); repeat(14) { val z = az.entre(z0, z1); val y = az.entre(VY0, VY1); drawLine(Color.White.copy(alpha = 0.35f), p(xv, y, z), p(xv, y - az.entre(0.1f, 0.3f), z + 0.01f), 0.5f) } }
        if (EscenaDeco.nevada(v.m)) {
            val nieve = lerp(v.ext.tono(C.MARFIL), C.NOCHE, 0.35f * v.noche)
            val monton = Path().apply { val a = p(xv, VY0, z0); moveTo(a.x, a.y); for (k in 0..8) { val t = k / 8f; val q = p(xv, VY0 + 0.16f + 0.06f * sin(t * 5f + i), z0 + (z1 - z0) * t); lineTo(q.x, q.y) }; val b = p(xv, VY0, z1); lineTo(b.x, b.y); close() }
            clipPath(hueco) { drawPath(monton, nieve) }
        }
        // El bastidor y su travesaño, y la reja de fuera: barrotes de hierro.
        val madera = lerp(C.CAOBA, C.LACA, 0.3f)
        drawPath(hueco, v.s(V3(xv, 2f, z0), madera, izq), style = Stroke(1.2f))
        val ym = (VY0 + VY1) / 2f
        drawLine(v.s(V3(xv, ym, z0), madera, izq), p(xv, ym, z0), p(xv, ym, z1), 0.9f)
        for (k in 1..3) { val z = z0 + (z1 - z0) * k / 4f; drawLine(v.s(V3(xv, ym, z), C.HIERRO, izq), p(xv + 0.02f, VY0, z), p(xv + 0.02f, VY1, z), 0.035f * CamDeco.k(z)) }
        // La hoja de abajo abierta en los días de calor: el vidrio tumbado hacia dentro.
        if (EscenaDeco.ventanasAbiertas(v.m)) {
            val hoja = forma(x + 0.02f, ym, z0 + 0.03f, x + 0.02f, ym, z1 - 0.03f, xv - 0.2f, VY1 - 0.05f, z1 - 0.03f, xv - 0.2f, VY1 - 0.05f, z0 + 0.03f)
            drawPath(hoja, Color.White.copy(alpha = 0.12f)); drawPath(hoja, v.s(V3(x, ym, z0), madera, izq), style = Stroke(0.8f))
        }
        // El alféizar de dentro, de piedra, con su canto de luz.
        drawLine(v.s(V3(x, VY0, (z0 + z1) / 2f), lerp(muro, C.MARFIL, 0.2f), V3(0f, 1f, -0.5f)), p(x, VY0, z0 - 0.04f), p(x, VY0, z1 + 0.04f), 1.4f)
    }

    /**
     * La calle, vista desde abajo por la ventana [i]: se dibuja en el plano del vidrio, con su misma perspectiva **[Aproximación]**
     * (la fachada de enfrente corre paralela a la pared, así que se fuga hacia el mismo punto). Abajo, la acera y el
     * bordillo; en la primera ventana, la boca de riego; en la segunda, la rueda y el guardabarros de un coche parado;
     * detrás, la fachada de ladrillo con su escalera de incendios y, de noche, el letrero de neón (una copa, una nota, una
     * estrella: formas sin letras).
     */
    private fun DrawScope.calle(v: VistaDeco, i: Int, z0: Float, z1: Float) {
        val xv = DER + MURO; val ext = v.ext; val noche = v.noche; val dia = 1f - noche
        fun q(u: Float, w: Float) = p(xv, VY0 + (VY1 - VY0) * w, z0 + (z1 - z0) * u)
        fun banda(u0: Float, w0: Float, u1: Float, w1: Float, c: Color) = drawPath(poligono(q(u0, w0), q(u1, w0), q(u1, w1), q(u0, w1)), c)
        val llueve = EscenaDeco.lluvia(v.m); val nieva = EscenaDeco.nevada(v.m)
        // El cielo, una rendija arriba, y la fachada de enfrente.
        banda(0f, 0.86f, 1f, 1.01f, lerp(ext.medio, ext.alto, 0.4f))
        val ladrillo = ext.cara(C.LADRILLO, if (dia > 0.5f) 0.3f else -0.3f, 0.35f)
        banda(0f, 0.3f, 1f, 0.87f, lerp(ladrillo, Color(0xFF14142A), 0.55f * noche))
        // Sus ventanas: oscuras de día, alguna encendida de noche.
        for (k in 0..2) { val u = 0.12f + k * 0.32f; val luz = noche > 0.4f && (k + i) % 2 == 0
            banda(u, 0.56f, u + 0.14f, 0.8f, if (luz) lerp(C.LAMPARA, C.ORO, 0.3f) else lerp(ext.tono(C.PANO, 0.3f), Color(0xFF101024), noche * 0.6f)) }
        // La escalera de incendios: un zigzag de hierro con sus plataformas.
        val hierro = lerp(ext.tono(C.HIERRO, 0.2f), Color(0xFF0A0A16), noche * 0.5f)
        for (k in 0..1) { val w = 0.5f + k * 0.3f; drawLine(hierro, q(0.05f, w), q(0.95f, w), 0.8f); drawLine(hierro, q(0.15f, w), q(0.75f, w + 0.3f), 0.6f) }
        for (u in listOf(0.05f, 0.5f, 0.95f)) drawLine(hierro, q(u, 0.3f), q(u, 0.9f), 0.4f)
        // El letrero de neón de enfrente (de noche): formas sin letras.
        if (v.neon > 0.05f && i < EscenaDeco.letreros(v.m)) {
            val c = if (i == 0) C.NEON else C.NEON_JADE
            val trazo = Path()
            if (i == 0) { val a = q(0.25f, 0.78f); val b = q(0.75f, 0.78f); val pie = q(0.5f, 0.52f); trazo.moveTo(a.x, a.y); trazo.lineTo(b.x, b.y); trazo.lineTo(pie.x, pie.y); trazo.close(); val base = q(0.5f, 0.4f); trazo.moveTo(pie.x, pie.y); trazo.lineTo(base.x, base.y) }
            else { val a = q(0.3f, 0.45f); val b = q(0.45f, 0.75f); val c2 = q(0.62f, 0.7f); trazo.moveTo(a.x, a.y); trazo.lineTo(b.x, b.y); trazo.lineTo(c2.x, c2.y); trazo.addOval(androidx.compose.ui.geometry.Rect(a.x - 2f, a.y - 1.2f, a.x + 1f, a.y + 1.2f)) }
            tuboNeon(trazo, c, v.neon, 0.8f)
        }
        // La calzada y el bordillo, y la acera, que llega hasta el vidrio.
        val acera = lerp(ext.tono(C.ACERA, 0.1f), Color(0xFF181830), noche * 0.5f)
        banda(0f, 0.22f, 1f, 0.31f, lerp(ext.tono(C.LACA, 0.2f), Color(0xFF0E0E1C), noche * 0.4f))
        banda(0f, 0.17f, 1f, 0.22f, lerp(acera, Color.White, 0.15f))
        banda(0f, -0.02f, 1f, 0.17f, acera)
        for (k in 1..3) drawLine(lerp(acera, C.LACA, 0.3f), q(k * 0.28f, 0f), q(k * 0.28f - 0.05f, 0.17f), 0.4f)
        if (llueve) { banda(0f, -0.02f, 1f, 0.17f, lerp(acera, ext.medio, 0.3f).copy(alpha = 0.5f)); if (v.neon > 0.1f) drawLine(C.NEON.copy(alpha = 0.5f * v.neon), q(0.3f, 0.12f), q(0.7f, 0.06f), 1.2f) }
        if (i == 0) {
            // La boca de riego, en la acera: roja, con sus tapas y su sombrerete.
            val roja = ext.cara(C.PARED, if (dia > 0.5f) 0.2f else -0.2f, 0.05f).let { lerp(it, Color(0xFF241018), noche * 0.4f) }
            val cuerpo = poligono(q(0.4f, 0.05f), q(0.62f, 0.05f), q(0.6f, 0.62f), q(0.42f, 0.62f))
            drawPath(cuerpo, roja)
            drawPath(poligono(q(0.36f, 0.62f), q(0.66f, 0.62f), q(0.6f, 0.75f), q(0.42f, 0.75f)), lerp(roja, C.LACA, 0.25f))
            drawPath(poligono(q(0.3f, 0.32f), q(0.4f, 0.32f), q(0.4f, 0.44f), q(0.3f, 0.44f)), lerp(roja, Color.White, 0.15f))
            drawPath(poligono(q(0.62f, 0.32f), q(0.72f, 0.32f), q(0.72f, 0.44f), q(0.62f, 0.44f)), lerp(roja, C.LACA, 0.3f))
            drawPath(poligono(q(0.36f, 0.05f), q(0.66f, 0.05f), q(0.66f, 0.1f), q(0.36f, 0.1f)), lerp(roja, C.LACA, 0.3f))
            if (nieva) drawPath(poligono(q(0.38f, 0.75f), q(0.62f, 0.75f), q(0.55f, 0.8f), q(0.45f, 0.8f)), ext.tono(C.MARFIL))
        } else {
            // La rueda de un coche parado: el neumático, el tapacubos cromado y el guardabarros.
            val c = q(0.62f, 0.32f); val r = (q(0.62f, 0.62f).y - c.y).let { kotlin.math.abs(it) }
            drawPath(Path().apply { val a = q(0.15f, 0.58f); val b = q(1.05f, 0.58f); moveTo(a.x, a.y); cubicTo(a.x, a.y - r * 0.9f, b.x, b.y - r * 0.9f, b.x, b.y); close() }, lerp(ext.tono(C.LACA, 0.1f), Color(0xFF0A0A14), noche * 0.4f))
            drawCircle(lerp(ext.tono(C.LACA, 0.1f), Color(0xFF050508), noche * 0.4f), r, c)
            drawCircle(lerp(ext.tono(C.CROMO, 0.1f), C.NEON, 0.3f * v.neon), r * 0.45f, c)
            for (k in 0..5) { val a = k * 1.05f; drawLine(lerp(ext.tono(C.CROMO, 0.1f), C.LACA, 0.4f), c, c + Offset(cos(a) * r * 0.4f, sin(a) * r * 0.4f), 0.4f) }
            drawLine(lerp(Color.White, C.NEON, 0.4f * v.neon).copy(alpha = 0.5f), c + Offset(-r * 0.2f, -r * 0.25f), c + Offset(r * 0.05f, -r * 0.35f), 0.6f)
        }
        // La luz de la calle: de día, el sol en la fachada; de noche, la farola y el resplandor del letrero.
        if (noche > 0.3f) mancha(q(0.5f, 0.4f), 14f, 10f, lerp(C.LAMPARA, C.NEON, 0.4f * v.neon), 0.25f * noche)
        if (llueve || EscenaDeco.nevada(v.m) || v.m.ficha.clima == Clima.NIEBLA) drawPath(poligono(q(0f, 0f), q(1f, 0f), q(1f, 1f), q(0f, 1f)), lerp(ext.bruma, ext.medio, 0.5f).copy(alpha = 0.25f))
    }

    /** Lo que enmarca por arriba, delante de todo: el borde oscuro del techo cercano, para que la lámpara colgante se recorte. */
    fun DrawScope.marco(v: VistaDeco) {
        drawRect(Brush.verticalGradient(0f to C.SOMBRA.copy(alpha = 0.45f), 1f to Color.Transparent, startY = 0f, endY = 26f), Offset.Zero, Size(ANCHO, 26f), blendMode = BlendMode.Multiply)
    }

    /** Para que el color de algo en la penumbra de un rincón tire a violeta y no a gris. */
    fun enPenumbra(c: Color, k: Float): Color = mult(c, C.SOMBRA, k)
}
