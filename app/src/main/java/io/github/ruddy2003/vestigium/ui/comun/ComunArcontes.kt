package io.github.ruddy2003.vestigium.ui.comun

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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.TrazosArcontes
import io.github.ruddy2003.vestigium.ui.biblioteca.textoEn
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Arcontes
// =====================================================================

/**
 * Las rayas de los renglones de un campo de varios renglones, de abajo arriba: la primera ([k] = 0) al pie del último
 * renglón, a [margenY] del canto de abajo, y luego una cada 24 sp mientras quede sitio arriba (16 dp bajo [margenY]).
 */
private inline fun DrawScope.renglonesArc(margenY: Dp, raya: (k: Int, y: Float) -> Unit) {
    val paso = maxOf(24.sp.toPx(), 1f)
    val tope = margenY.toPx() + 16.dp.toPx()
    var y = size.height - margenY.toPx() + 1.dp.toPx()
    var k = 0
    while (y > tope && k < 200) { raya(k, y); y -= paso; k++ }
}

/** La razón de contraste entre dos colores opacos (WCAG 2): de 1 (iguales) a 21 (negro sobre blanco). */
private fun contrasteArc(a: Color, b: Color): Float {
    val la = a.luminance(); val lb = b.luminance()
    return (maxOf(la, lb) + 0.05f) / (minOf(la, lb) + 0.05f)
}

/**
 * El color [c] de un texto, tal cual si se lee sobre [fondo] (contraste de al menos [minimo]:1); si no, llevado poco a poco
 * hacia la tinta de los códices (sobre un fondo claro) o hacia el lino (sobre uno oscuro) hasta que se lea. Con [tenue] se
 * apaga antes hacia el fondo, lo más que deja ese contraste.
 */
private fun legibleArc(c: Color, fondo: Color, tenue: Boolean = false, minimo: Float = 4.6f): Color {
    val opaco = c.copy(alpha = 1f)
    if (tenue) for (k in 8 downTo 1) {
        val m = mezcla(opaco, fondo, k * 0.05f)
        if (contrasteArc(m, fondo) >= minimo) return m
    }
    if (contrasteArc(opaco, fondo) >= minimo) return opaco
    val hacia = if (fondo.luminance() > 0.18f) Color(0xFF120F1C) else Color(0xFFF6F0E0)
    for (k in 1..20) {
        val m = mezcla(opaco, hacia, k * 0.05f)
        if (contrasteArc(m, fondo) >= minimo) return m
    }
    return hacia
}

/** El ancho, en píxeles, de [texto] en una sola línea con el mismo estilo con que lo dibuja [textoEn]. */
private fun anchoArc(medidor: TextMeasurer, texto: String, letra: FontFamily, tam: Float, peso: FontWeight = FontWeight.Normal, espaciado: Float = 0f): Float =
    if (texto.isBlank()) 0f
    else medidor.measure(texto, TextStyle(fontFamily = letra, fontSize = tam.sp, lineHeight = (tam * 1.05f).sp, fontWeight = peso, letterSpacing = espaciado.sp),
        maxLines = 1, softWrap = false).size.width.toFloat()

/** Un nicho tallado en [r]: los lados rectos y, arriba, el arco rebajado que sube [flecha] desde sus arranques. */
private fun nichoArc(r: Rect, flecha: Float): Path = Path().apply {
    val f = flecha.coerceIn(0f, minOf(r.height, r.width / 2))
    moveTo(r.left, r.bottom); lineTo(r.left, r.top + f)
    if (f > 0f) arcTo(Rect(r.left, r.top, r.right, r.top + 2 * f), 180f, 180f, false) else lineTo(r.right, r.top)
    lineTo(r.right, r.bottom); close()
}

/** Una losa de [r] con las esquinas de arriba en el arco pequeño de los nichos ([arriba]) y las de abajo casi rectas ([abajo]). */
private fun losaArc(r: Rect, arriba: Float, abajo: Float): Path = Path().apply {
    addRoundRect(RoundRect(r.left, r.top, r.right, r.bottom, CornerRadius(arriba), CornerRadius(arriba), CornerRadius(abajo), CornerRadius(abajo)))
}

/** Una curva cerrada y suave que pasa cerca de [p] (por los puntos medios de cada lado): el barro apretado, el canto gastado. */
private fun curvaArc(p: List<Offset>): Path = Path().apply {
    val n = p.size
    if (n < 3) return@apply
    fun medio(a: Offset, b: Offset) = Offset((a.x + b.x) / 2, (a.y + b.y) / 2)
    val m0 = medio(p[0], p[1])
    moveTo(m0.x, m0.y)
    for (k in 1..n) {
        val a = p[k % n]; val b = medio(a, p[(k + 1) % n])
        quadraticTo(a.x, a.y, b.x, b.y)
    }
    close()
}

/**
 * Arcontes: las piezas comunes de la casa del salador de Magdala, en Galilea hacia el año 30, como la ve un iniciado. Todo es
 * de la Galilea real: la caliza labrada como los sillares de entonces (el marco liso a cincel dentado y el centro picado), los
 * nichos de arco rebajado, la hoja de papiro con sus fibras y la juntura de sus hojas, la lámpara de aceite de barro, el sello
 * de barro, la correa de cuero del códice y el lino de los rollos con su recuadro de hilo azul. Y, por las grietas de la
 * piedra, la luz blanca y violeta que no es de este mundo. El campo es la tablilla de caliza con su hoja de papiro metida en
 * el hueco: con el foco, la piedra se abre por el canto de arriba y deja salir la luz. De noche es la misma casa a oscuras,
 * azul negra, donde la única luz cálida es la de la lámpara.
 */
object ComunArcontes : DisenoComun {
    private val BARRO = TrazosArcontes.BARRO
    private val BARRO_CLARO = TrazosArcontes.BARRO_CLARO
    private val CUERO = TrazosArcontes.CUERO
    private val CORREA = TrazosArcontes.CORREA
    private val LINO = TrazosArcontes.LINO
    private val CIELO = TrazosArcontes.CIELO_ANTIGUO
    private val ESFERA = TrazosArcontes.ESFERA
    private val GRIETA = TrazosArcontes.GRIETA
    private val GRIETA_HALO = TrazosArcontes.GRIETA_HALO
    private val LLAMA_HALO = TrazosArcontes.LLAMA_HALO
    private val ORO = TrazosArcontes.ORO_DEMIURGO

    // ---------------------------------------------------------------- los colores de la casa (de día y de noche)

    /** La caliza al sol; de noche, la misma piedra apenas vista, azul. */
    private fun piedra(oscuro: Boolean): Color = if (oscuro) Color(0xFF2A2D42) else Color(0xFFDDD0AC)
    /** La cara de la piedra que da a la luz (los marcos, las jambas). */
    private fun piedraLuz(oscuro: Boolean): Color = if (oscuro) Color(0xFF34374F) else Color(0xFFEFE6CC)
    /** El fondo de un nicho o de un hueco tallado. */
    private fun hueco(oscuro: Boolean): Color = if (oscuro) Color(0xFF111324) else Color(0xFFC9B98F)
    /** El papiro: la hoja al sol; de noche, la misma hoja a oscuras. */
    private fun papiro(oscuro: Boolean): Color = if (oscuro) Color(0xFF1B1E32) else Color(0xFFECDDB3)
    /** El color de las fibras del papiro. */
    private fun fibra(oscuro: Boolean): Color = if (oscuro) Color(0xFF30355A) else Color(0xFF9C7F44)
    private fun tinta(oscuro: Boolean): Color = if (oscuro) Color(0xFFECE4CF) else Color(0xFF1E1A2B)
    private fun tenue(oscuro: Boolean): Color = if (oscuro) Color(0xFFA09CB0) else Color(0xFF57506A)
    /** El ocre del mundo visible: de día, el oro quemado; de noche, la llama. */
    private fun ocre(oscuro: Boolean): Color = if (oscuro) Color(0xFFE9B04F) else Color(0xFF7A4F0B)
    /** El lago: de día, hondo; de noche, bajo la luna. */
    private fun lago(oscuro: Boolean): Color = if (oscuro) Color(0xFF86BBCB) else Color(0xFF2B5B6C)
    /** El violeta de la grieta: de día, oscuro para que se vea sobre la piedra; de noche, la luz. */
    private fun violeta(oscuro: Boolean): Color = if (oscuro) Color(0xFFC4B0FF) else Color(0xFF5A3B8F)
    /** El trazo hundido de lo tallado. */
    private fun sombraTallado(oscuro: Boolean): Color = if (oscuro) Color.Black.copy(alpha = 0.6f) else Color(0xFF4A3A1C).copy(alpha = 0.34f)
    /** El filo de luz de lo tallado: el sol de día; de noche, la luna, apenas. */
    private fun luzFilo(oscuro: Boolean): Color = if (oscuro) Color(0xFFB9C3E6).copy(alpha = 0.1f) else Color.White.copy(alpha = 0.62f)
    /** La luz que baja sobre la cara de la piedra: el sol de día; de noche, la luna. */
    private fun luzCara(oscuro: Boolean): Color = if (oscuro) Color(0xFFB9C3E6).copy(alpha = 0.06f) else Color.White.copy(alpha = 0.38f)
    /** El barro de la lámpara: de noche, sin su llama, se va con la oscuridad. */
    private fun barroLampara(encendida: Boolean, oscuro: Boolean): Color =
        if (oscuro && !encendida) mezcla(BARRO_CLARO, CIELO, 0.5f) else BARRO_CLARO
    /** La sombra de lo que está encima de algo. */
    private fun sombraPieza(oscuro: Boolean): Color = Color.Black.copy(alpha = if (oscuro) 0.5f else 0.16f)

    // ---------------------------------------------------------------- lo labrado, el papiro y el barro

    /**
     * El hueco tallado [p] en la piedra: con la luz arriba a la izquierda, la sombra cae sobre su canto de arriba y el de la
     * izquierda (en dos capas, para que se vea hondo), y el labio de abajo y el de la derecha se llevan un filo de luz.
     */
    private fun DrawScope.hundidoArc(p: Path, hondo: Float, sombra: Color, luz: Color) {
        clipPath(p) {
            translate(hondo, hondo) { drawPath(p, sombra, style = Stroke(hondo * 2f)) }
            translate(hondo * 1.7f, hondo * 1.7f) { drawPath(p, sombra.copy(alpha = sombra.alpha * 0.35f), style = Stroke(hondo * 3.4f)) }
            translate(-hondo * 0.4f, -hondo * 0.4f) { drawPath(p, luz, style = Stroke(hondo * 0.8f)) }
        }
    }

