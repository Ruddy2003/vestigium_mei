package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRefugio.Compuerta
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 * El pasillo grande, visto por el portón de la sala de bombas: «de punta a punta… Al fondo, la Puerta, enorme, redonda,
 * cerrada». Un túnel abierto en la roca («una ciudad escondida en la roca»), sostenido por costillas de acero viejo
 * remachado, con la pintura levantada; entre costilla y costilla, la roca picada, oscura y mojada («brilla la humedad,
 * como un sudor»), con sus chorreras de cal y de óxido; abajo, el zócalo de hormigón con la franja de aviso gastada y las
 * rejillas del aire con su mancha de polvo; los cables grapados a la pared; las puertas de los cuartos, con su chapa sin
 * número; en la clave, la fila de tubos de luz en sus carcasas viejas; en el suelo, la línea amarilla que va hasta la
 * Puerta. En la pared de la izquierda, la compuerta del sector y, más allá, el ventanal de la sala de tanques.
 *
 * **[Diseño propio]**: el cuento no dice cómo es el pasillo por dentro (solo que es largo, tiene tubos en el techo, roca
 * húmeda y la Puerta al fondo); el túnel con costillas, la compuerta, las puertas y el ventanal en su pared son de esta
 * escena. La Puerta y lo de fuera, en [PuertaRefugio].
 */
internal object PasilloRefugio {
    private val C = ColorRefugio
    private val K = CamRefugio
    private val R = EscenaRefugio

    // ---------------------------------------------------------------- Las medidas (en metros)

    /** Las paredes del pasillo, a cada lado. */
    const val IZQ = -3.15f
    const val DER = 1.45f
    const val CENTRO_X = (IZQ + DER) / 2f
    /** Hasta dónde suben las paredes rectas y la clave de la bóveda. */
    const val PARED = 2.75f
    const val BOVEDA = 4.6f
    /** El zócalo de hormigón. */
    const val ZOCALO = 0.9f
    /** Donde empieza el pasillo (detrás del portón) y donde está la Puerta. */
    val BOCA: Float get() = SalaRefugio.FONDO + SalaRefugio.GRUESO
    const val PUERTA_Z = 37.6f
    val LARGO: Float get() = PUERTA_Z - BOCA
    /** La Puerta: un círculo de acero de cuatro metros. */
    val PUERTA_C = V3(CENTRO_X, 2.15f, PUERTA_Z)
    const val PUERTA_R = 2.0f
    /** La línea amarilla que nadie pisa, delante de la Puerta. */
    const val LINEA_Z = PUERTA_Z - 1.7f

    /** Las costillas de acero, cada cuatro metros y medio (entre ellas, la roca). */
    val COSTILLAS: List<Float> = (0..6).map { 9.6f + 4.5f * it }.filter { it < PUERTA_Z - 0.4f }
    /** Los tubos de luz de la clave, uno entre cada dos costillas. */
    val TUBOS: List<Float> = COSTILLAS.zipWithNext { a, b -> (a + b) / 2f }.filter { it < PUERTA_Z - 4f }

    /** La compuerta del sector, en la pared de la izquierda, más allá del ventanal, entre dos costillas. */
    const val COMPUERTA_Z0 = 23.65f
    const val COMPUERTA_Z1 = 25.75f
    const val COMPUERTA_ALTO = 2.35f
    val LUZ_COMPUERTA = V3(IZQ + 0.12f, 2.6f, (COMPUERTA_Z0 + COMPUERTA_Z1) / 2f)

    /**
     * El ventanal de la sala de tanques, en la misma pared, nada más pasar la boca: largo y alto, de costilla a costilla
     * (en esa pared se ve muy al sesgo; aquí, cerca, es donde más se abre).
     */
    const val VENTANAL_Z0 = 9.35f
    const val VENTANAL_Z1 = 20.9f
    const val VENTANAL_Y0 = 0.45f
    const val VENTANAL_Y1 = 2.62f

    /** Las puertas de los cuartos: en qué pared (-1 izquierda, 1 derecha) y de dónde a dónde, a lo largo del pasillo. */
    private val PUERTAS = listOf(Triple(1, 15.0f, 16.1f), Triple(-1, 28.5f, 29.6f), Triple(1, 24.0f, 25.1f))

    /** El centro de la puerta [i] del pasillo, en su pared. */
    fun puerta(i: Int): V3 { val (lado, z0, z1) = PUERTAS[i.coerceIn(0, PUERTAS.size - 1)]; return V3(if (lado < 0) IZQ else DER, 1.0f, (z0 + z1) / 2f) }

    /** Dónde está el árbol de cable de la Nochebuena (pisa el suelo del pasillo, junto a la pared de la derecha). */
    val ARBOL = V3(DER - 0.75f, 0f, BOCA + 1.6f)

    /** La sección del túnel: las paredes rectas y la bóveda, de la pared de la izquierda a la de la derecha (x, y). */
    val SECCION: List<Pair<Float, Float>> = buildList {
        add(IZQ to 0f); add(IZQ to PARED)
        val n = 10
        for (i in 1 until n) { val a = PI.toFloat() * i / n; add((CENTRO_X - (DER - IZQ) / 2f * cos(a)) to (PARED + (BOVEDA - PARED) * sin(a))) }
        add(DER to PARED); add(DER to 0f)
    }

