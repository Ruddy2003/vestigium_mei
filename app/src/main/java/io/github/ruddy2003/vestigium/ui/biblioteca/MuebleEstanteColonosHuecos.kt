package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.CALABAZA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.CRUDO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.HIERRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.HIERRO_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LADRILLO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LATA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LATA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.MAIZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.MARINO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.NOGAL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.OXIDO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.PELTRE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.PINO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.SALVIA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.VERDE
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas

/*
 * Lo que hay en los huecos libres de las repisas de la alacena de Colonos: cosas de cocina con uso, grandes (del 70 al 90 % del
 * alto del casillero), agrupadas y encimadas, distintas por repisa. Y el candil de frasco del costado izquierdo.
 */

/**
 * El hueco libre de la repisa [k], de [oc] (donde acaban sus volúmenes) al costado derecho. Según la repisa, uno de cuatro
 * grupos: los frascos de conserva de cierre de alambre; la maceta de lata con su geranio y un frasco chico; el molinillo de
 * café con el costurero de lata; o los platos de peltre de pie con el jarro de las cucharas de palo. Si el grupo no cabe, el
 * siguiente que quepa; si el hueco es largo, otro grupo distinto a su lado (hasta tres); si no cabe ninguno, una maceta o un
 * frasco solos.
 */
internal fun DrawScope.huecoLibreColonos(k: Int, r: Rect, oc: Float, g: MarcoColonos) {
    val w = g.w
    var x = oc + w * 0.02f
    val fin = g.x1 - w * 0.03f
    if (fin - x < w * 0.08f) return
    val piso = r.bottom - w * 0.006f
    val al = r.height
    // El ancho de cada grupo, en altos del casillero: frascos, maceta, molinillo y costurero, platos y jarro.
    val anchos = floatArrayOf(0.5f, 0.62f, 0.95f, 1.36f)
    var cual = Math.floorMod(k * 3 + 1, 4)
    var puestos = 0
    clipRect(r.left, r.top, r.right, r.bottom + 2f) {
        // Si el hueco es largo, caben dos o tres grupos seguidos, cada uno distinto.
        while (puestos < 3) {
            val libre = fin - x
            val elegido = (0 until 4).map { (cual + it) % 4 }.firstOrNull { libre >= al * anchos[it] }
            if (elegido == null) {
                if (puestos == 0) {
                    if (k % 2 == 0) macetaColonos(g, x, piso, al, k, false) else frascoConserva(g, Offset(x + al * 0.17f, piso), al * 0.3f, al * 0.62f, k, 4)
                }
                break
            }
            val usado = when (elegido) {
                0 -> { val tres = libre >= al * 0.72f; frascosColonos(g, x, piso, al, k + puestos, tres); if (tres) 0.72f else 0.5f }
                1 -> { macetaColonos(g, x, piso, al, k + puestos, true); 0.62f }
                2 -> { molinilloYCosturero(g, x, piso, al); 0.95f }
                else -> { val tres = libre > al * 1.9f; platosYJarro(g, x, piso, al, tres); if (tres) 1.88f else 1.36f }
            }
            x += al * (usado + 0.12f)
            cual = (elegido + 1) % 4
            puestos++
        }
    }
}

// ---------------------------------------------------------------- los frascos de conserva

/** Lo que guardan los frascos: duraznos en mitades, chiles verdes, jitomates, frijoles. */
private val CONSERVAS = listOf(Color(0xFFE38A34), Color(0xFF6E8A35), Color(0xFFB8382A), Color(0xFF6A3A28))

/** Dos frascos grandes de conserva y uno chico delante, encimados. */
private fun DrawScope.frascosColonos(g: MarcoColonos, x0: Float, piso: Float, al: Float, k: Int, tres: Boolean) {
    val an = al * 0.3f
    frascoConserva(g, Offset(x0 + an * 0.55f, piso), an, al * 0.82f, k, 1)
    if (tres) frascoConserva(g, Offset(x0 + an * 1.45f, piso), an * 0.95f, al * 0.7f, k + 1, 2)
    frascoConserva(g, Offset(x0 + an * (if (tres) 1.0f else 1.15f), piso + 0.5f), an * 0.72f, al * 0.42f, k + 2, 3)
}

