package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontFamily
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble

/**
 * La paleta del estante de Horror cósmico: la sala de lectura de arriba del archivo de un pueblo de pescadores, hacia 1926. Lo
 * frío: el papel pintado de escamas verde mar, la niebla de la ventana, el vidrio de la lente y de las boyas, el cinc, el verdín
 * y la noche verde honda. Lo cálido: la teca del barco de la librería, el cobre de los clavos, el latón, la cera, las llamas. El
 * paño ciruela del fondo de los casilleros es el violeta del tema. Los acentos: el lacre, el rojo del minio que asoma en la teca.
 */
internal object PaletaArcano {
    /** El papel pintado de escamas: su fondo verde mar, la luz, la sombra, el trazo de las escamas y el filete de oro viejo. */
    val MAR = Color(0xFF3A675E)
    val MAR_LUZ = Color(0xFF6E9C8E)
    val MAR_OSC = Color(0xFF22423D)
    val ESCAMA = Color(0xFF2A4F48)
    val ORO = Color(0xFFB0965A)
    val ORO_OSC = Color(0xFF6E5A2E)
    /** El yeso del techo y de donde se despega el papel; el salobre de la humedad. */
    val YESO = Color(0xFFD2CDB6)
    val YESO_OSC = Color(0xFF8E8A72)
    val SALOBRE = Color(0xFF6E6A40)
    /** La teca de cubierta de la librería: su cara, su luz, su sombra y lo hondo; la teca lavada por el mar (gris plata). */
    val TECA = Color(0xFF8C5A30)
    val TECA_LUZ = Color(0xFFC08A52)
    val TECA_OSC = Color(0xFF5A3418)
    val TECA_HONDA = Color(0xFF2C170A)
    val TECA_GRIS = Color(0xFF9A907C)
    /** Lo que queda de la pintura del casco: el verde de la obra muerta y el minio de la obra viva. */
    val CASCO = Color(0xFF2E4A40)
    val MINIO = Color(0xFFAA4A2C)
    /** El paño ciruela del fondo de los casilleros: su cara, su luz, su sombra y lo hondo. */
    val PANO = Color(0xFF5E3052)
    val PANO_LUZ = Color(0xFF85547A)
    val PANO_OSC = Color(0xFF3A1C34)
    val PANO_HONDO = Color(0xFF1E0E1E)
    /** El cobre de los clavos y su verdín. */
    val COBRE = Color(0xFFB86A3E)
    val COBRE_LUZ = Color(0xFFEAA676)
    val VERDIN = Color(0xFF5FA08A)
    val VERDIN_OSC = Color(0xFF2E6656)
    /** El latón de la lente, del guardabrisas y de los herrajes; el bronce de los pernos. */
    val LATON = Color(0xFFC59E4E)
    val LATON_LUZ = Color(0xFFF2D892)
    val LATON_OSC = Color(0xFF6E5222)
    val BRONCE = Color(0xFF9A7036)
    /** El hierro (el pie de la lente, el farol) y el cinc del cubo. */
    val HIERRO = Color(0xFF2C3134)
    val HIERRO_LUZ = Color(0xFF7C878A)
    val OXIDO = Color(0xFF8A4A2C)
    val CINC = Color(0xFF98A3A4)
    val CINC_LUZ = Color(0xFFDCE4E2)
    val CINC_OSC = Color(0xFF545E62)
    /** El papel: el de los rollos y los legajos, el de las etiquetas (de valor medio), su sombra y la tinta. */
    val PAPEL = Color(0xFFE0D4B2)
    val MANILA = Color(0xFFCDB98A)
    val PAPEL_OSC = Color(0xFF9E8A62)
    val TINTA = Color(0xFF1C2622)
    /** El lacre, la piola alquitranada, el hule negro, el corcho y la lona cruda. */
    val LACRE = Color(0xFF8E2A22)
    val LACRE_LUZ = Color(0xFFC8503E)
    val PIOLA = Color(0xFF2E241A)
    val HULE = Color(0xFF242A2C)
    val CORCHO = Color(0xFFB08A5C)
    val LONA = Color(0xFFCDBE98)
    val LONA_OSC = Color(0xFF8E7E5C)
    /** El vidrio: el de la lente (aguamarina vieja), el de las boyas (verde) y su brillo. */
    val VIDRIO = Color(0xFFA6CCBF)
    val VIDRIO_OSC = Color(0xFF4E7A70)
    val BOYA = Color(0xFF3F8A62)
    val BOYA_OSC = Color(0xFF1C4A34)
    val VIDRIO_LUZ = Color(0xFFEAF6F0)
    /** La cera, la llama, su corazón y la brasa de la mecha. */
    val CERA = Color(0xFFECE2C6)
    val CERA_OSC = Color(0xFFB8A784)
    val LLAMA = Color(0xFFFFB55A)
    val FUEGO = Color(0xFFFFEAB6)
    val BRASA = Color(0xFFD8642E)
    /** La baldosa hidráulica: hueso y verde pizarra. */
    val HUESO = Color(0xFFD5CAAE)
    val PIZARRA = Color(0xFF3A514C)
    /** La niebla de la ventana: de día, de noche, el haz del faro y la del ocaso. */
    val NIEBLA = Color(0xFFCBD6CE)
    val NIEBLA_ALTA = Color(0xFFAFC0B8)
    val NIEBLA_NOCHE = Color(0xFF16302F)
    val NIEBLA_NOCHE_ALTA = Color(0xFF0B1C1E)
    val HAZ = Color(0xFFD6EEDF)
    val OCASO = Color(0xFFF4A072)
    val OCASO_ALTO = Color(0xFFB878A0)
    val NIEBLA_SUENOS = Color(0xFF1E2446)
    /** La noche: lo hondo de la pared (verde), del mueble (violeta), del paño y del suelo; y los de los Sueños oscuros. */
    val NOCHE_PARED = Color(0xFF0B2220)
    val NOCHE_MUEBLE = Color(0xFF22143A)
    val NOCHE_FONDO = Color(0xFF1E1244)
    val NOCHE_SUELO = Color(0xFF0E1A1E)
    val SUENOS_PARED = Color(0xFF101A38)
    val SUENOS_MUEBLE = Color(0xFF1A1030)
    val SUENOS_FONDO = Color(0xFF160E34)
    /** El calor de la pared junto a la llama (el verde mar a la luz de la vela) y el del mueble. */
    val CALIDO_PARED = Color(0xFFD8843C)
    val CALIDO_MUEBLE = Color(0xFFB0642A)
    /** El frío de la ventana de noche (la niebla alumbrada por el faro) y de la luna. */
    val FRIO_NOCHE = Color(0xFF3E7468)
    val LUNA = Color(0xFFE6EEEA)
    val LUNA_RAS = Color(0xFFA6CEC6)
    /** La luna de los Sueños oscuros en lo que alumbra por la ventana: un azul de pizarra con algo de verde. */
    val LUNA_NOCHE = Color(0xFF41628A)
    /** La sombra del ocaso (violeta) y la fría del día. */
    val SOMBRA_OCASO = Color(0xFF3E2A5C)
    val FRIO = Color(0xFF24323C)

