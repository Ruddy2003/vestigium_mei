package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.TrazosFrente
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import kotlin.math.pow

/**
 * La paleta del estante de Frente exterior: la sala de radar de la base escondida en las ruinas, la misma del Ideario. La piedra
 * verdosa de los sillares con su humedad, su musgo y sus raíces; la chapa esmaltada color hueso de la consola y su sombra verde
 * gris; la consola oscura de verde casi negro; el cromo gastado, la baquelita y el cobre del tubo; el hormigón del piso y la
 * franja de peligro; la cinta de papel de las etiquetas; el verde turquesa del vidrio grabado y el ámbar de los pilotos. Dos
 * temperaturas: lo frío es la piedra, la consola, el vidrio y la noche; lo cálido, la cúpula, los pilotos, el óxido y la estufa.
 */
internal object PaletaFrente {
    /** La piedra de los sillares (la del Ideario), su junta en sombra, la humedad, el musgo y las raíces. */
    val PIEDRA = TrazosFrente.PIEDRA
    val PIEDRA_LUZ = Color(0xFFB4B6A8)
    val PIEDRA_SOMBRA = TrazosFrente.PIEDRA_SOMBRA
    val HUMEDAD = Color(0xFF34402F)
    val MUSGO = TrazosFrente.MUSGO
    val RAIZ = TrazosFrente.RAIZ
    /** La chapa esmaltada color hueso: su cara, su luz, su sombra y la sombra verde gris de la consola (la del Ideario). */
    val HUESO = TrazosFrente.HUESO
    val HUESO_LUZ = Color(0xFFF6F2E8)
    val HUESO_OSC = Color(0xFFC5BCA9)
    val CHAPA_SOMBRA = Color(0xFF4A544D)
    /** El acero que asoma donde saltó la pintura, y el óxido de debajo de los tornillos. */
    val ACERO = Color(0xFF3E4C57)
    val OXIDO = Color(0xFF9C4A1E)
    /** La consola oscura, de verde casi negro: su cara, su hondo y su canto en luz. */
    val CONSOLA = TrazosFrente.CONSOLA_OSCURA
    val CONSOLA_OSC = Color(0xFF141D1A)
    val CONSOLA_LUZ = Color(0xFF34453F)
    /** El cromo gastado de los biseles y los filetes, y el aluminio de las placas y las tapas. */
    val CROMO = TrazosFrente.CROMO
    val CROMO_LUZ = Color(0xFFEEECE7)
    val CROMO_OSC = Color(0xFF6E6B65)
    val ALUMINIO = Color(0xFFC6C5BE)
    /** La baquelita de las perillas y las cajas, y su brillo. */
    val BAQUELITA = TrazosFrente.BAQUELITA
    val BAQUELITA_LUZ = Color(0xFF4E4741)
    /** El cobre del tubo del techo. */
    val COBRE = TrazosFrente.COBRE
    /** El hormigón del piso, su junta y la franja de peligro. */
    val HORMIGON = Color(0xFFA9ACA2)
    val HORMIGON_OSC = Color(0xFF6F7369)
    val PELIGRO = TrazosFrente.PELIGRO
    /** La cinta de papel de las etiquetas (algo tostada: de valor medio, no blanca), su sombra y la tinta. */
    val CINTA = Color(0xFFCDBB8D)
    val CINTA_OSC = Color(0xFFA08C60)
    /** El cartón manila de las tarjetas de las valijas. */
    val MANILA = Color(0xFFD1B477)
    val TINTA = Color(0xFF1E2523)
    /** La hoja de la carta estelar, impresa en papel crudo. */
    val PAPEL = Color(0xFFE8E0C8)
    /** La tinta ámbar quemada de lo impreso (la ruta de la carta, las franjas de las etiquetas). */
    val QUEMADO_TINTA = Color(0xFFB85C0F)
    /** Los pilotos: ámbar, rojo y el verde del radar. */
    val AMBAR = TrazosFrente.PILOTO_AMBAR
    val ROJO = TrazosFrente.PILOTO_ROJO
    val BOTON = Color(0xFFA8201A)
    val RADAR = TrazosFrente.RADAR
    val PANTALLA = TrazosFrente.PANTALLA
    /** El vidrio grabado: el vidrio en penumbra, a la luz, y su trazo iluminado por el canto. */
    val VIDRIO = TrazosFrente.VIDRIO
    val VIDRIO_LUZ = TrazosFrente.VIDRIO_LUZ
    val VIDRIO_TRAZO = TrazosFrente.VIDRIO_TRAZO
    /** La película de las latas, color miel oscuro. */
    val PELICULA = Color(0xFF3B2414)
    val MIEL = Color(0xFF8A5A2C)
    /** El cartón pardo de los tubos y la lona cruda de las valijas sin ánimo. */
    val CARTON = Color(0xFF9C7A52)
    val LONA = Color(0xFFB2A784)
    /** El cuero de las correas. */
    val CUERO = Color(0xFF5A3A24)
    val CUERO_LUZ = Color(0xFF86603E)
    /** La sombra del día: verde negro, la de la piedra húmeda (la del Ideario), no un violeta ni un negro. */
    val SOMBRA = Color(0xFF16201C)
    /** La noche: lo hondo de la pared, del mueble y del fondo de los casilleros; en los Sueños oscuros, más hacia el azul. */
    val NOCHE = Color(0xFF07130F)
    val HONDO = Color(0xFF0C1813)
    val FONDO_NOCHE = Color(0xFF050D0A)
    val NOCHE_SUENOS = Color(0xFF061219)
    val HONDO_SUENOS = Color(0xFF08141A)
    val FONDO_SUENOS = Color(0xFF040C12)
    /** El turno de guardia de los Sueños claros: las lámparas bajadas a ámbar y la sombra violeta. */
    val GUARDIA = Color(0xFFF2A15A)
    val SOMBRA_GUARDIA = Color(0xFF34284E)
    /** Hacia donde se apaga lo que queda lejos de las lámparas bajadas: un pardo violáceo hondo (oscurece, no tiñe de rosa). */
    val HONDO_GUARDIA = Color(0xFF2A2230)
    /** La luz de la cúpula (la del Ideario), su corazón, el foco rojo, la estufa y su brasa. */
    val LAMPARA = Color(0xFFFFE3B4)
    val FUEGO = Color(0xFFFFF4DE)
    val FOCO = Color(0xFFFF4A2E)
    val ESTUFA = Color(0xFFFF7A2E)
    val BRASA = Color(0xFFE4502A)

