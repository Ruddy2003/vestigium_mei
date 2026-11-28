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
import io.github.ruddy2003.vestigium.ui.Arcontes
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.TrazosArcontes
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.sombraSuave
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Arcontes
// =====================================================================

/**
 * El sello de barro de las cartas de Arcontes (una bula): un disco de barro de borde irregular, apretado sobre el cordel, con
 * el ouroboros hundido y la chispa en el centro. Lo usan el sobre de la biblioteca y `selloCarta` (al pie de la carta y en su
 * PDF). [alfa] lo hace aparecer al caer; [semilla] cambia las ondas del borde.
 */
internal fun DrawScope.bullaArcontes(c: Offset, r: Float, barro: Color, alfa: Float = 1f, semilla: Int = 5) {
    val a = alfa.coerceIn(0f, 1f)
    if (a <= 0f || r <= 0f) return
    val base = barro.copy(alpha = 1f)
    val borde = Path()
    for (i in 0..40) {
        val ang = 2 * PI * i / 40
        val rr = r * (1f + 0.06f * sin(ang * 3 + semilla).toFloat() + 0.035f * sin(ang * 7 + semilla * 2).toFloat())
        val p = Offset(c.x + rr * cos(ang).toFloat(), c.y + rr * 0.92f * sin(ang).toFloat())
        if (i == 0) borde.moveTo(p.x, p.y) else borde.lineTo(p.x, p.y)
    }
    borde.close()
    translate(r * 0.07f, r * 0.12f) { drawPath(borde, Color.Black.copy(alpha = 0.25f * a)) }
    drawPath(borde, base.copy(alpha = a))
    drawPath(borde, Brush.radialGradient(listOf(Color.White.copy(alpha = 0.26f * a), Color.Transparent), center = Offset(c.x - r * 0.38f, c.y - r * 0.42f), radius = r * 1.15f))
    drawPath(borde, mezcla(base, Color.Black, 0.4f).copy(alpha = 0.55f * a), style = Stroke(r * 0.08f))
    // El ouroboros apretado en el barro: el surco hondo y, abajo a la derecha, el labio que levantó el sello.
    val hondo = mezcla(base, Color.Black, 0.45f).copy(alpha = a)
    val labio = mezcla(base, Color.White, 0.32f).copy(alpha = 0.75f * a)
    with(TrazosArcontes) {
        ouroboros(Offset(c.x + r * 0.04f, c.y + r * 0.05f), r * 0.55f, r * 0.17f, labio)
        ouroboros(c, r * 0.55f, r * 0.17f, hondo, escamas = labio)
    }
    drawCircle(hondo, r * 0.11f, c)
    drawCircle(labio, r * 0.05f, Offset(c.x + r * 0.03f, c.y + r * 0.04f))
}

/**
 * Arcontes: la biblioteca de la comunidad escondida, en la misma cueva de Galilea del Ideario. El estante del diario es el
 * armario de libros de la comunidad, de olivo, metido en un entrante de la roca, con sus códices, rollos y envoltorios de lana
 * (lo dibuja [MuebleEstanteArcontes]).
 * La tapa del diario es el códice del día, cerrado sobre la mesa: la solapa en punta que lo envuelve, el lazo de cuero con su
 * pasador de hueso y la hoja suelta de papiro donde se lee lo escrito; al abrirse, se suelta la solapa y la tapa gira sobre
 * el lomo hasta dejar ver la primera hoja escrita, con su título enmarcado al pie.
 */
object EstanteArcontes : DisenoBiblioteca {
    // ---------------------------------------------------------------- la paleta de la cueva
    private val CALIZA = TrazosArcontes.CALIZA
    private val CALIZA_SOMBRA = TrazosArcontes.CALIZA_SOMBRA
    /** La caliza donde le da el sol, y el fondo de los huecos, en sombra. */
    private val CALIZA_LUZ = Color(0xFFEBDFBF)
    private val HUECO = Color(0xFF8C7A5C)
    private val BARRO = TrazosArcontes.BARRO
    private val BARRO_CLARO = TrazosArcontes.BARRO_CLARO
    private val ORO = TrazosArcontes.ORO_DEMIURGO
    private val CIELO = TrazosArcontes.CIELO_ANTIGUO
    private val ESFERA = TrazosArcontes.ESFERA
    private val GRIETA = TrazosArcontes.GRIETA
    private val GRIETA_HALO = TrazosArcontes.GRIETA_HALO
    private val LLAMA = TrazosArcontes.LLAMA
    private val LLAMA_HALO = TrazosArcontes.LLAMA_HALO
    private val SOMBRA = TrazosArcontes.SOMBRA
    private val CUERO = TrazosArcontes.CUERO
    private val CORREA = TrazosArcontes.CORREA
    /** Los cueros de los códices: el pardo de la tapa, cada uno de un curtido un poco distinto. */
    private val CUEROS = listOf(CUERO, Color(0xFF77543A), Color(0xFF5F422A), Color(0xFF6E5236))
    /** El papiro de las hojas, el papiro de los cantos y la fibra. */
    private val PAPIRO = Color(0xFFEDE3C8)
    private val PAPIRO_OSC = Color(0xFFD6C49B)
    private val FIBRA = Color(0xFF8E7650)
    /** La tinta de carbón, violácea de tan negra, la tinta gastada y el ocre de las marcas. */
    private val TINTA = Color(0xFF1E1A2B)
    private val TENUE = Color(0xFF5E5466)
    private val OCRE = Color(0xFF87590F)
    /** La madera de olivo de las tablas: su color, su veta y su canto en luz. */
    private val OLIVO = Color(0xFFA08A60)
    private val OLIVO_VETA = Color(0xFF5B4630)
    private val OLIVO_LUZ = Color(0xFFC9B282)
    /** El cordel de lino torcido y el hueso de los pasadores y las cuentas. */
    private val CORDEL = Color(0xFFCDBB8E)
    private val HUESO = Color(0xFFE8DEC6)
    /** La tierra apisonada del suelo. */
    private val TIERRA = Color(0xFF85704F)
    /** La cueva de noche (modo oscuro): el azul negro del cielo antiguo, donde se hunde la piedra lejos de la lámpara. */
    private val PENUMBRA = Color(0xFF090B1D)
    /** La tinta de noche: el lino a la luz de la lámpara, sobre el papiro a oscuras. */
    private val TINTA_NOCHE = Color(0xFFF1E2C2)
    private val TENUE_NOCHE = Color(0xFFA09CB0)

    private val TITULO: FontFamily = LetraExtra.caudex
    private val TEXTO: FontFamily = LetraExtra.literata

    /**
     * El color de lo que no alumbra ninguna lámpara: en modo oscuro, la misma cueva de noche, hundida en el azul negro del
     * cielo antiguo; de día, tal cual (con [noche], la de los sueños, un poco hacia el cielo de la noche).
     */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color = when {
        oscuro -> mezcla(c, PENUMBRA, 0.84f)
        noche -> mezcla(c, CIELO, 0.4f)
        else -> c
    }

    /**
     * Un color a [cerca] (0 a 1) de una lámpara encendida: de día, como [t]; en modo oscuro, menos hundido en la noche cuanto
     * más cerca está, y calentado por la llama.
     */
    private fun luz(c: Color, oscuro: Boolean, noche: Boolean, cerca: Float): Color {
        if (!oscuro) return t(c, false, noche)
        val k = cerca.coerceIn(0f, 1f)
        return mezcla(mezcla(c, PENUMBRA, 0.9f - 0.52f * Math.pow(k.toDouble(), 1.4).toFloat()), LLAMA, 0.2f * k)
    }

    /**
     * El papiro donde se lee: de día, claro; en modo oscuro, la hoja a oscuras que alcanza la luz de la lámpara (a [cerca]), con
     * la tinta clara encima ([tinta]); en los sueños de día, un poco hacia el cielo de la noche.
     */
    private fun papel(oscuro: Boolean, noche: Boolean, cerca: Float = 0.45f): Color = when {
        oscuro -> luz(PAPIRO, true, noche, cerca)
        noche -> mezcla(PAPIRO, CIELO, 0.22f)
        else -> PAPIRO
    }

    /** La tinta sobre el papiro: de carbón de día; de noche, clara, a la luz de la lámpara. */
    private fun tinta(oscuro: Boolean): Color = if (oscuro) TINTA_NOCHE else TINTA
    private fun tenue(oscuro: Boolean): Color = if (oscuro) TENUE_NOCHE else TENUE
    /** El ocre de las marcas: el oro quemado de día; de noche, la llama. */
    private fun ocre(oscuro: Boolean): Color = if (oscuro) LLAMA else OCRE

    /** La luz de una lámpara encendida sobre lo que la rodea, en modo oscuro: el halo cálido y, más cerca, la llama. */
    private fun DrawScope.resplandor(c: Offset, r: Float, fuerza: Float = 1f) {
        mancha(c, r, LLAMA_HALO.copy(alpha = 0.26f * fuerza))
        mancha(c, r * 0.4f, LLAMA_HALO.copy(alpha = 0.22f * fuerza))
    }

    /** El orden de los planetas en las siete esferas, de la más baja a la más alta (recurso: así se dibujan). */
    private val PLANETAS = listOf("Luna", "Mercurio", "Venus", "Sol", "Marte", "Júpiter", "Saturno")

    // =================================================================
    //  Ayudantes de dibujo de la cueva
    // =================================================================

    /** El ancho de un texto, para ajustar tiras y rótulos a lo que dicen. */
    private fun anchoTx(medidor: TextMeasurer, texto: String, letra: FontFamily, tam: Float, espaciado: Float = 0f, peso: FontWeight = FontWeight.Normal): Float =
        medidor.measure(texto, TextStyle(fontFamily = letra, fontSize = tam.sp, fontWeight = peso, letterSpacing = espaciado.sp), maxLines = 1, softWrap = false).size.width.toFloat()

    /** Un texto alineado a la izquierda dentro de [r]: [textoEn] en una caja de su ancho medido. */
    private fun DrawScope.aIzquierda(medidor: TextMeasurer, texto: String, r: Rect, letra: FontFamily, color: Color, tam: Float,
                                     espaciado: Float = 0f, peso: FontWeight = FontWeight.Normal, minimo: Float = 6f) {
        val ancho = anchoTx(medidor, texto, letra, tam, espaciado, peso) + 2f
        textoEn(medidor, texto, Rect(r.left, r.top, minOf(r.right, r.left + ancho), r.bottom), letra, color, tam, peso = peso, espaciado = espaciado, minimo = minimo)
    }

    /**
     * Un texto en [r]: en un renglón si cabe a [tam]; si no cabe y tiene un « · », se parte ahí en dos renglones a [k] veces el
     * tamaño («Cielo 290» arriba, el planeta y la fecha hebrea abajo). [r] debe tener alto para los dos.
     */
    private fun DrawScope.enDos(medidor: TextMeasurer, texto: String, r: Rect, letra: FontFamily, color: Color, tam: Float,
                                peso: FontWeight = FontWeight.Normal, k: Float = 0.8f) {
        val i = texto.indexOf(" · ")
        if (i <= 0 || anchoTx(medidor, texto, letra, tam, peso = peso) <= r.width) {
            textoEn(medidor, texto, r, letra, color, tam, peso = peso, minimo = tam * 0.5f)
            return
        }
        val mitad = r.top + r.height * 0.52f
        textoEn(medidor, texto.substring(0, i).trim(), Rect(r.left, r.top, r.right, mitad), letra, color, tam, peso = peso, minimo = tam * 0.5f)
        textoEn(medidor, texto.substring(i + 3).trim(), Rect(r.left, mitad, r.right, r.bottom), letra, color, tam * k, minimo = tam * 0.42f)
    }

    /** Dibuja [bloque] con la transformación afín x' = a·x + c·y + tx, y' = b·x + d·y + ty (para las tapas que giran). */
    private fun DrawScope.afin(a: Float, b: Float, c: Float, d: Float, tx: Float, ty: Float, bloque: DrawScope.() -> Unit) {
        withTransform({ transform(Matrix(floatArrayOf(a, b, 0f, 0f, c, d, 0f, 0f, 0f, 0f, 1f, 0f, tx, ty, 0f, 1f))) }) { bloque() }
    }

    /** La pestaña [k] de las cinco: exactamente una quinta parte del ancho [w] (ahí se toca). */
    private fun quinta(k: Int, w: Float, arriba: Float, abajo: Float): Rect = Rect(w * k / 5f, arriba, w * (k + 1) / 5f, abajo)

    /** Un nicho de arco de medio punto dentro de [r] (si el arco no cabe, se rebaja hasta la mitad del alto). */
    private fun nicho(r: Rect): Path = Path().apply {
        val flecha = minOf(r.width / 2f, r.height * 0.5f)
        moveTo(r.left, r.bottom); lineTo(r.left, r.top + flecha)
        arcTo(Rect(r.left, r.top, r.right, r.top + 2 * flecha), 180f, 180f, false)
        lineTo(r.right, r.bottom); close()
    }

    /**
     * La caliza dentro de [r]: el color [base], los estratos que la cruzan, los poros y las conchitas fósiles y, aquí y allá,
     * las marcas del cincel en grupos de tres. [fuerza] baja todo el dibujo (para los fondos en sombra).
     */
    private fun DrawScope.piedra(r: Rect, base: Color, semilla: Int, fuerza: Float = 1f) {
        val u = density
        drawRect(base, r.topLeft, r.size)
        val oscura = mezcla(base, Color.Black, 0.45f)
        vetas(r, oscura.copy(alpha = 0.16f * fuerza), semilla, densidad = 0.28f, grosor = 1.6f, ondulacion = 0.5f)
        val n = (r.width * r.height / (u * u * 70f)).toInt().coerceIn(6, 260)
        for (k in 0 until n) {
            val p = Offset(r.left + r.width * azar(semilla, k), r.top + r.height * azar(semilla + 1, k))
            val tam = (0.35f + 0.9f * azar(semilla + 2, k)) * u
            if (azar(semilla + 3, k) > 0.8f) drawCircle(mezcla(base, Color.White, 0.4f).copy(alpha = 0.5f * fuerza), tam, p)
            else drawCircle(oscura.copy(alpha = (0.1f + 0.18f * azar(semilla + 4, k)) * fuerza), tam, p)
        }
        // Las marcas del cincel: tres trazos cortos paralelos, inclinados.
        val m = (r.width * r.height / (u * u * 1400f)).toInt().coerceIn(1, 40)
        for (k in 0 until m) {
            val o = Offset(r.left + r.width * azar(semilla + 7, k), r.top + r.height * azar(semilla + 8, k))
            val l = (2.5f + 3f * azar(semilla + 9, k)) * u
            for (j in 0 until 3) {
                val d = Offset(j * 1.6f * u, j * 0.4f * u)
                drawLine(oscura.copy(alpha = 0.2f * fuerza), o + d, o + d + Offset(l * 0.45f, l), 0.6f * u, StrokeCap.Round)
                drawLine(mezcla(base, Color.White, 0.5f).copy(alpha = 0.18f * fuerza), o + d + Offset(0.5f * u, 0f), o + d + Offset(l * 0.45f + 0.5f * u, l), 0.4f * u, StrokeCap.Round)
            }
        }
    }

