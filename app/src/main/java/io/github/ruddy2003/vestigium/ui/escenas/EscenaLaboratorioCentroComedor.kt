package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.CentroLab.Mesa
import io.github.ruddy2003.vestigium.ui.escenas.GeoCentro.RV
import io.github.ruddy2003.vestigium.ui.escenas.GeoCentro.RW
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.ARRIBA
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.caja
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.cara
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.cilindro
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.linea
import io.github.ruddy2003.vestigium.ui.escenas.PiezasCentro.trazo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * El comedor grande, en el suelo del hall (`composicion_laboratorio_centro.md`, «El vacío y el suelo del comedor»): el
 * suelo redondo de losas claras con las juntas en abanico, gastadas por los pasos; **las mesas largas** en filas que fugan
 * hacia la cabecera, cada una con lo suyo según la hora (sin personas: lo que queda); el mostrador con sus cubas; **la mesa
 * del Consejo en la cabecera**, sobre el estrado, delante de la pared de pantallas; **las flechas de colores**, que nacen del
 * centro y se van hacia las bocas de los pasillos (el Roble, el Haya y el Almendro, el de Botánica, con el resplandor de los
 * jardines al fondo); la puerta redonda de la sala del destello; y lo de los días: la cinta de la cola del censo, las
 * sillas en filas de las jornadas, las fresas del año nuevo, el pastel de la Fundación, las velas de la noche larga.
 */
internal object ComedorCentro {
    private val C = ColorLaboratorio

    /** Una mesa larga: de [x0] a [x1] a lo largo de x, a la altura [z] del hall. */
    class MesaLarga(val x0: Float, val x1: Float, val z: Float)

    val MESAS = listOf(
        MesaLarga(-6.2f, -2.0f, -1.5f), MesaLarga(2.0f, 6.5f, -1.5f),
        MesaLarga(-6.6f, -2.0f, 1.5f), MesaLarga(2.0f, 6.7f, 1.5f),
        MesaLarga(-6.0f, -0.7f, 3.7f), MesaLarga(0.7f, 6.2f, 3.7f),
        MesaLarga(-4.4f, -0.5f, 5.8f), MesaLarga(0.5f, 4.6f, 5.8f),
    )
    const val ALTO_MESA = 0.76f
    const val ANCHO_MESA = 0.9f

    /** La cabecera: el estrado delante de la pared de pantallas, con la mesa del Consejo a lo largo de z. */
    const val CABECERA_X = 8.55f
    const val ESTRADO = 0.35f
    private const val EX0 = 7.3f
    private const val EX1 = 9.9f
    private const val EZ0 = -2.6f
    private const val EZ1 = 3.6f

    /** Los colores de las tres bocas (el Roble, el Haya, el Almendro) y el de la flecha del destello. */
    val COLORES_BOCAS = listOf(C.BARRO, C.AMARILLO, C.GRAMA)
    val COLOR_DESTELLO: Color = lerp(C.CIAN, C.VIOLETA, 0.45f)

    fun DrawScope.abajo(v: VistaCentro) {
        muro(v)
        suelo(v)
        flechas(v)
        with(LuzCentro) { enElSuelo(v) }
        mostrador(v)
        with(LuzCentro) { sombrasEnElSuelo(v, esqueletos(v)) }
        cosas(v)
    }

    // ---------------------------------------------------------------- El muro de abajo, con las bocas

    private fun haciaDentro(g: Float): V3 = V3(-cos(GeoCentro.rad(g)), 0f, -sin(GeoCentro.rad(g)))

    /** El muro del piso de abajo, bajo la primera galería: en penumbra, con las tres bocas de los pasillos y la puerta del destello. */
    private fun DrawScope.muro(v: VistaCentro) {
        val alto = GeoCentro.G1 - GeoCentro.LOSA
        for ((a, b) in GaleriaCentro.tramos()) {
            val gm = (a + b) / 2f
            cara(v, PiezasCentro.tramoV(RW, 0f, alto, a, b), lerp(C.HORMIGON, C.TIERRA, 0.12f), haciaDentro(gm), GaleriaCentro.lejos(GeoCentro.aro(RW, 0f, gm)))
        }
        // La losa de la primera galería vista por debajo, desde lejos (el techo del piso de abajo): una franja oscura.
        for ((a, b) in GaleriaCentro.tramos()) cara(v, PiezasCentro.tramoH(RV, RW, alto, a, b), lerp(C.HORMIGON, C.SOMBRA, 0.25f), PiezasCentro.ABAJO, GaleriaCentro.lejos(GeoCentro.aro(RV, alto, a)))
        PiezasCentro.run { manchasEnLaPared(v, RW, 0.3f, alto - 0.2f, GeoCentro.PA1, 360f + GeoCentro.PA0, 50, "muro") }
        for ((i, g) in GeoCentro.BOCAS.withIndex()) boca(v, g, COLORES_BOCAS[i], i == 2)
        destello(v)
    }

