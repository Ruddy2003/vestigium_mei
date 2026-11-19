package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.ADOBE
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.ALAMO
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.ALAMO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.ANIL
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.ARCILLA
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.BARRO
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.BERMELLON
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.BRONCE
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.CAL
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.CUERO
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.FIELTRO
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.LLAMA
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.PAJA
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.SOL
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.TIERRA
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.TINTA
import io.github.ruddy2003.vestigium.ui.biblioteca.SedaTinta.VIDRIO
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.cos
import kotlin.math.sin

// Las piezas del almacén de la Ruta de la Seda (MuebleEstanteSeda): lo que hay arriba, al pie y en los huecos libres.

/** La sombra de lo que pisa: un óvalo corrido hacia donde cae la luz. */
internal fun DrawScope.contactoSeda(centro: Offset, ancho: Float, alto: Float, a: AireSeda, fuerza: Float = 0.34f) =
    drawOval(a.sombra(if (a.oscuro) fuerza * 1.3f else fuerza), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))

/** Une [q] con curvas suaves (Catmull-Rom pasado a Bézier). */
internal fun suaveSeda(p: Path, q: List<Offset>) {
    for (i in 0 until q.size - 1) {
        val a = q[maxOf(i - 1, 0)]; val b = q[i]; val c = q[i + 1]; val d = q[minOf(i + 2, q.size - 1)]
        val c1 = b + (c - a) / 6f; val c2 = c - (d - b) / 6f
        p.cubicTo(c1.x, c1.y, c2.x, c2.y, c.x, c.y)
    }
}

/** El contorno de algo torneado: [p] son pares (medio ancho, altura) de arriba abajo, en unidades de [s] desde [cx] y [y0]. */
internal fun perfilSeda(cx: Float, y0: Float, s: Float, p: FloatArray): Path {
    val der = List(p.size / 2) { Offset(cx + p[2 * it] * s, y0 + p[2 * it + 1] * s) }
    val izq = der.reversed().map { Offset(2 * cx - it.x, it.y) }
    return Path().apply { moveTo(der[0].x, der[0].y); suaveSeda(this, der); lineTo(izq[0].x, izq[0].y); suaveSeda(this, izq); close() }
}

/**
 * El volumen de gouache sobre algo redondo ya pintado: la media luna de sombra del lado contrario a la luz (en dos tonos) y
 * el claro del otro. De día la luz viene de la izquierda; de noche, de donde esté la lámpara ([haciaX]: 1 a la derecha).
 */
internal fun DrawScope.redondoSeda(forma: Path, a: AireSeda, cerca: Float, haciaX: Float, fuerza: Float = 1f, claro: Float = 1f) {
    val r = forma.getBounds(); val an = r.width; val hh = r.height
    val sl = if (a.oscuro) haciaX else -1f
    drawPath(bordeSeda(forma, sl * an * 0.26f, -hh * 0.02f), a.sombra(fuerza * 0.2f))
    drawPath(bordeSeda(forma, sl * an * 0.11f, -hh * 0.01f), a.sombra(fuerza * 0.2f))
    clipPath(forma) {
        drawOval(if (a.oscuro) FILO_MUEBLE.copy(alpha = 0.26f * cerca * claro) else a.claro(0.2f * claro),
            Offset(if (sl < 0) r.left + an * 0.1f else r.right - an * 0.36f, r.top + hh * 0.06f), Size(an * 0.26f, hh * 0.72f))
    }
    if (a.oscuro && cerca > 0.03f) drawPath(bordeSeda(forma, -sl * an * 0.04f, hh * 0.03f), FILO_MUEBLE.copy(alpha = (0.05f + 0.6f * cerca).coerceAtMost(0.65f)))
}

// ---------------------------------------------------------------- al pie

