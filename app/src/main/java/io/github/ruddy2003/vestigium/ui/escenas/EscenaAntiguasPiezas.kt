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
    /** El horizonte: la altura de los ojos (de pie en el poyo) y el borde de abajo del cielo. */
    const val H = 150f
    val MIRADA = Mirada(H, 560f)

    /** La muesca del collado y la cumbre alta, la de la izquierda. */
    val COLLADO = Offset(305f, 108.6f)
    val CUMBRE = Offset(196f, 23.8f)

    /**
     * La cresta lejana, de izquierda a derecha: sube por detrás de la ladera de las vetas a la cumbre alta, baja larga por
     * la cara norte hasta el collado y vuelve a subir, empinada, por la otra cumbre, que sale por el borde por encima del
     * alero de la posada.
     */
    val CRESTA = pts(
        -6f, 98f, 8f, 94f, 20f, 90.4f, 30f, 86f, 40f, 80.6f, 50f, 76.2f, 58f, 72.6f, 66f, 70.2f, 74f, 71f, 82f, 68f, 90f, 63.4f, 98f, 60.2f,
        106f, 56.2f, 114f, 53.2f, 122f, 50.6f, 130f, 47f, 138f, 44.6f, 146f, 40.6f, 154f, 37.2f, 162f, 34.6f, 170f, 31.2f, 178f, 28.6f,
        185f, 26f, 191f, 24.6f, 196f, 23.8f, 201f, 24.8f, 206f, 26.6f, 212f, 29.6f, 218f, 33.4f, 225f, 37.2f, 232f, 41.8f, 239f, 46.6f,
        246f, 51f, 252f, 56.4f, 258f, 61f, 264f, 66.6f, 270f, 71.6f, 276f, 77.6f, 282f, 84f, 287f, 90f, 291f, 95f, 295f, 100f, 299f, 104.6f,
        302f, 107.4f, 305f, 108.6f, 308f, 107.6f, 311f, 104.8f, 315f, 100.8f, 319f, 96f, 324f, 89.6f, 330f, 82f, 337f, 73f, 344f, 64.6f,
        351f, 57f, 358f, 50f, 365f, 44.4f, 373f, 39f, 380f, 35f, 388f, 32f,
    )

    /** El pie de la cara norte: el borde de arriba a la izquierda de la ladera del paso, de la garganta al collado. */
    val PIE_CARA = pts(
        166f, 210f, 171f, 198f, 177f, 187f, 185f, 177f, 194f, 168f, 204f, 160f, 215f, 152f, 226f, 145f, 237f, 139f, 248f, 133f, 258f, 128f,
        267f, 123.4f, 275f, 119.6f, 282f, 116.4f, 288f, 113.8f, 294f, 111.6f, 299f, 110f, 305f, 108.6f,
    )

    /** El pie de la otra cumbre: el borde de arriba a la derecha de la ladera del paso, del collado a la esquina de la posada. */
    val PIE_DERECHA = pts(305f, 108.6f, 310f, 110.8f, 315f, 113.6f, 320f, 117f, 326f, 121f, 332f, 125f, 342f, 131f)

    /**
     * El camino, de la punta del puente al collado: cinco tramos en zetas por la ladera de los alerces, la pedrera y la
     * cornisa. Las vueltas están en los puntos 2, 4, 6 y 8.
     */
    val ZETAS = pts(211f, 196f, 248f, 189.6f, 293f, 180f, 262f, 172.4f, 226f, 162.4f, 263f, 153.4f, 301f, 144f, 278f, 137f, 262f, 129.4f, 288f, 120.4f, 300f, 114f, 305f, 109.4f)

    /** El camino de este lado del torrente: de la puerta de la posada, por la plaza, al puente. */
    val BAJADA = pts(372f, 292f, 340f, 276f, 306f, 260f, 280f, 247f, 258f, 236f, 240f, 226f, 228f, 218f, 221f, 212f)

    /**
     * Las piedras marcadas, de abajo arriba, con su runa: la del puente («la primera»), las de las vueltas y la última,
     * antes del collado, con la runa de Arn medio tapada de liquen.
     */
    val PIEDRAS: List<Pair<Offset, Runa>> = listOf(
        Offset(204f, 194f) to Runa.CAMINO, Offset(270f, 185.4f) to Runa.AGUA, Offset(297f, 178.4f) to Runa.CAMINO, Offset(242f, 166.6f) to Runa.REFUGIO,
        Offset(222f, 160.6f) to Runa.CAMINO, Offset(304f, 142.6f) to Runa.PELIGRO, Offset(259f, 128.2f) to Runa.CAMINO, Offset(284f, 120.4f) to Runa.PELIGRO,
        Offset(299f, 112.6f) to Runa.CAMINO_QUE_SIGUE,
    )

    /** El refugio de la Piedra Partida, a media subida; el de la Cabra, en la cara norte; el de Arn, en el collado. */
    val PIEDRA_PARTIDA = Offset(240f, 170f)
    val CABRA = Offset(236f, 84f)
    val REFUGIO_ARN = Offset(311f, 109.4f)

    /** El borde de arriba de la ladera de las vetas, de detrás del pino a la garganta. */
    val VETAS = pts(
        -6f, 104f, 10f, 101f, 24f, 99.4f, 38f, 100.4f, 50f, 103f, 62f, 106.8f, 74f, 110f, 86f, 114.4f, 98f, 119.6f, 108f, 124.4f, 118f, 130f,
        128f, 136.6f, 138f, 143f, 148f, 150f, 156f, 156.6f, 164f, 163.4f, 171f, 170.6f, 176f, 178f, 180f, 186f, 183f, 195f, 185f, 206f,
    )

    /** El torrente: su eje, de detrás de la esquina de la posada a la izquierda, con su medio ancho en cada punto. */
    val TORRENTE: List<Pair<Offset, Float>> = listOf(
        Offset(338f, 192.4f) to 2.8f, Offset(320f, 194.6f) to 3.2f, Offset(300f, 196f) to 3.6f, Offset(282f, 198.4f) to 3.8f, Offset(262f, 198.8f) to 4.2f,
        Offset(244f, 201.6f) to 4.4f, Offset(228f, 202f) to 4.8f, Offset(214f, 204.4f) to 5.2f, Offset(200f, 207.6f) to 5.4f, Offset(186f, 210f) to 5.8f,
        Offset(170f, 212.4f) to 6.2f, Offset(152f, 216.6f) to 6.6f, Offset(134f, 219.2f) to 7f, Offset(116f, 223.4f) to 7.3f, Offset(98f, 225.4f) to 7.6f,
        Offset(80f, 229.6f) to 8f, Offset(62f, 232.2f) to 8.4f, Offset(44f, 236.6f) to 8.8f, Offset(24f, 239.6f) to 9.2f, Offset(-8f, 246f) to 9.8f,
    )

    /** Los dos extremos del puente de troncos: el de este lado y el de allá. */
    val PUENTE_ACA = Offset(222f, 212.6f)
    val PUENTE_ALLA = Offset(210f, 196.4f)

    /** La piedra de la plaza donde suena el cuerno. */
    val PIEDRA_DEL_CUERNO = Offset(262f, 228f)

    /** La altura del torrente en [x] (su eje). */
    fun torrenteEn(x: Float): Float = TrazosValle.alturaEn(TORRENTE.map { it.first }.reversed(), x)

    /** Un azar fijo del cuadro (lo que no cambia de un día a otro). */
    fun fijo(vararg partes: Any): Azar = Azar(Semilla.de("antiguas", *partes))
}

