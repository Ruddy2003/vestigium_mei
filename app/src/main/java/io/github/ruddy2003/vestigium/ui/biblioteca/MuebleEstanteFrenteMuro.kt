package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.TrazosFrente
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import java.util.Random

/**
 * La pared del estante de Frente: el mismo muro de sillares verdosos del Ideario (`TrazosFrente.muroSillares`, la misma piedra,
 * la misma junta, la misma humedad y el mismo musgo, con hiladas de la misma altura), sus raíces colgando de arriba y el tubo de
 * cobre con sus abrazaderas cruzando a la misma altura. De día, el charco de la cúpula en la piedra; de noche, la penumbra verde,
 * el halo tibio de la cúpula y el resplandor turquesa del vidrio. Dentro de los casilleros y en las orillas, la misma piedra en
 * una baldosa pintada una vez ([baldosa]).
 */
internal object MuroEstanteFrente {
    private val P = PaletaFrente

    /** El alto de una hilada de sillares, en dp: el del Ideario. */
    const val HILADA = 19f

    // ---------------------------------------------------------------- la piedra de la corona

    /** Los colores de la piedra a la luz del momento, a [k] de la luz. */
    private fun piedra(a: AireFrente, k: Float, c: Color) = if (a.oscuro) a.luz(c, k) else a.dia(c, 0f)

    /**
     * La pared de arriba, en [r]: el muro de sillares, el charco de luz de la cúpula, el resplandor del vidrio de noche, las
     * raíces que cuelgan de arriba y el tubo de cobre.
     */
    fun DrawScope.pared(r: Rect, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val w = size.width
        val kMedio = if (a.oscuro) 0.22f else 1f
        val humedad = if (a.oscuro) Color(0xFF04100A).copy(alpha = 0.7f) else P.HUMEDAD.copy(alpha = 0.5f)
        with(TrazosFrente) {
            muroSillares(r, HILADA * u, piedra(a, kMedio, P.PIEDRA), piedra(a, kMedio * 0.8f, P.PIEDRA_SOMBRA), humedad,
                piedra(a, kMedio, P.MUSGO), 6, if (a.oscuro) 0.3f else if (a.noche) 0.8f else 1f)
        }
        val lamp = Offset(w * MuebleEstanteFrente.CUPULA_X, w * MuebleEstanteFrente.CUPULA_Y)
        val vid = Offset(w * MuebleEstanteFrente.VIDRIO_X, w * MuebleEstanteFrente.VIDRIO_Y)
        clipRect(r.left, r.top, r.right, r.bottom) {
            if (!a.oscuro) {
                // De día, el charco de la cúpula: la piedra más clara y tibia debajo de la lámpara, que se abre hacia abajo; lo
                // de la derecha, arriba, queda más hondo.
                val c = Offset(lamp.x + w * 0.04f, lamp.y + w * 0.1f)
                val luz = if (a.noche) P.GUARDIA else Color(0xFFFFF0D2)
                drawOval(Brush.radialGradient(0f to luz.copy(alpha = if (a.noche) 0.34f else 0.38f), 0.45f to luz.copy(alpha = if (a.noche) 0.12f else 0.14f),
                    1f to luz.copy(alpha = 0f), center = c, radius = w * 0.46f), Offset(c.x - w * 0.46f, c.y - w * 0.46f), Size(w * 0.92f, w * 0.92f))
                drawRect(Brush.radialGradient(0f to Color.Transparent, 0.55f to Color.Transparent, 1f to a.sombra(if (a.noche) 0.2f else 0.22f),
                    center = c, radius = w * 0.95f), r.topLeft, r.size)
            } else {
                // De noche, el halo tibio de la cúpula (apagada en los Sueños), el resplandor del vidrio y, lejos, la penumbra.
                if (!a.dormida) {
                    val c = Offset(lamp.x + w * 0.03f, lamp.y + w * 0.07f)
                    drawOval(Brush.radialGradient(0f to P.LAMPARA.copy(alpha = 0.72f), 0.25f to P.LAMPARA.copy(alpha = 0.36f), 0.6f to P.LAMPARA.copy(alpha = 0.1f),
                        1f to P.LAMPARA.copy(alpha = 0f), center = c, radius = w * 0.5f), Offset(c.x - w * 0.5f, c.y - w * 0.5f), Size(w, w))
                }
                val rv = w * (if (a.dormida) 0.42f else 0.28f)
                drawOval(Brush.radialGradient(0f to P.VIDRIO_TRAZO.copy(alpha = if (a.dormida) 0.3f else 0.2f), 0.5f to P.VIDRIO_TRAZO.copy(alpha = if (a.dormida) 0.08f else 0.05f),
                    1f to P.VIDRIO_TRAZO.copy(alpha = 0f), center = vid, radius = rv), Offset(vid.x - rv, vid.y - rv), Size(rv * 2, rv * 2))
                val centro = if (a.dormida) vid else lamp
                drawRect(Brush.radialGradient(0f to Color.Transparent, 0.32f to Color.Transparent, 1f to a.hondo.copy(alpha = 0.7f),
                    center = centro, radius = w * 0.95f), r.topLeft, r.size)
            }
            // Las raíces que cuelgan de arriba, por las orillas y una corta en medio.
            raices(listOf(Triple(w * 0.022f, r.height * 0.95f, 3.4f * u), Triple(w * 0.33f, r.height * 0.16f, 2.4f * u), Triple(w * 0.982f, r.height * 1.05f, 3.6f * u)),
                r.top, a, cerca, 21)
        }
        // El tubo de cobre con sus abrazaderas, a la altura del del Ideario.
        val yTubo = r.top + w * 0.032f
        val kt = cerca(Offset(w * 0.3f, yTubo))
        with(TrazosFrente) { tubo(Offset(-2f * u, yTubo), Offset(w + 2f * u, yTubo), 4.2f * u, piedra(a, kt, P.COBRE), if (a.oscuro) 0.2f + 0.6f * kt else 1f, juntas = 90f * u) }
        listOf(0.08f, 0.36f, 0.64f, 0.93f).forEach { f ->
            val k = cerca(Offset(w * f, yTubo))
            drawRoundRect(a.sombra(0.3f), Offset(w * f - 2f * u, yTubo - 3f * u), Size(5 * u, 7f * u), CornerRadius(1 * u))
            drawRoundRect(piedra(a, k, P.CROMO_OSC), Offset(w * f - 2.5f * u, yTubo - 3.4f * u), Size(5 * u, 6.8f * u), CornerRadius(1 * u))
            drawLine(a.brillo(0.3f, k), Offset(w * f - 1.8f * u, yTubo - 2.8f * u), Offset(w * f - 1.8f * u, yTubo + 2.8f * u), 0.7f * u)
        }
    }

