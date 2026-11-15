package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.TrazosArcontes
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraContacto
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import java.util.Random
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Arcontes: Vida en la casa del salador de pescado en Magdala, a la orilla del lago de Galilea, hacia el año 30
 * (`docs/diseno/temas/arcontes.md`). Muros de caliza encalada con desconchones por donde asoma la piedra, vigas y tablas de
 * olivo retorcido, techo de cañizo, piso de tierra apisonada, esteras de junco, lino, jarras y ánforas de barro acanaladas,
 * cestos de sal, pescado abierto secándose, la red con sus flotadores y sus pesas, y la lámpara de aceite de pico en su
 * nicho con la mancha de hollín encima. Escondido entre lo de todos los días, lo del iniciado: la jarra sellada del códice,
 * el amuleto de jaspe colgado de un clavo y, por alguna rendija, la luz blanca y violeta de la grieta. La luz viene de arriba
 * a la izquierda y la sombra es violeta (`ui/muebles/Luz.kt`, versión sutil). De noche (modo oscuro) es la misma casa con el
 * azul negro del cielo antiguo encima: todo se hunde en el aire de la noche, por la puerta se ven las estrellas y, apenas,
 * los círculos de las esferas, y la única luz cálida es la de la lámpara, que alumbra solo lo que tiene cerca.
 * - Social: el cuarto donde se reúne el círculo. La viga maestra con las cabezas de las vigas sobre el cañizo; a la
 *   izquierda, la puerta que da al lago, con sus jambas de sillares, el dintel de olivo, las barcas y el pescado colgado al
 *   aire; en medio, el velo de lino colgado de su vara (el nicho: el grafo va en el lino); a la derecha, el nicho de la
 *   lámpara, el amuleto en su clavo, la repisa con el jarrito y los cuencos, la red colgada y, en el suelo, la jarra sellada,
 *   el ánfora apoyada en el muro y el cesto de sal. Abajo, la estera con la comida: panes, el cuenco de pescado, la jarra y
 *   las copas, y el rayo de sol (de noche, de estrellas) que entra por la puerta.
 * - Mapas: la mesa del salado vista desde arriba: tablones de olivo con su veta y sus nudos, y el lienzo de lino tendido
 *   (el nicho) sujeto en las esquinas con cantos de basalto; a la izquierda, el pescado abierto en su sal, el montón de sal y
 *   la pala; a la derecha, la lámpara vista desde arriba, el tintero con el cálamo, el cuenco de aceitunas, el cuchillo y el
 *   amuleto; por la junta de dos tablones se cuela la luz de la grieta. Abajo, el canto de la mesa, los caballetes y la estera.
 * - Otras vidas: el muro de las hornacinas. Arriba, la viga de olivo con el pescado colgado a secar y la red tendida con sus
 *   flotadores; cinco hornacinas de arco iguales (el nicho) en la caliza, separadas por pilares encalados; abajo, el poyo de
 *   piedra con su estera, la lámpara en una punta y el cesto de sal en la otra.
 * - Íconos: la fachada del Templo de Jerusalén con su vid de oro y el velo de la puerta, por el que asoma la grieta; una
 *   hoja de papiro ahumada que una prutá de Herodes Antipas, la de la caña, raspa hasta descubrir el lago; y el lago de
 *   Galilea dibujado en papiro con tres bulas de barro unidas por el camino de la orilla.
 * - Notas: la estera de junco colgada del muro en su marco de olivo, bajo la viga, con la repisa de la lámpara, el tintero,
 *   los cálamos y el jarrito. Las notas son hojas de papiro con sus fibras, colgadas de una espina; cada tipo lleva su señal
 *   en la esquina (la chispa, las esferas, la lámpara, el nudo de lino) y las fijadas, una bula de barro con el ouroboros.
 */
internal object VidaArcontes : DisenoVida {
    // ---------------------------------------------------------------- La paleta de la casa
    private val CALIZA = TrazosArcontes.CALIZA
    private val CALIZA_SOMBRA = TrazosArcontes.CALIZA_SOMBRA
    /** El enlucido de cal del muro, su luz y la piedra que asoma donde se cayó. */
    private val CAL = Color(0xFFEAE0C4)
    private val CAL_LUZ = Color(0xFFF6EFDC)
    private val PIEDRA = Color(0xFFC6B48C)
    private val PIEDRA_OSC = Color(0xFF8F7C5A)
    /** La tierra apisonada del piso. */
    private val TIERRA = Color(0xFFA48B63)
    private val TIERRA_OSC = Color(0xFF6E5A3E)
    /** La madera de olivo: amarilla y parda, con vetas oscuras que se retuercen. */
    private val OLIVO = Color(0xFFA0845A)
    private val OLIVO_LUZ = Color(0xFFCBB07E)
    private val OLIVO_OSC = Color(0xFF654C32)
    private val OLIVO_VETA = Color(0xFF4A3524)
    /** El junco de las esteras y del cañizo. */
    private val JUNCO = Color(0xFFC4A96A)
    private val JUNCO_LUZ = Color(0xFFDCC68E)
    private val JUNCO_OSC = Color(0xFF8A7140)
    private val LINO = TrazosArcontes.LINO
    private val LINO_OSC = Color(0xFFCFC2A2)
    private val CUERDA = Color(0xFFCDBB8E)
    private val BARRO = TrazosArcontes.BARRO
    private val BARRO_CLARO = TrazosArcontes.BARRO_CLARO
    private val BARRO_LUZ = Color(0xFFD69C6C)
    private val SAL = Color(0xFFF8F5EC)
    /** El pescado salado: la piel plateada y la carne abierta. */
    private val PIEL = Color(0xFFA9A493)
    private val CARNE = Color(0xFFD8B98A)
    private val PAN = Color(0xFFC78F4F)
    private val ACEITUNA = Color(0xFF4B4A2A)
    private val BASALTO = Color(0xFF4A4A4E)
    private val BRONCE = Color(0xFFA77A3C)
    private val BRONCE_LUZ = Color(0xFFDDB173)
    private val PAPIRO = Color(0xFFEADFBF)
    private val FIBRA = Color(0xFF9C8458)
    private val TINTA = Color(0xFF1E1A2B)
    private val ORO = TrazosArcontes.ORO_DEMIURGO
    private val LAGO = TrazosArcontes.LAGO
    private val LAGO_HONDO = TrazosArcontes.LAGO_HONDO
    private val CIELO_DIA = Color(0xFFD5DAD0)
    private val BRUMA = Color(0xFFEFE4C2)
    private val COLINAS = Color(0xFFB7A27E)
    private val ARENA = Color(0xFFD9C79C)
    private val CIELO = TrazosArcontes.CIELO_ANTIGUO
    private val ESFERA = TrazosArcontes.ESFERA
    private val LLAMA = TrazosArcontes.LLAMA
    private val LLAMA_HALO = TrazosArcontes.LLAMA_HALO
    private val GRIETA_HALO = TrazosArcontes.GRIETA_HALO

    /** El aire de la noche: lo que multiplica cada color, y el azul negro hacia el que se hunde. */
    private val AIRE = Color(0xFF6A7099)
    private val NOCHE = Color(0xFF0D1020)
    /** El barro y la madera a la luz de la lámpara. */
    private val CALIDO = Color(0xFF9A6430)

    // ---------------------------------------------------------------- Ayudas

