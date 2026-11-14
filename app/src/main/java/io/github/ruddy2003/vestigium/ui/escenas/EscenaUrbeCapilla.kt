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
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nube
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.planoH
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.planoV
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/** Una superficie del cuadro: lleva sus dos medidas (a lo largo y a lo alto) a un punto del lienzo. */
internal typealias Plano = (Float, Float) -> Offset

/**
 * La capilla de los frescos: su caja en perspectiva, la ventana con lo que se ve por ella, la mancha de sol y el altar.
 *
 * La capilla se mide en unidades de la pared del fondo (una unidad es una del lienzo en esa pared): [X0] y [X1] son sus
 * paredes, [SUELO] el suelo, [ARRANQUE] donde nace la bóveda. La tercera medida, `t`, es la distancia a la pared del
 * fondo hacia quien mira. [p] lleva un punto de la capilla al lienzo.
 */
internal object CapillaUrbe {
    private val C = ColorUrbe

    /** El punto de fuga: los ojos de quien mira, de pie en la entrada. */
    val FUGA = Offset(182f, 152f)

    /** A cuántas unidades está quien mira de la pared del fondo. */
    private const val OJO = 300f

    const val X0 = 100f
    const val X1 = 264f
    const val ANCHURA = X1 - X0
    const val SUELO = 200f
    const val ARRANQUE = 86f
    private const val FLECHA = 62f

    /** Hasta dónde se dibuja hacia quien mira (más acá ya queda fuera del cuadro). */
    const val CERCA = 196f

    /** Cuánto se agranda lo que está a [t] de la pared del fondo. */
    fun s(t: Float): Float = 1f / (1f - t.coerceIn(-900f, 286f) / OJO)

    /** El punto del lienzo de lo que está en ([x], [y]) a [t] de la pared del fondo. */
    fun p(x: Float, y: Float, t: Float = 0f): Offset { val k = s(t); return Offset(FUGA.x + (x - FUGA.x) * k, FUGA.y + (y - FUGA.y) * k) }

    /** Un punto de la bóveda rebajada: [a] va de 0 (el arranque de la izquierda) a 1 (el de la derecha). */
    fun boveda(a: Float, t: Float): Offset = p(182f - 82f * cos(PI.toFloat() * a), ARRANQUE - FLECHA * sin(PI.toFloat() * a), t)

    val FONDO: Plano = { u, v -> Offset(u, v) }
    val DERECHA: Plano = { t, v -> p(X1, v, t) }
    val IZQUIERDA: Plano = { t, v -> p(X0, v, t) }
    val BOVEDA: Plano = { a, t -> boveda(a, t) }

    // ---- La ventana, en la pared izquierda: de VT0 a VT1 de hondo, del alféizar a la jamba, con su arco encima.
    private const val VT0 = 30f
    private const val VT1 = 130f
    private const val ALFEIZAR = 146f
    private const val JAMBA = 96f
    private const val GRUESO = 15f
    private val ARCO = listOf(30f to 96f, 37f to 87f, 55f to 79.5f, 80f to 76.5f, 105f to 79.5f, 123f to 87f, 130f to 96f)

    /** Desde dónde alumbra el cielo que entra por la ventana. */
    val LUZ_VENTANA = p(X0, 118f, 80f)
    val VELA_IZQ = Offset(160f, 141f)
    val VELA_DER = Offset(204f, 141f)
    val LAMPARILLA = Offset(240f, 118f)

    fun formaDelFondo(): Path = Path().apply { moveTo(X0, SUELO); for (i in 0..16) { val o = boveda(i / 16f, 0f); lineTo(o.x, o.y) }; lineTo(X1, SUELO); close() }
    fun formaDelSuelo(): Path = poligono(p(X0, SUELO), p(X1, SUELO), p(X1, SUELO, CERCA), p(X0, SUELO, CERCA))
    fun formaIzquierda(): Path = poligono(p(X0, ARRANQUE), p(X0, SUELO), p(X0, SUELO, CERCA), p(X0, ARRANQUE, CERCA))
    fun formaDerecha(): Path = poligono(p(X1, ARRANQUE), p(X1, SUELO), p(X1, SUELO, CERCA), p(X1, ARRANQUE, CERCA))
    fun formaDeLaBoveda(): Path = Path().apply {
        for (i in 0..16) { val o = boveda(i / 16f, 0f); if (i == 0) moveTo(o.x, o.y) else lineTo(o.x, o.y) }
        for (i in 16 downTo 0) { val o = boveda(i / 16f, CERCA); lineTo(o.x, o.y) }
        close()
    }

