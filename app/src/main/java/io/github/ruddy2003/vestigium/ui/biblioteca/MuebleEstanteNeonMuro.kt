package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.ALUMINIO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.AMBAR
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.CEMENTO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.CIAN
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.GRANATE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.HIERRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.HUESO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.JADE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.JADE_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.JADE_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.JUNTA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.MADERA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.MAGENTA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.PLASTICO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.SODIO
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/** El lado de un azulejo, en anchos del mueble. */
private const val AZULEJO_NEON = 0.048f
/** La fila de la cenefa (contando desde arriba). */
private const val FILA_CENEFA_NEON = 8

/**
 * Los azulejos de la pared dentro de [r] (en píxeles): la junta, y cada azulejo vidriado con su color según la luz que le
 * llega, su canto alto encendido y el bajo en sombra, su brillo de vidriado del lado de la luz; la fila de la cenefa, partida en
 * diagonal en granate y hueso; algunos rajados y uno que falta, con el cemento.
 */
internal fun DrawScope.azulejosNeon(r: Rect, g: MarcoNeon, conCenefa: Boolean) {
    val u = density; val w = g.w
    val lado = w * AZULEJO_NEON
    if (lado < 2f || r.width <= 0f || r.height <= 0f) return
    clipRect(r.left, r.top, r.right, r.bottom) {
        // La junta, del color hondo de la pared con la luz de su centro.
        val tj = g.t(r.center)
        drawRect(tj.c(JUNTA), r.topLeft, r.size)
        val c0 = (r.left / lado).toInt(); val c1 = (r.right / lado).toInt()
        val f0 = (r.top / lado).toInt(); val f1 = (r.bottom / lado).toInt()
        val junta = maxOf(1f, 1.1f * u)
        for (f in f0..f1) for (c in c0..c1) {
            val x = c * lado; val y = f * lado
            val centro = Offset(x + lado / 2, y + lado / 2)
            val t = g.t(centro)
            val sem = c * 131 + f * 977
            val cara = Rect(x + junta / 2, y + junta / 2, x + lado - junta / 2, y + lado - junta / 2)
            // El que falta: el cemento rugoso y el borde roto.
            if (c == (0.86f / AZULEJO_NEON).toInt() && f == 3) {
                drawRect(t.c(CEMENTO), cara.topLeft, cara.size)
                drawRect(t.s(0.45f), cara.topLeft, Size(cara.width, cara.height * 0.18f))
                for (j in 0 until 6) drawCircle(t.s(0.3f), (0.8f + azar(sem, j) * 1.4f) * u, Offset(cara.left + cara.width * azar(sem, j + 10), cara.top + cara.height * azar(sem, j + 20)))
                continue
            }
            // Alguno repuesto de otro color (crema), y el tono de cada uno un poco distinto.
            val repuesto = azar(sem, 9) > 0.965f
            val base = if (conCenefa && f == FILA_CENEFA_NEON) JADE_OSC
                else if (repuesto) mezcla(HUESO, JADE_LUZ, 0.25f)
                else mezcla(JADE, if (azar(sem, 1) > 0.5f) JADE_LUZ else JADE_OSC, 0.18f * azar(sem, 2))
            drawRect(t.c(base), cara.topLeft, cara.size)
            if (conCenefa && f == FILA_CENEFA_NEON) {
                // La cenefa: cada azulejo partido en diagonal, granate abajo y hueso arriba, alternando el sentido.
                val tri = if (c % 2 == 0) poligonoNeon(cara.left, cara.bottom, cara.right, cara.top, cara.right, cara.bottom)
                    else poligonoNeon(cara.left, cara.top, cara.right, cara.bottom, cara.left, cara.bottom)
                drawRect(t.c(mezcla(HUESO, JADE_LUZ, 0.2f)), cara.topLeft, cara.size)
                drawPath(tri, t.c(GRANATE))
            }
            // El vidriado: el canto de arriba encendido, el de abajo en sombra, el cojín del lado de la luz.
            drawRect(t.b(0.35f), cara.topLeft, Size(cara.width, maxOf(1f, 0.9f * u)))
            drawRect(t.s(0.32f), Offset(cara.left, cara.bottom - 1.2f * u), Size(cara.width, 1.2f * u))
            val lx = if (t.izq) cara.left else cara.right - 1.1f * u
            drawRect(t.b(0.22f), Offset(lx, cara.top), Size(1.1f * u, cara.height))
            drawRect(t.s(0.18f), Offset(if (t.izq) cara.right - 1.1f * u else cara.left, cara.top), Size(1.1f * u, cara.height))
            // El reflejo del vidriado: una raya corta del color de la luz, más viva cerca de ella.
            if (t.k > 0.42f && azar(sem, 4) > 0.35f) {
                val rx = cara.left + cara.width * (if (t.izq) 0.18f else 0.58f)
                drawLine(t.b(0.45f, 1.1f), Offset(rx, cara.top + cara.height * 0.2f), Offset(rx + cara.width * 0.2f, cara.top + cara.height * 0.2f), 1.1f * u, StrokeCap.Round)
            }
            // Rajados: una grieta quebrada.
            if (azar(sem, 5) > 0.9f) {
                val p = Path().apply {
                    moveTo(cara.left + cara.width * azar(sem, 6), cara.top)
                    lineTo(cara.left + cara.width * (0.3f + 0.4f * azar(sem, 7)), cara.top + cara.height * 0.45f)
                    lineTo(cara.left + cara.width * azar(sem, 8), cara.bottom)
                }
                drawPath(p, t.s(0.5f), style = Stroke(0.8f * u))
                translate1(p, 0.7f * u) { drawPath(it, t.b(0.25f), style = Stroke(0.5f * u)) }
            }
        }
    }
}

