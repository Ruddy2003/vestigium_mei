package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.TrazosNouveau
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt

/** Los colores del rincón del carnet de bocetos de Art nouveau (la libreta de momentos). */
internal object PaletaLibretaNouveau {
    /** La pared: el revoque pintado de rosa viejo, su luz y su sombra; el friso estarcido, algo más hondo. */
    val ROSA = Color(0xFFC79A8C)
    val ROSA_LUZ = Color(0xFFE6C2B2)
    val ROSA_OSC = Color(0xFF8F625C)
    val FRISO = Color(0xFFAA7A70)
    /** El nogal del biombo, de la mesa y del atril; el parqué encerado. */
    val NOGAL = Color(0xFF6E4C33)
    val NOGAL_LUZ = Color(0xFFA67D57)
    val NOGAL_OSC = Color(0xFF412B1C)
    val PARQUE = Color(0xFF5A3B27)
    /** La seda plisada del biombo: celadón; con la ventana detrás, crema dorada. El bordado, verde de tallo. */
    val SEDA = Color(0xFFC3CBA8)
    val SEDA_LUZ = Color(0xFFF4E8C2)
    val SEDA_OSC = Color(0xFF8B9675)
    val BORDADO = Color(0xFF56704C)
    /** El carnet: la tela verde del lomo y de las puntas, el papel del rótulo y de las hojas, la tinta, el filete y la cinta. */
    val TELA = Color(0xFF3D5A48)
    val TELA_LUZ = Color(0xFF6A8A6C)
    val PAPEL = Color(0xFFF2EBDA)
    val CANTO = Color(0xFFE3D6B8)
    val TINTA = Color(0xFF2A2530)
    val FILETE = Color(0xFF4B6A4E)
    val CINTA = Color(0xFF7E5FA6)
    val CINTA_LUZ = Color(0xFFB79CD8)
    /** El jaspeado de las tapas: el fondo de las venas y los colores de las piedras. */
    val VENA = Color(0xFF2E2742)
    val PIEDRAS = listOf(Color(0xFF8A6FAE), Color(0xFF94A684), Color(0xFFC89B58), Color(0xFFC48F86), Color(0xFFE3D3AA), Color(0xFF5D5A8A))
    /** El bronce, el vidrio de la pantalla y del florero, el charol de la caja de colores y su esmalte, el oro de los filetes. */
    val BRONCE = Color(0xFF8C6B3A)
    val BRONCE_LUZ = Color(0xFFDDBD7C)
    val BRONCE_OSC = Color(0xFF4A3418)
    val AMBAR = Color(0xFFE2AC5C)
    val VIDRIO_ROSA = Color(0xFFCF968E)
    val VIOLETA = Color(0xFF6B4E8C)
    val VIDRIO_VERDE = Color(0xFF7FA88A)
    val CHAROL = Color(0xFF221C22)
    val ESMALTE = Color(0xFFEDE8DC)
    val ORO = Color(0xFFC9A04C)
    /** Los colores de la caja de acuarelas: iris, salvia, ocre, rosa, azul de Prusia y carmín. */
    val TALLO = Color(0xFF8FA67E)
    val PASTILLAS = listOf(Color(0xFF7E5FA6), Color(0xFF8FA67E), Color(0xFFD9A14A), Color(0xFFD69A84), Color(0xFF2C4A6B), Color(0xFFB5533A))
    /** La noche: el ciruela hondo del cuarto, el azul hondo del lado de la ventana, la lámpara y la luna. */
    val HONDO = Color(0xFF24131F)
    val HONDO_FRIO = Color(0xFF151D2C)
    val LAMPARA = Color(0xFFF5D488)
    val LLAMA = Color(0xFFFFB85E)
    val FUEGO = Color(0xFFFFEDC0)
    val LUNA = Color(0xFFDCE4F2)
    val LUNA_RAS = Color(0xFFA8BCDA)
    /** Las letras: la del cartel en el nombre y la de leer en la cuenta. */
    val TITULO: FontFamily = LetraExtra.federant
    val TEXTO: FontFamily = LetraExtra.brygada
}

/**
 * La luz del rincón del carnet. De día entra por la ventana de la derecha, detrás del biombo ([d], de 0 a 1: lo lejos de ella
 * queda algo más hondo). De noche ([oscuro]) arde la lámpara colgada de la izquierda ([k], de 0 a 1: cerca de ella cada
 * color se dora; lejos se hunde en el ciruela del cuarto) y por la seda del biombo entra la luna ([f]: el lado frío).
 */
internal class AireLibretaNouveau(val oscuro: Boolean) {
    private val p = PaletaLibretaNouveau

    /** El color propio de algo, hundido en el ciruela de la noche (oscuro, de su mismo tono). */
    fun hundido(c: Color): Color = Color(c.red * 0.15f + p.HONDO.red * 0.82f, c.green * 0.15f + p.HONDO.green * 0.82f, c.blue * 0.15f + p.HONDO.blue * 0.82f, c.alpha)

