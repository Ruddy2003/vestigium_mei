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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import kotlin.math.pow

/**
 * La obra del estante de Romano ([MuebleEstanteRomano]), hecha con oficio: las pilastras de estuco blanco con su basa, su
 * capitel y su panel con filete; la cornisa de estuco moldurada con el friso de cinabrio pintado; las tablas gruesas de roble
 * con el canto moldurado y sus ménsulas de bronce; cada casillero con su fondo de negro pompeyano y su filete, y con su
 * hondura (los cantos de dentro y la sombra de la tabla de encima); el podio negro jaspeado; el letrero del mes.
 */
internal object ObraEstanteRomano {
    private val P = PaletaRomano

    /**
     * De noche (modo oscuro), el velo de la noche encima de algo que se pintó con la luz plena de la linterna: un solo
     * degradado, más hondo cuanto más lejos de la llama ([cerca]), a lo largo ([vertical]) o a lo ancho. Así la luz baja sin
     * escalones y sin pintar cada tramo por separado.
     */
    fun DrawScope.veloNoche(r: Rect, a: AireRomano, vertical: Boolean, cerca: (Offset) -> Float) {
        if (!a.oscuro || r.width <= 0f || r.height <= 0f) return
        val n = 9
        val paradas = Array(n) { i ->
            val f = i / (n - 1f)
            val p = if (vertical) Offset(r.center.x, r.top + r.height * f) else Offset(r.left + r.width * f, r.center.y)
            f to a.velo.copy(alpha = (0.86f * (1f - cerca(p)).pow(1.15f)).coerceIn(0f, 0.86f))
        }
        val brocha = if (vertical) Brush.verticalGradient(*paradas, startY = r.top, endY = r.bottom) else Brush.horizontalGradient(*paradas, startX = r.left, endX = r.right)
        drawRect(brocha, r.topLeft, r.size)
    }

    // ---------------------------------------------------------------- el casillero

    /**
     * Un casillero: el fondo de negro pompeyano con su filete de cinabrio y su línea de ocre; los cantos de dentro (el de la
     * izquierda en sombra, el de la derecha con la luz de la ventana), la cara de abajo de la tabla de encima y la cara de
     * arriba de la de abajo; la sombra de la tabla y del pilar; de noche, el charco de la linterna donde llega.
     */
    fun DrawScope.casillero(k: Int, r: Rect, a: AireRomano, cerca: (Offset) -> Float) {
        val u = density
        val w = size.width
        if (r.width <= 0f || r.height <= 0f) return
        val kc = cerca(r.center)
        val dW = w * 0.024f
        val dH = r.height * 0.07f
        val dB = r.height * 0.06f
        val fondo = Rect(r.left + dW, r.top + dH, r.right - dW, r.bottom - dB)
        drawRect(a.luz(P.NEGRO, kc * 0.7f), r.topLeft, r.size)
        // El filete pintado: cinabrio y, por dentro, una línea fina de ocre.
        val f1 = fondo.deflate(w * 0.022f)
        if (f1.width > 4f * u && f1.height > 4f * u) {
            drawRect(a.luz(P.CINABRIO, kc), f1.topLeft, f1.size, style = Stroke(1.4f * u))
            val f2 = f1.deflate(3.2f * u)
            drawRect(a.luz(P.OCRE, kc).copy(alpha = 0.55f), f2.topLeft, f2.size, style = Stroke(0.6f * u))
        }
        // La linterna, de noche, donde llega.
        if (a.lumbre && kc > 0.05f) clipRect(r.left, r.top, r.right, r.bottom) {
            val llama = Offset(w * MuebleEstanteRomano.LAMP_X, w * MuebleEstanteRomano.LAMP_Y)
            mancha(llama, w * (0.55f + 0.3f * kc), P.LLAMA.copy(alpha = (if (a.oscuro) 0.42f else 0.2f) * kc))
        }
        // La sombra de la tabla de encima sobre el fondo y la del pilar izquierdo (de día).
        drawRect(a.sombra(0.5f), Offset(fondo.left, fondo.top), Size(fondo.width, r.height * 0.08f))
        drawRect(a.sombra(0.22f), Offset(fondo.left, fondo.top + r.height * 0.08f), Size(fondo.width, r.height * 0.1f))
        if (!a.oscuro) drawRect(a.sombra(0.25f), Offset(fondo.left, fondo.top), Size(w * 0.03f, fondo.height))
        // Los cantos de dentro: la cara de abajo de la tabla de encima, los costados y la cara de arriba de la de abajo.
        drawPath(poligonoRomano(r.left, r.top, r.right, r.top, fondo.right, fondo.top, fondo.left, fondo.top), a.luz(P.ROBLE_OSC, kc))
        drawPath(poligonoRomano(r.left, r.top, fondo.left, fondo.top, fondo.left, fondo.bottom, r.left, r.bottom), a.luz(mezcla(P.ESTUCO_SOMBRA, P.NEGRO, 0.45f), kc))
        drawPath(poligonoRomano(r.right, r.top, r.right, r.bottom, fondo.right, fondo.bottom, fondo.right, fondo.top), a.luz(P.ESTUCO_SOMBRA, kc))
        drawPath(poligonoRomano(r.left, r.bottom, fondo.left, fondo.bottom, fondo.right, fondo.bottom, r.right, r.bottom), a.luz(mezcla(P.ROBLE, P.ROBLE_LUZ, 0.4f), kc))
        drawLine(a.brillo(0.3f, kc), Offset(fondo.left, fondo.bottom), Offset(fondo.right, fondo.bottom), 0.7f * u)
        if (a.oscuro) drawLine(a.lunar(0.35f * (1f - kc)), Offset(fondo.right, fondo.top), Offset(fondo.right, fondo.bottom), 1f * u)
    }