/**
 * Un frasco de conserva de cierre de alambre, en [pie]: el vidrio con su hombro y su cuello, lo de dentro (de [cual] según la
 * semilla) que se ve por el vidrio con el aire de arriba, la tapa de vidrio con su goma naranja, el alambre del cierre con su
 * palanca, el brillo del vidrio, su sombra en la tabla y en el fondo.
 */
private fun DrawScope.frascoConserva(g: MarcoColonos, pie: Offset, an: Float, alto: Float, cual: Int, semilla: Int) {
    val u = density; val a = g.a
    val c = Offset(pie.x, pie.y - alto * 0.45f)
    val k = g.k(c); val br = g.br(c)
    val hombro = pie.y - alto * 0.8f
    val cuello = pie.y - alto * 0.88f
    val cuerpo = Path().apply {
        addRoundRect(RoundRect(Rect(pie.x - an / 2, hombro, pie.x + an / 2, pie.y), CornerRadius(an * 0.16f)))
        addRect(Rect(pie.x - an * 0.36f, cuello, pie.x + an * 0.36f, hombro + an * 0.1f))
    }
    // La sombra en el fondo y en la tabla.
    if (!a.oscuro || g.hacia(c) > 0f) drawRoundRect(a.sombra(0.25f), Offset(pie.x - an / 2 + (if (a.oscuro) -an * 0.25f else an * 0.25f), cuello + an * 0.1f),
        Size(an, pie.y - cuello - an * 0.1f), CornerRadius(an * 0.16f))
    contactoColonos(Offset(pie.x, pie.y), an * 1.3f, g.w * 0.012f, a, 0.42f)
    // Lo de dentro, detrás del vidrio.
    val conserva = CONSERVAS[Math.floorMod(cual, CONSERVAS.size)]
    clipPath(cuerpo) {
        drawRect(a.luz(mezcla(conserva, Color(0xFFE8C98A), 0.25f), k, br), Offset(pie.x - an / 2, hombro + an * 0.08f), Size(an, pie.y - hombro))
        val trozos = Path()
        for (i in 0 until 9) {
            val tx = pie.x - an * 0.42f + an * 0.84f * azar(semilla * 31 + cual, i)
            val ty = hombro + an * 0.2f + (pie.y - hombro - an * 0.25f) * azar(semilla * 31 + cual, 20 + i)
            val tr = an * (0.13f + 0.08f * azar(semilla, 40 + i))
            if (Math.floorMod(cual, CONSERVAS.size) == 1) trozos.addOval(Rect(tx - tr * 0.4f, ty - tr * 1.4f, tx + tr * 0.4f, ty + tr * 1.4f))
            else trozos.addOval(Rect(tx - tr, ty - tr * 0.85f, tx + tr, ty + tr * 0.85f))
        }
        drawPath(trozos, a.luz(conserva, k, br))
        drawPath(trozos, a.sombra(0.25f), style = Stroke(0.6f * u))
        // El vidrio: verdoso en los cantos, el brillo largo del lado de la luz y el oscuro del otro.
        drawRect(Color(0xFFBFD8CC).copy(alpha = 0.22f), Offset(pie.x - an / 2, cuello), Size(an * 0.12f, pie.y - cuello))
        drawRect(Color(0xFFBFD8CC).copy(alpha = 0.22f), Offset(pie.x + an * 0.38f, cuello), Size(an * 0.12f, pie.y - cuello))
        val izq = g.hacia(c) < 0f
        drawRect(a.brillo(0.6f, maxOf(k, 0.35f), br), Offset(if (izq) pie.x - an * 0.32f else pie.x + an * 0.22f, hombro + an * 0.15f), Size(an * 0.1f, pie.y - hombro - an * 0.35f))
        drawRect(a.sombra(0.2f), Offset(if (izq) pie.x + an * 0.2f else pie.x - an / 2, cuello), Size(an * 0.3f, pie.y - cuello))
    }
    drawPath(cuerpo, a.luz(Color(0xFF8FB0A4), k, br).copy(alpha = 0.55f), style = Stroke(0.8f * u))
    // La tapa de vidrio con la goma naranja y el alambre del cierre.
    val goma = Rect(pie.x - an * 0.4f, cuello - an * 0.05f, pie.x + an * 0.4f, cuello + an * 0.03f)
    drawRect(a.luz(Color(0xFFD9692A), k, br), goma.topLeft, goma.size)
    val tapa = Rect(pie.x - an * 0.4f, cuello - an * 0.2f, pie.x + an * 0.4f, cuello - an * 0.04f)
    drawRoundRect(a.luz(Color(0xFFCFE0D8), k, br).copy(alpha = 0.85f), tapa.topLeft, tapa.size, CornerRadius(an * 0.2f, an * 0.12f))
    drawRoundRect(a.brillo(0.5f, maxOf(k, 0.3f), br), Offset(tapa.left + an * 0.1f, tapa.top + 0.5f * u), Size(an * 0.25f, an * 0.04f), CornerRadius(an * 0.02f))
    val alambre = Path().apply {
        moveTo(pie.x - an * 0.36f, cuello + an * 0.08f); lineTo(pie.x - an * 0.05f, tapa.top - an * 0.02f); lineTo(pie.x + an * 0.36f, cuello + an * 0.08f)
        moveTo(pie.x + an * 0.36f, cuello + an * 0.08f); lineTo(pie.x + an * 0.42f, cuello + an * 0.32f)
    }
    drawPath(alambre, a.luz(LATA, maxOf(k, 0.2f), br), style = Stroke(0.9f * u, cap = StrokeCap.Round))
    if (a.oscuro && k > 0.1f) drawRoundRect(a.brillo(0.5f * k, k, br), Offset(pie.x - an / 2, hombro), Size(an, pie.y - hombro), CornerRadius(an * 0.16f), style = Stroke(1f * u))
}

