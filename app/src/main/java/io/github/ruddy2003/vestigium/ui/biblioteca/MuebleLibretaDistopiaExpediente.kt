package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDistopia as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/** Los caminos de la tapa del expediente, armados una vez por tamaño (la tapa no cambia con los folios: crece hacia atrás). */
private class FormasExpedienteDistopia(val t: Rect, u: Float) {
    /** La pestaña de la carpeta, arriba a la derecha. */
    val pestana = Rect(t.left + t.width * 0.6f, t.top - 11f * u, t.left + t.width * 0.9f, t.top + 1f * u)
    /** La tapa con su pestaña, y lo que se gasta de sus cantos (claro por fuera, hondo por dentro). */
    val forma: Path
    val gastado: Path
    val hondo: Path
    /** Los filos: el canto de la izquierda (la luz) y el de la derecha (de noche, la luz fría). */
    val filoIzq: Path
    val filoDer: Path
    /** Dónde va el balduque, la etiqueta y el lazo. */
    val balduque = t.top + t.height * 0.7f
    val etiqueta = Rect(t.left + t.width * 0.12f, t.top + t.height * 0.2f, t.right - t.width * 0.12f, t.top + t.height * 0.47f)
    val nudo = Offset(t.left + t.width * 0.66f, balduque + 0.2f * u)
    /** La lazada del balduque y sus dos cabos sueltos (se trazan con el ancho de la cinta). */
    val lazada = Path()
    val cabos = Path()
    /** El clip de alambre de la papeleta, arriba a la izquierda. */
    val clip = Path()
    val clipX = t.left + t.width * 0.21f

    init {
        val rr = Path().apply { addRoundRect(RoundRect(t, CornerRadius(2.5f * u))) }
        val p = pestana
        val pest = Path().apply {
            moveTo(p.left - 4f * u, t.top + 1f * u)
            lineTo(p.left, p.top + 3f * u)
            quadraticTo(p.left, p.top, p.left + 3f * u, p.top)
            lineTo(p.right - 3f * u, p.top)
            quadraticTo(p.right, p.top, p.right, p.top + 3f * u)
            lineTo(p.right + 4f * u, t.top + 1f * u)
            close()
        }
        forma = Path().apply { op(rr, pest, PathOperation.Union) }
        gastado = bordeDistopia(forma, 3.5f * u, 3.5f * u)
        hondo = bordeDistopia(forma, -3f * u, -3f * u)
        filoIzq = bordeDistopia(forma, 1.8f * u, 0f)
        filoDer = bordeDistopia(forma, -1.6f * u, 0f)
        val k = nudo
        // Una sola lazada, ladeada hacia arriba a la izquierda, y dos cabos flojos, uno más largo: atado deprisa, no de regalo.
        lazada.apply {
            moveTo(k.x - 1f * u, k.y - 0.5f * u)
            cubicTo(k.x - 5f * u, k.y - 9f * u, k.x - 13.5f * u, k.y - 8.5f * u, k.x - 12f * u, k.y - 3.5f * u)
            cubicTo(k.x - 11f * u, k.y - 0.5f * u, k.x - 5f * u, k.y + 0.2f * u, k.x - 1f * u, k.y + 0.6f * u)
        }
        cabos.apply {
            moveTo(k.x - 0.5f * u, k.y + 1f * u)
            cubicTo(k.x - 3f * u, k.y + 6f * u, k.x - 7.5f * u, k.y + 10f * u, k.x - 6f * u, k.y + 17f * u)
            moveTo(k.x + 1f * u, k.y + 0.5f * u)
            cubicTo(k.x + 6f * u, k.y + 2.5f * u, k.x + 9f * u, k.y + 8f * u, k.x + 13f * u, k.y + 22f * u)
        }
        val x0 = clipX; val y0 = t.top - 5f * u
        clip.apply {
            moveTo(x0, y0 + 18f * u); lineTo(x0, y0 + 3f * u)
            cubicTo(x0, y0 - 0.6f * u, x0 + 6f * u, y0 - 0.6f * u, x0 + 6f * u, y0 + 3f * u)
            lineTo(x0 + 6f * u, y0 + 22f * u)
            cubicTo(x0 + 6f * u, y0 + 25f * u, x0 + 1.4f * u, y0 + 25f * u, x0 + 1.4f * u, y0 + 22f * u)
            lineTo(x0 + 1.4f * u, y0 + 6f * u)
            cubicTo(x0 + 1.4f * u, y0 + 4f * u, x0 + 4.4f * u, y0 + 4f * u, x0 + 4.4f * u, y0 + 6f * u)
            lineTo(x0 + 4.4f * u, y0 + 17f * u)
        }
    }
}

