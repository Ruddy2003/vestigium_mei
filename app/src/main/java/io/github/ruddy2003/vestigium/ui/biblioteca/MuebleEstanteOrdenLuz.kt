package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontFamily
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import kotlin.math.pow

/**
 * La paleta del estante de Orden: la sala capitular del Ideario. El acero de las placas del muro en sus tres valores, el
 * pavonado gris azulado del mueble, el latón de las placas y las cantoneras, el carmesí del estandarte (aquí, el esmalte del
 * friso, el cojín de la reliquia y el bidón), el azul de plano con su trazo, el cuero, la cera, la chapa estriada y las franjas
 * de peligro. Dos temperaturas: lo frío es el acero, la luminaria y la noche azul; lo cálido, el latón, el carmesí y las velas.
 */
internal object PaletaOrden {
    /** El acero en tres valores (los del Ideario) y la pared de placas. */
    val ACERO_CLARO = Color(0xFFA7AFB7)
    val ACERO_MEDIO = Color(0xFF6D7680)
    val ACERO_HONDO = Color(0xFF373E48)
    val PARED = mezcla(ACERO_MEDIO, ACERO_CLARO, 0.12f)
    /** El pavonado del mueble (el del cuerpo de la cajonera del Ideario), su luz y su hondo. */
    val PAVON = mezcla(ACERO_HONDO, ACERO_MEDIO, 0.28f)
    val PAVON_LUZ = mezcla(ACERO_MEDIO, ACERO_CLARO, 0.35f)
    val PAVON_HONDO = Color(0xFF262B33)
    /** La pintura gris azulada de las gavetas del Ideario. */
    val PINTURA = Color(0xFF55616C)
    /** El latón de placas, cantoneras, aros y pesas. */
    val LATON = Color(0xFFCBA24C)
    val LATON_BRILLO = Color(0xFFF2DA92)
    val LATON_HONDO = Color(0xFF73541F)
    /** El latón de las placas del día: un punto más hondo, para que no hagan una reja de cuadros claros. */
    val LATON_PLACA = Color(0xFFBA9343)
    /** La placa de latón de noche: de lejos (sigue dando contraste con la tinta) a junto a la vela. */
    val PLACA_NOCHE = Color(0xFF9C8248)
    val PLACA_VELA = Color(0xFFE8C677)
    /** El carmesí: el paño, su hondo y su luz. */
    val GRANA = Color(0xFFA31A2A)
    val GRANA_HONDA = Color(0xFF5A0D18)
    val GRANA_LUZ = Color(0xFFD24A5A)
    /** El azul de plano y su trazo. */
    val AZUL_PLANO = Color(0xFF2F5D8E)
    val TRAZO = Color(0xFFDCE7F1)
    /** Las franjas de peligro, gastadas, y el betún. */
    val PELIGRO = Color(0xFFD5AB3E)
    val BETUN = Color(0xFF1A1B20)
    /** La cera, el óxido, el cuero, el papel de los pliegos y la lona cruda. */
    val CERA = Color(0xFFEDE2C4)
    val OXIDO = Color(0xFF8E4A26)
    val CUERO = Color(0xFF4A3426)
    val CUERO_PARDO = Color(0xFF6A4A32)
    val PAPEL = Color(0xFFDCD3B8)
    val LONA = Color(0xFFA79C80)
    /** El vidrio del fanal y la baquelita del zócalo de la válvula. */
    val VIDRIO = Color(0xFFCAD8E0)
    val BAQUELITA = Color(0xFF2A211C)
    /** La luz fría de la luminaria, la sombra violeta azulada del día y la tinta de las placas. */
    val LUZ_FRIA = Color(0xFFF1F0E8)
    val SOMBRA_FRIA = Color(0xFF242638)
    val SOMBRA = mezcla(SOMBRA_MUEBLE, SOMBRA_FRIA, 0.5f)
    val TINTA = Color(0xFF1B1710)
    /** La noche del Ideario (el acero azulado hondo), la llama de las velas, su corazón, el piloto y el destello frío. */
    val NOCHE = Color(0xFF0F1622)
    val LLAMA = Color(0xFFFFC56E)
    val CORAZON = Color(0xFFFFF4D6)
    val PILOTO = Color(0xFFE8362C)
    val DESTELLO = Color(0xFF9DB2CE)
    /** La vigilia (Sueños claros): la luminaria bajada a ámbar rosado y la sombra violeta. */
    val VIGILIA = Color(0xFFF3B68A)
    val SOMBRA_VIGILIA = Color(0xFF3A2C52)
    /** La luz de guardia de la luminaria en los Sueños oscuros: azul y baja. */
    val GUARDIA = Color(0xFF9CB8E8)

