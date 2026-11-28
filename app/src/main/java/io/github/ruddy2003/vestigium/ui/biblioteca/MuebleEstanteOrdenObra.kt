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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.arcoApuntado
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.ceil
import kotlin.math.floor

/**
 * El mueble de Orden: la librería encadenada, del mismo taller que la cajonera del Ideario (el mismo pavonado, los mismos
 * remaches de cabeza grande y el mismo latón). La cornisa honda con su canto de latón, la gola remachada, el friso de esmalte
 * carmesí en paneles y el arquitrabe, con dos torrecillas sobre los pilares; los pilares de viga de doble T remachada con su
 * capitel y su basa de latón; las repisas de perfil en U con la barra de latón de las cadenas y sus soportes; bajo cada tabla, el
 * guardapolvo de chapa calada en arquitos apuntados y las cartelas de las esquinas. Por detrás, el muro de placas.
 */
internal object ObraEstanteOrden {
    private val P = PaletaOrden
    private val M = MuebleEstanteOrden

    /** El hueco que deja cada pilar entre él y el borde (el muro que asoma a los lados del mueble), en anchos. */
    const val ORILLA = 0.016f
    /** El alto del guardapolvo calado, en dp. */
    const val GUARDAPOLVO = 8.5f
    /** A qué alto de la tabla va la barra de las cadenas (en fracción de la tabla, desde arriba). */
    const val BARRA = 0.47f

    /** El color de [c] en ([x], [y]): de día con la mancha de la luminaria; de noche, con la luz que le llega. */
    private fun DrawScope.en(c: Color, f: Float, x: Float, y: Float, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float, dia: Float = 0.3f): Color {
        val w = size.width
        val p = Offset(x, y)
        return if (a.oscuro) { val b = a.luz(c, cerca(p), frio(p)); if (f < 0f) mezcla(b, P.NOCHE, -f * 0.45f) else b }
        else a.luminaria(a.dia(c, f, cerca(p)), M.luminaria(x / w, y / w) * dia)
    }

    /**
     * Una franja horizontal [r] de [c] con la luz cambiando a lo ancho: con lumbre, en un degradado de nueve paradas (en tramos
     * se veían los escalones entre un charco y otro); de día, en cuatro tramos lisos y opacos (la luz de la sala apenas cambia).
     */
    private fun DrawScope.franjaH(r: Rect, c: Color, f: Float, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float, dia: Float = 0.3f) {
        if (r.width <= 0f || r.height <= 0f) return
        val y = r.center.y
        if (a.lumbre) {
            val paradas = Array(9) { i -> val x = r.left + r.width * i / 8f; (i / 8f) to en(c, f, x, y, a, cerca, frio, dia) }
            drawRect(Brush.horizontalGradient(*paradas, startX = r.left, endX = r.right), r.topLeft, r.size)
            return
        }
        val n = 4
        val paso = r.width / n
        for (i in 0 until n) {
            val x0 = r.left + paso * i
            drawRect(en(c, f, x0 + paso / 2, y, a, cerca, frio, dia), Offset(x0, r.top), Size(paso + (if (i < n - 1) 1f else 0f), r.height))
        }
    }

    /** El pincel de una franja horizontal (un degradado de cinco paradas), para pintar con él una figura cualquiera. */
    private fun DrawScope.pincelH(r: Rect, c: Color, f: Float, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float, dia: Float = 0.3f): Brush {
        val y = r.center.y
        val paradas = Array(5) { i -> val x = r.left + r.width * i / 4f; (i / 4f) to en(c, f, x, y, a, cerca, frio, dia) }
        return Brush.horizontalGradient(*paradas, startX = r.left, endX = maxOf(r.left + 1f, r.right))
    }

    /** El pincel de una franja vertical, con una parada en cada punto de [ys] (de arriba abajo, dentro de [r]). */
    private fun DrawScope.pincelV(r: Rect, c: Color, f: Float, ys: FloatArray, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float, dia: Float = 0.3f): Brush {
        val x = r.center.x
        val alto = maxOf(1f, r.height)
        val paradas = Array(ys.size) { i -> ((ys[i] - r.top) / alto).coerceIn(0f, 1f) to en(c, f, x, ys[i], a, cerca, frio, dia) }
        return Brush.verticalGradient(*paradas, startY = r.top, endY = r.top + alto)
    }

    /** Puntos a lo alto de [r] para los pinceles verticales: cada [paso] px, con los dos extremos. */
    private fun puntos(r: Rect, paso: Float): FloatArray {
        val n = (r.height / maxOf(8f, paso)).toInt().coerceIn(1, 40)
        return FloatArray(n + 1) { i -> r.top + r.height * i / n }
    }

    // ---------------------------------------------------------------- la cornisa y las torrecillas