/**
 * El expediente de momentos, que se pinta en cada dibujo (engorda con los folios y se abre): la sombra en la chapa del atril, la
 * contratapa, el canto de los folios arriba y a la derecha (con las copias de color y las papeletas que asoman), el doblez del
 * lomo, la tapa de cartón manila con su pestaña, las rayas del doblez, la etiqueta del formulario con el nombre y la cuenta, el
 * balduque con su lazo, la papeleta del clip y el remiendo de cinta; y delante, el canal del atril donde descansa.
 */
internal object ExpedienteLibretaDistopia {
    /** El cartón manila de la carpeta (el de los legajos de la estantería) y el de la contratapa, algo más hondo. */
    private val MANILA = P.CARPETA
    private val MANILA_OSC = mezcla(P.CARPETA, P.KRAFT, 0.4f)
    /** Las copias de color (papel cebolla rosa, verde y amarillo) y el papel hueso de los folios. */
    private val COPIAS = listOf(Color(0xFFE8C4BC), Color(0xFFC9DCC4), Color(0xFFEBDDA0))
    private val FOLIO = mezcla(P.HUESO, P.CARPETA, 0.12f)
    private val CINTA = Color(0xFFE2C27A)

    private var formas: FormasExpedienteDistopia? = null

    private fun formas(t: Rect, u: Float): FormasExpedienteDistopia =
        formas?.takeIf { it.t == t } ?: FormasExpedienteDistopia(t, u).also { formas = it }

    fun DrawScope.expediente(e: EstadoLibreta, s: SitioLibretaDistopia, a: AireDistopia, medidor: TextMeasurer) {
        val U = s.U
        val t = s.tapa; val v = s.v
        val f = formas(t, U)
        val ab = e.abierto.coerceIn(0f, 1f)
        val lc = s.luz(a, t.center)
        // La sombra en la chapa del atril (de día, abajo a la derecha; de noche, a la derecha, lejos de la lámpara).
        val d = if (a.oscuro) Offset(8f * U, 2f * U) else Offset(5f * U, 5f * U)
        drawRect(lc.s(0.32f), Offset(t.left + v.x + d.x, t.top + v.y + d.y), Size(t.width + 2f * U, t.height - v.y - 10f * U))
        // La contratapa, detrás, algo más grande, y lo que asoma entre los folios.
        val atras = Rect(t.left + v.x, t.top + v.y - 1.5f * U, t.right + v.x + 1.5f * U, t.bottom + v.y)
        val ya = t.top + t.height * 0.4f
        drawRect(Brush.horizontalGradient(listOf(s.luz(a, Offset(atras.left, ya)).c(MANILA_OSC), s.luz(a, Offset(atras.right, ya)).c(MANILA_OSC)), atras.left, atras.right), atras.topLeft, atras.size)
        papeletas(e, s, a)
        cantos(e, s, a)
        lomo(s, a)
        if (ab > 0.01f) primeraHoja(s, a)
        val sx = 1f - 0.94f * ab
        scale(sx, 1f, Offset(t.left, t.center.y)) { tapa(e, s, a, f, medidor) }
        if (ab > 0.01f) drawRect(lc.s(0.4f * ab), Offset(t.left + t.width * sx, t.top), Size(8f * U, t.height))
        canal(s, a)
    }

