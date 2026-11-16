package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.GeoCentro.G1
import io.github.ruddy2003.vestigium.ui.escenas.GeoCentro.G2
import io.github.ruddy2003.vestigium.ui.escenas.GeoCentro.LOSA
import io.github.ruddy2003.vestigium.ui.escenas.GeoCentro.RV
import io.github.ruddy2003.vestigium.ui.escenas.GeoCentro.RW
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.ABAJO
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.ARRIBA
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.cara
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.caraGrande
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.linea
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.trazo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import kotlin.math.cos
import kotlin.math.sin

/**
 * Las dos galerías en anillo alrededor del vacío (`composicion_laboratorio_centro.md`, «Alrededor, las galerías»): el suelo
 * de cada una, el canto de su losa (con su espesor) y la losa vista por debajo, la barandilla de vidrio de canto grueso con
 * su pasamanos de metal gastado, la pared de fuera con las puertas de las alas (cada una con su franja de color), las
 * luces empotradas en la losa de encima, y los dos elevadores silenciosos que corren por su carril. Se cortan en la pared de
 * pantallas, que ocupa ese tramo de dos pisos de alto, con el canto de la losa a la vista.
 *
 * La de arriba se pinta antes que la pared de pantallas y que el comedor; la del primer piso, después del comedor (tapa lo
 * que hay bajo ella), menos su barandilla de cerca, que es de la esquina ([EsquinaCentro]).
 */
internal object GaleriaCentro {
    private val C = ColorLaboratorio

    /** El paso de los tramos de los anillos (grados). */
    private const val PASO = 5f

    /** Los colores de las alas, por puertas: Botánica, Medicina, Mentes, la Vigía, el Destello, Mantenimiento. **[Diseño propio]** */
    val ALAS = listOf(C.GRAMA, C.CIAN, C.VIOLETA, C.AMARILLO, lerp(C.CIAN, C.VIOLETA, 0.5f), C.BARRO)

    /** El tramo de cerca de la barandilla del primer piso (grados): lo pinta la esquina, después del haz. */
    const val CERCA0 = -178f
    const val CERCA1 = -34f

    /** Los tramos de un anillo, saltando la pared de pantallas: pares (desde, hasta) en grados. */
    fun tramos(desde: Float = GeoCentro.PA1, hasta: Float = 360f + GeoCentro.PA0, paso: Float = PASO): List<Pair<Float, Float>> {
        val n = ((hasta - desde) / paso).toInt().coerceAtLeast(1); val d = (hasta - desde) / n
        return (0 until n).map { desde + it * d to desde + (it + 1) * d }
    }

    private fun haciaDentro(g: Float): V3 = V3(-cos(GeoCentro.rad(g)), 0f, -sin(GeoCentro.rad(g)))

    /** Qué tan lejos está (para la bruma del aire del hall): 0 cerca, 1 al otro lado del vacío. */
    fun lejos(w: V3): Float = ((CamCentro.hondo(w) - 6f) / 16f).coerceIn(0f, 1f) * 0.8f

    // =================================================================
    //  La segunda galería
    // =================================================================

    fun DrawScope.segunda(v: VistaCentro) {
        val techo = GeoCentro.YB
        // La pared de fuera, con las puertas de las alas.
        pared(v, G2, techo, 2)
        // La losa vista por debajo (lo que está sobre la primera galería), con sus luces empotradas.
        for ((a, b) in tramos()) cara(v, PiezasCentro.tramoH(RV, RW, G2 - LOSA, a, b), lerp(C.HORMIGON, C.TIERRA, 0.1f), ABAJO, lejos(GeoCentro.aro(RV, G2, a)))
        // Las juntas de los paneles del techo de la galería y la canaleta de las luces.
        val junta = lerp(C.SOMBRA, C.HORMIGON, 0.4f)
        for ((a, _) in tramos(paso = 10f)) linea(v, GeoCentro.aro(RV + 0.05f, G2 - LOSA - 0.003f, a), GeoCentro.aro(RW, G2 - LOSA - 0.003f, a), junta, ABAJO, 0.5f, 0.5f)
        trazo(v, PiezasCentro.arco(RV + 1.3f, G2 - LOSA - 0.004f, GeoCentro.PA1, 360f + GeoCentro.PA0, 90), lerp(C.ACERO, C.SOMBRA, 0.4f), ABAJO, 0.12f, 0.6f)
        luces(v, G2 - LOSA - 0.01f, true)
        canto(v, G2)
        barandilla(v, G2, GeoCentro.PA1, 360f + GeoCentro.PA0)
        extremos(v, G2)
    }

