package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * Lo que se ve por la ventana de la cocina: el patio entre bloques, casi otra escena. Cambia con la casa
 * ([EscenaUrss.casa]) y no con la cocina, como los paisajes de la Ruta de la Seda alrededor de su campamento:
 *
 * - **La casa vieja** (hasta la mudanza), mirada desde un segundo piso: enfrente, el bloque viejo de cinco pisos, de
 *   estuco, con su portal y su banco; delante, los árboles grandes del patio, el arenero, el columpio, las farolas y, en
 *   invierno, la pista de hielo; entre ese bloque y el de al lado, la ciudad lejana en capas: la fábrica con su cartel
 *   del plan, la chimenea de la central con su vapor blanco, la estrella roja del Kremlin; y, por encima del tejado,
 *   la torre de Ostánkino.
 * - **La casa nueva** (desde la mudanza), mirada desde un noveno piso: se ve más ciudad. Enfrente, los bloques blancos
 *   de nueve pisos «todos iguales, en filas», con el barro entre medias y los árboles recién plantados que son palos;
 *   lejos, más filas de bloques, las grúas de las obras, la fábrica, la central y la torre.
 *
 * **La luz de fuera** (es la mitad de la luz del cuadro): con sol, la sombra grande de los bloques cae sobre el patio
 * hacia quien mira, y el sol entra por el hueco entre dos bloques en una franja que camina con la hora (como la franja de
 * sol del valle de Mongol) **[Aproximación]**: no es la sombra real de esos bloques con ese sol. De noche, el azul de la
 * nieve y de la luna, el damero de ventanas encendidas y el naranja de las farolas.
 *
 * **[Diseño propio]**: cómo son el patio y la ciudad. El cuento dice el patio de tierra con árboles y el banco de viejas,
 * los bloques blancos con barro y palos, los patios regados para patinar, las ventanas que se encienden una a una, las
 * farolas naranjas, la torre de Ostánkino con su luz roja y las estrellas del Kremlin «encendidas siempre».
 */
internal object PatioUrss {
    private val C = ColorUrss
    private val R get() = EscenaUrss

    /** El horizonte de fuera: a la altura de los ojos. */
    const val H = 92f

    /** El cielo se pinta a esta escala por el hueco, para que el sol de la mañana y la luna quepan en la ventana. */
    private const val ESCALA_CIELO = 0.4f
    private const val CIELO_X = 170f
    private const val CIELO_Y = 22f

    /** Dónde se ve el sol (o la luna) en el cuadro, por la ventana. */
    internal fun solEnLaVentana(l: Luces): Offset = Offset(CIELO_X + ESCALA_CIELO * l.solX * 380f, CIELO_Y + ESCALA_CIELO * ((H - CIELO_Y) / ESCALA_CIELO) * (1f - l.solAltura))

    fun DrawScope.vista(m: Momento, ext: Luces, caja: Rect) {
        val clima = m.ficha.clima; val semilla = m.semilla("cielo")
        val hor = (H - CIELO_Y) / ESCALA_CIELO
        translate(CIELO_X, CIELO_Y) { scale(ESCALA_CIELO, ESCALA_CIELO, Offset.Zero) { cielo(ext, hor, semilla); astros(ext, hor, semilla); nubes(ext, hor, clima, semilla) } }
        val p = Pincel(ext, Rayo(ext))
        when (R.casa(m)) {
            EscenaUrss.Casa.VIEJA -> casaVieja(m, ext, p, caja)
            EscenaUrss.Casa.NUEVA -> casaNueva(m, ext, p, caja)
        }
        // El tiempo: la niebla por capas ya va en cada plano; aquí el velo de la lluvia y la claridad lechosa de la nieve.
        if (clima != Clima.NIEBLA) tiempoQuieto(ext, clima, H, semilla)
    }