    val LETRA: FontFamily = LetraExtra.estarcido
}

/**
 * El aire del momento en el estante de Orden: de día (la luminaria enrejada de arriba a la izquierda, fría), de noche (el modo
 * oscuro: arden las velas y el piloto rojo; lo lejano baja al azul hondo del acero, con el mismo cálculo que el Ideario), en la
 * vigilia de los Sueños claros (la luminaria bajada a ámbar rosado y las velas) y en la guardia de los Sueños oscuros (la
 * luminaria en su luz azul baja, que manda, y las velas bajas). [k] es siempre cuánta luz de vela le llega a algo (0 a 1);
 * [frio], cuánta luz de guardia.
 */
internal class AireOrden(val oscuro: Boolean, val noche: Boolean) {
    private val p = PaletaOrden
    /** Si arden las velas (de noche y en los dos Sueños). */
    val lumbre: Boolean get() = oscuro || noche
    /** La guardia de los Sueños oscuros: la luminaria azul manda; las velas, bajas. */
    val dormida: Boolean get() = oscuro && noche
    /** La vigilia de los Sueños claros. */
    val vigilia: Boolean get() = noche && !oscuro

    /** La sombra de la vigilia: la del día con un punto de violeta (solo en lo que no da la luz, sin teñir la pared). */
    private val SOMBRA_TIBIA = mezcla(p.SOMBRA, p.SOMBRA_VIGILIA, 0.45f)

    /** El hondo de un color de noche: el del Ideario (multiplicado por el aire azul de la sala, no hacia un gris). */
    private fun hondo(c: Color) = Color((c.red * 0.2f + 0.04f).coerceIn(0f, 1f), (c.green * 0.23f + 0.055f).coerceIn(0f, 1f), (c.blue * 0.3f + 0.085f).coerceIn(0f, 1f), c.alpha)

    /** De noche: baja hacia su hondo azul y, cerca de la vela, recobra su color y se calienta (el `n` del Ideario). */
    private fun deNoche(c: Color, k: Float): Color {
        val q = k.coerceIn(0f, 1f)
        return mezcla(mezcla(hondo(c), c, 0.8f * q.pow(1.1f)), p.LLAMA, 0.24f * q)
    }

    /** El color bajo la luz de guardia: azul frío según su claridad. */
    private fun alFrio(c: Color): Color {
        val l = c.red * 0.3f + c.green * 0.59f + c.blue * 0.11f
        return Color((l * 0.52f + 0.03f).coerceAtMost(1f), (l * 0.66f + 0.05f).coerceAtMost(1f), (l * 0.92f + 0.09f).coerceAtMost(1f), c.alpha)
    }

    /** En la vigilia: un punto más bajo, tibio donde llega la vela y con un poco de violeta lejos. */
    private fun enVigilia(c: Color, k: Float): Color {
        val q = k.coerceIn(0f, 1f)
        val bajo = Color(c.red * 0.9f, c.green * 0.88f, c.blue * 0.9f, c.alpha)
        val tibio = Color((c.red * 1.05f + 0.02f).coerceAtMost(1f), c.green * 0.94f, c.blue * 0.8f, c.alpha)
        return mezcla(bajo, tibio, q)
    }

    /** El color de [c] con la luz de vela que le llega ([k]) y la de guardia ([frio]); de día, tal cual. */
    fun luz(c: Color, k: Float, frio: Float = 0f): Color {
        if (!oscuro) return if (noche) enVigilia(c, k) else c
        val base = deNoche(c, k)
        val f = frio.coerceIn(0f, 1f)
        if (f <= 0.01f) return base
        return mezcla(base, mezcla(hondo(c), alFrio(c), 0.75f), (0.75f * f).coerceAtMost(0.55f) * (1f - 0.6f * k.coerceIn(0f, 1f)))
    }

