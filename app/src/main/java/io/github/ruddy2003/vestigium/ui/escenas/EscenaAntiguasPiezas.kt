package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas.Senal
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.pts
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * Lo que todos los planos del cuadro necesitan de un momento, calculado una vez: la luz (el sol o la luna, la sombra que
 * sube por el paso), la nieve, el camino, lo encendido y las señales del día. [ligera]: para la parte viva, que se
 * traza en cada cuadro, sin lo que solo pide la quieta.
 */
internal class VistaAntiguas(val m: Momento, ligera: Boolean = false) {
    val l = EscenaAntiguas.luces(m)
    val r: Rayo = LuzAntiguas.rayo(l)
    val sol = Sol3(r)
    val p = Pincel(l, r)
    val sombra = LuzAntiguas.sombra(l)
    val luna = LuzAntiguas.luna(l)
    val noche = l.noche
    val clima: Clima = m.ficha.clima
    val lugar = EscenaAntiguas.lugar(m)
    val camino = EscenaAntiguas.camino(m)
    val nieveArriba = EscenaAntiguas.nieveArriba(m)
    val nieveLadera = EscenaAntiguas.nieveLadera(m)
    val nieveAbajo = EscenaAntiguas.nieveAbajo(m)
    val invierno = EscenaAntiguas.caminoBorrado(m)
    val encendido = EscenaAntiguas.encendido(m)
    val senales: Set<Senal> = if (ligera) emptySet() else EscenaAntiguas.senales(m)

    /** La niebla, de 0 (nada) a 3 (cerrada): con niebla, 3; con nubes bajas, nieve o lluvia, algo entre los planos. */
    val niebla: Int = when (clima) {
        Clima.NIEBLA -> 3
        Clima.NIEVE -> 2
        Clima.LLUVIA, Clima.TORMENTA -> 1
        Clima.NUBES -> if (m.azar("bancos").real01() < 0.4f) 1 else 0
        Clima.DESPEJADO, Clima.VIENTO, Clima.POLVO, Clima.SIN_CLIMA -> 0
    }

    /** Cuánto de la sombra grande hay en [punto] (0 al sol, 1 en la sombra). */
    fun sombraEn(punto: Offset): Float = LuzAntiguas.sombraEn(sombra, punto)

    /** El pincel de lo que está en [punto]: sin sol si la sombra grande lo cubre; de noche, con la lumbre de la puerta si le llega. */
    fun pincelEn(punto: Offset, lumbre: Float = 0f): Pincel {
        val k = sombraEn(punto)
        val base = if (k <= 0.02f) p else Pincel(l, Rayo(r.dia, r.lat, r.cot, r.tan, r.ax, r.ay, r.fuerza * (1f - k), r.color, r.contraste * (1f - 0.55f * k)), 0f, p.lumbre)
        return if (lumbre > 0.01f) base.con(lumbre) else base
    }

    /** El sol que tumba las sombras arrojadas en [punto]: el de la vista, sin fuerza en la sombra grande. */
    fun rayoEn(punto: Offset): Rayo { val k = sombraEn(punto); return if (k <= 0.02f) r else Rayo(r.dia, r.lat, r.cot, r.tan, r.ax, r.ay, r.fuerza * (1f - k), r.color, r.contraste) }
}

/** Un número de 0 a 1 de un azar (sin chocar con el `real` de la biblioteca en este archivo). */
internal fun Azar.real01(): Float = entero(100000) / 100000f

/**
 * Los sitios fijos del cuadro, en el lienzo (380 × 285), puestos a mano. Los dibujos y las pruebas los comparten.
 */
internal object SitioAntiguas {
    /** El horizonte: la altura de los ojos (de pie en la plaza, delante de la puerta) y el borde de abajo del cielo. */
    const val H = 150f
    val MIRADA = Mirada(H, 560f)

    /** La muesca del collado y la cumbre alta, a la izquierda del centro. */
    val COLLADO = Offset(305f, 108.6f)
    val CUMBRE = Offset(150f, 20.2f)

