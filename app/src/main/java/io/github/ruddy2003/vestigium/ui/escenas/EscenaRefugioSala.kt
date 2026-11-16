package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRefugio.Obra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * La sala de bombas de la residente: «una nave de techo bajo, llena de máquinas viejas pintadas de verde, tubos de todos
 * los tamaños, manómetros, válvulas con ruedas rojas y un olor a aceite y a agua que no se quita nunca». Es el cuadro de
 * Refugio (`composicion_refugio.md`), ordenado por planos, de atrás adelante, para que otro lo continúe:
 * 1. **El pasillo** ([PasilloRefugio]), por el hueco del portón.
 * 2. **La caja de la sala** ([techo], [paredDelFondo], [suelo], [porton]): los bloques pintados de crema con el zócalo
 *    verde azulado, el suelo de losas de hormigón con su rejilla y sus manchas, el portón de hormigón con su costilla de
 *    acero y la cinta de aviso.
 * 3. **Lo de la pared** ([enLaPared]): la pared de manómetros, el cuadro de palancas, la columna de nivel de la cisterna,
 *    la lucecita de guardia, los carteles de antes de la guerra; en el quicio de la derecha, el interruptor, el extintor,
 *    la percha de las linternas y el gancho del mono.
 * 4. **La luz en las superficies** ([LuzRefugio]: el charco de la lámpara con la sombra de su jaula) y **las sombras**.
 * 5. **Las cosas** ([cosas]): la bomba grande (el marco de la izquierda), la fila de bombas, el filtro grande, la bancada
 *    con lo suyo, los cubos, la manguera, la caja de herramientas, el objeto del día; las de cada estado y cada día.
 * 6. **Los tubos del techo** ([PiezasTubos]), la lámpara colgada y el aire ([LuzRefugio]); el grano.
 *
 * **[Diseño propio]**: cómo es la sala y dónde va cada cosa. El cuento nombra la pared de manómetros, las máquinas verdes,
 * las ruedas rojas, el filtro grande «del tamaño de un niño», el cuaderno del turno, los tubitos de muestra, el
 * microscopio, el tocadiscos pequeño y la lámpara de la sala.
 */
internal object SalaRefugio {
    private val C = ColorRefugio
    private val K = CamRefugio
    private val R = EscenaRefugio
    private val P = PasilloRefugio

    // ---------------------------------------------------------------- Las medidas (en metros)

    /** La pared del fondo de la sala, la del portón. */
    const val FONDO = 7.5f
    /** El grueso de esa pared en el portón. */
    const val GRUESO = 0.55f
    const val TECHO = 3.2f
    /** Las paredes de los lados (no se ven: quedan fuera del cuadro). */
    const val IZQ = -12f
    const val DER = 2.7f
    /** El portón: más estrecho que el pasillo, con su arco rebajado. */
    const val PORTON_X0 = -2.75f
    const val PORTON_X1 = 1.05f
    const val ARRANQUE = 2.4f
    const val CLAVE = 2.95f
    /** Hasta dónde llega el zócalo pintado. */
    const val ZOCALO = 1.15f

    /** El arco del portón: su radio y su centro (un arco de círculo rebajado). */
    private val ARCO_R: Float = run { val w = (PORTON_X1 - PORTON_X0) / 2f; val f = CLAVE - ARRANQUE; (w * w + f * f) / (2f * f) }
    private val ARCO_Y: Float = CLAVE - ARCO_R
    private val ARCO_A: Float = asin(((PORTON_X1 - PORTON_X0) / 2f) / ARCO_R)

    /** La altura del arco del portón en la [x] (dentro del hueco). */
    fun alturaDelArco(x: Float): Float { val dx = x - (PORTON_X0 + PORTON_X1) / 2f; return ARCO_Y + sqrt((ARCO_R * ARCO_R - dx * dx).coerceAtLeast(0f)) }

    /** El contorno del hueco del portón a la profundidad [z]. */
    fun arcoDelPorton(z: Float): List<V3> = buildList {
        add(V3(PORTON_X0, -0.02f, z)); add(V3(PORTON_X0, ARRANQUE, z))
        val cx = (PORTON_X0 + PORTON_X1) / 2f
        for (i in 1 until 16) { val a = -ARCO_A + 2f * ARCO_A * i / 16f; add(V3(cx + ARCO_R * sin(a), ARCO_Y + ARCO_R * cos(a), z)) }
        add(V3(PORTON_X1, ARRANQUE, z)); add(V3(PORTON_X1, -0.02f, z))
    }