// ---------------------------------------------------------------- la maceta de lata

/**
 * La maceta de lata con su geranio: la lata grande, con su franja de pintura y el óxido, la tierra, los tallos, las hojas
 * redondas con su anillo más oscuro y dos ramilletes de flores de ladrillo; y un frasco chico al lado.
 */
private fun DrawScope.macetaColonos(g: MarcoColonos, x0: Float, piso: Float, al: Float, k0: Int, conFrasco: Boolean) {
    val u = density; val a = g.a
    val an = al * 0.38f; val lh = al * 0.4f
    val lata = Rect(x0 + al * 0.04f, piso - lh, x0 + al * 0.04f + an, piso)
    val c = lata.center
    val k = g.k(c); val br = g.br(Offset(c.x, c.y - al * 0.3f))
    val kp = g.k(Offset(c.x, piso - al * 0.65f))
    if (!a.oscuro) drawRect(a.sombra(0.22f), Offset(lata.left + an * 0.3f, piso - al * 0.85f), Size(an * 1.1f, al * 0.85f))
    // El geranio: tallos, hojas y flores, detrás y delante del borde.
    val tallos = Path(); val hojas = Path(); val anillos = Path(); val flores = Path()
    val puntas = listOf(Offset(-0.32f, 0.82f), Offset(0.05f, 0.9f), Offset(0.42f, 0.74f), Offset(-0.05f, 0.62f), Offset(0.3f, 0.55f))
    puntas.forEachIndexed { i, (fx, fy) ->
        val p = Offset(c.x + an * fx * 1.3f, piso - al * fy)
        tallos.moveTo(c.x + an * fx * 0.3f, lata.top + 1f); tallos.quadraticTo(c.x + an * fx * 0.9f, (lata.top + p.y) / 2, p.x, p.y)
        val rh = al * (0.1f + 0.03f * azar(7970 + k0, i))
        hojas.addOval(Rect(p.x - rh, p.y - rh * 0.85f, p.x + rh, p.y + rh * 0.85f))
        anillos.addOval(Rect(p.x - rh * 0.55f, p.y - rh * 0.45f, p.x + rh * 0.55f, p.y + rh * 0.45f))
    }
    for (j in 0 until 2) {
        val fc = Offset(c.x + an * (if (j == 0) -0.15f else 0.32f), piso - al * (if (j == 0) 0.96f else 0.84f))
        tallos.moveTo(c.x, lata.top); tallos.lineTo(fc.x, fc.y)
        for (i in 0 until 7) {
            val ang = i * 0.9f
            val q = Offset(fc.x + al * 0.045f * kotlin.math.cos(ang), fc.y + al * 0.04f * kotlin.math.sin(ang))
            flores.addOval(Rect(q.x - al * 0.022f, q.y - al * 0.022f, q.x + al * 0.022f, q.y + al * 0.022f))
        }
    }
    drawPath(tallos, a.luz(mezcla(VERDE, PINO, 0.3f), kp, br), style = Stroke(1.3f * u))
    drawPath(hojas, a.luz(VERDE, kp, br))
    drawPath(anillos, a.luz(mezcla(VERDE, HIERRO, 0.35f), kp, br).copy(alpha = 0.6f), style = Stroke(1.2f * u))
    drawPath(hojas, a.sombra(0.25f), style = Stroke(0.6f * u))
    drawPath(flores, a.luz(LADRILLO, kp, br))
    drawPath(flores, a.brillo(0.25f, maxOf(kp, 0.3f), br), style = Stroke(0.5f * u))
    // La lata: su cuerpo, la franja de pintura que le queda, el óxido, los anillos, la luz y la sombra, y el borde.
    contactoColonos(Offset(c.x, piso), an * 1.3f, g.w * 0.012f, a, 0.42f)
    drawRect(a.luz(LATA, k, br), lata.topLeft, lata.size)
    drawRect(a.luz(MARINO, k, br), Offset(lata.left, lata.top + lh * 0.3f), Size(an, lh * 0.4f))
    drawRect(a.luz(MAIZ, k, br), Offset(lata.left, lata.top + lh * 0.44f), Size(an, lh * 0.06f))
    drawRect(a.luz(OXIDO, k, br).copy(alpha = 0.6f), Offset(lata.left, lata.bottom - lh * 0.18f), Size(an, lh * 0.18f))
    for (i in 1..3) drawLine(a.sombra(0.22f), Offset(lata.left, lata.top + lh * i / 4f), Offset(lata.right, lata.top + lh * i / 4f), 0.7f * u)
    val izq = g.hacia(c) < 0f
    drawRect(a.brillo(0.35f, maxOf(k, 0.3f), br), Offset(if (izq) lata.left + an * 0.1f else lata.right - an * 0.25f, lata.top), Size(an * 0.15f, lh))
    drawRect(a.sombra(0.28f), Offset(if (izq) lata.right - an * 0.3f else lata.left, lata.top), Size(an * 0.3f, lh))
    drawRect(a.luz(LATA_OSC, k, br), Offset(lata.left - 0.5f * u, lata.top - 1.2f * u), Size(an + 1f * u, 2.4f * u))
    drawRect(a.luz(Color(0xFF4A3424), k, br), Offset(lata.left + 1f * u, lata.top + 1.2f * u), Size(an - 2f * u, 1.6f * u))
    if (conFrasco) frascoConserva(g, Offset(lata.right + al * 0.13f, piso), al * 0.24f, al * 0.5f, k0 + 3, 9)
}

