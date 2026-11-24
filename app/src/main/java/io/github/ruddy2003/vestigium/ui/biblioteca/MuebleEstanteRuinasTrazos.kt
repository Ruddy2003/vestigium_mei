package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaRuinas as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los trazos que comparten las piezas del estante de la Ciudad verde: caminos suaves, el volumen de gouache de una forma, la hoja
 * de hiedra con sus tres lóbulos, el tallo con sus hojas, la fronda de helecho, la mata de musgo y el vilano. Cada uno tiene su
 * versión completa (para lo que se pinta una sola vez, en la corona guardada) y, donde hace falta, una barata.
 */

/** [p] corrido. */
internal fun corridoRuinas(p: Path, dx: Float, dy: Float) = Path().apply { addPath(p, Offset(dx, dy)) }

/** Lo que queda de [p] al quitarle la misma forma corrida: una media luna en el borde contrario al corrimiento. */
internal fun bordeRuinas(p: Path, dx: Float, dy: Float) = Path().apply { op(p, corridoRuinas(p, dx, dy), PathOperation.Difference) }

/** Una figura cerrada que pasa suave por los puntos [q] (Catmull-Rom pasado a Bézier). */
internal fun cerradaRuinas(q: List<Offset>): Path = Path().apply {
    val m = q.size
    moveTo(q[0].x, q[0].y)
    for (i in 0 until m) {
        val a = q[(i - 1 + m) % m]; val b = q[i]; val c = q[(i + 1) % m]; val d = q[(i + 2) % m]
        val c1 = b + (c - a) / 6f; val c2 = c - (d - b) / 6f
        cubicTo(c1.x, c1.y, c2.x, c2.y, c.x, c.y)
    }
    close()
}

/** Una línea abierta que pasa suave por [q]. */
internal fun abiertaRuinas(q: List<Offset>): Path = Path().apply {
    moveTo(q[0].x, q[0].y)
    for (i in 0 until q.size - 1) {
        val a = q[maxOf(i - 1, 0)]; val b = q[i]; val c = q[i + 1]; val d = q[minOf(i + 2, q.size - 1)]
        val c1 = b + (c - a) / 6f; val c2 = c - (d - b) / 6f
        cubicTo(c1.x, c1.y, c2.x, c2.y, c.x, c.y)
    }
}

/** Un polígono de lados rectos por los puntos [q]. */
internal fun poligonoRuinas(q: List<Offset>): Path = Path().apply {
    moveTo(q[0].x, q[0].y)
    for (i in 1 until q.size) lineTo(q[i].x, q[i].y)
    close()
}

/** El contorno de un desconchón: un polígono de cantos vivos y desiguales (la pintura salta en escamas, no en burbujas). */
internal fun desconchonRuinas(c: Offset, rx: Float, ry: Float, semilla: Int, m: Int = 15): Path = Path().apply {
    for (k in 0 until m) {
        val a = 6.2832f * k / m + azar(semilla, k) * 0.25f
        val r = if (k % 3 == 0) 0.55f + 0.2f * azar(semilla, k + 30) else 0.85f + 0.25f * azar(semilla, k + 50)
        val p = Offset(c.x + rx * r * cos(a), c.y + ry * r * sin(a))
        if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
    }
    close()
}

/** Un triángulo, un cuadrilátero, un pentágono o un hexágono de lados rectos (Offset no admite `vararg`). */
internal fun poligonoRuinas(a: Offset, b: Offset, c: Offset): Path = poligonoRuinas(listOf(a, b, c))
internal fun poligonoRuinas(a: Offset, b: Offset, c: Offset, d: Offset): Path = poligonoRuinas(listOf(a, b, c, d))
internal fun poligonoRuinas(a: Offset, b: Offset, c: Offset, d: Offset, e: Offset): Path = poligonoRuinas(listOf(a, b, c, d, e))
internal fun poligonoRuinas(a: Offset, b: Offset, c: Offset, d: Offset, e: Offset, f: Offset): Path = poligonoRuinas(listOf(a, b, c, d, e, f))

