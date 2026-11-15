package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.TrazosNouveau
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraContacto
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Art nouveau
// =====================================================================

/**
 * Art nouveau: el mueble de cajones planos de un taller de litografía de París hacia 1900 (`docs/diseno/temas/nouveau.md`),
 * de roble claro cortado al cuarto, con sus radios brillantes, como los muebles de Nancy. Cada tema es un cajón ancho y bajo,
 * de los que guardan las pruebas de imprenta sin doblarlas: en el centro, el portaetiquetas de latón con su arco y su yema,
 * con la cartulina del rótulo, y a los lados dos tiradores de latón en forma de hoja. Los cajones más llenos quedan
 * entreabiertos y asoman las pruebas, cada una con su prueba de color (las cruces de registro y la franja de las tintas);
 * el de las ideas sin tema guarda las maculaturas, revueltas, con una que se sale por el borde. Los costados llevan un tallo
 * tallado que se enrosca arriba, y bajo la tapa corre un friso con dos líneas de látigo. Encima, lo del taller: la piedra
 * litográfica con un iris dibujado al revés y su esponja, la losa de entintar con el rodillo de cuero y la espátula, un frasco
 * de tinta y la lámpara de vidrio de Nancy. En la pared, la ventana de hierro del taller con los tejados de zinc de París y,
 * en un cordel, las pruebas que se secan colgadas con pinzas: la de un iris, la de un cartel a medio imprimir (le faltan el
 * azul, el violeta y el negro) y una tira de pruebas de tinta. De día la luz entra por la ventana, arriba a la izquierda; de
 * noche, el mismo taller en el azul negro de la calle, con algunas buhardillas encendidas, y la única luz cálida es la de la
 * lámpara, que alumbra lo que tiene cerca. Abajo, el zócalo con su faldón curvo, las patas que acaban en voluta, el piso de
 * tablas con manchas de tinta, el cubo de zinc con la esponja de mojar la piedra y las resmas de papel atadas con cordel. Por
 * dentro, el cajón abierto visto desde arriba: la pila de pruebas, la de encima con su imagen apenas impresa (solo el
 * amarillo y el verde), las cruces de registro un poco corridas entre tinta y tinta, las marcas de corte, la franja de color,
 * las notas a lápiz y una hoja de papel de seda. Las fichas son pruebas de imprenta en papel de tina, con el borde barbado
 * abajo y a la izquierda, las marcas de corte, las cruces de registro a los lados y la franja de las tintas; la grande lleva
 * además el timbre seco del taller, una libélula en relieve.
 */
