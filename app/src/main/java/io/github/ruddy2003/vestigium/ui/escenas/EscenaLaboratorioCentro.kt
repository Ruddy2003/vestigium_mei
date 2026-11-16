package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.EscenaLaboratorio.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * El segundo cuadro de Laboratorio: **el centro del Claustro** (`composicion_laboratorio_centro.md` de la tanda S17), el
 * gran hall redondo bajo la vieja plaza de la ciudad, con el comedor grande abajo, visto de pie desde la galería del
 * primer piso, en la esquina donde ella come sola, de espaldas al ruido. Lo que cuenta el cuento de ese sitio: «El comedor
 * grande está en el centro del Claustro, debajo de la vieja plaza de la ciudad: mesas largas y una pared entera de pantallas
 * de la Vigía con las ruinas de arriba en directo. Casi nadie las mira. Los niños, sí.»
 *
 * Aquí van las funciones de estado del centro (cuándo se pinta, qué hay en las mesas, la luz del pozo, las luces de la
 * cena y de las fiestas); el cuadro, en [CuadroCentro], y sus planos en `EscenaLaboratorioCentro*.kt`.
 */
internal object CentroLab {
    // ---------------------------------------------------------------- Cuándo aparece

    /**
     * Los días del cuento que la ponen en lo común fuera de las horas de comer, y de qué hora a qué hora (lista corta, para
     * el autor):
     * - **el censo** (12 de febrero): «desde temprano hay una cola que da la vuelta al comedor grande»: la mañana, hasta las 10;
     * - **el año nuevo** (1 de enero): «Esta tarde, en el comedor grande, hay fruta de los jardines para todos»: de la comida a
     *   la cena;
     * - **la Fundación** (18 de marzo): «Esta tarde hay comida de fiesta en el comedor grande»: de la comida a la cena.
     *
     * Las jornadas (tres días de junio, «en el estrado del comedor grande») también pasan ahí, pero toda la jornada de
     * trabajo; se cuentan con las sillas en filas y el estrado a las horas de comer, y el resto del día queda el laboratorio
     * **[Diseño propio]**.
     */
    internal class DiaComun(val id: String, val desde: Double, val hasta: Double)

    val DIAS_DE_LO_COMUN = listOf(DiaComun("censo", 7.5, 10.0), DiaComun("anio_nuevo", 12.5, 21.0), DiaComun("fundacion", 12.5, 21.0))

    /** Si a esta hora el horario la pone en el comedor grande: el desayuno, la comida y la cena del Consejo. */
    fun aLaHoraDeComer(m: Momento): Boolean = when (m.hacer) {
        Hacer.COMER, Hacer.GENTE -> true
        Hacer.TRABAJO, Hacer.LIBRE, Hacer.DORMIR, Hacer.CAMINO, Hacer.CUIDAR, Hacer.REZAR, null -> false
    }

    /** Si hoy es uno de [DIAS_DE_LO_COMUN] y es su hora. */
    fun diaDeLoComun(m: Momento): Boolean = DIAS_DE_LO_COMUN.any { d -> EscenaLaboratorio.hoyEs(m, d.id) && m.hora >= d.desde && m.hora < d.hasta }

    /**
     * Si a esta hora se pinta el centro y no el laboratorio: a las horas de comer (el desayuno de 6:30 a 7:30, la comida de
     * 12:30 a 13:30 y la cena del Consejo de 19:30 a 21:00), con el Claustro abierto; también arriba, donde las pantallas
     * enseñan el campamento (lo que el Claustro mira de su subida); y las horas de los días de lo común. Nunca en la
     * cuarentena ni en el cierre («el comedor grande está cerrado»), ni en la campana (está dentro).
     */
    fun toca(m: Momento): Boolean = when (EscenaLaboratorio.lugar(m)) {
        Lugar.LABORATORIO -> aLaHoraDeComer(m) || diaDeLoComun(m)
        Lugar.ARRIBA -> aLaHoraDeComer(m)
        Lugar.CUARENTENA, Lugar.CAMPANA, Lugar.CIERRE -> false
    }

    // ---------------------------------------------------------------- Lo que hay en las mesas

