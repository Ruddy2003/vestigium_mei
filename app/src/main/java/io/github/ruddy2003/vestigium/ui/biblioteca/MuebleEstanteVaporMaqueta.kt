package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.BERMELLON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.FILETE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.GRANATE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.HIERRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.HIERRO_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.HOJALATA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LATON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LATON_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LATON_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.NEGRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.NOGAL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.NOGAL_LUZ
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

/**
 * La maqueta de enseñanza del cuarto del encargado (ver [MuebleEstanteVapor]): una máquina tanque genérica de la época, de dos
 * ejes acoplados y uno trasero, sobre su tramo de vía (rieles y traviesas) en una peana de madera oscura con su placa de latón
 * lisa. Pintura granate con filetes amarillos y bordes negros, caja de humos y chimenea negras, topes bermellón, domo y válvula
 * de seguridad de latón pulido, pasamanos de latón, cilindro de fuera con su biela y su cruceta, ruedas de radios con su
 * contrapeso y la biela de acoplamiento de acero. No copia ninguna máquina conocida y no lleva número ni letras.
 */
internal object MaquetaVapor {
    /**
     * La maqueta entera: la peana va de [x0] a [x1] y pisa en [pie]; la máquina mide [largo] y mira a la izquierda. [k], [f] y
     * [br] son la luz que le llega; [hacia] de qué lado (−1, de la izquierda; 1, de la derecha).
     */
    fun DrawScope.maqueta(x0: Float, x1: Float, pie: Float, largo: Float, a: AireVapor, k: Float, f: Float, br: Float, hacia: Float) {
        val u = density
        val L = largo
        val c: (Color) -> Color = { col -> a.luz(col, k, f, br) }
        // La peana: la tabla oscura con su moldura de luz arriba y la placa lisa de latón.
        val ph = L * 0.055f
        drawRect(a.sombra(0.35f), Offset(x0 + L * 0.02f * -hacia, pie - 1f * u), Size(x1 - x0, 2.5f * u))
        drawRect(c(NOGAL), Offset(x0, pie - ph), Size(x1 - x0, ph))
        drawRect(c(NOGAL_LUZ), Offset(x0, pie - ph), Size(x1 - x0, ph * 0.3f))
        drawRect(a.brillo(0.5f, k, br), Offset(x0, pie - ph), Size(x1 - x0, 0.9f * u))
        drawRect(a.sombra(0.35f), Offset(x0, pie - ph * 0.7f), Size(x1 - x0, 0.9f * u))
        val placa = Rect((x0 + x1) / 2 - L * 0.1f, pie - ph * 0.62f, (x0 + x1) / 2 + L * 0.1f, pie - ph * 0.15f)
        drawRect(c(LATON), placa.topLeft, placa.size)
        drawRect(a.brillo(0.6f, k, br), placa.topLeft, Size(placa.width, 0.8f * u))
        drawRect(c(LATON_OSC), placa.topLeft, placa.size, style = Stroke(0.6f * u))
        // El balasto, las traviesas (sus testas) y el riel.
        val vt = pie - ph
        drawRect(c(Color(0xFF7C7466)), Offset(x0 + L * 0.02f, vt - L * 0.018f), Size(x1 - x0 - L * 0.04f, L * 0.018f))
        val trav = Path()
        var tx = x0 + L * 0.035f
        while (tx < x1 - L * 0.04f) { trav.addRect(Rect(tx, vt - L * 0.022f, tx + L * 0.028f, vt - L * 0.008f)); tx += maxOf(1f, L * 0.062f) }
        drawPath(trav, c(Color(0xFF4A3424)))
        val yr = vt - L * 0.034f
        drawRect(c(HIERRO), Offset(x0 + L * 0.01f, yr), Size(x1 - x0 - L * 0.02f, L * 0.013f))
        drawRect(a.brillo(0.7f, maxOf(k, 0.3f), br), Offset(x0 + L * 0.01f, yr), Size(x1 - x0 - L * 0.02f, 0.9f * u))

        // ---- la máquina, de atrás adelante: el bastidor y las ruedas, la plataforma, el tanque, la caldera, la cabina.
        val xs = x0 + (x1 - x0 - L) / 2
        fun x(fr: Float) = xs + L * fr
        fun y(fr: Float) = yr - L * fr
        val plat = y(0.215f)
        // El bastidor, en sombra detrás de las ruedas.
        drawRect(c(NEGRO), Offset(x(0.1f), y(0.2f)), Size(L * 0.8f, L * 0.08f))
        // Las ruedas: dos acopladas y una trasera.
        val ang = 0.6f
        val rD = L * 0.105f; val rT = L * 0.07f
        val ruedas = listOf(Offset(x(0.335f), yr - rD) to rD, Offset(x(0.565f), yr - rD) to rD, Offset(x(0.8f), yr - rT) to rT)
        ruedas.forEachIndexed { i, (cr, rr) -> rueda(cr, rr, if (i < 2) 10 else 8, ang + i * 0.0f, a, k, f, br, i < 2) }
        // La biela de acoplamiento, de muñón a muñón.
        val m1 = Offset(ruedas[0].first.x + rD * 0.55f * cos(ang), ruedas[0].first.y + rD * 0.55f * sin(ang))
        val m2 = Offset(ruedas[1].first.x + rD * 0.55f * cos(ang), ruedas[1].first.y + rD * 0.55f * sin(ang))
        drawLine(c(HIERRO_LUZ), m1, m2, L * 0.022f, StrokeCap.Round)
        drawLine(a.brillo(0.6f, k, br), m1 - Offset(0f, L * 0.007f), m2 - Offset(0f, L * 0.007f), 0.8f * u)
        listOf(m1, m2).forEach { m -> drawCircle(c(HIERRO_LUZ), L * 0.018f, m); drawCircle(c(LATON), L * 0.007f, m) }
        // El cilindro de fuera, su vástago, la cruceta en su guía y la biela hasta el muñón de la primera rueda.
        val cil = Rect(x(0.1f), y(0.205f), x(0.235f), y(0.115f))
        drawRect(c(GRANATE), cil.topLeft, cil.size)
        drawRect(a.brillo(0.35f, k, br), Offset(cil.left, cil.top + cil.height * 0.15f), Size(cil.width, cil.height * 0.18f))
        drawRect(a.sombra(0.3f), Offset(cil.left, cil.bottom - cil.height * 0.3f), Size(cil.width, cil.height * 0.3f))
        drawRect(c(FILETE), Offset(cil.left + L * 0.01f, cil.top + L * 0.008f), Size(cil.width - L * 0.02f, cil.height - L * 0.016f), style = Stroke(0.6f * u))
        drawOval(c(HOJALATA), Offset(cil.left - L * 0.012f, cil.top - L * 0.004f), Size(L * 0.024f, cil.height + L * 0.008f))
        drawOval(a.brillo(0.8f, k, br), Offset(cil.left - L * 0.008f, cil.top + L * 0.006f), Size(L * 0.008f, cil.height * 0.4f))
        val guia = y(0.16f)
        drawLine(c(HIERRO_LUZ), Offset(cil.right, guia - L * 0.014f), Offset(x(0.33f), guia - L * 0.014f), L * 0.007f)
        drawLine(c(HIERRO_LUZ), Offset(cil.right, guia + L * 0.014f), Offset(x(0.33f), guia + L * 0.014f), L * 0.007f)
        val cruceta = Offset(x(0.3f), guia)
        drawLine(c(HOJALATA), Offset(cil.right, guia), cruceta, L * 0.011f)
        drawRect(c(HIERRO), Offset(cruceta.x - L * 0.018f, guia - L * 0.014f), Size(L * 0.036f, L * 0.028f))
        drawLine(c(HIERRO_LUZ), cruceta, m1, L * 0.017f, StrokeCap.Round)
        drawLine(a.brillo(0.5f, k, br), cruceta - Offset(0f, L * 0.005f), m1 - Offset(0f, L * 0.005f), 0.7f * u)
        // La plataforma y su faldón con el filete.
        drawRect(c(NEGRO), Offset(x(0.06f), plat - L * 0.012f), Size(L * 0.88f, L * 0.014f))
        drawRect(c(GRANATE), Offset(x(0.1f), plat + L * 0.002f), Size(L * 0.82f, L * 0.026f))
        drawRect(c(FILETE), Offset(x(0.11f), plat + L * 0.012f), Size(L * 0.8f, 0.6f * u))
        drawRect(a.brillo(0.45f, k, br), Offset(x(0.06f), plat - L * 0.012f), Size(L * 0.88f, 0.8f * u))
        // Los topes: la viga bermellón, el tope negro con su plato de acero y el gancho; delante y detrás.
        listOf(x(0.06f) to -1f, x(0.94f) to 1f).forEach { (bx, s) ->
            val viga = Rect(if (s < 0) bx else bx - L * 0.035f, plat - L * 0.03f, if (s < 0) bx + L * 0.035f else bx, plat + L * 0.05f)
            drawRect(c(BERMELLON), viga.topLeft, viga.size)
            drawRect(a.sombra(0.3f), Offset(viga.left, viga.bottom - L * 0.012f), Size(viga.width, L * 0.012f))
            val ty = plat + L * 0.012f
            val tope = Rect(if (s < 0) bx - L * 0.045f else bx, ty - L * 0.012f, if (s < 0) bx else bx + L * 0.045f, ty + L * 0.012f)
            drawRect(c(NEGRO), tope.topLeft, tope.size)
            val plato = if (s < 0) tope.left else tope.right
            drawRect(c(HOJALATA), Offset(plato - L * 0.006f, ty - L * 0.024f), Size(L * 0.012f, L * 0.048f))
            drawRect(a.brillo(0.7f, k, br), Offset(plato - L * 0.004f, ty - L * 0.02f), Size(L * 0.004f, L * 0.03f))
        }
        // La caja de humos y su silleta, negras; la puerta de delante y su manija.
        val yc = plat - L * 0.105f
        val rc = L * 0.1f
        drawRect(c(NEGRO), Offset(x(0.11f), yc), Size(L * 0.13f, plat - yc))
        val humos = Rect(x(0.1f), yc - rc, x(0.24f), yc + rc)
        drawRect(c(NEGRO), humos.topLeft, humos.size)
        cuerpoRedondo(humos, a, k, br, 0.6f)
        drawOval(c(Color(0xFF2E2B2E)), Offset(humos.left - L * 0.014f, humos.top + L * 0.006f), Size(L * 0.028f, humos.height - L * 0.012f))
        drawOval(a.brillo(0.4f, k, br), Offset(humos.left - L * 0.012f, humos.top + L * 0.02f), Size(L * 0.008f, humos.height * 0.4f))
        drawLine(c(HOJALATA), Offset(humos.left - L * 0.03f, yc), Offset(humos.left - L * 0.008f, yc), L * 0.008f, StrokeCap.Round)
        // La chimenea, con su falda y su borde volado.
        val ch = Path().apply {
            val cx = x(0.17f); val base = humos.top + L * 0.012f; val tope = base - L * 0.13f
            moveTo(cx - L * 0.05f, base); quadraticTo(cx - L * 0.028f, base - L * 0.01f, cx - L * 0.026f, base - L * 0.03f)
            lineTo(cx - L * 0.03f, tope + L * 0.02f); lineTo(cx - L * 0.042f, tope + L * 0.012f); lineTo(cx - L * 0.042f, tope)
            lineTo(cx + L * 0.042f, tope); lineTo(cx + L * 0.042f, tope + L * 0.012f); lineTo(cx + L * 0.03f, tope + L * 0.02f)
            lineTo(cx + L * 0.026f, base - L * 0.03f); quadraticTo(cx + L * 0.028f, base - L * 0.01f, cx + L * 0.05f, base); close()
        }
        drawPath(ch, c(NEGRO))
        clipPath(ch) { cuerpoRedondo(Rect(x(0.12f), humos.top - L * 0.13f, x(0.22f), humos.top + L * 0.02f), a, k, br, 0.9f, vertical = true) }
        drawRect(a.brillo(0.6f, k, br), Offset(x(0.128f), humos.top - L * 0.118f), Size(L * 0.084f, 0.8f * u))
        // El tanque de agua del costado, granate con su filete y su borde negro.
        val tq = Rect(x(0.27f), plat - L * 0.112f, x(0.6f), plat)
        // La caldera: el cilindro granate con sus aros negros fileteados.
        val cal = Rect(x(0.24f), yc - L * 0.093f, x(0.63f), yc + L * 0.093f)
        drawRect(c(GRANATE), cal.topLeft, cal.size)
        cuerpoRedondo(cal, a, k, br, 1f)
        listOf(0.33f, 0.45f, 0.57f).forEach { fr ->
            drawRect(c(NEGRO), Offset(x(fr) - L * 0.007f, cal.top), Size(L * 0.014f, cal.height))
            drawRect(c(FILETE), Offset(x(fr) - L * 0.0105f, cal.top), Size(0.5f * u, cal.height))
            drawRect(c(FILETE), Offset(x(fr) + L * 0.0105f, cal.top), Size(0.5f * u, cal.height))
        }
        // El pasamanos de latón con sus columnitas.
        drawLine(c(LATON), Offset(x(0.24f), yc - L * 0.035f), Offset(x(0.62f), yc - L * 0.035f), 0.9f * u)
        listOf(0.27f, 0.4f, 0.52f).forEach { fr -> drawCircle(c(LATON_OSC), 1.1f * u, Offset(x(fr), yc - L * 0.035f)) }
        // El domo de latón pulido: lo más brillante de la maqueta.
        val domo = Path().apply {
            val cx = x(0.44f); val base = cal.top + L * 0.012f
            moveTo(cx - L * 0.06f, base); quadraticTo(cx - L * 0.045f, base - L * 0.005f, cx - L * 0.042f, base - L * 0.025f)
            cubicTo(cx - L * 0.04f, base - L * 0.085f, cx + L * 0.04f, base - L * 0.085f, cx + L * 0.042f, base - L * 0.025f)
            quadraticTo(cx + L * 0.045f, base - L * 0.005f, cx + L * 0.06f, base); close()
        }
        drawPath(domo, c(LATON))
        clipPath(domo) {
            val db = Rect(x(0.38f), cal.top - L * 0.08f, x(0.5f), cal.top + L * 0.012f)
            drawRect(c(LATON_OSC), Offset(db.left + db.width * (if (hacia < 0) 0.55f else 0f), db.top), Size(db.width * 0.45f, db.height))
            drawRect(c(LATON_LUZ), Offset(db.left + db.width * (if (hacia < 0) 0.22f else 0.6f), db.top), Size(db.width * 0.14f, db.height))
            drawOval(Color.White.copy(alpha = if (a.oscuro) 0.6f * k else 0.75f), Offset(db.left + db.width * (if (hacia < 0) 0.27f else 0.63f), db.top + db.height * 0.25f), Size(db.width * 0.07f, db.height * 0.3f))
        }
        drawPath(domo, c(LATON_OSC), style = Stroke(0.6f * u))
        // La válvula de seguridad, una trompeta de latón sobre el hogar.
        val vs = x(0.59f)
        val valv = poligonoVapor(vs - L * 0.025f, cal.top + L * 0.008f, vs - L * 0.01f, cal.top - L * 0.03f, vs - L * 0.02f, cal.top - L * 0.06f,
            vs + L * 0.02f, cal.top - L * 0.06f, vs + L * 0.01f, cal.top - L * 0.03f, vs + L * 0.025f, cal.top + L * 0.008f)
        drawPath(valv, c(LATON))
        drawRect(c(LATON_LUZ), Offset(vs - L * (if (hacia < 0) 0.008f else -0.002f), cal.top - L * 0.058f), Size(L * 0.006f, L * 0.06f))
        drawPath(valv, c(LATON_OSC), style = Stroke(0.5f * u))
        // El tanque, que tapa la mitad de abajo de la caldera.
        drawRect(c(GRANATE), tq.topLeft, tq.size)
        drawRect(a.brillo(0.4f, k, br), tq.topLeft, Size(tq.width, L * 0.008f))
        drawRect(a.sombra(0.22f), Offset(tq.left, tq.top + tq.height * 0.65f), Size(tq.width, tq.height * 0.35f))
        drawRect(c(NEGRO), tq.topLeft, tq.size, style = Stroke(L * 0.006f))
        drawRect(c(FILETE), Offset(tq.left + L * 0.014f, tq.top + L * 0.014f), Size(tq.width - L * 0.028f, tq.height - L * 0.028f), style = Stroke(0.7f * u))
        // La cabina: los costados con su recorte, el techo volado, el filete y el borde de latón del recorte.
        val cab = Rect(x(0.63f), plat - L * 0.3f, x(0.82f), plat)
        drawRect(c(GRANATE), cab.topLeft, cab.size)
        drawRect(a.sombra(0.2f), Offset(cab.left + cab.width * (if (hacia < 0) 0.7f else 0f), cab.top), Size(cab.width * 0.3f, cab.height))
        val recorte = Rect(cab.left + L * 0.035f, cab.top + L * 0.03f, cab.right - L * 0.022f, cab.top + L * 0.14f)
        drawRect(c(Color(0xFF1A1416)), recorte.topLeft, recorte.size)
        drawCircle(c(LATON), L * 0.012f, Offset(recorte.left + recorte.width * 0.3f, recorte.top + recorte.height * 0.6f))
        drawRect(c(LATON), recorte.topLeft, recorte.size, style = Stroke(0.9f * u))
        drawRect(c(FILETE), Offset(cab.left + L * 0.014f, recorte.bottom + L * 0.016f), Size(cab.width - L * 0.028f, cab.bottom - recorte.bottom - L * 0.03f), style = Stroke(0.7f * u))
        val techo = Path().apply {
            moveTo(cab.left - L * 0.025f, cab.top + L * 0.012f); quadraticTo(cab.center.x, cab.top - L * 0.03f, cab.right + L * 0.02f, cab.top + L * 0.012f)
            lineTo(cab.right + L * 0.02f, cab.top + L * 0.024f); quadraticTo(cab.center.x, cab.top - L * 0.016f, cab.left - L * 0.025f, cab.top + L * 0.024f); close()
        }
        drawPath(techo, c(NEGRO))
        drawPath(techo, a.brillo(0.35f, k, br), style = Stroke(0.6f * u))
        // El silbato de latón delante del techo.
        drawRect(c(LATON), Offset(cab.left + L * 0.006f, cab.top - L * 0.03f), Size(L * 0.012f, L * 0.03f))
        drawOval(c(LATON_LUZ), Offset(cab.left + L * 0.002f, cab.top - L * 0.04f), Size(L * 0.02f, L * 0.014f))
        // La carbonera, con el carbón amontonado.
        val car = Rect(x(0.82f), plat - L * 0.16f, x(0.92f), plat)
        drawRect(c(GRANATE), car.topLeft, car.size)
        drawRect(c(FILETE), Offset(car.left + L * 0.01f, car.top + L * 0.012f), Size(car.width - L * 0.02f, car.height - L * 0.024f), style = Stroke(0.7f * u))
        drawRect(c(NEGRO), car.topLeft, car.size, style = Stroke(L * 0.005f))
        val carbon = Path().apply {
            moveTo(car.left, car.top); lineTo(car.left + car.width * 0.15f, car.top - L * 0.03f); lineTo(car.left + car.width * 0.4f, car.top - L * 0.045f)
            lineTo(car.left + car.width * 0.7f, car.top - L * 0.035f); lineTo(car.right, car.top - L * 0.01f); lineTo(car.right, car.top); close()
        }
        drawPath(carbon, c(Color(0xFF15141A)))
        listOf(0.2f to 0.6f, 0.45f to 0.85f, 0.7f to 0.6f).forEach { (fx, fy) -> drawCircle(a.brillo(0.6f, k, br), 0.9f * u, Offset(car.left + car.width * fx, car.top - L * 0.035f * fy)) }
        // La sombra de la caldera en el tanque y de la plataforma en las ruedas.
        drawRect(a.sombra(0.25f), Offset(tq.left, tq.top), Size(tq.width, L * 0.012f))
        clipRect(x(0.1f), plat, x(0.92f), plat + L * 0.06f) { drawRect(Brush.verticalGradient(listOf(a.sombra(0.4f), Color.Transparent), plat, plat + L * 0.06f), Offset(x(0.1f), plat), Size(L * 0.82f, L * 0.06f)) }
    }