// ---------------------------------------------------------------- el molinillo y el costurero

/** El molinillo de café, alto, y delante, a su derecha, el costurero de lata con los carretes encima. */
private fun DrawScope.molinilloYCosturero(g: MarcoColonos, x0: Float, piso: Float, al: Float) {
    molinilloColonos(g, Offset(x0 + al * 0.24f, piso), al)
    costureroColonos(g, Offset(x0 + al * 0.68f, piso + 0.5f), al)
}

/** El molinillo de café: la caja de nogal con su cajoncito y su perilla, la tapa de lata, la copa, la manivela y su puño. */
private fun DrawScope.molinilloColonos(g: MarcoColonos, pie: Offset, al: Float) {
    val u = density; val a = g.a
    val lado = al * 0.44f
    val caja = Rect(pie.x - lado / 2, pie.y - lado, pie.x + lado / 2, pie.y)
    val k = g.k(caja.center); val br = g.br(caja.center)
    if (!a.oscuro) drawRect(a.sombra(0.22f), Offset(caja.left + lado * 0.3f, caja.top - lado * 0.3f), Size(lado, lado * 1.3f))
    contactoColonos(Offset(pie.x, pie.y), lado * 1.3f, g.w * 0.012f, a, 0.42f)
    drawRect(a.luz(NOGAL, k, br), caja.topLeft, caja.size)
    vetas(caja, a.sombra(0.25f), 7980, 0.8f, vertical = true, grosor = 0.5f)
    val izq = g.hacia(caja.center) < 0f
    drawRect(a.brillo(0.25f, maxOf(k, 0.3f), br), Offset(if (izq) caja.left else caja.right - lado * 0.2f, caja.top), Size(lado * 0.2f, lado))
    drawRect(a.sombra(0.3f), Offset(if (izq) caja.right - lado * 0.25f else caja.left, caja.top), Size(lado * 0.25f, lado))
    val cj = Rect(caja.left + lado * 0.15f, caja.top + lado * 0.55f, caja.right - lado * 0.15f, caja.bottom - lado * 0.12f)
    drawRect(a.sombra(0.45f), cj.topLeft, cj.size, style = Stroke(1f * u))
    drawCircle(a.luz(LATA, k, br), lado * 0.05f, cj.center)
    val tapa = Rect(caja.left - lado * 0.05f, caja.top - lado * 0.1f, caja.right + lado * 0.05f, caja.top)
    drawRect(a.luz(LATA, k, br), tapa.topLeft, tapa.size)
    drawRect(a.brillo(0.4f, maxOf(k, 0.3f), br), tapa.topLeft, Size(tapa.width, 0.9f * u))
    val copa = Rect(caja.center.x - lado * 0.3f, tapa.top - lado * 0.3f, caja.center.x + lado * 0.3f, tapa.top + lado * 0.02f)
    drawPath(Path().apply { moveTo(copa.left, copa.top); lineTo(copa.right, copa.top); lineTo(copa.right - lado * 0.1f, copa.bottom); lineTo(copa.left + lado * 0.1f, copa.bottom); close() },
        a.luz(mezcla(LATA, HIERRO, 0.25f), k, br))
    drawOval(a.luz(mezcla(LATA, HIERRO, 0.5f), k, br), Offset(copa.left, copa.top - lado * 0.05f), Size(copa.width, lado * 0.1f))
    val eje = Offset(caja.center.x, copa.top - lado * 0.08f)
    val mano = Offset(eje.x + lado * 0.6f, eje.y - lado * 0.18f)
    drawLine(a.luz(HIERRO, k, br), Offset(eje.x, copa.top), eje, lado * 0.05f)
    drawLine(a.luz(HIERRO, k, br), eje, mano, lado * 0.06f, StrokeCap.Round)
    drawLine(a.luz(NOGAL, k, br), mano, mano + Offset(0f, -lado * 0.3f), lado * 0.11f, StrokeCap.Round)
    drawCircle(a.luz(HIERRO_LUZ, k, br), lado * 0.05f, eje)
}

