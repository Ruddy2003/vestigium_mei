package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * La librería empotrada de Botánico ([MuebleEstanteBotanico]), de roble claro como el gabinete del Ideario: la cornisa (el
 * tablero visto desde arriba, el canto, la gola, el friso con su filete de nogal y los resaltos sobre las pilastras, el
 * arquitrabe), los casilleros sin fondo (entre las tablas se ve la pared salvia del cuarto, en la sombra de la tabla de arriba
 * y del costado), las tablas con su canto grueso y el fleco de cuero festoneado que cuelga de cada una, las dos pilastras
 * acanaladas con su capitel y su basa, los portaetiquetas de latón de los meses y el zócalo. De día la luz viene de arriba a
 * la izquierda; de noche, cada tramo se apaga con la distancia a la lumbre que le toca.
 */
internal object ObraEstanteBotanico {
    private val P = PaletaBotanico

    /** Cuántos tramos lleva lo que se pinta a lo ancho: de noche la luz cambia de un tramo a otro sin escalón. */
    private fun tramos(s: SitioBotanico, ancho: Float) = if (s.a.lumbre) (ancho / (s.w * 0.03f)).toInt().coerceIn(2, 48) else 3

    // ---------------------------------------------------------------- la cornisa (en la imagen guardada de arriba)