    // ---------------------------------------------------------------- las pilastras

    /**
     * Una pilastra de estuco blanco, de mármol fingido: el fuste con sus vetas, sus cinco estrías y el filete de cinabrio,
     * el capitel (ábaco, equino y collarino) y la basa (toro y plinto); la luz a la izquierda y la sombra a la derecha; de
     * noche, por tramos según la linterna, con el filo en el canto que la mira y el roce de la luna en el otro.
     */
    fun DrawScope.pilastra(r: Rect, izquierda: Boolean, a: AireRomano, cercaReal: (Offset) -> Float) {
        val u = density
        if (r.width <= 1f || r.height <= 1f) return
        // De noche se pinta con la luz plena y encima va el velo; al anochecer, cada parte con la suya.
        val cerca: (Offset) -> Float = if (a.oscuro) { _ -> 1f } else cercaReal
        val cap = minOf(r.height * 0.3f, 21f * u)
        val bas = minOf(r.height * 0.3f, 17f * u)
        val fuste = Rect(r.left + r.width * 0.08f, r.top + cap, r.right - r.width * 0.08f, r.bottom - bas)
        // El fuste, por tramos (de noche la luz cambia de arriba abajo).
        val tramos = maxOf(1, (fuste.height / (40f * u)).toInt()).coerceAtMost(40)
        val th = fuste.height / tramos
        for (t in 0 until tramos) {
            val y0 = fuste.top + th * t
            drawRect(a.luz(P.ESTUCO, cerca(Offset(r.center.x, y0 + th / 2))), Offset(r.left, y0), Size(r.width, th + 1f))
        }
        vetas(fuste, a.luz(P.ESTUCO_VETA, cerca(fuste.center)).copy(alpha = 0.45f), if (izquierda) 7201 else 7202, 0.35f, vertical = true, grosor = 0.6f, ondulacion = 2.4f)
        // La luz y la sombra del fuste (de día, de la ventana a la izquierda).
        drawRect(a.claro(0.3f), Offset(r.left, fuste.top), Size(r.width * 0.14f, fuste.height))
        drawRect(a.sombra(0.22f), Offset(r.right - r.width * 0.2f, fuste.top), Size(r.width * 0.2f, fuste.height))
        // Las estrías: cinco canales de remate redondo; dentro de cada uno, la pared de la izquierda en sombra y la de la
        // derecha con luz (de día la luz viene de la ventana). Tres caminos para todas, así pesan poco.
        val est = Rect(r.left + r.width * 0.16f, fuste.top + 7f * u, r.right - r.width * 0.16f, fuste.bottom - 7f * u)
        if (est.height > 8f * u) {
            val kp = cerca(est.center)
            val n = 5
            val paso = est.width / n
            val canal = paso * 0.62f
            val fondos = Path(); val sombras = Path(); val luces = Path()
            for (j in 0 until n) {
                val x = est.left + paso * j + (paso - canal) / 2
                fondos.addRoundRect(RoundRect(Rect(x, est.top, x + canal, est.bottom), CornerRadius(canal / 2)))
                sombras.moveTo(x + canal * 0.15f, est.top + canal * 0.5f); sombras.lineTo(x + canal * 0.15f, est.bottom - canal * 0.5f)
                luces.moveTo(x + canal * 0.88f, est.top + canal * 0.6f); luces.lineTo(x + canal * 0.88f, est.bottom - canal * 0.4f)
            }
            drawPath(fondos, a.luz(P.ESTUCO_SOMBRA, kp).copy(alpha = 0.55f))
            drawPath(sombras, a.sombra(0.3f), style = Stroke(canal * 0.3f))
            drawPath(luces, a.brillo(0.55f, kp), style = Stroke(canal * 0.14f))
            // El filete de cinabrio pintado alrededor de las estrías.
            val fil = est.inflate(3f * u)
            drawRect(a.luz(P.CINABRIO, kp).copy(alpha = 0.7f), fil.topLeft, fil.size, style = Stroke(1f * u))
        }
        // El capitel: el collarino, el equino y el ábaco, que vuelan un poco sobre el fuste.
        val kc = cerca(Offset(r.center.x, r.top + cap / 2))
        val vuelo = 3f * u
        val collarino = Rect(r.left + r.width * 0.04f, r.top + cap * 0.78f, r.right - r.width * 0.04f, r.top + cap)
        val equino = Rect(r.left - vuelo * 0.4f, r.top + cap * 0.38f, r.right + vuelo * 0.4f, r.top + cap * 0.78f)
        val abaco = Rect(r.left - vuelo, r.top, r.right + vuelo, r.top + cap * 0.38f)
        drawRect(a.sombra(0.35f), Offset(abaco.left + 1.5f * u, abaco.bottom), Size(abaco.width, 2.4f * u))
        drawRect(a.luz(P.ESTUCO, kc), collarino.topLeft, collarino.size)
        drawRect(a.brillo(0.4f, kc), collarino.topLeft, Size(collarino.width, 0.8f * u))
        drawRect(a.sombra(0.3f), Offset(collarino.left, collarino.bottom - 1f * u), Size(collarino.width, 1f * u))
        drawRect(a.luz(P.ESTUCO, kc), equino.topLeft, equino.size)
        drawRect(a.brillo(0.45f, kc), equino.topLeft, Size(equino.width, equino.height * 0.35f))
        drawRect(a.sombra(0.32f), Offset(equino.left, equino.top + equino.height * 0.62f), Size(equino.width, equino.height * 0.38f))
        drawRect(a.luz(P.ESTUCO, kc), abaco.topLeft, abaco.size)
        drawRect(a.brillo(0.5f, kc), abaco.topLeft, Size(abaco.width, 1f * u))
        drawRect(a.luz(P.CINABRIO, kc).copy(alpha = 0.7f), Offset(abaco.left, abaco.center.y - 0.5f * u), Size(abaco.width, 1f * u))
        drawRect(a.sombra(0.22f), Offset(abaco.right - abaco.width * 0.18f, abaco.top), Size(abaco.width * 0.18f, abaco.height + equino.height))
        // La basa: el toro, el filete y el plinto.
        val kb = cerca(Offset(r.center.x, r.bottom - bas / 2))
        val filete = Rect(r.left + r.width * 0.02f, r.bottom - bas, r.right - r.width * 0.02f, r.bottom - bas * 0.82f)
        val toro = Rect(r.left - vuelo * 0.5f, r.bottom - bas * 0.82f, r.right + vuelo * 0.5f, r.bottom - bas * 0.42f)
        val plinto = Rect(r.left - vuelo, r.bottom - bas * 0.42f, r.right + vuelo, r.bottom)
        drawRect(a.luz(P.ESTUCO, kb), filete.topLeft, filete.size)
        drawRect(a.luz(P.ESTUCO, kb), toro.topLeft, toro.size)
        drawRect(a.brillo(0.45f, kb), toro.topLeft, Size(toro.width, toro.height * 0.4f))
        drawRect(a.sombra(0.3f), Offset(toro.left, toro.top + toro.height * 0.7f), Size(toro.width, toro.height * 0.3f))
        drawRect(a.luz(P.ESTUCO, kb), plinto.topLeft, plinto.size)
        drawRect(a.brillo(0.35f, kb), plinto.topLeft, Size(plinto.width, 0.9f * u))
        drawRect(a.sombra(0.25f), Offset(plinto.right - plinto.width * 0.2f, toro.top), Size(plinto.width * 0.2f, r.bottom - toro.top))
        veloNoche(Rect(r.left - vuelo, r.top, r.right + vuelo, r.bottom), a, true, cercaReal)
        // El filo de la linterna en el canto que la mira y el roce de la luna en el otro.
        if (a.oscuro) {
            if (!izquierda) {
                for (t in 0 until tramos) {
                    val y0 = fuste.top + th * t
                    val kf = cercaReal(Offset(r.left, y0 + th / 2))
                    if (kf > 0.08f) drawRect(a.filo(0.55f * kf), Offset(r.left, y0), Size(1.4f * u, th + 1f))
                }
            } else {
                // El roce azul de la luna de la ventana en el canto que la mira: fuerte arriba y que se pierde abajo.
                val l = fuste.height
                for ((j, al) in listOf(0.65f, 0.45f, 0.28f, 0.14f).withIndex())
                    drawRect(a.lunar(al), Offset(r.right - 1.8f * u, r.top + l * 0.25f * j), Size(1.6f * u, l * 0.25f + 1f))
                drawRect(a.lunar(0.12f), Offset(r.right - r.width * 0.3f, r.top), Size(r.width * 0.3f - 1.8f * u, l * 0.45f))
            }
        }
    }