    /**
     * La cresta lejana, de izquierda a derecha: sube despacio, con un hombro, de detrás de la ladera de las vetas a la cumbre
     * alta; baja más empinada por la derecha a otro hombro con su gendarme, y de ahí, larga, hasta el collado; vuelve a
     * subir por la otra cumbre, que se mete detrás del cuerpo alto de la posada. **[Diseño propio]**: no es un triángulo
     * en el eje del cuadro.
     */
    val CRESTA = pts(
        -6f, 74f, 10f, 70f, 24f, 64.6f, 38f, 60f, 50f, 54.4f, 60f, 50.6f, 70f, 49f, 80f, 47.4f, 90f, 42.6f, 100f, 37.4f, 110f, 33f,
        118f, 29.8f, 126f, 26.4f, 134f, 23.8f, 140f, 22f, 146f, 20.8f, 150f, 20.2f, 154f, 21.2f, 160f, 23.8f, 166f, 26.6f, 172f, 30.4f,
        178f, 34.2f, 184f, 37.4f, 190f, 39.8f, 196f, 41f, 204f, 42.2f, 212f, 44.2f, 217f, 42.2f, 221f, 43.6f, 226f, 48f, 234f, 53.2f,
        242f, 58.4f, 250f, 63.6f, 258f, 69.4f, 266f, 75.6f, 274f, 82.4f, 282f, 89.6f, 289f, 95.8f, 295f, 101f, 300f, 105.2f, 305f, 108.6f,
        309f, 107f, 314f, 103.2f, 320f, 97.6f, 326f, 91.2f, 333f, 84f, 340f, 77f, 348f, 70f, 358f, 63f, 368f, 57f, 380f, 51f, 388f, 48f,
    )

    /** El pie de la cara norte: el borde de arriba de la ladera del paso, de la garganta al collado. */
    val PIE_CARA = pts(
        166f, 214f, 167f, 198f, 170f, 182f, 175f, 167f, 181f, 153f, 189f, 141f, 199f, 131f, 211f, 123f, 225f, 117.4f, 240f, 113.6f,
        256f, 111.4f, 272f, 110f, 289f, 109.2f, 305f, 108.6f,
    )

    /** El pie de la otra cumbre: el borde de arriba a la derecha de la ladera del paso, del collado a detrás de la posada. */
    val PIE_DERECHA = pts(305f, 108.6f, 312f, 110.6f, 320f, 113.4f, 330f, 117f, 342f, 121f, 356f, 125f)

    /**
     * El camino, del cabo de allá del puente al collado: cinco tramos en zetas por la ladera de los alerces y la pedrera, y
     * el último, largo, por la cornisa. Las vueltas están en los puntos 1 a 4.
     */
    val ZETAS = pts(183f, 197.6f, 234f, 183.4f, 180f, 168.6f, 239f, 153f, 194f, 139.4f, 230f, 129.6f, 268f, 119.6f, 305f, 109.6f)

    /** El camino de este lado del torrente: de la puerta de la posada, por la plaza, al puente. */
    val BAJADA = pts(330f, 292f, 300f, 270f, 272f, 254f, 248f, 240f, 226f, 229f, 208f, 220f, 198f, 214.4f)

    /**
     * Las piedras marcadas, de abajo arriba, con su runa: la del puente («la primera»), las de las vueltas, las de la
     * cornisa y la última, antes del collado, con la runa de Arn medio tapada de liquen.
     */
    val PIEDRAS: List<Pair<Offset, Runa>> = listOf(
        Offset(181f, 197f) to Runa.CAMINO, Offset(210f, 189f) to Runa.AGUA, Offset(237f, 182f) to Runa.CAMINO, Offset(182f, 167.2f) to Runa.REFUGIO,
        Offset(242f, 152f) to Runa.PELIGRO, Offset(196f, 138.2f) to Runa.CAMINO, Offset(232f, 127.6f) to Runa.PELIGRO, Offset(268f, 118.4f) to Runa.CAMINO,
        Offset(300f, 110f) to Runa.CAMINO_QUE_SIGUE,
    )