object IdeasNouveau : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta del taller
    /** El roble claro del mueble, cortado al cuarto: su luz, su sombra y lo hondo de las vetas. */
    private val ROBLE = Color(0xFFAE8456)
    private val ROBLE_LUZ = Color(0xFFD2AE7E)
    private val ROBLE_OSC = Color(0xFF775230)
    private val ROBLE_HONDO = Color(0xFF3F2A17)
    /** El latón de los tiradores y los portaetiquetas. */
    private val LATON = Color(0xFFC9A04C)
    private val LATON_LUZ = Color(0xFFF4E0A0)
    private val LATON_OSC = Color(0xFF6E5220)
    /** La cartulina del rótulo y el papel de las pruebas (dos resmas, una más fría). */
    private val CARTA = Color(0xFFF3ECD9)
    private val PAPEL = Color(0xFFF2EBDA)
    private val PAPEL_FRIO = Color(0xFFE6E3D8)
    private val PAPEL_SEDA = Color(0xFFFBF8EE)
    /** El revoque de la pared del taller y el piso de tablas de pino. */
    private val PARED = Color(0xFFDDD7BF)
    private val PARED_OSC = Color(0xFFAFA88D)
    private val PISO = Color(0xFFB39672)
    private val PISO_OSC = Color(0xFF7D6446)
    /** El zinc de los tejados de París, la piedra de las fachadas y las chimeneas de barro. */
    private val ZINC = Color(0xFF8C98A0)
    private val FACHADA = Color(0xFFE0D5BE)
    private val CHIMENEA = Color(0xFFB8693F)
    /** La madera clara de las pinzas y los mangos. */
    private val HAYA = Color(0xFFD8B98A)
    /** La tinta de la prensa (también la del rótulo) y la tinta violeta de la losa. */
    private val TINTA = Color(0xFF2A2530)
    private val TINTA_LOSA = Color(0xFF3F2E62)
    /** La noche: el azul negro de la calle (el fondo del tema de noche) y su cielo. */
    private val NOCHE = Color(0xFF121519)
    private val CIELO_NOCHE = Color(0xFF0C1322)
    /** La luz eléctrica de la lámpara y de las ventanas lejanas. */
    private val LAMPARA = TrazosNouveau.LUZ

    /** Las tintas de la prueba de color, en el orden en que pasan por la prensa: amarillo, rojo, azul, violeta, verde y negro. */
    private val TINTAS = listOf(TrazosNouveau.IRIS_LENGUA, TrazosNouveau.TINTA_ROJA, TrazosNouveau.TINTA_AZUL, TrazosNouveau.IRIS,
        TrazosNouveau.TALLO_SOMBRA, TrazosNouveau.PLUMA)

    /** Un color a [cerca] (0 a 1) de la lámpara: de día, tal cual; de noche, hundido en el azul negro y calentado cerca de ella. */
    private fun luz(c: Color, oscuro: Boolean, cerca: Float): Color {
        if (!oscuro) return c
        val k = cerca.coerceIn(0f, 1f)
        return mezcla(mezcla(c, NOCHE, 0.88f - 0.62f * Math.pow(k.toDouble(), 1.2).toFloat()), LAMPARA, 0.22f * k)
    }

    /** La sombra: de día, violeta hondo, como en los muebles; de noche, el azul negro de la calle. */
    private fun sombra(oscuro: Boolean, a: Float) = if (oscuro) NOCHE.copy(alpha = (a * 1.1f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, solo el filo cálido de la lámpara, según lo cerca que esté. */
    private fun brillo(oscuro: Boolean, a: Float, cerca: Float = 0f) =
        if (oscuro) LAMPARA.copy(alpha = (a * 0.8f * cerca).coerceIn(0f, 1f)) else luzMueble(a)

    override val cols = 1
    override val proporcion = 3.7f
    override val corona = 0.7f
    override val base = 0.27f
    override val lado = 0.065f
    override val hueco = 0.024f

    /** En la corona: dónde empieza la tapa vista desde arriba, su canto de frente y el friso, en fracción de su alto. */
    private const val TAPA = 0.8f
    private const val CANTO = 0.87f
    private const val FRISO = 0.94f
    /** Cuánto baja el frente del cajón al abrirse, en fracción de su alto. */
    private const val SALIDA = 0.2f
    /** En la base: el fondo del zócalo, la línea de la pared con el piso y donde pisan las patas, en fracción de su alto. */
    private const val ZOCALO = 0.36f
    private const val RODAPIE = 0.6f
    private const val PISAN = 0.8f

    // ---------------------------------------------------------------- ayudantes del taller

    /**
     * Una pieza de roble cortado al cuarto dentro de [r]: el color [base], la veta y los radios del roble (la «maillure»),
     * manchas cortas y claras que brillan a lo largo de la veta. [vertical] si la tabla corre de arriba abajo.
     */
    private fun DrawScope.roble(r: Rect, base: Brush, semilla: Int, oscuro: Boolean, vertical: Boolean, cerca: Float = 0.1f) {
        val u = density
        drawRect(base, r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            vetas(r, ROBLE_HONDO.copy(alpha = if (oscuro) 0.45f else 0.26f), semilla, densidad = 0.5f, vertical = vertical, grosor = 0.8f, ondulacion = 0.45f)
            val cuantas = (r.width * r.height / (u * u * 220f)).toInt().coerceIn(2, 160)
            val radio = if (oscuro) luz(ROBLE_LUZ, true, cerca).copy(alpha = 0.28f) else ROBLE_LUZ.copy(alpha = 0.34f)
            for (k in 0 until cuantas) {
                val p = Offset(r.left + r.width * azar(semilla + 7, k), r.top + r.height * azar(semilla + 8, k))
                val l = (4f + 10f * azar(semilla + 9, k)) * u
                val a = (0.4f + 0.8f * azar(semilla + 10, k)) * u
                if (vertical) drawOval(radio, Offset(p.x - a / 2, p.y), Size(a, l)) else drawOval(radio, Offset(p.x, p.y - a / 2), Size(l, a))
            }
        }
    }

    /** El latón, de su brillo a su sombra, de [a] a [b]. */
    private fun laton(oscuro: Boolean, cerca: Float, a: Offset, b: Offset): Brush =
        Brush.linearGradient(listOf(luz(LATON_LUZ, oscuro, cerca), luz(LATON, oscuro, cerca), luz(LATON_OSC, oscuro, cerca)), a, b)

    /** Un trazo tallado en la madera: la sombra arriba a la izquierda y el filo de luz abajo a la derecha, como un surco. */
    private fun DrawScope.tallado(p: Path, grueso: Float, oscuro: Boolean, cerca: Float) {
        val u = density
        translate(-0.5f * u, -0.5f * u) { drawPath(p, sombra(oscuro, if (oscuro) 0.6f else 0.42f), style = Stroke(grueso, cap = StrokeCap.Round, join = StrokeJoin.Round)) }
        translate(0.55f * u, 0.55f * u) { drawPath(p, if (oscuro) LAMPARA.copy(alpha = 0.12f * cerca) else luzMueble(0.32f), style = Stroke(grueso * 0.7f, cap = StrokeCap.Round, join = StrokeJoin.Round)) }
    }

    /** Una cruz de registro: el círculo y la cruz que se imprimen igual en cada piedra para que los colores caigan en su lugar. */
    private fun DrawScope.cruzRegistro(c: Offset, r: Float, color: Color, grosor: Float) {
        drawCircle(color, r * 0.58f, c, style = Stroke(grosor))
        drawLine(color, c - Offset(r, 0f), c + Offset(r, 0f), grosor)
        drawLine(color, c - Offset(0f, r), c + Offset(0f, r), grosor)
    }

    /** La cruz de registro de dos tintas, una apenas corrida de la otra (como sale en las pruebas). */
    private fun DrawScope.cruzDoble(c: Offset, r: Float, grosor: Float, corrida: Float, oscuro: Boolean, cerca: Float) {
        cruzRegistro(c + Offset(corrida, -corrida * 0.4f), r, luz(TrazosNouveau.TINTA_ROJA, oscuro, cerca).copy(alpha = 0.8f), grosor)
        cruzRegistro(c, r, luz(TrazosNouveau.TINTA_AZUL, oscuro, cerca).copy(alpha = 0.85f), grosor)
    }

    /**
     * La franja de color de una prueba: un cuadro por tinta, de [lado] cada uno, desde [x], [y]. Las tintas que ya pasaron
     * por la prensa ([hechas], un bit por tinta) van llenas; las que faltan, solo el contorno del cuadro.
     */
    private fun DrawScope.franjaColor(x: Float, y: Float, lado: Float, hechas: Int, oscuro: Boolean, cerca: Float, vertical: Boolean = false) {
        val u = density
        TINTAS.forEachIndexed { i, t ->
            val p = if (vertical) Offset(x, y + i * lado * 1.18f) else Offset(x + i * lado * 1.18f, y)
            if (hechas and (1 shl i) != 0) drawRect(luz(t, oscuro, cerca), p, Size(lado, lado))
            else drawRect(luz(TINTA, oscuro, cerca).copy(alpha = 0.4f), p, Size(lado, lado), style = Stroke(maxOf(0.4f * u, lado * 0.08f)))
        }
    }

    /** Las marcas de corte en las cuatro esquinas de [r]: dos rayas cortas por esquina, por fuera de la imagen. */
    private fun DrawScope.marcasCorte(r: Rect, largo: Float, color: Color, grosor: Float) {
        listOf(Offset(r.left, r.top) to Offset(-1f, -1f), Offset(r.right, r.top) to Offset(1f, -1f),
            Offset(r.left, r.bottom) to Offset(-1f, 1f), Offset(r.right, r.bottom) to Offset(1f, 1f)).forEach { (p, s) ->
            drawLine(color, p + Offset(s.x * largo * 0.25f, 0f), p + Offset(s.x * largo * 1.25f, 0f), grosor)
            drawLine(color, p + Offset(0f, s.y * largo * 0.25f), p + Offset(0f, s.y * largo * 1.25f), grosor)
        }
    }

    /** Una nota a lápiz: una línea de rasgos sueltos, de [ancho], como una palabra escrita deprisa. */
    private fun DrawScope.garabato(x: Float, y: Float, ancho: Float, alto: Float, color: Color, semilla: Int) {
        val u = density
        val p = Path()
        val pasos = (ancho / (1.6f * u)).toInt().coerceIn(6, 60)
        for (i in 0..pasos) {
            val f = i / pasos.toFloat()
            val q = Offset(x + ancho * f, y + alto * 0.5f * sin(f * 28f + semilla) * (0.5f + 0.5f * azar(semilla, i)) - alto * 0.2f * azar(semilla + 1, i))
            if (i == 0) p.moveTo(q.x, q.y) else p.lineTo(q.x, q.y)
        }
        drawPath(p, color, style = Stroke(0.55f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    // ---------------------------------------------------------------- el latón del cajón

    /**
     * Un tirador de latón en forma de hoja, con centro [c] y [largo], que apunta hacia afuera ([s], −1 a la izquierda y 1 a la
     * derecha): la hoja cuelga un poco por su peso, con su nervio y sus venas, y se ensambla en una roseta con un zarcillo.
     */
    private fun DrawScope.tirador(c: Offset, largo: Float, s: Float, oscuro: Boolean, cerca: Float) {
        val u = density
        val base = c - Offset(s * largo * 0.4f, 0f)
        val punta = c + Offset(s * largo * 0.6f, largo * 0.04f)
        val ancho = largo * 0.4f
        val hoja = TrazosNouveau.hojaLanza(base, punta, ancho, 0.1f * s)
        // La sombra que echa en el frente (la hoja se separa de la madera).
        translate(1.6f * u, 2.6f * u) { drawPath(hoja, sombra(oscuro, if (oscuro) 0.7f else 0.4f)) }
        drawPath(hoja, laton(oscuro, cerca, Offset(c.x - largo * 0.3f, c.y - ancho * 0.6f), Offset(c.x + largo * 0.2f, c.y + ancho * 0.7f)))
        // El nervio, por la misma curva que la hoja, y sus venas hacia la punta.
        val dx = punta.x - base.x; val dy = punta.y - base.y
        val l = kotlin.math.sqrt(dx * dx + dy * dy)
        val nx = -dy / l; val ny = dx / l
        val comba = 0.1f * s
        val medio = TrazosNouveau.bezier(base, Offset(base.x + dx * 0.35f + nx * l * comba, base.y + dy * 0.35f + ny * l * comba),
            Offset(base.x + dx * 0.75f + nx * l * comba * 0.6f, base.y + dy * 0.75f + ny * l * comba * 0.6f), punta, 16)
        val vena = luz(LATON_OSC, oscuro, cerca).copy(alpha = 0.75f)
        drawPath(TrazosNouveau.linea(medio.subList(0, 14)), vena, style = Stroke(0.8f * u, cap = StrokeCap.Round))
        listOf(4, 7, 10).forEach { i ->
            val q = medio[i]
            val hacia = medio[i + 3] - q
            val hl = hacia.getDistance().coerceAtLeast(0.01f)
            val dir = hacia / hl
            for (lado in listOf(-1f, 1f)) {
                val fin = q + dir * (ancho * 0.28f) + Offset(-dir.y, dir.x) * (ancho * 0.32f * lado * (1f - i / 16f))
                drawLine(vena.copy(alpha = 0.5f), q, fin, 0.55f * u, StrokeCap.Round)
            }
        }
        // El canto de la hoja del lado de la luz.
        drawPath(TrazosNouveau.linea(medio.subList(2, 12).map { it + Offset(0f, -ancho * 0.24f) }), brillo(oscuro, 0.55f, cerca + 0.3f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
        // La roseta donde se ensambla, con su zarcillo enroscado hacia adentro.
        val rr = largo * 0.11f
        val rizo = TrazosNouveau.puntosRizo(base + Offset(-s * rr * 0.6f, -rr * 0.4f), Offset(-s, -0.6f), rr * 1.3f, s < 0f, 1.1f, 20)
        translate(1f * u, 1.6f * u) { drawPath(TrazosNouveau.linea(rizo), sombra(oscuro, 0.4f), style = Stroke(1.3f * u, cap = StrokeCap.Round)) }
        drawPath(TrazosNouveau.linea(rizo), luz(LATON, oscuro, cerca), style = Stroke(1.3f * u, cap = StrokeCap.Round))
        drawCircle(sombra(oscuro, 0.45f), rr * 1.1f, base + Offset(0.8f * u, 1.3f * u))
        drawCircle(Brush.radialGradient(listOf(luz(LATON_LUZ, oscuro, cerca), luz(LATON, oscuro, cerca), luz(LATON_OSC, oscuro, cerca)), base - Offset(rr * 0.35f, rr * 0.35f), rr * 1.3f), rr, base)
        drawLine(luz(LATON_OSC, oscuro, cerca), base - Offset(rr * 0.5f, -rr * 0.3f), base + Offset(rr * 0.5f, -rr * 0.3f), 0.6f * u)
    }

    /**
     * El portaetiquetas de latón alrededor de [r]: el marco con su arco al centro y una yema arriba, las dos volutas de abajo,
     * los dos clavos y, dentro, la cartulina del rótulo ([carta]) o, si no tiene, el hueco con la madera.
     */
    private fun DrawScope.portaEtiquetas(r: Rect, carta: Color?, oscuro: Boolean, cerca: Float) {
        val u = density
        val t = maxOf(2.4f * u, r.height * 0.07f)
        val o = Rect(r.left - t, r.top - t, r.right + t, r.bottom + t)
        val rad = t * 1.6f
        val arco = t * 2.1f
        val cx = o.center.x; val aw = o.width
        val contorno = Path().apply {
            moveTo(o.left + rad, o.top)
            lineTo(cx - aw * 0.2f, o.top)
            cubicTo(cx - aw * 0.09f, o.top, cx - aw * 0.07f, o.top - arco, cx, o.top - arco)
            cubicTo(cx + aw * 0.07f, o.top - arco, cx + aw * 0.09f, o.top, cx + aw * 0.2f, o.top)
            lineTo(o.right - rad, o.top); quadraticTo(o.right, o.top, o.right, o.top + rad)
            lineTo(o.right, o.bottom - rad); quadraticTo(o.right, o.bottom, o.right - rad, o.bottom)
            lineTo(o.left + rad, o.bottom); quadraticTo(o.left, o.bottom, o.left, o.bottom - rad)
            lineTo(o.left, o.top + rad); quadraticTo(o.left, o.top, o.left + rad, o.top); close()
        }
        val hueco = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(r, CornerRadius(t * 0.5f))) }
        val marco = Path().apply { addPath(contorno); addPath(hueco); fillType = PathFillType.EvenOdd }
        // La sombra del marco en el frente del cajón.
        translate(1.4f * u, 2.2f * u) { drawPath(contorno, sombra(oscuro, if (oscuro) 0.6f else 0.32f)) }
        // La cartulina, con la sombra que le echa el marco arriba y un filete impreso; o el hueco vacío.
        if (carta != null) {
            drawPath(hueco, carta)
            clipPath(hueco) {
                drawRect(Brush.verticalGradient(listOf(sombra(oscuro, 0.28f), Color.Transparent), r.top, r.top + r.height * 0.3f), r.topLeft, Size(r.width, r.height * 0.3f))
                drawRect(Brush.horizontalGradient(listOf(sombra(oscuro, 0.14f), Color.Transparent), r.left, r.left + t * 3f), r.topLeft, Size(t * 3f, r.height))
                val fi = Rect(r.left + t * 0.9f, r.top + t * 0.9f, r.right - t * 0.9f, r.bottom - t * 0.9f)
                drawRect(luz(TrazosNouveau.TALLO_SOMBRA, oscuro, cerca).copy(alpha = 0.45f), fi.topLeft, fi.size, style = Stroke(0.5f * u))
            }
        } else {
            drawPath(hueco, luz(ROBLE_OSC, oscuro, cerca))
            clipPath(hueco) {
                vetas(r, ROBLE_HONDO.copy(alpha = 0.4f), 91, densidad = 0.6f, grosor = 0.6f, ondulacion = 0.3f)
                drawRect(Brush.verticalGradient(listOf(sombra(oscuro, 0.55f), Color.Transparent), r.top, r.bottom), r.topLeft, r.size)
                // Un resto de la goma de la cartulina que se quitó.
                drawRoundRect(luz(CARTA, oscuro, cerca).copy(alpha = 0.35f), Offset(r.left + r.width * 0.12f, r.top + r.height * 0.55f), Size(r.width * 0.14f, r.height * 0.18f), CornerRadius(1.5f * u))
            }
        }
        // El marco de latón y su canto en luz.
        drawPath(marco, laton(oscuro, cerca, Offset(o.left, o.top - arco), Offset(o.right, o.bottom)))
        drawPath(contorno, luz(LATON_OSC, oscuro, cerca).copy(alpha = 0.8f), style = Stroke(0.7f * u))
        drawPath(hueco, luz(LATON_OSC, oscuro, cerca).copy(alpha = 0.7f), style = Stroke(0.6f * u))
        drawLine(brillo(oscuro, 0.75f, cerca + 0.35f), Offset(o.left + rad, o.top + 0.6f * u), Offset(cx - aw * 0.2f, o.top + 0.6f * u), 0.8f * u, StrokeCap.Round)
        // La yema del arco: una hoja chica que sube.
        val yema = TrazosNouveau.petalo(Offset(cx, o.top - arco + t * 0.6f), Offset(cx, o.top - arco - t * 1.5f), t * 1.4f)
        translate(0.8f * u, 1.2f * u) { drawPath(yema, sombra(oscuro, 0.35f)) }
        drawPath(yema, laton(oscuro, cerca, Offset(cx - t, o.top - arco - t * 1.5f), Offset(cx + t, o.top - arco + t)))
        drawLine(luz(LATON_OSC, oscuro, cerca).copy(alpha = 0.7f), Offset(cx, o.top - arco + t * 0.4f), Offset(cx, o.top - arco - t * 1.1f), 0.5f * u)
        // Las dos volutas de abajo, que se enroscan hacia afuera.
        for (s in listOf(-1f, 1f)) {
            val desde = Offset(cx + s * aw * 0.3f, o.bottom - t * 0.3f)
            val pts = TrazosNouveau.puntosRizo(desde, Offset(s, 0.35f), t * 0.75f, s > 0f, 1.05f, 22)
            val linea = TrazosNouveau.linea(listOf(desde - Offset(s * t * 1.6f, 0f), desde) + pts)
            translate(0.7f * u, 1f * u) { drawPath(linea, sombra(oscuro, 0.35f), style = Stroke(t * 0.42f, cap = StrokeCap.Round)) }
            drawPath(linea, luz(LATON, oscuro, cerca), style = Stroke(t * 0.42f, cap = StrokeCap.Round))
        }
        // Los dos clavos de los lados.
        for (x in listOf(o.left + t * 0.5f, o.right - t * 0.5f)) {
            val p = Offset(x, o.center.y)
            drawCircle(luz(LATON_OSC, oscuro, cerca), t * 0.42f, p)
            drawCircle(brillo(oscuro, 0.6f, cerca + 0.3f), t * 0.16f, p - Offset(t * 0.12f, t * 0.12f))
        }
    }

    // ---------------------------------------------------------------- la pared, la ventana y el cordel

    /**
     * La ventana del taller dentro de [r], de hierro pintado de verde, con su arco rebajado y sus cristales: detrás, el cielo
     * y los tejados de zinc de París con sus buhardillas y chimeneas; de noche, el cielo azul negro y alguna buhardilla
     * encendida. En las esquinas de arriba, dos escuadras de hierro con su línea de látigo.
     */
    private fun DrawScope.ventana(r: Rect, oscuro: Boolean) {
        val u = density
        val flecha = r.width * 0.1f
        val vano = Path().apply {
            moveTo(r.left, r.bottom); lineTo(r.left, r.top + flecha)
            quadraticTo(r.center.x, r.top - flecha, r.right, r.top + flecha)
            lineTo(r.right, r.bottom); close()
        }
        // El derrame del muro alrededor del vano: en sombra arriba y a la derecha, en luz a la izquierda.
        val derrame = 5f * u
        val exterior = Path().apply {
            moveTo(r.left - derrame, r.bottom + derrame); lineTo(r.left - derrame, r.top + flecha - derrame * 0.6f)
            quadraticTo(r.center.x, r.top - flecha - derrame * 1.8f, r.right + derrame, r.top + flecha - derrame * 0.6f)
            lineTo(r.right + derrame, r.bottom + derrame); close()
        }
        drawPath(exterior, if (oscuro) luz(PARED_OSC, true, 0.05f) else mezcla(PARED, PARED_OSC, 0.55f))
        clipPath(vano) {
            // El cielo.
            if (!oscuro) {
                drawRect(Brush.verticalGradient(listOf(Color(0xFFBCCFD3), Color(0xFFDDE4DC), Color(0xFFF1EBD8)), r.top - flecha, r.bottom), Offset(r.left, r.top - flecha), Size(r.width, r.height + flecha))
                for (k in 0 until 3) {
                    val c = Offset(r.left + r.width * (0.2f + 0.33f * k), r.top + r.height * (0.12f + 0.12f * azar(811, k)))
                    for (j in 0 until 4) {
                        val rx = r.width * (0.08f + 0.06f * azar(812 + k, j)); val ry = rx * 0.45f
                        drawOval(Color.White.copy(alpha = 0.55f), Offset(c.x - rx + j * rx * 0.5f - rx * 0.6f, c.y - ry - (if (j % 2 == 1) ry * 0.5f else 0f)), Size(rx * 2, ry * 2))
                    }
                }
                pinceladas(r, Color.White, 813, 10, u, 0.12f)
            } else {
                drawRect(Brush.verticalGradient(listOf(CIELO_NOCHE, Color(0xFF151D2E), Color(0xFF1E2434)), r.top - flecha, r.bottom), Offset(r.left, r.top - flecha), Size(r.width, r.height + flecha))
                for (k in 0 until 26) {
                    val p = Offset(r.left + r.width * azar(815, k), r.top - flecha + r.height * 0.45f * azar(816, k))
                    drawCircle(Color(0xFFE6E2D6).copy(alpha = 0.3f + 0.5f * azar(817, k)), (0.3f + 0.6f * azar(818, k)) * u, p)
                }
                // La luna menguante, chica y fría, arriba a la izquierda.
                val lc = Offset(r.left + r.width * 0.5f, r.top + r.height * 0.17f); val lr = r.width * 0.055f
                mancha(lc, lr * 4f, Color(0xFFCFD6E4).copy(alpha = 0.1f))
                val luna = Path().apply { addOval(Rect(lc, lr)) }
                val corte = Path().apply { addOval(Rect(lc + Offset(lr * 0.45f, -lr * 0.15f), lr * 0.9f)) }
                drawPath(Path().apply { op(luna, corte, androidx.compose.ui.graphics.PathOperation.Difference) }, Color(0xFFE4E3D8))
                // El resplandor de la ciudad encendida en el horizonte.
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, TrazosNouveau.FAROL.copy(alpha = 0.16f)), r.top + r.height * 0.35f, r.top + r.height * 0.62f),
                    Offset(r.left, r.top + r.height * 0.35f), Size(r.width, r.height * 0.27f))
            }
            // Los tejados lejanos: una fila de mansardas de zinc, pálidas en la calima.
            val lejos = r.top + r.height * 0.56f
            val fila = Path().apply {
                moveTo(r.left, r.bottom)
                var x = r.left - r.width * 0.05f; var k = 0
                lineTo(x, lejos)
                while (x < r.right + r.width * 0.1f) {
                    val a = r.width * (0.16f + 0.1f * azar(821, k))
                    val alto = r.height * (0.03f + 0.04f * azar(822, k))
                    lineTo(x + a * 0.15f, lejos - alto); lineTo(x + a * 0.85f, lejos - alto); lineTo(x + a, lejos)
                    x += a; k++
                }
                lineTo(r.right, r.bottom); close()
            }
            drawPath(fila, if (oscuro) Color(0xFF1A2232) else Color(0xFFB8C2C4))
            for (k in 0 until 7) {
                val x = r.left + r.width * (0.05f + 0.14f * k + 0.05f * azar(823, k))
                val a = r.height * (0.03f + 0.03f * azar(824, k))
                drawRect(if (oscuro) Color(0xFF1A2232) else Color(0xFFB3BCBE), Offset(x, lejos - r.height * 0.07f - a), Size(2.4f * u, a + r.height * 0.05f))
                if (oscuro && azar(825, k) > 0.55f) drawRect(LAMPARA.copy(alpha = 0.7f), Offset(x + r.width * 0.04f, lejos - r.height * 0.015f), Size(1.6f * u, 1.8f * u))
            }
            // El edificio de enfrente: la fachada de piedra, su cornisa y su mansarda de zinc con buhardillas y chimeneas.
            val cornisa = r.top + r.height * 0.7f
            val cumbre = r.top + r.height * 0.6f
            drawRect(if (oscuro) Color(0xFF181E2A) else FACHADA, Offset(r.left, cornisa), Size(r.width, r.bottom - cornisa))
            drawRect(if (oscuro) Color(0xFF222838) else mezcla(FACHADA, Color.White, 0.3f), Offset(r.left, cornisa - 1.4f * u), Size(r.width, 2.4f * u))
            val mansarda = Path().apply {
                moveTo(r.left, cornisa - 1.4f * u); lineTo(r.left, cumbre + r.height * 0.02f)
                lineTo(r.left + r.width * 0.06f, cumbre); lineTo(r.right - r.width * 0.06f, cumbre); lineTo(r.right, cumbre + r.height * 0.02f)
                lineTo(r.right, cornisa - 1.4f * u); close()
            }
            drawPath(mansarda, Brush.verticalGradient(if (oscuro) listOf(Color(0xFF1D2534), Color(0xFF141A26)) else listOf(mezcla(ZINC, Color.White, 0.25f), ZINC), cumbre, cornisa))
            // Las juntas de las bandas de zinc.
            var xz = r.left + 3f * u
            while (xz < r.right) { drawLine(if (oscuro) Color(0xFF0F141E) else mezcla(ZINC, TINTA, 0.25f), Offset(xz, cumbre + 1f * u), Offset(xz, cornisa - 2f * u), 0.5f * u); xz += 4.2f * u }
            // Las buhardillas: el tejadillo a dos aguas, las jambas de piedra y la ventana alta; de noche, dos encendidas.
            for (k in 0 until 3) {
                val bx = r.left + r.width * (0.17f + 0.33f * k)
                val bw = r.width * 0.075f; val bh = (cornisa - cumbre) * 0.62f
                val by = cornisa - 2.4f * u - bh
                val piedraB = if (oscuro) Color(0xFF262D3C) else mezcla(FACHADA, Color.White, 0.25f)
                drawRect(piedraB, Offset(bx - bw / 2 - 1.4f * u, by), Size(bw + 2.8f * u, bh + 1f * u))
                drawPath(Path().apply { moveTo(bx - bw / 2 - 2.6f * u, by + 0.5f * u); lineTo(bx, by - bw * 0.62f); lineTo(bx + bw / 2 + 2.6f * u, by + 0.5f * u); close() },
                    if (oscuro) Color(0xFF1F2634) else mezcla(ZINC, Color.White, 0.1f))
                val encendida = oscuro && k != 1
                drawRect(if (encendida) LAMPARA.copy(alpha = 0.85f) else if (oscuro) Color(0xFF0C1019) else Color(0xFF55606A), Offset(bx - bw / 2, by + 1.2f * u), Size(bw, bh - 1.4f * u))
                if (encendida) mancha(Offset(bx, by + bh * 0.5f), bw * 2.2f, LAMPARA.copy(alpha = 0.16f))
                drawLine(piedraB, Offset(bx, by + 1.2f * u), Offset(bx, by + bh), 0.6f * u)
                drawLine(piedraB, Offset(bx - bw / 2, by + bh * 0.45f), Offset(bx + bw / 2, by + bh * 0.45f), 0.6f * u)
            }
            // Las chimeneas con sus caños de barro, y una paloma en la más alta (de día).
            listOf(0.08f to 0.12f, 0.62f to 0.16f, 0.9f to 0.1f).forEachIndexed { k, (fx, fh) ->
                val x = r.left + r.width * fx; val cw = r.width * 0.09f
                val top = cumbre - r.height * fh
                drawRect(if (oscuro) Color(0xFF1C2230) else mezcla(FACHADA, PARED_OSC, 0.25f), Offset(x, top), Size(cw, cumbre - top + 1f * u))
                drawRect(if (oscuro) Color(0xFF262C3A) else mezcla(FACHADA, Color.White, 0.3f), Offset(x - 0.8f * u, top), Size(cw + 1.6f * u, 1.6f * u))
                for (j in 0 until 3) {
                    val px = x + cw * (0.15f + 0.3f * j)
                    drawRect(if (oscuro) Color(0xFF2A2428) else CHIMENEA, Offset(px, top - 3.4f * u), Size(cw * 0.2f, 3.4f * u))
                    drawRect(if (oscuro) Color(0xFF1A1618) else mezcla(CHIMENEA, TINTA, 0.35f), Offset(px + cw * 0.12f, top - 3.4f * u), Size(cw * 0.08f, 3.4f * u))
                }
                if (!oscuro && k == 1) {
                    val pv = Offset(x + cw * 0.85f, top - 3.4f * u)
                    drawOval(Color(0xFF6E7378), Offset(pv.x - 2.2f * u, pv.y - 2.6f * u), Size(4.6f * u, 2.6f * u))
                    drawCircle(Color(0xFF6E7378), 1.1f * u, pv + Offset(2.2f * u, -2.6f * u))
                }
            }
            // El último piso de la fachada: el balcón corrido de hierro y sus ventanas altas con persianas abiertas.
            val yb = cornisa + r.height * 0.13f
            for (k in 0 until 5) {
                val x = r.left + r.width * (0.07f + 0.2f * k)
                val vw = r.width * 0.07f
                val on = oscuro && azar(831, k) > 0.6f
                drawRect(if (oscuro) Color(0xFF1C2230) else mezcla(FACHADA, PARED_OSC, 0.35f), Offset(x - vw * 0.45f, cornisa + r.height * 0.035f), Size(vw * 0.4f, r.height * 0.2f))
                drawRect(if (oscuro) Color(0xFF1C2230) else mezcla(FACHADA, PARED_OSC, 0.35f), Offset(x + vw * 1.05f, cornisa + r.height * 0.035f), Size(vw * 0.4f, r.height * 0.2f))
                drawRect(if (on) TrazosNouveau.FAROL.copy(alpha = 0.7f) else if (oscuro) Color(0xFF0D1118) else Color(0xFF6A747C), Offset(x, cornisa + r.height * 0.035f), Size(vw, r.height * 0.2f))
                drawLine(if (oscuro) Color(0xFF222838) else mezcla(FACHADA, Color.White, 0.3f), Offset(x + vw / 2, cornisa + r.height * 0.035f), Offset(x + vw / 2, cornisa + r.height * 0.235f), 0.5f * u)
            }
            drawLine(if (oscuro) Color(0xFF0A0D14) else TrazosNouveau.HIERRO_SOMBRA, Offset(r.left, yb), Offset(r.right, yb), 0.8f * u)
            var xb = r.left + 1f * u
            while (xb < r.right) { drawLine(if (oscuro) Color(0xFF0A0D14) else TrazosNouveau.HIERRO_SOMBRA, Offset(xb, yb), Offset(xb, yb + r.height * 0.05f), 0.4f * u); xb += 1.8f * u }
            // El reflejo en los cristales.
            listOf(0.2f to 0.12f, 0.58f to 0.07f).forEach { (fx, fw) ->
                val x0 = r.left + r.width * fx
                drawPath(Path().apply {
                    moveTo(x0, r.bottom); lineTo(x0 + r.width * fw, r.bottom)
                    lineTo(x0 + r.width * (fw + 0.35f), r.top - flecha); lineTo(x0 + r.width * 0.35f, r.top - flecha); close()
                }, Color.White.copy(alpha = if (oscuro) 0.03f else 0.13f))
            }
        }
        // Los montantes y los travesaños de hierro.
        val hierro = if (oscuro) luz(TrazosNouveau.HIERRO, true, 0.05f) else TrazosNouveau.HIERRO
        val filo = if (oscuro) Color(0xFF3A4A52).copy(alpha = 0.5f) else mezcla(TrazosNouveau.HIERRO, Color.White, 0.4f)
        clipPath(vano) {
            listOf(1f / 3f, 2f / 3f).forEach { f ->
                val x = r.left + r.width * f
                drawLine(hierro, Offset(x, r.top - flecha), Offset(x, r.bottom), 1.8f * u)
                drawLine(filo, Offset(x - 0.6f * u, r.top - flecha), Offset(x - 0.6f * u, r.bottom), 0.5f * u)
            }
            listOf(0.3f, 0.55f, 0.8f).forEach { f ->
                val y = r.top + r.height * f
                drawLine(hierro, Offset(r.left, y), Offset(r.right, y), 1.8f * u)
                drawLine(filo, Offset(r.left, y - 0.6f * u), Offset(r.right, y - 0.6f * u), 0.5f * u)
            }
        }
        drawPath(vano, hierro, style = Stroke(3.6f * u))
        // Las escuadras de las esquinas de arriba: una línea de látigo de hierro.
        with(TrazosNouveau) {
            latigo(Offset(r.left + 1.5f * u, r.top + r.height * 0.27f), Offset(r.left + r.width * 0.2f, r.top + flecha * 0.55f), r.width * 0.05f, 2.2f * u, hierro, 0.22f, true, false)
            latigo(Offset(r.right - 1.5f * u, r.top + r.height * 0.27f), Offset(r.right - r.width * 0.2f, r.top + flecha * 0.55f), r.width * 0.05f, 2.2f * u, hierro, -0.22f, false, false)
        }
        // El alféizar de piedra.
        val alf = Rect(r.left - derrame - 2f * u, r.bottom, r.right + derrame + 2f * u, r.bottom + 5f * u)
        sombraContacto(Offset(alf.center.x + 3f * u, alf.bottom + 1.5f * u), alf.width, 4f * u, if (oscuro) 0.5f else 0.25f)
        drawRect(if (oscuro) luz(TrazosNouveau.PIEDRA, true, 0.1f) else TrazosNouveau.PIEDRA, alf.topLeft, alf.size)
        drawRect(brillo(oscuro, 0.5f), alf.topLeft, Size(alf.width, 1.2f * u))
        drawRect(sombra(oscuro, 0.3f), Offset(alf.left, alf.bottom - 1f * u), Size(alf.width, 1f * u))
    }

    /** Un punto del cordel (una curva que cuelga de [a] a [b] con su panza en [m]) en [t], de 0 a 1. */
    private fun cordel(a: Offset, m: Offset, b: Offset, t: Float): Offset {
        val s = 1f - t
        return a * (s * s) + m * (2 * s * t) + b * (t * t)
    }

    /** Una pinza de tender de madera clara, con su resorte, de [alto], girada [giro] grados, con la boca en [c]. */
    private fun DrawScope.pinza(c: Offset, alto: Float, giro: Float, oscuro: Boolean, cerca: Float) {
        val u = density
        val a = alto * 0.17f
        rotate(giro, c) {
            translate(1.2f * u, 1.6f * u) { drawRoundRect(sombra(oscuro, 0.35f), Offset(c.x - a, c.y - alto * 0.55f), Size(a * 2, alto), CornerRadius(a * 0.6f)) }
            for (s in listOf(-1f, 1f)) {
                val x = if (s < 0f) c.x - a else c.x + 0.25f * u
                drawRoundRect(luz(if (s < 0f) HAYA else mezcla(HAYA, ROBLE_OSC, 0.2f), oscuro, cerca), Offset(x, c.y - alto * 0.55f), Size(a - 0.25f * u, alto), CornerRadius(a * 0.5f))
            }
            drawLine(luz(ROBLE_HONDO, oscuro, cerca).copy(alpha = 0.6f), Offset(c.x, c.y - alto * 0.55f), Offset(c.x, c.y + alto * 0.45f), 0.5f * u)
            drawLine(brillo(oscuro, 0.5f, cerca + 0.2f), Offset(c.x - a + 0.8f * u, c.y - alto * 0.5f), Offset(c.x - a + 0.8f * u, c.y + alto * 0.35f), 0.6f * u)
            // El resorte de alambre.
            val y = c.y - alto * 0.08f
            drawLine(luz(Color(0xFF8A8C88), oscuro, cerca), Offset(c.x - a - 0.6f * u, y), Offset(c.x + a + 0.6f * u, y), 1.2f * u, StrokeCap.Round)
            drawLine(brillo(oscuro, 0.6f, cerca + 0.2f), Offset(c.x - a, y - 0.4f * u), Offset(c.x + a * 0.2f, y - 0.4f * u), 0.4f * u)
        }
    }

    /**
     * Una prueba colgada a secar, dentro de [r] (sin girar): el papel con su sombra en la pared y la esquina de abajo un poco
     * enroscada. [tipo]: 0, la prueba de un iris con todas sus tintas; 1, un cartel a medio imprimir (el amarillo, el rojo y
     * el verde, sin el azul, el violeta ni el negro); 2, una tira de pruebas de tinta, una tinta sobre otra.
     */
    private fun DrawScope.pruebaColgada(r: Rect, tipo: Int, oscuro: Boolean, cerca: Float) {
        val u = density
        fun c(col: Color) = luz(col, oscuro, cerca)
        val doblez = r.width * 0.16f
        val hoja = Path().apply {
            moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom - doblez)
            lineTo(r.right - doblez, r.bottom); lineTo(r.left, r.bottom); close()
        }
        // La sombra en la pared: de día, corrida a la derecha y abajo; de noche, la lámpara la echa hacia arriba, apenas.
        if (!oscuro) translate(3.5f * u, 5f * u) { drawPath(hoja, sombraMueble(0.16f)) }
        else translate(-2f * u, -3f * u) { drawPath(hoja, NOCHE.copy(alpha = 0.35f)) }
        drawPath(hoja, Brush.linearGradient(listOf(c(mezcla(PAPEL, Color.White, 0.3f)), c(PAPEL), c(mezcla(PAPEL, PARED_OSC, 0.25f))), r.topLeft, r.bottomRight))
        clipPath(hoja) {
            val tinta = c(TINTA)
            when (tipo) {
                0 -> {
                    val m = r.width * 0.14f
                    val img = Rect(r.left + m, r.top + m * 1.1f, r.right - m, r.bottom - m * 1.7f)
                    drawRect(c(TrazosNouveau.IRIS_LENGUA).copy(alpha = 0.3f), img.topLeft, img.size)
                    drawRect(c(TrazosNouveau.TALLO).copy(alpha = 0.35f), Offset(img.left, img.bottom - img.height * 0.18f), Size(img.width, img.height * 0.18f))
                    with(TrazosNouveau) { iris(Offset(img.center.x, img.bottom - 1f * u), img.height * 0.94f, c(IRIS), c(TALLO), c(TALLO_SOMBRA), c(PLUMA).copy(alpha = 0.7f)) }
                    drawRect(tinta.copy(alpha = 0.6f), img.topLeft, img.size, style = Stroke(0.6f * u))
                    cruzDoble(Offset(r.left + m * 0.5f, img.center.y), m * 0.32f, 0.45f * u, 0.4f * u, oscuro, cerca)
                    cruzDoble(Offset(r.right - m * 0.5f, img.center.y), m * 0.32f, 0.45f * u, 0.4f * u, oscuro, cerca)
                    franjaColor(img.left, img.bottom + m * 0.45f, m * 0.42f, 0b111111, oscuro, cerca)
                    garabato(img.right - img.width * 0.3f, img.bottom + m * 0.55f, img.width * 0.28f, m * 0.4f, c(Color(0xFF6A6A70)).copy(alpha = 0.7f), 3)
                }
                1 -> {
                    val m = r.width * 0.11f
                    val img = Rect(r.left + m, r.top + m, r.right - m, r.bottom - m * 1.9f)
                    // La primera piedra, el amarillo: el sol detrás de la figura.
                    drawCircle(c(TrazosNouveau.IRIS_LENGUA).copy(alpha = 0.6f), img.width * 0.36f, Offset(img.center.x, img.top + img.height * 0.5f))
                    drawRect(c(TrazosNouveau.IRIS_LENGUA).copy(alpha = 0.3f), Offset(img.left, img.bottom - img.height * 0.16f), Size(img.width, img.height * 0.16f))
                    // La segunda, el rojo: las letras del título, todavía sin su contorno.
                    drawRect(c(TrazosNouveau.TINTA_ROJA), Offset(img.left + img.width * 0.06f, img.top + img.height * 0.03f), Size(img.width * 0.88f, img.height * 0.075f))
                    drawRect(c(TrazosNouveau.TINTA_ROJA), Offset(img.left + img.width * 0.22f, img.top + img.height * 0.13f), Size(img.width * 0.56f, img.height * 0.035f))
                    drawRect(c(TrazosNouveau.TINTA_ROJA), Offset(img.left + img.width * 0.3f, img.bottom - img.height * 0.11f), Size(img.width * 0.4f, img.height * 0.04f))
                    // La tercera, el verde: el marco de látigo y la libélula, que espera su azul.
                    with(TrazosNouveau) {
                        latigo(Offset(img.left + img.width * 0.08f, img.bottom - img.height * 0.2f), Offset(img.left + img.width * 0.16f, img.top + img.height * 0.3f), img.width * 0.07f, 1.5f * u, c(TALLO_SOMBRA), 0.18f, false, false)
                        latigo(Offset(img.right - img.width * 0.08f, img.bottom - img.height * 0.2f), Offset(img.right - img.width * 0.16f, img.top + img.height * 0.3f), img.width * 0.07f, 1.5f * u, c(TALLO_SOMBRA), -0.18f, true, false)
                        libelula(Offset(img.center.x, img.top + img.height * 0.47f), img.width * 0.5f, -12f, 0f, c(TALLO_SOMBRA), c(mezcla(PAPEL, IRIS_LENGUA, 0.3f)), c(IRIS_LENGUA), null)
                    }
                    cruzDoble(Offset(r.left + m * 0.5f, img.center.y), m * 0.38f, 0.45f * u, 0.5f * u, oscuro, cerca)
                    cruzDoble(Offset(r.right - m * 0.5f, img.center.y), m * 0.38f, 0.45f * u, 0.5f * u, oscuro, cerca)
                    marcasCorte(img, m * 0.35f, tinta.copy(alpha = 0.5f), 0.4f * u)
                    franjaColor(img.left, img.bottom + m * 0.6f, m * 0.5f, 0b010011, oscuro, cerca)
                }
                else -> {
                    val m = r.width * 0.16f
                    val lado = r.width - 2 * m
                    var y = r.top + m * 1.6f
                    // Cada tinta sola y, abajo, las que se montan una sobre otra.
                    TINTAS.forEach { t -> drawRect(c(t), Offset(r.left + m, y), Size(lado, lado * 0.62f)); y += lado * 0.78f }
                    listOf(0 to 1, 0 to 2, 1 to 2).forEach { (a, b) ->
                        if (y + lado * 0.62f < r.bottom - m) {
                            drawRect(c(TINTAS[a]), Offset(r.left + m, y), Size(lado, lado * 0.62f))
                            drawRect(c(TINTAS[b]).copy(alpha = 0.6f), Offset(r.left + m + lado * 0.25f, y), Size(lado * 0.75f, lado * 0.62f))
                            y += lado * 0.78f
                        }
                    }
                    // Las palomitas a lápiz al lado de las que salieron bien.
                    for (k in listOf(0, 2, 3)) {
                        val py = r.top + m * 1.6f + k * lado * 0.78f + lado * 0.3f
                        drawLine(c(Color(0xFF55555C)), Offset(r.right - m * 0.8f, py), Offset(r.right - m * 0.55f, py + m * 0.25f), 0.5f * u)
                        drawLine(c(Color(0xFF55555C)), Offset(r.right - m * 0.55f, py + m * 0.25f), Offset(r.right - m * 0.15f, py - m * 0.3f), 0.5f * u)
                    }
                }
            }
            pinceladas(r, if (oscuro) NOCHE else PARED_OSC, 840 + tipo, 6, u, 0.07f)
        }
        // La esquina doblada: el revés del papel, un poco más oscuro.
        val esquina = Path().apply { moveTo(r.right, r.bottom - doblez); lineTo(r.right - doblez, r.bottom); lineTo(r.right - doblez * 0.85f, r.bottom - doblez * 0.85f); close() }
        drawPath(esquina, c(mezcla(PAPEL, PARED_OSC, 0.5f)))
        drawPath(esquina, sombra(oscuro, 0.2f), style = Stroke(0.4f * u))
    }

    // ---------------------------------------------------------------- lo que hay encima del mueble

    /**
     * La piedra litográfica, una losa gruesa de caliza de [x0] a [x1], de pie en [pie]: el canto con sus vetas y sus
     * desportilladuras, el bisel del borde y, arriba, el dibujo de un iris en lápiz graso, al revés (como se dibuja en la
     * piedra), con el brillo de la goma arábiga y la esponja húmeda en la esquina.
     */
    private fun DrawScope.piedra(x0: Float, x1: Float, pie: Float, grueso: Float, fondo: Float, oscuro: Boolean, cerca: Float) {
        val u = density
        fun c(col: Color) = luz(col, oscuro, cerca)
        val caliza = TrazosNouveau.PIEDRA
        sombraContacto(Offset((x0 + x1) / 2 + 6f * u, pie), (x1 - x0) * 1.15f, 5f * u, if (oscuro) 0.55f else 0.32f)
        val frente = Rect(x0, pie - grueso, x1, pie)
        drawRect(Brush.horizontalGradient(listOf(c(mezcla(caliza, LUZ_MUEBLE, 0.3f)), c(caliza), c(mezcla(caliza, SOMBRA_MUEBLE, 0.3f))), x0, x1), frente.topLeft, frente.size)
        clipRect(frente.left, frente.top, frente.right, frente.bottom) {
            // Las capas de la caliza y sus puntitos.
            for (k in 0 until 5) {
                val y = frente.top + frente.height * (0.2f + 0.16f * k + 0.05f * azar(851, k))
                drawLine(c(mezcla(caliza, SOMBRA_MUEBLE, 0.25f)).copy(alpha = 0.5f), Offset(x0, y), Offset(x1, y + (azar(852, k) - 0.5f) * 2f * u), 0.5f * u)
            }
            for (k in 0 until 40) drawCircle(c(mezcla(caliza, SOMBRA_MUEBLE, 0.4f)).copy(alpha = 0.4f), 0.4f * u, Offset(x0 + (x1 - x0) * azar(853, k), frente.top + frente.height * azar(854, k)))
        }
        // La cara de arriba, en perspectiva, con el bisel claro del borde.
        val e = fondo * 0.18f
        val cara = Path().apply { moveTo(x0, frente.top); lineTo(x0 + e, frente.top - fondo); lineTo(x1 - e, frente.top - fondo); lineTo(x1, frente.top); close() }
        drawPath(cara, Brush.verticalGradient(listOf(c(mezcla(caliza, LUZ_MUEBLE, 0.15f)), c(mezcla(caliza, LUZ_MUEBLE, 0.35f))), frente.top - fondo, frente.top))
        clipPath(cara) {
            // El iris dibujado al revés, aplastado por la perspectiva.
            val ci = Offset(x0 + (x1 - x0) * 0.42f, frente.top - fondo * 0.1f)
            withTransform({ scale(-1f, 0.42f, ci) }) {
                val graso = c(TINTA).copy(alpha = 0.6f)
                val trazo = Stroke(0.9f * u, cap = StrokeCap.Round, join = StrokeJoin.Round)
                val a = fondo * 1.9f
                val fl = Offset(ci.x, ci.y - a * 0.62f)
                for (g in listOf(-0.45f, 0f, 0.45f)) drawPath(TrazosNouveau.petalo(fl, Offset(fl.x + sin(g) * a * 0.3f, fl.y - cos(g) * a * 0.3f), a * 0.2f), graso, style = trazo)
                for (g in listOf(-0.75f, 0.75f)) drawPath(TrazosNouveau.petalo(fl, Offset(fl.x + sin(g) * a * 0.34f, fl.y + cos(g) * a * 0.3f), a * 0.22f), graso, style = trazo)
                drawLine(graso, ci, fl, 0.9f * u)
                drawPath(TrazosNouveau.hojaLanza(ci, Offset(ci.x - a * 0.28f, ci.y - a * 0.62f), a * 0.09f, 0.1f, espada = true), graso, style = trazo)
                drawPath(TrazosNouveau.hojaLanza(ci, Offset(ci.x + a * 0.24f, ci.y - a * 0.5f), a * 0.08f, -0.1f, espada = true), graso, style = trazo)
                // Las letras del cartel, también al revés: unas rayas de lápiz graso.
                for (k in 0 until 2) drawLine(graso, Offset(ci.x - a * 0.9f, ci.y - a * (0.72f - 0.25f * k)), Offset(ci.x - a * (0.4f + 0.15f * k), ci.y - a * (0.72f - 0.25f * k)), 1.4f * u)
            }
            // El brillo de la goma arábiga y lo mojado de la esponja.
            drawOval(brillo(oscuro, 0.35f, cerca + 0.2f), Offset(x0 + (x1 - x0) * 0.12f, frente.top - fondo * 0.8f), Size((x1 - x0) * 0.35f, fondo * 0.3f))
            drawOval(c(mezcla(caliza, SOMBRA_MUEBLE, 0.25f)).copy(alpha = 0.5f), Offset(x1 - (x1 - x0) * 0.32f, frente.top - fondo * 0.7f), Size((x1 - x0) * 0.26f, fondo * 0.6f))
        }
        drawLine(brillo(oscuro, 0.7f, cerca + 0.3f), Offset(x0 + 0.5f * u, frente.top), Offset(x1 - 0.5f * u, frente.top), 1f * u)
        // Dos desportilladuras del canto.
        listOf(x0 to 1f, x1 to -1f).forEach { (x, s) ->
            drawPath(Path().apply { moveTo(x, frente.top + grueso * 0.15f); lineTo(x + s * 3f * u, frente.top + grueso * 0.35f); lineTo(x, frente.top + grueso * 0.55f); close() },
                c(mezcla(caliza, SOMBRA_MUEBLE, 0.45f)))
        }
        // La esponja natural, amarilla, con sus poros.
        val ce = Offset(x1 - (x1 - x0) * 0.17f, frente.top - fondo * 0.45f)
        val er = (x1 - x0) * 0.1f
        drawOval(sombra(oscuro, 0.3f), Offset(ce.x - er * 0.8f, ce.y - er * 0.1f), Size(er * 2.2f, er * 0.8f))
        val esponja = Path().apply {
            for (k in 0..11) {
                val a = k / 12f * 2f * PI.toFloat()
                val rr = er * (0.85f + 0.25f * azar(861, k))
                val p = Offset(ce.x + cos(a) * rr * 1.1f, ce.y - er * 0.4f + sin(a) * rr * 0.65f)
                if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
            }
            close()
        }
        drawPath(esponja, Brush.radialGradient(listOf(c(Color(0xFFE2C27A)), c(Color(0xFFB98F46))), ce - Offset(er * 0.4f, er * 0.8f), er * 1.6f))
        for (k in 0 until 12) drawCircle(c(Color(0xFF7E5E2A)).copy(alpha = 0.7f), (0.4f + 0.5f * azar(862, k)) * u,
            Offset(ce.x + (azar(863, k) - 0.5f) * er * 1.8f, ce.y - er * 0.4f + (azar(864, k) - 0.5f) * er * 1.1f))
    }

    /**
     * La losa de entintar de [x0] a [x1] con la capa de tinta violeta extendida y las huellas del rodillo, la espátula con
     * tinta en la hoja y, encima, el rodillo de cuero con sus dos mangos de madera torneada.
     */
    private fun DrawScope.losaYRodillo(x0: Float, x1: Float, pie: Float, ch: Float, oscuro: Boolean, calor: (Offset) -> Float) {
        val u = density
        val grueso = ch * 0.03f; val fondo = ch * 0.06f
        val cl = calor(Offset((x0 + x1) / 2, pie))
        fun c(col: Color, k: Float = cl) = luz(col, oscuro, k)
        val marmol = Color(0xFFDAD6CB)
        sombraContacto(Offset((x0 + x1) / 2 + 5f * u, pie), (x1 - x0) * 1.1f, 4f * u, if (oscuro) 0.5f else 0.3f)
        drawRect(Brush.horizontalGradient(listOf(c(mezcla(marmol, LUZ_MUEBLE, 0.2f)), c(mezcla(marmol, SOMBRA_MUEBLE, 0.2f))), x0, x1), Offset(x0, pie - grueso), Size(x1 - x0, grueso))
        val e = fondo * 0.2f
        val cara = Path().apply { moveTo(x0, pie - grueso); lineTo(x0 + e, pie - grueso - fondo); lineTo(x1 - e, pie - grueso - fondo); lineTo(x1, pie - grueso); close() }
        drawPath(cara, c(mezcla(marmol, LUZ_MUEBLE, 0.25f)))
        // La capa de tinta, brillante, con las huellas del rodillo.
        val ti = Path().apply {
            moveTo(x0 + (x1 - x0) * 0.1f, pie - grueso - fondo * 0.12f); lineTo(x0 + (x1 - x0) * 0.14f, pie - grueso - fondo * 0.88f)
            lineTo(x1 - (x1 - x0) * 0.2f, pie - grueso - fondo * 0.88f); lineTo(x1 - (x1 - x0) * 0.16f, pie - grueso - fondo * 0.12f); close()
        }
        drawPath(ti, c(TINTA_LOSA))
        clipPath(ti) {
            for (k in 0 until 4) {
                val y = pie - grueso - fondo * (0.25f + 0.18f * k)
                drawLine(brillo(oscuro, 0.22f, cl + 0.4f).copy(alpha = if (oscuro) 0.2f * cl else 0.22f), Offset(x0, y), Offset(x1, y), 0.8f * u)
            }
        }
        drawLine(brillo(oscuro, 0.6f, cl + 0.3f), Offset(x0 + 0.5f * u, pie - grueso), Offset(x1 - 0.5f * u, pie - grueso), 0.8f * u)
        // La espátula, de lado, con tinta en la hoja.
        val ep = Offset(x1 - (x1 - x0) * 0.08f, pie - grueso - fondo * 0.35f)
        rotate(-8f, ep) {
            drawRoundRect(c(HAYA), Offset(ep.x, ep.y - 1.2f * u), Size((x1 - x0) * 0.22f, 2.4f * u), CornerRadius(1.2f * u))
            drawPath(Path().apply { moveTo(ep.x, ep.y - 1.4f * u); lineTo(ep.x - (x1 - x0) * 0.2f, ep.y - 2.2f * u); quadraticTo(ep.x - (x1 - x0) * 0.24f, ep.y, ep.x - (x1 - x0) * 0.2f, ep.y + 1.6f * u); lineTo(ep.x, ep.y + 1.4f * u); close() },
                c(Color(0xFFA5A8A6)))
            drawRect(c(TINTA_LOSA), Offset(ep.x - (x1 - x0) * 0.2f, ep.y - 1.6f * u), Size((x1 - x0) * 0.07f, 3.2f * u))
        }
        // El rodillo, acostado sobre la losa.
        val rr = ch * 0.04f
        val cy = pie - grueso - fondo * 0.55f - rr * 0.5f
        val r0 = x0 + (x1 - x0) * 0.12f; val r1 = x0 + (x1 - x0) * 0.66f
        val crr = calor(Offset((r0 + r1) / 2, cy))
        drawOval(sombra(oscuro, 0.4f), Offset(r0 + 2f * u, cy + rr * 0.55f), Size(r1 - r0, rr * 0.7f))
        // Los mangos: el eje de hierro y la madera torneada.
        for ((x, s) in listOf(r0 to -1f, r1 to 1f)) {
            drawLine(c(Color(0xFF6E6E6A), crr), Offset(x, cy), Offset(x + s * rr * 0.9f, cy), rr * 0.3f)
            val m0 = x + s * rr * 0.8f
            val mango = Path().apply {
                moveTo(m0, cy - rr * 0.32f)
                cubicTo(m0 + s * rr * 0.6f, cy - rr * 0.55f, m0 + s * rr * 1.6f, cy - rr * 0.4f, m0 + s * rr * 2.1f, cy - rr * 0.3f)
                quadraticTo(m0 + s * rr * 2.5f, cy, m0 + s * rr * 2.1f, cy + rr * 0.3f)
                cubicTo(m0 + s * rr * 1.6f, cy + rr * 0.4f, m0 + s * rr * 0.6f, cy + rr * 0.55f, m0, cy + rr * 0.32f); close()
            }
            drawPath(mango, Brush.verticalGradient(listOf(c(mezcla(HAYA, LUZ_MUEBLE, 0.4f), crr), c(HAYA, crr), c(mezcla(HAYA, ROBLE_HONDO, 0.45f), crr)), cy - rr * 0.55f, cy + rr * 0.55f))
            drawLine(c(mezcla(HAYA, ROBLE_HONDO, 0.5f), crr), Offset(m0 + s * rr * 0.25f, cy - rr * 0.4f), Offset(m0 + s * rr * 0.25f, cy + rr * 0.4f), 0.6f * u)
        }
        // El cilindro de cuero, con la tinta abajo y el brillo arriba.
        val cil = Rect(r0, cy - rr, r1, cy + rr)
        drawRoundRect(Brush.verticalGradient(listOf(c(Color(0xFF9C7350), crr), c(Color(0xFF6B4A30), crr), c(mezcla(Color(0xFF6B4A30), TINTA_LOSA, 0.7f), crr), c(TINTA_LOSA, crr)), cil.top, cil.bottom),
            cil.topLeft, cil.size, CornerRadius(rr * 0.25f))
        drawLine(brillo(oscuro, 0.5f, crr + 0.4f), Offset(cil.left + rr * 0.3f, cy - rr * 0.55f), Offset(cil.right - rr * 0.3f, cy - rr * 0.55f), rr * 0.18f, StrokeCap.Round)
        // Las costuras del cuero.
        for (k in 1..2) drawLine(c(Color(0xFF4A3220), crr).copy(alpha = 0.6f), Offset(cil.left + cil.width * k / 3f, cil.top), Offset(cil.left + cil.width * k / 3f, cil.bottom), 0.5f * u)
        drawOval(c(mezcla(Color(0xFF6B4A30), ROBLE_HONDO, 0.4f), crr), Offset(cil.left - rr * 0.18f, cil.top), Size(rr * 0.36f, rr * 2f))
        drawOval(c(mezcla(Color(0xFF6B4A30), ROBLE_HONDO, 0.2f), crr), Offset(cil.right - rr * 0.18f, cil.top), Size(rr * 0.36f, rr * 2f))
    }

    /** El frasco de tinta, chato, de vidrio oscuro con la tinta adentro, su corcho y su etiqueta de papel con una libélula. */
    private fun DrawScope.frasco(pie: Offset, alto: Float, oscuro: Boolean, cerca: Float) {
        val u = density
        fun c(col: Color) = luz(col, oscuro, cerca)
        val a = alto * 0.42f
        sombraContacto(Offset(pie.x + 3f * u, pie.y), a * 2.2f, 4f * u, if (oscuro) 0.5f else 0.32f)
        val cuerpo = Path().apply {
            moveTo(pie.x - a, pie.y)
            lineTo(pie.x - a, pie.y - alto * 0.5f)
            cubicTo(pie.x - a, pie.y - alto * 0.66f, pie.x - a * 0.4f, pie.y - alto * 0.7f, pie.x - a * 0.32f, pie.y - alto * 0.78f)
            lineTo(pie.x - a * 0.32f, pie.y - alto * 0.86f); lineTo(pie.x + a * 0.32f, pie.y - alto * 0.86f); lineTo(pie.x + a * 0.32f, pie.y - alto * 0.78f)
            cubicTo(pie.x + a * 0.4f, pie.y - alto * 0.7f, pie.x + a, pie.y - alto * 0.66f, pie.x + a, pie.y - alto * 0.5f)
            lineTo(pie.x + a, pie.y); close()
        }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(c(Color(0xFF4E3E66)), c(Color(0xFF2A2236)), c(Color(0xFF17121F))), pie.x - a, pie.x + a))
        // El corcho.
        drawRoundRect(c(Color(0xFFB08A5A)), Offset(pie.x - a * 0.36f, pie.y - alto), Size(a * 0.72f, alto * 0.17f), CornerRadius(1.2f * u))
        drawLine(brillo(oscuro, 0.4f, cerca + 0.3f), Offset(pie.x - a * 0.3f, pie.y - alto + 0.8f * u), Offset(pie.x + a * 0.2f, pie.y - alto + 0.8f * u), 0.6f * u)
        // La etiqueta, con su filete y una libélula chica.
        val et = Rect(pie.x - a * 0.78f, pie.y - alto * 0.46f, pie.x + a * 0.78f, pie.y - alto * 0.12f)
        drawRect(c(CARTA), et.topLeft, et.size)
        drawRect(c(TrazosNouveau.TALLO_SOMBRA).copy(alpha = 0.7f), Offset(et.left + 1f * u, et.top + 1f * u), Size(et.width - 2f * u, et.height - 2f * u), style = Stroke(0.4f * u))
        with(TrazosNouveau) { libelula(et.center, et.height * 0.62f, 90f, 0f, c(LIBELULA), c(NACAR), c(ORO), null) }
        // El brillo del vidrio.
        drawLine(brillo(oscuro, 0.55f, cerca + 0.5f).copy(alpha = if (oscuro) 0.5f * cerca else 0.45f), Offset(pie.x - a * 0.7f, pie.y - alto * 0.62f), Offset(pie.x - a * 0.7f, pie.y - alto * 0.06f), 1.2f * u, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- el mueble entero

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width; val alto = size.height
        val ch = d.corona.height
        val b = d.base; val hb = b.height
        val tapa = ch * TAPA; val canto = ch * CANTO; val friso = ch * FRISO
        val pie = tapa + (canto - tapa) * 0.62f
        // La lámpara, encima del mueble a la derecha: de noche, la única luz cálida del taller.
        val altoLampara = ch * 0.4f
        val pieLampara = Offset(w * 0.865f, pie)
        val foco = Offset(pieLampara.x, pie - altoLampara * 0.62f)
        val alcance = w * 0.76f
        fun calor(p: Offset) = (1f - (p - foco).getDistance() / alcance).coerceIn(0f, 1f)
        fun c(col: Color, p: Offset) = luz(col, oscuro, calor(p))
        /** El color de una pieza larga, de arriba abajo: de noche, más tibio cuanto más cerca de la lámpara. */
        fun largo(col: Color, x: Float, y0: Float, y1: Float): Brush =
            if (!oscuro) Brush.verticalGradient(listOf(col, col), y0, y1) else Brush.verticalGradient(List(6) { i -> c(col, Offset(x, y0 + (y1 - y0) * i / 5f)) }, y0, y1)
        val rodapie = b.top + hb * RODAPIE
        val pisan = b.top + hb * PISAN
        val cuerpo = Rect(w * 0.012f, friso, w * 0.988f, b.top)
        val x0 = d.huecos.first().left; val x1 = d.huecos.first().right

        // ---- la pared del taller
        if (!oscuro) {
            drawRect(Brush.horizontalGradient(listOf(mezcla(PARED, LUZ_MUEBLE, 0.3f), PARED, mezcla(PARED, PARED_OSC, 0.45f)), 0f, w), Offset.Zero, Size(w, rodapie))
            pinceladas(Rect(0f, 0f, w, rodapie), PARED_OSC, 801, 30, u, 0.1f)
            pinceladas(Rect(0f, 0f, w, rodapie), Color.White, 802, 16, u, 0.1f)
        } else {
            drawRect(Brush.radialGradient(listOf(luz(PARED, true, 0.82f), luz(PARED, true, 0.48f), luz(PARED, true, 0.16f), mezcla(NOCHE, CIELO_NOCHE, 0.4f)), foco, alcance),
                Offset.Zero, Size(w, rodapie))
            pinceladas(Rect(0f, 0f, w, rodapie), NOCHE, 801, 26, u, 0.12f)
        }
        // La ventana del taller, arriba a la izquierda (de ahí viene la luz del día), y su claridad en la pared.
        val vr = Rect(w * 0.05f, ch * 0.06f, w * 0.38f, ch * 0.66f)
        if (!oscuro) mancha(Offset(vr.center.x, vr.center.y), w * 0.42f, luzMueble(0.35f))
        ventana(vr, oscuro)

        // ---- el cordel con las pruebas que se secan
        val ca = Offset(vr.right + 1f * u, ch * 0.13f)
        val cb = Offset(w + 2f * u, ch * 0.09f)
        val cm = Offset(w * 0.7f, ch * 0.21f)
        fun tCordel(x: Float) = ((x - ca.x) / (cb.x - ca.x)).coerceIn(0f, 1f)
        val colgadas = listOf(Triple(0.425f, 0.585f, 0.27f), Triple(0.61f, 0.79f, 0.25f), Triple(0.815f, 0.885f, 0.21f))
        // El cordel, con su sombra en la pared.
        val cuerda = Path().apply { for (i in 0..30) { val p = cordel(ca, cm, cb, i / 30f); if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) } }
        if (!oscuro) translate(2f * u, 4f * u) { drawPath(cuerda, sombraMueble(0.14f), style = Stroke(1f * u)) }
        drawPath(cuerda, c(Color(0xFFB8A988), Offset(w * 0.7f, ch * 0.2f)), style = Stroke(0.9f * u))
        // El clavo de la derecha donde se ata... queda fuera; el de la izquierda, en el marco de la ventana.
        drawCircle(c(TrazosNouveau.HIERRO_SOMBRA, ca), 1.6f * u, ca)
        colgadas.forEachIndexed { i, (fa, fb, fh) ->
            val pa = cordel(ca, cm, cb, tCordel(w * fa)); val pb = cordel(ca, cm, cb, tCordel(w * fb))
            val giro = Math.toDegrees(atan2((pb.y - pa.y).toDouble(), (pb.x - pa.x).toDouble())).toFloat()
            val ancho = (pb - pa).getDistance()
            val r = Rect(pa.x, pa.y + 1.2f * u, pa.x + ancho, pa.y + 1.2f * u + ch * fh)
            val cerca = calor(r.center)
            rotate(giro * 0.8f, pa) { pruebaColgada(r, i, oscuro, cerca) }
            // Las pinzas en las esquinas de arriba (la tira chica lleva una sola).
            val pinzas = if (i == 2) listOf(pa + (pb - pa) * 0.5f) else listOf(pa + (pb - pa) * 0.12f, pa + (pb - pa) * 0.88f)
            pinzas.forEachIndexed { j, p -> pinza(p + Offset(0f, 1.5f * u), ch * 0.075f, (if (j == 0) -6f else 5f) + giro, oscuro, cerca) }
        }

        // ---- el cuerpo del mueble: los costados, los travesaños y el hueco de cada cajón
        roble(cuerpo, largo(mezcla(ROBLE, ROBLE_OSC, 0.3f), w * 0.5f, cuerpo.top, cuerpo.bottom), 870, oscuro, vertical = true, cerca = 0.15f)
        drawRect(Brush.horizontalGradient(listOf(brillo(oscuro, 0.18f), Color.Transparent, sombra(oscuro, if (oscuro) 0.3f else 0.22f)), cuerpo.left, cuerpo.right), cuerpo.topLeft, cuerpo.size)
        d.huecos.forEach { h ->
            drawRect(sombra(oscuro, if (oscuro) 0.85f else 0.6f), Offset(h.left - 1.6f * u, h.top - 1.6f * u), Size(h.width + 3.2f * u, h.height + 3.2f * u))
            drawLine(brillo(oscuro, 0.45f, 0.3f), Offset(h.left - 1.6f * u, h.bottom + 2.2f * u), Offset(h.right + 1.6f * u, h.bottom + 2.2f * u), 0.9f * u)
        }
        // Los costados: dos pilastras con un tallo tallado que se enrosca arriba y echa una hoja abajo.
        listOf(Rect(cuerpo.left, cuerpo.top, x0 - 2.4f * u, cuerpo.bottom) to 1f, Rect(x1 + 2.4f * u, cuerpo.top, cuerpo.right, cuerpo.bottom) to -1f).forEachIndexed { i, (pr, s) ->
            roble(pr, largo(ROBLE, pr.center.x, pr.top, pr.bottom), 880 + i, oscuro, vertical = true, cerca = 0.2f)
            drawRect(Brush.horizontalGradient(listOf(brillo(oscuro, 0.3f, 0.3f), Color.Transparent, sombra(oscuro, 0.3f)), pr.left, pr.right), pr.topLeft, pr.size)
            val cx = pr.center.x
            val arriba = pr.top + 10f * u; val abajo = pr.bottom - 12f * u
            val tallo = Path().apply {
                moveTo(cx, abajo)
                cubicTo(cx + s * pr.width * 0.18f, abajo - (abajo - arriba) * 0.35f, cx - s * pr.width * 0.18f, arriba + (abajo - arriba) * 0.35f, cx, arriba + pr.width * 0.5f)
            }
            val cerca = calor(Offset(cx, pr.top))
            tallado(tallo, 1.3f * u, oscuro, cerca)
            val rizo = TrazosNouveau.puntosRizo(Offset(cx, arriba + pr.width * 0.5f), Offset(0f, -1f), pr.width * 0.24f, s > 0f, 1.2f, 24)
            tallado(TrazosNouveau.linea(listOf(Offset(cx, arriba + pr.width * 0.5f)) + rizo), 1.3f * u, oscuro, cerca)
            tallado(TrazosNouveau.hojaLanza(Offset(cx, abajo - 2f * u), Offset(cx + s * pr.width * 0.28f, abajo - pr.width * 0.95f), pr.width * 0.28f, 0.1f * s), 0.9f * u, oscuro, cerca)
            tallado(TrazosNouveau.hojaLanza(Offset(cx, abajo - (abajo - arriba) * 0.5f), Offset(cx - s * pr.width * 0.3f, abajo - (abajo - arriba) * 0.5f - pr.width * 0.9f), pr.width * 0.24f, -0.1f * s), 0.9f * u, oscuro, cerca)
        }

        // ---- el friso bajo la tapa, con dos líneas de látigo talladas que nacen de una yema al centro
        val fr = Rect(cuerpo.left, friso, cuerpo.right, d.huecos.first().top - 2.4f * u)
        roble(fr, largo(ROBLE, w / 2, fr.top, fr.bottom), 890, oscuro, vertical = false, cerca = 0.4f)
        drawRect(sombra(oscuro, 0.35f), Offset(fr.left, fr.bottom - 1f * u), Size(fr.width, 1f * u))
        if (fr.height > 6f * u) {
            val cy = fr.center.y + 0.5f * u
            for (s in listOf(-1f, 1f)) {
                val pts = TrazosNouveau.puntosLatigo(Offset(w / 2 + s * w * 0.04f, cy + fr.height * 0.12f), Offset(w / 2 + s * w * 0.33f, cy - fr.height * 0.05f), fr.height * 0.28f, 0.12f * s, s > 0f)
                tallado(TrazosNouveau.linea(pts), 1.3f * u, oscuro, calor(Offset(w / 2 + s * w * 0.2f, cy)))
            }
            for (s in listOf(-1f, 1f)) tallado(TrazosNouveau.hojaLanza(Offset(w / 2, cy + fr.height * 0.3f), Offset(w / 2 + s * w * 0.035f, cy - fr.height * 0.22f), fr.height * 0.2f, -0.12f * s), 0.9f * u, oscuro, calor(Offset(w / 2, cy)))
            tallado(TrazosNouveau.petalo(Offset(w / 2, cy + fr.height * 0.18f), Offset(w / 2, cy - fr.height * 0.34f), fr.height * 0.2f), 0.9f * u, oscuro, calor(Offset(w / 2, cy)))
        }

        // ---- la tapa: el tablero visto un poco desde arriba y su canto moldurado
        val tablero = Rect(0f, tapa, w, canto)
        drawRect(Brush.horizontalGradient(listOf(c(mezcla(ROBLE_LUZ, LUZ_MUEBLE, 0.15f), Offset(0f, tapa)), c(ROBLE_LUZ, Offset(w * 0.5f, tapa)), c(ROBLE, Offset(w, tapa))), 0f, w), tablero.topLeft, tablero.size)
        clipRect(tablero.left, tablero.top, tablero.right, tablero.bottom) {
            vetas(tablero, ROBLE_HONDO.copy(alpha = if (oscuro) 0.4f else 0.22f), 895, densidad = 0.8f, grosor = 0.7f, ondulacion = 0.3f)
            drawRect(Brush.verticalGradient(listOf(sombra(oscuro, 0.25f), Color.Transparent), tapa, tapa + (canto - tapa) * 0.5f), tablero.topLeft, Size(w, (canto - tapa) * 0.5f))
        }
        // De noche, la luz de la lámpara cae sobre la tapa.
        if (oscuro) withTransform({ scale(1f, 0.22f, Offset(pieLampara.x, pie)) }) { mancha(Offset(pieLampara.x, pie), w * 0.45f, TrazosNouveau.LUZ_HALO.copy(alpha = 0.35f)) }
        val moldura = Rect(0f, canto, w, friso)
        drawRect(Brush.verticalGradient(listOf(c(mezcla(ROBLE_LUZ, LUZ_MUEBLE, 0.3f), Offset(w * 0.6f, canto)), c(ROBLE, Offset(w * 0.6f, canto)), c(ROBLE_OSC, Offset(w * 0.6f, friso))), canto, friso), moldura.topLeft, moldura.size)
        vetas(moldura, ROBLE_HONDO.copy(alpha = 0.25f), 896, densidad = 0.5f, grosor = 0.6f, ondulacion = 0.25f)
        drawLine(brillo(oscuro, 0.7f, calor(Offset(w * 0.8f, canto)) + 0.2f), Offset(0f, canto + 0.6f * u), Offset(w, canto + 0.6f * u), 1f * u)
        drawLine(sombra(oscuro, 0.3f), Offset(0f, canto + (friso - canto) * 0.62f), Offset(w, canto + (friso - canto) * 0.62f), 0.6f * u)
        drawRect(Brush.verticalGradient(listOf(sombra(oscuro, if (oscuro) 0.6f else 0.4f), Color.Transparent), friso, friso + 6f * u), Offset(cuerpo.left, friso), Size(cuerpo.width, 6f * u))

        // ---- lo del taller, encima del mueble: la piedra, la losa con el rodillo, el frasco y la lámpara
        piedra(w * 0.06f, w * 0.31f, pie, ch * 0.085f, ch * 0.055f, oscuro, calor(Offset(w * 0.18f, pie)))
        losaYRodillo(w * 0.36f, w * 0.63f, pie, ch, oscuro) { p -> calor(p) }
        frasco(Offset(w * 0.705f, pie), ch * 0.15f, oscuro, calor(Offset(w * 0.705f, pie - ch * 0.07f)))
        sombraContacto(Offset(pieLampara.x + 4f * u, pie), altoLampara * 0.55f, 4f * u, if (oscuro) 0.3f else 0.32f)
        with(TrazosNouveau) { lampara(pieLampara, altoLampara, if (oscuro) 1f else 0f) }

        // ---- la base: el piso, la pared bajo el mueble, el zócalo con su faldón y las patas
        // El rodapié de la pared y el piso de tablas de pino, con manchas de tinta.
        drawRect(c(mezcla(PARED, PARED_OSC, 0.6f), Offset(w / 2, rodapie)), Offset(0f, rodapie - 4f * u), Size(w, 4f * u))
        drawRect(Brush.verticalGradient(listOf(c(mezcla(PISO, PISO_OSC, 0.3f), Offset(w / 2, rodapie)), c(PISO_OSC, Offset(w / 2, alto))), rodapie, alto), Offset(0f, rodapie), Size(w, alto - rodapie))
        if (!oscuro) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.25f), Color.Transparent), 0f, w * 0.6f), Offset(0f, rodapie), Size(w * 0.6f, alto - rodapie))
        var yt = rodapie; var kt = 0
        while (yt < alto) {
            val paso = (3.2f + 2.6f * kt) * u
            drawLine(sombra(oscuro, 0.35f), Offset(0f, yt), Offset(w, yt), 0.7f * u)
            var xj = w * azar(901, kt) * 0.4f
            while (xj < w) { drawLine(sombra(oscuro, 0.25f), Offset(xj, yt), Offset(xj, yt + paso), 0.6f * u); xj += w * (0.35f + 0.3f * azar(902 + kt, (xj / w * 10).toInt())) }
            yt += paso; kt++
        }
        for (k in 0 until 4) {
            val p = Offset(w * (0.3f + 0.15f * k + 0.08f * azar(905, k)), alto - hb * (0.04f + 0.1f * azar(906, k)))
            val rr = (1.2f + 1.6f * azar(907, k)) * u
            drawOval(c(if (k % 2 == 0) TINTA_LOSA else TrazosNouveau.TINTA_AZUL, p).copy(alpha = 0.55f), Offset(p.x - rr * 1.4f, p.y - rr * 0.5f), Size(rr * 2.8f, rr))
        }
        // La sombra debajo del mueble.
        drawRect(Brush.verticalGradient(listOf(sombra(oscuro, if (oscuro) 0.85f else 0.6f), sombra(oscuro, if (oscuro) 0.5f else 0.25f)), b.top, pisan + 2f * u), Offset(cuerpo.left + 4f * u, b.top), Size(cuerpo.width - 8f * u, pisan - b.top + 2f * u))
        // El zócalo: el faldón curvo entre las patas, con su yema colgante al centro, y las patas que acaban en voluta.
        val zb = b.top + hb * ZOCALO
        val pata = w * 0.07f
        val faldon = Path().apply {
            moveTo(cuerpo.left, b.top); lineTo(cuerpo.right, b.top); lineTo(cuerpo.right, pisan)
            lineTo(cuerpo.right - pata * 0.55f, pisan)
            cubicTo(cuerpo.right - pata * 0.7f, zb + hb * 0.12f, cuerpo.right - pata * 1.2f, zb, cuerpo.right - pata * 2.2f, zb - hb * 0.02f)
            cubicTo(w * 0.62f, zb - hb * 0.04f, w * 0.56f, zb - hb * 0.03f, w * 0.53f, zb + hb * 0.02f)
            quadraticTo(w * 0.5f, zb + hb * 0.14f, w * 0.47f, zb + hb * 0.02f)
            cubicTo(w * 0.44f, zb - hb * 0.03f, w * 0.38f, zb - hb * 0.04f, cuerpo.left + pata * 2.2f, zb - hb * 0.02f)
            cubicTo(cuerpo.left + pata * 1.2f, zb, cuerpo.left + pata * 0.7f, zb + hb * 0.12f, cuerpo.left + pata * 0.55f, pisan)
            lineTo(cuerpo.left, pisan); close()
        }
        clipPath(faldon) {
            roble(Rect(cuerpo.left, b.top, cuerpo.right, pisan), largo(ROBLE, w / 2, b.top, pisan), 910, oscuro, vertical = false, cerca = 0.1f)
            drawRect(Brush.verticalGradient(listOf(brillo(oscuro, 0.25f), Color.Transparent, sombra(oscuro, 0.35f)), b.top, pisan), Offset(cuerpo.left, b.top), Size(cuerpo.width, pisan - b.top))
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, sombra(oscuro, 0.3f)), cuerpo.right - pata, cuerpo.right), Offset(cuerpo.right - pata, b.top), Size(pata, pisan - b.top))
        }
        drawPath(faldon, sombra(oscuro, 0.45f), style = Stroke(0.8f * u))
        drawLine(brillo(oscuro, 0.5f, 0.2f), Offset(cuerpo.left, b.top + 0.6f * u), Offset(cuerpo.right, b.top + 0.6f * u), 0.9f * u)
        // El surco tallado que sigue la curva del faldón, por dentro.
        val surco = Path().apply {
            moveTo(cuerpo.left + pata * 0.55f, pisan - hb * 0.1f)
            cubicTo(cuerpo.left + pata * 0.75f, zb + hb * 0.02f, cuerpo.left + pata * 1.3f, zb - hb * 0.1f, cuerpo.left + pata * 2.3f, zb - hb * 0.12f)
            cubicTo(w * 0.38f, zb - hb * 0.14f, w * 0.44f, zb - hb * 0.13f, w * 0.48f, zb - hb * 0.08f)
            moveTo(cuerpo.right - pata * 0.55f, pisan - hb * 0.1f)
            cubicTo(cuerpo.right - pata * 0.75f, zb + hb * 0.02f, cuerpo.right - pata * 1.3f, zb - hb * 0.1f, cuerpo.right - pata * 2.3f, zb - hb * 0.12f)
            cubicTo(w * 0.62f, zb - hb * 0.14f, w * 0.56f, zb - hb * 0.13f, w * 0.52f, zb - hb * 0.08f)
        }
        tallado(surco, 1.1f * u, oscuro, 0.1f)
        // Las volutas de las patas, que se enroscan hacia afuera sobre el piso.
        for (s in listOf(-1f, 1f)) {
            val x = if (s < 0f) cuerpo.left + pata * 0.3f else cuerpo.right - pata * 0.3f
            sombraContacto(Offset(x + 3f * u, pisan + 1f * u), pata * 1.4f, 4f * u, if (oscuro) 0.6f else 0.38f)
            val desde = Offset(x - s * pata * 0.1f, pisan - hb * 0.12f)
            val pts = TrazosNouveau.puntosRizo(desde, Offset(s, 0.4f), pata * 0.22f, s > 0f, 1.1f, 20)
            val voluta = TrazosNouveau.linea(listOf(desde) + pts)
            drawPath(voluta, c(ROBLE_OSC, Offset(x, pisan)), style = Stroke(2.6f * u, cap = StrokeCap.Round))
            drawPath(voluta, brillo(oscuro, 0.3f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
        }
        // La marquetería del zócalo, como en los muebles de Nancy: un tallo de iris a cada lado, en maderas de color.
        for (sd in listOf(-1f, 1f)) marqueteria(Offset(w / 2 + sd * w * 0.19f, b.top + hb * 0.19f), w * 0.25f, hb * 0.2f, sd, oscuro)
        // La placa con «y N más», en el zócalo; sin placa, una yema tallada al centro.
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            portaEtiquetas(r, luz(CARTA, oscuro, 0.45f), oscuro, 0.2f)
        } else tallado(TrazosNouveau.hojaLanza(Offset(w * 0.44f, b.top + hb * 0.2f), Offset(w * 0.56f, b.top + hb * 0.19f), hb * 0.08f, 0.05f), 0.9f * u, oscuro, 0.1f)
        // En el piso: el cubo de zinc con la esponja de mojar la piedra y dos resmas de papel atadas.
        balde(Offset(w * 0.17f, alto - hb * 0.05f), hb * 0.42f, oscuro, calor(Offset(w * 0.17f, alto)))
        resmas(Offset(w * 0.8f, alto - hb * 0.04f), w * 0.15f, hb * 0.17f, oscuro, calor(Offset(w * 0.8f, alto)))

        // ---- la luz de la lámpara, por encima de todo lo que tiene cerca
        if (oscuro) {
            mancha(foco, w * 0.55f, TrazosNouveau.LUZ_HALO.copy(alpha = 0.12f))
            mancha(foco, w * 0.2f, TrazosNouveau.LUZ_HALO.copy(alpha = 0.18f))
        }
        granoMueble(Rect(0f, 0f, w, alto), oscuro)
    }

    /**
     * Un tallo de iris en marquetería, acostado, que nace en [desde] y se va hacia [s] (−1 a la izquierda, 1 a la derecha) a lo
     * largo de [largo]: el tallo de nogal, las hojas de sicomoro teñido de verde y de arce claro, y la flor de madera teñida de
     * violeta, cada pieza con el filo oscuro del corte de la sierra.
     */
    private fun DrawScope.marqueteria(desde: Offset, largo: Float, alto: Float, s: Float, oscuro: Boolean) {
        val u = density
        fun c(col: Color) = luz(col, oscuro, 0.12f)
        val corte = c(ROBLE_HONDO).copy(alpha = 0.7f)
        val nogal = c(Color(0xFF5E3C22)); val verde = c(Color(0xFF8A9868)); val arce = c(Color(0xFFE2CB98)); val violeta = c(Color(0xFF7B5C88))
        val fin = desde + Offset(s * largo, -alto * 0.1f)
        val tallo = TrazosNouveau.bezier(desde, desde + Offset(s * largo * 0.3f, alto * 0.35f), desde + Offset(s * largo * 0.65f, -alto * 0.4f), fin, 24)
        drawPath(TrazosNouveau.cinta(tallo) { t -> alto * 0.09f * (1f - 0.6f * t) }, nogal)
        // Las hojas, alternas, de dos maderas.
        listOf(Triple(4, -1f, true), Triple(9, 1f, false), Triple(14, -1f, false), Triple(18, 1f, true)).forEach { (i, lado, clara) ->
            val b = tallo[i]
            val punta = b + Offset(s * largo * 0.2f, lado * alto * 0.42f)
            val hoja = TrazosNouveau.hojaLanza(b, punta, alto * 0.2f, 0.12f * lado * s)
            drawPath(hoja, if (clara) arce else verde)
            drawPath(hoja, corte, style = Stroke(0.5f * u))
            drawLine(corte.copy(alpha = 0.35f), b, b + (punta - b) * 0.8f, 0.4f * u)
        }
        // La flor al final: tres pétalos que suben y dos que caen.
        val fl = fin
        listOf(-0.5f to 0.85f, 0.5f to 0.85f, 0f to 1f).forEach { (g, k) ->
            val p = TrazosNouveau.petalo(fl, fl + Offset(sin(g) * alto * 0.5f * k, -cos(g) * alto * 0.5f * k), alto * 0.3f * k)
            drawPath(p, if (g == 0f) c(mezcla(Color(0xFF7B5C88), Color.White, 0.25f)) else violeta); drawPath(p, corte, style = Stroke(0.5f * u))
        }
        listOf(-1.1f, 1.1f).forEach { g ->
            val p = TrazosNouveau.petalo(fl, fl + Offset(sin(g) * alto * 0.48f, cos(g) * alto * 0.2f + alto * 0.12f), alto * 0.28f)
            drawPath(p, violeta); drawPath(p, corte, style = Stroke(0.5f * u))
        }
        drawCircle(c(TrazosNouveau.IRIS_LENGUA), alto * 0.06f, fl)
    }

    /** El cubo de zinc con agua y la esponja que asoma, de [alto], de pie en [pie]: con él se moja la piedra antes de entintarla. */
    private fun DrawScope.balde(pie: Offset, alto: Float, oscuro: Boolean, cerca: Float) {
        val u = density
        fun c(col: Color) = luz(col, oscuro, cerca)
        val a1 = alto * 0.55f; val a0 = alto * 0.42f
        sombraContacto(Offset(pie.x + 4f * u, pie.y), a1 * 2.4f, 5f * u, if (oscuro) 0.6f else 0.35f)
        val cuerpo = Path().apply { moveTo(pie.x - a0, pie.y); lineTo(pie.x - a1, pie.y - alto); lineTo(pie.x + a1, pie.y - alto); lineTo(pie.x + a0, pie.y); close() }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(c(Color(0xFFC9CED0)), c(Color(0xFF9AA2A6)), c(Color(0xFF6E767C))), pie.x - a1, pie.x + a1))
        // Los aros del cubo.
        listOf(0.18f, 0.82f).forEach { f ->
            val y = pie.y - alto * f
            val ax = a0 + (a1 - a0) * f
            drawLine(c(Color(0xFF6E767C)), Offset(pie.x - ax, y), Offset(pie.x + ax, y), 1.4f * u)
            drawLine(brillo(oscuro, 0.4f, cerca + 0.2f), Offset(pie.x - ax, y - 0.9f * u), Offset(pie.x, y - 0.9f * u), 0.5f * u)
        }
        drawLine(brillo(oscuro, 0.5f, cerca + 0.2f), Offset(pie.x - a0 * 0.6f, pie.y - alto * 0.1f), Offset(pie.x - a1 * 0.62f, pie.y - alto * 0.9f), 1.2f * u, StrokeCap.Round)
        // La boca, con el agua y la esponja.
        val boca = Rect(pie.x - a1, pie.y - alto - a1 * 0.22f, pie.x + a1, pie.y - alto + a1 * 0.22f)
        drawOval(c(Color(0xFF5E666C)), boca.topLeft, boca.size)
        drawOval(c(Color(0xFF3F4E58)), Offset(boca.left + 1.4f * u, boca.top + 1.6f * u), Size(boca.width - 2.8f * u, boca.height - 2.4f * u))
        drawOval(c(Color(0xFFD6B26A)), Offset(pie.x - a1 * 0.2f, boca.top - a1 * 0.12f), Size(a1 * 0.8f, a1 * 0.42f))
        for (k in 0 until 6) drawCircle(c(Color(0xFF8A6A30)), 0.5f * u, Offset(pie.x + a1 * (0.05f + 0.45f * azar(921, k)), boca.top + a1 * 0.25f * azar(922, k)))
        // El asa de alambre, caída hacia atrás.
        drawArc(c(Color(0xFF7A8288)), 180f, 180f, false, Offset(boca.left + 1f * u, boca.top - a1 * 0.5f), Size(boca.width - 2f * u, a1 * 0.9f), style = Stroke(0.9f * u))
    }

    /** Dos resmas de papel envueltas en papel de estraza y atadas con cordel en cruz, una encima de otra, un poco corridas. */
    private fun DrawScope.resmas(pie: Offset, ancho: Float, alto: Float, oscuro: Boolean, cerca: Float) {
        val u = density
        fun c(col: Color) = luz(col, oscuro, cerca)
        sombraContacto(Offset(pie.x + 5f * u, pie.y), ancho * 1.25f, 5f * u, if (oscuro) 0.6f else 0.35f)
        for (k in 0..1) {
            val dx = if (k == 0) 0f else ancho * 0.08f
            val y1 = pie.y - alto * k
            val r = Rect(pie.x - ancho / 2 + dx, y1 - alto, pie.x + ancho / 2 + dx, y1)
            val estraza = if (k == 0) Color(0xFFB6956A) else Color(0xFFC4A57A)
            drawRect(Brush.horizontalGradient(listOf(c(mezcla(estraza, LUZ_MUEBLE, 0.2f)), c(estraza), c(mezcla(estraza, SOMBRA_MUEBLE, 0.25f))), r.left, r.right), r.topLeft, r.size)
            // La cara de arriba, en perspectiva, con el cordel en cruz.
            val f = alto * 0.45f
            val cara = Path().apply { moveTo(r.left, r.top); lineTo(r.left + f * 0.3f, r.top - f); lineTo(r.right - f * 0.3f, r.top - f); lineTo(r.right, r.top); close() }
            if (k == 1) drawPath(cara, c(mezcla(estraza, LUZ_MUEBLE, 0.3f)))
            val cuerda = c(Color(0xFFE6DCC2))
            drawLine(cuerda, Offset(r.center.x, r.top - (if (k == 1) f else 0f)), Offset(r.center.x, r.bottom), 0.8f * u)
            drawLine(cuerda, Offset(r.left, r.center.y), Offset(r.right, r.center.y), 0.8f * u)
            if (k == 1) drawLine(cuerda, Offset(r.left + f * 0.15f, r.top - f * 0.5f), Offset(r.right - f * 0.15f, r.top - f * 0.5f), 0.8f * u)
            // El rótulo de la resma: un papel con dos rayas.
            if (k == 0) {
                drawRect(c(CARTA), Offset(r.left + ancho * 0.08f, r.top + alto * 0.18f), Size(ancho * 0.26f, alto * 0.5f))
                drawLine(c(TINTA).copy(alpha = 0.6f), Offset(r.left + ancho * 0.11f, r.top + alto * 0.35f), Offset(r.left + ancho * 0.3f, r.top + alto * 0.35f), 0.5f * u)
                drawLine(c(TINTA).copy(alpha = 0.6f), Offset(r.left + ancho * 0.11f, r.top + alto * 0.5f), Offset(r.left + ancho * 0.24f, r.top + alto * 0.5f), 0.5f * u)
            }
            drawRect(sombra(oscuro, 0.3f), r.topLeft, r.size, style = Stroke(0.5f * u))
        }
    }

    // ---------------------------------------------------------------- el compartimento: un cajón plano

    /**
     * Lo que asoma de un cajón entreabierto en la franja de [arriba]: el fondo en sombra y la pila de pruebas, cada una un poco
     * corrida (de dos resmas, alguna con su color), y la de encima con su franja de color y su cruz de registro. Las del cajón
     * sin tema están revueltas.
     */
    private fun DrawScope.pruebasAsomadas(w: Float, arriba: Float, k: Int, semilla: Int, sinTema: Boolean, oscuro: Boolean, cerca: Float) {
        val u = density
        fun c(col: Color) = luz(col, oscuro, cerca)
        drawRect(Brush.verticalGradient(listOf(if (oscuro) Color(0xFF07090B) else Color(0xFF2A2018), c(ROBLE_HONDO)), 0f, arriba), Offset.Zero, Size(w, arriba + 2f * u))
        val m = k.coerceIn(1, 7)
        clipRect(0f, 0f, w, arriba + 1f * u) {
            for (i in 0 until m) {
                val encima = i == m - 1
                val xa = w * (0.035f + 0.04f * azar(semilla, i))
                val xb = w * (0.965f - 0.04f * azar(semilla, i + 20))
                val ya = arriba * (0.5f - 0.32f * (i + 1) / m) + (if (sinTema) (azar(semilla, i + 40) - 0.5f) * arriba * 0.3f else 0f)
                val giro = if (sinTema) (azar(semilla, i + 60) - 0.5f) * 5f else 0f
                val papel = when (Math.floorMod(semilla + i, 3)) { 0 -> PAPEL; 1 -> PAPEL_FRIO; else -> mezcla(PAPEL, CARTA, 0.5f) }
                rotate(giro, Offset(w / 2, arriba)) {
                    drawRect(sombra(oscuro, 0.35f), Offset(xa + 1f * u, ya + 1f * u), Size(xb - xa, arriba - ya + 4f * u))
                    drawRect(Brush.verticalGradient(listOf(c(mezcla(papel, SOMBRA_MUEBLE, 0.25f)), c(papel)), ya, arriba), Offset(xa, ya), Size(xb - xa, arriba - ya + 4f * u))
                    // Una prueba de abajo que ya tiene color: asoma su mancha en la punta.
                    if (!encima && azar(semilla, i + 80) > 0.55f) {
                        val t = TINTAS[Math.floorMod(semilla / 3 + i, 5)]
                        val izq = azar(semilla, i + 90) > 0.5f
                        drawRect(c(t).copy(alpha = 0.85f), Offset(if (izq) xa else xb - w * 0.05f, ya + 1f * u), Size(w * 0.05f, arriba - ya))
                    }
                    drawLine(c(mezcla(papel, Color.White, 0.5f)), Offset(xa, ya), Offset(xb, ya), 0.5f * u)
                    if (encima) {
                        val s = ((arriba - ya) * 0.42f).coerceIn(2f * u, 5.5f * u)
                        val yc = ya + (arriba - ya) * 0.48f - s / 2
                        if (arriba - ya > 4.5f * u) {
                            franjaColor(xa + w * 0.07f, yc, s, if (sinTema) 0b000111 else 0b111111, oscuro, cerca)
                            cruzDoble(Offset(w * 0.5f, yc + s / 2), s * 0.75f, 0.45f * u, 0.4f * u, oscuro, cerca)
                            cruzDoble(Offset(xb - w * 0.08f, yc + s / 2), s * 0.75f, 0.45f * u, 0.4f * u, oscuro, cerca)
                            garabato(w * 0.62f, yc + s * 0.2f, w * 0.12f, s * 0.7f, c(Color(0xFF6A6A70)).copy(alpha = 0.6f), semilla)
                            // El borde de la imagen impresa, que asoma entre las marcas: cada cajón guarda otro cartel.
                            val ix = w * 0.3f; val iw = w * 0.14f
                            clipRect(ix, ya + 1f * u, ix + iw, arriba) {
                                when (Math.floorMod(semilla / 7, 3)) {
                                    0 -> {
                                        drawCircle(c(TrazosNouveau.IRIS_LENGUA), iw * 0.45f, Offset(ix + iw * 0.5f, arriba + iw * 0.22f))
                                        drawPath(TrazosNouveau.petalo(Offset(ix + iw * 0.4f, arriba + 2f * u), Offset(ix + iw * 0.55f, ya + 1f * u), iw * 0.28f), c(TrazosNouveau.IRIS))
                                    }
                                    1 -> {
                                        drawRect(c(mezcla(PAPEL, TrazosNouveau.IRIS_LENGUA, 0.35f)), Offset(ix, ya), Size(iw, arriba - ya))
                                        drawPath(TrazosNouveau.linea(TrazosNouveau.bezier(Offset(ix, arriba), Offset(ix + iw * 0.4f, ya - 2f * u), Offset(ix + iw * 0.6f, arriba + 2f * u), Offset(ix + iw, ya + 1f * u), 16)),
                                            c(TrazosNouveau.TALLO_SOMBRA), style = Stroke(1.6f * u, cap = StrokeCap.Round))
                                    }
                                    else -> {
                                        drawRect(c(TrazosNouveau.TINTA_AZUL), Offset(ix, ya), Size(iw, arriba - ya))
                                        drawRect(c(TrazosNouveau.TINTA_ROJA), Offset(ix + iw * 0.12f, ya + (arriba - ya) * 0.3f), Size(iw * 0.76f, (arriba - ya) * 0.3f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // La sombra del travesaño de arriba sobre lo que asoma.
        drawRect(Brush.verticalGradient(listOf(sombra(oscuro, 0.6f), Color.Transparent), 0f, arriba * 0.5f), Offset.Zero, Size(w, arriba * 0.5f))
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        val k = if (c.vacio) 0 else c.n
        // De noche, la lámpara queda encima del mueble: a los cajones les llega poco, más al filo de arriba.
        val cerca = 0.16f
        fun t(col: Color, f: Float = 0f) = luz(col, oscuro, cerca + f)
        // Los cajones más llenos quedan entreabiertos; el de las maculaturas, más.
        val entre = if (c.vacio || k == 0) 0f else h * (0.04f + 0.014f * k.coerceIn(0, 8)) + (if (c.sinTema) h * 0.03f else 0f)
        val baja = h * SALIDA * ab
        val arriba = entre + baja
        if (arriba > 0.5f) pruebasAsomadas(w, arriba, k.coerceAtLeast(1), c.semilla, c.sinTema, oscuro, cerca + 0.08f)

        // El frente del cajón: roble con su veta, el bisel y un filete tallado alrededor.
        val f = Rect(0f, arriba, w, h + baja)
        if (ab > 0f) sombraSuave(Rect(f.left, f.bottom - 4f * u, f.right, f.bottom + 2f * u), 2f * u, 8f * u * ab, 0.3f * ab)
        roble(f, Brush.verticalGradient(listOf(t(mezcla(ROBLE, ROBLE_LUZ, 0.35f), 0.08f), t(ROBLE), t(mezcla(ROBLE, ROBLE_OSC, 0.3f), -0.04f)), f.top, f.bottom), c.semilla, oscuro, vertical = false, cerca = cerca)
        drawRect(Brush.horizontalGradient(listOf(brillo(oscuro, 0.18f), Color.Transparent, sombra(oscuro, 0.16f)), 0f, w), f.topLeft, f.size)
        bisel(f, if (oscuro) LAMPARA.copy(alpha = 0.16f) else luzMueble(0.45f), sombra(oscuro, 0.4f), 1.3f * u)
        val i = 4.5f * u
        val filete = Rect(i, f.top + i, w - i, f.bottom - i)
        if (filete.height > 6f * u) {
            val rr = minOf(filete.height * 0.35f, 7f * u)
            val p = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(filete, CornerRadius(rr))) }
            tallado(p, 0.9f * u, oscuro, cerca)
        }

        // El portaetiquetas con la cartulina del rótulo (o vacío, en el cajón todavía sin tema).
        val r = Rect(w * rotulo.left, h * rotulo.top + baja, w * rotulo.right, h * rotulo.bottom + baja)
        portaEtiquetas(r, if (c.vacio) null else t(CARTA, 0.3f), oscuro, cerca + 0.1f)
        // Los tiradores de latón en forma de hoja, uno a cada lado.
        val ty = r.center.y + r.height * 0.06f
        tirador(Offset(w * 0.12f, ty), w * 0.1f, -1f, oscuro, cerca + 0.12f)
        tirador(Offset(w * 0.88f, ty), w * 0.1f, 1f, oscuro, cerca + 0.24f)
        // De noche, el filo cálido que deja la lámpara en el canto de arriba y su reflejo, más tibio hacia la derecha, donde está.
        if (oscuro) {
            drawLine(LAMPARA.copy(alpha = 0.22f), Offset(1f * u, f.top + 0.6f * u), Offset(w - 1f * u, f.top + 0.6f * u), 0.8f * u)
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, LAMPARA.copy(alpha = 0.07f)), w * 0.4f, w), Offset(w * 0.4f, f.top), Size(w * 0.6f, f.height))
        }

        // Las maculaturas: una prueba revuelta que se sale por el borde del cajón, sobre el frente.
        if (c.sinTema && !c.vacio && entre > 0f) {
            val p0 = Offset(w * 0.04f, arriba - 2f * u)
            val salida = Path().apply {
                moveTo(p0.x, p0.y); lineTo(w * 0.17f, arriba - 3f * u)
                lineTo(w * 0.15f, arriba + h * 0.13f); lineTo(w * 0.03f, arriba + h * 0.1f); close()
            }
            translate(1.5f * u, 2.5f * u) { drawPath(salida, sombra(oscuro, 0.35f)) }
            drawPath(salida, Brush.linearGradient(listOf(t(PAPEL, 0.1f), t(mezcla(PAPEL, SOMBRA_MUEBLE, 0.15f))), p0, Offset(w * 0.15f, arriba + h * 0.13f)))
            clipPath(salida) {
                drawRect(t(TrazosNouveau.TINTA_ROJA).copy(alpha = 0.75f), Offset(w * 0.06f, arriba + h * 0.02f), Size(w * 0.07f, h * 0.04f))
                cruzDoble(Offset(w * 0.1f, arriba + h * 0.085f), h * 0.025f, 0.4f * u, 0.6f * u, oscuro, cerca)
            }
        }
        // Al abrirlo, sube la claridad del papel.
        if (ab > 0f) mancha(Offset(w * 0.5f, arriba * 0.6f), w * 0.4f, if (oscuro) LAMPARA.copy(alpha = 0.12f * ab) else luzMueble(0.3f * ab))
    }

    override val rotulo = Rect(0.25f, 0.34f, 0.75f, 0.8f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.dellaRespira
    override val salida = SALIDA
    override val placa: Rect = Rect(0.33f, 0.14f, 0.67f, 0.34f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior: el cajón abierto, visto desde arriba

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val pl = 18 * u; val pa = 30 * u; val pf = 24 * u
        val fondo = Rect(pl, pa, w - pl, h - pf)
        // De noche, la lámpara queda arriba, sobre el mueble: alumbra el cajón desde lo alto y deja hundidas las esquinas.
        val centro = Offset(w * 0.55f, h * 0.38f)
        fun cerca(p: Offset) = (0.62f - (p - centro).getDistance() / (maxOf(w, h) * 0.9f)).coerceIn(0.06f, 0.55f)
        fun t(col: Color, p: Offset) = luz(col, oscuro, cerca(p))
        // El fondo del cajón, en sombra, y la pila de pruebas de abajo, cada una corrida a su modo.
        drawRect(t(mezcla(ROBLE, ROBLE_HONDO, 0.6f), centro), fondo.topLeft, fondo.size)
        val hoja = Rect(fondo.left + 5f * u, fondo.top + 7f * u, fondo.right - 9f * u, fondo.bottom - 12f * u)
        listOf(Offset(5f, 9f) to PAPEL_FRIO, Offset(-3f, 6f) to mezcla(PAPEL, CARTA, 0.5f), Offset(3f, 4f) to PAPEL, Offset(-1f, 2f) to PAPEL_FRIO).forEachIndexed { i, (d, col) ->
            val r = Rect(hoja.left + d.x * u, hoja.top + d.y * u * 0.4f, hoja.right + d.x * u, hoja.bottom + d.y * u)
            drawRect(sombra(oscuro, 0.3f), Offset(r.left + 1.5f * u, r.top + 1.5f * u), r.size)
            drawRect(t(mezcla(col, SOMBRA_MUEBLE, 0.12f), centro), r.topLeft, r.size)
            drawLine(t(mezcla(col, Color.White, 0.4f), centro), Offset(r.left, r.bottom), Offset(r.right, r.bottom), 0.6f * u)
            // Una de abajo ya tiene su rojo: asoma en el borde.
            if (i == 1) drawRect(t(TrazosNouveau.TINTA_ROJA, centro).copy(alpha = 0.8f), Offset(r.right - 3f * u, r.top + r.height * 0.3f), Size(3f * u, r.height * 0.2f))
        }
        // La prueba de encima: el papel crema, su imagen apenas impresa (el amarillo y el verde) y sus marcas en los márgenes.
        drawRect(sombra(oscuro, 0.35f), Offset(hoja.left + 2f * u, hoja.top + 2.5f * u), hoja.size)
        drawRect(Brush.linearGradient(listOf(t(mezcla(PAPEL, Color.White, 0.25f), hoja.topLeft), t(PAPEL, centro), t(mezcla(PAPEL, PARED_OSC, 0.3f), hoja.bottomRight)), hoja.topLeft, hoja.bottomRight), hoja.topLeft, hoja.size)
        clipRect(hoja.left, hoja.top, hoja.right, hoja.bottom) {
            pinceladas(hoja, if (oscuro) NOCHE else PARED_OSC, 951, 22, u, 0.07f)
            val mg = hoja.width * 0.11f
            val img = Rect(hoja.left + mg, hoja.top + mg * 1.4f, hoja.right - mg, hoja.bottom - mg * 1.8f)
            // El amarillo: el sol y la franja del suelo; el verde: los tallos de látigo que suben por los lados.
            drawCircle(t(TrazosNouveau.IRIS_LENGUA, centro).copy(alpha = 0.2f), img.width * 0.4f, Offset(img.center.x, img.top + img.height * 0.32f))
            drawRect(t(TrazosNouveau.IRIS_LENGUA, centro).copy(alpha = 0.14f), Offset(img.left, img.bottom - img.height * 0.12f), Size(img.width, img.height * 0.12f))
            with(TrazosNouveau) {
                latigo(Offset(img.left + img.width * 0.06f, img.bottom), Offset(img.left + img.width * 0.18f, img.top + img.height * 0.22f), img.width * 0.06f, 3f * u, t(TALLO, centro).copy(alpha = 0.3f), 0.16f, false, false)
                latigo(Offset(img.right - img.width * 0.06f, img.bottom), Offset(img.right - img.width * 0.18f, img.top + img.height * 0.22f), img.width * 0.06f, 3f * u, t(TALLO, centro).copy(alpha = 0.3f), -0.16f, true, false)
            }
            val lapiz = t(Color(0xFF6A6A70), centro).copy(alpha = 0.55f)
            marcasCorte(img, mg * 0.4f, t(TINTA, centro).copy(alpha = 0.45f), 0.6f * u)
            listOf(Offset(hoja.left + mg * 0.5f, img.center.y), Offset(hoja.right - mg * 0.5f, img.center.y), Offset(img.center.x, hoja.top + mg * 0.7f), Offset(img.center.x, hoja.bottom - mg * 0.6f)).forEach { p ->
                cruzDoble(p, mg * 0.3f, 0.7f * u, 0.8f * u, oscuro, cerca(p))
            }
            franjaColor(img.left, hoja.bottom - mg * 1.15f, mg * 0.3f, 0b010001, oscuro, cerca(Offset(img.left, hoja.bottom)))
            // Las notas a lápiz del impresor, arriba a la derecha y abajo.
            garabato(img.right - img.width * 0.32f, hoja.top + mg * 0.55f, img.width * 0.3f, mg * 0.35f, lapiz, 11)
            garabato(img.right - img.width * 0.24f, hoja.bottom - mg * 1.05f, img.width * 0.22f, mg * 0.3f, lapiz, 17)
            drawLine(lapiz, Offset(img.right - img.width * 0.24f, hoja.bottom - mg * 0.75f), Offset(img.right - img.width * 0.02f, hoja.bottom - mg * 0.78f), 0.6f * u)
        }
        // La hoja de papel de seda que separa una prueba de otra, echada de través sobre la esquina de abajo.
        val seda = Path().apply {
            moveTo(hoja.right - hoja.width * 0.42f, hoja.bottom + 3f * u); lineTo(hoja.right + 2f * u, hoja.bottom - hoja.height * 0.3f)
            lineTo(hoja.right + 6f * u, hoja.bottom + 6f * u); close()
        }
        drawPath(seda, t(PAPEL_SEDA, Offset(hoja.right, hoja.bottom)).copy(alpha = 0.55f))
        drawPath(seda, t(PAPEL_SEDA, Offset(hoja.right, hoja.bottom)).copy(alpha = 0.7f), style = Stroke(0.6f * u))
        drawLine(sombra(oscuro, 0.12f), Offset(hoja.right - hoja.width * 0.3f, hoja.bottom - 1f * u), Offset(hoja.right - 4f * u, hoja.bottom - hoja.height * 0.22f), 0.8f * u)
        // La plegadera de hueso, olvidada en el margen de abajo a la izquierda.
        val pg = Offset(hoja.left + hoja.width * 0.06f, hoja.bottom - 4f * u)
        rotate(-6f, pg) {
            drawRoundRect(sombra(oscuro, 0.3f), Offset(pg.x + 1.5f * u, pg.y - 1.5f * u), Size(hoja.width * 0.3f, 5f * u), CornerRadius(2.5f * u))
            drawRoundRect(t(Color(0xFFEDE4CC), pg), Offset(pg.x, pg.y - 3f * u), Size(hoja.width * 0.3f, 5f * u), CornerRadius(2.5f * u))
            drawLine(brillo(oscuro, 0.6f, cerca(pg)), Offset(pg.x + 3f * u, pg.y - 2f * u), Offset(pg.x + hoja.width * 0.27f, pg.y - 2f * u), 0.6f * u)
        }
        // De noche, la luz que cae de la lámpara; las esquinas se hunden en el azul negro.
        if (oscuro) {
            mancha(centro, minOf(w, h) * 0.8f, TrazosNouveau.LUZ_HALO.copy(alpha = 0.07f))
            drawRect(Brush.radialGradient(listOf(Color.Transparent, NOCHE.copy(alpha = 0.6f)), centro, maxOf(w, h) * 0.75f))
        }
        // Las paredes del cajón vistas desde arriba: la de atrás y la de la izquierda en sombra, la de delante y la de la derecha a la luz.
        val atras = Path().apply { moveTo(0f, 0f); lineTo(w, 0f); lineTo(w - pl, pa); lineTo(pl, pa); close() }
        val izq = Path().apply { moveTo(0f, 0f); lineTo(pl, pa); lineTo(pl, h - pf); lineTo(0f, h); close() }
        val der = Path().apply { moveTo(w, 0f); lineTo(w, h); lineTo(w - pl, h - pf); lineTo(w - pl, pa); close() }
        val delante = Path().apply { moveTo(0f, h); lineTo(pl, h - pf); lineTo(w - pl, h - pf); lineTo(w, h); close() }
        listOf(Triple(atras, Rect(0f, 0f, w, pa), mezcla(ROBLE_LUZ, SOMBRA_MUEBLE, 0.18f)), Triple(izq, Rect(0f, 0f, pl, h), mezcla(ROBLE, SOMBRA_MUEBLE, 0.3f)),
            Triple(der, Rect(w - pl, 0f, w, h), mezcla(ROBLE, LUZ_MUEBLE, 0.05f)), Triple(delante, Rect(0f, h - pf, w, h), mezcla(ROBLE_LUZ, LUZ_MUEBLE, 0.1f))).forEachIndexed { i, (p, r, col) ->
            clipPath(p) { roble(r, Brush.verticalGradient(listOf(t(col, r.center), t(col, r.center))), 960 + i, oscuro, vertical = i == 1 || i == 2, cerca = cerca(r.center)) }
            drawPath(p, sombra(oscuro, 0.3f), style = Stroke(0.6f * u))
        }
        // La sombra que echan las paredes en el fondo y el canto de las tablas, en luz.
        sombraInterior(fondo, 20 * u, if (oscuro) 0.45f else 0.28f)
        drawRect(brillo(oscuro, 0.5f, 0.3f), Offset(0f, 0f), Size(w, 1.2f * u))
        drawRect(brillo(oscuro, 0.4f, 0.2f), Offset(0f, 0f), Size(1.2f * u, h))
        // Las colas de milano de las esquinas del cajón.
        for ((x, y) in listOf(0f to 0f, w - pl * 0.5f to 0f)) {
            for (j in 0 until 2) drawRect(sombra(oscuro, 0.25f), Offset(x + pl * 0.1f, y + pa * (0.2f + 0.35f * j)), Size(pl * 0.35f, pa * 0.18f))
        }
        granoMueble(Rect(0f, 0f, w, h), oscuro, 0.7f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) Color(0xFFECE5D6) else TINTA
    override val pared: Dp = 20.dp
    override val columnas = 2
    override val lineas = 4

    // ---------------------------------------------------------------- las fichas: pruebas de imprenta en papel de tina

    /** El borde barbado del papel de tina, abajo y a la izquierda; arriba y a la derecha, el corte recto de la guillotina. */
    private fun contornoPrueba(w: Float, h: Float, semilla: Int): Path = Path().apply {
        val amp = minOf(w, h) * 0.008f
        // Una ondulación suave (dos senos) con fibras chicas encima, nunca un diente de sierra.
        fun barba(k: Int, s0: Int) = amp * (0.9f + 0.45f * sin(k * 0.55f + s0 * 0.37f) + 0.3f * sin(k * 1.7f + s0) + 0.35f * azar(s0, k))
        moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h - amp * 0.6f)
        val pasos = (w / (amp * 1.6f)).toInt().coerceIn(16, 160)
        for (k in 1..pasos) lineTo(w - w * k / pasos, h - barba(k, semilla))
        val pasosV = (h / (amp * 1.6f)).toInt().coerceIn(16, 200)
        for (k in 1 until pasosV) lineTo(barba(k, semilla + 1), h - h * k / pasosV)
        close()
    }

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(contornoPrueba(s.width, s.height, semilla)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.45f else 1f
        val contorno = contornoPrueba(w, h, semilla)
        // El borde barbado, más delgado y translúcido: se oscurece un poco.
        clipPath(contorno) {
            drawRect(Brush.horizontalGradient(listOf(sombraMueble(0.07f), Color.Transparent), 0f, 4f * u * g), Offset.Zero, Size(4f * u * g, h))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombraMueble(0.07f)), h - 4f * u * g, h), Offset(0f, h - 4f * u * g), Size(w, 4f * u * g))
            drawLine(luzMueble(0.6f), Offset(0f, 0.6f * u), Offset(w, 0.6f * u), 0.8f * u)
        }
        val tinta = p.tinta.copy(alpha = 0.45f)
        // Las marcas de corte, en las cuatro esquinas.
        val mc = Rect(7f * u * g, 7f * u * g, w - 7f * u * g, h - 7f * u * g)
        marcasCorte(mc, 3f * u * g, tinta, 0.5f * u)
        // Las cruces de registro, a media altura de los dos lados, con sus dos tintas un poco corridas.
        listOf(Offset(5.5f * u * g, h * 0.5f), Offset(w - 5.5f * u * g, h * 0.5f)).forEach { c ->
            cruzRegistro(c + Offset(0.5f * u, -0.3f * u), 3.2f * u * g, TrazosNouveau.TINTA_ROJA.copy(alpha = 0.7f), 0.5f * u)
            cruzRegistro(c, 3.2f * u * g, TrazosNouveau.TINTA_AZUL.copy(alpha = 0.75f), 0.5f * u)
        }
        // La franja de las tintas, abajo a la izquierda (cuántas pasaron depende de la prueba).
        val hechas = (1 shl (2 + Math.floorMod(semilla, 5))) - 1
        franjaColor(if (grande) 26f * u else 14f * u, h - 6.5f * u * g, 3.2f * u * g, hechas, false, 0f)
        // En la grande, el timbre seco del taller: una libélula en relieve dentro de su aro, abajo a la derecha.
        if (grande) {
            val c = Offset(w - 26f * u, h - 26f * u)
            val r = 13f * u
            for ((d, col) in listOf(Offset(0.6f * u, 0.6f * u) to luzMueble(0.75f), Offset(-0.45f * u, -0.45f * u) to sombraMueble(0.2f))) {
                translate(d.x, d.y) {
                    drawCircle(col, r, c, style = Stroke(1.1f * u))
                    drawCircle(col, r * 0.8f, c, style = Stroke(0.5f * u))
                    // La libélula del taller, en relieve: el cuerpo y las cuatro alas en aspa, solo su contorno.
                    rotate(-24f, c) {
                        drawLine(col, c + Offset(0f, -r * 0.45f), c + Offset(0f, r * 0.62f), 1.3f * u, StrokeCap.Round)
                        drawCircle(col, r * 0.09f, c + Offset(0f, -r * 0.5f))
                        for ((sx, sy) in listOf(-1f to -1f, 1f to -1f, -1f to 1f, 1f to 1f)) {
                            val ala = TrazosNouveau.petalo(c + Offset(sx * r * 0.04f, sy * r * 0.06f - r * 0.1f), c + Offset(sx * r * 0.62f, -r * 0.1f + sy * r * 0.18f), r * 0.2f)
                            drawPath(ala, col, style = Stroke(0.6f * u))
                        }
                    }
                }
            }
        }
    }

    override val margenes = MargenesFicha(15.dp, 15.dp, 17.dp, 26.dp, 27.dp, 32.dp)
    override val proporcionFicha = 0.9f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // La cruz de registro con las tintas en su lugar: la prueba salió bien, está hecho.
            "hecho" -> {
                drawCircle(TrazosNouveau.ORO, s * 0.2f, c)
                cruzRegistro(c, s * 0.48f, p.tinta, s * 0.085f)
                drawCircle(p.tinta, s * 0.07f, c)
            }
            // El iris abierto: florece, convence.
            "convence" -> with(TrazosNouveau) {
                florAbierta(Offset(c.x, c.y + s * 0.18f), s * 0.46f, IRIS, 1f, -PI.toFloat() / 2, TALLO_SOMBRA, IRIS_LENGUA)
            }
            // La línea de látigo que vuelve sobre sí: le sigue dando vueltas.
            "piensa" -> with(TrazosNouveau) {
                latigo(Offset(c.x - s * 0.42f, c.y + s * 0.4f), Offset(c.x + s * 0.12f, c.y - s * 0.08f), s * 0.2f, s * 0.15f, p.acento, 0.3f, true, false)
            }
            // El capullo cerrado en su tallo: todavía no abre.
            "duda" -> {
                val tallo = Path().apply { moveTo(c.x - s * 0.1f, c.y + s * 0.46f); quadraticTo(c.x + s * 0.12f, c.y + s * 0.1f, c.x, c.y - s * 0.05f) }
                drawPath(tallo, p.tenue, style = Stroke(s * 0.07f, cap = StrokeCap.Round))
                drawPath(TrazosNouveau.hojaLanza(Offset(c.x + s * 0.02f, c.y + s * 0.26f), Offset(c.x + s * 0.34f, c.y + s * 0.12f), s * 0.13f, -0.15f), p.tenue)
                drawPath(TrazosNouveau.petalo(Offset(c.x, c.y), Offset(c.x + s * 0.04f, c.y - s * 0.46f), s * 0.3f), p.tenue)
            }
            // La cruz fuera de registro, el rojo por un lado y el azul por otro: así no.
            "desacuerdo" -> {
                cruzRegistro(c + Offset(-s * 0.08f, -s * 0.07f), s * 0.4f, TrazosNouveau.TINTA_ROJA, s * 0.08f)
                cruzRegistro(c + Offset(s * 0.08f, s * 0.07f), s * 0.4f, TrazosNouveau.TINTA_AZUL, s * 0.08f)
            }
            else -> {}
        }
    }

    /** La pinza de tender con que se cuelgan las pruebas a secar. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        rotate(-28f, c) {
            for (lado in listOf(-1f, 1f)) {
                val x = if (lado < 0f) c.x - s * 0.17f else c.x + s * 0.01f
                drawRoundRect(mezcla(HAYA, ROBLE_OSC, if (lado < 0f) 0.15f else 0.35f), Offset(x, c.y - s * 0.48f), Size(s * 0.16f, s * 0.96f), CornerRadius(s * 0.07f))
            }
            drawLine(ROBLE_HONDO.copy(alpha = 0.6f), Offset(c.x, c.y - s * 0.48f), Offset(c.x, c.y + s * 0.48f), s * 0.03f)
            drawLine(Color(0xFF7A7C78), Offset(c.x - s * 0.24f, c.y - s * 0.06f), Offset(c.x + s * 0.24f, c.y - s * 0.06f), s * 0.09f, StrokeCap.Round)
        }
    }

    override val desorden = 2f
}