/** Dibuja [p] corrida [dx] a la derecha. */
private inline fun DrawScope.translate1(p: Path, dx: Float, bloque: DrawScope.(Path) -> Unit) {
    val q = Path().apply { addPath(p, Offset(dx, 0f)) }
    bloque(q)
}

/** La pared de los lados del cuerpo (la franja que asoma a cada lado de la alacena), del pie de la corona al suelo. */
internal fun DrawScope.paredLadosNeon(g: MarcoNeon) {
    val abajo = g.suelo
    if (abajo <= g.ch) return
    azulejosNeon(Rect(0f, g.ch, g.ml + 1f, abajo), g, false)
    azulejosNeon(Rect(g.mr - 1f, g.ch, g.w, abajo), g, false)
    // La sombra que echa la alacena en la pared: del lado contrario a la luz que le llega.
    val u = density
    drawRect(g.a.sombra(0.4f), Offset(g.mr, g.ch), Size(4f * u, abajo - g.ch))
    drawRect(g.a.sombra(0.22f), Offset(g.ml - 3f * u, g.ch), Size(3f * u, abajo - g.ch))
}

/** La pared de arriba, detrás del bodegón: los azulejos, los chorretones bajo la ventana y la sombra de la cornisa. */
internal fun DrawScope.paredArribaNeon(g: MarcoNeon) {
    val u = density; val w = g.w
    azulejosNeon(Rect(0f, 0f, w, g.ch), g, true)
    // El hollín y la grasa que suben y bajan por los azulejos: manchas largas y tenues.
    for (j in 0 until 9) {
        val x = w * azar(5111, j); val y = w * (0.02f + 0.4f * azar(5112, j))
        val t = g.t(x, y)
        drawOval(t.s(0.1f + 0.08f * azar(5113, j)), Offset(x - w * 0.02f, y), Size(w * (0.03f + 0.03f * azar(5114, j)), w * (0.08f + 0.1f * azar(5115, j))))
    }
    // En los Sueños, la sombra moteada de la lluvia del vidrio, en lo que alumbra la farola junto a la ventana.
    if (g.a.noche) for (j in 0 until 40) {
        val x = w * (0.02f + 0.42f * azar(5121, j)); val y = w * (0.02f + 0.44f * azar(5122, j))
        val m = MuebleEstanteNeon
        if (x > w * m.VENT_X0 - w * 0.025f && x < w * m.VENT_X1 + w * 0.01f && y > w * m.VENT_Y0 - w * 0.025f && y < w * m.VENT_Y1 + w * 0.025f) continue
        val t = g.t(x, y)
        if (t.k < 0.3f) continue
        drawCircle(t.s(0.3f), (1f + 1.6f * azar(5123, j)) * u, Offset(x, y))
        if (azar(5124, j) > 0.6f) drawLine(t.s(0.22f), Offset(x, y), Offset(x + 0.6f * u, y + w * 0.03f * azar(5125, j)), 1.2f * u)
    }
    // Los chorretones del agua que entra por la ventana, bajo el alféizar.
    for (j in 0 until 7) {
        val x = w * (0.07f + 0.25f * azar(5101, j))
        val y0 = w * (MuebleEstanteNeon.VENT_Y1 + 0.03f)
        val largo = w * (0.04f + 0.1f * azar(5102, j))
        val t = g.t(x, y0)
        drawLine(t.s(0.22f), Offset(x, y0), Offset(x + 0.6f * u, y0 + largo), (1.6f + 2.4f * azar(5103, j)) * u, StrokeCap.Round)
    }
}

