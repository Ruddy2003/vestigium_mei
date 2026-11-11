package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Casa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LLAMA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.LUZ_DE_CASA
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Techo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.agua
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.antorcha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.arbol
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.astros
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.brasas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.briznas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.casa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cielo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cipres
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.corona
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.destellos
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.guirnalda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hierbaDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.hojaDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.monte
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.muralla
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nubes
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.olivo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.pano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.perfil
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sembrado
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
 * Helénico: el puerto de Zea, en el Pireo, visto desde el patio de la casa de Demeas (remero de un trirreme, Atenas,
 * 431 a 427 a. C.). **[Aproximación]**: es una composición con lo que había allí, no la vista desde un punto real.
 *
 * - **Al fondo:** el golfo con Salamina a la izquierda; a la derecha, el llano del Ática con sus campos de cebada, los
 *   Muros Largos que suben hacia la ciudad, la roca de la Acrópolis con el Partenón recién terminado y, detrás, los
 *   montes.
 * - **En medio:** la dársena. En la otra orilla, la fila de cobertizos de la flota; en esta, a la izquierda, el
 *   cobertizo del «Aura», la nave de Demeas.
 * - **Delante:** el patio: el murete encalado, el banco de piedra con el objeto del día, el brasero, la tinaja, el
 *   candelero y, desde la temporada 2, la higuera que planta por su padre.
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
 * - **El tramo del día:** el brasero encendido con la olla a la hora de comer; el candelero y la copa al anochecer,
 *   cuando hay gente; brasas y todo apagado mientras duerme.
 */
internal object EscenaHeleno : EscenaVida {
    override val vidas = listOf("remero")

    /** Dónde está el horizonte del mar. */
    private const val H = 128f

    // Los materiales, con los colores del tema (los de su casa en `ui/vida/VidaHeleno.kt`).
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

    // ---------------------------------------------------------------- Lo que la ficha dice

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
        val l = luces(m)
        val est = m.ficha.estacion
        val semilla = m.ficha.semilla
        // El perfil de la tierra es siempre el mismo: su azar no depende del día.
        val fijo = Azar(Semilla.de("heleno", "tierra"))

        // 1. Cielo y lejanía.
        cielo(l, H)
        astros(l, H, semilla)
        nubes(l, H, m.ficha.clima, semilla)
        monte(l, perfil(fijo, 146f, 660f, H + 2f, 52f, 0.6f, 40, isla = true), H + 3f, Color(0xFF566C92), 0.82f)
        monte(l, perfil(fijo, 190f, 600f, H + 2f, 30f, 0.35f, 36, isla = true), H + 3f, Color(0xFF76845E), 0.6f)
        agua(l, Rect(0f, H, ANCHO, 236f), m.azar("mar"), viento = m.viento)
        monte(l, perfil(fijo, -30f, 126f, H + 0.6f, 17f, 0.5f, 16, isla = true), H + 0.8f, Color(0xFF5C7078), 0.78f)
        monte(l, perfil(fijo, 122f, 164f, H + 0.6f, 6f, 0.3f, 8, isla = true), H + 0.8f, Color(0xFF66786E), 0.7f)
        if (m.con("partida")) flotilla(l, 5, haciaLaIzquierda = true)
        if (m.con("regreso")) flotilla(l, 5, haciaLaIzquierda = false)
        llano(m, l, est, fijo)

        // 2. Plano medio: la otra orilla con sus cobertizos, la dársena y, en esta orilla, el cobertizo del «Aura».
        muelle(m, l, fijo)
        navesDeLaDarsena(m, l)
        orillaCercana(m, l, est, fijo)
        when (nave(m)) {
            Nave.REMANDO -> trirreme(l, Offset(250f, 190f), 82f, haciaLaDerecha = false, propia = true, remos = true, lejos = 0.1f)
            Nave.AMARRADA -> trirreme(l, Offset(152f, 208f), 102f, haciaLaDerecha = true, propia = true, lejos = 0f, conCorona = fiesta(m))
            Nave.COBERTIZO -> Unit                                              // la dibuja `orillaCercana`, dentro del cobertizo
            Nave.AUSENTE -> Unit
        }

        // 3. Primer plano: el patio.
        patio(m, l, est, fijo)

