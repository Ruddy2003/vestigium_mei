package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.withTransform
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CAL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.ESTUCO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.FRIO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.ALMAGRA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.ANOCHECER
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.BRONCE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CEDRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CEDRO_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CEDRO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CIELO_DIA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CIELO_NOCHE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.LLAMA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.LOSA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.LUNA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.LUZ_LUNA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.NOCHE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.NOCHE_SUENOS
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.PERGAMINO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.PIEDRA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.PIEDRA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.SOL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.TINTA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.YESO
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoAndalus.borde
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoAndalus.corrido
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoAndalus.poligono
import io.github.ruddy2003.vestigium.ui.ideario.arcoHerradura
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.pow

// =====================================================================
//  Al-Ándalus: la paleta, el aire de cada momento, las formas comunes, el muro, la ventana y el suelo de la biblioteca
// =====================================================================

/**
 * La paleta de la biblioteca de Al-Ándalus (sale de la del cuadro y de la del Ideario, en otra disposición): la piedra dorada
 * de Córdoba, el yeso y la almagra de las dovelas, el cedro, el bronce, el cuero, el pergamino, el barro y el vidrio.
 */
internal object PaletaAndalus {
    /** La calcarenita dorada de los sillares, su cara más oscura y la losa gris del suelo. */
    val PIEDRA = Color(0xFFD9B57E)
    val PIEDRA_OSC = Color(0xFFAC8656)
    val LOSA = Color(0xFFB4A68A)
    /** La cal con que está encalado el muro, y el tono frío que le pone la luna de noche, hacia la ventana. */
    val CAL = Color(0xFFF2EBDC)
    val FRIO = Color(0xFF5C6E9C)
    /** El estuco pintado del fondo de los casilleros: un índigo apagado, encalado y gastado. */
    val ESTUCO = Color(0xFF4E6676)
    /** La loza blanca de estaño, el verde de cobre y las sedas: índigo y verde. */
    val LOZA = Color(0xFFF1EADA)
    val VERDE = Color(0xFF2E7D5E)
    val SEDA_ANIL = Color(0xFF2B4C86)
    val SEDA_VERDE = Color(0xFF2C7262)
    /** El yeso de las dovelas claras, del alfiz y de la celosía; la almagra de las dovelas de ladrillo. */
    val YESO = Color(0xFFEDE2C8)
    val ALMAGRA = Color(0xFFA9482F)
    /** El cedro del mueble: el de siempre, su luz y lo hondo de la talla. */
    val CEDRO = Color(0xFF7E4E2C)
    val CEDRO_LUZ = Color(0xFFB07E4C)
    val CEDRO_OSC = Color(0xFF3E2414)
    /** El bronce del candil y del astrolabio, el oro de los dorados. */
    val BRONCE = Color(0xFFB7853E)
    val ORO = Color(0xFFD8AE58)
    /** El cuero (cordobán sin teñir) y su hondo; el pergamino de etiquetas y hojas; la tinta. */
    val CUERO = Color(0xFF8A5530)
    val CUERO_OSC = Color(0xFF4A2A1A)
    val PERGAMINO = Color(0xFFEDE0BE)
    val TINTA = Color(0xFF2A1A12)
    /** El barro claro de la alcarraza, la loza melada con su manganeso y el vidrio verdoso. */
    val BARRO = Color(0xFFDCCBA4)
    val MELADO = Color(0xFFC08A30)
    val MANGANESO = Color(0xFF3E2A26)
    val VIDRIO = Color(0xFF86B49C)
    /** La madera clara de la tapa del laúd y del atril, y la seda carmesí e índigo de cabezadas y borlas. */
    val PINO = Color(0xFFD2AC72)
    val CARMESI = Color(0xFFB02A3C)
    val ANIL = Color(0xFF2F5C88)

    // Las luces: la noche del cuarto, la de los Sueños (más azul), el anochecer de los Sueños en claro, la llama, la luna.
    val NOCHE = Color(0xFF151A2E)
    val NOCHE_SUENOS = Color(0xFF0F1736)
    val ANOCHECER = Color(0xFF2E2F5E)
    val LLAMA = Color(0xFFFFC56E)
    val LUNA = Color(0xFFEDEFF5)
    /** La luz fría que deja la luna al pasar por la celosía, y la del sol, cálida. */
    val LUZ_LUNA = Color(0xFFBFD0F0)
    val SOL = Color(0xFFFFE9B8)
    /** El cielo que se ve por los huecos de la celosía: de día, de noche. */
    val CIELO_DIA = Color(0xFFF8F0DA)
    val CIELO_NOCHE = Color(0xFF1E2A52)
}