    /**
     * La cornisa en [r] (de donde pisan las cosas de arriba hasta el primer casillero): la cara de arriba, el canto con su filo
     * de latón, la gola remachada, el junquillo, el friso de esmalte carmesí en paneles y el arquitrabe; los capiteles de los
     * pilares la cortan a los lados. Encima de los pilares, las dos torrecillas.
     */
    fun DrawScope.cornisa(r: Rect, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val w = size.width
        val y0 = r.top
        val l = M.LADO * w
        listOf(true, false).forEach { izq -> torrecilla(if (izq) Rect(w * ORILLA, y0 - w * 0.14f, l, y0) else Rect(w - l, y0 - w * 0.14f, w - w * ORILLA, y0), izq, a, cerca, frio) }
        val x0 = w * 0.004f; val x1 = w * 0.996f
        // La cara de arriba, vista apenas desde arriba: donde pisan las cosas.
        franjaH(Rect(x0, y0 - w * 0.006f, x1, y0 + w * 0.006f), P.PAVON_LUZ, 0.12f, a, cerca, frio, 0.6f)
        // El canto de la losa, con su filo de latón arriba.
        franjaH(Rect(x0, y0 + w * 0.006f, x1, y0 + w * 0.024f), P.PAVON, 0.02f, a, cerca, frio, 0.5f)
        franjaH(Rect(x0, y0 + w * 0.006f, x1, y0 + w * 0.0095f), P.LATON, 0.05f, a, cerca, frio, 0.5f)
        drawRect(a.brillo(0.5f, cerca(Offset(w * M.VELA_X, y0))), Offset(x0, y0 + w * 0.006f), Size(x1 - x0, 1f * u))
        drawRect(a.sombra(0.35f), Offset(x0, y0 + w * 0.021f), Size(x1 - x0, w * 0.003f))
        // La gola: hondo arriba (bajo el vuelo), clara abajo donde mira a la luz; una fila de remaches.
        val gola = Rect(w * 0.012f, y0 + w * 0.024f, w * 0.988f, y0 + w * 0.048f)
        franjaH(Rect(gola.left, gola.top, gola.right, gola.top + gola.height * 0.4f), P.PAVON_HONDO, -0.2f, a, cerca, frio, 0.2f)
        franjaH(Rect(gola.left, gola.top + gola.height * 0.4f, gola.right, gola.bottom), P.PAVON, 0.1f, a, cerca, frio, 0.5f)
        drawRect(a.sombra(0.5f), Offset(gola.left, gola.top), Size(gola.width, 1.6f * u))
        remachesFila(gola.left + w * 0.03f, gola.right - w * 0.02f, gola.top + gola.height * 0.66f, w * 0.05f, w * 0.0045f, a, cerca, frio)
        // El junquillo de latón.
        val junq = Rect(w * 0.012f, gola.bottom, w * 0.988f, gola.bottom + w * 0.008f)
        franjaH(junq, P.LATON, 0f, a, cerca, frio, 0.5f)
        drawRect(a.brillo(0.45f, cerca(junq.center)), Offset(junq.left, junq.top + 0.6f * u), Size(junq.width, 1f * u))
        drawRect(a.sombra(0.4f), Offset(junq.left, junq.bottom - 1.2f * u), Size(junq.width, 1.2f * u))
        // El friso de esmalte carmesí entre los capiteles, en paneles con su filete de latón y sus bollones.
        val friso = Rect(l, junq.bottom, w - l, y0 + w * 0.104f)
        friso(friso, a, cerca, frio)
        // El arquitrabe: una banda de acero con su canto de luz y sus remaches.
        val arq = Rect(w * 0.012f, friso.bottom, w * 0.988f, r.bottom)
        franjaH(arq, P.PAVON, 0f, a, cerca, frio, 0.4f)
        franjaH(Rect(arq.left, arq.top, arq.right, arq.top + w * 0.005f), P.LATON, 0f, a, cerca, frio, 0.4f)
        drawRect(a.sombra(0.4f), Offset(arq.left, arq.top + w * 0.005f), Size(arq.width, 1.2f * u))
        remachesFila(l + w * 0.03f, w - l - w * 0.02f, arq.top + w * 0.015f, w * 0.06f, w * 0.0038f, a, cerca, frio)
        drawRect(a.sombra(0.55f), Offset(arq.left, r.bottom - 3f - 1.4f * u), Size(arq.width, 1.4f * u))
        // Los capiteles de los pilares, que cortan el friso: un dado de acero con su collar de latón.
        listOf(true, false).forEach { izq ->
            val c = if (izq) Rect(w * ORILLA - w * 0.004f, junq.bottom, l + w * 0.004f, r.bottom - 3f) else Rect(w - l - w * 0.004f, junq.bottom, w - w * ORILLA + w * 0.004f, r.bottom - 3f)
            capitel(c, izq, a, cerca, frio)
        }
        // Los extremos de la cornisa, con su vuelta y su sombra en el muro de los lados.
        drawRect(a.sombra(0.45f), Offset(x1, y0), Size(w - x1, r.bottom - y0))
        drawRect(a.destello(0.4f, cerca(Offset(x0, y0)), frio(Offset(x0, y0))), Offset(x0, y0 - w * 0.006f), Size(1.2f * u, w * 0.03f))
    }

    /** Una fila de remaches de [xa] a [xb] a la altura [y], cada [paso], en tres caminos (sombra, cabeza y brillo). */
    private fun DrawScope.remachesFila(xa: Float, xb: Float, y: Float, paso: Float, rr: Float, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        if (paso <= 1f || xb <= xa) return
        val cab = Path(); val bri = Path()
        var x = xa
        while (x <= xb) {
            cab.addOval(Rect(x - rr, y - rr, x + rr, y + rr))
            bri.addOval(Rect(x - rr * 0.7f, y - rr * 0.75f, x - rr * 0.02f, y - rr * 0.08f))
            x += paso
        }
        val caja = Rect(xa - rr, y - rr, xb + rr, y + rr)
        translate(rr * 0.45f, rr * 0.5f) { drawPath(cab, a.sombra(0.4f)) }
        drawPath(cab, pincelH(caja, P.ACERO_MEDIO, 0f, a, cerca, frio, 0.5f))
        if (a.oscuro) drawPath(bri, pincelH(caja, P.DESTELLO, 0.1f, a, cerca, frio)) else drawPath(bri, a.destello(0.7f, 0f))
        drawPath(cab, a.sombra(0.25f), style = Stroke(0.5f * u))
    }