    /**
     * Lo que se ve en el comedor a esta hora **[Diseño propio]**, sin personas:
     * - [DESAYUNO]: las mesas a medio poner, el mostrador con las cubas humeando;
     * - [COMIDA]: todo usado, las bandejas por todas partes, los bancos apartados;
     * - [CENA]: el comedor recogido y solo la cabecera puesta, con sus lámparas bajas;
     * - [FIESTA]: el año nuevo y la Fundación, de tarde: las mesas con la comida de fiesta;
     * - [CENSO]: la mañana del censo, la mesa del censo y la cinta de la cola alrededor del comedor.
     */
    enum class Mesa { DESAYUNO, COMIDA, CENA, FIESTA, CENSO }

    fun mesa(m: Momento): Mesa {
        if (EscenaLaboratorio.hoyEs(m, "censo") && m.hora >= 7.5 && m.hora < 10.0) return Mesa.CENSO
        val fiesta = (EscenaLaboratorio.anioNuevo(m) || EscenaLaboratorio.fundacion(m)) && m.hora >= 12.5
        return when (m.hacer) {
            Hacer.COMER -> if (m.hora < 10.0) Mesa.DESAYUNO else if (fiesta) Mesa.FIESTA else Mesa.COMIDA
            Hacer.GENTE -> if (fiesta) Mesa.FIESTA else Mesa.CENA
            Hacer.TRABAJO, Hacer.LIBRE, Hacer.DORMIR, Hacer.CAMINO, Hacer.CUIDAR, Hacer.REZAR, null -> if (fiesta) Mesa.FIESTA else if (m.hora < 10.0) Mesa.DESAYUNO else Mesa.CENA
        }
    }

    /** Las jornadas (tres días de junio): las sillas en filas delante del estrado. */
    fun jornadas(m: Momento): Boolean = EscenaLaboratorio.diaNumero(m, "jornadas") > 0

    /** Arriba: junto a la barandilla, sillas arrimadas (alguien miró las pantallas). */
    fun sillasALaBarandilla(m: Momento): Boolean = EscenaLaboratorio.arriba(m)

    /** El año nuevo: «fresas pequeñas y ácidas, tres por cabeza». */
    fun fresas(m: Momento): Boolean = EscenaLaboratorio.anioNuevo(m) && m.hora >= 12.5

    /** La Fundación: las banderas de papel de cada departamento, colgadas de galería a galería (la de Botánica, verde) **[Diseño propio]**: el cuento las cuelga entre los tanques. */
    fun banderas(m: Momento): Boolean = EscenaLaboratorio.fundacion(m)

    // ---------------------------------------------------------------- La luz

    /**
     * Cuánto ha bajado la luz del hall, de 0 (día) a 1 (noche): de noche (la luz tenue de la ficha) y en la cena de la
     * noche larga («esta noche será la más oscura del año»), que se hace a la luz de las velas. El apagón, a media luz.
     */
    fun penumbra(m: Momento): Float = when {
        EscenaLaboratorio.deNoche(m) -> 1f
        EscenaLaboratorio.nocheLarga(m) && m.hora >= 19.5 -> 0.85f
        EscenaLaboratorio.apagon(m) -> 0.45f
        else -> 0f
    }

    /**
     * La luz del pozo: el anillo de lámparas «que imitan el día» da la vuelta como un sol **[Diseño propio]**. [d] es la
     * dirección del haz (unitaria, hacia abajo), [color] y [fuerza] su luz, y [mancha] dónde cae en el suelo del comedor el
     * centro de la mancha (metros, el x y el z).
     */
    class Pozo(val d: V3, val color: Color, val fuerza: Float, val mancha: Offset)

    /**
     * Por dónde cae la mancha del pozo en el suelo a la hora [h] (x y z en metros): en el desayuno, sesgada hacia la
     * izquierda del cuadro; a mediodía, a plomo bajo el pozo; en la cena, sesgada hacia la derecha, hasta subir por la pared
     * de pantallas. Se mueve en línea recta entre las tres.
     */
    fun mancha(h: Float): Offset {
        val alba = Offset(-2.62f, 4.55f); val medio = Offset(0.25f, 0.05f); val tarde = Offset(8.8f, 1.9f)
        return if (h <= 13f) { val t = ((h - 6.5f) / 6.5f).coerceIn(0f, 1f); alba + (medio - alba) * t } else { val t = ((h - 13f) / 8f).coerceIn(0f, 1f); medio + (tarde - medio) * t }
    }

    /** El color del haz a la hora [h]: cálido al alba, blanco a mediodía, dorado y luego naranja en la cena. */
    fun colorDelPozo(h: Float): Color = when {
        h < 9f -> lerp(Color(0xFFFFC27E), Color(0xFFFFF4E0), ((h - 6.5f) / 2.5f).coerceIn(0f, 1f))
        h < 16f -> lerp(Color(0xFFFFF4E0), Color(0xFFFFFAF2), 1f - abs(h - 12.5f) / 3.5f)
        else -> lerp(Color(0xFFFFF0D6), Color(0xFFFFA858), ((h - 16f) / 4.5f).coerceIn(0f, 1f))
    }