    private fun mez(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** El color de noche: multiplicado por el aire azul y hundido hacia el azul negro del cielo antiguo; de día, tal cual. */
    private fun n(c: Color, o: Boolean): Color {
        if (!o) return c
        return mez(Color(c.red * AIRE.red, c.green * AIRE.green, c.blue * AIRE.blue, c.alpha), NOCHE.copy(alpha = c.alpha), 0.42f)
    }

    /** El color de noche a la luz de la lámpara: [f] (0 a 1) es cuánto le llega. De día, tal cual. */
    private fun lam(c: Color, o: Boolean, f: Float): Color = if (!o) c else mez(n(c, o), mez(c, CALIDO, 0.4f), f * 0.85f)

    /** El fondo sólido de un nicho: el [material] llevado a la luminancia de la tarjeta. */
    private fun nicho(e: EstadoMueble, o: Boolean, material: Color): Color =
        if (o) mez(e.fondo, n(material, o), 0.22f) else mez(e.fondo, material, 0.3f)

    private fun DrawScope.enLienzo(a: Rect) = Rect(size.width * a.left, size.height * a.top, size.width * a.right, size.height * a.bottom)
    private fun DrawScope.rect(r: Rect, c: Color) = drawRect(c, r.topLeft, r.size)

    /** Un número estable de 0 a 1. */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** El halo de una llama: discos que se suman hacia el centro, sin degradado. */
    private fun DrawScope.halo(c: Offset, r: Float, color: Color, fuerza: Float, pasos: Int = 26) {
        val a = 0.3f / pasos
        for (k in 0 until pasos) drawCircle(color.copy(alpha = (a * fuerza * (1f + k * 2f / pasos)).coerceIn(0f, 1f)), r * (1f - k / pasos.toFloat()) * (1f - k / pasos.toFloat() * 0.15f), c)
    }

    /** Todo lo que se dibuja dentro de [bloque], menos lo que caería sobre [nicho]. */
    private fun DrawScope.fueraDe(nicho: Rect, bloque: DrawScope.() -> Unit) {
        val p = Path().apply { addRect(Rect(-size.width, -size.height, size.width * 2f, size.height * 2f)); addRect(nicho); fillType = PathFillType.EvenOdd }
        clipPath(p) { bloque() }
    }

    /** Un contorno irregular dentro de [r] (un desconchón, una piedra, un montón): un óvalo de radios que varían. */
    private fun mancha(r: Rect, semilla: Int, puntos: Int = 14, rugoso: Float = 0.22f): Path {
        val path = Path()
        for (k in 0 until puntos) {
            val a = 2 * PI * k / puntos
            val f = 1f - rugoso * azar(semilla, k)
            val x = r.center.x + r.width / 2 * f * cos(a).toFloat()
            val y = r.center.y + r.height / 2 * f * sin(a).toFloat()
            if (k == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        return path
    }

    /** La sombra blanda de una pieza sobre lo que tiene detrás, corrida abajo a la derecha. */
    private fun DrawScope.sombraPieza(r: Rect, alcance: Float, fuerza: Float, radio: Float = 0f) {
        for (k in 3 downTo 1) {
            val d = alcance * k / 3f
            drawRoundRect(sombraMueble(fuerza / 3f), Offset(r.left + d * 0.5f, r.top + d * 0.6f), Size(r.width + d * 0.2f, r.height + d * 0.15f), CornerRadius(radio + d))
        }
    }

    /**
     * El muro encalado: la cal con sus pinceladas, unos desconchones por donde asoma la caliza en hiladas y unas grietas
     * finas de las de siempre (no la de la luz).
     */
    private fun DrawScope.muro(r: Rect, o: Boolean, u: Float, semilla: Int, huecos: List<Rect> = emptyList()) {
        rect(r, n(CAL, o))
        pinceladas(r, n(CAL_LUZ, o), semilla, (r.width * r.height / (300f * u * u)).toInt().coerceIn(8, 40), u, if (o) 0.06f else 0.12f)
        pinceladas(r, n(CALIZA_SOMBRA, o), semilla + 5, (r.width * r.height / (450f * u * u)).toInt().coerceIn(6, 30), u, 0.07f)
        huecos.forEachIndexed { i, hueco -> desconchon(hueco, o, u, semilla + 11 * i) }
        val rnd = Random(semilla * 7L + 1L)
        repeat(3) {
            var x = r.left + r.width * rnd.nextFloat(); var y = r.top + r.height * (0.1f + 0.5f * rnd.nextFloat())
            val path = Path().apply { moveTo(x, y) }
            repeat(5) { x += (rnd.nextFloat() - 0.5f) * 3f * u; y += (1.2f + rnd.nextFloat() * 2f) * u; path.lineTo(x, y) }
            drawPath(path, n(CALIZA_SOMBRA, o).copy(alpha = 0.45f), style = Stroke(0.22f * u, join = StrokeJoin.Round))
        }
    }

    /** Un desconchón: la cal caída, la caliza de debajo en hiladas, la sombra del borde de la cal arriba y su canto con luz abajo. */
    private fun DrawScope.desconchon(r: Rect, o: Boolean, u: Float, semilla: Int) {
        val forma = mancha(r, semilla, 16, 0.3f)
        drawPath(forma, n(PIEDRA, o))
        clipPath(forma) {
            val hilada = r.height * 0.5f
            var y = r.top + hilada * (0.3f + 0.4f * azar(semilla, 1))
            var fila = 0
            while (y < r.bottom) {
                drawLine(n(PIEDRA_OSC, o), Offset(r.left, y), Offset(r.right, y), 0.35f * u)
                var x = r.left + r.width * 0.3f * azar(semilla, fila + 4) + (if (fila % 2 == 0) 0f else r.width * 0.2f)
                while (x < r.right) { drawLine(n(PIEDRA_OSC, o), Offset(x, y - hilada), Offset(x, y), 0.3f * u); x += r.width * 0.45f }
                y += hilada; fila++
            }
            pinceladas(r, n(PIEDRA_OSC, o), semilla, 4, u * 0.5f, 0.15f)
            translate(-0.5f * u, -0.6f * u) { drawPath(forma, sombraMueble(if (o) 0.5f else 0.35f), style = Stroke(1.2f * u)) }
        }
        translate(0.25f * u, 0.3f * u) { drawPath(forma, luzMueble(if (o) 0.08f else 0.55f), style = Stroke(0.4f * u)) }
    }

    /** Un tramo de madera de olivo: la base, las vetas que se retuercen y algún nudo; [vertical] para los postes. */
    private fun DrawScope.madera(r: Rect, o: Boolean, u: Float, semilla: Int, vertical: Boolean = false, f: Float = 0f, densidad: Float = 1f) {
        rect(r, lam(OLIVO, o, f))
        val largo = if (vertical) r.height else r.width
        val ancho = if (vertical) r.width else r.height
        if (largo <= 0f || ancho <= 0f) return
        clipRect(r.left, r.top, r.right, r.bottom) {
            val lineas = (ancho / (1.5f * u) * densidad).toInt().coerceIn(2, 30)
            for (k in 0 until lineas) {
                val base = ancho * (k + azar(semilla, k)) / lineas
                val amp = (0.4f + 1.4f * azar(semilla, k + 50)) * u
                val fase = azar(semilla, k + 90) * 6.28f
                val ondas = 1.2f + 2.2f * azar(semilla, k + 130)
                val path = Path()
                for (s in 0..20) {
                    val t = largo * s / 20
                    val v = base + amp * sin(fase + ondas * 6.28f * s / 20)
                    val x = if (vertical) r.left + v else r.left + t
                    val y = if (vertical) r.top + t else r.top + v
                    if (s == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                val oscura = azar(semilla, k + 170) < 0.3f
                drawPath(path, (if (oscura) lam(OLIVO_VETA, o, f) else lam(OLIVO_OSC, o, f)).copy(alpha = if (oscura) 0.45f else 0.28f), style = Stroke((0.16f + 0.26f * azar(semilla, k + 210)) * u))
            }
            // Los nudos: un ojo oscuro con sus anillos alrededor.
            val nudos = (largo / (40f * u)).toInt().coerceIn(1, 4)
            for (k in 0 until nudos) {
                val t = largo * (k + 0.2f + 0.6f * azar(semilla, k + 300)) / nudos
                val v = ancho * (0.25f + 0.5f * azar(semilla, k + 310))
                val c = if (vertical) Offset(r.left + v, r.top + t) else Offset(r.left + t, r.top + v)
                val k0 = minOf(0.9f * u, ancho * 0.11f)
                val rx = if (vertical) k0 else k0 * 2f; val ry = if (vertical) k0 * 2f else k0
                drawOval(lam(OLIVO_OSC, o, f).copy(alpha = 0.5f), Offset(c.x - rx * 1.6f, c.y - ry * 1.6f), Size(rx * 3.2f, ry * 3.2f), style = Stroke(0.25f * u))
                drawOval(lam(OLIVO_VETA, o, f), Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2))
                drawOval(lam(OLIVO_LUZ, o, f).copy(alpha = 0.4f), Offset(c.x - rx * 0.6f, c.y - ry * 0.7f), Size(rx * 0.7f, ry * 0.6f))
            }
        }
    }

    /** Una viga de olivo tendida: el tronco de canto irregular, su luz arriba, su sombra abajo y la sombra en el muro. */
    private fun DrawScope.viga(r: Rect, o: Boolean, u: Float, semilla: Int, sombraMuro: Boolean = true, f: Float = 0f) {
        val forma = Path()
        val pasos = 24
        for (s in 0..pasos) {
            val x = r.left + r.width * s / pasos
            val y = r.top + r.height * 0.12f * (azar(semilla, s) - 0.5f)
            if (s == 0) forma.moveTo(x, y) else forma.lineTo(x, y)
        }
        for (s in pasos downTo 0) {
            val x = r.left + r.width * s / pasos
            val y = r.bottom + r.height * 0.1f * (azar(semilla, s + 40) - 0.5f)
            forma.lineTo(x, y)
        }
        forma.close()
        if (sombraMuro) {
            drawRect(sombraMueble(if (o) 0.3f else 0.22f), Offset(r.left, r.bottom), Size(r.width, 1.6f * u))
            drawRect(sombraMueble(if (o) 0.18f else 0.12f), Offset(r.left, r.bottom), Size(r.width, 3.2f * u))
        }
        clipPath(forma) {
            madera(Rect(r.left, r.top - r.height * 0.1f, r.right, r.bottom + r.height * 0.1f), o, u, semilla, f = f)
            // El tronco es redondo: luz arriba, sombra abajo.
            drawRect(luzMueble(if (o) 0.06f + 0.1f * f else 0.32f), Offset(r.left, r.top - r.height * 0.1f), Size(r.width, r.height * 0.32f))
            drawRect(sombraMueble(if (o) 0.35f else 0.28f), Offset(r.left, r.top + r.height * 0.72f), Size(r.width, r.height * 0.4f))
        }
        drawPath(forma, sombraMueble(0.45f), style = Stroke(0.25f * u))
    }

    /** La cabeza de una viga del techo, de frente: la corteza, los anillos, la raja y su sombra. */
    private fun DrawScope.cabezaViga(c: Offset, r0: Float, o: Boolean, semilla: Int) {
        val r = r0 * (0.85f + 0.25f * azar(semilla, 7))
        val forma = mancha(Rect(c.x - r, c.y - r * 0.94f, c.x + r, c.y + r * 0.94f), semilla, 12, 0.1f)
        translate(r * 0.18f, r * 0.24f) { drawPath(forma, sombraMueble(0.35f)) }
        drawPath(forma, n(OLIVO_VETA, o))
        val cara = mancha(Rect(c.x - r * 0.82f, c.y - r * 0.78f, c.x + r * 0.82f, c.y + r * 0.78f), semilla + 1, 12, 0.08f)
        drawPath(cara, n(mez(OLIVO, OLIVO_LUZ, 0.4f), o))
        val ojo = Offset(c.x + r * 0.12f * (azar(semilla, 3) - 0.5f), c.y + r * 0.1f)
        for (k in 1..3) drawCircle(n(OLIVO_OSC, o).copy(alpha = 0.35f), r * 0.78f * k / 4f, ojo, style = Stroke(r * 0.05f))
        val a = azar(semilla, 1) * 6.28f
        drawLine(n(OLIVO_VETA, o).copy(alpha = 0.7f), Offset(c.x + r * 0.75f * cos(a), c.y + r * 0.72f * sin(a)), Offset(c.x + r * 0.35f * cos(a), c.y + r * 0.33f * sin(a)), r * 0.07f, StrokeCap.Round)
        clipPath(cara) { drawCircle(sombraMueble(if (o) 0.3f else 0.22f), r, Offset(c.x + r * 0.45f, c.y + r * 0.5f)) }
    }

    /** El cañizo del techo: el junco en hileras apretadas, en penumbra bajo las vigas. */
    private fun DrawScope.canizo(r: Rect, o: Boolean, u: Float) {
        rect(r, n(mez(JUNCO_OSC, OLIVO_VETA, 0.35f), o))
        var x = r.left
        var k = 0
        while (x < r.right) {
            val col = if (k % 3 == 0) JUNCO else JUNCO_OSC
            drawLine(n(col, o).copy(alpha = 0.7f), Offset(x, r.top), Offset(x + 0.3f * u, r.bottom), 0.5f * u)
            x += 0.9f * u + 0.4f * u * azar(k, 3); k++
        }
        drawRect(sombraMueble(if (o) 0.35f else 0.28f), r.topLeft, r.size)
    }

    /** Una estera de junco en trapecio (vista en el suelo): el tejido en espiga, el ribete y su sombra. */
    private fun DrawScope.estera(a: Offset, b: Offset, c: Offset, d: Offset, o: Boolean, u: Float, f: Float = 0f) {
        val forma = Path().apply { moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(c.x, c.y); lineTo(d.x, d.y); close() }
        translate(0.6f * u, 0.8f * u) { drawPath(forma, sombraMueble(0.3f)) }
        drawPath(forma, lam(JUNCO, o, f))
        clipPath(forma) {
            val top = minOf(a.y, b.y); val bot = maxOf(c.y, d.y)
            val izq = minOf(a.x, d.x); val der = maxOf(b.x, c.x)
            val fila = 1.6f * u
            var y = top; var k = 0
            while (y < bot) {
                val paso = 1.4f * u
                var x = izq - paso + (if (k % 2 == 0) 0f else paso / 2)
                while (x < der) {
                    val s = if ((k / 2) % 2 == 0) 1f else -1f
                    drawLine(lam(JUNCO_OSC, o, f).copy(alpha = 0.6f), Offset(x, y), Offset(x + s * paso * 0.6f, y + fila), 0.28f * u)
                    drawLine(lam(JUNCO_LUZ, o, f).copy(alpha = 0.45f), Offset(x + 0.3f * u, y), Offset(x + 0.3f * u + s * paso * 0.6f, y + fila), 0.18f * u)
                    x += paso
                }
                y += fila; k++
            }
            drawLine(lam(JUNCO_OSC, o, f), a, b, 1.6f * u)
            drawLine(lam(JUNCO_OSC, o, f), d, c, 1.8f * u)
        }
        drawPath(forma, lam(JUNCO_OSC, o, f), style = Stroke(0.5f * u))
    }

    /**
     * Una jarra de barro acanalada, de pie sobre [base], de [alto] y [ancho]: el cuerpo con la luz a la izquierda y su lado de
     * sombra a la derecha, las acanaladuras del torno, el cuello, el borde y las dos asas. [f] es cuánto le llega la lámpara.
     */
    private fun DrawScope.jarra(base: Offset, alto: Float, ancho: Float, o: Boolean, u: Float, f: Float = 0f, asas: Boolean = true, barro: Color = BARRO_CLARO): Path {
        val x = base.x; val y = base.y
        val cuerpo = Path().apply {
            moveTo(x - ancho * 0.18f, y)
            cubicTo(x - ancho * 0.52f, y - alto * 0.12f, x - ancho * 0.56f, y - alto * 0.62f, x - ancho * 0.3f, y - alto * 0.8f)
            lineTo(x - ancho * 0.2f, y - alto * 0.92f)
            lineTo(x - ancho * 0.26f, y - alto)
            lineTo(x + ancho * 0.26f, y - alto)
            lineTo(x + ancho * 0.2f, y - alto * 0.92f)
            lineTo(x + ancho * 0.3f, y - alto * 0.8f)
            cubicTo(x + ancho * 0.56f, y - alto * 0.62f, x + ancho * 0.52f, y - alto * 0.12f, x + ancho * 0.18f, y)
            close()
        }
        sombraContacto(Offset(x + ancho * 0.12f, y), ancho * 0.9f, ancho * 0.16f, if (o) 0.4f else 0.3f)
        if (asas) listOf(-1f, 1f).forEach { s ->
            drawArc(lam(mez(barro, Color.Black, 0.2f), o, f), if (s < 0) 90f else -90f, 180f, false,
                Offset(x + s * ancho * 0.36f - ancho * 0.11f, y - alto * 0.86f), Size(ancho * 0.22f, alto * 0.22f), style = Stroke(ancho * 0.06f, cap = StrokeCap.Round))
        }
        drawPath(cuerpo, lam(barro, o, f))
        clipPath(cuerpo) {
            // La luz a la izquierda y el lado de sombra a la derecha, en franjas, sin degradado.
            drawRect(luzMueble(if (o) 0.05f + 0.15f * f else 0.22f), Offset(x - ancho * 0.6f, y - alto), Size(ancho * 0.36f, alto))
            drawRect(luzMueble(if (o) 0.04f + 0.1f * f else 0.18f), Offset(x - ancho * 0.36f, y - alto), Size(ancho * 0.12f, alto))
            drawRect(sombraMueble(if (o) 0.35f else 0.22f), Offset(x + ancho * 0.12f, y - alto), Size(ancho * 0.6f, alto))
            drawRect(sombraMueble(if (o) 0.3f else 0.2f), Offset(x + ancho * 0.3f, y - alto), Size(ancho * 0.4f, alto))
            // Las acanaladuras del torno.
            var yy = y - alto * 0.74f
            while (yy < y - alto * 0.3f) {
                drawArc(lam(mez(barro, Color.Black, 0.3f), o, f).copy(alpha = 0.22f), 10f, 160f, false, Offset(x - ancho * 0.6f, yy - alto * 0.03f), Size(ancho * 1.2f, alto * 0.06f), style = Stroke(0.2f * u))
                drawArc(luzMueble(if (o) 0.04f else 0.18f), 100f, 60f, false, Offset(x - ancho * 0.6f, yy - alto * 0.025f), Size(ancho * 1.2f, alto * 0.06f), style = Stroke(0.18f * u))
                yy += alto * 0.055f
            }
            drawRect(sombraMueble(0.3f), Offset(x - ancho, y - alto * 0.92f), Size(ancho * 2, alto * 0.025f))
        }
        drawLine(lam(mez(barro, Color.White, 0.3f), o, f), Offset(x - ancho * 0.26f, y - alto), Offset(x + ancho * 0.26f, y - alto), 0.5f * u, StrokeCap.Round)
        drawPath(cuerpo, sombraMueble(0.4f), style = Stroke(0.25f * u))
        return cuerpo
    }

    /** Un cuenco de barro bajo, de frente, con el borde visto un poco desde arriba; devuelve el óvalo de la boca. */
    private fun DrawScope.cuenco(c: Offset, ancho: Float, alto: Float, o: Boolean, u: Float, f: Float = 0f, barro: Color = BARRO_CLARO): Rect {
        val boca = Rect(c.x - ancho / 2, c.y - alto * 0.22f, c.x + ancho / 2, c.y + alto * 0.22f)
        sombraContacto(Offset(c.x + ancho * 0.08f, c.y + alto), ancho * 0.85f, alto * 0.3f, 0.28f)
        val cuerpo = Path().apply {
            moveTo(boca.left, c.y)
            cubicTo(boca.left + ancho * 0.05f, c.y + alto * 0.8f, boca.right - ancho * 0.05f, c.y + alto * 0.8f, boca.right, c.y)
            close()
        }
        drawPath(cuerpo, lam(barro, o, f))
        clipPath(cuerpo) { drawRect(sombraMueble(if (o) 0.35f else 0.22f), Offset(c.x + ancho * 0.1f, c.y), Size(ancho, alto)) }
        drawRect(lam(mez(barro, Color.Black, 0.3f), o, f), Offset(c.x - ancho * 0.18f, c.y + alto * 0.62f), Size(ancho * 0.36f, alto * 0.14f))
        drawOval(lam(mez(barro, Color.White, 0.22f), o, f), boca.topLeft, boca.size)
        drawOval(lam(mez(barro, Color.Black, 0.35f), o, f), Offset(boca.left + ancho * 0.07f, boca.top + boca.height * 0.15f), Size(ancho * 0.86f, boca.height * 0.78f))
        return boca
    }

    /**
     * Un pescado abierto y salado, como se secaba: las dos mitades abiertas desde el lomo, la espina, la cola y la sal.
     * [c] es el centro, [largo] de la cabeza a la cola y [giro] en grados (0: cabeza a la derecha).
     */
    private fun DrawScope.pezAbierto(c: Offset, largo: Float, giro: Float, o: Boolean, u: Float, f: Float = 0f) {
        rotate(giro, c) {
            val l = largo; val a = largo * 0.36f
            val cuerpo = Path().apply {
                moveTo(c.x + l * 0.5f, c.y)
                cubicTo(c.x + l * 0.4f, c.y - a * 0.6f, c.x - l * 0.1f, c.y - a * 0.62f, c.x - l * 0.3f, c.y - a * 0.2f)
                lineTo(c.x - l * 0.5f, c.y - a * 0.5f); lineTo(c.x - l * 0.44f, c.y); lineTo(c.x - l * 0.5f, c.y + a * 0.5f)
                lineTo(c.x - l * 0.3f, c.y + a * 0.2f)
                cubicTo(c.x - l * 0.1f, c.y + a * 0.62f, c.x + l * 0.4f, c.y + a * 0.6f, c.x + l * 0.5f, c.y)
                close()
            }
            translate(0.3f * u, 0.45f * u) { drawPath(cuerpo, sombraMueble(0.3f)) }
            drawPath(cuerpo, lam(PIEL, o, f))
            // La carne abierta, en dos mitades, con la espina en medio.
            val carne = Path().apply {
                moveTo(c.x + l * 0.4f, c.y)
                cubicTo(c.x + l * 0.32f, c.y - a * 0.42f, c.x - l * 0.08f, c.y - a * 0.44f, c.x - l * 0.26f, c.y - a * 0.1f)
                lineTo(c.x - l * 0.26f, c.y + a * 0.1f)
                cubicTo(c.x - l * 0.08f, c.y + a * 0.44f, c.x + l * 0.32f, c.y + a * 0.42f, c.x + l * 0.4f, c.y)
                close()
            }
            drawPath(carne, lam(CARNE, o, f))
            drawLine(lam(mez(CARNE, Color.Black, 0.3f), o, f), Offset(c.x + l * 0.36f, c.y), Offset(c.x - l * 0.24f, c.y), a * 0.07f)
            for (k in 1..5) {
                val x = c.x + l * (0.3f - 0.1f * k)
                drawLine(lam(mez(CARNE, Color.Black, 0.18f), o, f).copy(alpha = 0.6f), Offset(x, c.y), Offset(x - l * 0.03f, c.y - a * 0.28f), a * 0.035f)
                drawLine(lam(mez(CARNE, Color.Black, 0.18f), o, f).copy(alpha = 0.6f), Offset(x, c.y), Offset(x - l * 0.03f, c.y + a * 0.28f), a * 0.035f)
            }
            // La sal pegada.
            for (k in 0 until 7) drawCircle(n(SAL, o).copy(alpha = 0.8f), a * (0.03f + 0.03f * azar(k, 2)), Offset(c.x + l * (0.35f - 0.6f * azar(k, 5)), c.y + a * (0.38f * (azar(k, 9) - 0.5f))))
            drawPath(cuerpo, lam(mez(PIEL, Color.Black, 0.4f), o, f), style = Stroke(0.2f * u))
            drawLine(luzMueble(if (o) 0.1f else 0.4f), Offset(c.x + l * 0.3f, c.y - a * 0.38f), Offset(c.x - l * 0.05f, c.y - a * 0.42f), 0.25f * u, StrokeCap.Round)
        }
    }

    /** Un cesto de sal trenzado en espiral, de frente, con el montón de sal que asoma y sus granos. */
    private fun DrawScope.cestoSal(base: Offset, ancho: Float, alto: Float, o: Boolean, u: Float, f: Float = 0f) {
        sombraContacto(Offset(base.x + ancho * 0.1f, base.y), ancho * 1.05f, alto * 0.22f, 0.3f)
        // El montón de sal.
        val monton = Path().apply {
            moveTo(base.x - ancho * 0.44f, base.y - alto * 0.8f)
            quadraticTo(base.x - ancho * 0.15f, base.y - alto * 1.25f, base.x + ancho * 0.05f, base.y - alto * 1.18f)
            quadraticTo(base.x + ancho * 0.3f, base.y - alto * 1.1f, base.x + ancho * 0.44f, base.y - alto * 0.8f)
            close()
        }
        drawPath(monton, n(SAL, o))
        drawPath(Path().apply {
            moveTo(base.x + ancho * 0.05f, base.y - alto * 1.18f); quadraticTo(base.x + ancho * 0.3f, base.y - alto * 1.1f, base.x + ancho * 0.44f, base.y - alto * 0.8f)
            lineTo(base.x + ancho * 0.1f, base.y - alto * 0.8f); close()
        }, sombraMueble(if (o) 0.3f else 0.14f))
        for (k in 0 until 9) drawCircle(luzMueble(if (o) 0.25f else 0.9f), u * (0.15f + 0.2f * azar(k, 7)), Offset(base.x + ancho * (0.36f * (azar(k, 3) - 0.6f)), base.y - alto * (0.85f + 0.25f * azar(k, 4))))
        // El cesto: más ancho arriba, trenzado en vueltas.
        val cesto = Path().apply {
            moveTo(base.x - ancho * 0.5f, base.y - alto * 0.82f); lineTo(base.x + ancho * 0.5f, base.y - alto * 0.82f)
            lineTo(base.x + ancho * 0.38f, base.y); lineTo(base.x - ancho * 0.38f, base.y); close()
        }
        drawPath(cesto, lam(JUNCO, o, f))
        clipPath(cesto) {
            var y = base.y - alto * 0.82f
            var k = 0
            while (y < base.y) {
                val paso = alto * 0.16f
                var x = base.x - ancho * 0.55f + (if (k % 2 == 0) 0f else ancho * 0.05f)
                while (x < base.x + ancho * 0.55f) {
                    drawOval(lam(JUNCO_LUZ, o, f).copy(alpha = 0.55f), Offset(x, y + paso * 0.15f), Size(ancho * 0.08f, paso * 0.7f))
                    x += ancho * 0.1f
                }
                drawLine(lam(JUNCO_OSC, o, f), Offset(base.x - ancho, y), Offset(base.x + ancho, y), 0.25f * u)
                y += paso; k++
            }
            drawRect(sombraMueble(if (o) 0.35f else 0.22f), Offset(base.x + ancho * 0.12f, base.y - alto), Size(ancho, alto))
        }
        drawLine(lam(JUNCO_OSC, o, f), Offset(base.x - ancho * 0.52f, base.y - alto * 0.82f), Offset(base.x + ancho * 0.52f, base.y - alto * 0.82f), 0.7f * u, StrokeCap.Round)
    }

    /**
     * La red colgada de dos clavos, [a] y [b]: la malla en rombos que cae hasta [caida], los flotadores de corcho en la
     * relinga de arriba y las pesas de piedra en la de abajo.
     */
    private fun DrawScope.red(a: Offset, b: Offset, caida: Float, o: Boolean, u: Float, semilla: Int, f: Float = 0f) {
        val ancho = b.x - a.x
        fun arriba(t: Float) = Offset(a.x + ancho * t, a.y + (b.y - a.y) * t + caida * 0.18f * sin(PI.toFloat() * t))
        fun abajo(t: Float) = Offset(a.x + ancho * (0.06f + 0.88f * t) + 1.5f * u * sin(t * 9f + semilla), a.y + caida * (0.82f + 0.18f * sin(PI.toFloat() * t)) + caida * 0.08f * azar(semilla, (t * 20).toInt()))
        val hilo = lam(mez(CUERDA, OLIVO_OSC, 0.25f), o, f)
        val filas = 12; val cols = 9
        // Los nudos de la malla, en filas alternadas; cada nudo se une con los dos de la fila de abajo: rombos.
        fun nudo(i: Float, j: Int): Offset {
            val t = (i / cols).coerceIn(0f, 1f); val s = j / filas.toFloat()
            val p0 = arriba(t); val p1 = abajo(t)
            val temblor = 0.35f * u * (azar(semilla + j, (i * 2).toInt()) - 0.5f)
            return Offset(p0.x + (p1.x - p0.x) * s + temblor, p0.y + (p1.y - p0.y) * s + s * s * caida * 0.04f)
        }
        val malla = Path()
        for (j in 0 until filas) {
            val par = j % 2 == 0
            var i = if (par) 0f else 0.5f
            while (i <= cols + 0.01f) {
                val a0 = nudo(i, j)
                val izq = nudo(i - 0.5f, j + 1); val der = nudo(i + 0.5f, j + 1)
                if (i - 0.5f >= -0.01f) { malla.moveTo(a0.x, a0.y); malla.lineTo(izq.x, izq.y) }
                if (i + 0.5f <= cols + 0.01f) { malla.moveTo(a0.x, a0.y); malla.lineTo(der.x, der.y) }
                i += 1f
            }
        }
        translate(0.4f * u, 0.6f * u) { drawPath(malla, sombraMueble(0.2f), style = Stroke(0.2f * u)) }
        drawPath(malla, hilo.copy(alpha = 0.9f), style = Stroke(0.2f * u))
        // La relinga de arriba, con los flotadores, y la de abajo, con las pesas.
        val rel = Path().apply { for (k in 0..20) { val q = arriba(k / 20f); if (k == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) } }
        drawPath(rel, lam(CUERDA, o, f), style = Stroke(0.5f * u))
        for (k in 1..4) {
            val q = arriba(k / 5f)
            drawOval(sombraMueble(0.3f), Offset(q.x - 1.2f * u, q.y - 0.5f * u), Size(2.8f * u, 1.6f * u))
            drawOval(lam(Color(0xFFB48A5A), o, f), Offset(q.x - 1.4f * u, q.y - 0.8f * u), Size(2.8f * u, 1.6f * u))
            drawOval(luzMueble(if (o) 0.06f else 0.35f), Offset(q.x - 1.1f * u, q.y - 0.7f * u), Size(1.4f * u, 0.6f * u))
        }
        for (k in 0..5) {
            val q = abajo(k / 5f)
            drawCircle(sombraMueble(0.3f), 0.9f * u, Offset(q.x + 0.3f * u, q.y + 0.4f * u))
            drawCircle(n(BASALTO, o), 0.9f * u, q)
            drawCircle(luzMueble(if (o) 0.06f else 0.3f), 0.35f * u, Offset(q.x - 0.3f * u, q.y - 0.3f * u))
        }
        listOf(a, b).forEach { clavo(it, o, u, f) }
    }

    /** Un clavo de madera clavado en el muro, con su sombra. */
    private fun DrawScope.clavo(c: Offset, o: Boolean, u: Float, f: Float = 0f) {
        drawCircle(sombraMueble(0.35f), 0.8f * u, Offset(c.x + 0.4f * u, c.y + 0.5f * u))
        drawCircle(lam(OLIVO_OSC, o, f), 0.75f * u, c)
        drawCircle(lam(OLIVO_LUZ, o, f), 0.3f * u, Offset(c.x - 0.25f * u, c.y - 0.25f * u))
    }

    /** El amuleto de jaspe colgado de su cordel, con su bisel de bronce: lo del iniciado, entre lo de todos los días. */
    private fun DrawScope.amuleto(clavoEn: Offset, r: Float, caida: Float, o: Boolean, u: Float, f: Float = 0f) {
        val c = Offset(clavoEn.x, clavoEn.y + caida)
        drawLine(lam(CUERDA, o, f), clavoEn, Offset(c.x - r * 0.55f, c.y - r * 0.8f), 0.25f * u)
        drawLine(lam(CUERDA, o, f), clavoEn, Offset(c.x + r * 0.55f, c.y - r * 0.8f), 0.25f * u)
        drawCircle(sombraMueble(0.35f), r * 1.15f, Offset(c.x + r * 0.25f, c.y + r * 0.35f))
        drawCircle(lam(BRONCE, o, f), r * 1.15f, c)
        drawArc(lam(BRONCE_LUZ, o, f), 170f, 110f, false, Offset(c.x - r * 1.05f, c.y - r * 1.05f), Size(r * 2.1f, r * 2.1f), style = Stroke(r * 0.12f))
        with(TrazosArcontes) { gemaJaspe(c, r) }
        if (o) drawCircle(NOCHE.copy(alpha = 0.55f * (1f - f)), r, c)
        clavo(clavoEn, o, u, f)
    }

    /**
     * La jarra sellada del códice: alta, con la tapa de barro, el cordel cruzado y la bula sobre el nudo. De noche, por la
     * junta de la tapa se escapa un hilo de la luz de la grieta.
     */
    private fun DrawScope.jarraSellada(base: Offset, alto: Float, ancho: Float, o: Boolean, u: Float, f: Float = 0f) {
        val cuerpo = jarra(base, alto, ancho, o, u, f, asas = true, barro = mez(BARRO_CLARO, BARRO, 0.35f))
        val bocaY = base.y - alto
        // La tapa.
        val tapa = Rect(base.x - ancho * 0.32f, bocaY - alto * 0.07f, base.x + ancho * 0.32f, bocaY + alto * 0.02f)
        drawRoundRect(lam(BARRO, o, f), tapa.topLeft, tapa.size, CornerRadius(tapa.height * 0.5f))
        drawRoundRect(luzMueble(if (o) 0.06f else 0.25f), tapa.topLeft, Size(tapa.width * 0.5f, tapa.height * 0.5f), CornerRadius(tapa.height * 0.4f))
        drawCircle(lam(BARRO, o, f), ancho * 0.07f, Offset(base.x, tapa.top - ancho * 0.03f))
        // La junta: un hilo de luz de la grieta, de noche.
        if (o) {
            halo(Offset(base.x, tapa.bottom), ancho * 0.6f, GRIETA_HALO, 0.6f)
            drawLine(TrazosArcontes.GRIETA.copy(alpha = 0.85f), Offset(tapa.left + ancho * 0.06f, tapa.bottom), Offset(tapa.right - ancho * 0.1f, tapa.bottom), 0.3f * u, StrokeCap.Round)
        }
        // El cordel que baja cruzado y la bula sobre el nudo.
        val cord = lam(CUERDA, o, f)
        drawLine(cord, Offset(tapa.left + ancho * 0.04f, tapa.center.y), Offset(base.x + ancho * 0.08f, bocaY + alto * 0.12f), 0.3f * u)
        drawLine(cord, Offset(tapa.right - ancho * 0.04f, tapa.center.y), Offset(base.x - ancho * 0.08f, bocaY + alto * 0.12f), 0.3f * u)
        val bula = Offset(base.x, bocaY + alto * 0.12f)
        drawCircle(sombraMueble(0.35f), ancho * 0.1f, Offset(bula.x + 0.3f * u, bula.y + 0.4f * u))
        drawCircle(lam(Color(0xFF7A3E24), o, f), ancho * 0.1f, bula)
        with(TrazosArcontes) { ouroboros(bula, ancho * 0.055f, ancho * 0.022f, lam(Color(0xFF4E2412), o, f)) }
        drawLine(cord, bula, Offset(bula.x + ancho * 0.04f, bula.y + alto * 0.08f), 0.25f * u)
    }

    /** Un ánfora de pie de punta, apoyada en el muro: el cuerpo largo, los hombros, las asas y el cuello. */
    private fun DrawScope.anfora(base: Offset, alto: Float, ancho: Float, giro: Float, o: Boolean, u: Float, f: Float = 0f) {
        sombraContacto(Offset(base.x + ancho * 0.25f, base.y), ancho * 0.9f, ancho * 0.16f, 0.3f)
        rotate(giro, base) {
            val x = base.x; val y = base.y
            val cuerpo = Path().apply {
                moveTo(x, y)
                cubicTo(x - ancho * 0.1f, y - alto * 0.1f, x - ancho * 0.52f, y - alto * 0.35f, x - ancho * 0.5f, y - alto * 0.66f)
                quadraticTo(x - ancho * 0.48f, y - alto * 0.78f, x - ancho * 0.15f, y - alto * 0.82f)
                lineTo(x - ancho * 0.12f, y - alto * 0.97f); lineTo(x + ancho * 0.12f, y - alto * 0.97f); lineTo(x + ancho * 0.15f, y - alto * 0.82f)
                quadraticTo(x + ancho * 0.48f, y - alto * 0.78f, x + ancho * 0.5f, y - alto * 0.66f)
                cubicTo(x + ancho * 0.52f, y - alto * 0.35f, x + ancho * 0.1f, y - alto * 0.1f, x, y)
                close()
            }
            listOf(-1f, 1f).forEach { s ->
                drawPath(Path().apply { moveTo(x + s * ancho * 0.12f, y - alto * 0.93f); quadraticTo(x + s * ancho * 0.42f, y - alto * 0.94f, x + s * ancho * 0.36f, y - alto * 0.76f) },
                    lam(mez(BARRO_CLARO, Color.Black, 0.25f), o, f), style = Stroke(ancho * 0.08f, cap = StrokeCap.Round))
            }
            drawPath(cuerpo, lam(BARRO_CLARO, o, f))
            clipPath(cuerpo) {
                drawRect(luzMueble(if (o) 0.05f + 0.12f * f else 0.24f), Offset(x - ancho * 0.6f, y - alto), Size(ancho * 0.32f, alto))
                drawRect(sombraMueble(if (o) 0.35f else 0.24f), Offset(x + ancho * 0.1f, y - alto), Size(ancho * 0.6f, alto))
                drawLine(lam(BARRO, o, f).copy(alpha = 0.5f), Offset(x - ancho, y - alto * 0.66f), Offset(x + ancho, y - alto * 0.66f), 0.3f * u)
            }
            drawLine(lam(BARRO_LUZ, o, f), Offset(x - ancho * 0.13f, y - alto * 0.97f), Offset(x + ancho * 0.13f, y - alto * 0.97f), 0.6f * u, StrokeCap.Round)
            drawPath(cuerpo, sombraMueble(0.4f), style = Stroke(0.25f * u))
        }
    }

    /** Un nicho de arco en el muro de caliza: el canto, la hondura en sombra, el poyete y la mancha de hollín si alumbra. */
    private fun DrawScope.nichoArco(r: Rect, o: Boolean, u: Float, hollin: Boolean, f: Float = 0f): Path {
        val radio = r.width / 2
        val hueco = Path().apply {
            moveTo(r.left, r.bottom); lineTo(r.left, r.top + radio)
            arcTo(Rect(r.left, r.top, r.right, r.top + 2 * radio), 180f, 180f, false)
            lineTo(r.right, r.bottom); close()
        }
        if (hollin) for (k in 0 until 4) {
            val c = Offset(r.center.x + r.width * 0.18f, r.top + r.width * 0.12f - r.width * 0.1f * k)
            drawOval(n(Color(0xFF3A2E28), o).copy(alpha = 0.05f), Offset(c.x - r.width * (0.42f - 0.06f * k), c.y - r.width * 0.3f), Size(r.width * (0.84f - 0.12f * k), r.width * 0.6f))
        }
        // El canto encalado alrededor.
        translate(-0.5f * u, -0.5f * u) { drawPath(hueco, luzMueble(if (o) 0.08f else 0.6f), style = Stroke(1.2f * u)) }
        drawPath(hueco, lam(mez(CALIZA, CALIZA_SOMBRA, 0.55f), o, f))
        clipPath(hueco) {
            translate(-1.2f * u, -1.2f * u) { drawPath(hueco, sombraMueble(if (o) 0.6f else 0.42f), style = Stroke(4.5f * u)) }
            drawRect(sombraMueble(if (o) 0.3f else 0.18f), Offset(r.left, r.top), Size(r.width, r.height * 0.3f))
        }
        val poyete = Rect(r.left - 1.2f * u, r.bottom - 0.4f * u, r.right + 1.2f * u, r.bottom + 1.4f * u)
        rect(poyete, lam(CALIZA, o, f))
        drawLine(luzMueble(if (o) 0.1f else 0.6f), Offset(poyete.left, poyete.top), Offset(poyete.right, poyete.top), 0.35f * u)
        drawRect(sombraMueble(0.3f), Offset(poyete.left, poyete.bottom), Size(poyete.width, 0.8f * u))
        return hueco
    }

    /** La lámpara de aceite en su sitio, apagada de día y encendida de noche, con el halo que deja en lo que tiene cerca. */
    private fun DrawScope.lamparaAceite(c: Offset, ancho: Float, o: Boolean) {
        val alto = ancho * 0.36f
        sombraContacto(Offset(c.x + ancho * 0.05f, c.y + alto * 0.46f), ancho * 0.8f, alto * 0.25f, 0.3f)
        with(TrazosArcontes) { lampara(c, ancho, barro = if (o) mez(BARRO_CLARO, CALIDO, 0.2f) else BARRO_CLARO, llama = if (o) 1f else 0f) }
    }

    /** Dónde queda la llama de la lámpara de [lampara] con centro [c] y ancho [ancho]. */
    private fun llamaDe(c: Offset, ancho: Float): Offset = Offset(c.x + ancho * 0.395f, c.y - ancho * 0.36f * 0.4f - ancho * 0.12f)

    /** La grieta de luz por una rendija: pequeña, más tenue de día. */
    private fun DrawScope.grietaEn(desde: Offset, hasta: Offset, ancho: Float, o: Boolean, semilla: Int) {
        with(TrazosArcontes) { grieta(desde, hasta, ancho, if (o) 1f else 0.6f, semilla) }
    }

    // ---------------------------------------------------------------- Social

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.225f, 0.175f, 0.775f, 0.765f)
        MuebleVida.MAPAS -> Rect(0.24f, 0.10f, 0.76f, 0.74f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.065f, 0.27f, 0.935f, 0.815f)
        MuebleVida.NOTAS -> Rect(0.07f, 0.13f, 0.93f, 0.87f)
    }

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val o = oscuro
        val ni = enLienzo(area(MuebleVida.SOCIAL))
        val vigaA = h * 0.068f; val vigaB = h * 0.122f
        val suelo = h * 0.845f

        // El muro encalado y el techo: el cañizo con las cabezas de las vigas y la viga maestra de olivo.
        muro(Rect(0f, vigaB, w, suelo), o, u, e.semilla + 2, listOf(
            Rect(w * 0.035f, h * 0.14f, w * 0.12f, h * 0.175f), Rect(w * 0.8f, h * 0.74f, w * 0.85f, h * 0.81f), Rect(w * 0.885f, h * 0.135f, w * 0.935f, h * 0.17f)))
        canizo(Rect(0f, 0f, w, vigaA + u), o, u)
        var xv = 4f * u; var kv = 0
        while (xv < w + 3f * u) { cabezaViga(Offset(xv, vigaA * (0.56f + 0.08f * azar(e.semilla, kv))), vigaA * 0.44f, o, kv + e.semilla); xv += (14f + 4f * azar(e.semilla, kv + 9)) * u; kv++ }
        viga(Rect(0f, vigaA, w, vigaB), o, u, e.semilla + 1)

        // El piso de tierra apisonada.
        val piso = Rect(0f, suelo, w, h)
        rect(piso, n(TIERRA, o))
        pinceladas(piso, n(TIERRA_OSC, o), e.semilla + 30, 16, u, 0.12f)
        pinceladas(piso, n(ARENA, o), e.semilla + 31, 10, u, 0.12f)
        drawRect(sombraMueble(if (o) 0.4f else 0.25f), Offset(0f, suelo), Size(w, 1.2f * u))
        // De noche, lo que queda lejos de la lámpara se hunde más.
        if (o) drawRect(NOCHE.copy(alpha = 0.28f), Offset.Zero, size)

        // La puerta que da al lago: lo de afuera, las jambas de sillares, el dintel y el umbral.
        val puerta = Rect(w * 0.03f, h * 0.235f, w * 0.17f, suelo)
        clipRect(puerta.left, puerta.top, puerta.right, puerta.bottom) { afuera(puerta, o, u, e.semilla) }
        drawRect(sombraMueble(if (o) 0.4f else 0.3f), puerta.topLeft, Size(puerta.width, 1.6f * u))
        val jamba = 3.6f * u
        listOf(puerta.left - jamba, puerta.right).forEachIndexed { lado, x0 ->
            var y = puerta.top; var k = 0
            while (y < suelo - 0.1f) {
                val alto = minOf((7f + 3f * azar(e.semilla + lado, k)) * u, suelo - y)
                sillar(Rect(x0, y, x0 + jamba, y + alto), o, u)
                y += alto; k++
            }
        }
        // La luz que entra por la puerta pinta el canto de dentro de la jamba derecha.
        drawRect(if (o) ESFERA.copy(alpha = 0.12f) else luzMueble(0.35f), Offset(puerta.right, puerta.top + 2f * u), Size(0.8f * u, puerta.height - 2f * u))
        val dintel = Rect(puerta.left - jamba - 1.6f * u, puerta.top - 4.2f * u, puerta.right + jamba + 1.6f * u, puerta.top)
        viga(dintel, o, u, e.semilla + 9)
        val umbral = Rect(puerta.left - jamba - 0.8f * u, suelo - 0.6f * u, puerta.right + jamba + 0.8f * u, suelo + 2f * u)
        rect(umbral, n(CALIZA, o))
        drawLine(luzMueble(if (o) 0.1f else 0.55f), umbral.topLeft, Offset(umbral.right, umbral.top), 0.4f * u)
        drawRect(sombraMueble(0.3f), Offset(umbral.left, umbral.bottom), Size(umbral.width, 0.8f * u))

        // El rayo que entra por la puerta y cae en el piso: de día, sol con su polvo dorado; de noche, la luz de las estrellas.
        val rayo = Path().apply {
            moveTo(puerta.left + 1f * u, umbral.bottom); lineTo(puerta.right, umbral.bottom)
            lineTo(puerta.right + w * 0.2f, h); lineTo(puerta.left + w * 0.1f, h); close()
        }
        drawPath(rayo, if (o) ESFERA.copy(alpha = 0.14f) else luzMueble(0.32f))
        if (!o) {
            val polvo = Path().apply {
                moveTo(puerta.left, puerta.top + 4f * u); lineTo(puerta.right, puerta.top + 2f * u)
                lineTo(puerta.right + w * 0.2f, h); lineTo(puerta.left + w * 0.1f, h); close()
            }
            clipPath(polvo) {
                for (k in 0 until 22) drawCircle(ORO.copy(alpha = 0.3f + 0.4f * azar(e.semilla, k)), u * (0.12f + 0.18f * azar(e.semilla, k + 30)),
                    Offset(puerta.left + (puerta.width + w * 0.2f) * azar(e.semilla, k + 60), puerta.top + (h - puerta.top) * azar(e.semilla, k + 90)))
            }
        }

        // A la derecha: el nicho de la lámpara con su hollín, el amuleto en su clavo y la repisa de olivo.
        val nl = Rect(w * 0.835f, h * 0.2f, w * 0.935f, h * 0.395f)
        val lampC = Offset(nl.center.x - nl.width * 0.08f, nl.bottom - nl.width * 0.17f)
        val lampW = nl.width * 0.78f
        val llama = llamaDe(lampC, lampW)
        // De noche, la lámpara alumbra lo que tiene cerca: su halo en el muro y en el piso, fuera del lino.
        if (o) fueraDe(ni.inflate(1.4f * u)) { halo(llama, w * 0.3f, LLAMA_HALO, 1.3f); halo(llama, w * 0.12f, LLAMA, 1.2f) }
        nichoArco(nl, o, u, hollin = true, f = 0.7f)
        lamparaAceite(lampC, lampW, o)
        amuleto(Offset(w * 0.958f, h * 0.24f), 1.9f * u, h * 0.075f, o, u, 0.6f)
        // La grieta: una rendija fina en la cal, arriba, entre la viga y el nicho.
        grietaEn(Offset(w * 0.968f, vigaB + 1.5f * u), Offset(w * 0.952f, h * 0.2f), 0.3f * u, o, e.semilla + 4)
        val rep = Rect(w * 0.81f, h * 0.48f, w * 0.975f, h * 0.48f + 2.2f * u)
        listOf(rep.left + rep.width * 0.2f, rep.right - rep.width * 0.2f).forEach { x ->
            val mensula = Path().apply { moveTo(x - 1.2f * u, rep.bottom); lineTo(x + 1.2f * u, rep.bottom); lineTo(x + 0.4f * u, rep.bottom + 4f * u); lineTo(x - 0.4f * u, rep.bottom + 4f * u); close() }
            translate(0.6f * u, 0.6f * u) { drawPath(mensula, sombraMueble(0.3f)) }
            drawPath(mensula, lam(OLIVO_OSC, o, 0.5f))
        }
        drawRect(sombraMueble(0.3f), Offset(rep.left + 0.6f * u, rep.bottom), Size(rep.width, 1.4f * u))
        madera(rep, o, u, e.semilla + 12, f = 0.5f)
        drawLine(luzMueble(if (o) 0.15f else 0.5f), rep.topLeft, Offset(rep.right, rep.top), 0.35f * u)
        // En la repisa: un jarrito con su asa y dos cuencos apilados.
        jarrito(Offset(rep.left + rep.width * 0.28f, rep.top), rep.width * 0.2f, h * 0.085f, o, u, 0.55f)
        cuenco(Offset(rep.left + rep.width * 0.68f, rep.top - h * 0.03f), rep.width * 0.34f, h * 0.03f, o, u, 0.5f)
        cuenco(Offset(rep.left + rep.width * 0.68f, rep.top - h * 0.052f), rep.width * 0.3f, h * 0.025f, o, u, 0.5f, BARRO_LUZ)

        // La red colgada debajo, y en el suelo la jarra sellada, el ánfora apoyada en el muro y el cesto de sal.
        red(Offset(w * 0.815f, h * 0.535f), Offset(w * 0.97f, h * 0.545f), h * 0.2f, o, u, e.semilla, 0.35f)
        anfora(Offset(w * 0.955f, h * 0.975f), h * 0.36f, w * 0.05f, -9f, o, u, 0.12f)
        jarraSellada(Offset(w * 0.875f, h * 0.965f), h * 0.3f, w * 0.06f, o, u, 0.18f)
        cestoSal(Offset(w * 0.8f, h * 0.985f), w * 0.055f, h * 0.075f, o, u, 0.25f)

        // El velo de lino, colgado de su vara: el nicho donde va tu gente.
        val vara = ni.top - 3f * u
        val tela = Rect(ni.left - 1.2f * u, vara, ni.right + 1.2f * u, ni.bottom + 2.8f * u)
        sombraPieza(tela, 3f * u, if (o) 0.45f else 0.3f)
        rect(tela, n(LINO, o))
        // Los pliegues en los bordes, fuera del nicho, y el dobladillo con sus flecos.
        listOf(tela.left to ni.left, ni.right to tela.right).forEachIndexed { lado, (x0, x1) ->
            drawRect(if (lado == 0) luzMueble(if (o) 0.04f else 0.3f) else sombraMueble(if (o) 0.25f else 0.14f), Offset(x0, tela.top), Size(x1 - x0, tela.height))
            drawLine(sombraMueble(0.18f), Offset((x0 + x1) / 2, tela.top + 2f * u), Offset((x0 + x1) / 2, tela.bottom), 0.25f * u)
        }
        val dobladillo = Rect(tela.left, ni.bottom, tela.right, tela.bottom)
        rect(dobladillo, n(LINO_OSC, o))
        drawLine(n(mez(LINO_OSC, OLIVO_OSC, 0.4f), o), Offset(tela.left, dobladillo.top + dobladillo.height * 0.55f), Offset(tela.right, dobladillo.top + dobladillo.height * 0.55f), 0.25f * u)
        var xf = tela.left + 0.6f * u
        while (xf < tela.right) { drawLine(n(LINO_OSC, o), Offset(xf, tela.bottom), Offset(xf + 0.2f * u, tela.bottom + 1.3f * u + 0.5f * u * azar(xf.toInt(), 1)), 0.22f * u); xf += 0.9f * u }
        rect(ni, nicho(e, o, LINO))
        clipRect(ni.left, ni.top, ni.right, ni.bottom) {
            val hebra = (if (o) LINO else FIBRA).copy(alpha = if (o) 0.025f else 0.045f)
            var yy = ni.top + 1.2f * u
            while (yy < ni.bottom) { drawLine(hebra, Offset(ni.left, yy), Offset(ni.right, yy), 0.2f * u); yy += 1.3f * u }
            drawRect(sombraMueble(if (o) 0.12f else 0.07f), ni.topLeft, Size(ni.width, 2.2f * u))
        }
        // Las presillas que abrazan la vara, y la vara de olivo sobre sus dos clavos.
        val presillas = 9
        for (k in 0..presillas) {
            val x = tela.left + tela.width * k / presillas
            drawRoundRect(n(LINO_OSC, o), Offset(x - 1f * u, vara - 1.6f * u), Size(2f * u, 2.6f * u), CornerRadius(0.6f * u))
            drawRoundRect(sombraMueble(0.25f), Offset(x - 1f * u, vara - 1.6f * u), Size(2f * u, 2.6f * u), CornerRadius(0.6f * u), style = Stroke(0.2f * u))
        }
        val rv = Rect(tela.left - 3f * u, vara - 1.6f * u, tela.right + 3f * u, vara + 0.2f * u)
        drawRect(sombraMueble(0.3f), Offset(rv.left + 0.6f * u, rv.bottom), Size(rv.width, 1.1f * u))
        drawRoundRect(n(OLIVO, o), rv.topLeft, rv.size, CornerRadius(rv.height / 2))
        drawLine(luzMueble(if (o) 0.1f else 0.45f), Offset(rv.left + u, rv.top + 0.35f * u), Offset(rv.right - u, rv.top + 0.35f * u), 0.3f * u, StrokeCap.Round)
        listOf(rv.left + 1.6f * u, rv.right - 1.6f * u).forEach { clavo(Offset(it, rv.center.y), o, u) }

        // La estera tendida delante, con la comida del círculo: panes, el cuenco de pescado, la jarra y las copas.
        val eTop = suelo + 2.2f * u; val eBot = h - 0.8f * u
        estera(Offset(ni.left + 2f * u, eTop), Offset(ni.right - 2f * u, eTop), Offset(ni.right + 3f * u, eBot), Offset(ni.left - 3f * u, eBot), o, u)
        val yb = (eTop + eBot) / 2 + 1.2f * u
        listOf(0.32f to 0f, 0.38f to 0.5f, 0.35f to 1f).forEach { (fx, dy) -> pan(Offset(w * fx, yb - dy * 1.8f * u), w * 0.045f, o, u) }
        val bocaPez = cuenco(Offset(w * 0.47f, yb - 2.6f * u), w * 0.085f, h * 0.05f, o, u)
        clipRect(bocaPez.left, bocaPez.top - 3f * u, bocaPez.right, bocaPez.center.y + 0.3f * u) {
            pezAbierto(Offset(bocaPez.center.x - bocaPez.width * 0.12f, bocaPez.center.y - 0.4f * u), bocaPez.width * 0.62f, -8f, o, u)
            pezAbierto(Offset(bocaPez.center.x + bocaPez.width * 0.15f, bocaPez.center.y - 0.2f * u), bocaPez.width * 0.55f, 172f, o, u)
        }
        jarrito(Offset(w * 0.575f, yb + 2.2f * u), w * 0.04f, h * 0.11f, o, u)
        listOf(0.625f, 0.665f).forEach { cuenco(Offset(w * it, yb - 0.4f * u), w * 0.028f, h * 0.035f, o, u, barro = BARRO_LUZ) }
        val olivas = cuenco(Offset(w * 0.71f, yb + 0.8f * u), w * 0.04f, h * 0.03f, o, u)
        for (k in 0 until 5) drawOval(n(ACEITUNA, o), Offset(olivas.left + olivas.width * (0.15f + 0.15f * k), olivas.center.y - 0.9f * u + 0.3f * u * (k % 2)), Size(1.2f * u, 0.9f * u))

        granoMueble(Rect(0f, 0f, w, h), o)
    }

    /** Lo que se ve por la puerta: el cielo, los montes del otro lado, el lago con la barca, la orilla y el pescado colgado. */
    private fun DrawScope.afuera(r: Rect, o: Boolean, u: Float, semilla: Int) {
        val horizonte = r.top + r.height * 0.47f
        val orilla = r.top + r.height * 0.84f
        if (o) {
            rect(r, CIELO)
            with(TrazosArcontes) { esferas(Offset(r.right + r.width * 0.25f, r.top - r.height * 0.05f), r.height * 0.75f, ESFERA.copy(alpha = 0.17f), 0.18f * u) }
            for (k in 0 until 14) drawCircle(TrazosArcontes.GRIETA.copy(alpha = 0.35f + 0.5f * azar(semilla, k)), u * (0.12f + 0.2f * azar(semilla, k + 20)),
                Offset(r.left + r.width * azar(semilla, k + 40), r.top + (horizonte - r.top) * azar(semilla, k + 60)))
        } else {
            rect(r, CIELO_DIA)
            rect(Rect(r.left, horizonte - r.height * 0.16f, r.right, horizonte), mez(CIELO_DIA, BRUMA, 0.5f))
            rect(Rect(r.left, horizonte - r.height * 0.08f, r.right, horizonte), BRUMA)
        }
        // Los montes del otro lado del lago.
        val montes = Path().apply {
            moveTo(r.left, horizonte)
            lineTo(r.left, horizonte - r.height * 0.05f)
            quadraticTo(r.left + r.width * 0.3f, horizonte - r.height * 0.1f, r.left + r.width * 0.55f, horizonte - r.height * 0.06f)
            quadraticTo(r.left + r.width * 0.8f, horizonte - r.height * 0.03f, r.right, horizonte - r.height * 0.07f)
            lineTo(r.right, horizonte); close()
        }
        drawPath(montes, if (o) mez(CIELO, ESFERA, 0.22f) else mez(COLINAS, BRUMA, 0.35f))
        // El lago, más claro hacia el horizonte, con sus brillos.
        rect(Rect(r.left, horizonte, r.right, orilla), n(LAGO, o))
        rect(Rect(r.left, horizonte, r.right, horizonte + (orilla - horizonte) * 0.3f), if (o) mez(n(LAGO, o), CIELO, 0.4f) else mez(LAGO, BRUMA, 0.35f))
        rect(Rect(r.left, orilla - (orilla - horizonte) * 0.3f, r.right, orilla), n(LAGO_HONDO, o).copy(alpha = 0.25f))
        rect(Rect(r.left, orilla - (orilla - horizonte) * 0.15f, r.right, orilla), n(LAGO_HONDO, o).copy(alpha = 0.2f))
        for (k in 0 until 16) {
            val y = horizonte + (orilla - horizonte) * azar(semilla, k + 100)
            val x = r.left + r.width * azar(semilla, k + 120)
            drawLine((if (o) ESFERA else LUZ_LAGO).copy(alpha = if (o) 0.5f else 0.6f), Offset(x, y), Offset(x + (1.5f + 2f * azar(semilla, k)) * u, y), 0.25f * u, StrokeCap.Round)
        }
        // La barca con su vela, a media agua.
        barca(Offset(r.left + r.width * 0.58f, horizonte + (orilla - horizonte) * 0.42f), r.width * 0.5f, o, u)
        // La orilla de arena.
        rect(Rect(r.left, orilla, r.right, r.bottom), n(ARENA, o))
        rect(Rect(r.left, orilla, r.right, orilla + 1.2f * u), n(mez(ARENA, TIERRA_OSC, 0.3f), o))
        drawLine(n(SAL, o).copy(alpha = 0.6f), Offset(r.left, orilla), Offset(r.right, orilla), 0.4f * u)
        for (k in 0 until 7) {
            val q = Offset(r.left + r.width * azar(semilla, k + 200), orilla + 2f * u + (r.bottom - orilla - 3f * u) * azar(semilla, k + 220))
            drawOval(sombraMueble(0.25f), Offset(q.x - 0.5f * u, q.y - 0.1f * u), Size(1.4f * u, 0.6f * u))
            drawOval(n(if (k % 3 == 0) BASALTO else CALIZA_SOMBRA, o), Offset(q.x - 0.7f * u, q.y - 0.4f * u), Size(1.3f * u, 0.7f * u))
        }
        // El pescado colgado a secar bajo el alero, en su cordel.
        val cord = Path().apply { moveTo(r.left - u, r.top + 3f * u); quadraticTo(r.center.x, r.top + 6f * u, r.right + u, r.top + 3.4f * u) }
        drawPath(cord, n(CUERDA, o), style = Stroke(0.25f * u))
        listOf(0.2f, 0.47f, 0.76f).forEachIndexed { k, t ->
            val x = r.left + r.width * t; val y = r.top + 3.2f * u + 2.6f * u * sin(PI.toFloat() * t)
            drawLine(n(CUERDA, o), Offset(x, y), Offset(x, y + 0.8f * u), 0.2f * u)
            pezAbierto(Offset(x, y + 0.8f * u + r.width * 0.11f), r.width * 0.22f, 90f + 6f * (k - 1), o, u)
        }
    }

    /** El brillo del lago de día. */
    private val LUZ_LAGO = Color(0xFFF3EBD2)

    /** Una barca de pesca del lago, de lado: el casco de tablas, el mástil, la vela cuadrada y, de noche, su farol. */
    private fun DrawScope.barca(c: Offset, largo: Float, o: Boolean, u: Float) {
        val casco = Path().apply {
            moveTo(c.x - largo * 0.5f, c.y - largo * 0.08f)
            lineTo(c.x + largo * 0.5f, c.y - largo * 0.1f)
            quadraticTo(c.x + largo * 0.4f, c.y + largo * 0.04f, c.x + largo * 0.25f, c.y + largo * 0.05f)
            lineTo(c.x - largo * 0.3f, c.y + largo * 0.05f)
            quadraticTo(c.x - largo * 0.45f, c.y + largo * 0.03f, c.x - largo * 0.5f, c.y - largo * 0.08f)
            close()
        }
        drawPath(casco, n(OLIVO_OSC, o))
        drawLine(n(OLIVO_LUZ, o), Offset(c.x - largo * 0.48f, c.y - largo * 0.075f), Offset(c.x + largo * 0.48f, c.y - largo * 0.095f), 0.3f * u)
        drawLine(n(OLIVO_VETA, o), Offset(c.x - largo * 0.4f, c.y - largo * 0.01f), Offset(c.x + largo * 0.42f, c.y - largo * 0.03f), 0.2f * u)
        val mastil = c.x - largo * 0.02f
        drawLine(n(OLIVO_OSC, o), Offset(mastil, c.y - largo * 0.08f), Offset(mastil, c.y - largo * 0.72f), 0.35f * u)
        val vela = Path().apply {
            moveTo(mastil - largo * 0.26f, c.y - largo * 0.66f); lineTo(mastil + largo * 0.26f, c.y - largo * 0.66f)
            quadraticTo(mastil + largo * 0.31f, c.y - largo * 0.4f, mastil + largo * 0.24f, c.y - largo * 0.2f)
            lineTo(mastil - largo * 0.22f, c.y - largo * 0.2f)
            quadraticTo(mastil - largo * 0.18f, c.y - largo * 0.42f, mastil - largo * 0.26f, c.y - largo * 0.66f); close()
        }
        drawPath(vela, if (o) mez(LINO, CIELO, 0.72f) else LINO)
        clipPath(vela) { drawRect(sombraMueble(if (o) 0.2f else 0.16f), Offset(mastil, c.y - largo), Size(largo, largo)) }
        drawLine(n(OLIVO_OSC, o), Offset(mastil - largo * 0.28f, c.y - largo * 0.66f), Offset(mastil + largo * 0.28f, c.y - largo * 0.66f), 0.3f * u)
        if (o) {
            val f = Offset(c.x + largo * 0.38f, c.y - largo * 0.16f)
            halo(f, 2.4f * u, LLAMA_HALO, 0.9f)
            drawCircle(LLAMA, 0.35f * u, f)
        }
        // Su reflejo en el agua.
        drawLine((if (o) mez(CIELO, ESFERA, 0.3f) else LAGO_HONDO).copy(alpha = 0.5f), Offset(c.x - largo * 0.3f, c.y + largo * 0.09f), Offset(c.x + largo * 0.25f, c.y + largo * 0.09f), 0.5f * u)
    }

    /** Un sillar de caliza de la jamba: su cara, la luz arriba y a la izquierda, la sombra abajo y la junta. */
    private fun DrawScope.sillar(r: Rect, o: Boolean, u: Float) {
        rect(r, n(CALIZA, o))
        pinceladas(r, n(CALIZA_SOMBRA, o), (r.top + r.left).toInt(), 2, u * 0.4f, 0.2f)
        drawLine(luzMueble(if (o) 0.1f else 0.55f), Offset(r.left, r.top + 0.25f * u), Offset(r.right, r.top + 0.25f * u), 0.4f * u)
        drawLine(luzMueble(if (o) 0.06f else 0.35f), Offset(r.left + 0.25f * u, r.top), Offset(r.left + 0.25f * u, r.bottom), 0.4f * u)
        drawLine(sombraMueble(if (o) 0.5f else 0.35f), Offset(r.left, r.bottom - 0.2f * u), Offset(r.right, r.bottom - 0.2f * u), 0.4f * u)
        drawLine(sombraMueble(if (o) 0.35f else 0.22f), Offset(r.right - 0.25f * u, r.top), Offset(r.right - 0.25f * u, r.bottom), 0.5f * u)
    }

    /** Un jarrito de barro con su asa, de pie sobre [base]. */
    private fun DrawScope.jarrito(base: Offset, ancho: Float, alto: Float, o: Boolean, u: Float, f: Float = 0f) {
        val x = base.x; val y = base.y
        drawPath(Path().apply { moveTo(x + ancho * 0.14f, y - alto * 0.9f); cubicTo(x + ancho * 0.75f, y - alto * 0.95f, x + ancho * 0.7f, y - alto * 0.5f, x + ancho * 0.36f, y - alto * 0.45f) },
            lam(mez(BARRO_CLARO, Color.Black, 0.25f), o, f), style = Stroke(ancho * 0.1f, cap = StrokeCap.Round))
        val cuerpo = Path().apply {
            moveTo(x - ancho * 0.22f, y)
            cubicTo(x - ancho * 0.6f, y - alto * 0.1f, x - ancho * 0.56f, y - alto * 0.55f, x - ancho * 0.16f, y - alto * 0.68f)
            lineTo(x - ancho * 0.14f, y - alto * 0.9f); lineTo(x - ancho * 0.24f, y - alto); lineTo(x + ancho * 0.18f, y - alto)
            lineTo(x + ancho * 0.14f, y - alto * 0.9f); lineTo(x + ancho * 0.16f, y - alto * 0.68f)
            cubicTo(x + ancho * 0.56f, y - alto * 0.55f, x + ancho * 0.6f, y - alto * 0.1f, x + ancho * 0.22f, y)
            close()
        }
        sombraContacto(Offset(x + ancho * 0.1f, y), ancho * 0.9f, ancho * 0.2f, 0.3f)
        drawPath(cuerpo, lam(BARRO_CLARO, o, f))
        clipPath(cuerpo) {
            drawRect(luzMueble(if (o) 0.05f + 0.12f * f else 0.24f), Offset(x - ancho, y - alto), Size(ancho * 0.72f, alto))
            drawRect(sombraMueble(if (o) 0.35f else 0.24f), Offset(x + ancho * 0.1f, y - alto), Size(ancho, alto))
            drawLine(lam(BARRO, o, f).copy(alpha = 0.5f), Offset(x - ancho, y - alto * 0.5f), Offset(x + ancho, y - alto * 0.5f), 0.25f * u)
        }
        drawPath(cuerpo, sombraMueble(0.4f), style = Stroke(0.22f * u))
    }

    /** Un pan redondo y chato, con su corte y la luz arriba. */
    private fun DrawScope.pan(c: Offset, ancho: Float, o: Boolean, u: Float) {
        val alto = ancho * 0.42f
        drawOval(sombraMueble(0.3f), Offset(c.x - ancho * 0.42f, c.y - alto * 0.2f), Size(ancho, alto * 0.8f))
        drawOval(n(mez(PAN, Color.Black, 0.2f), o), Offset(c.x - ancho / 2, c.y - alto / 2), Size(ancho, alto))
        drawOval(n(PAN, o), Offset(c.x - ancho / 2, c.y - alto / 2), Size(ancho, alto * 0.82f))
        drawOval(luzMueble(if (o) 0.06f else 0.3f), Offset(c.x - ancho * 0.36f, c.y - alto * 0.42f), Size(ancho * 0.45f, alto * 0.3f))
        drawLine(n(mez(PAN, Color.Black, 0.35f), o), Offset(c.x - ancho * 0.2f, c.y - alto * 0.1f), Offset(c.x + ancho * 0.22f, c.y - alto * 0.14f), 0.3f * u, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val o = oscuro
        val ni = enLienzo(area(MuebleVida.MAPAS))
        val tabla = Rect(w * 0.012f, h * 0.018f, w * 0.988f, h * 0.8f)
        val canto = h * 0.865f
        val suelo = h * 0.93f

        // El piso de tierra con su estera, y la sombra de la mesa.
        rect(Rect(0f, 0f, w, h), n(TIERRA, o))
        pinceladas(Rect(0f, canto, w, h), n(TIERRA_OSC, o), e.semilla + 3, 10, u, 0.12f)
        estera(Offset(w * 0.06f, suelo - 1.6f * u), Offset(w * 0.94f, suelo - 1.6f * u), Offset(w * 0.98f, h + u), Offset(w * 0.02f, h + u), o, u)
        drawRect(sombraMueble(if (o) 0.5f else 0.35f), Offset(w * 0.03f, canto), Size(w * 0.95f, (suelo - canto) * 0.7f))

        // Los dos caballetes de olivo, con su travesaño.
        listOf(w * 0.12f, w * 0.88f).forEach { x ->
            listOf(-1f, 1f).forEach { s ->
                val pata = Path().apply {
                    moveTo(x + s * 1.2f * u - 1.4f * u, canto); lineTo(x + s * 1.2f * u + 1.4f * u, canto)
                    lineTo(x + s * 6f * u + 1.3f * u, suelo + 0.6f * u); lineTo(x + s * 6f * u - 1.3f * u, suelo + 0.6f * u); close()
                }
                drawPath(pata, n(if (s < 0) OLIVO else OLIVO_OSC, o))
                drawPath(pata, sombraMueble(0.4f), style = Stroke(0.25f * u))
                sombraContacto(Offset(x + s * 6f * u + 0.8f * u, suelo + 0.6f * u), 4f * u, 1.2f * u, 0.35f)
            }
        }
        val trav = Rect(w * 0.12f, (canto + suelo) / 2 - 0.9f * u, w * 0.88f, (canto + suelo) / 2 + 0.9f * u)
        drawRect(sombraMueble(0.3f), Offset(trav.left, trav.bottom), Size(trav.width, 0.8f * u))
        madera(trav, o, u, e.semilla + 40)

        // El canto de la mesa, de frente: la testa de los tablones y su sombra.
        val frente = Rect(tabla.left, tabla.bottom, tabla.right, canto)
        madera(frente, o, u, e.semilla + 50)
        drawRect(sombraMueble(if (o) 0.35f else 0.22f), frente.topLeft, frente.size)
        drawLine(luzMueble(if (o) 0.1f else 0.45f), Offset(frente.left, frente.top + 0.3f * u), Offset(frente.right, frente.top + 0.3f * u), 0.4f * u)
        listOf(0.08f, 0.5f, 0.92f).forEach { fx -> clavo(Offset(frente.left + frente.width * fx, frente.center.y), o, u) }

        // El tablero visto desde arriba: cuatro tablones de olivo con sus juntas y sus clavijas.
        drawRect(sombraMueble(0.3f), Offset(tabla.left + 0.6f * u, tabla.top + 0.8f * u), tabla.size)
        val tablones = 4
        val alto = tabla.height / tablones
        for (k in 0 until tablones) {
            val r = Rect(tabla.left, tabla.top + alto * k, tabla.right, tabla.top + alto * (k + 1))
            madera(r, o, u, e.semilla * 3 + k * 17, densidad = 0.7f)
            drawRect((if (k % 2 == 0) luzMueble(if (o) 0.03f else 0.1f) else sombraMueble(0.05f)), r.topLeft, r.size)
            pinceladas(r, n(OLIVO_LUZ, o), e.semilla + k, 6, u, 0.08f)
            if (k > 0) {
                drawLine(n(OLIVO_VETA, o), Offset(r.left, r.top), Offset(r.right, r.top), 0.5f * u)
                drawLine(luzMueble(if (o) 0.06f else 0.3f), Offset(r.left, r.top + 0.45f * u), Offset(r.right, r.top + 0.45f * u), 0.25f * u)
            }
            listOf(r.left + 2.6f * u, r.right - 2.6f * u).forEach { x ->
                drawCircle(n(OLIVO_VETA, o), 0.75f * u, Offset(x, r.center.y))
                drawCircle(n(OLIVO_OSC, o), 0.45f * u, Offset(x - 0.15f * u, r.center.y - 0.15f * u))
            }
        }
        drawRect(n(OLIVO_VETA, o), tabla.topLeft, tabla.size, style = Stroke(0.4f * u))
        drawLine(luzMueble(if (o) 0.08f else 0.4f), Offset(tabla.left + 0.4f * u, tabla.top + 0.4f * u), Offset(tabla.right, tabla.top + 0.4f * u), 0.4f * u)
        drawLine(luzMueble(if (o) 0.06f else 0.3f), Offset(tabla.left + 0.4f * u, tabla.top + 0.4f * u), Offset(tabla.left + 0.4f * u, tabla.bottom), 0.4f * u)

        // De noche, lo lejano se hunde; la lámpara alumbra su lado de la mesa (fuera del lino).
        val lampC = Offset(w * 0.87f, h * 0.2f)
        val rl = minOf(w * 0.045f, 7f * u)
        if (o) {
            drawRect(NOCHE.copy(alpha = 0.3f), Offset.Zero, size)
            fueraDe(ni.inflate(1.6f * u)) { halo(Offset(lampC.x + rl * 1.6f, lampC.y), w * 0.32f, LLAMA_HALO, 1.4f); halo(Offset(lampC.x + rl * 1.6f, lampC.y), w * 0.12f, LLAMA, 0.8f) }
        }
        val calor = { x: Float, y: Float -> if (!o) 0f else (1f - (Offset(x, y) - lampC).getDistance() / (w * 0.3f)).coerceIn(0f, 1f) }

        // La grieta: la luz que se cuela por la junta de dos tablones, a la derecha.
        val junta = tabla.top + alto * 3
        grietaEn(Offset(w * 0.8f, junta - 0.1f * u), Offset(w * 0.93f, junta + 0.15f * u), 0.25f * u, o, e.semilla + 21)

        // El lienzo de lino tendido sobre la mesa (el nicho), con su sombra, su orilla deshilachada y los cantos de basalto.
        val lienzo = ni.inflate(1.6f * u)
        sombraPieza(lienzo, 2.2f * u, if (o) 0.45f else 0.32f)
        val orilla = Path()
        val pasos = 60
        for (k in 0 until pasos) {
            val t = k / pasos.toFloat()
            val per = 2 * (lienzo.width + lienzo.height)
            var d = per * t
            val q = when {
                d < lienzo.width -> Offset(lienzo.left + d, lienzo.top)
                d < lienzo.width + lienzo.height -> { d -= lienzo.width; Offset(lienzo.right, lienzo.top + d) }
                d < 2 * lienzo.width + lienzo.height -> { d -= lienzo.width + lienzo.height; Offset(lienzo.right - d, lienzo.bottom) }
                else -> { d -= 2 * lienzo.width + lienzo.height; Offset(lienzo.left, lienzo.bottom - d) }
            }
            val j = 0.35f * u * (azar(e.semilla, k) - 0.5f)
            if (k == 0) orilla.moveTo(q.x + j, q.y + j) else orilla.lineTo(q.x + j, q.y - j)
        }
        orilla.close()
        drawPath(orilla, n(LINO, o))
        drawPath(orilla, n(LINO_OSC, o), style = Stroke(0.35f * u))
        clipPath(orilla) {
            // Los pliegues que el lino deja en el margen, fuera del nicho.
            drawRect(n(LINO_OSC, o).copy(alpha = 0.6f), Offset(lienzo.left, lienzo.top), Size(lienzo.width, 1.2f * u))
            drawRect(luzMueble(if (o) 0.04f else 0.4f), Offset(lienzo.left, lienzo.top), Size(1.2f * u, lienzo.height))
        }
        rect(ni, nicho(e, o, LINO))
        clipRect(ni.left, ni.top, ni.right, ni.bottom) {
            val hebra = (if (o) LINO else FIBRA).copy(alpha = if (o) 0.025f else 0.05f)
            var yy = ni.top + 1.1f * u
            while (yy < ni.bottom) { drawLine(hebra, Offset(ni.left, yy), Offset(ni.right, yy), 0.18f * u); yy += 1.2f * u }
            var xx = ni.left + 1.1f * u
            while (xx < ni.right) { drawLine(hebra.copy(alpha = hebra.alpha * 0.6f), Offset(xx, ni.top), Offset(xx, ni.bottom), 0.15f * u); xx += 1.2f * u }
        }
        listOf(Offset(ni.left - 2.8f * u, ni.top - 2.6f * u), Offset(ni.right + 2.8f * u, ni.top - 2.6f * u), Offset(ni.left - 2.8f * u, ni.bottom + 2.6f * u), Offset(ni.right + 2.8f * u, ni.bottom + 2.6f * u)).forEachIndexed { k, c ->
            canto(c, 2.6f * u, o, u, e.semilla + k, calor(c.x, c.y))
        }

        // A la izquierda, el salado: la cama de sal con el pescado abierto, el montón de sal con su pala y el cuchillo.
        val izq = Rect(tabla.left + 2.6f * u, tabla.top + 2.4f * u, ni.left - 4.6f * u, tabla.bottom - 2.4f * u)
        // La artesa de olivo con su cama de sal.
        val artesa = Rect(izq.left, izq.top + izq.height * 0.3f, izq.right, izq.top + izq.height * 0.86f)
        sombraPieza(artesa, 1.8f * u, 0.4f, 1.2f * u)
        drawRoundRect(n(OLIVO_OSC, o), artesa.topLeft, artesa.size, CornerRadius(1.2f * u))
        madera(artesa.deflate(0.3f * u), o, u, e.semilla + 77, densidad = 0.5f)
        drawRoundRect(luzMueble(if (o) 0.06f else 0.3f), artesa.topLeft, artesa.size, CornerRadius(1.2f * u), style = Stroke(0.4f * u))
        val cama = artesa.deflate(1.6f * u)
        rect(cama, n(SAL, o))
        drawRect(sombraMueble(if (o) 0.3f else 0.2f), cama.topLeft, Size(cama.width, 1f * u))
        drawRect(sombraMueble(if (o) 0.2f else 0.14f), cama.topLeft, Size(0.9f * u, cama.height))
        clipRect(cama.left, cama.top, cama.right, cama.bottom) {
            for (k in 0 until 50) drawCircle((if (k % 3 == 0) sombraMueble(0.18f) else luzMueble(if (o) 0.2f else 0.9f)), u * (0.12f + 0.18f * azar(e.semilla, k + 400)),
                Offset(cama.left + cama.width * azar(e.semilla, k + 410), cama.top + cama.height * azar(e.semilla, k + 420)))
        }
        val lp = cama.width * 0.86f
        listOf(0.22f, 0.5f, 0.78f).forEachIndexed { k, fy ->
            pezAbierto(Offset(cama.center.x + (k - 1) * u * 0.6f, cama.top + cama.height * (0.2f + 0.3f * k)), lp, if (k == 1) 183f else 3f, o, u)
        }
        // El cesto de sal visto desde arriba, con la pala de olivo hundida en la sal.
        val rc = minOf(izq.height * 0.13f, izq.width * 0.32f)
        val cc = Offset(izq.left + rc * 1.15f, izq.top + rc * 1.05f)
        drawCircle(sombraMueble(0.35f), rc, Offset(cc.x + 0.8f * u, cc.y + 1.1f * u))
        drawCircle(n(JUNCO_OSC, o), rc, cc)
        for (k in 0 until 4) drawCircle(n(if (k % 2 == 0) JUNCO else JUNCO_LUZ, o), rc * (1f - 0.05f * k) - 0.2f * u, cc, style = Stroke(0.45f * u))
        var ang = 0f
        while (ang < 360f) {
            val a = Math.toRadians(ang.toDouble())
            drawLine(n(JUNCO_OSC, o).copy(alpha = 0.6f), Offset(cc.x + rc * 0.8f * cos(a).toFloat(), cc.y + rc * 0.8f * sin(a).toFloat()), Offset(cc.x + rc * cos(a).toFloat(), cc.y + rc * sin(a).toFloat()), 0.2f * u)
            ang += 14f
        }
        val sal = rc * 0.8f
        drawCircle(n(SAL, o), sal, cc)
        drawArc(sombraMueble(if (o) 0.3f else 0.22f), 180f, 120f, false, Offset(cc.x - sal * 0.9f, cc.y - sal * 0.9f), Size(sal * 1.8f, sal * 1.8f), style = Stroke(sal * 0.2f))
        drawCircle(sombraMueble(if (o) 0.15f else 0.07f), sal * 0.5f, Offset(cc.x + sal * 0.25f, cc.y + sal * 0.3f))
        for (k in 0 until 18) {
            val a = azar(e.semilla, k + 600) * 6.28f; val d = sal * 0.85f * azar(e.semilla, k + 610)
            drawCircle(if (k % 4 == 0) sombraMueble(0.2f) else luzMueble(if (o) 0.2f else 0.9f), u * (0.12f + 0.16f * azar(e.semilla, k + 620)), Offset(cc.x + d * cos(a), cc.y + d * sin(a)))
        }
        val pala0 = Offset(cc.x + sal * 0.1f, cc.y + sal * 0.05f)
        val pala1 = Offset(izq.right - 0.4f * u, cc.y + rc * 0.75f)
        drawLine(sombraMueble(0.3f), Offset(pala0.x + 0.5f * u, pala0.y + 0.7f * u), Offset(pala1.x + 0.5f * u, pala1.y + 0.7f * u), 1.1f * u, StrokeCap.Round)
        drawLine(n(OLIVO, o), pala0, pala1, 1.1f * u, StrokeCap.Round)
        drawLine(luzMueble(if (o) 0.1f else 0.4f), Offset(pala0.x, pala0.y - 0.3f * u), Offset(pala1.x, pala1.y - 0.3f * u), 0.25f * u, StrokeCap.Round)
        drawOval(n(OLIVO_LUZ, o), Offset(pala0.x - 2.6f * u, pala0.y - 1.4f * u), Size(3.4f * u, 2.8f * u))
        drawOval(n(SAL, o), Offset(pala0.x - 2.2f * u, pala0.y - 1f * u), Size(2.6f * u, 2f * u))
        // El cuchillo de hierro con su cacha de olivo y sus remaches.
        cuchillo(Offset(izq.left + izq.width * 0.08f, izq.bottom - 1.2f * u), Offset(izq.right, izq.bottom - 2.6f * u), o, u)

        // A la derecha: la lámpara, el tintero con el cálamo, el cuenco de aceitunas y el amuleto.
        val der = Rect(ni.right + 4.6f * u, tabla.top + 2.4f * u, tabla.right - 2.6f * u, tabla.bottom - 2.4f * u)
        lamparaArriba(lampC, rl, o, u)
        val tinta = Offset(der.left + der.width * 0.3f, der.top + der.height * 0.52f)
        tintero(tinta, minOf(der.width * 0.17f, 3.6f * u), o, u, calor(tinta.x, tinta.y))
        val oliv = Offset(der.left + der.width * 0.7f, der.top + der.height * 0.58f)
        cuencoArriba(oliv, minOf(der.width * 0.22f, 4.6f * u), o, u, calor(oliv.x, oliv.y))
        val am = Offset(der.left + der.width * 0.42f, der.top + der.height * 0.86f)
        val cordel = Path().apply {
            moveTo(am.x, am.y - 1.6f * u)
            cubicTo(am.x - 5f * u, am.y - 4f * u, am.x - 7f * u, am.y + 2f * u, am.x - 3f * u, am.y + 2.6f * u)
            cubicTo(am.x + 2f * u, am.y + 3.4f * u, am.x + 7f * u, am.y + 1f * u, am.x + 4.6f * u, am.y - 2.6f * u)
        }
        drawPath(cordel, sombraMueble(0.25f), style = Stroke(0.35f * u))
        drawPath(cordel, lam(CUERDA, o, calor(am.x, am.y)), style = Stroke(0.28f * u))
        val ra = 1.9f * u
        drawCircle(sombraMueble(0.35f), ra * 1.15f, Offset(am.x + ra * 0.3f, am.y + ra * 0.4f))
        drawCircle(lam(BRONCE, o, calor(am.x, am.y)), ra * 1.15f, am)
        with(TrazosArcontes) { gemaJaspe(am, ra) }
        if (o) drawCircle(NOCHE.copy(alpha = 0.45f * (1f - calor(am.x, am.y))), ra, am)

        granoMueble(Rect(0f, 0f, w, h), o)
    }

    /** Un canto rodado de basalto que sujeta el lienzo: oscuro, con su brillo arriba a la izquierda. */
    private fun DrawScope.canto(c: Offset, r: Float, o: Boolean, u: Float, semilla: Int, f: Float = 0f) {
        val forma = mancha(Rect(c.x - r, c.y - r * 0.82f, c.x + r, c.y + r * 0.82f), semilla, 14, 0.14f)
        translate(0.6f * u, 0.8f * u) { drawPath(forma, sombraMueble(0.4f)) }
        drawPath(forma, lam(BASALTO, o, f))
        clipPath(forma) {
            drawCircle(sombraMueble(0.35f), r, Offset(c.x + r * 0.55f, c.y + r * 0.6f))
            for (k in 0 until 6) drawCircle(lam(Color(0xFF2E2E33), o, f), r * 0.07f, Offset(c.x + r * (azar(semilla, k) - 0.5f) * 1.4f, c.y + r * (azar(semilla, k + 9) - 0.5f) * 1.2f))
        }
        drawOval(luzMueble(if (o) 0.1f + 0.2f * f else 0.35f), Offset(c.x - r * 0.62f, c.y - r * 0.6f), Size(r * 0.7f, r * 0.42f))
    }

    /** Un cuchillo de hierro con la cacha de olivo y dos remaches, tendido de [a] (el mango) a [b] (la punta). */
    private fun DrawScope.cuchillo(a: Offset, b: Offset, o: Boolean, u: Float) {
        val d = b - a
        val largo = d.getDistance()
        val ang = Math.toDegrees(kotlin.math.atan2(d.y, d.x).toDouble()).toFloat()
        rotate(ang, a) {
            val m = largo * 0.36f
            val hoja = Path().apply {
                moveTo(a.x + m, a.y - 0.9f * u); lineTo(a.x + largo * 0.82f, a.y - 0.75f * u)
                quadraticTo(a.x + largo * 0.97f, a.y - 0.4f * u, a.x + largo, a.y + 0.55f * u)
                lineTo(a.x + m, a.y + 0.75f * u); close()
            }
            translate(0.5f * u, 0.8f * u) { drawPath(hoja, sombraMueble(0.3f)); drawRoundRect(sombraMueble(0.3f), Offset(a.x, a.y - 1f * u), Size(m, 2f * u), CornerRadius(u)) }
            drawPath(hoja, n(Color(0xFF7C7E84), o))
            drawLine(luzMueble(if (o) 0.12f else 0.55f), Offset(a.x + m, a.y - 0.6f * u), Offset(a.x + largo * 0.85f, a.y - 0.5f * u), 0.3f * u)
            drawRoundRect(n(OLIVO_OSC, o), Offset(a.x, a.y - 1f * u), Size(m, 2f * u), CornerRadius(u))
            drawLine(luzMueble(if (o) 0.08f else 0.3f), Offset(a.x + u, a.y - 0.55f * u), Offset(a.x + m - u, a.y - 0.55f * u), 0.25f * u, StrokeCap.Round)
            listOf(0.3f, 0.7f).forEach { f -> drawCircle(n(BRONCE_LUZ, o), 0.35f * u, Offset(a.x + m * f, a.y)) }
        }
    }

    /** La lámpara de aceite vista desde arriba: el cuerpo redondo, el disco hundido, el agujero, el pico y la llama de noche. */
    private fun DrawScope.lamparaArriba(c: Offset, r: Float, o: Boolean, u: Float) {
        val f = if (o) 1f else 0f
        val pico = Path().apply {
            moveTo(c.x + r * 0.4f, c.y - r * 0.45f); lineTo(c.x + r * 1.55f, c.y - r * 0.32f)
            quadraticTo(c.x + r * 1.85f, c.y, c.x + r * 1.55f, c.y + r * 0.32f); lineTo(c.x + r * 0.4f, c.y + r * 0.45f); close()
        }
        translate(0.8f * u, 1.1f * u) { drawPath(pico, sombraMueble(0.35f)); drawCircle(sombraMueble(0.35f), r, c) }
        drawPath(pico, lam(BARRO_CLARO, o, f))
        drawCircle(lam(BARRO_CLARO, o, f), r, c)
        drawArc(luzMueble(if (o) 0.2f else 0.45f), 180f, 90f, false, Offset(c.x - r * 0.86f, c.y - r * 0.86f), Size(r * 1.72f, r * 1.72f), style = Stroke(r * 0.12f))
        drawArc(sombraMueble(if (o) 0.25f else 0.3f), 0f, 90f, false, Offset(c.x - r * 0.86f, c.y - r * 0.86f), Size(r * 1.72f, r * 1.72f), style = Stroke(r * 0.14f))
        drawCircle(lam(mez(BARRO_CLARO, Color.Black, 0.22f), o, f), r * 0.55f, c)
        drawCircle(lam(mez(BARRO, Color.Black, 0.5f), o, f), r * 0.16f, Offset(c.x - r * 0.1f, c.y))
        val boca = Offset(c.x + r * 1.5f, c.y)
        drawCircle(lam(mez(BARRO, Color.Black, 0.55f), o, f), r * 0.17f, boca)
        if (o) {
            halo(boca, r * 2.4f, LLAMA_HALO, 1.6f, 10)
            drawPath(Path().apply {
                moveTo(boca.x - r * 0.15f, boca.y); quadraticTo(boca.x + r * 0.2f, boca.y - r * 0.32f, boca.x + r * 0.62f, boca.y - r * 0.05f)
                quadraticTo(boca.x + r * 0.2f, boca.y + r * 0.26f, boca.x - r * 0.15f, boca.y); close()
            }, LLAMA)
            drawCircle(LLAMA_HALO, r * 0.12f, Offset(boca.x + r * 0.1f, boca.y - r * 0.02f))
        } else {
            drawOval(Color(0xFF2A221E).copy(alpha = 0.35f), Offset(boca.x - r * 0.05f, boca.y - r * 0.18f), Size(r * 0.5f, r * 0.36f))
        }
    }

    /** El tintero de barro visto desde arriba, con la tinta de carbón y el cálamo de caña apoyado. */
    private fun DrawScope.tintero(c: Offset, r: Float, o: Boolean, u: Float, f: Float) {
        drawCircle(sombraMueble(0.35f), r, Offset(c.x + 0.6f * u, c.y + 0.8f * u))
        drawCircle(lam(BARRO, o, f), r, c)
        drawArc(luzMueble(if (o) 0.1f + 0.2f * f else 0.4f), 180f, 90f, false, Offset(c.x - r * 0.85f, c.y - r * 0.85f), Size(r * 1.7f, r * 1.7f), style = Stroke(r * 0.12f))
        drawCircle(TINTA, r * 0.55f, c)
        drawCircle(luzMueble(if (o) 0.1f else 0.35f), r * 0.12f, Offset(c.x - r * 0.2f, c.y - r * 0.2f))
        val p0 = Offset(c.x + r * 0.2f, c.y - r * 0.1f); val p1 = Offset(c.x + r * 3.4f, c.y - r * 2.4f)
        drawLine(sombraMueble(0.3f), Offset(p0.x + 0.5f * u, p0.y + 0.7f * u), Offset(p1.x + 0.5f * u, p1.y + 0.7f * u), 0.7f * u, StrokeCap.Round)
        drawLine(lam(JUNCO_LUZ, o, f), p0, p1, 0.65f * u, StrokeCap.Round)
        drawLine(lam(JUNCO_OSC, o, f), Offset(p0.x + (p1.x - p0.x) * 0.5f, p0.y + (p1.y - p0.y) * 0.5f), Offset(p0.x + (p1.x - p0.x) * 0.53f, p0.y + (p1.y - p0.y) * 0.53f), 0.8f * u)
        drawLine(TINTA, p0, Offset(p0.x + (p1.x - p0.x) * 0.08f, p0.y + (p1.y - p0.y) * 0.08f), 0.5f * u, StrokeCap.Round)
    }

    /** Un cuenco de aceitunas visto desde arriba. */
    private fun DrawScope.cuencoArriba(c: Offset, r: Float, o: Boolean, u: Float, f: Float) {
        drawCircle(sombraMueble(0.35f), r, Offset(c.x + 0.7f * u, c.y + 0.9f * u))
        drawCircle(lam(BARRO_CLARO, o, f), r, c)
        drawCircle(lam(mez(BARRO_CLARO, Color.Black, 0.3f), o, f), r * 0.8f, c)
        drawArc(luzMueble(if (o) 0.1f + 0.2f * f else 0.45f), 180f, 90f, false, Offset(c.x - r * 0.92f, c.y - r * 0.92f), Size(r * 1.84f, r * 1.84f), style = Stroke(r * 0.1f))
        for (k in 0 until 9) {
            val a = azar(k, 31) * 6.28f; val d = r * 0.55f * azar(k, 37)
            val q = Offset(c.x + d * cos(a), c.y + d * sin(a))
            drawOval(lam(ACEITUNA, o, f), Offset(q.x - r * 0.18f, q.y - r * 0.13f), Size(r * 0.36f, r * 0.26f))
            drawCircle(luzMueble(if (o) 0.1f else 0.4f), r * 0.05f, Offset(q.x - r * 0.07f, q.y - r * 0.05f))
        }
    }

    // ---------------------------------------------------------------- (provisional para el arnés)

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {}
    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {}
    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {}
    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {}
    override fun tintaNota(oscuro: Boolean): Color = TINTA
}