    /**
     * El marco liso de un sillar, labrado a cincel dentado: rayitas finas y sesgadas, todas en la misma dirección, de largo
     * desigual, en la franja de [ancho] junto a cada borde de [r]. Dos capas de trazo, una más marcada que la otra.
     */
    private fun DrawScope.peinadoArc(r: Rect, ancho: Float, color: Color, semilla: Int) {
        val u = density
        if (r.width < 2 * ancho || r.height < 2 * ancho) return
        val a = Path(); val b = Path()
        val paso = maxOf(2.1f * u, 1f)
        val alto = ancho - 1.2f * u
        fun rayita(p: Path, x: Float, y: Float, k: Int) {
            val l = alto * (0.4f + 0.6f * azar(semilla, k + 131))
            p.moveTo(x, y); p.lineTo(x + l * 0.6f, y + l)
        }
        var x = r.left + ancho * 0.3f; var k = 0
        while (x < r.right - ancho && k < 500) {
            rayita(if (azar(semilla, k) > 0.5f) a else b, x, r.top + 0.6f * u + alto * 0.3f * azar(semilla, k + 211), k)
            rayita(if (azar(semilla, k + 37) > 0.5f) a else b, x + paso * 0.4f, r.bottom - ancho + 0.6f * u + alto * 0.3f * azar(semilla, k + 251), k + 61)
            x += paso * (0.7f + 0.6f * azar(semilla, k + 83)); k++
        }
        var y = r.top + ancho; var j = 0
        while (y < r.bottom - ancho && j < 300) {
            rayita(if (azar(semilla, j + 300) > 0.5f) a else b, r.left + 0.6f * u + alto * 0.2f * azar(semilla, j + 331), y, j + 361)
            rayita(if (azar(semilla, j + 337) > 0.5f) a else b, r.right - ancho + 0.6f * u + alto * 0.2f * azar(semilla, j + 371), y, j + 401)
            y += paso * (0.7f + 0.6f * azar(semilla, j + 383)); j++
        }
        drawPath(a, color.copy(alpha = color.alpha * 0.55f), style = Stroke(0.45f * u, cap = StrokeCap.Round))
        drawPath(b, color.copy(alpha = color.alpha * 0.28f), style = Stroke(0.4f * u, cap = StrokeCap.Round))
    }

    /** La cara picada de un sillar en [r]: los golpecitos del puntero, puntos pequeños al azar. */
    private fun DrawScope.picadoArc(r: Rect, color: Color, semilla: Int) {
        val u = density
        val n = (r.width * r.height / (9f * u * u)).toInt().coerceIn(0, 900)
        if (n == 0) return
        val p = Path()
        for (k in 0 until n) {
            val x = r.left + r.width * azar(semilla, k * 3); val y = r.top + r.height * azar(semilla, k * 3 + 1)
            p.addOval(Rect(Offset(x, y), (0.22f + 0.38f * azar(semilla, k * 3 + 2)) * u))
        }
        drawPath(p, color)
    }

    /**
     * Un surco tallado en la piedra de [x0] a [x1] a la altura [y]: el trazo hundido y, debajo, su filo de luz. Va parejo hasta
     * los tres cuartos y se pierde hacia [x1], como una raya hecha a cincel que se gasta.
     */
    private fun DrawScope.surcoArc(x0: Float, x1: Float, y: Float, oscuro: Boolean, a: Float = 1f) {
        if (abs(x1 - x0) < 2f) return
        val u = density
        val s = sombraTallado(oscuro).let { it.copy(alpha = it.alpha * a) }
        val l = luzFilo(oscuro).let { it.copy(alpha = it.alpha * a) }
        drawLine(Brush.horizontalGradient(0f to s, 0.75f to s, 1f to s.copy(alpha = 0f), startX = x0, endX = x1), Offset(x0, y), Offset(x1, y), 0.8f * u, StrokeCap.Round)
        drawLine(Brush.horizontalGradient(0f to l, 0.75f to l, 1f to l.copy(alpha = 0f), startX = x0, endX = x1), Offset(x0, y + 0.8f * u), Offset(x1, y + 0.8f * u),
            0.6f * u, StrokeCap.Round)
    }

    /**
     * Las fibras de una hoja de papiro en [r]: las de la cara, a lo largo, finas y de largo desigual (unas más marcadas); las de
     * la otra capa, de arriba abajo, apenas; y la juntura (la kóllesis) donde se pegaron dos hojas del rollo, un poco más oscura.
     * Con [fuerza] menor, todo más tenue.
     */
    private fun DrawScope.fibrasArc(r: Rect, color: Color, semilla: Int, fuerza: Float = 1f) {
        val u = density
        if (r.width < 4 * u || r.height < 4 * u) return
        val fuertes = Path(); val suaves = Path()
        val paso = maxOf(1.25f * u, 1f)
        var y = r.top + paso * 0.5f; var k = 0
        while (y < r.bottom && k < 300) {
            val p = if (azar(semilla, k) > 0.66f) fuertes else suaves
            val x0 = r.left + r.width * 0.5f * azar(semilla, k + 500) * azar(semilla, k + 900)
            val x1 = r.right - r.width * 0.5f * azar(semilla, k + 700) * azar(semilla, k + 1100)
            val d = (azar(semilla, k + 1300) - 0.5f) * 0.8f * u
            p.moveTo(x0, y); p.lineTo((x0 + x1) / 2, y + d); p.lineTo(x1, y + d * 0.3f)
            y += paso * (0.7f + 0.6f * azar(semilla, k + 1500)); k++
        }
        drawPath(suaves, color.copy(alpha = color.alpha * 0.16f * fuerza), style = Stroke(0.45f * u))
        drawPath(fuertes, color.copy(alpha = color.alpha * 0.3f * fuerza), style = Stroke(0.6f * u))
        val otra = Path()
        var x = r.left + 2 * u; var j = 0
        while (x < r.right && j < 300) {
            otra.moveTo(x, r.top); otra.lineTo(x + 0.4f * u, r.bottom)
            x += (3.5f + 4f * azar(semilla, j + 1700)) * u; j++
        }
        drawPath(otra, color.copy(alpha = color.alpha * 0.045f * fuerza), style = Stroke(0.5f * u))
        if (r.width > 60 * u) {
            val xk = r.left + r.width * (0.58f + 0.28f * azar(semilla, 1900))
            drawRect(color.copy(alpha = color.alpha * 0.09f * fuerza), Offset(xk, r.top), Size(2.6f * u, r.height))
            drawLine(color.copy(alpha = color.alpha * 0.22f * fuerza), Offset(xk, r.top), Offset(xk + 0.3f * u, r.bottom), 0.6f * u)
        }
    }