    /** La lámpara de aceite de barro ([TrazosArcontes.lampara]), con el halo de su llama sobre la roca si está encendida. */
    private fun DrawScope.lamparaRoca(c: Offset, w: Float, oscuro: Boolean, noche: Boolean, encendida: Boolean, llama: Float = 1f) {
        if (encendida) {
            val fuego = Offset(c.x + w * 0.46f, c.y - w * 0.4f)
            mancha(fuego, w * 3.2f, LLAMA_HALO.copy(alpha = if (noche) 0.3f else 0.24f))
            mancha(fuego, w * 1.3f, LLAMA_HALO.copy(alpha = 0.3f))
        }
        drawOval(Color.Black.copy(alpha = 0.28f), Offset(c.x - w * 0.42f, c.y + w * 0.08f), Size(w * 0.86f, w * 0.12f))
        val barro = if (encendida) mezcla(t(BARRO_CLARO, oscuro, noche), LLAMA, 0.18f) else t(BARRO_CLARO, oscuro, noche)
        with(TrazosArcontes) { lampara(c, w, barro, if (encendida) llama else 0f) }
        // De día, apagada: el pico ennegrecido de tantas noches.
        if (!encendida) drawCircle(Color.Black.copy(alpha = 0.35f), w * 0.05f, Offset(c.x + w * 0.45f, c.y - w * 0.32f))
    }

    /** La grieta de luz ([TrazosArcontes.grieta]) con un halo violeta alrededor cuando brilla más. */
    private fun DrawScope.grietaLuz(desde: Offset, hasta: Offset, ancho: Float, luz: Float, semilla: Int) {
        if (luz > 0.5f) mancha(Offset((desde.x + hasta.x) / 2, (desde.y + hasta.y) / 2), abs(hasta.y - desde.y) * 0.9f + abs(hasta.x - desde.x), GRIETA_HALO.copy(alpha = 0.12f * luz))
        with(TrazosArcontes) { grieta(desde, hasta, ancho, luz, semilla) }
    }

    /** El ouroboros de todo el tema ([TrazosArcontes.ouroboros]). */
    private fun DrawScope.ouro(c: Offset, r: Float, grueso: Float, color: Color, escamas: Color? = null) {
        with(TrazosArcontes) { ouroboros(c, r, grueso, color, escamas = escamas) }
    }

