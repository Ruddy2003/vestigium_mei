package io.github.ruddy2003.vestigium.ui.logros

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.TrazosNouveau
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Art nouveau
// =====================================================================

/**
 * Art nouveau: las medallas de la Exposición, en una sala del Palacio de las Exposiciones de 1900. Cada trofeo descansa en
 * una peana de caoba tallada como los muebles de Nancy (los costados que se curvan en línea de látigo, los pies que se
 * enroscan y, al frente, una hoja de nenúfar de bronce del metal del nivel), y en el hall, en la repisa de vidrio de su
 * vitrina; su placa es la etiqueta de la vitrina, de cartulina crema en su marco dorado. El salón es la sala de la Exposición
 * por dentro: las vitrinas de caoba y vidrio, una por columna de trofeos, con sus repisas; arriba, la nave de hierro y vidrio
 * por donde entra la luz, y abajo el mosaico del piso. De noche, la misma sala con la luz eléctrica de las vitrinas. En la
 * campaña corre un coche de madera del metro de 1900 y, al llegar a la última estación, espera la entrada de Guimard con sus
 * faroles encendidos.
 */
object LogrosNouveau : DisenoLogros {
    private val CAOBA = Color(0xFF6A3A26)
    private val CAOBA_OSCURA = Color(0xFF3E2016)
    private val CAOBA_LUZ = Color(0xFF9A5C3C)
    /** El arce claro de la marquetería. */
    private val ARCE = Color(0xFFD9BC8C)
    private val CREMA = Color(0xFFF3EAD3)
    private val NOCHE = Color(0xFF121519)
    private val HIERRO = TrazosNouveau.HIERRO
    private val LUZ = TrazosNouveau.LUZ
    private val HALO = TrazosNouveau.LUZ_HALO
    /** La sombra con color: el violeta de la tinta. */
    private val UMBRA = Color(0xFF2A2234)

    /** El color de noche: la misma madera bajo la luz eléctrica, hundida en el azul negro de la calle. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, NOCHE, 0.42f) else c

    /** El gris translúcido de lo que aún no se gana. */
    private fun silueta(oscuro: Boolean): Color = if (oscuro) Color(0xFFECE5D6).copy(alpha = 0.13f) else Color(0xFF2A2530).copy(alpha = 0.13f)

    private fun DrawScope.fr(x0: Float, y0: Float, x1: Float, y1: Float) = Rect(size.width * x0, size.height * y0, size.width * x1, size.height * y1)
    private fun DrawScope.fp(x: Float, y: Float) = Offset(size.width * x, size.height * y)

    /** La caoba en volumen: la luz entra por la izquierda y se va a la sombra a la derecha. */
    private fun caoba(c: Color, x0: Float, x1: Float): Brush = Brush.horizontalGradient(listOf(mezcla(c, Color.White, 0.18f), c, mezcla(c, UMBRA, 0.5f)), x0, x1)

    /** La veta de la caoba: líneas largas que ondulan, más oscuras, dentro de [r]. */
    private fun DrawScope.veta(r: Rect, c: Color, lw: Float, semilla: Int) {
        clipRect(r.left, r.top, r.right, r.bottom) {
            var y = r.top + lw; var k = 0
            while (y < r.bottom && k < 40) {
                val v = Path().apply {
                    moveTo(r.left, y); var x = r.left; var j = 0
                    while (x < r.right && j < 60) { val paso = r.width * 0.12f; quadraticTo(x + paso / 2, y + (azar(semilla, k * 60 + j) - 0.5f) * lw * 2.2f, x + paso, y); x += paso; j++ }
                }
                drawPath(v, mezcla(c, UMBRA, 0.35f).copy(alpha = 0.45f), style = Stroke(lw * 0.45f))
                y += lw * (2.2f + azar(semilla + 9, k) * 1.6f); k++
            }
        }
    }

    /**
     * La hoja de nenúfar de bronce, del metal del nivel: el disco de la hoja con su corte, las nervaduras que salen del centro
     * y la flor cerrada encima; el legendario, con su brillo.
     */
    private fun DrawScope.nenufar(c: Offset, r: Float, e: EstadoPedestal, fino: Boolean) {
        val u = density
        drawCircle(UMBRA.copy(alpha = 0.3f), r, c + Offset(r * 0.12f, r * 0.16f))
        val hoja = Path().apply {
            moveTo(c.x, c.y)
            arcTo(Rect(c, r), -70f, 320f, false)
            close()
        }
        drawPath(hoja, Brush.radialGradient(listOf(e.brillo, e.base, e.metalOscuro), Offset(c.x - r * 0.35f, c.y - r * 0.35f), r * 1.5f))
        drawPath(hoja, e.metalOscuro, style = Stroke(maxOf(r * 0.08f, 0.45f * u), join = StrokeJoin.Round))
        if (fino) for (k in 0 until 7) {
            val a = Math.toRadians(-40.0 + k * 42.0)
            drawLine(e.metalOscuro.copy(alpha = 0.6f), c, Offset(c.x + r * 0.85f * cos(a).toFloat(), c.y + r * 0.85f * sin(a).toFloat()), maxOf(r * 0.05f, 0.35f * u))
        }
        // La flor cerrada, de pie sobre la hoja.
        val f = Offset(c.x - r * 0.05f, c.y - r * 0.2f)
        listOf(-0.5f, 0f, 0.5f).forEach { a ->
            val pet = TrazosNouveau.petalo(f, Offset(f.x + sin(a) * r * 0.7f, f.y - cos(a) * r * 0.8f), r * 0.5f)
            drawPath(pet, Brush.linearGradient(listOf(e.brillo, e.base), Offset(f.x, f.y - r * 0.8f), f))
            drawPath(pet, e.metalOscuro.copy(alpha = 0.7f), style = Stroke(maxOf(r * 0.05f, 0.35f * u)))
        }
        if (e.nivel >= 4 && fino) drawLine(Color.White.copy(alpha = 0.85f), Offset(c.x - r * 0.62f, c.y + r * 0.1f), Offset(c.x - r * 0.2f, c.y - r * 0.55f), maxOf(r * 0.1f, 0.5f * u), StrokeCap.Round)
    }