    /**
     * Un sello de barro (una bulla) en [c], de radio [r]: la masa apretada de borde irregular, con la luz arriba a la izquierda,
     * el anillo del sello hundido y el ouroboros impreso, con su filo de luz.
     */
    private fun DrawScope.bullaArc(c: Offset, r: Float, oscuro: Boolean, semilla: Int) {
        val u = density
        val claro = if (oscuro) mezcla(BARRO_CLARO, CIELO, 0.32f) else mezcla(BARRO_CLARO, Color.White, 0.12f)
        val medio = if (oscuro) mezcla(BARRO, CIELO, 0.32f) else BARRO
        val hondo = mezcla(medio, Color.Black, 0.45f)
        val masa = curvaArc(List(14) { k ->
            val a = 2 * PI * k / 14
            val rr = r * (0.9f + 0.16f * azar(semilla, k))
            Offset(c.x + rr * cos(a).toFloat(), c.y + rr * sin(a).toFloat())
        })
        translate(0.4f * u, 0.9f * u) { drawPath(masa, sombraPieza(oscuro).copy(alpha = if (oscuro) 0.45f else 0.22f)) }
        drawPath(masa, Brush.radialGradient(listOf(claro, medio, hondo), Offset(c.x - r * 0.35f, c.y - r * 0.4f), r * 1.6f))
        val g = maxOf(r * 0.06f, 0.5f * u)
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.08f else 0.22f), r * 0.66f, c + Offset(g, g), style = Stroke(g))
        drawCircle(hondo.copy(alpha = 0.6f), r * 0.66f, c, style = Stroke(g))
        with(TrazosArcontes) {
            ouroboros(c + Offset(g, g), r * 0.4f, r * 0.16f, Color.White.copy(alpha = if (oscuro) 0.08f else 0.22f), -60f)
            ouroboros(c, r * 0.4f, r * 0.16f, hondo.copy(alpha = 0.9f), -60f)
        }
    }

    /**
     * Dos cuñas de tinta, como las de la banda de los códices: gruesas en el codo y finas en las puntas, la de adelante más
     * grande y más marcada. Apuntan a la derecha ([hacia] = 1) o a la izquierda (-1), con el codo de la de adelante en [c].
     */
    private fun DrawScope.cunasArc(c: Offset, alto: Float, hacia: Float, color: Color) {
        fun cuna(x: Float, a: Float, alfa: Float) {
            fun p(dx: Float, dy: Float) = Offset(x + hacia * dx * a, c.y + dy * a)
            val t1 = p(-0.55f, -1f); val o1 = p(-0.05f, -0.36f); val codo = p(0.5f, 0f); val o2 = p(-0.05f, 0.36f); val t2 = p(-0.55f, 1f)
            val i2 = p(-0.12f, 0.3f); val dentro = p(0.06f, 0f); val i1 = p(-0.12f, -0.3f)
            drawPath(Path().apply {
                moveTo(t1.x, t1.y); quadraticTo(o1.x, o1.y, codo.x, codo.y); quadraticTo(o2.x, o2.y, t2.x, t2.y)
                quadraticTo(i2.x, i2.y, dentro.x, dentro.y); quadraticTo(i1.x, i1.y, t1.x, t1.y); close()
            }, color.copy(alpha = color.alpha * alfa))
        }
        cuna(c.x - hacia * alto * 1.1f, alto * 0.8f, 0.55f)
        cuna(c.x - hacia * alto * 0.5f, alto, 1f)
    }

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 17.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = tinta(oscuro)
    override fun pistaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF8E8AA2) else Color(0xFF635B6E)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) lago(true) else ocre(false)
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFFF8A7A) else Color(0xFF9E2B25)

    /**
     * La tablilla de caliza con su hoja de papiro: la losa con las esquinas de arriba en arco, su marco liso labrado a cincel
     * dentado y la luz que baja; en el hueco, la hoja de papiro con sus fibras y la juntura de dos hojas, y el renglón trazado
     * con su punto de pautado en cada punta (uno por renglón si son varios). Con el foco, la hoja se marca y la piedra se abre
     * por el canto de arriba, hacia la derecha: la grieta deja salir la luz. Con un error, la hoja se marca en rojo de jaspe; apagada, la piedra y
     * la hoja se van al gris.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 24 * u || h < 20 * u) return
        val gris = if (oscuro) Color(0xFF1C1E2C) else Color(0xFFC9C3B6)
        fun a(c: Color) = if (e.habilitado) c else mezcla(c, gris, 0.45f)
        val alto = h - 1.2f * u
        val radio = minOf(9 * u, alto / 2)
        val losa = losaArc(Rect(0f, 0f, w, alto), radio, 2 * u)
        translate(0.6f * u, 1.2f * u) { drawPath(losa, sombraPieza(oscuro)) }
        drawPath(losa, a(piedra(oscuro)))
        val b = 5f * u
        clipPath(losa) {
            drawRect(Brush.verticalGradient(listOf(luzCara(oscuro), Color.Transparent, Color.Black.copy(alpha = if (oscuro) 0.25f else 0.1f)), 0f, alto))
            peinadoArc(Rect(0f, 0f, w, alto), b, sombraTallado(oscuro).let { it.copy(alpha = it.alpha * (if (e.habilitado) 0.75f else 0.4f)) }, e.semilla)
        }
        drawLine(luzFilo(oscuro), Offset(radio, 0.7f * u), Offset(w - radio, 0.7f * u), 0.7f * u)
        // La hoja de papiro metida en el hueco.
        val r = Rect(b, b, w - b, alto - b)
        val hoja = losaArc(r, maxOf(radio - b, 2 * u), 1 * u)
        drawPath(hoja, a(papiro(oscuro)))
        clipPath(hoja) { fibrasArc(r, fibra(oscuro), e.semilla, if (e.habilitado) 1f else 0.5f) }
        hundidoArc(hoja, 1.3f * u, Color.Black.copy(alpha = if (oscuro) 0.5f else 0.2f), luzFilo(oscuro))
        // El renglón, con el punto de pautado en cada punta.
        val izq = margenCampoX.toPx() - 2 * u; val der = w - margenCampoX.toPx() + 2 * u
        val raya = tinta(oscuro).copy(alpha = if (!e.habilitado) 0.1f else if (e.enfocado) 0.34f else 0.2f)
        fun renglon(y: Float) {
            if (der - izq < 8 * u) return
            drawLine(raya, Offset(izq, y), Offset(der, y), 0.7f * u)
            drawCircle(raya, 0.7f * u, Offset(izq - 2.2f * u, y)); drawCircle(raya, 0.7f * u, Offset(der + 2.2f * u, y))
        }
        if (e.multilinea) renglonesArc(margenCampoY) { _, y -> renglon(y) } else renglon(h - 10.5f * u)
        // Con el foco o con un error, la hoja se marca en su color.
        if (e.error) drawPath(hoja, errorCampo(oscuro), style = Stroke(1.5f * u))
        else if (e.enfocado && e.habilitado) drawPath(hoja, rotuloCampo(oscuro).copy(alpha = 0.9f), style = Stroke(1.2f * u))
        // Con el foco, la piedra se abre por el canto de arriba, a la derecha, y deja salir la luz.
        if (e.enfocado && e.habilitado && !e.error) clipPath(losa) {
            val yg = b * 0.5f
            val desde = Offset(maxOf(w - 70 * u, w * 0.55f), yg + 0.4f * u); val hasta = Offset(w - radio * 0.9f, yg - 0.3f * u)
            // La raja en la piedra, hundida, y por ella la luz.
            drawLine(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.2f), desde, hasta, 2.6f * u, StrokeCap.Round)
            with(TrazosArcontes) { grieta(desde, hasta, 0.9f * u, 1f, e.semilla + 3) }
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * Un surco tallado en la caliza, que se pierde hacia los dos lados; al centro, el ouroboros pequeño con la chispa dentro
     * y, a cada lado, tres puntos que se achican: con la chispa, las siete luces. Tenue, a la mitad.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val y = size.height / 2
        val f = if (e.tenue) 0.5f else 1f
        val cx = w / 2
        val r = 3.4f * u
        val hueco = r + 15 * u
        if (cx - hueco > 6 * u) {
            surcoArc(cx - hueco, 2 * u, y, oscuro, 0.85f * f)
            surcoArc(cx + hueco, w - 2 * u, y, oscuro, 0.85f * f)
        }
        val punto = (if (oscuro) ESFERA else mezcla(ESFERA, Color(0xFF2A2440), 0.3f)).copy(alpha = 0.85f * f)
        for (k in 0 until 3) {
            val d = r + (4.5f + k * 3.6f) * u
            val rr = (1f - k * 0.2f) * u
            drawCircle(punto, rr, Offset(cx - d, y)); drawCircle(punto, rr, Offset(cx + d, y))
        }
        val serpiente = if (oscuro) mezcla(ESFERA, LINO, 0.3f) else mezcla(ORO, Color(0xFF5A3F08), 0.4f)
        with(TrazosArcontes) {
            ouroboros(Offset(cx, y), r, 1.4f * u, serpiente.copy(alpha = f), -60f)
            chispa(Offset(cx, y), 1.7f * u, f, if (oscuro) GRIETA else violeta(false), GRIETA_HALO)
        }
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = ocre(oscuro)

    /**
     * La correa de cuero del códice bajo la palabra, con el pasador de hueso a la izquierda y el nudo a la derecha. Al
     * presionarlo, detrás de la palabra se enciende un nicho con la luz de la lámpara y la correa se aclara; apagado, queda
     * solo una raya punteada tallada, sin correa.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val izq = minOf(9 * u, w * 0.18f); val der = w - izq
        val y = h / 2 + 11.5f * u
        if (der - izq < 10 * u) return
        if (!e.habilitado) {
            drawLine(sombraTallado(oscuro).copy(alpha = 0.3f), Offset(izq, y), Offset(der, y), 0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 2.2f * u)))
            return
        }
        if (e.presionado && h > 20 * u) {
            val n = nichoArc(Rect(3 * u, 5 * u, w - 3 * u, h - 5 * u), minOf(8 * u, (h - 10 * u) * 0.3f))
            drawPath(n, Brush.radialGradient(listOf(LLAMA_HALO.copy(alpha = if (oscuro) 0.26f else 0.45f), LLAMA_HALO.copy(alpha = 0f)), Offset(w / 2, h * 0.66f),
                maxOf(w * 0.62f, 24 * u)))
            drawPath(n, ocre(oscuro).copy(alpha = 0.55f), style = Stroke(0.8f * u))
        }
        val cuero = if (e.presionado) mezcla(CUERO, TrazosArcontes.LLAMA, if (oscuro) 0.45f else 0.3f) else if (oscuro) mezcla(CUERO, LINO, 0.12f) else CUERO
        // La correa, que acaba en punta a la izquierda, con su costura.
        val g = 2.3f * u
        drawPath(Path().apply {
            moveTo(izq - 1.5f * u, y); lineTo(izq + 3 * u, y - g / 2); lineTo(der - 3 * u, y - g / 2); lineTo(der - 3 * u, y + g / 2); lineTo(izq + 3 * u, y + g / 2); close()
        }, cuero)
        drawLine(LINO.copy(alpha = if (oscuro) 0.14f else 0.3f), Offset(izq + 3 * u, y - g / 2 + 0.3f * u), Offset(der - 4 * u, y - g / 2 + 0.3f * u), 0.4f * u)
        drawLine(LINO.copy(alpha = if (oscuro) 0.22f else 0.45f), Offset(izq + 4 * u, y + 0.1f * u), Offset(der - 6 * u, y + 0.1f * u), 0.45f * u,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.2f * u, 1.5f * u)))
        // La punta que pasa bajo el pasador de hueso y cuelga.
        drawLine(cuero, Offset(der - 3 * u, y), Offset(der + 1.6f * u, y + 2.8f * u), g * 0.8f, StrokeCap.Round)
        val hueso = if (oscuro) mezcla(LINO, CIELO, 0.5f) else LINO
        val pasador = Rect(der - 4.4f * u, y - 3.6f * u, der - 2.2f * u, y + 3.6f * u)
        drawRoundRect(sombraPieza(oscuro), pasador.topLeft + Offset(0.4f * u, 0.6f * u), pasador.size, CornerRadius(1.1f * u))
        drawRoundRect(hueso, pasador.topLeft, pasador.size, CornerRadius(1.1f * u))
        drawRoundRect(CORREA.copy(alpha = 0.6f), pasador.topLeft, pasador.size, CornerRadius(1.1f * u), style = Stroke(0.45f * u))
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    override val margenFila: Dp = 18.dp

    /**
     * Una repisa de la casa: el surco tallado al pie y, a la izquierda, un nicho pequeño en la pared con su lámpara apagada
     * (más alto si la fila lleva detalle). Al tocarla la lámpara se enciende y su luz cálida baña la fila; de noche es la
     * única luz cálida.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2
        if (e.presionado) {
            drawRect(Brush.horizontalGradient(listOf(LLAMA_HALO.copy(alpha = if (oscuro) 0.07f else 0.14f), LLAMA_HALO.copy(alpha = 0f)), 0f, w))
            drawRect(Brush.radialGradient(listOf(LLAMA_HALO.copy(alpha = if (oscuro) 0.26f else 0.3f), LLAMA_HALO.copy(alpha = 0f)), Offset(8 * u, cy), 80 * u))
        }
        surcoArc(margenFila.toPx(), w - 2 * u, h - 1.4f * u, oscuro, 0.7f)
        if (h < 16 * u) return
        val alto = if (e.conDetalle) minOf(24 * u, h - 8 * u) else minOf(17 * u, h - 8 * u)
        val r = Rect(1.5f * u, cy - alto / 2, 13 * u, cy + alto / 2)
        val n = nichoArc(r, 5.5f * u)
        drawPath(n, hueco(oscuro))
        hundidoArc(n, 0.9f * u, Color.Black.copy(alpha = if (oscuro) 0.5f else 0.3f), luzFilo(oscuro))
        drawLine(if (oscuro) piedraLuz(true) else luzFilo(false), Offset(r.left - 0.8f * u, r.bottom + 0.5f * u), Offset(r.right + 0.8f * u, r.bottom + 0.5f * u), 0.9f * u)
        with(TrazosArcontes) { lampara(Offset(r.center.x - 0.7f * u, r.bottom - 1.9f * u), 9.6f * u, barroLampara(e.presionado, oscuro), if (e.presionado) 1f else 0f) }
    }

    /** Dos cuñas de tinta como las de la banda de los códices, con la chispa delante. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val cx = size.width / 2; val cy = size.height / 2
        val s = size.minDimension / 20f
        with(TrazosArcontes) { chispa(Offset(cx - 6.6f * s, cy), 2.1f * s, 1f, if (oscuro) GRIETA else violeta(false), GRIETA_HALO) }
        cunasArc(Offset(cx + 6.4f * s, cy), 5f * s, 1f, tenue(oscuro))
    }

    /**
     * El sello de barro de la jarra: fija, la bulla apretada sobre su cordel, con el ouroboros impreso; sin fijar, solo el
     * cordel y el contorno punteado donde iría el sello, con el ouroboros apenas.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val c = Offset(size.width / 2, size.height / 2)
        val r = size.minDimension * 0.3f
        val cordel = if (oscuro) Color(0xFF8F8A80) else Color(0xFFA48D5E)
        drawLine(cordel.copy(alpha = if (fija) 1f else 0.45f), Offset(c.x - r * 1.5f, c.y + r * 0.4f), Offset(c.x + r * 1.5f, c.y - r * 0.15f), 1 * u, StrokeCap.Round)
        if (fija) bullaArc(c, r, oscuro, 23)
        else {
            val t = tenue(oscuro).copy(alpha = 0.55f)
            drawCircle(t, r, c, style = Stroke(1 * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 1.6f * u))))
            with(TrazosArcontes) { ouroboros(c, r * 0.42f, r * 0.17f, t.copy(alpha = 0.32f), -60f) }
        }
    }

    override val tamDibujoVacio: Dp = 46.dp
    override fun tintaVacio(oscuro: Boolean): Color = tenue(oscuro)

    /**
     * Un nicho vacío en la pared de caliza: la cara picada del muro, el nicho hondo de arco rebajado con su moldura tallada
     * alrededor, la sombra que deja el sol arriba a la izquierda y el umbral de piedra al pie. Una grieta finísima cruza la
     * esquina de arriba; de noche, por ella sale un hilo de luz.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 40 * u || h < 40 * u) return
        drawRect(if (oscuro) Color(0xFF181B2E) else Color(0xFFE6DCC0))
        picadoArc(Rect(0f, 0f, w, h), sombraTallado(oscuro).let { it.copy(alpha = it.alpha * 0.35f) }, e.semilla)
        val m = 9 * u
        val n = Rect(m, m, w - m, h - 6 * u)
        val flecha = minOf(n.height * 0.28f, n.width * 0.12f, 24 * u)
        val moldura = nichoArc(Rect(n.left - 3.5f * u, n.top - 3.5f * u, n.right + 3.5f * u, n.bottom), flecha + 1.5f * u)
        translate(0.6f * u, 0.6f * u) { drawPath(moldura, luzFilo(oscuro), style = Stroke(0.7f * u)) }
        drawPath(moldura, sombraTallado(oscuro), style = Stroke(0.7f * u))
        val nicho = nichoArc(n, flecha)
        drawPath(nicho, if (oscuro) Color(0xFF101223) else Color(0xFFD8CAA4))
        hundidoArc(nicho, 1.7f * u, Color.Black.copy(alpha = if (oscuro) 0.45f else 0.16f), luzFilo(oscuro))
        // El umbral de piedra.
        val umbral = Rect(n.left - 5 * u, n.bottom - 0.5f * u, n.right + 5 * u, h)
        drawRect(if (oscuro) piedra(true) else piedraLuz(false), umbral.topLeft, umbral.size)
        drawLine(luzFilo(oscuro), umbral.topLeft, Offset(umbral.right, umbral.top), 0.8f * u)
        drawLine(sombraTallado(oscuro), Offset(umbral.left, umbral.top + 1.2f * u), Offset(umbral.right, umbral.top + 1.2f * u), 0.5f * u)
        // La grieta finísima de la esquina: de día, solo la raja; de noche, un hilo de luz.
        val desde = Offset(w - 30 * u, 0f); val hasta = Offset(w - 4 * u, minOf(26 * u, h * 0.4f))
        if (oscuro) with(TrazosArcontes) { grieta(desde, hasta, 0.35f * u, 0.55f, e.semilla + 11) }
        else drawPath(Path().apply {
            moveTo(desde.x, desde.y)
            for (k in 1..5) {
                val t = k / 5f
                lineTo(desde.x + (hasta.x - desde.x) * t + (azar(e.semilla, k + 70) - 0.5f) * 3 * u, desde.y + (hasta.y - desde.y) * t)
            }
        }, sombraTallado(false).copy(alpha = 0.5f), style = Stroke(0.6f * u, join = StrokeJoin.Round))
    }

    /**
     * Lo vacío: la jarra de barro abierta, sin nada dentro. La boca oscura a la vista, el cordel del sello roto que cuelga del
     * cuello, la banda ondulada incisa del hombro, las dos asas y, en el suelo, la tapa caída de lado. De día, con el sol a la
     * izquierda; de noche, el barro a oscuras con el filo de la luna.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val s = size.minDimension
        fun f(v: Float) = v * s
        val claro = if (oscuro) mezcla(BARRO_CLARO, CIELO, 0.6f) else mezcla(BARRO_CLARO, Color.White, 0.15f)
        val medio = if (oscuro) mezcla(BARRO, CIELO, 0.6f) else BARRO_CLARO
        val hondo = if (oscuro) mezcla(BARRO, CIELO, 0.78f) else BARRO
        val cx = f(0.4f)
        // Las sombras en el suelo.
        drawOval(sombraPieza(oscuro), Offset(cx - f(0.22f), f(0.875f)), Size(f(0.44f), f(0.06f)))
        drawOval(sombraPieza(oscuro), Offset(f(0.66f), f(0.9f)), Size(f(0.3f), f(0.05f)))
        // La jarra.
        val jarra = Path().apply {
            moveTo(cx - f(0.07f), f(0.17f))
            cubicTo(cx - f(0.09f), f(0.22f), cx - f(0.2f), f(0.24f), cx - f(0.205f), f(0.36f))
            cubicTo(cx - f(0.215f), f(0.55f), cx - f(0.19f), f(0.74f), cx - f(0.075f), f(0.9f))
            lineTo(cx + f(0.075f), f(0.9f))
            cubicTo(cx + f(0.19f), f(0.74f), cx + f(0.215f), f(0.55f), cx + f(0.205f), f(0.36f))
            cubicTo(cx + f(0.2f), f(0.24f), cx + f(0.09f), f(0.22f), cx + f(0.07f), f(0.17f))
            close()
        }
        // Las asas, del cuello al hombro.
        listOf(-1f, 1f).forEach { l ->
            drawPath(Path().apply { moveTo(cx + l * f(0.07f), f(0.2f)); quadraticTo(cx + l * f(0.27f), f(0.17f), cx + l * f(0.185f), f(0.32f)) },
                if (l < 0) medio else hondo, style = Stroke(1.5f * u, cap = StrokeCap.Round))
        }
        drawPath(jarra, Brush.horizontalGradient(listOf(claro, medio, hondo), cx - f(0.2f), cx + f(0.2f)))
        clipPath(jarra) {
            // Las vueltas del torno y la banda ondulada del hombro.
            for (k in 0 until 4) drawLine(Color.Black.copy(alpha = 0.06f), Offset(cx - f(0.25f), f(0.48f + k * 0.1f)), Offset(cx + f(0.25f), f(0.48f + k * 0.1f)), 0.5f * u)
            drawPath(Path().apply {
                moveTo(cx - f(0.21f), f(0.37f))
                var x = -0.21f
                while (x < 0.21f) { x += 0.01f; lineTo(cx + f(x), f(0.37f + 0.014f * sin(x * 75f))) }
            }, Color.Black.copy(alpha = 0.32f), style = Stroke(0.6f * u))
            if (oscuro) drawPath(jarra, Color(0xFFB9C3E6).copy(alpha = 0.22f), style = Stroke(1.2f * u))
        }
        // El labio y la boca vacía, oscura.
        drawOval(claro, Offset(cx - f(0.1f), f(0.135f)), Size(f(0.2f), f(0.05f)))
        drawOval(Color(0xFF120C08).copy(alpha = if (oscuro) 0.85f else 0.8f), Offset(cx - f(0.075f), f(0.143f)), Size(f(0.15f), f(0.032f)))
        // El cordel del sello, roto, que cuelga.
        val cordel = if (oscuro) Color(0xFF7D786E) else Color(0xFF9C8458)
        drawPath(Path().apply { moveTo(cx - f(0.06f), f(0.19f)); quadraticTo(cx - f(0.16f), f(0.22f), cx - f(0.13f), f(0.31f)) }, cordel, style = Stroke(0.8f * u, cap = StrokeCap.Round))
        drawLine(cordel, Offset(cx - f(0.13f), f(0.31f)), Offset(cx - f(0.145f), f(0.335f)), 0.5f * u)
        drawLine(cordel, Offset(cx - f(0.13f), f(0.31f)), Offset(cx - f(0.115f), f(0.338f)), 0.5f * u)
        // La tapa caída de lado.
        val pt = Offset(f(0.81f), f(0.86f))
        rotate(-24f, pt) {
            drawArc(Brush.horizontalGradient(listOf(claro, hondo), pt.x - f(0.13f), pt.x + f(0.13f)), 180f, 180f, true, Offset(pt.x - f(0.13f), pt.y - f(0.09f)), Size(f(0.26f), f(0.18f)))
            drawRect(hondo, Offset(pt.x - f(0.14f), pt.y - 0.2f * u), Size(f(0.28f), 1.2f * u))
            drawCircle(medio, f(0.025f), Offset(pt.x, pt.y - f(0.1f)))
        }
    }

    override val margenAviso: Dp = 34.dp
    override fun tintaAviso(oscuro: Boolean): Color = tinta(oscuro)

    /**
     * El aviso es una hoja de papiro junto a la jamba de caliza: en la jamba, un nicho pequeño tallado y, dentro, según el
     * aviso, la lámpara encendida (su luz cálida se derrama sobre la piedra y la hoja) o la grieta de luz en el fondo del nicho.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 40 * u || h < 20 * u) return
        val forma = losaArc(Rect(0f, 0f, w, h), minOf(6 * u, h / 2), 1.5f * u)
        val hoja = if (oscuro) papiro(true) else mezcla(papiro(false), LINO, 0.45f)
        drawPath(forma, hoja)
        val banda = 25 * u
        clipPath(forma) {
            fibrasArc(Rect(banda, 0f, w, h), fibra(oscuro), e.semilla + 5, 0.55f)
            drawRect(piedra(oscuro), Offset.Zero, Size(banda, h))
            drawRect(Brush.verticalGradient(listOf(luzCara(oscuro), Color.Transparent), 0f, h), Offset.Zero, Size(banda, h))
            picadoArc(Rect(0f, 0f, banda, h), sombraTallado(oscuro).let { it.copy(alpha = it.alpha * 0.35f) }, e.semilla)
            drawLine(sombraTallado(oscuro), Offset(banda, 0f), Offset(banda, h), 0.9f * u)
            drawLine(luzFilo(oscuro), Offset(banda - 0.8f * u, 0f), Offset(banda - 0.8f * u, h), 0.6f * u)
        }
        drawPath(forma, (if (oscuro) Color(0xFF343A5A) else Color(0xFFBBA673)).copy(alpha = 0.8f), style = Stroke(0.8f * u))
        // El nicho de la jamba.
        val n = Rect(4.5f * u, 5 * u, 20.5f * u, minOf(h - 5 * u, 36 * u))
        if (n.height < 10 * u) return
        val nicho = nichoArc(n, 7.5f * u)
        drawPath(nicho, hueco(oscuro))
        hundidoArc(nicho, 1 * u, Color.Black.copy(alpha = if (oscuro) 0.5f else 0.28f), luzFilo(oscuro))
        with(TrazosArcontes) {
            if (e.semilla % 2 == 0) {
                val c = Offset(n.center.x - 0.9f * u, n.bottom - 2.6f * u)
                val l = Offset(c.x + 5 * u, c.y - 5 * u)
                drawCircle(Brush.radialGradient(listOf(LLAMA_HALO.copy(alpha = if (oscuro) 0.3f else 0.45f), LLAMA_HALO.copy(alpha = 0f)), l, 30 * u), 30 * u, l)
                lampara(c, 14 * u, BARRO_CLARO, 1f)
            } else clipPath(nicho) {
                grieta(Offset(n.center.x + 0.4f * u, n.top + 3 * u), Offset(n.center.x - 0.5f * u, n.bottom - 1.5f * u), 0.55f * u, 1f, e.semilla)
            }
        }
    }

    override val margenCifra: Dp = 11.dp
    override fun tintaCifra(oscuro: Boolean): Color = tinta(oscuro)
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) lago(true) else ocre(false)

    /**
     * Un sillar de caliza como los de entonces: el marco liso labrado a cincel dentado, con sus rayitas, y el centro picado
     * a punta que sobresale un poco, con la luz en su canto de arriba y de la izquierda y la sombra en los otros dos.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 20 * u || h < 20 * u) return
        val r = Rect(0f, 0f, w, h - 1.4f * u)
        drawRoundRect(sombraPieza(oscuro), Offset(0.8f * u, 1.4f * u), r.size, CornerRadius(2 * u))
        drawRoundRect(piedra(oscuro), r.topLeft, r.size, CornerRadius(2 * u))
        val m = 3.4f * u
        peinadoArc(r, m, sombraTallado(oscuro).let { it.copy(alpha = it.alpha * 0.7f) }, e.semilla)
        val jiba = r.deflate(m)
        drawRoundRect(if (oscuro) mezcla(piedra(true), piedraLuz(true), 0.5f) else mezcla(piedra(false), piedraLuz(false), 0.6f), jiba.topLeft, jiba.size, CornerRadius(1.2f * u))
        picadoArc(jiba, sombraTallado(oscuro).let { it.copy(alpha = it.alpha * 0.32f) }, e.semilla + 7)
        drawLine(luzFilo(oscuro), jiba.topLeft, Offset(jiba.right, jiba.top), 0.8f * u)
        drawLine(luzFilo(oscuro), jiba.topLeft, Offset(jiba.left, jiba.bottom), 0.8f * u)
        drawLine(sombraTallado(oscuro), Offset(jiba.left, jiba.bottom), jiba.bottomRight, 0.8f * u)
        drawLine(sombraTallado(oscuro), Offset(jiba.right, jiba.top), jiba.bottomRight, 0.8f * u)
        drawLine(luzFilo(oscuro), Offset(2 * u, 0.6f * u), Offset(w - 2 * u, 0.6f * u), 0.7f * u)
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 12.dp

    /**
     * El fondo donde va el texto de cada tarjeta: el fondo del nicho para la cifra; el papiro para lo que falta; el cielo
     * antiguo (oscuro también de día) para la noche; el lino de los rollos para Vida.
     */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> if (oscuro) Color(0xFF161A2D) else Color(0xFFE2D6B6)
        TipoTarjeta.PENDIENTE -> if (oscuro) Color(0xFF1B1E32) else Color(0xFFEBDDB4)
        TipoTarjeta.NOCHE -> if (oscuro) Color(0xFF0D1024) else Color(0xFF151832)
        TipoTarjeta.VIDA -> if (oscuro) Color(0xFF171A2C) else Color(0xFFF1E9D5)
    }

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> tinta(oscuro)
        TipoTarjeta.PENDIENTE -> tinta(oscuro)
        TipoTarjeta.NOCHE -> Color(0xFFECE4CF)
        TipoTarjeta.VIDA -> tinta(oscuro)
    }

    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> tenue(oscuro)
        TipoTarjeta.PENDIENTE -> tenue(oscuro)
        TipoTarjeta.NOCHE -> Color(0xFFA9A6C0)
        TipoTarjeta.VIDA -> tenue(oscuro)
    }

    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> if (oscuro) lago(true) else ocre(false)
        TipoTarjeta.PENDIENTE -> ocre(oscuro)
        TipoTarjeta.NOCHE -> violeta(true)
        TipoTarjeta.VIDA -> lago(oscuro)
    }

    /**
     * Las tarjetas de la casa del salador. La cifra de Hoy es un **nicho** en la pared: la jamba de caliza con su arco
     * rebajado, el hueco con la sombra del sol y la repisa de piedra al pie (donde se apoyan las barras); destacada, el hueco
     * se marca y la jamba derecha se agrieta con su luz. «Te falta hoy» es la **hoja de papiro** con sus fibras y la lámpara
     * encendida en la esquina, porque aún se trabaja; completa, la lámpara se apaga con su hilo de humo y cae el sello de
     * barro. La noche es el **cielo antiguo** con el diagrama de las siete esferas de los ofitas (recurso), su marca en cada
     * una y la serpiente que lo rodea, entre estrellas; sellada, se encienden las siete luces y se abre la grieta. Vida es el
     * **lino** de los rollos, con su tejido, el recuadro de hilo azul y el fleco al pie. Al tocarlas se oscurecen.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        if (size.width < 24 * u || size.height < 24 * u) return
        val base = fondoTarjeta(e.tipo, oscuro).let { if (e.presionado) mezcla(it, if (oscuro) Color.Black else Color(0xFF8C7A58), 0.14f) else it }
        when (e.tipo) {
            TipoTarjeta.METRICA -> tarjetaNicho(e, base, oscuro)
            TipoTarjeta.PENDIENTE -> tarjetaPapiro(e, base, oscuro)
            TipoTarjeta.NOCHE -> tarjetaCielo(e, base)
            TipoTarjeta.VIDA -> tarjetaLino(e, base, oscuro)
        }
    }

    /** El nicho de la cifra de Hoy (ver [tarjeta]). */
    private fun DrawScope.tarjetaNicho(e: EstadoTarjeta, base: Color, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val flecha = minOf(13 * u, h * 0.16f, w * 0.12f)
        val marco = nichoArc(Rect(0f, 0f, w, h - 1.6f * u), flecha)
        translate(0.8f * u, 1.6f * u) { drawPath(marco, sombraPieza(oscuro)) }
        drawPath(marco, if (e.presionado) mezcla(piedraLuz(oscuro), Color.Black, 0.08f) else piedraLuz(oscuro))
        clipPath(marco) {
            drawRect(Brush.verticalGradient(listOf(luzCara(oscuro), Color.Transparent), 0f, h * 0.5f))
            picadoArc(Rect(0f, 0f, w, h), sombraTallado(oscuro).let { it.copy(alpha = it.alpha * 0.3f) }, e.semilla + 13)
        }
        val j = 5 * u
        val dentro = Rect(j, j, w - j, h - 6.5f * u)
        val hueco = nichoArc(dentro, maxOf(flecha - 1.5f * u, 0f))
        drawPath(hueco, base)
        hundidoArc(hueco, 2 * u, Color.Black.copy(alpha = if (oscuro) 0.5f else 0.2f), luzFilo(oscuro))
        // La repisa de piedra.
        val rep = Rect(j - 1.5f * u, h - 6.5f * u, w - j + 1.5f * u, h - 2.4f * u)
        drawRect(piedra(oscuro), rep.topLeft, rep.size)
        drawLine(luzFilo(oscuro), rep.topLeft, Offset(rep.right, rep.top), 0.9f * u)
        drawLine(sombraTallado(oscuro), Offset(rep.left, rep.bottom), rep.bottomRight, 0.8f * u)
        drawPath(marco, (if (oscuro) Color(0xFF343A5A) else Color(0xFFBBA673)).copy(alpha = 0.7f), style = Stroke(0.7f * u))
        if (e.destacado) {
            drawPath(hueco, acentoTarjeta(e.tipo, oscuro).copy(alpha = 0.8f), style = Stroke(1.2f * u))
            clipPath(marco) {
                val desde = Offset(w - j / 2 - 0.3f * u, h * 0.16f); val hasta = Offset(w - j / 2 + 0.4f * u, h * 0.64f)
                drawLine(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.22f), desde, hasta, 2.2f * u, StrokeCap.Round)
                with(TrazosArcontes) { grieta(desde, hasta, 0.75f * u, 1f, e.semilla + 29) }
            }
        }
    }

    /** La hoja de papiro de «Te falta hoy» (ver [tarjeta]). */
    private fun DrawScope.tarjetaPapiro(e: EstadoTarjeta, base: Color, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El canto de arriba y el de abajo, deshilachados como los de una hoja de verdad.
        val n = (w / (7 * u)).toInt().coerceIn(4, 120)
        val hoja = Path().apply {
            moveTo(0f, 1.6f * u)
            for (k in 1..n) lineTo(w * k / n, (0.6f + 1.8f * azar(e.semilla, k)) * u)
            lineTo(w, h - 1.6f * u)
            for (k in n - 1 downTo 0) lineTo(w * k / n, h - (0.6f + 1.8f * azar(e.semilla, k + 200)) * u)
            close()
        }
        translate(0.6f * u, 1.4f * u) { drawPath(hoja, sombraPieza(oscuro)) }
        drawPath(hoja, base)
        clipPath(hoja) { fibrasArc(Rect(0f, 0f, w, h), fibra(oscuro), e.semilla + 31) }
        drawPath(hoja, fibra(oscuro).copy(alpha = 0.45f), style = Stroke(0.7f * u, join = StrokeJoin.Round))
        val lw = 30 * u
        val c = Offset(w - 24 * u, 17 * u)
        with(TrazosArcontes) {
            drawOval(sombraPieza(oscuro), Offset(c.x - lw * 0.38f, c.y + lw * 0.13f), Size(lw * 0.7f, 3.2f * u))
            if (!e.destacado) {
                // Mientras falta algo, la lámpara arde en la esquina.
                val l = Offset(c.x + lw * 0.39f, c.y - lw * 0.3f)
                drawCircle(Brush.radialGradient(listOf(LLAMA_HALO.copy(alpha = if (oscuro) 0.3f else 0.5f), LLAMA_HALO.copy(alpha = 0f)), l, 56 * u), 56 * u, l)
                lampara(c, lw, BARRO_CLARO, 1f)
            } else {
                // Completa: la lámpara apagada, con su hilo de humo, y el sello de barro.
                lampara(c, lw, barroLampara(false, oscuro), 0f)
                val p = Offset(c.x + lw * 0.39f, c.y - lw * 0.36f * 0.45f)
                drawPath(Path().apply {
                    moveTo(p.x, p.y); cubicTo(p.x - 3 * u, p.y - 3 * u, p.x + 3 * u, p.y - 5 * u, p.x - 0.5f * u, p.y - 9 * u)
                }, tenue(oscuro).copy(alpha = 0.4f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
                bullaArc(if (h > 64 * u) Offset(w - 16 * u, h - 15 * u) else Offset(c.x - lw * 0.4f - 13 * u, c.y + 2 * u), 7.5f * u, oscuro, e.semilla + 3)
            }
        }
        if (e.destacado) drawPath(hoja, ocre(oscuro).copy(alpha = 0.75f), style = Stroke(1.3f * u, join = StrokeJoin.Round))
    }

    /** El cielo antiguo de la noche (ver [tarjeta]). */
    private fun DrawScope.tarjetaCielo(e: EstadoTarjeta, base: Color) {
        val u = density
        val w = size.width; val h = size.height
        val sellado = e.destacado
        val forma = losaArc(Rect(0f, 0f, w, h), 6 * u, 1.5f * u)
        drawPath(forma, base)
        val c = Offset(w - 52 * u, h / 2)
        val r = maxOf(h * 0.92f, 64 * u)
        clipPath(forma) {
            // Las estrellas.
            val n = (w * h / (230f * u * u)).toInt().coerceIn(8, 90)
            for (k in 0 until n) drawCircle(LINO.copy(alpha = 0.2f + 0.5f * azar(e.semilla + 61, k)), (0.3f + 0.55f * azar(e.semilla + 62, k)) * u,
                Offset(w * azar(e.semilla + 63, k), h * azar(e.semilla + 64, k)))
            // La luz de lo alto en el centro del diagrama, las siete esferas y la serpiente que las rodea.
            drawCircle(Brush.radialGradient(listOf(GRIETA_HALO.copy(alpha = if (sellado) 0.2f else 0.1f), GRIETA_HALO.copy(alpha = 0f)), c, r * 0.5f), r * 0.5f, c)
            with(TrazosArcontes) {
                esferas(c, r, ESFERA.copy(alpha = 0.55f), 0.7f * u)
                ouroboros(c, r * 1.08f, 2.8f * u, ESFERA.copy(alpha = 0.5f), 168f)
                if (sellado) {
                    for (i in 0 until 7) {
                        val ri = r * (0.32f + 0.68f * i / 6f)
                        val a = Math.toRadians(i * 51.0)
                        chispa(Offset(c.x + ri * cos(a).toFloat(), c.y + ri * sin(a).toFloat()), 2.6f * u, 0.95f)
                    }
                    if (h > 60 * u) grieta(Offset(w - 15 * u, 1.5f * u), Offset(w - 11 * u, minOf(17 * u, h * 0.24f)), 0.55f * u, 1f, e.semilla + 41)
                }
            }
        }
        drawPath(forma, if (sellado) violeta(true).copy(alpha = 0.8f) else Color(0xFF343A5A), style = Stroke((if (sellado) 1.3f else 1f) * u))
    }

    /** El lino de los rollos de Vida (ver [tarjeta]). */
    private fun DrawScope.tarjetaLino(e: EstadoTarjeta, base: Color, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val tela = h - 3 * u
        val forma = losaArc(Rect(0f, 0f, w, tela), 5 * u, 1 * u)
        translate(0.6f * u, 1.4f * u) { drawPath(forma, sombraPieza(oscuro)) }
        drawPath(forma, base)
        clipPath(forma) {
            // El tejido: la urdimbre y la trama, con algún hilo más grueso.
            val hilo = if (oscuro) Color.White.copy(alpha = 0.035f) else Color(0xFF8A7A55).copy(alpha = 0.09f)
            val p = Path()
            var x = 0.7f * u; var k = 0
            while (x < w && k < 400) { p.moveTo(x, 0f); p.lineTo(x, tela); x += 1.5f * u; k++ }
            var y = 0.7f * u; k = 0
            while (y < tela && k < 300) { p.moveTo(0f, y); p.lineTo(w, y); y += 1.5f * u; k++ }
            drawPath(p, hilo, style = Stroke(0.45f * u))
            for (j in 0 until 4) {
                val yy = tela * azar(e.semilla + 71, j)
                drawLine(hilo.copy(alpha = hilo.alpha * 2f), Offset(0f, yy), Offset(w, yy), 0.9f * u)
            }
        }
        // El recuadro de hilo azul, doble.
        val azul = lago(oscuro).copy(alpha = if (oscuro) 0.5f else 0.68f)
        val r1 = Rect(4 * u, 4 * u, w - 4 * u, tela - 4 * u)
        if (r1.width > 8 * u && r1.height > 8 * u) {
            drawRect(azul, r1.topLeft, r1.size, style = Stroke(0.9f * u))
            val r2 = r1.deflate(1.9f * u)
            drawRect(azul.copy(alpha = azul.alpha * 0.7f), r2.topLeft, r2.size, style = Stroke(0.5f * u))
        }
        // El fleco.
        val fleco = if (oscuro) mezcla(base, LINO, 0.25f) else mezcla(base, Color(0xFF8A7A55), 0.35f)
        val pf = Path()
        var x = 3 * u; var k = 0
        while (x < w - 3 * u && k < 300) {
            pf.moveTo(x, tela - 0.4f * u); pf.lineTo(x + (azar(e.semilla + 81, k) - 0.5f) * 0.8f * u, h - (0.3f + 1.2f * azar(e.semilla + 82, k)) * u)
            x += 1.7f * u; k++
        }
        drawPath(pf, fleco, style = Stroke(0.6f * u, cap = StrokeCap.Round))
        drawPath(forma, (if (oscuro) Color(0xFF343A5A) else Color(0xFFBBA673)).copy(alpha = 0.6f), style = Stroke(0.6f * u))
    }

    // ---------------------------------------------------------------- Gastos

    /** La letra de los rótulos: Caudex, la de los códices. */
    private val letraRotulos: FontFamily = LetraExtra.caudex

    override val altoBalanza: Dp = 62.dp

    /**
     * El dintel de caliza con dos nichos, uno al lado del otro, separados por una pilastra: a la izquierda el gasto, a la
     * derecha el ingreso. El elegido tiene su lámpara encendida en el umbral: su luz cálida llena el hueco, el hueco se marca
     * de su color y la letra va en negrita; el otro queda a la sombra, con la lámpara apagada y la letra tenue.
     */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 90 * u || h < 36 * u) return
        val dintel = Rect(0f, 0f, w, h - 1.4f * u)
        drawRoundRect(sombraPieza(oscuro), Offset(0.6f * u, 1.4f * u), dintel.size, CornerRadius(2 * u))
        drawRoundRect(piedraLuz(oscuro), dintel.topLeft, dintel.size, CornerRadius(2 * u))
        drawRect(Brush.verticalGradient(listOf(luzCara(oscuro), Color.Transparent), 0f, dintel.bottom), dintel.topLeft, dintel.size)
        peinadoArc(dintel, 3 * u, sombraTallado(oscuro).let { it.copy(alpha = it.alpha * 0.6f) }, 17)
        val xm = w / 2
        // La pilastra del medio: el fuste con sus dos estrías, el capitel y la basa.
        val pil = Rect(xm - 3.5f * u, 7 * u, xm + 3.5f * u, h - 8 * u)
        drawRect(piedra(oscuro), pil.topLeft, pil.size)
        listOf(-1.2f, 1.2f).forEach { d -> drawLine(sombraTallado(oscuro), Offset(xm + d * u, pil.top + 3 * u), Offset(xm + d * u, pil.bottom - 2 * u), 0.6f * u) }
        drawRect(piedra(oscuro), Offset(xm - 5 * u, pil.top - 1.5f * u), Size(10 * u, 2.4f * u))
        drawLine(sombraTallado(oscuro), Offset(xm - 5 * u, pil.top + 0.9f * u), Offset(xm + 5 * u, pil.top + 0.9f * u), 0.5f * u)
        val umbralY = h - 8 * u
        for (lado in 0..1) {
            val ingreso = lado == 1
            val elegida = e.ingreso == ingreso
            val r = if (ingreso) Rect(xm + 8 * u, 6 * u, w - 6 * u, umbralY) else Rect(6 * u, 6 * u, xm - 8 * u, umbralY)
            if (r.width < 30 * u) continue
            val flecha = minOf(9 * u, r.height * 0.3f)
            val n = nichoArc(r, flecha)
            val fondo = if (elegida) (if (oscuro) Color(0xFF1F1F33) else Color(0xFFF3E7C6)) else (if (oscuro) Color(0xFF111325) else Color(0xFFCDBF99))
            drawPath(n, fondo)
            val lp = Offset(r.left + 12 * u, r.bottom - 3.6f * u)
            if (elegida) clipPath(n) {
                val l = Offset(lp.x + 7 * u, lp.y - 6 * u)
                val alcance = if (oscuro) 58 * u else r.width * 0.8f
                drawCircle(Brush.radialGradient(listOf(LLAMA_HALO.copy(alpha = if (oscuro) 0.34f else 0.45f), LLAMA_HALO.copy(alpha = 0f)), l, alcance), alcance, l)
            }
            hundidoArc(n, 1.6f * u, Color.Black.copy(alpha = if (oscuro) 0.5f else (if (elegida) 0.16f else 0.26f)), luzFilo(oscuro))
            val color = if (ingreso) e.colorIngreso else e.colorGasto
            if (elegida) drawPath(n, color.copy(alpha = 0.85f), style = Stroke(1.3f * u))
            with(TrazosArcontes) { lampara(lp, 19 * u, barroLampara(elegida, oscuro), if (elegida) 1f else 0f) }
            val texto = if (ingreso) e.textoIngreso else e.textoGasto
            textoEn(medidor, texto, Rect(r.left + 24 * u, r.top + flecha * 0.5f, r.right - 6 * u, r.bottom - 2 * u), letraRotulos,
                legibleArc(color, fondo, tenue = !elegida), if (elegida) 18f else 15.5f, peso = if (elegida) FontWeight.Bold else FontWeight.Normal)
        }
        // El umbral corrido de los dos nichos.
        drawLine(luzFilo(oscuro), Offset(4 * u, umbralY + 0.6f * u), Offset(w - 4 * u, umbralY + 0.6f * u), 0.9f * u)
        drawLine(sombraTallado(oscuro), Offset(4 * u, umbralY + 2 * u), Offset(w - 4 * u, umbralY + 2 * u), 0.6f * u)
    }

    override val altoMes: Dp = 52.dp

    /**
     * El rollo de papiro abierto con el nombre del mes: la hoja con sus fibras y, en cada punta, la parte enrollada con sus
     * vueltas y las perillas de madera de su varilla; sobre la hoja, junto a cada rollo, las cuñas de tinta que llevan al mes
     * anterior y al siguiente.
     */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 140 * u || h < 30 * u) return
        val hoja = Rect(16 * u, 7 * u, w - 16 * u, h - 7 * u)
        val pap = papiro(oscuro)
        drawRect(sombraPieza(oscuro), Offset(hoja.left, hoja.top + 1.4f * u), hoja.size)
        drawRect(pap, hoja.topLeft, hoja.size)
        clipRect(hoja.left, hoja.top, hoja.right, hoja.bottom) { fibrasArc(hoja, fibra(oscuro), 37) }
        drawLine(fibra(oscuro).copy(alpha = 0.4f), hoja.topLeft, Offset(hoja.right, hoja.top), 0.6f * u)
        drawLine(fibra(oscuro).copy(alpha = 0.4f), Offset(hoja.left, hoja.bottom), hoja.bottomRight, 0.6f * u)
        // Los dos rollos, con sus vueltas y las perillas de la varilla.
        val madera = if (oscuro) Color(0xFF3A2C24) else Color(0xFF6B4A2E)
        val rolloC = if (oscuro) mezcla(pap, LINO, 0.06f) else mezcla(pap, Color(0xFF9C7F44), 0.18f)
        listOf(hoja.left, hoja.right).forEach { x ->
            val r = Rect(x - 6 * u, 4 * u, x + 6 * u, h - 4 * u)
            drawCircle(madera, 1.9f * u, Offset(x, 2.6f * u)); drawCircle(madera, 1.9f * u, Offset(x, h - 2.6f * u))
            drawRoundRect(Brush.horizontalGradient(listOf(mezcla(rolloC, Color.Black, 0.3f), mezcla(rolloC, Color.White, if (oscuro) 0.05f else 0.25f), rolloC,
                mezcla(rolloC, Color.Black, 0.35f)), r.left, r.right), r.topLeft, r.size, CornerRadius(3 * u))
            listOf(-2.4f, 0.6f, 3.2f).forEach { d -> drawLine(fibra(oscuro).copy(alpha = 0.35f), Offset(x + d * u, r.top + 1 * u), Offset(x + d * u, r.bottom - 1 * u), 0.5f * u) }
        }
        val t = tenue(oscuro)
        cunasArc(Offset(30 * u, h / 2), 5 * u, -1f, t)
        cunasArc(Offset(w - 30 * u, h / 2), 5 * u, 1f, t)
        textoEn(medidor, e.titulo, Rect(48 * u, hoja.top + 2 * u, w - 48 * u, hoja.bottom - 2 * u), letraRotulos, legibleArc(tinta(oscuro), pap, minimo = 7f), 18f,
            peso = FontWeight.Bold, espaciado = 0.4f)
    }

    /**
     * Un renglón del libro de cuentas: el surco tallado al pie y, al principio, la moneda pequeña de bronce: entera y del color
     * del ocre si entra; solo su contorno, en el azul del lago, si sale.
     */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 30 * u || h < 10 * u) return
        surcoArc(10 * u, w - 2 * u, h - 1.4f * u, oscuro, 0.55f)
        val p = Offset(4.2f * u, h - 2.8f * u)
        if (e.ingreso) {
            drawCircle(ocre(oscuro), 2.4f * u, p)
            drawCircle(Color.Black.copy(alpha = 0.3f), 2.4f * u, p, style = Stroke(0.5f * u))
            drawCircle(Color.White.copy(alpha = 0.35f), 0.8f * u, p - Offset(0.7f * u, 0.7f * u))
            drawCircle(Color.Black.copy(alpha = 0.3f), 0.6f * u, p)
        } else {
            drawCircle(lago(oscuro).copy(alpha = 0.85f), 2.3f * u, p, style = Stroke(0.8f * u))
            drawCircle(lago(oscuro).copy(alpha = 0.6f), 0.6f * u, p)
        }
    }

    override val altoRotuloDia: Dp = 22.dp

    /**
     * La fecha en la etiqueta de papiro que se ata a las jarras: la etiqueta de esquinas cortadas, con su agujero y el cordel
     * que sale a la izquierda, la fecha en Caudex (con mayúscula solo al principio, como en los códices) y, detrás, el surco
     * tallado que sigue hasta el borde.
     */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 60 * u || h < 12 * u) return
        val fecha = texto.lowercase().replaceFirstChar { it.uppercase() }
        val tam = 12.5f; val esp = 0.5f
        val ancho = minOf(anchoArc(medidor, fecha, letraRotulos, tam, FontWeight.Normal, esp), w * 0.62f)
        val et = Rect(8 * u, 2.5f * u, 8 * u + ancho + 17 * u, h - 2.5f * u)
        val k = 3 * u
        val forma = Path().apply {
            moveTo(et.left + k, et.top); lineTo(et.right, et.top); lineTo(et.right, et.bottom); lineTo(et.left + k, et.bottom)
            lineTo(et.left, et.bottom - k); lineTo(et.left, et.top + k); close()
        }
        val pap = papiro(oscuro)
        val cordel = if (oscuro) Color(0xFF7D786E) else Color(0xFF9C8458)
        val agujero = Offset(et.left + 3.6f * u, h / 2)
        drawPath(Path().apply { moveTo(0f, h / 2 + 3.5f * u); quadraticTo(et.left - 3 * u, h / 2 - 3 * u, agujero.x, agujero.y) }, cordel, style = Stroke(0.8f * u, cap = StrokeCap.Round))
        translate(0.5f * u, 1 * u) { drawPath(forma, sombraPieza(oscuro)) }
        drawPath(forma, pap)
        clipPath(forma) { fibrasArc(et, fibra(oscuro), texto.length + 41, 0.8f) }
        drawPath(forma, fibra(oscuro).copy(alpha = 0.55f), style = Stroke(0.6f * u))
        drawCircle(if (oscuro) CIELO else Color(0xFF5C4A2A), 1.1f * u, agujero)
        drawCircle(cordel, 1.1f * u, agujero, style = Stroke(0.5f * u))
        textoEn(medidor, fecha, Rect(et.left + 8 * u, et.top + 1 * u, et.left + 8 * u + ancho + 2 * u, et.bottom - 1 * u), letraRotulos,
            legibleArc(tinta(oscuro), pap), tam, espaciado = esp)
        if (et.right + 16 * u < w) surcoArc(et.right + 6 * u, w - 4 * u, h / 2, oscuro, 0.7f)
    }

    // ---------------------------------------------------------------- adornos de la hoja rápida, las relaciones y la cinta

    /**
     * La hoja rápida es la losa de caliza con su grieta. Una moldura tallada sigue el borde por dentro, con las esquinas de
     * arriba en arco, y se corta donde la piedra se abre: ahí la luz blanca y violeta baña la piedra, ilumina los labios de la
     * grieta y sigue, finísima, un poco más abajo de su punta. Arriba, a la izquierda, el ouroboros con la chispa y las siete
     * luces en fila; a la derecha, las siete esferas. Por los dos cantos, la jamba labrada a cincel; al pie, la lámpara
     * encendida y el códice cerrado con su correa.
     */
    override fun DrawScope.adornoQuick(e: EstadoAdorno, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (w < 160 * u || h < 120 * u) return
        val traza = if (oscuro) e.secundario.copy(alpha = e.secundario.alpha * 0.42f) else e.primario.copy(alpha = e.primario.alpha * 0.5f)
        val fino = traza.copy(alpha = traza.alpha * 0.5f)
        val luz = luzFilo(oscuro)
        val gx = w * 0.72f
        // La luz de la grieta sobre la piedra.
        val cg = Offset(gx, 7 * u)
        drawCircle(Brush.radialGradient(listOf(GRIETA_HALO.copy(alpha = if (oscuro) 0.3f else 0.24f), GRIETA_HALO.copy(alpha = 0f)), cg, 28 * u), 28 * u, cg)
        // La moldura, cortada en la grieta.
        val m = 4.5f * u; val r = 9.5f * u
        val moldura = Path().apply {
            moveTo(gx - 8 * u, m)
            lineTo(m + r, m); quadraticTo(m, m, m, m + r); lineTo(m, h - m); lineTo(w - m, h - m); lineTo(w - m, m + r); quadraticTo(w - m, m, w - m - r, m)
            lineTo(gx + 8 * u, m)
        }
        translate(0.6f * u, 0.6f * u) { drawPath(moldura, luz, style = Stroke(0.6f * u)) }
        drawPath(moldura, traza, style = Stroke(0.8f * u))
        // Los labios de la grieta, con la luz, y la raja finísima que sigue bajo su punta.
        val izq = Path().apply { moveTo(gx - 3.2f * u, 0f); lineTo(gx - 1.3f * u, 4.5f * u); lineTo(gx - 2.3f * u, 8f * u); lineTo(gx - 0.6f * u, 12.5f * u) }
        val der = Path().apply { moveTo(gx + 3.1f * u, 0f); lineTo(gx + 2.1f * u, 4.8f * u); lineTo(gx + 1.3f * u, 8.4f * u); lineTo(gx + 1f * u, 12.5f * u) }
        listOf(izq, der).forEach { p ->
            drawPath(p, GRIETA_HALO.copy(alpha = 0.3f), style = Stroke(2.2f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(p, GRIETA.copy(alpha = 0.85f), style = Stroke(0.7f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        val sigue = Path().apply { moveTo(gx + 0.2f * u, 12f * u); lineTo(gx - 0.5f * u, 14.6f * u); lineTo(gx + 0.5f * u, 16.4f * u); lineTo(gx - 0.1f * u, 17.8f * u) }
        drawPath(sigue, GRIETA_HALO.copy(alpha = 0.38f), style = Stroke(2.6f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(sigue, GRIETA.copy(alpha = 0.95f), style = Stroke(0.8f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
        // Dos rajitas que se abren hacia los lados, en la piedra.
        val raja = sombraTallado(oscuro)
        drawPath(Path().apply { moveTo(gx - 2 * u, 6 * u); lineTo(gx - 6 * u, 8.6f * u); lineTo(gx - 9 * u, 8 * u) }, raja, style = Stroke(0.5f * u, join = StrokeJoin.Round))
        drawPath(Path().apply { moveTo(gx + 1.6f * u, 9.5f * u); lineTo(gx + 5 * u, 11 * u); lineTo(gx + 6.5f * u, 13.5f * u) }, raja, style = Stroke(0.5f * u, join = StrokeJoin.Round))
        // Arriba a la izquierda: el ouroboros con la chispa y las siete luces; a la derecha, las siete esferas.
        val yb = 12 * u
        with(TrazosArcontes) {
            ouroboros(Offset(28 * u, yb), 5.6f * u, 2.2f * u, traza.copy(alpha = minOf(1f, traza.alpha * 1.5f)), -60f)
            chispa(Offset(28 * u, yb), 2.8f * u, 0.95f, if (oscuro) GRIETA else violeta(false), GRIETA_HALO)
            esferas(Offset(w - 28 * u, yb), 6.8f * u, e.secundario.copy(alpha = e.secundario.alpha * 0.65f), 0.5f * u)
        }
        for (k in 0 until 7) drawCircle(traza.copy(alpha = minOf(1f, traza.alpha * 1.3f)), (1.5f - k * 0.1f) * u, Offset(42 * u + k * 7 * u, yb))
        val x0 = 42 * u + 6 * 7 * u + 6 * u
        if (gx - 13 * u > x0) drawLine(fino, Offset(x0, yb), Offset(gx - 13 * u, yb), 0.7f * u)
        if (w - 39 * u > gx + 13 * u) drawLine(fino, Offset(gx + 13 * u, yb), Offset(w - 39 * u, yb), 0.7f * u)
        // Las jambas de los dos cantos: la segunda raya y el labrado a cincel entre las dos.
        val y0 = 28 * u; val y1 = h - 28 * u
        if (y1 > y0) {
            val cincel = Path()
            listOf(m, w - m - 4.5f * u).forEach { x ->
                var y = y0; var k = 0
                while (y < y1 && k < 600) { cincel.moveTo(x + 0.8f * u, y); cincel.lineTo(x + 3.7f * u, y + 0.3f * u); y += maxOf(2.2f * u, 1f); k++ }
            }
            drawPath(cincel, fino.copy(alpha = fino.alpha * 0.8f), style = Stroke(0.45f * u))
            drawLine(traza, Offset(m + 4.5f * u, y0), Offset(m + 4.5f * u, y1), 0.7f * u)
            drawLine(traza, Offset(w - m - 4.5f * u, y0), Offset(w - m - 4.5f * u, y1), 0.7f * u)
        }
        // Al pie: la lámpara encendida, el códice cerrado y, entre los dos, el surco con sus tres luces.
        val lp = Offset(28 * u, h - 10 * u)
        drawCircle(Brush.radialGradient(listOf(LLAMA_HALO.copy(alpha = if (oscuro) 0.26f else 0.3f), LLAMA_HALO.copy(alpha = 0f)), Offset(lp.x + 6 * u, lp.y - 4 * u), 30 * u),
            30 * u, Offset(lp.x + 6 * u, lp.y - 4 * u))
        with(TrazosArcontes) { lampara(lp, 23 * u, BARRO_CLARO, 1f) }
        val cod = Rect(w - 42 * u, h - 16 * u, w - 20 * u, h - 6.5f * u)
        val cuero = if (oscuro) mezcla(CUERO, CIELO, 0.3f) else CUERO
        drawRoundRect(sombraPieza(oscuro), cod.topLeft + Offset(0.5f * u, 0.9f * u), cod.size, CornerRadius(1.2f * u))
        drawRect(if (oscuro) mezcla(LINO, CIELO, 0.55f) else LINO, Offset(cod.left + 1 * u, cod.top + 1.2f * u), Size(cod.width, cod.height - 1.2f * u))
        drawRoundRect(cuero, cod.topLeft, Size(cod.width - 0.6f * u, cod.height - 1 * u), CornerRadius(1.2f * u))
        drawPath(Path().apply { moveTo(cod.right - 5 * u, cod.top); lineTo(cod.right - 0.6f * u, cod.top + 2 * u); lineTo(cod.right - 0.6f * u, cod.bottom - 3 * u); lineTo(cod.right - 5 * u, cod.bottom - 1 * u) },
            mezcla(cuero, Color.Black, 0.25f), style = Stroke(0.6f * u))
        val correa = if (oscuro) mezcla(CORREA, LINO, 0.15f) else CORREA
        drawLine(correa, Offset(cod.left - 1 * u, cod.center.y), Offset(cod.right + 3 * u, cod.center.y), 1.1f * u, StrokeCap.Round)
        drawPath(Path().apply { moveTo(cod.right + 3 * u, cod.center.y); quadraticTo(cod.right + 6 * u, cod.center.y + 2 * u, cod.right + 4.5f * u, cod.bottom + 1.5f * u) },
            correa, style = Stroke(1 * u, cap = StrokeCap.Round))
        val ys = h - 10 * u
        if (cod.left - 8 * u > 52 * u) {
            val cx = (52 * u + cod.left - 8 * u) / 2
            drawLine(fino, Offset(52 * u, ys), Offset(cx - 9 * u, ys), 0.7f * u)
            drawLine(fino, Offset(cx + 9 * u, ys), Offset(cod.left - 8 * u, ys), 0.7f * u)
            for (k in -1..1) drawCircle(traza, (if (k == 0) 1.3f else 0.9f) * u, Offset(cx + k * 4.5f * u, ys))
        }
    }

    /**
     * El recuadro de Relaciones es un nicho trazado en la piedra: el arco rebajado sobre sus dos jambas y el umbral doble al
     * pie; por dentro del arco, las siete luces en fila curva y, en la clave, la chispa. Tenue en el centro.
     */
    override fun DrawScope.adornoRelacion(e: EstadoAdorno, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (w < 40 * u || h < 30 * u) return
        val base = if (oscuro) e.secundario else e.primario
        val linea = base.copy(alpha = base.alpha * 0.42f); val fino = base.copy(alpha = base.alpha * 0.2f)
        val r = Rect(4 * u, 4 * u, w - 4 * u, h - 4 * u)
        val flecha = minOf(11 * u, r.height * 0.3f, r.width / 2)
        drawPath(Path().apply {
            moveTo(r.left, r.bottom - 2 * u); lineTo(r.left, r.top + flecha)
            arcTo(Rect(r.left, r.top, r.right, r.top + 2 * flecha), 180f, 180f, false)
            lineTo(r.right, r.bottom - 2 * u)
        }, linea, style = Stroke(0.8f * u))
        drawLine(linea, Offset(r.left - 1.5f * u, r.bottom), Offset(r.right + 1.5f * u, r.bottom), 1 * u)
        drawLine(fino, Offset(r.left, r.bottom - 2 * u), Offset(r.right, r.bottom - 2 * u), 0.6f * u)
        val rx = r.width / 2 - 4 * u; val ry = maxOf(flecha - 3.5f * u, 1 * u)
        val c = Offset(r.center.x, r.top + flecha)
        for (k in 0 until 7) {
            val a = Math.toRadians(180.0 + 22.5 * (k + 1))
            drawCircle(linea, 0.9f * u, Offset(c.x + rx * cos(a).toFloat(), c.y + ry * sin(a).toFloat()))
        }
        with(TrazosArcontes) { chispa(Offset(r.center.x, r.top), 2.4f * u, 0.85f, e.secundario, e.secundario) }
    }

    /**
     * El icono de la cinta dentro del ouroboros: la serpiente que se muerde la cola, con su cabeza arriba a la derecha, y por
     * dentro la primera de las esferas, en un círculo fino.
     */
    override fun DrawScope.marcoPista(e: EstadoAdorno, oscuro: Boolean) {
        val s = size.minDimension
        val c = Offset(size.width / 2, size.height / 2)
        with(TrazosArcontes) { ouroboros(c, s * 0.4f, s * 0.1f, e.primario, -60f) }
        drawCircle(e.secundario.copy(alpha = e.secundario.alpha * 0.6f), s * 0.33f, c, style = Stroke(s * 0.025f))
    }
}