    /** De día, más claro ([f] positivo) o más hondo (negativo); de noche y en los Sueños, como [luz]. */
    fun dia(c: Color, f: Float, k: Float = 0f, frio: Float = 0f): Color {
        val b = luz(c, k, frio)
        if (oscuro) return b
        return if (f > 0f) mezcla(b, if (noche) p.VIGILIA else p.LUZ_FRIA, f) else if (f < 0f) mezcla(b, if (noche) SOMBRA_TIBIA else p.SOMBRA, -f) else b
    }

    /**
     * Lo que toca la luz directa de la luminaria ([f], de 0 a 1): de día, más claro y frío; en la vigilia, ámbar rosado; de noche
     * no hay (la luz de guardia de los Sueños oscuros va en [luz]). Es el color de la superficie bajo esa luz, opaco.
     */
    fun luminaria(base: Color, f: Float): Color {
        val k = f.coerceIn(0f, 1f)
        if (oscuro || k <= 0f) return base
        return if (noche) mezcla(base, Color((base.red * 1.18f + 0.08f).coerceAtMost(1f), (base.green * 1.02f + 0.04f).coerceAtMost(1f), (base.blue * 0.88f + 0.02f).coerceAtMost(1f), base.alpha), k)
        else mezcla(base, Color((base.red * 1.16f + 0.05f).coerceAtMost(1f), (base.green * 1.17f + 0.05f).coerceAtMost(1f), (base.blue * 1.17f + 0.06f).coerceAtMost(1f), base.alpha), k)
    }

    /** La sombra: de día, violeta azulada; en la vigilia, violeta; de noche, el azul hondo. */
    fun sombra(a: Float): Color = when {
        oscuro -> p.NOCHE.copy(alpha = (a * 1.2f).coerceIn(0f, 1f))
        noche -> SOMBRA_TIBIA.copy(alpha = (a * 1.05f).coerceIn(0f, 1f))
        else -> p.SOMBRA.copy(alpha = a.coerceIn(0f, 1f))
    }

    /** El brillo de lo que mira a la luz: de día, la luz fría; en la vigilia, ámbar; de noche, la vela según [k]. */
    fun brillo(a: Float, k: Float): Color = when {
        oscuro -> p.LLAMA.copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f))
        noche -> p.VIGILIA.copy(alpha = (a * 0.7f).coerceIn(0f, 1f))
        else -> p.LUZ_FRIA.copy(alpha = a.coerceIn(0f, 1f))
    }

    /** El destello de un remache o un canto pulido: de noche, frío de lejos y cálido cerca de la vela (el del Ideario). */
    fun destello(a: Float, k: Float, frio: Float = 0f): Color = when {
        oscuro -> mezcla(mezcla(p.DESTELLO, p.GUARDIA, frio), p.LLAMA, (k * 1.3f).coerceIn(0f, 1f)).copy(alpha = (a * (0.3f + 0.55f * k + 0.35f * frio)).coerceIn(0f, 1f))
        noche -> mezcla(p.LUZ_FRIA, p.VIGILIA, 0.6f).copy(alpha = (a * 0.8f).coerceIn(0f, 1f))
        else -> p.LUZ_FRIA.copy(alpha = a.coerceIn(0f, 1f))
    }

    /** El filo cálido de la vela en el canto que la mira (de noche y en la vigilia). */
    fun filo(a: Float): Color = if (oscuro) p.LLAMA.copy(alpha = a.coerceIn(0f, 1f)) else if (noche) p.VIGILIA.copy(alpha = (a * 0.55f).coerceIn(0f, 1f)) else Color.Transparent

    /** El filo frío de la luz de guardia (Sueños oscuros). */
    fun filoFrio(a: Float): Color = if (dormida) p.GUARDIA.copy(alpha = a.coerceIn(0f, 1f)) else Color.Transparent

    /**
     * El color del ánimo en el cuerpo de una entrada: de noche conserva su tono de día y baja con la distancia a la vela (se
     * hunde en valor, no pierde el matiz); en la vigilia, tibio.
     */
    fun animo(t: Color, k: Float, frio: Float = 0f): Color {
        val q = k.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) enVigilia(t, q) else t
        val apagado = mezcla(Color(t.red * 0.26f, t.green * 0.26f, t.blue * 0.28f, t.alpha), p.NOCHE, 0.22f)
        val enc = mezcla(t, Color((t.red * 1.05f + 0.03f).coerceAtMost(1f), t.green * 0.96f, t.blue * 0.8f, t.alpha), 0.5f)
        val base = mezcla(apagado, enc, (1.12f * q.toDouble().pow(1.4).toFloat()).coerceAtMost(1f))
        val f = frio.coerceIn(0f, 1f)
        return if (f > 0.01f) mezcla(base, mezcla(apagado, alFrio(t), 0.7f), (0.7f * f).coerceAtMost(0.6f) * (1f - 0.6f * q)) else base
    }

    /** El latón de una placa con texto: se hunde menos, para que el día se lea; junto a la vela, encendido. */
    fun placa(k: Float, frio: Float = 0f): Color {
        val q = k.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) enVigilia(p.LATON_PLACA, q) else p.LATON_PLACA
        val b = mezcla(p.PLACA_NOCHE, p.PLACA_VELA, (q * 1.3f).coerceAtMost(1f))
        val lejos = mezcla(b, Color(0xFF4E4838), 0.5f * (1f - q))
        return if (frio > 0.05f) mezcla(lejos, Color(0xFF7E8A98), (0.5f * frio).coerceAtMost(0.4f) * (1f - q)) else lejos
    }

    /** La tinta de las placas: siempre más honda que el latón. */
    fun tinta(): Color = if (oscuro) Color(0xFF15120C) else p.TINTA
}