    /** El contorno de la roca: de lo alto del zócalo de la izquierda, por la bóveda, a lo alto del zócalo de la derecha. */
    private val ROCA: List<Pair<Float, Float>> = buildList {
        for (k in 0..3) add(IZQ to (ZOCALO + (PARED - ZOCALO) * k / 3f))
        for (i in 2 until SECCION.size - 2) add(SECCION[i])
        for (k in 0..3) add(DER to (PARED - (PARED - ZOCALO) * k / 3f))
    }

    private fun q(par: Pair<Float, Float>, z: Float) = V3(par.first, par.second, z)

    /** La normal de un tramo de contorno, de ([x0], [y0]) a ([x1], [y1]), hacia dentro del túnel. */
    private fun normalDe(x0: Float, y0: Float, x1: Float, y1: Float): V3 = K.unidad(V3(y1 - y0, -(x1 - x0), 0f))

    private fun normalDentro(i: Int): V3 { val (x0, y0) = SECCION[i]; val (x1, y1) = SECCION[i + 1]; return normalDe(x0, y0, x1, y1) }

    /** Un número de 0 a 1 que sale siempre igual de [a], [b] y [c] (para la roca picada, sin crear un azar por vértice). */
    internal fun ruido(a: Int, b: Int, c: Int = 0): Float { val s = sin(a * 12.9898 + b * 78.233 + c * 37.719) * 43758.5453; return (s - kotlin.math.floor(s)).toFloat() }

    // ---------------------------------------------------------------- El sol que entra por la Puerta

    /** El centro de la mancha que deja en el suelo del pasillo el sol que entra por la Puerta. */
    fun centroDeLaMancha(s: EscenaRefugio.SolDePuerta): V3 {
        val (dz, dx) = deriva(s, PUERTA_C.y)
        return V3((PUERTA_C.x + dx).coerceIn(IZQ + 0.3f, DER - 0.3f), 0f, (PUERTA_Z - dz).coerceIn(BOCA, PUERTA_Z))
    }

    /** Cuánto se corre hacia la sala ([first]) y de lado ([second]) el rayo que entra a la altura [y] hasta el suelo. */
    private fun deriva(s: EscenaRefugio.SolDePuerta, y: Float): Pair<Float, Float> {
        val largo = y / tan(Math.toRadians(s.elevacion.toDouble())).toFloat(); val az = Math.toRadians(s.azimut.toDouble())
        return (largo * cos(az).toFloat()) to (-largo * sin(az).toFloat())
    }

    /** La mancha de sol en el suelo del pasillo: la Puerta llevada por el rayo hasta el suelo (en metros del suelo). */
    fun manchaDelSol(s: EscenaRefugio.SolDePuerta): List<V3> = K.circulo(PUERTA_C, K.FRENTE, PUERTA_R * 0.96f, 28).map { p ->
        val (dz, dx) = deriva(s, p.y)
        V3((p.x + dx).coerceIn(IZQ, DER), 0f, (PUERTA_Z - dz).coerceAtLeast(BOCA - 0.3f))
    }

    // ---------------------------------------------------------------- El pasillo, de lejos a cerca

    /** Lo que se ve del pasillo: el hueco del portón (todo el pasillo se pinta recortado a él). */
    fun hueco(): Path = K.forma(SalaRefugio.arcoDelPorton(SalaRefugio.FONDO + SalaRefugio.GRUESO * 0.5f))

    fun DrawScope.pasillo(v: VistaRefugio) {
        clipPath(hueco()) {
            drawRect(v.c(V3(CENTRO_X, 2f, 20f), K.NADA, C.ROCA, true), Offset.Zero, Size(380f, 285f))
            with(PuertaRefugio) { fondo(v) }
            val tramos = (listOf(BOCA) + COSTILLAS + listOf(PUERTA_Z)).distinct().sorted()
            for (i in tramos.size - 2 downTo 0) {
                val z0 = tramos[i]; val z1 = tramos[i + 1]
                tramo(v, z0, z1)
                if (z0 in COSTILLAS) costilla(v, z0)
            }
            with(LuzRefugio) { enElPasillo(v) }
            with(PiezasTubos) { enElPasillo(v) }
            lineaAmarilla(v)
            compuerta(v)
            with(SalaRefugio) { cosasDelPasillo(v) }
        }
    }

    /** Un tramo del túnel entre [z0] y [z1]: el suelo, el zócalo, la roca picada, lo que hay en las paredes y el tubo de luz. */
    private fun DrawScope.tramo(v: VistaRefugio, z0: Float, z1: Float) {
        suelo(v, z0, z1)
        for (lado in listOf(-1, 1)) zocalo(v, if (lado < 0) IZQ else DER, z0, z1, if (lado < 0) K.DER else K.IZQ)
        roca(v, z0, z1)
        for ((i, p) in PUERTAS.withIndex()) if (p.second >= z0 && p.third <= z1) puertaDeCuarto(v, i)
        if (VENTANAL_Z1 > z0 && VENTANAL_Z0 < z1) ventanal(v, maxOf(z0, VENTANAL_Z0), minOf(z1, VENTANAL_Z1))
        cables(v, z0, z1)
        TUBOS.firstOrNull { it in z0..z1 }?.let { tuboDeLuz(v, it) }
    }

