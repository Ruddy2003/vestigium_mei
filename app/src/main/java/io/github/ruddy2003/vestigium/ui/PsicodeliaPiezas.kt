package io.github.ruddy2003.vestigium.ui

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Density
import io.github.ruddy2003.vestigium.ui.TrazosCiudad.cables
import io.github.ruddy2003.vestigium.ui.TrazosTinta.bandas
import io.github.ruddy2003.vestigium.ui.TrazosTinta.conRegistro
import io.github.ruddy2003.vestigium.ui.TrazosTinta.trama
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/*
 * Psicodelia v2, tanda 2: las piezas de dibujo que comparten la decoración y los componentes del tema (el emblema, la banda
 * bajo los títulos, el fondo vivo, las esquinas, los controles…) y `comun/ComunPsicodelia.kt`. Cada pieza tiene una sola
 * geometría y dos pintores (`docs/diseno/temas/psicodelia.md`):
 * - la **ciudad** (cara BASE): luz de la izquierda, sombra corta de color café, grano de película; sobria, nada brilla;
 * - la **tinta** (cara INTENSA): tintas planas (magenta, ultramar y, con la intensidad, el bermellón), la tinta que graba,
 *   rayos y bandas que ondulan, el registro que se corre; la intensidad calienta los tres colores de rol.
 * Todo es puro `DrawScope` (sin Android): el arnés de escritorio lo dibuja (`herramientas/arnes_muebles`, modo `comun`).
 *
 * Reglas: ningún texto se dibuja aquí sobre un motivo sin placa opaca debajo; ningún bucle con paso que pueda valer 0;
 * ninguna coordenada no finita (los tamaños vacíos no dibujan nada).
 */

/**
 * Los colores de una variante de Psicodelia ([oscuro] y [cara]) con la intensidad [i] ya aplicada (en la cara de la tinta;
 * la ciudad no la lee). Los papeles de la interfaz salen de `PaletasPsicodelia` (no cambian con [i]); el primario, el
 * secundario y el terciario de la tinta, de [TrazosTinta.rol]; las tintas de dibujo, de [TrazosTinta.tinta].
 */
internal class ColoresPsi(val oscuro: Boolean, val cara: Cara, val i: Float) {
    private val p = PaletasPsicodelia.de(oscuro, cara)
    val ciudad: Boolean get() = cara == Cara.BASE
    val fondo = Color(p.fondo)
    val superficie = Color(p.superficie)
    val variante = Color(p.variante)
    val borde = Color(p.borde)
    val tinta = Color(p.tinta)
    val tenue = Color(p.tenue)
    val error = Color(p.error)
    val contenedor = Color(p.contenedor)
    val sobreContenedor = Color(p.sobreContenedor)
    val contSecundario = Color(p.contSecundario)
    val sobrePrimario = Color(p.sobrePrimario)
    val primario = if (cara == Cara.INTENSA) TrazosTinta.rol(TrazosTinta.Rol.PRIMARIO, oscuro, i) else Color(p.primario)
    val secundario = if (cara == Cara.INTENSA) TrazosTinta.rol(TrazosTinta.Rol.SECUNDARIO, oscuro, i) else Color(p.secundario)
    val terciario = if (cara == Cara.INTENSA) TrazosTinta.rol(TrazosTinta.Rol.TERCIARIO, oscuro, i) else Color(p.terciario)

    // La tinta: las tres tintas de dibujo, la que graba y el papel.
    val magenta = TrazosTinta.tinta(TrazosTinta.Tinta.MAGENTA, oscuro, i)
    val bermellon = TrazosTinta.tinta(TrazosTinta.Tinta.BERMELLON, oscuro, i)
    val ultramar = TrazosTinta.tinta(TrazosTinta.Tinta.ULTRAMAR, oscuro, i)
    val grabado = TrazosTinta.grabado(oscuro)
    val papel = TrazosTinta.papel(oscuro)
    val m: TrazosTinta.Medidas = TrazosTinta.medidas(i)

    /** La sombra corta de la ciudad (luz de la izquierda: cae abajo a la derecha), café y no gris. */
    val sombra: Color = if (oscuro) Color.Black.copy(alpha = 0.42f) else TrazosCiudad.SOMBRA.copy(alpha = 0.2f)
    /** El brillo de lo que mira a la luz (la tarde de la ventana; de noche, la lámpara). */
    val luz: Color = if (oscuro) TrazosCiudad.LAMPARA.copy(alpha = 0.12f) else TrazosCiudad.AL_SOL.copy(alpha = 0.55f)
    /** El grano de película de la ciudad. */
    val grano: Float = if (oscuro) TrazosCiudad.GRANO_NOCHE else TrazosCiudad.GRANO_DIA
}

/** Las piezas de Psicodelia (ver la cabecera del archivo). */
internal object PiezasPsicodelia {
    private val colores = HashMap<Triple<Boolean, Cara, Float>, ColoresPsi>()

    /**
     * Los colores de una variante, en la intensidad en escalones ([EstadoTema.escalon]): así lo que depende de ella cambia
     * en nueve pasos y se puede guardar en caché. Por omisión, la cara y la intensidad de [EstadoTema] (leerlas dentro de
     * un dibujo hace que Compose lo vuelva a dibujar cuando cambian).
     */
    fun colores(oscuro: Boolean, cara: Cara = EstadoTema.cara, i: Float = EstadoTema.intensidad): ColoresPsi {
        val e = if (cara == Cara.BASE) 0f else EstadoTema.escalon(i)
        return colores.getOrPut(Triple(oscuro, cara, e)) { ColoresPsi(oscuro, cara, e) }
    }