    /** El contorno del hueco de la ventana, [hondo] unidades dentro del muro. */
    private fun hueco(hondo: Float): Path = poligono(buildList { add(p(X0 - hondo, ALFEIZAR, VT0)); for ((t, y) in ARCO) add(p(X0 - hondo, y, t)); add(p(X0 - hondo, ALFEIZAR, VT1)) })

    /** Los cristales de la ventana, entre las rejas: cada uno, sus esquinas (hondo, alto). */
    private fun cristales(): List<List<Pair<Float, Float>>> = buildList {
        val ts = listOf(VT0, 63f, 97f, VT1); val ys = listOf(JAMBA, 113f, 130f, ALFEIZAR); val r = 0.8f
        for (i in 0..2) for (j in 0..2) add(listOf(ts[i] + r to ys[j] + r, ts[i + 1] - r to ys[j] + r, ts[i + 1] - r to ys[j + 1] - r, ts[i] + r to ys[j + 1] - r))
        add(ARCO.map { (t, y) -> t.coerceIn(VT0 + r, VT1 - r) to y + r } + listOf(VT1 - r to JAMBA - r, VT0 + r to JAMBA - r))
    }

    /**
     * Dónde cae el rayo que entra por el punto ([t], [y]) de la ventana: en el suelo, en la pared derecha o, si va
     * hacia el fondo, en la pared del altar; lo que encuentre antes.
     */
    private fun cae(t: Float, y: Float, sol: EscenaUrbe.Entra): Offset {
        val alSuelo = (SUELO - y) / sol.e; val alFondo = if (sol.a < -0.02f) t / -sol.a else Float.MAX_VALUE
        val dx = min(min(alSuelo, ANCHURA), alFondo)
        return p(X0 + dx, y + sol.e * dx, (t + sol.a * dx).coerceAtLeast(0f))
    }

