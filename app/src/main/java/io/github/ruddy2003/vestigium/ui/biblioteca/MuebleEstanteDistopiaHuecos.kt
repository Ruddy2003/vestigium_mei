package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDistopia as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo que llena los huecos de las repisas donde no llegan las entradas: grupos de dos o tres cosas del archivo, solapadas, que
 * llenan del 70 al 90 % del alto del hueco y cambian de un hueco a otro. El fichero de cajón sobre dos libros registro, con la
 * perforadora; las carpetas tumbadas con la grapadora y el bote de cola; el termo y la fiambrera del funcionario; el bloque de
 * impresos en blanco con el rollo de balduque y la botella de tinta; la caja de archivo de pie con la bandeja de alambre. En un
 * hueco muy ancho, dos grupos.
 */
internal object HuecosEstanteDistopia {
    fun DrawScope.hueco(k: Int, r: Rect, ocupado: Float, g: MarcoDistopia) {
        val w = g.w
        val x0 = ocupado + w * 0.014f
        val libre = r.right - x0 - w * 0.026f
        if (libre < w * 0.12f) return
        // El grupo de cada hueco sale de su repisa (es lo que acompaña, no una entrada); la sexta no repite la primera.
        val orden = intArrayOf(0, 2, 4, 1, 3)
        val i = Math.floorMod(2 * k + k / 5, 5)
        if (libre > w * 0.5f) {
            val mitad = libre * 0.5f
            grupo(orden[i], Rect(x0, r.top, x0 + mitad, r.bottom), g)
            grupo(orden[Math.floorMod(i + 2, 5)], Rect(x0 + mitad + w * 0.012f, r.top, r.right - w * 0.026f, r.bottom), g)
        } else grupo(orden[i], Rect(x0, r.top, x0 + libre, r.bottom), g)
    }

    private fun DrawScope.grupo(cual: Int, r: Rect, g: MarcoDistopia) {
        val w = g.w
        val an = minOf(r.width, w * 0.3f)
        val z = Rect(r.left, r.top, r.left + an, r.bottom)
        val l = g.luz(z.center.x, z.top + z.height * 0.55f, 0.05f)
        val hacia = g.hacia(z.center.x, z.center.y)
        when (cual) {
            0 -> ficheroConPerforadora(z, l, hacia, g)
            1 -> carpetasConGrapadora(z, l, hacia, g)
            2 -> termoConFiambrera(z, l, hacia, g)
            3 -> impresosConBalduque(z, l, hacia, g)
            else -> cajaConBandeja(z, l, hacia, g)
        }
    }

    /** La sombra que una cosa echa en el fondo de chapa (lejos de la luz) y la de contacto en la balda. */
    private fun DrawScope.sombras(f: Path, x0: Float, x1: Float, pie: Float, l: LuzDistopia, hacia: Float) {
        val u = density
        drawPath(corridoDistopia(f, -hacia * 6f * u, if (l.a.oscuro) -1f * u else 3f * u), l.s(if (l.a.oscuro) 0.45f else 0.36f))
        drawOval(l.s(0.55f), Offset(x0 - hacia * 2f * u, pie - 2.6f * u), Size(x1 - x0 + 4f * u, 4.4f * u))
    }

    private fun rr(r: Rect, c: Float) = Path().apply { addRoundRect(RoundRect(r, CornerRadius(c))) }

    // ---------------------------------------------------------------- el fichero sobre los libros registro, y la perforadora

