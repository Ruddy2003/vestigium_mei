package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.NOCHE_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraContacto
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * El mueble de la galería con sus cuadros: lo que pone `RecuadroGaleria` (y el arnés de los muebles) dentro de su escena. El
 * mueble del tema ([d], `DisenoVida.muebleGaleria`), las [imagenes] en sus huecos (las primeras [MuebleGaleriaComun.CUANTOS];
 * null deja el lienzo en blanco) y encima la luz de su marco ([MuebleGaleriaComun.encima]). Sin ninguna imagen, el lienzo del
 * marco grande lleva un corazón a lápiz: ahí irá el primero.
 *
 * Los huecos son los del dibujo por omisión ([MuebleGaleriaComun.huecos]): un tema que reescriba `muebleGaleria` debe dejar
 * sus marcos en esos mismos sitios.
 */
@Composable
internal fun ParedDeCuadros(d: DisenoVida, e: EstadoMueble, oscuro: Boolean, imagenes: List<ImageBitmap?>, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) { with(d) { muebleGaleria(e, oscuro) } }
        Canvas(Modifier.fillMaxSize()) {
            if (size.width < 1f || size.height < 1f) return@Canvas
            val nicho = d.areaGaleria()
            val huecos = MuebleGaleriaComun.huecos(size.width, size.height, nicho)
            huecos.forEachIndexed { i, r -> imagenes.getOrNull(i)?.let { img -> cuadroEn(img, r) } }
            if (imagenes.none { it != null }) huecos.firstOrNull()?.let { r -> with(MuebleGaleriaComun) { corazonALapiz(r, e, oscuro) } }
            with(MuebleGaleriaComun) { encima(e, oscuro, nicho) }
        }
    }
}

/** Pone [img] en [r] llenándolo, recortada al centro si no es 4:3. */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.cuadroEn(img: ImageBitmap, r: Rect) {
    if (img.width <= 0 || img.height <= 0 || r.width < 1f || r.height < 1f) return
    val prop = r.width / r.height
    var sw = img.width; var sh = (img.width / prop).roundToInt()
    if (sh > img.height) { sh = img.height; sw = (img.height * prop).roundToInt().coerceAtMost(img.width) }
    drawImage(
        img, srcOffset = IntOffset((img.width - sw) / 2, (img.height - sh) / 2), srcSize = IntSize(sw.coerceAtLeast(1), sh.coerceAtLeast(1)),
        dstOffset = IntOffset(r.left.roundToInt(), r.top.roundToInt()), dstSize = IntSize(r.width.roundToInt().coerceAtLeast(1), r.height.roundToInt().coerceAtLeast(1)),
        filterQuality = FilterQuality.High,
    )
}

/**
 * El mueble de la galería por omisión (parte 8, encargo C; `docs/diseno/CUADROS_PLAN.md`): el que usan los 39 temas mientras
 * no dibujen el suyo (`DisenoVida.muebleGaleria`). Solo con los colores de [EstadoMueble], para que valga en cualquier tema,
 * en claro y en oscuro, sin parecer de ninguno.
 *
 * Es una pared con su moldura de colgar (`MUEBLES.md`, versión 2: solo el mueble y la pared, nada de ventanas):
 * - arriba, el **riel** de colgar cuadros, con sus ganchos;
 * - cuatro **marcos** colgados de sus cordeles, de tamaños distintos y acomodados como en una pared de casa (uno grande a la
 *   izquierda, dos chicos uno sobre otro y uno mediano), cada uno con su sombra en la pared. Dentro de cada marco está el
 *   **hueco** ([huecos]) donde la pantalla pone la miniatura de un cuadro guardado; sin cuadro, se ve un lienzo en blanco;
 * - debajo, una **repisa** con sus ménsulas y lo que quedó de colgar: el carrete de cordel, el martillo, la lata de alcayatas
 *   con dos sueltas y un cuadro apoyado contra la pared, sin colgar todavía;
 * - una **lámpara de cuadro** de latón sobre el marco grande: de día está apagada y la luz viene de arriba a la izquierda
 *   (una mancha clara en la pared, las sombras a la derecha y abajo); de noche está encendida y es la única luz.
 *
 * Lo que la pantalla pone encima de los huecos se ilumina igual que el resto con [encima]: la sombra que el marco deja en
 * la imagen, el reflejo del vidrio y, de noche, la luz de la lámpara.
 */
internal object MuebleGaleriaComun {

    /** Un marco colgado: su borde de fuera, el [hueco] de la imagen, su [estilo] (0 a 3) y el ancho de su moldura y su paspartú. */
    class Colgado(val marco: Rect, val hueco: Rect, val estilo: Int, val moldura: Float, val paspartu: Float)

    /** La moldura y el paspartú de cada estilo, en dp de un lienzo de 360: dorado ancho, madera fina con paspartú, negro liso, tallado con paspartú. */
    private val MOLDURA = floatArrayOf(5.2f, 2.6f, 3.4f, 3.2f)
    private val PASPARTU = floatArrayOf(0.6f, 3.4f, 0.4f, 3.6f)

    /** Cuántos cuadros caben: los últimos guardados, del más nuevo al más viejo, en el orden de [huecos]. */
    const val CUANTOS = 4