    // =================================================================
    //  La primera galería
    // =================================================================

    fun DrawScope.primera(v: VistaCentro) {
        // La pared de fuera, con sus puertas.
        pared(v, G1, G2 - LOSA, 1)
        // El suelo de la galería: losas de hormigón pulido, más oscuras hacia la pared.
        val az = Azar(Semilla.de("laboratorio", "centro", "galeria"))
        for ((a, b) in tramos()) {
            val w = GeoCentro.aro((RV + RW) / 2f, G1, (a + b) / 2f)
            val base = lerp(lerp(C.PISO, C.TELA, 0.35f), C.BARRO, 0.05f)
            val propio = if (az.real() < 0.2f) lerp(base, C.TELA, 0.2f) else lerp(base, C.PISO, az.entre(0f, 0.3f))
            caraGrande(v, PiezasCentro.tramoH(RV, RW, G1, a, b), propio, ARRIBA, GeoCentro.aro(RV, G1, (a + b) / 2f), GeoCentro.aro(RW, G1, (a + b) / 2f), lejos(w), pasos = 3)
            // La junta entre losas.
            linea(v, GeoCentro.aro(RV, G1 + 0.002f, a), GeoCentro.aro(RW, G1 + 0.002f, a), lerp(C.SOMBRA, C.PISO, 0.4f), ARRIBA, 0.4f, 0.35f)
        }
        // Los charcos de las luces empotradas en la losa de arriba.
        luces(v, G1 + 0.003f, false)
        canto(v, G1)
        barandilla(v, G1, GeoCentro.PA1, CERCA0 + 360f)
        barandilla(v, G1, CERCA1 + 360f, 360f + GeoCentro.PA0)
        extremos(v, G1)
    }

    /** La pared de fuera de una galería, de [y0] a [y1], con las puertas de las alas. [piso]: 1 o 2 (cambia el orden de los colores). */
    private fun DrawScope.pared(v: VistaCentro, y0: Float, y1: Float, piso: Int) {
        val muro = lerp(C.HORMIGON, C.TIERRA, 0.08f)
        for ((a, b) in tramos()) {
            val gm = (a + b) / 2f
            cara(v, PiezasCentro.tramoV(RW, y0, y1, a, b), muro, haciaDentro(gm), lejos(GeoCentro.aro(RW, y0, gm)))
        }
        // El zócalo gastado y la franja de pintura de las alas, a la altura de la mano.
        for ((a, b) in tramos()) {
            val gm = (a + b) / 2f
            cara(v, PiezasCentro.tramoV(RW - 0.01f, y0, y0 + 0.12f, a, b), lerp(C.ACERO, C.TIERRA, 0.4f), haciaDentro(gm), lejos(GeoCentro.aro(RW, y0, gm)))
        }
        // La humedad y los remiendos.
        PiezasCentro.run { manchasEnLaPared(v, RW, y0 + 0.2f, y1 - 0.1f, GeoCentro.PA1, 360f + GeoCentro.PA0, 70, "pared $piso") }
        // Las puertas: una cada 24 grados, con su franja de color y el vidrio del ojo de buey encendido.
        var i = 0
        var g = GeoCentro.PA1 + 9f + piso * 6f
        while (g < 360f + GeoCentro.PA0 - 6f) {
            puerta(v, g, y0, ALAS[(i + piso * 2) % ALAS.size])
            g += 24f; i++
        }
    }

    /** Una puerta de ala en la pared de fuera, a [g] grados, desde el suelo [y0]: el hueco, la hoja de metal, la franja de color y el ventanuco encendido. */
    private fun DrawScope.puerta(v: VistaCentro, g: Float, y0: Float, color: Color) {
        val n = haciaDentro(g)
        val w0 = GeoCentro.aro(RW, y0, g); if (!CamCentro.seVe(w0, n)) return
        if (!CamCentro.enCuadro(CamCentro.p(w0))) return
        val lj = lejos(w0)
        val ancho = 6f                                                          // grados (≈1.15 m)
        val g0 = g - ancho / 2f; val g1 = g + ancho / 2f
        // El marco (un poco más oscuro) y la hoja.
        cara(v, PiezasCentro.tramoV(RW - 0.02f, y0, y0 + 2.3f, g0 - 0.6f, g1 + 0.6f), lerp(C.ACERO, C.HORMIGON, 0.4f), n, lj, siempre = true)
        cara(v, PiezasCentro.tramoV(RW - 0.03f, y0, y0 + 2.15f, g0, g1), lerp(C.ACERO, C.SOMBRA, 0.25f), n, lj, siempre = true)
        // La franja de color del ala, a media altura, que sigue un poco por la pared.
        cara(v, PiezasCentro.tramoV(RW - 0.035f, y0 + 1.0f, y0 + 1.22f, g0 - 2.5f, g1 + 2.5f), color, n, lj, siempre = true)
        // El ventanuco, encendido por dentro.
        val f = CamCentro.forma(PiezasCentro.tramoV(RW - 0.04f, y0 + 1.45f, y0 + 1.85f, g - 1.2f, g + 1.2f)) ?: return
        drawPath(f, lerp(lerp(C.LUZ_DIA, C.VIDRIO, 0.4f), v.fondo, 0.3f + 0.4f * lj))
    }

