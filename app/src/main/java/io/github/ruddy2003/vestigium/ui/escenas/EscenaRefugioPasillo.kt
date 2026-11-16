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
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRefugio.Compuerta
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRefugio.Puerta
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nube
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 * El pasillo grande, visto por el portón de la sala de bombas: «de punta a punta… Al fondo, la Puerta, enorme, redonda,
 * cerrada». Un túnel en la roca con su bóveda, sostenido por costillas de acero remachadas cada tres metros; la roca
 * brilla de humedad entre ellas; en la clave, la fila de tubos de luz; abajo, el zócalo de hormigón con su franja de
 * aviso y las rejillas del aire; en el suelo, la línea amarilla que va hasta la Puerta y la que «nadie pisa», delante de
 * ella. En su pared de la izquierda, la compuerta del sector y, más allá, el ventanal de la sala de tanques.
 *
 * **[Diseño propio]**: el cuento no dice cómo es el pasillo por dentro (solo que es largo, tiene tubos en el techo, roca
 * húmeda y la Puerta al fondo); el túnel con costillas, la compuerta y el ventanal en su pared son de esta escena.
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
    /** Donde empieza el pasillo (detrás del portón) y donde está la Puerta. */
    val BOCA: Float get() = SalaRefugio.FONDO + SalaRefugio.GRUESO
    const val PUERTA_Z = 37.6f
    val LARGO: Float get() = PUERTA_Z - BOCA
    /** La Puerta: un círculo de acero de cuatro metros. */
    val PUERTA_C = V3(CENTRO_X, 2.15f, PUERTA_Z)
    const val PUERTA_R = 2.0f
    /** La línea amarilla que nadie pisa, delante de la Puerta. */
    const val LINEA_Z = PUERTA_Z - 1.7f

    /** Las costillas de acero, cada tres metros. */
    val COSTILLAS: List<Float> = (0..9).map { 9.6f + 3f * it }.filter { it < PUERTA_Z - 0.4f }
    /** Los tubos de luz de la clave, uno entre cada dos costillas. */
    val TUBOS: List<Float> = COSTILLAS.zipWithNext { a, b -> (a + b) / 2f }.filter { it < PUERTA_Z - 4f }

    /** La compuerta del sector, en la pared de la izquierda, entre dos costillas. */
    const val COMPUERTA_Z0 = 10.05f
    const val COMPUERTA_Z1 = 12.15f
    const val COMPUERTA_ALTO = 2.35f
    val LUZ_COMPUERTA = V3(IZQ + 0.12f, 2.6f, (COMPUERTA_Z0 + COMPUERTA_Z1) / 2f)

    /** El ventanal de la sala de tanques, más allá, en la misma pared. */
    const val VENTANAL_Z0 = 13.3f
    const val VENTANAL_Z1 = 23.3f
    const val VENTANAL_Y0 = 0.75f
    const val VENTANAL_Y1 = 2.5f

    /** Dónde está el árbol de cable de la Nochebuena (pisa el suelo del pasillo, junto a la pared de la derecha). */
    val ARBOL = V3(DER - 0.75f, 0f, BOCA + 1.6f)

    /** La sección del túnel: las paredes rectas y la bóveda, de la pared de la izquierda a la de la derecha (x, y). */
    val SECCION: List<Pair<Float, Float>> = buildList {
        add(IZQ to 0f); add(IZQ to PARED)
        val n = 10
        for (i in 1 until n) { val a = PI.toFloat() * i / n; add((CENTRO_X - (DER - IZQ) / 2f * cos(a)) to (PARED + (BOVEDA - PARED) * sin(a))) }
        add(DER to PARED); add(DER to 0f)
    }

    private fun q(par: Pair<Float, Float>, z: Float) = V3(par.first, par.second, z)

    /** La normal de la cara de la sección entre sus puntos [i] e [i]+1, hacia dentro del túnel. */
    private fun normalDentro(i: Int): V3 {
        val (x0, y0) = SECCION[i]; val (x1, y1) = SECCION[i + 1]
        val dx = x1 - x0; val dy = y1 - y0
        // La sección se recorre de izquierda a derecha por arriba: la normal hacia dentro es (dy, −dx) normalizada.
        return K.unidad(V3(dy, -dx, 0f))
    }

    // ---------------------------------------------------------------- El sol que entra por la Puerta

    /**
     * El centro de la mancha que deja en el suelo del pasillo el sol que entra por la Puerta: el punto del suelo al que
     * llega el rayo que pasa por el centro de la Puerta.
     */
    fun centroDeLaMancha(s: EscenaRefugio.SolDePuerta): V3 {
        val (dz, dx) = deriva(s, PUERTA_C.y)
        return V3((PUERTA_C.x + dx).coerceIn(IZQ + 0.3f, DER - 0.3f), 0f, (PUERTA_Z - dz).coerceIn(BOCA, PUERTA_Z))
    }

    /** Cuánto se corre hacia la sala ([first]) y de lado ([second]) el rayo que entra a la altura [y] hasta el suelo. */
    private fun deriva(s: EscenaRefugio.SolDePuerta, y: Float): Pair<Float, Float> {
        val largo = y / tan(Math.toRadians(s.elevacion.toDouble())).toFloat()
        val az = Math.toRadians(s.azimut.toDouble())
        return (largo * cos(az).toFloat()) to (-largo * sin(az).toFloat())
    }

    /** La mancha de sol en el suelo del pasillo: la Puerta llevada por el rayo hasta el suelo (en metros del suelo). */
    fun manchaDelSol(s: EscenaRefugio.SolDePuerta): List<V3> = K.circulo(PUERTA_C, K.FRENTE, PUERTA_R * 0.96f, 28).map { p ->
        val (dz, dx) = deriva(s, p.y)
        V3((p.x + dx).coerceIn(IZQ, DER), 0f, (PUERTA_Z - dz).coerceAtLeast(BOCA - 0.3f))
    }

    // ---------------------------------------------------------------- El hueco del portón

    /** Lo que se ve del pasillo: el hueco del portón (todo el pasillo se pinta recortado a él). */
    fun hueco(): Path = K.forma(SalaRefugio.arcoDelPorton(SalaRefugio.FONDO + SalaRefugio.GRUESO * 0.5f))

    // ---------------------------------------------------------------- El pasillo, de lejos a cerca

    fun DrawScope.pasillo(v: VistaRefugio) {
        clipPath(hueco()) {
            drawRect(v.c(V3(CENTRO_X, 2f, 20f), K.NADA, C.ROCA, true), Offset.Zero, Size(380f, 285f))
            fondo(v)
            val tramos = (listOf(BOCA) + COSTILLAS + listOf(PUERTA_Z)).distinct().sorted()
            for (i in tramos.size - 2 downTo 0) {
                val z0 = tramos[i]; val z1 = tramos[i + 1]
                tramo(v, z0, z1)
                if (z0 in COSTILLAS) costilla(v, z0)
            }
            with(LuzRefugio) { enElPasillo(v) }
            with(PiezasTubos) { enElPasillo(v) }
            lineaAmarilla(v)
            ventanal(v)
            compuerta(v)
            loDelPasillo(v)
        }
    }

    /** Un tramo del túnel entre [z0] y [z1]: el suelo, las paredes con su zócalo y la bóveda, cada cara con su luz. */
    private fun DrawScope.tramo(v: VistaRefugio, z0: Float, z1: Float) {
        val az = Azar(Semilla.de("refugio", "roca", (z0 * 10).toInt()))
        // El suelo: losas de hormigón, más oscuras junto a las paredes (donde escurre la humedad).
        for (j in 0..1) {
            val za = z0 + (z1 - z0) * j / 2f; val zb = z0 + (z1 - z0) * (j + 1) / 2f
            for ((xa, xb) in listOf(IZQ to IZQ + 0.5f, IZQ + 0.5f to DER - 0.5f, DER - 0.5f to DER)) {
                val borde = xa == IZQ || xb == DER
                val tono = lerp(C.HORMIGON, C.ROCA, if (borde) 0.45f else 0.12f * az.real())
                Trazo3.run { caraDegradada(v, listOf(V3(xa, 0f, za), V3(xb, 0f, za), V3(xb, 0f, zb), V3(xa, 0f, zb)), K.ARRIBA, tono, V3((xa + xb) / 2f, 0f, za), V3((xa + xb) / 2f, 0f, zb), true, 2) }
            }
            barraEnSuelo(v, V3(IZQ, 0.002f, zb), V3(DER, 0.002f, zb), 0.025f, C.TINTA, 0.35f)
        }
        // Las paredes y la bóveda, cara por cara de la sección.
        for (i in 0 until SECCION.size - 1) {
            val n = normalDentro(i)
            val a = q(SECCION[i], z0); val b = q(SECCION[i + 1], z0); val c = q(SECCION[i + 1], z1); val d = q(SECCION[i], z1)
            if (!K.seVe(K.centro(listOf(a, b, c, d)), n)) continue
            val esBoveda = SECCION[i].second >= PARED - 0.01f && SECCION[i + 1].second >= PARED - 0.01f
            if (!esBoveda) {
                // Una pared recta: el zócalo de hormigón con su franja de aviso y, encima, la roca.
                val x = SECCION[i].first; val zoc = 0.9f
                Trazo3.run { caraDegradada(v, listOf(V3(x, 0f, z0), V3(x, 0f, z1), V3(x, zoc, z1), V3(x, zoc, z0)), n, lerp(C.HORMIGON, C.CHAPA, 0.25f), V3(x, 0.45f, z0), V3(x, 0.45f, z1), true, 2) }
                franjaDeAviso(v, x, zoc - 0.12f, zoc, z0, z1, n)
                Trazo3.run { caraDegradada(v, listOf(V3(x, zoc, z0), V3(x, zoc, z1), V3(x, PARED, z1), V3(x, PARED, z0)), n, C.ROCA, V3(x, 1.8f, z0), V3(x, 1.8f, z1), true, 3) }
                rocaHumeda(v, x, zoc, PARED, z0, z1, n, az)
                // La rejilla del aire, a ras del zócalo, en algunos tramos.
                if (az.real() < 0.5f) { val zr = z0 + (z1 - z0) * az.entre(0.25f, 0.6f); rejilla(v, x, 0.22f, zr, zr + 0.55f, n) }
            } else {
                Trazo3.run { caraDegradada(v, listOf(a, b, c, d), n, lerp(C.ROCA, C.TINTA, 0.12f), K.medio(a, b), K.medio(c, d), true, 2) }
                // Manchas de la roca en la bóveda.
                repeat(2) { val t = az.real(); val zz = z0 + (z1 - z0) * az.real(); val e = a + (b - a) * t
                    drawCircle(lerp(C.ROCA, C.TINTA, 0.4f).copy(alpha = 0.25f), 0.25f * K.k(e) * az.entre(0.6f, 1.4f), K.p(V3(e.x, e.y, zz))) }
            }
        }
        // Los cables y tubos que corren por las paredes: la bandeja de cables a la izquierda, dos tubos a la derecha.
        Trazo3.run {
            barra(V3(IZQ + 0.06f, 2.45f, z0), V3(IZQ + 0.06f, 2.45f, z1), 0.07f, v.c(V3(IZQ, 2.45f, (z0 + z1) / 2f), K.DER, C.HIERRO, true), StrokeCap.Butt)
            barra(V3(IZQ + 0.09f, 2.37f, z0), V3(IZQ + 0.09f, 2.37f, z1), 0.05f, v.c(V3(IZQ, 2.37f, (z0 + z1) / 2f), K.DER, lerp(C.TINTA, C.ROJO, 0.25f), true), StrokeCap.Butt)
            barra(V3(DER - 0.12f, 2.15f, z0), V3(DER - 0.12f, 2.15f, z1), 0.16f, v.c(V3(DER, 2.15f, (z0 + z1) / 2f), K.IZQ, C.ACERO, true), StrokeCap.Butt)
            barra(V3(DER - 0.1f, 2.45f, z0), V3(DER - 0.1f, 2.45f, z1), 0.1f, v.c(V3(DER, 2.45f, (z0 + z1) / 2f), K.IZQ, C.COBRE, true), StrokeCap.Butt)
        }
    }

    /** Una línea en el suelo, de [grueso] metros. */
    private fun DrawScope.barraEnSuelo(v: VistaRefugio, a: V3, b: V3, grueso: Float, color: Color, alfa: Float) {
        Trazo3.run { barra(a, b, grueso, color.copy(alpha = alfa), StrokeCap.Butt) }
    }

    /** La franja de aviso amarilla y negra del zócalo, en la pared [x], de [y0] a [y1]. */
    private fun DrawScope.franjaDeAviso(v: VistaRefugio, x: Float, y0: Float, y1: Float, z0: Float, z1: Float, n: V3) {
        Trazo3.run { cara(v, listOf(V3(x, y0, z0), V3(x, y0, z1), V3(x, y1, z1), V3(x, y1, z0)), n, C.AMARILLO, true) }
        val paso = 0.32f; var z = z0
        while (z < z1) {
            val za = z; val zb = (z + paso * 0.5f).coerceAtMost(z1)
            Trazo3.run { cara(v, listOf(V3(x, y0, za), V3(x, y0, zb), V3(x, y1, (zb + 0.1f).coerceAtMost(z1)), V3(x, y1, (za + 0.1f).coerceAtMost(z1))), n, C.TINTA, true, 0.85f) }
            z += paso
        }
    }

    /** La roca húmeda entre dos costillas: vetas, manchas oscuras y el brillo de la humedad, «como un sudor». */
    private fun DrawScope.rocaHumeda(v: VistaRefugio, x: Float, y0: Float, y1: Float, z0: Float, z1: Float, n: V3, az: Azar) {
        repeat(5) {
            val zz = az.entre(z0, z1); val yy = az.entre(y0 + 0.2f, y1 - 0.2f); val w = az.entre(0.25f, 0.7f); val h = az.entre(0.12f, 0.4f)
            val pts = listOf(V3(x, yy - h, zz - w), V3(x, yy - h * 0.6f, zz + w * 0.7f), V3(x, yy + h, zz + w), V3(x, yy + h * 0.8f, zz - w * 0.5f))
            Trazo3.run { cara(v, pts, n, lerp(C.ROCA, if (it % 2 == 0) C.TINTA else C.HORMIGON, 0.3f), true, 0.55f) }
        }
        // El brillo de la humedad: hilos de luz que bajan por la roca.
        val brillo = lerp(v.colorTubo, Color.White, 0.3f)
        repeat(4) {
            val zz = az.entre(z0 + 0.1f, z1 - 0.1f); val ya = az.entre(y0 + 0.6f, y1); val yb = ya - az.entre(0.4f, 1.1f)
            Trazo3.run { barra(V3(x, ya, zz), V3(x, yb.coerceAtLeast(y0), zz + az.entre(-0.08f, 0.08f)), 0.02f, brillo.copy(alpha = 0.18f + 0.18f * v.fuerzaTubo.coerceAtMost(1f))) }
        }
    }

    /** Una rejilla del aire en el zócalo, de [z0] a [z1]. */
    private fun DrawScope.rejilla(v: VistaRefugio, x: Float, y: Float, z0: Float, z1: Float, n: V3) {
        Trazo3.run {
            cara(v, listOf(V3(x, y, z0), V3(x, y, z1), V3(x, y + 0.3f, z1), V3(x, y + 0.3f, z0)), n, C.TINTA, true)
            for (k in 1..4) { val yy = y + 0.3f * k / 5f; barra(V3(x, yy, z0 + 0.03f), V3(x, yy, z1 - 0.03f), 0.018f, v.c(V3(x, yy, (z0 + z1) / 2f), n, C.ACERO, true), StrokeCap.Butt) }
        }
    }

    /** Una costilla de acero en [z]: una viga doble T que sigue la sección, con su ala de cara a la sala y sus remaches. */
    private fun DrawScope.costilla(v: VistaRefugio, z: Float) {
        val hondo = 0.2f; val canto = 0.14f
        for (i in 0 until SECCION.size - 1) {
            val n = normalDentro(i)
            val a = q(SECCION[i], z); val b = q(SECCION[i + 1], z)
            val a2 = a + n * canto; val b2 = b + n * canto
            // El alma de la viga, que sobresale de la roca hacia dentro.
            Trazo3.run { cara(v, listOf(a, b, b2, a2), K.FRENTE, C.ACERO, true) }
            // El ala de dentro, que mira al túnel.
            Trazo3.run { cara(v, listOf(a2, b2, b2 + K.FONDO * hondo, a2 + K.FONDO * hondo), n, lerp(C.ACERO, C.CHAPA, 0.3f), true) }
        }
        // El filo de luz del canto y los remaches.
        val pts = SECCION.map { K.p(q(it, z)) }
        val filo = Path().apply { moveTo(pts[0].x, pts[0].y); for (i in 1 until pts.size) lineTo(pts[i].x, pts[i].y) }
        drawPath(filo, lerp(v.colorTubo, Color.White, 0.2f).copy(alpha = 0.35f), style = Stroke(0.5f))
        if (z < BOCA + 7f) for (i in 0 until SECCION.size - 1) {
            val n = normalDentro(i); val m = K.medio(q(SECCION[i], z), q(SECCION[i + 1], z)) + n * 0.07f
            drawCircle(v.c(m, K.FRENTE, lerp(C.ACERO, Color.White, 0.3f), true), 0.035f * K.k(m), K.p(m))
        }
    }

    // ---------------------------------------------------------------- El fondo: la Puerta

    /** El muro del fondo, la Puerta (cerrada, entornada o abierta) y, si está abierta, lo de fuera. */
    private fun DrawScope.fondo(v: VistaRefugio) {
        // El muro de hormigón del fondo, con el marco de acero de la Puerta.
        val muro = SECCION.map { q(it, PUERTA_Z) }
        Trazo3.run { caraDegradada(v, muro, K.FRENTE, lerp(C.HORMIGON, C.ROCA, 0.3f), V3(CENTRO_X, BOVEDA, PUERTA_Z), V3(CENTRO_X, 0f, PUERTA_Z), true, 3) }
        val c = PUERTA_C
        // El marco: un aro grueso de acero con sus pernos.
        Trazo3.run {
            disco(v, c + K.FRENTE * 0.02f, K.FRENTE, PUERTA_R + 0.38f, lerp(C.ACERO, C.TINTA, 0.25f), true, 32)
            aro(c + K.FRENTE * 0.03f, K.FRENTE, PUERTA_R + 0.2f, 0.06f, v.c(c, K.FRENTE, lerp(C.ACERO, Color.White, 0.25f), true), 32)
        }
        for (i in 0 until 16) { val a = 2f * PI.toFloat() * i / 16; val w = V3(c.x + cos(a) * (PUERTA_R + 0.3f), c.y + sin(a) * (PUERTA_R + 0.3f), PUERTA_Z - 0.04f); drawCircle(v.c(w, K.FRENTE, C.LATON, true), 0.05f * K.k(w), K.p(w)) }
        when (v.puerta) {
            Puerta.CERRADA -> hoja(v, 0f)
            Puerta.ENTORNADA -> { fuera(v); hoja(v, 9f) }
            Puerta.ABIERTA -> { fuera(v); hoja(v, 80f) }
        }
        // La línea amarilla que nadie pisa.
        val l0 = LINEA_Z; Trazo3.run { cara(v, listOf(V3(IZQ + 0.15f, 0.003f, l0), V3(DER - 0.15f, 0.003f, l0), V3(DER - 0.15f, 0.003f, l0 + 0.16f), V3(IZQ + 0.15f, 0.003f, l0 + 0.16f)), K.ARRIBA, C.AMARILLO, true) }
    }

    /**
     * La hoja de la Puerta, girada [giro] grados sobre su gozne (a la derecha, hacia el pasillo): un disco de acero grueso
     * con sus anillos, sus pasadores en el canto y, en el centro, la rueda «como la de una caja fuerte». Sin número.
     */
    private fun DrawScope.hoja(v: VistaRefugio, giro: Float) {
        val c = PUERTA_C; val r = PUERTA_R
        val g = Math.toRadians(giro.toDouble()).toFloat()
        // El gozne está en el canto de la derecha; la hoja gira hacia el pasillo (hacia los ojos).
        val gozne = V3(c.x + r, c.y, PUERTA_Z - 0.05f)
        // Gira hacia dentro: el canto de la izquierda viene hacia el pasillo.
        fun gira(p: V3): V3 { val dx = p.x - gozne.x; val dz = p.z - gozne.z; return V3(gozne.x + dx * cos(g) - dz * sin(g), p.y, gozne.z + dx * sin(g) + dz * cos(g)) }
        val n0 = K.unidad(V3(sin(g), 0f, -cos(g)))
        val cc0 = gira(V3(c.x, c.y, PUERTA_Z - 0.05f))
        val n = if (K.seVe(cc0, n0)) n0 else n0 * -1f
        val cc = cc0
        val grueso = 0.55f
        // El canto de la hoja (se ve cuando está girada).
        if (giro > 1f) {
            val borde = K.circulo(cc, n, r, 30)
            val atras = borde.map { it - n * grueso }
            val canto = K.casco((borde + atras).map { K.p(it) })
            drawPath(poligono(canto), v.c(cc, K.IZQ, lerp(C.ACERO, C.TINTA, 0.3f), true))
        }
        Trazo3.run {
            disco(v, cc, n, r, C.ACERO, true, 36)
            // Los anillos labrados de la hoja, los pasadores y la placa del número, sin número.
            aro(cc + n * 0.01f, n, r * 0.86f, 0.05f, v.c(cc, n, lerp(C.ACERO, C.TINTA, 0.35f), true), 32)
            aro(cc + n * 0.01f, n, r * 0.62f, 0.035f, v.c(cc, n, lerp(C.ACERO, C.TINTA, 0.3f), true), 28)
            aro(cc + n * 0.015f, n, r * 0.97f, 0.04f, v.c(cc, n, lerp(C.ACERO, Color.White, 0.25f), true), 36)
        }
        // La placa pintada donde iría el número: una franja amarilla gastada, sin cifras.
        val u = K.unidad(K.cruz(n, K.ARRIBA))
        val pl = cc + K.ARRIBA * (r * 0.74f)
        Trazo3.run { cara(v, listOf(pl - u * 0.42f - K.ARRIBA * 0.12f, pl + u * 0.42f - K.ARRIBA * 0.12f, pl + u * 0.42f + K.ARRIBA * 0.12f, pl - u * 0.42f + K.ARRIBA * 0.12f).map { it + n * 0.02f }, n, lerp(C.AMARILLO, C.ACERO, 0.35f), true) }
        // La rueda del centro: el aro, seis radios y el cubo.
        val rc = cc + n * 0.18f
        Trazo3.run {
            barra(cc, rc, 0.1f, v.c(cc, K.NADA, C.HIERRO, true))
            for (i in 0 until 6) { val a = PI.toFloat() * i / 3f; val d = u * cos(a) + K.ARRIBA * sin(a); barra(rc, rc + d * 0.5f, 0.05f, v.c(rc, n, lerp(C.ACERO, Color.White, 0.15f), true)); disco(v, rc + d * 0.56f, n, 0.06f, C.ACERO, true, 8) }
            aro(rc, n, 0.5f, 0.06f, v.c(rc, n, lerp(C.ACERO, Color.White, 0.2f), true), 24)
            disco(v, rc + n * 0.02f, n, 0.12f, C.HIERRO, true, 12)
        }
        // Los pasadores del canto, alrededor.
        for (i in 0 until 12) { val a = 2f * PI.toFloat() * i / 12 + 0.26f; val d = u * cos(a) + K.ARRIBA * sin(a); val w = cc + d * (r * 0.92f) + n * 0.02f; drawCircle(v.c(w, n, lerp(C.ACERO, C.TINTA, 0.4f), true), 0.07f * K.k(w), K.p(w)) }
        // Entornada: el filo de luz de fuera por la rendija.
        if (giro in 1f..30f) {
            val f = v.fuera ?: return
            val luz = lerp(lerp(f.medio, Color.White, 0.5f), f.bajo, 0.3f).copy(alpha = (0.5f + 0.5f * (1f - f.noche)).coerceIn(0f, 1f))
            val arco = K.circulo(V3(c.x, c.y, PUERTA_Z), K.FRENTE, r * 0.99f, 20, (PI * 0.55).toFloat(), (PI * 1.45).toFloat()).map { K.p(it) }
            val rendija = Path().apply { moveTo(arco[0].x, arco[0].y); for (i in 1 until arco.size) lineTo(arco[i].x, arco[i].y) }
            drawPath(rendija, luz, style = Stroke(1.3f, cap = StrokeCap.Round))
            drawPath(rendija, luz.copy(alpha = 0.25f), style = Stroke(4f, cap = StrokeCap.Round), blendMode = BlendMode.Plus)
        }
    }

    /**
     * Lo de fuera, por el hueco de la Puerta: el cielo de la hora, las montañas en dos filas con su bruma, el valle con el
     * arroyo y las ruinas del pueblo, la ladera con un árbol torcido y piedras; de noche, las estrellas y el fuego. Poco
     * detalle, pero en capas (regla 10). Con lluvia, la cortina gris.
     */
    private fun DrawScope.fuera(v: VistaRefugio) {
        val f = v.fuera ?: return
        val agujero = K.forma(K.circulo(V3(PUERTA_C.x, PUERTA_C.y, PUERTA_Z + 0.05f), K.FRENTE, PUERTA_R, 36))
        clipPath(agujero) {
            val h = K.CY
            cielo(f, h)
            val az = Azar(Semilla.de(v.m.ficha.semilla, "fuera"))
            val c = K.p(PUERTA_C)
            // Las estrellas, de noche.
            if (f.estrellas > 0.05f) repeat(26) { drawCircle(Color(0xFFF6F2E6).copy(alpha = (f.estrellas * az.entre(0.35f, 1f)).coerceIn(0f, 1f)), az.entre(0.25f, 0.55f), Offset(c.x + az.entre(-18f, 18f), c.y + az.entre(-18f, -1f))) }
            // El sol bajo de la tarde, que se pone detrás de las montañas.
            val sol = v.sol
            if (sol != null && sol.elevacion < 20f && kotlin.math.abs(sol.azimut) < 30f) {
                val ps = Offset(c.x - sol.azimut * 0.5f, h - sol.elevacion * 0.9f)
                drawCircle(Brush.radialGradient(listOf(f.solColor.copy(alpha = 0.7f), Color.Transparent), ps, 12f), 12f, ps, blendMode = BlendMode.Screen)
                drawCircle(lerp(f.solColor, Color.White, 0.6f), 2.2f, ps)
            }
            // Unas nubes chicas, si hay.
            if (f.nublado > 0.2f || az.real() < 0.5f) for (k in 0..1) nube(f, c.x - 24f + 22f * k + az.entre(-4f, 4f), c.y - 6f - 6f * k, 14f + 6f * az.real(), az, carga = f.nublado, lejos = 0.6f)
            // Las montañas: la fila de lejos, azulada, y la de cerca.
            monte(f, perfil(Azar(Semilla.de("refugio", "montes", 1)), c.x - 24f, c.x + 24f, h + 0.5f, 7f, 0.5f, 16), h + 6f, Color(0xFF6C7A8C), 0.85f)
            monte(f, perfil(Azar(Semilla.de("refugio", "montes", 2)), c.x - 24f, c.x + 24f, h + 3f, 5f, 0.6f, 14), h + 8f, Color(0xFF5E6E5A), 0.55f)
            // El valle, con el arroyo que brilla y las ruinas del pueblo.
            drawRect(f.tono(Color(0xFF6C7A4E), 0.4f), Offset(c.x - 24f, h + 3f), Size(48f, 6f))
            drawLine(lerp(f.tono(Color(0xFF9CB4C0), 0.3f), lerp(f.bajo, Color.White, 0.3f), 0.6f), Offset(c.x - 14f, h + 6.5f), Offset(c.x + 10f, h + 5f), 0.6f)
            for (k in 0..4) { val x = c.x + 4f + k * 2.6f; val alto = az.entre(1f, 2.6f); drawRect(f.tono(Color(0xFFB8A888), 0.35f), Offset(x, h + 4.5f - alto), Size(1.6f, alto)); drawRect(f.tono(Color(0xFF4A4438), 0.35f), Offset(x + 1.1f, h + 4.5f - alto), Size(0.5f, alto)) }
            // La ladera de delante, con hierba, piedras y un árbol torcido.
            val ladera = Path().apply { moveTo(c.x - 26f, h + 9f); cubicTo(c.x - 10f, h + 7f, c.x + 6f, h + 10f, c.x + 26f, h + 8f); lineTo(c.x + 26f, h + 30f); lineTo(c.x - 26f, h + 30f); close() }
            drawPath(ladera, f.tono(Color(0xFF7E8A4E), 0.1f))
            repeat(6) { val p = Offset(c.x + az.entre(-16f, 16f), h + az.entre(9.5f, 13f)); drawCircle(f.tono(Color(0xFF9A9480), 0.05f), az.entre(0.5f, 1.1f), p) }
            val t0 = Offset(c.x - 9f, h + 10f)
            drawLine(f.tono(Color(0xFF3E3428), 0f), t0, t0 + Offset(2f, -6f), 0.9f, StrokeCap.Round)
            drawLine(f.tono(Color(0xFF3E3428), 0f), t0 + Offset(2f, -6f), t0 + Offset(-1.5f, -10f), 0.6f, StrokeCap.Round)
            drawLine(f.tono(Color(0xFF3E3428), 0f), t0 + Offset(2f, -6f), t0 + Offset(5f, -8.5f), 0.5f, StrokeCap.Round)
            mancha(t0 + Offset(-1f, -10.5f), 3f, 1.8f, f.tono(Color(0xFF4E6038), 0f), 0.9f)
            mancha(t0 + Offset(5f, -9f), 2.4f, 1.5f, f.tono(Color(0xFF4E6038), 0f), 0.9f)
            // El fuego de la noche, delante de la Puerta, y su resplandor en la ladera.
            if (v.fuego > 0.02f) {
                val pf = Offset(c.x + 3f, h + 11f)
                drawCircle(Brush.radialGradient(listOf(C.FUEGO.copy(alpha = 0.55f * v.fuego), Color.Transparent), pf, 16f), 16f, pf, blendMode = BlendMode.Plus)
                drawCircle(lerp(C.FUEGO, Color.White, 0.4f).copy(alpha = v.fuego), 1.6f, pf)
            }
            // La lluvia: la cortina gris.
            if (R.climaDeFuera(v.m) == Clima.LLUVIA) drawRect(Color(0xFFA8B0B8).copy(alpha = 0.28f), Offset(c.x - 26f, c.y - 26f), Size(52f, 52f))
        }
        // El canto del hueco, en sombra.
        Trazo3.run { aro(V3(PUERTA_C.x, PUERTA_C.y, PUERTA_Z + 0.02f), K.FRENTE, PUERTA_R, 0.08f, lerp(C.TINTA, C.ACERO, 0.3f), 36) }
    }

    // ---------------------------------------------------------------- La línea amarilla, el ventanal y la compuerta

    /** La línea amarilla del suelo, a lo largo del pasillo, de la boca a la Puerta: la que lleva la diagonal hasta el fondo. */
    private fun DrawScope.lineaAmarilla(v: VistaRefugio) {
        val x = IZQ + 0.55f; val z0 = BOCA - 0.2f; val z1 = LINEA_Z
        val n = 8
        for (i in 0 until n) {
            val za = z0 + (z1 - z0) * i / n; val zb = z0 + (z1 - z0) * (i + 1) / n
            Trazo3.run { cara(v, listOf(V3(x, 0.004f, za), V3(x + 0.13f, 0.004f, za), V3(x + 0.13f, 0.004f, zb), V3(x, 0.004f, zb)), K.ARRIBA, lerp(C.AMARILLO, C.HORMIGON, 0.15f * (i % 3)), true) }
        }
    }

    /**
     * El ventanal de la sala de tanques, en la pared de la izquierda: tras el vidrio, los tanques de cristal con sus algas
     * (verdes y espesas, oscuras y llenas, o claras y recién sembradas, según el arco `cultivo`) bajo la luz violeta. El
     * día de la cosecha, los tanques se vacían y quedan «como ventanas».
     */
    private fun DrawScope.ventanal(v: VistaRefugio) {
        val x = IZQ; val n = K.DER
        val z0 = VENTANAL_Z0; val z1 = VENTANAL_Z1; val y0 = VENTANAL_Y0; val y1 = VENTANAL_Y1
        // El hueco: dentro, la sala de tanques en violeta.
        val dentro = listOf(V3(x, y0, z0), V3(x, y0, z1), V3(x, y1, z1), V3(x, y1, z0))
        val fondoTanques = lerp(lerp(C.TINTA, C.VIOLETA, 0.25f + 0.3f * v.tanques), C.CHAPA, 0.2f)
        drawPath(K.forma(dentro), fondoTanques)
        clipPath(K.forma(dentro)) {
            val cosecha = R.cosecha(v.m)
            val tramo = R.tramoCultivo(v.m)
            // El agua de cada tramo: recién sembrados, clara (más cristal que verde, con los brotes abajo); verdes y espesos;
            // llenos y oscuros de algas. El día de la cosecha, vaciándose: «parecen ventanas».
            val agua = when { cosecha -> lerp(C.PISCINA, C.PAPEL, 0.55f); tramo == EscenaRefugio.TramoCultivo.SEMBRADO -> lerp(C.ALGA, C.PAPEL, 0.55f); tramo == EscenaRefugio.TramoCultivo.VERDE -> C.ALGA; else -> lerp(C.ALGA, C.TINTA, 0.5f) }
            val opaco = when { cosecha -> 0.35f; tramo == EscenaRefugio.TramoCultivo.SEMBRADO -> 0.55f; tramo == EscenaRefugio.TramoCultivo.VERDE -> 0.85f; else -> 0.97f }
            // Los tanques: una fila de vasos de vidrio, cada uno con su agua hasta el nivel, la luz violeta arriba y el poso abajo.
            val tanques = 5
            val paso = (z1 - z0 - 0.5f) / tanques
            for (k in 0 until tanques) {
                val za = z0 + 0.25f + paso * k; val zb = za + paso * 0.84f
                val xa = x - 0.7f
                val nivel = if (cosecha) y0 + 0.15f + 0.9f * ((k + (v.m.ficha.especiales.firstOrNull { it.dia.id == "cosecha" }?.numero ?: 1)) % 3) / 3f else y1 - 0.18f
                val vaso = listOf(V3(xa, y0 - 0.1f, za), V3(xa, y0 - 0.1f, zb), V3(xa, nivel, zb), V3(xa, nivel, za))
                drawPath(K.forma(listOf(V3(xa, y0 - 0.1f, za), V3(xa, y0 - 0.1f, zb), V3(xa, y1, zb), V3(xa, y1, za))), lerp(C.VIOLETA, Color.White, 0.5f).copy(alpha = if (cosecha) 0.45f else 0.18f))
                drawPath(K.forma(vaso), Brush.verticalGradient(listOf(lerp(agua, C.VIOLETA, 0.35f * v.tanques).copy(alpha = opaco), agua.copy(alpha = opaco), lerp(agua, C.TINTA, 0.35f).copy(alpha = opaco)), startY = K.p(V3(xa, nivel, za)).y, endY = K.p(V3(xa, y0, za)).y))
                // El poso y los brotes de abajo, recién sembrados: mechones verdes.
                if (tramo == EscenaRefugio.TramoCultivo.SEMBRADO && !cosecha) for (j in 0..3) { val zz = za + (zb - za) * (j + 0.5f) / 4f; Trazo3.run { barra(V3(xa, y0 - 0.05f, zz), V3(xa, y0 + 0.18f, zz + 0.04f), 0.035f, lerp(C.ALGA, C.TINTA, 0.2f)) } }
                // La línea del agua, el brillo del vidrio y el borde.
                Trazo3.run { barra(V3(xa, nivel, za), V3(xa, nivel, zb), 0.025f, lerp(C.VIOLETA, Color.White, 0.6f).copy(alpha = 0.7f)); barra(V3(xa, y0 - 0.1f, za), V3(xa, y1, za), 0.03f, lerp(C.VIOLETA, Color.White, 0.5f).copy(alpha = 0.65f)) }
            }
            // Las lámparas violetas encima de los tanques.
            for (k in 0..2) { val zz = z0 + 1.2f + 2.4f * k; val w = V3(x - 0.7f, y1 - 0.08f, zz); drawLine(lerp(C.VIOLETA, Color.White, 0.6f).copy(alpha = 0.5f + 0.5f * v.tanques), K.p(w), K.p(w + V3(0f, 0f, 0.9f)), 0.9f) }
        }
        // El marco de acero remachado y el vidrio.
        Trazo3.run {
            for ((a, b) in listOf(V3(x, y0, z0) to V3(x, y0, z1), V3(x, y1, z0) to V3(x, y1, z1), V3(x, y0, z0) to V3(x, y1, z0), V3(x, y0, z1) to V3(x, y1, z1))) barra(a, b, 0.09f, v.c(K.medio(a, b), n, C.HIERRO, true), StrokeCap.Square)
            for (k in 1..3) { val zz = z0 + (z1 - z0) * k / 4f; barra(V3(x, y0, zz), V3(x, y1, zz), 0.05f, v.c(V3(x, 1.6f, zz), n, C.HIERRO, true)) }
        }
        drawPath(K.forma(dentro), lerp(C.VIOLETA, Color.White, 0.5f).copy(alpha = 0.03f + 0.03f * v.tanques), blendMode = BlendMode.Plus)
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
        // El marco.
        Trazo3.run { cara(v, listOf(V3(x, 0f, z0 - 0.2f), V3(x, 0f, z1 + 0.2f), V3(x, alto + 0.25f, z1 + 0.2f), V3(x, alto + 0.25f, z0 - 0.2f)), n, lerp(C.ACERO, C.CHAPA, 0.4f), true) }
        when (v.compuerta) {
            Compuerta.ABIERTA -> {
                // El pasillo del sector, oscuro, y la hoja recogida arriba: se le ve el canto con su franja.
                drawPath(K.forma(hueco), v.c(V3(x - 1f, 1f, (z0 + z1) / 2f), K.DER, lerp(C.TINTA, C.ROCA, 0.3f), true))
                Trazo3.run { cara(v, listOf(V3(x, alto - 0.18f, z0), V3(x, alto - 0.18f, z1), V3(x, alto, z1), V3(x, alto, z0)), K.ABAJO, C.AMARILLO, true) }
                var z = z0; while (z < z1) { Trazo3.run { cara(v, listOf(V3(x, alto - 0.18f, z), V3(x, alto - 0.18f, (z + 0.14f).coerceAtMost(z1)), V3(x, alto, (z + 0.22f).coerceAtMost(z1)), V3(x, alto, (z + 0.08f).coerceAtMost(z1))), K.ABAJO, C.TINTA, true) }; z += 0.3f }
            }
            Compuerta.BAJADA -> {
                Trazo3.run { caraDegradada(v, hueco, n, lerp(C.ACERO, C.CHAPA, 0.2f), V3(x, alto, (z0 + z1) / 2f), V3(x, 0f, (z0 + z1) / 2f), true) }
                // Los refuerzos de la hoja, la ventanilla y el intercomunicador.
                Trazo3.run {
                    for (k in 1..3) { val yy = alto * k / 4f; barra(V3(x, yy, z0), V3(x, yy, z1), 0.06f, v.c(V3(x, yy, z0), n, lerp(C.ACERO, C.TINTA, 0.3f), true), StrokeCap.Butt) }
                    val zm = (z0 + z1) / 2f
                    cara(v, listOf(V3(x, 1.0f, zm - 0.3f), V3(x, 1.0f, zm + 0.3f), V3(x, 1.4f, zm + 0.3f), V3(x, 1.4f, zm - 0.3f)), n, C.HIERRO, true)
                    barra(V3(x, 1.0f, zm), V3(x, 1.4f, zm), 0.025f, v.c(V3(x, 1.2f, zm), n, C.ACERO, true))
                    cara(v, listOf(V3(x, 1.5f, z1 - 0.25f), V3(x, 1.5f, z1 - 0.08f), V3(x, 1.75f, z1 - 0.08f), V3(x, 1.75f, z1 - 0.25f)), n, lerp(C.CREMA, C.ACERO, 0.4f), true)
                }
            }
        }
        // La luz de aviso, encima: apagada, o roja y encendida.
        val luz = LUZ_COMPUERTA
        Trazo3.run { disco(v, luz, n, 0.13f, C.HIERRO, true, 14) }
        if (v.compuerta == Compuerta.BAJADA) {
            drawCircle(lerp(C.ALARMA, Color.White, 0.35f), 0.09f * K.k(luz), K.p(luz))
            drawCircle(Brush.radialGradient(listOf(C.ALARMA.copy(alpha = 0.5f), Color.Transparent), K.p(luz), 0.9f * K.k(luz)), 0.9f * K.k(luz), K.p(luz), blendMode = BlendMode.Plus)
        } else drawCircle(lerp(C.ROJO, C.TINTA, 0.45f), 0.08f * K.k(luz), K.p(luz))
    }

    /** Lo que hay en el suelo del pasillo según el día: el árbol de cable, las botellas del racionamiento, las cestas, las hojas. */
    private fun DrawScope.loDelPasillo(v: VistaRefugio) {
        with(SalaRefugio) { cosasDelPasillo(v) }
    }
}
