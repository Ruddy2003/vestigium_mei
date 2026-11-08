package io.github.ruddy2003.vestigium.ui.logros

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.engrane
import io.github.ruddy2003.vestigium.ui.ideario.estrella
import io.github.ruddy2003.vestigium.ui.ideario.manometro
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

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
}