    /** La bruma entre los planos: más con niebla, con nieve que cae y con lluvia; en el aire frío, un poco siempre. */
    private fun DrawScope.bruma(ext: Luces, m: Momento, caja: Rect, y0: Float, y1: Float, cuanta: Float) {
        val k = when (m.ficha.clima) { Clima.NIEBLA -> 0.75f; Clima.NIEVE -> 0.5f; Clima.LLUVIA, Clima.TORMENTA -> 0.4f; Clima.NUBES -> 0.2f; else -> 0.12f } * cuanta
        if (k <= 0.01f) return
        drawRect(Brush.verticalGradient(0f to ext.bruma.copy(alpha = 0f), 0.5f to ext.bruma.copy(alpha = k), 1f to ext.bruma.copy(alpha = k * 0.6f), startY = y0, endY = y1), Offset(caja.left, y0), Size(caja.width, y1 - y0))
    }

    /** El suelo del patio con su nieve, su tierra o su barro, de lejos a cerca: la superficie grande de fuera. */
    private fun DrawScope.suelo(m: Momento, ext: Luces, p: Pincel, forma: Path, y0: Float, y1: Float) {
        val nv = R.nieve(m); val barro = R.barro(m); val hojas = R.hojas(m); val dia = (1f - ext.noche).coerceIn(0f, 1f)
        val tierra = lerp(lerp(C.TIERRA, lerp(C.VERDE, C.LATON, 0.3f), 0.55f * hojas * (1f - R.amarillas(m) * 0.5f)), C.TIERRA, barro)
        val nieve = lerp(C.NIEVE, C.LUNA, 0.45f * ext.noche)
        val cerca = lerp(p.arriba(tierra), p.arriba(nieve), nv); val lejos = lerp(p.arriba(tierra, 0.6f), p.arriba(nieve, 0.6f), nv)
        drawPath(forma, Brush.verticalGradient(0f to lejos, 1f to cerca, startY = y0, endY = y1))
        clipPath(forma) {
            val az = Azar(Semilla.de("urss", "patio suelo", if (R.casaNueva(m)) 2 else 1))
            // Lo que pisa el patio: montones, sendas, charcos; la nieve vieja más gris donde se pisa.
            repeat(30) {
                val y = az.entre(y0, y1); val u = (y - y0) / (y1 - y0).coerceAtLeast(1f); val c = Offset(az.entre(160f, 330f), y)
                mancha(c, (3f + 10f * u), (0.6f + 1.6f * u), if (nv > 0.4f) p.sombra(nieve, 0.4f) else p.sombra(tierra, 0.4f), 0.22f)
            }
            if (nv > 0.4f) repeat(14) { val y = az.entre(y0, y1); val c = Offset(az.entre(160f, 330f), y); mancha(c, az.entre(3f, 9f), az.entre(0.8f, 1.6f), p.luz(nieve), 0.35f * dia) }
            if (barro > 0.3f || R.nieveMojada(m)) repeat(7) {
                // Los charcos, que devuelven el cielo.
                val y = az.entre(y0 + 4f, y1); val u = (y - y0) / (y1 - y0).coerceAtLeast(1f); val c = Offset(az.entre(170f, 320f), y)
                drawOval(lerp(ext.medio, ext.alto, 0.3f), Rect(c.x - (2f + 6f * u), c.y - (0.5f + 1f * u), c.x + (2f + 6f * u), c.y + (0.5f + 1f * u)))
                drawOval(Color.White.copy(alpha = 0.18f * dia), Rect(c.x - (1f + 3f * u), c.y - 0.3f, c.x + (1f + 3f * u), c.y + 0.3f))
            }
            if (nv < 0.4f && R.amarillas(m) > 0.3f) repeat(40) { val c = Offset(az.entre(160f, 330f), az.entre(y0, y1)); drawOval(p.luz(lerp(C.ORO, C.NARANJA, az.real() * 0.5f)), Rect(c.x, c.y, c.x + 1.2f, c.y + 0.6f)) }
            if (R.pelusa(m) > 0.3f) repeat(30) { val c = Offset(az.entre(160f, 330f), az.entre(y0, y1)); drawOval(p.luz(C.PAPEL).copy(alpha = 0.8f), Rect(c.x, c.y, c.x + 2.4f, c.y + 0.8f)) }
        }
    }

    // ---------------------------------------------------------------- La casa vieja