    /**
     * La boca de un pasillo: el hueco que se aleja (su suelo con la flecha que sigue), el marco con la franja de su color y,
     * encima, un rótulo sin letras con la silueta de su hoja. Por la de Botánica, al fondo, el resplandor de los jardines.
     */
    private fun DrawScope.boca(v: VistaCentro, g: Float, color: Color, botanica: Boolean) {
        val n = haciaDentro(g); val lj = GaleriaCentro.lejos(GeoCentro.aro(RW, 0f, g))
        val ancho = 6.5f; val alto = 2.5f
        val g0 = g - ancho; val g1 = g + ancho
        // El marco, pintado del color del pasillo.
        cara(v, PiezasCentro.tramoV(RW - 0.02f, 0f, alto + 0.18f, g0 - 0.9f, g1 + 0.9f), color, n, lj, siempre = true)
        // El hueco: el pasillo que se va, oscuro, con su fondo encendido.
        val hueco = PiezasCentro.tramoV(RW - 0.03f, 0f, alto, g0, g1)
        val f = CamCentro.forma(hueco) ?: return
        val luzFondo = if (botanica) lerp(v.colorBotanica, Color.White, 0.2f) else lerp(C.LUZ_DIA, v.fondo, 0.5f)
        val fondo = lerp(lerp(C.SOMBRA, C.NEGRO, 0.4f), luzFondo, if (botanica) 0.65f * v.botanica else 0.3f)
        val arriba = CamCentro.p(GeoCentro.aro(RW, alto, g)); val abajo = CamCentro.p(GeoCentro.aro(RW, 0f, g))
        drawPath(f, Brush.verticalGradient(listOf(lerp(fondo, C.NEGRO, 0.35f), fondo, lerp(fondo, color, 0.15f)), startY = arriba.y, endY = abajo.y))
        // Al fondo del pasillo, una puerta más clara (la boca de lejos), y la flecha del suelo que se mete.
        val fondoBoca = CamCentro.forma(PiezasCentro.tramoV(RW - 0.04f, 0.2f, alto * 0.62f, g - ancho * 0.3f, g + ancho * 0.3f))
        if (fondoBoca != null) drawPath(fondoBoca, lerp(fondo, luzFondo, 0.55f))
        val fl = CamCentro.forma(listOf(GeoCentro.aro(RW - 0.04f, 0.01f, g - 1.2f), GeoCentro.aro(RW - 0.04f, 0.01f, g + 1.2f), GeoCentro.aro(RW - 0.04f, 0.28f, g + 0.4f), GeoCentro.aro(RW - 0.04f, 0.28f, g - 0.4f)))
        if (fl != null) drawPath(fl, lerp(color, fondo, 0.45f))
        // El rótulo sin letras: una placa del color del pasillo, con la silueta de su hoja (roble, haya, almendro).
        val placa = PiezasCentro.tramoV(RW - 0.03f, alto + 0.28f, alto + 0.62f, g - 2.6f, g + 2.6f)
        cara(v, placa, lerp(color, C.TELA, 0.25f), n, lj, siempre = true)
        val c = CamCentro.p(GeoCentro.aro(RW - 0.04f, alto + 0.45f, g)); val k = CamCentro.k(GeoCentro.aro(RW, alto, g))
        val hoja = v.c(GeoCentro.aro(RW, alto, g), lerp(C.HOJA_OSC, C.NEGRO, 0.2f), n, lj)
        mancha(c, 0.22f * k, 0.09f * k, hoja, 0.95f)
        drawLine(hoja, c - Offset(0.28f * k, 0f), c + Offset(0.26f * k, 0f), 0.4f)
        // El resplandor de los jardines, que sale por la boca de Botánica y se tiende en el suelo.
        if (botanica && v.botanica > 0.05f) {
            val p = CamCentro.p(GeoCentro.aro(RW - 1.2f, 0f, g))
            mancha(p, 2.8f * k, 0.55f * k, v.colorBotanica, 0.3f * v.botanica)
        }
    }