    private fun DrawScope.ficheroConPerforadora(z: Rect, l: LuzDistopia, hacia: Float, g: MarcoDistopia) {
        val w = g.w; val u = density
        val pie = z.bottom
        val an = minOf(z.width * 0.78f, w * 0.17f)
        val x0 = z.left
        // Los dos libros registro tumbados: tela verde botella y negra, con las cantoneras de piel y las hojas a la vista.
        val telas = listOf(Color(0xFF2E4A3C), Color(0xFF2A2A2E))
        var y = pie
        val silueta = Path()
        for (i in 0 until 2) {
            val al = z.height * (0.13f + 0.02f * i)
            val dx = if (i == 1) an * 0.05f else 0f
            val lomo = Rect(x0 + dx, y - al, x0 + an - dx * 0.4f, y)
            silueta.addRect(lomo)
            y -= al
        }
        val fichero = Rect(x0 + an * 0.06f, y - z.height * 0.36f, x0 + an * 0.9f, y)
        silueta.addRect(Rect(fichero.left, fichero.top - z.height * 0.12f, fichero.right, fichero.bottom))
        sombras(silueta, x0, x0 + an, pie, l, hacia)
        y = pie
        for (i in 0 until 2) {
            val al = z.height * (0.13f + 0.02f * i)
            val dx = if (i == 1) an * 0.05f else 0f
            val lomo = Rect(x0 + dx, y - al, x0 + an - dx * 0.4f, y)
            val tela = l.c(telas[i])
            drawRoundRect(Brush.verticalGradient(listOf(mezcla(tela, Color.White, 0.14f), tela, mezcla(tela, l.a.hondo, 0.35f)), lomo.top, lomo.bottom), lomo.topLeft, lomo.size, CornerRadius(al * 0.25f))
            // Las hojas del canto, del lado de la luz, y las cantoneras de piel.
            val hx = if (hacia < 0f) lomo.left + lomo.width * 0.04f else lomo.right - lomo.width * 0.12f
            drawRect(l.c(mezcla(P.HUESO, P.CARPETA, 0.3f)), Offset(hx, lomo.top + al * 0.16f), Size(lomo.width * 0.08f, al * 0.68f))
            listOf(lomo.left, lomo.right - lomo.width * 0.14f).forEach { cx -> drawRect(l.c(mezcla(P.MADERA, P.BAQUELITA, 0.3f)), Offset(cx, lomo.top), Size(lomo.width * 0.14f, al)) }
            drawLine(l.b(0.4f), Offset(lomo.left + 2f * u, lomo.top + 1f * u), Offset(lomo.right - 2f * u, lomo.top + 1f * u), 0.9f * u)
            drawLine(l.s(0.4f), Offset(lomo.left, lomo.bottom - 0.6f * u), Offset(lomo.right, lomo.bottom - 0.6f * u), 1f * u)
            if (l.k > 0.06f) drawLine(l.filo(0.7f), Offset(lomo.left, lomo.top + 1f), Offset(lomo.right, lomo.top + 1f), 1f * u)
            y -= al
        }
        // El fichero de cajón, de chapa verde oliva: el cajón algo sacado, con las fichas que asoman y sus pestañas de colores.
        val fichas = Path()
        val pestanas = Path()
        val nf = 9
        for (j in 0 until nf) {
            val fx = fichero.left + fichero.width * (0.08f + 0.84f * j / (nf - 1f))
            val alto = z.height * (0.06f + 0.05f * azar(8001, j))
            fichas.addRect(Rect(fx - fichero.width * 0.05f, fichero.top - alto, fx + fichero.width * 0.04f, fichero.top + 2f))
            if (j % 3 == 1) pestanas.addRect(Rect(fx - fichero.width * 0.05f, fichero.top - alto - z.height * 0.03f, fx - fichero.width * 0.01f, fichero.top - alto + 1f))
        }
        drawPath(fichas, l.c(mezcla(P.HUESO, P.CARPETA, 0.25f)))
        drawPath(pestanas, l.c(P.CARTEL))
        val caja = rr(fichero, 2f * u)
        planosDistopia(caja, l.c(mezcla(P.OLIVA, P.ACERO, 0.35f)), l, hacia, 0.18f, 0.26f)
        drawLine(l.b(0.6f), Offset(fichero.left + 1f * u, fichero.top + 1f * u), Offset(fichero.right - 1f * u, fichero.top + 1f * u), 1f * u)
        // El frente del cajón: el portaetiquetas con su tarjeta en blanco y el tirador de concha.
        val frente = Rect(fichero.left + fichero.width * 0.08f, fichero.top + fichero.height * 0.14f, fichero.right - fichero.width * 0.08f, fichero.bottom - fichero.height * 0.12f)
        drawRect(l.s(0.25f), frente.topLeft, Size(frente.width, 1.2f * u))
        drawRect(l.b(0.3f), Offset(frente.left, frente.bottom - 1f * u), Size(frente.width, 1f * u))
        val tj = Rect(frente.center.x - frente.width * 0.26f, frente.top + frente.height * 0.16f, frente.center.x + frente.width * 0.26f, frente.top + frente.height * 0.42f)
        drawRect(l.c(P.ACERO_LUZ), tj.inflate(1.4f * u).topLeft, tj.inflate(1.4f * u).size)
        drawRect(l.c(mezcla(P.HUESO, P.CARPETA, 0.3f)), tj.topLeft, tj.size)
        val ti = Rect(frente.center.x - frente.width * 0.16f, frente.top + frente.height * 0.6f, frente.center.x + frente.width * 0.16f, frente.top + frente.height * 0.78f)
        drawArc(l.c(P.ACERO_LUZ), 180f, 180f, true, ti.topLeft, Size(ti.width, ti.height * 2f))
        drawArc(l.b(0.7f), 200f, 70f, false, ti.topLeft, Size(ti.width, ti.height * 2f), style = Stroke(0.9f * u))
        drawLine(l.c(P.BAQUELITA), Offset(ti.left, ti.bottom), Offset(ti.right, ti.bottom), 1.2f * u)
        if (l.k > 0.06f) drawPath(bordeDistopia(caja, -hacia * 1.4f * u, 0f), l.filo(0.7f))
        // La perforadora de dos agujeros, delante a la derecha: base de hierro, la palanca curva y su muelle.
        val px = x0 + an * 0.78f
        val pw = minOf(z.width - an * 0.7f, w * 0.1f).coerceAtLeast(w * 0.07f)
        val pb = Rect(px, pie - z.height * 0.06f, px + pw, pie)
        val lp = g.luz(pb.center.x, pb.center.y, 0.06f)
        drawOval(lp.s(0.5f), Offset(pb.left - 1f * u, pie - 2.4f * u), Size(pb.width + 4f * u, 4f * u))
        drawRoundRect(lp.c(P.BAQUELITA), pb.topLeft, pb.size, CornerRadius(2f * u))
        drawLine(lp.b(0.5f), Offset(pb.left + 2f * u, pb.top + 1f * u), Offset(pb.right - 2f * u, pb.top + 1f * u), 0.8f * u)
        val palanca = Path().apply {
            moveTo(pb.left + pw * 0.08f, pb.top)
            cubicTo(pb.left + pw * 0.2f, pb.top - z.height * 0.12f, pb.left + pw * 0.7f, pb.top - z.height * 0.13f, pb.right - pw * 0.02f, pb.top - z.height * 0.06f)
            lineTo(pb.right - pw * 0.04f, pb.top - z.height * 0.025f)
            cubicTo(pb.left + pw * 0.66f, pb.top - z.height * 0.09f, pb.left + pw * 0.26f, pb.top - z.height * 0.08f, pb.left + pw * 0.2f, pb.top)
            close()
        }
        drawPath(corridoDistopia(palanca, -hacia * 2f * u, 2f * u), lp.s(0.4f))
        drawPath(palanca, lp.c(mezcla(P.ACERO_OSC, P.HIERRO, 0.4f)))
        drawPath(bordeDistopia(palanca, 0f, 1.4f * u), lp.b(0.6f))
        listOf(0.38f, 0.66f).forEach { f -> drawRect(lp.c(P.ACERO_LUZ), Offset(pb.left + pw * f, pb.top - z.height * 0.045f), Size(2.2f * u, z.height * 0.045f)) }
        if (lp.k > 0.06f) drawPath(bordeDistopia(palanca, -hacia * 1.2f * u, 0f), lp.filo(0.7f))
    }