    private fun DrawScope.casaVieja(m: Momento, ext: Luces, p: Pincel, caja: Rect) {
        val nv = R.nieve(m); val hojas = R.hojas(m); val amar = R.amarillas(m); val noche = ext.noche; val band = R.banderas(m)
        val az = Azar(Semilla.de("urss", "patio viejo"))
        with(PiezasFueraUrss) {
            // Lo lejano, en capas: los bloques del fondo, la central con su vapor, la fábrica con el cartel del plan, el Kremlin.
            filaDeBloques(m, ext, p, 236f, 330f, H + 1.6f, 9f, 0.88f, Azar(Semilla.de("urss", "fondo viejo 1")), nuevos = false)
            ostankino(ext, p, Offset(226f, H + 1f), 64f, 0.8f, luzRoja = noche > 0.3f || ext.nublado > 0.6f)
            chimeneaConVapor(ext, p, Offset(286f, H + 1.6f), 44f, 0.82f, R.frio(m), m.viento)
            estrellaDelKremlin(ext, Offset(306f, H - 4f))
            bruma(ext, m, caja, H - 30f, H + 6f, 1f)
            fabrica(ext, p, Offset(258f, H + 3.6f), 36f, 0.7f, R.plan(m), humo = true, az = Azar(Semilla.de("urss", "fabrica")))
            filaDeBloques(m, ext, p, 296f, 330f, H + 5f, 12f, 0.66f, Azar(Semilla.de("urss", "fondo viejo 2")), nuevos = false)
        }
        bruma(ext, m, caja, H - 20f, H + 14f, 0.8f)
        // El suelo del patio, de la base de los bloques a la ventana.
        val suelo = poligono(listOf(Offset(160f, 125f), Offset(262f, 114f), Offset(262f, H + 6f), Offset(330f, H + 6f), Offset(330f, 150f), Offset(160f, 150f)))
        suelo(m, ext, p, suelo, H + 6f, 142f)
        // El bloque de al lado, lejos, a la derecha del hueco.
        val otro = Fachada(Offset(296f, 78f), Offset(296f, 110f), Offset(336f, 81f), Offset(336f, 108f))
        with(PiezasFueraUrss) { bloqueViejo(m, ext, p, otro, 0.45f, nv) }
        // El bloque de enfrente.
        val f = Fachada(Offset(160f, 56f), Offset(160f, 125f), Offset(262f, 70f), Offset(262f, 114f))
        with(PiezasFueraUrss) { bloqueViejo(m, ext, p, f, 0.22f, nv) }
        if (band == EscenaUrss.Banderas.BALCONES || band == EscenaUrss.Banderas.TODAS) banderasEnLaFachada(m, ext, p, f, 0.22f)
        bruma(ext, m, caja, 100f, 128f, 0.5f)
        // La sombra grande de los bloques en el patio, con la franja de sol que entra por el hueco.
        sombraDeLosBloques(m, ext, f, otro)
        // El patio: la pista de hielo, el arenero, el columpio, el banco del portal, las farolas y las sendas.
        with(PiezasFueraUrss) {
            if (R.pista(m)) {
                val pz = Path().apply { moveTo(222f, 126f); cubicTo(240f, 121f, 286f, 119f, 304f, 123f); cubicTo(314f, 127f, 306f, 135f, 290f, 136f); cubicTo(262f, 138f, 230f, 137f, 220f, 132f); cubicTo(214f, 129f, 216f, 127f, 222f, 126f); close() }
                pista(ext, p, pz, Rect(214f, 119f, 314f, 138f), 0.1f, Azar(Semilla.de("urss", "pista")))
            }
            arenero(ext, p, Offset(286f, 112f), 10f, nv, 0.3f)
            columpio(ext, p, Offset(312f, 114f), 10f, nv, 0.32f)
            banco(ext, p, Offset(198f, 122f), 10f, nv, 0.2f, patines = R.pista(m))
            farola(ext, p, Offset(246f, 118f), 24f, R.farolas(m), nv, 0.25f, band == EscenaUrss.Banderas.FAROLAS || band == EscenaUrss.Banderas.TODAS)
            farola(ext, p, Offset(300f, 140f), 40f, R.farolas(m), nv, 0.05f, band == EscenaUrss.Banderas.FAROLAS || band == EscenaUrss.Banderas.TODAS)
        }
        sendas(m, ext, p)
        // Los árboles grandes del patio: el de la izquierda, muy cerca, sube por todo el hueco; los otros, delante del bloque.
        with(PiezasFueraUrss) {
            arbolViejo(ext, p, Offset(276f, 128f), 64f, hojas, amar, nv, Azar(Semilla.de("urss", "arbol 2")), 0.25f)
            arbolViejo(ext, p, Offset(318f, 112f), 34f, hojas, amar, nv, Azar(Semilla.de("urss", "arbol 3")), 0.4f)
            arbolViejo(ext, p, Offset(196f, 156f), 168f, hojas, amar, nv, Azar(Semilla.de("urss", "arbol 1")), 0.05f)
        }
        // De noche, la luz de las farolas en la nieve y en el aire.
        if (R.farolas(m)) with(PiezasFueraUrss) { luzDeFarola(ext, Offset(246f, 118f), 24f, nv, 0.6f); luzDeFarola(ext, Offset(300f, 140f), 40f, nv, 0.8f) }
        nocheAzul(m, ext, caja)
    }