    /** El refugio de la Piedra Partida, a media subida; el de la Cabra, en la cara norte; el de Arn, en el collado. */
    val PIEDRA_PARTIDA = Offset(207f, 172.6f)
    val CABRA = Offset(212f, 76f)
    val REFUGIO_ARN = Offset(287f, 110.6f)

    /** El borde de arriba de la ladera de las vetas, de detrás del pino a la garganta. */
    val VETAS = pts(
        -6f, 104f, 10f, 101f, 24f, 99.4f, 38f, 100.4f, 50f, 103f, 62f, 106.8f, 74f, 110f, 86f, 114.4f, 98f, 119.6f, 108f, 124.4f, 118f, 130f,
        128f, 136.6f, 138f, 143f, 146f, 150f, 151f, 157f, 155f, 165f, 157.4f, 173f, 158.8f, 181f, 159.8f, 190f, 160.4f, 199f, 161f, 208f,
    )

    /** El torrente: su eje, de detrás de la posada a la izquierda, con su medio ancho en cada punto. */
    val TORRENTE: List<Pair<Offset, Float>> = listOf(
        Offset(338f, 192.4f) to 2.8f, Offset(320f, 194.6f) to 3.2f, Offset(300f, 196f) to 3.6f, Offset(282f, 198.4f) to 3.8f, Offset(262f, 199f) to 4.2f,
        Offset(244f, 201.6f) to 4.4f, Offset(228f, 202.6f) to 4.8f, Offset(214f, 205f) to 5.2f, Offset(200f, 207.6f) to 5.4f, Offset(186f, 210f) to 5.8f,
        Offset(170f, 212.4f) to 6.2f, Offset(152f, 216.6f) to 6.6f, Offset(134f, 219.2f) to 7f, Offset(116f, 223.4f) to 7.3f, Offset(98f, 225.4f) to 7.6f,
        Offset(80f, 229.6f) to 8f, Offset(62f, 232.2f) to 8.4f, Offset(44f, 236.6f) to 8.8f, Offset(24f, 239.6f) to 9.2f, Offset(-8f, 246f) to 9.8f,
    )

    /** Los dos cabos del puente de troncos: el de este lado y el de allá. Cruza el torrente en diagonal, largo. */
    val PUENTE_ACA = Offset(199f, 215f)
    val PUENTE_ALLA = Offset(182f, 199.4f)

    /** La piedra de la plaza donde suena el cuerno. */
    val PIEDRA_DEL_CUERNO = Offset(226f, 233f)

    /** La altura del torrente en [x] (su eje). */
    fun torrenteEn(x: Float): Float = TrazosValle.alturaEn(TORRENTE.map { it.first }.reversed(), x)

    /** Un azar fijo del cuadro (lo que no cambia de un día a otro). */
    fun fijo(vararg partes: Any): Azar = Azar(Semilla.de("antiguas", *partes))
}

/**
 * La fachada de la posada, en tres cuartos, vista desde la plaza: una cámara de agujero (la misma
 * [SitioAntiguas.MIRADA]) y un muro recto que mira a la plaza y se aleja hacia la izquierda con un giro de 33° respecto de
 * la mirada. Los puntos del muro se dan con [s], los metros a lo largo del muro (0 en la jamba de la derecha de la puerta,
 * positivo hacia la izquierda, que se aleja), [h], la altura, y [fuera], los metros por delante del muro (hacia la plaza).
 * Los ojos van a [OJO] metros del suelo.
 *
 * La posada tiene dos cuerpos **[Diseño propio]**: a la izquierda, el zaguán de una planta, con la puerta, de [S_ALTO] a
 * [S_FIN], con su tejado de una sola agua que baja hacia la izquierda; a la derecha, el cuerpo alto (la planta de piedra y
 * el piso de tablas del cuarto alto) que sale por el borde. Así, por encima del collado (donde salen el sol y la luna) no
 * hay nada de la posada: lo alto empieza a la derecha de la muesca.
 */