    val LETRA: FontFamily = Letra.castoro
}

/**
 * El aire del momento: de día (la niebla de la ventana), de noche (el modo oscuro: arden el guardabrisas, la vela de un hueco y el
 * farol del suelo) y en los Sueños ([noche]: el ocaso en claro, la luna en oscuro). De noche cada color va de su hondo (oscuro, de
 * su tono, con el aire de la noche) a su color según cuánta luz le llega, con el calor de la llama encima. La pared se hunde en un
 * verde de fondo de agua; la teca y el paño ([parte]), en un violeta; el suelo, en un azul verdoso.
 */
internal class AireArcano(val oscuro: Boolean, val noche: Boolean, val parte: Int = PARED) {
    private val p = PaletaArcano
    /** Si arden las luces (de noche siempre; en los Sueños claros, recién prendidas). */
    val lumbre: Boolean get() = oscuro || noche

    val hondo: Color get() = when {
        oscuro && noche -> when (parte) { MUEBLE -> p.SUENOS_MUEBLE; FONDO -> p.SUENOS_FONDO; else -> p.SUENOS_PARED }
        oscuro -> when (parte) { MUEBLE -> p.NOCHE_MUEBLE; FONDO -> p.NOCHE_FONDO; SUELO -> p.NOCHE_SUELO; else -> p.NOCHE_PARED }
        noche -> p.SOMBRA_OCASO
        else -> sombraMueble(1f)
    }

    /** El color propio de algo, hundido: oscuro, algo de su tono, con el aire de la noche. */
    fun hundido(c: Color): Color {
        val n = hondo
        return Color(c.red * 0.14f + n.red * 0.86f, c.green * 0.14f + n.green * 0.86f, c.blue * 0.14f + n.blue * 0.86f, c.alpha)
    }

    /** A la luz de la vela: el rojo sube, el azul baja. */
    private fun aLaLlama(c: Color): Color = Color((c.red * 1.12f).coerceAtMost(1f), c.green * 0.88f, c.blue * 0.62f, c.alpha)