    /** Las banderas rojas de los días de fiesta en los balcones y en las ventanas de la fachada, y la pancarta sin letras. */
    private fun DrawScope.banderasEnLaFachada(m: Momento, ext: Luces, p: Pincel, f: Fachada, lejos: Float) {
        val mojada = R.diaNumero(m, "revolucion") == 2
        with(PiezasFueraUrss) {
            for ((piso, col) in listOf(1 to 1, 2 to 7, 3 to 4, 4 to 1, 1 to 9, 3 to 7)) {
                val a = f.q((col + 0.8f) / 11f, 0.06f + piso * 0.183f + 0.12f)
                banderaDeBalcon(ext, p, a, f.alto((col + 0.8f) / 11f) * 0.12f, lejos, mojada)
            }
        }
        if (R.banderas(m) == EscenaUrss.Banderas.TODAS) {
            // La pancarta roja a lo largo de la planta baja, con su raya de oro (sin letras).
            val pz = f.trozo(0.02f, 0.72f, 0.7f, 0.77f)
            drawPath(pz, lerp(p.frente(C.ROJO, lejos), C.ROJO, 0.25f))
            drawLine(p.luz(C.ORO, lejos), f.q(0.04f, 0.745f), f.q(0.68f, 0.745f), 0.4f)
        }
    }

    /**
     * La sombra de los bloques en el patio con el sol de cara (el sol está detrás de ellos, en el cielo de la ventana):
     * las sombras salen del pie de cada bloque hacia quien mira, por las rectas que vienen del sol en el horizonte; por el
     * hueco entre los dos bloques entra la franja de sol. Larga con el sol bajo; corta a mediodía de verano.
     */
    private fun DrawScope.sombraDeLosBloques(m: Momento, ext: Luces, f: Fachada, otro: Fachada) {
        val r = Rayo(ext); if (r.fuerza < 0.05f) return
        val sol = solEnLaVentana(ext); val fuga = Offset(sol.x, H)
        // Cuánto se alarga la sombra hacia quien mira: con el sol bajo, más allá de la ventana.
        val largo = (34f * r.cot).coerceIn(10f, 90f)
        fun hacia(a: Offset, l: Float): Offset { val d = a - fuga; val n = d / d.getDistance().coerceAtLeast(0.01f); return a + n * (l / n.y.coerceAtLeast(0.2f)) }
        capaSombra(r, 0.9f) { c ->
            for (b in listOf(f, otro)) {
                val a0 = b.bl; val a1 = b.br
                drawPath(poligono(a0, a1, hacia(a1, largo), hacia(a0, largo)), c)
            }
            // Los árboles: sus troncos dejan rayas largas en la nieve.
            for ((x, y, h) in listOf(Triple(196f, 156f, 168f), Triple(276f, 128f, 64f), Triple(318f, 112f, 34f))) { val a = Offset(x, y); drawLine(c, a, hacia(a, largo * h / 120f), 1.6f, StrokeCap.Round) }
        }
    }

