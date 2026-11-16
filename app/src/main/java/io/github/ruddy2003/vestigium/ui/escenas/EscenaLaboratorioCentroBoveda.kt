package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.cara
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.trazo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * La bóveda del hall, vista desde abajo: un platillo bajo de hormigón pintado de blanco que arranca en la pared, sobre la
 * segunda galería, y se cierra en **el aro del pozo de aire** (`composicion_laboratorio_centro.md`, «Arriba»). Lleva las
 * juntas del encofrado en anillos y en gajos, las chorreras que bajan del pozo, los remiendos de otra pintura; el aro con su
 * **anillo de lámparas «que imitan el día»** (el tramo encendido da la vuelta como un sol: [CentroLab.pozo]); dentro, el pozo
 * encendido y **el ventilador**, enorme y lento (sus aspas giran en la parte viva); y pegada al techo, sobre la pared de
 * pantallas, **la rendija de la Vigía** con el resplandor de sus pantallas, que no se apaga nunca.
 */
internal object BovedaCentro {
    private val C = ColorLaboratorio
    private const val GAJOS = 40
    private const val ANILLOS = 6

    /** El alto del aro del pozo (su canto de dentro) y dónde gira el ventilador. */
    const val ARO_ALTO = 0.6f
    const val VENTILADOR_Y = GeoCentro.YO - 0.5f
    const val ASPAS = 6

    /** La normal de la bóveda (hacia dentro y abajo) en el punto [t] del perfil, a [g] grados. */
    private fun normal(t: Float, g: Float): V3 {
        val h = PI.toFloat() / 2f
        val dr = (GeoCentro.RO - GeoCentro.RW) * sin(t * h) * h; val dy = (GeoCentro.YO - GeoCentro.YB) * cos(t * h) * h
        val a = GeoCentro.rad(g)
        return V3(-dy * cos(a), dr, -dy * sin(a))
    }

    private fun punto(t: Float, g: Float): V3 = GeoCentro.aro(GeoCentro.radioBoveda(t), GeoCentro.altoBoveda(t), g)

    fun DrawScope.boveda(v: VistaCentro) {
        val base = lerp(C.HORMIGON, C.TIERRA, 0.06f)
        // La bóveda, en gajos y anillos: cada pieza con su luz (más clara cerca del pozo, que la alumbra).
        for (i in 0 until GAJOS) {
            val g0 = -180f + 360f * i / GAJOS; val g1 = g0 + 360f / GAJOS
            for (j in 0 until ANILLOS) {
                val t0 = j / ANILLOS.toFloat(); val t1 = (j + 1) / ANILLOS.toFloat()
                val ps = listOf(punto(t0, g0), punto(t0, g1), punto(t1, g1), punto(t1, g0))
                val q = CamCentro.puntos(ps); if (q.size < 3 || q.none { CamCentro.enCuadro(it, 10f) }) continue
                val tm = (t0 + t1) / 2f; val gm = (g0 + g1) / 2f
                // Un gajo de cada tres lleva otra mano de pintura (los remiendos), algo más cálida.
                val propio = if ((i * 7 + j * 3) % 11 == 0) lerp(base, C.TELA, 0.5f) else if ((i + j) % 5 == 0) lerp(base, C.TIERRA, 0.12f) else base
                drawPath(poligono(q), v.c(punto(tm, gm), propio, normal(tm, gm)))
            }
        }
        juntas(v)
        manchas(v)
        with(EstructuraCentro) { costillasEnLaBoveda(v) }
        vigia(v)
        pozo(v)
    }

    /** Las juntas del encofrado: los anillos y los gajos, finos y en sombra. */
    private fun DrawScope.juntas(v: VistaCentro) {
        val junta = lerp(C.SOMBRA, C.HORMIGON, 0.45f)
        for (j in 1 until ANILLOS) {
            val t = j / ANILLOS.toFloat()
            val ps = (0..72).map { punto(t, -180f + 5f * it) }
            trazo(v, ps, junta, normal(t, 90f), 0.035f, 0.55f)
        }
        for (i in 0 until GAJOS step 2) {
            val g = -180f + 360f * i / GAJOS
            trazo(v, (0..ANILLOS).map { punto(it / ANILLOS.toFloat(), g) }, junta, normal(0.5f, g), 0.03f, 0.45f)
        }
    }

