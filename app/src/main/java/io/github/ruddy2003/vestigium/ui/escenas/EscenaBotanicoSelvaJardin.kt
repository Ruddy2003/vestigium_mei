package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.SelvaBotanico.k
import io.github.ruddy2003.vestigium.ui.escenas.SelvaBotanico.pc
import io.github.ruddy2003.vestigium.ui.escenas.SelvaBotanico.sueloEn
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/**
 * El jardín vuelto selva, a dos pasos del tablado. De lejos a cerca: la linde de la selva al pie de la montaña, con las
 * palmeras de tronco fino que suben; el árbol enorme de raíces tabulares (el gran vertical natural, a la izquierda del
 * centro), con sus bromelias y orquídeas en las ramas y las lianas colgando; los helechos arborescentes; el naranjo con
 * fruta; el macizo de plátanos con su racimo; las heliconias rojas; el arroyo que baja entre piedras con musgo y cruza
 * hacia la derecha, con el sendero a su lado (la diagonal grande del cuadro); y el plátano junto a la mesa, cuyas hojas
 * hacen la bóveda.
 */
internal object JardinSelva {
    private val C = ColorBotanico

    /** El árbol grande: dónde pisa, en metros. Más atrás que lo demás del jardín, para que se vea entero, de las raíces a la bóveda. */
    val ARBOL = V3(-0.55f, sueloEn(-0.55f, 16.5f), 16.5f)

    /** El helecho arborescente: a la derecha, contra el claro de la bahía, donde su estrella de frondas se recorta. */
    val HELECHO = V3(3.35f, sueloEn(3.35f, 10.6f), 10.6f)
    const val ALTO_HELECHO = 3.9f

    /** El macizo de plátanos, entre el árbol y el helecho: la mata de atrás y la del racimo, delante. */
    private val PLATANOS = listOf(Triple(V3(2.35f, sueloEn(2.35f, 13.6f), 13.6f), 3.5f, false), Triple(V3(1.55f, sueloEn(1.55f, 12.2f), 12.2f), 4.1f, true))
    private val HOJAS_PLATANOS = listOf(5, 7)

    /** Las matas de heliconias: pocas y grandes, para que se lean sus flores. */
    private val HELICONIAS = listOf(Triple(-1.5f, 7.4f, 1.8f), Triple(2.55f, 8.1f, 1.9f), Triple(3.45f, 6.9f, 1.7f))

    /** El naranjo, a la izquierda, detrás de la mesa. */
    private val NARANJO = V3(-3.3f, sueloEn(-3.3f, 10.5f), 10.5f)

    /** El arroyo: su eje, de arriba (lejos, a la izquierda) abajo (cerca, a la derecha), en metros, con el agua a su altura. */
    val ARROYO = listOf(V3(-3.8f, 1.1f, 38f), V3(-2.2f, 0.7f, 28f), V3(-0.6f, 0.25f, 20f), V3(0.9f, -0.12f, 14f), V3(1.6f, -0.45f, 10f), V3(2.0f, -0.66f, 7.5f), V3(2.25f, -0.8f, 5.6f), V3(2.6f, -0.92f, 4.1f), V3(3.0f, -1.0f, 3.0f))

    /** El sendero, por la orilla de la izquierda del arroyo: de la esquina del tablado hacia el árbol grande. */
    val SENDERO = listOf(V3(1.3f, -0.4f, 5.6f), V3(1.05f, -0.38f, 7.4f), V3(0.65f, -0.25f, 9.8f), V3(0.25f, -0.1f, 12.2f), V3(0.5f, 0.05f, 15f))