    /** El suelo: losas de hormigón, más oscuras y mojadas junto a las paredes, donde escurre la humedad. */
    private fun DrawScope.suelo(v: VistaRefugio, z0: Float, z1: Float) {
        for (j in 0..1) {
            val za = z0 + (z1 - z0) * j / 2f; val zb = z0 + (z1 - z0) * (j + 1) / 2f
            for ((k, x) in listOf(IZQ to IZQ + 0.45f, IZQ + 0.45f to DER - 0.45f, DER - 0.45f to DER).withIndex()) {
                val tono = lerp(lerp(C.HORMIGON, C.ROCA, 0.45f), C.TINTA, if (k != 1) 0.5f else 0.28f + 0.12f * ruido(j, (z0 * 3).toInt(), k))
                Trazo3.run { caraDegradada(v, listOf(V3(x.first, 0f, za), V3(x.second, 0f, za), V3(x.second, 0f, zb), V3(x.first, 0f, zb)), K.ARRIBA, tono, V3((x.first + x.second) / 2f, 0f, za), V3((x.first + x.second) / 2f, 0f, zb), true, 2) }
            }
            Trazo3.run { barra(V3(IZQ, 0.002f, zb), V3(DER, 0.002f, zb), 0.03f, C.TINTA.copy(alpha = 0.4f), StrokeCap.Butt) }
        }
    }

    /** El zócalo de hormigón de la pared [x], con su franja de aviso gastada y, a veces, una rejilla con su mancha de polvo. */
    private fun DrawScope.zocalo(v: VistaRefugio, x: Float, z0: Float, z1: Float, n: V3) {
        if (!K.seVe(V3(x, 0.5f, (z0 + z1) / 2f), n)) return
        Trazo3.run { caraDegradada(v, listOf(V3(x, 0f, z0), V3(x, 0f, z1), V3(x, ZOCALO, z1), V3(x, ZOCALO, z0)), n, lerp(lerp(C.HORMIGON, C.CHAPA, 0.3f), C.TINTA, 0.2f), V3(x, ZOCALO, z0), V3(x, 0f, z0), true, 2) }
        // La franja amarilla y negra, a trozos: la pintura saltada deja ver el hormigón.
        val y0 = ZOCALO - 0.14f
        var z = z0; var k = 0
        while (z < z1 - 0.05f) {
            val zb = (z + 0.17f).coerceAtMost(z1); val falta = ruido(k, (z0 * 7).toInt(), if (x < 0f) 1 else 2) < 0.22f
            if (!falta) Trazo3.run { cara(v, listOf(V3(x, y0, z), V3(x, y0, zb), V3(x, ZOCALO, (zb + 0.07f).coerceAtMost(z1)), V3(x, ZOCALO, (z + 0.07f).coerceAtMost(z1))), n, if (k % 2 == 0) lerp(C.AMARILLO, C.HORMIGON, 0.2f) else C.TINTA, true, 0.9f) }
            z = zb; k++
        }
        if (ruido((z0 * 5).toInt(), if (x < 0f) 3 else 4) < 0.55f) rejilla(v, x, 0.2f, z0 + (z1 - z0) * 0.35f, z0 + (z1 - z0) * 0.35f + 0.55f, n)
    }

    /** Una rejilla del aire en el zócalo, de [z0] a [z1], con la mancha de polvo que deja encima. */
    private fun DrawScope.rejilla(v: VistaRefugio, x: Float, y: Float, z0: Float, z1: Float, n: V3) {
        Trazo3.run {
            cara(v, listOf(V3(x, y + 0.3f, z0 - 0.05f), V3(x, y + 0.3f, z1 + 0.05f), V3(x, y + 0.75f, z1 + 0.15f), V3(x, y + 0.75f, z0 - 0.15f)), n, C.TINTA, true, 0.35f)
            cara(v, listOf(V3(x, y, z0), V3(x, y, z1), V3(x, y + 0.3f, z1), V3(x, y + 0.3f, z0)), n, C.TINTA, true)
            for (k in 1..4) { val yy = y + 0.3f * k / 5f; barra(V3(x, yy, z0 + 0.03f), V3(x, yy, z1 - 0.03f), 0.022f, v.c(V3(x, yy, (z0 + z1) / 2f), n, lerp(C.ACERO, C.OXIDO, 0.3f), true), StrokeCap.Butt) }
        }
    }

