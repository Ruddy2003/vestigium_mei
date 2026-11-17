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

    /** Los árboles de la casa vieja: dónde pisan y cuánto miden (sus sombras van en [sombraDeLosBloques]). */
    private val ARBOLES_VIEJOS = listOf(Triple(190f, 162f, 172f), Triple(228f, 118f, 66f), Triple(330f, 104f, 34f))

    private fun DrawScope.casaVieja(m: Momento, ext: Luces, p: Pincel, caja: Rect) {
        val nv = R.nieve(m); val hojas = R.hojas(m); val amar = R.amarillas(m); val noche = ext.noche; val band = R.banderas(m)
        with(PiezasFueraUrss) {
            // Lo lejano, en capas y recortado contra el cielo: los bloques del fondo, la torre de Ostánkino, la central con su
            // vapor, la torre del Kremlin con su estrella y la fábrica con el cartel del plan.
            filaDeBloques(m, ext, p, 240f, 330f, H + 1.6f, 9f, 0.88f, Azar(Semilla.de("urss", "fondo viejo 1")), nuevos = false)
            ostankino(ext, p, Offset(264f, H + 1f), 60f, 0.5f, luzRoja = noche > 0.3f || ext.nublado > 0.6f)
            torreDelKremlin(ext, p, Offset(288f, H + 2f), 26f, 0.55f)
            chimeneaConVapor(ext, p, Offset(318f, H + 2f), 54f, 0.78f, R.frio(m), m.viento)
            bruma(ext, m, caja, H - 30f, H + 6f, 0.9f)
            fabrica(ext, p, Offset(248f, H + 4f), 48f, 0.6f, R.plan(m), humo = true, az = Azar(Semilla.de("urss", "fabrica")))
        }
        bruma(ext, m, caja, H - 14f, H + 10f, 0.7f)
        // El suelo del patio, de la base de los bloques a la ventana: ocupa casi la mitad de abajo del hueco.
        val suelo = poligono(listOf(Offset(160f, 103f), Offset(250f, 96f), Offset(250f, H + 6f), Offset(336f, H + 6f), Offset(336f, 150f), Offset(160f, 150f)))
        suelo(m, ext, p, suelo, H + 4f, 142f)
        // El bloque de al lado, lejos, a la derecha del hueco, y el de enfrente.
        val otro = Fachada(Offset(300f, 72f), Offset(300f, 97f), Offset(336f, 75f), Offset(336f, 95f))
        with(PiezasFueraUrss) { bloqueViejo(m, ext, p, otro, 0.45f, nv) }
        val f = Fachada(Offset(160f, 42f), Offset(160f, 103f), Offset(250f, 57f), Offset(250f, 96f))
        with(PiezasFueraUrss) { bloqueViejo(m, ext, p, f, 0.22f, nv) }
        if (band == EscenaUrss.Banderas.BALCONES || band == EscenaUrss.Banderas.TODAS) banderasEnLaFachada(m, ext, p, f, 0.22f)
        bruma(ext, m, caja, 90f, 112f, 0.4f)
        // La sombra grande de los bloques en el patio, con la franja de sol que entra por el hueco.
        sombraDeLosBloques(m, ext, f, otro, ARBOLES_VIEJOS)
        // El patio, cerca y grande: la pista de hielo, el arenero, el columpio, el banco del portal, las farolas y las sendas.
        sendas(m, ext, p)
        with(PiezasFueraUrss) {
            if (R.pista(m)) {
                val pz = Path().apply { moveTo(214f, 112f); cubicTo(236f, 106f, 290f, 104f, 306f, 110f); cubicTo(318f, 115f, 310f, 126f, 290f, 128f); cubicTo(258f, 131f, 224f, 130f, 212f, 123f); cubicTo(204f, 118f, 206f, 114f, 214f, 112f); close() }
                pista(ext, p, pz, Rect(206f, 104f, 314f, 130f), 0.06f, Azar(Semilla.de("urss", "pista")))
            }
            banco(ext, p, Offset(192f, 106f), 15f, nv, 0.18f, patines = R.pista(m))
            arenero(ext, p, Offset(178f, 128f), 22f, nv, 0.08f)
            columpio(ext, p, Offset(312f, 122f), 20f, nv, 0.12f)
            farola(ext, p, Offset(238f, 104f), 30f, R.farolas(m), nv, 0.15f, band == EscenaUrss.Banderas.FAROLAS || band == EscenaUrss.Banderas.TODAS)
            farola(ext, p, Offset(304f, 141f), 54f, R.farolas(m), nv, 0.02f, band == EscenaUrss.Banderas.FAROLAS || band == EscenaUrss.Banderas.TODAS)
        }
        // Los árboles grandes del patio: el de la izquierda, muy cerca, sube por todo el hueco; los otros, delante del bloque.
        with(PiezasFueraUrss) {
            arbolViejo(ext, p, Offset(ARBOLES_VIEJOS[1].first, ARBOLES_VIEJOS[1].second), ARBOLES_VIEJOS[1].third, hojas, amar, nv, Azar(Semilla.de("urss", "arbol 2")), 0.15f)
            arbolViejo(ext, p, Offset(ARBOLES_VIEJOS[2].first, ARBOLES_VIEJOS[2].second), ARBOLES_VIEJOS[2].third, hojas, amar, nv, Azar(Semilla.de("urss", "arbol 3")), 0.35f)
            arbolViejo(ext, p, Offset(ARBOLES_VIEJOS[0].first, ARBOLES_VIEJOS[0].second), ARBOLES_VIEJOS[0].third, hojas, amar, nv, Azar(Semilla.de("urss", "arbol 1")), 0.02f)
        }
        // De noche, la luz de las farolas en la nieve y en el aire.
        if (R.farolas(m)) with(PiezasFueraUrss) { luzDeFarola(ext, Offset(238f, 104f), 30f, nv, 0.65f); luzDeFarola(ext, Offset(304f, 141f), 54f, nv, 0.8f) }
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
    private fun DrawScope.sombraDeLosBloques(m: Momento, ext: Luces, f: Fachada, otro: Fachada, arboles: List<Triple<Float, Float, Float>>) {
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
            for ((x, y, h) in arboles) { val a = Offset(x, y); drawLine(c, a, hacia(a, largo * h / 120f), (h * 0.02f).coerceIn(0.8f, 3f), StrokeCap.Round) }
        }
    }

    /** Las sendas pisadas en la nieve, del portal a la calle y alrededor de la pista, con sus bordes amontonados. */
    private fun DrawScope.sendas(m: Momento, ext: Luces, p: Pincel) {
        val nv = R.nieve(m); if (nv < 0.4f) return
        val pisada = p.sombra(lerp(C.NIEVE, C.HORMIGON, 0.35f), 0.2f)
        // Del portal hacia quien mira, y otra a lo largo del bloque; con su borde de nieve amontonada y las huellas.
        for ((senda, ancho) in listOf(Path().apply { moveTo(206f, 101f); cubicTo(204f, 116f, 196f, 128f, 210f, 148f) } to 4.2f, Path().apply { moveTo(206f, 101f); cubicTo(236f, 100f, 280f, 99f, 340f, 98f) } to 2.2f, Path().apply { moveTo(318f, 150f); cubicTo(314f, 136f, 322f, 124f, 336f, 116f) } to 3.4f)) {
            drawPath(senda, p.luz(C.NIEVE).copy(alpha = 0.6f), style = Stroke(ancho + 1.4f, cap = StrokeCap.Round))
            drawPath(senda, pisada, style = Stroke(ancho, cap = StrokeCap.Round))
            drawPath(senda, p.sombra(lerp(C.NIEVE, C.TIERRA, 0.3f), 0.1f).copy(alpha = 0.5f), style = Stroke(ancho * 0.3f, cap = StrokeCap.Round))
        }
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
            // Lo lejano: filas de bloques a distintas distancias, una torre de dieciséis pisos, las grúas, la central, la
            // fábrica, la torre de Ostánkino y la del Kremlin, recortadas contra el cielo, que desde el noveno se ve grande.
            filaDeBloques(m, ext, p, 166f, 336f, H + 1f, 7f, 0.92f, Azar(Semilla.de("urss", "fondo nuevo 1")), nuevos = true)
            ostankino(ext, p, Offset(232f, H + 1f), 62f, 0.5f, luzRoja = noche > 0.3f || ext.nublado > 0.6f)
            torreDelKremlin(ext, p, Offset(304f, H + 1.6f), 22f, 0.6f)
            chimeneaConVapor(ext, p, Offset(300f, H + 1f), 40f, 0.82f, R.frio(m), m.viento)
            bruma(ext, m, caja, H - 26f, H + 4f, 0.9f)
            grua(ext, p, Offset(208f, H + 5f), 36f, 0.66f, 1f)
            fabrica(ext, p, Offset(254f, H + 5f), 34f, 0.66f, R.plan(m), humo = true, az = Azar(Semilla.de("urss", "fabrica")))
            filaDeBloques(m, ext, p, 170f, 336f, H + 6f, 12f, 0.7f, Azar(Semilla.de("urss", "fondo nuevo 2")), nuevos = true)
            torreDeBloques(m, ext, p, Offset(262f, H + 7f), 14f, 48f, 0.55f)
            grua(ext, p, Offset(326f, H + 9f), 44f, 0.5f, 0.8f)
        }
        bruma(ext, m, caja, H - 14f, H + 14f, 0.7f)
        // El suelo: el descampado de barro (o de nieve) entre los bloques.
        val suelo = poligono(listOf(Offset(160f, 124f), Offset(284f, 113f), Offset(284f, H + 12f), Offset(336f, H + 12f), Offset(336f, 150f), Offset(160f, 150f)))
        suelo(m, ext, p, suelo, H + 12f, 142f)
        // La otra fila, más lejos y más baja, a la derecha; el bloque de enfrente, con su testero en sombra a la izquierda.
        val otro = Fachada(Offset(290f, 90f), Offset(290f, 112f), Offset(336f, 92f), Offset(336f, 110f))
        with(PiezasFueraUrss) { bloqueNuevo(m, ext, p, otro, 0.48f, nv, 9, 7, 1000) }
        val f = Fachada(Offset(192f, 62f), Offset(192f, 124f), Offset(284f, 74f), Offset(284f, 113f))
        with(PiezasFueraUrss) { testero(ext, p, Offset(168f, 66f), Offset(168f, 128f), f.tl, f.bl, 0.22f, nv); bloqueNuevo(m, ext, p, f, 0.22f, nv, 9, 9, 2000) }
        if (band == EscenaUrss.Banderas.BALCONES || band == EscenaUrss.Banderas.TODAS) with(PiezasFueraUrss) {
            for ((piso, col) in listOf(2 to 1, 5 to 7, 7 to 4, 3 to 4)) banderaDeBalcon(ext, p, f.q((col + 0.8f) / 9f, (piso + 0.6f) / 9f), f.alto((col + 0.8f) / 9f) * 0.08f, 0.22f, R.diaNumero(m, "revolucion") == 2)
        }
        bruma(ext, m, caja, 96f, 120f, 0.4f)
        sombraDeLosBloques(m, ext, f, otro, emptyList())
        // El descampado: los palos recién plantados en fila, la zanja con los tubos, las losas de hormigón, un banco nuevo.
        obra(m, ext, p)
        if (R.pista(m)) with(PiezasFueraUrss) {
            val pz = Path().apply { moveTo(266f, 126f); cubicTo(286f, 121f, 326f, 122f, 332f, 127f); cubicTo(336f, 132f, 306f, 136f, 282f, 135f); cubicTo(264f, 134f, 258f, 129f, 266f, 126f); close() }
            pista(ext, p, pz, Rect(258f, 121f, 336f, 136f), 0.06f, Azar(Semilla.de("urss", "pista nueva")))
        }
        with(PiezasFueraUrss) {
            val az = Azar(Semilla.de("urss", "palos"))
            for (i in 0..6) { val x = 180f + i * 16f + az.entre(-2f, 2f); arbolJoven(ext, p, Offset(x, 127f + (i % 2) * 1.4f), 13f + az.entre(-1.5f, 1.5f), hojas, nv, 0.12f) }
            for (i in 0..3) { val x = 196f + i * 34f; arbolJoven(ext, p, Offset(x, 142f), 21f, hojas, nv, 0.02f) }
            banco(ext, p, Offset(236f, 126f), 14f, nv, 0.1f)
            farola(ext, p, Offset(254f, 122f), 30f, R.farolas(m), nv, 0.12f, band == EscenaUrss.Banderas.FAROLAS || band == EscenaUrss.Banderas.TODAS)
            farola(ext, p, Offset(316f, 141f), 48f, R.farolas(m), nv, 0.02f, band == EscenaUrss.Banderas.FAROLAS || band == EscenaUrss.Banderas.TODAS)
        }
        if (R.farolas(m)) with(PiezasFueraUrss) { luzDeFarola(ext, Offset(254f, 122f), 30f, nv, 0.6f); luzDeFarola(ext, Offset(316f, 141f), 48f, nv, 0.8f) }
        nocheAzul(m, ext, caja)
    }

    /** Lo que queda de la obra en el barrio nuevo: la zanja abierta con sus tubos, las losas de hormigón apiladas y unas tablas. */
    private fun DrawScope.obra(m: Momento, ext: Luces, p: Pincel) {
        val nv = R.nieve(m); val blanco = lerp(p.arriba(C.NIEVE, 0.1f), C.LUNA, 0.3f * ext.noche)
        // Las losas apiladas.
        for (i in 0..2) { val y = 138f - i * 3f; drawRect(p.frente(C.HORMIGON, 0.05f), Offset(170f + i * 0.8f, y), Size(24f, 3f)); drawRect(p.arriba(C.HORMIGON, 0.05f), Offset(170f + i * 0.8f, y - 0.8f), Size(24f, 0.8f)); drawLine(p.sombra(C.HORMIGON, 0.05f), Offset(170f + i * 0.8f + 12f, y), Offset(170f + i * 0.8f + 12f, y + 3f), 0.4f) }
        if (nv > 0.3f) drawRect(blanco, Offset(171.6f, 131f), Size(23f, 1.4f))
        // Los tubos grandes, tumbados.
        for (i in 0..2) { val c = Offset(206f + i * 6f, 140f); drawCircle(p.sombra(C.HIERRO, 0.05f), 3f, c); drawCircle(p.medio(lerp(C.HIERRO, C.LADRILLO, 0.3f), 0.05f), 2.4f, c); drawCircle(p.sombra(C.TINTA, 0.05f), 1.4f, c) }
        // La zanja, en diagonal, con su montón de tierra.
        drawPath(poligono(Offset(226f, 136f), Offset(258f, 130f), Offset(261f, 133f), Offset(229f, 139.4f)), p.sombra(C.TIERRA, 0.05f))
        drawPath(Path().apply { moveTo(232f, 134f); cubicTo(240f, 129f, 250f, 128f, 258f, 129f); lineTo(232f, 134f); close() }, if (nv > 0.3f) blanco else p.arriba(C.TIERRA, 0.05f))
    }
}