/**
 * El aire del momento en la biblioteca de Al-Ándalus: de día, de noche (el modo oscuro) y en los Sueños ([noche]: al anochecer
 * en claro, la noche más azul en oscuro). De él salen el color de cada cosa según cuánta luz del candil le llega, la sombra, la
 * luz, el filo y la luz fría de la luna que entra por la celosía.
 */
internal class AireAndalus(val oscuro: Boolean, val noche: Boolean) {
    /** Si arde el candil. */
    val lumbre: Boolean get() = oscuro || noche
    /** El color en que se hunde lo que no se alumbra. */
    val hondo: Color get() = if (oscuro) (if (noche) NOCHE_SUENOS else NOCHE) else SOMBRA_MUEBLE

    fun luz(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(mezcla(c, ANOCHECER, 0.4f - 0.18f * k), FILO_MUEBLE, 0.12f * k) else c
        return mezcla(mezcla(c, hondo, 0.88f - 0.78f * k.pow(0.95f)), FILO_MUEBLE, 0.2f * k)
    }
    fun sombra(a: Float): Color = if (oscuro) hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)
    fun claro(a: Float): Color = luzMueble(if (oscuro) a * 0.15f else if (noche) a * 0.55f else a)
    fun filo(a: Float): Color = FILO_MUEBLE.copy(alpha = if (oscuro) a.coerceIn(0f, 1f) else if (noche) (a * 0.4f).coerceIn(0f, 1f) else 0f)
    /** El color del ánimo: de noche se apaga menos que lo demás, para que se siga viendo en la repisa más lejana. */
    fun animo(t: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(t, ANOCHECER, 0.2f) else t
        return mezcla(mezcla(t, hondo, 0.62f - 0.52f * k), FILO_MUEBLE, 0.1f * k)
    }
    /** El pergamino de las etiquetas: casi no se apaga, para que el día se lea siempre. */
    fun etiqueta(cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(PERGAMINO, ANOCHECER, 0.12f) else mezcla(PERGAMINO, Color.White, 0.2f)
        return mezcla(mezcla(PERGAMINO, hondo, 0.4f - 0.32f * k), FILO_MUEBLE, 0.16f * k)
    }
    val tinta: Color get() = if (oscuro) mezcla(TINTA, hondo, 0.5f) else TINTA
    /** La luz que entra por la celosía: de día, la del sol; de noche y en los Sueños, la de la luna, fría. */
    fun celosia(a: Float): Color = if (lumbre) LUZ_LUNA.copy(alpha = (if (oscuro) a * 0.85f else a * 0.6f).coerceIn(0f, 1f)) else SOL.copy(alpha = a.coerceIn(0f, 1f))
}

/** Formas que usan los tres archivos de la biblioteca de Al-Ándalus. */
internal object TrazoAndalus {
    fun corrido(p: Path, dx: Float, dy: Float = 0f) = Path().apply { addPath(p, Offset(dx, dy)) }

    /** Lo que queda de [p] al quitarle la misma forma corrida: una media luna en el borde contrario al corrimiento. */
    fun borde(p: Path, dx: Float, dy: Float) = Path().apply { op(p, corrido(p, dx, dy), PathOperation.Difference) }

    fun ovalo(c: Offset, rx: Float, ry: Float) = Path().apply { addOval(Rect(c.x - rx, c.y - ry, c.x + rx, c.y + ry)) }

    fun poligono(vararg xy: Float) = Path().apply {
        moveTo(xy[0], xy[1])
        for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
        close()
    }

    /** Une [q] con curvas suaves (Catmull-Rom pasado a Bézier). */
    fun suave(p: Path, q: List<Offset>) {
        for (i in 0 until q.size - 1) {
            val a = q[maxOf(i - 1, 0)]; val b = q[i]; val c = q[i + 1]; val d = q[minOf(i + 2, q.size - 1)]
            val c1 = b + (c - a) / 6f; val c2 = c - (d - b) / 6f
            p.cubicTo(c1.x, c1.y, c2.x, c2.y, c.x, c.y)
        }
    }

    /** El contorno de algo torneado: [p] son pares (medio ancho, altura) de arriba abajo, en unidades de [s] desde [cx] y [y0]. */
    fun perfil(cx: Float, y0: Float, s: Float, p: FloatArray): Path {
        val der = List(p.size / 2) { Offset(cx + p[2 * it] * s, y0 + p[2 * it + 1] * s) }
        val izq = der.reversed().map { Offset(2 * cx - it.x, it.y) }
        return Path().apply { moveTo(der[0].x, der[0].y); suave(this, der); lineTo(izq[0].x, izq[0].y); suave(this, izq); close() }
    }

