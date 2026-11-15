package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.EJE
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.PALO_MAYOR
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.REGALA
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.RODA
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.TRINQUETE
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.Vista
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.estribor
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.babor
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.pc
import io.github.ruddy2003.vestigium.ui.escenas.EscenaBotanico.Velas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Las velas y la jarcia del bergantín ([BarcoBotanico]): el aparejo **[Aproximación]** de un bergantín de 27 metros (la
 * ficha dice «bergantín»; no se comprobó en la sesión cómo iba aparejado el barco real).
 *
 * Cada vela se arma en metros (sus cuatro puños y su panza, [Vela]) y se pinta como lona, no como papel ([lona]): los
 * paños cosidos con su costura y su doblez de luz, cada uno de su tanda; la panza, que cambia de luz a lo largo de la
 * vela según hacia dónde mire cada trozo; la relinga; los rizos con sus tomadores en la cangreja; los remiendos, las
 * manchas de humedad y de roce; y encima **la sombra de la jarcia, del palo y de la otra vela**, proyectadas con el
 * mismo sol que da las sombras de la cubierta. La cara que da la espalda al sol se queda en azul violeta, pero la lona
 * deja pasar algo de luz tibia; de noche la luna platea la cara que la mira y la claraboya enciende desde abajo el pie
 * de la cangreja y la botavara.
 *
 * Con viento largo el bergantín lleva la mayor de cruz (que se ve por su pie, arriba), la cangreja cazada a babor (el
 * marco de arriba a la izquierda) y, lejos, el trinquete y los foques; fondeado o con tormenta, todo aferrado, y se ve
 * más cielo y más jarcia. Las vergas quedan por encima del cuadro: arriba se ven las brazas, los brandales, los
 * obenques, la escota con sus motones, las drizas y el amantillo de la botavara.
 */
internal object VelasBarco {
    private val C = ColorBotanico

    // ---------------------------------------------------------------- Cuentas con puntos de tres dimensiones

    private fun dot(a: V3, b: V3) = a.x * b.x + a.y * b.y + a.z * b.z
    private fun cruz(a: V3, b: V3) = V3(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x)
    private fun unidad(a: V3): V3 { val l = sqrt(dot(a, a)).coerceAtLeast(1e-5f); return V3(a.x / l, a.y / l, a.z / l) }
    private val OJOS = V3(0f, BarcoBotanico.OJO, 0f)

    // ---------------------------------------------------------------- Las velas en metros

    /**
     * Una vela por sus cuatro puños, en metros del barco: [a] y [b], abajo (amura y escota, o los dos puños de una vela
     * de cruz, de babor a estribor); [c] y [d], arriba (el pico y la boca, o los penoles de la verga). [panza]: hacia
     * dónde y cuánto se hincha, en metros. [pie]: cuánto sube el pie en medio (el alunamiento de una vela de cruz).
     */
    internal class Vela(val a: V3, val b: V3, val c: V3, val d: V3, val panza: V3, val pie: Float = 0f) {
        fun en(u: Float, v: Float): V3 {
            val ab = a + (b - a) * u; val dc = d + (c - d) * u; val q = ab + (dc - ab) * v
            val bolsa = 4f * u * (1f - u) * (0.3f + 0.7f * sin(PI.toFloat() * (0.12f + 0.82f * v)))
            return q + panza * bolsa + V3(0f, pie * 4f * u * (1f - u) * (1f - v) * (1f - v), 0f)
        }
        val esquinas: List<V3> get() = listOf(a, b, c, d)
    }

    private fun mayorDe(caida: Boolean): Vela {
        // La verga, muy arriba (fuera del cuadro), bracead: el penol de babor más a popa. El pie, a 4.9 m, más ancho que la verga.
        val b = 0.3f; val zc = PALO_MAYOR + 0.5f
        fun en(d: Float, h: Float, atras: Float = 0f) = V3(EJE + d * cos(b), h, zc + d * sin(b) - atras)
        return Vela(en(-6.8f, 5.3f, 0.6f), en(6.8f, 5.3f, 0.6f), en(6.3f, 13.2f), en(-6.3f, 13.2f), V3(-sin(b), 0f, cos(b)) * (if (caida) 0.25f else 1.35f), pie = if (caida) 0.15f else 0.55f)
    }

    /** La cangreja: del palo hacia popa, cazada a babor; la botavara, larga, sale por arriba a la izquierda. */
    private const val PHI = 0.4f
    private fun cangrejaDe(caida: Boolean): Vela {
        val phi = if (caida) 0.22f else PHI; val z0 = PALO_MAYOR - 0.38f
        val escota = V3(EJE - 10.4f * sin(phi), 3.85f, z0 - 10.4f * cos(phi))
        val gaf = phi + 0.08f; val lg = 8f * cos(0.62f)
        val pico = V3(EJE - lg * sin(gaf), 11f + 8f * sin(0.62f), z0 - lg * cos(gaf))
        return Vela(V3(EJE, 3.42f, z0), escota, pico, V3(EJE, 11f, z0), V3(-cos(phi), 0f, sin(phi)) * (if (caida) 0.2f else 0.8f))
    }