    /** Las papeletas y las copias que asoman entre los folios, por la derecha y por arriba: más cuantos más folios. */
    private fun DrawScope.papeletas(e: EstadoLibreta, s: SitioLibretaDistopia, a: AireDistopia) {
        val U = s.U; val u = density
        val t = s.tapa; val v = s.v
        val n = if (e.hojas <= 0) 0 else (1 + e.hojas / 9).coerceAtMost(11)
        for (j in 0 until n) {
            val sem = 7300 + j * 11
            val fondo = 0.2f + 0.65f * azar(sem, 1)
            val papel = if (j % 3 == 1) P.HUESO else COPIAS[j % COPIAS.size]
            val asoma = (3f + 7f * azar(sem, 2)) * U
            if (j % 4 != 3) {
                // Por la derecha, sin pisar el balduque.
                var fy = 0.08f + 0.84f * azar(sem, 3)
                if (fy > 0.64f && fy < 0.76f) fy -= 0.14f
                val y = t.top + t.height * fy + v.y * fondo
                val alto = (5f + 9f * azar(sem, 4)) * U
                val x0 = t.right + v.x * fondo
                val l = s.luz(a, Offset(t.right + v.x, y))
                drawRect(l.s(0.3f), Offset(x0, y + 1.2f * U), Size(t.right + v.x - x0 + asoma, alto))
                drawRect(l.c(papel), Offset(x0, y), Size(t.right + v.x - x0 + asoma, alto))
                drawLine(l.c(mezcla(papel, P.KRAFT, 0.3f)), Offset(t.right + v.x + asoma, y), Offset(t.right + v.x + asoma, y + alto), 0.8f * u)
                if (a.oscuro) drawLine(l.frio(0.8f), Offset(t.right + v.x + asoma, y), Offset(t.right + v.x + asoma, y + alto), 0.8f * u)
            } else {
                // Por arriba.
                val x = t.left + t.width * (0.12f + 0.6f * azar(sem, 5)) + v.x * fondo
                val ancho = (8f + 14f * azar(sem, 6)) * U
                val y0 = t.top + v.y * fondo
                val l = s.luz(a, Offset(x, y0))
                drawRect(l.c(papel), Offset(x, t.top + v.y - asoma), Size(ancho, y0 - (t.top + v.y - asoma)))
                drawLine(l.b(0.5f), Offset(x, t.top + v.y - asoma), Offset(x + ancho, t.top + v.y - asoma), 0.8f * u)
            }
        }
    }