    /** Lo redondo visto de lado: luz en una franja de arriba, sombra abajo (o, en [vertical], luz a la izquierda y sombra a la derecha). */
    private fun DrawScope.cuerpoRedondo(r: Rect, a: AireVapor, k: Float, br: Float, fuerza: Float, vertical: Boolean = false) {
        if (vertical) {
            drawRect(a.brillo(0.28f * fuerza, maxOf(k, 0.25f), br), Offset(r.left + r.width * 0.18f, r.top), Size(r.width * 0.16f, r.height))
            drawRect(a.sombra(0.3f * fuerza), Offset(r.left + r.width * 0.62f, r.top), Size(r.width * 0.38f, r.height))
            return
        }
        drawRect(a.brillo(0.22f * fuerza, maxOf(k, 0.25f), br), Offset(r.left, r.top + r.height * 0.12f), Size(r.width, r.height * 0.2f))
        drawRect(a.brillo(0.35f * fuerza, maxOf(k, 0.25f), br), Offset(r.left, r.top + r.height * 0.18f), Size(r.width, r.height * 0.05f))
        drawRect(a.sombra(0.22f * fuerza), Offset(r.left, r.top + r.height * 0.6f), Size(r.width, r.height * 0.4f))
        drawRect(a.sombra(0.2f * fuerza), Offset(r.left, r.top + r.height * 0.82f), Size(r.width, r.height * 0.18f))
    }