    val LETRA: FontFamily = LetraExtra.chivoMono
}

/**
 * El aire del momento en el estante de Frente: de día (la base con las lámparas encendidas, la luz de la cúpula de arriba a la
 * izquierda), de noche (el modo oscuro: la base en penumbra verde; arden la cúpula, el foco rojo de un hueco y la estufa del
 * pie, y el vidrio grabado brilla en turquesa) y en los Sueños ([noche]: el turno de guardia en claro, la base dormida en
 * oscuro). De noche cada color va de su hondo (oscuro, de su tono, con el aire de la noche) a su color según cuánta luz le llega
 * ([cerca], de 0 a 1), con el calor de la cúpula encima; el frío del vidrio ([vidrio], de 0 a 1) lo pinta de turquesa. En el
 * cuerpo del mueble ([cuerpo]) lo que no se alumbra se hunde en un verde botella; el fondo de los casilleros ([fondo]), más hondo.
 */
internal class AireFrente(val oscuro: Boolean, val noche: Boolean, val cuerpo: Boolean = false, val fondo: Boolean = false) {
    private val p = PaletaFrente
    /** Si arden las luces de la base (de noche siempre; en los Sueños claros, bajadas). */
    val lumbre: Boolean get() = oscuro || noche
    /** La base dormida (los Sueños oscuros): la cúpula apagada; manda el vidrio. */
    val dormida: Boolean get() = oscuro && noche
    /** El aire en que se hunde lo que no se alumbra. */
    val hondo: Color = when {
        oscuro && fondo -> if (noche) p.FONDO_SUENOS else p.FONDO_NOCHE
        oscuro && cuerpo -> if (noche) p.HONDO_SUENOS else p.HONDO
        oscuro -> if (noche) p.NOCHE_SUENOS else p.NOCHE
        noche -> p.SOMBRA_GUARDIA
        else -> p.SOMBRA
    }

    /** El color propio de algo, hundido: oscuro, algo de su tono, con el aire de la noche. */
    fun hundido(c: Color): Color {
        val f = if (cuerpo) 0.1f else 0.09f
        val n = hondo
        return Color(c.red * f + n.red * (1 - f), c.green * f + n.green * (1 - f), c.blue * f + n.blue * (1 - f), c.alpha)
    }

