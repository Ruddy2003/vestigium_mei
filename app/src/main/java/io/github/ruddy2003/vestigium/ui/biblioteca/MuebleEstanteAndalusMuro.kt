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

/**
 * La sillería a soga y tizón de la Córdoba califal: hiladas iguales en que alterna un sillar largo (a soga) con dos cortos
 * (a tizón), de calcarenita dorada, cada uno de un tono, con sus juntas, sus poros y la mancha de luz arriba a la izquierda.
 */
internal fun DrawScope.muroAndalus(a: AireAndalus, hasta: Float, cerca: (Offset) -> Float) {
    val u = density; val w = size.width
    // Una hilada mide lo que un sillar de alto; el sillar a soga es casi tres veces más largo que el tizón.
    val hc = maxOf(w * 0.105f, 6f * u)
    val juntas = Path(); val filos = Path(); val bajos = Path(); val mellas = Path()
    var y = 0f; var f = 0
    while (y < hasta && f < 300) {
        val y1 = minOf(y + hc, hasta)
        var x = -hc * (0.3f + 2.2f * azar(1901, f))
        var j = 0
        while (x < w && j < 60) {
            // A soga y tizón: un sillar largo y luego dos (a veces tres) cabezas cuadradas.
            val ciclo = (j + f) % 3
            val soga = ciclo == 0
            val largo = if (soga) hc * (2.5f + 0.5f * azar(1902 + f, j)) else hc * (0.72f + 0.12f * azar(1903 + f, j))
            val r = Rect(x, y, x + largo, y1)
            val tono = mezcla(PIEDRA, if (azar(1904 + f, j) < 0.34f) YESO else PIEDRA_OSC, (if (soga) 0.04f else 0.18f) + 0.3f * azar(1905 + f, j))
            drawRect(a.luz(tono, cerca(r.center) * 0.32f), r.topLeft, r.size)
            juntas.moveTo(x, y); juntas.lineTo(x, y1)
            // Cada sillar: la arista de arriba y la de la izquierda toman la luz; la de abajo se gasta y hace sombra.
            filos.moveTo(x + 1.4f * u, y1 - 2f * u); filos.lineTo(x + 1.4f * u, y + 1.6f * u); filos.lineTo(x + largo - 2f * u, y + 1.6f * u)
            bajos.moveTo(x + 2f * u, y1 - 1.6f * u); bajos.lineTo(x + largo - 1.2f * u, y1 - 1.6f * u); bajos.lineTo(x + largo - 1.2f * u, y + 2f * u)
            // Una mella en alguna arista.
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
    drawPath(mellas, a.sombra(0.22f))
    drawPath(bajos, a.sombra(if (a.oscuro) 0.3f else 0.2f), style = Stroke(2.4f * u))
    drawPath(filos, a.claro(0.36f), style = Stroke(1.3f * u))
    drawPath(juntas, a.luz(mezcla(YESO, PIEDRA_OSC, 0.45f), 0.1f), style = Stroke(1.6f * u))
    drawPath(juntas, a.sombra(if (a.oscuro) 0.4f else 0.3f), style = Stroke(0.7f * u))
    // Los poros de la calcarenita y su desgaste.
    val area = Rect(0f, 0f, w, hasta)
    val veces = (hasta / (w * 0.6f)).coerceIn(1f, 14f)
    pinceladas(area, a.luz(mezcla(PIEDRA, SOMBRA_MUEBLE, 0.4f), 0.1f), 1911, (28 * veces).toInt(), u, 0.08f)
    pinceladas(area, a.luz(mezcla(PIEDRA, LUZ_MUEBLE, 0.5f), 0.1f), 1912, (22 * veces).toInt(), u, 0.09f)
    val poros = Path()
    for (k in 0 until (60 * veces).toInt()) {
        val p = Offset(w * azar(1913, k), hasta * azar(1914, k))
        val r = (0.5f + 1.1f * azar(1915, k)) * u
        poros.addOval(Rect(p.x - r, p.y - r * 0.7f, p.x + r, p.y + r * 0.7f))
    }
    drawPath(poros, a.sombra(0.28f))
    // La mancha de luz del día, arriba a la izquierda, alrededor de la ventana.
    if (!a.oscuro) mancha(Offset(w * 0.18f, w * 0.2f), w * 0.95f, luzMueble(if (a.noche) 0.1f else 0.42f))
    // Y hacia la derecha y abajo el muro se va quedando en penumbra.
    pinceladas(Rect(w * 0.6f, 0f, w, w * MuebleEstanteAndalus.CORONA), a.sombra(1f), 1916, 16, u, 0.06f)
}

/**
 * La ventana de herradura, arriba a la izquierda: el alfiz con sus albanegas de ataurique, las dovelas alternas de almagra y
 * yeso, el derrame del muro (en sombra el lado izquierdo y el intradós; a la luz el derecho y el alféizar, con la trama de
 * la celosía) y, al fondo, la celosía de piedra de círculos secantes: por sus huecos, el cielo; de noche, la luna.
 */
internal fun DrawScope.ventanaAndalus(a: AireAndalus) {
    val u = density; val w = size.width
    val alfiz = Rect(w * 0.052f, w * 0.03f, w * 0.318f, w * 0.408f)
    val arco = Rect(w * 0.083f, w * 0.06f, w * 0.287f, w * 0.398f)
    val intra = Rect(w * 0.106f, w * 0.083f, w * 0.264f, w * 0.398f)
    val celo = Rect(w * 0.131f, w * 0.118f, w * 0.239f, w * 0.355f)
    val lejos = 0.12f
    // El alfiz: un marco de yeso que sobresale del muro, con su sombra a la derecha y abajo.
    drawRect(a.sombra(0.3f), alfiz.topLeft + Offset(2.4f * u, 2.6f * u), alfiz.size)
    drawRect(a.luz(YESO, lejos), alfiz.topLeft, alfiz.size)
    pinceladas(alfiz, a.luz(mezcla(YESO, PIEDRA_OSC, 0.5f), lejos), 1931, 14, u, 0.08f)
    val dentro = alfiz.deflate(w * 0.009f)
    drawRect(a.luz(mezcla(YESO, PIEDRA, 0.35f), lejos), dentro.topLeft, dentro.size)
    drawRect(a.claro(0.5f), alfiz.topLeft, Size(alfiz.width, 1.2f * u))
    drawRect(a.sombra(0.3f), dentro.topLeft, Size(dentro.width, 1.2f * u))
    drawRect(a.sombra(0.22f), dentro.topLeft, Size(1.1f * u, dentro.height))
    // Las albanegas: dos roleos de ataurique a cada lado, tallados en el yeso.
    albanegasAndalus(dentro, arco, a)
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
        // Las jambas, bajo el arranque, de yeso.
        drawRect(a.luz(YESO, lejos), Offset(arco.left, arranque), Size(arco.width, arco.bottom - arranque))
        pinceladas(arco, a.luz(mezcla(ALMAGRA, SOMBRA_MUEBLE, 0.3f), lejos), 1932, 12, u, 0.1f)
    }
    drawPath(borde(anillo, 1.2f * u, 1.4f * u), a.claro(0.35f))
    drawPath(borde(exterior, -1f * u, -1f * u), a.sombra(0.3f))
    // El derrame: el grueso del muro entre el arco y la celosía.
    val hueco = arcoHerradura(celo)
    clipPath(interior) {
        drawRect(a.luz(mezcla(YESO, PIEDRA, 0.4f), lejos), intra.topLeft, intra.size)
        // A la luz, el lado derecho y el alféizar; en sombra, el intradós y el lado izquierdo.
        val luz = poligono(celo.center.x, celo.top, intra.right, intra.top + intra.width * 0.5f, intra.right, intra.bottom, intra.left, intra.bottom, celo.left, celo.bottom, celo.right, celo.bottom, celo.right, celo.top + celo.width * 0.4f)
        drawPath(luz, a.luz(mezcla(YESO, LUZ_MUEBLE, if (a.lumbre) 0.1f else 0.45f), lejos))
        drawPath(poligono(intra.left, intra.top, celo.left, celo.top, celo.left, celo.bottom, intra.left, intra.bottom), a.sombra(if (a.lumbre) 0.3f else 0.22f))
        drawRect(a.sombra(0.16f), intra.topLeft, Size(intra.width, (celo.top - intra.top) * 1.3f))
        // La trama de la celosía, que la luz dibuja en el alféizar y en el lado derecho.
        tramaAndalus(Rect(celo.left - w * 0.004f, celo.bottom + w * 0.004f, intra.right, intra.bottom - w * 0.004f), a, 0.55f, 0.5f, 0.42f, 0.02f)
        tramaAndalus(Rect(celo.right + w * 0.004f, celo.top + w * 0.06f, intra.right - w * 0.003f, celo.bottom), a, 0.45f, 0.36f, 1.4f, 0.016f)
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
        if (!a.lumbre) drawRect(Color(0xFFDDE6EC).copy(alpha = 0.5f), celo.topLeft, Size(celo.width, celo.height * 0.4f))
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
    }
    drawPath(hueco, a.luz(mezcla(YESO, PIEDRA, 0.3f), lejos), style = Stroke(w * 0.007f))
    drawPath(hueco, a.sombra(0.3f), style = Stroke(0.8f * u))
    // El resplandor de la ventana en el muro.
    if (a.lumbre) mancha(celo.center, w * 0.3f, LUZ_LUNA.copy(alpha = if (a.oscuro) 0.16f else 0.1f))
}

/** Dos roleos de ataurique en cada albanega (entre el arco y el alfiz), tallados en el yeso. */
internal fun DrawScope.albanegasAndalus(dentro: Rect, arco: Rect, a: AireAndalus) {
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
    drawPath(tallo, a.luz(mezcla(YESO, LUZ_MUEBLE, 0.3f), 0.12f), style = Stroke(1.3f * u, cap = StrokeCap.Round))
}

/**
 * La trama de luz que deja la celosía: lentejas en hilera, aplastadas en [aplaste] y corridas en [sesgo] (la luz llega de
 * soslayo), con [fuerza] de alfa. Va recortada a [r].
 */
internal fun DrawScope.tramaAndalus(r: Rect, a: AireAndalus, fuerza: Float, aplaste: Float, sesgo: Float, paso: Float = 0.034f) {
    if (r.width <= 1f || r.height <= 1f) return
    val u = density
    val s = maxOf(size.width * paso, 3f * u)
    // La mancha: un paralelogramo (la luz llega de soslayo), con una penumbra alrededor.
    val d = r.height * sesgo * 0.3f
    val mancha = poligono(r.left, r.top, r.right - d, r.top, r.right, r.bottom, r.left + d, r.bottom)
    drawPath(mancha, a.celosia(fuerza * 0.35f), style = Stroke(3f * u))
    drawPath(mancha, a.celosia(fuerza))
    // Los barrotes de piedra de la celosía, que dejan su sombra en la mancha: los círculos secantes, aplastados y corridos.
    val barras = Path()
    val filas = (r.height / (s * aplaste)).toInt().coerceIn(1, 40) + 2
    val cols = (r.width / s).toInt().coerceIn(1, 40) + 3
    for (j in -1 until filas) for (i in -2 until cols) {
        val yy = r.top + j * s * aplaste
        val c = Offset(r.left + i * s + (yy - r.top) / maxOf(r.height, 1f) * d, yy)
        barras.addOval(Rect(c.x - s * 0.707f, c.y - s * 0.707f * aplaste, c.x + s * 0.707f, c.y + s * 0.707f * aplaste))
    }
    clipPath(mancha) {
        drawPath(barras, a.sombra(if (a.lumbre) 0.22f else 0.16f), style = Stroke(s * 0.2f * maxOf(aplaste, 0.45f)))
    }
}

// ---------------------------------------------------------------- el suelo

/** Las losas de piedra del suelo, con sus juntas en perspectiva, y la trama de la celosía que cae en ellas. */
internal fun DrawScope.sueloAndalus(a: AireAndalus, suelo: Float, cerca: (Offset) -> Float) {
    val u = density; val w = size.width; val h = size.height
    if (h - suelo <= 1f) return
    val k = cerca(Offset(w * 0.5f, suelo)) * 0.6f
    drawRect(a.luz(LOSA, k), Offset(0f, suelo), Size(w, h - suelo))
    pinceladas(Rect(0f, suelo, w, h), a.luz(mezcla(LOSA, SOMBRA_MUEBLE, 0.4f), k), 1941, 20, u, 0.1f)
    pinceladas(Rect(0f, suelo, w, h), a.luz(mezcla(LOSA, LUZ_MUEBLE, 0.5f), k), 1942, 14, u, 0.1f)
    val juntas = Path()
    val fuga = Offset(w * 0.5f, suelo - w * 0.9f)
    var y = suelo + (h - suelo) * 0.38f
    while (y < h) { juntas.moveTo(0f, y); juntas.lineTo(w, y); y += maxOf((h - suelo) * 0.62f, 2f * u) }
    for (i in -4..8) {
        val xb = w * (i * 0.17f + 0.03f)
        val t = (h - fuga.y) / (suelo - fuga.y)
        juntas.moveTo(fuga.x + (xb - fuga.x) / t, suelo); juntas.lineTo(xb, h)
    }
    drawPath(juntas, a.sombra(0.3f), style = Stroke(0.9f * u))
    // La trama de la celosía en el suelo, al pie de la ventana.
    tramaAndalus(Rect(w * 0.05f, suelo + (h - suelo) * 0.2f, w * 0.36f, h - (h - suelo) * 0.04f), a, if (a.lumbre) 0.3f else 0.36f, 0.72f, 1.2f, 0.05f)
    // La sombra del mueble en el suelo, junto al zócalo.
    drawRect(a.sombra(0.45f), Offset(0f, suelo), Size(w, 1.6f * u))
    drawRect(a.sombra(0.16f), Offset(0f, suelo + 1.6f * u), Size(w, 5f * u))
}