    /** El friso de esmalte carmesí en [r]: paneles con su filete de latón, bollones entre ellos y el esmalte saltado en las aristas. */
    private fun DrawScope.friso(r: Rect, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val w = size.width
        franjaH(r, P.GRANA, 0f, a, cerca, frio, 0.35f)
        // El esmalte, con su brillo arriba y su hondo abajo.
        drawRect(a.brillo(0.16f, cerca(r.center)), Offset(r.left, r.top + r.height * 0.12f), Size(r.width, r.height * 0.14f))
        drawRect(a.sombra(0.3f), Offset(r.left, r.bottom - r.height * 0.22f), Size(r.width, r.height * 0.22f))
        drawRect(a.sombra(0.45f), Offset(r.left, r.top), Size(r.width, 1.4f * u))
        // Los paneles: un filete de latón hundido; entre panel y panel, un bollón.
        val n = maxOf(2, (r.width / (w * 0.13f)).toInt())
        val paso = r.width / n
        val filete = Path()
        for (i in 0 until n) {
            val pr = Rect(r.left + paso * i + w * 0.012f, r.top + r.height * 0.22f, r.left + paso * (i + 1) - w * 0.012f, r.bottom - r.height * 0.24f)
            filete.addRect(pr)
        }
        translate(0.8f * u, 0.9f * u) { drawPath(filete, a.sombra(0.4f), style = Stroke(1.2f * u)) }
        drawPath(filete, pincelH(r, P.LATON, 0f, a, cerca, frio, 0.4f), style = Stroke(1.2f * u))
        for (i in 1 until n) {
            val c = Offset(r.left + paso * i, r.center.y)
            roblonOrden(c, w * 0.0075f, a, cerca(c), P.LATON, frio(c))
        }
        // El esmalte saltado, donde asoma el acero, sobre todo en las aristas.
        for (q in 0 until 14) {
            val x = r.left + r.width * azar(905, q)
            val arriba = q % 3 != 0
            val y = if (arriba) r.top + 1.2f * u + r.height * 0.12f * azar(906, q) else r.top + r.height * (0.3f + 0.5f * azar(906, q))
            val s = w * (0.004f + 0.01f * azar(907, q))
            drawOval(en(P.ACERO_MEDIO, 0f, x, y, a, cerca, frio).copy(alpha = 0.8f), Offset(x, y), Size(s * 1.6f, s * 0.6f))
        }
        // De día, el lado de la luminaria más vivo.
        if (!a.oscuro) drawRect(Brush.horizontalGradient(listOf(a.brillo(0.12f, 0f), Color.Transparent), r.left, r.left + r.width * 0.4f), r.topLeft, Size(r.width * 0.4f, r.height))
    }

    /** El capitel de un pilar en [r]: el dado de acero con su collar de latón abajo, su luz a la izquierda y su sombra a la derecha. */
    private fun DrawScope.capitel(r: Rect, izq: Boolean, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val w = size.width
        drawRect(a.sombra(0.4f), Offset(r.right, r.top), Size(2.4f * u, r.height))
        franjaH(r, P.PAVON, 0.04f, a, cerca, frio, 0.5f)
        drawRect(a.destello(0.45f, cerca(r.topLeft), frio(r.topLeft)), r.topLeft, Size(1.4f * u, r.height))
        drawRect(a.sombra(0.35f), Offset(r.right - r.width * 0.2f, r.top), Size(r.width * 0.2f, r.height))
        // Un panel hundido con su remache.
        val pan = Rect(r.left + r.width * 0.2f, r.top + r.height * 0.14f, r.right - r.width * 0.2f, r.top + r.height * 0.62f)
        drawRect(a.sombra(0.3f), pan.topLeft, pan.size)
        drawRect(a.destello(0.3f, cerca(pan.center), frio(pan.center)), Offset(pan.left, pan.bottom - 1f * u), Size(pan.width, 1f * u))
        drawRect(a.destello(0.25f, cerca(pan.center), frio(pan.center)), Offset(pan.right - 1f * u, pan.top), Size(1f * u, pan.height))
        roblonOrden(pan.center, w * 0.006f, a, cerca(pan.center), P.LATON, frio(pan.center))
        // El collar de latón.
        val col = Rect(r.left, r.bottom - r.height * 0.26f, r.right, r.bottom - r.height * 0.06f)
        franjaH(col, P.LATON, 0f, a, cerca, frio, 0.5f)
        drawRect(a.brillo(0.5f, cerca(col.center)), Offset(col.left, col.top + 0.5f * u), Size(col.width, 1f * u))
        drawRect(a.sombra(0.4f), Offset(col.left, col.bottom - 1.2f * u), Size(col.width, 1.2f * u))
    }