    // ---------------------------------------------------------------- las carpetas tumbadas, la grapadora y el bote de cola

    private fun DrawScope.carpetasConGrapadora(z: Rect, l: LuzDistopia, hacia: Float, g: MarcoDistopia) {
        val w = g.w; val u = density
        val pie = z.bottom
        val an = minOf(z.width * 0.92f, w * 0.2f)
        val x0 = z.left
        // El bote de cola, de vidrio, detrás a la derecha, con su pincel.
        val bote = Rect(x0 + an * 0.62f, pie - z.height * 0.5f, x0 + an * 0.96f, pie - z.height * 0.0f)
        val lb = g.luz(bote.center.x, bote.center.y, 0.045f)
        val pincel0 = Offset(bote.center.x, bote.top + bote.height * 0.1f)
        val pincel1 = Offset(bote.center.x + bote.width * 0.42f, bote.top - z.height * 0.3f)
        val siluetaBote = Path().apply { addRect(bote); moveTo(pincel0.x, pincel0.y); lineTo(pincel1.x + 3f * u, pincel1.y); lineTo(pincel1.x, pincel1.y); close() }
        sombras(siluetaBote, bote.left, bote.right, pie, lb, hacia)
        drawLine(lb.c(P.MADERA), pincel0, pincel1, 2.6f * u, StrokeCap.Round)
        drawLine(lb.b(0.4f), pincel0 - Offset(0.8f * u, 0f), pincel1 - Offset(0.8f * u, 0f), 0.7f * u)
        val vidrio = rr(bote, bote.width * 0.18f)
        drawPath(vidrio, lb.c(mezcla(P.CARPETA, P.OXIDO, 0.35f)))
        // La cola dentro (ámbar espeso) hasta media altura, y el vidrio encima.
        drawRect(lb.c(mezcla(P.CARPETA, P.MADERA, 0.4f)), Offset(bote.left + 1.4f * u, bote.top + bote.height * 0.45f), Size(bote.width - 2.8f * u, bote.height * 0.5f))
        drawPath(vidrio, lb.c(P.ACERO_LUZ).copy(alpha = 0.25f))
        drawRect(lb.b(0.7f), Offset(if (hacia < 0f) bote.left + bote.width * 0.16f else bote.right - bote.width * 0.26f, bote.top + bote.height * 0.12f), Size(bote.width * 0.1f, bote.height * 0.7f))
        drawRect(lb.c(P.BAQUELITA), Offset(bote.left - 1f * u, bote.top), Size(bote.width + 2f * u, bote.height * 0.1f))
        drawRect(lb.c(P.HUESO).copy(alpha = 0.85f), Offset(bote.left, bote.top + bote.height * 0.22f), Size(bote.width, bote.height * 0.16f))
        // La pila de carpetas de manila tumbadas, desalineadas, con hojas que asoman.
        val n = 7
        val th = z.height * 0.05f
        val silueta = Path().apply { addRect(Rect(x0, pie - th * n, x0 + an * 0.74f, pie)) }
        sombras(silueta, x0, x0 + an * 0.74f, pie, l, hacia)
        for (i in 0 until n) {
            val yb = pie - i * th
            val dx = (azar(8100, i) - 0.5f) * an * 0.06f
            val col = if (i == 2) mezcla(P.CARPETA, P.CARTEL, 0.35f) else if (i == 5) mezcla(P.CARPETA, P.FONDO, 0.35f) else mezcla(P.CARPETA, P.CARTON, 0.3f * azar(8101, i))
            val r = Rect(x0 + dx, yb - th, x0 + an * 0.74f + dx, yb)
            if (azar(8102, i) < 0.55f) drawRect(l.c(P.HUESO), Offset(r.right - an * 0.02f, r.top + th * 0.25f), Size(an * 0.05f, th * 0.45f))
            drawRect(l.c(col), r.topLeft, r.size)
            drawRect(l.s(0.35f), Offset(r.left, r.bottom - 1.1f * u), Size(r.width, 1.1f * u))
            drawRect(l.b(0.4f), r.topLeft, Size(r.width, 0.8f * u))
            drawRect(l.s(0.18f), Offset(if (hacia < 0f) r.right - r.width * 0.22f else r.left, r.top), Size(r.width * 0.22f, r.height))
            if (l.k > 0.06f) drawLine(l.filo(0.6f), Offset(r.left, r.top + 0.5f), Offset(r.right, r.top + 0.5f), 0.9f * u)
        }
        // La grapadora encima: el cuerpo de hierro esmaltado, el yunque de abajo y la cabeza con su muelle.
        val top = pie - th * n
        val gr = Rect(x0 + an * 0.08f, top - z.height * 0.085f, x0 + an * 0.6f, top)
        val lg = g.luz(gr.center.x, gr.center.y, 0.05f)
        drawOval(lg.s(0.5f), Offset(gr.left, top - 2f * u), Size(gr.width + 3f * u, 3.6f * u))
        drawRoundRect(lg.c(P.ACERO_OSC), Offset(gr.left, top - gr.height * 0.25f), Size(gr.width, gr.height * 0.25f), CornerRadius(1.6f * u))
        val cuerpo = Path().apply {
            moveTo(gr.left + gr.width * 0.02f, top - gr.height * 0.3f)
            lineTo(gr.left + gr.width * 0.06f, gr.top + gr.height * 0.18f)
            quadraticTo(gr.left + gr.width * 0.1f, gr.top, gr.left + gr.width * 0.3f, gr.top)
            lineTo(gr.right - gr.width * 0.06f, gr.top + gr.height * 0.12f)
            quadraticTo(gr.right, gr.top + gr.height * 0.2f, gr.right - gr.width * 0.02f, top - gr.height * 0.3f)
            close()
        }
        planosDistopia(cuerpo, lg.c(mezcla(P.ESMALTE, P.BAQUELITA, 0.3f)), lg, hacia, 0.2f, 0.3f)
        drawPath(bordeDistopia(cuerpo, 0f, 1.6f * u), lg.b(0.55f))
        drawCircle(lg.c(P.ACERO_LUZ), 1.4f * u, Offset(gr.right - gr.width * 0.12f, top - gr.height * 0.45f))
        if (lg.k > 0.06f) drawPath(bordeDistopia(cuerpo, -hacia * 1.2f * u, 0f), lg.filo(0.7f))
    }