    /** La botavara: del palo al puño de escota de la cangreja (y un poco más). */
    private fun botavara(caida: Boolean): Pair<V3, V3> { val c = cangrejaDe(caida); return V3(EJE, 3.3f, PALO_MAYOR - 0.42f) to (c.b + (c.b - c.a) * 0.06f - V3(0f, 0.1f, 0f)) }

    /** Los dos foques, del bauprés al estay del trinquete, cazados a babor. */
    private fun foques(caida: Boolean): List<Vela> {
        val lado = if (caida) 0.3f else 1.2f
        val f1 = Vela(V3(EJE, 3.95f, RODA + 5.5f), V3(EJE - lado, 5.6f, RODA - 1.6f), V3(EJE - 0.2f, 15.5f, TRINQUETE + 3f), V3(EJE, 15.6f, TRINQUETE + 3.1f), V3(-1f, 0f, 0f) * (if (caida) 0.1f else 0.6f))
        val f2 = Vela(V3(EJE, 4.6f, RODA + 9.5f), V3(EJE - lado * 1.3f, 6.4f, RODA + 1.2f), V3(EJE - 0.2f, 18.5f, TRINQUETE + 2f), V3(EJE, 18.6f, TRINQUETE + 2.1f), V3(-1f, 0f, 0f) * (if (caida) 0.1f else 0.5f))
        return listOf(f2, f1)
    }

    /** Las velas que dan sombra en la cubierta hoy (sus esquinas, en metros): solo las largas o caídas, no las aferradas. */
    fun velasQueDanSombra(m: Momento): List<List<V3>> {
        val v = EscenaBotanico.velas(m); if (v == Velas.AFERRADAS) return emptyList()
        val caida = v == Velas.CAIDAS
        return listOf(mayorDe(caida).esquinas, cangrejaDe(caida).esquinas)
    }

    // ---------------------------------------------------------------- La jarcia en metros (lo que da sombra en las velas y la cubierta)

    private val TOPE_MAYOR = V3(EJE, 16.2f, PALO_MAYOR)
    private val TOPE_TRINQUETE = V3(EJE, 15.2f, TRINQUETE)
    private val MASTELERO_MAYOR = V3(EJE, 23f, PALO_MAYOR + 0.1f)

    /** Los obenques de una banda del palo mayor: del pie (en la mesa de guarnición, por fuera de la regala) a la cabeza. */
    internal fun obenquesMayor(estriborLado: Boolean): List<Pair<V3, V3>> = (0 until 5).map { i ->
        val z = PALO_MAYOR - 1.4f + 2.8f * i / 4f; val x = if (estriborLado) estribor(z) - 0.1f else babor(z) + 0.1f
        V3(x, 1.25f, z) to V3(EJE + (if (estriborLado) 0.25f else -0.25f), 15.8f, PALO_MAYOR)
    }

    /** Los brandales del mastelero mayor: de lo alto a las mesas de guarnición, más a popa que los obenques. */
    internal fun brandales(): List<Pair<V3, V3>> = listOf(V3(estribor(8.3f) + 0.1f, 1.25f, 8.3f) to MASTELERO_MAYOR, V3(estribor(9f) + 0.1f, 1.25f, 9f) to MASTELERO_MAYOR, V3(babor(8.5f) - 0.1f, 1.25f, 8.5f) to MASTELERO_MAYOR)

    /** La jarcia que cruza el cielo y da sombra: obenques, brandales, el palo y el estay mayor. */
    private fun jarcia(): List<Pair<V3, V3>> = obenquesMayor(true) + obenquesMayor(false) + brandales() +
        listOf(V3(EJE, 0f, PALO_MAYOR) to V3(EJE, 23f, PALO_MAYOR), V3(EJE, 16f, PALO_MAYOR + 0.2f) to V3(EJE, 1.6f, TRINQUETE - 0.6f))

    /** La sombra de la jarcia en la cubierta, en metros (x, z) de punta a punta, para la capa de sombras. */
    fun sombraDeLaJarcia(sol: Sol3): List<Pair<Offset, Offset>> = jarcia().map { (a, b) -> BarcoBotanico.alSuelo(sol, a) to BarcoBotanico.alSuelo(sol, b) }

    // ---------------------------------------------------------------- La lona