        // 5 y 7. El velo del tiempo que hace.
        tiempoQuieto(l, m.ficha.clima, H, semilla)
    }

    /** El llano del Ática: la costa, los campos, las casas de labor, los Muros Largos, la ciudad y la Acrópolis. */
    private fun DrawScope.llano(m: Momento, l: Luces, est: Estacion, fijo: Azar) {
        val hierba = hierbaDe(est, mediterraneo = true)
        val tierra = Path().apply {
            moveTo(178f, H - 0.5f); lineTo(ANCHO + 2f, H - 0.5f); lineTo(ANCHO + 2f, 168f); lineTo(196f, 166f)
            cubicTo(170f, 165f, 150f, 162f, 141f, 155f)
            cubicTo(140f, 149f, 152f, 145f, 160f, 139f)
            cubicTo(166f, 134f, 172f, 131f, 178f, H - 0.5f)
            close()
        }
        drawPath(tierra, Brush.verticalGradient(listOf(l.tono(hierba, 0.7f), l.tono(hierba, 0.45f), l.tono(lerp(hierba, ARENA, 0.5f), 0.3f)), H, 168f))

        // Los campos: una colcha de bancales, cada uno con su grano. Los quemados, según los estragos.
        val campo = m.arco("campo"); val estragos = m.arco("estragos")
        val azar = m.azar("campos")
        val bancales = listOf(
            listOf(Offset(196f, 132f), Offset(232f, 131f), Offset(238f, 138f), Offset(192f, 139f)),
            listOf(Offset(300f, 131f), Offset(342f, 131f), Offset(352f, 138f), Offset(304f, 138f)),
            listOf(Offset(346f, 131f), Offset(382f, 131f), Offset(382f, 139f), Offset(356f, 138f)),
            listOf(Offset(186f, 140f), Offset(222f, 139.5f), Offset(224f, 147f), Offset(178f, 148f)),
            listOf(Offset(256f, 139f), Offset(300f, 139f), Offset(306f, 147f), Offset(258f, 147.5f)),
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

        // Dos casas de labor, que la guerra va dejando en ruinas, y sus olivos.
        casa(l, Rect(362f, 141.5f, 373f, 146.5f), CASA_LABOR, 0.5f, m.azar("casa1"), dano = (estragos * 1.6f).coerceIn(0f, 1f))
        casa(l, Rect(240f, 140.5f, 250f, 145.5f), CASA_LABOR, 0.5f, m.azar("casa2"), dano = ((estragos - 0.35f) * 1.6f).coerceIn(0f, 1f))
        listOf(Offset(228f, 148f), Offset(252f, 148.5f), Offset(376f, 148f), Offset(302f, 148.5f)).forEach { olivo(l, it, 7f, 0.5f, fijo) }

        // La ciudad al pie de la roca, la Acrópolis y el Partenón.
        val roca = l.tono(Color(0xFFCDB48A), 0.42f); val rocaSombra = l.tono(Color(0xFF8E7658), 0.42f)
        repeat(13) {
            val x = 258f + it * 4.3f + fijo.entre(-1f, 1f); val w = fijo.entre(2.6f, 4.4f); val h = fijo.entre(1.6f, 3f)
            drawRect(l.tono(if (it % 3 == 0) Color(0xFFD8C8A8) else Color(0xFFEFE4CC), 0.45f), Offset(x, 131.5f - h), Size(w, h))
            drawRect(l.tono(Color(0xFFB8683A), 0.45f), Offset(x - 0.3f, 131.5f - h - 0.7f), Size(w + 0.6f, 0.8f))
            if (l.noche > 0.5f && it % 3 == 1 && m.hacer != Hacer.DORMIR) drawCircle(LUZ_DE_CASA.copy(alpha = 0.8f), 0.45f, Offset(x + w / 2f, 131f - h / 2f))
        }
        drawPath(Path().apply {
            moveTo(268f, 130.5f); cubicTo(270f, 124f, 272f, 121.5f, 276f, 121f); lineTo(296f, 120.4f)
            cubicTo(300f, 121f, 302f, 125f, 304f, 130.5f); close()
        }, Brush.horizontalGradient(if (l.solX > 0.75f) listOf(rocaSombra, roca) else listOf(roca, rocaSombra), 268f, 304f))
        // El Partenón: el basamento, las columnas (se adivinan), el entablamento y el frontón.
        val marmol = l.tono(Color(0xFFFBF6EA), 0.3f); val sombra = l.tono(Color(0xFF9C8E78), 0.35f)
        drawRect(marmol, Offset(279.5f, 119.6f), Size(13f, 0.9f))
        drawRect(sombra, Offset(280.3f, 116.6f), Size(11.4f, 3f))
        for (i in 0..7) drawRect(marmol, Offset(280.3f + i * 1.53f, 116.6f), Size(0.7f, 3f))
        drawRect(marmol, Offset(279.8f, 115.7f), Size(12.4f, 1f))
        drawPath(poligono(Offset(279.6f, 115.7f), Offset(292.4f, 115.7f), Offset(286f, 113.6f)), marmol)
        // Los Propileos, a un lado, más bajos.
        drawRect(marmol, Offset(273f, 118.8f), Size(4.2f, 2.2f)); drawPath(poligono(Offset(272.8f, 118.8f), Offset(277.4f, 118.8f), Offset(275.1f, 117.7f)), marmol)

        // Los Muros Largos: bajan de la ciudad al puerto, con sus torres.
        val a = Offset(266f, 132.5f); val b = Offset(214f, 149.5f)
        muralla(l, b, a, 2.2f, CALIZA, 0.5f)
        for (i in 0..5) { val p = b + (a - b) * (i / 5f); drawRect(l.tono(lerp(CALIZA, Color.White, 0.15f), 0.5f), Offset(p.x - 1f, p.y - 3.6f), Size(2f, 3.6f)) }
        // Las chozas de los refugiados, arrimadas al muro.
        val chozas = (m.arco("refugiados") * 11f).roundToInt()
        val az = Azar(Semilla.de("heleno", "chozas"))
        for (i in 0 until chozas) {
            val p = b + (a - b) * ((i + 0.5f) / 11f) + Offset(az.entre(1.5f, 4f), az.entre(1.2f, 2.6f))
            val w = az.entre(2.6f, 3.8f)
            drawPath(poligono(p + Offset(-w / 2f, 0f), p + Offset(w / 2f, 0f), p + Offset(w * 0.1f, -w * 0.62f)), l.tono(if (i % 2 == 0) Color(0xFF8A6E4A) else Color(0xFFA4875A), 0.45f))
            drawLine(l.tono(Color(0xFF3A2C20), 0.45f), p + Offset(-w * 0.1f, 0f), p + Offset(-w * 0.1f, -w * 0.3f), 0.5f)
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
                drawRect(Brush.verticalGradient(listOf(brea.copy(alpha = 0.42f), Color.Transparent), y, y + h * 1.5f), Offset(x0 + L * 0.04f, y), Size(L * 0.96f, h * 1.5f))
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

    /** El patio de la casa: el murete, el suelo, la higuera, la tinaja, el brasero, el banco con el objeto del día y el candelero. */
    private fun DrawScope.patio(m: Momento, l: Luces, est: Estacion, fijo: Azar) {
        // El olivo de detrás del murete, a la derecha.
        olivo(l, Offset(360f, 228f), 62f, 0f, fijo)

        // El murete encalado, con su albardilla de teja y su zócalo rojo.
        val cal = l.tono(ENCALADO); val calSombra = l.tono(lerp(ENCALADO, Color(0xFF5A5468), 0.28f))
        drawRect(Brush.verticalGradient(listOf(cal, lerp(cal, calSombra, 0.6f)), 224f, 258f), Offset(0f, 224f), Size(ANCHO, 34f))
        repeat(26) { val px = fijo.entre(0f, ANCHO); val py = fijo.entre(226f, 250f); drawLine(calSombra.copy(alpha = 0.22f), Offset(px, py), Offset(px + fijo.entre(3f, 9f), py + fijo.entre(-0.6f, 0.6f)), 0.6f) }
        drawRect(l.tono(ROJO), Offset(0f, 251f), Size(ANCHO, 7f))
        drawRect(l.tono(lerp(ROJO, Color.Black, 0.3f)), Offset(0f, 257.2f), Size(ANCHO, 1f))
        drawRect(Brush.verticalGradient(listOf(l.tono(lerp(TERRACOTA, Color(0xFFFFD9A8), 0.3f)), l.tono(TERRACOTA)), 219.5f, 224.5f), Offset(0f, 219.5f), Size(ANCHO, 5f))
        var x = 0f
        while (x < ANCHO) { drawLine(l.tono(TERRA_OSC).copy(alpha = 0.7f), Offset(x, 219.8f), Offset(x, 224.4f), 0.6f); x += 9.5f }
        drawRect(Color.Black.copy(alpha = 0.16f), Offset(0f, 224.5f), Size(ANCHO, 1.6f))

        // El suelo de tierra apisonada, con unas losas.
        val suelo = l.tono(Color(0xFFC8B28A)); val sueloOsc = l.tono(Color(0xFF9E8862))
        drawRect(Brush.verticalGradient(listOf(sueloOsc, suelo, lerp(suelo, sueloOsc, 0.5f)), 258f, ALTO), Offset(0f, 258f), Size(ANCHO, ALTO - 258f))
        repeat(9) {
            val cx = 20f + it * 42f + fijo.entre(-8f, 8f); val cy = fijo.entre(266f, 281f); val w = fijo.entre(22f, 34f)
            drawOval(l.tono(Color(0xFFD8C6A2)).copy(alpha = 0.55f), Offset(cx - w / 2f, cy - 2.2f), Size(w, 4.4f))
        }

        // Los adornos del murete.
        if (fiesta(m)) {
            guirnalda(l, Offset(64f, 226.5f), Offset(168f, 226.5f), 11f, m.azar("gm", 1), flores = listOf(ROJO, ORO, MARMOL), grueso = 3.2f)
            guirnalda(l, Offset(252f, 226.5f), Offset(338f, 226.5f), 10f, m.azar("gm", 2), flores = listOf(ROJO, ORO, MARMOL), grueso = 3.2f)
        }
        if (m.con("luto")) pano(l, Offset(262f, 219f), Offset(304f, 219f), 26f, Color(0xFF2C2630))

        higuera(m, l, est)
        tinaja(l, Offset(96f, 268f))
        brasero(m, l, Offset(136f, 270f))
        banco(m, l)
        candelero(m, l, Offset(336f, 270f))
        // Dos ánforas arrimadas al muro.
        anfora(l, Offset(292f, 268f), 30f, -0.16f); anfora(l, Offset(306f, 269f), 26f, 0.1f)
    }

    /** La higuera que planta por su padre el 18 de noviembre de la temporada 2: crece con el arco `higuera`. */
    private fun DrawScope.higuera(m: Momento, l: Luces, est: Estacion) {
        val v = m.arco("higuera")
        val pie = Offset(34f, 266f)
        // El alcorque, que está desde siempre: antes de la higuera, vacío.
        drawOval(l.tono(Color(0xFF7A6448)), Offset(pie.x - 13f, pie.y - 3f), Size(26f, 7f))
        for (i in 0..7) { val a = i / 8f * 6.283f; drawCircle(l.tono(if (i % 2 == 0) CALIZA else Color(0xFFB8A888)), 2f, pie + Offset(cos(a) * 13f, sin(a) * 3.4f + 0.5f)) }
        if (v <= 0.01f) return
        val alto = 16f + 118f * v
        arbol(l, pie, alto, est, 0f, Azar(Semilla.de("heleno", "higuera")), hoja = hojaDe(est)?.let { lerp(it, Color(0xFF4E8A3E), 0.5f) }, tronco = Color(0xFF8A7C6C))
        // Los higos, a finales del verano, cuando ya es un árbol.
        if (v > 0.55f && est == Estacion.VERANO || v > 0.55f && est == Estacion.OTONO && m.ficha.dia.substring(5) < "10-10") {
            val az = Azar(Semilla.de("heleno", "higos"))
            repeat(9) { drawCircle(l.tono(HIGO), 1.5f, pie + Offset(az.entre(-0.3f, 0.3f) * alto, -alto * az.entre(0.5f, 0.85f))) }
        }
    }

    /** Una tinaja grande de barro (pithos), medio tapada. */
    private fun DrawScope.tinaja(l: Luces, pie: Offset) {
        val barro = l.tono(Color(0xFFC07848)); val luz = l.tono(Color(0xFFE2A070)); val sombra = l.tono(Color(0xFF7A4226))
        drawOval(Color.Black.copy(alpha = 0.22f), Offset(pie.x - 15f, pie.y - 2.5f), Size(32f, 6f))
        val cuerpo = Path().apply {
            moveTo(pie.x - 6f, pie.y); cubicTo(pie.x - 17f, pie.y - 8f, pie.x - 17f, pie.y - 28f, pie.x - 8f, pie.y - 35f)
            lineTo(pie.x + 8f, pie.y - 35f); cubicTo(pie.x + 17f, pie.y - 28f, pie.x + 17f, pie.y - 8f, pie.x + 6f, pie.y); close()
        }
        drawPath(cuerpo, Brush.horizontalGradient(if (l.solX > 0.3f) listOf(sombra, barro, luz, barro) else listOf(barro, luz, barro, sombra), pie.x - 15f, pie.x + 15f))
        drawRoundRect(barro, Offset(pie.x - 10f, pie.y - 38.5f), Size(20f, 4.5f), CornerRadius(2f))
        drawLine(sombra, Offset(pie.x - 13f, pie.y - 24f), Offset(pie.x + 13f, pie.y - 24f), 1f); drawLine(sombra, Offset(pie.x - 13.5f, pie.y - 21.5f), Offset(pie.x + 13.5f, pie.y - 21.5f), 0.6f)
        // La tapa de madera, corrida.
        drawOval(l.tono(MADERA_OSC), Offset(pie.x - 9f, pie.y - 41f), Size(19f, 4f))
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
        if (conGente(m) && luces(m).noche > 0.15f) resplandor(pie + Offset(7f, -56f), 46f, 0.5f * luces(m).noche)
    }

    /** El banco de piedra arrimado al murete, con el objeto del día encima (el mismo del que habla el cuento). */
    private fun DrawScope.banco(m: Momento, l: Luces) {
        val piedra = l.tono(MARMOL); val sombra = l.tono(lerp(MARMOL, Color(0xFF5A5468), 0.34f))
        drawOval(Color.Black.copy(alpha = 0.2f), Offset(170f, 266f), Size(88f, 6f))
        drawRect(sombra, Offset(178f, 253f), Size(8f, 15f)); drawRect(sombra, Offset(242f, 253f), Size(8f, 15f))
        drawRect(piedra, Offset(178f, 253f), Size(3f, 15f)); drawRect(piedra, Offset(242f, 253f), Size(3f, 15f))
        drawRect(Brush.verticalGradient(listOf(l.tono(lerp(MARMOL, Color.White, 0.35f)), piedra, sombra), 247f, 253.5f), Offset(172f, 247f), Size(84f, 6.5f))
        drawLine(sombra, Offset(172f, 253.4f), Offset(256f, 253.4f), 0.8f)
        val sobre = Offset(214f, 247f)                                         // el centro del tablero
        when (m.ficha.objeto) {
            "remo" -> remo(l)
            "cojin" -> cojin(l, sobre)
            "estrobo" -> estrobo(l, sobre)
            "anfora" -> anfora(l, sobre + Offset(0f, 0.5f), 27f, 0f)
            "lampara" -> lucerna(m, l, sobre)
            "red" -> red(l)
            "higos" -> higos(l, sobre)
            "dracma" -> dracmas(l, sobre)
            "aulos" -> aulos(l, sobre)
            "copa" -> copa(l, sobre, 1.25f)
            "manto" -> manto(l)
            "pan" -> pan(l, sobre)
            else -> Unit
        }
        // Al anochecer, con gente: la copa y la jarra del vino, a un lado.
        if (conGente(m) && m.ficha.objeto != "copa") copa(l, Offset(188f, 247f), 0.8f)
        if (conGente(m)) jarra(l, Offset(244f, 247f))
        // De luto: un lécito blanco con su cinta oscura.
        if (m.con("luto")) lecito(l, Offset(236f, 247f))
    }

    // ---------------------------------------------------------------- Los doce objetos del día

    /** El remo: de abeto, largo como dos hombres y medio, tendido sobre el murete, con su marca cerca del puño y su estrobo. */
    private fun DrawScope.remo(l: Luces) {
        val abeto = l.tono(Color(0xFFDCBC84)); val luz = l.tono(Color(0xFFF2DCA8)); val sombra = l.tono(Color(0xFF8A6A3E))
        val a = Offset(150f, 217.2f); val b = Offset(282f, 216f)
        drawLine(Color.Black.copy(alpha = 0.22f), a + Offset(2f, 2.4f), b + Offset(0f, 2.4f), 2.2f, StrokeCap.Round)
        drawLine(sombra, a + Offset(0f, 0.9f), b + Offset(0f, 0.9f), 2.6f, StrokeCap.Round); drawLine(abeto, a, b, 2.2f, StrokeCap.Round); drawLine(luz, a + Offset(0f, -0.7f), b + Offset(0f, -0.7f), 0.6f)
        // La pala.
        drawPath(Path().apply {
            moveTo(246f, 216.4f); cubicTo(254f, 211.6f, 276f, 211f, 284f, 215.6f)
            cubicTo(276f, 220.6f, 254f, 220.6f, 246f, 216.4f); close()
        }, Brush.verticalGradient(listOf(luz, abeto, sombra), 211f, 220.6f))
        drawLine(sombra.copy(alpha = 0.6f), Offset(250f, 216.2f), Offset(282f, 215.8f), 0.5f)
        // El puño, la marca y el estrobo de cuero.
        drawLine(sombra, Offset(148f, 217.2f), Offset(156f, 217.1f), 3f, StrokeCap.Round)
        drawLine(l.tono(ROJO), Offset(164f, 215.6f), Offset(164f, 218.6f), 1.3f); drawLine(l.tono(ROJO), Offset(167f, 215.6f), Offset(167f, 218.6f), 1.3f)
        drawLine(l.tono(CUERO), Offset(190f, 215.2f), Offset(190f, 219f), 2.6f, StrokeCap.Round)
    }

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
        val x0 = 180f; val x1 = 250f; val arriba = 220f
        // El borde de abajo cuelga en ondas.
        val abajo = { x: Float -> 243f + 2.4f * sin((x - x0) * 0.21f) }
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
        val negro = l.tono(BARNIZ); val luz = l.tono(Color(0xFF5A4A40))
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
        val negro = l.tono(BARNIZ); val luz = l.tono(Color(0xFF5A4A40))
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
        pano(l, Offset(190f, 219f), Offset(238f, 219f), 25f, lana)
        drawLine(l.tono(PURPURA), Offset(193f, 241.5f), Offset(236f, 240f), 1.6f)
        drawRect(l.tono(lerp(lana, Color.White, 0.2f)), Offset(188f, 217.4f), Size(52f, 3.2f))
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

    // ---------------------------------------------------------------- La parte viva

    override fun seMueve(m: Momento): Boolean = true                          // el agua siempre brilla

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        val l = luces(m)
        val viento = (if (m.ficha.clima == Clima.DESPEJADO) 0.25f else m.viento) * -1f     // el viento del mar tumba los humos hacia tierra… hacia la izquierda del cuadro
        destellos(l, Rect(0f, H + 1f, ANCHO, 218f), reloj, m.semilla("destellos"))

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
            reflejo(Offset(152f, 156f), 12f, LLAMA, 0.5f * l.noche, reloj, 5)
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
        val brasero = Offset(136f, 270f)
        if (comiendo(m)) {
            llama(brasero + Offset(-6f, -17f), 7f, reloj, 1); llama(brasero + Offset(6f, -17f), 6f, reloj, 2)
            resplandor(brasero + Offset(0f, -18f), 30f, 0.25f + 0.5f * l.noche)
            humo(l, brasero + Offset(0f, -30f), 58f, 15f, reloj, viento, 0.12f, 0f, 4, 10, 0.8f)
        } else if (!enElMar(m)) {
            brasas(l, brasero + Offset(0f, -16f), 16f, reloj)
            if (l.noche > 0.3f) resplandor(brasero + Offset(0f, -16f), 14f, 0.35f * l.noche, Color(0xFFFF5A1E))
        }
        // La lucerna del candelero y la del banco.
        if (conGente(m) && l.noche > 0.15f) llama(Offset(343f, 216.6f), 4.2f, reloj, 3)
        if (m.ficha.objeto == "lampara" && l.noche > 0.3f && m.hacer != Hacer.DORMIR && !enElMar(m)) llama(Offset(227f, 241.8f), 4f, reloj, 4)

        tiempoVivo(l, m.ficha.clima, reloj, H, m.ficha.semilla)
    }

    // ---------------------------------------------------------------- El gato

    override fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 18f, 276f, hasta = 362f))
        add(Ancla(TipoAncla.MIRADOR, 272f, 219.5f))
        add(Ancla(TipoAncla.OBJETO, 214f, 268f))
        add(Ancla(TipoAncla.ABRIGO, 214f, 270f))                              // debajo del banco
        if (comiendo(m)) add(Ancla(TipoAncla.FUEGO, 160f, 274f))
        if (fiesta(m)) add(Ancla(TipoAncla.ADORNO, 116f, 258f))
    }
}
