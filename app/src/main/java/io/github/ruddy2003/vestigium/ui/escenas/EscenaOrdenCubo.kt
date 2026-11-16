package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.cara
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.palo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasOrden.sillares
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * El cubo de la esquina **[Diseño propio]**: un torreón bajo y en ángulo, de sillares sin labrar, que se adelanta sobre la
 * muralla donde el adarve dobla. Es el primer plano del cuadro, cortado por el borde de la derecha: cierra el camino de
 * ronda (el adarve que se ve es el tramo corto de delante, con sus dientes de sombra), tiene su tronera hacia el patio, su
 * tejadillo de losas, la hornacina con el candil grande de la guardia y, en la cara del patio, la cabria con la que se sube
 * del patio lo que hace falta en la muralla. Contra él, el rincón de la guardia.
 */
internal object CuboOrden {
    private val C = ColorOrden
    private const val P = CamOrden.PATIO

    /** El centro de la planta (s, v), su lado y su giro: una esquina mira a quien mira, y se ven dos caras. */
    const val S = 2.2f
    const val V = 10.6f
    const val LADO = 4.4f
    const val GIRO = -0.56f
    /** Lo alto de los muros, sobre el adarve, y la punta del tejadillo: no más arriba de la línea de los astros. */
    const val ALTO = 3.5f
    const val PUNTA = 4.5f

    /** Las esquinas de la planta (s, v): la de delante (la que mira a quien mira), delante-derecha, detrás-derecha, detrás-izquierda. */
    val esquinas: List<Offset> = PiezasOrden.esquinas(S, V, GIRO, LADO, LADO)
    private val nIzq = V3(-cos(GIRO), 0f, sin(GIRO))
    private val nDel = V3(-sin(GIRO), 0f, -cos(GIRO))

    /** Los puntos del cuerpo, para su sombra. */
    fun cuerpo(): List<V3> = esquinas.flatMap { listOf(V3(it.x, 0f, it.y), V3(it.x, ALTO, it.y)) } + V3(S, PUNTA, V)

    /** Un punto de la cara del patio (de la esquina de delante, [u] = 0, a la de detrás, [u] = 1) a la altura [h]. */
    private fun enIzq(u: Float, h: Float): V3 { val a = esquinas[0]; val d = esquinas[3]; return V3(a.x + (d.x - a.x) * u, h, a.y + (d.y - a.y) * u) }

    /** Un punto de la cara de delante (de la esquina de delante, [u] = 0, a la de la derecha, [u] = 1) a la altura [h]. */
    private fun enDel(u: Float, h: Float): V3 { val a = esquinas[0]; val b = esquinas[1]; return V3(a.x + (b.x - a.x) * u, h, a.y + (b.y - a.y) * u) }