    /** Las sendas pisadas en la nieve, del portal a la calle y alrededor de la pista, con sus bordes amontonados. */
    private fun DrawScope.sendas(m: Momento, ext: Luces, p: Pincel) {
        val nv = R.nieve(m); if (nv < 0.4f) return
        val pisada = p.sombra(lerp(C.NIEVE, C.HORMIGON, 0.35f), 0.2f)
        val senda = Path().apply { moveTo(214f, 122f); cubicTo(220f, 128f, 200f, 134f, 182f, 146f) }
        drawPath(senda, pisada, style = Stroke(2.4f, cap = StrokeCap.Round))
        drawPath(senda, p.luz(C.NIEVE).copy(alpha = 0.5f), style = Stroke(0.5f))
        val otra = Path().apply { moveTo(214f, 121f); cubicTo(240f, 120f, 270f, 117f, 330f, 116f) }
        drawPath(otra, pisada, style = Stroke(1.4f, cap = StrokeCap.Round))
    }

    /** De noche, el azul de la nieve y de la luna sobre todo lo de fuera; las farolas y las ventanas destacan en naranja. */
    private fun DrawScope.nocheAzul(m: Momento, ext: Luces, caja: Rect) {
        if (ext.noche < 0.2f) return
        drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to C.LUNA.copy(alpha = 0.12f * ext.noche * (0.4f + R.nieve(m))), startY = H, endY = caja.bottom), Offset(caja.left, H), Size(caja.width, caja.bottom - H), blendMode = BlendMode.Plus)
    }

    // ---------------------------------------------------------------- La casa nueva

    private fun DrawScope.casaNueva(m: Momento, ext: Luces, p: Pincel, caja: Rect) {
        val nv = R.nieve(m); val hojas = R.hojas(m); val noche = ext.noche; val band = R.banderas(m)
        with(PiezasFueraUrss) {
            // Lo lejano: tres filas de bloques blancos, las grúas, la central, la fábrica, la torre y el Kremlin.
            filaDeBloques(m, ext, p, 166f, 330f, H + 1f, 7f, 0.92f, Azar(Semilla.de("urss", "fondo nuevo 1")), nuevos = true)
            ostankino(ext, p, Offset(252f, H + 0.6f), 46f, 0.85f, luzRoja = noche > 0.3f || ext.nublado > 0.6f)
            estrellaDelKremlin(ext, Offset(290f, H - 2.4f))
            chimeneaConVapor(ext, p, Offset(306f, H + 1f), 32f, 0.86f, R.frio(m), m.viento)
            bruma(ext, m, caja, H - 26f, H + 4f, 1f)
            grua(ext, p, Offset(206f, H + 4f), 30f, 0.72f, 1f)
            fabrica(ext, p, Offset(222f, H + 4.6f), 26f, 0.74f, R.plan(m), humo = true, az = Azar(Semilla.de("urss", "fabrica")))
            filaDeBloques(m, ext, p, 170f, 330f, H + 5f, 11f, 0.72f, Azar(Semilla.de("urss", "fondo nuevo 2")), nuevos = true)
            grua(ext, p, Offset(296f, H + 7f), 36f, 0.55f, 0.8f)
        }
        bruma(ext, m, caja, H - 16f, H + 12f, 0.8f)
        // El suelo: el descampado de barro (o de nieve) entre los bloques.
        val suelo = poligono(listOf(Offset(160f, 128f), Offset(262f, 117f), Offset(262f, H + 9f), Offset(330f, H + 9f), Offset(330f, 150f), Offset(160f, 150f)))
        suelo(m, ext, p, suelo, H + 9f, 142f)
        // La otra fila de bloques, más lejos, a la derecha, y el bloque de enfrente.
        val otro = Fachada(Offset(270f, 92f), Offset(270f, 116f), Offset(336f, 93f), Offset(336f, 113f))
        with(PiezasFueraUrss) { bloqueNuevo(m, ext, p, otro, 0.5f, nv, 9, 9, 1000) }
        val f = Fachada(Offset(160f, 64f), Offset(160f, 128f), Offset(262f, 78f), Offset(262f, 117f))
        with(PiezasFueraUrss) { bloqueNuevo(m, ext, p, f, 0.22f, nv, 9, 10, 2000) }
        if (band == EscenaUrss.Banderas.BALCONES || band == EscenaUrss.Banderas.TODAS) with(PiezasFueraUrss) {
            for ((piso, col) in listOf(2 to 1, 5 to 8, 7 to 4, 3 to 6)) banderaDeBalcon(ext, p, f.q((col + 0.8f) / 10f, (piso + 0.6f) / 9f), f.alto((col + 0.8f) / 10f) * 0.08f, 0.22f, R.diaNumero(m, "revolucion") == 2)
        }
        bruma(ext, m, caja, 100f, 124f, 0.5f)
        sombraDeLosBloques(m, ext, f, otro)
        // El descampado: los palos recién plantados en fila, la zanja con los tubos, las losas de hormigón, un banco nuevo.
        with(PiezasFueraUrss) {
            val az = Azar(Semilla.de("urss", "palos"))
            for (i in 0..7) { val x = 176f + i * 18f + az.entre(-2f, 2f); arbolJoven(ext, p, Offset(x, 128f + (i % 2) * 1.4f), 9f + az.entre(-1f, 1f), hojas, nv, 0.2f) }
            for (i in 0..4) { val x = 190f + i * 28f; arbolJoven(ext, p, Offset(x, 140f), 15f, hojas, nv, 0.05f) }
            banco(ext, p, Offset(266f, 124f), 9f, nv, 0.2f)
            farola(ext, p, Offset(232f, 124f), 22f, R.farolas(m), nv, 0.2f, band == EscenaUrss.Banderas.FAROLAS || band == EscenaUrss.Banderas.TODAS)
            farola(ext, p, Offset(312f, 138f), 34f, R.farolas(m), nv, 0.05f, band == EscenaUrss.Banderas.FAROLAS || band == EscenaUrss.Banderas.TODAS)
        }
        obra(m, ext, p)
        if (R.pista(m)) with(PiezasFueraUrss) {
            val pz = Path().apply { moveTo(272f, 128f); cubicTo(290f, 125f, 318f, 126f, 322f, 130f); cubicTo(324f, 134f, 300f, 136f, 282f, 135f); cubicTo(268f, 134f, 264f, 130f, 272f, 128f); close() }
            pista(ext, p, pz, Rect(264f, 125f, 324f, 136f), 0.1f, Azar(Semilla.de("urss", "pista nueva")))
        }
        if (R.farolas(m)) with(PiezasFueraUrss) { luzDeFarola(ext, Offset(232f, 124f), 22f, nv, 0.6f); luzDeFarola(ext, Offset(312f, 138f), 34f, nv, 0.8f) }
        nocheAzul(m, ext, caja)
    }

    /** Lo que queda de la obra en el barrio nuevo: la zanja abierta con sus tubos, las losas de hormigón apiladas y unas tablas. */
    private fun DrawScope.obra(m: Momento, ext: Luces, p: Pincel) {
        val nv = R.nieve(m); val blanco = lerp(p.arriba(C.NIEVE, 0.1f), C.LUNA, 0.3f * ext.noche)
        // Las losas apiladas.
        for (i in 0..2) { val y = 132f - i * 2f; drawRect(p.frente(C.HORMIGON, 0.1f), Offset(186f + i * 0.6f, y), Size(16f, 2f)); drawRect(p.arriba(C.HORMIGON, 0.1f), Offset(186f + i * 0.6f, y - 0.6f), Size(16f, 0.6f)) }
        if (nv > 0.3f) drawRect(blanco, Offset(187f, 127f), Size(15f, 1f))
        // Los tubos grandes, tumbados.
        for (i in 0..2) { val c = Offset(214f + i * 4.2f, 135f); drawCircle(p.sombra(C.HIERRO, 0.1f), 2f, c); drawCircle(p.medio(lerp(C.HIERRO, C.LADRILLO, 0.3f), 0.1f), 1.6f, c); drawCircle(p.sombra(C.TINTA, 0.1f), 0.9f, c) }
        // La zanja, en diagonal.
        drawPath(poligono(Offset(240f, 132f), Offset(262f, 128f), Offset(264f, 130f), Offset(242f, 134.4f)), p.sombra(C.TIERRA, 0.1f))
    }
}