    // ---- Dónde está cada cosa.
    /** La bomba grande, el marco de la izquierda. */
    val BOMBA = V3(-3.25f, 0f, 2.75f)
    /** La fila de bombas viejas hacia el portón: la segunda y la tercera (la «bomba tres» del cuento). */
    val BOMBA_2 = V3(-3.7f, 0f, 4.7f)
    val BOMBA_3 = V3(-4.25f, 0f, 6.35f)
    /** El filtro grande de la depuradora. */
    val FILTRO = V3(-1.6f, 0f, 4.5f)
    const val FILTRO_R = 0.34f
    const val FILTRO_ALTO = 1.36f
    /** La bancada: de [BANCADA_X0] a [BANCADA_X1], de [BANCADA_Z0] a [BANCADA_Z1], con la tapa a [BANCADA_Y]. */
    const val BANCADA_X0 = -1.8f
    const val BANCADA_X1 = -0.15f
    const val BANCADA_Z0 = 2.55f
    const val BANCADA_Z1 = 3.25f
    const val BANCADA_Y = 0.9f
    /** El sitio del objeto chico del día: la esquina de delante de la bancada, a la derecha. */
    val SOPORTE = V3(-0.5f, BANCADA_Y, 2.78f)
    /**
     * El objeto grande del día pisa el suelo junto a la punta de la bancada y se recuesta en su canto. Va 30 cm más al fondo
     * que la esquina de delante para que su pie caiga dentro del cuadro (en el lienzo, hacia y = 271): así se ven enteros la
     * llave, el tocadiscos del comedor con sus patas, el mono y la manta.
     */
    val PIE_GRANDE = V3(0.05f, 0f, 3.35f)
    val APOYO_GRANDE = V3(BANCADA_X1, BANCADA_Y - 0.05f, 2.95f)
    /** La radio de la sala (el día de la señal), en la bancada. */
    val RADIO = V3(-1.45f, BANCADA_Y + 0.12f, 3.1f)
    /** La pared de manómetros, la columna de nivel y el cuadro de palancas, en la pared del fondo. */
    const val TABLERO_X0 = -5.85f
    const val TABLERO_X1 = -3.95f
    const val COLUMNA_X = -3.55f
    const val ARMARIO_X0 = -7.1f
    const val ARMARIO_X1 = -6.15f
    /** La lucecita de guardia, encima del tablero; la baliza de la alarma, junto al portón. */
    val GUARDIA = V3(-4.3f, 2.85f, FONDO - 0.12f)
    val BALIZA = V3(-3.15f, 2.98f, FONDO - 0.15f)
    /** El cuadrito de mando de la bomba grande, con su piloto ámbar (de noche, una de las luces cálidas de delante). */
    val PILOTO_BOMBA = V3(-2.35f, 1.32f, 2.2f)
    /** El quicio de la derecha: el gancho del mono y la percha de las linternas. */
    val GANCHO = V3(1.42f, 1.75f, FONDO - 0.06f)

    /** Dónde cuelga la bombilla de la lámpara de trabajo, según la obra. */
    fun bombilla(o: Obra): V3 = when (o) {
        Obra.MANOMETROS -> V3(-4.6f, 2.42f, 6.95f)
        Obra.BOMBA -> V3(-2.55f, 2.1f, 3.1f)
        Obra.BANCADA -> V3(-0.65f, 2.0f, 2.9f)
        Obra.FILTRO -> V3(-1.4f, 2.1f, 4.0f)
    }

    /**
     * Hacia dónde alumbra la lámpara de jaula, que tiene la pantallita de un lado: casi siempre hacia abajo, así que desde
     * los ojos (más abajo que ella) se ve la jaula encendida y su cono. A primera hora cuelga pegada a la pared de
     * manómetros, que queda dentro de su cono.
     */
    fun haciaDonde(o: Obra): V3 = when (o) {
        Obra.MANOMETROS -> K.unidad(V3(0.12f, -1f, 0.06f))
        Obra.BOMBA -> K.unidad(V3(-0.15f, -1f, -0.05f))
        Obra.BANCADA -> K.ABAJO
        Obra.FILTRO -> K.unidad(V3(0.15f, -1f, -0.2f))
    }