    /** Al ocaso de los Sueños claros: algo más bajo y más tibio. */
    private fun alOcaso(c: Color): Color = Color(c.red * 0.95f, c.green * 0.85f, c.blue * 0.84f, c.alpha)

    /** Cerca de la llama lo claro se vuelve miel, sin perder del todo su color. */
    private fun ambar(c: Color, k: Float): Color {
        val v = aLaLlama(c)
        val l = c.red * 0.3f + c.green * 0.59f + c.blue * 0.11f
        val tono = Color((l * 1.5f).coerceAtMost(1f), (l * 1.08f).coerceAtMost(1f), l * 0.55f, c.alpha)
        // La pared y el paño conservan más su color (verde, ciruela): la llama los tiñe, no los vuelve pardos.
        val f = if (parte == MUEBLE || parte == SUELO) 0.75f else 0.42f
        return mezcla(mezcla(c, v, f), tono, (if (parte == MUEBLE || parte == SUELO) 0.32f else 0.16f) * k)
    }

    /** El color de [c] con [cerca] de luz cálida (0 a 1). De día, tal cual. */
    fun luz(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(alOcaso(c), p.LLAMA, 0.1f * k) else c
        val f = (1.08f * Math.pow(k.toDouble(), 1.25).toFloat()).coerceAtMost(0.95f)
        return mezcla(mezcla(hundido(c), ambar(c, k), f), if (parte == MUEBLE) p.BRASA else p.LLAMA, 0.1f * k)
    }

    /** De día, más claro (positivo) o más hondo (negativo); de noche, como [luz]. */
    fun dia(c: Color, f: Float, cerca: Float = 1f): Color {
        if (oscuro) return luz(if (f > 0f) mezcla(c, luzMueble(1f), f * 0.5f) else if (f < 0f) mezcla(c, Color.Black, -f * 0.5f) else c, cerca)
        val b = if (noche) alOcaso(c) else c
        return if (f > 0f) mezcla(b, luzMueble(1f), f) else if (f < 0f) mezcla(b, hondo, -f) else b
    }

    /**
     * Lo que se pinta debajo del mapa de luz: el color a la luz entera (de noche, encendido y tibio; el mapa lo hunde con la
     * distancia a las llamas).
     */
    fun pleno(c: Color, f: Float = 0f): Color = if (oscuro) luz(if (f > 0f) mezcla(c, luzMueble(1f), f * 0.5f) else if (f < 0f) mezcla(c, Color.Black, -f * 0.5f) else c, 0.9f) else dia(c, f)

    fun sombra(a: Float): Color =
        if (oscuro) hondo.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else if (noche) hondo.copy(alpha = (a * 1.1f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** La luz crema del día; al ocaso, la rosa; de noche, casi nada. */
    fun claro(a: Float): Color = if (oscuro) luzMueble(a * 0.06f) else if (noche) p.OCASO.copy(alpha = (a * 0.55f).coerceIn(0f, 1f)) else luzMueble(a)

    /** El brillo de lo que mira a la luz: de día crema; al ocaso, rosa; de noche, el filo de la llama según [cerca]. */
    fun brillo(a: Float, cerca: Float): Color =
        if (!oscuro) (if (noche) mezcla(luzMueble(1f), p.OCASO, 0.55f).copy(alpha = (a * 0.75f).coerceIn(0f, 1f)) else luzMueble(a))
        else p.LLAMA.copy(alpha = (a * 0.95f * cerca).coerceIn(0f, 1f))

    /** El filo de la llama en lo que la mira; al ocaso, el del sol rasante; de día no hay. */
    fun filo(a: Float): Color = if (oscuro) p.LLAMA.copy(alpha = a.coerceIn(0f, 1f)) else if (noche) p.OCASO.copy(alpha = (a * 0.6f).coerceIn(0f, 1f)) else Color.Transparent

    /** El roce frío de la ventana: de día, la niebla; de noche, el faro; en los Sueños oscuros, la luna (más). */
    fun frio(a: Float): Color = when {
        oscuro -> p.LUNA_RAS.copy(alpha = (if (noche) a * 1.3f else a * 0.8f).coerceIn(0f, 1f))
        noche -> p.OCASO.copy(alpha = (a * 0.7f).coerceIn(0f, 1f))
        else -> mezcla(p.NIEBLA, Color.White, 0.4f).copy(alpha = (a * 0.8f).coerceIn(0f, 1f))
    }

    /** El color del ánimo: de noche conserva su tono de día y solo baja con la distancia a la luz; al ocaso, tibio. */
    fun animo(t: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) alOcaso(t) else t
        val apagado = mezcla(Color(t.red * 0.2f, t.green * 0.17f, t.blue * 0.2f, t.alpha), hondo, 0.35f)
        val encendido = mezcla(t, aLaLlama(t), 0.45f)
        return mezcla(apagado, encendido, (1.1f * Math.pow(k.toDouble(), 1.15).toFloat()).coerceAtMost(1f))
    }

    /** El papel de una etiqueta con la luz que le llega: ámbar cerca de la llama, pardo verdoso hondo lejos; siempre legible. */
    fun etiqueta(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) alOcaso(c) else c
        val lejos = Color(c.red * 0.3f, c.green * 0.25f, c.blue * 0.19f, c.alpha)
        val alumbrado = Color((c.red * 1.04f).coerceAtMost(1f), c.green * 0.9f, c.blue * 0.66f, c.alpha)
        return mezcla(lejos, alumbrado, Math.pow(k.toDouble(), 1.4).toFloat().coerceAtMost(1f))
    }