/**
 * La ventana del cuarto al callejón, arriba a la izquierda: el hueco en el muro con su derrame encendido por la luz de fuera,
 * el callejón detrás del vidrio (la pared de enfrente con sus ventanas, las cajas de luz borrosas, los cables, el vapor y, en el
 * diario, los tubos del letrero de enfrente que cruzan la esquina; en los Sueños, el letrero apagado y la farola de sodio), la
 * lluvia y las gotas en el vidrio, la reja de barrotes, el marco de aluminio con su hoja corredera y el alféizar.
 */
internal fun DrawScope.ventanaNeon(g: MarcoNeon) {
    val u = density; val w = g.w
    val m = MuebleEstanteNeon
    val noche = g.a.noche
    val v = Rect(w * m.VENT_X0, w * m.VENT_Y0, w * m.VENT_X1, w * m.VENT_Y1)
    val luzFuera = if (noche) SODIO else mezcla(MAGENTA, AMBAR, 0.15f)
    // El derrame (el grueso del muro): arriba y a la izquierda, encendido por la luz de fuera; abajo, el alféizar.
    val hondo = w * 0.022f
    val marco = Rect(v.left - hondo, v.top - hondo, v.right + hondo * 0.4f, v.bottom + hondo * 0.3f)
    drawRect(g.a.sombra(0.4f), Offset(marco.left + 3f * u, marco.top + 3f * u), marco.size)
    drawRect(g.a.luz(CEMENTO, 0.5f, luzFuera), marco.topLeft, marco.size)
    drawPath(poligonoNeon(marco.left, marco.top, marco.right, marco.top, v.right, v.top, v.left, v.top), g.a.luz(CEMENTO, 1.1f, luzFuera))
    drawPath(poligonoNeon(marco.left, marco.top, v.left, v.top, v.left, v.bottom, marco.left, marco.bottom), g.a.luz(CEMENTO, 0.8f, luzFuera))
    drawPath(poligonoNeon(marco.right, marco.top, marco.right, marco.bottom, v.right, v.bottom, v.right, v.top), g.a.luz(CEMENTO, 0.25f, luzFuera))
    // El callejón.
    clipRect(v.left, v.top, v.right, v.bottom) { callejonNeon(v, g) }
    // La reja de barrotes (por fuera, recortada contra la luz, con su filo).
    val barras = 5
    for (j in 1 until barras) {
        val x = v.left + v.width * j / barras
        drawLine(Color(0xFF0B0710), Offset(x, v.top), Offset(x, v.bottom), 2.2f * u)
        drawLine(luzFuera.copy(alpha = 0.55f), Offset(x - 1.1f * u, v.top), Offset(x - 1.1f * u, v.bottom), 0.7f * u)
    }
    listOf(0.36f, 0.72f).forEach { fy ->
        val y = v.top + v.height * fy
        drawLine(Color(0xFF0B0710), Offset(v.left, y), Offset(v.right, y), 2f * u)
        drawLine(luzFuera.copy(alpha = 0.45f), Offset(v.left, y - 1f * u), Offset(v.right, y - 1f * u), 0.7f * u)
    }
    // El marco de aluminio: el de fuera, la hoja corredera de la izquierda (delante) y su tirador.
    val tm = g.t(v.center)
    val al = g.a.luz(ALUMINIO, 0.55f, luzFuera)
    val g1 = 4.2f * u
    drawRect(al, v.topLeft, Size(v.width, g1))
    drawRect(al, Offset(v.left, v.bottom - g1), Size(v.width, g1))
    drawRect(al, v.topLeft, Size(g1, v.height))
    drawRect(g.a.luz(ALUMINIO, 0.3f, luzFuera), Offset(v.right - g1, v.top), Size(g1, v.height))
    val medio = v.left + v.width * 0.52f
    drawRect(g.a.sombra(0.45f), Offset(medio + g1, v.top), Size(2.5f * u, v.height))
    drawRect(al, Offset(medio - g1 / 2, v.top), Size(g1 * 1.3f, v.height))
    drawRect(tm.b(0.6f, 1.4f), Offset(medio - g1 / 2, v.top), Size(1f * u, v.height))
    drawRect(Color.White.copy(alpha = 0.18f), Offset(v.left, v.top), Size(v.width, 1f * u))
    drawRoundRect(g.a.luz(HIERRO, 0.6f, luzFuera), Offset(medio - g1 * 0.9f, v.center.y - 5f * u), Size(2.4f * u, 10f * u), CornerRadius(1.2f * u))
    // El alféizar de cemento: su cara de arriba, encendida, y el frente en sombra; el goterón.
    val al0 = v.bottom; val alfeizar = Rect(v.left - hondo * 1.3f, al0, v.right + hondo * 0.9f, al0 + w * 0.02f)
    drawRect(g.a.sombra(0.45f), Offset(alfeizar.left + 2f * u, alfeizar.bottom), Size(alfeizar.width, 4f * u))
    drawRect(g.a.luz(CEMENTO, 0.55f, luzFuera), alfeizar.topLeft, alfeizar.size)
    drawRect(g.a.luz(mezcla(CEMENTO, Color.White, 0.3f), 1.1f, luzFuera), alfeizar.topLeft, Size(alfeizar.width, alfeizar.height * 0.36f))
    drawRect(g.a.sombra(0.3f), Offset(alfeizar.left, alfeizar.bottom - 1.4f * u), Size(alfeizar.width, 1.4f * u))
    // El charquito en el alféizar, con el reflejo de la luz de fuera.
    drawOval(luzFuera.copy(alpha = 0.55f), Offset(v.left + v.width * 0.3f, al0 + 1f * u), Size(v.width * 0.3f, 2.2f * u))
    // El resplandor del vidrio en el cuarto (pegado a la ventana).
    mancha(v.center, v.width * 0.95f, luzFuera.copy(alpha = if (noche) 0.08f else 0.11f))
}

