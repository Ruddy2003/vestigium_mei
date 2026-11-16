package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

// =====================================================================
//  Ciudad verde
// =====================================================================

/**
 * Ciudad verde: la estantería rescatada de un sótano de la ciudad caída, donde alguien vive y guarda lo que encuentra. Es de
 * ángulo ranurado, con la pintura verde militar saltada y el óxido en los cantos, y sus baldas son tablas rescatadas de anchos
 * distintos (una, de un cajón de fruta con sus letras de plantilla gastadas; otra, en dos tramos); la pata de la izquierda va
 * calzada con un ladrillo y una unión está atada con alambre. Detrás, un muro de hormigón con historia: las marcas del
 * encofrado y sus agujeros de los latiguillos, una grieta con dos varillas oxidadas al aire y sus chorreras, manchas de
 * humedad con musgo abajo y una flecha con un «-2» de plantilla medio borrados. La luz de día entra por un boquete del techo
 * que queda fuera del cuadro, arriba a la izquierda: una mancha de sol en el hormigón con la sombra de unas hojas que no se
 * ven. Encima de la estantería, a la izquierda, la radio de manivela a medio reparar (el destornillador, los tornillos, la tapa
 * de las pilas y dos cables sueltos), con su antena de alambre que sube a un clavo del muro, sobre una pila de libros atados
 * con cuerda; al centro, un tarro de semillas y una taza de esmalte desportillada con un brote; a la derecha, el farol de
 * queroseno. En el muro, un manojo de hierbas secándose en un clavo y un mapa dibujado a mano, sujeto con cinta. Por un poste
 * sube la hiedra y por el otro una enredadera con flores que sigue por una balda. Al pie, sobre las losas, una que levantan las
 * raíces con un helecho, setas junto al ladrillo, una regadera de chapa abollada y una caja de tubérculos cortada por el borde.
 * Cada tema es un recipiente rescatado sobre su balda, de una de tres clases según el tema: una lata de conserva con el
 * abrefácil levantado y las notas enrolladas asomando, un frasco de vidrio con tapa de tela atada (las notas se ven dentro) o
 * una caja de galletas abollada con la tapa entreabierta; en las tres, el tema va en una tira de cinta de enmascarar, asoman
 * más notas cuantas más guarda y en algunas trepa una ramita. Lo que no tiene tema va en un bote de pintura viejo, con las notas
 * sueltas y la cinta tachada; un hueco todavía sin tema es la balda con el cerco de óxido de una lata y una maceta hecha con
 * media lata con su brote, o una telaraña en el rincón. De noche, verde negro hondo: arde el farol (su halo en el hormigón,
 * brillos en el vidrio y el metal, los recipientes de cerca encendidos y los de abajo casi perdidos), unas luciérnagas junto a
 * la hiedra y, donde estaba el sol, una mancha fría de luna con sus hojas. Por dentro, el fondo de la lata con musgo y hojas
 * caídas. Las fichas son notas en papel rescatado, con manchas de agua y una hoja prensada.
 */