    // ---------------------------------------------------------------- el termo y la fiambrera

    private fun DrawScope.termoConFiambrera(z: Rect, l: LuzDistopia, hacia: Float, g: MarcoDistopia) {
        val w = g.w; val u = density
        val pie = z.bottom
        val x0 = z.left
        // El termo: cuerpo de chapa esmaltada roja de cartel, el vaso-tapón de aluminio, la boca y el asa de alambre.
        val tw = minOf(z.width * 0.32f, w * 0.062f)
        val th = z.height * 0.8f
        val termo = Rect(x0 + tw * 0.2f, pie - th, x0 + tw * 1.2f, pie)
        val lt = g.luz(termo.center.x, termo.center.y, 0.05f)
        val cuerpoT = Rect(termo.left, termo.top + th * 0.26f, termo.right, termo.bottom)
        val vaso = Rect(termo.left - tw * 0.04f, termo.top + th * 0.06f, termo.right + tw * 0.04f, termo.top + th * 0.27f)
        val silueta = Path().apply { addRect(cuerpoT); addRect(vaso) }
        sombras(silueta, termo.left, termo.right, pie, lt, hacia)
        val ct = rr(cuerpoT, tw * 0.18f)
        cilindroDistopia(ct, lt.c(mezcla(P.CARTEL, P.BURDEOS, 0.3f)), lt, hacia, 0.5f)
        // La faja de abajo y la de arriba, de aluminio, y la pintura saltada.
        drawRect(lt.c(P.ACERO_LUZ), Offset(cuerpoT.left, cuerpoT.bottom - th * 0.08f), Size(cuerpoT.width, th * 0.06f))
        drawRect(lt.c(P.ACERO_LUZ), Offset(cuerpoT.left, cuerpoT.top), Size(cuerpoT.width, th * 0.05f))
        saltadoDistopia(Offset(cuerpoT.left + tw * 0.3f, cuerpoT.top + th * 0.4f), tw * 0.12f, 8201, lt.c(P.HIERRO), lt.b(0.5f))
        saltadoDistopia(Offset(cuerpoT.right - tw * 0.25f, cuerpoT.top + th * 0.58f), tw * 0.09f, 8202, lt.c(P.HIERRO), lt.b(0.5f))
        // El asa de alambre a un lado.
        drawArc(lt.c(P.ACERO_LUZ), -80f, 160f, false, Offset(cuerpoT.right - tw * 0.2f, cuerpoT.top + th * 0.12f), Size(tw * 0.5f, th * 0.4f), style = Stroke(1.3f * u))
        // El vaso-tapón, de aluminio acanalado, y su boca.
        val cv = rr(vaso, tw * 0.08f)
        cilindroDistopia(cv, lt.c(P.ACERO_LUZ), lt, hacia, 0.7f)
        for (j in 1 until 4) drawLine(lt.s(0.25f), Offset(vaso.left, vaso.top + vaso.height * j / 4f), Offset(vaso.right, vaso.top + vaso.height * j / 4f), 0.7f * u)
        drawRoundRect(lt.c(P.ACERO), Offset(termo.left + tw * 0.25f, termo.top), Size(tw * 0.5f, th * 0.07f), CornerRadius(1.4f * u))
        if (lt.k > 0.06f) drawPath(bordeDistopia(ct, -hacia * 1.4f * u, 0f), lt.filo(0.7f))
        // La fiambrera de lata, delante a la derecha: el cuerpo de esquinas redondas, la tapa con su reborde y el cierre de alambre.
        val fw = minOf(z.width - tw * 1.1f, w * 0.15f).coerceAtLeast(w * 0.08f)
        val fh = z.height * 0.34f
        val f = Rect(termo.right - tw * 0.15f, pie - fh, termo.right - tw * 0.15f + fw, pie)
        val lf = g.luz(f.center.x, f.center.y, 0.06f)
        val cf = rr(f, fh * 0.16f)
        sombras(cf, f.left, f.right, pie, lf, hacia)
        planosDistopia(cf, lf.c(mezcla(P.ACERO_LUZ, P.OLIVA, 0.3f)), lf, hacia, 0.16f, 0.24f)
        val tapa = Rect(f.left - 1.2f * u, f.top - 1f * u, f.right + 1.2f * u, f.top + fh * 0.24f)
        drawRoundRect(lf.c(mezcla(P.ACERO_LUZ, P.OLIVA, 0.2f)), tapa.topLeft, tapa.size, CornerRadius(fh * 0.12f))
        drawLine(lf.b(0.7f), Offset(tapa.left + 3f * u, tapa.top + 1f * u), Offset(tapa.right - 3f * u, tapa.top + 1f * u), 1f * u)
        drawLine(lf.s(0.4f), Offset(tapa.left, tapa.bottom), Offset(tapa.right, tapa.bottom), 1.2f * u)
        // El cierre: el alambre que pasa por encima y su gancho al frente.
        val ce = Offset(f.center.x, tapa.bottom + fh * 0.12f)
        drawLine(lf.c(P.HIERRO), Offset(ce.x - fw * 0.06f, tapa.top), Offset(ce.x - fw * 0.06f, ce.y), 1.2f * u)
        drawLine(lf.c(P.HIERRO), Offset(ce.x + fw * 0.06f, tapa.top), Offset(ce.x + fw * 0.06f, ce.y), 1.2f * u)
        drawRoundRect(lf.c(P.ACERO_LUZ), Offset(ce.x - fw * 0.09f, ce.y - 1f * u), Size(fw * 0.18f, fh * 0.12f), CornerRadius(1f * u))
        // Las abolladuras y el óxido de la lata vieja.
        abolladuraDistopia(Offset(f.left + fw * 0.7f, f.top + fh * 0.62f), fw * 0.12f, fh * 0.14f, lf)
        drawOval(lf.c(P.OXIDO).copy(alpha = 0.35f), Offset(f.left + fw * 0.08f, f.bottom - fh * 0.22f), Size(fw * 0.22f, fh * 0.12f))
        if (lf.k > 0.06f) drawPath(bordeDistopia(cf, -hacia * 1.4f * u, 0f), lf.filo(0.7f))
    }