    /** Bajo la lámpara: el azul baja más que el rojo y el color toma el tono dorado de la luz (no es un velo encima). */
    private fun dorado(c: Color): Color {
        val v = Color((c.red * 1.08f).coerceAtMost(1f), c.green * 0.9f, c.blue * 0.62f, c.alpha)
        val l = c.red * 0.3f + c.green * 0.59f + c.blue * 0.11f
        return mezcla(v, Color((l * 1.5f).coerceAtMost(1f), (l * 1.18f).coerceAtMost(1f), l * 0.6f, c.alpha), 0.3f)
    }

    /** A la luna: la luminosidad del color con el tono de la luna, baja (la luz es poca y fría). */
    private fun lunar(c: Color): Color {
        val l = c.red * 0.3f + c.green * 0.59f + c.blue * 0.11f
        return Color(l * 0.42f + p.HONDO_FRIO.red * 0.6f, l * 0.5f + p.HONDO_FRIO.green * 0.6f, (l * 0.68f + p.HONDO_FRIO.blue * 0.6f).coerceAtMost(1f), c.alpha)
    }

    /** El color [c] con la luz que le llega: [k] de la lámpara, [f] de la luna (de noche) y [d] del día. */
    fun luz(c: Color, k: Float, f: Float = 0f, d: Float = 0.6f): Color {
        if (!oscuro) {
            val x = d - 0.62f
            return if (x >= 0f) mezcla(c, luzMueble(1f), x * 0.45f) else mezcla(c, sombraMueble(1f), -x * 0.5f)
        }
        val kk = k.coerceIn(0f, 1f)
        var col = mezcla(hundido(c), dorado(c), (1.05f * kk).coerceAtMost(0.95f))
        val ff = (f * (1f - kk)).coerceIn(0f, 1f)
        if (ff > 0.001f) col = mezcla(col, lunar(c), 0.75f * ff)
        return col
    }

    /** El papel donde va la tinta: de noche recoge la luz de la lámpara y no baja tanto (la tinta se sigue leyendo). */
    fun papel(c: Color, k: Float, d: Float = 0.6f): Color {
        if (!oscuro) return luz(c, k, 0f, d)
        val kk = (0.42f + 0.58f * sqrt(k.coerceIn(0f, 1f))).coerceAtMost(1f)
        return mezcla(hundido(c), dorado(c), kk * 0.86f)
    }

    /** La sombra: de día, violeta hondo; de noche, el ciruela del cuarto. */
    fun sombra(a: Float): Color = if (oscuro) p.HONDO.copy(alpha = (a * 1.2f).coerceIn(0f, 1f)) else sombraMueble(a)
    /** La luz crema de lo que mira a la ventana, de día; de noche no hay. */
    fun claro(a: Float): Color = if (oscuro) Color.Transparent else luzMueble(a)
    /** El filo de la lámpara en lo que la mira, de noche, según [k]. */
    fun filo(a: Float, k: Float): Color = if (oscuro) p.LLAMA.copy(alpha = (a * k).coerceIn(0f, 1f)) else Color.Transparent
    /** El filo frío de la luna, de noche, según [f]. */
    fun fria(a: Float, f: Float): Color = if (oscuro) p.LUNA_RAS.copy(alpha = (a * f).coerceIn(0f, 1f)) else Color.Transparent
}

/**
 * La libreta de momentos de Art nouveau (la usa `EstanteNouveau`, en `BibliotecaNouveau.kt`): el carnet de bocetos del
 * dibujante del taller de litografía, en su rincón del entresuelo, la misma casa de París hacia 1900 que el estante de caoba y
 * la cajonera de roble, con otra madera (nogal) y otra luz. La pared de revoque rosa viejo con su friso estarcido de tallos y
 * bayas y la moldura de colgar cuadros; a la derecha, el biombo de nogal de tres hojas con la seda plisada y bordada, delante
 * de la ventana; a la izquierda, la lámpara de vidrio colgada del techo, una campana de pétalos que va del ámbar al violeta.
 * En la mesa de trabajo de nogal, con su faldón tallado y sus patas en látigo, el atril de mesa sostiene el carnet: las tapas
 * de cartón jaspeado (de piedras, como el papel de guardas), el lomo y las puntas de tela verde, el rótulo de papel con el
 * nombre y la cuenta, el canto de las hojas que engorda con ellas, alguna hoja suelta que asoma y la cinta de seda atada en
 * lazo. Al lado, lo que se usa: la caja de acuarelas abierta con su pincel, el vaso del agua y, en un florero de vidrio, la flor
 * que se está dibujando (la del día). En la repisa del atril, el lápiz y el carboncillo.
 *
 * De día la luz entra por la ventana de detrás del biombo (la seda brilla y las sombras caen a la izquierda). De noche arde la
 * lámpara: su charco dora la mesa, la caja y la mitad del carnet, y lo lejano se hunde en el ciruela del cuarto; por la seda
 * del biombo entra la luna, fría. Lo fijo se pinta una vez por tamaño y modo en una imagen guardada; encima, cada vez, lo que
 * cambia con las hojas (el canto, las sueltas, el lazo, el nombre y la cuenta). Al abrirse, la tapa gira sobre la bisagra del
 * lomo y deja ver el primer boceto.
 */