    /** Una rueda de radios: la llanta de acero con su pestaña, el centro granate, los radios, el cubo, el contrapeso y el muñón. */
    private fun DrawScope.rueda(c: Offset, r: Float, radios: Int, ang: Float, a: AireVapor, k: Float, f: Float, br: Float, contrapeso: Boolean) {
        val u = density
        drawCircle(a.luz(Color(0xFF151316), k, f, br), r * 0.86f, c)
        val ray = Path()
        for (i in 0 until radios) {
            val t = ang + i * (2f * Math.PI.toFloat() / radios)
            ray.moveTo(c.x + r * 0.2f * cos(t), c.y + r * 0.2f * sin(t)); ray.lineTo(c.x + r * 0.82f * cos(t), c.y + r * 0.82f * sin(t))
        }
        drawPath(ray, a.luz(GRANATE, k, f, br), style = Stroke(r * 0.1f))
        if (contrapeso) {
            val t = ang + Math.PI.toFloat()
            drawArc(a.luz(GRANATE, k, f, br), Math.toDegrees(t.toDouble()).toFloat() - 40f, 80f, false, Offset(c.x - r * 0.62f, c.y - r * 0.62f), Size(r * 1.24f, r * 1.24f), style = Stroke(r * 0.32f))
        }
        drawCircle(a.luz(GRANATE, k, f, br), r * 0.84f, c, style = Stroke(r * 0.1f))
        drawCircle(a.luz(NEGRO, k, f, br), r * 0.95f, c, style = Stroke(r * 0.12f))
        drawArc(a.brillo(0.7f, maxOf(k, 0.3f), br), 150f, 110f, false, Offset(c.x - r, c.y - r), Size(r * 2f, r * 2f), style = Stroke(0.9f * u))
        drawCircle(a.luz(GRANATE, k, f, br), r * 0.24f, c)
        drawCircle(a.luz(HOJALATA, k, f, br), r * 0.1f, c)
        drawCircle(a.brillo(0.6f, k, br), r * 0.05f, c - Offset(r * 0.04f, r * 0.04f))
    }
}
