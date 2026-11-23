package io.github.ruddy2003.vestigium.ui.biblioteca

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
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.ceil
import kotlin.math.floor

/**
 * La librería de Horror cósmico ([MuebleEstanteArcano]), hecha con las maderas de un barco: la regala por cornisa con el cordón
 * tallado debajo, los costados de dos tablones con sus tarugos, los agujeros de los pernos y lo que queda de la pintura del casco,
 * las tablas gruesas de canto redondo con clavos de cobre y su chorrera de verdín, una curva de madera en cada esquina de arriba
 * de los casilleros, el fondo forrado de paño ciruela con sus tachuelas, y el zócalo con el cajón clavado. Se pinta con la luz
 * entera y encima va el mapa de luz del cuerpo ([luzCuerpo]).
 */
internal object ObraEstanteArcano {
    private val P = PaletaArcano
    private val M = MuebleEstanteArcano

    // ---------------------------------------------------------------- la cornisa

    /** La tapa de encima, la regala con su moldura, el friso del cordón tallado y el junquillo de abajo. */
    fun DrawScope.cornisa(g: MarcoArcano) {
        val w = g.w; val u = density
        val am = g.am
        val y0 = g.apoyo; val ch = g.ch
        fun y(f: Float) = y0 + w * f
        fun c(col: Color, f: Float = 0f) = am.pleno(col, f)
        drawRect(c(P.TECA_OSC), Offset(0f, y0), Size(w, ch - y0 + 2f))
        // La tapa: la cara de encima, gastada, con su veta.
        drawRect(c(P.TECA_LUZ, 0.05f), Offset(0f, y0), Size(w, w * 0.012f))
        vetas(Rect(0f, y0, w, y(0.012f)), c(P.TECA_OSC).copy(alpha = 0.4f), 7601, densidad = 0.6f, grosor = 0.5f, ondulacion = 0.3f)
        // La regala: el canto grueso y redondo de la borda, con su luz arriba y su sombra abajo, y una ranura.
        val r0 = y(0.012f); val r1 = y(0.048f)
        drawRect(c(P.TECA), Offset(0f, r0), Size(w, r1 - r0))
        drawRect(c(P.TECA_LUZ, 0.12f), Offset(0f, r0), Size(w, (r1 - r0) * 0.28f))
        drawRect(c(P.TECA_LUZ), Offset(0f, r0 + (r1 - r0) * 0.28f), Size(w, (r1 - r0) * 0.14f))
        drawRect(c(P.TECA_OSC), Offset(0f, r0 + (r1 - r0) * 0.72f), Size(w, (r1 - r0) * 0.28f))
        drawLine(c(P.TECA_HONDA), Offset(0f, r0 + (r1 - r0) * 0.55f), Offset(w, r0 + (r1 - r0) * 0.55f), 1.1f * u)
        drawLine(c(P.TECA_LUZ, 0.2f).copy(alpha = 0.6f), Offset(0f, r0 + (r1 - r0) * 0.55f + 1.2f * u), Offset(w, r0 + (r1 - r0) * 0.55f + 1.2f * u), 0.7f * u)
        vetas(Rect(0f, r0, w, r1), c(P.TECA_HONDA).copy(alpha = 0.35f), 7602, densidad = 0.5f, grosor = 0.6f, ondulacion = 0.3f)
        // Lo gastado de la regala: la teca lavada, gris, donde rozaba el cabo.
        for (j in 0 until 5) {
            val x = w * (0.08f + 0.2f * j + 0.06f * azar(7603, j))
            drawPath(irregularArcano(Offset(x, r0 + (r1 - r0) * 0.3f), w * (0.025f + 0.02f * azar(7604, j)), (r1 - r0) * 0.22f, 7605 + j, 8, 0.4f), c(P.TECA_GRIS).copy(alpha = 0.55f))
        }
        // Los pernos de bronce de la regala.
        var xp = w * 0.04f
        while (xp < w) { clavoArcano(Offset(xp, r0 + (r1 - r0) * 0.5f), 1.7f * u, am, 0.9f, P.BRONCE); xp += w * 0.16f }
        drawRect(am.sombra(0.55f), Offset(0f, r1), Size(w, 2.6f * u))
        // El friso: la tabla con el cordón tallado (una soga torcida), y restos del verde del casco.
        val f0 = y(0.05f); val f1 = y(0.094f)
        drawRect(c(P.TECA, -0.08f), Offset(0f, f0), Size(w, f1 - f0))
        drawRect(am.sombra(0.4f), Offset(0f, f0), Size(w, 2.2f * u))
        for (j in 0 until 4) {
            val x = w * (0.1f + 0.26f * j + 0.08f * azar(7610, j))
            drawPath(irregularArcano(Offset(x, f0 + (f1 - f0) * 0.5f), w * (0.04f + 0.03f * azar(7611, j)), (f1 - f0) * 0.4f, 7612 + j, 10, 0.45f), c(P.CASCO).copy(alpha = 0.75f))
        }
        cordon(Rect(0f, f0 + (f1 - f0) * 0.2f, w, f1 - (f1 - f0) * 0.18f), g)
        // El junquillo de abajo, con su luz, y la sombra que deja en el primer casillero.
        drawRect(c(P.TECA_LUZ, 0.04f), Offset(0f, f1), Size(w, (ch - f1) * 0.45f))
        drawRect(c(P.TECA_OSC), Offset(0f, f1 + (ch - f1) * 0.45f), Size(w, (ch - f1) * 0.55f + 2f))
        drawLine(am.brillo(0.5f, 0.9f), Offset(0f, f1 + 0.8f * u), Offset(w, f1 + 0.8f * u), 0.8f * u)
    }