    /**
     * Dónde va cada marco en un lienzo de [ancho] × [alto] pixeles con el [nicho] (fracciones de 0 a 1), en el orden en que se
     * llenan: el grande, el mediano de la derecha, el chico de arriba y el chico de abajo. Vacío si el lienzo no tiene sitio.
     */
    fun colgados(ancho: Float, alto: Float, nicho: Rect): List<Colgado> {
        if (!(ancho >= 1f && alto >= 1f)) return emptyList()
        val n = Rect(nicho.left * ancho, nicho.top * alto, nicho.right * ancho, nicho.bottom * alto)
        if (n.width < 8f || n.height < 8f) return emptyList()
        val u = min(ancho / 360f, alto / 190f)
        val e = FloatArray(4) { (MOLDURA[it] + PASPARTU[it]) * u }
        val alturaUtil = n.height * 0.70f
        val vertical = 5f * u
        // Las alturas de las imágenes (4:3): la grande llena la altura útil, las dos chicas se apilan y la mediana es 3/4 de la grande.
        var hA = alturaUtil - 2 * e[0]
        var hB = (alturaUtil - 2 * e[1] - 2 * e[2] - vertical) / 2f
        var hC = 0.74f * hA
        val fijo = 2 * e[0] + 2 * max(e[1], e[2]) + 2 * e[3]
        val minimoHueco = 16f * u
        var hueco = 10f * u
        val anchos = { (hA + hB + hC) * 4f / 3f }
        if (anchos() + fijo + 2 * hueco > n.width) {
            val f = (n.width - fijo - 2 * hueco) / anchos()
            if (!(f > 0f)) return emptyList()
            hA *= f; hB *= f; hC *= f
        }
        if (hB < 2f || hA < 2f || hC < 2f) return emptyList()
        // Lo que sobra a lo ancho: un poco más entre marcos y el resto se reparte a los lados, más a la derecha (asimetría).
        val sobra = max(0f, n.width - anchos() - fijo - 2 * hueco)
        hueco = min(hueco + sobra * 0.22f, max(hueco, minimoHueco + 8f * u))
        val resto = max(0f, n.width - anchos() - fijo - 2 * hueco)
        val arriba = n.top + n.height * 0.03f
        var x = n.left + resto * 0.35f
        fun marco(estilo: Int, x0: Float, y0: Float, h: Float): Colgado {
            val m = MOLDURA[estilo] * u; val p = PASPARTU[estilo] * u
            val dentro = Rect(x0 + m + p, y0 + m + p, x0 + m + p + h * 4f / 3f, y0 + m + p + h)
            return Colgado(Rect(dentro.left - m - p, dentro.top - m - p, dentro.right + m + p, dentro.bottom + m + p), dentro, estilo, m, p)
        }
        val a = marco(0, x, arriba, hA)
        x = a.marco.right + hueco
        // La columna de los dos chicos: el más ancho de los dos marca la columna; cada uno, centrado en ella.
        val anchoB = hB * 4f / 3f + 2 * max(e[1], e[2])
        val b1 = marco(1, x + (anchoB - hB * 4f / 3f - 2 * e[1]) / 2f, arriba + n.height * 0.05f, hB)
        val b2 = marco(2, x + (anchoB - hB * 4f / 3f - 2 * e[2]) / 2f, b1.marco.bottom + vertical, hB)
        x += anchoB + hueco
        val c = marco(3, x, arriba + n.height * 0.12f, hC)
        return listOf(a, c, b1, b2)
    }

    /** Los huecos de las imágenes, en pixeles, en el orden en que se llenan (ver [colgados]). */
    fun huecos(ancho: Float, alto: Float, nicho: Rect): List<Rect> = colgados(ancho, alto, nicho).map { it.hueco }

    // ---------------------------------------------------------------- la paleta

    /**
     * La paleta corta del mueble, toda sacada de los cuatro colores del tema: la pared, la madera (de la moldura, la repisa y
     * el martillo), el metal (ganchos, lámpara, lata), el lienzo y los cuatro marcos. De noche no es la de día con un velo: es
     * otra, hundida hacia el fondo oscuro del tema y el azul violeta de la noche, y la enciende la lámpara.
     */
    private class Paleta(e: EstadoMueble, val oscuro: Boolean) {
        val f = e.fondo; val t = e.tinta; val a = e.acento; val s = e.secundario
        val pared: Color = if (!oscuro) lerp(lerp(f, t, 0.10f), s, 0.07f) else lerp(lerp(f, NOCHE_MUEBLE, 0.45f), t, 0.05f)
        val paredHonda: Color = if (!oscuro) lerp(pared, t, 0.14f) else lerp(pared, Color.Black, 0.35f)
        val madera: Color = if (!oscuro) lerp(lerp(f, t, 0.52f), a, 0.22f) else lerp(lerp(f, t, 0.20f), a, 0.16f)
        val maderaClara: Color = lerp(madera, if (!oscuro) f else t, 0.28f)
        val maderaHonda: Color = lerp(madera, if (!oscuro) t else Color.Black, 0.38f)
        val metal: Color = if (!oscuro) lerp(lerp(f, t, 0.30f), a, 0.30f) else lerp(lerp(f, t, 0.30f), a, 0.30f)
        val lienzo: Color = if (!oscuro) lerp(lerp(f, Color(0xFFFFF6E6), 0.45f), t, 0.05f) else lerp(f, t, 0.30f)
        val papel: Color = if (!oscuro) lerp(f, Color(0xFFFFF8EC), 0.6f) else lerp(f, t, 0.36f)
        val cordel: Color = if (!oscuro) lerp(lerp(f, t, 0.45f), a, 0.10f) else lerp(f, t, 0.40f)
        /** Los marcos: dorado (del acento), madera oscura, negro y tallado (del secundario). */
        val marcos: List<Color> = if (!oscuro) listOf(lerp(a, t, 0.18f), lerp(madera, t, 0.32f), lerp(t, f, 0.12f), lerp(s, t, 0.22f))
        else listOf(lerp(a, f, 0.42f), lerp(madera, f, 0.25f), lerp(f, Color.Black, 0.35f), lerp(s, f, 0.48f))
        fun luz(c: Color, k: Float): Color = lerp(c, if (oscuro) FILO_MUEBLE else Color(0xFFFFF4DC), k)
        fun sombra(c: Color, k: Float): Color = lerp(c, if (oscuro) Color(0xFF0B0A12) else Color(0xFF2A2436), k)
    }

