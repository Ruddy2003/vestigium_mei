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
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaBotanico.Costa
import io.github.ruddy2003.vestigium.ui.escenas.EscenaBotanico.Velas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.agua
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.estela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nube
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * El bergantín: el cuadro principal de Botánico (`composicion.md` de la tanda S13, cuadro A). De pie en la popa, junto
 * al timón, mirando hacia proa por la banda de estribor: la cubierta se aleja hacia el centro izquierda y el mar queda a
 * la derecha. Visto a lo largo, no de través, para no repetir el cuadro de Pirata.
 *
 * **La perspectiva.** Una cámara de agujero (la de Mongol: [Mirada], [Sitio]) con los ojos a [OJO] metros sobre la
 * cubierta y el eje del barco hacia el punto de fuga ([FUGA], [H]). Las medidas del barco van en metros desde los
 * ojos: x a estribor, z hacia proa ([pc], [sitio]). Un bergantín de 27 metros **[Aproximación]**: crujía a 2.3 m a
 * babor de los ojos, media manga de 3.8 m, la amurada de estribor a 1.5 m a la derecha; el palo mayor a 11.5 m, el
 * trinquete a 20 m, la roda a 26.5 m.
 *
 * Los planos, de atrás adelante (cada uno, una función; el agente de este cuadro los rellena):
 * - **El mundo** ([mundo]): el cielo, la costa del tramo ([costa]) y el mar ([mar]). Con tormenta se inclina entero.
 * - **El barco** ([barco]): la cubierta y las dos amuradas, la jarcia lejana, y la fila de crujía de proa a popa (el
 *   trinquete, los botes, el palo mayor con su cabillero, la claraboya, el cañón, la bitácora y la rueda).
 * - **El primer plano** ([primerPlano]): el taller del naturalista. La mesa con el objeto del día, la prensa, las cajas
 *   de frascos, los cajones trincados a la amurada (cuántos, el arco `cajones`) y el farol de popa.
 * - **El marco** ([marco]): la vela de popa con su botavara (arriba a la izquierda), la vela mayor y su escota con sus
 *   motones (que cruza el cielo hasta la cubierta), los obenques del palo mayor y el empavesado de las fiestas.
 */
internal object BarcoBotanico {
    private val C = ColorBotanico

    // ---------------------------------------------------------------- La cámara

    /** El horizonte y el punto de fuga del eje del barco. */
    const val H = 112f
    const val FUGA = 150f
    const val F = 300f
    /** La altura de los ojos sobre la cubierta, en metros. */
    const val OJO = 1.65f
    val MIRADA = Mirada(H, F, FUGA)

    /** Un punto del barco (x a estribor, [h] sobre la cubierta, z hacia proa, en metros desde los ojos) en el lienzo. */
    fun pc(x: Float, h: Float, z: Float): Offset { val zz = z.coerceAtLeast(0.2f); return Offset(FUGA + F * x / zz, H + F * (OJO - h) / zz) }

    /** Cuántas unidades del lienzo mide un metro a [z] metros. */
    fun k(z: Float): Float = F / z.coerceAtLeast(0.2f)

    /** El sitio de una cosa que pisa la cubierta en ([x], [z]), girada [giro] radianes. */
    fun sitio(x: Float, z: Float, giro: Float = 0f): Sitio = Sitio(MIRADA, pc(x, 0f, z), k(z), giro)

    // ---------------------------------------------------------------- El barco

    /** La crujía, a babor de los ojos. */
    const val EJE = -2.3f
    const val MEDIA_MANGA = 3.8f
    /** La altura de la regala (el canto de arriba de la amurada). */
    const val REGALA = 1.05f
    const val PALO_MAYOR = 11.5f
    const val TRINQUETE = 20f
    const val RODA = 26.5f

    /** La media manga de la cubierta a [z] metros: igual hasta los 15 m y, hacia la proa, se cierra en la roda. */
    fun media(z: Float): Float = if (z <= 15f) MEDIA_MANGA else MEDIA_MANGA * sqrt((1f - ((z - 15f) / (RODA - 15f)).let { it * it }).coerceAtLeast(0f))
    fun estribor(z: Float) = EJE + media(z)
    fun babor(z: Float) = EJE - media(z)

    // ---------------------------------------------------------------- Dónde está cada cosa

    val SITIO_TIMON = sitio(EJE, 4.4f, 0.32f)
    val SITIO_BITACORA = sitio(-0.85f, 6.1f, 0.28f)
    val SITIO_CLARABOYA = sitio(EJE + 0.15f, 6.9f, 0.06f)
    val SITIO_CANON = sitio(0.18f, 7.4f, -0.04f)
    val SITIO_PALO = sitio(EJE, PALO_MAYOR, 0f)
    val SITIO_TRINQUETE = sitio(EJE, TRINQUETE, 0f)
    val SITIO_BOTE = sitio(EJE, 15.6f, 0.02f)
    val SITIO_MESA = sitio(-0.3f, 4.1f, 0.34f)
    val SITIO_FRASCOS = sitio(0.3f, 3.55f, -0.25f)

    /** La cuba del agua, en la esquina de la aleta, trincada a la amurada: cortada por los bordes de abajo y de la derecha. */
    val SITIO_CUBA = sitio(1.16f, 2.2f, 0f)

    /** El farol de popa, sobre la regala de la aleta de estribor. */
    val FAROL: Offset get() = pc(1.66f, 1.42f, 3.3f)

    /** El suelo del gato: la cubierta de delante, de la rueda a los cajones. */
    const val PISO_GATO = 277f
    const val GATO_DESDE = 30f
    const val GATO_HASTA = 184f

    /**
     * Los huecos para los cajones, de abajo arriba y de popa a proa, contra la amurada de estribor: (x, z, piso, giro).
     * Se llenan en este orden; los primeros quedan cortados por el borde de abajo.
     */
    private val HUECOS = listOf(
        floatArrayOf(1.06f, 3.4f, 0f, 0.05f), floatArrayOf(1.08f, 2.65f, 0f, -0.04f), floatArrayOf(1.06f, 2.65f, 1f, 0.07f),
        floatArrayOf(1.05f, 4.15f, 0f, -0.06f), floatArrayOf(1.07f, 3.4f, 1f, -0.05f), floatArrayOf(1.04f, 2.65f, 2f, -0.1f),
        floatArrayOf(1.06f, 4.9f, 0f, 0.04f), floatArrayOf(1.05f, 4.15f, 1f, 0.08f), floatArrayOf(1.06f, 3.4f, 2f, 0.11f),
    )
    private const val ALTO_CAJON = 0.46f

    /** Los cajones que hay hoy en cubierta: tantos como diga [EscenaBotanico.cuantosCajones] (lo vigila una prueba). */
    fun cajonesEnCubierta(m: Momento): Int = EscenaBotanico.cuantosCajones(m).coerceIn(1, HUECOS.size)

    /** Lo alto de la pila de cajones de popa, donde el gato se sienta a mirar (y en el lienzo). */
    fun miradorDeLosCajones(m: Momento): Offset {
        val pila = (0 until cajonesEnCubierta(m)).map { HUECOS[it] }.filter { it[1] == 3.4f }
        return pc(0.95f, ALTO_CAJON * pila.size, 3.4f)
    }

    /**
     * Dónde va el objeto chico del día: sobre la mesa de trabajo (en los lugares sin cuadro propio, que van sin mesa,
     * sobre un cajón de soporte en su sitio). Y dónde pisa y dónde se apoya el grande: delante de la mesa, recostado en su canto.
     */
    fun soporte(m: Momento): Offset = if (EscenaBotanico.mesaDeCubierta(m) != null) mesa(m).tablero else SITIO_MESA.p(0f, ALTO_CAJON + 0.02f, 0f)
    val PIE_GRANDE: Offset get() = pc(0.1f, 0f, 3.6f)
    fun apoyoGrande(m: Momento): Offset = if (EscenaBotanico.mesaDeCubierta(m) != null) SITIO_MESA.p(0.58f, 0.8f, -0.3f) else SITIO_MESA.p(0.4f, ALTO_CAJON, -0.26f)

    private fun mesa(m: Momento): PiezasBotanico.Mesa = PiezasBotanico.Mesa(SITIO_MESA, Semilla.de("botanico", "mesa", "cubierta"), PiezasBotanico.TipoMesa.CUBIERTA,
        tapada = EscenaBotanico.mesaDeCubierta(m) == EscenaBotanico.Mesa.TAPADA, nieve = EscenaBotanico.nieve(m))