/**
 * La fachada de la posada vista de pie en el poyo de su puerta, a lo largo, en fuga hacia la izquierda: una cámara de
 * agujero (la misma [SitioAntiguas.MIRADA]) y un muro recto que pasa a [A] metros a la derecha de los ojos y se aleja con
 * un giro de [B] (la tangente del ángulo con la mirada). [z] es la distancia en metros (de 9.2, el borde derecho del
 * cuadro, a 16, la esquina) y [h], la altura sobre el suelo del muro, también en metros; los ojos van a [OJO] metros.
 * **[Diseño propio]**: con la posada tan cerca y el collado en el centro, el muro solo cabe así, casi a lo largo de la
 * mirada; la puerta se ve de canto y lo que da la cara es la piedra de su jamba, la de los años.
 */
internal object FachadaPosada {
    const val A = 1.943f
    const val B = 0.1286f
    const val OJO = 2f
    const val Z_BORDE = 9.22f
    const val Z_ESQUINA = 16f
    const val ALERO = 3.5f

    private val F get() = SitioAntiguas.MIRADA.focal
    private val C get() = SitioAntiguas.MIRADA.centro

    /** Un punto del muro: a [z] metros de los ojos, [h] de alto y [fuera] metros por delante del muro (hacia la plaza). */
    fun p(z: Float, h: Float, fuera: Float = 0f): Offset = Offset(C + F * ((A + B * z - fuera) / z), SitioAntiguas.H - F * (h - OJO) / z)

