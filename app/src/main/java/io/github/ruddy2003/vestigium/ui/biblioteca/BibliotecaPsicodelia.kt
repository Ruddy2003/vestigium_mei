package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.Cara
import io.github.ruddy2003.vestigium.ui.ColoresPsi
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.PiezasPsicodelia
import io.github.ruddy2003.vestigium.ui.Psicodelia
import io.github.ruddy2003.vestigium.ui.TrazosCiudad
import io.github.ruddy2003.vestigium.ui.TrazosTinta
import io.github.ruddy2003.vestigium.ui.TrazosTinta.conRegistro
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Psicodelia (versión 2: dos caras)
// =====================================================================

/**
 * Psicodelia: la repisa de tablas de pino sobre pilas de ladrillo del cuarto de David, en una casa victoriana vieja del
 * Haight (el mueble y cada entrada están en [MuebleEstantePsicodelia]). Aquí, lo de alrededor, en las dos caras del tema
 * (`docs/diseno/temas/psicodelia.md`): la ciudad, sobria y gastada como una foto de los setenta (luz de la tarde por la
 * izquierda, sombra café, grano de película; de noche, la lámpara de pantalla) y la psicodelia, el cartel impreso a tintas
 * planas (magenta y ultramar; con la intensidad, también el bermellón, más croma y el registro corrido) sobre papel crema o
 * sobre negro de terciopelo. Cada pieza lee la cara y la intensidad de `EstadoTema` con [PiezasPsicodelia.colores]: un
 * solo dibujo, dos pintores.
 *
 * - **La tapa:** el disco del día como un álbum. La funda con su portada (la calle de victorianas en foto, o el cartel del
 *   disco hecho sol con su corona de rayos) y, saliendo de ella, la funda interior con el disco; al abrirse, la portada se abre
 *   como un álbum doble y deja ver la lista de las pistas.
 * - **La página:** la funda interior, con la etiqueta del disco y el disco del día como emblema; los cables del trolebús o
 *   las bandas del cartel al pie.
 * - **La libreta:** el periódico del barrio, «La Niebla», a dos tintas de tintero partido, sobre la mesa de la cocina,
 *   contra la pared con el cartel de la semana (sin ventana).
 * - **Las cartas:** postales de 1967 (la calle con niebla, el parque, la bahía; en la tinta, carteles impresos), atadas con
 *   bramante y selladas con una etiqueta de disco.
 * - **Lo de alrededor:** los separadores de la caja de discos, el dial de la radio (el 106.9), la pared con los carteles de
 *   David sobre la tabla y los ladrillos, la portada de cada obra como un cartel propio y el recuerdo como un volante de
 *   mimeógrafo.
 * Sin flores, sin el símbolo de la paz, sin arcoíris, sin burbujas ni cinta de la frente (la guía, «Lo que se va»).
 */
object EstantePsicodelia : DisenoBiblioteca {
    // ---------------------------------------------------------------- las letras (Libre Franklin y, en la tinta, Bloque)
    private val TEXTO: FontFamily = LetraExtra.franklin
    private val ROTULO: FontFamily = LetraExtra.franklinRotulo

    /** La letra de los títulos: el titular de periódico en la ciudad, la de bloque de cartel en la tinta. */
    private fun titulo(k: ColoresPsi): FontFamily = if (k.ciudad) LetraExtra.franklinTitular else LetraExtra.bloque

    // ---------------------------------------------------------------- los colores de los objetos (apagados por la película)
    private val VINILO = Color(0xFF1E1916)
    private val CREMA = TrazosTinta.CREMA
    private val NEGRO = TrazosTinta.NEGRO
    /** La pared de yeso del departamento: verde salvia gastado. */
    private val YESO = Color(0xFFA2A68C)
    /** El pino sin pintar de las tablas, en sombra, y sus nudos. */
    private val PINO = Color(0xFFCFA673)
    private val PINO_OSC = Color(0xFF8C6A42)
    private val NUDO = Color(0xFF7A532E)
    /** El papel de periódico del barrio y la cinta de pintor. */
    private val PRENSA = Color(0xFFE6DCC6)
    private val CINTA = Color(0xFFD9C893)
    /** El bramante que ata las postales y el corcho del tablero. */
    private val BRAMANTE = Color(0xFFB59A6A)
    private val CORCHO = Color(0xFFB98A55)
    /** El verde botella y el ocre mostaza de la ciudad (los de su paleta). */
    private val VERDE_BOTELLA = Color(0xFF3D5838)
    private val OCRE = Color(0xFF7A5713)
    /** La tinta violeta del mimeógrafo (el volante del recuerdo, en la ciudad). */
    private val MIMEO = Color(0xFF5B3F9E)

    /** Los colores de la variante en uso (la cara y la intensidad de `EstadoTema`). */
    private fun k(oscuro: Boolean): ColoresPsi = PiezasPsicodelia.colores(oscuro)

    /** Lo de la ciudad de noche: el mismo color hundido en la penumbra café del cuarto; en la tinta oscura, en el terciopelo. */
    private fun noche(c: Color, k: ColoresPsi, f: Float = 0.6f): Color =
        if (!k.oscuro) c else lerp(c, if (k.ciudad) TrazosCiudad.PARED_NOCHE else NEGRO, f)

    /** La línea de dibujo del cartel: la tinta de grabar sobre papel, el crema sobre negro. */
    private fun linea(k: ColoresPsi): Color = if (k.oscuro) CREMA else k.grabado

    /** El grano de película de la ciudad sobre [forma]; en la tinta, nada. */
    private fun DrawScope.grano(k: ColoresPsi, forma: Path?, fuerza: Float = 0.4f) {
        if (k.ciudad) with(PiezasPsicodelia) { granoPelicula(forma, k.grano * fuerza) }
    }

    // =================================================================
    //  Ayudantes de dibujo
    // =================================================================

    /** El ancho de un texto, para ajustar rótulos a lo que dicen. */
    private fun anchoTx(medidor: TextMeasurer, texto: String, letra: FontFamily, tam: Float, espaciado: Float = 0f): Float =
        medidor.measure(texto, TextStyle(fontFamily = letra, fontSize = tam.sp, letterSpacing = espaciado.sp), maxLines = 1, softWrap = false).size.width.toFloat()

    /** Un texto alineado a la izquierda dentro de [r]. */
    private fun DrawScope.aIzquierda(medidor: TextMeasurer, texto: String, r: Rect, letra: FontFamily, color: Color, tam: Float,
                                     espaciado: Float = 0f, minimo: Float = 6f) {
        val ancho = anchoTx(medidor, texto, letra, tam, espaciado) + 2f
        textoEn(medidor, texto, Rect(r.left, r.top, minOf(r.right, r.left + ancho), r.bottom), letra, color, tam, espaciado = espaciado, minimo = minimo)
    }

    /**
     * Un título decorativo: en la tinta, impreso dos veces (la segunda pasada en magenta, corrida de registro con la
     * intensidad; sin intensidad, la sombra apenas corrida); en la ciudad, una sola vez.
     */
    private fun DrawScope.dosTintas(medidor: TextMeasurer, texto: String, r: Rect, letra: FontFamily, color: Color, k: ColoresPsi, tam: Float, minimo: Float = 6f) {
        if (!k.ciudad) {
            val d = (0.8f + k.m.registroTitulo) * density
            textoEn(medidor, texto, r.translate(d, -d * 0.4f), letra, k.magenta.copy(alpha = 0.75f), tam, minimo = minimo)
        }
        textoEn(medidor, texto, r, letra, color, tam, minimo = minimo)
    }

    /**
     * Un texto en tinta de tintero partido, la del periódico del barrio: el color corre de un extremo al otro de las letras
     * entre las dos tintas de [colores]. Si no cabe en [r], se achica.
     */
    private fun DrawScope.tintero(medidor: TextMeasurer, texto: String, r: Rect, letra: FontFamily, tam: Float, colores: List<Color>, minimo: Float = 6f) {
        if (texto.isBlank() || r.width <= 1f || r.height <= 1f || colores.size < 2) return
        var estilo = TextStyle(fontFamily = letra, fontSize = tam.sp, lineHeight = (tam * 1.05f).sp)
        var capa = medidor.measure(texto, estilo, maxLines = 1, softWrap = false)
        val f = minOf(1f, r.width / capa.size.width.coerceAtLeast(1), r.height / capa.size.height.coerceAtLeast(1))
        if (f < 1f) {
            val nuevo = maxOf(minimo, tam * f * 0.96f)
            estilo = estilo.copy(fontSize = nuevo.sp, lineHeight = (nuevo * 1.05f).sp)
            capa = medidor.measure(texto, estilo, maxLines = 1, softWrap = false)
        }
        val o = Offset(r.center.x - capa.size.width / 2f, r.center.y - capa.size.height / 2f)
        drawText(capa, brush = Brush.horizontalGradient(colores, o.x, o.x + capa.size.width.toFloat()), topLeft = o)
    }

    /** Dibuja [bloque] con la transformación afín x' = a·x + c·y + tx, y' = b·x + d·y + ty (para la portada que se abre). */
    private fun DrawScope.afin(a: Float, b: Float, c: Float, d: Float, tx: Float, ty: Float, bloque: DrawScope.() -> Unit) {
        withTransform({ transform(Matrix(floatArrayOf(a, b, 0f, 0f, c, d, 0f, 0f, 0f, 0f, 1f, 0f, tx, ty, 0f, 1f))) }) { bloque() }
    }

    /** La pestaña [k] de las cinco: exactamente una quinta parte del ancho [w] (ahí se toca). */
    private fun quinta(k: Int, w: Float, arriba: Float, abajo: Float): Rect = Rect(w * k / 5f, arriba, w * (k + 1) / 5f, abajo)