    /** Por dónde camina el gato: delante de todo, de la bomba grande a la manguera. */
    const val PISO_GATO = 278f

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(v: VistaRefugio) {
        drawRect(v.c(V3(-3f, 1.5f, 6f), K.NADA, C.PENUMBRA), Offset.Zero, Size(ANCHO, ALTO))
        with(PasilloRefugio) { pasillo(v) }
        techo(v)
        paredDelFondo(v)
        suelo(v)
        porton(v)
        with(PiezasSala) { enElSuelo(v) }
        with(PiezasPared) { enLaPared(v) }
        if (R.primerDia(v.m)) with(PiezasSala) { guirnaldaDelPorton(v) }
        with(LuzRefugio) { enLaSala(v) }
        val cosas = with(PiezasSala) { cosas(v) }.sortedByDescending { it.hondo }
        with(LuzRefugio) { sombras(v, cosas) }
        val lejos = cosas.filter { it.hondo > 4.2f }; val cerca = cosas.filter { it.hondo <= 4.2f }
        for (k in lejos) k.pintar(this)
        with(PiezasTubos) { tubos(v) }
        for (k in cerca) k.pintar(this)
        with(PiezasTubos) { delante(v) }
        with(PiezasSala) { lamparas(v) }
        with(LuzRefugio) { enElAire(v); penumbra(v) }
        grano(Azar(Semilla.de("refugio", "grano")), 0.5f + 0.35f * (1f - v.noche))
    }

    // ---------------------------------------------------------------- La caja de la sala

    /** El techo bajo de hormigón, con sus juntas de encofrado: casi siempre en penumbra (la lámpara alumbra hacia abajo). */
    private fun DrawScope.techo(v: VistaRefugio) {
        var x = IZQ
        while (x < DER) {
            val x1 = (x + 1.2f).coerceAtMost(DER); var z = 1.2f
            while (z < FONDO) {
                val z1 = (z + 1.25f).coerceAtMost(FONDO)
                Trazo3.run { cara(v, listOf(V3(x, TECHO, z), V3(x1, TECHO, z), V3(x1, TECHO, z1), V3(x, TECHO, z1)), K.ABAJO, lerp(C.HORMIGON, C.TINTA, 0.25f)) }
                z = z1
            }
            x = x1
        }
        // Las vigas que cruzan el techo.
        for (zv in listOf(5.6f)) Trazo3.run { caja(v, IZQ, DER, TECHO - 0.28f, TECHO, zv, zv + 0.3f, lerp(C.HORMIGON, C.TINTA, 0.2f)) }
    }