internal object MuebleLibretaNouveau {
    private val P = PaletaLibretaNouveau
    private val CLARO = AireLibretaNouveau(false)
    private val OSCURO = AireLibretaNouveau(true)

    val ALTO: Dp = 262.dp

    private var plano: PlanoLibretaNouveau? = null
    private fun planoDe(w: Float, h: Float, u: Float): PlanoLibretaNouveau =
        plano?.takeIf { it.w == w && it.h == h && it.u == u } ?: PlanoLibretaNouveau(w, h, u).also { plano = it }

    /** La imagen guardada de lo fijo: una por modo (claro y oscuro), del tamaño del lienzo. */
    private val claves = arrayOfNulls<String>(2)
    private val imagenes = arrayOfNulls<ImageBitmap>(2)

    private fun DrawScope.fijo(ranura: Int, clave: String, pintar: DrawScope.() -> Unit): ImageBitmap? {
        val ancho = ceil(size.width).toInt(); val alto = ceil(size.height).toInt()
        if (ancho < 1 || alto < 1) return null
        imagenes[ranura]?.let { if (claves[ranura] == clave) return it }
        val nueva = ImageBitmap(ancho, alto)
        CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(nueva), Size(ancho.toFloat(), alto.toFloat())) { pintar() }
        imagenes[ranura] = nueva; claves[ranura] = clave
        return nueva
    }

    // ---------------------------------------------------------------- la escena

    fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 8f || h < 8f) return
        val pl = planoDe(w, h, u)
        val a = if (oscuro) OSCURO else CLARO
        val flor = FloresNouveau.tipo(e.semilla)
        val img = fijo(if (oscuro) 1 else 0, "$w×$h·$u·$flor") { fondo(pl, a, flor) } ?: return
        drawImage(img)
        val ab = e.abierto.coerceIn(0f, 1f)
        val lleno = (sqrt(e.hojas.coerceAtLeast(0).toFloat()) / 13f).coerceIn(0f, 1f)
        val g = h * 0.014f + (pl.gruesoMax - h * 0.014f) * lleno
        canto(pl, a, g, e.hojas)
        sueltas(pl, a, g, e)
        if (ab > 0.01f) abierto(pl, a, img, ab, e, medidor) else {
            lazo(pl, a, g)
            rotulo(pl, a, e, medidor, 1f)
        }
    }

    /** Lo fijo del rincón, en el orden en que se tapan: la pared, el suelo, el biombo, la mesa, el atril con la tapa, la lámpara. */
    private fun DrawScope.fondo(pl: PlanoLibretaNouveau, a: AireLibretaNouveau, flor: Int) {
        with(RinconLibretaNouveau) {
            muro(pl, a)
            suelo(pl, a)
            luzDeLampara(pl, a)
            biombo(pl, a)
        }
        with(PiezasLibretaNouveau) { sombraDelCarnet(pl, a) }
        with(RinconLibretaNouveau) { mesa(pl, a) }
        with(PiezasLibretaNouveau) {
            florero(pl, a, flor)
            atril(pl, a)
        }
        tapaCarnet(pl, a)
        with(PiezasLibretaNouveau) {
            repisa(pl, a)
            caja(pl, a)
            pl.vaso?.let { vaso(pl, a, it) }
            lampara(pl, a)
        }
        granoMueble(Rect(0f, 0f, size.width, size.height), a.oscuro)
    }

    // ---------------------------------------------------------------- la tapa del carnet (fija)

    /**
     * La tapa del carnet: el cartón jaspeado de piedras (violeta de iris, salvia, ocre, rosa y crema sobre las venas oscuras),
     * el lomo de tela verde con la canal de la bisagra, las dos puntas de tela, los cantos del cartón con su luz y el rótulo de
     * papel pegado, con su filete verde impreso y sus hojitas en las esquinas (el nombre y la cuenta van encima, cada vez).
     */
    private fun DrawScope.tapaCarnet(pl: PlanoLibretaNouveau, a: AireLibretaNouveau) {
        val u = pl.u
        val t = pl.tapa; val lomo = pl.lomo
        jaspeado(Rect(lomo.right - 1f, t.top, t.right, t.bottom), pl, a, 4127)
        // Las puntas de tela, arriba y abajo a la derecha.
        val punta = pl.cw * 0.11f
        val kp = pl.k(t.topRight); val fp = pl.f(t.topRight)
        val tela = a.luz(P.TELA, kp, fp, pl.d(t.topRight))
        drawPath(poligonoNouveau(t.right - punta, t.top, t.right, t.top, t.right, t.top + punta), tela)
        drawPath(poligonoNouveau(t.right - punta, t.bottom, t.right, t.bottom, t.right, t.bottom - punta), tela)
        drawLine(a.sombra(0.35f), Offset(t.right - punta, t.top), Offset(t.right, t.top + punta), 1f * u)
        drawLine(a.sombra(0.35f), Offset(t.right - punta, t.bottom), Offset(t.right, t.bottom - punta), 1f * u)
        // El lomo de tela, con su trama y la canal de la bisagra.
        val kl = pl.k(lomo.center); val fl = pl.f(lomo.center)
        val telaL = a.luz(P.TELA, kl, fl, pl.d(lomo.center))
        drawRect(telaL, lomo.topLeft, lomo.size)
        val trama = Path()
        var y = lomo.top + 1.5f * u
        while (y < lomo.bottom) { trama.moveTo(lomo.left, y); trama.lineTo(lomo.right, y + lomo.width * 0.35f); y += 2.2f * u }
        clipRect(lomo.left, lomo.top, lomo.right, lomo.bottom) {
            drawPath(trama, a.luz(P.TELA_LUZ, kl, fl).copy(alpha = 0.22f), style = Stroke(0.6f * u))
        }
        val canal = lomo.left + lomo.width * 0.3f
        drawLine(a.sombra(0.45f), Offset(canal, lomo.top + 1f * u), Offset(canal, lomo.bottom - 1f * u), 1.6f * u)
        drawLine(if (a.oscuro) a.filo(0.5f, kl) else a.claro(0.4f), Offset(canal + 1.4f * u, lomo.top + 1f * u), Offset(canal + 1.4f * u, lomo.bottom - 1f * u), 0.8f * u)
        // El borde del papel jaspeado, montado sobre la tela: su sombrita.
        drawLine(a.sombra(0.4f), Offset(lomo.right, t.top), Offset(lomo.right, t.bottom), 1.2f * u)
        drawLine(a.luz(P.PIEDRAS[4], kl, fl).copy(alpha = 0.5f), Offset(lomo.right + 1f * u, t.top), Offset(lomo.right + 1f * u, t.bottom), 0.6f * u)
        // Los cantos del cartón: de día, la luz arriba y a la derecha; de noche, la lámpara en el de la izquierda y la luna en el de la derecha.
        drawLine(a.claro(0.55f), Offset(t.left + 2f * u, t.top + 0.6f * u), Offset(t.right - 1f * u, t.top + 0.6f * u), 1.2f * u)
        drawLine(a.claro(0.45f), Offset(t.right - 0.6f * u, t.top + 1f * u), Offset(t.right - 0.6f * u, t.bottom - 1f * u), 1.2f * u)
        drawLine(a.filo(0.75f, pl.k(Offset(t.left, t.center.y))), Offset(t.left + 0.6f * u, t.top + 1f * u), Offset(t.left + 0.6f * u, t.bottom - 1f * u), 1.2f * u)
        drawLine(a.filo(0.6f, pl.k(t.topCenter)), Offset(t.left + 1f * u, t.top + 0.6f * u), Offset(t.center.x, t.top + 0.6f * u), 1f * u)
        drawLine(a.fria(0.5f, pl.f(t.centerRight)), Offset(t.right - 0.6f * u, t.top + 1f * u), Offset(t.right - 0.6f * u, t.bottom - 1f * u), 1f * u)
        drawLine(a.sombra(0.4f), Offset(t.left, t.bottom - 0.8f * u), Offset(t.right, t.bottom - 0.8f * u), 1.6f * u)
        etiqueta(pl, a)
    }

    /**
     * El jaspeado de piedras en [r]: sobre el fondo de las venas, piedras redondeadas que se empujan unas a otras, cada una con
     * su anillo más hondo, su centro y su vena fina; las primeras grandes y las últimas chicas, entre las otras. Cada piedra toma
     * la luz del sitio donde está.
     */
    private fun DrawScope.jaspeado(r: Rect, pl: PlanoLibretaNouveau, a: AireLibretaNouveau, semilla: Int) {
        val u = pl.u
        clipRect(r.left, r.top, r.right, r.bottom) {
            val vena = a.luz(P.VENA, pl.k(r.center), pl.f(r.center), pl.d(r.center))
            drawRect(vena, r.topLeft, r.size)
            val r0 = pl.h * 0.027f
            val n = ((r.width + 2f * r0) * (r.height + 2f * r0) / (r0 * r0) * 0.85f).toInt().coerceIn(20, 700)
            for (i in 0 until n) {
                val c = Offset(r.left - r0 + (r.width + 2f * r0) * azar(semilla, i), r.top - r0 + (r.height + 2f * r0) * azar(semilla + 1, i))
                val rr = r0 * (0.6f + 0.75f * azar(semilla + 2, i)) * (1f - 0.4f * i / n)
                val q = (azar(semilla + 3, i) * 10f).toInt()
                val tono = P.PIEDRAS[when (q) { 0, 1, 2 -> 0; 3, 4 -> 1; 5 -> 2; 6, 7 -> 3; 8 -> 4; else -> 5 }]
                val col = a.luz(tono, pl.k(c), pl.f(c), pl.d(c))
                val piedra = blando(c, rr, 1f, semilla + i * 3)
                drawPath(piedra, mezcla(col, vena, 0.32f))
                drawPath(blando(c + Offset(0f, -rr * 0.06f), rr, 0.72f, semilla + i * 3), col)
                drawPath(piedra, vena, style = Stroke(0.75f * u))
                // Las motas de la piedra.
                if (azar(semilla + 4, i) > 0.6f) drawCircle(mezcla(col, vena, 0.4f), rr * 0.1f, c + Offset(rr * 0.25f, rr * 0.12f))
            }
        }
    }

    /** Una piedra del jaspeado: un contorno redondeado e irregular (curvas por los puntos medios), escalado por [f]. */
    private fun blando(c: Offset, rr: Float, f: Float, semilla: Int): Path {
        val m = 9
        val xs = FloatArray(m); val ys = FloatArray(m)
        val giro = azar(semilla, 97) * 6.2832f
        for (j in 0 until m) {
            val ang = 6.2832f * j / m + giro
            val rad = rr * f * (0.82f + 0.36f * azar(semilla, j))
            xs[j] = c.x + rad * cos(ang) * 1.12f; ys[j] = c.y + rad * sin(ang) * 0.9f
        }
        return Path().apply {
            moveTo((xs[m - 1] + xs[0]) / 2f, (ys[m - 1] + ys[0]) / 2f)
            for (j in 0 until m) { val n = (j + 1) % m; quadraticTo(xs[j], ys[j], (xs[j] + xs[n]) / 2f, (ys[j] + ys[n]) / 2f) }
            close()
        }
    }

    /** El rótulo de papel pegado: su sombrita, el papel, el filete impreso doble y una hojita en cada esquina. */
    private fun DrawScope.etiqueta(pl: PlanoLibretaNouveau, a: AireLibretaNouveau) {
        val u = pl.u
        val r = pl.etiqueta
        val k = pl.k(r.center)
        val lado = if (a.oscuro) 1f else -1f
        drawRect(a.sombra(0.45f), r.topLeft + Offset(lado * 1.2f * u, 1.4f * u), r.size)
        drawRect(a.papel(P.PAPEL, k, pl.d(r.center)), r.topLeft, r.size)
        // El papel se tuesta un poco hacia los bordes.
        drawRect(a.luz(P.CANTO, k, 0f, pl.d(r.center)).copy(alpha = 0.6f), r.topLeft, r.size, style = Stroke(2.2f * u))
        val filete = a.luz(P.FILETE, maxOf(k, 0.5f))
        val f1 = Rect(r.left + 3.4f * u, r.top + 3.4f * u, r.right - 3.4f * u, r.bottom - 3.4f * u)
        drawRect(filete, f1.topLeft, f1.size, style = Stroke(0.9f * u))
        val f2 = Rect(f1.left + 2f * u, f1.top + 2f * u, f1.right - 2f * u, f1.bottom - 2f * u)
        drawRect(filete.copy(alpha = 0.7f), f2.topLeft, f2.size, style = Stroke(0.5f * u))
        listOf(f1.topLeft to Offset(1f, 1f), f1.topRight to Offset(-1f, 1f), f1.bottomLeft to Offset(1f, -1f), f1.bottomRight to Offset(-1f, -1f)).forEach { (q, s) ->
            drawPath(TrazosNouveau.hojaLanza(q + Offset(s.x * 2.4f * u, s.y * 2.4f * u), q + Offset(s.x * 9f * u, s.y * 6.5f * u), 1.8f * u, 0.18f * s.x * s.y), filete)
        }
    }

    // ---------------------------------------------------------------- lo que cambia con las hojas

    /**
     * El grueso del carnet: la cara de arriba y el canto de la derecha (las hojas, crema, con sus líneas, más cuantas más hojas),
     * la cabeza del lomo de tela a la izquierda y la tapa de atrás, que asoma por detrás.
     */
    private fun DrawScope.canto(pl: PlanoLibretaNouveau, a: AireLibretaNouveau, g: Float, hojas: Int) {
        val u = pl.u
        val t = pl.tapa
        val o = Offset(g * 0.72f, -g * 0.56f)
        val kA = pl.k(t.topCenter); val kD = pl.k(t.centerRight); val fD = pl.f(t.centerRight)
        // La tapa de atrás, un filo más afuera que las hojas.
        val atras = a.luz(P.TELA, kD, fD, 0.4f)
        drawPath(poligonoNouveau(t.left + o.x, t.top + o.y - 1.2f * u, t.right + o.x + 1.2f * u, t.top + o.y - 1.2f * u, t.right + o.x + 1.2f * u, t.bottom + o.y,
            t.right + o.x, t.bottom + o.y, t.right + o.x, t.top + o.y, t.left + o.x, t.top + o.y), atras)
        // Las hojas: arriba, de día la luz de la ventana; de noche, lo que alcanza la lámpara. A la derecha, el canto de cara a la ventana.
        val arriba = a.papel(P.CANTO, kA * 0.85f, 0.85f)
        val derecha = if (a.oscuro) a.luz(P.CANTO, kD, fD) else a.luz(P.CANTO, 0f, 0f, 0.74f)
        drawPath(poligonoNouveau(t.left, t.top, t.right, t.top, t.right + o.x, t.top + o.y, t.left + o.x, t.top + o.y), arriba)
        drawPath(poligonoNouveau(t.right, t.top, t.right + o.x, t.top + o.y, t.right + o.x, t.bottom + o.y, t.right, t.bottom), derecha)
        // Las líneas de las hojas.
        val n = (hojas / 3).coerceIn(2, 16)
        val lineas = Path()
        for (i in 1..n) {
            val fi = i / (n + 1f) + (azar(hojas, i) - 0.5f) * 0.3f / (n + 1f)
            lineas.moveTo(t.left + pl.lomo.width * 0.4f + o.x * fi, t.top + o.y * fi); lineas.lineTo(t.right + o.x * fi, t.top + o.y * fi)
            lineas.lineTo(t.right + o.x * fi, t.bottom + o.y * fi)
        }
        drawPath(lineas, a.sombra(0.22f), style = Stroke(0.5f * u))
        // La cabeza del lomo: la tela que envuelve las hojas a la izquierda.
        val kl = pl.k(pl.lomo.topCenter)
        drawPath(poligonoNouveau(t.left, t.top, t.left + pl.lomo.width * 0.4f, t.top, t.left + pl.lomo.width * 0.4f + o.x, t.top + o.y, t.left + o.x, t.top + o.y),
            a.luz(P.TELA, kl, 0f, 0.7f))
        drawLine(a.filo(0.5f, kl), Offset(t.left, t.top), Offset(t.left + o.x, t.top + o.y), 1f * u)
        drawLine(a.fria(0.45f, fD), Offset(t.right + o.x, t.top + o.y), Offset(t.right + o.x, t.bottom + o.y), 1f * u)
        drawLine(a.sombra(0.3f), Offset(t.right, t.top), Offset(t.right, t.bottom), 0.8f * u)
    }

    /**
     * Las hojas sueltas que asoman (ninguna con una sola hoja; hasta tres con muchas): por arriba y por el canto, cada una con la
     * aguada de un color del boceto cerca de su orilla.
     */
    private fun DrawScope.sueltas(pl: PlanoLibretaNouveau, a: AireLibretaNouveau, g: Float, e: EstadoLibreta) {
        val n = when { e.hojas <= 1 -> 0; e.hojas < 8 -> 1; e.hojas < 30 -> 2; else -> 3 }
        if (n == 0) return
        val u = pl.u
        val t = pl.tapa
        val o = Offset(g * 0.72f, -g * 0.56f)
        val s = 9173
        for (i in 0 until n) {
            val fi = 0.3f + 0.45f * azar(s, i)
            val arriba = i % 2 == 0
            val col = a.papel(P.PAPEL, pl.k(t.topCenter) * 0.9f, 0.8f)
            val aguada = a.luz(P.PASTILLAS[(i * 2 + e.hojas) % 4], pl.k(t.topCenter), pl.f(t.topRight)).copy(alpha = 0.55f)
            if (arriba) {
                val x0 = t.left + pl.cw * ((if (i == 0) 0.6f else 0.18f) + 0.1f * azar(s + 1, i)) + o.x * fi
                val y0 = t.top + o.y * fi
                val ancho = pl.cw * (0.16f + 0.12f * azar(s + 2, i)); val alto = pl.h * (0.022f + 0.02f * azar(s + 3, i)) + g * 0.3f
                val inc = (azar(s + 4, i) - 0.5f) * alto * 0.6f
                val hoja = poligonoNouveau(x0, y0, x0 + ancho, y0, x0 + ancho + inc, y0 - alto + inc * 0.3f, x0 + inc, y0 - alto - inc * 0.3f)
                drawPath(hoja, col)
                clipRect(x0 - ancho, y0 - alto * 2f, x0 + ancho * 2f, y0) {
                    drawOval(aguada, Offset(x0 + ancho * 0.3f + inc, y0 - alto * 0.95f), Size(ancho * 0.45f, alto * 0.6f))
                }
                drawPath(hoja, a.sombra(0.3f), style = Stroke(0.6f * u))
            } else {
                val x0 = t.right + o.x * fi
                val y0 = t.top + pl.ch * (0.35f + 0.4f * azar(s + 1, i)) + o.y * fi
                val alto = pl.ch * (0.18f + 0.12f * azar(s + 2, i)); val sale = pl.h * (0.018f + 0.014f * azar(s + 3, i)) + g * 0.3f
                val hoja = poligonoNouveau(x0, y0, x0 + sale, y0 - alto * 0.08f, x0 + sale, y0 + alto * 0.92f, x0, y0 + alto)
                drawPath(hoja, if (a.oscuro) a.luz(P.PAPEL, pl.k(t.centerRight), pl.f(t.centerRight)) else col)
                clipRect(x0, y0 - alto, x0 + sale * 2f, y0 + alto * 2f) {
                    drawOval(aguada, Offset(x0 + sale * 0.4f, y0 + alto * 0.3f), Size(sale * 0.8f, alto * 0.4f))
                }
                drawPath(hoja, a.sombra(0.3f), style = Stroke(0.6f * u))
            }
        }
    }

    /**
     * La cinta de seda que cierra el carnet, atada en lazo en el canto de la derecha: el cabo que sale de debajo del jaspeado,
     * las dos lazadas, el nudo y las dos puntas cortadas en cola de golondrina que cuelgan por el canto.
     */
    private fun DrawScope.lazo(pl: PlanoLibretaNouveau, a: AireLibretaNouveau, g: Float) {
        val u = pl.u
        val t = pl.tapa
        val o = Offset(g * 0.72f, -g * 0.56f)
        val nudo = Offset(t.right + o.x * 0.45f, t.top + pl.ch * 0.5f + o.y * 0.45f)
        val k = pl.k(nudo); val f = pl.f(nudo)
        val cinta = a.luz(P.CINTA, k, f, 0.8f)
        val luz = if (a.oscuro) mezcla(cinta, P.LUNA_RAS, 0.35f * f) else a.luz(P.CINTA_LUZ, k, f, 0.8f)
        val ancho = pl.h * 0.02f
        // El cabo sobre la tapa, desde debajo del jaspeado.
        val desde = Offset(t.right - pl.cw * 0.07f, nudo.y + 1f * u)
        drawLine(a.sombra(0.3f), desde + Offset(-1f * u, 1.6f * u), nudo + Offset(-1f * u, 1.6f * u), ancho)
        drawLine(cinta, desde, nudo, ancho)
        drawLine(luz.copy(alpha = 0.6f), desde + Offset(0f, -ancho * 0.3f), nudo + Offset(0f, -ancho * 0.3f), ancho * 0.25f)
        // Las dos lazadas, una hacia arriba y otra hacia abajo a la derecha.
        val l = pl.h * 0.064f
        listOf(Offset(-0.35f, -1f), Offset(0.75f, -0.55f)).forEachIndexed { i, dir ->
            val punta = nudo + Offset(dir.x * l, dir.y * l)
            val nx = -dir.y; val ny = dir.x
            val lazada = Path().apply {
                moveTo(nudo.x, nudo.y)
                cubicTo(nudo.x + (dir.x * 0.4f + nx * 0.55f) * l, nudo.y + (dir.y * 0.4f + ny * 0.55f) * l, punta.x + nx * 0.4f * l, punta.y + ny * 0.4f * l, punta.x, punta.y)
                cubicTo(punta.x - nx * 0.4f * l, punta.y - ny * 0.4f * l, nudo.x + (dir.x * 0.4f - nx * 0.55f) * l, nudo.y + (dir.y * 0.4f - ny * 0.55f) * l, nudo.x, nudo.y)
                close()
            }
            withTransform({ translate(1.2f * u, 1.8f * u) }) { drawPath(lazada, a.sombra(0.3f)) }
            drawPath(lazada, if (i == 0) cinta else mezcla(cinta, a.sombra(1f), 0.12f))
            drawPath(lazada, luz.copy(alpha = 0.55f), style = Stroke(0.8f * u))
            drawPath(lazada, a.sombra(0.35f), style = Stroke(0.5f * u))
        }
        // Las dos puntas que cuelgan, con su corte en cola de golondrina.
        listOf(Offset(-0.12f, 1f), Offset(0.32f, 0.92f)).forEach { dir ->
            val fin = nudo + Offset(dir.x * l * 1.5f, dir.y * l * 1.5f)
            val med = ancho * 0.5f
            val punta = Path().apply {
                moveTo(nudo.x - med, nudo.y)
                quadraticTo(nudo.x + dir.x * l * 0.6f - med, nudo.y + l * 0.7f, fin.x - med, fin.y)
                lineTo(fin.x, fin.y - med * 1.2f); lineTo(fin.x + med, fin.y)
                quadraticTo(nudo.x + dir.x * l * 0.6f + med, nudo.y + l * 0.7f, nudo.x + med, nudo.y)
                close()
            }
            withTransform({ translate(1.2f * u, 1.6f * u) }) { drawPath(punta, a.sombra(0.28f)) }
            drawPath(punta, cinta)
            drawPath(punta, luz.copy(alpha = 0.4f), style = Stroke(0.6f * u))
        }
        drawCircle(cinta, ancho * 0.62f, nudo)
        drawCircle(luz.copy(alpha = 0.6f), ancho * 0.25f, nudo + Offset(-ancho * 0.2f, -ancho * 0.2f))
        drawCircle(a.sombra(0.4f), ancho * 0.62f, nudo, style = Stroke(0.6f * u))
    }

    /** El nombre y la cuenta en el rótulo, a tinta, con el filete chico entre los dos ([alfa]: al abrirse se desvanecen). */
    private fun DrawScope.rotulo(pl: PlanoLibretaNouveau, a: AireLibretaNouveau, e: EstadoLibreta, medidor: TextMeasurer, alfa: Float) {
        if (alfa < 0.01f) return
        val u = pl.u
        val r = pl.etiqueta
        val tinta = (if (a.oscuro) mezcla(P.TINTA, P.HONDO, 0.25f) else P.TINTA).copy(alpha = alfa)
        val dentro = Rect(r.left + 7f * u, r.top + 5f * u, r.right - 7f * u, r.bottom - 5f * u)
        textoEn(medidor, e.nombre, Rect(dentro.left, dentro.top, dentro.right, dentro.top + dentro.height * 0.5f), P.TITULO, tinta, 18f, minimo = 8f)
        val yf = dentro.top + dentro.height * 0.53f
        drawLine(a.luz(P.FILETE, 0.6f).copy(alpha = 0.8f * alfa), Offset(dentro.center.x - dentro.width * 0.18f, yf), Offset(dentro.center.x + dentro.width * 0.18f, yf), 0.8f * u)
        drawCircle(a.luz(P.FILETE, 0.6f).copy(alpha = alfa), 1.3f * u, Offset(dentro.center.x, yf))
        val i = e.cuenta.indexOf(" · ")
        val tenue = tinta.copy(alpha = 0.86f * alfa)
        if (i > 0) {
            val mitad = dentro.top + dentro.height * 0.78f
            textoEn(medidor, e.cuenta.substring(0, i).trim(), Rect(dentro.left, dentro.top + dentro.height * 0.58f, dentro.right, mitad), P.TEXTO, tenue, 10.5f, minimo = 6f)
            textoEn(medidor, e.cuenta.substring(i + 3).trim(), Rect(dentro.left, mitad, dentro.right, dentro.bottom), P.TEXTO, tenue, 9.5f, minimo = 6f)
        } else textoEn(medidor, e.cuenta, Rect(dentro.left, dentro.top + dentro.height * 0.6f, dentro.right, dentro.bottom), P.TEXTO, tenue, 10.5f, minimo = 6f)
    }

    // ---------------------------------------------------------------- al abrirse

    /**
     * El carnet que se abre: debajo de la tapa, el primer boceto (la flor del día a lápiz, con su aguada); la tapa gira sobre la
     * bisagra del lomo (la misma imagen guardada, cada vez más angosta y más en sombra) y el nombre se desvanece. La repisa del
     * atril, que va delante, se vuelve a pegar encima.
     */
    private fun DrawScope.abierto(pl: PlanoLibretaNouveau, a: AireLibretaNouveau, img: ImageBitmap, ab: Float, e: EstadoLibreta, medidor: TextMeasurer) {
        val u = pl.u
        val t = pl.tapa
        val hoja = Rect(pl.lomo.right, t.top + 1f * u, t.right - 1.5f * u, pl.labio)
        val k = pl.k(hoja.center)
        drawRect(a.papel(P.PAPEL, k, 0.75f), hoja.topLeft, hoja.size)
        // El boceto: la flor del día a lápiz (solo el trazo) con sus aguadas, la flor y las hojas, un poco corridas.
        val p = FloresNouveau.partes(FloresNouveau.tipo(e.semilla))
        val c = Offset(hoja.center.x + hoja.width * 0.06f, hoja.center.y + hoja.height * 0.04f)
        val r = hoja.height * 0.4f
        val grafito = a.luz(Color(0xFF4A4650), k).copy(alpha = 0.75f)
        withTransform({ translate(c.x, c.y); scale(r, r, Offset.Zero) }) {
            withTransform({ translate(0.05f, 0.04f) }) {
                drawPath(p.hojas, a.luz(P.TALLO, k).copy(alpha = 0.4f))
                drawPath(p.flor, a.luz(P.PASTILLAS[0], k).copy(alpha = 0.36f))
                drawPath(p.frente, a.luz(P.PASTILLAS[3], k).copy(alpha = 0.3f))
            }
            drawPath(p.tallo, grafito, style = Stroke(0.022f, cap = StrokeCap.Round))
            drawPath(p.hojas, grafito, style = Stroke(0.016f))
            drawPath(p.flor, grafito, style = Stroke(0.016f))
            drawPath(p.frente, grafito, style = Stroke(0.016f))
            drawPath(p.pliegues, grafito.copy(alpha = 0.5f), style = Stroke(0.012f))
        }
        drawRect(a.sombra(0.3f), hoja.topLeft, Size(4f * u, hoja.height))
        // La tapa que gira sobre la bisagra.
        val x0 = floor(pl.lomo.right).toInt(); val y0 = floor(t.top - 1f).toInt()
        val x1 = ceil(t.right + 1f).toInt(); val y1 = floor(pl.labio).toInt()
        if (x1 > x0 && y1 > y0) {
            val esc = 1f - 0.92f * ab
            withTransform({ scale(esc, 1f, Offset(x0.toFloat(), 0f)) }) {
                drawImage(img, IntOffset(x0, y0), IntSize(x1 - x0, y1 - y0), IntOffset(x0, y0), IntSize(x1 - x0, y1 - y0))
                rotulo(pl, a, e, medidor, (1f - ab * 3f).coerceIn(0f, 1f))
                drawRect(a.sombra(0.55f * ab), Offset(x0.toFloat(), y0.toFloat()), Size((x1 - x0).toFloat(), (y1 - y0).toFloat()))
            }
        }
        // La repisa del atril, delante.
        val r0 = pl.repisa
        val ry0 = floor(pl.labio - 1f).toInt(); val ry1 = ceil(r0.bottom + 1f).toInt()
        val rx0 = floor(r0.left).toInt(); val rx1 = ceil(r0.right).toInt()
        if (rx1 > rx0 && ry1 > ry0) drawImage(img, IntOffset(rx0, ry0), IntSize(rx1 - rx0, ry1 - ry0), IntOffset(rx0, ry0), IntSize(rx1 - rx0, ry1 - ry0))
    }
}