    /**
     * La roca picada entre dos costillas: primero el fondo de roca oscura, tramo por tramo del contorno; encima, las piedras
     * que dejó el pico, polígonos torcidos con su normal inclinada al azar, así que cada una recibe su luz (las que miran a
     * los tubos, claras; las que miran al suelo, hondas), con un filo claro de agua en el canto de arriba de las mojadas; y
     * encima, las chorreras de cal y de óxido y los hilos de agua que bajan.
     */
    private fun DrawScope.roca(v: VistaRefugio, z0: Float, z1: Float) {
        val s = (z0 * 10).toInt()
        val lejos = z0 > 24f
        val celdas = if (lejos) 4 else 7
        for (j in 0 until ROCA.size - 1) {
            val (x0, y0) = ROCA[j]; val (x1, y1) = ROCA[j + 1]; val base = normalDe(x0, y0, x1, y1)
            val a0 = V3(x0, y0, z0); val b0 = V3(x1, y1, z0)
            if (!K.seVe(V3((x0 + x1) / 2f, (y0 + y1) / 2f, (z0 + z1) / 2f), base)) continue
            Trazo3.run { caraDegradada(v, listOf(a0, b0, V3(x1, y1, z1), V3(x0, y0, z1)), base, lerp(C.ROCA, C.TINTA, 0.62f), V3(x0, y0, z0), V3(x0, y0, z1), true, 2) }
            // Las piedras: una fila de celdas corridas media celda en las filas impares, cada una torcida.
            val largo = (z1 - z0) / celdas; val corre = if (j % 2 == 0) 0f else largo * 0.5f
            val u = K.unidad(V3(x1 - x0, y1 - y0, 0f))
            val ancho = K.largo(V3(x1 - x0, y1 - y0, 0f))
            for (k in 0..celdas) {
                val za = z0 + corre + largo * (k - 0.5f); val zb = za + largo
                if (zb < z0 + 0.05f || za > z1 - 0.05f) continue
                val r = ruido(j, k, s)
                val cen = V3((x0 + x1) / 2f, (y0 + y1) / 2f, ((za + zb) / 2f).coerceIn(z0 + 0.1f, z1 - 0.1f)) + base * (0.03f + 0.05f * r)
                val inclina = K.unidad(base + u * ((ruido(j, k, s + 1) - 0.5f) * 2.4f) + K.FONDO * ((ruido(j, k, s + 2) - 0.5f) * 2.2f))
                val lados = 5 + (r * 3f).toInt()
                val pts = (0 until lados).map { i ->
                    val ang = 2f * PI.toFloat() * i / lados + r
                    val e = 0.62f + 0.38f * ruido(j * 7 + i, k, s + 3)
                    cen + u * (cos(ang) * ancho * 0.5f * e) + K.FONDO * (sin(ang) * largo * 0.55f * e)
                }.map { q -> V3(q.x, q.y, q.z.coerceIn(z0 + 0.03f, z1 - 0.03f)) }
                val mojada = ruido(j, k, s + 4) < 0.35f
                val tono = lerp(lerp(C.ROCA, C.TINTA, 0.15f + 0.55f * ruido(j, k, s + 5)), C.CAL, if (r > 0.88f) 0.25f else 0f)
                Trazo3.run { cara(v, pts, inclina, if (mojada) lerp(tono, C.TINTA, 0.25f) else tono, true) }
                // El filo de arriba de la piedra, mojado: brilla con la luz de los tubos.
                if (mojada && !lejos) {
                    val arriba = pts.sortedByDescending { K.punto(it - cen, u) + 0.3f * it.y }.take(2)
                    Trazo3.run { barra(arriba[0], arriba[1], 0.02f, lerp(v.colorTubo, Color.White, 0.4f).copy(alpha = (0.35f * v.fuerzaTubo).coerceIn(0f, 0.4f))) }
                }
            }
        }
        chorreras(v, z0, z1)
    }

    /** Las chorreras de cal (claras) y de óxido (al pie de las costillas) y los hilos de agua que brillan, en las dos paredes. */
    private fun DrawScope.chorreras(v: VistaRefugio, z0: Float, z1: Float) {
        val s = (z0 * 10).toInt()
        for (lado in listOf(-1, 1)) {
            val x = if (lado < 0) IZQ + 0.02f else DER - 0.02f; val n = if (lado < 0) K.DER else K.IZQ
            if (!K.seVe(V3(x, 1.5f, (z0 + z1) / 2f), n)) continue
            for (k in 0..3) {
                val z = z0 + 0.15f + (z1 - z0 - 0.3f) * ruido(k, s, lado + 5); val largo = 0.6f + 1.2f * ruido(k, s, lado + 6)
                val cal = k % 2 == 0
                Trazo3.run { barra(V3(x, PARED, z), V3(x, PARED - largo, z + 0.05f), if (cal) 0.07f else 0.05f, v.c(V3(x, PARED - largo / 2f, z), n, if (cal) C.CAL else C.OXIDO, true).copy(alpha = 0.4f)) }
            }
            // El óxido que baja de la costilla.
            Trazo3.run { barra(V3(x, PARED - 0.1f, z0 + 0.08f), V3(x, 1.2f, z0 + 0.12f), 0.08f, v.c(V3(x, 2f, z0), n, C.OXIDO, true).copy(alpha = 0.35f)) }
            // Los hilos de agua que bajan por la roca y brillan con la luz de los tubos.
            if (v.fuerzaTubo > 0.15f) for (k in 0..2) { val z = z0 + (z1 - z0) * ruido(k, s, lado + 9); val ya = PARED - 0.2f * k
                Trazo3.run { barra(V3(x, ya, z), V3(x, ZOCALO + 0.1f, z + 0.04f), 0.018f, lerp(v.colorTubo, Color.White, 0.5f).copy(alpha = 0.3f)) } }
        }
    }

    /** Los cables grapados a la pared de la izquierda, con su comba entre grapa y grapa, y los dos tubos de la derecha. */
    private fun DrawScope.cables(v: VistaRefugio, z0: Float, z1: Float) {
        val colores = listOf(C.TINTA, lerp(C.TINTA, C.ROJO, 0.35f), lerp(C.TINTA, C.AMARILLO, 0.25f))
        for ((k, col) in colores.withIndex()) {
            val y = 2.5f - 0.08f * k; val c = v.c(V3(IZQ, y, (z0 + z1) / 2f), K.DER, col, true)
            var z = z0
            while (z < z1 - 0.1f) { val zb = (z + 0.75f).coerceAtMost(z1); Trazo3.run { comba(V3(IZQ + 0.04f, y, z), V3(IZQ + 0.04f, y, zb), 0.018f, 0.03f, c, 5) }; z = zb }
        }
        var z = z0 + 0.2f
        while (z < z1) { Trazo3.run { barra(V3(IZQ + 0.05f, 2.55f, z), V3(IZQ + 0.05f, 2.3f, z), 0.025f, v.c(V3(IZQ, 2.4f, z), K.DER, C.ACERO, true)) }; z += 0.75f }
        Trazo3.run {
            barra(V3(DER - 0.12f, 2.15f, z0), V3(DER - 0.12f, 2.15f, z1), 0.16f, v.c(V3(DER, 2.15f, (z0 + z1) / 2f), K.IZQ, lerp(C.ACERO, C.OXIDO, 0.2f), true), StrokeCap.Butt)
            barra(V3(DER - 0.1f, 2.45f, z0), V3(DER - 0.1f, 2.45f, z1), 0.1f, v.c(V3(DER, 2.45f, (z0 + z1) / 2f), K.IZQ, C.COBRE, true), StrokeCap.Butt)
        }
    }

