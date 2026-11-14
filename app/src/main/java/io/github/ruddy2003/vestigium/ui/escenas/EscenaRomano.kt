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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Casa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LLAMA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LUZ_DE_CASA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Techo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.agua
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.antorcha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.bajio
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.brasas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.casa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.estela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hierbaDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hojaDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.floor
import kotlin.math.roundToInt

/** La paleta de Romano: corta, de madera, cuero, caliza y rojo de rubia, con el Danubio gris verdoso. */
internal object ColorRomano {
    val PIEDRA = Color(0xFFDDD2B8)
    val PIEDRA_OSC = Color(0xFFA4967A)
    val MADERA = Color(0xFFB48E5A)
    val MADERA_OSC = Color(0xFF5E4630)
    val MADERA_VIEJA = Color(0xFFA89E8A)
    val CUERO = Color(0xFFB48C5C)
    val CUERO_OSC = Color(0xFF76563A)
    val ROJO = Color(0xFFA8382A)
    val ROJO_OSC = Color(0xFF6E2620)
    val ORO = Color(0xFFD2AC4E)
    val BRONCE = Color(0xFFB07A3A)
    val HIERRO = Color(0xFF70727C)
    val BARRO = Color(0xFFC07848)
    val PAJA = Color(0xFFCBAE70)
    val LINO = Color(0xFFE8DCC0)
    val TIERRA = Color(0xFFB89E72)
    val BOSQUE = Color(0xFF3E5A44)
    val RIO = Color(0xFF488A8C)
    val RIO_HONDO = Color(0xFF285C6C)
    val RIO_ORILLA = Color(0xFF9CC2AC)
    val NIEVE = Color(0xFFF0F3F6)
}

/**
 * Romano: el Danubio visto desde el camino de ronda del campamento de la Legión VII Claudia en Viminacium, donde Cayo
 * (legionario, 110 a 114 d. C.) hace sus guardias. **[Aproximación]**: es una composición con lo que había en un
 * campamento del río, no la vista desde un punto real.
 *
 * - **Al fondo:** la otra orilla, «plana y oscura, donde ya no es el imperio»: prados, tres filas de bosque, una aldea
 *   de chozas y, detrás, unos montes bajos.
 * - **En medio:** el río, ancho, con una isla de sauces; en esta orilla, el poblado pegado al campamento, el
 *   embarcadero con las barcas de la flota y, en el prado, las tiendas de cuero de las cohortes con sus fuegos.
 * - **Delante:** el camino de ronda. El parapeto, que empieza siendo una empalizada de troncos y se va rehaciendo en
 *   piedra tramo a tramo; el puesto de guardia con sus cosas (la cuba del agua, el molino de mano, las jabalinas en
 *   pabellón, el escudo en su funda, el brasero, el banco con el objeto del día) y, a la derecha, la torre, con su
 *   escalera, el farol de la puerta y el vexilo de la cohorte. Su alero enmarca el cuadro por arriba.
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG3.kt`):
 * - **La muralla** (arco `muralla`, clave `obra`): de los seis tramos del parapeto, cuántos son ya de piedra
 *   ([tramosDePiedra]); el que está en obra sube hilada a hilada, con su andamio ([enObra]). Con el arco en 1, la
 *   torre también es de piedra.
 * - **La guarnición** (arco `guarnicion`): cuántas tiendas ([tiendas]) y cuántos fuegos ([fuegos]) hay en el prado.
 * - **El estado `camino`** (seis semanas en el desfiladero): su puesto, vacío ([puesto]): faltan su escudo, sus
 *   jabalinas y su casco, y el brasero está frío.
 * - **El río** ([helado], [embarcadero]): helado los eneros que nieva y todo enero de la temporada 2; crecido y sin
 *   embarcadero cuando el deshielo se lo lleva.
 * - **Las claves:** `fiesta` (guirnaldas de laurel, cintas y, de noche, antorchas), `rosas` (el vexilo y la torre
 *   adornados con rosas), `humo` (la aldea de enfrente arde), `luto` (un paño oscuro y el vexilo con crespón),
 *   `partida` (las barcas de la flota bajan en fila con sus vexilos), `nieve` (todo blanco).
 * - **El objeto del día** y **el tramo del día:** el brasero encendido con su olla a la hora de comer; el farol de
 *   la torre al anochecer; brasas mientras duerme.
 */
internal object EscenaRomano : EscenaVida {
    override val vidas = listOf("legionario")

    /** La línea de la otra orilla contra el cielo. */
    const val H = 112f

    // El parapeto del camino de ronda: lo alto de las almenas, el fondo de los vanos y dónde toca el suelo.
    const val ALMENA = 200f
    const val VANO = 216f
    const val BASE = 248f

    /** Dónde empieza la torre (su costado); el parapeto va de 0 hasta aquí, en [TRAMOS] tramos. */
    const val TORRE = 327f
    const val TRAMOS = 6

    private val C = ColorRomano

    // ---------------------------------------------------------------- Lo que el cuento mueve

    private fun mes(m: Momento) = m.ficha.dia.substring(5, 7).toInt()
    private fun diaDelMes(m: Momento) = m.ficha.dia.substring(8, 10).toInt()

    /** Cayo está en el camino del desfiladero, lejos del campamento. */
    internal fun enCamino(m: Momento) = m.estado == "camino"

    /** Cómo está su puesto de guardia. */
    internal enum class Puesto { DE_GUARDIA, VACIO }

    /** Su puesto: con sus cosas, o vacío mientras está en el camino. Lo vigila `EscenaRomanoPrueba`. */
    internal fun puesto(m: Momento): Puesto = if (enCamino(m)) Puesto.VACIO else Puesto.DE_GUARDIA

    /** Cuántos tramos del parapeto son ya de piedra (de 0 a [TRAMOS]), contados desde la izquierda. */
    internal fun tramosDePiedra(m: Momento): Int = floor(m.arco("muralla") * TRAMOS + 0.001f).toInt().coerceIn(0, TRAMOS)

    /** Hay un tramo en obra, con su andamio: mientras la muralla sube y no ha terminado. */
    internal fun enObra(m: Momento): Boolean = m.arco("muralla") < 0.999f && (m.arco("muralla") > 0f || m.con("obra"))

    /** Cuánto lleva subido el tramo en obra, de 0 a 1. */
    internal fun avanceDelTramo(m: Momento): Float = (m.arco("muralla") * TRAMOS).let { it - floor(it + 0.001f) }.coerceIn(0f, 1f)

    /** Con la muralla terminada, la torre también es de piedra. */
    internal fun torreDePiedra(m: Momento): Boolean = m.arco("muralla") >= 0.999f

    /** Cuántas tiendas hay en el prado (de 0 a 18): baja cuando parte la vejilación. */
    internal fun tiendas(m: Momento): Int = (m.arco("guarnicion") * 18f).roundToInt().coerceIn(0, 18)

    /** Cuántos fuegos hay entre las tiendas (de 0 a 5). */
    internal fun fuegos(m: Momento): Int = (m.arco("guarnicion") * 5f).roundToInt().coerceIn(0, 5)

    internal fun nevado(m: Momento): Boolean = m.ficha.clima == Clima.NIEVE || m.con("nieve")

    /** El Danubio está helado: los eneros que nieva y, en la temporada 2, del 10 de enero a fin de mes (los sármatas lo cruzan el 18). */
    internal fun helado(m: Momento): Boolean = mes(m) == 1 && (nevado(m) || (m.ficha.temporada == 2 && diaDelMes(m) >= 10))

    internal enum class Embarcadero { ENTERO, ANEGADO, ROTO }

    /** El embarcadero: el día de la crecida queda bajo el agua; después, hasta fines de julio, solo quedan los pilotes. */
    internal fun embarcadero(m: Momento): Embarcadero = when {
        crecida(m) -> Embarcadero.ANEGADO
        m.ficha.temporada == 3 && ((mes(m) == 5 && diaDelMes(m) > 20) || mes(m) == 6 || mes(m) == 7) -> Embarcadero.ROTO
        else -> Embarcadero.ENTERO
    }

    /** El día en que el río crece con el deshielo (el día especial `crecida`, que no lleva clave de escena). */
    internal fun crecida(m: Momento): Boolean = m.ficha.especiales.any { it.dia.id == "crecida" }

    internal fun comiendo(m: Momento) = m.hacer == Hacer.COMER && !enCamino(m)
    internal fun conGente(m: Momento) = m.hacer == Hacer.GENTE && !enCamino(m)
    internal fun fiesta(m: Momento) = m.con("fiesta") || m.con("rosas")
    internal fun antorchas(m: Momento) = fiesta(m) && m.luces().noche > 0.4f

    /** Las barcas de la flota pasan río abajo a las horas de trabajo, si el río no está helado ni hay tormenta. */
    private fun pasaLaFlota(m: Momento) = m.hacer == Hacer.TRABAJO && !helado(m) && m.ficha.clima != Clima.TORMENTA && !m.con("partida") && !crecida(m)

    // ---------------------------------------------------------------- La parte quieta