    /** Cuántas unidades del lienzo mide un metro a la distancia [z]. */
    fun escala(z: Float): Float = F / z

    /** La forma de un rectángulo del muro (de [z0] a [z1] y de [h0] a [h1]), [fuera] metros por delante de él. */
    fun cara(z0: Float, z1: Float, h0: Float, h1: Float, fuera: Float = 0f): Path = poligono(p(z0, h0, fuera), p(z1, h0, fuera), p(z1, h1, fuera), p(z0, h1, fuera))

    /** La forma de un rectángulo perpendicular al muro, a la distancia [z] (la cara que mira a quien mira), de [d0] a [d1] metros hacia fuera. */
    fun canto(z: Float, d0: Float, d1: Float, h0: Float, h1: Float): Path = poligono(p(z, h0, d0), p(z, h0, d1), p(z, h1, d1), p(z, h1, d0))

    /** La forma de una tapa horizontal a la altura [h], de [z0] a [z1] y de [d0] a [d1] metros hacia fuera. */
    fun tapa(z0: Float, z1: Float, d0: Float, d1: Float, h: Float): Path = poligono(p(z0, h, d0), p(z1, h, d0), p(z1, h, d1), p(z0, h, d1))

    /** A qué distancia está el muro en la columna [x] del lienzo (para el muro que se ve en esa columna). */
    fun zEn(x: Float): Float = A / ((x - C) / F - B)
}

/**
 * Las runas del paso: marcas inventadas de pocos trazos rectos, talladas, que no son letras de ningún alfabeto ni las de
 * ninguna obra **[Diseño propio]**. El cuento nombra cuatro («agua, peligro, refugio», y la de «camino», con «una raya más
 * que no termina» para «camino que sigue»). Nunca brillan: son talladas, y se leen por la sombra del surco.
 */
internal enum class Runa(val trazos: List<Pair<Offset, Offset>>) {
    /** Tres rayas cortas tendidas, una debajo de otra y corridas, como agua que baja en escalones. */
    AGUA(listOf(Offset(-0.5f, -0.6f) to Offset(0.2f, -0.6f), Offset(-0.3f, -0.05f) to Offset(0.4f, -0.05f), Offset(-0.1f, 0.5f) to Offset(0.6f, 0.5f))),
    /** Un tejado abierto con un palo dentro: el refugio. */
    REFUGIO(listOf(Offset(-0.6f, 0.2f) to Offset(0f, -0.6f), Offset(0f, -0.6f) to Offset(0.6f, 0.2f), Offset(0f, -0.15f) to Offset(0f, 0.65f))),
    /** Una raya tendida con dos patas cortas abajo y un pico arriba en medio: peligro. */
    PELIGRO(listOf(Offset(-0.6f, -0.1f) to Offset(0.6f, -0.1f), Offset(-0.45f, -0.1f) to Offset(-0.55f, 0.55f), Offset(0.45f, -0.1f) to Offset(0.55f, 0.55f), Offset(0f, -0.1f) to Offset(0.12f, -0.7f))),
    /** Un palo con dos rayas que salen hacia delante y suben: el camino. */
    CAMINO(listOf(Offset(-0.35f, 0.65f) to Offset(-0.35f, -0.6f), Offset(-0.35f, 0.1f) to Offset(0.4f, -0.2f), Offset(-0.35f, -0.35f) to Offset(0.4f, -0.65f))),
    /** La de camino, «con una raya más que no termina»: la de abajo se va hasta el borde de la piedra. */
    CAMINO_QUE_SIGUE(CAMINO.trazos + listOf(Offset(-0.35f, 0.55f) to Offset(0.75f, 0.2f))),
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

    /** Una lucecita lejana (una ventana, una linterna en la ladera): un punto cálido con su halo. */
    fun DrawScope.lucecita(c: Offset, r: Float, fuerza: Float, color: Color = C.LUMBRE) {
        if (fuerza <= 0.02f) return
        resplandor(c, r * 4.5f, 0.55f * fuerza, color)
        drawCircle(lerp(color, Color(0xFFFFF0C8), 0.45f).copy(alpha = fuerza.coerceIn(0f, 1f)), r, c)
    }
}