    /** Las raíces que cuelgan desde [y0]: cada una es (x, largo, grosor), en manojo con una compañera más fina. */
    fun DrawScope.raices(lista: List<Triple<Float, Float, Float>>, y0: Float, a: AireFrente, cerca: (Offset) -> Float, semilla: Int) {
        lista.forEachIndexed { k, (x, largo, grosor) ->
            val kk = cerca(Offset(x, y0 + largo * 0.4f))
            val color = piedra(a, kk, P.RAIZ)
            val brillo = if (a.oscuro) P.LAMPARA.copy(alpha = 0.18f * kk) else if (a.noche) P.GUARDIA.copy(alpha = 0.3f) else Color(0xFFC9BC94).copy(alpha = 0.32f)
            with(TrazosFrente) {
                raiz(Offset(x + (if (k % 2 == 0) 1f else -1f) * grosor * 0.9f, y0), largo * 0.7f, grosor * 0.55f, mezcla(color, Color.Black, 0.15f), brillo, semilla * 13 + k + 50, sombra = if (a.oscuro) 0.4f else 0.26f)
                raiz(Offset(x, y0), largo, grosor, color, brillo, semilla * 13 + k, sombra = if (a.oscuro) 0.4f else 0.28f)
            }
        }
    }

    // ---------------------------------------------------------------- la baldosa de piedra de los casilleros

    private var claveBaldosa = ""
    private var pincel: ShaderBrush? = null
    private var claveBaldosa2 = ""
    private var pincel2: ShaderBrush? = null

    /**
     * La piedra de dentro de los casilleros y de las orillas: el mismo sillar del muro, en una baldosa que empalma por los cuatro
     * lados y se pinta una sola vez por densidad y momento ([oscuro]: la penumbra; si no, la piedra en la sombra del mueble).
     * Guarda las dos últimas (el diario y los Sueños se turnan).
     */
    fun baldosa(u: Float, a: AireFrente): ShaderBrush {
        val clave = "$u:${a.oscuro}:${a.noche}"
        if (clave == claveBaldosa) pincel?.let { return it }
        if (clave == claveBaldosa2) pincel2?.let { return it }
        val ancho = (220f * u).toInt().coerceAtLeast(8)
        val alto = (4 * HILADA * u).toInt().coerceAtLeast(8)
        val img = ImageBitmap(ancho, alto)
        val k = if (a.oscuro) 0.2f else 1f
        val base = if (a.oscuro) a.luz(P.PIEDRA, k) else mezcla(a.dia(P.PIEDRA, 0f), P.SOMBRA, 0.42f)
        val junta = if (a.oscuro) a.luz(P.PIEDRA_SOMBRA, k * 0.7f) else mezcla(a.dia(P.PIEDRA_SOMBRA, 0f), P.SOMBRA, 0.42f)
        val musgo = if (a.oscuro) a.luz(P.MUSGO, k) else mezcla(a.dia(P.MUSGO, 0f), P.SOMBRA, 0.3f)
        val humedad = if (a.oscuro) Color(0xFF020906).copy(alpha = 0.6f) else P.HUMEDAD.copy(alpha = 0.55f)
        CanvasDrawScope().draw(Density(u), LayoutDirection.Ltr, Canvas(img), Size(ancho.toFloat(), alto.toFloat())) {
            sillares(ancho.toFloat(), alto.toFloat(), 4, base, junta, musgo, humedad, if (a.oscuro) 0.35f else 0.85f)
        }
        val nuevo = ShaderBrush(ImageShader(img, TileMode.Repeated, TileMode.Repeated))
        claveBaldosa2 = claveBaldosa; pincel2 = pincel
        claveBaldosa = clave; pincel = nuevo
        return nuevo
    }