    override fun DrawScope.quieta(m: Momento) {
        val l = m.luces(); val r = Rayo(l)
        val est = m.ficha.estacion
        val semilla = m.ficha.semilla
        val fijo = Azar(Semilla.de("romano", "tierra"))

        // 1. Cielo y lejanía: los montes y la otra orilla.
        cielo(l, H, semilla)
        astros(l, H, semilla)
        nubes(l, H, m.ficha.clima, semilla)
        monte(l, perfil(fijo, -30f, 410f, H + 1f, 27f, 0.5f, 34), H + 2f, Color(0xFF6C7696), 0.88f)
        monte(l, perfil(fijo, 150f, 440f, H + 1f, 17f, 0.34f, 24, isla = true), H + 2f, Color(0xFF62766A), 0.76f)
        monte(l, perfil(fijo, -70f, 130f, H + 1f, 12f, 0.3f, 14, isla = true), H + 2f, Color(0xFF66785E), 0.7f)

        // 2. El río, la otra orilla, la isla y esta orilla con el poblado, el embarcadero y las tiendas.
        rio(m, l)
        otraOrilla(m, l, r, est)
        isla(m, l, r, est)
        if (!helado(m)) estela(l, Rect(0f, 126f, ANCHO, 200f), m.azar("estela"))
        barcasEnElRio(m, l)
        estaOrilla(m, l, r, est)
        // El velo de la nieve aclara lo lejano y se corta poco más abajo de la otra orilla: va antes de lo cercano, para
        // que su borde no cruce los chopos ni la torre.
        val nevando = m.ficha.clima == Clima.NIEVE
        if (nevando) tiempoQuieto(l, m.ficha.clima, H, semilla)
        chopos(m, l, r, est)

        // 3. Primer plano: el camino de ronda, con el parapeto, el puesto de guardia y la torre.
        with(CosasRomano) { caminoDeRonda(m, l, r, est) }

        // 4. El velo del tiempo que hace y el grano del papel.
        if (!nevando) tiempoQuieto(l, m.ficha.clima, H, semilla)
        grano(Azar(Semilla.de("romano", "grano")), 0.6f + 0.4f * r.dia)
    }

    // ---------------------------------------------------------------- El río

    /** La otra orilla, de izquierda a derecha: entrantes, puntas y barras de arena, puestos a mano. */
    private val LEJANA = listOf(
        Offset(-4f, 131.4f), Offset(16f, 130f), Offset(30f, 131.8f), Offset(48f, 129.4f), Offset(66f, 130.8f), Offset(86f, 128.4f), Offset(104f, 129.8f),
        Offset(122f, 127.8f), Offset(146f, 129f), Offset(168f, 127f), Offset(190f, 128.2f), Offset(214f, 126f), Offset(238f, 127.6f), Offset(262f, 125.4f),
        Offset(288f, 126.6f), Offset(312f, 124.6f), Offset(336f, 125.8f), Offset(360f, 124f), Offset(384f, 124.8f),
    )

    /** Esta orilla: calas, lenguas de grava y la punta donde arranca el embarcadero. */
    private val ORILLA = listOf(
        Offset(-4f, 199f), Offset(12f, 196.4f), Offset(27f, 197.6f), Offset(42f, 193.2f), Offset(58f, 190.4f), Offset(72f, 192.2f), Offset(88f, 187.4f),
        Offset(104f, 185f), Offset(118f, 186.6f), Offset(134f, 183.6f), Offset(146f, 180.4f), Offset(160f, 181.6f), Offset(176f, 176.8f), Offset(192f, 177.6f),
        Offset(208f, 173.4f), Offset(226f, 171.6f), Offset(242f, 172.6f), Offset(260f, 168.2f), Offset(280f, 166.6f), Offset(298f, 167.2f), Offset(318f, 163f),
        Offset(340f, 161.4f), Offset(360f, 162f), Offset(384f, 157.6f),
    )

    /** A qué altura queda esta orilla en [x]. */
    private fun orillaEn(x: Float): Float {
        for (i in 0 until ORILLA.size - 1) if (x <= ORILLA[i + 1].x) { val a = ORILLA[i]; val b = ORILLA[i + 1]; return a.y + (b.y - a.y) * ((x - a.x) / (b.x - a.x)).coerceIn(0f, 1f) }
        return ORILLA.last().y
    }

    /** La tierra de esta orilla, con el borde corrido [k] veces hacia dentro: 0 es la línea del agua; 1, donde empieza la hierba. */
    private fun tierraDeAca(k: Float): Path = curva(ORILLA.map { p -> val cerca = ((p.y - 156f) / 44f).coerceIn(0f, 1f); p + Offset(-k * 1.2f * cerca, k * (2.2f + 3.6f * cerca)) }).apply {
        lineTo(ANCHO + 4f, 226f); lineTo(-4f, 226f); close()
    }