    /** Una puerta de cuarto en su pared: el marco de acero, la hoja oscura, la chapa en blanco y el tirador; de noche, a veces, su rendija de luz. */
    private fun DrawScope.puertaDeCuarto(v: VistaRefugio, i: Int) {
        val (lado, z0, z1) = PUERTAS[i]; val x = if (lado < 0) IZQ + 0.01f else DER - 0.01f; val n = if (lado < 0) K.DER else K.IZQ
        val alto = 2.05f
        Trazo3.run {
            cara(v, listOf(V3(x, 0f, z0 - 0.12f), V3(x, 0f, z1 + 0.12f), V3(x, alto + 0.12f, z1 + 0.12f), V3(x, alto + 0.12f, z0 - 0.12f)), n, lerp(C.ACERO, C.CHAPA, 0.4f), true)
            cara(v, listOf(V3(x, 0.02f, z0), V3(x, 0.02f, z1), V3(x, alto, z1), V3(x, alto, z0)), n, lerp(C.CHAPA, C.TINTA, 0.35f), true)
            cara(v, listOf(V3(x, 1.55f, z0 + 0.35f), V3(x, 1.55f, z1 - 0.35f), V3(x, 1.72f, z1 - 0.35f), V3(x, 1.72f, z0 + 0.35f)), n, C.LATON, true)
            barra(V3(x, 1.0f, z1 - 0.15f), V3(x, 1.0f, z1 - 0.35f), 0.04f, v.c(V3(x, 1f, z1), n, C.ACERO, true))
        }
        if (R.rendija(v.m) == i) {
            // La rendija de luz por el canto y por debajo: alguien despierto en su cuarto.
            val luz = lerp(C.LAMPARA, Color.White, 0.25f)
            Trazo3.run { barra(V3(x, 0.03f, z0 + 0.02f), V3(x, alto - 0.03f, z0 + 0.02f), 0.025f, luz); barra(V3(x, 0.02f, z0), V3(x, 0.02f, z1), 0.03f, luz) }
            val p = K.p(V3(x, 1f, z0)); val k = K.k(V3(x, 1f, z0))
            drawCircle(Brush.radialGradient(listOf(C.LAMPARA.copy(alpha = 0.4f), Color.Transparent), p, 1.2f * k), 1.2f * k, p, blendMode = BlendMode.Plus)
        }
    }

    /** El tubo de luz de la clave en [z], en su carcasa vieja: uno más amarillo; otro, el que falla (parpadea en la parte viva). */
    private fun DrawScope.tuboDeLuz(v: VistaRefugio, z: Float) {
        val i = TUBOS.indexOf(z)
        val a = V3(CENTRO_X, BOVEDA - 0.2f, z - 0.65f); val b = V3(CENTRO_X, BOVEDA - 0.2f, z + 0.65f)
        Trazo3.run {
            caja(v, CENTRO_X - 0.12f, CENTRO_X + 0.12f, BOVEDA - 0.2f, BOVEDA - 0.06f, z - 0.72f, z + 0.72f, lerp(C.HIERRO, C.OXIDO, 0.25f), enPasillo = true)
            barra(V3(CENTRO_X, BOVEDA - 0.06f, z), V3(CENTRO_X, BOVEDA + 0.1f, z), 0.02f, C.TINTA)
        }
        val amarillo = i == 2; val falla = i == 4
        val color = if (amarillo) lerp(v.colorTubo, C.LAMPARA, 0.45f) else v.colorTubo
        val fuerza = v.fuerzaTubo.coerceAtMost(1f) * (if (falla) 0.45f else 1f)
        Trazo3.run { barra(a, b, 0.07f, lerp(color, Color.White, 0.5f * fuerza)) }
        val m = K.p(K.medio(a, b)); val k = K.k(a)
        drawOval(Brush.radialGradient(listOf(color.copy(alpha = 0.35f * fuerza), Color.Transparent), m, 0.9f * k), m - Offset(0.9f * k, 0.35f * k), Size(1.8f * k, 0.7f * k), blendMode = BlendMode.Plus)
    }

    /** Si el tubo [i] es el que parpadea (lo usa la parte viva). */
    fun tuboQueFalla(): Float = TUBOS.getOrElse(4) { TUBOS.last() }