    /**
     * El jardín, ordenado por profundidad con el valor y la bruma (como la niebla de Horror cósmico y la vegetación de
     * Ciudad verde): lo lejano (la linde y las palmeras finas), pálido y azulado tras un velo húmedo; lo de en medio (el
     * árbol grande), más claro, con otro velo detrás; lo cercano (los plátanos, el helecho, el naranjo y las heliconias),
     * oscuro y saturado. Pocas plantas y enteras, para que cada una se reconozca a tamaño de teléfono.
     */
    fun DrawScope.jardin(m: Momento, luz: LuzSelva.Luz) {
        val p = luz.p; val az = Azar(Semilla.de("botanico", "jardin"))
        suelo(m, luz, az)
        // Las palmeras de tronco fino que suben de la linde, ya pálidas.
        with(PiezasBotanico) {
            for ((x, z, inc) in listOf(Triple(1.0f, 24f, 0.04f), Triple(-2.6f, 27f, -0.03f))) {
                val pie = pc(x, sueloEn(x, z), z); val alto = 15f * k(z)
                val cima = pie + Offset(inc * alto, -alto)
                drawPath(Path().apply { moveTo(pie); cubicTo(pie + Offset(0f, -alto * 0.4f), cima + Offset(-inc * alto * 0.6f, alto * 0.3f), cima) }, p.medio(lerp(C.ROCA, C.ARENA, 0.25f), 0.55f), style = Stroke(max(0.8f, 0.2f * k(z))))
            }
        }
        arroyo(m, luz, az)
        // El velo húmedo entre lo lejano y lo de en medio: la montaña y la linde se van al azul pálido.
        velo(m, luz, 58f, 160f, 0.8f, 0.3f)
        // El árbol grande, con sus lianas y sus bromelias y orquídeas en las horquillas: más claro que lo de delante.
        arbol(m, luz)
        // Otro velo, más tenue, entre el árbol y lo de delante.
        velo(m, luz, 96f, 172f, 0.4f, 0.15f)
        with(PiezasBotanico) {
            // El macizo de plátanos, la mata de atrás primero; la del racimo, de cara.
            for ((i, t) in PLATANOS.withIndex()) {
                val (q, h, racimo) = t; val pie = pc(q); val (pin, cl) = pinYHaz(luz, q.x, h * 0.6f, q.z)
                platano(pin, pie, h * k(q.z), Azar(Semilla.de("botanico", "platano jardin", i + 4)), 0.1f, racimo = racimo, hojas = HOJAS_PLATANOS[i], contraluz = maxOf(cl, BovedaSelva.contraluz(luz, pie + Offset(0f, -h * k(q.z) * 0.7f))))
            }
            // El helecho arborescente, con su estrella de frondas contra el claro.
            run { val pie = pc(HELECHO); val (pin, cl) = pinYHaz(luz, HELECHO.x, ALTO_HELECHO, HELECHO.z)
                helechoArborescente(pin, pie, ALTO_HELECHO * k(HELECHO.z), Azar(Semilla.de("botanico", "helecho jardin", 7)), 0.03f, frondas = 13, contraluz = maxOf(cl, BovedaSelva.contraluz(luz, pie + Offset(0f, -ALTO_HELECHO * k(HELECHO.z))))) }
            // El naranjo con fruta, a la izquierda.
            run { val q = NARANJO; val (pin, _) = pinYHaz(luz, q.x, 2f, q.z); naranjo(pin, pc(q), 3.3f * k(q.z), 2.7f * k(q.z), Azar(Semilla.de("botanico", "naranjo jardin")), 0.03f, frutos = 16) }
        }
        // El monte bajo de cerca, a las dos orillas del arroyo: helechos, hojas de corazón y piedras con musgo.
        monteBajo(m, luz)
        with(PiezasBotanico) {
            // Las heliconias, en matas grandes a la orilla del tablado y del arroyo.
            for ((i, t) in HELICONIAS.withIndex()) {
                val (x, z, h) = t; val pie = pc(x, sueloEn(x, z), z); val (pin, cl) = pinYHaz(luz, x, h, z)
                heliconia(pin, pie, h * k(z), Azar(Semilla.de("botanico", "heliconia", i + 3)), 0f, contraluz = maxOf(cl, BovedaSelva.contraluz(luz, pie + Offset(0f, -h * k(z))) * 0.7f), flores = 3, cuelga = i == 1)
            }
        }
        // El plátano junto a la mesa: el falso tronco y la cima (sus hojas son la bóveda).
        platanoDeLaMesa(m, luz)
    }

    /**
     * El pincel de lo que está en el jardín en ([x], [h], [z]) y cuánto lo enciende un haz: si un haz le da, el pincel
     * del sol y el contraluz (las hojas casi amarillas).
     */
    private fun pinYHaz(luz: LuzSelva.Luz, x: Float, h: Float, z: Float): Pair<Pincel, Float> {
        val hz = LuzSelva.enHaz(luz, pc(x, h, z))
        return if (hz > 0.25f) luz.p to (0.45f + 0.55f * hz) * luz.directa else pinJardin(luz, x, h, z) to 0f
    }

    /**
     * Un velo húmedo entre dos planos del jardín, de [y0] a [y1]: lo de detrás queda más pálido y azulado. Siempre hay
     * algo (la humedad de la selva), [base]; más con la bruma de la mañana, y espeso con niebla (la niebla va por capas,
     * como en Horror cósmico, no como un velo encima de todo).
     */
    private fun DrawScope.velo(m: Momento, luz: LuzSelva.Luz, y0: Float, y1: Float, cuanta: Float, base: Float) {
        val tiempo = when (m.ficha.clima) { Clima.NIEBLA -> 1f; Clima.LLUVIA, Clima.TORMENTA -> 0.7f; Clima.NIEVE -> 0.6f; else -> base + 0.35f * luz.bruma }
        val k = (tiempo * cuanta).coerceIn(0f, 1f); if (k < 0.02f) return
        val c = luz.l.tono(lerp(lerp(luz.l.bruma, Color(0xFFDCE8EC), 0.45f), luz.l.alto, 0.15f)).let { lerp(it, luz.l.bruma, luz.l.noche * 0.6f) }
        // Solo sobre la montaña y la selva (siguiendo su silueta): el cielo y la bahía, al aire libre, quedan limpios.
        clipPath(LejosSelva.TIERRA) {
            drawRect(Brush.verticalGradient(listOf(c.copy(alpha = 0f), c.copy(alpha = 0.45f * k), c.copy(alpha = 0.65f * k), c.copy(alpha = 0f)), y0 - 30f, y1), Offset(0f, y0 - 30f), Size(ANCHO, y1 - y0 + 30f))
            val az = Azar(Semilla.de("botanico", "bruma entre", y0))
            repeat(4) { mancha(Offset(az.entre(-20f, 300f), az.entre(y0 + 10f, y1 - 10f)), az.entre(70f, 140f), az.entre(6f, 12f), c, 0.55f * k) }
        }
    }

