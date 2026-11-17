package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.caja
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.cara
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.palo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo que hay en el patio todos los días **[Diseño propio]**: el patio como sitio de oficio, con lo que dejan quienes
 * trabajan en él. El carro de las patrullas junto a la puerta de la escalera, con su rueda de recambio; las cajas y los
 * sacos de lo que traen; los caballetes de coser la Garza, apilados contra la muralla del fondo hasta que hagan falta; la
 * leña y los sacos de carbón de la fragua; la carretilla de la Cámara; y un par de cabras sueltas, fuera del corral.
 */
internal object CosasDelPatio {
    private val C = ColorOrden
    private const val P = CamOrden.PATIO

    /** Lo del fondo del patio (va con los edificios del fondo). */
    fun DrawScope.delFondo(v: VistaOrden) {
        val libre = EscenaOrden.garza(v.m) != EscenaOrden.Garza.CAIDA && EscenaOrden.garza(v.m) != EscenaOrden.Garza.COSIENDO
        val ocupado = EscenaOrden.caparazonesPintados(v.m) || EscenaOrden.mercado(v.m) || EscenaOrden.caza(v.m) || EscenaOrden.hoyEs(v.m, "anio_nuevo")
        // El taller de la tela de la Garza (cuando ni está tendida ni hay fiesta en el patio): los caballetes con el paño
        // tendido y los rollos; si no, los caballetes apilados contra el fondo.
        if (libre && !ocupado) { taller(v); rollosDeTela(v) } else if (libre) caballetesApilados(v)
        // El carro de las patrullas, junto a la puerta, con su carga.
        carro(v, V3(-12f, P, 53.5f), 0.45f)
        val az = PiezasOrden.fijo("cajas")
        for (i in 0 until 5) caja(v.lz, -3.5f - az.entre(0f, 3f), 55.5f + az.entre(-1f, 1f), az.entre(-0.4f, 0.4f), 0.9f, 0.7f, P + (if (i == 4) 0.6f else 0f), P + (if (i == 4) 1.2f else 0.6f), lerp(C.MADERA, C.ARENA, az.real() * 0.4f), 0.2f)
        for (i in 0 until 4) saco(v, V3(-7.5f - i * 0.7f, P, 57.5f), 0.2f)
    }

    /** Lo de cerca del patio (va después de la Cámara y antes de la cisterna). */
    fun DrawScope.deCerca(v: VistaOrden) {
        lena(v)
        for (i in 0 until 3) saco(v, V3(-8.2f - i * 0.6f, P, 21.9f + i * 0.25f), 0.06f)
        abrevadero(v)
        cabrasSueltas(v)
    }

    /** Los caballetes de coser la Garza, apilados contra la muralla del fondo. */
    private fun DrawScope.caballetesApilados(v: VistaOrden) {
        for (i in 0 until 3) {
            val s = -36f - i * 1.1f; val vv = 38f + i * 0.3f
            palo(v.lz, V3(s - 0.6f, P, vv), V3(s, P + 1.6f, vv - 0.1f), 0.18f, C.MADERA, 0.2f)
            palo(v.lz, V3(s + 0.6f, P, vv), V3(s, P + 1.6f, vv - 0.1f), 0.18f, C.MADERA, 0.2f)
            palo(v.lz, V3(s - 0.5f, P + 1.4f, vv - 0.15f), V3(s + 0.5f, P + 1.4f, vv - 0.15f), 0.2f, C.MADERA_OSC, 0.2f)
        }
    }

    /** Los rollos de tela gris de recambio de la Garza, en su sitio del patio, sobre unas tablas. */
    private fun DrawScope.rollosDeTela(v: VistaOrden) {
        caja(v.lz, -24f, 31f, 0.5f, 2.6f, 1.2f, P, P + 0.15f, C.MADERA, 0.12f)
        for (i in 0 until 3) {
            val a = V3(-25.1f + i * 0.2f, P + 0.45f + (if (i == 2) 0.55f else 0f), 30.4f + i * 0.45f - (if (i == 2) 0.2f else 0f))
            palo(v.lz, a, V3(a.x + 2.2f, a.y, a.z + 0.9f), 0.9f, if (i == 1) C.TELA_VIEJA else C.TELA, 0.12f)
        }
    }

