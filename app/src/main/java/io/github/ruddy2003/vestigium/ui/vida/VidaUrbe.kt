package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Urbe (la Roma de hoy): travertino y revoque ocre, el rojo pompeyano de las fachadas, el verde de las persianas y del
 * hierro de los quioscos, sampietrini en el suelo (la misma casa que su edícola del Ideario y su acera de logros).
 * - Social: la terraza de un bar en la piazza: la tenda a franjas arriba, dos sombrillas con su mesita de mármol, su
 *   tazzina y su silla de bistró en las orillas, y los sampietrini abajo; la fachada ocre, apenas, al fondo.
 * - Mapas: la edícola: el techo de zinc con el lambrequín festoneado, las columnas de hierro verde con las piantine
 *   colgadas de una pinza, el mostrador y la acera.
 * - Otras vidas: el escaparate de una tienda de antigüedades: la insegna verde con su filete de oro, el marco de nogal con
 *   cinco vanos de vidrio y sus reflejos, un atril de terciopelo en cada uno, sobre la soglia de travertino.
 * - Íconos: el Coliseo con su anillo caído, el rasca y gana con su moneda de un euro y las cúpulas como marcadores, junto
 *   al Tíber.
 * - Notas: el muro de revoque con desconchones que dejan ver el ladrillo, restos de carteles viejos y el marco de hierro de
 *   las affissioni; cada nota es un cartel pegado, con el borde de abajo rasgado, cinta en las esquinas (o una chincheta
 *   si está fijada) y, según el tipo, papel amarillo, rosa o verde, el timbro del Comune o las tiras para arrancar.
 */
internal object VidaUrbe : DisenoVida {
    private val TRAVERTINO = Color(0xFFE2D5BC)
    private val TRAVERTINO_SOMBRA = Color(0xFFC2B190)
    private val TRAVERTINO_PORO = Color(0xFFA8956F)
    private val REVOQUE = Color(0xFFD9A867)
    private val LADRILLO = Color(0xFFA2513A)
    private val CREMA = Color(0xFFF2E8CF)
    private val MARMOL = Color(0xFFF4F0E6)
    private val HIERRO = Color(0xFF2E3532)
    private val HIERRO_NOCHE = Color(0xFF5E6662)
    private val ZINC = Color(0xFF8C9490)
    private val ZINC_OSC = Color(0xFF5E6662)
    private val ADOQUIN = Color(0xFF7A746C)
    private val ADOQUIN_OSC = Color(0xFF4E4A44)
    private val NOGAL = Color(0xFF7A5232)
    private val NOGAL_LUZ = Color(0xFF9C6F45)
    private val ORO = Color(0xFFC9A24C)
    private val PAPEL = Color(0xFFF6F3EA)
    private val INSEGNA = Color(0xFF243A2A)
    private val PERSIANA = Color(0xFF3D5E45)
    private val TEVERE = Color(0xFF5E8CA8)
    /** Los restos de carteles viejos y los papeles de las notas. */
    private val ROSA = Color(0xFFE8B4A8)
    private val VERDE_BOLETO = Color(0xFFB8D0A8)
    private val OCRE_PALIDO = Color(0xFFE0C47A)
    private val CARTA = Color(0xFFF7F1E3)
    private val GIALLO = Color(0xFFF3E4A8)
    private val ROSA_PAPEL = Color(0xFFF2D9CF)
    private val VERDE_PAPEL = Color(0xFFDCE7D2)
    /** La luz de sodio de la calle, con la que se apagan los papeles de noche. */
    private val NOCHE_PAPEL = Color(0xFF6B6052)
    private val TINTA = Color(0xFF2B2420)
    private val TINTA_NOCHE = Color(0xFF241D18)

    private fun mez(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** Un número estable de 0 a 1 (las irregularidades de cada cosa, siempre las mismas). */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** Un material del tema a la luz de la tarjeta: de noche se hunde hacia el fondo, como los sampietrini bajo las farolas. */
    private fun t(c: Color, e: EstadoMueble, oscuro: Boolean, extra: Float = 0f): Color = mez(c, e.fondo, (if (oscuro) 0.42f else 0.06f) + extra)

    /** La franja clara de la tela de toldos y sombrillas: crema de noche; de día, un rosa del acento (el crema se perdería en el revoque). */
    private fun claraDeTela(e: EstadoMueble, oscuro: Boolean): Color = if (oscuro) t(CREMA, e, oscuro) else mez(e.acento, e.fondo, 0.72f)

    /** El hierro de mesas, sillas y marcos: casi negro de día; de noche, aclarado con la tinta para que se vea. */
    private fun hierro(e: EstadoMueble, oscuro: Boolean): Color = if (oscuro) e.tinta.copy(alpha = 0.45f) else HIERRO.copy(alpha = 0.85f)

    /** Un papel arrancado: el borde de arriba recto y los otros tres mordidos al azar hasta [hondo]. */
    private fun rasgado(r: Rect, semilla: Int, hondo: Float): Path = Path().apply {
        moveTo(r.left, r.top); lineTo(r.right, r.top)
        val n = 6
        for (i in 1..n) lineTo(r.right - hondo * azar(semilla, i), r.top + r.height * i / n)
        for (i in 1..n) lineTo(r.right - r.width * i / n, r.bottom - hondo * azar(semilla, i + 10))
        for (i in 1 until n) lineTo(r.left + hondo * azar(semilla, i + 20), r.bottom - r.height * i / n)
        close()
    }

    // ---------------------------------------------------------------- Social: la terraza del bar

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height
        // La fachada ocre del bar, apenas: el fondo de la piazza.
        drawRect(mez(e.fondo, REVOQUE, if (oscuro) 0.08f else 0.14f), Offset.Zero, size)
        // La tenda a franjas arriba, los sampietrini abajo y una mesa con su sombrilla en cada orilla.
        tenda(Rect(0f, 0f, w, h * 0.085f), e, oscuro)
        sampietrini(Rect(0f, h * 0.87f, w, h), e, oscuro)
        mesaConSombrilla(w * 0.085f, 1f, e, oscuro)
        mesaConSombrilla(w * 0.915f, -1f, e, oscuro)
    }