    private fun DrawScope.rio(m: Momento, l: Luces) {
        val zona = Rect(0f, 122f, ANCHO, 204f)
        if (helado(m)) {
            // El hielo: una losa pálida con ventisqueros, grietas y, en la corriente, un canal de agua negra.
            val hielo = l.tono(Color(0xFFDCE6EA)); val hieloLejos = l.tono(Color(0xFFC2D0DA), 0.5f)
            drawRect(Brush.verticalGradient(listOf(hieloLejos, hielo), zona.top, zona.bottom), zona.topLeft, zona.size)
            val az = Azar(Semilla.de("romano", "hielo"))
            repeat(16) { val p = az.real(); mancha(Offset(az.entre(0f, ANCHO), 130f + 68f * p), az.entre(26f, 70f) * (0.5f + p), 1.2f + 4f * p, l.tono(C.NIEVE), 0.5f) }
            repeat(9) { val p = az.real(); mancha(Offset(az.entre(0f, ANCHO), 130f + 68f * p), az.entre(20f, 60f) * (0.5f + p), 1f + 3f * p, l.tono(Color(0xFF8FA6B8)), 0.3f) }
            // Donde la corriente es más viva quedan charcos de agua negra, y el hielo se monta en crestas.
            for ((c, w) in listOf(Offset(96f, 158f) to 26f, Offset(178f, 150f) to 18f, Offset(246f, 162f) to 30f, Offset(330f, 140f) to 16f, Offset(40f, 176f) to 20f)) {
                val charco = curva(listOf(c + Offset(-w, 0.4f), c + Offset(-w * 0.4f, -1.6f), c + Offset(w * 0.3f, -1.2f), c + Offset(w, 0.2f), c + Offset(w * 0.4f, 1.6f), c + Offset(-w * 0.5f, 1.4f), c + Offset(-w, 0.4f)))
                drawPath(charco, l.tono(C.RIO_HONDO).copy(alpha = 0.8f)); drawPath(charco, l.tono(Color.White).copy(alpha = 0.7f), style = Stroke(0.7f))
            }
            repeat(26) {
                var p = Offset(az.entre(0f, ANCHO), az.entre(134f, 196f)); val g = Path().apply { moveTo(p) }
                repeat(4) { p += Offset(az.entre(4f, 16f), az.entre(-2.4f, 2.4f)); g.lineTo(p) }
                drawPath(g, l.tono(Color(0xFF6E8798)).copy(alpha = 0.4f), style = Stroke(0.4f, join = StrokeJoin.Round))
                if (it % 3 == 0) drawPath(g, l.tono(Color.White).copy(alpha = 0.8f), style = Stroke(1f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
            return
        }
        val turbio = if (crecida(m)) 0.6f else 0f
        agua(l, zona, m.azar("rio"), propio = lerp(C.RIO, Color(0xFF8E7C56), turbio), viento = m.viento, hondo = lerp(C.RIO_HONDO, Color(0xFF6A5A3E), turbio), orilla = lerp(C.RIO_ORILLA, Color(0xFFB8A678), turbio))
        // La corriente: unos hilos largos que siguen el cauce.
        val az = Azar(Semilla.de("romano", "corriente"))
        repeat(14) {
            val y = az.entre(136f, 188f); val x = az.entre(-20f, 300f); val w = az.entre(30f, 90f) * (0.4f + (y - 130f) / 64f)
            drawPath(Path().apply { moveTo(x, y); cubicTo(x + w * 0.3f, y - 1.2f, x + w * 0.7f, y - 2.6f, x + w, y - 3.2f) }, lerp(l.bajo, Color.White, 0.3f).copy(alpha = 0.16f), style = Stroke(0.6f, cap = StrokeCap.Round))
        }
    }

    private fun bosque(est: Estacion, nieve: Boolean): Color {
        val hoja = when (est) { Estacion.OTONO -> lerp(C.BOSQUE, Color(0xFF9A7A3A), 0.45f); Estacion.INVIERNO -> lerp(C.BOSQUE, Color(0xFF6E6658), 0.6f); Estacion.PRIMAVERA -> lerp(C.BOSQUE, Color(0xFF6E9A4A), 0.35f); else -> C.BOSQUE }
        return if (nieve) lerp(hoja, C.NIEVE, 0.45f) else hoja
    }

    private fun prado(est: Estacion, nieve: Boolean): Color = if (nieve) C.NIEVE else lerp(hierbaDe(est), Color(0xFF8C9470), 0.3f)

    /** La otra orilla: el prado, cuatro filas de bosque, la aldea de chozas con su cerca y los juncos del borde. */
    private fun DrawScope.otraOrilla(m: Momento, l: Luces, r: Rayo, est: Estacion) {
        val az = Azar(Semilla.de("romano", "otra orilla"))
        val nieve = nevado(m)
        val tierra = curva(LEJANA, H + 0.6f)
        // El reflejo de la orilla y del bosque, pegado a ella.
        if (!helado(m)) for (i in 0 until LEJANA.size - 1) { val y = maxOf(LEJANA[i].y, LEJANA[i + 1].y) + 0.5f
            reflejoRoto(LEJANA[i].x, LEJANA[i + 1].x, y, 9f, l.tono(bosque(est, nieve), 0.42f), 0.5f, az, 0.9f); reflejoRoto(LEJANA[i].x, LEJANA[i + 1].x, y, 3f, l.tono(Color(0xFFD8CCA8), 0.4f), 0.3f, az, 0.8f) }
        // El agua clara y la barra de arena.
        if (!helado(m)) bajio(l, curva(LEJANA), 10f, C.RIO_ORILLA)
        drawPath(curva(LEJANA), l.tono(Color(0xFFDCD0AC), 0.36f).copy(alpha = 0.95f), style = Stroke(2f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        val pr = prado(est, nieve)
        drawPath(tierra, Brush.verticalGradient(listOf(l.tono(pr, 0.74f), l.tono(pr, 0.46f)), H, 131f))
        clipPath(tierra) {
            // La calima que despega la orilla de los montes.
            mancha(Offset(190f, H + 0.6f), 240f, 4f, l.bruma, 0.2f + 0.3f * r.dia)
            // Cuatro filas de bosque: cada una más cerca, más alta y más oscura; con claros.
            val copa = bosque(est, nieve)
            for (k in 0..3) {
                val y = H + 4f + k * 3.5f; val lejos = 0.7f - 0.08f * k
                val oscuro = l.tono(mult(copa, r.color, 0.55f * r.contraste), lejos); val medio = l.tono(copa, lejos); val claro = l.tono(lerp(copa, Color(0xFFE2DC9C), 0.34f * r.dia), lejos)
                var x = -8f + k * 3f
                while (x < ANCHO + 8f) {
                    val w = az.entre(5f, 10f) + k * 1.4f
                    val hueco = az.real() < 0.16f || (k == 3 && x > 146f && x < 214f)                          // el claro de la aldea
                    if (!hueco) {
                        val h = az.entre(3.2f, 5.2f) + k * 1.1f
                        if (est == Estacion.INVIERNO && k >= 2) drawLine(l.tono(Color(0xFF4A4038), lejos), Offset(x + w / 2f, y + h * 0.7f), Offset(x + w / 2f, y - h * 0.3f), 0.5f)
                        drawOval(oscuro, Offset(x, y - h), Size(w, h * 1.7f))
                        drawOval(medio, Offset(x + w * 0.1f + r.lat * w * 0.12f, y - h), Size(w * 0.78f, h * 1.1f))
                        drawOval(claro, Offset(x + w * 0.24f + r.lat * w * 0.2f, y - h), Size(w * 0.5f, h * 0.55f))
                    }
                    x += w * az.entre(0.5f, 0.9f)
                }
            }
            // Juncos en el borde del agua.
            repeat(90) { val x = az.entre(0f, ANCHO); val i = LEJANA.indexOfLast { it.x <= x }.coerceIn(0, LEJANA.size - 2); val y = maxOf(LEJANA[i].y, LEJANA[i + 1].y) - 0.8f
                drawLine(l.tono(if (nieve) Color(0xFFB8B098) else Color(0xFF8A8A4E), 0.4f), Offset(x, y), Offset(x + az.entre(-0.6f, 0.6f), y - az.entre(1.4f, 3.2f)), 0.45f) }
        }
        // La aldea: una cerca de palos y siete chozas redondas de techo de paja. Arde el día que cruzan los sármatas.
        val arde = m.con("humo")
        for (i in 0..27) { val x = 150f + i * 2.2f; drawLine(l.tono(C.MADERA_OSC, 0.42f), Offset(x, 127.4f - (x - 150f) * 0.012f), Offset(x, 125f - (x - 150f) * 0.012f), 0.7f) }
        val chozas = listOf(Offset(156f, 125.2f), Offset(165f, 123.4f), Offset(174f, 125.6f), Offset(183f, 123f), Offset(192f, 125.2f), Offset(200f, 123.2f), Offset(207f, 125f))
        chozas.sortedBy { it.y }.forEachIndexed { i, p ->
            val w = 6.6f + (i % 3) * 1f
            if (r.fuerza > 0.05f) drawOval(r.color, Offset(if (r.lat >= 0f) p.x - w * 1.1f else p.x - 0.5f, p.y - 0.6f), Size(w * 1.3f, 1.1f), alpha = r.fuerza * 0.8f, blendMode = BlendMode.Multiply)
            drawRect(l.tono(if (arde) Color(0xFF3A302A) else Color(0xFF9A8460), 0.4f), Offset(p.x - w * 0.36f, p.y - 2f), Size(w * 0.72f, 2f))
            drawRect(l.tono(Color(0xFF2E241C), 0.4f), Offset(p.x - 0.5f, p.y - 1.5f), Size(1f, 1.5f))
            val techo = if (arde) Color(0xFF2E2622) else if (nieve) C.NIEVE else C.PAJA
            drawPath(poligono(p + Offset(-w / 2f, -1.7f), p + Offset(w / 2f, -1.7f), p + Offset(0f, -1.7f - w * 0.66f)), l.tono(techo, 0.4f))
            drawPath(poligono(p + Offset(if (r.lat >= 0f) -w / 2f else w / 2f, -1.7f), p + Offset(0f, -1.7f), p + Offset(0f, -1.7f - w * 0.66f)), l.tono(mult(techo, r.color, 0.6f * r.contraste), 0.4f))
            if (!arde && l.noche > 0.5f && i % 2 == 0 && m.hacer != Hacer.DORMIR) drawCircle(LUZ_DE_CASA.copy(alpha = 0.8f), 0.5f, p + Offset(0f, -0.8f))
        }
        // Dos piraguas varadas en la barra, frente a la aldea.
        for (x in listOf(170f, 190f)) drawPath(Path().apply { moveTo(x, 128.2f); cubicTo(x + 2f, 129.4f, x + 7f, 129.4f, x + 9f, 127.8f); lineTo(x + 8f, 128.6f); lineTo(x + 1f, 128.8f); close() }, l.tono(C.MADERA_OSC, 0.38f))
    }

    /** La isla de sauces, en medio del río: una barra de grava con su hierba, sus árboles y su reflejo. */
    private fun DrawScope.isla(m: Momento, l: Luces, r: Rayo, est: Estacion) {
        val az = Azar(Semilla.de("romano", "isla"))
        val nieve = nevado(m)
        val barra = curva(listOf(Offset(220f, 149.4f), Offset(232f, 146.8f), Offset(248f, 145.2f), Offset(268f, 144.6f), Offset(288f, 145.6f), Offset(304f, 147.4f), Offset(316f, 149.6f)), 150.2f)
        if (!helado(m)) {
            reflejoRoto(228f, 310f, 150.4f, 13f, l.tono(bosque(est, nieve), 0.26f), 0.55f, az, 1f)
            reflejoRoto(222f, 314f, 150.4f, 2.6f, l.tono(Color(0xFFD8CCA8), 0.26f), 0.4f, az, 1f)
            bajio(l, curva(listOf(Offset(214f, 150.8f), Offset(266f, 152.4f), Offset(322f, 150.6f))), 10f, C.RIO_ORILLA)
        }
        drawPath(barra, l.tono(if (nieve) C.NIEVE else Color(0xFFD2C6A6), 0.26f))
        clipPath(barra) { drawRect(l.tono(prado(est, nieve), 0.26f), Offset(232f, 143f), Size(76f, 4.6f)); drawRect(l.tono(mult(Color(0xFF8A7A5A), r.color, 0.5f), 0.26f), Offset(218f, 149.2f), Size(100f, 1.2f)) }
        val sauce = hojaDe(est)?.let { lerp(it, Color(0xFFA9B99A), 0.4f) }
        listOf(240f to 12f, 252f to 17f, 265f to 14f, 278f to 19f, 292f to 13f, 302f to 9f).forEach { (x, h) ->
            if (r.fuerza > 0.05f) drawOval(r.color, Offset(if (r.lat >= 0f) x - 8f else x - 1f, 146.6f), Size(9f, 1.4f), alpha = r.fuerza * 0.7f, blendMode = BlendMode.Multiply)
            sauce(l, r, Offset(x, 147.2f), h, sauce, nieve, az)
        }
    }

    /** Un sauce de la isla: el tronco corto y torcido y la copa ancha, con sus varas colgando. Pelado en invierno. */
    private fun DrawScope.sauce(l: Luces, r: Rayo, pie: Offset, alto: Float, hoja: Color?, nieve: Boolean, az: Azar) {
        val lejos = 0.26f; val corteza = l.tono(Color(0xFF5E5040), lejos)
        val cruz = pie + Offset(az.entre(-0.6f, 0.6f), -alto * 0.36f)
        drawLine(corteza, pie, cruz, alto * 0.09f, StrokeCap.Round)
        for (s in listOf(-1f, 1f)) drawLine(corteza, cruz, cruz + Offset(s * alto * 0.2f, -alto * 0.24f), alto * 0.05f, StrokeCap.Round)
        if (hoja == null) {
            repeat(12) { val a = -1.57f + az.entre(-1.3f, 1.3f); val q = cruz + Offset(cos(a) * alto * 0.34f, sin(a) * alto * 0.4f); drawLine(corteza.copy(alpha = 0.8f), cruz + Offset(cos(a) * alto * 0.1f, sin(a) * alto * 0.2f), q, 0.35f); drawLine(corteza.copy(alpha = 0.6f), q, q + Offset(0f, alto * 0.22f), 0.3f)
                if (nieve) drawCircle(l.tono(C.NIEVE, lejos), 0.5f, q) }
            return
        }
        val c = pie + Offset(0f, -alto * 0.66f); val rx = alto * 0.42f; val ry = alto * 0.3f
        val oscuro = l.tono(mult(hoja, r.color, 0.6f * r.contraste), lejos); val medio = l.tono(hoja, lejos); val claro = l.tono(lerp(hoja, Color(0xFFF2EEC0), 0.4f * r.dia), lejos)
        // Las varas que cuelgan, por debajo de la copa.
        repeat(16) { val x = c.x + az.entre(-1f, 1f) * rx; val y0 = c.y + ry * 0.2f; drawLine((if (it % 3 == 0) medio else oscuro), Offset(x, y0), Offset(x + az.entre(-0.5f, 0.5f), y0 + alto * az.entre(0.14f, 0.34f)), 0.5f, StrokeCap.Round) }
        drawOval(oscuro, Offset(c.x - rx, c.y - ry), Size(rx * 2f, ry * 2f))
        drawOval(medio, Offset(c.x - rx * 0.86f + r.lat * rx * 0.12f, c.y - ry), Size(rx * 1.6f, ry * 1.4f))
        repeat(5) { val q = c + Offset(az.entre(-0.6f, 0.6f) * rx + r.lat * rx * 0.2f, -ry * az.entre(0.3f, 0.9f)); drawOval(claro, Offset(q.x - rx * 0.24f, q.y - ry * 0.16f), Size(rx * 0.48f, ry * 0.36f)) }
        if (nieve) drawOval(l.tono(C.NIEVE, lejos), Offset(c.x - rx * 0.7f, c.y - ry * 1.04f), Size(rx * 1.4f, ry * 0.5f))
    }

    /**
     * Una barca de la flota del río, de costado, con la popa en [popa] (en la línea del agua): casco bajo, roda y
     * codaste curvos, una faja roja, los escudos en la borda, el palo con la vela aferrada y la caseta de popa.
     */
    private fun DrawScope.barca(l: Luces, popa: Offset, largo: Float, haciaLaDerecha: Boolean, lejos: Float, remos: Boolean = false, vexilo: Boolean = false, enSeco: Boolean = false) {
        val dibujo: DrawScope.() -> Unit = {
            val x0 = popa.x; val y = popa.y; val L = largo; val h = largo * 0.075f
            val madera = l.tono(C.MADERA_OSC, lejos); val luz = l.tono(C.MADERA, lejos)
            if (!enSeco) {
                val az = Azar(Semilla.de("barca", x0.toInt(), y.toInt()))
                reflejoRoto(x0 + L * 0.04f, x0 + L * 0.98f, y, h * 1.9f, madera, 0.55f, az, maxOf(0.7f, h * 0.24f))
                reflejoRoto(x0 + L * 0.2f, x0 + L * 0.8f, y + h * 0.8f, h * 1.2f, l.tono(C.ROJO, lejos), 0.3f, az, maxOf(0.7f, h * 0.24f))
                if (remos) for (i in 0..3) drawLine(l.tono(Color.White, lejos).copy(alpha = 0.6f), Offset(x0 - L * (0.03f + 0.09f * i), y + h * 0.16f * (i % 2)), Offset(x0 - L * (0.09f + 0.09f * i), y + h * 0.16f * (i % 2)), maxOf(0.5f, h * 0.14f), StrokeCap.Round)
            }
            val casco = Path().apply {
                moveTo(x0 + L * 0.07f, y)
                cubicTo(x0 + L * 0.01f, y - h * 0.5f, x0 - L * 0.02f, y - h * 1.6f, x0 + L * 0.03f, y - h * 2.7f)
                lineTo(x0 + L * 0.07f, y - h * 2.6f)
                cubicTo(x0 + L * 0.06f, y - h * 1.8f, x0 + L * 0.08f, y - h * 1.2f, x0 + L * 0.13f, y - h * 1.05f)
                cubicTo(x0 + L * 0.4f, y - h * 0.85f, x0 + L * 0.7f, y - h * 0.85f, x0 + L * 0.9f, y - h * 1.1f)
                cubicTo(x0 + L * 0.95f, y - h * 1.4f, x0 + L * 0.98f, y - h * 2f, x0 + L * 0.97f, y - h * 2.5f)
                lineTo(x0 + L * 1.01f, y - h * 2.4f)
                cubicTo(x0 + L * 1.03f, y - h * 1.5f, x0 + L * 1.0f, y - h * 0.6f, x0 + L * 0.93f, y)
                if (enSeco) cubicTo(x0 + L * 0.8f, y + h * 0.9f, x0 + L * 0.2f, y + h * 0.9f, x0 + L * 0.07f, y)
                close()
            }
            drawPath(casco, Brush.verticalGradient(listOf(luz, madera, madera), y - h * 1.3f, y + h * 0.3f))
            drawLine(l.tono(C.ROJO, lejos), Offset(x0 + L * 0.12f, y - h * 0.62f), Offset(x0 + L * 0.92f, y - h * 0.66f), maxOf(0.5f, h * 0.2f))
            // Los escudos en la borda.
            val n = (L / 5f).toInt().coerceAtLeast(3)
            for (i in 0 until n) drawCircle(l.tono(if (i % 2 == 0) C.ROJO else C.ORO, lejos), maxOf(0.45f, h * 0.24f), Offset(x0 + L * (0.2f + 0.62f * i / (n - 1f)), y - h * 1.05f))
            // La caseta de popa, de cuero, y el palo con la vela aferrada a la verga.
            drawPath(poligono(Offset(x0 + L * 0.1f, y - h * 1.1f), Offset(x0 + L * 0.26f, y - h * 1.05f), Offset(x0 + L * 0.24f, y - h * 2.1f), Offset(x0 + L * 0.12f, y - h * 2.2f)), l.tono(C.CUERO, lejos))
            val palo = Offset(x0 + L * 0.55f, y - h * 1f)
            drawLine(madera, palo, palo + Offset(0f, -h * 4.4f), maxOf(0.5f, h * 0.18f))
            drawLine(l.tono(C.LINO, lejos), palo + Offset(-L * 0.17f, -h * 3.9f), palo + Offset(L * 0.17f, -h * 4.1f), maxOf(0.8f, h * 0.42f), StrokeCap.Round)
            drawLine(madera.copy(alpha = 0.7f), palo + Offset(0f, -h * 4.4f), Offset(x0 + L * 0.97f, y - h * 2.5f), 0.3f)
            if (remos) for (i in 0 until (L / 4.4f).toInt()) { val x = x0 + L * (0.22f + 0.6f * i / (L / 4.4f)); drawLine(luz, Offset(x, y - h * 0.5f), Offset(x - L * 0.035f, y + h * 0.5f), maxOf(0.35f, h * 0.1f), StrokeCap.Round) }
            if (vexilo) {
                val asta = Offset(x0 + L * 0.9f, y - h * 1.2f)
                drawLine(madera, asta, asta + Offset(0f, -h * 3.4f), maxOf(0.4f, h * 0.14f))
                drawRect(l.tono(C.ROJO, lejos), asta + Offset(-h * 1.1f, -h * 3.3f), Size(h * 2.2f, h * 1.9f)); drawRect(l.tono(C.ORO, lejos), asta + Offset(-h * 1.1f, -h * 1.5f), Size(h * 2.2f, maxOf(0.4f, h * 0.3f)))
            }
        }
        if (haciaLaDerecha) dibujo() else scale(-1f, 1f, Offset(popa.x + largo / 2f, popa.y)) { dibujo() }
    }

    /** Lo que va por el agua: las barcas de la flota que bajan a sus horas, y la fila que parte hacia oriente. */
    private fun DrawScope.barcasEnElRio(m: Momento, l: Luces) {
        if (m.con("partida") && !helado(m)) {
            // La vejilación embarcada: cinco barcas en fila, río abajo, cada una con su vexilo.
            for (i in 4 downTo 0) { val k = i / 4f; val largo = 40f - 20f * k; barca(l, Offset(96f + 190f * k, 164f - 22f * k), largo, haciaLaDerecha = true, lejos = 0.12f + 0.3f * k, remos = true, vexilo = true) }
        }
        if (pasaLaFlota(m)) {
            val paso = (((m.hora * 3).toInt() % 9) / 9f)
            barca(l, Offset(16f + 190f * paso, 163f - 9f * paso), 40f - 8f * paso, haciaLaDerecha = true, lejos = 0.14f + 0.1f * paso, remos = true)
        }
    }

    // ---------------------------------------------------------------- Esta orilla

    private val CASA_ADOBE = Casa(Color(0xFFE2D4B4), Techo.PAJA, Color(0xFFC0A468), Color(0xFF4A3524), ventanas = 1)
    private val CASA_TABLA = Casa(Color(0xFFB89668), Techo.MADERA, Color(0xFF8A6C4A), Color(0xFF3E2C1E), ventanas = 1)
    private val CASA_TEJA = Casa(Color(0xFFEADFC6), Techo.TEJA, Color(0xFFB8683A), Color(0xFF4A3524), ventanas = 2, zocalo = Color(0xFF9A4A34))

    /** Dónde van las dieciocho tiendas del prado, en tres filas que siguen la orilla. Las últimas son las que se van. */
    private val SITIOS: List<Pair<Offset, Float>> = (0 until 18).map { n ->
        val fila = n % 3; val i = n / 3
        val w = 13.4f + fila * 2.6f
        val x = 184f + i * (23.5f + fila * 2.2f) + fila * 6f
        Offset(x, 186f + fila * 9.2f - (x - 190f) * 0.075f) to w
    }

    /** Dónde arden los fuegos del prado. */
    internal val FUEGOS = listOf(Offset(209f, 191.2f), Offset(259f, 187.4f), Offset(307f, 183.6f), Offset(233f, 199.8f), Offset(287f, 195.6f))

    private fun DrawScope.sombraCorta(r: Rayo, pie: Offset, ancho: Float, alto: Float) {
        if (r.fuerza < 0.05f) return
        val d = (r.ax * alto).coerceIn(-22f, 22f)
        drawOval(r.color, Offset(minOf(pie.x, pie.x + d) - ancho / 2f, pie.y - 0.7f), Size(abs(d) + ancho, maxOf(1.1f, ancho * 0.28f)), alpha = r.fuerza * 0.85f, blendMode = BlendMode.Multiply)
    }

    /**
     * Una tienda de cuero de ocho hombres (papilio), vista de tres cuartos: el frente en triángulo sobre su faldón
     * bajo, con la abertura oscura y una hoja recogida a un lado; el faldón largo del costado, de pieles cosidas, que
     * se comba entre viento y viento; el caballete, los vientos y las estacas. [n] le da su tono y sus arrugas.
     */
    private fun DrawScope.tienda(l: Luces, r: Rayo, pie: Offset, w: Float, nieve: Boolean, n: Int) {
        val lejos = 0.14f
        val az = Azar(Semilla.de("romano", "tienda", n))
        val pared = w * 0.15f; val alto = w * 0.6f; val fuga = Offset(w * 0.8f, -w * 0.065f)
        // El frente: dos esquinas en el suelo, los dos aleros y la cumbre. El costado se va hacia la derecha y hacia el fondo.
        val a = pie; val b = pie + Offset(w * 0.46f, 0f)
        val aleroA = a + Offset(0f, -pared); val aleroB = b + Offset(0f, -pared); val cima = pie + Offset(w * 0.23f, -alto)
        val bFondo = b + fuga; val aleroFondo = aleroB + fuga; val cimaFondo = cima + fuga
        sombraCorta(r, pie + Offset(w * 0.66f, 0f), w * 1.1f, w * 0.5f)
        val base = lerp(lerp(C.CUERO, C.CUERO_OSC, 0.3f + 0.2f * az.real()), Color(0xFF9A5E3A), 0.25f)
        val cuero = if (nieve) lerp(base, C.NIEVE, 0.72f) else base
        val alSol = l.tono(lerp(cuero, Color(0xFFFFE9C0), 0.46f * r.contraste), lejos); val enSombra = l.tono(mult(cuero, r.color, 0.7f * r.contraste), lejos); val medio = l.tono(cuero, lejos)
        val costura = l.tono(lerp(C.CUERO_OSC, Color(0xFF3A2A1E), 0.4f), lejos)
        // Los vientos del costado y sus estacas, por detrás del faldón.
        val cuerda = l.tono(C.LINO, lejos).copy(alpha = 0.85f); val estaca = l.tono(C.MADERA_OSC, lejos)
        for (k in listOf(0.04f, 0.5f, 0.96f)) { val q = aleroB + fuga * k; val e = b + fuga * k + Offset(w * 0.2f, w * 0.07f); drawLine(cuerda, q, e, 0.3f); drawLine(estaca, e, e + Offset(0.3f, -1.3f), 0.6f) }
        // El faldón bajo del costado y el faldón largo: el alero se comba entre viento y viento.
        drawPath(poligono(aleroB, aleroFondo, bFondo, b), if (r.lat >= 0f) medio else enSombra)
        val faldon = Path().apply {
            moveTo(cima); lineTo(cimaFondo); lineTo(aleroFondo)
            val m1 = aleroB + fuga * 0.5f
            cubicTo(aleroFondo + (m1 - aleroFondo) * 0.4f + Offset(0f, 0.9f), m1 + (aleroFondo - m1) * 0.2f + Offset(0f, 0.9f), m1)
            cubicTo(m1 + (aleroB - m1) * 0.3f + Offset(0f, 0.9f), aleroB + (m1 - aleroB) * 0.2f + Offset(0f, 0.9f), aleroB); close()
        }
        drawPath(faldon, Brush.linearGradient(if (r.lat >= 0f) listOf(medio, alSol) else listOf(enSombra, lerp(enSombra, medio, 0.5f)), cima, aleroFondo))
        clipPath(faldon) {
            // Las pieles: una costura a media altura y dos de arriba abajo; unas arrugas que bajan de la cumbre.
            val med = cima + (aleroB - cima) * 0.52f
            drawLine(costura.copy(alpha = 0.7f), med, med + fuga, 0.35f)
            for (k in listOf(0.34f, 0.67f)) { val q = cima + fuga * k; drawLine(costura.copy(alpha = 0.6f), q, q + (aleroB - cima) + Offset(0f, 0.6f), 0.35f) }
            repeat(3) { val q = cima + fuga * az.entre(0.08f, 0.94f); drawLine((if (r.lat >= 0f) enSombra else alSol).copy(alpha = 0.35f), q, q + (aleroB - cima) * az.entre(0.3f, 0.7f) + Offset(az.entre(-0.6f, 0.6f), 0f), 0.5f) }
            if (nieve) drawPath(poligono(cima, cimaFondo, cimaFondo + (aleroFondo - cimaFondo) * 0.45f, cima + (aleroB - cima) * 0.45f), l.tono(C.NIEVE, lejos).copy(alpha = 0.85f))
        }
        // El frente: le da el sol cuando viene de la izquierda. La abertura, oscura, y la hoja recogida a su lado.
        val frente = poligono(a, b, aleroB, cima, aleroA)
        drawPath(frente, if (r.lat < 0f) alSol else enSombra)
        val medioFrente = pie + Offset(w * 0.23f, 0f)
        drawPath(poligono(medioFrente + Offset(-w * 0.075f, 0f), medioFrente + Offset(w * 0.085f, 0f), cima + Offset(0.1f, alto * 0.3f)), l.tono(Color(0xFF241A14), lejos))
        drawPath(poligono(medioFrente + Offset(w * 0.085f, 0f), medioFrente + Offset(w * 0.16f, -pared * 0.9f), cima + Offset(0.1f, alto * 0.3f)), if (r.lat < 0f) l.tono(lerp(cuero, Color(0xFFFFF2D0), 0.5f), lejos) else medio)
        drawLine(costura, aleroA, aleroB, 0.3f)
        // El caballete, que asoma por delante, y los cantos del faldón.
        drawLine(costura, cima, cimaFondo, 0.6f, StrokeCap.Round)
        drawLine(l.tono(C.MADERA, lejos), cima + Offset(-0.9f, 0.1f), cima + Offset(0.5f, 0f), 0.9f, StrokeCap.Round)
        drawLine(l.tono(lerp(cuero, Color(0xFFFFF2D0), 0.5f), lejos).copy(alpha = 0.7f), cima, aleroB, 0.35f); drawLine(costura.copy(alpha = 0.6f), cima, aleroA, 0.35f)
        // Los vientos del frente, con sus estacas.
        for ((q, e) in listOf(aleroA to a + Offset(-w * 0.22f, w * 0.05f), aleroB to b + Offset(w * 0.1f, w * 0.12f), cima to pie + Offset(-w * 0.14f, w * 0.14f))) { drawLine(cuerda, q, e, 0.3f); drawLine(estaca, e, e + Offset(0.3f, -1.3f), 0.6f) }
    }

    /** Esta orilla: la grava, el talud y el prado; el camino, el poblado, el embarcadero con sus barcas y las tiendas. */
    private fun DrawScope.estaOrilla(m: Momento, l: Luces, r: Rayo, est: Estacion) {
        val az = Azar(Semilla.de("romano", "esta orilla"))
        val nieve = nevado(m); val hielo = helado(m)
        val orilla = curva(ORILLA)
        // De abajo arriba: el agua clara y su espuma, la grava mojada, el talud de tierra en sombra y la hierba.
        if (!hielo) {
            bajio(l, orilla, 15f, lerp(C.RIO_ORILLA, Color(0xFFB8A678), if (crecida(m)) 0.6f else 0f))
            drawPath(orilla, l.tono(Color.White).copy(alpha = 0.55f * (1f - 0.6f * l.noche)), style = Stroke(1.3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        val grava = if (nieve) lerp(Color(0xFFC9BEA4), C.NIEVE, 0.7f) else Color(0xFFC9BEA4)
        val tierra = tierraDeAca(if (crecida(m)) 0.9f else 0f)
        drawPath(tierra, Brush.verticalGradient(listOf(l.tono(lerp(grava, Color(0xFF7E7460), 0.35f), 0.2f), l.tono(grava, 0.1f)), 158f, 204f))
        clipPath(tierra) {
            // Cantos rodados y lenguas de lodo.
            ORILLA.forEachIndexed { i, p -> repeat(4) { val q = p + Offset(az.entre(-6f, 8f), az.entre(0.8f, 4.6f)); val w = az.entre(0.6f, 1.6f) * (0.6f + i / 30f); drawOval(l.tono(if (az.real() > 0.5f) Color(0xFF8A8070) else Color(0xFFE8E0CA), 0.15f), Offset(q.x - w, q.y - w * 0.45f), Size(w * 2f, w * 0.9f)) } }
            drawPath(tierraDeAca(0.72f), l.tono(mult(Color(0xFF8A7450), r.color, 0.55f * r.dia), 0.15f))
            val pasto = tierraDeAca(1f); val pr = prado(est, nieve)
            drawPath(pasto, Brush.verticalGradient(listOf(l.tono(pr, 0.24f), l.tono(lerp(pr, Color(0xFF6A7A46), if (nieve) 0f else 0.2f), 0.08f)), 160f, 216f))
            clipPath(pasto) {
                // El prado no es una mesa: lomas con su lado de sombra, rodales y, donde se pisa, la tierra pelada.
                repeat(9) {
                    val c = Offset(az.entre(0f, ANCHO), az.entre(170f, 212f)); val w = az.entre(26f, 64f)
                    mancha(c, w, az.entre(2f, 4.4f), l.tono(mult(pr, Color(0xFF7E86A6), 0.7f), 0.12f), 0.32f)
                    mancha(c + Offset(r.lat * 6f, -3.2f), w * 0.8f, az.entre(1.4f, 2.8f), l.tono(lerp(pr, Color(0xFFF2E8B8), 0.45f), 0.12f), 0.3f)
                }
                if (!nieve) repeat(120) { val p = Offset(az.entre(0f, ANCHO), az.entre(164f, 214f)); drawOval(l.tono(Color(0xFF5E7244), 0.14f).copy(alpha = 0.45f), p, Size(az.entre(1.4f, 4f), az.entre(0.6f, 1.2f))) }
                // Donde acampan, la hierba está gastada.
                mancha(Offset(262f, 190f), 92f, 15f, l.tono(if (nieve) Color(0xFFD8D4C8) else Color(0xFFC2B080), 0.12f), 0.5f)
            }
        }
        // Matas de mimbre y juncos en el borde, que lo rompen.
        if (!crecida(m)) listOf(20f, 66f, 112f, 170f, 236f, 292f, 352f).forEach { x ->
            val p = Offset(x, orillaEn(x) + 2.4f)
            repeat(7) { drawLine(l.tono(if (nieve) Color(0xFFB8B098) else lerp(hierbaDe(est), Color(0xFF7A7A44), 0.5f), 0.14f), p + Offset(az.entre(-4f, 4f), 1f), p + Offset(az.entre(-5f, 5f), -az.entre(2.4f, 5.6f)), 0.5f, StrokeCap.Round) }
        }

        camino(m, l, r, nieve)
        embarcaderoYBarcas(m, l, r)
        poblado(m, l, r, est, nieve)

        // Las tiendas, de atrás adelante. Donde hubo una y ya no está queda el rectángulo de hierba amarilla.
        val cuantas = tiendas(m)
        SITIOS.withIndex().sortedBy { it.value.first.y }.forEach { (n, sitio) ->
            val (pie, w) = sitio
            if (n < cuantas) tienda(l, r, pie, w, nieve, n)
            else if (!nieve) {
                // Lo que deja una tienda al irse: el rectángulo de hierba amarilla, la zanja del agua y un par de estacas.
                val hueco = poligono(pie + Offset(0f, -0.2f), pie + Offset(w * 0.46f, 0f), pie + Offset(w * 1.26f, -w * 0.065f), pie + Offset(w * 0.8f, -w * 0.065f - 0.2f))
                drawPath(hueco, l.tono(Color(0xFFD6C484), 0.14f).copy(alpha = 0.8f)); drawPath(hueco, l.tono(Color(0xFF8A7650), 0.14f).copy(alpha = 0.6f), style = Stroke(0.35f))
                drawLine(l.tono(C.MADERA_OSC, 0.14f), pie + Offset(-w * 0.1f, 0.6f), pie + Offset(-w * 0.1f + 0.3f, -0.7f), 0.6f)
            }
        }
        // Los hogares de piedra de cada fuego (la llama y el humo van en la parte viva).
        FUEGOS.take(fuegos(m)).forEach { p ->
            drawOval(l.tono(Color(0xFF6E6658), 0.14f), Offset(p.x - 2.4f, p.y - 0.9f), Size(4.8f, 1.8f)); drawOval(l.tono(Color(0xFF2E2622), 0.14f), Offset(p.x - 1.5f, p.y - 0.6f), Size(3f, 1.1f))
        }
    }

    /** El camino que baja de la puerta al poblado y al embarcadero. */
    private fun DrawScope.camino(m: Momento, l: Luces, r: Rayo, nieve: Boolean) {
        val traza = curva(listOf(Offset(384f, 207f), Offset(340f, 208f), Offset(296f, 205.6f), Offset(244f, 207.4f), Offset(196f, 205f), Offset(160f, 200f), Offset(138f, 192f), Offset(128f, 187f)))
        val ramal = curva(listOf(Offset(160f, 200f), Offset(120f, 203f), Offset(70f, 201f), Offset(30f, 204f), Offset(-4f, 203f)))
        val tierra = l.tono(if (nieve) Color(0xFFCFC8BA) else Color(0xFFD2BE94), 0.1f); val borde = l.tono(mult(Color(0xFF9A8460), r.color, 0.4f), 0.1f)
        for (p in listOf(traza, ramal)) { drawPath(p, borde, style = Stroke(5.4f, cap = StrokeCap.Round, join = StrokeJoin.Round)); drawPath(p, tierra, style = Stroke(4.2f, cap = StrokeCap.Round, join = StrokeJoin.Round)) }
        // Las rodadas de los carros.
        drawPath(traza, borde.copy(alpha = 0.55f), style = Stroke(0.5f)); drawPath(ramal, borde.copy(alpha = 0.45f), style = Stroke(0.5f))
    }

    /** El embarcadero de pilotes y las dos barcas de la flota amarradas a él. Con hielo, las barcas están varadas en la grava. */
    private fun DrawScope.embarcaderoYBarcas(m: Momento, l: Luces, r: Rayo) {
        val estado = embarcadero(m)
        val a = Offset(124f, 186.6f); val b = Offset(133f, 185f); val c = Offset(166f, 169.6f); val d = Offset(158f, 170.8f)
        val az = Azar(Semilla.de("romano", "embarcadero"))
        val pilote = l.tono(C.MADERA_OSC, 0.12f)
        // Los pilotes, con su reflejo.
        for (i in 0..5) {
            val k = i / 5f
            for (lado in listOf(a + (d - a) * k, b + (c - b) * k)) {
                if (estado == Embarcadero.ROTO && i % 2 == 1) continue
                if (!helado(m)) reflejoRoto(lado.x - 0.8f, lado.x + 0.8f, lado.y + 2.6f, 5f, pilote, 0.5f, az, 0.8f)
                drawLine(pilote, lado + Offset(0f, -1.6f), lado + Offset(0f, 2.8f), 1.3f)
            }
        }
        if (estado == Embarcadero.ENTERO) {
            if (!helado(m)) reflejoRoto(126f, 164f, 181f, 4f, pilote, 0.35f, az)
            val tablas = poligono(a, b, c, d)
            drawPath(tablas, Brush.linearGradient(listOf(l.tono(C.MADERA, 0.1f), l.tono(C.MADERA_VIEJA, 0.16f)), a, c))
            clipPath(tablas) { for (i in 1..13) { val k = i / 14f; drawLine(l.tono(C.MADERA_OSC, 0.12f).copy(alpha = 0.6f), a + (d - a) * k, b + (c - b) * k, 0.4f) } }
            drawLine(l.tono(mult(C.MADERA_OSC, r.color, 0.5f), 0.12f), b + Offset(0f, 0.9f), c + Offset(0f, 0.9f), 1.1f)
            if (nevado(m)) drawLine(l.tono(C.NIEVE, 0.1f), a + Offset(0.6f, -0.3f), d + Offset(0.6f, -0.3f), 1.2f, StrokeCap.Round)
            // Los norays, y una antorcha en la punta las noches de fiesta (va en la parte viva).
            for (p in listOf(c + Offset(-1f, -0.4f), a + (d - a) * 0.5f)) drawLine(pilote, p, p + Offset(0f, -3.6f), 1.2f, StrokeCap.Round)
        }
        when {
            helado(m) || estado != Embarcadero.ENTERO -> {
                // Varadas en la grava, cada una un poco tumbada.
                rotate(-5f, Offset(84f, 192f)) { barca(l, Offset(64f, 192.6f), 42f, haciaLaDerecha = true, lejos = 0.08f, enSeco = true) }
                rotate(4f, Offset(184f, 181f)) { barca(l, Offset(170f, 181.4f), 30f, haciaLaDerecha = true, lejos = 0.14f, enSeco = true) }
            }
            m.con("partida") -> Unit                                              // se fueron con la vejilación
            else -> {
                barca(l, Offset(90f, 181f), 42f, haciaLaDerecha = true, lejos = 0.08f)
                barca(l, Offset(168f, 169f), 30f, haciaLaDerecha = true, lejos = 0.16f)
                drawLine(l.tono(C.LINO, 0.1f).copy(alpha = 0.8f), Offset(131f, 180f), Offset(138f, 183f), 0.35f)
            }
        }
    }

    /** El poblado pegado al campamento: dos hileras de casas a lo largo del camino, con sus huertos y sus álamos. */
    private fun DrawScope.poblado(m: Momento, l: Luces, r: Rayo, est: Estacion, nieve: Boolean) {
        val az = Azar(Semilla.de("romano", "poblado"))
        val despiertos = l.noche > 0.5f && m.hacer != Hacer.DORMIR
        // Los huertos, entre las casas y el río.
        if (!nieve) listOf(Rect(22f, 199.4f, 40f, 203f), Rect(60f, 196.4f, 78f, 199.6f), Rect(146f, 189f, 160f, 192f)).forEach { h ->
            val verde = if (est == Estacion.INVIERNO) Color(0xFF8A7A58) else if (est == Estacion.OTONO) Color(0xFF8E9450) else Color(0xFF5E8A44)
            drawPath(poligono(h.topLeft + Offset(2f, 0f), h.topRight + Offset(2f, 0f), h.bottomRight, h.bottomLeft), l.tono(Color(0xFF8A7250), 0.12f))
            var x = h.left + 1.4f
            while (x < h.right) { drawLine(l.tono(verde, 0.12f), Offset(x + 1.6f, h.top + 0.4f), Offset(x, h.bottom - 0.4f), 0.7f); x += 2f }
        }
        // La hilera de atrás, junto al agua; después, los álamos; delante, la que da al camino.
        val atras = listOf(Triple(4f, 17f, CASA_ADOBE), Triple(40f, 16f, CASA_TABLA), Triple(78f, 18f, CASA_ADOBE), Triple(102f, 15f, CASA_TABLA))
        atras.forEachIndexed { i, (x, w, c) ->
            val base = orillaEn(x) + 7.4f; val h = 7.4f + (i % 2) * 1.4f
            sombraCorta(r, Offset(x + w / 2f, base), w, h)
            casa(l, Rect(x, base - h, x + w, base), if (nieve) Casa(c.muro, c.techo, C.NIEVE, c.puerta, c.ventanas) else c, 0.14f, Azar(Semilla.de("romano", "casa", i)), encendida = despiertos && i % 2 == 0)
        }
        listOf(30f to 22f, 142f to 26f, 162f to 19f).forEach { (x, h) ->
            val pie = Offset(x, orillaEn(x) + 9f)
            sombraCorta(r, pie, 3f, h * 0.7f)
            alamo(l, r, pie, h, est, nieve, az)
        }
        val delante = listOf(Triple(-4f, 22f, CASA_TEJA), Triple(22f, 19f, CASA_ADOBE), Triple(46f, 24f, CASA_TEJA), Triple(94f, 20f, CASA_TABLA), Triple(118f, 21f, CASA_ADOBE))
        delante.forEachIndexed { i, (x, w, c) ->
            val base = 203f - (x * 0.012f) - (i % 2) * 1.6f; val h = 10f + (i % 3) * 1.4f
            sombraCorta(r, Offset(x + w / 2f, base), w, h)
            casa(l, Rect(x, base - h, x + w, base), if (nieve) Casa(c.muro, c.techo, C.NIEVE, c.puerta, c.ventanas, c.zocalo) else c, 0.08f, Azar(Semilla.de("romano", "casa2", i)), encendida = despiertos && i % 2 == 1)
        }
    }

    /** Un álamo: alto y estrecho, con su lado de luz; pelado en invierno. */
    private fun DrawScope.alamo(l: Luces, r: Rayo, pie: Offset, alto: Float, est: Estacion, nieve: Boolean, az: Azar) {
        val lejos = 0.12f
        drawLine(l.tono(Color(0xFF5E4E3C), lejos), pie, pie + Offset(0f, -alto * 0.5f), alto * 0.06f)
        val hoja = hojaDe(est)
        if (hoja == null) {
            repeat(9) { val y = pie.y - alto * az.entre(0.3f, 0.98f); val s = if (it % 2 == 0) 1f else -1f; drawLine(l.tono(Color(0xFF5E4E3C), lejos), Offset(pie.x, y + alto * 0.1f), Offset(pie.x + s * alto * az.entre(0.05f, 0.12f), y), 0.35f) }
            drawLine(l.tono(Color(0xFF5E4E3C), lejos), pie + Offset(0f, -alto * 0.5f), pie + Offset(0f, -alto), 0.4f)
            if (nieve) drawLine(l.tono(C.NIEVE, lejos), pie + Offset(-0.3f, -alto * 0.5f), pie + Offset(-0.3f, -alto * 0.9f), 0.35f)
            return
        }
        val w = alto * 0.16f
        val copa = Path().apply { moveTo(pie.x, pie.y - alto); cubicTo(pie.x + w, pie.y - alto * 0.7f, pie.x + w * 1.15f, pie.y - alto * 0.34f, pie.x + w * 0.3f, pie.y - alto * 0.2f); lineTo(pie.x - w * 0.3f, pie.y - alto * 0.2f); cubicTo(pie.x - w * 1.15f, pie.y - alto * 0.34f, pie.x - w, pie.y - alto * 0.7f, pie.x, pie.y - alto); close() }
        val oscuro = l.tono(mult(hoja, r.color, 0.7f * r.contraste), lejos); val claro = l.tono(lerp(hoja, Color(0xFFF2E8A8), 0.3f * r.dia), lejos)
        drawPath(copa, Brush.horizontalGradient(if (r.lat >= 0f) listOf(oscuro, claro) else listOf(claro, oscuro), pie.x - w, pie.x + w))
        clipPath(copa) { repeat(14) { val p = Offset(pie.x + az.entre(-w, w), pie.y - alto * az.entre(0.24f, 0.96f)); drawOval((if (it % 2 == 0) oscuro else claro).copy(alpha = 0.5f), p, Size(az.entre(1f, 2.2f), az.entre(0.8f, 1.6f))) } }
    }

    /**
     * Los tres chopos que crecen al pie de la muralla, del lado del poblado: cruzan el cuadro de abajo arriba y lo
     * enmarcan por la izquierda. Cada uno es distinto: el de atrás, más bajo y medio tapado; el del medio, el más alto;
     * el de la derecha, más ancho, ladeado y con el tronco a la vista. Verdes en verano, amarillos en otoño y pelados
     * en invierno.
     */
    private fun DrawScope.chopos(m: Momento, l: Luces, r: Rayo, est: Estacion) {
        val nieve = nevado(m)
        chopo(l, r, Offset(57f, 238f), 146f, est, nieve, 1, ancho = 0.95f, ladeo = -2.2f, copaDesde = 0.3f, guias = 4)
        chopo(l, r, Offset(113f, 238f), 136f, est, nieve, 2, ancho = 1.3f, ladeo = 4.6f, copaDesde = 0.46f, guias = 6)
        chopo(l, r, Offset(82f, 238f), 191f, est, nieve, 3, ancho = 1.05f, ladeo = 0.8f, copaDesde = 0.36f, guias = 7)
    }

    /** Hasta dónde llega la copa de una guía de chopo, de 0 (donde empieza, abajo) a 1 (la punta): un huso, más ancho hacia abajo. */
    private fun husoDeChopo(t: Float): Float { val q = Math.pow(t.toDouble(), 0.62).toFloat(); return Math.pow((4f * q * (1f - q)).toDouble().coerceAtLeast(0.0), 0.62).toFloat() }

    /**
     * Un chopo: el tronco pálido y, encima, varias guías (las ramas que suben pegadas al tronco), cada una con su huso
     * de hojas, de distinta altura y anchura. Entre guía y guía quedan entrantes y claros por donde se ve el cielo.
     * [ancho] lo ensancha, [ladeo] lo inclina (en grados), [copaDesde] dice a qué altura empieza la copa (deja el
     * tronco a la vista) y [guias], cuántas lleva.
     */
    private fun DrawScope.chopo(l: Luces, r: Rayo, pie: Offset, alto: Float, est: Estacion, nieve: Boolean, n: Int, ancho: Float, ladeo: Float, copaDesde: Float, guias: Int) {
        val az = Azar(Semilla.de("romano", "chopo", n))
        rotate(ladeo, pie) {
            val cima = pie.y - alto; val rx = alto * 0.1f * ancho
            // El tronco: liso y pálido, con sus marcas oscuras, más fino hacia arriba y un poco torcido.
            val corteza = Color(0xFFB4AE98)
            val tuerce = az.entre(-2.4f, 2.4f)
            val tronco = Path().apply {
                moveTo(pie.x - alto * 0.028f, pie.y); cubicTo(pie.x - alto * 0.02f + tuerce, pie.y - alto * 0.3f, pie.x - 1.6f - tuerce, pie.y - alto * 0.6f, pie.x - 0.5f, cima + alto * 0.08f)
                lineTo(pie.x + 0.5f, cima + alto * 0.08f); cubicTo(pie.x + 1.6f - tuerce, pie.y - alto * 0.6f, pie.x + alto * 0.02f + tuerce, pie.y - alto * 0.3f, pie.x + alto * 0.028f, pie.y); close()
            }
            val luz = l.tono(lerp(corteza, Color(0xFFFFF2D0), 0.4f * r.contraste)); val sombra = l.tono(mult(corteza, r.color, 0.8f * r.contraste))
            drawPath(tronco, Brush.horizontalGradient(if (r.lat >= 0f) listOf(sombra, l.tono(corteza), luz) else listOf(luz, l.tono(corteza), sombra), pie.x - alto * 0.028f, pie.x + alto * 0.028f))
            clipPath(tronco) { repeat(26) { val y = az.entre(cima + alto * 0.2f, pie.y); val x = pie.x + az.entre(-3.4f, 3.4f); drawLine(l.tono(Color(0xFF4A4438)).copy(alpha = az.entre(0.4f, 0.8f)), Offset(x, y), Offset(x + az.entre(1.4f, 3.4f), y + az.entre(-0.4f, 0.4f)), az.entre(0.5f, 1f), StrokeCap.Round) } }
            val hoja = hojaDe(est)?.let { h -> val propia = if (est == Estacion.OTONO) lerp(h, Color(0xFFDDB838), 0.5f + 0.1f * n) else h; lerp(propia, if (n % 2 == 0) Color(0xFF8AA04A) else Color(0xFF3E6A40), 0.14f) }
            val rama = l.tono(Color(0xFF6A6252))
            val oscuro = hoja?.let { l.tono(mult(lerp(it, C.BOSQUE, 0.55f), r.color, 0.6f * r.contraste)) } ?: rama
            val medio = hoja?.let { l.tono(lerp(it, C.BOSQUE, 0.36f)) } ?: rama
            val claro = hoja?.let { l.tono(lerp(lerp(it, C.BOSQUE, 0.12f), Color(0xFFF2E8A0), 0.36f * r.dia)) } ?: rama
            // Una o dos ramas bajas, sueltas, por debajo de la copa.
            repeat(if (copaDesde > 0.4f) 3 else 1) {
                val t = copaDesde * az.entre(0.62f, 0.94f); val lado = if (it % 2 == 0) -1f else 1f; val desde = Offset(pie.x, pie.y - alto * t); val largo = alto * az.entre(0.07f, 0.11f)
                val fin = desde + Offset(lado * largo * 0.6f, -largo)
                drawPath(Path().apply { moveTo(desde); cubicTo(desde + Offset(lado * largo * 0.55f, -largo * 0.2f), fin + Offset(0f, largo * 0.4f), fin) }, rama, style = Stroke(0.8f, cap = StrokeCap.Round))
                if (hoja != null) repeat(9) { val p = fin + Offset(az.entre(-2.2f, 2.2f), az.entre(-3.4f, 3f)); drawLine(if (az.real() > 0.5f) medio else oscuro, p, p + Offset(az.entre(-0.6f, 0.6f), -az.entre(1.2f, 2.2f)), 0.95f, StrokeCap.Round) }
            }
            // Las guías, de atrás adelante: las de los lados, más bajas; la del medio, la más alta.
            class Guia(val base: Offset, val punta: Offset, val rx: Float)
            val lista = (0 until guias).map { i ->
                val u = if (guias == 1) 0f else (i / (guias - 1f)) * 2f - 1f                            // de −1 (izquierda) a 1 (derecha)
                val lejosDelEje = kotlin.math.abs(u)
                val t0 = copaDesde + az.entre(0f, 0.16f) + 0.1f * (1f - lejosDelEje)
                val tope = 1f - lejosDelEje * az.entre(0.16f, 0.42f) - az.entre(0f, 0.05f)
                Guia(Offset(pie.x + u * rx * 0.34f, pie.y - alto * t0), Offset(pie.x + u * rx * az.entre(0.7f, 1.1f) + az.entre(-1.6f, 1.6f), pie.y - alto * tope), rx * az.entre(0.44f, 0.62f) * (1f - 0.2f * lejosDelEje))
            }.sortedBy { -kotlin.math.abs(it.punta.x - pie.x) }
            lista.forEachIndexed { gi, g ->
                val eje = { t: Float -> g.base + (g.punta - g.base) * t }
                // La rama que la lleva.
                drawPath(Path().apply { moveTo(Offset(pie.x, g.base.y + alto * 0.05f)); cubicTo(Offset(g.base.x, g.base.y + alto * 0.02f), eje(0.3f), g.punta) }, rama, style = Stroke(if (hoja == null) 1.1f else 0.8f, cap = StrokeCap.Round))
                if (hoja == null) {
                    // Pelado: las ramillas, que suben abiertas en abanico estrecho.
                    repeat(9) { val t = az.entre(0.08f, 0.95f); val p = eje(t); val lado = if (it % 2 == 0) 1f else -1f; val largo = alto * az.entre(0.05f, 0.1f) * (1.1f - t * 0.6f)
                        val fin = p + Offset(lado * largo * az.entre(0.2f, 0.5f), -largo)
                        drawLine(rama.copy(alpha = 0.9f), p, fin, 0.5f, StrokeCap.Round)
                        drawLine(rama.copy(alpha = 0.7f), p + (fin - p) * 0.5f, p + (fin - p) * 0.5f + Offset(-lado * largo * 0.2f, -largo * 0.4f), 0.35f, StrokeCap.Round)
                        if (nieve) drawLine(l.tono(C.NIEVE).copy(alpha = 0.9f), p + (fin - p) * 0.3f + Offset(-lado * 0.5f, -0.4f), fin + Offset(-lado * 0.5f, -0.4f), 0.5f, StrokeCap.Round) }
                    return@forEachIndexed
                }
                val izq = (0..10).map { val t = it / 10f; eje(t) + Offset(-g.rx * husoDeChopo(t) * az.entre(0.7f, 1.2f), 0f) }
                val der = (0..10).map { val t = it / 10f; eje(t) + Offset(g.rx * husoDeChopo(t) * az.entre(0.7f, 1.2f), 0f) }
                val masa = curva(izq + der.reversed() + izq.first())
                drawPath(masa, oscuro)
                clipPath(masa) {
                    repeat(4) { val t = az.real(); mancha(eje(t) + Offset(az.entre(-0.7f, 0.7f) * g.rx * husoDeChopo(t), 0f), az.entre(1.6f, 3.4f), az.entre(5f, 11f), l.tono(lerp(C.BOSQUE, Color(0xFF1E2A26), 0.4f)), 0.4f) }
                    mancha(eje(0.5f) + Offset(r.lat * g.rx * 0.5f, 0f), g.rx * 0.5f, alto * 0.16f, medio, 0.5f)
                }
                // Las hojas: chicas, siguiendo la guía hacia arriba; las claras, solo en el fleco del lado del sol.
                val largoGuia = (g.base.y - g.punta.y)
                repeat((largoGuia * g.rx * 0.11f).toInt().coerceIn(20, 170)) {
                    val t = az.real(); val w = g.rx * husoDeChopo(t) * 1.08f; val u = az.entre(-1f, 1f)
                    val p = eje(t) + Offset(u * w, 0f)
                    val alSol = u * (if (r.lat >= 0f) 1f else -1f); val suerte = az.real()
                    val c = when { alSol > 0.4f && suerte > 0.5f -> claro; suerte > 0.52f - 0.25f * alSol -> medio; else -> oscuro }
                    val a = -1.57f + u * 0.5f + az.entre(-0.3f, 0.3f); val n2 = az.entre(1.3f, 2.4f)
                    drawLine(c, p, p + Offset(cos(a) * n2, sin(a) * n2), 0.95f, StrokeCap.Round)
                }
                // Alguna ramilla se sale de la silueta.
                if (gi % 2 == 0) { val t = az.entre(0.3f, 0.8f); val lado = if (g.punta.x >= pie.x) 1f else -1f; val p = eje(t) + Offset(lado * g.rx * husoDeChopo(t), 0f); val fin = p + Offset(lado * az.entre(2.4f, 4.6f), -az.entre(5f, 9f))
                    drawLine(rama, p, fin, 0.4f, StrokeCap.Round); repeat(5) { val q = p + (fin - p) * az.entre(0.3f, 1f); drawLine(if (lado * r.lat > 0f) claro else medio, q, q + Offset(lado * 0.6f, -az.entre(1.2f, 2f)), 0.9f, StrokeCap.Round) } }
                if (nieve) repeat(8) { val t = az.real(); val p = eje(t) + Offset(az.entre(-0.8f, 0.8f) * g.rx * husoDeChopo(t), 0f); drawOval(l.tono(C.NIEVE).copy(alpha = 0.85f), p, Size(az.entre(2f, 4f), 1.2f)) }
            }
        }
    }

    // ---------------------------------------------------------------- La parte viva

    override fun seMueve(m: Momento): Boolean = true                          // el río siempre brilla, y si está helado humean los fuegos

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        val l = m.luces()
        // El viento baja por el río: tumba los humos hacia la derecha del cuadro. Con el día en calma queda una brisa.
        val viento = if (m.ficha.clima == Clima.DESPEJADO) 0.22f else m.viento
        if (!helado(m)) destellos(l, Rect(0f, 130f, ANCHO, 190f), reloj, m.semilla("destellos"))

        // La aldea de enfrente, ardiendo.
        if (m.con("humo")) {
            humo(l, Offset(164f, 121f), 78f, 32f, reloj, viento * 0.7f, 0.85f, 0.3f, 1, 12, 1.25f)
            humo(l, Offset(184f, 120.6f), 64f, 26f, reloj, viento * 0.7f, 0.8f, 0.34f, 2, 11, 1.1f)
            humo(l, Offset(202f, 121.4f), 50f, 20f, reloj, viento * 0.7f, 0.7f, 0.4f, 3, 10)
            for ((i, x) in listOf(160f, 174f, 186f, 199f).withIndex()) llama(Offset(x, 123.6f), 4.4f, reloj, 20 + i)
            resplandor(Offset(180f, 122f), 34f, 0.25f + 0.45f * l.noche, Color(0xFFFF6A2A))
            if (!helado(m)) reflejo(Offset(180f, 131f), 14f, LLAMA, 0.4f + 0.3f * l.noche, reloj, 9)
        }

        // Los fuegos del prado: llama y humo a las horas de comer y de estar juntos; un hilo de humo y brasas el resto.
        val encendidos = m.hacer == Hacer.COMER || m.hacer == Hacer.GENTE
        FUEGOS.take(fuegos(m)).forEachIndexed { i, p ->
            if (encendidos) {
                llama(p + Offset(0f, -0.2f), 3.2f, reloj, 30 + i)
                resplandor(p + Offset(0f, -1.4f), 11f, 0.2f + 0.5f * l.noche)
                humo(l, p + Offset(0f, -3f), 26f + 4f * (i % 2), 7f, reloj, viento, 0.15f, 0.12f, 30 + i, 8, 0.8f)
            } else {
                if (m.hacer != Hacer.DORMIR || i == 0) humo(l, p + Offset(0f, -1f), 16f, 4f, reloj, viento, 0.1f, 0.12f, 30 + i, 6, 0.5f)
                if (l.noche > 0.3f) { drawCircle(Color(0xFFFF7A3A).copy(alpha = 0.8f), 0.7f, p); resplandor(p, 6f, 0.4f * l.noche, Color(0xFFFF5A1E)) }
            }
        }
        // Las chimeneas del poblado, en invierno o a la hora de comer.
        if (m.hacer == Hacer.COMER || m.ficha.estacion == Estacion.INVIERNO) {
            humo(l, Offset(58f, 190f), 24f, 6f, reloj, viento, 0.2f, 0.1f, 41, 7, 0.6f); humo(l, Offset(12f, 192f), 20f, 5f, reloj, viento, 0.2f, 0.1f, 42, 7, 0.55f)
        }
        // Las antorchas de las fiestas de noche: en el embarcadero, con su reflejo, y en el parapeto.
        if (antorchas(m)) {
            if (embarcadero(m) == Embarcadero.ENTERO) { antorcha(l, Offset(165f, 169.2f), 5f, reloj, 0.1f, 1); if (!helado(m)) reflejo(Offset(166.6f, 171.4f), 13f, LLAMA, 0.55f, reloj, 1) }
            antorcha(l, Offset(206f, 186f), 4.6f, reloj, 0.12f, 2); antorcha(l, Offset(300f, 180f), 4.6f, reloj, 0.12f, 3)
        }
        with(CosasRomano) { caminoVivo(m, l, reloj, viento) }
        tiempoVivo(l, m.ficha.clima, reloj, H, m.ficha.semilla)
    }

    // ---------------------------------------------------------------- El gato

    override fun estorbos(m: Momento): List<Float> = listOf(CosasRomano.BRASERO.x, CosasRomano.LENA_X)

    override fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 50f, 279.5f, hasta = 300f))
        add(Ancla(TipoAncla.MIRADOR, CosasRomano.miradorEn(m), VANO))            // en un vano del parapeto, mirando al río
        add(Ancla(TipoAncla.OBJETO, CosasRomano.objetoEn(m).x, 282f))
        add(Ancla(TipoAncla.ABRIGO, 262f, 280f))                              // debajo del banco
        if (comiendo(m) || conGente(m)) add(Ancla(TipoAncla.FUEGO, 128f, 283f, hasta = CosasRomano.BRASERO.x))
        if (fiesta(m)) add(Ancla(TipoAncla.ADORNO, 206f, 252f))
    }
}