internal object FachadaPosada {
    const val OJO = 2f
    private const val SEN = 0.5446f
    private const val COS = 0.8387f

    /** A qué distancia y a qué lado de la mirada está la jamba de la derecha de la puerta, a ras de suelo (en metros). */
    const val Z0 = 10.6f
    private val X0 get() = (338f - SitioAntiguas.MIRADA.centro) * Z0 / SitioAntiguas.MIRADA.focal

    /** La esquina de la izquierda del cuerpo alto y la del zaguán, a lo largo del muro. */
    const val S_ALTO = -0.24f
    const val S_FIN = 2.25f

    /** El alero del zaguán junto al cuerpo alto y en su esquina; el del cuerpo alto. */
    const val ALERO_ZAGUAN = 2.62f
    const val ALERO_PUNTA = 2.32f
    const val ALERO_ALTO = 4.3f

    /** La altura del borde del tejado del zaguán en [s] (baja hacia la esquina de la izquierda). */
    fun aleroEn(s: Float): Float = ALERO_ZAGUAN + (ALERO_PUNTA - ALERO_ZAGUAN) * ((s - S_ALTO) / (S_FIN - S_ALTO)).coerceIn(0f, 1f)

    private val F get() = SitioAntiguas.MIRADA.focal
    private val C get() = SitioAntiguas.MIRADA.centro

    private fun xm(s: Float, fuera: Float): Float = X0 - s * SEN - fuera * COS
    private fun zm(s: Float, fuera: Float): Float = Z0 + s * COS - fuera * SEN

    /** Un punto del muro: a [s] metros a lo largo, [h] de alto y [fuera] metros por delante. */
    fun p(s: Float, h: Float, fuera: Float = 0f): Offset { val z = zm(s, fuera); return Offset(C + F * xm(s, fuera) / z, SitioAntiguas.H - F * (h - OJO) / z) }

    /** Cuántas unidades del lienzo mide un metro en ese punto. */
    fun escala(s: Float, fuera: Float = 0f): Float = F / zm(s, fuera)

    /** La normal de la cara del muro (mira a la plaza: a la izquierda y hacia quien mira) y la de los cantos que se ven (a la derecha). */
    val CARA = V3(-COS, 0f, -SEN)
    val CANTO = V3(SEN, 0f, -COS)

    /** La forma de un rectángulo del muro (de [s0] a [s1] y de [h0] a [h1]), [fuera] metros por delante de él. */
    fun cara(s0: Float, s1: Float, h0: Float, h1: Float, fuera: Float = 0f): Path = poligono(p(s0, h0, fuera), p(s1, h0, fuera), p(s1, h1, fuera), p(s0, h1, fuera))

    /** La forma de un rectángulo perpendicular al muro en [s] (los que se ven miran a la derecha), de [f0] a [f1] metros hacia fuera. */
    fun canto(s: Float, f0: Float, f1: Float, h0: Float, h1: Float): Path = poligono(p(s, h0, f0), p(s, h0, f1), p(s, h1, f1), p(s, h1, f0))

    /** La forma de una tapa horizontal a la altura [h], de [s0] a [s1] y de [f0] a [f1] metros hacia fuera. */
    fun tapa(s0: Float, s1: Float, f0: Float, f1: Float, h: Float): Path = poligono(p(s0, h, f0), p(s1, h, f0), p(s1, h, f1), p(s0, h, f1))

    /** A cuántos metros de los ojos está ese punto (para ordenar y para las sombras). */
    fun hondo(s: Float, fuera: Float = 0f): Float = zm(s, fuera)

    /** Dónde toca el suelo el muro en la columna [x] del lienzo; null si en esa columna no hay posada delante. */
    fun baseEn(x: Float): Float? {
        if (x < p(S_FIN, 0f).x - 4f) return null
        var a = S_FIN; var b = -3f
        repeat(18) { val m = (a + b) / 2f; if (p(m, 0f).x < x) a = m else b = m }
        return p((a + b) / 2f, 0f).y
    }
}