/** Un polígono cerrado por sus puntos (x, y, x, y…). */
internal fun poligonoOrden(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

/** La sombra de lo que pisa: un óvalo corrido hacia donde cae la luz ([hacia]: 1, a la derecha; -1, a la izquierda). */
internal fun DrawScope.contactoOrden(centro: Offset, ancho: Float, alto: Float, a: AireOrden, fuerza: Float = 0.34f, hacia: Float = 1f) =
    drawOval(a.sombra(if (a.oscuro) fuerza * 1.2f else fuerza), Offset(centro.x - ancho * (0.5f - 0.12f * hacia), centro.y - alto / 2), Size(ancho, alto))

/** Un remache de cabeza grande: su sombra abajo a la derecha, la cabeza del color [base] con su luz, y el destello. */
internal fun DrawScope.roblonOrden(c: Offset, r: Float, a: AireOrden, k: Float, base: Color = PaletaOrden.ACERO_MEDIO, frio: Float = 0f) {
    drawCircle(a.sombra(0.4f), r * 1.05f, Offset(c.x + r * 0.45f, c.y + r * 0.5f))
    drawCircle(a.luz(base, k, frio), r, c)
    drawCircle(a.destello(0.75f, k, frio), r * 0.34f, Offset(c.x - r * 0.35f, c.y - r * 0.38f))
}

/** Cómo se reparte la luz en un cuerpo redondo, de la luz a la sombra: dónde empieza cada franja y hacia qué va. */
private val FRANJAS_ORDEN = floatArrayOf(0f, 0.07f, 0.19f, 0.37f, 0.58f, 0.76f, 0.9f, 1f)
private val TONOS_FRANJA_ORDEN = floatArrayOf(0.26f, 0.55f, 0.3f, 0.04f, -0.22f, -0.46f, -0.7f)

/**
 * Un cuerpo redondo en [r] por franjas opacas (sin degradados), de [claro] (lo que mira a la luz) a [hondo], pasando por
 * [cara]; la luz viene de [hacia] (-1, la izquierda; 1, la derecha). [suave] baja el contraste.
 */
internal fun DrawScope.redondoOrden(r: Rect, cara: Color, claro: Color, hondo: Color, hacia: Float, suave: Float = 1f) {
    if (r.width <= 0f || r.height <= 0f) return
    for (i in 0 until 7) {
        val f0 = FRANJAS_ORDEN[i]; val f1 = FRANJAS_ORDEN[i + 1]
        val m = TONOS_FRANJA_ORDEN[i] * suave
        val c = if (m >= 0f) mezcla(cara, claro, m) else mezcla(cara, hondo, -m)
        val mas = if (i < 6) 1.2f else 0f
        val x0 = if (hacia < 0f) r.left + r.width * f0 else r.right - r.width * f1 - mas
        drawRect(c, Offset(x0, r.top), Size(r.width * (f1 - f0) + mas, r.height))
    }
}

/** La media luna recortada de los Sueños, de radio [r] centrada en el origen, armada una vez por tamaño. */
private var radioLunaOrden = -1f
private var caminoLunaOrden = Path()

internal fun lunaOrden(r: Float): Path {
    if (r != radioLunaOrden) {
        val fuera = Path().apply { addOval(Rect(-r, -r, r, r)) }
        val dentro = Path().apply { addOval(Rect(-r * 0.8f + r * 0.52f, -r * 0.86f - r * 0.24f, r * 0.8f + r * 0.52f, r * 0.86f - r * 0.24f)) }
        caminoLunaOrden = Path().apply { op(fuera, dentro, PathOperation.Difference) }
        radioLunaOrden = r
    }
    return caminoLunaOrden
}

/** La media luna de plata de los Sueños en [c], con su sombra y su canto. */
internal fun DrawScope.lunaPlataOrden(c: Offset, r: Float, a: AireOrden) {
    val u = density
    val m = lunaOrden(r)
    translate(c.x + 0.7f * u, c.y + 1f * u) { drawPath(m, a.sombra(0.55f)) }
    translate(c.x, c.y) {
        drawPath(m, if (a.oscuro) Color(0xFFD2DCE8) else Color(0xFFE8ECF0))
        drawPath(m, Color(0xFF262A34), style = Stroke(0.7f * u))
    }
}

// ---------------------------------------------------------------- el ánimo

/** Las escalas del ánimo de Orden, la de día y la de noche (bajo, medio, alto). */
private val TONOS_ORDEN: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.ORDEN, false) to juegoTonos(TemaId.ORDEN, true) }