    /** El canto de los folios, a la derecha y arriba: el papel con las rayas de las hojas, alguna copia de color y el balduque que pasa. */
    private fun DrawScope.cantos(e: EstadoLibreta, s: SitioLibretaDistopia, a: AireDistopia) {
        val U = s.U; val u = density
        val t = s.tapa; val v = s.v
        val ld = s.luz(a, Offset(t.right + v.x * 0.5f, t.center.y))
        val lt = s.luz(a, Offset(t.center.x, t.top))
        val der = poligonoDistopia(Offset(t.right, t.top + 1.5f * U), Offset(t.right + v.x, t.top + v.y + 1.5f * U), Offset(t.right + v.x, t.bottom + v.y - 1f * U), Offset(t.right, t.bottom - 1f * U))
        val arr = poligonoDistopia(Offset(t.left + 1.5f * U, t.top), Offset(t.left + 1.5f * U + v.x, t.top + v.y), Offset(t.right + v.x, t.top + v.y), Offset(t.right, t.top))
        drawPath(der, ld.c(mezcla(FOLIO, P.KRAFT, 0.12f)))
        drawPath(arr, lt.c(FOLIO))
        if (!a.oscuro) drawPath(arr, lt.b(0.25f))
        // Las rayas de las hojas, más cuantas más hojas, y alguna copia de color.
        val rayas = Path()
        val copias = Path()
        val n = (3 + e.hojas / 6).coerceAtMost(18)
        for (k in 1 until n) {
            val fr = k / n.toFloat()
            val c = if (azar(7330, k) < 0.22f) copias else rayas
            c.moveTo(t.right + v.x * fr, t.top + 3f * U + v.y * fr); c.lineTo(t.right + v.x * fr, t.bottom - 3f * U + v.y * fr)
            c.moveTo(t.left + 4f * U + v.x * fr, t.top + v.y * fr); c.lineTo(t.right - 1f * U + v.x * fr, t.top + v.y * fr)
        }
        drawPath(rayas, ld.s(0.28f), style = Stroke(0.6f * u))
        drawPath(copias, ld.c(mezcla(COPIAS[0], COPIAS[2], 0.4f)), style = Stroke(1.1f * u))
        // El balduque da la vuelta por el canto de la derecha, y se hunde un poco en los folios.
        val yb = t.top + t.height * 0.7f
        val banda = poligonoDistopia(Offset(t.right, yb - 1.3f * U), Offset(t.right + v.x, yb - 1.3f * U + v.y), Offset(t.right + v.x, yb + 1.3f * U + v.y), Offset(t.right, yb + 1.3f * U))
        drawPath(banda, ld.c(mezcla(P.CARTEL, P.BURDEOS, 0.45f)))
        // De noche, la luz fría de la calle en el canto de la derecha y en el de arriba.
        if (a.oscuro) {
            drawLine(s.luz(a, Offset(t.right + v.x, t.top + 30f * U)).frio(0.9f), Offset(t.right + v.x, t.top + v.y + 2f * U), Offset(t.right + v.x, t.bottom + v.y - 2f * U), 1.2f * u)
            drawLine(s.luz(a, Offset(t.right, t.top)).frio(0.7f), Offset(t.left + 1.5f * U + v.x, t.top + v.y), Offset(t.right + v.x, t.top + v.y), 1f * u)
        }
    }

    /** El doblez del lomo de la carpeta, a la izquierda de la tapa: redondeado, con su luz. */
    private fun DrawScope.lomo(s: SitioLibretaDistopia, a: AireDistopia) {
        val U = s.U; val u = density
        val t = s.tapa
        val r = Rect(t.left - s.lomo, t.top + 1f * U, t.left + 1f * U, t.bottom - 0.5f * U)
        val l = s.luz(a, Offset(r.center.x, t.center.y))
        val c = l.c(MANILA_OSC)
        drawRoundRect(Brush.horizontalGradient(listOf(mezcla(c, a.hondo, 0.25f), mezcla(c, Color.White, if (a.oscuro) 0.05f else 0.12f), c), r.left, r.right), r.topLeft, r.size, CornerRadius(r.width * 0.5f))
        if (l.k > 0.05f) drawLine(l.filo(0.9f), Offset(r.left + 0.5f * U, r.top + 4f * U), Offset(r.left + 0.5f * U, r.bottom - 4f * U), 1.2f * u)
    }