    /**
     * La pared del fondo, de bloques: el zócalo pintado de verde azulado hasta [ZOCALO] y el crema encima, con las juntas,
     * las chorreras de las goteras y la cal. Cada bloque recibe la luz que le toca: así la lámpara deja su forma en ella.
     */
    private fun DrawScope.paredDelFondo(v: VistaRefugio) {
        val hueco = K.forma(arcoDelPorton(FONDO))
        val z = FONDO; val n = K.FRENTE
        clipPath(hueco, clipOp = ClipOp.Difference) {
            // La argamasa, toda de una vez.
            Trazo3.run { caraDegradada(v, listOf(V3(IZQ, 0f, z), V3(DER, 0f, z), V3(DER, TECHO, z), V3(IZQ, TECHO, z)), n, lerp(C.CREMA, C.HORMIGON, 0.55f), V3(-4f, TECHO, z), V3(-4f, 0f, z)) }
            val az = Azar(Semilla.de("refugio", "bloques"))
            val alto = 0.27f; val ancho = 0.56f; val junta = 0.016f
            var fila = 0; var y = 0f
            while (y < TECHO - 0.01f) {
                val y1 = (y + alto).coerceAtMost(TECHO)
                var x = IZQ - (if (fila % 2 == 0) 0f else ancho / 2f)
                while (x < DER) {
                    val xa = x.coerceAtLeast(IZQ); val xb = (x + ancho).coerceAtMost(DER)
                    val dentro = xb > PORTON_X0 && xa < PORTON_X1 && y < alturaDelArco(((xa + xb) / 2f).coerceIn(PORTON_X0, PORTON_X1)) - 0.05f
                    if (!dentro) {
                        val base = if (y1 <= ZOCALO + 0.01f) lerp(C.CHAPA, C.VERDE, 0.2f * az.real()) else lerp(C.CREMA, if (az.real() < 0.5f) C.PAPEL else C.HORMIGON, 0.25f * az.real())
                        Trazo3.run { cara(v, listOf(V3(xa + junta, y + junta, z), V3(xb - junta, y + junta, z), V3(xb - junta, y1 - junta, z), V3(xa + junta, y1 - junta, z)), n, base) }
                    } else az.real()
                    x += ancho
                }
                y = y1; fila++
            }
            // La raya pintada encima del zócalo.
            Trazo3.run { barra(V3(IZQ, ZOCALO + 0.02f, z - 0.005f), V3(DER, ZOCALO + 0.02f, z - 0.005f), 0.035f, v.c(V3(-4f, ZOCALO, z), n, C.TINTA)) }
            // Las chorreras de las goteras, que bajan de los tubos, y la cal.
            val ch = Azar(Semilla.de("refugio", "chorreras"))
            repeat(9) {
                val x0 = ch.entre(-10f, 2.4f); if (x0 > PORTON_X0 - 0.2f && x0 < PORTON_X1 + 0.2f) return@repeat
                val largo = ch.entre(0.6f, 2.2f); val ancho2 = ch.entre(0.06f, 0.22f)
                val pts = listOf(V3(x0 - ancho2, TECHO, z - 0.01f), V3(x0 + ancho2, TECHO, z - 0.01f), V3(x0 + ancho2 * 0.4f, TECHO - largo, z - 0.01f), V3(x0 - ancho2 * 0.3f, TECHO - largo * 0.9f, z - 0.01f))
                Trazo3.run { cara(v, pts, n, if (it % 3 == 0) C.CAL else lerp(C.ROCA, C.TINTA, 0.3f), alfa = 0.32f) }
            }
        }
    }