/**
 * El costurero: una lata redonda de galletas sin marca con la tapa ladeada encima, y sobre ella los carretes de hilo de colores
 * y el alfiletero de trapo; su sombra.
 */
private fun DrawScope.costureroColonos(g: MarcoColonos, pie: Offset, al: Float) {
    val u = density; val a = g.a
    val an = al * 0.5f; val lh = al * 0.26f
    val lata = Rect(pie.x - an / 2, pie.y - lh, pie.x + an / 2, pie.y)
    val k = g.k(lata.center); val br = g.br(lata.center)
    contactoColonos(Offset(pie.x, pie.y), an * 1.2f, g.w * 0.012f, a, 0.42f)
    drawRect(a.luz(SALVIA, k, br), lata.topLeft, lata.size)
    drawRect(a.luz(CRUDO, k, br).copy(alpha = 0.7f), Offset(lata.left, lata.top + lh * 0.55f), Size(an, lh * 0.1f))
    drawRect(a.luz(OXIDO, k, br).copy(alpha = 0.5f), Offset(lata.left, lata.bottom - lh * 0.16f), Size(an, lh * 0.16f))
    val izq = g.hacia(lata.center) < 0f
    drawRect(a.brillo(0.3f, maxOf(k, 0.3f), br), Offset(if (izq) lata.left + an * 0.08f else lata.right - an * 0.2f, lata.top), Size(an * 0.12f, lh))
    drawRect(a.sombra(0.28f), Offset(if (izq) lata.right - an * 0.25f else lata.left, lata.top), Size(an * 0.25f, lh))
    // La tapa ladeada y lo de encima.
    rotate(-6f, Offset(lata.center.x, lata.top)) {
        drawOval(a.luz(mezcla(SALVIA, HIERRO, 0.2f), k, br), Offset(lata.left - an * 0.04f, lata.top - lh * 0.22f), Size(an * 1.08f, lh * 0.3f))
        drawOval(a.brillo(0.35f, maxOf(k, 0.3f), br), Offset(lata.left + an * 0.1f, lata.top - lh * 0.18f), Size(an * 0.4f, lh * 0.08f))
    }
    val colores = listOf(LADRILLO, MARINO, MAIZ, CRUDO)
    for (i in 0 until 3) {
        val cx = lata.left + an * (0.2f + 0.24f * i)
        val ca = an * 0.18f; val chh = al * (0.14f + 0.02f * i)
        val carrete = Rect(cx - ca / 2, lata.top - lh * 0.1f - chh, cx + ca / 2, lata.top - lh * 0.1f)
        drawRect(a.sombra(0.3f), Offset(carrete.left + 1f * u, carrete.top + 1f * u), carrete.size)
        drawRect(a.luz(colores[i], k, br), Offset(carrete.left + ca * 0.08f, carrete.top + chh * 0.12f), Size(ca * 0.84f, chh * 0.76f))
        drawRect(a.luz(PINO, k, br), Offset(carrete.left, carrete.top), Size(ca, chh * 0.12f))
        drawRect(a.luz(PINO, k, br), Offset(carrete.left, carrete.bottom - chh * 0.12f), Size(ca, chh * 0.12f))
        drawRect(a.brillo(0.3f, maxOf(k, 0.3f), br), Offset(carrete.left + ca * 0.15f, carrete.top + chh * 0.12f), Size(ca * 0.15f, chh * 0.76f))
    }
    // El alfiletero de trapo, redondo, con dos alfileres.
    val al0 = Offset(lata.right - an * 0.12f, lata.top - lh * 0.2f)
    drawCircle(a.luz(CALABAZA, k, br), al * 0.06f, al0)
    drawCircle(a.brillo(0.3f, maxOf(k, 0.3f), br), al * 0.02f, al0 - Offset(al * 0.02f, al * 0.02f))
    drawLine(a.luz(LATA, k, br), al0, al0 + Offset(al * 0.05f, -al * 0.08f), 0.7f * u)
    drawLine(a.luz(LATA, k, br), al0, al0 + Offset(-al * 0.02f, -al * 0.09f), 0.7f * u)
}

