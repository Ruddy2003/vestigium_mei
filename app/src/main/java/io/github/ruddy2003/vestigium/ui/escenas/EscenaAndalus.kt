package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Casa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LLAMA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LUZ_DE_CASA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Techo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.agua
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.bajio
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.banderines
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.brasas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.briznas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.casa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cipres
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.estela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.guirnalda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hierbaDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.muralla
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.olivo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.pano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraDeFollaje
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Al-Ándalus: Córdoba vista desde el patio de la casa de Zaynab (tejedora de seda, del 970 al 974).
 * **[Aproximación]**: es una composición con lo que había allí, no la vista desde un punto real. Se mira hacia el río:
 * la sierra, de la que habla el cuento, queda a la espalda de quien mira.
 *
 * - **Al fondo:** las lomas de la campiña, con sus olivares y una alquería; la otra orilla con su soto.
 * - **En medio:** el Guadalquivir, con el puente de piedra de los romanos, la azuda con su molino y una noria en esta
 *   orilla; la muralla del río; la mezquita grande, con su alminar y sus naves de tejado; y el caserío, blanco, que
 *   baja en tres hileras hasta la tapia, con sus azoteas, las cúpulas de un baño, palmeras y el alminar del barrio.
 * - **Delante:** el patio, visto un poco desde arriba. A la izquierda, el muro de la sala, encalado y con el zócalo de
 *   almagra, y en él la puerta de arco de herradura con sus dovelas rojas y blancas; arrimado a él, el telar. A la
 *   derecha, la tapia baja por encima de la cual se ve la ciudad. En el suelo de losas, la alberca con su pila de
 *   mármol; la estera con la mesa baja (el objeto del día va encima), los cojines y el anafe; macetas, una tinaja, el
 *   jazmín que trepa por la esquina y el naranjo, cuyo tronco cruza el cuadro y cuya copa lo enmarca por arriba.
 *
 * Cómo está pintado: como `EscenaHeleno`, que es la vara (`docs/diseno/ESCENAS.md`): la luz viene del lado del sol y
 * todo arroja su sombra; el río es hondo lejos y claro en la orilla y refleja el puente; la alberca refleja el cielo, la
 * tapia y la pila; encima de todo va el grano.
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG4.kt`):
 * - **La tela** (arco `tela`): en el telar, la urdimbre sola al empezar; después, la tela tejida, que se va enrollando
 *   en el enjulio de delante mientras el de atrás se vacía. Terminada y entregada, el telar queda desnudo. La del
 *   califa (temporadas 1 y 2) es carmesí; la de la boda (de la 3 a la 5), azul. Las dos, con su banda de oro.
 * - **El río** (arco `rio`): crecido y pardo en invierno, lamiendo los arcos del puente; bajo en verano, con islas de
 *   grava y juncos.
 * - **El Ramadán** (estado `ramadan`, clave `lamparas`): de día no se enciende el anafe; al caer la tarde se pone la
 *   mesa en el patio y de noche arden las lámparas del patio, las del alminar y media ciudad.
 * - **Las fiestas y los duelos** (claves `fiesta` y `luto`): colgaduras de seda en la tapia, una guirnalda de arrayán
 *   en la puerta y banderines; un paño blanco, sin teñir, sobre la tapia y el telar tapado.
 * - **La guerra** (clave `guerra`): las lanzas y los estandartes blancos de la hueste, que cruza el puente.
 * - **La seda nueva** (clave `cosecha`): las madejas amarillas tendidas a secar.
 * - **La nieve** (clave `nieve`, o el tiempo): los tejados, la tapia y la copa del naranjo, blancos.
 * - **El objeto del día:** sobre la mesa baja; el laúd, apoyado en ella; la jarra, colgada del naranjo.
 * - **El tramo del día:** la esterilla tendida a la hora de rezar; el anafe encendido con su olla a la hora de comer;
 *   la bandeja con los vasos cuando hay gente; el candil del nicho al anochecer; todo apagado mientras duerme.
 */
internal object EscenaAndalus : EscenaVida {
    override val vidas = listOf("cordobesa")

    // La vista de la ciudad está dibujada en sus propias medidas (las de [HV], con el lomo de la tapia en 176 y la
    // esquina del muro en 184) y se pone en el cuadro agrandada [KV] veces: así se puede acercar o alejar entera.
    private const val KV = 1.34f
    private const val HV = 118f

    // El muro de la sala, a la izquierda: hasta dónde llega y dónde está su alero.
    internal const val ESQUINA = 124f
    internal const val ALERO = 76f

    // La tapia: su lomo, dónde empieza la cara y dónde toca el suelo.
    internal const val CAP = 182f
    internal const val CARA = 188f
    internal const val BASE = 230f

    /** Dónde está el horizonte (el pie de las lomas), ya en el cuadro. */
    private const val H = CAP + (HV - 176f) * KV

    /** Un punto de la vista, en el cuadro. */
    internal fun enLaVista(p: Offset) = Offset(ESQUINA + (p.x - 184f) * KV, CAP + (p.y - 176f) * KV)

    // La puerta de la sala: el centro de su arco, su radio y la mitad del ancho del vano.
    internal val ARCO = Offset(60f, 156f)
    private const val RADIO = 27f
    private const val VANO = 22f

    // Dónde están las cosas del patio que también usa la parte viva.
    internal val ANAFE = Offset(192f, 283f)
    internal val MESA = Offset(94f, 249f)
    internal val NICHO = Offset(112f, 152f)
    internal val PILA = Offset(168f, 241f)

    // ---- La paleta.
    internal val CAL = Color(0xFFF2EADA)
    internal val ALMAGRA = Color(0xFFA9482F)
    internal val LADRILLO = Color(0xFFC98458)
    internal val TEJA = Color(0xFFBC6A40)
    internal val PIEDRA = Color(0xFFDCC38F)
    internal val MARMOL = Color(0xFFEFE8DA)
    internal val HOJA = Color(0xFF2F5A3C)
    internal val ARRAYAN = Color(0xFF557C48)
    internal val NARANJA = Color(0xFFE8902C)
    internal val RIO = Color(0xFF5F8F7C)
    internal val RIO_HONDO = Color(0xFF3C6A66)
    internal val RIO_ORILLA = Color(0xFFA9CDA8)
    internal val BARRO = Color(0xFFC88A5A)
    internal val MADERA = Color(0xFFAA7A4A)
    internal val MADERA_OSC = Color(0xFF6C4A30)
    internal val CARMESI = Color(0xFFB02A3C)
    internal val ANIL = Color(0xFF2F5C88)
    internal val ORO = Color(0xFFDAB150)
    internal val SEDA = Color(0xFFEBCB62)
    internal val BRONCE = Color(0xFFB7853E)
    internal val VIDRIADO = Color(0xFF3E8A6C)

    private val CASA_ALQUERIA = Casa(Color(0xFFF2EAD8), Techo.TEJA, TEJA, MADERA_OSC, ventanas = 1)

    // ---------------------------------------------------------------- Lo que el cuento mueve

    /** Es Ramadán: el día va al revés. */
    internal fun ramadan(m: Momento) = m.estado == "ramadan"

    /** Las lámparas del Ramadán: están colgadas todo el mes y sus dos noches señaladas. */
    internal fun lamparas(m: Momento) = ramadan(m) || m.con("lamparas")

    /** Las lámparas arden: de noche, mientras están colgadas. */
    internal fun lamparasEncendidas(m: Momento) = lamparas(m) && m.luces().noche > 0.3f

    /**
     * El anafe está encendido. Un día cualquiera, a las horas de comer. En Ramadán no se cocina de día: se enciende
     * al caer la tarde, para la cena con que se rompe el ayuno.
     */
    internal fun guisando(m: Momento) = if (ramadan(m)) m.hacer == Hacer.CUIDAR else m.hacer == Hacer.COMER

    /** La mesa está puesta en el patio: en Ramadán, al caer la tarde; los demás días, cuando hay gente. */
    internal fun mesaPuesta(m: Momento) = if (ramadan(m)) m.hacer == Hacer.GENTE || m.hacer == Hacer.CUIDAR else m.hacer == Hacer.GENTE

    /** Cuánto lleva tejido de la pieza, de 0 (la urdimbre) a 1 (terminada). */
    internal fun tela(m: Momento): Float = m.arco("tela").coerceIn(0f, 1f)

    /** Cómo está el telar. */
    internal enum class Telar { URDIMBRE, TEJIENDO, DESNUDO }

    /** El telar: con la urdimbre sola, con la tela a medias o desnudo, cuando la pieza está terminada y entregada. */
    internal fun telar(m: Momento): Telar = when { tela(m) >= 0.999f -> Telar.DESNUDO; tela(m) < 0.02f -> Telar.URDIMBRE; else -> Telar.TEJIENDO }

    /** La segunda pieza, la de la boda (de la temporada 3 en adelante), es azul; la del califa, carmesí. */
    internal fun telaDeLaBoda(m: Momento) = m.ficha.temporada >= 3

    /** El río, de 0 (casi seco) a 1 (crecido). */
    internal fun rio(m: Momento): Float = m.arco("rio").coerceIn(0f, 1f)

    /** Cuánto va crecido, de 0 a 1: desde que pasa de su nivel medio. */
    internal fun crecida(m: Momento): Float = ((rio(m) - 0.55f) / 0.45f).coerceIn(0f, 1f)

    /** Cuántas islas de grava asoman (de 0 a 5): más cuanto más bajo va. */
    internal fun islas(m: Momento): Int = (((0.6f - rio(m)) / 0.4f).coerceIn(0f, 1f) * 5f).roundToInt()

    /** La hueste cruza el puente. */
    internal fun hueste(m: Momento) = m.con("guerra")

    internal fun fiesta(m: Momento) = m.con("fiesta")
    internal fun luto(m: Momento) = m.con("luto")
    internal fun sedaNueva(m: Momento) = m.con("cosecha")

    /** Hay nieve posada: el día que nieva en Córdoba, o si el tiempo es de nieve. */
    internal fun nevado(m: Momento) = m.con("nieve") || m.ficha.clima == Clima.NIEVE

    /** El candil del nicho: encendido al anochecer y de noche, mientras la casa está despierta. */
    internal fun candil(m: Momento) = m.luces().noche > 0.15f && m.hacer != Hacer.DORMIR

    /** Hay luz dentro de la sala. */
    private fun salaEncendida(m: Momento) = m.luces().noche > 0.3f && m.hacer != Hacer.DORMIR

    // ---------------------------------------------------------------- La parte quieta

    override fun DrawScope.quieta(m: Momento) {
        val l = m.luces(); val r = Rayo(l)
        val est = m.ficha.estacion
        val semilla = m.ficha.semilla
        val fijo = Azar(Semilla.de("andalus", "tierra"))

        // 1. Cielo y lejanía.
        cielo(l, H, semilla)
        astros(l, H, semilla)
        nubes(l, H, m.ficha.clima, semilla)
        vista { campina(m, l, r, est, fijo) }

        // 2. El río con su puente, y la ciudad que baja hasta la tapia.
        vista { aguaDelRio(m, l) }
        estela(l, Rect(ESQUINA, H + 6f, ANCHO, CAP - 30f), m.azar("estela"))
        vista { elRio(m, l, r); ciudad(m, l, r) }

        // 3. El velo del tiempo que hace borra lo lejano, no el patio: va antes que él (salvo el polvo, que lo envuelve todo).
        val clima = m.ficha.clima
        if (clima != Clima.POLVO) tiempoQuieto(l, clima, H, semilla)

        // 4. El patio.
        patio(m, l, r, est)
        copaDelNaranjo(m, l, r, est)

        // 5. El polvo o un resto de niebla dentro del patio, y el grano del papel.
        if (clima == Clima.POLVO) tiempoQuieto(l, clima, H, semilla)
        if (clima == Clima.NIEBLA) {
            drawRect(l.bruma.copy(alpha = 0.16f), Offset.Zero, Size(ANCHO, ALTO))
            mancha(Offset(250f, BASE + 4f), 150f, 8f, lerp(l.bruma, Color.White, 0.3f), 0.32f); mancha(Offset(60f, 250f), 90f, 7f, lerp(l.bruma, Color.White, 0.3f), 0.22f)
        }
        grano(Azar(Semilla.de("andalus", "grano")), 0.6f + 0.4f * r.dia)
    }

    /** Pinta [dibujo] en las medidas de la vista, agrandado y puesto en su sitio del cuadro. */
    private inline fun DrawScope.vista(crossinline dibujo: DrawScope.() -> Unit) =
        withTransform({ translate(ESQUINA - 184f * KV, CAP - 176f * KV); scale(KV, KV, Offset.Zero) }) { dibujo() }

    private fun mezcla(a: Float, b: Float, k: Float) = a + (b - a) * k
    private fun enSombra(l: Luces, r: Rayo, c: Color, lejos: Float) = l.tono(mult(c, r.color, 0.25f + 0.65f * r.dia), lejos)

    /** La sombra corta de algo lejano que está de pie en [pie]. */
    private fun DrawScope.sombraCorta(r: Rayo, pie: Offset, ancho: Float, alto: Float) {
        if (r.fuerza < 0.05f) return
        val d = (r.ax * alto).coerceIn(-18f, 18f)
        drawOval(r.color, Offset(minOf(pie.x, pie.x + d) - ancho / 2f, pie.y - 0.6f), Size(abs(d) + ancho, maxOf(1f, ancho * 0.3f)), alpha = r.fuerza * 0.85f, blendMode = BlendMode.Multiply)
    }

    // ---------------------------------------------------------------- La campiña

    /** La cresta de la loma más cercana de la otra orilla, puesta a mano. */
    private val LOMA = listOf(
        Offset(150f, 113.4f), Offset(176f, 111.2f), Offset(196f, 108.6f), Offset(214f, 109.8f), Offset(232f, 106.8f), Offset(250f, 107.6f), Offset(266f, 110.6f),
        Offset(284f, 109.2f), Offset(300f, 111.8f), Offset(322f, 110f), Offset(340f, 107.4f), Offset(360f, 108.8f), Offset(384f, 106.4f),
    )

    /** Las lomas de la otra orilla: sierras azules al fondo, la campiña con sus olivares y sembrados, una alquería y el soto del río. */
    private fun DrawScope.campina(m: Momento, l: Luces, r: Rayo, est: Estacion, fijo: Azar) {
        val az = Azar(Semilla.de("andalus", "campina"))
        monte(l, perfil(fijo, 40f, 560f, HV + 1f, 30f, 0.55f, 40, isla = true), HV + 2f, Color(0xFF767C9C), 0.88f)
        monte(l, perfil(fijo, 110f, 470f, HV + 1f, 17f, 0.35f, 30, isla = true), HV + 2f, Color(0xFF8A8E78), 0.7f)
        mancha(Offset(280f, HV - 2f), 120f, 5f, l.bruma, 0.16f + 0.3f * r.dia)
        val hierba = lerp(hierbaDe(est, mediterraneo = true), Color(0xFFB8AC74), 0.3f)
        val loma = curva(LOMA, HV + 3.5f)
        drawPath(loma, Brush.verticalGradient(listOf(l.tono(lerp(hierba, Color(0xFFC8BE8E), 0.3f), 0.62f), l.tono(hierba, 0.5f)), 106f, HV + 3f))
        clipPath(loma) {
            // Las lomas tienen su lado de sombra, y los sembrados hacen una colcha.
            repeat(6) {
                val c = Offset(az.entre(180f, 380f), az.entre(110f, 119f)); val w = az.entre(24f, 52f)
                mancha(c, w, az.entre(1.6f, 3f), l.tono(mult(hierba, Color(0xFF7E86A6), 0.7f), 0.55f), 0.34f)
                mancha(c + Offset(r.lat * 6f, -2.4f), w * 0.8f, az.entre(1.2f, 2f), l.tono(lerp(hierba, Color(0xFFF2E8B8), 0.5f), 0.55f), 0.3f)
            }
            val trigo = when (est) { Estacion.VERANO -> Color(0xFFDDC070); Estacion.OTONO -> Color(0xFFB09868); Estacion.INVIERNO -> Color(0xFF8EA65A); else -> Color(0xFF9DB45A) }
            listOf(
                listOf(Offset(196f, 112f), Offset(222f, 111f), Offset(228f, 115.4f), Offset(192f, 116.4f)),
                listOf(Offset(256f, 111.6f), Offset(286f, 112.2f), Offset(292f, 116.6f), Offset(252f, 116f)),
                listOf(Offset(318f, 113f), Offset(342f, 111f), Offset(350f, 115f), Offset(322f, 117f)),
            ).forEachIndexed { i, b ->
                drawPath(poligono(b), l.tono(if (i == 1) lerp(trigo, Color(0xFFA08460), 0.5f) else trigo, 0.55f).copy(alpha = 0.85f))
                clipPath(poligono(b)) { for (k in 0..14) drawLine(l.tono(Color(0xFF6E6A44), 0.55f).copy(alpha = 0.3f), Offset(b[0].x + k * 3f, b[0].y - 1f), Offset(b[3].x + k * 3f, b[3].y + 1f), 0.35f) }
            }
        }
        // El olivar, en hileras, y la alquería con sus cipreses.
        for (fila in 0..2) for (i in 0..9) {
            val p = Offset(228f + i * 3.4f + fila * 1.4f, 112.4f + fila * 1.9f)
            if (p.x < 256f) { sombraCorta(r, p, 2f, 1.6f); olivo(l, p, 2.4f + 0.3f * fila, 0.6f, fijo) }
        }
        for (i in 0..7) { val p = Offset(352f + i * 3.6f, 113f + (i % 3) * 1.5f); olivo(l, p, 2.8f, 0.58f, fijo) }
        cipres(l, Offset(303f, 115.4f), 6.4f, 0.55f); cipres(l, Offset(306.4f, 115.6f), 5f, 0.55f)
        sombraCorta(r, Offset(312f, 115.8f), 6f, 3f)
        casa(l, Rect(308f, 112.2f, 316f, 115.8f), CASA_ALQUERIA, 0.55f, az, encendida = l.noche > 0.5f && m.hacer != Hacer.DORMIR)
        // La otra orilla: la franja de grava y el soto de álamos y tarajes.
        drawRect(l.tono(Color(0xFFCDBF96), 0.5f), Offset(0f, HV + 2.6f), Size(ANCHO, 2.6f))
        var x = 150f
        while (x < ANCHO + 4f) {
            val w = az.entre(3f, 7f); val h = az.entre(2.2f, 5.4f)
            val verde = if (az.real() > 0.5f) Color(0xFF55764C) else Color(0xFF6A8850)
            if (az.real() > 0.14f) {
                drawOval(l.tono(verde, 0.5f), Offset(x, HV + 3.4f - h), Size(w, h + 1f))
                drawOval(l.tono(lerp(verde, Color(0xFFE2E0A0), 0.4f), 0.5f), Offset(x + (if (r.lat >= 0f) w * 0.45f else 0f), HV + 3.4f - h), Size(w * 0.55f, h * 0.6f))
            }
            x += w * az.entre(0.6f, 1.1f)
        }
    }

    // ---------------------------------------------------------------- El río

    // El puente: de esta orilla (cerca, a la derecha) a la otra (lejos, a la izquierda).
    private val PUENTE_A = Offset(288f, 150.4f); private val PUENTE_B = Offset(200f, 123.4f)
    private const val OJOS = 13

    /** El tamaño de las cosas del puente a [t] de su largo (0, en esta orilla; 1, en la otra). */
    private fun talla(t: Float) = 1f - 0.64f * t

    /** Dónde empieza el ojo [i] del puente (y dónde acaba el anterior): se van apretando con la distancia. */
    private fun tramo(i: Int): Float { var s = 0f; var total = 0f; for (k in 0 until OJOS) { val w = talla((k + 0.5f) / OJOS); if (k < i) s += w; total += w }; return s / total }

    private fun enElPuente(t: Float) = PUENTE_A + (PUENTE_B - PUENTE_A) * t

    /** La orilla de la ciudad, de izquierda a derecha: con sus entrantes y la punta donde arranca el puente. */
    private val ORILLA = listOf(
        Offset(140f, 149.6f), Offset(170f, 150.2f), Offset(186f, 148.8f), Offset(198f, 149.6f), Offset(212f, 147.8f), Offset(226f, 149.4f), Offset(242f, 150.6f), Offset(258f, 149.2f),
        Offset(274f, 150.8f), Offset(292f, 151.6f), Offset(306f, 150.4f), Offset(322f, 151.8f), Offset(338f, 150.6f), Offset(352f, 152f), Offset(368f, 151f), Offset(386f, 152.2f),
    )

    private val ISLAS = listOf(Triple(226f, 142.6f, 28f), Triple(264f, 131.4f, 17f), Triple(196f, 134.4f, 15f), Triple(352f, 139.4f, 24f), Triple(262f, 146.4f, 16f))

    /** Los colores del río: verde cuando va manso; pardo, de barro, cuando va crecido. */
    private fun coloresDelRio(m: Momento): Triple<Color, Color, Color> {
        val crecida = crecida(m)
        return Triple(lerp(RIO, Color(0xFF97845A), 0.75f * crecida), lerp(RIO_HONDO, Color(0xFF6E6248), 0.7f * crecida), lerp(RIO_ORILLA, Color(0xFFC8B88E), 0.7f * crecida))
    }

    /** El agua del Guadalquivir, con el soto de la otra orilla reflejado. */
    private fun DrawScope.aguaDelRio(m: Momento, l: Luces) {
        val (propio, hondo, orilla) = coloresDelRio(m)
        agua(l, Rect(0f, HV + 4.6f, ANCHO, 154f), m.azar("rio"), propio, m.viento, hondo = hondo, orilla = orilla)
        reflejoRoto(150f, ANCHO, HV + 5f, 3.4f, l.tono(Color(0xFF4E6A4E), 0.5f), 0.5f, Azar(Semilla.de("andalus", "soto")), 0.8f)
    }

    /** Lo que hay en el río: las islas de grava cuando va bajo, el puente, esta orilla y su noria. */
    private fun DrawScope.elRio(m: Momento, l: Luces, r: Rayo) {
        val az = Azar(Semilla.de("andalus", "rio"))
        val crecida = crecida(m)
        val orilla = coloresDelRio(m).third

        // Las islas de grava, con sus juncos, cuando el río va bajo.
        val cuantas = islas(m)
        ISLAS.take(cuantas).forEachIndexed { i, (cx, cy, w) ->
            val k = 0.55f + (cy - 128f) / 40f
            val isla = Path().apply { moveTo(cx - w / 2f, cy); cubicTo(cx - w * 0.3f, cy - 2.2f * k, cx + w * 0.2f, cy - 2.6f * k, cx + w / 2f, cy - 0.2f); cubicTo(cx + w * 0.25f, cy + 1.2f * k, cx - w * 0.3f, cy + 1.4f * k, cx - w / 2f, cy); close() }
            drawOval(l.tono(orilla, 0.3f).copy(alpha = 0.5f * (1f - 0.7f * l.noche)), Offset(cx - w * 0.62f, cy - 2.6f * k), Size(w * 1.24f, 5.2f * k))
            drawPath(isla, Brush.verticalGradient(listOf(l.tono(Color(0xFFE4D8B4), 0.36f), l.tono(Color(0xFFB8A882), 0.36f)), cy - 2.6f * k, cy + 1.4f * k))
            clipPath(isla) { repeat(8) { drawCircle(l.tono(if (it % 2 == 0) Color(0xFF8E8468) else Color(0xFFF2EACC), 0.36f), az.entre(0.3f, 0.7f) * k, Offset(az.entre(cx - w / 2f, cx + w / 2f), az.entre(cy - 2f * k, cy + k))) } }
            // Los juncos.
            val n = 5 + i * 2
            repeat(n) { val x = cx - w * 0.28f + it * (w * 0.5f / n) + az.entre(-0.6f, 0.6f); val h = az.entre(2f, 4.6f) * k; drawLine(l.tono(if (it % 2 == 0) Color(0xFF6E8A4A) else Color(0xFF8A9A58), 0.36f), Offset(x, cy - 1.2f * k), Offset(x + az.entre(-0.8f, 0.8f), cy - 1.2f * k - h), 0.6f, StrokeCap.Round) }
            reflejoRoto(cx - w * 0.4f, cx + w * 0.4f, cy + 1.2f * k, 3f * k, l.tono(Color(0xFF7E7A5A), 0.36f), 0.4f, az, 0.8f)
        }

        // La azuda: la presa de piedra que cruza medio río en diagonal, con su espuma, y el molino en su punta.
        val a0 = Offset(350f, 150.4f); val a1 = Offset(318f, 137.2f)
        drawLine(l.tono(Color(0xFF8E866E), 0.4f), a0, a1, 1.3f, StrokeCap.Round)
        val espuma = l.tono(Color.White, 0.3f)
        repeat(22) { val t = az.real(); val p = a0 + (a1 - a0) * t; drawLine(espuma.copy(alpha = az.entre(0.35f, 0.8f) * (1f - 0.6f * l.noche)), p + Offset(-1f, 0.8f), p + Offset(-az.entre(2f, 5.4f) * (1f + crecida), 1f + az.entre(0f, 1.4f)), 0.6f, StrokeCap.Round) }
        molino(l, r, Offset(314f, 137.6f), az)

        puente(m, l, r, az)

        // Esta orilla: el agua clara, la grava y el talud, con sus juncos, hasta el pie de la muralla.
        val orillaPath = curva(ORILLA)
        bajio(l, orillaPath, 7f, orilla)
        drawPath(orillaPath, l.tono(Color.White, 0.3f).copy(alpha = 0.5f * (1f - 0.6f * l.noche)), style = Stroke(1.1f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val tierra = curva(ORILLA, 160f)
        drawPath(tierra, Brush.verticalGradient(listOf(l.tono(Color(0xFFD6C8A0), 0.34f), l.tono(Color(0xFF9C8C64), 0.3f)), 148f, 156f))
        clipPath(tierra) {
            ORILLA.forEach { p -> repeat(3) { val q = p + Offset(az.entre(-6f, 6f), az.entre(0.4f, 2.6f)); val w = az.entre(0.5f, 1.3f); drawOval(l.tono(if (az.real() > 0.5f) Color(0xFF8A7E62) else Color(0xFFF0E6C8), 0.3f), Offset(q.x - w, q.y - w * 0.5f), Size(w * 2f, w)) } }
        }
        for (i in 0 until ORILLA.size - 1) if (i % 3 != 1) {
            val a = ORILLA[i]; val b = ORILLA[i + 1]
            repeat(7) { val p = a + (b - a) * az.real(); val h = az.entre(2f, 5f); drawLine(l.tono(if (it % 2 == 0) Color(0xFF64824A) else Color(0xFF86985A), 0.3f), p + Offset(0f, 0.6f), p + Offset(az.entre(-1f, 1f), -h), 0.7f, StrokeCap.Round) }
        }
        noria(l, r, Offset(194f, 143f))
    }

    /** El molino de la azuda: un cubo de piedra con su tejado, medio metido en el agua. */
    private fun DrawScope.molino(l: Luces, r: Rayo, pie: Offset, az: Azar) {
        val lejos = 0.42f
        val piedra = l.tono(PIEDRA, lejos); val sombra = enSombra(l, r, PIEDRA, lejos)
        reflejoRoto(pie.x - 4f, pie.x + 4f, pie.y + 0.4f, 5f, l.tono(Color(0xFF8A7C5E), lejos), 0.5f, az, 0.8f)
        drawRect(piedra, Offset(pie.x - 4f, pie.y - 4.4f), Size(8f, 4.8f))
        drawRect(sombra, Offset(if (r.lat >= 0f) pie.x - 4f else pie.x + 1f, pie.y - 4.4f), Size(3f, 4.8f))
        drawPath(poligono(Offset(pie.x - 4.6f, pie.y - 4.4f), Offset(pie.x + 4.6f, pie.y - 4.4f), Offset(pie.x + 2.6f, pie.y - 6.6f), Offset(pie.x - 2.6f, pie.y - 6.6f)), l.tono(TEJA, lejos))
        drawRect(l.tono(Color(0xFF3A3028), lejos), Offset(pie.x - 0.6f, pie.y - 1.6f), Size(1.4f, 2f))
    }

    /** La noria de esta orilla: la rueda, sus radios y sus cangilones, y el machón de piedra por donde sale el agua. */
    private fun DrawScope.noria(l: Luces, r: Rayo, c: Offset) {
        val lejos = 0.3f; val rad = 7.4f
        val madera = l.tono(Color(0xFF6E5238), lejos); val clara = l.tono(Color(0xFFA08460), lejos)
        // El machón y el arranque del acueducto.
        val piedra = l.tono(PIEDRA, lejos); val sombra = enSombra(l, r, PIEDRA, lejos)
        drawRect(piedra, Offset(c.x + 5.4f, c.y - 6.6f), Size(5.4f, 15f)); drawRect(sombra, Offset(if (r.lat >= 0f) c.x + 5.4f else c.x + 8.6f, c.y - 6.6f), Size(2.2f, 15f))
        drawRect(piedra, Offset(c.x + 10.8f, c.y - 6.6f), Size(11f, 2f))
        for (i in 0..1) { val x = c.x + 12.6f + i * 5f; drawPath(Path().apply { moveTo(x, c.y + 8f); lineTo(x, c.y - 2.4f); cubicTo(x, c.y - 4.8f, x + 3.4f, c.y - 4.8f, x + 3.4f, c.y - 2.4f); lineTo(x + 3.4f, c.y + 8f) }, sombra.copy(alpha = 0.55f)) }
        drawCircle(madera, rad, c, style = Stroke(1.1f)); drawCircle(clara, rad - 1.4f, c, style = Stroke(0.4f))
        for (i in 0 until 12) {
            val a = i * 0.5236f + 0.2f; val d = Offset(cos(a), sin(a))
            drawLine(madera, c, c + d * rad, 0.5f)
            drawRect(l.tono(BARRO, lejos), Offset(c.x + d.x * (rad + 0.6f) - 0.7f, c.y + d.y * (rad + 0.6f) - 0.7f), Size(1.4f, 1.4f))
        }
        drawCircle(clara, 1.1f, c)
    }

    /** El puente de piedra: los ojos, los tajamares, el pretil, su sombra y su reflejo. Cuando el río crece, el agua sube por las pilas. */
    private fun DrawScope.puente(m: Momento, l: Luces, r: Rayo, az: Azar) {
        val lejos = 0.36f
        val nivel = 1.5f * crecida(m) - 1.1f * ((0.55f - rio(m)) / 0.35f).coerceIn(0f, 1f)        // cuánto sube (o baja) el agua en las pilas
        val piedra = l.tono(PIEDRA, lejos); val luz = l.tono(lerp(PIEDRA, Color.White, 0.35f), lejos); val sombra = enSombra(l, r, lerp(PIEDRA, Color(0xFF8A7A5E), 0.3f), lejos)
        val hueco = l.tono(mult(Color(0xFF6A5E48), r.color, 0.6f), lejos)
        fun agua(t: Float) = enElPuente(t).y - nivel * talla(t)
        fun arriba(t: Float) = enElPuente(t).y - 8.6f * talla(t)
        // El reflejo del puente entero, antes que el puente.
        for (i in 0 until OJOS) {
            val t0 = tramo(i); val t1 = tramo(i + 1); val s = talla((t0 + t1) / 2f)
            reflejoRoto(enElPuente(t1).x, enElPuente(t0).x, maxOf(agua(t0), agua(t1)) + 0.3f, 6.5f * s, l.tono(Color(0xFF8E805E), lejos), 0.5f, az, 0.8f)
        }
        // El cuerpo del puente.
        val cuerpo = Path().apply { moveTo(enElPuente(0f).x + 5f, arriba(0f)); lineTo(enElPuente(1f).x - 2f, arriba(1f)); lineTo(enElPuente(1f).x - 2f, agua(1f)); lineTo(enElPuente(0f).x + 5f, agua(0f) + 1f); close() }
        drawPath(cuerpo, Brush.verticalGradient(listOf(luz, piedra, lerp(piedra, sombra, 0.5f)), arriba(1f), agua(0f)))
        // Los ojos y, entre ellos, las pilas con su tajamar.
        for (i in 0 until OJOS) {
            val t0 = tramo(i); val t1 = tramo(i + 1); val tm = (t0 + t1) / 2f; val s = talla(tm)
            val x0 = enElPuente(t0).x; val x1 = enElPuente(t1).x; val ancho = x0 - x1
            val pila = ancho * 0.2f
            val clave = enElPuente(tm).y - 6.2f * s
            val ojo = Path().apply {
                moveTo(x0 - pila, agua(t0)); lineTo(x0 - pila, enElPuente(t0).y - 2.6f * s)
                cubicTo(x0 - pila, clave - 0.2f, x1 + pila, clave - 0.2f, x1 + pila, enElPuente(t1).y - 2.6f * s)
                lineTo(x1 + pila, agua(t1)); close()
            }
            drawPath(ojo, Brush.verticalGradient(listOf(hueco, lerp(hueco, l.tono(RIO_HONDO, lejos), 0.6f)), clave, agua(t0)))
            // La rosca del arco, del lado de la luz.
            drawPath(ojo, luz.copy(alpha = 0.7f), style = Stroke(0.45f * s + 0.2f))
            // El tajamar: una cuña que parte la corriente.
            val px = x0; val py = agua(t0)
            drawPath(poligono(Offset(px - pila, py), Offset(px + pila, py), Offset(px + pila * 0.2f, py - 3.6f * s), Offset(px - pila * 0.6f, py - 3.6f * s)), if (r.lat >= 0f) luz else sombra)
            drawPath(poligono(Offset(px - pila, py), Offset(px - pila * 0.1f, py + 1.3f * s), Offset(px + pila, py)), l.tono(Color(0xFF9C8C68), lejos))
            if (crecida(m) > 0.3f) drawLine(l.tono(Color.White, 0.3f).copy(alpha = 0.6f * crecida(m) * (1f - 0.6f * l.noche)), Offset(px - pila * 1.6f, py + 0.6f), Offset(px + pila * 1.4f, py + 0.8f), 0.6f, StrokeCap.Round)
        }
        // El pretil y la sombra que da sobre la cara del puente.
        drawLine(luz, Offset(enElPuente(0f).x + 5f, arriba(0f)), Offset(enElPuente(1f).x - 2f, arriba(1f)), 0.7f)
        if (r.fuerza > 0.05f) drawLine(r.color, Offset(enElPuente(0f).x + 5f, arriba(0f) + 1.4f), Offset(enElPuente(1f).x - 2f, arriba(1f) + 0.8f), 0.9f, alpha = r.fuerza * 0.6f, blendMode = BlendMode.Multiply)
        // La hueste que cruza: no se ven los hombres, solo las lanzas y los estandartes blancos por encima del pretil.
        if (hueste(m)) {
            val hz = Azar(Semilla.de("andalus", "hueste"))
            val blanco = l.tono(Color(0xFFF6F2E6), lejos); val asta = l.tono(Color(0xFF4A3A2A), lejos)
            var t = 0.04f
            while (t < 0.9f) {
                val p = Offset(enElPuente(t).x, arriba(t)); val s = talla(t)
                val alto = hz.entre(3.2f, 5.4f) * s
                drawLine(asta, p, p + Offset(hz.entre(-0.3f, 0.3f), -alto), 0.35f + 0.2f * s)
                if (hz.real() > 0.72f) drawPath(poligono(p + Offset(0f, -alto - 1.4f * s), p + Offset(-4.4f * s, -alto - 0.6f * s), p + Offset(-4.4f * s, -alto + 2f * s), p + Offset(0f, -alto + 1.6f * s)), blanco)
                else drawCircle(l.tono(Color(0xFFD8D4C8), lejos), 0.35f * s + 0.15f, p + Offset(0f, -alto))
                t += hz.entre(0.012f, 0.03f)
            }
        }
    }

    // ---------------------------------------------------------------- La ciudad

    /**
     * Córdoba, entre el río y la tapia: la muralla de la orilla con la puerta del puente, la mezquita grande con su
     * alminar, y el caserío, que baja en tres hileras, cada una más grande y más nítida que la anterior.
     */
    private fun DrawScope.ciudad(m: Momento, l: Luces, r: Rayo) {
        val az = Azar(Semilla.de("andalus", "ciudad"))
        val cae = if (r.lat >= 0f) -1f else 1f
        val despierta = m.hacer != Hacer.DORMIR || lamparas(m)
        // El suelo de la ciudad: lo que asoma entre casa y casa son tapias, callejas y corrales, no cielo.
        val calleja = l.tono(Color(0xFFD9CDB2), 0.2f * (1f - 0.8f * l.noche)); val callejaSombra = enSombra(l, r, Color(0xFFBFB096), 0.14f * (1f - 0.8f * l.noche))
        drawRect(Brush.verticalGradient(listOf(calleja, callejaSombra), 154f, 180f), Offset(140f, 154f), Size(ANCHO - 136f, 28f))
        repeat(40) { val x = az.entre(150f, ANCHO); val y = az.entre(158f, 178f); drawRect(if (it % 2 == 0) callejaSombra else l.tono(Color(0xFFEFE6D2), 0.12f * (1f - 0.8f * l.noche)), Offset(x, y), Size(az.entre(3f, 9f), az.entre(0.8f, 2f))) }
        // La muralla del río, con sus torres, y la puerta del puente.
        val lejosM = 0.34f
        val tapial = lerp(PIEDRA, Color(0xFFC8A070), 0.35f)
        if (r.fuerza > 0.05f) drawLine(r.color, Offset(150f, 157.6f), Offset(ANCHO, 157f), 1.4f, alpha = r.fuerza * 0.7f, blendMode = BlendMode.Multiply)
        muralla(l, Offset(150f, 157f), Offset(281f, 156.4f), 5.2f, tapial, lejosM, almenas = true)
        muralla(l, Offset(296f, 156.4f), Offset(ANCHO + 2f, 156.6f), 5.2f, tapial, lejosM, almenas = true)
        for (x in listOf(186f, 214f, 242f, 268f, 316f, 346f, 372f)) {
            drawRect(Brush.horizontalGradient(if (cae < 0f) listOf(enSombra(l, r, tapial, lejosM), l.tono(tapial, lejosM)) else listOf(l.tono(tapial, lejosM), enSombra(l, r, tapial, lejosM)), x - 2.6f, x + 2.6f), Offset(x - 2.6f, 149f), Size(5.2f, 8.2f))
            for (i in 0..2) drawRect(l.tono(lerp(tapial, Color.White, 0.2f), lejosM), Offset(x - 2.8f + i * 2.1f, 147.8f), Size(1.3f, 1.3f))
        }
        // La puerta del puente.
        val pg = Offset(288.5f, 156.6f)
        drawRect(Brush.horizontalGradient(if (cae < 0f) listOf(enSombra(l, r, PIEDRA, lejosM), l.tono(PIEDRA, lejosM)) else listOf(l.tono(PIEDRA, lejosM), enSombra(l, r, PIEDRA, lejosM)), pg.x - 7f, pg.x + 7f), Offset(pg.x - 7f, pg.y - 11.4f), Size(14f, 11.4f))
        for (i in 0..4) drawRect(l.tono(lerp(PIEDRA, Color.White, 0.25f), lejosM), Offset(pg.x - 7.2f + i * 3.1f, pg.y - 12.8f), Size(1.8f, 1.5f))
        drawPath(Path().apply { moveTo(pg.x - 2.6f, pg.y); lineTo(pg.x - 2.6f, pg.y - 4.4f); cubicTo(pg.x - 3.6f, pg.y - 8.8f, pg.x + 3.6f, pg.y - 8.8f, pg.x + 2.6f, pg.y - 4.4f); lineTo(pg.x + 2.6f, pg.y); close() }, l.tono(Color(0xFF3A3028), lejosM))

        mezquita(m, l, r, az, despierta)

        // El caserío.
        var n = 0
        fun casita(x: Float, base: Float, w: Float, h: Float, lejosDeDia: Float, detalle: Boolean) {
            val lejos = lejosDeDia * (1f - 0.8f * l.noche)
            val propio = listOf(Color(0xFFF6F0E2), Color(0xFFEADFC8), Color(0xFFFAF6EC), Color(0xFFE2D4B6))[n % 4]; n++
            val azotea = n % 3 != 0
            if (r.fuerza > 0.05f) drawOval(r.color, Offset(if (cae < 0f) x - w * 0.5f else x + w * 0.5f, base - 0.7f), Size(w, 1.1f), alpha = r.fuerza * 0.8f, blendMode = BlendMode.Multiply)
            drawRect(l.tono(propio, lejos), Offset(x, base - h), Size(w, h))
            drawRect(enSombra(l, r, propio, lejos), Offset(if (cae < 0f) x else x + w * 0.64f, base - h), Size(w * 0.36f, h))
            if (azotea) {
                // El pretil de la azotea, y a veces la ropa tendida o un cuarto alto.
                drawRect(l.tono(lerp(propio, Color.White, 0.3f), lejos), Offset(x - 0.3f, base - h - 0.9f), Size(w + 0.6f, 1.1f))
                if (detalle && n % 4 == 1) { drawRect(l.tono(propio, lejos), Offset(x + w * 0.5f, base - h - h * 0.5f), Size(w * 0.42f, h * 0.5f)); drawRect(l.tono(TEJA, lejos), Offset(x + w * 0.46f, base - h - h * 0.5f - 0.8f), Size(w * 0.5f, 1f)) }
                if (detalle && n % 4 == 2) for (i in 0..3) drawRect(l.tono(listOf(CARMESI, CAL, ANIL, SEDA)[i], lejos), Offset(x + 1.4f + i * (w - 3f) / 4f, base - h - 3.2f), Size(1.5f, 2.2f))
            } else {
                drawPath(poligono(Offset(x - 0.5f, base - h), Offset(x + w + 0.5f, base - h), Offset(x + w * 0.8f, base - h * 1.32f), Offset(x + w * 0.2f, base - h * 1.32f)), l.tono(if (n % 2 == 0) TEJA else lerp(TEJA, Color(0xFF8E4A2C), 0.4f), lejos))
                drawLine(l.tono(lerp(TEJA, Color.White, 0.3f), lejos), Offset(x + w * 0.2f, base - h * 1.32f), Offset(x + w * 0.8f, base - h * 1.32f), 0.4f)
            }
            if (detalle) {
                drawRect(l.tono(Color(0xFF4A3A2C), lejos), Offset(x + w * 0.2f, base - h * 0.5f), Size(maxOf(0.7f, w * 0.14f), h * 0.5f))
                drawRect(l.tono(Color(0xFF3E3A44), lejos), Offset(x + w * 0.62f, base - h * 0.78f), Size(maxOf(0.6f, w * 0.1f), maxOf(0.8f, h * 0.16f)))
            }
            // La nieve, posada en la azotea o en el tejado.
            if (nevado(m)) drawRect(l.tono(Color(0xFFFCFDFF), lejos), Offset(x - 0.4f, base - h - (if (azotea) 1.4f else h * 0.34f)), Size(w + 0.8f, if (azotea) 1.4f else h * 0.2f))
            val luz = l.noche > 0.5f && despierta && (if (lamparas(m)) n % 2 == 1 else n % 3 == 1)
            if (luz) drawCircle(LUZ_DE_CASA.copy(alpha = 0.9f), if (detalle) 0.7f else 0.45f, Offset(x + w * 0.66f, base - h * 0.66f))
        }
        fun palmera(pie: Offset, alto: Float, lejos: Float) {
            val tronco = l.tono(Color(0xFF7A6248), lejos); val verde = l.tono(Color(0xFF4E7244), lejos); val claro = l.tono(Color(0xFF86A058), lejos)
            sombraCorta(r, pie, 2f, alto * 0.5f)
            drawLine(tronco, pie, pie + Offset(alto * 0.04f, -alto), maxOf(0.7f, alto * 0.07f))
            val c = pie + Offset(alto * 0.04f, -alto)
            for (i in 0 until 9) {
                val a = -3.14f + i * 0.39f + 0.02f; val largo = alto * 0.42f
                val fin = c + Offset(cos(a) * largo, sin(a) * largo * 0.7f + largo * 0.34f * abs(cos(a)))
                drawPath(Path().apply { moveTo(c); cubicTo(c + Offset(cos(a) * largo * 0.5f, sin(a) * largo * 0.8f), fin + Offset(0f, -largo * 0.2f), fin) }, if (cos(a) * r.lat > 0f) claro else verde, style = Stroke(maxOf(0.6f, alto * 0.05f), cap = StrokeCap.Round))
            }
        }
        fun hilera(x0: Float, x1: Float, base: Float, w0: Float, w1: Float, h0: Float, h1: Float, lejos: Float, detalle: Boolean, huecos: List<Float>) {
            var x = x0
            while (x < x1) {
                val w = az.entre(w0, w1)
                if (huecos.any { it >= x && it < x + w }) { x += w * 0.7f; continue }
                if (az.real() > 0.12f) casita(x, base + az.entre(-0.6f, 0.6f), w, az.entre(h0, h1), lejos, detalle)
                else cipres(l, Offset(x + w / 2f, base), az.entre(5f, 8f) * (1.2f - lejos), lejos)
                x += w + az.entre(0.1f, 1f)
            }
        }
        hilera(266f, ANCHO + 4f, 161.6f, 4.6f, 7.4f, 3.2f, 4.8f, 0.28f, false, emptyList())
        mancha(Offset(270f, 162.6f), 130f, 2.2f, l.bruma, 0.26f * r.dia)
        palmera(Offset(276f, 166f), 13f, 0.3f)
        hilera(150f, ANCHO + 6f, 167.4f, 7.4f, 11.6f, 4.6f, 7.2f, 0.18f, true, listOf(282f, 276f))
        // El alminar de la mezquita del barrio.
        alminar(l, r, Offset(282f, 168f), 3.8f, 17f, 0.18f, m)
        // Las cúpulas del baño: tres, encaladas, con sus lumbreras.
        for (i in 0..2) {
            val c = Offset(222f + i * 6.6f, 164.6f)
            drawPath(Path().apply { moveTo(c.x - 3.3f, c.y); cubicTo(c.x - 3.3f, c.y - 4.6f, c.x + 3.3f, c.y - 4.6f, c.x + 3.3f, c.y); close() }, Brush.horizontalGradient(if (cae < 0f) listOf(enSombra(l, r, CAL, 0.18f), l.tono(CAL, 0.18f)) else listOf(l.tono(CAL, 0.18f), enSombra(l, r, CAL, 0.18f)), c.x - 3.3f, c.x + 3.3f))
            for (k in -1..1) drawCircle(l.tono(Color(0xFF4A4650), 0.26f), 0.3f, c + Offset(k * 1.3f, -1.6f - (1 - abs(k)) * 0.9f))
        }
        mancha(Offset(270f, 170.6f), 130f, 1.8f, l.bruma, 0.16f * r.dia)
        palmera(Offset(372f, 177f), 19f, 0.1f); palmera(Offset(208f, 178f), 17f, 0.1f); palmera(Offset(264f, 178.4f), 14f, 0.1f)
        hilera(170f, ANCHO + 10f, 178.6f, 12f, 19f, 7.6f, 12f, 0.08f, true, listOf(208f, 264f, 372f))
    }

    /** Un alminar: el cuerpo cuadrado con sus ventanas, el cuerpo alto, la cupulilla y las manzanas doradas del remate. */
    private fun DrawScope.alminar(l: Luces, r: Rayo, pie: Offset, ancho: Float, alto: Float, lejos: Float, m: Momento) {
        val cae = if (r.lat >= 0f) -1f else 1f
        val piedra = l.tono(PIEDRA, lejos); val sombra = enSombra(l, r, PIEDRA, lejos); val luz = l.tono(lerp(PIEDRA, Color.White, 0.3f), lejos)
        sombraCorta(r, pie, ancho, alto * 0.6f)
        val x0 = pie.x - ancho / 2f
        drawRect(Brush.horizontalGradient(if (cae < 0f) listOf(sombra, sombra, piedra) else listOf(piedra, sombra, sombra), x0, x0 + ancho), Offset(x0, pie.y - alto * 0.78f), Size(ancho, alto * 0.78f))
        // Las almenas del primer cuerpo y el cuerpo alto.
        val y1 = pie.y - alto * 0.78f
        for (i in 0..2) drawRect(luz, Offset(x0 - 0.2f + i * (ancho - 0.9f) / 2f, y1 - ancho * 0.18f), Size(ancho * 0.22f + 0.3f, ancho * 0.2f))
        val a2 = ancho * 0.56f
        drawRect(Brush.horizontalGradient(if (cae < 0f) listOf(sombra, piedra) else listOf(piedra, sombra), pie.x - a2 / 2f, pie.x + a2 / 2f), Offset(pie.x - a2 / 2f, pie.y - alto * 0.94f), Size(a2, alto * 0.16f))
        drawPath(Path().apply { moveTo(pie.x - a2 / 2f, pie.y - alto * 0.94f); cubicTo(pie.x - a2 / 2f, pie.y - alto * 1.02f, pie.x + a2 / 2f, pie.y - alto * 1.02f, pie.x + a2 / 2f, pie.y - alto * 0.94f); close() }, luz)
        drawLine(l.tono(ORO, lejos), Offset(pie.x, pie.y - alto), Offset(pie.x, pie.y - alto * 1.12f), maxOf(0.35f, ancho * 0.06f))
        for (i in 0..2) drawCircle(l.tono(lerp(ORO, Color.White, 0.2f), lejos * 0.6f), maxOf(0.4f, ancho * (0.11f - 0.02f * i)), Offset(pie.x, pie.y - alto * (1.03f + 0.035f * i)))
        // Las ventanas geminadas, con su toque de almagra, y la imposta.
        val oscuro = l.tono(Color(0xFF4A3E38), lejos)
        for (k in 0..1) {
            val y = pie.y - alto * (0.36f + 0.26f * k)
            for (s in listOf(-1f, 1f)) drawRect(oscuro, Offset(pie.x + s * ancho * 0.14f - ancho * 0.07f, y), Size(ancho * 0.14f, alto * 0.07f))
            drawLine(l.tono(ALMAGRA, lejos), Offset(pie.x - ancho * 0.28f, y - 0.5f), Offset(pie.x + ancho * 0.28f, y - 0.5f), maxOf(0.3f, ancho * 0.05f))
        }
        drawLine(luz, Offset(x0, pie.y - alto * 0.2f), Offset(x0 + ancho, pie.y - alto * 0.2f), 0.4f)
        // En Ramadán, de noche, las lámparas del alminar.
        if (lamparasEncendidas(m)) for (i in 0..2) drawCircle(LUZ_DE_CASA, maxOf(0.5f, ancho * 0.09f), Offset(x0 + i * ancho / 2f, y1 - ancho * 0.3f))
    }

    /** Dónde pisa el alminar de la mezquita grande, en las medidas de la vista. */
    private val MINAR = Offset(254f, 149f)

    /** La mezquita grande: el muro con sus contrafuertes y sus almenas, las naves de tejado, el patio de los naranjos y el alminar. */
    private fun DrawScope.mezquita(m: Momento, l: Luces, r: Rayo, az: Azar, despierta: Boolean) {
        val lejos = 0.32f; val cae = if (r.lat >= 0f) -1f else 1f
        val piedra = l.tono(PIEDRA, lejos); val sombra = enSombra(l, r, PIEDRA, lejos); val luz = l.tono(lerp(PIEDRA, Color.White, 0.3f), lejos)
        val x0 = 198f; val x1 = 272f; val base = 160.4f; val alto = 12.6f
        // Las naves: una fila de tejados a dos aguas, vistos de testero.
        val teja = l.tono(TEJA, lejos); val tejaSombra = enSombra(l, r, lerp(TEJA, Color(0xFF7A4028), 0.3f), lejos)
        var x = x0 + 1f; var i = 0
        while (x < x1 - 26f) {
            drawPath(poligono(Offset(x, base - alto + 0.6f), Offset(x + 2.5f, base - alto - 2.8f), Offset(x + 5f, base - alto + 0.6f)), teja)
            drawPath(if (cae < 0f) poligono(Offset(x, base - alto + 0.6f), Offset(x + 2.5f, base - alto - 2.8f), Offset(x + 2.5f, base - alto + 0.6f)) else poligono(Offset(x + 5f, base - alto + 0.6f), Offset(x + 2.5f, base - alto - 2.8f), Offset(x + 2.5f, base - alto + 0.6f)), tejaSombra)
            x += 5f; i++
        }
        // El muro.
        drawRect(Brush.verticalGradient(listOf(luz, piedra, lerp(piedra, sombra, 0.4f)), base - alto, base), Offset(x0, base - alto), Size(x1 - x0, alto))
        var c = x0
        while (c <= x1) {
            drawRect(piedra, Offset(c - 1.1f, base - alto - 0.6f), Size(2.2f, alto + 0.6f)); drawRect(sombra, Offset(if (cae < 0f) c - 1.1f else c + 0.3f, base - alto - 0.6f), Size(0.8f, alto + 0.6f))
            if (r.fuerza > 0.05f) drawRect(r.color, Offset(if (cae < 0f) c - 2.6f else c + 1.1f, base - alto + 0.6f), Size(1.5f, alto - 0.6f), alpha = r.fuerza * 0.5f, blendMode = BlendMode.Multiply)
            c += 7f
        }
        var a = x0
        while (a < x1) { drawRect(luz, Offset(a, base - alto - 1.5f), Size(1.5f, 1.5f)); a += 2.8f }
        // Dos puertas, con su arco y sus dovelas (apenas un toque rojo).
        for (px in listOf(x0 + 16.5f, x0 + 44.5f)) {
            drawPath(Path().apply { moveTo(px - 1.6f, base); lineTo(px - 1.6f, base - 3.4f); cubicTo(px - 2.3f, base - 6.4f, px + 2.3f, base - 6.4f, px + 1.6f, base - 3.4f); lineTo(px + 1.6f, base); close() }, l.tono(Color(0xFF3E3028), lejos))
            drawPath(Path().apply { moveTo(px - 2.4f, base - 3.6f); cubicTo(px - 3f, base - 7.4f, px + 3f, base - 7.4f, px + 2.4f, base - 3.6f) }, l.tono(ALMAGRA, lejos), style = Stroke(0.6f))
            if (l.noche > 0.5f && despierta) drawCircle(LUZ_DE_CASA.copy(alpha = 0.8f), 0.6f, Offset(px, base - 2.4f))
        }
        // Las copas de los naranjos del patio, que asoman.
        for (k in 0..4) { val p = Offset(x1 - 23.5f + k * 4.4f, base - alto - 0.4f); drawOval(l.tono(HOJA, lejos), Offset(p.x - 2.2f, p.y - 2f), Size(4.4f, 3f)); drawOval(l.tono(lerp(HOJA, Color(0xFFD8E0A0), 0.35f), lejos), Offset(p.x - 2.2f + (if (cae < 0f) 2f else 0f), p.y - 2f), Size(2.4f, 1.6f)) }
        alminar(l, r, MINAR, 7.4f, 44f, 0.3f, m)
    }

    // ---------------------------------------------------------------- El patio

    private fun DrawScope.patio(m: Momento, l: Luces, r: Rayo, est: Estacion) {
        val az = Azar(Semilla.de("andalus", "patio"))
        tapia(m, l, r, az)
        muroDeLaSala(m, l, r, az)
        suelo(l, r, est, az)
        if (clima(m) == Clima.LLUVIA || clima(m) == Clima.TORMENTA) charcos(l)
        alberca(m, l, r)
        AndalusCosas.run { esteras(m, l) }
        if (nevado(m)) nieveEnElSuelo(l, r)
        val cosas = AndalusCosas.todas(m, l, r, est).sortedBy { it.pie.y }

        // Las sombras que caen sobre los muros y el suelo: la del alero, la del lomo de la tapia, la de cada cosa y la del naranjo.
        capaSombra(r) { c ->
            val alero = (8f * r.tan * 0.55f).coerceIn(2f, 13f)
            drawRect(c, Offset(-2f, ALERO + 2f), Size(ESQUINA + 5f, alero))
            var ax = -3f + r.ax.coerceIn(-3f, 3f); while (ax < ESQUINA + 2f) { drawOval(c, Offset(ax + 0.4f, ALERO + 2f + alero - 2f), Size(6.8f, 4f)); ax += 7.6f }
            // El alféizar y el recerco de la ventana alta, y las impostas de la puerta, también dan la suya.
            drawRect(c, Offset(18f + r.ax.coerceIn(-2f, 2f), 111.6f), Size(26f, (2.4f * r.tan * 0.5f).coerceIn(0.8f, 4f)))
            val lomo = (3.4f * r.tan * 0.55f).coerceIn(0.8f, 5f)
            drawRect(c, Offset(ESQUINA, CARA), Size(ANCHO - ESQUINA, lomo))
            var x = ESQUINA; while (x < ANCHO) { drawOval(c, Offset(x + 1f, CARA + lomo - 1.2f), Size(7.5f, 2.4f)); x += 9.5f }
            // La rosca del arco da sombra dentro del vano; y el muro alto, sobre la tapia, por la tarde.
            if (r.lat < 0f) drawPath(poligono(Offset(ESQUINA, CAP), Offset(ESQUINA + minOf(60f, 12f * r.cot), CAP), Offset(ESQUINA + minOf(60f, 12f * r.cot) * 0.4f, BASE), Offset(ESQUINA, BASE)), c)
            sombraDelNaranjo(r, c, enCosas = false)
            cosas.forEach { sombraArrojada(r, it.forma, it.pie, c, BASE, if (it.pie.x < ESQUINA) ALERO else CAP, if (it.pie.x < ESQUINA) 0f else ESQUINA, if (it.pie.x < ESQUINA) ESQUINA else ANCHO) }
        }
        jazmin(l, r, est)
        colgaduras(m, l, r)
        cosas.forEach { it.pintar(this) }
        capaSombra(r, 0.8f) { c -> cosas.filter { it.bajoElFollaje }.forEach { k -> clipPath(k.forma) { sombraDelNaranjo(r, c, enCosas = true) } } }
        if (nevado(m)) nievePosada(l, r)
    }

    /** Un desconchón: donde se cayó el encalado asoma el ladrillo, con sus hiladas. */
    private fun DrawScope.desconchon(l: Luces, r: Rayo, c: Offset, rad: Float, az: Azar) {
        val pts = (0 until 11).map { val a = it / 11f * 6.283f; val d = rad * az.entre(0.4f, 1.15f); Offset(c.x + cos(a) * d * 1.5f, c.y + sin(a) * d * 0.8f) }
        val forma = curva(pts + pts.first()).apply { close() }
        val junta = l.tono(Color(0xFFE8D8BC))
        drawPath(forma, l.tono(LADRILLO))
        clipPath(forma) {
            var y = c.y - rad; var fila = 0
            while (y < c.y + rad) {
                drawLine(junta.copy(alpha = 0.7f), Offset(c.x - rad * 2f, y), Offset(c.x + rad * 2f, y), 0.5f)
                var x = c.x - rad * 2f + (fila % 2) * 3.5f
                while (x < c.x + rad * 2f) { drawLine(junta.copy(alpha = 0.6f), Offset(x, y), Offset(x, y + 2.6f), 0.45f); x += 7f }
                y += 2.6f; fila++
            }
            translate(-r.lat * 1.1f, 1.3f) { drawPath(forma, mult(Color.White, r.color).copy(alpha = 0.55f * (0.4f + 0.6f * r.dia)), style = Stroke(2f), blendMode = BlendMode.Multiply) }
        }
        drawPath(forma, l.tono(Color.White).copy(alpha = 0.5f), style = Stroke(0.45f))
    }

    /** El encalado de un paño de muro: los brochazos, las chorreras que bajan de arriba y la humedad que sube del suelo. */
    private fun DrawScope.encalado(l: Luces, r: Rayo, x0: Float, x1: Float, y0: Float, az: Azar, brochazos: Int, chorreras: Int) {
        val cal = l.tono(CAL); val calLuz = l.tono(lerp(CAL, Color.White, 0.55f)); val calSombra = l.tono(mult(CAL, r.color, 0.8f))
        drawRect(Brush.verticalGradient(listOf(calLuz, cal, lerp(cal, calSombra, 0.3f)), y0, BASE), Offset(x0, y0), Size(x1 - x0, BASE - y0))
        clipRect(x0, y0, x1, BASE) {
            repeat(brochazos) {
                val p = Offset(az.entre(x0, x1), az.entre(y0, BASE)); val w = az.entre(4f, 16f)
                drawLine((if (az.real() > 0.5f) calLuz else calSombra).copy(alpha = 0.10f + 0.12f * az.real()), p, p + Offset(w, az.entre(-1.2f, 1.2f)), az.entre(0.8f, 2.4f), StrokeCap.Round)
            }
            repeat(chorreras) {
                val x = az.entre(x0, x1); val h = az.entre(6f, 26f)
                drawRect(Brush.verticalGradient(listOf(l.tono(Color(0xFF8C8478)).copy(alpha = 0.24f), Color.Transparent), y0, y0 + h), Offset(x, y0), Size(az.entre(0.8f, 2.6f), h))
            }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFF8A7A62)).copy(alpha = 0.22f)), BASE - 16f, BASE), Offset(x0, BASE - 16f), Size(x1 - x0, 16f))
        }
    }

    /** El zócalo de almagra de un paño de muro: el borde de arriba, comido; la pintura, saltada. */
    private fun DrawScope.zocalo(l: Luces, x0: Float, x1: Float, arriba: Float, az: Azar) {
        val cal = l.tono(CAL)
        val forma = Path().apply {
            moveTo(x0, BASE); lineTo(x0, arriba)
            var x = x0
            while (x < x1) { x = minOf(x1, x + az.entre(4f, 10f)); lineTo(x, arriba + az.entre(-0.9f, 1.1f)) }
            lineTo(x1, BASE); close()
        }
        drawPath(forma, Brush.verticalGradient(listOf(l.tono(lerp(ALMAGRA, CAL, 0.18f)), l.tono(lerp(ALMAGRA, Color(0xFF4A2A22), 0.25f))), arriba, BASE))
        clipPath(forma) {
            repeat(((x1 - x0) / 6f).toInt()) { drawOval(cal.copy(alpha = az.entre(0.2f, 0.55f)), Offset(az.entre(x0, x1), az.entre(arriba, BASE)), Size(az.entre(1.5f, 7f), az.entre(0.7f, 2f))) }
            // Un filete blanco, casi borrado, cerca del borde.
            drawLine(cal.copy(alpha = 0.5f), Offset(x0, arriba + 2.6f), Offset(x1, arriba + 2.6f), 0.5f)
        }
    }

    /** La tapia baja, por encima de la cual se ve la ciudad: encalada, con su zócalo de almagra y su lomo de teja. */
    private fun DrawScope.tapia(m: Momento, l: Luces, r: Rayo, az: Azar) {
        val x0 = ESQUINA
        encalado(l, r, x0, ANCHO, CARA, az, 150, 16)
        zocalo(l, x0, ANCHO + 2f, BASE - 17f, az)
        clipRect(x0, CARA, ANCHO, BASE) {
            listOf(Triple(212f, 206f, 7f), Triple(262f, 199f, 4.5f), Triple(150f, 212f, 5f), Triple(346f, 204f, 6f)).forEach { (cx, cy, rad) -> desconchon(l, r, Offset(cx, cy), rad, az) }
            repeat(3) {
                var p = Offset(az.entre(x0 + 20f, ANCHO - 20f), CARA); val g = Path().apply { moveTo(p.x, p.y) }
                repeat(7) { p += Offset(az.entre(-3f, 3f), az.entre(2f, 6f)); g.lineTo(p.x, p.y) }
                drawPath(g, l.tono(Color(0xFF6A6058)).copy(alpha = 0.5f), style = Stroke(0.4f))
            }
        }
        // El lomo de teja.
        val teja = l.tono(TEJA); val tejaLuz = l.tono(lerp(TEJA, Color(0xFFFFE0B0), 0.45f)); val tejaOsc = l.tono(mult(lerp(TEJA, Color(0xFF7A4028), 0.5f), r.color, 0.6f))
        drawRect(tejaLuz, Offset(x0 - 1f, CAP), Size(ANCHO - x0 + 2f, 3f))
        drawRect(Brush.verticalGradient(listOf(teja, tejaOsc), CAP + 3f, CARA + 0.6f), Offset(x0 - 1f, CAP + 3f), Size(ANCHO - x0 + 2f, CARA + 0.6f - CAP - 3f))
        var x = x0 - 1f
        while (x < ANCHO) {
            drawLine(tejaOsc.copy(alpha = 0.85f), Offset(x, CAP + 0.2f), Offset(x, CARA + 0.5f), 0.7f)
            drawOval(tejaLuz.copy(alpha = 0.6f), Offset(x + (if (r.lat >= 0f) 5.4f else 1.2f), CAP + 3.3f), Size(3f, 1.7f))
            if (az.real() > 0.78f) drawCircle(l.tono(Color(0xFFB8B070)).copy(alpha = 0.7f), az.entre(0.5f, 1.1f), Offset(x + az.entre(1f, 8f), CAP + az.entre(0.6f, 2.4f)))
            x += 9.5f
        }
    }

    /** El punto del arco de la puerta que está a [phi] radianes de la clave (negativo, a la izquierda) y a [rad] del centro. */
    private fun enElArco(phi: Float, rad: Float) = Offset(ARCO.x + rad * sin(phi), ARCO.y - rad * cos(phi))

    /** El vano de la puerta: las jambas y el arco de herradura, que se cierra más abajo de su centro. */
    private fun vanoDeLaPuerta(): Path {
        val tope = kotlin.math.asin(VANO / RADIO); val fin = (Math.PI - tope).toFloat()
        return Path().apply {
            moveTo(ARCO.x - VANO, BASE)
            lineTo(enElArco(-fin, RADIO))
            for (i in 1..40) lineTo(enElArco(-fin + 2f * fin * i / 40f, RADIO))
            lineTo(ARCO.x + VANO, BASE); close()
        }
    }

    /**
     * El muro de la sala: encalado, con su zócalo de almagra y su alero de teja. En él, la puerta de arco de herradura,
     * con las dovelas rojas y blancas y el alfiz que la enmarca, y el nicho del candil.
     */
    private fun DrawScope.muroDeLaSala(m: Momento, l: Luces, r: Rayo, az: Azar) {
        val cal = l.tono(CAL); val calLuz = l.tono(lerp(CAL, Color.White, 0.55f)); val calSombra = l.tono(mult(CAL, r.color, 0.8f))
        encalado(l, r, -2f, ESQUINA, ALERO, az, 260, 22)
        zocalo(l, -2f, ESQUINA, BASE - 24f, az)
        clipRect(0f, ALERO, ESQUINA, BASE) {
            listOf(Triple(88f, 98f, 5.6f), Triple(112f, 196f, 5f), Triple(8f, 190f, 5f), Triple(6f, 138f, 4.4f)).forEach { (cx, cy, rad) -> desconchon(l, r, Offset(cx, cy), rad, az) }
            repeat(3) {
                var p = Offset(az.entre(10f, ESQUINA - 10f), ALERO); val g = Path().apply { moveTo(p.x, p.y) }
                repeat(9) { p += Offset(az.entre(-3f, 3f), az.entre(3f, 8f)); g.lineTo(p.x, p.y) }
                drawPath(g, l.tono(Color(0xFF6A6058)).copy(alpha = 0.5f), style = Stroke(0.4f))
            }
        }
        // Las chorreras que bajan del alero: el agua de cada canal ha ido dejando su reguero, verdoso arriba.
        clipRect(0f, ALERO, ESQUINA, BASE) {
            var cx = 3f
            while (cx < ESQUINA - 4f) {
                val h = az.entre(9f, 34f); val w = az.entre(1.2f, 3f)
                drawRect(Brush.verticalGradient(listOf(l.tono(Color(0xFF7E8468)).copy(alpha = 0.34f), l.tono(Color(0xFF8C8478)).copy(alpha = 0.16f), Color.Transparent), ALERO + 1f, ALERO + 1f + h), Offset(cx, ALERO + 1f), Size(w, h))
                cx += az.entre(7f, 15f)
            }
        }
        ventanaAlta(m, l, r)
        // La esquina del muro: un filo de luz o de sombra, según de dónde venga el sol.
        drawRect(if (r.lat >= 0f) calLuz else lerp(cal, calSombra, 0.7f), Offset(ESQUINA - 2.2f, ALERO), Size(2.2f, BASE - ALERO))

        // ---- La puerta.
        val tope = kotlin.math.asin(VANO / RADIO); val fin = (Math.PI - tope).toFloat()
        val fuera = RADIO + 12.5f
        // El alfiz: un recuadro apenas rehundido, con su moldura.
        val ax0 = ARCO.x - fuera - 4f; val ax1 = ARCO.x + fuera + 4f; val ay0 = ARCO.y - fuera - 4.4f; val ay1 = enElArco(fin, RADIO).y + 2f
        drawRect(lerp(cal, calSombra, 0.22f), Offset(ax0, ay0), Size(ax1 - ax0, ay1 - ay0))
        drawRect(calSombra.copy(alpha = 0.6f), Offset(ax0, ay0), Size(ax1 - ax0, ay1 - ay0), style = Stroke(1.2f))
        drawLine(calLuz, Offset(ax0 - 0.8f, ay0 - 0.8f), Offset(ax1 + 0.8f, ay0 - 0.8f), 0.7f); drawLine(calLuz, Offset(if (r.lat >= 0f) ax1 + 0.8f else ax0 - 0.8f, ay0 - 0.8f), Offset(if (r.lat >= 0f) ax1 + 0.8f else ax0 - 0.8f, ay1), 0.7f)
        // Una roseta pintada en cada albanega, medio borrada.
        for (s in listOf(-1f, 1f)) {
            val c = Offset(ARCO.x + s * (fuera - 4f), ay0 + 9f)
            drawCircle(l.tono(ALMAGRA).copy(alpha = 0.55f), 3.6f, c, style = Stroke(0.7f))
            for (k in 0 until 6) { val a = k * 1.047f; drawCircle(l.tono(ALMAGRA).copy(alpha = 0.5f), 0.9f, c + Offset(cos(a) * 2f, sin(a) * 2f)) }
        }
        // Las dovelas, rojas y blancas, alternadas: de ladrillo y de piedra.
        val n = 15
        for (i in 0 until n) {
            val a0 = -fin + 2f * fin * i / n; val a1 = -fin + 2f * fin * (i + 1) / n
            val forma = Path().apply {
                moveTo(enElArco(a0, RADIO)); for (k in 1..4) lineTo(enElArco(a0 + (a1 - a0) * k / 4f, RADIO))
                for (k in 4 downTo 0) lineTo(enElArco(a0 + (a1 - a0) * k / 4f, fuera)); close()
            }
            val roja = i % 2 == 0
            val propio = if (roja) lerp(ALMAGRA, LADRILLO, 0.25f + 0.3f * az.real()) else lerp(MARMOL, PIEDRA, 0.2f + 0.3f * az.real())
            // La mitad del arco que mira al sol, más clara.
            val lado = sin((a0 + a1) / 2f) * r.lat
            drawPath(forma, l.tono(if (lado > 0f) lerp(propio, Color(0xFFFFF2D0), 0.18f * r.contraste) else mult(propio, r.color, 0.3f * r.contraste)))
            clipPath(forma) {
                if (roja) for (k in 1..3) { val rr = RADIO + (fuera - RADIO) * k / 4f; drawLine(l.tono(CAL).copy(alpha = 0.4f), enElArco(a0, rr), enElArco(a1, rr), 0.35f) }
                if (az.real() > 0.55f) mancha(enElArco((a0 + a1) / 2f, RADIO + az.entre(2f, 10f)), 3f, 2f, cal, 0.4f)
            }
            drawPath(forma, l.tono(Color(0xFF7A6A5A)).copy(alpha = 0.6f), style = Stroke(0.4f))
        }
        // Las impostas, de piedra.
        for (s in listOf(-1f, 1f)) {
            val p = enElArco(s * fin, RADIO)
            drawRect(l.tono(MARMOL), Offset(if (s < 0f) p.x - 13f else p.x - 0.5f, p.y - 1f), Size(13.5f, 3.6f))
            drawRect(l.tono(mult(MARMOL, r.color, 0.6f)), Offset(if (s < 0f) p.x - 13f else p.x - 0.5f, p.y + 1.6f), Size(13.5f, 1f))
        }
        // El vano: la sala a oscuras (o con su luz), la cortina recogida a un lado y el umbral.
        val vano = vanoDeLaPuerta()
        val encendida = salaEncendida(m)
        val dentro = l.tono(Color(0xFF2E2630)); val dentroLuz = lerp(l.tono(Color(0xFF5A4A44)), Color(0xFFE0A860), if (encendida) 0.5f else 0f)
        drawPath(vano, Brush.verticalGradient(listOf(lerp(dentro, dentroLuz, if (encendida) 0.12f else 0f), lerp(dentro, dentroLuz, if (encendida) 0.45f else 0.4f), lerp(dentro, dentroLuz, if (encendida) 0.8f else 0.5f)), ARCO.y - RADIO, BASE))
        clipPath(vano) {
            // El grueso del muro, que se ve por el lado contrario a quien mira (está a la izquierda del centro del cuadro).
            val grueso = Path().apply { moveTo(ARCO.x - VANO, BASE); lineTo(enElArco(-fin, RADIO)); for (i in 1..20) lineTo(enElArco(-fin + fin * i / 20f, RADIO)); for (i in 20 downTo 0) lineTo(enElArco(-fin + fin * i / 20f, RADIO) + Offset(5f, 1.5f)); lineTo(ARCO.x - VANO + 5f, BASE); close() }
            drawPath(grueso, Brush.verticalGradient(listOf(lerp(cal, calSombra, 0.9f), lerp(cal, calSombra, 0.5f)), ARCO.y - RADIO, BASE))
            // Al fondo, una alacena y el borde de un tapiz, que apenas se adivinan.
            // Al fondo, un arca y, encima, un nicho con su jarro: apenas se adivinan.
            drawRoundRect(l.tono(Color(0xFF4A3A3A)).copy(alpha = 0.45f), Offset(ARCO.x - 14f, BASE - 22f), Size(22f, 14f), androidx.compose.ui.geometry.CornerRadius(1.5f))
            drawLine(l.tono(BRONCE).copy(alpha = 0.5f), Offset(ARCO.x - 14f, BASE - 16f), Offset(ARCO.x + 8f, BASE - 16f), 0.7f)
            drawPath(Path().apply { moveTo(ARCO.x - 9f, BASE - 34f); lineTo(ARCO.x - 9f, BASE - 44f); cubicTo(ARCO.x - 9f, BASE - 50f, ARCO.x + 1f, BASE - 50f, ARCO.x + 1f, BASE - 44f); lineTo(ARCO.x + 1f, BASE - 34f); close() }, l.tono(Color(0xFF3A2E34)).copy(alpha = 0.5f))
            drawRect(l.tono(CARMESI).copy(alpha = 0.3f), Offset(ARCO.x - 18f, BASE - 8f), Size(34f, 3f))
            // La cortina: añil, recogida con un cordón a la jamba derecha.
            val tela = l.tono(lerp(ANIL, Color(0xFF1A2438), if (encendida) 0.1f else 0.35f)); val pliegue = l.tono(lerp(ANIL, Color(0xFF101828), 0.55f)); val clara = l.tono(lerp(ANIL, Color(0xFFC8D8E8), 0.3f))
            val cortina = Path().apply {
                moveTo(ARCO.x - 2f, ARCO.y - RADIO - 2f); lineTo(ARCO.x + RADIO + 2f, ARCO.y - RADIO - 2f); lineTo(ARCO.x + VANO + 2f, BASE)
                lineTo(ARCO.x + VANO - 8f, BASE); cubicTo(ARCO.x + VANO - 3f, BASE - 20f, ARCO.x + VANO - 1f, BASE - 30f, ARCO.x + VANO - 5f, BASE - 38f)
                cubicTo(ARCO.x + 8f, BASE - 50f, ARCO.x + 2f, BASE - 70f, ARCO.x - 2f, ARCO.y - RADIO - 2f); close()
            }
            drawPath(cortina, tela)
            clipPath(cortina) {
                for (i in 0..5) { val x = ARCO.x + 4f + i * 4.6f; drawPath(Path().apply { moveTo(x - 4f, ARCO.y - RADIO); cubicTo(x, BASE - 66f, x + 6f, BASE - 50f, ARCO.x + VANO - 4f, BASE - 38f) }, if (i % 2 == 0) pliegue else clara.copy(alpha = 0.6f), style = Stroke(0.9f)) }
                for (i in 0..2) drawLine(pliegue, Offset(ARCO.x + VANO - 6f + i * 2.4f, BASE - 36f), Offset(ARCO.x + VANO - 7f + i * 3f, BASE), 0.8f)
                drawRect(l.tono(ORO).copy(alpha = 0.8f), Offset(ARCO.x - 4f, BASE - 14f), Size(40f, 1.4f)); drawRect(l.tono(ORO).copy(alpha = 0.8f), Offset(ARCO.x - 4f, BASE - 10.6f), Size(40f, 0.6f))
            }
            drawLine(l.tono(ORO), Offset(ARCO.x + VANO - 7f, BASE - 38f), Offset(ARCO.x + VANO + 1f, BASE - 39.6f), 1.2f, StrokeCap.Round)
            // La luz del día entra un trecho por el suelo de la sala.
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFFC8B090)).copy(alpha = 0.5f * (1f - l.noche))), BASE - 16f, BASE), Offset(ARCO.x - VANO, BASE - 16f), Size(2f * VANO, 16f))
        }
        drawPath(vano, l.tono(Color(0xFF6A5A4E)).copy(alpha = 0.7f), style = Stroke(0.6f))
        // El umbral: un escalón de piedra, gastado en el medio.
        val u0 = ARCO.x - VANO - 5f; val u1 = ARCO.x + VANO + 5f
        drawPath(poligono(Offset(u0, BASE - 1f), Offset(u1, BASE - 1f), Offset(u1 + 3f, BASE + 3.4f), Offset(u0 - 3f, BASE + 3.4f)), l.tono(lerp(MARMOL, PIEDRA, 0.4f)))
        drawRect(l.tono(mult(PIEDRA, r.color, 0.6f)), Offset(u0 - 3f, BASE + 3.4f), Size(u1 - u0 + 6f, 2.8f))
        mancha(Offset(ARCO.x, BASE + 1.4f), 14f, 1.6f, l.tono(Color(0xFFB8A888)), 0.5f)
        // Unas babuchas, dejadas al entrar.
        for ((dx, gira) in listOf(-15f to -0.3f, -9.4f to 0.25f)) {
            val q = Offset(ARCO.x + dx, BASE + 6.6f)
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(q.x - 3.4f, q.y - 0.6f), Size(7.4f, 2f))
            drawPath(Path().apply { moveTo(q.x - 3f, q.y); cubicTo(q.x - 3.4f, q.y - 2.4f, q.x + 1.6f + gira, q.y - 3f, q.x + 3.6f + gira, q.y - 1f); cubicTo(q.x + 2.4f, q.y + 0.6f, q.x - 1.6f, q.y + 0.8f, q.x - 3f, q.y); close() }, l.tono(Color(0xFFC09040)))
            drawOval(l.tono(Color(0xFF6A4420)), Offset(q.x - 2.6f, q.y - 1.8f), Size(3f, 1.5f))
        }

        // ---- El nicho del candil: un arquillo rehundido en el muro, tiznado por arriba.
        val nx = NICHO.x; val ny = NICHO.y
        val nicho = Path().apply { moveTo(nx - 6f, ny + 7f); lineTo(nx - 6f, ny - 2f); cubicTo(nx - 7f, ny - 9f, nx + 7f, ny - 9f, nx + 6f, ny - 2f); lineTo(nx + 6f, ny + 7f); close() }
        drawPath(nicho, l.tono(mult(CAL, Color(0xFF8A84A8), 0.9f)))
        clipPath(nicho) { drawRect(Brush.verticalGradient(listOf(l.tono(Color(0xFF3A3230)).copy(alpha = 0.6f), Color.Transparent), ny - 9f, ny + 1f), Offset(nx - 7f, ny - 9f), Size(14f, 10f)) }
        drawPath(nicho, calSombra.copy(alpha = 0.7f), style = Stroke(0.6f))
        drawRect(calLuz, Offset(nx - 7.4f, ny + 7f), Size(14.8f, 1.6f))
        AndalusCosas.run { candilDeBarro(l, Offset(nx - 0.5f, ny + 7f), 0.9f) }

        // ---- El alero: las bocas de las tejas, en fila, y la sombra que dan (va en la capa de sombras).
        val teja = l.tono(TEJA); val tejaLuz = l.tono(lerp(TEJA, Color(0xFFFFE0B0), 0.45f)); val tejaOsc = l.tono(mult(lerp(TEJA, Color(0xFF7A4028), 0.5f), r.color, 0.6f))
        drawRect(tejaOsc, Offset(-2f, ALERO - 1.4f), Size(ESQUINA + 7f, 3.6f))
        var x = -3f
        while (x < ESQUINA + 4f) {
            drawPath(Path().apply { moveTo(x, ALERO + 0.6f); cubicTo(x, ALERO - 6f, x + 7f, ALERO - 6f, x + 7f, ALERO + 0.6f); close() }, teja)
            drawPath(Path().apply { moveTo(x + 0.8f, ALERO - 0.4f); cubicTo(x + 0.8f, ALERO - 5f, x + 6.2f, ALERO - 5f, x + 6.2f, ALERO - 0.4f) }, tejaLuz, style = Stroke(0.8f))
            drawOval(tejaOsc, Offset(x + 1.8f, ALERO - 2.6f), Size(3.4f, 3f))
            x += 7.6f
        }
    }

    /**
     * La ventana alta de la sala: pequeña, con su arquillo de herradura, el alféizar de piedra y una celosía de madera
     * que deja ver sin ser visto. De noche, con la sala despierta, la luz se cuela por los huecos.
     */
    private fun DrawScope.ventanaAlta(m: Momento, l: Luces, r: Rayo) {
        val c = Offset(31f, 97f); val w = 8f
        val calSombra = l.tono(mult(CAL, r.color, 0.8f)); val calLuz = l.tono(lerp(CAL, Color.White, 0.55f))
        val hueco = Path().apply { moveTo(c.x - w, c.y + 11f); lineTo(c.x - w, c.y - 1f); cubicTo(c.x - w - 1.6f, c.y - 12f, c.x + w + 1.6f, c.y - 12f, c.x + w, c.y - 1f); lineTo(c.x + w, c.y + 11f); close() }
        // El recerco, apenas rehundido, con su filete de almagra.
        val recerco = Path().apply { moveTo(c.x - w - 4f, c.y + 11f); lineTo(c.x - w - 4f, c.y - 13.4f); lineTo(c.x + w + 4f, c.y - 13.4f); lineTo(c.x + w + 4f, c.y + 11f); close() }
        drawPath(recerco, lerp(l.tono(CAL), calSombra, 0.25f)); drawPath(recerco, calSombra.copy(alpha = 0.6f), style = Stroke(0.8f))
        drawPath(Path().apply { moveTo(c.x - w - 1.4f, c.y - 0.6f); cubicTo(c.x - w - 3.4f, c.y - 14f, c.x + w + 3.4f, c.y - 14f, c.x + w + 1.4f, c.y - 0.6f) }, l.tono(ALMAGRA).copy(alpha = 0.75f), style = Stroke(1.6f))
        val encendida = salaEncendida(m)
        drawPath(hueco, if (encendida) lerp(l.tono(Color(0xFF3A2E30)), Color(0xFFE8B060), 0.7f) else l.tono(Color(0xFF2A2430)))
        clipPath(hueco) {
            // La celosía: listones cruzados al sesgo.
            val liston = l.tono(if (encendida) Color(0xFF4A3220) else MADERA_OSC); val canto = l.tono(MADERA)
            var d = -30f
            while (d < 30f) {
                drawLine(liston, Offset(c.x + d - 14f, c.y + 12f), Offset(c.x + d + 14f, c.y - 16f), 1.5f); drawLine(liston, Offset(c.x + d - 14f, c.y - 16f), Offset(c.x + d + 14f, c.y + 12f), 1.5f)
                if (!encendida) drawLine(canto.copy(alpha = 0.55f), Offset(c.x + d - 14.4f, c.y + 11.6f), Offset(c.x + d + 13.6f, c.y - 16.4f), 0.4f)
                d += 5.6f
            }
            // El grueso del muro da sombra arriba y del lado del sol.
            drawPath(hueco, mult(Color.White, r.color).copy(alpha = 0.5f * (0.3f + 0.7f * r.dia)), style = Stroke(3.4f), blendMode = BlendMode.Multiply)
        }
        drawPath(hueco, l.tono(Color(0xFF6A5A4E)).copy(alpha = 0.7f), style = Stroke(0.5f))
        // El alféizar, de piedra, con su sombra y el reguero que deja debajo.
        drawRect(l.tono(lerp(MARMOL, PIEDRA, 0.3f)), Offset(c.x - w - 5f, c.y + 11f), Size(2f * w + 10f, 2.6f)); drawRect(calLuz, Offset(c.x - w - 5f, c.y + 11f), Size(2f * w + 10f, 0.7f))
        drawRect(l.tono(mult(PIEDRA, r.color, 0.7f)), Offset(c.x - w - 5f, c.y + 13.6f), Size(2f * w + 10f, 1f))
        drawRect(Brush.verticalGradient(listOf(l.tono(Color(0xFF8C8478)).copy(alpha = 0.3f), Color.Transparent), c.y + 14.6f, c.y + 30f), Offset(c.x - w - 3f, c.y + 14.6f), Size(2.2f, 15.4f))
        drawRect(Brush.verticalGradient(listOf(l.tono(Color(0xFF8C8478)).copy(alpha = 0.26f), Color.Transparent), c.y + 14.6f, c.y + 24f), Offset(c.x + w + 1f, c.y + 14.6f), Size(1.8f, 9.4f))
    }

    /** El suelo: losas de piedra en hiladas que se agrandan hacia quien mira, con sus juntas, y la humedad cerca de la alberca. */
    private fun DrawScope.suelo(l: Luces, r: Rayo, est: Estacion, az: Azar) {
        val tierra = l.tono(Color(0xFFCDB894)); val tierraOsc = l.tono(Color(0xFFA48C68))
        drawRect(Brush.verticalGradient(listOf(tierraOsc, tierra, lerp(tierra, tierraOsc, 0.35f)), BASE, ALTO), Offset(0f, BASE + 1.6f), Size(ANCHO, ALTO - BASE))
        drawRect(tierraOsc, Offset(-2f, BASE), Size(ANCHO + 4f, 2f))
        fun px(x: Float, y: Float) = 190f + (x - 190f) * (0.72f + 0.28f * (y - BASE) / (ALTO - BASE))
        listOf(BASE + 2.2f to 238.4f, 239.2f to 248.4f, 249.4f to 260.6f, 261.8f to 274.4f, 275.8f to 290f).forEachIndexed { f, (y0, y1) ->
            var x = -az.entre(40f, 80f)
            while (x < ANCHO + 90f) {
                val w = az.entre(26f, 48f) * (0.9f + 0.1f * f)
                if (az.real() > 0.1f) {
                    val a = Offset(px(x + 0.7f, y0), y0); val b = Offset(px(x + w - 0.7f, y0), y0)
                    val c = Offset(px(x + w - 0.7f + az.entre(-1.4f, 1.4f), y1), y1); val d = Offset(px(x + 0.7f + az.entre(-1.4f, 1.4f), y1), y1)
                    val tono = lerp(Color(0xFFDCCDAE), if (az.real() > 0.5f) Color(0xFFC6B28E) else Color(0xFFE8DEC6), az.real() * 0.7f).let { if (az.real() > 0.82f) lerp(it, Color(0xFFC49A7C), 0.4f) else it }
                    drawPath(poligono(a, b, c, d), Brush.verticalGradient(listOf(l.tono(lerp(tono, Color(0xFF8E7C62), 0.14f)), l.tono(tono)), y0, y1))
                    drawLine(l.tono(Color.White).copy(alpha = 0.3f), a, b, 0.5f); drawLine(l.tono(Color(0xFF5E4E3A)).copy(alpha = 0.5f), d + Offset(0f, 0.6f), c + Offset(0f, 0.6f), 0.7f)
                    if (az.real() > 0.6f) { val g = a + Offset(az.entre(2f, w * 0.6f), 1f); drawLine(l.tono(Color(0xFF7A6850)).copy(alpha = 0.5f), g, g + Offset(az.entre(3f, 9f), az.entre(2f, y1 - y0 - 2f)), 0.4f) }
                }
                x += w
            }
        }
        repeat(170) { drawCircle((if (az.real() > 0.5f) l.tono(Color(0xFF7E6A4E)) else l.tono(Color(0xFFF0E4C8))).copy(alpha = 0.36f), az.entre(0.3f, 0.9f), Offset(az.entre(0f, ANCHO), az.entre(BASE + 2f, ALTO))) }
        // El verdín de las juntas, donde salpica el agua, y la hierba al pie de los muros.
        repeat(26) { val p = Offset(az.entre(126f, 262f), az.entre(234f, 272f)); mancha(p, az.entre(2f, 6f), az.entre(0.5f, 1.2f), l.tono(Color(0xFF7A8A54)), 0.3f) }
        val hierba = lerp(hierbaDe(est, true), Color(0xFF7E8A52), 0.4f)
        listOf(2f to 12f, 108f to 118f, 250f to 262f, 300f to 316f, 364f to 378f).forEach { (a, b) -> briznas(l, a, b, BASE + 2f, hierba, az, 4.5f, 1.8f) }
        // Hojas del naranjo, caídas; y en primavera, el azahar.
        repeat(18) { val p = Offset(az.entre(250f, ANCHO), az.entre(BASE + 6f, ALTO - 2f)); val a = az.entre(0f, 3.14f); drawLine(l.tono(if (it % 3 == 0) Color(0xFFB8A050) else lerp(HOJA, Color(0xFF86A060), 0.4f)), p, p + Offset(cos(a) * 3f, sin(a) * 1.3f), 1.5f, StrokeCap.Round) }
        if (est == Estacion.PRIMAVERA) repeat(40) { drawCircle(l.tono(Color(0xFFFFFDF4)).copy(alpha = 0.9f), az.entre(0.5f, 0.9f), Offset(az.entre(240f, ANCHO), az.entre(BASE + 4f, ALTO - 2f))) }
    }

    private fun clima(m: Momento) = m.ficha.clima

    /** Con lluvia, las losas mojadas: más oscuras, con charcos que reflejan el cielo y lo encalado de los muros. */
    private fun DrawScope.charcos(l: Luces) {
        val az = Azar(Semilla.de("andalus", "charcos"))
        drawRect(l.tono(Color(0xFF5A5448)).copy(alpha = 0.22f), Offset(0f, BASE + 1.6f), Size(ANCHO, ALTO - BASE))
        val cielo = lerp(l.medio, Color.White, 0.2f)
        repeat(9) {
            val c = Offset(az.entre(10f, ANCHO - 10f), az.entre(BASE + 8f, ALTO - 4f)); val rx = az.entre(12f, 30f); val ry = az.entre(1.6f, 3.6f)
            mancha(c, rx, ry, l.tono(Color(0xFF4A463E)), 0.35f); mancha(c + Offset(1f, -0.4f), rx * 0.8f, ry * 0.6f, cielo, 0.4f)
            reflejoRoto(c.x - rx * 0.5f, c.x + rx * 0.5f, c.y - ry * 0.4f, ry * 1.2f, l.tono(CAL), 0.3f, az, 0.8f)
        }
    }

    // La alberca: el borde de fuera, de piedra, y el agua, vista desde arriba.
    private val ALB_F0 = Offset(146f, 241f); private val ALB_F1 = Offset(238f, 241f); private val ALB_C0 = Offset(134f, 266f); private val ALB_C1 = Offset(250f, 266f)

    private fun aguaDeLaAlberca() = poligono(ALB_F0 + Offset(3.6f, 3f), ALB_F1 + Offset(-3.6f, 3f), ALB_C1 + Offset(-5f, -4.4f), ALB_C0 + Offset(5f, -4.4f))

    /** La alberca: el brocal de piedra, la pared mojada del fondo, el agua con lo que refleja y las hojas que flotan. */
    private fun DrawScope.alberca(m: Momento, l: Luces, r: Rayo) {
        val az = Azar(Semilla.de("andalus", "alberca"))
        val piedra = l.tono(lerp(MARMOL, PIEDRA, 0.35f)); val luz = l.tono(lerp(MARMOL, Color.White, 0.4f)); val sombra = l.tono(mult(PIEDRA, r.color, 0.7f))
        val y0 = ALB_F0.y; val y1 = ALB_C0.y
        // El brocal: la losa de encima y el canto que mira a quien mira.
        val fuera = poligono(ALB_F0, ALB_F1, ALB_C1, ALB_C0)
        drawOval(Color.Black.copy(alpha = 0.16f), Offset(ALB_C0.x - 2f, y1 + 2.6f), Size(ALB_C1.x - ALB_C0.x + 4f, 5f))
        drawPath(poligono(ALB_C0, ALB_C1, ALB_C1 + Offset(0.6f, 5f), ALB_C0 + Offset(-0.6f, 5f)), Brush.verticalGradient(listOf(piedra, sombra), y1, y1 + 5f))
        drawPath(fuera, Brush.verticalGradient(listOf(piedra, luz), y0, y1))
        clipPath(fuera) {
            // Las juntas de las losas del brocal y su verdín.
            for (i in 0..9) { val t = i / 9f; drawLine(sombra.copy(alpha = 0.5f), ALB_F0 + (ALB_F1 - ALB_F0) * t, ALB_F0 + (ALB_F1 - ALB_F0) * t + Offset((t - 0.5f) * 2f, 3.4f), 0.4f); drawLine(sombra.copy(alpha = 0.5f), ALB_C0 + (ALB_C1 - ALB_C0) * t + Offset(0f, -4.4f), ALB_C0 + (ALB_C1 - ALB_C0) * t, 0.4f) }
            repeat(12) { mancha(Offset(az.entre(ALB_C0.x, ALB_C1.x), az.entre(y0, y1)), az.entre(2f, 7f), az.entre(0.6f, 1.4f), l.tono(Color(0xFF8A9460)), 0.22f) }
        }
        // La pared del fondo, mojada, y el agua.
        val agua = aguaDeLaAlberca()
        val a0 = ALB_F0 + Offset(3.6f, 3f); val a1 = ALB_F1 + Offset(-3.6f, 3f); val yb = y1 - 4.4f; val medio = (a0.x + a1.x) / 2f
        val hondo = l.tono(Color(0xFF2E5C5E)); val claro = lerp(l.tono(Color(0xFF6FA696)), l.medio, 0.25f)
        drawPath(agua, Brush.verticalGradient(listOf(lerp(hondo, l.bajo, 0.25f), lerp(hondo, claro, 0.5f), claro), a0.y, yb))
        clipPath(agua) {
            // La pared de enfrente, de piedra con verdín, que se ve por encima del agua, y su reflejo.
            drawRect(l.tono(Color(0xFF8E8A68)), Offset(a0.x - 2f, a0.y), Size(a1.x - a0.x + 4f, 2.8f))
            drawRect(l.tono(Color(0xFF55664A)), Offset(a0.x - 2f, a0.y + 2f), Size(a1.x - a0.x + 4f, 0.9f))
            reflejoRoto(a0.x, a1.x, a0.y + 2.8f, 5.4f, l.tono(Color(0xFF8E8A68)), 0.4f, az, 0.8f)
            // El cielo, que se mira en ella: una calma clara en medio.
            mancha(Offset(medio + 16f, a0.y + 11f), 34f, 3.6f, lerp(l.medio, Color.White, 0.25f), 0.42f * (1f - 0.5f * l.noche))
            mancha(Offset(medio - 22f, a0.y + 15f), 20f, 2f, lerp(l.alto, Color.White, 0.2f), 0.3f * (1f - 0.5f * l.noche))
            // La tapia encalada, reflejada, y la mancha oscura del telar y del naranjo por la derecha.
            reflejoRoto(medio - 10f, a1.x, a0.y + 3f, 6.4f, l.tono(CAL), 0.3f, az, 0.9f)
            mancha(Offset(a1.x - 6f, a0.y + 12f), 14f, 5f, l.tono(HOJA), 0.4f)
            repeat(30) { val p = Offset(az.entre(a0.x - 6f, a1.x), az.entre(a0.y + 3.4f, yb)); val w = az.entre(3f, 10f); drawLine((if (it % 2 == 0) lerp(l.bajo, Color.White, 0.3f) else hondo).copy(alpha = az.entre(0.14f, 0.34f)), p, p + Offset(w, 0f), 0.5f, StrokeCap.Round) }
            // El agua clara pegada al borde de delante: se ve el fondo.
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFFA9CDA8)).copy(alpha = 0.5f * (1f - 0.6f * l.noche))), yb - 5f, yb), Offset(a0.x - 12f, yb - 5f), Size(a1.x - a0.x + 24f, 5f))
            // El brocal da sombra sobre el agua, del lado del sol.
            if (r.fuerza > 0.05f) drawRect(r.color, Offset(if (r.lat >= 0f) a1.x - 5f else a0.x - 10f, a0.y), Size(12f, 24f), alpha = r.fuerza * 0.5f, blendMode = BlendMode.Multiply)
            // Hojas y, en primavera, flores de azahar, flotando.
            repeat(7) { val p = Offset(az.entre(a0.x + 4f, a1.x - 4f), az.entre(a0.y + 5f, yb - 2f)); drawOval(l.tono(if (it % 3 == 0) Color(0xFFC8A850) else lerp(HOJA, Color(0xFF86A060), 0.3f)), Offset(p.x, p.y), Size(2.8f, 1.2f)) }
            if (m.ficha.estacion == Estacion.PRIMAVERA) repeat(9) { drawCircle(l.tono(Color(0xFFFFFDF4)), 0.8f, Offset(az.entre(medio, a1.x - 4f), az.entre(a0.y + 5f, yb - 2f))) }
        }
        drawPath(agua, sombra.copy(alpha = 0.7f), style = Stroke(0.5f))
        drawLine(luz, ALB_C0 + Offset(0f, 0.2f), ALB_C1 + Offset(0f, 0.2f), 0.6f)
    }

    // ---------------------------------------------------------------- El jazmín y las colgaduras

    /** El jazmín: sube por la esquina del muro desde su arriate, corre bajo el alero y se descuelga sobre la tapia. */
    private fun DrawScope.jazmin(l: Luces, r: Rayo, est: Estacion) {
        val az = Azar(Semilla.de("andalus", "jazmin"))
        val tallo = l.tono(Color(0xFF5E5238)); val verde = l.tono(Color(0xFF3E6A42)); val claro = l.tono(lerp(Color(0xFF5E8A4E), Color(0xFFE2E6A0), 0.35f * r.dia)); val hondo = l.tono(Color(0xFF28483A))
        val flor = l.tono(Color(0xFFFFFDF2))
        val conFlor = est == Estacion.VERANO || est == Estacion.PRIMAVERA || est == Estacion.OTONO
        fun rama(puntos: List<Offset>, ancho: Float, hojas: Int) {
            drawPath(curva(puntos), tallo, style = Stroke(0.9f, cap = StrokeCap.Round))
            for (i in 0 until puntos.size - 1) {
                val a = puntos[i]; val b = puntos[i + 1]
                repeat(hojas) {
                    val p = a + (b - a) * az.real() + Offset(az.entre(-ancho, ancho), az.entre(-ancho * 0.7f, ancho * 0.7f))
                    val g = az.entre(0f, 6.28f); val n = az.entre(1.6f, 2.8f)
                    val suerte = az.real()
                    drawLine(if (suerte < 0.4f) hondo else if (suerte < 0.8f) verde else claro, p, p + Offset(cos(g) * n, sin(g) * n * 0.7f), 1.3f, StrokeCap.Round)
                    if (conFlor && az.real() > 0.9f) { drawCircle(flor, 0.75f, p + Offset(0.6f, -0.6f)); drawCircle(l.tono(SEDA), 0.25f, p + Offset(0.6f, -0.6f)) }
                }
            }
        }
        // El arriate, al pie de la esquina.
        val e = ESQUINA
        drawPath(poligono(Offset(e - 6f, BASE), Offset(e + 16f, BASE), Offset(e + 19f, BASE + 6f), Offset(e - 8f, BASE + 6f)), l.tono(Color(0xFF5A4A38)))
        drawPath(poligono(Offset(e - 8f, BASE + 6f), Offset(e + 19f, BASE + 6f), Offset(e + 19f, BASE + 8.8f), Offset(e - 8f, BASE + 8.8f)), l.tono(LADRILLO))
        rama(listOf(Offset(e + 4f, BASE + 2f), Offset(e + 1f, BASE - 22f), Offset(e + 5f, BASE - 48f), Offset(e + 1f, BASE - 76f), Offset(e + 3f, BASE - 104f), Offset(e - 1f, BASE - 128f), Offset(e - 3f, ALERO + 8f)), 4.6f, 22)
        rama(listOf(Offset(e - 3f, ALERO + 9f), Offset(e - 20f, ALERO + 8f), Offset(e - 40f, ALERO + 12f), Offset(e - 58f, ALERO + 9f), Offset(e - 70f, ALERO + 13f)), 4f, 20)
        rama(listOf(Offset(e + 3f, CAP + 2f), Offset(e + 16f, CAP - 1.5f), Offset(e + 32f, CAP), Offset(e + 46f, CAP - 1.5f)), 3.4f, 18)
        rama(listOf(Offset(e + 2f, BASE - 86f), Offset(e - 8f, BASE - 92f), Offset(e - 16f, BASE - 108f)), 3f, 12)
        // Unas guías que cuelgan del lomo de la tapia y del alero.
        for (x in listOf(e + 12f, e + 26f, e + 39f)) { val h = az.entre(8f, 17f); rama(listOf(Offset(x, CAP + 1f), Offset(x + az.entre(-2f, 2f), CAP + 1f + h * 0.5f), Offset(x + az.entre(-3f, 3f), CAP + 1f + h)), 1.8f, 7) }
        for (x in listOf(e - 16f, e - 34f, e - 56f)) { val h = az.entre(7f, 14f); rama(listOf(Offset(x, ALERO + 10f), Offset(x + az.entre(-2f, 2f), ALERO + 10f + h * 0.5f), Offset(x + az.entre(-3f, 3f), ALERO + 10f + h)), 1.8f, 7) }
    }

    /** Lo que se cuelga según el día: las colgaduras y la guirnalda de las fiestas, el paño blanco del luto, las madejas de la seda nueva y las lámparas del Ramadán. */
    private fun DrawScope.colgaduras(m: Momento, l: Luces, r: Rayo) {
        if (fiesta(m)) {
            // Colgaduras de seda sobre la tapia, una guirnalda de arrayán en el alfiz y banderines del muro al naranjo.
            colgadura(l, r, 190f, 229f, 31f, CARMESI, ORO)
            colgadura(l, r, 234f, 266f, 25f, ANIL, SEDA)
            guirnalda(l, Offset(ARCO.x - 40f, ARCO.y - RADIO - 14f), Offset(ARCO.x + 40f, ARCO.y - RADIO - 14f), 9f, m.azar("guirnalda"), hoja = ARRAYAN, flores = listOf(Color(0xFFFFFDF2), NARANJA, CARMESI), grueso = 3f)
            banderines(l, Offset(ESQUINA + 2f, ALERO + 6f), Offset(338f, 110f), 12f, listOf(CARMESI, SEDA, VIDRIADO, CAL, ANIL), 7f)
        }
        if (luto(m)) {
            // En la Córdoba omeya el luto era blanco, por oposición al negro de los abasíes (comprobado: informe de S1): un paño sin teñir sobre la tapia.
            pano(l, Offset(186f, CAP + 1f), Offset(234f, CAP + 1f), 30f, Color(0xFFF4F0E6))
        }
        if (sedaNueva(m)) {
            // Las madejas de la seda nueva, tendidas en una caña de la esquina al naranjo.
            val a = Offset(ESQUINA + 4f, 140f); val b = Offset(338f, 128f)
            drawLine(l.tono(MADERA), a, b, 1.1f)
            for (i in 0..10) {
                val p = a + (b - a) * ((i + 0.7f) / 12f); val largo = 15f + (i % 3) * 3f
                val madeja = Path().apply { moveTo(p.x - 2.4f, p.y); cubicTo(p.x - 3.6f, p.y + largo * 0.5f, p.x - 2.6f, p.y + largo, p.x, p.y + largo + 1f); cubicTo(p.x + 2.6f, p.y + largo, p.x + 3.6f, p.y + largo * 0.5f, p.x + 2.4f, p.y); close() }
                val propio = if (i % 4 == 3) lerp(SEDA, Color.White, 0.5f) else SEDA
                drawPath(madeja, Brush.horizontalGradient(if (r.lat >= 0f) listOf(l.tono(mult(propio, r.color, 0.5f)), l.tono(propio), l.tono(lerp(propio, Color.White, 0.5f))) else listOf(l.tono(lerp(propio, Color.White, 0.5f)), l.tono(propio), l.tono(mult(propio, r.color, 0.5f))), p.x - 3.4f, p.x + 3.4f))
                clipPath(madeja) { for (k in -1..1) drawLine(l.tono(Color(0xFFB89838)).copy(alpha = 0.6f), Offset(p.x + k * 1.3f, p.y), Offset(p.x + k * 1.5f, p.y + largo), 0.3f) }
                drawLine(l.tono(Color(0xFFB89838)), Offset(p.x - 2.2f, p.y + largo * 0.3f), Offset(p.x + 2.2f, p.y + largo * 0.3f), 0.5f)
            }
        }
        if (lamparas(m)) AndalusCosas.run { LAMPARAS.forEach { lampara(l, it, lamparasEncendidas(m)) } }
    }

    /**
     * Una colgadura de fiesta: un paño de seda rica echado sobre el pretil, que cae por delante con sus pliegues. Lleva
     * dos filas de ruedas bordadas, una cenefa ancha al pie (con trazos, no letras) y flecos.
     */
    private fun DrawScope.colgadura(l: Luces, r: Rayo, x0: Float, x1: Float, caida: Float, propio: Color, bordado: Color) {
        val az = Azar(Semilla.de("andalus", "colgadura", x0.toInt()))
        val w = x1 - x0; val y1 = CARA + caida
        val tela = l.tono(propio); val luz = l.tono(lerp(propio, Color.White, 0.32f)); val honda = l.tono(mult(lerp(propio, Color(0xFF1E1420), 0.4f), r.color, 0.5f)); val oro = l.tono(bordado); val oroOsc = l.tono(lerp(bordado, Color(0xFF5A3A14), 0.5f))
        // La sombra que le da a la tapia, del lado contrario al sol.
        if (r.fuerza > 0.05f) drawRect(r.color, Offset(x0 + (if (r.lat >= 0f) -3f else 3f), CARA + 1f), Size(w, caida + 2f), alpha = r.fuerza * 0.6f, blendMode = BlendMode.Multiply)
        // Lo que queda echado sobre el lomo.
        drawPath(poligono(Offset(x0 + 1.4f, CAP - 0.8f), Offset(x1 - 1.4f, CAP - 0.8f), Offset(x1, CARA + 0.4f), Offset(x0, CARA + 0.4f)), luz)
        drawLine(oro, Offset(x0 + 0.8f, CAP + 2.4f), Offset(x1 - 0.8f, CAP + 2.4f), 0.8f)
        // El paño que cae: los cantos, apenas ondulados; el pie, en tres ondas.
        val forma = Path().apply {
            moveTo(x0, CARA); lineTo(x1, CARA); cubicTo(x1 + 1.6f, CARA + caida * 0.4f, x1 - 1.2f, CARA + caida * 0.8f, x1 - 0.4f, y1)
            for (i in 3 downTo 1) { val xa = x0 + w * i / 3f; val xb = x0 + w * (i - 1) / 3f; cubicTo(xa - w * 0.1f, y1 + 2.6f, xb + w * 0.1f, y1 + 2.6f, xb + (if (i == 1) 0.4f else 0f), y1 - (if (i == 1) 0f else 0.8f)) }
            cubicTo(x0 + 1.2f, CARA + caida * 0.8f, x0 - 1.6f, CARA + caida * 0.4f, x0, CARA); close()
        }
        drawPath(forma, tela)
        clipPath(forma) {
            // Las ruedas bordadas, en dos filas, cada una con su botón y sus cuatro perlas.
            val rad = w / 8.4f
            for (fila in 0..1) for (i in 0..2) {
                val c = Offset(x0 + w * (i + 0.5f) / 3f, CARA + caida * (0.2f + 0.3f * fila))
                drawCircle(oro, rad, c, style = Stroke(0.8f)); drawCircle(oroOsc, rad * 0.72f, c, style = Stroke(0.35f)); drawCircle(oro, rad * 0.3f, c)
                for (k in 0 until 4) { val a = k * 1.571f; drawCircle(oro, 0.5f, c + Offset(cos(a) * rad * 1.5f, sin(a) * rad * 1.2f)) }
            }
            // La cenefa del pie, entre dos filetes.
            drawRect(oro, Offset(x0 - 2f, y1 - 7.6f), Size(w + 4f, 3.6f)); drawRect(oro, Offset(x0 - 2f, y1 - 9.6f), Size(w + 4f, 0.7f)); drawRect(oro, Offset(x0 - 2f, y1 - 2.8f), Size(w + 4f, 0.7f))
            var cx = x0 + 1.4f; var i = 0
            while (cx < x1 - 1.4f) { val ancho = listOf(1.2f, 2.4f, 0.8f, 1.8f)[i % 4]; drawLine(honda, Offset(cx, y1 - 6.8f), Offset(cx + ancho * 0.3f, y1 - 4.8f), 0.55f); if (i % 3 == 0) drawLine(honda, Offset(cx, y1 - 4.8f), Offset(cx + ancho, y1 - 4.8f), 0.45f); cx += ancho + 0.9f; i++ }
            // Los pliegues: franjas de sombra y de brillo que bajan del lomo, encima del bordado.
            for (k in 0 until 5) {
                val x = x0 + w * (k + 0.5f) / 5f + az.entre(-0.8f, 0.8f); val medio = w / 10f
                drawRect(Brush.horizontalGradient(listOf(Color.Transparent, honda.copy(alpha = 0.6f), Color.Transparent), x - medio, x), Offset(x - medio, CARA), Size(medio, caida + 5f))
                drawRect(Brush.horizontalGradient(listOf(Color.Transparent, luz.copy(alpha = 0.55f), Color.Transparent), x, x + medio), Offset(x, CARA), Size(medio, caida + 5f))
            }
            drawRect(Brush.verticalGradient(listOf(honda.copy(alpha = 0.55f), Color.Transparent), CARA, CARA + 4f), Offset(x0 - 2f, CARA), Size(w + 4f, 4f))
        }
        drawPath(forma, honda.copy(alpha = 0.7f), style = Stroke(0.4f))
        // Los flecos.
        var fx = x0 + 0.8f
        while (fx < x1 - 0.4f) { val t = ((fx - x0) / w * 3f) % 1f; val y = y1 + 1.9f * 4f * t * (1f - t) * 0.5f; drawLine(oro, Offset(fx, y + 0.2f), Offset(fx + az.entre(-0.5f, 0.5f), y + az.entre(3f, 4.4f)), 0.5f); fx += 1.4f }
    }

    /** Una capa de nieve posada sobre el borde que va de [a] a [b]: el lomo, abombado; el borde de abajo, irregular, con algún colgajo; y su sombra azulada. */
    private fun DrawScope.capaDeNieve(l: Luces, r: Rayo, a: Offset, b: Offset, grosor: Float, az: Azar) {
        val blanco = l.tono(Color(0xFFFCFDFF)); val sombra = l.tono(mult(Color(0xFFDDE4F4), r.color, 0.6f))
        val n = maxOf(3, ((b - a).getDistance() / 6f).toInt())
        val arriba = (0..n).map { i -> val fin = if (i == 0 || i == n) 0.2f else 1f; a + (b - a) * (i / n.toFloat()) + Offset(0f, -grosor * fin * az.entre(0.7f, 1.3f)) }
        val abajo = (0..n).map { i -> a + (b - a) * (i / n.toFloat()) + Offset(0f, az.entre(0.2f, 1.1f) + (if (az.real() > 0.82f) az.entre(0.8f, 2.4f) else 0f)) }
        val forma = curva(arriba).apply {
            val q = abajo.reversed(); lineTo(q.first())
            for (i in 1 until q.size) { val p0 = q[i - 1]; val p1 = q[i]; cubicTo(p0.x + (p1.x - p0.x) * 0.4f, p0.y + 0.7f, p0.x + (p1.x - p0.x) * 0.6f, p1.y + 0.7f, p1.x, p1.y) }
            close()
        }
        drawPath(forma, blanco)
        clipPath(forma) {
            drawPath(curva(abajo.map { it + Offset(0f, -0.3f) }), sombra, style = Stroke(1.5f))
            drawPath(curva(arriba.map { it + Offset(r.lat * 0.6f, 0.7f) }), lerp(blanco, Color.White, 0.6f), style = Stroke(0.6f))
        }
    }

    /** La nieve sobre algo redondo (una mata, una tapa, una masa de hojas): una media luna, más gruesa en lo alto. */
    private fun DrawScope.gorroDeNieve(l: Luces, r: Rayo, c: Offset, rx: Float, ry: Float, az: Azar) {
        val blanco = l.tono(Color(0xFFFCFDFF)); val sombra = l.tono(mult(Color(0xFFDDE4F4), r.color, 0.6f))
        val forma = Path().apply {
            moveTo(c.x - rx, c.y); cubicTo(c.x - rx * 0.9f, c.y - ry * 1.5f, c.x + rx * 0.9f, c.y - ry * 1.5f, c.x + rx, c.y)
            cubicTo(c.x + rx * 0.5f, c.y - ry * az.entre(0.15f, 0.5f), c.x - rx * 0.4f, c.y - ry * az.entre(0.2f, 0.6f), c.x - rx, c.y); close()
        }
        drawPath(forma, blanco)
        clipPath(forma) { drawPath(Path().apply { moveTo(c.x - rx, c.y + 0.3f); cubicTo(c.x - rx * 0.4f, c.y - ry * 0.3f, c.x + rx * 0.5f, c.y - ry * 0.25f, c.x + rx, c.y + 0.3f) }, sombra, style = Stroke(1.3f)) }
    }

    /** La nieve del suelo: montones al pie de los muros, donde el viento la arrima, y rodales en las losas. Va antes que las cosas, que la pisan. */
    private fun DrawScope.nieveEnElSuelo(l: Luces, r: Rayo) {
        val az = Azar(Semilla.de("andalus", "nieve del suelo"))
        val blanco = l.tono(Color(0xFFFCFDFF)); val sombra = l.tono(mult(Color(0xFFDDE4F4), r.color, 0.6f))
        repeat(18) {
            val p = Offset(az.entre(0f, ANCHO), az.entre(BASE + 6f, ALTO - 1f)); val rx = az.entre(8f, 24f); val ry = az.entre(1.3f, 3f)
            mancha(p + Offset(0f, 0.9f), rx, ry, sombra, 0.45f); mancha(p, rx * 0.92f, ry * 0.8f, blanco, 0.88f)
        }
        for ((x0, x1) in listOf(0f to 32f, 190f to 262f, 326f to ANCHO)) capaDeNieve(l, r, Offset(x0, BASE + 3.4f), Offset(x1, BASE + 3.4f), 2.8f, az)
    }

    /**
     * La nieve posada: una capa sobre el pretil, el tejadillo, el brocal, el castillo del telar, su banco, las macetas y
     * la tapa de la tinaja; montones al pie de los muros y rodales en el suelo. No cuaja donde hay lumbre ni agua que corre.
     */
    private fun DrawScope.nievePosada(l: Luces, r: Rayo) {
        val az = Azar(Semilla.de("andalus", "nieve"))
        capaDeNieve(l, r, Offset(ESQUINA - 1f, CAP + 0.4f), Offset(ANCHO + 2f, CAP + 0.4f), 3.2f, az)
        capaDeNieve(l, r, Offset(-2f, ALERO - 4.2f), Offset(ESQUINA + 5f, ALERO - 4.2f), 3.2f, az)
        capaDeNieve(l, r, Offset(18f, 108.4f), Offset(44f, 108.4f), 1.6f, az)                                        // el alféizar de la ventana alta
        capaDeNieve(l, r, ALB_F0 + Offset(2f, 1.2f), Offset(PILA.x - 24f, ALB_F0.y + 1.2f), 1.8f, az); capaDeNieve(l, r, Offset(PILA.x + 24f, ALB_F0.y + 1.2f), ALB_F1 + Offset(-2f, 1.2f), 1.8f, az)
        // El brocal de delante, salvo donde lo tapa el anafe.
        capaDeNieve(l, r, ALB_C0 + Offset(3f, 1.6f), Offset(ANAFE.x - 25f, ALB_C0.y + 1.6f), 2.2f, az); capaDeNieve(l, r, Offset(ANAFE.x + 25f, ALB_C0.y + 1.6f), ALB_C1 + Offset(-3f, 1.6f), 2.2f, az)
        AndalusCosas.NIEVE_EN_BORDES.forEach { (a, b, g) -> capaDeNieve(l, r, a, b, g, az) }
        AndalusCosas.NIEVE_EN_REDONDOS.forEach { (c, rx, ry) -> gorroDeNieve(l, r, c, rx, ry, az) }
    }

    // ---------------------------------------------------------------- El naranjo

    /** Hasta dónde cuelga la copa del naranjo en cada punto del cuadro: deja una ventana para el sol de la mañana y la luna. */
    private fun cuelga(x: Float): Float {
        val p = listOf(228f to 0f, 250f to 15f, 274f to 29f, 294f to 35f, 316f to 39f, 327f to 62f, 340f to 114f, 366f to 136f, 392f to 148f)
        if (x <= p.first().first) return p.first().second
        for (i in 0 until p.size - 1) if (x <= p[i + 1].first) return mezcla(p[i].second, p[i + 1].second, (x - p[i].first) / (p[i + 1].first - p[i].first))
        return p.last().second
    }

    internal fun formaDelTronco() = Path().apply {
        moveTo(335f, 270f); cubicTo(340f, 252f, 338f, 228f, 341f, 204f); cubicTo(344f, 182f, 339f, 160f, 341f, 142f)
        cubicTo(336f, 122f, 322f, 100f, 300f, 78f); lineTo(304f, 74f); cubicTo(322f, 88f, 336f, 106f, 344f, 122f)
        cubicTo(345f, 102f, 342f, 76f, 336f, 50f); lineTo(341f, 48f); cubicTo(349f, 76f, 352f, 102f, 352f, 124f)
        cubicTo(358f, 110f, 368f, 98f, 384f, 88f); lineTo(384f, 100f); cubicTo(371f, 110f, 361f, 126f, 357f, 142f)
        cubicTo(354f, 162f, 358f, 184f, 356f, 206f); cubicTo(354f, 230f, 358f, 252f, 363f, 270f); close()
    }

    /** El tronco del naranjo, en su alcorque: liso, verdoso, con sus nudos. */
    internal fun troncoCosa(l: Luces, r: Rayo): Cosa {
        val forma = formaDelTronco()
        return Cosa(forma, Offset(348f, 270f), bajoElFollaje = false) {
            // El alcorque: un cuadro de ladrillo de canto con la tierra regada dentro.
            val ladrillo = l.tono(LADRILLO); val tierra = l.tono(Color(0xFF5A4838))
            val fuera = poligono(Offset(328f, 260f), Offset(374f, 260f), Offset(380f, 276f), Offset(323f, 276f))
            drawPath(fuera, ladrillo)
            clipPath(fuera) { for (i in 0..16) drawLine(l.tono(Color(0xFF8A5236)).copy(alpha = 0.7f), Offset(324f + i * 3.6f, 259f), Offset(322f + i * 3.8f, 277f), 0.5f) }
            drawPath(poligono(Offset(331f, 262.4f), Offset(371f, 262.4f), Offset(375f, 273.4f), Offset(327.4f, 273.4f)), Brush.verticalGradient(listOf(lerp(tierra, Color.Black, 0.25f), tierra), 262f, 274f))
            drawRect(l.tono(mult(LADRILLO, r.color, 0.7f)), Offset(323f, 276f), Size(57f, 2.6f))
            val propio = Color(0xFF7C7460)
            val luz = l.tono(lerp(propio, Color(0xFFFFE9C4), 0.55f * r.contraste)); val medio = l.tono(propio); val sombra = l.tono(mult(propio, r.color, 0.9f * r.contraste))
            drawPath(forma, Brush.horizontalGradient(if (r.lat >= 0f) listOf(sombra, sombra, medio, luz, medio) else listOf(medio, luz, medio, sombra, sombra), 336f, 362f))
            val az = Azar(Semilla.de("andalus", "corteza"))
            val osc = l.tono(Color(0xFF3E3A30)); val cla = l.tono(Color(0xFFCFC8A8))
            clipPath(forma) {
                // Las ramas, vistas desde abajo, quedan a contraluz.
                drawRect(Brush.verticalGradient(listOf(osc.copy(alpha = 0.75f), Color.Transparent), 40f, 150f), Offset(290f, 30f), Size(100f, 124f))
                // La corteza: vetas que siguen el tronco, líquenes claros y los cortes de la poda.
                repeat(70) {
                    val x = az.entre(336f, 362f); val y = az.entre(110f, 266f); val h = az.entre(8f, 30f); val tuerce = az.entre(-3f, 3f)
                    drawPath(Path().apply { moveTo(x, y); cubicTo(x + tuerce, y + h * 0.3f, x - tuerce, y + h * 0.7f, x + tuerce * 0.4f, y + h) }, (if (it % 3 == 0) cla else osc).copy(alpha = az.entre(0.18f, 0.45f)), style = Stroke(az.entre(0.5f, 1.4f), cap = StrokeCap.Round))
                }
                repeat(9) { mancha(Offset(az.entre(338f, 360f), az.entre(150f, 250f)), az.entre(2f, 4f), az.entre(2f, 5f), l.tono(Color(0xFFB8C4A0)), 0.35f) }
                drawOval(osc.copy(alpha = 0.85f), Offset(344f, 184f), Size(6f, 10f)); drawOval(cla.copy(alpha = 0.6f), Offset(343f, 183f), Size(8f, 12f), style = Stroke(0.8f))
                drawOval(osc.copy(alpha = 0.6f), Offset(351f, 226f), Size(4f, 6f))
                // El verdín del pie.
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFF6A7A48)).copy(alpha = 0.5f)), 240f, 268f), Offset(334f, 240f), Size(30f, 28f))
            }
        }
    }

    /** Un ramo de hojas de naranjo: anchas, lustrosas, en abanico. [fleco], de 0 (dentro de la copa) a 1 (en el borde, al sol). */
    private fun DrawScope.ramo(l: Luces, r: Rayo, c: Offset, az: Azar, fleco: Float) {
        val hondo = l.tono(Color(0xFF233F30)); val verde = l.tono(HOJA); val medio = l.tono(Color(0xFF4A7A48)); val sol = l.tono(lerp(Color(0xFF6E9A50), Color(0xFFE6E49A), 0.5f * r.dia))
        val n = 5 + az.entero(3); val giro = az.entre(0f, 6.28f)
        for (i in 0 until n) {
            val a = giro + i * 6.283f / n + az.entre(-0.3f, 0.3f); val largo = az.entre(3.6f, 5.4f)
            val p = c + Offset(cos(a) * largo, sin(a) * largo * 0.8f)
            val suerte = az.real() - 0.35f * (fleco - 0.5f)
            val color = when { suerte < 0.36f -> hondo; suerte < 0.72f -> verde; suerte < 0.9f || cos(a) * r.lat < 0f -> medio; else -> sol }
            val n2 = Offset(-sin(a), cos(a) * 0.8f) * 1.25f
            drawPath(Path().apply { moveTo(c); cubicTo(c + (p - c) * 0.4f + n2, c + (p - c) * 0.8f + n2, p); cubicTo(c + (p - c) * 0.8f - n2, c + (p - c) * 0.4f - n2, c); close() }, color)
        }
    }

    /** La copa del naranjo, que entra por arriba y por la derecha: masas oscuras, ramos de hojas lustrosas y, según la estación, naranjas o azahar. */
    private fun DrawScope.copaDelNaranjo(m: Momento, l: Luces, r: Rayo, est: Estacion) {
        val az = Azar(Semilla.de("andalus", "naranjo"))
        val oscuro = l.tono(Color(0xFF1F3A2C)); val medio = l.tono(Color(0xFF2C5038))
        repeat(110) {
            val x = az.entre(238f, 398f); val y = az.entre(-12f, cuelga(x) * 0.74f); val rad = az.entre(6f, 14f)
            drawOval(if (it % 3 == 0) medio else oscuro, Offset(x - rad * 1.25f, y - rad * 0.75f), Size(rad * 2.5f, rad * 1.5f))
        }
        val frutas = ArrayList<Offset>()
        repeat(420) {
            val x = az.entre(230f, 396f); val tope = cuelga(x); val y = az.entre(-6f, tope * 0.94f)
            ramo(l, r, Offset(x, y), az, (y / maxOf(1f, tope)).coerceIn(0f, 1f))
            if (it % 10 == 0 && y > tope * 0.35f) frutas += Offset(x + az.entre(-3f, 3f), y + az.entre(1f, 5f))
        }
        // Unos claros: el cielo que se cuela entre las hojas.
        repeat(16) { val x = az.entre(250f, 384f); val y = az.entre(0f, cuelga(x) * 0.5f); drawOval(lerp(l.medio, l.bajo, 0.4f).copy(alpha = 0.55f), Offset(x, y), Size(az.entre(1.6f, 4f), az.entre(1f, 2.4f))) }
        // Las naranjas (del otoño a la primavera; verdes al principio) o el azahar.
        val madura = when (est) { Estacion.INVIERNO -> 1f; Estacion.PRIMAVERA -> 0.9f; Estacion.OTONO -> 0.35f; else -> -1f }
        if (madura >= 0f) frutas.forEachIndexed { i, p ->
            if (est == Estacion.PRIMAVERA && i % 3 != 0) return@forEachIndexed
            val propio = lerp(Color(0xFF7E9A3E), NARANJA, madura)
            drawCircle(l.tono(mult(propio, r.color, 0.5f)), 2.7f, p + Offset(-r.lat * 0.5f, 0.5f))
            drawCircle(l.tono(propio), 2.5f, p)
            drawCircle(l.tono(lerp(propio, Color(0xFFFFF0C0), 0.6f)), 1f, p + Offset(r.lat * 0.9f, -0.9f))
        }
        if (est == Estacion.PRIMAVERA) repeat(46) {
            val x = az.entre(240f, 392f); val y = az.entre(2f, cuelga(x) * 0.95f)
            for (k in 0..2) drawCircle(l.tono(Color(0xFFFFFDF4)), 0.9f, Offset(x + k * 1.3f - 1.3f, y + (k % 2) * 0.9f))
        }
        if (nevado(m)) repeat(30) { val x = az.entre(240f, 392f); val y = az.entre(3f, maxOf(5f, cuelga(x) * 0.72f)); gorroDeNieve(l, r, Offset(x, y), az.entre(6f, 12f), az.entre(2.2f, 3.8f), az) }
    }

    /**
     * La sombra de la copa del naranjo: cae debajo de ella a mediodía y se corre hacia el otro lado del sol cuando está
     * bajo. Es una masa de hoja menuda, con muchos claros por donde pasa el sol. Va dentro de una capa de sombra.
     */
    private fun DrawScope.sombraDelNaranjo(r: Rayo, c: Color, enCosas: Boolean) {
        val az = Azar(Semilla.de("andalus", "sombra del naranjo"))
        val cx = 322f - r.lat * 120f
        val zona = Rect(cx - 80f, CAP + 2f, cx + 80f, ALTO + 12f)
        val estira = 1f + abs(r.lat) * (r.cot / 2.4f).coerceAtMost(1.2f)
        fun punto(): Offset { val u = (az.real() + az.real()) - 1f; val v = (az.real() + az.real()) / 2f; return Offset(zona.center.x + u * zona.width / 2f, zona.top + v * zona.height) }
        clipRect(0f, if (enCosas) 120f else ALERO, ANCHO, ALTO) {
            repeat(64) { val p = punto(); val rad = az.entre(6f, 13f); drawOval(c, Offset(p.x - rad * estira, p.y - rad * 0.6f), Size(rad * 2f * estira, rad * 1.2f)) }
            repeat(150) { val p = punto() + Offset(az.entre(-22f, 22f) * estira, az.entre(-9f, 9f)); val rad = az.entre(1.2f, 3.4f); drawOval(c, Offset(p.x - rad * estira, p.y - rad * 0.5f), Size(rad * 2f * estira, rad)) }
            repeat(240) { val p = punto(); val rad = az.entre(0.8f, 2.7f); drawOval(Color.Black, Offset(p.x - rad * estira, p.y - rad * 0.5f), Size(rad * 2f * estira, rad), blendMode = BlendMode.Clear) }
        }
    }

    // ---------------------------------------------------------------- La parte viva

    override fun seMueve(m: Momento): Boolean = true                          // el río y la alberca siempre brillan

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        val l = m.luces()
        val viento = -(if (m.ficha.clima == Clima.DESPEJADO) 0.2f else m.viento)
        destellos(l, Rect(ESQUINA + 4f, H + 8f, 262f, CAP - 34f), reloj, m.semilla("destellos"), 14)

        // El humo de un horno del barrio, que la vecina enciende temprano.
        if (m.hacer == Hacer.COMER || m.hacer == Hacer.REZAR) humo(l, enLaVista(Offset(238f, 166f)), 38f, 9f, reloj, viento * 0.6f, 0.25f, 0.25f, 3, 8, 0.8f)

        // El surtidor de la pila: un hilo de agua que sube y cae, y las ondas que hace.
        val pila = PILA + Offset(0f, -19f)
        val alto = 5.4f + 0.7f * sin(reloj * 5f)
        val blanco = lerp(l.tono(Color.White), Color.White, 0.3f)
        drawLine(blanco.copy(alpha = 0.85f), pila, pila + Offset(0f, -alto), 0.8f, StrokeCap.Round)
        for (i in 0..3) { val t = ((reloj * 1.4f + i * 0.25f) % 1f); val s = if (i % 2 == 0) -1f else 1f; drawCircle(blanco.copy(alpha = 0.8f * (1f - t)), 0.5f, pila + Offset(s * (1f + 3.4f * t), -alto + 7f * t * t)) }
        for (i in 0..1) { val t = ((reloj * 0.5f + i * 0.5f) % 1f); drawOval(blanco.copy(alpha = 0.4f * (1f - t)), Offset(PILA.x + 22f - 9f * t, 252f - 1.2f * t), Size(18f * t, 2.4f * t), style = Stroke(0.4f)) }

        // El anafe: la llama y el humo cuando se guisa; brasas después.
        if (guisando(m)) {
            val k = AndalusCosas.K_ANAFE
            llama(ANAFE + Offset(-11.4f * k, -14f * k), 6f * k, reloj, 1); llama(ANAFE + Offset(11.4f * k, -14f * k), 5f * k, reloj, 2)
            resplandor(ANAFE + Offset(0f, -14f * k), 44f, 0.22f + 0.5f * l.noche)
            humo(l, ANAFE + Offset(0f, -31f * k), 70f, 16f, reloj, viento, 0.12f, 0f, 4, 10, 0.8f)
        } else if (m.hacer != Hacer.DORMIR) {
            val k = AndalusCosas.K_ANAFE
            brasas(l, ANAFE + Offset(0f, -13.6f * k), 15f * k, reloj)
            if (l.noche > 0.3f) resplandor(ANAFE + Offset(0f, -13f * k), 34f, 0.45f * l.noche, Color(0xFFFF5A1E))
        }
        // El candil del nicho, que alumbra el muro.
        if (candil(m)) { val pico = AndalusCosas.picoDelCandil(NICHO + Offset(-0.5f, 7f), 0.9f); llama(pico, 3.6f, reloj, 3); resplandor(pico + Offset(0f, -2f), 46f, 0.25f + 0.4f * l.noche) }
        // La luz de la sala, que sale por la puerta y cae en el umbral.
        if (salaEncendida(m)) resplandor(Offset(ARCO.x - 4f, BASE - 8f), 44f, 0.3f * l.noche, LUZ_DE_CASA)
        // El candil del día, encendido sobre la mesa.
        if (m.ficha.objeto == "candil" && l.noche > 0.3f && m.hacer != Hacer.DORMIR) { val pico = AndalusCosas.picoDelCandil(MESA + Offset(0f, 2.2f), AndalusCosas.K_CANDIL); llama(pico, 4.4f, reloj, 4); resplandor(pico + Offset(0f, -2f), 34f, 0.4f * l.noche) }
        // Las lámparas del Ramadán: cada una con su llama, su halo y, las de la tapia, su reflejo en la alberca.
        if (lamparasEncendidas(m)) AndalusCosas.LAMPARAS.forEachIndexed { i, p ->
            llama(p + Offset(0f, 1.6f), 3.4f, reloj, 10 + i)
            resplandor(p, 36f, 0.5f * l.noche, Color(0xFFFFC870))
            if (p.y > 160f) reflejo(Offset(p.x, 246f), 14f, LLAMA, 0.5f * l.noche, reloj, i)
        }
        // Las luces del alminar y de la ciudad, que tiemblan.
        if (lamparasEncendidas(m)) { resplandor(enLaVista(MINAR + Offset(0f, -35f)), 18f, 0.5f * l.noche); resplandor(enLaVista(Offset(282f, 154f)), 10f, 0.4f * l.noche) }

        tiempoVivo(l, m.ficha.clima, reloj, H, m.ficha.semilla)
    }

    // ---------------------------------------------------------------- El gato

    override fun estorbos(m: Momento): List<Float> = listOf(ANAFE.x)

    override fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 16f, 281f, hasta = 330f))
        add(Ancla(TipoAncla.MIRADOR, 208f, CAP))
        add(Ancla(TipoAncla.OBJETO, if (m.ficha.objeto == "jarra") 318f else MESA.x, 281f))
        add(Ancla(TipoAncla.ABRIGO, 42f, 281f))                               // en el umbral de la sala
        if (guisando(m)) add(Ancla(TipoAncla.FUEGO, ANAFE.x + 46f, 282f, hasta = ANAFE.x))
        if (fiesta(m)) add(Ancla(TipoAncla.ADORNO, 209f, 214f))
    }
}