    /**
     * El suelo de losas de hormigón: cada losa con su luz (degradada de lejos a cerca), las juntas, la rejilla del desagüe,
     * la canaleta junto a la pared, las manchas de aceite y de agua, y la cinta de aviso del umbral del portón.
     */
    private fun DrawScope.suelo(v: VistaRefugio) {
        val az = Azar(Semilla.de("refugio", "losas"))
        // Debajo, el color de las juntas: oscuro, para que entre losa y losa no asome la penumbra alumbrada.
        Trazo3.run { cara(v, listOf(V3(IZQ, 0f, 1.3f), V3(DER, 0f, 1.3f), V3(DER, 0f, FONDO), V3(IZQ, 0f, FONDO)), K.ARRIBA, lerp(C.TINTA, C.HORMIGON, 0.3f)) }
        val lado = 1.2f
        var x = IZQ
        while (x < DER) {
            val x1 = (x + lado).coerceAtMost(DER); var z = 1.3f
            while (z < FONDO) {
                val z1 = (z + lado).coerceAtMost(FONDO)
                val tono = lerp(C.HORMIGON, if (az.real() < 0.5f) C.ROCA else C.CREMA, 0.18f * az.real())
                Trazo3.run { caraDegradada(v, listOf(V3(x + 0.012f, 0f, z + 0.012f), V3(x1 - 0.012f, 0f, z + 0.012f), V3(x1 - 0.012f, 0f, z1 - 0.012f), V3(x + 0.012f, 0f, z1 - 0.012f)), K.ARRIBA, tono, V3((x + x1) / 2f, 0f, z1), V3((x + x1) / 2f, 0f, z), pasos = 2) }
                z = z1
            }
            x = x1
        }
        // La canaleta del desagüe, junto a la pared del fondo, y la rejilla.
        Trazo3.run {
            cara(v, listOf(V3(IZQ, 0.001f, FONDO - 0.42f), V3(PORTON_X0 - 0.1f, 0.001f, FONDO - 0.42f), V3(PORTON_X0 - 0.1f, 0.001f, FONDO - 0.2f), V3(IZQ, 0.001f, FONDO - 0.2f)), K.ARRIBA, lerp(C.TINTA, C.ROCA, 0.3f))
            val g0 = V3(-2.35f, 0.002f, 5.7f)
            cara(v, listOf(g0, g0 + V3(0.6f, 0f, 0f), g0 + V3(0.6f, 0f, 0.6f), g0 + V3(0f, 0f, 0.6f)), K.ARRIBA, C.TINTA)
            for (k in 1..5) { val xx = g0.x + 0.1f * k; barra(V3(xx, 0.004f, g0.z + 0.03f), V3(xx, 0.004f, g0.z + 0.57f), 0.03f, v.c(g0, K.ARRIBA, C.ACERO)) }
        }
        // Las manchas: aceite junto a las bombas, agua en el camino de la gotera, el cerco del cubo.
        val mz = Azar(Semilla.de("refugio", "manchas"))
        for ((cx, cz, r, col) in listOf(Cuatro(-2.6f, 3.9f, 0.6f, C.TINTA), Cuatro(-3.0f, 5.6f, 0.45f, C.TINTA), Cuatro(-2.0f, 3.5f, 0.5f, lerp(C.ROCA, C.CHAPA, 0.4f)), Cuatro(0.3f, 5.2f, 0.7f, lerp(C.ROCA, C.CHAPA, 0.3f)), Cuatro(-0.6f, 6.4f, 0.4f, C.TINTA))) {
            val pts = (0 until 9).map { i -> val a = 6.283f * i / 9; val rr = r * mz.entre(0.6f, 1.1f); V3(cx + cos(a) * rr, 0.003f, cz + sin(a) * rr * 0.8f) }
            Trazo3.run { cara(v, pts, K.ARRIBA, col, alfa = 0.22f) }
        }
        if (R.filtros(v.m)) {
            // El suelo mojado de los filtros: un charco grande con su brillo.
            val pts = (0 until 12).map { i -> val a = 6.283f * i / 12; val rr = 1.1f * mz.entre(0.7f, 1.1f); V3(FILTRO.x + 0.3f + cos(a) * rr * 1.3f, 0.003f, FILTRO.z - 0.6f + sin(a) * rr) }
            Trazo3.run { cara(v, pts, K.ARRIBA, lerp(C.TINTA, C.CHAPA, 0.4f), alfa = 0.45f) }
        }
        // La cinta de aviso del umbral del portón, en el suelo.
        val z0 = FONDO - 0.32f
        var xx = PORTON_X0; var i = 0
        while (xx < PORTON_X1) { val xb = (xx + 0.22f).coerceAtMost(PORTON_X1)
            Trazo3.run { cara(v, listOf(V3(xx, 0.004f, z0), V3(xb, 0.004f, z0), V3(xb + 0.08f, 0.004f, FONDO), V3(xx + 0.08f, 0.004f, FONDO)), K.ARRIBA, if (i % 2 == 0) C.AMARILLO else C.TINTA, alfa = 0.85f) }
            xx = xb; i++ }
    }

    private class Cuatro(val a: Float, val b: Float, val c: Float, val d: Color) { operator fun component1() = a; operator fun component2() = b; operator fun component3() = c; operator fun component4() = d }