// ---------------------------------------------------------------- los platos y el jarro

/** Dos o tres platos de peltre de pie, apoyados en el fondo, y el jarro con las cucharas de palo delante. */
private fun DrawScope.platosYJarro(g: MarcoColonos, x0: Float, piso: Float, al: Float, tres: Boolean) {
    val u = density; val a = g.a
    val rp = al * 0.42f
    val n = if (tres) 3 else 2
    for (i in 0 until n) {
        val c = Offset(x0 + rp + i * rp * 0.62f, piso - rp * 0.98f)
        val k = g.k(c); val br = g.br(c)
        rotate(-7f + 5f * i, Offset(c.x, piso)) {
            if (!a.oscuro) drawOval(a.sombra(0.3f), Offset(c.x - rp + g.w * 0.012f, c.y - rp + g.w * 0.008f), Size(rp * 2, rp * 2))
            drawCircle(a.luz(PELTRE, k, br), rp, c)
            drawCircle(a.luz(mezcla(CRUDO, Color.White, 0.35f), k, br), rp * 0.9f, c)
            drawCircle(a.sombra(0.14f), rp * 0.6f, c, style = Stroke(1f * u))
            drawCircle(a.luz(mezcla(CRUDO, LATA, 0.4f), k, br), rp * 0.58f, c)
            drawCircle(a.luz(HIERRO, k, br), rp * 0.05f, Offset(c.x + rp * 0.72f, c.y + rp * 0.32f))
            drawArc(a.brillo(0.55f, maxOf(k, 0.3f), br), 200f, 70f, false, Offset(c.x - rp * 0.8f, c.y - rp * 0.8f), Size(rp * 1.6f, rp * 1.6f), style = Stroke(1.4f * u))
            drawCircle(a.sombra(0.3f), rp, c, style = Stroke(0.8f * u))
        }
    }
    jarroColonos(g, Offset(x0 + rp * 0.9f, piso + 0.5f), al)
}