    companion object {
        const val PARED = 0
        const val MUEBLE = 1
        const val FONDO = 2
        const val SUELO = 3
    }
}

/**
 * Lo que va encima de lo pintado a la luz entera, en ([x], [y]) (anchos), con [k] de luz cálida y [kf] del frío de la ventana:
 * de noche, el oscuro con color del aire [a] tanto como falte la luz, con el calor de la llama cerca y, junto a la ventana, la
 * niebla verde alumbrada por el faro; al ocaso, la sombra violeta que crece lejos de la ventana; de día, la sombra fría lejos
 * de la ventana.
 */
internal fun mascaraArcano(a: AireArcano, x: Float, y: Float, k: Float, kf: Float): Color {
    val P = PaletaArcano
    if (!a.lumbre) {
        val lejos = ((x - 0.2f).coerceAtLeast(0f) * 0.16f + (y - 0.3f).coerceAtLeast(0f) * 0.05f).coerceIn(0f, 0.22f)
        return P.FRIO.copy(alpha = lejos * (1f - 0.6f * kf))
    }
    if (!a.oscuro) {
        // El ocaso: la luz rosa de la ventana tiñe lo que tiene cerca; lejos (a la derecha y abajo) crece la sombra violeta.
        val tarde = (0.26f + 0.46f * ((x - 0.15f) * 0.8f + (y - 0.4f) * 0.3f).coerceIn(0f, 1f)) * (1f - k) * (1f - 0.85f * kf)
        val calido = 0.2f * k + 0.46f * kf
        val al = (tarde + calido).coerceIn(0f, 1f)
        val tibio = mezcla(P.LLAMA, P.OCASO, (kf / maxOf(0.01f, kf + k)).coerceIn(0f, 1f))
        return mezcla(P.SOMBRA_OCASO, tibio, if (al > 0.001f) calido / al else 0f).copy(alpha = al)
    }
    val pared = a.parte == AireArcano.PARED
    val kk = if (pared) Math.pow(k.toDouble(), 1.35).toFloat() else k
    val hondo = Math.pow((1f - kk).toDouble(), if (pared) 0.62 else 0.66).toFloat() * (if (pared) 0.96f else 0.95f)
    val nucleo = ((kk - (if (pared) 0.22f else 0.55f)) / (if (pared) 0.7f else 0.42f)).coerceIn(0f, 1f)
    val f = nucleo * nucleo * (3f - 2f * nucleo)
    // La ventana: lo lejano de la llama, junto a ella, se hunde menos y en un verde frío (la niebla alumbrada).
    val q = (kf * (1f - kk)).coerceIn(0f, 1f)
    val base = mezcla(a.hondo, if (a.noche) P.LUNA_NOCHE else P.FRIO_NOCHE, (if (a.noche) 0.65f else 0.45f) * q)
    val al = maxOf(hondo * (1f - (if (a.noche) 0.5f else 0.35f) * q), (if (pared) 0.5f else 0.5f) * f)
    return mezcla(base, if (pared) P.CALIDO_PARED else P.CALIDO_MUEBLE, f).copy(alpha = al)
}