    /**
     * Una costilla de acero viejo en [z]: una viga doble T que sigue la sección, pintada de verde azulado y con la pintura
     * levantada (el óxido asoma a trozos), con sus remaches.
     */
    private fun DrawScope.costilla(v: VistaRefugio, z: Float) {
        val canto = 0.11f; val hondo = 0.16f; val s = (z * 10).toInt()
        val ventanal = z > VENTANAL_Z0 + 0.1f && z < VENTANAL_Z1 - 0.1f
        for (i in 0 until SECCION.size - 1) {
            if (ventanal && i == 0) continue
            val n = normalDentro(i)
            val a = q(SECCION[i], z); val b = q(SECCION[i + 1], z); val a2 = a + n * canto; val b2 = b + n * canto
            val oxidada = ruido(i, s, 11) < 0.35f
            Trazo3.run { cara(v, listOf(a, b, b2, a2), K.FRENTE, if (oxidada) lerp(C.OXIDO, C.TINTA, 0.2f) else lerp(C.CHAPA, C.TINTA, 0.35f), true) }
            Trazo3.run { cara(v, listOf(a2, b2, b2 + K.FONDO * hondo, a2 + K.FONDO * hondo), n, if (oxidada) lerp(C.OXIDO, C.CHAPA, 0.3f) else lerp(C.CHAPA, C.ACERO, 0.2f), true) }
            // La pintura levantada: una escama clara en el canto, a trozos.
            if (ruido(i, s, 12) < 0.3f) Trazo3.run { barra(a2 + (b2 - a2) * 0.2f, a2 + (b2 - a2) * 0.55f, 0.04f, v.c(a2, n, lerp(C.CHAPA, C.PAPEL, 0.3f), true).copy(alpha = 0.7f)) }
            // Los remaches, cada tramo.
            val m = K.medio(a, b) + n * 0.07f
            if (z < BOCA + 16f) drawCircle(v.c(m, K.FRENTE, lerp(C.ACERO, Color.White, 0.2f), true), 0.035f * K.k(m), K.p(m))
        }
        val pts = SECCION.map { K.p(q(it, z)) }
        val filo = Path().apply { moveTo(pts[0].x, pts[0].y); for (i in 1 until pts.size) lineTo(pts[i].x, pts[i].y) }
        drawPath(filo, lerp(v.colorTubo, Color.White, 0.2f).copy(alpha = 0.1f), style = androidx.compose.ui.graphics.drawscope.Stroke(0.4f))
    }

    // ---------------------------------------------------------------- La línea amarilla, el ventanal y la compuerta

    /** La línea amarilla del suelo, gastada a trozos, de la boca a la Puerta: la que lleva la diagonal hasta el fondo. */
    private fun DrawScope.lineaAmarilla(v: VistaRefugio) {
        val x = IZQ + 0.55f; val z0 = BOCA - 0.2f; val z1 = LINEA_Z; val n = 14
        for (i in 0 until n) {
            val za = z0 + (z1 - z0) * i / n; val zb = z0 + (z1 - z0) * (i + 1) / n
            val gasto = ruido(i, 3, 7)
            Trazo3.run { cara(v, listOf(V3(x, 0.004f, za), V3(x + 0.13f, 0.004f, za), V3(x + 0.13f, 0.004f, zb - 0.1f * gasto), V3(x, 0.004f, zb - 0.1f * gasto)), K.ARRIBA, lerp(C.AMARILLO, C.HORMIGON, 0.35f * gasto), true) }
        }
    }

    /**
     * La parte del ventanal de la sala de tanques entre [z0] y [z1] (el ventanal cruza varias costillas): tras el vidrio,
     * los tanques con sus algas, que leen el arco `cultivo` (claras y con más vidrio que verde, recién sembradas; verdes y
     * espesas; oscuras y llenas), bajo sus lámparas violetas. El día de la cosecha, vaciándose: «parecen ventanas».
     */
    private fun DrawScope.ventanal(v: VistaRefugio, z0: Float, z1: Float) {
        val x = IZQ; val n = K.DER; val y0 = VENTANAL_Y0; val y1 = VENTANAL_Y1
        val dentro = listOf(V3(x, y0, z0), V3(x, y0, z1), V3(x, y1, z1), V3(x, y1, z0))
        val forma = K.forma(dentro)
        clipPath(forma) { tanques(v, z0, z1) }
        Trazo3.run {
            for ((a, b) in listOf(V3(x, y0, z0) to V3(x, y0, z1), V3(x, y1, z0) to V3(x, y1, z1))) barra(a, b, 0.1f, v.c(K.medio(a, b), n, lerp(C.HIERRO, C.OXIDO, 0.2f), true), StrokeCap.Square)
            if (z0 == VENTANAL_Z0) barra(V3(x, y0, z0), V3(x, y1, z0), 0.1f, v.c(V3(x, 1.5f, z0), n, C.HIERRO, true), StrokeCap.Square)
            if (z1 == VENTANAL_Z1) barra(V3(x, y0, z1), V3(x, y1, z1), 0.1f, v.c(V3(x, 1.5f, z1), n, C.HIERRO, true), StrokeCap.Square)
            val zm = (z0 + z1) / 2f; barra(V3(x, y0, zm), V3(x, y1, zm), 0.05f, v.c(V3(x, 1.6f, zm), n, C.HIERRO, true))
        }
        // El vidrio: el violeta en el marco y un reflejo al sesgo.
        drawPath(forma, lerp(C.VIOLETA, Color.White, 0.5f).copy(alpha = 0.04f + 0.04f * v.tanques), blendMode = BlendMode.Plus)
    }

    /** Los tanques de cultivo: dónde está cada uno, detrás del vidrio (el centro de su base). */
    private val TANQUES: List<V3> = (0..9).map { V3(IZQ - 0.88f, 0f, VENTANAL_Z0 + 0.6f + 1.9f * it) }
    private const val TANQUE_R = 0.7f
    private const val TANQUE_ALTO = 2.95f