    /** El carro de las patrullas: la caja de tablas, dos ruedas altas de radios y la vara apoyada en el suelo. */
    private fun DrawScope.carro(v: VistaOrden, c: V3, giro: Float) {
        val lz = v.lz; val dx = cos(giro); val dz = -sin(giro); val ex = sin(giro); val ez = cos(giro)
        fun en(x: Float, h: Float, z: Float) = V3(c.x + dx * x + ex * z, c.y + h, c.z + dz * x + ez * z)
        rueda(v, en(-0.85f, 0.6f, 0f), V3(dx, 0f, dz), 0.6f)
        caja(lz, c.x, c.z, giro, 1.5f, 2.6f, P + 0.55f, P + 1.25f, C.MADERA, 0.2f, arriba = C.MADERA_OSC)
        palo(lz, en(0f, 0.7f, -1.3f), en(0f, 0.05f, -3f), 0.12f, C.MADERA_OSC, 0.2f)
        rueda(v, en(0.85f, 0.6f, 0f), V3(dx, 0f, dz), 0.6f)
        // La carga: sacos y un barril, atados.
        for (i in 0 until 3) saco(v, en(-0.35f + i * 0.35f, 1.25f, -0.6f + (i % 2) * 0.5f), 0.2f)
        caja(lz, en(0.3f, 0f, 0.6f).x, en(0.3f, 0f, 0.6f).z, giro, 0.7f, 0.7f, P + 1.25f, P + 2.05f, lerp(C.MADERA, C.MADERA_OSC, 0.4f), 0.2f)
    }

    /** El abrevadero de piedra junto al corral, con el agua y las cabras alrededor. */
    private fun DrawScope.abrevadero(v: VistaOrden) {
        caja(v.lz, -13.9f, 21f, 0f, 0.9f, 2.6f, P, P + 0.65f, lerp(C.PIEDRA, C.ARENA, 0.2f), 0.06f, arriba = lerp(C.VIDRIO, C.HOLLIN, 0.4f))
    }

    /** El taller de la tela: dos caballetes con un paño de la Garza tendido encima, a medio coser, y otro colgando. */
    private fun DrawScope.taller(v: VistaOrden) {
        val lz = v.lz
        for ((s, vv) in listOf(-20.5f to 23.5f, -24.5f to 25.2f)) {
            palo(lz, V3(s, P, vv - 0.5f), V3(s, P + 1.3f, vv), 0.16f, C.MADERA, 0.12f)
            palo(lz, V3(s, P, vv + 0.5f), V3(s, P + 1.3f, vv), 0.16f, C.MADERA, 0.12f)
            palo(lz, V3(s - 0.4f, P + 1.25f, vv), V3(s + 0.4f, P + 1.25f, vv), 0.18f, C.MADERA_OSC, 0.12f)
        }
        val pano = listOf(V3(-19.6f, P + 1.3f, 23.0f), V3(-25.3f, P + 1.3f, 24.7f), V3(-25.4f, P + 0.2f, 25.9f), V3(-19.7f, P + 0.25f, 24.2f))
        cara(lz, pano.take(2) + listOf(V3(-25.3f, P + 1.3f, 25.8f), V3(-19.6f, P + 1.3f, 24.1f)), V3(0f, 1f, 0f), C.TELA, 0.12f)
        cara(lz, listOf(pano[0], pano[1], pano[2], pano[3]), V3(0.2f, 0.3f, -1f), C.TELA_VIEJA, 0.12f)
        drawLine(v.l.tono(C.HOLLIN, 0.12f).copy(alpha = 0.5f), CamOrden.p(-21.5f, P + 1.31f, 23.5f), CamOrden.p(-23.6f, P + 1.31f, 24.2f), 0.5f)
    }