    /** Un cordel torcido a lo largo de [p]: el cuerpo, su sombra y las vueltas de la torsión. */
    private fun DrawScope.cordel(p: Path, color: Color, g: Float) {
        drawPath(p, mezcla(color, Color.Black, 0.4f), style = Stroke(g * 1.3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(p, color, style = Stroke(g, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(p, mezcla(color, Color.White, 0.35f), style = Stroke(g * 0.38f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(g * 0.7f, g * 1f), 0f)))
    }

    /** Un cordel recto de [a] a [b]. */
    private fun DrawScope.cordel(a: Offset, b: Offset, color: Color, g: Float) = cordel(Path().apply { moveTo(a.x, a.y); lineTo(b.x, b.y) }, color, g)

    /**
     * Una fila de diplés (‹›), el adorno con que los copistas enmarcaban los títulos de los tratados (recurso: así se
     * dibuja): marcas en ángulo de alto [alto] entre [x0] y [x1]. Con [haciaCentro], las de cada mitad apuntan al centro y dejan
     * libre un hueco de [hueco] a cada lado de él; si no, apuntan todas a la derecha (o a la izquierda, con [izquierda]).
     */
    private fun DrawScope.diples(x0: Float, x1: Float, y: Float, alto: Float, color: Color, grosor: Float, haciaCentro: Boolean = false, hueco: Float = 0f,
                                 izquierda: Boolean = false) {
        if (x1 - x0 < alto) return
        val paso = alto * 0.9f
        val cx = (x0 + x1) / 2
        val p = Path()
        var x = x0
        while (x + alto * 0.5f <= x1) {
            val medio = x + alto * 0.25f
            if (abs(medio - cx) >= hueco) {
                val derecha = if (haciaCentro) medio < cx else !izquierda
                if (derecha) { p.moveTo(x, y - alto / 2); p.lineTo(x + alto * 0.5f, y); p.lineTo(x, y + alto / 2) }
                else { p.moveTo(x + alto * 0.5f, y - alto / 2); p.lineTo(x, y); p.lineTo(x + alto * 0.5f, y + alto / 2) }
            }
            x += paso
        }
        drawPath(p, color, style = Stroke(grosor, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    /** Las fibras del papiro dentro de [r]: la capa horizontal, más marcada, y la vertical, apenas. */
    private fun DrawScope.fibras(r: Rect, color: Color, semilla: Int, paso: Float) {
        val u = density
        var y = r.top + paso * 0.5f; var k = 0
        while (y < r.bottom) {
            val x0 = r.left + r.width * 0.3f * azar(semilla + 6, k)
            drawLine(color.copy(alpha = color.alpha * (0.2f + 0.8f * azar(semilla, k))), Offset(x0, y), Offset(r.right - r.width * 0.25f * azar(semilla + 7, k), y + (azar(semilla + 1, k) - 0.5f) * u), 0.45f * u)
            y += paso * (0.7f + 0.6f * azar(semilla + 2, k)); k++
        }
        var x = r.left + paso * 4 * azar(semilla + 4, 0); k = 0
        while (x < r.right) {
            drawLine(color.copy(alpha = color.alpha * 0.12f * (0.4f + azar(semilla + 5, k))), Offset(x, r.top), Offset(x, r.bottom), 0.7f * u)
            x += paso * (5f + 9f * azar(semilla + 3, k)); k++
        }
    }

    /** Una hoja de papiro de borde un poco roído dentro de [r], con su sombra y sus fibras. Devuelve su forma. */
    private fun DrawScope.hoja(r: Rect, color: Color, semilla: Int, sombra: Float = 0.22f, roido: Float = 0.012f): Path {
        val u = density
        val forma = contornoIrregular(r, semilla, 48, roido)
        if (sombra > 0f) translate(0.8f * u, 1.4f * u) { drawPath(forma, Color.Black.copy(alpha = sombra)) }
        drawPath(forma, color)
        clipPath(forma) {
            fibras(r, FIBRA.copy(alpha = 0.16f), semilla, 2.2f * u)
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, FIBRA.copy(alpha = 0.12f)), r.top, r.bottom), r.topLeft, r.size)
        }
        drawPath(forma, mezcla(color, FIBRA, 0.5f).copy(alpha = 0.6f), style = Stroke(0.6f * u))
        return forma
    }

    /** El cuero de una tapa dentro de [r]: su curtido, el grano, los rozones y el canto gastado y más claro. */
    private fun DrawScope.cuero(r: Rect, color: Color, semilla: Int, rad: Float) {
        val u = density
        val forma = Path().apply { addRoundRect(RoundRect(r, CornerRadius(rad))) }
        drawPath(forma, Brush.verticalGradient(listOf(mezcla(color, Color.White, 0.08f), color, mezcla(color, Color.Black, 0.18f)), r.top, r.bottom))
        clipPath(forma) {
            val n = (r.width * r.height / (u * u * 10f)).toInt().coerceIn(10, 500)
            for (k in 0 until n) drawCircle((if (k % 3 == 0) Color.White else Color.Black).copy(alpha = 0.06f + 0.06f * azar(semilla, k)), (0.3f + 0.5f * azar(semilla + 1, k)) * u,
                Offset(r.left + r.width * azar(semilla + 2, k), r.top + r.height * azar(semilla + 3, k)))
            for (k in 0 until 4) {
                val o = Offset(r.left + r.width * azar(semilla + 5, k), r.top + r.height * azar(semilla + 6, k))
                drawOval(Color.White.copy(alpha = 0.05f), o, Size((6f + 14f * azar(semilla + 7, k)) * u, (2f + 3f * azar(semilla + 8, k)) * u))
            }
        }
        drawPath(forma, mezcla(color, Color.White, 0.22f).copy(alpha = 0.5f), style = Stroke(1.2f * u))
        drawPath(forma, Color.Black.copy(alpha = 0.35f), style = Stroke(0.5f * u))
    }

    /** El grabado en seco de las tapas: el filete doble del borde y la red de diagonales que llena el campo. */
    private fun DrawScope.grabado(r: Rect, cuero: Color, celda: Float, red: Boolean = true) {
        val u = density
        val hondo = mezcla(cuero, Color.Black, 0.4f).copy(alpha = 0.7f)
        val luz = mezcla(cuero, Color.White, 0.2f).copy(alpha = 0.45f)
        listOf(0f, 2.4f * u).forEach { e ->
            val q = Rect(r.left + e, r.top + e, r.right - e, r.bottom - e)
            drawRect(luz, q.topLeft + Offset(0.5f * u, 0.5f * u), q.size, style = Stroke(0.5f * u))
            drawRect(hondo, q.topLeft, q.size, style = Stroke(0.6f * u))
        }
        if (!red) return
        val q = Rect(r.left + 4.8f * u, r.top + 4.8f * u, r.right - 4.8f * u, r.bottom - 4.8f * u)
        if (q.width <= 0f || q.height <= 0f) return
        clipRect(q.left, q.top, q.right, q.bottom) {
            var x = q.left - q.height
            while (x < q.right) {
                drawLine(hondo, Offset(x, q.top), Offset(x + q.height, q.bottom), 0.55f * u)
                drawLine(hondo, Offset(x + q.height, q.top), Offset(x, q.bottom), 0.55f * u)
                x += celda
            }
        }
    }

    /** Una cuenta de vidrio (o de hueso, si no hay color) ensartada en un cordel. */
    private fun DrawScope.cuenta(c: Offset, r: Float, color: Color?, oscuro: Boolean = false) {
        val col = (color ?: HUESO).let { if (oscuro) mezcla(it, PENUMBRA, 0.4f) else it }
        drawCircle(Color.Black.copy(alpha = 0.3f), r, c + Offset(r * 0.2f, r * 0.3f))
        drawCircle(col, r, c)
        drawCircle(mezcla(col, Color.Black, 0.35f), r, c, style = Stroke(r * 0.22f))
        drawCircle(Color.White.copy(alpha = if (color == null) 0.5f else 0.6f), r * 0.32f, c - Offset(r * 0.35f, r * 0.35f))
    }

    /** Las siete esferas, con un punto encendido en la del planeta [marca] (de 0, la Luna, a 6, Saturno; -1 si ninguno). */
    private fun DrawScope.esferasDia(c: Offset, r: Float, color: Color, grosor: Float, marca: Int, luz: Color) {
        for (i in 0 until 7) {
            val ri = r * (0.3f + 0.7f * i / 6f)
            drawCircle(color.copy(alpha = color.alpha * (0.5f + 0.5f * (1f - i / 7f))), ri, c, style = Stroke(grosor))
        }
        drawCircle(color, grosor * 1.4f, c)
        if (marca in 0..6) {
            val ri = r * (0.3f + 0.7f * marca / 6f)
            val a = -PI / 2 + marca * 0.9
            val p = Offset(c.x + ri * cos(a).toFloat(), c.y + ri * sin(a).toFloat())
            mancha(p, grosor * 7f, luz.copy(alpha = 0.45f))
            drawCircle(luz, grosor * 2.2f, p)
        }
    }

    /** El planeta del día a partir de la fecha como la escribe el tema («Cielo 290 · Saturno · …»); -1 si no lo nombra. */
    private fun planetaDe(fecha: String): Int = PLANETAS.indexOfFirst { fecha.contains(it) }

    /** Media pieza de una bula que se parte por una línea quebrada a la altura [y]: la de arriba ([arriba]) o la de abajo. */
    private fun mitad(r: Rect, y: Float, arriba: Boolean, semilla: Int): Path = Path().apply {
        val dientes = 5
        val amp = r.height * 0.12f
        moveTo(r.left - 2f, if (arriba) r.top - 2f else r.bottom + 2f)
        lineTo(r.left - 2f, y)
        for (i in 1..dientes) {
            val x = r.left + r.width * i / dientes
            lineTo(x - r.width / dientes * 0.5f, y + (if (i % 2 == 0) amp else -amp) * (0.5f + 0.9f * azar(semilla, i)))
            lineTo(x + if (i == dientes) 2f else 0f, y)
        }
        lineTo(r.right + 2f, if (arriba) r.top - 2f else r.bottom + 2f)
        close()
    }

    /** El rayo de sol que entra por [boca] (un ancho en lo alto) y se abre hacia [pie], con su polvo dorado flotando. */
    private fun DrawScope.rayo(boca: Pair<Offset, Offset>, pie: Pair<Offset, Offset>, fuerza: Float, semilla: Int) {
        val u = density
        val p = Path().apply { moveTo(boca.first.x, boca.first.y); lineTo(boca.second.x, boca.second.y); lineTo(pie.second.x, pie.second.y); lineTo(pie.first.x, pie.first.y); close() }
        drawPath(p, Brush.linearGradient(listOf(ORO.copy(alpha = 0.2f * fuerza), LLAMA_HALO.copy(alpha = 0.1f * fuerza), Color.Transparent),
            Offset((boca.first.x + boca.second.x) / 2, boca.first.y), Offset((pie.first.x + pie.second.x) / 2, pie.first.y)))
        clipPath(p) {
            val minX = minOf(boca.first.x, pie.first.x); val maxX = maxOf(boca.second.x, pie.second.x)
            val minY = minOf(boca.first.y, boca.second.y); val maxY = maxOf(pie.first.y, pie.second.y)
            for (k in 0 until 46) {
                val o = Offset(minX + (maxX - minX) * azar(semilla, k), minY + (maxY - minY) * azar(semilla + 1, k))
                val f = 1f - (o.y - minY) / (maxY - minY).coerceAtLeast(1f)
                drawCircle(LLAMA_HALO.copy(alpha = (0.35f + 0.5f * azar(semilla + 2, k)) * fuerza * (0.3f + 0.7f * f)), (0.4f + 0.8f * azar(semilla + 3, k)) * u, o)
            }
        }
    }

    // =================================================================
    //  El mueble: el armario de libros de la comunidad, en la cueva
    // =================================================================

    override val corona = MuebleEstanteArcontes.CORONA
    override val base = MuebleEstanteArcontes.BASE
    override val lado = MuebleEstanteArcontes.LADO
    override val altoRepisa: Dp = MuebleEstanteArcontes.ALTO_REPISA
    override val tabla: Dp = MuebleEstanteArcontes.TABLA
    override val separa: Dp = MuebleEstanteArcontes.SEPARA
    override val letra: FontFamily = TITULO
    /** Estante rehecho: el aura solo sale al tocar (ver [DisenoBiblioteca.auraEnReposo]). */
    override val auraEnReposo = false

    override fun ancho(palabras: Int): Dp = MuebleEstanteArcontes.ancho(palabras)
    override fun alto(semilla: Int, palabras: Int): Float = MuebleEstanteArcontes.alto(semilla, palabras)

    /** El estante del diario: el armario de olivo de la comunidad, en la cueva (ver [MuebleEstanteArcontes]). */
    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) =
        with(MuebleEstanteArcontes) { estante(d, medidor, oscuro, noche) }

    /** Cada entrada: el códice, el rollo o el envoltorio de lana (ver [MuebleEstanteArcontes]). */
    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) =
        with(MuebleEstanteArcontes) { volumen(v, medidor, oscuro) }

    // =================================================================
    //  La tapa: el códice del día, cerrado sobre la mesa
    // =================================================================

    override val altoTapa: Dp = 196.dp
    override val cajaTapa = Rect(0.1f, 0.305f, 0.6f, 0.625f)
    override val letraTapa: FontFamily = TEXTO
    override val tamTapa = 12f
    override val lineasTapa = 4
    override val centradoTapa = false
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = tinta(oscuro)
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra en el códice" else "$n palabras en el códice"

    /**
     * El códice del día, cerrado sobre la mesa: el cuero pardo con el filete doble y la red de diagonales grabados en seco, el
     * canto de papiro que asoma y, encima, la hoja suelta de papiro donde se lee lo escrito, con la fecha del tema arriba y una
     * fila de diplés. La solapa de la tapa de atrás envuelve el canto y se cierra en punta, con su filete y las siete esferas
     * grabadas; de la punta sale el lazo de cuero, que da dos vueltas al códice, y el pasador de hueso. Al pie, el ouroboros
     * grabado y cuántas palabras hay. Al abrirse, la solapa se suelta y se va hacia la derecha; luego la tapa gira sobre el
     * lomo y deja ver la primera hoja escrita, con su título enmarcado de diplés al pie. De noche, la lámpara la alumbra desde
     * una esquina.
     */
    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = luz(x, oscuro, e.noche, 0.5f)
        val a = e.abierto.coerceIn(0f, 1f)
        val aSol = (a / 0.35f).coerceIn(0f, 1f)
        val aTapa = ((a - 0.3f) / 0.7f).coerceIn(0f, 1f)
        val cuero = c(CUEROS[Math.floorMod(e.semilla, CUEROS.size)])
        val hojaCol = papel(oscuro, e.noche, 0.55f)
        val cuerpo = Rect(w * 0.02f, h * 0.05f, w * 0.9f, h * 0.95f)
        val x0 = cuerpo.left + w * 0.03f
        val cara = Rect(x0, cuerpo.top, cuerpo.right, cuerpo.bottom)
        val rad = 5 * u
        val correa = c(CORREA)
        // La sombra sobre la mesa y las hojas de papiro que asoman por el canto.
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.25f), Offset(cuerpo.left + 3 * u, cuerpo.top + 5 * u), cuerpo.size, CornerRadius(rad))
        for (j in 3 downTo 1) drawRoundRect(mezcla(hojaCol, FIBRA, 0.14f * j), Offset(cara.left + j * 0.9f * u, cara.top + j * 1.2f * u), cara.size, CornerRadius(rad))
        // Debajo de la tapa: la tapa de atrás y la primera hoja escrita.
        if (aTapa > 0.01f) {
            cuero(cara, mezcla(cuero, Color.Black, 0.25f), e.semilla + 1, rad)
            primeraHoja(Rect(cara.left + w * 0.025f, cara.top + h * 0.05f, cara.right - w * 0.025f, cara.bottom - h * 0.05f), e, medidor, oscuro)
        }
        // La tapa de delante, que gira sobre el lomo y se ve cada vez más de canto.
        val ang = aTapa * 1.45f
        val co = cos(ang); val si = sin(ang)
        if (aTapa > 0.01f) drawRect(Color.Black.copy(alpha = 0.3f * si), Offset(x0 + cara.width * co, cara.top), Size(14 * u * si, cara.height))
        val puntaX = cara.right - cara.width * 0.3f
        val puntaY = cara.center.y
        afin(co, -si * 0.1f, 0f, 1f, x0 - x0 * co, x0 * si * 0.1f) {
            cuero(cara, cuero, e.semilla, rad)
            grabado(cara.deflate(4 * u), cuero, 15 * u)
            // El lazo, que da dos vueltas al códice (por debajo de la hoja suelta) mientras la solapa está cerrada.
            if (aSol < 0.04f) listOf(puntaY - 1.8f * u, puntaY + 2.8f * u).forEach { y ->
                drawLine(Color.Black.copy(alpha = 0.3f), Offset(cuerpo.left - 1 * u, y + 1.2f * u), Offset(puntaX, y + 1.2f * u), 2.6f * u)
                drawLine(correa, Offset(cuerpo.left - 1 * u, y), Offset(puntaX, y), 2.4f * u)
                drawLine(Color.White.copy(alpha = 0.14f), Offset(cuerpo.left - 1 * u, y - 0.8f * u), Offset(puntaX, y - 0.8f * u), 0.5f * u)
            }
            // La hoja suelta, con la fecha del tema arriba y una fila de diplés.
            val hj = Rect(w * 0.075f, h * 0.1f, w * 0.625f, h * 0.665f)
            hoja(hj, hojaCol, e.semilla + 5, 0.3f, 0.008f)
            enDos(medidor, e.fecha, Rect(hj.left + 8 * u, hj.top + 3 * u, hj.right - 8 * u, h * 0.255f), TITULO, tinta(oscuro), 12.5f, peso = FontWeight.Bold, k = 0.8f)
            val yd = h * 0.278f
            diples(hj.left + 10 * u, hj.right - 10 * u, yd, 4 * u, c(ocre(oscuro)), 0.8f * u, haciaCentro = true, hueco = 8 * u)
            drawCircle(c(ocre(oscuro)), 1.5f * u, Offset(hj.center.x, yd))
            // Al pie, el ouroboros y las palabras, grabados en seco.
            val pie = Rect(cara.left + w * 0.045f, h * 0.76f, puntaX - w * 0.04f, h * 0.88f)
            val hondo = mezcla(cuero, Color.Black, 0.45f)
            ouro(Offset(pie.left + 8 * u, pie.center.y), 6.5f * u, 2f * u, hondo, escamas = mezcla(cuero, Color.White, 0.2f))
            val palabras = if (e.vacio) "hoja en blanco" else palabrasTapa(e.palabras)
            aIzquierda(medidor, palabras, Rect(pie.left + 20 * u, pie.top, pie.right, pie.bottom).translate(0.5f * u, 0.5f * u), TITULO, Color.White.copy(alpha = 0.12f), 10.5f, minimo = 6f)
            aIzquierda(medidor, palabras, Rect(pie.left + 20 * u, pie.top, pie.right, pie.bottom), TITULO, mezcla(cuero, Color(0xFFF1DDB0), 0.55f), 10.5f, minimo = 6f)
            if (aTapa > 0f) drawRoundRect(Color.Black.copy(alpha = 0.45f * (1f - co)), cara.topLeft, cara.size, CornerRadius(rad))
        }
        // La solapa de la tapa de atrás: envuelve el canto y se cierra en punta. Al soltarse gira sobre el canto y se va.
        if (aTapa < 0.05f) {
            val bis = cara.right
            val x1 = cara.right - cara.width * 0.2f
            val solapa = Path().apply { moveTo(bis, cara.top); lineTo(x1, cara.top); lineTo(puntaX, puntaY); lineTo(x1, cara.bottom); lineTo(bis, cara.bottom); close() }
            val k = cos(PI * aSol).toFloat()
            val esc = if (abs(k) < 0.03f) 0.03f * (if (k < 0f) -1f else 1f) else k
            withTransform({ scale(esc, 1f, Offset(bis, 0f)) }) {
                if (k >= 0f) {
                    translate(-1.5f * u, 1.5f * u) { drawPath(solapa, Color.Black.copy(alpha = 0.35f)) }
                    drawPath(solapa, Brush.horizontalGradient(listOf(mezcla(cuero, Color.White, 0.1f), cuero, mezcla(cuero, Color.Black, 0.2f)), puntaX, bis))
                    clipPath(solapa) {
                        for (n in 0 until 140) drawCircle(Color.Black.copy(alpha = 0.07f), (0.3f + 0.5f * azar(e.semilla + 9, n)) * u,
                            Offset(puntaX + (bis - puntaX) * azar(e.semilla + 10, n), cara.top + cara.height * azar(e.semilla + 11, n)))
                    }
                    val filete = Path().apply {
                        moveTo(bis - 4 * u, cara.top + 4 * u); lineTo(x1 + 2 * u, cara.top + 4 * u); lineTo(puntaX + 7 * u, puntaY)
                        lineTo(x1 + 2 * u, cara.bottom - 4 * u); lineTo(bis - 4 * u, cara.bottom - 4 * u)
                    }
                    translate(0.5f * u, 0.5f * u) { drawPath(filete, Color.White.copy(alpha = 0.12f), style = Stroke(0.6f * u)) }
                    drawPath(filete, mezcla(cuero, Color.Black, 0.45f), style = Stroke(0.8f * u))
                    // Las siete esferas, grabadas en el campo de la solapa.
                    val ce = Offset((x1 + bis) / 2f + 2 * u, cara.top + cara.height * 0.26f)
                    esferasDia(Offset(ce.x + 0.5f * u, ce.y + 0.5f * u), 13 * u, Color.White.copy(alpha = 0.14f), 0.5f * u, -1, Color.Transparent)
                    esferasDia(ce, 13 * u, mezcla(cuero, Color.Black, 0.45f), 0.6f * u, -1, Color.Transparent)
                    val ce2 = Offset(ce.x, cara.bottom - cara.height * 0.26f)
                    ouro(ce2, 9 * u, 2.6f * u, mezcla(cuero, Color.Black, 0.45f), escamas = mezcla(cuero, Color.White, 0.18f))
                    drawPath(solapa, Color.Black.copy(alpha = 0.35f), style = Stroke(0.7f * u))
                    // El nudo del lazo en la punta y el pasador de hueso.
                    drawCircle(correa, 2.8f * u, Offset(puntaX + 1 * u, puntaY))
                    rotate(-14f, Offset(puntaX - 2 * u, puntaY)) {
                        drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(puntaX - 3.4f * u, puntaY - 9 * u), Size(3.4f * u, 19 * u), CornerRadius(1.7f * u))
                        drawRoundRect(c(HUESO), Offset(puntaX - 4 * u, puntaY - 9.6f * u), Size(3.4f * u, 19 * u), CornerRadius(1.7f * u))
                        drawLine(Color.Black.copy(alpha = 0.18f), Offset(puntaX - 3.2f * u, puntaY - 7 * u), Offset(puntaX - 3.2f * u, puntaY + 7 * u), 0.5f * u)
                    }
                    if (aSol > 0f) drawPath(solapa, Color.Black.copy(alpha = 0.4f * aSol))
                } else {
                    // Por dentro, la gamuza clara del cuero.
                    drawPath(solapa, mezcla(cuero, Color(0xFFC9A57A), 0.45f))
                    drawPath(solapa, Color.Black.copy(alpha = 0.2f + 0.2f * (1f + k)), style = Stroke(0.6f * u))
                }
            }
        }
        // El lomo, que no se mueve.
        val lomo = Rect(cuerpo.left, cuerpo.top, x0, cuerpo.bottom)
        drawRoundRect(mezcla(cuero, Color.Black, 0.2f), lomo.topLeft, lomo.size, CornerRadius(3.5f * u))
        drawRect(Color.White.copy(alpha = 0.08f), Offset(lomo.left + lomo.width * 0.3f, lomo.top + 2 * u), Size(lomo.width * 0.2f, lomo.height - 4 * u))
        drawLine(Color.Black.copy(alpha = 0.4f), Offset(x0, cuerpo.top + 2 * u), Offset(x0, cuerpo.bottom - 2 * u), 1.1f * u)
        if (aSol < 0.04f) listOf(puntaY - 1.8f * u, puntaY + 2.8f * u).forEach { y -> drawLine(correa, Offset(cuerpo.left - 1 * u, y), Offset(x0, y), 2.4f * u) }
        // De noche, la lámpara alumbra el códice desde la esquina de arriba.
        if (e.noche || oscuro) clipPath(Path().apply { addRoundRect(RoundRect(cuerpo, CornerRadius(rad))) }) { mancha(Offset(cuerpo.right, cuerpo.top), w * 0.5f, LLAMA_HALO.copy(alpha = 0.2f)) }
    }

    /**
     * La primera hoja del códice, la que se ve al abrirlo: el papiro con el número de la hoja en letra hebrea arriba, una
     * columna de renglones escritos a mano y, al pie, el título enmarcado entre filas de diplés, como al final de los tratados.
     */
    private fun DrawScope.primeraHoja(r: Rect, e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        hoja(r, papel(oscuro, e.noche), e.semilla + 9, 0.2f, 0.006f)
        textoEn(medidor, "א׳", Rect(r.center.x - 12 * u, r.top + 2 * u, r.center.x + 12 * u, r.top + 13 * u), TITULO, tenue(oscuro), 8.5f, minimo = 5f)
        val pie = r.bottom - r.height * 0.3f
        var y = r.top + 19 * u; var k = 0
        while (y < pie) {
            var x = r.left + 12 * u
            val fin = r.right - 12 * u - 18 * u * azar(e.semilla, k)
            var j = 0
            while (x < fin) {
                val l = (4f + 11f * azar(e.semilla + k, j)) * u
                val x2 = minOf(x + l, fin)
                drawLine(tinta(oscuro).copy(alpha = 0.42f), Offset(x, y + (azar(e.semilla + 3, j + k) - 0.5f) * 0.6f * u), Offset(x2, y), 1.3f * u, StrokeCap.Round)
                x = x2 + 3 * u; j++
            }
            y += 8 * u; k++
        }
        val ty = pie + (r.bottom - pie) * 0.5f
        diples(r.left + 14 * u, r.right - 14 * u, ty - 8 * u, 3.6f * u, ocre(oscuro).copy(alpha = 0.8f), 0.7f * u, haciaCentro = true, hueco = 34 * u)
        diples(r.left + 14 * u, r.right - 14 * u, ty + 8 * u, 3.6f * u, ocre(oscuro).copy(alpha = 0.8f), 0.7f * u, haciaCentro = true, hueco = 34 * u)
        textoEn(medidor, if (e.noche) "ⲣⲁⲥⲟⲩ" else "ⲡϫⲱⲱⲙⲉ", Rect(r.center.x - 32 * u, ty - 7 * u, r.center.x + 32 * u, ty + 7 * u), TITULO, tinta(oscuro), 11f, minimo = 6f)
    }

    // =================================================================
    //  La página abierta y el escritor
    // =================================================================

    override val altoEncabezado: Dp = 118.dp

    /**
     * La cabecera de la hoja, enmarcada como el título de un tratado: arriba y abajo, las filas de diplés que apuntan al centro;
     * a la izquierda, el ouroboros con el cielo del día en letras hebreas; a la derecha, las siete esferas con el planeta del
     * día encendido. Al centro, el rótulo, la fecha del tema (el cielo arriba, en grande; el planeta y la fecha hebrea debajo)
     * y el detalle.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val tinta = p.tinta; val acento = p.acento
        diples(4 * u, w - 4 * u, 7 * u, 5 * u, acento.copy(alpha = 0.7f), 0.9f * u, haciaCentro = true, hueco = 10 * u)
        drawCircle(acento, 1.8f * u, Offset(w / 2, 7 * u))
        // El ouroboros con el cielo del día.
        val mc = Offset(32 * u, 60 * u)
        ouro(mc, 22 * u, 4.4f * u, tinta.copy(alpha = 0.85f), escamas = p.color.copy(alpha = 0.7f))
        textoEn(medidor, Arcontes.numero(e.numero), Rect(mc.x - 15 * u, mc.y - 10 * u, mc.x + 15 * u, mc.y + 10 * u), TITULO, acento, 13f, peso = FontWeight.Bold, minimo = 6f)
        // Las siete esferas con el planeta del día.
        esferasDia(Offset(w - 32 * u, 60 * u), 24 * u, tinta.copy(alpha = 0.5f), 0.7f * u, planetaDe(e.fecha), if (e.noche) p.detalle else acento)
        // El rótulo, la fecha y el detalle.
        val x0 = 64 * u; val x1 = w - 64 * u
        textoEn(medidor, e.rotulo, Rect(x0, 15 * u, x1, 31 * u), TITULO, acento, 10.5f, espaciado = 0.4f, minimo = 6f)
        enDos(medidor, e.fecha, Rect(x0, 33 * u, x1, 76 * u), TITULO, tinta, 17f, peso = FontWeight.Bold, k = 0.7f)
        textoEn(medidor, e.detalle, Rect(x0, 78 * u, x1, 94 * u), TEXTO, p.tenue, 10.5f, minimo = 6f)
        diples(x0, x1, 103 * u, 3.6f * u, acento.copy(alpha = 0.5f), 0.7f * u, haciaCentro = true, hueco = 7 * u)
        drawCircle(acento.copy(alpha = 0.6f), 1.2f * u, Offset(w / 2, 103 * u))
        drawLine(tinta.copy(alpha = 0.18f), Offset(4 * u, h - 5 * u), Offset(w - 4 * u, h - 5 * u), 0.6f * u)
    }

    /**
     * La hoja de papiro del códice: la juntura de dos hojas pegadas (la kollesis) a la derecha, el lugar del número de la hoja
     * arriba entre dos diplés, unos agujeros que se comió el tiempo en las orillas, el ouroboros como marca de agua y, al pie,
     * la fila de diplés donde iría el título. En modo oscuro, la lámpara alumbra el centro y las orillas quedan en penumbra. De
     * noche, las siete esferas se adivinan desde la esquina y un hilo de la grieta corre por el margen.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val tinta = p.tinta
        val xk = w * 0.84f
        drawRect(tinta.copy(alpha = 0.03f), Offset(xk, 0f), Size(5 * u, h))
        drawLine(tinta.copy(alpha = 0.06f), Offset(xk, 0f), Offset(xk, h), 0.6f * u)
        diples(w / 2 - 24 * u, w / 2 + 24 * u, 22 * u, 4 * u, p.acento.copy(alpha = 0.45f), 0.7f * u, haciaCentro = true, hueco = 8 * u)
        drawCircle(p.acento.copy(alpha = 0.5f), 1.4f * u, Offset(w / 2, 22 * u))
        // Los agujeros de las orillas.
        val hueco = if (p.oscura) Color.Black.copy(alpha = 0.4f) else mezcla(p.color, FIBRA, 0.6f).copy(alpha = 0.5f)
        for (k in 0 until 6) {
            val o = if (k < 4) Offset((5f + 5f * azar(61, k)) * u, h * (0.18f + 0.21f * k + 0.05f * azar(62, k))) else Offset(w * (0.32f + 0.34f * (k - 4)), h - (5f + 4f * azar(63, k)) * u)
            val s = (3f + 4f * azar(64, k)) * u
            val forma = contornoIrregular(Rect(o.x - s, o.y - s * 0.7f, o.x + s, o.y + s * 0.7f), 65 + k, 12, 0.22f)
            drawPath(forma, hueco)
            drawPath(forma, FIBRA.copy(alpha = 0.35f), style = Stroke(0.6f * u))
        }
        ouro(Offset(w / 2, h * 0.52f), w * 0.2f, w * 0.05f, tinta.copy(alpha = 0.03f))
        diples(w * 0.18f, w * 0.82f, h - 46 * u, 4.5f * u, p.acento.copy(alpha = 0.35f), 0.8f * u, haciaCentro = true, hueco = 14 * u)
        ouro(Offset(w / 2, h - 46 * u), 5 * u, 1.6f * u, p.acento.copy(alpha = 0.45f))
        // En modo oscuro, la hoja de noche: la lámpara alumbra el centro, donde se lee, y las orillas se van al azul negro.
        if (oscuro && !p.oscura) {
            drawRect(Brush.radialGradient(0f to LLAMA_HALO.copy(alpha = 0.1f), 0.42f to Color.Transparent, 1f to PENUMBRA.copy(alpha = 0.6f),
                center = Offset(w * 0.42f, h * 0.3f), radius = maxOf(w, h) * 0.95f))
            drawRect(Brush.horizontalGradient(listOf(PENUMBRA.copy(alpha = 0.35f), Color.Transparent), 0f, 18 * u), Offset.Zero, Size(18 * u, h))
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, PENUMBRA.copy(alpha = 0.35f)), w - 18 * u, w), Offset(w - 18 * u, 0f), Size(18 * u, h))
        }
        if (noche) {
            val o = Offset(w * 1.02f, h * 1.02f)
            for (i in 0 until 7) drawCircle(p.detalle.copy(alpha = 0.09f - i * 0.007f), w * (0.25f + 0.17f * i), o, style = Stroke(0.8f * u))
            grietaLuz(Offset(w - 9 * u, h * 0.2f), Offset(w - 11 * u, h * 0.3f), 0.6f * u, 0.3f, 29)
        }
    }

    /** La barra de arriba es el canto de cuero de la tapa: el filete grabado y, cada tanto, la puntada del lazo que la cose. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(if (p.oscura) mezcla(p.color, Color.Black, 0.3f) else mezcla(mezcla(p.color, PAPIRO_OSC, 0.45f), Color.Black, if (oscuro) 0.3f else 0f))
        val banda = Rect(0f, h - 8 * u, w, h)
        val cu = t(CUERO, oscuro, noche)
        drawRect(Brush.verticalGradient(listOf(mezcla(cu, Color.White, 0.12f), cu, mezcla(cu, Color.Black, 0.3f)), banda.top, banda.bottom), banda.topLeft, banda.size)
        drawLine(mezcla(cu, Color.Black, 0.45f), Offset(0f, banda.center.y), Offset(w, banda.center.y), 0.6f * u)
        drawLine(Color.White.copy(alpha = 0.12f), Offset(0f, banda.center.y + 0.7f * u), Offset(w, banda.center.y + 0.7f * u), 0.4f * u)
        var x = 16 * u
        while (x < w - 6 * u) {
            drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(x - 0.6f * u, banda.top - 0.5f * u), Size(2.6f * u, banda.height + 2 * u), CornerRadius(1.3f * u))
            drawRoundRect(t(CORREA, oscuro, noche), Offset(x - 1.3f * u, banda.top - 1.5f * u), Size(2.6f * u, banda.height + 2 * u), CornerRadius(1.3f * u))
            x += 48 * u
        }
        drawLine(p.acento.copy(alpha = 0.6f), Offset(0f, banda.top - 1.4f * u), Offset(w, banda.top - 1.4f * u), 0.8f * u)
    }

    /** El botón de guardar es una tablilla de barro cocido, con su filete hundido, sus poros y el brillo de la cocción. */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(2 * u, 2 * u, w - 3 * u, h - 3.5f * u)
        val rad = minOf(r.height / 2f, 12 * u)
        if (oscuro || noche) drawRoundRect(LLAMA_HALO.copy(alpha = 0.2f), r.inflate(3 * u).topLeft, r.inflate(3 * u).size, CornerRadius(rad + 3 * u))
        drawRoundRect(Color.Black.copy(alpha = 0.3f), r.topLeft + Offset(1.2f * u, 2.2f * u), r.size, CornerRadius(rad))
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(BARRO_CLARO, BARRO, 0.3f), BARRO, mezcla(BARRO, Color.Black, 0.25f)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(rad))
        for (k in 0 until 30) drawCircle(Color.Black.copy(alpha = 0.12f), (0.3f + 0.5f * azar(17, k)) * u, Offset(r.left + r.width * azar(19, k), r.top + r.height * azar(23, k)))
        val dentro = r.deflate(3 * u)
        val rd = CornerRadius((rad - 3 * u).coerceAtLeast(0f))
        drawRoundRect(Color.White.copy(alpha = 0.16f), dentro.topLeft + Offset(0.6f * u, 0.6f * u), dentro.size, rd, style = Stroke(0.5f * u))
        drawRoundRect(mezcla(BARRO, Color.Black, 0.45f).copy(alpha = 0.7f), dentro.topLeft, dentro.size, rd, style = Stroke(0.8f * u))
        drawRoundRect(Color.White.copy(alpha = 0.15f), Offset(r.left + 4 * u, r.top + 1.5f * u), Size(r.width - 8 * u, r.height * 0.3f), CornerRadius(rad))
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = Color(0xFFFFF1D6)

    // =================================================================
    //  La libreta de momentos: la jarra sellada con el códice
    // =================================================================

    override val altoLibreta: Dp = 268.dp

    /**
     * La jarra sellada, en la cueva, como la que guardó los códices de Nag Hammadi: la caliza de la pared con el nicho tallado
     * donde está la jarra alta de barro, con sus asas, las marcas del torno y la franja peinada del hombro; la boca tapada con
     * un cuenco boca abajo, sellado con barro y atado con dos vueltas de cordel, y la bula con el ouroboros sobre el nudo. Del
     * cuello cuelga la tira de papiro con el nombre y la cuenta. En el nicho, la lámpara en su repisa de roca; en la pared,
     * la grieta de luz; en el suelo, sobre la tierra, los códices que van saliendo de la jarra (más cuantas más hojas). De día
     * entra el sol con su polvo dorado; de noche, la lámpara encendida. Al abrirse, la bula se parte, el cordel se suelta, la
     * tapa se levanta y sale de la boca una luz blanca y violeta, poca, y asoman los lomos de los códices.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        // Lo que alcanza la lámpara del nicho: la jarra, su tapa y su sello; la repisa, más.
        fun j(x: Color) = luz(x, oscuro, false, 0.45f)
        val a = e.abierto.coerceIn(0f, 1f)
        val romper = (a / 0.3f).coerceIn(0f, 1f)
        val alzar = ((a - 0.25f) / 0.75f).coerceIn(0f, 1f)
        // La pared de la cueva y el nicho tallado.
        piedra(Rect(0f, 0f, w, h), c(CALIZA), 131)
        val ni = Rect(w * 0.07f, h * 0.06f, w * 0.6f, h * 0.86f)
        val boca = nicho(ni)
        clipPath(boca) {
            piedra(ni.inflate(2 * u), c(HUECO), 137, 0.7f)
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.35f), Color.Transparent), ni.top, ni.top + ni.height * 0.45f), ni.topLeft, ni.size)
            drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent), ni.left, ni.left + 12 * u), ni.topLeft, Size(12 * u, ni.height))
        }
        drawPath(boca, Color.Black.copy(alpha = 0.28f), style = Stroke(0.8f * u))
        translate(0.7f * u, 0.7f * u) { drawPath(nicho(ni.inflate(5 * u)), Color.White.copy(alpha = if (oscuro) 0.06f else 0.25f), style = Stroke(0.7f * u)) }
        drawPath(nicho(ni.inflate(5 * u)), c(CALIZA_SOMBRA), style = Stroke(0.8f * u))
        // La grieta de la pared, que se enciende más al abrirse la jarra.
        grietaLuz(Offset(w * 0.86f, h * 0.05f), Offset(w * 0.88f, h * 0.2f), 1f * u, ((if (oscuro) 0.7f else 0.35f) + 0.4f * alzar).coerceAtMost(1f), 21)
        // El suelo de tierra.
        val suelo = h * 0.86f
        drawRect(Brush.verticalGradient(listOf(c(mezcla(TIERRA, Color.Black, 0.3f)), c(TIERRA)), suelo, h), Offset(0f, suelo), Size(w, h - suelo))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, suelo), Size(w, 2 * u))
        for (k in 0 until 50) drawCircle(c(mezcla(TIERRA, if (k % 3 == 0) Color.White else Color.Black, 0.3f)).copy(alpha = 0.4f), (0.3f + 0.6f * azar(139, k)) * u,
            Offset(w * azar(141, k), suelo + (h - suelo) * azar(143, k)))
        // La lámpara, en su repisa de roca dentro del nicho.
        val rep = Rect(ni.left + 4 * u, ni.top + ni.height * 0.44f, ni.left + 40 * u, ni.top + ni.height * 0.44f + 5 * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(rep.left + 1.5f * u, rep.bottom), Size(rep.width, 4 * u))
        drawRect(luz(CALIZA_LUZ, oscuro, false, 0.85f), rep.topLeft, Size(rep.width, 2 * u))
        drawRect(luz(CALIZA, oscuro, false, 0.85f), Offset(rep.left, rep.top + 2 * u), Size(rep.width, rep.height - 2 * u))
        lamparaRoca(Offset(rep.center.x, rep.top - 30 * u * 0.17f), 30 * u, oscuro, false, oscuro)
        // Los códices que ya salieron de la jarra, en el suelo: más cuantas más hojas.
        val n = (1 + e.hojas / 15).coerceIn(1, 4)
        val g = ((6f + 1.6f * sqrt(e.hojas.coerceAtLeast(0).toFloat())) / n).coerceIn(7f, 14f) * u
        var y = h * 0.95f
        for (j in 0 until n) {
            val x0 = w * (0.6f + 0.03f * azar(e.semilla, j)); val x1 = x0 + w * 0.3f
            val r = Rect(x0, y - g, x1, y)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + 2 * u, r.top + 2 * u), r.size)
            val cuero = luz(CUEROS[Math.floorMod(e.semilla + j, CUEROS.size)], oscuro, false, 0.25f)
            drawRoundRect(cuero, r.topLeft, r.size, CornerRadius(1.5f * u))
            val hojas = Rect(r.left + 3 * u, r.top + 1.6f * u, r.right - 1 * u, r.bottom - 1.6f * u)
            drawRect(papel(oscuro, false, 0.25f), hojas.topLeft, hojas.size)
            var yy = hojas.top + 1 * u
            while (yy < hojas.bottom) { drawLine(FIBRA.copy(alpha = 0.3f), Offset(hojas.left, yy), Offset(hojas.right, yy), 0.4f * u); yy += 1.1f * u }
            drawLine(Color.White.copy(alpha = 0.15f), Offset(r.left, r.top + 0.4f * u), Offset(r.right, r.top + 0.4f * u), 0.5f * u)
            // El lazo que ata cada códice, con su cabo.
            val xl = r.left + r.width * (0.3f + 0.3f * azar(e.semilla + 2, j))
            drawLine(luz(CORREA, oscuro, false, 0.25f), Offset(xl, r.top - 0.5f * u), Offset(xl, r.bottom + 0.5f * u), 2.2f * u)
            if (j == n - 1) cordel(Path().apply { moveTo(xl, r.top); quadraticTo(xl + 6 * u, r.top - 5 * u, xl + 12 * u, r.top - 1 * u) }, luz(CORREA, oscuro, false, 0.25f), 1.4f * u)
            y -= g + 0.6f * u
        }
        // La jarra.
        val jc = w * 0.34f
        val pie = h * 0.92f
        val alto = h * 0.7f
        val hw = minOf(w * 0.115f, alto * 0.25f)
        val cuello = hw * 0.5f
        val bocaY = pie - alto
        val barro = j(mezcla(BARRO_CLARO, CALIZA_SOMBRA, 0.22f))
        drawOval(Color.Black.copy(alpha = 0.35f), Offset(jc - hw * 1.05f, pie - 4 * u), Size(hw * 2.4f, 9 * u))
        val cuerpo = Path().apply {
            moveTo(jc - hw * 0.3f, pie)
            cubicTo(jc - hw * 1.02f, pie - alto * 0.1f, jc - hw * 1.1f, pie - alto * 0.56f, jc - hw * 0.8f, pie - alto * 0.8f)
            cubicTo(jc - hw * 0.6f, pie - alto * 0.9f, jc - cuello, pie - alto * 0.9f, jc - cuello, pie - alto * 0.95f)
            lineTo(jc - cuello * 1.18f, bocaY)
            lineTo(jc + cuello * 1.18f, bocaY)
            lineTo(jc + cuello, pie - alto * 0.95f)
            cubicTo(jc + cuello, pie - alto * 0.9f, jc + hw * 0.6f, pie - alto * 0.9f, jc + hw * 0.8f, pie - alto * 0.8f)
            cubicTo(jc + hw * 1.1f, pie - alto * 0.56f, jc + hw * 1.02f, pie - alto * 0.1f, jc + hw * 0.3f, pie)
            close()
        }
        // Las asas, del hombro al cuerpo.
        listOf(-1f, 1f).forEach { s ->
            drawPath(Path().apply {
                moveTo(jc + s * hw * 0.62f, pie - alto * 0.86f)
                cubicTo(jc + s * hw * 1.18f, pie - alto * 0.88f, jc + s * hw * 1.2f, pie - alto * 0.7f, jc + s * hw * 0.98f, pie - alto * 0.66f)
            }, mezcla(barro, Color.Black, 0.25f), style = Stroke(hw * 0.12f, cap = StrokeCap.Round))
        }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(mezcla(barro, Color.White, 0.12f), barro, mezcla(barro, Color.Black, 0.2f), mezcla(barro, Color.Black, 0.45f)), jc - hw * 1.1f, jc + hw * 1.1f))
        clipPath(cuerpo) {
            for (k in 1..16) {
                val yy = pie - alto * (0.05f * k)
                drawLine(Color.Black.copy(alpha = 0.06f), Offset(jc - hw * 1.2f, yy), Offset(jc + hw * 1.2f, yy + 1 * u), 0.6f * u)
            }
            // La franja peinada del hombro: dos ondas paralelas.
            for (j in 0 until 2) {
                val y0 = pie - alto * (0.72f - 0.03f * j)
                val onda = Path().apply {
                    moveTo(jc - hw * 1.1f, y0)
                    var x = jc - hw * 1.1f
                    while (x < jc + hw * 1.1f) { quadraticTo(x + hw * 0.1f, y0 - 2.4f * u, x + hw * 0.2f, y0); quadraticTo(x + hw * 0.3f, y0 + 2.4f * u, x + hw * 0.4f, y0); x += hw * 0.4f }
                }
                drawPath(onda, mezcla(barro, Color.Black, 0.4f).copy(alpha = 0.55f), style = Stroke(0.8f * u))
            }
            // Las manchas y los desconchones de los años.
            for (k in 0 until 7) mancha(Offset(jc + hw * (azar(147, k) * 1.8f - 0.9f), pie - alto * (0.1f + 0.6f * azar(149, k))), (4f + 8f * azar(151, k)) * u, Color.Black.copy(alpha = 0.08f))
            for (k in 0 until 3) {
                val o = Offset(jc + hw * (azar(153, k) * 1.4f - 0.7f), pie - alto * (0.15f + 0.5f * azar(155, k)))
                drawPath(contornoIrregular(Rect(o.x, o.y, o.x + 6 * u, o.y + 3.5f * u), 157 + k, 8, 0.25f), mezcla(barro, Color.White, 0.3f).copy(alpha = 0.6f))
            }
        }
        drawPath(cuerpo, Color.Black.copy(alpha = 0.3f), style = Stroke(0.8f * u))
        // Por dentro de la boca, al abrirse: la oscuridad, los lomos de los códices y la luz que sale.
        val bocaR = Rect(jc - cuello * 1.18f, bocaY - cuello * 0.32f, jc + cuello * 1.18f, bocaY + cuello * 0.32f)
        if (alzar > 0f) {
            drawOval(c(Color(0xFF1C140E)), bocaR.topLeft, bocaR.size)
            clipPath(Path().apply { addOval(bocaR) }) {
                listOf(-0.5f, 0.05f, 0.55f).forEachIndexed { k, f ->
                    val x = jc + cuello * f
                    drawRoundRect(j(CUEROS[k]), Offset(x - cuello * 0.24f, bocaY - cuello * 0.1f - k * 0.8f * u), Size(cuello * 0.48f, cuello * 1.2f), CornerRadius(1.5f * u))
                    drawLine(j(CORREA), Offset(x - cuello * 0.24f, bocaY + cuello * 0.14f), Offset(x + cuello * 0.24f, bocaY + cuello * 0.14f), 1.2f * u)
                }
                mancha(Offset(jc, bocaY), cuello * 1.4f, GRIETA.copy(alpha = 0.6f * alzar))
            }
            val luz = Path().apply {
                moveTo(bocaR.left + cuello * 0.3f, bocaY); lineTo(bocaR.right - cuello * 0.3f, bocaY)
                lineTo(jc + cuello * 2.2f, bocaY - h * 0.3f); lineTo(jc - cuello * 2.2f, bocaY - h * 0.3f); close()
            }
            drawPath(luz, Brush.verticalGradient(listOf(GRIETA_HALO.copy(alpha = 0.32f * alzar), Color.Transparent), bocaY, bocaY - h * 0.3f))
            mancha(Offset(jc, bocaY - cuello * 0.6f), cuello * 4.5f, GRIETA_HALO.copy(alpha = 0.24f * alzar))
        }
        // El cordel: dos vueltas al cuello que sujetan la tapa; se afloja al partirse la bula.
        val cordelA = (1f - romper * 1.3f).coerceIn(0f, 1f)
        if (cordelA > 0f) listOf(bocaY + 8 * u, bocaY + 12 * u).forEach { yy ->
            cordel(Path().apply { moveTo(jc - cuello * 1.1f, yy - 1 * u); quadraticTo(jc, yy + 2.5f * u, jc + cuello * 1.1f, yy - 1 * u) }, j(CORDEL).copy(alpha = cordelA), 1.3f * u)
        }
        // La tapa: el cuenco boca abajo con el barro del sello alrededor; al abrirse se levanta y se ladea.
        withTransform({
            translate(hw * 0.9f * alzar, -h * 0.17f * alzar)
            rotate(32f * alzar, Offset(jc, bocaY))
        }) {
            val sello = Path().apply {
                moveTo(jc - cuello * 1.45f, bocaY + 6 * u)
                var x = jc - cuello * 1.45f; var j = 0
                while (x < jc + cuello * 1.45f) { lineTo(x, bocaY + (5f + 2.5f * azar(161, j)) * u); x += 3 * u; j++ }
                lineTo(jc + cuello * 1.45f, bocaY + 5 * u); lineTo(jc + cuello * 1.3f, bocaY - 2 * u); lineTo(jc - cuello * 1.3f, bocaY - 2 * u); close()
            }
            // El sello de barro oscuro, apretado a mano alrededor de la tapa, con las marcas de los dedos.
            val selloCol = j(mezcla(BARRO, Color(0xFF2E2620), 0.45f))
            drawPath(sello, Brush.horizontalGradient(listOf(mezcla(selloCol, Color.White, 0.12f), selloCol, mezcla(selloCol, Color.Black, 0.35f)), jc - cuello * 1.45f, jc + cuello * 1.45f))
            for (k in 0 until 4) drawOval(Color.White.copy(alpha = 0.08f), Offset(jc - cuello * 1.1f + k * cuello * 0.6f, bocaY - 0.5f * u), Size(cuello * 0.4f, 3 * u))
            val tapa = Path().apply {
                moveTo(jc - cuello * 1.5f, bocaY - 1 * u)
                cubicTo(jc - cuello * 1.45f, bocaY - cuello * 1.25f, jc + cuello * 1.45f, bocaY - cuello * 1.25f, jc + cuello * 1.5f, bocaY - 1 * u)
                close()
            }
            drawPath(tapa, Brush.horizontalGradient(listOf(mezcla(barro, Color.White, 0.14f), barro, mezcla(barro, Color.Black, 0.35f)), jc - cuello * 1.5f, jc + cuello * 1.5f))
            drawPath(tapa, Color.Black.copy(alpha = 0.3f), style = Stroke(0.7f * u))
            drawOval(mezcla(barro, Color.Black, 0.3f), Offset(jc - cuello * 0.32f, bocaY - cuello * 1.02f), Size(cuello * 0.64f, cuello * 0.22f))
        }
        // La bula sobre el nudo: entera, o partida en dos.
        val pc = Offset(jc, bocaY + 12 * u)
        val rb = minOf(cuello * 0.7f, 9 * u)
        if (romper <= 0f) bullaArcontes(pc, rb, j(BARRO_CLARO), 1f, e.semilla)
        else if (romper < 1f || alzar < 0.6f) {
            val pr = Rect(pc.x - rb * 1.15f, pc.y - rb * 1.15f, pc.x + rb * 1.15f, pc.y + rb * 1.15f)
            withTransform({ translate(-2 * u * romper, -1 * u * romper); rotate(-10f * romper, pc) }) { clipPath(mitad(pr, pc.y, true, e.semilla)) { bullaArcontes(pc, rb, j(BARRO_CLARO), 1f, e.semilla) } }
            withTransform({ translate(2 * u * romper, 4 * u * romper); rotate(8f * romper, pc) }) { clipPath(mitad(pr, pc.y, false, e.semilla)) { bullaArcontes(pc, rb, j(BARRO_CLARO), 1f, e.semilla) } }
        }
        // La tira de papiro con el nombre y la cuenta, colgada del cuello.
        val tira = Rect(w * 0.55f, h * 0.3f, w * 0.96f, h * 0.55f)
        cordel(Path().apply { moveTo(jc + cuello, bocaY + 10 * u); quadraticTo(w * 0.5f, bocaY + 2 * u, tira.left + 6 * u, tira.top + 3 * u) }, j(CORDEL), 1.1f * u)
        rotate(-2f, tira.center) {
            hoja(tira, papel(oscuro, false, 0.4f), e.semilla + 3, 0.3f, 0.02f)
            drawCircle(Color.Black.copy(alpha = 0.4f), 1.6f * u, Offset(tira.left + 6 * u, tira.top + 4 * u))
            textoEn(medidor, e.nombre, Rect(tira.left + 10 * u, tira.top + tira.height * 0.08f, tira.right - 8 * u, tira.top + tira.height * 0.52f), TITULO, tinta(oscuro), 15f, peso = FontWeight.Bold, minimo = 8f)
            diples(tira.left + 14 * u, tira.right - 12 * u, tira.top + tira.height * 0.57f, 3.2f * u, ocre(oscuro).copy(alpha = 0.7f), 0.6f * u, haciaCentro = true, hueco = 5 * u)
            textoEn(medidor, e.cuenta, Rect(tira.left + 8 * u, tira.top + tira.height * 0.63f, tira.right - 8 * u, tira.bottom - tira.height * 0.08f), TEXTO, tinta(oscuro).copy(alpha = 0.85f), 10f, minimo = 6f)
        }
        // La luz: de día, el sol que entra desde la boca de la cueva; de noche, el resplandor de la lámpara.
        if (!oscuro) rayo(Offset(-w * 0.1f, 0f) to Offset(w * 0.16f, 0f), Offset(w * 0.22f, suelo) to Offset(w * 0.7f, suelo), 0.85f, 41)
        else resplandor(Offset(rep.center.x + 14 * u, rep.top - 15 * u), w * 0.7f)
    }

    /**
     * La pestaña de un día: la etiqueta de papiro que se ataba a los rollos y a los códices, de orillas roídas, con su agujero y
     * el cordel; en el cordel, la cuenta del color del ánimo (de hueso, si no hay). La letra hebrea del día de la semana va
     * arriba y el número del día en grande.
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        rotate(-2f + 4f * azar(e.semilla, 1), Offset(w / 2, h / 2)) {
            val tira = Rect(4 * u, 10 * u, w - 4 * u, h - 3 * u)
            hoja(tira, papel(oscuro, false), e.semilla, 0.3f, 0.025f)
            val agujero = Offset(w / 2, tira.top + 4 * u)
            cordel(Path().apply { moveTo(agujero.x - 0.6f * u, agujero.y); lineTo(w / 2 - 3 * u, 0f) }, t(CORDEL, oscuro, false), 0.9f * u)
            cordel(Path().apply { moveTo(agujero.x + 0.6f * u, agujero.y); lineTo(w / 2 + 3 * u, 0f) }, t(CORDEL, oscuro, false), 0.9f * u)
            drawCircle(Color.Black.copy(alpha = 0.45f), 1.4f * u, agujero)
            cuenta(Offset(w / 2, 4.5f * u), 2.6f * u, e.tono, oscuro)
            textoEn(medidor, e.semana, Rect(tira.right - 16 * u, tira.top + 1.5f * u, tira.right - 2 * u, tira.top + 11 * u), TITULO, ocre(oscuro), 8f, minimo = 5f)
            textoEn(medidor, e.dia, Rect(tira.left, tira.top + 10 * u, tira.right, tira.bottom - 8 * u), TITULO, tinta(oscuro), 17f, peso = FontWeight.Bold, minimo = 8f)
            diples(tira.left + 5 * u, tira.right - 5 * u, tira.bottom - 4.5f * u, 2.6f * u, ocre(oscuro).copy(alpha = 0.6f), 0.5f * u, haciaCentro = true, hueco = 2 * u)
        }
    }

    /** El rótulo de un mes: la tira de papiro con el nombre, entre dos filas de diplés que apuntan hacia ella. */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val tam = 11f
        val largo = (anchoTx(medidor, texto, TITULO, tam, 0.5f, FontWeight.Bold) + 30 * u).coerceAtMost(w - 80 * u)
        val tira = Rect(w / 2 - largo / 2, h / 2 - 10.5f * u, w / 2 + largo / 2, h / 2 + 10.5f * u)
        val tinte = if (oscuro) mezcla(ocre(oscuro), LLAMA, 0.6f) else ocre(oscuro)
        diples(12 * u, tira.left - 8 * u, h / 2, 6 * u, tinte.copy(alpha = 0.7f), 0.9f * u)
        diples(tira.right + 8 * u, w - 12 * u, h / 2, 6 * u, tinte.copy(alpha = 0.7f), 0.9f * u, izquierda = true)
        drawCircle(tinte, 1.8f * u, Offset(6 * u, h / 2)); drawCircle(tinte, 1.8f * u, Offset(w - 6 * u, h / 2))
        hoja(tira, papel(oscuro, false), texto.length * 7 + 3, 0.25f, 0.03f)
        textoEn(medidor, texto, Rect(tira.left + 8 * u, tira.top, tira.right - 8 * u, tira.bottom), TITULO, tinta(oscuro), tam, peso = FontWeight.Bold, espaciado = 0.5f, minimo = 6f)
    }

    // =================================================================
    //  Las cartas: cartas entre comunidades escondidas
    // =================================================================

    /** La solapa que gira sobre su bisagra horizontal en [bisagra]: con [abrir] en 0 cuelga, en 1 apunta arriba y se ve por dentro. */
    private fun DrawScope.solapa(bisagra: Float, abrir: Float, bloque: DrawScope.(Boolean) -> Unit) {
        val k = cos(PI * abrir.coerceIn(0f, 1f)).toFloat()
        val esc = if (abs(k) < 0.02f) 0.02f * (if (k < 0f) -1f else 1f) else k
        withTransform({ scale(1f, esc, Offset(size.width / 2f, bisagra)) }) { bloque(k < 0f) }
    }

    /**
     * La carta, como se mandaban entonces: la hoja de papiro doblada en un paquete, con sus pliegues y sus fibras; el último
     * doblez hace de solapa y por dentro deja ver la tinta de lo escrito. A quién va se escribe con tinta por fuera, entre una
     * fila de diplés. La ata un cordel de pelo de cabra, que da dos vueltas, y sobre el nudo, a caballo en el borde del doblez,
     * va la bula de barro con el ouroboros: cuando la carta ya se puede abrir, por la juntura se escapa la luz blanca y violeta;
     * al abrirla, la bula se parte. Dentro, la hoja desplegada, con sus renglones.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        val cu = e.cuerpo
        val s = Rect(cu.left + 4 * u, cu.top + 2 * u, cu.right - 4 * u, cu.bottom - 5 * u)
        val w = s.width; val h = s.height
        val k = (w / (164f * u)).coerceIn(0.8f, 2.2f)
        val bis = s.top
        val borde = bis + h * 0.32f
        val ab = e.abrir.coerceIn(0f, 1f)
        val cerrada = ab < 0.5f
        // De noche, el papiro a oscuras que alcanza la lámpara: lo escrito por fuera, con la tinta clara.
        val papiro = if (oscuro) luz(Color(0xFFE6D8B4), true, false, 0.45f) else Color(0xFFE6D8B4)
        val papiroOsc = if (oscuro) luz(Color(0xFFCDB98C), true, false, 0.3f) else Color(0xFFCDB98C)
        val pelo = c(Color(0xFF3B3026))
        val ro = minOf(w, h) * 0.13f
        val pc = Offset(s.center.x, borde)
        val pr = Rect(pc.x - ro * 1.15f, pc.y - ro * 1.15f, pc.x + ro * 1.15f, pc.y + ro * 1.15f)
        fun DrawScope.bula(alfa: Float) = bullaArcontes(pc, ro, mezcla(luz(BARRO_CLARO, oscuro, false, 0.55f), LLAMA_HALO, e.brillo * 0.2f), alfa, e.semilla)

        sombraSuave(s, 3 * u, 6 * u, 0.28f)
        drawRect(papiroOsc, s.topLeft, s.size)
        // El último doblez, que hace de solapa: por fuera el papiro con sus fibras; por dentro, la tinta de la carta.
        fun DrawScope.tapaSolapa(adentro: Boolean) {
            val p = Path().apply {
                moveTo(s.left, bis); lineTo(s.right, bis); lineTo(s.right, borde)
                var x = s.right; var j = 0
                while (x > s.left) { lineTo(x, borde + (azar(e.semilla, j) - 0.5f) * 1.6f * u); x -= 6 * u; j++ }
                lineTo(s.left, borde); close()
            }
            if (adentro) {
                drawPath(p, mezcla(papiro, Color.White, 0.08f))
                clipPath(p) {
                    var y = bis + 6 * u; var j = 0
                    while (y < borde - 3 * u) { drawLine(tinta(oscuro).copy(alpha = 0.14f), Offset(s.left + w * 0.1f, y), Offset(s.right - w * (0.1f + 0.2f * azar(e.semilla + 1, j)), y), 0.9f * u); y += 5.5f * u; j++ }
                }
                return
            }
            drawLine(Color.Black.copy(alpha = 0.25f), Offset(s.left, borde + 1.4f * u), Offset(s.right, borde + 1.4f * u), 1.6f * u)
            drawPath(p, Brush.verticalGradient(listOf(mezcla(papiro, Color.White, 0.1f), papiro), bis, borde))
            clipPath(p) { fibras(Rect(s.left, bis, s.right, borde), FIBRA.copy(alpha = 0.2f), e.semilla + 3, 2.2f * u * k) }
            drawPath(p, FIBRA.copy(alpha = 0.5f), style = Stroke(0.6f * u))
            if (e.romper > 0f) withTransform({ translate(-1.2f * u * e.romper, -2.5f * u * e.romper); rotate(-7f * e.romper, pc) }) {
                clipPath(mitad(pr, pc.y, true, e.semilla)) { bula(1f) }
            }
        }
        if (!cerrada) solapa(bis, ab) { tapaSolapa(it) }
        // La hoja desplegada, que sale.
        if (e.subir > 0f) {
            val arriba0 = s.top + h * 0.05f
            val alza = minOf(e.subir.coerceIn(0f, 1f) * h * 0.9f, (arriba0 - 4 * u).coerceAtLeast(0f))
            val hj = Rect(s.left + w * 0.07f, arriba0 - alza, s.right - w * 0.07f, arriba0 - alza + h * 0.9f)
            // La hoja que sale queda cerca de la lámpara: de noche, apenas en penumbra y tibia.
            val papelCarta = if (oscuro) mezcla(mezcla(e.hoja, PENUMBRA, 0.22f), LLAMA, 0.06f) else e.hoja
            hoja(hj, papelCarta, e.semilla + 1, 0.22f, 0.006f)
            diples(hj.left + 8 * u, hj.right - 8 * u, hj.top + 5 * u, 3 * u, ocre(oscuro).copy(alpha = 0.6f), 0.6f * u, haciaCentro = true, hueco = 5 * u)
            var y = hj.top + 6 * u * k + 11 * u
            while (y < hj.bottom - 3 * u) { drawLine(e.renglones, Offset(hj.left + 6 * u, y), Offset(hj.right - 6 * u, y), 0.7f * u); y += 11 * u }
            drawLine(Color.Black.copy(alpha = 0.08f), Offset(hj.center.x, hj.top), Offset(hj.center.x, hj.bottom), 0.8f * u)
        }
        // El paquete: el papiro doblado, con sus fibras y sus pliegues.
        val forma = contornoIrregular(s, e.semilla + 7, 60, 0.004f)
        drawPath(forma, Brush.verticalGradient(listOf(papiro, mezcla(papiro, papiroOsc, 0.5f)), s.top, s.bottom))
        clipPath(forma) {
            fibras(s, FIBRA.copy(alpha = 0.2f), e.semilla, 2.4f * u * k)
            drawLine(Color.Black.copy(alpha = 0.1f), Offset(s.center.x, s.top), Offset(s.center.x, s.bottom), 1f * u)
            drawLine(Color.White.copy(alpha = 0.22f), Offset(s.center.x + 1 * u, s.top), Offset(s.center.x + 1 * u, s.bottom), 0.6f * u)
            drawLine(Color.Black.copy(alpha = 0.08f), Offset(s.left, s.top + h * 0.7f), Offset(s.right, s.top + h * 0.7f), 0.9f * u)
            drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.08f), Color.Transparent, Color.Transparent, Color.Black.copy(alpha = 0.1f)), s.left, s.right), s.topLeft, s.size)
        }
        drawPath(forma, FIBRA.copy(alpha = 0.55f), style = Stroke(0.7f * u))
        if (!cerrada) drawRect(Color.Black.copy(alpha = 0.3f), Offset(s.left, s.top), Size(w, 1.6f * u))
        if (oscuro) clipPath(forma) { mancha(Offset(s.right, s.top), w * 0.7f, LLAMA_HALO.copy(alpha = 0.12f)) }
        // A quién va, escrito con tinta por fuera.
        val dir = Rect(s.left + w * 0.1f, s.top + h * 0.45f, s.right - w * 0.1f, s.bottom - h * 0.07f)
        textoEn(medidor, e.linea1, Rect(dir.left, dir.top, dir.right, dir.top + dir.height * 0.5f), TITULO, tinta(oscuro), 11.5f, peso = FontWeight.Bold, minimo = 6.5f)
        if (e.linea1.isNotBlank() || e.linea2.isNotBlank())
            diples(dir.left + dir.width * 0.22f, dir.right - dir.width * 0.22f, dir.top + dir.height * 0.55f, 3 * u, ocre(oscuro).copy(alpha = 0.6f), 0.6f * u, haciaCentro = true, hueco = 4 * u)
        textoEn(medidor, e.linea2, Rect(dir.left, dir.top + dir.height * 0.6f, dir.right, dir.bottom), TEXTO, tinta(oscuro).copy(alpha = 0.85f), 9.5f, minimo = 5.5f)
        if (cerrada) solapa(bis, ab) { tapaSolapa(it) }
        // La luz que se escapa por la juntura de la carta que ya se puede abrir.
        if (e.brillo > 0f && cerrada) {
            val a0 = Offset(s.left + w * 0.04f, borde + 0.6f * u); val a1 = Offset(s.right - w * 0.04f, borde + 0.6f * u)
            drawLine(GRIETA_HALO.copy(alpha = 0.18f * e.brillo), a0, a1, 7 * u, StrokeCap.Round)
            drawLine(GRIETA_HALO.copy(alpha = 0.35f * e.brillo), a0, a1, 3 * u, StrokeCap.Round)
            drawLine(GRIETA.copy(alpha = 0.9f * e.brillo), a0, a1, 0.8f * u, StrokeCap.Round)
        }
        // El cordel de pelo de cabra: se va atando en dos vueltas; abierta la carta, queda suelto al pie.
        if (e.cordon > 0f && cerrada) {
            val f = e.cordon.coerceIn(0f, 1f)
            cordel(Offset(s.left - 1 * u, borde + 1 * u), Offset(s.left - 1 * u + (w + 2 * u) * (f * 2f).coerceAtMost(1f), borde - 0.5f * u), pelo, 1.6f * u * k)
            if (f > 0.5f) cordel(Offset(s.left - 1 * u, borde + 4.5f * u), Offset(s.left - 1 * u + (w + 2 * u) * ((f - 0.5f) * 2f), borde + 3.5f * u), pelo, 1.4f * u * k)
            if (f >= 1f) {
                cordel(Path().apply { moveTo(pc.x, pc.y); quadraticTo(pc.x - 6 * u, pc.y + 6 * u, pc.x - 9 * u, pc.y + 15 * u) }, pelo, 1.2f * u * k)
                cordel(Path().apply { moveTo(pc.x, pc.y); quadraticTo(pc.x + 4 * u, pc.y + 8 * u, pc.x + 5 * u, pc.y + 17 * u) }, pelo, 1.2f * u * k)
            }
        } else if (e.romper > 0.5f) {
            cordel(Path().apply { moveTo(s.left + w * 0.05f, s.bottom + 1 * u); cubicTo(s.left + w * 0.3f, s.bottom - 6 * u, s.left + w * 0.6f, s.bottom + 5 * u, s.right - w * 0.08f, s.bottom - 2 * u) }, pelo, 1.3f * u * k)
        }
        // La bula: entera (y, al sellar, cayendo desde grande), o partida por el borde del doblez.
        if (e.romper <= 0f) {
            if (e.brillo > 0f) drawCircle(Brush.radialGradient(listOf(GRIETA_HALO.copy(alpha = 0.45f * e.brillo), Color.Transparent), center = pc, radius = ro * 2.4f), ro * 2.4f, pc)
            if (e.alfaSello > 0f) withTransform({ scale(e.escalaSello, e.escalaSello, pc) }) { bula(e.alfaSello) }
        } else {
            withTransform({ translate(1f * u * e.romper, 3f * u * e.romper); rotate(-4f + 8f * e.romper, pc) }) {
                clipPath(mitad(pr, pc.y, false, e.semilla)) { bula(1f) }
            }
        }
    }

    // La hoja desplegada queda arriba, entre su fila de diplés y la boca del paquete.
    override val cajaPrevia = Rect(0.12f, 0.16f, 0.88f, 0.31f)

    override val altoBuzon: Dp = 210.dp

    /**
     * El escondite de las cartas entre las comunidades, en la roca de la cueva: arriba, la repisa con la lámpara, la tira de
     * papiro colgada con «Cartas escondidas», la grieta de luz y, apoyada en la pared, la losa que tapa el escondite. Debajo,
     * tres nichos tallados, cada uno con su tira: las que ya se pueden abrir (con la luz que se escapa por la juntura y la bula
     * clara), las atadas (con su cordel y la bula) y las leídas (desplegadas). Caben siete por nicho; si hay más, se dice
     * cuántas más. Abajo, el umbral de piedra.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        piedra(Rect(0f, 0f, w, h), c(CALIZA), 151)
        // La repisa de arriba, con la lámpara, la grieta y la losa del escondite.
        val rep = Rect(w * 0.03f, 42 * u, w * 0.97f, 48 * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(rep.left + 1.5f * u, rep.bottom), Size(rep.width, 4 * u))
        drawRect(luz(CALIZA_LUZ, oscuro, false, 0.5f), rep.topLeft, Size(rep.width, 2 * u))
        drawRect(luz(CALIZA, oscuro, false, 0.5f), Offset(rep.left, rep.top + 2 * u), Size(rep.width, rep.height - 2 * u))
        grietaLuz(Offset(w * 0.66f, 5 * u), Offset(w * 0.69f, rep.top - 4 * u), 1f * u, if (oscuro) 0.9f else 0.45f, 9)
        lamparaRoca(Offset(w * 0.12f, rep.top - 28 * u * 0.17f), 28 * u, oscuro, false, oscuro)
        val losa = Path().apply { moveTo(w * 0.8f, rep.top); lineTo(w * 0.83f, 8 * u); lineTo(w * 0.93f, 6 * u); lineTo(w * 0.95f, rep.top); close() }
        translate(2 * u, 0f) { drawPath(losa, Color.Black.copy(alpha = 0.3f)) }
        clipPath(losa) { piedra(Rect(w * 0.79f, 5 * u, w * 0.96f, rep.top), c(CALIZA_LUZ), 153) }
        drawPath(losa, Color.Black.copy(alpha = 0.3f), style = Stroke(0.7f * u))
        val et = Rect(w * 0.28f, 12 * u, w * 0.58f, 34 * u)
        cordel(Offset(et.left + 6 * u, et.top + 2 * u), Offset(et.left + 10 * u, 2 * u), c(CORDEL), 0.9f * u)
        cordel(Offset(et.right - 6 * u, et.top + 2 * u), Offset(et.right - 10 * u, 2 * u), c(CORDEL), 0.9f * u)
        hoja(et, papel(oscuro, false, 0.5f), 157, 0.25f, 0.02f)
        textoEn(medidor, "Cartas escondidas", Rect(et.left + 6 * u, et.top, et.right - 6 * u, et.bottom), TITULO, tinta(oscuro), 10.5f, peso = FontWeight.Bold, minimo = 6f)
        // Los tres nichos.
        val baseY = h - 12 * u
        val grupos = listOf(Triple("Para abrir", e.listas, 0), Triple("Atadas", e.selladas, 1), Triple("Leídas", e.abiertas, 2))
        val anchoB = (w - 32 * u) / 3f
        grupos.forEach { (nombre, cuantas, tipo) ->
            val bx = 12 * u + tipo * (anchoB + 4 * u)
            val ti = Rect(bx + anchoB * 0.1f, 54 * u, bx + anchoB * 0.9f, 71 * u)
            hoja(ti, papel(oscuro, false, 0.35f), 163 + tipo, 0.25f, 0.025f)
            textoEn(medidor, "$nombre · $cuantas", Rect(ti.left + 4 * u, ti.top, ti.right - 4 * u, ti.bottom), TITULO, tinta(oscuro), 9.5f, peso = FontWeight.Bold, minimo = 5f)
            val ni = Rect(bx + 4 * u, 78 * u, bx + anchoB - 4 * u, baseY)
            translate(0.7f * u, 0.7f * u) { drawPath(nicho(ni.inflate(3.5f * u)), Color.White.copy(alpha = if (oscuro) 0.06f else 0.25f), style = Stroke(0.6f * u)) }
            drawPath(nicho(ni.inflate(3.5f * u)), c(CALIZA_SOMBRA), style = Stroke(0.7f * u))
            clipPath(nicho(ni)) {
                piedra(ni.inflate(2 * u), c(HUECO), 167 + tipo, 0.7f)
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent), ni.top, ni.top + 24 * u), ni.topLeft, Size(ni.width, 24 * u))
                if (tipo == 0 && cuantas > 0) mancha(Offset(ni.center.x, ni.bottom - 10 * u), ni.width * 0.7f, GRIETA_HALO.copy(alpha = 0.22f))
                val vis = cuantas.coerceAtMost(7)
                for (j in 0 until vis) {
                    val gy = ni.bottom - 7 * u - j * 6.5f * u
                    val gx = ni.left + 5 * u + (azar(tipo * 13 + 5, j) - 0.5f) * 4 * u
                    val gw = ni.width - 10 * u
                    drawRect(Color.Black.copy(alpha = 0.3f), Offset(gx + 1 * u, gy + 1.5f * u), Size(gw, 5 * u))
                    drawRect(luz(mezcla(Color(0xFFE6D8B4), Color(0xFFCDB98C), 0.3f * (j % 3)), oscuro, false, 0.3f), Offset(gx, gy), Size(gw, 5 * u))
                    drawLine(Color.Black.copy(alpha = 0.12f), Offset(gx + gw / 2, gy), Offset(gx + gw / 2, gy + 5 * u), 0.6f * u)
                    when (tipo) {
                        0 -> {
                            drawLine(GRIETA.copy(alpha = 0.7f), Offset(gx + 2 * u, gy + 0.8f * u), Offset(gx + gw - 2 * u, gy + 0.8f * u), 0.8f * u)
                            bullaArcontes(Offset(gx + gw * 0.5f, gy + 2.5f * u), 2.8f * u, luz(BARRO_CLARO, oscuro, false, 0.4f), 1f, j)
                        }
                        1 -> {
                            drawLine(c(Color(0xFF3B3026)), Offset(gx - 0.5f * u, gy + 2.5f * u), Offset(gx + gw + 0.5f * u, gy + 2.5f * u), 1f * u)
                            bullaArcontes(Offset(gx + gw * 0.5f, gy + 2.5f * u), 2.6f * u, c(BARRO), 1f, j + 7)
                        }
                        else -> {
                            drawRect(papel(oscuro, false, 0.3f), Offset(gx + gw * (0.12f + 0.1f * azar(31, j)), gy - 3.5f * u), Size(gw * 0.55f, 4 * u))
                            drawLine(tinta(oscuro).copy(alpha = 0.3f), Offset(gx + gw * 0.18f, gy - 2 * u), Offset(gx + gw * 0.5f, gy - 2 * u), 0.6f * u)
                        }
                    }
                }
                if (cuantas > 7) textoEn(medidor, "+${cuantas - 7}", Rect(ni.left, ni.top + ni.width * 0.32f, ni.right, ni.top + ni.width * 0.32f + 16 * u), TITULO,
                    if (oscuro) LLAMA else c(CALIZA_LUZ), 12f, peso = FontWeight.Bold)
                if (cuantas == 0) ouro(Offset(ni.center.x, ni.center.y + 6 * u), minOf(ni.width, ni.height) * 0.16f, 2.2f * u, c(CALIZA_SOMBRA).copy(alpha = 0.6f))
            }
            drawPath(nicho(ni), Color.Black.copy(alpha = 0.3f), style = Stroke(0.8f * u))
        }
        // El umbral de piedra.
        drawRect(Brush.verticalGradient(listOf(c(CALIZA_LUZ), c(CALIZA)), baseY, h), Offset(0f, baseY), Size(w, h - baseY))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.25f), Offset(0f, baseY), Size(w, 1 * u))
        if (oscuro) resplandor(Offset(w * 0.12f + 28 * u * 0.46f, rep.top - 15 * u), w * 0.55f)
    }

    // =================================================================
    //  Lo de alrededor: las pestañas, el buscador, las obras y los recuerdos
    // =================================================================

    override val altoPestanas: Dp = 76.dp

    /**
     * El emblema de cada biblioteca, como trazo dentro de [r]: 0, el diario (el códice con su solapa en punta y el lazo); 1, los
     * momentos (la jarra con su tapa); 2, los sueños (la luna dentro de las esferas); 3, el ideario (la chispa de luz); 4, las
     * cartas (el paquete doblado, atado y con su bula).
     */
    private fun iconoBiblioteca(k: Int, r: Rect): Path = Path().apply {
        val w = r.width; val h = r.height; val x = r.left; val y = r.top
        val c = r.center
        when (k) {
            0 -> {
                addRect(Rect(x + w * 0.1f, y + h * 0.1f, x + w * 0.82f, y + h * 0.9f))
                moveTo(x + w * 0.82f, y + h * 0.1f); lineTo(x + w * 0.56f, y + h * 0.5f); lineTo(x + w * 0.82f, y + h * 0.9f)
                moveTo(x + w * 0.56f, y + h * 0.5f); lineTo(x - w * 0.02f, y + h * 0.5f)
            }
            1 -> {
                moveTo(x + w * 0.42f, y + h * 0.98f)
                cubicTo(x + w * 0.14f, y + h * 0.86f, x + w * 0.16f, y + h * 0.38f, x + w * 0.38f, y + h * 0.28f)
                lineTo(x + w * 0.62f, y + h * 0.28f)
                cubicTo(x + w * 0.84f, y + h * 0.38f, x + w * 0.86f, y + h * 0.86f, x + w * 0.58f, y + h * 0.98f)
                close()
                addArc(Rect(x + w * 0.32f, y + h * 0.06f, x + w * 0.68f, y + h * 0.42f), 180f, 180f)
                moveTo(x + w * 0.3f, y + h * 0.24f); lineTo(x + w * 0.7f, y + h * 0.24f)
            }
            2 -> {
                addOval(Rect(c.x - w * 0.5f, c.y - w * 0.5f, c.x + w * 0.5f, c.y + w * 0.5f))
                addOval(Rect(c.x - w * 0.34f, c.y - w * 0.34f, c.x + w * 0.34f, c.y + w * 0.34f))
                addArc(Rect(c.x - w * 0.18f, c.y - w * 0.18f, c.x + w * 0.18f, c.y + w * 0.18f), 70f, 220f)
                addArc(Rect(c.x - w * 0.11f, c.y - w * 0.16f, c.x + w * 0.17f, c.y + w * 0.16f), 100f, 160f)
            }
            3 -> {
                moveTo(c.x, y); quadraticTo(c.x + w * 0.06f, c.y - h * 0.06f, x + w * 0.85f, c.y); quadraticTo(c.x + w * 0.06f, c.y + h * 0.06f, c.x, y + h)
                quadraticTo(c.x - w * 0.06f, c.y + h * 0.06f, x + w * 0.15f, c.y); quadraticTo(c.x - w * 0.06f, c.y - h * 0.06f, c.x, y); close()
            }
            else -> {
                addRect(Rect(x + w * 0.04f, y + h * 0.22f, x + w * 0.96f, y + h * 0.82f))
                moveTo(x + w * 0.04f, y + h * 0.46f); lineTo(x + w * 0.96f, y + h * 0.46f)
                addOval(Rect(c.x - w * 0.13f, y + h * 0.46f - w * 0.13f, c.x + w * 0.13f, y + h * 0.46f + w * 0.13f))
            }
        }
    }

    /**
     * Las pestañas son cinco nichos tallados en una franja de caliza: en cada uno, el emblema, el nombre y la cuenta. El elegido
     * se alumbra por dentro con la luz de la lámpara, que dora su emblema.
     */
    override fun DrawScope.pestanas(e: EstadoPestanas, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val banda = Rect(0f, 1 * u, w, h - 1 * u)
        val forma = Path().apply { addRoundRect(RoundRect(banda, CornerRadius(5 * u))) }
        clipPath(forma) { piedra(banda, c(CALIZA), 171) }
        drawPath(forma, Color.Black.copy(alpha = 0.25f), style = Stroke(0.8f * u))
        val claroTxt = Color(0xFFF1E2C2)
        for (k in 0 until 5) {
            val q = quinta(k, w, 0f, h)
            val ni = Rect(q.left + 5 * u, 6 * u, q.right - 5 * u, h - 7 * u)
            val sel = k == e.actual
            clipPath(nicho(ni)) {
                piedra(ni.inflate(2 * u), if (sel) luz(mezcla(HUECO, CALIZA, 0.62f), oscuro, false, 0.7f) else c(mezcla(HUECO, CALIZA, 0.45f)), 181 + k, 0.6f)
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), ni.top, ni.top + 18 * u), ni.topLeft, Size(ni.width, 18 * u))
                if (sel) mancha(Offset(ni.center.x, ni.bottom - 4 * u), ni.width * 0.95f, LLAMA_HALO.copy(alpha = if (oscuro) 0.45f else 0.5f))
            }
            drawPath(nicho(ni), Color.Black.copy(alpha = if (sel) 0.4f else 0.25f), style = Stroke(if (sel) 1.1f * u else 0.8f * u))
            val texto = if (oscuro) (if (sel) claroTxt else claroTxt.copy(alpha = 0.72f)) else (if (sel) tinta(oscuro) else tinta(oscuro).copy(alpha = 0.72f))
            val icono = if (sel) (if (oscuro) LLAMA else ocre(oscuro)) else texto
            val ic = Rect(ni.center.x - 8 * u, ni.top + 9 * u, ni.center.x + 8 * u, ni.top + 25 * u)
            drawPath(iconoBiblioteca(k, ic), icono, style = Stroke(1.2f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
            textoEn(medidor, e.nombres.getOrElse(k) { "" }, Rect(ni.left + 2 * u, ni.top + 27 * u, ni.right - 2 * u, ni.top + 40 * u), TITULO, texto, 9f,
                peso = if (sel) FontWeight.Bold else FontWeight.Normal, minimo = 5f)
            textoEn(medidor, "${e.cuentas.getOrElse(k) { 0 }}", Rect(ni.left + 2 * u, ni.top + 41 * u, ni.right - 2 * u, ni.bottom - 2 * u), TITULO, if (sel) icono else texto, 11f,
                peso = FontWeight.Bold, minimo = 6f)
        }
    }

    // ---------------------------------------------------------------- el buscador

    override val altoCampo: Dp = 52.dp
    override val cajaCampo = Rect(0.17f, 0.24f, 0.83f, 0.76f)
    override val letraCampo: FontFamily = TEXTO
    override fun tintaCampo(oscuro: Boolean): Color = tinta(oscuro)

    /**
     * El buscador es una tira de papiro puesta sobre una piedra: a la izquierda, la lámpara de aceite, que se enciende con el
     * foco; a la derecha, el ouroboros, que se dora y deja ver en su centro la chispa.
     */
    override fun DrawScope.campo(e: EstadoCampo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val forma = Path().apply { addRoundRect(RoundRect(Rect(0f, 0f, w, h), CornerRadius(8 * u))) }
        clipPath(forma) { piedra(Rect(0f, 0f, w, h), c(CALIZA), 191) }
        drawPath(forma, Color.Black.copy(alpha = 0.25f), style = Stroke(0.8f * u))
        val hj = Rect(w * 0.14f, h * 0.13f, w * 0.86f, h * 0.87f)
        hoja(hj, papel(oscuro, false, if (e.enfocado) 0.55f else 0.4f), 193, 0.28f, 0.02f)
        if (e.enfocado) drawRect(LLAMA_HALO.copy(alpha = 0.1f), hj.topLeft, hj.size)
        if (e.enfocado && oscuro) clipPath(Path().apply { addRoundRect(RoundRect(Rect(0f, 0f, w, h), CornerRadius(8 * u))) }) { resplandor(Offset(w * 0.1f, h * 0.3f), w * 0.3f, 0.8f) }
        val lw = minOf(h * 0.75f, w * 0.1f)
        lamparaRoca(Offset(w * 0.065f, h * 0.68f), lw, oscuro, false, e.enfocado)
        val oc = Offset(w * 0.93f, h / 2)
        val orr = minOf(h * 0.26f, w * 0.045f)
        ouro(oc, orr, orr * 0.32f, if (e.enfocado) c(ocre(oscuro)) else c(CALIZA_SOMBRA), escamas = c(CALIZA_LUZ))
        if (e.enfocado) { mancha(oc, orr * 1.3f, GRIETA_HALO.copy(alpha = 0.45f)); drawCircle(GRIETA, orr * 0.2f, oc) }
        if (e.vacio) aIzquierda(medidor, e.pista, Rect(w * cajaCampo.left, h * cajaCampo.top, w * cajaCampo.right, h * cajaCampo.bottom), TEXTO, tinta(oscuro).copy(alpha = if (oscuro) 0.6f else 0.45f), 14f, minimo = 9f)
    }

    // ---------------------------------------------------------------- las obras

    /**
     * Las obras se apoyan en un nicho ancho y bajo de la roca, de arco rebajado, sobre una tabla de olivo: la lámpara en la
     * punta de la tabla, la grieta de luz en la caliza y, de día, el sol que entra en diagonal con su polvo.
     */
    override fun DrawScope.repisaObras(medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        piedra(Rect(0f, 0f, w, h), c(CALIZA), 201)
        val y0 = h - 24 * u
        val ni = Rect(10 * u, 8 * u, w - 10 * u, y0)
        val elipse = Rect(ni.left, ni.top, ni.right, ni.top + ni.height * 0.7f)
        val boca = Path().apply { moveTo(ni.left, ni.bottom); lineTo(ni.left, ni.top + ni.height * 0.35f); arcTo(elipse, 180f, 180f, false); lineTo(ni.right, ni.bottom); close() }
        clipPath(boca) {
            piedra(ni, c(HUECO), 203, 0.7f)
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.35f), Color.Transparent), ni.top, ni.top + ni.height * 0.5f), ni.topLeft, ni.size)
        }
        translate(0.7f * u, 0.7f * u) { drawPath(boca, Color.White.copy(alpha = if (oscuro) 0.06f else 0.25f), style = Stroke(0.7f * u)) }
        drawPath(boca, Color.Black.copy(alpha = 0.3f), style = Stroke(0.8f * u))
        grietaLuz(Offset(w * 0.86f, ni.top + ni.height * 0.25f), Offset(w * 0.9f, ni.top + ni.height * 0.62f), 1f * u, if (oscuro) 0.9f else 0.4f, 17)
        val tabla = Rect(0f, y0, w, h - 9 * u)
        fun l(x: Color) = luz(x, oscuro, false, 0.35f)
        drawRect(Brush.verticalGradient(listOf(l(OLIVO_LUZ), l(OLIVO), l(mezcla(OLIVO, OLIVO_VETA, 0.45f))), tabla.top, tabla.bottom), tabla.topLeft, tabla.size)
        vetas(tabla, l(OLIVO_VETA).copy(alpha = 0.5f), 207, densidad = 1.3f, grosor = 0.9f, ondulacion = 1.8f)
        drawRect(Color.White.copy(alpha = if (oscuro) 0.08f else 0.26f), tabla.topLeft, Size(w, 1 * u))
        drawRect(Color.Black.copy(alpha = 0.34f), Offset(0f, tabla.bottom), Size(w, 3 * u))
        piedra(Rect(0f, tabla.bottom + 3 * u, w, h), c(CALIZA_LUZ), 209)
        lamparaRoca(Offset(ni.left + 22 * u, y0 - 30 * u * 0.17f), 30 * u, oscuro, false, oscuro)
        if (oscuro) resplandor(Offset(ni.left + 22 * u + 30 * u * 0.46f, y0 - 15 * u), w * 0.5f)
        else rayo(Offset(w * 0.56f, 0f) to Offset(w * 0.74f, 0f), Offset(w * 0.22f, y0) to Offset(w * 0.6f, y0), 0.7f, 205)
    }

    /** Un título partido por palabras en a lo más tres renglones que caben en [r]. */
    private fun DrawScope.tituloEn(medidor: TextMeasurer, titulo: String, r: Rect, letra: FontFamily, color: Color, tam: Float, peso: FontWeight) {
        val palabras = titulo.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        if (palabras.isEmpty()) return
        val renglones = mutableListOf<String>()
        var actual = ""
        palabras.forEach { p ->
            val prueba = if (actual.isEmpty()) p else "$actual $p"
            if (actual.isNotEmpty() && anchoTx(medidor, prueba, letra, tam, peso = peso) > r.width && renglones.size < 2) { renglones.add(actual); actual = p }
            else { actual = prueba }
        }
        renglones.add(actual)
        val alto = r.height / 3f
        var y = r.center.y - alto * renglones.size / 2f
        renglones.forEach { s ->
            textoEn(medidor, s, Rect(r.left, y, r.right, y + alto), letra, color, tam, peso = peso, minimo = tam * 0.5f)
            y += alto
        }
    }

    /** Cuál de [n] variantes le toca a una obra según su [semilla], que puede venir negativa. */
    private fun variante(semilla: Int, n: Int): Int = Math.floorMod(semilla, n.coerceAtLeast(1))

    /**
     * La portada de una obra es la tapa de un códice: el cuero con su filete doble y la red de diagonales grabados, la solapa
     * que entra por la derecha y se cierra en punta, con su lazo y el pasador de hueso; arriba, pegada, la tira de papiro con
     * el tipo y el título; abajo, otra con el autor y el año, y el ouroboros grabado al pie. En miniatura, solo la inicial en
     * su tira.
     */
    override fun DrawScope.portadaObra(e: EstadoPortada, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = w / u
        fun c(x: Color) = luz(x, oscuro, false, 0.5f)
        val cuero = c(CUEROS[variante(e.semilla, CUEROS.size)])
        val tapa = Rect(0f, 0f, w, h)
        cuero(tapa, cuero, e.semilla, w * 0.03f)
        if (e.mini) {
            val r = Rect(w * 0.18f, h * 0.3f, w * 0.82f, h * 0.7f)
            hoja(r, papel(oscuro, false), e.semilla, 0.25f, 0.03f)
            textoEn(medidor, e.titulo.trim().take(1).uppercase().ifEmpty { "◌" }, r, TITULO, tinta(oscuro), s * 0.36f, peso = FontWeight.Bold, minimo = 5f)
            return
        }
        grabado(tapa.deflate(w * 0.05f), cuero, w * 0.16f)
        // La solapa, por la derecha, con su punta, el lazo y el pasador.
        val px = w * 0.8f; val py = h * 0.52f; val x1 = w * 0.9f
        val solapa = Path().apply { moveTo(w, 0f); lineTo(x1, 0f); lineTo(px, py); lineTo(x1, h); lineTo(w, h); close() }
        val correa = c(CORREA)
        listOf(py - w * 0.012f, py + w * 0.02f).forEach { y -> drawLine(correa, Offset(0f, y), Offset(px, y), maxOf(1.2f * u, w * 0.018f)) }
        translate(-w * 0.012f, 0f) { drawPath(solapa, Color.Black.copy(alpha = 0.3f)) }
        drawPath(solapa, mezcla(cuero, Color.White, 0.06f))
        drawPath(solapa, Color.Black.copy(alpha = 0.3f), style = Stroke(maxOf(0.5f * u, w * 0.006f)))
        drawCircle(correa, w * 0.022f, Offset(px + w * 0.006f, py))
        drawRoundRect(c(HUESO), Offset(px - w * 0.03f, py - h * 0.05f), Size(w * 0.025f, h * 0.1f), CornerRadius(w * 0.012f))
        // La tira con el tipo y el título.
        val ti = Rect(w * 0.1f, h * 0.08f, w * 0.74f, h * 0.45f)
        hoja(ti, papel(oscuro, false), e.semilla + 1, 0.3f, 0.015f)
        textoEn(medidor, e.tipo, Rect(ti.left + w * 0.04f, ti.top + h * 0.015f, ti.right - w * 0.04f, ti.top + h * 0.075f), TITULO, ocre(oscuro), s * 0.07f, espaciado = s * 0.01f, minimo = 3.5f)
        diples(ti.left + w * 0.08f, ti.right - w * 0.08f, ti.top + h * 0.09f, w * 0.03f, ocre(oscuro).copy(alpha = 0.7f), maxOf(0.5f * u, w * 0.006f), haciaCentro = true, hueco = w * 0.03f)
        tituloEn(medidor, e.titulo, Rect(ti.left + w * 0.04f, ti.top + h * 0.11f, ti.right - w * 0.04f, ti.bottom - h * 0.01f), TITULO, tinta(oscuro), s * 0.1f, FontWeight.Bold)
        // La tira con el autor y el año.
        val pie = listOf(e.autor, e.anio).filter { it.isNotBlank() }.joinToString(" · ")
        if (pie.isNotEmpty()) {
            val ta = Rect(w * 0.12f, h * 0.6f, w * 0.72f, h * 0.73f)
            hoja(ta, papel(oscuro, false), e.semilla + 2, 0.3f, 0.02f)
            textoEn(medidor, pie, ta.deflate(w * 0.03f), TEXTO, tinta(oscuro).copy(alpha = 0.85f), s * 0.065f, minimo = 3.5f)
        }
        ouro(Offset(w * 0.42f, h * 0.86f), w * 0.07f, w * 0.022f, mezcla(cuero, Color.Black, 0.45f), escamas = mezcla(cuero, Color.White, 0.2f))
    }

    // ---------------------------------------------------------------- los recuerdos

    override val margenRecuerdo: Dp = 14.dp
    override val altoRotuloRecuerdo: Dp = 46.dp
    override fun tintaRecuerdo(oscuro: Boolean): Color = tinta(oscuro)

    /** «Hace un año» o «Hace N años», como lo dice la hoja del recuerdo. */
    private fun haceAnios(n: Int): String = if (n == 1) "Hace un año" else "Hace $n años"

    /** El clima sin su signo (☀ ☂ ❄), que las letras del tema no traen. */
    private fun sinSigno(clima: String): String = clima.dropWhile { !it.isLetterOrDigit() }.trim()

    /**
     * El recuerdo es un fragmento de papiro: las fibras, unos agujeros que se comió el tiempo en la orilla derecha y, como marca
     * de agua, las siete esferas. En la cabecera, el ouroboros con los años en letras hebreas, «Hace N años» en ocre y la fecha
     * del tema con el clima; a la derecha, la cuenta del color del ánimo colgada de su cordel (de hueso, si no hay). Bajo la
     * cabecera, la fila de diplés.
     */
    override fun DrawScope.recuerdo(e: EstadoRecuerdo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val fondo = papel(oscuro, false, 0.3f)
        val tinta = tintaRecuerdo(oscuro)
        val acento = if (oscuro) LLAMA else ocre(oscuro)
        val cab = altoRotuloRecuerdo.toPx()
        val forma = Path().apply { addRoundRect(RoundRect(Rect(0f, 0f, w, h), CornerRadius(8 * u))) }
        drawPath(forma, fondo)
        clipPath(forma) {
            fibras(Rect(0f, 0f, w, h), (if (oscuro) PAPIRO else FIBRA).copy(alpha = if (oscuro) 0.07f else 0.17f), e.semilla, 2.4f * u)
            if (oscuro) mancha(Offset(0f, 0f), w * 0.6f, LLAMA_HALO.copy(alpha = 0.1f))
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.06f), Color.Transparent), 0f, cab), Offset.Zero, Size(w, cab))
            for (k in 0 until 3) {
                val o = Offset(w - (3f + 3f * azar(e.semilla, k)) * u, h * (0.3f + 0.24f * k))
                val sz = (3f + 3f * azar(e.semilla + 1, k)) * u
                drawPath(contornoIrregular(Rect(o.x - sz, o.y - sz * 0.7f, o.x + sz, o.y + sz * 0.7f), e.semilla + k, 10, 0.22f), Color.Black.copy(alpha = if (oscuro) 0.4f else 0.18f))
            }
            if (h > cab + 40 * u) for (i in 0 until 7) drawCircle(tinta.copy(alpha = 0.05f), (8f + 4f * i) * u, Offset(w - 40 * u, h - 26 * u), style = Stroke(0.6f * u))
        }
        drawPath(forma, Color.Black.copy(alpha = 0.2f), style = Stroke(1f * u))
        diples(10 * u, w - 10 * u, cab - 1 * u, 4 * u, acento.copy(alpha = 0.6f), 0.7f * u, haciaCentro = true, hueco = 6 * u)
        val oc = Offset(24 * u, cab / 2f)
        ouro(oc, 15 * u, 3.2f * u, tinta.copy(alpha = 0.8f), escamas = fondo)
        textoEn(medidor, Arcontes.numero(e.anios.coerceAtLeast(1)), Rect(oc.x - 10 * u, oc.y - 7 * u, oc.x + 10 * u, oc.y + 7 * u), TITULO, acento, 10f, peso = FontWeight.Bold, minimo = 5f)
        val col = Rect(46 * u, 5 * u, w - 30 * u, cab - 7 * u)
        aIzquierda(medidor, haceAnios(e.anios), Rect(col.left, col.top, col.right, col.top + col.height * 0.5f), TITULO, acento, 11f, peso = FontWeight.Bold, minimo = 6f)
        val clima = sinSigno(e.clima)
        aIzquierda(medidor, if (clima.isEmpty()) e.fecha else e.fecha + " · " + clima, Rect(col.left, col.top + col.height * 0.5f, col.right, col.bottom), TEXTO, tinta, 10.5f, minimo = 6.5f)
        cordel(Offset(w - 16 * u, 3 * u), Offset(w - 16 * u, cab / 2f - 4 * u), if (oscuro) mezcla(CORDEL, PENUMBRA, 0.3f) else CORDEL, 1f * u)
        cuenta(Offset(w - 16 * u, cab / 2f), 4.6f * u, e.animo, oscuro)
    }
}