    /**
     * Los tanques tras el vidrio, de [z0] a [z1]: cilindros de vidrio con su agua de color y sus algas, que leen el arco
     * `cultivo` (claras y con más vidrio que verde; verdes y espesas; oscuras y llenas), con su aro de acero arriba y abajo
     * y su lámpara violeta encima. El día de la cosecha, a medio vaciar y claros: «parecen ventanas».
     */
    private fun DrawScope.tanques(v: VistaRefugio, z0: Float, z1: Float) {
        val cosecha = R.cosecha(v.m); val tramo = R.tramoCultivo(v.m)
        val agua = when { cosecha -> lerp(C.PISCINA, C.PAPEL, 0.5f); tramo == EscenaRefugio.TramoCultivo.SEMBRADO -> lerp(C.PISCINA, C.ALGA, 0.35f); tramo == EscenaRefugio.TramoCultivo.VERDE -> lerp(C.ALGA, C.AMARILLO, 0.15f); else -> lerp(C.ALGA, C.TINTA, 0.62f) }
        val luz = lerp(C.VIOLETA, Color.White, 0.25f)
        val dia = (v.m.ficha.especiales.firstOrNull { it.dia.id == "cosecha" }?.numero ?: 1)
        // Detrás, la sala de tanques: violeta en penumbra.
        drawRect(lerp(lerp(C.TINTA, C.VIOLETA, 0.25f + 0.35f * v.tanques), C.SOMBRA, 0.2f), Offset.Zero, Size(380f, 285f))
        for ((k, t) in TANQUES.withIndex().reversed()) {
            if (t.z + TANQUE_R < z0 - 0.3f || t.z - TANQUE_R > z1 + 0.3f) continue
            val nivel = if (cosecha) 0.4f + 1.6f * ((k + dia) % 3) / 3f else TANQUE_ALTO - 0.25f
            tanque(v, t, nivel, agua, luz, tramo, cosecha, k)
        }
    }

    /** Un tanque en [t], con el agua hasta [nivel]: el vidrio, el agua con su degradado, las algas, la raya del nivel, los aros. */
    private fun DrawScope.tanque(v: VistaRefugio, t: V3, nivel: Float, agua: Color, luz: Color, tramo: EscenaRefugio.TramoCultivo, cosecha: Boolean, k: Int) {
        fun cuerpo(y0: Float, y1: Float): Path = androidx.compose.ui.graphics.Path().also { camino ->
            val pts = K.casco((K.circulo(t + V3(0f, y0, 0f), K.ARRIBA, TANQUE_R, 16) + K.circulo(t + V3(0f, y1, 0f), K.ARRIBA, TANQUE_R, 16)).map { K.p(it) })
            if (pts.size >= 3) { camino.moveTo(pts[0].x, pts[0].y); for (q in pts.drop(1)) camino.lineTo(q.x, q.y); camino.close() }
        }
        val opaco = when { cosecha -> 0.55f; tramo == EscenaRefugio.TramoCultivo.SEMBRADO -> 0.62f; tramo == EscenaRefugio.TramoCultivo.VERDE -> 0.95f; else -> 1f }
        // El vidrio vacío, claro de la lámpara de arriba.
        drawPath(cuerpo(0.1f, TANQUE_ALTO), lerp(luz, C.SOMBRA, 0.45f).copy(alpha = 0.55f))
        // El agua, con su degradado: violeta arriba, su color en medio, honda abajo.
        val arriba = K.p(t + V3(0f, nivel, 0f)).y; val abajo = K.p(t + V3(0f, 0.1f, 0f)).y
        drawPath(cuerpo(0.1f, nivel), Brush.verticalGradient(listOf(lerp(agua, luz, 0.35f * v.tanques).copy(alpha = opaco), agua.copy(alpha = opaco), lerp(agua, C.TINTA, 0.45f).copy(alpha = opaco)), startY = arriba, endY = abajo))
        algas(t, nivel, tramo, cosecha, k)
        // El brillo del vidrio, un filo vertical del lado de la luz, y la raya del nivel.
        val c = K.medio(t, t + V3(0f, nivel, 0f))
        Trazo3.run {
            barra(t + V3(0.5f, 0.2f, -0.35f), t + V3(0.5f, TANQUE_ALTO - 0.2f, -0.35f), 0.05f, lerp(luz, Color.White, 0.5f).copy(alpha = 0.35f))
            comba(t + V3(TANQUE_R * 0.95f, nivel, -TANQUE_R * 0.3f), t + V3(-TANQUE_R * 0.2f, nivel, -TANQUE_R * 0.95f), -0.08f, 0.035f, lerp(luz, Color.White, 0.55f).copy(alpha = 0.8f), 6)
            for (y in listOf(0.1f, TANQUE_ALTO)) aro(t + V3(0f, y, 0f), K.ARRIBA, TANQUE_R + 0.02f, 0.06f, v.c(t + V3(0f, y, 0f), K.NADA, lerp(C.ACERO, C.HIERRO, 0.3f), true), 18)
        }
        if (K.hondo(c) < 0.5f) return
        // La lámpara violeta del tanque, encima: una barra que brilla.
        val l0 = t + V3(0.1f, TANQUE_ALTO + 0.12f, -0.4f); val l1 = t + V3(0.1f, TANQUE_ALTO + 0.12f, 0.4f)
        Trazo3.run { barra(l0, l1, 0.07f, lerp(luz, Color.White, 0.4f + 0.4f * v.tanques)) }
        drawCircle(Brush.radialGradient(listOf(luz.copy(alpha = 0.45f * (0.4f + 0.6f * v.tanques)), Color.Transparent), K.p(K.medio(l0, l1)), 0.8f * K.k(l0)), 0.8f * K.k(l0), K.p(K.medio(l0, l1)), blendMode = BlendMode.Plus)
    }