    fun pozo(m: Momento): Pozo {
        val h = m.hora.toFloat()
        val mm = mancha(h)
        val d = V3(mm.x, -GeoCentro.YO, mm.y).unidad()
        // De noche las lámparas del pozo pasan a «su azul de noche»: un haz frío y bajo, como de luna.
        val noche = penumbra(m)
        val color = lerp(colorDelPozo(h), Color(0xFF9FB4F0), noche)
        val fuerza = when {
            EscenaLaboratorio.deNoche(m) -> 0.3f
            EscenaLaboratorio.nocheLarga(m) && h >= 19.5f -> 0.12f
            EscenaLaboratorio.apagon(m) -> 0.35f
            else -> 1f
        }
        return Pozo(d, color, fuerza, mm)
    }

    /** El ventilador del pozo: quieto en el apagón («se ha ido todo: la luz, las rejillas, el zumbido»). */
    fun ventilador(m: Momento): Boolean = !EscenaLaboratorio.apagon(m)

    /**
     * La luz de fondo del hall: la que las lámparas del pozo reparten rebotando en el hormigón blanco. Verde agua por la
     * mañana, más blanca a mediodía, malva en la cena; de noche, el azul del Claustro.
     */
    fun fondo(m: Momento): Color {
        val h = m.hora.toFloat()
        val dia = when {
            h < 10f -> lerp(Color(0xFF5A7E98), Color(0xFF6E92A6), ((h - 6.5f) / 3.5f).coerceIn(0f, 1f))
            h < 16f -> Color(0xFF7698AA)
            else -> lerp(Color(0xFF6E8EA4), Color(0xFF6A6694), ((h - 16f) / 4.5f).coerceIn(0f, 1f))
        }
        val noche = if (EscenaLaboratorio.nocheLarga(m)) Color(0xFF242C4C) else Color(0xFF34406C)
        return lerp(dia, noche, penumbra(m))
    }

    /**
     * Cuánta luz del pozo se reparte por el hall (la que imita el día, difusa): poca al alba, la que más a mediodía, y baja
     * en la cena; de noche, casi nada.
     */
    fun difusa(m: Momento): Float {
        val h = m.hora.toFloat()
        val dia = when { h < 13f -> 0.55f + 0.2f * ((h - 6.5f) / 6.5f).coerceIn(0f, 1f); else -> 0.75f - 0.25f * ((h - 13f) / 8f).coerceIn(0f, 1f) }
        return dia * pozo(m).fuerza.coerceAtMost(1f) * (1f - 0.7f * penumbra(m))
    }

    /** Cuánto ve el punto [w] lo alto del pozo: entero en el vacío; poco bajo las losas de las galerías y al fondo de ellas. */
    fun alCielo(w: V3): Float {
        val r = sqrt(w.x * w.x + w.z * w.z)
        if (r <= GeoCentro.RV - 0.2f || w.y > GeoCentro.YB) return 1f
        val hondo = ((r - GeoCentro.RV + 0.2f) / 1.6f).coerceIn(0f, 1f)
        val bajo = w.y < GeoCentro.G1 - GeoCentro.LOSA || (w.y >= GeoCentro.G1 && w.y < GeoCentro.G2 - GeoCentro.LOSA) || w.y >= GeoCentro.G2
        return if (bajo) 1f - 0.85f * hondo else 1f - 0.25f * hondo
    }

    /** Hacia qué color se va lo del otro lado del vacío: el aire del hall, algo verde agua. */
    fun bruma(m: Momento): Color = lerp(lerp(Color(0xFFAEC2CA), Color(0xFFB4ACC4), ((m.hora.toFloat() - 16f) / 4f).coerceIn(0f, 1f)), Color(0xFF2E3858), penumbra(m))