    /** La boca de la sala del destello: una puerta redonda, con su resplandor azul que late (la parte viva lo hace latir). */
    private fun DrawScope.destello(v: VistaCentro) {
        val g = GeoCentro.DESTELLO; val n = haciaDentro(g)
        val c = GeoCentro.aro(RW - 0.03f, 1.35f, g); val a = GeoCentro.rad(g); val t = V3(-sin(a), 0f, cos(a))
        val aro = (0 until 24).map { i -> val b = i * 2f * Math.PI.toFloat() / 24f; c + t * (1.25f * cos(b)) + V3(0f, 1.25f * sin(b), 0f) }
        val dentro = (0 until 24).map { i -> val b = i * 2f * Math.PI.toFloat() / 24f; c + t * (0.98f * cos(b)) + V3(0f, 0.98f * sin(b), 0f) }
        CamCentro.forma(aro)?.let { drawPath(it, v.c(c, lerp(C.ACERO, COLOR_DESTELLO, 0.25f), n, GaleriaCentro.lejos(c))) }
        val azul = lerp(Color(0xFF8FC4FF), Color.White, 0.2f)
        CamCentro.forma(dentro)?.let { drawPath(it, lerp(lerp(C.NEGRO, C.SOMBRA, 0.3f), azul, 0.55f * v.destello + 0.1f)) }
        if (v.destello > 0.01f) { val p = CamCentro.p(c); val k = CamCentro.k(c); mancha(p, 2.4f * k, 1.6f * k, azul, 0.22f * v.destello) }
    }

    // ---------------------------------------------------------------- El suelo

    /** El suelo redondo: losas en anillos, con las juntas en abanico, de una en una con su tono; bajo las galerías, en penumbra. */
    private fun DrawScope.suelo(v: VistaCentro) {
        val az = Azar(Semilla.de("laboratorio", "centro", "suelo"))
        val propio = lerp(lerp(C.HORMIGON, C.TELA, 0.5f), C.BARRO, 0.08f)
        val anillos = 10; val gajos = 36
        for (j in 0 until anillos) {
            val r0 = RW * j / anillos; val r1 = RW * (j + 1) / anillos
            for (i in 0 until gajos) {
                val g0 = -180f + 360f * i / gajos; val g1 = g0 + 360f / gajos
                val ps = if (j == 0) listOf(V3(0f, 0f, 0f), GeoCentro.aro(r1, 0f, g0), GeoCentro.aro(r1, 0f, g1)) else PiezasCentro.tramoH(r0, r1, 0f, g0, g1)
                val q = CamCentro.puntos(ps); if (q.size < 3 || q.none { CamCentro.enCuadro(it, 6f) }) continue
                val w = GeoCentro.aro((r0 + r1) / 2f, 0f, (g0 + g1) / 2f)
                val tono = lerp(propio, if (az.real() > 0.5f) C.TELA else C.PISO, az.entre(0f, 0.22f))
                drawPath(poligono(q), v.c(w, tono, ARRIBA, GaleriaCentro.lejos(w) * 0.7f, conHaz = false))
            }
        }
        // Las juntas: los anillos y los radios, finos.
        val junta = lerp(C.SOMBRA, C.PISO, 0.45f)
        for (j in 1..anillos) trazo(v, PiezasCentro.arco(RW * j / anillos, 0.003f, -180f, 180f, 72), junta, ARRIBA, 0.03f, 0.45f)
        for (i in 0 until gajos) { val g = -180f + 360f * i / gajos; linea(v, GeoCentro.aro(RW / anillos, 0.003f, g), GeoCentro.aro(RW, 0.003f, g), junta, ARRIBA, 0.5f, 0.35f) }
        // Lo gastado por los pasos: los pasillos entre las mesas y el camino a las bocas, más pulidos y un poco más oscuros.
        val gastado = lerp(C.PISO, C.SOMBRA, 0.25f)
        for (z in listOf(0f, 2.6f, 4.8f, -2.9f)) {
            var x = -6f
            while (x < 7f) { val w = V3(x, 0f, z); val q = CamCentro.p(w); val k = CamCentro.k(w); mancha(q, 0.9f * k, 0.3f * k * 0.45f, v.c(w, gastado, ARRIBA, conHaz = false), 0.16f); x += 0.9f }
        }
        // El medallón del centro: un aro de latón embutido, donde nacen las flechas.
        trazo(v, PiezasCentro.arco(1.65f, 0.004f, -180f, 180f, 40), C.LATON, ARRIBA, 0.07f, 0.9f)
        trazo(v, PiezasCentro.arco(1.45f, 0.004f, -180f, 180f, 40), lerp(C.LATON, C.SOMBRA, 0.3f), ARRIBA, 0.025f, 0.7f)
    }