object IdeasRuinas : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta (la del cuadro del tema)
    /** El hormigón del muro: cálido a la luz, frío en sombra, y el de las losas. */
    private val HORMIGON_CALIDO = Color(0xFFBDB7A6)
    private val HORMIGON_FRIO = Color(0xFF9C9A8E)
    private val LOSA = Color(0xFF8E8A7C)
    /** El óxido: vivo en los cantos, hondo en las chorreras. */
    private val OXIDO = Color(0xFFA34A1E)
    private val OXIDO_OSC = Color(0xFF6A2E12)
    private val OXIDO_VIVO = Color(0xFFC0703A)
    /** La pintura verde militar de la estantería y el metal desnudo debajo. */
    private val VERDE_MILITAR = Color(0xFF5E6A4A)
    private val HOJALATA = Color(0xFFB8BCB8)
    private val HOJALATA_OSC = Color(0xFF7E8480)
    /** La madera rescatada: gris de intemperie, la cálida y la de pino del cajón de fruta. */
    private val MADERA = Color(0xFFA07E56)
    private val MADERA_GRIS = Color(0xFF8E8676)
    private val MADERA_OSC = Color(0xFF54473A)
    private val PINO = Color(0xFFC9AE7E)
    /** Los verdes: hiedra, su luz, musgo y helecho; la flor de la enredadera. */
    private val HIEDRA = Color(0xFF3E6B2A)
    private val HIEDRA_LUZ = Color(0xFF6A9A3E)
    private val MUSGO = Color(0xFF5A7A3A)
    private val HELECHO = Color(0xFF86A04E)
    private val FLOR = Color(0xFFF4D4DC)
    /** La tela roja gastada (la radio, la tapa del frasco), la paja y el agua del mapa y del esmalte. */
    private val TELA = Color(0xFFB8543A)
    private val PAJA = Color(0xFFDCC27C)
    private val AGUA = Color(0xFF4E8F86)
    private val LADRILLO = Color(0xFFA8664A)
    private val TIERRA = Color(0xFF6C4E38)
    private val HOLLIN = Color(0xFF38322E)
    /**
     * La cinta de enmascarar de los rótulos. De noche va de [CINTA_NOCHE] (lejos del farol) a [CINTA_FAROL] (junto a él): la
     * de lejos aún da 7.3:1 con [TINTA] en el título y 4.6:1 en la cuenta (al 78 %).
     */
    private val CINTA = Color(0xFFE8DCB0)
    private val CINTA_NOCHE = Color(0xFFBAB08E)
    private val CINTA_FAROL = Color(0xFFEAD6A4)
    private val TINTA = Color(0xFF1F2420)
    private val PAPEL = Color(0xFFE6E3D6)
    /** La noche del sótano: verde negro hondo. La llama del farol, la luz de las luciérnagas y la de la luna. */
    private val NOCHE = Color(0xFF0C1510)
    private val LLAMA = Color(0xFFFFC56E)
    private val LUCIERNAGA = Color(0xFFD8F07A)
    private val LUNA = Color(0xFFB4C4C0)

    /** El color de siempre del interior (no se toca). */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    // ---------------------------------------------------------------- la luz y la noche

    /** Dónde está el globo del farol, en fracciones del ancho. */
    private val FAROL = Offset(0.80f, 0.38f)

    /** Cuánto alumbra el farol un punto [p] dado en fracciones del ancho (0 a 1): cae con la distancia, despacio. */
    private fun luzFarol(p: Offset): Float = (1.15f * (1f - (p - FAROL).getDistance() / 1.8f)).coerceIn(0f, 1f)

    /** Un color de noche a [k] (0 a 1) del farol: hundido en el verde negro y calentado cerca de él. */
    private fun noche(c: Color, k: Float): Color {
        val q = k.coerceIn(0f, 1f)
        return mezcla(mezcla(c, NOCHE, 0.82f - 0.6f * q.pow(1.2f)), LLAMA, 0.26f * q)
    }

    private fun sombra(oscuro: Boolean, a: Float) = if (oscuro) NOCHE.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)
    private fun brillo(oscuro: Boolean, a: Float, k: Float = 0f) = if (oscuro) LLAMA.copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f)) else luzMueble(a)

    /**
     * Cómo se pinta algo según dónde está: de día, tal cual (con [dia], un poco más claro arriba a la izquierda); de noche, según
     * [k], lo que le llega del farol. De día la luz viene de la izquierda; de noche, del farol, a la derecha de casi todo.
     */
    private class Tono(val oscuro: Boolean, val k: Float, val dia: Float = 0f)

    private fun Tono.c(col: Color) = if (oscuro) noche(col, k) else if (dia > 0f) mezcla(col, LUZ_MUEBLE, dia) else col
    private fun Tono.s(a: Float) = sombra(oscuro, a)
    private fun Tono.b(a: Float, extra: Float = 0f) = brillo(oscuro, a, k + extra)
    private val Tono.luzIzq get() = !oscuro

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false
    override val proporcion = 1.3f
    override val corona = 0.6f
    override val base = 0.36f
    override val lado = 0.06f
    override val hueco = 0.045f

    /** La balda de arriba: su cara de arriba, su canto y el ángulo de debajo, en fracciones del ancho desde arriba. */
    private const val TAB = 0.505f
    /** El grosor de la cara de arriba de cada balda y el del ángulo de debajo del canto, en fracciones del ancho. */
    private const val CARA = 0.014f
    private const val ANGULO = 0.013f
    /** En la base, en fracción de su alto: la balda del pie, el rincón del muro con el suelo y donde pisan las patas. */
    private const val BALDA_PIE = 0.2f
    private const val RINCON = 0.3f
    private const val PIES = 0.42f
    /** Los postes: el ala de delante (de [0] a [1]) y el ala de lado, en sombra, hasta [2]; en fracciones del ancho. */
    private val POSTES = listOf(floatArrayOf(0.016f, 0.04f, 0.05f), floatArrayOf(0.483f, 0.507f, 0.517f), floatArrayOf(0.948f, 0.972f, 0.982f))

    // ---------------------------------------------------------------- ayudantes

    /** Revuelve la semilla de un tema (su `hashCode`, que en temas parecidos se parece) para repartir bien las tres clases. */
    private fun mezclar(s: Int): Int {
        var x = s * 0x9E3779B1L.toInt()
        x = x xor (x ushr 15)
        x *= 0x85EBCA6BL.toInt()
        return x xor (x ushr 13)
    }

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    /** Un punto de una forma dada en coordenadas propias ([x], [y]), girada [ang] grados y puesta en [c]. */
    private fun girado(c: Offset, x: Float, y: Float, ang: Float): Offset {
        val a = Math.toRadians(ang.toDouble())
        val ca = cos(a).toFloat(); val sa = sin(a).toFloat()
        return Offset(c.x + x * ca - y * sa, c.y + x * sa + y * ca)
    }

    /** Una hoja de hiedra de largo [s], con el pecíolo en [c] y la punta hacia [ang] grados (0, hacia arriba): tres lóbulos. */
    private fun hojaPath(c: Offset, s: Float, ang: Float, ancho: Float = 1f): Path {
        val pts = listOf(
            0f to 0f,
            -0.22f * ancho to 0.04f, -0.48f * ancho to -0.12f, -0.46f * ancho to -0.34f,
            -0.44f * ancho to -0.46f, -0.3f * ancho to -0.52f, -0.2f * ancho to -0.58f,
            -0.14f * ancho to -0.78f, -0.05f * ancho to -0.92f, 0f to -1f,
            0.05f * ancho to -0.92f, 0.14f * ancho to -0.78f, 0.2f * ancho to -0.58f,
            0.3f * ancho to -0.52f, 0.44f * ancho to -0.46f, 0.46f * ancho to -0.34f,
            0.48f * ancho to -0.12f, 0.22f * ancho to 0.04f, 0f to 0f,
        ).map { (x, y) -> girado(c, x * s, y * s, ang) }
        return Path().apply {
            moveTo(pts[0].x, pts[0].y)
            var i = 1
            while (i + 2 < pts.size) { cubicTo(pts[i].x, pts[i].y, pts[i + 1].x, pts[i + 1].y, pts[i + 2].x, pts[i + 2].y); i += 3 }
            close()
        }
    }

    /** Una hoja dibujada una a una: su sombra en lo de detrás, el color, la mitad en sombra y el nervio. */
    private fun DrawScope.hoja(c: Offset, s: Float, ang: Float, color: Color, t: Tono, sombraFuera: Boolean = true, ancho: Float = 1f) {
        val p = hojaPath(c, s, ang, ancho)
        if (sombraFuera) translate(s * 0.18f, s * 0.14f) { drawPath(p, t.s(0.22f)) }
        drawPath(p, t.c(color))
        // La mitad que no mira a la luz, un punto más honda; el nervio, claro.
        val punta = girado(c, 0f, -s, ang)
        val lado = girado(c, if (t.luzIzq) s * 0.45f else -s * 0.45f, -s * 0.4f, ang)
        drawPath(Path().apply { moveTo(c.x, c.y); lineTo(punta.x, punta.y); lineTo(lado.x, lado.y); close() }, t.s(0.16f))
        drawLine(t.c(mezcla(color, PAJA, 0.45f)).copy(alpha = 0.7f), c, Offset((c.x + punta.x) / 2 + (punta.x - c.x) * 0.3f, (c.y + punta.y) / 2 + (punta.y - c.y) * 0.3f), s * 0.05f)
    }

    /** Un tallo que pasa por [pts] con hojas alternas, cada [cada] puntos; los colores alternan entre hiedra y su luz. */
    private fun DrawScope.tallo(pts: List<Offset>, s: Float, semilla: Int, tono: (Offset) -> Tono, cada: Int = 1, grosor: Float = 1.3f, sombraFuera: Boolean = true) {
        val u = density
        val p = Path().apply {
            moveTo(pts[0].x, pts[0].y)
            for (i in 1 until pts.size) {
                val a = pts[i - 1]; val b = pts[i]
                cubicTo(a.x + (b.x - a.x) * 0.3f + (azar(semilla, i) - 0.5f) * s * 0.5f, a.y + (b.y - a.y) * 0.3f, a.x + (b.x - a.x) * 0.7f, a.y + (b.y - a.y) * 0.7f, b.x, b.y)
            }
        }
        val t0 = tono(pts[pts.size / 2])
        if (sombraFuera) translate(s * 0.15f, s * 0.12f) { drawPath(p, t0.s(0.2f), style = Stroke(grosor * u)) }
        drawPath(p, t0.c(mezcla(HIEDRA, TIERRA, 0.45f)), style = Stroke(grosor * u, cap = StrokeCap.Round))
        for (i in 1 until pts.size step cada) {
            val a = pts[i - 1]; val b = pts[i]
            val ang = Math.toDegrees(kotlin.math.atan2((b.x - a.x).toDouble(), -(b.y - a.y).toDouble())).toFloat()
            val ladoH = if (i % 2 == 0) 1f else -1f
            val tam = s * (0.75f + 0.5f * azar(semilla, i + 40))
            val col = if (azar(semilla, i + 60) > 0.55f) HIEDRA_LUZ else HIEDRA
            hoja(b, tam, ang + ladoH * (45f + 25f * azar(semilla, i + 80)), col, tono(b), sombraFuera)
        }
    }

    /** La sombra de algo en el muro, por detrás: de día, corrida a la derecha y abajo; de noche, larga y hacia fuera del farol. */
    private fun DrawScope.sombraEnMuro(sil: Path, centro: Offset, w: Float, oscuro: Boolean, hasta: Float, fuerza: Float = 1f) {
        clipRect(0f, 0f, w, hasta) {
            if (!oscuro) translate(w * 0.022f, w * 0.01f) { drawPath(sil, sombraMueble(0.2f * fuerza)) }
            else {
                val o = (centro - FAROL * w) * 0.28f
                withTransform({ translate(o.x, o.y); scale(1.18f, 1.18f, centro) }) { drawPath(sil, NOCHE.copy(alpha = 0.42f * fuerza)) }
            }
        }
    }

    /** Un clavo en el muro, con su cabeza y su sombra. */
    private fun DrawScope.clavo(c: Offset, t: Tono) {
        val u = density
        drawCircle(t.s(0.35f), 1.8f * u, c + Offset(1.4f * u, 1.2f * u))
        drawCircle(t.c(OXIDO_OSC), 1.8f * u, c)
        drawCircle(t.b(0.5f, 0.3f), 0.7f * u, c - Offset(0.6f * u, 0.6f * u))
    }

    // ---------------------------------------------------------------- el muro

    /** El muro de hormigón hasta [hasta]: el tono, las tablas del encofrado con sus juntas y latiguillos, pinceladas y humedad. */
    private fun DrawScope.muro(w: Float, hasta: Float, oscuro: Boolean) {
        val u = density
        val t0 = Tono(oscuro, 0f)
        drawRect(t0.c(HORMIGON_CALIDO), Offset.Zero, Size(w, hasta))
        // De día, el hormigón se enfría hacia abajo y hacia la derecha, lejos del boquete.
        if (!oscuro) {
            drawRect(Brush.linearGradient(listOf(Color.Transparent, HORMIGON_FRIO.copy(alpha = 0.55f), mezcla(HORMIGON_FRIO, SOMBRA_MUEBLE, 0.25f).copy(alpha = 0.7f)),
                Offset(w * 0.1f, 0f), Offset(w * 0.9f, hasta)), Offset.Zero, Size(w, hasta))
        }
        // Las tablas del encofrado: bandas de 0,12 del ancho con su veta impresa, la rebaba de cada junta y los latiguillos.
        val banda = w * 0.122f
        var y = w * 0.035f; var j = 0
        while (y < hasta) {
            val r = Rect(0f, y, w, minOf(y + banda, hasta))
            vetas(r, (if (oscuro) NOCHE else HORMIGON_FRIO).copy(alpha = if (oscuro) 0.35f else 0.28f), 700 + j, 0.35f, grosor = 0.7f, ondulacion = 0.5f)
            // Una tabla de cada banda era de otro tono (otra madera, otra tanda de hormigón).
            val x0 = w * azar(710, j) * 0.8f
            drawRect((if (oscuro) NOCHE else if (j % 2 == 0) HORMIGON_FRIO else LUZ_MUEBLE).copy(alpha = if (oscuro) 0.12f else 0.09f), Offset(x0, y), Size(w * (0.2f + 0.3f * azar(711, j)), r.height))
            drawLine(sombra(oscuro, 0.22f), Offset(0f, y), Offset(w, y + 1.5f * u), 1f * u)
            drawLine(brillo(oscuro, 0.22f, 0.15f), Offset(0f, y + 1.3f * u), Offset(w, y + 2.8f * u), 0.8f * u)
            // Las juntas verticales de las tablas, a tresbolillo.
            var x = w * (0.18f + 0.2f * azar(712, j))
            while (x < w) {
                drawLine(sombra(oscuro, 0.13f), Offset(x, y), Offset(x, y + r.height), 0.8f * u)
                x += w * (0.38f + 0.2f * azar(713, (x / 10f).toInt() + j))
            }
            // Los agujeros de los latiguillos, sobre la junta.
            var xl = w * (0.08f + 0.06f * (j % 2))
            while (xl < w) {
                val c = Offset(xl, y)
                drawCircle(sombra(oscuro, 0.45f), 2.6f * u, c)
                drawCircle(sombra(oscuro, 0.3f), 1.6f * u, c + Offset(0.6f * u, 0.6f * u))
                drawArc(brillo(oscuro, 0.4f, 0.2f), 0f, 120f, false, Offset(c.x - 2.6f * u, c.y - 2.6f * u), Size(5.2f * u, 5.2f * u), style = Stroke(0.8f * u))
                xl += w * 0.3f
            }
            y += banda; j++
        }
        pinceladas(Rect(0f, 0f, w, hasta), if (oscuro) NOCHE else HORMIGON_FRIO, 731, 70, u, if (oscuro) 0.08f else 0.1f)
        pinceladas(Rect(0f, 0f, w, hasta), if (oscuro) MUSGO else LUZ_MUEBLE, 732, 30, u, if (oscuro) 0.03f else 0.07f)
        // Manchas de humedad con musgo: suben del suelo y bajan de la grieta.
        for (i in 0 until 26) {
            val x = w * azar(741, i)
            val yy = hasta - w * (0.02f + 0.18f * azar(742, i) * azar(743, i))
            val rr = w * (0.02f + 0.05f * azar(744, i))
            drawOval(sombra(oscuro, 0.07f), Offset(x - rr, yy - rr * 0.6f), Size(rr * 2, rr * 1.2f))
            if (i % 2 == 0) drawOval((if (oscuro) noche(MUSGO, 0f) else MUSGO).copy(alpha = 0.22f), Offset(x - rr * 0.5f, yy - rr * 0.2f), Size(rr, rr * 0.5f))
        }
    }

    /** La mancha del boquete del techo en el muro: de día, el sol; de noche, la luna, muy tenue. */
    private fun manchaBoquete(w: Float): Path {
        val pts = listOf(-0.02f to -0.02f, 0.47f to -0.02f, 0.5f to 0.06f, 0.48f to 0.11f, 0.53f to 0.17f, 0.51f to 0.25f, 0.55f to 0.31f,
            0.44f to 0.33f, 0.36f to 0.37f, 0.25f to 0.35f, 0.16f to 0.39f, 0.07f to 0.37f, -0.02f to 0.4f)
        return Path().apply {
            pts.forEachIndexed { i, (x, y) -> if (i == 0) moveTo(x * w, y * w) else lineTo(x * w, y * w) }
            close()
        }
    }

    /** Las ramitas con hojas cuya sombra cae en la mancha del boquete (las plantas de arriba, que no se ven). */
    private fun DrawScope.hojasDelBoquete(w: Float, color: Color) {
        val ramas = listOf(
            listOf(Offset(0.0f, 0.06f), Offset(0.08f, 0.1f), Offset(0.16f, 0.12f), Offset(0.24f, 0.17f), Offset(0.3f, 0.24f)),
            listOf(Offset(0.36f, -0.02f), Offset(0.38f, 0.05f), Offset(0.43f, 0.1f), Offset(0.47f, 0.18f)),
            listOf(Offset(0.0f, 0.26f), Offset(0.06f, 0.27f), Offset(0.12f, 0.31f), Offset(0.17f, 0.36f)),
        )
        ramas.forEachIndexed { r, pts ->
            val p = Path().apply { moveTo(pts[0].x * w, pts[0].y * w); for (i in 1 until pts.size) lineTo(pts[i].x * w, pts[i].y * w) }
            drawPath(p, color, style = Stroke(w * 0.003f, cap = StrokeCap.Round))
            for (i in 1 until pts.size) {
                val a = pts[i - 1] * w; val b = pts[i] * w
                for (j in 0 until 3) {
                    val f = (j + 0.5f) / 3f
                    val c = a + (b - a) * f
                    val ang = Math.toDegrees(kotlin.math.atan2((b.x - a.x).toDouble(), -(b.y - a.y).toDouble())).toFloat()
                    drawPath(hojaPath(c, w * (0.026f + 0.016f * azar(751 + r, i * 3 + j)), ang + (if (j % 2 == 0) 55f else -55f), 0.8f), color)
                }
            }
        }
        // Y la masa de hojas del borde de arriba, por donde entra la luz.
        for (i in 0 until 14) {
            val c = Offset(w * (0.02f + 0.48f * azar(760, i)), w * (0.0f + 0.03f * azar(761, i)))
            drawPath(hojaPath(c, w * (0.022f + 0.012f * azar(762, i)), 150f + 60f * azar(763, i), 0.9f), color)
        }
    }

    /** La flecha y el «-2» de plantilla, medio borrados. */
    private fun DrawScope.plantilla(c: Offset, s: Float, oscuro: Boolean) {
        val u = density
        val pintura = (if (oscuro) noche(OXIDO_OSC, 0.15f) else mezcla(OXIDO_OSC, HOLLIN, 0.3f)).copy(alpha = 0.55f)
        val g = s * 0.13f
        // La flecha, hacia la izquierda, con los puentes de la plantilla.
        drawPath(poligono(c.x - s * 1.25f, c.y, c.x - s * 0.85f, c.y - s * 0.32f, c.x - s * 0.85f, c.y - s * 0.12f, c.x - s * 0.82f, c.y - s * 0.12f,
            c.x - s * 0.82f, c.y + s * 0.12f, c.x - s * 0.85f, c.y + s * 0.12f, c.x - s * 0.85f, c.y + s * 0.32f), pintura)
        drawRect(pintura, Offset(c.x - s * 0.76f, c.y - s * 0.1f), Size(s * 0.5f, s * 0.2f))
        // El guion y el dos.
        drawRect(pintura, Offset(c.x - s * 0.08f, c.y - g / 2), Size(s * 0.24f, g))
        val dos = Path().apply {
            moveTo(c.x + s * 0.3f, c.y - s * 0.22f)
            cubicTo(c.x + s * 0.32f, c.y - s * 0.52f, c.x + s * 0.72f, c.y - s * 0.52f, c.x + s * 0.72f, c.y - s * 0.24f)
            cubicTo(c.x + s * 0.72f, c.y - s * 0.04f, c.x + s * 0.5f, c.y + s * 0.1f, c.x + s * 0.32f, c.y + s * 0.36f)
            lineTo(c.x + s * 0.76f, c.y + s * 0.36f)
        }
        drawPath(dos, pintura, style = Stroke(g, cap = StrokeCap.Butt, join = StrokeJoin.Miter))
        // Los puentes de la plantilla, en el dos.
        drawRect(if (oscuro) noche(HORMIGON_CALIDO, 0.15f) else mezcla(HORMIGON_CALIDO, LUZ_MUEBLE, 0.2f), Offset(c.x + s * 0.48f, c.y - s * 0.55f), Size(s * 0.05f, s * 0.18f))
        // Lo que se llevó el tiempo: manchas de hormigón limpio encima, y una chorrera de pintura.
        for (i in 0 until 10) {
            val p = Offset(c.x + s * (-1.2f + 2f * azar(771, i)), c.y + s * (-0.45f + 0.9f * azar(772, i)))
            val q = s * (0.06f + 0.14f * azar(773, i))
            drawOval((if (oscuro) noche(HORMIGON_CALIDO, 0.1f) else mezcla(HORMIGON_CALIDO, HORMIGON_FRIO, 0.3f)).copy(alpha = 0.75f), Offset(p.x - q, p.y - q * 0.6f), Size(q * 2, q * 1.2f))
        }
        drawLine(pintura, Offset(c.x + s * 0.4f, c.y + s * 0.4f), Offset(c.x + s * 0.41f, c.y + s * 0.62f), 1.2f * u, StrokeCap.Round)
    }

    /** La grieta con su desconchón, las dos varillas oxidadas al aire, el musgo en la grieta y las chorreras de óxido. */
    private fun DrawScope.grieta(w: Float, oscuro: Boolean) {
        val u = density
        val t = Tono(oscuro, if (oscuro) luzFarol(Offset(0.63f, 0.14f)) * 0.8f else 0f)
        // Las chorreras de óxido que bajan desde las varillas.
        for (i in 0 until 7) {
            val x = w * (0.615f + 0.05f * azar(781, i))
            val y0 = w * (0.125f + 0.01f * azar(782, i))
            val largo = w * (0.08f + 0.22f * azar(783, i))
            val an = w * (0.004f + 0.006f * azar(784, i))
            drawPath(poligono(x - an, y0, x + an, y0, x + an * 0.3f + w * 0.004f * azar(785, i), y0 + largo, x - an * 0.2f, y0 + largo * 0.94f),
                t.c(if (i % 2 == 0) OXIDO else OXIDO_VIVO).copy(alpha = 0.18f + 0.18f * azar(786, i)))
        }
        // La grieta, de arriba abajo, con dos ramales.
        val pts = listOf(0.6f to 0f, 0.612f to 0.04f, 0.605f to 0.075f, 0.625f to 0.11f, 0.64f to 0.15f, 0.633f to 0.19f, 0.652f to 0.23f, 0.646f to 0.27f, 0.66f to 0.3f)
        val g = Path().apply { pts.forEachIndexed { i, (x, y) -> if (i == 0) moveTo(x * w, y * w) else lineTo(x * w, y * w) } }
        translate(1f * u, 1f * u) { drawPath(g, t.b(0.35f, 0.2f), style = Stroke(0.9f * u)) }
        drawPath(g, t.c(HOLLIN), style = Stroke(1.5f * u, join = StrokeJoin.Round))
        drawPath(Path().apply { moveTo(w * 0.633f, w * 0.19f); lineTo(w * 0.61f, w * 0.215f); lineTo(w * 0.6f, w * 0.25f) }, t.c(HOLLIN), style = Stroke(1f * u))
        drawPath(Path().apply { moveTo(w * 0.605f, w * 0.075f); lineTo(w * 0.585f, w * 0.085f); lineTo(w * 0.575f, w * 0.1f) }, t.c(HOLLIN), style = Stroke(0.9f * u))
        // El desconchón: el hormigón caído deja ver el árido, más oscuro y rugoso.
        val hueco = poligono(w * 0.6f, w * 0.1f, w * 0.627f, w * 0.088f, w * 0.66f, w * 0.1f, w * 0.668f, w * 0.128f, w * 0.65f, w * 0.15f, w * 0.618f, w * 0.146f, w * 0.598f, w * 0.128f)
        drawPath(hueco, t.c(mezcla(HORMIGON_FRIO, HOLLIN, 0.42f)))
        clipPath(hueco) {
            for (i in 0 until 10) drawCircle(t.c(mezcla(HORMIGON_FRIO, HOLLIN, if (i % 2 == 0) 0.15f else 0.3f)), w * (0.002f + 0.002f * azar(791, i)),
                Offset(w * (0.6f + 0.068f * azar(792, i)), w * (0.088f + 0.062f * azar(793, i))))
            drawRect(t.s(0.35f), Offset(w * 0.595f, w * 0.085f), Size(w * 0.08f, w * 0.012f))
        }
        drawPath(hueco, t.b(0.3f, 0.2f), style = Stroke(0.8f * u))
        // Las dos varillas, con sus resaltes, una doblada hacia fuera.
        listOf(Triple(Offset(0.617f, 0.12f), Offset(0.598f, 0.06f), 0f), Triple(Offset(0.647f, 0.125f), Offset(0.69f, 0.085f), 1f)).forEach { (a0, b0, dob) ->
            val a = a0 * w; val b = b0 * w
            drawLine(t.s(0.3f), a + Offset(w * 0.008f, w * 0.006f), b + Offset(w * 0.014f, w * 0.008f), 2.4f * u, StrokeCap.Round)
            drawLine(t.c(OXIDO_OSC), a, b, 2.6f * u, StrokeCap.Round)
            drawLine(t.c(OXIDO_VIVO), a - Offset(0.5f * u, 0.5f * u), b - Offset(0.5f * u, 0.5f * u), 0.9f * u, StrokeCap.Round)
            for (k in 1 until 6) {
                val p = a + (b - a) * (k / 6f)
                drawLine(t.c(OXIDO_OSC), p - Offset(1.4f * u, -1f * u), p + Offset(1.4f * u, -1f * u), 0.9f * u)
            }
            if (dob > 0f) drawLine(t.c(OXIDO_OSC), b, b + Offset(w * 0.012f, w * 0.014f), 2.4f * u, StrokeCap.Round)
        }
        // El musgo que se metió en la grieta, abajo.
        for (i in 0 until 9) {
            val f = 0.55f + 0.45f * azar(795, i)
            val p = Offset(w * (0.625f + 0.035f * f), w * (0.15f + 0.15f * f))
            drawCircle(t.c(if (i % 3 == 0) HELECHO else MUSGO), w * (0.003f + 0.003f * azar(796, i)), p + Offset((azar(797, i) - 0.5f) * w * 0.01f, 0f))
        }
    }

    // ---------------------------------------------------------------- lo colgado del muro

    /** El mapa dibujado a mano: una hoja sujeta con cinta, con curvas de nivel, un río, un camino, casas y unas cruces. */
    private fun DrawScope.mapa(w: Float, oscuro: Boolean) {
        val u = density
        val c = Offset(w * 0.79f, w * 0.13f)
        val t = Tono(oscuro, if (oscuro) luzFarol(c / w) else 0f)
        val r = Rect(c.x - w * 0.1f, c.y - w * 0.075f, c.x + w * 0.1f, c.y + w * 0.075f)
        val hoja = poligono(r.left, r.top, r.right, r.top + w * 0.002f, r.right - w * 0.002f, r.bottom - w * 0.03f, r.right - w * 0.03f, r.bottom, r.left + w * 0.002f, r.bottom - w * 0.003f)
        rotate(-3f, c) {
            sombraEnMuro(hoja, c, w, oscuro, w * TAB, 0.9f)
            drawPath(hoja, t.c(mezcla(PAPEL, PAJA, 0.25f)))
            clipPath(hoja) {
                // Las curvas de nivel de un monte, el río, el camino de puntos, unas casas y las cruces.
                for (i in 0 until 3) {
                    val q = 1f - i * 0.28f
                    drawOval(t.c(MADERA).copy(alpha = 0.6f), Offset(r.left + w * (0.03f + 0.02f * i), r.top + w * (0.015f + 0.012f * i)), Size(w * 0.08f * q, w * 0.05f * q), style = Stroke(0.8f * u))
                }
                val rio = Path().apply {
                    moveTo(r.left, r.bottom - w * 0.03f)
                    cubicTo(r.left + w * 0.05f, r.bottom - w * 0.05f, r.left + w * 0.08f, r.bottom - w * 0.01f, r.left + w * 0.12f, r.top + w * 0.08f)
                    cubicTo(r.left + w * 0.15f, r.top + w * 0.05f, r.right - w * 0.02f, r.top + w * 0.06f, r.right, r.top + w * 0.03f)
                }
                drawPath(rio, t.c(AGUA), style = Stroke(1.8f * u, cap = StrokeCap.Round))
                for (j in 0 until 9) {
                    val x = r.left + w * (0.02f + 0.018f * j); val y = r.top + w * (0.12f - 0.006f * j)
                    drawLine(t.c(HOLLIN).copy(alpha = 0.7f), Offset(x, y), Offset(x + w * 0.009f, y - w * 0.002f), 0.9f * u)
                }
                for (j in 0 until 5) drawRect(t.c(HOLLIN).copy(alpha = 0.55f), Offset(r.left + w * (0.13f + 0.012f * j), r.top + w * (0.095f + 0.008f * (j % 2))), Size(w * 0.007f, w * 0.006f))
                listOf(Offset(0.055f, 0.03f), Offset(0.16f, 0.12f)).forEach { o ->
                    val p = Offset(r.left + w * o.x, r.top + w * o.y)
                    drawLine(t.c(TELA), p - Offset(w * 0.006f, w * 0.006f), p + Offset(w * 0.006f, w * 0.006f), 1.4f * u)
                    drawLine(t.c(TELA), p - Offset(w * 0.006f, -w * 0.006f), p + Offset(w * 0.006f, -w * 0.006f), 1.4f * u)
                }
                drawCircle(t.c(TELA), w * 0.012f, Offset(r.left + w * 0.16f, r.top + w * 0.12f), style = Stroke(0.9f * u))
                // Los pliegues de cuando iba doblado.
                drawLine(t.s(0.18f), Offset(c.x, r.top), Offset(c.x, r.bottom), 1f * u)
                drawLine(t.b(0.3f, 0.1f), Offset(c.x + 1f * u, r.top), Offset(c.x + 1f * u, r.bottom), 0.8f * u)
                drawLine(t.s(0.14f), Offset(r.left, c.y), Offset(r.right, c.y), 1f * u)
                drawRect(t.s(0.12f), Offset(c.x, r.top), Size(r.width / 2, r.height))
            }
            // La esquina de abajo que se levantó, con su revés.
            val esquina = poligono(r.right - w * 0.002f, r.bottom - w * 0.03f, r.right - w * 0.03f, r.bottom, r.right - w * 0.036f, r.bottom - w * 0.024f)
            translate(w * 0.004f, w * 0.004f) { drawPath(esquina, t.s(0.3f)) }
            drawPath(esquina, t.c(mezcla(PAPEL, HORMIGON_FRIO, 0.35f)))
            // La cinta en las dos esquinas de arriba.
            listOf(r.topLeft to -35f, Offset(r.right, r.top) to 32f).forEach { (p, a) ->
                rotate(a, p) {
                    drawRect(t.s(0.15f), Offset(p.x - w * 0.02f + 1f * u, p.y - w * 0.007f + 1f * u), Size(w * 0.04f, w * 0.014f))
                    drawRect(t.c(CINTA).copy(alpha = 0.92f), Offset(p.x - w * 0.02f, p.y - w * 0.007f), Size(w * 0.04f, w * 0.014f))
                }
            }
        }
    }

    /** El manojo de hierbas secándose cabeza abajo, colgado de un clavo por un cordel. */
    private fun DrawScope.hierbas(w: Float, oscuro: Boolean) {
        val u = density
        val t = Tono(oscuro, if (oscuro) luzFarol(Offset(0.12f, 0.15f)) else 0f)
        val clavoP = Offset(w * 0.115f, w * 0.07f)
        val atado = Offset(w * 0.118f, w * 0.105f)
        val tallos = (0 until 13).map { i ->
            val a = Math.toRadians((90.0 + (i - 6) * 5.5 + (azar(801, i) - 0.5) * 6))
            val l = w * (0.11f + 0.05f * azar(802, i))
            atado + Offset((cos(a) * l).toFloat(), (sin(a) * l).toFloat())
        }
        // La sombra del manojo en el muro (de día, al sol, nítida; de noche, apenas).
        val sx = if (oscuro) -w * 0.01f else w * 0.024f; val sy = if (oscuro) w * 0.012f else w * 0.014f
        tallos.forEach { p -> drawLine(t.s(if (oscuro) 0.18f else 0.09f), atado + Offset(sx, sy), p + Offset(sx * 1.4f, sy * 1.2f), 1.8f * u, StrokeCap.Round) }
        drawLine(t.s(0.15f), clavoP + Offset(sx, sy), atado + Offset(sx, sy), 1f * u)
        // El cordel, del clavo al atado.
        drawLine(t.c(PAJA), clavoP, atado - Offset(w * 0.004f, 0f), 1f * u)
        drawLine(t.c(PAJA), clavoP, atado + Offset(w * 0.004f, 0f), 1f * u)
        clavo(clavoP, t)
        // Los tallos con sus hojitas secas (unos más verdes, otros pajizos), y unas flores secas en las puntas.
        tallos.forEachIndexed { i, p ->
            val col = when (i % 3) { 0 -> mezcla(MUSGO, PAJA, 0.35f); 1 -> mezcla(HELECHO, PAJA, 0.5f); else -> mezcla(MUSGO, HORMIGON_FRIO, 0.3f) }
            drawLine(t.c(mezcla(col, TIERRA, 0.3f)), atado, p, 1f * u, StrokeCap.Round)
            for (j in 1..5) {
                val q = atado + (p - atado) * (j / 5.4f)
                val s = w * (0.006f + 0.004f * azar(803, i * 7 + j))
                rotate(if (j % 2 == 0) 35f else -35f, q) { drawOval(t.c(col), Offset(q.x - s * 0.35f, q.y - s), Size(s * 0.7f, s * 2f)) }
            }
            if (i % 4 == 1) drawCircle(t.c(mezcla(FLOR, TELA, 0.25f)), w * 0.004f, p)
        }
        // La luz en los tallos de delante y el cordel del atado.
        drawLine(t.b(0.35f, 0.2f), atado + Offset(-w * 0.004f, w * 0.01f), tallos[2], 1f * u)
        for (k in 0 until 4) drawLine(t.c(mezcla(PAJA, TIERRA, 0.2f)), atado + Offset(-w * 0.009f, w * (0.002f + 0.004f * k)), atado + Offset(w * 0.009f, w * (0.004f + 0.004f * k)), 1.1f * u)
    }

    // ---------------------------------------------------------------- la estantería

    /** Un tramo de ángulo ranurado vertical (el poste), de [y0] a [y1]: verde militar saltado, ranuras y óxido en los cantos. */
    private fun DrawScope.poste(i: Int, y0: Float, y1: Float, w: Float, oscuro: Boolean) {
        val u = density
        val (xa, xb, xc) = POSTES[i].let { Triple(it[0] * w, it[1] * w, it[2] * w) }
        val paso = w * 0.05f
        var y = y0
        // En tramos, para que de noche la luz del farol caiga poco a poco a lo largo del poste.
        while (y < y1) {
            val yb = minOf(y + paso, y1)
            val t = Tono(oscuro, if (oscuro) luzFarol(Offset(xb / w, (y + yb) / 2 / w)) else 0f)
            drawRect(t.c(VERDE_MILITAR), Offset(xa, y), Size(xb - xa, yb - y + 0.5f))
            drawRect(t.c(mezcla(VERDE_MILITAR, SOMBRA_MUEBLE, 0.45f)), Offset(xb, y), Size(xc - xb, yb - y + 0.5f))
            // Las ranuras: una larga y un agujero redondo, alternados.
            var ys = y + w * 0.008f
            var j = ((y - y0) / (w * 0.025f)).toInt()
            while (ys < yb - w * 0.012f) {
                val cx = (xa + xb) / 2
                if (j % 2 == 0) {
                    drawRoundRect(t.c(HOLLIN), Offset(cx - w * 0.0035f, ys), Size(w * 0.007f, w * 0.016f), CornerRadius(w * 0.0035f))
                    drawLine(t.b(0.3f, 0.2f), Offset(cx - w * 0.0035f, ys + w * 0.016f), Offset(cx + w * 0.0035f, ys + w * 0.016f), 0.8f * u)
                } else {
                    drawCircle(t.c(HOLLIN), w * 0.003f, Offset(cx, ys + w * 0.006f))
                }
                ys += w * 0.025f; j++
            }
            // La pintura saltada: el metal desnudo y el óxido.
            for (k in 0 until 3) {
                val s = (y / u).toInt() * 7 + i * 131 + k
                if (azar(s, 1) > 0.62f) {
                    val px = xa + (xb - xa) * azar(s, 2); val py = y + (yb - y) * azar(s, 3)
                    val q = w * (0.003f + 0.006f * azar(s, 4))
                    drawPath(poligono(px - q, py, px - q * 0.3f, py - q * 0.8f, px + q, py - q * 0.4f, px + q * 0.6f, py + q * 0.7f, px - q * 0.4f, py + q),
                        t.c(if (azar(s, 5) > 0.6f) mezcla(HOJALATA, VERDE_MILITAR, 0.45f) else OXIDO))
                }
            }
            y = yb
        }
        val tc = Tono(oscuro, if (oscuro) luzFarol(Offset(xb / w, (y0 + y1) / 2 / w)) else 0f)
        // Los cantos: luz en el de la izquierda, óxido en la arista y sombra en el ala de lado.
        drawRect(tc.b(0.35f, 0.3f), Offset(xa, y0), Size(1.2f * u, y1 - y0))
        drawLine(tc.c(OXIDO).copy(alpha = 0.55f), Offset(xb, y0), Offset(xb, y1), 1.2f * u)
        drawRect(tc.s(0.3f), Offset(xc - 1f * u, y0), Size(1f * u, y1 - y0))
        // De noche, el filo del poste que mira al farol.
        if (oscuro) {
            val xf = if (xb < FAROL.x * w) xb - 1f * u else xa
            drawLine(LLAMA.copy(alpha = 0.35f * tc.k), Offset(xf, y0), Offset(xf, minOf(y1, y0 + w * 0.6f)), 1f * u)
        }
        // El óxido que chorrea al pie.
        drawRect(tc.c(OXIDO_OSC).copy(alpha = 0.6f), Offset(xa, y1 - w * 0.03f), Size(xc - xa, w * 0.03f))
        drawRect(tc.c(OXIDO).copy(alpha = 0.55f), Offset(xa, y1 - w * 0.05f), Size((xb - xa) * 0.6f, w * 0.02f))
    }

    /**
     * Una franja horizontal de [color] de [x0] a [x1]: de día, lisa; de noche, en tramos cortos que se encienden hacia el farol
     * (sin degradados: Paparazzi pinta de negro los últimos cuando una imagen tiene muchos).
     */
    private fun DrawScope.tiras(x0: Float, x1: Float, y: Float, alto: Float, w: Float, oscuro: Boolean, color: Color, sube: Float = 0f) {
        if (!oscuro) { drawRect(color, Offset(x0, y), Size(x1 - x0, alto)); return }
        val n = 14
        val paso = (x1 - x0) / n
        for (i in 0 until n) {
            val x = x0 + paso * i
            drawRect(noche(color, luzFarol(Offset((x + paso / 2) / w, (y + alto / 2) / w)) + sube), Offset(x, y), Size(paso + 0.6f, alto))
        }
    }

    /** Un tornillo con su tuerca, sobre el ángulo. */
    private fun DrawScope.tornillo(c: Offset, w: Float, t: Tono) {
        val u = density
        drawCircle(t.s(0.4f), w * 0.0055f, c + Offset(0.8f * u, 0.8f * u))
        drawCircle(t.c(mezcla(HOJALATA_OSC, OXIDO, 0.4f)), w * 0.0055f, c)
        drawCircle(t.b(0.5f, 0.3f), w * 0.0022f, c - Offset(w * 0.0015f, w * 0.0015f))
    }

    /**
     * Una balda: la tabla rescatada (su cara de arriba que empieza en [y], su canto) y el ángulo ranurado de debajo, con sus
     * tornillos en los postes. [nivel] decide la tabla: cálida, gris, en dos tramos o la del cajón de fruta.
     */
    private fun DrawScope.balda(y: Float, nivel: Int, w: Float, oscuro: Boolean): Float {
        val u = density
        val canto = w * (0.02f + 0.008f * azar(811, nivel))
        val x0 = w * (0.006f + 0.02f * azar(812, nivel)); val x1 = w * (0.994f - 0.02f * azar(813, nivel))
        val madera = when (nivel % 4) { 0 -> MADERA_GRIS; 1 -> MADERA; 2 -> mezcla(MADERA_GRIS, MADERA, 0.5f); else -> PINO }
        val tramos = if (nivel % 4 == 2) listOf(x0 to w * 0.61f, w * 0.614f to x1) else listOf(x0 to x1)
        val yf = y + w * CARA
        val yc = yf + canto
        tramos.forEachIndexed { ti, (a, b) ->
            val s = nivel * 17 + ti
            // De noche, la tabla se enciende hacia el farol: de izquierda a derecha, con su color en cada punta.
            tiras(a, b, y, yf - y, w, oscuro, mezcla(madera, LUZ_MUEBLE, 0.22f), 0.08f)
            tiras(a, b, yf, canto, w, oscuro, madera)
            vetas(Rect(a, yf, b, yc), MADERA_OSC.copy(alpha = if (oscuro) 0.3f else 0.38f), 820 + s, 0.9f, grosor = 0.7f, ondulacion = 0.6f)
            vetas(Rect(a, y, b, yf), MADERA_OSC.copy(alpha = if (oscuro) 0.18f else 0.22f), 840 + s, 0.6f, grosor = 0.5f, ondulacion = 0.4f)
            val tk = Tono(oscuro, if (oscuro) luzFarol(Offset((a + b) / 2 / w, yf / w)) else 0f)
            drawRect(tk.b(0.5f, 0.3f), Offset(a, yf), Size(b - a, 1.1f * u))
            drawRect(tk.s(0.35f), Offset(a, yc - 1.4f * u), Size(b - a, 1.4f * u))
            // Las testas, con la veta cortada; una esquina mellada; unos agujeros de clavos viejos.
            drawRect(tk.c(mezcla(madera, MADERA_OSC, 0.4f)), Offset(a, yf), Size(w * 0.004f, canto))
            drawRect(tk.c(mezcla(madera, MADERA_OSC, 0.5f)), Offset(b - w * 0.004f, yf), Size(w * 0.004f, canto))
            if (azar(s, 3) > 0.4f) drawPath(poligono(b, yf, b - w * 0.018f, yf, b, yf + canto * 0.6f), sombra(oscuro, 0.5f))
            for (k in 0 until 3) {
                val p = Offset(a + (b - a) * azar(s, 10 + k), yf + canto * (0.3f + 0.4f * azar(s, 20 + k)))
                drawCircle(tk.c(HOLLIN), 1.1f * u, p)
                drawCircle(tk.c(OXIDO).copy(alpha = 0.4f), 2.4f * u, p + Offset(0f, 1.6f * u))
            }
        }
        // La tabla del cajón de fruta: unas letras de plantilla gastadas que no se leen y una franja.
        if (nivel % 4 == 3) {
            val tk = Tono(oscuro, if (oscuro) luzFarol(Offset(0.3f, yf / w)) * 0.8f else 0f)
            val tinta = tk.c(TELA).copy(alpha = 0.5f)
            val yl = yf + canto * 0.18f; val hl = canto * 0.64f
            listOf(0.2f, 0.245f, 0.29f, 0.36f).forEachIndexed { i, fx ->
                val x = w * fx
                when (i % 3) {
                    0 -> { drawRect(tinta, Offset(x, yl), Size(w * 0.008f, hl)); drawRect(tinta, Offset(x, yl), Size(w * 0.026f, hl * 0.26f)) }
                    1 -> { drawRect(tinta, Offset(x, yl), Size(w * 0.008f, hl)); drawRect(tinta, Offset(x + w * 0.018f, yl), Size(w * 0.008f, hl)); drawRect(tinta, Offset(x, yl + hl * 0.4f), Size(w * 0.026f, hl * 0.22f)) }
                    else -> { drawRect(tinta, Offset(x, yl), Size(w * 0.026f, hl * 0.24f)); drawRect(tinta, Offset(x + w * 0.009f, yl), Size(w * 0.008f, hl)) }
                }
            }
            drawRect(tk.c(AGUA).copy(alpha = 0.45f), Offset(w * 0.42f, yl + hl * 0.35f), Size(w * 0.2f, hl * 0.3f))
            // El desgaste encima: se llevó media letra.
            for (i in 0 until 7) drawOval(tk.c(PINO).copy(alpha = 0.7f), Offset(w * (0.19f + 0.2f * azar(851, i)), yl + hl * azar(852, i) - w * 0.004f), Size(w * (0.01f + 0.02f * azar(853, i)), w * 0.007f))
        }
        // El ángulo de debajo del canto, de poste a poste.
        val ya = yc; val yb = ya + w * ANGULO
        val ta = Tono(oscuro, 0f)
        tiras(w * POSTES[0][0], w * POSTES[2][2], ya, yb - ya, w, oscuro, VERDE_MILITAR)
        drawRect(ta.s(0.4f), Offset(w * POSTES[0][0], yb - 1.2f * u), Size(w * (POSTES[2][2] - POSTES[0][0]), 1.2f * u))
        drawRect(brillo(oscuro, 0.3f, if (oscuro) luzFarol(Offset(0.75f, ya / w)) else 0f), Offset(w * POSTES[0][0], ya), Size(w * (POSTES[2][2] - POSTES[0][0]), 0.8f * u))
        var xs = w * 0.07f; var j = nivel
        while (xs < w * 0.94f) {
            val tk = Tono(oscuro, if (oscuro) luzFarol(Offset(xs / w, ya / w)) else 0f)
            if (j % 2 == 0) drawRoundRect(tk.c(HOLLIN), Offset(xs, ya + (yb - ya) * 0.3f), Size(w * 0.016f, (yb - ya) * 0.4f), CornerRadius(w * 0.003f))
            else drawCircle(tk.c(HOLLIN), (yb - ya) * 0.2f, Offset(xs + w * 0.008f, (ya + yb) / 2))
            // La pintura saltada y el óxido de los cantos.
            if (azar(860 + nivel, j) > 0.55f) drawRect(tk.c(OXIDO).copy(alpha = 0.7f), Offset(xs + w * 0.02f, ya), Size(w * (0.01f + 0.03f * azar(861 + nivel, j)), (yb - ya) * 0.35f))
            if (azar(870 + nivel, j) > 0.7f) drawRect(tk.c(HOJALATA).copy(alpha = 0.7f), Offset(xs + w * 0.006f, yb - (yb - ya) * 0.4f), Size(w * 0.012f, (yb - ya) * 0.3f))
            xs += w * 0.034f; j++
        }
        return yb
    }

    // ---------------------------------------------------------------- lo de encima de la estantería

    /** Los libros rescatados, atados con una cuerda: tres, de tamaños distintos, con el lomo hacia fuera. */
    private fun DrawScope.libros(pie: Float, w: Float, oscuro: Boolean) {
        val u = density
        val t = Tono(oscuro, if (oscuro) luzFarol(Offset(0.1f, pie / w)) else 0f)
        val datos = listOf(Triple(Rect(w * 0.035f, pie - w * 0.024f, w * 0.17f, pie), AGUA, 0f), Triple(Rect(w * 0.045f, pie - w * 0.042f, w * 0.158f, pie - w * 0.024f), TELA, -1.5f),
            Triple(Rect(w * 0.04f, pie - w * 0.064f, w * 0.162f, pie - w * 0.042f), MADERA, 1.5f))
        drawOval(t.s(0.35f), Offset(w * 0.04f, pie - w * 0.006f), Size(w * 0.16f, w * 0.012f))
        datos.forEachIndexed { i, (r, col, giro) ->
            rotate(giro, r.center) {
                drawRoundRect(t.c(col), r.topLeft, r.size, CornerRadius(w * 0.003f))
                // El lomo: dos nervios, el título que ya no se lee, la tela gastada en las puntas y una mancha de agua.
                drawRect(t.s(0.2f), Offset(r.left, r.bottom - r.height * 0.25f), Size(r.width, r.height * 0.25f))
                drawRect(t.b(0.3f, 0.2f), Offset(r.left, r.top), Size(r.width, 1f * u))
                listOf(0.18f, 0.82f).forEach { fx -> drawLine(t.s(0.3f), Offset(r.left + r.width * fx, r.top), Offset(r.left + r.width * fx, r.bottom), 1f * u) }
                drawRect(t.c(mezcla(col, PAPEL, 0.5f)).copy(alpha = 0.6f), Offset(r.left + r.width * 0.32f, r.top + r.height * 0.35f), Size(r.width * 0.34f, r.height * 0.3f))
                drawOval(t.c(mezcla(col, HOLLIN, 0.3f)).copy(alpha = 0.4f), Offset(r.left + r.width * (0.5f + 0.2f * i), r.top), Size(r.width * 0.3f, r.height * 0.8f))
                drawRect(t.c(mezcla(col, PAPEL, 0.4f)), Offset(r.right - w * 0.006f, r.top), Size(w * 0.006f, r.height))
                drawRect(t.s(0.25f), Offset(r.right - w * 0.002f, r.top), Size(w * 0.002f, r.height))
            }
        }
        // La cuerda: rodea la pila y se anuda arriba con un lazo.
        val cuerda = t.c(PAJA)
        val cx = w * 0.098f
        drawLine(t.s(0.3f), Offset(cx + 1f * u, pie - w * 0.064f), Offset(cx + 1f * u, pie), 1.6f * u)
        drawLine(cuerda, Offset(cx, pie - w * 0.064f), Offset(cx, pie), 1.6f * u)
        drawOval(cuerda, Offset(cx - w * 0.012f, pie - w * 0.075f), Size(w * 0.013f, w * 0.009f), style = Stroke(1.3f * u))
        drawOval(cuerda, Offset(cx, pie - w * 0.074f), Size(w * 0.012f, w * 0.008f), style = Stroke(1.3f * u))
        drawLine(cuerda, Offset(cx, pie - w * 0.066f), Offset(cx + w * 0.016f, pie - w * 0.05f), 1.2f * u, StrokeCap.Round)
        drawCircle(t.c(mezcla(PAJA, TIERRA, 0.4f)), 1.8f * u, Offset(cx, pie - w * 0.066f))
    }

    /** La radio de manivela: cuerpo, rejilla, dial, perillas, asa, manivela y la tapa, en coordenadas del lienzo. */
    private fun cuerpoRadio(w: Float, pie: Float) = Rect(w * 0.15f, pie - w * 0.105f, w * 0.385f, pie - w * 0.002f)

    private fun siluetaRadio(w: Float, pie: Float): Path = Path().apply {
        val r = cuerpoRadio(w, pie)
        addRoundRect(androidx.compose.ui.geometry.RoundRect(r, CornerRadius(w * 0.014f)))
        addRect(Rect(r.left + w * 0.04f, r.top - w * 0.018f, r.right - w * 0.04f, r.top))
    }

    private fun DrawScope.radio(pie: Float, w: Float, oscuro: Boolean) {
        val u = density
        val r = cuerpoRadio(w, pie)
        val t = Tono(oscuro, if (oscuro) luzFarol(Offset(0.3f, pie / w - 0.05f)) else 0f)
        val esq = w * 0.014f
        // La sombra de contacto en la balda.
        drawOval(t.s(0.4f), Offset(r.left - w * 0.004f, pie - w * 0.007f), Size(r.width + w * 0.035f, w * 0.013f))
        // El asa de llevar, abatida hacia atrás, con cinta aislante.
        val asa = Path().apply {
            moveTo(r.left + w * 0.035f, r.top + w * 0.004f)
            cubicTo(r.left + w * 0.04f, r.top - w * 0.026f, r.right - w * 0.04f, r.top - w * 0.026f, r.right - w * 0.035f, r.top + w * 0.004f)
        }
        drawPath(asa, t.s(0.3f), style = Stroke(w * 0.009f))
        drawPath(asa, t.c(HOLLIN), style = Stroke(w * 0.007f, cap = StrokeCap.Round))
        translate(0f, -1.2f * u) { drawPath(asa, t.b(0.25f, 0.2f), style = Stroke(w * 0.002f)) }
        drawRect(t.c(mezcla(HOLLIN, AGUA, 0.5f)), Offset(r.center.x - w * 0.02f, r.top - w * 0.021f), Size(w * 0.03f, w * 0.009f))
        // El cuerpo de plástico rojo gastado: la cara de delante y, a la derecha, la de lado en sombra.
        drawRoundRect(t.c(TELA), r.topLeft, r.size, CornerRadius(esq))
        val lado = Rect(r.right - w * 0.03f, r.top, r.right, r.bottom)
        clipPath(Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(r, CornerRadius(esq))) }) {
            if (t.luzIzq) {
                drawRect(t.s(0.3f), lado.topLeft, lado.size)
                drawRect(luzMueble(0.25f), r.topLeft, Size(r.width, w * 0.006f))
                drawRect(luzMueble(0.15f), r.topLeft, Size(w * 0.006f, r.height))
            } else {
                drawRect(t.s(0.35f), r.topLeft, Size(w * 0.05f, r.height))
                drawRect(t.b(0.45f, 0.2f), Offset(r.right - w * 0.008f, r.top), Size(w * 0.008f, r.height))
                drawRect(t.b(0.4f, 0.2f), r.topLeft, Size(r.width, w * 0.004f))
            }
            drawRect(t.s(0.25f), Offset(r.left, r.bottom - w * 0.012f), Size(r.width, w * 0.012f))
            // La junta de las dos mitades de la caja, abierta a la derecha: por ahí salen los cables.
            drawLine(t.c(mezcla(TELA, HOLLIN, 0.6f)), Offset(r.left, r.top + r.height * 0.5f), Offset(r.right, r.top + r.height * 0.5f), 0.8f * u)
            // Rayones y la pintura comida.
            for (i in 0 until 8) {
                val p = Offset(r.left + r.width * azar(901, i), r.top + r.height * azar(902, i))
                drawLine(t.c(mezcla(TELA, PAPEL, 0.5f)).copy(alpha = 0.6f), p, p + Offset(w * 0.012f * azar(903, i), -w * 0.004f), 0.7f * u)
            }
        }
        // La rejilla del altavoz: un panel oscuro con sus agujeros.
        val rej = Rect(r.left + w * 0.014f, r.top + w * 0.016f, r.left + w * 0.105f, r.bottom - w * 0.016f)
        drawRoundRect(t.c(HOLLIN), rej.topLeft, rej.size, CornerRadius(w * 0.006f))
        drawRoundRect(t.b(0.25f, 0.1f), Offset(rej.left, rej.bottom - 1f * u), Size(rej.width, 1f * u), CornerRadius(0.5f * u))
        for (fy in 0 until 5) for (fx in 0 until 6) {
            val p = Offset(rej.left + rej.width * (fx + 0.5f) / 6f, rej.top + rej.height * (fy + 0.5f) / 5f)
            drawCircle(t.c(mezcla(HOLLIN, NOCHE, 0.6f)), w * 0.0035f, p)
            drawCircle(t.b(0.18f, 0.1f), w * 0.0012f, p + Offset(w * 0.0015f, w * 0.0018f))
        }
        // El dial: la ventanita con su escala y la aguja, y el cristal rayado.
        val dial = Rect(r.left + w * 0.118f, r.top + w * 0.016f, r.right - w * 0.04f, r.top + w * 0.05f)
        drawRect(t.s(0.35f), dial.topLeft, Size(dial.width, dial.height + 1.4f * u))
        drawRect(t.c(mezcla(PAPEL, PAJA, 0.4f)), dial.topLeft, dial.size)
        for (i in 0..10) {
            val x = dial.left + dial.width * (0.06f + 0.88f * i / 10f)
            drawLine(t.c(HOLLIN), Offset(x, dial.bottom - dial.height * (if (i % 2 == 0) 0.45f else 0.28f)), Offset(x, dial.bottom), 0.8f * u)
        }
        drawLine(t.c(HOLLIN).copy(alpha = 0.6f), Offset(dial.left + dial.width * 0.06f, dial.top + dial.height * 0.32f), Offset(dial.right - dial.width * 0.06f, dial.top + dial.height * 0.32f), 0.7f * u)
        drawLine(t.c(OXIDO), Offset(dial.left + dial.width * 0.62f, dial.top + 1f * u), Offset(dial.left + dial.width * 0.62f, dial.bottom - 1f * u), 1.3f * u)
        drawLine(t.b(0.6f, 0.4f), Offset(dial.left + dial.width * 0.1f, dial.top + dial.height * 0.9f), Offset(dial.left + dial.width * 0.4f, dial.top + 1f * u), 1f * u)
        // Las dos perillas, con sus estrías.
        listOf(0.2f, 0.62f).forEachIndexed { i, fx ->
            val c = Offset(dial.left + dial.width * fx + w * 0.008f, r.bottom - w * 0.03f)
            val rr = w * (if (i == 0) 0.011f else 0.014f)
            drawCircle(t.s(0.35f), rr, c + Offset(w * 0.003f, w * 0.003f))
            drawCircle(t.c(HOLLIN), rr, c)
            drawCircle(t.c(mezcla(HOLLIN, HOJALATA, 0.35f)), rr * 0.7f, c)
            for (k in 0 until 8) {
                val a = k * Math.PI / 4
                drawLine(t.c(HOLLIN), c + Offset((cos(a) * rr * 0.4f).toFloat(), (sin(a) * rr * 0.4f).toFloat()), c + Offset((cos(a) * rr * 0.68f).toFloat(), (sin(a) * rr * 0.68f).toFloat()), 0.6f * u)
            }
            drawCircle(t.b(0.5f, 0.3f), rr * 0.25f, c - Offset(rr * 0.35f, rr * 0.35f))
        }
        // Los tornillos de las esquinas: falta uno.
        listOf(Offset(r.left + w * 0.007f, r.top + w * 0.007f), Offset(r.right - w * 0.007f, r.top + w * 0.007f), Offset(r.left + w * 0.007f, r.bottom - w * 0.007f), Offset(r.right - w * 0.007f, r.bottom - w * 0.007f)).forEachIndexed { i, p ->
            if (i == 3) drawCircle(t.c(HOLLIN), w * 0.0022f, p)
            else { drawCircle(t.c(HOJALATA_OSC), w * 0.0028f, p); drawLine(t.c(HOLLIN), p - Offset(w * 0.002f, 0f), p + Offset(w * 0.002f, 0f), 0.6f * u) }
        }
        // Los cables que salen por la junta abierta, a la derecha, y caen por delante de la balda.
        listOf(TELA to 0f, AGUA to 1f, PAJA to 2f).forEach { (col, i) ->
            val a = Offset(r.right - w * 0.006f, r.top + r.height * 0.5f)
            val b = Offset(r.right + w * (0.012f + 0.008f * i), pie + w * (0.006f + 0.004f * i))
            val p = Path().apply { moveTo(a.x, a.y); cubicTo(a.x + w * 0.02f, a.y - w * 0.01f, b.x + w * 0.004f, b.y - w * 0.04f, b.x, b.y) }
            drawPath(p, t.c(mezcla(col, if (col == TELA) OXIDO else col, 0.3f)), style = Stroke(1.3f * u, cap = StrokeCap.Round))
            drawCircle(t.c(OXIDO_VIVO), 1.1f * u, b)
        }
        // La manivela, desplegada, en el costado derecho.
        val eje = Offset(r.right - w * 0.008f, r.top + r.height * 0.32f)
        val mano = Offset(r.right + w * 0.022f, r.top + r.height * 0.02f)
        drawLine(t.s(0.3f), eje + Offset(w * 0.004f, w * 0.004f), mano + Offset(w * 0.004f, w * 0.004f), w * 0.007f, StrokeCap.Round)
        drawLine(t.c(HOLLIN), eje, mano, w * 0.0065f, StrokeCap.Round)
        drawLine(t.b(0.35f, 0.3f), eje - Offset(0f, w * 0.002f), mano - Offset(0f, w * 0.002f), 0.8f * u)
        drawCircle(t.c(mezcla(HOLLIN, HOJALATA, 0.3f)), w * 0.008f, eje)
        drawRoundRect(t.c(PAJA), Offset(mano.x - w * 0.006f, mano.y - w * 0.016f), Size(w * 0.012f, w * 0.018f), CornerRadius(w * 0.006f))
        drawRoundRect(t.s(0.3f), Offset(mano.x + w * 0.0005f, mano.y - w * 0.016f), Size(w * 0.0055f, w * 0.018f), CornerRadius(w * 0.003f))
        // Delante, en la balda: la tapa de las pilas, el destornillador y tres tornillos sueltos.
        val tapa = poligono(r.left + w * 0.02f, pie + w * 0.002f, r.left + w * 0.07f, pie + w * 0.001f, r.left + w * 0.075f, pie - w * 0.007f, r.left + w * 0.027f, pie - w * 0.006f)
        translate(1f * u, 1f * u) { drawPath(tapa, t.s(0.35f)) }
        drawPath(tapa, t.c(mezcla(TELA, HOLLIN, 0.25f)))
        drawLine(t.b(0.35f, 0.2f), Offset(r.left + w * 0.027f, pie - w * 0.006f), Offset(r.left + w * 0.075f, pie - w * 0.007f), 0.8f * u)
        val d0 = Offset(r.left + w * 0.11f, pie + w * 0.003f); val d1 = Offset(r.left + w * 0.19f, pie - w * 0.005f)
        drawLine(t.s(0.35f), d0 + Offset(1.4f * u, 1.6f * u), d1 + Offset(1.4f * u, 1.6f * u), w * 0.006f, StrokeCap.Round)
        drawLine(t.c(HOJALATA), d0 + (d1 - d0) * 0.5f, d1, w * 0.0025f, StrokeCap.Butt)
        drawLine(t.c(PAJA), d0, d0 + (d1 - d0) * 0.5f, w * 0.008f, StrokeCap.Round)
        drawLine(t.b(0.45f, 0.3f), d0 + Offset(0f, -w * 0.002f), d0 + (d1 - d0) * 0.45f + Offset(0f, -w * 0.002f), 0.8f * u)
        listOf(0.205f, 0.218f, 0.226f).forEachIndexed { i, fx ->
            val p = Offset(w * fx + w * 0.012f * i, pie - w * 0.001f * i)
            drawCircle(t.c(HOJALATA_OSC), w * 0.0028f, p)
            drawCircle(t.b(0.5f, 0.3f), w * 0.001f, p - Offset(w * 0.001f, w * 0.001f))
        }
    }

    /** El tarro de cristal con semillas y su tapa de rosca oxidada. */
    private fun cuerpoTarro(w: Float, pie: Float) = Rect(w * 0.515f, pie - w * 0.07f, w * 0.57f, pie)

    private fun DrawScope.tarro(pie: Float, w: Float, oscuro: Boolean) {
        val u = density
        val r = cuerpoTarro(w, pie)
        val t = Tono(oscuro, if (oscuro) luzFarol(Offset(0.55f, pie / w - 0.04f)) else 0f)
        drawOval(t.s(0.35f), Offset(r.left - w * 0.002f, pie - w * 0.005f), Size(r.width + w * 0.016f, w * 0.01f))
        val cuerpo = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(r.left, r.top + w * 0.012f, r.right, r.bottom), CornerRadius(w * 0.008f))) }
        // El vidrio: se ve el muro detrás, un poco verde.
        drawPath(cuerpo, t.c(AGUA).copy(alpha = 0.22f))
        clipPath(cuerpo) {
            // Las semillas: dos tercios del tarro, con su superficie inclinada y granos sueltos.
            val sup = r.top + r.height * 0.38f
            drawPath(poligono(r.left, sup + w * 0.004f, r.right, sup - w * 0.002f, r.right, r.bottom, r.left, r.bottom), t.c(mezcla(PAJA, TIERRA, 0.35f)))
            for (i in 0 until 26) {
                val p = Offset(r.left + r.width * azar(911, i), sup + (r.bottom - sup) * azar(912, i))
                drawOval(t.c(if (i % 3 == 0) TIERRA else if (i % 3 == 1) PAJA else mezcla(PAJA, LUZ_MUEBLE, 0.4f)), Offset(p.x - w * 0.002f, p.y - w * 0.0014f), Size(w * 0.004f, w * 0.0028f))
            }
            drawRect(t.s(0.25f), Offset(r.right - r.width * 0.3f, r.top), Size(r.width * 0.3f, r.height))
        }
        // Los brillos del vidrio (de día a la izquierda; de noche, el farol a la derecha) y el canto oscuro.
        val bx = if (t.luzIzq) r.left + r.width * 0.16f else r.right - r.width * 0.26f
        drawRect(if (oscuro) LLAMA.copy(alpha = 0.55f * t.k + 0.05f) else luzMueble(0.55f), Offset(bx, r.top + w * 0.018f), Size(r.width * 0.1f, r.height * 0.7f))
        drawRect(if (oscuro) LLAMA.copy(alpha = 0.3f * t.k) else luzMueble(0.3f), Offset(bx + r.width * 0.16f, r.top + w * 0.02f), Size(r.width * 0.04f, r.height * 0.5f))
        drawPath(cuerpo, t.c(mezcla(AGUA, HOLLIN, 0.4f)).copy(alpha = 0.6f), style = Stroke(1f * u))
        // La tapa de rosca, oxidada.
        val tapa = Rect(r.left + w * 0.003f, r.top, r.right - w * 0.003f, r.top + w * 0.013f)
        drawRect(t.c(OXIDO), tapa.topLeft, tapa.size)
        for (i in 1..3) drawLine(t.c(OXIDO_OSC), Offset(tapa.left, tapa.top + tapa.height * i / 4f), Offset(tapa.right, tapa.top + tapa.height * i / 4f), 0.7f * u)
        drawRect(t.b(0.35f, 0.3f), tapa.topLeft, Size(tapa.width, 1f * u))
        drawRect(t.c(HOJALATA).copy(alpha = 0.5f), Offset(tapa.left + tapa.width * 0.1f, tapa.top + 1f * u), Size(tapa.width * 0.2f, tapa.height * 0.4f))
    }

    /** La taza de esmalte desportillada, con tierra y un brote. */
    private fun DrawScope.taza(pie: Float, w: Float, oscuro: Boolean) {
        val u = density
        val r = Rect(w * 0.6f, pie - w * 0.044f, w * 0.648f, pie)
        val t = Tono(oscuro, if (oscuro) luzFarol(Offset(0.62f, pie / w - 0.03f)) else 0f)
        drawOval(t.s(0.35f), Offset(r.left - w * 0.002f, pie - w * 0.005f), Size(r.width + w * 0.022f, w * 0.01f))
        // El asa, a la derecha.
        drawOval(t.c(PAPEL), Offset(r.right - w * 0.006f, r.top + w * 0.008f), Size(w * 0.02f, w * 0.022f), style = Stroke(w * 0.0045f))
        drawArc(t.c(AGUA), -60f, 120f, false, Offset(r.right - w * 0.006f, r.top + w * 0.008f), Size(w * 0.02f, w * 0.022f), style = Stroke(w * 0.0015f))
        // El cuerpo de esmalte crema, con el borde y el pie de azul verdoso.
        drawRect(t.c(PAPEL), r.topLeft, r.size)
        drawRect(t.c(AGUA), r.topLeft, Size(r.width, w * 0.004f))
        drawRect(t.c(mezcla(AGUA, HOLLIN, 0.2f)), Offset(r.left, r.bottom - w * 0.003f), Size(r.width, w * 0.003f))
        if (t.luzIzq) drawRect(t.s(0.25f), Offset(r.right - r.width * 0.32f, r.top), Size(r.width * 0.32f, r.height))
        else drawRect(t.s(0.3f), r.topLeft, Size(r.width * 0.35f, r.height))
        drawRect(t.b(0.5f, 0.3f), Offset(if (t.luzIzq) r.left + r.width * 0.14f else r.right - r.width * 0.2f, r.top + w * 0.006f), Size(r.width * 0.07f, r.height * 0.75f))
        // Los desportillados: el hierro negro debajo del esmalte, uno en el borde.
        listOf(Offset(0.3f, 0.45f), Offset(0.7f, 0.75f), Offset(0.15f, 0.05f)).forEachIndexed { i, o ->
            val p = Offset(r.left + r.width * o.x, r.top + r.height * o.y)
            val q = w * (0.003f + 0.002f * i)
            drawPath(poligono(p.x - q, p.y, p.x, p.y - q * 0.8f, p.x + q * 1.1f, p.y - q * 0.2f, p.x + q * 0.4f, p.y + q * 0.8f), t.c(HOLLIN))
        }
        // La tierra y el brote: un tallo con dos hojas abiertas y una que asoma.
        drawOval(t.c(TIERRA), Offset(r.left + w * 0.002f, r.top + w * 0.002f), Size(r.width - w * 0.004f, w * 0.006f))
        val b = Offset(r.center.x - w * 0.003f, r.top + w * 0.004f)
        val c = b + Offset(w * 0.004f, -w * 0.032f)
        drawPath(Path().apply { moveTo(b.x, b.y); cubicTo(b.x - w * 0.004f, b.y - w * 0.012f, c.x - w * 0.002f, c.y + w * 0.012f, c.x, c.y) }, t.c(HELECHO), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        hoja(c, w * 0.022f, -62f, HIEDRA_LUZ, t, ancho = 0.75f)
        hoja(c, w * 0.019f, 58f, HELECHO, t, ancho = 0.75f)
        hoja(c + Offset(0f, w * 0.002f), w * 0.01f, 5f, HELECHO, t, sombraFuera = false, ancho = 0.6f)
    }

    /** El farol de queroseno: depósito, globo de vidrio, guarda de tubos, sombrerete y asa. De noche, encendido. */
    private fun DrawScope.farol(pie: Float, w: Float, oscuro: Boolean) {
        val u = density
        val cx = w * FAROL.x
        val t = Tono(oscuro, if (oscuro) 0.9f else 0f)
        val dep = Rect(cx - w * 0.043f, pie - w * 0.034f, cx + w * 0.043f, pie)
        val g0 = dep.top - w * 0.01f; val g1 = g0 - w * 0.088f
        val som = Rect(cx - w * 0.036f, g1 - w * 0.022f, cx + w * 0.036f, g1 + w * 0.002f)
        // De día, su sombra en el muro, a la derecha (solo por encima de la balda).
        if (!oscuro) clipRect(0f, 0f, w * 2, pie - w * CARA * 0.5f) {
            translate(w * 0.02f, w * 0.006f) {
                val c = sombraMueble(0.17f)
                drawRoundRect(c, dep.topLeft, dep.size, CornerRadius(w * 0.014f))
                drawRect(c, Offset(cx - w * 0.046f, g1), Size(w * 0.092f, g0 - g1 + w * 0.01f))
                drawRect(c, Offset(som.left, som.top), som.size)
                drawCircle(c, w * 0.02f, Offset(cx, som.top - w * 0.012f), style = Stroke(1.4f * density))
            }
        }
        drawOval(t.s(if (oscuro) 0.5f else 0.38f), Offset(dep.left - w * 0.004f, pie - w * 0.008f), Size(dep.width + w * 0.03f, w * 0.014f))
        // El asa de alambre, arriba, y su anilla.
        val asa = Path().apply { moveTo(som.left + w * 0.004f, som.bottom - w * 0.006f); cubicTo(som.left - w * 0.01f, som.top - w * 0.04f, som.right + w * 0.01f, som.top - w * 0.04f, som.right - w * 0.004f, som.bottom - w * 0.006f) }
        drawPath(asa, t.c(HOJALATA_OSC), style = Stroke(1.4f * u))
        drawCircle(t.c(HOJALATA_OSC), w * 0.005f, Offset(cx, som.top - w * 0.028f), style = Stroke(1.2f * u))
        // Los tubos de la guarda, a los lados del globo, que se cierran arriba.
        listOf(-1f, 1f).forEach { s ->
            val p = Path().apply { moveTo(cx + s * w * 0.04f, dep.top + w * 0.004f); lineTo(cx + s * w * 0.046f, g0 - w * 0.02f); cubicTo(cx + s * w * 0.048f, g1, cx + s * w * 0.04f, som.top, cx + s * w * 0.02f, som.top) }
            drawPath(p, t.c(mezcla(VERDE_MILITAR, HOJALATA_OSC, 0.4f)), style = Stroke(w * 0.007f, cap = StrokeCap.Round))
            drawPath(p, if (s < 0 == t.luzIzq) t.b(0.4f, 0.3f) else t.s(0.3f), style = Stroke(w * 0.0022f))
        }
        // El globo de vidrio, abombado.
        val globo = Path().apply {
            moveTo(cx - w * 0.018f, g0)
            cubicTo(cx - w * 0.04f, g0 - w * 0.02f, cx - w * 0.04f, g1 + w * 0.03f, cx - w * 0.02f, g1)
            lineTo(cx + w * 0.02f, g1)
            cubicTo(cx + w * 0.04f, g1 + w * 0.03f, cx + w * 0.04f, g0 - w * 0.02f, cx + w * 0.018f, g0)
            close()
        }
        val llamaC = Offset(cx, g0 - w * 0.03f)
        if (oscuro) {
            drawPath(globo, Brush.radialGradient(listOf(Color(0xFFFFF0C0), LLAMA, mezcla(LLAMA, TELA, 0.5f)), llamaC, w * 0.045f))
            // La llama y la mecha.
            drawPath(Path().apply { moveTo(cx - w * 0.006f, llamaC.y + w * 0.012f); cubicTo(cx - w * 0.009f, llamaC.y, cx - w * 0.002f, llamaC.y - w * 0.012f, cx, llamaC.y - w * 0.02f); cubicTo(cx + w * 0.003f, llamaC.y - w * 0.01f, cx + w * 0.009f, llamaC.y, cx + w * 0.006f, llamaC.y + w * 0.012f); close() }, Color(0xFFFFF6DA))
            drawRect(Color(0xFFFFE6A0), Offset(cx - w * 0.007f, llamaC.y + w * 0.011f), Size(w * 0.014f, w * 0.004f))
        } else {
            drawPath(globo, mezcla(HORMIGON_CALIDO, AGUA, 0.25f).copy(alpha = 0.55f))
            clipPath(globo) {
                // El hollín arriba, la mecha apagada en su quemador y el muro que se ve a través.
                drawRect(HOLLIN.copy(alpha = 0.35f), Offset(cx - w * 0.04f, g1), Size(w * 0.08f, w * 0.022f))
                drawRect(HOLLIN.copy(alpha = 0.15f), Offset(cx - w * 0.04f, g1 + w * 0.022f), Size(w * 0.08f, w * 0.012f))
                drawRect(sombraMueble(0.18f), Offset(cx + w * 0.008f, g1), Size(w * 0.04f, g0 - g1))
            }
            drawRect(HOJALATA_OSC, Offset(cx - w * 0.009f, g0 - w * 0.012f), Size(w * 0.018f, w * 0.012f))
            drawRect(HOLLIN, Offset(cx - w * 0.005f, g0 - w * 0.016f), Size(w * 0.01f, w * 0.004f))
            drawPath(Path().apply { moveTo(cx - w * 0.026f, g0 - w * 0.012f); cubicTo(cx - w * 0.033f, g0 - w * 0.04f, cx - w * 0.031f, g1 + w * 0.02f, cx - w * 0.02f, g1 + w * 0.006f) }, luzMueble(0.7f), style = Stroke(w * 0.004f, cap = StrokeCap.Round))
            drawLine(luzMueble(0.45f), Offset(cx - w * 0.012f, g0 - w * 0.03f), Offset(cx - w * 0.01f, g1 + w * 0.012f), 1f * u)
        }
        drawPath(globo, t.c(mezcla(AGUA, HOLLIN, 0.5f)).copy(alpha = 0.6f), style = Stroke(1f * u))
        // Los alambres de la guarda por delante del globo.
        listOf(-0.016f, 0.016f).forEach { fx -> drawLine(t.c(HOJALATA_OSC).copy(alpha = 0.9f), Offset(cx + w * fx * 1.4f, g0), Offset(cx + w * fx, g1), 1f * u) }
        // El sombrerete con sus respiraderos.
        val somP = Path().apply { moveTo(som.left, som.bottom); lineTo(som.left + w * 0.012f, som.top); lineTo(som.right - w * 0.012f, som.top); lineTo(som.right, som.bottom); close() }
        drawPath(somP, t.c(VERDE_MILITAR))
        clipPath(somP) {
            drawRect(if (t.luzIzq) t.s(0.3f) else t.s(0.35f), Offset(if (t.luzIzq) cx + w * 0.01f else som.left, som.top), Size(w * 0.026f, som.height))
            for (i in 0 until 5) drawCircle(t.c(HOLLIN), w * 0.0025f, Offset(som.left + w * (0.014f + 0.011f * i), som.top + som.height * 0.55f))
            drawRect(t.c(OXIDO).copy(alpha = 0.5f), Offset(som.left, som.bottom - w * 0.004f), Size(som.width, w * 0.004f))
        }
        drawLine(t.b(0.4f, 0.4f), Offset(som.left + w * 0.012f, som.top), Offset(som.right - w * 0.012f, som.top), 1f * u)
        if (oscuro) drawLine(LLAMA.copy(alpha = 0.7f), Offset(som.left + w * 0.003f, som.bottom), Offset(som.right - w * 0.003f, som.bottom), 1.4f * u)
        // El quemador y el depósito, verde militar con su óxido, la boca del queroseno y la rueda de la mecha.
        drawRect(t.c(mezcla(HOJALATA_OSC, HOLLIN, 0.2f)), Offset(cx - w * 0.024f, g0), Size(w * 0.048f, dep.top - g0))
        drawRoundRect(t.c(VERDE_MILITAR), dep.topLeft, dep.size, CornerRadius(w * 0.014f))
        clipPath(Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(dep, CornerRadius(w * 0.014f))) }) {
            if (t.luzIzq) {
                drawRect(sombraMueble(0.3f), Offset(dep.right - dep.width * 0.3f, dep.top), Size(dep.width * 0.3f, dep.height))
                drawRect(luzMueble(0.3f), Offset(dep.left + dep.width * 0.12f, dep.top), Size(dep.width * 0.1f, dep.height))
            } else {
                // De noche, la luz del globo baja sobre el depósito.
                drawRect(LLAMA.copy(alpha = 0.35f), dep.topLeft, Size(dep.width, dep.height * 0.3f))
                drawRect(NOCHE.copy(alpha = 0.4f), Offset(dep.left, dep.bottom - dep.height * 0.4f), Size(dep.width, dep.height * 0.4f))
            }
            drawRect(t.c(OXIDO).copy(alpha = 0.6f), Offset(dep.left, dep.bottom - w * 0.008f), Size(dep.width, w * 0.008f))
            for (i in 0 until 6) drawCircle(t.c(if (i % 2 == 0) OXIDO else HOJALATA), w * (0.002f + 0.003f * azar(921, i)), Offset(dep.left + dep.width * azar(922, i), dep.top + dep.height * azar(923, i)))
            drawLine(t.s(0.3f), Offset(dep.left, dep.top + dep.height * 0.35f), Offset(dep.right, dep.top + dep.height * 0.35f), 0.8f * u)
        }
        drawCircle(t.c(HOJALATA_OSC), w * 0.006f, Offset(dep.left + w * 0.016f, dep.top + w * 0.002f))
        drawLine(t.c(HOJALATA_OSC), Offset(cx + w * 0.024f, g0 + w * 0.005f), Offset(cx + w * 0.034f, g0 + w * 0.005f), 1.4f * u)
        drawCircle(t.c(HOJALATA), w * 0.005f, Offset(cx + w * 0.036f, g0 + w * 0.005f))
    }

    // ---------------------------------------------------------------- el pie

    /** La regadera de chapa, abollada, con su alcachofa. */
    private fun DrawScope.regadera(c: Offset, w: Float, oscuro: Boolean) {
        val u = density
        val t = Tono(oscuro, if (oscuro) luzFarol(c / w) else 0f)
        val r = Rect(c.x - w * 0.055f, c.y - w * 0.085f, c.x + w * 0.05f, c.y)
        drawOval(t.s(0.38f), Offset(r.left, c.y - w * 0.008f), Size(r.width + w * 0.07f, w * 0.018f))
        // El pitorro, largo, con la alcachofa.
        val p0 = Offset(r.right - w * 0.006f, r.bottom - w * 0.022f); val p1 = Offset(r.right + w * 0.07f, r.top + w * 0.005f)
        drawLine(t.c(HOJALATA_OSC), p0, p1, w * 0.012f, StrokeCap.Butt)
        drawLine(t.b(0.35f, 0.2f), p0 - Offset(0f, w * 0.004f), p1 - Offset(0f, w * 0.004f), 1f * u)
        rotate(-35f, p1) {
            drawRoundRect(t.c(mezcla(HOJALATA, OXIDO, 0.25f)), Offset(p1.x - w * 0.004f, p1.y - w * 0.014f), Size(w * 0.016f, w * 0.028f), CornerRadius(w * 0.004f))
            drawRect(t.c(HOLLIN), Offset(p1.x + w * 0.009f, p1.y - w * 0.013f), Size(w * 0.003f, w * 0.026f))
        }
        // El asa de arriba y la de atrás.
        drawPath(Path().apply { moveTo(r.left + w * 0.008f, r.top + w * 0.006f); cubicTo(r.left + w * 0.01f, r.top - w * 0.04f, r.right - w * 0.02f, r.top - w * 0.04f, r.right - w * 0.016f, r.top + w * 0.004f) },
            t.c(HOJALATA_OSC), style = Stroke(w * 0.006f))
        drawArc(t.c(HOJALATA_OSC), 90f, 180f, false, Offset(r.left - w * 0.018f, r.top + w * 0.02f), Size(w * 0.036f, w * 0.04f), style = Stroke(w * 0.005f))
        // El cuerpo, con sus aros, una abolladura y el óxido del culo.
        val cuerpo = Path().apply { moveTo(r.left + w * 0.004f, r.top); lineTo(r.right - w * 0.004f, r.top); lineTo(r.right, r.bottom); lineTo(r.left, r.bottom); close() }
        drawPath(cuerpo, t.c(HOJALATA))
        clipPath(cuerpo) {
            if (t.luzIzq) drawRect(sombraMueble(0.3f), Offset(r.right - r.width * 0.32f, r.top), Size(r.width * 0.32f, r.height))
            else drawRect(NOCHE.copy(alpha = 0.45f), r.topLeft, Size(r.width * 0.35f, r.height))
            drawRect(t.b(0.45f, 0.3f), Offset(if (t.luzIzq) r.left + r.width * 0.12f else r.right - r.width * 0.2f, r.top), Size(r.width * 0.08f, r.height))
            listOf(0.1f, 0.55f, 0.9f).forEach { fy ->
                val y = r.top + r.height * fy
                drawLine(t.s(0.3f), Offset(r.left, y), Offset(r.right, y), 1.4f * u)
                drawLine(t.b(0.35f, 0.2f), Offset(r.left, y + 1.4f * u), Offset(r.right, y + 1.4f * u), 0.8f * u)
            }
            // La abolladura: una media luna honda con su filo de luz.
            val ab = Offset(r.left + r.width * 0.38f, r.top + r.height * 0.35f)
            drawOval(t.s(0.3f), Offset(ab.x - w * 0.016f, ab.y - w * 0.012f), Size(w * 0.03f, w * 0.022f))
            drawArc(t.b(0.55f, 0.3f), 20f, 140f, false, Offset(ab.x - w * 0.014f, ab.y - w * 0.008f), Size(w * 0.03f, w * 0.022f), style = Stroke(1.2f * u))
            drawRect(t.c(OXIDO).copy(alpha = 0.55f), Offset(r.left, r.bottom - w * 0.012f), Size(r.width, w * 0.012f))
            for (i in 0 until 5) drawCircle(t.c(OXIDO).copy(alpha = 0.5f), w * (0.003f + 0.004f * azar(931, i)), Offset(r.left + r.width * azar(932, i), r.bottom - w * 0.012f - w * 0.02f * azar(933, i)))
        }
    }

    /** La caja de madera con tubérculos, cortada por el borde derecho. */
    private fun DrawScope.caja(r: Rect, w: Float, oscuro: Boolean) {
        val u = density
        val t = Tono(oscuro, if (oscuro) luzFarol(r.center / w) else 0f)
        drawRect(t.s(0.4f), Offset(r.left + w * 0.02f, r.bottom - w * 0.006f), Size(r.width, w * 0.012f))
        // Los tubérculos de arriba, que asoman.
        for (i in 0 until 9) {
            val c = Offset(r.left + r.width * (0.08f + 0.1f * i), r.top + w * (0.004f - 0.008f * azar(941, i)))
            val q = w * (0.014f + 0.006f * azar(942, i))
            drawOval(t.c(mezcla(TIERRA, PAJA, 0.35f + 0.2f * azar(943, i))), Offset(c.x - q, c.y - q * 0.7f), Size(q * 2, q * 1.4f))
            drawOval(t.b(0.3f, 0.2f), Offset(c.x - q * 0.6f, c.y - q * 0.6f), Size(q * 0.8f, q * 0.5f))
            drawCircle(t.c(TIERRA), 0.9f * u, c + Offset(q * 0.3f, q * 0.1f))
        }
        // Las tablas, con su hueco entre ellas, y el listón de la esquina.
        val tablas = 3
        for (i in 0 until tablas) {
            val y = r.top + r.height * i / tablas
            val tr = Rect(r.left, y + w * 0.003f, r.right, y + r.height / tablas)
            drawRect(t.c(if (i == 1) MADERA else MADERA_GRIS), tr.topLeft, tr.size)
            vetas(tr, MADERA_OSC.copy(alpha = 0.35f), 950 + i, 0.8f, grosor = 0.6f)
            drawRect(t.b(0.35f, 0.2f), tr.topLeft, Size(tr.width, 1f * u))
            drawRect(t.s(0.45f), Offset(tr.left, y), Size(tr.width, w * 0.003f))
        }
        drawRect(t.c(mezcla(MADERA_GRIS, MADERA_OSC, 0.3f)), Offset(r.left, r.top), Size(w * 0.016f, r.height))
        drawRect(t.b(0.3f, 0.2f), Offset(r.left, r.top), Size(1.2f * u, r.height))
        drawRect(t.s(0.3f), Offset(r.left + w * 0.016f, r.top), Size(1.4f * u, r.height))
        listOf(0.2f, 0.55f, 0.85f).forEach { fy -> drawCircle(t.c(HOLLIN), 1.1f * u, Offset(r.left + w * 0.008f, r.top + r.height * fy)) }
    }

    /** Un helecho de [n] frondas que nace en [b]. */
    private fun DrawScope.helecho(b: Offset, s: Float, n: Int, semilla: Int, w: Float, oscuro: Boolean) {
        val u = density
        val t = Tono(oscuro, if (oscuro) luzFarol(b / w) * 0.9f else 0f)
        for (i in 0 until n) {
            val a = -150f + 120f * i / (n - 1).coerceAtLeast(1) + (azar(semilla, i) - 0.5f) * 14f
            val l = s * (0.7f + 0.4f * azar(semilla, i + 10))
            val ar = Math.toRadians(a.toDouble())
            val fin = b + Offset((cos(ar) * l).toFloat(), (sin(ar) * l * 0.85f).toFloat())
            // La fronda se arquea: sube y luego cae.
            val ctrl = b + Offset((cos(ar) * l * 0.5f).toFloat(), (sin(ar) * l * 0.9f).toFloat() - l * 0.2f)
            val raquis = Path().apply { moveTo(b.x, b.y); cubicTo(ctrl.x, ctrl.y, ctrl.x, ctrl.y, fin.x, fin.y) }
            val col = if (i % 2 == 0) HELECHO else MUSGO
            translate(s * 0.05f, s * 0.04f) { drawPath(raquis, t.s(0.2f), style = Stroke(1.6f * u)) }
            drawPath(raquis, t.c(mezcla(col, TIERRA, 0.2f)), style = Stroke(1.3f * u, cap = StrokeCap.Round))
            val pasos = 10
            for (j in 1..pasos) {
                val f = j / (pasos + 1f)
                fun bez(g: Float) = b * ((1 - g) * (1 - g) * (1 - g)) + ctrl * (3 * (1 - g) * (1 - g) * g + 3 * (1 - g) * g * g) + fin * (g * g * g)
                val p = bez(f)
                val q = bez(f + 0.02f)
                val dir = q - p
                val ang = Math.toDegrees(kotlin.math.atan2(dir.x.toDouble(), -dir.y.toDouble())).toFloat()
                val largo = s * 0.16f * (1f - f * 0.75f)
                listOf(-1f, 1f).forEach { sg ->
                    rotate(ang + sg * 70f, p) {
                        drawOval(t.c(if (sg < 0) col else mezcla(col, HIEDRA, 0.35f)), Offset(p.x - largo * 0.18f, p.y - largo), Size(largo * 0.36f, largo))
                    }
                }
            }
        }
    }

    /** Unas setas en el rincón húmedo, junto a [b]. */
    private fun DrawScope.setas(b: Offset, w: Float, oscuro: Boolean) {
        val u = density
        val t = Tono(oscuro, if (oscuro) luzFarol(b / w) else 0f)
        listOf(Triple(0f, 0.02f, 0.016f), Triple(0.018f, 0.014f, 0.012f), Triple(-0.014f, 0.009f, 0.008f)).forEach { (dx, alto, r) ->
            val p = b + Offset(w * dx, 0f)
            drawOval(t.s(0.3f), Offset(p.x - w * r * 0.6f, p.y - w * 0.002f), Size(w * r * 1.8f, w * 0.005f))
            drawLine(t.c(mezcla(PAPEL, PAJA, 0.4f)), p, p - Offset(-w * 0.002f, w * alto), w * r * 0.45f, StrokeCap.Round)
            val som = Rect(p.x - w * r + w * 0.002f, p.y - w * alto - w * r * 0.7f, p.x + w * r + w * 0.002f, p.y - w * alto + w * r * 0.3f)
            drawArc(t.c(mezcla(TIERRA, PAJA, 0.4f)), 180f, 180f, true, som.topLeft, som.size)
            drawArc(t.b(0.4f, 0.3f), 200f, 70f, false, Offset(som.left + w * r * 0.2f, som.top + w * r * 0.15f), Size(som.width * 0.7f, som.height * 0.8f), style = Stroke(1.2f * u))
            drawLine(t.c(mezcla(TIERRA, HOLLIN, 0.3f)), Offset(som.left, som.center.y), Offset(som.right, som.center.y), 1f * u)
        }
    }

    // ---------------------------------------------------------------- el mueble

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width; val alto = size.height
        val b = d.base; val hb = b.height
        val rincon = b.top + hb * RINCON
        val pie = b.top + hb * PIES
        val tabla0 = w * TAB
        fun tk(x: Float, y: Float) = Tono(oscuro, if (oscuro) luzFarol(Offset(x / w, y / w)) else 0f)

        // ---- el muro y su luz
        muro(w, rincon + 1f * u, oscuro)
        val boquete = manchaBoquete(w)
        if (!oscuro) {
            drawPath(boquete, luzMueble(0.3f))
            clipPath(boquete) {
                drawRect(luzMueble(0.12f), Offset.Zero, Size(w * 0.36f, w * 0.26f))
                hojasDelBoquete(w, mezcla(SOMBRA_MUEBLE, HIEDRA, 0.3f).copy(alpha = 0.2f))
            }
            // Las motas de sol que se cuelan entre las hojas, fuera de la mancha.
            for (i in 0 until 9) {
                val c = Offset(w * (0.05f + 0.55f * azar(1001, i)), w * (0.36f + 0.1f * azar(1002, i)))
                val q = w * (0.006f + 0.01f * azar(1003, i))
                drawOval(luzMueble(0.2f), Offset(c.x - q, c.y - q * 0.6f), Size(q * 2, q * 1.2f))
            }
        } else {
            drawPath(boquete, LUNA.copy(alpha = 0.07f))
            clipPath(boquete) { hojasDelBoquete(w, NOCHE.copy(alpha = 0.35f)) }
            // El halo del farol en el hormigón: sube y se abre por el muro.
            mancha(Offset(w * FAROL.x, w * (FAROL.y - 0.05f)), w * 0.75f, LLAMA.copy(alpha = 0.2f))
            mancha(Offset(w * FAROL.x, w * FAROL.y), w * 0.32f, LLAMA.copy(alpha = 0.26f))
            mancha(Offset(w * FAROL.x, w * (FAROL.y + 0.35f)), w * 0.75f, LLAMA.copy(alpha = 0.13f))
        }

        // ---- lo pintado y lo roto en el muro, y lo colgado
        plantilla(Offset(w * 0.47f, w * 0.12f), w * 0.07f, oscuro)
        grieta(w, oscuro)
        mapa(w, oscuro)
        hierbas(w, oscuro)

        // ---- de noche, el muro del fondo de cada hueco toma la luz del farol según lo cerca que esté (arriba a la derecha, más)
        if (oscuro) d.huecos.forEach { r ->
            val kk = luzFarol(Offset(r.center.x / w, r.center.y / w))
            if (kk > 0.05f) for (j in 0 until 6) {
                val f = 1f - j * 0.13f
                val c = Offset(r.right - r.width * 0.25f, r.top + r.height * 0.3f)
                drawOval(LLAMA.copy(alpha = 0.05f * kk), Offset(c.x - r.width * 0.6f * f, c.y - r.height * 0.6f * f), Size(r.width * 1.2f * f, r.height * 1.2f * f))
            }
        }

        // ---- las sombras en el muro de lo que tiene delante: lo de encima y la estantería
        val pieArriba = tabla0 + w * CARA * 0.6f
        // Lo de encima va un poco más grande que su medida de base: la radio con los libros y el tarro con la taza.
        val pivRadio = Offset(w * 0.22f, pieArriba); val pivTarro = Offset(w * 0.585f, pieArriba)
        val escRadio = 1.3f; val escTarro = 1.35f
        withTransform({ scale(escRadio, escRadio, pivRadio) }) {
            sombraEnMuro(siluetaRadio(w, pieArriba), cuerpoRadio(w, pieArriba).center, w, oscuro, tabla0 + 1f, 0.9f)
            sombraEnMuro(Path().apply { addRect(Rect(w * 0.035f, pieArriba - w * 0.066f, w * 0.17f, pieArriba)) }, Offset(w * 0.1f, pieArriba - w * 0.03f), w, oscuro, tabla0 + 1f, 0.8f)
        }
        withTransform({ scale(escTarro, escTarro, pivTarro) }) {
            sombraEnMuro(Path().apply { addRect(cuerpoTarro(w, pieArriba)) }, cuerpoTarro(w, pieArriba).center, w, oscuro, tabla0 + 1f, 0.8f)
            sombraEnMuro(Path().apply { addRect(Rect(w * 0.6f, pieArriba - w * 0.044f, w * 0.65f, pieArriba)) }, Offset(w * 0.62f, pieArriba - w * 0.02f), w, oscuro, tabla0 + 1f, 0.8f)
        }
        // La antena de alambre: del clavo del muro a la radio, y su sombra.
        val clavoA = Offset(w * 0.3f, w * 0.045f)
        val bornes = pivRadio + (Offset(w * 0.19f, pieArriba - w * 0.11f) - pivRadio) * escRadio
        val antena = Path().apply { moveTo(bornes.x, bornes.y); cubicTo(bornes.x + w * 0.01f, bornes.y - w * 0.14f, clavoA.x - w * 0.04f, clavoA.y + w * 0.12f, clavoA.x, clavoA.y) }
        translate(if (oscuro) -w * 0.012f else w * 0.016f, w * 0.01f) { drawPath(antena, sombra(oscuro, 0.22f), style = Stroke(1.3f * u)) }

        // La estantería en el muro: bajo cada balda, su sombra; junto a cada poste, la suya.
        val niveles = listOf(tabla0) + d.filas.map { it.bottom - w * CARA } + listOf(b.top + hb * BALDA_PIE)
        niveles.forEach { y ->
            val yb = y + w * (CARA + 0.026f + ANGULO)
            drawPath(poligono(w * 0.03f, yb, w * 0.99f, yb, w, yb + w * 0.012f, w, yb + w * 0.03f, w * 0.05f, yb + w * 0.03f), sombra(oscuro, if (oscuro) 0.4f else 0.2f))
        }
        POSTES.forEachIndexed { i, p ->
            val dx = if (!oscuro) w * 0.016f else if (p[1] < FAROL.x) -w * 0.016f else w * 0.016f
            drawRect(sombra(oscuro, if (oscuro) 0.35f else 0.18f), Offset((if (dx > 0) p[2] * w else p[0] * w + dx), w * 0.48f), Size(abs(dx), rincon - w * 0.48f))
        }

        // ---- el suelo de losas, con la sombra de la estantería
        val suelo = Rect(0f, rincon, w, alto)
        drawRect(Brush.verticalGradient(listOf(Tono(oscuro, 0f).c(mezcla(LOSA, SOMBRA_MUEBLE, 0.35f)), Tono(oscuro, 0.05f).c(LOSA), Tono(oscuro, 0.08f).c(mezcla(LOSA, LUZ_MUEBLE, 0.15f))), rincon, alto), suelo.topLeft, suelo.size)
        val juntas = listOf(0.2f, 0.45f, 0.78f).map { rincon + (alto - rincon) * it }
        val fuga = Offset(w * 0.5f, rincon - w * 1.1f)
        var y0 = rincon
        juntas.forEachIndexed { i, y ->
            drawLine(sombra(oscuro, 0.35f), Offset(0f, y), Offset(w, y), 1.2f * u)
            drawLine(brillo(oscuro, 0.2f, 0.05f), Offset(0f, y + 1.2f * u), Offset(w, y + 1.2f * u), 0.7f * u)
            for (j in -5..5) {
                val xb = w * 0.5f + (j + (if (i % 2 == 0) 0f else 0.5f)) * w * 0.24f
                fun x(yy: Float) = fuga.x + (xb - fuga.x) * (yy - fuga.y) / (alto - fuga.y)
                drawLine(sombra(oscuro, 0.3f), Offset(x(y0), y0), Offset(x(y), y), 1f * u)
            }
            y0 = y
        }
        pinceladas(suelo, if (oscuro) NOCHE else SOMBRA_MUEBLE, 1011, 20, u, if (oscuro) 0.1f else 0.08f)
        // El musgo de las juntas.
        for (i in 0 until 30) {
            val x = w * azar(1012, i); val y = juntas[i % 3]
            drawOval(Tono(oscuro, if (oscuro) luzFarol(Offset(x / w, y / w)) else 0f).c(if (i % 2 == 0) MUSGO else HELECHO).copy(alpha = 0.6f), Offset(x, y - w * 0.002f), Size(w * (0.01f + 0.02f * azar(1013, i)), w * 0.005f))
        }
        // La sombra de la estantería en las losas, corrida a la derecha.
        drawPath(poligono(w * 0.02f, pie - 1f * u, w * 0.98f, pie - 1f * u, w, pie + hb * 0.12f, w * 0.06f, pie + hb * 0.12f), sombra(oscuro, if (oscuro) 0.45f else 0.28f))
        drawRect(sombra(oscuro, if (oscuro) 0.5f else 0.3f), Offset(w * 0.02f, rincon), Size(w * 0.96f, pie - rincon))

        // ---- la estantería: baldas, postes, tornillos, el alambre de una unión y el ladrillo que calza la pata
        val ladrillo = Rect(-w * 0.004f, pie - w * 0.03f, w * 0.075f, pie + w * 0.004f)
        niveles.forEachIndexed { i, y -> balda(y, i, w, oscuro) }
        POSTES.indices.forEach { i -> poste(i, w * 0.475f, if (i == 0) ladrillo.top + w * 0.004f else pie, w, oscuro) }
        // Las cabezas cortadas de los postes, con su óxido.
        POSTES.forEach { p ->
            val tt = tk(p[1] * w, w * 0.475f)
            drawRect(tt.c(OXIDO), Offset(p[0] * w, w * 0.475f), Size((p[2] - p[0]) * w, w * 0.004f))
            drawLine(tt.b(0.5f, 0.3f), Offset(p[0] * w, w * 0.475f), Offset(p[1] * w, w * 0.475f), 1f * u)
        }
        niveles.forEach { y ->
            val ya = y + w * (CARA + 0.024f) + w * ANGULO / 2
            POSTES.forEach { p -> tornillo(Offset((p[0] + p[1]) / 2 * w, ya), w, tk(p[1] * w, ya)) }
        }
        // El alambre de atar en la unión del poste del medio con la tercera balda.
        if (niveles.size > 3) {
            val ya = niveles[3] + w * (CARA + 0.03f)
            val tt = tk(0.5f * w, ya)
            for (k in 0 until 6) {
                val x0 = w * (POSTES[1][0] - 0.006f); val x1 = w * (POSTES[1][2] + 0.006f)
                val yy = ya - w * 0.014f + k * w * 0.005f
                drawLine(tt.s(0.35f), Offset(x0, yy + w * 0.004f + 1f * u), Offset(x1, yy + 1f * u), 1.4f * u)
                drawLine(tt.c(mezcla(HOJALATA_OSC, OXIDO, 0.35f)), Offset(x0, yy + w * 0.004f), Offset(x1, yy), 1.1f * u)
            }
            drawLine(tt.c(HOJALATA_OSC), Offset(w * (POSTES[1][2] + 0.006f), ya + w * 0.012f), Offset(w * (POSTES[1][2] + 0.02f), ya + w * 0.024f), 1.1f * u, StrokeCap.Round)
        }
        // El ladrillo bajo la pata de la izquierda, con su mortero viejo y su musgo.
        run {
            val tt = tk(ladrillo.center.x, ladrillo.center.y)
            drawRect(tt.s(0.4f), Offset(ladrillo.left + w * 0.01f, ladrillo.bottom - w * 0.004f), Size(ladrillo.width, w * 0.01f))
            drawRect(tt.c(LADRILLO), ladrillo.topLeft, ladrillo.size)
            drawRect(tt.c(mezcla(LADRILLO, LUZ_MUEBLE, 0.25f)), ladrillo.topLeft, Size(ladrillo.width, w * 0.006f))
            drawRect(tt.s(0.3f), Offset(ladrillo.right - w * 0.012f, ladrillo.top), Size(w * 0.012f, ladrillo.height))
            for (i in 0 until 6) drawCircle(tt.c(if (i % 2 == 0) HORMIGON_CALIDO else mezcla(LADRILLO, HOLLIN, 0.3f)), w * (0.002f + 0.003f * azar(1021, i)), Offset(ladrillo.left + ladrillo.width * azar(1022, i), ladrillo.top + ladrillo.height * azar(1023, i)))
            drawOval(tt.c(MUSGO), Offset(ladrillo.left, ladrillo.bottom - w * 0.008f), Size(ladrillo.width * 0.5f, w * 0.008f))
        }
        // Las patas de los otros postes pisan las losas.
        POSTES.drop(1).forEach { p -> drawOval(sombra(oscuro, 0.4f), Offset(p[0] * w, pie - w * 0.003f), Size((p[2] - p[0]) * w + w * 0.02f, w * 0.008f)) }

        // ---- lo de encima, de atrás adelante
        withTransform({ scale(escRadio, escRadio, pivRadio) }) {
            libros(pieArriba, w, oscuro)
            radio(pieArriba, w, oscuro)
        }
        // La antena, por delante de los libros: el borne en la radio y el alambre hasta el clavo.
        run {
            val tt = tk(w * 0.25f, w * 0.25f)
            drawPath(antena, tt.c(mezcla(OXIDO_VIVO, HOJALATA, 0.3f)), style = Stroke(1.2f * u))
            drawCircle(tt.c(HOLLIN), w * 0.004f, bornes)
            clavo(clavoA, tt)
            drawPath(Path().apply { moveTo(clavoA.x, clavoA.y); cubicTo(clavoA.x + w * 0.01f, clavoA.y + w * 0.01f, clavoA.x + w * 0.012f, clavoA.y + w * 0.03f, clavoA.x + w * 0.004f, clavoA.y + w * 0.035f) }, tt.c(mezcla(OXIDO_VIVO, HOJALATA, 0.3f)), style = Stroke(1.1f * u))
        }
        withTransform({ scale(escTarro, escTarro, pivTarro) }) {
            tarro(pieArriba, w, oscuro)
            taza(pieArriba, w, oscuro)
        }
        withTransform({ scale(1.5f, 1.5f, Offset(w * FAROL.x, pieArriba)) }) { farol(pieArriba, w, oscuro) }

        // ---- la hiedra que sube por el poste de la derecha y se derrama por la balda de arriba
        val tonoEn: (Offset) -> Tono = { p -> Tono(oscuro, if (oscuro) luzFarol(p / w) else 0f) }
        val subida = List(18) { i ->
            val f = i / 17f
            Offset(w * (0.968f + 0.022f * sin(f * 19f)), pie - (pie - w * 0.49f) * f)
        }
        tallo(subida, w * 0.034f, 1031, tonoEn, grosor = 1.8f)
        tallo(listOf(Offset(w * 0.968f, w * 0.5f), Offset(w * 0.93f, w * 0.502f), Offset(w * 0.9f, w * 0.515f), Offset(w * 0.885f, w * 0.545f), Offset(w * 0.88f, w * 0.585f), Offset(w * 0.886f, w * 0.62f)), w * 0.03f, 1032, tonoEn)
        tallo(listOf(Offset(w * 0.975f, w * 0.49f), Offset(w * 0.99f, w * 0.44f), Offset(w * 1.0f, w * 0.4f)), w * 0.03f, 1033, tonoEn)
        // La enredadera con flores: sube por el poste de la izquierda y sigue por una balda.
        val nivelFlor = niveles.getOrNull(3) ?: niveles.last()
        val enre = List(14) { i ->
            val f = i / 13f
            Offset(w * (0.033f + 0.016f * sin(f * 15f)), ladrillo.top - (ladrillo.top - nivelFlor - w * 0.01f) * f)
        } + listOf(Offset(w * 0.07f, nivelFlor + w * 0.025f), Offset(w * 0.12f, nivelFlor + w * 0.032f), Offset(w * 0.17f, nivelFlor + w * 0.03f), Offset(w * 0.22f, nivelFlor + w * 0.04f), Offset(w * 0.25f, nivelFlor + w * 0.058f))
        tallo(enre, w * 0.028f, 1041, tonoEn, grosor = 1.1f)
        listOf(3, 7, 11, 15, 17).forEach { i ->
            val p = enre[i] + Offset(w * 0.008f, -w * 0.004f)
            val tt = tonoEn(p)
            // Una flor de trompeta: el cáliz, la corola de cinco puntas y su garganta.
            drawPath(estrella(p, w * 0.013f, 5, 0.72f, -90f + 13f * i), tt.s(0.25f).copy(alpha = 0.2f))
            drawPath(estrella(p - Offset(w * 0.002f, w * 0.002f), w * 0.013f, 5, 0.72f, -90f + 13f * i), tt.c(FLOR))
            drawCircle(tt.c(mezcla(FLOR, PAJA, 0.6f)), w * 0.004f, p - Offset(w * 0.002f, w * 0.002f))
            drawCircle(tt.b(0.4f, 0.3f), w * 0.004f, p - Offset(w * 0.006f, w * 0.007f))
        }

        // ---- el pie: la losa levantada por las raíces y su helecho, las setas, la regadera y la caja
        run {
            val tt = tk(w * 0.58f, pie + hb * 0.3f)
            fun q(x: Float, y: Float) = Offset(w * x, pie + hb * y)
            fun pol(vararg o: Float) = poligono(*o)
            // El hueco que dejó la losa: tierra oscura, con migas.
            drawPath(pol(w * 0.46f, pie + hb * 0.25f, w * 0.7f, pie + hb * 0.2f, w * 0.72f, pie + hb * 0.41f, w * 0.48f, pie + hb * 0.46f), tt.c(mezcla(TIERRA, HOLLIN, 0.35f)))
            for (i in 0 until 12) drawCircle(tt.c(mezcla(TIERRA, PAJA, 0.2f)), w * (0.002f + 0.003f * azar(1061, i)), q(0.42f + 0.1f * azar(1062, i), 0.3f + 0.25f * azar(1063, i)))
            // Las raíces: salen por debajo del lado alzado y se arrastran por las losas.
            listOf(Triple(q(0.47f, 0.34f), q(0.33f, 0.52f), 0.011f), Triple(q(0.48f, 0.4f), q(0.4f, 0.74f), 0.008f), Triple(q(0.6f, 0.43f), q(0.66f, 0.7f), 0.006f)).forEach { (a0, b0, g) ->
                val pr = Path().apply { moveTo(a0.x, a0.y); cubicTo(a0.x - w * 0.03f, a0.y + hb * 0.02f, b0.x + w * 0.05f, b0.y - hb * 0.12f, b0.x, b0.y) }
                translate(w * 0.004f, hb * 0.02f) { drawPath(pr, tt.s(0.3f), style = Stroke(w * g, cap = StrokeCap.Round)) }
                drawPath(pr, tt.c(mezcla(TIERRA, MADERA, 0.35f)), style = Stroke(w * g, cap = StrokeCap.Round))
                translate(-0.6f * u, -1f * u) { drawPath(pr, tt.b(0.35f, 0.2f), style = Stroke(1f * u)) }
                drawLine(tt.c(mezcla(TIERRA, MADERA, 0.35f)), b0, b0 + Offset(-w * 0.025f, hb * 0.04f), 1f * u, StrokeCap.Round)
            }
            // La losa, alzada por la izquierda y apoyada por la derecha: su cara de arriba, su canto y su testa.
            drawPath(pol(w * 0.46f, pie + hb * 0.31f, w * 0.73f, pie + hb * 0.42f, w * 0.73f, pie + hb * 0.47f, w * 0.46f, pie + hb * 0.38f), tt.c(mezcla(LOSA, SOMBRA_MUEBLE, 0.4f)))
            drawPath(pol(w * 0.44f, pie + hb * 0.11f, w * 0.46f, pie + hb * 0.31f, w * 0.46f, pie + hb * 0.38f, w * 0.44f, pie + hb * 0.18f), tt.c(mezcla(LOSA, SOMBRA_MUEBLE, 0.25f)))
            val cara = pol(w * 0.44f, pie + hb * 0.11f, w * 0.71f, pie + hb * 0.21f, w * 0.73f, pie + hb * 0.42f, w * 0.46f, pie + hb * 0.31f)
            drawPath(cara, tt.c(mezcla(LOSA, LUZ_MUEBLE, 0.22f)))
            clipPath(cara) {
                for (i in 0 until 6) drawOval(tt.c(if (i % 2 == 0) MUSGO else HORMIGON_FRIO).copy(alpha = 0.35f), q(0.46f + 0.24f * azar(1064, i), 0.15f + 0.2f * azar(1065, i)), Size(w * 0.03f, w * 0.01f))
                drawLine(tt.c(HOLLIN), q(0.55f, 0.15f), q(0.6f, 0.36f), 1.2f * u)
            }
            drawLine(tt.b(0.55f, 0.3f), q(0.44f, 0.11f), q(0.71f, 0.21f), 1.4f * u)
            drawLine(tt.b(0.3f, 0.2f), q(0.44f, 0.11f), q(0.46f, 0.31f), 1f * u)
            helecho(q(0.47f, 0.27f), w * 0.11f, 6, 1051, w, oscuro)
        }
        withTransform({ scale(1.6f, 1.6f, Offset(w * 0.11f, pie + hb * 0.06f)) }) { setas(Offset(w * 0.11f, pie + hb * 0.06f), w, oscuro) }
        helecho(Offset(w * 0.02f, alto - hb * 0.08f), w * 0.13f, 5, 1052, w, oscuro)
        withTransform({ scale(1.3f, 1.3f, Offset(w * 0.25f, alto - hb * 0.1f)) }) { regadera(Offset(w * 0.25f, alto - hb * 0.1f), w, oscuro) }
        caja(Rect(w * 0.79f, pie + hb * 0.12f, w * 1.02f, alto - hb * 0.06f), w, oscuro)

        // ---- de noche: lo lejano se hunde, arde el farol y unas luciérnagas junto a la hiedra
        if (oscuro) {
            val c = Offset(w * FAROL.x, w * FAROL.y)
            drawRect(Brush.radialGradient(listOf(Color.Transparent, NOCHE.copy(alpha = 0.22f), NOCHE.copy(alpha = 0.42f)), c, w * 1.9f), Offset.Zero, size)
            mancha(c, w * 0.12f, LLAMA.copy(alpha = 0.35f))
            listOf(Offset(0.935f, 0.7f), Offset(0.99f, 0.95f), Offset(0.925f, 1.32f), Offset(0.065f, 1.2f), Offset(0.03f, 1.55f), Offset(0.97f, 1.75f)).forEachIndexed { i, f ->
                val p = f * w
                if (p.y < alto - w * 0.05f) {
                    drawCircle(LUCIERNAGA.copy(alpha = 0.08f), w * 0.016f, p)
                    drawCircle(LUCIERNAGA.copy(alpha = 0.16f), w * 0.008f, p)
                    drawCircle(LUCIERNAGA, w * (0.0022f + 0.001f * (i % 2)), p)
                }
            }
        }

        // ---- la placa de «y N más»: una tira de cinta en el canto de la balda del pie
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            val tt = tk(r.center.x, r.center.y)
            val cinta = contornoIrregular(r, 37, 24, 0.03f)
            translate(1.2f * u, 1.6f * u) { drawPath(cinta, tt.s(0.35f)) }
            drawPath(cinta, if (oscuro) mezcla(CINTA_NOCHE, CINTA_FAROL, tt.k * 1.3f) else CINTA)
        }
        granoMueble(Rect(0f, 0f, w, alto), oscuro)
    }

    // ---------------------------------------------------------------- los compartimentos

    /**
     * Tapa el aura en reposo de `Modifier.halo()` alrededor de una silueta, de noche (como en Helénico): `halo()` calca todo lo
     * que pinta el compartimento y lo corre unos 2.5 dp; sobre el muro oscuro eso es una franja clara que se lee como un recorte.
     * Se pinta en una capa propia con `SrcAtop`: en la capa del aura no agrega nada, y en el dibujo normal oscurece la franja.
     */
    private fun DrawScope.cubreAura(oscuro: Boolean, rellenos: List<Path>, trazos: List<Pair<Path, Float>> = emptyList()) {
        if (!oscuro) return
        val ancho = 3.1f * density; val junto = 1.8f * density
        drawIntoCanvas { it.saveLayer(Rect(-ancho * 4, -ancho * 4, size.width + ancho * 4, size.height + ancho * 4), Paint().apply { blendMode = BlendMode.SrcAtop }) }
        for ((color, a) in listOf(NOCHE.copy(alpha = 0.42f) to ancho, NOCHE.copy(alpha = 0.6f) to junto)) {
            rellenos.forEach { drawPath(it, color, style = Stroke(a * 2, join = StrokeJoin.Round), blendMode = BlendMode.Src) }
            trazos.forEach { (tr, g) -> drawPath(tr, color, style = Stroke(g + a * 2, cap = StrokeCap.Round, join = StrokeJoin.Round), blendMode = BlendMode.Src) }
        }
        drawIntoCanvas { it.restore() }
    }

    /** La sombra de lo que va en un compartimento, en la balda y en el muro: con `SrcAtop`, cae en lo ya pintado y no en el aura. */
    private fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, t: Tono) =
        drawOval(t.s(if (t.oscuro) 0.5f else 0.36f), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto), blendMode = BlendMode.SrcAtop)

    private fun DrawScope.sombraDetras(sil: Path, t: Tono, hasta: Float) {
        val w = size.width
        clipRect(-w, -w, w * 2, hasta) {
            if (!t.oscuro) translate(w * 0.06f, w * 0.018f) { drawPath(sil, sombraMueble(0.16f), blendMode = BlendMode.SrcAtop) }
            else if (t.k > 0.1f) translate(-w * 0.07f, w * 0.03f) { drawPath(sil, NOCHE.copy(alpha = (0.6f * t.k).coerceAtMost(0.5f)), blendMode = BlendMode.SrcAtop) }
        }
    }

    /** La cinta de enmascarar del rótulo en [r], con los extremos rasgados; [tachada]: la del bote sin tema, con su raya. */
    private fun DrawScope.cinta(r: Rect, t: Tono, semilla: Int, tachada: Boolean) {
        val u = density
        val p = Path().apply {
            moveTo(r.left + 2f * u, r.top)
            lineTo(r.right - 1f * u, r.top + 0.5f * u)
            for (k in 1..5) lineTo(r.right + (if (k % 2 == 0) -2.2f else 0.6f) * u * (0.5f + azar(semilla, k)), r.top + r.height * k / 6f)
            lineTo(r.right - 1.5f * u, r.bottom)
            lineTo(r.left + 1f * u, r.bottom - 0.5f * u)
            for (k in 5 downTo 1) lineTo(r.left + (if (k % 2 == 0) 2.4f else -0.4f) * u * (0.5f + azar(semilla, k + 10)), r.top + r.height * k / 6f)
            close()
        }
        translate(1f * u, 1.4f * u) { drawPath(p, t.s(0.3f)) }
        drawPath(p, if (t.oscuro) mezcla(CINTA_NOCHE, CINTA_FAROL, t.k * 1.3f) else mezcla(CINTA, LUZ_MUEBLE, t.dia))
        clipPath(p) {
            // El papel crepé: unas arrugas a lo largo, y el borde de arriba un poco levantado.
            for (k in 0 until 3) drawLine(t.s(0.08f), Offset(r.left, r.top + r.height * (0.25f + 0.25f * k)), Offset(r.right, r.top + r.height * (0.27f + 0.25f * k)), 0.7f * u)
            drawRect(t.b(0.35f, 0.2f), r.topLeft, Size(r.width, 1f * u))
            drawRect(t.s(0.12f), Offset(r.left, r.bottom - 1.6f * u), Size(r.width, 1.6f * u))
        }
        // El bote sin tema: la cinta tachada con una raya de rotulador a la izquierda, donde no tapa el texto.
        if (tachada) drawLine(TINTA.copy(alpha = 0.7f), Offset(r.left + r.width * 0.04f, r.top + r.height * 0.55f), Offset(r.left + r.width * 0.12f, r.top + r.height * 0.45f), 1.6f * u, StrokeCap.Round)
    }

    /** Una nota enrollada de pie, de [ancho] y [alto] desde [pie], ladeada [giro] grados; asoma de un recipiente. */
    private fun DrawScope.notaEnrollada(x: Float, pie: Float, ancho: Float, alto: Float, giro: Float, t: Tono, semilla: Int): Path {
        val u = density
        val silueta = girar(Path().apply { addRect(Rect(x, pie - alto, x + ancho, pie)); addOval(Rect(x, pie - alto - ancho * 0.22f, x + ancho, pie - alto + ancho * 0.22f)) }, giro, Offset(x + ancho / 2, pie))
        rotate(giro, Offset(x + ancho / 2, pie)) {
            val r = Rect(x, pie - alto, x + ancho, pie)
            drawRect(t.c(if (semilla % 3 == 0) mezcla(PAPEL, PAJA, 0.35f) else PAPEL), r.topLeft, r.size)
            drawRect(if (t.luzIzq) t.s(0.22f) else t.s(0.28f), Offset(if (t.luzIzq) r.right - ancho * 0.35f else r.left, r.top), Size(ancho * 0.35f, r.height))
            drawRect(t.b(0.35f, 0.3f), Offset(if (t.luzIzq) r.left + ancho * 0.15f else r.right - ancho * 0.3f, r.top), Size(ancho * 0.15f, r.height))
            // Lo escrito que se ve por fuera: unas rayas de tinta.
            for (k in 0 until 3) drawLine(t.c(TINTA).copy(alpha = 0.35f), Offset(r.left + ancho * 0.2f, r.top + alto * (0.2f + 0.12f * k)), Offset(r.right - ancho * 0.2f, r.top + alto * (0.22f + 0.12f * k)), 0.6f * u)
            // La boca del rollo: la espiral del papel.
            drawOval(t.c(mezcla(PAPEL, HOJALATA_OSC, 0.4f)), Offset(r.left, r.top - ancho * 0.22f), Size(ancho, ancho * 0.44f))
            drawArc(t.c(mezcla(PAPEL, TINTA, 0.25f)), 0f, 270f, false, Offset(r.left + ancho * 0.25f, r.top - ancho * 0.11f), Size(ancho * 0.5f, ancho * 0.22f), style = Stroke(0.7f * u))
            if (semilla % 4 == 1) drawRect(t.c(TELA), Offset(r.left, r.top + alto * 0.55f), Size(ancho, 1.4f * u))
        }
        return silueta
    }

    /** Una ramita de hiedra que trepa por un recipiente desde [b], [alto] arriba. */
    private fun DrawScope.ramita(b: Offset, alto: Float, t: Tono, semilla: Int) {
        val w = size.width
        val pts = List(6) { i -> Offset(b.x + w * 0.02f * sin(i * 1.7f + semilla), b.y - alto * i / 5f) }
        tallo(pts, w * 0.06f, semilla, { t }, grosor = 1f, sombraFuera = false)
    }

    /** La lata de conserva, con su abrefácil levantado y las notas enrolladas asomando. */
    private fun DrawScope.lata(c: EstadoCajon, pie: Float, ab: Float, t: Tono) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(w * 0.12f, h * 0.3f, w * 0.88f, pie)
        val elip = w * 0.05f
        val boca = Rect(r.left, r.top - elip, r.right, r.top + elip)
        val sil = Path().apply { addRect(Rect(r.left, r.top, r.right, r.bottom)); addOval(boca) }
        sombraDetras(sil, t, pie - w * 0.03f)
        contacto(Offset(r.center.x + w * 0.02f, pie), r.width * 1.08f, w * 0.05f, t)
        // La tapa levantada, de pie detrás de la boca, sujeta por la bisagra de atrás.
        val giroTapa = 14f - 24f * ab
        rotate(giroTapa, Offset(r.right - w * 0.14f, r.top - elip * 0.3f)) {
            val tr = Rect(r.right - w * 0.5f, r.top - elip * 0.3f - w * 0.2f, r.right - w * 0.1f, r.top - elip * 0.3f)
            drawOval(t.c(HOJALATA), tr.topLeft, tr.size)
            drawOval(t.c(HOJALATA_OSC), Offset(tr.left + tr.width * 0.08f, tr.top + tr.height * 0.08f), Size(tr.width * 0.84f, tr.height * 0.84f), style = Stroke(1.2f * u))
            drawOval(if (t.luzIzq) t.s(0.2f) else t.b(0.4f, 0.2f), Offset(tr.left + tr.width * 0.5f, tr.top), Size(tr.width * 0.5f, tr.height))
            drawOval(t.c(HOJALATA_OSC), Offset(tr.center.x - w * 0.03f, tr.bottom - w * 0.07f), Size(w * 0.06f, w * 0.04f), style = Stroke(1.6f * u))
            drawLine(t.c(mezcla(HOJALATA, LUZ_MUEBLE, 0.3f)), Offset(tr.left + tr.width * 0.15f, tr.center.y), Offset(tr.left + tr.width * 0.3f, tr.top + tr.height * 0.25f), 0.8f * u)
            drawOval(t.c(HOJALATA_OSC), tr.topLeft, tr.size, style = Stroke(1f * u))
        }
        val tapaSil = girar(Path().apply { addOval(Rect(r.right - w * 0.5f, r.top - elip * 0.3f - w * 0.2f, r.right - w * 0.1f, r.top - elip * 0.3f)) }, giroTapa, Offset(r.right - w * 0.14f, r.top - elip * 0.3f))
        val contorno = mutableListOf(sil, tapaSil)
        // El fondo de la boca, oscuro.
        drawOval(t.c(mezcla(HOLLIN, OXIDO_OSC, 0.3f)), boca.topLeft, boca.size)
        // Las notas enrolladas: más cuantas más hay; al abrir, suben.
        val m = c.n.coerceIn(1, 8)
        for (i in 0 until m) {
            val f = (i + 0.5f) / m
            val an = w * (0.075f + 0.02f * azar(c.semilla, i + 5))
            val x = r.left + w * 0.04f + (r.width - w * 0.08f - an) * f + (azar(c.semilla, i + 7) - 0.5f) * w * 0.02f
            val alto = h * (0.1f + 0.012f * c.n.coerceAtMost(8) + 0.1f * azar(c.semilla, i)) + ab * h * 0.12f
            contorno += notaEnrollada(x, r.top + elip * 0.6f, an, alto + elip * 0.6f, (azar(c.semilla, i + 9) - 0.5f) * 16f, t, c.semilla + i)
        }
        // El cuerpo: hojalata con sus nervios, su óxido y el reborde de arriba por delante.
        val cuerpo = Path().apply { moveTo(r.left, r.top); arcTo(boca, 180f, -180f, false); lineTo(r.right, r.bottom - elip); arcTo(Rect(r.left, r.bottom - 2 * elip, r.right, r.bottom), 0f, 180f, false); close() }
        drawPath(cuerpo, t.c(HOJALATA))
        clipPath(cuerpo) {
            var y = r.top + h * 0.12f
            while (y < r.bottom - elip * 1.5f) {
                drawArc(t.s(0.3f), 0f, 180f, false, Offset(r.left, y - elip), Size(r.width, elip * 2), style = Stroke(1.2f * u))
                drawArc(t.b(0.4f, 0.2f), 0f, 180f, false, Offset(r.left, y - elip + 1.6f * u), Size(r.width, elip * 2), style = Stroke(0.8f * u))
                y += h * 0.1f
            }
            for (k in 0 until 7) {
                val p = Offset(r.left + r.width * azar(c.semilla, k + 30), r.bottom - r.height * 0.45f * azar(c.semilla, k + 40) * azar(c.semilla, k + 45))
                val q = w * (0.03f + 0.07f * azar(c.semilla, k + 50))
                drawOval(t.c(if (k % 2 == 0) OXIDO else OXIDO_VIVO).copy(alpha = 0.45f + 0.3f * azar(c.semilla, k + 60)), Offset(p.x - q, p.y - q * 0.4f), Size(q * 2, q * 0.8f))
            }
            drawRect(t.c(OXIDO_OSC).copy(alpha = 0.5f), Offset(r.left, r.bottom - elip * 1.6f), Size(r.width, elip * 1.6f))
            sombraCilindro(r, t)
        }
        drawArc(t.c(HOJALATA_OSC), 0f, 180f, false, boca.topLeft, boca.size, style = Stroke(2.4f * u))
        drawArc(t.b(0.5f, 0.3f), 20f, 120f, false, Offset(boca.left, boca.top + 1.2f * u), boca.size, style = Stroke(1f * u))
        drawArc(t.c(HOJALATA_OSC), 180f, 180f, false, boca.topLeft, boca.size, style = Stroke(1.4f * u))
        cubreAura(t.oscuro, contorno)
    }

    /** [p] girado [ang] grados alrededor de [piv] (para tapar el aura de lo que se dibujó girado). */
    private fun girar(p: Path, ang: Float, piv: Offset): Path = Path().apply {
        addPath(p)
        transform(Matrix().apply { translate(piv.x, piv.y); rotateZ(ang); translate(-piv.x, -piv.y) })
    }

    /** La sombra de un cilindro: el lado contrario a la luz más hondo, y una franja de brillo del lado de la luz. */
    private fun DrawScope.sombraCilindro(r: Rect, t: Tono) {
        val sw = r.width
        if (t.luzIzq) {
            drawRect(t.s(0.2f), Offset(r.right - sw * 0.32f, r.top), Size(sw * 0.32f, r.height))
            drawRect(t.s(0.18f), Offset(r.right - sw * 0.12f, r.top), Size(sw * 0.12f, r.height))
            drawRect(t.b(0.22f), Offset(r.left + sw * 0.08f, r.top), Size(sw * 0.14f, r.height))
            drawRect(t.b(0.45f), Offset(r.left + sw * 0.13f, r.top), Size(sw * 0.035f, r.height))
        } else {
            drawRect(t.s(0.3f), Offset(r.left, r.top), Size(sw * 0.4f, r.height))
            drawRect(t.s(0.25f), Offset(r.left, r.top), Size(sw * 0.15f, r.height))
            drawRect(t.b(0.15f), Offset(r.right - sw * 0.24f, r.top), Size(sw * 0.14f, r.height))
            drawRect(t.b(0.5f), Offset(r.right - sw * 0.17f, r.top), Size(sw * 0.035f, r.height))
        }
    }

    /** El frasco de vidrio con tapa de tela atada: las notas se ven dentro. */
    private fun DrawScope.frasco(c: EstadoCajon, pie: Float, ab: Float, t: Tono) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(w * 0.1f, h * 0.36f, w * 0.9f, pie)
        val cuello = Rect(w * 0.24f, h * 0.22f, w * 0.76f, h * 0.38f)
        val vidrio = Path().apply {
            moveTo(cuello.left, cuello.top); lineTo(cuello.left, cuello.bottom - h * 0.03f)
            cubicTo(r.left, cuello.bottom, r.left, r.top, r.left, r.top + h * 0.06f)
            lineTo(r.left, r.bottom - w * 0.05f)
            cubicTo(r.left, r.bottom, r.left + w * 0.04f, r.bottom, r.left + w * 0.08f, r.bottom)
            lineTo(r.right - w * 0.08f, r.bottom)
            cubicTo(r.right - w * 0.04f, r.bottom, r.right, r.bottom, r.right, r.bottom - w * 0.05f)
            lineTo(r.right, r.top + h * 0.06f)
            cubicTo(r.right, r.top, r.right, cuello.bottom, cuello.right, cuello.bottom - h * 0.03f)
            lineTo(cuello.right, cuello.top); close()
        }
        sombraDetras(vidrio, t, pie - w * 0.03f)
        contacto(Offset(r.center.x + w * 0.02f, pie), r.width * 1.06f, w * 0.05f, t)
        // Por dentro: las notas enrolladas de pie, apoyadas unas en otras.
        val m = c.n.coerceIn(1, 9)
        clipPath(vidrio) {
            drawPath(vidrio, t.c(AGUA).copy(alpha = 0.16f))
            for (i in 0 until m) {
                val f = (i + 0.5f) / m
                val an = w * (0.07f + 0.02f * azar(c.semilla, i + 5))
                val x = r.left + w * 0.05f + (r.width - w * 0.1f - an) * f
                val alto = h * (0.24f + 0.02f * c.n.coerceAtMost(9) + 0.12f * azar(c.semilla, i)) + ab * h * 0.08f
                notaEnrollada(x, pie - w * 0.02f, an, alto, (azar(c.semilla, i + 9) - 0.5f) * 22f, t, c.semilla + i)
            }
            // El vidrio por encima: verdoso, más hondo en los bordes y en el culo grueso.
            drawPath(vidrio, t.c(mezcla(AGUA, HOJALATA, 0.3f)).copy(alpha = 0.28f))
            drawRect(t.c(AGUA).copy(alpha = 0.35f), Offset(r.left, r.top), Size(w * 0.05f, r.height))
            drawRect(t.c(AGUA).copy(alpha = 0.35f), Offset(r.right - w * 0.05f, r.top), Size(w * 0.05f, r.height))
            drawRect(t.c(mezcla(AGUA, HIEDRA, 0.4f)).copy(alpha = 0.5f), Offset(r.left, r.bottom - w * 0.035f), Size(r.width, w * 0.035f))
            if (!t.luzIzq) drawRect(NOCHE.copy(alpha = 0.3f), Offset(r.left, r.top), Size(r.width * 0.35f, r.height))
        }
        // Los brillos del vidrio: una franja ancha y otra fina, del lado de la luz; y el reflejo del hombro.
        val bx = if (t.luzIzq) r.left + w * 0.07f else r.right - w * 0.12f
        val brilloV = if (t.oscuro) LLAMA.copy(alpha = (0.7f * t.k + 0.06f).coerceAtMost(0.75f)) else luzMueble(0.6f)
        drawRoundRect(brilloV, Offset(bx, r.top + h * 0.08f), Size(w * 0.04f, r.height * 0.62f), CornerRadius(w * 0.02f))
        drawRoundRect(brilloV.copy(alpha = brilloV.alpha * 0.5f), Offset(bx + (if (t.luzIzq) w * 0.06f else -w * 0.04f), r.top + h * 0.1f), Size(w * 0.014f, r.height * 0.4f), CornerRadius(w * 0.007f))
        drawArc(brilloV, if (t.luzIzq) 190f else 290f, 60f, false, Offset(r.left + w * 0.04f, r.top - h * 0.02f), Size(r.width - w * 0.08f, h * 0.2f), style = Stroke(1.6f * u))
        drawPath(vidrio, t.c(mezcla(AGUA, HOLLIN, 0.45f)).copy(alpha = 0.7f), style = Stroke(1.2f * u))
        // La tapa de tela: el paño abombado sobre la boca, con su falda plegada y el cordel atado al cuello.
        val sube = ab * h * 0.12f
        var faldaSil = Path()
        translate(0f, -sube) {
            val falda = Path().apply {
                moveTo(cuello.left - w * 0.06f, cuello.top + h * 0.02f)
                cubicTo(cuello.left - w * 0.02f, cuello.top - h * 0.1f, cuello.right + w * 0.02f, cuello.top - h * 0.1f, cuello.right + w * 0.06f, cuello.top + h * 0.02f)
                lineTo(cuello.right + w * 0.09f, cuello.top + h * 0.13f)
                var x = cuello.right + w * 0.09f
                var k = 0
                while (x > cuello.left - w * 0.09f) {
                    val x2 = x - w * 0.045f
                    cubicTo(x - w * 0.01f, cuello.top + h * (0.15f + 0.03f * (k % 2)), x2 + w * 0.01f, cuello.top + h * (0.15f + 0.03f * (k % 2)), x2, cuello.top + h * (0.12f + 0.02f * azar(c.semilla, k)))
                    x = x2; k++
                }
                close()
            }
            faldaSil = Path().apply { addPath(falda, Offset(0f, -sube)) }
            translate(w * 0.01f, h * 0.015f) { drawPath(falda, t.s(0.3f)) }
            drawPath(falda, t.c(TELA))
            clipPath(falda) {
                // Los cuadros desvaídos del paño, los pliegues y la luz en la copa.
                for (k in 0 until 9) drawRect(t.c(mezcla(TELA, PAPEL, 0.35f)).copy(alpha = 0.5f), Offset(cuello.left - w * 0.1f + k * w * 0.08f, cuello.top - h * 0.1f), Size(w * 0.025f, h * 0.3f))
                drawRect(t.c(mezcla(TELA, PAPEL, 0.35f)).copy(alpha = 0.35f), Offset(cuello.left - w * 0.1f, cuello.top + h * 0.04f), Size(cuello.width + w * 0.2f, h * 0.02f))
                for (k in 0 until 6) {
                    val x = cuello.left - w * 0.06f + k * w * 0.11f
                    drawLine(t.s(0.3f), Offset(x + w * 0.02f, cuello.top + h * 0.05f), Offset(x, cuello.top + h * 0.18f), 1.6f * u)
                }
                drawOval(t.b(0.3f, 0.2f), Offset(cuello.left + (if (t.luzIzq) 0f else cuello.width * 0.5f), cuello.top - h * 0.08f), Size(cuello.width * 0.5f, h * 0.08f))
                drawRect(if (t.luzIzq) t.s(0.25f) else t.s(0.3f), Offset(if (t.luzIzq) cuello.right else cuello.left - w * 0.1f, cuello.top - h * 0.1f), Size(w * 0.1f, h * 0.3f))
            }
            // El cordel que la ata, con su lazo.
            val yc = cuello.top + h * 0.075f
            drawLine(t.c(PAJA), Offset(cuello.left - w * 0.065f, yc), Offset(cuello.right + w * 0.065f, yc + h * 0.006f), 1.8f * u)
            val nudo = Offset(cuello.left + cuello.width * 0.3f, yc + h * 0.002f)
            drawOval(t.c(PAJA), Offset(nudo.x - w * 0.05f, nudo.y - h * 0.03f), Size(w * 0.05f, h * 0.035f), style = Stroke(1.4f * u))
            drawOval(t.c(PAJA), Offset(nudo.x, nudo.y - h * 0.028f), Size(w * 0.045f, h * 0.032f), style = Stroke(1.4f * u))
            drawLine(t.c(PAJA), nudo, nudo + Offset(-w * 0.03f, h * 0.08f), 1.3f * u, StrokeCap.Round)
            drawLine(t.c(PAJA), nudo, nudo + Offset(w * 0.01f, h * 0.09f), 1.3f * u, StrokeCap.Round)
            drawCircle(t.c(mezcla(PAJA, TIERRA, 0.3f)), 2f * u, nudo)
        }
        cubreAura(t.oscuro, listOf(vidrio, faldaSil))
    }

    /** La caja de galletas abollada, redonda y baja, con la tapa entreabierta y las notas asomando por la rendija. */
    private fun DrawScope.galletas(c: EstadoCajon, pie: Float, ab: Float, t: Tono) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(w * 0.07f, h * 0.44f, w * 0.93f, pie)
        val elip = w * 0.06f
        val pintura = mezcla(AGUA, HIEDRA, 0.25f)
        val sil = Path().apply { addRect(r); addOval(Rect(r.left, r.top - elip, r.right, r.top + elip)) }
        sombraDetras(sil, t, pie - w * 0.03f)
        contacto(Offset(r.center.x + w * 0.02f, pie), r.width * 1.06f, w * 0.05f, t)
        // Las notas, planas y abiertas en abanico por la rendija: más y más altas cuantas más hay.
        val m = c.n.coerceIn(1, 9)
        val alza = h * (0.05f + 0.006f * m) + ab * h * 0.12f
        val contorno = mutableListOf(sil)
        drawOval(t.c(mezcla(HOLLIN, OXIDO_OSC, 0.3f)), Offset(r.left, r.top - elip), Size(r.width, elip * 2))
        for (i in 0 until m) {
            val f = (i + 0.5f) / m
            val x = r.left + w * 0.06f + (r.width - w * 0.24f) * f
            val alto = h * (0.17f + 0.12f * azar(c.semilla, i)) + alza
            val giroH = (f - 0.5f) * 40f + (azar(c.semilla, i + 3) - 0.5f) * 10f
            contorno += girar(Path().apply { addRect(Rect(x, r.top + elip * 0.5f - alto, x + w * 0.12f, r.top + elip * 0.5f)) }, giroH, Offset(x + w * 0.06f, r.top + elip * 0.5f))
            rotate(giroH, Offset(x + w * 0.06f, r.top + elip * 0.5f)) {
                val hoja = Rect(x, r.top + elip * 0.5f - alto, x + w * 0.12f, r.top + elip * 0.5f)
                drawRect(t.c(if (i % 3 == 0) mezcla(PAPEL, PAJA, 0.3f) else PAPEL), hoja.topLeft, hoja.size)
                drawRect(t.s(0.15f), Offset(hoja.right - w * 0.03f, hoja.top), Size(w * 0.03f, hoja.height))
                for (k in 0 until 3) drawLine(t.c(TINTA).copy(alpha = 0.3f), Offset(hoja.left + w * 0.015f, hoja.top + h * (0.03f + 0.025f * k)), Offset(hoja.right - w * 0.02f, hoja.top + h * (0.03f + 0.025f * k)), 0.6f * u)
                drawRect(t.b(0.3f, 0.2f), hoja.topLeft, Size(hoja.width, 1f * u))
            }
        }
        // El cuerpo: lata pintada, con su cenefa de relieve, la pintura gastada, el óxido y la abolladura.
        val cuerpo = Path().apply { moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom - elip * 0.7f); arcTo(Rect(r.left, r.bottom - elip * 1.4f, r.right, r.bottom), 0f, 180f, false); close() }
        drawPath(cuerpo, t.c(pintura))
        clipPath(cuerpo) {
            listOf(0.18f, 0.86f).forEach { fy ->
                val y = r.top + r.height * fy
                drawArc(t.c(mezcla(pintura, PAJA, 0.55f)), 0f, 180f, false, Offset(r.left, y - elip * 0.7f), Size(r.width, elip * 1.4f), style = Stroke(w * 0.022f))
                for (k in 0 until 14) {
                    val x = r.left + r.width * (k + 0.5f) / 14f
                    drawCircle(t.c(mezcla(pintura, HOLLIN, 0.3f)), w * 0.006f, Offset(x, y + elip * 0.7f * (1f - ((x - r.center.x) / (r.width / 2)).let { it * it }).coerceAtLeast(0f).let { kotlin.math.sqrt(it) }))
                }
            }
            // La pintura gastada: rayones que dejan ver la hojalata.
            for (k in 0 until 10) {
                val p = Offset(r.left + r.width * azar(c.semilla, k + 70), r.top + r.height * azar(c.semilla, k + 80))
                drawLine(t.c(HOJALATA).copy(alpha = 0.55f), p, p + Offset(w * 0.04f * azar(c.semilla, k + 90), -w * 0.01f), 0.8f * u)
            }
            for (k in 0 until 4) {
                val p = Offset(r.left + r.width * azar(c.semilla, k + 100), r.bottom - r.height * 0.3f * azar(c.semilla, k + 110))
                val q = w * (0.03f + 0.04f * azar(c.semilla, k + 120))
                drawOval(t.c(OXIDO).copy(alpha = 0.55f), Offset(p.x - q, p.y - q * 0.4f), Size(q * 2, q * 0.8f))
            }
            // La abolladura, a un lado: un hundido hondo con su filo de luz.
            val ab0 = Offset(r.left + r.width * (if (c.semilla % 2 == 0) 0.85f else 0.12f), r.top + r.height * 0.35f)
            drawOval(t.s(0.32f), Offset(ab0.x - w * 0.06f, ab0.y - h * 0.04f), Size(w * 0.11f, h * 0.12f))
            drawArc(t.b(0.55f, 0.3f), 30f, 130f, false, Offset(ab0.x - w * 0.055f, ab0.y - h * 0.02f), Size(w * 0.11f, h * 0.12f), style = Stroke(1.4f * u))
            sombraCilindro(r, t)
        }
        // La tapa, entreabierta: alzada del lado izquierdo, apoyada en el borde derecho.
        val giro = -8f - 5f * (c.n.coerceAtMost(8) / 8f) - 10f * ab
        rotate(giro, Offset(r.right, r.top)) {
            val banda = Rect(r.left - w * 0.012f, r.top - h * 0.07f, r.right + w * 0.012f, r.top + h * 0.02f)
            val tapa = Path().apply { addOval(Rect(banda.left, banda.top - elip, banda.right, banda.top + elip)); addRect(banda); addOval(Rect(banda.left, banda.bottom - elip, banda.right, banda.bottom + elip)) }
            contorno += girar(tapa, giro, Offset(r.right, r.top))
            translate(w * 0.008f, h * 0.02f) { drawPath(tapa, t.s(0.3f)) }
            drawPath(tapa, t.c(pintura))
            clipPath(tapa) {
                drawOval(t.c(mezcla(pintura, LUZ_MUEBLE, 0.25f)), Offset(banda.left, banda.top - elip), Size(banda.width, elip * 2))
                drawOval(t.c(mezcla(pintura, PAJA, 0.4f)), Offset(banda.left + w * 0.14f, banda.top - elip * 0.6f), Size(banda.width - w * 0.28f, elip * 1.2f), style = Stroke(1.4f * u))
                // Una flor de relieve en la tapa, desvaída.
                drawPath(estrella(Offset(banda.center.x, banda.top), w * 0.035f, 6, 0.5f), t.c(mezcla(pintura, PAJA, 0.45f)).copy(alpha = 0.7f))
                sombraCilindro(banda, t)
                drawRect(t.b(0.4f, 0.3f), Offset(banda.left, banda.top), Size(banda.width, 1.2f * u))
            }
        }
        cubreAura(t.oscuro, contorno)
    }

    /** El bote de pintura viejo de lo que no tiene tema: chorretones secos, el asa de alambre y las notas sueltas. */
    private fun DrawScope.bote(c: EstadoCajon, pie: Float, ab: Float, t: Tono) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(w * 0.13f, h * 0.32f, w * 0.87f, pie)
        val elip = w * 0.05f
        val boca = Rect(r.left, r.top - elip, r.right, r.top + elip)
        val sil = Path().apply { addRect(r); addOval(boca) }
        sombraDetras(sil, t, pie - w * 0.03f)
        contacto(Offset(r.center.x + w * 0.02f, pie), r.width * 1.08f, w * 0.05f, t)
        val contorno = mutableListOf(sil)
        val asa = Path().apply { addArc(Rect(r.left - w * 0.01f, r.top - h * 0.24f, r.right + w * 0.01f, r.top + h * 0.24f), 180f, 180f) }
        // El asa de alambre, echada hacia atrás.
        drawArc(t.c(HOJALATA_OSC), 180f, 180f, false, Offset(r.left - w * 0.01f, r.top - h * 0.24f), Size(r.width + w * 0.02f, h * 0.48f), style = Stroke(1.6f * u))
        drawOval(t.c(mezcla(HOLLIN, OXIDO_OSC, 0.3f)), boca.topLeft, boca.size)
        // Las notas sueltas, revueltas: hojas dobladas que asoman ladeadas.
        val m = c.n.coerceIn(1, 8)
        for (i in 0 until m) {
            val x = r.left + w * 0.05f + (r.width - w * 0.2f) * (i + 0.5f) / m
            val alto = h * (0.1f + 0.08f * azar(c.semilla, i) + 0.01f * m) + ab * h * 0.12f
            val giroH = (azar(c.semilla, i + 3) - 0.5f) * 60f
            contorno += girar(poligono(x, r.top + elip * 0.4f, x + w * 0.1f, r.top + elip * 0.4f, x + w * 0.11f, r.top - alto, x + w * 0.02f, r.top - alto + h * 0.03f), giroH, Offset(x + w * 0.05f, r.top + elip * 0.4f))
            rotate(giroH, Offset(x + w * 0.05f, r.top + elip * 0.4f)) {
                val hoja = poligono(x, r.top + elip * 0.4f, x + w * 0.1f, r.top + elip * 0.4f, x + w * 0.11f, r.top - alto, x + w * 0.02f, r.top - alto + h * 0.03f)
                drawPath(hoja, t.c(if (i % 2 == 0) PAPEL else mezcla(PAPEL, PAJA, 0.3f)))
                drawLine(t.s(0.25f), Offset(x + w * 0.055f, r.top), Offset(x + w * 0.065f, r.top - alto + h * 0.015f), 0.9f * u)
                drawLine(t.b(0.4f, 0.2f), Offset(x + w * 0.02f, r.top - alto + h * 0.03f), Offset(x + w * 0.11f, r.top - alto), 0.8f * u)
            }
        }
        // El cuerpo: la lata gris con chorretones de pintura seca de dos colores que bajan del borde.
        val cuerpo = Path().apply { moveTo(r.left, r.top); arcTo(boca, 180f, -180f, false); lineTo(r.right, r.bottom - elip); arcTo(Rect(r.left, r.bottom - 2 * elip, r.right, r.bottom), 0f, 180f, false); close() }
        drawPath(cuerpo, t.c(mezcla(HOJALATA, HORMIGON_FRIO, 0.4f)))
        clipPath(cuerpo) {
            for (k in 0 until 7) {
                val x = r.left + r.width * azar(c.semilla, k + 130)
                val largo = r.height * (0.15f + 0.6f * azar(c.semilla, k + 140))
                val an = w * (0.02f + 0.03f * azar(c.semilla, k + 150))
                val col = if (k % 3 == 0) PAJA else if (k % 3 == 1) mezcla(PAPEL, PAJA, 0.2f) else AGUA
                drawRoundRect(t.c(col), Offset(x - an / 2, r.top - elip), Size(an, largo + elip), CornerRadius(an / 2))
                drawCircle(t.c(col), an * 0.7f, Offset(x, r.top + largo - an * 0.2f))
            }
            drawRect(t.c(PAJA), Offset(r.left, r.top - elip), Size(r.width, elip * 1.3f))
            drawRect(t.c(OXIDO_OSC).copy(alpha = 0.45f), Offset(r.left, r.bottom - elip * 1.5f), Size(r.width, elip * 1.5f))
            sombraCilindro(r, t)
        }
        drawArc(t.c(mezcla(HOJALATA_OSC, PAJA, 0.4f)), 0f, 180f, false, boca.topLeft, boca.size, style = Stroke(2.4f * u))
        // Las orejas del asa.
        listOf(r.left + w * 0.01f, r.right - w * 0.01f).forEach { x -> drawCircle(t.c(HOJALATA_OSC), w * 0.016f, Offset(x, r.top + h * 0.03f)) }
        cubreAura(t.oscuro, contorno, listOf(asa to 1.6f * u))
    }

    /** Un hueco sin tema: el cerco de óxido de una lata y una maceta hecha con media lata, o una telaraña en el rincón. */
    private fun DrawScope.vacio(c: EstadoCajon, pie: Float, t: Tono) {
        val u = density
        val w = size.width; val h = size.height
        // El cerco de óxido donde hubo una lata.
        drawOval(t.c(OXIDO).copy(alpha = 0.45f), Offset(w * 0.5f, pie - w * 0.018f), Size(w * 0.34f, w * 0.036f), style = Stroke(2f * u))
        drawOval(t.c(OXIDO_OSC).copy(alpha = 0.3f), Offset(w * 0.53f, pie - w * 0.012f), Size(w * 0.24f, w * 0.022f))
        if (Math.floorMod(c.semilla / 7919, 2) == 0) {
            // La maceta: media lata con tierra y un brote de dos hojas.
            val r = Rect(w * 0.16f, pie - h * 0.22f, w * 0.42f, pie)
            val elip = w * 0.03f
            contacto(Offset(r.center.x + w * 0.02f, pie), r.width * 1.15f, w * 0.045f, t)
            val cuerpo = Path().apply { moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom - elip); arcTo(Rect(r.left, r.bottom - 2 * elip, r.right, r.bottom), 0f, 180f, false); close() }
            drawOval(t.c(TIERRA), Offset(r.left, r.top - elip), Size(r.width, elip * 2))
            drawPath(cuerpo, t.c(HOJALATA))
            clipPath(cuerpo) {
                for (k in 0 until 3) drawRect(t.c(OXIDO).copy(alpha = 0.5f), Offset(r.left + r.width * azar(c.semilla, k), r.top + r.height * azar(c.semilla, k + 5)), Size(w * 0.04f, w * 0.03f))
                sombraCilindro(r, t)
            }
            // El borde cortado a tijera, en dientes.
            val dientes = Path().apply {
                moveTo(r.left, r.top)
                for (k in 0..8) lineTo(r.left + r.width * k / 8f, r.top + (if (k % 2 == 0) 0f else -w * 0.01f))
            }
            drawPath(dientes, t.c(HOJALATA_OSC), style = Stroke(1.2f * u))
            val b = Offset(r.center.x, r.top - elip * 0.2f)
            val tope = b + Offset(w * 0.01f, -h * 0.2f)
            drawPath(Path().apply { moveTo(b.x, b.y); cubicTo(b.x - w * 0.02f, b.y - h * 0.08f, tope.x, tope.y + h * 0.08f, tope.x, tope.y) }, t.c(HELECHO), style = Stroke(1.6f * u, cap = StrokeCap.Round))
            hoja(tope, w * 0.08f, -60f, HIEDRA_LUZ, t, ancho = 0.7f)
            hoja(tope + Offset(0f, h * 0.03f), w * 0.07f, 65f, HELECHO, t, ancho = 0.7f)
            hoja(tope, w * 0.035f, 0f, HELECHO, t, sombraFuera = false, ancho = 0.6f)
        } else {
            // La telaraña en el rincón de arriba a la izquierda, entre el poste y la balda de arriba.
            val o = Offset(0f, 0f)
            val hilo = if (t.oscuro) PAPEL.copy(alpha = 0.05f + 0.2f * t.k) else PAPEL.copy(alpha = 0.32f)
            // Los radios, de largos distintos; y la espiral, colgando un poco entre radio y radio.
            val radios = (0 until 6).map { k ->
                val a = Math.toRadians(4.0 + k * 17.0 + 6.0 * azar(c.semilla, k + 200))
                val l = w * (0.2f + 0.1f * azar(c.semilla, k + 210))
                Offset((cos(a) * l).toFloat(), (sin(a) * l).toFloat())
            }
            radios.forEach { p -> drawLine(hilo, o, p, 0.5f * u) }
            for (j in 1..5) {
                val f = j / 5.6f
                val p = Path()
                var prev = Offset.Zero
                radios.forEachIndexed { k, q ->
                    val pp = q * (f + 0.04f * azar(c.semilla, j * 7 + k))
                    if (k == 0) p.moveTo(pp.x, pp.y)
                    else { val m = (prev + pp) * 0.5f * 0.9f; p.cubicTo(m.x, m.y, m.x, m.y, pp.x, pp.y) }
                    prev = pp
                }
                drawPath(p, hilo, style = Stroke(0.5f * u))
            }
            // Una hojita seca enganchada en la tela.
            hoja(radios[3] * 0.55f, w * 0.035f, 160f, mezcla(PAJA, TIERRA, 0.45f), t, sombraFuera = false, ancho = 0.8f)
            // Y unas hojas secas caídas en la balda.
            listOf(0.25f to 20f, 0.36f to -40f).forEach { (fx, a) -> hoja(Offset(w * fx, pie - w * 0.006f), w * 0.05f, 90f + a, mezcla(PAJA, TIERRA, 0.4f), t, sombraFuera = false, ancho = 0.8f) }
        }
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Dónde está este hueco en el mueble (en fracciones del ancho), para saber cuánto le llega del farol: el de arriba a la
        // derecha, cálido; cada fila hacia abajo, más apagado; la columna de la izquierda, un punto más lejos.
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        val k = if (oscuro) luzFarol(centro) else 0f
        // De día, la luz de arriba a la izquierda aclara apenas las filas de arriba y la columna de la izquierda.
        val dia = if (oscuro) 0f else 0.06f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.025f else 0f)
        val t = Tono(oscuro, k, dia)
        // El pie de los recipientes, en la cara de arriba de la balda (la pinta el mueble al fondo de cada hueco).
        val pie = h - w * (CARA / anchoH) * 0.45f
        if (c.vacio) { vacio(c, pie, t); return }
        val m = mezclar(c.semilla)
        val clase = if (c.sinTema) -1 else Math.floorMod(m, 3)
        when (clase) {
            -1 -> bote(c, pie, ab, t)
            0 -> lata(c, pie, ab, t)
            1 -> frasco(c, pie, ab, t)
            else -> galletas(c, pie, ab, t)
        }
        // La cinta con el tema (la letra la pone el rótulo).
        cinta(Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom), t, c.semilla, c.sinTema)
        // En algunas trepa una ramita de hiedra por el costado.
        if (Math.floorMod(m ushr 4, 3) == 0) ramita(Offset(w * (if (Math.floorMod(m ushr 7, 2) == 0) 0.14f else 0.84f), pie - w * 0.01f), h * 0.45f, t, c.semilla)
        // Al abrirlo, sube un poco de luz de dentro.
        if (ab > 0f) mancha(Offset(w / 2, h * 0.3f), w * 0.4f, if (oscuro) LLAMA.copy(alpha = 0.18f * ab) else luzMueble(0.3f * ab))
    }

    override val rotulo = Rect(0.11f, 0.5f, 0.89f, 0.77f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.overpass
    override val salida = 0f
    override val placa = Rect(0.33f, 0.22f, 0.67f, 0.33f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior y las fichas (los de siempre)

    /** Una hoja de hiedra: tres lóbulos. */
    private fun DrawScope.hojaHiedra(c: Offset, s: Float, color: Color) {
        drawCircle(color, s * 0.42f, Offset(c.x, c.y - s * 0.2f))
        drawCircle(color, s * 0.34f, Offset(c.x - s * 0.32f, c.y + s * 0.08f))
        drawCircle(color, s * 0.34f, Offset(c.x + s * 0.32f, c.y + s * 0.08f))
    }

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo de la lata: hojalata con sus anillos, musgo en el borde y unas hojas caídas.
        val fondo = if (oscuro) mezcla(HOJALATA, Color.Black, 0.6f) else HOJALATA
        drawRect(fondo)
        val c = Offset(w / 2, h / 2)
        var r = 20 * u
        while (r < maxOf(w, h)) { drawCircle(Color.Black.copy(alpha = 0.06f), r, c, style = Stroke(2 * u)); r += 26 * u }
        for (k in 0 until 18) {
            val a = azar(51, k) * 6.28f
            mancha(Offset(c.x + w * 0.5f * cos(a), c.y + h * 0.5f * sin(a)), (30f + 30f * azar(52, k)) * u, MUSGO.copy(alpha = 0.3f))
        }
        for (k in 0 until 4) {
            val p = Offset(w * azar(53, k), h * azar(54, k))
            rotate(azar(55, k) * 360f, p) {
                drawOval(HIEDRA.copy(alpha = 0.3f), Offset(p.x - 14 * u, p.y - 6 * u), Size(28 * u, 12 * u))
                drawLine(HIEDRA.copy(alpha = 0.4f), Offset(p.x - 14 * u, p.y), Offset(p.x + 14 * u, p.y), 0.8f * u)
            }
        }
        val pl = 12 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { rr -> drawRect(t(HOJALATA_OSC, oscuro), rr.topLeft, rr.size) }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 16 * u, 0.3f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) PAPEL else TINTA
    override val pared: Dp = 14.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(contornoIrregular(Rect(0f, 0f, s.width, s.height), semilla, 26, 0.01f)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(PAPEL.copy(alpha = 0.4f))
        // Manchas de agua y una hoja prensada en la esquina de abajo.
        for (k in 0 until 3) {
            val c = Offset(w * azar(semilla, k), h * azar(semilla, k + 10))
            drawCircle(Color(0xFF8A7A50).copy(alpha = 0.1f), minOf(w, h) * (0.1f + 0.1f * azar(semilla, k + 20)), c)
            drawCircle(Color(0xFF8A7A50).copy(alpha = 0.14f), minOf(w, h) * (0.1f + 0.1f * azar(semilla, k + 20)), c, style = Stroke(1 * u))
        }
        val hc = Offset(w - 14 * u * g, h - 14 * u * g)
        rotate(-35f, hc) {
            drawOval(HIEDRA.copy(alpha = 0.35f), Offset(hc.x - 9 * u * g, hc.y - 4 * u * g), Size(18 * u * g, 8 * u * g))
            drawLine(HIEDRA.copy(alpha = 0.5f), Offset(hc.x - 12 * u * g, hc.y), Offset(hc.x + 9 * u * g, hc.y), 0.8f * u)
        }
    }

    override val margenes = MargenesFicha(13.dp, 14.dp, 14.dp, 26.dp, 26.dp, 30.dp)
    override val proporcionFicha = 0.9f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            "hecho" -> selloHecho(OXIDO, p.color)
            // Un brote con sus dos hojas.
            "convence" -> {
                drawLine(HIEDRA, Offset(c.x, c.y + s * 0.44f), Offset(c.x, c.y - s * 0.06f), s * 0.07f, StrokeCap.Round)
                rotate(-35f, Offset(c.x, c.y - s * 0.06f)) { drawOval(HIEDRA_LUZ, Offset(c.x - s * 0.42f, c.y - s * 0.2f), Size(s * 0.42f, s * 0.24f)) }
                rotate(35f, Offset(c.x, c.y - s * 0.06f)) { drawOval(HIEDRA, Offset(c.x, c.y - s * 0.2f), Size(s * 0.42f, s * 0.24f)) }
            }
            "piensa" -> hojaHiedra(c, s * 0.62f, HIEDRA)
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(OXIDO)
            else -> {}
        }
    }

    /** Un clavo oxidado. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawLine(OXIDO_OSC, Offset(c.x - s * 0.2f, c.y - s * 0.2f), Offset(c.x + s * 0.32f, c.y + s * 0.4f), s * 0.1f, StrokeCap.Round)
        drawCircle(OXIDO, s * 0.2f, Offset(c.x - s * 0.24f, c.y - s * 0.24f))
    }

    override val desorden = 2f
}
