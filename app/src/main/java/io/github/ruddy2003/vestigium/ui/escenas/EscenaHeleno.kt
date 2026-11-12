package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Casa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LLAMA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LUZ_DE_CASA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.MAR_ORILLA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Techo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.agua
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.antorcha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.arbol
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.bajio
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.brasas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.briznas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.casa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cipres
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.corona
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
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sembrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraDeFollaje
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.volumen
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Helénico: el puerto de Zea, en el Pireo, visto desde el patio de la casa de Demeas (remero de un trirreme, Atenas,
 * 431 a 427 a. C.). **[Aproximación]**: es una composición con lo que había allí, no la vista desde un punto real.
 *
 * - **Al fondo:** el golfo con Salamina a la izquierda; a la derecha, el llano del Ática: la costa con sus calas, sus
 *   puntas y la franja de arena y roca que el mar le ha ido comiendo; los campos de cebada; los Muros Largos que suben
 *   hacia la ciudad; Atenas trepando por su loma hasta el pie de la Acrópolis, con el Partenón recién terminado; el
 *   Licabeto y, detrás, los montes.
 * - **En medio:** la dársena. En la otra orilla, la fila de cobertizos de la flota; en esta, a la izquierda, el
 *   cobertizo del «Aura», la nave de Demeas.
 * - **Delante:** el patio, visto un poco desde arriba. El murete encalado, gastado, con un vano a la izquierda por
 *   donde se baja a la playa. Las cosas de la casa, arrimadas unas a otras: el candelero, dos ánforas, la tinaja, el
 *   brasero, el banco de piedra con el objeto del día, un canasto y un rollo de cuerda colgado. A la derecha crece un
 *   olivo cuya copa enmarca el cuadro por arriba; a la izquierda, desde la temporada 2, la higuera que planta por su
 *   padre.
 *
 * Cómo está pintado **[Diseño propio]**: la luz viene del lado del sol ([Rayo]) y todo arroja su sombra, larga por la
 * mañana y por la tarde y corta a mediodía; la copa del olivo da sombra rota sobre el murete, el suelo y las cosas. El
 * agua es honda lejos y clara en la orilla, y refleja las naves, los cobertizos y la costa. Encima de todo va el grano.
 *
 * **Este cuadro es la vara de calidad de todas las escenas** (`docs/diseno/ESCENAS.md`; decisión 20 del plan). Si su
 * aspecto cambia, hay que rehacer su imagen de referencia (`docs/diseno/escenas/referencia_heleno.jpg`) y la guía.
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioPiloto.kt`):
 * - **El «Aura»** (arco `nave` y estado `mar`): en su cobertizo de noviembre al 12 de abril; en el agua el resto del
 *   año, amarrada o, a las horas en que él trabaja, remando en la dársena; **no está** mientras él está en el mar.
 * - **La flota** (arco `flota`): cuántas naves hay en la dársena, o en los cobertizos en invierno.
 * - **El campo** (arco `campo`): la cebada brota, verdea, dora y se siega; con la clave `cosecha`, las gavillas.
 * - **La guerra** (arcos `estragos` y `refugiados`, clave `humo`): rodales quemados y casas de labor en ruinas en el
 *   llano, chozas junto a los Muros Largos y, los días de invasión, columnas de humo.
 * - **La peste** (arco `peste`, clave `piras`): humos delgados junto a la ciudad.
 * - **Las fiestas y los duelos** (claves `fiesta`, `antorchas`, `luto`): guirnaldas y cintas; antorchas en el muelle;
 *   un paño oscuro en el murete y un lécito en el banco.
 * - **El eclipse** (clave `eclipse`): pasado el mediodía el sol es una media luna y el día se apaga.
 * - **La partida y el regreso** (claves `partida` y `regreso`): la fila de naves que sale o entra por la bocana.
 * - **La higuera** (arco `higuera`): crece cada año; se pela en invierno.
 * - **El objeto del día:** sobre el banco; el remo, apoyado en el murete; la red y el manto, colgados de él.
 * - **El tramo del día:** el brasero encendido con la olla a la hora de comer; la lucerna del candelero, la copa y la
 *   jarra al anochecer, cuando hay gente; brasas y todo apagado mientras duerme.
 */
internal object EscenaHeleno : EscenaVida {
    override val vidas = listOf("remero")

    /** Dónde está el horizonte del mar. */
    private const val H = 128f

    // El murete del patio: su lomo, dónde empieza la cara y dónde toca el suelo. A la izquierda de VANO está abierto.
    private const val CAP = 218f
    private const val CARA = 224.5f
    private const val BASE = 250f
    private const val VANO = 70f

    // Dónde están las cosas del patio que también usa la parte viva, y a qué escala van.
    private val TINAJA = Offset(148f, 275f)
    private val BRASERO = Offset(190f, 282f)
    private const val KB = 1.4f
    private val CANDELERO = Offset(91f, 264f)
    private const val KC = 0.66f
    private val SOBRE = Offset(255f, 255.4f)
    private const val KO = 1.35f

    private val MARMOL = Color(0xFFEDE6D6)
    private val CALIZA = Color(0xFFD8C49A)
    private val ENCALADO = Color(0xFFEFE6D0)
    private val TERRACOTA = Color(0xFFC2652F)
    private val TERRA_OSC = Color(0xFF8A4220)
    private val BARNIZ = Color(0xFF1E1814)
    private val EGEO = Color(0xFF2E5E8E)
    private val PURPURA = Color(0xFF6A2F7E)
    private val ORO = Color(0xFFD2AC4E)
    private val BRONCE = Color(0xFFB07A3A)
    private val ROJO = Color(0xFF9A3B2C)
    private val BREA = Color(0xFF33261E)
    private val MADERA = Color(0xFFB58648)
    private val MADERA_OSC = Color(0xFF6E4A2E)
    private val ARENA = Color(0xFFCDB98C)
    private val CUERO = Color(0xFF9A6A3C)
    private val HIGO = Color(0xFF5B3A6A)

    private val CASA_PUERTO = Casa(ENCALADO, Techo.TEJA, TERRACOTA, MADERA_OSC, ventanas = 1, zocalo = ROJO)
    private val CASA_LABOR = Casa(Color(0xFFE2D4B4), Techo.TEJA, Color(0xFFB8683A), MADERA_OSC, ventanas = 1)

    /** A qué horas se ve el eclipse y con cuánta fuerza: pasado el mediodía, con el máximo hacia las 14:40. */
    private fun eclipse(m: Momento): Float = if (!m.con("eclipse")) 0f else (1.5f - abs(m.hora.toFloat() - 14.7f)).coerceIn(0f, 1f)

    private fun luces(m: Momento): Luces = m.luces(eclipse(m))

    /** El «Aura» está en el agua (y no en su cobertizo). */
    private fun enElAgua(m: Momento) = m.arco("nave") >= 0.5f

    /** Demeas está embarcado: su nave no está. */
    private fun enElMar(m: Momento) = m.estado == "mar"

    /** A esta hora están remando en la dársena: en temporada, en sus horas de trabajo, si no hay tormenta. */
    private fun remando(m: Momento) = enElAgua(m) && !enElMar(m) && m.hacer == Hacer.TRABAJO && m.ficha.clima != Clima.TORMENTA

    /** Dónde puede estar el «Aura». */
    internal enum class Nave { COBERTIZO, AMARRADA, REMANDO, AUSENTE }

    /**
     * Dónde está el «Aura» en este momento. Es lo que el cuadro tiene que contar igual que el cuento (decisión 18): la
     * prueba `EscenasPrueba` lo compara con el calendario en las fechas clave.
     */
    internal fun nave(m: Momento): Nave = when {
        enElMar(m) -> Nave.AUSENTE
        !enElAgua(m) -> Nave.COBERTIZO
        remando(m) -> Nave.REMANDO
        else -> Nave.AMARRADA
    }

    /** Cuántas naves de la flota se ven a flote en la dársena (de 0 a 7): en invierno, ninguna; están en los cobertizos. */
    internal fun navesAFlote(m: Momento): Int = if (enElAgua(m)) (m.arco("flota") * 7f).roundToInt() else 0

    /** En invierno, a sus horas de trabajo, calafatean la nave en el cobertizo: hay un caldero de brea al fuego. */
    private fun calafateando(m: Momento) = !enElAgua(m) && !enElMar(m) && m.hacer == Hacer.TRABAJO

    private fun comiendo(m: Momento) = m.hacer == Hacer.COMER && !enElMar(m)
    private fun conGente(m: Momento) = m.hacer == Hacer.GENTE && !enElMar(m)
    private fun fiesta(m: Momento) = m.con("fiesta") || m.con("antorchas")
    private fun antorchas(m: Momento) = m.con("antorchas") || (m.con("fiesta") && luces(m).noche > 0.4f)

    /** Cuántos humos de pira hay junto a la ciudad. */
    private fun piras(m: Momento) = (m.arco("peste") * 4f).roundToInt() + (if (m.con("piras")) 2 else 0)

    // ---------------------------------------------------------------- La parte quieta

    override fun DrawScope.quieta(m: Momento) {
        val l = luces(m); val r = Rayo(l)
        val est = m.ficha.estacion
        val semilla = m.ficha.semilla
        // El perfil de la tierra es siempre el mismo: su azar no depende del día.
        val fijo = Azar(Semilla.de("heleno", "tierra"))

        // 1. Cielo y lejanía.
        cielo(l, H, semilla)
        astros(l, H, semilla)
        nubes(l, H, m.ficha.clima, semilla)
        monte(l, perfil(fijo, 146f, 660f, H + 2f, 52f, 0.6f, 40, isla = true), H + 3f, Color(0xFF6A7494), 0.82f)
        monte(l, perfil(fijo, 190f, 600f, H + 2f, 30f, 0.35f, 36, isla = true), H + 3f, Color(0xFF7E8660), 0.6f)
        agua(l, Rect(0f, H, ANCHO, 238f), m.azar("mar"), viento = m.viento)
        monte(l, perfil(fijo, -30f, 126f, H + 0.6f, 17f, 0.5f, 16, isla = true), H + 0.8f, Color(0xFF66747A), 0.78f)
        monte(l, perfil(fijo, 122f, 164f, H + 0.6f, 6f, 0.3f, 8, isla = true), H + 0.8f, Color(0xFF6E7C70), 0.7f)
        reflejosDeLaCosta(l)
        if (m.con("partida")) flotilla(l, 5, haciaLaIzquierda = true)
        if (m.con("regreso")) flotilla(l, 5, haciaLaIzquierda = false)
        llano(m, l, r, est, fijo)

        // 2. Plano medio: la otra orilla con sus cobertizos, la dársena y, en esta orilla, el cobertizo del «Aura».
        muelle(m, l, fijo)
        sombrasDelMuelle(r)
        reflejosDelMuelle(m, l)
        estela(l, Rect(0f, H, ANCHO, 236f), m.azar("estela"))
        navesDeLaDarsena(m, l)
        bajio(l, orillaDeLaPlaya())
        orillaCercana(m, l, est, fijo)
        espuma(l)
        when (nave(m)) {
            Nave.REMANDO -> trirreme(l, Offset(246f, 188f), 82f, haciaLaDerecha = false, propia = true, remos = true, lejos = 0.1f)
            Nave.AMARRADA -> trirreme(l, Offset(152f, 207f), 88f, haciaLaDerecha = true, propia = true, lejos = 0f, conCorona = fiesta(m))
            Nave.COBERTIZO -> Unit                                              // la dibuja `orillaCercana`, dentro del cobertizo
            Nave.AUSENTE -> Unit
        }

        // 3. Primer plano: el patio, y la copa del olivo que lo enmarca.
        patio(m, l, r, est)
        follajeDelOlivo(l, r)

        // 4. El velo del tiempo que hace y el grano del papel.
        tiempoQuieto(l, m.ficha.clima, H, semilla)
        grano(Azar(Semilla.de("heleno", "grano")), 0.6f + 0.4f * r.dia)
    }

    // ---------------------------------------------------------------- El mar

    /** La tierra lejana se refleja en el agua como una franja más oscura pegada a la costa. */
    private fun DrawScope.reflejosDeLaCosta(l: Luces) {
        val az = Azar(Semilla.de("heleno", "costa"))
        reflejoRoto(-10f, 124f, H + 0.9f, 6f, l.tono(Color(0xFF4E5E6A), 0.5f), 0.5f, az, 0.8f)
        reflejoRoto(124f, 164f, H + 0.9f, 3f, l.tono(Color(0xFF55665E), 0.5f), 0.45f, az, 0.8f)
    }

    /** Los cobertizos, la torre, la casa y los cipreses de la otra orilla, reflejados; y el agua clara al pie del muelle. */
    private fun DrawScope.reflejosDelMuelle(m: Momento, l: Luces) {
        val az = Azar(Semilla.de("heleno", "reflejos")); val lejos = 0.34f
        drawLine(l.tono(MAR_ORILLA).copy(alpha = 0.45f * (1f - 0.7f * l.noche)), Offset(194f, 168.6f), Offset(ANCHO, 170.6f), 3.6f)
        // La orilla del llano se refleja en una franja pegada a ella.
        for (i in 12 until COSTA.size - 1) reflejoRoto(COSTA[i].x, COSTA[i + 1].x, maxOf(COSTA[i].y, COSTA[i + 1].y) + 0.7f, 5f, l.tono(Color(0xFFA09672), 0.4f), 0.42f, az)
        for (i in 0 until 9) {
            val x = 204f + i * 15.4f; val y = 169.4f + i * 0.2f
            reflejoRoto(x + 1.8f, x + 13.6f, y, 9f, l.tono(Color(0xFF2E2620), lejos), 0.45f, az)
            reflejoRoto(x - 0.7f, x + 2.3f, y, 12f, l.tono(MARMOL, lejos), 0.62f, az)
            reflejoRoto(x + 1f, x + 14.4f, y + 9f, 6f, l.tono(TERRACOTA, lejos), 0.42f, az)
        }
        reflejoRoto(148f, 156f, 156.5f, 13f, l.tono(CALIZA, lejos), 0.55f, az)
        reflejoRoto(350f, 361f, 170.4f, 16f, l.tono(Color(0xFF24402E), lejos), 0.42f, az)
        reflejoRoto(363f, 384f, 170.6f, 9f, l.tono(ENCALADO, lejos), 0.5f, az)
        if (l.noche > 0.5f && m.hacer != Hacer.DORMIR) reflejoRoto(368f, 378f, 170.6f, 14f, LUZ_DE_CASA, 0.6f * l.noche, az)
    }

    private fun orillaDeLaPlaya() = Path().apply {
        moveTo(132f, 190f); cubicTo(146f, 194f, 156f, 199f, 160f, 204f); cubicTo(140f, 212f, 90f, 218f, -2f, 224f)
    }

    /** La línea de espuma donde rompe el agua en la arena, y la arena mojada. */
    private fun DrawScope.espuma(l: Luces) {
        val az = Azar(Semilla.de("heleno", "espuma"))
        val blanco = l.tono(Color.White)
        translate(1.6f, 1.4f) { drawPath(orillaDeLaPlaya(), blanco.copy(alpha = 0.7f), style = Stroke(0.9f, cap = StrokeCap.Round)) }
        repeat(26) {
            val k = az.real(); val p = if (k < 0.3f) Offset(mezcla(134f, 160f, k / 0.3f), mezcla(191f, 204f, k / 0.3f)) else Offset(mezcla(160f, 0f, (k - 0.3f) / 0.7f), mezcla(205f, 224.5f, (k - 0.3f) / 0.7f))
            drawLine(blanco.copy(alpha = az.entre(0.3f, 0.7f)), p + Offset(3f, 2.4f + az.entre(0f, 2.5f)), p + Offset(3f + az.entre(2f, 7f), 2.6f + az.entre(0f, 2.5f)), 0.6f, StrokeCap.Round)
        }
    }

    /**
     * La costa del llano, de la línea del horizonte al arranque del muelle: entrantes, salientes y la punta de la torre.
     * Es un trazo fijo, puesto a mano, para que no salga una curva de compás.
     */
    private val COSTA = listOf(
        Offset(183f, H - 0.5f), Offset(177f, 129.8f), Offset(171.5f, 130.6f), Offset(173.5f, 133.2f), Offset(166f, 135.4f), Offset(160.5f, 137f), Offset(163f, 139.8f),
        Offset(156f, 142f), Offset(149.5f, 143.6f), Offset(151.5f, 146.8f), Offset(145.5f, 149f), Offset(141f, 151.8f), Offset(139.5f, 155.4f), Offset(144f, 157.6f),
        Offset(151f, 158.6f), Offset(157f, 161.6f), Offset(165f, 162.2f), Offset(171f, 164.8f), Offset(180f, 165.2f), Offset(187f, 167.2f), Offset(196f, 166.2f),
    )

    /** La tierra del llano con la costa corrida [k] veces hacia dentro: con 0 es la costa; con 1, hasta donde llega la hierba. */
    private fun tierraDelLlano(k: Float): Path = curva(COSTA.map { p -> val cerca = ((p.y - H) / 40f).coerceIn(0f, 1f); p + Offset(k * (1.5f + 3.5f * cerca), -k * (0.5f + 1.5f * cerca)) }).apply {
        lineTo(ANCHO + 2f, 168f); lineTo(ANCHO + 2f, H - 0.5f); close()
    }

    /** El llano del Ática: la costa, los campos, las casas de labor, los Muros Largos y, al fondo, la ciudad con su roca. */
    private fun DrawScope.llano(m: Momento, l: Luces, r: Rayo, est: Estacion, fijo: Azar) {
        val hierba = lerp(hierbaDe(est, mediterraneo = true), Color(0xFFB6AE78), 0.35f)
        val az = Azar(Semilla.de("heleno", "llano"))
        val tierra = tierraDelLlano(0f); val orilla = curva(COSTA)
        // El agua clara de la orilla y la espuma que la bordea.
        bajio(l, orilla, 11f)
        drawPath(orilla, l.tono(Color.White, 0.3f).copy(alpha = 0.6f * (1f - 0.6f * l.noche)), style = Stroke(1.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        // De abajo arriba: la franja que el mar ha ido comiendo (arena en las calas, roca en las puntas), el talud de
        // tierra que deja al descubierto, en sombra, y la hierba, que no llega hasta el agua.
        drawPath(tierra, Brush.verticalGradient(listOf(l.tono(Color(0xFFBDAA8A), 0.4f), l.tono(Color(0xFFE6D8B2), 0.3f)), H, 168f))
        clipPath(tierra) {
            COSTA.forEachIndexed { i, p ->
                // Cantos y peñas sobre la franja.
                if (i > 0) repeat(3) { val q = p + Offset(az.entre(-0.6f, 3f), az.entre(-2.2f, 1.2f)); val w = az.entre(0.6f, 1.7f) * (0.5f + i / 20f); drawOval(l.tono(if (az.real() > 0.5f) Color(0xFF8E8068) else Color(0xFFF0E6CC), 0.3f), Offset(q.x - w, q.y - w * 0.5f), Size(w * 2f, w)) }
            }
            drawPath(tierraDelLlano(0.82f), l.tono(mult(Color(0xFF94804E), r.color, 0.6f * r.dia), 0.35f))
            val pasto = tierraDelLlano(1f)
            drawPath(pasto, Brush.verticalGradient(listOf(l.tono(hierba, 0.7f), l.tono(hierba, 0.45f), l.tono(lerp(hierba, ARENA, 0.5f), 0.3f)), H, 168f))
            clipPath(pasto) {
                // El llano no es una mesa: lomas suaves, con su lado de sombra, y manchas de monte bajo.
                repeat(5) {
                    val c = Offset(az.entre(200f, 370f), az.entre(136f, 160f)); val w = az.entre(30f, 70f)
                    mancha(c, w, az.entre(2f, 4f), l.tono(mult(hierba, Color(0xFF7E86A6), 0.7f), 0.45f), 0.3f)
                    mancha(c + Offset(r.lat * 6f, -3.4f), w * 0.8f, az.entre(1.4f, 2.6f), l.tono(lerp(hierba, Color(0xFFF2E8B8), 0.5f), 0.45f), 0.3f)
                }
                repeat(60) { val p = Offset(az.entre(150f, 380f), az.entre(132f, 166f)); drawOval(l.tono(Color(0xFF6E7E52), 0.45f).copy(alpha = 0.5f), Offset(p.x, p.y), Size(az.entre(1.2f, 3.4f), az.entre(0.6f, 1.1f))) }
            }
        }
        // Peñas en el agua, frente a las puntas, con su espuma.
        listOf(Offset(157.6f, 136.2f), Offset(146.6f, 143.2f), Offset(136.6f, 153.4f), Offset(134.8f, 156.6f), Offset(153.6f, 162.4f), Offset(168.2f, 166.4f)).forEachIndexed { i, p ->
            val w = 1.1f + 0.22f * i
            drawOval(l.tono(Color.White, 0.3f).copy(alpha = 0.55f * (1f - 0.6f * l.noche)), Offset(p.x - w * 1.5f, p.y - 0.1f), Size(w * 3f, w * 0.8f))
            drawOval(l.tono(Color(0xFF6E6456), 0.3f), Offset(p.x - w, p.y - w * 0.7f), Size(w * 2f, w)); drawOval(l.tono(Color(0xFFCDBFA2), 0.3f), Offset(p.x - w * 0.5f + r.lat * w * 0.3f, p.y - w * 0.7f), Size(w, w * 0.45f))
        }

        // Los campos: una colcha de bancales, cada uno con su grano. Los quemados, según los estragos.
        val campo = m.arco("campo"); val estragos = m.arco("estragos")
        val azar = m.azar("campos")
        val bancales = listOf(
            listOf(Offset(196f, 132f), Offset(232f, 131f), Offset(238f, 138f), Offset(192f, 139f)),
            listOf(Offset(326f, 132f), Offset(346f, 131.6f), Offset(352f, 138f), Offset(322f, 138f)),
            listOf(Offset(350f, 131.4f), Offset(382f, 131f), Offset(382f, 139f), Offset(356f, 138f)),
            listOf(Offset(186f, 140f), Offset(222f, 139.5f), Offset(224f, 147f), Offset(178f, 148f)),
            listOf(Offset(256f, 139.4f), Offset(300f, 139.2f), Offset(306f, 147f), Offset(258f, 147.5f)),
            listOf(Offset(308f, 139.5f), Offset(350f, 139f), Offset(358f, 146.5f), Offset(312f, 147f)),
            listOf(Offset(354f, 140f), Offset(382f, 140f), Offset(382f, 147f), Offset(362f, 146.5f)),
        )
        bancales.forEachIndexed { i, b ->
            // No todos van al mismo paso: unos se sembraron antes que otros.
            val etapa = (campo + (fijo.real() - 0.5f) * 0.14f).coerceIn(0f, 1f)
            val lejos = if (i < 3) 0.6f else 0.45f
            // Los de la derecha, los más lejanos del puerto, arden primero.
            val quemado = ((estragos - (bancales.size - 1 - i) * 0.09f) * 1.5f).coerceIn(0f, 1f)
            sembrado(l, poligono(b), Rect(b.minOf { it.x }, b.minOf { it.y }, b.maxOf { it.x }, b.maxOf { it.y }), etapa, lejos, azar, quemado, gavillas = m.con("cosecha") && campo > 0.5f)
        }

        // Dos casas de labor, que la guerra va dejando en ruinas, y los olivos: sueltos y en un olivar junto a la costa.
        casa(l, Rect(362f, 141.5f, 373f, 146.5f), CASA_LABOR, 0.5f, m.azar("casa1"), dano = (estragos * 1.6f).coerceIn(0f, 1f))
        casa(l, Rect(240f, 140.5f, 250f, 145.5f), CASA_LABOR, 0.5f, m.azar("casa2"), dano = ((estragos - 0.35f) * 1.6f).coerceIn(0f, 1f))
        val olivos = listOf(Offset(228f, 148f), Offset(252f, 148.5f), Offset(376f, 148f), Offset(302f, 148.5f)) +
            (0 until 11).map { Offset(170f + (it % 4) * 8f + (it / 4) * 3f + az.entre(-1.5f, 1.5f), 152f + (it / 4) * 3.4f + az.entre(-0.6f, 0.6f)) }
        olivos.sortedBy { it.y }.forEach { sombraCorta(r, it, 5f, 4f); olivo(l, it, if (it.x < 210f) 6f else 7f, 0.5f, fijo) }

        ciudad(m, l, r)

        // Los Muros Largos: bajan de la ciudad al puerto, con sus torres.
        val a = Offset(266f, 135f); val b = Offset(214f, 149.5f)
        val lado = if (r.lat >= 0f) -1f else 1f
        if (r.fuerza > 0.05f) drawLine(r.color, b + Offset(lado * 1.6f, 1.1f), a + Offset(lado * 1.6f, 1.1f), 1.6f, alpha = r.fuerza * 0.85f, blendMode = BlendMode.Multiply)
        muralla(l, b, a, 2.2f, CALIZA, 0.5f)
        for (i in 0..5) { val p = b + (a - b) * (i / 5f); drawRect(l.tono(lerp(CALIZA, Color.White, 0.15f), 0.5f), Offset(p.x - 1f, p.y - 3.6f), Size(2f, 3.6f)) }
        // Las chozas de los refugiados, arrimadas al muro.
        val chozas = (m.arco("refugiados") * 11f).roundToInt()
        val az2 = Azar(Semilla.de("heleno", "chozas"))
        for (i in 0 until chozas) {
            val p = b + (a - b) * ((i + 0.5f) / 11f) + Offset(az2.entre(1.5f, 4f), az2.entre(1.2f, 2.6f))
            val w = az2.entre(2.6f, 3.8f)
            drawPath(poligono(p + Offset(-w / 2f, 0f), p + Offset(w / 2f, 0f), p + Offset(w * 0.1f, -w * 0.62f)), l.tono(if (i % 2 == 0) Color(0xFF8A6E4A) else Color(0xFFA4875A), 0.45f))
            drawLine(l.tono(Color(0xFF3A2C20), 0.45f), p + Offset(-w * 0.1f, 0f), p + Offset(-w * 0.1f, -w * 0.3f), 0.5f)
        }
    }

    /** La sombra corta de algo lejano que está de pie en [pie]: una mancha tendida hacia el lado contrario al sol. */
    private fun DrawScope.sombraCorta(r: Rayo, pie: Offset, ancho: Float, alto: Float) {
        if (r.fuerza < 0.05f) return
        val d = (r.ax * alto).coerceIn(-20f, 20f)
        drawOval(r.color, Offset(minOf(pie.x, pie.x + d) - ancho / 2f, pie.y - 0.6f), Size(abs(d) + ancho, maxOf(1f, ancho * 0.3f)), alpha = r.fuerza * 0.85f, blendMode = BlendMode.Multiply)
    }

    /**
     * Atenas, lejos: no es una fila de casas pegada al suelo, sino un caserío que sube por una loma hasta el pie de la
     * roca, con la muralla delante, el Licabeto detrás y una calima que lo despega de los montes. Arriba, la Acrópolis
     * con el Partenón recién terminado y los Propileos.
     */
    private fun DrawScope.ciudad(m: Momento, l: Luces, r: Rayo) {
        val az = Azar(Semilla.de("heleno", "ciudad"))
        val cae = if (r.lat >= 0f) -1f else 1f                                  // hacia dónde caen las sombras
        fun enSombra(c: Color, lejos: Float) = l.tono(mult(c, r.color, 0.25f + 0.65f * r.dia), lejos)
        // El Licabeto, detrás, y la calima.
        val licabeto = curva(listOf(Offset(308f, 131f), Offset(320f, 126.4f), Offset(329f, 119.4f), Offset(333.5f, 118.2f), Offset(338f, 121.4f), Offset(348f, 127.6f), Offset(358f, 131f)), 131.4f)
        drawPath(licabeto, l.tono(Color(0xFF969078), 0.66f))
        clipPath(licabeto) { drawPath(if (cae < 0f) poligono(Offset(333.5f, 118.2f), Offset(300f, 131.4f), Offset(331f, 131.4f)) else poligono(Offset(333.5f, 118.2f), Offset(362f, 131.4f), Offset(336f, 131.4f)), enSombra(Color(0xFF969078), 0.66f).copy(alpha = 0.7f)) }
        mancha(Offset(290f, 129.6f), 70f, 4.6f, l.bruma, 0.18f + 0.32f * r.dia)
        // La loma en que se asienta la ciudad.
        val loma = curva(listOf(Offset(234f, 136.2f), Offset(248f, 133.4f), Offset(264f, 131.2f), Offset(286f, 130.4f), Offset(306f, 131f), Offset(318f, 133f), Offset(328f, 136.2f)), 136.4f)
        drawPath(loma, Brush.verticalGradient(listOf(l.tono(Color(0xFFC2B48C), 0.5f), l.tono(Color(0xFFA9A070), 0.42f)), 130.4f, 136.4f))

        // La roca: el lado de la luz, el de la sombra, las grietas del cantil y el muro que la corona.
        val roca = l.tono(Color(0xFFD2B88C), 0.42f); val rocaSombra = enSombra(Color(0xFFAE9470), 0.42f)
        val penon = Path().apply { moveTo(266f, 131.4f); cubicTo(268.5f, 125f, 271f, 121.6f, 275.4f, 120.6f); lineTo(296.6f, 120f); cubicTo(301f, 121f, 303.5f, 125.6f, 306f, 131.4f); close() }
        drawPath(penon, Brush.horizontalGradient(if (cae < 0f) listOf(rocaSombra, rocaSombra, roca, roca) else listOf(roca, roca, rocaSombra, rocaSombra), 266f, 306f))
        clipPath(penon) {
            repeat(16) { val x = az.entre(267f, 305f); drawLine(enSombra(Color(0xFF8A7458), 0.42f).copy(alpha = 0.55f), Offset(x, az.entre(121f, 125f)), Offset(x + az.entre(-1f, 1f), az.entre(126f, 131f)), 0.4f) }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFF7E7A52), 0.45f).copy(alpha = 0.6f)), 127f, 131.4f), Offset(264f, 127f), Size(44f, 4.6f))     // la maleza del pie
        }
        drawLine(l.tono(Color(0xFFEDE2C8), 0.38f), Offset(274.6f, 120.5f), Offset(297.4f, 119.9f), 0.9f)
        // El Partenón: el basamento, las columnas (se adivinan), el entablamento y el frontón.
        val marmol = l.tono(Color(0xFFFBF6EA), 0.3f); val sombra = enSombra(Color(0xFFB4A68E), 0.35f)
        drawRect(marmol, Offset(279.5f, 119.2f), Size(13f, 0.9f))
        drawRect(sombra, Offset(280.3f, 116.2f), Size(11.4f, 3f))
        for (i in 0..7) drawRect(marmol, Offset(280.3f + i * 1.53f, 116.2f), Size(0.7f, 3f))
        drawRect(marmol, Offset(279.8f, 115.3f), Size(12.4f, 1f))
        drawPath(poligono(Offset(279.6f, 115.3f), Offset(292.4f, 115.3f), Offset(286f, 113.2f)), marmol)
        // Los Propileos, a un lado, más bajos.
        drawRect(marmol, Offset(273f, 118.4f), Size(4.2f, 2.2f)); drawPath(poligono(Offset(272.8f, 118.4f), Offset(277.4f, 118.4f), Offset(275.1f, 117.3f)), marmol)

        // El caserío, de atrás adelante: cada hilera más grande, más nítida y un poco más abajo que la anterior.
        var n = 0
        fun casita(x: Float, base: Float, w: Float, h: Float, lejos: Float, puerta: Boolean) {
            val propio = listOf(Color(0xFFF2E8D2), Color(0xFFE2D2B0), Color(0xFFF6EEDC), Color(0xFFD8C49C))[n % 4]; n++
            if (r.fuerza > 0.05f) drawOval(r.color, Offset(if (cae < 0f) x - w * 0.7f else x + w * 0.5f, base - 0.5f), Size(w * 1.2f, 0.9f), alpha = r.fuerza * 0.8f, blendMode = BlendMode.Multiply)
            drawRect(l.tono(propio, lejos), Offset(x, base - h), Size(w, h))
            drawRect(enSombra(propio, lejos), Offset(if (cae < 0f) x else x + w * 0.62f, base - h), Size(w * 0.38f, h))
            drawPath(poligono(Offset(x - 0.4f, base - h), Offset(x + w + 0.4f, base - h), Offset(x + w * 0.74f, base - h * 1.36f), Offset(x + w * 0.26f, base - h * 1.36f)), l.tono(if (n % 3 == 0) Color(0xFFA85A34) else Color(0xFFC0703E), lejos))
            if (puerta) drawRect(l.tono(Color(0xFF4A3A2C), lejos), Offset(x + w * 0.42f, base - h * 0.55f), Size(maxOf(0.5f, w * 0.16f), h * 0.55f))
            if (l.noche > 0.5f && n % 3 == 1 && m.hacer != Hacer.DORMIR) drawCircle(LUZ_DE_CASA.copy(alpha = 0.85f), 0.45f, Offset(x + w * 0.5f, base - h * 0.5f))
        }
        fun hilera(x0: Float, x1: Float, base: (Float) -> Float, w0: Float, w1: Float, h0: Float, h1: Float, lejos: Float, puerta: Boolean) {
            var x = x0
            while (x < x1) {
                val w = az.entre(w0, w1)
                if (az.real() > 0.2f) casita(x, base(x) + az.entre(-0.4f, 0.4f), w, az.entre(h0, h1), lejos, puerta)
                else cipres(l, Offset(x + w / 2f, base(x) + 0.2f), az.entre(3f, 5f), lejos)                                    // un hueco con su ciprés
                x += w + az.entre(0.2f, 1.4f)
            }
        }
        hilera(250f, 320f, { 129.6f + abs(it - 286f) * 0.03f }, 2f, 3.2f, 1.3f, 2.1f, 0.5f, false)
        hilera(244f, 324f, { 131.5f + abs(it - 286f) * 0.035f }, 2.6f, 4f, 1.7f, 2.7f, 0.44f, false)
        // La muralla de la ciudad, con sus torres, por delante de la hilera más cercana.
        hilera(240f, 322f, { 133.5f + abs(it - 284f) * 0.04f }, 3.2f, 5f, 2.2f, 3.3f, 0.38f, true)
        val pt = listOf(Offset(236f, 136.6f), Offset(252f, 135.9f), Offset(266f, 135.4f), Offset(284f, 135.3f), Offset(304f, 135.8f), Offset(326f, 137f))
        for (i in 0 until pt.size - 1) {
            drawLine(l.tono(CALIZA, 0.42f), pt[i], pt[i + 1], 1.5f)
            drawLine(l.tono(lerp(CALIZA, Color.White, 0.3f), 0.42f), pt[i] + Offset(0f, -0.75f), pt[i + 1] + Offset(0f, -0.75f), 0.4f)
            drawRect(Brush.horizontalGradient(if (cae < 0f) listOf(enSombra(CALIZA, 0.42f), l.tono(CALIZA, 0.42f)) else listOf(l.tono(CALIZA, 0.42f), enSombra(CALIZA, 0.42f)), pt[i].x - 1f, pt[i].x + 1f), Offset(pt[i].x - 1f, pt[i].y - 2.6f), Size(2f, 3.3f))
        }
        if (r.fuerza > 0.05f) drawLine(r.color, pt.first() + Offset(0f, 1.2f), pt.last() + Offset(0f, 1.2f), 0.9f, alpha = r.fuerza * 0.6f, blendMode = BlendMode.Multiply)
    }

    /** Las sombras cortas de la otra orilla: la torre, los cipreses y los cobertizos (cada frontón le da sombra al de al lado). */
    private fun DrawScope.sombrasDelMuelle(r: Rayo) {
        if (r.fuerza < 0.05f) return
        sombraCorta(r, Offset(152f, 155f), 8f, 9f); sombraCorta(r, Offset(352f, 164f), 4f, 12f); sombraCorta(r, Offset(359f, 164.5f), 4f, 9f)
        for (i in 0 until 9) {
            val x = 204f + i * 15.4f; val b = 163f + i * 0.2f
            drawPath(if (r.lat >= 0f) poligono(Offset(x - 0.4f, b - 11f), Offset(x + 15.4f / 2f, b - 16.5f), Offset(x + 15.4f / 2f, b - 11f)) else poligono(Offset(x + 15.8f, b - 11f), Offset(x + 15.4f / 2f, b - 16.5f), Offset(x + 15.4f / 2f, b - 11f)),
                r.color, alpha = r.fuerza * 0.68f, blendMode = BlendMode.Multiply)
        }
    }

    /** La otra orilla: el muelle de piedra, la fila de cobertizos de la flota, la torre de la bocana y las casas del puerto. */
    private fun DrawScope.muelle(m: Momento, l: Luces, fijo: Azar) {
        val lejos = 0.34f
        // La casa del puerto y los cipreses, al fondo a la derecha.
        cipres(l, Offset(352f, 164f), 21f, lejos); cipres(l, Offset(359f, 164.5f), 16f, lejos)
        casa(l, Rect(363f, 154f, 384f, 165f), CASA_PUERTO, lejos, m.azar("casapuerto"), encendida = l.noche > 0.5f && m.hacer != Hacer.DORMIR)
        // La torre de la bocana, con su fuego de guardia de noche.
        val piedra = l.tono(CALIZA, lejos); val piedraSombra = l.tono(lerp(CALIZA, Color(0xFF50483C), 0.4f), lejos)
        drawRect(Brush.horizontalGradient(if (l.solX > 0.4f) listOf(piedraSombra, piedra) else listOf(piedra, piedraSombra), 148f, 156f), Offset(148f, 141f), Size(8f, 14f))
        for (i in 0..2) drawRect(piedra, Offset(147.4f + i * 3.4f, 139.2f), Size(2.2f, 2f))
        drawRect(l.tono(BARNIZ, lejos), Offset(151f, 146f), Size(1.6f, 2.6f))
        // El muelle.
        drawPath(poligono(Offset(196f, 162.5f), Offset(ANCHO + 2f, 164.5f), Offset(ANCHO + 2f, 168.5f), Offset(194f, 166.5f)), Brush.verticalGradient(listOf(l.tono(MARMOL, lejos), l.tono(Color(0xFFA89878), lejos)), 162f, 168.5f))
        // Los cobertizos: en invierno guardan las naves (se ve la proa de cada una); en temporada están vacíos.
        val guardadas = if (enElAgua(m)) 0 else (m.arco("flota") * 9f).roundToInt()
        for (i in 0 until 9) cobertizoLejano(l, 204f + i * 15.4f, 163f + i * 0.2f, 15.4f, ocupado = i < guardadas, adornado = fiesta(m), lejos = lejos, azar = fijo)
    }

    /** Un cobertizo de la fila, visto de frente: dos pilares, el frontón, el tejado y la rampa que baja al agua. */
    private fun DrawScope.cobertizoLejano(l: Luces, x: Float, base: Float, w: Float, ocupado: Boolean, adornado: Boolean, lejos: Float, azar: Azar) {
        val piedra = l.tono(CALIZA, lejos); val luz = l.tono(MARMOL, lejos)
        val teja = l.tono(TERRACOTA, lejos); val tejaOsc = l.tono(TERRA_OSC, lejos)
        val alero = base - 11f; val cumbre = base - 16.5f
        // El hueco oscuro y, dentro, la proa de la nave guardada.
        drawRect(l.tono(Color(0xFF2E2620), lejos * 0.7f), Offset(x + 1.6f, alero), Size(w - 3.2f, 11f))
        if (ocupado) {
            val cx = x + w / 2f
            drawPath(Path().apply {
                moveTo(cx - 3.4f, base - 7.5f); cubicTo(cx - 3.2f, base - 3f, cx - 1.2f, base - 1.2f, cx, base - 0.8f)
                cubicTo(cx + 1.2f, base - 1.2f, cx + 3.2f, base - 3f, cx + 3.4f, base - 7.5f); close()
            }, Brush.verticalGradient(listOf(l.tono(MADERA, lejos), l.tono(BREA, lejos)), base - 7.5f, base - 1f))
            drawLine(l.tono(MADERA_OSC, lejos), Offset(cx, base - 9.6f), Offset(cx, base - 1f), 0.7f)
            drawCircle(l.tono(MARMOL, lejos), 0.55f, Offset(cx - 1.6f, base - 5.6f)); drawCircle(l.tono(MARMOL, lejos), 0.55f, Offset(cx + 1.6f, base - 5.6f))
            drawRect(l.tono(BRONCE, lejos), Offset(cx - 0.6f, base - 1.6f), Size(1.2f, 1.4f))
        }
        // La rampa.
        drawPath(poligono(Offset(x + 2.4f, base), Offset(x + w - 2.4f, base), Offset(x + w - 3.6f, base + 3.4f), Offset(x + 3.6f, base + 3.4f)), l.tono(Color(0xFFB8A888), lejos).copy(alpha = 0.85f))
        // Pilares, frontón y tejado.
        drawRect(piedra, Offset(x + 0.2f, alero), Size(1.6f, 11f)); drawRect(luz, Offset(x + 0.2f, alero), Size(0.6f, 11f))
        drawRect(piedra, Offset(x + w - 1.8f, alero), Size(1.6f, 11f))
        drawPath(poligono(Offset(x - 0.4f, alero), Offset(x + w + 0.4f, alero), Offset(x + w / 2f, cumbre)), luz)
        drawPath(poligono(Offset(x + 1.6f, alero - 0.6f), Offset(x + w - 1.6f, alero - 0.6f), Offset(x + w / 2f, cumbre + 1.5f)), l.tono(lerp(MARMOL, EGEO, 0.35f), lejos))
        drawLine(teja, Offset(x - 0.6f, alero), Offset(x + w / 2f, cumbre - 0.5f), 1.3f, StrokeCap.Round)
        drawLine(tejaOsc, Offset(x + w + 0.6f, alero), Offset(x + w / 2f, cumbre - 0.5f), 1.3f, StrokeCap.Round)
        drawCircle(l.tono(ORO, lejos), 0.7f, Offset(x + w / 2f, cumbre - 1f))
        if (adornado) guirnalda(l, Offset(x + 1.2f, alero + 1f), Offset(x + w - 1.2f, alero + 1f), 2.6f, azar, flores = listOf(ROJO, ORO), grueso = 1f, lejos = lejos)
    }

    /** Las naves amarradas en la dársena, frente a los cobertizos: tantas como diga el arco `flota`. En invierno, ninguna. */
    private fun DrawScope.navesDeLaDarsena(m: Momento, l: Luces) {
        if (!enElAgua(m)) {
            // En invierno solo queda alguna barca de pesca.
            barca(l, Offset(236f, 180f), 16f, 0.25f); barca(l, Offset(318f, 187f), 19f, 0.2f)
            return
        }
        val cuantas = navesAFlote(m)
        val sitios = listOf(
            Triple(Offset(206f, 171.5f), 38f, true), Triple(Offset(326f, 172.5f), 38f, false), Triple(Offset(262f, 172f), 38f, true),
            Triple(Offset(300f, 181f), 46f, false), Triple(Offset(232f, 172f), 38f, false), Triple(Offset(354f, 182f), 46f, true), Triple(Offset(290f, 172.5f), 38f, true),
        )
        // De atrás hacia delante, para que las de delante tapen a las de atrás.
        sitios.take(cuantas).sortedBy { it.first.y }.forEach { (popa, largo, derecha) ->
            trirreme(l, popa, largo, haciaLaDerecha = derecha, propia = false, lejos = 0.3f, conCinta = fiesta(m))
        }
        barca(l, Offset(176f, 176f), 13f, 0.3f)
    }

    /** Una barca de pesca, chica, con su palo. */
    private fun DrawScope.barca(l: Luces, popa: Offset, largo: Float, lejos: Float) {
        val h = largo * 0.16f
        drawPath(Path().apply {
            moveTo(popa.x, popa.y - h); cubicTo(popa.x + largo * 0.2f, popa.y + h * 0.5f, popa.x + largo * 0.8f, popa.y + h * 0.5f, popa.x + largo, popa.y - h * 1.3f)
            lineTo(popa.x + largo * 0.9f, popa.y - h * 0.9f); lineTo(popa.x + largo * 0.1f, popa.y - h * 0.8f); close()
        }, l.tono(MADERA_OSC, lejos))
        drawLine(l.tono(EGEO, lejos), Offset(popa.x + largo * 0.1f, popa.y - h * 0.75f), Offset(popa.x + largo * 0.9f, popa.y - h * 0.85f), 0.6f)
        drawLine(l.tono(MADERA_OSC, lejos), Offset(popa.x + largo * 0.5f, popa.y - h * 0.6f), Offset(popa.x + largo * 0.5f, popa.y - largo * 0.55f), 0.6f)
        drawRect(Brush.verticalGradient(listOf(l.tono(BREA, lejos).copy(alpha = 0.3f), Color.Transparent), popa.y, popa.y + h * 2f), Offset(popa.x + largo * 0.1f, popa.y), Size(largo * 0.8f, h * 2f))
    }

    /** La fila de naves que sale por la bocana hacia mar abierto (o que entra), lejos, con sus remos. */
    private fun DrawScope.flotilla(l: Luces, cuantas: Int, haciaLaIzquierda: Boolean) {
        for (i in 0 until cuantas) {
            val k = i / (cuantas - 1f)
            // Las primeras ya van lejos; las últimas salen de la bocana.
            val x = if (haciaLaIzquierda) 16f + 100f * k else 132f - 100f * k
            val y = H + 4.5f + 15f * k
            val largo = 17f + 17f * k
            trirreme(l, Offset(if (haciaLaIzquierda) x + largo else x, y), largo, haciaLaDerecha = !haciaLaIzquierda, propia = false, remos = true, lejos = 0.5f - 0.3f * k, sencilla = true)
        }
    }

    /**
     * Un trirreme de costado, a flote, con la popa en [popa] (en la línea del agua) y [largo] de eslora: la proa con su
     * espolón de bronce y su ojo pintado, la popa que sube y se curva en abanico, la postiza de los remos y el timón.
     *
     * - [propia]: es el «Aura»: lleva la faja y el ojo azules y un gallardete púrpura en la popa.
     * - [remos]: con los remos fuera y la estela (va remando); sin ellos, está amarrada.
     * - [enSeco]: sacada del agua, se le ve también la obra viva (para el cobertizo).
     * - [sencilla]: para las muy lejanas, sin detalles.
     */
    private fun DrawScope.trirreme(
        l: Luces, popa: Offset, largo: Float, haciaLaDerecha: Boolean, propia: Boolean, lejos: Float = 0f, remos: Boolean = false, enSeco: Boolean = false,
        sencilla: Boolean = false, conCorona: Boolean = false, conCinta: Boolean = false,
    ) {
        val x0 = popa.x; val y = popa.y; val L = largo; val h = largo * 0.07f
        val dibujo: DrawScope.() -> Unit = {
            val brea = l.tono(BREA, lejos); val madera = l.tono(MADERA, lejos); val maderaLuz = l.tono(lerp(MADERA, Color.White, 0.25f), lejos)
            val faja = l.tono(if (propia) EGEO else ROJO, lejos)
            if (!enSeco) {
                // El reflejo, bajo la línea del agua.
                val azr = Azar(Semilla.de("nave", x0.toInt(), y.toInt())); val paso = maxOf(0.7f, h * 0.2f)
                reflejoRoto(x0 + L * 0.05f, x0 + L * 0.98f, y, h * 1.7f, brea, 0.6f, azr, paso)
                if (!sencilla) { reflejoRoto(x0 + L * 0.14f, x0 + L * 0.88f, y + h * 0.9f, h * 1.3f, maderaLuz, 0.34f, azr, paso); reflejoRoto(x0 - L * 0.01f, x0 + L * 0.13f, y + h * 1.2f, h * 2.4f, madera, 0.4f, azr, paso) }
                if (remos) {
                    // La estela.
                    val espuma = l.tono(Color.White, lejos).copy(alpha = 0.6f)
                    for (i in 0..3) drawLine(espuma, Offset(x0 - L * (0.04f + 0.09f * i), y + h * 0.15f * (i % 2)), Offset(x0 - L * (0.1f + 0.09f * i), y + h * 0.15f * (i % 2)), maxOf(0.5f, h * 0.12f), StrokeCap.Round)
                }
            }
            val casco = Path().apply {
                moveTo(x0 + L * 0.06f, y)
                cubicTo(x0, y - h * 0.6f, x0 - L * 0.012f, y - h * 1.7f, x0 + L * 0.03f, y - h * 2.8f)
                cubicTo(x0 + L * 0.055f, y - h * 3.5f, x0 + L * 0.12f, y - h * 3.45f, x0 + L * 0.135f, y - h * 2.85f)
                cubicTo(x0 + L * 0.105f, y - h * 3.0f, x0 + L * 0.078f, y - h * 2.5f, x0 + L * 0.08f, y - h * 1.5f)
                lineTo(x0 + L * 0.11f, y - h * 1.05f)
                cubicTo(x0 + L * 0.35f, y - h * 0.9f, x0 + L * 0.7f, y - h * 0.9f, x0 + L * 0.9f, y - h * 1.1f)
                cubicTo(x0 + L * 0.93f, y - h * 1.35f, x0 + L * 0.948f, y - h * 1.9f, x0 + L * 0.932f, y - h * 2.35f)
                lineTo(x0 + L * 0.957f, y - h * 2.3f)
                cubicTo(x0 + L * 0.985f, y - h * 1.6f, x0 + L * 0.975f, y - h * 0.9f, x0 + L * 0.962f, y - h * 0.5f)
                lineTo(x0 + L * 1.035f, y - h * 0.2f); lineTo(x0 + L * 1.035f, y + h * 0.02f)
                if (enSeco) {
                    lineTo(x0 + L * 0.95f, y + h * 0.1f)
                    cubicTo(x0 + L * 0.85f, y + h * 0.95f, x0 + L * 0.2f, y + h * 0.95f, x0 + L * 0.06f, y)
                }
                close()
            }
            drawPath(casco, Brush.verticalGradient(listOf(madera, brea, brea), y - h * 1.2f, y + h * 0.2f))
            // El espolón de bronce.
            drawPath(poligono(Offset(x0 + L * 0.962f, y - h * 0.5f), Offset(x0 + L * 1.035f, y - h * 0.2f), Offset(x0 + L * 1.035f, y + h * 0.02f), Offset(x0 + L * 0.95f, y + h * 0.02f)), l.tono(BRONCE, lejos))
            if (!sencilla) {
                // La postiza (la caja de los remos), su faja de color y las portas.
                drawPath(poligono(Offset(x0 + L * 0.15f, y - h * 0.98f), Offset(x0 + L * 0.86f, y - h * 0.98f), Offset(x0 + L * 0.87f, y - h * 0.62f), Offset(x0 + L * 0.14f, y - h * 0.62f)), maderaLuz)
                drawLine(faja, Offset(x0 + L * 0.13f, y - h * 0.5f), Offset(x0 + L * 0.9f, y - h * 0.52f), maxOf(0.5f, h * 0.16f))
                val portas = (L / 4.2f).toInt()
                for (i in 0 until portas) drawCircle(brea, maxOf(0.3f, h * 0.07f), Offset(x0 + L * (0.17f + 0.68f * i / (portas - 1f)), y - h * 0.8f))
                // El ojo de la proa.
                val ojo = Offset(x0 + L * 0.918f, y - h * 0.86f)
                drawOval(l.tono(MARMOL, lejos), Offset(ojo.x - h * 0.3f, ojo.y - h * 0.19f), Size(h * 0.6f, h * 0.38f))
                drawCircle(l.tono(if (propia) EGEO else BARNIZ, lejos), h * 0.13f, ojo)
                // El abanico de la popa y el timón.
                for (i in 0..2) drawLine(madera, Offset(x0 + L * 0.03f, y - h * 2.8f), Offset(x0 + L * (0.075f + 0.03f * i), y - h * (3.3f - 0.22f * i)), maxOf(0.4f, h * 0.09f), StrokeCap.Round)
                drawLine(madera, Offset(x0 + L * 0.1f, y - h * 1.1f), Offset(x0 + L * 0.035f, y + h * (if (enSeco) 0.8f else 0.35f)), maxOf(0.5f, h * 0.13f), StrokeCap.Round)
                // La proa: el remate que se curva hacia atrás.
                drawLine(faja, Offset(x0 + L * 0.905f, y - h * 1.12f), Offset(x0 + L * 0.94f, y - h * 2.3f), maxOf(0.4f, h * 0.12f), StrokeCap.Round)
            }
            if (propia) {
                // El gallardete del «Aura».
                val asta = Offset(x0 + L * 0.09f, y - h * 3.3f)
                drawLine(madera, Offset(x0 + L * 0.09f, y - h * 1.3f), asta + Offset(0f, -h * 1.5f), maxOf(0.5f, h * 0.09f))
                drawPath(poligono(asta + Offset(0f, -h * 1.5f), asta + Offset(-L * 0.085f, -h * 1.25f), asta + Offset(-L * 0.045f, -h * 1.05f), asta + Offset(-L * 0.08f, -h * 0.75f), asta + Offset(0f, -h * 0.7f)), l.tono(PURPURA, lejos))
                drawLine(l.tono(ORO, lejos), asta + Offset(0f, -h * 1.1f), asta + Offset(-L * 0.06f, -h * 1.02f), maxOf(0.4f, h * 0.08f))
            }
            if (remos) {
                // Los remos, echados hacia popa, y su salpicadura.
                val cuantos = if (sencilla) 6 else (L / 4.2f).toInt()
                val pala = l.tono(lerp(MADERA, Color.White, 0.35f), lejos)
                for (i in 0 until cuantos) {
                    val x = x0 + L * (0.2f + 0.62f * i / (cuantos - 1f))
                    drawLine(pala, Offset(x, y - h * 0.7f), Offset(x - L * 0.045f, y + h * 0.35f), maxOf(0.35f, h * 0.08f), StrokeCap.Round)
                    if (!sencilla) drawCircle(l.tono(Color.White, lejos).copy(alpha = 0.7f), maxOf(0.3f, h * 0.08f), Offset(x - L * 0.048f, y + h * 0.4f))
                }
            }
            if (conCorona) corona(l, Offset(x0 + L * 0.085f, y - h * 2.1f), h * 0.75f, cinta = ROJO, lejos = lejos)
            if (conCinta) {
                drawLine(l.tono(ORO, lejos), Offset(x0 + L * 0.1f, y - h * 3.2f), Offset(x0 + L * 0.02f, y - h * 2.6f), maxOf(0.4f, h * 0.14f), StrokeCap.Round)
                drawLine(l.tono(ROJO, lejos), Offset(x0 + L * 0.1f, y - h * 3.2f), Offset(x0 + L * 0.04f, y - h * 2.2f), maxOf(0.4f, h * 0.14f), StrokeCap.Round)
            }
        }
        if (haciaLaDerecha) dibujo() else scale(-1f, 1f, popa) { dibujo() }
    }

    /** Esta orilla: la playa, la grada y el cobertizo del «Aura», con la nave dentro en invierno. */
    private fun DrawScope.orillaCercana(m: Momento, l: Luces, est: Estacion, fijo: Azar) {
        val arena = l.tono(ARENA); val arenaOsc = l.tono(lerp(ARENA, Color(0xFF6A5A40), 0.35f))
        drawPath(Path().apply {
            moveTo(-2f, 184f); cubicTo(40f, 180f, 96f, 182f, 132f, 190f)
            cubicTo(146f, 194f, 156f, 199f, 160f, 204f)
            cubicTo(140f, 212f, 90f, 218f, -2f, 224f); close()
        }, Brush.verticalGradient(listOf(arena, arenaOsc), 182f, 222f))
        briznas(l, 0f, 60f, 186f, hierbaDe(est, true), fijo, 3.5f, 2.6f)
        // La línea de mojado, donde la arena toca el agua.
        drawPath(Path().apply { moveTo(132f, 190f); cubicTo(146f, 194f, 156f, 199f, 160f, 204f); cubicTo(140f, 212f, 110f, 215f, 84f, 218f) }, l.tono(Color(0xFF8A7A5A)).copy(alpha = 0.6f), style = Stroke(1.4f, cap = StrokeCap.Round))

        // El cobertizo, de costado: el fondo en sombra, la nave (si está), los pilares y el tejado.
        val piso = { x: Float -> 197f + (x - 6f) * 0.045f }                     // la grada baja hacia el agua
        val alero = { x: Float -> 170f + (x - 2f) * 0.03f }
        drawPath(poligono(Offset(6f, alero(6f)), Offset(130f, alero(130f)), Offset(130f, piso(130f)), Offset(6f, piso(6f))), l.tono(Color(0xFF4A3E34)).copy(alpha = 0.92f))
        // Las vigas de la grada, que siguen hasta el agua.
        drawPath(poligono(Offset(6f, piso(6f)), Offset(150f, piso(150f) + 0.5f), Offset(158f, piso(158f) + 3f), Offset(6f, piso(6f) + 3.4f)), l.tono(lerp(ARENA, MADERA_OSC, 0.35f)))
        for (i in 0..11) { val x = 12f + i * 12.5f; drawLine(l.tono(MADERA_OSC), Offset(x, piso(x) + 0.6f), Offset(x + 3f, piso(x) + 3f), 1.1f, StrokeCap.Round) }

        if (nave(m) == Nave.COBERTIZO) {
            trirreme(l, Offset(12f, piso(12f) - 5.6f), 112f, haciaLaDerecha = true, propia = true, enSeco = true, conCorona = fiesta(m))
            // Los puntales que la sostienen.
            for (i in 0..4) { val x = 26f + i * 22f; drawLine(l.tono(MADERA_OSC), Offset(x, piso(x)), Offset(x + 3f, piso(x) - 6f), 1f, StrokeCap.Round) }
            if (calafateando(m)) {
                // La escalera apoyada en el casco y el caldero de la brea (el fuego y el humo van en la parte viva).
                val pie = Offset(141f, piso(141f) + 2f)
                drawLine(l.tono(MADERA), Offset(100f, piso(100f)), Offset(106f, piso(100f) - 16f), 0.9f); drawLine(l.tono(MADERA), Offset(104f, piso(104f)), Offset(110f, piso(104f) - 16f), 0.9f)
                for (i in 1..4) drawLine(l.tono(MADERA), Offset(100f + i * 1.2f, piso(100f) - i * 3.2f), Offset(104f + i * 1.2f, piso(104f) - i * 3.2f), 0.7f)
                drawOval(l.tono(BARNIZ), Offset(pie.x - 4.5f, pie.y - 6f), Size(9f, 6f)); drawLine(l.tono(BARNIZ), pie + Offset(-3.5f, 0f), pie + Offset(-5f, 2.5f), 0.9f); drawLine(l.tono(BARNIZ), pie + Offset(3.5f, 0f), pie + Offset(5f, 2.5f), 0.9f)
            }
        } else {
            // Vacío: los puntales en el suelo y unos rodillos.
            for (i in 0..3) { val x = 30f + i * 26f; drawLine(l.tono(MADERA_OSC), Offset(x, piso(x) - 0.6f), Offset(x + 12f, piso(x + 12f) - 0.9f), 1.2f, StrokeCap.Round) }
        }
        val piedra = l.tono(CALIZA); val piedraLuz = l.tono(MARMOL); val piedraSombra = l.tono(lerp(CALIZA, Color(0xFF4A4034), 0.4f))
        for (i in 0..5) {
            val x = 8f + i * 24f
            drawRect(Brush.horizontalGradient(if (l.solX > 0.3f) listOf(piedraSombra, piedra, piedraLuz) else listOf(piedraLuz, piedra, piedraSombra), x, x + 5f), Offset(x, alero(x)), Size(5f, piso(x) - alero(x) + 0.6f))
            drawRect(piedraLuz, Offset(x - 0.8f, alero(x)), Size(6.6f, 1.4f))
        }
        // El tejado: el faldón de teja, con sus canales, la cumbrera y las antefijas.
        val teja = l.tono(TERRACOTA); val tejaLuz = l.tono(lerp(TERRACOTA, Color(0xFFFFD9A8), 0.3f)); val tejaOsc = l.tono(TERRA_OSC)
        val faldon = poligono(Offset(0f, alero(0f) + 0.8f), Offset(136f, alero(136f) + 0.8f), Offset(130f, alero(130f) - 11.5f), Offset(4f, alero(4f) - 11.5f))
        drawPath(faldon, Brush.verticalGradient(listOf(tejaLuz, teja, tejaOsc), 158f, 174f))
        var x = 4f
        while (x < 134f) { drawLine(tejaOsc.copy(alpha = 0.6f), Offset(x + 0.5f, alero(x) - 11f), Offset(x - 0.8f, alero(x) + 0.6f), 0.6f); x += 3.6f }
        drawLine(tejaLuz, Offset(4f, alero(4f) - 11.5f), Offset(130f, alero(130f) - 11.5f), 1.6f, StrokeCap.Round)
        x = 2f
        while (x < 136f) { drawCircle(l.tono(lerp(TERRACOTA, Color.White, 0.45f)), 0.9f, Offset(x, alero(x) + 0.9f)); x += 7.2f }
        drawLine(Color.Black.copy(alpha = 0.25f), Offset(2f, alero(2f) + 2.2f), Offset(134f, alero(134f) + 2.2f), 1.2f)
        if (fiesta(m)) for (i in 0..4) { val a = 13f + i * 24f; guirnalda(l, Offset(a, alero(a) + 3f), Offset(a + 19f, alero(a + 19f) + 3f), 5f, m.azar("g", i), flores = listOf(ROJO, ORO, MARMOL), grueso = 1.7f) }
    }

    // ---------------------------------------------------------------- El patio

    private fun DrawScope.patio(m: Momento, l: Luces, r: Rayo, est: Estacion) {
        val az = Azar(Semilla.de("heleno", "patio"))
        vano(l, est, az)
        murete(l, r, az)
        suelo(l, est, az)
        // Lo que cuelga del murete: las guirnaldas de las fiestas, el paño del luto y, si es el objeto del día, la red o el manto.
        if (fiesta(m)) {
            guirnalda(l, Offset(82f, 226.5f), Offset(178f, 226.5f), 10f, m.azar("gm", 1), flores = listOf(ROJO, ORO, MARMOL), grueso = 3.2f)
            guirnalda(l, Offset(238f, 226.5f), Offset(336f, 226.5f), 10f, m.azar("gm", 2), flores = listOf(ROJO, ORO, MARMOL), grueso = 3.2f)
        }
        if (m.con("luto")) pano(l, Offset(296f, CAP), Offset(334f, CAP), 24f, Color(0xFF2C2630))
        if (m.ficha.objeto == "red") red(l)
        if (m.ficha.objeto == "manto") manto(l)
        val cosas = cosasDelPatio(m, l, r).sortedBy { it.pie.y }

        // Las sombras que caen sobre el murete y el suelo: la del alero, la de cada cosa y la del follaje.
        capaSombra(r) { c ->
            val alero = (3.4f * r.tan * 0.55f).coerceIn(0.8f, 5f)
            drawRect(c, Offset(VANO, CARA), Size(ANCHO - VANO, alero))
            var x = VANO
            while (x < ANCHO) { drawOval(c, Offset(x + 1f, CARA + alero - 1.2f), Size(7.5f, 2.4f)); x += 9.5f }
            drawRect(c, Offset(VANO - 16f, 203f), Size(26f, (2.6f * r.tan * 0.5f).coerceIn(0.6f, 3.4f)))      // bajo la losa del pilar
            // La cuerda colgada, pegada al muro.
            translate(r.ax * 1.6f, 1.2f + 0.5f * r.tan) { clipRect(VANO, CARA, ANCHO, BASE) { drawOval(c, Offset(224f, 226f), Size(17f, 21f)) } }
            sombraDelOlivo(r, c, enCosas = false)
            cosas.forEach { sombraArrojada(r, it.forma, it.pie, c, BASE, CAP, VANO) }
            sombraDelRemo(m, r, c)
        }
        cuerdaColgada(l, r)
        cosas.forEach { it.pintar(this) }
        // El follaje también da sombra sobre las cosas.
        capaSombra(r, 0.8f) { c -> cosas.filter { it.bajoElFollaje }.forEach { k -> clipPath(k.forma) { sombraDelOlivo(r, c, enCosas = true) } } }
        higuera(m, l, r, est)
    }

    /** Por el vano del murete se ve la cuesta que baja del patio a la playa, con su senda y dos escalones. */
    private fun DrawScope.vano(l: Luces, est: Estacion, az: Azar) {
        val tierra = l.tono(Color(0xFFCDB487)); val tierraOsc = l.tono(Color(0xFFA88C62))
        drawPath(Path().apply { moveTo(-2f, 213f); cubicTo(20f, 211f, 50f, 215f, 78f, 221f); lineTo(78f, BASE + 2f); lineTo(-2f, BASE + 2f); close() }, Brush.verticalGradient(listOf(tierra, tierraOsc), 212f, BASE))
        drawPath(Path().apply { moveTo(14f, BASE); cubicTo(22f, 238f, 44f, 228f, 38f, 214f); lineTo(50f, 216f); cubicTo(58f, 230f, 44f, 240f, 50f, BASE); close() }, l.tono(Color(0xFFE6D3A8)).copy(alpha = 0.75f))
        val hierba = lerp(hierbaDe(est, true), Color(0xFF8A8A52), 0.3f)
        briznas(l, 0f, 20f, 228f, hierba, az, 7f, 2f); briznas(l, 54f, 70f, 234f, hierba, az, 6f, 2.2f); briznas(l, 0f, 12f, 244f, hierba, az, 8f, 2f)
        repeat(9) { drawCircle(l.tono(if (it % 2 == 0) CALIZA else Color(0xFF9E8E72)), az.entre(0.8f, 1.8f), Offset(az.entre(4f, 60f), az.entre(222f, 240f))) }
        // Los escalones: la huella con luz, el frente en sombra.
        val luz = l.tono(lerp(MARMOL, CALIZA, 0.4f)); val frente = l.tono(mult(CALIZA, Color(0xFF8C8ABA), 0.7f))
        drawPath(poligono(Offset(10f, 240.6f), Offset(58f, 240.6f), Offset(61f, 244f), Offset(6f, 244f)), luz); drawRect(frente, Offset(6f, 244f), Size(55f, 2.6f))
        drawPath(poligono(Offset(4f, 246.6f), Offset(62f, 246.6f), Offset(66f, 250.6f), Offset(-1f, 250.6f)), luz); drawRect(frente, Offset(-1f, 250.6f), Size(67f, 2.4f))
    }

    /** El murete encalado: la cara con sus brochazos, chorreras, desconchones y grietas; el zócalo rojo gastado; el lomo de teja y el pilar del vano. */
    private fun DrawScope.murete(l: Luces, r: Rayo, az: Azar) {
        val x0 = VANO
        val cal = l.tono(ENCALADO); val calLuz = l.tono(lerp(ENCALADO, Color.White, 0.55f)); val calSombra = l.tono(mult(ENCALADO, r.color, 0.8f))
        drawRect(Brush.verticalGradient(listOf(calLuz, cal, lerp(cal, calSombra, 0.32f)), CARA, BASE), Offset(x0, CARA), Size(ANCHO - x0, BASE - CARA))
        clipRect(x0, CARA, ANCHO, BASE) {
            repeat(170) {
                val p = Offset(az.entre(x0, ANCHO), az.entre(CARA, BASE)); val w = az.entre(4f, 16f)
                drawLine((if (az.real() > 0.5f) calLuz else calSombra).copy(alpha = 0.10f + 0.12f * az.real()), p, p + Offset(w, az.entre(-1.2f, 1.2f)), az.entre(0.8f, 2.4f), StrokeCap.Round)
            }
            repeat(18) {
                val x = az.entre(x0, ANCHO); val h = az.entre(5f, 17f)
                drawRect(Brush.verticalGradient(listOf(l.tono(Color(0xFF8C8478)).copy(alpha = 0.24f), Color.Transparent), CARA, CARA + h), Offset(x, CARA), Size(az.entre(0.8f, 2.6f), h))
            }
            // La humedad que sube del suelo.
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFF8A7A62)).copy(alpha = 0.22f)), 236f, BASE), Offset(x0, 236f), Size(ANCHO - x0, BASE - 236f))
        }
        // El zócalo rojo: el borde de arriba, comido; la pintura, saltada.
        val zocalo = Path().apply {
            moveTo(x0, BASE); lineTo(x0, 243.6f)
            var x = x0
            while (x < ANCHO + 6f) { x += az.entre(4f, 10f); lineTo(x, 243.6f + az.entre(-0.9f, 1.1f)) }
            lineTo(ANCHO + 6f, BASE); close()
        }
        drawPath(zocalo, Brush.verticalGradient(listOf(l.tono(lerp(ROJO, ENCALADO, 0.2f)), l.tono(lerp(ROJO, Color(0xFF4A2A22), 0.25f))), 243f, BASE))
        clipPath(zocalo) { repeat(46) { drawOval(cal.copy(alpha = az.entre(0.2f, 0.55f)), Offset(az.entre(x0, ANCHO), az.entre(243.5f, BASE)), Size(az.entre(1.5f, 7f), az.entre(0.7f, 2f))) } }
        clipRect(x0, CARA, ANCHO, BASE) {
            listOf(Triple(128f, 244f, 10f), Triple(272f, 238f, 5.5f), Triple(330f, 246f, 8f), Triple(196f, 247f, 5f)).forEach { (cx, cy, rad) -> desconchon(l, r, Offset(cx, cy), rad, az) }
            repeat(3) {
                var p = Offset(az.entre(x0 + 20f, ANCHO - 20f), CARA); val g = Path().apply { moveTo(p.x, p.y) }
                repeat(6) { p += Offset(az.entre(-3f, 3f), az.entre(2f, 5f)); g.lineTo(p.x, p.y) }
                drawPath(g, l.tono(Color(0xFF6A6058)).copy(alpha = 0.55f), style = Stroke(0.4f))
            }
        }
        // El lomo de teja, que se ve un poco por arriba.
        val teja = l.tono(TERRACOTA); val tejaLuz = l.tono(lerp(TERRACOTA, Color(0xFFFFE0B0), 0.45f)); val tejaOsc = l.tono(mult(TERRA_OSC, r.color, 0.6f))
        drawRect(tejaLuz, Offset(x0 - 1f, CAP), Size(ANCHO - x0 + 2f, 3.2f))
        drawRect(Brush.verticalGradient(listOf(teja, tejaOsc), CAP + 3.2f, CARA + 0.6f), Offset(x0 - 1f, CAP + 3.2f), Size(ANCHO - x0 + 2f, CARA + 0.6f - CAP - 3.2f))
        var x = x0 - 1f
        while (x < ANCHO) {
            drawLine(tejaOsc.copy(alpha = 0.85f), Offset(x, CAP + 0.2f), Offset(x, CARA + 0.5f), 0.7f)
            drawOval(tejaLuz.copy(alpha = 0.6f), Offset(x + (if (r.lat >= 0f) 5.4f else 1.2f), CAP + 3.5f), Size(3f, 1.8f))
            if (az.real() > 0.8f) drawCircle(l.tono(Color(0xFFB8B070)).copy(alpha = 0.7f), az.entre(0.5f, 1.1f), Offset(x + az.entre(1f, 8f), CAP + az.entre(0.6f, 2.6f)))   // liquen
            x += 9.5f
        }
        // El pilar del vano: su cara, su canto (hacia el vano) y la losa de encima.
        val p0 = VANO - 10f; val p1 = VANO + 8f; val pt = 203f
        val canto = if (r.lat >= 0f) lerp(cal, calSombra, 0.6f) else calLuz
        drawPath(poligono(Offset(p0, pt), Offset(p0 - 4.5f, pt + 2.5f), Offset(p0 - 4.5f, BASE + 0.5f), Offset(p0, BASE + 2f)), canto)
        drawRect(Brush.verticalGradient(listOf(calLuz, cal, lerp(cal, calSombra, 0.3f)), pt, BASE), Offset(p0, pt), Size(p1 - p0, BASE + 2f - pt))
        clipRect(p0, pt, p1, BASE + 2f) {
            repeat(22) { val p = Offset(az.entre(p0, p1), az.entre(pt, BASE)); drawLine(calSombra.copy(alpha = 0.16f), p, p + Offset(az.entre(2f, 7f), az.entre(-1f, 1f)), az.entre(0.8f, 2f), StrokeCap.Round) }
            desconchon(l, r, Offset(p0 + 3f, 238f), 5f, az)
        }
        drawRect(l.tono(lerp(ROJO, ENCALADO, 0.2f)), Offset(p0, 243.6f), Size(p1 - p0, BASE + 2f - 243.6f))
        drawPath(poligono(Offset(p0 - 7f, pt), Offset(p1 + 3f, pt), Offset(p1 + 1f, pt - 4.6f), Offset(p0 - 4f, pt - 4.6f)), l.tono(lerp(MARMOL, Color.White, 0.3f)))
        drawRect(l.tono(mult(CALIZA, r.color, 0.5f)), Offset(p0 - 7f, pt), Size(p1 - p0 + 10f, 1.6f))
    }

    /** Un desconchón: donde se cayó el encalado asoma el adobe, con sus hiladas. */
    private fun DrawScope.desconchon(l: Luces, r: Rayo, c: Offset, rad: Float, az: Azar) {
        val pts = (0 until 11).map { val a = it / 11f * 6.283f; val d = rad * az.entre(0.4f, 1.15f); Offset(c.x + cos(a) * d * 1.6f, c.y + sin(a) * d * 0.75f) }
        val forma = curva(pts + pts.first()).apply { close() }
        val junta = l.tono(Color(0xFFA88660))
        drawPath(forma, l.tono(Color(0xFFD6B88E)))
        clipPath(forma) {
            var y = c.y - rad; var fila = 0
            while (y < c.y + rad) {
                drawLine(junta.copy(alpha = 0.7f), Offset(c.x - rad * 2f, y), Offset(c.x + rad * 2f, y), 0.5f)
                var x = c.x - rad * 2f + (fila % 2) * 4f
                while (x < c.x + rad * 2f) { drawLine(junta.copy(alpha = 0.6f), Offset(x, y), Offset(x, y + 3.4f), 0.45f); x += 8f }
                y += 3.4f; fila++
            }
            // El canto del encalado da sombra dentro, arriba y del lado de la luz.
            translate(-r.lat * 1.1f, 1.3f) { drawPath(forma, mult(Color.White, r.color).copy(alpha = 0.55f * (0.4f + 0.6f * r.dia)), style = Stroke(2f), blendMode = BlendMode.Multiply) }
        }
        drawPath(forma, l.tono(Color.White).copy(alpha = 0.55f), style = Stroke(0.45f))
    }

    /** El suelo: tierra apisonada con losas en tres hiladas que se agrandan hacia quien mira; guijarros, hojas y hierba en las juntas. */
    private fun DrawScope.suelo(l: Luces, est: Estacion, az: Azar) {
        val tierra = l.tono(Color(0xFFC9B088)); val tierraOsc = l.tono(Color(0xFFA08660))
        drawRect(Brush.verticalGradient(listOf(tierraOsc, tierra, lerp(tierra, tierraOsc, 0.4f)), BASE, ALTO), Offset(0f, BASE + 1.8f), Size(ANCHO, ALTO - BASE))
        drawRect(tierraOsc, Offset(VANO - 6f, BASE), Size(ANCHO, 2f))
        fun px(x: Float, y: Float) = 190f + (x - 190f) * (0.86f + 0.14f * (y - BASE) / (ALTO - BASE))
        listOf(BASE + 2.4f to 258.2f, 259f to 268.6f, 269.6f to 287f).forEachIndexed { f, (y0, y1) ->
            var x = -az.entre(10f, 40f)
            while (x < ANCHO + 30f) {
                val w = az.entre(24f, 46f) * (0.74f + 0.2f * f)
                if (az.real() > 0.16f) {
                    val a = Offset(px(x + 0.7f, y0), y0); val b = Offset(px(x + w - 0.7f, y0), y0)
                    val c = Offset(px(x + w - 0.7f + az.entre(-1.4f, 1.4f), y1), y1); val d = Offset(px(x + 0.7f + az.entre(-1.4f, 1.4f), y1), y1)
                    val tono = lerp(Color(0xFFD9C9A8), if (az.real() > 0.5f) Color(0xFFC2AE88) else Color(0xFFE8DCC2), az.real() * 0.7f)
                    drawPath(poligono(a, b, c, d), Brush.verticalGradient(listOf(l.tono(lerp(tono, Color(0xFF8E7C62), 0.14f)), l.tono(tono)), y0, y1))
                    drawLine(l.tono(Color.White).copy(alpha = 0.32f), a, b, 0.5f); drawLine(l.tono(Color(0xFF5E4E3A)).copy(alpha = 0.5f), d + Offset(0f, 0.6f), c + Offset(0f, 0.6f), 0.7f)
                    if (az.real() > 0.6f) { val g = a + Offset(az.entre(2f, w * 0.6f), 1f); drawLine(l.tono(Color(0xFF7A6850)).copy(alpha = 0.5f), g, g + Offset(az.entre(3f, 9f), az.entre(2f, y1 - y0 - 2f)), 0.4f) }
                }
                x += w
            }
        }
        repeat(150) { drawCircle((if (az.real() > 0.5f) l.tono(Color(0xFF7E6A4E)) else l.tono(Color(0xFFF0E4C8))).copy(alpha = 0.38f), az.entre(0.3f, 0.9f), Offset(az.entre(0f, ANCHO), az.entre(BASE + 2f, ALTO))) }
        val hierba = lerp(hierbaDe(est, true), Color(0xFF8A8A52), 0.3f)
        listOf(84f to 96f, 228f to 240f, 304f to 322f, 356f to 372f).forEach { (a, b) -> briznas(l, a, b, BASE + 2f, hierba, az, 4.5f, 1.8f) }
        // Hojas de olivo caídas.
        repeat(16) { val p = Offset(az.entre(200f, ANCHO), az.entre(BASE + 4f, ALTO - 2f)); val a = az.entre(0f, 3.14f); drawLine(l.tono(if (it % 2 == 0) Color(0xFF8A9470) else Color(0xFFB0A070)), p, p + Offset(cos(a) * 3.2f, sin(a) * 1.2f), 1f, StrokeCap.Round) }
    }

    /** Todo lo que hay en el patio, cada cosa con su silueta. */
    private fun cosasDelPatio(m: Momento, l: Luces, r: Rayo): List<Cosa> = buildList {
        add(candeleroCosa(m, l))
        add(anforaCosa(l, r, Offset(108f, 266f), 46f, -0.15f)); add(anforaCosa(l, r, Offset(122f, 270f), 40f, 0.13f))
        add(tinajaCosa(l, r, TINAJA, 1.5f))
        add(braseroCosa(m, l))
        add(bancoCosa(m, l, r))
        if (m.ficha.objeto == "remo") add(remoCosa(l, r))
        add(canastoCosa(l, r, Offset(318f, 280f)))
        add(troncoCosa(l, r))
    }

    /** La tinaja grande de barro (pithos), con su tapa de madera corrida. */
    private fun tinajaCosa(l: Luces, r: Rayo, pie: Offset, k: Float): Cosa {
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx * k, pie.y + dy * k)
        val forma = Path().apply {
            moveTo(p(-6f, 0f)); cubicTo(p(-18f, -8f), p(-18f, -29f), p(-8.5f, -35f)); lineTo(p(-10.5f, -36f)); lineTo(p(-10.5f, -39.5f)); lineTo(p(10.5f, -39.5f)); lineTo(p(10.5f, -36f)); lineTo(p(8.5f, -35f))
            cubicTo(p(18f, -29f), p(18f, -8f), p(6f, 0f)); close()
        }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.26f), p(-16f, -2.6f), Size(33f * k, 6f * k))
            volumen(l, r, forma, pie.x - 17f * k, pie.x + 17f * k, Color(0xFFC07848))
            val sombra = l.tono(Color(0xFF7A4226))
            clipPath(forma) {
                // Los cordones en relieve, las marcas del torno y lo que el uso le ha dejado.
                for (y in listOf(-25f, -22.6f)) { drawLine(sombra.copy(alpha = 0.8f), p(-18f, y), p(18f, y), 0.9f * k); drawLine(l.tono(Color(0xFFE8B080)).copy(alpha = 0.6f), p(-18f, y - 0.8f), p(18f, y - 0.8f), 0.4f * k) }
                val az = Azar(Semilla.de("heleno", "tinaja"))
                repeat(14) { val y = az.entre(-34f, -2f); drawLine(sombra.copy(alpha = 0.14f), p(-18f, y), p(18f, y), 0.5f) }
                repeat(9) { mancha(p(az.entre(-14f, 14f), az.entre(-30f, -3f)), az.entre(3f, 8f) * k, az.entre(2f, 6f) * k, if (it % 3 == 0) l.tono(Color(0xFFE6D2B8)) else sombra, 0.22f) }
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFFE2D4BC)).copy(alpha = 0.35f)), p(0f, -7f).y, pie.y), p(-18f, -7f), Size(36f * k, 7f * k))   // el salitre del pie
            }
            drawRoundRect(l.tono(Color(0xFFCB8452)), p(-10.5f, -39.5f), Size(21f * k, 3.6f * k), CornerRadius(1.6f * k))
            drawLine(l.tono(Color(0xFFF0C090)), p(-9f, -39f), p(9f, -39f), 0.5f * k)
            drawOval(l.tono(MADERA_OSC), p(-9f, -42.2f), Size(20f * k, 4f * k)); drawOval(l.tono(MADERA), p(-8f, -42.4f), Size(18f * k, 2.8f * k))
        }
    }

    /** Un ánfora de transporte apoyada: [ladeo] la inclina. */
    private fun anforaCosa(l: Luces, r: Rayo, pie: Offset, alto: Float, ladeo: Float): Cosa {
        val w = alto * 0.2f
        val cima = pie + Offset(ladeo * alto, -alto)
        fun p(k: Float, dx: Float) = Offset(pie.x + (cima.x - pie.x) * k + dx, pie.y + (cima.y - pie.y) * k)
        val forma = Path().apply {
            moveTo(p(0f, 0f)); cubicTo(p(0.25f, -w * 1.2f), p(0.62f, -w * 1.5f), p(0.78f, -w * 0.42f)); lineTo(p(1f, -w * 0.5f)); lineTo(p(1f, w * 0.5f)); lineTo(p(0.78f, w * 0.42f))
            cubicTo(p(0.62f, w * 1.5f), p(0.25f, w * 1.2f), p(0f, 0f)); close()
        }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.22f), Offset(pie.x - w * 1.3f, pie.y - 1.6f), Size(w * 2.8f, 3.6f))
            val x = pie.x + ladeo * alto * 0.5f
            volumen(l, r, forma, x - w * 1.4f, x + w * 1.4f, Color(0xFFCB8A58))
            val sombra = l.tono(Color(0xFF86502E))
            for (s in listOf(-1f, 1f)) drawPath(Path().apply { moveTo(p(0.95f, s * w * 0.42f)); cubicTo(p(0.95f, s * w * 1.25f), p(0.72f, s * w * 1.3f), p(0.68f, s * w * 0.95f)) }, if (s * r.lat > 0f) l.tono(Color(0xFFD89A68)) else sombra, style = Stroke(alto * 0.045f, cap = StrokeCap.Round))
            drawLine(l.tono(Color(0xFFE8B080)), p(1f, -w * 0.55f), p(1f, w * 0.55f), alto * 0.05f, StrokeCap.Round)
            clipPath(forma) { drawLine(sombra.copy(alpha = 0.6f), p(0.6f, -w * 2f), p(0.6f, w * 2f), 0.6f); drawLine(sombra.copy(alpha = 0.6f), p(0.56f, -w * 2f), p(0.56f, w * 2f), 0.4f) }
        }
    }

    /** El brasero, con su olla a la hora de comer. */
    private fun braseroCosa(m: Momento, l: Luces): Cosa {
        val pie = BRASERO; val k = KB
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx * k, pie.y + dy * k)
        val forma = Path().apply {
            moveTo(p(-9f, 0f)); lineTo(p(-7f, -8f)); lineTo(p(-13f, -17f)); lineTo(p(-12f, -19f))
            if (comiendo(m)) { lineTo(p(-9f, -19f)); lineTo(p(-9.5f, -26f)); lineTo(p(-7f, -29.6f)); lineTo(p(7f, -29.6f)); lineTo(p(9.5f, -26f)); lineTo(p(9f, -19f)) }
            lineTo(p(12f, -19f)); lineTo(p(13f, -17f)); lineTo(p(7f, -8f)); lineTo(p(9f, 0f)); lineTo(p(6f, 0f)); lineTo(p(2f, -7f)); lineTo(p(-2f, -7f)); lineTo(p(-6f, 0f)); close()
        }
        return Cosa(forma, pie) {
            // La ceniza derramada alrededor.
            val az = Azar(Semilla.de("heleno", "ceniza"))
            repeat(5) { mancha(p(az.entre(-14f, 16f), az.entre(-1f, 2.4f)), az.entre(4f, 9f) * k, az.entre(1f, 2f) * k, l.tono(Color(0xFF8E8880)), 0.4f) }
            scale(k, k, pie) { brasero(m, l, pie) }
        }
    }

    /** El candelero de bronce con su lucerna. */
    private fun candeleroCosa(m: Momento, l: Luces): Cosa {
        val pie = CANDELERO; val k = KC
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx * k, pie.y + dy * k)
        val forma = Path().apply {
            moveTo(p(-7f, 0f)); lineTo(p(-0.9f, -6f)); lineTo(p(-0.9f, -48f)); lineTo(p(-5f, -49f)); lineTo(p(-4.5f, -55f)); lineTo(p(7f, -54f)); lineTo(p(5f, -49f)); lineTo(p(0.9f, -48f)); lineTo(p(0.9f, -6f)); lineTo(p(7f, 0f)); close()
        }
        return Cosa(forma, pie) { scale(k, k, pie) { candelero(m, l, pie) } }
    }

    /** El banco de piedra, visto un poco desde arriba, con el objeto del día encima. */
    private fun bancoCosa(m: Momento, l: Luces, r: Rayo): Cosa {
        val x0 = 210f; val x1 = 300f; val yt = 251.6f; val yf = 258.4f; val g = 5.6f; val y1 = 280.5f
        val forma = poligono(listOf(Offset(x0 + 4f, yt), Offset(x1 - 4f, yt), Offset(x1, yf), Offset(x1, yf + g), Offset(x1 - 7f, yf + g), Offset(x1 - 7f, y1), Offset(x1 - 21f, y1), Offset(x1 - 21f, yf + g),
            Offset(x0 + 21f, yf + g), Offset(x0 + 21f, y1), Offset(x0 + 7f, y1), Offset(x0 + 7f, yf + g), Offset(x0, yf + g), Offset(x0, yf)))
        return Cosa(forma, Offset((x0 + x1) / 2f, y1)) {
            val piedra = l.tono(MARMOL); val luz = l.tono(lerp(MARMOL, Color.White, 0.55f)); val sombra = l.tono(mult(MARMOL, r.color, 0.85f * r.contraste))
            drawOval(Color.Black.copy(alpha = 0.22f), Offset(x0 + 2f, y1 - 2.4f), Size(x1 - x0 - 4f, 5.4f))
            // Lo de debajo del tablero, en sombra.
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.34f), Color.Transparent), yf + g, yf + g + 10f), Offset(x0 + 8f, yf + g), Size(x1 - x0 - 16f, 10f))
            // Las patas: la cara de delante y el canto que mira al sol (o el que le da la espalda).
            for (px in listOf(x0 + 7f, x1 - 21f)) {
                drawRect(Brush.verticalGradient(listOf(sombra, piedra), yf + g, y1), Offset(px, yf + g), Size(14f, y1 - yf - g))
                drawRect(if (r.lat >= 0f) luz else sombra, Offset(px + 11f, yf + g), Size(3f, y1 - yf - g)); drawRect(if (r.lat >= 0f) sombra else luz, Offset(px, yf + g), Size(2.4f, y1 - yf - g))
            }
            // El tablero: arriba, con toda la luz; el canto, a media luz.
            val tablero = poligono(Offset(x0 + 4f, yt), Offset(x1 - 4f, yt), Offset(x1, yf), Offset(x0, yf))
            drawPath(tablero, Brush.verticalGradient(listOf(piedra, luz), yt, yf))
            val az = Azar(Semilla.de("heleno", "banco"))
            clipPath(tablero) { repeat(7) { val x = az.entre(x0, x1); drawLine(l.tono(Color(0xFFB8AC98)).copy(alpha = 0.5f), Offset(x, yt), Offset(x + az.entre(-14f, 14f), yf), 0.4f) } }
            drawRect(Brush.verticalGradient(listOf(piedra, lerp(piedra, sombra, 0.75f)), yf, yf + g), Offset(x0, yf), Size(x1 - x0, g))
            // Los desportillados del canto.
            repeat(6) { val x = az.entre(x0 + 2f, x1 - 4f); drawPath(poligono(Offset(x, yf), Offset(x + az.entre(1.5f, 4f), yf), Offset(x + 1f, yf + az.entre(0.8f, 1.8f))), sombra) }
            drawLine(luz, Offset(x0, yf), Offset(x1, yf), 0.5f)
            // El objeto del día, encima.
            val sobre = SOBRE
            capaSombra(r) { c -> if (m.ficha.objeto != "remo" && m.ficha.objeto != "red" && m.ficha.objeto != "manto") clipPath(tablero) { drawOval(c, Offset(sobre.x - 20f + r.ax * 4f, sobre.y - 2.6f), Size(40f, 5f)) } }
            scale(KO, KO, sobre) {
                when (m.ficha.objeto) {
                    "cojin" -> cojin(l, sobre)
                    "estrobo" -> estrobo(l, sobre)
                    "anfora" -> anfora(l, sobre + Offset(0f, 0.5f), 27f, 0f)
                    "lampara" -> lucerna(m, l, sobre)
                    "higos" -> higos(l, sobre)
                    "dracma" -> dracmas(l, sobre)
                    "aulos" -> aulos(l, sobre)
                    "copa" -> copa(l, sobre, 1.25f)
                    "pan" -> pan(l, sobre)
                    else -> Unit
                }
                if (conGente(m) && m.ficha.objeto != "copa") copa(l, sobre + Offset(-22f, 0f), 0.8f)
                if (conGente(m)) jarra(l, sobre + Offset(24f, 0.6f))
                if (m.con("luto")) lecito(l, sobre + Offset(16f, 0f))
            }
        }
    }

    private val REMO_A = Offset(341f, 284f); private val REMO_B = Offset(250f, 201f)

    /** El remo del día, apoyado en el murete: el puño en el suelo y la pala asomando por encima del lomo. */
    private fun remoCosa(l: Luces, r: Rayo): Cosa {
        val a = REMO_A; val b = REMO_B
        val d = b - a; val largo = d.getDistance(); val u = d / largo; val n = Offset(-u.y, u.x)
        val c = a + u * (largo * 0.74f)                                         // donde empieza la pala
        val pala = Path().apply {
            moveTo(c + n * 1.2f); cubicTo(c + u * 8f + n * 4.4f, c + u * 22f + n * 4.2f, b); cubicTo(c + u * 22f - n * 4.2f, c + u * 8f - n * 4.4f, c - n * 1.2f); close()
        }
        val forma = Path().apply { addPath(poligono(a + n * 1.4f, c + n * 1.3f, c - n * 1.3f, a - n * 1.4f)); addPath(pala) }
        return Cosa(forma, a) {
            val abeto = l.tono(Color(0xFFDCBC84)); val luz = l.tono(Color(0xFFF6E2B0)); val sombra = l.tono(mult(Color(0xFFB08A52), r.color, 0.6f * r.contraste))
            val arriba = if (r.lat >= 0f) -1f else 1f                            // de qué lado del asta da la luz
            drawLine(sombra, a - n * (0.7f * arriba), c - n * (0.7f * arriba), 2.9f, StrokeCap.Round); drawLine(abeto, a, c, 2.3f, StrokeCap.Round); drawLine(luz, a + n * (0.7f * arriba), c + n * (0.7f * arriba), 0.6f)
            drawPath(pala, Brush.linearGradient(listOf(if (arriba > 0f) luz else sombra, abeto, if (arriba > 0f) sombra else luz), c + n * 4f, c - n * 4f))
            drawLine(sombra.copy(alpha = 0.7f), c + u * 2f, b - u * 2f, 0.5f)
            // El puño, la marca roja de su dueño y el estrobo de cuero.
            drawLine(l.tono(Color(0xFF8A6A3E)), a, a + u * 9f, 3.3f, StrokeCap.Round)
            for (t in listOf(15f, 18.4f)) drawLine(l.tono(ROJO), a + u * t + n * 1.5f, a + u * t - n * 1.5f, 1.4f)
            drawLine(l.tono(CUERO), a + u * 46f + n * 2f, a + u * 46f - n * 2f, 3f, StrokeCap.Round)
            drawPath(Path().apply { moveTo(a + u * 46f - n * 2f); cubicTo(a + u * 44f - n * 8f, a + u * 52f - n * 10f, a + u * 50f - n * 14f) }, l.tono(CUERO), style = Stroke(1.1f, cap = StrokeCap.Round))
        }
    }

    /** La sombra del remo: nace donde toca el lomo del murete y se abre hacia el suelo. */
    private fun DrawScope.sombraDelRemo(m: Momento, r: Rayo, c: Color) {
        if (m.ficha.objeto != "remo") return
        val d = REMO_B - REMO_A
        fun enY(y: Float) = REMO_A + d * ((y - REMO_A.y) / d.y)
        val toca = enY(CAP + 1f); val base = enY(BASE)
        val corre = (r.ax * 9f).coerceIn(-30f, 30f)
        clipRect(VANO, CAP, ANCHO, ALTO) { drawPath(poligono(listOf(toca, toca + Offset(2.6f, 0.6f), base + Offset(corre + 3f, 0f), REMO_A + Offset(2f, 0f), REMO_A + Offset(-2f, 0f), base + Offset(corre, 0f))), c) }
    }

    /** Un canasto de mimbre con un paño dentro. */
    private fun canastoCosa(l: Luces, r: Rayo, pie: Offset): Cosa {
        val w = 15f; val h = 17f
        val forma = Path().apply { moveTo(pie.x - w * 0.8f, pie.y); lineTo(pie.x - w, pie.y - h); lineTo(pie.x + w, pie.y - h); lineTo(pie.x + w * 0.8f, pie.y); close() }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.22f), Offset(pie.x - w, pie.y - 2f), Size(w * 2.1f, 4.4f))
            // El asa, por detrás.
            drawPath(Path().apply { moveTo(pie.x - w * 0.8f, pie.y - h); cubicTo(pie.x - w * 0.7f, pie.y - h * 2.1f, pie.x + w * 0.7f, pie.y - h * 2.1f, pie.x + w * 0.8f, pie.y - h) }, l.tono(Color(0xFF8A6A3C)), style = Stroke(1.8f, cap = StrokeCap.Round))
            // El paño que asoma.
            drawPath(Path().apply { moveTo(pie.x - w * 0.8f, pie.y - h); cubicTo(pie.x - w * 0.4f, pie.y - h - 7f, pie.x + w * 0.3f, pie.y - h - 5f, pie.x + w * 0.85f, pie.y - h); close() }, l.tono(Color(0xFFE6DCC4)))
            drawPath(Path().apply { moveTo(pie.x + w * 0.2f, pie.y - h - 2f); cubicTo(pie.x + w * 0.7f, pie.y - h - 3f, pie.x + w * 1.1f, pie.y - h + 2f, pie.x + w * 1.05f, pie.y - h + 8f); lineTo(pie.x + w * 0.7f, pie.y - h + 6f); close() }, l.tono(Color(0xFFD8CCB0)))
            volumen(l, r, forma, pie.x - w, pie.x + w, Color(0xFFC49A5A))
            clipPath(forma) {
                val raya = l.tono(Color(0xFF7A5A2E))
                var y = pie.y - h + 1.6f; var i = 0
                while (y < pie.y) {
                    drawLine(raya.copy(alpha = 0.55f), Offset(pie.x - w, y), Offset(pie.x + w, y), 0.5f)
                    var x = pie.x - w + (i % 2) * 1.6f
                    while (x < pie.x + w) { drawLine(raya.copy(alpha = 0.45f), Offset(x, y), Offset(x + 0.6f, y + 2.2f), 0.5f); x += 3.2f }
                    y += 2.2f; i++
                }
            }
            drawLine(l.tono(Color(0xFFE2BC7A)), Offset(pie.x - w, pie.y - h), Offset(pie.x + w, pie.y - h), 1.5f, StrokeCap.Round)
        }
    }

    /** Un rollo de cuerda colgado de una estaca en el murete. */
    private fun DrawScope.cuerdaColgada(l: Luces, r: Rayo) {
        val c = Offset(232.5f, 236f)
        val cuerda = l.tono(Color(0xFFCDAE78)); val sombra = l.tono(mult(Color(0xFF9A7A4A), r.color, 0.5f))
        drawRect(l.tono(MADERA_OSC), Offset(c.x - 1.2f, c.y - 11.4f), Size(2.4f, 3f))
        for (i in 0..3) {
            val w = 15f - i * 1.8f; val h = 19.5f - i * 1.4f
            drawOval(sombra, Offset(c.x - w / 2f + 0.5f, c.y - 9.4f + i * 0.5f), Size(w, h), style = Stroke(2f))
            drawOval(cuerda, Offset(c.x - w / 2f, c.y - 10f + i * 0.5f), Size(w, h), style = Stroke(1.5f))
        }
        drawPath(Path().apply { moveTo(c.x + 4f, c.y + 8f); cubicTo(c.x + 7f, c.y + 12f, c.x + 3f, c.y + 14f, c.x + 5f, c.y + 17f) }, cuerda, style = Stroke(1.4f, cap = StrokeCap.Round))
    }

    /** Una hoja de higuera: ancha, de cinco lóbulos. */
    private fun hojaDeHiguera(c: Offset, rad: Float, giro: Float): Path {
        val radios = listOf(1f, 0.6f, 0.95f, 0.55f, 0.8f, 0.3f, 0.8f, 0.55f, 0.95f, 0.6f)
        val pts = radios.mapIndexed { i, k -> val a = giro + i / 10f * 6.283f; c + Offset(sin(a), -cos(a)) * (rad * k) }
        return curva(pts + pts.first()).apply { close() }
    }

    /**
     * La higuera que planta por su padre el 18 de noviembre de la temporada 2: crece con el arco `higuera`, junto al
     * vano del murete. Su alcorque, un cerco de piedras, está desde siempre.
     */
    private fun DrawScope.higuera(m: Momento, l: Luces, r: Rayo, est: Estacion) {
        val v = m.arco("higuera")
        val pie = Offset(32f, 272f)
        drawOval(l.tono(Color(0xFF7A6448)), Offset(pie.x - 13f, pie.y - 3f), Size(26f, 7f))
        for (i in 0..7) { val a = i / 8f * 6.283f; drawCircle(l.tono(if (i % 2 == 0) CALIZA else Color(0xFFB8A888)), 2f, pie + Offset(cos(a) * 13f, sin(a) * 3.4f + 0.5f)) }
        if (v <= 0.01f) return
        val alto = 16f + 118f * v
        val az = Azar(Semilla.de("heleno", "higuera"))
        val hoja = hojaDe(est)?.let { lerp(it, Color(0xFF4E8A3E), 0.5f) }
        capaSombra(r) { c -> drawOval(c, Offset(pie.x - alto * 0.34f + r.ax * alto * 0.2f, pie.y - 2f), Size(alto * 0.68f, alto * 0.09f + 3f)) }
        arbol(l, pie, alto, est, 0f, az, hoja = hoja, tronco = Color(0xFF8A7C6C))
        if (hoja != null) {
            // Las hojas, una a una, por encima de la copa: las de arriba y del lado del sol, más claras.
            val centro = pie + Offset(0f, -alto * 0.68f)
            repeat((30 + 110 * v).toInt()) {
                val a = az.entre(0f, 6.283f); val d = az.entre(0.25f, 1.08f)
                val p = centro + Offset(cos(a) * alto * 0.38f * d, sin(a) * alto * 0.33f * d)
                val luz = ((centro.y - p.y) / (alto * 0.33f) * 0.5f + (p.x - centro.x) / (alto * 0.38f) * 0.5f * r.lat + 0.5f).coerceIn(0f, 1f)
                val color = lerp(mult(hoja, r.color, 0.55f * r.contraste), lerp(hoja, Color(0xFFE6E08A), 0.35f * r.dia), luz * az.entre(0.6f, 1f))
                drawPath(hojaDeHiguera(p, az.entre(3f, 5.2f) * (0.6f + 0.5f * v), az.entre(2.2f, 4f)), l.tono(color))
            }
        }
        // Los higos, a finales del verano, cuando ya es un árbol.
        if (v > 0.55f && est == Estacion.VERANO || v > 0.55f && est == Estacion.OTONO && m.ficha.dia.substring(5) < "10-10") {
            val azh = Azar(Semilla.de("heleno", "higos"))
            repeat(9) { drawCircle(l.tono(HIGO), 1.5f, pie + Offset(azh.entre(-0.3f, 0.3f) * alto, -alto * azh.entre(0.5f, 0.85f))) }
        }
    }

    /** Un ánfora de transporte, de pie, apoyada en el muro; [ladeo] la inclina. */
    private fun DrawScope.anfora(l: Luces, pie: Offset, alto: Float, ladeo: Float) {
        val barro = l.tono(Color(0xFFCB8A58)); val luz = l.tono(Color(0xFFE8B080)); val sombra = l.tono(Color(0xFF86502E))
        val w = alto * 0.2f
        val cima = pie + Offset(ladeo * alto, -alto)
        fun p(k: Float, dx: Float) = Offset(pie.x + (cima.x - pie.x) * k + dx, pie.y + (cima.y - pie.y) * k)
        val cuerpo = Path().apply {
            moveTo(p(0f, 0f).x, p(0f, 0f).y)
            cubicTo(p(0.25f, -w * 1.2f).x, p(0.25f, 0f).y, p(0.62f, -w * 1.5f).x, p(0.62f, 0f).y, p(0.78f, -w * 0.42f).x, p(0.78f, 0f).y)
            lineTo(p(1f, -w * 0.42f).x, p(1f, 0f).y); lineTo(p(1f, w * 0.42f).x, p(1f, 0f).y); lineTo(p(0.78f, w * 0.42f).x, p(0.78f, 0f).y)
            cubicTo(p(0.62f, w * 1.5f).x, p(0.62f, 0f).y, p(0.25f, w * 1.2f).x, p(0.25f, 0f).y, p(0f, 0f).x, p(0f, 0f).y)
            close()
        }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(sombra, barro, luz, barro), pie.x - w * 1.4f + ladeo * alto * 0.5f, pie.x + w * 1.4f + ladeo * alto * 0.5f))
        // Las asas y el labio.
        for (s in listOf(-1f, 1f)) drawPath(Path().apply { moveTo(p(0.95f, s * w * 0.42f).x, p(0.95f, 0f).y); cubicTo(p(0.95f, s * w * 1.25f).x, p(0.95f, 0f).y, p(0.72f, s * w * 1.3f).x, p(0.72f, 0f).y, p(0.68f, s * w * 0.95f).x, p(0.68f, 0f).y) }, sombra, style = Stroke(alto * 0.045f, cap = StrokeCap.Round))
        drawLine(luz, p(1f, -w * 0.55f), p(1f, w * 0.55f), alto * 0.05f, StrokeCap.Round)
    }

    /** El brasero de barro sobre sus tres patas: con la olla al fuego a la hora de comer; con brasas el resto del día. */
    private fun DrawScope.brasero(m: Momento, l: Luces, pie: Offset) {
        val barro = l.tono(Color(0xFFA85C34)); val sombra = l.tono(Color(0xFF6A3620))
        drawOval(Color.Black.copy(alpha = 0.2f), Offset(pie.x - 14f, pie.y - 2f), Size(28f, 5f))
        for (dx in listOf(-9f, 0f, 9f)) drawLine(sombra, pie + Offset(dx * 0.8f, -9f), pie + Offset(dx, 0f), 1.8f, StrokeCap.Round)
        drawPath(Path().apply {
            moveTo(pie.x - 13f, pie.y - 16f); cubicTo(pie.x - 12f, pie.y - 7f, pie.x - 6f, pie.y - 6.5f, pie.x, pie.y - 6.5f)
            cubicTo(pie.x + 6f, pie.y - 6.5f, pie.x + 12f, pie.y - 7f, pie.x + 13f, pie.y - 16f); close()
        }, Brush.horizontalGradient(listOf(sombra, barro, l.tono(Color(0xFFD08458)), barro), pie.x - 13f, pie.x + 13f))
        drawOval(l.tono(Color(0xFF3A2A22)), Offset(pie.x - 13f, pie.y - 18.2f), Size(26f, 4.6f))
        if (comiendo(m)) {
            // La olla (chytra), tiznada, con su tapa.
            val olla = l.tono(Color(0xFF4A3A32))
            drawPath(Path().apply {
                moveTo(pie.x - 7f, pie.y - 28f); cubicTo(pie.x - 11f, pie.y - 24f, pie.x - 9f, pie.y - 17.5f, pie.x, pie.y - 17.5f)
                cubicTo(pie.x + 9f, pie.y - 17.5f, pie.x + 11f, pie.y - 24f, pie.x + 7f, pie.y - 28f); close()
            }, Brush.horizontalGradient(listOf(l.tono(Color(0xFF2A201C)), olla, l.tono(Color(0xFF6A5648))), pie.x - 10f, pie.x + 10f))
            drawOval(l.tono(Color(0xFF6A5648)), Offset(pie.x - 7.5f, pie.y - 29.6f), Size(15f, 3.2f))
            drawCircle(l.tono(Color(0xFF2A201C)), 1.1f, pie + Offset(0f, -30.4f))
        }
    }

    /** El candelero de bronce, con su lucerna arriba: encendida al anochecer, cuando hay gente. */
    private fun DrawScope.candelero(m: Momento, l: Luces, pie: Offset) {
        val bronce = l.tono(BRONCE); val luz = l.tono(Color(0xFFE6BC72))
        drawOval(Color.Black.copy(alpha = 0.2f), Offset(pie.x - 9f, pie.y - 1.5f), Size(18f, 4f))
        for (dx in listOf(-7f, 0f, 7f)) drawLine(bronce, pie + Offset(0f, -6f), pie + Offset(dx, 0f), 1.4f, StrokeCap.Round)
        drawLine(bronce, pie + Offset(0f, -6f), pie + Offset(0f, -48f), 1.5f); drawLine(luz, pie + Offset(-0.5f, -6f), pie + Offset(-0.5f, -48f), 0.5f)
        drawOval(bronce, Offset(pie.x - 5f, pie.y - 50f), Size(10f, 2.6f))
        // La lucerna de barro.
        drawPath(Path().apply {
            moveTo(pie.x - 4.5f, pie.y - 52f); cubicTo(pie.x - 4.5f, pie.y - 55.5f, pie.x + 3f, pie.y - 55.5f, pie.x + 3.5f, pie.y - 53.5f)
            lineTo(pie.x + 7f, pie.y - 53.6f); lineTo(pie.x + 7f, pie.y - 52.4f); lineTo(pie.x + 3.5f, pie.y - 50.2f); lineTo(pie.x - 3.5f, pie.y - 50.2f); close()
        }, l.tono(Color(0xFFB86A3C)))
    }

    // ---------------------------------------------------------------- Los doce objetos del día

    /** El cojín de remero: una almohadilla de cuero engrasado, con sus correas. */
    private fun DrawScope.cojin(l: Luces, sobre: Offset) {
        val cuero = l.tono(CUERO); val luz = l.tono(Color(0xFFC49260)); val sombra = l.tono(Color(0xFF5E3C20))
        drawRoundRect(Brush.verticalGradient(listOf(luz, cuero, sombra), sobre.y - 9f, sobre.y), Offset(sobre.x - 15f, sobre.y - 9f), Size(30f, 9f), CornerRadius(4.2f))
        drawLine(sombra, Offset(sobre.x - 12f, sobre.y - 4.5f), Offset(sobre.x + 12f, sobre.y - 4.5f), 0.7f)
        for (dx in listOf(-8f, 0f, 8f)) drawCircle(sombra, 0.8f, Offset(sobre.x + dx, sobre.y - 4.5f))
        drawPath(Path().apply { moveTo(sobre.x + 14f, sobre.y - 5f); cubicTo(sobre.x + 22f, sobre.y - 6f, sobre.x + 22f, sobre.y + 3f, sobre.x + 18f, sobre.y + 9f) }, sombra, style = Stroke(1.3f, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(sobre.x - 14f, sobre.y - 5f); cubicTo(sobre.x - 21f, sobre.y - 5f, sobre.x - 22f, sobre.y + 2f, sobre.x - 19f, sobre.y + 7f) }, sombra, style = Stroke(1.3f, cap = StrokeCap.Round))
    }

    /** El estrobo: la lazada de cuero que ata el remo a su tolete, enrollada. */
    private fun DrawScope.estrobo(l: Luces, sobre: Offset) {
        val cuero = l.tono(CUERO); val sombra = l.tono(Color(0xFF5E3C20))
        drawOval(sombra, Offset(sobre.x - 11f, sobre.y - 6.2f), Size(22f, 6.4f), style = Stroke(2.6f))
        drawOval(cuero, Offset(sobre.x - 10f, sobre.y - 7.2f), Size(20f, 6f), style = Stroke(2.2f))
        drawOval(l.tono(Color(0xFFC49260)), Offset(sobre.x - 7f, sobre.y - 8.4f), Size(15f, 4.6f), style = Stroke(1.6f))
        drawPath(Path().apply { moveTo(sobre.x + 9f, sobre.y - 3f); cubicTo(sobre.x + 16f, sobre.y - 2f, sobre.x + 17f, sobre.y + 4f, sobre.x + 14f, sobre.y + 10f) }, cuero, style = Stroke(1.8f, cap = StrokeCap.Round))
        // El tolete de madera, al lado.
        drawRoundRect(l.tono(MADERA_OSC), Offset(sobre.x - 19f, sobre.y - 9f), Size(3.4f, 9f), CornerRadius(1.5f))
    }

    /** La lucerna de barro: encendida si ya es de noche y no duerme. */
    private fun DrawScope.lucerna(m: Momento, l: Luces, sobre: Offset) {
        drawPath(Path().apply {
            moveTo(sobre.x - 9f, sobre.y - 2.5f); cubicTo(sobre.x - 10f, sobre.y - 8.5f, sobre.x + 4f, sobre.y - 8.5f, sobre.x + 6f, sobre.y - 5f)
            lineTo(sobre.x + 13f, sobre.y - 5.2f); lineTo(sobre.x + 13f, sobre.y - 2.8f); lineTo(sobre.x + 6f, sobre.y); lineTo(sobre.x - 7f, sobre.y); close()
        }, Brush.verticalGradient(listOf(l.tono(Color(0xFFD08A58)), l.tono(Color(0xFF8A4A2A))), sobre.y - 8f, sobre.y))
        drawOval(l.tono(BARNIZ), Offset(sobre.x - 4f, sobre.y - 7.6f), Size(5f, 1.8f))
        drawPath(Path().apply { moveTo(sobre.x - 9f, sobre.y - 4f); cubicTo(sobre.x - 14f, sobre.y - 5f, sobre.x - 14f, sobre.y - 1f, sobre.x - 9f, sobre.y - 1.4f) }, l.tono(Color(0xFF8A4A2A)), style = Stroke(1.2f))
        if (luces(m).noche > 0.3f && m.hacer != Hacer.DORMIR && !enElMar(m)) resplandor(sobre + Offset(13f, -7f), 34f, 0.5f * luces(m).noche)
    }

    /** La red de pesca, tendida a secar sobre el murete: la malla en rombos, los corchos arriba y los plomos abajo. */
    private fun DrawScope.red(l: Luces) {
        val hilo = l.tono(Color(0xFF5E4E32)).copy(alpha = 0.8f)
        val x0 = 268f; val x1 = 336f; val arriba = CAP + 2f
        // El borde de abajo cuelga en ondas.
        val abajo = { x: Float -> 241f + 2.4f * sin((x - x0) * 0.21f) }
        val contorno = Path().apply {
            moveTo(x0, arriba); lineTo(x1, arriba)
            var x = x1
            while (x >= x0) { lineTo(x, abajo(x)); x -= 3f }
            close()
        }
        clipPath(contorno) {
            var x = x0 - 30f
            while (x < x1 + 30f) { drawLine(hilo, Offset(x, arriba), Offset(x + 14f, arriba + 28f), 0.5f); drawLine(hilo, Offset(x, arriba), Offset(x - 14f, arriba + 28f), 0.5f); x += 4.6f }
        }
        drawLine(l.tono(Color(0xFF4A3C26)), Offset(x0 - 2f, arriba - 0.4f), Offset(x1 + 2f, arriba - 0.4f), 1.2f, StrokeCap.Round)
        for (i in 0..5) drawOval(l.tono(Color(0xFFC49A62)), Offset(x0 + 3f + i * 12.4f, arriba - 2.2f), Size(5f, 3f))
        var x = x0 + 2f
        while (x < x1) { drawCircle(l.tono(Color(0xFF6E7078)), 1f, Offset(x, abajo(x) + 0.6f)); x += 8.4f }
    }

    /** Un cuenco de barniz negro con higos. */
    private fun DrawScope.higos(l: Luces, sobre: Offset) {
        val higo = l.tono(HIGO); val luz = l.tono(Color(0xFF8A6A9A)); val verde = l.tono(Color(0xFF7A9A4A))
        listOf(Offset(-7f, -9f), Offset(0f, -11f), Offset(7f, -9f), Offset(-3.5f, -13.5f), Offset(3.5f, -14f)).forEachIndexed { i, d ->
            val p = sobre + d
            drawPath(Path().apply { moveTo(p.x, p.y - 4f); cubicTo(p.x + 4.4f, p.y - 1f, p.x + 3.6f, p.y + 3.4f, p.x, p.y + 3.4f); cubicTo(p.x - 3.6f, p.y + 3.4f, p.x - 4.4f, p.y - 1f, p.x, p.y - 4f); close() }, if (i == 3) verde else higo)
            drawCircle(luz.copy(alpha = 0.7f), 0.9f, p + Offset(-1.2f, -0.6f))
        }
        drawPath(Path().apply {
            moveTo(sobre.x - 13f, sobre.y - 8.5f); cubicTo(sobre.x - 11f, sobre.y - 2.5f, sobre.x - 5f, sobre.y - 2f, sobre.x - 3f, sobre.y - 2f)
            lineTo(sobre.x - 4f, sobre.y); lineTo(sobre.x + 4f, sobre.y); lineTo(sobre.x + 3f, sobre.y - 2f)
            cubicTo(sobre.x + 5f, sobre.y - 2f, sobre.x + 11f, sobre.y - 2.5f, sobre.x + 13f, sobre.y - 8.5f); close()
        }, Brush.horizontalGradient(listOf(l.tono(BARNIZ), l.tono(Color(0xFF4A3A30)), l.tono(BARNIZ)), sobre.x - 13f, sobre.x + 13f))
        drawLine(l.tono(TERRACOTA), Offset(sobre.x - 12f, sobre.y - 7f), Offset(sobre.x + 12f, sobre.y - 7f), 0.8f)
    }

    /** Unas dracmas de plata, con la lechuza. */
    private fun DrawScope.dracmas(l: Luces, sobre: Offset) {
        val plata = l.tono(Color(0xFFCDD0D4)); val luz = l.tono(Color(0xFFF2F4F6)); val sombra = l.tono(Color(0xFF7E848C))
        listOf(Offset(-9f, -2.2f) to false, Offset(1f, -2.6f) to false, Offset(9f, -5.2f) to true).forEach { (d, dePie) ->
            val c = sobre + d
            if (dePie) {
                // Una apoyada de canto contra otra: se le ve la lechuza.
                drawCircle(sombra, 5.4f, c + Offset(0.5f, 0.4f)); drawCircle(plata, 5f, c); drawCircle(luz, 5f, c, style = Stroke(0.6f))
                drawOval(sombra, Offset(c.x - 2.2f, c.y - 2.4f), Size(4.4f, 5.4f)); drawCircle(luz, 0.8f, c + Offset(-1f, -1f)); drawCircle(luz, 0.8f, c + Offset(1f, -1f))
            } else {
                drawOval(sombra, Offset(c.x - 5.2f, c.y - 1.1f), Size(10.4f, 3.6f)); drawOval(plata, Offset(c.x - 5.2f, c.y - 2f), Size(10.4f, 3.6f)); drawOval(luz, Offset(c.x - 3.4f, c.y - 1.6f), Size(5f, 1.6f))
            }
        }
    }

    /** El aulós: la flauta doble que marca el ritmo del remo. */
    private fun DrawScope.aulos(l: Luces, sobre: Offset) {
        val cana = l.tono(Color(0xFFD8BC84)); val sombra = l.tono(Color(0xFF7A5E36))
        for (s in listOf(-1f, 1f)) {
            val a = Offset(sobre.x - 4f, sobre.y - 3f + s * 0.4f); val b = Offset(sobre.x + 24f, sobre.y - 3f + s * 2.6f)
            drawLine(sombra, a + Offset(0f, 0.8f), b + Offset(0f, 0.8f), 2f, StrokeCap.Round); drawLine(cana, a, b, 1.7f, StrokeCap.Round)
            for (i in 1..4) drawCircle(sombra, 0.5f, a + (b - a) * (0.3f + 0.15f * i))
            drawCircle(l.tono(BRONCE), 1.3f, b)
        }
        // La boquilla doble y la correa (forbeia).
        drawLine(sombra, Offset(sobre.x - 10f, sobre.y - 2.6f), Offset(sobre.x - 4f, sobre.y - 3f), 1f, StrokeCap.Round)
        drawPath(Path().apply { moveTo(sobre.x - 10f, sobre.y - 2.6f); cubicTo(sobre.x - 20f, sobre.y - 9f, sobre.x - 22f, sobre.y - 1f, sobre.x - 14f, sobre.y - 0.6f) }, l.tono(CUERO), style = Stroke(1.2f, cap = StrokeCap.Round))
    }

    /** Una copa de beber (kílix): ancha, baja, de barniz negro, con sus dos asas. */
    private fun DrawScope.copa(l: Luces, sobre: Offset, k: Float) {
        val negro = lerp(l.tono(BARNIZ), Color(0xFF3A2A22), 0.5f * l.noche); val luz = lerp(l.tono(Color(0xFF5A4A40)), LLAMA, 0.45f * l.noche)
        drawPath(Path().apply {
            moveTo(sobre.x - 11f * k, sobre.y - 9f * k); cubicTo(sobre.x - 9f * k, sobre.y - 4.6f * k, sobre.x - 3f * k, sobre.y - 4.2f * k, sobre.x - 1.2f * k, sobre.y - 4.2f * k)
            lineTo(sobre.x - 1.2f * k, sobre.y - 1.2f * k); lineTo(sobre.x - 4.5f * k, sobre.y); lineTo(sobre.x + 4.5f * k, sobre.y); lineTo(sobre.x + 1.2f * k, sobre.y - 1.2f * k)
            lineTo(sobre.x + 1.2f * k, sobre.y - 4.2f * k); cubicTo(sobre.x + 3f * k, sobre.y - 4.2f * k, sobre.x + 9f * k, sobre.y - 4.6f * k, sobre.x + 11f * k, sobre.y - 9f * k); close()
        }, Brush.horizontalGradient(listOf(negro, luz, negro), sobre.x - 11f * k, sobre.x + 11f * k))
        for (s in listOf(-1f, 1f)) drawPath(Path().apply { moveTo(sobre.x + s * 9.5f * k, sobre.y - 7.6f * k); cubicTo(sobre.x + s * 15f * k, sobre.y - 9f * k, sobre.x + s * 15f * k, sobre.y - 5f * k, sobre.x + s * 8f * k, sobre.y - 5.6f * k) }, negro, style = Stroke(1f * k, cap = StrokeCap.Round))
        drawLine(l.tono(TERRACOTA), Offset(sobre.x - 9.6f * k, sobre.y - 7.6f * k), Offset(sobre.x + 9.6f * k, sobre.y - 7.6f * k), 0.7f * k)
    }

    /** La jarra del vino (enócoe). */
    private fun DrawScope.jarra(l: Luces, sobre: Offset) {
        val negro = lerp(l.tono(BARNIZ), Color(0xFF3A2A22), 0.5f * l.noche); val luz = lerp(l.tono(Color(0xFF5A4A40)), LLAMA, 0.45f * l.noche)
        drawPath(Path().apply {
            moveTo(sobre.x - 2.6f, sobre.y); cubicTo(sobre.x - 8f, sobre.y - 3f, sobre.x - 7f, sobre.y - 11f, sobre.x - 2f, sobre.y - 13f)
            lineTo(sobre.x - 3f, sobre.y - 17f); lineTo(sobre.x + 3.6f, sobre.y - 16.4f); lineTo(sobre.x + 2f, sobre.y - 13f)
            cubicTo(sobre.x + 7f, sobre.y - 11f, sobre.x + 8f, sobre.y - 3f, sobre.x + 2.6f, sobre.y); close()
        }, Brush.horizontalGradient(listOf(negro, luz, negro), sobre.x - 7f, sobre.x + 7f))
        drawPath(Path().apply { moveTo(sobre.x + 3f, sobre.y - 16f); cubicTo(sobre.x + 10f, sobre.y - 16f, sobre.x + 9f, sobre.y - 9f, sobre.x + 5.6f, sobre.y - 8f) }, negro, style = Stroke(1.2f, cap = StrokeCap.Round))
        drawLine(l.tono(TERRACOTA), Offset(sobre.x - 5.6f, sobre.y - 8f), Offset(sobre.x + 5.6f, sobre.y - 8f), 0.8f)
    }

    /** El manto (himation) de lana, doblado sobre el murete, con su cenefa. */
    private fun DrawScope.manto(l: Luces) {
        val lana = Color(0xFFC8A45A)
        pano(l, Offset(276f, CAP), Offset(324f, CAP), 23f, lana)
        drawLine(l.tono(PURPURA), Offset(279f, 238.5f), Offset(322f, 237f), 1.6f)
        drawRect(l.tono(lerp(lana, Color.White, 0.2f)), Offset(274f, CAP - 1.6f), Size(52f, 3.2f))
    }

    /** El pan de cebada: dos hogazas en una tabla, con su cuchillo. */
    private fun DrawScope.pan(l: Luces, sobre: Offset) {
        drawRoundRect(l.tono(MADERA), Offset(sobre.x - 18f, sobre.y - 2f), Size(36f, 2.2f), CornerRadius(1f))
        val corteza = l.tono(Color(0xFFB8824A)); val miga = l.tono(Color(0xFFE2C088)); val sombra = l.tono(Color(0xFF7A522A))
        for ((dx, w) in listOf(-7f to 16f, 8f to 13f)) {
            val c = Offset(sobre.x + dx, sobre.y - 2f)
            drawPath(Path().apply { moveTo(c.x - w / 2f, c.y); cubicTo(c.x - w / 2f, c.y - w * 0.62f, c.x + w / 2f, c.y - w * 0.62f, c.x + w / 2f, c.y); close() }, Brush.verticalGradient(listOf(miga, corteza, sombra), c.y - w * 0.5f, c.y))
            drawLine(sombra.copy(alpha = 0.6f), c + Offset(-w * 0.2f, -w * 0.36f), c + Offset(w * 0.2f, -w * 0.36f), 0.7f); drawLine(sombra.copy(alpha = 0.6f), c + Offset(0f, -w * 0.44f), c + Offset(0f, -w * 0.1f), 0.7f)
        }
        drawLine(l.tono(Color(0xFF9AA0A8)), Offset(sobre.x + 16f, sobre.y - 3f), Offset(sobre.x + 24f, sobre.y - 3.6f), 1.1f, StrokeCap.Round); drawLine(l.tono(MADERA_OSC), Offset(sobre.x + 24f, sobre.y - 3.6f), Offset(sobre.x + 28f, sobre.y - 3.9f), 1.6f, StrokeCap.Round)
    }

    /** El lécito de fondo blanco, el vaso de los entierros, con una cinta oscura. */
    private fun DrawScope.lecito(l: Luces, sobre: Offset) {
        val blanco = l.tono(Color(0xFFF2ECDC)); val negro = l.tono(BARNIZ)
        drawRect(negro, Offset(sobre.x - 2.6f, sobre.y - 1.4f), Size(5.2f, 1.4f))
        drawPath(Path().apply {
            moveTo(sobre.x - 2f, sobre.y - 1.4f); cubicTo(sobre.x - 4.4f, sobre.y - 5f, sobre.x - 4f, sobre.y - 15f, sobre.x - 3.4f, sobre.y - 17f)
            lineTo(sobre.x + 3.4f, sobre.y - 17f); cubicTo(sobre.x + 4f, sobre.y - 15f, sobre.x + 4.4f, sobre.y - 5f, sobre.x + 2f, sobre.y - 1.4f); close()
        }, Brush.horizontalGradient(listOf(l.tono(Color(0xFFC8C0AC)), blanco, l.tono(Color(0xFFC8C0AC))), sobre.x - 4f, sobre.x + 4f))
        drawPath(poligono(Offset(sobre.x - 3.4f, sobre.y - 17f), Offset(sobre.x + 3.4f, sobre.y - 17f), Offset(sobre.x + 1.1f, sobre.y - 19.4f), Offset(sobre.x - 1.1f, sobre.y - 19.4f)), negro)
        drawRect(negro, Offset(sobre.x - 1.1f, sobre.y - 24f), Size(2.2f, 4.8f)); drawOval(negro, Offset(sobre.x - 2.6f, sobre.y - 25.6f), Size(5.2f, 2f))
        drawPath(Path().apply { moveTo(sobre.x + 1f, sobre.y - 23f); cubicTo(sobre.x + 5.4f, sobre.y - 23f, sobre.x + 5f, sobre.y - 17.6f, sobre.x + 3f, sobre.y - 17f) }, negro, style = Stroke(0.8f))
        drawLine(l.tono(Color(0xFF3A2A44)), Offset(sobre.x - 4f, sobre.y - 11f), Offset(sobre.x + 4f, sobre.y - 11f), 1.3f)
        drawLine(l.tono(Color(0xFF3A2A44)), Offset(sobre.x + 3f, sobre.y - 11f), Offset(sobre.x + 6f, sobre.y - 5f), 1.1f, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- El olivo que enmarca el cuadro

    private fun mezcla(a: Float, b: Float, k: Float) = a + (b - a) * k

    /** Hasta dónde cuelga la copa del olivo en cada punto del cuadro: deja una ventana para el sol de la mañana. */
    private fun cuelga(x: Float): Float {
        val p = listOf(178f to 4f, 230f to 24f, 270f to 38f, 296f to 44f, 324f to 48f, 345f to 80f, 392f to 114f)
        if (x <= p.first().first) return p.first().second
        for (i in 0 until p.size - 1) if (x <= p[i + 1].first) return mezcla(p[i].second, p[i + 1].second, (x - p[i].first) / (p[i + 1].first - p[i].first))
        return p.last().second
    }

    private fun formaDelTronco() = Path().apply {
        moveTo(332f, 292f); cubicTo(344f, 268f, 331f, 240f, 341f, 210f); cubicTo(347f, 188f, 337f, 166f, 344f, 144f); lineTo(344f, 126f)
        cubicTo(338f, 108f, 324f, 76f, 304f, 54f); cubicTo(288f, 40f, 258f, 24f, 224f, 13f); lineTo(225f, 10f)
        cubicTo(262f, 18f, 294f, 32f, 314f, 50f); cubicTo(330f, 66f, 342f, 88f, 348f, 104f)
        cubicTo(347f, 76f, 342f, 44f, 335f, 6f); lineTo(340f, 5f); cubicTo(349f, 42f, 354f, 76f, 356f, 102f)
        cubicTo(364f, 86f, 376f, 68f, 390f, 52f); lineTo(390f, 74f); cubicTo(376f, 92f, 368f, 114f, 365f, 138f)
        cubicTo(360f, 166f, 369f, 190f, 363f, 214f); cubicTo(357f, 242f, 375f, 266f, 383f, 292f); close()
    }

    /** El tronco del olivo que crece en el patio, a la derecha: retorcido, con su corteza y sus huecos. */
    private fun troncoCosa(l: Luces, r: Rayo): Cosa {
        val forma = formaDelTronco()
        return Cosa(forma, Offset(357f, 290f), bajoElFollaje = false) {
            val propio = Color(0xFF93806C)
            val luz = l.tono(lerp(propio, Color(0xFFFFE9C4), 0.5f * r.contraste)); val medio = l.tono(propio); val sombra = l.tono(mult(propio, r.color, 0.8f * r.contraste))
            drawPath(forma, Brush.horizontalGradient(if (r.lat >= 0f) listOf(sombra, medio, medio, luz, medio) else listOf(medio, luz, medio, medio, sombra), 336f, 376f))
            val az = Azar(Semilla.de("heleno", "corteza"))
            val osc = l.tono(Color(0xFF4A3C34)); val cla = l.tono(Color(0xFFD2C2A8))
            clipPath(forma) {
                // Las ramas, vistas desde abajo, quedan a contraluz.
                drawRect(Brush.verticalGradient(listOf(osc.copy(alpha = 0.6f), Color.Transparent), 20f, 150f), Offset(200f, 0f), Size(190f, 150f))
                // La corteza: vetas largas que siguen la torsión del tronco.
                repeat(70) {
                    val x = az.entre(334f, 380f); val y = az.entre(120f, 285f); val h = az.entre(10f, 38f); val tuerce = az.entre(-6f, 6f)
                    drawPath(Path().apply { moveTo(x, y); cubicTo(x + tuerce, y + h * 0.3f, x - tuerce, y + h * 0.7f, x + tuerce * 0.4f, y + h) }, (if (it % 3 == 0) cla else osc).copy(alpha = az.entre(0.2f, 0.5f)), style = Stroke(az.entre(0.5f, 1.6f), cap = StrokeCap.Round))
                }
                // Un hueco del tronco viejo y un nudo.
                drawOval(osc.copy(alpha = 0.9f), Offset(346f, 198f), Size(7f, 14f)); drawOval(cla.copy(alpha = 0.5f), Offset(345f, 197f), Size(9f, 16f), style = Stroke(0.8f))
                drawOval(osc.copy(alpha = 0.6f), Offset(354f, 250f), Size(5f, 8f))
            }
        }
    }

    /** Una ramilla de olivo con sus hojas en pares. [fleco], de 0 (dentro de la copa, a contraluz) a 1 (en el borde, donde les da el sol). */
    private fun DrawScope.ramilla(l: Luces, r: Rayo, desde: Offset, angulo: Float, largo: Float, az: Azar, k: Float = 1f, fleco: Float = 0.5f) {
        val fin = desde + Offset(cos(angulo) * largo, sin(angulo) * largo)
        val comba = Offset(-sin(angulo), cos(angulo)) * (largo * az.entre(-0.2f, 0.2f))
        fun en(t: Float): Offset { val q = 1f - t; return desde * (q * q) + (desde + (fin - desde) * 0.5f + comba) * (2f * q * t) + fin * (t * t) }
        val verde = l.tono(Color(0xFF4C5E44)); val medio = l.tono(Color(0xFF6E8660)); val plata = l.tono(Color(0xFFA9B99A)); val sol = l.tono(lerp(Color(0xFF93A878), Color(0xFFE2DE9C), 0.55f * r.dia))
        drawPath(Path().apply { moveTo(desde); val c = desde + (fin - desde) * 0.5f + comba; cubicTo(c, c, fin) }, l.tono(Color(0xFF5A5044)), style = Stroke(0.5f * k, cap = StrokeCap.Round))
        var t = 0.1f; var lado = 1f
        while (t <= 1f) {
            val p = en(t); val a = angulo + lado * az.entre(0.3f, 0.62f); val n = az.entre(3.4f, 5.2f) * k
            val suerte = az.real() - 0.3f * (fleco - 0.5f)
            val c = when { suerte < 0.42f -> verde; suerte < 0.78f -> medio; suerte < 0.93f || lado * r.lat < 0f -> plata; else -> sol }
            drawLine(c, p, p + Offset(cos(a) * n, sin(a) * n), 1.25f * k, StrokeCap.Round)
            t += 1.5f * k / largo; lado = -lado
        }
    }

    /** La copa del olivo, que entra por arriba y por la derecha: masas oscuras al fondo y ramillas colgando sobre el cielo. */
    private fun DrawScope.follajeDelOlivo(l: Luces, r: Rayo) {
        val az = Azar(Semilla.de("heleno", "olivo"))
        val oscuro = l.tono(Color(0xFF38483A)); val medio = l.tono(Color(0xFF4E6248))
        repeat(54) {
            val x = az.entre(196f, 396f); val y = az.entre(-12f, cuelga(x) * 0.6f); val rad = az.entre(7f, 17f)
            drawOval(if (it % 3 == 0) medio else oscuro, Offset(x - rad * 1.3f, y - rad * 0.7f), Size(rad * 2.6f, rad * 1.4f))
        }
        repeat(230) {
            val x = az.entre(182f, 394f); val tope = cuelga(x); val y = az.entre(-6f, tope * 0.78f)
            ramilla(l, r, Offset(x, y), 1.57f + az.entre(-1.0f, 1.0f), az.entre(6f, 16f).coerceAtMost(maxOf(6f, tope * 0.36f)), az, fleco = (y / tope).coerceIn(0f, 1f))
        }
        // Unos claros: el cielo que se cuela entre las hojas.
        repeat(22) { val x = az.entre(210f, 384f); val y = az.entre(0f, cuelga(x) * 0.55f); drawOval(lerp(l.medio, l.bajo, 0.4f).copy(alpha = 0.6f), Offset(x, y), Size(az.entre(1.6f, 4.4f), az.entre(1f, 2.6f))) }
    }

    /**
     * La sombra de la copa del olivo: cae debajo de ella a mediodía y se va corriendo hacia el otro lado del sol cuando
     * está bajo. [enCosas]: la que cae sobre las cosas del patio (más arriba que el murete).
     */
    private fun DrawScope.sombraDelOlivo(r: Rayo, c: Color, enCosas: Boolean) {
        val az = Azar(Semilla.de("heleno", "sombra del olivo"))
        val cx = 330f - r.lat * 150f
        clipRect(if (enCosas) 0f else VANO, if (enCosas) 150f else CAP, ANCHO, ALTO) { sombraDeFollaje(az, Rect(cx - 100f, 200f, cx + 100f, ALTO + 14f), r, c) }
    }

    // ---------------------------------------------------------------- La parte viva

    override fun seMueve(m: Momento): Boolean = true                          // el agua siempre brilla

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        val l = luces(m)
        // El viento viene del mar: tumba los humos hacia la izquierda del cuadro. Con el día en calma queda una brisa.
        val viento = -(if (m.ficha.clima == Clima.DESPEJADO) 0.25f else m.viento)
        destellos(l, Rect(0f, H + 1f, ANCHO, 216f), reloj, m.semilla("destellos"))

        // Los humos de la guerra y de la peste, al fondo.
        if (m.con("humo")) {
            humo(l, Offset(346f, 139f), 78f, 30f, reloj, viento * 0.7f, 0.85f, 0.35f, 1, 12, 1.25f)
            humo(l, Offset(372f, 136f), 62f, 24f, reloj, viento * 0.7f, 0.8f, 0.4f, 2, 11, 1.1f)
            humo(l, Offset(312f, 137f), 50f, 20f, reloj, viento * 0.7f, 0.7f, 0.45f, 3, 10)
            if (l.noche > 0.3f) { resplandor(Offset(350f, 137f), 26f, 0.6f * l.noche, Color(0xFFFF6A2A)); resplandor(Offset(374f, 135f), 18f, 0.5f * l.noche, Color(0xFFFF6A2A)) }
        }
        val piras = piras(m)
        for (i in 0 until piras.coerceAtMost(6)) {
            val pie = Offset(238f + i * 7f, 134.4f - (i % 2) * 1.2f)
            humo(l, pie, 40f + 7f * (i % 3), 9f, reloj, viento * 0.5f, 0.7f, 0.3f, 10 + i, 8, 1.15f)
            drawCircle(LLAMA.copy(alpha = 0.55f + 0.4f * l.noche), 0.8f, pie)
            if (l.noche > 0.3f) resplandor(pie, 5f, 0.6f * l.noche, Color(0xFFFF6A2A))
        }

        // El fuego de guardia de la torre de la bocana, de noche, y su reflejo.
        if (l.noche > 0.4f) {
            llama(Offset(152f, 139.4f), 3.4f, reloj, 5); resplandor(Offset(152f, 137.6f), 12f, 0.6f * l.noche)
            reflejo(Offset(152f, 156.5f), 14f, LLAMA, 0.6f * l.noche, reloj, 5)
        }
        // Las antorchas del muelle en las fiestas de noche, cada una con su reflejo.
        if (antorchas(m)) for (i in 0..5) {
            val pie = Offset(212f + i * 27.6f, 164f + i * 0.2f)
            antorcha(l, pie, 5.4f, reloj, 0.2f, i)
            reflejo(pie + Offset(0f, 4f), 16f, LLAMA, 0.55f * maxOf(l.noche, 0.3f), reloj, i)
        }

        // El caldero de la brea, en invierno, a sus horas de trabajo.
        if (calafateando(m)) {
            val pie = Offset(141f, 205f)
            llama(pie + Offset(0f, 0.6f), 5f, reloj, 9)
            humo(l, pie + Offset(0f, -6f), 46f, 13f, reloj, viento, 0.9f, 0f, 7, 9)
        }

        // El brasero: la llama y el humo a la hora de comer; brasas después.
        if (comiendo(m)) {
            llama(BRASERO + Offset(-6f * KB, -17f * KB), 7f * KB, reloj, 1); llama(BRASERO + Offset(6f * KB, -17f * KB), 6f * KB, reloj, 2)
            resplandor(BRASERO + Offset(0f, -18f * KB), 34f * KB, 0.25f + 0.5f * l.noche)
            humo(l, BRASERO + Offset(0f, -30f * KB), 70f, 17f, reloj, viento, 0.12f, 0f, 4, 10, 0.8f)
        } else if (!enElMar(m)) {
            brasas(l, BRASERO + Offset(0f, -16f * KB), 16f * KB, reloj)
            if (l.noche > 0.3f) resplandor(BRASERO + Offset(0f, -16f * KB), 34f * KB, 0.5f * l.noche, Color(0xFFFF5A1E))
        }
        // La lucerna del candelero, que alumbra el muro, y la del banco.
        if (conGente(m) && l.noche > 0.15f) { llama(CANDELERO + Offset(7f * KC, -53.4f * KC), 4.6f * KC, reloj, 3); resplandor(CANDELERO + Offset(7f * KC, -56f * KC), 58f, 0.55f * l.noche) }
        if (m.ficha.objeto == "lampara" && l.noche > 0.3f && m.hacer != Hacer.DORMIR && !enElMar(m)) llama(SOBRE + Offset(13f * KO, -5.2f * KO), 4f * KO, reloj, 4)

        tiempoVivo(l, m.ficha.clima, reloj, H, m.ficha.semilla)
    }

    // ---------------------------------------------------------------- El gato

    /** Al gato le llega la misma luz que a la escena, con su eclipse. */
    override fun luzDelGato(m: Momento, en: Offset): LuzGato { val l = luces(m); return LuzGato(Rayo(l), l.noche) { l.tono(it) } }

    override fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 18f, 279f, hasta = 330f))
        add(Ancla(TipoAncla.MIRADOR, 300f, CAP))
        add(Ancla(TipoAncla.OBJETO, 255f, 282f))
        add(Ancla(TipoAncla.ABRIGO, 255f, 279f))                              // debajo del banco
        if (comiendo(m)) add(Ancla(TipoAncla.FUEGO, 164f, 283f))
        if (fiesta(m)) add(Ancla(TipoAncla.ADORNO, 128f, 252f))
    }
}