    /**
     * El volumen de gouache sobre algo redondo ya pintado: la media luna de sombra del lado contrario a la luz, el claro del
     * otro y, con [brillo], el del vidriado o del metal. De día la luz viene de la izquierda; de noche, de donde esté el candil
     * ([haciaX]: 1 a la derecha, -1 a la izquierda), con un filo cálido tanto más fuerte cuanto más [cerca].
     */
    fun DrawScope.redondo(forma: Path, a: AireAndalus, cerca: Float, haciaX: Float, brillo: Float = 0f, fuerza: Float = 1f) {
        val r = forma.getBounds(); val an = r.width; val hh = r.height
        val sl = if (a.oscuro) haciaX else -1f
        drawPath(borde(forma, sl * an * 0.26f, -hh * 0.02f), a.sombra(fuerza * 0.2f))
        drawPath(borde(forma, sl * an * 0.11f, -hh * 0.01f), a.sombra(fuerza * 0.2f))
        clipPath(forma) {
            drawOval(if (a.oscuro) FILO_MUEBLE.copy(alpha = 0.26f * cerca) else a.claro(0.2f), Offset(if (sl < 0) r.left + an * 0.1f else r.right - an * 0.36f, r.top + hh * 0.06f), Size(an * 0.26f, hh * 0.72f))
            if (brillo > 0f) drawRoundRect(if (a.oscuro) FILO_MUEBLE.copy(alpha = (brillo * 1.6f * cerca).coerceAtMost(0.7f)) else a.claro(brillo),
                Offset(if (sl < 0) r.left + an * 0.21f else r.right - an * 0.255f, r.top + hh * 0.14f), Size(an * 0.05f, hh * 0.44f), CornerRadius(an * 0.03f))
        }
        if (a.oscuro && cerca > 0.03f) drawPath(borde(forma, -sl * an * 0.04f, hh * 0.03f), FILO_MUEBLE.copy(alpha = (0.05f + 0.6f * cerca).coerceAtMost(0.65f)))
    }

    /** La sombra de lo que pisa: un óvalo corrido hacia donde cae la luz. */
    fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: AireAndalus, fuerza: Float = 0.34f) =
        drawOval(a.sombra(if (a.oscuro) fuerza * 1.3f else fuerza), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))
}

// ---------------------------------------------------------------- el muro

/** Dónde está la ventana, en anchos del mueble: el centro de su celosía (de ahí sale la luz fría de la luna). */
internal const val VENTANA_X_ANDALUS = 0.185f
internal const val VENTANA_Y_ANDALUS = 0.235f

/**
 * La sillería a soga y tizón de la Córdoba califal, encalada: hiladas iguales en que alterna un sillar largo (a soga) con dos
 * cortos (a tizón), cada uno de su tono bajo la cal, con sus juntas, la cal gastada a trechos y la mancha de luz arriba a la
 * izquierda. De noche, el charco cálido del candil y, hacia la ventana, el frío de la luna.
 */