    // ---------------------------------------------------------------- las tablas

    /**
     * Una tabla gruesa de roble: la cara de encima clara, el canto con su moldura (un bocel abajo con su luz y su sombra, una
     * media caña arriba), sus vetas y golpes; las ménsulas de bronce que la sostienen a cada lado; de noche, por tramos y con
     * el filo donde mira a la linterna.
     */
    fun DrawScope.tabla(r: Rect, semilla: Int, a: AireRomano, cerca: (Offset) -> Float) {
        val u = density
        if (r.width <= 0f || r.height <= 0f) return
        val tramos = 6
        val an = r.width / tramos
        for (t in 0 until tramos) {
            val x0 = r.left + an * t
            val k = cerca(Offset(x0 + an / 2, r.center.y))
            drawRect(a.luz(P.ROBLE, k), Offset(x0, r.top), Size(an + 1f, r.height))
            drawRect(a.luz(P.ROBLE_LUZ, k), Offset(x0, r.top), Size(an + 1f, r.height * 0.16f))
            drawRect(a.luz(mezcla(P.ROBLE, P.ROBLE_LUZ, 0.35f), k), Offset(x0, r.top + r.height * 0.66f), Size(an + 1f, r.height * 0.18f))
            if (a.oscuro && k > 0.1f) drawRect(a.filo(0.55f * k), Offset(x0, r.top + r.height * 0.16f), Size(an + 1f, 1f * u))
            if (a.oscuro) drawRect(a.lunar(0.3f * (1f - t / tramos.toFloat()) * (1f - k)), Offset(x0, r.top), Size(an + 1f, 1f * u))
        }
        vetas(Rect(r.left, r.top + r.height * 0.2f, r.right, r.top + r.height * 0.62f), a.luz(P.ROBLE_OSC, 0.4f).copy(alpha = 0.4f), semilla, 0.7f, grosor = 0.5f)
        // La moldura: la media caña arriba y el bocel abajo.
        drawLine(a.sombra(0.35f), Offset(r.left, r.top + r.height * 0.2f), Offset(r.right, r.top + r.height * 0.2f), 1f * u)
        drawLine(a.sombra(0.3f), Offset(r.left, r.top + r.height * 0.64f), Offset(r.right, r.top + r.height * 0.64f), 0.9f * u)
        drawLine(a.claro(0.3f), Offset(r.left, r.top + r.height * 0.68f), Offset(r.right, r.top + r.height * 0.68f), 0.8f * u)
        drawRect(a.sombra(0.35f), Offset(r.left, r.bottom - r.height * 0.12f), Size(r.width, r.height * 0.12f))
        // Golpes de uso.
        for (j in 0 until 5) {
            val x = r.left + r.width * azar(semilla, 30 + j)
            drawLine(a.sombra(0.3f), Offset(x, r.top + r.height * 0.3f), Offset(x + 3f * u, r.top + r.height * 0.34f), 0.6f * u)
        }
        // Las ménsulas de bronce, una a cada lado, que bajan en voluta.
        for (lado in listOf(-1f, 1f)) {
            val x = if (lado < 0) r.left + 3f * u else r.right - 3f * u
            val k = cerca(Offset(x, r.bottom))
            mensula(Offset(x, r.bottom), lado, minOf(15f * u, r.height * 0.9f), a, k)
        }
    }