    /** Una línea ondulada de [desde] a [hasta], de [ondas] ondas y [alto] de amplitud. */
    private fun onda(desde: Offset, hasta: Offset, ondas: Float, alto: Float, fase: Float = 0f): Path = Path().apply {
        val pasos = 48
        for (i in 0..pasos) {
            val f = i / pasos.toFloat()
            val x = desde.x + (hasta.x - desde.x) * f
            val y = desde.y + (hasta.y - desde.y) * f + alto * sin(f * ondas * 2f * PI.toFloat() + fase)
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
    }

    /**
     * La banda de cada cara de [x0] a [x1] a la altura [y]: en la ciudad, los cables del trolebús; en la tinta, las bandas
     * del cartel a dos tintas (tres con la intensidad), corridas de registro. Termina, si [conDisco], en el disco chico del día.
     */
    private fun DrawScope.banda(x0: Float, x1: Float, y: Float, k: ColoresPsi, alfa: Float = 1f, conDisco: Boolean = false) {
        val u = density
        if (x1 - x0 < 8 * u) return
        with(PiezasPsicodelia) {
            if (k.ciudad) cablesTrolebus(x0, x1, y, k.tenue.copy(alpha = 0.6f * alfa), grosor = 0.8f * u)
            else ondas(x0, x1, y, (1.2f + 0.05f * k.m.amplitud) * u, 26 * u, 1.2f * u, 2.4f * u, tintasBanda(k).map { (t, c) -> t to c.copy(alpha = c.alpha * alfa) }, k.m.registro * 0.5f, adelgaza = false)
            if (conDisco) disco(Offset(x1, y), 4.5f * u, k)
        }
    }

    /**
     * Pino sin pintar dentro de [r] (la mesa, la tabla, el marco del corcho): en la ciudad, el color [base] con su veta y
     * algún nudo; en la tinta, el ultramar plano con la trama de la tinta de grabar y su línea.
     */
    private fun DrawScope.pino(r: Rect, base: Color, k: ColoresPsi, semilla: Int, vertical: Boolean = false, nudos: Int = 2) {
        val u = density
        if (r.width <= 0f || r.height <= 0f) return
        if (!k.ciudad) {
            drawRect(if (k.oscuro) lerp(NEGRO, k.ultramar, 0.35f) else k.papel, r.topLeft, r.size)
            with(TrazosTinta) { trama(Path().apply { addRect(r) }, 3f * u, 0.8f * u, k.ultramar.copy(alpha = if (k.oscuro) 0.8f else 0.9f), if (vertical) 90f else 0f) }
            drawRect(linea(k).copy(alpha = 0.7f), r.topLeft, r.size, style = Stroke(0.7f * u))
            return
        }
        val c = noche(base, k)
        drawRect(c, r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            vetas(r, lerp(c, noche(PINO_OSC, k), 0.6f).copy(alpha = 0.5f), semilla, 0.9f, vertical, 0.7f, 0.5f)
            vetas(r, lerp(c, TrazosCiudad.AL_SOL, 0.3f).copy(alpha = if (k.oscuro) 0.06f else 0.22f), semilla + 5, 0.5f, vertical, 0.6f, 0.7f)
            repeat(nudos) { j ->
                val q = Offset(r.left + r.width * (0.1f + 0.8f * azar(semilla, j + 70)), r.top + r.height * (0.25f + 0.5f * azar(semilla, j + 80)))
                val rr = minOf(minOf(r.width, r.height) * (0.16f + 0.1f * azar(semilla, j + 90)), (3.5f + 2f * azar(semilla, j + 91)) * u)
                val s = if (vertical) Size(rr * 1.2f, rr * 2f) else Size(rr * 2.2f, rr * 1.1f)
                drawOval(noche(NUDO, k).copy(alpha = 0.7f), Offset(q.x - s.width / 2, q.y - s.height / 2), s)
            }
        }
    }

    /** Una chincheta clavada en [c]: en la ciudad, la cabeza redonda con su brillo y su sombra café; en la tinta, plana. */
    private fun DrawScope.chincheta(c: Offset, r: Float, color: Color, k: ColoresPsi) {
        if (k.ciudad) {
            drawCircle(k.sombra, r, c + Offset(r * 0.35f, r * 0.5f))
            drawCircle(noche(color, k, 0.4f), r, c)
            drawCircle(TrazosCiudad.AL_SOL.copy(alpha = if (k.oscuro) 0.2f else 0.55f), r * 0.3f, c - Offset(r * 0.35f, r * 0.35f))
        } else {
            drawCircle(color, r, c)
            drawCircle(linea(k), r, c, style = Stroke(0.5f * density))
        }
    }

    /** Un trozo de cinta de pintor pegado en [r], con lo que dice escrito a plumón. */
    private fun DrawScope.cintaPintor(r: Rect, texto: String, medidor: TextMeasurer, k: ColoresPsi, tam: Float, giro: Float) {
        val u = density
        rotate(giro, r.center) {
            drawRect(k.sombra, r.topLeft + Offset(0.6f * u, 1 * u), r.size)
            val fondo = if (k.ciudad) noche(CINTA, k, 0.55f) else (if (k.oscuro) lerp(NEGRO, CREMA, 0.85f) else k.papel)
            drawRect(fondo, r.topLeft, r.size)
            if (!k.ciudad) drawRect(linea(k).copy(alpha = 0.6f), r.topLeft, r.size, style = Stroke(0.6f * u))
            // Las puntas rotas de la cinta.
            listOf(r.left, r.right).forEach { x -> var y = r.top; while (y < r.bottom) { drawCircle(k.sombra, 0.7f * u, Offset(x, y)); y += 2 * u } }
            val tx = if (fondo.luminance() > 0.35f) Color(0xFF2A2119) else CREMA
            textoEn(medidor, texto, r.deflate(2 * u), TEXTO, tx, tam, minimo = 5f)
        }
    }

    /** Una luna chica (la de los discos de los sueños): el creciente, recortado. */
    private fun DrawScope.lunita(c: Offset, r: Float, color: Color) {
        val redondo = Path().apply { addOval(Rect(c, r)) }
        val sombra = Path().apply { addOval(Rect(Offset(c.x + r * 0.42f, c.y - r * 0.2f), r * 0.86f)) }
        drawPath(Path.combine(PathOperation.Difference, redondo, sombra), color)
    }

    /**
     * Una victoriana de San Francisco de fines de los sesenta, de pie en [base] (el centro de su frente, abajo): el cuerpo,
     * el frontón en punta o la cornisa recta ([fronton]), el mirador de tres paños de dos pisos (el de la izquierda al sol, el
     * de la derecha en sombra café) y la puerta alta. [cuerpo] es su pintura; [ventana], el vidrio (de noche, la lámpara).
     */
    private fun DrawScope.victoriana(base: Offset, ancho: Float, alto: Float, cuerpo: Color, ventana: Color, fronton: Boolean) {
        if (ancho <= 1f || alto <= 1f) return
        val x0 = base.x - ancho / 2; val top = base.y - alto
        val luz = TrazosCiudad.alSol(cuerpo, 0.3f); val sombra = TrazosCiudad.enSombra(cuerpo, 0.45f)
        drawRect(cuerpo, Offset(x0, top), Size(ancho, alto))
        drawRect(sombra, Offset(x0 + ancho * 0.86f, top), Size(ancho * 0.14f, alto))
        if (fronton) drawPath(Path().apply { moveTo(x0 - ancho * 0.03f, top); lineTo(base.x, top - ancho * 0.36f); lineTo(x0 + ancho * 1.03f, top); close() }, luz)
        else drawRect(sombra, Offset(x0 - ancho * 0.04f, top - ancho * 0.06f), Size(ancho * 1.08f, ancho * 0.07f))
        // El mirador: tres paños, dos pisos.
        val mx = x0 + ancho * 0.08f; val tercio = ancho * 0.52f / 3f
        listOf(luz, cuerpo, sombra).forEachIndexed { j, c ->
            drawRect(c, Offset(mx + j * tercio, top + alto * 0.08f), Size(tercio, alto * 0.74f))
            for (piso in 0..1) drawRect(ventana, Offset(mx + j * tercio + tercio * 0.2f, top + alto * (0.13f + piso * 0.36f)), Size(tercio * 0.6f, alto * 0.24f))
        }
        // La puerta alta, a la derecha.
        drawRect(sombra, Offset(x0 + ancho * 0.68f, base.y - alto * 0.46f), Size(ancho * 0.16f, alto * 0.46f))
    }

    // =================================================================
    //  El mueble y cada entrada: el dibujo está en MuebleEstantePsicodelia
    // =================================================================

    override val corona = MuebleEstantePsicodelia.CORONA
    override val base = MuebleEstantePsicodelia.BASE
    override val lado = MuebleEstantePsicodelia.LADO
    override val altoRepisa: Dp = MuebleEstantePsicodelia.ALTO_REPISA
    override val tabla: Dp = MuebleEstantePsicodelia.TABLA
    override val separa: Dp = MuebleEstantePsicodelia.SEPARA
    override val letra: FontFamily = ROTULO

    /** Estante rehecho: el aura solo sale al tocar (ver [DisenoBiblioteca.auraEnReposo]). */
    override val auraEnReposo = false

    override fun ancho(palabras: Int): Dp = MuebleEstantePsicodelia.ancho(palabras)
    override fun alto(semilla: Int, palabras: Int): Float = MuebleEstantePsicodelia.alto(semilla, palabras)

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) =
        with(MuebleEstantePsicodelia) { estante(d, medidor, oscuro, noche) }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) =
        with(MuebleEstantePsicodelia) { volumen(v, medidor, oscuro) }

    /** La pegatina redonda del ánimo, en [c]: el color del ánimo con su borde crema; sin ánimo, no hay. */
    private fun DrawScope.puntoAnimo(c: Offset, r: Float, tono: Color?, k: ColoresPsi) {
        if (tono == null) return
        drawCircle(k.sombra, r, c + Offset(0.4f * density, 0.7f * density))
        drawCircle(if (k.oscuro) lerp(CREMA, NEGRO, 0.15f) else CREMA, r, c)
        drawCircle(tono, r * 0.76f, c)
        if (k.ciudad) drawCircle(Color.White.copy(alpha = 0.4f), r * 0.24f, c - Offset(r * 0.3f, r * 0.3f))
    }

    /**
     * La etiqueta de un disco usada como sello (la de las postales y la del recuerdo), en [c]: el papel crema troquelado en
     * redondo, el aro de [color] con sus dos renglones impresos y el agujero del centro. [alfa] la hace aparecer al caer.
     */
    private fun DrawScope.etiquetaSello(c: Offset, r: Float, color: Color, k: ColoresPsi, alfa: Float = 1f, giro: Float = 0f) {
        val a = alfa.coerceIn(0f, 1f)
        if (a <= 0f || r <= 0f) return
        drawCircle(k.sombra.copy(alpha = k.sombra.alpha * a), r, c + Offset(r * 0.07f, r * 0.11f))
        drawCircle(CREMA.copy(alpha = a), r, c)
        drawCircle(color.copy(alpha = a), r * 0.8f, c)
        rotate(giro * 57.3f, c) {
            val renglon = CREMA.copy(alpha = 0.7f * a)
            drawLine(renglon, Offset(c.x - r * 0.45f, c.y - r * 0.35f), Offset(c.x + r * 0.45f, c.y - r * 0.35f), r * 0.08f)
            drawLine(renglon, Offset(c.x - r * 0.3f, c.y + r * 0.38f), Offset(c.x + r * 0.3f, c.y + r * 0.38f), r * 0.07f)
        }
        drawCircle(CREMA.copy(alpha = a), r * 0.1f, c)
        if (k.ciudad) drawArc(Color.White.copy(alpha = 0.35f * a), 200f, 60f, false, Offset(c.x - r * 0.7f, c.y - r * 0.7f), Size(r * 1.4f, r * 1.4f), style = Stroke(r * 0.08f, cap = StrokeCap.Round))
        else drawCircle(linea(k).copy(alpha = 0.6f * a), r, c, style = Stroke(r * 0.05f))
    }

    // =================================================================
    //  La tapa: el disco del día como un álbum
    // =================================================================

    override val altoTapa: Dp = 200.dp
    override val cajaTapa = Rect(0.575f, 0.33f, 0.93f, 0.78f)
    override val letraTapa: FontFamily = TEXTO
    override val tamTapa = 11.5f
    override val lineasTapa = 5
    override val centradoTapa = false
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = k(oscuro).tinta
    override fun palabrasTapa(n: Int): String = Psicodelia.numero(n) + if (n == 1) " palabra en la funda" else " palabras en la funda"

    /** La franja de arriba de la funda interior, en [r]: los cables (la ciudad) o las bandas del cartel (la tinta). */
    private fun DrawScope.franjaFunda(r: Rect, k: ColoresPsi) = banda(r.left, r.right, r.center.y, k, 0.8f)

    /**
     * El disco del día como un álbum: a la izquierda, la funda con su portada (la foto de la calle, o el cartel del disco hecho
     * sol); de ella sale, a la derecha, la funda interior de papel, donde se lee lo escrito, y asoma por arriba el disco. Al
     * abrirse, la portada se abre como un álbum doble (y deja ver la lista de las pistas) y el disco sube.
     */
    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val k = k(oscuro)
        val a = e.abierto.coerceIn(0f, 1f)
        val partes = e.fecha.split(" · ")
        val ladoPista = if (partes.size > 2) partes.drop(2).joinToString(" · ") else ""
        val lado = minOf(h * 0.88f, w * 0.52f)
        val cara = Rect(w * 0.03f, (h - lado) / 2f, w * 0.03f + lado, (h + lado) / 2f)
        val interior = Rect(cara.right - 26 * u, h * 0.22f, w * 0.97f, h * 0.95f)
        // La sombra sobre la mesa.
        drawRect(k.sombra, Offset(interior.left + 4 * u, interior.top + 5 * u), interior.size)
        drawRect(k.sombra, Offset(cara.left + 4 * u, cara.top + 6 * u), cara.size)
        // El disco, que asoma por arriba de la funda interior (y sube al abrirse).
        val dc = Offset(cara.right + (interior.right - cara.right) * 0.48f, h * 0.5f - a * h * 0.16f)
        with(PiezasPsicodelia) { disco(dc, h * 0.38f, k, giro = (a * 0.6f + 0.1f) * 360f, etiqueta = if (e.noche) k.terciario else null) }
        // La funda interior de papel, con su franja y la cuenta de palabras.
        val funda = Path().apply { addRect(interior) }
        drawPath(funda, k.superficie)
        drawRect(Brush.horizontalGradient(listOf(k.sombra, Color.Transparent), interior.left, interior.left + 40 * u), interior.topLeft, Size(40 * u, interior.height))
        franjaFunda(Rect(interior.left + 30 * u, interior.top + 4 * u, interior.right - 6 * u, interior.top + 16 * u), k)
        grano(k, funda)
        drawPath(funda, k.borde.copy(alpha = 0.7f), style = Stroke(0.6f * u))
        val pie = listOf(if (e.vacio) "sin estrenar" else palabrasTapa(e.palabras), ladoPista).filter { it.isNotBlank() }.joinToString(" · ")
        aIzquierda(medidor, pie, Rect(w * cajaTapa.left, h * 0.83f, interior.right - 6 * u, h * 0.91f), ROTULO, k.primario, 8.5f, espaciado = 0.3f, minimo = 5f)
        // Debajo de la portada, el interior del álbum doble.
        if (a > 0.01f) pliegue(cara, e, medidor, k)
        val ang = a * 1.5f
        val co = cos(ang); val si = sin(ang)
        if (a > 0.01f) drawRect(Color.Black.copy(alpha = 0.3f * si), Offset(cara.left + cara.width * co, cara.top), Size(16 * u * si, cara.height))
        afin(co, -si * 0.1f, 0f, 1f, cara.left - cara.left * co, cara.left * si * 0.1f) {
            portada(cara, e, medidor, k)
            if (a > 0f) drawRect(Color.Black.copy(alpha = 0.45f * (1f - co)), cara.topLeft, cara.size)
        }
    }

    /**
     * La portada del álbum, en [r]. La ciudad: una foto de la calle a la tarde (el cielo de bruma, las victorianas grises y una
     * pintada, el poste y los cables), con grano; de noche (los sueños), la misma calle a oscuras con alguna ventana encendida
     * y la luna. Abajo, la franja crema con «Disco», el número en el titular y el día. La tinta: el cartel del disco hecho sol,
     * con su corona de rayos que ondulan, los anillos que trae la intensidad, el marco de arco y las bandas; el número en letra
     * de bloque, impreso dos veces.
     */
    private fun DrawScope.portada(r: Rect, e: EstadoTapa, medidor: TextMeasurer, k: ColoresPsi) {
        val u = density
        val partes = e.fecha.split(" · ")
        val titular = partes.getOrElse(0) { e.fecha }
        val numero = titular.substringAfter(" ", titular).trim()
        val palabraDisco = titular.substringBefore(" ", titular)
        val dia = partes.getOrElse(1) { "" }
        val forma = Path().apply { addRoundRect(RoundRect(r, CornerRadius(2.5f * u))) }
        val franja = Rect(r.left, r.bottom - r.height * 0.3f, r.right, r.bottom)
        clipPath(forma) {
            if (k.ciudad) {
                val foto = Rect(r.left, r.top, r.right, franja.top)
                if (!e.noche) {
                    drawRect(Brush.verticalGradient(listOf(TrazosCiudad.BRUMA, TrazosCiudad.LUZ_BAJA), foto.top, foto.bottom), foto.topLeft, foto.size)
                    drawCircle(Brush.radialGradient(listOf(TrazosCiudad.SOL_VELADO, Color.Transparent), Offset(foto.left + foto.width * 0.2f, foto.top + foto.height * 0.5f), foto.width * 0.35f),
                        foto.width * 0.35f, Offset(foto.left + foto.width * 0.2f, foto.top + foto.height * 0.5f))
                } else drawRect(Brush.verticalGradient(listOf(Color(0xFF1A1512), Color(0xFF3A2C22)), foto.top, foto.bottom), foto.topLeft, foto.size)
                // Las victorianas de la cuesta, de la más lejana a la más cercana.
                val n = 4
                val anchoC = foto.width / 3.2f
                for (j in 0 until n) {
                    val base = Offset(foto.left + anchoC * (0.4f + j * 0.95f), foto.bottom + foto.height * 0.02f - j * foto.height * 0.05f)
                    val col = TrazosCiudad.CASAS[Math.floorMod(j + e.semilla, TrazosCiudad.CASAS.size)]
                    val vidrio = if (e.noche) (if ((j + e.semilla) % 3 == 0) TrazosCiudad.LAMPARA else Color(0xFF2A211A)) else TrazosCiudad.enSombra(Color(0xFF6E7A80), 0.3f)
                    victoriana(base, anchoC * 0.92f, foto.height * (0.6f + 0.08f * (j % 2)), if (e.noche) TrazosCiudad.enSombra(col, 0.7f) else col, vidrio, j % 2 == 0)
                }
                // El poste y los cables por delante.
                with(TrazosCiudad) { poste(Offset(foto.right - foto.width * 0.18f, foto.bottom), foto.height * 0.92f, 2.6f * u, if (e.noche) Color(0xFF14100D) else POSTE) }
                with(PiezasPsicodelia) { cablesTrolebus(foto.left, foto.right, foto.top + foto.height * 0.16f, (if (e.noche) Color(0xFF14100D) else TrazosCiudad.POSTE).copy(alpha = 0.8f)) }
                if (e.noche) lunita(Offset(foto.left + foto.width * 0.24f, foto.top + foto.height * 0.24f), foto.height * 0.09f, TrazosCiudad.SOL_VELADO)
                // De noche (el modo oscuro), la foto vista en el cuarto a media luz.
                if (k.oscuro) drawRect(TrazosCiudad.PARED_NOCHE.copy(alpha = 0.38f), foto.topLeft, foto.size)
                // La franja de abajo, crema, con el disco y el día.
                drawRect(k.superficie, franja.topLeft, franja.size)
                drawLine(k.borde, Offset(franja.left, franja.top), Offset(franja.right, franja.top), 0.8f * u)
                val izq = Rect(franja.left + 8 * u, franja.top + 3 * u, franja.left + franja.width * 0.5f, franja.bottom - 3 * u)
                aIzquierda(medidor, palabraDisco.uppercase(), Rect(izq.left, izq.top, izq.right, izq.top + izq.height * 0.32f), ROTULO, k.primario, 8f, espaciado = 1.2f, minimo = 5f)
                aIzquierda(medidor, numero, Rect(izq.left, izq.top + izq.height * 0.3f, izq.right, izq.bottom), titulo(k), k.tinta, 28f, minimo = 10f)
                textoEn(medidor, dia, Rect(franja.left + franja.width * 0.5f, franja.top + 3 * u, franja.right - 6 * u, franja.bottom - 3 * u), TEXTO, k.tenue, 11f, minimo = 6f)
                grano(k, forma, 0.6f)
                drawRect(Brush.radialGradient(0f to Color.Transparent, 0.65f to Color.Transparent, 1f to TrazosCiudad.VINETA.copy(alpha = 0.3f), center = r.center, radius = r.width * 0.75f), r.topLeft, r.size)
            } else {
                drawRect(k.papel, r.topLeft, r.size)
                val centro = Offset(r.center.x, r.top + r.height * 0.36f)
                val rd = r.width * 0.15f
                val marco = TrazosTinta.arco(Rect(r.left + 5 * u, r.top + 5 * u, r.right - 5 * u, franja.top - 2 * u), r.width * 0.4f, 2f * u)
                clipPath(marco) {
                    with(PiezasPsicodelia) {
                        corona(centro, rd * 1.25f, r.width * 0.8f, k, giro = e.semilla * 11f, n = 20)
                        ondas(r.left, r.right, franja.top - r.height * 0.1f, (2f + 0.12f * k.m.amplitud) * u, r.width * 0.3f, 2f * u, 4.5f * u, tintasBanda(k), k.m.registro)
                    }
                }
                for (j in k.m.anillos.coerceIn(0, 8) downTo 1) drawCircle(if (j % 2 == 0) k.magenta else k.ultramar, rd * (1.06f + 0.1f * j), centro, style = Stroke(r.width * 0.012f))
                with(PiezasPsicodelia) { disco(centro, rd, k, etiqueta = if (e.noche) k.terciario else null) }
                if (e.noche) lunita(Offset(r.left + r.width * 0.2f, r.top + r.height * 0.18f), r.width * 0.06f, linea(k))
                drawPath(marco, linea(k), style = Stroke(1.6f * u))
                // La franja de abajo, con el número en letra de bloque.
                drawRect(k.superficie, franja.topLeft, franja.size)
                drawLine(linea(k), Offset(franja.left, franja.top), Offset(franja.right, franja.top), 1.2f * u)
                val izq = Rect(franja.left + 8 * u, franja.top + 3 * u, franja.left + franja.width * 0.5f, franja.bottom - 3 * u)
                aIzquierda(medidor, palabraDisco.uppercase(), Rect(izq.left, izq.top, izq.right, izq.top + izq.height * 0.32f), ROTULO, k.primario, 8f, espaciado = 1.2f, minimo = 5f)
                dosTintas(medidor, numero, Rect(izq.left, izq.top + izq.height * 0.3f, izq.right, izq.bottom), titulo(k), linea(k), k, 24f, minimo = 10f)
                textoEn(medidor, dia, Rect(franja.left + franja.width * 0.5f, franja.top + 3 * u, franja.right - 6 * u, franja.bottom - 3 * u), TEXTO, k.tenue, 11f, minimo = 6f)
            }
        }
        drawPath(forma, if (k.ciudad) k.sombra else linea(k), style = Stroke(0.8f * u))
        // La etiqueta de precio de la tienda, pegada en la esquina.
        etiquetaSello(Offset(r.right - r.width * 0.12f, r.top + r.width * 0.12f), r.width * 0.07f, if (e.noche) k.terciario else k.primario, k, 1f, e.semilla * 0.3f)
    }

    /**
     * El interior del álbum doble, el que se ve al abrir la portada: el papel con la lista de las pistas de cada lado, como
     * renglones con su número, el día y el disco chico al pie.
     */
    private fun DrawScope.pliegue(r: Rect, e: EstadoTapa, medidor: TextMeasurer, k: ColoresPsi) {
        val u = density
        val hoja = Path().apply { addRect(r) }
        drawPath(hoja, k.superficie)
        drawRect(Brush.horizontalGradient(listOf(k.sombra, Color.Transparent), r.left, r.left + 16 * u), r.topLeft, Size(16 * u, r.height))
        franjaFunda(Rect(r.left + 12 * u, r.top + 4 * u, r.right - 8 * u, r.top + 14 * u), k)
        val col = Rect(r.left + 16 * u, r.top + 20 * u, r.right - 10 * u, r.bottom - 26 * u)
        listOf("Lado A", "Lado B").forEachIndexed { i, lado ->
            val y0 = col.top + i * col.height / 2f
            aIzquierda(medidor, lado, Rect(col.left, y0, col.right, y0 + 14 * u), ROTULO, k.primario, 9.5f, espaciado = 0.6f, minimo = 6f)
            for (j in 0 until 4) {
                val y = y0 + 20 * u + j * 9 * u
                if (y > y0 + col.height / 2f - 4 * u) break
                textoEn(medidor, "${j + 1 + i * 6}", Rect(col.left, y - 4 * u, col.left + 12 * u, y + 4 * u), ROTULO, k.tenue, 7f, minimo = 4f)
                drawLine(k.tenue.copy(alpha = 0.45f), Offset(col.left + 16 * u, y), Offset(col.left + 16 * u + (col.width - 20 * u) * (0.5f + 0.4f * azar(e.semilla, j + i * 7)), y), 1.2f * u, StrokeCap.Round)
            }
        }
        textoEn(medidor, e.fecha.split(" · ").getOrElse(1) { "" }, Rect(r.left + 16 * u, r.bottom - 22 * u, r.right - 30 * u, r.bottom - 8 * u), TEXTO, k.tenue, 9f, minimo = 5f)
        with(PiezasPsicodelia) { disco(Offset(r.right - 18 * u, r.bottom - 15 * u), 6 * u, k) }
        grano(k, hoja)
    }

    // =================================================================
    //  La página abierta: la funda interior
    // =================================================================

    override val altoEncabezado: Dp = 112.dp

    /**
     * La etiqueta de un disco en [c], de radio [r], con su anillo de vinilo y sus surcos: el papel de [color], el agujero del
     * centro y [numero]; de noche ([luna]), con la luna chica arriba.
     */
    private fun DrawScope.etiquetaDisco(c: Offset, r: Float, color: Color, numero: String, medidor: TextMeasurer, k: ColoresPsi, luna: Boolean) {
        val u = density
        drawCircle(k.sombra, r + 6 * u, c + Offset(0.8f * u, 1.2f * u))
        drawCircle(VINILO, r + 6 * u, c)
        var g = r + 1.5f * u
        while (g < r + 5.5f * u) { drawCircle(Color.White.copy(alpha = 0.08f), g, c, style = Stroke(0.4f * u)); g += 1.2f * u }
        if (k.ciudad) drawArc(TrazosCiudad.AL_SOL.copy(alpha = 0.3f), 200f, 44f, false, Offset(c.x - r - 3.5f * u, c.y - r - 3.5f * u), Size((r + 3.5f * u) * 2, (r + 3.5f * u) * 2), style = Stroke(3 * u))
        drawCircle(color, r, c)
        if (!k.ciudad) conRegistro(TrazosTinta.Tinta.ULTRAMAR, 0f) { drawCircle(k.ultramar, r * 0.88f, c, style = Stroke(r * 0.06f)) }
        else drawCircle(TrazosCiudad.enSombra(color, 0.35f), r * 0.88f, c, style = Stroke(r * 0.05f))
        if (luna) lunita(Offset(c.x, c.y - r * 0.48f), r * 0.22f, CREMA)
        else drawLine(CREMA.copy(alpha = 0.6f), Offset(c.x - r * 0.4f, c.y - r * 0.45f), Offset(c.x + r * 0.4f, c.y - r * 0.45f), r * 0.08f)
        drawCircle(CREMA, r * 0.07f, c)
        textoEn(medidor, numero, Rect(c.x - r * 0.75f, c.y + r * 0.16f, c.x + r * 0.75f, c.y + r * 0.62f), ROTULO, CREMA, 9f, minimo = 5f)
    }

    /**
     * El encabezado de la funda interior: a la izquierda, la etiqueta del disco del día con su número; a la derecha, el disco
     * del día como emblema (la etiqueta que gira en la ciudad, el disco hecho sol en la tinta); al centro, la cabecera, el
     * disco del día en el titular (o en letra de bloque, impresa dos veces), el día con el lado y la pista y el detalle; al
     * pie, los cables del trolebús o las bandas del cartel, con el disco chico en medio.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val k = k(p.oscura)
        val partes = e.fecha.split(" · ")
        val titular = partes.getOrElse(0) { e.fecha }
        val resto = partes.drop(1).joinToString(" · ")
        val color = if (e.noche) k.terciario else if (k.ciudad) TrazosCiudad.ETIQUETA else k.magenta
        etiquetaDisco(Offset(32 * u, 54 * u), 22 * u, color, Psicodelia.numero(e.numero), medidor, k, e.noche)
        with(PiezasPsicodelia) { emblema(Offset(w - 32 * u, 52 * u), 21 * u, 0.15f, p.oscura) }
        val x0 = 62 * u; val x1 = w - 62 * u
        textoEn(medidor, e.rotulo, Rect(x0, 10 * u, x1, 24 * u), ROTULO, p.acento, 9.5f, espaciado = 0.6f, minimo = 6f)
        dosTintas(medidor, titular, Rect(x0, 27 * u, x1, 58 * u), titulo(k), p.tinta, k, 24f, minimo = 10f)
        textoEn(medidor, resto, Rect(x0, 60 * u, x1, 76 * u), TEXTO, p.detalle, 11f, minimo = 6f)
        textoEn(medidor, e.detalle, Rect(x0, 77 * u, x1, 91 * u), TEXTO, p.tenue, 10f, minimo = 6f)
        banda(18 * u, w / 2 - 10 * u, 100 * u, k, 0.85f)
        banda(w / 2 + 10 * u, w - 18 * u, 100 * u, k, 0.85f)
        with(PiezasPsicodelia) { disco(Offset(w / 2, 100 * u), 5 * u, k) }
        drawLine(p.tinta.copy(alpha = 0.14f), Offset(4 * u, h - 2 * u), Offset(w - 4 * u, h - 2 * u), 0.6f * u)
    }

    /**
     * Sobre el papel: el roce redondo que deja el disco en la funda, abajo a la derecha, y el remate del pie (el disco chico
     * entre dos bandas). En el modo oscuro, el papel se hunde en las orillas (la penumbra del cuarto o el terciopelo) y el
     * centro, donde se lee, queda claro. De noche (los sueños), la luna y, en la tinta, las luces líquidas en manchas planas.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val k = k(p.oscura)
        val rc = Offset(w - 64 * u, h - 150 * u)
        drawCircle(p.tinta.copy(alpha = 0.035f), 60 * u, rc, style = Stroke(10 * u))
        drawCircle(p.tinta.copy(alpha = 0.03f), 22 * u, rc, style = Stroke(3 * u))
        val y = h - 44 * u
        banda(w / 2 - w * 0.18f, w / 2 - 9 * u, y, k, 0.45f)
        banda(w / 2 + 9 * u, w / 2 + w * 0.18f, y, k, 0.45f)
        with(PiezasPsicodelia) { disco(Offset(w / 2, y), 4.5f * u, k, sombra = false) }
        if (oscuro && !p.oscura) {
            val orilla = if (k.ciudad) TrazosCiudad.PARED_NOCHE else NEGRO
            drawRect(Brush.radialGradient(0f to Color.Transparent, 0.45f to Color.Transparent, 1f to orilla.copy(alpha = 0.6f),
                center = Offset(w * 0.5f, h * 0.3f), radius = maxOf(w, h) * 0.9f))
        }
        if (noche) {
            val l = Offset(w - 40 * u, h - 96 * u)
            if (!k.ciudad) listOf(k.magenta, k.ultramar, if (k.m.bermellon > 0f) k.bermellon else k.magenta).forEachIndexed { j, c ->
                drawCircle(c.copy(alpha = 0.06f), (26f + 22f * azar(911, j)) * u, Offset(w * (0.12f + 0.2f * j), h * (0.8f + 0.08f * azar(913, j))))
            }
            drawCircle(Brush.radialGradient(listOf((if (k.ciudad) TrazosCiudad.SOL_VELADO else CREMA).copy(alpha = 0.18f), Color.Transparent), l, 40 * u), 40 * u, l)
            lunita(l, 12 * u, (if (k.ciudad) TrazosCiudad.SOL_VELADO else CREMA).copy(alpha = 0.7f))
        }
    }

    /** La barra de arriba: el borde de la funda interior, con su banda y, al pie, una franja de la tinta del tema. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val k = k(p.oscura || oscuro)
        drawRect(if (p.oscura) lerp(p.color, Color.Black, 0.3f) else lerp(p.color, k.variante, 0.5f))
        banda(0f, w, h - 9 * u, k, 0.8f)
        if (k.ciudad) {
            drawRect(k.primario, Offset(0f, h - 4 * u), Size(w, 4 * u))
            drawRect(TrazosCiudad.AL_SOL.copy(alpha = if (k.oscuro) 0.08f else 0.3f), Offset(0f, h - 4 * u), Size(w, 1 * u))
        } else {
            conRegistro(TrazosTinta.Tinta.ULTRAMAR, 0f) { drawRect(k.ultramar, Offset(0f, h - 4 * u), Size(w, 4 * u)) }
            conRegistro(TrazosTinta.Tinta.MAGENTA, k.m.registro * 0.4f) { drawRect(k.magenta.copy(alpha = 0.85f), Offset(0f, h - 4 * u), Size(w, 1.8f * u)) }
        }
        drawLine(Color.Black.copy(alpha = 0.3f), Offset(0f, h - 0.5f * u), Offset(w, h - 0.5f * u), 0.6f * u)
    }

    /**
     * El botón de guardar. La ciudad: la placa redonda del naranja quemado, con su sombra café corta y el brillo de la tarde
     * arriba, y un disco chico en cada punta. La tinta: el marco de arco del cartel, impreso dos veces (el ultramar corrido
     * debajo, el magenta encima), con su filete por dentro.
     */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val k = k(oscuro || p.oscura)
        val r = Rect(3 * u, 3 * u, w - 5 * u, h - 6 * u)
        if (r.width <= 4 * u || r.height <= 4 * u) return
        if (k.ciudad) {
            val rad = CornerRadius(r.height / 2f)
            drawRoundRect(k.sombra, r.topLeft + Offset(1.2f * u, 2.4f * u), r.size, rad)
            drawRoundRect(k.primario, r.topLeft, r.size, rad)
            drawRoundRect(TrazosCiudad.AL_SOL.copy(alpha = if (k.oscuro) 0.12f else 0.28f), Offset(r.left + 10 * u, r.top + 2 * u), Size(r.width - 20 * u, r.height * 0.26f), CornerRadius(r.height * 0.13f))
            val f = r.deflate(2.6f * u)
            drawRoundRect(k.sobrePrimario.copy(alpha = 0.35f), f.topLeft, f.size, CornerRadius(f.height / 2f), style = Stroke(0.7f * u))
        } else {
            val forma = TrazosTinta.arco(r, r.height / 2f, 2f * u)
            translate((1.6f + k.m.registro * 0.6f) * u, 1.6f * u) { drawPath(forma, k.secundario) }
            drawPath(forma, k.primario)
            drawPath(TrazosTinta.arco(r.deflate(2.6f * u), (r.height / 2f - 2.6f * u).coerceAtLeast(1f), 1f * u), k.sobrePrimario.copy(alpha = 0.45f), style = Stroke(0.7f * u))
        }
        listOf(r.left + r.height * 0.5f, r.right - r.height * 0.5f).forEach { x ->
            with(PiezasPsicodelia) { disco(Offset(x, r.center.y), minOf(5.5f * u, r.height * 0.24f), k, sombra = false) }
        }
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = k(oscuro || p.oscura).sobrePrimario

    // =================================================================
    //  La libreta de momentos: el periódico del barrio, en la mesa de la cocina
    // =================================================================

    override val altoLibreta: Dp = 270.dp

    /** El cartel de la semana de David en [q], a tres tintas (o a dos sin la intensidad), con su marco de arco. */
    private fun DrawScope.cartelChico(q: Rect, k: ColoresPsi, semilla: Int) {
        val u = density
        val kc = if (k.ciudad) PiezasPsicodelia.colores(k.oscuro, Cara.INTENSA, 0f) else k
        val papel = if (k.ciudad) noche(TrazosCiudad.pelicula(TrazosTinta.PAPEL), k, 0.55f) else kc.papel
        drawRect(papel, q.topLeft, q.size)
        val marco = TrazosTinta.arco(q.deflate(q.width * 0.06f), q.width * 0.4f, 1f * u)
        val c = Offset(q.center.x, q.top + q.height * 0.36f)
        clipPath(marco) {
            with(PiezasPsicodelia) {
                corona(c, q.width * 0.16f, q.width * 0.9f, kc, giro = semilla * 13f, n = 16)
                disco(c, q.width * 0.14f, kc, sombra = false)
            }
        }
        drawPath(marco, linea(kc).copy(alpha = if (k.ciudad) 0.75f else 1f), style = Stroke(1f * u))
        // En la ciudad, el cartel es una cosa de la foto: la película lo apaga.
        if (k.ciudad) drawRect(TrazosCiudad.SOMBRA.copy(alpha = if (k.oscuro) 0.4f else 0.12f), q.topLeft, q.size)
    }

    /**
     * La portada del periódico del barrio, doblada a la mitad, en [r]: el nombre inventado, «La Niebla», en tinta de tintero
     * partido entre dos tintas; las orejas de la cabecera (el disco chico y el número de la edición); la línea de la fecha con
     * el nombre y la cuenta de la libreta; abajo, el grabado del disco hecho sol y las columnas.
     */
    private fun DrawScope.primeraPlana(r: Rect, e: EstadoLibreta, medidor: TextMeasurer, k: ColoresPsi) {
        val u = density
        val papelP = if (k.ciudad) (if (k.oscuro) Color(0xFF4A4036) else PRENSA) else (if (k.oscuro) lerp(NEGRO, CREMA, 0.16f) else k.papel)
        val tintaP = if (k.oscuro) CREMA else (if (k.ciudad) Color(0xFF2A2119) else k.grabado)
        val plana = Path().apply { addRect(r) }
        drawPath(plana, papelP)
        // El papel barato: fibras y el amarillo de las orillas.
        for (j in 0 until 30) drawLine(tintaP.copy(alpha = 0.05f), Offset(r.left + r.width * azar(e.semilla, j), r.top + r.height * azar(e.semilla, j + 40)),
            Offset(r.left + r.width * azar(e.semilla, j) + 3 * u, r.top + r.height * azar(e.semilla, j + 40) + 1 * u), 0.5f * u)
        if (k.ciudad) drawRect(Brush.radialGradient(0f to Color.Transparent, 0.7f to Color.Transparent, 1f to Color(0xFFB89A62).copy(alpha = if (k.oscuro) 0.1f else 0.25f),
            center = r.center, radius = r.width * 0.75f), r.topLeft, r.size)
        val m = r.deflate(6 * u)
        // La cabecera, en tinta de tintero partido entre dos tintas.
        val cab = Rect(m.left + m.width * 0.16f, m.top, m.right - m.width * 0.16f, m.top + m.height * 0.3f)
        val tintas = if (k.ciudad) listOf(TrazosCiudad.pelicula(TrazosTinta.tinta(TrazosTinta.Tinta.MAGENTA, k.oscuro, 0f)), TrazosCiudad.pelicula(TrazosTinta.tinta(TrazosTinta.Tinta.ULTRAMAR, k.oscuro, 0f)))
            else listOf(k.magenta, if (k.m.bermellon > 0.5f) k.bermellon else k.ultramar)
        tintero(medidor, "La Niebla", cab, titulo(k), 30f, tintas, minimo = 10f)
        // Las orejas: el disco chico a la izquierda y el número de la edición a la derecha.
        val oreja = m.height * 0.24f
        with(PiezasPsicodelia) { disco(Offset(m.left + oreja * 0.55f, cab.center.y), oreja * 0.36f, k, sombra = false) }
        val or2 = Rect(m.right - m.width * 0.15f, cab.top + 2 * u, m.right, cab.bottom - 2 * u)
        drawRect(tintaP.copy(alpha = 0.6f), or2.topLeft, or2.size, style = Stroke(0.6f * u))
        textoEn(medidor, "Nº", Rect(or2.left, or2.top, or2.right, or2.center.y), ROTULO, tintaP, 7f, minimo = 4f)
        textoEn(medidor, Psicodelia.numero(e.hojas.coerceAtLeast(1)), Rect(or2.left, or2.center.y - 1 * u, or2.right, or2.bottom), ROTULO, tintaP, 8f, minimo = 4f)
        // La línea de la fecha, entre dos rayas: el nombre y la cuenta.
        val yl = cab.bottom + 2 * u
        drawLine(tintaP, Offset(m.left, yl), Offset(m.right, yl), 0.9f * u)
        textoEn(medidor, e.nombre + " · " + e.cuenta, Rect(m.left, yl + 1 * u, m.right, yl + 11 * u), ROTULO, tintaP, 7.5f, minimo = 4.5f)
        drawLine(tintaP, Offset(m.left, yl + 12 * u), Offset(m.right, yl + 12 * u), 0.5f * u)
        // El grabado del disco hecho sol, a la izquierda: los rayos que ondulan en la tinta que graba y, encima, la otra tinta.
        val cuerpo = Rect(m.left, yl + 15 * u, m.right, m.bottom)
        val mc = Offset(cuerpo.left + cuerpo.width * 0.27f, cuerpo.top + cuerpo.height * 0.62f)
        val mr = cuerpo.height * 0.56f
        clipRect(cuerpo.left, cuerpo.top, cuerpo.left + cuerpo.width * 0.53f, cuerpo.bottom) {
            drawPath(TrazosTinta.rayos(mc, mr * 0.5f, mr * 1.3f, 24, mr * 0.12f, 1.4f, e.semilla * 0.3f), tintaP.copy(alpha = 0.85f))
            conRegistro(TrazosTinta.Tinta.MAGENTA, if (k.ciudad) 0.6f else k.m.registro * 0.5f) { drawCircle(tintas[0].copy(alpha = 0.8f), mr * 0.45f, mc) }
            drawCircle(papelP, mr * 0.2f, mc)
            drawCircle(tintaP, mr * 0.45f, mc, style = Stroke(0.8f * u))
        }
        // Las columnas de texto, a la derecha, con su titular.
        val cols = Rect(cuerpo.left + cuerpo.width * 0.56f, cuerpo.top + 2 * u, cuerpo.right, cuerpo.bottom)
        drawRect(tintaP.copy(alpha = 0.85f), Offset(cols.left, cols.top), Size(cols.width * 0.9f, 3 * u))
        drawRect(tintaP.copy(alpha = 0.85f), Offset(cols.left, cols.top + 5 * u), Size(cols.width * 0.6f, 3 * u))
        val ancho = (cols.width - 4 * u) / 2f
        for (j in 0 until 2) {
            var y = cols.top + 12 * u; var n = 0
            while (y < cols.bottom - 2 * u) {
                val largo = ancho * (if (n % 6 == 5) 0.5f else 0.92f)
                drawLine(tintaP.copy(alpha = 0.35f), Offset(cols.left + j * (ancho + 4 * u), y), Offset(cols.left + j * (ancho + 4 * u) + largo, y), 1.1f * u)
                y += 3 * u; n++
            }
        }
        drawLine(tintaP.copy(alpha = 0.4f), Offset(cols.left - 3 * u, cuerpo.top), Offset(cols.left - 3 * u, cuerpo.bottom), 0.5f * u)
        grano(k, plana)
    }

    /**
     * El periódico del barrio, «La Niebla» (nombre inventado: se vendía en la calle), sobre la mesa de pino de la cocina del
     * departamento, contra la pared (sin ventana): en la pared, el cartel de la semana de David clavado con chinchetas y una
     * postal; en la mesa, la taza de café y el lápiz. El periódico está doblado a la mitad y debajo asoman las ediciones
     * anteriores (engorda con las hojas). Al abrirse, la plana gira sobre su lomo y deja ver las columnas de adentro. La luz:
     * de día, la de la tarde por la izquierda; de noche, la lámpara de pantalla por arriba (la ciudad) o las manchas de las
     * luces líquidas en la pared (la tinta).
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val k = k(oscuro)
        val a = e.abierto.coerceIn(0f, 1f)
        val mesaY = h * 0.42f
        // La pared de la cocina.
        val pared = if (k.ciudad) noche(YESO, k, 0.7f) else (if (k.oscuro) NEGRO else k.papel)
        drawRect(pared, Offset.Zero, Size(w, mesaY))
        if (k.ciudad && !k.oscuro) drawRect(Brush.horizontalGradient(listOf(TrazosCiudad.AL_SOL.copy(alpha = 0.3f), Color.Transparent), 0f, w * 0.7f), Offset.Zero, Size(w, mesaY))
        if (!k.ciudad && k.oscuro) {
            // La proyección de las luces líquidas en la pared: tres manchas planas de tinta.
            clipRect(0f, 0f, w, mesaY) {
                drawCircle(k.magenta.copy(alpha = 0.22f + 0.1f * k.i), w * 0.2f, Offset(w * 0.24f, mesaY * 0.4f))
                drawCircle(k.ultramar.copy(alpha = 0.22f + 0.1f * k.i), w * 0.17f, Offset(w * 0.72f, mesaY * 0.55f))
                if (k.m.bermellon > 0f) drawCircle(k.bermellon.copy(alpha = 0.2f * k.m.bermellon), w * 0.13f, Offset(w * 0.5f, mesaY * 0.2f))
            }
        }
        // El cartel de la semana, clavado con dos chinchetas, y su sombra.
        val cartel = Rect(w * 0.18f, h * 0.05f, w * 0.18f + mesaY * 0.62f, mesaY - 10 * u)
        rotate(-1.5f, cartel.center) {
            drawRect(k.sombra, cartel.topLeft + Offset(3 * u, 4 * u), cartel.size)
            cartelChico(cartel, k, e.semilla)
            listOf(cartel.left + 4 * u, cartel.right - 4 * u).forEach { x -> chincheta(Offset(x, cartel.top + 4 * u), 2 * u, k.primario, k) }
        }
        // A la derecha, una postal clavada.
        val po = Rect(w * 0.62f, h * 0.1f, w * 0.62f + w * 0.14f, h * 0.1f + w * 0.14f / 1.45f)
        rotate(5f, Offset(po.center.x, po.top)) {
            postalChica(po, e.semilla, k, false)
            chincheta(Offset(po.center.x, po.top + 3 * u), 2 * u, k.secundario, k)
        }
        // La mesa de pino, en perspectiva, con su canto.
        val mesa = Path().apply { moveTo(w * 0.03f, mesaY); lineTo(w * 0.97f, mesaY); lineTo(w, h * 0.9f); lineTo(0f, h * 0.9f); close() }
        clipPath(mesa) {
            pino(Rect(0f, mesaY, w, h * 0.9f), PINO, k, 501, nudos = 2)
            if (k.ciudad) drawRect(Brush.verticalGradient(listOf(k.sombra, Color.Transparent), mesaY, mesaY + 18 * u), Offset(0f, mesaY), Size(w, 18 * u))
        }
        val canto = Rect(0f, h * 0.9f, w, h)
        pino(canto, PINO_OSC, k, 503, nudos = 0)
        if (k.ciudad) drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (k.oscuro) 0.03f else 0.16f), Color.Transparent, Color.Black.copy(alpha = 0.3f)), canto.top, canto.bottom), canto.topLeft, canto.size)
        // La taza de café y el lápiz.
        val tc = Offset(w * 0.86f, h * 0.6f)
        val loza = if (k.ciudad) noche(TrazosCiudad.ETIQUETA, k, 0.5f) else k.magenta
        drawOval(k.sombra, Offset(tc.x - 15 * u, tc.y - 4 * u), Size(32 * u, 16 * u))
        drawOval(loza, Offset(tc.x - 14 * u, tc.y - 10 * u), Size(28 * u, 18 * u))
        drawOval(if (k.ciudad) noche(Color(0xFF4A2A18), k) else linea(k), Offset(tc.x - 11 * u, tc.y - 8 * u), Size(22 * u, 13 * u))
        drawArc(loza, -60f, 120f, false, Offset(tc.x + 9 * u, tc.y - 7 * u), Size(10 * u, 10 * u), style = Stroke(3 * u))
        if (!k.ciudad) drawOval(linea(k), Offset(tc.x - 14 * u, tc.y - 10 * u), Size(28 * u, 18 * u), style = Stroke(0.7f * u))
        rotate(-18f, Offset(w * 0.1f, h * 0.8f)) {
            drawLine(k.sombra, Offset(w * 0.04f, h * 0.82f), Offset(w * 0.2f, h * 0.82f), 4 * u, StrokeCap.Round)
            drawLine(if (k.ciudad) noche(OCRE, k, 0.4f) else k.ultramar, Offset(w * 0.04f, h * 0.81f), Offset(w * 0.18f, h * 0.81f), 3.4f * u)
            drawPath(Path().apply { moveTo(w * 0.18f, h * 0.81f - 1.7f * u); lineTo(w * 0.205f, h * 0.81f); lineTo(w * 0.18f, h * 0.81f + 1.7f * u); close() }, if (k.ciudad) noche(PINO, k) else k.papel)
        }
        // El periódico.
        val cw = minOf(w * 0.56f, (h * 0.9f - mesaY) * 1.36f)
        val ch = cw / 1.36f
        val x0 = w * 0.47f - cw / 2
        val y0 = mesaY + (h * 0.9f - mesaY - ch) * 0.45f
        val grueso = (1.5f + minOf(e.hojas, 50) * 0.11f) * u
        val cara = Rect(x0, y0, x0 + cw, y0 + ch)
        rotate(-2f, cara.center) {
            drawRect(k.sombra, Offset(cara.left + 4 * u, cara.top + grueso + 4 * u), cara.size)
            // Las ediciones de antes, apiladas debajo.
            val pila = Rect(cara.left + 1.5f * u, cara.top + 1.5f * u, cara.right + grueso * 0.6f, cara.bottom + grueso)
            drawRect(if (k.ciudad) noche(lerp(PRENSA, Color(0xFFC9B48A), 0.35f), k) else (if (k.oscuro) lerp(NEGRO, CREMA, 0.1f) else lerp(k.papel, k.ultramar, 0.12f)), pila.topLeft, pila.size)
            var yy = cara.bottom + 1 * u
            while (yy < pila.bottom) { drawLine(Color.Black.copy(alpha = 0.14f), Offset(pila.left, yy), Offset(pila.right, yy), 0.4f * u); yy += 1.2f * u }
            // Debajo de la plana, las columnas de adentro.
            val girar = ((a - 0.15f) / 0.85f).coerceIn(0f, 1f)
            if (girar > 0.01f) {
                val papelI = if (k.ciudad) (if (k.oscuro) Color(0xFF4A4036) else PRENSA) else (if (k.oscuro) lerp(NEGRO, CREMA, 0.16f) else k.papel)
                val tintaI = if (k.oscuro) CREMA else (if (k.ciudad) Color(0xFF2A2119) else k.grabado)
                drawRect(papelI, cara.topLeft, cara.size)
                val cols = 3
                val anchoC = (cara.width - 20 * u) / cols
                for (j in 0 until cols) {
                    var y = cara.top + 14 * u; var n = 0
                    while (y < cara.bottom - 8 * u) { drawLine(tintaI.copy(alpha = 0.3f), Offset(cara.left + 8 * u + j * (anchoC + 2 * u), y), Offset(cara.left + 8 * u + j * (anchoC + 2 * u) + anchoC * (if (n % 7 == 6) 0.5f else 0.92f), y), 1f * u); y += 3 * u; n++ }
                }
                drawRect(papelI, Offset(cara.left + 8 * u, cara.top + 4 * u), Size(cara.width - 16 * u, 9 * u))
                textoEn(medidor, e.cuenta, Rect(cara.left + 8 * u, cara.top + 3 * u, cara.right - 8 * u, cara.top + 13 * u), TEXTO, tintaI, 8.5f, minimo = 5f)
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent), cara.left, cara.left + 14 * u), cara.topLeft, Size(14 * u, cara.height))
            }
            val ang = girar * 1.5f
            val co = cos(ang); val si = sin(ang)
            afin(co, -si * 0.1f, 0f, 1f, cara.left - cara.left * co, cara.left * si * 0.1f) {
                primeraPlana(cara, e, medidor, k)
                drawRect(if (k.ciudad) k.sombra else linea(k).copy(alpha = 0.6f), cara.topLeft, cara.size, style = Stroke(0.6f * u))
                if (girar > 0f) drawRect(Color.Black.copy(alpha = 0.45f * (1f - co)), cara.topLeft, cara.size)
            }
            if (girar < 0.02f) drawLine(Color.White.copy(alpha = if (k.oscuro) 0.08f else 0.35f), Offset(cara.left, cara.bottom - 0.6f * u), Offset(cara.right, cara.bottom - 0.6f * u), 1f * u)
        }
        // La luz de la ciudad: de noche, la lámpara de pantalla por arriba a la izquierda y lo lejano perdido; y el grano.
        if (k.ciudad) {
            if (k.oscuro) drawRect(Brush.radialGradient(0f to TrazosCiudad.LAMPARA_LUZ.copy(alpha = 0.1f), 0.35f to Color.Transparent, 1f to TrazosCiudad.PARED_NOCHE.copy(alpha = 0.55f),
                center = Offset(w * 0.3f, 0f), radius = w * 1.05f), Offset.Zero, Size(w, h))
            grano(k, null, 0.5f)
        }
    }

    /**
     * La pestaña de un día: un sencillo de vinilo con su etiqueta del color del ánimo (crema, si no lo hay), con el número del
     * día en Libre Franklin y, debajo, la letra de la semana.
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val k = k(oscuro)
        val c = Offset(w / 2, 22 * u)
        val r = minOf(w / 2 - 2 * u, 20 * u)
        rotate(-4f + 8f * azar(e.semilla, 1), c) {
            drawCircle(k.sombra, r, c + Offset(1 * u, 1.6f * u))
            drawCircle(VINILO, r, c)
            var g = r * 0.95f
            while (g > r * 0.62f) { drawCircle(Color.White.copy(alpha = 0.07f), g, c, style = Stroke(0.4f * u)); g -= 1.3f * u }
            if (k.ciudad) drawArc(TrazosCiudad.AL_SOL.copy(alpha = 0.22f), 200f + 40f * azar(e.semilla, 2), 34f, true, Offset(c.x - r * 0.95f, c.y - r * 0.95f), Size(r * 1.9f, r * 1.9f))
            val etiqueta = e.tono ?: if (k.oscuro) Color(0xFF3A3029) else CREMA
            drawCircle(etiqueta, r * 0.6f, c)
            drawCircle(if (k.ciudad) Color.Black.copy(alpha = 0.15f) else linea(k).copy(alpha = 0.6f), r * 0.6f, c, style = Stroke(0.5f * u))
            val tx = if (etiqueta.luminance() > 0.3f) Color(0xFF2A2119) else CREMA
            textoEn(medidor, e.dia, Rect(c.x - r * 0.55f, c.y - r * 0.45f, c.x + r * 0.55f, c.y + r * 0.45f), ROTULO, tx, 13f, minimo = 6f)
        }
        textoEn(medidor, e.semana, Rect(0f, c.y + r + 1.5f * u, w, h - 1 * u), ROTULO, k.tenue, 8.5f, espaciado = 0.4f, minimo = 5f)
    }

    /** El rótulo de un mes: el nombre del álbum en el titular (o en bloque), entre dos bandas que acaban en un disco chico. */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val k = k(oscuro)
        val tam = 15f
        val letraM = titulo(k)
        val largo = (anchoTx(medidor, texto, letraM, tam) + 18 * u).coerceAtMost(w - 70 * u)
        banda(12 * u, w / 2 - largo / 2 - 4 * u, h / 2, k, 0.9f)
        banda(w / 2 + largo / 2 + 4 * u, w - 12 * u, h / 2, k, 0.9f)
        with(PiezasPsicodelia) {
            disco(Offset(12 * u, h / 2), 4.5f * u, k, sombra = false)
            disco(Offset(w - 12 * u, h / 2), 4.5f * u, k, sombra = false)
        }
        dosTintas(medidor, texto, Rect(w / 2 - largo / 2, 0f, w / 2 + largo / 2, h), letraM, k.tinta, k, tam, minimo = 8f)
    }

    // =================================================================
    //  Las cartas: postales de 1967
    // =================================================================

    /**
     * El anverso de una postal en [r]: la ilustración con su borde blanco y abajo la franja donde se escribe, con [linea1] y
     * [linea2]. La ciudad (una foto, según [variante]): la calle en cuesta con niebla y sus victorianas, el parque con su
     * colina, o la bahía con la niebla que entra. La tinta (un cartel impreso): el disco hecho sol con su corona, las bandas
     * que se abren alrededor del disco, o los rayos con su trama dentro del marco de arco. Encima, una etiqueta de disco pegada.
     */
    private fun DrawScope.anverso(r: Rect, variante: Int, semilla: Int, linea1: String, linea2: String, medidor: TextMeasurer, k: ColoresPsi, g: Float) {
        val u = density
        val blanco = if (k.ciudad) noche(Color(0xFFF4ECDD), k, 0.55f) else (if (k.oscuro) lerp(NEGRO, CREMA, 0.12f) else k.papel)
        drawRect(blanco, r.topLeft, r.size)
        val franja = r.top + r.height * 0.74f
        val il = Rect(r.left + r.width * 0.045f, r.top + r.height * 0.06f, r.right - r.width * 0.045f, franja)
        clipRect(il.left, il.top, il.right, il.bottom) {
            if (k.ciudad) {
                fun c(x: Color) = noche(x, k, 0.5f)
                when (variante) {
                    0 -> {
                        drawRect(Brush.verticalGradient(listOf(c(TrazosCiudad.BRUMA), c(TrazosCiudad.LUZ_BAJA)), il.top, il.bottom), il.topLeft, il.size)
                        val anchoC = il.width * 0.3f
                        for (j in 0 until 4) {
                            val base = Offset(il.left + anchoC * (0.45f + j * 0.98f), il.bottom - il.height * 0.08f + j * il.height * 0.07f)
                            victoriana(base, anchoC * 0.92f, il.height * 0.7f, c(TrazosCiudad.CASAS[(j + semilla).mod(TrazosCiudad.CASAS.size)]), c(Color(0xFF5E6B70)), j % 2 == 1)
                        }
                        drawPath(Path().apply { moveTo(il.left, il.bottom - il.height * 0.08f); lineTo(il.right, il.bottom + il.height * 0.2f); lineTo(il.right, il.bottom); lineTo(il.left, il.bottom); close() }, c(Color(0xFF8E877C)))
                        for (j in 0 until 3) drawOval(Color.White.copy(alpha = if (k.oscuro) 0.06f else 0.3f), Offset(il.left - il.width * 0.2f + j * il.width * 0.3f, il.top + il.height * (0.3f + 0.18f * j)), Size(il.width * 0.8f, il.height * 0.12f))
                    }
                    1 -> {
                        drawRect(Brush.verticalGradient(listOf(c(Color(0xFFB9C2BC)), c(TrazosCiudad.SOL_VELADO)), il.top, il.bottom), il.topLeft, il.size)
                        val colina = Path().apply { moveTo(il.left, il.bottom); lineTo(il.left, il.bottom - il.height * 0.22f); cubicTo(il.left + il.width * 0.3f, il.bottom - il.height * 0.45f, il.left + il.width * 0.7f, il.bottom - il.height * 0.4f, il.right, il.bottom - il.height * 0.18f); lineTo(il.right, il.bottom); close() }
                        drawPath(colina, c(Color(0xFF6E7A52)))
                        listOf(0.62f to 0.5f, 0.78f to 0.46f, 0.9f to 0.38f).forEach { (fx, fh) ->
                            val x = il.left + il.width * fx
                            val pie = il.bottom - il.height * 0.3f
                            drawLine(c(TrazosCiudad.MADERA), Offset(x, pie), Offset(x, pie - il.height * fh * 0.4f), 1.4f * u * g)
                            drawOval(c(VERDE_BOTELLA), Offset(x - il.height * 0.12f, pie - il.height * fh), Size(il.height * 0.24f, il.height * fh * 0.75f))
                        }
                    }
                    else -> {
                        drawRect(Brush.verticalGradient(listOf(c(TrazosCiudad.BRUMA), c(Color(0xFFE6D8BE))), il.top, il.bottom), il.topLeft, il.size)
                        val agua = il.top + il.height * 0.62f
                        drawRect(c(Color(0xFF6F7F82)), Offset(il.left, agua), Size(il.width, il.bottom - agua))
                        drawPath(Path().apply { moveTo(il.left, agua); cubicTo(il.left + il.width * 0.25f, agua - il.height * 0.2f, il.left + il.width * 0.45f, agua - il.height * 0.14f, il.left + il.width * 0.6f, agua); close() }, c(Color(0xFF7D7A62)))
                        for (j in 0 until 4) drawLine(Color.White.copy(alpha = 0.2f), Offset(il.left + il.width * (0.1f + 0.2f * j), agua + il.height * (0.08f + 0.06f * (j % 2))), Offset(il.left + il.width * (0.2f + 0.2f * j), agua + il.height * (0.08f + 0.06f * (j % 2))), 0.6f * u)
                        drawOval(Color.White.copy(alpha = if (k.oscuro) 0.06f else 0.4f), Offset(il.left + il.width * 0.3f, agua - il.height * 0.2f), Size(il.width * 0.9f, il.height * 0.18f))
                    }
                }
                grano(k, Path().apply { addRect(il) }, 0.6f)
            } else {
                drawRect(k.papel, il.topLeft, il.size)
                val cc = Offset(il.center.x, il.top + il.height * 0.45f)
                with(PiezasPsicodelia) {
                    when (variante) {
                        0 -> { corona(cc, il.height * 0.22f, il.width * 0.7f, k, giro = semilla * 9f, n = 18); disco(cc, il.height * 0.2f, k, sombra = false) }
                        1 -> {
                            with(TrazosTinta) {
                                conRegistro(TrazosTinta.Tinta.ULTRAMAR, k.m.registro) { bandas(cc, il.height * 1.4f, 5, il.width * 0.07f, il.width * (0.02f + 0.04f * k.i), il.width * 0.022f, k.ultramar, 1.3f, semilla * 0.1f, 0.9f) }
                                conRegistro(TrazosTinta.Tinta.MAGENTA, k.m.registro) { bandas(cc, il.height * 1.4f, 4, il.width * 0.07f, il.width * (0.02f + 0.04f * k.i), il.width * 0.012f, k.magenta, 1.3f, semilla * 0.1f + 0.05f, 0.9f) }
                            }
                            disco(cc, il.height * 0.18f, k, sombra = false)
                        }
                        else -> {
                            val marco = TrazosTinta.arco(il.deflate(il.width * 0.08f), il.width * 0.3f, 1f * u)
                            clipPath(marco) { drawPath(TrazosTinta.rayos(cc, il.height * 0.15f, il.width, 16, il.width * 0.04f, 1.5f), linea(k)) }
                            drawPath(marco, linea(k), style = Stroke(1f * u))
                            disco(cc, il.height * 0.14f, k, sombra = false)
                        }
                    }
                }
            }
        }
        drawRect(if (k.ciudad) Color.Black.copy(alpha = 0.18f) else linea(k).copy(alpha = 0.6f), il.topLeft, il.size, style = Stroke(0.5f * u))
        // La etiqueta de disco pegada encima, en una esquina.
        val p1 = if (semilla.mod(2) == 0) Offset(il.right - il.width * 0.08f, il.top + il.height * 0.14f) else Offset(il.left + il.width * 0.1f, il.bottom - il.height * 0.12f)
        etiquetaSello(p1, minOf(il.width, il.height) * 0.11f, if (variante == 1) k.secundario else k.primario, k, 1f, semilla * 0.2f)
        val tx = if (blanco.luminance() > 0.35f) (if (k.ciudad) Color(0xFF2A2119) else k.grabado) else CREMA
        val fr = Rect(r.left + r.width * 0.05f, franja + 2 * u, r.right - r.width * 0.17f, r.bottom - 2 * u)
        if (linea1.isNotBlank()) textoEn(medidor, linea1, Rect(fr.left, fr.top, fr.right, fr.top + fr.height * 0.55f), ROTULO, tx, 10f * g, minimo = 5f)
        if (linea2.isNotBlank()) textoEn(medidor, linea2, Rect(fr.left, fr.top + fr.height * 0.52f, fr.right, fr.bottom), TEXTO, tx.copy(alpha = 0.75f), 8f * g, minimo = 4.5f)
    }

    /**
     * El reverso de una postal en [r], el lado dividido: «Post Card» arriba, la raya del medio, a la izquierda los renglones
     * para lo que se escribe (en [renglones]), a la derecha la estampilla con el puente (propia, sin copiar ninguna), el
     * matasellos de San Francisco con sus ondas y las líneas de la dirección, con [linea1] y [linea2].
     */
    private fun DrawScope.reverso(r: Rect, linea1: String, linea2: String, hoja: Color, renglones: Color, escrito: Float, medidor: TextMeasurer, k: ColoresPsi, g: Float) {
        val u = density
        val blanco = if (k.ciudad) noche(Color(0xFFF4ECDD), k, 0.55f) else (if (k.oscuro) lerp(NEGRO, CREMA, 0.12f) else k.papel)
        drawRect(lerp(blanco, hoja, 0.2f), r.topLeft, r.size)
        val tintaC = if (blanco.luminance() > 0.35f) (if (k.ciudad) Color(0xFF2A2119) else k.grabado) else CREMA
        textoEn(medidor, "Post Card", Rect(r.left, r.top + r.height * 0.03f, r.right, r.top + r.height * 0.16f), ROTULO, tintaC.copy(alpha = 0.8f), 10f * g, espaciado = 1.4f, minimo = 5f)
        val medio = r.left + r.width * 0.52f
        drawLine(tintaC.copy(alpha = 0.5f), Offset(medio, r.top + r.height * 0.22f), Offset(medio, r.bottom - r.height * 0.06f), 0.7f * u)
        var y = r.top + r.height * 0.32f; var j = 0
        while (y < r.bottom - r.height * 0.08f) {
            drawLine(renglones, Offset(r.left + r.width * 0.06f, y), Offset(medio - r.width * 0.05f, y), 0.5f * u)
            if (escrito > 0f && j < 6) {
                val largo = (medio - r.width * 0.11f - r.left - r.width * 0.06f) * (0.6f + 0.35f * azar(j, 3)) * escrito.coerceIn(0f, 1f)
                drawLine(tintaC.copy(alpha = 0.18f), Offset(r.left + r.width * 0.06f, y - 2.5f * u * g), Offset(r.left + r.width * 0.06f + largo, y - 2.5f * u * g), 1f * u * g, StrokeCap.Round)
            }
            y += 9 * u * g; j++
        }
        // La estampilla: el puente con sus dos torres sobre la niebla, «U.S.» y el año.
        val st = Rect(r.right - r.width * 0.2f, r.top + r.height * 0.17f, r.right - r.width * 0.05f, r.top + r.height * 0.17f + r.width * 0.18f)
        drawRect(blanco, st.topLeft, st.size)
        var x = st.left
        while (x <= st.right) { drawCircle(lerp(blanco, hoja, 0.2f), 0.9f * u * g, Offset(x, st.top)); drawCircle(lerp(blanco, hoja, 0.2f), 0.9f * u * g, Offset(x, st.bottom)); x += 2.4f * u * g }
        val dentro = st.deflate(2 * u * g)
        val fondoSt = if (k.ciudad) noche(TrazosCiudad.CASA_PINTADA, k, 0.4f) else k.ultramar
        drawRect(fondoSt, dentro.topLeft, dentro.size)
        val claro = CREMA
        clipRect(dentro.left, dentro.top, dentro.right, dentro.bottom) {
            drawRect(claro.copy(alpha = 0.35f), Offset(dentro.left, dentro.bottom - dentro.height * 0.28f), Size(dentro.width, dentro.height * 0.28f))
            listOf(0.3f, 0.72f).forEach { f -> drawRect(claro, Offset(dentro.left + dentro.width * f - 0.8f * u, dentro.top + dentro.height * 0.3f), Size(1.6f * u, dentro.height * 0.55f)) }
            drawPath(Path().apply { moveTo(dentro.left, dentro.top + dentro.height * 0.6f); quadraticTo(dentro.left + dentro.width * 0.3f, dentro.top + dentro.height * 0.62f, dentro.left + dentro.width * 0.3f, dentro.top + dentro.height * 0.32f)
                quadraticTo(dentro.left + dentro.width * 0.51f, dentro.top + dentro.height * 0.66f, dentro.left + dentro.width * 0.72f, dentro.top + dentro.height * 0.32f)
                quadraticTo(dentro.left + dentro.width * 0.72f, dentro.top + dentro.height * 0.62f, dentro.right, dentro.top + dentro.height * 0.6f) }, claro, style = Stroke(0.5f * u))
            drawLine(claro, Offset(dentro.left, dentro.top + dentro.height * 0.66f), Offset(dentro.right, dentro.top + dentro.height * 0.66f), 1 * u)
        }
        textoEn(medidor, "U.S.", Rect(dentro.left, dentro.top, dentro.left + dentro.width * 0.6f, dentro.top + dentro.height * 0.28f), ROTULO, claro, 5.5f * g, minimo = 3f)
        textoEn(medidor, "1967", Rect(dentro.right - dentro.width * 0.45f, dentro.bottom - dentro.height * 0.3f, dentro.right, dentro.bottom), ROTULO, claro, 6f * g, minimo = 3f)
        // El matasellos de San Francisco, con sus ondas.
        val ms = Offset(st.left - r.width * 0.03f, st.center.y + st.height * 0.12f)
        val mr = st.width * 0.5f
        val tintaM = tintaC.copy(alpha = 0.55f)
        drawCircle(tintaM, mr, ms, style = Stroke(0.8f * u * g))
        drawCircle(tintaM, mr * 0.7f, ms, style = Stroke(0.4f * u * g))
        textoEn(medidor, "San Francisco", Rect(ms.x - mr * 0.66f, ms.y - mr * 0.5f, ms.x + mr * 0.66f, ms.y - mr * 0.02f), ROTULO, tintaM, 5f * g, minimo = 2.5f)
        textoEn(medidor, "Calif.", Rect(ms.x - mr * 0.6f, ms.y + mr * 0.02f, ms.x + mr * 0.6f, ms.y + mr * 0.45f), ROTULO, tintaM, 5f * g, minimo = 2.5f)
        for (i in 0 until 5) {
            val yo = ms.y - mr * 0.5f + i * mr * 0.25f
            drawPath(onda(Offset(ms.x + mr * 1.05f, yo), Offset(ms.x + mr * 2.7f, yo), 2f, 1.4f * u * g), tintaM, style = Stroke(0.7f * u * g))
        }
        val dir = Rect(medio + r.width * 0.05f, r.top + r.height * 0.52f, r.right - r.width * 0.05f, r.bottom - r.height * 0.08f)
        listOf(0.33f, 0.66f, 1f).forEach { f -> drawLine(renglones, Offset(dir.left, dir.top + dir.height * f), Offset(dir.right, dir.top + dir.height * f), 0.5f * u) }
        if (linea1.isNotBlank()) aIzquierda(medidor, linea1, Rect(dir.left, dir.top, dir.right, dir.top + dir.height * 0.33f), TEXTO, tintaC.copy(alpha = 0.85f), 8.5f * g, minimo = 4.5f)
        if (linea2.isNotBlank()) aIzquierda(medidor, linea2, Rect(dir.left, dir.top + dir.height * 0.33f, dir.right, dir.top + dir.height * 0.66f), TEXTO, tintaC.copy(alpha = 0.7f), 7.5f * g, minimo = 4f)
    }

    /** El bramante que ata el montón de postales, de [desde] a [hasta]: dos hilos torcidos, con su sombra. */
    private fun DrawScope.bramante(desde: Offset, hasta: Offset, k: ColoresPsi) {
        val u = density
        val largo = (hasta - desde).getDistance()
        if (largo < 1f) return
        val col = if (k.ciudad) noche(BRAMANTE, k, 0.45f) else linea(k)
        drawLine(k.sombra, desde + Offset(0.6f * u, 1f * u), hasta + Offset(0.6f * u, 1f * u), 1.6f * u)
        drawLine(col, desde, hasta, 1.4f * u)
        // La torsión del hilo, en rayitas.
        val n = (largo / (2.2f * u)).toInt().coerceIn(1, 400)
        val d = (hasta - desde) / largo
        for (i in 0 until n) {
            val p = desde + d * (i * 2.2f * u)
            drawLine(Color.Black.copy(alpha = 0.18f), p + Offset(-d.y, d.x) * (0.7f * u), p + d * (1.2f * u) - Offset(-d.y, d.x) * (0.7f * u), 0.4f * u)
        }
    }

    /**
     * La carta es una postal de 1967, en su montón: debajo, dos postales más; encima, la del día (y en su franja, a quién va y
     * cuándo). Las cartas al futuro van atadas con bramante y en el nudo, como sello, una etiqueta de disco; la que ya se
     * puede abrir brilla. Al abrirla, la etiqueta se rompe, la postal se da vuelta (se ve el reverso dividido, con la
     * estampilla y el matasellos de San Francisco) y sube.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val k = k(oscuro)
        val cu = e.cuerpo
        val s = Rect(cu.left + 6 * u, cu.top + 4 * u, cu.right - 6 * u, cu.bottom - 6 * u)
        val cw = s.width * 0.92f
        val ch = minOf(s.height * 0.92f, cw / 1.45f)
        val g = (cw / (134f * u)).coerceIn(0.8f, 2.4f)
        val alza = e.subir.coerceIn(0f, 1f) * cu.top * 0.9f
        val base = Rect(s.center.x - cw / 2, s.center.y - ch / 2, s.center.x + cw / 2, s.center.y + ch / 2)
        val carta = base.translate(0f, -alza)
        val variante = Math.floorMod(e.semilla, 3)
        // Las postales de debajo, un poco torcidas, con un poco de su color.
        listOf(-6f to (if (k.ciudad) TrazosCiudad.CASA_PINTADA else k.ultramar), 5f to (if (k.ciudad) TrazosCiudad.ETIQUETA else k.magenta)).forEachIndexed { i, (gr, col) ->
            rotate(gr, base.center) {
                val q = base.translate(if (i == 0) -3 * u else 3 * u, if (i == 0) 3 * u else 1 * u)
                drawRect(k.sombra, q.topLeft + Offset(1.5f * u, 2.5f * u), q.size)
                drawRect(if (k.ciudad) noche(Color(0xFFF0E8D8), k, 0.55f) else (if (k.oscuro) lerp(NEGRO, CREMA, 0.12f) else k.papel), q.topLeft, q.size)
                val il = Rect(q.left + q.width * 0.045f, q.top + q.height * 0.06f, q.right - q.width * 0.045f, q.top + q.height * 0.74f)
                drawRect((if (k.ciudad) noche(col, k, 0.5f) else col).copy(alpha = 0.8f), il.topLeft, il.size)
                drawRect(Color.Black.copy(alpha = 0.15f), q.topLeft, q.size, style = Stroke(0.5f * u))
            }
        }
        // El brillo de la que ya se puede abrir.
        if (e.brillo > 0f) drawRoundRect(k.primario.copy(alpha = 0.35f * e.brillo), carta.inflate(5 * u).topLeft, carta.inflate(5 * u).size, CornerRadius(6 * u))
        // La postal de encima, que se da vuelta al abrirse.
        val giro = cos(PI * e.abrir.coerceIn(0f, 1f)).toFloat()
        val ancho = carta.width * abs(giro).coerceAtLeast(0.02f)
        val vista = Rect(carta.center.x - ancho / 2, carta.top, carta.center.x + ancho / 2, carta.bottom)
        drawRect(k.sombra, vista.topLeft + Offset(2 * u, 3 * u), vista.size)
        withTransform({ scale(abs(giro).coerceAtLeast(0.02f), 1f, carta.center) }) {
            if (giro >= 0f) anverso(carta, variante, e.semilla, e.linea1, e.linea2, medidor, k, g)
            else reverso(carta, e.linea1, e.linea2, e.hoja, e.renglones, (e.subir * 2f).coerceIn(0f, 1f), medidor, k, g)
            drawRect(Color.Black.copy(alpha = 0.25f), carta.topLeft, carta.size, style = Stroke(0.6f * u))
        }
        if (giro < 0.98f) drawRect(Color.Black.copy(alpha = 0.25f * (1f - abs(giro))), vista.topLeft, vista.size)
        if (giro < 0f) return
        // El bramante que ata el montón: una vuelta de arriba abajo y otra de lado a lado, y el nudo con sus puntas.
        val cruce = Offset(carta.left + carta.width * 0.9f, carta.top + carta.height * 0.42f)
        if (e.cordon > 0f) {
            val f = e.cordon.coerceIn(0f, 1f)
            val v1 = (f * 2f).coerceAtMost(1f)
            bramante(Offset(cruce.x, carta.top - 2 * u), Offset(cruce.x, carta.top - 2 * u + (carta.height + 4 * u) * v1), k)
            if (f > 0.5f) {
                val v2 = ((f - 0.5f) * 2f).coerceIn(0f, 1f)
                bramante(Offset(carta.left - 2 * u, cruce.y), Offset(carta.left - 2 * u + (carta.width + 4 * u) * v2, cruce.y), k)
            }
            if (f >= 1f) listOf(-1f, 1f).forEach { sg -> bramante(cruce, cruce + Offset(sg * 9 * u * g, 18 * u * g), k) }
        }
        // La etiqueta del nudo: entera (y, al sellar, cayendo desde grande), o rota en dos.
        val ro = minOf(carta.width, carta.height) * 0.14f
        val tinte = if (e.alFuturo) k.primario else k.secundario
        if (e.romper <= 0f) {
            if (e.brillo > 0f) drawCircle(Brush.radialGradient(listOf(k.primario.copy(alpha = 0.5f * e.brillo), Color.Transparent), center = cruce, radius = ro * 2.6f), ro * 2.6f, cruce)
            if (e.alfaSello > 0f) withTransform({ scale(e.escalaSello, e.escalaSello, cruce) }) { etiquetaSello(cruce, ro, tinte, k, e.alfaSello, 0.4f) }
        } else {
            val rr = e.romper.coerceIn(0f, 1f)
            val corte = Path().apply {
                moveTo(cruce.x - ro * 0.1f, cruce.y - ro * 1.2f); lineTo(cruce.x + ro * 0.12f, cruce.y - ro * 0.4f); lineTo(cruce.x - ro * 0.08f, cruce.y + ro * 0.2f)
                lineTo(cruce.x + ro * 0.1f, cruce.y + ro * 1.2f)
            }
            val izq = Path().apply { addPath(corte); lineTo(cruce.x - ro * 1.3f, cruce.y + ro * 1.2f); lineTo(cruce.x - ro * 1.3f, cruce.y - ro * 1.2f); close() }
            val der = Path().apply { addPath(corte); lineTo(cruce.x + ro * 1.3f, cruce.y + ro * 1.2f); lineTo(cruce.x + ro * 1.3f, cruce.y - ro * 1.2f); close() }
            withTransform({ translate(-3 * u * rr, 1 * u * rr); rotate(-8f * rr, cruce) }) { clipPath(izq) { etiquetaSello(cruce, ro, tinte, k, 1f, 0.4f) } }
            withTransform({ translate(3 * u * rr, 2 * u * rr); rotate(9f * rr, cruce) }) { clipPath(der) { etiquetaSello(cruce, ro, tinte, k, 1f, 0.4f) } }
        }
    }

    // La postal que sube, dada vuelta: lo escrito va en la mitad izquierda del reverso, bajo «Post Card».
    override val cajaPrevia = Rect(0.13f, 0.36f, 0.49f, 0.7f)

    override val altoBuzon: Dp = 210.dp

    /** Una postal chica clavada en el corcho, en [q]: el borde blanco y un poco de su ilustración (o de su cartel). */
    private fun DrawScope.postalChica(q: Rect, i: Int, k: ColoresPsi, brillo: Boolean) {
        val u = density
        drawRect(k.sombra, q.topLeft + Offset(1 * u, 1.6f * u), q.size)
        val blanco = if (k.ciudad) noche(Color(0xFFF4ECDD), k, if (brillo) 0.3f else 0.55f) else (if (k.oscuro) lerp(NEGRO, CREMA, if (brillo) 0.3f else 0.12f) else k.papel)
        drawRect(blanco, q.topLeft, q.size)
        val il = Rect(q.left + q.width * 0.07f, q.top + q.height * 0.09f, q.right - q.width * 0.07f, q.top + q.height * 0.72f)
        clipRect(il.left, il.top, il.right, il.bottom) {
            if (k.ciudad) {
                drawRect(noche(listOf(TrazosCiudad.BRUMA, TrazosCiudad.LUZ_BAJA, Color(0xFFB9C2BC))[i.mod(3)], k, 0.5f), il.topLeft, il.size)
                when (i.mod(3)) {
                    0 -> victoriana(Offset(il.center.x, il.bottom), il.width * 0.5f, il.height * 0.8f, noche(TrazosCiudad.CASA_GRIS, k, 0.5f), noche(Color(0xFF5E6B70), k, 0.5f), true)
                    1 -> drawRect(noche(Color(0xFF6F7F82), k, 0.5f), Offset(il.left, il.top + il.height * 0.62f), Size(il.width, il.height * 0.38f))
                    else -> drawPath(Path().apply { moveTo(il.left, il.bottom); cubicTo(il.left + il.width * 0.3f, il.bottom - il.height * 0.5f, il.right - il.width * 0.3f, il.bottom - il.height * 0.4f, il.right, il.bottom - il.height * 0.2f); lineTo(il.right, il.bottom); close() }, noche(Color(0xFF6E7A52), k, 0.5f))
                }
            } else {
                drawRect(k.papel, il.topLeft, il.size)
                val cc = il.center
                drawPath(TrazosTinta.rayos(cc, il.height * 0.2f, il.width, 12, il.width * 0.03f, 1.3f, i * 0.7f), if (i % 2 == 0) k.ultramar else k.magenta)
                drawCircle(k.papel, il.height * 0.22f, cc)
                drawCircle(if (i % 2 == 0) k.magenta else k.ultramar, il.height * 0.16f, cc)
            }
        }
        drawRect(if (k.ciudad) Color.Black.copy(alpha = 0.18f) else linea(k).copy(alpha = 0.6f), q.topLeft, q.size, style = Stroke(0.5f * u))
    }

    /**
     * El correo del departamento: un tablero de corcho en la pared de la cocina, con su marco de pino y un trozo de cinta de
     * pintor arriba que dice «Correo»; tres montones de postales clavadas con chinchetas, cada uno con su cinta escrita a
     * plumón: las que ya se pueden abrir (con un halo tibio), las postales para otro verano (atadas con bramante y su
     * etiqueta) y las leídas. Caben seis por montón; si hay más, se dice cuántas más.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val k = k(oscuro)
        drawRect(if (k.ciudad) noche(YESO, k, 0.7f) else (if (k.oscuro) NEGRO else k.papel))
        if (k.ciudad && !k.oscuro) drawRect(Brush.horizontalGradient(listOf(TrazosCiudad.AL_SOL.copy(alpha = 0.25f), Color.Transparent, k.sombra.copy(alpha = 0.06f)), 0f, w), Offset.Zero, Size(w, h))
        // El tablero: marco de pino y corcho.
        val tab = Rect(w * 0.04f, h * 0.05f, w * 0.96f, h * 0.95f)
        drawRect(k.sombra, tab.topLeft + Offset(2 * u, 4 * u), tab.size)
        pino(tab, PINO, k, 601, nudos = 2)
        val corcho = tab.deflate(6 * u)
        if (k.ciudad) {
            drawRect(noche(CORCHO, k), corcho.topLeft, corcho.size)
            clipRect(corcho.left, corcho.top, corcho.right, corcho.bottom) {
                for (j in 0 until 260) drawCircle(noche(if (j % 3 == 0) Color(0xFF7A5230) else if (j % 3 == 1) Color(0xFFD9AE78) else Color(0xFF5E3E22), k).copy(alpha = 0.5f),
                    (0.4f + 0.8f * azar(603, j)) * u, Offset(corcho.left + corcho.width * azar(605, j), corcho.top + corcho.height * azar(607, j)))
                drawRect(Brush.verticalGradient(listOf(k.sombra, Color.Transparent), corcho.top, corcho.top + 10 * u), corcho.topLeft, Size(corcho.width, 10 * u))
            }
        } else {
            drawRect(if (k.oscuro) lerp(NEGRO, k.bermellon, 0.2f) else lerp(k.papel, k.bermellon, 0.25f), corcho.topLeft, corcho.size)
            clipRect(corcho.left, corcho.top, corcho.right, corcho.bottom) {
                for (j in 0 until 160) drawCircle(linea(k).copy(alpha = 0.35f), 0.6f * u, Offset(corcho.left + corcho.width * azar(605, j), corcho.top + corcho.height * azar(607, j)))
            }
        }
        drawRect(if (k.ciudad) Color.Black.copy(alpha = 0.3f) else linea(k), corcho.topLeft, corcho.size, style = Stroke(0.8f * u))
        cintaPintor(Rect(w / 2 - 34 * u, tab.top - 3 * u, w / 2 + 34 * u, tab.top + 13 * u), "Correo", medidor, k, 11f, -2f)
        // Los tres montones.
        val cuentas = listOf(e.listas, e.selladas, e.abiertas)
        val nombres = listOf("Para leer · ${e.listas}", "Para otro verano · ${e.selladas}", "Leídas · ${e.abiertas}")
        val chinchetas = listOf(k.primario, k.secundario, k.terciario)
        val zona = (corcho.width - 12 * u) / 3f
        for (j in 0 until 3) {
            val zx = corcho.left + 6 * u + j * zona
            val n = cuentas[j].coerceAtMost(6)
            val qa = zona * 0.8f; val qh = qa / 1.45f
            val base = Offset(zx + zona / 2, corcho.top + corcho.height * 0.47f)
            if (j == 0 && n > 0) drawCircle(Brush.radialGradient(listOf(k.primario.copy(alpha = 0.25f), Color.Transparent), base, zona * 0.6f), zona * 0.6f, base)
            for (i in 0 until n) {
                val gq = (i - (n - 1) / 2f) * 7f + (azar(j * 13 + i, 2) - 0.5f) * 4f
                val q = Rect(base.x - qa / 2 + (i - (n - 1) / 2f) * 3 * u, base.y - qh / 2 - i * 2.5f * u, base.x + qa / 2 + (i - (n - 1) / 2f) * 3 * u, base.y + qh / 2 - i * 2.5f * u)
                rotate(gq, Offset(q.center.x, q.top)) {
                    postalChica(q, i + j, k, j == 0)
                    if (i == n - 1) chincheta(Offset(q.center.x, q.top + 3 * u), 2.2f * u, chinchetas[j], k)
                }
            }
            if (j == 1 && n > 0) {
                val top = base.y - qh / 2 - (n - 1) * 2.5f * u
                bramante(Offset(base.x + qa * 0.25f, top - 3 * u), Offset(base.x + qa * 0.25f, base.y + qh / 2 + 2 * u), k)
                etiquetaSello(Offset(base.x + qa * 0.25f, base.y), 6 * u, k.primario, k)
            }
            cintaPintor(Rect(zx + 4 * u, corcho.bottom - 22 * u, zx + zona - 4 * u, corcho.bottom - 6 * u), nombres[j], medidor, k, 8.5f, if (j == 1) 1.5f else -1f)
            if (cuentas[j] > 6) {
                val pc = Offset(zx + zona - 12 * u, corcho.bottom - 34 * u)
                drawCircle(k.sombra, 10 * u, pc + Offset(0.8f * u, 1.2f * u))
                drawCircle(k.secundario, 10 * u, pc)
                textoEn(medidor, "+${cuentas[j] - 6}", Rect(pc.x - 9 * u, pc.y - 7 * u, pc.x + 9 * u, pc.y + 7 * u), ROTULO, if (k.secundario.luminance() > 0.35f) Color(0xFF2A2119) else CREMA, 10f, minimo = 6f)
            }
        }
        if (k.oscuro) drawRect(Brush.radialGradient(0f to Color.Transparent, 0.4f to Color.Transparent, 1f to (if (k.ciudad) TrazosCiudad.PARED_NOCHE else NEGRO).copy(alpha = 0.5f), center = Offset(w * 0.5f, 0f), radius = w), Offset.Zero, Size(w, h))
        grano(k, null, 0.5f)
    }

    // =================================================================
    //  Lo de alrededor: las pestañas, el buscador, las obras y los recuerdos
    // =================================================================

    override val altoPestanas: Dp = 78.dp

    /**
     * El emblema de cada biblioteca, como trazo dentro de [r]: 0, el diario (el disco de larga duración); 1, los momentos (el
     * periódico con su cabecera); 2, los sueños (la luna y una estrella); 3, el ideario (el pliego de dibujo con la esquina
     * doblada); 4, las cartas (la postal con su estampilla).
     */
    private fun iconoBiblioteca(k: Int, r: Rect): Path = Path().apply {
        val w = r.width; val h = r.height; val x = r.left; val y = r.top
        val c = r.center
        when (k) {
            0 -> {
                addOval(Rect(c, minOf(w, h) * 0.48f))
                addOval(Rect(c, minOf(w, h) * 0.18f))
                addArc(Rect(c, minOf(w, h) * 0.34f), 200f, 60f)
            }
            1 -> {
                addRect(Rect(x + w * 0.08f, y + h * 0.1f, x + w * 0.92f, y + h * 0.9f))
                moveTo(x + w * 0.2f, y + h * 0.28f); lineTo(x + w * 0.8f, y + h * 0.28f)
                moveTo(x + w * 0.2f, y + h * 0.48f); lineTo(x + w * 0.46f, y + h * 0.48f)
                moveTo(x + w * 0.2f, y + h * 0.64f); lineTo(x + w * 0.46f, y + h * 0.64f)
                addOval(Rect(Offset(x + w * 0.66f, y + h * 0.6f), w * 0.13f))
            }
            2 -> {
                addArc(Rect(c.x - w * 0.38f, c.y - w * 0.38f, c.x + w * 0.38f, c.y + w * 0.38f), 60f, 240f)
                addArc(Rect(c.x - w * 0.14f, c.y - w * 0.36f, c.x + w * 0.3f, c.y + w * 0.36f), 270f, -180f)
                moveTo(c.x + w * 0.38f, c.y - h * 0.42f); lineTo(c.x + w * 0.38f, c.y - h * 0.22f)
                moveTo(c.x + w * 0.28f, c.y - h * 0.32f); lineTo(c.x + w * 0.48f, c.y - h * 0.32f)
            }
            3 -> {
                moveTo(x + w * 0.12f, y + h * 0.08f); lineTo(x + w * 0.66f, y + h * 0.08f); lineTo(x + w * 0.88f, y + h * 0.3f)
                lineTo(x + w * 0.88f, y + h * 0.92f); lineTo(x + w * 0.12f, y + h * 0.92f); close()
                moveTo(x + w * 0.66f, y + h * 0.08f); lineTo(x + w * 0.66f, y + h * 0.3f); lineTo(x + w * 0.88f, y + h * 0.3f)
                moveTo(x + w * 0.26f, y + h * 0.5f); lineTo(x + w * 0.72f, y + h * 0.5f)
                moveTo(x + w * 0.26f, y + h * 0.68f); lineTo(x + w * 0.6f, y + h * 0.68f)
            }
            else -> {
                addRect(Rect(x + w * 0.02f, y + h * 0.2f, x + w * 0.98f, y + h * 0.8f))
                addRect(Rect(x + w * 0.66f, y + h * 0.28f, x + w * 0.88f, y + h * 0.5f))
                moveTo(c.x, y + h * 0.3f); lineTo(c.x, y + h * 0.72f)
                moveTo(x + w * 0.1f, y + h * 0.5f); lineTo(x + w * 0.4f, y + h * 0.5f)
                moveTo(x + w * 0.1f, y + h * 0.62f); lineTo(x + w * 0.36f, y + h * 0.62f)
            }
        }
    }

    /**
     * Las pestañas son los cinco separadores de cartulina de una caja de discos: asoman entre las fundas, cada uno de un color
     * de la cara (los apagados de la ciudad; las tintas del cartel), con su emblema, su nombre y su cuenta; el elegido sube
     * y, en la tinta, se imprime dos veces con la otra tinta corrida detrás. Abajo, la tablilla de la caja.
     */
    override fun DrawScope.pestanas(e: EstadoPestanas, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val k = k(oscuro)
        val canto = Rect(0f, h - 15 * u, w, h)
        val colores = if (k.ciudad) listOf(TrazosCiudad.enSombra(TrazosCiudad.ETIQUETA, 0.25f), TrazosCiudad.enSombra(TrazosCiudad.CASA_PINTADA, 0.4f), VERDE_BOTELLA, OCRE, TrazosCiudad.COCHE)
            else listOf(k.magenta, k.ultramar, TrazosTinta.sobreimpresion(k.magenta, k.ultramar, k.oscuro), if (k.m.bermellon > 0.5f) k.bermellon else k.magenta, k.ultramar)
        // Las fundas de la caja, que asoman detrás de los separadores.
        var x = 2 * u; var i = 0
        val fundas = if (k.ciudad) listOf(PINO_OSC, VINILO, TrazosCiudad.ETIQUETA, PRENSA, TrazosCiudad.CASA_PINTADA) else listOf(k.papel, linea(k), k.magenta, k.papel, k.ultramar)
        while (x < w - 2 * u) {
            val alto = 16 * u + 6 * u * azar(i, 5)
            drawRect(if (k.ciudad) noche(fundas[i % 5], k) else fundas[i % 5], Offset(x, canto.top - alto), Size(3.6f * u, alto))
            drawLine(Color.Black.copy(alpha = 0.3f), Offset(x + 3.6f * u, canto.top - alto), Offset(x + 3.6f * u, canto.top), 0.5f * u)
            x += 4.2f * u; i++
        }
        for (j in 0 until 5) {
            val q0 = quinta(j, w, 0f, h)
            val sel = j == e.actual
            val sube = if (sel) 0f else 7 * u
            val r = Rect(q0.left + 3 * u, 2 * u + sube, q0.right - 3 * u, canto.top + 4 * u)
            val forma = if (k.ciudad) Path().apply {
                moveTo(r.left, r.bottom); lineTo(r.left, r.top + 11 * u)
                quadraticTo(r.left, r.top, r.left + 11 * u, r.top)
                lineTo(r.right - 11 * u, r.top)
                quadraticTo(r.right, r.top, r.right, r.top + 11 * u)
                lineTo(r.right, r.bottom); close()
            } else TrazosTinta.arco(r, minOf(13 * u, r.width / 2f), 0f)
            val col = if (k.ciudad) noche(colores[j], k, if (sel) 0.3f else 0.5f) else (if (sel) colores[j] else lerp(colores[j], k.papel, if (k.oscuro) 0.45f else 0.3f))
            translate(1 * u, 2 * u) { drawPath(forma, k.sombra) }
            if (sel && !k.ciudad) translate((2.2f + k.m.registro * 0.5f) * u, 1.6f * u) { drawPath(forma, if (j % 2 == 0) k.ultramar else k.magenta) }
            drawPath(forma, col)
            clipPath(forma) {
                if (k.ciudad) drawRect(Brush.verticalGradient(listOf(TrazosCiudad.AL_SOL.copy(alpha = if (sel) 0.22f else 0.1f), Color.Transparent, k.sombra), r.top, r.bottom), r.topLeft, r.size)
                // La cartulina: un poco de fibra.
                for (n in 0 until 8) drawLine(Color.White.copy(alpha = 0.06f), Offset(r.left, r.top + r.height * azar(j, n)), Offset(r.right, r.top + r.height * azar(j, n) + 2 * u), 0.5f * u)
            }
            if (!k.ciudad) drawPath(forma, linea(k).copy(alpha = 0.7f), style = Stroke(0.7f * u))
            val texto = if (col.luminance() > 0.4f) Color(0xFF2A2119) else CREMA
            val marca = if (sel) (if (k.ciudad) TrazosCiudad.SOL_VELADO else (if (col.luminance() > 0.4f) k.grabado else CREMA)) else texto
            val ic = Rect(r.center.x - 8 * u, r.top + 6 * u, r.center.x + 8 * u, r.top + 21 * u)
            drawPath(iconoBiblioteca(j, ic), marca, style = Stroke(1.3f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
            textoEn(medidor, e.nombres.getOrElse(j) { "" }, Rect(r.left + 2 * u, r.top + 23 * u, r.right - 2 * u, r.top + 37 * u), ROTULO, texto, 10f, minimo = 5f)
            textoEn(medidor, Psicodelia.numero(e.cuentas.getOrElse(j) { 0 }), Rect(r.left + 2 * u, r.top + 37 * u, r.right - 2 * u, r.top + 49 * u), ROTULO, marca, 9.5f, minimo = 5f)
        }
        // La tablilla de la caja, al frente.
        drawRect(k.sombra, Offset(canto.left, canto.top - 2 * u), Size(canto.width, 2 * u))
        pino(canto, PINO, k, 701, nudos = 2)
        if (k.ciudad) drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (k.oscuro) 0.04f else 0.24f), Color.Transparent, Color.Black.copy(alpha = 0.3f)), canto.top, canto.bottom), canto.topLeft, canto.size)
        listOf(canto.left + 6 * u, canto.right - 6 * u).forEach { xx -> drawCircle(Color.Black.copy(alpha = 0.4f), 0.9f * u, Offset(xx, canto.center.y)) }
    }

    // ---------------------------------------------------------------- el buscador

    override val altoCampo: Dp = 52.dp
    override val cajaCampo = Rect(0.17f, 0.24f, 0.83f, 0.8f)
    override val letraCampo: FontFamily = TEXTO
    override fun tintaCampo(oscuro: Boolean): Color = k(oscuro).tinta

    /**
     * El buscador es el dial de la radio de válvulas: el mueble de madera (en la tinta, impreso en ultramar con su trama), el
     * vidrio del dial con las rayitas de la escala de la FM arriba y la aguja, y las dos perillas a los lados. Con el foco, el
     * dial se enciende con su luz tibia y la aguja se va al 106.9, la FM de noche.
     */
    override fun DrawScope.campo(e: EstadoCampo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val k = k(oscuro)
        val r = Rect(2 * u, 3 * u, w - 2 * u, h - 3 * u)
        val rad = CornerRadius(12 * u)
        val mueble = Path().apply { addRoundRect(RoundRect(r, rad)) }
        drawRoundRect(k.sombra, r.topLeft + Offset(1 * u, 2 * u), r.size, rad)
        if (k.ciudad) {
            drawRoundRect(Brush.verticalGradient(listOf(noche(Color(0xFF9A6438), k), noche(Color(0xFF6E4024), k)), r.top, r.bottom), r.topLeft, r.size, rad)
            clipPath(mueble) { vetas(r, Color.Black.copy(alpha = 0.18f), 801, 0.8f, false, 0.5f, 0.4f) }
            drawRoundRect(TrazosCiudad.AL_SOL.copy(alpha = if (k.oscuro) 0.05f else 0.2f), Offset(r.left + 6 * u, r.top + 1.5f * u), Size(r.width - 12 * u, 3 * u), CornerRadius(1.5f * u))
        } else {
            drawPath(mueble, if (k.oscuro) lerp(NEGRO, k.ultramar, 0.35f) else k.papel)
            with(TrazosTinta) { trama(mueble, 3f * u, 0.8f * u, k.ultramar) }
            drawPath(mueble, linea(k), style = Stroke(0.8f * u))
        }
        // El vidrio del dial.
        val dial = Rect(w * 0.14f, h * 0.16f, w * 0.86f, h * 0.84f)
        val dradio = CornerRadius(dial.height / 2f)
        val vidrio = when {
            k.oscuro && e.enfocado -> if (k.ciudad) Color(0xFF3A2C22) else lerp(NEGRO, CREMA, 0.2f)
            k.oscuro -> if (k.ciudad) Color(0xFF241C16) else lerp(NEGRO, CREMA, 0.08f)
            e.enfocado -> Color(0xFFFFEDC4)
            else -> if (k.ciudad) Color(0xFFF3ECDD) else k.papel
        }
        if (e.enfocado) drawRoundRect((if (k.ciudad) TrazosCiudad.LAMPARA_LUZ else k.primario).copy(alpha = 0.32f), dial.inflate(3 * u).topLeft, dial.inflate(3 * u).size, CornerRadius(dradio.x + 3 * u))
        drawRoundRect(vidrio, dial.topLeft, dial.size, dradio)
        // La escala: rayitas de la FM a lo largo del borde de arriba.
        val escala = k.tenue.copy(alpha = 0.6f)
        val x0 = dial.left + dial.height * 0.45f; val x1 = dial.right - dial.height * 0.45f
        for (i in 0..40) {
            val xx = x0 + (x1 - x0) * i / 40f
            drawLine(escala, Offset(xx, dial.top + 2 * u), Offset(xx, dial.top + (if (i % 5 == 0) 6f else 4f) * u), (if (i % 5 == 0) 0.7f else 0.4f) * u)
        }
        textoEn(medidor, "FM", Rect(dial.left + 3 * u, dial.top + 1 * u, dial.left + dial.height * 0.5f, dial.top + 9 * u), ROTULO, escala, 6f, minimo = 3f)
        val f = if (e.enfocado) (106.9f - 88f) / 20f else 0.18f
        val ax = x0 + (x1 - x0) * f
        drawLine(if (k.ciudad) TrazosCiudad.LETRERO else k.bermellon.copy(alpha = 1f), Offset(ax, dial.top + 1.5f * u), Offset(ax, dial.top + dial.height * 0.32f), 1.2f * u, StrokeCap.Round)
        drawRoundRect(k.borde.copy(alpha = 0.6f), dial.topLeft, dial.size, dradio, style = Stroke(0.8f * u))
        // Las perillas, de baquelita, con su raya; la de sintonía gira con el foco.
        listOf(Offset(w * 0.075f, h / 2), Offset(w * 0.925f, h / 2)).forEachIndexed { i, kc ->
            val kr = minOf(h * 0.3f, w * 0.05f)
            with(PiezasPsicodelia) { perilla(kc, kr, if (k.ciudad) noche(Color(0xFF3A2416), k, 0.4f) else linea(k), vidrio, k) }
            val ang = if (i == 1 && e.enfocado) 0.9 else -PI / 2
            drawLine(if (k.ciudad) noche(Color(0xFFEFE2C0), k, 0.3f) else vidrio, kc, Offset(kc.x + (kr * 0.7f * cos(ang)).toFloat(), kc.y + (kr * 0.7f * sin(ang)).toFloat()), 1 * u, StrokeCap.Round)
        }
        if (e.vacio) aIzquierda(medidor, e.pista, Rect(w * cajaCampo.left, h * cajaCampo.top, w * cajaCampo.right, h * cajaCampo.bottom), TEXTO,
            k.tinta.copy(alpha = if (k.oscuro) 0.6f else 0.5f), 14f, minimo = 9f)
    }

    // ---------------------------------------------------------------- las obras

    /**
     * Las obras se apoyan en la tabla de pino sobre ladrillos, contra la pared del cuarto de David, donde están clavados sus
     * carteles (cuatro, a dos tintas o a tres con la intensidad, cada uno con su marco de arco y el disco hecho sol). La ciudad:
     * el yeso salvia con la luz de la tarde y el grano; de noche, la lámpara de pantalla. La tinta: la pared es el papel o el
     * terciopelo, con las manchas de las luces líquidas de noche.
     */
    override fun DrawScope.repisaObras(medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val k = k(oscuro)
        drawRect(if (k.ciudad) noche(YESO, k, 0.7f) else (if (k.oscuro) NEGRO else k.papel), Offset.Zero, Size(w, h))
        if (k.ciudad && !k.oscuro) drawRect(Brush.horizontalGradient(listOf(TrazosCiudad.AL_SOL.copy(alpha = 0.3f), Color.Transparent), 0f, w * 0.7f), Offset.Zero, Size(w, h))
        if (!k.ciudad && k.oscuro) {
            drawCircle(k.magenta.copy(alpha = 0.2f + 0.1f * k.i), w * 0.22f, Offset(w * 0.2f, h * 0.3f))
            drawCircle(k.ultramar.copy(alpha = 0.2f + 0.1f * k.i), w * 0.2f, Offset(w * 0.78f, h * 0.4f))
            if (k.m.bermellon > 0f) drawCircle(k.bermellon.copy(alpha = 0.2f * k.m.bermellon), w * 0.15f, Offset(w * 0.5f, h * 0.15f))
        }
        val n = 4
        for (j in 0 until n) {
            val cw = w / n * 0.62f; val chh = h * 0.44f
            val q = Rect(w * (j + 0.5f) / n - cw / 2, h * 0.1f + (j % 2) * 6 * u, w * (j + 0.5f) / n + cw / 2, h * 0.1f + (j % 2) * 6 * u + chh)
            rotate(if (j % 2 == 0) -2f else 1.5f, q.center) {
                drawRect(k.sombra, q.topLeft + Offset(2 * u, 3 * u), q.size)
                cartelChico(q, k, j * 7 + 3)
                chincheta(Offset(q.center.x, q.top + 3 * u), 1.8f * u, listOf(k.primario, k.secundario, k.terciario)[j % 3], k)
            }
        }
        // La tabla de pino sobre sus dos pilas de ladrillo.
        val y0 = h - 22 * u
        val tabla = Rect(0f, y0, w, y0 + 7 * u)
        drawRect(k.sombra, Offset(0f, y0 - 3 * u), Size(w, 3 * u))
        if (k.ciudad) {
            listOf(w * 0.08f, w * 0.82f).forEach { x -> with(TrazosCiudad) { ladrillos(Rect(x, tabla.bottom, x + w * 0.1f, h), 4f * u, noche(COCHE, k), semilla = 5) } }
        } else listOf(w * 0.08f, w * 0.82f).forEach { x ->
            val zona = Rect(x, tabla.bottom, x + w * 0.1f, h)
            drawRect(k.bermellon.copy(alpha = if (k.m.bermellon > 0f) 1f else 0.6f), zona.topLeft, zona.size)
            var yy = zona.top + 4 * u
            while (yy < zona.bottom) { drawLine(linea(k), Offset(zona.left, yy), Offset(zona.right, yy), 0.6f * u); yy += 4 * u }
            drawRect(linea(k), zona.topLeft, zona.size, style = Stroke(0.6f * u))
        }
        pino(tabla, PINO, k, 911, nudos = 1)
        if (k.oscuro && k.ciudad) drawRect(Brush.radialGradient(0f to TrazosCiudad.LAMPARA_LUZ.copy(alpha = 0.1f), 0.4f to Color.Transparent, 1f to TrazosCiudad.PARED_NOCHE.copy(alpha = 0.5f), center = Offset(w * 0.5f, 0f), radius = w), Offset.Zero, Size(w, h))
        grano(k, null, 0.5f)
    }

    /** Un título partido por palabras en a lo más tres renglones que caben en [r], impreso dos veces en la tinta. */
    private fun DrawScope.tituloEn(medidor: TextMeasurer, titulo: String, r: Rect, letra: FontFamily, color: Color, k: ColoresPsi, tam: Float) {
        val palabras = titulo.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        if (palabras.isEmpty()) return
        val lineas = mutableListOf<String>()
        var actual = ""
        palabras.forEach { p ->
            val prueba = if (actual.isEmpty()) p else "$actual $p"
            if (actual.isNotEmpty() && anchoTx(medidor, prueba, letra, tam) > r.width && lineas.size < 2) { lineas.add(actual); actual = p }
            else { actual = prueba }
        }
        lineas.add(actual)
        val alto = r.height / 3f
        var y = r.center.y - alto * lineas.size / 2f
        lineas.forEach { s ->
            dosTintas(medidor, s, Rect(r.left, y, r.right, y + alto), letra, color, k, tam, minimo = tam * 0.45f)
            y += alto
        }
    }

    /** Cuál de [n] variantes le toca a una obra según su [semilla], que puede venir negativa. */
    private fun variante(semilla: Int, n: Int): Int = Math.floorMod(semilla, n.coerceAtLeast(1))

    /**
     * La portada de una obra. La ciudad: una tapa sobria de los setenta, de un color apagado de la paleta (según la semilla),
     * con la franja crema del tipo, el título en el titular, el disco chico y el autor y el año al pie, y su grano. La tinta: un
     * cartel propio, con el marco de arco, la corona de rayos que ondulan alrededor del disco, el título en letra de bloque
     * impreso dos veces y el autor y el año en su placa. En miniatura, solo la inicial en su aro.
     */
    override fun DrawScope.portadaObra(e: EstadoPortada, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val k = k(oscuro)
        val s = w / u
        val g = maxOf(0.5f * u, w * 0.006f)
        val tapa = Path().apply { addRect(Rect(0f, 0f, w, h)) }
        if (k.ciudad) {
            val fondo = noche(listOf(TrazosCiudad.ETIQUETA, VERDE_BOTELLA, TrazosCiudad.CASA_PINTADA, OCRE, TrazosCiudad.COCHE)[variante(e.semilla, 5)], k, 0.4f)
            val sobre = CREMA
            drawPath(tapa, fondo)
            if (e.mini) {
                drawCircle(sobre, w * 0.42f, Offset(w / 2, h / 2), style = Stroke(maxOf(1f * u, w * 0.05f)))
                textoEn(medidor, e.titulo.trim().take(1).uppercase().ifEmpty { "·" }, Rect(0f, h * 0.25f, w, h * 0.75f), titulo(k), sobre, s * 0.5f, minimo = 5f)
                grano(k, tapa, 0.6f)
                return
            }
            drawRect(Brush.linearGradient(listOf(TrazosCiudad.AL_SOL.copy(alpha = if (k.oscuro) 0.05f else 0.22f), Color.Transparent), Offset.Zero, Offset(w * 0.7f, h * 0.7f)), Offset.Zero, Size(w, h))
            val tipo = Rect(w * 0.08f, h * 0.08f, w * 0.92f, h * 0.15f)
            drawRect(sobre, tipo.topLeft, tipo.size)
            textoEn(medidor, e.tipo.uppercase(), tipo.deflate(w * 0.01f), ROTULO, fondo, s * 0.065f, espaciado = s * 0.012f, minimo = 3.5f)
            tituloEn(medidor, e.titulo, Rect(w * 0.08f, h * 0.2f, w * 0.92f, h * 0.55f), titulo(k), sobre, k, s * 0.13f)
            with(PiezasPsicodelia) { disco(Offset(w / 2, h * 0.7f), w * 0.13f, k) }
            val pie = listOf(e.autor, e.anio).filter { it.isNotBlank() }.joinToString(" · ")
            if (pie.isNotEmpty()) textoEn(medidor, pie, Rect(w * 0.08f, h * 0.84f, w * 0.92f, h * 0.92f), TEXTO, sobre, s * 0.065f, minimo = 3.5f)
            grano(k, tapa, 0.6f)
            drawPath(tapa, k.sombra, style = Stroke(g))
        } else {
            val fondo = k.papel
            drawPath(tapa, fondo)
            if (e.mini) {
                drawCircle(k.magenta, w * 0.42f, Offset(w / 2, h / 2), style = Stroke(maxOf(1f * u, w * 0.05f)))
                dosTintas(medidor, e.titulo.trim().take(1).uppercase().ifEmpty { "·" }, Rect(0f, h * 0.25f, w, h * 0.75f), titulo(k), linea(k), k, s * 0.5f, minimo = 5f)
                return
            }
            val marco = TrazosTinta.arco(Rect(w * 0.05f, h * 0.035f, w * 0.95f, h * 0.965f), w * 0.42f, w * 0.02f)
            val cc = Offset(w / 2, h * 0.68f)
            clipPath(marco) {
                with(PiezasPsicodelia) {
                    corona(cc, w * 0.16f, w * 0.9f, k, giro = e.semilla * 7f, n = 22)
                    disco(cc, w * 0.14f, k, sombra = false)
                }
            }
            drawPath(marco, linea(k), style = Stroke(w * 0.02f))
            val tipo = Rect(w * 0.18f, h * 0.1f, w * 0.82f, h * 0.16f)
            drawRect(fondo, tipo.topLeft, tipo.size)
            textoEn(medidor, e.tipo.uppercase(), tipo, ROTULO, k.primario, s * 0.065f, espaciado = s * 0.012f, minimo = 3.5f)
            val placaT = Rect(w * 0.1f, h * 0.18f, w * 0.9f, h * 0.48f)
            drawRect(fondo, placaT.topLeft, placaT.size)
            tituloEn(medidor, e.titulo, placaT, titulo(k), linea(k), k, s * 0.12f)
            val pie = listOf(e.autor, e.anio).filter { it.isNotBlank() }.joinToString(" · ")
            if (pie.isNotEmpty()) {
                val ca = Rect(w * 0.14f, h * 0.86f, w * 0.86f, h * 0.93f)
                drawRect(fondo, ca.topLeft, ca.size)
                drawRect(linea(k), ca.topLeft, ca.size, style = Stroke(g))
                textoEn(medidor, pie, ca.deflate(w * 0.02f), TEXTO, k.tinta, s * 0.06f, minimo = 3.5f)
            }
            drawPath(tapa, linea(k), style = Stroke(g))
        }
    }

    // ---------------------------------------------------------------- los recuerdos

    override val margenRecuerdo: Dp = 14.dp
    override val altoRotuloRecuerdo: Dp = 50.dp
    override fun tintaRecuerdo(oscuro: Boolean): Color = k(oscuro).tinta

    /** «Hace un año» o «Hace N años». */
    private fun haceAnios(n: Int): String = if (n == 1) "Hace un año" else "Hace $n años"

    /** El clima sin su signo (☀ ☂ ❄), que las letras del tema no traen. */
    private fun sinSigno(clima: String): String = clima.dropWhile { !it.isLetterOrDigit() }.trim()

    /**
     * El recuerdo es un volante del barrio. La ciudad: tirado en mimeógrafo con su tinta violeta, el papel un poco gris, las
     * manchas de la tinta. La tinta: impreso a dos tintas, con la banda del cartel arriba. En los dos, sujeto con un trozo de
     * cinta; en la cabecera, una etiqueta de disco con los años, «Hace N años» y la fecha con el clima; debajo, una onda; a la
     * derecha, la pegatina del ánimo y, abajo, el boceto a lápiz del disco hecho sol.
     */
    override fun DrawScope.recuerdo(e: EstadoRecuerdo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val k = k(oscuro)
        val tinta = tintaRecuerdo(oscuro)
        val mimeo = if (k.ciudad) (if (k.oscuro) Color(0xFFB7A6E0) else MIMEO) else k.terciario
        val cab = altoRotuloRecuerdo.toPx()
        val forma = Path().apply { addRoundRect(RoundRect(Rect(0f, 0f, w, h), CornerRadius(if (k.ciudad) 8 * u else 2 * u))) }
        clipPath(forma) {
            drawRect(k.superficie)
            if (k.ciudad) for (j in 0 until 7) drawCircle(mimeo.copy(alpha = 0.04f), (6f + 12f * azar(e.semilla, j)) * u, Offset(w * azar(e.semilla, j + 5), h * azar(e.semilla, j + 11)))
            else banda(0f, w, 5 * u, k, 0.8f)
            // El boceto a lápiz del disco hecho sol, abajo a la derecha.
            if (h > cab + 40 * u) {
                val r = ((h - cab - 16 * u) * 0.4f).coerceAtMost(30 * u)
                val c = Offset(w - 34 * u, h - r - 8 * u)
                drawPath(TrazosTinta.rayos(c, r * 0.5f, r * 1.4f, 14, r * 0.12f, 1.3f), mimeo.copy(alpha = 0.35f), style = Stroke(0.8f * u))
                drawCircle(mimeo.copy(alpha = 0.45f), r * 0.45f, c, style = Stroke(1 * u))
                drawCircle(mimeo.copy(alpha = 0.45f), r * 0.12f, c, style = Stroke(0.8f * u))
            }
        }
        grano(k, forma)
        drawPath(forma, if (k.ciudad) k.sombra else linea(k).copy(alpha = 0.7f), style = Stroke(1f * u))
        rotate(-4f, Offset(w * 0.5f, 2 * u)) { drawRect((if (k.ciudad) noche(CINTA, k, 0.55f) else k.papel).copy(alpha = 0.85f), Offset(w * 0.5f - 18 * u, -4 * u), Size(36 * u, 11 * u)) }
        drawPath(onda(Offset(14 * u, cab - 2 * u), Offset(w - 40 * u, cab - 2 * u), 6f, 1.4f * u), mimeo.copy(alpha = 0.6f), style = Stroke(0.9f * u, cap = StrokeCap.Round))
        // La etiqueta de disco con los años.
        val tc = Offset(28 * u, cab / 2f + 3 * u)
        etiquetaSello(tc, 16 * u, if (k.ciudad) TrazosCiudad.ETIQUETA else k.magenta, k, 1f, 0.15f)
        drawCircle(CREMA, 8.5f * u, tc)
        textoEn(medidor, "${e.anios.coerceAtLeast(1)}", Rect(tc.x - 8 * u, tc.y - 7 * u, tc.x + 8 * u, tc.y + 7 * u), ROTULO, Color(0xFF2A2119), 11f, minimo = 6f)
        val col = Rect(52 * u, 8 * u, w - 34 * u, cab - 6 * u)
        aIzquierda(medidor, haceAnios(e.anios), Rect(col.left, col.top, col.right, col.top + col.height * 0.55f), titulo(k), mimeo, 15f, minimo = 7f)
        val clima = sinSigno(e.clima)
        aIzquierda(medidor, if (clima.isEmpty()) e.fecha else e.fecha + " · " + clima, Rect(col.left, col.top + col.height * 0.55f, col.right, col.bottom), TEXTO, tinta, 10.5f, minimo = 6.5f)
        puntoAnimo(Offset(w - 18 * u, 20 * u), 7 * u, e.animo, k)
    }
}