    /** La tenda del bar: franjas del acento y crema, con su festón de medias lunas. */
    private fun DrawScope.tenda(r: Rect, e: EstadoMueble, oscuro: Boolean) {
        val u = size.minDimension / 100f
        val n = 18
        val paso = r.width / n
        val franja = e.acento.copy(alpha = if (oscuro) 0.4f else 0.55f)
        val crema = claraDeTela(e, oscuro)
        for (j in 0 until n) drawRect(if (j % 2 == 0) franja else crema, Offset(r.left + j * paso, r.top), Size(paso + 0.5f, r.height * 0.7f))
        for (j in 0 until n) drawArc(if (j % 2 == 0) franja else crema, 0f, 180f, true, Offset(r.left + j * paso, r.top + r.height * 0.4f), Size(paso, r.height * 0.6f))
        drawLine(Color.Black.copy(alpha = 0.15f), Offset(r.left, r.top + r.height * 0.7f), Offset(r.right, r.top + r.height * 0.7f), 0.4f * u)
    }

    /** Los sampietrini: hiladas de adoquines cuadrados, alternadas, cada uno de un gris distinto. */
    private fun DrawScope.sampietrini(r: Rect, e: EstadoMueble, oscuro: Boolean) {
        val u = size.minDimension / 100f
        drawRect(mez(ADOQUIN_OSC, e.fondo, if (oscuro) 0.55f else 0.35f), r.topLeft, r.size)
        val la = 4.6f * u
        var y = r.top; var fila = 0
        while (y < r.bottom && fila < 12) {
            var x = r.left - (if (fila % 2 == 0) 0f else la / 2); var k = 0
            while (x < r.right && k < 120) {
                val tono = mez(mez(ADOQUIN_OSC, ADOQUIN, 0.35f + 0.65f * azar(fila + e.semilla, k)), e.fondo, if (oscuro) 0.5f else 0.28f)
                drawRoundRect(tono, Offset(x + 0.45f * u, y + 0.45f * u), Size(la - 0.9f * u, la - 0.9f * u), CornerRadius(1.1f * u))
                x += la; k++
            }
            y += la; fila++
        }
    }