/** El suelo de tierra apisonada, con la paja que se cae de los fardos y unos guijarros. */
internal fun DrawScope.sueloSeda(a: AireSeda, suelo: Float, cerca: (Offset) -> Float) {
    val u = density; val w = size.width; val h = size.height
    if (h <= suelo) return
    drawRect(a.luz(TIERRA, 0.04f), Offset(0f, suelo), Size(w, h - suelo))
    val tierra = Rect(0f, suelo, w, h)
    // Lo apisonado: manchas más hondas y más claras, tendidas (el suelo se ve de canto), y el barro de la humedad.
    pinceladas(tierra, a.luz(mezcla(TIERRA, SOMBRA_MUEBLE, 0.4f), 0.04f), 941, 30, u, 0.14f)
    pinceladas(tierra, a.luz(mezcla(TIERRA, LUZ_MUEBLE, 0.5f), 0.04f), 942, 22, u, 0.14f)
    pinceladas(tierra, a.luz(mezcla(TIERRA, BARRO, 0.5f), 0.04f), 950, 16, u, 0.16f)
    drawOval(a.luz(mezcla(TIERRA, BARRO, 0.6f), 0.04f).copy(alpha = 0.45f), Offset(w * 0.02f, suelo + (h - suelo) * 0.55f), Size(w * 0.3f, (h - suelo) * 0.3f))
    // Las grietas finas de la tierra seca.
    val grietas = Path()
    for (k in 0 until 7) {
        var q = Offset(w * azar(951, k), suelo + (h - suelo) * (0.3f + 0.6f * azar(952, k)))
        grietas.moveTo(q.x, q.y)
        for (j in 0 until 4) {
            q += Offset((6f + 8f * azar(953 + j, k)) * u, (azar(957 + j, k) - 0.5f) * 3f * u)
            grietas.lineTo(q.x, q.y)
        }
    }
    drawPath(grietas, a.sombra(0.3f), style = Stroke(0.6f * u))
    drawPath(corridoSeda(grietas, 0f, 0.8f * u), a.claro(0.25f), style = Stroke(0.5f * u))
    for (k in 0 until 26) {
        val p = Offset(w * azar(943, k), suelo + (h - suelo) * (0.15f + 0.8f * azar(944, k)))
        val ang = azar(945, k) * 3.14f
        val l = (4f + 7f * azar(946, k)) * u
        drawLine(a.luz(PAJA, maxOf(cerca(p), 0.05f)).copy(alpha = 0.7f), p, p + Offset(l * cos(ang), l * sin(ang) * 0.35f), 0.7f * u, StrokeCap.Round)
    }
    for (k in 0 until 12) {
        val p = Offset(w * azar(947, k), suelo + (h - suelo) * (0.25f + 0.7f * azar(948, k)))
        val r = (0.8f + 1.6f * azar(949, k)) * u
        drawOval(a.sombra(0.3f), Offset(p.x - r + 0.6f * u, p.y - r * 0.4f), Size(2 * r, 1.1f * r))
        drawOval(a.luz(mezcla(TIERRA, CAL, 0.4f), 0.04f), Offset(p.x - r, p.y - r * 0.6f), Size(2 * r, 1.1f * r))
    }
    drawRect(a.sombra(0.4f), Offset(0f, suelo), Size(w, 1.5f * u))
    drawRect(a.sombra(0.14f), Offset(0f, suelo + 1.5f * u), Size(w, 4f * u))
}

/**
 * La tinaja del vino (Nanai-vandak vende vino, y su pueblo tiene fama de beberlo): un khum de barro, ovoide, apoyado en la
 * tierra con el cerco húmedo de lo que se derrama, con sus estrías del torno, el chorreón de vino en el hombro, las dos asas,
 * la tapa de álamo con su agarradera y el cazo de calabaza apoyado.
 */
