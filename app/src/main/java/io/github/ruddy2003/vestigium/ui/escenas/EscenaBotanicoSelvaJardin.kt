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

    /** El árbol grande: dónde pisa, en metros. */
    val ARBOL = V3(-0.35f, sueloEn(-0.35f, 13.5f), 13.5f)

    /** El arroyo: su eje, de arriba (lejos, a la izquierda) abajo (cerca, a la derecha), en metros, con el agua a su altura. */
    val ARROYO = listOf(V3(-3.8f, 1.1f, 38f), V3(-2.2f, 0.7f, 28f), V3(-0.6f, 0.25f, 20f), V3(0.9f, -0.12f, 14f), V3(1.6f, -0.45f, 10f), V3(2.0f, -0.66f, 7.5f), V3(2.25f, -0.8f, 5.6f), V3(2.6f, -0.92f, 4.1f), V3(3.0f, -1.0f, 3.0f))

    /** El sendero, por la orilla de la izquierda del arroyo: de la esquina del tablado hacia el árbol grande. */
    val SENDERO = listOf(V3(1.3f, -0.4f, 5.6f), V3(1.05f, -0.38f, 7.4f), V3(0.65f, -0.25f, 9.8f), V3(0.25f, -0.1f, 12.2f), V3(0.5f, 0.05f, 15f))

    fun DrawScope.jardin(m: Momento, luz: LuzSelva.Luz) {
        val l = luz.l; val p = luz.p; val az = Azar(Semilla.de("botanico", "jardin"))
        suelo(m, luz, az)
        // Las palmeras de tronco fino que suben de la linde y se salen por arriba, entre la bóveda.
        with(PiezasBotanico) {
            for ((x, z, inc) in listOf(Triple(1.35f, 22f, 0.04f), Triple(-1.5f, 26f, -0.03f), Triple(2.55f, 30f, 0.02f))) {
                val pie = pc(x, sueloEn(x, z), z); val alto = 15f * k(z)
                val cima = pie + Offset(inc * alto, -alto)
                drawPath(Path().apply { moveTo(pie); cubicTo(pie + Offset(0f, -alto * 0.4f), cima + Offset(-inc * alto * 0.6f, alto * 0.3f), cima) }, p.medio(lerp(C.ROCA, C.ARENA, 0.25f), 0.3f), style = Stroke(max(0.8f, 0.2f * k(z))))
                drawPath(Path().apply { moveTo(pie + Offset(0.5f, 0f)); cubicTo(pie + Offset(0.5f, -alto * 0.4f), cima + Offset(-inc * alto * 0.6f + 0.5f, alto * 0.3f), cima + Offset(0.5f, 0f)) }, p.luz(lerp(C.ROCA, C.ARENA, 0.4f), 0.3f).copy(alpha = 0.5f * luz.r.dia), style = Stroke(max(0.4f, 0.07f * k(z))))
            }
        }
        arroyo(m, luz, az)
        // El árbol grande, con sus lianas y sus bromelias y orquídeas en las horquillas.
        arbol(m, luz)
        with(PiezasBotanico) {
            // Los helechos arborescentes, de tronco oscuro y fronda en estrella (los dos de detrás, antes de la bruma).
            for ((i, t) in listOf(Triple(-2.15f, 12.5f, 4.3f), Triple(2.95f, 13.5f, 3.7f)).withIndex()) {
                val (x, z, h) = t; val pie = pc(x, sueloEn(x, z), z)
                helechoArborescente(pinJardin(luz, x, h, z), pie, h * k(z), Azar(Semilla.de("botanico", "helecho jardin", i)), 0.08f, contraluz = BovedaSelva.contraluz(luz, pie + Offset(0f, -h * k(z))))
            }
        }
        // La bruma se queda entre los planos: entre el árbol grande y lo de delante.
        brumaEntre(m, luz, 120f, 178f, 1f)
        with(PiezasBotanico) {
            run { val x = -2.9f; val z = 8.4f; val h = 3.1f; val pie = pc(x, sueloEn(x, z), z)
                helechoArborescente(pinJardin(luz, x, h, z), pie, h * k(z), Azar(Semilla.de("botanico", "helecho jardin", 2)), 0.06f, contraluz = BovedaSelva.contraluz(luz, pie + Offset(0f, -h * k(z)))) }
            // El naranjo con fruta, a la izquierda.
            run { val x = -3.25f; val z = 10f; naranjo(pinJardin(luz, x, 2f, z), pc(x, sueloEn(x, z), z), 3.3f * k(z), 2.7f * k(z), Azar(Semilla.de("botanico", "naranjo jardin")), 0.08f, frutos = 18) }
            // El macizo de plátanos con su racimo, a la derecha del sendero.
            for ((i, t) in listOf(Triple(2.75f, 9.3f, 4.6f), Triple(3.5f, 10.4f, 3.6f)).withIndex()) {
                val (x, z, h) = t; val pie = pc(x, sueloEn(x, z), z)
                platano(pinJardin(luz, x, h * 0.7f, z), pie, h * k(z), Azar(Semilla.de("botanico", "platano jardin", i)), 0.08f, racimo = i == 0, hojas = 8, contraluz = BovedaSelva.contraluz(luz, pie + Offset(0f, -h * k(z) * 0.7f)))
            }
            // Las heliconias, en matas a la orilla del tablado y del arroyo.
            for ((i, t) in listOf(Triple(-2.35f, 8.3f, 1.7f), Triple(-1.45f, 7.6f, 1.6f), Triple(0.75f, 7.9f, 1.5f), Triple(1.75f, 6.7f, 1.4f), Triple(3.3f, 7.0f, 1.6f)).withIndex()) {
                val (x, z, h) = t; val pie = pc(x, sueloEn(x, z), z)
                heliconia(pinJardin(luz, x, h, z), pie, h * k(z), Azar(Semilla.de("botanico", "heliconia", i)), 0.04f, contraluz = BovedaSelva.contraluz(luz, pie + Offset(0f, -h * k(z))) * 0.7f, flores = 2 + i % 2, cuelga = i % 2 == 1)
            }
        }
        // Otra capa de bruma, más tenue, entre el jardín y el tablado.
        brumaEntre(m, luz, 168f, 215f, 0.55f)
        // El plátano junto a la mesa: el falso tronco y la cima (sus hojas son la bóveda).
        platanoDeLaMesa(m, luz)
        // Lo que se ve en el aire del jardín: los rayos que bajan entre las hojas.
        clipPath(poligono(listOf(Offset(-10f, 0f), Offset(196f, 0f), Offset(250f, 60f), Offset(268f, 110f), Offset(268f, 230f), Offset(-10f, 230f)))) {
            with(LejosSelva) { rayos(luz, Rect(40f, 10f, 250f, 210f), 4, Semilla.de("botanico", "rayos jardin", m.ficha.dia), 0.7f) }
        }
    }

    /**
     * Un banco de bruma entre dos planos del jardín, de [y0] a [y1]: con niebla, espeso (la niebla va por capas, como en
     * Horror cósmico, no como un velo encima de todo); con lluvia, menos; con buen tiempo, solo la de la mañana.
     */
    private fun DrawScope.brumaEntre(m: Momento, luz: LuzSelva.Luz, y0: Float, y1: Float, cuanta: Float) {
        val tiempo = when (m.ficha.clima) { Clima.NIEBLA -> 1f; Clima.LLUVIA, Clima.TORMENTA -> 0.5f; Clima.NIEVE -> 0.45f; else -> 0.35f * luz.bruma }
        val k = (tiempo * cuanta).coerceIn(0f, 1f); if (k < 0.02f) return
        val c = lerp(luz.l.bruma, Color.White, 0.25f)
        drawRect(Brush.verticalGradient(listOf(c.copy(alpha = 0f), c.copy(alpha = 0.5f * k), c.copy(alpha = 0.75f * k), c.copy(alpha = 0f)), y0 - 50f, y1 + 10f), Offset(0f, y0 - 50f), Size(ANCHO, y1 - y0 + 60f))
        val az = Azar(Semilla.de("botanico", "bruma entre", y0))
        repeat(6) { mancha(Offset(az.entre(-20f, ANCHO + 20f), az.entre(y0, y1)), az.entre(60f, 140f), az.entre(6f, 14f), c, 0.6f * k) }
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
        repeat(260) { val x = az.entre(-10f, 275f); val y = az.entre(136f, 175f); dabsO += Offset(x, y); if (az.real() < 0.5f) dabsC += Offset(x + 1f, y - 1.4f) }
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
            repeat(40) { val q = V3(az.entre(-4f, 5f), 0f, az.entre(5.5f, 22f)); val c = pc(q.x, sueloEn(q.x, q.z), q.z); val e = k(q.z)
                mancha(c, e * az.entre(0.4f, 1.1f), e * az.entre(0.08f, 0.2f), if (it % 3 == 0) l.tono(lerp(C.CUERO, C.SELVA_OSC, 0.4f), 0.1f) else l.tono(lerp(C.SELVA, C.HOJA, 0.35f), 0.1f), 0.35f) }
            // Los helechos bajos: matas de frondas cortas que se abren en abanico, más grandes cuanto más cerca.
            val oscuras = ArrayList<Offset>(); val claras = ArrayList<Offset>()
            repeat(150) {
                val q = V3(az.entre(-4.5f, 5.5f), 0f, 5.6f + 16f * az.real() * az.real()); val c = pc(q.x, sueloEn(q.x, q.z), q.z); val e = k(q.z) * az.entre(0.35f, 0.6f)
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
        capaSombra(luz.r, 0.85f) { col ->
            for ((x, z, alto, radio) in listOf(Cuatro(ARBOL.x, ARBOL.z, 9f, 1.4f), Cuatro(2.75f, 9.3f, 4.6f, 1.6f), Cuatro(-2.15f, 12.5f, 4.3f, 1.5f), Cuatro(2.95f, 13.5f, 3.7f, 1.3f), Cuatro(-3.25f, 10f, 3.3f, 1.3f), Cuatro(-1.6f, 6.8f, 3.5f, 1.4f)))
                sombra(sombraEnElSuelo(luz, V3(x, sueloEn(x, z), z), alto, radio), col)
        }
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
        drawPath(agua, Brush.verticalGradient(listOf(hondo, lerp(hondo, claro, 0.35f), lerp(hondo, claro, 0.62f)), 125f, 300f))
        clipPath(agua) {
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

    private fun DrawScope.arbol(m: Momento, luz: LuzSelva.Luz) {
        val pie = pc(ARBOL); val e = k(ARBOL.z)
        // El tronco queda a la sombra de su propia copa (que se sale por arriba): solo lo tocan los rayos que bajan.
        val p = luz.sombra
        val az = Azar(Semilla.de("botanico", "arbol grande"))
        val horquillas = with(PiezasBotanico) { arbolDeRaices(p, pie, 1.5f * e, pie.y + 20f, az, 0.06f) }
        // La liana que abraza el tronco, los helechos y las bromelias que viven en él, y las motas de sol que se cuelan.
        with(PiezasBotanico) {
            val sube = (0..14).map { i -> val t = i / 14f; pie + Offset(sin(t * 9f) * 0.75f * e * (1f - 0.2f * t), -t * pie.y * 0.95f) }
            liana(p, sube, 1.3f, az, 0.06f)
            for ((i, t) in listOf(0.32f, 0.5f, 0.66f).withIndex()) { val q = pie + Offset((if (i % 2 == 0) -0.55f else 0.6f) * e, -t * pie.y); bromelia(p, q, e * az.entre(0.6f, 0.9f), az, 0.06f, flor = i == 1) }
        }
        if (luz.directa > 0.2f && !luz.deLuna) { val lado = if (luz.r.lat >= 0f) 1f else -1f
            repeat(7) { val q = pie + Offset(lado * az.entre(0.05f, 0.6f) * e, -az.entre(0.1f, 0.85f) * pie.y); mancha(q, e * az.entre(0.12f, 0.28f), e * az.entre(0.2f, 0.4f), luz.color, 0.35f * luz.directa) } }
        // Las lianas que cuelgan de las ramas, en lazos que cruzan por arriba hacia la casa.
        with(PiezasBotanico) {
            val ramas = horquillas
            for ((i, h) in ramas.withIndex()) {
                if (i % 2 == 0) { val fin = Offset(h.x + az.entre(-40f, 40f), h.y + az.entre(60f, 110f)); liana(p, listOf(h, h + Offset(az.entre(-6f, 6f), 30f), fin + Offset(0f, -12f), fin), 1.4f, az, 0.05f) }
                else liana(p, listOf(h, Offset((h.x + 95f) * 0.5f, h.y + 55f), Offset(95f, 30f)), 1.6f, az, 0.05f)
            }
            // Las bromelias y las orquídeas en las horquillas.
            for ((i, h) in ramas.withIndex()) bromelia(p, h, e * az.entre(0.7f, 1.1f), az, 0.05f, flor = i % 2 == 0)
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