    /** El centro de la mancha de sol: de ahí rebota su luz. */
    fun centroDeLaMancha(sol: EscenaUrbe.Entra): Offset = cae(80f, 120f, sol)

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val l = EscenaUrbe.luces(m); val f = EscenaUrbe.focos(m); val a = Alumbrado(l, f); val ext = m.luces(); val sol = EscenaUrbe.solQueEntra(m)
        drawRect(l.tono(C.HOLLIN), Offset.Zero, Size(ANCHO, ALTO))
        paredes(m, a)
        suelo(m, a)
        with(FrescoUrbe) { pintar(m, a) }
        molduras(a)
        ventana(m, a, ext)
        pilastra(a)
        rincones(l)
        sol?.let { manchaDeSol(it) }
        altar(m, a)
        with(ObraUrbe) { andamios(m, a); primerPlano(m, a, f) }
        // El aire de la capilla: el halo de cada luz encendida y la penumbra de los bordes.
        for (q in f.drop(1)) if (q.fuerza > 0.3f && q.color != ext.solColor) mancha(q.p, q.alcance * 0.5f, q.alcance * 0.4f, q.color, 0.05f * q.fuerza.coerceAtMost(1f))
        drawRect(Brush.radialGradient(0f to Color.Transparent, 0.55f to Color.Transparent, 1f to C.SOMBRA.copy(alpha = 0.3f), center = Offset(170f, 150f), radius = 290f), Offset.Zero, Size(ANCHO, ALTO), blendMode = BlendMode.Multiply)
        grano(Azar(Semilla.de("urbe", "grano")), 0.55f + 0.35f * (1f - l.noche))
    }

    /** El yeso de las paredes y de la bóveda, debajo de la pintura: más sucio cuanto menos se ha limpiado. */
    private fun DrawScope.paredes(m: Momento, a: Alumbrado) {
        val sucio = 0.1f + 0.42f * (1f - EscenaUrbe.fresco(m))
        val yeso = lerp(C.YESO, C.HOLLIN, sucio)
        drawPath(formaDeLaBoveda(), a.en(boveda(0.5f, 40f), lerp(yeso, C.HOLLIN, 0.2f)))
        planoV(a, formaDelFondo(), 182f, 24f, SUELO, yeso)
        planoH(a, formaIzquierda(), 0f, X0, 150f, lerp(yeso, C.HOLLIN, 0.1f), mira = 1f)
        planoH(a, formaDerecha(), X1, ANCHO, 150f, yeso, mira = -1f)
        // Manchas de humedad y desconchones del yeso en la pared de la ventana, que no lleva pintura de figuras.
        val az = Azar(Semilla.de("urbe", "yeso"))
        clipPath(formaIzquierda()) {
            repeat(9) { val o = p(X0, az.entre(100f, 196f), az.entre(4f, 180f)); mancha(o, az.entre(6f, 16f), az.entre(8f, 22f), a.en(o, C.HOLLIN), 0.18f) }
            repeat(5) { val o = p(X0, az.entre(150f, 198f), az.entre(4f, 170f)); mancha(o, az.entre(4f, 9f), az.entre(3f, 7f), a.en(o, C.CAL, 1f), 0.3f) }
        }
    }

    private fun DrawScope.suelo(m: Momento, a: Alumbrado) {
        val az = Azar(Semilla.de("urbe", "suelo")); val n = 8; val w = ANCHURA / n
        for (j in 0 until 10) for (i in 0 until n) {
            val t0 = j * w; val x0 = X0 + i * w
            val base = if ((i + j) % 2 == 0) C.COTTO else lerp(C.COTTO, C.PIEDRA, 0.66f)
            val o = p(x0 + w / 2f, SUELO, t0 + w / 2f)
            drawPath(poligono(p(x0, SUELO, t0), p(x0 + w, SUELO, t0), p(x0 + w, SUELO, t0 + w), p(x0, SUELO, t0 + w)), a.en(o, lerp(base, C.HOLLIN, az.entre(0.02f, 0.2f)), 0f, 0.5f * (1f - t0 / 210f)))
        }
        val junta = a.en(p(182f, SUELO, 60f), C.HOLLIN).copy(alpha = 0.4f)
        for (i in 0..n) drawLine(junta, p(X0 + i * w, SUELO), p(X0 + i * w, SUELO, 205f), 0.5f)
        for (j in 0..10) drawLine(junta, p(X0, SUELO, j * w), p(X1, SUELO, j * w), 0.4f + 0.05f * j)
        clipPath(formaDelSuelo()) {
            // El paso de siglos: una senda más clara de la entrada al altar; y polvo de yeso y gotas de cal de la obra.
            for (k in 0..6) { val o = p(182f + az.entre(-8f, 8f), SUELO, 20f + k * 27f); mancha(o, 20f * s(20f + k * 27f), 5f * s(20f + k * 27f), a.en(o, C.CAL), 0.1f) }
            if (EscenaUrbe.hayAndamio(m)) repeat(46) { val o = p(az.entre(X0 + 4f, X1 - 4f), SUELO, az.entre(2f, 190f)); drawCircle(a.en(o, C.CAL).copy(alpha = az.entre(0.25f, 0.6f)), az.entre(0.4f, 1.1f), o) }
            // Lo que el suelo devuelve de la ventana.
            val v = p(X0 + 26f, SUELO, 86f); mancha(v, 40f, 12f, a.focos[0].color, 0.1f * a.focos[0].fuerza)
        }
    }

    /** La cornisa de piedra donde nace la bóveda y el zócalo, en las dos paredes de los lados. */
    private fun DrawScope.molduras(a: Alumbrado) {
        for ((x, mira) in listOf(X0 to 1f, X1 to -1f)) {
            val cornisa = poligono(p(x, ARRANQUE - 3.4f), p(x, ARRANQUE + 2.6f), p(x, ARRANQUE + 2.6f, CERCA), p(x, ARRANQUE - 3.4f, CERCA))
            drawPath(cornisa, a.en(p(x, ARRANQUE, 60f), C.PIEDRA, mira))
            drawLine(a.en(p(x, ARRANQUE, 60f), C.CAL, mira), p(x, ARRANQUE - 3.4f), p(x, ARRANQUE - 3.4f, CERCA), 0.8f)
            drawLine(a.en(p(x, ARRANQUE, 60f), C.HOLLIN).copy(alpha = 0.5f), p(x, ARRANQUE + 2.6f), p(x, ARRANQUE + 2.6f, CERCA), 0.8f)
        }
        // La pared del fondo: una cenefa de piedra a la altura del arranque.
        drawRect(a.en(Offset(182f, ARRANQUE), C.PIEDRA), Offset(X0, ARRANQUE - 2f), Size(ANCHURA, 3.6f))
        drawLine(a.en(Offset(182f, ARRANQUE), C.CAL), Offset(X0, ARRANQUE - 2f), Offset(X1, ARRANQUE - 2f), 0.7f)
    }

    /**
     * En la pared de la ventana, lo que queda cerca de quien mira: unos exvotos de plata, y la pilastra de la entrada
     * con su pila de agua bendita. Enmarca el cuadro por la izquierda.
     */
    private fun DrawScope.pilastra(a: Alumbrado) {
        for (i in 0..1) for (j in 0..2) { val t = 135f + i * 6.4f; val v = 106f + j * 9f
            drawPath(poligono(p(X0, v, t), p(X0, v, t + 4.4f), p(X0, v + 6f, t + 4.4f), p(X0, v + 6f, t)), a.en(p(X0, v, t), if ((i + j) % 2 == 0) C.ACERO else C.ORO, 1f))
            drawPath(poligono(p(X0, v, t), p(X0, v, t + 4.4f), p(X0, v + 6f, t + 4.4f), p(X0, v + 6f, t)), a.en(p(X0, v, t), C.HOLLIN).copy(alpha = 0.5f), style = Stroke(0.5f)) }
        val t0 = 150f; val t1 = 180f; val x = X0 + 5f; val arriba = ARRANQUE + 2.6f
        drawPath(poligono(p(X0, arriba, t0), p(x, arriba, t0), p(x, SUELO, t0), p(X0, SUELO, t0)), a.en(p(x, 150f, t0), lerp(C.PIEDRA, C.HOLLIN, 0.3f), 1f))
        val fuste = poligono(p(x, arriba, t0), p(x, SUELO, t0), p(x, SUELO, t1), p(x, arriba, t1))
        planoV(a, fuste, 14f, p(x, arriba, t0).y, p(x, SUELO, t0).y, lerp(C.PIEDRA, C.YESO, 0.3f), mira = 1f)
        clipPath(fuste) {
            val az = Azar(Semilla.de("urbe", "pilastra"))
            for (v in listOf(arriba + 5f, arriba + 9f, 188f, 193f)) drawLine(a.en(p(x, v, t0), C.CAL, 1f).copy(alpha = 0.7f), p(x, v, t0), p(x, v, t1), 1.2f)
            drawPath(poligono(p(x, arriba + 14f, t0 + 6f), p(x, 184f, t0 + 6f), p(x, 184f, t1), p(x, arriba + 14f, t1)), a.en(p(x, 140f, t0 + 8f), C.HOLLIN).copy(alpha = 0.12f))
            repeat(8) { val o = p(x, az.entre(40f, 196f), az.entre(t0, t1)); mancha(o, az.entre(3f, 8f), az.entre(8f, 20f), a.en(o, C.HOLLIN), 0.18f) }
        }
        // La pila de agua bendita.
        val o = p(x, 150f, 160f)
        val pila = Path().apply { moveTo(o.x - 3f, o.y - 4f); lineTo(o.x + 15f, o.y - 2f); cubicTo(o.x + 14f, o.y + 8f, o.x + 4f, o.y + 10f, o.x - 3f, o.y + 9f); close() }
        drawPath(pila, a.en(o, C.CAL, 1f)); drawOval(a.en(o, lerp(C.AZUL, C.CAL, 0.5f), 1f), Offset(o.x - 2f, o.y - 4.6f), Size(16f, 3.6f)); drawPath(pila, a.en(o, C.HOLLIN).copy(alpha = 0.3f), style = Stroke(0.6f))
    }

    /** La penumbra de los rincones, que es lo que da hondura a la caja. */
    private fun DrawScope.rincones(l: Luces) {
        val c = C.SOMBRA.copy(alpha = 0.3f)
        drawRect(Brush.horizontalGradient(0f to c, 1f to Color.Transparent, startX = X0, endX = X0 + 16f), Offset(X0, ARRANQUE), Size(16f, SUELO - ARRANQUE), blendMode = BlendMode.Multiply)
        drawRect(Brush.horizontalGradient(0f to Color.Transparent, 1f to c, startX = X1 - 16f, endX = X1), Offset(X1 - 16f, ARRANQUE), Size(16f, SUELO - ARRANQUE), blendMode = BlendMode.Multiply)
        drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to c.copy(alpha = 0.22f), startY = SUELO - 12f, endY = SUELO), Offset(X0, SUELO - 12f), Size(ANCHURA, 12f), blendMode = BlendMode.Multiply)
        clipPath(formaDelSuelo()) { drawRect(Brush.verticalGradient(0f to c, 1f to Color.Transparent, startY = SUELO, endY = SUELO + 14f), Offset(X0 - 10f, SUELO), Size(ANCHURA + 20f, 14f), blendMode = BlendMode.Multiply) }
        if (l.noche > 0.3f) drawRect(Brush.verticalGradient(0f to c.copy(alpha = 0.3f * l.noche), 1f to Color.Transparent, startY = 0f, endY = 90f), Offset.Zero, Size(ANCHO, 90f), blendMode = BlendMode.Multiply)
    }

    // ---------------------------------------------------------------- La ventana y lo que se ve por ella

    private fun DrawScope.ventana(m: Momento, a: Alumbrado, ext: Luces) {
        val dentro = hueco(0f); val vidrio = hueco(GRUESO)
        // El derrame del muro: de día es lo más claro de la capilla.
        drawPath(dentro, lerp(a.en(LUZ_VENTANA, C.YESO), lerp(ext.medio, Color.White, 0.5f), 0.4f * (1f - ext.noche)))
        clipPath(dentro) {
            clipPath(vidrio) { fuera(m, ext) }
            // Las rejas de hierro, a contraluz.
            val reja = lerp(a.en(LUZ_VENTANA, C.HIERRO), Color(0xFF1A1A22), 0.5f)
            for (t in listOf(63f, 97f)) drawLine(reja, p(X0 - GRUESO, ALFEIZAR, t), p(X0 - GRUESO, 77f, t), 1.1f)
            for (y in listOf(JAMBA, 113f, 130f)) drawLine(reja, p(X0 - GRUESO, y, VT0), p(X0 - GRUESO, y, VT1), 1f)
        }
        // El marco de piedra, con su filo de luz por dentro.
        drawPath(dentro, a.en(LUZ_VENTANA + Offset(20f, 30f), C.PIEDRA, 1f), style = Stroke(2.8f))
        drawPath(dentro, lerp(ext.medio, Color.White, 0.6f).copy(alpha = 0.5f * (1f - ext.noche)), style = Stroke(0.7f))
        // El alféizar, que sobresale, y la claridad que el cielo deja en la pared de alrededor.
        drawLine(a.en(LUZ_VENTANA + Offset(0f, 30f), C.PIEDRA, 1f), p(X0, ALFEIZAR + 1.5f, VT0 - 3f), p(X0, ALFEIZAR + 1.5f, VT1 + 3f), 3.4f)
        mancha(LUZ_VENTANA, 62f, 70f, lerp(ext.medio, Color.White, 0.5f), 0.1f * (1f - ext.noche) * (1f - 0.4f * ext.nublado))
    }

    /** Lo que se ve por la ventana, que queda por encima de los ojos: el cielo del día y lo más alto de los tejados. */
    private fun DrawScope.fuera(m: Momento, ext: Luces) {
        val h = 176f; val semilla = m.semilla("cielo")
        cielo(ext, h, semilla); astros(ext, h, semilla); nubes(ext, h, m.ficha.clima, semilla)
        // Una o dos nubes del día que pasan justo por el hueco.
        val az = m.azar("ventana")
        if (m.ficha.clima == Clima.DESPEJADO || m.ficha.clima == Clima.NUBES || m.ficha.clima == Clima.VIENTO) repeat(1 + az.entero(2)) { nube(ext, az.entre(8f, 52f), az.entre(78f, 118f), az.entre(22f, 40f), az, if (m.ficha.clima == Clima.NUBES) 0.2f else 0f, 0.5f) }
        val r = Rayo(ext); val luz = if (r.lat > 0f) 1f else -1f
        // Lo alto de Roma: una cúpula con su linterna, un campanario y la copa de un pino.
        val plomo = ext.tono(C.PLOMO, 0.5f); val ocre = ext.tono(C.OCRE_ROMA, 0.45f)
        val cupula = Path().apply { moveTo(34f, 150f); cubicTo(34f, 134f, 42f, 126f, 51f, 126f); cubicTo(60f, 126f, 68f, 134f, 68f, 150f); close() }
        drawRect(ocre, Offset(31f, 144f), Size(40f, 12f)); drawPath(cupula, plomo)
        clipPath(cupula) { drawRect(ext.tono(lerp(C.PLOMO, Color.White, 0.3f), 0.5f).copy(alpha = 0.7f * r.dia), Offset(if (luz > 0f) 53f else 30f, 120f), Size(15f, 34f)); for (x in listOf(42f, 51f, 60f)) drawLine(ext.tono(C.HIERRO, 0.6f).copy(alpha = 0.35f), Offset(x, 150f), Offset(51f + (x - 51f) * 0.2f, 126f), 0.5f) }
        drawRect(ocre, Offset(48.6f, 119f), Size(4.8f, 7.4f)); drawPath(poligono(Offset(47.8f, 119.4f), Offset(51f, 114f), Offset(54.2f, 119.4f)), plomo)
        drawRect(ext.tono(C.ROSA_ROMA, 0.4f), Offset(17f, 128f), Size(9f, 30f)); drawRect(ext.tono(C.HOLLIN, 0.5f).copy(alpha = 0.6f), Offset(19.6f, 132f), Size(3.8f, 5.4f)); drawPath(poligono(Offset(16f, 128.4f), Offset(21.5f, 121f), Offset(27f, 128.4f)), ext.tono(C.TEJA, 0.4f))
        drawOval(ext.tono(C.PINO, 0.42f), Offset(66f, 136f), Size(26f, 9f)); drawOval(ext.tono(C.PINO_LUZ, 0.42f).copy(alpha = 0.8f * r.dia), Offset(if (luz > 0f) 76f else 67f, 136.6f), Size(13f, 4f))
        if (ext.noche > 0.5f) for ((x, y) in listOf(20.6f to 134.6f, 44f to 148f, 58f to 147f)) drawCircle(Color(0xFFFFC56A).copy(alpha = 0.8f * ext.noche), 0.9f, Offset(x, y))
        // Lo que pasa hoy en la ciudad y se ve de lejos: el humo blanco, la estela de tres colores del desfile.
        if (EscenaUrbe.humoBlanco(m)) for (k in 0..5) mancha(Offset(30f + k * 2.2f, 124f - k * 6f), 3f + k * 1.3f, 3.4f + k, ext.tono(Color.White), 0.6f - 0.07f * k)
        if (EscenaUrbe.desfile(m) && ext.noche < 0.4f) for ((k, c) in listOf(Color(0xFF4E9A5C), Color.White, Color(0xFFCC4A3A)).withIndex()) drawLine(c.copy(alpha = 0.7f), Offset(6f, 92f + k * 3.4f), Offset(96f, 70f + k * 3.4f), 2.4f, StrokeCap.Round)
        if (m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA) for (k in 0..26) { val x = az.entre(6f, 92f); val y = az.entre(50f, 146f); drawLine(Color.White.copy(alpha = 0.3f), Offset(x, y), Offset(x - 1.2f, y + 5f), 0.5f) }
    }

    /**
     * La mancha de sol que entra por la ventana: cada cristal, llevado por el rayo hasta el suelo, la pared derecha y
     * la pared del fondo, y recortado en cada una. **[Aproximación]**: lo que hay en medio (el andamio, el altar) no
     * la corta. Es la pieza que hará falta en los otros interiores con ventana.
     */
    private fun DrawScope.manchaDeSol(sol: EscenaUrbe.Entra) {
        val enSuelo = Path(); val enDerecha = Path(); val enFondo = Path()
        for (c in cristales()) {
            enSuelo.addPath(poligono(c.map { (t, y) -> val dx = (SUELO - y) / sol.e; p(X0 + dx, SUELO, t + sol.a * dx) }))
            enDerecha.addPath(poligono(c.map { (t, y) -> p(X1, y + sol.e * ANCHURA, t + sol.a * ANCHURA) }))
            if (sol.a < -0.02f) enFondo.addPath(poligono(c.map { (t, y) -> val dx = t / -sol.a; Offset(X0 + dx, y + sol.e * dx) }))
        }
        // De sol, cálida; de luna, fría. El borde, blando: una penumbra de tres trazos, cada uno más ancho y más tenue.
        val color = if (sol.color == C.LUNA) sol.color else lerp(sol.color, Color(0xFFFFD49A), 0.4f)
        for ((donde, que) in listOf(formaDelSuelo() to enSuelo, formaDerecha() to enDerecha, formaDelFondo() to enFondo)) clipPath(donde) {
            // En las paredes, que tienen hollín, la mancha se nota menos que en el suelo.
            val k = sol.fuerza * (if (que === enSuelo) 1f else 0.62f)
            for (ancho in listOf(8f, 5f, 2.4f)) drawPath(que, color.copy(alpha = 0.04f * k), style = Stroke(ancho), blendMode = BlendMode.Plus)
            drawPath(que, color.copy(alpha = 0.3f * k), blendMode = BlendMode.Plus)
        }
        // El haz en el aire, y en él el polvo de la obra.
        val boca = buildList { add(VT0 to ALFEIZAR); addAll(ARCO); add(VT1 to ALFEIZAR) }
        for (i in boca.indices) { val (t0, y0) = boca[i]; val (t1, y1) = boca[(i + 1) % boca.size]
            drawPath(poligono(p(X0, y0, t0), p(X0, y1, t1), cae(t1, y1, sol), cae(t0, y0, sol)), color.copy(alpha = 0.03f * sol.fuerza), blendMode = BlendMode.Plus) }
        val az = Azar(Semilla.de("urbe", "polvo"))
        repeat(44) { val t = az.entre(VT0, VT1); val y = az.entre(82f, ALFEIZAR); val k = az.entre(0.08f, 0.96f); val a = p(X0, y, t); val b = cae(t, y, sol)
            drawCircle(color.copy(alpha = (0.5f * sol.fuerza * az.real()).coerceIn(0f, 1f)), az.entre(0.3f, 0.7f), a + (b - a) * k) }
    }

    // ---------------------------------------------------------------- El altar

    private fun DrawScope.altar(m: Momento, a: Alumbrado) {
        // La tarima.
        drawPath(poligono(p(134f, 194f, 0f), p(230f, 194f, 0f), p(230f, 194f, 34f), p(134f, 194f, 34f)), a.en(p(182f, 194f, 20f), lerp(C.PIEDRA, C.COTTO, 0.3f)))
        drawPath(poligono(p(134f, 194f, 34f), p(230f, 194f, 34f), p(230f, SUELO, 34f), p(134f, SUELO, 34f)), a.en(p(182f, 198f, 34f), lerp(C.PIEDRA, C.HOLLIN, 0.3f)))
        // El bloque del altar: el frente, con su recuadro de mármoles, y la mesa.
        val f0 = p(150f, 164f, 22f); val f1 = p(214f, 194f, 22f)
        planoH(a, poligono(f0, Offset(f1.x, f0.y), f1, Offset(f0.x, f1.y)), f0.x, f1.x, 180f, C.PIEDRA)
        drawRect(a.en(Offset(182f, 182f), lerp(C.ROJO, C.PIEDRA, 0.35f)), Offset(f0.x + 6f, f0.y + 5f), Size(f1.x - f0.x - 12f, f1.y - f0.y - 9f))
        drawPath(poligono(Offset(182f, f0.y + 7f), Offset(f1.x - 12f, (f0.y + f1.y) / 2f), Offset(182f, f1.y - 6f), Offset(f0.x + 12f, (f0.y + f1.y) / 2f)), a.en(Offset(182f, 182f), lerp(C.VERDE, C.PIEDRA, 0.3f)))
        drawCircle(a.en(Offset(182f, 182f), C.ORO), 3.4f, Offset(182f, (f0.y + f1.y) / 2f))
        drawPath(poligono(p(146f, 162f, 2f), p(218f, 162f, 2f), p(218f, 162f, 25f), p(146f, 162f, 25f)), a.en(Offset(182f, 162f), lerp(C.PIEDRA, C.CAL, 0.3f)))
        // El mantel, que cuelga por delante con su puntilla. En luto, un paño negro encima.
        val m0 = p(146f, 162f, 25f); val m1 = p(218f, 168.5f, 25f)
        drawRect(a.en(Offset(182f, 165f), C.CAL), m0, Size(m1.x - m0.x, m1.y - m0.y))
        for (k in 0..17) drawCircle(a.en(Offset(182f, 168f), C.CAL), 1.25f, Offset(m0.x + 2.2f + k * (m1.x - m0.x - 4.4f) / 17f, m1.y))
        if (EscenaUrbe.luto(m)) { val negro = a.en(Offset(182f, 166f), Color(0xFF2A2630)); drawRect(negro, Offset(m0.x, m1.y + 2f), Size(m1.x - m0.x, 4f)); drawPath(poligono(Offset(178f, m1.y + 2f), Offset(186f, m1.y + 2f), Offset(189f, m1.y + 16f), Offset(182f, m1.y + 11f), Offset(175f, m1.y + 16f)), negro) }
        // El crucifijo y los dos candeleros de latón.
        val laton = a.en(Offset(182f, 150f), lerp(C.ORO, C.MADERA_OSC, 0.25f))
        drawLine(laton, Offset(182f, 162f), Offset(182f, 134f), 1.6f); drawLine(laton, Offset(176.6f, 141f), Offset(187.4f, 141f), 1.5f); drawRect(laton, Offset(178f, 159.6f), Size(8f, 2.6f))
        for (v in listOf(VELA_IZQ, VELA_DER)) {
            drawPath(poligono(Offset(v.x - 3.4f, 162.4f), Offset(v.x + 3.4f, 162.4f), Offset(v.x + 1f, 159f), Offset(v.x - 1f, 159f)), laton); drawLine(laton, Offset(v.x, 159.4f), Offset(v.x, 152f), 1.5f); drawRect(laton, Offset(v.x - 2f, 151f), Size(4f, 1.3f))
            drawRect(a.en(v, C.CERA), Offset(v.x - 1f, v.y + 1f), Size(2f, 10f))
        }
        // La lamparilla, que cuelga de su brazo de hierro y no se apaga.
        val hierro = a.en(LAMPARILLA, C.HIERRO)
        drawLine(hierro, Offset(X1 - 1f, 100f), Offset(LAMPARILLA.x, 102f), 1f); drawLine(hierro, Offset(LAMPARILLA.x, 102f), Offset(LAMPARILLA.x, LAMPARILLA.y - 3f), 0.6f)
        drawPath(poligono(LAMPARILLA + Offset(-2.6f, -3f), LAMPARILLA + Offset(2.6f, -3f), LAMPARILLA + Offset(1.8f, 3.4f), LAMPARILLA + Offset(-1.8f, 3.4f)), Color(0xFFC83A2A)); drawCircle(Color(0xFFFFB070), 1.1f, LAMPARILLA)
        resplandor(LAMPARILLA, 9f, 0.5f, Color(0xFFFF7446))
        // En fiesta, flores en el altar y al pie; en Navidad, el belén de la capilla en la tarima.
        if (EscenaUrbe.fiesta(m) || EscenaUrbe.abierta(m)) {
            val az = Azar(Semilla.de("urbe", "flores"))
            for (x in listOf(169f, 195f)) { drawRect(a.en(Offset(x, 158f), C.AZUL), Offset(x - 1.8f, 157f), Size(3.6f, 5f)); repeat(7) { drawCircle(a.en(Offset(x, 152f), if (it % 3 == 0) C.ROJO else C.CAL), 1.4f, Offset(x + az.entre(-3.4f, 3.4f), 153.4f + az.entre(-3.4f, 2.6f))) } }
        }
        if (EscenaUrbe.navidad(m)) {
            val b = p(160f, 194f, 28f)
            drawPath(poligono(b + Offset(-9f, 0f), b + Offset(9f, 0f), b + Offset(9f, -8f), b + Offset(0f, -13f), b + Offset(-9f, -8f)), a.en(b, C.MADERA_OSC)); drawRect(a.en(b, C.HOLLIN), b + Offset(-6f, -7f), Size(12f, 7f))
            drawCircle(a.en(b, C.ORO), 1.5f, b + Offset(0f, -15.4f)); for (dx in listOf(-3f, 0f, 3f)) drawCircle(a.en(b, if (dx == 0f) C.CAL else C.AZUL), 1.2f, b + Offset(dx, -1.6f))
        }
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val ext = m.luces()
        if (EscenaUrbe.velas(m)) {
            for ((i, v) in listOf(VELA_IZQ, VELA_DER).withIndex()) { resplandor(v, 9f, 0.5f, C.VELA); llama(v + Offset(0f, 1f), 4.4f, reloj, 31 + i) }
            with(ObraUrbe) { llamasDelVelero(m, reloj) }
        }
        if (EscenaUrbe.sinLuz(m)) with(ObraUrbe) { llamaDelFarol(m, reloj) }
        // La lamparilla late despacio.
        drawCircle(Color(0xFFFFD0A0).copy(alpha = 0.5f + 0.4f * sin(reloj * 2.3f)), 0.8f, LAMPARILLA)
        // Lo que pasa fuera se cuela por la ventana: el relámpago, los cohetes de la noche de año nuevo.
        if (m.ficha.clima == Clima.TORMENTA) { val k = sin(reloj * 0.9f) * sin(reloj * 3.7f + 1f); if (k > 0.86f) { drawPath(hueco(0f), Color.White.copy(alpha = 0.5f)); drawRect(Color.White.copy(alpha = 0.05f), Offset.Zero, Size(ANCHO, ALTO)) } }
        if (EscenaUrbe.hoyEs(m, "capodanno") && ext.noche > 0.6f) clipPath(hueco(GRUESO)) {
            val az = Azar(Semilla.de("urbe", "cohetes", (reloj * 0.6f).toInt()))
            repeat(2) { val c = Offset(az.entre(24f, 74f), az.entre(84f, 124f)); val col = listOf(C.ORO, C.ROJO, C.CAL, C.VERDE)[az.entero(4)]; val k = (reloj * 0.6f) % 1f
                for (j in 0 until 10) { val t = 6.2832f * j / 10f; drawCircle(lerp(col, Color.White, 0.4f).copy(alpha = 1f - k), 0.8f, c + Offset(cos(t), sin(t)) * (2f + 9f * k)) } }
        }
    }
}