internal fun DrawScope.tinajaSeda(pie: Offset, w: Float, a: AireSeda, cerca: Float, frio: Float) {
    val u = density
    val s = w * 0.24f
    val forma = perfilSeda(pie.x, pie.y - s, s, floatArrayOf(0.18f, 0f, 0.2f, 0.04f, 0.3f, 0.1f, 0.42f, 0.28f, 0.44f, 0.5f, 0.38f, 0.75f, 0.28f, 0.9f))
    // La sombra en el muro, a la derecha.
    translate(if (a.oscuro) w * 0.012f else w * 0.035f, -w * 0.01f) { drawPath(forma, a.sombra(if (a.oscuro) 0.4f else 0.22f)) }
    // Donde pisa: el cerco húmedo del vino que se derrama al sacarlo y la sombra de contacto en la tierra.
    drawOval(a.luz(mezcla(TIERRA, BARRO, 0.55f), 0.04f, frio).copy(alpha = 0.5f), Offset(pie.x - s * 0.55f, pie.y - s * 0.15f), Size(s * 1.2f, s * 0.12f))
    contactoSeda(Offset(pie.x + s * 0.06f, pie.y - s * 0.1f), s * 0.8f, s * 0.06f, a, 0.45f)
    drawPath(forma, a.luz(ARCILLA, cerca, frio))
    clipPath(forma) {
        // Las estrías del torno y el chorreón de vino seco.
        for (j in 0 until 7) {
            val y = pie.y - s * (0.85f - 0.09f * j)
            drawLine(a.sombra(0.16f), Offset(pie.x - s * 0.5f, y), Offset(pie.x + s * 0.5f, y), 0.8f * u)
            drawLine(a.claro(0.14f), Offset(pie.x - s * 0.5f, y + 1f * u), Offset(pie.x + s * 0.5f, y + 1f * u), 0.6f * u)
        }
        val vino = a.luz(Color(0xFF5A1E28), cerca, frio).copy(alpha = 0.6f)
        drawPath(Path().apply {
            moveTo(pie.x + s * 0.02f, pie.y - s * 0.96f); cubicTo(pie.x + s * 0.12f, pie.y - s * 0.8f, pie.x + s * 0.05f, pie.y - s * 0.7f, pie.x + s * 0.1f, pie.y - s * 0.6f)
            lineTo(pie.x + s * 0.14f, pie.y - s * 0.62f); cubicTo(pie.x + s * 0.1f, pie.y - s * 0.75f, pie.x + s * 0.18f, pie.y - s * 0.85f, pie.x + s * 0.1f, pie.y - s * 0.96f); close()
        }, vino)
        pinceladas(Rect(pie.x - s * 0.5f, pie.y - s, pie.x + s * 0.5f, pie.y), a.luz(mezcla(ARCILLA, CAL, 0.4f), cerca, frio), 951, 10, u, 0.12f)
    }
    redondoSeda(forma, a, cerca, 1f, fuerza = 1.2f)
    // Las dos asas del hombro, de barro, con su sombra.
    listOf(-1f, 1f).forEach { l ->
        val c0 = Offset(pie.x + l * s * 0.33f, pie.y - s * 0.86f)
        drawArc(a.sombra(0.3f), if (l < 0) 90f else -90f, 180f, false, Offset(c0.x - s * 0.06f + 1.5f * u, c0.y - s * 0.07f + 1.5f * u), Size(s * 0.12f, s * 0.14f), style = Stroke(s * 0.035f))
        drawArc(a.luz(mezcla(ARCILLA, CAL, 0.1f), cerca, frio), if (l < 0) 90f else -90f, 180f, false, Offset(c0.x - s * 0.06f + l * s * 0.03f, c0.y - s * 0.07f), Size(s * 0.12f, s * 0.14f), style = Stroke(s * 0.035f))
    }
    // La tapa: un disco de álamo con su agarradera, un poco ladeada; el cazo de calabaza apoyado en el borde.
    val tapa = Rect(pie.x - s * 0.25f, pie.y - s * 1.04f, pie.x + s * 0.25f, pie.y - s * 0.97f)
    drawRect(a.sombra(0.3f), Offset(tapa.left + 2f * u, tapa.bottom), Size(tapa.width, 2f * u))
    drawRoundRect(a.luz(ALAMO, cerca, frio), tapa.topLeft, tapa.size, CornerRadius(2f * u))
    drawRect(a.claro(0.4f), tapa.topLeft, Size(tapa.width, 1f * u))
    drawLine(a.sombra(0.25f), Offset(tapa.left + tapa.width * 0.3f, tapa.top), Offset(tapa.left + tapa.width * 0.3f, tapa.bottom), 0.6f * u)
    drawLine(a.sombra(0.25f), Offset(tapa.left + tapa.width * 0.65f, tapa.top), Offset(tapa.left + tapa.width * 0.65f, tapa.bottom), 0.6f * u)
    val agarre = Rect(pie.x - s * 0.08f, tapa.top - s * 0.05f, pie.x + s * 0.08f, tapa.top + 1f * u)
    drawRoundRect(a.luz(mezcla(ALAMO, ALAMO_OSC, 0.3f), cerca, frio), agarre.topLeft, agarre.size, CornerRadius(2f * u))
    drawRect(a.claro(0.4f), agarre.topLeft, Size(agarre.width, 0.8f * u))
    val m0 = Offset(pie.x + s * 0.18f, tapa.top + 1f * u); val m1 = Offset(pie.x + s * 0.56f, pie.y - s * 1.12f)
    drawLine(a.sombra(0.3f), m0 + Offset(1.5f * u, 1.5f * u), m1 + Offset(1.5f * u, 1.5f * u), 2.4f * u, StrokeCap.Round)
    drawLine(a.luz(mezcla(PAJA, ALAMO, 0.5f), cerca, frio), m0, m1, 2.4f * u, StrokeCap.Round)
    drawOval(a.luz(Color(0xFFC79A52), cerca, frio), Offset(m0.x - s * 0.1f, m0.y - s * 0.05f), Size(s * 0.15f, s * 0.08f))
    drawOval(a.sombra(0.25f), Offset(m0.x - s * 0.08f, m0.y - s * 0.04f), Size(s * 0.1f, s * 0.035f))
}

/**
 * El fieltro enrollado (la cama de los caravasares), tendido en el suelo contra el muro y atado con dos sogas de pelo de
 * camello: blando, con la pelusa en el canto, la orilla de fieltro rojo de la vuelta de fuera y, en la cabeza que se ve, la
 * espiral de las vueltas con sus orillas roja y añil. Lo corta el borde derecho.
 */