    /** La normal de la vela en ([u], [v]), vuelta hacia los ojos. Para la luz se exagera la panza: así se lee la curva. */
    private fun normal(vela: Vela, u: Float, v: Float): V3 {
        val s = Vela(vela.a, vela.b, vela.c, vela.d, vela.panza * 2.8f, vela.pie)
        val du = s.en((u + 0.03f).coerceAtMost(1f), v) - s.en((u - 0.03f).coerceAtLeast(0f), v)
        val dv = s.en(u, (v + 0.03f).coerceAtMost(1f)) - s.en(u, (v - 0.03f).coerceAtLeast(0f))
        val n = unidad(cruz(du, dv)); val q = vela.en(u, v)
        return if (dot(n, OJOS - q) < 0f) V3(-n.x, -n.y, -n.z) else n
    }

    /**
     * El color de la lona en un trozo de normal [n]: al sol, crema; de espaldas al sol, azul violeta, con la luz tibia que
     * la atraviesa; de noche, oscura, con la cara que mira a la luna plateada.
     */
    private fun colorLona(v: Vista, n: V3, base: Color): Color {
        val t = v.sol.t(n)
        var c = if (t >= 0f) v.p.por(base, 0.1f + 0.85f * t) else v.p.por(base, t)
        if (t < 0f && v.r.dia > 0.2f) c = lerp(c, v.l.tono(lerp(base, lerp(Color(0xFFFFE2A8), v.l.solColor, 0.45f), 0.5f)), (0.5f * -t * v.r.fuerza / 0.72f).coerceIn(0f, 0.45f))
        val noche = v.l.noche
        if (noche > 0.3f) {
            // De noche la lona es oscura (azul violeta); la luna platea lo que la mira.
            c = lerp(c, mult(c, Color(0xFF6A66A6), 0.7f), 0.5f * noche)
            if (v.luna > 0.05f && t > 0f) c = lerp(c, lerp(v.l.tono(base), EscenaBotanico.LUZ_DE_LUNA, 0.55f), (0.85f * t * v.luna).coerceIn(0f, 0.55f))
        }
        return c
    }

    /** El contorno de una vela en el lienzo. */
    private fun contorno(vela: Vela): Path {
        val n = 14
        val pts = (0..n).map { pc(vela.en(it / n.toFloat(), 0f)) } + (0..n).map { pc(vela.en(1f, it / n.toFloat())) } + (0..n).map { pc(vela.en(1f - it / n.toFloat(), 1f)) } + (0..n).map { pc(vela.en(0f, 1f - it / n.toFloat())) }
        return poligono(pts)
    }

    /** La jarcia que da sombra en las velas: la de [jarcia] y, además, la braza y la escota de la mayor, el amantillo y las drizas de la cangreja. */
    private fun jarciaParaVelas(v: Vista): List<Pair<V3, V3>> {
        val caida = v.velas == Velas.CAIDAS; val may = mayorDe(caida); val cang = cangrejaDe(caida); val (_, fin) = botavara(caida)
        val motonB = V3(estribor(5.6f) + 0.05f, 3.4f, 5.6f)
        return jarcia() + listOf(may.c + V3(0.4f, 0f, 0f) to motonB, motonB to V3(estribor(4.4f) - 0.05f, 0.9f, 4.4f), may.b to V3(estribor(3.75f) - 0.05f, REGALA + 0.04f, 3.75f),
            fin to V3(EJE, 15.6f, PALO_MAYOR - 0.2f), cang.c + (cang.d - cang.c) * 0.35f to V3(EJE, 17f, PALO_MAYOR), cang.c + (cang.d - cang.c) * 0.75f to V3(EJE, 16.5f, PALO_MAYOR))
    }

