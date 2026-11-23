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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.TrazosNouveau
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Art nouveau
// =====================================================================

/**
 * La viñeta engomada de las postales de Art nouveau: un timbre redondo de borde dentado, como las viñetas de propaganda
 * que se pegaban en las cartas hacia 1900 (recurso), con el iris impreso dentro de su anillo de látigo. Sella el lazo de las
 * postales atadas y sale al pie de la carta y en su PDF (`selloCarta`). [alfa] la hace aparecer al caer; [papel] es el color
 * de su fondo y [tinta], el del iris y el anillo.
 */
internal fun DrawScope.vinetaNouveau(c: Offset, r: Float, papel: Color, tinta: Color, alfa: Float = 1f) {
    val a = alfa.coerceIn(0f, 1f)
    if (a <= 0f || r <= 0f) return
    // El borde dentado del papel engomado.
    val dientes = 28
    val borde = Path()
    for (i in 0..dientes * 2) {
        val ang = PI * i / dientes
        val rr = if (i % 2 == 0) r else r * 0.92f
        val p = Offset(c.x + rr * cos(ang).toFloat(), c.y + rr * sin(ang).toFloat())
        if (i == 0) borde.moveTo(p.x, p.y) else borde.lineTo(p.x, p.y)
    }
    borde.close()
    translate(r * 0.06f, r * 0.1f) { drawPath(borde, Color.Black.copy(alpha = 0.25f * a)) }
    drawPath(borde, papel.copy(alpha = a))
    drawPath(borde, Brush.radialGradient(listOf(Color.White.copy(alpha = 0.3f * a), Color.Transparent), center = Offset(c.x - r * 0.35f, c.y - r * 0.4f), radius = r * 1.1f))
    // El anillo de color y, dentro, el campo claro con el iris.
    drawCircle(tinta.copy(alpha = 0.85f * a), r * 0.78f, c)
    drawCircle(mezcla(papel, Color.White, 0.25f).copy(alpha = a), r * 0.62f, c)
    drawCircle(TrazosNouveau.ORO.copy(alpha = a), r * 0.7f, c, style = Stroke(r * 0.04f))
    clipPath(Path().apply { addOval(Rect(c, r * 0.6f)) }) {
        with(TrazosNouveau) {
            iris(Offset(c.x, c.y + r * 0.62f), r * 1.12f, tinta.copy(alpha = a), mezcla(TALLO, tinta, 0.2f).copy(alpha = a), mezcla(TALLO_SOMBRA, tinta, 0.3f).copy(alpha = a))
        }
    }
    // Las dos curvas de látigo del anillo, arriba, como un marco de cartel.
    listOf(-1f, 1f).forEach { s ->
        val p = Path().apply {
            moveTo(c.x + s * r * 0.08f, c.y - r * 0.7f)
            cubicTo(c.x + s * r * 0.3f, c.y - r * 0.92f, c.x + s * r * 0.62f, c.y - r * 0.7f, c.x + s * r * 0.5f, c.y - r * 0.5f)
        }
        drawPath(p, mezcla(papel, Color.White, 0.4f).copy(alpha = 0.8f * a), style = Stroke(r * 0.05f, cap = StrokeCap.Round))
    }
}

/**
 * Art nouveau: el estante del diario es la biblioteca abierta de caoba del despacho de la tienda del taller de litografía
 * (hecha en un taller del faubourg Saint-Antoine a la moda de 1900), con el papel-cuero dorado en el fondo de los casilleros,
 * contra la pared de papel pintado verde gris, con el ramo grande en el jarrón camafeo arriba; las entradas son el tomo de la
 * revista ilustrada, el rollo de carteles y el legajo de cartas, cada una con la flor de su día. De día, el sol del escaparate;
 * de noche, el aplique eléctrico, el quinqué de un hueco y la palmatoria del pie. Su dibujo está en `MuebleEstanteNouveau.kt`.
 * Distinto del Ideario, que es la cajonera de planos de peral del taller, bajo la ventana a los tejados.
 * La tapa del diario es el tomo del día: tela verde salvia, el marco de tallos dorado, la fecha en el cartucho, la viñeta
 * pegada donde se lee lo escrito y el iris de oro; al abrirse, la tapa gira sobre el lomo y deja ver la portadilla.
 */
object EstanteNouveau : DisenoBiblioteca {
    // ---------------------------------------------------------------- la paleta del salón
    private val ORO = TrazosNouveau.ORO
    private val ORO_BRILLO = TrazosNouveau.ORO_BRILLO
    private val TALLO = TrazosNouveau.TALLO
    private val TALLO_SOMBRA = TrazosNouveau.TALLO_SOMBRA
    private val IRIS = TrazosNouveau.IRIS
    private val MALVA = TrazosNouveau.MALVA
    private val HIERRO = TrazosNouveau.HIERRO
    private val AMBAR = TrazosNouveau.VIDRIO_AMBAR
    private val VIOLETA = TrazosNouveau.VIDRIO_VIOLETA
    private val VERDE_VIDRIO = TrazosNouveau.VIDRIO_VERDE
    private val LUZ = TrazosNouveau.LUZ
    private val LUZ_HALO = TrazosNouveau.LUZ_HALO
    private val PIEDRA = TrazosNouveau.PIEDRA
    /** El nogal tallado del mueble: su color, en sombra y en luz, y el hueco oscuro de la talla. */
    private val NOGAL = Color(0xFF6E4C33)
    private val NOGAL_OSC = Color(0xFF3F2A1C)
    private val NOGAL_LUZ = Color(0xFF9A7350)
    /** El fondo de las repisas, de nogal más oscuro, y el plomo de los vitrales. */
    private val FONDO_MUEBLE = Color(0xFF4A3424)
    private val PLOMO = Color(0xFF3A3640)
    /** El papel pintado del salón: crema con tallos verde salvia. */
    private val PAPEL_PARED = Color(0xFFE6DDC4)
    private val PAPEL = Color(0xFFEFE8D6)
    private val PAPEL_CLARO = Color(0xFFF7F2E6)
    /** El latón de los portaetiquetas y tiradores. */
    private val LATON = Color(0xFFC09A52)
    /** Las telas de las encuadernaciones: verde salvia, verde de hierro, violeta de iris, malva y ocre. */
    private val TELAS = listOf(Color(0xFF5E7A58), Color(0xFF3D5A48), Color(0xFF6D4C82), Color(0xFF9A6E92), Color(0xFFA9874A))
    /** La tinta de la prensa y la tinta tenue. */
    private val TINTA = Color(0xFF2A2530)
    private val TENUE = Color(0xFF5E5662)
    private val OCRE = Color(0xFF7A5810)
    /** La calle de noche (modo oscuro): el azul negro donde se hunde el salón lejos de la lámpara. */
    private val NOCHE = Color(0xFF121519)
    /** La hora azul de París: el mismo salón al anochecer (los sueños en modo claro). */
    private val ANOCHECER = Color(0xFF2B3047)
    /** La tinta de noche, clara, a la luz de la lámpara. */
    private val TINTA_NOCHE = Color(0xFFECE5D6)
    private val TENUE_NOCHE = Color(0xFFADA79A)

    private val TITULO: FontFamily = LetraExtra.federant
    private val TEXTO: FontFamily = LetraExtra.brygada
    private val ROTULO: FontFamily = LetraExtra.dellaRespira

    /**
     * El color de lo que no alumbra la lámpara: en modo oscuro, el mismo salón de noche, hundido en el azul negro de la calle;
     * de día, tal cual (con [noche], la de los sueños, el salón a la hora azul).
     */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color = when {
        oscuro -> mezcla(c, NOCHE, 0.86f)
        noche -> mezcla(c, ANOCHECER, 0.4f)
        else -> c
    }

    /** Un color a [cerca] (0 a 1) de la lámpara: de día, como [t]; de noche, menos hundido cuanto más cerca, y entibiado por su luz. */
    private fun luz(c: Color, oscuro: Boolean, noche: Boolean, cerca: Float): Color {
        if (!oscuro) return t(c, false, noche)
        val k = cerca.coerceIn(0f, 1f)
        return mezcla(mezcla(c, NOCHE, 0.88f - 0.5f * Math.pow(k.toDouble(), 1.3).toFloat()), LUZ, 0.16f * k)
    }

    /** El papel donde se lee: crema de día; de noche, el papel en penumbra que alcanza la lámpara, con la tinta clara encima. */
    private fun papel(oscuro: Boolean, noche: Boolean, cerca: Float = 0.5f): Color = when {
        oscuro -> luz(PAPEL, true, noche, cerca)
        noche -> mezcla(PAPEL, ANOCHECER, 0.22f)
        else -> PAPEL
    }

    private fun tinta(oscuro: Boolean): Color = if (oscuro) TINTA_NOCHE else TINTA
    private fun tenue(oscuro: Boolean): Color = if (oscuro) TENUE_NOCHE else TENUE
    /** El dorado de las letras: oro viejo en tinta de día; de noche, la luz de la lámpara. */
    private fun oro(oscuro: Boolean): Color = if (oscuro) LUZ else OCRE

    /** La luz de la lámpara sobre lo que la rodea, en modo oscuro: el halo cálido y, más cerca, el foco. */
    private fun DrawScope.resplandor(c: Offset, r: Float, fuerza: Float = 1f) {
        mancha(c, r, LUZ_HALO.copy(alpha = 0.2f * fuerza))
        mancha(c, r * 0.4f, LUZ_HALO.copy(alpha = 0.22f * fuerza))
    }

    // =================================================================
    //  Ayudantes de dibujo del salón
    // =================================================================

    /** El ancho de un texto, para ajustar rótulos a lo que dicen. */
    private fun anchoTx(medidor: TextMeasurer, texto: String, letra: FontFamily, tam: Float, espaciado: Float = 0f, peso: FontWeight = FontWeight.Normal): Float =
        medidor.measure(texto, TextStyle(fontFamily = letra, fontSize = tam.sp, fontWeight = peso, letterSpacing = espaciado.sp), maxLines = 1, softWrap = false).size.width.toFloat()

    /** Un texto alineado a la izquierda dentro de [r]: [textoEn] en una caja de su ancho medido. */
    private fun DrawScope.aIzquierda(medidor: TextMeasurer, texto: String, r: Rect, letra: FontFamily, color: Color, tam: Float,
                                     espaciado: Float = 0f, peso: FontWeight = FontWeight.Normal, minimo: Float = 6f) {
        val ancho = anchoTx(medidor, texto, letra, tam, espaciado, peso) + 2f
        textoEn(medidor, texto, Rect(r.left, r.top, minOf(r.right, r.left + ancho), r.bottom), letra, color, tam, peso = peso, espaciado = espaciado, minimo = minimo)
    }

    /**
     * La fecha del almanaque en [r]: en un renglón si cabe a [tam]; si no, se parte en el primer « · » en dos renglones: el día
     * («samedi 15 novembre») arriba, y la flor con lo que significa abajo, en la letra de leer, a [k] veces el tamaño.
     */
    private fun DrawScope.enDos(medidor: TextMeasurer, texto: String, r: Rect, letra: FontFamily, color: Color, tam: Float,
                                k: Float = 0.66f, color2: Color = color) {
        val i = texto.indexOf(" · ")
        if (i <= 0 || anchoTx(medidor, texto, letra, tam) <= r.width) {
            textoEn(medidor, texto, r, letra, color, tam, minimo = tam * 0.5f)
            return
        }
        val mitad = r.top + r.height * 0.56f
        textoEn(medidor, texto.substring(0, i).trim(), Rect(r.left, r.top, r.right, mitad), letra, color, tam, minimo = tam * 0.5f)
        textoEn(medidor, texto.substring(i + 3).trim(), Rect(r.left, mitad, r.right, r.bottom), TEXTO, color2, tam * k, minimo = tam * 0.4f)
    }

    /** Un texto en relieve dorado: la sombra corrida abajo y, encima, el oro. */
    private fun DrawScope.dorado(medidor: TextMeasurer, texto: String, r: Rect, letra: FontFamily, oro: Color, tam: Float,
                                 espaciado: Float = 0f, vertical: Boolean = false, minimo: Float = 5f, peso: FontWeight = FontWeight.Normal) {
        val u = density
        textoEn(medidor, texto, r.translate(0.35f * u, 0.5f * u), letra, Color.Black.copy(alpha = 0.45f * oro.alpha), tam, vertical = vertical, peso = peso, espaciado = espaciado, minimo = minimo)
        textoEn(medidor, texto, r, letra, oro, tam, vertical = vertical, peso = peso, espaciado = espaciado, minimo = minimo)
    }

    /** Dibuja [bloque] con la transformación afín x' = a·x + c·y + tx, y' = b·x + d·y + ty (para la tapa que gira). */
    private fun DrawScope.afin(a: Float, b: Float, c: Float, d: Float, tx: Float, ty: Float, bloque: DrawScope.() -> Unit) {
        withTransform({ transform(Matrix(floatArrayOf(a, b, 0f, 0f, c, d, 0f, 0f, 0f, 0f, 1f, 0f, tx, ty, 0f, 1f))) }) { bloque() }
    }

    /** La pestaña [k] de las cinco: exactamente una quinta parte del ancho [w] (ahí se toca). */
    private fun quinta(k: Int, w: Float, arriba: Float, abajo: Float): Rect = Rect(w * k / 5f, arriba, w * (k + 1) / 5f, abajo)