    /** Las flechas de colores: nacen del medallón y se van hacia las bocas y hacia la puerta del destello, con sus puntas. */
    private fun DrawScope.flechas(v: VistaCentro) {
        val destinos = GeoCentro.BOCAS.zip(COLORES_BOCAS) + listOf(GeoCentro.DESTELLO to COLOR_DESTELLO)
        for ((g, color) in destinos) {
            val a = GeoCentro.rad(g); val t = V3(-sin(a), 0f, cos(a)); val u = V3(cos(a), 0f, sin(a))
            val r0 = 1.75f; val r1 = RW - 0.4f; val w = 0.16f
            val tira = listOf(u * r0 + t * -w, u * r1 + t * -w, u * r1 + t * w, u * r0 + t * w).map { it + V3(0f, 0.004f, 0f) }
            val f = CamCentro.forma(tira) ?: continue
            drawPath(f, v.c(u * ((r0 + r1) / 2f), lerp(color, C.PISO, 0.25f), ARRIBA, 0.2f, conHaz = false).copy(alpha = 0.85f))
            // Las puntas, cada dos metros y medio.
            var r = r0 + 1.2f
            while (r < r1 - 0.6f) {
                val punta = listOf(u * r + t * -0.32f, u * (r + 0.45f), u * r + t * 0.32f, u * (r + 0.16f)).map { it + V3(0f, 0.005f, 0f) }
                CamCentro.forma(punta)?.let { drawPath(it, v.c(u * r, color, ARRIBA, 0.1f, conHaz = false)) }
                r += 2.5f
            }
        }
    }

    // ---------------------------------------------------------------- El mostrador

    /** El mostrador, contra el muro de la izquierda, bajo la galería: largo y curvo, con las cubas (humean en el desayuno y la comida) y las bandejas apiladas. */
    private fun DrawScope.mostrador(v: VistaCentro) {
        val r = RW - 1.3f
        for (g in listOf(132f, 140f, 148f, 156f, 164f)) {
            val a = GeoCentro.rad(g); val o = GeoCentro.aro(r, 0f, g); val u = V3(-sin(a), 0f, cos(a))
            caja(v, o, u, 1.55f, 0.75f, 0.95f, lerp(C.ACERO, C.HORMIGON, 0.35f), GaleriaCentro.lejos(o), tapa = lerp(C.ACERO, Color.White, 0.25f))
        }
        val humean = v.mesa == Mesa.DESAYUNO || v.mesa == Mesa.COMIDA || v.mesa == Mesa.FIESTA
        for ((i, g) in listOf(134f, 141f, 149f, 157f).withIndex()) {
            val c = GeoCentro.aro(r - 0.05f, 0.95f, g)
            cilindro(v, c, 0.26f, 0.32f, lerp(C.ACERO, Color.White, 0.2f), lerp(C.ACERO, C.NEGRO, 0.4f))
            if (humean) { val q = CamCentro.p(c + V3(0f, 0.6f, 0f)); val k = CamCentro.k(c); mancha(q, 0.35f * k, 0.5f * k, lerp(v.fondo, Color.White, 0.5f), 0.22f - 0.03f * i) }
        }
        // Las bandejas apiladas al final del mostrador.
        val p = GeoCentro.aro(r, 0.95f, 162f)
        for (j in 0 until 6) caja(v, p + V3(0f, j * 0.03f, 0f), V3(0f, 0f, 1f), 0.45f, 0.35f, 0.025f, lerp(C.ACERO, C.CIAN, 0.2f), GaleriaCentro.lejos(p))
    }