    /**
     * El monte bajo de cerca: matas de helecho de frondas arqueadas, matas de hojas en corazón (como los caládios y los
     * antúrios de la sombra) y piedras con musgo, a las dos orillas del arroyo y hasta la playa, cada una más grande cuanto
     * más cerca. Al sol, más claras y amarillas; a la sombra, verde hondo.
     */
    private fun DrawScope.monteBajo(m: Momento, luz: LuzSelva.Luz) {
        val az = Azar(Semilla.de("botanico", "monte bajo"))
        val sitios = ArrayList<Triple<V3, Int, Float>>()
        // Al sol, el suelo del jardín se enciende a manchas (lo que dejan pasar los plátanos); a la sombra, se ahonda.
        if (luz.directa > 0.2f && !luz.deLuna) repeat(12) { val z = 3.6f + 6f * az.real(); val x = 2.7f + az.entre(0f, 2.6f); val c = pc(x, sueloEn(x, z), z); val e = k(z)
            mancha(c, e * az.entre(0.4f, 0.9f), e * az.entre(0.1f, 0.22f), lerp(luz.color, C.HOJA_LUZ, 0.5f), 0.22f * luz.directa) }
        repeat(14) { val z = 4.6f + 5.5f * az.real(); val x = 2.7f + az.entre(0f, 2.6f) + (z - 3.4f) * 0.15f; sitios += Triple(V3(x, sueloEn(x, z), z), az.entero(3), az.entre(0.45f, 0.85f)) }
        repeat(5) { val z = 5.6f + 2.5f * az.real(); val x = az.entre(1.5f, 1.85f); sitios += Triple(V3(x, sueloEn(x, z), z), 0, az.entre(0.35f, 0.5f)) }
        for ((q, tipo, tam) in sitios.sortedByDescending { it.first.z }) {
            val c = pc(q); val e = k(q.z) * tam; val p = pinJardin(luz, q.x, q.y + 0.4f, q.z)
            when (tipo) {
                0, 1 -> {
                    // Una mata de helecho: frondas arqueadas que salen del centro, con sus pinnas en dos tonos.
                    val claras = ArrayList<Offset>(); val oscuras = ArrayList<Offset>(); val n = 7 + az.entero(4)
                    repeat(n) { j -> val a = -PI.toFloat() / 2f + (j - (n - 1) / 2f) * (2.8f / n) + az.entre(-0.1f, 0.1f); val l = e * az.entre(0.7f, 1.05f)
                        val fin = c + Offset(cos(a) * l, sin(a) * l * 0.55f + l * 0.35f * abs(cos(a))); val ctrl = c + Offset(cos(a) * l * 0.4f, sin(a) * l * 0.8f)
                        for (s in 1..7) { val t = s / 8f; val u = 1f - t; val qq = c * (u * u) + ctrl * (2f * u * t) + fin * (t * t); val w = l * 0.13f * (1f - t * 0.7f)
                            claras += qq; claras += qq + Offset(w * 0.6f, -w * 0.5f); oscuras += qq; oscuras += qq + Offset(-w * 0.6f, -w * 0.3f) } }
                    val verde = if (tipo == 0) lerp(C.HOJA, C.SELVA, 0.35f) else lerp(C.SELVA, C.HOJA, 0.25f)
                    drawPoints(oscuras, PointMode.Lines, p.por(verde, -0.5f, 0.05f), max(0.6f, e * 0.04f), StrokeCap.Round)
                    drawPoints(claras, PointMode.Lines, p.por(verde, 0.4f, 0.05f), max(0.6f, e * 0.04f), StrokeCap.Round)
                }
                else -> {
                    // Una mata de hojas en corazón, en sus tallos, cada una con su nervio claro.
                    repeat(4 + az.entero(3)) { j -> val a = -PI.toFloat() / 2f + az.entre(-0.9f, 0.9f); val l = e * az.entre(0.5f, 0.9f)
                        val base = c + Offset(cos(a) * l * 0.6f, sin(a) * l * 0.6f); val d = Offset(cos(a + az.entre(-0.6f, 0.6f)), 0.55f + 0.3f * az.real()) * (l * 0.55f)
                        drawLine(p.medio(lerp(C.HOJA, C.SELVA, 0.4f), 0.05f), c, base, max(0.4f, e * 0.025f))
                        val n = Offset(-d.y, d.x) * 0.55f; val tip = base + d
                        val hoja = Path().apply { moveTo(base); cubicTo(base - n * 0.9f - d * 0.2f, tip - n * 0.8f - d * 0.3f, tip); cubicTo(tip + n * 0.8f - d * 0.3f, base + n * 0.9f - d * 0.2f, base); close() }
                        val color = if (j % 3 == 0) lerp(C.HOJA, C.LONA, 0.25f) else lerp(C.SELVA, C.HOJA, 0.3f)
                        drawPath(hoja, p.por(color, if (j % 2 == 0) 0.35f else -0.3f, 0.05f))
                        drawLine(p.luz(lerp(color, C.LONA, 0.4f), 0.05f).copy(alpha = 0.6f), base, tip - d * 0.15f, max(0.3f, e * 0.012f)) }
                }
            }
        }
        // Las piedras con musgo entre las matas.
        repeat(5) { val z = 4.4f + 4f * az.real(); val x = 2.9f + az.entre(0f, 1.6f); piedra(luz.p, pc(x, sueloEn(x, z), z), k(z) * az.entre(0.12f, 0.22f), az, 0.05f) }
    }