    /**
     * La tapa de cartón manila, que gira sobre el lomo al abrirse: el color con su plano de luz y su plano de sombra, el desgaste
     * de los cantos, las rayas del doblez, el remiendo de cinta, la etiqueta, el balduque con su lazo y la papeleta del clip.
     */
    private fun DrawScope.tapa(e: EstadoLibreta, s: SitioLibretaDistopia, a: AireDistopia, f: FormasExpedienteDistopia, medidor: TextMeasurer) {
        val U = s.U; val u = density
        val t = s.tapa
        val y = f.etiqueta.center.y
        val li = s.luz(a, Offset(t.left, y)); val lm = s.luz(a, Offset(t.center.x, y)); val lr = s.luz(a, Offset(t.right, y))
        val ci = li.c(MANILA); val cm = lm.c(MANILA); val cr = lr.c(MANILA)
        // El cartón, alabeado por el grueso de los folios: un bisel de luz junto al doblez, el tono con la luz a lo ancho y el canto
        // de la derecha, que se dobla hacia atrás, más hondo.
        val claro = Color((ci.red * 1.08f + 0.04f).coerceAtMost(1f), (ci.green * 1.08f + 0.04f).coerceAtMost(1f), (ci.blue * 1.06f + 0.03f).coerceAtMost(1f), 1f)
        val honda = mezcla(cr, a.sombra(1f).copy(alpha = 1f), 0.16f)
        drawPath(f.forma, Brush.horizontalGradient(colorStops = arrayOf(0f to claro, 0.035f to claro, 0.06f to ci, 0.5f to cm, 0.93f to cr, 0.975f to honda, 1f to honda), startX = t.left, endX = t.right))
        clipPath(f.forma) {
            // De noche, lo alto de la tapa queda por encima de la boca de la tulipa: más hondo.
            if (a.oscuro) {
                val y1 = s.boca + 4f * U
                drawRect(Brush.verticalGradient(listOf(a.sombra(0.32f), a.sombra(0f)), t.top - 12f * U, y1), Offset(t.left, t.top - 12f * U), Size(t.width, y1 - t.top + 12f * U))
            }
            drawPath(f.gastado, lm.c(mezcla(MANILA, P.HUESO, 0.55f)).copy(alpha = 0.5f))
            drawPath(f.hondo, lm.c(mezcla(MANILA, P.KRAFT, 0.5f)).copy(alpha = 0.35f))
            // Las rayas del doblez, junto al lomo: hundidas, con su labio de luz.
            for (k in 0 until 2) {
                val x = t.left + (7f + 4f * k) * U
                drawLine(li.s(0.16f), Offset(x, t.top + 3f * U), Offset(x, t.bottom - 3f * U), 0.7f * u)
                drawLine(li.b(0.22f), Offset(x + 0.9f * U, t.top + 3f * U), Offset(x + 0.9f * U, t.bottom - 3f * U), 0.6f * u)
            }
            // Lo impreso en la carpeta, sin letra: los renglones de los campos bajo la etiqueta y la tirita en blanco de la pestaña.
            val tinta = lm.c(P.BAQUELITA).copy(alpha = if (a.oscuro) 0.3f else 0.22f)
            for (k in 0 until 3) {
                val y = f.etiqueta.bottom + (10f + 9f * k) * U
                drawLine(tinta, Offset(f.etiqueta.left + (if (k == 0) 0f else 16f * U), y), Offset(f.etiqueta.right, y), 0.7f * u)
            }
            val p = f.pestana
            drawRect(s.luz(a, p.center).c(P.HUESO), Offset(p.left + 4f * U, p.top + 3f * U), Size(p.width - 8f * U, 5f * U))
            drawRect(tinta, Offset(p.left + 4f * U, p.top + 3f * U), Size(p.width - 8f * U, 5f * U), style = Stroke(0.6f * u))
            // El remiendo: una tira de cinta engomada, amarillenta, en el canto de la derecha.
            rotate(8f, Offset(t.right - 4f * U, t.top + t.height * 0.55f)) {
                drawRect(lr.c(CINTA).copy(alpha = 0.55f), Offset(t.right - 9f * U, t.top + t.height * 0.55f - 11f * U), Size(12f * U, 22f * U))
            }
        }
        // Los filos: la luz en el canto de la izquierda (de día, el de arriba), la calle fría en el de la derecha.
        if (li.k > 0.05f) drawPath(f.filoIzq, li.filo(0.8f))
        if (a.oscuro) drawPath(f.filoDer, s.luz(a, Offset(t.right, t.top + 40f * U)).frio(0.75f))
        else drawLine(li.b(0.55f), Offset(t.left + 3f * U, t.top + 0.8f * U), Offset(f.pestana.left - 4f * U, t.top + 0.8f * U), 1.1f * u)
        etiqueta(e, s, a, f, medidor)
        balduque(s, a, f)
        papeleta(s, a, f)
    }