    // ---------------------------------------------------------------- Las mesas y lo de encima

    /** Los esqueletos de lo que hay en el suelo (para sus sombras en la mancha del pozo). */
    fun esqueletos(v: VistaCentro): List<List<V3>> = buildList {
        for (t in MESAS) add(PiezasCentro.esquinas(V3((t.x0 + t.x1) / 2f, 0f, t.z), V3(1f, 0f, 0f), t.x1 - t.x0, ANCHO_MESA, ALTO_MESA, ALTO_MESA - 0.06f))
        for (t in MESAS) for (s in listOf(-1f, 1f)) add(PiezasCentro.esquinas(V3((t.x0 + t.x1) / 2f + bancoCorrido(v, t, s), 0f, t.z + s * 0.72f), V3(1f, 0f, 0f), t.x1 - t.x0 - 0.3f, 0.3f, 0.45f, 0.4f))
        add(PiezasCentro.esquinas(V3((EX0 + EX1) / 2f, 0f, (EZ0 + EZ1) / 2f), V3(1f, 0f, 0f), EX1 - EX0, EZ1 - EZ0, ESTRADO))
        add(PiezasCentro.esquinas(V3(CABECERA_X, ESTRADO, 0.4f), V3(0f, 0f, 1f), 4.2f, 0.95f, ALTO_MESA, ALTO_MESA - 0.06f))
    }

    /** Cuánto se ha corrido el banco [s] de la mesa [t] (en la comida, apartados; en la cena, a ras). */
    private fun bancoCorrido(v: VistaCentro, t: MesaLarga, s: Float): Float {
        val az = Azar(Semilla.de("laboratorio", "centro", "banco", (t.z * 10).toInt(), (t.x0 * 10).toInt(), s.toInt()))
        return when (v.mesa) { Mesa.COMIDA, Mesa.FIESTA -> az.entre(-0.3f, 0.3f); Mesa.DESAYUNO, Mesa.CENSO -> az.entre(-0.12f, 0.12f); Mesa.CENA -> 0f }
    }

    /** Las mesas, de atrás adelante, con sus bancos y lo de encima; y el estrado con la mesa del Consejo. */
    private fun DrawScope.cosas(v: VistaCentro) {
        val orden = (MESAS.map { CamCentro.hondo(V3((it.x0 + it.x1) / 2f, 0f, it.z)) to { mesa(v, it) } } +
            listOf(CamCentro.hondo(V3(CABECERA_X, 0f, 0.5f)) to { cabecera(v) }) +
            (if (CentroLab.jornadas(v.m)) listOf(CamCentro.hondo(V3(5f, 0f, 0.5f)) + 0.5f to { jornadas(v) }) else emptyList()) +
            (if (v.mesa == Mesa.CENSO) listOf(CamCentro.hondo(V3(-2.8f, 0f, -0.2f)) to { censo(v) }) else emptyList()))
            .sortedByDescending { it.first }
        for ((_, pinta) in orden) pinta()
    }

    private fun DrawScope.mesa(v: VistaCentro, t: MesaLarga) {
        val cx = (t.x0 + t.x1) / 2f; val largo = t.x1 - t.x0; val u = V3(1f, 0f, 0f)
        val madera = lerp(C.MADERA, C.HORMIGON, 0.15f)
        // La sombra blanda de debajo (la luz del hall viene de arriba).
        val sq = CamCentro.p(V3(cx, 0f, t.z)); val sk = CamCentro.k(V3(cx, 0f, t.z))
        mancha(sq, largo * 0.55f * sk, 0.7f * sk * 0.5f, v.c(V3(cx, 0f, t.z), C.SOMBRA, ARRIBA, conHaz = false), 0.35f)
        // El banco de detrás, la mesa (patas y tablero) y el banco de delante.
        val detras = if (CamCentro.hondo(V3(cx, 0f, t.z + 0.72f)) > CamCentro.hondo(V3(cx, 0f, t.z - 0.72f))) 1f else -1f
        banco(v, t, detras)
        for (x in listOf(t.x0 + 0.25f, t.x1 - 0.25f)) for (s in listOf(-0.3f, 0.3f)) caja(v, V3(x, 0f, t.z + s), u, 0.07f, 0.07f, ALTO_MESA - 0.05f, lerp(C.ACERO, C.NEGRO, 0.25f))
        caja(v, V3(cx, ALTO_MESA - 0.05f, t.z), u, largo, ANCHO_MESA, 0.05f, madera, tapa = lerp(madera, C.TELA, 0.25f))
        encima(v, t)
        banco(v, t, -detras)
    }