    /** Un rizo de látigo tallado en la madera: la curva clara del lado de la luz y la oscura del lado de la sombra. */
    private fun DrawScope.rizoTallado(desde: Offset, hasta: Offset, rizo: Float, c: Color, lw: Float, horario: Boolean) {
        val pts = TrazosNouveau.puntosLatigo(desde, hasta, rizo, 0.22f, horario)
        val cinta = TrazosNouveau.cinta(pts) { t -> lw * (1.6f - 1.1f * t) }
        drawPath(Path().apply { addPath(cinta, Offset(lw * 0.5f, lw * 0.5f)) }, mezcla(c, UMBRA, 0.6f).copy(alpha = 0.8f))
        drawPath(cinta, mezcla(c, Color.White, 0.22f).copy(alpha = 0.7f))
    }

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * La peana de caoba de los muebles de Nancy: la tabla de arriba con sus cantos redondos y el filo claro, el cuerpo de
     * costados que se curvan hacia dentro como un tallo, con la marquetería de arce y los dos rizos tallados, la hoja de nenúfar
     * de bronce del metal del nivel al frente, y los pies que se abren y se enroscan; de noche, la misma madera bajo la
     * luz eléctrica de la vitrina, que le cae de arriba.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u
        val lw = maxOf(h * 0.0035f, 0.45f * u)
        val tabla = Path().apply { addRoundRect(RoundRect(fr(0.07f, 0.765f, 0.93f, 0.792f), CornerRadius(h * 0.014f))) }
        val cuerpo = Path().apply {
            moveTo(w * 0.15f, h * 0.792f)
            cubicTo(w * 0.22f, h * 0.83f, w * 0.2f, h * 0.89f, w * 0.12f, h * 0.935f)
            lineTo(w * 0.88f, h * 0.935f)
            cubicTo(w * 0.8f, h * 0.89f, w * 0.78f, h * 0.83f, w * 0.85f, h * 0.792f); close()
        }
        val zocalo = Path().apply {
            moveTo(w * 0.08f, h * 0.962f)
            quadraticTo(w * 0.06f, h * 0.935f, w * 0.12f, h * 0.932f)
            lineTo(w * 0.88f, h * 0.932f)
            quadraticTo(w * 0.94f, h * 0.935f, w * 0.92f, h * 0.962f); close()
        }
        if (!e.hecho) { drawPath(Path().apply { addPath(tabla); addPath(cuerpo); addPath(zocalo) }, silueta(oscuro)); return }
        // La sombra en el piso.
        drawOval(UMBRA.copy(alpha = if (oscuro) 0.45f else 0.2f), fp(0.05f, 0.948f), Size(w * 0.92f, h * 0.026f))
        val c = t(CAOBA, oscuro)
        drawPath(zocalo, caoba(t(CAOBA_OSCURA, oscuro), w * 0.08f, w * 0.92f))
        drawPath(cuerpo, caoba(c, w * 0.12f, w * 0.88f))
        if (fino) {
            clipPath(cuerpo) { veta(fr(0.12f, 0.795f, 0.88f, 0.935f), c, lw, e.semilla) }
            // La marquetería de arce: un filete que sigue la curva del costado y se cierra abajo en un arco.
            val filete = Path().apply {
                moveTo(w * 0.22f, h * 0.805f)
                cubicTo(w * 0.27f, h * 0.84f, w * 0.26f, h * 0.885f, w * 0.22f, h * 0.918f)
                quadraticTo(w * 0.5f, h * 0.9f, w * 0.78f, h * 0.918f)
                cubicTo(w * 0.74f, h * 0.885f, w * 0.73f, h * 0.84f, w * 0.78f, h * 0.805f)
            }
            drawPath(filete, t(ARCE, oscuro).copy(alpha = 0.75f), style = Stroke(lw * 0.7f, cap = StrokeCap.Round))
            // Los dos rizos tallados, uno a cada lado de la hoja.
            rizoTallado(fp(0.42f, 0.9f), fp(0.3f, 0.82f), h * 0.018f, c, lw, true)
            rizoTallado(fp(0.58f, 0.9f), fp(0.7f, 0.82f), h * 0.018f, c, lw, false)
        }
        // La sombra que la tabla echa sobre el cuerpo.
        drawRect(Brush.verticalGradient(listOf(UMBRA.copy(alpha = 0.35f), Color.Transparent), h * 0.792f, h * 0.815f), fp(0.12f, 0.792f), Size(w * 0.76f, h * 0.023f))
        nenufar(fp(0.5f, 0.86f), h * 0.033f, e, fino)
        // La tabla: el canto redondo, más claro, con el filo de luz arriba.
        drawPath(tabla, Brush.verticalGradient(listOf(mezcla(c, CAOBA_LUZ, if (oscuro) 0.3f else 0.7f), c, mezcla(c, UMBRA, 0.3f)), h * 0.765f, h * 0.792f))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.18f else 0.42f), fp(0.09f, 0.768f), fp(0.91f, 0.768f), lw * 0.8f, StrokeCap.Round)
        if (fino) drawLine(t(ARCE, oscuro).copy(alpha = 0.6f), fp(0.1f, 0.785f), fp(0.9f, 0.785f), lw * 0.4f)
        // Los pies que se enroscan, a cada lado del zócalo.
        listOf(-1f, 1f).forEach { s ->
            val x = w * (0.5f + s * 0.4f)
            drawCircle(caoba(t(CAOBA_OSCURA, oscuro), x - h * 0.02f, x + h * 0.02f), h * 0.014f, Offset(x, h * 0.95f))
            if (fino) drawArc(mezcla(c, Color.White, 0.25f).copy(alpha = 0.6f), if (s < 0) 120f else -60f, 200f, false, Offset(x - h * 0.009f, h * 0.941f), Size(h * 0.018f, h * 0.018f), style = Stroke(lw * 0.5f))
        }
        // De noche, la luz de la bombilla de la vitrina, que cae desde arriba sobre la tabla.
        if (oscuro) drawRect(Brush.verticalGradient(listOf(HALO.copy(alpha = 0.22f), Color.Transparent), h * 0.765f, h * 0.84f), fp(0.07f, 0.765f), Size(w * 0.86f, h * 0.075f))
    }

    /** La repisa de la vitrina en el hall: el canto de vidrio verdoso con su brillo, sobre el friso de caoba con la hoja de nenúfar. */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val lw = maxOf(h * 0.03f, 0.5f * u)
        val vidrio = fr(0f, 0f, 1f, 0.22f)
        val friso = Path().apply {
            moveTo(w * 0.03f, h * 0.22f); lineTo(w * 0.97f, h * 0.22f)
            cubicTo(w * 0.95f, h * 0.6f, w * 0.9f, h * 0.85f, w * 0.82f, h)
            lineTo(w * 0.18f, h)
            cubicTo(w * 0.1f, h * 0.85f, w * 0.05f, h * 0.6f, w * 0.03f, h * 0.22f); close()
        }
        if (!e.hecho) { drawPath(Path().apply { addRect(vidrio); addPath(friso) }, silueta(oscuro)); return }
        val c = t(CAOBA, oscuro)
        drawPath(friso, caoba(c, 0f, w))
        clipPath(friso) { veta(fr(0f, 0.22f, 1f, 1f), c, lw * 0.5f, e.semilla) }
        rizoTallado(Offset(w * 0.4f, h * 0.7f), Offset(w * 0.15f, h * 0.4f), h * 0.14f, c, lw * 0.5f, true)
        rizoTallado(Offset(w * 0.6f, h * 0.7f), Offset(w * 0.85f, h * 0.4f), h * 0.14f, c, lw * 0.5f, false)
        nenufar(Offset(w / 2, h * 0.58f), h * 0.28f, e, true)
        drawRect(Brush.verticalGradient(listOf(UMBRA.copy(alpha = 0.35f), Color.Transparent), h * 0.22f, h * 0.42f), Offset(0f, h * 0.22f), Size(w, h * 0.2f))
        // El canto del vidrio: verde por dentro, con el filo de luz.
        drawRect(Brush.verticalGradient(listOf(Color(0xFFE4F0E6), Color(0xFF9FC3AE), Color(0xFF5F8A72)), vidrio.top, vidrio.bottom), vidrio.topLeft, vidrio.size)
        drawLine(Color.White.copy(alpha = 0.7f), Offset(0f, lw * 0.4f), Offset(w, lw * 0.4f), lw * 0.6f)
        if (oscuro) drawRect(Brush.verticalGradient(listOf(HALO.copy(alpha = 0.3f), Color.Transparent), 0f, h * 0.5f), Offset.Zero, Size(w, h * 0.5f))
    }

    override val margenPlaca: Dp = 10.dp
    override fun tintaPlaca(oscuro: Boolean): Color = Color(0xFF2A2530)
    override fun rotuloPlaca(oscuro: Boolean): Color = Color(0xFF6D4C82)

    /**
     * La etiqueta de la vitrina: la cartulina crema en su marco dorado del metal del nivel, con dos esquinas muy redondas y dos
     * casi rectas (como el marco del tema), el filete de dentro y un rizo de látigo en cada esquina redonda.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val m = minOf(2.5f * u, h * 0.08f)
        val g = minOf(h * 0.42f, 14f * u); val ch = 1.5f * u
        fun hoja(d: Float) = Path().apply { addRoundRect(RoundRect(Rect(m + d, m + d, w - m - d, h - m - d), CornerRadius(maxOf(g - d, 1f)), CornerRadius(ch), CornerRadius(maxOf(g - d, 1f)), CornerRadius(ch))) }
        val marco = hoja(0f)
        if (!e.hecho) { drawPath(marco, silueta(oscuro)); return }
        drawPath(Path().apply { addPath(marco, Offset(0.8f * u, 1.2f * u)) }, UMBRA.copy(alpha = if (oscuro) 0.45f else 0.22f))
        drawPath(marco, Brush.linearGradient(listOf(e.brillo, e.base, e.metalOscuro, e.base), Offset(0f, 0f), Offset(w, h)))
        val carton = hoja(1.8f * u)
        drawPath(carton, Brush.verticalGradient(listOf(mezcla(CREMA, Color.White, 0.3f), CREMA, mezcla(CREMA, ARCE, 0.35f)), 0f, h))
        drawPath(hoja(3.4f * u), e.base.copy(alpha = 0.7f), style = Stroke(0.6f * u))
        // El grano de la cartulina.
        clipPath(carton) { repeat(24) { k -> drawCircle(UMBRA.copy(alpha = 0.05f), (0.4f + azar(e.semilla, k)) * u, Offset(w * azar(e.semilla, 40 + k), h * azar(e.semilla, 80 + k))) } }
        // Los rizos de las dos esquinas redondas.
        listOf(Offset(m + 4f * u, m + 4f * u) to 1f, Offset(w - m - 4f * u, h - m - 4f * u) to -1f).forEach { (o, s) ->
            val pts = TrazosNouveau.puntosLatigo(Offset(o.x + s * minOf(w * 0.18f, 18f * u), o.y), Offset(o.x + s * 2.4f * u, o.y + s * 2.4f * u), 2.2f * u, 0.2f, s > 0)
            drawPath(TrazosNouveau.cinta(pts) { k -> u * (1.1f - 0.7f * k) }, e.metalOscuro.copy(alpha = 0.85f))
        }
    }

    // ---------------------------------------------------------------- el salón del hall

    /** La geometría de la sala: una vitrina por columna, el montante de caoba en el borde de cada una y el trofeo en medio. */
    private class GeoSala(val w: Float, val h: Float, val u: Float, columnas: Int) {
        val n = columnas.coerceIn(1, 6)
        val paso = w / n
        val montante = minOf(paso * 0.08f, 11f * u)
        val techo = minOf(40f * u, h * 0.1f)
        val piso = minOf(22f * u, h * 0.08f)
        val suelo = h - piso
        val modulo = maxOf(230f * u, 1f)
        fun borde(i: Int) = i * paso
    }

    /**
     * La sala de la Exposición: la pared de papel pintado con sus tallos, las vitrinas de caoba y vidrio entre los montantes
     * tallados (una por columna, con sus repisas de vidrio y el reflejo en el cristal), la nave de hierro y vidrio arriba, con
     * sus arcos que se curvan como tallos, y el mosaico del piso abajo. De día, la luz difusa que baja por el vidrio del techo;
     * de noche, la misma sala con el techo oscuro y la luz eléctrica de las bombillas de cada vitrina.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val g = GeoSala(size.width, size.height, density, e.columnas)
        clipRect(0f, 0f, size.width, size.height) { escenaSala(g, e, oscuro) }
    }

    private fun DrawScope.escenaSala(g: GeoSala, e: EstadoSalon, oscuro: Boolean) {
        val w = g.w; val h = g.h; val u = g.u
        fun m(c: Color, k: Float) = mezcla(e.fondo, c, k)
        // La pared: el papel pintado, salvia muy pálido, con tallos de lirio al tresbolillo.
        drawRect(m(TrazosNouveau.TALLO, if (oscuro) 0.06f else 0.1f), Offset.Zero, Size(w, h))
        var yy = g.techo + 30f * u; var fila = 0
        while (yy < g.suelo && fila < 60) {
            var xx = if (fila % 2 == 0) 26f * u else 66f * u; var k = 0
            while (xx < w && k < 40) {
                val pie = Offset(xx, yy + 34f * u)
                drawPath(Path().apply { moveTo(pie.x, pie.y); quadraticTo(pie.x + 6f * u, pie.y - 18f * u, pie.x - 1f * u, pie.y - 34f * u) }, m(TrazosNouveau.TALLO_SOMBRA, if (oscuro) 0.12f else 0.16f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
                drawPath(TrazosNouveau.hojaLanza(pie, Offset(pie.x - 9f * u, pie.y - 14f * u), 4f * u, -0.2f), m(TrazosNouveau.TALLO_SOMBRA, if (oscuro) 0.1f else 0.13f))
                drawCircle(m(TrazosNouveau.MALVA, if (oscuro) 0.12f else 0.16f), 2.6f * u, Offset(pie.x - 1f * u, pie.y - 35f * u))
                xx += 80f * u; k++
            }
            yy += 70f * u; fila++
        }
        // La luz: de día, la que baja por el techo de vidrio; de noche, la sala en penumbra.
        if (oscuro) drawRect(Brush.verticalGradient(listOf(NOCHE.copy(alpha = 0.35f), Color.Transparent), g.techo, g.techo + 160f * u), Offset(0f, g.techo), Size(w, 160f * u))
        else drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.3f), Color.Transparent), g.techo, g.techo + 200f * u), Offset(0f, g.techo), Size(w, 200f * u))
        // Las vitrinas: el cristal con su reflejo, la luz de la bombilla de noche y las repisas de vidrio.
        for (i in 0 until g.n) {
            val x0 = g.borde(i) + g.montante; val x1 = g.borde(i + 1) - g.montante
            if (x1 - x0 < 8 * u) continue
            drawRect(m(Color(0xFFDCEBE2), if (oscuro) 0.05f else 0.12f), Offset(x0, g.techo), Size(x1 - x0, g.suelo - g.techo))
            // El reflejo del vidrio: dos franjas en diagonal, de arriba abajo.
            clipRect(x0, g.techo, x1, g.suelo) {
                var y0 = g.techo - (x1 - x0); var k = 0
                while (y0 < g.suelo && k < 40) {
                    drawPath(Path().apply { moveTo(x0, y0 + (x1 - x0) * 0.9f); lineTo(x1, y0); lineTo(x1, y0 + 14f * u); lineTo(x0, y0 + (x1 - x0) * 0.9f + 14f * u); close() }, Color.White.copy(alpha = if (oscuro) 0.04f else 0.12f))
                    y0 += 340f * u; k++
                }
            }
            var y0 = g.techo + 8f * u; var k2 = 0
            while (y0 < g.suelo - 40 * u && k2 < 20) {
                val yb = minOf(y0 + g.modulo - 14f * u, g.suelo - 4 * u)
                // La bombilla de la vitrina, de noche, que alumbra la repisa de abajo.
                if (oscuro) {
                    val b = Offset((x0 + x1) / 2, y0 + 2f * u)
                    drawRect(Brush.verticalGradient(listOf(HALO.copy(alpha = 0.14f), LUZ.copy(alpha = 0.05f), Color.Transparent), y0, yb), Offset(x0, y0), Size(x1 - x0, yb - y0))
                    drawCircle(Brush.radialGradient(listOf(HALO.copy(alpha = 0.6f), Color.Transparent), b, 9f * u), 9f * u, b)
                    drawCircle(LUZ, 1.6f * u, b)
                }
                // La repisa: el canto de vidrio con su brillo y su sombra.
                drawRect(m(Color(0xFF8FB8A0), if (oscuro) 0.3f else 0.4f), Offset(x0, yb - 3 * u), Size(x1 - x0, 3 * u))
                drawLine(m(Color.White, if (oscuro) 0.25f else 0.5f), Offset(x0, yb - 3 * u), Offset(x1, yb - 3 * u), 0.7f * u)
                drawRect(Brush.verticalGradient(listOf(m(UMBRA, 0.14f), Color.Transparent), yb, yb + 6 * u), Offset(x0, yb), Size(x1 - x0, 6 * u))
                y0 += g.modulo; k2++
            }
        }
        // Los montantes de caoba entre vitrinas, con su moldura, la cabeza tallada en rizo y el bronce del medio.
        for (i in 0..g.n) {
            val cx = g.borde(i)
            val r = Rect(cx - g.montante, g.techo, cx + g.montante, g.suelo)
            drawRect(Brush.horizontalGradient(listOf(m(CAOBA_LUZ, if (oscuro) 0.4f else 0.55f), m(CAOBA, if (oscuro) 0.5f else 0.65f), m(CAOBA_OSCURA, if (oscuro) 0.6f else 0.75f)), r.left, r.right), r.topLeft, r.size)
            drawLine(m(ARCE, if (oscuro) 0.18f else 0.3f), Offset(cx - g.montante * 0.35f, g.techo + 16f * u), Offset(cx - g.montante * 0.35f, g.suelo - 10f * u), 0.6f * u)
            // El bronce: una hoja de nenúfar chica a la altura de cada repisa.
            var yc = g.techo + g.modulo * 0.5f; var kc = 0
            while (yc < g.suelo - 50 * u && kc < 20) {
                val px = cx.coerceIn(8f * u, w - 8f * u)
                drawCircle(m(TrazosNouveau.ORO, if (oscuro) 0.4f else 0.5f), 3.2f * u, Offset(px, yc))
                drawCircle(m(CAOBA_OSCURA, 0.4f), 3.2f * u, Offset(px, yc), style = Stroke(0.6f * u))
                yc += g.modulo; kc++
            }
        }
        // El remate de cada vitrina: el arco de caoba que va de montante a montante, con sus rizos en los arranques y un iris
        // tallado en la clave.
        for (i in 0 until g.n) {
            val x0 = g.borde(i) + g.montante; val x1 = g.borde(i + 1) - g.montante
            if (x1 - x0 < 20 * u) continue
            val xm = (x0 + x1) / 2; val ya = g.techo + 26f * u; val yc = g.techo + 6f * u
            val arco = Path().apply { moveTo(x0, ya); cubicTo(x0 + (x1 - x0) * 0.12f, yc, x1 - (x1 - x0) * 0.12f, yc, x1, ya) }
            drawPath(Path().apply { addPath(arco, Offset(0.8f * u, 1.2f * u)) }, m(UMBRA, 0.2f), style = Stroke(4.4f * u))
            drawPath(arco, m(CAOBA, if (oscuro) 0.5f else 0.65f), style = Stroke(4.4f * u))
            drawPath(arco, m(CAOBA_LUZ, if (oscuro) 0.35f else 0.5f), style = Stroke(1f * u))
            listOf(-1f, 1f).forEach { s ->
                val desde = Offset(if (s < 0) x0 else x1, ya + 14f * u)
                val pts = TrazosNouveau.puntosLatigo(desde, Offset(desde.x - s * 10f * u, ya + 2f * u), 3.6f * u, 0.25f, s > 0)
                drawPath(TrazosNouveau.cinta(pts) { k -> u * (2.6f - 1.8f * k) }, m(CAOBA, if (oscuro) 0.5f else 0.65f))
            }
            val cl = Offset(xm, yc + 3f * u)
            listOf(-0.6f, 0f, 0.6f).forEach { a -> drawPath(TrazosNouveau.petalo(cl, Offset(cl.x + sin(a) * 8f * u, cl.y - cos(a) * 9f * u), 6f * u), m(TrazosNouveau.ORO, if (oscuro) 0.45f else 0.6f)) }
            drawCircle(m(CAOBA_OSCURA, 0.6f), 1.6f * u, cl)
        }
        // La nave de hierro y vidrio: el vidrio del techo (de día la luz; de noche el azul de la calle), los arcos de hierro
        // que se curvan como tallos y se juntan, y las costillas de los paneles.
        val nave = Rect(0f, 0f, w, g.techo)
        if (oscuro) drawRect(Brush.verticalGradient(listOf(m(NOCHE, 0.85f), m(Color(0xFF1C2630), 0.7f)), 0f, g.techo), nave.topLeft, nave.size)
        else drawRect(Brush.verticalGradient(listOf(m(Color(0xFFFFFBEF), 0.85f), m(Color(0xFFE9EEDC), 0.6f)), 0f, g.techo), nave.topLeft, nave.size)
        val hierro = m(HIERRO, if (oscuro) 0.55f else 0.5f)
        var xr = 0f; var kr = 0
        while (xr <= w && kr < 80) { drawLine(hierro.copy(alpha = 0.6f), Offset(xr, 0f), Offset(xr, g.techo), 0.6f * u); xr += 14f * u; kr++ }
        drawLine(hierro, Offset(0f, g.techo * 0.45f), Offset(w, g.techo * 0.45f), 0.6f * u)
        for (i in 0..g.n) {
            val cx = g.borde(i)
            listOf(-1f, 1f).forEach { s ->
                drawPath(Path().apply { moveTo(cx, g.techo); cubicTo(cx, g.techo * 0.35f, cx + s * g.paso * 0.2f, 0f, cx + s * g.paso * 0.5f, 0f) }, hierro, style = Stroke(2f * u))
            }
        }
        drawRect(m(HIERRO, if (oscuro) 0.6f else 0.55f), Offset(0f, g.techo - 3f * u), Size(w, 4f * u))
        if (oscuro) {
            // La luna, que se ve por el vidrio del techo.
            val lu = Offset(w * 0.82f, g.techo * 0.45f)
            drawCircle(Brush.radialGradient(listOf(Color(0xFFE9E4D2).copy(alpha = 0.3f), Color.Transparent), lu, 16f * u), 16f * u, lu)
            drawCircle(m(Color(0xFFE9E4D2), 0.6f), 4.5f * u, lu)
        }
        // El piso: el mosaico, con una cenefa de ondas y teselas claras y oscuras.
        drawRect(m(Color(0xFFCFC2A8), if (oscuro) 0.25f else 0.4f), Offset(0f, g.suelo), Size(w, g.piso))
        drawLine(m(UMBRA, 0.3f), Offset(0f, g.suelo), Offset(w, g.suelo), 1f * u)
        val onda = Path().apply {
            var x = 0f; val y = g.suelo + g.piso * 0.45f; moveTo(x, y)
            while (x < w) { cubicTo(x + 5f * u, y - 4f * u, x + 10f * u, y - 4f * u, x + 12f * u, y); cubicTo(x + 14f * u, y + 4f * u, x + 19f * u, y + 4f * u, x + 24f * u, y); x += 24f * u }
        }
        drawPath(onda, m(HIERRO, if (oscuro) 0.4f else 0.45f), style = Stroke(2.4f * u))
        repeat(60) { i -> drawRect(m(if (i % 3 == 0) CAOBA else Color(0xFFF3EAD3), if (oscuro) 0.2f else 0.3f), Offset(azar(e.semilla, 900 + i) * w, g.suelo + 2f * u + azar(e.semilla, 960 + i) * (g.piso - 4f * u)), Size(1.6f * u, 1.6f * u)) }
    }

    // ---------------------------------------------------------------- las campañas

    /**
     * El coche del metro de 1900, todo de madera, de perfil sobre sus rieles: la caja barnizada con sus ventanillas, el techo
     * de linternón, las ruedas y el farol de adelante; cuando brinca (hoy falta cumplir), da un saltito sobre la vía. De noche,
     * con las ventanillas y el farol encendidos.
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density
        val s = e.salto.coerceIn(0f, 1f)
        val p = e.punto
        val f = 1f - 0.35f * s
        drawOval(UMBRA.copy(alpha = (if (oscuro) 0.5f else 0.22f) * f), Offset(p.x - 12 * u * f, p.y - 1.6f * u * f), Size(24 * u * f, 3.2f * u * f))
        // Los rieles bajo el coche.
        drawLine(if (oscuro) Color(0xFF9AA3A0) else Color(0xFF4A5450), Offset(p.x - 14 * u, p.y + 0.4f * u), Offset(p.x + 14 * u, p.y + 0.4f * u), 0.9f * u)
        val y0 = p.y - 2.5f * u - s * 4f * u
        fun q(x: Float, y: Float) = Offset(p.x + x * u, y0 + y * u)
        val madera = if (oscuro) Color(0xFF7A4A30) else Color(0xFF6A3A26)
        val caja = Path().apply { addRoundRect(RoundRect(Rect(q(-11f, -12f), q(11f, -2f)), CornerRadius(2.2f * u))) }
        drawPath(caja, Brush.verticalGradient(listOf(mezcla(madera, Color.White, 0.2f), madera, mezcla(madera, UMBRA, 0.4f)), y0 - 12 * u, y0 - 2 * u))
        // El techo de linternón.
        drawPath(Path().apply { moveTo(q(-12f, -12f).x, q(-12f, -12f).y); quadraticTo(q(0f, -15.5f).x, q(0f, -15.5f).y, q(12f, -12f).x, q(12f, -12f).y); close() }, TrazosNouveau.HIERRO)
        drawRect(TrazosNouveau.HIERRO_SOMBRA, q(-6f, -15.8f), Size(12 * u, 1.6f * u))
        // Las ventanillas, con su luz de noche.
        for (k in 0 until 4) {
            val v = Rect(q(-9.2f + k * 4.8f, -10.6f), q(-6f + k * 4.8f, -6.4f))
            drawRoundRect(if (oscuro) LUZ else Color(0xFFE7EFE6), v.topLeft, v.size, CornerRadius(0.8f * u))
            if (!oscuro) drawLine(Color.White.copy(alpha = 0.7f), Offset(v.left + 0.5f * u, v.bottom - 0.6f * u), Offset(v.right - 0.6f * u, v.top + 0.6f * u), 0.4f * u)
        }
        if (oscuro) drawRect(Brush.verticalGradient(listOf(HALO.copy(alpha = 0.25f), Color.Transparent), y0 - 6 * u, y0 + 4 * u), q(-11f, -6f), Size(22 * u, 9 * u))
        drawLine(TrazosNouveau.ORO, q(-10.5f, -5.2f), q(10.5f, -5.2f), 0.5f * u)
        drawPath(caja, TrazosNouveau.PLUMA.copy(alpha = 0.7f), style = Stroke(0.6f * u))
        // Las ruedas y el farol.
        listOf(-6.5f, 6.5f).forEach { x -> drawCircle(Color(0xFF2E2A2A), 2f * u, q(x, -1.4f)); drawCircle(Color(0xFF9AA3A0), 0.7f * u, q(x, -1.4f)) }
        val farol = q(11.6f, -7.5f)
        if (oscuro) drawCircle(Brush.radialGradient(listOf(HALO.copy(alpha = 0.6f), Color.Transparent), farol, 9 * u), 9 * u, farol)
        drawCircle(if (oscuro) HALO else TrazosNouveau.FAROL, 1.1f * u, farol)
    }

    /**
     * La llegada a la última estación: la entrada de metro de Guimard en chico, con sus dos farolas de fundición verde que se
     * doblan y sostienen el letrero, los dos faroles rojo anaranjado encendidos y, al pie, un ramo de iris. De noche, con el
     * resplandor de los faroles.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val ancho = 40f * u; val alto = 46f * u; val mg = 2f * u
        val x = if (size.width > ancho + 2 * mg) e.meta.x.coerceIn(ancho / 2 + mg, size.width - ancho / 2 - mg) else size.width / 2
        val y = if (size.height > alto + 2 * mg) (e.meta.y - 2f * u).coerceIn(alto + mg, size.height - mg) else size.height - mg
        drawOval(UMBRA.copy(alpha = if (oscuro) 0.45f else 0.18f), Offset(x - 16 * u, y - 1.6f * u), Size(32 * u, 3.2f * u))
        val hierro = if (oscuro) mezcla(HIERRO, Color.White, 0.18f) else HIERRO
        // Las dos farolas: tallos que suben, se doblan hacia dentro y siguen hasta su farol.
        listOf(-1f, 1f).forEach { s ->
            val pts = TrazosNouveau.bezier(Offset(x + s * 13 * u, y), Offset(x + s * 14 * u, y - 18 * u), Offset(x + s * 13 * u, y - 30 * u), Offset(x + s * 9 * u, y - 33 * u), 20) +
                TrazosNouveau.bezier(Offset(x + s * 9 * u, y - 33 * u), Offset(x + s * 8 * u, y - 37 * u), Offset(x + s * 12 * u, y - 41 * u), Offset(x + s * 10 * u, y - 44 * u), 10).drop(1)
            drawPath(TrazosNouveau.cinta(pts) { t -> u * (2.6f - 1.4f * t) }, hierro)
            val farol = Offset(x + s * 10 * u, y - 41.5f * u)
            drawCircle(Brush.radialGradient(listOf(TrazosNouveau.FAROL.copy(alpha = if (oscuro) 0.7f else 0.4f), Color.Transparent), farol, 9 * u), 9 * u, farol)
            drawOval(Brush.radialGradient(listOf(Color(0xFFFFE2A8), TrazosNouveau.FAROL, Color(0xFFC0582E)), Offset(farol.x - 0.8f * u, farol.y - 0.6f * u), 3.4f * u), Offset(farol.x - 2.2f * u, farol.y - 2.8f * u), Size(4.4f * u, 5.6f * u))
        }
        // El letrero entre las dos.
        drawRoundRect(TrazosNouveau.ORO, Offset(x - 10 * u, y - 35 * u), Size(20 * u, 4.6f * u), CornerRadius(1.6f * u))
        drawRoundRect(Color(0xFFE9D08A), Offset(x - 9 * u, y - 34.3f * u), Size(18 * u, 3.2f * u), CornerRadius(1.2f * u))
        for (k in 0 until 9) drawLine(TrazosNouveau.HIERRO_SOMBRA, Offset(x - 7.5f * u + k * 1.9f * u, y - 33.6f * u), Offset(x - 7.5f * u + k * 1.9f * u, y - 31.8f * u), 0.6f * u)
        // La barandilla baja y la boca de la escalera.
        drawRect(Brush.verticalGradient(listOf(mezcla(UMBRA, LUZ, 0.35f), UMBRA), y - 9 * u, y), Offset(x - 11 * u, y - 9 * u), Size(22 * u, 9 * u))
        drawLine(hierro, Offset(x - 16 * u, y - 9 * u), Offset(x + 16 * u, y - 9 * u), 1.2f * u, StrokeCap.Round)
        // El ramo de iris al pie, a la izquierda.
        with(TrazosNouveau) { iris(Offset(x - 17 * u, y), 18f * u) }
    }

    // ---------------------------------------------------------------- objetos de Tu año

    /**
     * Los objetos de Tu año de Art nouveau: la luna, la creciente sobre los tejados con una línea de látigo de nube; la huella,
     * la suela de un botín de 1900 con su tacón aparte; la pesa, la halterofilia de los gimnasios de entonces: la pesa de dos
     * bolas con el mango torneado.
     */
    override fun DrawScope.objetoAnio(e: EstadoObjeto, oscuro: Boolean) {
        val r = e.radio; val c = e.centro; val u = density
        val g = maxOf(r * 0.1f, 0.8f * u)
        when (e.forma) {
            "luna" -> {
                val luna = Path().apply { addOval(Rect(Offset(c.x - r * 0.05f, c.y - r * 0.15f), r * 0.75f)) }
                clipPath(Path().apply { addOval(Rect(Offset(c.x + r * 0.32f, c.y - r * 0.38f), r * 0.66f)) }, androidx.compose.ui.graphics.ClipOp.Difference) { drawPath(luna, e.color) }
                if (r >= 6f * u) {
                    val pts = TrazosNouveau.puntosLatigo(Offset(c.x - r * 0.95f, c.y + r * 0.8f), Offset(c.x + r * 0.55f, c.y + r * 0.6f), r * 0.2f, -0.18f, false)
                    drawPath(TrazosNouveau.cinta(pts) { k -> g * (1.3f - 0.8f * k) }, e.color)
                }
            }
            "huella" -> {
                // La suela del botín: la planta ancha con la cintura que se estrecha y, aparte, el tacón.
                val planta = Path().apply {
                    moveTo(c.x - r * 0.05f, c.y - r * 0.98f)
                    cubicTo(c.x + r * 0.45f, c.y - r * 0.98f, c.x + r * 0.52f, c.y - r * 0.4f, c.x + r * 0.3f, c.y - r * 0.05f)
                    cubicTo(c.x + r * 0.18f, c.y + r * 0.18f, c.x + r * 0.2f, c.y + r * 0.3f, c.x + r * 0.15f, c.y + r * 0.34f)
                    lineTo(c.x - r * 0.18f, c.y + r * 0.34f)
                    cubicTo(c.x - r * 0.24f, c.y + r * 0.2f, c.x - r * 0.42f, c.y - r * 0.05f, c.x - r * 0.42f, c.y - r * 0.4f)
                    cubicTo(c.x - r * 0.42f, c.y - r * 0.8f, c.x - r * 0.3f, c.y - r * 0.98f, c.x - r * 0.05f, c.y - r * 0.98f); close()
                }
                drawPath(planta, e.color)
                drawRoundRect(e.color, Offset(c.x - r * 0.22f, c.y + r * 0.5f), Size(r * 0.4f, r * 0.45f), CornerRadius(r * 0.12f))
                if (r >= 6f * u) drawLine(e.fondo, Offset(c.x - r * 0.12f, c.y - r * 0.7f), Offset(c.x + r * 0.15f, c.y - r * 0.7f), g * 0.7f, StrokeCap.Round)
            }
            "pesa" -> {
                // La pesa de dos bolas del gimnasio, con el mango torneado.
                drawLine(e.color, Offset(c.x - r * 0.5f, c.y), Offset(c.x + r * 0.5f, c.y), r * 0.2f, StrokeCap.Round)
                listOf(-1f, 1f).forEach { s -> drawCircle(e.color, r * 0.38f, Offset(c.x + s * r * 0.58f, c.y)) }
                if (r >= 6f * u) {
                    listOf(-0.18f, 0f, 0.18f).forEach { dx -> drawLine(e.fondo, Offset(c.x + dx * r, c.y - r * 0.1f), Offset(c.x + dx * r, c.y + r * 0.1f), g * 0.6f) }
                    listOf(-1f, 1f).forEach { s -> drawCircle(e.fondo.copy(alpha = 0.6f), r * 0.1f, Offset(c.x + s * r * 0.48f, c.y - r * 0.13f)) }
                }
            }
            else -> {}
        }
    }
}