    /**
     * Cuánta luz de fondo le llega al punto [w], de 0 a 1: la luz entra por el vacío, así que lo que está bajo las galerías
     * y al fondo de ellas queda en penumbra; lo más alto de la bóveda, junto al pozo, recibe más.
     */
    fun abrigo(w: V3): Float {
        val r = sqrt(w.x * w.x + w.z * w.z)
        if (r <= GeoCentro.RV - 0.3f) return if (w.y > GeoCentro.YB) 0.95f + 0.1f * ((w.y - GeoCentro.YB) / 2f).coerceIn(0f, 1f) else 1f
        val hondo = ((r - GeoCentro.RV + 0.3f) / (GeoCentro.RW - GeoCentro.RV)).coerceIn(0f, 1f)
        // Bajo una losa (el piso de abajo, bajo la primera galería; la primera, bajo la segunda) o en la segunda, bajo la bóveda.
        val bajo = w.y < GeoCentro.G1 - GeoCentro.LOSA || (w.y >= GeoCentro.G1 && w.y < GeoCentro.G2 - GeoCentro.LOSA) || w.y >= GeoCentro.G2
        return if (bajo) 1f - 0.42f * hondo else 1f - 0.1f * hondo
    }

    /**
     * Cuánto tapa la pasarela el haz que llega a [w] (con la dirección [d]), de 0 a 1: se sigue el rayo hacia arriba hasta la
     * altura de la pasarela y se mira si cae sobre ella.
     */
    fun sombraDeLaPasarela(w: V3, d: V3): Float {
        val y = GeoCentro.G2 - 0.25f
        if (w.y >= y - 0.05f || d.y >= -0.01f) return 0f
        val s = (w.y - y) / d.y
        val qx = w.x - d.x * s; val qz = w.z - d.z * s
        val a = GeoCentro.PASA0; val b = GeoCentro.PASA1
        val abx = b.x - a.x; val abz = b.z - a.z; val l2 = abx * abx + abz * abz
        val t = ((qx - a.x) * abx + (qz - a.z) * abz) / l2
        if (t < 0f || t > 1f) return 0f
        val px = a.x + abx * t - qx; val pz = a.z + abz * t - qz
        val dist = sqrt(px * px + pz * pz)
        return ((GeoCentro.PASA_ANCHO / 2f + 0.05f - dist) / 0.15f).coerceIn(0f, 1f) * 0.92f
    }

    /** Cuánto alumbra la pared de pantallas el hall, de 0 a 1: como en el laboratorio; las jornadas, apagadas. */
    fun brilloDeLasPantallas(m: Momento): Float = if (EscenaLaboratorio.pantallaApagada(m)) 0f else (EscenaLaboratorio.brilloDePantalla(m) * (0.75f + 0.45f * penumbra(m))).coerceIn(0f, 1.2f)

    /** Las velas de la noche larga, en todas las mesas (desde la cena). */
    fun velas(m: Momento): Boolean = EscenaLaboratorio.velas(m)

    /** Las lámparas bajas de la mesa del Consejo: en la cena, cálidas; la noche larga, apagadas (quedan las velas). */
    fun lamparasDelConsejo(m: Momento): Float = when (mesa(m)) {
        Mesa.CENA, Mesa.FIESTA -> if (EscenaLaboratorio.nocheLarga(m) || m.hacer != Hacer.GENTE) 0f else if (EscenaLaboratorio.apagon(m)) 0.4f else 1f
        Mesa.DESAYUNO, Mesa.COMIDA, Mesa.CENSO -> 0f
    }

    /** El resplandor azul de la boca del destello; apagado en su revisión («la sala redonda está abierta como un reloj desmontado») y en el apagón. */
    fun destello(m: Momento): Float = if (EscenaLaboratorio.revisionDestello(m) > 0 || EscenaLaboratorio.apagon(m)) 0f else 0.7f

    /** La luz de los jardines que sale por la boca de Botánica: blanca de día, violeta de noche; la noche larga, a oscuras. */
    fun luzDeBotanica(m: Momento): Float = when {
        EscenaLaboratorio.nocheLarga(m) && m.hora >= 19.5 -> 0.15f
        EscenaLaboratorio.apagon(m) -> 0.3f
        else -> 0.8f
    }

    fun colorDeBotanica(m: Momento): Color = if (EscenaLaboratorio.deNoche(m) && !EscenaLaboratorio.luzLarga(m)) ColorLaboratorio.VIOLETA else lerp(ColorLaboratorio.LUZ_DIA, ColorLaboratorio.GRAMA, 0.15f)
}