    /** Las chorreras que bajan del pozo por la bóveda (la humedad del aire de arriba) y unos desconchones. */
    private fun DrawScope.manchas(v: VistaCentro) {
        val az = Azar(Semilla.de("laboratorio", "centro", "boveda"))
        val sucio = lerp(C.TIERRA, C.SOMBRA, 0.35f)
        repeat(26) {
            val g = az.entre(-20f, 200f); val t0 = az.entre(0.75f, 0.98f); val largo = az.entre(0.15f, 0.45f)
            val ps = (0..5).map { k -> punto((t0 - largo * k / 5f).coerceIn(0f, 1f), g + az.entre(-0.6f, 0.6f)) }
            trazo(v, ps, sucio, normal(t0, g), az.entre(0.04f, 0.1f), az.entre(0.12f, 0.3f))
        }
        repeat(18) {
            val g = az.entre(-30f, 210f); val t = az.entre(0.1f, 0.9f)
            val w = punto(t, g); val q = CamCentro.p(w); if (!CamCentro.enCuadro(q, 0f)) return@repeat
            val k = CamCentro.k(w)
            mancha(q, az.entre(0.3f, 0.8f) * k, az.entre(0.12f, 0.3f) * k, v.c(w, if (az.real() > 0.5f) C.TELA else sucio, normal(t, g)), az.entre(0.25f, 0.5f))
        }
    }

    // ---------------------------------------------------------------- La Vigía

    /**
     * La Vigía, pegada al techo sobre la pared de pantallas: una franja oscura con una rendija larga de vidrio por donde se
     * ve el resplandor de sus pantallas, «que no se apaga nunca».
     */
    private fun DrawScope.vigia(v: VistaCentro) {
        val g0 = GeoCentro.PA0 + 2f; val g1 = GeoCentro.PA1 - 2f
        val y0 = GeoCentro.PY1 + 0.08f; val y1 = GeoCentro.YB - 0.05f; val r = GeoCentro.RW - 0.02f
        val n = 10
        for (i in 0 until n) {
            val a = g0 + (g1 - g0) * i / n; val b = a + (g1 - g0) / n
            cara(v, PiezasCentro.tramoV(r, y0, y1, a, b), lerp(C.ACERO, C.NEGRO, 0.35f), V3(-cos(GeoCentro.rad((a + b) / 2f)), 0f, -sin(GeoCentro.rad((a + b) / 2f))), siempre = true)
        }
        // La rendija: el vidrio con el resplandor de dentro, frío (las pantallas de la Vigía con la noche o el día de arriba).
        val brillo = lerp(lerp(v.colorPantalla, Color(0xFF7FD8E0), 0.45f), Color.White, 0.15f)
        val ry0 = y0 + 0.22f; val ry1 = y1 - 0.18f
        for (i in 0 until n) {
            val a = g0 + 1f + (g1 - g0 - 2f) * i / n; val b = a + (g1 - g0 - 2f) / n - 0.25f
            val f = CamCentro.forma(PiezasCentro.tramoV(r - 0.03f, ry0, ry1, a, b)) ?: continue
            drawPath(f, Brush.verticalGradient(listOf(brillo.copy(alpha = 0.95f), lerp(brillo, C.NEGRO, 0.5f)), startY = CamCentro.p(GeoCentro.aro(r, ry1, a)).y, endY = CamCentro.p(GeoCentro.aro(r, ry0, a)).y))
        }
        val c = CamCentro.p(GeoCentro.aro(r, (ry0 + ry1) / 2f, (g0 + g1) / 2f))
        mancha(c, 70f, 9f, brillo, 0.12f + 0.25f * v.noche)
    }

