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
import androidx.compose.ui.graphics.PathEffect
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
import io.github.ruddy2003.vestigium.ui.ideario.sombraSuave
import io.github.ruddy2003.vestigium.ui.ideario.vetas
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
 * Art nouveau: la biblioteca del salón del taller de litografía, un mueble de la época de nogal tallado como los de Nancy.
 * Arriba, la pared con su papel pintado de tallos; el copete del mueble se curva en látigo y se enrosca a los lados, con un
 * iris tallado en medio; debajo, el vitral de casa (no de catedral): un iris y una libélula de vidrio de colores que pasa del
 * ámbar al violeta, emplomado con curvas de látigo; sobre el copete, un jarrón de vidrio de Nancy y la lámpara de hongo de
 * vidrio de colores. Los costados son dos tallos tallados que suben desde los pies, echan hojas y rematan en un capullo.
 * Las tablas llevan el canto moldurado y cada mes va en su portaetiquetas de latón, con la tarjeta impresa. En las repisas, lo
 * del taller: el jarrón de vidrio grabado con su flor, las pruebas de color enrolladas y atadas, el frasco de perfume con el
 * peine de carey. Abajo, el zócalo tallado con sus tiradores en forma de hoja, los pies que echan raíces y el parqué del
 * salón con la orilla de la alfombra. Cada entrada es un tomo de la revista ilustrada: encuadernado en tela con su tejuelo y
 * sus nervios dorados, el cuaderno suelto con su cubierta impresa o el álbum de lomo verde con el látigo de oro; la cinta de
 * seda del registro es del color del ánimo. De día entra la luz difusa de la ventana del taller, por la izquierda; de noche
 * es el mismo mueble en el azul negro de la calle, y la luz cálida de la lámpara de vidrio solo alcanza lo que tiene cerca.
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
    private val CAREY = TrazosNouveau.CAREY
    private val CAREY_MANCHA = TrazosNouveau.CAREY_MANCHA
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

    /** El mes para un lomo: solo su nombre («Almanach de novembre» se vuelve «novembre»). */
    private fun mesCorto(mes: String): String = mes.substringAfter("Almanach ").removePrefix("de ").removePrefix("d'").substringBefore(" ").trim()

    /** Cuál de los tres tomos es una entrada: 0, el tomo encuadernado en tela; 1, el cuaderno suelto; 2, el álbum de lomo verde. */
    private fun clase(semilla: Int): Int {
        val k = Math.floorMod(semilla xor (semilla ushr 7), 7)
        return if (k < 4) 0 else if (k < 6) 1 else 2
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
     * La lámpara de hongo de vidrio de Nancy, de pie en [pie] y de [alto]: el pie de vidrio violeta que se funde en ámbar, el
     * fuste y la pantalla en forma de hongo, con un iris grabado. Encendida ([encendida]), la pantalla se llena de luz cálida.
     */
    private fun DrawScope.lampara(pie: Offset, alto: Float, encendida: Boolean, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = if (encendida) x else t(x, oscuro, noche)
        val an = alto * 0.62f
        // El pie, abombado, y el fuste.
        val baseL = Path().apply {
            moveTo(pie.x - an * 0.36f, pie.y)
            cubicTo(pie.x - an * 0.36f, pie.y - alto * 0.12f, pie.x - an * 0.1f, pie.y - alto * 0.16f, pie.x - an * 0.07f, pie.y - alto * 0.5f)
            lineTo(pie.x + an * 0.07f, pie.y - alto * 0.5f)
            cubicTo(pie.x + an * 0.1f, pie.y - alto * 0.16f, pie.x + an * 0.36f, pie.y - alto * 0.12f, pie.x + an * 0.36f, pie.y)
            close()
        }
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(pie.x - an * 0.42f, pie.y - alto * 0.03f), Size(an * 0.84f, alto * 0.07f))
        val vidrioPie = if (oscuro && !encendida) t(VIOLETA, true, noche) else mezcla(VIOLETA, if (encendida) LUZ else VIOLETA, if (encendida) 0.25f else 0f)
        drawPath(baseL, Brush.verticalGradient(listOf(c(AMBAR), vidrioPie), pie.y - alto * 0.5f, pie.y))
        drawPath(baseL, Color.Black.copy(alpha = 0.25f), style = Stroke(0.6f * u))
        // La pantalla de hongo.
        val top = pie.y - alto
        val pantalla = Path().apply {
            moveTo(pie.x - an * 0.5f, pie.y - alto * 0.46f)
            cubicTo(pie.x - an * 0.52f, top + alto * 0.05f, pie.x + an * 0.52f, top + alto * 0.05f, pie.x + an * 0.5f, pie.y - alto * 0.46f)
            quadraticTo(pie.x, pie.y - alto * 0.52f, pie.x - an * 0.5f, pie.y - alto * 0.46f)
            close()
        }
        if (encendida) {
            mancha(Offset(pie.x, pie.y - alto * 0.62f), alto * 1.2f, LUZ_HALO.copy(alpha = 0.35f))
            drawPath(pantalla, Brush.radialGradient(listOf(Color(0xFFFFF4D2), LUZ, AMBAR, mezcla(AMBAR, VIOLETA, 0.5f)), center = Offset(pie.x, pie.y - alto * 0.55f), radius = an * 0.7f))
        } else {
            drawPath(pantalla, Brush.verticalGradient(listOf(c(mezcla(VIOLETA, MALVA, 0.3f)), c(AMBAR)), top, pie.y - alto * 0.46f))
        }
        // El iris grabado en la pantalla, más oscuro.
        clipPath(pantalla) {
            val k = if (encendida) 0.5f else 0.35f
            with(TrazosNouveau) {
                iris(Offset(pie.x - an * 0.18f, pie.y - alto * 0.4f), alto * 0.5f, mezcla(VIOLETA, Color.Black, 0.2f).copy(alpha = k), mezcla(HIERRO, Color.Black, 0.1f).copy(alpha = k), mezcla(HIERRO, Color.Black, 0.3f).copy(alpha = k))
            }
            drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.25f), Color.Transparent, Color.Black.copy(alpha = 0.25f)), pie.x - an * 0.5f, pie.x + an * 0.5f), Offset(pie.x - an * 0.5f, top), Size(an, alto * 0.6f))
        }
        drawPath(pantalla, Color.Black.copy(alpha = 0.3f), style = Stroke(0.7f * u))
        // La luz que cae por debajo de la pantalla.
        if (encendida) drawOval(LUZ_HALO.copy(alpha = 0.5f), Offset(pie.x - an * 0.36f, pie.y - alto * 0.5f), Size(an * 0.72f, alto * 0.08f))
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
    //  El mueble: la biblioteca de nogal del salón
    // =================================================================

    override val corona = 0.5f
    override val base = 0.15f
    override val lado = 0.07f
    override val altoRepisa: Dp = 108.dp
    override val tabla: Dp = 15.dp
    override val separa: Dp = 2.dp
    override val letra: FontFamily = TITULO

    override fun ancho(palabras: Int): Dp = (20 + (palabras / 45).coerceAtMost(16)).dp
    override fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.82f + 0.1f * azar(semilla, 4)
        1 -> 0.7f + 0.08f * azar(semilla, 4)
        else -> 0.9f + 0.08f * azar(semilla, 4)
    }

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        val lumbre = oscuro || noche
        val lampara = corona(d.corona, w * lado, oscuro, noche)
        // El fondo de las repisas: nogal oscuro, con la sombra de la tabla de arriba y, en la punta de la derecha, lo del taller.
        val fondo = Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, h - d.base.height)
        nogal(fondo, luz(FONDO_MUEBLE, oscuro, noche, 0.2f), 31, vertical = true)
        d.repisas.forEachIndexed { k, r ->
            clipRect(r.left, r.top, r.right, r.bottom) {
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (lumbre) 0.55f else 0.42f), Color.Transparent), r.top, r.top + 30 * u), r.topLeft, Size(r.width, 30 * u))
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), r.left, r.left + 14 * u), r.topLeft, Size(14 * u, r.height))
                drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f)), r.right - 14 * u, r.right), Offset(r.right - 14 * u, r.top), Size(14 * u, r.height))
                val cerca = (1f - k * 0.3f).coerceIn(0.15f, 0.7f)
                when (k % 3) {
                    0 -> jarron(Offset(r.right - 22 * u, r.bottom), 46 * u, oscuro, noche, cerca)
                    1 -> pruebas(Offset(r.right - 24 * u, r.bottom), r.height * 0.72f, oscuro, noche, cerca)
                    else -> tocador(Offset(r.right - 26 * u, r.bottom), oscuro, noche, cerca)
                }
            }
        }
        // Los costados: dos tallos tallados en el nogal, que suben desde los pies, echan hojas y rematan en un capullo.
        listOf(d.izquierda, d.derecha).forEachIndexed { j, p0 ->
            val p = Rect(p0.left, d.corona.bottom - d.corona.height * 0.62f, p0.right, h - d.base.height * 0.45f)
            costado(p, j == 1, oscuro, noche)
        }
        // Las tablas: nogal, con el canto moldurado (un listel claro y una media caña en sombra).
        d.tablas.forEachIndexed { k, r ->
            val q = Rect(r.left - 2 * u, r.top, r.right + 2 * u, r.bottom)
            nogal(q, luz(NOGAL, oscuro, noche, 0.3f), 71 + k * 5)
            drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (lumbre) 0.05f else 0.22f), Color.Transparent, Color.Black.copy(alpha = 0.3f)), q.top, q.bottom), q.topLeft, q.size)
            drawLine(mezcla(luz(NOGAL_LUZ, oscuro, noche, 0.3f), Color.White, 0.2f).copy(alpha = 0.6f), Offset(q.left, q.top + 2.5f * u), Offset(q.right, q.top + 2.5f * u), 0.7f * u)
            drawLine(Color.Black.copy(alpha = 0.35f), Offset(q.left, q.bottom - 3 * u), Offset(q.right, q.bottom - 3 * u), 0.8f * u)
            drawRect(Color.Black.copy(alpha = 0.36f), Offset(q.left, q.bottom), Size(q.width, 3.5f * u))
        }
        // El rótulo de cada mes: un portaetiquetas de latón con las puntas en látigo y la tarjeta impresa dentro.
        d.placas.forEach { p -> portaetiqueta(p.r, p.texto, medidor, oscuro, noche) }
        base(d.base, w * lado, oscuro, noche)
        // La luz: de día entra difusa por la ventana del taller, a la izquierda; de noche, solo la lámpara de vidrio alumbra, y lo
        // demás se queda en el azul negro de la calle.
        if (!lumbre) {
            drawRect(Brush.horizontalGradient(listOf(Color(0xFFFFF6E0).copy(alpha = 0.16f), Color.Transparent, Color.Black.copy(alpha = 0.1f)), 0f, w), Offset.Zero, Size(w, h))
        } else {
            resplandor(lampara, w * 0.62f, if (oscuro) 1f else 0.6f)
            drawRect(Brush.radialGradient(0f to Color.Transparent, 0.3f to Color.Transparent, 1f to (if (oscuro) NOCHE else ANOCHECER).copy(alpha = if (oscuro) 0.6f else 0.3f),
                center = lampara, radius = h * 0.95f), Offset.Zero, Size(w, h))
        }
    }

    /**
     * Un costado del mueble en [r]: el montante de nogal y, tallado en él, un tallo que sube ondulando desde el pie, echa hojas
     * alternas y remata arriba en un capullo; en el de la izquierda, una clavija de latón por repisa.
     */
    private fun DrawScope.costado(r: Rect, derecho: Boolean, oscuro: Boolean, noche: Boolean) {
        val u = density
        val madera = luz(NOGAL, oscuro, noche, if (derecho) 0.45f else 0.25f)
        nogal(r, madera, if (derecho) 41 else 43, vertical = true)
        drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.04f else 0.14f), Color.Transparent, Color.Black.copy(alpha = 0.4f)), r.left, r.right), r.topLeft, r.size)
        // El tallo tallado: sube ondulando por el centro del montante.
        val cx = r.center.x
        val amp = r.width * 0.16f
        val tallo = Path()
        val pasos = 60
        for (i in 0..pasos) {
            val f = i / pasos.toFloat()
            val y = r.bottom - r.height * f
            val x = cx + amp * sin(f * 9f + (if (derecho) 1.6f else 0f))
            if (i == 0) tallo.moveTo(x, y) else tallo.lineTo(x, y)
        }
        tallado(tallo, mezcla(madera, NOGAL_LUZ, 0.25f), maxOf(1.6f * u, r.width * 0.09f))
        // Las hojas, alternas, cada tanto.
        var f = 0.08f; var s = if (derecho) 1f else -1f
        while (f < 0.9f) {
            val y = r.bottom - r.height * f
            val x = cx + amp * sin(f * 9f + (if (derecho) 1.6f else 0f))
            val l = r.width * 0.42f
            relieve(TrazosNouveau.hojaLanza(Offset(x, y), Offset(x + s * l, y - l * 1.1f), l * 0.42f, 0.2f * s), mezcla(madera, NOGAL_LUZ, 0.25f), 1.2f * u)
            f += 0.07f; s = -s
        }
        // El capullo de arriba, tallado.
        val tope = Offset(cx + amp * sin(9f + (if (derecho) 1.6f else 0f)), r.top + r.width * 0.2f)
        relieve(TrazosNouveau.petalo(Offset(tope.x, tope.y + r.width * 0.5f), Offset(tope.x, tope.y - r.width * 0.25f), r.width * 0.55f), mezcla(madera, NOGAL_LUZ, 0.35f), 1.4f * u)
        drawRect(Color.Black.copy(alpha = 0.35f), r.topLeft, r.size, style = Stroke(0.6f * u))
    }

    /**
     * La corona: arriba, la pared del salón con su papel pintado; el copete de nogal se curva en látigo de un lado a otro, se
     * enrosca en las puntas y lleva un iris tallado en medio; sobre él, a la izquierda, el jarrón de Nancy con un iris de verdad y,
     * a la derecha, la lámpara de hongo (encendida de noche). Debajo del copete, el vitral de casa, y abajo la cornisa. Devuelve
     * dónde está la luz de la lámpara.
     */
    private fun DrawScope.corona(r: Rect, lado: Float, oscuro: Boolean, noche: Boolean): Offset {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        val lumbre = oscuro || noche
        val hh = r.height
        val izq = r.left + lado; val der = r.right - lado
        val cx = r.center.x
        // La pared y su papel pintado; de día, la luz de la ventana entra por la izquierda.
        papelPintado(r, c(PAPEL_PARED), c(TALLO_SOMBRA), 11)
        if (!lumbre) drawRect(Brush.horizontalGradient(listOf(Color(0xFFFFF8E8).copy(alpha = 0.3f), Color.Transparent, Color.Black.copy(alpha = 0.12f)), r.left, r.right), r.topLeft, r.size)
        // El copete: su borde de arriba se hunde en curva desde los costados y sube en una cúpula rebajada en el centro, como los
        // sombreretes de los muebles de Nancy.
        val hombro = r.top + hh * 0.36f
        val cima = r.top + hh * 0.12f
        val bajo = r.top + hh * 0.5f
        val anchoC = r.width * 0.19f
        fun borde(dy: Float, dx: Float): Path = Path().apply {
            moveTo(r.left + dx, hombro + hh * 0.07f + dy)
            cubicTo(r.left + r.width * 0.2f, hombro - hh * 0.02f + dy, cx - anchoC - r.width * 0.08f, hombro + hh * 0.03f + dy, cx - anchoC, cima + hh * 0.17f + dy)
            cubicTo(cx - anchoC * 0.85f, cima - hh * 0.03f + dy, cx + anchoC * 0.85f, cima - hh * 0.03f + dy, cx + anchoC, cima + hh * 0.17f + dy)
            cubicTo(cx + anchoC + r.width * 0.08f, hombro + hh * 0.03f + dy, r.right - r.width * 0.2f, hombro - hh * 0.02f + dy, r.right - dx, hombro + hh * 0.07f + dy)
        }
        val copete = borde(0f, 0f).apply { lineTo(r.right, r.bottom); lineTo(r.left, r.bottom); close() }
        drawPath(copete, Color.Black.copy(alpha = 0.25f), style = Stroke(5 * u))
        clipPath(copete) {
            nogal(Rect(r.left, cima, r.right, r.bottom), luz(NOGAL, oscuro, noche, 0.55f), 23)
            drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (lumbre) 0.05f else 0.18f), Color.Transparent), cima, bajo), Offset(r.left, cima), Size(r.width, bajo - cima))
        }
        // La moldura que sigue el borde del copete y se enrosca en las puntas, sobre los costados.
        val moldura = borde(hh * 0.055f, lado * 0.9f)
        listOf(-1f, 1f).forEach { s ->
            val x = if (s < 0) r.left + lado * 0.9f else r.right - lado * 0.9f
            val y = hombro + hh * 0.125f
            moldura.addArc(Rect(Offset(x + s * hh * 0.035f, y + hh * 0.035f), hh * 0.035f), if (s < 0) 180f else 0f, if (s < 0) 270f else -270f)
        }
        tallado(moldura, luz(NOGAL_LUZ, oscuro, noche, 0.55f), 2.4f * u)
        val talla = luz(mezcla(NOGAL, NOGAL_LUZ, 0.4f), oscuro, noche, 0.55f)
        listOf(-1f, 1f).forEach { s ->
            val desde = Offset(cx + s * hh * 0.18f, bajo - hh * 0.02f)
            val hasta = Offset(cx + s * (cx - izq) * 0.72f, hombro + hh * 0.12f)
            val pts = TrazosNouveau.puntosLatigo(desde, hasta, hh * 0.05f, 0.18f * s, s < 0)
            val p = Path().apply { pts.forEachIndexed { i, q -> if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) } }
            tallado(p, talla, 2.2f * u)
        }
        // El iris tallado en el centro del copete.
        with(TrazosNouveau) {
            val base0 = Offset(cx, bajo + hh * 0.02f)
            translate(0.8f * u, 1f * u) { iris(base0, hh * 0.36f, Color.Black.copy(alpha = 0.35f), Color.Black.copy(alpha = 0.35f), Color.Black.copy(alpha = 0.35f)) }
            iris(base0, hh * 0.36f, mezcla(talla, NOGAL_LUZ, 0.3f), talla, mezcla(talla, NOGAL_OSC, 0.3f))
        }
        // El vitral, en su marco de arco rebajado.
        val vit = Rect(izq + 6 * u, bajo + hh * 0.04f, der - 6 * u, r.bottom - hh * 0.1f)
        val arco = Path().apply {
            moveTo(vit.left, vit.bottom); lineTo(vit.left, vit.top + vit.height * 0.25f)
            quadraticTo(vit.left, vit.top, vit.left + vit.width * 0.12f, vit.top)
            lineTo(vit.right - vit.width * 0.12f, vit.top)
            quadraticTo(vit.right, vit.top, vit.right, vit.top + vit.height * 0.25f)
            lineTo(vit.right, vit.bottom); close()
        }
        drawPath(arco, Color.Black.copy(alpha = 0.4f), style = Stroke(5 * u))
        clipPath(arco) { vitral(vit, 7, if (oscuro) 0.75f else if (noche) 0.35f else 0f, noche) }
        tallado(arco, luz(NOGAL_LUZ, oscuro, noche, 0.5f), 2.6f * u)
        // La cornisa: el canto de abajo de la corona, sobre la primera repisa.
        val cornisa = Rect(r.left, r.bottom - hh * 0.07f, r.right, r.bottom)
        nogal(cornisa, luz(NOGAL, oscuro, noche, 0.45f), 29)
        drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (lumbre) 0.06f else 0.24f), Color.Transparent, Color.Black.copy(alpha = 0.35f)), cornisa.top, cornisa.bottom), cornisa.topLeft, cornisa.size)
        // Sobre el copete: el jarrón con su iris a la izquierda y la lámpara a la derecha.
        val yHombroIzq = hombro - hh * 0.005f
        jarron(Offset(r.left + (cx - r.left) * 0.42f, yHombroIzq - hh * 0.02f), hh * 0.2f, oscuro, noche, 0.5f, flor = true)
        val pieL = Offset(r.right - (r.right - cx) * 0.42f, yHombroIzq - hh * 0.02f)
        lampara(pieL, hh * 0.34f, lumbre, oscuro, noche)
        return Offset(pieL.x, pieL.y - hh * 0.2f)
    }

    /**
     * La base: el zócalo de nogal con un panel tallado en látigo y dos tiradores de latón en forma de hoja; el faldón, que se
     * curva entre los pies; los pies, que echan raíces; y abajo el parqué del salón, en espiga, con la orilla de la alfombra.
     */
    private fun DrawScope.base(b: Rect, lado: Float, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        val lumbre = oscuro || noche
        val zocalo = Rect(b.left, b.top, b.right, b.top + b.height * 0.56f)
        val suelo = Rect(b.left, zocalo.bottom - b.height * 0.06f, b.right, b.bottom)
        // El parqué en espiga y la orilla de la alfombra.
        val parque = c(Color(0xFF8A6440))
        drawRect(parque, suelo.topLeft, suelo.size)
        clipRect(suelo.left, suelo.top, suelo.right, suelo.bottom) {
            val l = 14 * u; val a = 4 * u
            var x = suelo.left - l; var k = 0
            while (x < suelo.right + l) {
                var y = suelo.top - l; var j = 0
                while (y < suelo.bottom + l) {
                    val tono = mezcla(parque, if ((k + j) % 2 == 0) Color.Black else NOGAL_LUZ, 0.12f + 0.08f * azar(k, j))
                    rotate(if (j % 2 == 0) 45f else -45f, Offset(x, y)) { drawRect(tono, Offset(x - l / 2, y - a / 2), Size(l, a)); drawRect(Color.Black.copy(alpha = 0.18f), Offset(x - l / 2, y - a / 2), Size(l, a), style = Stroke(0.4f * u)) }
                    y += a * 2.2f; j++
                }
                x += l * 0.72f; k++
            }
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.45f), Color.Transparent), suelo.top, suelo.top + 8 * u), suelo.topLeft, Size(suelo.width, 8 * u))
        }
        val alfombra = Rect(b.left + b.width * 0.12f, b.bottom - suelo.height * 0.34f, b.right - b.width * 0.12f, b.bottom)
        drawRect(c(Color(0xFF4E3A5C)), alfombra.topLeft, alfombra.size)
        drawRect(c(ORO).copy(alpha = 0.7f), Offset(alfombra.left, alfombra.top + 2 * u), Size(alfombra.width, 1 * u))
        var x0 = alfombra.left + 6 * u
        while (x0 < alfombra.right - 6 * u) {
            drawPath(TrazosNouveau.petalo(Offset(x0, alfombra.bottom - 1 * u), Offset(x0, alfombra.top + 4.5f * u), 4 * u), c(mezcla(MALVA, TALLO, 0.3f)).copy(alpha = 0.8f))
            x0 += 12 * u
        }
        // Los pies del mueble: el montante se ensancha y echa raíces sobre el parqué.
        listOf(b.left + lado * 0.5f, b.right - lado * 0.5f).forEachIndexed { j, px ->
            val s = if (j == 0) 1f else -1f
            val pie = Path().apply {
                moveTo(px - lado * 0.5f, zocalo.top)
                lineTo(px + lado * 0.5f, zocalo.top)
                cubicTo(px + lado * 0.5f, zocalo.bottom, px + lado * 0.9f, suelo.top + suelo.height * 0.3f, px + lado * 0.95f, suelo.top + suelo.height * 0.42f)
                lineTo(px - lado * 0.95f, suelo.top + suelo.height * 0.42f)
                cubicTo(px - lado * 0.9f, suelo.top + suelo.height * 0.3f, px - lado * 0.5f, zocalo.bottom, px - lado * 0.5f, zocalo.top)
                close()
            }
            drawOval(Color.Black.copy(alpha = 0.35f), Offset(px - lado * 1.1f, suelo.top + suelo.height * 0.34f), Size(lado * 2.2f, suelo.height * 0.18f))
            drawPath(pie, luz(NOGAL, oscuro, noche, 0.2f))
            drawPath(pie, Brush.horizontalGradient(listOf(Color.White.copy(alpha = if (lumbre) 0.04f else 0.14f), Color.Transparent, Color.Black.copy(alpha = 0.4f)), px - lado, px + lado))
            drawPath(pie, Color.Black.copy(alpha = 0.35f), style = Stroke(0.6f * u))
            tallado(Path().apply { moveTo(px, zocalo.top + 4 * u); quadraticTo(px + s * lado * 0.2f, zocalo.bottom, px - s * lado * 0.6f, suelo.top + suelo.height * 0.36f) },
                luz(NOGAL_LUZ, oscuro, noche, 0.2f), 1.2f * u)
        }
        // El zócalo, con el faldón que se curva entre los pies.
        val faldon = Path().apply {
            moveTo(b.left + lado, zocalo.top); lineTo(b.right - lado, zocalo.top); lineTo(b.right - lado, zocalo.bottom - zocalo.height * 0.1f)
            cubicTo(b.right - lado - b.width * 0.18f, zocalo.bottom - zocalo.height * 0.1f, b.center.x + b.width * 0.12f, zocalo.bottom + zocalo.height * 0.06f, b.center.x, zocalo.bottom + zocalo.height * 0.1f)
            cubicTo(b.center.x - b.width * 0.12f, zocalo.bottom + zocalo.height * 0.06f, b.left + lado + b.width * 0.18f, zocalo.bottom - zocalo.height * 0.1f, b.left + lado, zocalo.bottom - zocalo.height * 0.1f)
            close()
        }
        drawPath(faldon, Color.Black.copy(alpha = 0.35f), style = Stroke(4 * u))
        clipPath(faldon) {
            nogal(Rect(b.left, zocalo.top, b.right, zocalo.bottom + zocalo.height * 0.2f), luz(NOGAL, oscuro, noche, 0.25f), 141)
            drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (lumbre) 0.05f else 0.22f), Color.Transparent, Color.Black.copy(alpha = 0.3f)), zocalo.top, zocalo.bottom), zocalo.topLeft, zocalo.size)
        }
        // El panel tallado: dos látigos que nacen del centro y se enroscan hacia los tiradores.
        val ty = zocalo.top + zocalo.height * 0.48f
        val talla = luz(mezcla(NOGAL, NOGAL_LUZ, 0.45f), oscuro, noche, 0.25f)
        listOf(-1f, 1f).forEach { s ->
            val pts = TrazosNouveau.puntosLatigo(Offset(b.center.x, ty + zocalo.height * 0.2f), Offset(b.center.x + s * b.width * 0.24f, ty - zocalo.height * 0.08f), zocalo.height * 0.15f, 0.2f * s, s > 0)
            tallado(Path().apply { pts.forEachIndexed { i, q -> if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) } }, talla, 1.8f * u)
            // El tirador de latón en forma de hoja.
            val tc = Offset(b.center.x + s * b.width * 0.36f, ty)
            val hoja = TrazosNouveau.hojaLanza(Offset(tc.x - s * 9 * u, tc.y + 2 * u), Offset(tc.x + s * 9 * u, tc.y - 2 * u), 6 * u, 0.1f)
            translate(0.8f * u, 1.2f * u) { drawPath(hoja, Color.Black.copy(alpha = 0.4f)) }
            drawPath(hoja, Brush.linearGradient(listOf(mezcla(luz(LATON, oscuro, noche, 0.3f), Color.White, 0.3f), luz(LATON, oscuro, noche, 0.3f), mezcla(luz(LATON, oscuro, noche, 0.3f), Color.Black, 0.3f)),
                Offset(tc.x, tc.y - 4 * u), Offset(tc.x, tc.y + 4 * u)))
            drawLine(mezcla(luz(LATON, oscuro, noche, 0.3f), Color.Black, 0.4f), Offset(tc.x - s * 7 * u, tc.y + 1.6f * u), Offset(tc.x + s * 7 * u, tc.y - 1.6f * u), 0.6f * u)
        }
        drawCircle(talla, 2.4f * u, Offset(b.center.x, ty + zocalo.height * 0.2f))
        // El canto de arriba del zócalo, moldurado.
        drawRect(Brush.verticalGradient(listOf(luz(NOGAL_LUZ, oscuro, noche, 0.3f), luz(NOGAL, oscuro, noche, 0.3f), Color.Black.copy(alpha = 0.4f)), zocalo.top, zocalo.top + 5 * u), Offset(b.left + lado * 0.4f, zocalo.top), Size(b.width - lado * 0.8f, 5 * u))
    }

    /**
     * El portaetiquetas de un mes, en [r]: el marco de latón, con las puntas enroscadas en látigo y dos tornillitos, y dentro la
     * tarjeta crema con el nombre del mes impreso.
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

    /**
     * Las pruebas de color del taller, enrolladas y atadas con un cordel, recostadas en la repisa: papel crema con la espiral
     * del rollo arriba y, en el borde, la franja de prueba de cada tinta.
     */
    private fun DrawScope.pruebas(pie: Offset, alto: Float, oscuro: Boolean, noche: Boolean, cerca: Float) {
        val u = density
        fun c(x: Color) = luz(x, oscuro, noche, cerca)
        val colores = listOf(PAPEL_CLARO, mezcla(PAPEL, PIEDRA, 0.35f), PAPEL)
        colores.forEachIndexed { k, col ->
            val x = pie.x - 10 * u + k * 7.5f * u
            val r = 4f * u
            val largo = alto * (0.92f + 0.08f * k % 2)
            rotate(-10f + k * 8f, Offset(x, pie.y)) {
                val top = pie.y - largo
                drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(x - r + 1.2f * u, top + 1 * u), Size(r * 2, largo), CornerRadius(r * 0.4f))
                drawRect(Brush.horizontalGradient(listOf(c(mezcla(col, Color(0xFF8A7A5A), 0.35f)), c(col), c(mezcla(col, Color.White, 0.2f)), c(mezcla(col, Color(0xFF8A7A5A), 0.45f))), x - r, x + r),
                    Offset(x - r, top), Size(r * 2, largo))
                // La franja de prueba de una tinta, cerca del borde de la hoja.
                drawRect(c(listOf(TrazosNouveau.TINTA_ROJA, TrazosNouveau.TINTA_AZUL, IRIS)[k]).copy(alpha = 0.55f), Offset(x + r * 0.35f, top + largo * 0.05f), Size(r * 0.35f, largo * 0.5f))
                // La boca del rollo: el óvalo y la espiral del papel.
                drawOval(c(mezcla(col, Color(0xFF8A7A5A), 0.2f)), Offset(x - r, top - r * 0.45f), Size(r * 2, r * 0.9f))
                drawArc(c(mezcla(col, Color.Black, 0.35f)), 0f, 300f, false, Offset(x - r * 0.65f, top - r * 0.3f), Size(r * 1.3f, r * 0.6f), style = Stroke(0.5f * u))
                drawArc(c(mezcla(col, Color.Black, 0.35f)), 40f, 280f, false, Offset(x - r * 0.35f, top - r * 0.16f), Size(r * 0.7f, r * 0.32f), style = Stroke(0.5f * u))
                // La punta de la hoja que se suelta del rollo.
                drawPath(Path().apply { moveTo(x + r, top + largo * 0.12f); quadraticTo(x + r * 2.2f, top + largo * 0.1f, x + r * 1.9f, top + largo * 0.32f); lineTo(x + r, top + largo * 0.3f); close() },
                    c(mezcla(col, Color(0xFF8A7A5A), 0.25f)))
                // El cordel.
                drawLine(c(Color(0xFF8A6A44)), Offset(x - r, pie.y - largo * 0.42f), Offset(x + r, pie.y - largo * 0.38f), 0.9f * u)
                drawLine(c(Color(0xFF8A6A44)), Offset(x - r, pie.y - largo * 0.38f), Offset(x + r, pie.y - largo * 0.34f), 0.9f * u)
            }
        }
    }

    /** En la repisa de abajo, el frasco de perfume de vidrio tallado con su tapón en forma de flor y, delante, un peine de carey. */
    private fun DrawScope.tocador(pie: Offset, oscuro: Boolean, noche: Boolean, cerca: Float) {
        val u = density
        fun c(x: Color) = luz(x, oscuro, noche, cerca)
        val f = Offset(pie.x + 6 * u, pie.y)
        val cuerpo = Path().apply {
            moveTo(f.x - 9 * u, f.y); quadraticTo(f.x - 12 * u, f.y - 12 * u, f.x - 6 * u, f.y - 20 * u); lineTo(f.x + 6 * u, f.y - 20 * u)
            quadraticTo(f.x + 12 * u, f.y - 12 * u, f.x + 9 * u, f.y); close()
        }
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(f.x - 10 * u, f.y - 1.5f * u), Size(20 * u, 3 * u))
        drawPath(cuerpo, Brush.verticalGradient(listOf(c(mezcla(VERDE_VIDRIO, Color.White, 0.3f)), c(mezcla(AMBAR, MALVA, 0.4f))), f.y - 20 * u, f.y))
        drawPath(cuerpo, Color.White.copy(alpha = if (oscuro) 0.08f else 0.25f), style = Stroke(0.6f * u))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.15f else 0.5f), Offset(f.x - 6 * u, f.y - 16 * u), Offset(f.x - 7 * u, f.y - 5 * u), 1 * u, StrokeCap.Round)
        drawRect(c(ORO), Offset(f.x - 2.5f * u, f.y - 23 * u), Size(5 * u, 3 * u))
        for (j in 0 until 3) drawPath(TrazosNouveau.petalo(Offset(f.x, f.y - 23 * u), Offset(f.x + (j - 1) * 5 * u, f.y - 31 * u), 5 * u), c(mezcla(VIOLETA, MALVA, 0.4f + 0.2f * j)))
        // El peine de carey, recostado delante.
        val pc = Offset(pie.x - 14 * u, pie.y - 2 * u)
        rotate(-6f, pc) {
            val lomo = Path().apply { moveTo(pc.x - 11 * u, pc.y - 4 * u); quadraticTo(pc.x, pc.y - 12 * u, pc.x + 11 * u, pc.y - 4 * u); lineTo(pc.x + 11 * u, pc.y - 2 * u); lineTo(pc.x - 11 * u, pc.y - 2 * u); close() }
            drawPath(lomo, c(CAREY))
            for (k in 0 until 5) drawCircle(c(CAREY_MANCHA).copy(alpha = 0.7f), (0.8f + 0.8f * azar(5, k)) * u, Offset(pc.x - 8 * u + k * 4 * u, pc.y - 5 * u - 2 * u * azar(7, k)))
            var x = pc.x - 10 * u
            while (x < pc.x + 10.5f * u) { drawLine(c(CAREY), Offset(x, pc.y - 2.5f * u), Offset(x, pc.y + 2 * u), 0.8f * u); x += 1.8f * u }
        }
    }

    // =================================================================
    //  Cada entrada: un tomo de la revista ilustrada
    // =================================================================

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        when (clase(v.semilla)) {
            0 -> tomo(v, medidor, oscuro)
            1 -> cuaderno(v, medidor, oscuro)
            else -> album(v, medidor, oscuro)
        }
        // De noche, la lámpara alumbra los lomos desde arriba a la derecha.
        if (v.noche || oscuro) mancha(Offset(size.width * 0.8f, -size.height * 0.2f), size.width * 1.6f, LUZ_HALO.copy(alpha = if (oscuro) 0.12f else 0.08f))
    }

    /** El lomo redondo de un tomo dentro de [r]: la tela con su luz (más clara a un tercio, en sombra en los cantos). */
    private fun DrawScope.lomo(r: Rect, color: Color, semilla: Int) {
        tela(r, color, semilla)
        drawRect(Brush.horizontalGradient(0f to Color.Black.copy(alpha = 0.38f), 0.3f to Color.White.copy(alpha = 0.12f), 0.55f to Color.Transparent, 1f to Color.Black.copy(alpha = 0.42f),
            startX = r.left, endX = r.right), r.topLeft, r.size)
    }

    /** Un nervio del lomo a la altura [y]: el realce de la tela con su sombra y su luz, y un filete de oro. */
    private fun DrawScope.nervio(x0: Float, x1: Float, y: Float, oro: Color) {
        val u = density
        drawLine(Color.Black.copy(alpha = 0.35f), Offset(x0, y + 1.2f * u), Offset(x1, y + 1.2f * u), 1.2f * u)
        drawLine(Color.White.copy(alpha = 0.18f), Offset(x0, y - 1 * u), Offset(x1, y - 1 * u), 0.8f * u)
        drawLine(oro, Offset(x0 + 1 * u, y), Offset(x1 - 1 * u, y), 0.7f * u)
    }

    /** La cinta de seda del registro, del color del ánimo (de seda cruda, si no lo hay), que asoma por arriba y cae sobre el lomo. */
    private fun DrawScope.cinta(x: Float, largo: Float, tono: Color?, oscuro: Boolean, semilla: Int) {
        val u = density
        val col = (tono ?: Color(0xFFE6DCC2)).let { if (oscuro) mezcla(it, NOCHE, 0.3f) else it }
        val a = 2.6f * u
        val p = Path().apply {
            moveTo(x - a / 2, -1 * u); cubicTo(x - a / 2 - 3 * u, 4 * u, x + 2 * u * (azar(semilla, 3) - 0.5f) - a / 2, largo * 0.6f, x - a / 2, largo)
            lineTo(x, largo - a * 0.8f); lineTo(x + a / 2, largo)
            cubicTo(x + 2 * u * (azar(semilla, 3) - 0.5f) + a / 2, largo * 0.6f, x + a / 2 - 3 * u, 4 * u, x + a / 2, -1 * u); close()
        }
        translate(0.6f * u, 0.8f * u) { drawPath(p, Color.Black.copy(alpha = 0.3f)) }
        drawPath(p, col)
        drawLine(Color.White.copy(alpha = 0.3f), Offset(x - a * 0.2f, 0f), Offset(x - a * 0.2f, largo * 0.8f), 0.4f * u)
    }

    /**
     * El tomo encuadernado en tela: el lomo redondo con la cofia arriba y abajo y la cabezada; el tejuelo de otro color con el día
     * en oro; cuatro nervios con su filete; el mes en oro de arriba abajo; el floron al pie (de noche, la media luna); y la cinta
     * del registro del color del ánimo, que asoma y cae sobre el lomo.
     */
    private fun DrawScope.tomo(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val i = Math.floorMod(v.semilla, TELAS.size)
        val telaC = luz(TELAS[i], oscuro, v.noche, 0.4f)
        val tejC = luz(TELAS[(i + 2) % TELAS.size].let { mezcla(it, NOGAL_OSC, 0.25f) }, oscuro, v.noche, 0.4f)
        val oroC = luz(ORO, oscuro, v.noche, 0.7f)
        val r = Rect(0f, 0f, w, h)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(1 * u, 1 * u), Size(w, h), CornerRadius(2 * u))
        clipPath(Path().apply { addRoundRect(RoundRect(r, CornerRadius(2.2f * u))) }) {
            lomo(r, telaC, v.semilla)
            // La cofia y la cabezada, arriba; la cofia de abajo.
            drawRect(mezcla(telaC, Color.Black, 0.25f), Offset(0f, 0f), Size(w, 3 * u))
            var x = 1.5f * u; var k = 0
            while (x < w - 1 * u) { drawRect(if (k % 2 == 0) (v.tono ?: PAPEL).let { if (oscuro) mezcla(it, NOCHE, 0.4f) else it } else PAPEL.copy(alpha = 0.7f), Offset(x, 3 * u), Size(1.3f * u, 1.4f * u)); x += 1.6f * u; k++ }
            drawRect(mezcla(telaC, Color.Black, 0.25f), Offset(0f, h - 3 * u), Size(w, 3 * u))
            // Los nervios.
            val ys = listOf(0.1f, 0.36f, 0.82f, 0.94f).map { it * h }
            ys.forEach { y -> nervio(0f, w, y, oroC) }
            // El tejuelo con el día.
            val tj = Rect(1.6f * u, ys[0] + 2.5f * u, w - 1.6f * u, ys[1] - 2.5f * u)
            drawRect(tejC, tj.topLeft, tj.size)
            drawRect(Brush.horizontalGradient(0f to Color.Black.copy(alpha = 0.3f), 0.3f to Color.White.copy(alpha = 0.1f), 1f to Color.Black.copy(alpha = 0.35f), startX = tj.left, endX = tj.right), tj.topLeft, tj.size)
            drawRect(oroC.copy(alpha = 0.8f), tj.topLeft + Offset(1 * u, 1 * u), Size(tj.width - 2 * u, tj.height - 2 * u), style = Stroke(0.5f * u))
            dorado(medidor, v.dia, tj.deflate(1.5f * u), TITULO, oroC, 15f, minimo = 6f)
            // El mes, de arriba abajo.
            val mes = Rect(1 * u, ys[1] + 3 * u, w - 1 * u, ys[2] - 3 * u)
            if (mes.height > 16 * u) dorado(medidor, mesCorto(v.mes), mes, ROTULO, oroC, 8f, espaciado = 0.6f, vertical = true, minimo = 4f)
            // El floron entre los dos nervios de abajo.
            floron(Offset(w / 2, (ys[2] + ys[3]) / 2), minOf(w * 0.2f, 3.6f * u), oroC, v.noche)
            // El roce de los cantos.
            for (n in 0 until 8) drawCircle(Color.White.copy(alpha = 0.08f), (0.5f + azar(v.semilla, n)) * u, Offset(if (n % 2 == 0) 0.8f * u else w - 0.8f * u, h * azar(v.semilla, n + 30)))
        }
        cinta(w * 0.7f, minOf(h * 0.16f, 16 * u), v.tono, oscuro, v.semilla)
    }

    /**
     * El cuaderno suelto de la revista, sin encuadernar: el lomo de papel crema, un poco amarillento y roto arriba, con la franja
     * de color de la cubierta impresa donde va el día, el mes impreso de arriba abajo y un iris chico al pie; asoma por arriba un
     * papelito del color del ánimo.
     */
    private fun DrawScope.cuaderno(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = luz(x, oscuro, v.noche, 0.4f)
        val tinte = listOf(IRIS, HIERRO, Color(0xFFA9874A), Color(0xFF9A6E92))[Math.floorMod(v.semilla / 3, 4)]
        // El papelito del ánimo, que asoma por arriba.
        val tono = (v.tono ?: Color(0xFFE6DCC2)).let { if (oscuro) mezcla(it, NOCHE, 0.3f) else it }
        rotate(6f, Offset(w * 0.3f, 6 * u)) { drawRect(tono, Offset(w * 0.18f, -1 * u), Size(w * 0.24f, 10 * u)) }
        // El lomo de papel, con el borde de arriba roto.
        val forma = Path().apply {
            moveTo(0f, 3 * u)
            var x = 0f; var k = 0
            while (x < w) { lineTo(x, (1.2f + 2f * azar(v.semilla, k)) * u); x += 2.5f * u; k++ }
            lineTo(w, 2 * u); lineTo(w, h); lineTo(0f, h); close()
        }
        drawPath(forma, Color.Black.copy(alpha = 0.25f), style = Stroke(1.2f * u))
        clipPath(forma) {
            fun p(x: Color) = luz(x, oscuro, v.noche, 0.2f)
            drawRect(Brush.horizontalGradient(listOf(p(mezcla(PAPEL, Color(0xFFC9B48A), 0.5f)), p(mezcla(PAPEL, Color(0xFFC9B48A), 0.15f)), p(mezcla(PAPEL, Color(0xFFC9B48A), 0.6f))), 0f, w), Offset.Zero, Size(w, h))
            // Las manchas de los años.
            for (n in 0 until 5) drawCircle(c(Color(0xFFB89A62)).copy(alpha = 0.15f), (2f + 3f * azar(v.semilla, n + 7)) * u, Offset(w * azar(v.semilla, n), h * azar(v.semilla, n + 3)))
            // La franja impresa con el día.
            val franja = Rect(0f, h * 0.08f, w, h * 0.3f)
            drawRect(c(tinte), franja.topLeft, franja.size)
            drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.22f), Color.Transparent, Color.Black.copy(alpha = 0.25f)), 0f, w), franja.topLeft, franja.size)
            textoEn(medidor, v.dia, franja.deflate(1.5f * u), TITULO, if (oscuro) TINTA_NOCHE else PAPEL_CLARO, 15f, minimo = 6f)
            drawLine(c(tinte), Offset(1.5f * u, h * 0.33f), Offset(w - 1.5f * u, h * 0.33f), 0.6f * u)
            // El mes impreso.
            val mes = Rect(1 * u, h * 0.36f, w - 1 * u, h * 0.8f)
            if (mes.height > 16 * u) textoEn(medidor, mesCorto(v.mes), mes, ROTULO, tinta(oscuro).copy(alpha = 0.8f), 8f, vertical = true, espaciado = 0.5f, minimo = 4f)
            // El iris chico impreso al pie (de noche, la media luna).
            val pc = Offset(w / 2, h * 0.9f)
            if (v.noche) floron(pc, minOf(w * 0.2f, 3.6f * u), c(tinte), true)
            else with(TrazosNouveau) { iris(Offset(pc.x, h * 0.97f), minOf(h * 0.13f, w * 0.7f), c(tinte), c(TALLO_SOMBRA), c(HIERRO)) }
            // El doblez del lomo.
            drawLine(Color.Black.copy(alpha = 0.12f), Offset(w * 0.5f, 0f), Offset(w * 0.5f, h), 0.5f * u)
        }
    }

    /**
     * El álbum de lomo verde de hierro: tela oscura, una línea de látigo de oro que corre de arriba abajo por el lomo, el día en
     * un medallón dorado y el mes al pie; cinta del ánimo.
     */
    private fun DrawScope.album(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val telaC = luz(if (v.semilla % 2 == 0) HIERRO else Color(0xFF4B3A5E), oscuro, v.noche, 0.4f)
        val oroC = luz(ORO, oscuro, v.noche, 0.7f)
        val r = Rect(0f, 0f, w, h)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(1 * u, 1 * u), Size(w, h), CornerRadius(2 * u))
        clipPath(Path().apply { addRoundRect(RoundRect(r, CornerRadius(2 * u))) }) {
            lomo(r, telaC, v.semilla + 1)
            // Las cantoneras de piel en las puntas del lomo.
            drawRect(mezcla(telaC, NOGAL_OSC, 0.5f), Offset.Zero, Size(w, h * 0.06f))
            drawRect(mezcla(telaC, NOGAL_OSC, 0.5f), Offset(0f, h * 0.94f), Size(w, h * 0.06f))
            filete(Offset(1 * u, h * 0.06f), Offset(w - 1 * u, h * 0.06f), oroC, 0.7f * u)
            filete(Offset(1 * u, h * 0.94f), Offset(w - 1 * u, h * 0.94f), oroC, 0.7f * u)
            // El medallón con el día.
            val mr = minOf(w * 0.4f, 9 * u)
            val mc = Offset(w / 2, h * 0.06f + mr + 4 * u)
            drawCircle(oroC, mr, mc, style = Stroke(0.9f * u))
            drawCircle(oroC.copy(alpha = 0.5f), mr - 1.6f * u, mc, style = Stroke(0.4f * u))
            dorado(medidor, v.dia, Rect(mc.x - mr, mc.y - mr * 0.8f, mc.x + mr, mc.y + mr * 0.8f), TITULO, oroC, 12f, minimo = 5f)
            // La línea de látigo que corre por el lomo.
            val y0 = mc.y + mr + 3 * u
            val y1 = h * 0.8f
            if (y1 - y0 > 20 * u) latigo(Offset(w * 0.5f, y1), Offset(w * 0.5f, y0 + (y1 - y0) * 0.15f), minOf(w * 0.18f, 4 * u), maxOf(1.2f * u, w * 0.08f), oroC, 0.12f, true, false)
            val mes = Rect(1 * u, h * 0.82f, w - 1 * u, h * 0.93f)
            textoEn(medidor, mesCorto(v.mes).take(4) + if (mesCorto(v.mes).length > 4) "." else "", mes, ROTULO, oroC, 7f, minimo = 3.5f)
            if (v.noche) floron(Offset(w * 0.78f, mc.y + mr + 6 * u), 2.4f * u, oroC, true)
        }
        cinta(w * 0.3f, minOf(h * 0.18f, 18 * u), v.tono, oscuro, v.semilla)
    }

    // ==== PENDIENTE
    override val altoTapa: Dp = 200.dp
    override val cajaTapa = Rect(0.16f, 0.4f, 0.66f, 0.74f)
    override val letraTapa: FontFamily = TEXTO
    override val tamTapa = 12.5f
    override val lineasTapa = 4
    override val centradoTapa = false
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = tinta(oscuro)
    override fun palabrasTapa(n: Int): String = "$n"
    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {}
    override val altoEncabezado: Dp = 118.dp
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {}
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {}
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {}
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {}
    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = PAPEL_CLARO
    override val altoLibreta: Dp = 268.dp
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {}
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {}
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {}
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {}
    override val cajaPrevia = Rect(0.13f, 0.16f, 0.87f, 0.31f)
    override val altoBuzon: Dp = 210.dp
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {}
    override val altoPestanas: Dp = 78.dp
    override fun DrawScope.pestanas(e: EstadoPestanas, medidor: TextMeasurer, oscuro: Boolean) {}
    override val altoCampo: Dp = 52.dp
    override val cajaCampo = Rect(0.17f, 0.24f, 0.83f, 0.76f)
    override val letraCampo: FontFamily = TEXTO
    override fun tintaCampo(oscuro: Boolean): Color = tinta(oscuro)
    override fun DrawScope.campo(e: EstadoCampo, medidor: TextMeasurer, oscuro: Boolean) {}
    override fun DrawScope.repisaObras(medidor: TextMeasurer, oscuro: Boolean) {}
    override fun DrawScope.portadaObra(e: EstadoPortada, medidor: TextMeasurer, oscuro: Boolean) {}
    override val margenRecuerdo: Dp = 14.dp
    override val altoRotuloRecuerdo: Dp = 48.dp
    override fun tintaRecuerdo(oscuro: Boolean): Color = tinta(oscuro)
    override fun DrawScope.recuerdo(e: EstadoRecuerdo, medidor: TextMeasurer, oscuro: Boolean) {}
}