private fun cuadradoOrden(x: Float) = x * x

/** Dónde cae un color en una escala (de 0, bajo, a 1, alto): el tramo y la fracción que más se le acercan; si no está, null. */
private fun enEscalaOrden(t: Color, e: List<Color>): Float? {
    if (e.size < 3) return null
    var mejor: Float? = null
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = e[seg]; val a1 = e[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoOrden(t.red - a0.red - dr * f) + cuadradoOrden(t.green - a0.green - dg * f) + cuadradoOrden(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    return mejor
}

/**
 * Los materiales del ánimo, de bajo a alto, en el orden de la escala del tema: el carmesí del estandarte (hondo y vivo), la vitela
 * gris de los manuales viejos (de valor medio, para que no se pierda en el acero ni compita con el latón) y el azul del acero
 * templado, del claro al hondo. Son el cuero de los manuales y de los carcajes y la lona de las carpetas.
 */
private val MATERIALES_ORDEN = listOf(Color(0xFF6E1420), Color(0xFFA8222E), Color(0xFFBFBAA8), Color(0xFF3F6A8E), Color(0xFF243F5E))

/**
 * El material de una entrada según su ánimo [t]: se ubica en la escala del tema y se toma el mismo punto en [MATERIALES_ORDEN].
 * En el modo oscuro la escala del tema cambia de colores (el de en medio pasa del gris acero claro a casi negro): aquí la
 * entrada es un objeto del cuarto, así que se ubica el color en la escala de noche y se toma el mismo punto de la de día (el
 * `tonoDeDia` de las otras bibliotecas); la luz dibujada lo apaga después con la distancia. Si no está en ninguna, queda igual.
 */
internal fun materialOrden(t: Color?, oscuro: Boolean): Color? {
    if (t == null) return null
    val (dia, noche) = TONOS_ORDEN
    val x = (if (oscuro) enEscalaOrden(t, noche) else null) ?: enEscalaOrden(t, dia) ?: return t
    val q = x.coerceIn(0f, 1f) * (MATERIALES_ORDEN.size - 1)
    val i = q.toInt().coerceAtMost(MATERIALES_ORDEN.size - 2)
    return mezcla(MATERIALES_ORDEN[i], MATERIALES_ORDEN[i + 1], q - i)
}
