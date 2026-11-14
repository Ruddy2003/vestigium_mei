package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Estacion
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo verde de la Ciudad verde: copas, árboles pelados, masas de bosque y mantos de hiedra. Son piezas propias de esta
 * escena (no cambian ninguna de `paisaje/`); todas reciben la luz del [Rayo] por un lado y dejan el otro en sombra.
 */
internal object VerdeRuinas {
    private val C = ColorRuinas

    /** El color de la hoja de un árbol silvestre en la [estacion]; null si está pelado. En otoño cada árbol toma el suyo. */
    fun hoja(estacion: Estacion, az: Azar): Color? = when (estacion) {
        Estacion.PRIMAVERA -> lerp(Color(0xFF93BC52), Color(0xFF6FA548), az.real())
        Estacion.VERANO -> lerp(C.BOSQUE, Color(0xFF3C6E40), az.real())
        Estacion.OTONO -> listOf(Color(0xFFCB862E), Color(0xFFDCB444), C.TELA, Color(0xFF8E9C46), Color(0xFFC9842E))[az.entero(5)]
        Estacion.INVIERNO -> null
        Estacion.SECAS -> C.MUSGO
        Estacion.LLUVIAS -> C.BOSQUE
        Estacion.INUNDACION -> C.BOSQUE
        Estacion.SIEMBRA -> C.MUSGO
        Estacion.COSECHA -> C.MUSGO
        Estacion.NINGUNA -> C.BOSQUE
    }

    /** El verde de siempre: pinos, hiedra y lo que no pierde la hoja. */
    val PERENNE = Color(0xFF2E5438)

    /** Una copa redonda con su centro en [c]: la sombra por debajo y del lado contrario a la luz, el cuerpo y los toques de sol. */
    fun DrawScope.copa(l: Luces, r: Rayo, c: Offset, rad: Float, hoja: Color, lejos: Float, az: Azar, nieve: Boolean = false) {
        val lado = if (r.lat >= 0f) 1f else -1f
        val osc = l.tono(mult(lerp(hoja, Color(0xFF1E2A26), 0.3f), r.color, 0.5f * r.contraste), lejos)
        val medio = l.tono(hoja, lejos)
        val luz = l.tono(lerp(hoja, Color(0xFFF4F0BC), 0.34f * r.contraste), lejos)
        if (rad > 5.6f) { copaGrande(l, r, c, rad, hoja, lejos, az, nieve); return }
        if (rad < 2.6f) {
            // Las chicas (lo lejano): la sombra, el cuerpo y un toque de sol.
            drawCircle(osc, rad, c + Offset(-lado * rad * 0.25f, rad * 0.2f)); drawCircle(medio, rad * 0.82f, c + Offset(lado * rad * 0.15f, -rad * 0.12f))
            if (rad > 1.5f) drawCircle(luz, rad * 0.4f, c + Offset(lado * rad * 0.36f, -rad * 0.36f))
            if (nieve) drawCircle(l.tono(C.NIEVE, lejos), rad * 0.6f, c + Offset(0f, -rad * 0.4f))
            return
        }
        // La masa oscura, hecha de bolas que se pisan: el borde queda festoneado.
        val n = if (rad > 7f) 7 else 5
        for (i in 0 until n) { val a = 6.283f * i / n + az.entre(-0.3f, 0.3f); drawCircle(osc, rad * az.entre(0.5f, 0.66f), c + Offset(cos(a) * rad * 0.5f - lado * rad * 0.1f, sin(a) * rad * 0.42f + rad * 0.12f)) }
        for (i in 0 until n) { val a = 6.283f * i / n + az.entre(-0.4f, 0.4f); drawCircle(medio, rad * az.entre(0.4f, 0.56f), c + Offset(cos(a) * rad * 0.44f + lado * rad * 0.1f, sin(a) * rad * 0.36f - rad * 0.06f)) }
        for (i in 0 until n - 2) { val a = -1.57f + lado * az.entre(0.1f, 1.3f); drawCircle(luz, rad * az.entre(0.2f, 0.32f), c + Offset(cos(a) * rad * az.entre(0.2f, 0.62f), sin(a) * rad * az.entre(0.2f, 0.56f))) }
        if (nieve) { val b = l.tono(C.NIEVE, lejos); for (i in 0 until n - 1) drawCircle(b, rad * az.entre(0.2f, 0.34f), c + Offset(az.entre(-0.6f, 0.6f) * rad, -rad * az.entre(0.3f, 0.7f))) }
    }