    /**
     * Nogal dentro de [r]: el color [base], la veta fina (a lo largo, o de arriba abajo con [vertical]) y las aguas del nogal,
     * esas manchas alargadas más oscuras de la chapa.
     */
    private fun DrawScope.nogal(r: Rect, base: Color, semilla: Int, vertical: Boolean = false) {
        val u = density
        if (r.width <= 0f || r.height <= 0f) return
        drawRect(base, r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0 until 5) {
                val c = Offset(r.left + r.width * azar(semilla, k), r.top + r.height * azar(semilla, k + 20))
                val l = (if (vertical) r.height else r.width) * (0.2f + 0.3f * azar(semilla, k + 40))
                val g = 3 * u + 5 * u * azar(semilla, k + 60)
                val s = if (vertical) Size(g, l) else Size(l, g)
                drawOval(mezcla(base, NOGAL_OSC, 0.3f).copy(alpha = 0.35f), Offset(c.x - s.width / 2, c.y - s.height / 2), s)
            }
            vetas(r, mezcla(base, NOGAL_OSC, 0.55f).copy(alpha = 0.55f), semilla, 1.4f, vertical, 0.6f, 0.6f)
            vetas(r, mezcla(base, Color.White, 0.25f).copy(alpha = 0.2f), semilla + 3, 0.6f, vertical, 0.5f, 0.8f)
        }
    }

    /**
     * Un trazo tallado en la madera: el hueco oscuro abajo a la derecha (la luz viene de arriba a la izquierda), el canto claro
     * arriba a la izquierda y el relieve en [base].
     */
    private fun DrawScope.tallado(p: Path, base: Color, g: Float, cap: StrokeCap = StrokeCap.Round) {
        translate(g * 0.3f, g * 0.38f) { drawPath(p, Color.Black.copy(alpha = 0.42f), style = Stroke(g, cap = cap, join = StrokeJoin.Round)) }
        translate(-g * 0.16f, -g * 0.2f) { drawPath(p, mezcla(base, Color.White, 0.35f).copy(alpha = 0.7f), style = Stroke(g * 0.9f, cap = cap, join = StrokeJoin.Round)) }
        drawPath(p, base, style = Stroke(g * 0.82f, cap = cap, join = StrokeJoin.Round))
    }

    /** Una forma tallada en relieve (una hoja, un capullo): la sombra, el canto claro y la forma. */
    private fun DrawScope.relieve(p: Path, base: Color, g: Float) {
        translate(g * 0.4f, g * 0.5f) { drawPath(p, Color.Black.copy(alpha = 0.4f)) }
        translate(-g * 0.2f, -g * 0.25f) { drawPath(p, mezcla(base, Color.White, 0.35f).copy(alpha = 0.7f)) }
        drawPath(p, base)
    }

    /** Un filete dorado de [a] a [b]: la línea de oro con su brillo encima. */
    private fun DrawScope.filete(a: Offset, b: Offset, oro: Color, g: Float) {
        drawLine(Color.Black.copy(alpha = 0.3f * oro.alpha), a + Offset(0.3f * g, 0.5f * g), b + Offset(0.3f * g, 0.5f * g), g)
        drawLine(oro, a, b, g)
        drawLine(mezcla(oro, Color.White, 0.5f).copy(alpha = 0.55f * oro.alpha), a - Offset(0f, g * 0.25f), b - Offset(0f, g * 0.25f), g * 0.35f)
    }

    /** La línea de látigo de [TrazosNouveau], dorada o del color que se pida. */
    private fun DrawScope.latigo(desde: Offset, hasta: Offset, rizo: Float, grueso: Float, color: Color, comba: Float = 0.28f, horario: Boolean = true, sombra: Boolean = true) =
        with(TrazosNouveau) { latigo(desde, hasta, rizo, grueso, color, comba, horario, sombra) }

    /** Una hoja de sauce de [base] a [punta], rellena, con su nervio. */
    private fun DrawScope.hojita(base: Offset, punta: Offset, ancho: Float, color: Color, comba: Float = 0.14f) {
        drawPath(TrazosNouveau.hojaLanza(base, punta, ancho, comba), color)
        drawLine(mezcla(color, Color.Black, 0.35f).copy(alpha = 0.6f * color.alpha), base, Offset(base.x + (punta.x - base.x) * 0.8f, base.y + (punta.y - base.y) * 0.8f), ancho * 0.12f, StrokeCap.Round)
    }

    /**
     * El capullo dorado de los lomos y los marcos: dos hojitas que se abren a los lados y el botón al centro. De noche
     * ([luna]), una media luna en su lugar.
     */
    private fun DrawScope.floron(c: Offset, r: Float, oro: Color, luna: Boolean = false) {
        if (luna) {
            val disco = Path().apply { addOval(Rect(c, r * 0.8f)) }
            val sombra = Path().apply { addOval(Rect(Offset(c.x + r * 0.42f, c.y - r * 0.18f), r * 0.72f)) }
            drawPath(Path.combine(androidx.compose.ui.graphics.PathOperation.Difference, disco, sombra), oro)
            drawCircle(oro, r * 0.12f, Offset(c.x + r * 0.75f, c.y + r * 0.55f))
            return
        }
        hojita(Offset(c.x, c.y + r * 0.2f), Offset(c.x - r * 1.1f, c.y - r * 0.35f), r * 0.55f, oro, 0.2f)
        hojita(Offset(c.x, c.y + r * 0.2f), Offset(c.x + r * 1.1f, c.y - r * 0.35f), r * 0.55f, oro, -0.2f)
        drawPath(TrazosNouveau.petalo(Offset(c.x, c.y + r * 0.35f), Offset(c.x, c.y - r * 0.85f), r * 0.75f), oro)
        drawLine(oro, Offset(c.x, c.y + r * 0.3f), Offset(c.x, c.y + r * 1f), r * 0.16f, StrokeCap.Round)
    }

    /** Tela de encuadernar dentro de [r]: el color con su trama fina de hilos y las orillas gastadas. */
    private fun DrawScope.tela(r: Rect, base: Color, semilla: Int) {
        val u = density
        drawRect(base, r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            var y = r.top
            while (y < r.bottom) { drawLine(mezcla(base, Color.Black, 0.18f).copy(alpha = 0.18f), Offset(r.left, y), Offset(r.right, y), 0.4f * u); y += 1.4f * u }
            var x = r.left
            while (x < r.right) { drawLine(mezcla(base, Color.White, 0.2f).copy(alpha = 0.1f), Offset(x, r.top), Offset(x, r.bottom), 0.4f * u); x += 1.6f * u }
            for (k in 0 until 6) drawCircle(mezcla(base, Color.White, 0.3f).copy(alpha = 0.12f), (1f + 2f * azar(semilla, k)) * u,
                Offset(r.left + r.width * azar(semilla, k + 9), r.top + r.height * azar(semilla, k + 17)))
        }
    }

    /**
     * El papel pintado del salón dentro de [r]: crema, con un tallo fino que sube cada tanto y echa un par de hojas y un capullo
     * de malva, en columnas alternas, como los papeles pintados de 1900.
     */
    private fun DrawScope.papelPintado(r: Rect, base: Color, motivo: Color, semilla: Int) {
        val u = density
        drawRect(base, r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            val paso = 34 * u
            var x = r.left + paso * 0.5f; var col = 0
            while (x < r.right + paso) {
                var y = r.top - (if (col % 2 == 0) 0f else paso * 0.9f)
                while (y < r.bottom + paso) {
                    val pie = Offset(x, y + paso * 1.8f)
                    val cima = Offset(x + 3 * u, y)
                    val tallo = Path().apply { moveTo(pie.x, pie.y); cubicTo(pie.x - 5 * u, pie.y - paso * 0.6f, cima.x + 5 * u, cima.y + paso * 0.6f, cima.x, cima.y) }
                    drawPath(tallo, motivo.copy(alpha = motivo.alpha * 0.55f), style = Stroke(0.8f * u))
                    hojita(Offset(x, y + paso * 1.1f), Offset(x - 8 * u, y + paso * 0.75f), 3 * u, motivo.copy(alpha = motivo.alpha * 0.5f), 0.2f)
                    hojita(Offset(x + 1 * u, y + paso * 0.7f), Offset(x + 9 * u, y + paso * 0.4f), 3 * u, motivo.copy(alpha = motivo.alpha * 0.5f), -0.2f)
                    drawPath(TrazosNouveau.petalo(Offset(cima.x, cima.y + 2 * u), Offset(cima.x, cima.y - 6 * u), 5 * u), mezcla(motivo, MALVA, 0.6f).copy(alpha = motivo.alpha * 0.6f))
                    y += paso * 1.8f
                }
                x += paso; col++
            }
        }
    }

    /**
     * El vitral de casa dentro de [r]: vidrio de colores que pasa del violeta (arriba) al ámbar (abajo), una franja de agua
     * verde al pie, un iris al centro con sus hojas de espada, dos tallos a los lados y una libélula; todo emplomado, con los
     * plomos que dividen el fondo en curvas de látigo. Con [apagado] (de 0 a 1) el vidrio se hunde en la noche: sin luz detrás,
     * los colores apenas se adivinan.
     */
    private fun DrawScope.vitral(r: Rect, semilla: Int, apagado: Float, noche: Boolean) {
        val u = density
        val g = maxOf(1f * u, minOf(r.width, r.height) * 0.018f)
        fun v(c: Color) = mezcla(if (noche) mezcla(c, ANOCHECER, 0.35f) else c, NOCHE, 0.7f * apagado)
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawRect(Brush.verticalGradient(listOf(v(mezcla(VIOLETA, Color.White, 0.25f)), v(mezcla(MALVA, AMBAR, 0.35f)), v(mezcla(AMBAR, Color.White, 0.2f))), r.top, r.bottom), r.topLeft, r.size)
            // Lo moteado del vidrio colado.
            for (k in 0 until 40) {
                val c = Offset(r.left + r.width * azar(semilla, k), r.top + r.height * azar(semilla, k + 50))
                drawCircle((if (k % 2 == 0) Color.White else Color.Black).copy(alpha = 0.06f), (3f + 9f * azar(semilla, k + 99)) * u, c)
            }
            // El agua: una franja verde de borde ondulado.
            val agua = Path().apply {
                moveTo(r.left, r.bottom - r.height * 0.2f)
                cubicTo(r.left + r.width * 0.3f, r.bottom - r.height * 0.32f, r.left + r.width * 0.6f, r.bottom - r.height * 0.08f, r.right, r.bottom - r.height * 0.24f)
                lineTo(r.right, r.bottom); lineTo(r.left, r.bottom); close()
            }
            drawPath(agua, Brush.verticalGradient(listOf(v(VERDE_VIDRIO), v(mezcla(VERDE_VIDRIO, HIERRO, 0.5f))), r.bottom - r.height * 0.3f, r.bottom))
            // Los plomos del fondo: curvas de látigo que lo parten en piezas.
            val plomo = mezcla(PLOMO, NOCHE, 0.4f * apagado)
            val curvas = listOf(
                Path().apply { moveTo(r.left, r.top + r.height * 0.35f); cubicTo(r.left + r.width * 0.15f, r.top + r.height * 0.1f, r.left + r.width * 0.28f, r.top + r.height * 0.55f, r.left + r.width * 0.36f, r.top) },
                Path().apply { moveTo(r.right, r.top + r.height * 0.4f); cubicTo(r.right - r.width * 0.15f, r.top + r.height * 0.12f, r.right - r.width * 0.3f, r.top + r.height * 0.6f, r.right - r.width * 0.38f, r.top) },
                Path().apply { moveTo(r.left, r.top + r.height * 0.62f); cubicTo(r.left + r.width * 0.12f, r.top + r.height * 0.5f, r.left + r.width * 0.2f, r.top + r.height * 0.8f, r.left + r.width * 0.3f, r.bottom - r.height * 0.26f) },
                Path().apply { moveTo(r.right, r.top + r.height * 0.66f); cubicTo(r.right - r.width * 0.12f, r.top + r.height * 0.52f, r.right - r.width * 0.2f, r.top + r.height * 0.84f, r.right - r.width * 0.3f, r.bottom - r.height * 0.2f) },
            )
            curvas.forEach { drawPath(it, plomo, style = Stroke(g, cap = StrokeCap.Round)) }
            drawPath(agua, plomo, style = Stroke(g))
            with(TrazosNouveau) {
                // Dos tallos con su capullo, a los lados del iris.
                tallo(Offset(r.left + r.width * 0.3f, r.bottom), r.height * 0.85f, 1f, -1f, r.height * 0.03f, v(TALLO), v(TALLO_SOMBRA), v(MALVA))
                tallo(Offset(r.right - r.width * 0.3f, r.bottom), r.height * 0.8f, 1f, 1f, r.height * 0.03f, v(TALLO), v(TALLO_SOMBRA), v(MALVA))
                iris(Offset(r.center.x, r.bottom + r.height * 0.02f), r.height * 0.98f, v(IRIS), v(TALLO), v(TALLO_SOMBRA), plomo)
                libelula(Offset(r.right - r.width * 0.15f, r.top + r.height * 0.3f), r.height * 0.38f, -60f, 0f, v(TrazosNouveau.LIBELULA), v(TrazosNouveau.NACAR), plomo, plomo)
            }
            // El brillo del vidrio, en diagonal, de día.
            if (apagado < 0.5f) {
                val brillo = Path().apply {
                    moveTo(r.left + r.width * 0.05f, r.top); lineTo(r.left + r.width * 0.16f, r.top)
                    lineTo(r.left + r.width * 0.02f, r.bottom); lineTo(r.left - r.width * 0.09f, r.bottom); close()
                }
                drawPath(brillo, Color.White.copy(alpha = 0.16f * (1f - apagado * 2f)))
            }
        }
        drawRect(mezcla(PLOMO, NOCHE, 0.3f * apagado), r.topLeft, r.size, style = Stroke(g * 1.6f))
    }

    /**
     * El jarrón de vidrio de Nancy, de pie en [pie] y de [alto]: panzudo y de cuello alto, del ámbar al violeta, con una flor
     * grabada al ácido (más oscura) y el brillo del vidrio. [flor] pone un iris de verdad saliendo del cuello.
     */
    private fun DrawScope.jarron(pie: Offset, alto: Float, oscuro: Boolean, noche: Boolean, cerca: Float, flor: Boolean = false) {
        val u = density
        fun c(x: Color) = luz(x, oscuro, noche, cerca)
        val an = alto * 0.46f
        val cuerpo = Path().apply {
            moveTo(pie.x - an * 0.22f, pie.y)
            cubicTo(pie.x - an * 0.62f, pie.y - alto * 0.12f, pie.x - an * 0.56f, pie.y - alto * 0.5f, pie.x - an * 0.14f, pie.y - alto * 0.7f)
            quadraticTo(pie.x - an * 0.1f, pie.y - alto * 0.86f, pie.x - an * 0.2f, pie.y - alto)
            lineTo(pie.x + an * 0.2f, pie.y - alto)
            quadraticTo(pie.x + an * 0.1f, pie.y - alto * 0.86f, pie.x + an * 0.14f, pie.y - alto * 0.7f)
            cubicTo(pie.x + an * 0.56f, pie.y - alto * 0.5f, pie.x + an * 0.62f, pie.y - alto * 0.12f, pie.x + an * 0.22f, pie.y)
            close()
        }
        if (flor) with(TrazosNouveau) { iris(Offset(pie.x, pie.y - alto * 0.85f), alto * 0.9f, c(IRIS), c(TALLO), c(TALLO_SOMBRA)) }
        drawOval(Color.Black.copy(alpha = 0.28f), Offset(pie.x - an * 0.4f, pie.y - alto * 0.03f), Size(an * 0.8f, alto * 0.06f))
        drawPath(cuerpo, Brush.verticalGradient(listOf(c(VIOLETA), c(mezcla(VIOLETA, AMBAR, 0.5f)), c(AMBAR)), pie.y - alto, pie.y))
        clipPath(cuerpo) {
            // La flor grabada: un tallo con su hoja y la corola, en el vidrio de encima, más oscuro.
            val grab = c(mezcla(HIERRO, VIOLETA, 0.3f)).copy(alpha = 0.75f)
            val tallo = Path().apply { moveTo(pie.x - an * 0.3f, pie.y); cubicTo(pie.x - an * 0.1f, pie.y - alto * 0.25f, pie.x + an * 0.3f, pie.y - alto * 0.3f, pie.x + an * 0.12f, pie.y - alto * 0.55f) }
            drawPath(tallo, grab, style = Stroke(alto * 0.03f, cap = StrokeCap.Round))
            hojita(Offset(pie.x, pie.y - alto * 0.18f), Offset(pie.x - an * 0.45f, pie.y - alto * 0.4f), alto * 0.08f, grab, 0.25f)
            for (j in 0 until 3) drawPath(TrazosNouveau.petalo(Offset(pie.x + an * 0.12f, pie.y - alto * 0.55f), Offset(pie.x + an * (0.12f + (j - 1) * 0.22f), pie.y - alto * 0.68f), alto * 0.1f), grab)
            drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent, Color.Black.copy(alpha = 0.32f)), pie.x - an * 0.6f, pie.x + an * 0.6f), Offset(pie.x - an * 0.6f, pie.y - alto), Size(an * 1.2f, alto))
        }
        drawLine(Color.White.copy(alpha = if (oscuro) 0.18f else 0.45f), Offset(pie.x - an * 0.3f, pie.y - alto * 0.5f), Offset(pie.x - an * 0.22f, pie.y - alto * 0.2f), alto * 0.025f, StrokeCap.Round)
        drawPath(cuerpo, Color.Black.copy(alpha = 0.25f), style = Stroke(0.6f * u))
    }

    // =================================================================
    //  El estante y las entradas: la biblioteca de caoba del despacho (`MuebleEstanteNouveau.kt`)
    // =================================================================

    override val corona = MuebleEstanteNouveau.CORONA
    override val base = MuebleEstanteNouveau.BASE
    override val lado = MuebleEstanteNouveau.LADO
    override val altoRepisa: Dp = MuebleEstanteNouveau.ALTO_REPISA
    override val tabla: Dp = MuebleEstanteNouveau.TABLA
    override val separa: Dp = MuebleEstanteNouveau.SEPARA
    override val letra: FontFamily = TITULO

    override val auraEnReposo = false

    override fun ancho(palabras: Int): Dp = MuebleEstanteNouveau.ancho(palabras)
    override fun alto(semilla: Int, palabras: Int): Float = MuebleEstanteNouveau.alto(semilla, palabras)

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) =
        with(MuebleEstanteNouveau) { estante(d, medidor, oscuro, noche) }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) =
        with(MuebleEstanteNouveau) { volumen(v, medidor, oscuro) }

    /**
     * El portaetiquetas de latón del buzón, en [r]: el marco, con las puntas enroscadas en látigo y dos tornillitos, y dentro la
     * tarjeta crema con el nombre impreso.
     */
    private fun DrawScope.portaetiqueta(r: Rect, texto: String, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val tam = 9.5f
        val ancho = (anchoTx(medidor, texto, TITULO, tam) + 30 * u).coerceIn(r.width * 0.45f, r.width)
        val pl = Rect(r.center.x - ancho / 2, r.top - 0.5f * u, r.center.x + ancho / 2, r.bottom + 0.5f * u)
        val laton = luz(LATON, oscuro, noche, 0.45f)
        // Las puntas en látigo, a los lados.
        listOf(-1f, 1f).forEach { s ->
            val desde = Offset(if (s < 0) pl.left + 3 * u else pl.right - 3 * u, pl.center.y)
            latigo(desde, Offset(desde.x + s * 11 * u, pl.center.y - 1 * u), 3 * u, 1.6f * u, laton, 0.25f * s, s < 0)
        }
        drawRoundRect(Color.Black.copy(alpha = 0.35f), pl.topLeft + Offset(0.8f * u, 1.2f * u), pl.size, CornerRadius(3 * u))
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(laton, Color.White, 0.35f), laton, mezcla(laton, Color.Black, 0.35f)), pl.top, pl.bottom), pl.topLeft, pl.size, CornerRadius(3 * u))
        val tarjeta = pl.deflate(1.8f * u)
        drawRoundRect(papel(oscuro, noche, 0.55f), tarjeta.topLeft, tarjeta.size, CornerRadius(1.6f * u))
        drawRoundRect(Color.Black.copy(alpha = 0.2f), tarjeta.topLeft, tarjeta.size, CornerRadius(1.6f * u), style = Stroke(0.5f * u))
        listOf(pl.left + 4 * u, pl.right - 4 * u).forEach { x ->
            drawCircle(mezcla(laton, Color.Black, 0.4f), 1.2f * u, Offset(x, pl.center.y))
            drawLine(mezcla(laton, Color.White, 0.4f), Offset(x - 0.8f * u, pl.center.y), Offset(x + 0.8f * u, pl.center.y), 0.4f * u)
        }
        textoEn(medidor, texto, Rect(tarjeta.left + 6 * u, tarjeta.top, tarjeta.right - 6 * u, tarjeta.bottom), TITULO, tinta(oscuro), tam, minimo = 6f)
    }

    /** El lomo redondo de un tomo dentro de [r] (el de la tapa): la tela con su luz (más clara a un tercio, en sombra en los cantos). */
    private fun DrawScope.lomo(r: Rect, color: Color, semilla: Int) {
        tela(r, color, semilla)
        drawRect(Brush.horizontalGradient(0f to Color.Black.copy(alpha = 0.38f), 0.3f to Color.White.copy(alpha = 0.12f), 0.55f to Color.Transparent, 1f to Color.Black.copy(alpha = 0.42f),
            startX = r.left, endX = r.right), r.topLeft, r.size)
    }

    // =================================================================
    //  La tapa: el tomo del día, cerrado sobre la mesa del taller
    // =================================================================

    override val altoTapa: Dp = 200.dp
    override val cajaTapa = Rect(0.16f, 0.43f, 0.64f, 0.75f)
    override val letraTapa: FontFamily = TEXTO
    override val tamTapa = 12.5f
    override val lineasTapa = 4
    override val centradoTapa = false
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = tinta(oscuro)
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra impresa" else "$n palabras impresas"

    /**
     * El tomo del día, cerrado: el lomo de tela con sus nervios dorados a la izquierda y el canto de las hojas a la derecha;
     * la tapa de tela verde salvia con el marco de tallos dorado que se enroscan en las esquinas, la fecha del almanaque en oro
     * arriba (el día y, debajo, la flor con lo que significa), el látigo dorado que la subraya, la viñeta de papel crema pegada
     * donde se lee lo escrito y, a la derecha, el iris de oro. Al abrirse, la tapa gira sobre el lomo y deja ver la portadilla
     * de la revista. Descansa en la mesa de nogal del dibujante (la de la libreta), con la vela en su palmatoria al lado. De
     * día la luz viene de la ventana, arriba a la izquierda; de noche, de la vela de la derecha (la luna fría entra por la
     * izquierda, en los cantos); en el sueño manda la luna: la vela arde baja y la tela se apaga lejos de la ventana. El oro y
     * la viñeta van por encima de la penumbra de la tela: la fecha y el extracto se leen igual que de día.
     */
    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El sueño en claro: la misma pieza al anochecer, apenas más fría (la luna hace lo demás, no un filtro).
        fun c(x: Color) = if (!oscuro && e.noche) mezcla(x, ANOCHECER, 0.14f) else luz(x, oscuro, e.noche, if (oscuro && !e.noche) 0.72f else 0.55f)
        val a = e.abierto.coerceIn(0f, 1f)
        val cuerpo = Rect(w * 0.02f, h * 0.05f, w * 0.95f, h * 0.92f)
        val lomoAncho = w * 0.065f
        val x0 = cuerpo.left + lomoAncho
        val cara = Rect(x0, cuerpo.top, cuerpo.right, cuerpo.bottom)
        val telaC = c(TELAS[0])
        // El oro de noche recoge la luz de la vela: un poco más encendido que el resto de la tapa.
        val oroC = luz(ORO, oscuro, e.noche, if (oscuro && !e.noche) 0.9f else 0.75f)
        // La mesa donde descansa: el tablero de nogal que asoma alrededor, más hondo lejos, y su canto abajo.
        mesaTapa(cuerpo, oscuro, e.noche, ::c)
        // La sombra que echa en la mesa, con color, al lado contrario de la luz (de noche, de la vela de la derecha).
        val haciaX = if (oscuro && !e.noche) -1f else 1f
        val sombraT = when { oscuro -> PaletaLibretaNouveau.HONDO; e.noche -> ANOCHECER; else -> SOMBRA_MUEBLE }
        // Solo donde asoma: una tira abajo y otra al costado, en dos tonos (la de más cerca, más honda).
        for ((d, al) in listOf(Offset(6f, 8f) to (if (oscuro) 0.5f else 0.22f), Offset(2.5f, 4f) to (if (oscuro) 0.4f else 0.2f))) {
            val dx = haciaX * d.x * u; val dy = d.y * u
            drawRect(sombraT.copy(alpha = al), Offset(cuerpo.left + dx + 3 * u, cuerpo.bottom - 4 * u), Size(cuerpo.width - 6 * u, dy + 4 * u))
            drawRect(sombraT.copy(alpha = al), Offset(if (haciaX > 0) cuerpo.right - 4 * u else cuerpo.left + dx, cuerpo.top + dy + 3 * u), Size(abs(dx) + 4 * u, cuerpo.height - 7 * u))
        }
        val hojas = Rect(x0, cuerpo.top + 3 * u, cuerpo.right + 3.5f * u, cuerpo.bottom + 3.5f * u)
        drawRect(c(mezcla(PAPEL, Color(0xFFC9B48A), 0.3f)), hojas.topLeft, hojas.size)
        var yy = hojas.top + 2 * u
        while (yy < hojas.bottom) { drawLine(Color.Black.copy(alpha = 0.08f), Offset(cuerpo.right, yy), Offset(hojas.right, yy), 0.4f * u); yy += 1.4f * u }
        var xx = x0
        while (xx < hojas.right) { drawLine(Color.Black.copy(alpha = 0.08f), Offset(xx, cuerpo.bottom), Offset(xx, hojas.bottom), 0.4f * u); xx += 1.4f * u }
        // Debajo de la tapa: la portadilla.
        if (a > 0.01f) portadilla(cara, e, medidor, oscuro)
        val ang = a * 1.5f
        val co = cos(ang); val si = sin(ang)
        if (a > 0.01f) drawRect(Color.Black.copy(alpha = 0.3f * si), Offset(x0 + cara.width * co, cara.top), Size(16 * u * si, cara.height))
        afin(co, -si * 0.1f, 0f, 1f, x0 - x0 * co, x0 * si * 0.1f) {
            val forma = Path().apply { addRoundRect(RoundRect(cara, CornerRadius(4 * u))) }
            clipPath(forma) {
                tela(cara, telaC, e.semilla)
                // Las esquinas gastadas y la luz de la ventana sobre la tela.
                drawRect(Brush.linearGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.04f else 0.14f), Color.Transparent, sombraT.copy(alpha = 0.26f)), cara.topLeft, cara.bottomRight), cara.topLeft, cara.size)
                listOf(cara.topRight, cara.bottomRight).forEach { mancha(it, 12 * u, mezcla(telaC, Color.White, 0.3f).copy(alpha = 0.4f)) }
                // De noche, la tela se apaga lejos de la vela (o, en el sueño, lejos de la ventana de la luna); el oro y la viñeta,
                // que van encima, no: así la fecha se sigue leyendo.
                if (oscuro || e.noche) {
                    val desde = if (e.noche) Offset(-w * 0.1f, -h * 0.4f) else velaTapa(w, h)
                    val hondo = if (oscuro) NOCHE.copy(alpha = 0.5f) else ANOCHECER.copy(alpha = 0.28f)
                    drawRect(Brush.radialGradient(0f to Color.Transparent, 0.32f to Color.Transparent, 1f to hondo, center = desde, radius = w * 1.05f), cara.topLeft, cara.size)
                }
            }
            drawPath(forma, Color.Black.copy(alpha = 0.35f), style = Stroke(0.7f * u))
            // El filete de luz en el canto de arriba: de día, el de la ventana; de noche, el de la vela, hacia la derecha.
            val filete = when { !oscuro && !e.noche -> LUZ_MUEBLE.copy(alpha = 0.55f); oscuro && !e.noche -> PaletaLibretaNouveau.LLAMA.copy(alpha = 0.55f); else -> PaletaLibretaNouveau.LUNA.copy(alpha = 0.45f) }
            val desdeX = if (oscuro && !e.noche) cara.left + cara.width * 0.4f else cara.left + 2 * u
            val hastaX = if (oscuro && !e.noche) cara.right - 3 * u else cara.left + cara.width * 0.75f
            drawLine(filete, Offset(desdeX, cara.top + 0.8f * u), Offset(hastaX, cara.top + 0.8f * u), 1.2f * u)
            // El marco de tallos dorado, con los látigos en las esquinas.
            val m = cara.deflate(9 * u)
            drawRect(oroC.copy(alpha = 0.85f), m.topLeft, m.size, style = Stroke(0.9f * u))
            val m2 = m.deflate(3 * u)
            drawRect(oroC.copy(alpha = 0.6f), m2.topLeft, m2.size, style = Stroke(0.5f * u))
            listOf(m.topLeft to Offset(1f, 1f), m.topRight to Offset(-1f, 1f), m.bottomLeft to Offset(1f, -1f), m.bottomRight to Offset(-1f, -1f)).forEach { (q, s) ->
                latigo(Offset(q.x + s.x * 22 * u, q.y + s.y * 3 * u), Offset(q.x + s.x * 7 * u, q.y + s.y * 10 * u), 3.4f * u, 1.6f * u, oroC, 0.25f * s.x * s.y, s.x * s.y > 0, false)
                hojita(Offset(q.x + s.x * 3 * u, q.y + s.y * 22 * u), Offset(q.x + s.x * 9 * u, q.y + s.y * 34 * u), 3.4f * u, oroC.copy(alpha = 0.85f), 0.2f * s.x)
            }
            val xs = cara.left + cara.width * 0.68f
            // La fecha del almanaque, en oro.
            val fecha = Rect(m2.left + 8 * u, m2.top + 3 * u, xs + 4 * u, h * 0.3f)
            val i = e.fecha.indexOf(" · ")
            if (i > 0) {
                dorado(medidor, e.fecha.substring(0, i).trim(), Rect(fecha.left, fecha.top, fecha.right, fecha.top + fecha.height * 0.6f), TITULO, oroC, 16f, minimo = 8f)
                dorado(medidor, e.fecha.substring(i + 3).trim(), Rect(fecha.left, fecha.top + fecha.height * 0.58f, fecha.right, fecha.bottom), TEXTO, mezcla(oroC, ORO_BRILLO, 0.3f), 10f, minimo = 5f)
            } else dorado(medidor, e.fecha, fecha, TITULO, oroC, 16f, minimo = 8f)
            // El látigo doble que subraya la fecha.
            val yd = h * 0.345f
            val cxd = (fecha.left + fecha.right) / 2
            listOf(-1f, 1f).forEach { s -> latigo(Offset(cxd + s * 4 * u, yd), Offset(cxd + s * fecha.width * 0.36f, yd - 1.5f * u), 3 * u, 1.3f * u, oroC, 0.18f * s, s > 0, false) }
            floron(Offset(cxd, yd - 1.5f * u), 3.4f * u, oroC, e.noche)
            // La viñeta de papel pegada, donde se lee lo escrito.
            val vi = Rect(w * cajaTapa.left - 7 * u, h * cajaTapa.top - 6 * u, w * cajaTapa.right + 7 * u, h * cajaTapa.bottom + 5 * u)
            drawRect(Color.Black.copy(alpha = 0.3f), vi.topLeft + Offset(1 * u, 1.6f * u), vi.size)
            // De noche el papel queda en la penumbra de la vela (la tinta del extracto es clara: se lee a más de 4.5 a 1).
            drawRect(papel(oscuro, e.noche, if (oscuro) 0.34f else 0.6f), vi.topLeft, vi.size)
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color(0xFFC9B48A).copy(alpha = 0.18f)), vi.top, vi.bottom), vi.topLeft, vi.size)
            val fi = vi.deflate(3 * u)
            drawRect(c(TALLO_SOMBRA).copy(alpha = 0.55f), fi.topLeft, fi.size, style = Stroke(0.6f * u))
            listOf(fi.topLeft, fi.topRight, fi.bottomLeft, fi.bottomRight).forEach { q -> drawCircle(c(TALLO_SOMBRA).copy(alpha = 0.6f), 1.1f * u, q) }
            // Las palabras, en oro al pie.
            val palabras = if (e.vacio) "aún sin imprimir" else palabrasTapa(e.palabras)
            aIzquierda(medidor, palabras, Rect(m2.left + 26 * u, h * 0.8f, xs, h * 0.88f), ROTULO, oroC, 9.5f, espaciado = 0.5f, minimo = 6f)
            // El iris de oro, a la derecha.
            val ic = Offset(cara.left + cara.width * 0.84f, m2.bottom - 4 * u)
            with(TrazosNouveau) {
                translate(0.6f * u, 0.9f * u) { iris(ic, m2.height * 0.8f, Color.Black.copy(alpha = 0.3f), Color.Black.copy(alpha = 0.3f), Color.Black.copy(alpha = 0.3f)) }
                iris(ic, m2.height * 0.8f, oroC, mezcla(oroC, telaC, 0.25f), mezcla(oroC, telaC, 0.45f))
            }
            // De noche, la media luna y tres estrellas de oro sobre el iris.
            if (e.noche) {
                floron(Offset(ic.x + 14 * u, m2.top + 14 * u), 6 * u, oroC, true)
                listOf(-14f to 6f, -6f to 18f, 20f to 30f).forEach { (dx, dy) -> drawCircle(oroC, 1.1f * u, Offset(ic.x + dx * u, m2.top + dy * u)) }
            }
            if (a > 0f) drawRect(Color.Black.copy(alpha = 0.45f * (1f - co)), cara.topLeft, cara.size)
        }
        // El lomo, con sus nervios dorados.
        val lomoR = Rect(cuerpo.left, cuerpo.top - 1 * u, x0 + 2 * u, cuerpo.bottom + 1 * u)
        // De noche el lomo está lejos de la vela: más hondo, en su propio color (sin velo encima).
        lomo(lomoR, mezcla(if (oscuro && !e.noche) luz(TELAS[0], true, false, 0.42f) else telaC, NOGAL_OSC, 0.25f), e.semilla + 3)
        listOf(0.18f, 0.38f, 0.62f, 0.82f).forEach { f ->
            val y = lomoR.top + lomoR.height * f
            drawLine(Color.Black.copy(alpha = 0.3f), Offset(lomoR.left, y + 1.2f * u), Offset(lomoR.right, y + 1.2f * u), 1.2f * u)
            drawLine(oroC, Offset(lomoR.left + 1 * u, y), Offset(lomoR.right - 1 * u, y), 0.8f * u)
        }
        floron(Offset(lomoR.center.x, lomoR.top + lomoR.height * 0.5f), minOf(lomoR.width * 0.3f, 5 * u), oroC, e.noche)
        drawLine(Color.Black.copy(alpha = 0.4f), Offset(x0 + 2 * u, cuerpo.top), Offset(x0 + 2 * u, cuerpo.bottom), 1.2f * u)
        // El canto del lomo que mira a la ventana: de día, la luz; de noche y en el sueño, la luna fría.
        drawLine(if (!oscuro && !e.noche) LUZ_MUEBLE.copy(alpha = 0.4f) else PaletaLibretaNouveau.LUNA_RAS.copy(alpha = if (oscuro) 0.4f else 0.5f),
            Offset(lomoR.left + 0.8f * u, lomoR.top + 2 * u), Offset(lomoR.left + 0.8f * u, lomoR.bottom - 2 * u), 1.1f * u)
        luzTapa(cuerpo, oscuro, e.noche)
    }

    /** Dónde arde la vela de la tapa: a la derecha, detrás de la esquina de arriba del tomo. */
    private fun velaTapa(w: Float, h: Float) = Offset(w * 0.975f, h * 0.1f)

    /**
     * La mesa de la tapa: el tablero de nogal del dibujante que asoma alrededor del tomo (más hondo arriba, lejos) y su canto
     * abajo, con el filete de luz y el bocel; a la derecha, la vela en su palmatoria de latón (de día, apagada; en el sueño,
     * gastada y baja).
     */
    private fun DrawScope.mesaTapa(cuerpo: Rect, oscuro: Boolean, noche: Boolean, c: (Color) -> Color) {
        val u = density
        val w = size.width; val h = size.height
        val canto = h * 0.945f
        // Lejos, arriba, el tablero se pierde en el fondo de la escena; cerca, abajo, es nogal lleno.
        val lejos = c(mezcla(NOGAL, NOGAL_OSC, 0.55f))
        val tablero = Brush.verticalGradient(0f to lejos.copy(alpha = 0f), 0.4f to lejos.copy(alpha = 0.55f), 1f to c(NOGAL), startY = 0f, endY = canto)
        drawRect(tablero, Offset(0f, cuerpo.top), Size(cuerpo.left + 3 * u, canto - cuerpo.top))
        drawRect(tablero, Offset(cuerpo.right, cuerpo.top), Size(w - cuerpo.right, canto - cuerpo.top))
        drawRect(tablero, Offset(0f, cuerpo.bottom - 4 * u), Size(w, canto - cuerpo.bottom + 4 * u))
        // El canto del tablero, con su filete de luz y el bocel.
        drawRect(c(NOGAL_OSC), Offset(0f, canto), Size(w, h - canto))
        val filo = when { !oscuro && !noche -> LUZ_MUEBLE.copy(alpha = 0.5f); noche -> PaletaLibretaNouveau.LUNA_RAS.copy(alpha = 0.35f); else -> PaletaLibretaNouveau.LLAMA.copy(alpha = 0.4f) }
        drawLine(filo, Offset(0f, canto + 0.7f * u), Offset(w, canto + 0.7f * u), 1.4f * u)
        drawLine(Color.Black.copy(alpha = 0.3f), Offset(0f, canto + (h - canto) * 0.55f), Offset(w, canto + (h - canto) * 0.55f), 0.8f * u)
        // La palmatoria de latón y la vela.
        val v = velaTapa(w, h)
        val pie = Offset(v.x, h * 0.34f)
        val alto = if (noche) h * 0.1f else h * 0.19f
        val laton = c(LATON)
        drawOval(SOMBRA_MUEBLE.copy(alpha = if (oscuro) 0.5f else 0.25f), Offset(pie.x - 9 * u, pie.y - 1 * u), Size(16 * u, 5 * u))
        drawOval(laton, Offset(pie.x - 7 * u, pie.y - 3.5f * u), Size(14 * u, 6 * u))
        drawOval(mezcla(laton, Color.White, 0.35f).copy(alpha = 0.6f), Offset(pie.x - 5 * u, pie.y - 3 * u), Size(8 * u, 2 * u))
        val cera = if (oscuro) mezcla(c(PAPEL_CLARO), PaletaLibretaNouveau.LLAMA, if (noche) 0.15f else 0.35f) else c(PAPEL_CLARO)
        drawRect(cera, Offset(pie.x - 2.2f * u, pie.y - alto), Size(4.4f * u, alto - 1.5f * u))
        drawRect(Color.Black.copy(alpha = 0.15f), Offset(pie.x + 0.8f * u, pie.y - alto), Size(1.4f * u, alto - 1.5f * u))
        drawLine(Color(0xFF2A2420), Offset(pie.x, pie.y - alto), Offset(pie.x, pie.y - alto - 2 * u), 0.7f * u)
        // La llama: encendida de noche (en el sueño, chica y baja); de día, la mecha apagada.
        if (oscuro || noche) {
            val tam = if (noche) 0.6f else 1f
            val base = Offset(pie.x, pie.y - alto - 1.5f * u)
            mancha(base + Offset(0f, -4 * u * tam), 14 * u * tam, PaletaLibretaNouveau.LAMPARA.copy(alpha = 0.45f))
            val llama = Path().apply {
                moveTo(base.x, base.y - 9 * u * tam)
                cubicTo(base.x + 3.2f * u * tam, base.y - 4 * u * tam, base.x + 2.6f * u * tam, base.y, base.x, base.y)
                cubicTo(base.x - 2.6f * u * tam, base.y, base.x - 3.2f * u * tam, base.y - 4 * u * tam, base.x, base.y - 9 * u * tam)
                close()
            }
            drawPath(llama, PaletaLibretaNouveau.LLAMA)
            drawOval(PaletaLibretaNouveau.FUEGO, Offset(base.x - 1.2f * u * tam, base.y - 5 * u * tam), Size(2.4f * u * tam, 4.4f * u * tam))
        }
    }

    /**
     * La luz de la vela sobre lo que tiene cerca, de noche, encima de todo: el canto de las hojas, el iris y la esquina de la
     * derecha se entibian. Lo lejano ya se hundió en la tela (ver [tapa]); el lado frío está en los cantos y en la sombra.
     */
    private fun DrawScope.luzTapa(cuerpo: Rect, oscuro: Boolean, noche: Boolean) {
        if (!oscuro || noche) return
        val w = size.width; val h = size.height
        val v = velaTapa(w, h)
        val x0 = w * 0.5f
        drawRect(Brush.radialGradient(0f to PaletaLibretaNouveau.LLAMA.copy(alpha = 0.24f), 1f to Color.Transparent, center = v, radius = w * 0.5f), Offset(x0, 0f), Size(w - x0, h))
    }

    /**
     * La portadilla, la que se ve al abrir el tomo: el nombre de la revista en la letra del cartel, «revue illustrée», la
     * libélula en su marco de látigo, el filete y, al pie, la fecha del día y el lema del almanaque.
     */
    private fun DrawScope.portadilla(r: Rect, e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val hoja = papel(oscuro, e.noche, 0.65f)
        drawRect(hoja, r.topLeft, r.size)
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent), r.left, r.left + 16 * u), r.topLeft, Size(16 * u, r.height))
        val acento = if (oscuro) LUZ else Color(0xFF4B6A4E)
        val marco = Rect(r.left + 18 * u, r.top + 6 * u, r.right - 8 * u, r.bottom - 6 * u)
        drawRect(acento.copy(alpha = 0.45f), marco.topLeft, marco.size, style = Stroke(0.6f * u))
        listOf(marco.topLeft, marco.topRight, marco.bottomLeft, marco.bottomRight).forEach { q -> drawCircle(acento.copy(alpha = 0.6f), 1.4f * u, q) }
        val col = Rect(r.left + 20 * u, r.top + 8 * u, r.right - 12 * u, r.bottom - 8 * u)
        textoEn(medidor, "L'Atelier", Rect(col.left, col.top, col.right, col.top + col.height * 0.24f), TITULO, tinta(oscuro), 24f, minimo = 10f)
        textoEn(medidor, "revue illustrée", Rect(col.left, col.top + col.height * 0.23f, col.right, col.top + col.height * 0.33f), ROTULO, acento, 9f, espaciado = 1.5f, minimo = 5f)
        with(TrazosNouveau) { emblema(Offset(col.center.x, col.top + col.height * 0.56f), col.height * 0.17f, if (oscuro) LUZ else ORO, LIBELULA, NACAR) }
        val yf = col.top + col.height * 0.8f
        drawLine(acento.copy(alpha = 0.6f), Offset(col.left + col.width * 0.25f, yf), Offset(col.right - col.width * 0.25f, yf), 0.6f * u)
        textoEn(medidor, e.fecha.substringBefore(" · "), Rect(col.left, yf + 2 * u, col.right, col.bottom), TEXTO, tenue(oscuro), 10f, minimo = 6f)
    }

    // =================================================================
    //  La página abierta y el escritor
    // =================================================================

    override val altoEncabezado: Dp = 118.dp

    /**
     * La cabecera de la revista: arriba, un filete con las puntas enroscadas en látigo; a la izquierda, el medallón con un iris;
     * a la derecha, la libélula en su marco de látigo y, debajo, el folio del día, entre rayas. Al centro, el rótulo en la letra
     * de los carteles, la fecha del almanaque (el día en grande; la flor y su significado debajo) y el detalle; al pie, el
     * culo de lámpara: dos látigos que se abren desde un capullo.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val tinta = p.tinta; val acento = p.acento
        val oroC = if (p.oscura) LUZ else ORO
        // El filete de arriba, con sus puntas en látigo.
        drawLine(acento.copy(alpha = 0.55f), Offset(26 * u, 7 * u), Offset(w - 26 * u, 7 * u), 0.8f * u)
        latigo(Offset(30 * u, 7 * u), Offset(10 * u, 9 * u), 3.4f * u, 1.4f * u, acento.copy(alpha = 0.8f), 0.2f, false, false)
        latigo(Offset(w - 30 * u, 7 * u), Offset(w - 10 * u, 9 * u), 3.4f * u, 1.4f * u, acento.copy(alpha = 0.8f), -0.2f, true, false)
        floron(Offset(w / 2, 6 * u), 3.4f * u, oroC)
        // El medallón con el iris.
        val mc = Offset(32 * u, 60 * u); val mr = 22 * u
        drawCircle(Color.Black.copy(alpha = 0.12f), mr, mc + Offset(0.8f * u, 1.2f * u))
        drawCircle(mezcla(p.color, if (p.oscura) Color.Black else Color.White, 0.25f), mr, mc)
        clipPath(Path().apply { addOval(Rect(mc, mr - 1 * u)) }) {
            with(TrazosNouveau) { iris(Offset(mc.x, mc.y + mr * 1.05f), mr * 1.85f, if (p.oscura) mezcla(IRIS, LUZ, 0.25f) else IRIS, mezcla(TALLO, p.color, 0.1f), mezcla(TALLO_SOMBRA, p.color, 0.1f)) }
        }
        drawCircle(oroC, mr, mc, style = Stroke(1.6f * u))
        drawCircle(oroC.copy(alpha = 0.6f), mr - 3 * u, mc, style = Stroke(0.5f * u))
        // La libélula en su marco y el folio.
        val lc = Offset(w - 32 * u, 52 * u)
        with(TrazosNouveau) { emblema(lc, 20 * u, oroC, if (p.oscura) mezcla(LIBELULA, LUZ, 0.3f) else LIBELULA, NACAR) }
        textoEn(medidor, io.github.ruddy2003.vestigium.ui.Nouveau.numero(e.numero), Rect(lc.x - 24 * u, lc.y + 23 * u, lc.x + 24 * u, lc.y + 36 * u), ROTULO, acento, 9.5f, espaciado = 0.6f, minimo = 5f)
        // El rótulo, la fecha y el detalle.
        val x0 = 62 * u; val x1 = w - 62 * u
        textoEn(medidor, e.rotulo, Rect(x0, 15 * u, x1, 31 * u), ROTULO, acento, 10f, espaciado = 0.8f, minimo = 6f)
        enDos(medidor, e.fecha, Rect(x0, 33 * u, x1, 80 * u), TITULO, tinta, 19f, k = 0.58f, color2 = p.detalle)
        textoEn(medidor, e.detalle, Rect(x0, 81 * u, x1, 96 * u), TEXTO, p.tenue, 10.5f, minimo = 6f)
        // El culo de lámpara.
        listOf(-1f, 1f).forEach { s -> latigo(Offset(w / 2 + s * 5 * u, 106 * u), Offset(w / 2 + s * (x1 - x0) * 0.3f, 104 * u), 3.2f * u, 1.3f * u, acento.copy(alpha = 0.6f), 0.2f * s, s > 0, false) }
        floron(Offset(w / 2, 105 * u), 3.6f * u, oroC.copy(alpha = 0.9f))
        drawLine(tinta.copy(alpha = 0.16f), Offset(4 * u, h - 3 * u), Offset(w - 4 * u, h - 3 * u), 0.6f * u)
    }

    /**
     * Sobre el papel de cartel (que ya trae su cenefa de tallos): las cruces de registro de la prensa en dos esquinas y, al pie,
     * el culo de lámpara. En modo oscuro, la lámpara de vidrio alumbra desde arriba a la izquierda y
     * las orillas se van al azul negro de la calle. De noche, la luna asoma abajo a la derecha, entre unas estrellas.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val tinta = p.tinta
        // Las cruces de registro, con las que se casaban las piedras de cada color.
        listOf(Offset(16 * u, 16 * u), Offset(w - 16 * u, h - 16 * u)).forEach { q ->
            drawCircle(tinta.copy(alpha = 0.16f), 4 * u, q, style = Stroke(0.6f * u))
            drawLine(tinta.copy(alpha = 0.16f), Offset(q.x - 7 * u, q.y), Offset(q.x + 7 * u, q.y), 0.6f * u)
            drawLine(tinta.copy(alpha = 0.16f), Offset(q.x, q.y - 7 * u), Offset(q.x, q.y + 7 * u), 0.6f * u)
        }
        // El culo de lámpara, al pie.
        val y = h - 46 * u
        listOf(-1f, 1f).forEach { s -> latigo(Offset(w / 2 + s * 5 * u, y), Offset(w / 2 + s * w * 0.16f, y - 2 * u), 3.4f * u, 1.3f * u, p.acento.copy(alpha = 0.35f), 0.2f * s, s > 0, false) }
        floron(Offset(w / 2, y - 1 * u), 3.6f * u, p.acento.copy(alpha = 0.45f))
        // En modo oscuro, el papel a la luz de la lámpara.
        if (oscuro && !p.oscura) {
            drawRect(Brush.radialGradient(0f to LUZ_HALO.copy(alpha = 0.12f), 0.45f to Color.Transparent, 1f to NOCHE.copy(alpha = 0.6f),
                center = Offset(w * 0.22f, h * 0.12f), radius = maxOf(w, h) * 0.95f))
        }
        if (noche) {
            val l = Offset(w - 34 * u, h - 90 * u)
            mancha(l, 44 * u, LUZ_HALO.copy(alpha = 0.1f))
            floron(l, 13 * u, LUZ_HALO.copy(alpha = 0.45f), true)
            listOf(-30f to -14f, -18f to 10f, -44f to 4f, 6f to -30f).forEach { (dx, dy) -> drawCircle(LUZ_HALO.copy(alpha = 0.4f), 1.1f * u, Offset(l.x + dx * u, l.y + dy * u)) }
        }
    }

    /** La barra de arriba es la franja de tela del lomo: verde, con dos filetes dorados y una fila de capullos de oro. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(if (p.oscura) mezcla(p.color, Color.Black, 0.3f) else mezcla(mezcla(p.color, PAPEL_PARED, 0.5f), NOCHE, if (oscuro) 0.3f else 0f))
        val banda = Rect(0f, h - 10 * u, w, h)
        tela(banda, t(TELAS[0], oscuro, noche), 211)
        drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.15f), Color.Transparent, Color.Black.copy(alpha = 0.3f)), banda.top, banda.bottom), banda.topLeft, banda.size)
        val oroC = if (oscuro || p.oscura) LUZ else ORO
        drawLine(oroC, Offset(0f, banda.top + 1.5f * u), Offset(w, banda.top + 1.5f * u), 0.6f * u)
        drawLine(oroC, Offset(0f, banda.bottom - 1.5f * u), Offset(w, banda.bottom - 1.5f * u), 0.6f * u)
        var x = 24 * u
        while (x < w - 8 * u) { floron(Offset(x, banda.center.y), 2.2f * u, oroC); x += 48 * u }
        drawLine(p.acento.copy(alpha = 0.5f), Offset(0f, banda.top - 1.6f * u), Offset(w, banda.top - 1.6f * u), 0.7f * u)
    }

    /**
     * El botón de guardar es una placa esmaltada verde, como el letrero de las entradas del metro: el marco de latón dorado, el
     * esmalte con su brillo, un filete crema por dentro y, en cada punta, un capullo dorado que mira hacia afuera.
     */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(2 * u, 2.5f * u, w - 2 * u, h - 3.5f * u)
        val rad = CornerRadius(minOf(r.height / 2f, 9 * u))
        val oroC = if (oscuro || noche) mezcla(ORO, LUZ, 0.4f) else ORO
        if (oscuro || noche) drawRoundRect(LUZ_HALO.copy(alpha = 0.18f), r.inflate(3 * u).topLeft, r.inflate(3 * u).size, CornerRadius(rad.x + 3 * u))
        drawRoundRect(Color.Black.copy(alpha = 0.3f), r.topLeft + Offset(1.2f * u, 2.2f * u), r.size, rad)
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(oroC, Color.White, 0.3f), oroC, mezcla(oroC, Color.Black, 0.35f)), r.top, r.bottom), r.topLeft, r.size, rad)
        val esmalte = r.deflate(2.2f * u)
        val radE = CornerRadius((rad.x - 2.2f * u).coerceAtLeast(0f))
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(HIERRO, Color.White, 0.12f), HIERRO, mezcla(HIERRO, Color.Black, 0.3f)), esmalte.top, esmalte.bottom), esmalte.topLeft, esmalte.size, radE)
        val filete = esmalte.deflate(2.4f * u)
        drawRoundRect(PAPEL_CLARO.copy(alpha = 0.5f), filete.topLeft, filete.size, CornerRadius((radE.x - 2.4f * u).coerceAtLeast(0f)), style = Stroke(0.6f * u))
        // Los capullos de las puntas, que miran hacia afuera.
        rotate(-90f, Offset(r.left + 9 * u, r.center.y)) { floron(Offset(r.left + 9 * u, r.center.y), 2.8f * u, oroC) }
        rotate(90f, Offset(r.right - 9 * u, r.center.y)) { floron(Offset(r.right - 9 * u, r.center.y), 2.8f * u, oroC) }
        drawRoundRect(Color.White.copy(alpha = 0.16f), Offset(esmalte.left + 6 * u, esmalte.top + 1.2f * u), Size(esmalte.width - 12 * u, esmalte.height * 0.3f), CornerRadius(radE.x))
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = PAPEL_CLARO

    // =================================================================
    //  La libreta de momentos: el carnet de bocetos en su atril (MuebleLibretaNouveau*.kt)
    // =================================================================

    override val altoLibreta: Dp = MuebleLibretaNouveau.ALTO

    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) =
        with(MuebleLibretaNouveau) { libreta(e, medidor, oscuro) }

    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) =
        with(IndiceLibretaNouveau) { pestana(e, medidor, oscuro) }

    /** El rótulo de un mes: el título de sección de la revista, entre dos líneas de látigo que se enroscan hacia él. */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val tam = 13f
        val largo = (anchoTx(medidor, texto, TITULO, tam) + 22 * u).coerceAtMost(w - 90 * u)
        val oroC = if (oscuro) LUZ else OCRE
        val verde = if (oscuro) mezcla(TALLO, LUZ, 0.2f) else Color(0xFF4B6A4E)
        listOf(-1f, 1f).forEach { s ->
            val desde = Offset(w / 2 + s * (w / 2 - 8 * u), h / 2 + 2 * u)
            val hasta = Offset(w / 2 + s * (largo / 2 + 6 * u), h / 2 - 1 * u)
            latigo(desde, hasta, 4 * u, 1.6f * u, verde, 0.12f * s, s < 0)
            floron(Offset(desde.x, desde.y), 3 * u, oroC)
        }
        textoEn(medidor, texto, Rect(w / 2 - largo / 2, 0f, w / 2 + largo / 2, h), TITULO, tinta(oscuro), tam, minimo = 7f)
    }

    // =================================================================
    //  Las cartas: tarjetas postales ilustradas, atadas en paquete
    // =================================================================

    /**
     * El anverso de una postal en [r]: la ilustración propia, en su marco de arco con látigos (según [variante]: el estanque con
     * el iris y la libélula, el invernadero de hierro y vidrio con su cúpula, o la barandilla de fundición de una entrada de metro
     * entre castaños), y abajo la franja blanca donde se escribe, con [linea1] y [linea2].
     */
    private fun DrawScope.anverso(r: Rect, variante: Int, linea1: String, linea2: String, medidor: TextMeasurer, oscuro: Boolean, k: Float) {
        val u = density
        fun c(x: Color) = luz(x, oscuro, false, 0.45f)
        drawRect(c(PAPEL_CLARO), r.topLeft, r.size)
        val franja = r.top + r.height * 0.72f
        val il = Rect(r.left + r.width * 0.05f, r.top + r.height * 0.06f, r.right - r.width * 0.05f, franja)
        val arco = Path().apply {
            moveTo(il.left, il.bottom); lineTo(il.left, il.top + il.height * 0.3f)
            cubicTo(il.left, il.top - il.height * 0.08f, il.right, il.top - il.height * 0.08f, il.right, il.top + il.height * 0.3f)
            lineTo(il.right, il.bottom); close()
        }
        clipPath(arco) {
            when (variante) {
                0 -> {
                    // El estanque al atardecer: el cielo malva, el sol, el agua verde, el iris y la libélula.
                    drawRect(Brush.verticalGradient(listOf(c(mezcla(MALVA, Color.White, 0.3f)), c(mezcla(AMBAR, Color.White, 0.35f))), il.top, il.bottom), il.topLeft, il.size)
                    drawCircle(c(mezcla(AMBAR, Color.White, 0.5f)), il.height * 0.2f, Offset(il.left + il.width * 0.3f, il.top + il.height * 0.5f))
                    drawRect(c(VERDE_VIDRIO), Offset(il.left, il.bottom - il.height * 0.22f), Size(il.width, il.height * 0.22f))
                    for (j in 0 until 4) drawLine(c(Color.White).copy(alpha = 0.4f), Offset(il.left + il.width * (0.1f + 0.2f * j), il.bottom - il.height * (0.15f - 0.03f * (j % 2))), Offset(il.left + il.width * (0.22f + 0.2f * j), il.bottom - il.height * (0.15f - 0.03f * (j % 2))), 0.6f * u * k)
                    with(TrazosNouveau) {
                        iris(Offset(il.left + il.width * 0.66f, il.bottom), il.height * 0.95f, c(IRIS), c(TALLO), c(TALLO_SOMBRA), c(TrazosNouveau.PLUMA).copy(alpha = 0.6f))
                        libelula(Offset(il.left + il.width * 0.24f, il.top + il.height * 0.32f), il.height * 0.34f, 70f, 0f, c(TrazosNouveau.LIBELULA), c(TrazosNouveau.NACAR), c(ORO))
                    }
                }
                1 -> {
                    // El invernadero de hierro y vidrio: la cúpula y las naves, con la luz que se filtra.
                    drawRect(Brush.verticalGradient(listOf(c(Color(0xFFCFDCCB)), c(Color(0xFFEDE6CF))), il.top, il.bottom), il.topLeft, il.size)
                    val hierro = c(HIERRO)
                    val cc = Offset(il.center.x, il.bottom - il.height * 0.38f)
                    val rd = il.width * 0.22f
                    drawArc(c(Color(0xFFE6EFE2)), 180f, 180f, true, Offset(cc.x - rd, cc.y - rd), Size(rd * 2, rd * 2))
                    for (j in 0..6) { val ang = PI * j / 6; drawLine(hierro, cc, Offset(cc.x - rd * cos(ang).toFloat(), cc.y - rd * sin(ang).toFloat()), 0.7f * u * k) }
                    drawArc(hierro, 180f, 180f, false, Offset(cc.x - rd, cc.y - rd), Size(rd * 2, rd * 2), style = Stroke(1.2f * u * k))
                    drawArc(hierro, 180f, 180f, false, Offset(cc.x - rd * 0.55f, cc.y - rd * 0.55f), Size(rd * 1.1f, rd * 1.1f), style = Stroke(0.6f * u * k))
                    listOf(-1f, 1f).forEach { s ->
                        val nave = Rect(if (s < 0) il.left + il.width * 0.04f else cc.x + rd, cc.y - il.height * 0.08f, if (s < 0) cc.x - rd else il.right - il.width * 0.04f, il.bottom)
                        drawRect(c(Color(0xFFE6EFE2)), nave.topLeft, nave.size)
                        var x = nave.left
                        while (x <= nave.right) { drawLine(hierro, Offset(x, nave.top), Offset(x, il.bottom), 0.7f * u * k); x += 7 * u * k }
                        drawLine(hierro, nave.topLeft, nave.topRight, 1.2f * u * k)
                    }
                    drawRect(hierro, Offset(cc.x - rd, cc.y), Size(rd * 2, 1.2f * u * k))
                    var x = cc.x - rd
                    while (x <= cc.x + rd) { drawLine(hierro, Offset(x, cc.y), Offset(x, il.bottom), 0.7f * u * k); x += 6 * u * k }
                    drawRect(c(TALLO_SOMBRA), Offset(il.left, il.bottom - il.height * 0.1f), Size(il.width, il.height * 0.1f))
                    with(TrazosNouveau) { tallo(Offset(il.left + il.width * 0.12f, il.bottom), il.height * 0.7f, 1f, 1f, il.height * 0.025f, c(TALLO), c(TALLO_SOMBRA), c(MALVA)) }
                }
                else -> {
                    // La barandilla de fundición de una entrada de metro, verde, con sus escudos, entre dos castaños, a la hora azul.
                    drawRect(Brush.verticalGradient(listOf(c(Color(0xFF6E7AA0)), c(Color(0xFFD9B98A))), il.top, il.bottom), il.topLeft, il.size)
                    listOf(il.left + il.width * 0.1f, il.right - il.width * 0.1f).forEach { x ->
                        drawCircle(c(mezcla(HIERRO, TALLO, 0.4f)), il.height * 0.26f, Offset(x, il.top + il.height * 0.32f))
                        drawLine(c(NOGAL_OSC), Offset(x, il.top + il.height * 0.4f), Offset(x, il.bottom), il.width * 0.025f)
                    }
                    drawRect(c(PIEDRA), Offset(il.left, il.bottom - il.height * 0.14f), Size(il.width, il.height * 0.14f))
                    val hierro = c(HIERRO)
                    val y0 = il.bottom - il.height * 0.14f
                    val alto = il.height * 0.32f
                    drawLine(hierro, Offset(il.left + il.width * 0.2f, y0 - alto), Offset(il.right - il.width * 0.2f, y0 - alto), 1.4f * u * k)
                    var x = il.left + il.width * 0.22f; var j = 0
                    while (x < il.right - il.width * 0.2f) {
                        val p = Path().apply { moveTo(x, y0); cubicTo(x + 3 * u * k, y0 - alto * 0.4f, x - 3 * u * k, y0 - alto * 0.7f, x + 1 * u * k, y0 - alto) }
                        drawPath(p, hierro, style = Stroke(0.9f * u * k))
                        if (j % 3 == 1) drawOval(hierro, Offset(x - 3 * u * k, y0 - alto * 0.62f), Size(6 * u * k, 7 * u * k))
                        x += 5 * u * k; j++
                    }
                    listOf(il.left + il.width * 0.2f, il.right - il.width * 0.2f).forEachIndexed { i, xx ->
                        latigo(Offset(xx, y0), Offset(xx + (if (i == 0) -1f else 1f) * 4 * u * k, y0 - alto * 1.3f), 3 * u * k, 1.6f * u * k, hierro, if (i == 0) 0.2f else -0.2f, i == 0, false)
                    }
                }
            }
        }
        drawPath(arco, c(TINTA).copy(alpha = 0.7f), style = Stroke(0.8f * u * k))
        // Los látigos de las esquinas de abajo, que abrazan la ilustración.
        listOf(-1f, 1f).forEach { s ->
            val desde = Offset(if (s < 0) il.left + 2 * u else il.right - 2 * u, il.bottom - il.height * 0.02f)
            latigo(desde, Offset(desde.x - s * 9 * u * k, il.bottom - il.height * 0.32f), 2.4f * u * k, 1.2f * u * k, c(ORO), -0.2f * s, s > 0, false)
        }
        // La franja de abajo, con lo que se escribió.
        val fr = Rect(r.left + r.width * 0.05f, franja + 2 * u, r.right - r.width * 0.17f, r.bottom - 2 * u)
        if (linea1.isNotBlank()) textoEn(medidor, linea1, Rect(fr.left, fr.top, fr.right, fr.top + fr.height * 0.55f), TITULO, tinta(oscuro), 10f * k, minimo = 5f)
        if (linea2.isNotBlank()) textoEn(medidor, linea2, Rect(fr.left, fr.top + fr.height * 0.52f, fr.right, fr.bottom), TEXTO, tenue(oscuro), 8f * k, minimo = 4.5f)
    }

    /**
     * El reverso de una postal en [r], el lado dividido: «Carte postale» arriba, la raya del medio, a la izquierda los renglones
     * para la correspondencia (en el color [renglones]), a la derecha el sello de correos con el iris, el matasellos de París y las
     * líneas de la dirección, con [linea1] y [linea2].
     */
    private fun DrawScope.reverso(r: Rect, linea1: String, linea2: String, hoja: Color, renglones: Color, escrito: Float, medidor: TextMeasurer, oscuro: Boolean, k: Float) {
        val u = density
        fun c(x: Color) = luz(x, oscuro, false, 0.45f)
        drawRect(mezcla(c(PAPEL_CLARO), hoja, 0.25f), r.topLeft, r.size)
        val tintaC = tinta(oscuro)
        textoEn(medidor, "Carte postale", Rect(r.left, r.top + r.height * 0.03f, r.right, r.top + r.height * 0.17f), ROTULO, tintaC.copy(alpha = 0.8f), 10f * k, espaciado = 1.2f, minimo = 5f)
        val medio = r.left + r.width * 0.52f
        latigo(Offset(medio, r.bottom - r.height * 0.06f), Offset(medio, r.top + r.height * 0.24f), 2.4f * u * k, 0.9f * u * k, c(TALLO_SOMBRA), 0.04f, true, false)
        // La correspondencia: renglones (y, al abrirse, el trazo de lo escrito).
        var y = r.top + r.height * 0.32f; var j = 0
        while (y < r.bottom - r.height * 0.08f) {
            drawLine(renglones, Offset(r.left + r.width * 0.06f, y), Offset(medio - r.width * 0.05f, y), 0.5f * u)
            if (escrito > 0f && j < 6) {
                val largo = (medio - r.width * 0.11f - r.left - r.width * 0.06f) * (0.6f + 0.35f * azar(j, 3)) * escrito.coerceIn(0f, 1f)
                drawLine(tintaC.copy(alpha = 0.18f), Offset(r.left + r.width * 0.06f, y - 2.5f * u * k), Offset(r.left + r.width * 0.06f + largo, y - 2.5f * u * k), 1f * u * k, StrokeCap.Round)
            }
            y += 9 * u * k; j++
        }
        // El sello de correos, con su dentado, y el matasellos encima.
        val st = Rect(r.right - r.width * 0.2f, r.top + r.height * 0.18f, r.right - r.width * 0.05f, r.top + r.height * 0.18f + r.width * 0.18f)
        drawRect(c(PAPEL_CLARO), st.topLeft, st.size)
        var x = st.left
        while (x <= st.right) { drawCircle(mezcla(c(PAPEL_CLARO), hoja, 0.25f), 0.9f * u * k, Offset(x, st.top)); drawCircle(mezcla(c(PAPEL_CLARO), hoja, 0.25f), 0.9f * u * k, Offset(x, st.bottom)); x += 2.4f * u * k }
        val dentro = st.deflate(2 * u * k)
        drawRect(c(TALLO_SOMBRA), dentro.topLeft, dentro.size)
        clipRect(dentro.left, dentro.top, dentro.right, dentro.bottom) {
            with(TrazosNouveau) { iris(Offset(dentro.center.x, dentro.bottom), dentro.height * 1.05f, c(mezcla(IRIS, Color.White, 0.3f)), c(mezcla(TALLO, Color.White, 0.3f)), c(TALLO)) }
        }
        textoEn(medidor, "5 c", Rect(dentro.left, dentro.top, dentro.right, dentro.top + dentro.height * 0.3f), ROTULO, c(PAPEL_CLARO), 6f * k, minimo = 3f)
        val ms = Offset(st.left - r.width * 0.02f, st.center.y + st.height * 0.12f)
        val mr = st.width * 0.48f
        val tintaM = c(Color(0xFF3B3346)).copy(alpha = 0.6f)
        drawCircle(tintaM, mr, ms, style = Stroke(0.8f * u * k))
        drawCircle(tintaM, mr * 0.68f, ms, style = Stroke(0.5f * u * k))
        textoEn(medidor, "Paris", Rect(ms.x - mr * 0.66f, ms.y - mr * 0.6f, ms.x + mr * 0.66f, ms.y - mr * 0.05f), ROTULO, tintaM, 6f * k, minimo = 2.5f)
        for (i in 0 until 4) {
            val yo = ms.y - mr * 0.4f + i * mr * 0.3f
            val p = Path().apply { moveTo(ms.x + mr * 1.05f, yo); cubicTo(ms.x + mr * 1.6f, yo - 3 * u * k, ms.x + mr * 2f, yo + 3 * u * k, ms.x + mr * 2.6f, yo) }
            drawPath(p, tintaM, style = Stroke(0.7f * u * k))
        }
        // Las líneas de la dirección.
        val dir = Rect(medio + r.width * 0.05f, r.top + r.height * 0.52f, r.right - r.width * 0.05f, r.bottom - r.height * 0.08f)
        listOf(0.33f, 0.66f, 1f).forEach { f -> drawLine(renglones, Offset(dir.left, dir.top + dir.height * f), Offset(dir.right, dir.top + dir.height * f), 0.5f * u) }
        if (linea1.isNotBlank()) aIzquierda(medidor, linea1, Rect(dir.left, dir.top, dir.right, dir.top + dir.height * 0.33f), TEXTO, tintaC.copy(alpha = 0.85f), 8.5f * k, minimo = 4.5f)
        if (linea2.isNotBlank()) aIzquierda(medidor, linea2, Rect(dir.left, dir.top + dir.height * 0.33f, dir.right, dir.top + dir.height * 0.66f), TEXTO, tenue(oscuro), 7.5f * k, minimo = 4f)
    }

    /**
     * La carta es una tarjeta postal ilustrada, en su paquete: debajo, dos postales más; encima, la del día, con el anverso
     * ilustrado (y en su franja, a quién va y cuándo). Las cartas al futuro, «postales para el siglo nuevo», van atadas con una
     * cinta de seda malva, y en el cruce de la cinta, la viñeta engomada con el iris hace de sello; la que ya se puede abrir
     * brilla con la luz de la lámpara. Al abrirla, la viñeta se rasga, la postal se da vuelta (se ve el reverso dividido, con el
     * sello y el matasellos) y sube.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val cu = e.cuerpo
        val s = Rect(cu.left + 6 * u, cu.top + 4 * u, cu.right - 6 * u, cu.bottom - 6 * u)
        val cw = s.width * 0.92f
        val ch = minOf(s.height * 0.92f, cw / 1.45f)
        val k = (cw / (134f * u)).coerceIn(0.8f, 2.4f)
        val alza = e.subir.coerceIn(0f, 1f) * cu.top * 0.9f
        val base = Rect(s.center.x - cw / 2, s.center.y - ch / 2, s.center.x + cw / 2, s.center.y + ch / 2)
        val carta = base.translate(0f, -alza)
        val variante = Math.floorMod(e.semilla, 3)
        // Las postales de debajo, un poco torcidas.
        listOf(-5f to Color(0xFFE6DCC4), 4f to Color(0xFFEDE4CE)).forEachIndexed { i, (g, col) ->
            rotate(g, base.center) {
                val q = base.translate(if (i == 0) -2 * u else 3 * u, if (i == 0) 3 * u else 1 * u)
                drawRect(Color.Black.copy(alpha = 0.22f), q.topLeft + Offset(1.5f * u, 2.5f * u), q.size)
                drawRect(t(col, oscuro, false), q.topLeft, q.size)
                // Se adivina su ilustración, en su arco.
                val il = Rect(q.left + q.width * 0.05f, q.top + q.height * 0.06f, q.right - q.width * 0.05f, q.top + q.height * 0.72f)
                drawRoundRect(t(listOf(Color(0xFF8E9A86), Color(0xFFB59AAE))[i], oscuro, false), il.topLeft, il.size, CornerRadius(il.width * 0.3f, il.height * 0.25f))
                drawRect(Color.Black.copy(alpha = 0.15f), q.topLeft, q.size, style = Stroke(0.5f * u))
            }
        }
        // El brillo de la lámpara alrededor de la que ya se puede abrir.
        if (e.brillo > 0f) drawRoundRect(LUZ_HALO.copy(alpha = 0.35f * e.brillo), carta.inflate(5 * u).topLeft, carta.inflate(5 * u).size, CornerRadius(6 * u))
        // La postal de encima, que se da vuelta al abrirse.
        val giro = cos(PI * e.abrir.coerceIn(0f, 1f)).toFloat()
        val ancho = carta.width * abs(giro).coerceAtLeast(0.02f)
        val vista = Rect(carta.center.x - ancho / 2, carta.top, carta.center.x + ancho / 2, carta.bottom)
        drawRect(Color.Black.copy(alpha = 0.28f), vista.topLeft + Offset(2 * u, 3 * u), vista.size)
        withTransform({ scale(abs(giro).coerceAtLeast(0.02f), 1f, carta.center) }) {
            if (giro >= 0f) anverso(carta, variante, e.linea1, e.linea2, medidor, oscuro, k)
            else reverso(carta, e.linea1, e.linea2, e.hoja, e.renglones, (e.subir * 2f).coerceIn(0f, 1f), medidor, oscuro, k)
            drawRect(Color.Black.copy(alpha = 0.25f), carta.topLeft, carta.size, style = Stroke(0.6f * u))
        }
        if (giro < 0.98f) drawRect(Color.Black.copy(alpha = 0.25f * (1f - abs(giro))), vista.topLeft, vista.size)
        if (giro < 0f) return
        // La cinta de seda que ata el paquete: una vuelta de arriba abajo y otra de lado a lado, cruzadas.
        val cruce = Offset(carta.left + carta.width * 0.89f, carta.top + carta.height * 0.4f)
        val seda = t(MALVA, oscuro, false)
        if (e.cordon > 0f) {
            val f = e.cordon.coerceIn(0f, 1f)
            val g = 4.4f * u * k
            val v1 = (f * 2f).coerceAtMost(1f)
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(cruce.x - g / 2 + 1 * u, carta.top - 2 * u), Size(g, (carta.height + 4 * u) * v1))
            drawRect(Brush.horizontalGradient(listOf(mezcla(seda, Color.White, 0.3f), seda, mezcla(seda, Color.Black, 0.2f)), cruce.x - g / 2, cruce.x + g / 2), Offset(cruce.x - g / 2, carta.top - 2 * u), Size(g, (carta.height + 4 * u) * v1))
            if (f > 0.5f) {
                val v2 = ((f - 0.5f) * 2f).coerceIn(0f, 1f)
                drawRect(Color.Black.copy(alpha = 0.18f), Offset(carta.left - 2 * u, cruce.y - g / 2 + 1 * u), Size((carta.width + 4 * u) * v2, g))
                drawRect(Brush.verticalGradient(listOf(mezcla(seda, Color.White, 0.3f), seda, mezcla(seda, Color.Black, 0.2f)), cruce.y - g / 2, cruce.y + g / 2), Offset(carta.left - 2 * u, cruce.y - g / 2), Size((carta.width + 4 * u) * v2, g))
            }
            if (f >= 1f) {
                // Las dos lazadas y los cabos sueltos del lazo, bajo la viñeta.
                listOf(-1f, 1f).forEach { sg ->
                    val p = Path().apply { moveTo(cruce.x, cruce.y); cubicTo(cruce.x + sg * 14 * u * k, cruce.y - 12 * u * k, cruce.x + sg * 18 * u * k, cruce.y + 6 * u * k, cruce.x, cruce.y); close() }
                    drawPath(p, seda); drawPath(p, mezcla(seda, Color.Black, 0.3f), style = Stroke(0.6f * u))
                    drawLine(seda, cruce, cruce + Offset(sg * 7 * u * k, 16 * u * k), 3 * u * k, StrokeCap.Butt)
                }
            }
        }
        // La viñeta: entera (y, al sellar, cayendo desde grande), o rasgada en dos.
        val ro = minOf(carta.width, carta.height) * 0.15f
        val papelV = t(Color(0xFFF2E9D2), oscuro, false)
        val tintaV = t(if (e.alFuturo) IRIS else HIERRO, oscuro, false)
        if (e.romper <= 0f) {
            if (e.brillo > 0f) drawCircle(Brush.radialGradient(listOf(LUZ_HALO.copy(alpha = 0.6f * e.brillo), Color.Transparent), center = cruce, radius = ro * 2.6f), ro * 2.6f, cruce)
            if (e.alfaSello > 0f) withTransform({ scale(e.escalaSello, e.escalaSello, cruce) }) { vinetaNouveau(cruce, ro, papelV, tintaV, e.alfaSello) }
        } else {
            val rr = e.romper.coerceIn(0f, 1f)
            val corte = Path().apply {
                moveTo(cruce.x - ro * 0.1f, cruce.y - ro * 1.2f); lineTo(cruce.x + ro * 0.12f, cruce.y - ro * 0.4f); lineTo(cruce.x - ro * 0.08f, cruce.y + ro * 0.2f)
                lineTo(cruce.x + ro * 0.1f, cruce.y + ro * 1.2f)
            }
            val izq = Path().apply { addPath(corte); lineTo(cruce.x - ro * 1.3f, cruce.y + ro * 1.2f); lineTo(cruce.x - ro * 1.3f, cruce.y - ro * 1.2f); close() }
            val der = Path().apply { addPath(corte); lineTo(cruce.x + ro * 1.3f, cruce.y + ro * 1.2f); lineTo(cruce.x + ro * 1.3f, cruce.y - ro * 1.2f); close() }
            withTransform({ translate(-3 * u * rr, 1 * u * rr); rotate(-8f * rr, cruce) }) { clipPath(izq) { vinetaNouveau(cruce, ro, papelV, tintaV) } }
            withTransform({ translate(3 * u * rr, 2 * u * rr); rotate(9f * rr, cruce) }) { clipPath(der) { vinetaNouveau(cruce, ro, papelV, tintaV) } }
        }
    }

    // La postal que sube, dada vuelta: lo escrito va en la mitad izquierda del reverso, bajo «Carte postale».
    override val cajaPrevia = Rect(0.13f, 0.37f, 0.49f, 0.7f)

    override val altoBuzon: Dp = 210.dp

    /**
     * El correo del taller: un portacartas de nogal colgado de la pared de papel pintado, con el copete en látigo donde dice
     * «Correspondance» en un cartucho de latón; abajo, tres bolsillos tallados, cada uno con su portaetiquetas: las postales
     * que ya se pueden abrir (con la luz de la lámpara), las atadas con su cinta y su viñeta, y las leídas. Caben siete por
     * bolsillo; si hay más, se dice cuántas más. De noche, la lámpara de vidrio alumbra desde la repisa de la derecha.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        papelPintado(Rect(0f, 0f, w, h), c(PAPEL_PARED), c(TALLO_SOMBRA), 251)
        if (!oscuro) drawRect(Brush.horizontalGradient(listOf(Color(0xFFFFF8E8).copy(alpha = 0.25f), Color.Transparent, Color.Black.copy(alpha = 0.1f)), 0f, w), Offset.Zero, Size(w, h))
        // El tablero del portacartas, con su copete en látigo.
        val tab = Rect(w * 0.05f, h * 0.08f, w * 0.95f, h * 0.96f)
        val tablero = Path().apply {
            moveTo(tab.left, tab.bottom); lineTo(tab.left, tab.top + h * 0.16f)
            cubicTo(tab.left + tab.width * 0.18f, tab.top + h * 0.1f, tab.center.x - tab.width * 0.2f, tab.top + h * 0.12f, tab.center.x - tab.width * 0.16f, tab.top + h * 0.04f)
            cubicTo(tab.center.x - tab.width * 0.1f, tab.top - h * 0.04f, tab.center.x + tab.width * 0.1f, tab.top - h * 0.04f, tab.center.x + tab.width * 0.16f, tab.top + h * 0.04f)
            cubicTo(tab.center.x + tab.width * 0.2f, tab.top + h * 0.12f, tab.right - tab.width * 0.18f, tab.top + h * 0.1f, tab.right, tab.top + h * 0.16f)
            lineTo(tab.right, tab.bottom); close()
        }
        translate(2 * u, 4 * u) { drawPath(tablero, Color.Black.copy(alpha = 0.3f)) }
        clipPath(tablero) {
            nogal(Rect(tab.left, tab.top - h * 0.05f, tab.right, tab.bottom), luz(NOGAL, oscuro, false, 0.5f), 253, vertical = true)
            drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.04f else 0.14f), Color.Transparent, Color.Black.copy(alpha = 0.25f)), tab.left, tab.right), tab.topLeft - Offset(0f, h * 0.05f), Size(tab.width, tab.height + h * 0.05f))
        }
        tallado(tablero, luz(NOGAL_LUZ, oscuro, false, 0.5f), 1.6f * u)
        // El cartucho de latón con el título.
        val tit = Rect(w / 2 - w * 0.2f, tab.top + h * 0.03f, w / 2 + w * 0.2f, tab.top + h * 0.16f)
        portaetiqueta(tit, "Correspondance", medidor, oscuro, false)
        // Los tres bolsillos.
        val cuentas = listOf(e.listas, e.selladas, e.abiertas)
        val nombres = listOf("Para abrir · ${e.listas}", "Atadas · ${e.selladas}", "Leídas · ${e.abiertas}")
        val anchoB = (tab.width - 24 * u) / 3f
        for (j in 0 until 3) {
            val bx = tab.left + 6 * u + j * (anchoB + 6 * u)
            val boca = tab.top + h * 0.42f
            val bol = Rect(bx, boca, bx + anchoB, tab.bottom - 8 * u)
            // Las postales que asoman, un poco abiertas en abanico.
            val n = cuentas[j].coerceAtMost(7)
            for (i in 0 until n) {
                val g = (i - (n - 1) / 2f) * 6f
                val q = Rect(bol.left + anchoB * 0.12f, boca - h * 0.2f, bol.right - anchoB * 0.12f, boca + h * 0.1f)
                rotate(g, Offset(q.center.x, q.bottom)) {
                    drawRect(Color.Black.copy(alpha = 0.2f), q.topLeft + Offset(1 * u, 1.5f * u), q.size)
                    val col = when (j) { 0 -> mezcla(PAPEL_CLARO, LUZ, 0.25f); 1 -> mezcla(PAPEL, MALVA, 0.12f); else -> PAPEL }
                    drawRect(luz(col, oscuro, false, if (j == 0) 0.8f else 0.4f), q.topLeft, q.size)
                    // Un poco de la ilustración del anverso.
                    val il = Rect(q.left + q.width * 0.08f, q.top + q.height * 0.1f, q.right - q.width * 0.08f, q.top + q.height * 0.5f)
                    drawRect(luz(listOf(MALVA, VERDE_VIDRIO, Color(0xFF6E7AA0))[(i + j) % 3], oscuro, false, 0.4f).copy(alpha = 0.6f), il.topLeft, il.size)
                    drawRect(Color.Black.copy(alpha = 0.15f), q.topLeft, q.size, style = Stroke(0.5f * u))
                    if (j == 1) drawRect(luz(MALVA, oscuro, false, 0.4f), Offset(q.left + q.width * 0.7f, q.top), Size(3 * u, q.height))
                }
            }
            if (j == 1 && n > 0) vinetaNouveau(Offset(bol.left + anchoB * 0.65f, boca - h * 0.08f), 7 * u, t(Color(0xFFF2E9D2), oscuro, false), t(IRIS, oscuro, false))
            if (j == 0 && n > 0) mancha(Offset(bol.center.x, boca - h * 0.08f), anchoB * 0.7f, LUZ_HALO.copy(alpha = if (oscuro) 0.4f else 0.3f))
            // El frente del bolsillo: nogal tallado con el borde de arriba en curva.
            val frente = Path().apply {
                moveTo(bol.left, bol.bottom); lineTo(bol.left, boca + 4 * u)
                quadraticTo(bol.center.x, boca - 6 * u, bol.right, boca + 4 * u); lineTo(bol.right, bol.bottom); close()
            }
            translate(0f, 2 * u) { drawPath(frente, Color.Black.copy(alpha = 0.35f)) }
            clipPath(frente) { nogal(bol, luz(mezcla(NOGAL, NOGAL_LUZ, 0.25f), oscuro, false, 0.45f), 257 + j) }
            tallado(frente, luz(NOGAL_LUZ, oscuro, false, 0.45f), 1.2f * u)
            relieve(TrazosNouveau.hojaLanza(Offset(bol.center.x - anchoB * 0.25f, bol.bottom - 8 * u), Offset(bol.center.x + anchoB * 0.25f, bol.bottom - 12 * u), 5 * u, 0.15f),
                luz(mezcla(NOGAL, NOGAL_LUZ, 0.45f), oscuro, false, 0.45f), 1 * u)
            portaetiqueta(Rect(bol.left + 2 * u, boca + h * 0.12f, bol.right - 2 * u, boca + h * 0.21f), nombres[j], medidor, oscuro, false)
            if (cuentas[j] > 7) textoEn(medidor, "+${cuentas[j] - 7}", Rect(bol.left, boca + h * 0.24f, bol.right, boca + h * 0.36f), TITULO, if (oscuro) LUZ else PAPEL_CLARO, 12f, minimo = 6f)
        }
        if (oscuro) {
            val l = Offset(w * 0.9f, h * 0.05f)
            resplandor(l, w * 0.6f)
            drawRect(Brush.radialGradient(0f to Color.Transparent, 0.3f to Color.Transparent, 1f to NOCHE.copy(alpha = 0.5f), center = l, radius = w * 1.1f), Offset.Zero, Size(w, h))
        }
    }

    // =================================================================
    //  Lo de alrededor: las pestañas, el buscador, las obras y los recuerdos
    // =================================================================

    override val altoPestanas: Dp = 78.dp

    /**
     * El emblema de cada biblioteca, como trazo dentro de [r]: 0, el diario (el tomo con sus nervios); 1, los momentos (el carnet
     * con su cinta); 2, los sueños (la media luna y una estrella); 3, el ideario (el cajón plano con su tirador); 4, las cartas
     * (la postal con su sello).
     */
    private fun iconoBiblioteca(k: Int, r: Rect): Path = Path().apply {
        val w = r.width; val h = r.height; val x = r.left; val y = r.top
        val c = r.center
        when (k) {
            0 -> {
                addRoundRect(RoundRect(Rect(x + w * 0.28f, y + h * 0.04f, x + w * 0.72f, y + h * 0.96f), CornerRadius(w * 0.08f)))
                listOf(0.22f, 0.4f, 0.74f, 0.86f).forEach { f -> moveTo(x + w * 0.28f, y + h * f); lineTo(x + w * 0.72f, y + h * f) }
            }
            1 -> {
                addRect(Rect(x + w * 0.08f, y + h * 0.18f, x + w * 0.92f, y + h * 0.82f))
                moveTo(x + w * 0.22f, y + h * 0.18f); lineTo(x + w * 0.22f, y + h * 0.82f)
                moveTo(x + w * 0.08f, y + h * 0.56f); lineTo(x + w * 0.92f, y + h * 0.56f)
                moveTo(x + w * 0.74f, y + h * 0.56f); cubicTo(x + w * 0.9f, y + h * 0.36f, x + w * 1.02f, y + h * 0.62f, x + w * 0.74f, y + h * 0.56f)
            }
            2 -> {
                addArc(Rect(c.x - w * 0.38f, c.y - w * 0.38f, c.x + w * 0.38f, c.y + w * 0.38f), 60f, 240f)
                addArc(Rect(c.x - w * 0.14f, c.y - w * 0.36f, c.x + w * 0.3f, c.y + w * 0.36f), 270f, -180f)
                moveTo(c.x + w * 0.38f, c.y - h * 0.42f); lineTo(c.x + w * 0.38f, c.y - h * 0.22f)
                moveTo(c.x + w * 0.28f, c.y - h * 0.32f); lineTo(c.x + w * 0.48f, c.y - h * 0.32f)
            }
            3 -> {
                addRect(Rect(x + w * 0.04f, y + h * 0.3f, x + w * 0.96f, y + h * 0.78f))
                moveTo(x + w * 0.04f, y + h * 0.54f); lineTo(x + w * 0.96f, y + h * 0.54f)
                moveTo(c.x - w * 0.14f, y + h * 0.42f); quadraticTo(c.x, y + h * 0.34f, c.x + w * 0.14f, y + h * 0.42f)
                moveTo(c.x - w * 0.14f, y + h * 0.66f); quadraticTo(c.x, y + h * 0.58f, c.x + w * 0.14f, y + h * 0.66f)
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
     * Las pestañas son las cinco lengüetas de tela de un índice, que asoman por el canto de un tomo de la revista (la franja de
     * hojas de abajo, con el filete de oro): cada una de un color de las encuadernaciones, con su emblema, su nombre y su cuenta
     * dorados. La elegida asoma más y la alcanza la luz.
     */
    override fun DrawScope.pestanas(e: EstadoPestanas, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val canto = Rect(0f, h - 14 * u, w, h - 1 * u)
        val oroC = if (oscuro) LUZ else ORO
        for (k in 0 until 5) {
            val q0 = quinta(k, w, 0f, h)
            val sel = k == e.actual
            val sube = if (sel) 0f else 6 * u
            val r = Rect(q0.left + 3 * u, 2 * u + sube, q0.right - 3 * u, canto.top + 4 * u)
            val forma = Path().apply {
                moveTo(r.left, r.bottom); lineTo(r.left, r.top + 10 * u)
                quadraticTo(r.left, r.top, r.left + 10 * u, r.top)
                lineTo(r.right - 10 * u, r.top)
                quadraticTo(r.right, r.top, r.right, r.top + 10 * u)
                lineTo(r.right, r.bottom); close()
            }
            val telaC = luz(TELAS[listOf(0, 2, 1, 4, 3)[k]], oscuro, false, if (sel) 0.8f else 0.35f)
            translate(1 * u, 2 * u) { drawPath(forma, Color.Black.copy(alpha = 0.3f)) }
            clipPath(forma) {
                tela(r, telaC, 281 + k)
                drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (sel) 0.2f else 0.1f), Color.Transparent, Color.Black.copy(alpha = 0.25f)), r.top, r.bottom), r.topLeft, r.size)
                if (sel) mancha(Offset(r.center.x, r.top), r.width * 0.9f, LUZ_HALO.copy(alpha = if (oscuro) 0.35f else 0.25f))
            }
            val m = Rect(r.left + 3 * u, r.top + 3 * u, r.right - 3 * u, r.bottom)
            drawPath(Path().apply {
                moveTo(m.left, m.bottom); lineTo(m.left, m.top + 7 * u); quadraticTo(m.left, m.top, m.left + 7 * u, m.top)
                lineTo(m.right - 7 * u, m.top); quadraticTo(m.right, m.top, m.right, m.top + 7 * u); lineTo(m.right, m.bottom)
            }, oroC.copy(alpha = if (sel) 0.9f else 0.5f), style = Stroke(0.6f * u))
            val texto = if (sel) PAPEL_CLARO else PAPEL_CLARO.copy(alpha = 0.8f)
            val ic = Rect(r.center.x - 8 * u, r.top + 6 * u, r.center.x + 8 * u, r.top + 21 * u)
            drawPath(iconoBiblioteca(k, ic), if (sel) oroC else texto, style = Stroke(1.2f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
            textoEn(medidor, e.nombres.getOrElse(k) { "" }, Rect(r.left + 2 * u, r.top + 23 * u, r.right - 2 * u, r.top + 36 * u), TITULO, texto, 10f, minimo = 5f)
            textoEn(medidor, "${e.cuentas.getOrElse(k) { 0 }}", Rect(r.left + 2 * u, r.top + 36 * u, r.right - 2 * u, r.top + 48 * u), ROTULO, if (sel) oroC else texto, 9.5f, minimo = 5f)
        }
        // El canto de las hojas del tomo, con su filete dorado.
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(canto.left, canto.top - 2 * u), Size(canto.width, 2 * u))
        drawRect(papel(oscuro, false, 0.4f), canto.topLeft, canto.size)
        var y = canto.top + 2 * u
        while (y < canto.bottom) { drawLine(Color.Black.copy(alpha = 0.12f), Offset(canto.left, y), Offset(canto.right, y), 0.4f * u); y += 1.5f * u }
        drawLine(oroC, Offset(canto.left, canto.top + 0.5f * u), Offset(canto.right, canto.top + 0.5f * u), 1 * u)
        drawRect(Color.Black.copy(alpha = 0.2f), canto.topLeft, canto.size, style = Stroke(0.5f * u))
    }

    // ---------------------------------------------------------------- el buscador

    override val altoCampo: Dp = 52.dp
    override val cajaCampo = Rect(0.16f, 0.24f, 0.82f, 0.76f)
    override val letraCampo: FontFamily = TEXTO
    override fun tintaCampo(oscuro: Boolean): Color = tinta(oscuro)

    /**
     * El buscador es un cartucho de papel crema en su marco dorado, que remata en cada punta en una línea de látigo: a la
     * izquierda, la libélula, que abre las alas con el foco; a la derecha, el capullo de iris, que se abre y se enciende.
     */
    override fun DrawScope.campo(e: EstadoCampo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(2 * u, 4 * u, w - 2 * u, h - 4 * u)
        val rad = CornerRadius(r.height / 2f)
        val oroC = if (oscuro) mezcla(ORO, LUZ, if (e.enfocado) 0.6f else 0.2f) else if (e.enfocado) mezcla(ORO, OCRE, 0.3f) else ORO
        drawRoundRect(Color.Black.copy(alpha = 0.2f), r.topLeft + Offset(1 * u, 2 * u), r.size, rad)
        drawRoundRect(papel(oscuro, false, if (e.enfocado) 0.8f else 0.5f), r.topLeft, r.size, rad)
        if (e.enfocado) drawRoundRect(LUZ_HALO.copy(alpha = if (oscuro) 0.18f else 0.25f), r.topLeft, r.size, rad)
        drawRoundRect(oroC, r.topLeft, r.size, rad, style = Stroke(1.4f * u))
        val m = r.deflate(3 * u)
        drawRoundRect(oroC.copy(alpha = 0.55f), m.topLeft, m.size, CornerRadius(m.height / 2f), style = Stroke(0.5f * u))
        // La libélula, a la izquierda.
        val lc = Offset(w * 0.08f, h / 2)
        with(TrazosNouveau) { libelula(lc, h * 0.62f, -90f, if (e.enfocado) 0f else 0.7f, luz(LIBELULA, oscuro, false, 0.7f), luz(NACAR, oscuro, false, 0.8f), oroC) }
        // El capullo de iris, a la derecha, que se abre con el foco.
        val ic = Offset(w * 0.91f, h * 0.78f)
        val abre = if (e.enfocado) 1f else 0f
        hojita(Offset(ic.x, ic.y), Offset(ic.x - 9 * u, ic.y - 6 * u), 3 * u, luz(TALLO_SOMBRA, oscuro, false, 0.6f), 0.2f)
        hojita(Offset(ic.x, ic.y), Offset(ic.x + 9 * u, ic.y - 4 * u), 3 * u, luz(TALLO_SOMBRA, oscuro, false, 0.6f), -0.2f)
        val flor = luz(IRIS, oscuro, false, if (e.enfocado) 0.9f else 0.5f)
        for (j in -1..1) {
            val ang = -PI.toFloat() / 2 + j * (0.15f + 0.45f * abre)
            val punta = Offset(ic.x + cos(ang) * h * 0.42f, ic.y - 2 * u + sin(ang) * h * 0.42f)
            drawPath(TrazosNouveau.petalo(Offset(ic.x, ic.y - 2 * u), punta, h * (0.16f + 0.12f * abre)), if (j == 0) mezcla(flor, Color.White, 0.2f) else flor)
        }
        if (e.enfocado) {
            drawLine(TrazosNouveau.IRIS_LENGUA, Offset(ic.x, ic.y - 4 * u), Offset(ic.x, ic.y - h * 0.22f), 1.4f * u, StrokeCap.Round)
            mancha(Offset(ic.x, ic.y - h * 0.2f), h * 0.6f, LUZ_HALO.copy(alpha = 0.35f))
        }
        if (e.vacio) aIzquierda(medidor, e.pista, Rect(w * cajaCampo.left, h * cajaCampo.top, w * cajaCampo.right, h * cajaCampo.bottom), TEXTO,
            tinta(oscuro).copy(alpha = if (oscuro) 0.6f else 0.45f), 14f, minimo = 9f)
    }

    // ---------------------------------------------------------------- las obras

    /**
     * Las obras se apoyan en la vitrina del salón: el fondo de nogal, arriba una franja de vitral con su iris y, a la derecha, el
     * jarrón de Nancy; la repisa es una tabla de vidrio con su canto verde, sobre dos ménsulas talladas en látigo. De día entra
     * la luz de la ventana por la izquierda; de noche, la lámpara alumbra desde arriba.
     */
    override fun DrawScope.repisaObras(medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        nogal(Rect(0f, 0f, w, h), luz(FONDO_MUEBLE, oscuro, false, 0.3f), 301, vertical = true)
        val franja = Rect(0f, 0f, w, h * 0.16f)
        vitral(franja, 303, if (oscuro) 0.75f else 0f, false)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, franja.bottom), Size(w, 3 * u))
        nogal(Rect(0f, franja.bottom, w, franja.bottom + 6 * u), luz(NOGAL, oscuro, false, 0.5f), 305)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent), franja.bottom + 6 * u, franja.bottom + 30 * u), Offset(0f, franja.bottom + 6 * u), Size(w, 24 * u))
        // El jarrón, a la derecha, sobre la tabla de vidrio.
        val y0 = h - 22 * u
        jarron(Offset(w * 0.9f, y0), h * 0.32f, oscuro, false, 0.6f, flor = true)
        // La tabla de vidrio, con su canto verde, y las ménsulas.
        listOf(w * 0.12f, w * 0.88f).forEachIndexed { i, x ->
            val s = if (i == 0) 1f else -1f
            val pts = TrazosNouveau.puntosLatigo(Offset(x, y0 + 3 * u), Offset(x + s * 3 * u, h - 2 * u), 3 * u, 0.3f * s, s > 0)
            tallado(Path().apply { pts.forEachIndexed { j, q -> if (j == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) } }, luz(NOGAL_LUZ, oscuro, false, 0.4f), 2.6f * u)
        }
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, y0 + 4 * u), Size(w, 3 * u))
        drawRect(luz(Color(0xFFB9D4C4), oscuro, false, 0.5f).copy(alpha = 0.55f), Offset(0f, y0 - 2 * u), Size(w, 4 * u))
        drawRect(luz(VERDE_VIDRIO, oscuro, false, 0.5f), Offset(0f, y0 + 2 * u), Size(w, 2.2f * u))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.15f else 0.5f), Offset(0f, y0 - 1.6f * u), Offset(w, y0 - 1.6f * u), 0.6f * u)
        if (oscuro) {
            resplandor(Offset(w * 0.5f, 0f), w * 0.6f, 0.8f)
        } else drawRect(Brush.horizontalGradient(listOf(Color(0xFFFFF6E0).copy(alpha = 0.18f), Color.Transparent, Color.Black.copy(alpha = 0.1f)), 0f, w), Offset.Zero, Size(w, h))
    }

    /** Un título partido por palabras en a lo más tres renglones que caben en [r]. */
    private fun DrawScope.tituloEn(medidor: TextMeasurer, titulo: String, r: Rect, letra: FontFamily, color: Color, tam: Float) {
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
            textoEn(medidor, s, Rect(r.left, y, r.right, y + alto), letra, color, tam, minimo = tam * 0.5f)
            y += alto
        }
    }

    /** Cuál de [n] variantes le toca a una obra según su [semilla], que puede venir negativa. */
    private fun variante(semilla: Int, n: Int): Int = Math.floorMod(semilla, n.coerceAtLeast(1))

    /**
     * La portada de una obra es la cubierta impresa de una entrega de la revista, como un cartel: el papel crema, un marco de
     * color con las esquinas de arriba redondeadas y dos tallos que suben por los lados y se enroscan arriba; dentro, el tipo
     * en la letra de los rótulos, el título en la del cartel, un iris al pie y, en una cartela, el autor y el año. En
     * miniatura, solo la inicial dentro de un arco.
     */
    override fun DrawScope.portadaObra(e: EstadoPortada, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = w / u
        fun c(x: Color) = luz(x, oscuro, false, 0.6f)
        val tinte = c(listOf(HIERRO, VIOLETA, Color(0xFF8A6A3A), Color(0xFF7E5A7A))[variante(e.semilla, 4)])
        val g = maxOf(0.5f * u, w * 0.006f)
        drawRect(c(PAPEL), Offset.Zero, Size(w, h))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, c(Color(0xFFC9B48A)).copy(alpha = 0.25f)), 0f, h), Offset.Zero, Size(w, h))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset.Zero, Size(w, h), style = Stroke(g))
        val m = Rect(w * 0.06f, h * 0.04f, w * 0.94f, h * 0.96f)
        val marco = Path().apply {
            moveTo(m.left, m.bottom); lineTo(m.left, m.top + m.width * 0.3f)
            quadraticTo(m.left, m.top, m.left + m.width * 0.3f, m.top)
            lineTo(m.right - m.width * 0.3f, m.top)
            quadraticTo(m.right, m.top, m.right, m.top + m.width * 0.3f)
            lineTo(m.right, m.bottom); close()
        }
        if (e.mini) {
            drawPath(marco, tinte, style = Stroke(maxOf(1f * u, w * 0.05f)))
            textoEn(medidor, e.titulo.trim().take(1).uppercase().ifEmpty { "·" }, m.deflate(w * 0.1f), TITULO, c(TINTA), s * 0.5f, minimo = 5f)
            return
        }
        drawPath(marco, tinte, style = Stroke(w * 0.03f))
        drawPath(marco, c(ORO), style = Stroke(g))
        // Los dos tallos de los lados, que se enroscan arriba.
        listOf(-1f, 1f).forEach { sg ->
            val x = if (sg < 0) m.left + w * 0.07f else m.right - w * 0.07f
            latigo(Offset(x, m.bottom - h * 0.03f), Offset(x + sg * w * 0.02f, m.top + h * 0.22f), w * 0.05f, maxOf(0.8f * u, w * 0.018f), c(TALLO_SOMBRA), 0.05f * sg, sg < 0, false)
        }
        val col = Rect(m.left + w * 0.14f, m.top + h * 0.05f, m.right - w * 0.14f, m.bottom - h * 0.04f)
        textoEn(medidor, e.tipo, Rect(col.left, col.top, col.right, col.top + h * 0.07f), ROTULO, tinte, s * 0.075f, espaciado = s * 0.012f, minimo = 3.5f)
        drawLine(tinte.copy(alpha = 0.6f), Offset(col.left + col.width * 0.2f, col.top + h * 0.085f), Offset(col.right - col.width * 0.2f, col.top + h * 0.085f), g)
        tituloEn(medidor, e.titulo, Rect(col.left, col.top + h * 0.1f, col.right, col.top + h * 0.42f), TITULO, c(TINTA), s * 0.12f)
        with(TrazosNouveau) { iris(Offset(col.center.x, col.top + h * 0.72f), h * 0.26f, c(IRIS), c(TALLO), c(TALLO_SOMBRA)) }
        val pie = listOf(e.autor, e.anio).filter { it.isNotBlank() }.joinToString(" · ")
        if (pie.isNotEmpty()) {
            val ca = Rect(col.left - w * 0.04f, col.top + h * 0.74f, col.right + w * 0.04f, col.top + h * 0.85f)
            drawRoundRect(tinte, ca.topLeft, ca.size, CornerRadius(ca.height / 2f))
            textoEn(medidor, pie, ca.deflate(w * 0.03f), TEXTO, c(PAPEL_CLARO), s * 0.065f, minimo = 3.5f)
        }
    }

    // ---------------------------------------------------------------- los recuerdos

    override val margenRecuerdo: Dp = 14.dp
    override val altoRotuloRecuerdo: Dp = 48.dp
    override fun tintaRecuerdo(oscuro: Boolean): Color = tinta(oscuro)

    /** «Hace un año» o «Hace N años». */
    private fun haceAnios(n: Int): String = if (n == 1) "Hace un año" else "Hace $n años"

    /** El clima sin su signo (☀ ☂ ❄), que las letras del tema no traen. */
    private fun sinSigno(clima: String): String = clima.dropWhile { !it.isLetterOrDigit() }.trim()

    /**
     * El recuerdo es una hoja de herbario de salón, guardada entre las páginas de un tomo: el papel crema amarilleado en las
     * orillas, la flor prensada (un iris, con sus hojas) sujeta con dos tiras de papel engomado, y en la cabecera, la etiqueta
     * oval con los años, «Hace N años» en la letra del cartel y la fecha del almanaque con el clima. Bajo la cabecera, un
     * filete con látigos; a la derecha, la cinta de seda del color del ánimo.
     */
    override fun DrawScope.recuerdo(e: EstadoRecuerdo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val fondo = papel(oscuro, false, 0.45f)
        val tinta = tintaRecuerdo(oscuro)
        val acento = oro(oscuro)
        val cab = altoRotuloRecuerdo.toPx()
        val forma = Path().apply { addRoundRect(RoundRect(Rect(0f, 0f, w, h), CornerRadius(8 * u))) }
        clipPath(forma) {
            drawRect(fondo)
            drawRect(Brush.radialGradient(0f to Color.Transparent, 0.65f to Color.Transparent, 1f to Color(0xFFB89A62).copy(alpha = if (oscuro) 0.15f else 0.28f),
                center = Offset(w / 2, h / 2), radius = maxOf(w, h) * 0.7f))
            for (k in 0 until 6) drawCircle(Color(0xFFB89A62).copy(alpha = 0.08f), (4f + 8f * azar(e.semilla, k)) * u, Offset(w * azar(e.semilla, k + 5), h * azar(e.semilla, k + 11)))
            // La flor prensada, abajo a la derecha, con sus tiras de papel engomado.
            if (h > cab + 40 * u) {
                val pie = Offset(w - 46 * u, h - 6 * u)
                val alto = (h - cab - 14 * u).coerceAtMost(90 * u)
                with(TrazosNouveau) {
                    iris(pie, alto, mezcla(luz(IRIS, oscuro, false, 0.5f), fondo, 0.35f), mezcla(luz(TALLO, oscuro, false, 0.5f), fondo, 0.4f), mezcla(luz(TALLO_SOMBRA, oscuro, false, 0.5f), fondo, 0.35f))
                }
                listOf(Offset(pie.x - 4 * u, pie.y - alto * 0.3f), Offset(pie.x + 2 * u, pie.y - alto * 0.62f)).forEachIndexed { i, q ->
                    rotate(if (i == 0) -12f else 10f, q) { drawRect(mezcla(fondo, Color.White, 0.3f).copy(alpha = 0.85f), Offset(q.x - 9 * u, q.y - 2.5f * u), Size(18 * u, 5 * u)) }
                }
            }
        }
        drawPath(forma, Color.Black.copy(alpha = 0.25f), style = Stroke(1f * u))
        // El filete bajo la cabecera.
        drawLine(acento.copy(alpha = 0.55f), Offset(14 * u, cab - 1 * u), Offset(w - 44 * u, cab - 1 * u), 0.7f * u)
        latigo(Offset(w - 44 * u, cab - 1 * u), Offset(w - 34 * u, cab - 5 * u), 2.4f * u, 1 * u, acento.copy(alpha = 0.7f), -0.2f, true, false)
        // La etiqueta oval con los años.
        val tc = Offset(27 * u, cab / 2f + 1 * u)
        drawOval(Color.Black.copy(alpha = 0.15f), Offset(tc.x - 15 * u, tc.y - 11 * u) + Offset(0.8f * u, 1.2f * u), Size(30 * u, 22 * u))
        drawOval(mezcla(fondo, Color.White, 0.3f), Offset(tc.x - 15 * u, tc.y - 11 * u), Size(30 * u, 22 * u))
        drawOval(acento, Offset(tc.x - 15 * u, tc.y - 11 * u), Size(30 * u, 22 * u), style = Stroke(1.2f * u))
        drawOval(acento.copy(alpha = 0.5f), Offset(tc.x - 12.5f * u, tc.y - 8.5f * u), Size(25 * u, 17 * u), style = Stroke(0.5f * u))
        textoEn(medidor, "${e.anios.coerceAtLeast(1)}", Rect(tc.x - 10 * u, tc.y - 8 * u, tc.x + 10 * u, tc.y + 8 * u), TITULO, tinta, 13f, minimo = 6f)
        val col = Rect(50 * u, 6 * u, w - 32 * u, cab - 6 * u)
        aIzquierda(medidor, haceAnios(e.anios), Rect(col.left, col.top, col.right, col.top + col.height * 0.5f), TITULO, acento, 13f, minimo = 6f)
        val clima = sinSigno(e.clima)
        aIzquierda(medidor, if (clima.isEmpty()) e.fecha else e.fecha + " · " + clima, Rect(col.left, col.top + col.height * 0.5f, col.right, col.bottom), TEXTO, tinta, 10.5f, minimo = 6.5f)
        // La cinta de seda del ánimo, que cuelga del canto de arriba.
        val seda = (e.animo ?: Color(0xFFE6DCC2)).let { if (oscuro) mezcla(it, NOCHE, 0.25f) else it }
        val x = w - 18 * u
        val p = Path().apply { moveTo(x - 2.6f * u, 0f); lineTo(x + 2.6f * u, 0f); lineTo(x + 2.6f * u, cab * 0.7f); lineTo(x, cab * 0.7f - 3 * u); lineTo(x - 2.6f * u, cab * 0.7f); close() }
        translate(0.6f * u, 0.8f * u) { drawPath(p, Color.Black.copy(alpha = 0.25f)) }
        drawPath(p, seda)
        drawLine(Color.White.copy(alpha = 0.3f), Offset(x - 1 * u, 0f), Offset(x - 1 * u, cab * 0.6f), 0.4f * u)
    }
}
