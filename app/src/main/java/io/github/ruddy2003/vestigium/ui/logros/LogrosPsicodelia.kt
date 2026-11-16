package io.github.ruddy2003.vestigium.ui.logros

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Cara
import io.github.ruddy2003.vestigium.ui.ColoresPsi
import io.github.ruddy2003.vestigium.ui.EstadoTema
import io.github.ruddy2003.vestigium.ui.PiezasPsicodelia
import io.github.ruddy2003.vestigium.ui.TrazosCiudad
import io.github.ruddy2003.vestigium.ui.TrazosTinta
import io.github.ruddy2003.vestigium.ui.TrazosTinta.conRegistro
import io.github.ruddy2003.vestigium.ui.TrazosTinta.trama
import io.github.ruddy2003.vestigium.ui.ideario.azar
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

// =====================================================================
//  Psicodelia (versión 2: la ciudad y la tinta)
// =====================================================================

/*
 * Psicodelia v2, tanda 4 (`docs/diseno/temas/psicodelia.md`, «Los motivos de las figuras»; `psicodelia_base.md`): los logros
 * y los trofeos en las dos caras. Cada pieza tiene una sola geometría y dos pintores, como los muebles:
 * - la **ciudad** (cara BASE): luz de la tarde por la izquierda (de noche, la lámpara de pantalla), sombra corta de color
 *   café, los colores apagados de la foto de los 70 y el grano de película encima;
 * - la **tinta** (cara INTENSA): el cartel de la semana impreso, tintas planas (magenta y ultramar; con la intensidad,
 *   también el bermellón), el contorno y la trama de la tinta que graba en el lado de la sombra, el registro que se corre y,
 *   detrás de cada trofeo, los rayos que ondulan del cartel.
 * Todo es puro `DrawScope` (entra en el arnés de escritorio: `dibujar.sh logros psicodelia …`). La cara y la intensidad se
 * leen de [EstadoTema] (estado de instantánea: el dibujo se rehace solo cuando cambian). Sin flores, sin el símbolo de la
 * paz, sin arcoíris, sin furgonetas ni lámparas de cera: objetos con uso de la ciudad de David.
 */

/** Cómo se imprime una pieza en la cara de la tinta: el papel, la tinta que graba, una de las tres tintas o el metal del nivel. */
internal enum class TonoPsi { PAPEL, GRABAR, MAGENTA, BERMELLON, ULTRAMAR, METAL }

/**
 * Lo que necesita un trofeo de Psicodelia: si ya se ganó, su nivel (1 a 4), los tres tonos del metal de ese nivel, el color
 * de la silueta de lo que aún no se gana, si la pantalla es oscura, y la cara y la intensidad (por omisión, las de
 * [EstadoTema]).
 */
internal class TrofeoPsi(
    val hecho: Boolean, val nivel: Int, val metalOscuro: Color, val metal: Color, val brillo: Color, val silueta: Color, val oscuro: Boolean,
    val cara: Cara = EstadoTema.cara, val i: Float = EstadoTema.intensidad,
)

object LogrosPsicodelia : DisenoLogros {

    // ---------------------------------------------------------------- colores de la ciudad (los de la foto de los 70)

    private val VINILO = Color(0xFF1E1916)
    private val CARTON = Color(0xFFE2D2B0)
    private val PINO = Color(0xFFB48A5A)
    private val NARANJA = Color(0xFF9C4219)
    private val BOTELLA = Color(0xFF3D5838)
    private val MOSTAZA = Color(0xFFA77A2A)
    private val TINTA_FRASCO = Color(0xFF2B2A36)
    private val TELA = Color(0xFF8C7A5E)
    private val CAFE = Color(0xFF2A2119)

    private fun k(oscuro: Boolean, cara: Cara = EstadoTema.cara, i: Float = EstadoTema.intensidad): ColoresPsi = PiezasPsicodelia.colores(oscuro, cara, i)

    /** La tinta de cada [TonoPsi] en la variante [k]. El bermellón solo aparece con la intensidad: antes es magenta. */
    private fun tinta(tono: TonoPsi, k: ColoresPsi, metal: Color): Color = when (tono) {
        TonoPsi.PAPEL -> if (k.oscuro) TrazosTinta.CREMA else lerp(TrazosTinta.PAPEL, Color.White, 0.45f)
        TonoPsi.GRABAR -> if (k.oscuro) Color(0xFF26203A) else TrazosTinta.GRABAR
        TonoPsi.MAGENTA -> k.magenta
        TonoPsi.BERMELLON -> lerp(k.magenta, k.bermellon, k.m.bermellon)
        TonoPsi.ULTRAMAR -> k.ultramar
        TonoPsi.METAL -> metal
    }

    /** La línea de la tinta que graba: la azul negra sobre el papel, el crema sobre el negro. */
    private fun linea(k: ColoresPsi): Color = if (k.oscuro) TrazosTinta.CREMA.copy(alpha = 0.92f) else TrazosTinta.GRABAR

    // ---------------------------------------------------------------- piezas sueltas

    /**
     * Unas letras de bloque inventadas que llenan [caja] (no dicen nada): [n] columnas gruesas con los huecos casi cerrados,
     * como pedía la letra de los carteles de 1966; el canto de arriba sigue un arco ([arco], fracción del alto). Los huecos se
     * pintan con [hueco]. Cada letra sale siempre igual para la misma [semilla].
     */
    internal fun DrawScope.letrasBloque(caja: Rect, n: Int, color: Color, hueco: Color, arco: Float = 0.25f, semilla: Int = 0) {
        if (n <= 0 || caja.width < 2f || caja.height < 2f || !caja.width.isFinite() || !caja.height.isFinite()) return
        val paso = caja.width / n
        val sep = paso * 0.1f
        for (j in 0 until n) {
            val x0 = caja.left + j * paso + sep / 2; val ancho = paso - sep
            val u = (j + 0.5f) / n
            val arriba = caja.top + caja.height * arco * (1f - sin(PI.toFloat() * u))
            val alto = caja.bottom - arriba
            if (alto < 1f || ancho < 1f) continue
            val r = min(ancho, alto) * 0.22f
            drawRoundRect(color, Offset(x0, arriba), Size(ancho, alto), CornerRadius(r))
            val hx = ancho * 0.2f; val hy = alto * 0.16f
            when ((azar(semilla, j) * 4).toInt().coerceIn(0, 3)) {
                // Un hueco alto y estrecho en medio (la O, la D).
                0 -> drawRoundRect(hueco, Offset(x0 + ancho / 2 - hx / 2, arriba + alto * 0.22f), Size(hx, alto * 0.56f), CornerRadius(hx / 2))
                // Dos huecos chicos, uno sobre otro (la B, el 8).
                1 -> for (q in 0..1) drawRoundRect(hueco, Offset(x0 + ancho / 2 - hx / 2, arriba + alto * (0.2f + q * 0.38f)), Size(hx, hy), CornerRadius(hx / 2))
                // Dos muescas desde la derecha (la E).
                2 -> for (q in 0..1) drawRect(hueco, Offset(x0 + ancho * 0.55f, arriba + alto * (0.26f + q * 0.34f)), Size(ancho * 0.45f, hy * 0.7f))
                // Una muesca desde abajo y un hueco arriba (la A).
                else -> {
                    drawRect(hueco, Offset(x0 + ancho / 2 - hx / 2, arriba + alto * 0.62f), Size(hx, alto * 0.38f))
                    drawRoundRect(hueco, Offset(x0 + ancho / 2 - hx / 2, arriba + alto * 0.22f), Size(hx, hy), CornerRadius(hx / 2))
                }
            }
        }
    }

    /** Una línea que ondula en vertical, de [y0] a [y1] alrededor de [x], con [amp] px y [periodo] px de onda, corrida [fase] vueltas. */
    internal fun ondaVertical(x: Float, y0: Float, y1: Float, amp: Float, periodo: Float, fase: Float = 0f): Path = Path().apply {
        if (y1 <= y0 || periodo <= 0f || !y1.isFinite()) return@apply
        val paso = max(periodo / 16f, 0.5f)
        var y = y0; var primero = true
        while (y <= y1 + 0.01f) {
            val xx = x + amp * sin(2f * PI.toFloat() * ((y - y0) / periodo + fase))
            if (primero) { moveTo(xx, y); primero = false } else lineTo(xx, y)
            y += paso
        }
    }

    /** El brillo de la tarde (o de la lámpara, de noche) en lo que mira a la izquierda, y la sombra café a la derecha. */
    private fun luzCiudad(base: Color, oscuro: Boolean): Color = if (oscuro) lerp(base, TrazosCiudad.LAMPARA, 0.28f) else TrazosCiudad.alSol(base, 0.32f)

    // ================================================================ los trofeos

    /** Una pieza de un trofeo: su forma, su color en la ciudad y su tinta en el cartel; [trama] en el lado de la sombra. */
    private class Capa(val forma: Path, val ciudad: Color, val tono: TonoPsi, val trama: Boolean = true)