    /**
     * Una copa grande: no es una bola, sino varios pisos de follaje, cada uno con su luz y su sombra, sobre lo oscuro de
     * dentro. Los pisos del lado de la sombra van primero; el último es el de arriba, del lado del sol.
     */
    private fun DrawScope.copaGrande(l: Luces, r: Rayo, c: Offset, rad: Float, hoja: Color, lejos: Float, az: Azar, nieve: Boolean) {
        val lado = if (r.lat >= 0f) 1f else -1f
        val dentro = l.tono(mult(lerp(hoja, Color(0xFF16221E), 0.5f), r.color, 0.5f * r.contraste), lejos)
        drawOval(dentro, Offset(c.x - rad * 0.84f, c.y - rad * 0.58f), Size(rad * 1.68f, rad * 1.44f))
        val pisos = listOf(Offset(-0.52f * lado, 0.36f), Offset(0.5f * lado, 0.44f), Offset(-0.62f * lado, -0.1f), Offset(0.02f, 0.22f), Offset(0.6f * lado, 0f), Offset(-0.22f * lado, -0.5f), Offset(0.3f * lado, -0.42f))
        for ((i, d) in pisos.withIndex()) {
            val matiz = lerp(hoja, if (i % 2 == 0) Color(0xFF2E4A30) else Color(0xFFC0CC78), 0.12f)
            val q = c + Offset((d.x + az.entre(-0.06f, 0.06f)) * rad, d.y * rad); val rq = rad * az.entre(0.42f, 0.52f)
            if (rad >= 9f) { copa(l, r, q, rq, matiz, lejos, az, nieve); continue }
            // Las medianas: cada piso con tres toques (sombra, cuerpo y sol), que es lo que se alcanza a ver; así pesan poco.
            drawCircle(l.tono(mult(lerp(matiz, Color(0xFF1E2A26), 0.3f), r.color, 0.5f * r.contraste), lejos), rq, q + Offset(-lado * rq * 0.22f, rq * 0.2f))
            drawCircle(l.tono(matiz, lejos), rq * 0.84f, q + Offset(lado * rq * 0.14f, -rq * 0.1f))
            drawCircle(l.tono(lerp(matiz, Color(0xFFF4F0BC), 0.34f * r.contraste), lejos), rq * 0.44f, q + Offset(lado * rq * 0.36f, -rq * 0.36f))
            if (nieve) drawCircle(l.tono(C.NIEVE, lejos), rq * 0.5f, q + Offset(0f, -rq * 0.5f))
        }
    }

    /** Un árbol pelado: el tronco, el abanico de ramas y la pelusa de las ramillas. */
    fun DrawScope.pelado(l: Luces, pie: Offset, alto: Float, lejos: Float, az: Azar, corteza: Color = Color(0xFF6A5A4C), nieve: Boolean = false) {
        val c = l.tono(corteza, lejos)
        val cruz = pie + Offset(az.entre(-0.04f, 0.04f) * alto, -alto * 0.4f)
        drawLine(c, pie, cruz, (alto * 0.075f).coerceAtLeast(0.5f), StrokeCap.Round)
        drawOval(l.tono(lerp(corteza, Color(0xFFB0A498), 0.4f), lejos).copy(alpha = 0.3f), Offset(pie.x - alto * 0.36f, pie.y - alto), Size(alto * 0.72f, alto * 0.66f))
        val n = if (alto > 9f) 7 else 5
        for (i in 0 until n) {
            val a = -1.57f + (i - (n - 1) / 2f) * 0.34f + az.entre(-0.1f, 0.1f); val largo = alto * az.entre(0.36f, 0.58f)
            val fin = cruz + Offset(cos(a) * largo, sin(a) * largo)
            drawLine(c, cruz, fin, (alto * 0.035f).coerceAtLeast(0.35f), StrokeCap.Round)
            if (alto > 7f) for (s in listOf(-1f, 1f)) drawLine(c.copy(alpha = 0.8f), cruz + (fin - cruz) * 0.55f, fin + Offset(cos(a + s * 0.6f) * largo * 0.3f, sin(a + s * 0.6f) * largo * 0.3f), 0.35f, StrokeCap.Round)
            if (nieve && i % 2 == 0) drawLine(l.tono(C.NIEVE, lejos), cruz + (fin - cruz) * 0.3f + Offset(0f, -0.4f), fin + Offset(0f, -0.4f), 0.5f, StrokeCap.Round)
        }
    }