/** Un contorno de piedra o de bulto alrededor de [c]: [m] puntos con un poco de azar. */
internal fun bultoRuinas(c: Offset, rx: Float, ry: Float, semilla: Int, m: Int = 9, rudo: Float = 0.18f): Path =
    cerradaRuinas(List(m) { k ->
        val a = 6.2832f * k / m + azar(semilla, k) * 0.3f
        val r = 1f - rudo + 2 * rudo * azar(semilla, k + 50)
        Offset(c.x + rx * r * cos(a), c.y + ry * r * sin(a))
    })

/**
 * El volumen de gouache sobre una forma [f]: el color [base], la media luna de sombra del lado contrario a la luz ([hacia]: -1 si
 * la luz viene de la izquierda, 1 si de la derecha), el claro del lado de la luz y el filo de la llama.
 */
internal fun DrawScope.modeladoRuinas(f: Path, base: Color, l: LuzRuinas, hacia: Float, fuerza: Float = 1f, claroA: Float = 0.34f) {
    val r = f.getBounds(); val an = r.width; val al = r.height
    drawPath(f, base)
    drawPath(bordeRuinas(f, hacia * an * 0.24f, -al * 0.06f), l.s(0.24f * fuerza))
    drawPath(bordeRuinas(f, hacia * an * 0.09f, -al * 0.03f), l.s(0.2f * fuerza))
    drawPath(bordeRuinas(f, -hacia * an * 0.1f, al * 0.14f), l.b(claroA))
    if (l.k > 0.05f) drawPath(bordeRuinas(f, -hacia * an * 0.05f, al * 0.03f), l.filo(0.55f))
}

/**
 * El volumen de [modeladoRuinas], barato (para lo que se pinta en cada dibujo): un solo relleno con un degradado a lo ancho de
 * colores llenos (el color con su claro del lado de la luz y su sombra, cada vez más honda, del otro).
 */
internal fun DrawScope.modeladoRapidoRuinas(f: Path, base: Color, l: LuzRuinas, hacia: Float, fuerza: Float = 1f, claroA: Float = 0.34f) {
    val r = f.getBounds()
    if (r.width < 1f) { drawPath(f, base); return }
    val claro = l.b(claroA); val sombra = l.s(0.3f * fuerza); val honda = l.s(0.52f * fuerza)
    fun sobre(c: Color, a: Float) = mezcla(base, c.copy(alpha = 1f), c.alpha * a)
    val izqADer = listOf(sobre(claro, 1f), sobre(claro, 0.35f), base, base, sobre(sombra, 1f), sobre(honda, 1f))
    drawPath(f, Brush.horizontalGradient(if (hacia < 0f) izqADer else izqADer.asReversed(), r.left, r.right))
}

/** Un punto de una forma dada en coordenadas propias ([x], [y]), girada [ang] grados y puesta en [c]. */
internal fun giradoRuinas(c: Offset, x: Float, y: Float, ang: Float): Offset {
    val a = Math.toRadians(ang.toDouble())
    val ca = cos(a).toFloat(); val sa = sin(a).toFloat()
    return Offset(c.x + x * ca - y * sa, c.y + x * sa + y * ca)
}

/** Una hoja de hiedra de largo [s], con el pecíolo en [c] y la punta hacia [ang] grados (0, hacia arriba): tres lóbulos. */
internal fun hojaHiedraRuinas(c: Offset, s: Float, ang: Float, ancho: Float = 1f): Path {
    val pts = listOf(
        0f to 0f,
        -0.22f * ancho to 0.04f, -0.5f * ancho to -0.1f, -0.48f * ancho to -0.34f,
        -0.46f * ancho to -0.46f, -0.3f * ancho to -0.52f, -0.2f * ancho to -0.58f,
        -0.14f * ancho to -0.78f, -0.05f * ancho to -0.92f, 0f to -1f,
        0.05f * ancho to -0.92f, 0.14f * ancho to -0.78f, 0.2f * ancho to -0.58f,
        0.3f * ancho to -0.52f, 0.46f * ancho to -0.46f, 0.48f * ancho to -0.34f,
        0.5f * ancho to -0.1f, 0.22f * ancho to 0.04f, 0f to 0f,
    ).map { (x, y) -> giradoRuinas(c, x * s, y * s, ang) }
    return Path().apply {
        moveTo(pts[0].x, pts[0].y)
        var i = 1
        while (i + 2 < pts.size) { cubicTo(pts[i].x, pts[i].y, pts[i + 1].x, pts[i + 1].y, pts[i + 2].x, pts[i + 2].y); i += 3 }
        close()
    }
}