    /** El cubo entero: las dos caras que se ven, la tronera, la hornacina, el tejadillo y la cabria. */
    fun DrawScope.cubo(v: VistaOrden) {
        val lz = v.lz; val az = PiezasOrden.fijo("cubo")
        // La cara del patio: sobre el adarve, desde el suelo de losas; donde ya no hay adarve, baja hasta el patio.
        val corte = (esquinas[0].x - CamOrden.BORDE) / (esquinas[0].x - esquinas[3].x)
        // Lo de abajo (por debajo del adarve), solo donde no lo tapa el suelo del adarve que queda delante.
        val delanteDelCubo = poligono(listOf(V3(CamOrden.BORDE, 0f, 2f), V3(CamOrden.PARAPETO, 0f, 2f), V3(CamOrden.PARAPETO, 0f, 10.4f), V3(CamOrden.BORDE, 0f, 10.4f)).map { CamOrden.p(it) })
        clipPath(delanteDelCubo, clipOp = ClipOp.Difference) {
            cara(lz, listOf(enIzq(corte, 0f), enIzq(corte, P), enIzq(1f, P), enIzq(1f, 0f)), nIzq, C.PIEDRA)?.let { f ->
                clipPath(f) { sillares(enIzq(1f, -6f), enIzq(corte, -6f), 6f, lz.color(C.PIEDRA, nIzq), PiezasOrden.fijo("cubo", 1f), 0.5f, 0.85f) }
            }
        }
        val izquierda = listOf(enIzq(0f, 0f), enIzq(1f, 0f), enIzq(1f, ALTO), enIzq(0f, ALTO))
        cara(lz, izquierda, nIzq, C.PIEDRA)?.let { f ->
            clipPath(f) {
                sillares(enIzq(1f, 0f), enIzq(0f, 0f), ALTO, lz.color(C.PIEDRA, nIzq), az, 0.5f, 0.85f)
                alero(v, enIzq(1f, ALTO), enIzq(0f, ALTO), nIzq)
                tronera(v, enIzq(0.55f, 1.3f), enIzq(0.62f, 1.3f), nIzq)
            }
        }
        // La de delante: sobre el parapeto, desde lo alto de él (lo que queda delante del cubo se ve).
        val p0 = (CamOrden.PARAPETO - esquinas[0].x) / (esquinas[1].x - esquinas[0].x)
        val delante = listOf(enDel(0f, 0f), enDel(p0, 0f), enDel(p0, CamOrden.ALTO_PARAPETO), enDel(1f, CamOrden.ALTO_PARAPETO), enDel(1f, ALTO), enDel(0f, ALTO))
        cara(lz, delante, nDel, C.PIEDRA)?.let { f ->
            clipPath(f) {
                sillares(enDel(0f, 0f), enDel(1f, 0f), ALTO, lz.color(C.PIEDRA, nDel), az, 0.46f, 0.8f)
                alero(v, enDel(0f, ALTO), enDel(1f, ALTO), nDel)
                hornacina(v)
            }
        }
        // La arista de la esquina, más clara (la piedra gastada).
        drawLine(lerp(lz.color(C.PIEDRA, V3(nIzq.x + nDel.x, 0f, nIzq.z + nDel.z)), C.ARENA, 0.25f), CamOrden.p(enIzq(0f, 0f)), CamOrden.p(enIzq(0f, ALTO)), (CamOrden.k(esquinas[0].x, esquinas[0].y) * 0.06f).coerceAtLeast(0.6f))
        tejadillo(v)
        cabria(v)
    }

    /** La sombra del alero en lo alto del muro: una franja oscura bajo el tejadillo. */
    private fun DrawScope.alero(v: VistaOrden, a: V3, b: V3, n: V3) {
        val franja = listOf(V3(a.x, a.y - 0.35f, a.z), V3(b.x, b.y - 0.35f, b.z), b, a).map { CamOrden.p(it) }
        drawPath(poligono(franja), v.l.tono(lerp(C.ROCA_HONDA, C.HOLLIN, 0.4f)).copy(alpha = 0.55f * (1f - 0.5f * v.l.noche)))
    }

    /** La tronera hacia el patio: una raja alta y estrecha, honda, con su derrame. */
    private fun DrawScope.tronera(v: VistaOrden, a: V3, b: V3, n: V3) {
        val derrame = listOf(a, b, V3(b.x, b.y + 1.1f, b.z), V3(a.x, a.y + 1.1f, a.z)).map { CamOrden.p(it) }
        drawPath(poligono(derrame), v.lz.color(C.PIEDRA_OSC, n))
        val m = 0.3f
        val raja = listOf(V3(a.x + (b.x - a.x) * m, a.y + 0.08f, a.z + (b.z - a.z) * m), V3(b.x - (b.x - a.x) * m, b.y + 0.08f, b.z - (b.z - a.z) * m),
            V3(b.x - (b.x - a.x) * m, b.y + 1.02f, b.z - (b.z - a.z) * m), V3(a.x + (b.x - a.x) * m, a.y + 1.02f, a.z + (b.z - a.z) * m)).map { CamOrden.p(it) }
        drawPath(poligono(raja), v.l.tono(C.HOLLIN))
    }

    /** Dónde arde el candil grande de la hornacina del cubo. */
    val CANDIL: V3 get() = enDel(0.24f, 1.45f).let { V3(it.x + nDel.x * 0.05f, it.y, it.z + nDel.z * 0.05f) }