    /**
     * Los sillares de la baldosa, de [ancho] por [alto] en [filas] hiladas, como los de `TrazosFrente.muroSillares` pero
     * empalmando: cada hilada suma justo el ancho y la piedra que se pasa de un borde se pinta también en el otro.
     */
    private fun DrawScope.sillares(ancho: Float, alto: Float, filas: Int, piedra: Color, junta: Color, musgo: Color, humedad: Color, brillo: Float) {
        val rnd = Random(4711)
        drawRect(junta, Offset.Zero, Size(ancho, alto))
        val hh = alto / filas
        val g = hh * 0.06f
        for (f in 0 until filas) {
            val y = f * hh
            val x0 = -rnd.nextFloat() * hh * 1.6f
            val anchos = ArrayList<Float>()
            var x = x0
            while (x < x0 + ancho - hh * 1.2f) { val an = hh * (1.25f + rnd.nextFloat() * 1.5f); anchos.add(an); x += an }
            // La última piedra se ajusta para que la hilada mida justo el ancho de la baldosa.
            val sobra = x - (x0 + ancho)
            if (anchos.isNotEmpty()) anchos[anchos.size - 1] = anchos.last() - sobra
            x = x0
            for (an in anchos) {
                val tono = mezcla(piedra, if (rnd.nextBoolean()) Color.White else Color.Black, rnd.nextFloat() * 0.12f)
                val rad = g * (1.4f + rnd.nextFloat() * 1.6f)
                val musgoso = rnd.nextFloat() < 0.2f
                val humedo = rnd.nextFloat() < 0.32f
                val mx = rnd.nextFloat(); val ml = rnd.nextFloat()
                val poros = List(4 + rnd.nextInt(4)) { Offset(rnd.nextFloat(), rnd.nextFloat()) }
                for (desp in floatArrayOf(-ancho, 0f, ancho)) {
                    val s = Rect(x + desp + g, y + g, x + desp + an - g, y + hh - g)
                    if (s.right < 0f || s.left > ancho) continue
                    drawRoundRect(tono, s.topLeft, s.size, CornerRadius(rad))
                    drawRect(Color.White.copy(alpha = 0.06f * brillo), Offset(s.left + g, s.top + g), Size(s.width - 2 * g, s.height * 0.4f))
                    drawRect(Color.Black.copy(alpha = 0.08f), Offset(s.left + g, s.top + s.height * 0.62f), Size(s.width - 2 * g, s.height * 0.32f))
                    drawLine(Color.White.copy(alpha = 0.2f * brillo), Offset(s.left + g * 1.2f, s.top + g * 0.7f), Offset(s.right - g * 1.6f, s.top + g * 0.7f), g * 0.8f, StrokeCap.Round)
                    drawLine(Color.Black.copy(alpha = 0.26f), Offset(s.left + g * 1.4f, s.bottom - g * 0.6f), Offset(s.right - g, s.bottom - g * 0.6f), g * 1.1f, StrokeCap.Round)
                    drawLine(Color.Black.copy(alpha = 0.2f), Offset(s.right - g * 0.6f, s.top + g * 1.4f), Offset(s.right - g * 0.6f, s.bottom - g), g * 0.9f, StrokeCap.Round)
                    poros.forEach { p ->
                        val c = Offset(s.left + s.width * p.x, s.top + s.height * p.y)
                        drawCircle(Color.Black.copy(alpha = 0.16f), g * (0.3f + 0.5f * p.y), c)
                    }
                    if (humedo) {
                        val cx = s.left + s.width * (0.15f + 0.6f * mx)
                        val largo = hh * (0.5f + 1.1f * ml)
                        drawRect(Brush.verticalGradient(listOf(humedad.copy(alpha = humedad.alpha * 0.55f), Color.Transparent), s.top, s.top + largo),
                            Offset(cx - s.width * 0.12f, s.top), Size(s.width * 0.24f, minOf(largo, s.height)))
                    }
                    if (musgoso) for (j in 0 until 7) {
                        val pr = g * (0.6f + (j % 3) * 0.4f)
                        drawCircle(mezcla(musgo, Color.Black, 0.1f * (j % 3)), pr, Offset(s.left + s.width * (0.2f + 0.08f * j + 0.2f * mx), s.bottom - g * (0.5f + (j % 2) * 1.2f)))
                    }
                }
                x += an
            }
        }
    }

    /** La piedra de la baldosa en [r], corrida [dx] y [dy] (para que cada casillero tenga sus propias piedras). */
    fun DrawScope.piedraEn(r: Rect, a: AireFrente, dx: Float, dy: Float) {
        val b = baldosa(density, a)
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawContext.transform.translate(dx, dy)
            drawRect(b, Offset(r.left - dx, r.top - dy), r.size)
            drawContext.transform.translate(-dx, -dy)
        }
    }
}