    /** La etiqueta del formulario, pegada: el papel hueso con su filete impreso, el nombre y, debajo, la cuenta de los folios. */
    private fun DrawScope.etiqueta(e: EstadoLibreta, s: SitioLibretaDistopia, a: AireDistopia, f: FormasExpedienteDistopia, medidor: TextMeasurer) {
        val U = s.U; val u = density
        val r = f.etiqueta
        val l = s.luz(a, r.center)
        drawRect(l.s(0.3f), Offset(r.left + 0.8f * U, r.top + 1.2f * U), r.size)
        val papel = l.c(P.HUESO)
        drawRect(papel, r.topLeft, r.size)
        val tinta = l.tinta(papel)
        val filete = r.deflate(2.6f * U)
        drawRect(tinta.copy(alpha = 0.55f), filete.topLeft, filete.size, style = Stroke(0.8f * u))
        val corte = r.top + r.height * 0.56f
        drawLine(tinta.copy(alpha = 0.45f), Offset(filete.left, corte), Offset(filete.right, corte), 0.7f * u)
        textoEn(medidor, e.nombre, Rect(filete.left + 4f * U, filete.top + 1f * U, filete.right - 4f * U, corte - 1f * U), P.LETRA, tinta, 15f, peso = FontWeight.Medium, minimo = 8f)
        // La cuenta, en dos renglones si trae su «desde» (en uno no cabe sin cortarse).
        val partir = e.cuenta.indexOf(" ·")
        val renglones = if (partir > 0) listOf(e.cuenta.substring(0, partir), e.cuenta.substring(partir + 2).trim()) else listOf(e.cuenta)
        val alto = (filete.bottom - 1f * U - (corte + 1f * U)) / renglones.size
        renglones.forEachIndexed { i, texto ->
            val y = corte + 1f * U + alto * i
            textoEn(medidor, texto, Rect(filete.left + 4f * U, y, filete.right - 4f * U, y + alto), P.LETRA_MES, tinta.copy(alpha = 0.9f), 8.5f, peso = FontWeight.SemiBold, minimo = 6f)
        }
        if (l.k > 0.06f) drawLine(l.filo(0.6f), Offset(r.left, r.top), Offset(r.left, r.bottom), 1f * u)
        if (a.oscuro) drawLine(s.luz(a, Offset(r.right, r.top)).frio(0.5f), Offset(r.right, r.top), Offset(r.right, r.bottom), 0.8f * u)
    }