    /**
     * La hornacina con el candil grande de la guardia: el arco hondo en la piedra, el vaso de latón con su asa; de noche,
     * su llama (va con lo vivo) y su luz en la piedra.
     */
    private fun DrawScope.hornacina(v: VistaOrden) {
        val a = enDel(0.16f, 1.05f); val b = enDel(0.32f, 1.05f); val k = CamOrden.k(a.x, a.z)
        val qa = CamOrden.p(a); val qb = CamOrden.p(b); val qc = CamOrden.p(b.x, 1.8f, b.z); val qd = CamOrden.p(a.x, 1.8f, a.z)
        val arco = Path().apply {
            moveTo(qa.x, qa.y); lineTo(qb.x, qb.y); lineTo(qc.x, qc.y)
            cubicTo(qc.x, qc.y - k * 0.32f, qd.x, qd.y - k * 0.32f, qd.x, qd.y); close()
        }
        drawPath(arco, v.l.tono(lerp(C.HOLLIN, C.ROCA_HONDA, 0.45f)))
        drawPath(arco, v.lz.color(lerp(C.PIEDRA, C.ARENA, 0.35f), nDel), style = Stroke((k * 0.05f).coerceAtLeast(0.5f)))
        // El vaso de latón, grande, con su pico y su asa.
        val c = CamOrden.p(CANDIL); val w = k * 0.17f
        val vaso = Path().apply { moveTo(c.x - w, c.y - w * 0.2f); cubicTo(c.x - w, c.y + w * 1.1f, c.x + w, c.y + w * 1.1f, c.x + w, c.y - w * 0.2f); close() }
        drawPath(vaso, v.lz.color(C.LATON, V3(nDel.x - 0.4f, 0.3f, nDel.z), 0f, 0.15f))
        drawLine(v.l.tono(lerp(C.LATON, C.HOLLIN, 0.4f)), Offset(c.x + w, c.y), Offset(c.x + w * 1.7f, c.y - w * 0.35f), (k * 0.03f).coerceAtLeast(0.4f), StrokeCap.Round)
        drawArc(v.l.tono(lerp(C.LATON, C.HOLLIN, 0.3f)), 180f, 180f, false, Offset(c.x - w * 0.6f, c.y - w * 0.9f), Size(w * 1.2f, w * 1.2f), style = Stroke((k * 0.025f).coerceAtLeast(0.4f)))
    }

    /** Si el candil grande arde: con la corona de candiles (el primero que se enciende), y nunca de luto. */
    fun arde(v: VistaOrden): Boolean = EscenaOrden.candiles(v.m) > 0

    /** La luz del candil grande: el halo en la piedra del cubo y el charco en las losas del adarve. */
    fun DrawScope.luzDelCandil(v: VistaOrden) {
        if (!arde(v)) return
        val k = 0.25f + 0.75f * v.l.noche; val c = CamOrden.p(CANDIL); val e = CamOrden.k(CANDIL.x, CANDIL.z)
        resplandor(c, e * 1.6f, 0.55f * k, C.LUMBRE)
        val pie = CamOrden.p(CANDIL.x - 0.9f, 0f, CANDIL.z - 0.6f)
        drawOval(Brush.radialGradient(listOf(C.LUMBRE.copy(alpha = (0.3f * k).coerceIn(0f, 1f)), Color.Transparent), pie, e * 1.6f), Offset(pie.x - e * 1.6f, pie.y - e * 0.45f), Size(e * 3.2f, e * 0.9f), blendMode = BlendMode.Plus)
    }