    /** Una torrecilla en [r], sobre un pilar: el fuste remachado con su banda de latón, el chapitel en pirámide y la bola de latón. */
    private fun DrawScope.torrecilla(r: Rect, izq: Boolean, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val w = size.width
        val cx = r.center.x
        val fw = r.width * 0.78f
        val fuste = Rect(cx - fw / 2, r.top + r.height * 0.4f, cx + fw / 2, r.bottom)
        // Su sombra en el muro, abajo a la derecha (de noche, la que tiende la vela).
        if (!a.oscuro) drawRect(a.sombra(0.22f), Offset(fuste.left + w * 0.018f, fuste.top + w * 0.012f), Size(fuste.width, fuste.height))
        val ys = floatArrayOf(fuste.top, fuste.bottom)
        drawRect(pincelV(fuste, P.PAVON, 0.04f, ys, a, cerca, frio, 0.5f), fuste.topLeft, fuste.size)
        drawRect(a.destello(0.45f, cerca(fuste.center), frio(fuste.center)), fuste.topLeft, Size(1.4f * u, fuste.height))
        drawRect(a.sombra(0.4f), Offset(fuste.right - fuste.width * 0.26f, fuste.top), Size(fuste.width * 0.26f, fuste.height))
        listOf(0.3f, 0.7f).forEach { fx ->
            var y = fuste.top + w * 0.014f
            while (y < fuste.bottom - w * 0.006f) {
                val p = Offset(fuste.left + fuste.width * fx, y)
                roblonOrden(p, w * 0.0036f, a, cerca(p), P.ACERO_MEDIO, frio(p))
                y += w * 0.026f
            }
        }
        // La banda de latón.
        val banda = Rect(fuste.left - 1.4f * u, fuste.top + fuste.height * 0.12f, fuste.right + 1.4f * u, fuste.top + fuste.height * 0.12f + w * 0.009f)
        franjaH(banda, P.LATON, 0f, a, cerca, frio, 0.5f)
        drawRect(a.brillo(0.5f, cerca(banda.center)), Offset(banda.left, banda.top + 0.4f * u), Size(banda.width, 0.9f * u))
        drawRect(a.sombra(0.4f), Offset(banda.left, banda.bottom - 1f * u), Size(banda.width, 1f * u))
        // La cornisilla del fuste y el chapitel en pirámide: la cara de la luz y la de la sombra.
        val cor = Rect(fuste.left - w * 0.006f, fuste.top - w * 0.008f, fuste.right + w * 0.006f, fuste.top)
        franjaH(cor, P.PAVON_LUZ, 0f, a, cerca, frio, 0.6f)
        drawRect(a.sombra(0.4f), Offset(cor.left, cor.bottom - 1f * u), Size(cor.width, 1f * u))
        val punta = Offset(cx, r.top + r.height * 0.06f)
        val izqC = poligonoOrden(cor.left + w * 0.003f, cor.top, cx, cor.top, punta.x, punta.y)
        val derC = poligonoOrden(cx, cor.top, cor.right - w * 0.003f, cor.top, punta.x, punta.y)
        val kp = cerca(Offset(cx, cor.top)); val fp = frio(Offset(cx, cor.top))
        if (!a.oscuro) drawPath(poligonoOrden(cx + w * 0.02f, cor.top + w * 0.01f, cor.right + w * 0.02f, cor.top + w * 0.01f, punta.x + w * 0.02f, punta.y + w * 0.01f), a.sombra(0.18f))
        drawPath(izqC, a.dia(P.PAVON_LUZ, 0.1f, kp, fp).let { if (!a.oscuro) a.luminaria(it, M.luminaria(cx / w, cor.top / w) * 0.5f) else it })
        drawPath(derC, a.dia(P.PAVON, -0.25f, kp, fp))
        drawLine(a.destello(0.5f, kp, fp), Offset(cor.left + w * 0.003f, cor.top), punta, 1f * u)
        // Las tres escamas de chapa del chapitel, una fila de remaches.
        listOf(0.35f, 0.62f).forEach { f ->
            val y = punta.y + (cor.top - punta.y) * f
            val dx = (cor.width / 2 - w * 0.003f) * f
            drawLine(a.sombra(0.35f), Offset(cx - dx, y), Offset(cx + dx, y), 1f * u)
        }
        // La bola de latón del remate.
        val br = w * 0.0085f
        val bc = Offset(cx, punta.y - br * 0.6f)
        drawCircle(a.sombra(0.3f), br, bc + Offset(br * 0.4f, br * 0.4f))
        drawCircle(a.dia(P.LATON, 0f, cerca(bc), frio(bc)), br, bc)
        drawCircle(a.dia(P.LATON_HONDO, 0f, cerca(bc), frio(bc)), br * 0.75f, bc + Offset(br * 0.25f, br * 0.25f))
        drawCircle(a.dia(P.LATON, 0.1f, cerca(bc), frio(bc)), br * 0.6f, bc - Offset(br * 0.12f, br * 0.12f))
        drawCircle(a.destello(0.9f, cerca(bc), frio(bc)), br * 0.26f, bc - Offset(br * 0.38f, br * 0.4f))
    }

    // ---------------------------------------------------------------- los casilleros

    /** El camino de los arquitos del guardapolvo para un ancho dado, armado una vez por ancho y densidad. */
    private var claveArcos = ""
    private var caminoArcos = Path()

    private fun arcos(ancho: Float, u: Float): Path {
        val clave = "$ancho:$u"
        if (clave != claveArcos) {
            val p = Path()
            val alto = GUARDAPOLVO * u
            val n = maxOf(1, (ancho / (13f * u)).toInt())
            val paso = ancho / n
            for (i in 0 until n) {
                val x0 = paso * i + 1.4f * u; val x1 = paso * (i + 1) - 1.4f * u
                p.addPath(arcoApuntado(Rect(x0, alto * 0.3f, x1, alto + 1f), alto * 0.42f))
            }
            caminoArcos = p; claveArcos = clave
        }
        return caminoArcos
    }