    /**
     * Pinta una vela como lona. [panos]: cuántos paños; [rizos]: las fajas de rizos paralelas al pie (la cangreja);
     * [tapan]: las velas que le echan sombra; [lejos], la bruma.
     */
    private fun DrawScope.lona(v: Vista, vela: Vela, panos: Int, rizos: Int, az: Azar, tapan: List<Vela> = emptyList(), lejos: Float = 0f) {
        val forma = contorno(vela)
        val base = lerp(C.LONA, C.ARENA, 0.1f)
        val p = v.p; val noche = v.l.noche
        clipPath(forma) {
            // Cada paño, de su tanda: con el degradado de la panza a lo alto (según hacia dónde mira cada trozo).
            for (k in 0 until panos) {
                val u0 = k / panos.toFloat(); val u1 = (k + 1) / panos.toFloat(); val um = (u0 + u1) / 2f
                val tanda = lerp(base, if (k % 3 == 0) C.ARENA else if (k % 3 == 1) lerp(C.LONA, Color.White, 0.4f) else lerp(C.LONA, C.ROCA, 0.35f), az.entre(0.04f, 0.2f))
                val ps = (0..8).map { pc(vela.en(u0, it / 8f)) } + (8 downTo 0).map { pc(vela.en(u1 + 0.002f, it / 8f)) }
                val colores = listOf(0.02f, 0.22f, 0.45f, 0.7f, 0.96f).map { vv -> val c = colorLona(v, normal(vela, um, vv), tanda); if (lejos > 0f) lerp(c, v.l.bruma, 0.4f * lejos) else c }
                drawPath(poligono(ps), Brush.linearGradient(colores, pc(vela.en(um, 0f)), pc(vela.en(um, 1f))))
                // A lo ancho del paño, la luz cambia también: el borde que da a la costura, un poco más oscuro.
                val ps2 = (0..8).map { pc(vela.en(u0, it / 8f)) } + (8 downTo 0).map { pc(vela.en(u0 + (u1 - u0) * 0.22f, it / 8f)) }
                drawPath(poligono(ps2), p.sombra(tanda).copy(alpha = 0.1f * (1f - 0.5f * noche)))
            }
            // Las arrugas de tensión: abanicos largos que salen de los puños (más claros y más oscuros, alternados).
            for ((desde, hacia) in listOf(Offset(1f, 0f) to listOf(0.1f to 0.95f, 0.3f to 0.85f, 0.55f to 0.9f), Offset(0f, 0f) to listOf(0.4f to 0.6f, 0.65f to 0.8f), Offset(1f, 1f) to listOf(0.2f to 0.2f, 0.45f to 0.1f))) {
                for ((i, h) in hacia.withIndex()) {
                    val a = pc(vela.en(desde.x, desde.y)); val b = pc(vela.en(h.first, h.second)); val n = (b - a).let { Offset(-it.y, it.x) / it.getDistance().coerceAtLeast(1f) }
                    val ancho = (b - a).getDistance() * 0.035f
                    drawPath(poligono(a, b + n * ancho, b - n * ancho), (if (i % 2 == 0) p.sombra(base) else p.luz(base)).copy(alpha = 0.13f * (1f - 0.5f * noche)))
                }
            }
            // Las costuras: la junta oscura, el pespunte y, al lado, el doblez que toma la luz.
            val g = 0.55f + 0.45f * (1f - lejos)
            for (k in 1 until panos) {
                val u = k / panos.toFloat()
                val linea = (0..12).map { pc(vela.en(u, it / 12f)) }
                drawPath(Path().apply { moveTo(linea[0]); for (q in linea.drop(1)) lineTo(q) }, p.sombra(lerp(base, C.CANAMO, 0.4f)).copy(alpha = 0.55f), style = Stroke(g))
                drawPath(Path().apply { moveTo(linea[0] + Offset(0.9f, 0f)); for (q in linea.drop(1)) lineTo(q + Offset(0.9f, 0f)) }, p.luz(base).copy(alpha = 0.4f * (1f - 0.6f * noche)), style = Stroke(g * 0.7f))
                if (lejos < 0.2f) drawPath(Path().apply { moveTo(linea[0] + Offset(-0.9f, 0f)); for (q in linea.drop(1)) lineTo(q + Offset(-0.9f, 0f)) }, p.sombra(base).copy(alpha = 0.28f), style = Stroke(0.35f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.2f, 1.3f))))
            }
            // La panza: lo hondo, en sombra.
            mancha(pc(vela.en(0.42f, 0.4f)), (pc(vela.en(0.9f, 0.4f)) - pc(vela.en(0.1f, 0.4f))).getDistance() * 0.3f, 10f + (pc(vela.en(0.5f, 0.8f)) - pc(vela.en(0.5f, 0.1f))).getDistance() * 0.2f, p.sombra(base), 0.22f)
            // Los refuerzos de los puños: triángulos de lona doble en cada esquina, con sus costuras.
            for ((u, vv) in listOf(0f to 0f, 1f to 0f, 1f to 1f, 0f to 1f)) {
                val c0 = pc(vela.en(u, vv)); val c1 = pc(vela.en(if (u == 0f) 0.08f else 0.92f, vv)); val c2 = pc(vela.en(u, if (vv == 0f) 0.1f else 0.9f))
                drawPath(poligono(c0, c1, c2), p.sombra(base).copy(alpha = 0.2f)); drawLine(p.sombra(lerp(base, C.CANAMO, 0.4f)).copy(alpha = 0.5f), c1, c2, 0.6f)
            }
            // Los remiendos: un trozo de lona más nueva con su pespunte.
            repeat(4) {
                val u = az.entre(0.15f, 0.8f); val vv = az.entre(0.1f, 0.65f); val du = az.entre(0.05f, 0.1f); val dv = az.entre(0.05f, 0.1f)
                val q = listOf(vela.en(u, vv), vela.en(u + du, vv), vela.en(u + du, vv + dv), vela.en(u, vv + dv)).map { pc(it) }
                drawPath(poligono(q), lerp(colorLona(v, normal(vela, u, vv), lerp(C.LONA, Color.White, 0.5f)), Color.White, 0.04f).copy(alpha = 0.9f))
                drawPath(poligono(q), p.sombra(base).copy(alpha = 0.45f), style = Stroke(0.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1f, 1f))))
            }
            // La humedad y el roce: manchas grises cerca del pie y donde roza la jarcia; el moho de arriba.
            repeat(8) { val q = pc(vela.en(az.entre(0.05f, 0.95f), az.entre(0f, 0.35f))); mancha(q, az.entre(6f, 24f), az.entre(3f, 10f), p.sombra(lerp(C.LONA, C.ROCA, 0.5f)), 0.16f) }
            repeat(5) { val q = pc(vela.en(az.entre(0.1f, 0.9f), az.entre(0.4f, 0.95f))); mancha(q, az.entre(10f, 30f), az.entre(4f, 12f), p.sombra(lerp(C.LONA, C.CUERO, 0.3f)), 0.1f) }
            // Los rizos: la faja de refuerzo y la fila de tomadores, colgando.
            for (r in 1..rizos) {
                val vv = 0.11f * r
                val fila = (0..24).map { pc(vela.en(it / 24f, vv)) }
                drawPath(Path().apply { moveTo(fila[0]); for (q in fila.drop(1)) lineTo(q) }, p.sombra(base).copy(alpha = 0.45f), style = Stroke(2.2f))
                drawPath(Path().apply { moveTo(fila[0] + Offset(0f, -1.2f)); for (q in fila.drop(1)) lineTo(q + Offset(0f, -1.2f)) }, p.luz(base).copy(alpha = 0.4f * (1f - 0.6f * noche)), style = Stroke(0.8f))
                for (k in 1 until 24) { val q = fila[k]; val largo = 4f + az.entre(0f, 3f); drawLine(p.sombra(C.CANAMO).copy(alpha = 0.85f), q, q + Offset(az.entre(-0.8f, 0.8f), largo), 0.7f, StrokeCap.Round); drawCircle(p.medio(C.CANAMO), 0.75f, q) }
            }
            // La sombra de la jarcia y de las otras velas, con el mismo sol (o la luna) que la cubierta.
            if (v.r.fuerza > 0.05f) {
                val n0 = normal(vela, 0.5f, 0.5f); val q0 = vela.en(0.5f, 0.5f); val d = V3(v.sol.sx, -1f, v.sol.sz)
                val dn = dot(d, n0)
                if (kotlin.math.abs(dn) > 0.05f) {
                    fun sobre(a: V3): Offset? { val s = dot(q0 - a, n0) / dn; return if (s <= 0.05f) null else pc(a + d * s) }
                    val sombra = mult(Color(0xFFE8E0D0), v.r.color, 0.95f).copy(alpha = (v.r.fuerza * 0.95f).coerceIn(0f, 1f))
                    for ((a, b) in jarciaParaVelas(v)) {
                        val pa = sobre(a); val pb = sobre(b); if (pa == null || pb == null) continue
                        drawLine(sombra, pa, pb, (BarcoBotanico.k(((a.z + b.z) / 2f).coerceAtLeast(2f)) * 0.05f).coerceIn(0.8f, 3f), StrokeCap.Round, blendMode = BlendMode.Multiply)
                    }
                    for (otra in tapan) {
                        val q = otra.esquinas.map { sobre(it) }
                        if (q.any { it == null }) continue
                        drawPath(poligono(q.filterNotNull()), sombra.copy(alpha = sombra.alpha * 0.7f), blendMode = BlendMode.Multiply)
                    }
                }
            }
            // El sol detrás de la vela: la lona se enciende donde está el disco.
            val sol = Offset(v.l.solX * ANCHO, BarcoBotanico.H * (1f - v.l.solAltura))
            if (v.l.solAltura in 0.05f..1.05f && v.l.solFuerza > 0.3f && v.l.noche < 0.4f) resplandor(sol, 80f, 0.55f * v.l.solFuerza * (1f - v.l.noche), lerp(Color(0xFFFFE2A8), v.l.solColor, 0.4f))
        }
        // La relinga: el cabo cosido a la orilla, con su luz.
        drawPath(forma, p.sombra(lerp(C.LONA, C.CANAMO, 0.65f)).copy(alpha = 0.85f), style = Stroke(1.6f - 0.7f * lejos, join = StrokeJoin.Round))
        drawPath(forma, p.luz(lerp(C.LONA, C.CANAMO, 0.3f)).copy(alpha = 0.35f * (1f - lejos) * (1f - 0.6f * noche)), style = Stroke(0.6f, join = StrokeJoin.Round))
    }

    // ---------------------------------------------------------------- Lo lejano: la proa y el trinquete

    /** El bauprés con su botalón, los estayes del trinquete, el trinquete con su jarcia, su vela y los foques. */
    fun DrawScope.lejos(v: Vista) {
        val m = v.m; val p = v.p; val caida = v.velas == Velas.CAIDAS; val largas = v.velas != Velas.AFERRADAS
        with(PiezasBotanico) {
            val a = pc(EJE, 1.8f, RODA - 0.2f); val b = pc(EJE, 4.3f, RODA + 7f); val c = pc(EJE, 5.4f, RODA + 11f)
            palito(p, a, b, 2.2f, 1.4f, C.MADERA, 0.25f)
            palito(p, b, c, 1.2f, 0.7f, C.MADERA, 0.3f)
            // El moco y su barbiquejo, y los estayes que suben del bauprés a los topes.
            drawLine(p.sombra(C.BREA, 0.3f), pc(EJE, 3.2f, RODA + 6f), pc(EJE, 0.6f, RODA + 2f), 0.5f)
            drawLine(p.sombra(C.BREA, 0.3f), c, pc(TOPE_TRINQUETE.x, 18.6f, TRINQUETE + 0.2f), 0.6f); drawLine(p.sombra(C.BREA, 0.3f), b, pc(TOPE_TRINQUETE), 0.7f)
            // El trinquete, con su cofa fuera del cuadro y sus obenques.
            val tri = PiezasBotanico.Palo(BarcoBotanico.SITIO_TRINQUETE, Semilla.de("botanico", "trinquete"), 0.24f, 15f, nieve = v.nieve)
            obenques(tri, p, -3.4f, 1.25f, 13.5f, -1.2f, 1.2f, 4, v.nieve, 0.3f)
            // La vela de trinquete va cargada (con viento largo la mayor le quita el viento) **[Aproximación]**: se ven el palo,
            // su jarcia y los foques, y el cielo entre ellos.
            palo(tri, p, v.sol)
            obenques(tri, p, 3.45f, 1.25f, 13.5f, -1.2f, 1.2f, 4, v.nieve, 0.3f)
            if (largas) for ((i, f) in foques(caida).withIndex()) lona(v, f, 6, 0, Azar(Semilla.de("botanico", "foque", i)), lejos = 0.32f)
            else velaAferrada(p, a + (b - a) * 0.4f, b, 2.4f, Azar(Semilla.de("botanico", "foque aferrado")), 0.25f, v.nieve)
            // El estay mayor, del tope mayor al pie del trinquete.
            drawLine(p.sombra(C.BREA, 0.15f), pc(EJE, 16f, PALO_MAYOR + 0.2f), pc(EJE, 1.6f, TRINQUETE - 0.6f), 0.9f)
            drawLine(p.sombra(C.BREA, 0.15f), pc(EJE, 16f, PALO_MAYOR + 0.3f), pc(EJE, 2.0f, TRINQUETE - 0.5f), 0.6f)
            // El empavesado de proa: del botalón al tope del trinquete.
            if (EscenaBotanico.fiesta(m)) banderas(v, c, pc(EJE, 15.6f, TRINQUETE), 4f, 12, 0.25f)
        }
    }

    // ---------------------------------------------------------------- La mayor de cruz

    /** La mayor de cruz, si va larga: se ve su pie, arriba del cuadro, con la sombra del palo y de la jarcia. */
    fun DrawScope.mayor(v: Vista) {
        if (v.velas == Velas.AFERRADAS) return
        val caida = v.velas == Velas.CAIDAS
        val tapan = if (v.sol.sx > 0.05f) listOf(cangrejaDe(caida)) else emptyList()
        lona(v, mayorDe(caida), 14, 0, Azar(Semilla.de("botanico", "vela mayor")), tapan = tapan)
        // Las patas de bolina en las relingas de caída y los puños con su garrucho.
        val vela = mayorDe(caida); val p = v.p
        for (u in listOf(0f, 1f)) for (vv in listOf(0.18f, 0.32f)) { val q = pc(vela.en(u, vv)); drawLine(p.sombra(C.CANAMO), q, q + Offset(if (u == 0f) -6f else 6f, 4f), 0.6f) }
        for (u in listOf(0f, 1f)) { val q = pc(vela.en(u, 0f)); drawCircle(p.sombra(C.HIERRO), 1.6f, q, style = Stroke(0.7f)) }
    }

    // ---------------------------------------------------------------- El marco: la cangreja, la botavara y la jarcia de cerca

    /**
     * Lo que enmarca por arriba y cruza: la cangreja con su botavara (arriba a la izquierda, cortada por los bordes), o la
     * cangreja aferrada sobre la botavara; los brandales y las brazas que bajan por el cielo; la escota de la mayor con
     * sus motones, que cruza el cielo hasta la regala; el amantillo; y el empavesado de las fiestas. Nada tapa la ventana
     * de los astros (x de 270 a 330, y de 45 a 110).
     */
    fun DrawScope.marco(v: Vista) {
        val m = v.m; val p = v.p; val caida = v.velas == Velas.CAIDAS
        val az = Azar(Semilla.de("botanico", "velas"))
        with(PiezasBotanico) {
            // Los brandales de estribor y la braza de la mayor: bajan del cielo a la regala y a la aleta.
            for ((a, b) in brandales().take(2)) { val pa = pc(b); val pb = pc(a); drawLine(p.sombra(C.BREA), pa, pb, 1.1f); drawLine(p.luz(lerp(C.BREA, C.LONA, 0.35f)).copy(alpha = 0.4f), pa + Offset(-0.4f, 0f), pb + Offset(-0.4f, 0f), 0.4f) }
            if (v.velas != Velas.AFERRADAS) {
                val vela = mayorDe(caida)
                // La braza de estribor: del penol (fuera del cuadro) a un motón en el brandal y de ahí a la cabilla de la aleta.
                val penol = vela.c + V3(0.4f, 0f, 0f); val motonB = V3(estribor(5.6f) + 0.05f, 3.4f, 5.6f); val cabilla = V3(estribor(4.4f) - 0.05f, 0.9f, 4.4f)
                cabo(p, pc(penol), pc(motonB), 2f, 0.9f); moton(p, pc(motonB), 4.2f, 0.12f); cabo(p, pc(motonB), pc(cabilla), 1f, 0.9f)
                // La escota de estribor de la mayor: del puño baja por su aparejo hasta la regala, cerca de quien mira.
                escota(p, pc(vela.b), pc(estribor(3.75f) - 0.05f, REGALA + 0.04f, 3.75f), 7.5f)
            }
            if (v.velas == Velas.LARGAS || v.velas == Velas.CAIDAS) {
                val cang = cangrejaDe(caida); val (boca, fin) = botavara(caida)
                val tapan = if (v.sol.sx < -0.05f) emptyList<Vela>() else emptyList()
                lona(v, cang, 12, 2, Azar(Semilla.de("botanico", "cangreja")), tapan = tapan)
                // El pico, la boca de cangreja contra el palo y la driza de pico que sube.
                palito(p, pc(cang.d), pc(cang.c), 3f, 6f, C.MADERA, 0f, listOf(0.5f))
                drawLine(p.sombra(C.CANAMO), pc(cang.c + (cang.d - cang.c) * 0.35f), pc(EJE, 17f, PALO_MAYOR), 0.9f)
                drawLine(p.sombra(C.CANAMO), pc(cang.c + (cang.d - cang.c) * 0.75f), pc(EJE, 16.5f, PALO_MAYOR), 0.9f)
                // La botavara, gruesa y cercana, con sus zunchos; el amantillo que sube de su penol.
                palito(p, pc(boca), pc(fin), 4.4f, 11f, C.MADERA, 0f, listOf(0.22f, 0.55f))
                drawLine(p.sombra(C.CANAMO), pc(fin + V3(0f, 0.1f, 0f)), pc(EJE, 15.6f, PALO_MAYOR - 0.2f), 1.4f)
                // Los garruchos de la relinga de grátil, contra el palo.
                for (k in 0..6) { val q = pc(cang.a + (cang.d - cang.a) * (k / 6f)); drawOval(p.por(C.MADERA_OSC, 0.2f), Offset(q.x - 2.4f, q.y - 0.9f), Size(4.8f, 1.8f)) }
            } else {
                // Fondeado o con tormenta: la cangreja aferrada sobre la botavara, en crujía, y la botavara que sale por arriba a la izquierda.
                val boca = V3(EJE, 3.25f, PALO_MAYOR - 0.4f); val fin = V3(EJE + 0.05f, 3.6f, 1.6f)
                velaAferrada(p, pc(boca + (fin - boca) * 0.04f), pc(boca + (fin - boca) * 0.86f), 9f, az, 0f, v.nieve)
                palito(p, pc(boca), pc(fin), 4.4f, 12f, C.MADERA, 0f, listOf(0.3f, 0.7f))
                // La horquilla que la sostiene y el amantillo.
                drawLine(p.sombra(C.CANAMO), pc(fin + (boca - fin) * 0.1f), pc(EJE, 15.6f, PALO_MAYOR - 0.2f), 1.2f)
            }
            if (EscenaBotanico.fiesta(m)) banderas(v, pc(EJE, 15f, PALO_MAYOR), pc(estribor(5.5f) + 0.1f, REGALA + 0.05f, 5.5f), 8f, 22, 0f)
        }
    }

    /**
     * El empavesado: banderas de señales (rayas, cruces, cuarteles; sin letras) en un cabo de [a] a [b], que cuelga
     * [caida]. Cada una con su luz y su sombra, y alguna torcida por el viento.
     */
    private fun DrawScope.banderas(v: Vista, a: Offset, b: Offset, caida: Float, n: Int, lejos: Float) {
        val p = v.p
        val pts = combada(a, b, caida, n)
        drawPath(curva(pts), p.sombra(C.CANAMO, lejos), style = Stroke(0.6f))
        val colores = listOf(C.TEJA, C.LONA, lerp(C.MAR, C.BREA, 0.3f), C.LATON, C.BREA, C.TEJA, C.LONA, C.LATON, C.ESCARABAJO)
        for (i in 1 until pts.size - 1) {
            if (pts[i].y < -4f || pts[i].x > ANCHO + 4f) continue
            val q = pts[i]; val w = (2.4f + 6f * ((q.y + 20f) / 230f)).coerceIn(2f, 8f); val h = w * 0.8f; val c = colores[i % colores.size]; val c2 = colores[(i + 4) % colores.size]
            val vuelo = sin(i * 1.7f) * w * 0.25f
            val forma = poligono(Offset(q.x - w / 2f, q.y + 0.5f), Offset(q.x + w / 2f, q.y + 0.5f), Offset(q.x + w / 2f + vuelo, q.y + 0.5f + h), Offset(q.x - w / 2f + vuelo, q.y + 0.5f + h))
            drawPath(forma, p.medio(c, lejos))
            clipPath(forma) {
                when (i % 4) {
                    0 -> drawRect(p.medio(c2, lejos), Offset(q.x - w, q.y + 0.5f + h * 0.36f), Size(w * 2f, h * 0.3f))
                    1 -> { drawLine(p.medio(c2, lejos), Offset(q.x + vuelo * 0.5f, q.y), Offset(q.x + vuelo * 0.5f, q.y + h + 1f), w * 0.26f); drawLine(p.medio(c2, lejos), Offset(q.x - w, q.y + 0.5f + h / 2f), Offset(q.x + w, q.y + 0.5f + h / 2f), w * 0.26f) }
                    2 -> drawRect(p.medio(c2, lejos), Offset(q.x - w / 2f, q.y + 0.5f), Size(w / 2f + vuelo * 0.5f, h / 2f))
                    else -> drawPath(poligono(Offset(q.x - w / 2f, q.y + 0.5f), Offset(q.x + w / 2f, q.y + 0.5f), Offset(q.x + vuelo, q.y + 0.5f + h)), p.medio(c2, lejos))
                }
                drawPath(forma, p.sombra(c, lejos).copy(alpha = 0.35f), style = Stroke(w * 0.3f))
            }
        }
    }

    /** De noche, el resplandor de la claraboya en la botavara y el pie de la vela (lo llama [NocheBarco]). */
    internal fun pieDeLaCangreja(v: Vista): Offset = pc(cangrejaDe(v.velas == Velas.CAIDAS).en(0.25f, 0.05f))

    /** Si la cangreja va larga (y no aferrada): la claraboya la alumbra desde abajo. */
    internal fun cangrejaLarga(v: Vista): Boolean = v.velas != Velas.AFERRADAS

    /** El contorno de la cangreja en el lienzo, para recortar lo que la alumbra (lo usa [NocheBarco]). */
    internal fun formaDeLaCangreja(v: Vista): Path = contorno(cangrejaDe(v.velas == Velas.CAIDAS))

    /** La botavara, en el lienzo. */
    /**
     * Lo que de las velas y la jarcia de delante cruza el cielo, en el lienzo (para la prueba que vigila que nada tape la
     * ventana de los astros): el contorno de la mayor y de la cangreja, la escota de la mayor y la braza.
     */
    internal fun loQueCruzaElCielo(m: Momento): List<Offset> {
        val vel = EscenaBotanico.velas(m); if (vel == Velas.AFERRADAS) return emptyList()
        val caida = vel == Velas.CAIDAS; val may = mayorDe(caida); val cang = cangrejaDe(caida)
        fun borde(vela: Vela) = (0..40).flatMap { i -> val u = i / 40f; listOf(vela.en(u, 0f), vela.en(u, 1f), vela.en(0f, u), vela.en(1f, u)) }.map { pc(it) }
        fun linea(a: V3, b: V3) = (0..40).map { pc(a + (b - a) * (it / 40f)) }
        val motonB = V3(estribor(5.6f) + 0.05f, 3.4f, 5.6f)
        return borde(may) + borde(cang) + linea(may.b, V3(estribor(3.75f) - 0.05f, REGALA + 0.04f, 3.75f)) + linea(may.c + V3(0.4f, 0f, 0f), motonB)
    }

    internal fun botavaraEnLienzo(v: Vista): Pair<Offset, Offset> = if (v.velas == Velas.AFERRADAS) pc(EJE, 3.25f, PALO_MAYOR - 0.4f) to pc(EJE + 0.05f, 3.6f, 1.6f) else botavara(v.velas == Velas.CAIDAS).let { pc(it.first) to pc(it.second) }
}