    /** El tejadillo de losas: cuatro faldones de poca pendiente que vuelan sobre los muros, con sus hiladas de losa. */
    private fun DrawScope.tejadillo(v: VistaOrden) {
        val lz = v.lz; val vuelo = PiezasOrden.esquinas(S, V, GIRO, LADO + 0.7f, LADO + 0.7f).map { V3(it.x, ALTO - 0.1f, it.y) }
        val punta = V3(S, PUNTA, V); val color = if (v.nieve > 0f) C.NIEVE else lerp(C.LOSA, C.HOLLIN, 0.12f)
        for (i in 0 until 4) {
            val a = vuelo[i]; val b = vuelo[(i + 1) % 4]
            val n = normal(a, b, punta)
            val f = cara(lz, listOf(a, b, punta), n, color, 0f, 0.1f) ?: continue
            clipPath(f) {
                val junta = lerp(lz.color(color, n), C.HOLLIN, 0.35f).copy(alpha = 0.5f)
                for (j in 1..5) { val t = j / 6f; drawLine(junta, CamOrden.p(mezcla(a, punta, t)), CamOrden.p(mezcla(b, punta, t)), 0.5f) }
            }
            // El canto del faldón, más claro.
            drawLine(lerp(lz.color(color, n), C.ARENA, 0.3f), CamOrden.p(a), CamOrden.p(b), 0.8f)
        }
    }

    private fun mezcla(a: V3, b: V3, t: Float) = V3(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t, a.z + (b.z - a.z) * t)

    /** La normal hacia fuera del triángulo (a, b, punta), con a y b en el alero. */
    private fun normal(a: V3, b: V3, c: V3): V3 {
        val u = b - a; val w = c - a
        val n = V3(u.y * w.z - u.z * w.y, u.z * w.x - u.x * w.z, u.x * w.y - u.y * w.x)
        val l = sqrt(n.x * n.x + n.y * n.y + n.z * n.z).coerceAtLeast(0.001f)
        return (if (n.y < 0f) n * -1f else n) * (1f / l)
    }

    /**
     * La cabria del cubo: un brazo de madera que sale de lo alto de la cara del patio, con su tornapuntas y su polea; la
     * cuerda baja al patio con un cubo colgado a media subida y se ata en la cornamusa del muro. El verano
     * del pozo seco, la cuerda está recogida (no se sube agua).
     */
    private fun DrawScope.cabria(v: VistaOrden) {
        val lz = v.lz; val raiz = enIzq(0.58f, 3.05f); val punta = V3(raiz.x + nIzq.x * 2.3f, 3.05f, raiz.z + nIzq.z * 2.3f)
        palo(lz, raiz, punta, 0.22f, C.MADERA)
        palo(lz, enIzq(0.58f, 1.9f), mezcla(raiz, punta, 0.55f), 0.13f, C.MADERA_OSC)
        val q = CamOrden.p(punta); val k = CamOrden.k(punta.x, punta.z)
        drawCircle(lz.color(C.MADERA_OSC, V3(0f, 0f, -1f)), (k * 0.11f).coerceAtLeast(1f), q)
        // La cornamusa de madera en el muro, donde se ata la cuerda.
        val ata = enIzq(0.5f, 1.2f).let { V3(it.x + nIzq.x * 0.08f, it.y, it.z + nIzq.z * 0.08f) }
        palo(lz, V3(ata.x, ata.y, ata.z), V3(ata.x + nIzq.x * 0.25f, ata.y + 0.05f, ata.z + nIzq.z * 0.25f), 0.12f, C.MADERA_OSC)
        if (EscenaOrden.pozoSeco(v.m)) return
        val cuerda = v.l.tono(lerp(C.MADERA, C.HOLLIN, 0.3f))
        drawLine(cuerda, CamOrden.p(ata), CamOrden.p(raiz), 0.5f)
        val cubo = V3(punta.x, -1.6f, punta.z)
        drawLine(cuerda, q, CamOrden.p(cubo), 0.6f)
        val c = CamOrden.p(cubo); val w = CamOrden.k(cubo.x, cubo.z) * 0.22f
        val forma = Path().apply { moveTo(c.x - w, c.y); lineTo(c.x + w, c.y); lineTo(c.x + w * 0.8f, c.y + w * 1.5f); lineTo(c.x - w * 0.8f, c.y + w * 1.5f); close() }
        drawPath(forma, lz.color(C.MADERA, V3(0.3f, 0.2f, -1f)))
        drawLine(v.l.tono(C.HIERRO), Offset(c.x - w, c.y + w * 0.4f), Offset(c.x + w, c.y + w * 0.4f), 0.5f)
    }
}
