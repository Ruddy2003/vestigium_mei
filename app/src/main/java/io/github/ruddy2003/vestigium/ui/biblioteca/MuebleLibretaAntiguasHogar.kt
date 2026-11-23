package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * El hogar del rincón de la libreta de Tierras Antiguas ([MuebleLibretaAntiguas]), a la derecha y cortado por el borde: la
 * campana encalada y tiznada, con su cenefa ocre de rombos; la repisa de roble con dos platos vidriados; el dintel de dovelas y la
 * jamba de sillares de arenisca; la boca negra de hollín con su placa de hierro; el llar con el caldero; el fuelle colgado de la
 * jamba; la solera con la ceniza, los morillos, los troncos y el fuego. De día el fuego arde bajo y sube el humo; de noche es la
 * luz del rincón.
 */
internal object HogarLibretaAntiguas {
    private val P = PaletaAntiguas
    private val ARENISCA = Color(0xFFB39C78)
    private val ARENISCA_LUZ = Color(0xFFD8C4A0)
    private val ARENISCA_OSC = Color(0xFF6E5C44)
    private val HOLLIN = Color(0xFF1A120D)
    private val VIDRIADO_VERDE = Color(0xFF3E7A4A)
    private val VIDRIADO_OCRE = Color(0xFFC08A3A)
    private val CORTEZA = Color(0xFF4A3424)