    /**
     * El casillero [k] en [r]: el muro por detrás, la sombra franca de la tabla de arriba y del pilar de la luz, la cara de dentro
     * de los pilares, la panza de la tabla de arriba con su guardapolvo calado y sus cartelas, el suelo de la repisa y, de noche,
     * la luz que le llega (y el charco de la vela del hueco, si hay una en [xVela]).
     */
    fun DrawScope.casillero(k: Int, r: Rect, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float, xVela: Float) {
        val u = density
        val w = size.width
        with(MuroEstanteOrden) { fondo(r, a, cerca, frio) }
        if (a.lumbre) {
            // El resplandor cálido en el muro del fondo, del lado de la luz que le llega, que se apaga hacia el otro lado.
            val y = r.top + r.height * 0.45f
            val kI = cerca(Offset(r.left, y)); val kD = cerca(Offset(r.right, y))
            val kMax = maxOf(kI, kD)
            if (kMax > 0.2f) {
                val izqMas = kI >= kD
                val c = Offset(if (izqMas) r.left else r.right, r.top + r.height * 0.3f)
                val rr = r.width * (0.3f + 0.4f * kMax)
                drawRect(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = ((kMax - 0.14f) * (if (a.oscuro) 0.42f else 0.2f)).coerceAtMost(0.28f)), P.LLAMA.copy(alpha = 0f)), c, rr),
                    Offset(if (izqMas) r.left else maxOf(r.left, r.right - rr), r.top), Size(minOf(rr, r.width), r.height))
            }
            if (xVela >= 0f) {
                val c = Offset(xVela, r.bottom - r.height * M.VELA_HUECO_Y)
                val rr = w * 0.3f
                val f = if (a.dormida) 0.55f else if (a.oscuro) 1f else 0.45f
                drawCircle(Brush.radialGradient(0f to P.LLAMA.copy(alpha = 0.42f * f), 0.3f to P.LLAMA.copy(alpha = 0.16f * f), 1f to P.LLAMA.copy(alpha = 0f), center = c, radius = rr), rr, c)
            }
        }
        // La sombra franca del pilar de la luz y de la tabla de arriba en el muro, con su penumbra.
        val hT = r.height * 0.2f; val wL = w * 0.05f
        val luzIzq = !a.oscuro || a.dormida
        val sombra = if (luzIzq) Path().apply {
            moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.top + hT * 0.55f); lineTo(r.left + wL + hT * 0.6f, r.top + hT * 0.55f)
            lineTo(r.left + wL, r.top + hT + wL * 0.5f); lineTo(r.left + wL * 0.8f, r.bottom); lineTo(r.left, r.bottom); close()
        } else Path().apply { addRect(Rect(r.left, r.top, r.right, r.top + hT * 0.45f)) }
        drawPath(sombra, a.sombra(if (a.oscuro) 0.28f else 0.36f))
        // La cara de dentro de los pilares (el alma de la viga): la de la izquierda, en sombra; la de la derecha, a la luz.
        val hondo = 6.5f * u
        val ys = floatArrayOf(r.top, r.center.y, r.bottom)
        val caraIzq = poligonoOrden(r.left, r.top, r.left + hondo, r.top + hondo * 0.7f, r.left + hondo, r.bottom - 3f * u, r.left, r.bottom)
        drawPath(caraIzq, pincelV(Rect(r.left, r.top, r.left + hondo, r.bottom), P.PAVON_HONDO, -0.3f, ys, a, cerca, frio, 0.05f))
        drawLine(a.sombra(0.4f), Offset(r.left + hondo, r.top + hondo * 0.7f), Offset(r.left + hondo, r.bottom - 3f * u), 0.8f * u)
        val caraDer = poligonoOrden(r.right, r.top, r.right - hondo, r.top + hondo * 0.7f, r.right - hondo, r.bottom - 3f * u, r.right, r.bottom)
        drawPath(caraDer, pincelV(Rect(r.right - hondo, r.top, r.right, r.bottom), P.PAVON, 0.06f, ys, a, cerca, frio, 0.3f))
        drawLine(a.sombra(0.25f), Offset(r.right - hondo, r.top + hondo * 0.7f), Offset(r.right - hondo, r.bottom - 3f * u), 0.7f * u)
        // El suelo de la repisa, visto apenas desde arriba.
        val suelo = Rect(r.left + hondo * 0.5f, r.bottom - 4f * u, r.right - hondo * 0.5f, r.bottom)
        franjaH(suelo, P.PAVON_LUZ, -0.08f, a, cerca, frio, 0.3f)
        drawLine(a.sombra(0.45f), Offset(suelo.left, suelo.top), Offset(suelo.right, suelo.top), 0.9f * u)
        // La panza de la tabla de arriba y el guardapolvo calado.
        guardapolvo(Rect(r.left, r.top, r.right, r.top + GUARDAPOLVO * u), a, cerca, frio)
        // Las cartelas de las esquinas de arriba, remachadas.
        listOf(true, false).forEach { izq ->
            val x = if (izq) r.left else r.right
            val s = if (izq) 1f else -1f
            val cart = poligonoOrden(x, r.top, x + s * 15f * u, r.top, x + s * 15f * u, r.top + GUARDAPOLVO * u, x + s * 3f * u, r.top + 17f * u, x, r.top + 17f * u)
            translate(1.2f * u, 1.4f * u) { drawPath(cart, a.sombra(0.4f)) }
            drawPath(cart, en(P.PAVON, if (izq) -0.1f else 0.05f, x, r.top, a, cerca, frio, 0.4f))
            drawLine(a.destello(0.35f, cerca(Offset(x, r.top)), frio(Offset(x, r.top))), Offset(x + s * 15f * u, r.top + GUARDAPOLVO * u), Offset(x + s * 3f * u, r.top + 17f * u), 0.8f * u)
            val p = Offset(x + s * 5f * u, r.top + 11f * u)
            roblonOrden(p, 1.6f * u, a, cerca(p), P.ACERO_MEDIO, frio(p))
        }
    }

    /** El guardapolvo calado bajo la tabla de arriba: chapa con su junquillo y una fila de arquitos apuntados recortados. */
    private fun DrawScope.guardapolvo(r: Rect, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        franjaH(Rect(r.left, r.top, r.right, r.top + 2.4f * u), P.PAVON_HONDO, -0.2f, a, cerca, frio, 0.1f)
        val chapa = Rect(r.left, r.top + 2.4f * u, r.right, r.bottom)
        franjaH(chapa, P.PAVON, 0.04f, a, cerca, frio, 0.3f)
        val arcos = arcos(r.width, u)
        // Los vanos dejan ver el muro en sombra; el canto de cada uno coge la luz abajo a la izquierda.
        translate(r.left, r.top) {
            drawPath(arcos, pincelH(Rect(0f, 0f, r.width, r.height), P.PARED, -0.55f, a, { p -> cerca(p + r.topLeft) }, { p -> frio(p + r.topLeft) }, 0f))
            translate(0.7f * u, 0.7f * u) { drawPath(arcos, a.sombra(0.35f), style = Stroke(0.8f * u)) }
            drawPath(arcos, a.destello(0.25f, cerca(r.center), frio(r.center)), style = Stroke(0.6f * u))
        }
        drawRect(a.brillo(0.3f, cerca(r.center)), Offset(r.left, chapa.top), Size(r.width, 0.8f * u))
        drawRect(a.sombra(0.45f), Offset(r.left, r.bottom - 0.6f * u), Size(r.width, 1.2f * u))
    }

    // ---------------------------------------------------------------- las tablas

    /**
     * La tabla [k] en [r]: el perfil en U de acero (la cara de arriba con su luz, el labio y la cara), la barra de latón de las
     * cadenas con sus soportes, los remaches de las puntas y la línea de sombra que echa abajo.
     */
    fun DrawScope.tabla(k: Int, r: Rect, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val w = size.width
        val h = r.height
        // Debajo, la fila entera (de píxel entero) del color de la cara, para que la junta con el casillero no deje asomar nada.
        drawRect(en(P.PAVON, 0f, r.center.x, r.center.y, a, cerca, frio), Offset(r.left, floor(r.top)), Size(r.width, ceil(r.bottom) - floor(r.top)))
        franjaH(Rect(r.left, r.top, r.right, r.top + h * 0.12f), P.PAVON_LUZ, 0.25f, a, cerca, frio, 0.6f)
        franjaH(Rect(r.left, r.top + h * 0.12f, r.right, r.top + h * 0.22f), P.PAVON, 0.06f, a, cerca, frio, 0.4f)
        drawRect(a.sombra(0.35f), Offset(r.left, r.top + h * 0.22f), Size(r.width, 1f * u))
        franjaH(Rect(r.left, r.top + h * 0.22f + 1f * u, r.right, r.bottom), P.PAVON, -0.04f, a, cerca, frio, 0.3f)
        drawRect(a.brillo(0.4f, cerca(Offset(w * M.VELA_X, r.top))), Offset(r.left, r.top), Size(r.width, 0.9f * u))
        // El roce: rayas finas y algún golpe en el labio.
        for (j in 0 until 8) {
            val x = r.left + r.width * azar(k * 7 + 3, j)
            val y = r.top + h * (0.25f + 0.6f * azar(k * 7 + 4, j))
            drawLine(a.destello(0.22f, cerca(Offset(x, y)), frio(Offset(x, y))), Offset(x, y), Offset(x + w * (0.01f + 0.025f * azar(k * 7 + 5, j)), y + 0.4f * u), 0.6f * u)
        }
        // La barra de latón de las cadenas, con su sombra en la cara y sus soportes.
        val yb = r.top + h * BARRA
        val gb = h * 0.17f
        drawRect(a.sombra(0.45f), Offset(r.left, yb + gb * 0.5f), Size(r.width, gb * 0.7f))
        val barra = Rect(r.left + 4f * u, yb - gb / 2, r.right - 4f * u, yb + gb / 2)
        franjaH(barra, P.LATON, 0f, a, cerca, frio, 0.5f)
        drawRect(a.brillo(0.6f, cerca(barra.center)), Offset(barra.left, barra.top + gb * 0.18f), Size(barra.width, gb * 0.22f))
        drawRect(a.sombra(0.3f), Offset(barra.left, barra.bottom - gb * 0.3f), Size(barra.width, gb * 0.3f))
        val n = maxOf(2, (r.width / (w * 0.21f)).toInt())
        for (i in 0..n) {
            val x = r.left + 9f * u + (r.width - 18f * u) * i / n
            val sop = Rect(x - 3.4f * u, yb - gb * 0.95f, x + 3.4f * u, yb + gb * 0.95f)
            drawRect(a.sombra(0.4f), sop.topLeft + Offset(1f * u, 1.2f * u), sop.size)
            drawRect(en(P.PAVON_LUZ, 0f, x, yb, a, cerca, frio, 0.4f), sop.topLeft, sop.size)
            drawRect(a.sombra(0.35f), Offset(sop.right - 1.4f * u, sop.top), Size(1.4f * u, sop.height))
            val p = Offset(x, yb)
            roblonOrden(p, 1.5f * u, a, cerca(p), P.ACERO_MEDIO, frio(p))
        }
        drawRect(a.sombra(0.55f), Offset(r.left, r.bottom - 1f * u), Size(r.width, 1f * u))
        // En los Sueños oscuros, el filo frío de la luz de guardia en el canto de la tabla.
        if (a.dormida) for (i in 0 until 6) {
            val x0 = r.left + r.width * i / 6f
            val kv = frio(Offset(x0 + r.width / 12f, r.top))
            if (kv > 0.1f) drawRect(a.filoFrio((0.9f * kv).coerceAtMost(0.7f)), Offset(x0, r.top + 0.3f * u), Size(r.width / 6f + 1f, 1.1f * u))
        }
    }

    // ---------------------------------------------------------------- los pilares

    /**
     * El pilar en [r] ([izq]: el de la izquierda): el muro que asoma por fuera, el ala de la viga con sus dos filas de remaches
     * (luz del lado de la luminaria, sombra del otro), el capitel ya va en la cornisa; en cada tabla, la abrazadera de latón; abajo,
     * la basa de latón. En el de la izquierda, el piloto rojo del Ideario, atornillado a media altura.
     */
    fun DrawScope.pilar(r: Rect, izq: Boolean, d: DisposicionEstante, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val w = size.width
        val orilla = w * ORILLA
        val muro = if (izq) Rect(r.left, r.top, r.left + orilla, r.bottom) else Rect(r.right - orilla, r.top, r.right, r.bottom)
        val ala = if (izq) Rect(r.left + orilla, r.top, r.right, r.bottom) else Rect(r.left, r.top, r.right - orilla, r.bottom)
        val ys = puntos(r, w * 0.12f)
        // El muro que asoma por fuera: a la luz por la izquierda; a la derecha, en la sombra del pilar.
        drawRect(pincelV(muro, P.PARED, if (izq) 0f else -0.3f, ys, a, cerca, frio, 0.5f), muro.topLeft, muro.size)
        if (!izq) drawRect(a.sombra(0.35f), muro.topLeft, Size(muro.width * 0.6f, muro.height))
        // El ala de la viga.
        drawRect(pincelV(ala, P.PAVON, if (izq) 0.03f else -0.02f, ys, a, cerca, frio, 0.45f), ala.topLeft, ala.size)
        drawRect(pincelV(Rect(ala.left, ala.top, ala.left + ala.width * 0.16f, ala.bottom), P.PAVON_LUZ, 0.05f, ys, a, cerca, frio, 0.6f),
            ala.topLeft, Size(ala.width * 0.16f, ala.height))
        drawRect(a.destello(0.45f, 0f), ala.topLeft, Size(1.3f * u, ala.height))
        drawRect(a.sombra(0.42f), Offset(ala.right - ala.width * 0.18f, ala.top), Size(ala.width * 0.18f, ala.height))
        drawRect(a.sombra(0.25f), Offset(ala.left + ala.width * 0.47f, ala.top), Size(1.2f * u, ala.height))
        // De noche, el filo cálido del canto que mira a la vela más cercana.
        if (a.lumbre) {
            val n = ys.size
            for (i in 0 until n - 1) {
                val y0 = ys[i]; val y1 = ys[i + 1]
                val kk = cerca(Offset(if (izq) ala.right else ala.left, (y0 + y1) / 2))
                if (kk > 0.15f) drawRect(a.filo(0.55f * kk), Offset(if (izq) ala.right - 1.4f * u else ala.left, y0), Size(1.4f * u, y1 - y0 + 1f))
            }
        }
        // Las dos filas de remaches, en tres caminos.
        val cab = Path(); val bri = Path()
        val rr = w * 0.0048f
        listOf(0.27f, 0.7f).forEach { fx ->
            val x = ala.left + ala.width * fx
            var y = ala.top + w * 0.02f
            while (y < ala.bottom - w * 0.01f) {
                cab.addOval(Rect(x - rr, y - rr, x + rr, y + rr))
                bri.addOval(Rect(x - rr * 0.7f, y - rr * 0.75f, x - rr * 0.02f, y - rr * 0.08f))
                y += w * 0.034f
            }
        }
        translate(rr * 0.45f, rr * 0.5f) { drawPath(cab, a.sombra(0.4f)) }
        drawPath(cab, pincelV(ala, P.ACERO_MEDIO, 0.02f, ys, a, cerca, frio, 0.5f))
        if (a.oscuro) drawPath(bri, pincelV(ala, P.DESTELLO, 0.1f, ys, a, cerca, frio)) else drawPath(bri, a.destello(0.7f, 0f))
        // El roce de las manos a media altura y alguna chorrera de óxido.
        for (q in 0 until 6) {
            val y = ala.top + ala.height * azar(if (izq) 951 else 952, q)
            val x = ala.left + ala.width * (0.1f + 0.8f * azar(if (izq) 953 else 954, q))
            drawLine(a.destello(0.25f, cerca(Offset(x, y)), frio(Offset(x, y))), Offset(x, y), Offset(x + 0.5f * u, y + w * (0.02f + 0.04f * azar(955, q))), 1.1f * u, StrokeCap.Round)
        }
        for (q in 0 until 3) {
            val y = ala.top + ala.height * azar(if (izq) 956 else 957, q)
            val x = ala.left + ala.width * (0.27f + 0.43f * (q % 2))
            drawLine(en(P.OXIDO, 0f, x, y, a, cerca, frio).copy(alpha = 0.4f), Offset(x + 0.4f * u, y + rr), Offset(x + 0.8f * u, y + rr + w * 0.03f), 1.4f * u, StrokeCap.Round)
        }
        // La abrazadera de latón en cada tabla.
        d.tablas.forEach { t ->
            val ab = Rect(ala.left - 1f * u, t.top + t.height * 0.18f, ala.right + 1f * u, t.bottom - t.height * 0.18f)
            drawRect(a.sombra(0.35f), ab.topLeft + Offset(1.4f * u, 1.6f * u), ab.size)
            franjaH(ab, P.LATON, 0f, a, cerca, frio, 0.5f)
            drawRect(a.brillo(0.5f, cerca(ab.center)), Offset(ab.left, ab.top + 0.5f * u), Size(ab.width, 0.9f * u))
            drawRect(a.sombra(0.4f), Offset(ab.left, ab.bottom - 1.2f * u), Size(ab.width, 1.2f * u))
            listOf(0.27f, 0.7f).forEach { fx ->
                val p = Offset(ala.left + ala.width * fx, ab.center.y)
                roblonOrden(p, 1.7f * u, a, cerca(p), P.LATON_HONDO, frio(p))
            }
        }
        // La basa de latón.
        val basa = Rect(ala.left - w * 0.004f, r.bottom - w * 0.03f, ala.right + w * 0.004f, r.bottom)
        franjaH(basa, P.LATON, 0f, a, cerca, frio, 0.4f)
        drawRect(a.brillo(0.5f, cerca(basa.center)), Offset(basa.left, basa.top + 0.5f * u), Size(basa.width, 1f * u))
        drawRect(a.sombra(0.35f), Offset(basa.left, basa.top + basa.height * 0.45f), Size(basa.width, 1.2f * u))
        drawRect(a.sombra(0.4f), Offset(basa.right - basa.width * 0.2f, basa.top), Size(basa.width * 0.2f, basa.height))
        if (izq) piloto(ala, d, a, cerca, frio)
    }

    /** El piloto rojo del Ideario, atornillado al pilar a media altura: su caja con remaches, el cristal y la rejilla. */
    private fun DrawScope.piloto(ala: Rect, d: DisposicionEstante, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val w = size.width
        val rep = d.repisas.getOrNull(1) ?: d.repisas.firstOrNull() ?: return
        val c = Offset(ala.center.x, rep.top + rep.height * 0.42f)
        val kr = cerca(c); val fr = frio(c)
        val caja = Rect(c.x - w * 0.022f, c.y - w * 0.022f, c.x + w * 0.022f, c.y + w * 0.022f)
        if (a.oscuro) {
            val rr = w * 0.13f
            drawCircle(Brush.radialGradient(listOf(P.PILOTO.copy(alpha = if (a.dormida) 0.18f else 0.3f), P.PILOTO.copy(alpha = 0f)), c, rr), rr, c)
        }
        drawRect(a.sombra(0.4f), caja.topLeft + Offset(w * 0.005f, w * 0.005f), caja.size)
        drawRect(a.dia(P.ACERO_HONDO, 0f, kr, fr), caja.topLeft, caja.size)
        drawRect(a.destello(0.35f, kr, fr), caja.topLeft, Size(caja.width, 1.2f * u))
        drawRect(a.destello(0.25f, kr, fr), caja.topLeft, Size(1.2f * u, caja.height))
        drawRect(a.sombra(0.4f), Offset(caja.right - 1.6f * u, caja.top), Size(1.6f * u, caja.height))
        listOf(caja.topLeft + Offset(3f * u, 3f * u), Offset(caja.right - 3f * u, caja.top + 3f * u), Offset(caja.left + 3f * u, caja.bottom - 3f * u), caja.bottomRight - Offset(3f * u, 3f * u))
            .forEach { drawCircle(a.dia(P.ACERO_CLARO, 0f, kr, fr), 1.1f * u, it) }
        val rr = w * 0.0135f
        drawCircle(if (a.oscuro) P.PILOTO else mezcla(P.PILOTO, P.GRANA_HONDA, if (a.noche) 0.25f else 0.45f), rr, c)
        if (a.oscuro) drawCircle(Color(0xFFFFB0A0), rr * 0.45f, c - Offset(rr * 0.15f, rr * 0.15f))
        else drawCircle(P.LUZ_FRIA.copy(alpha = 0.55f), rr * 0.3f, c - Offset(rr * 0.38f, rr * 0.38f))
        val reja = a.dia(P.ACERO_CLARO, 0f, kr, fr)
        drawCircle(reja, rr * 1.12f, c, style = Stroke(1.4f * u))
        listOf(-0.45f, 0f, 0.45f).forEach { f -> drawLine(reja, Offset(c.x + rr * f, c.y - rr * 1.05f), Offset(c.x + rr * f, c.y + rr * 1.05f), 1f * u) }
        drawLine(reja, Offset(c.x - rr * 1.05f, c.y), Offset(c.x + rr * 1.05f, c.y), 1f * u)
    }

    // ---------------------------------------------------------------- las placas de los meses

    /** La placa de latón de un mes en la tabla: el marco en relieve, el filete hundido, cuatro tornillos, su sombra y el rótulo. */
    fun DrawScope.placa(p: PlacaEstante, medidor: TextMeasurer, a: AireOrden, k: Float, fr: Float) {
        val u = density
        val r = Rect(p.r.left, p.r.top - 1f * u, p.r.right, p.r.bottom + 1f * u)
        val color = a.placa(k, fr)
        drawRect(a.sombra(0.5f), r.topLeft + Offset(1.6f * u, 2f * u), r.size)
        drawRect(color, r.topLeft, r.size)
        drawRect(if (a.oscuro) P.LLAMA.copy(alpha = 0.3f * k + 0.08f) else P.LATON_BRILLO.copy(alpha = 0.8f), r.topLeft, Size(r.width, 1.4f * u))
        drawRect(if (a.oscuro) P.LLAMA.copy(alpha = 0.2f * k + 0.05f) else P.LATON_BRILLO.copy(alpha = 0.6f), r.topLeft, Size(1.4f * u, r.height))
        drawRect(mezcla(color, P.LATON_HONDO, 0.6f), Offset(r.left, r.bottom - 1.6f * u), Size(r.width, 1.6f * u))
        drawRect(mezcla(color, P.LATON_HONDO, 0.5f), Offset(r.right - 1.6f * u, r.top), Size(1.6f * u, r.height))
        val i = 3.2f * u
        drawRect(mezcla(color, P.LATON_HONDO, 0.45f), Offset(r.left + i, r.top + i), Size(r.width - 2 * i, r.height - 2 * i), style = Stroke(0.8f * u))
        if (!a.oscuro) drawPath(poligonoOrden(r.left + r.width * 0.06f, r.top, r.left + r.width * 0.12f, r.top, r.left + r.width * 0.08f, r.bottom, r.left + r.width * 0.02f, r.bottom), P.LUZ_FRIA.copy(alpha = 0.12f))
        listOf(Offset(r.left + 2.2f * u, r.top + 2.2f * u), Offset(r.right - 2.2f * u, r.top + 2.2f * u), Offset(r.left + 2.2f * u, r.bottom - 2.2f * u), Offset(r.right - 2.2f * u, r.bottom - 2.2f * u)).forEach {
            drawCircle(mezcla(color, P.LATON_HONDO, 0.5f), 1.2f * u, it)
            drawLine(mezcla(color, P.LATON_HONDO, 0.8f), it - Offset(0.9f * u, 0.5f * u), it + Offset(0.9f * u, 0.5f * u), 0.5f * u)
        }
        textoEn(medidor, p.texto.uppercase(), Rect(r.left + 7f * u, r.top + 1f * u, r.right - 7f * u, r.bottom - 1f * u), P.LETRA, a.tinta(), 11.5f, peso = FontWeight.Bold, espaciado = 0.6f, minimo = 7f)
    }
}