    /** Las algas de un tanque: brotes chicos en el fondo (recién sembrado), hebras que suben (verde) o una masa espesa (lleno). */
    private fun DrawScope.algas(t: V3, nivel: Float, tramo: EscenaRefugio.TramoCultivo, cosecha: Boolean, k: Int) {
        if (cosecha) return
        val (hebras, alto, color) = when (tramo) {
            EscenaRefugio.TramoCultivo.SEMBRADO -> Triple(7, 0.35f, lerp(C.ALGA, C.AMARILLO, 0.2f))
            EscenaRefugio.TramoCultivo.VERDE -> Triple(9, 0.6f, lerp(C.ALGA, C.TINTA, 0.35f))
            EscenaRefugio.TramoCultivo.LLENO -> Triple(12, 0.92f, lerp(C.ALGA, C.TINTA, 0.7f))
        }
        for (j in 0 until hebras) {
            val a = 2f * PI.toFloat() * (j + 0.5f * ruido(j, k, 22)) / hebras
            val pie = t + V3(cos(a) * TANQUE_R * 0.7f, 0.12f, sin(a) * TANQUE_R * 0.7f)
            val h = (nivel - 0.2f) * alto * (0.6f + 0.4f * ruido(j, k, 21))
            Trazo3.run { comba(pie, pie + V3(0.08f * (ruido(j, k, 23) - 0.5f), h, 0.05f), -0.06f, 0.07f, color.copy(alpha = 0.85f), 5) }
        }
    }

    /**
     * La compuerta del sector, en la pared de la izquierda: un hueco con marco de acero y, encima, su luz de aviso. Abierta,
     * se ve la hoja recogida arriba (su canto con la franja de aviso) y el pasillo del sector, más oscuro; bajada, la hoja
     * de acero cierra el hueco, con la ventanilla de dos puertas y el intercomunicador, y la luz roja encendida.
     */
    private fun DrawScope.compuerta(v: VistaRefugio) {
        val x = IZQ + 0.01f; val n = K.DER
        val z0 = COMPUERTA_Z0; val z1 = COMPUERTA_Z1; val alto = COMPUERTA_ALTO
        val hueco = listOf(V3(x, 0f, z0), V3(x, 0f, z1), V3(x, alto, z1), V3(x, alto, z0))
        Trazo3.run { cara(v, listOf(V3(x, 0f, z0 - 0.2f), V3(x, 0f, z1 + 0.2f), V3(x, alto + 0.25f, z1 + 0.2f), V3(x, alto + 0.25f, z0 - 0.2f)), n, lerp(C.ACERO, C.CHAPA, 0.4f), true) }
        when (v.compuerta) {
            Compuerta.ABIERTA -> {
                drawPath(K.forma(hueco), v.c(V3(x - 1f, 1f, (z0 + z1) / 2f), K.DER, lerp(C.TINTA, C.ROCA, 0.3f), true))
                Trazo3.run { cara(v, listOf(V3(x, alto - 0.18f, z0), V3(x, alto - 0.18f, z1), V3(x, alto, z1), V3(x, alto, z0)), K.ABAJO, C.AMARILLO, true) }
                var z = z0; while (z < z1) { Trazo3.run { cara(v, listOf(V3(x, alto - 0.18f, z), V3(x, alto - 0.18f, (z + 0.14f).coerceAtMost(z1)), V3(x, alto, (z + 0.22f).coerceAtMost(z1)), V3(x, alto, (z + 0.08f).coerceAtMost(z1))), K.ABAJO, C.TINTA, true) }; z += 0.3f }
            }
            Compuerta.BAJADA -> {
                Trazo3.run { caraDegradada(v, hueco, n, lerp(C.ACERO, C.CHAPA, 0.2f), V3(x, alto, (z0 + z1) / 2f), V3(x, 0f, (z0 + z1) / 2f), true) }
                Trazo3.run {
                    for (k in 1..3) { val yy = alto * k / 4f; barra(V3(x, yy, z0), V3(x, yy, z1), 0.06f, v.c(V3(x, yy, z0), n, lerp(C.ACERO, C.TINTA, 0.3f), true), StrokeCap.Butt) }
                    val zm = (z0 + z1) / 2f
                    cara(v, listOf(V3(x, 1.0f, zm - 0.3f), V3(x, 1.0f, zm + 0.3f), V3(x, 1.4f, zm + 0.3f), V3(x, 1.4f, zm - 0.3f)), n, C.HIERRO, true)
                    barra(V3(x, 1.0f, zm), V3(x, 1.4f, zm), 0.025f, v.c(V3(x, 1.2f, zm), n, C.ACERO, true))
                    cara(v, listOf(V3(x, 1.5f, z1 - 0.25f), V3(x, 1.5f, z1 - 0.08f), V3(x, 1.75f, z1 - 0.08f), V3(x, 1.75f, z1 - 0.25f)), n, lerp(C.CREMA, C.ACERO, 0.4f), true)
                }
            }
        }
        val luz = LUZ_COMPUERTA
        Trazo3.run { disco(v, luz, n, 0.13f, C.HIERRO, true, 14) }
        if (v.compuerta == Compuerta.BAJADA) {
            drawCircle(lerp(C.ALARMA, Color.White, 0.35f), 0.09f * K.k(luz), K.p(luz))
            drawCircle(Brush.radialGradient(listOf(C.ALARMA.copy(alpha = 0.5f), Color.Transparent), K.p(luz), 0.9f * K.k(luz)), 0.9f * K.k(luz), K.p(luz), blendMode = BlendMode.Plus)
        } else drawCircle(lerp(C.ROJO, C.TINTA, 0.45f), 0.08f * K.k(luz), K.p(luz))
    }
}