    /** El pincel de lo que está en el jardín: al sol o a la sombra del árbol y de las hojas. */
    private fun pinJardin(luz: LuzSelva.Luz, x: Float, h: Float, z: Float): Pincel = LuzSelva.pincelEn(luz, pc(x, h, z), LuzSelva.alumbrado(luz, x, h, z))

    // ---------------------------------------------------------------- El suelo, la linde y las sombras

    private fun DrawScope.suelo(m: Momento, luz: LuzSelva.Luz, az: Azar) {
        val l = luz.l; val p = luz.p
        // La linde de la selva al pie de la montaña: monte bajo oscuro, con hojas grandes en su borde.
        val linde = (0..20).map { val x = -12f + 286f * it / 20f; Offset(x, 140f + 6f * sin(it * 1.7f) + 4f * sin(it * 0.6f) + (if (x > 230f) (x - 230f) * 0.35f else 0f)) }
        drawPath(curva(linde, 190f), Brush.verticalGradient(listOf(l.tono(lerp(C.SELVA, C.SELVA_OSC, 0.6f), 0.2f), l.tono(lerp(C.SELVA_OSC, C.BREA, 0.3f), 0.12f)), 135f, 190f))
        val dabsO = ArrayList<Offset>(); val dabsC = ArrayList<Offset>()
        repeat(170) { val x = az.entre(-10f, 275f); val y = az.entre(136f, 175f); dabsO += Offset(x, y); if (az.real() < 0.5f) dabsC += Offset(x + 1f, y - 1.4f) }
        drawPoints(dabsO, PointMode.Points, l.tono(lerp(C.SELVA, C.SELVA_OSC, 0.35f), 0.2f), 3.2f, StrokeCap.Round)
        drawPoints(dabsC, PointMode.Points, l.tono(lerp(C.HOJA, C.HOJA_LUZ, 0.3f), 0.2f).copy(alpha = 0.5f + 0.4f * luz.r.dia), 1.6f, StrokeCap.Round)
        // El suelo del jardín: tierra negra cubierta de helechos bajos, musgo y hojarasca; baja a la playa por la derecha.
        val seca = LejosSelva.PLAYA.map { it + Offset(0f, 7f + (it.y - 150f) * 0.35f) }
        val tierra = poligono(listOf(Offset(-12f, 165f), Offset(258f, 168f)) + seca.filter { it.x > 260f } + listOf(Offset(ANCHO + 10f, ALTO + 10f), Offset(-12f, ALTO + 10f)))
        drawPath(tierra, Brush.verticalGradient(listOf(l.tono(lerp(C.SELVA, C.MADERA_OSC, 0.35f), 0.15f), l.tono(lerp(C.SELVA_OSC, C.MADERA_OSC, 0.4f), 0.05f)), 160f, ALTO))
        clipPath(tierra) {
            // La orilla de la playa: la arena se mete entre la hierba.
            drawPath(poligono(seca + listOf(Offset(ANCHO + 10f, 200f), Offset(262f, 196f))), Brush.verticalGradient(listOf(l.tono(lerp(C.ARENA, C.HOJA, 0.25f), 0.12f), Color.Transparent), 165f, 200f))
            // El musgo y la hojarasca: manchas blandas, verdes y pardas.
            repeat(16) { val q = V3(az.entre(-4f, 5f), 0f, az.entre(5.5f, 22f)); val c = pc(q.x, sueloEn(q.x, q.z), q.z); val e = k(q.z)
                mancha(c, e * az.entre(0.4f, 1.1f), e * az.entre(0.08f, 0.2f), if (it % 3 == 0) l.tono(lerp(C.CUERO, C.SELVA_OSC, 0.4f), 0.1f) else l.tono(lerp(C.SELVA, C.HOJA, 0.35f), 0.1f), 0.35f) }
            // Los helechos bajos: matas de frondas cortas que se abren en abanico, más grandes cuanto más cerca.
            val oscuras = ArrayList<Offset>(); val claras = ArrayList<Offset>()
            repeat(65) {
                val q = V3(az.entre(-4.5f, 5.5f), 0f, 8.5f + 14f * az.real() * az.real()); val c = pc(q.x, sueloEn(q.x, q.z), q.z); val e = k(q.z) * az.entre(0.25f, 0.42f)
                repeat(7) { j -> val a = -PI.toFloat() / 2f + (j - 3) * 0.42f + az.entre(-0.15f, 0.15f); val fin = c + Offset(cos(a) * e, sin(a) * e * 0.75f + e * 0.25f * abs(cos(a)))
                    (if (cos(a) * (if (p.solALaDerecha) 1f else -1f) > 0.1f) claras else oscuras).apply { add(c); add(fin) } }
            }
            drawPoints(oscuras, PointMode.Lines, l.tono(lerp(C.SELVA, C.SELVA_OSC, 0.25f), 0.08f), 1.3f, StrokeCap.Round)
            drawPoints(claras, PointMode.Lines, p.medio(lerp(C.HOJA, C.HOJA_LUZ, 0.2f), 0.08f), 1.1f, StrokeCap.Round)
            // El sendero de arena, con sus piedras pasaderas.
            val camino = SENDERO.map { pc(it) }
            val ancho = SENDERO.map { 0.62f * k(it.z) }
            val izq = camino.mapIndexed { i, q -> q + Offset(-ancho[i] / 2f, 0f) }; val der = camino.mapIndexed { i, q -> q + Offset(ancho[i] / 2f, 0f) }
            drawPath(poligono(izq + der.reversed()), p.arriba(lerp(C.ARENA, C.CUERO, 0.2f), 0.1f))
            for ((i, q) in camino.withIndex()) { if (i == 0) continue; val e = ancho[i]; drawOval(p.arriba(lerp(C.ROCA, C.ARENA, 0.3f), 0.1f), Offset(q.x - e * 0.25f, q.y - e * 0.09f), Size(e * 0.5f, e * 0.18f)); drawOval(p.sombra(C.ROCA, 0.1f).copy(alpha = 0.5f), Offset(q.x - e * 0.25f, q.y), Size(e * 0.5f, e * 0.06f)) }
        }
        // Las sombras del jardín: las del árbol grande, los plátanos y los helechos, tumbadas con el sol.
        // (la capa, recortada al suelo del jardín: no hace falta que cubra el cielo)
        clipRect(0f, 128f, ANCHO, ALTO) { capaSombra(luz.r, 0.85f) { col ->
            for ((x, z, alto, radio) in listOf(Cuatro(ARBOL.x, ARBOL.z, 9f, 1.6f), Cuatro(1.55f, 12.2f, 4.1f, 1.6f), Cuatro(2.35f, 13.6f, 3.5f, 1.4f), Cuatro(HELECHO.x, HELECHO.z, ALTO_HELECHO, 1.5f), Cuatro(NARANJO.x, NARANJO.z, 3.3f, 1.3f), Cuatro(-1.6f, 6.8f, 3.5f, 1.4f)))
                sombra(sombraEnElSuelo(luz, V3(x, sueloEn(x, z), z), alto, radio), col)
        } }
    }