    /** Un número de 0 a 1, siempre el mismo para la misma [semilla] y [k]. */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        return ((x xor (x ushr 16)) and 0xffffff) / 16777215f
    }

    /** Dónde cae la luz de la lámpara: un poco abajo del borde de arriba del marco grande. */
    private fun focoLampara(a: Colgado): Offset = Offset(a.marco.center.x, a.marco.top + a.marco.height * 0.30f)

    // ---------------------------------------------------------------- el dibujo

    /** Dibuja el mueble a todo el lienzo, con el nicho [nicho] (en fracciones de 0 a 1), sin las imágenes de los huecos. */
    fun DrawScope.dibujar(e: EstadoMueble, oscuro: Boolean, nicho: Rect) {
        val w = size.width; val h = size.height
        if (!(w >= 1f && h >= 1f)) return
        val u = min(w / 360f, h / 190f)
        val p = Paleta(e, oscuro)
        val todo = Rect(0f, 0f, w, h)
        val cs = colgados(w, h, nicho)
        val n = Rect(nicho.left * w, nicho.top * h, nicho.right * w, nicho.bottom * h)

        // 1. La pared: pintada a mano, con la mancha de luz arriba a la izquierda (de día) y más honda abajo.
        drawRect(p.pared)
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.paredHonda.copy(alpha = 0.55f)), startY = h * 0.35f, endY = h))
        pinceladas(todo, p.paredHonda, e.semilla, 30, u, if (oscuro) 0.10f else 0.09f)
        pinceladas(todo, p.luz(p.pared, 1f), e.semilla + 5, 18, u, if (oscuro) 0.025f else 0.07f)
        if (!oscuro) drawRect(Brush.radialGradient(listOf(luzMueble(0.34f), luzMueble(0.10f), Color.Transparent), Offset(w * 0.10f, -h * 0.15f), w * 0.75f))
        // El zócalo, cortado por el borde de abajo.
        val zocalo = Rect(0f, h * 0.955f, w, h)
        drawRect(p.maderaHonda, zocalo.topLeft, zocalo.size)
        drawRect(p.luz(p.maderaHonda, 0.25f), zocalo.topLeft, Size(w, max(1f, 1.1f * u)))
        drawRect(sombraMueble(0.18f), Offset(0f, zocalo.top - 2f * u), Size(w, 2f * u))

        // 2. El riel de colgar, a lo ancho, con su sombra en la pared.
        val riel = Rect(0f, h * 0.048f, w, h * 0.048f + 6.2f * u)
        drawRect(sombraMueble(if (oscuro) 0.40f else 0.20f), Offset(0f, riel.bottom), Size(w, 3.2f * u))
        drawRect(sombraMueble(if (oscuro) 0.20f else 0.09f), Offset(0f, riel.bottom + 3.2f * u), Size(w, 3.4f * u))
        drawRect(p.madera, riel.topLeft, riel.size)
        drawRect(p.luz(p.madera, 0.30f), riel.topLeft, Size(w, riel.height * 0.30f))
        drawRect(p.maderaHonda, Offset(0f, riel.top + riel.height * 0.55f), Size(w, max(1f, 0.9f * u)))
        drawRect(p.sombra(p.madera, 0.25f), Offset(0f, riel.bottom - riel.height * 0.22f), Size(w, riel.height * 0.22f))
        pinceladas(riel, p.maderaHonda, e.semilla + 9, 12, u * 0.5f, 0.18f)

        // 3. Los cordeles, de cada gancho a las dos armellas de su marco (detrás de los marcos).
        cs.forEach { c ->
            val g = Offset(c.marco.center.x, riel.bottom - 0.6f * u)
            val izq = Offset(c.marco.left + c.marco.width * 0.16f, c.marco.top + c.moldura * 0.5f)
            val der = Offset(c.marco.right - c.marco.width * 0.16f, c.marco.top + c.moldura * 0.5f)
            listOf(izq, der).forEach { q ->
                drawLine(sombraMueble(0.22f), g + Offset(1.4f * u, 1.6f * u), q + Offset(1.4f * u, 1.6f * u), 0.8f * u)
                drawLine(p.cordel, g, q, 0.75f * u, StrokeCap.Round)
            }
        }

        // 4. La repisa con sus ménsulas: la tabla vista un poco desde arriba, con su canto y su sombra en la pared.
        val tablaY = max(n.bottom + 1.5f * u, h * 0.835f)
        val tabla = Rect(w * 0.025f, tablaY, w * 0.975f, tablaY + 2.6f * u)
        val canto = Rect(tabla.left, tabla.bottom, tabla.right, tabla.bottom + 5.2f * u)
        drawRect(sombraMueble(if (oscuro) 0.40f else 0.24f), Offset(tabla.left + 3f * u, canto.bottom), Size(tabla.width, 4.5f * u))
        listOf(0.13f, 0.87f).forEach { fx ->
            val x = w * fx
            val m = Path().apply {
                moveTo(x - 2.4f * u, canto.bottom); lineTo(x + 2.4f * u, canto.bottom); lineTo(x + 2.4f * u, canto.bottom + 3f * u)
                quadraticTo(x + 1.2f * u, canto.bottom + 11f * u, x - 2.4f * u, canto.bottom + 15f * u); close()
            }
            drawPath(m, sombraMueble(if (oscuro) 0.35f else 0.20f), blendMode = BlendMode.SrcOver)
            drawRect(sombraMueble(if (oscuro) 0.35f else 0.20f), Offset(x + 2.4f * u, canto.bottom), Size(2.6f * u, 14f * u))
            drawRect(p.madera, Offset(x - 2.4f * u, canto.bottom), Size(4.8f * u, 15f * u))
            drawRect(p.luz(p.madera, 0.22f), Offset(x - 2.4f * u, canto.bottom), Size(1.4f * u, 15f * u))
            drawRect(p.sombra(p.madera, 0.30f), Offset(x + 1.2f * u, canto.bottom), Size(1.2f * u, 15f * u))
        }
        drawRect(p.maderaClara, tabla.topLeft, tabla.size)
        drawRect(p.madera, canto.topLeft, canto.size)
        drawRect(p.luz(p.madera, 0.32f), canto.topLeft, Size(canto.width, max(1f, 0.8f * u)))
        drawRect(p.sombra(p.madera, 0.25f), Offset(canto.left, canto.bottom - 1.2f * u), Size(canto.width, 1.2f * u))
        pinceladas(Rect(tabla.left, tabla.top, tabla.right, canto.bottom), p.maderaHonda, e.semilla + 13, 16, u * 0.6f, 0.22f)

        // 5. Lo de encima de la repisa (de atrás hacia adelante): el cuadro apoyado, el carrete, la lata y el martillo.
        val cMarco = cs.getOrNull(1)?.marco
        val apoyado = run {
            val alto = min(h * 0.24f, tabla.top - (cMarco?.bottom ?: (tabla.top - h * 0.24f)) - 4f * u)
            val ancho = alto * 1.22f
            if (alto > 8f * u) Rect(w * 0.955f - ancho, tabla.top - alto, w * 0.955f, tabla.top + 0.8f * u) else null
        }
        if (apoyado != null) cuadroApoyado(p, apoyado, u)
        carrete(p, Offset(w * 0.075f, tabla.top + 0.6f * u), u)
        lata(p, Offset(w * 0.47f, tabla.top + 0.6f * u), u, e.semilla)
        martillo(p, Offset(w * 0.20f, tabla.top + 0.4f * u), u)

        // 6. Los marcos, con su sombra en la pared y el lienzo en blanco dentro.
        cs.forEach { c -> marco(p, c, u) }
        // Los ganchos, encima del riel.
        cs.forEach { c -> gancho(p, Offset(c.marco.center.x, riel.bottom - 0.6f * u), u) }

        // 7. La lámpara de cuadro, sobre el marco grande, prendida del riel.
        cs.firstOrNull()?.let { lampara(p, it, riel, u) }

        // 8. La noche: lo lejano se hunde y la lámpara alumbra el marco grande y la pared de alrededor.
        if (oscuro) cs.firstOrNull()?.let { nocheDeLampara(it, w, h) }

        // 9. El grano fino, al final.
        granoMueble(todo, oscuro, 0.9f)
    }

    /**
     * Lo que va encima de las imágenes que la pantalla puso en los huecos, para que se vean dentro del marco y con la misma
     * luz: la sombra que la moldura de arriba y la de la izquierda dejan en la imagen, el reflejo del vidrio y, de noche, la luz
     * de la lámpara (la misma que [dibujar] le dio al lienzo en blanco). Va en todos los huecos, con imagen o sin ella.
     */
    fun DrawScope.encima(e: EstadoMueble, oscuro: Boolean, nicho: Rect) {
        val w = size.width; val h = size.height
        if (!(w >= 1f && h >= 1f)) return
        val u = min(w / 360f, h / 190f)
        val cs = colgados(w, h, nicho)
        cs.forEach { c ->
            val r = c.hueco
            clipRect(r.left, r.top, r.right, r.bottom) {
                // La sombra de la moldura: arriba y a la izquierda (la luz viene de arriba a la izquierda).
                val s = max(1f, (if (c.paspartu > 1f * u) 1.6f else 2.6f) * u)
                drawRect(sombraMueble(0.30f), r.topLeft, Size(r.width, s))
                drawRect(sombraMueble(0.14f), Offset(r.left, r.top + s), Size(r.width, s))
                drawRect(sombraMueble(0.22f), r.topLeft, Size(s, r.height))
                // El reflejo del vidrio: una banda en diagonal, tenue.
                if (!oscuro) {
                    val reflejo = Path().apply {
                        moveTo(r.left + r.width * 0.52f, r.top); lineTo(r.left + r.width * 0.78f, r.top)
                        lineTo(r.left + r.width * 0.38f, r.bottom); lineTo(r.left + r.width * 0.12f, r.bottom); close()
                    }
                    drawPath(reflejo, luzMueble(0.09f))
                }
            }
        }
        if (oscuro) cs.firstOrNull()?.let { a -> cs.forEach { c -> clipRect(c.hueco.left, c.hueco.top, c.hueco.right, c.hueco.bottom) { nocheDeLampara(a, w, h) } } }
    }

    /** Un corazón a lápiz en el lienzo en blanco [r], con su trazo repasado: la invitación a guardar el primero. */
    fun DrawScope.corazonALapiz(r: Rect, e: EstadoMueble, oscuro: Boolean) {
        if (r.width < 4f || r.height < 4f) return
        val p = Paleta(e, oscuro)
        val c = Offset(r.center.x, r.center.y + r.height * 0.04f)
        val k = r.height * 0.26f
        fun corazon(dx: Float, dy: Float) = Path().apply {
            moveTo(c.x + dx, c.y + k + dy)
            cubicTo(c.x - k * 1.9f + dx, c.y - k * 0.1f + dy, c.x - k * 0.9f + dx, c.y - k * 1.5f + dy, c.x + dx, c.y - k * 0.45f + dy)
            cubicTo(c.x + k * 0.9f + dx, c.y - k * 1.5f + dy, c.x + k * 1.9f + dx, c.y - k * 0.1f + dy, c.x + dx, c.y + k + dy)
        }
        val grafito = p.sombra(p.lienzo, if (oscuro) 0.55f else 0.45f)
        drawPath(corazon(0f, 0f), grafito.copy(alpha = 0.75f), style = Stroke(max(1f, k * 0.05f), cap = StrokeCap.Round))
        drawPath(corazon(k * 0.04f, k * 0.03f), grafito.copy(alpha = 0.35f), style = Stroke(max(1f, k * 0.03f), cap = StrokeCap.Round))
    }

    /** La luz de la noche: alrededor del marco grande, la de la lámpara; lejos, la pared se hunde. */
    private fun DrawScope.nocheDeLampara(a: Colgado, w: Float, h: Float) {
        val foco = focoLampara(a)
        drawRect(Brush.radialGradient(listOf(Color.Transparent, Color(0xFF0B0A12).copy(alpha = 0.30f), Color(0xFF0B0A12).copy(alpha = 0.62f)), foco, w * 0.85f))
        drawRect(Brush.radialGradient(listOf(FILO_MUEBLE.copy(alpha = 0.34f), FILO_MUEBLE.copy(alpha = 0.10f), Color.Transparent), foco, max(a.marco.width, a.marco.height) * 1.15f), blendMode = BlendMode.Screen)
    }

    /** Un marco: su sombra en la pared, la moldura con su lado de luz y de sombra, el paspartú y el lienzo en blanco. */
    private fun DrawScope.marco(p: Paleta, c: Colgado, u: Float) {
        val r = c.marco
        val m = c.moldura
        val base = p.marcos[c.estilo]
        // La sombra en la pared, a la derecha y abajo: una dura y una suave.
        drawRect(sombraMueble(if (p.oscuro) 0.30f else 0.14f), Offset(r.left + 4.5f * u, r.top + 5f * u), r.size)
        drawRect(sombraMueble(if (p.oscuro) 0.45f else 0.26f), Offset(r.left + 2f * u, r.top + 2.4f * u), r.size)
        // La moldura: cuatro trapecios, el de arriba y el de la izquierda a la luz.
        val i = Rect(r.left + m, r.top + m, r.right - m, r.bottom - m)
        fun lado(a: Offset, b: Offset, c2: Offset, d: Offset, color: Color) =
            drawPath(Path().apply { moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(c2.x, c2.y); lineTo(d.x, d.y); close() }, color)
        drawRect(base, r.topLeft, r.size)
        lado(r.topLeft, Offset(r.right, r.top), Offset(i.right, i.top), i.topLeft, p.luz(base, 0.30f))
        lado(r.topLeft, i.topLeft, Offset(i.left, i.bottom), Offset(r.left, r.bottom), p.luz(base, 0.14f))
        lado(Offset(r.right, r.top), Offset(r.right, r.bottom), Offset(i.right, i.bottom), Offset(i.right, i.top), p.sombra(base, 0.24f))
        lado(Offset(r.left, r.bottom), Offset(r.right, r.bottom), Offset(i.right, i.bottom), Offset(i.left, i.bottom), p.sombra(base, 0.36f))
        when (c.estilo) {
            // Dorado: un bocel brillante a la mitad de la moldura y un filo hondo por dentro.
            0 -> {
                val b = Rect(r.left + m * 0.45f, r.top + m * 0.45f, r.right - m * 0.45f, r.bottom - m * 0.45f)
                drawRect(p.luz(base, 0.55f), b.topLeft, b.size, style = Stroke(max(1f, 0.7f * u)))
                drawLine(p.sombra(base, 0.45f), Offset(b.left, b.bottom), Offset(b.right, b.bottom), max(1f, 0.6f * u))
                drawLine(p.sombra(base, 0.45f), Offset(b.right, b.top), Offset(b.right, b.bottom), max(1f, 0.6f * u))
            }
            // Tallado: cuentas a lo largo de la moldura.
            3 -> {
                val paso = max(1f, 3.2f * u)
                var x = r.left + paso
                while (x < r.right - paso * 0.5f) {
                    drawCircle(p.luz(base, 0.42f), 0.55f * u, Offset(x, r.top + m * 0.5f))
                    drawCircle(p.sombra(base, 0.40f), 0.55f * u, Offset(x, r.bottom - m * 0.5f))
                    x += paso
                }
            }
            // Madera: la veta, en rayas finas.
            1 -> {
                drawLine(p.sombra(base, 0.25f), Offset(r.left + m, r.top + m * 0.4f), Offset(r.right - m, r.top + m * 0.45f), max(1f, 0.4f * u))
                drawLine(p.sombra(base, 0.25f), Offset(r.left + m * 0.4f, r.top + m), Offset(r.left + m * 0.5f, r.bottom - m), max(1f, 0.4f * u))
            }
            // Negro: solo un filo de luz en el canto de arriba.
            else -> drawLine(p.luz(base, 0.35f), Offset(r.left + 0.5f * u, r.top + 0.5f * u), Offset(r.right - 0.5f * u, r.top + 0.5f * u), max(1f, 0.5f * u))
        }
        // El canto de dentro de la moldura (el rebaje), oscuro.
        drawRect(p.sombra(base, 0.55f), i.topLeft, i.size, style = Stroke(max(1f, 0.6f * u)))
        // El paspartú, con su corte biselado claro.
        if (c.paspartu > 0.8f * u) {
            drawRect(p.papel, Offset(i.left + 0.3f * u, i.top + 0.3f * u), Size(i.width - 0.6f * u, i.height - 0.6f * u))
            val hu = c.hueco
            drawRect(p.luz(p.papel, 0.5f), Offset(hu.left - 0.8f * u, hu.top - 0.8f * u), Size(hu.width + 1.6f * u, hu.height + 1.6f * u), style = Stroke(max(1f, 0.7f * u)))
        }
        // El lienzo en blanco: tela cruda con su trama.
        lienzo(p, c.hueco, u)
    }

    /** Un lienzo sin pintar: tela cruda, con la trama apenas visible. */
    private fun DrawScope.lienzo(p: Paleta, r: Rect, u: Float) {
        drawRect(p.lienzo, r.topLeft, r.size)
        val paso = max(1f, 1.8f * u)
        val hilo = p.sombra(p.lienzo, 0.10f).copy(alpha = 0.5f)
        clipRect(r.left, r.top, r.right, r.bottom) {
            var y = r.top + paso * 0.5f
            while (y < r.bottom) { drawLine(hilo, Offset(r.left, y), Offset(r.right, y), max(1f, 0.25f * u)); y += paso }
            var x = r.left + paso * 0.5f
            while (x < r.right) { drawLine(hilo.copy(alpha = 0.3f), Offset(x, r.top), Offset(x, r.bottom), max(1f, 0.25f * u)); x += paso }
        }
    }

    /** Un gancho de riel: la S de metal que muerde la moldura, con su brillo. */
    private fun DrawScope.gancho(p: Paleta, g: Offset, u: Float) {
        val ruta = Path().apply {
            moveTo(g.x - 1.6f * u, g.y - 5.2f * u)
            quadraticTo(g.x - 1.6f * u, g.y - 7.2f * u, g.x, g.y - 7.2f * u)
            quadraticTo(g.x + 1.4f * u, g.y - 7.2f * u, g.x + 1.4f * u, g.y - 5f * u)
            lineTo(g.x + 1.4f * u, g.y - 0.6f * u)
            quadraticTo(g.x + 1.4f * u, g.y + 1.6f * u, g.x, g.y + 1.6f * u)
            quadraticTo(g.x - 1.2f * u, g.y + 1.6f * u, g.x - 1.2f * u, g.y + 0.2f * u)
        }
        drawPath(ruta, sombraMueble(0.35f), style = Stroke(1.1f * u, cap = StrokeCap.Round))
        drawPath(ruta, p.metal, style = Stroke(0.9f * u, cap = StrokeCap.Round))
        drawCircle(p.luz(p.metal, 0.6f), 0.4f * u, Offset(g.x - 0.6f * u, g.y - 6.6f * u))
    }

    /** La lámpara de cuadro: el brazo prendido del riel y la pantalla de latón sobre el marco grande. De noche, encendida. */
    private fun DrawScope.lampara(p: Paleta, a: Colgado, riel: Rect, u: Float) {
        val cx = a.marco.center.x + a.marco.width * 0.12f
        val pantallaY = a.marco.top - 3.2f * u
        val largo = min(a.marco.width * 0.52f, 46f * u)
        val pantalla = Rect(cx - largo / 2f, pantallaY - 3.4f * u, cx + largo / 2f, pantallaY)
        // El brazo: baja del riel y se dobla hacia la pantalla.
        val base = Offset(cx + 2f * u, riel.bottom)
        drawRect(sombraMueble(0.3f), Offset(base.x + 1.2f * u, base.y), Size(1.4f * u, pantalla.top - base.y + 1f * u))
        drawRect(p.metal, Offset(base.x - 0.7f * u, riel.top + riel.height * 0.35f), Size(1.4f * u, pantalla.top - riel.top - riel.height * 0.35f + 0.4f * u))
        drawRect(p.luz(p.metal, 0.45f), Offset(base.x - 0.7f * u, riel.top + riel.height * 0.35f), Size(0.5f * u, pantalla.top - riel.top - riel.height * 0.35f))
        drawRoundRect(p.metal, Offset(base.x - 2f * u, riel.top + 0.5f * u), Size(4f * u, riel.height * 0.6f), CornerRadius(0.8f * u))
        // La pantalla: medio cilindro, más claro arriba y con la boca oscura (o encendida) abajo.
        drawRoundRect(sombraMueble(0.35f), Offset(pantalla.left + 1.5f * u, pantalla.top + 2f * u), pantalla.size, CornerRadius(1.6f * u))
        drawRoundRect(p.metal, pantalla.topLeft, pantalla.size, CornerRadius(1.6f * u))
        drawRoundRect(p.luz(p.metal, 0.5f), Offset(pantalla.left + 1f * u, pantalla.top + 0.4f * u), Size(pantalla.width - 2f * u, 1f * u), CornerRadius(0.5f * u))
        drawRect(p.sombra(p.metal, 0.35f), Offset(pantalla.left, pantalla.bottom - 1f * u), Size(pantalla.width, 1f * u))
        drawCircle(p.sombra(p.metal, 0.2f), 1.3f * u, Offset(pantalla.left + 0.6f * u, pantalla.center.y))
        drawCircle(p.sombra(p.metal, 0.2f), 1.3f * u, Offset(pantalla.right - 0.6f * u, pantalla.center.y))
        if (p.oscuro) {
            // La boca encendida y el cono de luz que baja sobre el marco y la pared.
            drawRect(FILO_MUEBLE, Offset(pantalla.left + 1.2f * u, pantalla.bottom - 0.9f * u), Size(pantalla.width - 2.4f * u, 0.9f * u))
            val cono = Path().apply {
                moveTo(pantalla.left + 1.2f * u, pantalla.bottom); lineTo(pantalla.right - 1.2f * u, pantalla.bottom)
                lineTo(a.marco.right + a.marco.width * 0.18f, a.marco.top + a.marco.height * 0.85f)
                lineTo(a.marco.left - a.marco.width * 0.18f, a.marco.top + a.marco.height * 0.85f); close()
            }
            drawPath(cono, Brush.verticalGradient(listOf(FILO_MUEBLE.copy(alpha = 0.22f), Color.Transparent), pantalla.bottom, a.marco.top + a.marco.height * 0.85f), blendMode = BlendMode.Screen)
            drawCircle(Brush.radialGradient(listOf(FILO_MUEBLE.copy(alpha = 0.55f), Color.Transparent), Offset(cx, pantalla.bottom), 9f * u), 9f * u, Offset(cx, pantalla.bottom), blendMode = BlendMode.Screen)
        }
    }

    /** El cuadro apoyado contra la pared, sin colgar: un paisajito con su marco, ladeado. */
    private fun DrawScope.cuadroApoyado(p: Paleta, r: Rect, u: Float) {
        // La sombra en la pared y en la tabla.
        sombraContacto(Offset(r.center.x + 3f * u, r.bottom - 0.6f * u), r.width * 1.05f, 3.2f * u, if (p.oscuro) 0.5f else 0.32f)
        rotate(-6f, Offset(r.left, r.bottom)) {
            drawRect(sombraMueble(if (p.oscuro) 0.4f else 0.22f), Offset(r.left + 3.2f * u, r.top + 1.6f * u), r.size)
            val base = lerp(p.madera, p.marcos[1], 0.5f)
            drawRect(base, r.topLeft, r.size)
            val m = 2.6f * u
            drawRect(p.luz(base, 0.3f), r.topLeft, Size(r.width, m))
            drawRect(p.sombra(base, 0.3f), Offset(r.right - m, r.top), Size(m, r.height))
            val i = Rect(r.left + m, r.top + m, r.right - m, r.bottom - m)
            if (i.width > 2f && i.height > 2f) clipRect(i.left, i.top, i.right, i.bottom) {
                // Un paisajito sencillo con los colores del tema: cielo, un sol bajo, dos lomas.
                val cielo = lerp(p.s, if (p.oscuro) p.t else p.f, if (p.oscuro) 0.25f else 0.55f)
                drawRect(cielo, i.topLeft, i.size)
                drawCircle(lerp(p.a, Color(0xFFFFE8B0), 0.5f), i.height * 0.13f, Offset(i.left + i.width * 0.68f, i.top + i.height * 0.42f))
                val loma1 = Path().apply {
                    moveTo(i.left, i.top + i.height * 0.62f); quadraticTo(i.left + i.width * 0.35f, i.top + i.height * 0.40f, i.right, i.top + i.height * 0.70f)
                    lineTo(i.right, i.bottom); lineTo(i.left, i.bottom); close()
                }
                drawPath(loma1, lerp(p.s, p.t, 0.35f))
                val loma2 = Path().apply {
                    moveTo(i.left, i.top + i.height * 0.86f); quadraticTo(i.left + i.width * 0.6f, i.top + i.height * 0.62f, i.right, i.top + i.height * 0.82f)
                    lineTo(i.right, i.bottom); lineTo(i.left, i.bottom); close()
                }
                drawPath(loma2, lerp(p.a, p.t, 0.45f))
                drawRect(sombraMueble(0.25f), i.topLeft, Size(i.width, 1.2f * u))
            }
        }
    }

    /** El carrete de cordel, de pie, con la punta del cordel suelta. */
    private fun DrawScope.carrete(p: Paleta, pie: Offset, u: Float) {
        val an = 9f * u; val al = 10f * u
        sombraContacto(Offset(pie.x + 2f * u, pie.y), an * 1.4f, 2.6f * u, if (p.oscuro) 0.5f else 0.3f)
        val cuerpo = Rect(pie.x - an / 2f + 1.2f * u, pie.y - al + 1.4f * u, pie.x + an / 2f - 1.2f * u, pie.y - 1.4f * u)
        drawRect(p.cordel, cuerpo.topLeft, cuerpo.size)
        val paso = max(1f, 1.1f * u)
        var y = cuerpo.top + paso * 0.5f
        while (y < cuerpo.bottom) { drawLine(p.sombra(p.cordel, 0.25f), Offset(cuerpo.left, y), Offset(cuerpo.right, y + 0.3f * u), max(1f, 0.35f * u)); y += paso }
        drawRect(p.luz(p.cordel, 0.3f), cuerpo.topLeft, Size(1.4f * u, cuerpo.height))
        drawRect(p.sombra(p.cordel, 0.3f), Offset(cuerpo.right - 1.4f * u, cuerpo.top), Size(1.4f * u, cuerpo.height))
        // Las dos tapas de madera.
        drawRoundRect(p.maderaClara, Offset(pie.x - an / 2f, pie.y - al), Size(an, 1.6f * u), CornerRadius(0.6f * u))
        drawRoundRect(p.madera, Offset(pie.x - an / 2f, pie.y - 1.6f * u), Size(an, 1.6f * u), CornerRadius(0.6f * u))
        // La punta del cordel, que cae por el canto de la repisa.
        val punta = Path().apply { moveTo(cuerpo.right, cuerpo.center.y); quadraticTo(pie.x + 8f * u, pie.y - 1f * u, pie.x + 7f * u, pie.y + 6.5f * u) }
        drawPath(punta, p.cordel, style = Stroke(0.6f * u, cap = StrokeCap.Round))
    }

    /** La lata de alcayatas, abierta, con las cabezas asomando y dos sueltas en la tabla. */
    private fun DrawScope.lata(p: Paleta, pie: Offset, u: Float, semilla: Int) {
        val an = 13f * u; val al = 6.4f * u
        sombraContacto(Offset(pie.x + 2.5f * u, pie.y), an * 1.3f, 2.6f * u, if (p.oscuro) 0.5f else 0.3f)
        val lata = Rect(pie.x - an / 2f, pie.y - al, pie.x + an / 2f, pie.y)
        // Las alcayatas de dentro, asomando por la boca.
        for (k in 0 until 6) {
            val x = lata.left + 1.5f * u + (an - 3f * u) * azar(semilla, k)
            val alto = (1.4f + 2.2f * azar(semilla + 3, k)) * u
            drawLine(p.metal, Offset(x, lata.top + 1f * u), Offset(x + (azar(semilla + 4, k) - 0.5f) * 2f * u, lata.top - alto), 0.6f * u, StrokeCap.Round)
        }
        drawRect(p.metal, lata.topLeft, lata.size)
        drawRect(p.luz(p.metal, 0.40f), lata.topLeft, Size(2f * u, lata.height))
        drawRect(p.sombra(p.metal, 0.35f), Offset(lata.right - 2.4f * u, lata.top), Size(2.4f * u, lata.height))
        drawRect(p.sombra(p.metal, 0.25f), Offset(lata.left, lata.top + 1.2f * u), Size(lata.width, 0.5f * u))
        drawOval(p.sombra(p.metal, 0.55f), Offset(lata.left + 0.4f * u, lata.top - 1f * u), Size(lata.width - 0.8f * u, 2f * u))
        // La tapa, recargada a un lado.
        rotate(-14f, Offset(lata.right + 1f * u, pie.y)) { drawRect(p.luz(p.metal, 0.15f), Offset(lata.right + 1f * u, pie.y - 1.2f * u), Size(an * 0.9f, 1.2f * u)) }
        // Dos alcayatas sueltas: un clavo con su codo.
        listOf(Offset(lata.right + 6f * u, pie.y - 0.5f * u) to 12f, Offset(lata.right + 11f * u, pie.y - 0.4f * u) to -30f).forEach { (q, g) ->
            rotate(g, q) {
                drawLine(sombraMueble(0.3f), q + Offset(0.8f * u, 0.6f * u), q + Offset(5.6f * u, 0.6f * u), 0.7f * u)
                drawLine(p.metal, q, q + Offset(5f * u, 0f), 0.6f * u, StrokeCap.Round)
                drawLine(p.metal, q + Offset(5f * u, 0f), q + Offset(5f * u, -1.8f * u), 0.6f * u, StrokeCap.Round)
                drawCircle(p.luz(p.metal, 0.5f), 0.3f * u, q + Offset(1.5f * u, -0.2f * u))
            }
        }
    }

    /** El martillo, acostado en la repisa, un poco ladeado. */
    private fun DrawScope.martillo(p: Paleta, pie: Offset, u: Float) {
        val largo = 24f * u
        sombraContacto(Offset(pie.x + largo * 0.45f, pie.y), largo * 1.1f, 2.4f * u, if (p.oscuro) 0.5f else 0.3f)
        rotate(-3f, pie) {
            // El mango, de madera clara, con su veta.
            val mango = Rect(pie.x + 4f * u, pie.y - 2.6f * u, pie.x + largo, pie.y - 0.4f * u)
            drawRoundRect(p.maderaClara, mango.topLeft, mango.size, CornerRadius(1f * u))
            drawLine(p.luz(p.maderaClara, 0.35f), Offset(mango.left + 1f * u, mango.top + 0.5f * u), Offset(mango.right - 1f * u, mango.top + 0.5f * u), 0.5f * u)
            drawLine(p.sombra(p.maderaClara, 0.25f), Offset(mango.left + 3f * u, mango.center.y + 0.3f * u), Offset(mango.right - 4f * u, mango.center.y), 0.3f * u)
            // La cabeza: la boca a la izquierda y la uña partida a la derecha del ojo.
            val cabeza = Rect(pie.x - 1f * u, pie.y - 5.4f * u, pie.x + 6.2f * u, pie.y - 0.2f * u)
            drawRoundRect(p.sombra(p.metal, 0.15f), cabeza.topLeft, cabeza.size, CornerRadius(0.8f * u))
            drawRect(p.luz(p.metal, 0.45f), Offset(cabeza.left, cabeza.top), Size(cabeza.width, 1f * u))
            drawRect(p.sombra(p.metal, 0.35f), Offset(cabeza.left, cabeza.bottom - 1f * u), Size(cabeza.width, 1f * u))
            drawRect(p.metal, Offset(cabeza.left - 1.6f * u, cabeza.top + 0.8f * u), Size(1.8f * u, cabeza.height - 1.6f * u))
        }
    }
}