    /**
     * Una sombrilla con su mesita en la orilla [x]: la tela en gajos del acento y crema con su festón, el mástil, la mesa
     * redonda de mármol con su pie de hierro y una tazzina hacia el centro ([s] es hacia dónde queda), y la silla de bistró
     * por fuera.
     */
    private fun DrawScope.mesaConSombrilla(x: Float, s: Float, e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val hierro = hierro(e, oscuro)
        val cy = h * 0.12f
        val ancho = w * 0.1f; val alto = h * 0.13f
        val tela = e.acento.copy(alpha = if (oscuro) 0.5f else 0.7f)
        val crema = claraDeTela(e, oscuro)
        val izq = x - ancho; val der = x + ancho; val base = cy + alto
        // El mástil, que baja hasta el pie de la mesa.
        drawLine(hierro, Offset(x, cy - 1.2f * u), Offset(x, h * 0.86f), 0.9f * u)
        // La tela, en cuatro gajos alternos, con su festón.
        val toldo = Path().apply {
            moveTo(x, cy)
            quadraticTo(x - ancho * 0.75f, cy + alto * 0.15f, izq, base)
            lineTo(der, base)
            quadraticTo(x + ancho * 0.75f, cy + alto * 0.15f, x, cy)
            close()
        }
        drawPath(toldo, crema)
        clipPath(toldo) {
            for (g in 0 until 4) if (g % 2 == 0) {
                val a0 = izq + (der - izq) * g / 4f; val a1 = izq + (der - izq) * (g + 1) / 4f
                drawPath(Path().apply { moveTo(x, cy); lineTo(a0, base); lineTo(a1, base); close() }, tela)
            }
        }
        for (g in 0 until 4) {
            val a0 = izq + (der - izq) * g / 4f
            drawArc(if (g % 2 == 0) tela else crema, 0f, 180f, true, Offset(a0, base - alto * 0.12f), Size((der - izq) / 4f, alto * 0.24f))
        }
        drawPath(toldo, Color.Black.copy(alpha = 0.12f), style = Stroke(0.4f * u))
        drawCircle(hierro, 0.9f * u, Offset(x, cy - 1.2f * u))
        // La silla de bistró, por fuera: el respaldo redondo, el asiento y las patas.
        val sx = x - s * w * 0.075f
        drawRoundRect(hierro, Offset(sx - 2.4f * u, h * 0.6f), Size(4.8f * u, h * 0.14f), CornerRadius(2.4f * u), style = Stroke(0.8f * u))
        drawLine(hierro, Offset(sx - 2.6f * u, h * 0.76f), Offset(sx + 2.6f * u, h * 0.76f), 1.1f * u, StrokeCap.Round)
        drawLine(hierro, Offset(sx - 2f * u, h * 0.76f), Offset(sx - 2.6f * u, h * 0.87f), 0.7f * u)
        drawLine(hierro, Offset(sx + 2f * u, h * 0.76f), Offset(sx + 2.6f * u, h * 0.87f), 0.7f * u)
        // La mesita de mármol con su pie de hierro.
        val my = h * 0.72f
        val mr = w * 0.065f
        drawLine(hierro, Offset(x, my), Offset(x, h * 0.865f), 1.4f * u)
        drawLine(hierro, Offset(x - mr * 0.55f, h * 0.875f), Offset(x + mr * 0.55f, h * 0.875f), 1.2f * u, StrokeCap.Round)
        drawOval(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(x - mr, my + 0.6f * u), Size(mr * 2, 2.6f * u))
        drawOval(t(MARMOL, e, oscuro, if (oscuro) 0.1f else 0f), Offset(x - mr, my - 1.4f * u), Size(mr * 2, 2.8f * u))
        drawOval(hierro, Offset(x - mr, my - 1.4f * u), Size(mr * 2, 2.8f * u), style = Stroke(0.4f * u))
        // La tazzina en su platito.
        val tx = x + s * mr * 0.35f; val ty = my - 0.4f * u
        val loza = t(PAPEL, e, oscuro, if (oscuro) 0.05f else 0f)
        val perfil = Color.Black.copy(alpha = if (oscuro) 0.35f else 0.25f)
        drawOval(loza, Offset(tx - 2.4f * u, ty - 0.8f * u), Size(4.8f * u, 1.4f * u))
        drawOval(perfil, Offset(tx - 2.4f * u, ty - 0.8f * u), Size(4.8f * u, 1.4f * u), style = Stroke(0.3f * u))
        drawRoundRect(loza, Offset(tx - 1.4f * u, ty - 3.4f * u), Size(2.8f * u, 3f * u), CornerRadius(0.6f * u))
        drawRoundRect(perfil, Offset(tx - 1.4f * u, ty - 3.4f * u), Size(2.8f * u, 3f * u), CornerRadius(0.6f * u), style = Stroke(0.3f * u))
        drawArc(perfil, -90f, 180f, false, Offset(tx + 1f * u, ty - 2.9f * u), Size(1.6f * u, 1.6f * u), style = Stroke(0.35f * u))
    }