/**
 * Las runas del paso: marcas inventadas de pocos trazos rectos, talladas, que no son letras de ningún alfabeto ni las de
 * ninguna obra **[Diseño propio]**. El cuento nombra cuatro («agua, peligro, refugio», y la de «camino», con «una raya más
 * que no termina» para «camino que sigue»). Nunca brillan: son talladas, y se leen por la sombra del surco.
 *
 * Se comprobaron una por una, de memoria **[sin verificar contra una tabla]**, contra las runas del futhark antiguo y del
 * joven (con sus variantes de rama corta), las letras latinas y griegas, el ogam y las cifras, en sus cuatro vueltas y en
 * espejo (la hoja `r3_antiguas_runas.jpg` las dibuja así). Lo que las aparta de las letras: las de palo llevan sus ramas a
 * los dos lados, a alturas distintas y hacia el mismo lado (las runas que llevan una rama a cada lado las sacan del mismo
 * punto, o de las dos puntas del palo y hacia lados contrarios); las otras son rayas
 * sueltas de largo y ángulo distintos, sin dos paralelas (no son palotes ni ogam) y sin esquinas ni cruces que se adivinen
 * (no forman una T, una L, una Y, una π ni un 7). La de «camino que sigue» es la misma marca que lleva la banderita
 * (`BanderitaAntiguas.MARCA`, llevada a estas medidas): un palo con una raya corta que sube a cada lado, a alturas
 * distintas, y la de abajo larga, que se va hasta el borde de la piedra; la de «camino» es esa misma sin la raya larga, y
 * la de «agua», la de camino con las ramas hacia abajo (el agua baja, el camino sube).
 */
internal enum class Runa(val trazos: List<Pair<Offset, Offset>>) {
    /** Un palo con una raya corta que baja a cada lado, a alturas distintas (la de la izquierda más arriba): el agua que baja. */
    AGUA(listOf(Offset(0f, -0.65f) to Offset(0f, 0.65f), Offset(0f, -0.36f) to Offset(-0.33f, -0.08f), Offset(0f, 0.12f) to Offset(0.33f, 0.4f))),
    /** Una raya larga que sube, el tejado de una sola agua, y debajo, suelta y corta, la losa del hogar: el abrigo. */
    REFUGIO(listOf(Offset(-0.62f, 0.05f) to Offset(0.62f, -0.5f), Offset(-0.1f, 0.5f) to Offset(0.4f, 0.5f))),
    /** Una raya larga que baja, la piedra que rueda, y dos esquirlas sueltas que saltan a los lados: peligro. */
    PELIGRO(listOf(Offset(-0.5f, -0.62f) to Offset(0.3f, 0.45f), Offset(0.18f, -0.3f) to Offset(0.58f, -0.36f), Offset(-0.62f, 0.58f) to Offset(-0.4f, 0.32f))),
    /** Un palo con una raya corta que sube a cada lado, a alturas distintas (la de la derecha más arriba): el camino en zetas. */
    CAMINO(listOf(Offset(0f, -0.65f) to Offset(0f, 0.65f), Offset(0f, -0.337f) to Offset(0.329f, -0.572f), Offset(0f, 0.024f) to Offset(-0.329f, -0.211f))),
    /** La de camino, «con una raya más que no termina»: la de abajo, larga, sube a la derecha hasta el borde de la piedra. */
    CAMINO_QUE_SIGUE(CAMINO.trazos + listOf(Offset(0f, 0.415f) to Offset(0.955f, 0.039f))),
}

internal object PiezasAntiguas {
    private val C = ColorAntiguas