/** El callejón de detrás del vidrio, dentro de [v]. */
private fun DrawScope.callejonNeon(v: Rect, g: MarcoNeon) {
    val u = density
    val noche = g.a.noche
    // El aire del callejón: de arriba hondo a la bruma de abajo, teñida de la luz que manda.
    val bruma = if (noche) Color(0xFF3B4A1C) else Color(0xFF5A1640)
    drawRect(Brush.verticalGradient(listOf(if (noche) Color(0xFF0B1214) else Color(0xFF16091F), bruma), v.top, v.bottom), v.topLeft, v.size)
    // La pared de enfrente: un bloque oscuro con ventanas pequeñas, algunas encendidas, y un aparato de aire con su goteo.
    val frente = Rect(v.left + v.width * 0.12f, v.top + v.height * 0.05f, v.left + v.width * 0.95f, v.bottom)
    drawRect(if (noche) Color(0xFF141C18) else Color(0xFF220E26), frente.topLeft, frente.size)
    for (f in 0 until 5) for (c in 0 until 4) {
        val s = f * 7 + c
        val r = Rect(frente.left + frente.width * (0.08f + 0.24f * c), frente.top + frente.height * (0.06f + 0.19f * f),
            frente.left + frente.width * (0.2f + 0.24f * c), frente.top + frente.height * (0.15f + 0.19f * f))
        val encendida = azar(5201, s) > (if (noche) 0.78f else 0.55f)
        val col = if (encendida) listOf(AMBAR, Color(0xFF9FE8FF), Color(0xFFFFD9A0))[s % 3].copy(alpha = 0.75f) else Color(0xFF0A060E)
        drawRect(col, r.topLeft, r.size)
    }
    // Las cajas de luz de los letreros lejanos: franjas y bloques de color, borrosas (sin letras ni signos).
    val cajas = if (noche) listOf(Triple(0.62f, 0.3f, Color(0xFF2A3C40))) else listOf(Triple(0.62f, 0.3f, CIAN), Triple(0.84f, 0.55f, AMBAR), Triple(0.3f, 0.66f, Color(0xFF7CFF6B)))
    cajas.forEachIndexed { i, (fx, fy, col) ->
        val c = Offset(v.left + v.width * fx, v.top + v.height * fy)
        val alto = v.height * (0.34f - 0.06f * i); val ancho = v.width * 0.09f
        if (!noche) mancha(c, alto * 0.9f, col.copy(alpha = 0.38f))
        drawRoundRect(col.copy(alpha = if (noche) 1f else 0.85f), Offset(c.x - ancho / 2, c.y - alto / 2), Size(ancho, alto), CornerRadius(2f * u))
        if (!noche) for (j in 0 until 3) drawRect(Color.White.copy(alpha = 0.5f), Offset(c.x - ancho * 0.3f, c.y - alto * 0.38f + j * alto * 0.3f), Size(ancho * 0.6f, alto * 0.12f))
    }
    // En los Sueños, la farola de sodio en su brazo, arriba a la derecha, con su halo.
    if (noche) {
        val f = Offset(v.left + v.width * 0.8f, v.top + v.height * 0.2f)
        mancha(f, v.width * 0.55f, SODIO.copy(alpha = 0.5f))
        drawLine(Color(0xFF0E120C), Offset(v.right, f.y - v.height * 0.04f), Offset(f.x + v.width * 0.04f, f.y - v.height * 0.04f), 2.4f * u)
        drawRoundRect(Color(0xFF1A1E14), Offset(f.x - v.width * 0.06f, f.y - v.height * 0.06f), Size(v.width * 0.12f, v.height * 0.05f), CornerRadius(3f * u))
        drawOval(mezcla(SODIO, Color.White, 0.55f), Offset(f.x - v.width * 0.045f, f.y - v.height * 0.015f), Size(v.width * 0.09f, v.height * 0.03f))
    }
    // Los cables del callejón, en comba, con sus gotas.
    listOf(0.18f to 0.1f, 0.46f to 0.06f).forEachIndexed { i, (fy, comba) ->
        val y = v.top + v.height * fy
        val p = Path().apply { moveTo(v.left, y); quadraticTo(v.center.x, y + v.height * comba * 2f, v.right, y - v.height * 0.03f) }
        drawPath(p, Color(0xFF07050A), style = Stroke(1.4f * u))
        for (j in 0 until 4) {
            val fx = 0.15f + 0.22f * j + 0.05f * i
            val x = v.left + v.width * fx
            val yy = y + v.height * comba * 4f * fx * (1f - fx) * 0.98f - v.height * 0.03f * fx
            drawCircle((if (noche) SODIO else MAGENTA).copy(alpha = 0.7f), 1.1f * u, Offset(x, yy + 1.6f * u))
        }
    }
    // El vapor que sube de una rejilla del callejón.
    for (j in 0 until 4) {
        val c = Offset(v.left + v.width * (0.42f + 0.08f * j), v.bottom - v.height * (0.15f + 0.17f * j))
        mancha(c, v.width * (0.16f + 0.05f * j), (if (noche) Color(0xFFB9C9A0) else Color(0xFFF0B6D8)).copy(alpha = 0.22f))
    }
    // El letrero de enfrente, pegado al vidrio: sus tubos cruzan la esquina de arriba a la izquierda. En los Sueños, apagado
    // (vidrio gris), con un tramo a media luz.
    val cx = v.left - v.width * 0.05f; val cy = v.top - v.height * 0.08f
    val arco = Path().apply { addArc(Rect(cx - v.width * 0.5f, cy - v.height * 0.5f, cx + v.width * 0.5f, cy + v.height * 0.5f), 0f, 90f) }
    val arco2 = Path().apply { addArc(Rect(cx - v.width * 0.36f, cy - v.height * 0.36f, cx + v.width * 0.36f, cy + v.height * 0.36f), 5f, 82f) }
    val recto = Path().apply { addArc(Rect(cx - v.width * 0.72f, cy - v.height * 0.72f, cx + v.width * 0.72f, cy + v.height * 0.72f), 12f, 40f) }
    if (noche) {
        tuboApagadoNeon(arco, 4.4f * u); tuboApagadoNeon(recto, 3.4f * u)
        tuboNeonEn(arco2, MAGENTA, 3.2f * u, 0.4f)
    } else {
        mancha(Offset(v.left + v.width * 0.12f, v.top + v.height * 0.14f), v.width * 0.6f, MAGENTA.copy(alpha = 0.55f))
        tuboNeonEn(arco, MAGENTA, 4.4f * u, 1f)
        tuboNeonEn(arco2, mezcla(MAGENTA, Color(0xFFFF8AD0), 0.4f), 3.2f * u, 0.9f)
        tuboNeonEn(recto, AMBAR, 3.4f * u, 1f)
    }
    // La lluvia: rayas finas inclinadas y, en el vidrio, gotas con su brillo y algún reguero (más en los Sueños).
    val gotas = if (noche) 46 else 24
    for (j in 0 until gotas) {
        val x = v.left + v.width * azar(5301, j); val y = v.top + v.height * azar(5302, j)
        val largo = v.height * (0.05f + 0.07f * azar(5303, j))
        drawLine(Color.White.copy(alpha = 0.16f), Offset(x, y), Offset(x - largo * 0.18f, y + largo), 0.7f * u)
    }
    for (j in 0 until gotas) {
        val o = Offset(v.left + v.width * azar(5311, j), v.top + v.height * azar(5312, j))
        val rg = (0.9f + 1.5f * azar(5313, j)) * u
        drawCircle(Color(0xFF05030A).copy(alpha = 0.35f), rg, o + Offset(0f, rg * 0.3f))
        drawCircle(mezcla(if (noche) SODIO else MAGENTA, Color.White, 0.5f).copy(alpha = 0.6f), rg * 0.45f, o - Offset(rg * 0.25f, rg * 0.25f))
        if (azar(5314, j) > (if (noche) 0.55f else 0.75f)) drawLine(Color.White.copy(alpha = 0.18f), Offset(o.x, o.y + rg), Offset(o.x + 0.6f * u, o.y + rg + v.height * 0.12f * azar(5315, j)), 0.8f * u)
    }
    // El vaho de abajo del vidrio.
    drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.1f)), v.bottom - v.height * 0.22f, v.bottom), Offset(v.left, v.bottom - v.height * 0.22f), Size(v.width, v.height * 0.22f))
}