    private data class Cuatro(val a: Float, val b: Float, val c: Float, val d: Float)

    /** La sombra en el suelo del jardín de algo que pisa en [pie] (en metros), de [alto] y [radio] metros: la base y la copa tumbadas. */
    private fun sombraEnElSuelo(luz: LuzSelva.Luz, pie: V3, alto: Float, radio: Float): Path {
        val pts = ArrayList<Offset>()
        for (i in 0 until 10) { val a = (2 * PI * i / 10).toFloat()
            pts += Offset(pie.x + radio * 0.25f * cos(a), pie.z + radio * 0.25f * sin(a))
            val q = LuzSelva.cae(luz, V3(pie.x + radio * cos(a), pie.y + alto * 0.75f, pie.z + radio * 0.6f * sin(a)), pie.y); pts += q }
        return LuzSelva.enLienzo(envolvente(pts), pie.y) ?: Path()
    }

    // ---------------------------------------------------------------- El arroyo

    /**
     * El arroyo que baja entre piedras con musgo: el agua oscura y clara a la vez (refleja el cielo y el follaje), con su
     * espuma en cada salto, más ancho y blanco con la lluvia; las piedras de las orillas, con su lado de luz y el musgo.
     */
    private fun DrawScope.arroyo(m: Momento, luz: LuzSelva.Luz, az: Azar) {
        val l = luz.l; val p = luz.p
        val crecido = SelvaBotanico.arroyoCrecido(m)
        val ancho = if (crecido) 1.35f else 1.0f
        // El eje, con más puntos, y las dos orillas, cada una con sus entrantes y salientes (nada de curva lisa).
        val puntos = (0 until ARROYO.size - 1).flatMap { i -> (0 until 4).map { j -> ARROYO[i] + (ARROYO[i + 1] - ARROYO[i]) * (j / 4f) } } + ARROYO.last()
        val eje = puntos.map { pc(it) }; val w = puntos.mapIndexed { i, it -> ancho * k(it.z) * 0.5f * (1f + 0.14f * sin(i * 1.9f) + 0.08f * sin(i * 4.3f)) }
        val izq = eje.mapIndexed { i, q -> q + Offset(-w[i] * (1f + 0.16f * sin(i * 2.3f + 1f)), -w[i] * 0.16f) }; val der = eje.mapIndexed { i, q -> q + Offset(w[i] * (1f + 0.16f * cos(i * 1.7f)), w[i] * 0.16f) }
        // El cauce: la orilla de tierra mojada, en sombra, un poco más ancha que el agua (la franja de paso).
        drawPath(poligono(izq.mapIndexed { i, q -> q + Offset(-2f - w[i] * 0.25f, 0f) } + der.mapIndexed { i, q -> q + Offset(2f + w[i] * 0.25f, 1f) }.reversed()), p.sombra(lerp(C.MADERA_OSC, C.SELVA_OSC, 0.35f), 0.1f))
        val agua = poligono(izq + der.reversed())
        val cielo = lerp(l.medio, l.alto, 0.35f)
        val hondo = l.tono(lerp(C.SELVA_OSC, C.MAR_HONDO, 0.45f)); val claro = l.tono(lerp(C.MAR, C.ORILLA, 0.35f))
        drawPath(agua, Brush.verticalGradient(listOf(hondo, lerp(hondo, claro, 0.3f), lerp(hondo, claro, 0.45f)), 125f, 300f))
        clipPath(agua) {
            // Donde hay poco fondo, junto a las orillas de cerca, se ven los guijarros del lecho.
            val gz = Azar(Semilla.de("botanico", "guijarros"))
            for (i in eje.indices) { if (eje[i].y < 190f) continue
                repeat(6) { val lado = if (gz.real() < 0.5f) -1f else 1f; val q = eje[i] + Offset(lado * w[i] * gz.entre(0.55f, 0.95f), gz.entre(-3f, 3f)); val r = 0.6f + (q.y - 180f) * 0.012f
                    drawOval(l.tono(lerp(C.ROCA, C.ARENA, gz.real() * 0.5f)).copy(alpha = 0.55f), Offset(q.x - r, q.y - r * 0.5f), Size(r * 2f, r)) } }
            // Lo que se refleja: el cielo del claro en vetas largas que siguen la corriente, y la orilla oscura de cada lado.
            val vetasC = ArrayList<Offset>(); val vetasO = ArrayList<Offset>()
            for (i in 0 until eje.size - 1) { val a = eje[i]; val b = eje[i + 1]; val d = b - a; val dl = d.getDistance().coerceAtLeast(1f)
                repeat(3) { val t = az.real(); val c = a + d * t; val ww = w[i] + (w[i + 1] - w[i]) * t; val off = az.entre(-0.85f, 0.85f) * ww; val q = c + Offset(off, off * 0.16f); val l2 = dl * az.entre(0.25f, 0.7f)
                    (if (abs(off) > ww * 0.55f) vetasO else vetasC).apply { add(q); add(q + d / dl * l2) } } }
            drawPoints(vetasO, PointMode.Lines, l.tono(C.SELVA_OSC).copy(alpha = 0.5f), 1.6f, StrokeCap.Round)
            drawPoints(vetasC, PointMode.Lines, lerp(cielo, Color.White, 0.2f).copy(alpha = 0.45f), 1.1f, StrokeCap.Round)
            // Los rizos de la corriente, de través, y el brillo del sol si le da.
            val rizos = ArrayList<Offset>(); val turbia = if (crecido) lerp(C.ARENA, C.CUERO, 0.4f) else C.LONA
            for (i in 0 until eje.size - 1) { val a = eje[i]; val b = eje[i + 1]; repeat(4) { val t = az.real(); val q = a + (b - a) * t + Offset(az.entre(-0.8f, 0.8f) * (w[i] + (w[i + 1] - w[i]) * t), 0f); val e = 1.2f + (q.y - 120f) * 0.025f
                rizos += q; rizos += q + Offset(e * az.entre(0.8f, 1.8f), -e * az.entre(0.4f, 0.9f)) } }
            drawPoints(rizos, PointMode.Lines, l.tono(lerp(turbia, Color.White, 0.35f)).copy(alpha = 0.6f), 0.6f, StrokeCap.Round)
            if (crecido) drawPath(agua, l.tono(lerp(C.ARENA, C.CUERO, 0.45f)).copy(alpha = 0.38f))
            if (luz.directa > 0.2f) drawPath(agua, luz.color.copy(alpha = 0.1f * luz.directa), blendMode = BlendMode.Plus)
        }
        // La orilla: el borde del agua, claro donde le da la luz.
        drawPath(curva(izq), lerp(cielo, Color.White, 0.3f).copy(alpha = 0.4f), style = Stroke(0.6f))
        // Las piedras de las orillas y del cauce, con musgo arriba, y la espuma de los saltos.
        val pz = Azar(Semilla.de("botanico", "piedras arroyo"))
        val esp = lerp(l.tono(C.LONA), Color.White, 0.3f)
        for (i in 1 until ARROYO.size) for (j in 0..3) {
            val a = ARROYO[i]; val lado = when (j) { 0 -> -1f; 1 -> 1f; else -> pz.entre(-0.6f, 0.6f) }
            val q = V3(a.x + lado * ancho * 0.55f + pz.entre(-0.15f, 0.15f), a.y + 0.05f, a.z + pz.entre(-0.9f, 0.9f)); val c = pc(q); val e = k(q.z) * pz.entre(0.18f, 0.36f)
            if (j >= 2) repeat(5) { drawOval(esp.copy(alpha = if (crecido) 0.85f else 0.6f), Offset(c.x - e * 1.2f + pz.entre(0f, e * 1.4f), c.y - e * 0.05f + pz.entre(-1f, 1.5f)), Size(e * pz.entre(0.4f, 1f), e * 0.16f)) }
            piedra(p, c, e, pz, 0.08f)
        }
    }