    /**
     * El portón: el hueco del grueso del muro (los derrames de los lados y el intradós del arco, con su luz, que entra del
     * pasillo), la costilla de acero remachada que lo enmarca por la cara de la sala y la cinta de aviso de las jambas.
     */
    private fun DrawScope.porton(v: VistaRefugio) {
        val z0 = FONDO; val z1 = FONDO + GRUESO
        val delante = arcoDelPorton(z0); val detras = arcoDelPorton(z1)
        // Los derrames y el intradós, cara por cara.
        for (i in 0 until delante.size - 1) {
            val a = delante[i]; val b = delante[i + 1]; val c = detras[i + 1]; val d = detras[i]
            val dx = b.x - a.x; val dy = b.y - a.y
            val n = K.unidad(V3(dy, -dx, 0f))
            if (!K.seVe(K.centro(listOf(a, b, c, d)), n)) continue
            Trazo3.run { caraDegradada(v, listOf(a, b, c, d), n, lerp(C.HORMIGON, C.ROCA, 0.25f), K.medio(a, b), K.medio(c, d)) }
        }
        // La costilla de acero: una banda remachada alrededor del hueco, en la cara de la sala.
        val fuera = arcoDelPorton(z0 - 0.02f).map { q -> val cx = (PORTON_X0 + PORTON_X1) / 2f; val cy = ARCO_Y; val dx = q.x - cx; val dy = q.y - cy
            if (q.y <= ARRANQUE + 0.01f) V3(q.x + (if (q.x < cx) -0.2f else 0.2f), q.y, q.z) else { val l = sqrt(dx * dx + dy * dy); V3(q.x + dx / l * 0.2f, q.y + dy / l * 0.2f, q.z) } }
        val dentro = arcoDelPorton(z0 - 0.02f)
        for (i in 0 until dentro.size - 1) {
            val pts = listOf(dentro[i], dentro[i + 1], fuera[i + 1], fuera[i])
            Trazo3.run { caraDegradada(v, pts, K.FRENTE, lerp(C.ACERO, C.CHAPA, 0.35f), K.medio(dentro[i], dentro[i + 1]), K.medio(fuera[i], fuera[i + 1]), pasos = 1) }
            val m = K.medio(K.medio(dentro[i], fuera[i]), K.medio(dentro[i + 1], fuera[i + 1]))
            drawCircle(v.c(m, K.FRENTE, lerp(C.ACERO, Color.White, 0.25f)), 0.028f * K.k(m), K.p(m))
        }
        // El filo de luz del canto interior, que da al pasillo.
        val filo = dentro.map { K.p(it) }
        for (i in 0 until filo.size - 1) drawLine(lerp(v.colorTubo, Color.White, 0.3f).copy(alpha = 0.4f), filo[i], filo[i + 1], 0.7f)
        // La cinta de aviso en las jambas, de abajo hasta 1.3 m.
        for (x in listOf(PORTON_X0 - 0.2f, PORTON_X1)) {
            var y = 0f; var i = 0
            while (y < 1.3f) { val y1 = (y + 0.16f).coerceAtMost(1.3f)
                Trazo3.run { cara(v, listOf(V3(x, y, z0 - 0.025f), V3(x + 0.2f, y + 0.09f, z0 - 0.025f), V3(x + 0.2f, y1 + 0.09f, z0 - 0.025f), V3(x, y1, z0 - 0.025f)), K.FRENTE, if (i % 2 == 0) C.AMARILLO else C.TINTA) }
                y = y1; i++ }
        }
    }

    // ---------------------------------------------------------------- El gato

    /**
     * La luz del gato: la de la sala donde pisa (la lámpara, el portón, la guardia, la penumbra). Su sombra la tumba la luz
     * que más lo alumbra.
     */
    fun luzDelGato(v: VistaRefugio, en: Offset): LuzGato {
        // El punto del suelo de la sala que cae en [en] (el gato camina por delante, a unos 3 m de los ojos).
        val zc = (K.F * (K.OJO - 0f) / (en.y - K.CY).coerceAtLeast(40f)).coerceIn(2f, 8f)
        val xc = (en.x - K.CX) * zc / K.F
        val w = V3(xc * K.COS - zc * K.SIN, 0.3f, xc * K.SIN + zc * K.COS)
        // La luz que manda en su lado claro: de día, la que más le llega (casi siempre la lámpara de jaula); de noche, la
        // cálida que más le llega (la lámpara del turno de más, la guardia, los pilotos de las bombas). Su sombra cae al
        // otro lado.
        val deLaSala = v.focos.filter { it.enSala && it.fuerza > 0.05f }
        val candidatas = if (v.dia) deLaSala else deLaSala.filter { it.color.red > it.color.blue + 0.15f }.ifEmpty { deLaSala }
        val manda = candidatas.maxByOrNull { llegaAlGato(it, w) }
        val rayo = if (manda != null) {
            val dx = w.x - manda.p.x; val lat = if (K.p(manda.p).x <= en.x) -1f else 1f
            rayoFijo(lat * (0.35f + 0.5f * (abs(dx) / 3f).coerceIn(0f, 1f)), 50f, (0.25f + 0.35f * llegaAlGato(manda, w)).coerceIn(0f, 0.7f), C.SOMBRA)
        } else rayoFijo(0.5f, 60f, 0.2f, C.SOMBRA)
        val brillo = manda?.let { lerp(it.color, Color.White, 0.35f) } ?: lerp(Color(0xFFD8E4F0), C.PISCINA, 0.3f * v.noche)
        // Su color: de día, el suyo con toda la luz de la sala en ese sitio; de noche, la que le da por delante (la penumbra
        // azul y lo cálido que tenga cerca), sin sumarle de frente el violeta y el azul que le llegan por la espalda desde el
        // portón: eso queda en su lado oscuro, que el gato pinta violeta (si se sumaba todo, salía rosado lila).
        // De noche, además, la penumbra que deja ver al gato (como la de los demás cuadros de noche): un azul más claro que
        // el de las paredes, para que un gato claro siga claro y uno oscuro, oscuro.
        // Bajo la lámpara del turno de más, de noche, manda la lámpara, como de día.
        val bajoLaLampara = manda != null && manda.cono != null && llegaAlGato(manda, w) > 0.2f
        val conTodo = v.dia || bajoLaLampara
        val normal = if (conTodo) K.NADA else K.unidad(V3(-w.x, K.OJO - w.y, -w.z))
        val amb = v.ambienteSala
        val ve = lerp(amb, Color(0xFF93AAEE), 0.62f)
        return LuzGato(rayo, v.noche.coerceIn(0f, 1f), brillo) { c ->
            val b = v.c(w, normal, c)
            if (conTodo) b else Color((b.red + c.red * (ve.red - amb.red)).coerceIn(0f, 1f), (b.green + c.green * (ve.green - amb.green)).coerceIn(0f, 1f), (b.blue + c.blue * (ve.blue - amb.blue)).coerceIn(0f, 1f), c.alpha)
        }
    }