    private fun DrawScope.banco(v: VistaCentro, t: MesaLarga, s: Float) {
        val cx = (t.x0 + t.x1) / 2f + bancoCorrido(v, t, s)
        caja(v, V3(cx, 0f, t.z + s * 0.72f), V3(1f, 0f, 0f), t.x1 - t.x0 - 0.3f, 0.3f, 0.45f, lerp(C.MADERA, C.NEGRO, 0.12f), tapa = lerp(C.MADERA, C.TELA, 0.1f))
    }

    /** Lo de encima de una mesa, según la hora: bandejas de cuatro huecos, jarras, vasos; las fresas, el pastel, las velas. */
    private fun DrawScope.encima(v: VistaCentro, t: MesaLarga) {
        val az = Azar(Semilla.de(v.m.ficha.semilla, "centro", "mesa", (t.z * 10).toInt(), (t.x0 * 10).toInt()))
        val y = ALTO_MESA
        val cuantas = when (v.mesa) { Mesa.DESAYUNO -> az.entero(5); Mesa.COMIDA -> 4 + az.entero(5); Mesa.FIESTA -> 5 + az.entero(3); Mesa.CENA -> 0; Mesa.CENSO -> az.entero(2) }
        val largo = t.x1 - t.x0
        repeat(cuantas) {
            val x = t.x0 + 0.35f + az.real() * (largo - 0.7f); val s = if (az.real() > 0.5f) 0.22f else -0.22f
            val o = V3(x, y, t.z + s)
            caja(v, o, V3(1f, 0f, 0f), 0.42f, 0.32f, 0.02f, lerp(C.ACERO, C.CIAN, 0.25f), tapa = lerp(C.ACERO, Color.White, 0.3f))
            // Los cuatro huecos con lo que quedó: verde, barro, tierra, crema.
            val q = CamCentro.p(o + V3(0f, 0.025f, 0f)); val k = CamCentro.k(o)
            for ((i, col) in listOf(C.GRAMA, C.BARRO, C.TIERRA, C.TELA).withIndex()) {
                if (az.real() < 0.35f) continue
                mancha(q + Offset(((i % 2) - 0.5f) * 0.18f * k, ((i / 2) - 0.5f) * 0.05f * k), 0.06f * k, 0.022f * k, v.c(o, col, ARRIBA), 0.9f)
            }
            if (CentroLab.fresas(v.m)) for (i in 0..2) drawCircle(v.c(o, C.TOMATE, ARRIBA), (0.03f * k).coerceAtLeast(0.5f), q + Offset((i - 1) * 0.06f * k, -0.01f * k))
        }
        // Las jarras de agua y los vasos.
        if (v.mesa != Mesa.CENA) repeat(1 + az.entero(2)) {
            val c = V3(t.x0 + 0.5f + az.real() * (largo - 1f), y, t.z + az.entre(-0.1f, 0.1f))
            cilindro(v, c, 0.07f, 0.24f, lerp(C.VIDRIO, Color.White, 0.25f), lerp(C.VIDRIO, C.CIAN, 0.3f), rArriba = 0.06f)
        }
        // El pastel de zanahoria de la Fundación, en una de cada dos mesas.
        if (CentroLab.banderas(v.m) && v.mesa == Mesa.FIESTA && az.real() > 0.4f) cilindro(v, V3((t.x0 + t.x1) / 2f, y, t.z), 0.18f, 0.1f, lerp(C.BARRO, C.AMARILLO, 0.4f), lerp(C.TELA, C.AMARILLO, 0.2f))
        // Las velas de la noche larga: tres por mesa, con su llama (la luz la pone LuzCentro).
        if (v.velas) for (i in 0..2) {
            val c = V3(t.x0 + largo * (0.2f + 0.3f * i), y, t.z)
            cilindro(v, c, 0.025f, 0.12f, C.TELA, lerp(C.TELA, C.AMARILLO, 0.2f))
            val q = CamCentro.p(c + V3(0f, 0.16f, 0f)); val k = CamCentro.k(c)
            drawCircle(lerp(C.VELA, Color.White, 0.4f), (0.025f * k).coerceAtLeast(0.6f), q)
            drawCircle(C.VELA.copy(alpha = 0.35f), (0.18f * k).coerceAtLeast(1.5f), q, blendMode = BlendMode.Plus)
        }
    }

