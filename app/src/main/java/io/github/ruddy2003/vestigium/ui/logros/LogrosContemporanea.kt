package io.github.ruddy2003.vestigium.ui.logros

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.ideario.arcoMedioPunto
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.ideario.engrane
import io.github.ruddy2003.vestigium.ui.ideario.estrella
import io.github.ruddy2003.vestigium.ui.ideario.manometro
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Edad Contemporánea: los logros de Urbe, Terminal, Vapor, Napoleón, Art déco, Botánico, Navidad, Abismo y URSS
// =====================================================================

/** El gris translúcido de lo que aún no se gana, como la figura del trofeo sin ganar. */
private fun gris(oscuro: Boolean): Color = (if (oscuro) Color(0xFFE6E6E6) else Color(0xFF1E1E1E)).copy(alpha = 0.11f)

/** La silueta tenue de un pedestal sin ganar: todas sus piezas en un solo trazo, así lo que se encima no se oscurece. */
private fun DrawScope.silueta(oscuro: Boolean, forma: Path.() -> Unit) = drawPath(Path().apply(forma), gris(oscuro))

/** La sombra al pie: tres óvalos cada vez más anchos y tenues (sin degradados). */
private fun DrawScope.sombraPie(cx: Float, ancho: Float, y: Float, alto: Float, fuerza: Float) {
    for (k in 1..3) {
        val a = ancho * (0.84f + 0.12f * k); val b = alto * (0.5f + 0.25f * k)
        drawOval(Color.Black.copy(alpha = fuerza / 3f), Offset(cx - a / 2, y - b / 2), Size(a, b))
    }
}

/** Los tonos del metal: los del nivel o, sin ganar, grises apagados (para el hall y la placa). */
private fun tonos(e: EstadoPedestal, oscuro: Boolean): EstadoPedestal {
    if (e.hecho) return e
    val g = if (oscuro) Color(0xFF55585A) else Color(0xFFA2A4A6)
    return EstadoPedestal(false, 1, mezcla(g, Color.Black, 0.3f), g, mezcla(g, Color.White, 0.35f), e.semilla)
}

/**
 * Una chapa del metal del nivel en [r]: el canto oscuro abajo y a la derecha, la cara y el filo de luz arriba; en
 * legendario (nivel 4), el cristal, con sus facetas en diagonal.
 */
private fun DrawScope.chapa(e: EstadoPedestal, r: Rect, radio: Float = 0f) {
    val canto = maxOf(minOf(r.width, r.height) * 0.16f, 0.5f * density)
    val cr = CornerRadius(radio)
    drawRoundRect(e.metalOscuro, r.topLeft, r.size, cr)
    drawRoundRect(e.base, r.topLeft, Size(r.width - canto * 0.6f, r.height - canto), cr)
    drawRect(e.brillo, Offset(r.left + radio * 0.4f, r.top), Size(maxOf(r.width - canto * 0.6f - radio * 0.8f, 0f), canto * 0.7f))
    if (e.nivel == 4) clipRect(r.left, r.top, r.right, r.bottom) {
        val d = r.height
        drawLine(Color.White.copy(alpha = 0.75f), Offset(r.left + r.width * 0.22f, r.bottom), Offset(r.left + r.width * 0.22f + d, r.top), canto * 0.55f)
        drawLine(Color.White.copy(alpha = 0.45f), Offset(r.left + r.width * 0.58f, r.bottom), Offset(r.left + r.width * 0.58f + d * 0.7f, r.top), canto * 0.35f)
        drawLine(e.metalOscuro.copy(alpha = 0.45f), Offset(r.left + r.width * 0.4f, r.bottom), Offset(r.left + r.width * 0.4f + d, r.top), canto * 0.3f)
    }
}

/** Un botón del metal del nivel (un clavo, un remache, una roseta); en legendario, una piedra tallada de cristal. */
private fun DrawScope.boton(e: EstadoPedestal, c: Offset, r: Float) {
    if (e.nivel == 4) {
        drawPath(Path().apply { moveTo(c.x, c.y - r); lineTo(c.x + r, c.y); lineTo(c.x, c.y + r); lineTo(c.x - r, c.y); close() }, e.base)
        drawPath(Path().apply { moveTo(c.x, c.y - r); lineTo(c.x - r, c.y); lineTo(c.x, c.y); close() }, e.brillo)
        drawPath(Path().apply { moveTo(c.x, c.y + r); lineTo(c.x + r, c.y); lineTo(c.x, c.y); close() }, e.metalOscuro)
        drawCircle(Color.White.copy(alpha = 0.85f), r * 0.2f, Offset(c.x - r * 0.3f, c.y - r * 0.28f))
    } else {
        drawCircle(e.metalOscuro, r, c)
        drawCircle(e.base, r * 0.8f, Offset(c.x - r * 0.1f, c.y - r * 0.1f))
        drawCircle(e.brillo, r * 0.32f, Offset(c.x - r * 0.32f, c.y - r * 0.32f))
    }
}

/** Un filete del metal del nivel siguiendo [forma]: el canto oscuro, la cara y la luz al centro, como un junquillo. */
private fun DrawScope.filete(e: EstadoPedestal, forma: Path, ancho: Float) {
    drawPath(forma, e.metalOscuro, style = Stroke(ancho))
    drawPath(forma, e.base, style = Stroke(ancho * 0.62f))
    drawPath(forma, if (e.nivel == 4) Color.White.copy(alpha = 0.85f) else e.brillo, style = Stroke(ancho * 0.22f))
}

/** Las vetas de [vetas], recortadas a su rectángulo (sus ondas no se salen de la pieza). */
private fun DrawScope.vetasEn(r: Rect, color: Color, semilla: Int, densidad: Float = 1f, grosor: Float = 1f, ondulacion: Float = 1f) =
    clipRect(r.left, r.top, r.right, r.bottom) { vetas(r, color, semilla, densidad = densidad, grosor = grosor, ondulacion = ondulacion) }

/** Un rectángulo redondeado como trazo. */
private fun redondo(r: Rect, radio: Float): Path = Path().apply { addRoundRect(RoundRect(r, CornerRadius(radio))) }

/**
 * La estrella de cinco puntas facetada, como en las medallas: cada punta con una mitad de luz y otra de cara, y el
 * contorno oscuro.
 */
private fun DrawScope.estrellaFacetada(e: EstadoPedestal, c: Offset, r: Float) {
    val pts = (0 until 10).map { k ->
        val a = Math.toRadians(-90.0 + k * 36.0); val rr = if (k % 2 == 0) r else r * 0.42f
        Offset(c.x + rr * cos(a).toFloat(), c.y + rr * sin(a).toFloat())
    }
    for (k in 0 until 10) {
        val p0 = pts[k]; val p1 = pts[(k + 1) % 10]
        drawPath(Path().apply { moveTo(c.x, c.y); lineTo(p0.x, p0.y); lineTo(p1.x, p1.y); close() }, if (k % 2 == 0) e.brillo else e.base)
    }
    drawPath(estrella(c, r, 5, 0.42f), e.metalOscuro, style = Stroke(maxOf(r * 0.06f, 0.4f * density)))
}

// ---------------------------------------------------------------- ayudantes del salón del hall

/**
 * La planta del salón del hall: [n] franjas de trofeos de ancho [paso] y sus filas. Las filas se estiman del alto (en el
 * hall, ≈ 215 dp cada una con 12 dp arriba y abajo; tope de 8) para poner hornacinas y faroles a la altura de cada trofeo.
 * Nada fuerte va en el centro de una franja ([cx]): ahí va el trofeo.
 */
private class PlantaContemporanea(val w: Float, val h: Float, val u: Float, columnas: Int) {
    val n = columnas.coerceIn(1, 6)
    val paso = w / n
    val filas = ((h - 24 * u) / (215 * u)).roundToInt().coerceIn(1, 8)
    val alto = maxOf((h - 24 * u) / filas, 1f)
    /** El centro de la franja [i]. */
    fun cx(i: Int) = paso * (i + 0.5f)
    /** La línea entre franjas [i] (0 y [n] son los bordes). */
    fun lx(i: Int) = paso * i
    /** El tope de la fila [k]. */
    fun y(k: Int) = 12 * u + alto * k
}

private fun DrawScope.planta(e: EstadoSalon) = PlantaContemporanea(size.width, size.height, density, e.columnas)

/** Un halo sin degradado: [capas] círculos concéntricos del mismo alfa bajo, que se suman hacia el centro. */
private fun DrawScope.halo(c: Offset, r: Float, color: Color, capas: Int = 5) {
    for (k in capas downTo 1) drawCircle(color, r * k / capas, c)
}

// ---------------------------------------------------------------- ayudantes de las campañas

/**
 * La sombra del marcador en el suelo, en [p]: tres óvalos de [ancho] que se quedan en su sitio cuando la figura brinca, pero
 * se achican y aclaran con [salto] (0 a 1). De noche, un poco más fuerte para que se lea sobre el mapa oscuro.
 */
private fun DrawScope.sombraMarcadorContemporanea(p: Offset, ancho: Float, salto: Float, oscuro: Boolean) {
    val s = salto.coerceIn(0f, 1f)
    val f = 1f - 0.3f * s
    sombraPie(p.x, ancho * f, p.y, ancho * 0.22f * f, (if (oscuro) 0.42f else 0.24f) * (1f - 0.35f * s))
}

/** Lo que sube el marcador al brincar: hasta 3 dp con [salto] en 1. */
private fun DrawScope.subidaContemporanea(salto: Float): Float = salto.coerceIn(0f, 1f) * 3 * density

/**
 * El pie de la escena de la victoria: la meta [m], corrida lo justo para que una escena de [ancho] (centrada en x) que sube
 * [alto] y baja [abajo] desde ese pie no se salga del lienzo. La meta suele estar cerca del borde derecho (o, en algún
 * mapa, del izquierdo o de arriba): ahí la escena se recorre hacia dentro.
 */
private fun DrawScope.pieVictoriaContemporanea(m: Offset, ancho: Float, alto: Float, abajo: Float = 0f): Offset {
    val margen = 2 * density
    val x = if (size.width > ancho + 2 * margen) m.x.coerceIn(ancho / 2 + margen, size.width - ancho / 2 - margen) else size.width / 2
    val y = if (size.height > alto + abajo + 2 * margen) m.y.coerceIn(alto + margen, size.height - abajo - margen) else size.height - abajo - margen
    return Offset(x, y)
}

/** Un trazo cerrado o abierto hecho con los puntos [pts] (en dp, relativos a [o]), para las figuras chicas. */
private fun trazoContemporanea(o: Offset, u: Float, cerrar: Boolean, vararg pts: Float): Path = Path().apply {
    moveTo(o.x + pts[0] * u, o.y + pts[1] * u)
    var k = 2
    while (k + 1 < pts.size) { lineTo(o.x + pts[k] * u, o.y + pts[k + 1] * u); k += 2 }
    if (cerrar) close()
}

/** Un estallido de fuegos artificiales en [c]: [n] rayos de [r] con su chispa en la punta y, de noche, su resplandor. */
private fun DrawScope.fuegoContemporanea(c: Offset, r: Float, n: Int, color: Color, oscuro: Boolean) {
    val u = density
    if (oscuro) halo(c, r * 1.3f, color.copy(alpha = 0.06f), 4)
    for (k in 0 until n) {
        val a = (2 * PI * k / n).toFloat() + 0.2f
        val p0 = Offset(c.x + r * 0.32f * cos(a), c.y + r * 0.32f * sin(a)); val p1 = Offset(c.x + r * cos(a), c.y + r * sin(a))
        drawLine(color.copy(alpha = if (oscuro) 0.95f else 0.8f), p0, p1, 0.8f * u, StrokeCap.Round)
        drawCircle(if (oscuro) mezcla(color, Color.White, 0.6f) else color, 0.75f * u, p1)
    }
    drawCircle(mezcla(color, Color.White, 0.5f), 0.9f * u, c)
}

// ---------------------------------------------------------------- ayudantes de los objetos de Tu año

/**
 * Un trazo de los objetos de Tu año, con sus puntos en fracciones del radio [r] alrededor del centro [c]: así el mismo
 * dibujo sirve de 4 a 20 dp.
 */
private class TrazoAnioContemporanea(val c: Offset, val r: Float) {
    val camino = Path()
    fun m(a: Float, b: Float) = camino.moveTo(c.x + a * r, c.y + b * r)
    fun l(a: Float, b: Float) = camino.lineTo(c.x + a * r, c.y + b * r)
    fun q(a: Float, b: Float, x: Float, y: Float) = camino.quadraticTo(c.x + a * r, c.y + b * r, c.x + x * r, c.y + y * r)
    fun z() = camino.close()
}

/** Arma un trazo de Tu año (ver [TrazoAnioContemporanea]). */
private fun trazoAnioContemporanea(c: Offset, r: Float, cuerpo: TrazoAnioContemporanea.() -> Unit): Path =
    TrazoAnioContemporanea(c, r).apply(cuerpo).camino

/** Una luna creciente de verdad (el disco de radio [r] en [c] menos el de radio [rCorte], corrido [corte]): sin pintar el fondo. */
private fun crecienteContemporanea(c: Offset, r: Float, corte: Offset, rCorte: Float): Path = Path.combine(
    androidx.compose.ui.graphics.PathOperation.Difference,
    Path().apply { addOval(Rect(c, r)) },
    Path().apply { addOval(Rect(c + corte, rCorte)) },
)

// =====================================================================

/**
 * Urbe: el trofeo se apoya en un trozo de acera romana, dos hileras de sampietrini de basalto sobre su cama de arena (con
 * una mata de pasto entre los cubos) y el bordillo de travertino con sus poros, sus juntas y la placa del metal del nivel
 * empotrada. En el hall, el rocchio: el tambor acanalado de una columna antigua reusado como guardacantón, con su losa y
 * el fleje del metal. La placa es la targa de mármol de las calles de Roma, con su marco tallado.
 */
object LogrosUrbe : DisenoLogros {
    private val TRAVERTINO = Color(0xFFE2D5BC)
    private val TRAVERTINO_LUZ = Color(0xFFF0E6D2)
    private val TRAVERTINO_SOMBRA = Color(0xFFC2B190)
    private val TRAVERTINO_PORO = Color(0xFFA8956F)
    private val BASALTO = Color(0xFF4A4642)
    private val BASALTO_LUZ = Color(0xFF706A62)
    private val ARENA = Color(0xFF9C8C70)
    private val PASTO = Color(0xFF6E8A3A)
    private val MARMOL = Color(0xFFF4F0E6)
    private val TINTA = Color(0xFF1E2224)
    private val ROJO = Color(0xFF8E3A2E)

    // salón: colores
    private val OCRE = Color(0xFFC98A3A)
    private val OCRE_CLARO = Color(0xFFE2AE62)
    private val OCRE_SOMBRA = Color(0xFF9A6428)
    private val LADRILLO = Color(0xFFA2513A)
    private val TEJA = Color(0xFFA8432A)
    private val PERSIANA = Color(0xFF3D5E45)
    private val PERSIANA_LUZ = Color(0xFF5E8466)
    private val GERANIO = Color(0xFFC8364A)
    private val VESPA = Color(0xFF8FB8A8)
    private val HIERRO = Color(0xFF2E3532)
    private val FAROL = Color(0xFFFFC46A)

    /** El color en modo oscuro: más hundido, como en su biblioteca. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100E0C), 0.34f) else c

    // ---------------------------------------------------------------- pedestales y placa

    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val fino = h >= 90 * u
        val hl = maxOf(0.6f * u, h * 0.004f)
        fun c(x: Color) = if (oscuro) mezcla(t(x, true), Color.Black, 0.18f) else x
        val bord = Rect(w * 0.08f, h * 0.872f, w * 0.92f, h * 0.952f)
        val adoq = Rect(w * 0.12f, h * 0.776f, w * 0.88f, h * 0.872f)
        if (!e.hecho) { silueta(oscuro) { addRect(bord); addRect(adoq) }; return }
        sombraPie(w / 2, bord.width, bord.bottom + h * 0.006f, h * 0.024f, if (oscuro) 0.5f else 0.24f)
        // La cama de arena y, encima, dos hileras de sampietrini, cada cubo de su tono, la segunda corrida medio paso.
        drawRect(c(ARENA), adoq.topLeft, adoq.size)
        val alto = adoq.height / 2
        var k = 0
        clipRect(adoq.left, adoq.top, adoq.right, adoq.bottom) {
            for (f in 0 until 2) {
                val y0 = adoq.top + f * alto
                var x = adoq.left + alto * 0.06f - (if (f == 1) alto * 0.5f else 0f)
                while (x < adoq.right && k < 60) {
                    val r = Rect(x, y0 + alto * 0.07f, x + alto * 0.88f, y0 + alto * 0.93f)
                    val tono = mezcla(BASALTO, BASALTO_LUZ, azar(e.semilla, k) * 0.8f)
                    drawRoundRect(c(tono), r.topLeft, r.size, CornerRadius(alto * 0.2f))
                    if (fino) {
                        // El lomo pulido por los pasos y una picadura.
                        drawRoundRect(c(mezcla(tono, Color.White, 0.2f)), Offset(r.left + alto * 0.1f, r.top + alto * 0.06f), Size(r.width - alto * 0.2f, r.height * 0.2f), CornerRadius(alto * 0.1f))
                        drawCircle(c(mezcla(tono, Color.Black, 0.35f)), alto * 0.05f, Offset(r.left + r.width * (0.25f + 0.5f * azar(e.semilla, k + 30)), r.top + r.height * 0.62f))
                    }
                    x += maxOf(alto, 1f); k++
                }
            }
            // Una mata de pasto que brota en la junta entre las dos hileras.
            if (fino) {
                val gx = adoq.left + adoq.width * (0.2f + 0.6f * azar(e.semilla, 90))
                for (j in -2..2) drawLine(c(PASTO), Offset(gx, adoq.top + alto * 1.05f), Offset(gx + j * alto * 0.13f, adoq.top + alto * (0.5f + 0.1f * abs(j))), hl, StrokeCap.Round)
            }
        }
        // El bordillo de travertino: el canto gastado arriba, la sombra al pie, las juntas entre las piedras y los poros.
        drawRect(c(TRAVERTINO), bord.topLeft, bord.size)
        drawRect(c(TRAVERTINO_LUZ), bord.topLeft, Size(bord.width, bord.height * 0.2f))
        drawLine(c(TRAVERTINO_SOMBRA), Offset(bord.left, bord.top + bord.height * 0.2f), Offset(bord.right, bord.top + bord.height * 0.2f), hl * 0.7f)
        drawRect(c(TRAVERTINO_SOMBRA), Offset(bord.left, bord.bottom - bord.height * 0.16f), Size(bord.width, bord.height * 0.16f))
        listOf(0.3f, 0.72f).forEach { f -> drawLine(c(TRAVERTINO_PORO), Offset(w * f, bord.top), Offset(w * f, bord.bottom), hl) }
        if (fino) for (i in 0 until 28) {
            val px = bord.left + bord.width * azar(e.semilla, 100 + i); val py = bord.top + bord.height * (0.3f + 0.5f * azar(e.semilla, 200 + i))
            drawOval(c(TRAVERTINO_PORO).copy(alpha = 0.75f), Offset(px, py), Size(w * (0.01f + 0.025f * azar(e.semilla, 300 + i)), maxOf(h * 0.0035f, 0.6f * u)))
        }
        // La placa del metal del nivel empotrada al frente, con sus dos tornillos.
        val pl = Rect(w * 0.39f, bord.top + bord.height * 0.3f, w * 0.61f, bord.bottom - bord.height * 0.24f)
        chapa(e, pl, pl.height * 0.12f)
        if (fino) listOf(pl.left + pl.width * 0.1f, pl.right - pl.width * 0.12f).forEach { x -> drawCircle(e.metalOscuro, pl.height * 0.12f, Offset(x, pl.center.y)) }
    }

    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val m = tonos(e, oscuro)
        fun c(x: Color) = if (oscuro) mezcla(t(x, true), Color.Black, 0.18f) else x
        sombraPie(w / 2, w * 0.72f, h * 0.95f, h * 0.16f, if (oscuro) 0.45f else 0.2f)
        // El rocchio: el tambor de una columna antigua, con su luz a la izquierda y su sombra a la derecha.
        val tambor = Rect(w * 0.2f, h * 0.15f, w * 0.8f, h * 0.86f)
        drawRect(c(TRAVERTINO), tambor.topLeft, tambor.size)
        drawRect(c(TRAVERTINO_LUZ), tambor.topLeft, Size(tambor.width * 0.22f, tambor.height))
        drawRect(c(TRAVERTINO_SOMBRA), Offset(tambor.right - tambor.width * 0.22f, tambor.top), Size(tambor.width * 0.22f, tambor.height))
        val n = 8
        for (k in 1 until n) {
            val x = tambor.left + tambor.width * k / n
            drawLine(c(TRAVERTINO_PORO), Offset(x, tambor.top), Offset(x, tambor.bottom), 0.8f * u)
            drawLine(c(TRAVERTINO_LUZ), Offset(x + 0.9f * u, tambor.top), Offset(x + 0.9f * u, tambor.bottom), 0.5f * u)
        }
        for (i in 0 until 12) drawOval(c(TRAVERTINO_PORO).copy(alpha = 0.6f), Offset(tambor.left + tambor.width * azar(e.semilla, i), tambor.top + tambor.height * azar(e.semilla, i + 40)), Size(2.2f * u, 0.6f * u))
        // El fleje del metal del nivel que lo ciñe, con sus remaches.
        val fleje = Rect(tambor.left - 0.6f * u, h * 0.42f, tambor.right + 0.6f * u, h * 0.57f)
        chapa(m, fleje)
        for (k in 1..3) boton(m, Offset(tambor.left + tambor.width * k / 4, fleje.center.y), fleje.height * 0.32f)
        // La losa encima y la basa al pie.
        val losa = Rect(w * 0.12f, 0f, w * 0.88f, h * 0.16f)
        drawRect(c(TRAVERTINO_SOMBRA), losa.topLeft, losa.size)
        drawRect(c(TRAVERTINO_LUZ), losa.topLeft, Size(losa.width, losa.height * 0.45f))
        val basa = Rect(w * 0.15f, h * 0.85f, w * 0.85f, h)
        drawRect(c(TRAVERTINO_SOMBRA), basa.topLeft, basa.size)
        drawLine(c(TRAVERTINO_LUZ), basa.topLeft, Offset(basa.right, basa.top), 0.7f * u)
    }

    override val margenPlaca: Dp = 8.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFEDE6D6) else TINTA
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFE0B27A) else ROJO

    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val u = density
        val m = tonos(e, oscuro)
        val r0 = Rect(Offset.Zero, size)
        fun c(x: Color) = if (oscuro) mezcla(t(x, true), Color.Black, 0.3f) else x
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.18f), Offset(0.6f * u, 1.6f * u), size, CornerRadius(2 * u))
        // La targa: el marco de travertino tallado en dos planos y, por fuera, el filete del metal del nivel.
        drawRoundRect(c(TRAVERTINO_SOMBRA), Offset.Zero, size, CornerRadius(2 * u))
        filete(m, redondo(r0.deflate(1.1f * u), 1.5f * u), 2.2f * u)
        val marco = r0.deflate(2.4f * u)
        drawRect(c(TRAVERTINO), marco.topLeft, marco.size)
        drawRect(c(TRAVERTINO_LUZ), marco.topLeft, Size(marco.width, 0.8f * u))
        val cara = r0.deflate(4.8f * u)
        drawRect(c(TRAVERTINO_PORO), Offset(cara.left - 0.9f * u, cara.top - 0.9f * u), Size(cara.width + 1.8f * u, cara.height + 1.8f * u))
        drawRect(c(TRAVERTINO_LUZ), Offset(cara.left - 0.9f * u, cara.bottom), Size(cara.width + 1.8f * u, 0.9f * u))
        // La cara lisa de mármol, donde va el nombre.
        drawRect(if (oscuro) Color(0xFF262A28) else MARMOL, cara.topLeft, cara.size)
        // Los dos clavos del metal que la sujetan al muro.
        listOf(marco.left + 1.2f * u, marco.right - 1.2f * u).forEach { x -> boton(m, Offset(x, size.height / 2), 0.95f * u) }
    }

    // ---------------------------------------------------------------- el salón del hall

    /**
     * La facciata de una calle de Roma al caer la tarde (de noche, con sus faroles encendidos): el muro de revoque ocre
     * con sus parches y algún desconchón que deja ver el ladrillo; entre cada franja de trofeos, una ventana con su marco
     * de travertino, el frontoncito, las persianas verdes abiertas de lamas y el alféizar con geranios (de noche, unas
     * encendidas y con su cortina); bajo cada una, el farol de brazo. En una de las líneas, la edícola de la Virgen con su
     * tejadito y su lamparilla. Arriba, el cielo, las tejas y el cornisón con sus ménsulas; las impostas de travertino
     * marcan los pisos. A la izquierda, la bajada de agua. Abajo, el zócalo de travertino, el bordillo y la acera de
     * sampietrini con el nasone, la Vespa que asoma a la izquierda y la maceta de laurel a la derecha.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) = clipRect { salon(e, oscuro) }

    /** La escena del salón, recortada al lienzo por [escenaSalon]. */
    private fun DrawScope.salon(e: EstadoSalon, oscuro: Boolean) {
        val p = planta(e); val u = p.u; val w = p.w; val h = p.h
        fun c(x: Color, f: Float) = mezcla(e.fondo, t(x, oscuro), f)
        val techo = 34 * u; val zocalo = h - 36 * u; val acera = h - 18 * u

        // El muro: el revoque ocre a todo el lienzo, con parches de otro tono (más tenue al centro, donde van los trofeos).
        drawRect(c(OCRE, if (oscuro) 0.28f else 0.22f), Offset(0f, techo), Size(w, zocalo - techo))
        for (k in 0 until 22) {
            val px = w * azar(e.semilla + 3, k); val py = techo + (zocalo - techo) * azar(e.semilla + 3, k + 50)
            val r = (12 + 30 * azar(e.semilla + 3, k + 100)) * u
            val tono = if (k % 3 == 0) OCRE_SOMBRA else OCRE_CLARO
            drawOval(t(tono, oscuro).copy(alpha = if (oscuro) 0.07f else 0.08f), Offset(px - r, py - r * 0.55f), Size(2 * r, 1.1f * r))
        }
        // Los desconchones: el revoque caído deja ver las hiladas de ladrillo (en los bordes, lejos de los trofeos).
        listOf(Rect(2 * u, zocalo - 64 * u, 30 * u, zocalo - 34 * u), Rect(w - 34 * u, techo + p.alto * 0.62f, w - 8 * u, techo + p.alto * 0.62f + 22 * u)).forEachIndexed { i, r ->
            val forma = contornoIrregular(r, e.semilla + 17 + i, 14, 0.22f)
            drawPath(forma, c(LADRILLO, if (oscuro) 0.4f else 0.32f))
            clipPath(forma) {
                var y = r.top; var f = 0
                while (y < r.bottom && f < 12) {
                    drawLine(c(OCRE_CLARO, 0.4f), Offset(r.left, y), Offset(r.right, y), 0.8f * u)
                    var x = r.left + (if (f % 2 == 0) 0f else 4.5f * u); var j = 0
                    while (x < r.right && j < 12) { drawLine(c(OCRE_CLARO, 0.4f), Offset(x, y), Offset(x, y + 3.6f * u), 0.7f * u); x += maxOf(9 * u, 1f); j++ }
                    y += maxOf(3.6f * u, 1f); f++
                }
            }
            drawPath(forma, c(OCRE_CLARO, 0.5f), style = Stroke(1.2f * u))
        }
        // Las impostas de travertino que marcan cada piso, entre fila y fila.
        for (k in 1 until p.filas) {
            val y = p.y(k) - 7 * u
            drawRect(c(TRAVERTINO, if (oscuro) 0.3f else 0.4f), Offset(0f, y), Size(w, 3.4f * u))
            drawLine(c(TRAVERTINO_SOMBRA, 0.35f), Offset(0f, y + 3.4f * u), Offset(w, y + 3.4f * u), 0.8f * u)
        }
        // La bajada de agua de lata a la izquierda, con sus abrazaderas.
        val bx = 25 * u
        drawRect(c(Color(0xFF8A8A80), if (oscuro) 0.45f else 0.4f), Offset(bx - 2 * u, techo), Size(4 * u, zocalo - techo))
        drawLine(c(Color.White, 0.25f), Offset(bx - 1 * u, techo), Offset(bx - 1 * u, zocalo), 0.7f * u)
        run { var y = techo + 30 * u; var k = 0; while (y < zocalo && k < 24) { drawRect(c(HIERRO, 0.4f), Offset(bx - 3 * u, y), Size(6 * u, 1.6f * u)); y += maxOf(70 * u, 1f); k++ } }

        // Entre cada franja, una ventana por piso (o, en la línea del medio del segundo piso, la edícola), y su farol.
        val medio = p.n / 2; val pisoEdicola = if (p.filas > 1) 1 else 0
        for (i in 0..p.n) for (k in 0 until p.filas) {
            val x = p.lx(i)
            val y0 = maxOf(p.y(k) + 16 * u, techo + 12 * u); val y1 = y0 + minOf(p.alto * 0.48f, 100 * u)
            if (y1 > zocalo - 20 * u) continue
            if (i == medio && k == pisoEdicola) { edicola(Offset(x, y0 + (y1 - y0) * 0.45f), u, e, oscuro); continue }
            val vw = 14 * u; val marco = 3 * u
            // Las persianas abiertas contra el muro, con sus lamas.
            listOf(-1f, 1f).forEach { s ->
                val px = if (s < 0) x - vw / 2 - marco - 7.5f * u else x + vw / 2 + marco + 0.5f * u
                drawRect(c(PERSIANA, if (oscuro) 0.5f else 0.55f), Offset(px, y0), Size(7 * u, y1 - y0))
                var y = y0 + 2.5f * u; var j = 0
                while (y < y1 - 1 * u && j < 40) { drawLine(c(PERSIANA_LUZ, 0.6f), Offset(px + 0.8f * u, y), Offset(px + 6.2f * u, y), 0.8f * u); y += maxOf(3.2f * u, 1f); j++ }
                drawRect(c(HIERRO, 0.3f), Offset(px, y0), Size(7 * u, y1 - y0), style = Stroke(0.6f * u))
            }
            // El marco de travertino, el frontoncito y el vidrio (de noche, unas con luz y cortina).
            drawRect(c(TRAVERTINO, if (oscuro) 0.5f else 0.75f), Offset(x - vw / 2 - marco, y0 - marco), Size(vw + 2 * marco, y1 - y0 + 2 * marco))
            drawPath(Path().apply { moveTo(x - vw / 2 - marco - 2 * u, y0 - marco); lineTo(x, y0 - marco - 6 * u); lineTo(x + vw / 2 + marco + 2 * u, y0 - marco); close() }, c(TRAVERTINO_SOMBRA, if (oscuro) 0.5f else 0.7f))
            val encendida = oscuro && azar(e.semilla + 31, i * 9 + k) > 0.4f
            drawRect(if (encendida) c(FAROL, 0.5f) else c(Color(0xFF2A2C2E), if (oscuro) 0.55f else 0.4f), Offset(x - vw / 2, y0), Size(vw, y1 - y0))
            if (encendida) {
                drawPath(Path().apply { moveTo(x - vw / 2, y0); quadraticTo(x - vw * 0.1f, (y0 + y1) / 2, x - vw / 2 + 2 * u, y1); lineTo(x - vw / 2, y1); close() }, c(Color(0xFFF2E6CC), 0.45f))
                drawPath(Path().apply { moveTo(x + vw / 2, y0); quadraticTo(x + vw * 0.1f, (y0 + y1) / 2, x + vw / 2 - 2 * u, y1); lineTo(x + vw / 2, y1); close() }, c(Color(0xFFF2E6CC), 0.45f))
            } else drawLine(Color.White.copy(alpha = 0.12f), Offset(x - vw / 2 + 2 * u, y0 + 3 * u), Offset(x + vw / 2 - 4 * u, y0 + 12 * u), 1.4f * u)
            drawLine(c(TRAVERTINO, 0.5f), Offset(x, y0), Offset(x, y1), 1 * u)
            drawLine(c(TRAVERTINO, 0.5f), Offset(x - vw / 2, y0 + (y1 - y0) * 0.36f), Offset(x + vw / 2, y0 + (y1 - y0) * 0.36f), 1 * u)
            // El alféizar con su maceta de geranios.
            drawRect(c(TRAVERTINO_LUZ, if (oscuro) 0.5f else 0.8f), Offset(x - vw / 2 - 6 * u, y1 + marco), Size(vw + 12 * u, 2.6f * u))
            drawRect(c(TEJA, 0.6f), Offset(x - vw / 2 - 2 * u, y1 + marco - 4 * u), Size(vw + 4 * u, 4 * u))
            for (j in 0 until 4) {
                val gx = x - vw / 2 + j * vw / 3
                drawCircle(c(PASTO, 0.5f), 2.4f * u, Offset(gx, y1 + marco - 5 * u))
                drawCircle(c(GERANIO, if (oscuro) 0.5f else 0.7f), 1.7f * u, Offset(gx + 0.8f * u, y1 + marco - 7 * u))
            }
            // El farol de brazo bajo la ventana (no en los bordes): de noche, encendido, con su halo en el muro.
            if (i in 1 until p.n) {
                val fy = y1 + 30 * u
                if (fy < zocalo - 30 * u) {
                    if (oscuro) halo(Offset(x + 9 * u, fy + 6 * u), 40 * u, FAROL.copy(alpha = 0.035f), 6)
                    drawPath(Path().apply { moveTo(x, fy - 6 * u); lineTo(x + 9 * u, fy - 6 * u); quadraticTo(x + 4 * u, fy - 1 * u, x, fy + 2 * u) }, c(HIERRO, 0.6f), style = Stroke(1.2f * u))
                    drawCircle(c(HIERRO, 0.6f), 1.6f * u, Offset(x + 2 * u, fy - 2 * u), style = Stroke(0.8f * u))
                    val farol = Path().apply { moveTo(x + 5.5f * u, fy - 1 * u); lineTo(x + 12.5f * u, fy - 1 * u); lineTo(x + 11.5f * u, fy + 9 * u); lineTo(x + 6.5f * u, fy + 9 * u); close() }
                    drawPath(farol, if (oscuro) c(FAROL, 0.9f) else c(Color(0xFFDCD6C4), 0.5f))
                    drawPath(farol, c(HIERRO, 0.7f), style = Stroke(0.9f * u))
                    drawPath(Path().apply { moveTo(x + 4.5f * u, fy - 1 * u); lineTo(x + 9 * u, fy - 5 * u); lineTo(x + 13.5f * u, fy - 1 * u); close() }, c(HIERRO, 0.7f))
                }
            }
        }

        // Arriba: el cielo (de tarde o de noche), las tejas y el cornisón de travertino con sus ménsulas.
        val cielo = if (oscuro) listOf(Color(0xFF1B2440), Color(0xFF2B2A3A)) else listOf(Color(0xFFE8955A), Color(0xFFF2C68C))
        drawRect(Brush.verticalGradient(cielo.map { mezcla(e.fondo, it, 0.55f) }, 0f, 12 * u), Offset.Zero, Size(w, 12 * u))
        if (oscuro) for (k in 0 until 7) drawCircle(Color.White.copy(alpha = 0.5f), 0.6f * u, Offset(w * azar(e.semilla + 5, k), 1.5f * u + 6 * u * azar(e.semilla + 5, k + 9)))
        run {
            var x = -3 * u; var k = 0
            while (x < w && k < 120) {
                drawArc(c(TEJA, if (oscuro) 0.5f else 0.6f), 180f, 180f, true, Offset(x, 9 * u), Size(7 * u, 6 * u))
                drawArc(c(mezcla(TEJA, Color.Black, 0.3f), 0.6f), 180f, 180f, false, Offset(x, 9 * u), Size(7 * u, 6 * u), style = Stroke(0.7f * u))
                x += maxOf(6.4f * u, 1f); k++
            }
        }
        drawRect(c(TRAVERTINO, if (oscuro) 0.55f else 0.8f), Offset(0f, 15 * u), Size(w, techo - 15 * u))
        drawRect(c(TRAVERTINO_LUZ, if (oscuro) 0.5f else 0.85f), Offset(0f, 15 * u), Size(w, 3 * u))
        drawLine(c(TRAVERTINO_SOMBRA, 0.6f), Offset(0f, 23 * u), Offset(w, 23 * u), 0.8f * u)
        run {
            var x = 4 * u; var k = 0
            while (x < w && k < 80) {
                drawRect(c(TRAVERTINO_SOMBRA, if (oscuro) 0.55f else 0.7f), Offset(x, 24 * u), Size(4 * u, techo - 25 * u))
                drawRect(c(TRAVERTINO_LUZ, 0.7f), Offset(x, 24 * u), Size(1 * u, techo - 25 * u))
                x += maxOf(13 * u, 1f); k++
            }
        }
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.18f else 0.07f), Offset(0f, techo), Size(w, 4 * u))

        // Abajo: el zócalo de travertino con sus juntas y poros, el bordillo y la acera de sampietrini.
        drawRect(c(TRAVERTINO, if (oscuro) 0.45f else 0.7f), Offset(0f, zocalo), Size(w, acera - zocalo))
        drawLine(c(TRAVERTINO_LUZ, 0.8f), Offset(0f, zocalo), Offset(w, zocalo), 1.2f * u)
        run {
            var x = 30 * u * azar(e.semilla, 7); var k = 0
            while (x < w && k < 40) { drawLine(c(TRAVERTINO_PORO, 0.6f), Offset(x, zocalo), Offset(x, acera), 0.8f * u); x += maxOf(48 * u, 1f); k++ }
            for (j in 0 until 30) drawOval(c(TRAVERTINO_PORO, 0.5f), Offset(w * azar(e.semilla + 8, j), zocalo + (acera - zocalo) * (0.2f + 0.6f * azar(e.semilla + 8, j + 40))), Size((1.5f + 3 * azar(e.semilla + 8, j + 80)) * u, 0.7f * u))
        }
        drawRect(c(BASALTO, if (oscuro) 0.45f else 0.35f), Offset(0f, acera), Size(w, h - acera))
        drawRect(c(TRAVERTINO_SOMBRA, 0.6f), Offset(0f, acera), Size(w, 2 * u))
        for (f in 0 until 2) {
            var x = if (f == 0) 0f else -4 * u; var k = 0
            while (x < w && k < 80) {
                drawRoundRect(c(mezcla(BASALTO, BASALTO_LUZ, azar(e.semilla + 9, k + f * 100)), if (oscuro) 0.5f else 0.42f), Offset(x + 0.6f * u, acera + 2.6f * u + f * 7.6f * u), Size(6.8f * u, 6.6f * u), CornerRadius(1.4f * u))
                x += maxOf(8 * u, 1f); k++
            }
        }

        // El nasone de hierro en una de las líneas, con su chorro.
        val nx = p.lx(medio).coerceIn(14 * u, w - 14 * u)
        drawRect(c(HIERRO, 0.6f), Offset(nx - 4 * u, zocalo - 4 * u), Size(8 * u, acera - zocalo + 6 * u))
        drawArc(c(HIERRO, 0.6f), 180f, 180f, true, Offset(nx - 4 * u, zocalo - 8 * u), Size(8 * u, 8 * u))
        drawLine(c(Color(0xFF5A6460), 0.6f), Offset(nx - 2 * u, zocalo - 2 * u), Offset(nx - 2 * u, acera), 0.8f * u)
        drawLine(c(HIERRO, 0.6f), Offset(nx + 4 * u, zocalo + 4 * u), Offset(nx + 9 * u, zocalo + 5 * u), 1.8f * u, StrokeCap.Round)
        drawPath(Path().apply { moveTo(nx + 9 * u, zocalo + 5.5f * u); quadraticTo(nx + 11 * u, zocalo + 9 * u, nx + 11 * u, h - 6 * u) }, c(Color(0xFF6FB3D9), 0.7f), style = Stroke(1 * u))
        drawOval(c(Color(0xFF6FB3D9), 0.35f), Offset(nx + 6 * u, h - 7 * u), Size(10 * u, 3 * u))

        // La Vespa que asoma por la izquierda: el escudo, el cofre redondo, el asiento, el manubrio y las ruedas chicas.
        run {
            val y = h - 5 * u
            val cuerpo = Path().apply {
                moveTo(-6 * u, y - 6 * u); cubicTo(-6 * u, y - 18 * u, 12 * u, y - 19 * u, 16 * u, y - 9 * u)
                lineTo(24 * u, y - 9 * u); lineTo(27 * u, y - 24 * u); lineTo(31 * u, y - 24 * u); lineTo(32 * u, y - 6 * u); close()
            }
            drawPath(cuerpo, c(VESPA, if (oscuro) 0.45f else 0.6f))
            drawPath(cuerpo, c(HIERRO, 0.35f), style = Stroke(0.8f * u))
            drawRoundRect(c(Color(0xFF3A2E28), 0.55f), Offset(-2 * u, y - 21 * u), Size(16 * u, 3.4f * u), CornerRadius(1.6f * u))
            drawLine(c(HIERRO, 0.6f), Offset(29 * u, y - 24 * u), Offset(28 * u, y - 29 * u), 1.2f * u)
            drawLine(c(HIERRO, 0.6f), Offset(24 * u, y - 29 * u), Offset(34 * u, y - 29 * u), 1.4f * u, StrokeCap.Round)
            drawCircle(if (oscuro) c(FAROL, 0.7f) else c(Color.White, 0.6f), 1.8f * u, Offset(31 * u, y - 21 * u))
            listOf(4 * u, 28 * u).forEach { x -> drawCircle(c(HIERRO, 0.7f), 5 * u, Offset(x, y)); drawCircle(c(Color(0xFFB8B8B0), 0.5f), 2 * u, Offset(x, y)) }
        }
        // La maceta de barro con su laurel, a la derecha.
        run {
            val mx = w - 16 * u; val y = h - 4 * u
            for (k in 0 until 9) {
                val a = PI * (0.1 + 0.8 * k / 8)
                drawCircle(c(if (k % 2 == 0) PASTO else mezcla(PASTO, Color.Black, 0.3f), if (oscuro) 0.45f else 0.55f), 5.5f * u, Offset(mx - (9 * u * cos(a)).toFloat(), y - 26 * u - (8 * u * sin(a)).toFloat()))
            }
            drawLine(c(Color(0xFF5E4528), 0.6f), Offset(mx, y - 14 * u), Offset(mx, y - 24 * u), 1.4f * u)
            drawPath(Path().apply { moveTo(mx - 8 * u, y - 14 * u); lineTo(mx + 8 * u, y - 14 * u); lineTo(mx + 6 * u, y); lineTo(mx - 6 * u, y); close() }, c(TEJA, if (oscuro) 0.55f else 0.7f))
            drawRect(c(mezcla(TEJA, Color.White, 0.25f), 0.7f), Offset(mx - 9 * u, y - 16 * u), Size(18 * u, 2.6f * u))
        }
    }

    /** La edícola de la Virgen: el marco de estuco, la imagen con su manto azul y su aureola, el tejadito y la lamparilla roja. */
    private fun DrawScope.edicola(c0: Offset, u: Float, e: EstadoSalon, oscuro: Boolean) {
        fun c(x: Color, f: Float) = mezcla(e.fondo, t(x, oscuro), f)
        val r = Rect(c0.x - 11 * u, c0.y - 15 * u, c0.x + 11 * u, c0.y + 15 * u)
        drawOval(c(TRAVERTINO_LUZ, if (oscuro) 0.5f else 0.85f), r.topLeft - Offset(3 * u, 3 * u), Size(r.width + 6 * u, r.height + 6 * u))
        drawOval(c(Color(0xFF4A6A8A), if (oscuro) 0.45f else 0.5f), r.topLeft, r.size)
        drawPath(Path().apply { moveTo(c0.x, c0.y - 9 * u); quadraticTo(c0.x - 9 * u, c0.y + 2 * u, c0.x - 7 * u, c0.y + 13 * u); lineTo(c0.x + 7 * u, c0.y + 13 * u); quadraticTo(c0.x + 9 * u, c0.y + 2 * u, c0.x, c0.y - 9 * u); close() }, c(Color(0xFF2E5AA0), 0.65f))
        drawCircle(c(Color(0xFFE8C9A8), 0.7f), 3 * u, Offset(c0.x, c0.y - 7 * u))
        drawCircle(c(Color(0xFFD9A93E), 0.8f), 5 * u, Offset(c0.x, c0.y - 7 * u), style = Stroke(0.9f * u))
        drawPath(Path().apply { moveTo(r.left - 6 * u, r.top - 2 * u); quadraticTo(c0.x, r.top - 14 * u, r.right + 6 * u, r.top - 2 * u); close() }, c(TEJA, 0.7f))
        val ly = r.bottom + 7 * u
        drawLine(c(HIERRO, 0.6f), Offset(c0.x, r.bottom + 3 * u), Offset(c0.x, ly - 2 * u), 0.8f * u)
        if (oscuro) halo(Offset(c0.x, ly), 16 * u, Color(0xFFFF6A3A).copy(alpha = 0.06f), 4)
        drawPath(Path().apply { moveTo(c0.x - 2.5f * u, ly - 2 * u); lineTo(c0.x + 2.5f * u, ly - 2 * u); lineTo(c0.x + 1.6f * u, ly + 3 * u); lineTo(c0.x - 1.6f * u, ly + 3 * u); close() }, c(Color(0xFFC8364A), if (oscuro) 0.95f else 0.7f))
    }

    // ---------------------------------------------------------------- las campañas

    /**
     * La motoneta que recorre el paseo, vista de lado y hacia la derecha: la carrocería redonda de atrás, el piso, el escudo
     * de adelante con su salpicadera, las dos ruedas chicas con su tapón de cromo, el asiento y, montada, la viajera con su
     * camisa blanca, el pañuelo rojo al viento y el casco del color de acento. El faro va encendido; de noche, con su halo y
     * su haz sobre el empedrado, y la figura lleva un filo claro para que se lea sobre el mapa oscuro.
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density; val p = e.punto
        sombraMarcadorContemporanea(p, 24 * u, e.salto, oscuro)
        val o = Offset(p.x, p.y - subidaContemporanea(e.salto))
        fun x(v: Float) = o.x + v * u
        fun y(v: Float) = o.y + v * u
        val llanta = t(HIERRO, oscuro); val cromo = t(Color(0xFFC9CDD2), oscuro); val verde = t(VESPA, oscuro)
        val verdeOsc = mezcla(verde, Color.Black, 0.28f); val filo = Color(0xFFF4EBD8).copy(alpha = 0.55f)
        // El haz del faro, de noche, sobre el suelo de adelante.
        if (oscuro) drawPath(trazoContemporanea(o, u, true, 9f, -17.8f, 27f, -13f, 27f, -1f, 9f, -15.8f), Color(0xFFFFF1C4).copy(alpha = 0.13f))
        // Las ruedas.
        listOf(-7f, 7.5f).forEach { cx ->
            drawCircle(llanta, 3.3f * u, Offset(x(cx), y(-3.3f)))
            drawCircle(cromo, 1.4f * u, Offset(x(cx), y(-3.3f)))
            drawCircle(llanta, 0.5f * u, Offset(x(cx), y(-3.3f)))
        }
        // La carrocería: el cofre redondo de atrás, el piso y el escudo de adelante, de una pieza.
        val cuerpo = Path().apply {
            moveTo(x(-11f), y(-4.2f)); cubicTo(x(-12f), y(-11.5f), x(-4f), y(-12.8f), x(-1.2f), y(-7.2f))
            lineTo(x(4.2f), y(-7f)); lineTo(x(5.6f), y(-16.4f)); lineTo(x(8.2f), y(-16.4f)); quadraticTo(x(9.8f), y(-10f), x(10.4f), y(-6.2f))
            quadraticTo(x(7.5f), y(-9.5f), x(4.6f), y(-5.4f)); lineTo(x(-3.6f), y(-4.6f)); quadraticTo(x(-7f), y(-8.4f), x(-10f), y(-4.2f)); close()
        }
        if (oscuro) drawPath(cuerpo, filo, style = Stroke(1.1f * u))
        drawPath(cuerpo, verde)
        drawPath(Path().apply { moveTo(x(-10.6f), y(-6f)); cubicTo(x(-10.4f), y(-10f), x(-5f), y(-11f), x(-2.4f), y(-8f)) }, mezcla(verde, Color.White, 0.45f), style = Stroke(0.7f * u))
        drawLine(verdeOsc, Offset(x(-1.2f), y(-7.1f)), Offset(x(4.4f), y(-6.9f)), 0.9f * u)
        drawLine(cromo, Offset(x(5.9f), y(-14.6f)), Offset(x(8.6f), y(-14.6f)), 0.6f * u)
        // El asiento.
        drawRoundRect(t(Color(0xFF4A3426), oscuro), Offset(x(-9.4f), y(-13.6f)), Size(7.6f * u, 2.2f * u), CornerRadius(1.1f * u))
        // La viajera: las piernas dobladas hacia el piso, la camisa, el brazo al manubrio.
        val pantalon = t(Color(0xFF2F3A4A), oscuro); val camisa = t(MARMOL, oscuro); val piel = t(Color(0xFFD9A57A), oscuro)
        drawPath(trazoContemporanea(o, u, false, -5.6f, -13.2f, 0.8f, -12.6f, 2.4f, -7.4f), pantalon, style = Stroke(2.1f * u, cap = StrokeCap.Round))
        drawLine(t(Color(0xFF6A3A22), oscuro), Offset(x(1.8f), y(-7.2f)), Offset(x(3.8f), y(-7.2f)), 1.3f * u, StrokeCap.Round)
        val torso = trazoContemporanea(o, u, true, -7.2f, -13.4f, -3.6f, -13.6f, -2.2f, -20.4f, -5.6f, -20.8f)
        if (oscuro) drawPath(torso, filo, style = Stroke(1f * u))
        drawPath(torso, camisa)
        drawLine(camisa, Offset(x(-3.4f), y(-19.6f)), Offset(x(5.6f), y(-18.3f)), 1.5f * u, StrokeCap.Round)
        drawCircle(piel, 0.8f * u, Offset(x(5.8f), y(-18.3f)))
        // El pañuelo rojo que ondea hacia atrás.
        drawPath(Path().apply { moveTo(x(-4.6f), y(-20.6f)); quadraticTo(x(-8f), y(-21.6f), x(-10.4f), y(-19.6f)); quadraticTo(x(-8f), y(-20f), x(-5f), y(-19.6f)); close() }, t(GERANIO, oscuro))
        // La cabeza con el casco del color de acento y su visera.
        drawCircle(piel, 2.3f * u, Offset(x(-3.4f), y(-22.8f)))
        drawArc(e.acento, 180f, 200f, true, Offset(x(-6f), y(-25.8f)), Size(5.4f * u, 5.4f * u))
        drawLine(mezcla(e.acento, Color.Black, 0.35f), Offset(x(-6f), y(-23.2f)), Offset(x(-0.4f), y(-23.2f)), 0.6f * u)
        drawCircle(Color.White.copy(alpha = 0.5f), 0.6f * u, Offset(x(-4.6f), y(-24.6f)))
        // El manubrio, el espejo y el faro.
        drawLine(t(HIERRO, oscuro), Offset(x(7f), y(-16.2f)), Offset(x(6.4f), y(-18.6f)), 1.2f * u)
        drawLine(cromo, Offset(x(4.4f), y(-18.6f)), Offset(x(8.4f), y(-18.6f)), 1.1f * u, StrokeCap.Round)
        drawLine(cromo, Offset(x(7.6f), y(-18.6f)), Offset(x(8.2f), y(-21.4f)), 0.5f * u)
        drawCircle(cromo, 0.8f * u, Offset(x(8.2f), y(-21.6f)))
        if (oscuro) halo(Offset(x(8.6f), y(-16.8f)), 5 * u, FAROL.copy(alpha = 0.1f), 4)
        drawCircle(cromo, 1.5f * u, Offset(x(8.6f), y(-16.8f)))
        drawCircle(if (oscuro) Color(0xFFFFF1C4) else FAROL, 1f * u, Offset(x(8.7f), y(-16.8f)))
    }

    /**
     * La fuente al final del paseo: la fachada de travertino con sus pilastras, la cornisa y el ático, el nicho con la estatua,
     * las rocas de donde cae el agua y la pila ancha; arriba, la moneda de oro que vuela por encima del hombro con su estela
     * (para volver a Roma) y dos más en el agua. A los lados, los faroles de la plaza. De noche, la fuente se ilumina desde
     * el agua: la pila brilla turquesa, la fachada se tiñe de luz cálida y los faroles se encienden.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val b = pieVictoriaContemporanea(e.meta, 50 * u, 46 * u, 4 * u)
        fun x(v: Float) = b.x + v * u
        fun y(v: Float) = b.y + v * u
        val trav = t(TRAVERTINO, oscuro); val luz = t(TRAVERTINO_LUZ, oscuro); val sombra = t(TRAVERTINO_SOMBRA, oscuro); val poro = t(TRAVERTINO_PORO, oscuro)
        val agua = if (oscuro) Color(0xFF3FD0D8) else Color(0xFF6FB3D9)
        // La fachada, con la luz de abajo de noche.
        drawRect(trav, Offset(x(-16f), y(-34f)), Size(32 * u, 28 * u))
        if (oscuro) drawRect(Brush.verticalGradient(listOf(Color.Transparent, FAROL.copy(alpha = 0.32f)), y(-34f), y(-6f)), Offset(x(-16f), y(-34f)), Size(32 * u, 28 * u))
        listOf(-13f, -8f, 8f, 13f).forEach { px ->
            drawRect(luz, Offset(x(px - 1f), y(-32f)), Size(2 * u, 25 * u))
            drawLine(sombra, Offset(x(px + 1f), y(-32f)), Offset(x(px + 1f), y(-7f)), 0.5f * u)
        }
        drawRect(sombra, Offset(x(-17.5f), y(-35.6f)), Size(35 * u, 2.4f * u))
        drawRect(luz, Offset(x(-17.5f), y(-36.4f)), Size(35 * u, 0.9f * u))
        // El ático con su remate y la cartela.
        drawRect(trav, Offset(x(-9f), y(-42f)), Size(18 * u, 6 * u))
        drawRect(luz, Offset(x(-5f), y(-41f)), Size(10 * u, 3.6f * u))
        drawLine(poro, Offset(x(-3.4f), y(-39.2f)), Offset(x(3.4f), y(-39.2f)), 0.6f * u)
        drawPath(trazoContemporanea(b, u, true, -10f, -42f, 0f, -46f, 10f, -42f), sombra)
        // El nicho con la estatua.
        drawPath(Path().apply { moveTo(x(-5f), y(-11f)); lineTo(x(-5f), y(-25f)); quadraticTo(x(0f), y(-32f), x(5f), y(-25f)); lineTo(x(5f), y(-11f)); close() }, sombra)
        drawPath(Path().apply { moveTo(x(-2.6f), y(-12f)); lineTo(x(-1.8f), y(-21.5f)); quadraticTo(x(0f), y(-23f), x(1.8f), y(-21.5f)); lineTo(x(2.6f), y(-12f)); close() }, luz)
        drawCircle(luz, 1.7f * u, Offset(x(0f), y(-24f)))
        drawLine(luz, Offset(x(1.4f), y(-21f)), Offset(x(3.8f), y(-26f)), 0.9f * u, StrokeCap.Round)
        // Las rocas y la cascada.
        drawPath(contornoIrregular(Rect(x(-12f), y(-12f), x(12f), y(-5f)), 23, 12, 0.3f), poro)
        for (k in -3..3) drawLine(agua.copy(alpha = 0.85f), Offset(x(k * 1.3f), y(-11f)), Offset(x(k * 2.4f), y(-3f)), 0.9f * u)
        // La pila: el agua y el borde de piedra.
        if (oscuro) halo(Offset(x(0f), y(-2f)), 16 * u, agua.copy(alpha = 0.035f), 4)
        drawArc(agua, 0f, 180f, true, Offset(x(-21f), y(-5f)), Size(42 * u, 9 * u))
        drawArc(Color.White.copy(alpha = 0.55f), 20f, 50f, false, Offset(x(-15f), y(-3.4f)), Size(30 * u, 5 * u), style = Stroke(0.6f * u))
        drawArc(sombra, 0f, 180f, false, Offset(x(-21f), y(-5f)), Size(42 * u, 9 * u), style = Stroke(1.6f * u))
        drawLine(luz, Offset(x(-21f), y(-0.6f)), Offset(x(21f), y(-0.6f)), 1.2f * u)
        // Las monedas: la del aire con su estela y dos en el fondo de la pila.
        val oro = OCRE_CLARO; val mon = Offset(x(-12f), y(-40f))
        drawPath(Path().apply { moveTo(x(-24f), y(-28f)); quadraticTo(x(-22f), y(-44f), mon.x, mon.y) }, oro.copy(alpha = 0.6f), style = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 1.6f * u))))
        if (oscuro) halo(mon, 6 * u, oro.copy(alpha = 0.12f), 3)
        drawCircle(OCRE_SOMBRA, 2.7f * u, mon); drawCircle(oro, 2.2f * u, mon); drawCircle(Color.White.copy(alpha = 0.7f), 0.8f * u, mon - Offset(0.8f * u, 0.8f * u))
        listOf(-7f to 1.2f, 9f to 2f).forEach { (dx, dy) -> drawOval(oro.copy(alpha = 0.85f), Offset(x(dx - 1.4f), y(dy - 0.6f)), Size(2.8f * u, 1.2f * u)) }
        // Los faroles de la plaza.
        listOf(-23f, 23f).forEach { fx ->
            drawLine(t(HIERRO, oscuro), Offset(x(fx), y(1f)), Offset(x(fx), y(-20f)), 1 * u)
            if (oscuro) halo(Offset(x(fx), y(-22f)), 7 * u, FAROL.copy(alpha = 0.1f), 4)
            drawPath(trazoContemporanea(b, u, true, fx - 1.8f, -20f, fx + 1.8f, -20f, fx + 1.2f, -24f, fx - 1.2f, -24f), if (oscuro) FAROL else t(Color(0xFFE8E0C8), false))
            drawPath(trazoContemporanea(b, u, true, fx - 2.2f, -24f, fx + 2.2f, -24f, fx, -26f), t(HIERRO, oscuro))
        }
    }

    // ---------------------------------------------------------------- objetos de Tu año

    /**
     * Urbe: la luna creciente sobre una cúpula de la ciudad (el tambor con sus ventanas, la cúpula y su linterna), dos
     * huellas de zapato de suela de cuero, una tras otra, y la barra de la palestra con sus discos.
     */
    override fun DrawScope.objetoAnio(e: EstadoObjeto, oscuro: Boolean) {
        val r = e.radio; val x = e.centro.x; val y = e.centro.y; val u = density
        val col = e.color; val fondo = e.fondo
        fun p(a: Float, b: Float) = Offset(x + a * r, y + b * r)
        fun g(f: Float) = maxOf(r * f, 0.8f * u)
        when (e.forma) {
            "luna" -> {
                drawPath(crecienteContemporanea(p(0.4f, -0.42f), r * 0.52f, Offset(r * 0.27f, -r * 0.17f), r * 0.44f), col)
                // La cúpula: media elipse sobre el tambor, la linterna arriba con su bolita.
                drawArc(col, 180f, 180f, true, p(-0.9f, 0.02f), Size(r * 1.36f, r * 1.06f))
                drawRect(col, p(-0.95f, 0.53f), Size(r * 1.46f, r * 0.42f))
                drawRect(col, p(-0.31f, -0.16f), Size(r * 0.18f, r * 0.24f))
                drawCircle(col, maxOf(r * 0.08f, 0.6f * u), p(-0.22f, -0.2f))
                if (r > 6 * u) {
                    drawLine(fondo, p(-0.9f, 0.55f), p(0.46f, 0.55f), g(0.05f))
                    for (k in 0..2) drawRoundRect(fondo, p(-0.74f + k * 0.42f, 0.64f), Size(maxOf(r * 0.14f, 0.8f * u), r * 0.22f), CornerRadius(r * 0.07f))
                }
            }
            "huella" -> {
                // Cada huella: la planta y el tacón aparte, como deja la suela de cuero en la piedra mojada.
                fun suela(cx: Float, cy: Float, giro: Float) = rotate(giro, p(cx, cy)) {
                    drawOval(col, p(cx - 0.24f, cy - 0.74f), Size(r * 0.48f, r * 0.82f))
                    drawOval(col, p(cx - 0.18f, cy + 0.2f), Size(r * 0.36f, r * 0.4f))
                    if (r > 8 * u) listOf(-0.5f, -0.28f).forEach { f -> drawLine(fondo, p(cx - 0.16f, cy + f), p(cx + 0.16f, cy + f), g(0.05f)) }
                }
                suela(-0.34f, 0.2f, -10f); suela(0.34f, -0.16f, 10f)
            }
            "pesa" -> {
                // La barra con sus collarines y dos discos por lado, el grande adentro.
                val b = g(0.07f)
                drawRect(col, Offset(x - r * 0.98f, y - b), Size(r * 1.96f, 2 * b))
                for (s in listOf(-1f, 1f)) {
                    drawRoundRect(col, p(s * 0.53f - 0.12f, -0.74f), Size(r * 0.24f, r * 1.48f), CornerRadius(r * 0.07f))
                    drawRoundRect(col, p(s * 0.77f - 0.08f, -0.5f), Size(r * 0.16f, r * 1.0f), CornerRadius(r * 0.05f))
                    drawRect(col, p(s * 0.33f - 0.05f, -0.2f), Size(r * 0.1f, r * 0.4f))
                    if (r > 6 * u) drawLine(fondo, p(s * 0.53f, -0.5f), p(s * 0.53f, -0.2f), g(0.05f))
                }
            }
            else -> {}
        }
    }
}

/**
 * Terminal (solo oscuro): el trofeo se apoya en el gabinete de escritorio de la computadora vieja, el de plástico beige
 * donde se ponía el monitor: la tapa con su junta, las bahías de la unidad de 5¼ y de la de 3½, la rejilla de ventilación,
 * el botón de encendido con sus pilotos verde y ámbar y la chapa del modelo en el metal del nivel. En el hall, una unidad
 * de rack: la chapa negra con sus orejas atornilladas, las asas del metal del nivel, la malla y las bahías con sus luces.
 * La placa es la pantalla de fósforo con su bisel y su filete de metal.
 */
object LogrosCiber : DisenoLogros {
    private val CARCASA = Color(0xFFC4BCA6)
    private val CARCASA_LUZ = Color(0xFFDCD5C1)
    private val CARCASA_SOMBRA = Color(0xFF948C76)
    private val PLASTICO = Color(0xFF34373B)
    private val PLASTICO_LUZ = Color(0xFF5A5E63)
    private val PLASTICO_OSC = Color(0xFF1B1D20)
    private val METAL = Color(0xFFB9BEC3)
    private val PANTALLA = Color(0xFF06110F)
    private val FOSFORO = Color(0xFF4FE3D6)
    private val FOSFORO_TENUE = Color(0xFF237A72)
    private val FOSFORO_CLARO = Color(0xFFBDEFF5)
    private val PILOTO = Color(0xFF5CF06A)
    private val AMBAR = Color(0xFFFFB347)

    // salón: colores
    private val PARED = Color(0xFF0E1A1E)
    private val LOSETA = Color(0xFF1A2428)
    private val TUBO = Color(0xFFDDF6FF)

    /** El color de siempre: el tema no tiene claro, así que todo va hundido como en su biblioteca. */
    private fun t(c: Color, oscuro: Boolean): Color = mezcla(c, Color(0xFF050607), 0.3f)

    // ---------------------------------------------------------------- pedestales y placa

    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val fino = h >= 90 * u
        val hl = maxOf(0.6f * u, h * 0.004f)
        fun c(x: Color) = t(x, true)
        val caja = Rect(w * 0.07f, h * 0.786f, w * 0.93f, h * 0.94f)
        val patas = listOf(Rect(w * 0.11f, caja.bottom - hl, w * 0.2f, h * 0.955f), Rect(w * 0.8f, caja.bottom - hl, w * 0.89f, h * 0.955f))
        if (!e.hecho) { silueta(true) { addRect(caja); patas.forEach { addRect(it) } }; return }
        sombraPie(w / 2, caja.width, h * 0.958f, h * 0.024f, 0.55f)
        patas.forEach { drawRect(c(PLASTICO_OSC), it.topLeft, it.size) }
        // El gabinete: el canto de sombra abajo, la cara, la luz del filo y la junta de la tapa.
        val radio = CornerRadius(h * 0.008f)
        drawRoundRect(c(CARCASA_SOMBRA), caja.topLeft, caja.size, radio)
        drawRoundRect(c(CARCASA), caja.topLeft, Size(caja.width, caja.height * 0.9f), radio)
        drawRect(c(CARCASA_LUZ), Offset(caja.left + h * 0.008f, caja.top), Size(caja.width - h * 0.016f, hl))
        val junta = caja.top + caja.height * 0.15f
        drawLine(c(CARCASA_SOMBRA), Offset(caja.left, junta), Offset(caja.right, junta), hl)
        drawLine(c(CARCASA_LUZ), Offset(caja.left, junta + hl), Offset(caja.right, junta + hl), hl * 0.6f)
        // Las bahías: la de 5¼ con su palanca y la de 3½ con su botón de expulsar; cada una con su ranura.
        val b1 = Rect(w * 0.11f, caja.top + caja.height * 0.26f, w * 0.43f, caja.top + caja.height * 0.48f)
        val b2 = Rect(w * 0.15f, caja.top + caja.height * 0.58f, w * 0.37f, caja.top + caja.height * 0.78f)
        listOf(b1, b2).forEach { b ->
            drawRect(c(CARCASA_SOMBRA), b.topLeft, b.size)
            drawRect(c(CARCASA_LUZ), Offset(b.left, b.bottom - hl * 0.7f), Size(b.width, hl * 0.7f))
            drawLine(c(PLASTICO_OSC), Offset(b.left + b.width * 0.08f, b.center.y), Offset(b.right - b.width * 0.08f, b.center.y), maxOf(hl, b.height * 0.16f))
        }
        if (fino) {
            drawRect(c(PLASTICO), Offset(b1.right - b1.width * 0.16f, b1.top + b1.height * 0.12f), Size(b1.width * 0.1f, b1.height * 0.3f))
            drawRect(c(CARCASA_LUZ), Offset(b2.right - b2.width * 0.2f, b2.bottom - b2.height * 0.36f), Size(b2.width * 0.13f, b2.height * 0.22f))
            drawCircle(PILOTO.copy(alpha = 0.8f), hl * 0.8f, Offset(b2.left + b2.width * 0.12f, b2.bottom - b2.height * 0.24f))
        }
        // La rejilla de ventilación: ranuras verticales.
        val rej = Rect(w * 0.65f, caja.top + caja.height * 0.27f, w * 0.89f, caja.top + caja.height * 0.8f)
        val paso = maxOf(w * 0.026f, 2.2f * u)
        var x = rej.left; var n = 0
        while (x < rej.right && n < 40) { drawLine(c(PLASTICO_OSC).copy(alpha = 0.85f), Offset(x, rej.top), Offset(x, rej.bottom), maxOf(hl, paso * 0.4f), StrokeCap.Round); x += maxOf(paso, 1f); n++ }
        // La chapa del modelo en el metal del nivel, el botón de encendido y los pilotos encendidos.
        val pl = Rect(w * 0.465f, caja.top + caja.height * 0.26f, w * 0.605f, caja.top + caja.height * 0.45f)
        chapa(e, pl, h * 0.003f)
        if (fino) for (k in 0 until 3) drawLine(e.metalOscuro.copy(alpha = 0.7f), Offset(pl.left + pl.width * (0.2f + k * 0.2f), pl.center.y), Offset(pl.left + pl.width * (0.32f + k * 0.2f), pl.center.y), hl * 0.6f)
        val bot = Rect(w * 0.475f, caja.top + caja.height * 0.56f, w * 0.535f, caja.top + caja.height * 0.8f)
        drawRect(c(CARCASA_SOMBRA), bot.topLeft, bot.size)
        drawRect(c(CARCASA_LUZ), bot.topLeft, Size(bot.width, hl * 0.7f))
        val rl = maxOf(h * 0.0065f, 0.9f * u)
        val l1 = Offset(w * 0.575f, bot.top + bot.height * 0.25f); val l2 = Offset(w * 0.575f, bot.top + bot.height * 0.78f)
        drawCircle(PILOTO.copy(alpha = 0.25f), rl * 2.6f, l1); drawCircle(PILOTO, rl, l1)
        drawCircle(AMBAR.copy(alpha = 0.2f), rl * 2.2f, l2); drawCircle(AMBAR, rl, l2)
    }

    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val m = tonos(e, true)
        fun c(x: Color) = t(x, true)
        sombraPie(w / 2, w * 0.86f, h * 0.94f, h * 0.18f, 0.5f)
        // La chapa negra de la unidad y las orejas de metal con sus tornillos.
        val frente = Rect(w * 0.07f, h * 0.1f, w * 0.93f, h * 0.88f)
        drawRect(c(PLASTICO_OSC), frente.topLeft, frente.size)
        drawRect(c(PLASTICO_LUZ), frente.topLeft, Size(frente.width, 0.8f * u))
        drawRect(Color.Black.copy(alpha = 0.5f), Offset(frente.left, frente.bottom - 1f * u), Size(frente.width, 1f * u))
        listOf(Rect(0f, h * 0.1f, w * 0.07f, h * 0.88f), Rect(w * 0.93f, h * 0.1f, w, h * 0.88f)).forEach { o ->
            drawRect(c(METAL), o.topLeft, o.size)
            drawRect(c(PLASTICO_LUZ), Offset(o.right - 0.6f * u, o.top), Size(0.6f * u, o.height))
            listOf(0.28f, 0.72f).forEach { f -> drawCircle(c(PLASTICO_OSC), 1.1f * u, Offset(o.center.x, o.top + o.height * f)) }
        }
        // Las asas del metal del nivel.
        listOf(w * 0.085f, w * 0.88f).forEach { x0 -> chapa(m, Rect(x0, h * 0.2f, x0 + w * 0.035f, h * 0.78f), 1f * u) }
        // La malla de ventilación.
        val malla = Rect(w * 0.16f, h * 0.24f, w * 0.48f, h * 0.74f)
        val paso = maxOf(2.4f * u, 1f)
        var y = malla.top; var fila = 0
        while (y < malla.bottom && fila < 20) {
            var x = malla.left + (if (fila % 2 == 1) paso / 2 else 0f); var n = 0
            while (x < malla.right && n < 60) { drawCircle(Color.Black.copy(alpha = 0.85f), 0.75f * u, Offset(x, y)); x += paso; n++ }
            y += paso * 0.86f; fila++
        }
        // Las cuatro bahías, cada una con su luz de estado.
        for (k in 0 until 4) {
            val b = Rect(w * (0.52f + k * 0.085f), h * 0.24f, w * (0.595f + k * 0.085f), h * 0.74f)
            drawRect(c(PLASTICO), b.topLeft, b.size)
            drawRect(c(PLASTICO_LUZ), b.topLeft, Size(b.width, 0.6f * u))
            val col = if (k == 2) AMBAR else FOSFORO
            drawCircle(col.copy(alpha = 0.25f), 2f * u, Offset(b.center.x, b.bottom - 2.2f * u))
            drawCircle(col, 0.9f * u, Offset(b.center.x, b.bottom - 2.2f * u))
        }
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = FOSFORO_CLARO
    override fun rotuloPlaca(oscuro: Boolean): Color = FOSFORO

    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val m = tonos(e, true)
        val r0 = Rect(Offset.Zero, size)
        drawRoundRect(Color.Black.copy(alpha = 0.5f), Offset(0f, 1.6f * u), size, CornerRadius(5 * u))
        // El bisel de plástico, el filete del metal del nivel y el vidrio negro de la pantalla.
        drawRoundRect(t(PLASTICO, true), Offset.Zero, size, CornerRadius(5 * u))
        drawRoundRect(t(PLASTICO_LUZ, true), Offset(1f * u, 0.6f * u), Size(w - 2 * u, 0.8f * u), CornerRadius(0.4f * u))
        filete(m, redondo(r0.deflate(2.2f * u), 4 * u), 1.8f * u)
        val vidrio = r0.deflate(3.8f * u)
        drawRoundRect(PANTALLA, vidrio.topLeft, vidrio.size, CornerRadius(3 * u))
        // El resplandor del fósforo en la orilla del vidrio y las escuadras de las esquinas.
        drawRoundRect(FOSFORO_TENUE.copy(alpha = 0.45f), vidrio.topLeft + Offset(1.1f * u, 1.1f * u), Size(vidrio.width - 2.2f * u, vidrio.height - 2.2f * u), CornerRadius(2.4f * u), style = Stroke(0.6f * u))
        val l = minOf(5f * u, vidrio.height * 0.25f)
        listOf(vidrio.topLeft to Offset(1f, 1f), Offset(vidrio.right, vidrio.top) to Offset(-1f, 1f), Offset(vidrio.left, vidrio.bottom) to Offset(1f, -1f), vidrio.bottomRight to Offset(-1f, -1f)).forEach { (p, s) ->
            val q = Offset(p.x + s.x * 2.2f * u, p.y + s.y * 2.2f * u)
            drawLine(FOSFORO.copy(alpha = 0.7f), q, Offset(q.x + s.x * l, q.y), 0.8f * u)
            drawLine(FOSFORO.copy(alpha = 0.7f), q, Offset(q.x, q.y + s.y * l * 0.7f), 0.8f * u)
        }
        // El piloto del monitor, en el bisel de abajo.
        drawCircle(PILOTO, 0.8f * u, Offset(w - 7 * u, h - 1.3f * u))
    }

    // ---------------------------------------------------------------- el salón del hall

    /**
     * La sala central de cómputo de noche, siempre en oscuro: la pared de paneles con su luz de fósforo, y detrás de cada
     * trofeo el hueco de un gabinete que se retiró, con su marco y sus rieles; entre las franjas, los gabinetes de cinta
     * con sus dos carretes tras la ventanilla ahumada (uno por piso), los pilotos de estado en hileras, la placa del modelo y
     * la rejilla al pie. Arriba, el plafón con sus lámparas de tubo y la charola de cables, de la que cuelgan mazos en los
     * bordes. Abajo, el piso técnico de losetas en perspectiva, una perforada, con su filo de luz. En la esquina izquierda,
     * la consola del operador con su monitor de tubo y el prompt; en la derecha, la impresora de líneas con su papel continuo
     * que cae en pliegues.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) = clipRect { salon(e, oscuro) }

    /** La escena del salón, recortada al lienzo por [escenaSalon]. */
    private fun DrawScope.salon(e: EstadoSalon, oscuro: Boolean) {
        val p = planta(e); val u = p.u; val w = p.w; val h = p.h
        fun c(x: Color, f: Float) = mezcla(e.fondo, t(x, oscuro), f)
        val techo = 30 * u; val piso = h - 34 * u

        // La pared de paneles, con sus juntas, y la luz de fósforo que la baña desde arriba.
        drawRect(c(PARED, 0.85f), Offset(0f, techo), Size(w, piso - techo))
        drawRect(Brush.verticalGradient(listOf(FOSFORO.copy(alpha = 0.07f), Color.Transparent), techo, techo + 180 * u), Offset(0f, techo), Size(w, 180 * u))
        run {
            var x = 0f; var k = 0
            while (x < w && k < 40) { drawLine(c(Color.Black, 0.6f), Offset(x, techo), Offset(x, piso), 1 * u); drawLine(c(FOSFORO_TENUE, 0.12f), Offset(x + 1 * u, techo), Offset(x + 1 * u, piso), 0.6f * u); x += maxOf(p.paso / 4, 1f); k++ }
            var y = techo + 60 * u; k = 0
            while (y < piso && k < 30) { drawLine(c(Color.Black, 0.5f), Offset(0f, y), Offset(w, y), 0.8f * u); y += maxOf(96 * u, 1f); k++ }
        }
        // Detrás de cada trofeo, el hueco del gabinete que se llevaron: el marco, los rieles con sus agujeros y la etiqueta.
        for (i in 0 until p.n) for (k in 0 until p.filas) {
            val x = p.cx(i); val y0 = p.y(k) + 4 * u; val y1 = minOf(p.y(k) + p.alto * 0.86f, piso - 6 * u)
            val an = minOf(p.paso - 40 * u, 120 * u)
            if (y1 - y0 < 40 * u) continue
            drawRect(c(Color.Black, 0.35f), Offset(x - an / 2, y0), Size(an, y1 - y0))
            drawRect(c(FOSFORO_TENUE, 0.22f), Offset(x - an / 2, y0), Size(an, y1 - y0), style = Stroke(1 * u))
            listOf(x - an / 2 + 4 * u, x + an / 2 - 4 * u).forEach { rx ->
                drawLine(c(METAL, 0.12f), Offset(rx, y0 + 2 * u), Offset(rx, y1 - 2 * u), 3 * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 2.5f * u)))
            }
            drawRect(c(CARCASA, 0.16f), Offset(x - 12 * u, y1 - 9 * u), Size(24 * u, 5 * u))
        }

        // Los gabinetes de cinta entre las franjas: chapa, carretes tras su ventanilla, pilotos, placa y rejilla.
        val ancho = 28 * u
        for (i in 0..p.n) {
            val x = p.lx(i)
            drawRect(c(PLASTICO_OSC, 0.95f), Offset(x - ancho / 2, techo), Size(ancho, piso - techo))
            drawRect(c(PLASTICO_LUZ, 0.5f), Offset(x - ancho / 2, techo), Size(1.2f * u, piso - techo))
            drawRect(c(PLASTICO, 0.7f), Offset(x + ancho / 2 - 1.6f * u, techo), Size(1.6f * u, piso - techo))
            drawRect(c(CARCASA, 0.55f), Offset(x - ancho / 2, techo + 2 * u), Size(ancho, 5 * u))
            for (k in 0 until p.filas) {
                val y0 = maxOf(p.y(k) + 10 * u, techo + 12 * u)
                if (y0 + 120 * u > piso) continue
                // La ventanilla ahumada con sus dos carretes de cinta.
                val ven = Rect(x - ancho / 2 + 3 * u, y0, x + ancho / 2 - 3 * u, y0 + 58 * u)
                drawRoundRect(c(PANTALLA, 0.95f), ven.topLeft, ven.size, CornerRadius(2 * u))
                drawRoundRect(c(FOSFORO_TENUE, 0.5f), ven.topLeft, ven.size, CornerRadius(2 * u), style = Stroke(0.8f * u))
                listOf(ven.top + 14 * u, ven.top + 42 * u).forEachIndexed { j, cy ->
                    val cc = Offset(x, cy); val r = 10 * u
                    val lleno = 0.45f + 0.45f * azar(e.semilla + i, k * 4 + j)
                    drawCircle(c(Color(0xFF3A2A1C), 0.8f), r * lleno, cc)
                    drawCircle(c(METAL, 0.55f), r, cc, style = Stroke(1 * u))
                    for (a in 0 until 3) { val ang = a * 2 * PI / 3 + j * 0.6 + k; drawLine(c(METAL, 0.5f), cc, Offset(cc.x + (r * 0.4f * cos(ang)).toFloat(), cc.y + (r * 0.4f * sin(ang)).toFloat()), 1.1f * u) }
                    drawCircle(c(PLASTICO_OSC, 1f), 2 * u, cc)
                }
                drawLine(c(Color(0xFF3A2A1C), 0.8f), Offset(x + 9 * u, ven.top + 14 * u), Offset(x + 9 * u, ven.top + 42 * u), 0.8f * u)
                drawLine(Color.White.copy(alpha = 0.08f), Offset(ven.left + 2 * u, ven.top + 4 * u), Offset(ven.left + 8 * u, ven.top + 2 * u), 1.4f * u)
                // Las hileras de pilotos: cada hilera, un trazo punteado de su largo.
                for (f in 0 until 6) {
                    val y = ven.bottom + 8 * u + f * 4.4f * u
                    val on = 1.5f + 3 * azar(e.semilla + 40 + i, k * 10 + f)
                    val col = if (f == 2) AMBAR else if (f % 2 == 0) PILOTO else FOSFORO
                    drawLine(col.copy(alpha = 0.75f), Offset(x - 10 * u, y), Offset(x + 10 * u, y), 1.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.8f * u, on * u)))
                }
                // La placa del modelo y los dos botones.
                drawRect(c(CARCASA, 0.5f), Offset(x - 9 * u, ven.bottom + 38 * u), Size(18 * u, 5 * u))
                drawLine(c(PLASTICO_OSC, 0.9f), Offset(x - 6 * u, ven.bottom + 40.5f * u), Offset(x + 3 * u, ven.bottom + 40.5f * u), 1 * u)
                listOf(-5f, 5f).forEach { dx -> drawCircle(c(if (dx < 0) Color(0xFFB0302A) else METAL, 0.6f), 2 * u, Offset(x + dx * u, ven.bottom + 50 * u)) }
                // La rejilla de ventilación bajo cada unidad.
                for (f in 0 until 5) drawLine(c(Color.Black, 1f), Offset(x - 9 * u, ven.bottom + 58 * u + f * 3 * u), Offset(x + 9 * u, ven.bottom + 58 * u + f * 3 * u), 1.2f * u)
            }
        }

        // El plafón: las lámparas de tubo (con su halo) y la charola de cables con los mazos que cuelgan en los bordes.
        drawRect(c(Color(0xFF0A1215), 1f), Offset.Zero, Size(w, techo))
        run {
            var x = 10 * u; var k = 0
            while (x < w && k < 30) {
                drawRect(FOSFORO_CLARO.copy(alpha = 0.04f), Offset(x - 6 * u, 2 * u), Size(40 * u, 14 * u))
                drawRect(c(TUBO, 0.55f), Offset(x, 6 * u), Size(28 * u, 2.4f * u))
                drawRect(c(TUBO, 0.4f), Offset(x, 10 * u), Size(28 * u, 2.4f * u))
                x += maxOf(64 * u, 1f); k++
            }
        }
        drawRect(c(METAL, 0.22f), Offset(0f, techo - 10 * u), Size(w, 8 * u))
        run { var x = 0f; var k = 0; while (x < w && k < 120) { drawLine(c(Color.Black, 0.8f), Offset(x, techo - 10 * u), Offset(x, techo - 2 * u), 0.8f * u); x += maxOf(6 * u, 1f); k++ } }
        listOf(FOSFORO_TENUE, Color(0xFF6A5A30), Color(0xFF3A3F55)).forEachIndexed { j, col ->
            drawLine(c(col, 0.6f), Offset(0f, techo - 7 * u + j * 2.2f * u), Offset(w, techo - 7 * u + j * 2.2f * u), 1.4f * u)
        }
        listOf(16 * u, w - 18 * u).forEachIndexed { s, x ->
            for (j in 0 until 3) {
                val caida = (40 + 30 * azar(e.semilla + 60, s * 3 + j)) * u
                drawPath(Path().apply { moveTo(x - 3 * u + j * 3 * u, techo - 2 * u); quadraticTo(x + (j - 1) * 8 * u, techo + caida, x + (if (s == 0) -14f else 14f) * u, techo + caida * 0.4f) },
                    c(listOf(FOSFORO_TENUE, Color(0xFF6A5A30), Color(0xFF3A3F55))[j], 0.6f), style = Stroke(1.6f * u))
            }
        }
        drawLine(c(FOSFORO, 0.35f), Offset(0f, techo), Offset(w, techo), 1 * u)

        // El piso técnico: las losetas en perspectiva, la perforada y el filo de luz.
        drawRect(c(LOSETA, 0.9f), Offset(0f, piso), Size(w, h - piso))
        drawRect(Brush.verticalGradient(listOf(FOSFORO.copy(alpha = 0.1f), Color.Transparent), piso, h), Offset(0f, piso), Size(w, h - piso))
        val fuga = Offset(w / 2, piso - 200 * u)
        run {
            var x = -w; var k = 0
            while (x < 2 * w && k < 60) {
                val t0 = (piso - fuga.y) / (h - fuga.y)
                drawLine(c(Color.Black, 0.8f), Offset(fuga.x + (x - fuga.x) * t0, piso), Offset(x, h), 0.9f * u)
                x += maxOf(40 * u, 1f); k++
            }
        }
        listOf(0.22f, 0.5f, 0.82f).forEach { f -> drawLine(c(Color.Black, 0.8f), Offset(0f, piso + (h - piso) * f), Offset(w, piso + (h - piso) * f), 0.9f * u) }
        run {
            val px = w / 2 - 20 * u
            for (f in 0 until 3) for (j in 0 until 8) drawCircle(c(Color.Black, 0.9f), 0.8f * u, Offset(px + j * 5 * u + f * 1.5f * u, piso + (h - piso) * (0.3f + 0.16f * f)))
        }
        drawLine(c(FOSFORO, 0.5f), Offset(0f, piso), Offset(w, piso), 1.2f * u)

        // La consola del operador, abajo a la izquierda: el monitor de tubo con el prompt y el teclado.
        run {
            val x0 = 2 * u; val y0 = piso - 40 * u
            drawRoundRect(c(CARCASA, 0.55f), Offset(x0, y0), Size(34 * u, 28 * u), CornerRadius(3 * u))
            drawRoundRect(c(CARCASA_SOMBRA, 0.5f), Offset(x0 + 6 * u, y0 + 28 * u), Size(22 * u, 4 * u), CornerRadius(1 * u))
            val pan = Rect(x0 + 4 * u, y0 + 4 * u, x0 + 30 * u, y0 + 22 * u)
            halo(pan.center, 26 * u, FOSFORO.copy(alpha = 0.03f), 5)
            drawRoundRect(c(PANTALLA, 1f), pan.topLeft, pan.size, CornerRadius(2.5f * u))
            for (f in 0 until 3) drawLine(FOSFORO.copy(alpha = 0.8f), Offset(pan.left + 2 * u, pan.top + (4 + 4 * f) * u), Offset(pan.left + (6 + 14 * azar(e.semilla + 70, f)) * u, pan.top + (4 + 4 * f) * u), 1.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.6f * u, 0.8f * u)))
            drawRect(FOSFORO_CLARO.copy(alpha = 0.95f), Offset(pan.left + 6 * u, pan.top + 14.4f * u), Size(2.4f * u, 1.4f * u))
            drawCircle(PILOTO.copy(alpha = 0.9f), 0.9f * u, Offset(x0 + 28 * u, y0 + 25 * u))
            drawPath(Path().apply { moveTo(x0, piso - 4 * u); lineTo(x0 + 36 * u, piso - 4 * u); lineTo(x0 + 32 * u, piso - 9 * u); lineTo(x0 + 4 * u, piso - 9 * u); close() }, c(CARCASA_SOMBRA, 0.55f))
            for (f in 0 until 2) drawLine(c(PLASTICO_OSC, 0.9f), Offset(x0 + 6 * u, piso - (7.6f - f * 2) * u), Offset(x0 + 30 * u, piso - (7.6f - f * 2) * u), 1 * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.6f * u, 0.8f * u)))
        }
        // La impresora de líneas, abajo a la derecha, con el papel continuo que cae en pliegues hasta el piso.
        run {
            val x1 = w - 2 * u; val y0 = piso - 30 * u
            drawRect(c(CARCASA, 0.5f), Offset(x1 - 34 * u, y0), Size(34 * u, 26 * u))
            drawRect(c(PLASTICO_OSC, 0.9f), Offset(x1 - 30 * u, y0 + 4 * u), Size(26 * u, 3 * u))
            drawCircle(AMBAR.copy(alpha = 0.85f), 0.9f * u, Offset(x1 - 6 * u, y0 + 20 * u))
            val papel = c(Color(0xFFE8F0E6), 0.32f)
            drawRect(papel, Offset(x1 - 28 * u, y0 - 14 * u), Size(22 * u, 18 * u))
            for (f in 0 until 4) drawLine(c(Color(0xFF6FA88A), 0.3f), Offset(x1 - 28 * u, y0 - (12 - f * 4) * u), Offset(x1 - 6 * u, y0 - (12 - f * 4) * u), 1.4f * u)
            drawLine(c(Color.Black, 0.5f), Offset(x1 - 27 * u, y0 - 14 * u), Offset(x1 - 27 * u, y0 + 4 * u), 0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.8f * u, 1.6f * u)))
            for (f in 0 until 3) drawPath(Path().apply { val y = h - 4 * u - f * 3 * u; moveTo(x1 - 30 * u, y); lineTo(x1 - 8 * u, y - 1.5f * u) }, papel, style = Stroke(2.6f * u))
        }
    }

    // ---------------------------------------------------------------- las campañas

    /**
     * La terminal que recorre la red: el monitor de tubo de plástico beige, de frente, sobre su pie giratorio, con la
     * pantalla de fósforo encendida (el indicador «>», una línea de texto y el cursor de bloque), sus líneas de barrido y el
     * resplandor verde azulado alrededor; abajo, la barbilla con el piloto del color de acento y, delante, el teclado con sus
     * hileras de teclas. Solo existe de noche, así que el fósforo es lo que la hace leerse sobre el mapa.
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density; val p = e.punto
        sombraMarcadorContemporanea(p, 24 * u, e.salto, true)
        val o = Offset(p.x, p.y - subidaContemporanea(e.salto))
        fun x(v: Float) = o.x + v * u
        fun y(v: Float) = o.y + v * u
        val carcasa = t(CARCASA, oscuro); val luz = t(CARCASA_LUZ, oscuro); val sombra = t(CARCASA_SOMBRA, oscuro)
        // El resplandor de la pantalla.
        halo(Offset(x(0f), y(-14.6f)), 15 * u, FOSFORO.copy(alpha = 0.05f), 4)
        // El pie giratorio.
        drawOval(sombra, Offset(x(-5f), y(-5f)), Size(10 * u, 2.6f * u))
        drawRect(sombra, Offset(x(-2.2f), y(-7f)), Size(4.4f * u, 2.6f * u))
        // La caja del monitor, con su bisel.
        drawRoundRect(sombra, Offset(x(-8.8f), y(-22f)), Size(17.6f * u, 15.6f * u), CornerRadius(2 * u))
        drawRoundRect(carcasa, Offset(x(-8.8f), y(-22f)), Size(17f * u, 14.8f * u), CornerRadius(2 * u))
        drawLine(luz, Offset(x(-7.4f), y(-21.6f)), Offset(x(7f), y(-21.6f)), 0.6f * u)
        // La pantalla: el vidrio, el texto, el cursor y las líneas de barrido.
        val pantalla = Rect(x(-6.8f), y(-20.2f), x(6.2f), y(-10.4f))
        drawRoundRect(Color(0xFF020807), pantalla.topLeft - Offset(0.6f * u, 0.6f * u), Size(pantalla.width + 1.2f * u, pantalla.height + 1.2f * u), CornerRadius(1.8f * u))
        drawRoundRect(PANTALLA, pantalla.topLeft, pantalla.size, CornerRadius(1.4f * u))
        drawPath(trazoContemporanea(o, u, false, -5.6f, -18.6f, -4.2f, -17.4f, -5.6f, -16.2f), FOSFORO, style = Stroke(0.7f * u))
        drawRect(FOSFORO.copy(alpha = 0.18f), Offset(x(-3.4f), y(-18.6f)), Size(3.6f * u, 2.6f * u))
        drawRect(FOSFORO, Offset(x(-3f), y(-18.4f)), Size(2.2f * u, 2.2f * u))
        drawLine(FOSFORO_TENUE, Offset(x(-5.6f), y(-14.2f)), Offset(x(3.6f), y(-14.2f)), 0.8f * u)
        drawLine(FOSFORO_TENUE, Offset(x(-5.6f), y(-12.2f)), Offset(x(0.6f), y(-12.2f)), 0.8f * u)
        for (k in 0 until 9) drawLine(Color.Black.copy(alpha = 0.22f), Offset(pantalla.left, pantalla.top + (k + 0.5f) * pantalla.height / 9), Offset(pantalla.right, pantalla.top + (k + 0.5f) * pantalla.height / 9), 0.35f * u)
        drawLine(Color.White.copy(alpha = 0.12f), Offset(pantalla.left + 1 * u, pantalla.bottom - 1 * u), Offset(pantalla.left + 5 * u, pantalla.top + 1 * u), 1.4f * u)
        // La barbilla: la rejilla, el piloto de acento y el botón.
        for (k in 0 until 3) drawLine(sombra, Offset(x(-6f + k * 1.4f), y(-9.4f)), Offset(x(-6f + k * 1.4f), y(-8.2f)), 0.5f * u)
        halo(Offset(x(5.4f), y(-8.8f)), 2.4f * u, e.acento.copy(alpha = 0.25f), 2)
        drawCircle(e.acento, 0.6f * u, Offset(x(5.4f), y(-8.8f)))
        drawRect(sombra, Offset(x(2f), y(-9.3f)), Size(2 * u, 1 * u))
        // El teclado, delante, con sus hileras de teclas.
        drawPath(trazoContemporanea(o, u, true, -11.4f, -0.2f, 11.4f, -0.2f, 10.4f, -3.8f, -10.4f, -3.8f), carcasa)
        drawLine(sombra, Offset(x(-11.4f), y(-0.3f)), Offset(x(11.4f), y(-0.3f)), 0.9f * u)
        for (fila in 0 until 3) {
            val yy = -3.1f + fila * 1f; val n = 11
            for (k in 0 until n) {
                val ancho = 20f - fila * 0.6f
                drawRect(t(PLASTICO_LUZ, oscuro).copy(alpha = 0.85f), Offset(x(-ancho / 2 + k * ancho / n + 0.15f), y(yy)), Size(ancho / n * 0.7f * u, 0.6f * u))
            }
        }
        drawRect(t(PLASTICO_LUZ, oscuro), Offset(x(-4f), y(-0.9f)), Size(8 * u, 0.5f * u))
    }

    /**
     * El acceso concedido: el monitor grande de la terminal, con la pantalla de fósforo que muestra el candado abierto, la
     * barra de progreso llena, la palomita, las líneas de texto y el indicador con su cursor; sus líneas de barrido, el reflejo
     * del vidrio, la barbilla con sus pilotos verde y ámbar y el pie. Alrededor, un confeti de píxeles (fósforo, ámbar, verde
     * y el color de acento) y el resplandor de la pantalla.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val b = pieVictoriaContemporanea(e.meta, 62 * u, 56 * u)
        fun x(v: Float) = b.x + v * u
        fun y(v: Float) = b.y + v * u
        val carcasa = t(CARCASA, oscuro); val luz = t(CARCASA_LUZ, oscuro); val sombra = t(CARCASA_SOMBRA, oscuro)
        halo(Offset(x(0f), y(-28f)), 30 * u, FOSFORO.copy(alpha = 0.04f), 5)
        // El pie y el cuello.
        drawOval(sombra, Offset(x(-13f), y(-5f)), Size(26 * u, 5 * u))
        drawOval(carcasa, Offset(x(-12f), y(-5.4f)), Size(24 * u, 3.6f * u))
        drawRect(sombra, Offset(x(-5f), y(-11f)), Size(10 * u, 7 * u))
        // La caja.
        drawRoundRect(sombra, Offset(x(-22f), y(-46f)), Size(44 * u, 36 * u), CornerRadius(4 * u))
        drawRoundRect(carcasa, Offset(x(-22f), y(-46f)), Size(43 * u, 35 * u), CornerRadius(4 * u))
        drawLine(luz, Offset(x(-19f), y(-45.4f)), Offset(x(18f), y(-45.4f)), 0.9f * u)
        // La pantalla.
        val r = Rect(x(-18f), y(-42.5f), x(17f), y(-17f))
        drawRoundRect(Color(0xFF020807), r.topLeft - Offset(1.2f * u, 1.2f * u), Size(r.width + 2.4f * u, r.height + 2.4f * u), CornerRadius(4 * u))
        drawRoundRect(PANTALLA, r.topLeft, r.size, CornerRadius(3 * u))
        // El candado abierto: el cuerpo, el ojo de la llave y el grillete levantado y girado.
        val cand = Offset(x(-8f), y(-26f))
        halo(cand, 9 * u, FOSFORO.copy(alpha = 0.07f), 3)
        drawArc(FOSFORO, 180f, 180f, false, Offset(cand.x - 1.4f * u, cand.y - 13.4f * u), Size(7 * u, 8 * u), style = Stroke(1.5f * u))
        drawLine(FOSFORO, Offset(cand.x + 5.6f * u, cand.y - 9.4f * u), Offset(cand.x + 5.6f * u, cand.y - 7.4f * u), 1.5f * u)
        drawRoundRect(FOSFORO, Offset(cand.x - 5 * u, cand.y - 5 * u), Size(10 * u, 8 * u), CornerRadius(1 * u))
        drawCircle(PANTALLA, 1.2f * u, Offset(cand.x, cand.y - 1.6f * u))
        drawRect(PANTALLA, Offset(cand.x - 0.5f * u, cand.y - 1.4f * u), Size(1 * u, 2.6f * u))
        // La barra de progreso llena, la palomita y el texto.
        drawRect(FOSFORO, Offset(x(1f), y(-38.6f)), Size(13 * u, 3 * u), style = Stroke(0.6f * u))
        for (k in 0 until 6) drawRect(FOSFORO, Offset(x(1.8f + k * 2.05f), y(-37.8f)), Size(1.6f * u, 1.4f * u))
        drawPath(trazoContemporanea(b, u, false, 3f, -30f, 5.6f, -27.2f, 11.4f, -33.4f), FOSFORO_CLARO, style = Stroke(1.6f * u, cap = StrokeCap.Square))
        listOf(9f, 6f, 11f).forEachIndexed { i, l -> drawLine(FOSFORO_TENUE, Offset(x(1f), y(-24.4f + i * 2f)), Offset(x(1f + l), y(-24.4f + i * 2f)), 0.9f * u) }
        drawPath(trazoContemporanea(b, u, false, -16f, -21.6f, -14.6f, -20.4f, -16f, -19.2f), FOSFORO, style = Stroke(0.8f * u))
        drawRect(FOSFORO, Offset(x(-13.4f), y(-21.6f)), Size(2 * u, 2.4f * u))
        // Las líneas de barrido y el reflejo.
        val paso = maxOf(1.4f * u, 1f)
        var yy = r.top + paso / 2; var n = 0
        while (yy < r.bottom && n < 400) { drawLine(Color.Black.copy(alpha = 0.2f), Offset(r.left, yy), Offset(r.right, yy), 0.4f * u); yy += paso; n++ }
        drawPath(trazoContemporanea(b, u, true, -16f, -18f, -9f, -18f, -2f, -41.5f, -9f, -41.5f), Color.White.copy(alpha = 0.06f))
        // La barbilla.
        for (k in 0 until 6) drawLine(sombra, Offset(x(-17f + k * 1.6f), y(-14.2f)), Offset(x(-17f + k * 1.6f), y(-11.8f)), 0.6f * u)
        listOf(PILOTO to 10f, AMBAR to 13f).forEach { (c, px) -> halo(Offset(x(px), y(-13f)), 3 * u, c.copy(alpha = 0.22f), 2); drawCircle(c, 0.8f * u, Offset(x(px), y(-13f))) }
        drawRect(t(METAL, oscuro), Offset(x(-3f), y(-14f)), Size(6 * u, 1.8f * u))
        // El confeti de píxeles.
        val colores = listOf(FOSFORO, AMBAR, PILOTO, e.acento, FOSFORO_CLARO)
        for (k in 0 until 18) {
            val a = (PI * (1.05 + 0.9 * azar(77, k))).toFloat(); val d = (31 + 12 * azar(77, k + 40)) * u
            val c = Offset(x(0f) + d * cos(a) * 0.76f, y(-28f) + d * sin(a) * 0.66f)
            val s = (if (k % 3 == 0) 1.6f else 1.1f) * u
            drawRect(colores[k % colores.size], Offset(c.x - s / 2, c.y - s / 2), Size(s, s))
        }
    }

    // ---------------------------------------------------------------- objetos de Tu año

    /**
     * Terminal: los tres objetos en celdas de pantalla, como arte de caracteres: la luna con su lado en sombra y sus
     * cráteres, la suela con su dibujo y la mancuerna.
     */
    override fun DrawScope.objetoAnio(e: EstadoObjeto, oscuro: Boolean) {
        val r = e.radio; val x = e.centro.x; val y = e.centro.y
        val col = e.color; val tenue = mezcla(e.fondo, col, 0.3f)
        // «#» el fósforo encendido, «+» el tenue, «.» nada. Las celdas se enciman medio píxel para que no queden rendijas.
        fun celdas(mapa: Array<String>) {
            val filas = mapa.size; val cols = mapa[0].length
            val lado = 2 * r / maxOf(filas, cols)
            val x0 = x - cols * lado / 2; val y0 = y - filas * lado / 2
            for (i in 0 until filas) for (j in 0 until cols) {
                val t = mapa[i][j]
                if (t == '#' || t == '+') drawRect(if (t == '#') col else tenue, Offset(x0 + j * lado, y0 + i * lado), Size(lado + 0.5f, lado + 0.5f))
            }
        }
        when (e.forma) {
            "luna" -> celdas(LUNA_ANIO)
            "huella" -> celdas(HUELLA_ANIO)
            "pesa" -> celdas(PESA_ANIO)
            else -> {}
        }
    }

    private val LUNA_ANIO = arrayOf(
        "...#++...",
        ".###++++.",
        ".##++.++.",
        "###++++++",
        "#.#+++.++",
        "####+++++",
        ".#.##+++.",
        ".#######.",
        "...###...",
    )
    private val HUELLA_ANIO = arrayOf(
        "##.#....",
        "##...#..",
        ".......#",
        ".######.",
        "########",
        "########",
        ".######.",
        "...####.",
        "...####.",
        "..#####.",
        "..#####.",
        "...###..",
    )
    private val PESA_ANIO = arrayOf(
        ".##.....##.",
        "###.....###",
        "#+#.....#+#",
        "###########",
        "#+#.....#+#",
        "###.....###",
        ".##.....##.",
    )
}

/**
 * Vapor: el trofeo se apoya en la peana de hierro fundido de la sala de máquinas: la cornisa con sus dentellones, el cuerpo
 * de chapa remachada entre dos pilastras, dos engranes de latón a los lados de la placa del metal del nivel y el cimiento
 * de ladrillo con sus juntas. En el hall, el calderín de cobre sobre sus patas de hierro, ceñido por flejes remachados,
 * con el manómetro de aro del metal al frente. La placa es la chapa de hierro de la máquina, remachada en las esquinas,
 * con el filete del metal y la cara crema.
 */
object LogrosVapor : DisenoLogros {
    private val HIERRO = Color(0xFF2E3230)
    private val HIERRO_LUZ = Color(0xFF59605C)
    private val HIERRO_OSC = Color(0xFF181A19)
    private val LATON = Color(0xFFC9A04E)
    private val LATON_LUZ = Color(0xFFE6C77E)
    private val LATON_OSC = Color(0xFF8A6A2C)
    private val COBRE = Color(0xFFB06A3B)
    private val COBRE_LUZ = Color(0xFFD99464)
    private val LADRILLO = Color(0xFF7A3E2C)
    private val LADRILLO_JUNTA = Color(0xFF4C2A20)
    private val CARA = Color(0xFFEDE3C8)
    private val CARA_NOCHE = Color(0xFF2E2820)
    private val ROJO = Color(0xFF9A2A22)

    // salón: colores
    private val CIELO = Color(0xFFBFD3D6)
    private val CIELO_NOCHE = Color(0xFF1C2638)
    private val GAS = Color(0xFFF2C46A)
    private val TABLA = Color(0xFF8A6A48)
    private val VAPOR_BLANCO = Color(0xFFF4F1EA)

    /** El color en modo oscuro: más hundido, como en su biblioteca. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0D0B09), 0.36f) else c

    // ---------------------------------------------------------------- pedestales y placa

    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val fino = h >= 90 * u
        val hl = maxOf(0.6f * u, h * 0.004f)
        fun c(x: Color) = t(x, oscuro)
        val corn = Rect(w * 0.1f, h * 0.775f, w * 0.9f, h * 0.802f)
        val cuerpo = Rect(w * 0.15f, h * 0.802f, w * 0.85f, h * 0.902f)
        val cimiento = Rect(w * 0.09f, h * 0.902f, w * 0.91f, h * 0.954f)
        if (!e.hecho) { silueta(oscuro) { addRect(corn); addRect(cuerpo); addRect(cimiento) }; return }
        sombraPie(w / 2, cimiento.width, h * 0.958f, h * 0.022f, if (oscuro) 0.5f else 0.25f)
        // El cimiento de ladrillo, con sus hiladas a soga.
        drawRect(c(LADRILLO), cimiento.topLeft, cimiento.size)
        val hiladas = if (fino) 3 else 1
        for (f in 0 until hiladas) {
            val y0 = cimiento.top + cimiento.height * f / hiladas; val y1 = cimiento.top + cimiento.height * (f + 1) / hiladas
            if (f > 0) drawLine(c(LADRILLO_JUNTA), Offset(cimiento.left, y0), Offset(cimiento.right, y0), hl)
            val largo = maxOf(cimiento.width / (if (fino) 7f else 4f), 1f)
            var x = cimiento.left + (if (f % 2 == 1) largo / 2 else largo); var n = 0
            while (x < cimiento.right && n < 30) { drawLine(c(LADRILLO_JUNTA), Offset(x, y0), Offset(x, y1), hl); x += largo; n++ }
        }
        drawRect(c(mezcla(LADRILLO, Color.White, 0.15f)), cimiento.topLeft, Size(cimiento.width, hl))
        // El cuerpo de chapa entre sus pilastras, con la luz de arriba y los remaches.
        drawRect(c(HIERRO), cuerpo.topLeft, cuerpo.size)
        val pil = cuerpo.width * 0.08f
        listOf(cuerpo.left, cuerpo.right - pil).forEach { x0 ->
            drawRect(c(HIERRO_OSC), Offset(x0, cuerpo.top), Size(pil, cuerpo.height))
            drawLine(c(HIERRO_LUZ), Offset(x0 + hl, cuerpo.top), Offset(x0 + hl, cuerpo.bottom), hl * 0.7f)
        }
        drawRect(c(HIERRO_LUZ), Offset(cuerpo.left, cuerpo.top), Size(cuerpo.width, hl))
        if (fino) {
            val paso = maxOf(cuerpo.width * 0.075f, 1f)
            var x = cuerpo.left + pil + paso * 0.6f; var n = 0
            while (x < cuerpo.right - pil && n < 30) {
                listOf(cuerpo.top + cuerpo.height * 0.16f, cuerpo.bottom - cuerpo.height * 0.16f).forEach { y ->
                    drawCircle(c(HIERRO_OSC), hl * 1.3f, Offset(x + hl * 0.3f, y + hl * 0.3f)); drawCircle(c(HIERRO_LUZ), hl * 1.1f, Offset(x, y))
                }
                x += paso; n++
            }
        }
        // Dos engranes de latón a los lados y, al centro, la placa del metal del nivel con sus remaches.
        if (fino) listOf(0.3f, 0.7f).forEach { f ->
            val cc = Offset(w * f, cuerpo.center.y); val r = cuerpo.height * 0.3f
            drawPath(engrane(cc, r, 8), c(LATON_OSC)); drawPath(engrane(cc - Offset(hl * 0.4f, hl * 0.4f), r * 0.95f, 8), c(LATON))
            drawCircle(c(LATON_LUZ), r * 0.42f, cc); drawCircle(c(HIERRO_OSC), r * 0.18f, cc)
        }
        val pl = Rect(w * 0.4f, cuerpo.top + cuerpo.height * 0.24f, w * 0.6f, cuerpo.bottom - cuerpo.height * 0.24f)
        chapa(e, pl, pl.height * 0.1f)
        val rr = maxOf(pl.height * 0.1f, 0.6f * u)
        listOf(pl.left + rr * 2, pl.right - rr * 2.4f).forEach { x -> drawCircle(e.metalOscuro, rr, Offset(x, pl.center.y)) }
        // La cornisa de hierro, con su canto de luz y los dentellones debajo.
        drawRect(c(HIERRO), corn.topLeft, corn.size)
        drawRect(c(HIERRO_LUZ), corn.topLeft, Size(corn.width, corn.height * 0.3f))
        if (fino) {
            val paso = maxOf(corn.width * 0.045f, 1f)
            var x = cuerpo.left + pil; var n = 0
            while (x < cuerpo.right - pil - paso * 0.5f && n < 40) { drawRect(c(HIERRO_OSC), Offset(x, corn.bottom), Size(paso * 0.55f, cuerpo.height * 0.07f)); x += paso; n++ }
        }
    }

    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val m = tonos(e, oscuro)
        fun c(x: Color) = t(x, oscuro)
        sombraPie(w / 2, w * 0.76f, h * 0.95f, h * 0.16f, if (oscuro) 0.45f else 0.22f)
        // Las patas de hierro.
        listOf(0.24f, 0.76f).forEach { f ->
            drawPath(Path().apply { moveTo(w * (f - 0.05f), h * 0.6f); lineTo(w * (f + 0.05f), h * 0.6f); lineTo(w * (f + 0.08f), h); lineTo(w * (f - 0.08f), h); close() }, c(HIERRO))
        }
        // El calderín de cobre, con su brillo y los flejes de hierro remachados.
        val tambor = Rect(w * 0.08f, h * 0.04f, w * 0.92f, h * 0.8f)
        drawRoundRect(c(COBRE), tambor.topLeft, tambor.size, CornerRadius(tambor.height / 2))
        drawRoundRect(c(COBRE_LUZ), Offset(tambor.left + tambor.height * 0.3f, tambor.top + tambor.height * 0.12f), Size(tambor.width - tambor.height * 0.6f, tambor.height * 0.14f), CornerRadius(tambor.height * 0.07f))
        drawRoundRect(Color.Black.copy(alpha = 0.22f), Offset(tambor.left + tambor.height * 0.2f, tambor.bottom - tambor.height * 0.2f), Size(tambor.width - tambor.height * 0.4f, tambor.height * 0.12f), CornerRadius(tambor.height * 0.06f))
        listOf(0.22f, 0.78f).forEach { f ->
            val x = w * f
            drawRect(c(HIERRO), Offset(x - 1.6f * u, tambor.top), Size(3.2f * u, tambor.height))
            for (k in 1..3) drawCircle(c(HIERRO_LUZ), 0.7f * u, Offset(x, tambor.top + tambor.height * k / 4))
        }
        // El manómetro, con el aro del metal del nivel, y su brillo.
        val cc = Offset(w / 2, h * 0.42f); val r = h * 0.36f
        manometro(cc, r, m.base, if (oscuro) CARA_NOCHE else CARA, ROJO, 0.7f)
        drawArc(m.brillo, 200f, 70f, false, Offset(cc.x - r * 0.9f, cc.y - r * 0.9f), Size(r * 1.8f, r * 1.8f), style = Stroke(r * 0.12f))
        drawArc(m.metalOscuro, 20f, 70f, false, Offset(cc.x - r * 0.9f, cc.y - r * 0.9f), Size(r * 1.8f, r * 1.8f), style = Stroke(r * 0.12f))
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFF1E3BE) else Color(0xFF231B14)
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) LATON_LUZ else Color(0xFF6B4F1C)

    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val m = tonos(e, oscuro)
        val r0 = Rect(Offset.Zero, size)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.25f), Offset(0.5f * u, 1.6f * u), size, CornerRadius(3 * u))
        // La chapa de hierro, con su canto de luz y los cuatro remaches de las esquinas.
        drawRoundRect(t(HIERRO, oscuro), Offset.Zero, size, CornerRadius(3 * u))
        drawRoundRect(t(HIERRO_LUZ, oscuro), Offset(1.2f * u, 0.5f * u), Size(w - 2.4f * u, 0.8f * u), CornerRadius(0.4f * u))
        listOf(Offset(2.2f * u, 2.2f * u), Offset(w - 2.2f * u, 2.2f * u), Offset(2.2f * u, h - 2.2f * u), Offset(w - 2.2f * u, h - 2.2f * u)).forEach { p ->
            drawCircle(t(HIERRO_OSC, oscuro), 1.3f * u, p + Offset(0.3f * u, 0.3f * u)); drawCircle(t(HIERRO_LUZ, oscuro), 1.1f * u, p)
        }
        // El filete del metal del nivel y la cara crema (en la penumbra, la del manómetro de noche).
        filete(m, redondo(r0.deflate(4.4f * u), 1.6f * u), 1.8f * u)
        val cara = r0.deflate(5.4f * u)
        drawRoundRect(if (oscuro) CARA_NOCHE else CARA, cara.topLeft, cara.size, CornerRadius(1 * u))
    }

    // ---------------------------------------------------------------- el salón del hall

    /**
     * La nave de una exposición universal, toda de hierro y vidrio: al fondo, la pared de cristal con sus vidrios y sus
     * junquillos (de día, el cielo claro; de noche, azul, con la luna), y arriba, sobre cada franja, el gran arco de celosía
     * con sus montantes en zigzag. Entre las franjas, columnas de hierro fundido acanaladas, con su capitel de hojas de
     * latón, sus anillos y, en cada piso, la lámpara de gas en su brazo de lira (de noche, encendida). Abajo, el zócalo de
     * ladrillo con sus rejillas de arco y el tubo de cobre con sus bridas y su válvula, sobre un piso de tablones. En el borde
     * izquierdo asoma el volante de una máquina; en el derecho, el regulador de bolas sobre su columna y una nube de vapor.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) = clipRect { salon(e, oscuro) }

    /** La escena del salón, recortada al lienzo por [escenaSalon]. */
    private fun DrawScope.salon(e: EstadoSalon, oscuro: Boolean) {
        val p = planta(e); val u = p.u; val w = p.w; val h = p.h
        fun c(x: Color, f: Float) = mezcla(e.fondo, t(x, oscuro), f)
        val zocalo = h - 52 * u; val piso = h - 14 * u

        // La pared de cristal: el cielo detrás (un degradado) y la cuadrícula de junquillos, más fina al centro de cada franja.
        val cielo = if (oscuro) listOf(CIELO_NOCHE, mezcla(CIELO_NOCHE, Color.Black, 0.4f)) else listOf(CIELO, mezcla(CIELO, CARA, 0.6f))
        drawRect(Brush.verticalGradient(cielo.map { mezcla(e.fondo, it, if (oscuro) 0.6f else 0.45f) }, 0f, zocalo), Offset.Zero, Size(w, zocalo))
        if (oscuro) {
            val luna = Offset(p.lx(0) + 20 * u, 70 * u)
            halo(luna, 26 * u, Color(0xFFE8E4D0).copy(alpha = 0.025f), 4)
            drawCircle(mezcla(e.fondo, Color(0xFFE8E4D0), 0.6f), 7 * u, luna)
            for (k in 0 until 12) drawCircle(Color.White.copy(alpha = 0.35f), 0.6f * u, Offset(w * azar(e.semilla + 4, k), zocalo * azar(e.semilla + 4, k + 20)))
        }
        run {
            val barra = c(HIERRO, if (oscuro) 0.4f else 0.22f)
            var y = 30 * u; var k = 0
            while (y < zocalo && k < 80) { drawLine(barra, Offset(0f, y), Offset(w, y), 0.8f * u); y += maxOf(26 * u, 1f); k++ }
            for (i in 0 until p.n) for (j in 1..5) {
                val x = p.lx(i) + p.paso * j / 6
                drawLine(barra, Offset(x, 30 * u), Offset(x, zocalo), 0.8f * u)
            }
            // El brillo del vidrio: unas diagonales claras en algunos vidrios de los bordes.
            for (k2 in 0 until 10) {
                val x = if (k2 % 2 == 0) w * 0.02f + 20 * u * azar(e.semilla, k2) else w * 0.98f - 20 * u * azar(e.semilla, k2)
                val y0 = 40 * u + (zocalo - 80 * u) * azar(e.semilla + 1, k2)
                drawLine(Color.White.copy(alpha = if (oscuro) 0.05f else 0.25f), Offset(x, y0), Offset(x + 8 * u, y0 - 10 * u), 1.4f * u)
            }
        }

        // El arco de celosía sobre cada franja: dos cordones y los montantes en zigzag entre ellos.
        for (i in 0 until p.n) {
            val x0 = p.lx(i); val x1 = p.lx(i + 1); val cx = (x0 + x1) / 2; val r = (x1 - x0) / 2
            val arranque = 120 * u; val clave = 8 * u
            fun punto(t0: Float, dentro: Float): Offset {
                val a = PI * (1 - t0)
                return Offset(cx + ((r - dentro) * cos(a)).toFloat(), arranque - ((arranque - clave - dentro) * sin(a)).toFloat())
            }
            val exterior = Path(); val interior = Path(); val zig = Path()
            for (s in 0..24) {
                val t0 = s / 24f
                val a = punto(t0, 0f); val b = punto(t0, 9 * u)
                if (s == 0) { exterior.moveTo(a.x, a.y); interior.moveTo(b.x, b.y); zig.moveTo(a.x, a.y) } else { exterior.lineTo(a.x, a.y); interior.lineTo(b.x, b.y); if (s % 2 == 0) zig.lineTo(a.x, a.y) else zig.lineTo(b.x, b.y) }
            }
            drawPath(exterior, c(HIERRO, if (oscuro) 0.6f else 0.42f), style = Stroke(2.4f * u))
            drawPath(interior, c(HIERRO, if (oscuro) 0.55f else 0.32f), style = Stroke(1.6f * u))
            drawPath(zig, c(HIERRO, if (oscuro) 0.45f else 0.25f), style = Stroke(0.9f * u))
        }

        // Las columnas de hierro fundido entre las franjas: el fuste acanalado, los anillos, el capitel y las lámparas de gas.
        for (i in 0..p.n) {
            val x = p.lx(i); val an = 12 * u
            drawRect(c(HIERRO, if (oscuro) 0.8f else 0.7f), Offset(x - an / 2, 0f), Size(an, zocalo))
            drawRect(c(HIERRO_LUZ, 0.7f), Offset(x - an / 2 + 1 * u, 0f), Size(2 * u, zocalo))
            listOf(-2f, 1f, 3.5f).forEach { dx -> drawLine(c(HIERRO_OSC, 0.8f), Offset(x + dx * u, 130 * u), Offset(x + dx * u, zocalo - 12 * u), 0.8f * u) }
            // El capitel de hojas de latón donde arrancan los arcos.
            val cap = 112 * u
            drawPath(Path().apply { moveTo(x - an / 2, cap + 14 * u); lineTo(x - an / 2 - 6 * u, cap); lineTo(x + an / 2 + 6 * u, cap); lineTo(x + an / 2, cap + 14 * u); close() }, c(LATON, if (oscuro) 0.6f else 0.75f))
            for (j in -1..1) drawPath(Path().apply { moveTo(x + j * 4 * u, cap + 14 * u); quadraticTo(x + j * 4 * u - 3 * u, cap + 6 * u, x + j * 6 * u, cap + 1 * u) }, c(LATON_OSC, 0.8f), style = Stroke(1 * u))
            drawRect(c(LATON_LUZ, 0.75f), Offset(x - an / 2 - 7 * u, cap - 4 * u), Size(an + 14 * u, 4 * u))
            // La basa de latón al pie.
            drawRect(c(LATON, 0.7f), Offset(x - an / 2 - 4 * u, zocalo - 10 * u), Size(an + 8 * u, 10 * u))
            drawLine(c(LATON_LUZ, 0.8f), Offset(x - an / 2 - 4 * u, zocalo - 10 * u), Offset(x + an / 2 + 4 * u, zocalo - 10 * u), 1 * u)
            for (k in 0 until p.filas) {
                val y = p.y(k) + 40 * u
                if (y < cap + 30 * u || y > zocalo - 40 * u) continue
                // El anillo del fuste y el brazo de lira con la lámpara de gas, hacia adentro del salón.
                drawRect(c(LATON, 0.65f), Offset(x - an / 2 - 1 * u, y - 2 * u), Size(an + 2 * u, 4 * u))
                listOf(-1f, 1f).forEach { s ->
                    if ((i == 0 && s < 0) || (i == p.n && s > 0)) return@forEach
                    val lx = x + s * 16 * u
                    drawPath(Path().apply { moveTo(x + s * an / 2, y); cubicTo(x + s * 10 * u, y - 6 * u, x + s * 14 * u, y + 2 * u, lx, y - 6 * u) }, c(HIERRO, 0.75f), style = Stroke(1.4f * u))
                    drawCircle(c(HIERRO, 0.7f), 1.8f * u, Offset(x + s * 9 * u, y + 2 * u), style = Stroke(0.8f * u))
                    val globo = Offset(lx, y - 12 * u)
                    if (oscuro) halo(globo, 32 * u, GAS.copy(alpha = 0.035f), 6)
                    drawCircle(if (oscuro) mezcla(e.fondo, GAS, 0.85f) else c(Color(0xFFF2EEE4), 0.6f), 4.5f * u, globo)
                    drawCircle(c(LATON_OSC, 0.6f), 4.5f * u, globo, style = Stroke(0.7f * u))
                    drawRect(c(LATON, 0.8f), Offset(lx - 2.5f * u, y - 7.5f * u), Size(5 * u, 2 * u))
                    drawRect(c(HIERRO, 0.8f), Offset(lx - 1.5f * u, y - 18 * u), Size(3 * u, 2 * u))
                }
            }
        }

        // El zócalo de ladrillo con sus hiladas, las rejillas de arco bajo cada franja y el tubo de cobre con su válvula.
        drawRect(c(LADRILLO, if (oscuro) 0.5f else 0.45f), Offset(0f, zocalo), Size(w, piso - zocalo))
        run {
            var y = zocalo + 5 * u; var f = 0
            while (y < piso && f < 12) {
                drawLine(c(LADRILLO_JUNTA, 0.6f), Offset(0f, y), Offset(w, y), 0.8f * u)
                var x = if (f % 2 == 0) 0f else 7 * u; var k = 0
                while (x < w && k < 60) { drawLine(c(LADRILLO_JUNTA, 0.6f), Offset(x, y - 5 * u), Offset(x, y), 0.8f * u); x += maxOf(14 * u, 1f); k++ }
                y += maxOf(5 * u, 1f); f++
            }
        }
        drawRect(c(HIERRO, 0.6f), Offset(0f, zocalo), Size(w, 3 * u))
        for (i in 0 until p.n) {
            val r = Rect(p.cx(i) - 14 * u, zocalo + 9 * u, p.cx(i) + 14 * u, zocalo + 28 * u)
            drawPath(arcoMedioPunto(r), c(HIERRO_OSC, 0.7f))
            for (j in 1..5) drawLine(c(HIERRO_LUZ, 0.5f), Offset(r.left + r.width * j / 6, r.top + 6 * u), Offset(r.left + r.width * j / 6, r.bottom), 1 * u)
            drawPath(arcoMedioPunto(r), c(LATON, 0.5f), style = Stroke(1 * u))
        }
        val ty = piso - 8 * u
        drawLine(c(COBRE, 0.75f), Offset(0f, ty), Offset(w, ty), 5 * u)
        drawLine(c(COBRE_LUZ, 0.75f), Offset(0f, ty - 1.4f * u), Offset(w, ty - 1.4f * u), 1.2f * u)
        for (i in 0..p.n) {
            val x = p.lx(i)
            listOf(-12f, 12f).forEach { dx -> drawRect(c(LATON_OSC, 0.8f), Offset(x + dx * u - 1.5f * u, ty - 4.5f * u), Size(3 * u, 9 * u)) }
            if (i in 1 until p.n) {
                drawLine(c(COBRE, 0.75f), Offset(x, ty), Offset(x, ty - 10 * u), 3 * u)
                drawCircle(c(ROJO, if (oscuro) 0.6f else 0.75f), 5 * u, Offset(x, ty - 12 * u), style = Stroke(1.6f * u))
                drawLine(c(ROJO, 0.75f), Offset(x - 5 * u, ty - 12 * u), Offset(x + 5 * u, ty - 12 * u), 1 * u)
                drawLine(c(ROJO, 0.75f), Offset(x, ty - 17 * u), Offset(x, ty - 7 * u), 1 * u)
            }
        }
        // El piso de tablones con sus juntas y clavos.
        drawRect(c(TABLA, if (oscuro) 0.45f else 0.5f), Offset(0f, piso), Size(w, h - piso))
        drawLine(c(HIERRO_OSC, 0.6f), Offset(0f, piso), Offset(w, piso), 1 * u)
        run { var x = 0f; var k = 0; while (x < w && k < 40) { drawLine(c(HIERRO_OSC, 0.4f), Offset(x, piso), Offset(x, h), 0.8f * u); drawCircle(c(HIERRO_OSC, 0.5f), 0.7f * u, Offset(x + 3 * u, piso + 4 * u)); x += maxOf(36 * u + 8 * u * azar(e.semilla, k), 1f); k++ } }

        // El volante de una máquina, que asoma en el borde izquierdo, con sus rayos y su llanta.
        run {
            val cc = Offset(-4 * u, zocalo - 40 * u); val r = 34 * u
            drawCircle(c(HIERRO, if (oscuro) 0.65f else 0.55f), r, cc, style = Stroke(6 * u))
            drawCircle(c(HIERRO_LUZ, 0.5f), r - 3.5f * u, cc, style = Stroke(1 * u))
            for (k in 0 until 6) { val a = k * PI / 3 + 0.3; drawLine(c(HIERRO, 0.6f), cc, Offset(cc.x + (r * cos(a)).toFloat(), cc.y + (r * sin(a)).toFloat()), 2.6f * u) }
            drawCircle(c(LATON, 0.7f), 6 * u, cc)
        }
        // El regulador de bolas en el borde derecho, sobre su columna, y la nube de vapor que se escapa.
        run {
            val x = w - 14 * u; val y0 = zocalo - 62 * u
            drawRect(c(HIERRO, 0.65f), Offset(x - 4 * u, y0 + 22 * u), Size(8 * u, zocalo - y0 - 22 * u))
            drawRect(c(LATON, 0.7f), Offset(x - 6 * u, y0 + 20 * u), Size(12 * u, 3 * u))
            drawLine(c(LATON_OSC, 0.8f), Offset(x, y0), Offset(x, y0 + 22 * u), 1.4f * u)
            listOf(-1f, 1f).forEach { s ->
                val bola = Offset(x + s * 10 * u, y0 + 12 * u)
                drawLine(c(LATON_OSC, 0.8f), Offset(x, y0 + 2 * u), bola, 1.2f * u)
                drawLine(c(LATON_OSC, 0.7f), Offset(x, y0 + 16 * u), Offset(x + s * 5 * u, y0 + 7 * u), 0.9f * u)
                drawCircle(c(LATON, 0.8f), 3.6f * u, bola)
                drawCircle(c(LATON_LUZ, 0.9f), 1.2f * u, bola - Offset(1 * u, 1 * u))
            }
            drawCircle(c(LATON, 0.8f), 2 * u, Offset(x, y0))
            for (k in 0 until 6) drawCircle(VAPOR_BLANCO.copy(alpha = if (oscuro) 0.06f else 0.22f), (6 + 3 * k) * u, Offset(x - 8 * u - k * 3 * u, zocalo - 18 * u - k * 9 * u))
        }
    }

    // ---------------------------------------------------------------- las campañas

    /**
     * La locomotora que recorre la línea, de lado y hacia la derecha, sobre su tramo de riel: la caldera de cobre con sus
     * fajas de latón, el domo y el silbato, la caja de humo de hierro con su chimenea de copa, el farol de gas al frente, el
     * cilindro, el quitapiedras rojo, la cabina con su ventana encendida y su techo volado, la rueda delantera, las dos
     * motrices con su biela y la de atrás. El filete del color de acento corre por el bastidor y el penacho de vapor se va
     * hacia atrás. De noche, el farol echa su haz y su halo, y la ventana de la cabina brilla.
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density; val p = e.punto
        sombraMarcadorContemporanea(p, 30 * u, e.salto, oscuro)
        val o = Offset(p.x, p.y - subidaContemporanea(e.salto))
        fun x(v: Float) = o.x + v * u
        fun y(v: Float) = o.y + v * u
        val hierro = t(HIERRO, oscuro); val hierroLuz = t(HIERRO_LUZ, oscuro); val laton = t(LATON, oscuro); val latonLuz = t(LATON_LUZ, oscuro)
        val cobre = t(COBRE, oscuro); val cobreLuz = t(COBRE_LUZ, oscuro); val rojo = t(ROJO, oscuro)
        val gas = if (oscuro) Color(0xFFFFE2A0) else GAS
        // El haz del farol, de noche.
        if (oscuro) drawPath(trazoContemporanea(o, u, true, 14.6f, -17.2f, 30f, -20f, 30f, -6f, 14.6f, -14.6f), gas.copy(alpha = 0.12f))
        // El tramo de riel con sus durmientes (quieto: no brinca).
        for (k in 0 until 6) drawRect(t(TABLA, oscuro).copy(alpha = 0.8f), Offset(p.x + (-14f + k * 6f) * u, p.y - 0.6f * u), Size(3.6f * u, 1.4f * u))
        drawLine(t(HIERRO_LUZ, oscuro), Offset(p.x - 16 * u, p.y - 0.6f * u), Offset(p.x + 19 * u, p.y - 0.6f * u), 0.9f * u)
        // El penacho de vapor que se va hacia atrás.
        listOf(Triple(9.6f, -24f, 2.1f), Triple(6f, -27f, 2.7f), Triple(1.2f, -28.6f, 3.1f), Triple(-4.4f, -28.4f, 2.5f)).forEachIndexed { i, (px, py, rr) ->
            drawCircle(VAPOR_BLANCO.copy(alpha = (if (oscuro) 0.6f else 0.85f) - i * 0.12f), rr * u, Offset(x(px), y(py)))
        }
        // La cabina: el cuerpo, la ventana encendida y el techo volado.
        drawRect(hierroLuz, Offset(x(-13f), y(-18.6f)), Size(8.4f * u, 12 * u))
        drawRect(laton, Offset(x(-13f), y(-18.6f)), Size(8.4f * u, 12 * u), style = Stroke(0.5f * u))
        if (oscuro) halo(Offset(x(-9f), y(-14.6f)), 4 * u, gas.copy(alpha = 0.12f), 3)
        drawRoundRect(gas.copy(alpha = if (oscuro) 1f else 0.85f), Offset(x(-11.4f), y(-17f)), Size(4.6f * u, 4 * u), CornerRadius(1.4f * u))
        drawRect(hierro, Offset(x(-14.2f), y(-20f)), Size(11 * u, 1.5f * u))
        drawLine(e.acento, Offset(x(-12.6f), y(-8.2f)), Offset(x(-5f), y(-8.2f)), 0.6f * u)
        // La caldera de cobre, sus fajas, el domo, el silbato y la caja de humo con su chimenea.
        drawRoundRect(cobre, Offset(x(-5f), y(-14.4f)), Size(17 * u, 7.6f * u), CornerRadius(3.6f * u))
        drawLine(cobreLuz, Offset(x(-4f), y(-13.2f)), Offset(x(11f), y(-13.2f)), 0.8f * u)
        listOf(-1.4f, 3.6f, 8.4f).forEach { bx -> drawRect(laton, Offset(x(bx), y(-14.4f)), Size(0.9f * u, 7.6f * u)) }
        drawArc(laton, 180f, 180f, true, Offset(x(1.4f), y(-17.2f)), Size(4.4f * u, 5.4f * u))
        drawCircle(latonLuz, 0.6f * u, Offset(x(2.6f), y(-16.2f)))
        drawLine(laton, Offset(x(-2.6f), y(-14.2f)), Offset(x(-2.6f), y(-16.6f)), 0.8f * u)
        drawRect(hierro, Offset(x(11.4f), y(-14.8f)), Size(2.8f * u, 8.4f * u))
        drawPath(trazoContemporanea(o, u, true, 9.4f, -14.4f, 11.8f, -14.4f, 12.6f, -21.2f, 8.6f, -21.2f), hierro)
        drawRect(cobreLuz, Offset(x(8.2f), y(-22.4f)), Size(4.8f * u, 1.3f * u))
        // El farol de gas al frente.
        if (oscuro) halo(Offset(x(14.6f), y(-16f)), 6 * u, gas.copy(alpha = 0.1f), 4)
        drawRect(hierro, Offset(x(12.4f), y(-17.6f)), Size(2.6f * u, 2.6f * u))
        drawRect(gas, Offset(x(14.2f), y(-17.1f)), Size(1.1f * u, 1.6f * u))
        // El bastidor, el filete de acento, el cilindro, la viga roja y el quitapiedras.
        drawRect(hierro, Offset(x(-14f), y(-7.2f)), Size(28.4f * u, 1.4f * u))
        drawLine(e.acento, Offset(x(-4f), y(-6.5f)), Offset(x(13f), y(-6.5f)), 0.5f * u)
        drawRoundRect(hierroLuz, Offset(x(8.4f), y(-6.2f)), Size(4.4f * u, 3 * u), CornerRadius(0.8f * u))
        drawRect(rojo, Offset(x(14f), y(-8.2f)), Size(1.6f * u, 3.2f * u))
        drawPath(trazoContemporanea(o, u, true, 13.6f, -5f, 15.6f, -5f, 18.4f, -0.8f, 13.6f, -0.8f), rojo)
        for (k in 0 until 3) drawLine(mezcla(rojo, Color.Black, 0.35f), Offset(x(14.4f + k * 0.9f), y(-4.6f)), Offset(x(14.4f + k * 1.4f), y(-1f)), 0.4f * u)
        // Las ruedas: la delantera, las dos motrices con su biela y la de atrás.
        fun rueda(cx: Float, r: Float) {
            val c = Offset(x(cx), y(-0.6f - r))
            drawCircle(hierro, r * u, c)
            drawCircle(rojo, r * 0.72f * u, c)
            for (k in 0 until 4) { val a = k * PI.toFloat() / 4; drawLine(hierro, Offset(c.x - cos(a) * r * 0.7f * u, c.y - sin(a) * r * 0.7f * u), Offset(c.x + cos(a) * r * 0.7f * u, c.y + sin(a) * r * 0.7f * u), 0.4f * u) }
            drawCircle(laton, r * 0.24f * u, c)
        }
        rueda(-9f, 2.2f); rueda(-1.6f, 3.6f); rueda(5.4f, 3.6f); rueda(11f, 2.1f)
        drawLine(latonLuz, Offset(x(-0.4f), y(-3.4f)), Offset(x(6.6f), y(-3.4f)), 0.9f * u, StrokeCap.Round)
        drawLine(t(Color(0xFFB9BEC3), oscuro), Offset(x(6.6f), y(-3.4f)), Offset(x(9.6f), y(-4.6f)), 0.7f * u)
    }

    /**
     * La llegada a la terminal: la fachada de ladrillo de la estación con su gran arco de hierro y vidrio (el abanico de
     * sus montantes sobre el travesaño), y dentro, de frente, la locomotora que entra con su puerta de humo redonda, el farol
     * encendido, la viga roja y los topes, envuelta en su vapor; arriba, la torre del reloj con su tejado y el banderín del
     * color de acento, la guirnalda de banderitas de la fiesta y, a los lados, los faroles de gas. De noche, el vidrio del arco
     * se enciende de luz de gas y los faroles brillan.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val b = pieVictoriaContemporanea(e.meta, 60 * u, 56 * u)
        fun x(v: Float) = b.x + v * u
        fun y(v: Float) = b.y + v * u
        val ladrillo = t(LADRILLO, oscuro); val junta = t(LADRILLO_JUNTA, oscuro); val hierro = t(HIERRO, oscuro); val laton = t(LATON, oscuro)
        val piedra = t(CARA, oscuro); val gas = if (oscuro) Color(0xFFFFE2A0) else GAS
        // La torre del reloj, detrás de la fachada.
        drawRect(ladrillo, Offset(x(-5.5f), y(-44f)), Size(11 * u, 16 * u))
        drawPath(trazoContemporanea(b, u, true, -7f, -44f, 7f, -44f, 0f, -50f), hierro)
        drawLine(hierro, Offset(x(0f), y(-50f)), Offset(x(0f), y(-55f)), 0.7f * u)
        drawPath(trazoContemporanea(b, u, true, 0f, -55f, 6f, -53.6f, 0f, -52.2f), e.acento)
        val reloj = Offset(x(0f), y(-38f))
        if (oscuro) halo(reloj, 6 * u, gas.copy(alpha = 0.1f), 3)
        drawCircle(laton, 4 * u, reloj); drawCircle(if (oscuro) Color(0xFFF4E2B0) else piedra, 3.3f * u, reloj)
        for (k in 0 until 12) { val a = (k * PI / 6).toFloat(); drawCircle(hierro, 0.3f * u, Offset(reloj.x + 2.7f * u * cos(a), reloj.y + 2.7f * u * sin(a))) }
        drawLine(hierro, reloj, reloj + Offset(0f, -2.4f * u), 0.6f * u); drawLine(hierro, reloj, reloj + Offset(1.8f * u, 0.4f * u), 0.6f * u)
        // La fachada de ladrillo con sus hiladas y la cornisa de piedra.
        drawRect(ladrillo, Offset(x(-24f), y(-30f)), Size(48 * u, 29 * u))
        for (k in 1 until 10) drawLine(junta.copy(alpha = 0.6f), Offset(x(-24f), y(-30f + k * 2.9f)), Offset(x(24f), y(-30f + k * 2.9f)), 0.35f * u)
        drawRect(piedra, Offset(x(-25.5f), y(-31.6f)), Size(51 * u, 2 * u))
        listOf(-21f, 19f).forEach { px -> drawRect(piedra, Offset(x(px), y(-29.6f)), Size(2 * u, 28.6f * u)) }
        // El arco: el vidrio, los montantes en abanico, el travesaño y, abajo, el andén en sombra.
        val arco = Path().apply { moveTo(x(-15f), y(-1f)); lineTo(x(-15f), y(-13f)); arcTo(Rect(x(-15f), y(-28f), x(15f), y(2f)), 180f, 180f, false); lineTo(x(15f), y(-1f)); close() }
        drawPath(arco, if (oscuro) Color(0xFF8A6A3A) else t(CIELO, false))
        if (oscuro) halo(Offset(x(0f), y(-16f)), 15 * u, gas.copy(alpha = 0.09f), 4)
        drawRect(t(HIERRO_OSC, oscuro).copy(alpha = 0.85f), Offset(x(-15f), y(-13f)), Size(30 * u, 12 * u))
        for (k in 0..8) { val a = (PI + k * PI / 8).toFloat(); drawLine(hierro, Offset(x(0f), y(-13f)), Offset(x(0f) + 15 * u * cos(a), y(-13f) + 15 * u * sin(a)), 0.6f * u) }
        drawArc(hierro, 180f, 180f, false, Offset(x(-8f), y(-21f)), Size(16 * u, 16 * u), style = Stroke(0.6f * u))
        drawPath(arco, hierro, style = Stroke(1.6f * u))
        drawLine(hierro, Offset(x(-15f), y(-13f)), Offset(x(15f), y(-13f)), 1 * u)
        drawPath(arco, piedra, style = Stroke(0.6f * u))
        // La locomotora de frente: el vapor, la chimenea, la puerta de humo, el farol, la viga y los topes.
        listOf(Triple(-5f, -17f, 3.4f), Triple(4.6f, -18f, 3.8f), Triple(0f, -21f, 3.6f), Triple(-9f, -14.6f, 2.6f), Triple(9.6f, -15f, 2.4f)).forEach { (px, py, rr) ->
            drawCircle(VAPOR_BLANCO.copy(alpha = if (oscuro) 0.55f else 0.8f), rr * u, Offset(x(px), y(py)))
        }
        drawRect(t(HIERRO_OSC, oscuro), Offset(x(-1.4f), y(-16f)), Size(2.8f * u, 4 * u))
        drawRect(hierro, Offset(x(-7f), y(-11f)), Size(14 * u, 8 * u))
        drawCircle(t(HIERRO_LUZ, oscuro), 4.4f * u, Offset(x(0f), y(-7.6f)))
        drawCircle(hierro, 4.4f * u, Offset(x(0f), y(-7.6f)), style = Stroke(0.6f * u))
        drawCircle(laton, 0.7f * u, Offset(x(0f), y(-7.6f)))
        halo(Offset(x(0f), y(-12.6f)), 4 * u, gas.copy(alpha = if (oscuro) 0.2f else 0.12f), 3)
        drawCircle(gas, 1.2f * u, Offset(x(0f), y(-12.6f)))
        drawRect(t(ROJO, oscuro), Offset(x(-8f), y(-3.4f)), Size(16 * u, 2.2f * u))
        listOf(-6f, 6f).forEach { px -> drawCircle(t(Color(0xFFB9BEC3), oscuro), 1 * u, Offset(x(px), y(-2.3f))) }
        // La guirnalda de banderitas.
        for (k in 0 until 12) {
            val f = k / 11f; val px = -23f + 46f * f; val py = -30f + 3.4f * (1f - (2 * f - 1) * (2 * f - 1))
            val col = listOf(t(ROJO, oscuro), laton, e.acento, piedra)[k % 4]
            drawPath(trazoContemporanea(b, u, true, px - 1.4f, py, px + 1.4f, py, px, py + 2.6f), col)
        }
        drawPath(Path().apply { moveTo(x(-24f), y(-30f)); quadraticTo(x(0f), y(-23.2f), x(24f), y(-30f)) }, hierro, style = Stroke(0.4f * u))
        // Los faroles de gas y el andén.
        listOf(-26f, 26f).forEach { fx ->
            drawLine(hierro, Offset(x(fx), y(0f)), Offset(x(fx), y(-14f)), 0.9f * u)
            if (oscuro) halo(Offset(x(fx), y(-16f)), 7 * u, gas.copy(alpha = 0.1f), 4)
            drawPath(trazoContemporanea(b, u, true, fx - 1.6f, -14f, fx + 1.6f, -14f, fx + 2.2f, -18f, fx - 2.2f, -18f), gas.copy(alpha = if (oscuro) 1f else 0.7f))
            drawPath(trazoContemporanea(b, u, true, fx - 2.6f, -18f, fx + 2.6f, -18f, fx, -20f), hierro)
        }
        drawLine(t(HIERRO_LUZ, oscuro), Offset(x(-28f), y(0f)), Offset(x(28f), y(0f)), 1.2f * u)
    }

    // ---------------------------------------------------------------- objetos de Tu año

    /**
     * Vapor: la luna llena tras el humo de la chimenea de la fábrica, el botín abotonado con su tacón y el halterio de
     * bolas de los forzudos de feria, con el brillo en cada bola.
     */
    override fun DrawScope.objetoAnio(e: EstadoObjeto, oscuro: Boolean) {
        val r = e.radio; val x = e.centro.x; val y = e.centro.y; val u = density
        val col = e.color; val fondo = e.fondo
        fun p(a: Float, b: Float) = Offset(x + a * r, y + b * r)
        fun g(f: Float) = maxOf(r * f, 0.8f * u)
        when (e.forma) {
            "luna" -> {
                drawCircle(col, r * 0.6f, p(0.24f, -0.26f))
                // La chimenea con su remate.
                drawRect(col, p(-0.8f, -0.04f), Size(r * 0.34f, r * 1.0f))
                drawRect(col, p(-0.88f, -0.18f), Size(r * 0.5f, r * 0.17f))
                // El humo: bocanadas en un tono del color, recortadas con el fondo para que se separen de la luna.
                val humo = mezcla(col, fondo, 0.55f)
                val bocanadas = listOf(Triple(-0.6f, -0.36f, 0.16f), Triple(-0.38f, -0.5f, 0.2f), Triple(-0.1f, -0.58f, 0.23f), Triple(0.22f, -0.66f, 0.22f), Triple(0.5f, -0.74f, 0.17f))
                bocanadas.forEach { (a, b, rr) -> drawCircle(fondo, r * rr + g(0.07f), p(a, b)) }
                bocanadas.forEach { (a, b, rr) -> drawCircle(humo, r * rr, p(a, b)) }
            }
            "huella" -> {
                drawPath(trazoAnioContemporanea(e.centro, r) {
                    m(-0.6f, -0.9f); l(-0.08f, -0.9f); l(-0.02f, -0.12f); q(0.12f, 0.08f, 0.55f, 0.18f); q(0.96f, 0.28f, 0.93f, 0.6f)
                    l(0.93f, 0.66f); l(-0.12f, 0.66f); l(-0.2f, 0.56f); l(-0.3f, 0.56f); l(-0.3f, 0.9f); l(-0.62f, 0.9f); l(-0.64f, 0.3f)
                    q(-0.67f, -0.3f, -0.6f, -0.9f); z()
                }, col)
                // Los botones del costado y, más grande, la costura de la suela.
                listOf(-0.68f, -0.44f, -0.2f).forEach { b -> drawCircle(fondo, maxOf(r * 0.065f, 0.5f * u), p(-0.2f, b)) }
                if (r > 6 * u) drawLine(fondo, p(0.04f, 0.53f), p(0.9f, 0.53f), g(0.05f))
            }
            "pesa" -> {
                val b = g(0.11f)
                drawRect(col, Offset(x - r * 0.5f, y - b), Size(r, 2 * b))
                for (s in listOf(-1f, 1f)) {
                    drawCircle(col, r * 0.42f, p(s * 0.56f, 0f))
                    drawCircle(fondo, maxOf(r * 0.1f, 0.55f * u), p(s * 0.56f - 0.15f, -0.15f))
                }
                if (r > 8 * u) listOf(-0.06f, 0.06f).forEach { a -> drawLine(fondo, p(a, -0.08f), p(a, 0.08f), g(0.04f)) }
            }
            else -> {}
        }
    }
}

/**
 * Napoleón: el trofeo se apoya en un plinto imperio de mármol verde veteado, con la cornisa y el escalón de caoba, los
 * filetes de bronce dorado, las rosetas de las esquinas y, al frente, la corona de laurel del metal del nivel con la
 * estrella al centro. En el hall, la consola de caoba sobre sus patas de estípite, con la abeja del metal en el faldón.
 * La placa es la cartela de pergamino de esquinas entrantes, en su marco de caoba con el filete del metal.
 */
object LogrosNapoleon : DisenoLogros {
    private val CAOBA = Color(0xFF6E2F1E)
    private val CAOBA_LUZ = Color(0xFF8D4430)
    private val CAOBA_VETA = Color(0xFF4A1E12)
    private val ORO = Color(0xFFD9B54A)
    private val ORO_LUZ = Color(0xFFF0D688)
    private val MARMOL_VERDE = Color(0xFF2F5D4A)
    private val MARMOL_VETA = Color(0xFF9CC4AE)
    private val PERGAMINO = Color(0xFFEDE2C4)
    private val TINTA = Color(0xFF2A1C14)
    private val AZUL_IMPERIAL = Color(0xFF1E3A8A)

    // salón: colores
    private val SEDA = Color(0xFF3F6B55)
    private val SEDA_LUZ = Color(0xFF6E9A7E)
    private val CARMESI = Color(0xFFA3172E)
    private val CARMESI_OSC = Color(0xFF6E0E1E)
    private val ROBLE = Color(0xFFB08A5A)
    private val ROBLE_OSC = Color(0xFF7E5E3A)
    private val LLAMA = Color(0xFFFFC870)
    private val CERA = Color(0xFFF4ECDA)

    /** El color en modo oscuro: más hundido, como en su biblioteca. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0E0B08), 0.34f) else c

    /** Una hoja de laurel: un óvalo girado. */
    private fun DrawScope.hoja(c: Offset, largo: Float, ancho: Float, grados: Float, color: Color) =
        rotate(grados, c) { drawOval(color, Offset(c.x - largo / 2, c.y - ancho / 2), Size(largo, ancho)) }

    // ---------------------------------------------------------------- pedestales y placa

    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val fino = h >= 90 * u
        val hl = maxOf(0.6f * u, h * 0.004f)
        fun c(x: Color) = t(x, oscuro)
        val corn = Rect(w * 0.11f, h * 0.775f, w * 0.89f, h * 0.802f)
        val cuerpo = Rect(w * 0.15f, h * 0.802f, w * 0.85f, h * 0.922f)
        val escalon = Rect(w * 0.08f, h * 0.922f, w * 0.92f, h * 0.954f)
        if (!e.hecho) { silueta(oscuro) { addRect(corn); addRect(cuerpo); addRect(escalon) }; return }
        sombraPie(w / 2, escalon.width, h * 0.958f, h * 0.022f, if (oscuro) 0.5f else 0.25f)
        // El escalón de caoba.
        drawRect(c(CAOBA), escalon.topLeft, escalon.size)
        drawRect(c(CAOBA_LUZ), escalon.topLeft, Size(escalon.width, escalon.height * 0.25f))
        if (fino) vetasEn(escalon, c(CAOBA_VETA).copy(alpha = 0.5f), e.semilla, densidad = 0.7f, grosor = 0.6f)
        // El cuerpo de mármol verde, con sus vetas claras.
        drawRect(c(MARMOL_VERDE), cuerpo.topLeft, cuerpo.size)
        if (fino) vetasEn(cuerpo, c(MARMOL_VETA).copy(alpha = 0.35f), e.semilla + 7, densidad = 0.35f, grosor = 0.7f, ondulacion = 2.4f)
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(cuerpo.right - cuerpo.width * 0.08f, cuerpo.top), Size(cuerpo.width * 0.08f, cuerpo.height))
        // Los filetes dorados arriba y abajo, y las rosetas de las esquinas.
        val oro = c(ORO)
        drawRect(oro, Offset(cuerpo.left, cuerpo.top), Size(cuerpo.width, hl * 1.2f))
        drawRect(oro, Offset(cuerpo.left, cuerpo.bottom - hl * 1.2f), Size(cuerpo.width, hl * 1.2f))
        if (fino) {
            drawRect(oro, Offset(cuerpo.left + cuerpo.width * 0.04f, cuerpo.top + cuerpo.height * 0.14f), Size(cuerpo.width * 0.92f, cuerpo.height * 0.72f), style = Stroke(hl * 0.6f))
            listOf(0.1f, 0.9f).forEach { f ->
                val rc = Offset(cuerpo.left + cuerpo.width * f, cuerpo.center.y); val rr = cuerpo.height * 0.13f
                for (k in 0 until 6) { val a = k * Math.PI / 3; drawCircle(oro, rr * 0.42f, Offset(rc.x + rr * 0.6f * cos(a).toFloat(), rc.y + rr * 0.6f * sin(a).toFloat())) }
                drawCircle(c(ORO_LUZ), rr * 0.45f, rc)
            }
        }
        // La corona de laurel del metal del nivel, con su estrella (o su piedra, en legendario).
        val cc = Offset(w / 2, cuerpo.center.y); val R = cuerpo.height * 0.34f
        if (fino) {
            for (s in listOf(-1f, 1f)) for (k in 0 until 6) {
                val a = Math.toRadians(90.0 + s * (28.0 + k * 25.0))
                val p = Offset(cc.x + R * cos(a).toFloat(), cc.y + R * sin(a).toFloat())
                val g = Math.toDegrees(a).toFloat() + 90f + s * 18f
                hoja(p, R * 0.56f, R * 0.25f, g, e.metalOscuro)
                hoja(p - Offset(R * 0.03f, R * 0.03f), R * 0.48f, R * 0.18f, g, if (k % 2 == 0) e.base else e.brillo)
            }
            drawCircle(e.metalOscuro, R * 0.1f, Offset(cc.x, cc.y + R))
        } else drawCircle(e.base, R, cc, style = Stroke(R * 0.32f))
        if (e.nivel == 4) boton(e, cc, R * 0.42f) else drawPath(estrella(cc, R * 0.45f, 5, 0.42f), e.brillo)
        // La cornisa de caoba con su moldura dorada.
        drawRect(c(CAOBA), corn.topLeft, corn.size)
        drawRect(c(CAOBA_LUZ), corn.topLeft, Size(corn.width, corn.height * 0.3f))
        drawRect(oro, Offset(corn.left, corn.bottom - hl), Size(corn.width, hl))
    }

    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val m = tonos(e, oscuro)
        fun c(x: Color) = t(x, oscuro)
        sombraPie(w / 2, w * 0.8f, h * 0.95f, h * 0.14f, if (oscuro) 0.45f else 0.2f)
        // Las patas de estípite, que se afinan al bajar, con sus zapatas doradas, y el travesaño.
        listOf(0.17f, 0.83f).forEach { f ->
            drawPath(Path().apply { moveTo(w * (f - 0.05f), h * 0.46f); lineTo(w * (f + 0.05f), h * 0.46f); lineTo(w * (f + 0.025f), h * 0.9f); lineTo(w * (f - 0.025f), h * 0.9f); close() }, c(CAOBA_VETA))
            drawRect(c(ORO), Offset(w * (f - 0.035f), h * 0.88f), Size(w * 0.07f, h * 0.12f))
        }
        drawRect(c(CAOBA), Offset(w * 0.2f, h * 0.72f), Size(w * 0.6f, h * 0.07f))
        // El faldón de caoba veteada con su marco dorado y, al centro, la abeja del metal del nivel.
        val faldon = Rect(w * 0.09f, h * 0.17f, w * 0.91f, h * 0.48f)
        drawRect(c(CAOBA), faldon.topLeft, faldon.size)
        vetasEn(faldon, c(CAOBA_VETA).copy(alpha = 0.6f), e.semilla, densidad = 0.8f, grosor = 0.6f)
        drawRect(c(ORO), faldon.topLeft + Offset(1.5f * u, 1.2f * u), Size(faldon.width - 3 * u, faldon.height - 2.4f * u), style = Stroke(0.7f * u))
        val b = Offset(w / 2, faldon.center.y); val r = faldon.height * 0.3f
        listOf(-1f, 1f).forEach { s -> drawOval(m.brillo.copy(alpha = 0.9f), Offset(b.x + s * r * 0.2f - r * 0.55f + s * r * 0.4f, b.y - r * 0.95f), Size(r * 1.1f, r * 0.8f)) }
        drawOval(m.metalOscuro, Offset(b.x - r * 0.42f, b.y - r * 0.8f), Size(r * 0.84f, r * 1.75f))
        drawOval(m.base, Offset(b.x - r * 0.32f, b.y - r * 0.72f), Size(r * 0.64f, r * 1.55f))
        for (k in 0 until 2) drawLine(m.metalOscuro, Offset(b.x - r * 0.32f, b.y + r * (0.05f + k * 0.35f)), Offset(b.x + r * 0.32f, b.y + r * (0.05f + k * 0.35f)), 0.6f * u)
        drawCircle(m.metalOscuro, r * 0.3f, Offset(b.x, b.y - r * 0.85f))
        // El tablero, con su canto de luz y la moldura dorada.
        val tablero = Rect(w * 0.05f, 0f, w * 0.95f, h * 0.18f)
        drawRect(c(CAOBA_LUZ), tablero.topLeft, tablero.size)
        drawRect(c(mezcla(CAOBA_LUZ, Color.White, 0.18f)), tablero.topLeft, Size(tablero.width, tablero.height * 0.3f))
        drawRect(c(ORO), Offset(tablero.left, tablero.bottom - 0.8f * u), Size(tablero.width, 0.8f * u))
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFF2E8CC) else TINTA
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) ORO else AZUL_IMPERIAL

    /** La cartela de esquinas entrantes (cuartos de círculo hacia dentro) en [r]. */
    private fun cartela(r: Rect, e: Float): Path = Path().apply {
        moveTo(r.left + e, r.top); lineTo(r.right - e, r.top); quadraticTo(r.right - e, r.top + e, r.right, r.top + e)
        lineTo(r.right, r.bottom - e); quadraticTo(r.right - e, r.bottom - e, r.right - e, r.bottom)
        lineTo(r.left + e, r.bottom); quadraticTo(r.left + e, r.bottom - e, r.left, r.bottom - e)
        lineTo(r.left, r.top + e); quadraticTo(r.left + e, r.top + e, r.left + e, r.top); close()
    }

    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val u = density
        val m = tonos(e, oscuro)
        val r0 = Rect(Offset.Zero, size)
        drawPath(cartela(r0.translate(0.5f * u, 1.6f * u), 5 * u), Color.Black.copy(alpha = if (oscuro) 0.45f else 0.22f))
        // El marco de caoba, el filete del metal del nivel y el pergamino, con su filete dorado de dentro.
        val marco = cartela(r0, 5 * u)
        drawPath(marco, t(CAOBA, oscuro))
        clipPath(marco) { vetas(r0, t(CAOBA_VETA, oscuro).copy(alpha = 0.6f), e.semilla, densidad = 0.6f, grosor = 0.6f) }
        filete(m, cartela(r0.deflate(1.8f * u), 4 * u), 1.8f * u)
        val cara = r0.deflate(4 * u)
        drawPath(cartela(cara, 3 * u), if (oscuro) Color(0xFF2A1D16) else PERGAMINO)
        drawPath(cartela(r0.deflate(5.6f * u), 2.2f * u), t(ORO, oscuro).copy(alpha = 0.8f), style = Stroke(0.5f * u))
        // Un punto dorado en cada esquina entrante.
        listOf(Offset(2.2f * u, 2.2f * u), Offset(size.width - 2.2f * u, 2.2f * u), Offset(2.2f * u, size.height - 2.2f * u), Offset(size.width - 2.2f * u, size.height - 2.2f * u)).forEach { p ->
            drawCircle(t(ORO, oscuro), 1.1f * u, p)
        }
    }

    // ---------------------------------------------------------------- el salón del hall

    /**
     * La galería imperial de un palacio, con las paredes forradas de seda verde y sus abejas de oro: detrás de cada trofeo,
     * el panel de boiserie con su filete dorado, sus rosetas en las esquinas y la corona de laurel. Entre las franjas,
     * pilastras de mármol verde veteado con su capitel y su basa de bronce dorado, el águila encima y, en cada piso, la
     * girándula de tres velas (de noche, encendidas). Arriba, la cornisa de ovas y el lambrequín carmesí en festones, con su
     * fleco y sus borlas. Abajo, el zócalo de caoba con sus tableros y el parquet en espiga. En la esquina izquierda, el
     * tambor de la Guardia con sus aros rojos y sus cuerdas; en la derecha, el taburete en X con su cojín.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) = clipRect { salon(e, oscuro) }

    /** La escena del salón, recortada al lienzo por [escenaSalon]. */
    private fun DrawScope.salon(e: EstadoSalon, oscuro: Boolean) {
        val p = planta(e); val u = p.u; val w = p.w; val h = p.h
        fun c(x: Color, f: Float) = mezcla(e.fondo, t(x, oscuro), f)
        val techo = 40 * u; val zocalo = h - 44 * u; val piso = h - 16 * u

        // La seda verde de la pared, con su listado y las abejas de oro en tresbolillo.
        drawRect(c(SEDA, if (oscuro) 0.4f else 0.2f), Offset(0f, techo), Size(w, zocalo - techo))
        run {
            var x = 0f; var k = 0
            while (x < w && k < 60) { drawLine(c(SEDA_LUZ, if (oscuro) 0.3f else 0.22f), Offset(x, techo), Offset(x, zocalo), 2.4f * u); x += maxOf(12 * u, 1f); k++ }
            val abeja = c(ORO, if (oscuro) 0.4f else 0.42f)
            var y = techo + 20 * u; var f = 0; var n = 0
            while (y < zocalo - 10 * u && f < 40) {
                var bx = if (f % 2 == 0) 14 * u else 32 * u
                while (bx < w && n < 220) {
                    drawOval(abeja, Offset(bx - 1.2f * u, y - 2 * u), Size(2.4f * u, 4.4f * u))
                    drawPath(Path().apply { moveTo(bx, y - 1 * u); lineTo(bx - 3 * u, y - 3 * u); lineTo(bx - 2.6f * u, y); close(); moveTo(bx, y - 1 * u); lineTo(bx + 3 * u, y - 3 * u); lineTo(bx + 2.6f * u, y); close() }, abeja)
                    bx += maxOf(36 * u, 1f); n++
                }
                y += maxOf(30 * u, 1f); f++
            }
        }
        // El panel de boiserie detrás de cada trofeo: el filete de oro, las rosetas y la corona de laurel.
        for (i in 0 until p.n) for (k in 0 until p.filas) {
            val an = minOf(p.paso - 44 * u, 124 * u)
            val r = Rect(p.cx(i) - an / 2, maxOf(p.y(k) + 2 * u, techo + 6 * u), p.cx(i) + an / 2, minOf(p.y(k) + p.alto - 18 * u, zocalo - 8 * u))
            if (r.height < 60 * u) continue
            drawRect(c(SEDA, if (oscuro) 0.48f else 0.26f), r.topLeft, r.size)
            drawRect(c(ORO, if (oscuro) 0.4f else 0.42f), r.topLeft, r.size, style = Stroke(1.4f * u))
            val r2 = r.deflate(5 * u)
            drawRect(c(ORO, if (oscuro) 0.3f else 0.3f), r2.topLeft, r2.size, style = Stroke(0.7f * u))
            listOf(r.topLeft, Offset(r.right, r.top), Offset(r.left, r.bottom), Offset(r.right, r.bottom)).forEach { q ->
                drawCircle(c(ORO, 0.5f), 3 * u, q); drawCircle(c(ORO_LUZ, 0.6f), 1.2f * u, q)
            }
            // La corona de laurel, abierta arriba, en lo alto del panel.
            val cc = Offset(r.center.x, r2.top + 16 * u)
            for (s in listOf(-1f, 1f)) for (j in 0 until 6) {
                val a = PI / 2 + s * (0.5 + j * 0.42)
                val q = Offset(cc.x + (10 * u * cos(a)).toFloat(), cc.y + (10 * u * sin(a)).toFloat())
                drawOval(c(ORO, if (oscuro) 0.38f else 0.36f), Offset(q.x - 1.6f * u, q.y - 1 * u), Size(3.2f * u, 2 * u))
            }
        }

        // Las pilastras de mármol verde entre las franjas, con capitel y basa dorados, el águila y las girándulas.
        for (i in 0..p.n) {
            val x = p.lx(i); val an = 18 * u
            drawRect(c(MARMOL_VERDE, if (oscuro) 0.7f else 0.6f), Offset(x - an / 2, techo), Size(an, zocalo - techo))
            run {
                var y = techo; var k = 0
                while (y < zocalo && k < 40) {
                    val y2 = y + (20 + 30 * azar(e.semilla + i, k)) * u
                    drawLine(c(MARMOL_VETA, if (oscuro) 0.35f else 0.28f), Offset(x - an / 2 + an * azar(e.semilla + i, k + 50), y), Offset(x - an / 2 + an * azar(e.semilla + i, k + 90), y2), 0.7f * u)
                    y = y2; k++
                }
            }
            drawRect(c(ORO, 0.5f), Offset(x - an / 2, techo), Size(an, zocalo - techo), style = Stroke(1 * u))
            drawRect(c(ORO, if (oscuro) 0.6f else 0.75f), Offset(x - an / 2 - 3 * u, techo), Size(an + 6 * u, 8 * u))
            for (j in 0 until 4) drawCircle(c(ORO_LUZ, 0.7f), 1.2f * u, Offset(x - an / 2 + an * (j + 0.5f) / 4, techo + 4 * u))
            drawRect(c(ORO, if (oscuro) 0.6f else 0.75f), Offset(x - an / 2 - 3 * u, zocalo - 8 * u), Size(an + 6 * u, 8 * u))
            drawLine(c(ORO_LUZ, 0.7f), Offset(x - an / 2 - 3 * u, zocalo - 8 * u), Offset(x + an / 2 + 3 * u, zocalo - 8 * u), 1 * u)
            for (k in 0 until p.filas) {
                val y = p.y(k) + 46 * u
                if (y < techo + 24 * u || y > zocalo - 40 * u) continue
                // La girándula: el brazo de bronce en lira y tres velas.
                drawPath(Path().apply { moveTo(x - 10 * u, y - 10 * u); quadraticTo(x - 10 * u, y + 2 * u, x, y + 3 * u); quadraticTo(x + 10 * u, y + 2 * u, x + 10 * u, y - 10 * u) }, c(ORO, 0.8f), style = Stroke(1.4f * u))
                drawLine(c(ORO, 0.8f), Offset(x, y + 6 * u), Offset(x, y - 12 * u), 1.4f * u)
                drawCircle(c(ORO, 0.85f), 2.4f * u, Offset(x, y + 6 * u))
                listOf(-10f, 0f, 10f).forEach { dx ->
                    val vx = x + dx * u; val vy = y - (if (dx == 0f) 14 else 10) * u
                    drawRect(c(ORO, 0.8f), Offset(vx - 2.5f * u, vy), Size(5 * u, 1.6f * u))
                    drawRect(c(CERA, if (oscuro) 0.75f else 0.85f), Offset(vx - 1.2f * u, vy - 9 * u), Size(2.4f * u, 9 * u))
                    if (oscuro) {
                        halo(Offset(vx, vy - 12 * u), 22 * u, LLAMA.copy(alpha = 0.035f), 4)
                        drawOval(LLAMA.copy(alpha = 0.95f), Offset(vx - 1.2f * u, vy - 14 * u), Size(2.4f * u, 4.4f * u))
                    }
                }
            }
        }

        // Arriba: la cornisa con sus ovas y el lambrequín carmesí en festones, con su fleco de oro y las borlas.
        drawRect(c(CAOBA, if (oscuro) 0.55f else 0.5f), Offset(0f, 0f), Size(w, techo))
        drawRect(c(ORO, if (oscuro) 0.5f else 0.6f), Offset(0f, 4 * u), Size(w, 2 * u))
        run { var x = 2 * u; var k = 0; while (x < w && k < 120) { drawOval(c(ORO, 0.55f), Offset(x, 8 * u), Size(3.4f * u, 4.6f * u)); drawLine(c(ORO, 0.55f), Offset(x + 4.7f * u, 8 * u), Offset(x + 4.7f * u, 12.6f * u), 0.7f * u); x += maxOf(6 * u, 1f); k++ } }
        drawRect(c(ORO, if (oscuro) 0.5f else 0.6f), Offset(0f, 14 * u), Size(w, 1.4f * u))
        val ondas = p.n * 3
        val tela = Path().apply {
            moveTo(0f, 15 * u); lineTo(w, 15 * u); lineTo(w, 22 * u)
            for (k in ondas downTo 1) { val x0 = w * k / ondas; val x1 = w * (k - 1) / ondas; quadraticTo((x0 + x1) / 2, 36 * u, x1, 22 * u) }
            close()
        }
        drawPath(tela, c(CARMESI, if (oscuro) 0.6f else 0.6f))
        for (k in 0 until ondas) {
            val x0 = w * k / ondas; val x1 = w * (k + 1) / ondas
            drawPath(Path().apply { moveTo(x0 + 3 * u, 20 * u); quadraticTo((x0 + x1) / 2, 31 * u, x1 - 3 * u, 20 * u) }, c(CARMESI_OSC, 0.6f), style = Stroke(1 * u))
            drawPath(Path().apply { moveTo(x0, 22 * u); quadraticTo((x0 + x1) / 2, 36 * u, x1, 22 * u) }, c(ORO, 0.75f), style = Stroke(1.6f * u))
            drawPath(Path().apply { moveTo(x0, 24 * u); quadraticTo((x0 + x1) / 2, 38 * u, x1, 24 * u) }, c(ORO, 0.55f), style = Stroke(2 * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.8f * u, 1.2f * u))))
        }
        for (i in 0..p.n) {
            val x = p.lx(i)
            // La borla que cuelga en cada línea y el águila sobre el capitel.
            drawLine(c(ORO, 0.75f), Offset(x, 22 * u), Offset(x, 30 * u), 1 * u)
            drawPath(Path().apply { moveTo(x - 1.5f * u, 30 * u); lineTo(x + 1.5f * u, 30 * u); lineTo(x + 3 * u, 37 * u); lineTo(x - 3 * u, 37 * u); close() }, c(ORO, 0.8f))
            val ag = Offset(x, techo + 18 * u)
            drawPath(Path().apply {
                moveTo(ag.x, ag.y - 6 * u); lineTo(ag.x - 2 * u, ag.y - 3 * u)
                quadraticTo(ag.x - 9 * u, ag.y - 9 * u, ag.x - 12 * u, ag.y - 4 * u); quadraticTo(ag.x - 7 * u, ag.y - 2 * u, ag.x - 3 * u, ag.y + 1 * u)
                lineTo(ag.x - 3 * u, ag.y + 6 * u); lineTo(ag.x, ag.y + 4 * u); lineTo(ag.x + 3 * u, ag.y + 6 * u); lineTo(ag.x + 3 * u, ag.y + 1 * u)
                quadraticTo(ag.x + 7 * u, ag.y - 2 * u, ag.x + 12 * u, ag.y - 4 * u); quadraticTo(ag.x + 9 * u, ag.y - 9 * u, ag.x + 2 * u, ag.y - 3 * u); close()
            }, c(ORO, if (oscuro) 0.7f else 0.8f))
            drawLine(c(ORO, 0.8f), Offset(ag.x - 4 * u, ag.y + 7 * u), Offset(ag.x + 4 * u, ag.y + 7 * u), 1.2f * u)
        }

        // Abajo: el zócalo de caoba con sus tableros de filete dorado y el parquet en espiga.
        drawRect(c(CAOBA, if (oscuro) 0.55f else 0.5f), Offset(0f, zocalo), Size(w, piso - zocalo))
        vetasEn(Rect(0f, zocalo, w, piso), c(CAOBA_VETA, 0.45f), e.semilla + 3, densidad = 0.7f, grosor = 0.7f)
        drawRect(c(ORO, 0.6f), Offset(0f, zocalo), Size(w, 1.6f * u))
        for (i in 0 until p.n) {
            val an = p.paso - 40 * u
            drawRect(c(ORO, 0.5f), Offset(p.cx(i) - an / 2, zocalo + 6 * u), Size(an, piso - zocalo - 12 * u), style = Stroke(0.9f * u))
        }
        drawRect(c(ROBLE, if (oscuro) 0.4f else 0.45f), Offset(0f, piso), Size(w, h - piso))
        run {
            val pa = 8 * u; var x = 0f; var k = 0
            while (x < w && k < 80) {
                drawLine(c(ROBLE_OSC, 0.6f), Offset(x, piso), Offset(x + pa, h), 0.8f * u)
                drawLine(c(ROBLE_OSC, 0.6f), Offset(x + pa, piso), Offset(x, h), 0.8f * u)
                x += maxOf(pa, 1f); k++
            }
        }

        // El tambor de la Guardia en la esquina izquierda: el casco azul, los aros rojos y las cuerdas en zigzag.
        run {
            val r = Rect(2 * u, zocalo - 4 * u, 34 * u, h - 3 * u)
            drawRect(c(AZUL_IMPERIAL, if (oscuro) 0.6f else 0.65f), r.topLeft, r.size)
            drawOval(c(PERGAMINO, 0.6f), Offset(r.left, r.top - 4 * u), Size(r.width, 8 * u))
            listOf(r.top + 2 * u, r.bottom - 5 * u).forEach { y -> drawRect(c(CARMESI, 0.75f), Offset(r.left, y), Size(r.width, 4 * u)) }
            val cuerda = Path().apply { moveTo(r.left, r.top + 6 * u); for (k in 1..8) lineTo(r.left + r.width * k / 8, if (k % 2 == 1) r.bottom - 5 * u else r.top + 6 * u) }
            drawPath(cuerda, c(PERGAMINO, 0.7f), style = Stroke(0.8f * u))
            drawLine(c(CAOBA, 0.8f), Offset(r.right - 6 * u, r.top - 6 * u), Offset(r.right + 4 * u, r.top - 16 * u), 1.4f * u, StrokeCap.Round)
        }
        // El taburete en X de la derecha: las patas cruzadas de caoba, el cojín carmesí y su fleco.
        run {
            val x1 = w - 4 * u; val x0 = x1 - 30 * u; val y0 = zocalo + 2 * u
            drawLine(c(CAOBA, 0.75f), Offset(x0 + 2 * u, h - 2 * u), Offset(x1 - 4 * u, y0 + 6 * u), 2.4f * u)
            drawLine(c(CAOBA, 0.75f), Offset(x1 - 2 * u, h - 2 * u), Offset(x0 + 4 * u, y0 + 6 * u), 2.4f * u)
            drawCircle(c(ORO, 0.8f), 2 * u, Offset((x0 + x1) / 2 + 0.5f * u, (y0 + 6 * u + h - 2 * u) / 2))
            drawRoundRect(c(CARMESI, if (oscuro) 0.6f else 0.7f), Offset(x0, y0 - 2 * u), Size(30 * u, 8 * u), CornerRadius(3 * u))
            drawLine(c(ORO, 0.7f), Offset(x0 + 1 * u, y0 + 6 * u), Offset(x1 - 1 * u, y0 + 6 * u), 1.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.8f * u, 1 * u)))
        }
    }

    // ---------------------------------------------------------------- las campañas

    /**
     * El granadero de la Guardia que marcha por la campaña, de lado y hacia la derecha: las polainas negras, el calzón
     * blanco, la casaca azul con su solapa blanca, la charretera roja y los faldones con su forro carmesí; el fusil al hombro
     * con su culata de nogal y la bayoneta; y el gorro alto de piel de oso con su chapa de oro y el penacho del color de
     * acento. De noche, la casaca y el gorro llevan un filo claro para que se lean sobre el mapa oscuro.
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density; val p = e.punto
        sombraMarcadorContemporanea(p, 16 * u, e.salto, oscuro)
        val o = Offset(p.x, p.y - subidaContemporanea(e.salto))
        fun x(v: Float) = o.x + v * u
        fun y(v: Float) = o.y + v * u
        val azul = t(AZUL_IMPERIAL, oscuro); val rojo = t(CARMESI, oscuro); val blanco = t(Color(0xFFF2EEE4), oscuro)
        val negro = t(Color(0xFF1A1614), oscuro); val oro = t(ORO, oscuro); val piel = t(Color(0xFFE0B48A), oscuro)
        val filo = Color(0xFFF2E8CC).copy(alpha = 0.6f)
        // Las piernas: el calzón blanco y las polainas negras, con los zapatos.
        listOf(-1.7f, 1.3f).forEach { lx ->
            if (oscuro) drawLine(filo, Offset(x(lx), y(-5.6f)), Offset(x(lx), y(-0.2f)), 2.9f * u)
            drawLine(blanco, Offset(x(lx), y(-9.6f)), Offset(x(lx), y(-5f)), 2f * u)
            drawLine(negro, Offset(x(lx), y(-5.2f)), Offset(x(lx), y(-0.6f)), 2f * u)
            drawRoundRect(negro, Offset(x(lx - 1f), y(-1f)), Size(2.6f * u, 1f * u), CornerRadius(0.4f * u))
        }
        // Los faldones con su forro carmesí y la casaca.
        val faldon = trazoContemporanea(o, u, true, -2.6f, -15f, -3.2f, -15f, -5.2f, -7.4f, -2.4f, -9.2f)
        val casaca = trazoContemporanea(o, u, true, -3.4f, -17.8f, 2.6f, -17.8f, 3.2f, -9.4f, -3f, -9.4f)
        if (oscuro) { drawPath(faldon, filo, style = Stroke(1f * u)); drawPath(casaca, filo, style = Stroke(1f * u)) }
        drawPath(faldon, rojo)
        drawPath(casaca, azul)
        // La solapa blanca, el cinturón y la cartuchera.
        drawPath(trazoContemporanea(o, u, true, 0.6f, -17.4f, 2.6f, -17.4f, 3f, -11.6f, 1.2f, -12.4f), blanco)
        drawLine(blanco, Offset(x(-3f), y(-10.6f)), Offset(x(3.1f), y(-10.6f)), 0.8f * u)
        drawLine(blanco, Offset(x(2f), y(-17.4f)), Offset(x(-3f), y(-11.4f)), 0.6f * u)
        drawRect(negro, Offset(x(-4.6f), y(-13.6f)), Size(2 * u, 2.4f * u))
        // La charretera, el brazo con su vuelta roja y la mano que sostiene el fusil.
        drawOval(rojo, Offset(x(-0.6f), y(-18.4f)), Size(3.2f * u, 1.6f * u))
        drawLine(azul, Offset(x(0.8f), y(-16.8f)), Offset(x(3.6f), y(-12.6f)), 1.7f * u, StrokeCap.Round)
        drawLine(rojo, Offset(x(3.2f), y(-13.2f)), Offset(x(3.9f), y(-12.2f)), 1.7f * u)
        // El fusil: la culata, el cañón y la bayoneta.
        drawPath(trazoContemporanea(o, u, true, 4.2f, -0.8f, 6.2f, -0.8f, 5.6f, -7f, 4.6f, -7f), t(ROBLE_OSC, oscuro))
        drawLine(t(ROBLE, oscuro), Offset(x(5f), y(-7f)), Offset(x(5f), y(-15f)), 1.1f * u)
        drawLine(t(Color(0xFF8A9096), oscuro), Offset(x(5f), y(-15f)), Offset(x(5f), y(-22.4f)), 0.7f * u)
        drawLine(t(Color(0xFFC9CED3), oscuro), Offset(x(5.4f), y(-21.6f)), Offset(x(5.4f), y(-27.4f)), 0.5f * u)
        drawCircle(piel, 0.8f * u, Offset(x(4.4f), y(-12.4f)))
        // La cabeza y el gorro de piel de oso con su chapa, su cordón y el penacho de acento.
        drawCircle(piel, 1.9f * u, Offset(x(0.4f), y(-19.6f)))
        val gorro = Path().apply {
            moveTo(x(-2.4f), y(-20.4f)); cubicTo(x(-3.8f), y(-24f), x(-3.2f), y(-28.4f), x(0f), y(-28.4f))
            cubicTo(x(3.2f), y(-28.4f), x(3.4f), y(-24f), x(2.6f), y(-20.4f)); close()
        }
        if (oscuro) drawPath(gorro, filo, style = Stroke(1.1f * u))
        drawPath(gorro, negro)
        drawPath(Path().apply { moveTo(x(-1.6f), y(-26.6f)); quadraticTo(x(-0.4f), y(-27.8f), x(1.2f), y(-27.4f)) }, Color.White.copy(alpha = 0.18f), style = Stroke(0.6f * u))
        drawPath(trazoContemporanea(o, u, true, 1.2f, -21f, 2.8f, -21f, 2.6f, -23.6f, 1.6f, -23.2f), oro)
        drawPath(Path().apply { moveTo(x(-2.4f), y(-22f)); quadraticTo(x(0f), y(-20.4f), x(2.4f), y(-22.6f)) }, t(Color(0xFFF2EEE4), oscuro), style = Stroke(0.45f * u))
        drawPath(Path().apply { moveTo(x(-2.8f), y(-21.6f)); cubicTo(x(-4.8f), y(-24f), x(-4.6f), y(-28.6f), x(-3.2f), y(-30.4f)); cubicTo(x(-2.6f), y(-28f), x(-2.4f), y(-24f), x(-2f), y(-22f)); close() }, e.acento)
        drawPath(Path().apply { moveTo(x(-3.2f), y(-29.6f)); quadraticTo(x(-3.8f), y(-26f), x(-2.8f), y(-22.6f)) }, mezcla(e.acento, Color.Black, 0.3f), style = Stroke(0.4f * u))
    }

    /**
     * La victoria de Austerlitz: el sol que sale detrás, con sus rayos largos y cortos; el asta con el águila de oro de alas
     * abiertas sobre su peana y la bandera de 1804 (el rombo blanco al centro, los rincones azules y rojos, el fleco de oro y
     * la «N» en su corona de laurel); al pie, el tambor del regimiento, azul con sus aros rojos, sus cuerdas en zigzag y los
     * palillos cruzados encima, y una rama de laurel. De noche, el sol y el oro brillan con su halo.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val b = pieVictoriaContemporanea(e.meta, 46 * u, 58 * u)
        fun x(v: Float) = b.x + v * u
        fun y(v: Float) = b.y + v * u
        val oro = t(ORO, oscuro); val oroLuz = ORO_LUZ; val azul = t(AZUL_IMPERIAL, oscuro); val rojo = t(CARMESI, oscuro)
        val blanco = t(Color(0xFFF6F2E8), oscuro)
        // El sol de Austerlitz.
        val sol = Offset(x(9f), y(-44f))
        if (oscuro) halo(sol, 15 * u, ORO_LUZ.copy(alpha = 0.07f), 5)
        for (k in 0 until 16) {
            val a = (k * PI / 8).toFloat(); val l = if (k % 2 == 0) 10.5f else 8f
            drawLine(oro.copy(alpha = 0.8f), Offset(sol.x + 6 * u * cos(a), sol.y + 6 * u * sin(a)), Offset(sol.x + l * u * cos(a), sol.y + l * u * sin(a)), (if (k % 2 == 0) 1.1f else 0.7f) * u, StrokeCap.Round)
        }
        drawCircle(oro, 5 * u, sol); drawCircle(oroLuz, 3.4f * u, sol)
        // El asta.
        drawLine(t(ROBLE_OSC, oscuro), Offset(x(-8f), y(0f)), Offset(x(-8f), y(-46f)), 1.3f * u)
        // La bandera de 1804.
        val f = Rect(x(-7.4f), y(-43f), x(10f), y(-29f))
        drawRect(blanco, f.topLeft, f.size)
        fun rincon(a: Offset, c: Offset, d: Offset, col: Color) = drawPath(Path().apply { moveTo(a.x, a.y); lineTo(c.x, c.y); lineTo(d.x, d.y); close() }, col)
        rincon(f.topLeft, Offset(f.center.x, f.top), Offset(f.left, f.center.y), azul)
        rincon(Offset(f.center.x, f.top), f.topRight, Offset(f.right, f.center.y), rojo)
        rincon(Offset(f.right, f.center.y), f.bottomRight, Offset(f.center.x, f.bottom), azul)
        rincon(Offset(f.center.x, f.bottom), f.bottomLeft, Offset(f.left, f.center.y), rojo)
        drawRect(oro, f.topLeft, f.size, style = Stroke(0.9f * u))
        for (k in 0 until 9) drawLine(oro, Offset(f.left + (k + 0.5f) * f.width / 9, f.bottom), Offset(f.left + (k + 0.5f) * f.width / 9, f.bottom + 1.4f * u), 0.5f * u)
        val c = f.center
        for (k in 0 until 10) {
            val a = (PI * (0.62 + k * 0.176)).toFloat()
            drawOval(t(SEDA, oscuro), Offset(c.x + 3.4f * u * cos(a) - 0.7f * u, c.y + 3.4f * u * sin(a) - 0.45f * u), Size(1.4f * u, 0.9f * u))
        }
        drawPath(Path().apply { moveTo(c.x - 1.4f * u, c.y + 1.8f * u); lineTo(c.x - 1.4f * u, c.y - 1.8f * u); lineTo(c.x + 1.4f * u, c.y + 1.8f * u); lineTo(c.x + 1.4f * u, c.y - 1.8f * u) }, oro, style = Stroke(0.7f * u))
        // El águila sobre su peana.
        val ag = Offset(x(-8f), y(-49f))
        if (oscuro) halo(ag, 8 * u, ORO_LUZ.copy(alpha = 0.06f), 3)
        drawRect(oro, Offset(ag.x - 2.6f * u, ag.y + 2.4f * u), Size(5.2f * u, 1.6f * u))
        drawOval(oro, Offset(ag.x - 1.6f * u, ag.y - 2.6f * u), Size(3.2f * u, 5.4f * u))
        drawCircle(oro, 1.2f * u, Offset(ag.x + 0.4f * u, ag.y - 3.2f * u))
        drawPath(trazoContemporanea(ag, u, true, 1.2f, -3.6f, 2.6f, -3.2f, 1.4f, -2.6f), oroLuz)
        listOf(-1f, 1f).forEach { s ->
            drawPath(trazoContemporanea(ag, u, true, 0f, -0.6f, s * 6.6f, -6.4f, s * 5.6f, -3f, s * 7f, -1.4f, s * 5f, 0.2f, s * 5.8f, 1.8f, s * 1.2f, 1.4f), oro)
            drawLine(oroLuz, Offset(ag.x + s * 1.4f * u, ag.y - 0.8f * u), Offset(ag.x + s * 5.6f * u, ag.y - 5f * u), 0.5f * u)
        }
        // La rama de laurel al pie del asta.
        for (k in 0 until 5) {
            val px = -10.4f - k * 1.4f; val py = -1.4f - k * 2.2f
            drawOval(t(SEDA, oscuro), Offset(x(px - 1.6f), y(py - 0.6f)), Size(2.2f * u, 1.1f * u))
            drawOval(t(SEDA_LUZ, oscuro), Offset(x(px - 0.2f), y(py - 1.6f)), Size(1.1f * u, 2f * u))
        }
        drawLine(t(SEDA, oscuro), Offset(x(-9.6f), y(0f)), Offset(x(-16.4f), y(-10.4f)), 0.5f * u)
        // El tambor: el casco azul, los aros rojos, las cuerdas en zigzag y los palillos.
        val tb = Rect(x(1f), y(-9f), x(14f), y(0f))
        drawRect(azul, tb.topLeft, tb.size)
        drawOval(t(Color(0xFFE8DFC8), oscuro), Offset(tb.left, tb.top - 1.6f * u), Size(tb.width, 3.2f * u))
        drawRect(rojo, Offset(tb.left, tb.top), Size(tb.width, 1.6f * u))
        drawRect(rojo, Offset(tb.left, tb.bottom - 1.6f * u), Size(tb.width, 1.6f * u))
        drawPath(Path().apply {
            moveTo(tb.left, tb.top + 1.6f * u)
            for (k in 1..6) lineTo(tb.left + k * tb.width / 6, if (k % 2 == 1) tb.bottom - 1.6f * u else tb.top + 1.6f * u)
        }, blanco, style = Stroke(0.5f * u))
        drawOval(t(Color(0xFFE8DFC8), oscuro), Offset(tb.left, tb.top - 1.6f * u), Size(tb.width, 3.2f * u), style = Stroke(0.4f * u))
        drawLine(t(ROBLE, oscuro), Offset(x(4f), y(-11.6f)), Offset(x(11f), y(-8.6f)), 0.8f * u, StrokeCap.Round)
        drawLine(t(ROBLE, oscuro), Offset(x(11f), y(-11.8f)), Offset(x(4.6f), y(-8.4f)), 0.8f * u, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- objetos de Tu año

    /**
     * Napoleón: la luna creciente sobre la tienda del vivac con su banderola, la bota de montar con la caña vuelta y la
     * espuela, y la bala encadenada: dos balas de cañón unidas por la cadena tensa.
     */
    override fun DrawScope.objetoAnio(e: EstadoObjeto, oscuro: Boolean) {
        val r = e.radio; val x = e.centro.x; val y = e.centro.y; val u = density
        val col = e.color; val fondo = e.fondo
        fun p(a: Float, b: Float) = Offset(x + a * r, y + b * r)
        fun g(f: Float) = maxOf(r * f, 0.8f * u)
        when (e.forma) {
            "luna" -> {
                drawPath(crecienteContemporanea(p(0.52f, -0.52f), r * 0.4f, Offset(r * 0.2f, -r * 0.14f), r * 0.34f), col)
                drawPath(trazoAnioContemporanea(e.centro, r) { m(-0.95f, 0.88f); l(-0.2f, -0.3f); l(0.55f, 0.88f); z() }, col)
                drawPath(trazoAnioContemporanea(e.centro, r) { m(-0.2f, 0.18f); l(0.04f, 0.88f); l(-0.44f, 0.88f); z() }, fondo)
                drawLine(col, p(-0.2f, -0.2f), p(-0.2f, -0.8f), g(0.07f))
                drawPath(trazoAnioContemporanea(e.centro, r) { m(-0.2f, -0.8f); l(-0.58f, -0.68f); l(-0.2f, -0.56f); z() }, col)
            }
            "huella" -> {
                val bota = trazoAnioContemporanea(e.centro, r) {
                    m(-0.52f, -0.95f); l(0.14f, -0.95f); l(0.06f, -0.7f); l(0.04f, 0.22f); q(0.14f, 0.42f, 0.55f, 0.5f); q(0.9f, 0.56f, 0.88f, 0.8f)
                    l(0.88f, 0.84f); l(-0.02f, 0.84f); l(-0.1f, 0.74f); l(-0.22f, 0.74f); l(-0.22f, 0.96f); l(-0.5f, 0.96f); l(-0.5f, 0.3f)
                    q(-0.44f, -0.3f, -0.52f, -0.95f); z()
                }
                drawPath(bota, col)
                // La caña vuelta, en un tono del color.
                clipPath(bota) { drawRect(mezcla(col, fondo, 0.5f), p(-0.6f, -1f), Size(r * 0.8f, r * 0.32f)) }
                // La espuela con su rodaja.
                drawLine(col, p(-0.5f, 0.68f), p(-0.76f, 0.62f), g(0.08f))
                drawCircle(col, r * 0.14f, p(-0.82f, 0.61f))
                drawCircle(fondo, maxOf(r * 0.05f, 0.4f * u), p(-0.82f, 0.61f))
            }
            "pesa" -> {
                // Las dos balas y la cadena tensa entre ellas.
                val izq = p(-0.66f, 0f); val der = p(0.66f, 0f)
                // Los eslabones, echados y parados por turno, como en una cadena de verdad.
                val t = Stroke(g(0.07f))
                listOf(-0.22f, 0.22f).forEach { a -> drawOval(col, p(a - 0.16f, -0.1f), Size(r * 0.32f, r * 0.2f), style = t) }
                drawOval(col, p(-0.07f, -0.17f), Size(r * 0.14f, r * 0.34f), style = t)
                // Las balas, con su brillo de hierro: un arco, no un punto, para que no parezcan ojos.
                listOf(izq, der).forEach { b ->
                    drawCircle(col, r * 0.33f, b)
                    if (r > 5 * u) drawArc(fondo, 200f, 60f, false, b - Offset(r * 0.22f, r * 0.22f), Size(r * 0.44f, r * 0.44f), style = Stroke(g(0.06f), cap = StrokeCap.Round))
                }
            }
            else -> {}
        }
    }
}

/**
 * Art déco: el trofeo se apoya en un plinto escalonado de ónix negro en tres gradas, con los cantos de cromo, el reflejo
 * pulido en diagonal, el abanico solar del metal del nivel al frente entre chevrones dorados y el zócalo de jade. En el
 * hall, el pedestal del vestíbulo: la losa de cromo, el fuste de jade con sus junquillos y el rombo del metal, sobre
 * gradas de laca. La placa es la cartela de laca de esquinas en escalón, con doble filete y los rombos de arriba y abajo.
 */
object LogrosDeco : DisenoLogros {
    private val LACA = Color(0xFF16171A)
    private val LACA_LUZ = Color(0xFF34363C)
    private val CREMA = Color(0xFFF0E6CF)
    private val ORO = Color(0xFFCDAA5C)
    private val ORO_CLARO = Color(0xFFE6C77A)
    private val JADE = Color(0xFF1F6B5C)
    private val JADE_LUZ = Color(0xFF4FA88C)
    private val CROMO = Color(0xFFC9CED3)
    private val CROMO_LUZ = Color(0xFFF1F4F6)
    private val CROMO_OSC = Color(0xFF7D858C)

    // salón: colores
    private val ARCE = Color(0xFFD8B98A)
    private val NOGAL = Color(0xFF6A4528)
    private val CORAL = Color(0xFFB5523B)
    private val PALMA = Color(0xFF3E6A48)
    private val VIDRIO = Color(0xFFE8F0EE)
    private val LUZ = Color(0xFFFFE2A0)

    /** El color en modo oscuro: más hundido, como en su biblioteca. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0C0C0E), 0.34f) else c

    /** Un rectángulo de esquinas en escalón, de [e] de entrante, como las cartelas del estante. */
    private fun escalonado(r: Rect, e: Float): Path = Path().apply {
        moveTo(r.left + e, r.top); lineTo(r.right - e, r.top); lineTo(r.right - e, r.top + e * 0.5f); lineTo(r.right - e * 0.5f, r.top + e * 0.5f)
        lineTo(r.right - e * 0.5f, r.top + e); lineTo(r.right, r.top + e); lineTo(r.right, r.bottom - e); lineTo(r.right - e * 0.5f, r.bottom - e)
        lineTo(r.right - e * 0.5f, r.bottom - e * 0.5f); lineTo(r.right - e, r.bottom - e * 0.5f); lineTo(r.right - e, r.bottom); lineTo(r.left + e, r.bottom)
        lineTo(r.left + e, r.bottom - e * 0.5f); lineTo(r.left + e * 0.5f, r.bottom - e * 0.5f); lineTo(r.left + e * 0.5f, r.bottom - e); lineTo(r.left, r.bottom - e)
        lineTo(r.left, r.top + e); lineTo(r.left + e * 0.5f, r.top + e); lineTo(r.left + e * 0.5f, r.top + e * 0.5f); lineTo(r.left + e, r.top + e * 0.5f); close()
    }

    /** Un rombo de centro [c] y semiejes [a] (horizontal) y [b] (vertical). */
    private fun rombo(c: Offset, a: Float, b: Float): Path = Path().apply { moveTo(c.x, c.y - b); lineTo(c.x + a, c.y); lineTo(c.x, c.y + b); lineTo(c.x - a, c.y); close() }

    // ---------------------------------------------------------------- pedestales y placa

    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val fino = h >= 90 * u
        val hl = maxOf(0.6f * u, h * 0.004f)
        fun c(x: Color) = t(x, oscuro)
        val g3 = Rect(w * 0.27f, h * 0.775f, w * 0.73f, h * 0.797f)
        val g2 = Rect(w * 0.2f, h * 0.797f, w * 0.8f, h * 0.82f)
        val cuerpo = Rect(w * 0.14f, h * 0.82f, w * 0.86f, h * 0.924f)
        val zocalo = Rect(w * 0.09f, h * 0.924f, w * 0.91f, h * 0.954f)
        if (!e.hecho) { silueta(oscuro) { addRect(g3); addRect(g2); addRect(cuerpo); addRect(zocalo) }; return }
        sombraPie(w / 2, zocalo.width, h * 0.958f, h * 0.022f, if (oscuro) 0.55f else 0.26f)
        // El zócalo de jade con su canto de cromo.
        drawRect(c(JADE), zocalo.topLeft, zocalo.size)
        drawRect(c(JADE_LUZ), zocalo.topLeft, Size(zocalo.width, zocalo.height * 0.22f))
        // Las tres gradas de ónix, cada una con su canto de cromo arriba y su sombra debajo.
        listOf(cuerpo, g2, g3).forEach { g ->
            drawRect(c(LACA), g.topLeft, g.size)
            drawRect(c(CROMO_OSC), g.topLeft, Size(g.width, hl * 1.4f))
            drawRect(c(CROMO_LUZ), g.topLeft, Size(g.width, hl * 0.6f))
        }
        // El reflejo pulido, en diagonal.
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
            drawPath(Path().apply {
                moveTo(cuerpo.left + cuerpo.width * 0.06f, cuerpo.bottom); lineTo(cuerpo.left + cuerpo.width * 0.16f, cuerpo.top)
                lineTo(cuerpo.left + cuerpo.width * 0.22f, cuerpo.top); lineTo(cuerpo.left + cuerpo.width * 0.12f, cuerpo.bottom); close()
            }, Color.White.copy(alpha = if (oscuro) 0.05f else 0.08f))
            // El abanico solar del metal del nivel, que sale del pie del cuerpo.
            val b = Offset(w / 2, cuerpo.bottom - cuerpo.height * 0.1f); val R = cuerpo.height * 0.72f
            val n = 9
            for (i in 0 until n) drawArc(if (i % 2 == 0) e.base else e.metalOscuro, 180f + i * 180f / n, 180f / n, true, Offset(b.x - R, b.y - R), Size(2 * R, 2 * R))
            if (e.nivel == 4) for (i in 0 until n step 2) drawArc(Color.White.copy(alpha = 0.5f), 180f + i * 180f / n, 180f / n * 0.3f, true, Offset(b.x - R, b.y - R), Size(2 * R, 2 * R))
            drawArc(e.brillo, 180f, 180f, true, Offset(b.x - R * 0.34f, b.y - R * 0.34f), Size(R * 0.68f, R * 0.68f))
            drawRect(e.metalOscuro, Offset(b.x - R * 1.05f, b.y), Size(R * 2.1f, hl))
        }
        // Los chevrones dorados a los lados y los junquillos de los extremos.
        if (fino) {
            val oro = c(ORO)
            listOf(-1f, 1f).forEach { s ->
                for (k in 0 until 3) {
                    val x = w / 2 + s * w * (0.24f + k * 0.045f); val cy = cuerpo.center.y; val a = cuerpo.height * 0.2f
                    drawPath(Path().apply { moveTo(x - s * a * 0.6f, cy - a); lineTo(x, cy); lineTo(x - s * a * 0.6f, cy + a) }, oro, style = Stroke(hl))
                }
                val x = w / 2 + s * w * 0.33f
                drawLine(oro.copy(alpha = 0.7f), Offset(x, cuerpo.top + cuerpo.height * 0.15f), Offset(x, cuerpo.bottom - cuerpo.height * 0.15f), hl * 0.6f)
            }
            listOf(g2.left, g2.right - w * 0.03f).forEach { x -> drawRect(c(JADE_LUZ), Offset(x, g2.top + hl * 1.4f), Size(w * 0.03f, g2.height - hl * 1.4f)) }
        }
    }

    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val m = tonos(e, oscuro)
        fun c(x: Color) = t(x, oscuro)
        sombraPie(w / 2, w * 0.84f, h * 0.95f, h * 0.14f, if (oscuro) 0.5f else 0.22f)
        // Las gradas de laca del pie, con su filete de oro.
        listOf(Rect(w * 0.08f, h * 0.88f, w * 0.92f, h), Rect(w * 0.13f, h * 0.78f, w * 0.87f, h * 0.88f)).forEach { g ->
            drawRect(c(LACA), g.topLeft, g.size)
            drawRect(c(ORO), g.topLeft, Size(g.width, 0.7f * u))
        }
        // El fuste de jade con sus junquillos de cromo.
        val fuste = Rect(w * 0.19f, h * 0.15f, w * 0.81f, h * 0.78f)
        drawRect(c(JADE), fuste.topLeft, fuste.size)
        drawRect(c(JADE_LUZ), fuste.topLeft, Size(fuste.width * 0.12f, fuste.height))
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(fuste.right - fuste.width * 0.12f, fuste.top), Size(fuste.width * 0.12f, fuste.height))
        for (k in 1..5) { if (k == 3) continue; val x = fuste.left + fuste.width * k / 6; drawLine(c(CROMO).copy(alpha = 0.75f), Offset(x, fuste.top), Offset(x, fuste.bottom), 0.8f * u) }
        // El rombo del metal del nivel, con su rombo de laca dentro.
        val cc = Offset(w / 2, fuste.center.y); val a = fuste.height * 0.42f
        drawPath(rombo(cc, a * 0.9f, a), m.metalOscuro)
        drawPath(rombo(cc - Offset(0.3f * u, 0.3f * u), a * 0.8f, a * 0.88f), m.base)
        drawPath(Path().apply { moveTo(cc.x, cc.y - a * 0.88f); lineTo(cc.x - a * 0.8f, cc.y); lineTo(cc.x, cc.y); close() }, m.brillo)
        drawPath(rombo(cc, a * 0.38f, a * 0.42f), c(LACA))
        // La losa de cromo de arriba.
        val losa = Rect(w * 0.06f, 0f, w * 0.94f, h * 0.16f)
        drawRect(c(CROMO), losa.topLeft, losa.size)
        drawRect(c(CROMO_LUZ), losa.topLeft, Size(losa.width, losa.height * 0.35f))
        drawRect(c(CROMO_OSC), Offset(losa.left, losa.bottom - 0.8f * u), Size(losa.width, 0.8f * u))
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = CREMA
    override fun rotuloPlaca(oscuro: Boolean): Color = ORO_CLARO

    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val m = tonos(e, oscuro)
        val r0 = Rect(Offset.Zero, size)
        drawPath(escalonado(r0.translate(0.4f * u, 1.6f * u), 7 * u), Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f))
        // La laca negra (en los dos modos), el filete del metal del nivel y el junquillo de oro de dentro.
        drawPath(escalonado(r0, 7 * u), LACA)
        filete(m, escalonado(r0.deflate(1.6f * u), 6 * u), 1.8f * u)
        drawPath(escalonado(r0.deflate(4.4f * u), 4 * u), ORO.copy(alpha = 0.75f), style = Stroke(0.55f * u))
        drawRect(LACA_LUZ.copy(alpha = 0.5f), Offset(7 * u, 3.2f * u), Size(w - 14 * u, 0.5f * u))
        // Los rombos del metal que cortan el filete arriba y abajo.
        listOf(1.6f * u, h - 1.6f * u).forEach { y ->
            drawPath(rombo(Offset(w / 2, y), 3.6f * u, 2.4f * u), m.metalOscuro)
            drawPath(rombo(Offset(w / 2, y), 2.8f * u, 1.8f * u), m.base)
            drawPath(rombo(Offset(w / 2, y), 1.1f * u, 0.8f * u), m.brillo)
        }
    }

    // ---------------------------------------------------------------- el salón del hall

    /**
     * El salón de baile de un transatlántico: el estuco crema de la pared y, detrás de cada trofeo, el gran panel de vidrio
     * grabado, con su remate escalonado y la fuente de chorros en abanico, apenas marcada; entre las franjas, las pilastras
     * de laca negra que suben en tres escalones, con sus estrías de oro, el filete de cromo y, en cada piso, el aplique de
     * abanico de vidrio esmerilado (de noche, encendido). Arriba, el techo con el gran sol de rayos y los casetones
     * escalonados con sus bandas de cromo. Abajo, el zócalo de laca con las tres líneas de cromo de la velocidad y la pista de
     * marquetería de arce y nogal. En la esquina izquierda, la palmera en su jardinera de cromo; en la derecha, la lámpara
     * de pie de pantalla escalonada.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) = clipRect { salon(e, oscuro) }

    /** La escena del salón, recortada al lienzo por [escenaSalon]. */
    private fun DrawScope.salon(e: EstadoSalon, oscuro: Boolean) {
        val p = planta(e); val u = p.u; val w = p.w; val h = p.h
        fun c(x: Color, f: Float) = mezcla(e.fondo, t(x, oscuro), f)
        val techo = 44 * u; val zocalo = h - 40 * u; val piso = h - 18 * u

        // El estuco crema de la pared, con un acanalado muy suave.
        drawRect(c(CREMA, if (oscuro) 0.16f else 0.5f), Offset(0f, techo), Size(w, zocalo - techo))
        run { var x = 0f; var k = 0; while (x < w && k < 80) { drawLine(c(ORO, if (oscuro) 0.1f else 0.12f), Offset(x, techo), Offset(x, zocalo), 0.7f * u); x += maxOf(9 * u, 1f); k++ } }
        // El panel de vidrio grabado detrás de cada trofeo: el remate en gradas, el marco de cromo y la fuente de chorros.
        for (i in 0 until p.n) for (k in 0 until p.filas) {
            val an = minOf(p.paso - 44 * u, 122 * u); val cx = p.cx(i)
            val y0 = maxOf(p.y(k) - 2 * u, techo + 6 * u); val y1 = minOf(p.y(k) + p.alto - 20 * u, zocalo - 8 * u)
            if (y1 - y0 < 70 * u) continue
            val g = 9 * u
            val panel = Path().apply {
                moveTo(cx - an / 2, y1); lineTo(cx - an / 2, y0 + 2 * g); lineTo(cx - an / 2 + g, y0 + 2 * g); lineTo(cx - an / 2 + g, y0 + g)
                lineTo(cx - an / 2 + 2 * g, y0 + g); lineTo(cx - an / 2 + 2 * g, y0); lineTo(cx + an / 2 - 2 * g, y0); lineTo(cx + an / 2 - 2 * g, y0 + g)
                lineTo(cx + an / 2 - g, y0 + g); lineTo(cx + an / 2 - g, y0 + 2 * g); lineTo(cx + an / 2, y0 + 2 * g); lineTo(cx + an / 2, y1); close()
            }
            drawPath(panel, c(VIDRIO, if (oscuro) 0.1f else 0.45f))
            clipPath(panel) {
                val base = Offset(cx, y1 - 6 * u)
                for (j in 0 until 9) {
                    val a = PI * (0.12 + 0.76 * j / 8)
                    val largo = (y1 - y0) * (0.55f + 0.3f * (1 - abs(j - 4) / 4f))
                    drawPath(Path().apply { moveTo(base.x, base.y); quadraticTo(base.x - (largo * 0.5f * cos(a)).toFloat(), base.y - largo, base.x - (largo * 0.9f * cos(a)).toFloat(), base.y - largo * 0.55f) },
                        c(Color.White, if (oscuro) 0.12f else 0.55f), style = Stroke(0.9f * u))
                }
                drawArc(c(Color.White, if (oscuro) 0.1f else 0.5f), 180f, 180f, false, Offset(cx - 18 * u, y1 - 14 * u), Size(36 * u, 16 * u), style = Stroke(1 * u))
            }
            drawPath(panel, c(CROMO, if (oscuro) 0.3f else 0.6f), style = Stroke(1.4f * u))
        }

        // Las pilastras de laca entre las franjas: tres escalones arriba, estrías de oro, filete de cromo y apliques de abanico.
        for (i in 0..p.n) {
            val x = p.lx(i); val an = 16 * u
            drawRect(c(LACA, if (oscuro) 0.7f else 0.75f), Offset(x - an / 2, techo), Size(an, zocalo - techo))
            for (s in 1..3) drawRect(c(LACA, if (oscuro) 0.7f else 0.75f), Offset(x - an / 2 - s * 3 * u, techo + (3 - s) * 4 * u), Size(an + s * 6 * u, 4 * u))
            drawRect(c(CROMO, 0.6f), Offset(x - an / 2 - 9 * u, techo + 12 * u), Size(an + 18 * u, 1.4f * u))
            listOf(-4f, 0f, 4f).forEach { dx -> drawLine(c(ORO, if (oscuro) 0.55f else 0.75f), Offset(x + dx * u, techo + 18 * u), Offset(x + dx * u, zocalo - 6 * u), 0.9f * u) }
            drawLine(c(CROMO_LUZ, 0.5f), Offset(x - an / 2 + 0.8f * u, techo + 14 * u), Offset(x - an / 2 + 0.8f * u, zocalo), 0.8f * u)
            for (k in 0 until p.filas) {
                val y = p.y(k) + 56 * u
                if (y < techo + 30 * u || y > zocalo - 30 * u) continue
                val r = 11 * u; val b = Offset(x, y)
                if (oscuro) halo(Offset(x, y - 6 * u), 46 * u, LUZ.copy(alpha = 0.03f), 6)
                for (j in 0 until 5) drawArc(if (oscuro) mezcla(e.fondo, LUZ, if (j % 2 == 0) 0.8f else 0.6f) else c(VIDRIO, if (j % 2 == 0) 0.95f else 0.75f), 180f + j * 36f, 36f, true, Offset(b.x - r, b.y - r), Size(2 * r, 2 * r))
                for (j in 0..5) { val a = PI + j * PI / 5; drawLine(c(ORO, 0.85f), b, Offset(b.x + (r * cos(a)).toFloat(), b.y + (r * sin(a)).toFloat()), 0.8f * u) }
                drawArc(c(ORO, 0.85f), 180f, 180f, false, Offset(b.x - r, b.y - r), Size(2 * r, 2 * r), style = Stroke(1.2f * u))
                drawRect(c(ORO, 0.85f), Offset(x - 5 * u, y), Size(10 * u, 2.4f * u))
                drawPath(Path().apply { moveTo(x - 3 * u, y + 2.4f * u); lineTo(x + 3 * u, y + 2.4f * u); lineTo(x, y + 8 * u); close() }, c(ORO, 0.8f))
            }
        }

        // El techo: los casetones escalonados con sus bandas de cromo y el gran sol de rayos que nace al centro.
        drawRect(c(CREMA, if (oscuro) 0.22f else 0.65f), Offset.Zero, Size(w, techo))
        clipRect(0f, 0f, w, techo - 8 * u) {
            val sol = Offset(w / 2, -6 * u)
            for (k in 0 until 31) {
                val a = PI * (k + 0.5) / 31
                drawLine(c(if (k % 2 == 0) ORO else ARCE, if (oscuro) 0.4f else 0.55f), sol, Offset(sol.x + (w * cos(a)).toFloat(), sol.y + (w * sin(a)).toFloat()), (if (k % 2 == 0) 2.4f else 1.2f) * u)
            }
            listOf(30f, 22f, 14f).forEachIndexed { j, r -> drawArc(c(if (j == 1) CROMO else ORO, if (oscuro) 0.55f else 0.75f), 0f, 180f, true, Offset(sol.x - r * u, sol.y - r * u), Size(2 * r * u, 2 * r * u)) }
            if (oscuro) halo(sol, 60 * u, LUZ.copy(alpha = 0.03f), 5)
        }
        for (s in 0..2) drawRect(c(if (s == 1) CROMO else LACA, if (oscuro) 0.55f else 0.6f), Offset(0f, techo - 8 * u + s * 2.6f * u), Size(w, if (s == 1) 1.4f * u else 2 * u))

        // Abajo: el zócalo de laca con las tres líneas de cromo y la pista de marquetería de arce y nogal.
        drawRect(c(LACA, if (oscuro) 0.65f else 0.7f), Offset(0f, zocalo), Size(w, piso - zocalo))
        for (j in 0 until 3) drawLine(c(CROMO, if (oscuro) 0.5f else 0.7f), Offset(0f, zocalo + (5 + j * 4) * u), Offset(w, zocalo + (5 + j * 4) * u), 1 * u)
        drawRect(c(ORO, 0.6f), Offset(0f, zocalo), Size(w, 1.2f * u))
        drawRect(c(ARCE, if (oscuro) 0.35f else 0.55f), Offset(0f, piso), Size(w, h - piso))
        run {
            val lado = 9 * u; var x = 0f; var k = 0
            while (x < w && k < 80) {
                for (f in 0 until 2) if ((k + f) % 2 == 0) drawPath(Path().apply { moveTo(x, piso + f * lado + lado / 2); lineTo(x + lado / 2, piso + f * lado); lineTo(x + lado, piso + f * lado + lado / 2); lineTo(x + lado / 2, piso + (f + 1) * lado); close() }, c(NOGAL, if (oscuro) 0.4f else 0.45f))
                x += maxOf(lado, 1f); k++
            }
        }
        drawLine(c(CROMO, 0.6f), Offset(0f, piso), Offset(w, piso), 1.2f * u)

        // La palmera en su jardinera de cromo y laca, en la esquina izquierda.
        run {
            val x = 16 * u; val y = zocalo + 6 * u
            for (j in 0 until 7) {
                val a = PI * (0.08 + 0.84 * j / 6)
                val punta = Offset(x - (30 * u * cos(a)).toFloat(), y - 46 * u - (24 * u * sin(a)).toFloat())
                drawPath(Path().apply { moveTo(x, y - 30 * u); quadraticTo((x + punta.x) / 2, punta.y - 10 * u, punta.x, punta.y + 10 * u) }, c(PALMA, if (oscuro) 0.45f else 0.6f), style = Stroke(2.6f * u, cap = StrokeCap.Round))
            }
            drawLine(c(Color(0xFF6A5234), 0.6f), Offset(x, y - 2 * u), Offset(x, y - 32 * u), 2.2f * u)
            drawRect(c(LACA, 0.8f), Offset(x - 11 * u, y - 4 * u), Size(22 * u, h - y))
            for (j in 0 until 3) drawRect(c(CROMO, 0.75f), Offset(x - 11 * u, y - 2 * u + j * 5 * u), Size(22 * u, 1.2f * u))
        }
        // La lámpara de pie de la derecha, con su pantalla escalonada (de noche, encendida).
        run {
            val x = w - 16 * u; val y = zocalo - 30 * u
            if (oscuro) halo(Offset(x, y + 4 * u), 40 * u, LUZ.copy(alpha = 0.035f), 5)
            drawLine(c(CROMO, 0.75f), Offset(x, y + 8 * u), Offset(x, h - 4 * u), 1.6f * u)
            drawRect(c(LACA, 0.8f), Offset(x - 8 * u, h - 5 * u), Size(16 * u, 3 * u))
            for (s in 0 until 3) {
                val an = (10 + s * 5) * u
                drawRect(if (oscuro) mezcla(e.fondo, LUZ, 0.75f - s * 0.1f) else c(CREMA, 0.9f), Offset(x - an / 2, y + s * 4 * u), Size(an, 4 * u))
                drawLine(c(ORO, 0.8f), Offset(x - an / 2, y + s * 4 * u + 4 * u), Offset(x + an / 2, y + s * 4 * u + 4 * u), 0.8f * u)
            }
            drawCircle(c(CORAL, 0.7f), 1.6f * u, Offset(x, y - 2 * u))
        }
    }

    // ---------------------------------------------------------------- las campañas

    /**
     * El trasatlántico que cruza el mapa, de lado y hacia la derecha: el casco de laca con su proa lanzada, la franja de oro
     * y la línea de flotación coral, la fila de ojos de buey, las dos cubiertas de crema con sus ventanas, las tres chimeneas
     * inclinadas hacia atrás con su remate negro, los dos mástiles con el cable entre ellos y, en el de proa, el gallardete
     * del color de acento; el humo se va hacia atrás y el agua se abre en la proa. De noche, los ojos de buey y las ventanas
     * se encienden y el casco lleva un filo de cromo para que se lea sobre el mapa oscuro.
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density; val p = e.punto
        sombraMarcadorContemporanea(p, 30 * u, e.salto, oscuro)
        // El agua: dos rizos quietos bajo el barco.
        drawArc(t(JADE_LUZ, oscuro).copy(alpha = 0.6f), 200f, 140f, false, Offset(p.x - 18 * u, p.y - 2.6f * u), Size(9 * u, 3 * u), style = Stroke(0.7f * u))
        drawArc(t(JADE_LUZ, oscuro).copy(alpha = 0.6f), 200f, 140f, false, Offset(p.x + 12 * u, p.y - 1.8f * u), Size(8 * u, 3 * u), style = Stroke(0.7f * u))
        val o = Offset(p.x, p.y - subidaContemporanea(e.salto))
        fun x(v: Float) = o.x + v * u
        fun y(v: Float) = o.y + v * u
        val laca = t(LACA, oscuro); val crema = t(CREMA, oscuro); val oro = t(ORO, oscuro); val coral = t(CORAL, oscuro)
        val luz = if (oscuro) LUZ else t(Color(0xFF3A4048), false)
        // El humo de las chimeneas.
        listOf(Triple(-6.6f, -24.4f, 1.8f), Triple(-10f, -25.6f, 2.3f), Triple(-14.4f, -25.8f, 2.6f)).forEachIndexed { i, (px, py, rr) ->
            drawCircle(t(CROMO, oscuro).copy(alpha = 0.7f - i * 0.15f), rr * u, Offset(x(px), y(py)))
        }
        // Los mástiles, el cable y el gallardete de acento.
        drawLine(laca, Offset(x(11f), y(-9f)), Offset(x(11.6f), y(-22.4f)), 0.6f * u)
        drawLine(laca, Offset(x(-12f), y(-9f)), Offset(x(-11.6f), y(-19.6f)), 0.6f * u)
        drawLine(t(CROMO_OSC, oscuro), Offset(x(-11.6f), y(-19.6f)), Offset(x(11.6f), y(-22.4f)), 0.35f * u)
        drawPath(trazoContemporanea(o, u, true, 11.6f, -22.4f, 16.4f, -21.4f, 11.6f, -20.4f), e.acento)
        // Las chimeneas inclinadas.
        listOf(-5.6f, -0.4f, 4.8f).forEach { fx ->
            val ch = trazoContemporanea(o, u, true, fx - 1.6f, -14.4f, fx + 1.6f, -14.4f, fx + 0.6f, -21.6f, fx - 2.6f, -21.6f)
            if (oscuro) drawPath(ch, CROMO.copy(alpha = 0.4f), style = Stroke(0.8f * u))
            drawPath(ch, coral)
            drawPath(trazoContemporanea(o, u, true, fx - 2.4f, -20.2f, fx + 0.8f, -20.2f, fx + 0.6f, -21.6f, fx - 2.6f, -21.6f), laca)
            drawLine(oro, Offset(x(fx - 1.9f), y(-17.4f)), Offset(x(fx + 1.2f), y(-17.4f)), 0.5f * u)
        }
        // Las cubiertas con sus ventanas.
        drawRoundRect(crema, Offset(x(-10.6f), y(-12.2f)), Size(20.6f * u, 3.4f * u), CornerRadius(0.6f * u))
        drawRoundRect(crema, Offset(x(-7.6f), y(-14.8f)), Size(14.6f * u, 2.8f * u), CornerRadius(0.6f * u))
        for (k in 0 until 9) drawRect(luz.copy(alpha = if (oscuro) 0.95f else 0.55f), Offset(x(-9.4f + k * 2.1f), y(-11.2f)), Size(1.1f * u, 1f * u))
        for (k in 0 until 6) drawRect(luz.copy(alpha = if (oscuro) 0.8f else 0.45f), Offset(x(-6.4f + k * 2.2f), y(-13.8f)), Size(1f * u, 0.8f * u))
        // El casco: la laca, la proa lanzada, la popa redonda, el filete de oro y la línea de flotación.
        val casco = Path().apply {
            moveTo(x(-14.6f), y(-9f)); lineTo(x(15.6f), y(-9.8f)); lineTo(x(12.4f), y(-2.6f)); lineTo(x(-11.6f), y(-2.6f))
            quadraticTo(x(-14.6f), y(-3.6f), x(-14.6f), y(-9f)); close()
        }
        if (oscuro) drawPath(casco, CROMO.copy(alpha = 0.5f), style = Stroke(1f * u))
        drawPath(casco, laca)
        drawPath(trazoContemporanea(o, u, true, -13.4f, -4.4f, 13.2f, -4.4f, 12.4f, -2.6f, -11.6f, -2.6f), coral)
        drawLine(oro, Offset(x(-14.4f), y(-8.2f)), Offset(x(15f), y(-8.9f)), 0.6f * u)
        for (k in 0 until 12) {
            val c = Offset(x(-11.6f + k * 2.1f), y(-6.4f))
            if (oscuro) drawCircle(LUZ.copy(alpha = 0.18f), 1.2f * u, c)
            drawCircle(if (oscuro) LUZ else t(CREMA, false), 0.55f * u, c)
        }
        // La ola de la proa.
        drawPath(Path().apply { moveTo(x(12.6f), y(-2.6f)); quadraticTo(x(15.6f), y(-3.4f), x(17.6f), y(-1.8f)) }, Color.White.copy(alpha = if (oscuro) 0.5f else 0.8f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
    }

    /**
     * La llegada a la ciudad: el rascacielos escalonado de laca con sus pilastras de cromo, los filos de oro en cada
     * retranqueo, la corona de arcos con sus ventanas triangulares y la aguja; detrás, el sol de rayos de oro, y abajo, la
     * puerta en arco y la acera. De día, sus ventanas reflejan el cielo; de noche, se encienden casi todas, la corona brilla
     * y dos reflectores cruzan sus haces por encima.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val b = pieVictoriaContemporanea(e.meta, 52 * u, 59 * u)
        fun x(v: Float) = b.x + v * u
        fun y(v: Float) = b.y + v * u
        val laca = t(LACA_LUZ, oscuro); val oro = if (oscuro) ORO_CLARO else ORO; val cromo = t(CROMO, oscuro)
        // Los reflectores de noche.
        if (oscuro) listOf(-1f, 1f).forEach { s ->
            drawPath(trazoContemporanea(b, u, true, s * 13f, -1f, -s * 22f, -58f, -s * 13f, -58f, s * 11.6f, -1f), LUZ.copy(alpha = 0.1f))
        }
        // El sol de rayos.
        val sol = Offset(x(0f), y(-22f))
        if (oscuro) halo(sol, 26 * u, ORO_CLARO.copy(alpha = 0.04f), 5)
        for (k in 0 until 15) {
            val a = (PI + (k + 0.5) * PI / 15).toFloat()
            val ancho = (if (k % 2 == 0) 1.8f else 0.9f) * u; val l = if (k % 2 == 0) 25f else 21f
            drawLine(oro.copy(alpha = if (oscuro) 0.7f else 0.55f), Offset(sol.x + 13 * u * cos(a), sol.y + 13 * u * sin(a)), Offset(sol.x + l * u * cos(a), sol.y + l * u * sin(a)), ancho)
        }
        // La torre: los tres cuerpos escalonados con sus filos de oro.
        val cuerpos = listOf(Rect(x(-11f), y(-17f), x(11f), y(0f)), Rect(x(-8f), y(-31f), x(8f), y(-17f)), Rect(x(-5f), y(-41f), x(5f), y(-31f)))
        cuerpos.forEachIndexed { i, r ->
            drawRect(laca, r.topLeft, r.size)
            drawRect(t(LACA, oscuro), Offset(r.right - r.width * 0.18f, r.top), Size(r.width * 0.18f, r.height))
            drawLine(oro, Offset(r.left - 0.6f * u, r.top), Offset(r.right + 0.6f * u, r.top), 1 * u)
            // Las ventanas, en columnas entre las pilastras.
            val cols = 6 - i * 2; val filas = (r.height / (2.4f * u)).toInt().coerceIn(1, 8)
            for (cx in 0 until cols) for (fy in 0 until filas) {
                if (i == 0 && fy >= filas - 2 && cx in (cols / 2 - 1)..(cols / 2)) continue
                val encendida = azar(31 + i, cx * 11 + fy) > 0.28f
                val col = if (oscuro) (if (encendida) LUZ else LUZ.copy(alpha = 0.18f)) else t(VIDRIO, false).copy(alpha = if (encendida) 0.75f else 0.4f)
                drawRect(col, Offset(r.left + (cx + 0.3f) * r.width / cols, r.top + 1.4f * u + fy * 2.4f * u), Size(r.width / cols * 0.42f, 1.2f * u))
            }
            for (k in 0..cols) drawLine(cromo.copy(alpha = 0.55f), Offset(r.left + k * r.width / cols - 0.1f * r.width / cols, r.top + 0.8f * u), Offset(r.left + k * r.width / cols - 0.1f * r.width / cols, r.bottom), 0.45f * u)
        }
        // La corona de arcos con sus ventanas triangulares y la aguja.
        if (oscuro) halo(Offset(x(0f), y(-44f)), 8 * u, LUZ.copy(alpha = 0.1f), 3)
        listOf(Triple(-41f, 5f, laca), Triple(-44.4f, 3.6f, cromo), Triple(-47f, 2.3f, laca)).forEach { (cy, r, col) ->
            drawArc(col, 180f, 180f, true, Offset(x(-r), y(cy - r)), Size(2 * r * u, 2 * r * u))
            drawArc(oro, 180f, 180f, false, Offset(x(-r), y(cy - r)), Size(2 * r * u, 2 * r * u), style = Stroke(0.5f * u))
        }
        for (k in 0 until 5) {
            val a = (PI + (k + 0.5) * PI / 5).toFloat(); val c = Offset(x(0f) + 4.2f * u * cos(a), y(-41f) + 4.2f * u * sin(a))
            drawPath(Path().apply { moveTo(c.x, c.y - 0.9f * u); lineTo(c.x + 0.6f * u, c.y + 0.6f * u); lineTo(c.x - 0.6f * u, c.y + 0.6f * u); close() }, if (oscuro) LUZ else oro)
        }
        drawPath(trazoContemporanea(b, u, true, -0.7f, -49f, 0.7f, -49f, 0f, -58f), t(CROMO_LUZ, oscuro))
        // La puerta en arco y la acera.
        drawPath(Path().apply { moveTo(x(-2.6f), y(0f)); lineTo(x(-2.6f), y(-4f)); quadraticTo(x(0f), y(-7.4f), x(2.6f), y(-4f)); lineTo(x(2.6f), y(0f)); close() }, if (oscuro) LUZ else oro)
        drawRect(oro, Offset(x(-3.6f), y(-6.6f)), Size(7.2f * u, 0.7f * u))
        drawLine(oro, Offset(x(-16f), y(0.4f)), Offset(x(16f), y(0.4f)), 1.2f * u)
    }

    // ---------------------------------------------------------------- objetos de Tu año

    /**
     * Art déco: la luna creciente con sus líneas de velocidad y una estrella de cuatro puntas, el zapato de dos tonos con
     * su tacón, y la mancuerna escalonada como un zigurat, de pura geometría.
     */
    override fun DrawScope.objetoAnio(e: EstadoObjeto, oscuro: Boolean) {
        val r = e.radio; val x = e.centro.x; val y = e.centro.y; val u = density
        val col = e.color; val fondo = e.fondo
        fun p(a: Float, b: Float) = Offset(x + a * r, y + b * r)
        fun g(f: Float) = maxOf(r * f, 0.8f * u)
        when (e.forma) {
            "luna" -> {
                drawPath(crecienteContemporanea(p(0.14f, 0f), r * 0.8f, Offset(r * 0.36f, -r * 0.22f), r * 0.66f), col)
                drawPath(estrella(p(0.52f, -0.24f), r * 0.28f, 4, 0.32f), col)
                listOf(0f to -0.98f, 0.26f to -0.88f, 0.52f to -0.76f).forEach { (b, a) ->
                    drawRect(col, Offset(x + a * r, y + b * r - g(0.05f)), Size(r * 0.5f, 2 * g(0.05f)))
                }
            }
            "huella" -> {
                val zapato = trazoAnioContemporanea(e.centro, r) {
                    m(-0.8f, -0.3f); l(-0.25f, -0.22f); q(0.15f, -0.4f, 0.55f, -0.18f); q(0.97f, 0f, 0.94f, 0.22f)
                    l(0.92f, 0.3f); l(-0.3f, 0.3f); l(-0.4f, 0.22f); l(-0.55f, 0.22f); l(-0.6f, 0.44f); l(-0.84f, 0.44f); l(-0.87f, 0.2f)
                    q(-0.93f, -0.05f, -0.8f, -0.3f); z()
                }
                drawPath(zapato, col)
                // El empeine claro entre la puntera y el talón, como en los zapatos de dos tonos.
                clipPath(zapato) { drawRect(mezcla(col, fondo, 0.62f), p(-0.62f, -0.6f), Size(r * 0.9f, r * 0.82f)) }
                if (r > 7 * u) listOf(0.4f, 0.5f, 0.6f).forEach { a -> drawCircle(fondo, maxOf(r * 0.035f, 0.4f * u), p(a, 0.05f)) }
            }
            "pesa" -> {
                val b = g(0.08f)
                drawRect(col, Offset(x - r * 0.45f, y - b), Size(r * 0.9f, 2 * b))
                for (s in listOf(-1f, 1f)) {
                    // Tres escalones de afuera hacia adentro, cada uno más alto.
                    listOf(Triple(0.76f, 0.92f, 0.32f), Triple(0.6f, 0.78f, 0.56f), Triple(0.42f, 0.62f, 0.82f)).forEach { (a0, a1, h) ->
                        val izq = if (s < 0) -a1 else a0
                        drawRect(col, p(izq, -h), Size(r * (a1 - a0), r * 2 * h))
                    }
                    if (r > 6 * u) drawLine(fondo, p(s * 0.52f, -0.58f), p(s * 0.52f, 0.58f), g(0.04f))
                }
            }
            else -> {}
        }
    }
}

/**
 * Botánico: el trofeo se apoya en el mueble bajo de roble del naturalista, sobre su peana de hierro con los pies en
 * voluta: el tablero, dos cajones de semillas con sus portatarjetas y sus tiradores del metal del nivel, una fronda de
 * helecho que se asoma por un lado y la hiedra que baja por el otro. En el hall, el velador de hierro colado de las
 * macetas, con el tablero de roble y la roseta de hojas del metal entre las volutas. La placa es la etiqueta de herbario
 * con su doble filete verde, en el marco del metal con sus dos remaches.
 */
object LogrosBotanico : DisenoLogros {
    private val ROBLE = Color(0xFF8A6A42)
    private val ROBLE_LUZ = Color(0xFFA8865A)
    private val ROBLE_VETA = Color(0xFF5E4528)
    private val HIERRO = Color(0xFF24382C)
    private val HIERRO_LUZ = Color(0xFF4A6553)
    private val PAPEL = Color(0xFFF1EAD6)
    private val TINTA = Color(0xFF2A2A22)
    private val TINTA_VERDE = Color(0xFF24442E)
    private val HELECHO = Color(0xFF4F7D3E)
    private val HOJA_CLARA = Color(0xFF86A95B)

    // salón: colores
    private val CIELO = Color(0xFFD6E6E0)
    private val CIELO_NOCHE = Color(0xFF14262A)
    private val VIDRIO = Color(0xFFE6F2EE)
    private val BARRO = Color(0xFFB0603A)
    private val BARRO_LUZ = Color(0xFFD08A5E)
    private val BALDOSA = Color(0xFFC9B48E)
    private val PALMA = Color(0xFF3F7A3A)
    private val FLOR = Color(0xFFC8486A)
    private val LATON = Color(0xFFC9A04E)
    private val LUMBRE = Color(0xFFFFCF7A)

    /** El color en modo oscuro: más hundido, como en su biblioteca. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0B0D0A), 0.34f) else c

    // ---------------------------------------------------------------- pedestales y placa

    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val fino = h >= 90 * u
        val hl = maxOf(0.6f * u, h * 0.004f)
        fun c(x: Color) = t(x, oscuro)
        val tablero = Rect(w * 0.09f, h * 0.775f, w * 0.91f, h * 0.802f)
        val cuerpo = Rect(w * 0.12f, h * 0.802f, w * 0.88f, h * 0.928f)
        val peana = Rect(w * 0.1f, h * 0.928f, w * 0.9f, h * 0.944f)
        if (!e.hecho) {
            silueta(oscuro) { addRect(tablero); addRect(cuerpo); addRect(peana); addRect(Rect(w * 0.12f, peana.bottom, w * 0.2f, h * 0.955f)); addRect(Rect(w * 0.8f, peana.bottom, w * 0.88f, h * 0.955f)) }
            return
        }
        sombraPie(w / 2, peana.width, h * 0.958f, h * 0.022f, if (oscuro) 0.5f else 0.24f)
        // La peana de hierro con los pies en voluta.
        drawRect(c(HIERRO), peana.topLeft, peana.size)
        drawRect(c(HIERRO_LUZ), peana.topLeft, Size(peana.width, hl * 0.7f))
        listOf(-1f, 1f).forEach { s ->
            val x = w / 2 + s * w * 0.34f; val y = peana.bottom
            drawRect(c(HIERRO), Offset(x - w * 0.04f, y - hl * 0.5f), Size(w * 0.08f, h * 0.011f))
            if (fino) drawArc(c(HIERRO), if (s < 0) 90f else -90f, 180f * s, false, Offset(x + s * w * 0.04f - h * 0.007f, y - h * 0.002f), Size(h * 0.014f, h * 0.014f), style = Stroke(hl))
        }
        // El cuerpo de roble veteado y los dos cajones de semillas.
        drawRect(c(ROBLE), cuerpo.topLeft, cuerpo.size)
        if (fino) vetasEn(cuerpo, c(ROBLE_VETA).copy(alpha = 0.45f), e.semilla, densidad = 0.7f, grosor = 0.6f)
        listOf(Rect(w * 0.16f, cuerpo.top + cuerpo.height * 0.14f, w * 0.485f, cuerpo.bottom - cuerpo.height * 0.14f),
            Rect(w * 0.515f, cuerpo.top + cuerpo.height * 0.14f, w * 0.84f, cuerpo.bottom - cuerpo.height * 0.14f)).forEach { cj ->
            drawRect(Color.Black.copy(alpha = 0.3f), cj.topLeft - Offset(hl * 0.6f, hl * 0.6f), Size(cj.width + hl * 1.2f, cj.height + hl * 1.2f))
            drawRect(c(ROBLE_LUZ), cj.topLeft, cj.size)
            if (fino) vetasEn(cj, c(ROBLE_VETA).copy(alpha = 0.35f), e.semilla + cj.left.toInt(), densidad = 0.6f, grosor = 0.5f)
            drawRect(c(ROBLE_VETA).copy(alpha = 0.5f), Offset(cj.left, cj.bottom - hl), Size(cj.width, hl))
            // El portatarjetas del metal con su tarjeta y, debajo, el tirador.
            val pt = Rect(cj.center.x - cj.width * 0.24f, cj.top + cj.height * 0.14f, cj.center.x + cj.width * 0.24f, cj.top + cj.height * 0.48f)
            chapa(e, pt, hl * 0.5f)
            drawRect(c(PAPEL), Offset(pt.left + pt.width * 0.12f, pt.top + pt.height * 0.22f), Size(pt.width * 0.76f, pt.height * 0.52f))
            if (fino) drawLine(c(TINTA_VERDE).copy(alpha = 0.6f), Offset(pt.left + pt.width * 0.22f, pt.center.y), Offset(pt.right - pt.width * 0.3f, pt.center.y), hl * 0.5f)
            boton(e, Offset(cj.center.x, cj.top + cj.height * 0.72f), maxOf(cj.height * 0.11f, 0.8f * u))
        }
        // El tablero, con su canto de luz.
        drawRect(c(ROBLE_LUZ), tablero.topLeft, tablero.size)
        drawRect(c(mezcla(ROBLE_LUZ, Color.White, 0.2f)), tablero.topLeft, Size(tablero.width, tablero.height * 0.3f))
        drawRect(c(ROBLE_VETA), Offset(tablero.left, tablero.bottom - hl), Size(tablero.width, hl))
        if (fino) {
            // La fronda de helecho que se asoma por la derecha, curvada como un báculo.
            val fronda = Path().apply { moveTo(w * 0.86f, tablero.bottom + cuerpo.height * 0.2f); quadraticTo(w * 0.97f, tablero.top - h * 0.01f, w * 0.92f, h * 0.745f) }
            drawPath(fronda, c(HELECHO), style = Stroke(hl, cap = StrokeCap.Round))
            for (k in 1..6) {
                val f = k / 7f
                val x = w * (0.86f + 0.11f * f - 0.06f * f * f * 2); val y = tablero.bottom + cuerpo.height * 0.2f - (cuerpo.height * 0.2f + h * 0.035f) * f
                val l = h * 0.012f * (1.2f - f * 0.6f)
                drawOval(c(HELECHO), Offset(x - l * 1.6f, y - l * 0.4f), Size(l * 1.6f, l * 0.8f))
                drawOval(c(HOJA_CLARA), Offset(x, y - l * 0.4f), Size(l * 1.6f, l * 0.8f))
            }
            // La hiedra que baja por la izquierda.
            drawPath(Path().apply { moveTo(w * 0.13f, tablero.bottom); quadraticTo(w * 0.09f, cuerpo.center.y, w * 0.115f, cuerpo.bottom) }, c(HELECHO), style = Stroke(hl * 0.7f))
            listOf(0.15f, 0.45f, 0.78f).forEachIndexed { i, f ->
                val p = Offset(w * (0.115f - 0.02f * sin(f * 3.1f)), tablero.bottom + cuerpo.height * f); val r = h * 0.009f
                drawPath(estrella(p, r * 1.3f, 3, 0.55f, giro = 90f + i * 20f), c(if (i % 2 == 0) HELECHO else HOJA_CLARA))
            }
        }
    }

    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val m = tonos(e, oscuro)
        fun c(x: Color) = t(x, oscuro)
        sombraPie(w / 2, w * 0.8f, h * 0.95f, h * 0.14f, if (oscuro) 0.45f else 0.2f)
        val hierro = c(HIERRO)
        // Las patas de hierro colado, un poco abiertas, con su voluta al pie.
        listOf(-1f, 1f).forEach { s ->
            val x0 = w / 2 + s * w * 0.3f; val x1 = w / 2 + s * w * 0.36f
            drawLine(hierro, Offset(x0, h * 0.3f), Offset(x1, h * 0.9f), 2 * u, StrokeCap.Round)
            drawArc(hierro, if (s < 0) 0f else 180f, 270f * s, false, Offset(x1 - (if (s < 0) 3.6f * u else 0f), h * 0.78f), Size(3.6f * u, 3.6f * u), style = Stroke(1.2f * u))
        }
        // Las volutas que se juntan al centro y la roseta de cuatro hojas del metal del nivel.
        val cc = Offset(w / 2, h * 0.58f)
        listOf(-1f, 1f).forEach { s ->
            drawPath(Path().apply { moveTo(w / 2 + s * w * 0.31f, h * 0.4f); cubicTo(w / 2 + s * w * 0.2f, h * 0.95f, w / 2 + s * w * 0.06f, h * 0.2f, cc.x + s * 2 * u, cc.y) }, hierro, style = Stroke(1.1f * u))
        }
        val r = h * 0.27f
        for (k in 0 until 4) rotate(45f + k * 90f, cc) {
            drawOval(m.metalOscuro, Offset(cc.x - r * 0.28f, cc.y - r * 1.05f), Size(r * 0.56f, r * 1.05f))
            drawOval(if (k % 2 == 0) m.base else m.brillo, Offset(cc.x - r * 0.2f, cc.y - r * 0.95f), Size(r * 0.4f, r * 0.9f))
        }
        boton(m, cc, r * 0.32f)
        // El faldón de hierro y el tablero de roble.
        drawRect(hierro, Offset(w * 0.12f, h * 0.2f), Size(w * 0.76f, h * 0.14f))
        drawRect(c(HIERRO_LUZ), Offset(w * 0.12f, h * 0.2f), Size(w * 0.76f, 0.6f * u))
        val tablero = Rect(w * 0.06f, 0f, w * 0.94f, h * 0.21f)
        drawRect(c(ROBLE_LUZ), tablero.topLeft, tablero.size)
        vetasEn(tablero, c(ROBLE_VETA).copy(alpha = 0.5f), e.semilla, densidad = 0.9f, grosor = 0.5f)
        drawRect(c(ROBLE_VETA), Offset(tablero.left, tablero.bottom - 0.8f * u), Size(tablero.width, 0.8f * u))
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFEDE6CF) else TINTA
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFF9BC06C) else TINTA_VERDE

    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val h = size.height
        val m = tonos(e, oscuro)
        val r0 = Rect(Offset.Zero, size)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.2f), Offset(0.5f * u, 1.5f * u), size, CornerRadius(1.5f * u))
        // El marco del metal del nivel, la tarjeta y el doble filete verde de las etiquetas de herbario.
        drawRoundRect(m.metalOscuro, Offset.Zero, size, CornerRadius(1.5f * u))
        filete(m, redondo(r0.deflate(1.2f * u), 1 * u), 2.2f * u)
        val cara = r0.deflate(2.4f * u)
        drawRect(if (oscuro) Color(0xFF23261F) else PAPEL, cara.topLeft, cara.size)
        val verde = if (oscuro) Color(0xFF5E7F4A) else TINTA_VERDE
        drawRect(verde, cara.topLeft + Offset(1.6f * u, 1.6f * u), Size(cara.width - 3.2f * u, cara.height - 3.2f * u), style = Stroke(0.8f * u))
        drawRect(verde.copy(alpha = 0.8f), cara.topLeft + Offset(2.8f * u, 2.8f * u), Size(cara.width - 5.6f * u, cara.height - 5.6f * u), style = Stroke(0.4f * u))
        // Los dos remaches del marco, a los lados.
        listOf(1.2f * u, size.width - 1.2f * u).forEach { x -> boton(m, Offset(x, h / 2), 1f * u) }
    }

    // ---------------------------------------------------------------- el salón del hall

    /**
     * El invernadero de hierro y vidrio: al fondo, tras los vidrios, el cielo (de noche, azul verdoso con la luna) y las
     * siluetas del jardín de afuera; sobre cada franja, la bóveda de cristal con sus costillas en abanico y sus montantes a
     * los lados. Entre las franjas, columnas delgadas de hierro verde con sus consolas caladas, una enredadera que sube en
     * espiral y, en cada piso, la cesta colgante de helechos. De día, los rayos de sol entran en diagonal. Abajo, el tubo de
     * la calefacción, la estantería de listones con macetas de barro (un cactus, un helecho, una orquídea; de noche, el
     * farol de queroseno encendido) y el piso de baldosas hidráulicas. A la izquierda sube la palmera de abanico en su tina; a
     * la derecha, la regadera de latón y la pila de macetas.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) = clipRect { salon(e, oscuro) }

    /** La escena del salón, recortada al lienzo por [escenaSalon]. */
    private fun DrawScope.salon(e: EstadoSalon, oscuro: Boolean) {
        val p = planta(e); val u = p.u; val w = p.w; val h = p.h
        fun c(x: Color, f: Float) = mezcla(e.fondo, t(x, oscuro), f)
        val banco = h - 46 * u; val piso = h - 16 * u

        // El cielo tras los vidrios y, abajo, las siluetas del jardín de afuera.
        val cielo = if (oscuro) listOf(CIELO_NOCHE, mezcla(CIELO_NOCHE, Color.Black, 0.5f)) else listOf(mezcla(CIELO, Color.White, 0.3f), CIELO)
        drawRect(Brush.verticalGradient(cielo.map { mezcla(e.fondo, it, if (oscuro) 0.55f else 0.5f) }, 0f, banco), Offset.Zero, Size(w, banco))
        if (oscuro) {
            val luna = Offset(w - 18 * u, 64 * u)
            halo(luna, 24 * u, Color(0xFFE6EEDA).copy(alpha = 0.03f), 4)
            drawCircle(mezcla(e.fondo, Color(0xFFE6EEDA), 0.6f), 6 * u, luna)
        }
        run {
            val sombra = c(PALMA, if (oscuro) 0.16f else 0.08f)
            for (k in 0 until 9) {
                val x = w * (k + azar(e.semilla + 2, k)) / 9; val y = banco - (20 + 50 * azar(e.semilla + 2, k + 20)) * u
                for (j in 0 until 5) {
                    val a = PI * (0.15 + 0.7 * j / 4)
                    drawPath(Path().apply { moveTo(x, banco); quadraticTo(x, y, x - (34 * u * cos(a)).toFloat(), y - (16 * u * sin(a)).toFloat()) }, sombra, style = Stroke(5 * u, cap = StrokeCap.Round))
                }
            }
        }
        // La bóveda de cristal sobre cada franja: el arco, las costillas en abanico, los montantes a los lados y sus travesaños.
        val hierro = c(if (oscuro) HIERRO_LUZ else HIERRO, 0.5f)
        val hierroFino = c(if (oscuro) HIERRO_LUZ else HIERRO, if (oscuro) 0.32f else 0.28f)
        for (i in 0 until p.n) {
            val x0 = p.lx(i) + 4 * u; val x1 = p.lx(i + 1) - 4 * u; val r = (x1 - x0) / 2; val cx = (x0 + x1) / 2
            val arriba = 6 * u
            drawPath(Path().apply { moveTo(x0, banco); lineTo(x0, arriba + r * 0.6f); arcTo(Rect(x0, arriba, x1, arriba + r * 1.2f), 180f, 180f, false); lineTo(x1, banco) }, hierro, style = Stroke(1.8f * u))
            val centro = Offset(cx, arriba + r * 0.6f)
            for (k in 1..7) { val a = PI + k * PI / 8; drawLine(hierroFino, centro + Offset((r * 0.22f * cos(a)).toFloat(), (r * 0.13f * sin(a)).toFloat()), Offset(cx + (r * cos(a)).toFloat(), centro.y + (r * 0.6f * sin(a)).toFloat()), 0.9f * u) }
            drawArc(hierroFino, 180f, 180f, false, Offset(cx - r * 0.22f, centro.y - r * 0.13f), Size(r * 0.44f, r * 0.26f), style = Stroke(1 * u))
            listOf(x0 + r * 0.2f, x1 - r * 0.2f).forEach { xx -> drawLine(hierroFino, Offset(xx, arriba + r * 0.3f), Offset(xx, banco), 0.9f * u) }
            run {
                var y = arriba + r * 0.7f; var k = 0
                while (y < banco && k < 60) { drawLine(hierroFino, Offset(x0, y), Offset(x0 + r * 0.2f, y), 0.8f * u); drawLine(hierroFino, Offset(x1 - r * 0.2f, y), Offset(x1, y), 0.8f * u); y += maxOf(28 * u, 1f); k++ }
            }
        }
        // De día, los rayos de sol que entran en diagonal por el cristal.
        if (!oscuro) for (k in 0 until 3) {
            val x = w * (0.1f + 0.33f * k)
            drawPath(Path().apply { moveTo(x, 0f); lineTo(x + 26 * u, 0f); lineTo(x + 26 * u + h * 0.35f, h); lineTo(x + h * 0.35f, h); close() }, Color(0xFFFFF6CC).copy(alpha = 0.1f))
        }

        // Las columnas de hierro entre las franjas: la enredadera en espiral, las consolas caladas y las cestas colgantes.
        for (i in 0..p.n) {
            val x = p.lx(i)
            drawRect(c(HIERRO, if (oscuro) 0.75f else 0.7f), Offset(x - 3.5f * u, 0f), Size(7 * u, banco))
            drawLine(c(HIERRO_LUZ, 0.6f), Offset(x - 2 * u, 0f), Offset(x - 2 * u, banco), 0.8f * u)
            listOf(-1f, 1f).forEach { s ->
                drawPath(Path().apply { moveTo(x + s * 3.5f * u, 60 * u); quadraticTo(x + s * 4 * u, 34 * u, x + s * 20 * u, 30 * u); quadraticTo(x + s * 10 * u, 40 * u, x + s * 3.5f * u, 70 * u) }, hierro, style = Stroke(1.4f * u))
                drawCircle(hierro, 3 * u, Offset(x + s * 9 * u, 44 * u), style = Stroke(1 * u))
            }
            // La enredadera: un tallo en zigzag suave y hojas alternas, hasta media altura.
            val tope = maxOf(banco - 260 * u, 80 * u)
            val tallo = Path().apply { moveTo(x, banco); var y = banco; var s = 1f; var k = 0; while (y > tope && k < 40) { quadraticTo(x + s * 7 * u, y - 9 * u, x, y - 18 * u); y -= maxOf(18 * u, 1f); s = -s; k++ } }
            drawPath(tallo, c(HELECHO, if (oscuro) 0.5f else 0.6f), style = Stroke(1.1f * u))
            run {
                var y = banco - 8 * u; var k = 0
                while (y > tope && k < 30) {
                    val s = if (k % 2 == 0) 1f else -1f
                    drawOval(c(if (k % 3 == 0) HOJA_CLARA else HELECHO, if (oscuro) 0.5f else 0.65f), Offset(x + s * 3 * u - (if (s < 0) 7 * u else 0f), y - 2.5f * u), Size(7 * u, 5 * u))
                    if (k % 5 == 2) drawCircle(c(FLOR, if (oscuro) 0.5f else 0.75f), 2 * u, Offset(x - s * 5 * u, y - 4 * u))
                    y -= maxOf(9 * u, 1f); k++
                }
            }
            for (k in 0 until p.filas) {
                val y = p.y(k) + 36 * u
                if (y < 70 * u || y > banco - 60 * u) continue
                listOf(-1f, 1f).forEach { s ->
                    if ((i == 0 && s < 0) || (i == p.n && s > 0)) return@forEach
                    // La cesta colgante: el brazo, las tres cadenas, la cesta de alambre y los helechos que cuelgan.
                    val cx = x + s * 22 * u; val cy = y + 18 * u
                    drawLine(hierro, Offset(x, y), Offset(cx, y), 1.2f * u)
                    listOf(-6f, 0f, 6f).forEach { dx -> drawLine(c(HIERRO, 0.45f), Offset(cx, y), Offset(cx + dx * u, cy), 0.6f * u) }
                    for (j in 0 until 7) {
                        val a = PI * (0.05 + 0.9 * j / 6)
                        drawPath(Path().apply { moveTo(cx, cy); quadraticTo(cx - (12 * u * cos(a)).toFloat(), cy + 2 * u, cx - (13 * u * cos(a)).toFloat(), cy + (8 + 12 * sin(a)).toFloat() * u) },
                            c(if (j % 2 == 0) HELECHO else HOJA_CLARA, if (oscuro) 0.5f else 0.65f), style = Stroke(2 * u, cap = StrokeCap.Round))
                    }
                    drawArc(c(HIERRO, 0.6f), 0f, 180f, true, Offset(cx - 7 * u, cy - 4 * u), Size(14 * u, 9 * u))
                    drawArc(c(HIERRO_LUZ, 0.5f), 0f, 180f, false, Offset(cx - 5 * u, cy - 2.5f * u), Size(10 * u, 6 * u), style = Stroke(0.6f * u))
                }
            }
        }

        // El tubo de la calefacción y la estantería de listones de roble con sus macetas.
        drawLine(c(HIERRO, 0.6f), Offset(0f, banco - 4 * u), Offset(w, banco - 4 * u), 4 * u)
        drawLine(c(HIERRO_LUZ, 0.5f), Offset(0f, banco - 5.4f * u), Offset(w, banco - 5.4f * u), 0.8f * u)
        for (j in 0 until 3) drawRect(c(ROBLE, if (oscuro) 0.5f else 0.6f), Offset(0f, banco + j * 4.5f * u), Size(w, 3.4f * u))
        drawRect(c(ROBLE_VETA, 0.5f), Offset(0f, banco + 13.5f * u), Size(w, piso - banco - 13.5f * u))
        run { var x = 10 * u; var k = 0; while (x < w && k < 30) { drawRect(c(ROBLE_VETA, 0.65f), Offset(x, banco + 13 * u), Size(3 * u, piso - banco - 13 * u)); x += maxOf(60 * u, 1f); k++ } }
        for (i in 0..p.n) {
            val x = p.lx(i).coerceIn(30 * u, w - 30 * u)
            val tipo = (i + e.semilla).mod(3)
            val y = banco
            drawPath(Path().apply { moveTo(x - 6 * u, y - 10 * u); lineTo(x + 6 * u, y - 10 * u); lineTo(x + 4.6f * u, y); lineTo(x - 4.6f * u, y); close() }, c(BARRO, if (oscuro) 0.55f else 0.7f))
            drawRect(c(BARRO_LUZ, 0.7f), Offset(x - 6.6f * u, y - 11.5f * u), Size(13.2f * u, 2.4f * u))
            when (tipo) {
                0 -> { // un cactus de brazos
                    drawRoundRect(c(PALMA, 0.65f), Offset(x - 2.5f * u, y - 30 * u), Size(5 * u, 20 * u), CornerRadius(2.5f * u))
                    drawRoundRect(c(PALMA, 0.65f), Offset(x + 2 * u, y - 24 * u), Size(5 * u, 3 * u), CornerRadius(1.5f * u))
                    drawRoundRect(c(PALMA, 0.65f), Offset(x + 4 * u, y - 30 * u), Size(3 * u, 8 * u), CornerRadius(1.5f * u))
                    drawCircle(c(FLOR, 0.8f), 1.6f * u, Offset(x, y - 31 * u))
                }
                1 -> for (j in 0 until 6) { // un helecho
                    val a = PI * (0.1 + 0.8 * j / 5)
                    drawPath(Path().apply { moveTo(x, y - 11 * u); quadraticTo(x - (8 * u * cos(a)).toFloat(), y - 28 * u, x - (16 * u * cos(a)).toFloat(), y - 14 * u - (10 * u * sin(a)).toFloat()) }, c(HELECHO, 0.6f), style = Stroke(1.8f * u, cap = StrokeCap.Round))
                }
                else -> { // una orquídea con su tutor
                    drawLine(c(ROBLE_VETA, 0.7f), Offset(x + 1 * u, y - 11 * u), Offset(x + 1 * u, y - 34 * u), 0.8f * u)
                    drawPath(Path().apply { moveTo(x + 1 * u, y - 34 * u); quadraticTo(x + 8 * u, y - 36 * u, x + 10 * u, y - 28 * u) }, c(HELECHO, 0.6f), style = Stroke(0.8f * u))
                    listOf(Offset(x + 5 * u, y - 34 * u), Offset(x + 9 * u, y - 30 * u), Offset(x + 10 * u, y - 25 * u)).forEach { q -> drawCircle(c(Color(0xFFE8C6E0), 0.8f), 2.2f * u, q); drawCircle(c(FLOR, 0.8f), 0.8f * u, q) }
                    listOf(-1f, 1f).forEach { s -> drawOval(c(PALMA, 0.6f), Offset(x - 1 * u + s * 4 * u - 3 * u, y - 15 * u), Size(8 * u, 3.4f * u)) }
                }
            }
        }
        // De noche, el farol de queroseno sobre la estantería, en la línea del medio.
        if (oscuro) {
            val x = p.lx(p.n / 2).coerceIn(30 * u, w - 30 * u) + 14 * u; val y = banco - 3 * u
            halo(Offset(x, y - 8 * u), 46 * u, LUMBRE.copy(alpha = 0.04f), 6)
            drawRect(c(LATON, 0.8f), Offset(x - 4 * u, y - 3 * u), Size(8 * u, 3 * u))
            drawOval(mezcla(e.fondo, LUMBRE, 0.8f), Offset(x - 3 * u, y - 13 * u), Size(6 * u, 10 * u))
            drawRect(c(LATON, 0.8f), Offset(x - 2 * u, y - 16 * u), Size(4 * u, 3 * u))
            drawArc(c(LATON, 0.7f), 180f, 180f, false, Offset(x - 4 * u, y - 20 * u), Size(8 * u, 8 * u), style = Stroke(0.7f * u))
        }

        // El piso de baldosas hidráulicas: el damero de barro y crema con su rombo al centro de cada una.
        drawRect(c(BALDOSA, if (oscuro) 0.4f else 0.55f), Offset(0f, piso), Size(w, h - piso))
        run {
            val lado = 8 * u; var x = 0f; var k = 0
            while (x < w && k < 80) {
                for (f in 0 until 2) {
                    val y = piso + f * lado
                    if ((k + f) % 2 == 0) drawRect(c(BARRO, if (oscuro) 0.45f else 0.5f), Offset(x, y), Size(lado, lado))
                    else drawPath(Path().apply { moveTo(x + lado / 2, y + 1.5f * u); lineTo(x + lado - 1.5f * u, y + lado / 2); lineTo(x + lado / 2, y + lado - 1.5f * u); lineTo(x + 1.5f * u, y + lado / 2); close() }, c(TINTA_VERDE, if (oscuro) 0.4f else 0.4f))
                }
                x += maxOf(lado, 1f); k++
            }
        }

        // La palmera de abanico en su tina, que sube por el borde izquierdo.
        run {
            val x = 12 * u; val y = piso - 2 * u
            val copa = Offset(x + 2 * u, y - 120 * u)
            drawPath(Path().apply { moveTo(x - 2 * u, y - 14 * u); quadraticTo(x + 6 * u, y - 60 * u, copa.x, copa.y); lineTo(copa.x + 3 * u, copa.y); quadraticTo(x + 9 * u, y - 60 * u, x + 3 * u, y - 14 * u); close() }, c(ROBLE_VETA, if (oscuro) 0.5f else 0.6f))
            for (j in 0 until 9) {
                val a = PI * (-0.1 + 1.2 * j / 8)
                val punta = Offset(copa.x - (34 * u * cos(a)).toFloat(), copa.y - (22 * u * sin(a)).toFloat())
                drawPath(Path().apply { moveTo(copa.x, copa.y); quadraticTo((copa.x + punta.x) / 2, minOf(copa.y, punta.y) - 8 * u, punta.x, punta.y) }, c(PALMA, if (oscuro) 0.45f else 0.6f), style = Stroke(3 * u, cap = StrokeCap.Round))
            }
            drawRect(c(ROBLE, 0.7f), Offset(x - 10 * u, y - 16 * u), Size(22 * u, 16 * u))
            listOf(y - 13 * u, y - 4 * u).forEach { yy -> drawRect(c(HIERRO, 0.75f), Offset(x - 10 * u, yy), Size(22 * u, 2 * u)) }
        }
        // La regadera de latón y la pila de macetas vacías a la derecha.
        run {
            val x = w - 20 * u; val y = h - 2 * u
            drawRoundRect(c(LATON, if (oscuro) 0.6f else 0.75f), Offset(x - 8 * u, y - 14 * u), Size(16 * u, 12 * u), CornerRadius(3 * u))
            drawLine(c(LATON, 0.75f), Offset(x - 7 * u, y - 10 * u), Offset(x - 18 * u, y - 20 * u), 1.6f * u)
            drawOval(c(LATON, 0.75f), Offset(x - 21 * u, y - 23 * u), Size(5 * u, 4 * u))
            drawArc(c(LATON, 0.75f), 180f, 180f, false, Offset(x - 5 * u, y - 20 * u), Size(10 * u, 10 * u), style = Stroke(1.2f * u))
            for (j in 0 until 3) drawPath(Path().apply { val b = y - 16 * u - j * 3 * u; moveTo(x + 6 * u, b); lineTo(x + 18 * u, b); lineTo(x + 16 * u, b + 6 * u); lineTo(x + 8 * u, b + 6 * u); close() }, c(if (j % 2 == 0) BARRO else BARRO_LUZ, 0.7f))
        }
    }

    // ---------------------------------------------------------------- las campañas

    /**
     * La naturalista de la expedición, de lado y hacia la derecha, a medio paso: las botas, el pantalón caqui, la camisa de
     * lino con su cinturón, la caja verde de herborizar colgada de su correa de latón, el salacot con la cinta del color de
     * acento y la red de mariposas en alto, con su aro de latón, la bolsa de tul y una mariposa rosa que se le escapa. De
     * noche lleva en la otra mano su farol encendido, con su halo.
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density; val p = e.punto
        sombraMarcadorContemporanea(p, 15 * u, e.salto, oscuro)
        val o = Offset(p.x, p.y - subidaContemporanea(e.salto))
        fun x(v: Float) = o.x + v * u
        fun y(v: Float) = o.y + v * u
        val caqui = t(Color(0xFFA08458), oscuro); val bota = t(ROBLE_VETA, oscuro); val lino = t(PAPEL, oscuro)
        val piel = t(Color(0xFFDDAA80), oscuro); val caja = t(HIERRO_LUZ, oscuro); val laton = t(LATON, oscuro)
        // La red: el palo, el aro y la bolsa de tul.
        drawLine(t(ROBLE_LUZ, oscuro), Offset(x(2.4f), y(-11f)), Offset(x(8.6f), y(-24f)), 0.8f * u, StrokeCap.Round)
        drawPath(Path().apply { moveTo(x(7.6f), y(-25.4f)); cubicTo(x(8f), y(-21f), x(10.4f), y(-18.6f), x(12.6f), y(-20.2f)); quadraticTo(x(12.8f), y(-23.6f), x(12.4f), y(-26.4f)); close() },
            Color.White.copy(alpha = if (oscuro) 0.45f else 0.7f))
        drawPath(Path().apply { moveTo(x(8.6f), y(-24f)); quadraticTo(x(10.4f), y(-21f), x(12.4f), y(-21f)) }, t(Color(0xFFB8B0A0), oscuro), style = Stroke(0.3f * u))
        rotate(-14f, Offset(x(10f), y(-25.9f))) { drawOval(laton, Offset(x(7.4f), y(-27.1f)), Size(5.2f * u, 2.4f * u), style = Stroke(0.55f * u)) }
        // La mariposa que se escapa.
        val mp = Offset(x(14.6f), y(-29f))
        listOf(-1f, 1f).forEach { s ->
            drawOval(t(FLOR, oscuro), Offset(mp.x + (if (s < 0) -2.6f else 0.1f) * u, mp.y - 2.2f * u), Size(2.5f * u, 2.2f * u))
            drawOval(mezcla(t(FLOR, oscuro), Color.White, 0.35f), Offset(mp.x + (if (s < 0) -1.8f else 0.2f) * u, mp.y - 0.2f * u), Size(1.6f * u, 1.5f * u))
        }
        drawLine(if (oscuro) Color(0xFFEDE6CF) else TINTA, Offset(mp.x, mp.y - 1.8f * u), Offset(mp.x, mp.y + 1.2f * u), 0.5f * u)
        // Las piernas a medio paso y las botas.
        drawLine(caqui, Offset(x(-1.2f), y(-9.4f)), Offset(x(-2.2f), y(-1.6f)), 1.9f * u)
        drawLine(caqui, Offset(x(0.8f), y(-9.4f)), Offset(x(2.4f), y(-1.6f)), 1.9f * u)
        drawRoundRect(bota, Offset(x(-3.4f), y(-2.4f)), Size(2.8f * u, 2.2f * u), CornerRadius(0.6f * u))
        drawRoundRect(bota, Offset(x(1.4f), y(-2.4f)), Size(2.9f * u, 2.2f * u), CornerRadius(0.6f * u))
        // El brazo de atrás: de noche, con el farol; de día, suelto.
        if (oscuro) {
            drawLine(lino, Offset(x(-1.4f), y(-16f)), Offset(x(-3.6f), y(-11.6f)), 1.5f * u, StrokeCap.Round)
            val f = Offset(x(-3.8f), y(-8.6f))
            halo(f, 8 * u, LUMBRE.copy(alpha = 0.09f), 4)
            drawLine(laton, Offset(x(-3.7f), y(-11.2f)), Offset(f.x, f.y - 2 * u), 0.4f * u)
            drawRect(laton, Offset(f.x - 1.3f * u, f.y - 2 * u), Size(2.6f * u, 0.6f * u))
            drawRect(LUMBRE, Offset(f.x - 1f * u, f.y - 1.4f * u), Size(2 * u, 2.6f * u))
            drawRect(laton, Offset(f.x - 1.3f * u, f.y + 1.2f * u), Size(2.6f * u, 0.6f * u))
        } else drawLine(lino, Offset(x(-1.4f), y(-16f)), Offset(x(-3.2f), y(-11.4f)), 1.5f * u, StrokeCap.Round)
        // El cuerpo: la camisa, el cinturón y la caja de herborizar con su correa.
        drawPath(trazoContemporanea(o, u, true, -2.6f, -17.6f, 2f, -17.6f, 2.4f, -9.2f, -2.4f, -9.2f), lino)
        drawLine(t(ROBLE_VETA, oscuro), Offset(x(-2.4f), y(-10.2f)), Offset(x(2.4f), y(-10.2f)), 0.8f * u)
        drawLine(laton, Offset(x(1.6f), y(-17.4f)), Offset(x(-3.4f), y(-11.6f)), 0.5f * u)
        drawRoundRect(caja, Offset(x(-6.6f), y(-12.8f)), Size(5 * u, 3.2f * u), CornerRadius(1.6f * u))
        drawLine(mezcla(caja, Color.White, 0.3f), Offset(x(-5.8f), y(-12.2f)), Offset(x(-2.4f), y(-12.2f)), 0.4f * u)
        drawCircle(laton, 0.45f * u, Offset(x(-1.9f), y(-11.2f)))
        // El brazo de adelante, que sostiene la red.
        drawLine(lino, Offset(x(0.6f), y(-16.2f)), Offset(x(2.6f), y(-12.6f)), 1.5f * u, StrokeCap.Round)
        drawCircle(piel, 0.8f * u, Offset(x(2.8f), y(-12.2f)))
        // La cabeza con el salacot y su cinta de acento.
        drawCircle(piel, 1.9f * u, Offset(x(0.6f), y(-19.6f)))
        drawArc(t(Color(0xFFE6DCC0), oscuro), 180f, 180f, true, Offset(x(-2.4f), y(-24.4f)), Size(5.8f * u, 6 * u))
        drawRoundRect(t(Color(0xFFD8CCAA), oscuro), Offset(x(-3.8f), y(-21.8f)), Size(8.6f * u, 1.1f * u), CornerRadius(0.5f * u))
        drawLine(e.acento, Offset(x(-2.3f), y(-22.2f)), Offset(x(3.3f), y(-22.2f)), 0.7f * u)
    }

    /**
     * La caja de Ward al final de la expedición: la vitrina de vidrio con su armazón de hierro verde, el tejado a dos aguas
     * con un panel abierto, el cajón de roble con su etiqueta escrita y sus patas; dentro, los helechos, una palma joven y la
     * orquídea en flor que llega al techo; por el panel abierto salen volando tres mariposas (una del color de acento). De
     * noche, la vitrina se ilumina por dentro y la rodean luciérnagas.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val b = pieVictoriaContemporanea(e.meta, 50 * u, 50 * u)
        fun x(v: Float) = b.x + v * u
        fun y(v: Float) = b.y + v * u
        val hierro = t(HIERRO_LUZ, oscuro); val roble = t(ROBLE, oscuro); val flor = if (oscuro) mezcla(t(FLOR, true), Color.White, 0.15f) else FLOR
        fun mariposa(c: Offset, s: Float, col: Color, giro: Float) = rotate(giro, c) {
            drawOval(col, Offset(c.x - 2.6f * s, c.y - 2.2f * s), Size(2.5f * s, 2.2f * s))
            drawOval(col, Offset(c.x + 0.1f * s, c.y - 2.2f * s), Size(2.5f * s, 2.2f * s))
            drawOval(mezcla(col, Color.White, 0.35f), Offset(c.x - 1.8f * s, c.y - 0.2f * s), Size(1.7f * s, 1.5f * s))
            drawOval(mezcla(col, Color.White, 0.35f), Offset(c.x + 0.1f * s, c.y - 0.2f * s), Size(1.7f * s, 1.5f * s))
            drawLine(if (oscuro) Color(0xFFEDE6CF) else t(TINTA, false), Offset(c.x, c.y - 1.8f * s), Offset(c.x, c.y + 1.2f * s), 0.5f * s)
        }
        // El interior: la luz de noche, los helechos, la palma y la orquídea.
        val dentro = Path().apply { moveTo(x(-15f), y(-7f)); lineTo(x(-15f), y(-24f)); lineTo(x(0f), y(-33f)); lineTo(x(15f), y(-24f)); lineTo(x(15f), y(-7f)); close() }
        drawPath(dentro, if (oscuro) LUMBRE.copy(alpha = 0.16f) else t(VIDRIO, false).copy(alpha = 0.5f))
        if (oscuro) halo(Offset(x(0f), y(-16f)), 18 * u, LUMBRE.copy(alpha = 0.035f), 4)
        listOf(-10f to 1f, -4f to -1f, 9f to -1f).forEach { (fx, s) ->
            for (k in 0 until 3) {
                val a = (-PI / 2 + s * (0.25 + k * 0.4)).toFloat(); val l = (11f - k * 2f) * u
                val ini = Offset(x(fx), y(-7f)); val fin = Offset(ini.x + l * cos(a), ini.y + l * sin(a))
                drawPath(Path().apply { moveTo(ini.x, ini.y); quadraticTo(ini.x + l * 0.2f * cos(a) - s * 1.5f * u, ini.y + l * 0.7f * sin(a), fin.x, fin.y) }, t(HELECHO, oscuro), style = Stroke(0.6f * u))
                for (j in 1..4) { val f = j / 5f; val q = Offset(ini.x + (fin.x - ini.x) * f, ini.y + (fin.y - ini.y) * f); drawOval(t(if (k % 2 == 0) HELECHO else HOJA_CLARA, oscuro), Offset(q.x - 1.4f * u, q.y - 0.5f * u), Size(2.8f * u, 1f * u)) }
            }
        }
        drawLine(t(ROBLE_VETA, oscuro), Offset(x(4f), y(-7f)), Offset(x(4.6f), y(-15f)), 0.9f * u)
        listOf(-2.4f, -1.2f, 0.4f, 1.6f, 2.6f).forEach { a -> drawPath(Path().apply { moveTo(x(4.6f), y(-15f)); quadraticTo(x(4.6f + a * 2f), y(-18f), x(4.6f + a * 3f), y(-14f + abs(a))) }, t(PALMA, oscuro), style = Stroke(0.9f * u, cap = StrokeCap.Round)) }
        drawPath(Path().apply { moveTo(x(-2f), y(-7f)); cubicTo(x(-3f), y(-16f), x(1f), y(-22f), x(-1f), y(-29f)) }, t(HOJA_CLARA, oscuro), style = Stroke(0.7f * u))
        listOf(Offset(-1f, -28.6f) to 2.6f, Offset(-0.4f, -23.4f) to 2.3f, Offset(-2.4f, -18.4f) to 2f).forEach { (c0, r) ->
            val c = Offset(x(c0.x), y(c0.y))
            for (k in 0 until 5) { val a = (k * 2 * PI / 5 - PI / 2).toFloat(); drawCircle(flor, r * 0.55f * u, Offset(c.x + r * 0.55f * u * cos(a), c.y + r * 0.55f * u * sin(a))) }
            drawCircle(t(Color(0xFFF2D27A), oscuro), r * 0.32f * u, c)
        }
        // El armazón y el vidrio: los montantes, el reflejo y el panel abierto del tejado.
        listOf(-15f, -7.5f, 0f, 7.5f, 15f).forEach { fx -> drawLine(hierro, Offset(x(fx), y(-7f)), Offset(x(fx), y(-24f)), 0.6f * u) }
        drawLine(hierro, Offset(x(-15f), y(-24f)), Offset(x(15f), y(-24f)), 0.7f * u)
        drawPath(trazoContemporanea(b, u, false, -15.6f, -23.6f, 0f, -33f, 1.2f, -32.3f), hierro, style = Stroke(0.8f * u))
        drawPath(trazoContemporanea(b, u, false, -7.5f, -24f, 0f, -33f), hierro, style = Stroke(0.5f * u))
        drawPath(trazoContemporanea(b, u, true, 1.2f, -32.3f, 15.6f, -23.6f, 19f, -30.6f, 5f, -39f), if (oscuro) LUMBRE.copy(alpha = 0.1f) else t(VIDRIO, false).copy(alpha = 0.6f))
        drawPath(trazoContemporanea(b, u, true, 1.2f, -32.3f, 15.6f, -23.6f, 19f, -30.6f, 5f, -39f), hierro, style = Stroke(0.7f * u))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.12f else 0.4f), Offset(x(-12.6f), y(-9f)), Offset(x(-6f), y(-22f)), 1.2f * u)
        // El cajón de roble, la etiqueta y las patas.
        drawRect(roble, Offset(x(-16.6f), y(-7.4f)), Size(33.2f * u, 6 * u))
        drawRect(t(ROBLE_LUZ, oscuro), Offset(x(-16.6f), y(-7.4f)), Size(33.2f * u, 1 * u))
        drawLine(t(ROBLE_VETA, oscuro), Offset(x(-16f), y(-3.6f)), Offset(x(16f), y(-3.4f)), 0.4f * u)
        drawRect(t(PAPEL, oscuro), Offset(x(-4.6f), y(-5.6f)), Size(9.2f * u, 3.4f * u))
        drawLine(t(TINTA, oscuro), Offset(x(-3.4f), y(-4.4f)), Offset(x(3f), y(-4.4f)), 0.4f * u)
        drawLine(t(TINTA, oscuro), Offset(x(-3.4f), y(-3.2f)), Offset(x(1.4f), y(-3.2f)), 0.4f * u)
        listOf(-15.4f, 13.4f).forEach { fx -> drawRect(t(ROBLE_VETA, oscuro), Offset(x(fx), y(-1.4f)), Size(2 * u, 1.4f * u)) }
        // Las mariposas que salen por el panel abierto.
        mariposa(Offset(x(9f), y(-42f)), 1.05f * u, e.acento, -14f)
        mariposa(Offset(x(-6f), y(-40f)), 0.95f * u, t(Color(0xFF4C7FB8), oscuro), 12f)
        mariposa(Offset(x(18f), y(-38f)), 0.85f * u, flor, 20f)
        // Las luciérnagas de noche.
        if (oscuro) listOf(-21f to -12f, -19f to -27f, -13f to -36f, 21f to -15f, 22f to -24f, 14f to -45f, -2f to -46f, 23f to -40f).forEach { (fx, fy) ->
            val c = Offset(x(fx), y(fy))
            halo(c, 2.6f * u, LUMBRE.copy(alpha = 0.1f), 2)
            drawCircle(LUMBRE, 0.55f * u, c)
        }
    }

    // ---------------------------------------------------------------- objetos de Tu año

    /**
     * Botánico: la luna creciente sobre una rama con sus hojas, la suela de la bota de expedición con sus tacos, y las
     * pesas de latón de la balanza del herbario, la grande y la chica, con su pomo.
     */
    override fun DrawScope.objetoAnio(e: EstadoObjeto, oscuro: Boolean) {
        val r = e.radio; val x = e.centro.x; val y = e.centro.y; val u = density
        val col = e.color; val fondo = e.fondo
        fun p(a: Float, b: Float) = Offset(x + a * r, y + b * r)
        fun g(f: Float) = maxOf(r * f, 0.8f * u)
        when (e.forma) {
            "luna" -> {
                drawPath(crecienteContemporanea(p(0.22f, -0.3f), r * 0.66f, Offset(r * 0.32f, -r * 0.22f), r * 0.56f), col)
                // La rama bajo la luna: el tallo y tres hojas.
                drawPath(Path().apply { moveTo(x - r * 0.95f, y + r * 0.9f); quadraticTo(x - r * 0.2f, y + r * 0.58f, x + r * 0.85f, y + r * 0.66f) }, col, style = Stroke(g(0.08f)))
                listOf(Triple(-0.55f, 0.72f, -55f), Triple(-0.02f, 0.64f, 50f), Triple(0.42f, 0.64f, -40f)).forEach { (a, b, giro) ->
                    val hoja = trazoAnioContemporanea(p(a, b), r) { m(0f, 0f); q(0.26f, -0.24f, 0f, -0.52f); q(-0.26f, -0.24f, 0f, 0f); z() }
                    rotate(giro, p(a, b)) { drawPath(hoja, col) }
                }
            }
            "huella" -> rotate(-12f, e.centro) {
                drawPath(Path().apply {
                    addOval(Rect(x - r * 0.42f, y - r * 0.95f, x + r * 0.42f, y + r * 0.15f))
                    addOval(Rect(x - r * 0.33f, y + r * 0.28f, x + r * 0.33f, y + r * 0.95f))
                    addRect(Rect(x - r * 0.28f, y - r * 0.1f, x + r * 0.26f, y + r * 0.55f))
                }, col)
                // Los tacos de la suela, menos cuando es tan chica que se volverían una mancha.
                val tacos = if (r > 7 * u) listOf(-0.68f to 0.26f, -0.42f to 0.32f, -0.16f to 0.28f, 0.5f to 0.2f, 0.74f to 0.2f) else listOf(-0.42f to 0.3f, 0.6f to 0.2f)
                tacos.forEach { (b, a) -> drawRoundRect(fondo, Offset(x - r * a, y + r * b - g(0.06f)), Size(2 * a * r, 2 * g(0.06f)), CornerRadius(g(0.06f))) }
            }
            "pesa" -> {
                // La grande: el cuerpo, el cuello y el pomo; la chica, a su lado, más abajo.
                drawRoundRect(col, p(-0.88f, -0.2f), Size(r * 0.9f, r * 0.98f), CornerRadius(r * 0.12f))
                drawRect(col, p(-0.55f, -0.38f), Size(r * 0.24f, r * 0.2f))
                drawCircle(col, r * 0.2f, p(-0.43f, -0.53f))
                drawRoundRect(col, p(0.16f, 0.2f), Size(r * 0.68f, r * 0.58f), CornerRadius(r * 0.1f))
                drawRect(col, p(0.41f, 0.06f), Size(r * 0.18f, r * 0.16f))
                drawCircle(col, r * 0.15f, p(0.5f, -0.06f))
                // Las molduras grabadas en el latón.
                drawLine(fondo, p(-0.8f, 0.06f), p(-0.06f, 0.06f), g(0.06f))
                drawLine(fondo, p(0.22f, 0.42f), p(0.78f, 0.42f), g(0.05f))
            }
            else -> {}
        }
    }
}

/**
 * Navidad: el trofeo se apoya en un regalo: la caja de papel rojo con sus estrellitas, la tapa verde nevada con una
 * ramita de acebo, y la cinta del metal del nivel que la cruza y se anuda en un moño al frente. En el hall, la repisa de
 * nogal de la chimenea con dos guirnaldas de abeto y sus luces, y la campana del metal colgada al centro con su lazo. La
 * placa es la etiqueta de regalo, con su ojal y su hilo rojo, la costura de puntadas y el filete del metal.
 */
object LogrosNavidad : DisenoLogros {
    private val NOGAL = Color(0xFF6A4528)
    private val NOGAL_LUZ = Color(0xFF8A603A)
    private val NOGAL_VETA = Color(0xFF432A17)
    private val ABETO = Color(0xFF2E5A3A)
    private val ABETO_OSC = Color(0xFF1E3F28)
    private val VERDE = Color(0xFF2F6A45)
    private val ROJO = Color(0xFFB3262E)
    private val ROJO_BAYA = Color(0xFFD92B36)
    private val NIEVE = Color(0xFFF4F6FA)
    private val NIEVE_SOMBRA = Color(0xFFC9D4E2)
    private val CREMA = Color(0xFFF3E8D2)
    private val TINTA = Color(0xFF3A1E18)
    /** Las bombillas del cordón: roja, verde, amarilla, azul y rosa. */
    private val BOMBILLAS = listOf(Color(0xFFE84A3C), Color(0xFF4CC46A), Color(0xFFF2C94C), Color(0xFF4C8CE8), Color(0xFFF08CC8))

    // salón: colores
    private val PAPEL_PINTADO = Color(0xFFE9DCC4)
    private val RAYA = Color(0xFFB8484A)
    private val AFUERA = Color(0xFFC9D8E6)
    private val AFUERA_NOCHE = Color(0xFF1A2A44)
    private val LADRILLO = Color(0xFF8E3A2A)
    private val FUEGO = Color(0xFFFF8A2E)
    private val LLAMA = Color(0xFFFFD27A)
    private val ORO = Color(0xFFD4AF37)
    private val AZUL = Color(0xFF6FB3E0)

    /** El color en modo oscuro: más hundido, como en su biblioteca. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100C0A), 0.34f) else c

    /** Una hoja de acebo: un óvalo puntiagudo con tres picos por lado, girado [grados]. */
    private fun DrawScope.acebo(c: Offset, largo: Float, grados: Float, color: Color) = rotate(grados, c) {
        val a = largo * 0.28f
        drawPath(Path().apply {
            moveTo(c.x - largo / 2, c.y)
            for (k in 1..3) { val x = c.x - largo / 2 + largo * k / 4f; lineTo(x - largo * 0.06f, c.y - a * (if (k == 2) 1f else 0.8f)); lineTo(x, c.y - a * 0.55f) }
            lineTo(c.x + largo / 2, c.y)
            for (k in 3 downTo 1) { val x = c.x - largo / 2 + largo * k / 4f; lineTo(x + largo * 0.06f, c.y + a * (if (k == 2) 1f else 0.8f)); lineTo(x, c.y + a * 0.55f) }
            close()
        }, color)
    }

    // ---------------------------------------------------------------- pedestales y placa

    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val fino = h >= 90 * u
        val hl = maxOf(0.6f * u, h * 0.004f)
        fun c(x: Color) = t(x, oscuro)
        val caja = Rect(w * 0.13f, h * 0.806f, w * 0.87f, h * 0.952f)
        val tapa = Rect(w * 0.1f, h * 0.776f, w * 0.9f, h * 0.812f)
        if (!e.hecho) { silueta(oscuro) { addRect(caja); addRect(tapa) }; return }
        sombraPie(w / 2, caja.width * 1.05f, h * 0.958f, h * 0.022f, if (oscuro) 0.5f else 0.24f)
        // La caja de papel rojo, con su sombra bajo la tapa y sus estrellitas.
        drawRect(c(ROJO), caja.topLeft, caja.size)
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(caja.left, caja.top), Size(caja.width, caja.height * 0.12f))
        drawRect(Color.Black.copy(alpha = 0.14f), Offset(caja.right - caja.width * 0.08f, caja.top), Size(caja.width * 0.08f, caja.height))
        if (fino) {
            val paso = maxOf(caja.width / 8f, 1f); var fila = 0; var y = caja.top + caja.height * 0.3f
            while (y < caja.bottom - paso * 0.3f && fila < 6) {
                var x = caja.left + paso * (if (fila % 2 == 0) 0.5f else 1f); var n = 0
                while (x < caja.right - paso * 0.3f && n < 12) { drawPath(estrella(Offset(x, y), paso * 0.16f, 5, 0.45f), c(CREMA).copy(alpha = 0.55f)); x += paso; n++ }
                y += paso * 0.7f; fila++
            }
        }
        // La cinta del metal del nivel: de arriba abajo y de lado a lado, con su luz y su sombra.
        val cw = w * 0.07f
        val ch = caja.height * 0.2f
        fun cintaV(r: Rect) { drawRect(e.base, r.topLeft, r.size); drawRect(e.brillo, r.topLeft, Size(r.width * 0.3f, r.height)); drawRect(e.metalOscuro, Offset(r.right - r.width * 0.18f, r.top), Size(r.width * 0.18f, r.height)) }
        val cy = caja.top + caja.height * 0.56f
        drawRect(e.base, Offset(caja.left, cy - ch / 2), Size(caja.width, ch))
        drawRect(e.brillo, Offset(caja.left, cy - ch / 2), Size(caja.width, ch * 0.28f))
        drawRect(e.metalOscuro, Offset(caja.left, cy + ch * 0.32f), Size(caja.width, ch * 0.18f))
        cintaV(Rect(w / 2 - cw / 2, caja.top, w / 2 + cw / 2, caja.bottom))
        if (e.nivel == 4) clipRect(caja.left, caja.top, caja.right, caja.bottom) {
            drawLine(Color.White.copy(alpha = 0.7f), Offset(w / 2 - cw / 2, cy + cw), Offset(w / 2 + cw / 2, cy - cw), hl)
        }
        // La tapa verde, con la nieve que le cae encima y escurre por el frente.
        drawRect(c(VERDE), tapa.topLeft, tapa.size)
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(tapa.left, tapa.bottom - hl), Size(tapa.width, hl))
        cintaV(Rect(w / 2 - cw / 2, tapa.top, w / 2 + cw / 2, tapa.bottom))
        val nieve = Path().apply {
            moveTo(tapa.left - w * 0.01f, tapa.top + tapa.height * 0.35f)
            quadraticTo(tapa.left, tapa.top - h * 0.008f, tapa.left + tapa.width * 0.12f, tapa.top - h * 0.004f)
            lineTo(tapa.right - tapa.width * 0.1f, tapa.top - h * 0.006f)
            quadraticTo(tapa.right + w * 0.012f, tapa.top - h * 0.004f, tapa.right + w * 0.008f, tapa.top + tapa.height * 0.4f)
            val gotas = 7
            for (k in gotas downTo 1) {
                val x0 = tapa.left + tapa.width * k / (gotas + 1f); val largo = tapa.height * (0.25f + 0.45f * azar(e.semilla, k))
                lineTo(x0 + tapa.width * 0.03f, tapa.top + tapa.height * 0.3f)
                quadraticTo(x0, tapa.top + tapa.height * 0.3f + largo * 2f, x0 - tapa.width * 0.03f, tapa.top + tapa.height * 0.3f)
            }
            close()
        }
        drawPath(nieve, c(NIEVE))
        drawPath(nieve, c(NIEVE_SOMBRA), style = Stroke(hl * 0.6f))
        // El moño al frente de la tapa.
        val mo = Offset(w / 2, tapa.center.y + tapa.height * 0.15f); val ml = tapa.height * 1.25f
        listOf(-1f, 1f).forEach { s ->
            drawOval(e.metalOscuro, Offset(mo.x + (if (s < 0) -ml * 1.15f else ml * 0.05f), mo.y - ml * 0.5f), Size(ml * 1.1f, ml))
            drawOval(e.base, Offset(mo.x + (if (s < 0) -ml * 1.0f else ml * 0.12f), mo.y - ml * 0.38f), Size(ml * 0.88f, ml * 0.72f))
            drawLine(e.metalOscuro, mo, Offset(mo.x + s * ml * 0.75f, mo.y + ml * 1.2f), cw * 0.62f)
            drawLine(e.base, mo, Offset(mo.x + s * ml * 0.7f, mo.y + ml * 1.15f), cw * 0.45f)
        }
        boton(e, mo, ml * 0.36f)
        // La ramita de acebo sobre la nieve, a la izquierda.
        if (fino) {
            val ra = Offset(tapa.left + tapa.width * 0.2f, tapa.top + h * 0.002f); val l = tapa.height * 1.1f
            acebo(ra + Offset(-l * 0.4f, 0f), l, -18f, c(ABETO))
            acebo(ra + Offset(l * 0.4f, -l * 0.05f), l, 22f, c(ABETO_OSC))
            listOf(Offset(-0.12f, -0.1f), Offset(0.12f, -0.12f), Offset(0f, 0.1f)).forEach { o -> drawCircle(c(ROJO_BAYA), l * 0.17f, ra + Offset(l * o.x, l * o.y)); drawCircle(Color.White.copy(alpha = 0.6f), l * 0.05f, ra + Offset(l * (o.x - 0.05f), l * (o.y - 0.05f))) }
        }
    }

    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val m = tonos(e, oscuro)
        fun c(x: Color) = t(x, oscuro)
        // La repisa de nogal, con su canto de luz y la moldura debajo.
        val repisa = Rect(w * 0.03f, 0f, w * 0.97f, h * 0.22f)
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(w * 0.06f, h * 0.22f), Size(w * 0.88f, h * 0.14f))
        drawRect(c(NOGAL), repisa.topLeft, repisa.size)
        drawRect(c(NOGAL_LUZ), repisa.topLeft, Size(repisa.width, repisa.height * 0.32f))
        drawRect(c(NOGAL_VETA), Offset(w * 0.07f, h * 0.22f), Size(w * 0.86f, h * 0.08f))
        // Las dos guirnaldas de abeto colgadas de la repisa, con sus luces (de noche, con su halo).
        val bombillas = mutableListOf<Pair<Offset, Color>>()
        listOf(w * 0.07f to w * 0.42f, w * 0.58f to w * 0.93f).forEachIndexed { g, (x0, x1) ->
            val ruta = Path().apply { moveTo(x0, h * 0.3f); quadraticTo((x0 + x1) / 2, h * 0.95f, x1, h * 0.3f) }
            drawPath(ruta, c(ABETO_OSC), style = Stroke(h * 0.2f, cap = StrokeCap.Round))
            drawPath(ruta, c(ABETO), style = Stroke(h * 0.11f, cap = StrokeCap.Round))
            for (k in 1..4) {
                val f = k / 5f; val x = x0 + (x1 - x0) * f; val y = h * 0.3f + (h * 0.65f) * 2 * f * (1 - f) * 0.92f
                bombillas.add(Offset(x, y + h * 0.05f) to BOMBILLAS[(g * 4 + k) % BOMBILLAS.size])
            }
        }
        bombillas.forEach { (p, col) ->
            if (oscuro) drawCircle(col.copy(alpha = 0.3f), 2.8f * u, p)
            drawCircle(col, 1.2f * u, p)
        }
        // La campana del metal del nivel, colgada al centro, con su lazo rojo.
        val cx = w / 2; val top = h * 0.36f; val bot = h * 0.84f; val r = (bot - top) * 0.62f
        drawLine(c(ROJO), Offset(cx, h * 0.28f), Offset(cx, top), 0.9f * u)
        val campana = Path().apply {
            moveTo(cx - r * 0.42f, top + r * 0.3f); quadraticTo(cx - r * 0.42f, top, cx, top); quadraticTo(cx + r * 0.42f, top, cx + r * 0.42f, top + r * 0.3f)
            quadraticTo(cx + r * 0.5f, bot - r * 0.2f, cx + r * 0.8f, bot); lineTo(cx - r * 0.8f, bot); quadraticTo(cx - r * 0.5f, bot - r * 0.2f, cx - r * 0.42f, top + r * 0.3f); close()
        }
        drawPath(campana, m.base)
        clipPath(campana) {
            drawRect(m.brillo, Offset(cx - r * 0.5f, top), Size(r * 0.28f, bot - top))
            drawRect(m.metalOscuro, Offset(cx + r * 0.3f, top), Size(r * 0.6f, bot - top))
        }
        drawRect(m.metalOscuro, Offset(cx - r * 0.82f, bot - 0.9f * u), Size(r * 1.64f, 0.9f * u))
        drawCircle(m.metalOscuro, r * 0.16f, Offset(cx, bot + r * 0.08f))
        listOf(-1f, 1f).forEach { s -> drawOval(c(ROJO), Offset(cx + (if (s < 0) -r * 0.62f else r * 0.02f), h * 0.24f), Size(r * 0.6f, r * 0.34f)) }
        drawCircle(c(ROJO_BAYA), r * 0.12f, Offset(cx, h * 0.3f))
    }

    override val margenPlaca: Dp = 10.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) NIEVE else TINTA
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFF86C99A) else ABETO

    /** La etiqueta de regalo: el lado izquierdo con las esquinas cortadas, el derecho redondeado. */
    private fun etiqueta(r: Rect, ch: Float, rd: Float): Path = Path().apply {
        moveTo(r.left + ch, r.top); lineTo(r.right - rd, r.top); quadraticTo(r.right, r.top, r.right, r.top + rd)
        lineTo(r.right, r.bottom - rd); quadraticTo(r.right, r.bottom, r.right - rd, r.bottom); lineTo(r.left + ch, r.bottom)
        lineTo(r.left, r.bottom - ch); lineTo(r.left, r.top + ch); close()
    }

    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val h = size.height
        val m = tonos(e, oscuro)
        val r0 = Rect(Offset.Zero, size)
        drawPath(etiqueta(r0.translate(0.5f * u, 1.6f * u), 6 * u, 3 * u), Color.Black.copy(alpha = if (oscuro) 0.45f else 0.2f))
        // La cartulina, el filete del metal del nivel y la costura de puntadas rojas.
        drawPath(etiqueta(r0, 6 * u, 3 * u), if (oscuro) Color(0xFF2B2326) else CREMA)
        filete(m, etiqueta(r0.deflate(1.1f * u), 5.4f * u, 2.4f * u), 2f * u)
        drawPath(etiqueta(r0.deflate(3.6f * u), 4.4f * u, 1.6f * u), (if (oscuro) Color(0xFFE0606A) else ROJO).copy(alpha = 0.8f),
            style = Stroke(0.7f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.2f * u, 1.6f * u), 0f)))
        // El ojal con su arandela del metal y el hilo rojo que sale por la izquierda.
        val ojal = Offset(5.2f * u, h / 2)
        drawCircle(m.metalOscuro, 1.9f * u, ojal); drawCircle(m.brillo, 1.5f * u, ojal)
        drawCircle(if (oscuro) Color(0xFF120E10) else Color(0xFF6A5A50), 0.9f * u, ojal)
        drawPath(Path().apply { moveTo(ojal.x, ojal.y); cubicTo(ojal.x - 3 * u, ojal.y - 4 * u, -2 * u, ojal.y - 2 * u, -5 * u, ojal.y - 6 * u) }, t(ROJO_BAYA, oscuro), style = Stroke(0.9f * u, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(ojal.x, ojal.y); cubicTo(ojal.x - 3 * u, ojal.y + 3 * u, -1 * u, ojal.y + 4 * u, -4 * u, ojal.y + 7 * u) }, t(ROJO_BAYA, oscuro), style = Stroke(0.9f * u, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- el salón del hall

    /**
     * La sala de la chimenea en Nochebuena: el papel pintado de rayas con su ramita entre raya y raya y el arrimadero de
     * nogal abajo. Entre las franjas, en cada piso, la ventana de cuarterones con su escarcha en las esquinas, la nieve que
     * cae afuera y se junta en el alféizar, las cortinas rojas recogidas y, de noche, la vela encendida. Arriba, la viga de
     * nogal con la guirnalda de abeto en festones, sus focos de colores (de noche, encendidos), los moños rojos y el
     * muérdago. Abajo, sobre el piso de tablas, la chimenea de ladrillo en una de las líneas, con su repisa, el fuego y los
     * calcetines colgados, y la alfombra redonda delante; en la esquina derecha, el árbol con sus esferas, su estrella y los
     * regalos; en la izquierda, el caballito de madera.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) = clipRect { salon(e, oscuro) }

    /** La escena del salón, recortada al lienzo por [escenaSalon]. */
    private fun DrawScope.salon(e: EstadoSalon, oscuro: Boolean) {
        val p = planta(e); val u = p.u; val w = p.w; val h = p.h
        fun c(x: Color, f: Float) = mezcla(e.fondo, t(x, oscuro), f)
        val techo = 30 * u; val friso = h - 50 * u; val piso = h - 16 * u
        val focos = BOMBILLAS

        // El papel pintado: rayas anchas y, entre ellas, la ramita de acebo repetida.
        drawRect(c(PAPEL_PINTADO, if (oscuro) 0.22f else 0.45f), Offset(0f, techo), Size(w, friso - techo))
        run {
            var x = 0f; var k = 0
            while (x < w && k < 50) {
                drawRect(c(RAYA, if (oscuro) 0.14f else 0.12f), Offset(x, techo), Size(7 * u, friso - techo))
                var y = techo + 16 * u + (if (k % 2 == 0) 0f else 20 * u); var j = 0
                while (y < friso && j < 40) {
                    val q = Offset(x + 15 * u, y)
                    drawOval(c(VERDE, if (oscuro) 0.2f else 0.22f), Offset(q.x - 3.5f * u, q.y - 1.2f * u), Size(3.5f * u, 2.4f * u))
                    drawOval(c(VERDE, if (oscuro) 0.2f else 0.22f), Offset(q.x, q.y - 1.2f * u), Size(3.5f * u, 2.4f * u))
                    drawCircle(c(ROJO, if (oscuro) 0.22f else 0.25f), 1 * u, Offset(q.x, q.y + 1.2f * u))
                    y += maxOf(40 * u, 1f); j++
                }
                x += maxOf(22 * u, 1f); k++
            }
        }

        // Las ventanas de cuarterones entre las franjas, una por piso.
        for (i in 0..p.n) for (k in 0 until p.filas) {
            val x = p.lx(i); val vw = 26 * u
            val y0 = maxOf(p.y(k) + 18 * u, techo + 14 * u); val y1 = y0 + minOf(p.alto * 0.42f, 86 * u)
            if (y1 > friso - 70 * u && !(i == 0 || i == p.n)) continue
            if (y1 > friso - 10 * u) continue
            val vidrio = Rect(x - vw / 2, y0, x + vw / 2, y1)
            drawRect(c(NOGAL, if (oscuro) 0.6f else 0.65f), Offset(vidrio.left - 3 * u, y0 - 3 * u), Size(vw + 6 * u, y1 - y0 + 6 * u))
            drawRect(if (oscuro) mezcla(e.fondo, AFUERA_NOCHE, 0.8f) else c(AFUERA, 0.75f), vidrio.topLeft, vidrio.size)
            clipRect(vidrio.left, vidrio.top, vidrio.right, vidrio.bottom) {
                // Los abetos nevados de afuera y la nieve que cae.
                for (j in 0 until 3) {
                    val ax = vidrio.left + vidrio.width * (0.2f + 0.3f * j); val ay = y1 - 2 * u
                    drawPath(Path().apply { moveTo(ax - 6 * u, ay); lineTo(ax, ay - (14 + 6 * azar(e.semilla + i, k * 3 + j)) * u); lineTo(ax + 6 * u, ay); close() }, if (oscuro) mezcla(e.fondo, ABETO_OSC, 0.7f) else c(ABETO, 0.35f))
                }
                drawRect(c(NIEVE, if (oscuro) 0.4f else 0.9f), Offset(vidrio.left, y1 - 4 * u), Size(vw, 4 * u))
                for (j in 0 until 9) drawCircle(Color.White.copy(alpha = if (oscuro) 0.6f else 0.9f), (0.6f + 0.6f * azar(e.semilla + 7, i * 40 + k * 9 + j)) * u, Offset(vidrio.left + vw * azar(e.semilla + 8, i * 40 + k * 9 + j), y0 + (y1 - y0) * azar(e.semilla + 9, i * 40 + k * 9 + j)))
                // La escarcha en las esquinas de abajo.
                listOf(vidrio.left, vidrio.right).forEach { ex -> drawCircle(Color.White.copy(alpha = if (oscuro) 0.12f else 0.4f), 7 * u, Offset(ex, y1)) }
            }
            drawLine(c(NOGAL, 0.7f), Offset(x, y0), Offset(x, y1), 1.6f * u)
            listOf(0.33f, 0.66f).forEach { f -> drawLine(c(NOGAL, 0.7f), Offset(vidrio.left, y0 + (y1 - y0) * f), Offset(vidrio.right, y0 + (y1 - y0) * f), 1.4f * u) }
            // Las cortinas recogidas con su lazo.
            listOf(-1f, 1f).forEach { s ->
                val bx = x + s * (vw / 2 + 3 * u)
                drawPath(Path().apply { moveTo(bx, y0 - 6 * u); lineTo(bx + s * 9 * u, y0 - 6 * u); quadraticTo(bx + s * 3 * u, (y0 + y1) / 2, bx + s * 6 * u, y1 + 4 * u); lineTo(bx - s * 1 * u, y1 + 4 * u); quadraticTo(bx - s * 4 * u, (y0 + y1) * 0.55f, bx, y0 - 6 * u); close() }, c(ROJO, if (oscuro) 0.45f else 0.55f))
                drawCircle(c(ORO, 0.7f), 1.6f * u, Offset(bx + s * 1 * u, y0 + (y1 - y0) * 0.6f))
            }
            drawRect(c(NOGAL, 0.75f), Offset(x - vw / 2 - 6 * u, y0 - 8 * u), Size(vw + 12 * u, 3 * u))
            // El alféizar con la nieve de adentro (algodón) y, de noche, la vela encendida.
            drawRect(c(NOGAL_LUZ, 0.75f), Offset(vidrio.left - 6 * u, y1 + 3 * u), Size(vw + 12 * u, 3 * u))
            if (oscuro) {
                val v = Offset(x + 6 * u, y1 + 3 * u)
                halo(v - Offset(0f, 8 * u), 26 * u, LLAMA.copy(alpha = 0.04f), 5)
                drawRect(c(CREMA, 0.8f), Offset(v.x - 1.4f * u, v.y - 7 * u), Size(2.8f * u, 7 * u))
                drawOval(LLAMA.copy(alpha = 0.95f), Offset(v.x - 1.2f * u, v.y - 11.5f * u), Size(2.4f * u, 4.4f * u))
            }
        }

        // Arriba: la viga de nogal, la guirnalda de abeto en festones con sus focos, los moños y el muérdago.
        drawRect(c(NOGAL, if (oscuro) 0.55f else 0.6f), Offset.Zero, Size(w, techo - 12 * u))
        vetasEn(Rect(0f, 0f, w, techo - 12 * u), c(NOGAL_VETA, 0.5f), e.semilla + 5, densidad = 0.8f, grosor = 0.7f)
        drawRect(c(NOGAL_VETA, 0.6f), Offset(0f, techo - 13 * u), Size(w, 1.4f * u))
        val festones = p.n * 2
        for (k in 0 until festones) {
            val x0 = w * k / festones; val x1 = w * (k + 1) / festones
            val guir = Path().apply { moveTo(x0, techo - 12 * u); quadraticTo((x0 + x1) / 2, techo + 14 * u, x1, techo - 12 * u) }
            drawPath(guir, c(ABETO_OSC, if (oscuro) 0.7f else 0.7f), style = Stroke(6 * u, cap = StrokeCap.Round))
            drawPath(guir, c(ABETO, 0.7f), style = Stroke(3 * u, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 2.4f * u))))
            for (j in 1..5) {
                val f = j / 6f; val x = x0 + (x1 - x0) * f; val y = techo - 12 * u + 26 * u * 2 * f * (1 - f) * 0.5f * 2
                val col = focos[(j + k) % focos.size]
                if (oscuro) halo(Offset(x, y + 3 * u), 9 * u, col.copy(alpha = 0.08f), 3)
                drawOval(if (oscuro) mezcla(e.fondo, col, 0.95f) else c(col, 0.6f), Offset(x - 1.3f * u, y + 1 * u), Size(2.6f * u, 4 * u))
            }
        }
        for (k in 0..festones) {
            val x = w * k / festones; val y = techo - 12 * u
            listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(x, y); quadraticTo(x + s * 6 * u, y - 6 * u, x + s * 6 * u, y + 2 * u); close() }, c(ROJO, if (oscuro) 0.6f else 0.75f)) }
            listOf(-1f, 1f).forEach { s -> drawLine(c(ROJO, if (oscuro) 0.6f else 0.75f), Offset(x, y), Offset(x + s * 3 * u, y + 9 * u), 1.6f * u) }
            drawCircle(c(ROJO_BAYA, 0.8f), 1.6f * u, Offset(x, y))
        }
        // El muérdago colgado en la línea del medio.
        run {
            val x = p.lx(p.n / 2).coerceIn(20 * u, w - 20 * u); val y = techo + 12 * u
            drawLine(c(ROJO, 0.6f), Offset(x, techo - 12 * u), Offset(x, y - 4 * u), 0.8f * u)
            for (j in 0 until 6) { val a = PI * (0.1 + 0.8 * j / 5); drawOval(c(VERDE, 0.6f), Offset(x - (6 * u * cos(a)).toFloat() - 2.5f * u, y + (3 * u * sin(a)).toFloat() - 1.4f * u), Size(5 * u, 2.8f * u)) }
            listOf(-1.5f, 1.5f, 0f).forEach { dx -> drawCircle(c(NIEVE, 0.85f), 1.3f * u, Offset(x + dx * u, y + 2 * u)) }
        }

        // Abajo: el arrimadero de nogal con sus tableros y el piso de tablas.
        drawRect(c(NOGAL, if (oscuro) 0.5f else 0.55f), Offset(0f, friso), Size(w, piso - friso))
        drawRect(c(NOGAL_LUZ, 0.7f), Offset(0f, friso), Size(w, 2.6f * u))
        run { var x = 6 * u; var k = 0; while (x < w && k < 30) { drawRect(c(NOGAL_VETA, 0.5f), Offset(x, friso + 7 * u), Size(34 * u, piso - friso - 12 * u), style = Stroke(0.9f * u)); x += maxOf(42 * u, 1f); k++ } }
        drawRect(c(NOGAL_LUZ, if (oscuro) 0.4f else 0.45f), Offset(0f, piso), Size(w, h - piso))
        run { var y = piso + 5 * u; var k = 0; while (y < h && k < 6) { drawLine(c(NOGAL_VETA, 0.5f), Offset(0f, y), Offset(w, y), 0.8f * u); y += maxOf(5 * u, 1f); k++ } }

        // La chimenea de ladrillo en una línea: la boca con el fuego, la repisa con sus velas y los calcetines.
        run {
            val x = p.lx(p.n / 2).coerceIn(44 * u, w - 44 * u); val an = minOf(80 * u, p.paso * 0.62f)
            val y0 = h - 74 * u
            if (oscuro) halo(Offset(x, h - 24 * u), 80 * u, FUEGO.copy(alpha = 0.025f), 6)
            drawRect(c(LADRILLO, if (oscuro) 0.6f else 0.55f), Offset(x - an / 2, y0), Size(an, h - y0))
            var y = y0 + 4 * u; var f = 0
            while (y < h && f < 20) {
                drawLine(c(CREMA, 0.35f), Offset(x - an / 2, y), Offset(x + an / 2, y), 0.7f * u)
                var xx = x - an / 2 + (if (f % 2 == 0) 0f else 4 * u); var j = 0
                while (xx < x + an / 2 && j < 20) { drawLine(c(CREMA, 0.35f), Offset(xx, y), Offset(xx, y + 4 * u), 0.7f * u); xx += maxOf(8 * u, 1f); j++ }
                y += maxOf(4 * u, 1f); f++
            }
            val boca = Rect(x - an * 0.3f, h - 40 * u, x + an * 0.3f, h - 2 * u)
            drawPath(arcoMedioPunto(boca), c(Color(0xFF1A1210), if (oscuro) 0.9f else 0.6f))
            clipPath(arcoMedioPunto(boca)) {
                drawCircle(Brush.radialGradient(listOf(FUEGO.copy(alpha = if (oscuro) 0.75f else 0.5f), Color.Transparent), Offset(x, boca.bottom), boca.width * 0.6f), boca.width * 0.6f, Offset(x, boca.bottom))
                for (j in -2..2) drawPath(Path().apply { moveTo(x + j * 5 * u - 3 * u, boca.bottom - 4 * u); quadraticTo(x + j * 5 * u, boca.bottom - (16 + 6 * (2 - abs(j))) * u, x + j * 5 * u + 3 * u, boca.bottom - 4 * u); close() }, (if (j % 2 == 0) LLAMA else FUEGO).copy(alpha = if (oscuro) 0.9f else 0.65f))
                drawLine(c(NOGAL_VETA, 0.9f), Offset(boca.left + 4 * u, boca.bottom - 3 * u), Offset(boca.right - 4 * u, boca.bottom - 5 * u), 3 * u, StrokeCap.Round)
            }
            // La repisa con sus dos velas y la guirnalda corta.
            drawRect(c(NOGAL, 0.85f), Offset(x - an / 2 - 6 * u, y0 - 6 * u), Size(an + 12 * u, 6 * u))
            drawRect(c(NOGAL_LUZ, 0.85f), Offset(x - an / 2 - 6 * u, y0 - 6 * u), Size(an + 12 * u, 1.4f * u))
            listOf(-1f, 1f).forEach { s ->
                val vx = x + s * an * 0.36f
                drawRect(c(CREMA, 0.85f), Offset(vx - 1.6f * u, y0 - 18 * u), Size(3.2f * u, 12 * u))
                if (oscuro) { halo(Offset(vx, y0 - 22 * u), 16 * u, LLAMA.copy(alpha = 0.05f), 4); drawOval(LLAMA.copy(alpha = 0.95f), Offset(vx - 1.2f * u, y0 - 23 * u), Size(2.4f * u, 4.4f * u)) }
            }
            // Los calcetines colgados de la repisa, rojos y verdes con su puño blanco.
            for (j in 0 until 3) {
                val cx = x - an * 0.24f + j * an * 0.24f; val sy = y0
                val col = if (j % 2 == 0) ROJO else VERDE
                drawPath(Path().apply { moveTo(cx - 3.5f * u, sy + 2 * u); lineTo(cx + 3.5f * u, sy + 2 * u); lineTo(cx + 3.5f * u, sy + 13 * u); quadraticTo(cx + 3.5f * u, sy + 18 * u, cx - 2 * u, sy + 18 * u); lineTo(cx - 6 * u, sy + 18 * u); quadraticTo(cx - 8 * u, sy + 14 * u, cx - 3.5f * u, sy + 13 * u); close() }, c(col, if (oscuro) 0.6f else 0.75f))
                drawRect(c(NIEVE, 0.85f), Offset(cx - 4 * u, sy), Size(8 * u, 3.4f * u))
            }
        }
        // La alfombra redonda delante del fuego.
        run {
            val x = p.lx(p.n / 2).coerceIn(44 * u, w - 44 * u)
            listOf(0f, 0.25f, 0.5f).forEachIndexed { j, f -> drawOval(c(if (j % 2 == 0) ROJO else CREMA, if (oscuro) 0.4f else 0.5f), Offset(x - (56 - 56 * f) * u, h - (7 - 7 * f) * u), Size((112 - 112 * f) * u, (14 - 14 * f) * u)) }
        }

        // El árbol en la esquina derecha: los pisos de ramas, las esferas, los focos, la estrella y los regalos.
        run {
            val ax = w - 6 * u; val base = h - 14 * u
            for (k in 0 until 4) {
                val an = (64 - k * 12) * u; val y = base - k * 26 * u
                drawPath(Path().apply { moveTo(ax - an / 2, y); lineTo(ax, y - 40 * u); lineTo(ax + an / 2, y); close() }, c(if (k % 2 == 0) ABETO else ABETO_OSC, if (oscuro) 0.6f else 0.65f))
                for (j in 0 until 3) {
                    val q = Offset(ax - an * (0.15f + 0.25f * j), y - (3 + 8 * azar(e.semilla + 12, k * 3 + j)) * u)
                    val col = listOf(ROJO_BAYA, ORO, AZUL)[(j + k) % 3]
                    if (oscuro && j == 1) halo(q, 7 * u, col.copy(alpha = 0.1f), 3)
                    drawCircle(if (oscuro) mezcla(e.fondo, col, 0.85f) else c(col, 0.8f), 2.4f * u, q)
                    drawCircle(Color.White.copy(alpha = 0.5f), 0.7f * u, q - Offset(0.8f * u, 0.8f * u))
                }
            }
            val est = Offset(ax - 1 * u, base - 4 * 26 * u - 18 * u)
            if (oscuro) halo(est, 14 * u, LLAMA.copy(alpha = 0.08f), 3)
            drawPath(estrella(est, 6 * u, 5, 0.45f), mezcla(e.fondo, ORO, if (oscuro) 0.95f else 0.85f))
            listOf(Rect(ax - 40 * u, base - 2 * u, ax - 22 * u, h - 2 * u), Rect(ax - 24 * u, base + 2 * u, ax - 8 * u, h - 2 * u)).forEachIndexed { j, r ->
                drawRect(c(if (j == 0) ROJO else VERDE, if (oscuro) 0.55f else 0.7f), r.topLeft, r.size)
                drawLine(c(ORO, 0.8f), Offset(r.center.x, r.top), Offset(r.center.x, r.bottom), 1.4f * u)
                drawLine(c(ORO, 0.8f), Offset(r.left, r.center.y), Offset(r.right, r.center.y), 1.4f * u)
            }
        }
        // El caballito de madera en la esquina izquierda: el balancín, el cuerpo, la cabeza con su crin y la silla roja.
        run {
            val x = 20 * u; val y = h - 4 * u
            drawArc(c(NOGAL, 0.75f), 20f, 140f, false, Offset(x - 18 * u, y - 14 * u), Size(36 * u, 14 * u), style = Stroke(2 * u))
            listOf(-8f, 8f).forEach { dx -> drawLine(c(NOGAL, 0.75f), Offset(x + dx * u, y - 4 * u), Offset(x + dx * 0.7f * u, y - 16 * u), 1.8f * u) }
            drawRoundRect(c(CREMA, if (oscuro) 0.55f else 0.75f), Offset(x - 11 * u, y - 23 * u), Size(22 * u, 8 * u), CornerRadius(4 * u))
            drawPath(Path().apply { moveTo(x + 8 * u, y - 21 * u); lineTo(x + 12 * u, y - 33 * u); lineTo(x + 18 * u, y - 31 * u); lineTo(x + 17 * u, y - 28 * u); lineTo(x + 13 * u, y - 27 * u); lineTo(x + 12 * u, y - 19 * u); close() }, c(CREMA, if (oscuro) 0.55f else 0.75f))
            drawLine(c(NOGAL_VETA, 0.75f), Offset(x + 11 * u, y - 32 * u), Offset(x + 8 * u, y - 23 * u), 1.6f * u)
            drawRect(c(ROJO, 0.75f), Offset(x - 4 * u, y - 25 * u), Size(8 * u, 4 * u))
            drawLine(c(NOGAL_VETA, 0.7f), Offset(x - 11 * u, y - 21 * u), Offset(x - 16 * u, y - 14 * u), 1.4f * u)
        }
    }

    // ---------------------------------------------------------------- las campañas

    /**
     * El trineo que lleva los regalos, de lado y hacia la derecha: los patines rojos con su voluta delantera y su filo de
     * oro, los postes y la tabla de nogal, el respaldo curvo, la carga (la caja roja con su lazo de oro y su ramita de acebo,
     * la verde con su cinta crema y, encima, la azul con el moño del color de acento), la nieve sobre las tapas, el
     * cascabel de la proa y la pértiga con su farol colgado. De noche, el farol se enciende con su halo.
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density; val p = e.punto
        sombraMarcadorContemporanea(p, 26 * u, e.salto, oscuro)
        val o = Offset(p.x, p.y - subidaContemporanea(e.salto))
        fun x(v: Float) = o.x + v * u
        fun y(v: Float) = o.y + v * u
        val rojo = t(ROJO, oscuro); val nogal = t(NOGAL, oscuro); val oro = t(ORO, oscuro); val nieve = t(NIEVE, oscuro)
        // El respaldo curvo y la pértiga del farol.
        drawPath(Path().apply { moveTo(x(-11.6f), y(-6f)); cubicTo(x(-12.4f), y(-9f), x(-13.4f), y(-12f), x(-11.6f), y(-13.4f)); quadraticTo(x(-10.4f), y(-13.6f), x(-10.8f), y(-12.2f)) }, nogal, style = Stroke(1.2f * u, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(x(7.4f), y(-6f)); lineTo(x(8.2f), y(-19.6f)); quadraticTo(x(9.6f), y(-21f), x(11.4f), y(-19.8f)) }, t(NOGAL_VETA, oscuro), style = Stroke(0.8f * u, cap = StrokeCap.Round))
        // El farol colgado.
        val f = Offset(x(11.4f), y(-16.2f))
        if (oscuro) halo(f, 9 * u, LLAMA.copy(alpha = 0.09f), 4)
        drawLine(t(TINTA, oscuro), Offset(f.x, f.y - 3.6f * u), Offset(f.x, f.y - 2.4f * u), 0.4f * u)
        drawPath(trazoContemporanea(f, u, true, -1.6f, -2.4f, 1.6f, -2.4f, 0f, -3.4f), oro)
        drawRect(if (oscuro) LLAMA else mezcla(LLAMA, CREMA, 0.4f), Offset(f.x - 1.2f * u, f.y - 2.4f * u), Size(2.4f * u, 3.4f * u))
        drawRect(oro, Offset(f.x - 1.2f * u, f.y - 2.4f * u), Size(2.4f * u, 3.4f * u), style = Stroke(0.4f * u))
        drawRect(oro, Offset(f.x - 1.5f * u, f.y + 1f * u), Size(3 * u, 0.6f * u))
        // La carga: la caja roja, la verde y la azul, con sus cintas, el acebo y la nieve.
        fun caja(l: Float, tp: Float, r: Float, bt: Float, cuerpo: Color, cinta: Color) {
            drawRect(cuerpo, Offset(x(l), y(tp)), Size((r - l) * u, (bt - tp) * u))
            drawRect(mezcla(cuerpo, Color.Black, 0.22f), Offset(x(r - (r - l) * 0.22f), y(tp)), Size((r - l) * 0.22f * u, (bt - tp) * u))
            drawLine(cinta, Offset(x((l + r) / 2), y(tp)), Offset(x((l + r) / 2), y(bt)), 0.8f * u)
            drawLine(cinta, Offset(x(l), y((tp + bt) / 2)), Offset(x(r), y((tp + bt) / 2)), 0.8f * u)
            drawRoundRect(nieve, Offset(x(l - 0.3f), y(tp - 0.7f)), Size((r - l + 0.6f) * u, 1.3f * u), CornerRadius(0.65f * u))
        }
        caja(-9.6f, -14.6f, -1.4f, -6.4f, rojo, oro)
        caja(-1.2f, -12f, 6.6f, -6.4f, t(VERDE, oscuro), t(CREMA, oscuro))
        caja(-8f, -19.2f, -2.8f, -15.2f, t(AZUL, oscuro), t(CREMA, oscuro))
        listOf(-1f, 1f).forEach { s -> drawOval(e.acento, Offset(x(-5.4f + s * 1.3f - 1.1f), y(-21f)), Size(2.2f * u, 1.6f * u)) }
        drawCircle(mezcla(e.acento, Color.Black, 0.25f), 0.5f * u, Offset(x(-5.4f), y(-20.2f)))
        acebo(Offset(x(-3.6f), y(-9f)), 3.6f * u, -30f, t(ABETO, oscuro))
        acebo(Offset(x(-2f), y(-8f)), 3.2f * u, 40f, t(VERDE, oscuro))
        drawCircle(t(ROJO_BAYA, oscuro), 0.6f * u, Offset(x(-2.8f), y(-8.8f)))
        // La tabla de nogal y los postes.
        drawRect(nogal, Offset(x(-12.4f), y(-6.6f)), Size(21 * u, 1.8f * u))
        drawRect(t(NOGAL_LUZ, oscuro), Offset(x(-12.4f), y(-6.6f)), Size(21 * u, 0.5f * u))
        listOf(-9.6f, -2.4f, 4.8f).forEach { px -> drawLine(t(NOGAL_VETA, oscuro), Offset(x(px), y(-4.8f)), Offset(x(px + 0.6f), y(-1.4f)), 0.8f * u) }
        // Los patines con su voluta y el cascabel.
        val patin = Path().apply {
            moveTo(x(-13f), y(-2.6f)); quadraticTo(x(-13f), y(-0.8f), x(-10.6f), y(-0.8f)); lineTo(x(8.4f), y(-0.8f))
            cubicTo(x(12.6f), y(-0.8f), x(13.6f), y(-5.4f), x(11.4f), y(-6.6f)); quadraticTo(x(9.8f), y(-7.2f), x(10f), y(-5.4f))
        }
        if (oscuro) drawPath(patin, CREMA.copy(alpha = 0.4f), style = Stroke(2.2f * u, cap = StrokeCap.Round))
        drawPath(patin, rojo, style = Stroke(1.4f * u, cap = StrokeCap.Round))
        drawPath(patin, oro, style = Stroke(0.35f * u))
        drawCircle(oro, 1.1f * u, Offset(x(12.4f), y(-3f)))
        drawCircle(t(TINTA, oscuro), 0.3f * u, Offset(x(12.4f), y(-2.4f)))
    }

    /**
     * El árbol de la plaza al final del camino: el abeto de cuatro pisos con sus bordes de nieve, la sombra del lado
     * derecho y las guirnaldas de bombillas de colores; arriba, la estrella de oro; al pie, el montículo de nieve, el tronco
     * y los regalos (uno del color de acento); alrededor, copos que caen. De noche, cada bombilla y la estrella brillan con su
     * halo.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val b = pieVictoriaContemporanea(e.meta, 48 * u, 50 * u, 2 * u)
        fun x(v: Float) = b.x + v * u
        fun y(v: Float) = b.y + v * u
        val abeto = if (oscuro) mezcla(ABETO, Color.White, 0.06f) else ABETO; val abetoOsc = t(ABETO_OSC, oscuro); val nieve = t(NIEVE, oscuro); val oro = if (oscuro) LLAMA else ORO
        // Los copos que caen.
        for (k in 0 until 14) {
            val c = Offset(x(-23f + 46f * azar(19, k)), y(-48f + 44f * azar(19, k + 30)))
            drawCircle(if (oscuro) NIEVE.copy(alpha = 0.75f) else NIEVE_SOMBRA, (0.45f + 0.4f * azar(19, k + 60)) * u, c)
        }
        // El montículo de nieve y el tronco.
        drawOval(t(NIEVE_SOMBRA, oscuro), Offset(x(-22f), y(-3.4f)), Size(44 * u, 5.6f * u))
        drawOval(nieve, Offset(x(-21f), y(-3.8f)), Size(42 * u, 4.6f * u))
        drawRect(t(NOGAL, oscuro), Offset(x(-1.8f), y(-7.6f)), Size(3.6f * u, 6 * u))
        // Los pisos del abeto, de abajo arriba: (base, medio ancho, alto).
        val pisos = listOf(Triple(-6.4f, 15f, 13f), Triple(-14.4f, 12.2f, 12f), Triple(-22.4f, 9.4f, 11f), Triple(-30f, 6.4f, 10f))
        pisos.forEach { (by, a, h) ->
            val piso = Path().apply {
                moveTo(x(0f), y(by - h)); lineTo(x(a), y(by))
                for (k in 4 downTo 1) quadraticTo(x(a * (k - 0.5f) / 4 * 2 - a), y(by + 1.4f), x(a * (k - 1) / 4 * 2 - a), y(by))
                close()
            }
            drawPath(piso, abeto)
            drawPath(trazoContemporanea(b, u, true, 0f, by - h, a, by, a * 0.25f, by + 0.6f), abetoOsc)
            drawPath(Path().apply { moveTo(x(-a), y(by)); quadraticTo(x(0f), y(by + 2f), x(a), y(by)) }, nieve, style = Stroke(0.9f * u, cap = StrokeCap.Round))
        }
        // Las guirnaldas de bombillas.
        var n = 0
        pisos.forEachIndexed { i, (by, a, h) ->
            val yy = by - h * 0.32f; val ancho = a * 0.72f; val cuantas = 6 - i
            for (k in 0 until cuantas) {
                val f = k / (cuantas - 1f); val px = -ancho + 2 * ancho * f; val py = yy + 2.2f * (1f - (2 * f - 1) * (2 * f - 1))
                val col = BOMBILLAS[n % BOMBILLAS.size]; n++
                val c = Offset(x(px), y(py))
                if (oscuro) halo(c, 2f * u, col.copy(alpha = 0.14f), 2)
                drawCircle(if (oscuro) mezcla(col, Color.White, 0.2f) else col, 0.85f * u, c)
            }
            drawPath(Path().apply { moveTo(x(-ancho), y(yy)); quadraticTo(x(0f), y(yy + 4.4f), x(ancho), y(yy)) }, t(TINTA, oscuro).copy(alpha = 0.35f), style = Stroke(0.3f * u))
        }
        // La estrella de la punta.
        val st = Offset(x(0f), y(-42f))
        if (oscuro) halo(st, 11 * u, LLAMA.copy(alpha = 0.08f), 5)
        drawPath(estrella(st, 4.6f * u, 5, 0.45f), oro)
        drawPath(estrella(Offset(st.x - 0.4f * u, st.y - 0.4f * u), 2.2f * u, 5, 0.45f), Color.White.copy(alpha = 0.55f))
        // Los regalos al pie.
        listOf(Triple(-16.4f, 7.6f, ROJO), Triple(9.4f, 7f, Color.Unspecified), Triple(-9.4f, 4.6f, AZUL)).forEach { (l, ancho, col) ->
            val cuerpo = if (col == Color.Unspecified) e.acento else t(col, oscuro)
            val alto = ancho * 0.8f
            drawRect(cuerpo, Offset(x(l), y(-1.6f - alto)), Size(ancho * u, alto * u))
            drawRect(mezcla(cuerpo, Color.Black, 0.22f), Offset(x(l + ancho * 0.75f), y(-1.6f - alto)), Size(ancho * 0.25f * u, alto * u))
            val cinta = if (col == ROJO) oro else t(CREMA, oscuro)
            drawLine(cinta, Offset(x(l + ancho / 2), y(-1.6f - alto)), Offset(x(l + ancho / 2), y(-1.6f)), 0.8f * u)
            drawLine(cinta, Offset(x(l), y(-1.6f - alto / 2)), Offset(x(l + ancho), y(-1.6f - alto / 2)), 0.8f * u)
            listOf(-1f, 1f).forEach { s -> drawOval(cinta, Offset(x(l + ancho / 2 + s * 1f - 0.9f), y(-2.8f - alto)), Size(1.8f * u, 1.3f * u)) }
        }
    }

    // ---------------------------------------------------------------- objetos de Tu año

    /**
     * Navidad: la luna creciente que duerme con su gorro de dormir (la borla y el ribete de piel), la bota de nieve con su
     * puño de piel, y una mancuerna cuyos discos son dos regalos con su cinta y su moño.
     */
    override fun DrawScope.objetoAnio(e: EstadoObjeto, oscuro: Boolean) {
        val r = e.radio; val x = e.centro.x; val y = e.centro.y; val u = density
        val col = e.color; val fondo = e.fondo
        fun p(a: Float, b: Float) = Offset(x + a * r, y + b * r)
        fun g(f: Float) = maxOf(r * f, 0.8f * u)
        when (e.forma) {
            "luna" -> {
                drawPath(crecienteContemporanea(p(-0.12f, 0.18f), r * 0.74f, Offset(r * 0.42f, -r * 0.14f), r * 0.56f), col)
                // El gorro sobre el cuerno de arriba, caído hacia la derecha.
                drawPath(trazoAnioContemporanea(e.centro, r) { m(-0.62f, -0.32f); q(-0.4f, -1.12f, 0.34f, -1f); q(0.78f, -0.9f, 0.8f, -0.44f); q(0.58f, -0.72f, 0.18f, -0.56f); z() }, col)
                // El ribete y la borla de piel: del color del fondo, con su orilla del color.
                val ancho = maxOf(r * 0.2f, 1.6f * u)
                drawLine(col, p(-0.64f, -0.3f), p(0.2f, -0.55f), ancho + 2 * g(0.05f), StrokeCap.Round)
                drawLine(fondo, p(-0.64f, -0.3f), p(0.2f, -0.55f), ancho, StrokeCap.Round)
                drawCircle(col, r * 0.18f + g(0.05f), p(0.8f, -0.36f)); drawCircle(fondo, r * 0.18f, p(0.8f, -0.36f))
            }
            "huella" -> {
                drawPath(trazoAnioContemporanea(e.centro, r) {
                    m(-0.52f, -0.55f); l(0.12f, -0.55f); l(0.14f, 0.15f); q(0.2f, 0.28f, 0.6f, 0.32f); q(0.98f, 0.4f, 0.92f, 0.72f)
                    q(0.88f, 0.9f, 0.62f, 0.9f); l(-0.48f, 0.9f); q(-0.6f, 0.9f, -0.58f, 0.7f); z()
                }, col)
                if (r > 6 * u) drawLine(fondo, p(-0.56f, 0.72f), p(0.86f, 0.72f), g(0.05f))
                // El puño de piel.
                val puno = Rect(x - r * 0.66f, y - r * 0.92f, x + r * 0.26f, y - r * 0.48f)
                drawRoundRect(fondo, puno.topLeft, puno.size, CornerRadius(r * 0.2f))
                drawRoundRect(col, puno.topLeft, puno.size, CornerRadius(r * 0.2f), style = Stroke(g(0.08f)))
            }
            "pesa" -> {
                val b = g(0.08f)
                drawRect(col, Offset(x - r * 0.4f, y + r * 0.1f - b), Size(r * 0.8f, 2 * b))
                for (s in listOf(-1f, 1f)) {
                    val cx = s * 0.64f
                    drawRect(col, p(cx - 0.32f, -0.3f), Size(r * 0.64f, r * 0.78f))
                    // La cinta en cruz; muy chica, solo la de arriba abajo, para que no se vuelva una mancha.
                    drawLine(fondo, p(cx, -0.3f), p(cx, 0.48f), g(0.08f))
                    if (r > 7 * u) drawLine(fondo, p(cx - 0.32f, 0.1f), p(cx + 0.32f, 0.1f), g(0.08f))
                    // El moño: dos triángulos que se juntan en el nudo.
                    drawPath(trazoAnioContemporanea(e.centro, r) {
                        m(cx, -0.38f); l(cx - 0.22f, -0.56f); l(cx - 0.22f, -0.26f); z()
                        m(cx, -0.38f); l(cx + 0.22f, -0.56f); l(cx + 0.22f, -0.26f); z()
                    }, col)
                }
            }
            else -> {}
        }
    }
}

/**
 * Abismo: el trofeo se apoya en el lastre del batiscafo sobre el fondo: el bloque de hierro con sus regueros de óxido,
 * las bridas de latón empernadas arriba y abajo, la placa de cota del metal del nivel con sus cuatro pernos, el lecho de
 * arena con sus ondas, una rama de coral y una anémona (de noche, con las puntas encendidas de bioluminiscencia). En el
 * hall, el bloque de teca con sus escuadras de latón y el ojo de buey de aro del metal, por el que brilla una medusa. La
 * placa es la chapa de esmalte de un instrumento, con su filete de metal y los pernos de las esquinas.
 */
object LogrosAbismo : DisenoLogros {
    private val TECA = Color(0xFF7A4E2C)
    private val TECA_LUZ = Color(0xFF9A6A3E)
    private val TECA_VETA = Color(0xFF4E301A)
    private val LATON = Color(0xFFC09A48)
    private val LATON_LUZ = Color(0xFFE2C27E)
    private val LATON_OSC = Color(0xFF7E6128)
    private val LASTRE = Color(0xFF3E4A52)
    private val LASTRE_LUZ = Color(0xFF5E6C75)
    private val OXIDO = Color(0xFF9A5A32)
    private val ARENA = Color(0xFFD9C79A)
    private val ARENA_SOMBRA = Color(0xFFB8A577)
    private val AGUA = Color(0xFF0C2338)
    private val BIOLUZ = Color(0xFF5FF2E0)
    private val CORAL = Color(0xFFC0563F)
    private val ANEMONA = Color(0xFFB77ADB)
    private val TINTA = Color(0xFF1B1E21)
    private val ESMALTE_BLANCO = Color(0xFFF2F1EA)

    // salón: colores
    private val AGUA_CLARA = Color(0xFF3F8FB0)
    private val AGUA_HONDA = Color(0xFF0B3A58)
    private val CASCO = Color(0xFFE4DCC6)
    private val CASCO_SOMBRA = Color(0xFFB8AE94)
    private val FOCO = Color(0xFFFFE3A0)

    /** El color en modo oscuro: más hundido, como en su biblioteca. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF07090B), 0.36f) else c

    // ---------------------------------------------------------------- pedestales y placa

    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val fino = h >= 90 * u
        val hl = maxOf(0.6f * u, h * 0.004f)
        fun c(x: Color) = if (oscuro) mezcla(t(x, true), AGUA, 0.25f) else x
        val brida = Rect(w * 0.13f, h * 0.775f, w * 0.87f, h * 0.8f)
        val bloque = Rect(w * 0.16f, h * 0.8f, w * 0.84f, h * 0.9f)
        val pie = Rect(w * 0.13f, h * 0.9f, w * 0.87f, h * 0.918f)
        val lecho = Path().apply {
            moveTo(w * 0.03f, h * 0.954f); quadraticTo(w * 0.1f, h * 0.905f, w * 0.3f, h * 0.91f); lineTo(w * 0.7f, h * 0.908f)
            quadraticTo(w * 0.92f, h * 0.902f, w * 0.97f, h * 0.954f); close()
        }
        if (!e.hecho) { silueta(oscuro) { addRect(brida); addRect(bloque); addRect(pie); addPath(lecho) }; return }
        sombraPie(w / 2, w * 0.9f, h * 0.958f, h * 0.018f, if (oscuro) 0.5f else 0.2f)
        // El lecho de arena con sus ondas.
        drawPath(lecho, c(ARENA))
        if (fino) for (k in 0 until 3) {
            val y = h * (0.926f + k * 0.009f)
            drawPath(Path().apply { moveTo(w * (0.1f + k * 0.03f), y); quadraticTo(w * 0.3f, y - h * 0.006f, w * 0.5f, y); quadraticTo(w * 0.7f, y + h * 0.006f, w * (0.9f - k * 0.03f), y) }, c(ARENA_SOMBRA), style = Stroke(hl * 0.7f))
        }
        // El bloque de lastre, con su canto de luz y los regueros de óxido.
        drawRect(c(LASTRE), bloque.topLeft, bloque.size)
        drawRect(c(LASTRE_LUZ), bloque.topLeft, Size(bloque.width * 0.1f, bloque.height))
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(bloque.right - bloque.width * 0.1f, bloque.top), Size(bloque.width * 0.1f, bloque.height))
        if (fino) for (k in 0 until 7) {
            val x = bloque.left + bloque.width * (0.08f + 0.84f * azar(e.semilla, k))
            if (x > w * 0.36f && x < w * 0.64f) continue
            drawLine(c(OXIDO).copy(alpha = 0.45f), Offset(x, bloque.top), Offset(x + hl * 0.5f, bloque.top + bloque.height * (0.35f + 0.5f * azar(e.semilla, k + 20))), hl * (0.8f + azar(e.semilla, k + 40)), StrokeCap.Round)
        }
        // Las bridas de latón, arriba y al pie, con sus pernos.
        listOf(brida, pie).forEach { b ->
            drawRect(c(LATON_OSC), b.topLeft, b.size)
            drawRect(c(LATON), b.topLeft, Size(b.width, b.height * 0.7f))
            drawRect(c(LATON_LUZ), b.topLeft, Size(b.width, b.height * 0.22f))
            if (fino) { val n = 7; for (k in 0 until n) drawCircle(c(LATON_OSC), b.height * 0.2f, Offset(b.left + b.width * (k + 0.5f) / n, b.center.y)) }
        }
        // La placa de cota del metal del nivel, con sus cuatro pernos y la regla.
        val pl = Rect(w * 0.37f, bloque.top + bloque.height * 0.2f, w * 0.63f, bloque.bottom - bloque.height * 0.2f)
        chapa(e, pl, hl)
        if (fino) {
            listOf(Offset(pl.left + pl.width * 0.08f, pl.top + pl.height * 0.2f), Offset(pl.right - pl.width * 0.1f, pl.top + pl.height * 0.2f),
                Offset(pl.left + pl.width * 0.08f, pl.bottom - pl.height * 0.26f), Offset(pl.right - pl.width * 0.1f, pl.bottom - pl.height * 0.26f)).forEach { p -> drawCircle(e.metalOscuro, hl * 0.8f, p) }
            for (k in 0..6) drawLine(e.metalOscuro.copy(alpha = 0.7f), Offset(pl.left + pl.width * (0.22f + k * 0.09f), pl.center.y - (if (k % 3 == 0) pl.height * 0.18f else pl.height * 0.08f)), Offset(pl.left + pl.width * (0.22f + k * 0.09f), pl.center.y + pl.height * 0.1f), hl * 0.45f)
        }
        // La rama de coral a la izquierda y la anémona a la derecha.
        if (fino) {
            val base = Offset(w * 0.1f, h * 0.93f)
            listOf(-28f, -8f, 14f).forEachIndexed { i, a ->
                val g = Math.toRadians(-90.0 + a); val l = h * (0.07f - i * 0.012f)
                val p = Offset(base.x + l * cos(g).toFloat(), base.y + l * sin(g).toFloat())
                drawLine(c(CORAL), base, p, hl * 1.6f, StrokeCap.Round)
                drawLine(c(CORAL), p - Offset(0f, -l * 0.3f), p + Offset(l * 0.25f, -l * 0.15f), hl * 1.1f, StrokeCap.Round)
            }
            val an = Offset(w * 0.9f, h * 0.928f)
            drawOval(c(ANEMONA), Offset(an.x - h * 0.016f, an.y - h * 0.012f), Size(h * 0.032f, h * 0.014f))
            for (k in 0 until 7) {
                val a = Math.toRadians(-160.0 + k * 23.0); val l = h * 0.035f
                val p = Offset(an.x + l * cos(a).toFloat(), an.y - h * 0.008f + l * sin(a).toFloat())
                drawLine(c(ANEMONA), Offset(an.x, an.y - h * 0.008f), p, hl, StrokeCap.Round)
                if (oscuro) { drawCircle(BIOLUZ.copy(alpha = 0.3f), hl * 1.8f, p); drawCircle(BIOLUZ, hl * 0.6f, p) }
            }
            if (oscuro) for (k in 0 until 5) drawCircle(BIOLUZ.copy(alpha = 0.5f), hl * 0.5f, Offset(w * (0.06f + 0.88f * azar(e.semilla, k + 60)), h * (0.79f + 0.1f * azar(e.semilla, k + 70))))
        }
    }

    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val m = tonos(e, oscuro)
        fun c(x: Color) = t(x, oscuro)
        sombraPie(w / 2, w * 0.86f, h * 0.95f, h * 0.14f, if (oscuro) 0.5f else 0.2f)
        // El bloque de teca veteada, con su canto de luz.
        val bloque = Rect(w * 0.07f, h * 0.06f, w * 0.93f, h * 0.94f)
        drawRect(c(TECA), bloque.topLeft, bloque.size)
        vetasEn(bloque, c(TECA_VETA).copy(alpha = 0.55f), e.semilla, densidad = 0.8f, grosor = 0.6f)
        drawRect(c(TECA_LUZ), bloque.topLeft, Size(bloque.width, 1f * u))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(bloque.left, bloque.bottom - 1.2f * u), Size(bloque.width, 1.2f * u))
        // Las escuadras de latón de las esquinas, con su perno.
        val l = h * 0.4f; val g = 1.6f * u
        listOf(Offset(1f, 1f), Offset(-1f, 1f), Offset(1f, -1f), Offset(-1f, -1f)).forEach { s ->
            val x = if (s.x > 0) bloque.left else bloque.right; val y = if (s.y > 0) bloque.top else bloque.bottom
            drawRect(c(LATON), Offset(minOf(x, x + s.x * l), minOf(y, y + s.y * g)), Size(l, g))
            drawRect(c(LATON), Offset(minOf(x, x + s.x * g), minOf(y, y + s.y * l)), Size(g, l))
            drawCircle(c(LATON_OSC), 0.6f * u, Offset(x + s.x * g * 0.5f, y + s.y * g * 0.5f))
        }
        // El ojo de buey con su aro del metal del nivel y sus pernos, y el agua honda con una medusa que brilla.
        val cc = Offset(w / 2, h / 2); val r = h * 0.4f
        drawCircle(m.metalOscuro, r, cc)
        drawCircle(m.base, r * 0.88f, cc - Offset(0.3f * u, 0.3f * u))
        for (k in 0 until 8) { val a = k * Math.PI / 4; drawCircle(m.metalOscuro, r * 0.07f, Offset(cc.x + r * 0.74f * cos(a).toFloat(), cc.y + r * 0.74f * sin(a).toFloat())) }
        drawCircle(AGUA, r * 0.58f, cc)
        drawCircle(BIOLUZ.copy(alpha = 0.25f), r * 0.3f, cc + Offset(0f, -r * 0.08f))
        drawArc(BIOLUZ, 180f, 180f, true, Offset(cc.x - r * 0.18f, cc.y - r * 0.24f), Size(r * 0.36f, r * 0.3f))
        for (k in 0 until 3) drawLine(BIOLUZ.copy(alpha = 0.8f), Offset(cc.x + r * (-0.1f + k * 0.1f), cc.y - r * 0.08f), Offset(cc.x + r * (-0.12f + k * 0.11f), cc.y + r * 0.26f), 0.5f * u)
        drawArc(Color.White.copy(alpha = 0.4f), 200f, 60f, false, Offset(cc.x - r * 0.48f, cc.y - r * 0.48f), Size(r * 0.96f, r * 0.96f), style = Stroke(0.8f * u))
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFD8F4F0) else TINTA
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) BIOLUZ else Color(0xFF17646C)

    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val m = tonos(e, oscuro)
        val r0 = Rect(Offset.Zero, size)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.22f), Offset(0.5f * u, 1.6f * u), size, CornerRadius(4 * u))
        // El aro del metal del nivel y la chapa de esmalte (de noche, del azul del agua honda).
        drawRoundRect(m.metalOscuro, Offset.Zero, size, CornerRadius(4 * u))
        filete(m, redondo(r0.deflate(1.3f * u), 3 * u), 2.4f * u)
        val cara = r0.deflate(2.6f * u)
        drawRoundRect(if (oscuro) AGUA else ESMALTE_BLANCO, cara.topLeft, cara.size, CornerRadius(2 * u))
        drawRoundRect((if (oscuro) BIOLUZ else Color(0xFF17646C)).copy(alpha = 0.35f), cara.topLeft + Offset(1.2f * u, 1.2f * u), Size(cara.width - 2.4f * u, cara.height - 2.4f * u), CornerRadius(1.4f * u), style = Stroke(0.5f * u))
        // Los cuatro pernos de las esquinas, con su ranura.
        listOf(Offset(4.6f * u, 4.6f * u), Offset(w - 4.6f * u, 4.6f * u), Offset(4.6f * u, h - 4.6f * u), Offset(w - 4.6f * u, h - 4.6f * u)).forEachIndexed { i, p ->
            boton(m, p, 1.3f * u)
            if (m.nivel != 4) rotate(30f + i * 40f, p) { drawLine(m.metalOscuro, p - Offset(1f * u, 0f), p + Offset(1f * u, 0f), 0.4f * u) }
        }
    }

    // ---------------------------------------------------------------- el salón del hall

    /**
     * La sala de observación de una estación abisal: cada franja de trofeos se asoma a un gran ventanal de bordes redondos,
     * con su marco de latón remachado, por el que se ve el fondo del mar (de día, el agua azul con los rayos de luz que
     * bajan; de noche, negra, con los destellos de la bioluz): la nieve marina, un banco de peces, una medusa con sus
     * tentáculos, el pez abisal con su señuelo y, abajo, el lecho de arena con corales y anémonas. Entre los ventanales, las
     * cuadernas del casco, de chapa crema remachada, con dos tubos (uno con su manómetro en cada piso) y la lámpara enjaulada
     * de mamparo (de noche, encendida). Arriba, la bóveda de planchas con su conducto de bridas; abajo, la cubierta de
     * rejilla. En la esquina izquierda, la escafandra de latón en su soporte; en la derecha, las botellas de aire atadas.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) = clipRect { salon(e, oscuro) }

    /** La escena del salón, recortada al lienzo por [escenaSalon]. */
    private fun DrawScope.salon(e: EstadoSalon, oscuro: Boolean) {
        val p = planta(e); val u = p.u; val w = p.w; val h = p.h
        fun c(x: Color, f: Float) = mezcla(e.fondo, t(x, oscuro), f)
        val techo = 34 * u; val cubierta = h - 22 * u; val banda = 15 * u

        // El agua detrás de todo (un degradado, del azul de arriba al hondo de abajo).
        val agua = if (oscuro) listOf(AGUA, mezcla(AGUA, Color.Black, 0.7f)) else listOf(AGUA_CLARA, AGUA_HONDA)
        drawRect(Brush.verticalGradient(agua.map { mezcla(e.fondo, it, if (oscuro) 0.7f else 0.28f) }, techo, cubierta), Offset(0f, techo), Size(w, cubierta - techo))
        val ventanas = (0 until p.n).map { i -> RoundRect(Rect(p.lx(i) + banda, techo + 8 * u, p.lx(i + 1) - banda, cubierta - 8 * u), CornerRadius(18 * u)) }
        val vidrio = Path().apply { ventanas.forEach { addRoundRect(it) } }
        clipPath(vidrio) {
            // De día, los rayos de luz que bajan; de noche, nada más la bioluz.
            if (!oscuro) for (k in 0 until p.n * 3) {
                val x = w * (k + 0.3f) / (p.n * 3)
                drawPath(Path().apply { moveTo(x, techo); lineTo(x + 10 * u, techo); lineTo(x + 50 * u, cubierta); lineTo(x + 30 * u, cubierta); close() }, Color.White.copy(alpha = 0.07f))
            }
            // La nieve marina.
            for (k in 0 until 70) drawCircle((if (oscuro) BIOLUZ else Color.White).copy(alpha = if (oscuro) 0.25f else 0.4f), (0.5f + 0.7f * azar(e.semilla + 3, k)) * u, Offset(w * azar(e.semilla + 4, k), techo + (cubierta - techo) * azar(e.semilla + 5, k)))
            for (i in 0 until p.n) {
                val x0 = p.lx(i) + banda; val x1 = p.lx(i + 1) - banda
                val sil = if (oscuro) mezcla(e.fondo, Color(0xFF0A2A3A), 0.9f) else c(AGUA_HONDA, 0.32f)
                for (k in 0 until p.filas) {
                    val y = p.y(k) + p.alto * 0.4f
                    if (y > cubierta - 60 * u) continue
                    when ((i + k + e.semilla).mod(3)) {
                        0 -> for (j in 0 until 5) { // un banco de peces
                            val q = Offset(x0 + 10 * u + j * 7 * u, y + (if (j % 2 == 0) 0f else 5f) * u - 40 * u)
                            drawOval(sil, Offset(q.x - 3 * u, q.y - 1.2f * u), Size(6 * u, 2.4f * u))
                            drawPath(Path().apply { moveTo(q.x + 2.5f * u, q.y); lineTo(q.x + 5 * u, q.y - 1.8f * u); lineTo(q.x + 5 * u, q.y + 1.8f * u); close() }, sil)
                        }
                        1 -> { // una medusa
                            val m = Offset(x1 - 16 * u, y - 30 * u); val col = if (oscuro) ANEMONA else Color.White
                            if (oscuro) halo(m, 14 * u, ANEMONA.copy(alpha = 0.06f), 3)
                            drawArc(col.copy(alpha = if (oscuro) 0.6f else 0.45f), 180f, 180f, true, Offset(m.x - 7 * u, m.y - 6 * u), Size(14 * u, 12 * u))
                            for (j in 0 until 4) drawPath(Path().apply { val tx = m.x - 5 * u + j * 3.3f * u; moveTo(tx, m.y); quadraticTo(tx - 3 * u, m.y + 8 * u, tx + 1 * u, m.y + 16 * u) }, col.copy(alpha = if (oscuro) 0.45f else 0.35f), style = Stroke(0.7f * u))
                        }
                        else -> { // el pez abisal con su señuelo
                            val q = Offset(x0 + 20 * u, y - 20 * u)
                            drawOval(sil, Offset(q.x - 9 * u, q.y - 6 * u), Size(18 * u, 12 * u))
                            drawPath(Path().apply { moveTo(q.x - 8 * u, q.y); lineTo(q.x - 15 * u, q.y - 5 * u); lineTo(q.x - 15 * u, q.y + 5 * u); close() }, sil)
                            drawPath(Path().apply { moveTo(q.x + 5 * u, q.y - 5 * u); quadraticTo(q.x + 10 * u, q.y - 15 * u, q.x + 14 * u, q.y - 9 * u) }, sil, style = Stroke(0.8f * u))
                            val luz = Offset(q.x + 14 * u, q.y - 9 * u)
                            if (oscuro) halo(luz, 10 * u, BIOLUZ.copy(alpha = 0.1f), 3)
                            drawCircle(if (oscuro) BIOLUZ else c(BIOLUZ, 0.5f), 1.6f * u, luz)
                            drawLine(if (oscuro) BIOLUZ.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.4f), Offset(q.x + 4 * u, q.y + 2 * u), Offset(q.x + 8 * u, q.y + 2 * u), 0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.8f * u, 0.8f * u)))
                        }
                    }
                }
                // El lecho de arena con sus dunas, corales y anémonas al fondo del ventanal.
                val lecho = cubierta - 30 * u
                drawPath(Path().apply { moveTo(x0, cubierta); lineTo(x0, lecho + 6 * u); quadraticTo(x0 + (x1 - x0) * 0.3f, lecho - 4 * u, (x0 + x1) / 2, lecho + 4 * u); quadraticTo(x0 + (x1 - x0) * 0.75f, lecho + 10 * u, x1, lecho); lineTo(x1, cubierta); close() }, c(ARENA, if (oscuro) 0.25f else 0.45f))
                listOf(0.15f, 0.82f).forEachIndexed { j, f ->
                    val cx = x0 + (x1 - x0) * f
                    val coral = Path().apply { moveTo(cx, lecho + 8 * u); lineTo(cx, lecho - 10 * u); moveTo(cx, lecho - 2 * u); lineTo(cx - 6 * u, lecho - 12 * u); moveTo(cx, lecho - 5 * u); lineTo(cx + 5 * u, lecho - 14 * u); moveTo(cx - 3 * u, lecho - 7 * u); lineTo(cx - 3 * u, lecho - 16 * u) }
                    drawPath(coral, c(if (j == 0) CORAL else ANEMONA, if (oscuro) 0.45f else 0.6f), style = Stroke(2 * u, cap = StrokeCap.Round))
                }
                val an = Offset(x0 + (x1 - x0) * 0.45f, lecho + 4 * u)
                for (j in 0 until 7) { val a = PI * (0.1 + 0.8 * j / 6); drawLine(c(ANEMONA, if (oscuro) 0.5f else 0.55f), an, Offset(an.x - (7 * u * cos(a)).toFloat(), an.y - (8 * u * sin(a)).toFloat()), 1.2f * u, StrokeCap.Round) }
            }
        }

        // El casco: chapa crema remachada en todo lo que no es ventanal, con sus juntas de planchas.
        val casco = Path().apply { fillType = PathFillType.EvenOdd; addRect(Rect(0f, 0f, w, h)); ventanas.forEach { addRoundRect(it) } }
        drawPath(casco, c(CASCO, if (oscuro) 0.22f else 0.55f))
        clipPath(casco) {
            var y = techo + 70 * u; var k = 0
            while (y < cubierta && k < 30) { drawLine(c(CASCO_SOMBRA, if (oscuro) 0.3f else 0.5f), Offset(0f, y), Offset(w, y), 1 * u); y += maxOf(110 * u, 1f); k++ }
        }
        // Los marcos de latón de los ventanales, con sus remaches.
        ventanas.forEachIndexed { i, r ->
            drawPath(Path().apply { addRoundRect(r) }, c(LATON, if (oscuro) 0.55f else 0.7f), style = Stroke(4 * u))
            drawPath(Path().apply { addRoundRect(RoundRect(r.left - 3 * u, r.top - 3 * u, r.right + 3 * u, r.bottom + 3 * u, CornerRadius(21 * u))) }, c(LATON_OSC, 0.5f), style = Stroke(1 * u))
            drawPath(Path().apply { addRoundRect(r) }, Color.White.copy(alpha = if (oscuro) 0.06f else 0.2f), style = Stroke(1 * u))
            listOf(r.left - 6 * u, r.right + 6 * u).forEach { x ->
                var y = r.top + 20 * u; var k = 0
                while (y < r.bottom - 20 * u && k < 60) { drawCircle(c(LATON_OSC, 0.65f), 1.2f * u, Offset(x, y)); y += maxOf(24 * u, 1f); k++ }
            }
            // El reflejo en el vidrio, una franja curva arriba a la izquierda.
            drawArc(Color.White.copy(alpha = if (oscuro) 0.05f else 0.18f), 185f, 60f, false, Offset(r.left + 5 * u, r.top + 5 * u), Size(36 * u, 36 * u), style = Stroke(2.4f * u))
        }

        // Las cuadernas entre los ventanales: los dos tubos, los manómetros y la lámpara enjaulada en cada piso.
        for (i in 0..p.n) {
            val x = p.lx(i)
            drawRect(c(CASCO_SOMBRA, if (oscuro) 0.3f else 0.55f), Offset(x - 5 * u, techo), Size(10 * u, cubierta - techo))
            listOf(-3f, 3f).forEach { dx -> var y = techo + 10 * u; var k = 0; while (y < cubierta && k < 70) { drawCircle(c(LASTRE, 0.5f), 0.9f * u, Offset(x + dx * u, y)); y += maxOf(18 * u, 1f); k++ } }
            listOf(-1f, 1f).forEach { s ->
                if ((i == 0 && s < 0) || (i == p.n && s > 0)) return@forEach
                val tx = x + s * 9 * u
                drawLine(c(if (s < 0) LASTRE else OXIDO, if (oscuro) 0.5f else 0.6f), Offset(tx, techo), Offset(tx, cubierta), 3.4f * u)
                drawLine(c(Color.White, 0.2f), Offset(tx - 0.8f * u, techo), Offset(tx - 0.8f * u, cubierta), 0.7f * u)
            }
            for (k in 0 until p.filas) {
                val y = p.y(k) + 34 * u
                if (y < techo + 20 * u || y > cubierta - 60 * u) continue
                if (i in 1 until p.n || p.n == 1) {
                    val m = Offset(x, y + 60 * u)
                    manometro(m, 6.5f * u, c(LATON, 0.8f), c(ESMALTE_BLANCO, if (oscuro) 0.5f else 0.85f), c(CORAL, 0.9f), 0.3f + 0.5f * azar(e.semilla, i * 8 + k))
                }
                // La lámpara de mamparo: la jaula sobre el globo, en la cuaderna.
                val lc = Offset(x, y)
                if (oscuro) halo(lc, 34 * u, FOCO.copy(alpha = 0.035f), 5)
                drawRoundRect(c(LASTRE, 0.7f), Offset(lc.x - 5 * u, lc.y - 7 * u), Size(10 * u, 14 * u), CornerRadius(4 * u))
                drawOval(if (oscuro) mezcla(e.fondo, FOCO, 0.85f) else c(ESMALTE_BLANCO, 0.8f), Offset(lc.x - 3.6f * u, lc.y - 5.5f * u), Size(7.2f * u, 11 * u))
                drawLine(c(LASTRE, 0.8f), Offset(lc.x - 4 * u, lc.y), Offset(lc.x + 4 * u, lc.y), 0.8f * u)
                drawLine(c(LASTRE, 0.8f), Offset(lc.x, lc.y - 6 * u), Offset(lc.x, lc.y + 6 * u), 0.8f * u)
            }
        }

        // Arriba: la bóveda de planchas remachadas y el conducto con sus bridas.
        drawRect(c(CASCO_SOMBRA, if (oscuro) 0.3f else 0.6f), Offset.Zero, Size(w, techo))
        run { var x = 0f; var k = 0; while (x < w && k < 40) { drawLine(c(LASTRE, 0.35f), Offset(x, 0f), Offset(x, techo - 12 * u), 0.8f * u); for (j in 0 until 2) drawCircle(c(LASTRE, 0.5f), 0.9f * u, Offset(x + 3 * u, (5 + 8 * j) * u)); x += maxOf(32 * u, 1f); k++ } }
        drawRect(c(LASTRE, if (oscuro) 0.5f else 0.55f), Offset(0f, techo - 12 * u), Size(w, 9 * u))
        drawLine(c(LASTRE_LUZ, 0.6f), Offset(0f, techo - 10.5f * u), Offset(w, techo - 10.5f * u), 1 * u)
        run { var x = 20 * u; var k = 0; while (x < w && k < 30) { drawRect(c(LATON_OSC, 0.6f), Offset(x, techo - 14 * u), Size(3 * u, 13 * u)); x += maxOf(56 * u, 1f); k++ } }

        // Abajo: la cubierta de rejilla de acero.
        drawRect(c(LASTRE, if (oscuro) 0.5f else 0.5f), Offset(0f, cubierta), Size(w, h - cubierta))
        drawRect(c(LATON, 0.6f), Offset(0f, cubierta), Size(w, 2 * u))
        run { var x = 0f; var k = 0; while (x < w && k < 80) { drawLine(c(Color.Black, 0.5f), Offset(x, cubierta + 3 * u), Offset(x + 6 * u, h), 1.2f * u); x += maxOf(7 * u, 1f); k++ } }
        listOf(cubierta + 9 * u, cubierta + 15 * u).forEach { y -> drawLine(c(LASTRE_LUZ, 0.5f), Offset(0f, y), Offset(w, y), 0.8f * u) }

        // La escafandra de latón en su soporte, en la esquina izquierda: el casco con sus tres mirillas y el peto.
        run {
            val cc = Offset(14 * u, cubierta - 26 * u)
            drawRect(c(TECA, 0.75f), Offset(cc.x - 8 * u, cc.y + 12 * u), Size(16 * u, cubierta - cc.y - 12 * u))
            drawRoundRect(c(LATON, if (oscuro) 0.6f else 0.75f), Offset(cc.x - 13 * u, cc.y + 6 * u), Size(26 * u, 8 * u), CornerRadius(3 * u))
            drawCircle(c(LATON, if (oscuro) 0.6f else 0.75f), 11 * u, cc)
            drawCircle(c(LATON_LUZ, 0.8f), 3 * u, cc - Offset(5 * u, 5 * u))
            drawCircle(c(AGUA, 0.8f), 4.4f * u, cc + Offset(1 * u, 1 * u)); drawCircle(c(LATON_OSC, 0.8f), 4.4f * u, cc + Offset(1 * u, 1 * u), style = Stroke(1.4f * u))
            listOf(-1f, 1f).forEach { s -> drawCircle(c(AGUA, 0.7f), 2.2f * u, cc + Offset(s * 8 * u, -2 * u)); drawCircle(c(LATON_OSC, 0.8f), 2.2f * u, cc + Offset(s * 8 * u, -2 * u), style = Stroke(0.9f * u)) }
            listOf(-10f, -4f, 4f, 10f).forEach { dx -> drawCircle(c(LATON_OSC, 0.8f), 0.9f * u, Offset(cc.x + dx * u, cc.y + 10 * u)) }
        }
        // Las botellas de aire, atadas con su correa, en la esquina derecha.
        run {
            val x1 = w - 4 * u
            for (j in 0 until 2) {
                val x = x1 - 7 * u - j * 12 * u; val y0 = cubierta - 44 * u
                drawRoundRect(c(if (j == 0) CORAL else ESMALTE_BLANCO, if (oscuro) 0.5f else 0.65f), Offset(x - 5 * u, y0), Size(10 * u, cubierta - y0), CornerRadius(5 * u))
                drawRect(c(LATON, 0.8f), Offset(x - 1.6f * u, y0 - 5 * u), Size(3.2f * u, 5 * u))
                drawCircle(c(LATON, 0.8f), 2.4f * u, Offset(x, y0 - 6 * u), style = Stroke(1 * u))
                drawLine(Color.White.copy(alpha = 0.3f), Offset(x - 3 * u, y0 + 4 * u), Offset(x - 3 * u, cubierta - 4 * u), 1 * u)
            }
            listOf(cubierta - 32 * u, cubierta - 12 * u).forEach { y -> drawRect(c(TECA_VETA, 0.8f), Offset(x1 - 31 * u, y), Size(30 * u, 2.4f * u)) }
        }
    }

    // ---------------------------------------------------------------- las campañas

    /**
     * El batiscafo que baja por la fosa, de lado y hacia la derecha: el flotador alargado de esmalte con su franja de óxido y
     * sus remaches, la torreta con su barandilla y el mástil con el gallardete del color de acento, las dos tolvas de lastre,
     * la hélice de popa, la esfera de latón colgada debajo con su ojo de buey encendido, la cadena guía que toca el fondo y el
     * foco de proa con su haz; suben burbujas de la torreta. De noche, el haz y el ojo de buey brillan con su halo y el casco
     * lleva un filo claro.
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density; val p = e.punto
        sombraMarcadorContemporanea(p, 22 * u, e.salto, oscuro)
        val o = Offset(p.x, p.y - subidaContemporanea(e.salto))
        fun x(v: Float) = o.x + v * u
        fun y(v: Float) = o.y + v * u
        val casco = t(CASCO, oscuro); val sombra = t(CASCO_SOMBRA, oscuro); val laton = t(LATON, oscuro); val latonOsc = t(LATON_OSC, oscuro)
        val lastre = t(LASTRE, oscuro); val bio = BIOLUZ
        // El haz del foco hacia el fondo.
        drawPath(trazoContemporanea(o, u, true, 11.6f, -8.6f, 26f, -1f, 18f, 1.4f, 11f, -7.4f), FOCO.copy(alpha = if (oscuro) 0.2f else 0.22f))
        // La cadena guía.
        drawLine((if (oscuro) LASTRE_LUZ else TINTA), Offset(x(-3.6f), y(-8f)), Offset(p.x - 3.6f * u, p.y - 0.4f * u), 0.5f * u, cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.8f * u, 0.5f * u)))
        // Las tolvas de lastre y la esfera con su ojo de buey.
        drawRoundRect(lastre, Offset(x(-9.4f), y(-9.6f)), Size(3.4f * u, 4.6f * u), CornerRadius(0.8f * u))
        drawRoundRect(lastre, Offset(x(6.4f), y(-9.6f)), Size(3.4f * u, 4.6f * u), CornerRadius(0.8f * u))
        drawRect(latonOsc, Offset(x(-0.6f), y(-9.4f)), Size(2.4f * u, 2 * u))
        val esfera = Offset(x(0.6f), y(-4.8f))
        if (oscuro) drawCircle(Color(0xFFEDE6D6).copy(alpha = 0.45f), 4.7f * u, esfera)
        drawCircle(latonOsc, 4.2f * u, esfera)
        drawCircle(laton, 3.6f * u, Offset(esfera.x - 0.3f * u, esfera.y - 0.3f * u))
        drawArc(t(LATON_LUZ, oscuro), 200f, 70f, false, Offset(esfera.x - 3f * u, esfera.y - 3f * u), Size(6 * u, 6 * u), style = Stroke(0.6f * u))
        val ojo = Offset(esfera.x + 2f * u, esfera.y - 0.2f * u)
        halo(ojo, 3.4f * u, bio.copy(alpha = if (oscuro) 0.2f else 0.12f), 2)
        drawCircle(latonOsc, 1.3f * u, ojo); drawCircle(bio, 0.85f * u, ojo)
        // El flotador: el esmalte, la franja de óxido, los remaches y el filo.
        val flotador = Path().apply { addRoundRect(RoundRect(Rect(x(-13.4f), y(-16.2f), x(13.4f), y(-8.8f)), CornerRadius(3.7f * u))) }
        drawPath(flotador, if (oscuro) Color(0xFFEDE6D6).copy(alpha = 0.5f) else t(TINTA, false).copy(alpha = 0.55f), style = Stroke(1f * u))
        drawPath(flotador, casco)
        clipPath(flotador) {
            drawRect(sombra, Offset(x(-14f), y(-11.4f)), Size(28 * u, 2.8f * u))
            drawRect(t(OXIDO, oscuro), Offset(x(-14f), y(-12.6f)), Size(28 * u, 1.2f * u))
        }
        for (k in 0 until 9) drawCircle(sombra, 0.32f * u, Offset(x(-10.4f + k * 2.6f), y(-14.6f)))
        drawLine(Color.White.copy(alpha = 0.5f), Offset(x(-10f), y(-15.4f)), Offset(x(9f), y(-15.4f)), 0.5f * u)
        // La hélice de popa.
        drawLine(latonOsc, Offset(x(-13.4f), y(-12.4f)), Offset(x(-15f), y(-12.4f)), 0.6f * u)
        drawOval(laton, Offset(x(-15.8f), y(-15f)), Size(1.4f * u, 5.2f * u))
        // El foco de proa.
        drawRect(lastre, Offset(x(10.4f), y(-9.4f)), Size(1.8f * u, 1.6f * u))
        if (oscuro) halo(Offset(x(12f), y(-8.4f)), 4 * u, FOCO.copy(alpha = 0.16f), 3)
        drawCircle(FOCO, 0.8f * u, Offset(x(12.2f), y(-8.4f)))
        // La torreta, su barandilla y el mástil con el gallardete de acento.
        drawRoundRect(casco, Offset(x(-3f), y(-20.4f)), Size(6 * u, 4.6f * u), CornerRadius(1.4f * u))
        drawRoundRect(sombra, Offset(x(-3f), y(-20.4f)), Size(6 * u, 4.6f * u), CornerRadius(1.4f * u), style = Stroke(0.4f * u))
        drawLine((if (oscuro) LASTRE_LUZ else TINTA), Offset(x(-5.4f), y(-18.8f)), Offset(x(5.4f), y(-18.8f)), 0.35f * u)
        listOf(-5.4f, 5.4f).forEach { px -> drawLine(t(LASTRE_LUZ, oscuro), Offset(x(px), y(-18.8f)), Offset(x(px), y(-16.2f)), 0.35f * u) }
        drawLine(lastre, Offset(x(1f), y(-20.4f)), Offset(x(1f), y(-25.4f)), 0.5f * u)
        drawPath(trazoContemporanea(o, u, true, 1f, -25.4f, 5.4f, -24.4f, 1f, -23.2f), e.acento)
        // Las burbujas.
        listOf(Triple(-2f, -23f, 0.7f), Triple(-3.4f, -26f, 0.9f), Triple(-2.2f, -29.4f, 1.1f)).forEach { (px, py, r) ->
            drawCircle((if (oscuro) bio else t(AGUA_CLARA, false)).copy(alpha = 0.75f), r * u, Offset(x(px), y(py)), style = Stroke(0.35f * u))
        }
    }

    /**
     * El fondo de la fosa, alcanzado: la loma de arena con sus rizos, la almeja gigante abierta con sus costillas y, dentro,
     * la perla que brilla; a un lado, el coral rojo ramificado y la bandera del color de acento clavada en la arena; al otro,
     * la anémona violeta; arriba, la medusa con sus tentáculos y una columna de burbujas. De noche, la perla, la medusa y
     * los puntitos de bioluz resplandecen.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val b = pieVictoriaContemporanea(e.meta, 52 * u, 50 * u, 2 * u)
        fun x(v: Float) = b.x + v * u
        fun y(v: Float) = b.y + v * u
        val arena = t(ARENA, oscuro); val arenaSom = t(ARENA_SOMBRA, oscuro); val bio = BIOLUZ
        val concha = t(ESMALTE_BLANCO, oscuro); val nacar = t(Color(0xFFF2C9C0), oscuro)
        // Los puntitos de bioluz.
        for (k in 0 until 10) {
            val c = Offset(x(-24f + 48f * azar(43, k)), y(-46f + 36f * azar(43, k + 20)))
            if (oscuro) halo(c, 2 * u, bio.copy(alpha = 0.14f), 2)
            drawCircle(bio.copy(alpha = if (oscuro) 0.9f else 0.6f), 0.45f * u, c)
        }
        // La loma de arena.
        drawArc(arenaSom, 180f, 180f, true, Offset(x(-25f), y(-9f)), Size(50 * u, 18 * u))
        drawArc(arena, 180f, 180f, true, Offset(x(-24f), y(-8.2f)), Size(48 * u, 16.4f * u))
        for (k in 0 until 3) drawArc(arenaSom, 200f, 50f, false, Offset(x(-18f + k * 12f), y(-4.6f + (k % 2) * 1.4f)), Size(9 * u, 4 * u), style = Stroke(0.5f * u))
        // La bandera clavada.
        drawLine(t(LATON, oscuro), Offset(x(-13f), y(-6f)), Offset(x(-13f), y(-32f)), 0.8f * u)
        drawPath(Path().apply { moveTo(x(-13f), y(-32f)); quadraticTo(x(-8f), y(-34f), x(-3f), y(-31f)); lineTo(x(-3f), y(-24.6f)); quadraticTo(x(-8f), y(-27.4f), x(-13f), y(-25.6f)); close() }, e.acento)
        drawCircle(t(LATON_LUZ, oscuro), 0.8f * u, Offset(x(-13f), y(-32.4f)))
        // El coral ramificado.
        fun rama(a: Offset, c: Offset, g: Float) = drawLine(t(CORAL, oscuro), a, c, g * u, StrokeCap.Round)
        val raiz = Offset(x(-19f), y(-5f))
        rama(raiz, Offset(x(-20f), y(-13f)), 1.3f); rama(Offset(x(-20f), y(-13f)), Offset(x(-23f), y(-18f)), 0.9f); rama(Offset(x(-20f), y(-13f)), Offset(x(-18f), y(-19f)), 0.9f)
        rama(Offset(x(-19.6f), y(-9f)), Offset(x(-16f), y(-13f)), 0.9f); rama(Offset(x(-23f), y(-18f)), Offset(x(-22.6f), y(-21f)), 0.6f)
        // La anémona.
        val an = Offset(x(17f), y(-6.6f))
        drawRoundRect(t(ANEMONA, oscuro), Offset(an.x - 2.4f * u, an.y - 3f * u), Size(4.8f * u, 4 * u), CornerRadius(1.4f * u))
        for (k in 0 until 9) {
            val a = (PI * (1.12 + k * 0.095)).toFloat()
            drawPath(Path().apply { moveTo(an.x, an.y - 2.6f * u); quadraticTo(an.x + 3 * u * cos(a), an.y - 2.6f * u + 4 * u * sin(a) - 1.6f * u, an.x + 5.4f * u * cos(a), an.y - 2.6f * u + 5.4f * u * sin(a)) },
                mezcla(t(ANEMONA, oscuro), Color.White, 0.3f), style = Stroke(0.6f * u, cap = StrokeCap.Round))
        }
        // La almeja abierta: la valva de abajo, la de arriba levantada, el nácar y la perla.
        val bisagra = Offset(x(1f), y(-7f))
        drawArc(nacar, 180f, 180f, true, Offset(bisagra.x - 11 * u, bisagra.y - 14 * u), Size(22 * u, 26 * u))
        for (k in 0..8) { val a = (PI + k * PI / 8).toFloat(); drawLine(concha, bisagra, Offset(bisagra.x + 11 * u * cos(a), bisagra.y + 13 * u * sin(a)), 1.1f * u) }
        drawArc(concha, 180f, 180f, false, Offset(bisagra.x - 11 * u, bisagra.y - 13 * u), Size(22 * u, 26 * u), style = Stroke(1.2f * u))
        val perla = Offset(bisagra.x, bisagra.y - 1.6f * u)
        halo(perla, (if (oscuro) 11f else 7f) * u, bio.copy(alpha = if (oscuro) 0.08f else 0.07f), 5)
        drawCircle(Color(0xFFF6F4EE), 2.6f * u, perla)
        drawCircle(mezcla(bio, Color.White, 0.6f), 2.6f * u, perla, style = Stroke(0.4f * u))
        drawCircle(Color.White, 0.8f * u, Offset(perla.x - 0.9f * u, perla.y - 0.9f * u))
        drawPath(Path().apply { moveTo(bisagra.x - 11 * u, bisagra.y); quadraticTo(bisagra.x, bisagra.y + 1.6f * u, bisagra.x + 11 * u, bisagra.y); quadraticTo(bisagra.x, bisagra.y + 6.4f * u, bisagra.x - 11 * u, bisagra.y); close() }, concha)
        for (k in 1..5) drawLine(t(CASCO_SOMBRA, oscuro), Offset(bisagra.x + (-11 + k * 3.66f) * u, bisagra.y + 0.6f * u), Offset(bisagra.x + (-11 + k * 3.66f) * 0.4f * u, bisagra.y + 3.6f * u), 0.4f * u)
        // La medusa.
        val me = Offset(x(14f), y(-36f))
        if (oscuro) halo(me, 10 * u, ANEMONA.copy(alpha = 0.07f), 4)
        val tono = if (oscuro) mezcla(ANEMONA, Color.White, 0.3f) else ANEMONA
        listOf(-3f, -1f, 1f, 3f).forEach { dx -> drawPath(Path().apply { moveTo(me.x + dx * u, me.y); quadraticTo(me.x + (dx - 1.4f) * u, me.y + 4 * u, me.x + dx * 1.2f * u, me.y + 8 * u) }, tono.copy(alpha = 0.75f), style = Stroke(0.5f * u)) }
        drawArc(tono.copy(alpha = 0.8f), 180f, 180f, true, Offset(me.x - 4.6f * u, me.y - 4.4f * u), Size(9.2f * u, 8.8f * u))
        drawArc(Color.White.copy(alpha = 0.4f), 200f, 60f, false, Offset(me.x - 3.4f * u, me.y - 3.4f * u), Size(6.8f * u, 6.8f * u), style = Stroke(0.6f * u))
        // La columna de burbujas.
        listOf(Triple(3f, -24f, 0.8f), Triple(4.4f, -29f, 1.1f), Triple(2.6f, -34f, 1.3f), Triple(4f, -40f, 1.6f)).forEach { (px, py, r) ->
            drawCircle((if (oscuro) bio else t(AGUA_CLARA, false)).copy(alpha = 0.7f), r * u, Offset(x(px), y(py)), style = Stroke(0.4f * u))
        }
    }

    // ---------------------------------------------------------------- objetos de Tu año

    /**
     * Abismo: la luna vista por el ojo de buey (el aro de bronce con sus pernos), la bota de buzo con su suela de plomo,
     * sus correas y las burbujas que suben, y el ancla, lo más pesado que se lleva al fondo.
     */
    override fun DrawScope.objetoAnio(e: EstadoObjeto, oscuro: Boolean) {
        val r = e.radio; val x = e.centro.x; val y = e.centro.y; val u = density
        val col = e.color; val fondo = e.fondo
        fun p(a: Float, b: Float) = Offset(x + a * r, y + b * r)
        fun g(f: Float) = maxOf(r * f, 0.8f * u)
        when (e.forma) {
            "luna" -> {
                drawCircle(col, r * 0.8f, e.centro, style = Stroke(maxOf(r * 0.24f, 1.4f * u)))
                if (r > 6 * u) for (k in 0 until 6) {
                    val a = (k * PI / 3 + PI / 6).toFloat()
                    drawCircle(fondo, maxOf(r * 0.06f, 0.5f * u), p(0.8f * cos(a), 0.8f * sin(a)))
                }
                drawPath(crecienteContemporanea(p(-0.08f, 0.02f), r * 0.42f, Offset(r * 0.2f, -r * 0.13f), r * 0.35f), col)
            }
            "huella" -> {
                drawPath(trazoAnioContemporanea(e.centro, r) {
                    m(-0.5f, -0.88f); l(0.1f, -0.88f); l(0.12f, 0.1f); q(0.3f, 0.2f, 0.6f, 0.24f); q(0.96f, 0.3f, 0.92f, 0.6f); l(-0.52f, 0.6f); z()
                }, col)
                // La suela de plomo, gruesa, separada del cuero por una raya del fondo.
                drawRoundRect(col, p(-0.62f, 0.56f), Size(r * 1.6f, r * 0.34f), CornerRadius(r * 0.08f))
                if (r > 6 * u) drawLine(fondo, p(-0.56f, 0.58f), p(0.92f, 0.58f), g(0.05f))
                listOf(-0.56f, -0.22f).forEach { b -> drawLine(fondo, p(-0.5f, b), p(0.11f, b), g(0.08f)) }
                drawCircle(col, r * 0.13f, p(0.5f, -0.5f), style = Stroke(g(0.06f)))
                drawCircle(col, r * 0.09f, p(0.7f, -0.82f), style = Stroke(g(0.06f)))
            }
            "pesa" -> {
                val gr = g(0.15f)
                drawCircle(col, r * 0.15f, p(0f, -0.76f), style = Stroke(g(0.09f)))
                drawLine(col, p(0f, -0.6f), p(0f, 0.78f), gr)
                drawLine(col, p(-0.42f, -0.42f), p(0.42f, -0.42f), gr, StrokeCap.Round)
                drawArc(col, 15f, 150f, false, p(-0.62f, -0.44f), Size(r * 1.24f, r * 1.24f), style = Stroke(gr))
                // Las uñas en las puntas de los brazos.
                for (s in listOf(-1f, 1f)) {
                    val ex = s * 0.6f; val ey = 0.34f
                    drawPath(trazoAnioContemporanea(e.centro, r) { m(ex + s * 0.14f, ey - 0.34f); l(ex + s * 0.14f, ey + 0.08f); l(ex - s * 0.2f, ey + 0.02f); z() }, col)
                }
            }
            else -> {}
        }
    }
}

/**
 * URSS: el trofeo se apoya en un plinto constructivista de granito rojo pulido, con su moteado y su reflejo, sobre el
 * escalón de granito negro y bajo la cornisa gris, con las franjas doradas de los pilones del metro y, al frente, la
 * estrella facetada del metal del nivel. En el hall, el pilón de mármol de la estación, acanalado, con su remate rojo y el
 * medallón del metal con la estrella. La placa es la tablilla de esmalte rojo de las calles, con su cara crema, el aro del
 * metal y los dos tornillos.
 */
object LogrosUrss : DisenoLogros {
    private val GRANITO_ROJO = Color(0xFF7A2A26)
    private val GRANITO_NEGRO = Color(0xFF2A2624)
    private val GRANITO_GRIS = Color(0xFF8A8580)
    private val MARMOL = Color(0xFFD8D0C0)
    private val MARMOL_SOMBRA = Color(0xFFB4AA96)
    private val ROJO = Color(0xFFB0241E)
    private val ROJO_OSC = Color(0xFF7E1914)
    private val ORO = Color(0xFFD9A93E)
    private val CREMA = Color(0xFFEFE4C9)
    private val TINTA = Color(0xFF1E1C1A)

    // salón: colores
    private val MARMOL_VETA = Color(0xFF8A7A60)
    private val BRONCE = Color(0xFFA67A3A)
    private val BRONCE_LUZ = Color(0xFFD9B06A)
    private val LUZ = Color(0xFFFFE6A3)
    private val VAGON = Color(0xFF3F6E8C)
    private val RIEL = Color(0xFF8A8D91)
    private val MADERA = Color(0xFF8A5A34)

    /** El color en modo oscuro: más hundido, como en su biblioteca. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0D0C0B), 0.34f) else c

    /** El moteado del granito: puntos claros y oscuros repartidos en [r]. */
    private fun DrawScope.moteado(r: Rect, base: Color, semilla: Int, n: Int, punto: Float) {
        for (k in 0 until n) {
            val claro = azar(semilla, k + 500) > 0.5f
            drawCircle(mezcla(base, if (claro) Color.White else Color.Black, 0.3f).copy(alpha = 0.7f), punto * (0.6f + azar(semilla, k + 600)),
                Offset(r.left + r.width * azar(semilla, k), r.top + r.height * azar(semilla, k + 300)))
        }
    }

    // ---------------------------------------------------------------- pedestales y placa

    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val fino = h >= 90 * u
        val hl = maxOf(0.6f * u, h * 0.004f)
        fun c(x: Color) = t(x, oscuro)
        val corn = Rect(w * 0.12f, h * 0.775f, w * 0.88f, h * 0.8f)
        val cuerpo = Rect(w * 0.16f, h * 0.8f, w * 0.84f, h * 0.924f)
        val escalon = Rect(w * 0.08f, h * 0.924f, w * 0.92f, h * 0.954f)
        if (!e.hecho) { silueta(oscuro) { addRect(corn); addRect(cuerpo); addRect(escalon) }; return }
        sombraPie(w / 2, escalon.width, h * 0.958f, h * 0.022f, if (oscuro) 0.5f else 0.25f)
        // El escalón de granito negro.
        drawRect(c(GRANITO_NEGRO), escalon.topLeft, escalon.size)
        drawRect(c(mezcla(GRANITO_NEGRO, Color.White, 0.18f)), escalon.topLeft, Size(escalon.width, escalon.height * 0.25f))
        if (fino) moteado(escalon, c(GRANITO_NEGRO), e.semilla + 3, 30, hl * 0.5f)
        // El cuerpo de granito rojo pulido, con su moteado, su sombra y el reflejo en diagonal.
        drawRect(c(GRANITO_ROJO), cuerpo.topLeft, cuerpo.size)
        if (fino) moteado(cuerpo, c(GRANITO_ROJO), e.semilla, 70, hl * 0.5f)
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(cuerpo.right - cuerpo.width * 0.1f, cuerpo.top), Size(cuerpo.width * 0.1f, cuerpo.height))
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
            drawPath(Path().apply {
                moveTo(cuerpo.left + cuerpo.width * 0.05f, cuerpo.bottom); lineTo(cuerpo.left + cuerpo.width * 0.17f, cuerpo.top)
                lineTo(cuerpo.left + cuerpo.width * 0.25f, cuerpo.top); lineTo(cuerpo.left + cuerpo.width * 0.13f, cuerpo.bottom); close()
            }, Color.White.copy(alpha = if (oscuro) 0.05f else 0.09f))
        }
        // Las franjas doradas de los pilones: dos arriba, una abajo y, a los lados, las verticales.
        val oro = c(ORO)
        drawRect(oro, Offset(cuerpo.left, cuerpo.top + cuerpo.height * 0.08f), Size(cuerpo.width, hl))
        if (fino) drawRect(oro, Offset(cuerpo.left, cuerpo.top + cuerpo.height * 0.08f + hl * 2.2f), Size(cuerpo.width, hl * 0.6f))
        drawRect(oro, Offset(cuerpo.left, cuerpo.bottom - cuerpo.height * 0.1f), Size(cuerpo.width, hl))
        if (fino) listOf(0.26f, 0.74f).forEach { f -> drawRect(oro, Offset(w * f - hl * 0.5f, cuerpo.top + cuerpo.height * 0.2f), Size(hl, cuerpo.height * 0.62f)) }
        // La estrella facetada del metal del nivel.
        estrellaFacetada(e, Offset(w / 2, cuerpo.center.y + cuerpo.height * 0.02f), cuerpo.height * 0.36f)
        // La cornisa de granito gris, con su canto de luz.
        drawRect(c(GRANITO_GRIS), corn.topLeft, corn.size)
        drawRect(c(mezcla(GRANITO_GRIS, Color.White, 0.3f)), corn.topLeft, Size(corn.width, corn.height * 0.3f))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(corn.left, corn.bottom - hl * 0.7f), Size(corn.width, hl * 0.7f))
    }

    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val m = tonos(e, oscuro)
        fun c(x: Color) = if (oscuro) mezcla(t(x, true), Color.Black, 0.2f) else x
        sombraPie(w / 2, w * 0.86f, h * 0.95f, h * 0.14f, if (oscuro) 0.45f else 0.2f)
        // El pilón de mármol, acanalado, con su luz y su sombra.
        val pilon = Rect(w * 0.1f, h * 0.15f, w * 0.9f, h * 0.86f)
        drawRect(c(MARMOL), pilon.topLeft, pilon.size)
        drawRect(c(MARMOL_SOMBRA), Offset(pilon.right - pilon.width * 0.12f, pilon.top), Size(pilon.width * 0.12f, pilon.height))
        val n = 10
        for (k in 1 until n) {
            val x = pilon.left + pilon.width * k / n
            if (abs(x - w / 2) < h * 0.38f) continue
            drawLine(c(MARMOL_SOMBRA), Offset(x, pilon.top), Offset(x, pilon.bottom), 0.8f * u)
            drawLine(c(Color.White).copy(alpha = 0.5f), Offset(x + 0.8f * u, pilon.top), Offset(x + 0.8f * u, pilon.bottom), 0.4f * u)
        }
        // El medallón del metal del nivel con la estrella roja.
        val cc = Offset(w / 2, h * 0.5f); val r = h * 0.33f
        drawCircle(m.metalOscuro, r, cc)
        drawCircle(m.base, r * 0.88f, cc - Offset(0.3f * u, 0.3f * u))
        drawArc(m.brillo, 190f, 80f, false, Offset(cc.x - r * 0.72f, cc.y - r * 0.72f), Size(r * 1.44f, r * 1.44f), style = Stroke(r * 0.12f))
        drawPath(estrella(cc, r * 0.62f, 5, 0.42f), c(ROJO))
        drawPath(Path().apply { val pts = estrella(cc, r * 0.62f, 5, 0.42f); addPath(pts) }, c(ROJO_OSC), style = Stroke(0.4f * u))
        // El remate de granito rojo con su filete dorado y la basa de granito negro.
        drawRect(c(GRANITO_ROJO), Offset(w * 0.06f, 0f), Size(w * 0.88f, h * 0.16f))
        drawRect(c(ORO), Offset(w * 0.06f, h * 0.16f - 0.8f * u), Size(w * 0.88f, 0.8f * u))
        drawRect(c(GRANITO_NEGRO), Offset(w * 0.06f, h * 0.86f), Size(w * 0.88f, h * 0.14f))
        drawRect(c(mezcla(GRANITO_NEGRO, Color.White, 0.2f)), Offset(w * 0.06f, h * 0.86f), Size(w * 0.88f, 0.6f * u))
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) CREMA else TINTA
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFF09A85) else ROJO

    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        val m = tonos(e, oscuro)
        val r0 = Rect(Offset.Zero, size)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.22f), Offset(0.5f * u, 1.6f * u), size, CornerRadius(2.5f * u))
        // El aro del metal del nivel, la banda de esmalte rojo y la cara crema.
        drawRoundRect(m.metalOscuro, Offset.Zero, size, CornerRadius(2.5f * u))
        filete(m, redondo(r0.deflate(0.9f * u), 2 * u), 1.6f * u)
        val banda = r0.deflate(1.8f * u)
        drawRoundRect(if (oscuro) ROJO_OSC else ROJO, banda.topLeft, banda.size, CornerRadius(1.5f * u))
        val cara = r0.deflate(4.6f * u)
        drawRect(if (oscuro) Color(0xFF1E1A19) else CREMA, cara.topLeft, cara.size)
        // Los dos tornillos en la banda roja.
        listOf(3.2f * u, w - 3.2f * u).forEach { x -> boton(m, Offset(x, h / 2), 1f * u) }
    }

    // ---------------------------------------------------------------- el salón del hall

    /**
     * Una estación de metro de los años treinta, un palacio bajo tierra: el mármol crema de los muros con sus vetas, y
     * detrás de cada trofeo, la arcada que abre hacia el andén, con su filete dorado y la estrella en la clave. Entre las
     * franjas, los pilones de mármol con su zócalo de granito rojo, su remate en escalones, las espigas de trigo en relieve y,
     * en cada piso, el aplique de bronce de dos brazos con sus globos (de noche, la estación vacía con las lámparas
     * encendidas). Arriba, la bóveda de casetones con sus plafones redondos, la banda roja en diagonal del friso y, en la
     * línea del medio, el reloj de la estación. Abajo, el andén de losas de granito en damero, la línea de seguridad, el
     * borde y las vías. A la izquierda, la banca de madera con sus patas de hierro; a la derecha, el frente del tren que
     * asoma del túnel con sus faros.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) = clipRect { salon(e, oscuro) }

    /** La escena del salón, recortada al lienzo por [escenaSalon]. */
    private fun DrawScope.salon(e: EstadoSalon, oscuro: Boolean) {
        val p = planta(e); val u = p.u; val w = p.w; val h = p.h
        fun c(x: Color, f: Float) = mezcla(e.fondo, t(x, oscuro), f)
        val techo = 40 * u; val anden = h - 34 * u; val via = h - 12 * u

        // El mármol crema de los muros, con sus vetas en diagonal.
        drawRect(c(MARMOL, if (oscuro) 0.22f else 0.45f), Offset(0f, techo), Size(w, anden - techo))
        for (k in 0 until 26) {
            val x = w * azar(e.semilla + 1, k); val y = techo + (anden - techo) * azar(e.semilla + 1, k + 40)
            drawPath(Path().apply { moveTo(x, y); quadraticTo(x + 14 * u, y + 10 * u, x + 30 * u * azar(e.semilla, k), y + 34 * u) }, c(MARMOL_VETA, if (oscuro) 0.16f else 0.2f), style = Stroke(0.8f * u))
        }
        // La arcada detrás de cada trofeo: el hueco hacia el andén, su filete de oro y la estrella en la clave.
        for (i in 0 until p.n) for (k in 0 until p.filas) {
            val an = minOf(p.paso - 40 * u, 128 * u)
            val r = Rect(p.cx(i) - an / 2, maxOf(p.y(k) - 4 * u, techo + 4 * u), p.cx(i) + an / 2, minOf(p.y(k) + p.alto - 16 * u, anden - 4 * u))
            if (r.height < 80 * u) continue
            val arco = arcoMedioPunto(Rect(r.left, r.top, r.right, r.bottom))
            drawPath(arco, c(MARMOL_SOMBRA, if (oscuro) 0.3f else 0.38f))
            val dentro = arcoMedioPunto(r.deflate(7 * u))
            drawPath(dentro, c(GRANITO_NEGRO, if (oscuro) 0.3f else 0.1f))
            drawPath(arco, c(ORO, if (oscuro) 0.4f else 0.5f), style = Stroke(1.2f * u))
            drawPath(dentro, c(ORO, if (oscuro) 0.25f else 0.3f), style = Stroke(0.7f * u))
            val clave = Offset(r.center.x, r.top + 3 * u)
            drawCircle(c(ORO, if (oscuro) 0.5f else 0.6f), 5.5f * u, clave)
            drawPath(estrella(clave, 4.2f * u, 5, 0.42f), c(ROJO, if (oscuro) 0.6f else 0.75f))
        }

        // Los pilones entre las franjas: mármol veteado, zócalo de granito rojo, remate en escalones, espigas y apliques.
        for (i in 0..p.n) {
            val x = p.lx(i); val an = 22 * u
            drawRect(c(MARMOL, if (oscuro) 0.4f else 0.8f), Offset(x - an / 2, techo), Size(an, anden - techo))
            drawRect(c(MARMOL_SOMBRA, if (oscuro) 0.4f else 0.7f), Offset(x + an / 2 - 4 * u, techo), Size(4 * u, anden - techo))
            run { var y = techo + 10 * u; var k = 0; while (y < anden && k < 40) { drawLine(c(MARMOL_VETA, 0.35f), Offset(x - an / 2, y), Offset(x + an / 2, y + (azar(e.semilla + i, k) - 0.3f) * 24 * u), 0.7f * u); y += maxOf(34 * u, 1f); k++ } }
            for (s in 1..3) drawRect(c(MARMOL_SOMBRA, if (oscuro) 0.45f else 0.8f), Offset(x - an / 2 - s * 3 * u, techo + (3 - s) * 4 * u), Size(an + s * 6 * u, 4 * u))
            drawRect(c(ROJO, if (oscuro) 0.5f else 0.7f), Offset(x - an / 2, techo + 16 * u), Size(an, 3 * u))
            drawRect(c(GRANITO_ROJO, if (oscuro) 0.6f else 0.7f), Offset(x - an / 2 - 3 * u, anden - 26 * u), Size(an + 6 * u, 26 * u))
            drawLine(c(ORO, 0.7f), Offset(x - an / 2 - 3 * u, anden - 26 * u), Offset(x + an / 2 + 3 * u, anden - 26 * u), 1.2f * u)
            for (j in 0 until 8) drawCircle(c(GRANITO_NEGRO, 0.35f), 0.7f * u, Offset(x - an / 2 + an * azar(e.semilla + 30, i * 8 + j), anden - 24 * u + 22 * u * azar(e.semilla + 31, i * 8 + j)))
            // Las espigas de trigo en relieve, bajo el remate.
            listOf(-1f, 1f).forEach { s ->
                val base = Offset(x, techo + 46 * u)
                drawPath(Path().apply { moveTo(base.x, base.y); quadraticTo(base.x + s * 2 * u, base.y - 12 * u, base.x + s * 6 * u, base.y - 22 * u) }, c(ORO, 0.6f), style = Stroke(0.8f * u))
                for (j in 0 until 4) drawOval(c(ORO, if (oscuro) 0.5f else 0.65f), Offset(base.x + s * (2 + j * 1.2f) * u - 1.2f * u, base.y - (10 + j * 3.4f) * u), Size(2.4f * u, 3.4f * u))
            }
            for (k in 0 until p.filas) {
                val y = p.y(k) + 50 * u
                if (y < techo + 50 * u || y > anden - 40 * u) continue
                // El aplique de bronce: el brazo doble y dos globos esmerilados.
                drawCircle(c(BRONCE, 0.8f), 3 * u, Offset(x, y + 6 * u))
                listOf(-1f, 1f).forEach { s ->
                    if ((i == 0 && s < 0) || (i == p.n && s > 0)) return@forEach
                    val g = Offset(x + s * 15 * u, y - 10 * u)
                    drawPath(Path().apply { moveTo(x, y + 6 * u); quadraticTo(x + s * 14 * u, y + 6 * u, g.x, g.y + 5 * u) }, c(BRONCE, 0.8f), style = Stroke(1.6f * u))
                    if (oscuro) halo(g, 34 * u, LUZ.copy(alpha = 0.035f), 6)
                    drawCircle(if (oscuro) mezcla(e.fondo, LUZ, 0.85f) else c(CREMA, 0.95f), 4.6f * u, g)
                    drawCircle(c(BRONCE, 0.6f), 4.6f * u, g, style = Stroke(0.7f * u))
                    drawRect(c(BRONCE_LUZ, 0.8f), Offset(g.x - 2.5f * u, g.y + 3.6f * u), Size(5 * u, 2 * u))
                }
            }
        }

        // Arriba: la bóveda de casetones con sus plafones, la banda roja en diagonal y el reloj de la estación.
        drawRect(c(MARMOL_SOMBRA, if (oscuro) 0.3f else 0.6f), Offset.Zero, Size(w, techo))
        run {
            var x = 0f; var k = 0
            while (x < w && k < 40) {
                drawRect(c(MARMOL, if (oscuro) 0.3f else 0.75f), Offset(x + 3 * u, 3 * u), Size(24 * u, 18 * u))
                drawRect(c(MARMOL_SOMBRA, 0.7f), Offset(x + 3 * u, 3 * u), Size(24 * u, 18 * u), style = Stroke(1 * u))
                val pl = Offset(x + 15 * u, 12 * u)
                if (oscuro) halo(pl, 12 * u, LUZ.copy(alpha = 0.05f), 3)
                drawCircle(if (oscuro) mezcla(e.fondo, LUZ, 0.7f) else c(CREMA, 0.95f), 3.6f * u, pl)
                drawCircle(c(BRONCE, 0.6f), 3.6f * u, pl, style = Stroke(0.7f * u))
                x += maxOf(30 * u, 1f); k++
            }
        }
        drawRect(c(ROJO, if (oscuro) 0.5f else 0.65f), Offset(0f, techo - 14 * u), Size(w, 6 * u))
        clipRect(0f, techo - 14 * u, w, techo - 8 * u) {
            var x = 0f; var k = 0
            while (x < w && k < 80) { drawLine(c(ROJO_OSC, 0.75f), Offset(x, techo - 8 * u), Offset(x + 6 * u, techo - 14 * u), 2 * u); x += maxOf(9 * u, 1f); k++ }
        }
        drawRect(c(ORO, if (oscuro) 0.5f else 0.65f), Offset(0f, techo - 8 * u), Size(w, 1.6f * u))
        drawRect(c(GRANITO_NEGRO, if (oscuro) 0.5f else 0.45f), Offset(0f, techo - 6 * u), Size(w, 6 * u))
        run {
            val rc = Offset(p.lx(p.n / 2).coerceIn(16 * u, w - 16 * u), techo + 8 * u)
            drawLine(c(BRONCE, 0.8f), Offset(rc.x, techo), Offset(rc.x, rc.y - 9 * u), 1.4f * u)
            drawCircle(c(BRONCE, 0.85f), 10 * u, rc)
            drawCircle(if (oscuro) c(CREMA, 0.7f) else c(CREMA, 0.95f), 8.4f * u, rc)
            for (j in 0 until 12) { val a = j * PI / 6; drawLine(c(TINTA, 0.8f), Offset(rc.x + (6.6f * u * cos(a)).toFloat(), rc.y + (6.6f * u * sin(a)).toFloat()), Offset(rc.x + (7.8f * u * cos(a)).toFloat(), rc.y + (7.8f * u * sin(a)).toFloat()), (if (j % 3 == 0) 1.2f else 0.6f) * u) }
            drawLine(c(TINTA, 0.9f), rc, rc + Offset(0f, -5.6f * u), 1 * u, StrokeCap.Round)
            drawLine(c(TINTA, 0.9f), rc, rc + Offset(3.6f * u, 1.6f * u), 1.3f * u, StrokeCap.Round)
        }

        // Abajo: el andén de losas de granito en damero, la línea de seguridad, el borde y las vías con sus durmientes.
        drawRect(c(GRANITO_GRIS, if (oscuro) 0.4f else 0.5f), Offset(0f, anden), Size(w, via - anden))
        run {
            val lado = 11 * u; var x = 0f; var k = 0
            while (x < w && k < 60) {
                if (k % 2 == 0) drawRect(c(GRANITO_ROJO, if (oscuro) 0.4f else 0.45f), Offset(x, anden), Size(lado, lado))
                else drawRect(c(GRANITO_NEGRO, if (oscuro) 0.4f else 0.4f), Offset(x, anden + lado), Size(lado, via - anden - lado - 4 * u))
                x += maxOf(lado, 1f); k++
            }
        }
        drawRect(c(ORO, if (oscuro) 0.6f else 0.75f), Offset(0f, via - 6 * u), Size(w, 2.4f * u))
        drawRect(c(MARMOL, if (oscuro) 0.35f else 0.6f), Offset(0f, via - 3.6f * u), Size(w, 3.6f * u))
        drawRect(c(GRANITO_NEGRO, if (oscuro) 0.7f else 0.55f), Offset(0f, via), Size(w, h - via))
        run { var x = 0f; var k = 0; while (x < w && k < 80) { drawRect(c(MADERA, if (oscuro) 0.35f else 0.4f), Offset(x, via + 4 * u), Size(4 * u, h - via - 4 * u)); x += maxOf(9 * u, 1f); k++ } }
        listOf(via + 5 * u, via + 10 * u).forEach { y -> drawLine(c(RIEL, 0.8f), Offset(0f, y), Offset(w, y), 1.4f * u) }

        // La banca de madera con sus patas de hierro, a la izquierda.
        run {
            val x0 = 4 * u; val y = anden + 2 * u
            drawRect(c(MADERA, if (oscuro) 0.55f else 0.7f), Offset(x0, y - 12 * u), Size(36 * u, 3 * u))
            drawRect(c(MADERA, if (oscuro) 0.55f else 0.7f), Offset(x0, y - 22 * u), Size(36 * u, 3 * u))
            drawRect(c(MADERA, if (oscuro) 0.55f else 0.7f), Offset(x0, y - 27 * u), Size(36 * u, 3 * u))
            listOf(x0 + 4 * u, x0 + 30 * u).forEach { lx ->
                drawLine(c(GRANITO_NEGRO, 0.8f), Offset(lx, y - 28 * u), Offset(lx, y + 6 * u), 1.6f * u)
                drawPath(Path().apply { moveTo(lx, y - 9 * u); quadraticTo(lx + 3 * u, y - 4 * u, lx + 1 * u, y + 6 * u) }, c(GRANITO_NEGRO, 0.8f), style = Stroke(1.2f * u))
            }
        }
        // El frente del tren que asoma del túnel, a la derecha: la cabina redonda, el parabrisas, los faros y la franja.
        run {
            val x0 = w - 30 * u; val y0 = anden - 52 * u
            drawRect(c(GRANITO_NEGRO, if (oscuro) 0.75f else 0.55f), Offset(w - 34 * u, y0 - 8 * u), Size(34 * u, via - y0 + 8 * u))
            val frente = Path().apply { moveTo(w, y0); lineTo(x0 + 8 * u, y0); quadraticTo(x0, y0, x0, y0 + 10 * u); lineTo(x0, via - 2 * u); lineTo(w, via - 2 * u); close() }
            drawPath(frente, c(VAGON, if (oscuro) 0.6f else 0.7f))
            drawRoundRect(if (oscuro) c(LUZ, 0.35f) else c(Color(0xFF2A3A44), 0.7f), Offset(x0 + 4 * u, y0 + 6 * u), Size(w - x0 - 4 * u, 14 * u), CornerRadius(3 * u))
            drawRect(c(CREMA, 0.75f), Offset(x0, y0 + 26 * u), Size(w - x0, 4 * u))
            drawRect(c(ROJO, 0.7f), Offset(x0, via - 14 * u), Size(w - x0, 3 * u))
            val faro = Offset(x0 + 8 * u, y0 + 38 * u)
            if (oscuro) halo(faro, 26 * u, LUZ.copy(alpha = 0.06f), 5)
            drawCircle(if (oscuro) mezcla(e.fondo, LUZ, 0.95f) else c(CREMA, 0.9f), 3 * u, faro)
            drawCircle(c(GRANITO_NEGRO, 0.8f), 3 * u, faro, style = Stroke(0.8f * u))
        }
    }

    // ---------------------------------------------------------------- las campañas

    /**
     * La locomotora del Transiberiano, de lado y hacia la derecha: el ténder con su carbón, la cabina con su ventana
     * encendida, la caldera verde con su frente redondo, los deflectores de humo con la estrella roja, la chimenea corta, el
     * faro, la franja blanca del bastidor, las ruedas rojas de llanta blanca con su biela y, sobre la cabina, la bandera de
     * fiesta del color de acento. El vapor se va hacia atrás. De noche, el faro echa su haz y su halo, y el casco lleva un
     * filo claro para leerse sobre el mapa oscuro.
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density; val p = e.punto
        sombraMarcadorContemporanea(p, 32 * u, e.salto, oscuro)
        val o = Offset(p.x, p.y - subidaContemporanea(e.salto))
        fun x(v: Float) = o.x + v * u
        fun y(v: Float) = o.y + v * u
        val verde = t(Color(0xFF2E5A44), oscuro); val verdeLuz = t(Color(0xFF4E8068), oscuro); val rojo = t(ROJO, oscuro); val crema = t(CREMA, oscuro)
        val negro = t(GRANITO_NEGRO, oscuro); val luz = if (oscuro) LUZ else t(LUZ, false)
        val filo = Color(0xFFEFE4C9).copy(alpha = 0.5f)
        // El haz del faro, de noche.
        if (oscuro) drawPath(trazoContemporanea(o, u, true, 15.4f, -14.6f, 30f, -17f, 30f, -5f, 15.4f, -12.4f), LUZ.copy(alpha = 0.12f))
        // El vapor.
        listOf(Triple(8.4f, -20.6f, 1.8f), Triple(5f, -23f, 2.4f), Triple(0.4f, -24.4f, 2.8f), Triple(-5f, -24f, 2.4f)).forEachIndexed { i, (px, py, rr) ->
            drawCircle(Color.White.copy(alpha = (if (oscuro) 0.55f else 0.85f) - i * 0.12f), rr * u, Offset(x(px), y(py)))
        }
        // El ténder con su carbón.
        val tender = trazoContemporanea(o, u, true, -15.6f, -12.4f, -7.6f, -12.4f, -7.6f, -4.4f, -15.6f, -4.4f)
        if (oscuro) drawPath(tender, filo, style = Stroke(1f * u))
        drawPath(tender, verde)
        drawPath(Path().apply { moveTo(x(-15.2f), y(-12.4f)); quadraticTo(x(-11.6f), y(-15f), x(-8f), y(-12.4f)); close() }, negro)
        // La cabina.
        val cabina = trazoContemporanea(o, u, true, -7.4f, -17.6f, -1.4f, -17.6f, -1.4f, -4.4f, -7.4f, -4.4f)
        if (oscuro) drawPath(cabina, filo, style = Stroke(1f * u))
        drawPath(cabina, verde)
        drawRect(negro, Offset(x(-8f), y(-18.6f)), Size(7.4f * u, 1.2f * u))
        if (oscuro) halo(Offset(x(-4.4f), y(-14f)), 3.6f * u, LUZ.copy(alpha = 0.14f), 3)
        drawRect(luz.copy(alpha = if (oscuro) 1f else 0.8f), Offset(x(-6.2f), y(-15.8f)), Size(3.4f * u, 3.2f * u))
        // La caldera con su frente redondo, la chimenea y los deflectores con la estrella.
        val caldera = Path().apply {
            moveTo(x(-1.6f), y(-15.6f)); lineTo(x(12f), y(-15.6f)); cubicTo(x(15.4f), y(-15.6f), x(15.4f), y(-6.6f), x(12f), y(-6.6f)); lineTo(x(-1.6f), y(-6.6f)); close()
        }
        if (oscuro) drawPath(caldera, filo, style = Stroke(1f * u))
        drawPath(caldera, verde)
        drawLine(verdeLuz, Offset(x(-1f), y(-14.4f)), Offset(x(12f), y(-14.4f)), 0.8f * u)
        drawRect(negro, Offset(x(8.4f), y(-17.8f)), Size(2.4f * u, 2.4f * u))
        drawArc(t(BRONCE, oscuro), 180f, 180f, true, Offset(x(2f), y(-17.4f)), Size(3.6f * u, 3.6f * u))
        drawRect(verdeLuz, Offset(x(6.6f), y(-14.8f)), Size(5.2f * u, 7.4f * u))
        drawRect(negro, Offset(x(6.6f), y(-14.8f)), Size(5.2f * u, 7.4f * u), style = Stroke(0.35f * u))
        drawPath(estrella(Offset(x(9.2f), y(-11.2f)), 2.2f * u, 5, 0.42f), if (oscuro) mezcla(ROJO, Color.White, 0.1f) else ROJO)
        drawPath(estrella(Offset(x(9.2f), y(-11.2f)), 2.2f * u, 5, 0.42f), t(ORO, oscuro), style = Stroke(0.3f * u))
        // El faro.
        if (oscuro) halo(Offset(x(14.6f), y(-13.6f)), 5 * u, LUZ.copy(alpha = 0.12f), 4)
        drawCircle(negro, 1.2f * u, Offset(x(14.2f), y(-13.6f)))
        drawCircle(luz, 0.75f * u, Offset(x(14.5f), y(-13.6f)))
        // El bastidor con su franja blanca y la viga roja.
        drawRect(negro, Offset(x(-15.6f), y(-6.8f)), Size(31.6f * u, 1.6f * u))
        drawLine(crema, Offset(x(-15.6f), y(-6f)), Offset(x(16f), y(-6f)), 0.5f * u)
        drawRect(rojo, Offset(x(15f), y(-7.4f)), Size(1.6f * u, 3 * u))
        // La bandera de fiesta del color de acento sobre la cabina, ondeando hacia atrás.
        drawLine(crema, Offset(x(-2.6f), y(-18.6f)), Offset(x(-2.6f), y(-24.6f)), 0.45f * u)
        drawPath(Path().apply { moveTo(x(-2.6f), y(-24.6f)); quadraticTo(x(-5f), y(-25.6f), x(-7.4f), y(-24.2f)); lineTo(x(-7.4f), y(-21.2f)); quadraticTo(x(-5f), y(-22.6f), x(-2.6f), y(-21.6f)); close() }, e.acento)
        // Las ruedas: las del ténder, las motrices rojas con su biela y la delantera.
        fun rueda(cx: Float, r: Float) {
            val c = Offset(x(cx), y(-0.4f - r))
            drawCircle(crema, r * u, c); drawCircle(rojo, r * 0.8f * u, c); drawCircle(negro, r * 0.22f * u, c)
        }
        rueda(-13.4f, 1.7f); rueda(-9.8f, 1.7f)
        rueda(-0.2f, 2.8f); rueda(5.6f, 2.8f); rueda(11.2f, 1.8f); rueda(14.4f, 1.6f)
        drawLine(t(RIEL, oscuro), Offset(x(-0.2f), y(-2.4f)), Offset(x(5.6f), y(-2.4f)), 0.8f * u, StrokeCap.Round)
        drawLine(t(RIEL, oscuro), Offset(x(5.6f), y(-2.4f)), Offset(x(9.4f), y(-4.4f)), 0.6f * u)
    }

    /**
     * La llegada a Vladivostok con salva de fuegos: la torre de ladrillo rojo con sus almenas, su reloj y su ventana, el
     * tejado en tienda verde y, en la punta, la estrella roja facetada con su filo de oro; al pie, dos espigas de trigo; en el
     * cielo, tres palmeras de fuegos artificiales (de oro, roja y del color de acento). De noche, la estrella se enciende
     * con su halo rojo y los fuegos brillan.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val b = pieVictoriaContemporanea(e.meta, 50 * u, 56 * u)
        fun x(v: Float) = b.x + v * u
        fun y(v: Float) = b.y + v * u
        val ladrillo = t(GRANITO_ROJO, oscuro); val ladrilloLuz = t(Color(0xFF9A3A30), oscuro); val marmol = t(MARMOL, oscuro)
        val tejado = t(Color(0xFF3E6B52), oscuro); val oro = if (oscuro) BRONCE_LUZ else ORO
        // Los fuegos de la salva.
        fuegoContemporanea(Offset(x(-15f), y(-42f)), 7.4f * u, 12, ORO, oscuro)
        fuegoContemporanea(Offset(x(15.6f), y(-46f)), 6.4f * u, 11, Color(0xFFE5484A), oscuro)
        fuegoContemporanea(Offset(x(16f), y(-27f)), 5 * u, 9, e.acento, oscuro)
        // Las espigas de trigo al pie.
        listOf(-1f, 1f).forEach { s ->
            val tallo = Path().apply { moveTo(x(s * 6f), y(0f)); quadraticTo(x(s * 12f), y(-5f), x(s * 13.4f), y(-15f)) }
            drawPath(tallo, oro, style = Stroke(0.6f * u))
            for (k in 0 until 5) {
                val f = 0.5f + k * 0.11f
                val c = Offset(x(s * (6f + 7.4f * f)), y(-15f * f * f - 0.6f))
                drawOval(oro, Offset(c.x - 0.7f * u - s * 0.8f * u, c.y - 1.2f * u), Size(1.4f * u, 2.4f * u))
                drawOval(oro, Offset(c.x - 0.7f * u + s * 0.8f * u, c.y - 1.6f * u), Size(1.4f * u, 2.4f * u))
            }
            drawOval(oro, Offset(x(s * 13.4f) - 0.7f * u, y(-17.4f)), Size(1.4f * u, 2.6f * u))
        }
        // La torre: el cuerpo de abajo con su puerta, el de arriba con su reloj y sus almenas.
        drawRect(ladrillo, Offset(x(-7f), y(-15f)), Size(14 * u, 15 * u))
        for (k in 1 until 6) drawLine(ladrilloLuz.copy(alpha = 0.6f), Offset(x(-7f), y(-k * 2.5f)), Offset(x(7f), y(-k * 2.5f)), 0.35f * u)
        drawRect(marmol, Offset(x(-7.6f), y(-15.6f)), Size(15.2f * u, 1.2f * u))
        drawPath(Path().apply { moveTo(x(-2.2f), y(0f)); lineTo(x(-2.2f), y(-4f)); quadraticTo(x(0f), y(-7f), x(2.2f), y(-4f)); lineTo(x(2.2f), y(0f)); close() }, t(GRANITO_NEGRO, oscuro))
        drawRect(ladrillo, Offset(x(-5f), y(-25f)), Size(10 * u, 9.4f * u))
        for (k in 0 until 5) drawRect(ladrillo, Offset(x(-5f + k * 2.2f), y(-26.6f)), Size(1.2f * u, 1.8f * u))
        val reloj = Offset(x(0f), y(-20.6f))
        if (oscuro) halo(reloj, 4 * u, LUZ.copy(alpha = 0.12f), 3)
        drawCircle(oro, 2.6f * u, reloj); drawCircle(if (oscuro) LUZ else CREMA, 2.1f * u, reloj)
        drawLine(t(TINTA, oscuro), reloj, reloj + Offset(0f, -1.6f * u), 0.4f * u); drawLine(t(TINTA, oscuro), reloj, reloj + Offset(1.2f * u, 0.3f * u), 0.4f * u)
        // El tejado en tienda y la aguja.
        drawPath(trazoContemporanea(b, u, true, -5.6f, -26.6f, 5.6f, -26.6f, 0.8f, -37f, -0.8f, -37f), tejado)
        drawPath(trazoContemporanea(b, u, true, 0f, -26.6f, 5.6f, -26.6f, 0.8f, -37f), mezcla(tejado, Color.Black, 0.25f))
        drawLine(oro, Offset(x(0f), y(-37f)), Offset(x(0f), y(-39.4f)), 0.7f * u)
        // La estrella roja facetada con su filo de oro.
        val st = Offset(x(0f), y(-44f))
        if (oscuro) halo(st, 12 * u, Color(0xFFFF4A3A).copy(alpha = 0.07f), 5)
        val claro = if (oscuro) Color(0xFFFF6A5A) else Color(0xFFE5484A)
        estrellaFacetada(EstadoPedestal(true, 1, t(ROJO_OSC, oscuro), ROJO, claro, 0), st, 6.4f * u)
        drawPath(estrella(st, 6.4f * u, 5, 0.42f), oro, style = Stroke(0.6f * u))
    }

    // ---------------------------------------------------------------- objetos de Tu año

    /**
     * URSS: la luna creciente con el Sputnik y sus cuatro antenas, el valenki (la bota de fieltro) con su chanclo, y la
     * pesa rusa con su asa y la estrella.
     */
    override fun DrawScope.objetoAnio(e: EstadoObjeto, oscuro: Boolean) {
        val r = e.radio; val x = e.centro.x; val y = e.centro.y; val u = density
        val col = e.color; val fondo = e.fondo
        fun p(a: Float, b: Float) = Offset(x + a * r, y + b * r)
        fun g(f: Float) = maxOf(r * f, 0.8f * u)
        when (e.forma) {
            "luna" -> {
                drawPath(crecienteContemporanea(p(-0.24f, 0.2f), r * 0.68f, Offset(r * 0.32f, -r * 0.26f), r * 0.56f), col)
                // El satélite: la bola y sus antenas hacia atrás, en diagonal.
                val sat = p(0.5f, -0.52f)
                listOf(25f, 40f, 55f, 70f).forEach { gr ->
                    val a = Math.toRadians(gr.toDouble()); drawLine(col, sat, sat + Offset((r * 0.55f * cos(a)).toFloat(), (r * 0.55f * sin(a)).toFloat()), g(0.06f))
                }
                drawCircle(col, maxOf(r * 0.18f, 1.2f * u), sat)
            }
            "huella" -> {
                val bota = trazoAnioContemporanea(e.centro, r) {
                    m(-0.5f, -0.9f); l(0.06f, -0.9f); l(0.06f, 0.15f); q(0.1f, 0.35f, 0.55f, 0.4f); q(0.95f, 0.46f, 0.9f, 0.74f)
                    q(0.86f, 0.92f, 0.6f, 0.92f); l(-0.4f, 0.92f); q(-0.58f, 0.92f, -0.56f, 0.7f); z()
                }
                drawPath(bota, col)
                // El chanclo de goma abajo y el doblez del fieltro arriba.
                drawLine(fondo, p(-0.6f, 0.62f), p(0.96f, 0.62f), g(0.07f))
                if (r > 6 * u) drawLine(fondo, p(-0.5f, -0.68f), p(0.06f, -0.68f), g(0.05f))
            }
            "pesa" -> {
                // El cuerpo de bola con la base plana y el asa ancha, que nace de los hombros.
                drawCircle(col, r * 0.66f, p(0f, 0.26f))
                drawRect(col, p(-0.32f, 0.74f), Size(r * 0.64f, r * 0.18f))
                val asa = maxOf(r * 0.17f, 1f * u)
                drawArc(col, 180f, 180f, false, p(-0.5f, -0.96f), Size(r * 1.0f, r * 0.8f), style = Stroke(asa))
                listOf(-1f, 1f).forEach { s -> drawLine(col, p(s * 0.5f, -0.57f), p(s * 0.46f, -0.1f), asa) }
                drawPath(estrella(p(0f, 0.3f), r * 0.3f, 5, 0.42f), fondo)
            }
            else -> {}
        }
    }
}