    // ---------------------------------------------------------------- El pozo

    /** Cuánto luce la lámpara del anillo que está a [g] grados: las del lado de donde viene el haz, más; a mediodía, todas. */
    fun lampara(v: VistaCentro, g: Float): Float {
        val d = v.pozo.d
        val hx = -d.x; val hz = -d.z; val hl = kotlin.math.sqrt(hx * hx + hz * hz)
        val sesgo = (hl / 0.6f).coerceIn(0f, 1f)
        val a = GeoCentro.rad(g)
        val frente = if (hl < 1e-3f) 1f else ((cos(a) * hx + sin(a) * hz) / hl + 1f) / 2f
        return v.pozo.fuerza * (1f - sesgo + sesgo * frente * frente * 1.3f).coerceIn(0.1f, 1.2f)
    }

    /**
     * El aro del pozo: su cara de abajo y su canto de dentro (con espesor), el anillo de lámparas, y por el hueco el pozo
     * encendido, con las vigas que sostienen el ventilador.
     */
    private fun DrawScope.pozo(v: VistaCentro) {
        val ro = GeoCentro.RO; val yo = GeoCentro.YO; val arriba = yo + ARO_ALTO
        val luz = v.pozo.color
        // El hueco: el pozo encendido, del color del haz (es la luz que imita el día), más blanco hacia arriba.
        val hueco = (0 until 48).map { CamCentro.p(GeoCentro.aro(ro - 0.02f, arriba, it * 7.5f)) }
        val brillo = (0.35f + 0.65f * v.pozo.fuerza).coerceIn(0f, 1f)
        val cielo = lerp(lerp(v.fondo, luz, 0.6f), Color.White, 0.25f * brillo)
        val yArr = hueco.minOf { it.y }; val yAb = hueco.maxOf { it.y }
        drawPath(poligono(hueco), Brush.verticalGradient(listOf(lerp(cielo, Color.White, 0.35f * brillo), cielo, lerp(cielo, v.fondo, 0.25f)), startY = yArr, endY = yAb))
        clipPath(poligono(hueco)) {
            // Las rejillas del pozo, que suben: líneas verticales en la pared de dentro, del lado de enfrente.
            for (i in 0 until 24) {
                val g = 15f + 150f * i / 23f
                val a0 = GeoCentro.aro(ro - 0.05f, arriba, g); val a1 = GeoCentro.aro(ro - 0.05f, arriba + 3.5f, g)
                drawLine(lerp(cielo, C.ACERO, 0.35f).copy(alpha = 0.5f), CamCentro.p(a0), CamCentro.p(a1), 0.6f)
            }
        }
        // El canto de dentro del aro: un cilindro corto que mira al eje; se ve el de enfrente.
        val n = 36
        for (i in 0 until n) {
            val g0 = i * 360f / n; val g1 = g0 + 360f / n; val gm = (g0 + g1) / 2f
            val nn = V3(-cos(GeoCentro.rad(gm)), 0f, -sin(GeoCentro.rad(gm)))
            val ps = PiezasCentro.tramoV(ro, yo, arriba, g0, g1)
            if (!CamCentro.seVe(ps[0], nn)) continue
            cara(v, ps, lerp(C.HORMIGON, C.ACERO, 0.25f), nn, siempre = true)
        }
        // La cara de abajo del aro, y el anillo de lámparas en ella: cada una luce según de dónde viene el haz.
        val ancho = 0.75f
        for (i in 0 until n) {
            val g0 = i * 360f / n; val g1 = g0 + 360f / n
            cara(v, PiezasCentro.tramoH(ro, ro + ancho, yo, g0, g1), lerp(C.HORMIGON, C.ACERO, 0.15f), PiezasCentro.ABAJO, siempre = true)
        }
        for (i in 0 until n) {
            val g0 = i * 360f / n + 1.2f; val g1 = g0 + 360f / n - 2.4f; val gm = (g0 + g1) / 2f
            val k = lampara(v, gm)
            val f = CamCentro.forma(PiezasCentro.tramoH(ro + 0.2f, ro + 0.5f, yo - 0.01f, g0, g1)) ?: continue
            drawPath(f, lerp(lerp(v.fondo, C.ACERO, 0.4f), lerp(luz, Color.White, 0.5f), (0.25f + 0.75f * k).coerceIn(0f, 1f)))
        }
        // Los cuatro brazos que bajan del aro y sostienen el ventilador, colgado en la boca del pozo (sus aspas giran en la parte viva).
        for (g in listOf(30f, 120f, 210f, 300f)) {
            val a = GeoCentro.aro(ro + 0.15f, yo - 0.02f, g); val b = GeoCentro.aro(0.6f, VENTILADOR_Y + 0.25f, g)
            PiezasCentro.run { trazo(v, listOf(a, b), lerp(C.ACERO, C.NEGRO, 0.35f), PiezasCentro.ABAJO, 0.12f) }
        }
        PiezasCentro.run { cilindro(v, V3(0f, VENTILADOR_Y - 0.1f, 0f), 0.55f, 0.55f, lerp(C.ACERO, C.NEGRO, 0.3f), null) }
        // El resplandor de las lámparas en la bóveda de alrededor.
        for (i in 0 until 12) {
            val g = i * 30f + 15f; val k = lampara(v, g); if (k < 0.15f) continue
            val w = GeoCentro.aro(ro + 1.1f, GeoCentro.bovedaEn(ro + 1.1f) - 0.02f, g); val q = CamCentro.p(w); if (!CamCentro.enCuadro(q)) continue
            val kk = CamCentro.k(w)
            drawCircle(Brush.radialGradient(listOf(luz.copy(alpha = (0.28f * k).coerceIn(0f, 1f)), Color.Transparent), q, 2.2f * kk), 2.2f * kk, q, blendMode = BlendMode.Plus)
        }
    }