internal fun DrawScope.fieltroSeda(pie: Offset, w: Float, a: AireSeda, cerca: Float) {
    val u = density
    val largo = w * 0.31f; val r = w * 0.066f
    val cuerpo = Rect(pie.x, pie.y - 2 * r, pie.x + largo, pie.y)
    val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(cuerpo, CornerRadius(r * 0.8f))) }
    // La sombra en el muro y en el suelo.
    drawPath(corridoSeda(forma, if (a.oscuro) -w * 0.02f else w * 0.022f, -w * 0.012f), a.sombra(if (a.oscuro) 0.34f else 0.2f))
    contactoSeda(Offset(cuerpo.center.x + 4f * u, pie.y), largo * 1.05f, 6f * u, a, 0.36f)
    val gris = Color(0xFFC9B48E)
    val fieltro = a.luz(gris, cerca)
    drawPath(forma, fieltro)
    clipPath(forma) {
        pinceladas(cuerpo, a.luz(mezcla(gris, SOMBRA_MUEBLE, 0.3f), cerca), 961, 18, u, 0.22f)
        pinceladas(cuerpo, a.luz(mezcla(gris, LUZ_MUEBLE, 0.5f), cerca), 962, 12, u, 0.2f)
        // El bulto: la luz arriba, la sombra abajo, sin degradados.
        drawRect(a.claro(0.3f), Offset(cuerpo.left, cuerpo.top + r * 0.2f), Size(largo, r * 0.45f))
        drawRect(a.sombra(0.16f), Offset(cuerpo.left, cuerpo.top + r * 1.25f), Size(largo, r * 0.75f))
        drawRect(a.sombra(0.16f), Offset(cuerpo.left, cuerpo.top + r * 1.6f), Size(largo, r * 0.4f))
        // La orilla de la vuelta de fuera, en diagonal, con su ribete de fieltro rojo.
        val o0 = Offset(cuerpo.left + largo * 0.1f, cuerpo.bottom); val o1 = Offset(cuerpo.left + largo * 0.3f, cuerpo.top)
        drawLine(a.sombra(0.35f), o0 + Offset(1.4f * u, 0f), o1 + Offset(1.4f * u, 0f), 1.4f * u)
        drawLine(a.luz(FIELTRO, cerca), o0, o1, 2.6f * u)
        drawLine(a.luz(ANIL, cerca), o0 - Offset(2.4f * u, 0f), o1 - Offset(2.4f * u, 0f), 1f * u)
    }
    // La pelusa del canto de arriba.
    var x = cuerpo.left + r
    var j = 0
    while (x < cuerpo.right) {
        drawOval(fieltro, Offset(x, cuerpo.top - 1f * u * azar(963, j)), Size((3f + 3f * azar(964, j)) * u, 2.4f * u))
        x += 4f * u; j++
    }
    // Las dos sogas, que dan la vuelta al rollo (se curvan con él) y aprietan el fieltro.
    listOf(0.4f, 0.76f).forEach { f ->
        val xs = cuerpo.left + largo * f
        val soga = a.luz(Color(0xFF8A6A48), cerca)
        val vuelta = Path().apply {
            moveTo(xs - 1.6f * u, cuerpo.top + 0.4f * u)
            cubicTo(xs + 1.2f * u, cuerpo.top + r * 0.6f, xs + 1.2f * u, cuerpo.bottom - r * 0.6f, xs - 1.6f * u, cuerpo.bottom - 0.4f * u)
        }
        drawPath(corridoSeda(vuelta, 1.6f * u, 0.6f * u), a.sombra(0.32f), style = Stroke(3.6f * u))
        drawPath(vuelta, soga, style = Stroke(3.4f * u))
        drawPath(corridoSeda(vuelta, -0.8f * u, 0f), a.claro(0.3f), style = Stroke(0.7f * u))
        // El fieltro se hunde a los lados de la soga.
        drawLine(a.sombra(0.2f), Offset(xs - 3.4f * u, cuerpo.top + 1f * u), Offset(xs - 3.4f * u, cuerpo.bottom - 1f * u), 1f * u)
        drawLine(a.sombra(0.2f), Offset(xs + 3f * u, cuerpo.top + 1f * u), Offset(xs + 3f * u, cuerpo.bottom - 1f * u), 1f * u)
    }
    // La cabeza del rollo, vista apenas de lado: la espiral de las vueltas, con las orillas roja y añil.
    val cab = Rect(cuerpo.left - r * 0.45f, cuerpo.top - r * 0.04f, cuerpo.left + r * 0.75f, cuerpo.bottom + r * 0.04f)
    drawOval(a.luz(mezcla(gris, SOMBRA_MUEBLE, 0.12f), cerca), cab.topLeft, cab.size)
    // Las vueltas del fieltro, gruesas y blandas: una espiral ancha que se cierra hacia el hueco del centro; la vuelta de
    // fuera lleva su orilla de fieltro rojo y la de dentro, la de añil.
    val espiral = Path()
    val pasos = 40
    for (j in 0..pasos) {
        val t = j / pasos.toFloat()
        val ang = 4.2f + 6.283f * 2.6f * t
        val fr = 0.9f - 0.78f * t
        val q = Offset(cab.center.x + cab.width / 2 * fr * cos(ang), cab.center.y + cab.height / 2 * fr * sin(ang))
        if (j == 0) espiral.moveTo(q.x, q.y) else espiral.lineTo(q.x, q.y)
    }
    drawPath(espiral, a.sombra(0.4f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
    drawPath(corridoSeda(espiral, -0.9f * u, -0.6f * u), a.luz(mezcla(Color(0xFFC9B48E), LUZ_MUEBLE, 0.3f), cerca), style = Stroke(0.8f * u))
    drawOval(a.luz(FIELTRO, cerca), cab.topLeft, cab.size, style = Stroke(2.2f * u))
    drawArc(a.luz(ANIL, cerca), 200f, 140f, false, Offset(cab.left + cab.width * 0.18f, cab.top + cab.height * 0.18f), Size(cab.width * 0.64f, cab.height * 0.64f), style = Stroke(1.2f * u))
    drawOval(a.sombra(0.5f), Offset(cab.center.x - cab.width * 0.08f, cab.center.y - cab.height * 0.06f), Size(cab.width * 0.16f, cab.height * 0.12f))
}

/**
 * Lo que cuelga de la estaca de la derecha, a media sombra y cortado por el borde: la soga de pelo de camello enrollada en
 * vueltas flojas, con su cabo suelto, y la calabaza del agua atada por la cintura, con su tapón de madera.
 */
internal fun DrawScope.colgadoSeda(estaca: Offset, w: Float, a: AireSeda, cerca: Float, frio: Float) {
    val u = density
    val sombraDia = if (a.oscuro) 0f else 0.14f
    val corr = if (a.oscuro) Offset(-w * 0.012f, w * 0.008f) else Offset(w * 0.014f, w * 0.012f)
    val soga = a.luz(Color(0xFF8A6A48), cerca, frio)
    val sogaOsc = mezcla(soga, a.hondo, 0.35f)
    // Las vueltas de la soga: óvalos flojos que cuelgan de la estaca, cada uno un poco corrido.
    val vueltas = listOf(Triple(0.055f, 0.13f, 0.004f), Triple(0.062f, 0.145f, -0.006f), Triple(0.05f, 0.125f, 0.01f), Triple(0.058f, 0.14f, 0.0f))
    val lazo = Path()
    vueltas.forEach { (rx, alto, dx) ->
        val r = Rect(estaca.x - w * rx + w * dx, estaca.y - w * 0.004f, estaca.x + w * rx + w * dx, estaca.y + w * alto)
        lazo.addOval(r)
    }
    drawPath(corridoSeda(lazo, corr.x, corr.y), a.sombra(0.22f), style = Stroke(2.6f * u))
    drawPath(lazo, sogaOsc, style = Stroke(3f * u))
    drawPath(lazo, soga, style = Stroke(2f * u))
    drawPath(corridoSeda(lazo, -0.6f * u, -0.6f * u), a.claro(0.25f), style = Stroke(0.6f * u))
    // El cabo suelto, que cuelga más abajo, y la atadura en la estaca.
    val cabo = Path().apply {
        moveTo(estaca.x - w * 0.01f, estaca.y + w * 0.02f)
        cubicTo(estaca.x - w * 0.03f, estaca.y + w * 0.1f, estaca.x + w * 0.004f, estaca.y + w * 0.15f, estaca.x - w * 0.016f, estaca.y + w * 0.2f)
    }
    drawPath(corridoSeda(cabo, corr.x, corr.y), a.sombra(0.2f), style = Stroke(2.4f * u))
    drawPath(cabo, soga, style = Stroke(2.4f * u, cap = StrokeCap.Round))
    drawCircle(sogaOsc, 3.6f * u, estaca + Offset(0f, 1.6f * u))
    // La calabaza: dos panzas, la de abajo mayor, colgada de un cordel por la cintura.
    val c = Offset(estaca.x - w * 0.06f, estaca.y + w * 0.105f)
    val s = w * 0.07f
    val cal = perfilSeda(c.x, c.y - s, s, floatArrayOf(0.08f, 0f, 0.1f, 0.06f, 0.22f, 0.22f, 0.18f, 0.42f, 0.13f, 0.5f, 0.3f, 0.7f, 0.36f, 0.92f, 0.26f, 1.1f, 0.12f, 1.18f))
    drawLine(sogaOsc, estaca, Offset(c.x, c.y - s * 0.5f), 1f * u)
    drawPath(corridoSeda(cal, corr.x, corr.y), a.sombra(0.22f))
    val piel = a.luz(Color(0xFFC79A52), cerca, frio)
    drawPath(cal, piel)
    redondoSeda(cal, a, cerca, -1f, fuerza = 1.1f)
    clipPath(cal) {
        // Las vetas de la cáscara seca y una mancha de uso.
        for (j in 0 until 3) drawLine(a.sombra(0.14f), Offset(c.x - s * 0.1f + j * s * 0.12f, c.y - s * 0.4f), Offset(c.x - s * 0.2f + j * s * 0.2f, c.y + s * 0.15f), 0.6f * u)
        drawOval(a.luz(Color(0xFF8A6034), cerca, frio).copy(alpha = 0.4f), Offset(c.x + s * 0.02f, c.y - s * 0.05f), Size(s * 0.22f, s * 0.14f))
    }
    // El cordel de la cintura y el tapón de madera.
    drawLine(sogaOsc, Offset(c.x - s * 0.14f, c.y - s * 0.5f), Offset(c.x + s * 0.14f, c.y - s * 0.48f), 1.6f * u)
    drawRoundRect(a.luz(SedaTinta.ALAMO_OSC, cerca, frio), Offset(c.x - s * 0.06f, c.y - s * 1.08f), Size(s * 0.12f, s * 0.1f), CornerRadius(1f * u))
    // A media sombra: el rincón de la derecha no lo alcanza el sol.
    if (sombraDia > 0f) drawPath(cal, a.sombra(sombraDia))
}

// ---------------------------------------------------------------- en los huecos libres

/**
 * Lo del tema en lo que queda libre de la repisa [k], a la derecha de [oc]: según cuánto hueco haya, una pila de papel de
 * Samarcanda con su piedra, la copa de plata de ocho lóbulos, un frasquito de vidrio en su nido de paja, las bolsitas de
 * almizcle en su plato. Cada repisa escoge lo suyo, para que no se repita.
 */
internal fun DrawScope.huecoLibreSeda(k: Int, r: Rect, oc: Float, a: AireSeda, cerca: Float, frio: Float) {
    val u = density
    val libre = (r.right - oc) / u
    val y = r.bottom - 1.2f * u
    when {
        libre >= 150f -> if (k % 2 == 1) {
            pilaPapelSeda(Offset(oc + 10f * u, y), 64f * u, 5, a, cerca + 0.04f, frio, k)
            copaPlataSeda(Offset(r.right - 50f * u, y), 46f * u, a, cerca - 0.03f, frio)
        } else {
            frascoPajaSeda(Offset(oc + 10f * u, y), 70f * u, a, cerca + 0.03f, frio)
            almizcleSeda(Offset(r.right - 44f * u, y), 52f * u, a, cerca - 0.03f, frio, k)
        }
        libre >= 74f -> when (k % 3) {
            0 -> pilaPapelSeda(Offset(oc + 8f * u, y), 58f * u, 4, a, cerca, frio, k)
            1 -> copaPlataSeda(Offset((oc + r.right) / 2, y), 44f * u, a, cerca, frio)
            else -> almizcleSeda(Offset((oc + r.right) / 2, y), 50f * u, a, cerca, frio, k)
        }
        libre >= 44f -> when (k % 3) {
            0 -> copaPlataSeda(Offset((oc + r.right) / 2, y), 36f * u, a, cerca, frio)
            1 -> almizcleSeda(Offset((oc + r.right) / 2, y), 36f * u, a, cerca, frio, k)
            else -> pilaPapelSeda(Offset(oc + 5f * u, y), libre * u - 10f * u, 3, a, cerca, frio, k)
        }
        libre >= 24f -> {
            // Un cálamo apoyado en el último volumen.
            val e0 = Offset(oc + 12f * u, y); val e1 = e0 + Offset(-9f * u, -50f * u)
            drawLine(a.sombra(if (a.oscuro) 0.4f else 0.26f), e0 + Offset(4f * u, 0f), e1 + Offset(7f * u, 4f * u), 1.6f * u, StrokeCap.Round)
            drawLine(a.luz(mezcla(PAJA, ALAMO, 0.4f), cerca, frio), e0, e1, 2f * u, StrokeCap.Round)
            drawLine(a.luz(TINTA, cerca, frio), e0, e0 + (e1 - e0) * 0.06f, 2f * u, StrokeCap.Round)
        }
    }
}

/** Una pila de papel de Samarcanda, en blanco, con los cantos desparejos y una piedra de río encima para que no se vuele. */
internal fun DrawScope.pilaPapelSeda(izq: Offset, an: Float, cuantas: Int, a: AireSeda, cerca: Float, frio: Float, semilla: Int) {
    val u = density
    val g = 3f * u
    contactoSeda(Offset(izq.x + an * 0.55f, izq.y), an * 1.15f, 5f * u, a, 0.36f)
    drawRect(a.sombra(if (a.oscuro) 0.34f else 0.2f), Offset(izq.x + 8f * u, izq.y - cuantas * g * 2f + 3f * u), Size(an, cuantas * g * 2f - 3f * u))
    for (j in 0 until cuantas * 2) {
        val dx = (azar(semilla + 50, j) - 0.5f) * 5f * u
        val rr = Rect(izq.x + dx, izq.y - (j + 1) * g, izq.x + dx + an * (0.94f + 0.06f * azar(semilla + 51, j)), izq.y - j * g)
        drawRect(a.luz(mezcla(PAPEL, ADOBE, 0.1f + 0.15f * azar(semilla + 52, j)), cerca, frio), rr.topLeft, rr.size)
        drawLine(a.sombra(0.22f), Offset(rr.left, rr.bottom), Offset(rr.right, rr.bottom), 0.5f * u)
    }
    drawRect(a.claro(0.4f), Offset(izq.x, izq.y - cuantas * 2 * g), Size(an, 0.8f * u))
    val p = Offset(izq.x + an * 0.5f, izq.y - cuantas * 2 * g)
    val piedra = Path().apply { addOval(Rect(p.x - an * 0.2f, p.y - an * 0.14f, p.x + an * 0.2f, p.y + 1f * u)) }
    drawPath(piedra, a.luz(Color(0xFF7E8288), cerca, frio))
    redondoSeda(piedra, a, cerca, 1f)
    drawOval(a.claro(0.4f), Offset(p.x - an * 0.12f, p.y - an * 0.12f), Size(an * 0.12f, an * 0.04f))
}

/** La copa de plata sogdiana de ocho lóbulos, sobre su pie, con la anilla para el pulgar. */
internal fun DrawScope.copaPlataSeda(pie: Offset, an: Float, a: AireSeda, cerca: Float, frio: Float) {
    val u = density
    val alto = an * 0.62f
    contactoSeda(Offset(pie.x + 2f * u, pie.y), an * 0.8f, 4f * u, a, 0.34f)
    val plata = a.luz(Color(0xFFC7C9CC), cerca, frio)
    val copa = perfilSeda(pie.x, pie.y - alto, an, floatArrayOf(0.5f, 0f, 0.48f, 0.16f, 0.4f, 0.36f, 0.28f, 0.48f, 0.1f, 0.54f, 0.08f, 0.56f))
    drawPath(copa, plata)
    clipPath(copa) {
        // Los lóbulos: ocho gajos, cada uno con su luz y su sombra.
        for (j in 0 until 6) {
            val t = (j + 0.5f) / 6f - 0.5f
            val x = pie.x + t * an * 0.95f
            drawLine(a.sombra(0.22f), Offset(x, pie.y - alto), Offset(pie.x + t * an * 0.3f, pie.y - alto * 0.18f), 0.8f * u)
            drawLine(a.claro(0.45f), Offset(x - 1.6f * u, pie.y - alto), Offset(pie.x + t * an * 0.3f - 1f * u, pie.y - alto * 0.18f), 0.7f * u)
        }
    }
    redondoSeda(copa, a, cerca, 1f, fuerza = 1.2f)
    drawRect(a.luz(Color(0xFFA6A8AE), cerca, frio), Offset(pie.x - an * 0.12f, pie.y - alto * 0.12f), Size(an * 0.24f, alto * 0.12f))
    drawOval(a.luz(Color(0xFFD6D8DC), cerca, frio), Offset(pie.x - an * 0.5f, pie.y - alto - an * 0.04f), Size(an, an * 0.08f), style = Stroke(1f * u))
    // La anilla del asa, a un lado, con su placa para el pulgar.
    drawCircle(plata, an * 0.1f, Offset(pie.x + an * 0.52f, pie.y - alto * 0.6f), style = Stroke(1.4f * u))
    drawRect(plata, Offset(pie.x + an * 0.42f, pie.y - alto * 0.9f), Size(an * 0.2f, 1.6f * u))
    if (!a.oscuro) drawCircle(Color.White.copy(alpha = 0.8f), 1.2f * u, Offset(pie.x - an * 0.3f, pie.y - alto * 0.7f))
    else drawCircle(FILO_MUEBLE.copy(alpha = 0.3f + 0.6f * cerca), 1.2f * u, Offset(pie.x + an * 0.3f, pie.y - alto * 0.7f))
}

/** Un frasquito de vidrio tumbado en su nido de paja, como llegó. */
internal fun DrawScope.frascoPajaSeda(izq: Offset, largo: Float, a: AireSeda, cerca: Float, frio: Float) {
    val u = density
    contactoSeda(Offset(izq.x + largo * 0.5f, izq.y), largo * 1.1f, 5f * u, a, 0.34f)
    val paja = a.luz(PAJA, cerca, frio)
    val pajaOsc = a.luz(mezcla(PAJA, BARRO, 0.5f), cerca, frio)
    for (j in 0 until 30) {
        val o = Offset(izq.x + largo * azar(971, j), izq.y - largo * 0.2f * azar(972, j))
        val ang = -0.5f + 1f * azar(973, j)
        val l = largo * (0.12f + 0.2f * azar(974, j))
        drawLine(if (j % 3 == 0) pajaOsc else paja, o, o + Offset(l * cos(ang), l * sin(ang) * 0.3f), 0.8f * u, StrokeCap.Round)
    }
    val c = Offset(izq.x + largo * 0.45f, izq.y - largo * 0.16f)
    rotate(-8f, c) {
        val cuerpo = Path().apply { addOval(Rect(c.x - largo * 0.26f, c.y - largo * 0.11f, c.x + largo * 0.18f, c.y + largo * 0.11f)) }
        drawPath(cuerpo, a.luz(VIDRIO, cerca, frio).copy(alpha = 0.7f))
        drawRect(a.luz(VIDRIO, cerca, frio).copy(alpha = 0.7f), Offset(c.x + largo * 0.16f, c.y - largo * 0.035f), Size(largo * 0.18f, largo * 0.07f))
        drawPath(bordeSeda(cuerpo, 0f, largo * 0.03f), a.luz(Color(0xFF3E6E5E), cerca, frio).copy(alpha = 0.5f))
        drawRoundRect(if (a.oscuro) FILO_MUEBLE.copy(alpha = 0.3f + 0.5f * cerca) else Color.White.copy(alpha = 0.8f), Offset(c.x - largo * 0.18f, c.y - largo * 0.07f), Size(largo * 0.22f, 1.6f * u), CornerRadius(0.8f * u))
    }
    for (j in 0 until 14) {
        val o = Offset(izq.x + largo * (0.1f + 0.8f * azar(975, j)), izq.y - largo * 0.05f * azar(976, j))
        val ang = -0.4f + 0.8f * azar(977, j)
        val l = largo * (0.1f + 0.16f * azar(978, j))
        drawLine(paja, o, o + Offset(l * cos(ang), l * sin(ang) * 0.3f), 0.8f * u, StrokeCap.Round)
    }
}

/** Las bolsitas de almizcle (vainas peludas, pardas) en un plato de barro. */
internal fun DrawScope.almizcleSeda(pie: Offset, an: Float, a: AireSeda, cerca: Float, frio: Float, semilla: Int) {
    val u = density
    contactoSeda(Offset(pie.x + 2f * u, pie.y), an * 1.05f, 4f * u, a, 0.34f)
    val cuantas = 3 + Math.floorMod(semilla, 2)
    for (j in 0 until cuantas) {
        val c = Offset(pie.x + an * (-0.26f + 0.52f * j / (cuantas - 1).coerceAtLeast(1)), pie.y - an * (0.18f + 0.06f * azar(semilla + 80, j)))
        val rr = an * (0.13f + 0.03f * azar(semilla + 81, j))
        val bolsa = Path().apply { addOval(Rect(c.x - rr, c.y - rr * 0.85f, c.x + rr, c.y + rr * 0.85f)) }
        drawPath(bolsa, a.luz(Color(0xFF6E4E34), cerca, frio))
        clipPath(bolsa) {
            for (i in 0 until 10) {
                val o = Offset(c.x + rr * (azar(semilla + 82 + j, i) - 0.5f) * 2f, c.y + rr * (azar(semilla + 90 + j, i) - 0.5f) * 1.6f)
                drawLine(a.luz(Color(0xFFA88A64), cerca, frio).copy(alpha = 0.6f), o, o + Offset(1.6f * u, -1.2f * u), 0.6f * u)
            }
        }
        redondoSeda(bolsa, a, cerca, 1f)
        drawCircle(a.luz(Color(0xFF3A2618), cerca, frio), rr * 0.16f, c - Offset(0f, rr * 0.5f))
    }
    val plato = Path().apply {
        moveTo(pie.x - an * 0.5f, pie.y - an * 0.14f); lineTo(pie.x + an * 0.5f, pie.y - an * 0.14f)
        cubicTo(pie.x + an * 0.4f, pie.y - an * 0.02f, pie.x + an * 0.3f, pie.y, pie.x + an * 0.2f, pie.y)
        lineTo(pie.x - an * 0.2f, pie.y); cubicTo(pie.x - an * 0.3f, pie.y, pie.x - an * 0.4f, pie.y - an * 0.02f, pie.x - an * 0.5f, pie.y - an * 0.14f); close()
    }
    drawPath(plato, a.luz(mezcla(ARCILLA, CAL, 0.15f), cerca, frio))
    drawPath(bordeSeda(plato, 0f, -1.2f * u), a.sombra(0.3f))
    drawLine(a.luz(BERMELLON, cerca, frio).copy(alpha = 0.6f), Offset(pie.x - an * 0.46f, pie.y - an * 0.11f), Offset(pie.x + an * 0.46f, pie.y - an * 0.11f), 1f * u)
    drawLine(a.claro(0.4f), Offset(pie.x - an * 0.5f, pie.y - an * 0.14f), Offset(pie.x + an * 0.1f, pie.y - an * 0.14f), 0.7f * u)
}