    /** El cordón tallado: una soga de tres cabos torcidos, cada vuelta con su luz arriba a la izquierda y su sombra. */
    private fun DrawScope.cordon(r: Rect, g: MarcoArcano) {
        val u = density
        val am = g.am
        val paso = r.height * 0.62f
        val cuerpo = Path(); val luces = Path(); val sombras = Path()
        var x = r.left - paso
        while (x < r.right + paso) {
            // Cada vuelta, una hoja inclinada de arriba a la derecha a abajo a la izquierda.
            val a = Offset(x + paso * 0.9f, r.top); val b = Offset(x, r.bottom)
            cuerpo.moveTo(a.x - paso * 0.35f, a.y)
            cuerpo.quadraticTo(a.x + paso * 0.3f, a.y + r.height * 0.1f, b.x + paso * 0.35f, b.y)
            cuerpo.quadraticTo(b.x - paso * 0.3f, b.y - r.height * 0.1f, a.x - paso * 0.35f, a.y)
            luces.moveTo(a.x - paso * 0.25f, a.y + r.height * 0.08f); luces.lineTo(b.x + paso * 0.05f, b.y - r.height * 0.22f)
            sombras.moveTo(a.x + paso * 0.15f, a.y + r.height * 0.22f); sombras.lineTo(b.x + paso * 0.42f, b.y - r.height * 0.06f)
            x += paso
        }
        clipRect(r.left, r.top - 1f, r.right, r.bottom + 1f) {
            drawRect(am.sombra(0.45f), r.topLeft, r.size)
            drawPath(cuerpo, am.pleno(P.TECA))
            drawPath(sombras, am.sombra(0.5f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
            drawPath(luces, am.pleno(P.TECA_LUZ, 0.15f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        }
        drawLine(am.sombra(0.5f), Offset(r.left, r.bottom), Offset(r.right, r.bottom), 1f * u)
    }

    // ---------------------------------------------------------------- los casilleros

    /**
     * Un casillero: el fondo de paño ciruela con su pelo gastado, la humedad y las tachuelas de arriba; la sombra franca de la tabla
     * de arriba y la del costado de la izquierda; el cielo (la cara de abajo de la tabla), los cantos de dentro de los costados y el
     * piso de la tabla de abajo en escorzo.
     */
    fun DrawScope.casillero(k: Int, r: Rect, g: MarcoArcano) {
        val w = g.w; val u = density
        val af = g.af; val am = g.am
        // Un píxel de más arriba (salvo en el primero, que toca la imagen de la corona), abajo y a los lados: ninguna junta a medio
        // píxel deja asomar el fondo.
        val y0 = if (k == 0) r.top else floor(r.top) - 1f
        val y1 = ceil(r.bottom) + 1f
        val x0 = floor(r.left) - 1f; val x1 = ceil(r.right) + 1f
        drawRect(af.pleno(P.PANO), Offset(x0, y0), Size(x1 - x0, y1 - y0))
        // El paño: el pelo gastado (pinceladas claras y hondas), las manchas de humedad con su cerco y alguna picadura.
        pinceladas(r, af.pleno(P.PANO_LUZ), 7700 + k * 13, 14, u, 0.16f)
        pinceladas(r, af.pleno(P.PANO_HONDO), 7701 + k * 13, 10, u, 0.2f)
        for (j in 0 until 2) {
            val c = Offset(r.left + r.width * (0.15f + 0.7f * azar(7710 + k, j)), r.top + r.height * (0.35f + 0.45f * azar(7711 + k, j)))
            val m = irregularArcano(c, w * (0.04f + 0.04f * azar(7712 + k, j)), r.height * (0.12f + 0.1f * azar(7713 + k, j)), 7714 + k * 7 + j, 11, 0.35f)
            drawPath(m, af.pleno(mezcla(P.PANO, P.SALOBRE, 0.5f)).copy(alpha = 0.3f))
            drawPath(m, af.pleno(mezcla(P.PANO_LUZ, P.YESO, 0.3f)).copy(alpha = 0.35f), style = Stroke(1f * u))
        }
        val picas = Path()
        for (j in 0 until 5) {
            val px = r.left + r.width * azar(7720 + k, j); val py = r.top + r.height * (0.3f + 0.6f * azar(7721 + k, j))
            val pr = (0.8f + 1.2f * azar(7722 + k, j)) * u
            picas.addOval(Rect(px - pr, py - pr * 0.7f, px + pr, py + pr * 0.7f))
        }
        drawPath(picas, af.pleno(P.PANO_HONDO).copy(alpha = 0.8f))
        // Las calvas del paño, donde se gastó el pelo y se ve la trama, más claras.
        for (j in 0 until 2) {
            val c = Offset(r.left + r.width * (0.1f + 0.8f * azar(7731 + k, j)), r.top + r.height * (0.4f + 0.35f * azar(7732 + k, j)))
            drawPath(irregularArcano(c, w * (0.03f + 0.03f * azar(7733 + k, j)), r.height * 0.1f, 7734 + k * 3 + j, 12, 0.35f), af.pleno(P.PANO_LUZ).copy(alpha = 0.28f))
        }
        // La sombra franca de la tabla de arriba y su degradado, y la del costado de la izquierda (la ventana está a la izquierda).
        val franca = w * 0.05f
        drawRect(af.sombra(if (af.oscuro) 0.5f else 0.46f), r.topLeft, Size(r.width, franca))
        drawRect(af.sombra(0.2f), Offset(r.left, r.top + franca), Size(r.width, w * 0.025f))
        drawRect(af.sombra(0.1f), Offset(r.left, r.top + franca + w * 0.025f), Size(r.width, w * 0.02f))
        drawPath(poligonoArcano(r.left, r.top, r.left + w * 0.06f, r.top, r.left + w * 0.04f, r.bottom, r.left, r.bottom), af.sombra(if (af.lumbre) 0.22f else 0.3f))
        // Las tachuelas de latón del paño, por arriba (en la sombra de la tabla).
        var xt = r.left + w * 0.03f
        while (xt < r.right - w * 0.02f) { drawCircle(af.pleno(P.LATON_OSC), 1.1f * u, Offset(xt, r.top + franca * 0.75f)); xt += w * 0.055f }
        // El cielo del casillero: la cara de abajo de la tabla de arriba.
        val cielo = w * 0.012f
        drawRect(am.pleno(P.TECA_HONDA), Offset(r.left, r.top), Size(r.width, cielo))
        // Los cantos de dentro de los costados: el de la izquierda en sombra, el de la derecha mirando a la ventana.
        val e = w * 0.022f
        drawPath(poligonoArcano(r.left - 1f, r.top, r.left + e, r.top + cielo, r.left + e, r.bottom - w * 0.016f, r.left - 1f, r.bottom), am.pleno(P.TECA_OSC, -0.12f))
        drawPath(poligonoArcano(r.right + 1f, r.top, r.right - e, r.top + cielo, r.right - e, r.bottom - w * 0.016f, r.right + 1f, r.bottom), am.pleno(P.TECA, 0.1f))
        drawLine(am.brillo(0.3f, 0.6f), Offset(r.right - e, r.top + cielo), Offset(r.right - e, r.bottom - w * 0.016f), 0.8f * u)
        // El piso: la cara de arriba de la tabla de abajo, en escorzo, gastada en el medio.
        val piso = w * 0.016f
        drawRect(am.pleno(P.TECA_LUZ, -0.06f), Offset(r.left, r.bottom - piso), Size(r.width, piso + 1f))
        drawRect(am.pleno(P.TECA_GRIS).copy(alpha = 0.35f), Offset(r.left + r.width * 0.2f, r.bottom - piso * 0.7f), Size(r.width * 0.55f, piso * 0.5f))
        drawRect(am.sombra(0.3f), Offset(r.left, r.bottom - piso), Size(r.width, 1.2f * u))
    }

    // ---------------------------------------------------------------- las tablas

    /** Una tabla: el canto redondo con su luz arriba y su sombra abajo, la veta, los clavos de cobre con su chorrera de verdín. */
    fun DrawScope.tabla(k: Int, r: Rect, g: MarcoArcano) {
        val w = g.w; val u = density
        val am = g.am
        fun c(col: Color, f: Float = 0f) = am.pleno(col, f)
        // Un píxel de más arriba y abajo: la junta con el casillero, a medio píxel, no deja ver el fondo.
        val y0 = floor(r.top); val y1 = ceil(r.bottom) + 1f
        drawRect(c(P.TECA), Offset(0f, y0), Size(w, y1 - y0))
        val hh = r.height
        drawRect(c(P.TECA_LUZ, 0.06f), Offset(0f, r.top), Size(w, hh * 0.22f))
        drawRect(c(P.TECA_LUZ, 0.2f), Offset(0f, r.top + hh * 0.08f), Size(w, hh * 0.07f))
        drawRect(c(P.TECA_OSC), Offset(0f, r.top + hh * 0.7f), Size(w, hh * 0.3f))
        drawRect(c(P.TECA_HONDA), Offset(0f, r.bottom - hh * 0.1f), Size(w, hh * 0.1f + 1f))
        vetas(Rect(0f, r.top, w, r.bottom), c(P.TECA_OSC).copy(alpha = 0.45f), 7800 + k, densidad = 0.7f, grosor = 0.6f, ondulacion = 0.4f)
        // La teca lavada (gris plata) donde se apoyaban las cosas, y un resto de minio.
        for (j in 0 until 3) {
            val x = w * (0.1f + 0.3f * j + 0.15f * azar(7810 + k, j))
            drawPath(irregularArcano(Offset(x, r.top + hh * 0.45f), w * (0.03f + 0.03f * azar(7811 + k, j)), hh * 0.18f, 7812 + k * 5 + j, 8, 0.4f), c(P.TECA_GRIS).copy(alpha = 0.4f))
        }
        // Los clavos de cobre, de tres en tres, y la chorrera de verdín que baja de algunos.
        var x = w * (0.13f + 0.03f * azar(7820, k))
        var j = 0
        while (x < w * 0.95f) {
            val p = Offset(x, r.top + hh * 0.42f)
            if (azar(7821 + k, j) < 0.55f) {
                val ch = Path().apply { moveTo(p.x, p.y); cubicTo(p.x + 1f * u, p.y + hh * 0.3f, p.x - 1f * u, p.y + hh * 0.5f, p.x + 0.5f * u, p.y + hh * (0.42f + 0.16f * azar(7822 + k, j))) }
                drawPath(ch, c(P.VERDIN).copy(alpha = 0.55f), style = Stroke(2.4f * u, cap = StrokeCap.Round))
            }
            clavoArcano(p, 1.6f * u, am, 0.9f, if (azar(7823 + k, j) < 0.4f) mezcla(P.COBRE, P.VERDIN, 0.55f) else P.COBRE)
            x += w * (0.19f + 0.04f * azar(7824 + k, j)); j++
        }
        drawRect(am.sombra(0.45f), Offset(0f, r.bottom), Size(w, 2f * u))
    }

    // ---------------------------------------------------------------- los costados

    /**
     * Un costado: dos tablones de teca con su junta, los tarugos de dos en dos, los agujeros de los pernos con su cerco de óxido,
     * lo que queda de la pintura del casco y el filete del canto de dentro. El de la izquierda mira a la ventana.
     */
    fun DrawScope.costado(r: Rect, izq: Boolean, g: MarcoArcano) {
        val w = g.w; val u = density
        val am = g.am
        fun c(col: Color, f: Float = 0f) = am.pleno(col, f)
        val x0 = if (izq) floor(r.left) else floor(r.left) - 1f
        val x1 = if (izq) ceil(r.right) + 1f else ceil(r.right)
        drawRect(c(P.TECA, if (izq) 0.04f else -0.06f), Offset(x0, r.top), Size(x1 - x0, r.height))
        vetas(r, c(P.TECA_OSC).copy(alpha = 0.5f), if (izq) 7901 else 7902, densidad = 0.9f, vertical = true, grosor = 0.6f, ondulacion = 0.5f)
        val s = if (izq) 0 else 50
        // Lo que queda de la pintura del casco: el verde de la obra muerta, desconchado.
        val restos = Path()
        for (j in 0 until 12) {
            val p = Offset(r.left + r.width * (0.1f + 0.8f * azar(7910 + s, j)), r.top + r.height * azar(7911 + s, j))
            restos.addPath(irregularArcano(p, r.width * (0.1f + 0.18f * azar(7912 + s, j)), w * (0.012f + 0.03f * azar(7913 + s, j)), 7914 + s + j, 6, 0.6f))
        }
        drawPath(restos, c(P.CASCO).copy(alpha = 0.78f))
        drawPath(restos, c(mezcla(P.CASCO, P.YESO, 0.25f)).copy(alpha = 0.5f), style = Stroke(0.7f * u))
        // La junta de los dos tablones.
        val xj = r.left + r.width * (if (izq) 0.46f else 0.54f)
        drawLine(c(P.TECA_HONDA), Offset(xj, r.top), Offset(xj, r.bottom), 1.3f * u)
        drawLine(c(P.TECA_LUZ, 0.1f).copy(alpha = 0.5f), Offset(xj + 1.3f * u, r.top), Offset(xj + 1.3f * u, r.bottom), 0.7f * u)
        // Los tarugos, de dos en dos, y los agujeros de los pernos con su cerco de óxido.
        var y = r.top + w * 0.06f
        var j = 0
        while (y < r.bottom - w * 0.03f) {
            listOf(0.25f, 0.75f).forEach { f ->
                val p = Offset(r.left + r.width * f, y + w * 0.01f * (f - 0.5f))
                drawCircle(c(P.TECA_OSC, -0.05f), 2.3f * u, p)
                drawCircle(c(P.TECA_LUZ).copy(alpha = 0.5f), 2.3f * u, p, style = Stroke(0.7f * u))
            }
            if (j % 2 == 1) {
                val p = Offset(r.left + r.width * (0.4f + 0.2f * azar(7930 + s, j)), y + w * 0.035f)
                drawCircle(c(P.MINIO).copy(alpha = 0.4f), 3.6f * u, p)
                drawCircle(c(P.TECA_HONDA), 1.8f * u, p)
            }
            y += w * (0.13f + 0.02f * azar(7931 + s, j)); j++
        }
        // El filete del canto de dentro y la luz del canto: el de la izquierda la recibe de la ventana.
        val xf = if (izq) r.right - r.width * 0.12f else r.left
        drawRect(c(P.TECA_LUZ, if (izq) 0.1f else -0.05f), Offset(xf, r.top), Size(r.width * 0.12f, r.height))
        drawLine(am.sombra(0.45f), Offset(if (izq) xf else xf + r.width * 0.12f, r.top), Offset(if (izq) xf else xf + r.width * 0.12f, r.bottom), 1f * u)
        if (izq) drawRect(am.claro(0.18f), Offset(r.left, r.top), Size(1.4f * u, r.height))
        else drawRect(am.sombra(0.3f), Offset(r.right - r.width * 0.25f, r.top), Size(r.width * 0.25f, r.height))
    }

    /**
     * Las curvas de madera: en cada esquina de arriba de un casillero, la escuadra de los barcos (un triángulo de lado cóncavo) con
     * su perno de bronce, que deja su sombra en el paño.
     */
    fun DrawScope.curvas(d: DisposicionEstante, g: MarcoArcano) {
        val w = g.w; val u = density
        val am = g.am
        val e = w * 0.022f
        val t = w * 0.036f
        d.repisas.forEach { r ->
            listOf(true, false).forEach { izq ->
                val x = if (izq) r.left + e else r.right - e
                val s = if (izq) 1f else -1f
                val y = r.top + w * 0.012f
                val curva = Path().apply {
                    moveTo(x, y); lineTo(x + s * t, y)
                    quadraticTo(x + s * t * 0.3f, y + t * 0.3f, x, y + t)
                    close()
                }
                drawPath(Path().apply { addPath(curva, Offset(s * 2f * u + 1.5f * u, 2.5f * u)) }, am.sombra(0.35f))
                drawPath(curva, am.pleno(P.TECA, if (izq) -0.04f else 0.06f))
                drawLine(am.pleno(P.TECA_LUZ, 0.1f), Offset(x + s * t, y + 0.6f * u), Offset(x + s * 0.5f * u, y + t * 0.96f), 0.8f * u)
                clavoArcano(Offset(x + s * t * 0.3f, y + t * 0.3f), 1.4f * u, am, 0.9f, P.BRONCE)
            }
        }
    }

    // ---------------------------------------------------------------- la luz del cuerpo

    /** La noche (y la sombra del día) del cuerpo: un mapa de luz estirado sobre los casilleros, las tablas y los costados. */
    fun DrawScope.luzCuerpo(d: DisposicionEstante, g: MarcoArcano, velas: FloatArray) {
        val w = g.w
        val abajo = d.tablas.lastOrNull()?.bottom ?: return
        val tablas = d.tablas.map { (it.top / w) to (it.bottom / w) }
        val xa = g.x0 / w + 0.024f; val xb = g.x1 / w - 0.024f
        val clave = "c:${w.toInt()}:${g.ch}:$abajo:${g.a.oscuro}:${g.a.noche}:${velas.joinToString(",")}"
        with(MapaLuzArcano) {
            pintar(1, clave, g.ch, abajo, 96) { x, y ->
                val madera = x < xa || x > xb || tablas.any { (t0, t1) -> y >= t0 - 0.003f && y <= t1 }
                val p = Offset(x * w, y * w)
                mascaraArcano(if (madera) g.am else g.af, x, y, g.cerca(p), g.frio(p))
            }
        }
    }

    // ---------------------------------------------------------------- el zócalo

    /**
     * El zócalo, desde la última tabla: la moldura de arriba, los tableros de los lados y en medio el cajón clavado con dos listones,
     * su anilla de bronce y la mancha oscura que baja de él; abajo, el rodapié comido por la humedad con su salitre.
     */
    fun DrawScope.zocalo(g: MarcoArcano, arriba: Float) {
        val w = g.w; val u = density
        val am = g.am
        fun c(col: Color, f: Float = 0f) = am.pleno(col, f)
        val y0 = g.base; val y1 = g.suelo
        val z = y1 - y0
        fun y(f: Float) = y0 + z * f
        drawRect(c(P.TECA), Offset(0f, arriba), Size(w, y1 - arriba + 1f))
        // La moldura de arriba: el canto que vuela, con su luz.
        drawRect(c(P.TECA_LUZ, 0.06f), Offset(0f, y(0f)), Size(w, z * 0.09f))
        drawRect(c(P.TECA_OSC), Offset(0f, y(0.09f)), Size(w, z * 0.06f))
        drawRect(am.sombra(0.45f), Offset(0f, y(0.15f)), Size(w, z * 0.05f))
        vetas(Rect(0f, y(0.2f), w, y(0.84f)), c(P.TECA_OSC).copy(alpha = 0.45f), 7950, densidad = 0.7f, grosor = 0.6f, ondulacion = 0.5f)
        // Los tableros de los lados, rehundidos.
        listOf(Rect(w * 0.035f, y(0.27f), w * 0.3f, y(0.76f)), Rect(w * 0.7f, y(0.27f), w * 0.965f, y(0.76f))).forEach { t ->
            drawRect(c(P.TECA, -0.08f), t.topLeft, t.size)
            drawRect(am.sombra(0.4f), t.topLeft, Size(t.width, 2f * u))
            drawRect(am.sombra(0.3f), t.topLeft, Size(2f * u, t.height))
            drawRect(c(P.TECA_LUZ, 0.1f), Offset(t.left, t.bottom - 1.4f * u), Size(t.width, 1.4f * u))
            drawRect(c(P.TECA_LUZ, 0.05f), Offset(t.right - 1.4f * u, t.top), Size(1.4f * u, t.height))
        }
        // El rodapié: más oscuro, comido por la humedad, con su raya de salitre.
        drawRect(c(P.TECA_OSC, -0.06f), Offset(0f, y(0.84f)), Size(w, z * 0.16f + 1f))
        drawRect(c(P.TECA_LUZ).copy(alpha = 0.5f), Offset(0f, y(0.84f)), Size(w, 1.2f * u))
        drawLine(c(P.YESO).copy(alpha = 0.4f), Offset(0f, y(0.93f)), Offset(w, y(0.91f)), 1.3f * u)
        for (j in 0 until 6) {
            val x = w * (0.05f + 0.18f * j + 0.06f * azar(7970, j))
            drawPath(irregularArcano(Offset(x, y(0.97f)), w * (0.02f + 0.02f * azar(7971, j)), z * 0.05f, 7972 + j, 8, 0.5f), c(P.YESO).copy(alpha = 0.3f))
        }
        // El cajón de en medio: la holgura oscura alrededor, el frente con su tablero en relieve y la anilla de bronce.
        val cj = Rect(w * 0.355f, y(0.2f), w * 0.645f, y(0.81f))
        drawRect(am.sombra(0.75f), Offset(cj.left - 2f * u, cj.top - 2f * u), Size(cj.width + 4f * u, cj.height + 4f * u))
        drawRect(c(mezcla(P.TECA, P.TECA_LUZ, 0.35f)), cj.topLeft, cj.size)
        drawRect(c(P.TECA_LUZ, 0.18f), cj.topLeft, Size(cj.width, 1.4f * u))
        drawRect(am.sombra(0.3f), Offset(cj.right - 2f * u, cj.top), Size(2f * u, cj.height))
        val tab = Rect(cj.left + w * 0.022f, cj.top + z * 0.08f, cj.right - w * 0.022f, cj.bottom - z * 0.08f)
        drawRect(c(mezcla(P.TECA, P.TECA_LUZ, 0.5f), 0.05f), tab.topLeft, tab.size)
        drawRect(c(P.TECA_LUZ, 0.15f), tab.topLeft, Size(tab.width, 1.2f * u))
        drawRect(c(P.TECA_LUZ, 0.1f), tab.topLeft, Size(1.2f * u, tab.height))
        drawRect(am.sombra(0.4f), Offset(tab.left, tab.bottom - 1.4f * u), Size(tab.width, 1.4f * u))
        drawRect(am.sombra(0.3f), Offset(tab.right - 1.4f * u, tab.top), Size(1.4f * u, tab.height))
        vetas(tab, c(P.TECA_OSC).copy(alpha = 0.35f), 7958, densidad = 0.5f, grosor = 0.6f, ondulacion = 0.4f)
        // La mancha oscura que sale por la holgura de abajo y baja por el rodapié hasta el borde, donde gotea.
        val mancha = Path().apply {
            moveTo(cj.left + cj.width * 0.36f, cj.bottom)
            cubicTo(cj.left + cj.width * 0.38f, y(0.86f), cj.left + cj.width * 0.46f, y(0.92f), cj.left + cj.width * 0.47f, y1)
            lineTo(cj.left + cj.width * 0.55f, y1)
            cubicTo(cj.left + cj.width * 0.56f, y(0.9f), cj.left + cj.width * 0.64f, y(0.86f), cj.left + cj.width * 0.66f, cj.bottom)
            close()
        }
        drawPath(mancha, am.sombra(0.55f))
        drawPath(mancha, c(mezcla(P.TECA_HONDA, P.SALOBRE, 0.3f)).copy(alpha = 0.6f), style = Stroke(1.2f * u))
        drawRect(c(P.TECA_HONDA).copy(alpha = 0.75f), Offset(cj.left + cj.width * 0.3f, cj.bottom - 1f * u), Size(cj.width * 0.42f, 2.8f * u))
        val anilla = Offset(cj.center.x, cj.top + cj.height * 0.36f)
        drawArc(am.sombra(0.45f), 15f, 150f, false, Offset(anilla.x - 7f * u + 1.4f * u, anilla.y - 1f * u + 2f * u), Size(14f * u, 15f * u), style = Stroke(2.2f * u))
        drawArc(c(P.BRONCE), 15f, 150f, false, Offset(anilla.x - 7f * u, anilla.y - 1f * u), Size(14f * u, 15f * u), style = Stroke(2.2f * u))
        drawArc(am.brillo(0.5f, 0.9f), 120f, 40f, false, Offset(anilla.x - 7f * u, anilla.y - 1f * u), Size(14f * u, 15f * u), style = Stroke(0.8f * u))
        clavoArcano(anilla, 3f * u, am, 0.9f, P.BRONCE)
        // Los dos listones clavados de través, toscos, de punta astillada, con sus clavos de hierro y la chorrera de óxido.
        listOf(0.04f, 0.66f).forEachIndexed { i, f ->
            val ly = cj.top + cj.height * f
            val lh = cj.height * 0.24f
            val x0 = cj.left - w * 0.045f; val x1 = cj.right + w * 0.045f
            val l = Rect(x0, ly, x1, ly + lh)
            val liston = poligonoArcano(x0 + w * 0.006f, ly, x1, ly + lh * 0.1f, x1 - w * 0.008f, ly + lh * 0.55f, x1 - w * 0.002f, ly + lh, x0, ly + lh * 0.92f, x0 + w * 0.009f, ly + lh * 0.45f)
            rotate(if (i == 0) -4.5f else -1.6f, l.center) {
                drawPath(Path().apply { addPath(liston, Offset(2.4f * u, 3f * u)) }, am.sombra(0.5f))
                drawPath(liston, c(P.TECA_GRIS, 0.08f))
                clipPath(liston) {
                    drawRect(c(P.TECA_GRIS, 0.18f), l.topLeft, Size(l.width, 1.4f * u))
                    drawRect(am.sombra(0.32f), Offset(l.left, l.bottom - 1.8f * u), Size(l.width, 1.8f * u))
                    vetas(l, c(P.TECA_OSC).copy(alpha = 0.35f), 7960 + i, densidad = 0.6f, grosor = 0.5f, ondulacion = 0.3f)
                }
                listOf(0.05f, 0.09f, 0.5f, 0.91f, 0.95f).forEachIndexed { q, fx ->
                    val p = Offset(l.left + l.width * fx, l.top + lh * (if (q % 2 == 0) 0.38f else 0.66f))
                    drawLine(c(P.OXIDO).copy(alpha = 0.45f), p, p + Offset(0.5f * u, lh * (1f + 0.7f * azar(7962 + i, q))), 2f * u, StrokeCap.Round)
                    clavoArcano(p, 1.8f * u, am, 0.9f, P.HIERRO_LUZ)
                }
            }
        }
    }

    // ---------------------------------------------------------------- el rótulo del mes

    /** El rótulo del mes: una tira de papel manila clavada al canto con dos tachuelas de cobre, ondulada por la humedad. */
    fun DrawScope.placa(p: PlacaEstante, medidor: TextMeasurer, g: MarcoArcano) {
        val u = density
        val am = g.am
        val k = g.cerca(p.r.center)
        val r = p.r
        rotate((azar(p.texto.hashCode(), 3) - 0.5f) * 1.6f, r.center) {
            drawRect(am.sombra(0.4f), Offset(r.left + 1.6f * u, r.top + 1.8f * u), r.size)
            val papel = am.etiqueta(P.MANILA, k)
            drawRect(papel, r.topLeft, r.size)
            drawRect(am.etiqueta(P.PAPEL_OSC, k).copy(alpha = 0.55f), Offset(r.left, r.bottom - r.height * 0.18f), Size(r.width, r.height * 0.18f))
            drawPath(irregularArcano(Offset(r.right - r.width * 0.15f, r.center.y), r.width * 0.12f, r.height * 0.45f, p.texto.hashCode(), 10, 0.3f), am.etiqueta(P.PAPEL_OSC, k).copy(alpha = 0.35f))
            // La esquina levantada por la humedad.
            drawPath(poligonoArcano(r.right - 6f * u, r.top, r.right, r.top, r.right, r.top + 5f * u), am.sombra(0.35f))
            drawPath(poligonoArcano(r.right - 6f * u, r.top, r.right, r.top + 5f * u, r.right - 5f * u, r.top + 4f * u), am.etiqueta(mezcla(P.MANILA, Color.White, 0.2f), k))
            listOf(r.left + 4f * u, r.right - 8f * u).forEach { x -> clavoArcano(Offset(x, r.center.y), 1.5f * u, am, k, P.COBRE) }
            val tinta = if (am.oscuro) Color(0xFF14100A) else P.TINTA
            textoEn(medidor, p.texto, Rect(r.left + 8f * u, r.top, r.right - 11f * u, r.bottom), M.LETRA, tinta, 10f, peso = FontWeight.SemiBold, minimo = 5f)
        }
    }
}