/**
 * Una hoja de hiedra pintada: su sombra en lo de detrás (corrida [sombra] px, del lado contrario a la luz), el color, la mitad que
 * no mira a la luz más honda, el nervio claro y, de noche, el filo de la llama.
 */
internal fun DrawScope.hojaRuinas(c: Offset, s: Float, ang: Float, color: Color, l: LuzRuinas, hacia: Float, sombra: Float = s * 0.2f, ancho: Float = 1f) {
    val p = hojaHiedraRuinas(c, s, ang, ancho)
    if (sombra > 0f) translate(-hacia * sombra, sombra * 0.8f) { drawPath(p, l.s(0.24f)) }
    drawPath(p, l.c(color))
    val punta = giradoRuinas(c, 0f, -s, ang)
    val lado = giradoRuinas(c, if (hacia < 0f) s * 0.46f else -s * 0.46f, -s * 0.42f, ang)
    drawPath(poligonoRuinas(c, punta, lado), l.s(0.18f))
    drawLine(l.c(mezcla(color, P.MUSGO_LUZ, 0.55f)).copy(alpha = 0.75f), c, c + (punta - c) * 0.78f, s * 0.06f)
    if (l.k > 0.1f) drawPath(bordeRuinas(p, -hacia * s * 0.08f, s * 0.06f), l.filo(0.6f))
}

/**
 * Un tallo de hiedra que pasa por [pts] con hojas alternas; [tam] es el largo de las hojas. La luz de cada hoja se pide a [luz] (en
 * su sitio). [cada]: una hoja cada tantos puntos.
 */
internal fun DrawScope.talloRuinas(pts: List<Offset>, tam: Float, semilla: Int, luz: (Offset) -> LuzRuinas, hacia: Float, cada: Int = 1, grosor: Float = 1.3f) {
    val u = density
    val p = abiertaRuinas(pts)
    val l0 = luz(pts[pts.size / 2])
    translate(-hacia * tam * 0.15f, tam * 0.12f) { drawPath(p, l0.s(0.22f), style = Stroke(grosor * u)) }
    drawPath(p, l0.c(mezcla(P.HIEDRA_OSC, P.TIERRA, 0.4f)), style = Stroke(grosor * u, cap = StrokeCap.Round))
    var i = 1
    while (i < pts.size) {
        val a = pts[i - 1]; val b = pts[i]
        val ang = Math.toDegrees(atan2((b.x - a.x).toDouble(), -(b.y - a.y).toDouble())).toFloat()
        val lado = if (i % 2 == 0) 1f else -1f
        val t = tam * (0.75f + 0.5f * azar(semilla, i + 40))
        val col = if (azar(semilla, i + 60) > 0.55f) P.HIEDRA_LUZ else P.HIEDRA
        hojaRuinas(b, t, ang + lado * (50f + 25f * azar(semilla, i + 80)), col, luz(b), hacia)
        i += maxOf(1, cada)
    }
}

/**
 * Una fronda de helecho que nace en [b] y va hacia [ang] grados (0, arriba), de largo [largo], con sus pinnas a los dos lados
 * cada vez más chicas hacia la punta; [curva] la dobla.
 */