internal fun DrawScope.muroAndalus(a: AireAndalus, hasta: Float, cerca: (Offset) -> Float) {
    val u = density; val w = size.width
    // Una hilada mide lo que un sillar de alto; el sillar a soga es casi tres veces más largo que el tizón.
    val hc = maxOf(w * 0.105f, 6f * u)
    val juntas = Path(); val filos = Path(); val bajos = Path(); val mellas = Path()
    val ventana = Offset(w * VENTANA_X_ANDALUS, w * VENTANA_Y_ANDALUS)
    val frio = mezcla(FRIO, a.hondo, if (a.oscuro) 0.5f else 0.1f)
    val llama = Offset(w * MuebleEstanteAndalus.LAMP_X, w * MuebleEstanteAndalus.LAMP_Y)
    var y = 0f; var f = 0
    while (y < hasta && f < 300) {
        val y1 = minOf(y + hc, hasta)
        var x = -hc * (0.3f + 2.2f * azar(1901, f))
        var j = 0
        while (x < w && j < 60) {
            // A soga y tizón: un sillar largo y luego dos cabezas cuadradas.
            val soga = (j + f) % 3 == 0
            val largo = if (soga) hc * (2.5f + 0.5f * azar(1902 + f, j)) else hc * (0.72f + 0.12f * azar(1903 + f, j))
            val r = Rect(x, y, x + largo, y1)
            val piedra = mezcla(PIEDRA, if (azar(1904 + f, j) < 0.34f) YESO else PIEDRA_OSC, (if (soga) 0.04f else 0.18f) + 0.3f * azar(1905 + f, j))
            // El charco del candil en el muro: más corto que el de la curva del mueble, para que caiga claro hacia los lados.
            val kl = (1f - (r.center - llama).getDistance() / (w * 0.72f)).coerceIn(0f, 1f).pow(1.5f) * 0.72f
            var c = a.luz(mezcla(piedra, CAL, 0.58f + 0.12f * azar(1910 + f, j)), kl)
            if (a.lumbre) {
                val m = (1f - (r.center - ventana).getDistance() / (w * 0.55f)).coerceIn(0f, 1f)
                c = mezcla(c, frio, (if (a.oscuro) 0.55f else 0.3f) * m)
            }
            drawRect(c, r.topLeft, r.size)
            juntas.moveTo(x, y); juntas.lineTo(x, y1)
            // Cada sillar: la arista de arriba y la de la izquierda toman la luz; la de abajo se gasta y hace sombra.
            filos.moveTo(x + 1.4f * u, y1 - 2f * u); filos.lineTo(x + 1.4f * u, y + 1.6f * u); filos.lineTo(x + largo - 2f * u, y + 1.6f * u)
            bajos.moveTo(x + 2f * u, y1 - 1.6f * u); bajos.lineTo(x + largo - 1.2f * u, y1 - 1.6f * u); bajos.lineTo(x + largo - 1.2f * u, y + 2f * u)
            if (azar(1906 + f, j) < 0.35f) {
                val mx = x + largo * (0.2f + 0.6f * azar(1907 + f, j)); val my = if (azar(1908 + f, j) < 0.5f) y + 1.5f * u else y1 - 1.5f * u
                val mr = hc * (0.06f + 0.06f * azar(1909 + f, j))
                mellas.addOval(Rect(mx - mr * 1.6f, my - mr * 0.6f, mx + mr * 1.6f, my + mr * 0.6f))
            }
            x += largo; j++
        }
        juntas.moveTo(0f, y1); juntas.lineTo(w, y1)
        y = y1; f++
    }
    // La cal gastada: a trechos se ve la piedra dorada de debajo.
    val gastado = Path()
    for (k in 0 until (14 * (hasta / w).coerceIn(1f, 12f)).toInt()) {
        val o = Offset(w * azar(1917, k), hasta * azar(1918, k))
        val rr = w * (0.012f + 0.03f * azar(1919, k))
        for (q in 0 until 3) gastado.addOval(Rect(o.x - rr + q * rr * 0.5f, o.y - rr * 0.4f + q * rr * 0.12f, o.x + rr * 0.4f + q * rr * 0.5f, o.y + rr * 0.3f + q * rr * 0.12f))
    }
    drawPath(gastado, a.luz(PIEDRA, 0.12f).copy(alpha = 0.28f))
    drawPath(mellas, a.sombra(0.2f))
    drawPath(bajos, a.sombra(if (a.oscuro) 0.3f else 0.17f), style = Stroke(2.4f * u))
    drawPath(filos, a.claro(0.4f), style = Stroke(1.3f * u))
    drawPath(juntas, a.luz(mezcla(CAL, PIEDRA_OSC, 0.3f), 0.1f), style = Stroke(1.6f * u))
    drawPath(juntas, a.sombra(if (a.oscuro) 0.36f else 0.22f), style = Stroke(0.7f * u))
    val area = Rect(0f, 0f, w, hasta)
    val veces = (hasta / (w * 0.6f)).coerceIn(1f, 14f)
    pinceladas(area, a.luz(mezcla(CAL, SOMBRA_MUEBLE, 0.35f), 0.1f), 1911, (26 * veces).toInt(), u, 0.08f)
    pinceladas(area, a.luz(mezcla(CAL, LUZ_MUEBLE, 0.6f), 0.1f), 1912, (22 * veces).toInt(), u, 0.1f)
    // La mancha de luz del día, arriba a la izquierda, alrededor de la ventana; hacia la derecha, la penumbra violeta.
    if (!a.oscuro) mancha(Offset(w * 0.18f, w * 0.2f), w * 0.95f, luzMueble(if (a.noche) 0.1f else 0.42f))
    pinceladas(Rect(w * 0.55f, 0f, w, w * MuebleEstanteAndalus.CORONA), a.sombra(1f), 1916, 22, u, 0.07f)
}

/**
 * La ventana de herradura, arriba a la izquierda: el alfiz con sus albanegas de ataurique, las dovelas alternas de almagra y
 * yeso, el derrame del muro (en sombra el lado izquierdo y el intradós; a la luz el derecho y el alféizar, con la trama de
 * la celosía) y, al fondo, la celosía de piedra de círculos secantes: por sus huecos, el cielo; de noche, la luna.
 */