    // ---------------------------------------------------------------- Mapas: la edícola

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val mapa = Rect(w * 0.06f, h * 0.1f, w * 0.94f, h * 0.9f)
        val verde = if (oscuro) mez(e.secundario, e.fondo, 0.45f) else e.secundario
        val verdeOsc = mez(verde, Color.Black, 0.3f)
        val crema = t(CREMA, e, oscuro)
        // El mostrador del quiosco, tenue donde va el mapa.
        drawRect(mez(e.fondo, CREMA, if (oscuro) 0.06f else 0.4f), mapa.topLeft, mapa.size)
        // Las columnas de hierro verde, con su capitel, su basa y su estría.
        val techo = h * 0.055f
        listOf(0f, w - w * 0.045f).forEach { x0 ->
            val col = Rect(x0, techo, x0 + w * 0.045f, h * 0.93f)
            drawRect(verde, col.topLeft, col.size)
            drawLine(verdeOsc, Offset(col.center.x, col.top + 3f * u), Offset(col.center.x, col.bottom - 3f * u), 0.7f * u)
            drawRect(verdeOsc, Offset(col.left, col.top + h * 0.05f), Size(col.width, 1.6f * u))
            drawRect(verdeOsc, Offset(col.left, col.bottom - 2.4f * u), Size(col.width, 2.4f * u))
        }
        // Las piantine colgadas de las columnas.
        var k = 0
        listOf(w * 0.0225f, w * 0.9775f).forEach { cx ->
            listOf(0.22f, 0.52f).forEach { f -> piantina(Offset(cx, h * f), w * 0.05f, h * 0.2f, e, oscuro, e.semilla + k); k++ }
        }
        // El techo de zinc, con sus juntas, y el lambrequín festoneado, verde y crema.
        drawRect(t(ZINC, e, oscuro), Offset.Zero, Size(w, techo))
        var x = 0f
        while (x < w) { drawLine(t(ZINC_OSC, e, oscuro), Offset(x, 0f), Offset(x, techo), 0.5f * u); x += w / 24f }
        drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.35f), Offset(0f, techo * 0.3f), Offset(w, techo * 0.3f), 0.5f * u)
        val nf = 16
        for (j in 0 until nf) drawArc(if (j % 2 == 0) verde else crema, 0f, 180f, true, Offset(w * j / nf, techo - h * 0.04f), Size(w / nf, h * 0.08f))
        // El mostrador de abajo, con su filete crema, y la acera.
        val banco = Rect(w * 0.045f, h * 0.905f, w * 0.955f, h * 0.945f)
        drawRect(verde, banco.topLeft, banco.size)
        drawLine(crema, Offset(banco.left, banco.top + 0.6f * u), Offset(banco.right, banco.top + 0.6f * u), 0.6f * u)
        sampietrini(Rect(0f, h * 0.945f, w, h), e, oscuro)
    }

    /** Una piantina doblada, colgada de su pinza en [c]: el papel con sus pliegues, el Tíber y una calle marcada. */
    private fun DrawScope.piantina(c: Offset, ancho: Float, alto: Float, e: EstadoMueble, oscuro: Boolean, semilla: Int) {
        val u = size.minDimension / 100f
        val r = Rect(c.x - ancho / 2, c.y, c.x + ancho / 2, c.y + alto)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(r.left + 0.6f * u, r.top + 0.8f * u), r.size)
        drawRect(t(PAPEL, e, oscuro, if (oscuro) 0.1f else 0f), r.topLeft, r.size)
        val pliegue = Color.Black.copy(alpha = 0.12f)
        drawLine(pliegue, Offset(r.center.x, r.top), Offset(r.center.x, r.bottom), 0.35f * u)
        drawLine(pliegue, Offset(r.left, r.top + alto / 3), Offset(r.right, r.top + alto / 3), 0.35f * u)
        drawLine(pliegue, Offset(r.left, r.top + alto * 2 / 3), Offset(r.right, r.top + alto * 2 / 3), 0.35f * u)
        val rio = Path().apply {
            moveTo(r.left, r.top + alto * (0.2f + 0.2f * azar(semilla, 1)))
            cubicTo(r.left + ancho * 0.4f, r.top + alto * 0.5f, r.left + ancho * 0.6f, r.top + alto * 0.3f, r.right, r.top + alto * (0.6f + 0.3f * azar(semilla, 2)))
        }
        drawPath(rio, TEVERE.copy(alpha = if (oscuro) 0.5f else 0.65f), style = Stroke(0.9f * u))
        drawLine(e.acento.copy(alpha = 0.7f), Offset(r.left + ancho * 0.2f, r.bottom - alto * 0.1f), Offset(r.right - ancho * 0.15f, r.top + alto * 0.15f), 0.7f * u)
        drawRect(hierro(e, oscuro), Offset(c.x - 1f * u, c.y - 1.2f * u), Size(2f * u, 2.4f * u))
    }

    // ---------------------------------------------------------------- Otras vidas: el escaparate del anticuario

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        // El marco de travertino de la fachada, con sus poros.
        drawRect(mez(TRAVERTINO, e.fondo, if (oscuro) 0.72f else 0.3f), Offset.Zero, size)
        val poro = mez(TRAVERTINO_PORO, e.fondo, if (oscuro) 0.6f else 0.2f).copy(alpha = 0.6f)
        for (k in 0 until 30) drawOval(poro, Offset(w * azar(e.semilla, k), h * azar(e.semilla, k + 30)), Size(u * (0.8f + 2f * azar(e.semilla, k + 60)), u * 0.45f))
        // La insegna: la tabla verde oscuro con su filete de oro y dos rosetas.
        val oro = t(ORO, e, oscuro)
        val ins = Rect(w * 0.03f, h * 0.025f, w * 0.97f, h * 0.11f)
        drawRect(mez(INSEGNA, e.fondo, if (oscuro) 0.25f else 0f), ins.topLeft, ins.size)
        drawRect(oro, Offset(ins.left + 1.2f * u, ins.top + 1.2f * u), Size(ins.width - 2.4f * u, ins.height - 2.4f * u), style = Stroke(0.5f * u))
        listOf(ins.left + 4f * u, ins.right - 4f * u).forEach { x -> drawCircle(oro, 1.1f * u, Offset(x, ins.center.y)) }
        // El escaparate de nogal y el interior en penumbra, con el terciopelo del fondo.
        val marco = Rect(w * 0.02f, h * 0.13f, w * 0.98f, h * 0.86f)
        val nogal = t(NOGAL, e, oscuro)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(marco.left + 0.6f * u, marco.top + 1f * u), marco.size)
        drawRect(nogal, marco.topLeft, marco.size)
        val vidrio = Rect(marco.left + 1.8f * u, marco.top + 1.8f * u, marco.right - 1.8f * u, marco.bottom - 1.8f * u)
        drawRect(mez(e.fondo, LADRILLO, if (oscuro) 0.1f else 0.08f), vidrio.topLeft, vidrio.size)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.04f), vidrio.topLeft, vidrio.size)
        // Los montantes que separan los cinco vanos.
        val paso = w * 0.184f
        for (i in 1..4) {
            val x = w / 2 + (i - 2.5f) * paso
            drawRect(nogal, Offset(x - 0.9f * u, vidrio.top), Size(1.8f * u, vidrio.height))
            drawLine(t(NOGAL_LUZ, e, oscuro), Offset(x - 0.9f * u, vidrio.top), Offset(x - 0.9f * u, vidrio.bottom), 0.4f * u)
        }
        drawRect(oro.copy(alpha = 0.7f), vidrio.topLeft, vidrio.size, style = Stroke(0.5f * u))
        // En cada vano, el atril de terciopelo bajo la medalla y los reflejos del vidrio.
        for (i in 0 until 5) {
            val cx = w / 2 + (i - 2) * paso
            val ay = h * 0.75f
            val atril = Path().apply {
                moveTo(cx - paso * 0.26f, vidrio.bottom); lineTo(cx - paso * 0.18f, ay)
                lineTo(cx + paso * 0.18f, ay); lineTo(cx + paso * 0.26f, vidrio.bottom); close()
            }
            drawPath(atril, e.acento.copy(alpha = if (oscuro) 0.28f else 0.22f))
            drawLine(oro.copy(alpha = 0.6f), Offset(cx - paso * 0.18f, ay), Offset(cx + paso * 0.18f, ay), 0.5f * u)
            val x0 = cx - paso * 0.42f
            drawLine(Color.White.copy(alpha = if (oscuro) 0.05f else 0.35f), Offset(x0 + paso * 0.12f, vidrio.top + 2f * u), Offset(x0, vidrio.top + paso * 0.35f), 1.6f * u)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.04f else 0.25f), Offset(x0 + paso * 0.24f, vidrio.top + 2f * u), Offset(x0 + paso * 0.08f, vidrio.top + paso * 0.45f), 0.7f * u)
        }
        // La soglia de travertino, con su luz, y la placa de latón al pie.
        val soglia = Rect(0f, h * 0.86f, w, h * 0.9f)
        drawRect(t(TRAVERTINO_SOMBRA, e, oscuro, if (oscuro) 0.15f else 0f), soglia.topLeft, soglia.size)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.07f else 0.4f), soglia.topLeft, Offset(soglia.right, soglia.top), 0.6f * u)
        drawRect(oro.copy(alpha = 0.5f), Offset(w * 0.02f, h * 0.9f), Size(w * 0.96f, h * 0.025f))
    }

    // ---------------------------------------------------------------- Íconos de los mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val c = Offset(size.width / 2, size.height / 2)
        val a = size.height * 0.4f
        when (id) {
            "unesco" -> colosseo(c, a, e)
            "rascar" -> grattaEVinci(c, a, e)
            else -> cupole(c, a, e)
        }
    }

    /** El Coliseo: tres pisos de arcos y el ático con sus ventanitas; a la derecha, el anillo exterior caído en escalones. */
    private fun DrawScope.colosseo(c: Offset, a: Float, e: EstadoIcono) {
        val izq = c.x - a * 1.35f; val der = c.x + a * 1.35f
        val abajo = c.y + a * 0.82f; val arriba = c.y - a * 0.9f
        val alto = abajo - arriba
        val silueta = Path().apply {
            moveTo(izq, abajo); lineTo(izq, arriba + alto * 0.06f)
            quadraticTo(c.x - a * 0.5f, arriba - alto * 0.02f, c.x + a * 0.25f, arriba)
            lineTo(c.x + a * 0.25f, arriba + alto * 0.12f)
            lineTo(c.x + a * 0.55f, arriba + alto * 0.12f)
            lineTo(c.x + a * 0.55f, arriba + alto * 0.3f)
            lineTo(c.x + a * 0.95f, arriba + alto * 0.36f)
            lineTo(c.x + a * 0.95f, arriba + alto * 0.52f)
            lineTo(der, arriba + alto * 0.58f)
            lineTo(der, abajo); close()
        }
        drawPath(silueta, e.c1)
        val n = 7; val celda = (der - izq) / n
        clipPath(silueta) {
            for (piso in 0 until 3) {
                val y0 = arriba + alto * (0.2f + 0.26f * piso)
                val y1 = y0 + alto * 0.26f
                drawRect(e.c2, Offset(izq, y0), Size(der - izq, alto * 0.03f))
                for (i in 0 until n) {
                    val cx = izq + celda * (i + 0.5f); val aw = celda * 0.56f
                    val top = y0 + alto * 0.07f
                    drawRect(e.fondo, Offset(cx - aw / 2, top + aw / 2), Size(aw, y1 - top - aw / 2 - alto * 0.01f))
                    drawCircle(e.fondo, aw / 2, Offset(cx, top + aw / 2))
                }
            }
            for (i in 0 until n) {
                val cx = izq + celda * (i + 0.5f)
                drawRect(e.fondo.copy(alpha = 0.8f), Offset(cx - celda * 0.15f, arriba + alto * 0.08f), Size(celda * 0.3f, alto * 0.07f))
            }
        }
        drawRect(e.c2, Offset(izq - a * 0.1f, abajo), Size(der - izq + a * 0.2f, a * 0.1f))
    }

    /** El rasca y gana: el boleto con su cabecera, la zona plateada rascada en zigzag y la moneda de un euro. */
    private fun DrawScope.grattaEVinci(c: Offset, a: Float, e: EstadoIcono) {
        val g = 1.6f * density
        val bol = Rect(c.x - a * 1.4f, c.y - a * 0.8f, c.x + a * 1.2f, c.y + a * 0.85f)
        val rad = CornerRadius(a * 0.08f)
        drawRoundRect(e.c2.copy(alpha = 0.2f), bol.topLeft, bol.size, rad)
        drawRect(e.c1, bol.topLeft, Size(bol.width, a * 0.28f))
        for (j in 0 until 3) drawCircle(e.fondo.copy(alpha = 0.8f), a * 0.05f, Offset(bol.left + a * (0.3f + 0.22f * j), bol.top + a * 0.14f))
        drawRoundRect(e.c2, bol.topLeft, bol.size, rad, style = Stroke(g))
        val zona = Rect(bol.left + a * 0.2f, bol.top + a * 0.48f, bol.right - a * 0.2f, bol.bottom - a * 0.22f)
        drawRect(mez(e.c2, e.fondo, 0.35f), zona.topLeft, zona.size)
        clipRect(zona.left, zona.top, zona.right, zona.bottom) {
            val p = Path().apply {
                moveTo(zona.left + a * 0.1f, zona.bottom - a * 0.1f)
                lineTo(zona.left + a * 0.45f, zona.top + a * 0.12f)
                lineTo(zona.left + a * 0.8f, zona.bottom - a * 0.1f)
                lineTo(zona.left + a * 1.15f, zona.top + a * 0.15f)
            }
            drawPath(p, e.c1, style = Stroke(a * 0.24f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        val mc = Offset(zona.left + a * 1.4f, zona.top + a * 0.02f); val mr = a * 0.36f
        drawCircle(Color.Black.copy(alpha = 0.18f), mr, Offset(mc.x + g * 0.6f, mc.y + g * 0.9f))
        drawCircle(e.c2, mr, mc)
        drawCircle(e.c1, mr * 0.62f, mc)
        drawCircle(e.fondo.copy(alpha = 0.5f), mr, mc, style = Stroke(g * 0.5f))
    }

    /** Tus lugares: el Tíber que cruza, dos calles y tres cúpulas como marcadores. */
    private fun DrawScope.cupole(c: Offset, a: Float, e: EstadoIcono) {
        val g = 1.6f * density
        val rio = Path().apply {
            moveTo(c.x - a * 1.5f, c.y - a * 0.1f)
            cubicTo(c.x - a * 0.6f, c.y + a * 0.9f, c.x + a * 0.2f, c.y - a * 0.7f, c.x + a * 1.5f, c.y + a * 0.5f)
        }
        drawPath(rio, e.c2.copy(alpha = 0.45f), style = Stroke(g * 2.2f, cap = StrokeCap.Round))
        val calle = e.c2.copy(alpha = 0.35f)
        drawLine(calle, Offset(c.x - a * 1.2f, c.y + a * 0.85f), Offset(c.x + a * 0.6f, c.y - a * 0.9f), g * 0.7f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(g * 2f, g)))
        drawLine(calle, Offset(c.x - a * 0.4f, c.y - a * 0.85f), Offset(c.x + a * 1.3f, c.y + a * 0.8f), g * 0.7f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(g * 2f, g)))
        listOf(
            Offset(c.x - a * 0.95f, c.y + a * 0.45f) to 1f,
            Offset(c.x + a * 0.05f, c.y + a * 0.05f) to 1.2f,
            Offset(c.x + a * 1.0f, c.y + a * 0.55f) to 0.85f,
        ).forEach { (p, k) -> cupola(p, a * 0.3f * k, e) }
    }

    /** Una cúpula de marcador con el pie en [p]: el tambor con sus ventanas, el casquete y la linterna, sobre su sombra. */
    private fun DrawScope.cupola(p: Offset, s: Float, e: EstadoIcono) {
        drawOval(Color.Black.copy(alpha = 0.16f), Offset(p.x - s * 1.2f, p.y - s * 0.15f), Size(s * 2.4f, s * 0.5f))
        drawRect(e.c1, Offset(p.x - s, p.y - s * 0.7f), Size(s * 2f, s * 0.7f))
        drawArc(e.c1, 180f, 180f, true, Offset(p.x - s * 0.92f, p.y - s * 0.7f - s * 0.92f), Size(s * 1.84f, s * 1.84f))
        drawRect(e.c1, Offset(p.x - s * 0.16f, p.y - s * 0.7f - s * 0.92f - s * 0.42f), Size(s * 0.32f, s * 0.46f))
        drawLine(e.fondo.copy(alpha = 0.7f), Offset(p.x - s, p.y - s * 0.7f), Offset(p.x + s, p.y - s * 0.7f), s * 0.12f)
        for (k in -1..1) drawRect(e.fondo.copy(alpha = 0.7f), Offset(p.x + k * s * 0.55f - s * 0.1f, p.y - s * 0.52f), Size(s * 0.2f, s * 0.35f))
    }

    // ---------------------------------------------------------------- Notas: el muro de las affissioni

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        // El muro de revoque ocre, con sus manchas de humedad.
        drawRoundRect(mez(REVOQUE, e.fondo, if (oscuro) 0.72f else 0.35f), Offset.Zero, size, CornerRadius(2f * u))
        for (k in 0 until 10) {
            val c = Offset(w * azar(e.semilla + 3, k), h * azar(e.semilla + 4, k))
            val r = (12f + 20f * azar(e.semilla + 5, k)) * u
            drawCircle(Brush.radialGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.12f else 0.06f), Color.Transparent), c, r), r, c)
        }
        // Donde se cayó el revoque: el ladrillo, en dos esquinas.
        desconchon(Offset(w * 0.9f, h * 0.2f), h * 0.13f, e, oscuro, e.semilla + 1)
        desconchon(Offset(w * 0.1f, h * 0.84f), h * 0.11f, e, oscuro, e.semilla + 2)
        // Los restos de carteles viejos, arrancados a medias.
        val colores = listOf(ROSA, VERDE_BOLETO, OCRE_PALIDO, PAPEL)
        for (k in 0 until 9) {
            val cx = w * (0.08f + 0.84f * azar(e.semilla + 7, k)); val cy = h * (0.1f + 0.8f * azar(e.semilla + 8, k))
            val ww = w * (0.1f + 0.12f * azar(e.semilla + 9, k)); val hh = h * (0.18f + 0.2f * azar(e.semilla + 10, k))
            val col = mez(colores[k % 4], e.fondo, if (oscuro) 0.75f else 0.45f)
            rotate(-4f + 8f * azar(e.semilla + 11, k), Offset(cx, cy)) {
                drawPath(rasgado(Rect(cx - ww / 2, cy - hh / 2, cx + ww / 2, cy + hh / 2), e.semilla + k, 2.5f * u), col)
            }
        }
        // El marco de hierro de las affissioni, con sus pernos y la placa esmaltada del Comune arriba.
        val hierro = if (oscuro) HIERRO_NOCHE else HIERRO
        drawRect(hierro, Offset(0.8f * u, 0.8f * u), Size(w - 1.6f * u, h - 1.6f * u), style = Stroke(1.6f * u))
        listOf(Offset(0.8f * u, 0.8f * u), Offset(w - 0.8f * u, 0.8f * u), Offset(0.8f * u, h - 0.8f * u), Offset(w - 0.8f * u, h - 0.8f * u)).forEach { p ->
            drawCircle(hierro, 1.7f * u, p)
            drawCircle(Color.White.copy(alpha = if (oscuro) 0.12f else 0.25f), 0.6f * u, Offset(p.x - 0.4f * u, p.y - 0.4f * u))
        }
        val placa = Rect(w * 0.42f, 0f, w * 0.58f, h * 0.05f)
        drawRoundRect(mez(CREMA, e.fondo, if (oscuro) 0.35f else 0f), placa.topLeft, placa.size, CornerRadius(0.8f * u))
        drawRoundRect(mez(PERSIANA, e.fondo, if (oscuro) 0.2f else 0f), Offset(placa.left + 0.6f * u, placa.top + 0.6f * u), Size(placa.width - 1.2f * u, placa.height - 1.2f * u), CornerRadius(0.6f * u), style = Stroke(0.5f * u))
    }

    /** Un desconchón del revoque centrado en [c]: el borde mordido y, dentro, el ladrillo con sus juntas. */
    private fun DrawScope.desconchon(c: Offset, r: Float, e: EstadoMueble, oscuro: Boolean, semilla: Int) {
        val u = size.minDimension / 100f
        val borde = Path().apply {
            val n = 11
            for (i in 0 until n) {
                val a = i * 2.0 * Math.PI / n
                val rr = r * (0.65f + 0.45f * azar(semilla, i))
                val px = c.x + rr * 1.5f * cos(a).toFloat(); val py = c.y + rr * sin(a).toFloat()
                if (i == 0) moveTo(px, py) else lineTo(px, py)
            }
            close()
        }
        val ladrillo = mez(LADRILLO, e.fondo, if (oscuro) 0.6f else 0.25f)
        clipPath(borde) {
            drawRect(mez(e.fondo, TRAVERTINO, 0.4f), Offset(c.x - r * 2f, c.y - r * 1.5f), Size(r * 4f, r * 3f))
            val alto = 2.6f * u; val largo = 7f * u
            var y = c.y - r * 1.2f; var f = 0
            while (y < c.y + r * 1.2f && f < 40) {
                var x = c.x - r * 1.8f - (if (f % 2 == 0) 0f else largo / 2)
                while (x < c.x + r * 1.8f) {
                    drawRect(ladrillo, Offset(x + 0.3f * u, y + 0.3f * u), Size(largo - 0.6f * u, alto - 0.6f * u))
                    x += largo
                }
                y += alto; f++
            }
        }
        drawPath(borde, Color.Black.copy(alpha = if (oscuro) 0.3f else 0.15f), style = Stroke(0.6f * u))
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        if (r.width < 1f || r.height < 1f) return
        val k = minOf(r.width, r.height) / 100f
        val base = when (e.tipo) {
            "idea" -> GIALLO
            "pensamiento" -> ROSA_PAPEL
            "proposito" -> VERDE_PAPEL
            else -> CARTA
        }
        val papel = if (oscuro) mez(base, NOCHE_PAPEL, 0.22f) else base
        val tinta = tintaNota(oscuro)
        // El cartel, con el borde de abajo rasgado, y su sombra en el muro.
        val hoja = Path().apply {
            moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom - 3f * k)
            val n = 9
            for (i in 1..n) lineTo(r.right - r.width * i / n, r.bottom - 3f * k * azar(e.indice + 3, i))
            close()
        }
        translate(1.4f * k, 2.2f * k) { drawPath(hoja, Color.Black.copy(alpha = if (oscuro) 0.4f else 0.18f)) }
        drawPath(hoja, papel)
        // Las arrugas de la cola.
        drawLine(Color.Black.copy(alpha = 0.06f), Offset(r.left + r.width * 0.15f, r.top + r.height * 0.2f), Offset(r.left + r.width * 0.5f, r.bottom - r.height * 0.1f), 0.8f * k)
        drawLine(Color.White.copy(alpha = 0.3f), Offset(r.left + r.width * 0.62f, r.top + r.height * 0.1f), Offset(r.right - r.width * 0.1f, r.top + r.height * 0.6f), 0.8f * k)
        // El detalle de cada tipo.
        when (e.tipo) {
            "idea" -> drawRect(LADRILLO.copy(alpha = 0.75f), Offset(r.left, r.top), Size(r.width, 2.6f * k))
            "pensamiento" -> {
                // La esquina doblada.
                val d = 9f * k
                drawPath(Path().apply { moveTo(r.right - d, r.top); lineTo(r.right, r.top + d); lineTo(r.right - d, r.top + d); close() }, mez(papel, Color.Black, 0.12f))
                drawLine(Color.Black.copy(alpha = 0.15f), Offset(r.right - d, r.top), Offset(r.right, r.top + d), 0.5f * k)
            }
            "proposito" -> {
                // El timbro del Comune, en verde.
                val c = Offset(r.right - 10f * k, r.top + 8f * k)
                drawCircle(PERSIANA.copy(alpha = 0.7f), 5f * k, c, style = Stroke(0.8f * k))
                drawCircle(PERSIANA.copy(alpha = 0.6f), 3.2f * k, c, style = Stroke(0.5f * k))
                drawCircle(PERSIANA.copy(alpha = 0.6f), 1f * k, c)
            }
            "recordatorio" -> {
                // Las tiras para arrancar, con su perforación.
                val y0 = r.bottom - 13f * k
                drawLine(tinta.copy(alpha = 0.35f), Offset(r.left + 2f * k, y0), Offset(r.right - 2f * k, y0), 0.6f * k, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.6f * k, 1.2f * k)))
                for (i in 1..9) {
                    val x = r.left + r.width * i / 10f
                    drawLine(tinta.copy(alpha = 0.3f), Offset(x, y0), Offset(x, r.bottom - 3f * k), 0.6f * k)
                }
            }
            else -> {}
        }
        if (e.fijada) {
            // Destacada: el filo del acento y una chincheta arriba al centro.
            drawPath(hoja, e.acento, style = Stroke(1.4f * density))
            val p = Offset(r.center.x, r.top + 5f * k)
            drawCircle(Color.Black.copy(alpha = 0.3f), 3.4f * k, Offset(p.x + 0.8f * k, p.y + 1.2f * k))
            drawCircle(e.acento, 3.4f * k, p)
            drawCircle(Color.White.copy(alpha = 0.5f), 1.1f * k, Offset(p.x - 1f * k, p.y - 1f * k))
        } else {
            drawPath(hoja, Color.Black.copy(alpha = 0.12f), style = Stroke(0.6f * k))
            // Dos tiras de cinta en las esquinas de arriba.
            val cinta = if (oscuro) CREMA.copy(alpha = 0.32f) else Color.White.copy(alpha = 0.55f)
            listOf(Offset(r.left + 5f * k, r.top + 2f * k) to -35f, Offset(r.right - 5f * k, r.top + 2f * k) to 35f).forEach { (p, g) ->
                rotate(g, p) {
                    drawRect(cinta, Offset(p.x - 7f * k, p.y - 2.5f * k), Size(14f * k, 5f * k))
                    drawRect(Color.Black.copy(alpha = 0.06f), Offset(p.x - 7f * k, p.y - 2.5f * k), Size(14f * k, 5f * k), style = Stroke(0.4f * k))
                }
            }
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) TINTA_NOCHE else TINTA
}