internal fun DrawScope.frondaRuinas(b: Offset, largo: Float, ang: Float, curva: Float, color: Color, l: LuzRuinas, hacia: Float, semilla: Int, pinnas: Int = 9) {
    val u = density
    val pts = List(6) { i ->
        val t = i / 5f
        giradoRuinas(b, curva * largo * t * t, -largo * t, ang)
    }
    val raquis = abiertaRuinas(pts)
    drawPath(raquis, l.c(mezcla(color, P.TIERRA, 0.35f)), style = Stroke(maxOf(0.8f * u, largo * 0.025f), cap = StrokeCap.Round))
    val hojas = Path()
    val claras = Path()
    for (k in 0 until pinnas) {
        val t = (k + 0.7f) / (pinnas + 0.4f)
        val i = (t * 5f).toInt().coerceIn(0, 4)
        val f = t * 5f - i
        val p = pts[i] + (pts[i + 1] - pts[i]) * f
        val dir = pts[i + 1] - pts[i]
        val a = Math.toDegrees(atan2(dir.x.toDouble(), -dir.y.toDouble())).toFloat()
        val tam = largo * 0.3f * (1f - t * 0.75f) * (0.85f + 0.3f * azar(semilla, k))
        for (lado in listOf(-1f, 1f)) {
            val q = giradoRuinas(p, lado * tam, -tam * 0.35f, a)
            val m1 = giradoRuinas(p, lado * tam * 0.5f, -tam * 0.38f, a)
            val m2 = giradoRuinas(p, lado * tam * 0.5f, tam * 0.08f, a)
            val destino = if (lado * hacia > 0f) hojas else claras
            destino.moveTo(p.x, p.y); destino.quadraticTo(m1.x, m1.y, q.x, q.y); destino.quadraticTo(m2.x, m2.y, p.x, p.y); destino.close()
        }
    }
    drawPath(claras, l.c(color))
    drawPath(hojas, l.c(mezcla(color, P.HIEDRA_OSC, 0.3f)))
    if (l.k > 0.1f) drawPath(claras, l.filo(0.35f))
}

/** Una mata de musgo: un bulto bajo de dos verdes con sus puntitos claros, ancho [an], sobre [b] (su pie). */
internal fun DrawScope.musgoRuinas(b: Offset, an: Float, al: Float, l: LuzRuinas, semilla: Int) {
    val u = density
    // Un cojín bajo y desigual, oscuro abajo, con la luz solo en lo alto, y las puntitas (los esporofitos) que se levantan.
    val f = bultoRuinas(Offset(b.x, b.y - al * 0.4f), an * 0.5f, al * 0.5f, semilla, 12, 0.3f)
    drawPath(corridoRuinas(f, 0f, al * 0.25f), l.s(0.3f))
    drawPath(f, l.c(mezcla(P.MUSGO, P.HIEDRA_OSC, 0.35f)))
    drawPath(bordeRuinas(f, an * 0.04f, al * 0.28f), l.c(P.MUSGO))
    val puntas = Path()
    for (k in 0 until 7) {
        val x = b.x + an * (azar(semilla, 70 + k) - 0.5f) * 0.8f
        val y = b.y - al * (0.55f + 0.35f * azar(semilla, 80 + k))
        puntas.moveTo(x, y); puntas.lineTo(x + (azar(semilla, 90 + k) - 0.5f) * 2f * u, y - al * 0.5f - 1.5f * u)
    }
    drawPath(puntas, l.c(mezcla(P.MUSGO_LUZ, P.KRAFT, 0.3f)), style = Stroke(0.7f * u, cap = StrokeCap.Round))
}

/** Un vilano de diente de león: el aquenio con su pico y la sombrilla de pelos, de alto [s], inclinado [ang] grados. */
internal fun DrawScope.vilanoRuinas(c: Offset, s: Float, ang: Float, color: Color, alfa: Float = 0.85f) {
    val u = density
    val pie = giradoRuinas(c, 0f, s * 0.55f, ang)
    val copa = giradoRuinas(c, 0f, -s * 0.25f, ang)
    drawLine(color.copy(alpha = alfa * 0.8f), pie, copa, 0.6f * u)
    drawCircle(mezcla(color, P.TIERRA, 0.5f).copy(alpha = alfa), maxOf(0.6f * u, s * 0.06f), pie)
    for (k in -3..3) {
        val q = giradoRuinas(copa, k * s * 0.13f, -s * (0.32f - 0.02f * k * k), ang)
        drawLine(color.copy(alpha = alfa * 0.7f), copa, q, 0.45f * u)
    }
    drawCircle(color.copy(alpha = alfa * 0.25f), s * 0.4f, giradoRuinas(copa, 0f, -s * 0.18f, ang))
}

/** La sombra de contacto: un óvalo bajo debajo de algo, corrido lejos de la luz. */
internal fun DrawScope.contactoRuinas(cx: Float, y: Float, an: Float, al: Float, l: LuzRuinas, hacia: Float, a: Float = 0.45f) {
    drawOval(l.s(a), Offset(cx - an * 0.5f - hacia * an * 0.08f, y - al * 0.5f), Size(an, al))
}