    // ---------------------------------------------------------------- La cabecera

    /** El estrado y la mesa del Consejo, con sus sillas de respaldo mirando al hall; en la cena, puesta y con sus lámparas bajas. */
    private fun DrawScope.cabecera(v: VistaCentro) {
        val piedra = lerp(C.HORMIGON, C.TIERRA, 0.18f)
        caja(v, V3((EX0 + EX1) / 2f, 0f, (EZ0 + EZ1) / 2f), V3(1f, 0f, 0f), EX1 - EX0, EZ1 - EZ0, ESTRADO, piedra, tapa = lerp(piedra, C.PISO, 0.3f))
        // El peldaño de delante.
        caja(v, V3(EX0 - 0.2f, 0f, 0.5f), V3(0f, 0f, 1f), 2.2f, 0.4f, ESTRADO / 2f, piedra)
        val u = V3(0f, 0f, 1f); val y = ESTRADO
        // Las sillas de respaldo, detrás de la mesa (de espaldas a las pantallas).
        for (z in listOf(-1.4f, -0.5f, 0.4f, 1.3f, 2.2f)) {
            caja(v, V3(CABECERA_X + 0.7f, y, z), u, 0.42f, 0.42f, 0.46f, lerp(C.MADERA, C.NEGRO, 0.2f))
            caja(v, V3(CABECERA_X + 0.9f, y + 0.46f, z), u, 0.42f, 0.05f, 0.55f, lerp(C.MADERA, C.NEGRO, 0.25f))
        }
        val madera = lerp(C.MADERA, C.BARRO, 0.15f)
        for (z in listOf(-1.5f, 2.3f)) caja(v, V3(CABECERA_X, y, z), u, 0.08f, 0.8f, ALTO_MESA - 0.05f, lerp(madera, C.NEGRO, 0.3f))
        caja(v, V3(CABECERA_X, y + ALTO_MESA - 0.05f, 0.4f), u, 4.2f, 0.95f, 0.05f, madera, tapa = lerp(madera, C.TELA, 0.2f))
        // Puesta en la cena (platos y vasos) y con las lámparas bajas, que cuelgan de cables largos.
        if (v.mesa == Mesa.CENA || v.mesa == Mesa.FIESTA) {
            for (z in listOf(-1.4f, -0.5f, 0.4f, 1.3f, 2.2f)) {
                val c = V3(CABECERA_X + 0.15f, y + ALTO_MESA + 0.005f, z)
                cilindro(v, c, 0.13f, 0.015f, C.TELA, lerp(C.TELA, C.BARRO, 0.1f))
                cilindro(v, c + V3(-0.25f, 0f, 0.2f), 0.035f, 0.1f, lerp(C.VIDRIO, Color.White, 0.3f), lerp(C.VIDRIO, C.CIAN, 0.2f))
            }
        }
        lamparas(v)
    }