    /** Una rueda de radios, en el plano perpendicular a su [eje]. */
    private fun DrawScope.rueda(v: VistaOrden, c: V3, eje: V3, r: Float) {
        val lado = V3(-eje.z, 0f, eje.x)
        val pts = (0..16).map { j -> val a = j * 6.2832f / 16f; CamOrden.p(c.x + lado.x * r * cos(a), c.y + r * sin(a), c.z + lado.z * r * cos(a)) }
        val camino = Path().apply { moveTo(pts[0].x, pts[0].y); for (q in pts.drop(1)) lineTo(q.x, q.y) }
        val k = CamOrden.k(c.x, c.z); val madera = v.lz.color(C.MADERA_OSC, V3(eje.x, 0.2f, eje.z), 0.2f)
        drawPath(camino, madera, style = Stroke((k * 0.09f).coerceAtLeast(0.6f)))
        for (j in 0 until 4) drawLine(madera, CamOrden.p(c), pts[j * 4], (k * 0.04f).coerceAtLeast(0.4f))
    }

    /** Un saco lleno, de lona, tumbado. */
    private fun DrawScope.saco(v: VistaOrden, pie: V3, lejos: Float) {
        val q = CamOrden.p(pie); val k = CamOrden.k(pie.x, pie.z)
        val forma = Path().apply {
            moveTo(q.x - k * 0.3f, q.y); cubicTo(q.x - k * 0.36f, q.y - k * 0.4f, q.x + k * 0.36f, q.y - k * 0.42f, q.x + k * 0.3f, q.y); close()
        }
        drawPath(forma, v.lz.color(lerp(C.ARENA, C.MADERA, 0.35f), V3(if (v.p.solALaDerecha) 0.6f else -0.6f, 0.6f, -0.6f), lejos))
        drawLine(v.l.tono(C.MADERA_OSC, lejos), q.plus(androidx.compose.ui.geometry.Offset(-k * 0.05f, -k * 0.36f)), q.plus(androidx.compose.ui.geometry.Offset(k * 0.08f, -k * 0.42f)), (k * 0.04f).coerceAtLeast(0.4f), StrokeCap.Round)
    }

    /** La leña de la fragua, apilada: troncos de testa, unos encima de otros, y los sacos de carbón al lado. */
    private fun DrawScope.lena(v: VistaOrden) {
        val lz = v.lz; val s0 = -8f; val vv = 19.8f
        caja(lz, s0 - 0.8f, vv, 0f, 1.7f, 0.9f, P, P + 0.95f, C.MADERA, 0.06f, arriba = lerp(C.MADERA, C.ARENA, 0.3f))
        val az = PiezasOrden.fijo("lena")
        for (fila in 0 until 3) for (i in 0 until 5 - fila) {
            val q = CamOrden.p(s0 - 0.12f - i * 0.32f - fila * 0.16f, P + 0.17f + fila * 0.3f, vv - 0.46f); val k = CamOrden.k(s0, vv)
            drawCircle(lz.color(lerp(C.MADERA, C.ARENA, 0.45f), V3(0f, 0f, -1f), 0.06f), k * az.entre(0.12f, 0.16f), q)
            drawCircle(lz.color(C.MADERA_OSC, V3(0f, 0f, -1f), 0.06f), k * 0.04f, q)
        }
    }

    /** Dos cabras sueltas, de día, fuera del corral: una blanca y una negra. */
    private fun DrawScope.cabrasSueltas(v: VistaOrden) {
        if (v.m.hora < 7.0 || v.m.hora >= 19.5) return
        with(PatioOrden) { cabraSuelta(v, V3(-13.2f, P, 22.7f), false, false); cabraSuelta(v, V3(-14.9f, P, 19.4f), true, true); cabraSuelta(v, V3(-12.6f, P, 20.6f), false, false) }
    }
}