    // ---------------------------------------------------------------- Lo que se mueve

    /** El ángulo de las aspas a la hora del [reloj]: despacio («enorme y lento»); quieto en el apagón. */
    fun giro(v: VistaCentro, reloj: Float): Float = if (CentroLab.ventilador(v.m)) reloj * 0.35f else 0.4f

    /** Las aspas del ventilador del pozo, oscuras contra la luz de arriba, que giran despacio. Solo se ve su mitad de enfrente, por el hueco del aro. */
    fun DrawScope.vivo(v: VistaCentro, reloj: Float) {
        val y = VENTILADOR_Y
        // Vistas por debajo, a contraluz del pozo encendido: oscuras, con el canto de ataque algo más claro.
        val oscuro = lerp(lerp(C.ACERO, C.NEGRO, 0.55f), v.fondo, 0.15f)
        val canto = lerp(oscuro, lerp(v.pozo.color, Color.White, 0.3f), 0.35f)
        val giro = giro(v, reloj)
        for (i in 0 until ASPAS) {
            val ps = aspa(giro + i * 2f * PI.toFloat() / ASPAS, y).map { CamCentro.p(it) }
            if (ps.all { it.y < -2f }) continue
            drawPath(poligono(ps), oscuro)
            drawLine(canto, ps[0], ps[2], 0.6f)
        }
    }

    /** Un aspa del ventilador a [a] radianes, a la altura [y]: ancha cerca del cubo y más fina en la punta, algo curvada. */
    fun aspa(a: Float, y: Float): List<V3> {
        val r0 = 0.5f; val r1 = GeoCentro.RO - 0.25f
        fun q(r: Float, da: Float) = V3(r * cos(a + da), y, r * sin(a + da))
        return listOf(q(r0, -0.32f), q(r1 * 0.6f, -0.2f), q(r1, -0.06f), q(r1, 0.1f), q(r1 * 0.6f, 0.16f), q(r0, 0.22f))
    }
}