/** Un tubo de neón encendido: el halo, el vidrio del color y el alma casi blanca (capas planas). [brillo] baja en el que flojea. */
internal fun DrawScope.tuboNeonEn(p: Path, color: Color, g: Float, brillo: Float) {
    drawPath(p, color.copy(alpha = 0.16f * brillo), style = Stroke(g * 4.5f, cap = StrokeCap.Round))
    drawPath(p, color.copy(alpha = 0.36f * brillo), style = Stroke(g * 2.2f, cap = StrokeCap.Round))
    drawPath(p, mezcla(Color(0xFF2A1830), color, 0.35f + 0.65f * brillo), style = Stroke(g, cap = StrokeCap.Round))
    drawPath(p, mezcla(color, Color.White, 0.65f).copy(alpha = 0.95f * brillo), style = Stroke(g * 0.35f, cap = StrokeCap.Round))
}

/** Un tubo de neón apagado: el vidrio gris con su brillo. */
internal fun DrawScope.tuboApagadoNeon(p: Path, g: Float) {
    drawPath(p, Color(0xFF3E4440), style = Stroke(g, cap = StrokeCap.Round))
    drawPath(p, Color(0xFF8E9A7A).copy(alpha = 0.6f), style = Stroke(g * 0.3f, cap = StrokeCap.Round))
}