    /** La cornisa de [r] (de donde pisan las cosas hasta el arranque de las repisas, y un poco más). */
    fun DrawScope.cornisa(r: Rect, s: SitioBotanico) {
        val w = size.width; val u = density
        val y0 = r.top
        val sup0 = y0 - w * 0.014f
        val canto0 = y0 + w * 0.004f
        val canto1 = y0 + w * 0.022f
        val gola1 = y0 + w * 0.034f
        val friso1 = y0 + w * 0.07f
        val arq1 = y0 + w * 0.086f
        val n = tramos(s, w)
        for (i in 0 until n) {
            val x0 = w * i / n; val x1 = w * (i + 1) / n + 1f
            val t = s.t(Offset((x0 + x1) / 2, y0 + w * 0.03f), true)
            // El tablero de arriba, visto desde encima (más claro, gastado), y su canto de frente.
            drawRect(t.c(mezcla(P.ROBLE_LUZ, P.ROBLE, 0.3f), 0.02f), Offset(x0, sup0), Size(x1 - x0, canto0 - sup0))
            drawRect(t.c(P.ROBLE_LUZ, 0.04f), Offset(x0, canto0), Size(x1 - x0, (canto1 - canto0) * 0.55f))
            drawRect(t.c(P.ROBLE, -0.02f), Offset(x0, canto0 + (canto1 - canto0) * 0.55f), Size(x1 - x0, (canto1 - canto0) * 0.45f))
            // La gola: clara arriba, honda abajo.
            drawRect(t.c(P.ROBLE, 0.06f), Offset(x0, canto1), Size(x1 - x0, (gola1 - canto1) * 0.4f))
            drawRect(t.c(P.ROBLE_OSC, 0f), Offset(x0, canto1 + (gola1 - canto1) * 0.4f), Size(x1 - x0, (gola1 - canto1) * 0.6f))
            // El friso y el arquitrabe en dos fajas.
            drawRect(t.c(P.ROBLE, 0f), Offset(x0, gola1), Size(x1 - x0, friso1 - gola1))
            drawRect(t.c(P.ROBLE, 0.03f), Offset(x0, friso1), Size(x1 - x0, (arq1 - friso1) * 0.5f))
            drawRect(t.c(P.ROBLE, -0.06f), Offset(x0, friso1 + (arq1 - friso1) * 0.5f), Size(x1 - x0, (arq1 - friso1) * 0.5f))
            drawRect(t.c(P.ROBLE_OSC, -0.05f), Offset(x0, arq1), Size(x1 - x0, r.bottom - arq1))
        }
        val tc = s.t(Offset(w * 0.5f, gola1), true)
        vetas(Rect(0f, gola1, w, friso1), tc.c(P.ROBLE_OSC).copy(alpha = 0.32f), 9101, 0.7f, grosor = 0.55f, ondulacion = 0.6f)
        vetas(Rect(0f, sup0, w, canto0), tc.c(P.ROBLE_OSC).copy(alpha = 0.25f), 9102, 0.8f, grosor = 0.5f, ondulacion = 0.4f)
        // Los filetes de luz y las líneas de sombra de cada moldura.
        drawRect(tc.s(0.3f), Offset(0f, sup0), Size(w, 1f * u))
        drawRect(tc.b(0.6f), Offset(0f, canto0), Size(w, 1.2f * u))
        drawRect(tc.s(0.4f), Offset(0f, canto1 - 1f * u), Size(w, 1f * u))
        drawRect(tc.b(0.35f), Offset(0f, canto1 + 0.5f * u), Size(w, 0.9f * u))
        drawRect(tc.s(0.5f), Offset(0f, gola1 - 1.2f * u), Size(w, 1.4f * u))
        drawRect(tc.s(0.35f), Offset(0f, gola1), Size(w, 2.4f * u))
        drawRect(tc.b(0.3f), Offset(0f, friso1), Size(w, 0.9f * u))
        drawRect(tc.s(0.35f), Offset(0f, friso1 + (arq1 - friso1) * 0.5f), Size(w, 0.8f * u))
        drawRect(tc.s(0.6f), Offset(0f, arq1), Size(w, 1.4f * u))
        // Los dentellones bajo el canto: tacos con su cara de luz, su costado en sombra y lo hondo entre ellos.
        val dent0 = canto1 + 1.2f * u; val dent1 = gola1 - 1.4f * u
        val pasoD = w * 0.021f
        var xd = w * 0.004f
        var nd = 0
        while (xd < w && nd < 200) {
            val t = s.t(Offset(xd, dent0), true)
            drawRect(t.c(P.ROBLE_HONDO, 0f), Offset(xd + pasoD * 0.62f, dent0), Size(pasoD * 0.38f, dent1 - dent0))
            drawRect(t.c(P.ROBLE, 0.08f), Offset(xd, dent0), Size(pasoD * 0.62f, dent1 - dent0))
            drawRect(t.b(0.45f), Offset(xd, dent0), Size(1f * u, dent1 - dent0))
            drawRect(t.s(0.3f), Offset(xd + pasoD * 0.5f, dent0), Size(pasoD * 0.12f, dent1 - dent0))
            xd += pasoD; nd++
        }
        drawRect(tc.s(0.45f), Offset(0f, dent1), Size(w, 1.4f * u))
        // El filete de nogal embutido en el friso.
        val fl = Rect(w * 0.1f, gola1 + (friso1 - gola1) * 0.34f, w * 0.9f, friso1 - (friso1 - gola1) * 0.34f)
        drawRect(tc.c(P.NOGAL), fl.topLeft, fl.size, style = Stroke(1.4f * u))
        drawRect(tc.b(0.25f), Offset(fl.left, fl.top + 1.4f * u), Size(fl.width, 0.6f * u))
        // Los resaltos del friso sobre las pilastras, con su florón tallado.
        val lw = w * MuebleEstanteBotanico.LADO
        listOf(0f, w - lw).forEach { x ->
            val t = s.t(Offset(x + lw / 2, gola1), true)
            val rr = Rect(x, canto1, x + lw, arq1)
            drawRect(t.c(P.ROBLE_LUZ, -0.02f), rr.topLeft, rr.size)
            drawRect(t.b(0.5f), rr.topLeft, Size(rr.width, 1f * u))
            drawRect(t.b(0.3f), rr.topLeft, Size(1f * u, rr.height))
            drawRect(t.s(0.4f), Offset(rr.right - 1.4f * u, rr.top), Size(1.4f * u, rr.height))
            drawRect(t.s(0.45f), Offset(rr.left, rr.bottom - 1.2f * u), Size(rr.width, 1.2f * u))
            floron(Offset(rr.center.x, (gola1 + friso1) / 2 + w * 0.004f), w * 0.015f, t)
        }
        // El gastado: golpes en el canto del tablero y rozaduras.
        for (j in 0 until 9) {
            val x = w * (0.04f + 0.92f * azar(9110, j))
            val t = s.t(Offset(x, canto0), true)
            drawPath(poligonoBotanico(x, canto0, x + w * 0.011f, canto0, x + w * 0.005f, canto0 + w * 0.004f), t.c(P.ROBLE_LUZ, 0.2f))
            val y = gola1 + (friso1 - gola1) * azar(9111, j)
            drawLine(t.b(0.2f), Offset(x, y), Offset(x + w * (0.015f + 0.025f * azar(9112, j)), y + 0.5f * u), 0.7f * u)
        }
    }

    /** Un florón tallado de cinco pétalos redondos y su botón (no una cruz), con su luz arriba a la izquierda. */
    private fun DrawScope.floron(c: Offset, rr: Float, t: TonoBotanico) {
        drawCircle(t.s(0.4f), rr * 1.05f, c + Offset(rr * 0.2f, rr * 0.25f))
        for (j in 0 until 5) {
            val an = -1.5708f + j * 1.2566f
            val q = Offset(c.x + rr * 0.55f * kotlin.math.cos(an), c.y + rr * 0.55f * kotlin.math.sin(an))
            drawCircle(t.c(P.ROBLE, 0.02f), rr * 0.42f, q)
            drawCircle(t.s(0.22f), rr * 0.42f, q + Offset(rr * 0.1f, rr * 0.12f), style = Stroke(rr * 0.12f))
            drawCircle(t.b(0.35f), rr * 0.16f, q - Offset(rr * 0.12f, rr * 0.12f))
        }
        drawCircle(t.c(P.ROBLE_OSC), rr * 0.3f, c)
        drawCircle(t.b(0.5f), rr * 0.12f, c - Offset(rr * 0.08f, rr * 0.08f))
    }