    /** Una piedra del arroyo: redondeada, con su lado de luz, la sombra debajo y el musgo encima. */
    private fun DrawScope.piedra(p: Pincel, c: Offset, e: Float, az: Azar, lejos: Float) {
        val lado = if (p.solALaDerecha) 1f else -1f
        val forma = Path().apply { moveTo(c + Offset(-e, 0f)); cubicTo(c + Offset(-e, -e * 0.7f), c + Offset(e * 0.6f, -e * 0.8f), c + Offset(e, -e * 0.1f)); cubicTo(c + Offset(e * 0.8f, e * 0.25f), c + Offset(-e * 0.7f, e * 0.25f), c + Offset(-e, 0f)); close() }
        val roca = lerp(C.ROCA, C.SELVA, 0.15f)
        drawPath(forma, Brush.horizontalGradient(listOf(p.sombra(roca, lejos), p.medio(roca, lejos), p.luz(roca, lejos)), c.x - lado * e, c.x + lado * e))
        clipPath(forma) { drawOval(p.por(lerp(C.SELVA, C.HOJA, 0.45f), 0.2f, lejos), Offset(c.x - e * 0.8f, c.y - e * 0.85f), Size(e * 1.5f, e * 0.55f)) }
    }

    // ---------------------------------------------------------------- El árbol grande