    // ---------------------------------------------------------------------------------------------- caché

    private var granoTile: ImageBitmap? = null
    private var granoDensidad = 0f

    /**
     * El grano de película de la ciudad sobre [forma] (o sobre todo el lienzo), con [fuerza] ([ColoresPsi.grano]). Se dibuja
     * una sola baldosa de 128 dp (una vez por densidad, con `TrazosCiudad.grano`) y se repite: es barato en cada cuadro. Sobre
     * las placas de la interfaz va a un 40 % de la fuerza de la lámina (que es la de un cuadro entero): más se veía sucio.
     */
    fun DrawScope.granoPelicula(forma: Path?, fuerza: Float) {
        if (fuerza <= 0f || size.width <= 0f || size.height <= 0f) return
        val tile = granoTile.takeIf { granoDensidad == density } ?: run {
            val lado = (128f * density).roundToInt().coerceIn(16, 1024)
            val img = ImageBitmap(lado, lado)
            CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(img), Size(lado.toFloat(), lado.toFloat())) {
                with(TrazosCiudad) { grano(1f, semilla = 7) }
            }
            granoTile = img; granoDensidad = density
            img
        }
        val pincel = ShaderBrush(ImageShader(tile, TileMode.Repeated, TileMode.Repeated))
        if (forma == null) drawRect(pincel, alpha = (fuerza * 0.4f).coerceIn(0f, 1f)) else drawPath(forma, pincel, alpha = (fuerza * 0.4f).coerceIn(0f, 1f))
    }

    /** Lo guardado en caché: cada imagen con su clave, de la más vieja a la más nueva. Con un tope de píxeles. */
    private val cache = LinkedHashMap<List<Any>, ImageBitmap>(16, 0.75f, true)
    private var pixeles = 0L
    private const val TOPE_PIXELES = 4_000_000L

    /**
     * Dibuja [bloque] guardado en caché con la [clave] (que debe llevar todo lo que lo cambia: claro u oscuro, la cara, el
     * escalón de la intensidad, el estado de la pieza…), más el tamaño y la densidad. Lo que no cambia no se vuelve a
     * trazar: se pega la imagen. Lo muy grande se dibuja directo. Solo para lo que no se mueve.
     */
    fun DrawScope.enCache(clave: List<Any>, bloque: DrawScope.() -> Unit) {
        val w = ceil(size.width).toInt(); val h = ceil(size.height).toInt()
        val px = w.toLong() * h
        if (w <= 0 || h <= 0 || px > TOPE_PIXELES / 3) { bloque(); return }
        val k = clave + listOf(w, h, density)
        val img = cache[k] ?: ImageBitmap(w, h).also { nueva ->
            CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(nueva), Size(w.toFloat(), h.toFloat())) { bloque() }
            cache[k] = nueva; pixeles += px
            val it = cache.entries.iterator()
            while (pixeles > TOPE_PIXELES && it.hasNext()) {
                val e = it.next()
                if (e.key == k) continue
                pixeles -= e.value.width.toLong() * e.value.height; it.remove()
            }
        }
        drawImage(img)
    }

    // ---------------------------------------------------------------------------------------------- figuras

    /**
     * El disco del día, en [c] con radio [r], la figura central del tema en las dos caras. La ciudad: el vinilo negro
     * levantado hacia el café, con sus surcos, el brillo de la tarde arriba a la izquierda, su sombra corta y la etiqueta
     * ([etiqueta], por omisión la naranja) con sus dos renglones impresos, girada [giro] grados. La tinta: el disco impreso
     * (en el papel, el aro de ultramar y los surcos de tinta de grabar; en el negro, el crema con su aro de terciopelo) con
     * la etiqueta magenta, corrida de registro con la intensidad.
     */
    fun DrawScope.disco(c: Offset, r: Float, k: ColoresPsi, giro: Float = 0f, etiqueta: Color? = null, sombra: Boolean = true) {
        if (r <= 0.5f || !r.isFinite()) return
        val u = density
        if (k.ciudad) {
            if (sombra) drawCircle(k.sombra, r, c + Offset(r * 0.06f + 0.5f * u, r * 0.09f + 0.6f * u))
            val vinilo = Color(0xFF1E1916)
            drawCircle(vinilo, r, c)
            val n = if (r > 14 * u) 5 else 2
            for (j in 1..n) drawCircle(Color.White.copy(alpha = 0.07f), r * (0.46f + 0.48f * j / (n + 1)), c, style = Stroke(max(0.35f * u, r * 0.012f)))
            val arco = Rect(c, r * 0.8f)
            drawArc(TrazosCiudad.AL_SOL.copy(alpha = 0.38f), 196f, 52f, false, arco.topLeft, arco.size, style = Stroke(max(0.6f * u, r * 0.06f), cap = StrokeCap.Round))
            drawArc(TrazosCiudad.AL_SOL.copy(alpha = 0.12f), 16f, 40f, false, arco.topLeft, arco.size, style = Stroke(max(0.5f * u, r * 0.04f), cap = StrokeCap.Round))
            val col = etiqueta ?: TrazosCiudad.ETIQUETA
            val re = r * 0.38f
            drawCircle(col, re, c)
            if (re > 4 * u) rotate(giro, c) {
                val renglon = TrazosCiudad.enSombra(col, 0.5f)
                drawLine(renglon, Offset(c.x - re * 0.55f, c.y - re * 0.42f), Offset(c.x + re * 0.55f, c.y - re * 0.42f), max(0.5f * u, re * 0.09f))
                drawLine(renglon, Offset(c.x - re * 0.35f, c.y + re * 0.45f), Offset(c.x + re * 0.35f, c.y + re * 0.45f), max(0.4f * u, re * 0.07f))
            }
            drawCircle(TrazosCiudad.alSol(col, 0.3f).copy(alpha = 0.5f), re, c, style = Stroke(max(0.3f * u, re * 0.04f)))
            drawCircle(vinilo, max(r * 0.05f, 0.6f * u), c)
        } else {
            val reg = k.m.registro
            if (k.oscuro) {
                drawCircle(TrazosTinta.CREMA, r * 0.9f, c)
                drawCircle(k.papel, r * 0.95f, c, style = Stroke(r * 0.1f))
                val n = if (r > 14 * u) 4 else 2
                for (j in 1..n) drawCircle(k.papel.copy(alpha = 0.35f), r * (0.46f + 0.4f * j / (n + 1)), c, style = Stroke(max(0.35f * u, r * 0.014f)))
            } else {
                drawCircle(k.ultramar, r, c)
                drawCircle(k.papel, r * 0.86f, c)
                val n = if (r > 14 * u) 5 else 2
                for (j in 1..n) drawCircle(k.grabado, r * (0.44f + 0.4f * j / (n + 1)), c, style = Stroke(max(0.35f * u, r * 0.016f)))
            }
            conRegistro(TrazosTinta.Tinta.MAGENTA, reg * 0.5f) { drawCircle(etiqueta ?: k.magenta, r * 0.36f, c) }
            drawCircle(k.grabado.let { if (k.oscuro) k.papel else it }, max(r * 0.05f, 0.6f * u), c)
        }
    }

    /**
     * La corona de rayos que ondulan alrededor del disco hecho sol, de [r0] a [r1], [n] rayos (la mitad se llenan), girada
     * [giro] grados: en el papel, en la tinta que graba; en el negro, en magenta; con la intensidad, una segunda corona en
     * ultramar corrida media vuelta de rayo, y la del bermellón detrás, corrida de registro.
     */
    fun DrawScope.corona(c: Offset, r0: Float, r1: Float, k: ColoresPsi, giro: Float = 0f, n: Int = 24) {
        if (r1 <= r0 || r0 < 0f || !r1.isFinite()) return
        val nn = max(n - n % 2, 4)
        val sector = 2f * PI.toFloat() / nn
        // Cada rayo es una cuña que ondula una vez a lo largo (una S suave), desplazada como mucho un tercio de su sector: los
        // rayos largos del cartel, no pétalos.
        val ondea = sector * (0.3f + 0.15f * k.i)
        val fase = giro * PI.toFloat() / 180f
        val principal = if (k.oscuro) TrazosTinta.CREMA else k.grabado
        if (k.m.bermellon > 0f) conRegistro(TrazosTinta.Tinta.BERMELLON, k.m.registro) {
            drawPath(cunas(c, r0, r1 * 0.94f, nn, 0.22f, ondea, fase + sector * 0.62f), k.bermellon.copy(alpha = k.m.bermellon))
        }
        drawPath(cunas(c, r0, r1, nn, 0.5f, ondea, fase), principal)
        conRegistro(TrazosTinta.Tinta.MAGENTA, k.m.registro) {
            drawPath(cunas(c, r0 + (r1 - r0) * 0.35f, r1, nn, 0.16f + 0.1f * k.i, ondea, fase + sector * 0.5f + sector * 0.12f),
                (if (k.oscuro) k.magenta else k.ultramar).copy(alpha = 0.95f))
        }
    }

    /**
     * [n] cuñas (rayos) alrededor de [c], de [r0] a [r1], cada una de [lleno] de su sector, que ondulan una vez a lo largo
     * desplazándose [ondea] radianes como mucho, giradas [fase] radianes. Para la corona del disco hecho sol.
     */
    fun cunas(c: Offset, r0: Float, r1: Float, n: Int, lleno: Float, ondea: Float, fase: Float): Path {
        val p = Path()
        if (n < 2 || r1 <= r0 || r0 < 0f || !r1.isFinite()) return p
        val sector = 2f * PI.toFloat() / n
        val ancho = sector * lleno.coerceIn(0.02f, 1f)
        val pasos = 24
        val dos = 2f * PI.toFloat()
        for (k in 0 until n) {
            val a0 = fase + sector * k
            for (s in 0..pasos) {
                val f = s.toFloat() / pasos
                val r = r0 + (r1 - r0) * f
                val a = a0 + ondea * sin(dos * f * 0.9f) - ancho * (0.5f + 0.25f * f) / 2f
                val x = c.x + r * cos(a); val y = c.y + r * sin(a)
                if (s == 0) p.moveTo(x, y) else p.lineTo(x, y)
            }
            for (s in pasos downTo 0) {
                val f = s.toFloat() / pasos
                val r = r0 + (r1 - r0) * f
                val a = a0 + ondea * sin(dos * f * 0.9f) + ancho * (0.5f + 0.25f * f) / 2f
                p.lineTo(c.x + r * cos(a), c.y + r * sin(a))
            }
            p.close()
        }
        return p
    }

    // ---------------------------------------------------------------------------------------------- el emblema

    /**
     * El emblema del encabezado (Decoracion, `dibujarEmblema`), en un cuadrado de radio [r], con [t] de 0 a 1 en cada ciclo.
     * La ciudad: el disco del día visto de cerca, la etiqueta que gira una vuelta por ciclo mientras el brillo de la tarde
     * se queda quieto (es la luz, no el disco). La tinta: el disco hecho sol, con su corona de rayos que gira una vuelta por
     * ciclo (16 rayos: cierra sin salto) y, con la intensidad, sus anillos alternos.
     */
    fun DrawScope.emblema(c: Offset, r: Float, t: Float, oscuro: Boolean, cara: Cara = EstadoTema.cara, i: Float = EstadoTema.intensidad) {
        if (r <= 1f) return
        val k = colores(oscuro, cara, i)
        if (k.ciudad) disco(c, r * 0.9f, k, giro = t * 360f)
        else {
            corona(c, r * 0.5f, r, k, giro = t * 360f / 2f, n = 16)
            disco(c, r * 0.5f, k, giro = 0f)
        }
    }

    // ---------------------------------------------------------------------------------------------- bandas y filetes

    /**
     * Los dos cables del trolebús de [x0] a [x1] a la altura [y], que cuelgan un poco entre los colgadores (uno cada
     * [tramo] px como mucho), con el aislador de cada colgador. Discretos: en [color].
     */
    fun DrawScope.cablesTrolebus(x0: Float, x1: Float, y: Float, color: Color, tramo: Float = 120f * density, grosor: Float = 0.9f * density) {
        val largo = x1 - x0
        if (largo < 8f * density || !largo.isFinite()) return
        val n = max(1, ceil(largo / max(tramo, 20f * density)).toInt())
        val paso = largo / n
        val sep = 3.2f * density
        for (j in 0 until n) {
            val a = x0 + j * paso; val b = a + paso
            cables(Offset(a, y - sep / 2), Offset(b, y - sep / 2), comba = min(1.4f * density, paso * 0.02f), separacion = sep, grosor = grosor, color = color)
        }
        for (j in 0..n) {
            val x = x0 + j * paso
            if (j == 0 || j == n) continue
            drawLine(color, Offset(x, y - sep / 2 - 2.2f * density), Offset(x, y + sep / 2 + 0.6f * density), grosor * 0.9f)
            drawCircle(color, 1.1f * density, Offset(x, y - sep / 2))
        }
    }

    /**
     * Unas líneas que ondulan, en paralelo, de [x0] a [x1] alrededor de [y], de [amp] px de amplitud y [periodo] px de onda,
     * corridas [fase] vueltas: la banda de cartel. Se dibujan en las tintas [tintas] (de la de atrás a la de delante), cada
     * una separada [sep] px de la anterior y corrida de registro como su tinta.
     */
    fun DrawScope.ondas(x0: Float, x1: Float, y: Float, amp: Float, periodo: Float, grosor: Float, sep: Float, tintas: List<Pair<TrazosTinta.Tinta, Color>>,
                        registro: Float, fase: Float = 0f, adelgaza: Boolean = true) {
        val largo = x1 - x0
        if (largo < 4f * density || periodo <= 0f || grosor <= 0f || !largo.isFinite()) return
        val paso = max(1.5f * density, 0.5f)
        val dos = 2f * PI.toFloat()
        tintas.forEachIndexed { j, (t, col) ->
            val dy = (j - (tintas.size - 1) / 2f) * sep
            conRegistro(t, registro) {
                val p = Path()
                var x = x0; var primero = true
                while (x <= x1 + 0.01f) {
                    val f = (x - x0) / largo
                    val env = if (adelgaza) sin(PI.toFloat() * f).coerceAtLeast(0f) else 1f
                    val yy = y + dy + amp * env * sin(dos * ((x - x0) / periodo + fase) + j * 0.6f)
                    if (primero) { p.moveTo(x, yy); primero = false } else p.lineTo(x, yy)
                    x += paso
                }
                drawPath(p, col, style = Stroke(grosor, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }
    }

    /** Las tintas que lleva una banda: magenta y ultramar; con la intensidad, también el bermellón (detrás). */
    fun tintasBanda(k: ColoresPsi): List<Pair<TrazosTinta.Tinta, Color>> = buildList {
        if (k.m.bermellon > 0f) add(TrazosTinta.Tinta.BERMELLON to k.bermellon.copy(alpha = k.m.bermellon))
        add(TrazosTinta.Tinta.ULTRAMAR to k.ultramar)
        add(TrazosTinta.Tinta.MAGENTA to k.magenta)
    }

    /**
     * La banda bajo cada título (`Meandro`), del tamaño del lienzo (a todo el ancho, 13 dp). La ciudad: los cables del
     * trolebús con sus colgadores y, al principio, la etiqueta del disco. La tinta: las bandas de cartel que se abren
     * alrededor del disco chico del centro, más hondas con la intensidad (hasta 2.6 dp: es una banda, no el cartel).
     */
    fun DrawScope.bandaTitulo(oscuro: Boolean, cara: Cara = EstadoTema.cara, i: Float = EstadoTema.intensidad) {
        val k = colores(oscuro, cara, i)
        val u = density; val w = size.width; val cy = size.height / 2
        if (w < 40 * u) return
        if (k.ciudad) {
            cablesTrolebus(16 * u, w - 4 * u, cy, k.tenue.copy(alpha = 0.75f))
            disco(Offset(7 * u, cy), 5.4f * u, k, sombra = true)
        } else {
            val medio = w / 2; val hueco = 10 * u
            val amp = (1.2f + 0.05f * k.m.amplitud).coerceAtMost(2.6f) * u
            val tintas = tintasBanda(k)
            ondas(4 * u, medio - hueco, cy, amp, 26 * u, 1.3f * u, 2.6f * u, tintas, k.m.registro * 0.5f)
            ondas(medio + hueco, w - 4 * u, cy, amp, 26 * u, 1.3f * u, 2.6f * u, tintas, k.m.registro * 0.5f, fase = 0.5f)
            disco(Offset(medio, cy), 5.6f * u, k)
        }
    }

    /**
     * El subrayado de un título de sección, de 0 a [fin] a la altura [y]. La ciudad: el filete de periódico, grueso al
     * principio y fino después, como bajo un titular. La tinta: una onda a dos tintas que acaba en un disco chico.
     */
    fun DrawScope.subrayado(fin: Float, y: Float, k: ColoresPsi) {
        val u = density
        if (fin < 12 * u) return
        if (k.ciudad) {
            val grueso = min(fin * 0.32f, 64 * u)
            drawLine(k.primario, Offset(0f, y), Offset(grueso, y), 2.2f * u)
            drawLine(k.tenue.copy(alpha = 0.6f), Offset(grueso + 3 * u, y + 0.6f * u), Offset(fin, y + 0.6f * u), 0.8f * u)
        } else {
            ondas(0f, fin - 6 * u, y, 1.1f * u + 0.06f * k.m.amplitud * u, 22 * u, 1.5f * u, 2.4f * u,
                listOf(TrazosTinta.Tinta.ULTRAMAR to k.ultramar, TrazosTinta.Tinta.MAGENTA to k.magenta), k.m.registro * 0.4f, adelgaza = false)
            disco(Offset(fin, y), 3.2f * u, k, sombra = false)
        }
    }

    /**
     * El filete que separa (en los diálogos y en las listas), centrado en [cy], de [x0] a [x1]. La ciudad: los cables con su
     * colgador en medio. La tinta: dos ondas que llegan al disco del centro. [f] apaga (los separadores tenues).
     */
    fun DrawScope.filete(x0: Float, x1: Float, cy: Float, k: ColoresPsi, f: Float = 1f) {
        val u = density
        if (x1 - x0 < 24 * u) return
        val medio = (x0 + x1) / 2
        if (k.ciudad) {
            cablesTrolebus(x0, x1, cy, k.tenue.copy(alpha = 0.6f * f), tramo = (x1 - x0) / 2f + 1f)
        } else {
            val tintas = listOf(TrazosTinta.Tinta.ULTRAMAR to k.ultramar.copy(alpha = 0.8f * f), TrazosTinta.Tinta.MAGENTA to k.magenta.copy(alpha = 0.8f * f))
            ondas(x0, medio - 8 * u, cy, 1.1f * u, 20 * u, 1.1f * u, 2.2f * u, tintas, k.m.registro * 0.4f)
            ondas(medio + 8 * u, x1, cy, 1.1f * u, 20 * u, 1.1f * u, 2.2f * u, tintas, k.m.registro * 0.4f, fase = 0.5f)
            disco(Offset(medio, cy), 4.2f * u, k, sombra = false)
        }
    }

    // ---------------------------------------------------------------------------------------------- el fondo vivo

    /**
     * El fondo vivo (Animaciones, `VivoTema.LUCES`), con [t] de 0 a 1 en cada vuelta de 60 s y [fuerza] (la opacidad que
     * pide la pantalla). Todo muy tenue: va detrás de todo.
     * - Ciudad · claro: la mancha de sol de la ventana, con la sombra de sus dos barrotes, que se corre despacio por la pared
     *   y vuelve (una ida y vuelta por vuelta).
     * - Ciudad · oscuro: el cono de la lámpara arriba a la izquierda, que respira un 3 %.
     * - Tinta: las bandas del cartel que respiran (2 dp, cinco veces por vuelta) y, con la intensidad, fluyen (las bandas por
     *   segundo de [TrazosTinta.Medidas], redondeadas a vueltas enteras para que el ciclo cierre); arriba a la derecha, el
     *   disco hecho sol que gira (sus 24 rayos repiten cada 15°: el giro se redondea a eso).
     */
    fun DrawScope.fondoVivo(t: Float, oscuro: Boolean, fuerza: Float, cara: Cara = EstadoTema.cara, i: Float = EstadoTema.intensidad) {
        val w = size.width; val h = size.height; val u = density
        if (w <= 0f || h <= 0f || fuerza <= 0f) return
        val k = colores(oscuro, cara, i)
        val dos = 2f * PI.toFloat()
        if (k.ciudad) {
            if (!oscuro) {
                // La mancha de sol: un paralelogramo de luz que entra de la izquierda, con la sombra de dos barrotes.
                val x = w * (0.18f + 0.42f * (0.5f - 0.5f * cos(dos * t)))
                val ancho = min(w * 0.42f, 220 * u); val alto = min(h * 0.36f, 300 * u); val y = h * 0.22f
                val incl = alto * 0.35f
                val mancha = Path().apply { moveTo(x, y); lineTo(x + ancho, y); lineTo(x + ancho + incl, y + alto); lineTo(x + incl, y + alto); close() }
                val a = (0.16f * fuerza).coerceIn(0f, 1f)
                drawPath(mancha, Brush.linearGradient(listOf(TrazosCiudad.LUZ_BAJA.copy(alpha = a), TrazosCiudad.SOL_VELADO.copy(alpha = a * 0.6f), Color.Transparent),
                    Offset(x, y), Offset(x + ancho + incl, y + alto)))
                for (b in 1..2) {
                    val bx = x + ancho * b / 3f
                    drawPath(Path().apply { moveTo(bx - 3 * u, y); lineTo(bx + 3 * u, y); lineTo(bx + 3 * u + incl, y + alto); lineTo(bx - 3 * u + incl, y + alto); close() },
                        TrazosCiudad.SOMBRA.copy(alpha = a * 0.25f))
                }
            } else {
                val respira = 1f + 0.03f * sin(dos * t * 4f)
                val o = Offset(w * 0.12f, -h * 0.04f); val r = max(w, h) * 0.7f * respira
                drawCircle(Brush.radialGradient(listOf(TrazosCiudad.LAMPARA_LUZ.copy(alpha = (0.11f * fuerza * respira).coerceIn(0f, 1f)),
                    TrazosCiudad.LAMPARA_LUZ.copy(alpha = 0.04f * fuerza), Color.Transparent), o, r), r, o)
            }
            return
        }
        val m = k.m
        val a = ((if (oscuro) 0.1f else 0.09f) * fuerza).coerceIn(0f, 1f)
        val respiro = m.respiro * u * sin(dos * t * 5f)
        val flujo = (m.bandasPorSegundo * 60f).roundToInt().toFloat()
        val fase = t * flujo
        val alto = h * 1.1f
        val sepB = max(w * 0.042f, 12 * u)
        val c = Offset(w / 2, h * 0.5f)
        val tintas = tintasBanda(k)
        tintas.forEachIndexed { j, (tt, col) ->
            conRegistro(tt, m.registro) {
                bandas(c, alto, m.bandasPorLado, sepB, (m.amplitud * u + respiro) * (1f + 0.15f * j), m.grosor * u * 0.7f,
                    col.copy(alpha = col.alpha * a), frecuencia = 1.4f, fase = fase + j * 0.18f, abre = 0.9f)
            }
        }
        // El disco hecho sol que asoma arriba a la derecha.
        val paso = 15f
        val vueltas = ((m.vueltasPorSegundo * 60f * 360f) / paso).roundToInt() * paso
        val sol = Offset(w * 0.92f, h * 0.06f); val rs = min(w, h) * 0.32f
        // En su propia capa, para darle la opacidad tenue de una vez (solo el cuadrado del sol).
        drawContext.canvas.saveLayer(Rect(sol.x - rs, sol.y - rs, sol.x + rs, sol.y + rs), androidx.compose.ui.graphics.Paint().apply { alpha = a.coerceIn(0f, 1f) })
        corona(sol, rs * 0.55f, rs, k, giro = t * vueltas, n = 24)
        disco(sol, rs * 0.5f, k, sombra = false)
        drawContext.canvas.restore()
    }

    // ---------------------------------------------------------------------------------------------- pequeñas piezas

    /**
     * Las esquinas de una tarjeta (Componentes, `esquinasComp`), a [i] del canto y de [u] de lado. La ciudad: las
     * esquineras de papel de un álbum de fotos (el triángulo que sujeta la foto), en las cuatro. La tinta: arriba a la
     * izquierda, un cuarto de corona de rayos; abajo a la derecha, un triángulo de trama.
     */
    fun DrawScope.esquinas(ins: Float, u: Float, k: ColoresPsi) {
        val w = size.width; val h = size.height
        if (w < 6 * u || h < 6 * u) return
        if (k.ciudad) {
            val l = u * 2.2f
            val col = lerp(k.variante, k.borde, 0.45f)
            listOf(Offset(ins, ins) to Offset(1f, 1f), Offset(w - ins, ins) to Offset(-1f, 1f), Offset(ins, h - ins) to Offset(1f, -1f), Offset(w - ins, h - ins) to Offset(-1f, -1f)).forEach { (o, s) ->
                val p = Path().apply { moveTo(o.x, o.y); lineTo(o.x + s.x * l, o.y); lineTo(o.x, o.y + s.y * l); close() }
                translate(0.5f * density, 0.7f * density) { drawPath(p, k.sombra) }
                drawPath(p, col)
                drawLine(k.luz, Offset(o.x + s.x * l, o.y), Offset(o.x, o.y + s.y * l), 0.6f * density)
            }
        } else {
            val c = Offset(ins, ins)
            clipPath(Path().apply { addRect(Rect(ins, ins, ins + u * 2.6f, ins + u * 2.6f)) }) {
                corona(c, u * 0.7f, u * 2.5f, k, n = 12)
                drawCircle(k.magenta, u * 0.6f, c)
            }
            val b = Offset(w - ins, h - ins)
            val tri = Path().apply { moveTo(b.x, b.y); lineTo(b.x - u * 2.4f, b.y); lineTo(b.x, b.y - u * 2.4f); close() }
            trama(tri, 1.6f * density, 0.7f * density, k.ultramar, 45f)
        }
    }

    /** La marca de la escala en una casilla (Componentes, `marcaEscala`), de [d] de unidad: el agujero de la picadora del boleto, o el disco con su corona. */
    fun DrawScope.marcaEscala(c: Color, fondo: Color, d: Float, k: ColoresPsi) {
        val w = size.width
        if (k.ciudad) {
            drawCircle(c, d * 0.95f, Offset(w / 2, d * 1.6f))
            drawCircle(fondo.copy(alpha = 0.85f), d * 0.42f, Offset(w / 2, d * 1.6f))
        } else {
            val o = Offset(w / 2, d * 1.6f)
            drawPath(cunas(o, d * 0.7f, d * 1.5f, 12, 0.5f, 0.12f, 0f), c)
            drawCircle(c, d * 0.62f, o)
            drawCircle(fondo.copy(alpha = 0.85f), d * 0.18f, o)
        }
    }

    /**
     * El adorno dentro de un botón (Componentes, `adornoBoton`), en [c], con [m] de margen. La ciudad: el filete pintado de
     * una placa de letrero, con sus dos tornillos. La tinta: el filete de arco del cartel y, abajo, una onda corta.
     */
    fun DrawScope.adornoBoton(c: Color, m: Float, k: ColoresPsi) {
        val w = size.width; val h = size.height; val fino = density
        if (w < m * 6 || h < m * 4) return
        if (k.ciudad) {
            val r = Rect(m, m, w - m, h - m)
            drawRoundRect(c, r.topLeft, r.size, CornerRadius(min(r.height / 2, 9 * density)), style = Stroke(fino * 0.8f))
            listOf(m * 2.4f, w - m * 2.4f).forEach { x -> drawCircle(c, fino * 1.1f, Offset(x, h / 2)); drawLine(TrazosCiudad.SOMBRA.copy(alpha = 0.3f), Offset(x - fino * 0.7f, h / 2), Offset(x + fino * 0.7f, h / 2), fino * 0.5f) }
        } else {
            drawPath(TrazosTinta.arco(Rect(m, m, w - m, h - m), min((h - 2 * m) / 2, 12 * density), 1.5f * density), c, style = Stroke(fino * 0.9f))
            if (w > 80 * density) ondas(w * 0.36f, w * 0.64f, h - m * 1.6f, fino * 0.8f, m * 2.6f, fino * 0.9f, 0f, listOf(TrazosTinta.Tinta.ULTRAMAR to c), k.m.registro * 0.3f)
        }
    }

    /**
     * Lo de un diálogo (ControlesTema, `marcoDialogo`), en un lienzo de [w]×[h] con [d] px por dp. La ciudad: los cables
     * del trolebús por dentro del canto de arriba, con su poste en la esquina de la izquierda; abajo a la izquierda, el pase
     * de autobús. La tinta: arriba a la derecha, un cuarto de disco hecho sol; arriba, las bandas que lo enmarcan; abajo a la
     * izquierda, el disco chico.
     */
    fun DrawScope.marcoDialogo(k: ColoresPsi, d: Float) {
        val w = size.width; val h = size.height
        if (w < 120 * d || h < 80 * d) return
        if (k.ciudad) {
            val col = k.tenue.copy(alpha = 0.55f)
            // El poste de madera con su brazo que sale hacia la calle, del que cuelgan los cables.
            drawLine(col, Offset(18 * d, 4 * d), Offset(18 * d, 30 * d), 2.4f * d)
            drawLine(col, Offset(18 * d, 6 * d), Offset(38 * d, 9.4f * d), 1.4f * d)
            cablesTrolebus(38 * d, w - 16 * d, 11 * d, col, tramo = w)
            pase(Rect(14 * d, h - 32 * d, 56 * d, h - 12 * d), k, -7f)
        } else {
            val sol = Offset(w - 6 * d, 6 * d)
            clipPath(Path().apply { addRect(Rect(w - 60 * d, 0f, w, 60 * d)) }) {
                corona(sol, 22 * d, 50 * d, k, n = 20)
                disco(sol, 20 * d, k, sombra = false)
            }
            ondas(24 * d, w - 70 * d, 12 * d, 1.4f * d, 30 * d, 1.4f * d, 3f * d, tintasBanda(k), k.m.registro * 0.5f)
            disco(Offset(20 * d, h - 20 * d), 7 * d, k)
        }
    }

    /**
     * El pase de autobús de la ciudad en [r], girado [grados]: la cartulina con su franja de color arriba, el número y la
     * picadura del revisor. Sin letras: solo rayas que las sugieren.
     */
    fun DrawScope.pase(r: Rect, k: ColoresPsi, grados: Float = 0f) {
        if (r.width <= 4f || r.height <= 4f) return
        val u = density
        rotate(grados, r.center) {
            val rr = CornerRadius(1.6f * u)
            drawRoundRect(k.sombra, r.topLeft + Offset(0.8f * u, 1.2f * u), r.size, rr)
            val carton = if (k.oscuro) Color(0xFFCDBB9C) else Color(0xFFF1E4C6)
            drawRoundRect(carton, r.topLeft, r.size, rr)
            drawRect(TrazosCiudad.alSol(k.primario, 0.1f), r.topLeft, Size(r.width, r.height * 0.26f))
            val tinta = Color(0xFF3A2C22)
            drawLine(tinta.copy(alpha = 0.7f), Offset(r.left + r.width * 0.1f, r.top + r.height * 0.5f), Offset(r.left + r.width * 0.6f, r.top + r.height * 0.5f), max(0.6f * u, r.height * 0.07f))
            drawLine(tinta.copy(alpha = 0.45f), Offset(r.left + r.width * 0.1f, r.top + r.height * 0.72f), Offset(r.left + r.width * 0.45f, r.top + r.height * 0.72f), max(0.5f * u, r.height * 0.05f))
            drawCircle(k.sombra.copy(alpha = 0.5f), r.height * 0.1f, Offset(r.right - r.width * 0.16f, r.top + r.height * 0.62f))
            drawRoundRect(TrazosCiudad.enSombra(carton, 0.3f), r.topLeft, r.size, rr, style = Stroke(0.5f * u))
        }
    }

    /** La perilla de un deslizador o un interruptor (ControlesTema, `perillaTema`): la etiqueta del disco, o el disco con su corona. */
    fun DrawScope.perilla(c: Offset, r: Float, color: Color, hueco: Color, k: ColoresPsi) {
        if (r <= 0.5f) return
        if (k.ciudad) {
            drawCircle(TrazosCiudad.SOMBRA.copy(alpha = 0.25f), r * 0.92f, c + Offset(r * 0.08f, r * 0.12f))
            drawCircle(color, r * 0.92f, c)
            drawCircle(hueco.copy(alpha = 0.5f), r * 0.6f, c, style = Stroke(r * 0.06f))
            drawCircle(hueco.copy(alpha = 0.8f), r * 0.12f, c)
            drawArc(TrazosCiudad.AL_SOL.copy(alpha = 0.45f), 200f, 50f, false, Offset(c.x - r * 0.72f, c.y - r * 0.72f), Size(r * 1.44f, r * 1.44f), style = Stroke(r * 0.1f, cap = StrokeCap.Round))
        } else {
            drawPath(cunas(c, r * 0.62f, r * 1.02f, 12, 0.5f, 0.12f, 0f), color)
            drawCircle(color, r * 0.66f, c)
            drawCircle(hueco.copy(alpha = 0.6f), r * 0.24f, c)
        }
    }

    /**
     * El riel de un deslizador (ControlesTema, `rielTema`) hasta [x], centrado en [cy], de [g] de grueso. Lo que falta, un
     * hilo fino. Lo recorrido, en la ciudad, el pasamanos del autobús (con su brillo); en la tinta, la onda del cartel, más
     * honda con la intensidad, con su segunda pasada en ultramar corrida de registro.
     */
    fun DrawScope.riel(x: Float, cy: Float, g: Float, activo: Color, inactivo: Color, k: ColoresPsi) {
        val w = size.width; val d = density
        drawLine(inactivo, Offset(min(x, w), cy), Offset(w, cy), max(g * 0.24f, 0.7f * d), StrokeCap.Round)
        if (x <= 0f) return
        if (k.ciudad) {
            drawLine(activo, Offset(0f, cy), Offset(x, cy), max(g * 0.55f, 2f * d), StrokeCap.Round)
            drawLine(TrazosCiudad.AL_SOL.copy(alpha = 0.45f), Offset(g * 0.3f, cy - g * 0.12f), Offset(max(x - g * 0.3f, 0f), cy - g * 0.12f), max(g * 0.12f, 0.6f * d), StrokeCap.Round)
        } else {
            val amp = g * (0.35f + 0.25f * k.i)
            val periodo = max(g * 4f, 12f * d)
            val pasadas = buildList {
                if (k.i > 0f) add(TrazosTinta.Tinta.ULTRAMAR to k.ultramar.copy(alpha = 0.8f))
                add(TrazosTinta.Tinta.MAGENTA to activo)
            }
            ondas(0f, x, cy, amp, periodo, max(g * 0.5f, 1.2f * d), 0f, pasadas, k.m.registro * 0.4f, adelgaza = false)
        }
    }

    /** La figura del tema en los íconos de la cinta y en las casillas (IconosCinta, `figuraTema`): el disco, con su corona en la tinta. */
    fun DrawScope.figura(c: Offset, r: Float, color: Color, hueco: Color, k: ColoresPsi) {
        if (r <= 0.5f) return
        if (!k.ciudad) drawPath(cunas(c, r * 0.7f, r, 12, 0.5f, 0.12f, 0f), color)
        drawCircle(color, if (k.ciudad) r * 0.95f else r * 0.68f, c)
        drawCircle(hueco.copy(alpha = 0.55f), r * 0.3f, c)
        drawCircle(color, r * 0.1f, c)
    }
}