    // ---------------------------------------------------------------- los casilleros

    /**
     * El casillero [r] de la repisa [k]: la pared salvia del cuarto, que se ve porque la librería no tiene fondo, hundida en la
     * sombra de la tabla de arriba y de la pilastra de la izquierda; la cara de abajo de la tabla de arriba, las caras de dentro
     * de los costados y el piso, con su luz.
     */
    fun DrawScope.casillero(k: Int, r: Rect, d: DisposicionEstante, s: SitioBotanico) {
        val w = size.width; val u = density
        // Una fila de más arriba y abajo: así las juntas con las tablas no dejan asomar el fondo.
        val fr = Rect(r.left - 1f, r.top - 1f, r.right + 1f, r.bottom + 1f)
        val n = tramos(s, r.width)
        val techo = w * 0.026f
        val lado = w * 0.028f
        val piso = w * 0.022f
        for (i in 0 until n) {
            val x0 = fr.left + fr.width * i / n; val x1 = fr.left + fr.width * (i + 1) / n + 1f
            val t = s.t(Offset((x0 + x1) / 2, r.center.y))
            // La pared: más honda arriba, bajo la tabla, y un poco más viva abajo, donde le llega la luz por la boca.
            drawRect(t.c(P.SALVIA, -0.16f), Offset(x0, fr.top), Size(x1 - x0, fr.height))
            drawRect(t.c(P.SALVIA_LUZ, -0.08f).copy(alpha = if (s.a.oscuro) 0.2f else 0.35f), Offset(x0, r.top + r.height * 0.56f), Size(x1 - x0, r.height * 0.36f))
        }
        val tc = s.t(r.center)
        // El enlucido: pinceladas, una grieta en algunos y algún clavo viejo.
        pinceladas(r, if (s.a.oscuro) P.NOCHE else P.SALVIA_HONDO, 9200 + k, (r.width / (24f * u)).toInt().coerceIn(4, 30), u, 0.12f)
        pinceladas(r, if (s.a.oscuro) P.NOCHE else P.SALVIA_LUZ, 9300 + k, (r.width / (40f * u)).toInt().coerceIn(3, 20), u, 0.1f)
        if (azar(9400, k) > 0.45f) {
            val p0 = Offset(r.left + r.width * (0.2f + 0.6f * azar(9401, k)), r.top + r.height * 0.3f)
            val t = s.t(p0)
            val g = Path().apply {
                moveTo(p0.x, p0.y); var q = p0
                for (j in 0 until 4) { q += Offset(w * (0.01f * azar(9402 + k, j) - 0.004f), r.height * (0.08f + 0.06f * azar(9403 + k, j))); lineTo(q.x, q.y) }
            }
            drawPath(g, t.s(0.26f), style = Stroke(0.7f * u))
        }
        val cv = Offset(r.left + r.width * (0.3f + 0.5f * azar(9404, k)), r.top + r.height * (0.38f + 0.2f * azar(9405, k)))
        drawCircle(s.t(cv).s(0.4f), 1.1f * u, cv)
        // Una mancha de humedad o de cola, vieja.
        val mx = r.left + r.width * azar(9406, k)
        drawOval(tc.s(0.08f), Offset(mx, r.top + r.height * (0.3f + 0.3f * azar(9407, k))), Size(w * 0.09f, w * 0.05f))
        // La sombra franca de la tabla de arriba en la pared (corrida abajo, la luz viene de arriba), y su penumbra.
        drawRect(tc.s(0.5f), Offset(r.left, r.top + techo * 0.7f), Size(r.width, r.height * 0.13f))
        drawRect(tc.s(0.24f), Offset(r.left, r.top + techo * 0.7f + r.height * 0.13f), Size(r.width, r.height * 0.1f))
        // La sombra de la pilastra de la izquierda en la pared (de día; de noche, la de la luz que le toca).
        drawRect(tc.s(0.34f), Offset(r.left + lado, r.top), Size(w * 0.05f, r.height))
        drawRect(tc.s(0.16f), Offset(r.left + lado + w * 0.05f, r.top), Size(w * 0.03f, r.height))
        // La cara de abajo de la tabla de arriba (un trapecio de roble en sombra).
        val kt = s.t(Offset(r.center.x, r.top), true)
        drawPath(poligonoBotanico(fr.left, fr.top, fr.right, fr.top, r.right - lado, r.top + techo, r.left + lado, r.top + techo), kt.c(P.ROBLE_OSC, -0.2f))
        drawRect(kt.s(0.35f), Offset(r.left + lado, r.top + techo - 1f * u), Size(r.width - 2 * lado, 1f * u))
        // Las caras de dentro de los costados: la de la izquierda mira a la derecha (en sombra de día); la de la derecha mira a
        // la luz. De noche, cada una según su lumbre.
        val tl = s.t(Offset(r.left, r.center.y), true); val tr = s.t(Offset(r.right, r.center.y), true)
        val kl = if (s.a.oscuro && r.left / w < MuebleEstanteBotanico.LAMP_X) 0.02f else -0.3f
        val kr = if (s.a.oscuro && r.right / w > MuebleEstanteBotanico.LAMP_X) -0.3f else 0.06f
        drawPath(poligonoBotanico(fr.left, fr.top, r.left + lado, r.top + techo, r.left + lado, r.bottom - piso, fr.left, fr.bottom), tl.c(P.ROBLE, kl))
        drawPath(poligonoBotanico(fr.right, fr.top, r.right - lado, r.top + techo, r.right - lado, r.bottom - piso, fr.right, fr.bottom), tr.c(P.ROBLE, kr))
        listOf(0.35f, 0.7f).forEach { f ->
            drawLine(tl.c(P.ROBLE_OSC).copy(alpha = 0.5f), Offset(r.left + lado * f, r.top + techo * f), Offset(r.left + lado * f, r.bottom - piso * f), 0.6f * u)
            drawLine(tr.c(P.ROBLE_OSC).copy(alpha = 0.4f), Offset(r.right - lado * f, r.top + techo * f), Offset(r.right - lado * f, r.bottom - piso * f), 0.6f * u)
        }
        drawRect(tr.b(0.3f), Offset(r.right - lado - 1f * u, r.top + techo), Size(1f * u, r.height - techo - piso))
        // El piso, visto desde arriba, con la luz que entra por la boca y la sombra de lo de atrás.
        val tp = s.t(Offset(r.center.x, r.bottom), true)
        drawPath(poligonoBotanico(r.left + lado, r.bottom - piso, r.right - lado, r.bottom - piso, fr.right, fr.bottom, fr.left, fr.bottom), tp.c(P.ROBLE_LUZ, -0.05f))
        drawRect(tp.s(0.42f), Offset(r.left + lado, r.bottom - piso), Size(r.width - 2 * lado, 1.2f * u))
        drawRect(tp.s(0.16f), Offset(r.left + lado, r.bottom - piso + 1.2f * u), Size(r.width - 2 * lado, piso * 0.4f))
        // Las rayas de lo que se saca y se mete.
        for (j in 0 until 4) {
            val x = r.left + r.width * (0.1f + 0.8f * azar(9410 + k, j))
            drawLine(tp.b(0.25f), Offset(x, r.bottom - piso * 0.6f), Offset(x + w * 0.03f, r.bottom - piso * 0.4f), 0.6f * u)
        }
    }