    /** El balduque: la cinta roja, estrecha, que ciñe la carpeta, con su nudo, una lazada y los dos cabos sueltos. */
    private fun DrawScope.balduque(s: SitioLibretaDistopia, a: AireDistopia, f: FormasExpedienteDistopia) {
        val U = s.U; val u = density
        val t = s.tapa
        val yb = f.balduque
        val l = s.luz(a, f.nudo)
        val li = s.luz(a, Offset(t.left, yb)); val lr = s.luz(a, Offset(t.right, yb))
        // La cinta, con su sombra en la tapa.
        drawRect(l.s(0.35f), Offset(t.left - s.lomo, yb + 1.3f * U), Size(t.width + s.lomo, 1.3f * U))
        drawRect(Brush.horizontalGradient(listOf(li.c(P.CARTEL), l.c(P.CARTEL), lr.c(mezcla(P.CARTEL, P.BURDEOS, 0.3f))), t.left - s.lomo, t.right), Offset(t.left - s.lomo, yb - 1.3f * U), Size(t.width + s.lomo, 2.6f * U))
        drawLine(l.b(0.35f), Offset(t.left - s.lomo, yb - 0.9f * U), Offset(t.right, yb - 0.9f * U), 0.6f * u)
        // El nudo: la sombra, los cabos, la lazada y el nudo, todo con el ancho de la cinta.
        val cinta = Stroke(2.5f * U)
        val dx = if (a.oscuro) 2f * U else 1.3f * U
        drawPath(corridoDistopia(f.cabos, dx, 1.6f * U), l.s(0.38f), style = cinta)
        drawPath(corridoDistopia(f.lazada, dx, 1.6f * U), l.s(0.38f), style = cinta)
        drawPath(f.cabos, l.c(mezcla(P.CARTEL, P.BURDEOS, 0.25f)), style = cinta)
        drawPath(f.lazada, l.c(P.CARTEL), style = cinta)
        drawPath(corridoDistopia(f.lazada, -0.5f * U, -0.6f * U), l.b(0.3f), style = Stroke(0.6f * u))
        drawRoundRect(l.c(mezcla(P.CARTEL, P.BURDEOS, 0.4f)), Offset(f.nudo.x - 2.2f * U, f.nudo.y - 2f * U), Size(4.4f * U, 4f * U), CornerRadius(1.4f * U))
        if (l.k > 0.06f) drawPath(corridoDistopia(f.lazada, -0.8f * U, 0f), l.filo(0.5f), style = Stroke(0.7f * u))
    }

    /** La papeleta rosa sujeta con el clip, arriba a la izquierda, que asoma por encima de la tapa. */
    private fun DrawScope.papeleta(s: SitioLibretaDistopia, a: AireDistopia, f: FormasExpedienteDistopia) {
        val U = s.U; val u = density
        val t = s.tapa
        val c = Offset(f.clipX + 7f * U, t.top + 1f * U)
        val l = s.luz(a, c)
        rotate(-4f, c) {
            val r = Rect(f.clipX - 6f * U, t.top - 13f * U, f.clipX + 20f * U, t.top + 15f * U)
            drawRect(l.s(0.3f), r.topLeft + Offset(1.4f * U, 1.8f * U), r.size)
            drawRect(l.c(COPIAS[0]), r.topLeft, r.size)
            drawPath(renglonesDistopia(Rect(r.left + 3f * U, r.top + 4f * U, r.right - 2.5f * U, r.bottom - 3f * U), 0.9f * U, 2.6f * U, 7340), l.c(Color(0xFF3B3466)).copy(alpha = 0.4f))
            drawLine(l.b(0.4f), r.topLeft, Offset(r.right, r.top), 0.8f * u)
        }
        drawPath(corridoDistopia(f.clip, 0.9f * U, 1.1f * U), l.s(0.4f), style = Stroke(0.9f * U))
        drawPath(f.clip, l.c(P.ACERO_LUZ), style = Stroke(0.9f * U))
        drawPath(corridoDistopia(f.clip, -0.25f * U, -0.25f * U), l.b(0.5f), style = Stroke(0.35f * U))
    }