    /** El lienzo de un trofeo en fracciones: x de 0 a 1 a lo ancho, y de 0 a 1 a lo alto. */
    private class Lienzo(val w: Float, val h: Float) {
        fun o(fx: Float, fy: Float) = Offset(w * fx, h * fy)
        fun r(x0: Float, y0: Float, x1: Float, y1: Float) = Rect(w * x0, h * y0, w * x1, h * y1)
        /** Un rectángulo (con esquinas de [radio], en fracción del ancho). */
        fun caja(x0: Float, y0: Float, x1: Float, y1: Float, radio: Float = 0f): Path = Path().apply {
            if (radio <= 0f) addRect(r(x0, y0, x1, y1)) else addRoundRect(RoundRect(r(x0, y0, x1, y1), CornerRadius(w * radio)))
        }
        /** Un círculo en ([cx], [cy]) de radio [rf] (fracción del ancho). */
        fun circulo(cx: Float, cy: Float, rf: Float): Path = Path().apply { addOval(Rect(o(cx, cy), w * rf)) }
        /** Un polígono por pares (x, y) en fracciones. */
        fun poli(vararg q: Float): Path = Path().apply {
            var k = 0
            while (k + 1 < q.size) { val p = o(q[k], q[k + 1]); if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y); k += 2 }
            close()
        }
        /** Un rectángulo girado [grados] alrededor de su centro (para lo que va ladeado sin girar todo el trofeo). */
        fun barra(x0: Float, y0: Float, x1: Float, y1: Float, ancho: Float): Path {
            val a = o(x0, y0); val b = o(x1, y1)
            val d = b - a; val l = d.getDistance().coerceAtLeast(0.001f)
            val n = Offset(-d.y / l, d.x / l) * (w * ancho / 2)
            return Path().apply { moveTo(a.x + n.x, a.y + n.y); lineTo(b.x + n.x, b.y + n.y); lineTo(b.x - n.x, b.y - n.y); lineTo(a.x - n.x, a.y - n.y); close() }
        }
    }

    /**
     * Dibuja el trofeo de [categoria] (sin el pedestal, que es [pedestalTrofeo]) en todo el lienzo. Los nueve objetos de la
     * guía: Diario, el cartel de la semana; Mundo, el pase de autobús; Cuerpo, las entradas de un baile; Mente, la plumilla
     * y el frasco de tinta; Cultura, la radio con el dial en el 106.9; Gente, el retroproyector de las luces; Constancia, el
     * disco del día; Curiosos, el pincel de rotulista del viejo Sal; Tuyos (y lo que no tenga categoría), la carpeta de
     * dibujos. El metal del nivel va en los herrajes, las tachuelas, las perillas, la etiqueta del disco y las cintas.
     */
    internal fun DrawScope.trofeo(categoria: String, t: TrofeoPsi) {
        val w = size.width; val h = size.height
        if (w <= 2f || h <= 2f) return
        val k = k(t.oscuro, t.cara, t.i)
        val l = Lienzo(w, h)
        val lw = w * 0.01f
        val fino = h > 70f * density
        val metal = if (t.hecho) t.metal else t.silueta
        val capas = ArrayList<Capa>()
        // El centro de los rayos del cartel (detrás, en la tinta), en fracciones; y el giro de la figura entera.
        var centro = Offset(0.5f, 0.42f)
        var giro = 0f
        var detalles: DrawScope.() -> Unit = {}
        when (categoria) {
            "Diario" -> {
                // El cartel de la semana de David, recién pegado en un poste de la calle: el disco hecho sol en medio, las letras
                // de bloque arriba, las líneas de la fecha abajo, tres tachuelas del metal del nivel y la cuarta esquina que se
                // levanta; en el poste, un jirón del cartel de la semana pasada.
                giro = -2.5f
                centro = Offset(0.5f, 0.4f)
                capas += Capa(l.caja(0.43f, 0.02f, 0.57f, 0.765f), TrazosCiudad.POSTE, TonoPsi.GRABAR)
                capas += Capa(l.caja(0.27f, 0.06f, 0.73f, 0.09f), TrazosCiudad.POSTE, TonoPsi.GRABAR, trama = false)
                // En el negro, el cartel es el de luz negra: impreso sobre el terciopelo, con los rayos en crema.
                val hojaTono = if (k.oscuro) TonoPsi.GRABAR else TonoPsi.PAPEL
                capas += Capa(l.caja(0.13f, 0.13f, 0.87f, 0.66f), CARTON, hojaTono, trama = false)
                detalles = {
                    val hoja = l.r(0.13f, 0.13f, 0.87f, 0.66f)
                    clipRect(hoja.left, hoja.top, hoja.right, hoja.bottom) {
                        if (k.ciudad) {
                            // El cartel visto en la calle, desteñido por el sol: el sol de rayos y las letras en tintas apagadas.
                            val destenido = TrazosCiudad.pelicula(Color(0xFFB0506E))
                            val c = l.o(0.5f, 0.43f)
                            drawPath(PiezasPsicodelia.cunas(c, w * 0.12f, w * 0.34f, 18, 0.5f, 0.12f, 0f), destenido.copy(alpha = 0.55f))
                            drawCircle(TrazosCiudad.pelicula(Color(0xFF4A5A9E)).copy(alpha = 0.7f), w * 0.12f, c)
                            drawCircle(CARTON, w * 0.045f, c)
                            letrasBloque(l.r(0.17f, 0.15f, 0.83f, 0.25f), 6, TrazosCiudad.COCHE.copy(alpha = 0.75f), CARTON, 0.3f, 3)
                            listOf(0.6f to 0.5f, 0.635f to 0.34f).forEach { (y, a) -> drawLine(CAFE.copy(alpha = 0.45f), l.o(0.5f - a / 2, y), l.o(0.5f + a / 2, y), h * 0.012f) }
                            // Lo que el sol se comió: una franja más clara arriba a la izquierda.
                            drawRect(Brush.linearGradient(listOf(TrazosCiudad.AL_SOL.copy(alpha = 0.45f), Color.Transparent), hoja.topLeft, hoja.center), hoja.topLeft, hoja.size)
                        } else {
                            // El cartel impreso: los rayos que ondulan en la tinta que graba, el disco con su etiqueta, las letras
                            // de bloque en magenta (con su segunda pasada corrida) y la fecha.
                            val c = l.o(0.5f, 0.44f)
                            with(PiezasPsicodelia) {
                                corona(c, w * 0.11f, w * 0.42f, k, n = 22)
                                disco(c, w * 0.12f, k, etiqueta = metal)
                            }
                            val letras = l.r(0.17f, 0.145f, 0.83f, 0.25f)
                            val papel = tinta(hojaTono, k, metal)
                            if (k.m.registro > 0f) translate(k.m.registroTitulo * density * 0.6f, -k.m.registroTitulo * density * 0.4f) {
                                letrasBloque(letras, 6, k.ultramar.copy(alpha = 0.85f), Color.Transparent, 0.3f, 3)
                            }
                            letrasBloque(letras, 6, k.magenta, papel, 0.3f, 3)
                            drawRect(k.ultramar, l.o(0.13f, 0.585f), Size(w * 0.74f, h * 0.075f))
                            listOf(0.608f to 0.46f, 0.632f to 0.3f).forEach { (y, a) -> drawLine(tinta(TonoPsi.PAPEL, k, metal), l.o(0.5f - a / 2, y), l.o(0.5f + a / 2, y), h * 0.011f, StrokeCap.Round) }
                        }
                    }
                    // La esquina de abajo a la derecha, sin tachuela, que se levanta.
                    drawPath(l.poli(0.87f, 0.58f, 0.87f, 0.66f, 0.79f, 0.66f), if (k.ciudad) TrazosCiudad.enSombra(CARTON, 0.35f) else linea(k).copy(alpha = 0.35f))
                    drawPath(l.poli(0.87f, 0.58f, 0.79f, 0.66f, 0.81f, 0.58f), if (k.ciudad) TrazosCiudad.alSol(CARTON, 0.4f) else tinta(TonoPsi.PAPEL, k, metal))
                    // El jirón del cartel viejo en el poste.
                    drawPath(l.poli(0.43f, 0.69f, 0.57f, 0.7f, 0.565f, 0.735f, 0.5f, 0.72f, 0.45f, 0.745f, 0.43f, 0.73f), if (k.ciudad) TrazosCiudad.pelicula(Color(0xFFC8A060)) else k.ultramar.copy(alpha = 0.8f))
                    // Las tres tachuelas.
                    listOf(0.17f to 0.165f, 0.83f to 0.165f, 0.17f to 0.625f).forEach { (x, y) -> tachuela(l.o(x, y), w * 0.026f, t, k) }
                }
            }
            "Mundo" -> {
                // El pase de autobús del mes (15 centavos el viaje hasta marzo de 1969), de pie y un poco ladeado: la cartulina con
                // su franja de color, el mes en letras de bloque, la cuadrícula de los días con las picaduras del revisor y la pinza
                // del metal del nivel; arriba, los dos cables del trolebús que lo llevan por la ciudad.
                giro = -5f
                centro = Offset(0.5f, 0.44f)
                capas += Capa(l.caja(0.14f, 0.22f, 0.86f, 0.7f, 0.03f), CARTON, TonoPsi.PAPEL, trama = false)
                capas += Capa(l.caja(0.14f, 0.22f, 0.86f, 0.33f, 0.03f), NARANJA, TonoPsi.MAGENTA, trama = false)
                capas += Capa(l.caja(0.44f, 0.15f, 0.56f, 0.27f, 0.015f), t.metal, TonoPsi.METAL)
                detalles = {
                    val tintaPase = if (k.ciudad) CAFE else TrazosTinta.GRABAR
                    // Los cables, por encima.
                    val cable = if (k.ciudad) TrazosCiudad.POSTE.copy(alpha = 0.8f) else k.ultramar
                    if (k.ciudad) {
                        listOf(0.06f, 0.085f).forEach { y -> drawPath(Path().apply { moveTo(0f, h * y); quadraticTo(w * 0.5f, h * (y + 0.03f), w, h * (y - 0.01f)) }, cable, style = Stroke(lw * 0.5f)) }
                    } else {
                        listOf(0.06f to k.ultramar, 0.085f to k.magenta).forEachIndexed { j, (y, c) ->
                            drawPath(Path().apply {
                                var x = 0f; moveTo(0f, h * y)
                                while (x < w) { val m = x + w * 0.125f; quadraticTo(m, h * y + (if (j == 0) -1 else 1) * lw * 1.5f, x + w * 0.25f, h * y); x += w * 0.25f }
                            }, c, style = Stroke(lw * 0.7f, cap = StrokeCap.Round))
                        }
                    }
                    // El mes, en letras de bloque sobre la franja.
                    letrasBloque(l.r(0.2f, 0.235f, 0.62f, 0.315f), 5, if (k.ciudad) CARTON else tinta(TonoPsi.PAPEL, k, metal), if (k.ciudad) NARANJA else k.magenta, 0.15f, 7)
                    // «15»: la tarifa, en dos cifras de bloque.
                    cifra15(l.r(0.66f, 0.235f, 0.82f, 0.315f), if (k.ciudad) CARTON else tinta(TonoPsi.PAPEL, k, metal))
                    // La cuadrícula de los días del mes, con las picaduras.
                    val g = l.r(0.2f, 0.38f, 0.8f, 0.64f)
                    for (f in 0..4) drawLine(tintaPase.copy(alpha = 0.35f), Offset(g.left, g.top + g.height * f / 4), Offset(g.right, g.top + g.height * f / 4), lw * 0.3f)
                    for (c in 0..7) drawLine(tintaPase.copy(alpha = 0.35f), Offset(g.left + g.width * c / 7, g.top), Offset(g.left + g.width * c / 7, g.bottom), lw * 0.3f)
                    for (n in 0 until 11) {
                        val c = n % 7; val f = n / 7
                        val o = Offset(g.left + g.width * (c + 0.5f) / 7, g.top + g.height * (f + 0.5f) / 4)
                        drawCircle(if (k.ciudad) TrazosCiudad.enSombra(CARTON, 0.55f) else k.ultramar, g.width / 7 * 0.22f, o)
                    }
                    if (!k.ciudad) clipRect(g.left + g.width * 0.6f, g.top, g.right, g.bottom) { trama(Path().apply { addRect(g) }, lw * 0.9f, lw * 0.25f, TrazosTinta.GRABAR.copy(alpha = 0.3f)) }
                    // El agujero de la pinza.
                    drawCircle(t.metalOscuro, w * 0.018f, l.o(0.5f, 0.2f))
                }
            }
            "Cuerpo" -> {
                // Las entradas de un baile del sábado (2 dólares), dos, abiertas en abanico: el cartón con el talón, la línea de
                // perforaciones y sus dos muescas, la franja de arriba, las letras de bloque y el «2» de la entrada.
                centro = Offset(0.5f, 0.45f)
                val atras = Path().apply { addRoundRect(RoundRect(l.r(0.14f, 0.24f, 0.86f, 0.5f), CornerRadius(w * 0.03f))) }
                val delante = Path().apply { addRoundRect(RoundRect(l.r(0.12f, 0.42f, 0.88f, 0.7f), CornerRadius(w * 0.03f))) }
                capas += Capa(atras, CARTON, TonoPsi.PAPEL, trama = false)
                capas += Capa(delante, CARTON, TonoPsi.PAPEL, trama = false)
                detalles = {
                    listOf(Triple(atras, l.r(0.14f, 0.24f, 0.86f, 0.5f), 11), Triple(delante, l.r(0.12f, 0.42f, 0.88f, 0.7f), 5)).forEachIndexed { j, (forma, r, sem) ->
                        clipPath(forma) {
                            val franja = if (k.ciudad) (if (j == 0) BOTELLA else NARANJA) else (if (j == 0) k.ultramar else tinta(TonoPsi.BERMELLON, k, metal))
                            drawRect(franja, r.topLeft, Size(r.width, r.height * 0.18f))
                            val talon = r.left + r.width * 0.22f
                            if (!k.ciudad) clipRect(r.left, r.top, talon, r.bottom) {
                                drawPath(PiezasPsicodelia.cunas(Offset(r.left, r.bottom), r.height * 0.15f, r.height * 1.1f, 16, 0.5f, 0.1f, 0f), k.magenta.copy(alpha = 0.85f))
                            } else drawRect(TrazosCiudad.enSombra(CARTON, 0.12f), Offset(r.left, r.top + r.height * 0.18f), Size(talon - r.left, r.height * 0.82f))
                            drawLine(if (k.ciudad) CAFE.copy(alpha = 0.5f) else TrazosTinta.GRABAR.copy(alpha = 0.7f), Offset(talon, r.top + r.height * 0.22f), Offset(talon, r.bottom), lw * 0.4f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(lw * 0.8f, lw * 0.7f)))
                            letrasBloque(Rect(talon + r.width * 0.06f, r.top + r.height * 0.3f, r.right - r.width * 0.26f, r.top + r.height * 0.7f), 5,
                                if (k.ciudad) CAFE.copy(alpha = 0.8f) else k.magenta, if (k.ciudad) CARTON else tinta(TonoPsi.PAPEL, k, metal), 0.2f, sem)
                            cifra2(Rect(r.right - r.width * 0.2f, r.top + r.height * 0.28f, r.right - r.width * 0.06f, r.top + r.height * 0.74f), if (k.ciudad) NARANJA else k.ultramar)
                            drawLine((if (k.ciudad) CAFE else TrazosTinta.GRABAR).copy(alpha = 0.4f), Offset(talon + r.width * 0.06f, r.top + r.height * 0.84f), Offset(r.right - r.width * 0.3f, r.top + r.height * 0.84f), lw * 0.35f)
                        }
                        // Las dos muescas del talón.
                        val tx = r.left + r.width * 0.22f
                        listOf(r.top, r.bottom).forEach { y -> drawCircle(if (k.ciudad) (if (t.oscuro) TrazosCiudad.PARED_NOCHE else TrazosCiudad.AL_SOL) else k.papel, r.height * 0.07f, Offset(tx, y)) }
                    }
                    // El sello de la sala, del metal del nivel, sobre la de delante.
                    val s = l.o(0.25f, 0.62f)
                    drawCircle(t.metal, w * 0.045f, s)
                    drawCircle(t.metalOscuro, w * 0.045f, s, style = Stroke(lw * 0.4f))
                    drawCircle(t.brillo, w * 0.02f, s)
                }
                giro = -6f
            }
            "Mente" -> {
                // La plumilla y el frasco de tinta del dibujante: el frasco cuadrado y bajo, de vidrio, con la tinta dentro, su
                // tapa del metal del nivel y la etiqueta; delante, el mango de la plumilla con su pluma de acero; y la mancha de
                // tinta que cayó al cargarla.
                centro = Offset(0.44f, 0.5f)
                capas += Capa(l.caja(0.2f, 0.44f, 0.64f, 0.765f, 0.04f), TINTA_FRASCO, TonoPsi.ULTRAMAR)
                capas += Capa(l.caja(0.33f, 0.37f, 0.51f, 0.45f), TINTA_FRASCO, TonoPsi.ULTRAMAR)
                capas += Capa(l.caja(0.31f, 0.3f, 0.53f, 0.38f, 0.015f), t.metal, TonoPsi.METAL)
                capas += Capa(l.caja(0.27f, 0.54f, 0.57f, 0.69f, 0.01f), CARTON, TonoPsi.PAPEL, trama = false)
                capas += Capa(l.barra(0.16f, 0.745f, 0.78f, 0.5f, 0.05f), TrazosCiudad.COCHE, TonoPsi.MAGENTA, trama = false)
                capas += Capa(l.poli(0.78f, 0.475f, 0.92f, 0.43f, 0.8f, 0.525f), t.metal, TonoPsi.METAL, trama = false)
                detalles = {
                    // El brillo del vidrio, de arriba abajo por la izquierda (o la lámpara, de noche).
                    if (k.ciudad) drawLine(luzCiudad(Color.White, t.oscuro).copy(alpha = 0.45f), l.o(0.24f, 0.47f), l.o(0.24f, 0.74f), lw * 1.1f, StrokeCap.Round)
                    else drawLine(tinta(TonoPsi.PAPEL, k, metal), l.o(0.24f, 0.47f), l.o(0.24f, 0.74f), lw * 0.9f, StrokeCap.Round)
                    // La etiqueta: el disco del día chico y dos renglones.
                    with(PiezasPsicodelia) { disco(l.o(0.35f, 0.615f), w * 0.045f, k, sombra = false) }
                    listOf(0.59f, 0.63f).forEach { y -> drawLine((if (k.ciudad) CAFE else TrazosTinta.GRABAR).copy(alpha = 0.6f), l.o(0.42f, y), l.o(0.54f, y), lw * 0.45f) }
                    // Las estrías de la tapa.
                    for (j in 1..5) drawLine(t.metalOscuro.copy(alpha = 0.5f), l.o(0.31f + 0.037f * j, 0.305f), l.o(0.31f + 0.037f * j, 0.375f), lw * 0.3f)
                    // La raja de la pluma.
                    drawLine(t.metalOscuro, l.o(0.8f, 0.5f), l.o(0.9f, 0.44f), lw * 0.3f)
                    // La mancha de tinta que cayó: plana en el cartel, un charquito con brillo en la ciudad.
                    val mancha = Path().apply {
                        val c = l.o(0.78f, 0.745f)
                        for (j in 0..24) {
                            val a = j * 2f * PI.toFloat() / 24
                            val rr = w * 0.07f * (1f + 0.22f * sin(a * 3f + 1f))
                            val p = Offset(c.x + rr * cos(a), c.y + rr * sin(a) * 0.32f)
                            if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                        }
                        close()
                    }
                    drawPath(mancha, if (k.ciudad) TINTA_FRASCO else k.ultramar)
                    if (k.ciudad) drawCircle(Color.White.copy(alpha = 0.3f), w * 0.012f, l.o(0.76f, 0.738f))
                }
            }
            "Cultura" -> {
                // La radio de mesa con el dial en el 106.9 (la FM que, desde el 12 de febrero de 1967, ponía discos enteros de
                // medianoche a las seis): el mueble de madera de canto redondo, la tela del altavoz, la ventanilla del dial con
                // su aguja y las dos perillas del metal del nivel. En el cartel, las ondas que salen de ella.
                centro = Offset(0.5f, 0.52f)
                capas += Capa(l.caja(0.08f, 0.38f, 0.92f, 0.765f, 0.07f), TrazosCiudad.MADERA, TonoPsi.MAGENTA)
                capas += Capa(l.caja(0.14f, 0.44f, 0.5f, 0.72f, 0.03f), TELA, TonoPsi.ULTRAMAR)
                capas += Capa(l.caja(0.55f, 0.45f, 0.87f, 0.56f, 0.015f), CARTON, TonoPsi.PAPEL, trama = false)
                detalles = {
                    // La tela del altavoz: un tejido de líneas finas (o la trama de la tinta).
                    val tela = l.r(0.14f, 0.44f, 0.5f, 0.72f)
                    val hilo = if (k.ciudad) TrazosCiudad.enSombra(TELA, 0.4f).copy(alpha = 0.5f) else tinta(TonoPsi.PAPEL, k, metal).copy(alpha = 0.55f)
                    clipRect(tela.left, tela.top, tela.right, tela.bottom) {
                        var x = tela.left + lw; while (x < tela.right) { drawLine(hilo, Offset(x, tela.top), Offset(x, tela.bottom), lw * 0.25f); x += max(lw * 1.1f, 1f) }
                        if (k.ciudad) { var y = tela.top + lw; while (y < tela.bottom) { drawLine(hilo.copy(alpha = 0.25f), Offset(tela.left, y), Offset(tela.right, y), lw * 0.25f); y += max(lw * 1.1f, 1f) } }
                    }
                    // El dial: las marcas de 88 a 108 y la aguja en el 106.9.
                    val dial = l.r(0.55f, 0.45f, 0.87f, 0.56f)
                    val tdial = if (k.ciudad) CAFE else TrazosTinta.GRABAR
                    for (j in 0..10) {
                        val x = dial.left + dial.width * (0.08f + 0.84f * j / 10f)
                        drawLine(tdial.copy(alpha = 0.7f), Offset(x, dial.top + dial.height * 0.2f), Offset(x, dial.top + dial.height * (if (j % 5 == 0) 0.55f else 0.4f)), lw * 0.3f)
                    }
                    val xa = dial.left + dial.width * (0.08f + 0.84f * ((106.9f - 88f) / 20f))
                    drawLine(if (k.ciudad) TrazosCiudad.LETRERO else k.magenta, Offset(xa, dial.top + dial.height * 0.1f), Offset(xa, dial.bottom - dial.height * 0.1f), lw * 0.6f)
                    if (k.ciudad && t.oscuro) drawRect(TrazosCiudad.LAMPARA_LUZ.copy(alpha = 0.25f), dial.topLeft, dial.size)
                    // Las dos perillas.
                    listOf(0.62f, 0.8f).forEach { x ->
                        val c = l.o(x, 0.66f); val r = w * 0.055f
                        drawCircle(t.metalOscuro, r, c)
                        drawCircle(Brush.radialGradient(listOf(t.brillo, t.metal, t.metalOscuro), Offset(c.x - r * 0.35f, c.y - r * 0.35f), r * 1.4f), r * 0.86f, c)
                        drawLine(t.metalOscuro, c, Offset(c.x, c.y - r * 0.8f), lw * 0.4f)
                    }
                    // Las patas.
                    listOf(0.16f, 0.84f).forEach { x -> drawRect(if (k.ciudad) TrazosCiudad.enSombra(TrazosCiudad.MADERA, 0.5f) else linea(k), l.o(x - 0.03f, 0.75f), Size(w * 0.06f, h * 0.015f)) }
                    // Las ondas del 106.9.
                    if (!k.ciudad) {
                        val o = l.o(0.5f, 0.38f)
                        for (j in 1..(3 + k.m.anillos / 3)) {
                            val r = w * (0.12f + j * 0.085f)
                            drawArc(if (j % 2 == 0) k.ultramar else k.magenta, 205f, 130f, false, Offset(o.x - r, o.y - r), Size(r * 2, r * 2), style = Stroke(lw * 0.9f, cap = StrokeCap.Round))
                        }
                    } else drawLine(TrazosCiudad.alSol(TrazosCiudad.MADERA, 0.5f).copy(alpha = 0.6f), l.o(0.15f, 0.395f), l.o(0.85f, 0.395f), lw * 0.5f, StrokeCap.Round)
                }
            }
            "Gente" -> {
                // El retroproyector de las luces de Ellen: la caja con la platina de vidrio, el plato de reloj con el aceite y las
                // tintas, el brazo y la cabeza con su lente; el haz sale hacia arriba a la izquierda y pinta en el aire las manchas
                // de luz líquida (planas, de tinta, en el cartel).
                centro = Offset(0.32f, 0.26f)
                capas += Capa(l.caja(0.12f, 0.56f, 0.82f, 0.765f, 0.02f), Color(0xFF5E5850), TonoPsi.GRABAR)
                capas += Capa(l.caja(0.16f, 0.53f, 0.62f, 0.57f), Color(0xFFB8C4C2), TonoPsi.PAPEL, trama = false)
                capas += Capa(l.caja(0.68f, 0.16f, 0.74f, 0.56f), Color(0xFF4A4640), TonoPsi.GRABAR, trama = false)
                capas += Capa(l.caja(0.5f, 0.12f, 0.82f, 0.22f, 0.02f), Color(0xFF4A4640), TonoPsi.GRABAR)
                detalles = {
                    // El haz y las manchas.
                    val lente = l.o(0.52f, 0.17f)
                    if (k.ciudad) {
                        drawPath(Path().apply { moveTo(lente.x, lente.y - h * 0.02f); lineTo(0f, h * 0.0f); lineTo(0f, h * 0.36f); lineTo(lente.x, lente.y + h * 0.02f); close() },
                            Brush.horizontalGradient(listOf(Color.Transparent, (if (t.oscuro) TrazosCiudad.LAMPARA else TrazosCiudad.SOL_VELADO).copy(alpha = 0.45f)), 0f, lente.x))
                        if (t.oscuro) listOf(Triple(0.12f, 0.12f, Color(0xFFB0506E)), Triple(0.2f, 0.24f, Color(0xFFC8662A))).forEach { (x, y, c) ->
                            drawCircle(Brush.radialGradient(listOf(c.copy(alpha = 0.55f), Color.Transparent), l.o(x, y), w * 0.12f), w * 0.12f, l.o(x, y))
                        }
                    } else {
                        listOf(Triple(0.13f, 0.12f, TonoPsi.MAGENTA), Triple(0.27f, 0.28f, TonoPsi.ULTRAMAR), Triple(0.1f, 0.33f, TonoPsi.BERMELLON)).forEachIndexed { j, (x, y, tono) ->
                            val c = l.o(x, y); val r = w * (0.1f - j * 0.015f)
                            val gota = Path().apply {
                                for (s in 0..30) {
                                    val a = s * 2f * PI.toFloat() / 30
                                    val rr = r * (1f + 0.18f * sin(a * 3f + j * 1.7f) + 0.08f * sin(a * 5f + j))
                                    val p = Offset(c.x + rr * cos(a), c.y + rr * sin(a))
                                    if (s == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                                }
                                close()
                            }
                            drawPath(gota, tinta(tono, k, metal).copy(alpha = 0.9f))
                        }
                        drawPath(Path().apply { moveTo(lente.x, lente.y - h * 0.02f); lineTo(0f, 0f); moveTo(lente.x, lente.y + h * 0.02f); lineTo(0f, h * 0.36f) }, linea(k).copy(alpha = 0.6f), style = Stroke(lw * 0.35f))
                    }
                    // La lente.
                    drawCircle(t.metalOscuro, w * 0.05f, l.o(0.5f, 0.17f))
                    drawCircle(t.metal, w * 0.036f, l.o(0.5f, 0.17f))
                    drawCircle(t.brillo, w * 0.014f, l.o(0.49f, 0.16f))
                    // El plato de reloj con el aceite y las tintas, sobre la platina.
                    val plato = l.r(0.22f, 0.49f, 0.52f, 0.55f)
                    drawOval(if (k.ciudad) Color(0xFFD8DED8).copy(alpha = 0.8f) else tinta(TonoPsi.PAPEL, k, metal), plato.topLeft, plato.size)
                    drawOval(if (k.ciudad) Color(0xFFB0506E).copy(alpha = 0.7f) else k.magenta, Offset(plato.left + plato.width * 0.15f, plato.top + plato.height * 0.2f), Size(plato.width * 0.4f, plato.height * 0.55f))
                    drawOval(if (k.ciudad) Color(0xFF4A5A9E).copy(alpha = 0.7f) else k.ultramar, Offset(plato.left + plato.width * 0.5f, plato.top + plato.height * 0.25f), Size(plato.width * 0.32f, plato.height * 0.5f))
                    drawOval(t.metalOscuro, plato.topLeft, plato.size, style = Stroke(lw * 0.4f))
                    // La rejilla del ventilador y el interruptor del metal del nivel.
                    for (j in 0 until 5) drawLine((if (k.ciudad) Color.Black else linea(k)).copy(alpha = 0.35f), l.o(0.18f, 0.62f + j * 0.025f), l.o(0.4f, 0.62f + j * 0.025f), lw * 0.4f)
                    drawRoundRect(t.metal, l.o(0.64f, 0.63f), Size(w * 0.1f, h * 0.04f), CornerRadius(lw))
                }
            }
            "Constancia" -> {
                // El disco del día, de pie delante de su funda: la funda impresa (dos franjas en la ciudad; las bandas que ondulan
                // en el cartel) y el disco que asoma con la etiqueta del metal del nivel.
                centro = Offset(0.58f, 0.46f)
                capas += Capa(l.caja(0.08f, 0.2f, 0.62f, 0.765f, 0.012f), BOTELLA, TonoPsi.ULTRAMAR)
                detalles = {
                    val funda = l.r(0.08f, 0.2f, 0.62f, 0.765f)
                    clipRect(funda.left, funda.top, funda.right, funda.bottom) {
                        if (k.ciudad) {
                            drawRect(NARANJA, Offset(funda.left, funda.top + funda.height * 0.58f), Size(funda.width, funda.height * 0.12f))
                            drawRect(MOSTAZA, Offset(funda.left, funda.top + funda.height * 0.72f), Size(funda.width, funda.height * 0.05f))
                            letrasBloque(Rect(funda.left + funda.width * 0.1f, funda.top + funda.height * 0.1f, funda.right - funda.width * 0.1f, funda.top + funda.height * 0.24f), 4,
                                CARTON.copy(alpha = 0.9f), BOTELLA, 0f, 21)
                        } else {
                            with(PiezasPsicodelia) {
                                ondas(funda.left, funda.right, funda.top + funda.height * 0.62f, lw * (1.2f + 0.08f * k.m.amplitud), funda.width * 0.5f, lw * 0.9f, lw * 1.8f,
                                    tintasBanda(k).map { (tt, c) -> tt to if (tt == TrazosTinta.Tinta.ULTRAMAR) tinta(TonoPsi.PAPEL, k, metal) else c }, k.m.registro * 0.5f, adelgaza = false)
                            }
                            letrasBloque(Rect(funda.left + funda.width * 0.1f, funda.top + funda.height * 0.1f, funda.right - funda.width * 0.1f, funda.top + funda.height * 0.26f), 4,
                                tinta(TonoPsi.PAPEL, k, metal), k.ultramar, 0.2f, 21)
                        }
                    }
                    with(PiezasPsicodelia) { disco(l.o(0.6f, 0.47f), w * 0.31f, k, giro = 12f, etiqueta = t.metal) }
                    // El brillo del metal en la etiqueta.
                    if (t.nivel >= 3) drawArc(t.brillo, 200f, 70f, false, l.o(0.6f - 0.09f, 0.47f - 0.09f * w / h), Size(w * 0.18f, w * 0.18f), style = Stroke(lw * 0.6f, cap = StrokeCap.Round))
                }
            }
            "Curiosos" -> {
                // El pincel de rotulista del viejo Sal, el de los letreros de las tiendas, apoyado en la lata de pintura de
                // letreros: el pelo largo y en punta cargado de pintura, la virola del metal del nivel y el mango largo; detrás,
                // la cartulina de muestra con las letras de bloque pintadas a pulso.
                centro = Offset(0.4f, 0.4f)
                capas += Capa(l.caja(0.08f, 0.42f, 0.5f, 0.765f, 0.01f), CARTON, TonoPsi.PAPEL, trama = false)
                capas += Capa(l.caja(0.5f, 0.52f, 0.88f, 0.765f, 0.02f), Color(0xFF8E8A80), TonoPsi.GRABAR)
                capas += Capa(l.barra(0.84f, 0.72f, 0.42f, 0.29f, 0.045f), PINO, TonoPsi.ULTRAMAR, trama = false)
                capas += Capa(l.barra(0.43f, 0.3f, 0.33f, 0.2f, 0.065f), t.metal, TonoPsi.METAL, trama = false)
                // El pelo: ancho junto a la virola y en punta larga hacia arriba a la izquierda.
                val pelo = Path().apply {
                    val a0 = l.o(0.355f, 0.17f); val a1 = l.o(0.305f, 0.225f); val p0 = l.o(0.13f, 0.03f)
                    moveTo(a0.x, a0.y); quadraticTo(w * 0.27f, h * 0.06f, p0.x, p0.y); quadraticTo(w * 0.18f, h * 0.14f, a1.x, a1.y); close()
                }
                capas += Capa(pelo, Color(0xFF3A2A20), TonoPsi.GRABAR, trama = false)
                detalles = {
                    // Las letras de la cartulina y su renglón de lápiz.
                    val tarjeta = l.r(0.08f, 0.42f, 0.5f, 0.765f)
                    letrasBloque(Rect(tarjeta.left + tarjeta.width * 0.1f, tarjeta.top + tarjeta.height * 0.14f, tarjeta.right - tarjeta.width * 0.12f, tarjeta.top + tarjeta.height * 0.5f), 3,
                        if (k.ciudad) TrazosCiudad.LETRERO else k.magenta, if (k.ciudad) CARTON else tinta(TonoPsi.PAPEL, k, metal), 0.18f, 17)
                    drawLine((if (k.ciudad) CAFE else k.ultramar).copy(alpha = 0.5f), Offset(tarjeta.left + tarjeta.width * 0.1f, tarjeta.top + tarjeta.height * 0.62f),
                        Offset(tarjeta.right - tarjeta.width * 0.2f, tarjeta.top + tarjeta.height * 0.62f), lw * 0.9f, StrokeCap.Round)
                    // La pintura en la punta del pelo.
                    clipPath(pelo) { drawPath(l.poli(0.1f, 0.0f, 0.26f, 0.0f, 0.24f, 0.14f, 0.1f, 0.12f), if (k.ciudad) TrazosCiudad.LETRERO else tinta(TonoPsi.BERMELLON, k, metal)) }
                    // La lata: el aro de arriba, la etiqueta y la pintura que se escurrió.
                    drawRect(t.metalOscuro, l.o(0.5f, 0.52f), Size(w * 0.38f, h * 0.02f))
                    drawRect(if (k.ciudad) CARTON else tinta(TonoPsi.PAPEL, k, metal), l.o(0.54f, 0.59f), Size(w * 0.3f, h * 0.1f))
                    drawCircle(if (k.ciudad) TrazosCiudad.LETRERO else k.magenta, w * 0.035f, l.o(0.69f, 0.64f))
                    drawPath(Path().apply { moveTo(w * 0.74f, h * 0.525f); lineTo(w * 0.8f, h * 0.525f); lineTo(w * 0.785f, h * 0.575f); quadraticTo(w * 0.775f, h * 0.595f, w * 0.765f, h * 0.575f); close() },
                        if (k.ciudad) TrazosCiudad.LETRERO else k.magenta)
                }
            }
            else -> {
                // Tuyos: la carpeta de dibujos de David, de cartón, entreabierta, con los pliegos que asoman (un boceto a lápiz en
                // la ciudad; un cartel a medias en la tinta) y las dos cintas del metal del nivel que la atan.
                centro = Offset(0.5f, 0.3f)
                capas += Capa(l.caja(0.2f, 0.12f, 0.74f, 0.5f), CARTON, TonoPsi.PAPEL, trama = false)
                capas += Capa(l.caja(0.28f, 0.08f, 0.84f, 0.42f), lerp(CARTON, Color.White, 0.3f), TonoPsi.PAPEL, trama = false)
                capas += Capa(l.caja(0.08f, 0.3f, 0.92f, 0.765f, 0.02f), Color(0xFF6A5A44), TonoPsi.ULTRAMAR)
                detalles = {
                    // Lo que asoma de los pliegos.
                    val pliego = l.r(0.28f, 0.08f, 0.84f, 0.3f)
                    clipRect(pliego.left, pliego.top, pliego.right, pliego.bottom) {
                        if (k.ciudad) {
                            drawArc(CAFE.copy(alpha = 0.45f), 190f, 160f, false, l.o(0.42f, 0.12f), Size(w * 0.3f, w * 0.3f), style = Stroke(lw * 0.4f))
                            drawLine(CAFE.copy(alpha = 0.35f), l.o(0.34f, 0.27f), l.o(0.78f, 0.13f), lw * 0.3f)
                            drawLine(CAFE.copy(alpha = 0.3f), l.o(0.36f, 0.22f), l.o(0.7f, 0.22f), lw * 0.3f)
                        } else {
                            with(PiezasPsicodelia) { corona(l.o(0.56f, 0.3f), w * 0.07f, w * 0.26f, k, n = 16) }
                            drawCircle(k.magenta, w * 0.07f, l.o(0.56f, 0.3f))
                        }
                    }
                    // La solapa de la carpeta y la etiqueta con su renglón.
                    drawRect(if (k.ciudad) TrazosCiudad.enSombra(Color(0xFF6A5A44), 0.3f) else k.magenta, l.o(0.08f, 0.3f), Size(w * 0.84f, h * 0.025f))
                    drawRect(if (k.ciudad) CARTON else tinta(TonoPsi.PAPEL, k, metal), l.o(0.32f, 0.47f), Size(w * 0.36f, h * 0.1f))
                    drawLine((if (k.ciudad) CAFE else TrazosTinta.GRABAR).copy(alpha = 0.6f), l.o(0.36f, 0.52f), l.o(0.64f, 0.52f), lw * 0.45f)
                    // Las cintas, del metal del nivel, con su lazo.
                    listOf(0.2f, 0.8f).forEach { x ->
                        drawRect(Brush.horizontalGradient(listOf(t.metalOscuro, t.metal, t.brillo, t.metal), w * (x - 0.02f), w * (x + 0.02f)), l.o(x - 0.02f, 0.38f), Size(w * 0.04f, h * 0.385f))
                    }
                    drawPath(Path().apply { moveTo(w * 0.8f, h * 0.6f); quadraticTo(w * 0.92f, h * 0.55f, w * 0.88f, h * 0.66f); quadraticTo(w * 0.84f, h * 0.68f, w * 0.8f, h * 0.6f); close() }, t.metal)
                    // Las esquinas gastadas de la carpeta.
                    if (k.ciudad) listOf(l.o(0.08f, 0.765f), l.o(0.92f, 0.765f)).forEach { drawCircle(TrazosCiudad.alSol(Color(0xFF6A5A44), 0.3f).copy(alpha = 0.5f), w * 0.025f, it) }
                }
            }
        }

        val todo = Path().apply { capas.forEach { addPath(it.forma) } }
        val pivote = Offset(w * 0.5f, h * 0.45f)
        rotate(giro, pivote) {
            if (!t.hecho) { drawPath(todo, t.silueta); return@rotate }
            // Detrás: la sombra café de la ciudad, o los rayos del cartel (y el registro corrido) en la tinta.
            if (k.ciudad) {
                translate(lw * 1.3f, lw * 0.9f) { drawPath(todo, k.sombra) }
            } else {
                clipRect(0f, 0f, w, h * 0.765f) {
                    val c = Offset(w * centro.x, h * centro.y)
                    drawPath(PiezasPsicodelia.cunas(c, w * 0.16f, w * 0.62f, 22, 0.42f, 0.14f + 0.08f * k.i, 0.2f),
                        (if (k.oscuro) k.ultramar else TrazosTinta.GRABAR).copy(alpha = if (k.oscuro) 0.28f else 0.13f))
                    for (j in 1..k.m.anillos) {
                        val r = w * (0.2f + j * 0.05f)
                        drawCircle((if (j % 2 == 0) k.ultramar else k.magenta).copy(alpha = 0.35f), r, c, style = Stroke(lw * 0.5f))
                    }
                }
                if (k.m.registro > 0f) conRegistro(TrazosTinta.Tinta.MAGENTA, k.m.registro * 0.8f) { drawPath(todo, k.magenta.copy(alpha = 0.45f)) }
            }
            capas.forEach { volumen(it, k, t, lw, fino) }
            detalles()
            if (k.ciudad) with(PiezasPsicodelia) { granoPelicula(todo, k.grano * 1.4f) }
        }
        // La sombra donde toca el pedestal.
        if (t.hecho) drawOval((if (k.ciudad) TrazosCiudad.SOMBRA else linea(k)).copy(alpha = if (t.oscuro) 0.45f else 0.2f), l.o(0.18f, 0.755f), Size(w * 0.64f, h * 0.02f))
    }

    /** Una pieza: en la ciudad, con la luz de la izquierda y el filo café; en la tinta, plana, con su trama a la derecha y el contorno. */
    private fun DrawScope.volumen(c: Capa, k: ColoresPsi, t: TrofeoPsi, lw: Float, fino: Boolean) {
        val b = c.forma.getBounds()
        if (b.isEmpty || !b.width.isFinite()) return
        if (k.ciudad) {
            val base = c.ciudad
            if (b.width > 2f) drawPath(c.forma, Brush.horizontalGradient(0f to luzCiudad(base, t.oscuro), 0.4f to base, 0.7f to base, 1f to TrazosCiudad.enSombra(base, 0.36f), startX = b.left, endX = b.right))
            else drawPath(c.forma, base)
            drawPath(c.forma, TrazosCiudad.enSombra(base, 0.6f).copy(alpha = 0.55f), style = Stroke(lw * 0.45f, join = StrokeJoin.Round))
        } else {
            val col = tinta(c.tono, k, t.metal)
            when (c.tono) {
                TonoPsi.MAGENTA -> conRegistro(TrazosTinta.Tinta.MAGENTA, k.m.registro * 0.5f) { drawPath(c.forma, col) }
                TonoPsi.BERMELLON -> conRegistro(TrazosTinta.Tinta.BERMELLON, k.m.registro * 0.5f) { drawPath(c.forma, col) }
                TonoPsi.PAPEL -> drawPath(c.forma, col)
                TonoPsi.GRABAR -> drawPath(c.forma, col)
                TonoPsi.ULTRAMAR -> drawPath(c.forma, col)
                TonoPsi.METAL -> drawPath(c.forma, col)
            }
            if (c.trama && fino) clipRect(b.left + b.width * 0.62f, b.top, b.right, b.bottom) {
                trama(c.forma, max(lw * 0.8f, 1.2f), max(lw * 0.26f, 0.6f), (if (c.tono == TonoPsi.METAL) t.metalOscuro else linea(k)).copy(alpha = 0.7f))
            }
            drawPath(c.forma, linea(k), style = Stroke(lw * 0.55f, join = StrokeJoin.Round))
        }
    }

    /** Una tachuela del metal del nivel, con su brillo y su sombra corta. */
    private fun DrawScope.tachuela(c: Offset, r: Float, t: TrofeoPsi, k: ColoresPsi) {
        if (k.ciudad) drawCircle(k.sombra, r, c + Offset(r * 0.25f, r * 0.3f))
        drawCircle(Brush.radialGradient(listOf(t.brillo, t.metal, t.metalOscuro), Offset(c.x - r * 0.3f, c.y - r * 0.35f), r * 1.5f), r, c)
        if (!k.ciudad) drawCircle(linea(k), r, c, style = Stroke(r * 0.18f))
    }

    /** «15», en dos cifras de bloque que llenan [r]. */
    private fun DrawScope.cifra15(r: Rect, c: Color) {
        val g = r.width * 0.16f
        drawRect(c, Offset(r.left + r.width * 0.06f, r.top), Size(g, r.height))
        val x0 = r.left + r.width * 0.36f; val x1 = r.right
        drawRect(c, Offset(x0, r.top), Size(x1 - x0, g))
        drawRect(c, Offset(x0, r.top), Size(g, r.height * 0.55f))
        drawRect(c, Offset(x0, r.top + r.height * 0.45f), Size(x1 - x0, g))
        drawRect(c, Offset(x1 - g, r.top + r.height * 0.45f), Size(g, r.height * 0.55f))
        drawRect(c, Offset(x0, r.bottom - g), Size(x1 - x0, g))
    }

    /** «2», en una cifra de bloque que llena [r]. */
    private fun DrawScope.cifra2(r: Rect, c: Color) {
        val g = min(r.width, r.height) * 0.26f
        drawRect(c, r.topLeft, Size(r.width, g))
        drawRect(c, Offset(r.right - g, r.top), Size(g, r.height * 0.55f))
        drawRect(c, Offset(r.left, r.top + r.height * 0.45f), Size(r.width, g))
        drawRect(c, Offset(r.left, r.top + r.height * 0.45f), Size(g, r.height * 0.55f))
        drawRect(c, Offset(r.left, r.bottom - g), Size(r.width, g))
    }

    // ================================================================ pedestales y placa

    /**
     * La caja de discos de pino, puesta de cabeza, que hace de peana (la que había en cada departamento compartido): la tapa
     * de tablas con su canto, el frente de dos tablas con la rendija, los postes de las esquinas, la etiqueta pegada al
     * frente (la del disco del día en la ciudad; un cartelito impreso en la tinta) y los flejes del metal del nivel con sus
     * clavos. Sin ganar, la silueta tenue.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        if (w <= 2f || h <= 2f) return
        val k = k(oscuro)
        val l = Lienzo(w, h)
        val lw = max(h * 0.0035f, 0.45f * u)
        val fino = h > 70 * u
        val tapa = l.r(0.05f, 0.765f, 0.95f, 0.79f)
        val frente = l.r(0.07f, 0.79f, 0.93f, 0.95f)
        val forma = Path().apply { addRect(tapa); addRect(frente) }
        if (!e.hecho) { drawPath(forma, silueta(oscuro, k)); return }
        val linea = linea(k)
        if (k.ciudad) {
            drawOval(TrazosCiudad.SOMBRA.copy(alpha = if (oscuro) 0.6f else 0.22f), l.o(0.03f, 0.935f), Size(w * 0.98f, h * 0.035f))
            val pino = if (oscuro) lerp(PINO, TrazosCiudad.PARED_NOCHE, 0.35f) else PINO
            drawRect(TrazosCiudad.enSombra(pino, 0.75f), frente.topLeft, frente.size)
            listOf(0.795f to 0.862f, 0.874f to 0.945f).forEachIndexed { j, (y0, y1) ->
                val r = l.r(0.07f, y0, 0.93f, y1)
                drawRect(Brush.horizontalGradient(listOf(luzCiudad(pino, oscuro), pino, pino, TrazosCiudad.enSombra(pino, 0.3f)), r.left, r.right), r.topLeft, r.size)
                if (fino) {
                    var y = r.top + lw * 1.4f; var q = 0
                    while (y < r.bottom && q < 12) { drawLine(TrazosCiudad.enSombra(pino, 0.4f).copy(alpha = 0.35f), Offset(r.left, y), Offset(r.right, y + (azar(e.semilla + j, q) - 0.5f) * lw * 2f), lw * 0.4f); y += lw * 2.8f; q++ }
                }
            }
            listOf(0.07f to 0.13f, 0.87f to 0.93f).forEach { (x0, x1) -> val r = l.r(x0, 0.79f, x1, 0.95f); drawRect(TrazosCiudad.enSombra(pino, 0.3f), r.topLeft, r.size) }
            drawRect(Brush.verticalGradient(listOf(TrazosCiudad.SOMBRA.copy(alpha = 0.4f), Color.Transparent), h * 0.79f, h * 0.82f), l.o(0.07f, 0.79f), Size(w * 0.86f, h * 0.03f))
            // La etiqueta: la del disco del día, naranja, con sus dos renglones.
            val et = l.r(0.36f, 0.81f, 0.64f, 0.925f)
            drawRect(CARTON, et.topLeft, et.size)
            with(PiezasPsicodelia) { disco(Offset(et.left + et.height * 0.55f, et.center.y), et.height * 0.38f, k, sombra = false) }
            listOf(0.38f, 0.62f).forEach { f -> drawLine(CAFE.copy(alpha = 0.55f), Offset(et.left + et.height * 1.05f, et.top + et.height * f), Offset(et.right - et.width * 0.1f, et.top + et.height * f), lw * 0.6f) }
            drawRect(Brush.verticalGradient(listOf(luzCiudad(pino, oscuro), pino, TrazosCiudad.enSombra(pino, 0.3f)), tapa.top, tapa.bottom), tapa.topLeft, tapa.size)
            with(PiezasPsicodelia) { granoPelicula(forma, k.grano * 1.2f) }
        } else {
            // La caja impresa: el frente en ultramar con trama, la tapa en la tinta que graba, la etiqueta con los rayos.
            val papel = tinta(TonoPsi.PAPEL, k, e.base)
            drawRect(k.ultramar, frente.topLeft, frente.size)
            if (fino) clipRect(frente.left + frente.width * 0.55f, frente.top, frente.right, frente.bottom) { trama(Path().apply { addRect(frente) }, max(lw * 1.6f, 1.2f), max(lw * 0.5f, 0.6f), linea.copy(alpha = 0.55f)) }
            drawLine(linea, l.o(0.07f, 0.868f), l.o(0.93f, 0.868f), lw * 1.4f)
            listOf(0.07f to 0.13f, 0.87f to 0.93f).forEach { (x0, x1) -> val r = l.r(x0, 0.79f, x1, 0.95f); drawRect(tinta(TonoPsi.GRABAR, k, e.base), r.topLeft, r.size) }
            val et = l.r(0.34f, 0.805f, 0.66f, 0.93f)
            drawRect(if (oscuro) tinta(TonoPsi.GRABAR, k, e.base) else papel, et.topLeft, et.size)
            clipRect(et.left, et.top, et.right, et.bottom) {
                with(PiezasPsicodelia) { corona(et.center, et.height * 0.3f, et.width * 0.62f, k, n = 18) }
                drawCircle(k.magenta, et.height * 0.28f, et.center)
            }
            drawRect(linea, et.topLeft, et.size, style = Stroke(lw * 0.7f))
            drawRect(tinta(TonoPsi.GRABAR, k, e.base), tapa.topLeft, tapa.size)
            drawRect(linea, tapa.topLeft, tapa.size, style = Stroke(lw * 0.6f))
            drawRect(linea, frente.topLeft, frente.size, style = Stroke(lw * 0.7f))
        }
        // Los flejes del metal del nivel, con sus clavos.
        listOf(0.155f, 0.845f).forEach { x ->
            val r = l.r(x - 0.022f, 0.79f, x + 0.022f, 0.95f)
            drawRect(Brush.horizontalGradient(listOf(e.metalOscuro, e.base, e.brillo, e.base, e.metalOscuro), r.left, r.right), r.topLeft, r.size)
            listOf(0.83f, 0.91f).forEach { y -> drawCircle(e.metalOscuro, max(h * 0.006f, 0.5f * u), l.o(x, y)) }
            if (!k.ciudad) drawRect(linea, r.topLeft, r.size, style = Stroke(lw * 0.5f))
        }
    }

    /** El gris translúcido de lo que aún no se gana. */
    private fun silueta(oscuro: Boolean, k: ColoresPsi): Color = (if (oscuro) TrazosTinta.CREMA else if (k.ciudad) CAFE else TrazosTinta.GRABAR).copy(alpha = 0.13f)

    /**
     * La pila de fundas de discos del hall: tres fundas acostadas y corridas; la de en medio, con el canto del metal del
     * nivel. En la ciudad, los colores apagados de la foto; en la tinta, impresas con sus bandas y su trama.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        if (w <= 2f || h <= 2f) return
        val k = k(oscuro)
        val capas = listOf(0.02f to 0.0f, 0.06f to 0.34f, 0.0f to 0.66f)
        if (!e.hecho) { drawPath(Path().apply { capas.forEach { (dx, y) -> addRect(Rect(w * dx, h * y, w * (0.98f - dx * 0.5f), h * (y + 0.32f))) } }, silueta(oscuro, k)); return }
        val linea = linea(k)
        capas.forEachIndexed { j, (dx, y) ->
            val r = Rect(w * dx, h * y, w * (0.98f - dx * 0.5f), h * (y + 0.32f))
            if (j == 1) {
                drawRect(Brush.horizontalGradient(listOf(e.metalOscuro, e.base, e.brillo, e.base, e.metalOscuro), r.left, r.right), r.topLeft, r.size)
            } else if (k.ciudad) {
                val c = if (j == 0) NARANJA else BOTELLA
                drawRect(Brush.horizontalGradient(listOf(luzCiudad(c, oscuro), c, TrazosCiudad.enSombra(c, 0.3f)), r.left, r.right), r.topLeft, r.size)
                drawRect(CARTON.copy(alpha = 0.7f), Offset(r.left + w * 0.08f, r.top), Size(w * 0.05f, r.height))
            } else {
                drawRect(if (j == 0) k.magenta else k.ultramar, r.topLeft, r.size)
                clipRect(r.left, r.top, r.right, r.bottom) {
                    with(PiezasPsicodelia) { ondas(r.left, r.right, r.center.y, r.height * 0.22f, w * 0.18f, max(0.8f * u, r.height * 0.12f), 0f,
                        listOf(TrazosTinta.Tinta.ULTRAMAR to tinta(TonoPsi.PAPEL, k, e.base)), k.m.registro * 0.4f, fase = j * 0.3f, adelgaza = false) }
                }
            }
            drawRect(if (k.ciudad) TrazosCiudad.SOMBRA.copy(alpha = 0.5f) else linea, r.topLeft, r.size, style = Stroke(0.5f * u))
        }
        if (k.ciudad) with(PiezasPsicodelia) { granoPelicula(null, k.grano) }
    }

    override val margenPlaca: Dp = 10.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (EstadoTema.cara == Cara.BASE) CAFE else TrazosTinta.GRABAR
    override fun rotuloPlaca(oscuro: Boolean): Color = if (EstadoTema.cara == Cara.BASE) NARANJA else Color(0xFF974164)

    /**
     * La entrada del baile: el cartón de esquinas redondas (siempre claro, también de noche: el nombre va encima en tinta
     * oscura), el talón de la izquierda con su línea de perforaciones y sus dos muescas, la franja de arriba y el borde del
     * metal del nivel. En la ciudad, una mancha de luz de la tarde y el grano; en la tinta, el talón impreso con sus rayos.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        if (w <= 2f || h <= 2f) return
        val k = k(oscuro)
        val m = min(2f * u, h * 0.06f); val rad = min(h * 0.18f, 6f * u)
        val r = Rect(m, m, w - m, h - m)
        val forma = Path().apply { addRoundRect(RoundRect(r, CornerRadius(rad))) }
        if (!e.hecho) { drawPath(forma, silueta(oscuro, k)); return }
        val carton = if (k.ciudad) CARTON else lerp(TrazosTinta.PAPEL, Color.White, 0.3f)
        val xt = r.left + min(w * 0.14f, 16f * u)
        translate(0.8f * u, 1.2f * u) { drawPath(forma, if (k.ciudad) TrazosCiudad.SOMBRA.copy(alpha = if (oscuro) 0.6f else 0.25f) else k.ultramar) }
        drawPath(forma, carton)
        clipPath(forma) {
            drawRect(if (k.ciudad) NARANJA else k.magenta, r.topLeft, Size(r.width, min(h * 0.12f, 4f * u)))
            if (k.ciudad) {
                drawRect(TrazosCiudad.enSombra(carton, 0.1f), Offset(r.left, r.top), Size(xt - r.left, r.height))
                drawRect(Brush.linearGradient(listOf(TrazosCiudad.AL_SOL.copy(alpha = 0.5f), Color.Transparent), r.topLeft, Offset(r.left + w * 0.4f, r.bottom)), r.topLeft, r.size)
                with(PiezasPsicodelia) { disco(Offset((r.left + xt) / 2, r.center.y + h * 0.05f), min((xt - r.left) * 0.32f, h * 0.2f), k, sombra = false) }
            } else {
                clipRect(r.left, r.top, xt, r.bottom) {
                    drawPath(PiezasPsicodelia.cunas(Offset((r.left + xt) / 2, r.center.y + h * 0.05f), (xt - r.left) * 0.25f, h * 0.7f, 14, 0.5f, 0.12f, 0f), k.ultramar)
                }
                drawCircle(k.magenta, min((xt - r.left) * 0.26f, h * 0.17f), Offset((r.left + xt) / 2, r.center.y + h * 0.05f))
            }
            drawLine((if (k.ciudad) CAFE else TrazosTinta.GRABAR).copy(alpha = 0.5f), Offset(xt, r.top + h * 0.14f), Offset(xt, r.bottom), 0.7f * u,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.4f * u, 1.6f * u)))
            if (k.ciudad) with(PiezasPsicodelia) { granoPelicula(null, k.grano) }
        }
        // Las dos muescas del talón.
        listOf(r.top to 0f, r.bottom to 180f).forEach { (y, a) -> drawArc((if (k.ciudad) CAFE else TrazosTinta.GRABAR).copy(alpha = 0.5f), a, 180f, false, Offset(xt - 2f * u, y - 2f * u), Size(4f * u, 4f * u), style = Stroke(0.6f * u)) }
        drawPath(forma, Brush.linearGradient(listOf(e.brillo, e.base, e.metalOscuro, e.base), Offset.Zero, Offset(w, h)), style = Stroke(1.4f * u))
        if (!k.ciudad) drawPath(Path().apply { addRoundRect(RoundRect(Rect(r.left + 2.6f * u, r.top + 2.6f * u, r.right - 2.6f * u, r.bottom - 2.6f * u), CornerRadius(max(rad - 2.6f * u, 1f)))) },
            TrazosTinta.GRABAR.copy(alpha = 0.5f), style = Stroke(0.5f * u))
    }

    // ================================================================ el salón del hall

    /** La geometría del salón: una pilastra en el borde de cada columna de trofeos, la cornisa arriba y el piso abajo. */
    private class GeoSalon(val w: Float, val h: Float, val u: Float, columnas: Int) {
        val n = columnas.coerceIn(1, 6)
        val paso = w / n
        val pilastra = min(paso * 0.08f, 12f * u)
        val cornisa = min(46f * u, h * 0.11f)
        val piso = min(26f * u, h * 0.08f)
        val suelo = h - piso
        fun borde(i: Int) = i * paso
    }

    /**
     * El salón: la sala de baile de la calle Sutter.
     * - Ciudad · claro: la sala de día, vacía, con la luz de la tarde que entra por las ventanas altas de la izquierda y cae en
     *   manchas sobre el muro de ladrillo pintado; las pilastras de madera con carteles viejos, desteñidos; la cornisa de
     *   madera arriba y el piso de tablas.
     * - Ciudad · oscuro: la misma sala de noche, con un aplique de pantalla en cada pilastra que deja su halo cálido.
     * - Tinta: la sala impresa como un cartel: en cada pilastra, dos ondas que bajan a dos tintas; en la cornisa, el marco de
     *   arco de cada columna con el disco hecho sol que asoma; el piso en ultramar con su trama. De noche, sobre el negro de
     *   terciopelo, las manchas planas de las luces líquidas en lo alto del muro.
     * Todo tenue sobre [EstadoSalon.fondo]; nada pasa por detrás de una columna de trofeos salvo el muro mismo.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val g = GeoSalon(size.width, size.height, density, e.columnas)
        if (g.w <= 2f || g.h <= 2f) return
        val k = k(oscuro)
        clipRect(0f, 0f, g.w, g.h) { if (k.ciudad) salonCiudad(g, e, k) else salonTinta(g, e, k) }
    }

    private fun DrawScope.salonCiudad(g: GeoSalon, e: EstadoSalon, k: ColoresPsi) {
        val w = g.w; val h = g.h; val u = g.u
        fun m(c: Color, f: Float) = lerp(e.fondo, c, f)
        // El muro de ladrillo pintado: las hiladas apenas marcadas.
        val junta = m(if (k.oscuro) Color.Black else TrazosCiudad.SOMBRA, 0.1f)
        var y = g.cornisa + 9 * u; var fila = 0
        while (y < g.suelo && fila < 400) {
            drawLine(junta, Offset(0f, y), Offset(w, y), 0.6f * u)
            var x = if (fila % 2 == 0) 0f else 13 * u
            while (x < w) { drawLine(junta, Offset(x, y - 9 * u), Offset(x, y), 0.6f * u); x += 26 * u }
            y += 9 * u; fila++
        }
        if (!k.oscuro) {
            // Las manchas de sol de las ventanas altas: paralelogramos que bajan hacia la derecha.
            for (i in 0 until g.n) {
                val x0 = g.borde(i) + g.paso * 0.18f
                val alto = min(g.suelo - g.cornisa, 420 * u)
                val mancha = Path().apply { moveTo(x0, g.cornisa); lineTo(x0 + g.paso * 0.3f, g.cornisa); lineTo(x0 + g.paso * 0.55f, g.cornisa + alto); lineTo(x0 + g.paso * 0.25f, g.cornisa + alto); close() }
                drawPath(mancha, Brush.verticalGradient(listOf(TrazosCiudad.SOL_VELADO.copy(alpha = 0.32f), TrazosCiudad.LUZ_BAJA.copy(alpha = 0.12f), Color.Transparent), g.cornisa, g.cornisa + alto))
            }
        }
        // Las pilastras de madera con los carteles viejos pegados.
        val madera = m(TrazosCiudad.MADERA, if (k.oscuro) 0.45f else 0.35f)
        for (i in 0..g.n) {
            val cx = g.borde(i)
            val r = Rect(cx - g.pilastra, g.cornisa, cx + g.pilastra, g.suelo)
            drawRect(Brush.horizontalGradient(listOf(lerp(madera, TrazosCiudad.AL_SOL, if (k.oscuro) 0.05f else 0.2f), madera, lerp(madera, TrazosCiudad.SOMBRA, 0.3f)), r.left, r.right), r.topLeft, r.size)
            var yc = g.cornisa + 14 * u; var kc = 0
            while (yc < g.suelo - 30 * u && kc < 40) {
                val alto = (30f + azar(e.semilla + i, kc) * 18f) * u
                val car = Rect(r.left + 1.4f * u, yc, r.right - 1.4f * u, min(yc + alto, g.suelo - 8 * u))
                val tono = listOf(Color(0xFFC8A060), Color(0xFFB0506E), Color(0xFF4A5A9E), Color(0xFFC8662A))[(i + kc) % 4]
                drawRect(m(TrazosCiudad.pelicula(tono), if (k.oscuro) 0.3f else 0.32f), car.topLeft, car.size)
                drawCircle(m(CARTON, if (k.oscuro) 0.25f else 0.4f), car.width * 0.28f, Offset(car.center.x, car.top + car.height * 0.6f))
                drawRect(m(CAFE, 0.25f), Offset(car.left + car.width * 0.12f, car.top + car.height * 0.12f), Size(car.width * 0.76f, car.height * 0.14f))
                yc += alto + 6 * u; kc++
            }
            if (k.oscuro) {
                // El aplique de pantalla y su halo cálido en el muro.
                val l = Offset(cx, g.cornisa + 70 * u)
                drawCircle(Brush.radialGradient(listOf(TrazosCiudad.LAMPARA_LUZ.copy(alpha = 0.22f), TrazosCiudad.LAMPARA_LUZ.copy(alpha = 0.06f), Color.Transparent), l, 120 * u), 120 * u, l)
                drawPath(Path().apply { moveTo(l.x - 6 * u, l.y - 8 * u); lineTo(l.x + 6 * u, l.y - 8 * u); lineTo(l.x + 9 * u, l.y + 4 * u); lineTo(l.x - 9 * u, l.y + 4 * u); close() }, m(TrazosCiudad.LAMPARA, 0.7f))
            }
        }
        // La cornisa de madera y el piso de tablas.
        drawRect(Brush.verticalGradient(listOf(m(TrazosCiudad.MADERA, 0.5f), m(TrazosCiudad.MADERA, 0.38f)), 0f, g.cornisa), Offset.Zero, Size(w, g.cornisa))
        drawLine(m(TrazosCiudad.AL_SOL, if (k.oscuro) 0.15f else 0.5f), Offset(0f, g.cornisa * 0.3f), Offset(w, g.cornisa * 0.3f), 1.2f * u)
        drawRect(Brush.verticalGradient(listOf(TrazosCiudad.SOMBRA.copy(alpha = 0.2f), Color.Transparent), g.cornisa, g.cornisa + 14 * u), Offset(0f, g.cornisa), Size(w, 14 * u))
        drawRect(m(lerp(TrazosCiudad.MADERA, TrazosCiudad.AL_SOL, 0.25f), if (k.oscuro) 0.4f else 0.45f), Offset(0f, g.suelo), Size(w, g.piso))
        var xt = 0f; var kt = 0
        while (xt < w && kt < 300) { drawLine(m(TrazosCiudad.SOMBRA, 0.2f), Offset(xt, g.suelo), Offset(xt - (xt - w / 2) * 0.3f, h), 0.6f * u); xt += 18f * u; kt++ }
        with(PiezasPsicodelia) { granoPelicula(null, k.grano * 0.8f) }
    }

    private fun DrawScope.salonTinta(g: GeoSalon, e: EstadoSalon, k: ColoresPsi) {
        val w = g.w; val h = g.h; val u = g.u
        fun m(c: Color, f: Float) = lerp(e.fondo, c, f)
        val grabado = if (k.oscuro) TrazosTinta.CREMA else TrazosTinta.GRABAR
        // De noche, las manchas de las luces líquidas en lo alto del muro, planas.
        if (k.oscuro) {
            val tintas = listOf(k.magenta, k.ultramar, lerp(k.magenta, k.bermellon, k.m.bermellon))
            for (i in 0 until g.n) for (j in 0..1) {
                val c = Offset(g.borde(i) + g.paso * (0.3f + 0.4f * j), g.cornisa + (60f + 50f * j + azar(e.semilla, i * 2 + j) * 60f) * u)
                val r = (38f + azar(e.semilla + 3, i * 2 + j) * 30f) * u
                val gota = Path().apply {
                    for (s in 0..30) {
                        val a = s * 2f * PI.toFloat() / 30
                        val rr = r * (1f + 0.2f * sin(a * 3f + i + j * 2f) + 0.08f * sin(a * 5f + j))
                        val p = Offset(c.x + rr * cos(a), c.y + rr * sin(a) * 0.8f)
                        if (s == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                    }
                    close()
                }
                drawPath(gota, m(tintas[(i + j) % 3], 0.11f + 0.05f * k.i))
            }
        }
        // Las pilastras: dos ondas que bajan, a dos tintas, corridas de registro.
        for (i in 0..g.n) {
            val cx = g.borde(i)
            val amp = (1.6f + 0.08f * k.m.amplitud) * u
            listOf(TrazosTinta.Tinta.ULTRAMAR to k.ultramar, TrazosTinta.Tinta.MAGENTA to k.magenta).forEachIndexed { j, (tt, c) ->
                conRegistro(tt, k.m.registro * 0.5f) {
                    drawPath(ondaVertical(cx + (j - 0.5f) * g.pilastra * 0.7f, g.cornisa, g.suelo, amp, 40f * u, j * 0.5f), m(c, 0.42f), style = Stroke(max(g.pilastra * 0.35f, 1.5f * u), cap = StrokeCap.Round))
                }
            }
        }
        // La cornisa: una banda en la tinta que graba con, en cada columna, el arco del cartel y el disco hecho sol que asoma.
        drawRect(m(grabado, if (k.oscuro) 0.22f else 0.3f), Offset.Zero, Size(w, g.cornisa))
        for (i in 0 until g.n) {
            val c = Offset(g.borde(i) + g.paso / 2, g.cornisa)
            clipRect(g.borde(i) + g.pilastra, 0f, g.borde(i + 1) - g.pilastra, g.cornisa) {
                drawPath(PiezasPsicodelia.cunas(c, g.cornisa * 0.45f, g.cornisa * 1.6f, 22, 0.5f, 0.12f, 0f), m(k.papel, 0.5f))
                drawCircle(m(k.magenta, 0.55f), g.cornisa * 0.42f, c)
                drawCircle(m(k.papel, 0.6f), g.cornisa * 0.14f, c)
            }
            val caja = Rect(g.borde(i) + g.pilastra * 1.6f, g.cornisa + 6 * u, g.borde(i + 1) - g.pilastra * 1.6f, g.suelo - 6 * u)
            if (caja.width > 8 * u && caja.height > 8 * u) drawPath(TrazosTinta.arco(caja, min(caja.width / 2, 60 * u), 2 * u), m(grabado, 0.18f), style = Stroke(1.2f * u))
        }
        // El piso, en ultramar con su trama.
        val piso = Rect(0f, g.suelo, w, h)
        drawRect(m(k.ultramar, 0.3f), piso.topLeft, piso.size)
        trama(Path().apply { addRect(piso) }, 3f * u, 0.8f * u, m(grabado, 0.25f))
        drawLine(m(grabado, 0.4f), Offset(0f, g.suelo), Offset(w, g.suelo), 1.2f * u)
    }

    // ================================================================ las campañas

    /**
     * El trolebús de la línea que sube a la calle Haight, de perfil y con el morro a la derecha: la caja de dos tonos con la
     * fila de ventanillas, las dos pértigas que suben a los cables y las ruedas; cuando hoy falta cumplir, da saltitos. En la
     * ciudad, el crema y el naranja quemado con su sombra café; en la tinta, impreso en magenta y ultramar con el contorno.
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density
        val k = k(oscuro)
        val s = e.salto.coerceIn(0f, 1f)
        val p = e.punto
        val f = 1f - 0.35f * s
        val linea = linea(k)
        drawOval((if (k.ciudad) TrazosCiudad.SOMBRA else linea).copy(alpha = (if (oscuro) 0.55f else 0.25f) * f), Offset(p.x - 13 * u * f, p.y - 1.6f * u * f), Size(26 * u * f, 3.2f * u * f))
        val y0 = p.y - 2.6f * u - s * 4f * u
        fun q(x: Float, y: Float) = Offset(p.x + x * u, y0 + y * u)
        val caja = Path().apply { addRoundRect(RoundRect(Rect(q(-12f, -13f), q(12f, -2f)), CornerRadius(3f * u))) }
        // Las pértigas, hacia atrás, hasta los cables.
        listOf(-3f, -6f).forEach { x -> drawLine(if (k.ciudad) TrazosCiudad.POSTE else linea, q(x, -13f), q(x - 7f, -21f), 0.6f * u, StrokeCap.Round) }
        if (k.ciudad) {
            translate(0.8f * u, 0.8f * u) { drawPath(caja, TrazosCiudad.SOMBRA.copy(alpha = 0.35f)) }
            drawPath(caja, Color(0xFFE6D8BC))
            clipPath(caja) { drawRect(NARANJA, q(-13f, -6f), Size(26f * u, 5f * u)) }
        } else {
            conRegistro(TrazosTinta.Tinta.MAGENTA, k.m.registro * 0.4f) { drawPath(caja, k.magenta) }
            clipPath(caja) { drawRect(k.ultramar, q(-13f, -6f), Size(26f * u, 5f * u)) }
        }
        // Las ventanillas y el parabrisas.
        val vidrio = if (k.ciudad) (if (oscuro) TrazosCiudad.LAMPARA.copy(alpha = 0.85f) else Color(0xFF6E7C7A)) else tinta(TonoPsi.PAPEL, k, e.acento)
        for (j in 0 until 4) drawRect(vidrio, q(-10.5f + j * 4.6f, -11.5f), Size(3.6f * u, 3.6f * u))
        drawRoundRect(vidrio, q(8.4f, -11.5f), Size(2.8f * u, 4.2f * u), CornerRadius(0.8f * u))
        drawPath(caja, if (k.ciudad) TrazosCiudad.SOMBRA.copy(alpha = 0.6f) else linea, style = Stroke(0.6f * u))
        // Las ruedas.
        listOf(-7f, 7f).forEach { x -> drawCircle(if (k.ciudad) VINILO else linea, 2.3f * u, q(x, -1.6f)); drawCircle(if (k.ciudad) Color(0xFFB8AE9C) else k.papel, 0.8f * u, q(x, -1.6f)) }
    }

    /**
     * La llegada a Monterey: el escenario del festival en chico, la tarima, las dos pilas de altavoces y el telón. En la
     * ciudad, sobrio, con la luz de la tarde; en la tinta, el disco hecho sol que sale detrás del telón con su corona de rayos
     * y, con la intensidad, sus anillos.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val k = k(oscuro)
        val ancho = 44f * u; val alto = 46f * u; val mg = 2f * u
        val x = if (size.width > ancho + 2 * mg) e.meta.x.coerceIn(ancho / 2 + mg, size.width - ancho / 2 - mg) else size.width / 2
        val y = if (size.height > alto + 2 * mg) (e.meta.y - 2f * u).coerceIn(alto + mg, size.height - mg) else size.height - mg
        val linea = linea(k)
        drawOval((if (k.ciudad) TrazosCiudad.SOMBRA else linea).copy(alpha = if (oscuro) 0.5f else 0.2f), Offset(x - 20 * u, y - 1.6f * u), Size(40 * u, 3.2f * u))
        val sol = Offset(x, y - 24f * u)
        if (!k.ciudad) {
            with(PiezasPsicodelia) {
                corona(sol, 8f * u, 22f * u, k, n = 20)
                for (j in 1..k.m.anillos.coerceAtMost(4)) drawCircle((if (j % 2 == 0) k.ultramar else k.magenta), (8f + j * 2.6f) * u, sol, style = Stroke(0.8f * u))
                disco(sol, 8f * u, k, etiqueta = e.acento)
            }
        }
        // El telón y la tarima.
        val telon = Rect(x - 13 * u, y - 22 * u, x + 13 * u, y - 6 * u)
        drawRect(if (k.ciudad) (if (oscuro) Color(0xFF3A2A20) else BOTELLA) else k.ultramar, telon.topLeft, telon.size)
        if (k.ciudad) {
            var xx = telon.left + 2 * u; while (xx < telon.right) { drawLine(TrazosCiudad.SOMBRA.copy(alpha = 0.35f), Offset(xx, telon.top), Offset(xx, telon.bottom), 0.6f * u); xx += 3 * u }
            with(PiezasPsicodelia) { disco(Offset(x, telon.center.y), 5f * u, k, sombra = false, etiqueta = e.acento) }
        } else trama(Path().apply { addRect(telon) }, 1.6f * u, 0.5f * u, linea.copy(alpha = 0.5f))
        drawRect(if (k.ciudad) TrazosCiudad.MADERA else tinta(TonoPsi.GRABAR, k, e.acento), Offset(x - 16 * u, y - 6 * u), Size(32 * u, 6 * u))
        drawLine(if (k.ciudad) TrazosCiudad.alSol(TrazosCiudad.MADERA, 0.5f) else linea, Offset(x - 16 * u, y - 6 * u), Offset(x + 16 * u, y - 6 * u), 0.7f * u)
        // Las dos pilas de altavoces.
        listOf(-1f, 1f).forEach { s ->
            val x0 = x + s * 18f * u - 4f * u
            for (j in 0 until 3) {
                val r = Rect(x0, y - (6f + j * 6f) * u, x0 + 8f * u, y - j * 6f * u)
                drawRect(if (k.ciudad) Color(0xFF2E2620) else tinta(TonoPsi.GRABAR, k, e.acento), r.topLeft, r.size)
                drawCircle(if (k.ciudad) Color(0xFF5E5246) else k.magenta, 2.2f * u, r.center)
                drawRect(if (k.ciudad) Color.Black.copy(alpha = 0.5f) else linea, r.topLeft, r.size, style = Stroke(0.4f * u))
            }
        }
        drawRect(if (k.ciudad) TrazosCiudad.SOMBRA.copy(alpha = 0.5f) else linea, telon.topLeft, telon.size, style = Stroke(0.5f * u))
    }

    // ================================================================ objetos de Tu año

    /**
     * Los objetos de Tu año: la luna, la creciente sobre la niebla que entra por el Golden Gate (dos líneas que ondulan); la
     * huella, la suela de una bota de ante con su dibujo de rayas; la pesa, la mancuerna con los discos como discos de
     * vinilo, con su etiqueta calada. Siluetas claras, iguales en las dos caras (son íconos de dato).
     */
    override fun DrawScope.objetoAnio(e: EstadoObjeto, oscuro: Boolean) {
        val r = e.radio; val c = e.centro; val u = density
        if (r <= 0.5f || !r.isFinite()) return
        val g = max(r * 0.1f, 0.8f * u)
        when (e.forma) {
            "luna" -> {
                val luna = Path().apply { addOval(Rect(Offset(c.x - r * 0.05f, c.y - r * 0.2f), r * 0.7f)) }
                clipPath(Path().apply { addOval(Rect(Offset(c.x + r * 0.3f, c.y - r * 0.42f), r * 0.6f)) }, androidx.compose.ui.graphics.ClipOp.Difference) { drawPath(luna, e.color) }
                listOf(0.62f to 1f, 0.86f to 0.7f).forEach { (dy, a) ->
                    drawPath(Path().apply {
                        moveTo(c.x - r * a, c.y + r * dy)
                        quadraticTo(c.x - r * a * 0.5f, c.y + r * (dy - 0.18f), c.x, c.y + r * dy)
                        quadraticTo(c.x + r * a * 0.5f, c.y + r * (dy + 0.18f), c.x + r * a, c.y + r * dy)
                    }, e.color, style = Stroke(g * 1.1f, cap = StrokeCap.Round))
                }
            }
            "huella" -> {
                // La suela de la bota de ante: la planta ancha, el arco y el tacón aparte.
                val planta = Path().apply {
                    moveTo(c.x - r * 0.02f, c.y - r * 1f)
                    cubicTo(c.x + r * 0.42f, c.y - r * 1f, c.x + r * 0.48f, c.y - r * 0.45f, c.x + r * 0.36f, c.y - r * 0.05f)
                    cubicTo(c.x + r * 0.3f, c.y + r * 0.18f, c.x - r * 0.3f, c.y + r * 0.18f, c.x - r * 0.36f, c.y - r * 0.05f)
                    cubicTo(c.x - r * 0.46f, c.y - r * 0.5f, c.x - r * 0.4f, c.y - r * 1f, c.x - r * 0.02f, c.y - r * 1f); close()
                }
                drawPath(planta, e.color)
                drawRoundRect(e.color, Offset(c.x - r * 0.3f, c.y + r * 0.38f), Size(r * 0.6f, r * 0.6f), CornerRadius(r * 0.22f))
                if (r >= 6f * u) {
                    listOf(-0.75f, -0.5f, -0.25f).forEach { dy -> drawLine(e.fondo, Offset(c.x - r * 0.3f, c.y + dy * r), Offset(c.x + r * 0.3f, c.y + dy * r), g * 0.6f) }
                    drawLine(e.fondo, Offset(c.x - r * 0.2f, c.y + r * 0.68f), Offset(c.x + r * 0.2f, c.y + r * 0.68f), g * 0.6f)
                }
            }
            "pesa" -> {
                drawLine(e.color, Offset(c.x - r * 0.62f, c.y), Offset(c.x + r * 0.62f, c.y), r * 0.18f, StrokeCap.Round)
                listOf(-1f, 1f).forEach { s ->
                    val o = Offset(c.x + s * r * 0.6f, c.y)
                    drawCircle(e.color, r * 0.4f, o)
                    if (r >= 6f * u) {
                        drawCircle(e.fondo, r * 0.3f, o, style = Stroke(r * 0.03f))
                        drawCircle(e.fondo, r * 0.14f, o, style = Stroke(r * 0.04f))
                    }
                }
            }
            else -> {}
        }
    }
}