/**
 * Lo colgado en la pared: la canaleta gris de la luz que baja del techo a su caja de enchufes (con el cable que va al acuario),
 * el reloj de pared sin cifras y el paraguas plegado colgado de un clavo, que gotea en la cornisa.
 */
internal fun DrawScope.colgadoNeon(g: MarcoNeon) {
    val u = density; val w = g.w
    // ---- la canaleta y la caja de enchufes
    val xc = w * 0.905f; val cajaY = w * 0.3f
    val tc = g.t(xc, w * 0.15f)
    drawRect(tc.s(0.4f), Offset(xc - w * 0.012f + 3f * u, 0f), Size(w * 0.024f, cajaY))
    drawRect(tc.c(PLASTICO), Offset(xc - w * 0.012f, 0f), Size(w * 0.024f, cajaY))
    drawRect(tc.b(0.4f), Offset(xc - w * 0.012f, 0f), Size(1.4f * u, cajaY))
    drawRect(tc.s(0.3f), Offset(xc + w * 0.012f - 2f * u, 0f), Size(2f * u, cajaY))
    listOf(0.06f, 0.18f).forEach { fy -> drawRect(tc.s(0.35f), Offset(xc - w * 0.013f, w * fy), Size(w * 0.026f, 1.6f * u)) }
    val caja = Rect(xc - w * 0.022f, cajaY, xc + w * 0.022f, cajaY + w * 0.048f)
    val tk = g.t(caja.center)
    drawRect(tk.s(0.45f), caja.topLeft + Offset(3f * u, 3f * u), caja.size)
    drawRoundRect(tk.c(mezcla(PLASTICO, HUESO, 0.4f)), caja.topLeft, caja.size, CornerRadius(1.6f * u))
    drawRect(tk.b(0.5f), caja.topLeft, Size(caja.width, 1f * u))
    // El enchufe enchufado y el cable que baja en comba hasta la bomba del acuario.
    val ench = Offset(caja.center.x, caja.center.y + caja.height * 0.1f)
    drawRoundRect(tk.c(HIERRO), Offset(ench.x - w * 0.009f, ench.y - w * 0.008f), Size(w * 0.018f, w * 0.016f), CornerRadius(1.4f * u))
    val cable = Path().apply { moveTo(ench.x, ench.y + w * 0.008f); cubicTo(ench.x, ench.y + w * 0.08f, w * 0.86f, g.apoyo - w * 0.06f, w * 0.86f, g.apoyo - w * 0.03f) }
    drawPath(cable, tk.s(0.4f), style = Stroke(2f * u))
    drawPath(cable, tk.c(Color(0xFF1C1A22)), style = Stroke(1.6f * u))
    // ---- el reloj de pared, sin cifras: el aro de plástico, la esfera, las marcas, las agujas y el vidrio
    val rc = Offset(w * 0.415f, w * 0.13f); val rr = w * 0.06f
    val tr = g.t(rc)
    drawCircle(tr.s(0.45f), rr, rc + Offset(rr * 0.16f * (if (tr.izq) 1f else -1f), rr * 0.2f))
    drawCircle(tr.c(Color(0xFF3F8C8A)), rr, rc)
    drawCircle(tr.c(HUESO), rr * 0.8f, rc)
    drawArc(tr.s(0.25f), 300f, 180f, false, Offset(rc.x - rr * 0.8f, rc.y - rr * 0.8f), Size(rr * 1.6f, rr * 1.6f), style = Stroke(rr * 0.12f))
    for (k in 0 until 12) {
        val an = Math.toRadians(k * 30.0)
        val c = kotlin.math.cos(an).toFloat(); val s = kotlin.math.sin(an).toFloat()
        val r0 = rr * (if (k % 3 == 0) 0.56f else 0.64f)
        drawLine(tr.c(HIERRO), Offset(rc.x + c * r0, rc.y + s * r0), Offset(rc.x + c * rr * 0.72f, rc.y + s * rr * 0.72f), (if (k % 3 == 0) 1.6f else 0.9f) * u)
    }
    // Las agujas: en el diario, pasadas las diez de la noche; en los Sueños, la misma esquina más tarde, casi las cuatro.
    val (hora, minuto) = if (g.a.noche) 3.75f to 45f else 10.2f to 12f
    fun aguja(f: Float, largo: Float, grueso: Float, col: Color) {
        val an = Math.toRadians((f * 360.0 - 90.0))
        drawLine(col, rc, Offset(rc.x + rr * largo * kotlin.math.cos(an).toFloat(), rc.y + rr * largo * kotlin.math.sin(an).toFloat()), grueso, StrokeCap.Round)
    }
    aguja(hora / 12f, 0.4f, 2f * u, tr.c(HIERRO))
    aguja(minuto / 60f, 0.58f, 1.4f * u, tr.c(HIERRO))
    aguja((minuto + 21f) / 60f, 0.6f, 0.8f * u, tr.c(Color(0xFFD8362E)))
    drawCircle(tr.c(HIERRO), 1.8f * u, rc)
    drawArc(tr.b(0.7f, 1.3f), 200f, 70f, false, Offset(rc.x - rr * 0.92f, rc.y - rr * 0.92f), Size(rr * 1.84f, rr * 1.84f), style = Stroke(1.4f * u))
    drawArc(Color.White.copy(alpha = 0.12f), 205f, 50f, true, Offset(rc.x - rr * 0.7f, rc.y - rr * 0.7f), Size(rr * 1.4f, rr * 1.4f))
    // ---- la postal del mar, pegada con cinta en los azulejos (el sol que nunca baja al nivel −3)
    postalNeon(g, Offset(w * 0.66f, w * 0.1f))
    // ---- el paraguas plegado, colgado de su mango de madera en un clavo, que gotea en la cornisa
    paraguasNeon(g, Offset(w * 0.962f, w * 0.06f))
}

