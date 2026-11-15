package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * El bergantín: el cuadro principal de Botánico (`composicion.md` de la tanda S13, cuadro A). De pie en la popa, junto
 * al timón, mirando hacia proa por la banda de estribor: la cubierta se aleja hacia el centro izquierda y el mar queda a
 * la derecha. Visto a lo largo, no de través, para no repetir el cuadro de Pirata.
 *
 * **La perspectiva.** Una cámara de agujero (la de Mongol: [Mirada], [Sitio]) con los ojos a [OJO] metros sobre la
 * cubierta y el eje del barco hacia el punto de fuga ([FUGA], [H]). Las medidas del barco van en metros desde los
 * ojos: x a estribor, h hacia arriba, z hacia proa ([pc], [sitio]); un punto así se escribe como `V3(x, h, z)`. Un
 * bergantín de 27 metros **[Aproximación]**: crujía a 2.3 m a babor de los ojos, media manga de 3.8 m, la amurada de
 * estribor a 1.5 m a la derecha; el palo mayor a 11.5 m, el trinquete a 20 m, la roda a 26.5 m.
 *
 * **La luz** ([rayoDelBarco], [Vista]). De día manda el sol de todas las escenas, con las sombras acortadas para que caigan
 * dentro del barco **[Aproximación]**: la «mancha de sol que camina» de los interiores aprobados (la capilla, Scriptorium,
 * Renacimiento) es aquí **la sombra de las velas, de la amurada y de la jarcia sobre la cubierta** ([Vista.manchas]):
 * por la mañana la amurada de estribor deja en penumbra la fila de cajones y la reja de los obenques cruza las tablas
 * hacia babor; a mediodía la mayor echa su sombra bajo ella, en medio de la cubierta; por la tarde la cangreja y la
 * amurada de babor la tienden hacia estribor. Lo que queda dentro de esa sombra se pinta en penumbra cálida
 * ([Vista.pincel]); lo que queda al sol se enciende. De noche, dos luces de color contrario, como en Renacimiento: la de la
 * cabina, que sube por la claraboya, la lámpara de la bitácora y el farol de popa, cálidas ([Lumbres]); y la luna, fría,
 * que platea la cara de las velas, los cantos, la cubierta mojada de relente y el mar, y tumba sus sombras
 * ([NocheBarco]).
 *
 * Los planos, de atrás adelante, cada uno en su archivo:
 * - **El mundo** ([MundoBarco], `EscenaBotanicoBarcoMundo.kt`): el cielo, la costa del tramo y el mar. Con tormenta se
 *   inclina entero.
 * - **Las velas y la jarcia** ([VelasBarco], `EscenaBotanicoBarcoVelas.kt`): la proa y el trinquete con sus foques, la
 *   mayor de cruz con su escota, la cangreja con su botavara (el marco de arriba a la izquierda), las brazas, los
 *   brandales y el empavesado.
 * - **La cubierta** ([CubiertaBarco], `EscenaBotanicoBarcoCubierta.kt`): las tablas, las dos amuradas (con la batayola, las
 *   cabillas, lo trincado), la capa de sombras y lo que va en crujía: los botes, el palo mayor, la claraboya, el cañón en
 *   su porta, la bitácora y la rueda.
 * - **El taller del naturalista** ([TallerBarco], `EscenaBotanicoBarcoTaller.kt`): la mesa con lo suyo y el objeto del
 *   día, los cajones (el arco `cajones`), la cuba, el balde, el lampazo, las adujas y las piedras.
 * - **La noche** ([NocheBarco], `EscenaBotanicoBarcoNoche.kt`): las dos luces.
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
    fun pc(a: V3): Offset = pc(a.x, a.y, a.z)

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
    /** Dónde empieza la cubierta que se ve (más acá queda debajo de los ojos). */
    const val POPA = 1.4f

    /** La media manga de la cubierta a [z] metros: igual hasta los 15 m y, hacia la proa, se cierra en la roda. */
    fun media(z: Float): Float = if (z <= 15f) MEDIA_MANGA else MEDIA_MANGA * sqrt((1f - ((z - 15f) / (RODA - 15f)).let { it * it }).coerceAtLeast(0f))
    fun estribor(z: Float) = EJE + media(z)
    fun babor(z: Float) = EJE - media(z)

    /** La batayola de estribor (la red de los coyes sobre la regala): de dónde a dónde va y cuánto alza. */
    const val BATAYOLA_DESDE = 6.2f
    const val BATAYOLA_HASTA = 14.5f
    const val BATAYOLA_ALTO = 0.36f

    // ---------------------------------------------------------------- Dónde está cada cosa

    val SITIO_TIMON = sitio(EJE, 4.4f, 0.32f)
    val SITIO_BITACORA = sitio(-1.45f, 5.6f, 0.3f)
    val SITIO_CLARABOYA = sitio(EJE + 0.05f, 8.6f, 0.06f)
    /** El cañón, arrimado a su porta de estribor, con la boca hacia ella. */
    const val Z_CANON = 9.1f
    val SITIO_CANON = sitio(estribor(Z_CANON) - 1.12f, Z_CANON, 0f)
    val SITIO_PALO = sitio(EJE, PALO_MAYOR, 0f)
    val SITIO_TRINQUETE = sitio(EJE, TRINQUETE, 0f)
    val SITIO_BOTE = sitio(EJE, 15.6f, 0.02f)

    /** La mesa de trabajo, a estribor del timón, cerca de quien mira y un poco girada **[Diseño propio]**. */
    val SITIO_MESA = sitio(-0.55f, 3.62f, 0.18f)
    const val MESA_ANCHO = 1.95f
    const val MESA_FONDO = 0.9f
    const val MESA_ALTO = 0.84f

    /** La cuba del agua, en la esquina de la aleta, trincada a la amurada: cortada por los bordes de abajo y de la derecha. */
    val SITIO_CUBA = sitio(1.15f, 2.05f, 0f)

    /** El farol de popa, sobre la regala de la aleta de estribor. */
    val FAROL: Offset get() = pc(1.66f, 1.42f, 3.3f)

    /** El suelo del gato: la cubierta de delante, de la rueda a los cajones (pisa más acá que todo lo plantado). */
    const val PISO_GATO = 277f
    const val GATO_DESDE = 30f
    const val GATO_HASTA = 184f

    /**
     * Los huecos para los cajones, de abajo arriba y de popa a proa, contra la amurada de estribor: (x, z, piso, giro).
     * Se llenan en este orden; los primeros quedan cortados por el borde de abajo.
     */
    internal val HUECOS = listOf(
        floatArrayOf(1.06f, 3.4f, 0f, 0.05f), floatArrayOf(1.08f, 2.65f, 0f, -0.04f), floatArrayOf(1.06f, 2.65f, 1f, 0.07f),
        floatArrayOf(1.05f, 4.15f, 0f, -0.06f), floatArrayOf(1.07f, 3.4f, 1f, -0.05f), floatArrayOf(1.04f, 2.65f, 2f, -0.1f),
        floatArrayOf(1.06f, 4.9f, 0f, 0.04f), floatArrayOf(1.05f, 4.15f, 1f, 0.08f), floatArrayOf(1.06f, 3.4f, 2f, 0.11f),
    )
    const val ALTO_CAJON = 0.46f

    /** Los cajones que hay hoy en cubierta: tantos como diga [EscenaBotanico.cuantosCajones] (lo vigila una prueba). */
    fun cajonesEnCubierta(m: Momento): Int = EscenaBotanico.cuantosCajones(m).coerceIn(1, HUECOS.size)

    /** Lo alto de la pila de cajones de popa, donde el gato se sienta a mirar (y en el lienzo). */
    fun miradorDeLosCajones(m: Momento): Offset {
        val pila = (0 until cajonesEnCubierta(m)).map { HUECOS[it] }.filter { it[1] == 3.4f }
        return pc(0.95f, ALTO_CAJON * pila.size, 3.4f)
    }

    /** La mesa de hoy (con el encerado si llueve o nieva). */
    internal fun mesa(m: Momento): PiezasBotanico.Mesa = PiezasBotanico.Mesa(SITIO_MESA, Semilla.de("botanico", "mesa", "cubierta"), PiezasBotanico.TipoMesa.CUBIERTA,
        ancho = MESA_ANCHO, fondo = MESA_FONDO, alto = MESA_ALTO, tapada = false, nieve = EscenaBotanico.nieve(m))

    /**
     * Dónde va el objeto chico del día: delante, en medio del tablero, con sitio alrededor (en los lugares sin cuadro
     * propio, que van sin mesa, sobre un cajón de soporte en su sitio).
     */
    fun soporte(m: Momento): Offset = if (EscenaBotanico.mesaDeCubierta(m) != null) mesa(m).sobre(0.12f, -0.34f) else SITIO_MESA.p(0f, ALTO_CAJON + 0.02f, 0f)

    /** El objeto grande pisa delante del canto derecho de la mesa y se recuesta en él. */
    val PIE_GRANDE: Offset get() = pc(0.5f, 0f, 3.2f)
    fun apoyoGrande(m: Momento): Offset = if (EscenaBotanico.mesaDeCubierta(m) != null) SITIO_MESA.p(MESA_ANCHO * 0.47f, MESA_ALTO + 0.02f, -MESA_FONDO * 0.42f) else SITIO_MESA.p(0.4f, ALTO_CAJON, -0.26f)

    /** La escala de los objetos del día en un sitio a [z] metros: la de Mongol, 60 unidades por metro a escala 1. */
    fun escalaEn(z: Float): Float = k(z) / 60f

    fun seMueve(m: Momento): Boolean = true                                    // el mar siempre brilla

    // ---------------------------------------------------------------- La luz del sol en cubierta

    /** Lo más larga que es una sombra: por cada metro de alto, a lo sumo este tanto de largo (a las 7:00, 1.3 m). */
    const val COT_MAX = 1.3f
    /** Lo menos de lado que entra la luz: así la sombra de las velas se ve tumbarse a babor o a estribor. */
    const val LAT_MIN = 0.62f

    /**
     * El rayo del barco: el de [EscenaBotanico.rayo] (el sol, o de noche la luna), con las sombras acortadas y un poco más
     * de lado, para que la sombra de las velas, de la amurada y de la jarcia caiga en la cubierta y no en el mar
     * **[Aproximación]**, como la luz que entra de lado en todas las escenas. Lo vigila la prueba de este cuadro.
     */
    internal fun rayoDelBarco(l: Luces): Rayo {
        val r = EscenaBotanico.rayo(l)
        val lat = if (abs(r.lat) < LAT_MIN) (if (r.lat < 0f) -LAT_MIN else LAT_MIN) else r.lat
        val cot = r.cot.coerceIn(0.45f, COT_MAX)
        return Rayo(r.dia, lat, cot, 1f / cot, -lat * cot, -0.33f * (1.3f - 0.6f * abs(lat)) * cot, r.fuerza, r.color, r.contraste)
    }

    /** Dónde cae en la cubierta (en metros: x, z) la sombra del punto [a] (x, h, z). */
    fun alSuelo(sol: Sol3, a: V3): Offset = Offset(a.x + sol.sx * a.y, a.z + sol.sz * a.y)

    /** La sombra de un cuerpo plano de esquinas [pts] en la cubierta, en metros. */
    fun sombraEnCubierta(sol: Sol3, pts: List<V3>): List<Offset> = pts.map { alSuelo(sol, it) }

    /** Un polígono de la cubierta (en metros: x, z) en el lienzo, recortado a lo que queda delante de los ojos. */
    fun enLienzo(q: List<Offset>): Path? { val r = recortaZ(q, POPA - 0.4f); return if (r.size < 3) null else poligono(r.map { pc(it.x, 0f, it.y) }) }

    /** Recorta un polígono de la cubierta a lo que está más allá de [z0] (Sutherland–Hodgman con un solo borde). */
    fun recortaZ(q: List<Offset>, z0: Float): List<Offset> {
        if (q.isEmpty()) return q
        val r = ArrayList<Offset>()
        for (i in q.indices) {
            val a = q[i]; val b = q[(i + 1) % q.size]
            val ina = a.y >= z0; val inb = b.y >= z0
            if (ina) r += a
            if (ina != inb) { val t = (z0 - a.y) / (b.y - a.y); r += Offset(a.x + (b.x - a.x) * t, z0) }
        }
        return r
    }

    /** Si el punto ([x], [z]) de la cubierta está dentro del polígono [q] (en metros). */
    fun dentro(x: Float, z: Float, q: List<Offset>): Boolean {
        var d = false; var j = q.size - 1
        for (i in q.indices) { val a = q[i]; val b = q[j]; if ((a.y > z) != (b.y > z) && x < (b.x - a.x) * (z - a.y) / (b.y - a.y) + a.x) d = !d; j = i }
        return d
    }

    /**
     * La sombra de la amurada de estribor en la cubierta, en metros (x, z): una franja a lo largo, cuando el sol viene de
     * estribor (por la mañana). La de babor, cuando viene de babor (por la tarde). Con la batayola, más alta en el combés.
     */
    fun sombraDeLaAmurada(sol: Sol3, estriborLado: Boolean): List<Offset>? {
        if (estriborLado && sol.sx > -0.02f) return null
        if (!estriborLado && sol.sx < 0.02f) return null
        val zs = (0..30).map { POPA + (RODA - POPA) * it / 30f }
        fun alto(z: Float) = REGALA + if (estriborLado && z in BATAYOLA_DESDE..BATAYOLA_HASTA) BATAYOLA_ALTO else 0f
        val borde = zs.map { z -> Offset(if (estriborLado) estribor(z) else babor(z), z) }
        val cae = zs.map { z -> val h = alto(z); Offset((if (estriborLado) estribor(z) else babor(z)) + sol.sx * h, z + sol.sz * h) }
        return borde + cae.reversed()
    }

    // ---------------------------------------------------------------- Las lumbres

    /** Las luces cálidas encendidas a esta hora: la claraboya, la lámpara de la bitácora y el farol de popa (de 0 a 1 cada una). */
    internal class Lumbres(val claraboya: Float, val bitacora: Float, val farol: Float)

    internal fun lumbres(m: Momento, l: Luces): Lumbres = Lumbres(
        claraboya = EscenaBotanico.luzDeLaCabina(m) * l.noche,
        bitacora = if (EscenaBotanico.lamparaDeLaBitacora(m)) l.noche else 0f,
        farol = if (EscenaBotanico.farolDePopa(m)) l.noche else 0f,
    )

    /** Dónde está cada lumbre en el lienzo: la boca de la claraboya, la ventanilla de la bitácora, el farol. */
    val LUZ_CLARABOYA: Offset get() = SITIO_CLARABOYA.p(0f, 0.75f, 0f)
    val LUZ_BITACORA: Offset get() = SITIO_BITACORA.p(0f, 1.22f, -0.2f)

    // ---------------------------------------------------------------- Lo que se calcula una vez por cuadro

    /**
     * Todo lo que el cuadro de un momento comparte entre sus planos: la luz, las lumbres, la nieve y las manchas de sombra
     * de la cubierta (en metros), para saber qué cosa queda en penumbra y cuál al sol.
     */
    internal class Vista(val m: Momento) {
        val l: Luces = deNoche(EscenaBotanico.luces(m))
        val r: Rayo = rayoDelBarco(l)
        val sol = Sol3(r)
        val p = Pincel(l, r)
        val lu = lumbres(m, l)
        val nieve = EscenaBotanico.nieve(m)
        val velas = EscenaBotanico.velas(m)
        /** Cuánto alumbra la luna (de 0 a 1): de noche, sin nubes. */
        val luna: Float = (((l.estrellas - 0.3f) / 0.7f).coerceIn(0f, 1f) * (1f - l.nublado)) * l.noche

        /** Las manchas de sombra grandes de la cubierta, en metros: la de cada vela y la de la amurada que da la espalda al sol. */
        val manchas: List<List<Offset>> = if (r.fuerza < 0.05f) emptyList() else buildList {
            for (v in VelasBarco.velasQueDanSombra(m)) add(sombraEnCubierta(sol, v))
            sombraDeLaAmurada(sol, true)?.let { add(it) }
            sombraDeLaAmurada(sol, false)?.let { add(it) }
        }

        /** Cuánto queda en sombra (0 al sol, 1 en sombra) el punto ([x], [z]) de la cubierta, a [h] metros de alto. */
        fun sombraEn(x: Float, z: Float, h: Float = 0f): Float {
            if (manchas.isEmpty()) return 0f
            val q = alSuelo(sol, V3(x, h, z))
            return if (manchas.any { dentro(q.x, q.y, it) }) 1f else 0f
        }

        /**
         * El pincel de una cosa que está en ([x], [z]) de la cubierta, a [h] metros: al sol, el de la escena; dentro de la
         * sombra de las velas o de la amurada, en penumbra cálida (el lado de luz apagado, el color hacia la sombra de
         * color). De noche, si una lumbre la alumbra, con su calor y con la sombra tumbada lejos de ella.
         */
        fun pincel(x: Float, z: Float, h: Float = 0.4f): Pair<Pincel, Sol3> {
            val en = pc(x, h, z)
            calido(en)?.let { return it }
            val s = sombraEn(x, z, h)
            return if (s > 0f) penumbra to solPenumbra else p to sol
        }

        val penumbra: Pincel by lazy { Pincel(enPenumbra(l), Rayo(r.dia, r.lat, r.cot, r.tan, r.ax, r.ay, r.fuerza * 0.25f, r.color, r.contraste * 0.45f), 0f, C.LUMBRE) }
        val solPenumbra: Sol3 by lazy { Sol3(penumbra.r) }

        /** De noche, el pincel cálido de lo que está en [en] (en el lienzo), si una lumbre lo alumbra. */
        fun calido(en: Offset): Pair<Pincel, Sol3>? {
            if (l.noche < 0.3f) return null
            val fuentes = listOf(LUZ_CLARABOYA to lu.claraboya * 0.9f, LUZ_BITACORA to lu.bitacora * 0.75f, FAROL to lu.farol)
            var mejor: Pair<Offset, Float>? = null; var calor = 0f
            for ((q, f) in fuentes) { if (f <= 0.02f) continue; val d = (q - en).getDistance(); val kk = f / (1f + (d / 70f) * (d / 70f)); calor += kk; if (mejor == null || kk > mejor.second) mejor = q to kk }
            if (mejor == null || calor < 0.08f) return null
            val foco = Foco(mejor.first, C.LUMBRE, 1f, 70f, suelo = en.y)
            val rr = rayoDeFoco(foco, en, 0.4f, C.SOMBRA)
            return Pincel(l, rr, calor.coerceIn(0f, 0.85f), C.LUMBRE) to Sol3(rr)
        }
    }

    /**
     * La luz de fuera, con la noche más honda a bordo: así lo que alumbran la claraboya, la bitácora, el farol y la luna
     * destaca en cálido o en frío sobre la penumbra, como en el taller de Renacimiento, en vez de quedar todo de un gris
     * parejo. El cielo no cambia.
     */
    private fun deNoche(l: Luces): Luces {
        if (l.noche < 0.05f) return l
        val k = 1f - 0.32f * l.noche
        val amb = Color((l.ambiente.red * k * 0.94f).coerceIn(0f, 1f), (l.ambiente.green * k * 0.96f).coerceIn(0f, 1f), (l.ambiente.blue * k).coerceIn(0f, 1f))
        return Luces(alto = l.alto, medio = l.medio, bajo = l.bajo, ambiente = amb, bruma = l.bruma, brumaFuerza = l.brumaFuerza, solX = l.solX, solAltura = l.solAltura,
            solColor = l.solColor, solFuerza = l.solFuerza, lunaX = l.lunaX, lunaAltura = l.lunaAltura, estrellas = l.estrellas, noche = l.noche, nublado = l.nublado, eclipse = l.eclipse)
    }

    /** La luz de la penumbra: la sombra de color de debajo de las velas, que deja pasar algo de sol tibio por la lona. */
    private fun enPenumbra(l: Luces): Luces {
        val tibia = lerp(mult(l.ambiente, C.SOMBRA, 0.62f), Color(0xFFE8C9A8), 0.12f)
        return Luces(alto = l.alto, medio = l.medio, bajo = l.bajo, ambiente = tibia, bruma = l.bruma, brumaFuerza = l.brumaFuerza, solX = l.solX, solAltura = l.solAltura,
            solColor = l.solColor, solFuerza = l.solFuerza, lunaX = l.lunaX, lunaAltura = l.lunaAltura, estrellas = l.estrellas, noche = l.noche, nublado = l.nublado, eclipse = l.eclipse)
    }

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val v = Vista(m)
        with(MundoBarco) { mundo(v) }
        with(VelasBarco) { lejos(v); mayor(v) }
        with(CubiertaBarco) { cubierta(v) }
        if (m.ficha.clima == Clima.NIEBLA) with(MundoBarco) { nieblaSobreLaProa(v) }
        with(NocheBarco) { lumbresEnCubierta(v) }
        with(TallerBarco) { primerPlano(v) }
        with(VelasBarco) { marco(v) }
        with(NocheBarco) { lumbresEnLoAlto(v) }
        if (m.ficha.clima != Clima.NIEBLA) tiempoQuieto(v.l, m.ficha.clima, H, m.semilla("tiempo"))
        grano(Azar(Semilla.de("botanico", "grano", "barco")), 0.6f + 0.4f * v.r.dia)
    }

    // ================================================================ Lo que se mueve

    fun DrawScope.vivo(m: Momento, reloj: Float) { with(NocheBarco) { vivo(m, reloj) } }

    // ================================================================ El gato

    fun luzDelGato(m: Momento, en: Offset): LuzGato { val l = EscenaBotanico.luces(m); return LuzGato(rayoDelBarco(l), l.noche) { l.tono(it) } }

    /** En la cubierta de delante no hay nada plantado en el paso del gato (la mesa y los cajones pisan detrás). */
    fun estorbos(m: Momento): List<Float> = emptyList()

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, GATO_DESDE, PISO_GATO, hasta = GATO_HASTA))
        val mirador = miradorDeLosCajones(m)
        add(Ancla(TipoAncla.MIRADOR, mirador.x, mirador.y))                     // en lo alto de los cajones de popa
        add(Ancla(TipoAncla.OBJETO, SITIO_MESA.pie.x + 10f, PISO_GATO))
        add(Ancla(TipoAncla.ABRIGO, SITIO_MESA.pie.x - 22f, PISO_GATO))           // debajo de la mesa
        if (EscenaBotanico.fiesta(m)) add(Ancla(TipoAncla.ADORNO, 236f, 150f))
    }
}