    // ---------------------------------------------------------------- los impresos, el balduque y la botella de tinta

    private fun DrawScope.impresosConBalduque(z: Rect, l: LuzDistopia, hacia: Float, g: MarcoDistopia) {
        val w = g.w; val u = density
        val pie = z.bottom
        val x0 = z.left
        // La botella de tinta de un litro, detrás a la izquierda: vidrio casi negro, el tapón de baquelita y la etiqueta lisa.
        val bw = minOf(z.width * 0.3f, w * 0.068f)
        val bh = z.height * 0.84f
        val bot = Rect(x0, pie - bh, x0 + bw, pie)
        val lb = g.luz(bot.center.x, bot.center.y, 0.045f)
        val botella = cerradaDistopia(listOf(Offset(bot.left + bw * 0.34f, bot.top + bh * 0.1f), Offset(bot.right - bw * 0.34f, bot.top + bh * 0.1f), Offset(bot.right - bw * 0.3f, bot.top + bh * 0.26f),
            Offset(bot.right, bot.top + bh * 0.42f), Offset(bot.right, pie - 2f * u), Offset(bot.left, pie - 2f * u), Offset(bot.left, bot.top + bh * 0.42f), Offset(bot.left + bw * 0.3f, bot.top + bh * 0.26f)))
        sombras(botella, bot.left, bot.right, pie, lb, hacia)
        planosDistopia(botella, lb.c(Color(0xFF243038)), lb, hacia, 0.2f, 0.3f)
        drawRect(lb.c(mezcla(P.HUESO, P.CARPETA, 0.3f)), Offset(bot.left + 1f * u, bot.top + bh * 0.52f), Size(bw - 2f * u, bh * 0.24f))
        drawRect(lb.c(P.FONDO), Offset(bot.left + 1f * u, bot.top + bh * 0.56f), Size(bw - 2f * u, 1f * u))
        drawRect(lb.b(0.8f), Offset(if (hacia < 0f) bot.left + bw * 0.16f else bot.right - bw * 0.24f, bot.top + bh * 0.42f), Size(bw * 0.08f, bh * 0.08f))
        drawRect(lb.b(0.7f), Offset(if (hacia < 0f) bot.left + bw * 0.16f else bot.right - bw * 0.24f, bot.top + bh * 0.8f), Size(bw * 0.08f, bh * 0.12f))
        drawRoundRect(lb.c(P.BAQUELITA), Offset(bot.left + bw * 0.28f, bot.top), Size(bw * 0.44f, bh * 0.12f), CornerRadius(1.6f * u))
        drawLine(lb.b(0.5f), Offset(bot.left + bw * 0.34f, bot.top + 1f * u), Offset(bot.right - bw * 0.34f, bot.top + 1f * u), 0.9f * u)
        if (lb.k > 0.06f) drawPath(bordeDistopia(botella, -hacia * 1.4f * u, 0f), lb.filo(0.7f))
        // El bloque de impresos en blanco, atado con cordel, delante.
        val iw = minOf(z.width - bw * 0.6f, w * 0.17f).coerceAtLeast(w * 0.09f)
        val ih = z.height * 0.4f
        val imp = Rect(x0 + bw * 0.6f, pie - ih, x0 + bw * 0.6f + iw, pie)
        val li = g.luz(imp.center.x, imp.center.y, 0.06f)
        val ci = Path().apply { addRect(imp) }
        sombras(ci, imp.left, imp.right, pie, li, hacia)
        drawRect(li.c(P.HUESO), imp.topLeft, imp.size)
        for (j in 0 until 9) {
            val y = imp.top + ih * (0.08f + 0.1f * j)
            drawLine(li.s(0.14f), Offset(imp.left, y), Offset(imp.right, y), 0.6f * u)
        }
        drawRect(li.s(0.22f), Offset(if (hacia < 0f) imp.right - iw * 0.14f else imp.left, imp.top), Size(iw * 0.14f, ih))
        drawRect(li.b(0.5f), imp.topLeft, Size(iw, 1f * u))
        // El cartón de abajo y el de arriba del bloque.
        drawRect(li.c(P.CARTON), Offset(imp.left, imp.bottom - ih * 0.08f), Size(iw, ih * 0.08f))
        drawRect(li.c(P.CARTON), imp.topLeft, Size(iw, ih * 0.07f))
        // El cordel en cruz.
        drawLine(li.c(mezcla(P.HUESO, P.CARPETA, 0.5f)), Offset(imp.left + iw * 0.3f, imp.top), Offset(imp.left + iw * 0.3f, imp.bottom), 1.3f * u)
        drawLine(li.c(mezcla(P.HUESO, P.CARPETA, 0.5f)), Offset(imp.left, imp.top + ih * 0.5f), Offset(imp.right, imp.top + ih * 0.5f), 1.3f * u)
        if (li.k > 0.06f) drawLine(li.filo(0.7f), Offset(imp.left, imp.top + 0.5f), Offset(imp.right, imp.top + 0.5f), 1f * u)
        // El rollo de balduque tumbado encima, con el cabo suelto que cae por delante del bloque.
        val rc = Offset(imp.left + iw * 0.62f, imp.top - z.height * 0.035f)
        val rx = minOf(iw * 0.3f, w * 0.042f); val ry = z.height * 0.035f
        drawOval(li.s(0.45f), Offset(rc.x - rx + 2f * u, imp.top - 2f * u), Size(rx * 2, 4f * u))
        drawRect(li.c(P.BURDEOS), Offset(rc.x - rx, rc.y), Size(rx * 2, ry))
        drawRect(li.c(P.CARTON), Offset(rc.x - rx, rc.y + ry * 0.8f), Size(rx * 2, ry * 0.2f))
        drawOval(li.c(P.CARTEL), Offset(rc.x - rx, rc.y - ry * 0.6f), Size(rx * 2, ry * 1.2f))
        drawOval(li.c(P.CARTON), Offset(rc.x - rx * 0.32f, rc.y - ry * 0.2f), Size(rx * 0.64f, ry * 0.4f))
        drawOval(li.b(0.45f), Offset(rc.x - rx * 0.8f, rc.y - ry * 0.45f), Size(rx * 0.6f, ry * 0.3f))
        val cabo = Path().apply {
            moveTo(rc.x + rx * 0.9f, rc.y + ry * 0.3f)
            cubicTo(rc.x + rx * 1.2f, imp.top + ih * 0.1f, rc.x + rx * 0.7f, imp.top + ih * 0.5f, rc.x + rx * 0.95f, imp.top + ih * 0.78f)
        }
        drawPath(corridoDistopia(cabo, -hacia * 1.4f * u, 1.4f * u), li.s(0.35f), style = Stroke(z.height * 0.03f))
        drawPath(cabo, li.c(P.CARTEL), style = Stroke(z.height * 0.03f, cap = StrokeCap.Butt))
        drawPath(cabo, li.b(0.35f), style = Stroke(0.8f * u))
    }