/** El jarro de peltre verde salvia con tres cucharas de palo. */
private fun DrawScope.jarroColonos(g: MarcoColonos, pie: Offset, al: Float) {
    val u = density; val a = g.a
    val ja = al * 0.14f; val jh = al * 0.4f
    val cuerpo = Rect(pie.x - ja, pie.y - jh, pie.x + ja, pie.y)
    val k = g.k(cuerpo.center); val br = g.br(cuerpo.center)
    listOf(-0.5f to -12f, 0.1f to 4f, 0.6f to 16f).forEach { (fx, giro) ->
        val b = Offset(pie.x + ja * fx, cuerpo.top + jh * 0.2f)
        rotate(giro, b) {
            val largo = jh * 1.05f
            drawLine(a.luz(PINO, k, br), b, Offset(b.x, b.y - largo), al * 0.03f, StrokeCap.Round)
            drawOval(a.luz(PINO, k, br), Offset(b.x - al * 0.045f, b.y - largo - al * 0.09f), Size(al * 0.09f, al * 0.12f))
            drawOval(a.sombra(0.2f), Offset(b.x - al * 0.025f, b.y - largo - al * 0.065f), Size(al * 0.05f, al * 0.07f))
        }
    }
    contactoColonos(Offset(pie.x, pie.y), ja * 2.6f, g.w * 0.012f, a, 0.42f)
    drawPath(Path().apply { moveTo(cuerpo.right, cuerpo.top + jh * 0.2f); cubicTo(cuerpo.right + ja * 0.8f, cuerpo.top + jh * 0.2f, cuerpo.right + ja * 0.8f, cuerpo.bottom - jh * 0.3f, cuerpo.right, cuerpo.bottom - jh * 0.25f) },
        a.luz(SALVIA, k, br), style = Stroke(al * 0.03f))
    drawRect(a.luz(SALVIA, k, br), cuerpo.topLeft, cuerpo.size)
    val izq = g.hacia(cuerpo.center) < 0f
    drawRect(a.brillo(0.3f, maxOf(k, 0.3f), br), Offset(if (izq) cuerpo.left + ja * 0.25f else cuerpo.right - ja * 0.5f, cuerpo.top), Size(ja * 0.25f, jh))
    drawRect(a.sombra(0.3f), Offset(if (izq) cuerpo.right - ja * 0.6f else cuerpo.left, cuerpo.top), Size(ja * 0.6f, jh))
    drawRect(a.luz(MARINO, k, br), Offset(cuerpo.left, cuerpo.top), Size(cuerpo.width, 1.6f * u))
    drawOval(a.luz(mezcla(SALVIA, HIERRO, 0.5f), k, br), Offset(cuerpo.left, cuerpo.top - al * 0.02f), Size(cuerpo.width, al * 0.04f))
    drawCircle(a.luz(HIERRO, k, br), al * 0.015f, Offset(cuerpo.left + ja * 0.6f, cuerpo.bottom - jh * 0.3f))
}

/**
 * El candil de frasco colgado de un clavo a media altura del costado izquierdo: un frasco de vidrio con su aceite ámbar, la tapa
 * de lata con el portamecha y la mecha, y el asa de alambre. De noche arde (la tercera luz del cuarto, chica, que tiñe a sus
 * vecinos); de día y al anochecer está apagado.
 */