    /** El color a la luz de la cúpula: crema tibia, así el azul baja más que el rojo. */
    private fun aLaLampara(c: Color): Color = Color((c.red * 1.06f).coerceAtMost(1f), c.green * 0.94f, c.blue * 0.74f, c.alpha)

    /** El color al turno de guardia (Sueños claros): algo más bajo y más tibio. */
    private fun alaGuardia(c: Color): Color = Color(c.red * 0.96f, c.green * 0.87f, c.blue * 0.76f, c.alpha)

    /** Cerca de la luz, lo claro (la chapa hueso, el papel) pasa a miel clara, no a amarillo sucio. */
    private fun encendido(c: Color, k: Float): Color {
        val v = aLaLampara(c)
        val l = c.red * 0.3f + c.green * 0.59f + c.blue * 0.11f
        val tono = Color((l * 1.32f).coerceAtMost(1f), (l * 1.08f).coerceAtMost(1f), l * 0.66f, c.alpha)
        return mezcla(mezcla(c, v, 0.8f), tono, (if (cuerpo) 0.28f else 0.4f) * k)
    }

    /** El color de algo bajo el resplandor del vidrio grabado: turquesa y más bajo. */
    fun alVidrio(c: Color): Color {
        val l = c.red * 0.3f + c.green * 0.59f + c.blue * 0.11f
        return Color((l * 0.16f + c.red * 0.06f).coerceAtMost(1f), (l * 0.56f + 0.05f).coerceAtMost(1f), (l * 0.5f + 0.05f).coerceAtMost(1f), c.alpha)
    }