    /** Un árbol de hoja caduca: con [hoja] nula, pelado. [blanco]: la corteza de un abedul. */
    fun DrawScope.arbol(l: Luces, r: Rayo, pie: Offset, alto: Float, hoja: Color?, lejos: Float, az: Azar, blanco: Boolean = false, nieve: Boolean = false) {
        val corteza = if (blanco) Color(0xFFD8D4C8) else Color(0xFF6A5A4C)
        if (hoja == null) { pelado(l, pie, alto, lejos, az, corteza, nieve); return }
        val c = l.tono(corteza, lejos)
        if (alto > 13f) {
            // Los grandes: el tronco se abre en tres ramas que se meten en la copa.
            val cruz = pie + Offset(az.entre(-0.03f, 0.03f) * alto, -alto * 0.36f)
            drawPath(Path().apply { moveTo(pie.x - alto * 0.05f, pie.y); lineTo(cruz.x - alto * 0.028f, cruz.y); lineTo(cruz.x + alto * 0.028f, cruz.y); lineTo(pie.x + alto * 0.05f, pie.y); close() }, c)
            if (!blanco) drawLine(l.tono(mult(corteza, r.color, 0.6f * r.contraste), lejos), pie + Offset(if (r.lat >= 0f) -alto * 0.03f else alto * 0.03f, 0f), cruz + Offset(if (r.lat >= 0f) -alto * 0.016f else alto * 0.016f, 0f), alto * 0.022f)
            for (a in listOf(-2.3f, -1.57f, -0.9f)) drawLine(c, cruz, cruz + Offset(cos(a) * alto * 0.3f, sin(a) * alto * 0.3f), alto * 0.032f, StrokeCap.Round)
        } else drawLine(c, pie, pie + Offset(0f, -alto * 0.5f), (alto * 0.08f).coerceAtLeast(0.5f), StrokeCap.Round)
        copa(l, r, pie + Offset(0f, -alto * 0.62f), alto * 0.4f, hoja, lejos, az, nieve)
    }

    /** Un pino o un abeto pequeño, de un verde que no cambia. */
    fun DrawScope.abeto(l: Luces, r: Rayo, pie: Offset, alto: Float, lejos: Float, nieve: Boolean = false) {
        val lado = if (r.lat >= 0f) 1f else -1f
        val osc = l.tono(mult(PERENNE, r.color, 0.5f * r.contraste), lejos); val cla = l.tono(lerp(PERENNE, Color(0xFF7A9A5C), 0.5f * r.contraste), lejos)
        val w = alto * 0.26f
        drawPath(Path().apply { moveTo(pie.x, pie.y - alto); lineTo(pie.x + w, pie.y - alto * 0.08f); lineTo(pie.x - w, pie.y - alto * 0.08f); close() }, osc)
        drawPath(Path().apply { moveTo(pie.x, pie.y - alto); lineTo(pie.x + lado * w, pie.y - alto * 0.08f); lineTo(pie.x + lado * w * 0.1f, pie.y - alto * 0.08f); close() }, cla)
        if (nieve) drawPath(Path().apply { moveTo(pie.x, pie.y - alto); lineTo(pie.x + w * 0.5f, pie.y - alto * 0.55f); lineTo(pie.x - w * 0.5f, pie.y - alto * 0.55f); close() }, l.tono(C.NIEVE, lejos))
        drawLine(l.tono(Color(0xFF4E3A28), lejos), pie, pie + Offset(0f, -alto * 0.1f), (alto * 0.07f).coerceAtLeast(0.4f))
    }