/** La postal pegada en los azulejos: el borde blanco, el cielo de tarde, el sol a medio hundir en el mar y su reflejo; dos tiras de cinta. */
private fun DrawScope.postalNeon(g: MarcoNeon, c: Offset) {
    val u = density; val w = g.w
    val t = g.t(c)
    rotate(-7f, c) {
        val r = Rect(c.x - w * 0.055f, c.y - w * 0.038f, c.x + w * 0.055f, c.y + w * 0.038f)
        drawRect(t.s(0.45f), r.topLeft + Offset(2.4f * u, 2.4f * u), r.size)
        drawRect(t.c(HUESO, 1.2f), r.topLeft, r.size)
        val f = Rect(r.left + w * 0.006f, r.top + w * 0.006f, r.right - w * 0.006f, r.bottom - w * 0.006f)
        val horizonte = f.top + f.height * 0.58f
        drawRect(t.c(Color(0xFF3E7AC8), 1.3f), f.topLeft, Size(f.width, horizonte - f.top))
        drawRect(t.c(Color(0xFFF0A050), 1.3f), Offset(f.left, horizonte - f.height * 0.2f), Size(f.width, f.height * 0.2f))
        clipRect(f.left, f.top, f.right, horizonte) { drawCircle(t.c(Color(0xFFFFD860), 1.5f), f.height * 0.2f, Offset(f.left + f.width * 0.62f, horizonte)) }
        drawRect(t.c(Color(0xFF1E3E78), 1.3f), Offset(f.left, horizonte), Size(f.width, f.bottom - horizonte))
        for (j in 0 until 3) drawLine(t.c(Color(0xFFFFD860), 1.4f), Offset(f.left + f.width * (0.55f + 0.03f * j), horizonte + f.height * (0.08f + 0.1f * j)), Offset(f.left + f.width * (0.69f - 0.02f * j), horizonte + f.height * (0.08f + 0.1f * j)), 1f * u)
        drawRect(t.b(0.4f), r.topLeft, Size(r.width, 1f * u))
        listOf(r.topLeft, Offset(r.right, r.bottom)).forEach { p ->
            rotate(45f, p) { drawRect(t.c(Color(0xFFD8CCA0), 1.2f).copy(alpha = 0.8f), Offset(p.x - w * 0.016f, p.y - w * 0.005f), Size(w * 0.032f, w * 0.01f)) }
        }
    }
}