internal fun DrawScope.candilFrascoColonos(g: MarcoColonos) {
    val u = density; val w = g.w; val a = g.a
    val llama = Offset(w * g.candil.x, w * g.candil.y)
    val tapa = llama.y + w * 0.012f
    val an = w * 0.019f; val al = w * 0.05f
    val frasco = Rect(llama.x - an, tapa, llama.x + an, tapa + al)
    val clavo = Offset(llama.x, llama.y - w * 0.05f)
    val k = g.k(frasco.center); val br = g.br(frasco.center)
    val arde = a.oscuro
    // La sombra del frasco en el costado (de día, a la derecha y abajo).
    if (!a.oscuro) drawRoundRect(a.sombra(0.3f), Offset(frasco.left + w * 0.008f, frasco.top + w * 0.006f), frasco.size, CornerRadius(an * 0.35f))
    // El asa de alambre, del clavo a los dos lados de la tapa.
    val asa = Path().apply { moveTo(frasco.left - 0.5f * u, tapa + 1f * u); lineTo(clavo.x, clavo.y); lineTo(frasco.right + 0.5f * u, tapa + 1f * u) }
    drawPath(asa, a.luz(LATA, maxOf(k, if (arde) 0.6f else 0f), br), style = Stroke(0.9f * u))
    // El frasco: el vidrio, el aceite ámbar hasta la mitad y su brillo.
    val forma = RoundRect(frasco, CornerRadius(an * 0.35f))
    val vidrio = mezcla(Color(0xFFCFE2DA), a.hondo, if (a.oscuro) 0.4f else 0f)
    drawRoundRect(vidrio.copy(alpha = 0.55f), frasco.topLeft, frasco.size, CornerRadius(an * 0.35f))
    clipPath(Path().apply { addRoundRect(forma) }) {
        val aceite = if (arde) Color(0xFFF0A23A) else a.luz(Color(0xFFC98A2E), k, br)
        drawRect(aceite, Offset(frasco.left, frasco.top + al * 0.45f), Size(frasco.width, al * 0.55f))
        drawRect(Color.White.copy(alpha = if (arde) 0.5f else 0.35f), Offset(frasco.left, frasco.top + al * 0.45f), Size(frasco.width, 0.8f * u))
        // La mecha que baja al aceite.
        drawLine(Color(0xFF6A5A44), Offset(llama.x, tapa), Offset(llama.x + an * 0.2f, frasco.bottom - al * 0.12f), 0.9f * u)
        drawRect(Color.White.copy(alpha = 0.45f), Offset(frasco.left + an * 0.25f, frasco.top + 2f * u), Size(an * 0.22f, al - 4f * u))
    }
    drawRoundRect(a.sombra(0.4f), frasco.topLeft, frasco.size, CornerRadius(an * 0.35f), style = Stroke(0.7f * u))
    // La tapa de lata con el portamecha.
    drawRect(a.luz(LATA, maxOf(k, if (arde) 0.7f else 0f), br), Offset(frasco.left - 0.6f * u, tapa - w * 0.006f), Size(frasco.width + 1.2f * u, w * 0.007f))
    drawRect(a.luz(LATA_OSC, maxOf(k, if (arde) 0.7f else 0f), br), Offset(llama.x - w * 0.003f, tapa - w * 0.011f), Size(w * 0.006f, w * 0.006f))
    if (arde) {
        mancha(llama, w * 0.16f, PaletaColonos.BOMBILLA.copy(alpha = 0.3f))
        mancha(llama, w * 0.04f, Color(0xFFFFE2A8).copy(alpha = 0.7f))
        val lla = Path().apply {
            moveTo(llama.x, llama.y - w * 0.014f)
            quadraticTo(llama.x + w * 0.006f, llama.y - w * 0.002f, llama.x, llama.y + w * 0.004f)
            quadraticTo(llama.x - w * 0.006f, llama.y - w * 0.002f, llama.x, llama.y - w * 0.014f); close()
        }
        drawPath(lla, Color(0xFFFFC560))
        drawCircle(Color.White.copy(alpha = 0.9f), w * 0.0022f, Offset(llama.x, llama.y))
    } else {
        drawLine(a.luz(Color(0xFF3A3028), k), Offset(llama.x, tapa - w * 0.011f), Offset(llama.x, tapa - w * 0.016f), 1f * u)
    }
    clavoColonos(clavo, 1.3f * u, g)
}