internal fun DrawScope.ventanaAndalus(a: AireAndalus) {
    val u = density; val w = size.width
    val alfiz = Rect(w * 0.04f, w * 0.02f, w * 0.33f, w * 0.414f)
    val arco = Rect(w * 0.074f, w * 0.05f, w * 0.296f, w * 0.404f)
    val intra = Rect(w * 0.099f, w * 0.075f, w * 0.271f, w * 0.404f)
    val celo = Rect(w * 0.126f, w * 0.112f, w * 0.244f, w * 0.358f)
    // De noche el marco queda lejos del candil: casi se pierde.
    val lejos = if (a.oscuro) 0.02f else 0.12f
    // El alfiz: un marco de yeso que sobresale del muro, con su sombra a la derecha y abajo.
    drawRect(a.sombra(0.28f), alfiz.topLeft + Offset(2.4f * u, 2.6f * u), alfiz.size)
    drawRect(a.luz(YESO, lejos), alfiz.topLeft, alfiz.size)
    pinceladas(alfiz, a.luz(mezcla(YESO, PIEDRA_OSC, 0.5f), lejos), 1931, 14, u, 0.08f)
    val dentro = alfiz.deflate(w * 0.009f)
    drawRect(a.luz(mezcla(YESO, PIEDRA, 0.3f), lejos), dentro.topLeft, dentro.size)
    drawRect(a.claro(0.5f), alfiz.topLeft, Size(alfiz.width, 1.2f * u))
    drawRect(a.sombra(0.28f), dentro.topLeft, Size(dentro.width, 1.2f * u))
    drawRect(a.sombra(0.2f), dentro.topLeft, Size(1.1f * u, dentro.height))
    albanegasAndalus(dentro, a, lejos)
    // El arco de dovelas alternas.
    val exterior = arcoHerradura(arco)
    val interior = arcoHerradura(intra)
    val anillo = Path().apply { op(exterior, interior, PathOperation.Difference) }
    drawPath(corrido(exterior, 1.6f * u, 1.8f * u), a.sombra(0.3f))
    drawPath(anillo, a.luz(YESO, lejos))
    val rad = arco.width / 2 / 0.906f
    val centro = Offset(arco.center.x, arco.top + rad)
    val arranque = centro.y + rad * 0.423f
    clipPath(anillo) {
        val dovelas = 13
        for (k in 0 until dovelas) {
            if (k % 2 == 1) continue
            val a0 = Math.toRadians(155.0 + 230.0 * k / dovelas); val a1 = Math.toRadians(155.0 + 230.0 * (k + 1) / dovelas)
            val lejosR = rad * 2f
            drawPath(poligono(centro.x, centro.y + rad * 0.25f, centro.x + lejosR * kotlin.math.cos(a0).toFloat(), centro.y + lejosR * kotlin.math.sin(a0).toFloat(),
                centro.x + lejosR * kotlin.math.cos(a1).toFloat(), centro.y + lejosR * kotlin.math.sin(a1).toFloat()), a.luz(ALMAGRA, lejos))
        }
        drawRect(a.luz(YESO, lejos), Offset(arco.left, arranque), Size(arco.width, arco.bottom - arranque))
        pinceladas(arco, a.luz(mezcla(ALMAGRA, SOMBRA_MUEBLE, 0.3f), lejos), 1932, 12, u, 0.1f)
    }
    drawPath(borde(anillo, 1.2f * u, 1.4f * u), a.claro(0.35f))
    drawPath(borde(exterior, -1f * u, -1f * u), a.sombra(0.3f))
    // El derrame: el grueso del muro entre el arco y la celosía.
    val hueco = arcoHerradura(celo)
    clipPath(interior) {
        drawRect(a.luz(mezcla(YESO, PIEDRA, 0.4f), lejos), intra.topLeft, intra.size)
        val luz = poligono(celo.center.x, celo.top, intra.right, intra.top + intra.width * 0.5f, intra.right, intra.bottom, intra.left, intra.bottom, celo.left, celo.bottom, celo.right, celo.bottom, celo.right, celo.top + celo.width * 0.4f)
        drawPath(luz, a.luz(mezcla(YESO, LUZ_MUEBLE, if (a.lumbre) 0.1f else 0.45f), lejos))
        drawPath(poligono(intra.left, intra.top, celo.left, celo.top, celo.left, celo.bottom, intra.left, intra.bottom), a.sombra(if (a.lumbre) 0.3f else 0.22f))
        drawRect(a.sombra(0.16f), intra.topLeft, Size(intra.width, (celo.top - intra.top) * 1.3f))
        // La trama de la celosía, que la luz dibuja en el alféizar y en el lado derecho.
        tramaAndalus(Rect(celo.left + w * 0.006f, celo.bottom + w * 0.004f, celo.right - w * 0.01f, intra.bottom - w * 0.006f), a, 0.7f, 0.42f, 0.5f, 0.02f, blando = 0.5f)
        drawLine(a.sombra(0.25f), Offset(celo.left, celo.bottom), Offset(intra.left, intra.bottom), 0.8f * u)
        drawLine(a.sombra(0.2f), Offset(celo.right, celo.bottom), Offset(intra.right, intra.bottom), 0.8f * u)
    }
    // La celosía: el cielo por los huecos y la piedra calada de círculos secantes.
    clipPath(hueco) {
        val cielo = when {
            a.oscuro -> if (a.noche) mezcla(CIELO_NOCHE, NOCHE_SUENOS, 0.3f) else CIELO_NOCHE
            a.noche -> Color(0xFF6F5F92)
            else -> CIELO_DIA
        }
        drawRect(cielo, celo.topLeft, celo.size)
        if (!a.lumbre) { drawRect(Color.White.copy(alpha = 0.6f), celo.topLeft, celo.size); drawRect(Color(0xFFDDE6EC).copy(alpha = 0.4f), celo.topLeft, Size(celo.width, celo.height * 0.35f)) }
        if (a.noche && !a.oscuro) drawRect(Color(0xFFE8A27A).copy(alpha = 0.45f), Offset(celo.left, celo.bottom - celo.height * 0.35f), Size(celo.width, celo.height * 0.35f))
        if (a.lumbre) {
            val luna = Offset(celo.center.x + celo.width * 0.12f, celo.top + celo.height * 0.3f)
            mancha(luna, celo.width * 0.7f, LUZ_LUNA.copy(alpha = 0.5f))
            drawCircle(LUNA, celo.width * 0.21f, luna)
            drawCircle(mezcla(LUNA, CIELO_NOCHE, 0.25f), celo.width * 0.05f, luna + Offset(celo.width * 0.06f, celo.width * 0.04f))
        }
        val s = celo.width / 4f
        val circulos = Path()
        for (i in -1..5) for (j in -1..10) {
            val c = Offset(celo.left + i * s, celo.top + j * s)
            if (c.y > celo.bottom + s) continue
            circulos.addOval(Rect(c.x - s * 0.707f, c.y - s * 0.707f, c.x + s * 0.707f, c.y + s * 0.707f))
        }
        val piedra = a.luz(mezcla(YESO, PIEDRA, 0.25f), lejos)
        drawPath(corrido(circulos, 0.9f * u, 1f * u), a.sombra(0.5f), style = Stroke(s * 0.24f))
        drawPath(circulos, piedra, style = Stroke(s * 0.22f))
        drawPath(corrido(circulos, -0.5f * u, -0.5f * u), a.claro(0.3f), style = Stroke(s * 0.06f))
        // De noche, la luna pone un filo frío en el canto de los barrotes.
        if (a.lumbre) drawPath(corrido(circulos, 0.5f * u, 0.5f * u), LUZ_LUNA.copy(alpha = if (a.oscuro) 0.3f else 0.2f), style = Stroke(s * 0.05f))
    }
    drawPath(hueco, a.luz(mezcla(YESO, PIEDRA, 0.3f), lejos), style = Stroke(w * 0.007f))
    drawPath(hueco, a.sombra(0.3f), style = Stroke(0.8f * u))
    // El resplandor de la ventana en el muro: cálido de día, frío de noche.
    if (a.lumbre) mancha(celo.center, w * 0.34f, LUZ_LUNA.copy(alpha = if (a.oscuro) 0.14f else 0.1f))
    else mancha(celo.center, w * 0.2f, SOL.copy(alpha = 0.35f))
}