/** Un polígono cerrado por sus puntos (x, y, x, y…). */
internal fun poligonoArcano(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

/** La sombra de lo que pisa: un óvalo corrido hacia donde cae la luz ([hacia]: 1, la luz a la derecha; -1, a la izquierda). */
internal fun DrawScope.contactoArcano(centro: Offset, ancho: Float, alto: Float, a: AireArcano, fuerza: Float = 0.36f, hacia: Float = -1f) =
    drawOval(a.sombra(if (a.oscuro) fuerza * 1.2f else fuerza), Offset(centro.x - ancho * (0.5f + 0.12f * hacia), centro.y - alto / 2), Size(ancho, alto))

/** Un clavo o perno de cabeza redonda: su sombra, la cabeza ([metal]) y el punto de luz. */
internal fun DrawScope.clavoArcano(p: Offset, r: Float, a: AireArcano, k: Float, metal: Color = PaletaArcano.COBRE) {
    drawCircle(a.sombra(0.4f), r * 1.05f, p + Offset(r * 0.45f, r * 0.55f))
    drawCircle(a.luz(metal, k), r, p)
    drawCircle(a.brillo(0.6f, k), r * 0.38f, p - Offset(r * 0.32f, r * 0.32f))
}

/** El destello de la luz en el latón o el vidrio (de día, crema; de noche, el de la llama si le llega). */
internal fun DrawScope.destelloArcano(c: Offset, r: Float, a: AireArcano, k: Float) {
    if (!a.oscuro) { drawCircle(a.claro(0.9f), r, c); return }
    if (k < 0.05f) return
    drawCircle(PaletaArcano.LLAMA.copy(alpha = (0.4f * k).coerceAtMost(0.35f)), r * 2.2f, c)
    drawCircle(PaletaArcano.FUEGO.copy(alpha = (1.1f * k).coerceAtMost(0.95f)), r, c)
}

/** Un cuerpo redondo de pie dentro de [r] (ir dentro de su recorte): el claro hacia la luz y la sombra al otro lado. */
internal fun DrawScope.cilindroArcano(r: Rect, a: AireArcano, k: Float, hacia: Float, fuerza: Float = 1f) {
    val der = hacia > 0f
    fun x(f: Float, an: Float) = if (der) r.right - r.width * (f + an) else r.left + r.width * f
    drawRect(a.brillo(0.16f * fuerza, k), Offset(x(0f, 0.34f), r.top), Size(r.width * 0.34f, r.height))
    drawRect(a.brillo(0.32f * fuerza, k), Offset(x(0.14f, 0.09f), r.top), Size(r.width * 0.09f, r.height))
    drawRect(a.sombra(0.16f * fuerza), Offset(x(0.56f, 0.44f), r.top), Size(r.width * 0.44f, r.height))
    drawRect(a.sombra(0.26f * fuerza), Offset(x(0.8f, 0.2f), r.top), Size(r.width * 0.2f, r.height))
}

/** Las escalas del ánimo de Horror cósmico, la de día y la de noche (bajo, medio, alto), para volver a la de día. */
private val TONOS_ARCANO: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.ARCANO, false) to juegoTonos(TemaId.ARCANO, true) }

private fun cuadradoArcano(x: Float) = x * x

/** Dónde cae [t] en la escala [escala] (bajo, medio, alto): de 0 a 1 a lo largo de ella, o null si no está en ella. */
private fun enEscalaArcano(t: Color, escala: List<Color>): Float? {
    if (escala.size < 3) return null
    var mejor: Float? = null
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = escala[seg]; val a1 = escala[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoArcano(t.red - a0.red - dr * f) + cuadradoArcano(t.green - a0.green - dg * f) + cuadradoArcano(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    return mejor
}

/**
 * Dónde cae el ánimo [t] en la escala del tema (0, lo bajo: el rojo de sangre de buey; 0.5, el papel; 1, lo alto: el verde de
 * fondo de mar), o null si no hay ánimo o no está en ella. En el modo oscuro la escala del tema cambia de colores (el de en medio
 * pasa del papel a un casi negro): aquí la entrada es un objeto de la sala, así que se ubica en la escala de noche y se toma el
 * mismo punto en la de día; después la luz dibujada lo apaga con la distancia.
 */
internal fun puntoAnimoArcano(t: Color?, oscuro: Boolean): Float? {
    if (t == null) return null
    val (dia, noche) = TONOS_ARCANO
    return (if (oscuro) enEscalaArcano(t, noche) else null) ?: enEscalaArcano(t, dia)
}

/** El color de [x] (0 a 1) en una rampa de puntos (posición, color). */
internal fun rampaArcano(x: Float, rampa: List<Pair<Float, Color>>): Color {
    for (i in 0 until rampa.size - 1) {
        val (f0, c0) = rampa[i]; val (f1, c1) = rampa[i + 1]
        if (x <= f1) return mezcla(c0, c1, ((x - f0) / maxOf(1e-4f, f1 - f0)).coerceIn(0f, 1f))
    }
    return rampa.last().second
}