    /**
     * Una runa tallada en [centro] con [alto] unidades: el surco en sombra y su labio de luz del lado del sol. [fresca], de
     * 0 a 1: recién repasada (el surco claro, la piedra viva); [gastada], de 0 a 1: la que ya casi no se lee.
     */
    fun DrawScope.runa(p: Pincel, runa: Runa, centro: Offset, alto: Float, piedra: Color, lejos: Float = 0f, fresca: Float = 0f, gastada: Float = 0f) {
        val g = (alto * 0.13f).coerceAtLeast(0.35f)
        val surco = lerp(p.sombra(lerp(piedra, C.ESCORIA, 0.45f), lejos), p.luz(lerp(piedra, C.FRESNO, 0.4f), lejos), 0.55f * fresca).copy(alpha = 0.9f * (1f - 0.7f * gastada))
        val labio = p.luz(lerp(piedra, C.NIEVE, 0.3f), lejos).copy(alpha = (0.55f * p.r.dia * (1f - gastada)).coerceIn(0f, 1f))
        val lado = if (p.solALaDerecha) 1f else -1f
        for ((a, b) in runa.trazos) {
            val pa = centro + a * alto * 0.5f; val pb = centro + b * alto * 0.5f
            drawLine(labio, pa + Offset(lado * g * 0.6f, g * 0.4f), pb + Offset(lado * g * 0.6f, g * 0.4f), g * 0.7f, StrokeCap.Round)
            drawLine(surco, pa, pb, g, StrokeCap.Round)
        }
    }

    /**
     * Una linterna de mina colgada de [cuelga] por un cordel de [largo]: de hierro y de cuerno raspado «tan fino que deja
     * pasar la luz», con su tapa, su asa y su base. [luz], de 0 a 1: encendida, el cuerno se enciende de ámbar y alumbra lo de
     * alrededor. [k] es su tamaño (1 ≈ 9 unidades de alto). Devuelve el centro, para el halo de la parte viva.
     */
    fun DrawScope.linterna(p: Pincel, cuelga: Offset, largo: Float, k: Float, luz: Float, lejos: Float = 0f, giro: Float = 0f, nieve: Float = 0f): Offset {
        val arriba = cuelga + Offset(giro * largo * 0.1f, largo)
        val hierro = C.ESCORIA
        drawLine(p.medio(lerp(C.CUERO, C.ESCORIA, 0.4f), lejos), cuelga, arriba, 0.45f * k, StrokeCap.Round)
        val w = 3.2f * k; val h = 5.6f * k
        val cuerpo = poligono(arriba + Offset(-w * 0.42f, 1.6f * k), arriba + Offset(w * 0.42f, 1.6f * k), arriba + Offset(w * 0.5f, 1.6f * k + h), arriba + Offset(-w * 0.5f, 1.6f * k + h))
        val ambar = lerp(C.ORO, C.LANA, 0.35f)
        if (luz > 0.02f) {
            drawPath(cuerpo, Brush.verticalGradient(listOf(lerp(p.medio(ambar, lejos), Color(0xFFFFE2A0), luz), lerp(p.medio(ambar, lejos), C.LUMBRE, luz)), arriba.y, arriba.y + h + 2f * k))
            resplandor(arriba + Offset(0f, 1.6f * k + h * 0.5f), 7f * k * (0.6f + 0.4f * luz), 0.85f * luz, C.LUMBRE)
        } else {
            drawPath(cuerpo, Brush.horizontalGradient(listOf(p.luz(ambar, lejos), p.sombra(lerp(ambar, C.CUERO, 0.4f), lejos)), arriba.x - w * 0.5f, arriba.x + w * 0.5f))
        }
        // El armazón: las varillas, la tapa en cono con su asa y la base.
        val varilla = p.medio(hierro, lejos)
        for (dx in listOf(-0.46f, 0f, 0.46f)) drawLine(varilla.copy(alpha = if (dx == 0f) 0.5f else 0.9f), arriba + Offset(w * dx * 0.92f, 1.6f * k), arriba + Offset(w * dx, 1.6f * k + h), 0.35f * k)
        drawPath(poligono(arriba + Offset(-w * 0.55f, 1.8f * k), arriba + Offset(w * 0.55f, 1.8f * k), arriba + Offset(w * 0.18f, 0f), arriba + Offset(-w * 0.18f, 0f)), p.por(hierro, 0.2f, lejos))
        drawCircle(varilla, 0.7f * k, arriba + Offset(0f, -0.4f * k), style = Stroke(0.3f * k))
        drawRect(p.por(hierro, -0.2f, lejos), arriba + Offset(-w * 0.56f, 1.6f * k + h), androidx.compose.ui.geometry.Size(w * 1.12f, 0.9f * k))
        if (nieve > 0.2f) drawPath(poligono(arriba + Offset(-w * 0.5f, 1.5f * k), arriba + Offset(w * 0.5f, 1.5f * k), arriba + Offset(w * 0.2f, 0.2f * k), arriba + Offset(-w * 0.2f, 0.2f * k)), p.luz(C.NIEVE, lejos).copy(alpha = nieve))
        if (luz > 0.02f) drawCircle(Color(0xFFFFF0C8).copy(alpha = 0.7f * luz), 0.9f * k, arriba + Offset(0f, 1.6f * k + h * 0.55f), blendMode = BlendMode.Plus)
        return arriba + Offset(0f, 1.6f * k + h * 0.5f)
    }