    /**
     * El color de [c] con la luz que le llega: [cerca] la cálida (la cúpula, el foco, la estufa), [vidrio] la fría del vidrio
     * grabado. De día, tal cual; al turno de guardia, más tibio.
     */
    fun luz(c: Color, cerca: Float, vidrio: Float = 0f): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) guardia(c, k) else c
        val base = mezcla(mezcla(hundido(c), encendido(c, k), (1.1f * k.toDouble().pow(1.5).toFloat()).coerceAtMost(0.94f)), p.LAMPARA, 0.1f * k)
        val f = vidrio.coerceIn(0f, 1f)
        return if (f > 0.01f) mezcla(base, mezcla(hundido(c), alVidrio(c), 0.85f), (0.7f * f.toDouble().pow(1.3).toFloat()).coerceAtMost(0.62f) * (1f - 0.6f * k)) else base
    }

    /**
     * Al turno de guardia (Sueños claros), con las lámparas bajadas: tibio cerca de la cúpula y de la estufa ([k] alto) y, lejos,
     * más bajo y hacia el violeta de la sombra.
     */
    private fun guardia(c: Color, k: Float): Color = mezcla(mezcla(alaGuardia(c), p.HONDO_GUARDIA, 0.34f * (1f - k)), p.GUARDIA, 0.1f * k)

    /** De día, más claro (positivo) o más hondo (negativo); de noche, como [luz]; al turno de guardia, con su luz [cerca]. */
    fun dia(c: Color, f: Float, cerca: Float = 1f, vidrio: Float = 0f): Color {
        if (oscuro) return luz(c, cerca, vidrio)
        val b = if (noche) guardia(c, cerca.coerceIn(0f, 1f)) else c
        return if (f > 0f) mezcla(b, luzMueble(1f), f) else if (f < 0f) mezcla(b, hondo, -f) else b
    }

    /**
     * Lo que toca la luz directa de la cúpula ([f], de 0 a 1) de día: [base] es el color sin ella. De día, la luz blanca tibia de
     * la lámpara; al turno de guardia, el ámbar bajo. Es el color de la superficie bajo esa luz, opaco: no un velo encima.
     */
    fun cupula(base: Color, f: Float): Color {
        val k = f.coerceIn(0f, 1f)
        if (oscuro || k <= 0f) return base
        return if (noche) mezcla(base, Color((base.red * 1.2f + 0.1f).coerceAtMost(1f), (base.green * 1.04f + 0.05f).coerceAtMost(1f), (base.blue * 0.86f + 0.02f).coerceAtMost(1f), base.alpha), k)
        else mezcla(base, Color((base.red * 1.2f + 0.06f).coerceAtMost(1f), (base.green * 1.18f + 0.05f).coerceAtMost(1f), (base.blue * 1.08f + 0.03f).coerceAtMost(1f), base.alpha), k)
    }

    /** La sombra: de día, verde negro; al turno de guardia, violeta; de noche, el aire hondo. */
    fun sombra(a: Float): Color =
        if (oscuro) hondo.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else if (noche) hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else p.SOMBRA.copy(alpha = a.coerceIn(0f, 1f))
    /** La luz crema del día; al turno de guardia, la ámbar; de noche, casi nada. */
    fun claro(a: Float): Color = if (oscuro) luzMueble(a * 0.07f) else if (noche) p.GUARDIA.copy(alpha = (a * 0.5f).coerceIn(0f, 1f)) else luzMueble(a)
    /** El brillo de lo que mira a la luz: de día, crema; al turno de guardia, ámbar; de noche, el de la cúpula según [cerca]. */
    fun brillo(a: Float, cerca: Float): Color =
        if (!oscuro) (if (noche) mezcla(luzMueble(1f), p.GUARDIA, 0.5f).copy(alpha = (a * 0.8f).coerceIn(0f, 1f)) else luzMueble(a))
        else p.LAMPARA.copy(alpha = (a * 0.95f * cerca).coerceIn(0f, 1f))
    /** El filo de la luz cálida en lo que la mira; al turno de guardia, el ámbar rasante; de día no hay. */
    fun filo(a: Float): Color = if (oscuro) p.LAMPARA.copy(alpha = a.coerceIn(0f, 1f)) else if (noche) p.GUARDIA.copy(alpha = (a * 0.6f).coerceIn(0f, 1f)) else Color.Transparent
    /** El filo frío del vidrio grabado (de noche). */
    fun filoVidrio(a: Float): Color = if (oscuro) p.VIDRIO_TRAZO.copy(alpha = (a * (if (noche) 1f else 0.7f)).coerceIn(0f, 1f)) else Color.Transparent

    /**
     * El color del ánimo en el cuerpo de una entrada: de noche conserva su tono de día y solo baja con la distancia a la luz (el
     * óxido sigue óxido, pardo lejos y encendido cerca); al turno de guardia, tibio.
     */
    fun animo(t: Color, cerca: Float, vidrio: Float = 0f): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(alaGuardia(t), p.HONDO_GUARDIA, 0.24f * (1f - k)) else t
        val apagado = mezcla(Color(t.red * 0.2f, t.green * 0.22f, t.blue * 0.21f, t.alpha), hondo, 0.25f)
        val enc = mezcla(t, aLaLampara(t), 0.5f)
        val base = mezcla(apagado, enc, (1.15f * k.toDouble().pow(1.5).toFloat()).coerceAtMost(1f))
        val f = vidrio.coerceIn(0f, 1f)
        return if (f > 0.01f) mezcla(base, mezcla(apagado, alVidrio(t), 0.85f), (0.75f * f).coerceAtMost(0.75f) * (1f - 0.6f * k)) else base
    }

    /**
     * La cinta de papel donde va el día, teñida por la luz que le llega: miel cerca de la cúpula, parda honda lejos (nunca más
     * clara que lo alumbrado) y siempre con contraste para la tinta.
     */
    fun etiqueta(c: Color, cerca: Float, vidrio: Float = 0f): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(alaGuardia(c), p.HONDO_GUARDIA, 0.22f * (1f - k)) else c
        val lejos = Color(c.red * 0.36f, c.green * 0.32f, c.blue * 0.23f, c.alpha)
        val alumbrado = Color((c.red * 1.02f).coerceAtMost(1f), c.green * 0.93f, c.blue * 0.72f, c.alpha)
        val base = mezcla(lejos, alumbrado, k.toDouble().pow(1.7).toFloat().coerceAtMost(1f))
        val f = vidrio.coerceIn(0f, 1f)
        return if (f > 0.01f) mezcla(base, Color(c.red * 0.36f, c.green * 0.62f, c.blue * 0.58f, c.alpha), (0.7f * f).coerceAtMost(0.7f) * (1f - 0.6f * k)) else base
    }

    /** La tinta sobre la cinta de papel: verde negro de día; de noche, siempre más honda que la cinta. */
    fun tinta(cerca: Float): Color = if (oscuro) Color(0xFF0B0F0D) else if (noche) mezcla(p.TINTA, p.SOMBRA_GUARDIA, 0.2f) else p.TINTA
}