    /** Las lámparas bajas del Consejo: tres pantallas de metal sobre la mesa, colgadas de cables que suben hasta perderse. */
    private fun DrawScope.lamparas(v: VistaCentro) {
        val y = ESTRADO + 2.0f
        for (z in listOf(-1.3f, 0.4f, 2.1f)) {
            val c = V3(CABECERA_X, y, z)
            linea(v, c, c + V3(0f, 6.5f, 0f), lerp(C.ACERO, C.NEGRO, 0.3f), V3(-1f, 0f, 0f), 0.5f, 0.8f)
            cilindro(v, c - V3(0f, 0.26f, 0f), 0.3f, 0.26f, lerp(C.ACERO, C.CIAN, 0.15f), null, rArriba = 0.08f)
            if (v.consejo > 0.01f) {
                val q = CamCentro.p(c - V3(0f, 0.27f, 0f)); val k = CamCentro.k(c)
                drawOval(lerp(C.LAMPARA, Color.White, 0.5f), q - Offset(0.26f * k, 0.03f * k), androidx.compose.ui.geometry.Size(0.52f * k, 0.07f * k))
                mancha(q + Offset(0f, 0.4f * k), 1.1f * k, 1.0f * k, C.LAMPARA, 0.3f * v.consejo)
                // El charco cálido en el tablero, debajo.
                val t = CamCentro.p(V3(CABECERA_X, ESTRADO + ALTO_MESA + 0.01f, z)); mancha(t, 0.75f * k, 0.22f * k, lerp(C.LAMPARA, Color.White, 0.2f), 0.55f * v.consejo)
            }
        }
    }

    // ---------------------------------------------------------------- Los días

    /** Las jornadas: delante del estrado, sillas en filas mirando a la cabecera, y un atril en el estrado. */
    private fun DrawScope.jornadas(v: VistaCentro) {
        val u = V3(0f, 0f, 1f)
        for (x in listOf(6.6f, 5.9f, 5.2f)) for (i in 0 until 7) {
            val z = -1.8f + i * 0.62f
            if (sqrt(x * x + z * z) > RW - 0.5f) continue
            caja(v, V3(x, 0f, z), u, 0.42f, 0.4f, 0.44f, lerp(C.CIAN, C.ACERO, 0.4f))
            caja(v, V3(x - 0.2f, 0.44f, z), u, 0.42f, 0.04f, 0.42f, lerp(C.CIAN, C.ACERO, 0.5f))
        }
        caja(v, V3(EX0 + 0.6f, ESTRADO, 0.5f), V3(0f, 0f, 1f), 0.5f, 0.4f, 1.1f, lerp(C.MADERA, C.NEGRO, 0.2f))
    }

    /** El censo: la mesa del censo con sus listas, y la cinta de la cola, que da la vuelta al comedor entre postes. */
    private fun DrawScope.censo(v: VistaCentro) {
        val o = V3(-2.8f, 0f, -0.2f); val u = V3(0f, 0f, 1f)
        caja(v, o, u, 2.0f, 0.8f, ALTO_MESA, lerp(C.MADERA, C.HORMIGON, 0.2f), tapa = lerp(C.MADERA, C.TELA, 0.3f))
        for (i in 0..3) caja(v, o + V3(0f, ALTO_MESA, -0.7f + i * 0.45f), u, 0.3f, 0.22f, 0.02f + 0.03f * (i % 2), C.TELA)
        // Los postes y la cinta, por fuera de las mesas.
        val postes = (0..26).map { i -> GeoCentro.aro(7.1f, 0f, 20f + i * 7f) } + (0..10).map { i -> GeoCentro.aro(5.2f, 0f, 195f - i * 9f) }
        for (p in postes) { if (!CamCentro.enCuadro(CamCentro.p(p))) continue; caja(v, p, V3(1f, 0f, 0f), 0.1f, 0.1f, 0.95f, lerp(C.ACERO, C.LATON, 0.5f)); cilindro(v, p, 0.2f, 0.05f, lerp(C.ACERO, C.NEGRO, 0.3f), null) }
        trazo(v, postes.take(27).map { it + V3(0f, 0.85f, 0f) }, C.AMARILLO, ARRIBA, 0.09f, 0.95f)
        trazo(v, postes.drop(27).map { it + V3(0f, 0.85f, 0f) }, C.AMARILLO, ARRIBA, 0.09f, 0.95f)
        // Las listas largas que cuelgan de la mesa del censo, y la caja de las tarjetas.
        caja(v, o + V3(0f, ALTO_MESA, 0.6f), u, 0.4f, 0.3f, 0.18f, C.MADERA)
        trazo(v, listOf(o + V3(0.4f, ALTO_MESA, -0.4f), o + V3(0.45f, 0.4f, -0.45f), o + V3(0.5f, 0.02f, -0.3f)), C.TELA, V3(1f, 0.3f, 0f), 0.12f, 0.95f)
    }

}