    /**
     * Una franja de bosque de [x0] a [x1] con el pie en [base] (una función de la x) y unos [alto] de altura: dos filas
     * de copas de alturas distintas, con algún abeto y algún claro. En invierno quedan los troncos, la pelusa de las
     * ramas y lo perenne. [cada]: la distancia entre árboles.
     */
    fun DrawScope.arboleda(l: Luces, r: Rayo, x0: Float, x1: Float, base: (Float) -> Float, alto: Float, lejos: Float, est: Estacion, az: Azar, cada: Float = alto * 0.55f, nieve: Boolean = false, abetos: Float = 0.16f) {
        for (fila in 0..1) {
            var x = x0 + az.entre(0f, cada)
            while (x < x1) {
                val h = alto * az.entre(0.62f, 1.1f) * (if (fila == 0) 1.08f else 0.86f)
                val pie = Offset(x, base(x) + (if (fila == 0) -alto * 0.12f else alto * 0.06f))
                val suerte = az.real()
                if (suerte < abetos) abeto(l, r, pie, h * 1.15f, lejos + 0.03f * (1 - fila), nieve)
                else if (suerte < 0.94f) arbol(l, r, pie, h, hoja(est, az), lejos + 0.03f * (1 - fila), az, blanco = suerte > 0.8f, nieve = nieve)
                x += cada * az.entre(0.6f, 1.4f)
            }
        }
    }

    /**
     * Un manto de hiedra dentro de [zona] (se recorta a [forma]): sube desde abajo hasta una fracción [cuanta] de la
     * altura, con el borde en lenguas, y cuelga del borde de arriba. La hiedra no pierde la hoja; [roja], de 0 a 1, tiñe
     * una parte de parra virgen (en otoño).
     */
    fun DrawScope.hiedra(l: Luces, r: Rayo, forma: Path, zona: Rect, cuanta: Float, lejos: Float, az: Azar, roja: Float = 0f, cuelga: Float = 0.3f, nieve: Boolean = false) {
        if (cuanta <= 0.01f && cuelga <= 0.01f) return
        val propio = lerp(C.HIEDRA, C.TELA, 0.75f * roja)
        val osc = l.tono(mult(lerp(propio, Color(0xFF1E2A26), 0.25f), r.color, 0.4f * r.contraste), lejos); val medio = l.tono(propio, lejos)
        val luz = l.tono(lerp(propio, Color(0xFFDDE49C), 0.4f * r.contraste), lejos)
        val paso = (zona.width / 9f).coerceIn(2.2f, 9f)
        clipPath(forma) {
            if (cuanta > 0.01f) {
                // Las lenguas que suben: cada una hasta donde le toca.
                val borde = Path().apply {
                    moveTo(zona.left - 2f, zona.bottom + 2f)
                    var x = zona.left - 2f
                    while (x < zona.right + paso) {
                        val sube = zona.height * cuanta * az.entre(0.45f, 1.25f)
                        lineTo(x, zona.bottom - sube * 0.7f); cubicTo(x + paso * 0.2f, zona.bottom - sube * 1.05f, x + paso * 0.6f, zona.bottom - sube * 1.05f, x + paso * 0.8f, zona.bottom - sube * 0.6f)
                        x += paso
                    }
                    lineTo(zona.right + 2f, zona.bottom + 2f); close()
                }
                drawPath(borde, osc)
                clipPath(borde) {
                    val pm = ArrayList<Offset>(); val pl = ArrayList<Offset>()
                    val n = (zona.width * zona.height * cuanta / (3.2f + 9f * lejos)).toInt().coerceIn(6, 520)
                    repeat(n) { val p = Offset(az.entre(zona.left, zona.right), zona.bottom - az.real() * zona.height * cuanta * 1.2f); if (it % 3 == 0) pl += p else pm += p }
                    val g = (2.6f - 1.7f * lejos).coerceAtLeast(0.8f)
                    drawPoints(pm, PointMode.Points, medio, g, StrokeCap.Round); drawPoints(pl, PointMode.Points, luz.copy(alpha = 0.85f), g * 0.7f, StrokeCap.Round)
                    if (nieve) { val pn = ArrayList<Offset>(); repeat(n / 4) { pn += Offset(az.entre(zona.left, zona.right), zona.bottom - az.real() * zona.height * cuanta * 1.2f) }; drawPoints(pn, PointMode.Points, l.tono(C.NIEVE, lejos), g * 0.8f, StrokeCap.Round) }
                }
            }
            if (cuelga > 0.01f) {
                // Lo que cuelga del borde de arriba, en ristras.
                var x = zona.left + az.entre(0f, paso)
                while (x < zona.right) {
                    val largo = zona.height * cuelga * az.entre(0.2f, 1f)
                    if (az.real() < 0.7f) {
                        drawLine(osc, Offset(x, zona.top), Offset(x + az.entre(-0.6f, 0.6f), zona.top + largo), (paso * 0.5f).coerceIn(0.7f, 3f), StrokeCap.Round)
                        drawLine(medio, Offset(x + 0.4f, zona.top), Offset(x + 0.4f, zona.top + largo * 0.7f), (paso * 0.25f).coerceIn(0.5f, 1.6f), StrokeCap.Round)
                    }
                    x += paso * az.entre(0.5f, 1.1f)
                }
            }
        }
    }