    /** El canto de la losa de una galería, que mira al vacío: con su espesor, más claro arriba (le da el hall) y una línea de sombra bajo el borde. */
    private fun DrawScope.canto(v: VistaCentro, y: Float) {
        for ((a, b) in tramos(paso = 4f)) {
            val gm = (a + b) / 2f; val n = haciaDentro(gm)
            val ps = PiezasCentro.tramoV(RV, y - LOSA, y, a, b)
            if (!CamCentro.seVe(ps[0], n)) continue
            cara(v, ps, lerp(C.HORMIGON, C.TELA, 0.3f), V3(n.x, 0.12f, n.z), lejos(ps[0]), siempre = true)
        }
        // El goterón: una línea oscura bajo el canto (la sombra de la losa) y la chorrera de la lluvia de los riegos.
        val ps = (0..72).map { GeoCentro.aro(RV - 0.01f, y - LOSA + 0.03f, GeoCentro.PA1 + (360f + GeoCentro.PA0 - GeoCentro.PA1) * it / 72f) }
        trazo(v, ps, lerp(C.SOMBRA, C.NEGRO, 0.3f), ABAJO, 0.05f, 0.5f)
    }

    /**
     * La barandilla de vidrio de una galería: el vidrio grueso, casi sin color, con su canto verdoso a la vista arriba y en
     * las juntas; los montantes y el pasamanos de metal gastado, y los reflejos de la luz del hall en algunos paños. Salta el
     * tramo de [salta0] a [salta1] grados (el de cerca, que pinta la esquina).
     */
    fun DrawScope.barandilla(v: VistaCentro, y: Float, desde: Float, hasta: Float) {
        val r = RV - 0.06f; val alto = GeoCentro.BARANDA
        val canto = lerp(C.VIDRIO, C.GRAMA, 0.25f)
        val pasos = tramos(desde, hasta, 3f)
        // Donde pasa una costilla, la barandilla se corta: los paños se sujetan a ella.
        fun enCostilla(g: Float) = GeoCentro.COSTILLAS.any { c -> val d = ((g - c) % 360f + 540f) % 360f - 180f; kotlin.math.abs(d) < 2.4f }
        for ((a, b) in pasos) {
            val gm = (a + b) / 2f
            if (enCostilla(gm)) continue
            val w = GeoCentro.aro(r, y + alto / 2f, gm)
            val f = CamCentro.forma(PiezasCentro.tramoV(r, y, y + alto, a, b)) ?: continue
            val lj = lejos(w)
            // El paño: un velo verde agua muy claro; un paño de cada cinco, con el reflejo del hall.
            drawPath(f, v.c(w, C.VIDRIO, haciaDentro(gm), lj).copy(alpha = 0.16f))
            if (((gm * 7).toInt() % 5) == 0) drawPath(f, lerp(v.fondo, Color.White, 0.5f).copy(alpha = 0.1f), blendMode = BlendMode.Screen)
        }
        // El canto de arriba del vidrio, el pasamanos y los montantes.
        for ((a, b) in pasos) {
            val gm = (a + b) / 2f; val lj = lejos(GeoCentro.aro(r, y, gm))
            if (enCostilla(gm)) continue
            val p0 = GeoCentro.aro(r, y + alto, a); val p1 = GeoCentro.aro(r, y + alto, b)
            val k = CamCentro.k(p0)
            linea(v, p0, p1, canto, ARRIBA, (0.05f * k).coerceIn(0.35f, 3f), 0.85f)
            // El pasamanos: un tubo de metal, gastado (más claro donde se agarra).
            val t0 = GeoCentro.aro(r - 0.02f, y + alto + 0.05f, a); val t1 = GeoCentro.aro(r - 0.02f, y + alto + 0.05f, b)
            linea(v, t0, t1, lerp(C.ACERO, C.LATON, 0.15f), V3(0f, 0.6f, 0f) + haciaDentro(gm), (0.055f * k).coerceIn(0.4f, 4f))
            linea(v, t0 + V3(0f, 0.02f, 0f), t1 + V3(0f, 0.02f, 0f), lerp(C.ACERO, Color.White, 0.45f), ARRIBA, (0.018f * k).coerceIn(0.2f, 1.4f), 0.7f * (1f - lj))
            // Los montantes, cada dos paños.
            if (((a + 720f) / 3f).toInt() % 2 == 0) linea(v, GeoCentro.aro(r, y, a), GeoCentro.aro(r, y + alto, a), lerp(C.ACERO, C.NEGRO, 0.2f), haciaDentro(a), (0.04f * k).coerceIn(0.3f, 3f), 0.9f)
        }
    }