    /**
     * El árbol grande, el gran vertical natural del cuadro: el tronco claro y entero, de las raíces tabulares (que se
     * abren por encima de lo que hay en el tablado) a la bóveda, con una liana que lo abraza, las bromelias de sus
     * horquillas y las lianas que cuelgan de sus ramas. Queda en el plano de en medio: más claro que lo de delante, con
     * el velo húmedo detrás; a la sombra de su propia copa, salvo donde lo cruza un haz, que lo enciende a franjas.
     */
    private fun DrawScope.arbol(m: Momento, luz: LuzSelva.Luz) {
        val pie = pc(ARBOL); val e = k(ARBOL.z)
        val p = if (luz.deLuna || luz.directa < 0.15f) luz.sombra else luz.p; val lejos = 0.14f
        val az = Azar(Semilla.de("botanico", "arbol grande"))
        val ancho = 2.0f * e
        val horquillas = with(PiezasBotanico) { arbolDeRaices(p, pie, ancho, pie.y + 20f, az, lejos) }
        with(PiezasBotanico) {
            // La liana que abraza el tronco y las bromelias que viven en él.
            val sube = (0..14).map { i -> val t = i / 14f; pie + Offset(sin(t * 7f + 1f) * 0.42f * ancho * (1f - 0.2f * t), -t * pie.y * 0.95f) }
            liana(p, sube, 1.4f, az, lejos)
            for ((i, t) in listOf(0.38f, 0.62f).withIndex()) { val q = pie + Offset((if (i % 2 == 0) -0.4f else 0.42f) * ancho, -t * pie.y); bromelia(p, q, e * az.entre(0.7f, 0.95f), az, lejos, flor = i == 1) }
        }
        // Las motas de sol que se cuelan por su copa, del lado de la luz, y las franjas que encienden los haces.
        if (luz.directa > 0.2f && !luz.deLuna) { val lado = if (luz.r.lat >= 0f) 1f else -1f
            with(LuzSelva) {
                repeat(5) { val q = pie + Offset(lado * az.entre(0.05f, 0.4f) * ancho, -az.entre(0.15f, 0.85f) * pie.y); brillo(q, ancho * az.entre(0.08f, 0.16f), ancho * az.entre(0.12f, 0.22f), luz.color, 0.35f * luz.directa) }
                for (j in 0..12) { val q = pie + Offset(0f, -pie.y * j / 12f); val h = LuzSelva.enHaz(luz, q); if (h > 0.2f) brillo(q, ancho * 0.45f, ancho * 0.35f, lerp(luz.color, C.LATON, 0.2f), 0.4f * h * luz.directa) }
            }
        }
        // Las lianas que cuelgan de las ramas (una en lazo que cruza por arriba hacia la casa) y las bromelias de las horquillas.
        with(PiezasBotanico) {
            for ((i, h) in horquillas.withIndex()) {
                if (i == 1) liana(p, listOf(h, Offset((h.x + 95f) * 0.5f, h.y + 50f), Offset(95f, 30f)), 1.5f, az, lejos)
                else if (i % 2 == 0) { val fin = Offset(h.x + az.entre(-25f, 25f), h.y + az.entre(55f, 95f)); liana(p, listOf(h, h + Offset(az.entre(-6f, 6f), 30f), fin + Offset(0f, -12f), fin), 1.2f, az, lejos, hojas = false) }
            }
            for ((i, h) in horquillas.withIndex()) if (i % 2 == 1 || i == 0) bromelia(p, h, e * az.entre(0.8f, 1.15f), az, lejos, flor = i % 2 == 0)
        }
    }