    /** La primera hoja (al abrirse): el folio con el encuadernador de lengüetas arriba, la cabecera y los renglones del formulario. */
    private fun DrawScope.primeraHoja(s: SitioLibretaDistopia, a: AireDistopia) {
        val U = s.U; val u = density
        val t = s.tapa
        val l = s.luz(a, t.center)
        drawRect(l.c(FOLIO), t.topLeft, t.size)
        drawRect(l.s(0.2f), t.topLeft, Size(10f * U, t.height))
        // El encuadernador: los dos agujeros, la pletina y las lengüetas dobladas hacia dentro.
        val y = t.top + 9f * U
        val x0 = t.left + t.width * 0.28f; val x1 = t.left + t.width * 0.72f
        drawCircle(l.s(0.6f), 1.6f * U, Offset(x0, y)); drawCircle(l.s(0.6f), 1.6f * U, Offset(x1, y))
        drawRect(l.c(P.ACERO_LUZ), Offset(x0 - 2f * U, y - 1.5f * U), Size(x1 - x0 + 4f * U, 3f * U))
        drawRect(l.s(0.3f), Offset(x0 - 2f * U, y + 1f * U), Size(x1 - x0 + 4f * U, 0.8f * U))
        drawRect(l.c(P.ACERO), Offset(x0, y - 1f * U), Size((x1 - x0) * 0.42f, 2f * U))
        drawRect(l.c(P.ACERO), Offset(x1 - (x1 - x0) * 0.42f, y - 1f * U), Size((x1 - x0) * 0.42f, 2f * U))
        // La cabecera del formulario y sus renglones a máquina.
        drawRect(l.c(mezcla(FOLIO, P.ACERO, 0.25f)), Offset(t.left + 12f * U, t.top + 18f * U), Size(t.width - 22f * U, 7f * U))
        drawPath(renglonesDistopia(Rect(t.left + 14f * U, t.top + 34f * U, t.right - 12f * U, t.bottom - 20f * U), 1f * U, 6f * U, 7350, 6f * U), l.c(P.BAQUELITA).copy(alpha = 0.35f))
    }

    /** El canal del atril, delante, donde descansa el expediente: la chapa doblada con su labio, los remaches y la sombra en la mesa. */
    private fun DrawScope.canal(s: SitioLibretaDistopia, a: AireDistopia) {
        val U = s.U; val u = density
        val r = s.canal
        val y = r.center.y
        drawRect(a.sombra(if (a.oscuro) 0.5f else 0.34f), Offset(r.left + 2f * U, r.bottom), Size(r.width + 2f * U, 3f * U))
        val cols = List(5) { j -> s.luz(a, Offset(r.left + r.width * j / 4f, y)).c(P.ACERO) }
        drawRoundRect(Brush.horizontalGradient(cols, r.left, r.right), r.topLeft, r.size, CornerRadius(1.5f * U))
        // El labio de arriba, enrollado: coge la luz.
        val luces = List(5) { j -> s.luz(a, Offset(r.left + r.width * j / 4f, r.top)).c(P.ACERO_LUZ) }
        drawRect(Brush.horizontalGradient(luces, r.left, r.right), Offset(r.left + 1f * U, r.top), Size(r.width - 2f * U, 2.4f * U))
        val li = s.luz(a, Offset(r.left + 20f * U, r.top))
        drawLine(li.b(0.6f), Offset(r.left + 1.5f * U, r.top + 0.6f * U), Offset(r.right - 1.5f * U, r.top + 0.6f * U), 0.8f * u)
        drawLine(a.sombra(0.3f), Offset(r.left + 1f * U, r.top + 2.6f * U), Offset(r.right - 1f * U, r.top + 2.6f * U), 0.8f * u)
        drawLine(a.sombra(0.35f), Offset(r.left + 1f * U, r.bottom - 0.6f * U), Offset(r.right - 1f * U, r.bottom - 0.6f * U), 0.8f * u)
        tornilloDistopia(Offset(r.left + 4f * U, y + 1f * U), 1.1f * u, li, 7360)
        tornilloDistopia(Offset(r.right - 4f * U, y + 1f * U), 1.1f * u, s.luz(a, Offset(r.right, y)), 7361)
        if (li.k > 0.05f) drawLine(li.filo(0.8f), Offset(r.left + 0.5f * U, r.top + 1f * U), Offset(r.left + 0.5f * U, r.bottom - 1f * U), 1f * u)
        if (a.oscuro) drawLine(s.luz(a, Offset(r.right, y)).frio(0.8f), Offset(r.right - 0.5f * U, r.top + 1f * U), Offset(r.right - 0.5f * U, r.bottom - 1f * U), 1f * u)
    }
}