    /** Los extremos de una galería contra la pared de pantallas: el corte de la losa, con su espesor a la vista. */
    private fun DrawScope.extremos(v: VistaCentro, y: Float) {
        for ((g, s) in listOf(GeoCentro.PA0 to 1f, GeoCentro.PA1 to -1f)) {
            val n = V3(-sin(GeoCentro.rad(g)) * s, 0f, cos(GeoCentro.rad(g)) * s)
            val ps = listOf(GeoCentro.aro(RV, y - LOSA, g), GeoCentro.aro(RW, y - LOSA, g), GeoCentro.aro(RW, y, g), GeoCentro.aro(RV, y, g))
            cara(v, ps, lerp(C.HORMIGON, C.TIERRA, 0.15f), n, lejos(ps[0]))
            val vidrio = listOf(GeoCentro.aro(RV - 0.06f, y, g), GeoCentro.aro(RV + 0.6f, y, g), GeoCentro.aro(RV + 0.6f, y + GeoCentro.BARANDA, g), GeoCentro.aro(RV - 0.06f, y + GeoCentro.BARANDA, g))
            CamCentro.forma(vidrio)?.let { drawPath(it, v.c(vidrio[0], C.VIDRIO, n).copy(alpha = 0.3f)) }
        }
    }

    /**
     * Las luces empotradas en la losa de la segunda galería: puntos que brillan vistos por debajo ([deFrente]) o, en el suelo
     * de la primera, sus charcos blandos.
     */
    private fun DrawScope.luces(v: VistaCentro, y: Float, deFrente: Boolean) {
        val color = lerp(C.LUZ_DIA, C.LAMPARA, 0.35f + 0.4f * v.noche)
        val k0 = 0.55f + 0.45f * v.noche
        var g = GeoCentro.PA1 + 6f
        while (g < 360f + GeoCentro.PA0 - 4f) {
            val w = GeoCentro.aro(RV + 1.3f, y, g); val q = CamCentro.p(w)
            if (CamCentro.hondo(w) > (if (deFrente) 2.2f else 0.9f) && CamCentro.enCuadro(q, 4f)) {
                val k = CamCentro.k(w)
                if (deFrente) {
                    val rr = (0.07f * k).coerceIn(0.4f, 1.6f)
                    drawOval(lerp(color, Color.White, 0.5f), q - Offset(rr, rr * 0.45f), androidx.compose.ui.geometry.Size(rr * 2f, rr * 0.9f))
                    mancha(q, rr * 3f, rr * 2f, color, 0.3f * k0)
                } else {
                    // El charco en el suelo: un óvalo aplastado por la fuga.
                    val r = 0.9f * k; val alto = r * ((CamCentro.OJO_Y - y) / CamCentro.hondo(w)).coerceIn(0.1f, 1f)
                    mancha(q, r, alto, color, 0.26f * k0)
                }
            }
            g += 12f
        }
    }

    // =================================================================
    //  Los elevadores
    // =================================================================

    /** El elevador quieto: una cabina de vidrio parada en la primera galería, en el carril de la costilla de [CABINA_QUIETA] grados. */
    const val CABINA_QUIETA = 115f
    /** El que se mueve (parte viva), en la costilla de [CABINA_VIVA] grados, entre la primera y la segunda galería. */
    const val CABINA_VIVA = 70f