    // ---------------------------------------------------------------- El plátano junto a la mesa

    /** El plátano junto a la mesa: el falso tronco, las vainas secas y la cima de donde salen las hojas de la bóveda. */
    private fun DrawScope.platanoDeLaMesa(m: Momento, luz: LuzSelva.Luz) {
        val pie = pc(BovedaSelva.PLATANO_PIE); val cima = pc(BovedaSelva.PLATANO_CIMA); val e = k(BovedaSelva.PLATANO_PIE.z)
        val p = pinJardin(luz, BovedaSelva.PLATANO_PIE.x, 1.5f, BovedaSelva.PLATANO_PIE.z)
        val lado = if (p.solALaDerecha) 1f else -1f
        val g0 = 0.17f * e; val g1 = 0.11f * e
        val tronco = poligono(pie + Offset(-g0, 0f), pie + Offset(g0, 0f), cima + Offset(g1, 0f), cima + Offset(-g1, 0f))
        drawPath(tronco, Brush.horizontalGradient(listOf(p.sombra(lerp(C.HOJA, C.CUERO, 0.25f)), p.medio(lerp(C.HOJA, C.ARENA, 0.25f)), p.luz(lerp(C.HOJA, C.ARENA, 0.35f))), pie.x - lado * g0, pie.x + lado * g0))
        val az = Azar(Semilla.de("botanico", "platano mesa"))
        clipPath(tronco) {
            for (j in 0..8) { val y = pie.y - (pie.y - cima.y) * j / 9f; drawPath(Path().apply { moveTo(Offset(pie.x - g0 * 1.2f, y)); cubicTo(Offset(pie.x, y - g0 * 0.8f), Offset(pie.x + g0 * 0.5f, y - g0 * 1.3f), Offset(pie.x + g0 * 1.2f, y - g0 * 2.2f)) }, p.sombra(lerp(C.HOJA, C.BREA, 0.4f)).copy(alpha = 0.45f), style = Stroke(0.7f)) }
            repeat(6) { mancha(pie + Offset(az.entre(-g0, g0), -az.entre(0f, pie.y - cima.y)), g0 * 0.4f, g0, p.medio(lerp(C.CUERO, C.BREA, 0.3f)), 0.35f) }
        }
        // Las vainas secas que cuelgan pegadas.
        with(PiezasBotanico) { repeat(3) { val a = cima + Offset(az.entre(-g1, g1), e * az.entre(0.05f, 0.4f)); hojaDePlatano(p, a, a + Offset(az.entre(-0.3f, 0.3f) * e, e * az.entre(0.6f, 0.9f)), e * 0.18f, e * 0.05f, 0.7f, 0.9f, 1f, -0.3f, az) } }
    }
}