    fun DrawScope.hogar(s: SitioLibretaAntiguas, L: LuzLibretaAntiguas) {
        val w = s.w; val h = s.h; val u = density
        val am = L.am
        val xj = s.xJamba
        // ---- la campana: enlucida y pintada del azul de la sala, tiznada arriba y en medio; su cara del costado y la cenefa ocre
        val izqArriba = xj + h * 0.12f
        val izqAbajo = xj - h * 0.035f
        val campana = poligonoAntiguas(izqArriba, 0f, w, 0f, w, s.repisa, izqAbajo, s.repisa)
        val costado = poligonoAntiguas(izqArriba - h * 0.022f, 0f, izqArriba, 0f, izqAbajo, s.repisa, izqAbajo - h * 0.022f, s.repisa)
        fun pc(c: Color) = colorParedAntiguas(L.a, c)
        drawPath(costado, pc(if (L.oscuro) P.LAPIS else mezcla(P.LAPIS_LUZ, Color.White, 0.12f)))
        drawPath(campana, pc(mezcla(P.LAPIS, P.LAPIS_OSC, 0.25f)))
        clipPath(campana) {
            pinceladas(Rect(izqAbajo, 0f, w, s.repisa), pc(P.LAPIS_LUZ), 7701, 24, u, 0.14f)
            pinceladas(Rect(izqAbajo, 0f, w, s.repisa), pc(P.LAPIS_HONDO), 7702, 20, u, 0.16f)
            // El tizne: sube de la boca y se abre arriba.
            val cx = (s.boca.left + w) / 2
            drawRect(Brush.verticalGradient(listOf(HOLLIN.copy(alpha = 0.6f), HOLLIN.copy(alpha = 0.26f), HOLLIN.copy(alpha = 0.06f)), 0f, s.repisa), Offset(izqAbajo, 0f), Size(w - izqAbajo, s.repisa))
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, HOLLIN.copy(alpha = 0.22f), Color.Transparent), izqAbajo, w + h * 0.1f), Offset(izqAbajo, 0f), Size(w - izqAbajo, s.repisa))
            // La sombra de la viga en la campana, y la cenefa ocre de rombos sobre la repisa.
            drawRect(am.sombra(0.3f), Offset(izqAbajo, 0f), Size(w, h * 0.012f))
            val c0 = s.repisa - h * 0.045f
            drawRect(L.cosa(P.OCRE, Offset(cx, c0)), Offset(izqAbajo, c0), Size(w - izqAbajo, h * 0.028f))
            var x = izqAbajo + h * 0.03f
            while (x < w) { drawPath(romboLibretaAntiguas(Offset(x, c0 + h * 0.014f), h * 0.009f, h * 0.011f), L.cosa(P.RUBIA, Offset(x, c0))); x += h * 0.04f }
            drawRect(L.cosa(P.RUBIA, Offset(cx, c0)), Offset(izqAbajo, c0 - h * 0.004f), Size(w - izqAbajo, h * 0.004f))
            with(L) { charco(Rect(izqAbajo, 0f, w, s.repisa), a.hondo, P.CALIDO_PARED) }
        }
        clipPath(costado) { with(L) { charco(Rect(izqAbajo - h * 0.03f, 0f, izqArriba, s.repisa), a.hondo, P.CALIDO_PARED) } }
        drawLine(am.sombra(0.35f), Offset(izqArriba, 0f), Offset(izqAbajo, s.repisa), 1f * u)
        // ---- los platos vidriados, de pie en la repisa contra la campana
        platos(s, L)
        // ---- la repisa de roble
        val rp = Rect(izqAbajo - h * 0.012f, s.repisa - h * 0.012f, w, s.repisa + h * 0.012f)
        drawRect(L.cosa(P.ROBLE, rp.center), rp.topLeft, rp.size)
        drawRect(if (L.oscuro) am.filo(0.25f * L.k(rp.center)) else am.claro(0.45f), rp.topLeft, Size(rp.width, 1.2f * u))
        drawRect(L.cosa(P.ROBLE_LUZ, rp.center).copy(alpha = 0.6f), Offset(rp.left, rp.top + rp.height * 0.3f), Size(rp.width, 1f * u))
        drawRect(am.sombra(0.4f), Offset(rp.left, rp.bottom - 1.4f * u), Size(rp.width, 1.4f * u))
        // ---- el dintel de dovelas
        val dt = Rect(xj - h * 0.012f, s.dintel0, w, s.dintel1)
        drawRect(L.cosa(ARENISCA, dt.center), dt.topLeft, dt.size)
        clipRect(dt.left, dt.top, dt.right, dt.bottom) {
            pinceladas(dt, L.cosa(ARENISCA_OSC, dt.center), 7710, 16, u, 0.16f)
            if (!L.oscuro) drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.16f), Color.Transparent), dt.left, dt.left + h * 0.3f), dt.topLeft, dt.size)
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, HOLLIN.copy(alpha = 0.55f)), dt.top + dt.height * 0.4f, dt.bottom), dt.topLeft, dt.size)
            // Las juntas de las dovelas, que se abren hacia abajo.
            var x = dt.left + h * 0.12f
            var j = 0
            while (x < w) {
                val inc = (x - (dt.left + (w - dt.left) / 2)) * 0.08f
                drawLine(am.sombra(0.5f), Offset(x - inc, dt.top), Offset(x + inc, dt.bottom), 1.2f * u)
                drawLine(L.cosa(ARENISCA_LUZ, dt.center).copy(alpha = 0.5f), Offset(x - inc + 1.2f * u, dt.top), Offset(x + inc + 1.2f * u, dt.bottom), 0.6f * u)
                x += h * (0.11f + 0.03f * azar(7711, j)); j++
            }
            if (L.oscuro) drawRect(Brush.verticalGradient(listOf(Color.Transparent, P.LLAMA.copy(alpha = 0.35f)), dt.top, dt.bottom), dt.topLeft, dt.size)
        }
        drawRect(if (L.oscuro) Color.Transparent else am.claro(0.4f), dt.topLeft, Size(dt.width, 1.2f * u))
        drawRect(am.sombra(0.5f), Offset(dt.left, dt.top), Size(1.2f * u, dt.height))
        // ---- la boca: el hollín, el costado de dentro, la placa de hierro
        val b = s.boca
        drawRect(L.cosa(HOLLIN, b.center), b.topLeft, b.size)
        if (!L.oscuro) drawRect(Brush.radialGradient(listOf(Color(0xFF5A4636).copy(alpha = 0.7f), Color.Transparent), s.fuego, h * 0.3f), b.topLeft, b.size)
        // La placa de hierro del fondo: baja, con su reborde y un rombo en relieve; casi se pierde en el hollín.
        val placa = Rect(s.fuego.x - h * 0.1f, h * 0.66f, s.fuego.x + h * 0.1f, b.bottom)
        drawRect(L.cosa(Color(0xFF221E20), placa.center), placa.topLeft, placa.size)
        drawRect(L.cosa(Color(0xFF3A3436), placa.center), placa.topLeft, placa.size, style = Stroke(1.6f * u))
        drawPath(romboLibretaAntiguas(Offset(placa.center.x, placa.top + h * 0.05f), h * 0.03f, h * 0.035f), L.cosa(Color(0xFF332D2F), placa.center))
        val dentro = poligonoAntiguas(b.left, b.top, b.left + h * 0.045f, b.top + h * 0.035f, b.left + h * 0.045f, b.bottom - h * 0.012f, b.left, b.bottom)
        drawPath(dentro, L.cosa(mezcla(ARENISCA_OSC, HOLLIN, 0.6f), b.center))
        if (L.oscuro) drawPath(dentro, P.LLAMA.copy(alpha = 0.3f))
        // ---- el llar y el caldero
        llar(s, L)
        // ---- la jamba de sillares
        jamba(s, L)
        // ---- la solera: la losa, su canto y la ceniza
        val so = Rect(xj - h * 0.02f, b.bottom, w, s.suelo + h * 0.004f)
        drawRect(L.cosa(ARENISCA, so.center), so.topLeft, so.size)
        drawRect(if (L.oscuro) am.filo(0.5f) else am.claro(0.45f), so.topLeft, Size(so.width, 1.4f * u))
        drawRect(am.sombra(0.35f), Offset(so.left, so.bottom - h * 0.008f), Size(so.width, h * 0.008f))
        drawLine(am.sombra(0.45f), Offset(so.left + h * 0.16f, so.top), Offset(so.left + h * 0.16f, so.bottom), 1f * u)
        drawOval(L.cosa(Color(0xFF8A8278), s.fuego), Offset(s.fuego.x - h * 0.17f, b.bottom - h * 0.018f), Size(h * 0.34f, h * 0.03f))
        with(L) { charco(Rect(so.left, so.top, w, so.bottom), am.hondo, Color(0xFFE0904A)) }
        // ---- el fuelle colgado de la jamba
        fuelle(Offset(xj + s.jamba * 0.42f, h * 0.505f), s, L)
    }

    /** La jamba de sillares de arenisca: las hiladas con sus juntas, la cara a la luz y el costado de dentro, que mira al fuego. */
    private fun DrawScope.jamba(s: SitioLibretaAntiguas, L: LuzLibretaAntiguas) {
        val h = s.h; val u = density
        val am = L.am
        val r = Rect(s.xJamba, s.dintel1, s.xJamba + s.jamba, s.boca.bottom)
        val dentro = h * 0.016f
        drawRect(am.sombra(0.4f), Offset(r.left - h * 0.02f, r.top), Size(h * 0.02f, r.height))
        drawRect(L.cosa(ARENISCA, Offset(r.left, r.center.y)), r.topLeft, Size(r.width - dentro, r.height))
        // Las hiladas, una alta y una baja.
        var y = r.top
        var j = 0
        while (y < r.bottom) {
            val al = h * (if (j % 2 == 0) 0.11f else 0.085f)
            val y1 = minOf(r.bottom, y + al)
            val c = Offset(r.center.x, (y + y1) / 2)
            val tono = if (j % 2 == 0) ARENISCA else mezcla(ARENISCA, ARENISCA_OSC, 0.18f)
            drawRect(L.cosa(tono, c), Offset(r.left, y), Size(r.width - dentro, y1 - y))
            pinceladas(Rect(r.left, y, r.right - dentro, y1), L.cosa(ARENISCA_OSC, c), 7720 + j, 5, u, 0.14f)
            drawRect(if (L.oscuro) am.filo(0.2f * L.k(c)) else am.claro(0.35f), Offset(r.left, y), Size(r.width - dentro, 1.2f * u))
            drawRect(am.sombra(0.45f), Offset(r.left, y1 - 1.4f * u), Size(r.width - dentro, 1.4f * u))
            y = y1; j++
        }
        if (!L.oscuro) drawRect(Color.White.copy(alpha = 0.14f), Offset(r.left, r.top), Size(h * 0.012f, r.height))
        // El costado de dentro, que mira al fuego: de día tiznado y en sombra; de noche, encendido.
        drawRect(L.cosa(mezcla(ARENISCA_OSC, HOLLIN, 0.3f), Offset(r.right, r.center.y)), Offset(r.right - dentro, r.top), Size(dentro, r.height))
        if (L.oscuro) drawRect(Brush.verticalGradient(listOf(P.LLAMA.copy(alpha = 0.1f), P.LLAMA.copy(alpha = 0.55f)), r.top, r.bottom), Offset(r.right - dentro, r.top), Size(dentro, r.height))
        drawRect(Brush.verticalGradient(listOf(HOLLIN.copy(alpha = 0.5f), Color.Transparent), r.top, r.top + h * 0.12f), Offset(r.left + r.width * 0.4f, r.top), Size(r.width * 0.6f, h * 0.12f))
    }

    /** Dos platos de barro vidriado de pie en la repisa, apoyados en la campana: uno verde con el borde ocre y uno ocre con el borde verde. */
    private fun DrawScope.platos(s: SitioLibretaAntiguas, L: LuzLibretaAntiguas) {
        val h = s.h; val u = density
        val am = L.am
        listOf(Triple(0.075f, 0.052f, true), Triple(0.19f, 0.04f, false)).forEach { (dx, rr, verde) ->
            val c = Offset(s.xJamba + h * dx, s.repisa - h * 0.012f - h * rr * 0.98f)
            if (c.x + rr * h > s.w + h * 0.02f) return@forEach
            val r = h * rr
            val fondo = if (verde) VIDRIADO_VERDE else VIDRIADO_OCRE
            val borde = if (verde) VIDRIADO_OCRE else VIDRIADO_VERDE
            drawCircle(am.sombra(0.35f), r, c + Offset(if (L.oscuro) -h * 0.01f else h * 0.012f, h * 0.004f))
            drawCircle(L.cosa(borde, c), r, c)
            drawCircle(L.cosa(fondo, c), r * 0.72f, c)
            drawCircle(L.cosa(mezcla(fondo, P.ROBLE_OSC, 0.35f), c), r * 0.72f, c, style = Stroke(1.2f * u))
            drawPath(romboLibretaAntiguas(c, r * 0.3f, r * 0.42f), L.cosa(P.CRUDO, c))
            drawPath(romboLibretaAntiguas(c, r * 0.13f, r * 0.19f), L.cosa(borde, c))
            // El brillo del vidriado: de día arriba a la izquierda; de noche, abajo, el del fuego.
            if (L.oscuro) drawArc(P.FUEGO.copy(alpha = (0.8f * L.k(c)).coerceAtMost(0.7f)), 30f, 110f, false, Offset(c.x - r * 0.86f, c.y - r * 0.86f), Size(r * 1.72f, r * 1.72f), style = Stroke(1.4f * u))
            else drawArc(Color.White.copy(alpha = 0.6f), 200f, 60f, false, Offset(c.x - r * 0.84f, c.y - r * 0.84f), Size(r * 1.68f, r * 1.68f), style = Stroke(1.6f * u))
        }
    }

    /** El llar: la cadena que baja de dentro de la campana, el gancho y el caldero de hierro con su asa. */
    private fun DrawScope.llar(s: SitioLibretaAntiguas, L: LuzLibretaAntiguas) {
        val h = s.h; val u = density
        val am = L.am
        val x = s.fuego.x
        val gancho = h * 0.58f
        val hierro = L.cosa(P.HIERRO, Offset(x, h * 0.5f))
        var y = s.dintel1 - h * 0.01f
        var j = 0
        while (y < gancho - h * 0.02f) {
            if (j % 2 == 0) drawOval(hierro, Offset(x - h * 0.007f, y), Size(h * 0.014f, h * 0.026f), style = Stroke(1.4f * u))
            else drawLine(hierro, Offset(x, y), Offset(x, y + h * 0.026f), 2.6f * u)
            y += h * 0.022f; j++
        }
        drawPath(Path().apply { moveTo(x, y); quadraticTo(x + h * 0.02f, y + h * 0.02f, x, gancho + h * 0.01f) }, hierro, style = Stroke(1.8f * u))
        // El caldero.
        val c = Offset(x, h * 0.665f)
        val an = h * 0.075f; val al = h * 0.07f
        drawArc(hierro, 200f, 140f, false, Offset(c.x - an * 0.95f, gancho), Size(an * 1.9f, (c.y - al * 0.45f - gancho) * 2f), style = Stroke(1.6f * u))
        val cuerpo = Path().apply {
            moveTo(c.x - an, c.y - al * 0.45f)
            cubicTo(c.x - an * 1.1f, c.y + al * 0.3f, c.x - an * 0.6f, c.y + al * 0.55f, c.x, c.y + al * 0.55f)
            cubicTo(c.x + an * 0.6f, c.y + al * 0.55f, c.x + an * 1.1f, c.y + al * 0.3f, c.x + an, c.y - al * 0.45f)
            close()
        }
        drawPath(cuerpo, L.cosa(Color(0xFF242124), c))
        clipPath(cuerpo) {
            if (L.oscuro) drawRect(Brush.verticalGradient(listOf(Color.Transparent, P.LLAMA.copy(alpha = 0.55f)), c.y, c.y + al * 0.55f), Offset(c.x - an * 1.2f, c.y - al), Size(an * 2.4f, al * 1.6f))
            else {
                drawOval(Color.White.copy(alpha = 0.22f), Offset(c.x - an * 0.8f, c.y - al * 0.35f), Size(an * 0.42f, al * 0.55f))
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, P.LLAMA.copy(alpha = 0.35f)), c.y, c.y + al * 0.55f), Offset(c.x - an * 1.2f, c.y - al), Size(an * 2.4f, al * 1.6f))
            }
        }
        drawOval(L.cosa(Color(0xFF5A565A), c), Offset(c.x - an * 1.06f, c.y - al * 0.55f), Size(an * 2.12f, al * 0.22f))
        drawArc(if (L.oscuro) P.FUEGO.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.45f), 160f, 60f, false, Offset(c.x - an * 1.06f, c.y - al * 0.55f), Size(an * 2.12f, al * 0.22f), style = Stroke(1f * density))
        drawOval(L.cosa(Color(0xFF141214), c), Offset(c.x - an * 0.9f, c.y - al * 0.52f), Size(an * 1.8f, al * 0.14f))
        listOf(-1f, 1f).forEach { sg -> drawCircle(hierro, h * 0.006f, Offset(c.x + sg * an * 1.02f, c.y - al * 0.42f)) }
    }

    /** El fuelle colgado de un clavo en la jamba: la tabla de nogal con su rombo, el cuero plegado con sus tachuelas y la boquilla de latón. */
    private fun DrawScope.fuelle(clavo: Offset, s: SitioLibretaAntiguas, L: LuzLibretaAntiguas) {
        val h = s.h; val u = density
        val am = L.am
        val c = clavo + Offset(-h * 0.004f, h * 0.11f)
        val an = h * 0.064f; val al = h * 0.15f
        val tabla = Path().apply {
            moveTo(c.x - an * 0.18f, c.y - al * 0.62f)
            lineTo(c.x + an * 0.18f, c.y - al * 0.62f)
            lineTo(c.x + an * 0.2f, c.y - al * 0.45f)
            cubicTo(c.x + an * 0.75f, c.y - al * 0.4f, c.x + an * 0.72f, c.y + al * 0.15f, c.x + an * 0.2f, c.y + al * 0.35f)
            lineTo(c.x - an * 0.2f, c.y + al * 0.35f)
            cubicTo(c.x - an * 0.72f, c.y + al * 0.15f, c.x - an * 0.75f, c.y - al * 0.4f, c.x - an * 0.2f, c.y - al * 0.45f)
            close()
        }
        // La sombra en la piedra.
        drawPath(Path().apply { addPath(tabla, Offset(if (L.oscuro) -h * 0.012f else h * 0.012f, h * 0.008f)) }, am.sombra(0.4f))
        // La tabla de detrás, corrida, con su asa; entre las dos, el cuero plegado en abanico desde la boquilla.
        drawPath(Path().apply { addPath(tabla, Offset(an * 0.26f, -al * 0.03f)) }, L.cosa(Color(0xFF452814), c))
        val cuero = L.cosa(P.CUERO, c)
        val fuelleCuero = poligonoAntiguas(c.x + an * 0.2f, c.y - al * 0.45f, c.x + an * 0.46f, c.y - al * 0.48f, c.x + an * 0.99f, c.y - al * 0.12f,
            c.x + an * 0.46f, c.y + al * 0.32f, c.x + an * 0.2f, c.y + al * 0.35f, c.x + an * 0.73f, c.y - al * 0.1f)
        drawPath(fuelleCuero, cuero)
        val bisagra = Offset(c.x + an * 0.32f, c.y + al * 0.34f)
        for (j in 0 until 4) {
            val f = 0.15f + 0.22f * j
            val borde = Offset(c.x + an * (0.46f + 0.5f * kotlin.math.sin(f * 3.1f)), c.y - al * 0.48f + al * 0.8f * f)
            drawLine(am.sombra(0.45f), bisagra, borde, 0.9f * u)
            drawLine(if (L.oscuro) am.filo(0.3f * L.k(c)) else am.claro(0.3f), bisagra + Offset(0f, -1.2f * u), borde + Offset(0f, -1.2f * u), 0.6f * u)
        }
        drawPath(tabla, L.cosa(Color(0xFF5E3A22), c))
        clipPath(tabla) {
            drawRect(if (L.oscuro) am.filo(0.3f * L.k(c)) else am.claro(0.25f), Offset(c.x - an, c.y - al), Size(an * 0.5f, al * 2f))
            drawRect(am.sombra(0.3f), Offset(c.x + an * 0.25f, c.y - al), Size(an, al * 2f))
        }
        drawPath(romboLibretaAntiguas(Offset(c.x, c.y - al * 0.05f), an * 0.18f, an * 0.26f), L.cosa(Color(0xFF3A2414), c))
        drawPath(tabla, L.cosa(P.CUERO_OSC, c), style = Stroke(0.8f * u))
        // Las tachuelas del canto.
        for (j in 0 until 5) {
            val ang = -0.9f + 0.45f * j
            drawCircle(L.cosa(P.LATON, c), 0.9f * u, Offset(c.x + kotlin.math.cos(ang) * an * 0.55f, c.y - al * 0.08f + kotlin.math.sin(ang) * al * 0.32f))
        }
        // La boquilla de latón, abajo, y el agujero del asa, arriba, en el clavo.
        drawPath(poligonoAntiguas(c.x - an * 0.13f, c.y + al * 0.35f, c.x + an * 0.13f, c.y + al * 0.35f, c.x + an * 0.04f, c.y + al * 0.62f, c.x - an * 0.04f, c.y + al * 0.62f), L.cosa(P.LATON, c))
        drawLine(if (L.oscuro) am.filo(0.6f * L.k(c)) else am.claro(0.6f), Offset(c.x - an * 0.09f, c.y + al * 0.37f), Offset(c.x - an * 0.03f, c.y + al * 0.6f), 0.7f * u)
        drawCircle(L.cosa(HOLLIN, c), an * 0.07f, Offset(c.x, c.y - al * 0.55f))
        drawCircle(L.cosa(P.HIERRO, clavo), 1.6f * u, clavo)
    }

    /**
     * El fuego, encima de todo lo del hogar: los morillos de hierro, los troncos cruzados con su corteza y sus cortes, las brasas y
     * las lenguas de la llama (bajas de día, con el humo que sube a la campana; altas de noche, con el resplandor que encharca la
     * solera y la boca).
     */
    fun DrawScope.fuego(s: SitioLibretaAntiguas, L: LuzLibretaAntiguas) {
        val h = s.h; val u = density
        val am = L.am
        val f = s.fuego
        val noche = L.oscuro
        val piso = s.boca.bottom
        clipRect(s.boca.left, s.dintel1, s.w, s.suelo + h * 0.004f) {
            // El resplandor en la boca y en la solera (de noche).
            if (noche) {
                drawCircle(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.55f), P.BRASA.copy(alpha = 0.2f), Color.Transparent), f, h * 0.32f), h * 0.32f, f, blendMode = BlendMode.Screen)
            }
            // Las brasas y la ceniza.
            drawOval(L.cosa(Color(0xFF3A302A), f), Offset(f.x - h * 0.13f, piso - h * 0.04f), Size(h * 0.26f, h * 0.05f))
            for (j in 0 until 14) {
                val p = Offset(f.x + h * (0.22f * azar(7730, j) - 0.11f), piso - h * (0.012f + 0.02f * azar(7731, j)))
                drawCircle((if (j % 3 == 0) P.FUEGO else P.BRASA).copy(alpha = if (noche) 0.95f else 0.75f), (0.8f + 1.2f * azar(7732, j)) * u, p)
            }
            // Los morillos: el poste de delante con su bola y el pie.
            listOf(-0.13f, 0.13f).forEach { dx ->
                val x = f.x + h * dx
                val hierro = L.cosa(P.HIERRO, Offset(x, piso))
                drawLine(hierro, Offset(x, piso), Offset(x, piso - h * 0.085f), 3f * u)
                drawCircle(L.cosa(P.LATON, Offset(x, piso)), h * 0.011f, Offset(x, piso - h * 0.09f))
                drawCircle(am.brillo(0.6f, 1f).let { if (noche) P.FUEGO.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.6f) }, 1f * u, Offset(x - h * 0.004f, piso - h * 0.094f))
                drawLine(hierro, Offset(x - h * 0.02f, piso), Offset(x + h * 0.02f, piso), 2.4f * u, StrokeCap.Round)
                drawLine(hierro, Offset(x, piso - h * 0.04f), Offset(x + h * 0.035f, piso - h * 0.05f), 2.2f * u)
            }
            // Los troncos: dos cruzados sobre los morillos, con su corte claro y la brasa en la corteza.
            listOf(Triple(-0.15f, 0.14f, -4f), Triple(-0.06f, 0.16f, 9f)).forEachIndexed { i, (x0, x1, giro) ->
                val a0 = Offset(f.x + h * x0, piso - h * (0.05f + 0.012f * i)); val a1 = Offset(f.x + h * x1, piso - h * (0.045f + 0.02f * i))
                val g = h * (0.034f - 0.004f * i)
                rotate(0f) {
                    drawLine(L.cosa(CORTEZA, a1), a0, a1, g, StrokeCap.Butt)
                    drawLine(if (noche) P.BRASA.copy(alpha = 0.85f) else L.cosa(Color(0xFF6A4C34), a1), a0 + Offset(0f, g * 0.3f), a1 + Offset(0f, g * 0.3f), 1.4f * u)
                    drawLine(am.sombra(0.4f), a0 + Offset(0f, -g * 0.36f), a1 + Offset(0f, -g * 0.36f), 1f * u)
                    drawOval(L.cosa(Color(0xFFC9A878), a0), Offset(a0.x - g * 0.25f, a0.y - g * 0.5f), Size(g * 0.5f, g))
                    drawOval(L.cosa(Color(0xFF8A6A44), a0), Offset(a0.x - g * 0.12f, a0.y - g * 0.25f), Size(g * 0.24f, g * 0.5f), style = Stroke(0.6f * u))
                }
                if (giro > 0f && noche) drawLine(P.FUEGO.copy(alpha = 0.7f), a0 + (a1 - a0) * 0.6f, a0 + (a1 - a0) * 0.85f, 1.2f * u)
            }
            // Las lenguas de la llama: de fuera adentro, el rojo, el naranja y el corazón claro.
            val alto = h * (if (noche) 0.2f else 0.1f)
            val base = piso - h * 0.06f
            listOf(Triple(-0.07f, 0.7f, 0f), Triple(-0.025f, 1f, 1f), Triple(0.02f, 0.85f, 2f), Triple(0.065f, 0.6f, 3f), Triple(0.0f, 0.5f, 4f)).forEach { (dx, k, sem) ->
                val c = Offset(f.x + h * dx, base)
                val a = alto * k * (0.85f + 0.3f * azar(7740, sem.toInt()))
                val an = h * 0.04f * (0.7f + 0.5f * k)
                val inclina = h * 0.02f * (azar(7741, sem.toInt()) - 0.5f)
                listOf(Triple(P.BRASA, 1f, 0.92f), Triple(P.LLAMA, 0.72f, 0.95f), Triple(P.FUEGO, 0.42f, 1f)).forEach { (col, e, al) ->
                    val lengua = Path().apply {
                        moveTo(c.x - an * e, c.y)
                        cubicTo(c.x - an * e, c.y - a * e * 0.5f, c.x + inclina - an * e * 0.3f, c.y - a * e * 0.75f, c.x + inclina * 1.4f, c.y - a * e)
                        cubicTo(c.x + inclina + an * e * 0.25f, c.y - a * e * 0.6f, c.x + an * e, c.y - a * e * 0.4f, c.x + an * e, c.y)
                        close()
                    }
                    drawPath(lengua, col.copy(alpha = al * (if (noche) 1f else 0.9f)))
                }
            }
            // De día, el humo que sube a la campana.
            if (!noche) {
                for (j in 0 until 6) {
                    val y = base - alto - h * 0.035f * j
                    val x = f.x + h * 0.02f * (azar(7750, j) - 0.5f) - h * 0.004f * j
                    mancha(Offset(x, y), h * (0.035f + 0.012f * j), Color(0xFFCFC8BE).copy(alpha = 0.16f))
                }
            }
        }
        // De noche, el halo del fuego sale de la boca: el canto de la jamba, la solera y el suelo de delante.
        if (noche) {
            mancha(Offset(f.x, piso), h * 0.6f, P.LLAMA.copy(alpha = 0.3f))
            mancha(Offset(s.xJamba + s.jamba, f.y), h * 0.2f, P.LLAMA.copy(alpha = 0.2f))
        }
    }
}