    /**
     * La nieve que no lo cubre todo (en primavera y en otoño): de lo alto ([arriba], en cada columna) a [limite], un manto
     * opaco con el borde de abajo festoneado; por debajo, manchas sueltas, cada vez más raras, con su sombra azul debajo
     * (lo que queda en las vaguadas y a la sombra). Nada de velos: la nieve es blanca o no está.
     */
    fun DrawScope.manchasDeNieve(p: Pincel, x0: Float, x1: Float, arriba: (Float) -> Float, limite: (Float) -> Float, az: Azar, lejos: Float, talla: Float = 1f) {
        val nieve = p.por(C.NIEVE, 0.3f, lejos); val sombra = p.sombra(C.NIEVE_SOMBRA, lejos)
        val paso = 5f
        val borde = ArrayList<Offset>(); var x = x0
        // El borde: lenguas desiguales que bajan por las vaguadas y entrantes donde asoma la roca, sin dientes iguales.
        var lengua = 0f
        while (x <= x1) {
            if (az.real01() < 0.18f) lengua = az.entre(5f, 14f) * talla
            borde += Offset(x, limite(x) + lengua + az.entre(-1.2f, 1.2f)); lengua *= 0.55f; x += paso
        }
        val techo = borde.map { Offset(it.x, minOf(arriba(it.x) - 3f, it.y)) }
        drawPath(poligono(techo + borde.reversed()), nieve)
        drawPath(io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva(borde), sombra.copy(alpha = 0.55f), style = Stroke(1.1f * talla))
        repeat((80 * talla).toInt().coerceAtLeast(30)) {
            val xx = az.entre(x0, x1); val hondo = az.real01()
            val q = Offset(xx, limite(xx) + 2f + hondo * hondo * 34f * talla)
            val w = az.entre(1.5f, 5f) * talla * (1f - 0.5f * hondo)
            drawPath(TrazosValle.blando(q, w, w * 0.32f, az, 7, 0.25f), nieve)
            drawLine(sombra.copy(alpha = 0.5f), q + Offset(-w * 0.7f, w * 0.3f), q + Offset(w * 0.7f, w * 0.3f), 0.6f * talla)
        }
    }

    /** Una lucecita lejana (una ventana, una linterna en la ladera): un punto cálido con su halo. */
    fun DrawScope.lucecita(c: Offset, r: Float, fuerza: Float, color: Color = C.LUMBRE) {
        if (fuerza <= 0.02f) return
        resplandor(c, r * 4.5f, 0.55f * fuerza, color)
        drawCircle(lerp(color, Color(0xFFFFF0C8), 0.45f).copy(alpha = fuerza.coerceIn(0f, 1f)), r, c)
    }
}