/** Dos roleos de ataurique en cada albanega (entre el arco y el alfiz), tallados en el yeso. */
internal fun DrawScope.albanegasAndalus(dentro: Rect, a: AireAndalus, lejos: Float) {
    val u = density
    val tallo = Path()
    for (l in listOf(-1f, 1f)) {
        val x = if (l < 0) dentro.left + dentro.width * 0.12f else dentro.right - dentro.width * 0.12f
        val y = dentro.top + dentro.height * 0.1f
        val r = dentro.width * 0.07f
        tallo.moveTo(x, y + r * 2.2f)
        tallo.cubicTo(x, y + r * 0.6f, x - l * r * 1.2f, y - r * 0.2f, x - l * r * 0.4f, y + r * 0.6f)
        tallo.cubicTo(x - l * r * 0.1f, y + r * 0.9f, x + l * r * 0.4f, y + r * 0.5f, x + l * r * 0.2f, y + r * 0.2f)
        tallo.moveTo(x, y + r * 1.2f)
        tallo.cubicTo(x + l * r * 0.8f, y + r * 0.8f, x + l * r * 1.4f, y + r * 1.6f, x + l * r * 0.9f, y + r * 2.2f)
    }
    drawPath(corrido(tallo, 0.7f * u, 0.8f * u), a.sombra(0.35f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
    drawPath(tallo, a.luz(mezcla(YESO, LUZ_MUEBLE, 0.3f), lejos), style = Stroke(1.3f * u, cap = StrokeCap.Round))
}

/**
 * La luz que entra por la celosía y cae en [r]: una mancha de borde blando, alargada ([estira]: el alto de un círculo de la
 * celosía en la mancha, por cada ancho) y sesgada ([sesgo]: lo que se corre a la derecha por cada unidad que baja), cálida de
 * día y fría de noche, cruzada por la sombra de los barrotes: los círculos secantes, deformados igual que la mancha. [blando]
 * es lo ancho de la penumbra.
 */
internal fun DrawScope.tramaAndalus(r: Rect, a: AireAndalus, fuerza: Float, estira: Float, sesgo: Float, paso: Float = 0.034f, blando: Float = 1f) {
    if (r.width <= 1f || r.height <= 1f || estira <= 0.01f) return
    val u = density
    val s = maxOf(size.width * paso, 3f * u)
    val d = r.height * sesgo
    val mancha = poligono(r.left, r.top, r.right, r.top, r.right + d, r.bottom, r.left + d, r.bottom)
    // La penumbra: tres trazos, cada uno más ancho y más tenue; luego la mancha.
    for ((ancho, al) in listOf(10f to 0.08f, 6f to 0.14f, 3f to 0.24f)) drawPath(mancha, a.celosia(fuerza * al), style = Stroke(ancho * u * blando, join = StrokeJoin.Round))
    drawPath(mancha, a.celosia(fuerza))
    // Los barrotes, dibujados derechos y llevados a la mancha por la misma deformación.
    val alto = r.height / estira
    val filas = (alto / s).toInt().coerceIn(0, 60) + 2
    val cols = (r.width / s).toInt().coerceIn(0, 60) + 3
    val barras = Path()
    for (j in -1..filas) for (i in -2..cols) barras.addOval(Rect(i * s - s * 0.707f, j * s - s * 0.707f, i * s + s * 0.707f, j * s + s * 0.707f))
    val m = Matrix()
    m.values[Matrix.SkewX] = sesgo * estira
    m.values[Matrix.ScaleY] = estira
    m.values[Matrix.TranslateX] = r.left
    m.values[Matrix.TranslateY] = r.top
    val sombra = if (a.lumbre) a.sombra(0.26f + 0.2f * fuerza) else a.sombra(0.1f + 0.22f * fuerza)
    clipPath(mancha) {
        withTransform({ transform(m) }) { drawPath(barras, sombra, style = Stroke(s * 0.24f)) }
    }
}

// ---------------------------------------------------------------- el suelo

/**
 * Las losas de caliza del suelo, cada una de su tono, con sus juntas en perspectiva, sus mellas, una grieta y el polvo junto al
 * zócalo; y la luz de la celosía que cae en ellas, alargada hacia quien mira.
 */
internal fun DrawScope.sueloAndalus(a: AireAndalus, suelo: Float, cerca: (Offset) -> Float) {
    val u = density; val w = size.width; val h = size.height
    if (h - suelo <= 1f) return
    val k = cerca(Offset(w * 0.5f, suelo)) * 0.6f
    val fuga = Offset(w * 0.5f, suelo - w * 0.9f)
    fun xEn(xb: Float, y: Float): Float { val t = (h - fuga.y) / (y - fuga.y); return fuga.x + (xb - fuga.x) / t }
    val filas = listOf(suelo, suelo + (h - suelo) * 0.4f, h)
    for (fi in 0 until 2) {
        val y0 = filas[fi]; val y1 = filas[fi + 1]
        for (i in -4..8) {
            val xa = w * (i * 0.17f + 0.03f + (fi % 2) * 0.085f); val xb = xa + w * 0.17f
            val tono = mezcla(LOSA, if (azar(1945 + fi, i + 10) < 0.5f) CAL else PIEDRA_OSC, 0.08f + 0.22f * azar(1946 + fi, i + 10))
            drawPath(poligono(xEn(xa, y0), y0, xEn(xb, y0), y0, xEn(xb, y1), y1, xEn(xa, y1), y1), a.luz(tono, k))
        }
    }
    pinceladas(Rect(0f, suelo, w, h), a.luz(mezcla(LOSA, SOMBRA_MUEBLE, 0.4f), k), 1941, 22, u, 0.1f)
    pinceladas(Rect(0f, suelo, w, h), a.luz(mezcla(LOSA, LUZ_MUEBLE, 0.5f), k), 1942, 16, u, 0.1f)
    val juntas = Path(); val filos = Path(); val mellas = Path()
    for (fi in 0 until 2) {
        val y0 = filas[fi]; val y1 = filas[fi + 1]
        juntas.moveTo(0f, y1); juntas.lineTo(w, y1)
        filos.moveTo(0f, y0 + 1.4f * u); filos.lineTo(w, y0 + 1.4f * u)
        for (i in -4..8) {
            val xa = w * (i * 0.17f + 0.03f + (fi % 2) * 0.085f)
            juntas.moveTo(xEn(xa, y0), y0); juntas.lineTo(xEn(xa, y1), y1)
            if (azar(1947 + fi, i + 10) < 0.4f) {
                val my = y0 + (y1 - y0) * azar(1948 + fi, i + 10); val mx = xEn(xa, my)
                mellas.addOval(Rect(mx - 2.4f * u, my - 1f * u, mx + 2.4f * u, my + 1f * u))
            }
        }
    }
    drawPath(mellas, a.sombra(0.25f))
    drawPath(filos, a.claro(0.3f), style = Stroke(0.8f * u))
    drawPath(juntas, a.sombra(0.32f), style = Stroke(1f * u))
    // Una grieta que cruza una losa.
    val g = Path().apply {
        var p = Offset(w * 0.47f, suelo + (h - suelo) * 0.5f); moveTo(p.x, p.y)
        for (q in 0 until 5) { p += Offset(w * (0.012f + 0.01f * azar(1949, q)), (h - suelo) * (azar(1950, q) - 0.4f) * 0.18f); lineTo(p.x, p.y) }
    }
    drawPath(g, a.sombra(0.35f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
    // El polvo y la sombra junto al zócalo.
    drawRect(a.sombra(0.45f), Offset(0f, suelo), Size(w, 1.6f * u))
    drawRect(a.sombra(0.16f), Offset(0f, suelo + 1.6f * u), Size(w, 6f * u))
    // La luz de la celosía en el suelo: alargada hacia quien mira y corrida a la derecha.
    tramaAndalus(Rect(w * 0.07f, suelo + (h - suelo) * 0.08f, w * 0.31f, h - (h - suelo) * 0.02f), a, if (a.oscuro) 0.28f else if (a.lumbre) 0.34f else 0.46f, 0.6f, 1.1f, 0.05f)
}

// ---------------------------------------------------------------- el fondo de los casilleros

/**
 * El fondo de un casillero ([r], la repisa [k]): estuco pintado de índigo apagado, con la cal de debajo asomando en
 * pinceladas y desconchones, un filete de almagra pintado arriba y, de noche, la luz del candil que le llega por tramos.
 */
internal fun DrawScope.fondoCasilleroAndalus(r: Rect, k: Int, a: AireAndalus, cerca: (Offset) -> Float) {
    val u = density
    if (r.width <= 1f || r.height <= 1f) return
    val tramos = if (a.lumbre) 8 else 1
    for (t in 0 until tramos) {
        val xa = r.left + r.width * t / tramos; val xb = r.left + r.width * (t + 1) / tramos + 0.6f
        drawRect(a.luz(ESTUCO, cerca(Offset((xa + xb) / 2, r.center.y))), Offset(xa, r.top), Size(xb - xa, r.height))
    }
    val kc = cerca(r.center)
    pinceladas(r, a.luz(mezcla(ESTUCO, CAL, 0.45f), kc), 1781 + k, 22, u, 0.15f)
    pinceladas(r, a.luz(mezcla(ESTUCO, SOMBRA_MUEBLE, 0.5f), kc), 1785 + k, 14, u, 0.13f)
    // Los desconchones: la pintura saltó y se ve la cal.
    val gastado = Path()
    for (q in 0 until 5) {
        val o = Offset(r.left + r.width * azar(1787 + k, q), r.top + r.height * (0.3f + 0.65f * azar(1788 + k, q)))
        val rr = r.height * (0.025f + 0.04f * azar(1789 + k, q))
        val lados = 8
        for (z in 0 until lados) {
            val ang = 6.283f * z / lados + azar(1790 + k, q * 7 + z) * 0.6f
            val rz = rr * (0.75f + 0.45f * azar(1791 + k, q * 7 + z))
            val pz = Offset(o.x + rz * 1.5f * kotlin.math.cos(ang), o.y + rz * kotlin.math.sin(ang))
            if (z == 0) gastado.moveTo(pz.x, pz.y) else gastado.lineTo(pz.x, pz.y)
        }
        gastado.close()
    }
    drawPath(gastado, a.luz(mezcla(CAL, ESTUCO, 0.3f), kc).copy(alpha = 0.55f))
    drawPath(borde(gastado, 0.8f * u, 1f * u), a.sombra(0.3f))
    // El filete de almagra y el de cal que corren por arriba (la sombra de la tabla los tapa a medias).
    drawRect(a.luz(ALMAGRA, kc), Offset(r.left, r.top + 12f * u), Size(r.width, 2.2f * u))
    drawRect(a.luz(CAL, kc).copy(alpha = 0.7f), Offset(r.left, r.top + 15f * u), Size(r.width, 1f * u))
}