    // ---------------------------------------------------------------- el fleco de cuero

    /**
     * El fleco de cuero que cuelga del canto de una tabla (o de la cornisa) en [y], de [x0] a [x1]: la tira clavada con su
     * filete dorado y los festones redondos, cada uno con su clavo de latón; en la repisa [k] (−1, la cornisa) un tramo se ha
     * despegado y cae.
     */
    fun DrawScope.fleco(y: Float, x0: Float, x1: Float, k: Int, s: SitioBotanico) {
        val w = size.width; val u = density
        val tira = 3.2f * u
        val festonAncho = 11f * u
        val festonAlto = 5.6f * u
        val n = ((x1 - x0) / festonAncho).toInt().coerceIn(1, 200)
        val paso = (x1 - x0) / n
        // Un tramo despegado: dos o tres festones que cuelgan torcidos (no en todas las repisas).
        val roto = if (azar(9500, k + 3) > 0.5f) (azar(9501, k + 3) * (n - 3)).toInt().coerceAtLeast(0) else -10
        val nt = tramos(s, x1 - x0)
        for (i in 0 until nt) {
            val a0 = x0 + (x1 - x0) * i / nt; val a1 = x0 + (x1 - x0) * (i + 1) / nt + 1f
            val t = s.t(Offset((a0 + a1) / 2, y + tira))
            drawRect(t.s(0.35f), Offset(a0, y + tira), Size(a1 - a0, 2.2f * u))
            drawRect(t.c(P.CUERO, 0.04f), Offset(a0, y), Size(a1 - a0, tira))
        }
        // Los festones y su sombra en la pared.
        val fest = Path(); val sombra = Path(); val caido = Path()
        for (i in 0 until n) {
            val cx = x0 + paso * (i + 0.5f)
            val l = cx - paso * 0.5f
            val esRoto = i - roto in 0..2
            val p = if (esRoto) caido else fest
            val cae = if (esRoto) festonAlto * (0.6f + 0.5f * (i - roto)) else 0f
            p.moveTo(l, y + tira - 0.5f * u)
            p.lineTo(l + paso, y + tira - 0.5f * u + cae * 0.4f)
            p.quadraticTo(l + paso * 0.98f, y + tira + festonAlto + cae, cx, y + tira + festonAlto * 1.1f + cae)
            p.quadraticTo(l + paso * 0.02f, y + tira + festonAlto + cae * 0.6f, l, y + tira - 0.5f * u)
            p.close()
            sombra.addOval(Rect(cx - paso * 0.42f + 1.2f * u, y + tira + festonAlto * 0.3f + 1.6f * u + cae, cx + paso * 0.42f + 1.2f * u, y + tira + festonAlto * 1.25f + 1.6f * u + cae))
        }
        val tc = s.t(Offset((x0 + x1) / 2, y + tira))
        drawPath(sombra, tc.s(0.3f))
        // Por tramos de luz, recortando: de noche cada tramo con su lumbre.
        for (i in 0 until nt) {
            val a0 = x0 + (x1 - x0) * i / nt; val a1 = x0 + (x1 - x0) * (i + 1) / nt + 1f
            val t = s.t(Offset((a0 + a1) / 2, y + tira))
            clipRect(a0, y - 1f, a1, y + tira + festonAlto * 3.5f) {
                drawPath(fest, t.c(P.CUERO))
                drawPath(caido, t.c(P.CUERO, -0.15f))
                drawPath(fest, t.c(P.CUERO_OSC).copy(alpha = 0.6f), style = Stroke(0.7f * u))
            }
        }
        // El filete dorado a lo largo de la tira, el filo de luz de arriba y los clavos.
        drawLine(tc.c(P.LATON).copy(alpha = 0.75f), Offset(x0, y + tira * 0.55f), Offset(x1, y + tira * 0.55f), 0.6f * u)
        drawRect(tc.b(0.4f), Offset(x0, y), Size(x1 - x0, 0.8f * u))
        for (i in 0..n) {
            val x = x0 + paso * i
            if (i - roto in 1..3) continue
            val t = s.t(Offset(x, y))
            drawCircle(t.c(P.LATON_OSC), 1.2f * u, Offset(x, y + tira * 0.55f))
            drawCircle(t.b(0.7f), 0.5f * u, Offset(x - 0.3f * u, y + tira * 0.4f))
        }
    }