    fun seMueve(m: Momento): Boolean = true                                    // el mar siempre brilla

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val l = EscenaBotanico.luces(m); val r = EscenaBotanico.rayo(l); val sol = Sol3(r); val p = Pincel(l, r)
        mundo(m, p, sol)
        barco(m, p, sol)
        primerPlano(m, p, sol)
        marco(m, p, sol)
        tiempoQuieto(l, m.ficha.clima, H, m.semilla("tiempo"))
        grano(Azar(Semilla.de("botanico", "grano", "barco")), 0.6f + 0.4f * r.dia)
    }

    /** Las luces cálidas encendidas a esta hora: la claraboya, la lámpara de la bitácora y el farol de popa (de 0 a 1 cada una). */
    private class Lumbres(val claraboya: Float, val bitacora: Float, val farol: Float)

    private fun lumbres(m: Momento, l: Luces): Lumbres = Lumbres(
        claraboya = EscenaBotanico.luzDeLaCabina(m) * l.noche,
        bitacora = if (EscenaBotanico.lamparaDeLaBitacora(m)) l.noche else 0f,
        farol = if (EscenaBotanico.farolDePopa(m)) l.noche else 0f,
    )

    /**
     * El pincel de una cosa que está en [en] (en el lienzo): de noche, si una lumbre la alumbra, con su calor y con la
     * sombra tumbada lejos de ella (la cara que la mira, cálida; la otra, del azul de la luna); si no, el de la luna o el sol.
     */
    private fun pincelEn(m: Momento, p: Pincel, sol: Sol3, en: Offset): Pair<Pincel, Sol3> {
        val lu = lumbres(m, p.l); if (p.l.noche < 0.3f) return p to sol
        val fuentes = listOf(SITIO_CLARABOYA.p(0f, 0.8f, 0f) to lu.claraboya * 0.9f, SITIO_BITACORA.p(0f, 1.15f, -0.2f) to lu.bitacora * 0.7f, FAROL to lu.farol)
        var mejor: Pair<Offset, Float>? = null; var calor = 0f
        for ((q, f) in fuentes) { if (f <= 0.02f) continue; val d = (q - en).getDistance(); val k = f / (1f + (d / 70f) * (d / 70f)); calor += k; if (mejor == null || k > mejor.second) mejor = q to k }
        if (mejor == null || calor < 0.08f) return p to sol
        val foco = io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco(mejor.first, C.LUMBRE, 1f, 70f, suelo = en.y)
        val rr = io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco(foco, en, 0.4f, C.SOMBRA)
        return Pincel(p.l, rr, calor.coerceIn(0f, 0.85f), C.LUMBRE) to Sol3(rr)
    }

    // ================================================================ El mundo: el cielo, la costa y el mar

    /** El cielo, la costa y el mar; con tormenta, inclinados unos grados alrededor del horizonte (se mueve el mundo, no el barco). */
    private fun DrawScope.mundo(m: Momento, p: Pincel, sol: Sol3) {
        val g = EscenaBotanico.inclinacion(m)
        if (g == 0f) { cieloYMar(m, p, sol); return }
        drawRect(p.l.alto, Offset.Zero, Size(ANCHO, ALTO))
        withTransform({ rotate(g, Offset(ANCHO / 2f, H)); scale(1.18f, 1.18f, Offset(ANCHO / 2f, H)) }) { cieloYMar(m, p, sol) }
    }

    private fun DrawScope.cieloYMar(m: Momento, p: Pincel, sol: Sol3) {
        val l = p.l; val semilla = m.ficha.semilla
        cielo(l, H, semilla)
        astros(l, H, semilla)
        nubes(l, H, m.ficha.clima, semilla)
        costa(m, p, sol)
        mar(m, p, sol)
    }

    /** Los tres colores del agua de cada costa (el propio, el hondo y el de la orilla): con la costa cambia el agua. */
    internal fun aguaDe(costa: Costa): Triple<Color, Color, Color> = when (costa) {
        Costa.MAR_ABIERTO -> Triple(lerp(C.MAR, C.MAR_HONDO, 0.45f), lerp(C.MAR_HONDO, Color(0xFF173A58), 0.45f), lerp(C.MAR, C.ORILLA, 0.2f))
        Costa.TROPICAL, Costa.VOLCANICA -> Triple(C.MAR, C.MAR_HONDO, C.ORILLA)
        Costa.ATOLON -> Triple(lerp(C.MAR, C.ORILLA, 0.25f), C.MAR, lerp(C.ORILLA, C.ARENA, 0.2f))
        Costa.PATAGONIA -> Triple(lerp(C.MAR, C.ARENA, 0.35f), lerp(C.MAR_HONDO, C.ROCA, 0.3f), lerp(C.ARENA, C.CUERO, 0.2f))
        Costa.FUEGO -> Triple(lerp(C.MAR_HONDO, C.ROCA, 0.3f), lerp(C.MAR_HONDO, C.BREA, 0.4f), lerp(C.MAR, C.ROCA, 0.4f))
        Costa.ANDES -> Triple(lerp(C.MAR, C.MAR_HONDO, 0.35f), C.MAR_HONDO, lerp(C.MAR, C.ROCA, 0.3f))
        Costa.COLINAS -> Triple(lerp(C.MAR, C.MAR_HONDO, 0.2f), C.MAR_HONDO, C.ORILLA)
        Costa.INGLESA -> Triple(lerp(C.MAR, C.ROCA, 0.35f), lerp(C.MAR_HONDO, C.ROCA, 0.3f), lerp(C.MAR, C.ROCA, 0.4f))
    }

    /**
     * El mar de estribor y el que asoma por babor: hondo lejos, claro cerca, del color de la costa; la mar de fondo en
     * lomos, con borreguillos si sopla; la estela del sol o la luna; y la espuma que levanta el barco al andar.
     */
    private fun DrawScope.mar(m: Momento, p: Pincel, sol: Sol3) {
        val l = p.l; val (propio, hondo, orilla) = aguaDe(EscenaBotanico.costa(m))
        val marea = EscenaBotanico.marejada(m)
        val zona = Rect(-80f, H, ANCHO + 80f, ALTO + 40f)
        agua(l, zona, m.azar("mar"), propio = propio, viento = (0.15f + 0.85f * marea), hondo = hondo, orilla = orilla)
        costaEnElAgua(m, p)
        // La mar de fondo: lomos tendidos con su cara de sombra y su cresta de luz, más altos con viento.
        val az = m.azar("oleaje")
        val oscuro = lerp(l.tono(hondo), Color(0xFF0C2238), 0.25f); val claro = lerp(l.tono(orilla), Color.White, 0.3f)
        repeat((10 + 16 * marea).toInt()) {
            val kk = az.real(); val prof = kk * kk; val y = H + 6f + 150f * prof
            val w = (24f + 110f * prof) * az.entre(0.7f, 1.3f); val alto = (0.6f + 6f * prof) * (0.4f + 1.4f * marea); val x = az.entre(-40f, ANCHO)
            drawPath(Path().apply { moveTo(x, y); cubicTo(x + w * 0.3f, y - alto, x + w * 0.62f, y - alto * 1.1f, x + w, y); cubicTo(x + w * 0.7f, y + alto * 0.5f, x + w * 0.3f, y + alto * 0.5f, x, y); close() }, oscuro.copy(alpha = 0.14f + 0.16f * marea))
            drawPath(Path().apply { moveTo(x + w * 0.08f, y - alto * 0.24f); cubicTo(x + w * 0.32f, y - alto * 0.98f, x + w * 0.6f, y - alto * 1.06f, x + w * 0.92f, y - alto * 0.2f) }, claro.copy(alpha = (0.18f + 0.22f * marea) * (1f - 0.5f * l.noche)), style = Stroke(0.5f + 1.1f * prof, cap = StrokeCap.Round))
            if (marea > 0.5f && az.real() > 0.3f) { val cx = x + w * az.entre(0.3f, 0.6f); drawPath(Path().apply { moveTo(cx - w * 0.14f, y - alto * 0.86f); cubicTo(cx - w * 0.04f, y - alto * 1.5f, cx + w * 0.06f, y - alto * 1.4f, cx + w * 0.16f, y - alto * 0.8f); close() }, l.tono(Color.White).copy(alpha = 0.7f)) }
        }
        if (EscenaBotanico.tormenta(m)) olasGrandes(m, p, hondo, orilla)
        estela(l, zona, m.azar("estela"))
        // La espuma de la marcha: el barco abre el agua por la amura y la deja correr por el costado hacia popa.
        if (!EscenaBotanico.fondeado(m) && !EscenaBotanico.calma(m)) {
            val ez = Azar(Semilla.de("botanico", "espuma"))
            val viva = 0.5f + 0.5f * marea
            repeat(70) {
                val z = ez.entre(3f, 26f); val q = pc(estribor(z) + 0.5f + ez.entre(0f, 1.6f) * (1f + (26f - z) / 10f), -2.4f, z)
                val w = ez.entre(4f, 14f) * (k(z) / 20f).coerceIn(0.3f, 2f)
                drawPath(Path().apply { moveTo(q); cubicTo(q + Offset(w * 0.3f, -0.8f), q + Offset(w * 0.7f, 0.8f), q + Offset(w, 0.2f)) }, l.tono(Color.White).copy(alpha = ez.entre(0.25f, 0.7f) * viva * (1f - 0.4f * l.noche)), style = Stroke(ez.entre(0.5f, 1.4f) * (k(z) / 30f).coerceIn(0.6f, 2f), cap = StrokeCap.Round))
            }
        }
        // De noche, el farol de popa y la claraboya en el agua de al lado.
        val lu = lumbres(m, l)
        if (lu.farol > 0.05f) reflejoRoto(FAROL.x - 6f, FAROL.x + 6f, 230f, 50f, C.LUMBRE, 0.35f * lu.farol, Azar(Semilla.de("botanico", "reflejo farol")), 1.2f)
    }

    /** Con tormenta, la mar gruesa: olas grandes que se levantan a estribor, con la cresta rota en espuma y los regueros por el lomo. */
    private fun DrawScope.olasGrandes(m: Momento, p: Pincel, hondo: Color, orilla: Color) {
        val l = p.l; val az = m.azar("olas grandes")
        val oscuro = lerp(l.tono(hondo), Color(0xFF0C2238), 0.4f); val verde = l.tono(lerp(hondo, orilla, 0.45f)); val blanco = l.tono(Color.White)
        for ((x, y, w, alto) in listOf(listOf(150f, 128f, 120f, 9f), listOf(250f, 140f, 160f, 14f), listOf(330f, 126f, 110f, 8f), listOf(200f, 160f, 190f, 20f), listOf(310f, 176f, 170f, 24f))) {
            val ola = Path().apply { moveTo(x, y); cubicTo(x + w * 0.24f, y - alto * 0.6f, x + w * 0.4f, y - alto * 1.1f, x + w * 0.56f, y - alto); cubicTo(x + w * 0.68f, y - alto * 0.86f, x + w * 0.84f, y - alto * 0.3f, x + w, y); cubicTo(x + w * 0.7f, y + alto * 0.5f, x + w * 0.3f, y + alto * 0.5f, x, y); close() }
            drawPath(ola, Brush.verticalGradient(listOf(verde, oscuro.copy(alpha = 0.85f), oscuro.copy(alpha = 0f)), y - alto, y + alto * 0.5f))
            drawPath(Path().apply { moveTo(x + w * 0.3f, y - alto * 0.8f); cubicTo(x + w * 0.42f, y - alto * 1.34f, x + w * 0.54f, y - alto * 1.24f, x + w * 0.7f, y - alto * 0.74f); cubicTo(x + w * 0.58f, y - alto * 0.86f, x + w * 0.44f, y - alto * 0.9f, x + w * 0.3f, y - alto * 0.8f); close() }, blanco.copy(alpha = 0.85f))
            repeat(7) { val k = az.entre(0.2f, 0.8f); val px = x + w * k; val py = y - alto * (1f - abs(k - 0.52f) * 1.9f).coerceAtLeast(0.1f) * 0.8f; drawPath(Path().apply { moveTo(px, py); cubicTo(Offset(px - 4f, py + 3f), Offset(px - 9f, py + 5f), Offset(px - az.entre(10f, 22f), py + az.entre(5f, 9f))) }, blanco.copy(alpha = 0.4f), style = Stroke(0.7f, cap = StrokeCap.Round)) }
            // El viento arranca la espuma de la cresta.
            repeat(5) { mancha(Offset(x + w * az.entre(0.4f, 0.7f) - az.entre(0f, 14f), y - alto * az.entre(1.1f, 1.6f)), az.entre(4f, 10f), az.entre(1f, 2.4f), blanco, 0.35f) }
        }
    }

    // ================================================================ La costa del tramo

    /**
     * La costa que se ve hoy en el horizonte, sobre todo a estribor ([EscenaBotanico.costa]). Lo propio de cada tramo
     * (dónde caen los montes, el pueblo) sale de [EscenaBotanico.aguas]: igual todos los días del tramo. Nada sube por
     * encima de y = 110 entre x = 270 y 330: ahí salen el sol de la mañana y la luna.
     */
    private fun DrawScope.costa(m: Momento, p: Pincel, sol: Sol3) {
        val az = EscenaBotanico.aguas(m, "costa")
        when (EscenaBotanico.costa(m)) {
            Costa.MAR_ABIERTO -> marAbierto(m, p, az)
            Costa.TROPICAL -> tropical(m, p, az)
            Costa.VOLCANICA -> volcanica(m, p, az)
            Costa.PATAGONIA -> patagonia(m, p, az)
            Costa.FUEGO -> fuego(m, p, az)
            Costa.ANDES -> andes(m, p, az)
            Costa.ATOLON -> atolon(m, p, az)
            Costa.COLINAS -> colinas(m, p, az)
            Costa.INGLESA -> inglesa(m, p, az)
        }
    }

    /** Un perfil de sierra que no sube en la ventana de los astros (x de 266 a 334): ahí se queda a ras del horizonte. */
    private fun sinTaparAstros(pts: List<Offset>): List<Offset> = pts.map { q -> if (q.x in 262f..338f) Offset(q.x, maxOf(q.y, H - 1.6f)) else q }

    /** Un morro de granito, como los de Río: casi vertical, liso, con sus regueros oscuros y una falda de selva. */
    private fun DrawScope.morro(p: Pincel, cx: Float, ancho: Float, alto: Float, sesgo: Float, lejos: Float, az: Azar) {
        val base = H + 1f; val l = p.l
        val pts = listOf(Offset(cx - ancho / 2f, base), Offset(cx - ancho * 0.42f, base - alto * 0.35f), Offset(cx - ancho * 0.3f + sesgo, base - alto * 0.85f), Offset(cx - ancho * 0.08f + sesgo, base - alto),
            Offset(cx + ancho * 0.12f + sesgo, base - alto * 0.96f), Offset(cx + ancho * 0.3f + sesgo, base - alto * 0.7f), Offset(cx + ancho * 0.42f, base - alto * 0.3f), Offset(cx + ancho / 2f, base))
        val forma = curva(pts, base + 1f)
        val roca = lerp(C.ROCA, C.TEJA, 0.12f)
        val lado = if (p.solALaDerecha) 1f else -1f
        drawPath(forma, Brush.horizontalGradient(listOf(l.tono(lerp(roca, Color(0xFF4A4C7A), 0.35f), lejos), l.tono(roca, lejos), l.tono(lerp(roca, Color(0xFFFFE8C0), 0.35f * p.r.dia), lejos)), cx - lado * ancho / 2f, cx + lado * ancho / 2f))
        clipPath(forma) {
            repeat(9) { val x = cx + az.entre(-0.4f, 0.4f) * ancho; drawLine(l.tono(lerp(roca, C.BREA, 0.4f), lejos).copy(alpha = 0.35f), Offset(x, base - alto * az.entre(0.5f, 0.95f)), Offset(x + az.entre(-1f, 1f), base - alto * 0.1f), az.entre(0.4f, 1.1f)) }
            // La falda de selva, de borde rizado, que sube más por las quebradas.
            val falda = (0..16).map { val x = cx - ancho / 2f + ancho * it / 16f; Offset(x, base - alto * (0.18f + 0.16f * az.real() + 0.1f * sin(it * 1.3f))) }
            drawPath(curva(falda, base + 2f), l.tono(lerp(C.SELVA, C.HOJA, 0.15f), lejos))
            drawPath(curva(falda.map { it + Offset(lado * 1.2f, -0.6f) }), l.tono(lerp(C.HOJA, C.ARENA, 0.2f), lejos).copy(alpha = 0.5f * p.r.dia), style = Stroke(1.2f))
        }
    }

    /** Un pueblo en la orilla: casas blancas de tejado rojo en dos o tres filas que suben, cada una con su lado de sombra, y una iglesia. */
    private fun DrawScope.pueblo(p: Pincel, x0: Float, x1: Float, base: Float, alto: Float, lejos: Float, az: Azar, torres: Int = 2, gris: Boolean = false, luces: Boolean = false) {
        val l = p.l; val lado = if (p.solALaDerecha) 1f else -1f
        val pared = if (gris) lerp(C.ROCA, C.LONA, 0.45f) else C.CAL; val tejado = if (gris) lerp(C.ROCA, C.BREA, 0.3f) else C.TEJA
        for (fila in 2 downTo 0) {
            var x = x0 + az.entre(0f, 3f) + fila * 2f
            val y = base - fila * alto * 0.55f
            while (x < x1 - fila * 4f) {
                val w = alto * az.entre(0.6f, 1.1f); val h = alto * az.entre(0.45f, 0.75f)
                drawRect(l.tono(pared, lejos), Offset(x, y - h), Size(w, h))
                drawRect(l.tono(mult(pared, Color(0xFF8688C6), 0.6f), lejos), Offset(if (lado > 0f) x else x + w * 0.65f, y - h), Size(w * 0.35f, h))
                drawPath(poligono(Offset(x - w * 0.08f, y - h), Offset(x + w * 1.08f, y - h), Offset(x + w * 0.5f, y - h - alto * 0.3f)), l.tono(tejado, lejos))
                if (luces && l.noche > 0.4f && az.real() > 0.55f) drawRect(C.LUMBRE.copy(alpha = 0.8f * l.noche), Offset(x + w * 0.35f, y - h * 0.6f), Size(maxOf(0.6f, w * 0.18f), maxOf(0.6f, h * 0.25f)))
                x += w * az.entre(1.1f, 1.6f)
            }
        }
        repeat(torres) { val x = x0 + (x1 - x0) * (0.3f + 0.35f * it); val y = base - alto * 0.9f
            drawRect(l.tono(pared, lejos), Offset(x, y - alto * 1.6f), Size(alto * 0.5f, alto * 1.6f)); drawPath(poligono(Offset(x - alto * 0.05f, y - alto * 1.6f), Offset(x + alto * 0.55f, y - alto * 1.6f), Offset(x + alto * 0.25f, y - alto * 2.3f)), l.tono(tejado, lejos)) }
    }

    /** La raya clara de la playa y la espuma al pie de la tierra, de [x0] a [x1]. */
    private fun DrawScope.orillaLejana(p: Pincel, x0: Float, x1: Float, az: Azar, arena: Color = C.ARENA, lejos: Float = 0.6f) {
        val l = p.l; var x = x0
        while (x < x1) { val w = az.entre(6f, 22f); if (az.real() > 0.25f) drawLine(l.tono(arena, lejos), Offset(x, H + 1f), Offset(minOf(x + w, x1), H + 1.2f), 1f, StrokeCap.Round); x += w + az.entre(2f, 8f) }
        repeat(((x1 - x0) / 10f).toInt()) { val px = az.entre(x0, x1); drawLine(l.tono(Color.White, lejos).copy(alpha = az.entre(0.3f, 0.7f) * (1f - 0.5f * l.noche)), Offset(px, H + 2.2f), Offset(px + az.entre(3f, 8f), H + 2.3f), 0.6f, StrokeCap.Round) }
    }

    private fun DrawScope.marAbierto(m: Momento, p: Pincel, az: Azar) {
        // Solo mar; a veces, un chubasco lejano que cuelga de una nube sobre el horizonte.
        if (az.real() > 0.5f && m.ficha.clima != Clima.TORMENTA) {
            val x = az.entre(40f, 230f)
            nube(p.l, x, H - 3f, az.entre(40f, 70f), az, carga = 0.4f, lejos = 0.75f, alza = 0.8f)
            for (k in 0..10) drawLine(p.l.tono(lerp(C.ROCA, C.LONA, 0.3f), 0.8f).copy(alpha = 0.18f), Offset(x + 6f + k * 4f, H - 5f), Offset(x + 2f + k * 4f, H), 1.2f)
        }
    }

    private fun DrawScope.tropical(m: Momento, p: Pincel, az: Azar) {
        val l = p.l
        // Detrás, la sierra azul con su selva hasta el cielo; delante, los morros de piedra y las lomas verdes; abajo, la playa.
        monte(l, sinTaparAstros(perfil(Azar(Semilla.de("botanico", "sierra tropical", az.entero(1000))), 120f, ANCHO + 30f, H + 1f, 34f, 0.55f, 30)), H + 2f, lerp(C.SELVA, Color(0xFF7A90A8), 0.55f), 0.86f)
        morro(p, az.entre(196f, 236f), az.entre(30f, 44f), az.entre(36f, 52f), az.entre(-4f, 4f), 0.6f, az)
        morro(p, az.entre(344f, 368f), az.entre(26f, 36f), az.entre(30f, 46f), az.entre(-3f, 3f), 0.55f, az)
        monte(l, sinTaparAstros(perfil(Azar(Semilla.de("botanico", "lomas tropical", az.entero(1000))), 150f, ANCHO + 20f, H + 1f, 12f, 0.35f, 26)), H + 2f, lerp(C.SELVA, C.HOJA, 0.25f), 0.6f)
        if (EscenaBotanico.puerto(m)) pueblo(p, az.entre(250f, 270f), az.entre(318f, 336f), H + 0.5f, 3.2f, 0.55f, az, 2, luces = true)
        orillaLejana(p, 150f, ANCHO, az)
        // Por babor, lejos, otra punta de tierra.
        monte(l, perfil(Azar(Semilla.de("botanico", "babor tropical", az.entero(1000))), -10f, 120f, H + 1f, 14f, 0.5f, 18, isla = true), H + 2f, lerp(C.SELVA, Color(0xFF7A90A8), 0.6f), 0.84f)
    }

    private fun DrawScope.volcanica(m: Momento, p: Pincel, az: Azar) {
        val l = p.l
        // Tahití y Mauricio, verdes; Cabo Verde, Santa Elena y las Galápagos, secas y negras.
        val tramo = EscenaBotanico.tramo(m)
        val verde = EscenaBotanico.costa(m) == Costa.VOLCANICA && EscenaBotanico.lugar(m) == EscenaBotanico.Lugar.BERGANTIN && ((tramo.temporada == 4 && tramo.desde == "11-15") || (tramo.temporada == 5 && tramo.desde == "04-29"))
        val ladera = if (verde) lerp(C.SELVA, C.HOJA, 0.2f) else lerp(C.ROCA, C.CUERO, 0.25f)
        val lava = lerp(C.BREA, C.ROCA, 0.3f)
        // El cono grande, con su cráter mellado, y otro más chico delante.
        val cx = az.entre(196f, 236f); val alto = az.entre(40f, 56f); val ancho = az.entre(120f, 160f)
        val cono = curva(listOf(Offset(cx - ancho / 2f, H + 1f), Offset(cx - ancho * 0.2f, H - alto * 0.55f), Offset(cx - ancho * 0.07f, H - alto), Offset(cx - ancho * 0.02f, H - alto * 0.9f), Offset(cx + ancho * 0.06f, H - alto * 0.97f), Offset(cx + ancho * 0.22f, H - alto * 0.5f), Offset(minOf(cx + ancho / 2f, 262f), H - 2f), Offset(minOf(cx + ancho * 0.7f, 266f), H + 1f)), H + 2f)
        monte(l, listOf(Offset(cx - ancho / 2f, H + 1f), Offset(cx - ancho * 0.2f, H - alto * 0.55f), Offset(cx - ancho * 0.07f, H - alto), Offset(cx + ancho * 0.06f, H - alto * 0.97f), Offset(cx + ancho * 0.22f, H - alto * 0.5f), Offset(minOf(cx + ancho / 2f, 262f), H - 2f), Offset(minOf(cx + ancho * 0.7f, 266f), H + 1f)), H + 2f, ladera, 0.62f)
        clipPath(cono) {
            // Las coladas de lava: lenguas oscuras que bajan del cráter.
            repeat(5) { val x = cx + az.entre(-0.12f, 0.12f) * ancho; drawPath(Path().apply { moveTo(x, H - alto * 0.9f); cubicTo(x + az.entre(-8f, 8f), H - alto * 0.5f, x + az.entre(-14f, 14f), H - alto * 0.2f, x + az.entre(-20f, 20f), H + 1f) }, l.tono(lava, 0.6f).copy(alpha = 0.5f), style = Stroke(az.entre(1.5f, 4f))) }
        }
        monte(l, perfil(Azar(Semilla.de("botanico", "cono chico", az.entero(1000))), 330f, ANCHO + 30f, H + 1f, 22f, 0.5f, 16, isla = true), H + 2f, lerp(ladera, lava, 0.3f), 0.58f)
        // El acantilado de la orilla: roca negra con la capa blanca de conchas en medio.
        val x0 = cx - ancho * 0.45f; val x1 = minOf(cx + ancho * 0.6f, 262f)
        drawRect(l.tono(lava, 0.55f), Offset(x0, H - 4f), Size(x1 - x0, 5f))
        drawRect(l.tono(C.LONA, 0.55f), Offset(x0, H - 2.4f), Size(x1 - x0, 1.1f))
        if (EscenaBotanico.puerto(m)) pueblo(p, x0 + 10f, x0 + 50f, H - 4f, 2.6f, 0.55f, az, 1, luces = true)
        orillaLejana(p, x0, ANCHO, az, lerp(C.ARENA, lava, 0.4f))
    }

    private fun DrawScope.patagonia(m: Momento, p: Pincel, az: Azar) {
        val l = p.l
        // La costa baja: la meseta parda, larga, y las barrancas claras con sus capas, que caen al agua.
        val alto = az.entre(5f, 9f)
        val meseta = (0..30).map { val x = 120f + (ANCHO + 40f - 120f) * it / 30f; Offset(x, H - alto * (0.7f + 0.3f * sin(it * 0.6f + az.real())) - (if (x in 262f..338f) 0f else 0f)) }
        monte(l, meseta, H + 2f, lerp(C.ARENA, C.CUERO, 0.35f), 0.62f)
        // La barranca: un frente vertical claro con sus rayas.
        drawRect(l.tono(lerp(C.ARENA, C.LONA, 0.3f), 0.58f), Offset(120f, H - alto * 0.55f), Size(ANCHO - 100f, alto * 0.55f + 1f))
        for (k in 0..2) drawLine(l.tono(lerp(C.ARENA, C.CUERO, 0.5f), 0.58f).copy(alpha = 0.6f), Offset(120f, H - alto * (0.15f + 0.15f * k)), Offset(ANCHO + 20f, H - alto * (0.12f + 0.15f * k)), 0.5f)
        if (az.real() > 0.5f) monte(l, perfil(Azar(Semilla.de("botanico", "cerro", az.entero(1000))), 170f, 240f, H + 1f, 16f, 0.3f, 14, isla = true), H + 2f, lerp(C.ARENA, C.ROCA, 0.4f), 0.66f)
        if (EscenaBotanico.puerto(m)) pueblo(p, az.entre(180f, 210f), az.entre(240f, 262f), H - alto * 0.5f, 2.4f, 0.6f, az, 2, luces = true)
        orillaLejana(p, 120f, ANCHO, az, lerp(C.ARENA, C.CUERO, 0.2f))
    }

    private fun DrawScope.fuego(m: Momento, p: Pincel, az: Azar) {
        val l = p.l
        // Montañas negras y quebradas, con nieve en las cumbres y un glaciar que baja al mar; abajo, el bosque oscuro.
        val sierra = sinTaparAstros(perfil(Azar(Semilla.de("botanico", "fuego sierra", az.entero(1000))), 110f, ANCHO + 30f, H + 1f, 46f, 0.9f, 34))
        monte(l, sierra, H + 2f, lerp(C.BREA, Color(0xFF5A6488), 0.45f), 0.6f)
        clipPath(curva(sierra, H + 2f)) {
            // La nieve: en lo alto de cada pico, en manchas que siguen la cresta.
            for (i in 1 until sierra.size - 1) { val q = sierra[i]; if (q.y < H - 22f) drawPath(poligono(q + Offset(-6f, 0f), q + Offset(0f, -2f), q + Offset(7f, 1f), q + Offset(2f, (H - q.y) * 0.35f), q + Offset(-3f, (H - q.y) * 0.25f)), l.tono(C.NIEVE, 0.5f)) }
            // El bosque oscuro de la falda.
            drawPath(curva((0..20).map { Offset(110f + (ANCHO - 80f) * it / 20f, H - 6f - 4f * az.real()) }, H + 2f), l.tono(C.SELVA_OSC, 0.55f))
        }
        // El glaciar: una lengua blanca y azulada que baja de un valle hasta el agua.
        val gx = az.entre(180f, 236f)
        drawPath(Path().apply { moveTo(gx - 4f, H - 30f); cubicTo(gx - 8f, H - 18f, gx - 14f, H - 6f, gx - 16f, H + 1f); lineTo(gx + 14f, H + 1f); cubicTo(gx + 10f, H - 8f, gx + 6f, H - 20f, gx + 4f, H - 30f); close() }, Brush.horizontalGradient(listOf(l.tono(lerp(C.NIEVE, Color(0xFF8AB4D0), 0.4f), 0.5f), l.tono(C.NIEVE, 0.5f)), gx - 16f, gx + 14f))
        drawRect(l.tono(lerp(C.NIEVE, Color(0xFF8AB4D0), 0.5f), 0.5f), Offset(gx - 16f, H - 1.5f), Size(30f, 2.5f))
        // El día que llegan, un humo en la orilla, de un fuego que no se ve.
        if (m.con("nieve")) humo(l, Offset(az.entre(300f, 330f), H), 30f, 3f, 2f, 0.6f, 0.2f, 0.5f, 3, 6, 0.7f)
        monte(l, perfil(Azar(Semilla.de("botanico", "fuego babor", az.entero(1000))), -10f, 120f, H + 1f, 28f, 0.8f, 18, isla = true), H + 2f, lerp(C.BREA, Color(0xFF6A7494), 0.5f), 0.8f)
        orillaLejana(p, 110f, ANCHO, az, C.ROCA)
    }

    private fun DrawScope.andes(m: Momento, p: Pincel, az: Azar) {
        val l = p.l
        // La cordillera nevada, alta y lejana; el volcán, un cono blanco; delante, los cerros secos de la costa.
        val cordillera = sinTaparAstros(perfil(Azar(Semilla.de("botanico", "cordillera", az.entero(1000))), 100f, ANCHO + 30f, H - 6f, 40f, 0.7f, 34))
        monte(l, cordillera, H + 2f, lerp(C.NIEVE, Color(0xFF8A9CC0), 0.45f), 0.84f)
        val vx = az.entre(196f, 222f); val va = az.entre(46f, 56f)
        val cono = listOf(Offset(vx - 54f, H - 3f), Offset(vx - 30f, H - va * 0.42f), Offset(vx - 13f, H - va * 0.82f), Offset(vx - 5f, H - va), Offset(vx + 3f, H - va * 0.97f), Offset(vx + 6f, H - va), Offset(vx + 15f, H - va * 0.8f), Offset(vx + 28f, H - va * 0.46f), Offset(vx + 44f, H - va * 0.14f), Offset(vx + 52f, H - 3f))
        monte(l, cono, H + 2f, lerp(C.NIEVE, Color(0xFF8A9CC0), 0.25f), 0.74f)
        clipPath(poligono(cono + listOf(Offset(vx + 46f, H), Offset(vx - 48f, H)))) { drawRect(l.tono(lerp(C.ROCA, Color(0xFF6A7494), 0.4f), 0.74f), Offset(vx - 50f, H - va * 0.45f), Size(100f, va * 0.5f)) }
        // El humo del volcán; el día del fuego, la columna y, de noche, el resplandor rojo y la lava.
        val e = EscenaBotanico.erupcion(m)
        if (e > 0f) {
            humo(l, Offset(vx, H - va), 60f, 14f, 2f, 0.3f, 0.6f, 0.4f, 7, 10, 0.5f + 0.5f * e)
            if (l.noche > 0.3f) { resplandor(Offset(vx, H - va), 40f, 0.9f * e * l.noche, Color(0xFFFF6A2A)); drawPath(Path().apply { moveTo(vx - 2f, H - va); cubicTo(vx - 6f, H - va * 0.6f, vx - 10f, H - va * 0.3f, vx - 14f, H - 6f) }, Color(0xFFFF7A30).copy(alpha = 0.8f * e), style = Stroke(1.4f)) }
        } else mancha(Offset(vx + 6f, H - va - 4f), 10f, 3f, l.tono(C.LONA, 0.7f), 0.4f)
        val cerros = sinTaparAstros(perfil(Azar(Semilla.de("botanico", "cerros secos", az.entero(1000))), 120f, ANCHO + 20f, H + 1f, 16f, 0.5f, 26))
        monte(l, cerros, H + 2f, lerp(C.ARENA, C.CUERO, 0.45f), 0.6f)
        if (EscenaBotanico.puerto(m)) pueblo(p, az.entre(170f, 196f), az.entre(236f, 260f), H - 1f, 2.8f, 0.58f, az, 1, luces = true)
        if (EscenaBotanico.ruinas(m)) repeat(8) { mancha(Offset(az.entre(176f, 258f), H - az.entre(2f, 8f)), az.entre(6f, 14f), az.entre(2f, 4f), l.tono(C.ARENA, 0.6f), 0.4f) }
        orillaLejana(p, 120f, ANCHO, az, lerp(C.ARENA, C.ROCA, 0.4f))
    }

    private fun DrawScope.atolon(m: Momento, p: Pincel, az: Azar) {
        val l = p.l
        // Un anillo de coral a flor de agua: la rompiente, la raya de arena y los grupos de palmeras; dentro, la laguna clara.
        val y0 = H + 0.4f
        drawRect(l.tono(lerp(C.ORILLA, C.ARENA, 0.3f), 0.5f), Offset(150f, y0 + 1.5f), Size(ANCHO - 150f, 2.6f))
        var x = 150f
        while (x < ANCHO) {
            val w = az.entre(20f, 60f)
            drawLine(l.tono(C.ARENA, 0.55f), Offset(x, y0), Offset(minOf(x + w, ANCHO), y0 - 0.4f), 1.6f, StrokeCap.Round)
            if (x !in 250f..340f && az.real() > 0.3f) repeat(2 + az.entero(4)) {
                val px = x + az.entre(0f, w); val h = az.entre(4f, 8f)
                drawLine(l.tono(lerp(C.ROCA, C.MADERA, 0.4f), 0.6f), Offset(px, y0), Offset(px + az.entre(-1.5f, 1.5f), y0 - h), 0.6f)
                for (k in 0..5) { val a = -3.0f + k * 0.5f; drawLine(l.tono(C.SELVA, 0.58f), Offset(px, y0 - h), Offset(px + kotlin.math.cos(a) * 3.4f, y0 - h + kotlin.math.sin(a) * 1.6f + 1.4f), 0.8f, StrokeCap.Round) }
            }
            x += w + az.entre(4f, 18f)
        }
        repeat(30) { val px = az.entre(150f, ANCHO); drawLine(l.tono(Color.White, 0.4f).copy(alpha = az.entre(0.4f, 0.8f) * (1f - 0.5f * l.noche)), Offset(px, y0 + 3.4f), Offset(px + az.entre(3f, 10f), y0 + 3.6f), 0.9f, StrokeCap.Round) }
    }

    private fun DrawScope.colinas(m: Momento, p: Pincel, az: Azar) {
        val l = p.l
        val tramo = EscenaBotanico.tramo(m)
        val cabo = tramo.temporada == 5 && tramo.desde == "05-31"
        val pardo = tramo.temporada == 5 && tramo.desde == "01-12"                  // Australia: colinas pardas y eucaliptos
        val verde = if (pardo) lerp(C.ARENA, C.SELVA, 0.35f) else lerp(C.HOJA, C.SELVA, 0.45f)
        monte(l, sinTaparAstros(perfil(Azar(Semilla.de("botanico", "colinas lejos", az.entero(1000))), 110f, ANCHO + 30f, H + 1f, 22f, 0.3f, 26)), H + 2f, lerp(verde, Color(0xFF7A90A8), 0.5f), 0.84f)
        if (cabo) {
            // La montaña de la Mesa: un frente de roca y la cima plana, con su mantel de nube.
            val x0 = az.entre(170f, 190f); val x1 = x0 + 70f
            drawPath(poligono(listOf(Offset(x0 - 16f, H + 1f), Offset(x0, H - 40f), Offset(x1, H - 41f), Offset(x1 + 18f, H + 1f))), Brush.verticalGradient(listOf(l.tono(lerp(C.ROCA, Color(0xFF7A90A8), 0.3f), 0.7f), l.tono(lerp(verde, C.ROCA, 0.4f), 0.66f)), H - 41f, H))
            mancha(Offset(x0 + 35f, H - 41f), 40f, 3f, l.tono(Color.White, 0.5f), 0.7f)
        }
        monte(l, sinTaparAstros(perfil(Azar(Semilla.de("botanico", "colinas cerca", az.entero(1000))), 130f, ANCHO + 20f, H + 1f, 13f, 0.3f, 26)), H + 2f, verde, 0.6f)
        if (EscenaBotanico.puerto(m)) pueblo(p, az.entre(220f, 240f), az.entre(258f, 266f), H, 2.6f, 0.58f, az, 1, luces = true)
        orillaLejana(p, 130f, ANCHO, az)
    }

    private fun DrawScope.inglesa(m: Momento, p: Pincel, az: Azar) {
        val l = p.l
        // Los acantilados verdes y grises, los campos en damero con sus setos, el pueblo de piedra con su iglesia y el castillo en la punta.
        val tierra = (0..24).map { val x = 120f + (ANCHO + 40f - 120f) * it / 24f; Offset(x, H - (if (x in 262f..338f) 1.5f else 11f + 5f * sin(it * 0.5f + az.real()))) }
        monte(l, tierra, H + 2f, lerp(C.HOJA, C.SELVA, 0.4f), 0.62f)
        clipPath(curva(tierra, H + 2f)) {
            var x = 120f
            while (x < ANCHO) { val w = az.entre(10f, 20f); drawRect(l.tono(if (az.real() > 0.5f) lerp(C.HOJA, C.ARENA, 0.3f) else lerp(C.HOJA, C.SELVA, 0.5f), 0.64f), Offset(x, H - 16f), Size(w, 12f)); drawLine(l.tono(C.SELVA_OSC, 0.6f), Offset(x, H - 16f), Offset(x, H - 4f), 0.6f); x += w }
            drawRect(l.tono(lerp(C.ROCA, C.LONA, 0.3f), 0.6f), Offset(120f, H - 4f), Size(ANCHO, 5f))
        }
        pueblo(p, az.entre(170f, 190f), az.entre(232f, 250f), H - 3f, 2.6f, 0.6f, az, 1, gris = true, luces = true)
        val cx = az.entre(345f, 362f)
        drawRect(l.tono(lerp(C.ROCA, C.LONA, 0.35f), 0.6f), Offset(cx, H - 16f), Size(7f, 6f)); drawRect(l.tono(lerp(C.ROCA, C.LONA, 0.25f), 0.6f), Offset(cx + 1.5f, H - 19f), Size(4f, 3f))
        orillaLejana(p, 120f, ANCHO, az, C.ROCA)
    }

    /** Lo que la costa deja en el agua: su reflejo oscuro bajo la orilla. */
    private fun DrawScope.costaEnElAgua(m: Momento, p: Pincel) {
        val c = EscenaBotanico.costa(m); if (c == Costa.MAR_ABIERTO) return
        val (_, hondo, _) = aguaDe(c)
        reflejoRoto(110f, ANCHO, H + 2.6f, if (EscenaBotanico.fondeado(m)) 9f else 5f, p.l.tono(lerp(hondo, C.SELVA, 0.3f), 0.6f), 0.4f, EscenaBotanico.aguas(m, "reflejo"), 0.8f)
    }

    // ================================================================ El barco

    /**
     * La cubierta, las dos amuradas y lo que va en crujía, de proa a popa: el bauprés y sus estayes, el trinquete, los
     * botes, el palo mayor con su cabillero y sus obenques, la claraboya, el cañón de estribor, la bitácora y la rueda.
     */
    private fun DrawScope.barco(m: Momento, p: Pincel, sol: Sol3) {
        val nieve = EscenaBotanico.nieve(m)
        proa(m, p, sol)
        cubierta(m, p, sol)
        amuradaDeBabor(m, p, sol)
        amuradaDeEstribor(m, p, sol)
        // Las sombras en la cubierta de lo que va en crujía y del cañón.
        capaSombra(sol.r) { c ->
            sombra(PiezasBotanico.sombraDePalo(PiezasBotanico.Palo(SITIO_TRINQUETE, 0L), sol, 4f), c)
            sombra(PiezasBotanico.sombraDeBote(bote(nieve), sol), c)
            sombra(PiezasBotanico.sombraDePalo(PiezasBotanico.Palo(SITIO_PALO, 0L), sol, 4f), c)
            sombra(PiezasBotanico.sombraDeClaraboya(claraboya(m, nieve), sol), c)
            sombra(PiezasBotanico.sombraDeCanon(PiezasBotanico.Canon(SITIO_CANON, 0L), sol), c)
            sombra(PiezasBotanico.sombraDeBitacora(PiezasBotanico.Bitacora(SITIO_BITACORA, 0L), sol), c)
            sombra(PiezasBotanico.sombraDeTimon(PiezasBotanico.Timon(SITIO_TIMON, 0L), sol), c)
        }
        // Lo de crujía, de proa a popa.
        with(PiezasBotanico) {
            val tri = PiezasBotanico.Palo(SITIO_TRINQUETE, Semilla.de("botanico", "trinquete"), 0.24f, 15f, nieve = nieve)
            obenques(tri, p, -3.4f, 1.25f, 12.5f, -1.2f, 1.2f, 4, nieve, 0.3f)
            palo(tri, p, sol)
            obenques(tri, p, 3.45f, 1.25f, 12.5f, -1.2f, 1.2f, 4, nieve, 0.3f)
            bote(bote(nieve), p, sol)
            val mayor = PiezasBotanico.Palo(SITIO_PALO, Semilla.de("botanico", "palo mayor"), 0.28f, 16f, nieve = nieve)
            obenques(mayor, p, -3.65f, 1.25f, 13.5f, -1.4f, 1.4f, 5, nieve, 0.15f)
            val (pm, sm) = pincelEn(m, p, sol, SITIO_PALO.p(0f, 1f, 0f))
            palo(mayor, pm, sm)
            val (pc0, sc0) = pincelEn(m, p, sol, SITIO_CLARABOYA.p(0f, 0.5f, 0f))
            claraboya(claraboya(m, nieve), pc0, sc0)
            val (pk, sk) = pincelEn(m, p, sol, SITIO_CANON.p(0f, 0.5f, 0f))
            canon(PiezasBotanico.Canon(SITIO_CANON, Semilla.de("botanico", "canon"), nieve = nieve), pk, sk)
            val lu = lumbres(m, p.l)
            val (pb, sb) = pincelEn(m, p, sol, SITIO_BITACORA.p(0.3f, 0.6f, -0.3f))
            bitacora(PiezasBotanico.Bitacora(SITIO_BITACORA, Semilla.de("botanico", "bitacora"), lampara = lu.bitacora, nieve = nieve), pb, sb)
            val (pt, st) = pincelEn(m, p, sol, SITIO_TIMON.p(0.4f, 1.1f, 0f))
            timon(PiezasBotanico.Timon(SITIO_TIMON, Semilla.de("botanico", "timon"), nieve = nieve), pt, st)
            // Los obenques de estribor del palo mayor, por delante de todo lo de crujía: suben de la mesa de guarnición.
            obenques(mayor, p, 3.7f, 1.25f, 13.5f, -1.4f, 1.4f, 5, nieve, 0f)
        }
    }

    private fun bote(nieve: Float) = PiezasBotanico.Bote(SITIO_BOTE, Semilla.de("botanico", "bote"), pintura = lerp(C.LONA, C.ARENA, 0.2f), nieve = nieve)
    private fun claraboya(m: Momento, nieve: Float) = PiezasBotanico.Claraboya(SITIO_CLARABOYA, Semilla.de("botanico", "claraboya"), luz = EscenaBotanico.luzDeLaCabina(m), nieve = nieve)

    /** La proa: el bauprés con su botalón, los estayes que suben al trinquete y, con las velas largas, los foques. */
    private fun DrawScope.proa(m: Momento, p: Pincel, sol: Sol3) {
        with(PiezasBotanico) {
            val a = pc(EJE, 1.8f, RODA - 0.2f); val b = pc(EJE, 4.3f, RODA + 7f); val c = pc(EJE, 5.4f, RODA + 11f)
            palito(p, a, b, 2.2f, 1.4f, C.MADERA, 0.25f)
            palito(p, b, c, 1.2f, 0.7f, C.MADERA, 0.3f)
            val tope = pc(EJE, 15f, TRINQUETE)
            drawLine(p.sombra(C.BREA, 0.3f), tope, c, 0.6f); drawLine(p.sombra(C.BREA, 0.3f), tope + Offset(0f, 20f), b, 0.7f)
            if (EscenaBotanico.velas(m) == Velas.LARGAS || EscenaBotanico.velas(m) == Velas.CAIDAS) {
                val az = Azar(Semilla.de("botanico", "foques"))
                val caida = EscenaBotanico.velas(m) == Velas.CAIDAS
                vela(p, b + (tope + Offset(0f, 20f) - b) * 0.05f, b + Offset(-14f, 6f), tope + Offset(0f, 22f), tope + Offset(0f, 20f), Offset(if (caida) 0f else -4f, 2f), 0.2f, az, 6, 0, 0.3f)
                vela(p, c + (tope - c) * 0.05f, c + Offset(-10f, 10f), tope + Offset(2f, 2f), tope, Offset(if (caida) 0f else -3f, 2f), 0.4f, az, 6, 0, 0.32f)
            } else velaAferrada(p, a + (b - a) * 0.4f, b, 2.4f, Azar(Semilla.de("botanico", "foque aferrado")), 0.25f, EscenaBotanico.nieve(m))
        }
    }

    /** La cubierta: tablas de pino lavadas con arena, que corren hacia proa, con sus costuras de brea, sus topes y el desgaste. */
    private fun DrawScope.cubierta(m: Momento, p: Pincel, sol: Sol3) {
        val zs = (0..50).map { 2.2f + (RODA - 2.2f) * it / 50f }
        val forma = poligono(zs.map { pc(estribor(it), 0f, it) } + zs.reversed().map { pc(babor(it), 0f, it) })
        val teca = C.TECA; val mojada = EscenaBotanico.cubiertaMojada(m); val nieve = EscenaBotanico.nieve(m)
        drawPath(forma, Brush.verticalGradient(0f to p.arriba(teca, 0.35f), 0.25f to p.arriba(teca, 0.12f), 1f to p.arriba(lerp(teca, C.ARENA, 0.15f)), startY = H + 6f, endY = ALTO))
        val az = Azar(Semilla.de("botanico", "cubierta"))
        clipPath(forma) {
            // Las costuras de brea, de popa a proa, cada vez más juntas hacia el fondo.
            var x = babor(3f) + 0.02f
            while (x < estribor(3f)) {
                drawLine(p.sombra(lerp(teca, C.BREA, 0.55f)).copy(alpha = 0.55f), pc(x, 0f, 2.2f), pc(x, 0f, RODA), 0.5f)
                // El tono de cada tabla y los topes, alternados.
                if (az.real() > 0.5f) drawPath(poligono(pc(x, 0f, 2.2f), pc(x + 0.15f, 0f, 2.2f), pc(x + 0.15f, 0f, RODA), pc(x, 0f, RODA)), p.arriba(lerp(teca, if (az.real() > 0.5f) C.ARENA else C.MADERA, 0.12f)).copy(alpha = 0.4f))
                var z = 2.4f + az.entre(0f, 5f)
                while (z < RODA) { drawLine(p.sombra(lerp(teca, C.BREA, 0.5f)).copy(alpha = 0.5f), pc(x, 0f, z), pc(x + 0.15f, 0f, z), (k(z) * 0.012f).coerceIn(0.3f, 1.2f)); z += az.entre(4.5f, 6.5f) }
                x += 0.15f
            }
            // El desgaste: donde más se pisa (junto a la rueda, al pie del palo), más claro; las manchas de brea y de agua.
            mancha(pc(-1.2f, 0f, 3.6f), 60f, 12f, p.arriba(lerp(teca, C.LONA, 0.35f)), 0.3f)
            mancha(pc(EJE + 0.8f, 0f, PALO_MAYOR - 0.6f), 30f, 4f, p.arriba(lerp(teca, C.LONA, 0.3f)), 0.3f)
            repeat(14) { val q = pc(az.entre(-4f, 1f), 0f, az.entre(3f, 16f)); mancha(q, az.entre(4f, 14f) * (k(4f) / 75f), az.entre(1f, 3f), p.sombra(lerp(teca, C.BREA, 0.3f)), 0.15f) }
            // Mojada (el baldeo del alba, la lluvia): más oscura y con el cielo reflejado.
            if (mojada) {
                drawPath(forma, p.sombra(lerp(teca, C.MADERA_OSC, 0.4f)).copy(alpha = 0.4f))
                repeat(10) { val q = pc(az.entre(-4f, 1f), 0f, az.entre(3f, 12f)); mancha(q, az.entre(10f, 30f), az.entre(1.5f, 4f), lerp(p.l.medio, Color.White, 0.2f), 0.25f * (1f - 0.6f * p.l.noche)) }
            }
            if (nieve > 0.02f) { drawPath(forma, p.arriba(C.NIEVE).copy(alpha = 0.75f * nieve)); repeat(20) { val q = pc(az.entre(-4f, 1f), 0f, az.entre(3f, 14f)); mancha(q, az.entre(6f, 18f), az.entre(1f, 3f), p.sombra(C.NIEVE), 0.3f * nieve) } }
            if (EscenaBotanico.escarcha(m) && nieve < 0.02f) drawPath(forma, Brush.verticalGradient(listOf(p.luz(C.NIEVE).copy(alpha = 0.12f), p.luz(C.NIEVE).copy(alpha = 0.3f)), H, ALTO))
        }
    }

    /** La amurada de babor, lejos: se ve por dentro desde el palo mayor hasta la proa, con la regala y la batayola. */
    private fun DrawScope.amuradaDeBabor(m: Momento, p: Pincel, sol: Sol3) {
        val zs = (0..30).map { 6f + (RODA - 6f) * it / 30f }
        val cara = poligono(zs.map { pc(babor(it), 0f, it) } + zs.reversed().map { pc(babor(it), REGALA, it) })
        val t = sol.t(V3(1f, 0f, 0f))
        drawPath(cara, p.por(C.AMURADA, t, 0.15f))
        clipPath(cara) { var z = 6.5f; while (z < RODA) { drawLine(p.sombra(C.AMURADA, 0.15f).copy(alpha = 0.5f), pc(babor(z), 0f, z), pc(babor(z), REGALA, z), (k(z) * 0.1f).coerceIn(0.4f, 3f)); z += 1.1f } }
        drawPath(Path().apply { moveTo(pc(babor(zs[0]), REGALA, zs[0])); for (z in zs.drop(1)) lineTo(pc(babor(z), REGALA, z)) }, p.medio(C.MADERA_OSC, 0.15f), style = Stroke(1.6f))
    }

    /**
     * La amurada de estribor por dentro: la diagonal grande del cuadro. Las tablas pintadas de color piedra, los
     * barraganetes, el trancanil con sus imbornales, la porta cerrada del cañón, el cabillero de la banda con sus
     * adujas, y la regala de madera.
     */
    private fun DrawScope.amuradaDeEstribor(m: Momento, p: Pincel, sol: Sol3) {
        val zs = (0..44).map { 1.6f + (RODA - 1.6f) * it / 44f }
        val pie = zs.map { pc(estribor(it), 0f, it) }; val arriba = zs.map { pc(estribor(it), REGALA, it) }
        val cara = poligono(pie + arriba.reversed())
        // La cara de dentro casi siempre queda a la sombra del sol, pero le rebota la luz de la cubierta: abajo, más cálida.
        val t = sol.t(V3(-1f, 0f, 0f)).coerceAtLeast(-0.35f)
        drawPath(cara, Brush.verticalGradient(listOf(p.por(C.AMURADA, t + 0.05f), p.por(lerp(C.AMURADA, C.TECA, 0.4f), t + 0.25f)), H, ALTO))
        val az = Azar(Semilla.de("botanico", "amurada"))
        clipPath(cara) {
            // Las tracas: líneas de popa a proa.
            for (h in listOf(0.28f, 0.55f, 0.82f)) drawPath(Path().apply { moveTo(pc(estribor(zs[0]), h, zs[0])); for (z in zs.drop(1)) lineTo(pc(estribor(z), h, z)) }, p.sombra(C.AMURADA).copy(alpha = 0.4f), style = Stroke(0.6f))
            // Los barraganetes, cada metro: maderos que sobresalen de la amurada, con la cara que mira a popa clara, la
            // de proa en sombra y la sombra que echan en las tablas.
            var z = 2f
            while (z < RODA - 1f) {
                val w = 0.13f; val fondo = 0.12f
                val a = pc(estribor(z), 0f, z); val b = pc(estribor(z), REGALA, z)
                val a1 = pc(estribor(z) - fondo, 0f, z); val b1 = pc(estribor(z) - fondo, REGALA, z)
                val a2 = pc(estribor(z + w) - fondo, 0f, z + w); val b2 = pc(estribor(z + w) - fondo, REGALA, z + w)
                drawPath(poligono(a2, a2 + (pc(estribor(z + w + 0.25f), 0f, z + w + 0.25f) - a2), b2 + (pc(estribor(z + w + 0.25f), REGALA, z + w + 0.25f) - b2), b2), p.sombra(C.AMURADA).copy(alpha = 0.35f * (0.4f + 0.6f * p.r.dia)))
                drawPath(poligono(a, a1, b1, b), p.por(lerp(C.AMURADA, C.LONA, 0.3f), t + 0.55f))
                drawPath(poligono(a1, a2, b2, b1), p.por(C.AMURADA, t + 0.15f))
                drawLine(p.sombra(lerp(C.AMURADA, C.BREA, 0.3f)).copy(alpha = 0.6f), a2, b2, (k(z) * 0.012f).coerceIn(0.3f, 1.6f))
                z += 1.05f
            }
            // El trancanil, oscuro, con los imbornales y el verdín.
            drawPath(poligono(pie + zs.reversed().map { pc(estribor(it), 0.1f, it) }), p.sombra(lerp(C.AMURADA, C.BREA, 0.45f)))
            for (zz in listOf(3.2f, 6.1f, 9.3f, 13.2f)) { val q = pc(estribor(zz), 0.05f, zz); drawOval(p.sombra(C.BREA), Offset(q.x - k(zz) * 0.08f, q.y - k(zz) * 0.05f), Size(k(zz) * 0.16f, k(zz) * 0.06f)) }
            // La porta del cañón, cerrada.
            val q0 = pc(estribor(7.0f), 0.32f, 7.0f); val q1 = pc(estribor(7.9f), 0.32f, 7.9f); val q2 = pc(estribor(7.9f), 0.88f, 7.9f); val q3 = pc(estribor(7.0f), 0.88f, 7.0f)
            drawPath(poligono(q0, q1, q2, q3), p.por(lerp(C.AMURADA, C.TEJA, 0.3f), t)); drawPath(poligono(q0, q1, q2, q3), p.sombra(C.BREA).copy(alpha = 0.5f), style = Stroke(0.8f))
            // Las chorreras de óxido de las argollas y los desconchones.
            repeat(8) { val zz = az.entre(2.5f, 14f); val q = pc(estribor(zz), az.entre(0.3f, 0.9f), zz); drawLine(p.sombra(C.CUERO).copy(alpha = 0.35f), q, q + Offset(0f, k(zz) * az.entre(0.1f, 0.3f)), (k(zz) * 0.015f).coerceIn(0.3f, 1.6f)) }
            repeat(10) { val zz = az.entre(2.5f, 12f); mancha(pc(estribor(zz), az.entre(0.2f, 0.9f), zz), k(zz) * az.entre(0.05f, 0.14f), k(zz) * az.entre(0.03f, 0.06f), p.por(lerp(C.AMURADA, C.MADERA, 0.6f), t), 0.3f) }
        }
        // Con tormenta, los rociones que saltan por encima de la regala.
        if (EscenaBotanico.tormenta(m)) { val rz = m.azar("rociones"); repeat(9) { val zz = rz.entre(5f, 16f); val q = pc(estribor(zz) + 0.3f, REGALA + rz.entre(0.3f, 1.4f), zz); mancha(q, k(zz) * rz.entre(0.4f, 0.9f), k(zz) * rz.entre(0.2f, 0.45f), p.l.tono(Color.White), 0.45f); drawPoints((0 until 12).map { q + Offset(rz.entre(-1f, 1f) * k(zz) * 0.6f, rz.entre(-1f, 0.6f) * k(zz) * 0.3f) }, androidx.compose.ui.graphics.PointMode.Points, p.l.tono(Color.White).copy(alpha = 0.7f), 1.1f, StrokeCap.Round) } }
        // La regala: el canto de arriba, de madera, con su cara de arriba al cielo.
        val fuera = zs.map { pc(estribor(it) + 0.28f, REGALA + 0.02f, it) }; val dentro = zs.map { pc(estribor(it) - 0.04f, REGALA + 0.02f, it) }
        drawPath(poligono(dentro + fuera.reversed()), p.arriba(lerp(C.MADERA, C.TECA, 0.3f)))
        drawPath(poligono(arriba + dentro.reversed()), p.por(C.MADERA_OSC, t))
        drawPath(Path().apply { moveTo(fuera[0]); for (q in fuera.drop(1)) lineTo(q) }, p.luz(lerp(C.MADERA, C.LONA, 0.3f)).copy(alpha = 0.6f), style = Stroke(0.8f))
        if (EscenaBotanico.nieve(m) > 0.02f || EscenaBotanico.escarcha(m)) drawPath(poligono(dentro + fuera.reversed()), p.luz(C.NIEVE).copy(alpha = if (EscenaBotanico.nieve(m) > 0.02f) 0.85f else 0.4f))
        // El cabillero de la banda, junto al palo mayor: cabillas con sus adujas colgadas.
        with(PiezasBotanico) {
            for (zz in listOf(9.4f, 10.0f, 10.6f, 12.4f, 13.0f)) {
                val q = pc(estribor(zz) - 0.06f, 0.72f, zz); val e = k(zz)
                drawLine(p.sombra(C.MADERA_OSC), q + Offset(0f, -e * 0.08f), q + Offset(0f, e * 0.1f), (e * 0.03f).coerceAtLeast(0.4f))
                for (j in 0..2) drawOval(p.por(C.CANAMO, if (j == 1) 0.3f else -0.2f), Offset(q.x - e * (0.07f + 0.015f * j), q.y), Size(e * (0.14f + 0.03f * j), e * (0.34f + 0.04f * j)), style = Stroke((e * 0.016f).coerceIn(0.3f, 1.6f)))
            }
        }
    }

    // ================================================================ El primer plano: el taller del naturalista

    /**
     * El primer plano, de atrás adelante por dónde pisa: la mesa de trabajo con el objeto del día (o, en los lugares sin
     * cuadro propio, un cajón de soporte), la prensa, la caja de frascos, los frascos sueltos, el objeto grande, los
     * cajones trincados contra la amurada y el farol de popa.
     */
    private fun DrawScope.primerPlano(m: Momento, p: Pincel, sol: Sol3) {
        val nieve = EscenaBotanico.nieve(m); val estado = EscenaBotanico.mesaDeCubierta(m)
        val cosas = ArrayList<Pair<Float, DrawScope.() -> Unit>>()
        val me = mesa(m)
        val (pm, sm) = pincelEn(m, p, sol, me.tablero)
        if (estado != null) {
            cosas += parte(SITIO_MESA.p(0f, 0f, 0f).y) {
                with(PiezasBotanico) {
                    mesa(me, pm, sm)
                    if (estado != EscenaBotanico.Mesa.TAPADA && estado != EscenaBotanico.Mesa.RECOGIDA) {
                        // Encima: la carta marina sujeta con piedras, una hoja de herbario, la prensa en su esquina.
                        val carta = listOf(me.sobre(-0.95f, -0.2f), me.sobre(-0.25f, -0.35f), me.sobre(-0.2f, 0.85f), me.sobre(-0.9f, 0.9f))
                        cartaMarina(pm, carta, sm.t(SITIO_MESA.normal(V3(0f, 1f, 0f))) + 0.2f, Azar(Semilla.de("botanico", "carta")))
                        val hoja = listOf(me.sobre(0.45f, 0.2f), me.sobre(0.92f, 0.15f), me.sobre(0.95f, 0.9f), me.sobre(0.5f, 0.92f))
                        hojaDeHerbario(pm, hoja, 0, 0.5f, Azar(Semilla.de("botanico", "herbario mesa")))
                        prensa(PiezasBotanico.Prensa(Sitio(MIRADA, me.sobre(-0.55f, 0.55f) + Offset(0f, 0f), SITIO_MESA.k, SITIO_MESA.giro + 0.25f), Semilla.de("botanico", "prensa"), base = 0f), pm, sm)
                    }
                    with(ObjetosBotanico) { sobreElSoporte(m, pm, me.tablero, 1f) }
                }
            }
        } else {
            // Sin mesa (fondeado frente a un lugar sin cuadro): un cajón hace de soporte.
            val c = PiezasBotanico.Cajon(SITIO_MESA, Semilla.de("botanico", "cajon soporte"), ancho = 0.82f, alto = ALTO_CAJON, fondo = 0.56f, nieve = nieve)
            cosas += parte(SITIO_MESA.p(0f, 0f, 0f).y) { with(PiezasBotanico) { cajon(c, pm, sm) }; with(ObjetosBotanico) { sobreElSoporte(m, pm, c.tapa, 1f) } }
        }
        // La caja de frascos y unos frascos sueltos junto a ella; piedras de la última costa; un cabo adujado.
        val frascos = PiezasBotanico.CajaDeFrascos(SITIO_FRASCOS, Semilla.de("botanico", "frascos"))
        cosas += parte(SITIO_FRASCOS.p(0f, 0f, 0f).y) {
            val (pf, sf) = pincelEn(m, p, sol, SITIO_FRASCOS.p(0f, 0.3f, 0f))
            with(PiezasBotanico) { cajaDeFrascos(frascos, pf, sf); frasco(pf, pc(0.66f, 0f, 3.4f), 22f, 0); frasco(pf, pc(0.72f, 0f, 3.65f), 17f, 1) }
        }
        cosas += parte(pc(-1.5f, 0f, 3.3f).y) { piedrasYCabo(p, sol) }
        ObjetosBotanico.grande(m, pm, PIE_GRANDE, apoyoGrande(m), 1f)?.let { g -> cosas += parte(g.pie.y) { g.pintar(this) } }
        // Los cajones, trincados contra la amurada: los de abajo primero, y de proa a popa.
        val cajones = (0 until cajonesEnCubierta(m)).map { i -> val h = HUECOS[i]
            PiezasBotanico.Cajon(sitio(h[0], h[1], h[3]), Semilla.de("botanico", "cajon", i), ancho = 0.82f - 0.06f * (i % 3), alto = ALTO_CAJON, fondo = 0.56f, base = h[2] * ALTO_CAJON,
                desgaste = 0.3f + 0.2f * (i % 4), forrado = EscenaBotanico.diaDeEnvio(m) && i == 0, nieve = nieve) }
        for (c in cajones.sortedWith(compareBy({ -it.s.hondo(V3(0f, 0f, 0f)) }, { it.base }))) cosas += parte(c.s.pie.y + c.base * 0.01f) {
            val (pcj, scj) = pincelEn(m, p, sol, c.s.p(0f, c.base + 0.2f, 0f)); with(PiezasBotanico) { cajon(c, pcj, scj) } }
        // La cuba del agua, en la esquina; la red tendida a secar sobre la regala, o su cabo saliendo por la popa.
        val cuba = PiezasBotanico.Cuba(SITIO_CUBA, Semilla.de("botanico", "cuba"), radio = 0.26f, alto = 0.9f, nieve = nieve)
        cosas += parte(ALTO + 60f) { val (pq, sq) = pincelEn(m, p, sol, SITIO_CUBA.p(0f, 0.8f, 0f)); with(PiezasBotanico) { cuba(cuba, pq, sq) } }
        when (EscenaBotanico.red(m)) {
            EscenaBotanico.Red.SECANDO -> cosas += parte(pc(estribor(5.6f), 0f, 5.6f).y) { with(PiezasBotanico) { redASecar(p, pc(estribor(4.7f) - 0.02f, REGALA + 0.02f, 4.7f), pc(estribor(6.7f) - 0.02f, REGALA + 0.02f, 6.7f), k(5.7f) * 0.55f, Azar(Semilla.de("botanico", "red"))) } }
            EscenaBotanico.Red.ARRASTRANDO -> cosas += parte(ALTO + 50f) { with(PiezasBotanico) { cabo(p, pc(estribor(2.8f) + 0.1f, REGALA + 0.05f, 2.8f), Offset(ANCHO + 30f, ALTO + 30f), 2f, 1.6f) } }
            EscenaBotanico.Red.GUARDADA -> Unit
        }
        // Las sombras del primer plano.
        capaSombra(sol.r) { c ->
            sombra(PiezasBotanico.sombraDeCuba(cuba, sol), c)
            if (estado != null) sombra(PiezasBotanico.sombraDeMesa(me, sol), c)
            sombra(PiezasBotanico.sombraDeCajaDeFrascos(frascos, sol), c)
            for (cj in cajones) sombra(PiezasBotanico.sombraDeCajon(cj, sol), c)
        }
        for ((_, pintar) in cosas.sortedBy { it.first }) pintar()
        farol(m, p, sol)
        // De noche, la luz de la claraboya y de la bitácora en la cubierta de alrededor.
        val lu = lumbres(m, p.l)
        if (lu.claraboya > 0.02f) resplandor(SITIO_CLARABOYA.p(0f, 0.3f, 0f), 70f, 0.4f * lu.claraboya, C.LUMBRE)
        if (lu.bitacora > 0.02f) resplandor(SITIO_BITACORA.p(0f, 1f, -0.3f), 50f, 0.35f * lu.bitacora, C.LUMBRE)
    }

    /** Las piedras que trajo de la última costa («les llena la cubierta de basura»), el martillo cerca, y un cabo adujado a la holandesa. */
    private fun DrawScope.piedrasYCabo(p: Pincel, sol: Sol3) {
        val az = Azar(Semilla.de("botanico", "piedras"))
        repeat(7) {
            val q = pc(az.entre(-1.9f, -0.9f), 0f, az.entre(3.1f, 3.6f)); val r = az.entre(2.5f, 6f)
            drawOval(p.r.color.copy(alpha = 0.5f * p.r.fuerza), Offset(q.x - r + p.r.ax.coerceIn(-2f, 2f) * r * 0.4f, q.y - r * 0.15f), Size(r * 2.3f, r * 0.5f), blendMode = BlendMode.Multiply)
            val forma = poligono(listOf(q + Offset(-r, 0f), q + Offset(-r * 0.8f, -r * 0.7f), q + Offset(r * 0.1f, -r * 0.95f), q + Offset(r * 0.9f, -r * 0.5f), q + Offset(r, 0f)))
            val roca = if (it % 3 == 0) lerp(C.ROCA, C.CUERO, 0.3f) else if (it % 3 == 1) lerp(C.ROCA, C.BREA, 0.3f) else lerp(C.ROCA, C.LONA, 0.3f)
            drawPath(forma, Brush.horizontalGradient(listOf(p.sombra(roca), p.medio(roca), p.luz(roca)), q.x + (if (p.solALaDerecha) -r else r), q.x + (if (p.solALaDerecha) r else -r)))
        }
        val c = pc(-2.6f, 0f, 3.15f); val e = k(3.15f)
        for (j in 0..6) { val r = e * (0.3f - 0.035f * j); drawOval(p.por(C.CANAMO, if (j % 2 == 0) 0.4f else -0.1f), Offset(c.x - r, c.y - r * 0.28f), Size(r * 2f, r * 0.56f), style = Stroke((e * 0.03f).coerceIn(0.5f, 3f))) }
    }

    /** El farol de popa, sobre la regala de la aleta: de latón, con sus vidrios; de noche, alumbra la amurada, los cajones y el agua. */
    private fun DrawScope.farol(m: Momento, p: Pincel, sol: Sol3) {
        val q = FAROL; val e = k(3.3f); val f = lumbres(m, p.l).farol
        // El candelero de hierro que sale de la regala, con su brazo, y el farol colgado.
        val base = pc(1.66f, REGALA + 0.02f, 3.3f); val codo = base + Offset(0f, -(base.y - q.y) - e * 0.22f)
        drawLine(p.sombra(C.HIERRO), base, codo, (e * 0.03f).coerceAtLeast(0.6f)); drawLine(p.sombra(C.HIERRO), codo, codo + Offset(-e * 0.1f, 0f), (e * 0.025f).coerceAtLeast(0.5f))
        drawLine(p.luz(C.HIERRO).copy(alpha = 0.5f), base + Offset(-0.4f, 0f), codo + Offset(-0.4f, 0f), (e * 0.01f).coerceAtLeast(0.3f))
        with(PiezasBotanico) { farol(p, q + Offset(-e * 0.1f, 0f), e * 0.3f, f) }
        if (f > 0.05f) resplandor(q + Offset(-e * 0.3f, e * 0.4f), e * 1.4f, 0.25f * f, C.LUMBRE)
    }

    // ================================================================ El marco

    /**
     * Lo que enmarca por arriba y cruza: la vela de popa con su botavara (arriba a la izquierda, cortada por los
     * bordes); la vela mayor, con la escota y sus motones que bajan cruzando el cielo hasta la regala; el empavesado de
     * las fiestas. Nada tapa la ventana de los astros (x de 270 a 330, y de 45 a 110).
     */
    private fun DrawScope.marco(m: Momento, p: Pincel, sol: Sol3) {
        val velas = EscenaBotanico.velas(m); val nieve = EscenaBotanico.nieve(m)
        val az = Azar(Semilla.de("botanico", "velas"))
        val lu = lumbres(m, p.l)
        with(PiezasBotanico) {
            if (velas == Velas.LARGAS || velas == Velas.CAIDAS) {
                val caida = velas == Velas.CAIDAS
                // La vela mayor (de cruz), con su verga muy arriba; los puños, a babor y a estribor; la escota de estribor baja a la regala.
                val b = 0.3f
                fun brazo(d: Float, h: Float) = pc(EJE + d * kotlin.math.cos(b), h, PALO_MAYOR + 0.35f + d * kotlin.math.sin(b))
                val puBabor = brazo(-5.3f, 4.1f); val puEstribor = brazo(5.3f, 4.1f)
                vela(p, puBabor, puEstribor, brazo(6.2f, 13f), brazo(-6.2f, 13f), Offset(0f, if (caida) 4f else -10f), sol.t(V3(0.2f, 0.1f, -1f)), az, 14, 2, 0.12f)
                escota(p, puEstribor, pc(estribor(3.7f) + 0.12f, REGALA + 0.04f, 3.7f), 7f)
                // La vela de popa (la cangreja), cazada a babor: la botavara baja del borde de arriba a la izquierda hasta el palo.
                val phi = if (caida) 0.18f else 0.42f
                val boca = pc(EJE, 3.25f, PALO_MAYOR - 0.4f); val escotaP = pc(EJE - 10f * kotlin.math.sin(phi), 3.9f, PALO_MAYOR - 0.4f - 10f * kotlin.math.cos(phi))
                val pico = pc(EJE - 8.6f * kotlin.math.sin(phi), 15.5f, PALO_MAYOR - 0.4f - 8.6f * kotlin.math.cos(phi)); val garganta = pc(EJE, 11f, PALO_MAYOR - 0.4f)
                vela(p, boca, escotaP, pico, garganta, Offset(-14f, if (caida) 16f else 8f), sol.t(V3(kotlin.math.cos(phi), 0f, -kotlin.math.sin(phi))), az, 16, 2, 0f, luzDeDentro = lu.claraboya)
                palito(p, boca, escotaP, 4.4f, 9f, C.MADERA, 0f, listOf(0.25f, 0.6f))
            } else {
                // Fondeado o con tormenta: la cangreja aferrada sobre la botavara, en crujía, y la botavara que sale por arriba a la izquierda.
                val boca = pc(EJE, 3.25f, PALO_MAYOR - 0.4f); val fin = pc(EJE + 0.05f, 3.7f, 2.2f)
                velaAferrada(p, boca + (fin - boca) * 0.04f, boca + (fin - boca) * 0.9f, 9f, az, 0f, nieve)
                palito(p, boca, fin, 4.4f, 12f, C.MADERA, 0f, listOf(0.3f, 0.7f))
            }
            // De noche, la claraboya alumbra desde abajo la botavara, la vela y el palo.
            if (lu.claraboya > 0.02f) { val q = pc(EJE, 3.3f, PALO_MAYOR - 2.5f); resplandor(q, 60f, 0.45f * lu.claraboya, C.LUMBRE); resplandor(pc(EJE, 1.8f, PALO_MAYOR - 0.3f), 26f, 0.4f * lu.claraboya, C.LUMBRE) }
            if (EscenaBotanico.fiesta(m)) empavesado(m, p)
        }
    }

    /** El empavesado: banderas de señales (rayas, cruces, cuarteles; sin letras) en un cabo del tope del palo mayor a la regala. */
    private fun DrawScope.empavesado(m: Momento, p: Pincel) {
        val a = pc(EJE, 15f, PALO_MAYOR); val b = pc(estribor(5.5f) + 0.1f, REGALA + 0.05f, 5.5f)
        val pts = combada(a, b, 8f, 22)
        drawPath(curva(pts), p.sombra(C.CANAMO), style = Stroke(0.6f))
        val colores = listOf(C.TEJA, C.LONA, lerp(C.MAR, C.BREA, 0.3f), C.LATON, C.BREA, C.TEJA, C.LONA, C.LATON)
        for (i in 2 until pts.size - 1 step 1) {
            if (pts[i].y < 0f) continue
            val q = pts[i]; val w = 2.6f + 5f * (q.y / 200f); val h = w * 0.8f; val c = colores[i % colores.size]; val c2 = colores[(i + 3) % colores.size]
            drawRect(p.medio(c), Offset(q.x - w / 2f, q.y + 0.5f), Size(w, h))
            when (i % 3) { 0 -> drawRect(p.medio(c2), Offset(q.x - w / 2f, q.y + 0.5f + h * 0.35f), Size(w, h * 0.3f)); 1 -> { drawLine(p.medio(c2), Offset(q.x, q.y + 0.5f), Offset(q.x, q.y + 0.5f + h), w * 0.25f); drawLine(p.medio(c2), Offset(q.x - w / 2f, q.y + 0.5f + h / 2f), Offset(q.x + w / 2f, q.y + 0.5f + h / 2f), w * 0.25f) }; else -> drawRect(p.medio(c2), Offset(q.x - w / 2f, q.y + 0.5f), Size(w / 2f, h / 2f)) }
        }
    }

    // ================================================================ Lo que se mueve

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val l = EscenaBotanico.luces(m)
        val g = EscenaBotanico.inclinacion(m)
        val zona = Rect(160f, H + 1f, ANCHO, 230f)
        if (g == 0f) destellos(l, zona, reloj, m.semilla("destellos"), 22)
        else withTransform({ rotate(g, Offset(ANCHO / 2f, H)); scale(1.18f, 1.18f, Offset(ANCHO / 2f, H)) }) { destellos(l, zona, reloj, m.semilla("destellos"), 26) }
        val lu = lumbres(m, l)
        if (lu.farol > 0.05f) reflejo(Offset(FAROL.x, 236f), 30f, C.LUMBRE, 0.5f * lu.farol, reloj, 3)
        tiempoVivo(l, m.ficha.clima, reloj, H, m.semilla("tiempo"))
    }

    // ================================================================ El gato

    fun luzDelGato(m: Momento, en: Offset): LuzGato { val l = EscenaBotanico.luces(m); return LuzGato(EscenaBotanico.rayo(l), l.noche) { l.tono(it) } }

    /** En la cubierta de delante no hay nada plantado en el paso del gato (la mesa y los cajones pisan detrás). */
    fun estorbos(m: Momento): List<Float> = emptyList()

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, GATO_DESDE, PISO_GATO, hasta = GATO_HASTA))
        val mirador = miradorDeLosCajones(m)
        add(Ancla(TipoAncla.MIRADOR, mirador.x, mirador.y))                     // en lo alto de los cajones de popa
        add(Ancla(TipoAncla.OBJETO, SITIO_MESA.pie.x, PISO_GATO))
        add(Ancla(TipoAncla.ABRIGO, SITIO_MESA.pie.x - 18f, PISO_GATO))           // debajo de la mesa
        if (EscenaBotanico.fiesta(m)) add(Ancla(TipoAncla.ADORNO, 220f, 150f))
    }
}