    /** Una ménsula de bronce bajo una tabla: el brazo pegado a la pared que baja en voluta, con su luz y su clavo. */
    private fun DrawScope.mensula(arriba: Offset, lado: Float, largo: Float, a: AireRomano, k: Float) {
        val u = density
        val s = -lado
        val p = Path().apply {
            moveTo(arriba.x, arriba.y)
            lineTo(arriba.x + s * largo * 0.42f, arriba.y)
            quadraticTo(arriba.x + s * largo * 0.38f, arriba.y + largo * 0.35f, arriba.x + s * largo * 0.12f, arriba.y + largo * 0.55f)
            quadraticTo(arriba.x, arriba.y + largo * 0.7f, arriba.x + s * largo * 0.05f, arriba.y + largo)
            lineTo(arriba.x - s * largo * 0.06f, arriba.y + largo)
            quadraticTo(arriba.x - s * largo * 0.1f, arriba.y + largo * 0.6f, arriba.x, arriba.y + largo * 0.45f)
            close()
        }
        drawPath(corridoRomano(p, 1.5f * u, 1.5f * u), a.sombra(0.35f))
        drawPath(p, a.luz(P.BRONCE, k))
        drawPath(p, a.brillo(0.55f, k), style = Stroke(0.6f * u))
        drawCircle(a.luz(P.BRONCE_LUZ, k), largo * 0.08f, Offset(arriba.x + s * largo * 0.05f, arriba.y + largo * 0.95f))
        remacheRomano(Offset(arriba.x + s * largo * 0.08f, arriba.y + largo * 0.22f), 1f * u, a, k, P.BRONCE_OSC)
    }