/**
 * El cuadro del centro: las capas, de atrás adelante. Los planos, cada uno en su archivo:
 * - **la bóveda** con el pozo, su anillo de lámparas y el ventilador, y la rendija de la Vigía ([BovedaCentro]);
 * - **las galerías**, sus barandillas de vidrio, las puertas de las alas, los elevadores, la pasarela y las costillas
 *   ([GaleriaCentro], [EstructuraCentro]);
 * - **la pared de pantallas** de la Vigía ([PantallasCentro]);
 * - **el comedor**: el suelo redondo, las mesas largas, el mostrador, la cabecera, las bocas de los pasillos ([ComedorCentro]);
 * - **el verde**: las cortinas de judías, las tomateras y las lechugas ([VerdeCentro]);
 * - **la luz**: la mancha y el haz del pozo, las sombras, el aire ([LuzCentro]);
 * - **su esquina**, en la galería: la mesa chica, la silla, la barandilla de cerca ([EsquinaCentro]).
 */
internal object CuadroCentro {
    /** Por dónde camina el gato: el suelo de la galería, delante de todo. */
    const val PISO_GATO = 277f
    const val GATO_DESDE = 78f
    const val GATO_HASTA = 330f

    fun seMueve(m: Momento): Boolean = true                                    // el ventilador, el polvo en el haz, las pantallas, el destello

    fun DrawScope.cuadro(m: Momento) {
        val v = VistaCentro(m)
        with(BovedaCentro) { boveda(v) }
        with(GaleriaCentro) { segunda(v) }
        with(PantallasCentro) { pared(v) }
        with(ComedorCentro) { abajo(v) }
        with(GaleriaCentro) { primera(v) }
        with(LuzCentro) { enLaGaleria(v) }
        with(VerdeCentro) { columnas(v) }
        with(EstructuraCentro) { costillasLejos(v); pasarela(v); banderas(v) }
        with(LuzCentro) { haz(v) }
        with(EstructuraCentro) { costillaCerca(v) }
        with(EsquinaCentro) { esquina(v) }
        with(VerdeCentro) { cortina(v) }
        with(LuzCentro) { velo(v) }
        grano(Azar(Semilla.de("laboratorio", "centro", "grano")), 0.5f + 0.35f * (1f - v.noche))
    }

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val v = VistaCentro(m)
        with(BovedaCentro) { vivo(v, reloj) }
        with(PantallasCentro) { vivo(v, reloj) }
        with(LuzCentro) { vivo(v, reloj) }
    }

    // ---------------------------------------------------------------- El gato

    /** El punto del suelo de la galería que se ve en [p] del lienzo. */
    fun enElSuelo(p: Offset): V3 = CamCentro.enPlano(p, GeoCentro.G1) ?: GeoCentro.aro(9.2f, GeoCentro.G1, -100f)

    /** La luz del gato: la del hall donde pisa (la de fondo, el haz si le llega, la lámpara de su mesa), y su sombra hacia dentro de la galería. */
    fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val v = VistaCentro(m)
        val w = enElSuelo(en) + V3(0f, 0.25f, 0f)
        val lat = if (en.x < EsquinaCentro.MESA_X) 0.55f else -0.45f
        val rayo = rayoFijo(lat, 56f, (0.32f * (1f - 0.5f * v.noche)).coerceIn(0f, 1f), ColorLaboratorio.SOMBRA)
        val brillo = lerp(v.pozo.color, Color.White, 0.5f)
        return LuzGato(rayo, v.noche.coerceIn(0f, 1f), brillo) { c -> v.c(w, c, V3(0f, 0.5f, 1f)) }
    }

    /** Lo plantado en el suelo del gato: la pata de la silla apartada (si cae en su tramo). */
    fun estorbos(m: Momento): List<Float> = listOf(EsquinaCentro.sillaX(m)).filter { it in GATO_DESDE..GATO_HASTA }

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, GATO_DESDE, PISO_GATO, hasta = GATO_HASTA))
        // El pasamanos de la barandilla, junto a su mesa: un sitio alto con nada delante.
        val mirador = EsquinaCentro.mirador()
        add(Ancla(TipoAncla.MIRADOR, mirador.x, mirador.y))
        // El objeto del día: en la mesa chica (el gato se pone al pie); el grande, en el suelo junto a la silla.
        add(Ancla(TipoAncla.OBJETO, if (ObjetosLaboratorio.esGrande(m)) EsquinaCentro.pieGrandeX() else GATO_DESDE + 6f, PISO_GATO))
        // Se guarece bajo la mesa chica.
        add(Ancla(TipoAncla.ABRIGO, GATO_DESDE + 2f, PISO_GATO))
        if (EscenaLaboratorio.fiesta(m)) add(Ancla(TipoAncla.ADORNO, 250f, PISO_GATO))
    }
}