/** El paraguas plegado colgado del clavo [clavo]: el mango en J, la tela enrollada con su cinta y la contera que gotea. */
private fun DrawScope.paraguasNeon(g: MarcoNeon, clavo: Offset) {
    val u = density; val w = g.w
    val t = g.t(clavo.x, clavo.y + w * 0.2f)
    // El mango de madera en J, colgado del clavo.
    val mango = Path().apply {
        moveTo(clavo.x + w * 0.004f, clavo.y + w * 0.06f)
        lineTo(clavo.x + w * 0.004f, clavo.y + w * 0.012f)
        cubicTo(clavo.x + w * 0.004f, clavo.y - w * 0.018f, clavo.x - w * 0.03f, clavo.y - w * 0.018f, clavo.x - w * 0.03f, clavo.y + w * 0.01f)
    }
    drawPath(mango, t.s(0.4f), style = Stroke(4.4f * u, cap = StrokeCap.Round))
    drawPath(mango, t.c(MADERA), style = Stroke(3.6f * u, cap = StrokeCap.Round))
    drawPath(mango, t.b(0.5f), style = Stroke(1f * u, cap = StrokeCap.Round))
    clavoNeon(clavo, 1.8f * u, t, ALUMINIO)
    // La tela enrollada: un huso largo, con sus pliegues y la cinta con su broche.
    val x = clavo.x + w * 0.004f
    val y0 = clavo.y + w * 0.055f; val y1 = clavo.y + w * 0.33f
    val ancho = w * 0.034f
    val tela = Path().apply {
        moveTo(x - ancho * 0.25f, y0)
        cubicTo(x - ancho * 0.7f, y0 + (y1 - y0) * 0.25f, x - ancho * 0.55f, y0 + (y1 - y0) * 0.7f, x - ancho * 0.05f, y1)
        lineTo(x + ancho * 0.05f, y1)
        cubicTo(x + ancho * 0.55f, y0 + (y1 - y0) * 0.7f, x + ancho * 0.7f, y0 + (y1 - y0) * 0.25f, x + ancho * 0.25f, y0)
        close()
    }
    drawPath(Path().apply { addPath(tela, Offset(-4f * u, 3f * u)) }, t.s(0.35f))
    val azul = Color(0xFF2E3F8A)
    drawPath(tela, t.c(azul))
    for (j in 0 until 3) {
        val px = x - ancho * 0.3f + ancho * 0.25f * j
        drawLine(t.s(0.4f), Offset(px, y0 + w * 0.01f), Offset(x + (px - x) * 0.2f, y1 - w * 0.02f), 1f * u)
        drawLine(t.b(0.3f), Offset(px - 1.2f * u, y0 + w * 0.01f), Offset(x + (px - x) * 0.2f - 1.2f * u, y1 - w * 0.03f), 0.7f * u)
    }
    drawLine(t.b(0.5f), Offset(x - ancho * 0.45f, y0 + (y1 - y0) * 0.15f), Offset(x - ancho * 0.35f, y0 + (y1 - y0) * 0.55f), 1.2f * u)
    val yc = y0 + (y1 - y0) * 0.34f
    drawRect(t.c(Color(0xFF1C2350)), Offset(x - ancho * 0.42f, yc), Size(ancho * 0.84f, w * 0.012f))
    drawCircle(t.c(ALUMINIO), 1.4f * u, Offset(x + ancho * 0.2f, yc + w * 0.006f))
    // La contera y las gotas que caen a la cornisa.
    drawLine(t.c(ALUMINIO), Offset(x, y1), Offset(x, y1 + w * 0.025f), 1.6f * u)
    val gota = g.t(x, g.apoyo)
    listOf(0.045f, 0.09f).forEach { dy -> drawOval(gota.b(0.8f, 1.5f), Offset(x - 1.1f * u, y1 + w * dy), Size(2.2f * u, 3.2f * u)) }
}