    /**
     * El letrero del mes: una cartela de estuco blanco clavada en el canto de la tabla, con su bisel, su sombra, el filete de
     * cinabrio y las letras pintadas; de noche se apaga poco, para que el mes se lea siempre.
     */
    fun DrawScope.letrero(p: PlacaEstante, medidor: TextMeasurer, a: AireRomano, k: Float) {
        val u = density
        val r = p.r
        if (r.width <= 0f || r.height <= 0f) return
        val kk = k.coerceIn(0f, 1f)
        val cara = if (a.oscuro) mezcla(mezcla(P.HUESO, a.hondo, 0.25f - 0.2f * kk), P.LLAMA, 0.12f * kk)
        else if (a.noche) mezcla(P.HUESO, P.ANOCHECER, 0.1f) else P.HUESO
        drawRect(a.sombra(0.45f), Offset(r.left + 1.4f * u, r.top + 1.8f * u), r.size)
        drawRect(cara, r.topLeft, r.size)
        drawRect(Color.White.copy(alpha = if (a.oscuro) 0.06f else 0.35f), r.topLeft, Size(r.width, 1f * u))
        drawRect(P.ESTUCO_SOMBRA.copy(alpha = 0.45f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
        drawRect(P.ESTUCO_SOMBRA.copy(alpha = 0.35f), Offset(r.right - 1.2f * u, r.top), Size(1.2f * u, r.height))
        val filete = r.deflate(2f * u)
        drawRect(P.CINABRIO.copy(alpha = 0.7f), filete.topLeft, filete.size, style = Stroke(0.7f * u))
        remacheRomano(Offset(r.left + 3.4f * u, r.center.y), 1.3f * u, a, kk, P.BRONCE)
        remacheRomano(Offset(r.right - 3.4f * u, r.center.y), 1.3f * u, a, kk, P.BRONCE)
        textoEn(medidor, p.texto, Rect(r.left + 7f * u, r.top + 0.5f * u, r.right - 7f * u, r.bottom), P.LETRA, P.CINABRIO_OSC, 9.5f,
            peso = FontWeight.Bold, espaciado = 0.8f, minimo = 5f)
    }

    // ---------------------------------------------------------------- el podio

    /**
     * El podio de obra sobre el que se levantan las pilastras: la moldura de estuco de arriba, el frente negro jaspeado con dos
     * paneles de filete y el plinto de abajo; de noche, por tramos según la linterna.
     */
    fun DrawScope.podio(r: Rect, a: AireRomano, cercaReal: (Offset) -> Float) {
        val u = density
        val w = size.width
        if (r.height <= 1f) return
        val cerca: (Offset) -> Float = if (a.oscuro) { _ -> 1f } else cercaReal
        val tramos = 6
        for (t in 0 until tramos) {
            val x0 = w * t / tramos
            drawRect(a.luz(P.NEGRO, cerca(Offset(x0 + w / tramos / 2, r.center.y))), Offset(x0, r.top), Size(w / tramos + 1f, r.height))
        }
        // El jaspeado: salpicaduras de ocre, cinabrio y cal.
        for (j in 0 until 30) {
            val p = Offset(w * azar(7301, j), r.top + r.height * (0.25f + 0.65f * azar(7302, j)))
            val col = when (j % 3) { 0 -> P.OCRE; 1 -> P.CINABRIO; else -> P.CAL }
            drawOval(a.luz(col, cerca(p)).copy(alpha = 0.22f), Offset(p.x - w * 0.012f, p.y - w * 0.003f), Size(w * (0.014f + 0.016f * azar(7303, j)), w * 0.006f))
        }
        // Los paneles con su filete.
        for ((x0, x1) in listOf(0.14f to 0.47f, 0.53f to 0.86f)) {
            val pr = Rect(w * x0, r.top + r.height * 0.34f, w * x1, r.bottom - r.height * 0.22f)
            if (pr.height > 3f * u) {
                val kp = cerca(pr.center)
                drawRect(a.luz(P.CINABRIO, kp).copy(alpha = 0.8f), pr.topLeft, pr.size, style = Stroke(1.2f * u))
                drawRect(a.luz(P.CAL, kp).copy(alpha = 0.35f), pr.deflate(3f * u).topLeft, pr.deflate(3f * u).size, style = Stroke(0.5f * u))
            }
        }
        // La moldura de estuco de arriba y el plinto.
        val mold = Rect(r.left, r.top, r.right, r.top + minOf(r.height * 0.22f, 9f * u))
        for (t in 0 until tramos) {
            val x0 = w * t / tramos
            val k = cerca(Offset(x0 + w / tramos / 2, mold.center.y))
            drawRect(a.luz(P.ESTUCO, k), Offset(x0, mold.top), Size(w / tramos + 1f, mold.height))
        }
        drawRect(a.claro(0.4f), mold.topLeft, Size(mold.width, 1f * u))
        drawRect(a.sombra(0.3f), Offset(mold.left, mold.top + mold.height * 0.55f), Size(mold.width, 1f * u))
        drawRect(a.sombra(0.45f), Offset(mold.left, mold.bottom), Size(mold.width, 2f * u))
        drawRect(a.luz(P.NEGRO_LUZ, cerca(Offset(w * 0.8f, r.bottom))), Offset(r.left, r.bottom - 3f * u), Size(r.width, 3f * u))
        drawRect(a.claro(0.15f), Offset(r.left, r.bottom - 3f * u), Size(r.width, 0.8f * u))
        veloNoche(r, a, false, cercaReal)
        if (a.oscuro) for (t in 0 until tramos) {
            val x0 = w * t / tramos
            val k = cercaReal(Offset(x0 + w / tramos / 2, mold.center.y))
            if (k > 0.1f) drawRect(a.filo(0.5f * k), Offset(x0, mold.top), Size(w / tramos + 1f, 1f * u))
        }
    }

    // ---------------------------------------------------------------- la cornisa

    /**
     * La cornisa de estuco que corona el estante, de borde a borde: la cara de encima donde se dejan las cosas, el cimacio con
     * su luz y su sombra, la corona con su goterón, la fila de modillones (cada uno con su cara, su voluta abajo y su sombra en
     * el fondo hondo), el friso de cinabrio con una ola corrida en ocre pálido y el arquitrabe de dos fajas; su sombra abajo; de
     * noche, con la luz plena y el velo encima, y el filo donde mira a la linterna.
     */
    fun DrawScope.cornisa(r: Rect, a: AireRomano, cercaReal: (Offset) -> Float) {
        val u = density
        val h = r.height
        if (h <= 1f) return
        val cerca: (Offset) -> Float = if (a.oscuro) { _ -> 1f } else cercaReal
        fun y(f: Float) = r.top + h * f
        drawRect(a.sombra(0.5f), Offset(r.left, r.bottom), Size(r.width, 3f * u))
        drawRect(a.sombra(0.22f), Offset(r.left, r.bottom + 3f * u), Size(r.width, 6f * u))
        val tramos = if (a.oscuro) 1 else 10
        val an = r.width / tramos
        for (t in 0 until tramos) {
            val x0 = r.left + an * t
            val k = cerca(Offset(x0 + an / 2, r.center.y))
            val e = a.luz(P.ESTUCO, k)
            // La cara de encima y el cimacio: luz arriba, sombra abajo.
            drawRect(a.luz(mezcla(P.ESTUCO, P.CAL, 0.6f), k), Offset(x0, y(0f)), Size(an + 1f, h * 0.08f))
            drawRect(e, Offset(x0, y(0.08f)), Size(an + 1f, h * 0.18f))
            drawRect(a.brillo(0.4f, k), Offset(x0, y(0.08f)), Size(an + 1f, h * 0.06f))
            drawRect(a.sombra(0.12f), Offset(x0, y(0.17f)), Size(an + 1f, h * 0.05f))
            drawRect(a.sombra(0.26f), Offset(x0, y(0.22f)), Size(an + 1f, h * 0.04f))
            // La corona, con su goterón.
            drawRect(e, Offset(x0, y(0.26f)), Size(an + 1f, h * 0.18f))
            drawRect(a.luz(mezcla(P.ESTUCO_SOMBRA, P.NEGRO, 0.45f), k), Offset(x0, y(0.44f)), Size(an + 1f, h * 0.04f))
            // El fondo hondo de los modillones.
            drawRect(a.luz(mezcla(P.ESTUCO_SOMBRA, P.NEGRO, 0.25f), k), Offset(x0, y(0.48f)), Size(an + 1f, h * 0.16f))
            // El friso de cinabrio.
            drawRect(a.luz(P.CINABRIO, k), Offset(x0, y(0.64f)), Size(an + 1f, h * 0.2f))
            drawRect(a.sombra(0.25f), Offset(x0, y(0.64f)), Size(an + 1f, h * 0.04f))
            // El arquitrabe de dos fajas.
            drawRect(e, Offset(x0, y(0.84f)), Size(an + 1f, h * 0.16f))
            drawRect(a.sombra(0.16f), Offset(x0, y(0.92f)), Size(an + 1f, h * 0.08f))
        }
        drawLine(a.claro(0.55f), Offset(r.left, y(0.08f)), Offset(r.right, y(0.08f)), 0.9f * u)
        drawLine(a.claro(0.4f), Offset(r.left, y(0.26f)), Offset(r.right, y(0.26f)), 0.8f * u)
        drawLine(a.claro(0.3f), Offset(r.left, y(0.84f)), Offset(r.right, y(0.84f)), 0.7f * u)
        drawLine(a.sombra(0.3f), Offset(r.left, y(0.92f)), Offset(r.right, y(0.92f)), 0.7f * u)
        // Los modillones: cara, voluta, canto de luz y sombra en el fondo, en tres caminos.
        val paso = maxOf(8f * u, size.width * 0.07f)
        val anM = paso * 0.42f
        val caras = Path(); val sombras = Path(); val luces = Path()
        var x = r.left + paso * 0.3f
        var i = 0
        while (x < r.right && i < 80) {
            val m = Rect(x, y(0.48f), x + anM, y(0.62f))
            sombras.addRect(Rect(m.right, m.top, m.right + anM * 0.45f, m.bottom + h * 0.02f))
            caras.addRect(Rect(m.left, m.top, m.right, m.bottom - m.height * 0.3f))
            caras.addOval(Rect(m.left, m.bottom - m.height * 0.55f, m.right, m.bottom + m.height * 0.05f))
            luces.moveTo(m.left + 0.6f * u, m.top); luces.lineTo(m.left + 0.6f * u, m.bottom - m.height * 0.2f)
            x += paso; i++
        }
        drawPath(sombras, a.sombra(0.4f))
        drawPath(caras, a.luz(P.ESTUCO, cerca(r.center)))
        drawPath(luces, a.brillo(0.6f, cerca(r.center)), style = Stroke(1f * u))
        // La ola corrida del friso, en ocre pálido.
        val ola = Path()
        val ph = h * 0.2f
        val pw = maxOf(6f * u, ph * 1.6f)
        var ox = r.left - pw * 0.3f
        var n = 0
        ola.moveTo(ox, y(0.8f))
        while (ox < r.right + pw && n < 200) {
            ola.cubicTo(ox + pw * 0.3f, y(0.8f), ox + pw * 0.35f, y(0.68f), ox + pw * 0.6f, y(0.68f))
            ola.cubicTo(ox + pw * 0.85f, y(0.68f), ox + pw * 0.85f, y(0.78f), ox + pw * 0.66f, y(0.77f))
            ola.moveTo(ox + pw * 0.66f, y(0.8f))
            ola.lineTo(ox + pw, y(0.8f))
            ox += pw; n++
        }
        drawPath(ola, a.luz(mezcla(P.OCRE_CLARO, P.CAL, 0.3f), cerca(Offset(r.right * 0.7f, r.center.y))).copy(alpha = 0.75f), style = Stroke(1f * u, cap = StrokeCap.Round))
        // Golpes y faltas del estuco en la corona.
        for (j in 0 until 3) {
            val gx = r.left + r.width * (0.15f + 0.3f * j + 0.1f * azar(7401, j))
            drawPath(poligonoRomano(gx, y(0.26f), gx + 5f * u, y(0.26f), gx + 3f * u, y(0.38f)), a.luz(P.ESTUCO_SOMBRA, cerca(Offset(gx, r.center.y))))
        }
        veloNoche(r, a, false, cercaReal)
        if (a.oscuro) {
            val t2 = 10
            val an2 = r.width / t2
            for (t in 0 until t2) {
                val x0 = r.left + an2 * t
                val k = cercaReal(Offset(x0 + an2 / 2, r.center.y))
                if (k > 0.08f) {
                    drawRect(a.filo(0.7f * k), Offset(x0, y(0.08f)), Size(an2 + 1f, 1.2f * u))
                    drawRect(a.filo(0.45f * k), Offset(x0, y(0.26f)), Size(an2 + 1f, 1f * u))
                }
            }
        }
    }
}