    /** Cuánto le llega al gato en [w] la luz [f] (por la distancia y, si tiene pantalla, por su cono), de 0 a 1. */
    private fun llegaAlGato(f: FocoRefugio, w: V3): Float {
        val d = K.largo(f.p - w); var k = f.fuerza / (1f + d * d / (f.alcance * f.alcance))
        f.cono?.let { e -> k *= ((K.punto(K.unidad(w - f.p), e) - f.coseFuera) / (f.coseDentro - f.coseFuera)).coerceIn(0f, 1f) }
        return k.coerceIn(0f, 1f)
    }

    /**
     * Lo plantado en el suelo del gato: la pata de la bancada y el cubo de la gotera; el día de la bomba tres, sus piezas en
     * el suelo; y el objeto grande del día, de pie junto a la bancada.
     */
    fun estorbos(m: Momento): List<Float> = buildList {
        add(PiezasSala.PATA_BANCADA_X); add(PiezasSala.CUBO_X); add(PiezasSala.TABURETE_X)
        if (R.bombaTres(m)) add(K.p(PiezasSala.PIEZAS).x)
        if (ObjetosRefugio.esGrande(m)) add(K.p(PIE_GRANDE).x)
    }

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 24f, PISO_GATO, hasta = 362f))
        add(Ancla(TipoAncla.MIRADOR, PiezasSala.MIRADOR.x, PiezasSala.MIRADOR.y))           // encima de la bomba grande
        add(Ancla(TipoAncla.OBJETO, K.p(SOPORTE).x, PISO_GATO))                               // al pie de la bancada, bajo el objeto
        add(Ancla(TipoAncla.ABRIGO, PiezasSala.ABRIGO_X, PISO_GATO))                          // bajo la bancada
        if (R.fiesta(m) || R.arbolDeCable(m)) add(Ancla(TipoAncla.ADORNO, PiezasSala.ADORNO_X, PISO_GATO - 4f))
    }

    // ---------------------------------------------------------------- Lo del pasillo (lo pinta [PasilloRefugio])

    /** Lo que hay en el suelo del pasillo según el día, dentro del hueco del portón. */
    fun DrawScope.cosasDelPasillo(v: VistaRefugio) {
        with(PiezasSala) { delPasillo(v) }
    }

    /** Un azar fijo de la sala. */
    internal fun azar(que: String) = Azar(Semilla.de("refugio", que))

    /** Un número entre [a] y [b] con el azar [az]. */
    internal fun entre(az: Azar, a: Float, b: Float) = az.entre(a, b)
    internal fun real(az: Azar) = az.real()
}