    /** Dónde está la cabina de [g] grados a la altura [y] (su suelo): delante de la costilla, en el vacío. */
    fun cabina(g: Float, y: Float): List<V3> {
        val r = GeoCentro.RC - 0.15f; val a = GeoCentro.rad(g)
        val t = V3(-sin(a), 0f, cos(a)); val c = GeoCentro.aro(r - 0.75f, y, g)
        val w = 0.8f; val h = 2.3f; val d = 0.7f
        val cx = cos(a); val cz = sin(a)
        return listOf(c + t * -w + V3(cx * d, 0f, cz * d), c + t * w + V3(cx * d, 0f, cz * d), c + t * w + V3(-cx * d, 0f, -cz * d), c + t * -w + V3(-cx * d, 0f, -cz * d))
            .let { base -> base + base.map { it + V3(0f, h, 0f) } }
    }

    /** El carril de un elevador: dos guías que bajan por la cara de la costilla, del suelo a la segunda galería. */
    fun DrawScope.carril(v: VistaCentro, g: Float) {
        val a = GeoCentro.rad(g); val t = V3(-sin(a), 0f, cos(a)); val r = GeoCentro.RC - 0.04f
        for (s in listOf(-0.5f, 0.5f)) {
            val p0 = GeoCentro.aro(r, 0f, g) + t * s; val p1 = GeoCentro.aro(r, G2 + 1.2f, g) + t * s
            linea(v, p0, p1, lerp(C.ACERO, C.NEGRO, 0.25f), haciaDentro(g), (0.06f * CamCentro.k(p0)).coerceIn(0.4f, 2.5f))
        }
    }

    /**
     * Una cabina de vidrio a [y] metros: el suelo y el techo de metal oscuro, los cuatro montantes, el vidrio con su reflejo,
     * la luz cálida de dentro (que se ve en el suelo de la cabina y en el techo) y el cable que sube.
     */
    fun DrawScope.cabinaDeVidrio(v: VistaCentro, g: Float, y: Float) {
        val e = cabina(g, y)
        val q = e.map { CamCentro.p(it) }
        val lj = lejos(e[0])
        val luz = lerp(C.LUZ_DIA, C.LAMPARA, 0.45f)
        val metal = lerp(C.ACERO, C.NEGRO, 0.45f)
        // Lo de detrás de la cabina, visto por el vidrio: un velo frío; y la luz de dentro, abajo.
        val cuerpo = io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono(envolvente(q))
        drawPath(cuerpo, v.c(e[0], lerp(C.VIDRIO, C.SOMBRA, 0.35f), V3(0f, 0f, -1f), lj).copy(alpha = 0.45f))
        val suelo = io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono(listOf(q[0], q[1], q[2], q[3]))
        drawPath(suelo, lerp(v.c(e[0], luz, ARRIBA, lj), luz, 0.5f))
        drawPath(io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono(listOf(q[4], q[5], q[6], q[7])), v.c(e[4], metal, ABAJO, lj))
        // El borde del suelo y del techo, y los montantes.
        for ((a, b) in listOf(0 to 1, 1 to 2, 2 to 3, 3 to 0)) { drawLine(v.c(e[a], metal, ARRIBA, lj), q[a], q[b], 0.9f); drawLine(v.c(e[a + 4], metal, ARRIBA, lj), q[a + 4], q[b + 4], 0.9f) }
        for (i in 0..3) drawLine(v.c(e[i], metal, ARRIBA, lj), q[i], q[i + 4], 0.7f, StrokeCap.Butt)
        // El reflejo en el vidrio y el halo de la luz de dentro.
        drawLine(Color.White.copy(alpha = 0.35f), Offset(q[4].x + (q[5].x - q[4].x) * 0.3f, q[4].y + 1f), Offset(q[0].x + (q[1].x - q[0].x) * 0.15f, q[0].y - 1f), 0.6f)
        val c = Offset((q[0].x + q[2].x) / 2f, (q[0].y + q[2].y) / 2f)
        mancha(c, (q[1].x - q[0].x).let { kotlin.math.abs(it) } * 0.9f + 2f, 3f, luz, 0.3f)
        // El cable, que sube a la segunda galería.
        val top = Offset((q[4].x + q[6].x) / 2f, (q[4].y + q[6].y) / 2f)
        val alto = CamCentro.p(GeoCentro.aro(GeoCentro.RC - 0.9f, G2 + 1.3f, g))
        drawLine(metal, top, alto, 0.4f)
    }

    @Suppress("unused") private val nada = Offset.Zero
}