    // ---------------------------------------------------------------- la caja de archivo y la bandeja de alambre

    private fun DrawScope.cajaConBandeja(z: Rect, l: LuzDistopia, hacia: Float, g: MarcoDistopia) {
        val w = g.w; val u = density
        val pie = z.bottom
        val x0 = z.left
        // La caja de archivo de cartón, de pie: la tapa encajada arriba, el agujero para la mano y el tarjetero con su tarjeta.
        val cw = minOf(z.width * 0.5f, w * 0.12f)
        val chh = z.height * 0.86f
        val c = Rect(x0, pie - chh, x0 + cw, pie)
        val lc = g.luz(c.center.x, c.center.y, 0.045f)
        val forma = Path().apply { addRect(c) }
        sombras(forma, c.left, c.right, pie, lc, hacia)
        planosDistopia(forma, lc.c(mezcla(P.CARTON, P.OXIDO, 0.12f)), lc, hacia, 0.14f, 0.22f)
        // La tapa: una franja de arriba un poco más ancha, con su canto de luz y su sombra.
        drawRect(lc.c(mezcla(P.CARTON, P.HUESO, 0.15f)), Offset(c.left - 1f * u, c.top), Size(cw + 2f * u, chh * 0.1f))
        drawLine(lc.b(0.55f), Offset(c.left, c.top + 0.8f * u), Offset(c.right, c.top + 0.8f * u), 1f * u)
        drawRect(lc.s(0.35f), Offset(c.left, c.top + chh * 0.1f), Size(cw, 1.6f * u))
        // El agujero para la mano.
        drawRoundRect(lc.c(Color(0xFF241C14)), Offset(c.center.x - cw * 0.22f, c.top + chh * 0.17f), Size(cw * 0.44f, chh * 0.06f), CornerRadius(chh * 0.03f))
        drawLine(lc.b(0.4f), Offset(c.center.x - cw * 0.2f, c.top + chh * 0.235f), Offset(c.center.x + cw * 0.2f, c.top + chh * 0.235f), 0.8f * u)
        // La etiqueta de la caja: una tarjeta clara con la raya roja y renglones que no se leen.
        val et = Rect(c.left + cw * 0.18f, c.top + chh * 0.34f, c.right - cw * 0.18f, c.top + chh * 0.58f)
        drawRect(lc.s(0.25f), et.topLeft + Offset(1f * u, 1.2f * u), et.size)
        drawRect(lc.c(P.HUESO), et.topLeft, et.size)
        drawRect(lc.c(P.CARTEL), et.topLeft, Size(et.width, et.height * 0.14f))
        drawPath(renglonesDistopia(Rect(et.left + et.width * 0.1f, et.top + et.height * 0.3f, et.right - et.width * 0.1f, et.bottom - et.height * 0.08f), 0.0022f * w, 0.0075f * w, 8401),
            lc.c(P.BAQUELITA).copy(alpha = 0.5f))
        // Una esquina aplastada y la cinta de embalar vieja.
        drawPath(poligonoDistopia(Offset(c.right, c.bottom - chh * 0.12f), Offset(c.right - cw * 0.1f, c.bottom), Offset(c.right, c.bottom)), lc.s(0.35f))
        drawRect(lc.c(mezcla(P.CARPETA, P.HUESO, 0.3f)).copy(alpha = 0.7f), Offset(c.center.x - cw * 0.08f, c.top + chh * 0.66f), Size(cw * 0.16f, chh * 0.34f))
        if (lc.k > 0.06f) drawPath(bordeDistopia(forma, -hacia * 1.4f * u, 0f), lc.filo(0.7f))
        // La bandeja de correspondencia de alambre, de dos pisos, delante a la derecha, con papeles.
        val bw = minOf(z.width - cw * 0.85f, w * 0.15f).coerceAtLeast(w * 0.09f)
        val bh = z.height * 0.46f
        val b = Rect(c.right - cw * 0.15f, pie - bh, c.right - cw * 0.15f + bw, pie)
        val lb = g.luz(b.center.x, b.center.y, 0.06f)
        drawOval(lb.s(0.5f), Offset(b.left, pie - 2.4f * u), Size(bw + 4f * u, 4f * u))
        val alambre = lb.c(mezcla(P.HIERRO, P.ACERO_OSC, 0.4f))
        // Los papeles de cada piso, que sobresalen un poco.
        for (piso in 0 until 2) {
            val yb = pie - bh * (0.06f + 0.5f * piso)
            val pr = Rect(b.left + bw * 0.06f, yb - bh * 0.18f, b.right - bw * 0.02f, yb)
            drawRect(lb.c(if (piso == 0) mezcla(P.HUESO, P.CARPETA, 0.4f) else P.HUESO), pr.topLeft, pr.size)
            for (j in 0 until 3) drawLine(lb.s(0.18f), Offset(pr.left, pr.top + pr.height * (0.3f + 0.25f * j)), Offset(pr.right, pr.top + pr.height * (0.3f + 0.25f * j)), 0.6f * u)
            drawRect(lb.b(0.4f), pr.topLeft, Size(pr.width, 0.9f * u))
        }
        // El alambre: los bordes de cada piso, las patas y los barrotes del frente (paralelos, en un solo camino).
        val malla = Path()
        for (piso in 0 until 2) {
            val yb = pie - bh * (0.02f + 0.5f * piso)
            malla.moveTo(b.left, yb); malla.lineTo(b.right, yb)
            malla.moveTo(b.left, yb - bh * 0.16f); malla.lineTo(b.right, yb - bh * 0.16f)
            var x = b.left + bw * 0.08f
            while (x < b.right - 1f) { malla.moveTo(x, yb); malla.lineTo(x, yb - bh * 0.16f); x += bw * 0.12f }
        }
        malla.moveTo(b.left, pie); malla.lineTo(b.left, pie - bh)
        malla.moveTo(b.right, pie); malla.lineTo(b.right, pie - bh)
        drawPath(malla, alambre, style = Stroke(1f * u))
        drawLine(lb.b(0.45f), Offset(b.left, pie - bh * 0.18f - 1f * u), Offset(b.right, pie - bh * 0.18f - 1f * u), 0.7f * u)
        if (lb.k > 0.06f) drawPath(malla, lb.filo(0.4f), style = Stroke(0.6f * u))
    }
}
