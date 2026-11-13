package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LLAMA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LUZ_DE_CASA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.agua
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.bajio
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.banderines
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.brasas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.estela
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.guirnalda
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
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.pano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sembrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.volumen
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Gótico: la obra de la catedral de Amiens vista desde el patio de la logia de los canteros, donde trabaja Pierre
 * (cantero, de 1240 a 1244). **[Aproximación]**: es una composición con lo que había allí, no la vista desde un punto
 * real.
 *
 * - **A la izquierda, cruzando el cuadro de abajo arriba:** la catedral. La nave ya está cubierta; la cierra un
 *   tabique de tablas en el arco donde empezará el coro. Delante, la cabecera a medio subir, con sus andamios y la
 *   grúa de rueda.
 * - **A la derecha, cuesta abajo:** los tejados del barrio de Saint-Leu, el Somme con su puente y su molino, la
 *   atalaya de la comuna, la muralla y, fuera, los campos de trigo y de waide (la hierba del tinte azul) y un molino
 *   de viento.
 * - **Delante:** el patio de la logia. Una pila de sillares ya labrados, la cabria con su polea, el tonel del agua,
 *   la artesa del mortero, el hogar con su caldero, la piedra que talla sobre su asiento, el banco con el objeto del
 *   día y el medio muro de zarzo de la logia. A la derecha, el poste y el alero de paja de la logia enmarcan el cuadro.
 *
 * Cómo está pintado: como manda `docs/diseno/ESCENAS.md`, con el cuadro de Helénico de vara. La luz viene del lado
 * del sol ([Rayo]) y todo arroja su sombra; por la mañana el alero de la logia deja medio patio en sombra.
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioPiloto.kt`):
 * - **La cabecera** (arco `coro`): sube cada año; primero los zócalos, luego los huecos de las ventanas.
 * - **La obra** (arco `andamios`): en marcha, con andamios en todos los muros y la grúa cargada a sus horas de
 *   trabajo; a medias; o **tapada**, con los muros cubiertos de paja y la grúa quieta, en invierno y desde que se
 *   acaba el dinero, el 3 de septiembre de la temporada 1 ([obra]).
 * - **La caída** (estado `herido`): el andamio cedido contra el muro y, en el patio, su piedra tapada con un saco y
 *   el mazo colgado ([andamioCaido]).
 * - **La crecida** (arco `agua`, clave `inundacion`): el Somme fuera de su cauce, con las casas bajas dentro.
 * - **El campo** (arcos `campo` y `guede`): el trigo brota, verdea, dora y se siega (con sus gavillas); la waide
 *   crece y la cortan dos veces.
 * - **Las fiestas** (claves `fiesta`, `procesion`, `campanas`, `hogueras`, `luto`, `eclipse`): guirnaldas y
 *   banderines; colgaduras en las casas, pendones en el camino y un estandarte en el patio; las campanas de la
 *   atalaya volteadas y los pájaros en el aire; hogueras en la ribera; un paño negro y velas; el sol en media luna.
 * - **El objeto del día:** sobre el banco; el compás y la plantilla, apoyados en el medio muro; la cuerda de nudos y
 *   la capucha, colgadas de él; la plomada, colgada de la cabria; la marca, recién abierta en la piedra.
 * - **El tramo del día:** lascas frescas y las herramientas sobre la piedra mientras trabaja; el caldero al fuego a
 *   la hora de comer; el farol y la jarra al caer la tarde; brasas de noche.
 */
internal object EscenaGotico : EscenaVida {
    override val vidas = listOf("cantero")

    /** El horizonte. */
    private const val H = 124f

    // El patio: dónde toca el suelo lo que hace de muro (la pila de sillares, a la izquierda; el medio muro de la
    // logia, a la derecha) y hasta dónde sube cada uno.
    private const val BASE = 246f
    private const val PILA = 205f
    private const val PILA_X = 116f
    private const val ZARZO = 206f
    private const val ZARZO_X = 232f

    // Dónde están las cosas que también usa la parte viva.
    private val HOGAR = Offset(134f, 268f)
    private const val K_HOGAR = 1.4f
    private val SOBRE = Offset(268f, 249f)
    private val FAROL = Offset(327f, 150f)
    private val FRAGUA = Offset(226f, 190f)

    private val CALIZA = Color(0xFFE6DCC2)
    private val CALIZA_OSC = Color(0xFFB8AA8C)
    private val PLOMO = Color(0xFF61798A)
    private val TEJA = Color(0xFFB4633C)
    private val PAJA = Color(0xFFCDB26C)
    private val PAJA_VIEJA = Color(0xFF9C8A5C)
    private val ENLUCIDO = Color(0xFFE4D8BA)
    private val ROBLE = Color(0xFF94683C)
    private val ROBLE_OSC = Color(0xFF5C4129)
    private val HIERRO = Color(0xFF4B4C54)
    private val TIERRA = Color(0xFFB09C78)
    private val GUEDE = Color(0xFF4F8A70)
    private val AZUL = Color(0xFF3D5F8F)
    private val ROJO = Color(0xFFA83E2E)
    private val ORO = Color(0xFFD8B04A)
    private val VIDRIADO = Color(0xFF5F8A4E)
    private val PAN = Color(0xFFC89858)
    private val CERA = Color(0xFFF0E3B4)
    private val SOMME = Color(0xFF4B7C80)
    private val SOMME_HONDO = Color(0xFF345E6A)
    private val SOMME_ORILLA = Color(0xFF93B9A2)
    private val NIEVE = Color(0xFFF0F3F6)
    private val CALIDA = Color(0xFFFFF2D0)

    // ---------------------------------------------------------------- Lo que cuenta

    /** El eclipse del 6 de octubre de la temporada 2: de 0 a 1, cuánto tapa la luna al sol a esta hora (a mediodía). */
    private fun eclipse(m: Momento): Float = if (!m.con("eclipse")) 0f else (1.5f - abs(m.hora.toFloat() - 12.4f)).coerceIn(0f, 1f)

    private fun luces(m: Momento): Luces = m.luces(eclipse(m))

    /** Cómo está la obra de la cabecera. */
    internal enum class Obra { EN_MARCHA, A_MEDIAS, TAPADA }

    /**
     * La obra, según el arco `andamios`: tapada con paja en invierno y desde que se acaba el dinero (menos de 0.3);
     * a medias cuando vuelve despacio; en marcha con 0.7 o más.
     */
    internal fun obra(m: Momento): Obra = m.arco("andamios").let { if (it < 0.3f) Obra.TAPADA else if (it < 0.7f) Obra.A_MEDIAS else Obra.EN_MARCHA }

    /** Cuántos cuerpos de andamio hay en la cabecera, de 0 a 5. */
    internal fun andamios(m: Momento): Int = when (obra(m)) { Obra.TAPADA -> 0; else -> (m.arco("andamios") * 5f).roundToInt().coerceIn(1, 5) }

    /** Si se ve el andamio que cedió: mientras él está herido. */
    internal fun andamioCaido(m: Momento): Boolean = m.estado == "herido"

    /** La altura de la cabecera, de 0 a 1 (1 sería la de la nave): el arco `coro`. */
    internal fun alturaDelCoro(m: Momento): Float = m.arco("coro").coerceIn(0f, 1f)

    /** Si la grúa sube una piedra: con la obra en marcha, a sus horas de trabajo y sin tormenta. */
    internal fun gruaCargada(m: Momento): Boolean = obra(m) == Obra.EN_MARCHA && m.hacer == Hacer.TRABAJO && m.ficha.clima != Clima.TORMENTA

    /** Cuánto se ha salido el Somme, de 0 a 1. */
    internal fun crecida(m: Momento): Float = maxOf(m.arco("agua"), if (m.con("inundacion")) 0.6f else 0f).coerceIn(0f, 1f)

    private fun tallando(m: Momento) = m.hacer == Hacer.TRABAJO && !andamioCaido(m)
    private fun comiendo(m: Momento) = m.hacer == Hacer.COMER
    private fun conGente(m: Momento) = m.hacer == Hacer.GENTE
    private fun fiesta(m: Momento) = m.con("fiesta")
    private fun procesion(m: Momento) = m.con("procesion")
    private fun siega(m: Momento) = m.ficha.especiales.any { it.dia.id == "siega" }
    private fun cogida(m: Momento) = m.ficha.especiales.any { it.dia.id == "guede" }
    private fun nevado(m: Momento) = m.ficha.clima == Clima.NIEVE
    private fun farol(l: Luces) = l.noche > 0.35f

    // ---------------------------------------------------------------- Ayudas de pintura

    /** El color de una cara plana: [hacia] va de −1 (da la espalda a la luz) a 1 (la mira de lleno). */
    private fun cara(l: Luces, r: Rayo, propio: Color, hacia: Float, lejos: Float = 0f): Color {
        val c = if (hacia >= 0f) lerp(propio, CALIDA, 0.34f * hacia * r.contraste) else mult(propio, r.color, 0.9f * -hacia * r.contraste)
        return l.tono(c, lejos)
    }

    /** Hacia dónde mira una cara que da a la derecha (+1) o a la izquierda (−1) respecto de la luz. */
    private fun lado(r: Rayo, mira: Float) = (r.lat * mira).coerceIn(-1f, 1f) * (0.5f + 0.5f * r.dia) - (1f - r.dia) * 0.25f

    private fun caja(x0: Float, y0: Float, x1: Float, y1: Float) = Path().apply { addRect(Rect(x0, y0, x1, y1)) }

    // ---------------------------------------------------------------- La parte quieta

    override fun DrawScope.quieta(m: Momento) {
        val l = luces(m); val r = Rayo(l)
        val est = m.ficha.estacion
        val semilla = m.ficha.semilla
        val fijo = Azar(Semilla.de("gotico", "tierra"))

        // 1. Cielo y lejanía.
        cielo(l, H, semilla)
        astros(l, H, semilla)
        nubes(l, H, m.ficha.clima, semilla)
        monte(l, perfil(fijo, 150f, 400f, H + 2f, 15f, 0.25f, 26), H + 3f, Color(0xFF7C8AA0), 0.86f)
        monte(l, perfil(fijo, 170f, 400f, H + 2f, 8f, 0.2f, 22), H + 3f, lerp(hierbaDe(est), Color(0xFF6C7C7A), 0.5f), 0.72f)
        campos(m, l, r, est, fijo)

        // 2. Plano medio: la villa cuesta abajo, con el río, y la catedral en su altozano.
        villa(m, l, r, est, fijo)
        catedral(m, l, r, est)

        // 3. Primer plano: el patio de la logia, y el alero que lo enmarca.
        patio(m, l, r, est)
        alero(m, l, r)

        // 4. El velo del tiempo que hace y el grano del papel.
        tiempoQuieto(l, m.ficha.clima, H, semilla)
        grano(Azar(Semilla.de("gotico", "grano")), 0.6f + 0.4f * r.dia)
    }

    // ---------------------------------------------------------------- Los campos de fuera

    /** Los campos de más allá de la muralla: trigo, waide y barbecho en tiras largas, un seto, y el molino de viento. */
    private fun DrawScope.campos(m: Momento, l: Luces, r: Rayo, est: Estacion, fijo: Azar) {
        val hierba = if (nevado(m)) NIEVE else hierbaDe(est)
        drawRect(Brush.verticalGradient(listOf(l.tono(hierba, 0.72f), l.tono(hierba, 0.5f)), H, 150f), Offset(190f, H), Size(190f, 28f))
        // Lomas suaves: el terreno no es plano.
        for ((cx, cy, rx) in listOf(Triple(250f, 131f, 46f), Triple(338f, 136f, 52f), Triple(292f, 141f, 40f))) {
            mancha(Offset(cx, cy), rx, 3.6f, l.tono(lerp(hierba, CALIDA, 0.25f), 0.6f), 0.5f * r.dia + 0.1f)
            mancha(Offset(cx + 10f * -r.lat, cy + 3.4f), rx * 0.9f, 2.6f, l.tono(mult(hierba, r.color, 0.5f), 0.6f), 0.4f * r.dia)
        }
        val trigo = if (nevado(m)) 0f else m.arco("campo"); val guede = m.arco("guede")
        // Las tiras: (x de arriba, x de abajo, ancho arriba, ancho abajo, qué lleva). 0 = trigo, 1 = waide, 2 = barbecho.
        val tiras = listOf(listOf(204f, 196f, 12f, 17f, 0f), listOf(219f, 216f, 9f, 13f, 1f), listOf(231f, 232f, 10f, 15f, 2f), listOf(244f, 250f, 12f, 18f, 0f), listOf(259f, 271f, 9f, 14f, 1f),
            listOf(271f, 288f, 11f, 17f, 0f), listOf(285f, 308f, 9f, 14f, 2f), listOf(297f, 325f, 12f, 19f, 1f), listOf(312f, 347f, 12f, 20f, 0f), listOf(327f, 370f, 14f, 22f, 1f))
        tiras.forEachIndexed { i, t ->
            val tira = poligono(Offset(t[0], 127.5f), Offset(t[0] + t[2], 127.5f), Offset(t[1] + t[3], 146f), Offset(t[1], 146f))
            val lim = Rect(minOf(t[0], t[1]), 127.5f, maxOf(t[0] + t[2], t[1] + t[3]), 146f)
            when (t[4].toInt()) {
                0 -> sembrado(l, tira, lim, trigo, 0.55f, Azar(Semilla.de("gotico", "trigo", i)), gavillas = siega(m))
                1 -> waide(m, l, tira, lim, guede, i)
                else -> { drawPath(tira, l.tono(lerp(hierba, TIERRA, 0.45f), 0.55f)); clipPath(tira) { val az = Azar(Semilla.de("gotico", "barbecho", i)); repeat(9) { mancha(Offset(az.entre(lim.left, lim.right), az.entre(lim.top, lim.bottom)), 2.6f, 1f, l.tono(hierba, 0.5f), 0.5f) } } }
            }
            if (nevado(m)) drawPath(tira, l.tono(NIEVE, 0.5f).copy(alpha = 0.7f))
        }
        // Un seto y unos árboles en la linde, y el camino que sale de la puerta.
        for (i in 0..13) { val x = 198f + i * 13.6f + fijo.entre(-3f, 3f); arbolito(l, r, Offset(x, 127.6f + fijo.entre(-0.6f, 0.6f)), fijo.entre(3.2f, 5.6f), est, 0.62f, fijo, nevado(m)) }
        drawPath(Path().apply { moveTo(300f, 147f); cubicTo(296f, 140f, 284f, 133f, 286f, 127f); lineTo(288.4f, 127f); cubicTo(287f, 133f, 300f, 140f, 304.5f, 147f); close() }, l.tono(lerp(TIERRA, CALIZA, 0.4f), 0.5f))
        // El molino de viento, en la loma: de poste, con sus cuatro aspas.
        val pm = Offset(252f, 126.4f)
        drawOval(l.tono(mult(hierba, r.color, 0.6f), 0.6f).copy(alpha = 0.7f * r.dia), Offset(pm.x - 4f - 5f * r.lat, pm.y - 0.6f), Size(8f, 1.6f))
        drawLine(l.tono(ROBLE_OSC, 0.6f), pm + Offset(-2.4f, 0f), pm + Offset(0f, -3.4f), 0.7f); drawLine(l.tono(ROBLE_OSC, 0.6f), pm + Offset(2.4f, 0f), pm + Offset(0f, -3.4f), 0.7f)
        drawRect(cara(l, r, ROBLE, lado(r, 1f), 0.6f), pm + Offset(-2.6f, -9.4f), Size(5.2f, 6.4f)); drawRect(cara(l, r, ROBLE, lado(r, -1f), 0.6f), pm + Offset(-2.6f, -9.4f), Size(2.2f, 6.4f))
        drawPath(poligono(pm + Offset(-3.2f, -9.2f), pm + Offset(0f, -12.2f), pm + Offset(3.2f, -9.2f)), l.tono(PAJA_VIEJA, 0.6f))
        val eje = pm + Offset(0.6f, -8.2f)
        for (k in 0..3) { val a = (0.5f + k * PI.toFloat() / 2f); val p = eje + Offset(cos(a), sin(a)) * 7.4f; drawLine(l.tono(ROBLE_OSC, 0.6f), eje, p, 0.5f); val q = Offset(-sin(a), cos(a)) * 1.5f
            drawPath(poligono(eje + (p - eje) * 0.3f, p, p + q, eje + (p - eje) * 0.3f + q), l.tono(ENLUCIDO, 0.6f).copy(alpha = 0.85f)) }
    }

    /** Una tira de waide: matas en hileras, de un verde azulado, que crecen con el arco; recién cortada queda la tierra. */
    private fun DrawScope.waide(m: Momento, l: Luces, tira: Path, lim: Rect, cuanto: Float, n: Int) {
        val tierra = lerp(TIERRA, Color(0xFF7A6248), 0.4f)
        drawPath(tira, l.tono(lerp(tierra, GUEDE, (cuanto * 1.15f).coerceIn(0f, 0.92f)), 0.55f))
        clipPath(tira) {
            val az = Azar(Semilla.de("gotico", "waide", n))
            var y = lim.top + 1f
            while (y < lim.bottom) { drawLine(l.tono(mult(GUEDE, Color(0xFF7A80B0), 0.7f), 0.55f).copy(alpha = 0.16f + 0.3f * cuanto), Offset(lim.left, y), Offset(lim.right, y + az.entre(-0.4f, 0.4f)), 0.5f); y += 1.7f + (y - lim.top) * 0.05f }
            if (cuanto > 0.3f) repeat((24 * cuanto).toInt()) { drawCircle(l.tono(lerp(GUEDE, Color(0xFFD8D070), 0.45f), 0.5f).copy(alpha = 0.7f), 0.55f, Offset(az.entre(lim.left, lim.right), az.entre(lim.top, lim.bottom))) }
            // En los días de la cogida, los montones de hoja en la linde.
            if (cogida(m)) repeat(3) { val p = Offset(az.entre(lim.left + 2f, lim.right - 2f), az.entre(lim.top + 4f, lim.bottom - 2f)); drawOval(l.tono(lerp(GUEDE, Color(0xFF2E4E46), 0.4f), 0.5f), p + Offset(-1.6f, -1.2f), Size(3.2f, 1.8f)) }
        }
    }

    /** Un árbol pequeño y lejano, con su lado de luz, su lado de sombra y su sombra corta: para setos, riberas y huertos. */
    private fun DrawScope.arbolito(l: Luces, r: Rayo, pie: Offset, alto: Float, est: Estacion, lejos: Float, az: Azar, nevado: Boolean = false, alamo: Boolean = false) {
        val hoja = hojaDe(est)
        val ancho = if (alamo) alto * 0.3f else alto * 0.62f
        if (r.fuerza > 0.05f) drawOval(r.color.copy(alpha = 0.5f * r.fuerza), Offset(pie.x - ancho * 0.5f + (r.ax * alto * 0.35f).coerceIn(-8f, 8f).let { if (it < 0f) it else 0f }, pie.y - 0.5f), Size(ancho + abs((r.ax * alto * 0.35f).coerceIn(-8f, 8f)), maxOf(0.9f, alto * 0.14f)), blendMode = androidx.compose.ui.graphics.BlendMode.Multiply)
        drawLine(l.tono(ROBLE_OSC, lejos), pie, pie + Offset(0f, -alto * 0.5f), maxOf(0.5f, alto * 0.07f))
        if (hoja == null) {
            for (k in -2..2) drawLine(l.tono(ROBLE_OSC, lejos).copy(alpha = 0.8f), pie + Offset(0f, -alto * 0.42f), pie + Offset(k * ancho * 0.24f + az.entre(-0.4f, 0.4f), -alto * (0.98f - 0.07f * abs(k))), maxOf(0.35f, alto * 0.03f))
            if (nevado) drawOval(l.tono(NIEVE, lejos).copy(alpha = 0.7f), pie + Offset(-ancho * 0.4f, -alto), Size(ancho * 0.8f, alto * 0.2f))
            return
        }
        val copa = if (nevado) lerp(hoja, NIEVE, 0.6f) else hoja
        val c = pie + Offset(0f, -alto * 0.64f); val ry = if (alamo) alto * 0.44f else alto * 0.36f
        drawOval(l.tono(mult(copa, r.color, 0.75f * r.contraste), lejos), Offset(c.x - ancho / 2f, c.y - ry), Size(ancho, ry * 2f))
        drawOval(l.tono(copa, lejos), Offset(c.x - ancho * 0.44f + ancho * 0.1f * r.lat, c.y - ry), Size(ancho * 0.8f, ry * 1.7f))
        drawOval(l.tono(lerp(copa, CALIDA, 0.3f * r.contraste), lejos), Offset(c.x - ancho * 0.28f + ancho * 0.2f * r.lat, c.y - ry * 0.92f), Size(ancho * 0.5f, ry))
    }

    // ---------------------------------------------------------------- La villa, cuesta abajo

    /** Lo que mira al frente (hacia quien mira el cuadro, y un poco a la izquierda): cuánta luz le da. */
    private fun frente(r: Rayo) = (0.25f - 0.5f * r.lat) * (0.5f + 0.5f * r.dia) - (1f - r.dia) * 0.25f

    /** La orilla de acá del Somme, de izquierda a derecha. Con la crecida baja hacia las casas. */
    private fun orillaDeAca(c: Float) = listOf(Offset(186f, 178f), Offset(206f, 177f), Offset(224f, 178.6f), Offset(243f, 176.2f), Offset(262f, 177.4f), Offset(281f, 175.2f), Offset(300f, 176f),
        Offset(318f, 173.6f), Offset(338f, 174.2f), Offset(358f, 171.4f), Offset(384f, 171f)).mapIndexed { i, p -> p + Offset(0f, c * (13f + 3f * sin(i * 1.7f))) }

    private fun orillaDeAlla(c: Float) = listOf(Offset(186f, 161f), Offset(210f, 160.2f), Offset(230f, 161.4f), Offset(252f, 160f), Offset(276f, 160.8f), Offset(298f, 159f), Offset(322f, 159.4f),
        Offset(346f, 157.4f), Offset(366f, 157.6f), Offset(384f, 156f)).map { it + Offset(0f, -1.6f * c) }

    /**
     * La villa: la muralla y sus torres, las casas de la otra orilla, el Somme con su puente y su molino, y de este
     * lado dos hileras de casas de entramado que suben hacia la obra, con la atalaya de la comuna entre ellas.
     */
    private fun DrawScope.villa(m: Momento, l: Luces, r: Rayo, est: Estacion, fijo: Azar) {
        val az = Azar(Semilla.de("gotico", "villa"))
        val c = crecida(m)
        val hierba = if (nevado(m)) NIEVE else hierbaDe(est)
        val noche = l.noche > 0.5f
        // El suelo de la villa: de la muralla al pie de la obra.
        drawRect(Brush.verticalGradient(listOf(l.tono(lerp(hierba, TIERRA, 0.35f), 0.5f), l.tono(lerp(hierba, TIERRA, 0.6f), 0.15f)), 146f, 214f), Offset(186f, 146f), Size(194f, 70f))
        // La muralla, con sus torres y la puerta.
        val piedra = lerp(CALIZA, Color(0xFFA89C88), 0.45f)
        drawPath(poligono(Offset(190f, 151f), Offset(380f, 147f), Offset(380f, 141.4f), Offset(190f, 145.4f)), Brush.verticalGradient(listOf(cara(l, r, piedra, frente(r) + 0.2f, 0.55f), cara(l, r, piedra, frente(r) - 0.2f, 0.55f)), 141f, 151f))
        var xa = 191f; while (xa < 380f) { drawRect(cara(l, r, piedra, frente(r) + 0.2f, 0.55f), Offset(xa, 144.4f - (xa - 190f) * 0.021f), Size(1.5f, 1.1f)); xa += 3f }
        for ((x, w, h) in listOf(Triple(212f, 5f, 9f), Triple(258f, 4.4f, 8f), Triple(296f, 9f, 11f), Triple(334f, 4.6f, 8.4f), Triple(368f, 5f, 9f))) {
            val base = 150.6f - (x - 190f) * 0.021f
            drawRect(cara(l, r, piedra, frente(r), 0.52f), Offset(x, base - h), Size(w, h)); drawRect(cara(l, r, piedra, lado(r, 1f), 0.52f), Offset(x + w * 0.62f, base - h), Size(w * 0.38f, h))
            drawPath(poligono(Offset(x - 0.7f, base - h), Offset(x + w / 2f, base - h - w * 0.9f), Offset(x + w + 0.7f, base - h)), l.tono(if (nevado(m)) NIEVE else PLOMO, 0.5f))
            if (w > 8f) drawPath(Path().apply { moveTo(x + 2.6f, base); lineTo(x + 2.6f, base - 3.4f); cubicTo(x + 2.6f, base - 6f, x + w - 2.6f, base - 6f, x + w - 2.6f, base - 3.4f); lineTo(x + w - 2.6f, base); close() }, l.tono(Color(0xFF2E2A2C), 0.45f))
        }
        // Las casas de la otra orilla y la iglesia de la parroquia: la hilera más lejana.
        for (i in 0..19) { val x = 192f + i * 9.6f + az.entre(-1.6f, 1.6f); if (x in 291f..305f) continue
            casaDeVilla(m, l, r, x, 158.4f - (x - 190f) * 0.014f + az.entre(-0.6f, 0.4f), az.entre(5.4f, 7.6f), az.entre(3f, 4.2f), 0.46f, az, encendida = noche && i % 3 == 0) }
        val ig = Offset(232f, 157.6f)
        drawRect(cara(l, r, CALIZA, frente(r), 0.46f), ig + Offset(-7f, -7f), Size(12f, 7f)); drawPath(poligono(ig + Offset(-7.6f, -7f), ig + Offset(-1f, -11f), ig + Offset(5.6f, -7f)), l.tono(if (nevado(m)) NIEVE else PLOMO, 0.44f))
        drawRect(cara(l, r, CALIZA, lado(r, 1f), 0.46f), ig + Offset(5f, -13f), Size(4f, 13f)); drawRect(cara(l, r, CALIZA, frente(r), 0.46f), ig + Offset(5f, -13f), Size(2.4f, 13f))
        drawPath(poligono(ig + Offset(4.4f, -13f), ig + Offset(7f, -24f), ig + Offset(9.6f, -13f)), l.tono(PLOMO, 0.44f))
        for (x in listOf(206f, 247f, 271f, 318f, 352f)) arbolito(l, r, Offset(x, 160.6f - (x - 190f) * 0.02f), 9f + (x % 5f), est, 0.42f, az, nevado(m), alamo = true)

        // El Somme.
        val aca = orillaDeAca(c); val alla = orillaDeAlla(c)
        val cauce = curva(alla + aca.reversed()).apply { close() }
        agua(l, Rect(186f, 150f, 384f, 200f), m.azar("somme"), lerp(SOMME, Color(0xFF8C7A58), 0.55f * c), m.viento, cauce, lerp(SOMME_HONDO, Color(0xFF6E6048), 0.55f * c), lerp(SOMME_ORILLA, Color(0xFFA89870), 0.6f * c))
        clipPath(cauce) {
            bajio(l, curva(aca), 7f, SOMME_ORILLA)
            val rz = Azar(Semilla.de("gotico", "reflejos"))
            for (i in 0 until alla.size - 1) reflejoRoto(alla[i].x, alla[i + 1].x, maxOf(alla[i].y, alla[i + 1].y) + 0.4f, 4.6f, l.tono(lerp(ENLUCIDO, ROBLE_OSC, 0.45f), 0.42f), 0.5f, rz, 0.8f)
            reflejoRoto(236f, 241.6f, 161.6f, 11f, l.tono(CALIZA, 0.4f), 0.5f, rz, 0.8f)
            for (x in listOf(206f, 247f, 271f, 318f, 352f)) reflejoRoto(x - 1.6f, x + 1.6f, 161.4f - (x - 190f) * 0.02f, 9f, l.tono(hojaDe(est) ?: ROBLE_OSC, 0.4f), 0.42f, rz, 0.8f)
            estela(l, Rect(186f, 156f, 384f, 196f), m.azar("estela"))
        }
        // Entre la hierba y el agua, la franja de barro que el río moja y deja, con sus juncos.
        drawPath(curva(aca.map { it + Offset(0f, 0.9f) }), l.tono(lerp(TIERRA, Color(0xFF6E6250), 0.5f), 0.3f), style = Stroke(1.7f, cap = StrokeCap.Round))
        drawPath(curva(aca.map { it + Offset(0f, -0.2f) }), l.tono(Color(0xFFE8F0E8), 0.3f).copy(alpha = 0.4f * (1f - 0.7f * l.noche)), style = Stroke(0.4f))
        val jz = Azar(Semilla.de("gotico", "juncos")); for (i in 0 until aca.size - 1) repeat(5) { val p = aca[i] + (aca[i + 1] - aca[i]) * jz.real() + Offset(0f, 0.6f); drawLine(l.tono(lerp(hierba, Color(0xFF6E7A3E), 0.5f), 0.3f), p, p + Offset(jz.entre(-0.6f, 0.6f), -jz.entre(1.6f, 3.6f)), 0.45f) }
        // El puente de piedra: el tablero, que se aleja, con sus tajamares y su sombra en el agua.
        val pa = Offset(296f, aca[6].y + 1.4f); val pb = Offset(301f, alla[5].y - 0.6f)
        drawPath(poligono(pa + Offset(9.4f, 0.6f), pb + Offset(6.4f, 0.4f), pb + Offset(9.6f, 1.8f), pa + Offset(14f, 2.6f)), l.tono(mult(SOMME, r.color, 0.8f), 0.3f).copy(alpha = 0.6f))
        drawPath(poligono(pa, pb, pb + Offset(6.4f, 0f), pa + Offset(9.4f, 0f)), cara(l, r, piedra, 0.5f * r.dia, 0.3f))
        drawPath(poligono(pa + Offset(9.4f, 0f), pb + Offset(6.4f, 0f), pb + Offset(6.4f, 2.2f), pa + Offset(9.4f, 3.2f)), cara(l, r, piedra, lado(r, 1f), 0.3f))
        for (k in listOf(0.3f, 0.66f)) { val q = pa + (pb - pa) * k + Offset(9.4f - 3f * k, 1f); drawOval(l.tono(Color(0xFF242A30), 0.3f), q + Offset(-1.7f, 0f), Size(3.4f, 2.4f - k)) }
        drawLine(cara(l, r, piedra, 0.9f * r.dia, 0.3f), pa, pb, 0.9f); drawLine(cara(l, r, piedra, 0.9f * r.dia, 0.3f), pa + Offset(9.4f, 0f), pb + Offset(6.4f, 0f), 0.9f)

        // Las casas de este lado, de atrás adelante: cada hilera más grande, más nítida y más abajo.
        val bz = Azar(Semilla.de("gotico", "barrio"))
        // El molino de agua, con su rueda.
        casaDeVilla(m, l, r, 218f, aca[2].y - 0.4f - 13f * c, 15f, 8.6f, 0.34f, bz, paja = true, encendida = false)
        val rueda = Offset(236.6f, aca[2].y - 2.6f - 13f * c)
        drawCircle(l.tono(ROBLE_OSC, 0.3f), 5f, rueda, style = Stroke(1.1f)); for (k in 0..5) { val a = k * PI.toFloat() / 3f + 0.3f; drawLine(l.tono(ROBLE, 0.3f), rueda - Offset(cos(a), sin(a)) * 5f, rueda + Offset(cos(a), sin(a)) * 5f, 0.55f) }
        for (i in 0..9) { val x = 244f + i * 14.4f + bz.entre(-2f, 2f); if (x in 285f..310f) continue
            val base = 184.4f - (x - 240f) * 0.03f + bz.entre(-1f, 1f)
            casaDeVilla(m, l, r, x, base, bz.entre(9.6f, 12.4f), bz.entre(5.6f, 7.4f), 0.3f, bz, paja = i % 4 == 1, encendida = noche && i % 2 == 0, colgadura = procesion(m) || fiesta(m), enAgua = c * 2.6f) }
        for (x in listOf(262f, 328f, 344f)) arbolito(l, r, Offset(x, aca[4].y - 1.6f - 13f * c), 13f + (x % 4f), est, 0.3f, bz, nevado(m), alamo = true)
        // La calle que baja al puente, entre las dos hileras.
        drawPath(Path().apply { moveTo(232f, 206f); cubicTo(262f, 196f, 292f, 192f, 298f, 178f); lineTo(307f, 178.4f); cubicTo(304f, 196f, 276f, 200f, 250f, 209f); close() }, l.tono(lerp(TIERRA, CALIZA, 0.35f), 0.2f))
        atalaya(m, l, r)
        for (i in 0..7) { val x = 236f + i * 19.6f + bz.entre(-2.4f, 2.4f); if (x in 262f..286f) continue
            val base = 201.4f + bz.entre(-1.4f, 1.6f) + abs(i - 3) * 0.5f
            casaDeVilla(m, l, r, x, base, bz.entre(14f, 18f), bz.entre(8.4f, 11f), 0.16f, bz, paja = i == 5, encendida = noche && i % 3 != 1, colgadura = procesion(m) || fiesta(m)) }
        // Los pendones de la procesión, clavados a lo largo de la calle.
        if (procesion(m)) for (k in 0..4) { val p = Offset(243f + k * 12.6f, 205f - k * 4.6f); val alto = 15f - k * 1.6f; drawLine(l.tono(ROBLE_OSC, 0.2f), p, p + Offset(0f, -alto), 0.7f)
            drawPath(poligono(p + Offset(0.4f, -alto), p + Offset(7f - k * 0.6f, -alto + 1.6f), p + Offset(0.4f, -alto + 5.4f - k * 0.4f)), l.tono(if (k % 2 == 0) ROJO else AZUL, 0.16f)); drawCircle(l.tono(ORO, 0.16f), 0.8f, p + Offset(0f, -alto)) }
        if (fiesta(m)) banderines(l, Offset(238f, 190f), Offset(300f, 184f), 4f, listOf(ROJO, ORO, AZUL), 2.6f, lejos = 0.2f)
        // La leña de las hogueras de San Juan, apilada en la ribera.
        if (m.con("hogueras")) for (p in HOGUERAS) { for (k in -2..2) drawLine(l.tono(ROBLE_OSC, 0.3f), p + Offset(k * 1.5f, 0f), p + Offset(-k * 0.5f, -5.4f), 0.8f) }
        // Las velas de los difuntos, de noche, al pie de la parroquia.
        if (m.con("luto") && l.noche > 0.4f) for (k in 0..6) drawCircle(LUZ_DE_CASA.copy(alpha = 0.9f), 0.6f, Offset(222f + k * 2.6f, 158.6f + (k % 2) * 0.7f))
    }

    private val HOGUERAS = listOf(Offset(254f, 180.4f), Offset(322f, 177.6f), Offset(356f, 175.6f))

    /**
     * Una casa de entramado de la villa: la fachada de hastial, el costado (que mira a la derecha), el tejado de teja o
     * de paja, las vigas a la vista y una ventana, que de noche puede estar encendida. [enAgua]: cuánto la cubre la
     * crecida.
     */
    private fun DrawScope.casaDeVilla(
        m: Momento, l: Luces, r: Rayo, x: Float, base: Float, w: Float, h: Float, lejos: Float, az: Azar,
        paja: Boolean = false, encendida: Boolean = false, colgadura: Boolean = false, enAgua: Float = 0f,
    ) {
        val costado = w * 0.34f; val alza = w * az.entre(0.52f, 0.7f)
        val muro = lerp(ENLUCIDO, if (az.real() > 0.6f) Color(0xFFD2B48C) else Color(0xFFEDE6D2), az.real() * 0.6f)
        val techo = if (nevado(m)) NIEVE else if (paja) PAJA_VIEJA else lerp(TEJA, Color(0xFF8A4A32), az.real() * 0.5f)
        if (r.fuerza > 0.05f) { val d = (r.ax * h * 0.7f).coerceIn(-9f, 9f)
            drawOval(r.color.copy(alpha = 0.55f * r.fuerza), Offset(minOf(x, x + d), base - 0.8f), Size(w + costado + abs(d), maxOf(1.2f, h * 0.2f)), blendMode = androidx.compose.ui.graphics.BlendMode.Multiply) }
        drawRect(cara(l, r, muro, lado(r, 1f), lejos), Offset(x + w, base - h), Size(costado, h))
        drawPath(poligono(Offset(x + w, base - h), Offset(x + w + costado, base - h), Offset(x + w * 0.5f + costado, base - h - alza), Offset(x + w * 0.5f, base - h - alza)), cara(l, r, techo, lado(r, 1f) * 0.8f + 0.1f, lejos))
        val fachada = poligono(listOf(Offset(x, base), Offset(x + w, base), Offset(x + w, base - h), Offset(x + w * 0.5f, base - h - alza), Offset(x, base - h)))
        drawPath(fachada, cara(l, r, muro, frente(r), lejos))
        val viga = l.tono(ROBLE_OSC, lejos).copy(alpha = if (lejos > 0.4f) 0.5f else 0.9f); val g = maxOf(0.4f, w * 0.045f)
        clipPath(fachada) {
            drawLine(viga, Offset(x, base - h), Offset(x + w, base - h), g); drawLine(viga, Offset(x, base - h * 0.5f), Offset(x + w, base - h * 0.5f), g * 0.8f)
            if (lejos < 0.4f) { for (k in 1..3) drawLine(viga, Offset(x + w * k / 4f, base), Offset(x + w * k / 4f, base - h - alza), g * 0.7f)
                drawLine(viga, Offset(x, base - h), Offset(x + w * 0.25f, base - h * 0.5f), g * 0.7f); drawLine(viga, Offset(x + w, base - h), Offset(x + w * 0.75f, base - h * 0.5f), g * 0.7f) }
        }
        // El vuelo del tejado sobre el hastial, y su borde.
        drawLine(cara(l, r, techo, 0.2f, lejos), Offset(x - 0.6f, base - h + 0.3f), Offset(x + w * 0.5f, base - h - alza - 0.3f), g * 1.6f); drawLine(cara(l, r, techo, -0.3f, lejos), Offset(x + w + 0.4f, base - h + 0.3f), Offset(x + w * 0.5f, base - h - alza - 0.3f), g * 1.6f)
        val ven = Offset(x + w * 0.56f, base - h * 0.86f)
        drawRect(if (encendida) LUZ_DE_CASA else l.tono(Color(0xFF3A3432), lejos), ven, Size(w * 0.2f, h * 0.24f))
        if (encendida) drawCircle(LUZ_DE_CASA.copy(alpha = 0.16f), w * 0.5f, ven + Offset(w * 0.1f, h * 0.12f))
        drawRect(l.tono(ROBLE_OSC, lejos), Offset(x + w * 0.16f, base - h * 0.44f), Size(w * 0.2f, h * 0.44f))
        if (colgadura && lejos < 0.4f) drawRect(l.tono(if (az.real() > 0.5f) ROJO else AZUL, lejos), ven + Offset(-w * 0.04f, h * 0.24f), Size(w * 0.28f, h * 0.2f))
        if (enAgua > 0.05f) { val y = base - minOf(enAgua, h * 0.4f)
            drawRect(l.tono(SOMME, lejos).copy(alpha = 0.92f), Offset(x - 2f, y), Size(w + costado + 4f, base - y + 1.4f))
            reflejoRoto(x, x + w + costado, y, 5f, l.tono(muro, lejos), 0.4f, az, 0.8f) }
    }

    /** La atalaya de la comuna: una torre de piedra con su cuerpo de campanas, que voltean en las fiestas grandes. */
    private fun DrawScope.atalaya(m: Momento, l: Luces, r: Rayo) {
        val x = 270f; val w = 10f; val base = 196f; val arriba = 131f; val lejos = 0.2f
        val piedra = lerp(CALIZA, Color(0xFFB8A888), 0.35f)
        if (r.fuerza > 0.05f) drawOval(r.color.copy(alpha = 0.5f * r.fuerza), Offset(minOf(x, x + r.ax * 9f), base - 1f), Size(w + 4f + abs(r.ax * 9f), 2.4f), blendMode = androidx.compose.ui.graphics.BlendMode.Multiply)
        drawRect(cara(l, r, piedra, frente(r), lejos), Offset(x, arriba), Size(w, base - arriba)); drawRect(cara(l, r, piedra, lado(r, 1f), lejos), Offset(x + w, arriba), Size(3.6f, base - arriba))
        val junta = l.tono(CALIZA_OSC, lejos).copy(alpha = 0.4f); var y = arriba + 4f; while (y < base) { drawLine(junta, Offset(x, y), Offset(x + w + 3.6f, y), 0.3f); y += 4.4f }
        for (yy in listOf(150f, 170f)) drawRect(l.tono(Color(0xFF34302E), lejos), Offset(x + 4f, yy), Size(1.6f, 4.4f))
        // El cuerpo de campanas: cuatro pies derechos, los huecos y, dentro, las dos campanas.
        drawRect(l.tono(Color(0xFF2E2C30), lejos), Offset(x - 0.6f, arriba - 9f), Size(w + 4.8f, 9f))
        for (k in 0..3) drawRect(cara(l, r, piedra, if (k == 3) lado(r, 1f) else frente(r), lejos), Offset(x - 0.6f + k * 4.5f, arriba - 9f), Size(1.5f, 9f))
        drawRect(cara(l, r, piedra, frente(r) + 0.2f, lejos), Offset(x - 1.4f, arriba - 1f), Size(w + 6.4f, 1.6f))
        val volteo = if (m.con("campanas")) 38f else 0f
        for ((k, cx) in listOf(x + 2.6f, x + 7.2f).withIndex()) rotate(if (k == 0) volteo else -volteo * 0.8f, Offset(cx, arriba - 8f)) {
            drawPath(Path().apply { moveTo(cx - 1.1f, arriba - 7.6f); cubicTo(cx - 1.2f, arriba - 4.6f, cx - 1.9f, arriba - 3.6f, cx - 2.1f, arriba - 2.6f); lineTo(cx + 2.1f, arriba - 2.6f); cubicTo(cx + 1.9f, arriba - 3.6f, cx + 1.2f, arriba - 4.6f, cx + 1.1f, arriba - 7.6f); close() }, l.tono(Color(0xFFB8924A), lejos))
        }
        drawPath(poligono(Offset(x - 1.6f, arriba - 9f), Offset(x + w * 0.5f + 1f, arriba - 23f), Offset(x + w + 5f, arriba - 9f)), cara(l, r, if (nevado(m)) NIEVE else PLOMO, frente(r), lejos))
        drawPath(poligono(Offset(x + w + 5f, arriba - 9f), Offset(x + w * 0.5f + 1f, arriba - 23f), Offset(x + w + 1f, arriba - 9f)), cara(l, r, if (nevado(m)) NIEVE else PLOMO, lado(r, 1f), lejos))
        drawLine(l.tono(HIERRO, lejos), Offset(x + w * 0.5f + 1f, arriba - 23f), Offset(x + w * 0.5f + 1f, arriba - 27f), 0.5f)
        drawPath(poligono(Offset(x + w * 0.5f + 1f, arriba - 27f), Offset(x + w * 0.5f + 4.4f, arriba - 26f), Offset(x + w * 0.5f + 1f, arriba - 25f)), l.tono(ROJO, lejos))
        if (l.noche > 0.5f) drawRect(LUZ_DE_CASA.copy(alpha = 0.8f), Offset(x + 4f, 150f), Size(1.6f, 4.4f))
    }

    // ---------------------------------------------------------------- La catedral

    /** Cuánta luz le da a una cara que mira hacia [mira]: −1 a la izquierda, 0 al frente, 1 a la derecha. */
    private fun haciaDe(r: Rayo, mira: Float) = (0.2f * (1f - abs(mira)) + r.lat * mira).coerceIn(-1f, 1f) * (0.5f + 0.5f * r.dia) - (1f - r.dia) * 0.25f

    private val CARAS = floatArrayOf(84f, 110f, 140f, 172f, 202f, 228f)
    private val PIES = floatArrayOf(203f, 207f, 210f, 210.6f, 208.4f, 204.6f)
    private val MIRAS = floatArrayOf(-0.9f, -0.45f, 0f, 0.45f, 0.9f)
    private val DISPAREJO = floatArrayOf(0.94f, 0.8f, 1f, 0.72f, 0.88f)
    /** En qué orden se van armando los andamios de la cabecera: primero el paño del centro. */
    private val ORDEN = intArrayOf(2, 1, 3, 0, 4)

    /** Un hueco de ventana ojival entre [x0] y [x1], del alféizar [abajo] al vértice [arriba]. */
    private fun ojiva(x0: Float, x1: Float, abajo: Float, arriba: Float) = Path().apply {
        val cx = (x0 + x1) / 2f; val arranque = arriba + (x1 - x0) * 0.9f
        moveTo(x0, abajo); lineTo(x0, arranque); cubicTo(x0, arranque - (x1 - x0) * 0.5f, cx - (x1 - x0) * 0.2f, arriba + (x1 - x0) * 0.2f, cx, arriba)
        cubicTo(cx + (x1 - x0) * 0.2f, arriba + (x1 - x0) * 0.2f, x1, arranque - (x1 - x0) * 0.5f, x1, arranque); lineTo(x1, abajo); close()
    }

    /**
     * La catedral, en su altozano: la nave ya cubierta, que se aleja hacia la izquierda con sus arbotantes; el hastial
     * con el gran arco cerrado con tablas, donde empezará el coro; y, delante, la cabecera a medio subir.
     */
    private fun DrawScope.catedral(m: Momento, l: Luces, r: Rayo, est: Estacion) {
        val az = Azar(Semilla.de("gotico", "catedral"))
        val nieve = nevado(m)
        val plomo = if (nieve) lerp(PLOMO, NIEVE, 0.82f) else PLOMO
        val vidrio = lerp(Color(0xFF2C3850), LUZ_DE_CASA, 0.34f * l.noche)
        fun pared(mira: Float, lejos: Float = 0.08f, propio: Color = CALIZA) = cara(l, r, propio, haciaDe(r, mira), lejos)
        val junta = l.tono(CALIZA_OSC, 0.08f)
        // El altozano de la obra: tierra blanca de polvo de piedra, con rodadas.
        val suelo = if (nieve) lerp(TIERRA, NIEVE, 0.7f) else lerp(TIERRA, CALIZA, 0.5f)
        drawPath(curva(listOf(Offset(-4f, 196f), Offset(60f, 195f), Offset(130f, 197f), Offset(196f, 199.4f), Offset(226f, 202.6f), Offset(246f, 208f), Offset(266f, 216f)), 252f), Brush.verticalGradient(listOf(l.tono(suelo, 0.14f), l.tono(lerp(suelo, TIERRA, 0.3f))), 195f, 250f))
        repeat(26) { mancha(Offset(az.entre(110f, 240f), az.entre(212f, 246f)), az.entre(6f, 18f), az.entre(1f, 2.6f), l.tono(if (it % 2 == 0) CALIZA else lerp(TIERRA, ROBLE_OSC, 0.3f)), 0.3f) }
        for (k in 0..1) drawPath(Path().apply { moveTo(150f + k * 9f, 213f); cubicTo(152f + k * 9f, 224f, 166f + k * 11f, 236f, 186f + k * 13f, 247f) }, l.tono(lerp(TIERRA, ROBLE_OSC, 0.45f)).copy(alpha = 0.5f), style = Stroke(1.5f, cap = StrokeCap.Round))

        // --- La nave, que se aleja hacia la izquierda.
        fun alero(x: Float) = 100f - 16f * (x + 2f) / 106f
        val tejado = poligono(Offset(104f, 84f), Offset(-2f, 100f), Offset(-2f, 62f), Offset(128f, 42f))
        drawPath(tejado, Brush.verticalGradient(listOf(cara(l, r, plomo, haciaDe(r, -0.6f) * 0.6f + 0.3f, 0.14f), cara(l, r, plomo, haciaDe(r, -0.6f) * 0.6f, 0.1f)), 42f, 100f))
        clipPath(tejado) { for (i in 0..16) { val t = i / 16f; drawLine(l.tono(mult(plomo, r.color, 0.7f), 0.1f).copy(alpha = 0.42f), Offset(-2f + 106f * t, 100f - 16f * t), Offset(-2f + 130f * t, 62f - 20f * t), 0.6f) }
            repeat(9) { mancha(Offset(az.entre(0f, 110f), az.entre(50f, 96f)), az.entre(6f, 14f), az.entre(2f, 5f), l.tono(if (it % 2 == 0) Color(0xFFA8B8C0) else Color(0xFF3E5262), 0.1f), 0.2f) } }
        drawLine(cara(l, r, plomo, 0.8f * r.dia, 0.1f), Offset(-2f, 62f), Offset(128f, 42f), 1f)
        for (i in 0..20) { val t = i / 20f; drawLine(l.tono(HIERRO, 0.12f), Offset(-2f + 126f * t, 62f - 19.4f * t), Offset(-2f + 126f * t, 60.2f - 19.4f * t), 0.5f) }
        val alto = poligono(Offset(-2f, 100f), Offset(104f, 84f), Offset(104f, 138f), Offset(-2f, 146f))
        drawPath(alto, pared(-0.8f, 0.1f))
        val colateral = poligono(Offset(-2f, 146f), Offset(104f, 138f), Offset(104f, 150f), Offset(-2f, 157f))
        val bajo = poligono(Offset(-2f, 157f), Offset(104f, 150f), Offset(104f, 200f), Offset(-2f, 197f))
        drawPath(bajo, pared(-0.8f, 0.07f)); drawPath(colateral, cara(l, r, plomo, haciaDe(r, -0.7f) * 0.5f + 0.1f, 0.08f))
        clipPath(alto) { var y = 86f; while (y < 150f) { drawLine(junta.copy(alpha = 0.2f), Offset(-2f, y + 16f), Offset(104f, y), 0.35f); y += 4.6f } }
        clipPath(bajo) { var y = 150f; while (y < 204f) { drawLine(junta.copy(alpha = 0.22f), Offset(-2f, y + 7f), Offset(104f, y), 0.35f); y += 5f }
            repeat(12) { mancha(Offset(az.entre(0f, 100f), az.entre(160f, 198f)), az.entre(3f, 9f), az.entre(2f, 6f), l.tono(if (it % 3 == 0) Color(0xFF8E947C) else CALIZA_OSC, 0.07f), 0.2f) }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFF7E8468), 0.05f).copy(alpha = 0.3f)), 186f, 200f), Offset(-2f, 186f), Size(108f, 14f)) }
        drawLine(pared(0.6f, 0.1f), Offset(-2f, 100f), Offset(104f, 84f), 1.3f); drawLine(l.tono(mult(CALIZA, r.color, 0.8f), 0.1f).copy(alpha = 0.6f), Offset(-2f, 101.6f), Offset(104f, 85.6f), 0.8f)
        // Los estribos con sus pináculos y sus arbotantes, y entre ellos los ventanales.
        val estribos = listOf(95f, 75f, 56f, 38f, 21f, 5f)
        for ((i, px) in estribos.withIndex()) {
            val e = alero(px); val k = 1f - i * 0.06f; val w = 5.6f * k
            val sig = if (i == 0) 104f + 6f else estribos[i - 1]
            val cx = (px + w + sig) / 2f + 0.6f; val va = 6.4f * k
            // El ventanal alto, con su parteluz y su rosa; el de la nave baja.
            val ventanal = ojiva(cx - va / 2f, cx + va / 2f, e + 37f * k + 3f, e + 6f)
            drawPath(ventanal, l.tono(mult(CALIZA, r.color, 0.85f), 0.1f), style = Stroke(1.5f)); drawPath(ventanal, l.tono(vidrio, 0.1f))
            drawLine(pared(0.4f, 0.1f).copy(alpha = 0.8f), Offset(cx, e + 12f), Offset(cx, e + 40f * k), 0.45f); drawCircle(pared(0.4f, 0.1f).copy(alpha = 0.8f), 1.3f * k, Offset(cx, e + 10.6f), style = Stroke(0.4f))
            val bajoV = ojiva(cx - va * 0.42f, cx + va * 0.42f, e + 90f * k + (1f - k) * 30f, e + 68f * k + (1f - k) * 34f)
            drawPath(bajoV, l.tono(mult(CALIZA, r.color, 0.85f), 0.08f), style = Stroke(1.2f)); drawPath(bajoV, l.tono(vidrio, 0.08f))
        }
        capaSombra(r, 0.9f) { c ->
            // La sombra de cada estribo y de su arbotante sobre el muro, y la del alero.
            for ((i, px) in estribos.withIndex()) { val e = alero(px); val k = 1f - i * 0.06f; val d = -r.lat * 7f * k
                clipPath(Path().apply { addPath(alto); addPath(bajo); addPath(colateral) }) {
                    drawPath(poligono(Offset(px + d, 200f), Offset(px + 5.6f * k + d, 200f), Offset(px + 5.6f * k + d, e + 28f), Offset(px + d, e + 30f)), c)
                    drawLine(c, Offset(px + d + 2f, e + 29f + 2f * r.tan.coerceAtMost(2f)), Offset(px + 12f * k + d, e + 11f + 2f * r.tan.coerceAtMost(2f)), 2f * k) } }
            clipPath(alto) { drawPath(poligono(Offset(-2f, 100f), Offset(104f, 84f), Offset(104f, 84f + (2.4f * r.tan).coerceIn(1f, 6f)), Offset(-2f, 100f + (2.4f * r.tan).coerceIn(1f, 6f))), c) }
        }
        for ((i, px) in estribos.withIndex()) {
            val e = alero(px); val k = 1f - i * 0.06f; val w = 5.6f * k; val cima = e + 27f
            drawLine(pared(0.3f, 0.09f), Offset(px + 2f, cima + 2f), Offset(px + 12f * k, e + 9f), 2.2f * k); drawLine(pared(0.9f, 0.09f), Offset(px + 2f, cima + 0.9f), Offset(px + 12f * k, e + 8f), 0.6f)
            drawRect(pared(-0.2f, 0.08f), Offset(px, cima), Size(w, 200f - cima)); drawRect(pared(1f, 0.08f), Offset(px + w * 0.62f, cima), Size(w * 0.38f, 200f - cima))
            for (yy in listOf(cima + 22f, cima + 46f)) drawLine(pared(0.8f, 0.08f), Offset(px - 0.5f, yy), Offset(px + w + 0.5f, yy), 0.7f)
            drawPath(poligono(Offset(px - 0.4f, cima), Offset(px + w / 2f, cima - 12f * k), Offset(px + w + 0.4f, cima)), pared(-0.2f, 0.09f)); drawPath(poligono(Offset(px + w / 2f, cima), Offset(px + w / 2f, cima - 12f * k), Offset(px + w + 0.4f, cima)), pared(1f, 0.09f))
            if (nieve) drawLine(l.tono(NIEVE, 0.08f), Offset(px + 2f, cima + 0.6f), Offset(px + 12f * k, e + 7.8f), 0.9f)
        }

        // --- El hastial, con el gran arco cerrado con tablas.
        val hastial = poligono(listOf(Offset(104f, 200f), Offset(206f, 203f), Offset(206f, 84f), Offset(155f, 36f), Offset(104f, 84f)))
        drawPath(hastial, Brush.horizontalGradient(listOf(pared(0.25f - 0.2f * r.lat), pared(0.25f + 0.2f * r.lat)), 104f, 206f))
        clipPath(hastial) {
            var y = 40f; while (y < 204f) { drawLine(junta.copy(alpha = 0.2f), Offset(104f, y), Offset(206f, y + 1.6f), 0.35f); y += 5f }
            repeat(60) { val p = Offset(az.entre(104f, 204f), 40f + 5f * az.entero(32)); drawLine(junta.copy(alpha = 0.22f), p, p + Offset(0f, 5f), 0.3f) }
            repeat(14) { mancha(Offset(az.entre(106f, 204f), az.entre(60f, 200f)), az.entre(4f, 11f), az.entre(4f, 12f), l.tono(if (it % 3 == 0) Color(0xFF8E947C) else if (it % 3 == 1) CALIDA else CALIZA_OSC, 0.08f), 0.16f) }
            // Las chorreras bajo el óculo y bajo las cornisas.
            for (x in listOf(149f, 161f, 118f, 192f)) drawLine(l.tono(Color(0xFF6E705E), 0.08f).copy(alpha = 0.2f), Offset(x, 76f), Offset(x + 0.6f, 76f + az.entre(14f, 30f)), 1.4f)
        }
        drawLine(pared(0.9f), Offset(104f, 84f), Offset(155f, 36f), 1.5f); drawLine(pared(0.9f), Offset(206f, 84f), Offset(155f, 36f), 1.5f)
        if (nieve) { drawLine(l.tono(NIEVE, 0.08f), Offset(104f, 83f), Offset(155f, 35f), 1.4f); drawLine(l.tono(NIEVE, 0.08f), Offset(206f, 83f), Offset(155f, 35f), 1.4f) }
        drawLine(l.tono(HIERRO, 0.1f), Offset(155f, 36f), Offset(155f, 27f), 0.7f); drawLine(l.tono(HIERRO, 0.1f), Offset(152.4f, 30f), Offset(157.6f, 30f), 0.7f)
        drawLine(pared(0.8f), Offset(104f, 124f), Offset(206f, 126f), 1f)
        // Una galería de arquillos ciegos sobre la imposta, con su sombra dentro.
        for (k in 0..8) { val x = 113f + k * 9.6f; if (x in 120f..188f && k in 3..5) continue
            val arq = ojiva(x, x + 5.6f, 123f, 108f); drawPath(arq, l.tono(mult(CALIZA, r.color, 0.9f), 0.08f).copy(alpha = 0.8f)); drawLine(pared(0.9f), Offset(if (r.lat >= 0f) x else x + 5.6f, 123f), Offset(if (r.lat >= 0f) x else x + 5.6f, 113f), 0.6f) }
        drawLine(l.tono(mult(CALIZA, r.color, 0.9f), 0.08f).copy(alpha = 0.7f), Offset(104f, 125.6f), Offset(206f, 127.6f), 1.1f)
        drawLine(l.tono(mult(CALIZA, r.color, 0.9f), 0.08f).copy(alpha = 0.5f), Offset(106f, 85.6f), Offset(155f, 39f), 1f); drawLine(l.tono(mult(CALIZA, r.color, 0.9f), 0.08f).copy(alpha = 0.5f), Offset(204f, 85.6f), Offset(155f, 39f), 1f)
        val oculo = Offset(155f, 66f)
        drawCircle(pared(0.7f), 8.4f, oculo); drawCircle(l.tono(mult(CALIZA, r.color, 0.9f), 0.08f), 7.2f, oculo); drawCircle(l.tono(vidrio, 0.08f), 6.2f, oculo)
        for (k in 0..5) { val a = k * PI.toFloat() / 3f; drawLine(pared(0.5f).copy(alpha = 0.85f), oculo, oculo + Offset(cos(a), sin(a)) * 6.2f, 0.5f) }
        drawCircle(pared(0.5f), 1.6f, oculo)
        val arco = ojiva(124f, 186f, 202.4f, 86f)
        drawPath(arco, l.tono(mult(CALIZA, r.color, 0.9f), 0.08f), style = Stroke(4.4f)); drawPath(arco, pared(0.7f), style = Stroke(1.1f))
        clipPath(arco) {
            drawRect(Brush.verticalGradient(listOf(l.tono(lerp(ROBLE, PAJA_VIEJA, 0.3f), 0.1f), l.tono(ROBLE, 0.06f)), 86f, 203f), Offset(124f, 86f), Size(62f, 118f))
            var x = 124f; var n = 0; while (x < 186f) { val w = 3.6f + (n % 3) * 0.7f; if (n % 4 == 1) drawRect(l.tono(lerp(ROBLE, CALIDA, 0.25f), 0.08f).copy(alpha = 0.6f), Offset(x, 86f), Size(w, 118f)); if (n % 5 == 3) drawRect(l.tono(ROBLE_OSC, 0.08f).copy(alpha = 0.4f), Offset(x, 86f), Size(w, 118f))
                drawLine(l.tono(ROBLE_OSC, 0.08f).copy(alpha = 0.7f), Offset(x, 86f), Offset(x, 204f), 0.45f); x += w; n++ }
            for (y in listOf(112f, 146f, 182f)) { drawLine(l.tono(ROBLE_OSC, 0.08f), Offset(124f, y), Offset(186f, y + 0.8f), 1.7f); drawLine(l.tono(lerp(ROBLE, CALIDA, 0.3f), 0.08f), Offset(124f, y - 0.9f), Offset(186f, y - 0.1f), 0.4f) }
            drawLine(l.tono(ROBLE_OSC, 0.08f), Offset(124f, 182f), Offset(186f, 112f), 1.1f)
            // El postigo por donde se entra a la nave. De noche deja ver la lámpara de dentro.
            drawRect(lerp(l.tono(Color(0xFF2A2420), 0.06f), LUZ_DE_CASA, 0.5f * l.noche), Offset(150f, 187f), Size(9f, 16f))
            // La sombra que el arco echa sobre las tablas.
            drawPath(arco, l.tono(mult(ROBLE_OSC, r.color, 0.9f), 0.08f).copy(alpha = 0.5f * r.dia + 0.1f), style = Stroke(9f))
        }
        // Las dos torrecillas de escalera que flanquean el hastial.
        for (x in listOf(99f, 201f)) {
            drawRect(pared(0f), Offset(x, 72f), Size(10f, 131f)); drawRect(pared(1f), Offset(x + 6.4f, 72f), Size(3.6f, 131f)); drawRect(pared(-1f).copy(alpha = 0.6f), Offset(x, 72f), Size(1.6f, 131f))
            for (yy in listOf(96f, 126f, 158f)) drawRect(l.tono(Color(0xFF3A3634), 0.08f), Offset(x + 3f, yy), Size(1.4f, 5f))
            for (yy in listOf(84f, 124f)) drawLine(pared(0.8f), Offset(x - 0.6f, yy), Offset(x + 10.6f, yy), 0.9f)
            drawPath(poligono(Offset(x - 0.8f, 72f), Offset(x + 5f, 52f), Offset(x + 10.8f, 72f)), cara(l, r, plomo, haciaDe(r, 0f), 0.08f)); drawPath(poligono(Offset(x + 5f, 72f), Offset(x + 5f, 52f), Offset(x + 10.8f, 72f)), cara(l, r, plomo, haciaDe(r, 1f), 0.08f))
        }
        cabecera(m, l, r, az)
        // La fragua del herrero de la obra, que asoma tras el medio muro de la logia.
        drawRect(cara(l, r, ENLUCIDO, frente(r), 0.1f), Offset(236f, 195f), Size(15f, 13f)); drawRect(lerp(l.tono(Color(0xFF2A2420), 0.1f), LLAMA_FRAGUA, if (m.hacer == Hacer.TRABAJO && obra(m) != Obra.TAPADA) 0.75f else 0f), Offset(240f, 199f), Size(6f, 7f))
        drawPath(poligono(Offset(233f, 196f), Offset(238f, 188.6f), Offset(254f, 190f), Offset(254f, 197f)), l.tono(if (nieve) NIEVE else PAJA_VIEJA, 0.1f))
    }

    private val LLAMA_FRAGUA = Color(0xFFFF9A3C)

    /** La cabecera: cinco paños de muro entre sus pilares, tan altos como diga el arco `coro`, con lo que la obra tenga encima. */
    private fun DrawScope.cabecera(m: Momento, l: Luces, r: Rayo, az: Azar) {
        val hc = 30f + 94f * alturaDelCoro(m)
        val tapada = obra(m) == Obra.TAPADA; val nieve = nevado(m); val caido = andamioCaido(m)
        val n = andamios(m)
        val madera = l.tono(ROBLE, 0.05f); val maderaLuz = l.tono(lerp(ROBLE, CALIDA, 0.45f), 0.05f); val maderaOsc = l.tono(ROBLE_OSC, 0.05f)
        fun cima(i: Int, x: Float): Float { val t = (x - CARAS[i]) / (CARAS[i + 1] - CARAS[i]); return PIES[i] + (PIES[i + 1] - PIES[i]) * t - hc * DISPAREJO[i] }
        fun pie(i: Int, x: Float): Float { val t = (x - CARAS[i]) / (CARAS[i + 1] - CARAS[i]); return PIES[i] + (PIES[i + 1] - PIES[i]) * t }
        fun conAndamio(i: Int) = if (caido) i == 1 || (i == 3 && n >= 2) else ORDEN.indexOf(i) < n
        // Los trazos de un cuerpo de andamio: pies derechos, travesaños, riostras y la escalera.
        fun DrawScope.andamio(i: Int, c: Color?, g: Float) {
            val x0 = CARAS[i] + 4f; val x1 = CARAS[i + 1] - 4f; val arriba = cima(i, x0) - 9f
            for (x in listOf(x0, (x0 + x1) / 2f, x1)) drawLine(c ?: maderaOsc, Offset(x, pie(i, x) + 1.6f), Offset(x + (x - (x0 + x1) / 2f) * 0.02f, arriba - (x % 3f)), g * 1.2f, StrokeCap.Round)
            var y = pie(i, x0) - 11f; var k = 0
            while (y > arriba + 3f) { val d = pie(i, x1) - pie(i, x0)
                drawLine(c ?: madera, Offset(x0 - 2f, y), Offset(x1 + 2f, y + d), g)
                if (k % 2 == 1 || y - 11f <= arriba + 3f) { drawLine(c ?: maderaLuz, Offset(x0 - 3f, y - 1.1f), Offset(x1 + 3f, y + d - 1.1f), g * 1.9f); if (c == null) drawLine(maderaOsc, Offset(x0 - 3f, y + 0.1f), Offset(x1 + 3f, y + d + 0.1f), 0.4f) }
                drawLine(c ?: madera, Offset(if (k % 2 == 0) x0 else x1, y), Offset(if (k % 2 == 0) (x0 + x1) / 2f else (x0 + x1) / 2f, y + 11f + d / 2f), g * 0.7f)
                y -= 11f; k++ }
            // La escalera de mano, del suelo al primer tablón.
            val e0 = Offset(x1 - 3f, pie(i, x1) + 2f); val e1 = Offset(x1 - 8f, pie(i, x1) - 22f)
            drawLine(c ?: maderaOsc, e0, e1, g * 0.8f); drawLine(c ?: maderaOsc, e0 + Offset(2.6f, 0f), e1 + Offset(2.6f, 0f), g * 0.8f)
            if (c == null) for (q in 1..6) { val p = e0 + (e1 - e0) * (q / 7f); drawLine(madera, p, p + Offset(2.6f, 0f), 0.5f) }
        }
        // De atrás adelante: los paños de los lados y, al final, el del centro.
        for (i in listOf(0, 4, 1, 3, 2)) {
            val x0 = CARAS[i]; val x1 = CARAS[i + 1]
            val pano = poligono(Offset(x0, PIES[i]), Offset(x1, PIES[i + 1]), Offset(x1, cima(i, x1)), Offset(x0, cima(i, x0)))
            val hacia = haciaDe(r, MIRAS[i])
            // Los dientes de las hiladas a medio poner, arriba.
            val dz = Azar(Semilla.de("gotico", "dientes", i))
            repeat(5) { val x = dz.entre(x0 + 1f, x1 - 7f); val w = dz.entre(3.4f, 6.4f); val h = dz.entre(1.6f, 3.6f); drawRect(cara(l, r, CALIZA, hacia + 0.1f, 0.05f), Offset(x, cima(i, x) - h), Size(w, h + 0.5f)) }
            drawPath(pano, cara(l, r, CALIZA, hacia, 0.05f))
            clipPath(pano) {
                var y = PIES[i] + 2f; while (y > cima(i, x0) - 4f) { drawLine(l.tono(CALIZA_OSC, 0.05f).copy(alpha = 0.3f), Offset(x0, y + (PIES[i] - PIES[i]) ), Offset(x1, y + PIES[i + 1] - PIES[i]), 0.4f); y -= 4.4f }
                repeat(16) { val p = Offset(dz.entre(x0, x1), PIES[i] - 4.4f * dz.entero(26)); drawLine(l.tono(CALIZA_OSC, 0.05f).copy(alpha = 0.3f), p, p + Offset(0f, -4.4f), 0.35f) }
                repeat(5) { mancha(Offset(dz.entre(x0, x1), dz.entre(cima(i, x0), PIES[i])), dz.entre(3f, 7f), dz.entre(3f, 8f), l.tono(if (it % 2 == 0) CALIDA else CALIZA_OSC, 0.05f), 0.2f) }
                // El zócalo, con su moldura.
                drawLine(cara(l, r, CALIZA, hacia + 0.4f, 0.05f), Offset(x0, PIES[i] - 6f), Offset(x1, PIES[i + 1] - 6f), 1.1f); drawLine(l.tono(mult(CALIZA, r.color, 0.8f), 0.05f).copy(alpha = 0.7f), Offset(x0, PIES[i] - 4.9f), Offset(x1, PIES[i + 1] - 4.9f), 0.7f)
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFF8C8468), 0.05f).copy(alpha = 0.32f)), PIES[i] - 5f, PIES[i] + 3f), Offset(x0, PIES[i] - 5f), Size(x1 - x0, 9f))
            }
            // El hueco de la ventana, cuando el muro ya ha subido lo bastante: abierto por arriba o ya cerrado en ojiva.
            val cx = (x0 + x1) / 2f; val va = (x1 - x0) * 0.32f; val alfeizar = pie(i, cx) - 14f; val vertice = alfeizar - 44f
            if (cima(i, cx) < alfeizar - 5f) {
                val abierto = cima(i, cx) > vertice - 4f
                val hueco = if (abierto) caja(cx - va / 2f, cima(i, cx) - 5f, cx + va / 2f, alfeizar) else ojiva(cx - va / 2f, cx + va / 2f, alfeizar, vertice)
                clipPath(pano) { drawPath(hueco, l.tono(lerp(mult(CALIZA, r.color, 0.9f), Color(0xFF6A6460), 0.5f), 0.07f))
                    drawLine(cara(l, r, CALIZA, if (r.lat >= 0f) 0.8f * r.dia else -0.6f, 0.05f), Offset(cx - va / 2f, alfeizar), Offset(cx - va / 2f, maxOf(cima(i, cx), vertice + va)), 1.2f)
                    drawLine(cara(l, r, CALIZA, if (r.lat < 0f) 0.8f * r.dia else -0.6f, 0.05f), Offset(cx + va / 2f, alfeizar), Offset(cx + va / 2f, maxOf(cima(i, cx), vertice + va)), 1.2f)
                    drawLine(cara(l, r, CALIZA, 0.8f * r.dia, 0.05f), Offset(cx - va / 2f - 1f, alfeizar), Offset(cx + va / 2f + 1f, alfeizar), 1.2f) }
            }
            if (tapada) paja(l, x0, x1, { cima(i, it) }, nieve, dz)
            else if (nieve) drawLine(l.tono(NIEVE, 0.05f), Offset(x0, cima(i, x0) - 0.6f), Offset(x1, cima(i, x1) - 0.6f), 1.6f)
        }
        // Las sombras de los andamios sobre sus paños (y las del que cedió), y encima los andamios.
        capaSombra(r, 0.7f) { c ->
            for (i in 0..4) if (conAndamio(i)) clipPath(poligono(Offset(CARAS[i], PIES[i]), Offset(CARAS[i + 1], PIES[i + 1]), Offset(CARAS[i + 1], cima(i, CARAS[i + 1])), Offset(CARAS[i], cima(i, CARAS[i])))) { translate(-r.lat * 3f, 1.4f) { andamio(i, c, 1.3f) } }
            if (caido) { for ((a, b) in CEDIDO) drawLine(c, a + Offset(-r.lat * 3f, 1.6f), b + Offset(-r.lat * 3f, 1.6f), 1.6f); drawOval(c, Offset(126f, 211f), Size(52f, 7f)) }
        }
        for (i in listOf(0, 4, 1, 3, 2)) if (conAndamio(i)) andamio(i, null, 0.9f)
        // Los pilares, que suben un poco más que los paños.
        for (i in 0..5) {
            val x = CARAS[i]; val alto = hc * (if (i == 0) DISPAREJO[0] else if (i == 5) DISPAREJO[4] else maxOf(DISPAREJO[i - 1], DISPAREJO[i])) + 7f
            val mira = if (i == 0) -0.9f else if (i == 5) 0.9f else (MIRAS[i - 1] + MIRAS[i]) / 2f
            drawRect(cara(l, r, CALIZA, haciaDe(r, mira) + 0.12f, 0.04f), Offset(x - 3.2f, PIES[i] - alto), Size(6.4f, alto + 1.6f))
            drawRect(cara(l, r, CALIZA, haciaDe(r, mira + 0.9f), 0.04f), Offset(x + 1.2f, PIES[i] - alto), Size(2f, alto + 1.6f)); drawRect(cara(l, r, CALIZA, haciaDe(r, mira - 0.9f), 0.04f), Offset(x - 3.2f, PIES[i] - alto), Size(1.4f, alto + 1.6f))
            drawRect(cara(l, r, CALIZA, haciaDe(r, mira) + 0.3f, 0.04f), Offset(x - 4.2f, PIES[i] - 4f), Size(8.4f, 5.6f))
            if (tapada) paja(l, x - 4f, x + 4f, { PIES[i] - alto }, nieve, Azar(Semilla.de("gotico", "paja pilar", i)))
            else if (nieve) drawRect(l.tono(NIEVE, 0.05f), Offset(x - 3.4f, PIES[i] - alto - 1.2f), Size(6.8f, 1.6f))
        }
        grua(m, l, r, cima(1, 124f))
        if (caido) andamioCedido(l, r)
        // Las piedras que esperan al pie, todavía en bruto, sobre sus rodillos.
        for ((k, p) in listOf(Offset(170f, 232f), Offset(186f, 238f), Offset(180f, 226f)).withIndex()) {
            drawOval(Color.Black.copy(alpha = 0.2f), p + Offset(-1f, 5f), Size(15f, 3f))
            drawRect(cara(l, r, CALIZA, haciaDe(r, 0f) - 0.1f), p, Size(13f, 6.4f)); drawPath(poligono(p, p + Offset(2.4f, -2.6f), p + Offset(15.4f, -2.6f), p + Offset(13f, 0f)), cara(l, r, CALIZA, 0.8f * r.dia))
            drawPath(poligono(p + Offset(13f, 0f), p + Offset(15.4f, -2.6f), p + Offset(15.4f, 3.8f), p + Offset(13f, 6.4f)), cara(l, r, CALIZA, lado(r, 1f)))
            if (k == 0) for (q in 0..1) drawCircle(l.tono(ROBLE_OSC), 1.2f, p + Offset(3f + q * 7f, 7f))
        }
    }

    /** La paja con que se tapan los muros a medio subir, para que no los reviente el hielo; con nieve encima, si nieva. */
    private fun DrawScope.paja(l: Luces, x0: Float, x1: Float, cima: (Float) -> Float, nieve: Boolean, az: Azar) {
        var x = x0 - 1f
        while (x < x1 + 1f) { val w = az.entre(3.6f, 6f); val y = cima(x.coerceIn(x0, x1))
            drawOval(l.tono(if (az.real() > 0.5f) lerp(PAJA, PAJA_VIEJA, 0.45f) else PAJA_VIEJA, 0.05f), Offset(x - 0.6f, y - 5f), Size(w + 1.2f, 6.8f))
            for (k in 0..2) drawLine(l.tono(lerp(PAJA, PAJA_VIEJA, 0.3f), 0.05f).copy(alpha = 0.85f), Offset(x + w * k / 3f, y - 3f), Offset(x + w * k / 3f + az.entre(-0.8f, 0.8f), y + az.entre(2f, 5f)), 0.6f)
            if (nieve) drawOval(l.tono(NIEVE, 0.05f), Offset(x - 0.6f, y - 6f), Size(w + 1.2f, 3.4f))
            x += w * 0.8f }
    }

    /** La grúa de rueda, sobre el paño del centro: la rueda de pisar, el mástil y la pluma. Con la obra tapada, recogida. */
    private fun DrawScope.grua(m: Momento, l: Luces, r: Rayo, y: Float) {
        val osc = l.tono(ROBLE_OSC, 0.05f); val mad = l.tono(ROBLE, 0.05f); val luz = l.tono(lerp(ROBLE, CALIDA, 0.4f), 0.05f)
        val tapada = obra(m) == Obra.TAPADA
        val eje = Offset(131f, y - 10.5f); val pie = Offset(120f, y - 1f)
        drawLine(mad, Offset(112f, y - 1f), Offset(142f, y - 0.6f), 1.8f); drawLine(luz, Offset(112f, y - 1.8f), Offset(142f, y - 1.4f), 0.5f)
        drawLine(osc, Offset(124f, y - 1f), eje, 1.1f); drawLine(osc, Offset(138f, y - 1f), eje, 1.1f)
        drawCircle(l.tono(lerp(ROBLE_OSC, Color(0xFF2A2420), 0.5f), 0.05f).copy(alpha = 0.5f), 8f, eje)
        drawCircle(mad, 8.6f, eje, style = Stroke(1.7f)); drawCircle(luz, 9.3f, eje, style = Stroke(0.4f))
        for (k in 0..3) { val a = k * PI.toFloat() / 4f + 0.2f; drawLine(osc, eje - Offset(cos(a), sin(a)) * 8.4f, eje + Offset(cos(a), sin(a)) * 8.4f, 0.7f) }
        drawCircle(osc, 1.4f, eje)
        val cabeza = pie + Offset(0f, -27f)
        val punta = if (tapada) pie + Offset(-15f, -13f) else pie + Offset(-21f, -36f)
        drawLine(osc, pie, cabeza, 1.7f); drawLine(mad, pie + Offset(0f, -4f), punta, 1.5f); drawLine(luz, pie + Offset(0.4f, -4.7f), punta + Offset(0f, -0.6f), 0.4f)
        drawLine(osc.copy(alpha = 0.8f), cabeza, punta, 0.45f); drawLine(osc.copy(alpha = 0.8f), cabeza, Offset(140f, y - 1.6f), 0.45f)
        if (tapada) return
        // La soga: del torno a la cabeza de la pluma y de ahí a la piedra, o al gancho vacío.
        drawLine(osc.copy(alpha = 0.8f), eje, punta, 0.45f)
        val cargada = gruaCargada(m)
        val cabo = punta + Offset(0f, if (cargada) 26f else 9f)
        drawLine(osc, punta, cabo, 0.5f)
        if (cargada) {
            if (r.fuerza > 0.05f) drawRect(r.color.copy(alpha = 0.5f * r.fuerza), cabo + Offset(-4.4f - r.lat * 4f, 3f), Size(8.8f, 5.8f), blendMode = androidx.compose.ui.graphics.BlendMode.Multiply)
            drawLine(osc, cabo, cabo + Offset(-3f, 2.6f), 0.5f); drawLine(osc, cabo, cabo + Offset(3f, 2.6f), 0.5f)
            drawRect(cara(l, r, CALIZA, haciaDe(r, 0f) + 0.1f, 0.03f), cabo + Offset(-4.4f, 2.4f), Size(8.8f, 5.8f)); drawRect(cara(l, r, CALIZA, haciaDe(r, 1f), 0.03f), cabo + Offset(2.4f, 2.4f), Size(2f, 5.8f))
            drawLine(cara(l, r, CALIZA, 0.9f * r.dia, 0.03f), cabo + Offset(-4.4f, 2.4f), cabo + Offset(4.4f, 2.4f), 0.6f)
        } else drawArc(l.tono(HIERRO, 0.05f), 0f, 200f, false, cabo + Offset(-1.2f, 0f), Size(2.4f, 2.6f), style = Stroke(0.6f))
    }

    private val CEDIDO = listOf(Offset(134f, 214f) to Offset(164f, 168f), Offset(182f, 210f) to Offset(154f, 176f), Offset(146f, 216f) to Offset(180f, 190f), Offset(126f, 212f) to Offset(150f, 200f))

    /** El andamio que cedió: los pies derechos vencidos contra el muro, un tablón colgando y el montón de tablas al pie. */
    private fun DrawScope.andamioCedido(l: Luces, r: Rayo) {
        val osc = l.tono(ROBLE_OSC, 0.04f); val mad = l.tono(ROBLE, 0.04f); val luz = l.tono(lerp(ROBLE, CALIDA, 0.45f), 0.04f)
        val palos = CEDIDO
        for ((a, b) in palos) { drawLine(osc, a, b, 1.5f, StrokeCap.Round); drawLine(mad, a + Offset(-50f, -0.5f), b + Offset(-50f, -0.5f), 0.6f) }
        // El tablón que quedó colgando de un cabo, y la soga suelta.
        drawPath(poligono(Offset(146f, 184f), Offset(174f, 199f), Offset(173f, 201.4f), Offset(145f, 186.4f)), luz); drawLine(osc, Offset(145f, 186.4f), Offset(173f, 201.4f), 0.5f)
        drawPath(Path().apply { moveTo(214f, 168f); cubicTo(216f, 180f, 208f, 186f, 212f, 198f) }, osc, style = Stroke(0.6f))
        // El montón al pie: tablas, un zarzo roto y el cubo volcado.
        val az = Azar(Semilla.de("gotico", "caido"))
        repeat(9) { val p = Offset(az.entre(130f, 172f), az.entre(210f, 217f)); val a = az.entre(-0.5f, 0.5f); val largo = az.entre(9f, 17f)
            drawLine(if (it % 3 == 0) luz else mad, p, p + Offset(cos(a), sin(a)) * largo, 1.5f); drawLine(osc, p + Offset(-50f, 0.8f), p + Offset(cos(a), sin(a)) * largo + Offset(-50f, 0.8f), 0.4f) }
        drawOval(osc, Offset(174f, 213f), Size(6f, 4.4f)); drawOval(l.tono(Color(0xFF2A2420)), Offset(175f, 213.8f), Size(3.4f, 2.8f))
    }

    // ---------------------------------------------------------------- El patio de la logia

    private fun DrawScope.patio(m: Momento, l: Luces, r: Rayo, est: Estacion) {
        val az = Azar(Semilla.de("gotico", "patio"))
        suelo(m, l, r, est, az)
        pila(m, l, r, az)
        zarzo(m, l, r, az)
        // Lo que cuelga del medio muro o se apoya en él.
        colgado(m, l, r)
        val cosas = cosasDelPatio(m, l, r).sortedBy { it.pie.y }

        // Las sombras que caen sobre la pila, el medio muro y el suelo: la del alero de la logia y la de cada cosa.
        capaSombra(r) { c ->
            sombraDelAlero(r, c, enCosas = false)
            drawRect(c, Offset(ZARZO_X, ZARZO + 2.4f), Size(ANCHO - ZARZO_X, (2.2f * r.tan * 0.5f).coerceIn(0.7f, 3.4f)))          // bajo el larguero
            cosas.forEach { sombraArrojada(r, it.forma, it.pie, c, BASE, PILA, 0f, PILA_X); clipRect(ZARZO_X, 0f, ANCHO, ALTO) { sombraArrojada(r, it.forma, it.pie, c, BASE, ZARZO, ZARZO_X, ANCHO) } }
        }
        cosas.forEach { it.pintar(this) }
        // El alero también da sombra sobre las cosas.
        // Y cada cosa da sombra sobre las que tiene detrás.
        capaSombra(r, 0.8f) { c ->
            cosas.forEachIndexed { i, k -> clipPath(k.forma) {
                if (k.bajoElFollaje) sombraDelAlero(r, c, enCosas = true)
                if (r.ay < -0.02f) for (j in i + 1 until cosas.size) { val sep = cosas[j].pie.y - k.pie.y; if (sep > 0.5f) translate(r.ax * sep / -r.ay, sep / -r.ay) { sombra(cosas[j].forma, c) } }
            } }
        }
        if (farol(l)) { resplandor(FAROL, 80f, 0.42f * l.noche); resplandor(FAROL + Offset(-14f, 76f), 70f, 0.3f * l.noche) }
    }

    /**
     * La sombra del alero de la logia: por la mañana, con el sol a la derecha, deja en sombra medio patio; a mediodía,
     * solo una franja junto al poste; por la tarde cae hacia fuera del cuadro. El borde es el fleco de la paja.
     */
    private fun DrawScope.sombraDelAlero(r: Rayo, c: Color, enCosas: Boolean) {
        if (r.ax >= 0f) return
        val e = 330f + r.ax * 34f
        val az = Azar(Semilla.de("gotico", "sombra del alero"))
        clipRect(0f, if (enCosas) 130f else ZARZO, ANCHO, ALTO) {
            drawPath(poligono(Offset(e, 130f), Offset(e + 4f, BASE), Offset(e + 30f, ALTO + 4f), Offset(ANCHO + 4f, ALTO + 4f), Offset(ANCHO + 4f, 130f)), c)
            // El fleco: mechones que se salen del borde.
            var y = 132f
            while (y < ALTO + 4f) { val x = if (y < BASE) e + 4f * (y - 130f) / (BASE - 130f) else e + 4f + 26f * (y - BASE) / (ALTO + 4f - BASE); val w = az.entre(2f, 7f)
                drawPath(poligono(Offset(x + 1f, y - 2.4f), Offset(x - w, y + az.entre(-0.6f, 1.6f)), Offset(x + 1f, y + 2.4f)), c); y += az.entre(2.4f, 5f) }
        }
    }

    /** El suelo del patio: tierra apisonada, blanca de polvo de piedra, con lascas, rodadas, unos tablones y, si llueve, charcos. */
    private fun DrawScope.suelo(m: Momento, l: Luces, r: Rayo, est: Estacion, az: Azar) {
        val nieve = nevado(m)
        val tierra = if (nieve) lerp(TIERRA, NIEVE, 0.72f) else lerp(TIERRA, Color(0xFF8A6E48), 0.5f)
        drawRect(Brush.verticalGradient(listOf(l.tono(lerp(tierra, CALIZA, 0.3f)).copy(alpha = 0f), l.tono(lerp(tierra, CALIZA, 0.18f)), l.tono(lerp(tierra, ROBLE_OSC, 0.2f))), BASE - 16f, ALTO), Offset(0f, BASE - 16f), Size(ANCHO, ALTO - BASE + 16f))
        // El polvo blanco de la talla, al pie de su piedra.
        if (!nieve) { mancha(Offset(196f, 281f), 46f, 6f, l.tono(CALIZA), 0.5f); mancha(Offset(190f, 278f), 26f, 3.6f, l.tono(Color(0xFFF6F0E0)), 0.5f) }
        // Lo pisado y lo sin pisar: manchas largas, más oscuras donde se camina.
        repeat(26) { mancha(Offset(az.entre(0f, ANCHO), az.entre(BASE, ALTO)), az.entre(10f, 34f), az.entre(1.4f, 4f), l.tono(if (it % 3 == 0) CALIZA else if (it % 3 == 1) lerp(TIERRA, ROBLE_OSC, 0.3f) else TIERRA), 0.2f) }
        // Las rodadas de la carretilla, que vienen de la obra.
        for (k in 0..1) drawPath(Path().apply { moveTo(184f + k * 14f, BASE - 1f); cubicTo(196f + k * 16f, 258f, 232f + k * 20f, 270f, 300f + k * 30f, ALTO + 2f) }, l.tono(lerp(TIERRA, ROBLE_OSC, 0.5f)).copy(alpha = 0.42f), style = Stroke(2.2f, cap = StrokeCap.Round))
        // Lascas de talla por todo el patio; muchas, y frescas, al pie de su piedra.
        repeat(70) { val p = Offset(az.entre(4f, ANCHO - 4f), az.entre(BASE + 2f, ALTO - 1f)); val w = az.entre(0.8f, 2.4f)
            drawPath(poligono(p, p + Offset(w, -w * 0.5f), p + Offset(w * 1.4f, w * 0.3f)), l.tono(if (it % 4 == 0) CALIZA_OSC else CALIZA).copy(alpha = 0.8f)) }
        // Hierba al pie de los muros y entre las piedras; en invierno, seca.
        if (!nieve) { val hierba = lerp(hierbaDe(est), Color(0xFF55703A), 0.4f)
            repeat(46) { val x = az.entre(0f, ANCHO); if (x in PILA_X + 6f..ZARZO_X - 6f) return@repeat
                val p = Offset(x, BASE + az.entre(-0.6f, 2f)); drawLine(l.tono(hierba).copy(alpha = 0.85f), p, p + Offset(az.entre(-1.6f, 1.6f), -az.entre(2f, 5.4f)), 0.6f) } }
        else repeat(8) { mancha(Offset(az.entre(0f, ANCHO), az.entre(BASE, ALTO)), az.entre(14f, 30f), az.entre(2f, 5f), l.tono(NIEVE), 0.6f) }
        if (!nieve) { val hierba = lerp(hierbaDe(est), Color(0xFF55703A), 0.3f)
            for ((c, rx) in listOf(Offset(126f, 243f) to 12f, Offset(226f, 245f) to 9f, Offset(8f, 250f) to 14f, Offset(372f, 251f) to 10f)) { mancha(c, rx, 2.2f, l.tono(hierba), 0.75f)
                repeat(9) { val p = c + Offset(az.entre(-rx, rx), az.entre(-1f, 1.4f)); drawLine(l.tono(lerp(hierba, CALIDA, 0.2f)), p, p + Offset(az.entre(-1.4f, 1.4f), -az.entre(2.4f, 5.6f)), 0.6f) } } }
        // Los charcos, con la lluvia: recogen el color del cielo.
        if (m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA || crecida(m) > 0.3f) for ((p, w) in listOf(Offset(66f, 268f) to 30f, Offset(226f, 281f) to 38f, Offset(310f, 262f) to 22f)) {
            drawOval(l.tono(lerp(TIERRA, ROBLE_OSC, 0.5f)).copy(alpha = 0.6f), p + Offset(-w / 2f - 1f, -2.6f), Size(w + 2f, 5.2f)); drawOval(lerp(l.medio, l.bajo, 0.5f).copy(alpha = 0.62f), p + Offset(-w / 2f, -2f), Size(w, 4f))
            drawLine(Color.White.copy(alpha = 0.3f * (1f - l.noche)), p + Offset(-w * 0.3f, -0.6f), p + Offset(w * 0.1f, -0.6f), 0.5f) }
    }

    /** La marca de un cantero, abierta en la cara de un sillar: la de Pierre (una flecha cruzada por dos rayas) u otra. */
    private fun DrawScope.marca(c: Offset, k: Float, cual: Int, color: Color, g: Float = 0.6f) {
        fun raya(a: Offset, b: Offset) = drawLine(color, c + a * k, c + b * k, g, StrokeCap.Round)
        when (cual % 4) {
            0 -> { raya(Offset(0f, 3f), Offset(0f, -3f)); raya(Offset(-1.6f, -1.2f), Offset(0f, -3f)); raya(Offset(1.6f, -1.2f), Offset(0f, -3f)); raya(Offset(-1.5f, 0.6f), Offset(1.5f, 0.6f)); raya(Offset(-1.5f, 1.9f), Offset(1.5f, 1.9f)) }
            1 -> { raya(Offset(-2f, 2f), Offset(0f, -2.4f)); raya(Offset(2f, 2f), Offset(0f, -2.4f)); raya(Offset(-2f, 2f), Offset(2f, 2f)) }
            2 -> { raya(Offset(-2f, -2f), Offset(2f, 2f)); raya(Offset(2f, -2f), Offset(-2f, 2f)); raya(Offset(-2f, 2f), Offset(2f, 2f)) }
            else -> { raya(Offset(0f, -2.6f), Offset(0f, 2.6f)); raya(Offset(-2f, -0.6f), Offset(2f, -0.6f)); raya(Offset(0f, 2.6f), Offset(2f, 1.2f)) }
        }
    }

    /** Un sillar labrado, visto un poco desde arriba: la cara, el lecho de arriba, las huellas del cincel dentado y lo que el tiempo le ha dejado. */
    private fun DrawScope.sillar(l: Luces, r: Rayo, x: Float, y: Float, w: Float, h: Float, az: Azar, arriba: Boolean, nieve: Boolean, marcaDe: Int = -1) {
        val propio = lerp(CALIZA, if (az.real() > 0.5f) Color(0xFFCDBB92) else Color(0xFFF0E8D4), az.real() * 0.75f)
        val bloque = caja(x, y, x + w, y + h)
        drawPath(bloque, Brush.horizontalGradient(listOf(cara(l, r, propio, frente(r) + 0.12f * r.lat), cara(l, r, propio, frente(r) - 0.12f * r.lat)), x, x + w))
        clipPath(bloque) {
            // Las rayas finas del cincel dentado, todas iguales.
            val inc = if (az.real() > 0.5f) 0.5f else -0.5f; var t = x - h; while (t < x + w + h) { drawLine(l.tono(CALIZA_OSC).copy(alpha = 0.17f), Offset(t, y), Offset(t + inc * h, y + h), 0.3f); t += 1.5f }
            repeat(3) { mancha(Offset(az.entre(x, x + w), az.entre(y, y + h)), az.entre(2f, 6f), az.entre(1.4f, 4f), l.tono(if (it == 0) CALIDA else CALIZA_OSC), 0.2f) }
            if (az.real() > 0.6f) drawPath(poligono(Offset(x + w, y + h), Offset(x + w - az.entre(2f, 5f), y + h), Offset(x + w, y + h - az.entre(2f, 4f))), l.tono(mult(propio, r.color, 0.9f)))      // una esquina saltada
        }
        drawLine(l.tono(mult(propio, r.color, 1f)).copy(alpha = 0.9f), Offset(x, y + h + 0.1f), Offset(x + w, y + h + 0.1f), 1f); drawLine(l.tono(Color(0xFF4E4A44)).copy(alpha = 0.5f), Offset(x + w + 0.3f, y), Offset(x + w + 0.3f, y + h), 0.6f)
        drawLine(l.tono(mult(propio, r.color, 0.95f)).copy(alpha = 0.6f), Offset(if (r.lat >= 0f) x + 0.2f else x + w - 0.2f, y), Offset(if (r.lat >= 0f) x + 0.2f else x + w - 0.2f, y + h), 0.6f)
        if (arriba) { drawPath(poligono(Offset(x, y), Offset(x + 2.6f, y - 3.4f), Offset(x + w + 2.6f, y - 3.4f), Offset(x + w, y)), if (nieve) l.tono(NIEVE) else cara(l, r, propio, 0.85f * r.dia + 0.1f))
            drawLine(l.tono(lerp(propio, Color.White, 0.5f)).copy(alpha = 0.7f * r.dia), Offset(x, y), Offset(x + w, y), 0.5f) }
        if (marcaDe >= 0) marca(Offset(x + w * 0.5f + az.entre(-w * 0.2f, w * 0.2f), y + h * 0.5f), h / 11f, marcaDe, l.tono(mult(propio, r.color, 1f)).copy(alpha = 0.85f), 0.55f)
    }

    /** La pila de sillares ya labrados, que esperan a subir: tres hiladas a la izquierda, dos después, y uno atravesado encima. */
    private fun DrawScope.pila(m: Momento, l: Luces, r: Rayo, az: Azar) {
        val nieve = nevado(m); val h = (BASE - PILA) / 3f
        // La hilada de abajo, la de en medio y la de arriba: cada una acaba antes que la de debajo.
        val hiladas = listOf(Triple(BASE - h, PILA_X, listOf(26f, 31f, 24f, 29f, 27f)), Triple(BASE - 2f * h, 96f, listOf(18f, 30f, 27f, 25f)), Triple(PILA, 66f, listOf(29f, 24f, 28f)))
        for ((n, hilada) in hiladas.withIndex()) {
            val (y, fin, anchos) = hilada
            var x = -6f - n * 4f
            for ((k, w) in anchos.withIndex()) {
                val w2 = minOf(w, fin - x); if (w2 < 6f) break
                val encima = n == 2 || x + w2 > (hiladas.getOrNull(n + 1)?.second ?: 0f) - 1f
                sillar(l, r, x, y, w2 - 0.7f, h - 0.6f, Azar(Semilla.de("gotico", "sillar", n, k)), encima, nieve, if ((n + k) % 3 == 1) n + k else -1)
                x += w2
            }
        }
        // Uno más, atravesado encima, y las cuñas de madera con que se calzan.
        sillar(l, r, 22f, PILA - h + 1.4f, 26f, h - 2f, Azar(Semilla.de("gotico", "sillar", 9)), true, nieve, 0)
        for (x in listOf(20f, 44f)) drawPath(poligono(Offset(x, PILA - 0.4f), Offset(x + 5f, PILA - 0.4f), Offset(x + 5f, PILA - 2f)), l.tono(ROBLE))
        // Su saya de paño azul, que se quita para tallar y deja sobre la pila.
        if (tallando(m)) {
            val c = Offset(84f, PILA + h + 0.6f)
            val tela = Path().apply { moveTo(c + Offset(-10f, -1.6f)); cubicTo(c + Offset(-4f, -4f), c + Offset(4f, -4f), c + Offset(10f, -1.4f)); cubicTo(c + Offset(11f, 6f), c + Offset(9f, 13f), c + Offset(10.6f, 19f)); cubicTo(c + Offset(6f, 17f), c + Offset(3f, 21f), c + Offset(-1f, 18.6f)); cubicTo(c + Offset(-5f, 21f), c + Offset(-8f, 17f), c + Offset(-10.6f, 18f)); cubicTo(c + Offset(-9f, 12f), c + Offset(-11f, 6f), c + Offset(-10f, -1.6f)); close() }
            drawPath(Path().apply { addPath(tela, Offset(-r.lat * 2f, 1.4f)) }, l.tono(mult(CALIZA, r.color, 1f)).copy(alpha = 0.6f * r.dia))
            drawPath(tela, Brush.horizontalGradient(listOf(cara(l, r, AZUL, 0.5f * r.lat + 0.1f), cara(l, r, AZUL, -0.5f * r.lat)), c.x - 11f, c.x + 11f))
            for (k in -2..2) drawPath(Path().apply { moveTo(c + Offset(k * 4f, -1f)); cubicTo(c + Offset(k * 4.4f, 6f), c + Offset(k * 3.6f, 12f), c + Offset(k * 4.4f, 18f)) }, l.tono(mult(AZUL, r.color, 0.9f)).copy(alpha = 0.55f), style = Stroke(0.6f))
            drawPath(poligono(c + Offset(-10f, -1.6f), c + Offset(10f, -1.4f), c + Offset(12f, -4.6f), c + Offset(-8f, -4.8f)), cara(l, r, AZUL, 0.8f * r.dia))
        }
        // El verdín de abajo, donde no da el sol.
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFF7C8660)).copy(alpha = 0.3f)), BASE - 9f, BASE), Offset(0f, BASE - 9f), Size(PILA_X, 9f))
    }

    /** El medio muro de la logia: zarzo de varas de avellano tejidas entre estacas, con unos restos de barro, entre pies derechos y bajo su larguero. */
    private fun DrawScope.zarzo(m: Momento, l: Luces, r: Rayo, az: Azar) {
        val muro = caja(ZARZO_X, ZARZO + 2f, ANCHO, BASE)
        val vara = lerp(ROBLE, PAJA, 0.62f)
        drawPath(muro, Brush.verticalGradient(listOf(cara(l, r, lerp(vara, ROBLE_OSC, 0.12f), frente(r)), cara(l, r, lerp(vara, ROBLE_OSC, 0.3f), frente(r) - 0.1f)), ZARZO, BASE))
        clipPath(muro) {
            // El tejido: cada vara pasa por delante de una estaca y por detrás de la siguiente.
            val luz = cara(l, r, lerp(vara, CALIDA, 0.3f), frente(r) + 0.35f); val sombra = l.tono(mult(vara, r.color, 0.9f)).copy(alpha = 0.6f); val paso = 6.4f
            var y = ZARZO + 3.4f; var n = 0
            while (y < BASE) { var x = ZARZO_X - (if (n % 2 == 0) 0f else paso); var k = 0
                while (x < ANCHO) { val comba = az.entre(0.5f, 1.1f)
                    drawPath(Path().apply { moveTo(x, y + 0.9f); cubicTo(x + paso * 0.6f, y + 0.9f + comba, x + paso * 1.4f, y + 0.9f + comba, x + paso * 2f, y + 0.9f) }, sombra, style = Stroke(1.1f))
                    drawPath(Path().apply { moveTo(x, y); cubicTo(x + paso * 0.6f, y - comba, x + paso * 1.4f, y - comba, x + paso * 2f, y) }, if ((k + n) % 5 == 0) l.tono(lerp(vara, ROBLE_OSC, 0.3f)) else luz, style = Stroke(0.9f, cap = StrokeCap.Round))
                    x += paso * 2f; k++ }
                y += 2.3f; n++ }
            var x = ZARZO_X + paso; while (x < ANCHO) { drawLine(l.tono(ROBLE_OSC).copy(alpha = 0.32f), Offset(x, ZARZO + 2f), Offset(x, BASE), 0.6f); x += paso }
            // Lo que queda del barro con que lo embarraron, a trozos, y lo que el tiempo ha puesto.
            for ((c, rx, ry) in listOf(Triple(Offset(300f, 238f), 9f, 5f), Triple(Offset(248f, 214f), 8f, 5f))) {
                val dz = Azar(Semilla.de("gotico", "barro", c.x.toInt())); val pts = (0 until 10).map { val a = it * 2f * PI.toFloat() / 10f; c + Offset(cos(a) * rx * dz.entre(0.65f, 1.1f), sin(a) * ry * dz.entre(0.65f, 1.1f)) }
                val costra = curva(pts + pts.first())
                drawPath(Path().apply { addPath(costra, Offset(-r.lat * 0.9f, 1f)) }, l.tono(mult(vara, r.color, 1f)).copy(alpha = 0.6f)); drawPath(costra, cara(l, r, lerp(ENLUCIDO, TIERRA, 0.35f), frente(r) + 0.1f))
                clipPath(costra) { repeat(4) { mancha(c + Offset(dz.entre(-rx, rx), dz.entre(-ry, ry)), dz.entre(2f, 6f), dz.entre(1.4f, 4f), l.tono(if (it % 2 == 0) CALIDA else Color(0xFF8E8468)), 0.3f) }; drawLine(l.tono(Color(0xFF6E5E48)).copy(alpha = 0.6f), c + Offset(-rx * 0.5f, -ry * 0.4f), c + Offset(rx * 0.3f, ry * 0.5f), 0.4f) } }
            repeat(12) { mancha(Offset(az.entre(ZARZO_X, ANCHO), az.entre(ZARZO, BASE)), az.entre(6f, 18f), az.entre(3f, 9f), l.tono(if (it % 2 == 0) Color(0xFF3E3024) else Color(0xFFD8C090)), 0.14f) }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFF4E4630)).copy(alpha = 0.5f)), BASE - 10f, BASE), Offset(ZARZO_X, BASE - 10f), Size(ANCHO - ZARZO_X, 10f))
        }
        // Los pies derechos y el larguero, con su canto de arriba al sol.
        for (x in listOf(ZARZO_X, 284f)) { drawRect(cara(l, r, ROBLE, frente(r) + 0.1f), Offset(x, ZARZO), Size(5.6f, BASE - ZARZO)); drawRect(cara(l, r, ROBLE, lado(r, 1f)), Offset(x + 3.8f, ZARZO), Size(1.8f, BASE - ZARZO)); drawLine(l.tono(ROBLE_OSC).copy(alpha = 0.5f), Offset(x + 1.6f, ZARZO + 4f), Offset(x + 2f, BASE - 3f), 0.35f) }
        drawRect(cara(l, r, ROBLE, frente(r)), Offset(ZARZO_X - 2f, ZARZO), Size(ANCHO - ZARZO_X + 2f, 3.6f))
        drawPath(poligono(Offset(ZARZO_X - 2f, ZARZO), Offset(ZARZO_X + 1f, ZARZO - 2.8f), Offset(ANCHO, ZARZO - 2.8f), Offset(ANCHO, ZARZO)), if (nevado(m)) l.tono(NIEVE) else cara(l, r, ROBLE, 0.8f * r.dia + 0.1f))
        var x = ZARZO_X; while (x < ANCHO) { drawLine(l.tono(ROBLE_OSC).copy(alpha = 0.4f), Offset(x, ZARZO + 0.6f), Offset(x + az.entre(8f, 20f), ZARZO + az.entre(0.6f, 2.8f)), 0.3f); x += az.entre(10f, 22f) }
    }

    /** Lo que cuelga del medio muro o se apoya en él: los adornos, el paño del luto y, si son el objeto del día, la cuerda de nudos, la capucha, la plantilla o el compás. */
    private fun DrawScope.colgado(m: Momento, l: Luces, r: Rayo) {
        // La criba de la arena, colgada de su clavo más allá del poste.
        val cr = Offset(363f, 224f)
        drawOval(l.tono(Color(0xFF2E2620)).copy(alpha = 0.4f * r.dia), cr + Offset(-9.6f - r.lat * 2f, -8f), Size(19.2f, 19.2f))
        drawCircle(cara(l, r, lerp(ROBLE, PAJA, 0.5f), frente(r) + 0.2f), 9.4f, cr); drawCircle(l.tono(Color(0xFF6E5A40)), 7.4f, cr)
        clipPath(Path().apply { addOval(Rect(cr.x - 7.4f, cr.y - 7.4f, cr.x + 7.4f, cr.y + 7.4f)) }) { for (k in -5..5) { drawLine(l.tono(Color(0xFFD8C090)).copy(alpha = 0.7f), cr + Offset(k * 1.6f, -8f), cr + Offset(k * 1.6f, 8f), 0.3f); drawLine(l.tono(Color(0xFFD8C090)).copy(alpha = 0.7f), cr + Offset(-8f, k * 1.6f), cr + Offset(8f, k * 1.6f), 0.3f) } }
        drawCircle(l.tono(ROBLE_OSC), 9.4f, cr, style = Stroke(0.5f)); drawCircle(l.tono(HIERRO), 0.9f, cr + Offset(0f, -10.4f))
        if (fiesta(m)) { guirnalda(l, Offset(ZARZO_X + 4f, ZARZO + 4f), Offset(284f, ZARZO + 4f), 9f, m.azar("gz", 1), flores = listOf(ROJO, ORO, Color.White), grueso = 3f)
            guirnalda(l, Offset(288f, ZARZO + 4f), Offset(334f, ZARZO + 4f), 9f, m.azar("gz", 2), flores = listOf(ROJO, ORO, Color.White), grueso = 3f)
            guirnalda(l, Offset(4f, PILA + 2f), Offset(62f, PILA + 2f), 8f, m.azar("gz", 3), flores = listOf(ROJO, ORO, Color.White), grueso = 2.8f) }
        if (m.con("luto")) pano(l, Offset(346f, ZARZO - 1f), Offset(378f, ZARZO - 1f), 26f, Color(0xFF2A2630))
        if (procesion(m)) {
            // El estandarte de la cofradía, arrimado al medio muro mientras pasa la procesión.
            val pie = Offset(352f, 262f); val cabeza = Offset(349f, 168f)
            drawLine(l.tono(ROBLE_OSC), pie, cabeza, 1.6f); drawLine(l.tono(ORO), cabeza + Offset(-8f, 6f), cabeza + Offset(8f, 6f), 1.2f)
            val tela = Path().apply { moveTo(cabeza + Offset(-7f, 6.6f)); lineTo(cabeza + Offset(7f, 6.6f)); lineTo(cabeza + Offset(7.6f, 34f)); lineTo(cabeza + Offset(3.4f, 30f)); lineTo(cabeza + Offset(0f, 35f)); lineTo(cabeza + Offset(-3.4f, 30f)); lineTo(cabeza + Offset(-7.6f, 34f)); close() }
            drawPath(tela, Brush.horizontalGradient(listOf(cara(l, r, AZUL, 0.3f), cara(l, r, AZUL, -0.3f)), cabeza.x - 8f, cabeza.x + 8f))
            drawLine(l.tono(ORO), cabeza + Offset(0f, 12f), cabeza + Offset(0f, 26f), 1.2f); drawLine(l.tono(ORO), cabeza + Offset(-4f, 17f), cabeza + Offset(4f, 17f), 1.2f)
            drawCircle(l.tono(ORO), 1.7f, cabeza)
        }
        when (m.ficha.objeto) {
            "cuerda" -> {
                // La cuerda de nudos, colgada en dos senos de dos clavos: trece nudos a la misma distancia.
                val soga = Path().apply { moveTo(244f, ZARZO + 5f); cubicTo(248f, ZARZO + 34f, 266f, ZARZO + 34f, 270f, ZARZO + 5f); cubicTo(274f, ZARZO + 30f, 292f, ZARZO + 30f, 296f, ZARZO + 5f); cubicTo(297f, ZARZO + 16f, 296f, ZARZO + 26f, 298f, ZARZO + 36f) }
                translate(-r.lat * 2.4f, 1.6f) { drawPath(soga, l.tono(Color(0xFF2E2620)).copy(alpha = 0.4f * r.dia), style = Stroke(2.6f)) }; drawPath(soga, l.tono(Color(0xFF5E4630)), style = Stroke(2.9f, cap = StrokeCap.Round)); drawPath(soga, l.tono(Color(0xFFEADCB0)), style = Stroke(2f, cap = StrokeCap.Round)); drawPath(soga, l.tono(Color(0xFFFFF6DC)).copy(alpha = 0.7f), style = Stroke(0.5f))
                val medida = androidx.compose.ui.graphics.PathMeasure().apply { setPath(soga, false) }
                for (k in 0..12) { val p = medida.getPosition(medida.length * (0.03f + 0.94f * k / 12f)); drawCircle(l.tono(Color(0xFF5E4630)), 2.4f, p); drawCircle(l.tono(Color(0xFFD8C290)), 1.7f, p); drawCircle(l.tono(Color(0xFFFFF6DC)), 0.6f, p + Offset(-0.4f, -0.5f)) }
                for (x in listOf(244f, 270f, 296f)) drawCircle(l.tono(HIERRO), 0.9f, Offset(x, ZARZO + 5f))
            }
            "capucha" -> {
                // La capucha de paño azul, colgada del larguero, con su punta larga.
                val c = Offset(306f, ZARZO - 1f)
                val tela = Path().apply { moveTo(c + Offset(-9f, 0f)); cubicTo(c + Offset(-11f, 12f), c + Offset(-12f, 22f), c + Offset(-10f, 30f)); cubicTo(c + Offset(-4f, 33f), c + Offset(4f, 33f), c + Offset(10f, 29f)); cubicTo(c + Offset(11f, 20f), c + Offset(10f, 10f), c + Offset(9f, 0f)); close() }
                drawPath(Path().apply { addPath(tela, Offset(-r.lat * 2f, 1.4f)) }, l.tono(Color(0xFF2E2620)).copy(alpha = 0.4f * r.dia))
                drawPath(tela, Brush.horizontalGradient(listOf(cara(l, r, AZUL, 0.5f * r.lat), cara(l, r, AZUL, -0.5f * r.lat)), c.x - 10f, c.x + 10f))
                drawOval(l.tono(mult(AZUL, Color(0xFF30304A), 0.9f)), c + Offset(-5f, 8f), Size(10f, 13f))                                 // el hueco de la cara
                for (k in -1..1) drawPath(Path().apply { moveTo(c + Offset(k * 5f, 2f)); cubicTo(c + Offset(k * 6f, 12f), c + Offset(k * 7f, 20f), c + Offset(k * 6.4f, 29f)) }, l.tono(mult(AZUL, r.color, 0.8f)).copy(alpha = 0.6f), style = Stroke(0.5f))
                drawPath(Path().apply { moveTo(c + Offset(8f, 1f)); cubicTo(c + Offset(16f, 4f), c + Offset(15f, 20f), c + Offset(17f, 34f)) }, cara(l, r, AZUL, 0f), style = Stroke(2.4f, cap = StrokeCap.Round))
            }
            "plantilla" -> {
                // La plantilla de un perfil, de tabla fina de pino, apoyada en el medio muro: asoma por encima del larguero.
                val p = Offset(250f, 250f); val pino = Color(0xFFEAD6A2)
                val tabla = Path().apply { moveTo(p); lineTo(p + Offset(26f, 0f)); lineTo(p + Offset(31f, -54f)); lineTo(p + Offset(19f, -54f)); cubicTo(p + Offset(18f, -43f), p + Offset(8f, -46f), p + Offset(9.4f, -34f)); cubicTo(p + Offset(11f, -25f), p + Offset(2f, -26f), p + Offset(3.6f, -14f)); close() }
                drawPath(Path().apply { addPath(tabla, Offset(-r.lat * 3f, 1.2f)) }, l.tono(Color(0xFF2E2620)).copy(alpha = 0.4f * r.dia))
                drawPath(tabla, Brush.horizontalGradient(listOf(cara(l, r, pino, 0.4f * r.lat + 0.3f), cara(l, r, pino, -0.4f * r.lat + 0.1f)), p.x, p.x + 31f))
                clipPath(tabla) { for (k in 0..8) drawLine(l.tono(Color(0xFF9A7A48)).copy(alpha = 0.35f), p + Offset(2f + k * 3.4f, 0f), p + Offset(5f + k * 3.4f, -54f), 0.35f); drawOval(l.tono(Color(0xFF9A7A48)).copy(alpha = 0.6f), p + Offset(17f, -20f), Size(3f, 4.4f)) }
                drawPath(tabla, l.tono(Color(0xFF6E5230)), style = Stroke(0.7f)); drawCircle(l.tono(Color(0xFF2A2420)), 1.3f, p + Offset(25f, -49f))
                marca(p + Offset(20f, -9f), 1.3f, 0, l.tono(ROJO).copy(alpha = 0.8f), 0.6f)
            }
            "compas" -> {
                // El compás de la logia, de hierro y largo como un brazo, abierto y apoyado en el medio muro.
                val gozne = Offset(318f, 208f)
                for ((punta, k) in listOf(Offset(306f, 258f) to 0, Offset(327f, 259f) to 1)) {
                    drawLine(l.tono(Color(0xFF2E2620)).copy(alpha = 0.4f * r.dia), gozne + Offset(-r.lat * 2.4f, 1.6f), punta + Offset(-r.lat * 2.4f, 0f), 2f)
                    drawLine(l.tono(HIERRO), gozne, punta, 2.4f, StrokeCap.Round); drawLine(l.tono(lerp(HIERRO, Color(0xFFB8C0CC), 0.6f)).copy(alpha = 0.8f), gozne + Offset(-0.6f, 0f), punta + Offset(-0.5f, -3f), 0.6f)
                    drawPath(poligono(punta + Offset(-1.2f, -4f), punta + Offset(1.2f, -4f), punta + Offset(if (k == 0) -0.6f else 0.6f, 1.6f)), l.tono(HIERRO)) }
                drawArc(l.tono(HIERRO), 20f, 140f, false, Offset(308f, 222f), Size(20f, 12f), style = Stroke(1f))
                drawCircle(l.tono(HIERRO), 3f, gozne); drawCircle(l.tono(Color(0xFFB8C0CC)), 1f, gozne + Offset(-0.6f, -0.8f))
            }
            else -> Unit
        }
    }

    // ---------------------------------------------------------------- Las cosas del patio

    private fun cosasDelPatio(m: Momento, l: Luces, r: Rayo): List<Cosa> = buildList {
        add(agrandar(tonelCosa(m, l, r, Offset(32f, 270f)), 1.24f))
        add(agrandar(cuboCosa(l, r, Offset(62f, 279f)), 1.15f))
        add(agrandar(artesaCosa(m, l, r), 1.15f))
        add(agrandar(hogarCosa(m, l, r), K_HOGAR))
        add(agrandar(piedraCosa(m, l, r), 1.3f))
        addAll(cabriaCosas(m, l, r))
        add(bancoCosa(m, l, r))
        add(agrandar(cestoCosa(l, r, Offset(318f, 284f)), 1.25f))
        add(posteCosa(m, l, r))
    }

    /** La misma cosa, [k] veces más grande, sin mover de sitio su pie. */
    private fun agrandar(c: Cosa, k: Float): Cosa {
        val forma = Path().apply { addPath(c.forma); transform(Matrix().apply { values[0] = k; values[5] = k; values[12] = c.pie.x * (1f - k); values[13] = c.pie.y * (1f - k) }) }
        return Cosa(forma, c.pie, c.bajoElFollaje) { scale(k, k, c.pie) { c.pintar(this) } }
    }

    /** El tonel del agua, de duelas de roble con sus aros de mimbre, y el agua quieta arriba. */
    private fun tonelCosa(m: Momento, l: Luces, r: Rayo, pie: Offset): Cosa {
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx, pie.y + dy)
        val forma = Path().apply { moveTo(p(-13f, 0f)); cubicTo(p(-17.6f, -12f), p(-17.6f, -32f), p(-13.6f, -44f)); lineTo(p(13.6f, -44f)); cubicTo(p(17.6f, -32f), p(17.6f, -12f), p(13f, 0f)); close() }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.26f), p(-16f, -3f), Size(33f, 6.4f))
            volumen(l, r, forma, pie.x - 17f, pie.x + 17f, ROBLE)
            clipPath(forma) {
                for (k in -4..4) drawLine(l.tono(ROBLE_OSC).copy(alpha = 0.5f), p(k * 3.6f, 0f), p(k * 4.3f, -22f), 0.45f)
                for (k in -4..4) drawLine(l.tono(ROBLE_OSC).copy(alpha = 0.5f), p(k * 4.3f, -22f), p(k * 3.6f, -44f), 0.45f)
                val az = Azar(Semilla.de("gotico", "tonel")); repeat(7) { mancha(p(az.entre(-14f, 14f), az.entre(-40f, -3f)), az.entre(2f, 6f), az.entre(3f, 9f), l.tono(if (it % 2 == 0) Color(0xFF3E3024) else Color(0xFFC8A070)), 0.2f) }
                // Lo mojado, abajo.
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFF3A3026)).copy(alpha = 0.45f)), pie.y - 9f, pie.y), p(-18f, -9f), Size(36f, 9f))
                for (y in listOf(-37f, -33.6f, -11f, -7.6f)) { drawPath(Path().apply { moveTo(p(-18f, y)); cubicTo(p(-8f, y + 2.2f), p(8f, y + 2.2f), p(18f, y)) }, l.tono(Color(0xFF6E5A3A)), style = Stroke(1.9f)); drawPath(Path().apply { moveTo(p(-18f, y - 0.7f)); cubicTo(p(-8f, y + 1.5f), p(8f, y + 1.5f), p(18f, y - 0.7f)) }, l.tono(Color(0xFFD0B078)).copy(alpha = 0.6f), style = Stroke(0.4f)) }
            }
            drawOval(l.tono(ROBLE_OSC), p(-13.6f, -46.6f), Size(27.2f, 5.6f)); drawOval(if (nevado(m)) l.tono(NIEVE) else lerp(l.tono(Color(0xFF2E4048)), l.medio, 0.3f), p(-12f, -45.8f), Size(24f, 4f))
            drawLine(Color.White.copy(alpha = 0.35f * (1f - l.noche)), p(-7f, -44.4f), p(1f, -44.4f), 0.5f)
        }
    }

    /** El cubo de madera, con su asa de soga. */
    private fun cuboCosa(l: Luces, r: Rayo, pie: Offset): Cosa {
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx, pie.y + dy)
        val forma = Path().apply { moveTo(p(-7f, 0f)); lineTo(p(-9f, -17f)); lineTo(p(9f, -17f)); lineTo(p(7f, 0f)); close() }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.24f), p(-9f, -2.2f), Size(19f, 4.6f))
            volumen(l, r, forma, pie.x - 9f, pie.x + 9f, lerp(ROBLE, PAJA_VIEJA, 0.3f))
            clipPath(forma) { for (k in -2..2) drawLine(l.tono(ROBLE_OSC).copy(alpha = 0.5f), p(k * 2.9f, 0f), p(k * 3.7f, -17f), 0.4f)
                for (y in listOf(-13.6f, -4f)) drawLine(l.tono(HIERRO), p(-10f, y), p(10f, y), 1.2f) }
            drawOval(l.tono(ROBLE_OSC), p(-9f, -18.6f), Size(18f, 3.6f)); drawOval(l.tono(Color(0xFF2A2420)), p(-7.6f, -18f), Size(15.2f, 2.4f))
            drawPath(Path().apply { moveTo(p(-8.4f, -17f)); cubicTo(p(-7f, -30f), p(7f, -30f), p(8.4f, -17f)) }, l.tono(Color(0xFFB89A62)), style = Stroke(0.9f))
        }
    }

    /** La artesa del mortero, con la cal ya batida y la batidera apoyada en el borde. */
    private fun artesaCosa(m: Momento, l: Luces, r: Rayo): Cosa {
        val pie = Offset(88f, 279f)
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx, pie.y + dy)
        val forma = poligono(listOf(p(-19f, 0f), p(19f, 0f), p(24f, -10f), p(20f, -14.6f), p(-20f, -14.6f), p(-24f, -10f)))
        val mango = Path().apply { moveTo(p(13f, -9f)); lineTo(p(15.4f, -9.6f)); lineTo(p(34f, -52f)); lineTo(p(31.8f, -53f)); close() }
        return Cosa(Path().apply { addPath(forma); addPath(mango) }, pie) {
            drawOval(Color.Black.copy(alpha = 0.24f), p(-22f, -2.6f), Size(46f, 5.6f))
            drawPath(poligono(p(-19f, 0f), p(19f, 0f), p(24f, -10f), p(-24f, -10f)), Brush.horizontalGradient(listOf(cara(l, r, ROBLE, frente(r) + 0.15f * r.lat), cara(l, r, ROBLE, frente(r) - 0.15f * r.lat)), pie.x - 24f, pie.x + 24f))
            for (k in -3..3) drawLine(l.tono(ROBLE_OSC).copy(alpha = 0.4f), p(k * 5.6f, 0f), p(k * 7f, -10f), 0.4f)
            drawPath(poligono(p(-24f, -10f), p(24f, -10f), p(20f, -14.6f), p(-20f, -14.6f)), l.tono(ROBLE_OSC))
            drawPath(poligono(p(-22f, -10.6f), p(22f, -10.6f), p(18.6f, -14f), p(-18.6f, -14f)), if (nevado(m)) l.tono(NIEVE) else l.tono(Color(0xFFE8E4DA)))
            val az = Azar(Semilla.de("gotico", "mortero")); repeat(6) { mancha(p(az.entre(-16f, 16f), az.entre(-13.4f, -11f)), az.entre(2f, 5f), 0.9f, l.tono(Color(0xFFB8B4A8)), 0.5f) }
            // Los chorretones de cal por fuera.
            for (x in listOf(-14f, -3f, 9f)) drawLine(l.tono(Color(0xFFE8E4DA)).copy(alpha = 0.7f), p(x, -10f), p(x - 0.6f, -10f + az.entre(3f, 7f)), 1.1f, StrokeCap.Round)
            drawPath(mango, cara(l, r, lerp(ROBLE, PAJA_VIEJA, 0.4f), 0.4f * r.lat)); drawLine(l.tono(ROBLE_OSC).copy(alpha = 0.6f), p(15.4f, -9.6f), p(34f, -52f), 0.4f)
            drawPath(poligono(p(9f, -11f), p(17f, -9.6f), p(16f, -13f), p(10f, -13.6f)), l.tono(HIERRO))
        }
    }

    /** El hogar: un ruedo de piedras, el trípode de hierro y el caldero colgado de su cadena. */
    private fun hogarCosa(m: Momento, l: Luces, r: Rayo): Cosa {
        val pie = HOGAR
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx, pie.y + dy)
        val olla = Path().apply { moveTo(p(-8f, -17f)); cubicTo(p(-11.6f, -13f), p(-10f, -5.6f), p(-4f, -5f)); lineTo(p(4f, -5f)); cubicTo(p(10f, -5.6f), p(11.6f, -13f), p(8f, -17f)); close() }
        val forma = Path().apply { addPath(olla); addOval(Rect(pie.x - 14f, pie.y - 5f, pie.x + 14f, pie.y + 2f))
            addPath(poligono(p(-13.6f, 0f), p(-12f, 0f), p(0.6f, -31f), p(-0.6f, -31f))); addPath(poligono(p(13.6f, 0f), p(12f, 0f), p(-0.6f, -31f), p(0.6f, -31f))) }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.3f), p(-15f, -3.6f), Size(30f, 7.4f))
            // La ceniza y las brasas apagadas, dentro del ruedo.
            drawOval(l.tono(Color(0xFF5A544E)), p(-11f, -3.4f), Size(22f, 5.4f)); drawOval(l.tono(Color(0xFF2E2A28)), p(-7f, -2.6f), Size(14f, 3.4f))
            // La pata de detrás, la cadena y el caldero.
            drawLine(l.tono(HIERRO), p(3f, -4f), p(0f, -31f), 1f)
            drawLine(l.tono(HIERRO), p(0f, -31f), p(0f, -19f), 0.6f)
            volumen(l, r, olla, pie.x - 11f, pie.x + 11f, Color(0xFFA8623A))
            clipPath(olla) { mancha(p(0f, -6f), 9f, 3.4f, l.tono(Color(0xFF2A2220)), 0.75f); mancha(p(-3f, -12f), 3f, 2f, l.tono(Color(0xFF5E8A78)), 0.3f) }
            drawOval(l.tono(Color(0xFF7A4428)), p(-8.4f, -18.8f), Size(16.8f, 3.6f)); drawOval(l.tono(if (comiendo(m)) Color(0xFFB89A62) else Color(0xFF1E1A1A)), p(-7f, -18.2f), Size(14f, 2.4f))
            drawPath(Path().apply { moveTo(p(-8f, -17f)); cubicTo(p(-6f, -25f), p(6f, -25f), p(8f, -17f)) }, l.tono(HIERRO), style = Stroke(0.7f))
            // Las dos patas de delante y el nudo del trípode.
            drawLine(l.tono(HIERRO), p(-12.8f, 0f), p(0f, -31f), 1.2f, StrokeCap.Round); drawLine(l.tono(HIERRO), p(12.8f, 0f), p(0f, -31f), 1.2f, StrokeCap.Round)
            drawLine(l.tono(lerp(HIERRO, Color(0xFFB8C0CC), 0.5f)).copy(alpha = 0.7f * r.dia), p(if (r.lat >= 0f) 12.4f else -12.4f, -1f), p(0f, -30.6f), 0.4f)
            // El ruedo de piedras, tiznadas por dentro.
            for (k in 0..6) { val a = PI.toFloat() * (0.04f + k * 0.153f); val c = p(-cos(a) * 12.6f, sin(a) * 2.6f + 0.4f); val rad = 2.6f + (k % 3) * 0.5f
                drawOval(cara(l, r, lerp(CALIZA_OSC, Color(0xFF5A5450), 0.4f), if ((c.x - pie.x) * r.lat > 0f) 0.5f else -0.5f), c + Offset(-rad, -rad * 0.75f), Size(rad * 2f, rad * 1.5f))
                drawArc(l.tono(Color(0xFF2A2624)).copy(alpha = 0.6f), 180f, 180f, false, c + Offset(-rad, -rad * 0.75f), Size(rad * 2f, rad * 1.5f), style = Stroke(0.5f)) }
        }
    }

    /**
     * La piedra que talla, sobre su asiento de madera: un capitel de hojas a medio sacar. Encima, el mazo y el cincel
     * mientras trabaja; tapada con un saco mientras está herido. Si la marca es el objeto del día, recién abierta.
     */
    private fun piedraCosa(m: Momento, l: Luces, r: Rayo): Cosa {
        val pie = Offset(192f, 279f)
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx, pie.y + dy)
        val bloque = Path().apply { moveTo(p(-17f, -9f)); lineTo(p(-17f, -24f)); cubicTo(p(-17f, -30f), p(-23f, -32f), p(-23f, -38f)); lineTo(p(-23f, -43f)); lineTo(p(23f, -43f)); lineTo(p(23f, -38f)); cubicTo(p(23f, -32f), p(17f, -30f), p(17f, -24f)); lineTo(p(17f, -9f)); close() }
        val asiento = poligono(listOf(p(-22f, 0f), p(22f, 0f), p(22f, -9f), p(-22f, -9f)))
        val saco = andamioCaido(m)
        return Cosa(Path().apply { addPath(bloque); addPath(asiento) }, pie) {
            drawOval(Color.Black.copy(alpha = 0.28f), p(-24f, -3f), Size(49f, 6.6f))
            // El asiento: dos durmientes de roble, con sus cabezas.
            drawPath(asiento, Brush.horizontalGradient(listOf(cara(l, r, ROBLE, frente(r) + 0.12f * r.lat), cara(l, r, ROBLE, frente(r) - 0.12f * r.lat)), pie.x - 22f, pie.x + 22f))
            drawLine(l.tono(ROBLE_OSC), p(-22f, -4.6f), p(22f, -4.6f), 0.6f); for (x in listOf(-16f, -5f, 7f, 17f)) { drawCircle(l.tono(lerp(ROBLE, CALIDA, 0.3f)), 1.9f, p(x, -6.9f)); drawCircle(l.tono(ROBLE_OSC), 1.9f, p(x, -6.9f), style = Stroke(0.35f)); drawCircle(l.tono(ROBLE_OSC), 1.7f, p(x + 2f, -2.3f), style = Stroke(0.35f)) }
            drawPath(poligono(p(-22f, -9f), p(22f, -9f), p(19f, -11f), p(-19f, -11f)), cara(l, r, ROBLE, 0.7f * r.dia))
            // El capitel: la cesta, el ábaco de arriba y las hojas ya sacadas de un lado; del otro, la piedra en bruto.
            volumen(l, r, bloque, pie.x - 23f, pie.x + 23f, CALIZA)
            clipPath(bloque) {
                val az = Azar(Semilla.de("gotico", "capitel"))
                var t = pie.x - 40f; while (t < pie.x + 30f) { drawLine(l.tono(CALIZA_OSC).copy(alpha = 0.16f), Offset(t, pie.y - 43f), Offset(t + 12f, pie.y - 9f), 0.3f); t += 1.5f }
                // El lado en bruto: los golpes del puntero.
                repeat(40) { val q = p(az.entre(3f, 22f), az.entre(-37f, -10f)); drawLine(l.tono(mult(CALIZA, r.color, 0.9f)).copy(alpha = 0.5f), q, q + Offset(1.6f, 0.9f), 0.5f) }
                // Las hojas de hiedra, en relieve: su luz arriba y su sombra debajo.
                for ((c, giro) in listOf(p(-11f, -23f) to -20f, p(-2f, -29f) to 12f, p(-12f, -33f) to -40f)) rotate(giro, c) {
                    val hoja = Path().apply { moveTo(c + Offset(0f, 5.4f)); cubicTo(c + Offset(-5f, 4f), c + Offset(-6.4f, -1f), c + Offset(-3.6f, -3f)); cubicTo(c + Offset(-3f, -5f), c + Offset(-1f, -5f), c + Offset(0f, -6.6f)); cubicTo(c + Offset(1f, -5f), c + Offset(3f, -5f), c + Offset(3.6f, -3f)); cubicTo(c + Offset(6.4f, -1f), c + Offset(5f, 4f), c + Offset(0f, 5.4f)); close() }
                    drawPath(Path().apply { addPath(hoja, Offset(-r.lat * 0.9f, 1.1f)) }, l.tono(mult(CALIZA, r.color, 1f)).copy(alpha = 0.75f)); drawPath(hoja, cara(l, r, CALIZA, 0.55f))
                    drawLine(l.tono(mult(CALIZA, r.color, 0.9f)).copy(alpha = 0.7f), c + Offset(0f, 5f), c + Offset(0f, -5f), 0.4f); drawLine(l.tono(mult(CALIZA, r.color, 0.9f)).copy(alpha = 0.6f), c + Offset(0f, 1f), c + Offset(-3.6f, -1.6f), 0.35f); drawLine(l.tono(mult(CALIZA, r.color, 0.9f)).copy(alpha = 0.6f), c + Offset(0f, 1f), c + Offset(3.6f, -1.6f), 0.35f) }
                // El collarino y el ábaco.
                drawLine(l.tono(mult(CALIZA, r.color, 0.9f)).copy(alpha = 0.7f), p(-18f, -12.6f), p(18f, -12.6f), 0.8f); drawLine(cara(l, r, CALIZA, 0.8f), p(-18f, -13.6f), p(18f, -13.6f), 0.5f)
                drawLine(l.tono(mult(CALIZA, r.color, 0.9f)).copy(alpha = 0.8f), p(-24f, -37.4f), p(24f, -37.4f), 0.9f)
                // Las líneas de trazo, en almagre, de lo que falta por sacar.
                drawPath(Path().apply { moveTo(p(6f, -34f)); cubicTo(p(14f, -30f), p(12f, -22f), p(7f, -16f)) }, l.tono(ROJO).copy(alpha = 0.55f), style = Stroke(0.5f))
            }
            drawPath(poligono(p(-23f, -43f), p(23f, -43f), p(20f, -46.4f), p(-20f, -46.4f)), if (nevado(m) && !saco) l.tono(NIEVE) else cara(l, r, CALIZA, 0.85f * r.dia + 0.1f))
            // La marca de Pierre: pequeña en el costado siempre; grande y recién abierta si hoy se habla de ella.
            if (m.ficha.objeto == "marca" && !saco) { drawOval(cara(l, r, CALIZA, 0.5f), p(1f, -36f), Size(18f, 24f)); marca(p(10f, -24f), 3.2f, 0, l.tono(Color(0xFF5A4E48)), 1.9f); marca(p(10.5f, -23.6f), 3.2f, 0, l.tono(Color(0xFFFFF8E8)).copy(alpha = 0.9f), 0.5f)
                repeat(7) { drawCircle(l.tono(Color.White).copy(alpha = 0.8f), 0.6f, p(3f + it * 2f, -9.6f - (it % 2) * 0.8f)) } }
            else marca(p(13.6f, -17f), 0.8f, 0, l.tono(mult(CALIZA, r.color, 1f)).copy(alpha = 0.85f), 0.5f)
            if (saco) {
                // El saco que la tapa mientras él no puede tallar.
                val tela = Path().apply { moveTo(p(-25f, -41f)); cubicTo(p(-10f, -49f), p(10f, -48f), p(25f, -42f)); cubicTo(p(27f, -34f), p(24f, -24f), p(26f, -15f)); cubicTo(p(18f, -19f), p(12f, -13f), p(4f, -18f)); cubicTo(p(-4f, -14f), p(-12f, -20f), p(-20f, -16f)); cubicTo(p(-24f, -24f), p(-27f, -32f), p(-25f, -41f)); close() }
                volumen(l, r, tela, pie.x - 26f, pie.x + 26f, Color(0xFF9C8660))
                clipPath(tela) { var x = -26f; while (x < 27f) { drawLine(l.tono(Color(0xFF6E5C40)).copy(alpha = 0.3f), p(x, -50f), p(x + 3f, -12f), 0.35f); x += 2.2f }
                    for (x in listOf(-14f, -2f, 11f)) drawPath(Path().apply { moveTo(p(x, -45f)); cubicTo(p(x - 2f, -36f), p(x + 3f, -28f), p(x, -17f)) }, l.tono(mult(Color(0xFF9C8660), r.color, 0.9f)).copy(alpha = 0.6f), style = Stroke(1.3f)) }
                drawLine(l.tono(Color(0xFFB89A62)), p(-26f, -30f), p(26f, -29f), 0.9f)
            } else if (tallando(m)) {
                // El mazo y el cincel, encima, y las lascas frescas al pie.
                mazo(l, r, p(-4f, -46.6f), 0.9f); drawLine(l.tono(HIERRO), p(8f, -45.6f), p(19f, -47.6f), 1.4f, StrokeCap.Round)
                val az = Azar(Semilla.de("gotico", "lascas")); repeat(26) { val q = p(az.entre(-30f, 34f), az.entre(-2f, 7f)); val w = az.entre(1.2f, 3.2f); drawPath(poligono(q, q + Offset(w, -w * 0.5f), q + Offset(w * 1.4f, w * 0.3f)), l.tono(Color(0xFFF6F0E0))) }
            }
        }
    }

    /** Un mazo de fresno, tumbado: la cabeza, gastada de un lado, y el mango. [k] lo agranda. */
    private fun DrawScope.mazo(l: Luces, r: Rayo, sobre: Offset, k: Float) {
        fun p(dx: Float, dy: Float) = Offset(sobre.x + dx * k, sobre.y + dy * k)
        val fresno = Color(0xFFCBA873)
        drawLine(cara(l, r, fresno, 0.2f), p(4f, -3f), p(19f, -1.4f), 2.2f * k, StrokeCap.Round); drawLine(l.tono(lerp(fresno, Color.White, 0.4f)).copy(alpha = 0.6f), p(4f, -3.6f), p(18f, -2.2f), 0.5f)
        val cabeza = Path().apply { moveTo(p(-6f, 0f)); cubicTo(p(-8.4f, -3f), p(-8f, -8f), p(-5f, -10f)); lineTo(p(3.4f, -10f)); cubicTo(p(6.6f, -8f), p(7f, -3f), p(4.6f, 0f)); close() }
        volumen(l, r, cabeza, sobre.x - 8f * k, sobre.x + 7f * k, fresno)
        clipPath(cabeza) { for (q in 0..4) drawLine(l.tono(Color(0xFF8A6A40)).copy(alpha = 0.35f), p(-8f, -9f + q * 2.2f), p(7f, -8.4f + q * 2.2f), 0.35f); mancha(p(-5f, -4f), 3f * k, 4f * k, l.tono(Color(0xFF8A6A40)), 0.4f) }
        drawOval(l.tono(lerp(fresno, Color(0xFF7A5A34), 0.4f)), p(2.2f, -10f), Size(4.4f * k, 10f * k))
    }

    /** La cabria: tres pies de pino atados arriba, con su polea, para subir la piedra a su asiento. De su soga cuelga el gancho, o la plomada si es el objeto del día. */
    private fun cabriaCosas(m: Momento, l: Luces, r: Rayo): List<Cosa> {
        val nudo = Offset(190f, 120f)
        val patas = listOf(Offset(158f, 262f), Offset(226f, 259f), Offset(204f, 284f))
        fun palo(a: Offset, g: Float) = Path().apply { val d = a - nudo; val n = Offset(-d.y, d.x) / d.getDistance(); val arriba = nudo - d * 0.07f
            moveTo(a - n * g); lineTo(a + n * g); lineTo(arriba + n * g * 0.6f); lineTo(arriba - n * g * 0.6f); close() }
        fun DrawScope.pintaPalo(a: Offset, g: Float, delante: Boolean) {
            val f = palo(a, g); val d = a - nudo
            drawPath(f, Brush.linearGradient(listOf(cara(l, r, lerp(ROBLE, PAJA_VIEJA, 0.45f), 0.5f * r.lat + 0.2f), cara(l, r, lerp(ROBLE, PAJA_VIEJA, 0.45f), -0.5f * r.lat - 0.1f)), a - Offset(g, 0f), a + Offset(g, 0f)))
            clipPath(f) { for (q in 1..7) { val c = nudo + d * (q / 8f); drawOval(l.tono(ROBLE_OSC).copy(alpha = 0.3f), c + Offset(-0.8f, -1.6f), Size(1.6f, 3.2f)) } }
            if (!delante) drawPath(f, l.tono(mult(ROBLE, r.color, 0.8f)).copy(alpha = 0.3f))
        }
        val detras = Cosa(Path().apply { addPath(palo(patas[0], 2f)); addPath(palo(patas[1], 2f)) }, Offset(190f, 261f), bajoElFollaje = false) {
            pintaPalo(patas[0], 2f, false); pintaPalo(patas[1], 2f, false)
        }
        val delante = Cosa(palo(patas[2], 2.4f), patas[2], bajoElFollaje = false) {
            // La soga y lo que cuelga de ella.
            val polea = nudo + Offset(0.6f, 9f)
            val plomada = m.ficha.objeto == "plomada"
            val cabo = if (plomada) Offset(polea.x, 188f) else Offset(polea.x + 1f, 214f)
            drawLine(l.tono(if (plomada) Color(0xFFF0E4C0) else Color(0xFFB89A62)), polea, cabo, 0.9f)
            if (!plomada) drawPath(Path().apply { moveTo(polea + Offset(2f, 0f)); cubicTo(polea + Offset(12f, 40f), Offset(214f, 230f), Offset(219f, 244f)) }, l.tono(Color(0xFFB89A62)), style = Stroke(0.9f))
            pintaPalo(patas[2], 2.4f, true)
            // La atadura de arriba y la polea.
            for (q in 0..4) drawLine(l.tono(Color(0xFFC8AA70)), nudo + Offset(-4.4f, -3f + q * 1.5f), nudo + Offset(4.4f, -2f + q * 1.5f), 1f)
            drawCircle(l.tono(ROBLE_OSC), 3.2f, polea); drawCircle(l.tono(lerp(ROBLE, CALIDA, 0.3f)), 2.1f, polea); drawCircle(l.tono(HIERRO), 0.7f, polea)
            if (plomada) {
                // La plomada: la bola de plomo, en pera, quieta al final de su cordel.
                val bola = Path().apply { moveTo(cabo); cubicTo(cabo + Offset(-3f, 5f), cabo + Offset(-9f, 13f), cabo + Offset(-7f, 20f)); cubicTo(cabo + Offset(-5f, 27f), cabo + Offset(5f, 27f), cabo + Offset(7f, 20f)); cubicTo(cabo + Offset(9f, 13f), cabo + Offset(3f, 5f), cabo); close() }
                drawPath(bola, l.tono(Color(0xFF2E2E36)), style = Stroke(1.2f)); volumen(l, r, bola, cabo.x - 9f, cabo.x + 9f, Color(0xFF8E909C)); drawPath(poligono(cabo + Offset(-1.6f, 25f), cabo + Offset(1.6f, 25f), cabo + Offset(0f, 30f)), l.tono(Color(0xFF55565E)))
                drawOval(Color.White.copy(alpha = 0.55f * r.dia), cabo + Offset(if (r.lat >= 0f) 1.4f else -4.4f, 10f), Size(3f, 6f))
            } else { drawArc(l.tono(HIERRO), -30f, 240f, false, cabo + Offset(-2.2f, 0f), Size(4.4f, 5f), style = Stroke(1.1f)) }
        }
        return listOf(detras, delante)
    }

    /** El banco de trabajo: un tablón grueso sobre dos caballetes. Encima, el objeto del día y lo que pida la hora. */
    private fun bancoCosa(m: Momento, l: Luces, r: Rayo): Cosa {
        val x0 = 236f; val x1 = 304f; val yt = 250f; val g = 5f; val y1 = 281f
        val forma = poligono(listOf(Offset(x0, yt), Offset(x1, yt), Offset(x1, yt + g), Offset(x1 - 5f, yt + g), Offset(x1 - 1f, y1), Offset(x1 - 5f, y1), Offset(x1 - 11f, yt + g + 6f), Offset(x1 - 17f, y1), Offset(x1 - 21f, y1), Offset(x1 - 16f, yt + g),
            Offset(x0 + 16f, yt + g), Offset(x0 + 21f, y1), Offset(x0 + 17f, y1), Offset(x0 + 11f, yt + g + 6f), Offset(x0 + 5f, y1), Offset(x0 + 1f, y1), Offset(x0 + 5f, yt + g), Offset(x0, yt + g)))
        return Cosa(forma, Offset((x0 + x1) / 2f, y1)) {
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(x0 + 2f, y1 - 2.4f), Size(x1 - x0 - 4f, 5f))
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), yt + g, yt + g + 10f), Offset(x0 + 6f, yt + g), Size(x1 - x0 - 12f, 10f))
            // Los caballetes.
            for (c in listOf(x0 + 11f, x1 - 11f)) { for (s in listOf(-1f, 1f)) { val pata = poligono(Offset(c + s * 4f, yt + g), Offset(c + s * 8f, yt + g), Offset(c + s * 10f, y1), Offset(c + s * 6f, y1)); drawPath(pata, cara(l, r, ROBLE, if (s * r.lat > 0f) 0.5f else -0.4f)) }
                drawLine(cara(l, r, ROBLE, 0.1f), Offset(c - 7.6f, yt + g + 14f), Offset(c + 7.6f, yt + g + 14f), 2f) }
            // El tablón: el canto, la cara de arriba y sus vetas.
            val canto = caja(x0, yt, x1, yt + g)
            drawPath(canto, Brush.horizontalGradient(listOf(cara(l, r, lerp(ROBLE, PAJA_VIEJA, 0.25f), frente(r) + 0.12f * r.lat), cara(l, r, lerp(ROBLE, PAJA_VIEJA, 0.25f), frente(r) - 0.12f * r.lat)), x0, x1))
            clipPath(canto) { val az = Azar(Semilla.de("gotico", "banco")); repeat(8) { val y = az.entre(yt, yt + g); drawLine(l.tono(ROBLE_OSC).copy(alpha = 0.35f), Offset(x0, y), Offset(x1, y + az.entre(-1f, 1f)), 0.3f) }; for (x in listOf(x0 + 20f, x1 - 26f)) drawOval(l.tono(ROBLE_OSC).copy(alpha = 0.5f), Offset(x, yt + 1.2f), Size(3f, 2f)) }
            val tablero = poligono(Offset(x0, yt), Offset(x0 + 4f, yt - 6f), Offset(x1 + 2f, yt - 6f), Offset(x1, yt))
            drawPath(tablero, if (nevado(m)) l.tono(lerp(ROBLE, NIEVE, 0.7f)) else cara(l, r, lerp(ROBLE, PAJA_VIEJA, 0.3f), 0.8f * r.dia + 0.05f))
            clipPath(tablero) { for (q in 1..3) drawLine(l.tono(ROBLE_OSC).copy(alpha = 0.3f), Offset(x0, yt - q * 1.6f), Offset(x1 + 2f, yt - q * 1.6f), 0.3f)
                // Las muescas y los cortes de años de uso.
                val az = Azar(Semilla.de("gotico", "muescas")); repeat(10) { val q = Offset(az.entre(x0 + 4f, x1 - 4f), az.entre(yt - 5f, yt - 1f)); drawLine(l.tono(ROBLE_OSC).copy(alpha = 0.5f), q, q + Offset(az.entre(1f, 3f), az.entre(-0.6f, 0.6f)), 0.35f) } }
            drawLine(l.tono(lerp(ROBLE, Color.White, 0.5f)).copy(alpha = 0.6f * r.dia), Offset(x0, yt), Offset(x1, yt), 0.5f)
            // La sombra del objeto sobre el tablero.
            if (r.fuerza > 0.05f) drawOval(r.color.copy(alpha = 0.5f * r.fuerza), Offset(SOBRE.x - 12f + (r.ax * 4f).coerceIn(-9f, 9f), yt - 4.4f), Size(24f, 3.6f), blendMode = androidx.compose.ui.graphics.BlendMode.Multiply)
            scale(1.4f, 1.4f, SOBRE + Offset(0f, -1f)) { objeto(m, l, r) }
            // Lo que pide la hora: la escudilla y la cuchara a la hora de comer; la jarra y los vasos cuando hay gente.
            val sitio = Offset(x0 + 10f, yt - 3f)
            if (comiendo(m)) { drawOval(l.tono(ROBLE_OSC), sitio + Offset(-5f, -3.6f), Size(10f, 3.6f)); drawPath(Path().apply { moveTo(sitio + Offset(-5f, -1.8f)); cubicTo(sitio + Offset(-4f, 2f), sitio + Offset(4f, 2f), sitio + Offset(5f, -1.8f)); close() }, cara(l, r, ROBLE, 0.2f)); drawOval(l.tono(Color(0xFFB89A62)), sitio + Offset(-3.8f, -3f), Size(7.6f, 2.2f))
                drawLine(cara(l, r, lerp(ROBLE, PAJA, 0.4f), 0.3f), sitio + Offset(7f, 0.6f), sitio + Offset(14f, -1f), 1f, StrokeCap.Round) }
            if (conGente(m) && m.ficha.objeto != "jarra") { jarra(l, r, sitio + Offset(0f, 1f), 0.8f); for (dx in listOf(9f, 15f)) { val v = sitio + Offset(dx, 1f); val vaso = poligono(v + Offset(-2f, 0f), v + Offset(2f, 0f), v + Offset(2.6f, -6f), v + Offset(-2.6f, -6f)); volumen(l, r, vaso, v.x - 2.6f, v.x + 2.6f, lerp(ROBLE, PAJA_VIEJA, 0.4f)) } }
        }
    }

    /** Un cesto de mimbre con el rollo de soga dentro. */
    private fun cestoCosa(l: Luces, r: Rayo, pie: Offset): Cosa {
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx, pie.y + dy)
        val forma = Path().apply { moveTo(p(-10f, 0f)); cubicTo(p(-13f, -5f), p(-14f, -12f), p(-13f, -16f)); lineTo(p(13f, -16f)); cubicTo(p(14f, -12f), p(13f, -5f), p(10f, 0f)); close() }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.26f), p(-13f, -2.4f), Size(27f, 5f))
            volumen(l, r, forma, pie.x - 14f, pie.x + 14f, Color(0xFFB8935A))
            clipPath(forma) { for (y in 0..7) drawLine(l.tono(Color(0xFF7A5A32)).copy(alpha = 0.5f), p(-14f, -2f * y - 0.6f), p(14f, -2f * y - 0.6f), 0.45f); for (x in -6..6) drawLine(l.tono(Color(0xFF7A5A32)).copy(alpha = 0.35f), p(x * 2.2f, 0f), p(x * 2.4f, -16f), 0.4f) }
            drawOval(l.tono(Color(0xFF8A6A3A)), p(-13.4f, -18f), Size(26.8f, 4.4f)); drawOval(l.tono(Color(0xFF3A2C20)), p(-11.6f, -17.4f), Size(23.2f, 3f))
            for (k in 0..2) drawOval(l.tono(Color(0xFFC8AA70)), p(-9f + k, -19.4f - k * 0.9f), Size(18f - 2f * k, 3.4f), style = Stroke(1.3f))
        }
    }

    /** El poste de la logia, que sube hasta el alero: de roble sin escuadrar, con sus grietas, la soga enrollada en su clavo y el farol. */
    private fun posteCosa(m: Momento, l: Luces, r: Rayo): Cosa {
        val x0 = 333f; val x1 = 346f
        val forma = poligono(listOf(Offset(x0 - 0.8f, ALTO + 2f), Offset(x1 + 0.8f, ALTO + 2f), Offset(x1, 26f), Offset(x0, 22f)))
        return Cosa(forma, Offset((x0 + x1) / 2f, ALTO + 1f), bajoElFollaje = false) {
            volumen(l, r, forma, x0 - 1f, x1 + 1f, ROBLE)
            clipPath(forma) { val az = Azar(Semilla.de("gotico", "poste")); repeat(9) { val x = az.entre(x0, x1); val y = az.entre(30f, ALTO); drawLine(l.tono(ROBLE_OSC).copy(alpha = 0.5f), Offset(x, y), Offset(x + az.entre(-0.6f, 0.6f), y + az.entre(12f, 40f)), 0.4f) }
                repeat(4) { val c = Offset(az.entre(x0 + 2f, x1 - 2f), az.entre(60f, 270f)); drawOval(l.tono(ROBLE_OSC).copy(alpha = 0.6f), c + Offset(-1.2f, -2f), Size(2.4f, 4f)) }
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFF3E3024)).copy(alpha = 0.5f)), 262f, ALTO), Offset(x0 - 1f, 262f), Size(12f, 24f)) }
            // El rollo de soga, colgado de su clavo.
            for (k in 0..2) drawOval(l.tono(if (k == 1) Color(0xFFD8BC84) else Color(0xFFB89A62)), Offset(x0 - 4f + k * 0.7f, 186f + k * 0.5f), Size(15f - k * 1.4f, 22f - k * 1.6f), style = Stroke(1.5f))
            drawLine(l.tono(Color(0xFF8A6E40)), Offset(x0 + 3f, 184f), Offset(x0 + 4f, 192f), 2.2f); drawCircle(l.tono(HIERRO), 1f, Offset(x0 + 3.6f, 185f))
            // El farol: de hierro y cuerno, en su escarpia.
            val f = FAROL
            drawLine(l.tono(HIERRO), Offset(x0 + 1f, f.y - 13f), Offset(f.x, f.y - 13f), 1f); drawLine(l.tono(HIERRO), Offset(f.x, f.y - 13f), Offset(f.x, f.y - 8f), 0.7f)
            drawRect(if (farol(l)) LUZ_DE_CASA else l.tono(Color(0xFFCDB98A)), Offset(f.x - 3.2f, f.y - 5f), Size(6.4f, 8.6f))
            for (dx in listOf(-3.2f, 0f, 3.2f)) drawLine(l.tono(HIERRO), Offset(f.x + dx, f.y - 5f), Offset(f.x + dx, f.y + 3.6f), 0.6f)
            drawPath(poligono(Offset(f.x - 4.2f, f.y - 5f), Offset(f.x, f.y - 9f), Offset(f.x + 4.2f, f.y - 5f)), l.tono(HIERRO)); drawRect(l.tono(HIERRO), Offset(f.x - 3.8f, f.y + 3.4f), Size(7.6f, 1.2f))
        }
    }

    // ---------------------------------------------------------------- Los objetos del día que van sobre el banco

    /** La jarra de la casa: de barro vidriado de verde, con una mella en el pico. */
    private fun DrawScope.jarra(l: Luces, r: Rayo, sobre: Offset, k: Float) {
        fun p(dx: Float, dy: Float) = Offset(sobre.x + dx * k, sobre.y + dy * k)
        drawPath(Path().apply { moveTo(p(4f, -11f)); cubicTo(p(10f, -11f), p(10f, -3f), p(4.6f, -3.6f)) }, l.tono(mult(VIDRIADO, r.color, 0.5f)), style = Stroke(1.5f * k))
        val cuerpo = Path().apply { moveTo(p(-3.4f, 0f)); cubicTo(p(-7f, -3f), p(-6.4f, -9f), p(-3f, -11f)); lineTo(p(-3.4f, -14f)); lineTo(p(-5f, -15.6f)); lineTo(p(-2.6f, -15f)); lineTo(p(3.4f, -15f)); lineTo(p(3f, -11f)); cubicTo(p(6.4f, -9f), p(7f, -3f), p(3.4f, 0f)); close() }
        volumen(l, r, cuerpo, sobre.x - 7f * k, sobre.x + 7f * k, VIDRIADO)
        clipPath(cuerpo) { drawRect(l.tono(Color(0xFFB8845A)), p(-8f, -2.4f), Size(16f * k, 3f * k)); mancha(p(-1f, -8f), 2f * k, 3f * k, l.tono(Color(0xFF3E5E34)), 0.4f) }
        drawLine(Color.White.copy(alpha = 0.5f * (1f - l.noche)), p(if (r.lat >= 0f) 2.4f else -2.6f, -9f), p(if (r.lat >= 0f) 2.8f else -3f, -5f), 0.6f, StrokeCap.Round)
        drawOval(l.tono(Color(0xFF2E3A28)), p(-3f, -15.8f), Size(6.2f * k, 1.6f * k))
    }

    /** El objeto del día, sobre el banco. Los que no caben ahí (la cuerda, la capucha, la plantilla, el compás, la plomada y la marca) van en su sitio. */
    private fun DrawScope.objeto(m: Momento, l: Luces, r: Rayo) {
        val s = SOBRE
        fun p(dx: Float, dy: Float) = Offset(s.x + dx, s.y + dy)
        // Lo de siempre, en la punta del banco: la piedra de afilar y una cuña.
        when (m.ficha.objeto) {
            "mazo" -> mazo(l, r, p(-3f, -1.4f), 1.3f)
            "cincel" -> {
                // El cincel dentado: la caña de hierro, la cabeza recalcada por los golpes y la boca, como un peine.
                drawLine(l.tono(Color(0xFF36363C)), p(-15f, -2f), p(10f, -4.4f), 3.2f, StrokeCap.Round); drawLine(l.tono(Color(0xFFAEB4C0)).copy(alpha = 0.85f), p(-14f, -2.9f), p(9f, -5.3f), 0.7f)
                drawOval(l.tono(Color(0xFF55565E)), p(-18.4f, -4.4f), Size(4.6f, 4.6f))
                val boca = poligono(p(9f, -6.6f), p(16f, -8.4f), p(17f, -3f), p(10f, -2.4f)); drawPath(boca, l.tono(Color(0xFF4A4C54)))
                for (q in 0..5) drawLine(l.tono(Color(0xFFD0D4DC)), p(16f + q * 0.1f, -8f + q * 0.95f), p(18.6f + q * 0.1f, -8.2f + q * 0.95f), 0.5f)
                repeat(5) { drawPath(poligono(p(-8f + it * 3f, 1f), p(-6.6f + it * 3f, 0f), p(-6f + it * 3f, 1.2f)), l.tono(Color(0xFFF6F0E0))) }
            }
            "escuadra" -> {
                // La escuadra de hierro, tendida sobre el tablero: el brazo largo y el corto, que se aleja.
                val forma = poligono(listOf(p(-15f, -0.6f), p(15f, -0.6f), p(15.6f, -3f), p(-10.6f, -3f), p(-7.4f, -8.4f), p(-10.4f, -8.4f)))
                drawPath(Path().apply { addPath(forma, Offset(-r.lat * 1.2f, 0.8f)) }, Color.Black.copy(alpha = 0.3f))
                drawPath(forma, l.tono(Color(0xFF4E5058))); drawLine(l.tono(Color(0xFFB8BEC8)).copy(alpha = 0.8f), p(-10f, -2.6f), p(15f, -2.6f), 0.5f); drawLine(l.tono(Color(0xFFB8BEC8)).copy(alpha = 0.6f), p(-10.6f, -3f), p(-7.8f, -8f), 0.5f)
                for (q in 0..8) drawLine(l.tono(Color(0xFF2A2A30)), p(-8f + q * 2.6f, -0.9f), p(-8f + q * 2.6f, -1.9f), 0.35f)
            }
            "jarra" -> jarra(l, r, p(0f, 0f), 1.35f)
            "pan" -> {
                // La hogaza, sobre su paño: la corteza oscura, la cruz del corte y la harina.
                drawPath(poligono(p(-16f, 0.6f), p(15f, 0.6f), p(18f, -4.6f), p(-12f, -5f)), cara(l, r, Color(0xFFE8E0CC), 0.5f)); drawLine(l.tono(AZUL).copy(alpha = 0.6f), p(-13f, -0.6f), p(14.6f, -0.6f), 0.5f)
                val hogaza = Path().apply { moveTo(p(-11f, -1.6f)); cubicTo(p(-12f, -9f), p(-5f, -12.6f), p(0f, -12.6f)); cubicTo(p(5f, -12.6f), p(12f, -9f), p(11f, -1.6f)); cubicTo(p(6f, 0.4f), p(-6f, 0.4f), p(-11f, -1.6f)); close() }
                volumen(l, r, hogaza, s.x - 12f, s.x + 12f, PAN)
                clipPath(hogaza) { mancha(p(0f, -11f), 8f, 3f, l.tono(Color(0xFFF2E6C8)), 0.5f); drawLine(l.tono(Color(0xFF7A5226)), p(-6f, -10f), p(6f, -5.6f), 0.9f); drawLine(l.tono(Color(0xFF7A5226)), p(6f, -10.6f), p(-6f, -5f), 0.9f) }
            }
            "vela" -> {
                // La vela de sebo, en su palmatoria de hierro, con el sebo corrido.
                drawOval(l.tono(HIERRO), p(-7f, -3f), Size(14f, 3.6f)); drawOval(l.tono(Color(0xFF2A2A30)), p(-5.6f, -2.6f), Size(11.2f, 2.4f))
                val vela = caja(s.x - 1.9f, s.y - 15f, s.x + 1.9f, s.y - 1.6f); volumen(l, r, vela, s.x - 1.9f, s.x + 1.9f, CERA)
                drawLine(l.tono(CERA), p(-1.6f, -13f), p(-2.4f, -7f), 1.2f, StrokeCap.Round); drawLine(l.tono(Color(0xFF2A2420)), p(0f, -15f), p(0.3f, -17f), 0.5f)
                drawArc(l.tono(HIERRO), 270f, 180f, false, p(5f, -6f), Size(5f, 5f), style = Stroke(0.9f))
            }
            else -> Unit
        }
    }

    // ---------------------------------------------------------------- El alero que enmarca el cuadro

    /** El alero de paja de la logia, que entra por arriba a la derecha: los pares, el canto de la paja con su fleco, y el jabalcón del poste. */
    private fun DrawScope.alero(m: Momento, l: Luces, r: Rayo) {
        val a = Offset(204f, -3f); val b = Offset(383f, 42f)
        fun en(t: Float) = a + (b - a) * t
        val az = Azar(Semilla.de("gotico", "alero"))
        // El jabalcón.
        val jab = poligono(Offset(337f, 86f), Offset(341f, 92f), Offset(304f, 26f), Offset(300f, 22f))
        drawPath(jab, Brush.linearGradient(listOf(cara(l, r, ROBLE, 0.4f * r.lat + 0.1f), cara(l, r, ROBLE, -0.4f * r.lat - 0.2f)), Offset(318f, 52f), Offset(324f, 58f)))
        // Lo de debajo del tejado: en sombra, con los pares y las latas.
        drawPath(poligono(a, Offset(383f, -3f), b), l.tono(Color(0xFF3E3024)))
        for (k in 0..6) { val p = en(0.1f + k * 0.14f); drawLine(l.tono(lerp(ROBLE, Color(0xFF3E3024), 0.35f)), p + Offset(-1f, 1f), p + Offset(46f, -40f), 3f); drawLine(l.tono(lerp(ROBLE, CALIDA, 0.2f)).copy(alpha = 0.5f), p + Offset(-2f, 0f), p + Offset(45f, -41f), 0.5f) }
        for (k in 1..3) drawLine(l.tono(Color(0xFF6E5438)).copy(alpha = 0.7f), en(0f) + Offset(k * 14f, -k * 12f), en(1f) + Offset(k * 14f, -k * 12f), 1f)
        if (farol(l)) drawPath(poligono(en(0.5f), Offset(383f, -3f), b), Brush.radialGradient(listOf(LUZ_DE_CASA.copy(alpha = 0.22f * l.noche), Color.Transparent), FAROL + Offset(0f, -90f), 80f))
        // El canto de la paja: gruesa, cortada a cuchillo, con las puntas claras.
        val canto = poligono(a + Offset(0f, -2f), b + Offset(0f, -2f), b + Offset(0f, 9.6f), a + Offset(0f, 5.6f))
        val paja = if (nevado(m)) lerp(PAJA, NIEVE, 0.35f) else PAJA
        drawPath(canto, Brush.verticalGradient(listOf(cara(l, r, lerp(paja, PAJA_VIEJA, 0.5f), haciaDe(r, -0.5f) - 0.1f), cara(l, r, paja, haciaDe(r, -0.5f) + 0.25f)), 0f, 50f))
        clipPath(canto) { repeat(150) { val t = az.real(); val p = en(t) + Offset(0f, az.entre(-2f, 8f)); drawLine(l.tono(if (it % 3 == 0) Color(0xFFF0DCA0) else if (it % 3 == 1) Color(0xFF8E7A4C) else PAJA).copy(alpha = 0.7f), p, p + Offset(az.entre(-0.8f, 0.8f), az.entre(2f, 5f)), 0.5f) } }
        // El fleco: las pajas que cuelgan del borde.
        repeat(74) { val t = az.real(); val p = en(t) + Offset(0f, 5.4f + 4f * t); drawLine(l.tono(if (it % 2 == 0) PAJA else PAJA_VIEJA), p, p + Offset(az.entre(-1f, 1f), az.entre(1.6f, 5.4f)), 0.6f) }
        // La lata que sujeta la paja, y sus ataduras de mimbre.
        drawLine(l.tono(ROBLE_OSC).copy(alpha = 0.7f), a + Offset(0f, 1.4f), b + Offset(0f, 3f), 1f)
        for (k in 0..11) { val p = en(0.04f + k * 0.085f) + Offset(0f, 2f + 0.14f * k); drawLine(l.tono(Color(0xFF6E5438)), p + Offset(-0.6f, -2f), p + Offset(0.6f, 2f), 0.8f) }
        if (nevado(m)) { repeat(16) { val t = az.real(); val p = en(t) + Offset(0f, 9f + 4f * t); drawPath(poligono(p + Offset(-0.9f, 0f), p + Offset(0.9f, 0f), p + Offset(0f, az.entre(3f, 9f))), Color.White.copy(alpha = 0.75f)) } }
        if (fiesta(m)) guirnalda(l, en(0.16f) + Offset(0f, 10f), en(0.76f) + Offset(0f, 12f), 12f, m.azar("ga"), flores = listOf(ROJO, ORO, Color.White), grueso = 3f)
    }

    // ---------------------------------------------------------------- La parte viva

    override fun seMueve(m: Momento): Boolean = true                          // el río siempre brilla

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        val l = luces(m)
        val viento = -(0.25f + 0.75f * m.viento)
        destellos(l, Rect(196f, 161f, 380f, 176f), reloj, m.semilla("destellos"), 9)
        // El humo de tres chimeneas de la villa, y el de la fragua a sus horas de trabajo.
        for ((i, p) in listOf(Offset(252f, 184f), Offset(312f, 186f), Offset(356f, 182f)).withIndex()) humo(l, p, 22f, 5f, reloj, viento, 0.25f, 0.25f, 20 + i, 7)
        if (m.hacer == Hacer.TRABAJO && obra(m) != Obra.TAPADA) humo(l, FRAGUA, 34f, 7f, reloj, viento, 0.55f, 0.1f, 4, 8)
        // Las hogueras de San Juan, de noche: en la ribera de acá y una en la otra orilla, con su reflejo.
        if (m.con("hogueras") && l.noche > 0.3f) {
            for ((i, p) in HOGUERAS.withIndex()) { llama(p, 8f, reloj, 30 + i); llama(p + Offset(2f, 0f), 6f, reloj, 40 + i); resplandor(p + Offset(0f, -4f), 26f, 0.6f); humo(l, p + Offset(0f, -8f), 30f, 6f, reloj, viento, 0.4f, 0.3f, 50 + i, 6) }
            val q = Offset(338f, 157f); llama(q, 5f, reloj, 60); resplandor(q, 16f, 0.5f); reflejo(q + Offset(0f, 2f), 12f, LLAMA, 0.7f, reloj, 3)
        }
        // Los pájaros que levantan las campanas.
        if (m.con("campanas") && l.noche < 0.6f) for (i in 0..11) {
            val a = reloj * 0.5f + i * 0.9f; val c = Offset(258f + 56f * cos(a * 0.7f + i), 84f + 24f * sin(a + i * 1.3f)); val ala = 2.2f + 1.2f * sin(reloj * 9f + i)
            val tinta = l.tono(Color(0xFF2E2C34), 0.2f); drawLine(tinta, c + Offset(-2.8f, -ala * 0.5f), c, 0.7f); drawLine(tinta, c, c + Offset(2.8f, -ala * 0.5f), 0.7f)
        }
        // El hogar: la llama y el vapor del caldero a la hora de comer; un fuego corto cuando hay gente; brasas de noche.
        val fuego = HOGAR + Offset(0f, -3f)
        if (comiendo(m)) { llama(fuego + Offset(-4f, 0f), 13f, reloj, 1); llama(fuego + Offset(4f, 0f), 10f, reloj, 2); resplandor(fuego + Offset(0f, -6f), 44f, 0.3f + 0.5f * l.noche)
            humo(l, HOGAR + Offset(0f, -28f), 46f, 10f, reloj, viento, 0.1f, 0f, 3, 8, 0.7f) }
        else if (conGente(m)) { llama(fuego, 7f, reloj, 1); resplandor(fuego + Offset(0f, -5f), 36f, 0.25f + 0.5f * l.noche); humo(l, HOGAR + Offset(0f, -12f), 40f, 8f, reloj, viento, 0.3f, 0f, 3, 7, 0.5f) }
        else if (l.noche > 0.3f || m.hacer == Hacer.DORMIR) { brasas(l, fuego, 16f, reloj); resplandor(fuego + Offset(0f, -2f), 24f, 0.3f * l.noche) }
        // El farol del poste y, si es el objeto del día, la vela del banco.
        if (farol(l)) { llama(FAROL + Offset(0f, 2.4f), 4.4f, reloj, 7); resplandor(FAROL, 30f, 0.5f) }
        if (m.ficha.objeto == "vela" && l.noche > 0.3f) { llama(SOBRE + Offset(0.3f, -16.6f), 5f, reloj, 5); resplandor(SOBRE + Offset(0f, -18f), 26f, 0.5f) }
        tiempoVivo(l, m.ficha.clima, reloj, H, m.ficha.semilla)
    }

    // ---------------------------------------------------------------- El gato

    /** Al gato le llega la misma luz que a la escena, con su eclipse. */
    override fun luzDelGato(m: Momento, en: Offset): LuzGato { val l = luces(m); return LuzGato(Rayo(l), l.noche) { l.tono(it) } }

    override fun estorbos(m: Momento): List<Float> = listOf(HOGAR.x)

    override fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 14f, 283f, hasta = 300f))
        add(Ancla(TipoAncla.MIRADOR, 84f, PILA + (BASE - PILA) / 3f))        // encima de la pila de sillares
        add(Ancla(TipoAncla.OBJETO, 268f, 283f))
        add(Ancla(TipoAncla.ABRIGO, 362f, 281f))                             // bajo el alero de la logia
        if (comiendo(m) || conGente(m)) add(Ancla(TipoAncla.FUEGO, HOGAR.x + 34f, 281f, hasta = HOGAR.x))
        if (fiesta(m)) add(Ancla(TipoAncla.ADORNO, 258f, ZARZO))
    }
}