    /** Un manojo de hojas sueltas alrededor de [c]: cada hoja es un trazo corto, oscuro por dentro y claro del lado de la luz. Para lo cercano. */
    fun DrawScope.hojarasca(l: Luces, r: Rayo, c: Offset, rad: Float, hoja: Color, az: Azar, cuantas: Int = 9) {
        val osc = l.tono(mult(lerp(hoja, Color(0xFF1E2A26), 0.35f), r.color, 0.4f * r.contraste)); val medio = l.tono(hoja); val luz = l.tono(lerp(hoja, Color(0xFFE4EAA0), 0.42f * r.contraste))
        val lado = if (r.lat >= 0f) 1f else -1f
        repeat(cuantas) {
            val a = az.entre(0f, 6.283f); val d = rad * az.real(); val p = c + Offset(cos(a) * d, sin(a) * d * 0.8f)
            val w = az.entre(2.4f, 3.8f); val giro = az.entre(-0.9f, 0.9f); val dir = Offset(cos(giro), sin(giro)) * (w * 0.42f)
            val suerte = az.real() + 0.25f * lado * (p.x - c.x) / rad - 0.22f * (p.y - c.y) / rad
            drawLine(if (suerte < 0.36f) osc else if (suerte < 0.8f) medio else luz, p - dir, p + dir, w * 0.6f, StrokeCap.Round)
        }
    }

    /** Una ristra de hiedra que cuelga de [desde], de [largo] unidades, con sus hojas a los lados: para lo cercano. */
    fun DrawScope.ristra(l: Luces, r: Rayo, desde: Offset, largo: Float, az: Azar, propio: Color = C.HIEDRA, mece: Float = 0f) {
        val osc = l.tono(lerp(propio, Color(0xFF1E2A26), 0.3f)); val medio = l.tono(propio); val luz = l.tono(lerp(propio, Color(0xFFDDE49C), 0.45f * r.contraste))
        val fin = desde + Offset(mece + az.entre(-2f, 2f), largo)
        val codo = desde + Offset(az.entre(-2.4f, 2.4f) + mece * 0.4f, largo * 0.5f)
        drawPath(Path().apply { moveTo(desde.x, desde.y); cubicTo(codo.x, codo.y, codo.x, codo.y, fin.x, fin.y) }, l.tono(Color(0xFF4A3E30)), style = Stroke(0.5f, cap = StrokeCap.Round))
        var t = 0.06f; var lado = 1f
        while (t <= 1f) {
            val q = 1f - t
            val p = desde * (q * q) + codo * (2f * q * t) + fin * (t * t)
            val n = az.entre(1.6f, 2.8f)
            val suerte = az.real()
            drawOval(if (suerte < 0.4f) osc else if (suerte < 0.82f) medio else luz, Offset(p.x + lado * 0.4f - n * (if (lado < 0f) 1f else 0f), p.y - n * 0.4f), Size(n, n * 0.8f))
            t += 2.1f / largo; lado = -lado
        }
    }
}