    // ---------------------------------------------------------------- las tablas

    /** La tabla [r] bajo la repisa [k]: el canto grueso de roble con su filete, su media caña y su sombra, y el fleco debajo. */
    fun DrawScope.tabla(k: Int, r: Rect, d: DisposicionEstante, s: SitioBotanico) {
        val w = size.width; val u = density
        val n = tramos(s, r.width)
        val bocel = r.top + r.height * 0.62f
        for (i in 0 until n) {
            val x0 = r.left + r.width * i / n - 1f; val x1 = r.left + r.width * (i + 1) / n + 1f
            val t = s.t(Offset((x0 + x1) / 2, r.center.y), true)
            drawRect(t.c(P.ROBLE, 0.02f), Offset(x0, r.top - 1f), Size(x1 - x0, bocel - r.top + 1f))
            drawRect(t.c(P.ROBLE_LUZ, 0f), Offset(x0, bocel), Size(x1 - x0, (r.bottom - bocel) * 0.45f))
            drawRect(t.c(P.ROBLE_OSC, 0f), Offset(x0, bocel + (r.bottom - bocel) * 0.45f), Size(x1 - x0, (r.bottom - bocel) * 0.55f + 1f))
            // De noche, el filo de la lumbre en el canto (más donde más alumbra).
            if (s.a.lumbre) drawRect(t.filo(0.6f), Offset(x0, r.top), Size(x1 - x0, 1.5f * u))
        }
        val tc = s.t(r.center, true)
        vetas(Rect(r.left, r.top, r.right, bocel), tc.c(P.ROBLE_OSC).copy(alpha = 0.35f), 9600 + k, 0.8f, grosor = 0.5f, ondulacion = 0.5f)
        drawRect(tc.b(0.55f), Offset(r.left, r.top), Size(r.width, 1.2f * u))
        drawRect(tc.s(0.35f), Offset(r.left, bocel - 1f * u), Size(r.width, 1f * u))
        drawRect(tc.b(0.3f), Offset(r.left, bocel + 0.4f * u), Size(r.width, 0.8f * u))
        drawRect(tc.s(0.5f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
        // Los golpes y el canto gastado donde más se toca.
        for (j in 0 until 4) {
            val x = r.left + r.width * azar(9700 + k, j)
            val t = s.t(Offset(x, r.top), true)
            drawPath(poligonoBotanico(x, r.top, x + w * 0.012f, r.top, x + w * 0.005f, r.top + w * 0.005f), t.c(P.ROBLE_LUZ, 0.22f))
        }
        // El fleco que cuelga de esta tabla sobre la repisa de abajo.
        if (k < d.tablas.lastIndex) fleco(r.bottom, r.left, r.right, k, s)
    }

    // ---------------------------------------------------------------- las pilastras

    /**
     * La pilastra [r] ([izq]: la de la izquierda): la cara de roble con sus cinco estrías (las de abajo, rellenas con su junquillo),
     * los listeles de los bordes, el capitel bajo la cornisa (el ábaco, el equino y el collarino) y la basa encima del zócalo.
     */
    fun DrawScope.costado(r: Rect, izq: Boolean, d: DisposicionEstante, s: SitioBotanico) {
        val w = size.width; val u = density
        if (r.height < 2f || r.width < 2f) return
        val nv = if (s.a.lumbre) (r.height / (w * 0.05f)).toInt().coerceIn(1, 120) else 1
        // La cara, por tramos a lo alto (de noche la luz cambia con la altura).
        for (i in 0 until nv) {
            val y0 = r.top + r.height * i / nv - 1f; val y1 = r.top + r.height * (i + 1) / nv + 1f
            val t = s.t(Offset(r.center.x, (y0 + y1) / 2), true)
            drawRect(t.c(P.ROBLE, if (izq) 0.04f else -0.04f), Offset(r.left - 1f, y0), Size(r.width + 2f, y1 - y0))
        }
        val capitel = w * 0.06f
        val basa = w * 0.045f
        val c0 = r.top + capitel; val c1 = r.bottom - basa
        // Los listeles de los bordes y las estrías, por tramos.
        val margen = r.width * 0.14f
        val estrias = 5
        val ancho = (r.width - 2 * margen) / estrias
        val junquillo = c1 - (c1 - c0) * 0.3f
        for (i in 0 until nv) {
            val y0 = maxOf(c0 + w * 0.012f, r.top + r.height * i / nv); val y1 = minOf(c1 - w * 0.01f, r.top + r.height * (i + 1) / nv + 1f)
            if (y1 <= y0) continue
            val t = s.t(Offset(r.center.x, (y0 + y1) / 2), true)
            // La luz viene de la izquierda (de día) o de la lumbre (de noche): cada estría, honda del lado que no la mira.
            val luzDer = s.a.oscuro && r.center.x / w < MuebleEstanteBotanico.LAMP_X && t.k > 0.2f
            for (e in 0 until estrias) {
                val x = r.left + margen + ancho * e
                val relleno = y1 > junquillo
                val ya = maxOf(y0, if (relleno) junquillo else y0)
                if (y0 < junquillo) {
                    val yb = minOf(y1, junquillo)
                    drawRect(t.c(P.ROBLE_OSC, -0.05f), Offset(x + ancho * 0.18f, y0), Size(ancho * 0.64f, yb - y0))
                    drawRect(t.s(0.3f), Offset(if (luzDer) x + ancho * 0.6f else x + ancho * 0.18f, y0), Size(ancho * 0.22f, yb - y0))
                    drawRect(t.b(0.4f), Offset(if (luzDer) x + ancho * 0.18f else x + ancho * 0.66f, y0), Size(ancho * 0.16f, yb - y0))
                }
                if (relleno && y1 > ya) {
                    // El junquillo: un cilindro dentro de la estría, en el tercio de abajo.
                    drawRect(t.c(P.ROBLE, 0.02f), Offset(x + ancho * 0.2f, ya), Size(ancho * 0.6f, y1 - ya))
                    drawRect(t.b(0.45f), Offset(if (luzDer) x + ancho * 0.55f else x + ancho * 0.26f, ya), Size(ancho * 0.14f, y1 - ya))
                    drawRect(t.s(0.3f), Offset(if (luzDer) x + ancho * 0.2f else x + ancho * 0.62f, ya), Size(ancho * 0.18f, y1 - ya))
                }
            }
        }
        // Los remates redondos de las estrías, arriba y donde empieza el junquillo.
        val tc = s.t(r.center, true)
        for (e in 0 until estrias) {
            val cx = r.left + margen + ancho * (e + 0.5f)
            val ta = s.t(Offset(cx, c0), true); val tj = s.t(Offset(cx, junquillo), true)
            drawArc(ta.c(P.ROBLE_OSC, -0.05f), 180f, 180f, true, Offset(cx - ancho * 0.32f, c0 + w * 0.012f - ancho * 0.32f), Size(ancho * 0.64f, ancho * 0.64f))
            drawArc(tj.c(P.ROBLE, 0.04f), 180f, 180f, true, Offset(cx - ancho * 0.3f, junquillo - ancho * 0.3f), Size(ancho * 0.6f, ancho * 0.6f))
            drawArc(tj.b(0.5f), 200f, 80f, false, Offset(cx - ancho * 0.3f, junquillo - ancho * 0.3f), Size(ancho * 0.6f, ancho * 0.6f), style = Stroke(0.6f * u))
        }
        // La veta y el canto: el de fuera, con su filo de luz (o de lumbre).
        vetas(Rect(r.left, c0, r.left + margen, c1), tc.c(P.ROBLE_OSC).copy(alpha = 0.3f), if (izq) 9801 else 9802, 0.9f, vertical = true, grosor = 0.5f)
        vetas(Rect(r.right - margen, c0, r.right, c1), tc.c(P.ROBLE_OSC).copy(alpha = 0.3f), if (izq) 9803 else 9804, 0.9f, vertical = true, grosor = 0.5f)
        for (i in 0 until nv) {
            val y0 = r.top + r.height * i / nv; val y1 = r.top + r.height * (i + 1) / nv
            val t = s.t(Offset(r.center.x, (y0 + y1) / 2), true)
            drawRect(t.b(if (izq) 0.55f else 0.25f), Offset(r.left, y0), Size(1.3f * u, y1 - y0))
            drawRect(t.s(if (izq) 0.3f else 0.45f), Offset(r.right - 1.6f * u, y0), Size(1.6f * u, y1 - y0))
            if (s.a.oscuro) {
                val der = r.center.x / w < MuebleEstanteBotanico.LAMP_X
                drawRect(t.filo(0.55f), Offset(if (der) r.right - 1.5f * u else r.left, y0), Size(1.5f * u, y1 - y0))
            }
        }
        // El capitel: el ábaco (una tabla que sobresale), el equino redondo y el collarino; con su sombra en la cara.
        val tcap = s.t(Offset(r.center.x, r.top + capitel / 2), true)
        val ab = Rect(r.left - 1f, r.top, r.right + 1f, r.top + capitel * 0.32f)
        val eq = Rect(r.left + r.width * 0.04f, ab.bottom, r.right - r.width * 0.04f, r.top + capitel * 0.62f)
        val co = Rect(r.left + r.width * 0.08f, eq.bottom + capitel * 0.08f, r.right - r.width * 0.08f, r.top + capitel * 0.82f)
        drawRect(tcap.s(0.4f), Offset(r.left, c0), Size(r.width, w * 0.012f))
        drawRect(tcap.c(P.ROBLE_LUZ, 0.02f), ab.topLeft, ab.size)
        drawRect(tcap.b(0.5f), ab.topLeft, Size(ab.width, 1f * u))
        drawRect(tcap.s(0.45f), Offset(ab.left, ab.bottom - 1f * u), Size(ab.width, 1f * u))
        drawRect(tcap.c(P.ROBLE, 0.06f), eq.topLeft, eq.size)
        drawRect(tcap.b(0.35f), eq.topLeft, Size(eq.width, eq.height * 0.35f))
        drawRect(tcap.s(0.3f), Offset(eq.left, eq.bottom - eq.height * 0.35f), Size(eq.width, eq.height * 0.35f))
        drawRect(tcap.c(P.ROBLE_OSC, 0f), Offset(co.left, eq.bottom), Size(co.width, co.top - eq.bottom))
        drawRect(tcap.c(P.ROBLE, 0.02f), co.topLeft, co.size)
        drawRect(tcap.b(0.4f), co.topLeft, Size(co.width, 0.8f * u))
        drawRect(tcap.s(0.4f), Offset(co.left, co.bottom - 1f * u), Size(co.width, 1f * u))
        // La basa: el plinto y el toro, encima del zócalo.
        val tb = s.t(Offset(r.center.x, r.bottom - basa / 2), true)
        val toro = Rect(r.left + r.width * 0.02f, c1, r.right - r.width * 0.02f, c1 + basa * 0.42f)
        val plinto = Rect(r.left - 1f, toro.bottom, r.right + 1f, r.bottom + 1f)
        drawRect(tb.c(P.ROBLE, 0.04f), toro.topLeft, toro.size)
        drawRect(tb.b(0.45f), toro.topLeft, Size(toro.width, toro.height * 0.3f))
        drawRect(tb.s(0.35f), Offset(toro.left, toro.bottom - toro.height * 0.35f), Size(toro.width, toro.height * 0.35f))
        drawRect(tb.c(P.ROBLE, -0.02f), plinto.topLeft, plinto.size)
        drawRect(tb.b(0.4f), plinto.topLeft, Size(plinto.width, 1f * u))
        drawRect(tb.s(0.4f), Offset(plinto.left, plinto.bottom - 2f * u), Size(plinto.width, 2f * u))
        // Las rozaduras del uso, a la altura de la mano.
        for (j in 0 until (r.height / (w * 0.18f)).toInt().coerceIn(1, 40)) {
            val y = r.top + r.height * azar(9900 + (if (izq) 0 else 1), j)
            val x = r.left + r.width * (0.15f + 0.6f * azar(9901, j))
            drawLine(s.t(Offset(x, y), true).b(0.22f), Offset(x, y), Offset(x + w * 0.008f, y + w * 0.014f), 0.8f * u, StrokeCap.Round)
        }
    }

    // ---------------------------------------------------------------- los portaetiquetas de los meses

    /** El portaetiquetas de latón de un mes (el de los cajoncitos del gabinete), con su tarjeta y sus dos tornillos. */
    fun DrawScope.placa(p: PlacaEstante, medidor: TextMeasurer, s: SitioBotanico) {
        val u = density
        val r0 = p.r
        // Con un rótulo largo (el mes del tema y el común), el portaetiquetas se alarga lo que haga falta, hasta media tabla más.
        val largo = (p.texto.length - 14).coerceIn(0, 22) / 22f
        val mas = r0.width * 0.3f * largo
        val r = Rect(r0.left - mas + r0.width * 0.05f, r0.top - 0.5f * u, r0.right + mas - r0.width * 0.05f, r0.bottom + 0.5f * u)
        val t = s.t(r.center, true)
        drawRect(t.s(0.45f), r.topLeft + Offset(1.4f * u, 1.8f * u), r.size)
        drawRect(t.c(P.LATON_OSC), r.topLeft, r.size)
        drawRect(t.c(P.LATON), r.topLeft + Offset(0.8f * u, 0.8f * u), Size(r.width - 1.6f * u, r.height - 1.6f * u))
        drawRect(t.b(0.6f), r.topLeft + Offset(0.8f * u, 0.8f * u), Size(r.width - 1.6f * u, 0.9f * u))
        val tj = Rect(r.left + 5f * u, r.top + 2.4f * u, r.right - 5f * u, r.bottom - 2.2f * u)
        drawRect(t.c(P.LATON_OSC), tj.topLeft - Offset(0.6f * u, 0.6f * u), Size(tj.width + 1.2f * u, tj.height + 1.2f * u))
        drawRect(t.etiqueta(P.CARTULINA), tj.topLeft, tj.size)
        drawRect(t.s(0.22f), tj.topLeft, Size(tj.width, tj.height * 0.2f))
        drawRect(t.etiqueta(P.CARTULINA_OSC).copy(alpha = 0.35f), Offset(tj.left, tj.bottom - tj.height * 0.15f), Size(tj.width, tj.height * 0.15f))
        listOf(r.left + 2.5f * u, r.right - 2.5f * u).forEach { x -> clavoBotanico(Offset(x, r.center.y), 1.2f * u, t, P.LATON_OSC) }
        textoEn(medidor, p.texto, Rect(tj.left + 3f * u, tj.top + 0.6f * u, tj.right - 3f * u, tj.bottom), P.LETRA, t.tinta(), 10.5f, espaciado = 0.5f - 0.4f * largo, minimo = 7f)
    }

    // ---------------------------------------------------------------- el zócalo

    /** El zócalo de [r] (de debajo de la última tabla hasta el piso): la moldura de arriba, la faja con su filete y los resaltos. */
    fun DrawScope.zocalo(r: Rect, s: SitioBotanico) {
        val w = size.width; val u = density
        val n = tramos(s, w)
        val mold = r.top + (r.bottom - r.top) * 0.22f
        for (i in 0 until n) {
            val x0 = w * i / n; val x1 = w * (i + 1) / n + 1f
            val t = s.t(Offset((x0 + x1) / 2, r.center.y), true)
            drawRect(t.c(P.ROBLE, -0.04f), Offset(x0, r.top), Size(x1 - x0, r.height + 1f))
            drawRect(t.c(P.ROBLE_LUZ, 0f), Offset(x0, r.top), Size(x1 - x0, (mold - r.top) * 0.5f))
            drawRect(t.c(P.ROBLE_OSC, 0f), Offset(x0, r.top + (mold - r.top) * 0.5f), Size(x1 - x0, (mold - r.top) * 0.5f))
        }
        val tc = s.t(r.center, true)
        vetas(Rect(0f, mold, w, r.bottom), tc.c(P.ROBLE_OSC).copy(alpha = 0.32f), 9950, 0.7f, grosor = 0.5f, ondulacion = 0.5f)
        drawRect(tc.s(0.45f), Offset(0f, r.top), Size(w, 2.6f * u))
        drawRect(tc.b(0.4f), Offset(0f, r.top + 2.6f * u), Size(w, 0.9f * u))
        drawRect(tc.s(0.35f), Offset(0f, mold), Size(w, 1.2f * u))
        val fl = Rect(w * 0.11f, mold + (r.bottom - mold) * 0.35f, w * 0.89f, r.bottom - (r.bottom - mold) * 0.3f)
        drawRect(tc.c(P.NOGAL), fl.topLeft, fl.size, style = Stroke(1.2f * u))
        // Los resaltos bajo las pilastras.
        val lw = w * MuebleEstanteBotanico.LADO
        listOf(0f, w - lw).forEach { x ->
            val t = s.t(Offset(x + lw / 2, r.center.y), true)
            drawRect(t.c(P.ROBLE, 0.03f), Offset(x, mold), Size(lw, r.bottom - mold))
            drawRect(t.b(0.35f), Offset(x, mold), Size(1f * u, r.bottom - mold))
            drawRect(t.s(0.4f), Offset(x + lw - 1.4f * u, mold), Size(1.4f * u, r.bottom - mold))
        }
        // Las marcas de los zapatos y de la escoba, abajo.
        for (j in 0 until 10) {
            val x = w * (0.08f + 0.84f * azar(9960, j))
            val y = r.bottom - (r.bottom - mold) * (0.1f + 0.3f * azar(9961, j))
            drawLine(tc.s(0.18f), Offset(x, y), Offset(x + w * (0.02f + 0.03f * azar(9962, j)), y + 0.4f * u), 1f * u, StrokeCap.Round)
        }
        drawRect(tc.s(0.5f), Offset(0f, r.bottom - 1.6f * u), Size(w, 1.6f * u))
    }
}