/** Un polígono cerrado por sus puntos (x, y, x, y…). */
internal fun poligonoFrente(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

/** La sombra de lo que pisa: un óvalo corrido hacia donde cae la luz ([hacia]: 1, a la derecha; -1, a la izquierda). */
internal fun DrawScope.contactoFrente(centro: Offset, ancho: Float, alto: Float, a: AireFrente, fuerza: Float = 0.34f, hacia: Float = 1f) =
    drawOval(a.sombra(if (a.oscuro) fuerza * 1.25f else fuerza), Offset(centro.x - ancho * (0.5f - 0.12f * hacia), centro.y - alto / 2), Size(ancho, alto))

/** Un tornillo de cabeza ranurada: su sombra, la cabeza de cromo con su luz y la ranura girada [giro] grados. */
internal fun DrawScope.tornilloFrente(c: Offset, r: Float, a: AireFrente, k: Float, giro: Float = 30f, vidrio: Float = 0f) {
    val p = PaletaFrente
    drawCircle(a.sombra(0.4f), r * 1.1f, Offset(c.x + r * 0.3f, c.y + r * 0.4f))
    drawCircle(a.luz(p.CROMO_OSC, k, vidrio), r, c)
    drawCircle(a.luz(p.CROMO, k, vidrio), r * 0.78f, Offset(c.x - r * 0.12f, c.y - r * 0.12f))
    rotate(giro, c) { drawRect(a.luz(p.CONSOLA_OSC, k), Offset(c.x - r * 0.7f, c.y - r * 0.13f), Size(r * 1.4f, r * 0.26f)) }
}

/**
 * Un piloto de consola en su aro de cromo: encendido, la lente de [luz] con su halo (más grande de noche) y su brillo; apagado,
 * el vidrio oscuro del mismo color.
 */
internal fun DrawScope.pilotoFrente(c: Offset, r: Float, luz: Color, encendido: Boolean, a: AireFrente, k: Float) {
    val p = PaletaFrente
    if (encendido && a.lumbre) drawCircle(androidx.compose.ui.graphics.Brush.radialGradient(listOf(luz.copy(alpha = if (a.oscuro) 0.4f else 0.2f), luz.copy(alpha = 0f)), c, r * 4.6f), r * 4.6f, c)
    drawCircle(a.sombra(0.4f), r * 1.45f, Offset(c.x + r * 0.25f, c.y + r * 0.35f))
    drawCircle(a.luz(p.CROMO, k), r * 1.4f, c)
    drawCircle(a.luz(p.BAQUELITA, k), r * 1.12f, c)
    drawCircle(if (encendido) luz else mezcla(luz, p.CONSOLA_OSC, if (a.oscuro) 0.8f else 0.62f), r, c)
    if (encendido) drawCircle(mezcla(luz, Color.White, 0.6f), r * 0.42f, Offset(c.x - r * 0.18f, c.y - r * 0.2f))
    else drawCircle(Color.White.copy(alpha = if (a.oscuro) 0.08f else 0.3f), r * 0.26f, Offset(c.x - r * 0.35f, c.y - r * 0.38f))
}

/** El destello de la luz en el cromo, el vidrio o el esmalte (de día, crema; de noche, el de la cúpula si le llega). */
internal fun DrawScope.destelloFrente(c: Offset, r: Float, a: AireFrente, k: Float) {
    if (!a.oscuro) { drawCircle(a.claro(0.85f), r, c); return }
    if (k < 0.05f) return
    drawCircle(PaletaFrente.LAMPARA.copy(alpha = (0.4f * k).coerceAtMost(0.35f)), r * 2.2f, c)
    drawCircle(PaletaFrente.FUEGO.copy(alpha = (1.1f * k).coerceAtMost(0.95f)), r, c)
}

/** Cómo se reparte la luz en un cuerpo redondo, de la luz a la sombra: dónde empieza cada franja y hacia qué va. */
private val FRANJAS_FRENTE = floatArrayOf(0f, 0.07f, 0.18f, 0.36f, 0.58f, 0.76f, 0.9f, 1f)
private val TONOS_FRANJA_FRENTE = floatArrayOf(0.3f, 0.62f, 0.34f, 0.06f, -0.22f, -0.48f, -0.72f)

/**
 * Un cuerpo redondo en [r] por franjas opacas (sin degradados), de [claro] (lo que mira a la luz) a [hondo], pasando por [cara];
 * la luz viene de [hacia] (-1, la izquierda; 1, la derecha). [suave] baja el contraste.
 */
internal fun DrawScope.redondoFrente(r: Rect, cara: Color, claro: Color, hondo: Color, hacia: Float, suave: Float = 1f) {
    for (i in 0 until 7) {
        val f0 = FRANJAS_FRENTE[i]; val f1 = FRANJAS_FRENTE[i + 1]
        val m = TONOS_FRANJA_FRENTE[i] * suave
        val c = if (m >= 0f) mezcla(cara, claro, m) else mezcla(cara, hondo, -m)
        val mas = if (i < 6) 1.2f else 0f
        val x0 = if (hacia < 0f) r.left + r.width * f0 else r.right - r.width * f1 - mas
        drawRect(c, Offset(x0, r.top), Size(r.width * (f1 - f0) + mas, r.height))
    }
}

/** La media luna recortada de los Sueños, de radio [r] centrada en el origen, armada una vez por tamaño. */
private var radioLunaFrente = -1f
private var caminoLunaFrente = Path()

internal fun lunaFrente(r: Float): Path {
    if (r != radioLunaFrente) {
        val fuera = Path().apply { addOval(Rect(-r, -r, r, r)) }
        val dentro = Path().apply { addOval(Rect(-r * 0.8f + r * 0.52f, -r * 0.86f - r * 0.24f, r * 0.8f + r * 0.52f, r * 0.86f - r * 0.24f)) }
        caminoLunaFrente = Path().apply { op(fuera, dentro, PathOperation.Difference) }
        radioLunaFrente = r
    }
    return caminoLunaFrente
}

// ---------------------------------------------------------------- el ánimo

/** Las escalas del ánimo de Frente exterior, la de día y la de noche (bajo, medio, alto). */
private val TONOS_FRENTE: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.FRENTE, false) to juegoTonos(TemaId.FRENTE, true) }

private fun cuadradoFrente(x: Float) = x * x

/** Dónde cae un color en una escala (de 0, bajo, a 1, alto): el tramo y la fracción que más se le acercan; si no está, null. */
private fun enEscalaFrente(t: Color, e: List<Color>): Float? {
    if (e.size < 3) return null
    var mejor: Float? = null
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = e[seg]; val a1 = e[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoFrente(t.red - a0.red - dr * f) + cuadradoFrente(t.green - a0.green - dg * f) + cuadradoFrente(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    return mejor
}

/**
 * Los materiales del ánimo, de bajo a alto, en el orden de la escala del tema: el verde del vidrio, el hueso de la consola y el
 * óxido del sillón y de las lámparas. Son el plástico de los cartuchos, el forro de los tubos y la lona de las valijas, con más
 * cuerpo que la escala de la interfaz (el hueso de en medio, algo tostado, para que no se pierda en la chapa).
 */
private val MATERIALES_FRENTE = listOf(Color(0xFF1D6352), Color(0xFF2F8470), Color(0xFFC4B996), Color(0xFFC9682A), Color(0xFFA23D16))

/**
 * El material de una entrada según su ánimo [t]: se ubica en la escala del tema y se toma el mismo punto en
 * [MATERIALES_FRENTE]. En el modo oscuro la escala del tema cambia de colores (el de en medio pasa del gris hueso a casi negro):
 * aquí la entrada es un objeto del cuarto, así que se ubica el color en la escala de noche y se toma el mismo punto de la de
 * día; la luz dibujada lo apaga después con la distancia. Si no está en ninguna escala, queda igual.
 */
internal fun materialFrente(t: Color?, oscuro: Boolean): Color? {
    if (t == null) return null
    val (dia, noche) = TONOS_FRENTE
    val x = (if (oscuro) enEscalaFrente(t, noche) else null) ?: enEscalaFrente(t, dia) ?: return t
    val q = x.coerceIn(0f, 1f) * (MATERIALES_FRENTE.size - 1)
    val i = q.toInt().coerceAtMost(MATERIALES_FRENTE.size - 2)
    return mezcla(MATERIALES_FRENTE[i], MATERIALES_FRENTE[i + 1], q - i)
}
