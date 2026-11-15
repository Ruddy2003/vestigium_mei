package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
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
import io.github.ruddy2003.vestigium.ui.TrazosGalia
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraContacto
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Galia: Vida en la casa grande del oppidum arverno, en el 52 a. C. (`docs/diseno/temas/galia.md`; la misma casa que su
 * biblioteca). Postes y vigas de roble labrados a azuela, paredes de zarzo de avellano con su barro y su paja picada,
 * techo de paja por dentro sobre sus cabrios, ennegrecido de humo; el hogar con su corro de piedras, los morillos de
 * hierro y el caldero de bronce colgado de su cadena; bancos cubiertos de pieles de lobo (Diodoro 5.28), mantos de lana
 * a cuadros, el escudo largo y las lanzas, el torques y los estáteros del caballo, las ánforas de vino traídas del sur y
 * el telar de pesas. Por la puerta, el oppidum: las casas de paja con su humo, la empalizada y el robledal. La luz viene
 * de arriba a la izquierda y la sombra es violeta (`ui/muebles/Luz.kt`, versión sutil). De noche (modo oscuro) es la misma
 * casa en el verde negro del robledal: la luz cálida solo llega cerca del fuego; por la puerta se ven la luna y, en el
 * llano, las hogueras del campamento romano.
 * - Social: la pared del fondo de la casa grande. Arriba, la paja sobre sus cabrios y la viga tirante; dos postes de
 *   roble; al centro, la manta de lana cruda con su orla tejida a cuadros, colgada de un asta de lanza (el nicho: tu gente
 *   va en la lana); a la izquierda, la puerta que da al oppidum y el manto a cuadros colgado del poste; a la derecha, el
 *   escudo largo con su umbo de aletas entre dos lanzas, el torques colgado de su clavija y el ánfora apoyada en el rincón.
 *   Abajo, el hogar: el corro de piedras, el fuego, los dos morillos con cabeza de buey y el caldero colgado de su cadena
 *   de un travesaño sobre dos horquillas; a cada lado, un banco cubierto con su piel.
 * - Mapas: la mesa del consejo vista desde arriba: tablones de roble con su veta y sus motas, y el cuero de buey tendido
 *   con la forma del animal (el nicho), sujeto con piedras; a la izquierda, la espada larga en su vaina de hierro con el
 *   tahalí de cadena, el cuerno de beber con su remate de bronce, la copa de barro negro y la bolsa con los estáteros del
 *   caballo; a la derecha, el torques, la tablilla de cera con su estilo (recurso), el muérdago y la hoz de oro. Abajo,
 *   el canto de la mesa, los caballetes y el suelo con paja; de noche, el resplandor del hogar entra por la izquierda.
 * - Otras vidas: el fondo de la casa entre sus postes. Arriba, la paja con el carnyx colgado de dos correas y su cabeza
 *   de jabalí asomando sobre la viga, el jamón y los ramos que se secan, y el dintel tallado con la «S» de las monedas;
 *   cinco tramos de barro alisado entre postes de roble con sus zapatas (el nicho) y, al pie, el zarzo donde se cayó el
 *   barro; abajo, el banco corrido con su piel y su manta a cuadros, el balde de duelas con aros de bronce y el ánfora.
 * - Íconos: la puerta del oppidum en su muralla de piedra y vigas (el murus gallicus) con la torre de madera encima; un
 *   cuero ahumado que el estátero del caballo raspa hasta descubrir los ríos de la tierra arverna; y un cuero con el río y
 *   tres alfileres de bronce con su gota de esmalte rojo unidos por el camino.
 * - Notas: el telar de pesas apoyado en la pared: los dos montantes de roble, el enjullo con la tela enrollada y la
 *   cabecera tejida a cuadros, la tela de lana cruda (el nicho), la vara de la calada y los hilos de la urdimbre que bajan
 *   en manojos hasta las pesas de barro; del montante cuelga el huso y al pie está el cesto de los ovillos. Cada nota es
 *   un retazo de cuero curtido prendido a la tela con una fíbula de bronce; su tipo se ve en la esquina (la hoja de roble, la luna nueva, la rueda o el nudo de lana a cuadros) y, si está
 *   fijada, la fíbula es de oro con su gota de coral.
 */
internal object VidaGalia : DisenoVida {
    // ---------------------------------------------------------------- La paleta de la casa grande
    private val ROBLE_HOJA = TrazosGalia.ROBLE
    private val ROBLE_HOJA_SOMBRA = TrazosGalia.ROBLE_SOMBRA
    private val MUERDAGO = TrazosGalia.MUERDAGO
    private val BAYA = TrazosGalia.BAYA
    private val BARRO = TrazosGalia.BARRO
    private val PAJA = TrazosGalia.PAJA
    private val MADERA = TrazosGalia.MADERA
    private val ORO = TrazosGalia.ORO
    private val ORO_BRILLO = TrazosGalia.ORO_BRILLO
    private val BRONCE = TrazosGalia.BRONCE
    private val VERDIN = TrazosGalia.VERDIN
    private val HIERRO = TrazosGalia.HIERRO
    private val LANA_ROJA = TrazosGalia.LANA_ROJA
    private val LANA_VERDE = TrazosGalia.LANA_VERDE
    private val LANA_OCRE = TrazosGalia.LANA_OCRE
    private val HUMO = TrazosGalia.HUMO
    private val NIEBLA = TrazosGalia.NIEBLA
    private val LUNA = TrazosGalia.LUNA
    private val FUEGO = TrazosGalia.FUEGO
    private val FUEGO_HALO = TrazosGalia.FUEGO_HALO
    /** Las hogueras del campamento romano, lejos (el terciario del tema de noche). */
    private val HOGUERA_ROMANA = Color(0xFFE58A5C)

    /** El roble labrado de los postes, las vigas y los tablones: su color, la luz del canto, la sombra y la veta. */
    private val TABLA = Color(0xFF8A6A40)
    private val TABLA_LUZ = Color(0xFFB4905C)
    private val TABLA_OSC = Color(0xFF5E4428)
    private val VETA = Color(0xFF4A3220)
    /** El barro de la pared, su luz, su sombra, el barro alisado de los tramos y la paja picada que lleva dentro. */
    private val BARRO_LUZ = Color(0xFFD0B784)
    private val BARRO_SOMBRA = Color(0xFF8E7448)
    private val ENLUCIDO = Color(0xFFD8C69C)
    private val PAJA_PICADA = Color(0xFFE2D29C)
    /** El zarzo de avellano que se ve donde se cayó el barro. */
    private val ZARZO = Color(0xFF7A5A34)
    private val ZARZO_LUZ = Color(0xFFA6845A)
    private val ZARZO_HONDO = Color(0xFF3E2C1A)
    /** La paja del techo por dentro, ennegrecida de humo, y sus cabrios. */
    private val PAJA_HUMO = Color(0xFF6E5A36)
    private val PAJA_HONDA = Color(0xFF3F3220)
    /** La tierra apisonada del piso y la paja tirada encima. */
    private val TIERRA = Color(0xFF8F7A56)
    private val TIERRA_OSC = Color(0xFF5E4C34)
    /** La lana sin teñir, el cuero curtido y las pieles de lobo. */
    private val LANA_CRUDA = Color(0xFFE6DCC0)
    private val LANA_CRUDA_OSC = Color(0xFFC9BC98)
    private val CUERO = Color(0xFFC6A170)
    private val CUERO_OSC = Color(0xFF8C6A42)
    private val CORREA = Color(0xFF5A3A22)
    private val LOBO = Color(0xFF8E7C62)
    private val LOBO_LUZ = Color(0xFFC8B596)
    private val LOBO_OSC = Color(0xFF4C4033)
    /** Las piedras del hogar y de la muralla. */
    private val PIEDRA = Color(0xFF8C867A)
    private val PIEDRA_OSC = Color(0xFF5E584E)
    /** El barro de las ánforas de vino de Italia y el de las ollas de la casa, bruñido y negro. */
    private val ANFORA = Color(0xFFC88A5C)
    private val ANFORA_LUZ = Color(0xFFE2AE82)
    private val OLLA = Color(0xFF3E3832)
    /** El escudo: tablas forradas de cuero pintado de ocre rojo. */
    private val ESCUDO = Color(0xFF8E4A2E)
    private val HIERRO_LUZ = Color(0xFF9A9C9C)
    private val FRESNO = Color(0xFFB08A58)
    private val CERA = Color(0xFFD9BE82)
    private val CORAL = Color(0xFFC0392B)
    private val RIO = Color(0xFF5F8C86)
    private val TINTA = Color(0xFF20251A)
    /** El cielo de día, sus nubes, los montes lejanos y el cielo de la noche en el robledal. */
    private val CIELO_DIA = Color(0xFFC9D5D2)
    private val NUBE = Color(0xFFF1F0E6)
    private val MONTE = Color(0xFF8E9C7C)
    private val CIELO_NOCHE = Color(0xFF131C17)

    /** El aire de la noche: lo que multiplica cada color, y el verde negro del robledal hacia el que se hunde. */
    private val AIRE = Color(0xFF6F8478)
    private val NOCHE = Color(0xFF0F1410)
    /** La madera y el barro a la luz del hogar. */
    private val CALIDO = Color(0xFFA8652A)

    // ---------------------------------------------------------------- Ayudas

    private fun mez(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** El color de noche: multiplicado por el aire y hundido hacia el verde negro del robledal; de día, tal cual. */
    private fun n(c: Color, o: Boolean): Color {
        if (!o) return c
        return mez(Color(c.red * AIRE.red, c.green * AIRE.green, c.blue * AIRE.blue, c.alpha), NOCHE.copy(alpha = c.alpha), 0.45f)
    }

    /** El color de noche a la luz del hogar: [f] (0 a 1) es cuánto le llega. De día, tal cual. */
    private fun lam(c: Color, o: Boolean, f: Float): Color = if (!o) c else mez(n(c, o), mez(c, CALIDO, 0.38f), f * 0.85f)

    /** El fondo sólido de un nicho: el [material] llevado a la luminancia de la tarjeta. */
    private fun nicho(e: EstadoMueble, o: Boolean, material: Color): Color =
        if (o) mez(e.fondo, n(material, o), 0.22f) else mez(e.fondo, material, 0.32f)

    private fun DrawScope.enLienzo(a: Rect) = Rect(size.width * a.left, size.height * a.top, size.width * a.right, size.height * a.bottom)
    private fun DrawScope.rect(r: Rect, c: Color) = drawRect(c, r.topLeft, r.size)

    /** Un número estable de 0 a 1. */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** El halo del fuego: discos que se suman hacia el centro, sin degradado. */
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

    /** Un camino de puntos [pts] suavizado con curvas por los puntos medios. */
    private fun suave(pts: List<Offset>): Path = Path().apply {
        moveTo(pts[0].x, pts[0].y)
        for (k in 1 until pts.size - 1) {
            val m = Offset((pts[k].x + pts[k + 1].x) / 2, (pts[k].y + pts[k + 1].y) / 2)
            quadraticTo(pts[k].x, pts[k].y, m.x, m.y)
        }
        lineTo(pts.last().x, pts.last().y)
    }

    // ---------------------------------------------------------------- Los materiales de la casa

    /**
     * Un tramo de roble labrado: la base, la veta larga y casi recta, y las motas de los radios (lo que distingue al roble
     * aserrado); [vertical] para los postes, [f] es cuánto le llega el fuego de noche.
     */
    private fun DrawScope.roble(r: Rect, o: Boolean, u: Float, semilla: Int, vertical: Boolean = false, f: Float = 0f, base: Color = TABLA) {
        rect(r, lam(base, o, f))
        val largo = if (vertical) r.height else r.width
        val ancho = if (vertical) r.width else r.height
        if (largo <= 0f || ancho <= 0f) return
        clipRect(r.left, r.top, r.right, r.bottom) {
            val lineas = (ancho / (1.2f * u)).toInt().coerceIn(2, 40)
            for (k in 0 until lineas) {
                val b = ancho * (k + azar(semilla, k)) / lineas
                val amp = (0.15f + 0.55f * azar(semilla, k + 50)) * u
                val fase = azar(semilla, k + 90) * 6.28f
                val ondas = 0.6f + 1.2f * azar(semilla, k + 130)
                val path = Path()
                for (s in 0..14) {
                    val t = largo * s / 14
                    val v = b + amp * sin(fase + ondas * 6.28f * s / 14)
                    val x = if (vertical) r.left + v else r.left + t
                    val y = if (vertical) r.top + t else r.top + v
                    if (s == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                val oscura = azar(semilla, k + 170) < 0.35f
                drawPath(path, lam(if (oscura) VETA else TABLA_OSC, o, f).copy(alpha = if (oscura) 0.42f else 0.26f), style = Stroke((0.14f + 0.22f * azar(semilla, k + 210)) * u))
            }
            // Las motas de los radios: rayitas claras y cortas a lo largo de la veta.
            val motas = (largo * ancho / (55f * u * u)).toInt().coerceIn(2, 70)
            for (k in 0 until motas) {
                val t = largo * azar(semilla, k + 400); val v = ancho * azar(semilla, k + 470)
                val l = (0.6f + 1.4f * azar(semilla, k + 540)) * u
                val a = if (vertical) Offset(r.left + v, r.top + t) else Offset(r.left + t, r.top + v)
                val b = if (vertical) Offset(a.x, a.y + l) else Offset(a.x + l, a.y)
                drawLine(lam(TABLA_LUZ, o, f).copy(alpha = 0.4f), a, b, 0.32f * u, StrokeCap.Round)
            }
        }
    }

    /**
     * Un poste de roble labrado a azuela, de pie: la veta, la cara en luz a la izquierda y el lado en sombra a la derecha,
     * las huellas de la azuela y su sombra en la pared.
     */
    private fun DrawScope.poste(r: Rect, o: Boolean, u: Float, semilla: Int, f: Float = 0f) {
        drawRect(sombraMueble(if (o) 0.3f else 0.2f), Offset(r.right, r.top), Size(1.8f * u, r.height))
        drawRect(sombraMueble(if (o) 0.16f else 0.1f), Offset(r.right, r.top), Size(3.4f * u, r.height))
        roble(r, o, u, semilla, vertical = true, f = f)
        drawRect(luzMueble(if (o) 0.04f + 0.1f * f else 0.26f), r.topLeft, Size(r.width * 0.3f, r.height))
        drawRect(sombraMueble(if (o) 0.34f else 0.26f), Offset(r.left + r.width * 0.7f, r.top), Size(r.width * 0.3f, r.height))
        // Las huellas de la azuela: golpes cóncavos a lo alto, con su borde en luz.
        var y = r.top + 3f * u * azar(semilla, 3)
        var k = 0
        while (y < r.bottom - 2f * u) {
            val x0 = r.left + r.width * (0.1f + 0.3f * azar(semilla, k + 20))
            val x1 = x0 + r.width * (0.35f + 0.25f * azar(semilla, k + 40))
            drawArc(sombraMueble(if (o) 0.25f else 0.18f), 20f, 140f, false, Offset(x0, y), Size(x1 - x0, 1.6f * u), style = Stroke(0.3f * u))
            drawLine(luzMueble(if (o) 0.04f else 0.22f), Offset(x0 + 0.3f * u, y + 0.2f * u), Offset(x1 - 0.3f * u, y + 0.2f * u), 0.2f * u)
            y += (6f + 7f * azar(semilla, k + 60)) * u; k++
        }
        drawLine(sombraMueble(0.45f), Offset(r.right, r.top), Offset(r.right, r.bottom), 0.25f * u)
    }

    /** Una viga de roble labrada, tendida: la veta, la cara de arriba en luz, la de abajo en sombra y su sombra en la pared. */
    private fun DrawScope.viga(r: Rect, o: Boolean, u: Float, semilla: Int, sombraMuro: Boolean = true, f: Float = 0f) {
        if (sombraMuro) {
            drawRect(sombraMueble(if (o) 0.3f else 0.22f), Offset(r.left, r.bottom), Size(r.width, 1.6f * u))
            drawRect(sombraMueble(if (o) 0.16f else 0.11f), Offset(r.left, r.bottom), Size(r.width, 3.4f * u))
        }
        roble(r, o, u, semilla, f = f)
        drawRect(luzMueble(if (o) 0.05f + 0.1f * f else 0.3f), r.topLeft, Size(r.width, r.height * 0.22f))
        drawRect(sombraMueble(if (o) 0.34f else 0.26f), Offset(r.left, r.top + r.height * 0.72f), Size(r.width, r.height * 0.28f))
        // Las huellas de la azuela, en diagonal.
        var x = r.left + 4f * u * azar(semilla, 5)
        var k = 0
        while (x < r.right - 3f * u) {
            val l = (3f + 3f * azar(semilla, k + 80)) * u
            drawLine(sombraMueble(if (o) 0.22f else 0.16f), Offset(x, r.top + r.height * 0.3f), Offset(x + l * 0.3f, r.top + r.height * 0.68f), 0.3f * u)
            x += (8f + 9f * azar(semilla, k + 90)) * u; k++
        }
        drawLine(sombraMueble(0.45f), Offset(r.left, r.bottom), Offset(r.right, r.bottom), 0.25f * u)
    }

    /**
     * La paja del techo vista por dentro: los haces en hileras, ennegrecidos de humo, las latas que los sujetan y los
     * cabrios redondos que bajan hacia la viga, abiertos en abanico desde la cumbrera.
     */
    private fun DrawScope.techo(r: Rect, o: Boolean, u: Float, semilla: Int, cumbre: Float = 0.5f) {
        rect(r, n(PAJA_HONDA, o))
        clipRect(r.left, r.top, r.right, r.bottom) {
            // Los haces de paja, en hileras: cada hilera cuelga sobre la de abajo y deja las puntas desparejas.
            val curso = 3.4f * u
            var y = r.top - curso * 0.6f
            var fila = 0
            while (y < r.bottom) {
                drawRect(n(mez(PAJA_HUMO, PAJA_HONDA, 0.35f), o), Offset(r.left, y), Size(r.width, curso))
                var x = r.left - 2f * u + 0.5f * u * azar(semilla, fila)
                var k = 0
                while (x < r.right + 2f * u) {
                    val inclina = if (x < r.left + r.width * cumbre) 0.22f else -0.22f
                    val l = curso * (0.85f + 0.5f * azar(semilla + fila, k))
                    val col = when ((k + fila * 2) % 4) { 0 -> PAJA; 1 -> mez(PAJA, PAJA_HUMO, 0.4f); 2 -> PAJA_HUMO; else -> mez(PAJA, PAJA_HUMO, 0.7f) }
                    drawLine(n(col, o).copy(alpha = 0.45f + 0.35f * azar(semilla + fila, k + 9)), Offset(x, y), Offset(x + inclina * l, y + l), 0.34f * u, StrokeCap.Round)
                    x += 0.55f * u; k++
                }
                // La sombra que la hilera de arriba deja en la de abajo.
                drawRect(sombraMueble(if (o) 0.3f else 0.22f), Offset(r.left, y + curso * 0.9f), Size(r.width, 0.7f * u))
                y += curso; fila++
            }
            // Las latas de avellano, de través, cada dos hileras.
            var yl = r.top + curso * 1.3f
            while (yl < r.bottom) {
                drawLine(sombraMueble(if (o) 0.4f else 0.3f), Offset(r.left, yl + 0.5f * u), Offset(r.right, yl + 0.5f * u), 0.6f * u)
                drawLine(n(ZARZO, o), Offset(r.left, yl), Offset(r.right, yl), 0.65f * u)
                drawLine(luzMueble(if (o) 0.03f else 0.18f), Offset(r.left, yl - 0.2f * u), Offset(r.right, yl - 0.2f * u), 0.2f * u)
                yl += curso * 2f
            }
            // Los cabrios: rollizos que bajan abiertos en abanico desde la cumbrera, con su lado de luz.
            val apice = Offset(r.left + r.width * cumbre, r.top - r.height * 1.3f)
            val pasos = 8
            for (k in 0..pasos) {
                val xb = r.left - r.width * 0.12f + r.width * 1.24f * k / pasos
                val g = 2.1f * u
                drawLine(sombraMueble(if (o) 0.4f else 0.3f), Offset(apice.x + 0.8f * u, apice.y), Offset(xb + 1.1f * u, r.bottom), g * 1.3f)
                drawLine(n(mez(TABLA, PAJA_HONDA, 0.3f), o), apice, Offset(xb, r.bottom), g)
                drawLine(luzMueble(if (o) 0.04f else 0.2f), Offset(apice.x - g * 0.28f, apice.y), Offset(xb - g * 0.28f, r.bottom), g * 0.28f)
                drawLine(sombraMueble(if (o) 0.3f else 0.22f), Offset(apice.x + g * 0.3f, apice.y), Offset(xb + g * 0.3f, r.bottom), g * 0.3f)
            }
            // El hollín que se junta arriba.
            drawRect(n(PAJA_HONDA, o).copy(alpha = 0.3f), r.topLeft, Size(r.width, r.height * 0.4f))
            drawRect(sombraMueble(if (o) 0.25f else 0.14f), r.topLeft, r.size)
        }
    }

    /**
     * La pared de zarzo y barro: el barro con sus pinceladas, la paja picada que se ve en él, las pasadas de la mano que lo
     * alisó, unas grietas finas y, en [huecos], los desconchones por donde asoma el zarzo.
     */
    private fun DrawScope.muroBarro(r: Rect, o: Boolean, u: Float, semilla: Int, huecos: List<Rect> = emptyList(), base: Color = BARRO) {
        rect(r, n(base, o))
        val area = r.width * r.height / (u * u)
        pinceladas(r, n(BARRO_LUZ, o), semilla, (area / 260f).toInt().coerceIn(8, 44), u, if (o) 0.06f else 0.14f)
        pinceladas(r, n(BARRO_SOMBRA, o), semilla + 5, (area / 380f).toInt().coerceIn(6, 34), u, 0.1f)
        clipRect(r.left, r.top, r.right, r.bottom) {
            // Las pasadas de la mano: arcos anchos y tenues.
            val pasadas = (area / 300f).toInt().coerceIn(4, 30)
            for (k in 0 until pasadas) {
                val c = Offset(r.left + r.width * azar(semilla, k + 700), r.top + r.height * azar(semilla, k + 730))
                val rr = (4f + 5f * azar(semilla, k + 760)) * u
                drawArc(n(BARRO_LUZ, o).copy(alpha = if (o) 0.08f else 0.22f), 200f + 40f * azar(semilla, k), 70f, false, Offset(c.x - rr, c.y - rr), Size(rr * 2, rr * 2), style = Stroke(0.5f * u))
            }
            // La paja picada.
            val pajas = (area / 14f).toInt().coerceIn(20, 420)
            for (k in 0 until pajas) {
                val c = Offset(r.left + r.width * azar(semilla, k + 1000), r.top + r.height * azar(semilla, k + 1500))
                val a = azar(semilla, k + 2000) * 6.28f
                val l = (0.5f + 0.9f * azar(semilla, k + 2500)) * u
                val clara = k % 3 != 0
                drawLine(n(if (clara) PAJA_PICADA else BARRO_SOMBRA, o).copy(alpha = if (clara) 0.5f else 0.4f), c, Offset(c.x + l * cos(a), c.y + l * sin(a)), 0.2f * u, StrokeCap.Round)
            }
            // Las grietas del barro seco.
            repeat(3) { g ->
                var x = r.left + r.width * azar(semilla, g + 300); var y = r.top + r.height * (0.15f + 0.6f * azar(semilla, g + 310))
                val path = Path().apply { moveTo(x, y) }
                for (s in 0 until 5) { x += (azar(semilla, g * 10 + s) - 0.5f) * 3f * u; y += (1.2f + 1.8f * azar(semilla, g * 10 + s + 5)) * u; path.lineTo(x, y) }
                drawPath(path, n(BARRO_SOMBRA, o).copy(alpha = 0.55f), style = Stroke(0.22f * u, join = StrokeJoin.Round))
            }
        }
        huecos.forEachIndexed { i, hueco -> desconchon(hueco, o, u, semilla + 11 * i) }
    }

    /** Un desconchón del barro: el zarzo de avellano de debajo, tejido entre sus estacas, y el canto del barro alrededor. */
    private fun DrawScope.desconchon(r: Rect, o: Boolean, u: Float, semilla: Int) {
        val forma = mancha(r, semilla, 16, 0.3f)
        drawPath(forma, n(ZARZO_HONDO, o))
        clipPath(forma) { zarzo(r.inflate(2f * u), o, u, semilla) }
        clipPath(forma) { translate(-0.5f * u, -0.6f * u) { drawPath(forma, sombraMueble(if (o) 0.45f else 0.35f), style = Stroke(1.2f * u)) } }
        translate(0.2f * u, 0.25f * u) { drawPath(forma, luzMueble(if (o) 0.06f else 0.35f), style = Stroke(0.4f * u)) }
        drawPath(forma, n(BARRO_SOMBRA, o), style = Stroke(0.25f * u))
    }

    /**
     * El zarzo: estacas de pie y varas de avellano tejidas entre ellas; cada vara pasa por delante de una estaca y por
     * detrás de la siguiente, y la hilera de abajo al revés.
     */
    private fun DrawScope.zarzo(r: Rect, o: Boolean, u: Float, semilla: Int, f: Float = 0f) {
        rect(r, lam(ZARZO_HONDO, o, f))
        val paso = 5.5f * u
        val fila = 1.35f * u
        clipRect(r.left, r.top, r.right, r.bottom) {
            val estacas = ArrayList<Float>()
            var xe = r.left + paso * (0.2f + 0.5f * azar(semilla, 1))
            while (xe < r.right + paso) { estacas.add(xe); xe += paso }
            // Las estacas, detrás.
            estacas.forEach { x -> drawRect(lam(mez(ZARZO, ZARZO_HONDO, 0.4f), o, f), Offset(x - 0.8f * u, r.top), Size(1.6f * u, r.height)) }
            var y = r.top + fila * 0.1f
            var k = 0
            while (y < r.bottom + fila) {
                // La vara entera, con su luz arriba y su sombra abajo, apenas ondulada.
                val vara = Path().apply {
                    moveTo(r.left - u, y)
                    estacas.forEachIndexed { j, x -> lineTo(x, y + (if ((j + k) % 2 == 0) -0.18f else 0.18f) * u) }
                }
                drawPath(vara, sombraMueble(if (o) 0.4f else 0.32f), style = Stroke(fila * 0.95f))
                drawPath(vara, lam(if (k % 3 == 1) ZARZO_LUZ else mez(ZARZO, ZARZO_LUZ, 0.4f), o, f), style = Stroke(fila * 0.72f))
                translate(0f, -fila * 0.18f) { drawPath(vara, luzMueble(if (o) 0.04f else 0.22f), style = Stroke(fila * 0.18f)) }
                // Las estacas que quedan delante de esta vara.
                estacas.forEachIndexed { j, x ->
                    if ((j + k) % 2 == 0) {
                        drawRect(lam(mez(ZARZO, ZARZO_HONDO, 0.15f), o, f), Offset(x - 0.8f * u, y - fila * 0.5f), Size(1.6f * u, fila))
                        drawLine(luzMueble(if (o) 0.03f else 0.15f), Offset(x - 0.5f * u, y - fila * 0.5f), Offset(x - 0.5f * u, y + fila * 0.5f), 0.25f * u)
                    }
                }
                y += fila; k++
            }
        }
    }

    /** El piso de tierra apisonada, con la paja tirada encima. */
    private fun DrawScope.tierra(r: Rect, o: Boolean, u: Float, semilla: Int) {
        rect(r, n(TIERRA, o))
        pinceladas(r, n(TIERRA_OSC, o), semilla, 14, u, 0.14f)
        pinceladas(r, n(BARRO_LUZ, o), semilla + 1, 10, u, if (o) 0.05f else 0.12f)
        clipRect(r.left, r.top, r.right, r.bottom) {
            val pajas = (r.width * r.height / (u * u) / 9f).toInt().coerceIn(20, 300)
            for (k in 0 until pajas) {
                val c = Offset(r.left + r.width * azar(semilla, k + 30), r.top + r.height * azar(semilla, k + 330))
                val a = (azar(semilla, k + 630) - 0.5f) * 1.2f
                val l = (1f + 1.8f * azar(semilla, k + 930)) * u
                drawLine(n(if (k % 4 == 0) TIERRA_OSC else PAJA, o).copy(alpha = 0.3f + 0.3f * azar(semilla, k + 1230)), c, Offset(c.x + l * cos(a), c.y + l * sin(a) * 0.4f), 0.22f * u, StrokeCap.Round)
            }
        }
    }

    /**
     * La lana tejida a cuadros sobre [zona]: el fondo del [base], las franjas verdes, ocres y oscuras que se cruzan (más
     * hondas donde se cruzan) y la sarga en diagonal. [paso] es lo que mide una repetición del dibujo.
     */
    private fun DrawScope.cuadros(zona: Rect, o: Boolean, u: Float, paso: Float, f: Float = 0f, base: Color = LANA_ROJA, fuerza: Float = 1f) {
        rect(zona, lam(base, o, f))
        clipRect(zona.left, zona.top, zona.right, zona.bottom) {
            val franjas = listOf(Triple(0f, 0.3f, LANA_VERDE), Triple(0.4f, 0.06f, LANA_OCRE), Triple(0.58f, 0.05f, Color(0xFF2A2018)), Triple(0.76f, 0.06f, LANA_OCRE))
            for (vertical in listOf(true, false)) {
                var s = (if (vertical) zona.left else zona.top) - paso * 0.35f
                val fin = if (vertical) zona.right else zona.bottom
                while (s < fin) {
                    franjas.forEach { (t, ancho, col) ->
                        val a = (if (ancho > 0.1f) 0.5f else 0.75f) * fuerza
                        if (vertical) drawRect(lam(col, o, f).copy(alpha = a), Offset(s + paso * t, zona.top), Size(paso * ancho, zona.height))
                        else drawRect(lam(col, o, f).copy(alpha = a), Offset(zona.left, s + paso * t), Size(zona.width, paso * ancho))
                    }
                    s += paso
                }
            }
            // La sarga: el tejido en diagonal.
            var d = zona.left - zona.height
            while (d < zona.right) {
                drawLine(sombraMueble(0.08f * fuerza), Offset(d, zona.bottom), Offset(d + zona.height, zona.top), 0.2f * u)
                d += 0.7f * u
            }
        }
    }

    /** Una clavija de madera clavada en la pared, vista de frente. */
    private fun DrawScope.clavija(c: Offset, o: Boolean, u: Float, f: Float = 0f) {
        drawCircle(sombraMueble(0.35f), 1.1f * u, Offset(c.x + 0.4f * u, c.y + 0.5f * u))
        drawCircle(lam(TABLA_OSC, o, f), 1f * u, c)
        drawCircle(lam(TABLA_LUZ, o, f), 0.6f * u, Offset(c.x - 0.2f * u, c.y - 0.2f * u))
        drawCircle(lam(VETA, o, f).copy(alpha = 0.5f), 0.25f * u, Offset(c.x - 0.1f * u, c.y - 0.1f * u))
    }

    /** Una cadena de eslabones ovalados de [a] a [b]: uno de frente y otro de canto. */
    private fun DrawScope.cadena(a: Offset, b: Offset, eslabon: Float, o: Boolean, u: Float, f: Float) {
        val dx = b.x - a.x; val dy = b.y - a.y
        val l = sqrt(dx * dx + dy * dy).coerceAtLeast(0.01f)
        val ang = atan2(dy, dx) * 180f / PI.toFloat()
        val n0 = (l / (eslabon * 0.78f)).toInt().coerceAtLeast(1)
        for (k in 0 until n0) {
            val t = (k + 0.5f) / n0
            val c = Offset(a.x + dx * t, a.y + dy * t)
            rotate(ang, c) {
                if (k % 2 == 0) {
                    drawOval(lam(HIERRO, o, f), Offset(c.x - eslabon / 2, c.y - eslabon * 0.3f), Size(eslabon, eslabon * 0.6f), style = Stroke(0.35f * u))
                    drawArc(luzMueble(if (o) 0.06f + 0.2f * f else 0.4f), 180f, 90f, false, Offset(c.x - eslabon / 2, c.y - eslabon * 0.3f), Size(eslabon, eslabon * 0.6f), style = Stroke(0.18f * u))
                } else drawLine(lam(mez(HIERRO, Color.Black, 0.3f), o, f), Offset(c.x - eslabon / 2, c.y), Offset(c.x + eslabon / 2, c.y), 0.4f * u, StrokeCap.Round)
            }
        }
    }

    // ---------------------------------------------------------------- Social

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.235f, 0.2f, 0.765f, 0.71f)
        MuebleVida.MAPAS -> Rect(0.25f, 0.10f, 0.75f, 0.72f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.06f, 0.29f, 0.94f, 0.815f)
        MuebleVida.NOTAS -> Rect(0.075f, 0.14f, 0.925f, 0.815f)
    }

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val o = oscuro
        val ni = enLienzo(area(MuebleVida.SOCIAL))
        val techoB = h * 0.1f; val vigaB = h * 0.155f
        val suelo = h * 0.80f
        val fuego = Offset(w * 0.5f, h * 0.935f)

        // La pared de zarzo y barro, con sus desconchones; el techo de paja y la viga tirante.
        muroBarro(Rect(0f, techoB, w, suelo), o, u, e.semilla + 2, listOf(
            Rect(w * 0.835f, h * 0.665f, w * 0.89f, h * 0.76f), Rect(w * 0.2f, h * 0.735f, w * 0.232f, h * 0.79f), Rect(w * 0.005f, h * 0.175f, w * 0.075f, h * 0.24f)))
        techo(Rect(0f, 0f, w, techoB + u), o, u, e.semilla)
        // El humo del hogar, que se junta bajo la paja.
        if (!o) for (k in 0 until 6) drawOval(HUMO.copy(alpha = 0.12f + 0.08f * azar(e.semilla, k)),
            Offset(w * (0.2f + 0.5f * azar(e.semilla, k + 10)), h * (0.0f + 0.05f * azar(e.semilla, k + 20))), Size(w * (0.18f + 0.15f * azar(e.semilla, k + 30)), h * 0.06f))

        // El piso de tierra apisonada.
        val piso = Rect(0f, suelo, w, h)
        tierra(piso, o, u, e.semilla + 30)
        drawRect(sombraMueble(if (o) 0.4f else 0.28f), Offset(0f, suelo), Size(w, 1.2f * u))
        // De noche, lo que queda lejos del fuego se hunde más.
        if (o) drawRect(NOCHE.copy(alpha = 0.3f), Offset.Zero, size)

        // La puerta que da al oppidum: lo de afuera, las jambas y el dintel de roble y el umbral.
        val puerta = Rect(w * 0.03f, h * 0.27f, w * 0.15f, suelo)
        val jamba = 2.8f * u
        poste(Rect(puerta.left - jamba, puerta.top - 1f * u, puerta.left, suelo), o, u, e.semilla + 21)
        clipRect(puerta.left, puerta.top, puerta.right, puerta.bottom) { afuera(puerta, o, u, e.semilla) }
        drawRect(sombraMueble(if (o) 0.4f else 0.3f), puerta.topLeft, Size(puerta.width, 1.4f * u))
        poste(Rect(puerta.right, puerta.top - 1f * u, puerta.right + jamba, suelo), o, u, e.semilla + 22)
        // La luz que entra por la puerta pinta el canto de dentro de la jamba derecha.
        drawRect(if (o) LUNA.copy(alpha = 0.12f) else luzMueble(0.4f), Offset(puerta.right, puerta.top + u), Size(0.7f * u, puerta.height - u))
        viga(Rect(puerta.left - jamba - 1.6f * u, puerta.top - 4.4f * u, puerta.right + jamba + 1.6f * u, puerta.top), o, u, e.semilla + 9)
        val umbral = Rect(puerta.left - jamba - 0.6f * u, suelo - 0.6f * u, puerta.right + jamba + 0.6f * u, suelo + 1.8f * u)
        roble(umbral, o, u, e.semilla + 23)
        drawLine(luzMueble(if (o) 0.08f else 0.5f), umbral.topLeft, Offset(umbral.right, umbral.top), 0.4f * u)
        drawRect(sombraMueble(0.3f), Offset(umbral.left, umbral.bottom), Size(umbral.width, 0.8f * u))
        // El rayo que entra por la puerta y cae en el piso: de día, sol; de noche, la luna.
        val rayo = Path().apply {
            moveTo(puerta.left + 0.6f * u, umbral.bottom); lineTo(puerta.right, umbral.bottom)
            lineTo(puerta.right + w * 0.17f, h); lineTo(puerta.left + w * 0.08f, h); close()
        }
        drawPath(rayo, if (o) LUNA.copy(alpha = 0.08f) else luzMueble(0.3f))

        // Los dos postes grandes de la casa, bajo la viga tirante.
        val posteI = Rect(w * 0.17f, vigaB - u, w * 0.198f, suelo + 0.8f * u)
        val posteD = Rect(w * 0.802f, vigaB - u, w * 0.83f, suelo + 0.8f * u)
        poste(posteI, o, u, e.semilla + 4, 0.1f)
        poste(posteD, o, u, e.semilla + 5, 0.1f)
        viga(Rect(0f, techoB, w, vigaB), o, u, e.semilla + 1)
        // Las cabezas de los postes, que asoman sobre la viga, con su espiga.
        listOf(posteI, posteD).forEach { p ->
            drawRect(sombraMueble(0.3f), Offset(p.left - 0.6f * u, vigaB), Size(p.width + 1.2f * u, 0.8f * u))
        }

        // De noche, el fuego alumbra lo que tiene cerca: su halo en el muro y en el piso, fuera de la lana.
        val foco = Offset(fuego.x, fuego.y - 5f * u)
        if (o) fueraDe(ni.inflate(1.2f * u)) { halo(foco, w * 0.36f, FUEGO_HALO, 1.15f); halo(foco, w * 0.13f, FUEGO, 0.9f) }

        // A la izquierda, el manto a cuadros colgado de una clavija del poste.
        manto(Offset(posteI.center.x, h * 0.3f), 7f * u, h * 0.33f, o, u, e.semilla + 40, 0.12f)

        // A la derecha: dos lanzas cruzadas, el escudo largo entre ellas, el torques en su clavija del poste y el ánfora.
        lanza(Offset(w * 0.858f, h * 0.79f), Offset(w * 0.975f, h * 0.19f), o, u, 0.1f)
        lanza(Offset(w * 0.968f, h * 0.79f), Offset(w * 0.85f, h * 0.2f), o, u, 0.1f)
        escudo(Rect(w * 0.864f, h * 0.255f, w * 0.958f, h * 0.6f), o, u, 0.12f)
        val clT = Offset(posteD.center.x, h * 0.27f)
        clavija(clT, o, u, 0.1f)
        with(TrazosGalia) {
            val oro = lam(ORO, o, 0.12f); val brillo = lam(ORO_BRILLO, o, 0.12f)
            torques(Offset(clT.x, clT.y + 3.4f * u), 3.6f * u, 0.95f * u, oro, brillo)
        }
        olla(Offset(w * 0.872f, h * 0.975f), w * 0.05f, h * 0.12f, o, u, 0.3f)
        anfora(Offset(w * 0.945f, h * 0.985f), h * 0.36f, w * 0.056f, -5f, o, u, 0.2f)

        // La manta de lana cruda con su orla a cuadros, colgada de un asta de lanza: el nicho donde va tu gente.
        val asta = ni.top - 3.4f * u
        val tela = Rect(ni.left - 3.2f * u, asta, ni.right + 3.2f * u, ni.bottom + 4.4f * u)
        sombraPieza(tela, 3f * u, if (o) 0.45f else 0.3f)
        val fq = 0.0f
        cuadros(Rect(tela.left, tela.top, tela.right, ni.top), o, u, 9f * u, fq)
        cuadros(Rect(tela.left, ni.bottom, tela.right, tela.bottom), o, u, 9f * u, 0.35f)
        cuadros(Rect(tela.left, ni.top, ni.left, ni.bottom), o, u, 9f * u, fq)
        cuadros(Rect(ni.right, ni.top, tela.right, ni.bottom), o, u, 9f * u, fq)
        // La luz a la izquierda y la sombra a la derecha de los pliegues de la orla.
        drawRect(luzMueble(if (o) 0.03f else 0.18f), Offset(tela.left, tela.top), Size(ni.left - tela.left, tela.height))
        drawRect(sombraMueble(if (o) 0.25f else 0.16f), Offset(ni.right, tela.top), Size(tela.right - ni.right, tela.height))
        // Los flecos: la urdimbre anudada en manojos.
        var xf = tela.left + 0.5f * u
        var kf = 0
        while (xf < tela.right) {
            val col = if (kf % 3 == 1) LANA_OCRE else LANA_ROJA
            drawLine(lam(col, o, 0.5f), Offset(xf, tela.bottom), Offset(xf + 0.2f * u, tela.bottom + 1.6f * u + 0.6f * u * azar(kf, 1)), 0.32f * u, StrokeCap.Round)
            if (kf % 4 == 0) drawCircle(lam(mez(col, Color.Black, 0.25f), o, 0.5f), 0.45f * u, Offset(xf + 0.6f * u, tela.bottom + 0.3f * u))
            xf += 0.8f * u; kf++
        }
        rect(ni, nicho(e, o, LANA_CRUDA))
        clipRect(ni.left, ni.top, ni.right, ni.bottom) {
            // La lana: la trama muy tenue, a lo ancho y a lo alto.
            val hebra = (if (o) LANA_CRUDA else LANA_CRUDA_OSC).copy(alpha = if (o) 0.03f else 0.1f)
            var yy = ni.top + 0.9f * u
            while (yy < ni.bottom) { drawLine(hebra, Offset(ni.left, yy), Offset(ni.right, yy), 0.18f * u); yy += 1.1f * u }
            var xx = ni.left + 0.9f * u
            while (xx < ni.right) { drawLine(hebra.copy(alpha = hebra.alpha * 0.6f), Offset(xx, ni.top), Offset(xx, ni.bottom), 0.15f * u); xx += 1.3f * u }
            drawRect(sombraMueble(if (o) 0.12f else 0.07f), ni.topLeft, Size(ni.width, 2f * u))
        }
        // Las presillas de cuero que abrazan el asta, y el asta de fresno con su punta de hierro, en dos clavijas.
        val presillas = 10
        for (k in 0..presillas) {
            val x = tela.left + 1f * u + (tela.width - 2f * u) * k / presillas
            drawRoundRect(n(CORREA, o), Offset(x - 0.9f * u, asta - 1.4f * u), Size(1.8f * u, 2.6f * u), CornerRadius(0.5f * u))
            drawLine(luzMueble(if (o) 0.05f else 0.25f), Offset(x - 0.5f * u, asta - 1.2f * u), Offset(x - 0.5f * u, asta + 0.8f * u), 0.2f * u)
        }
        val av = Rect(tela.left - 3f * u, asta - 1.5f * u, tela.right + 2f * u, asta + 0.1f * u)
        drawRect(sombraMueble(0.3f), Offset(av.left + 0.6f * u, av.bottom), Size(av.width, 1f * u))
        drawRoundRect(n(FRESNO, o), av.topLeft, av.size, CornerRadius(av.height / 2))
        drawLine(luzMueble(if (o) 0.08f else 0.45f), Offset(av.left + u, av.top + 0.35f * u), Offset(av.right - u, av.top + 0.35f * u), 0.3f * u, StrokeCap.Round)
        puntaLanza(Offset(av.right, av.center.y), 0f, 8f * u, o, u)
        // El regatón de la otra punta.
        drawPath(Path().apply { moveTo(av.left, av.top); lineTo(av.left - 2.4f * u, av.center.y); lineTo(av.left, av.bottom); close() }, n(HIERRO, o))
        listOf(av.left + 2.2f * u, av.right - 2.4f * u).forEach { clavija(Offset(it, av.center.y + 0.1f * u), o, u) }

        // El hogar: el travesaño sobre sus horquillas, el caldero colgado de la cadena, los morillos y el fuego.
        val barra = h * 0.735f
        val hI = w * 0.418f; val hD = w * 0.582f
        listOf(hI, hD).forEachIndexed { k, x -> horquilla(Offset(x, h * 0.958f), barra, o, u, e.semilla + 50 + k) }
        val travesano = Rect(hI - 1.6f * u, barra - 0.6f * u, hD + 1.6f * u, barra + 0.9f * u)
        drawRect(sombraMueble(0.3f), Offset(travesano.left + 0.5f * u, travesano.bottom), Size(travesano.width, 0.8f * u))
        drawRoundRect(lam(TABLA, o, 0.75f), travesano.topLeft, travesano.size, CornerRadius(travesano.height / 2))
        drawLine(luzMueble(if (o) 0.15f else 0.4f), Offset(travesano.left + u, travesano.top + 0.3f * u), Offset(travesano.right - u, travesano.top + 0.3f * u), 0.28f * u, StrokeCap.Round)
        val boca = h * 0.79f
        val anchoC = w * 0.085f
        val gancho = Offset(fuego.x, barra + 2.6f * u)
        cadena(Offset(fuego.x, barra + 0.4f * u), gancho, 1.5f * u, o, u, 0.85f)
        listOf(-1f, 1f).forEach { s -> cadena(gancho, Offset(fuego.x + s * anchoC * 0.5f, boca - 0.3f * u), 1.3f * u, o, u, 0.9f) }
        drawCircle(lam(HIERRO, o, 0.9f), 0.7f * u, gancho, style = Stroke(0.35f * u))
        hogar(fuego, w * 0.09f, o, u, e.semilla + 70)
        with(TrazosGalia) { hoguera(Offset(fuego.x, fuego.y - 0.4f * u), w * 0.075f, 0.72f, lam(MADERA, o, 1f)) }
        listOf(-1f, 1f).forEach { s -> morillo(Offset(fuego.x + s * w * 0.055f, h * 0.968f), h * 0.1f, s, o, u) }
        caldero(Offset(fuego.x, boca), anchoC, o, u, 0.95f)

        // Los dos bancos, cada uno con su piel de lobo; en el de la izquierda, un manto a cuadros doblado.
        banco(Rect(w * 0.235f, h * 0.875f, w * 0.385f, h * 0.985f), o, u, e.semilla + 60, 0.55f, doblado = true)
        banco(Rect(w * 0.615f, h * 0.875f, w * 0.765f, h * 0.985f), o, u, e.semilla + 61, 0.55f)

        granoMueble(Rect(0f, 0f, w, h), o)
    }

    /**
     * Lo que se ve por la puerta: el oppidum. De día, el cielo con nubes, el robledal en los montes, la empalizada y las
     * casas de paja con su humo; de noche, la luna, las estrellas, el mismo oppidum a oscuras con alguna puerta encendida
     * y, abajo en el llano, la línea de las hogueras del campamento romano.
     */
    private fun DrawScope.afuera(r: Rect, o: Boolean, u: Float, semilla: Int) {
        val horizonte = r.top + r.height * 0.36f
        val llano = r.top + r.height * 0.43f
        val muralla = r.top + r.height * 0.6f
        if (o) {
            rect(r, CIELO_NOCHE)
            for (k in 0 until 16) drawCircle(LUNA.copy(alpha = 0.3f + 0.5f * azar(semilla, k)), u * (0.1f + 0.18f * azar(semilla, k + 20)),
                Offset(r.left + r.width * azar(semilla, k + 40), r.top + (horizonte - r.top) * azar(semilla, k + 60)))
            val luna = Offset(r.left + r.width * 0.66f, r.top + r.height * 0.13f)
            halo(luna, r.width * 0.42f, LUNA, 0.5f, 14)
            drawCircle(LUNA, r.width * 0.11f, luna)
            drawCircle(mez(LUNA, CIELO_NOCHE, 0.12f), r.width * 0.035f, Offset(luna.x + r.width * 0.03f, luna.y - r.width * 0.02f))
        } else {
            rect(r, CIELO_DIA)
            rect(Rect(r.left, horizonte - r.height * 0.1f, r.right, horizonte), mez(CIELO_DIA, NIEBLA, 0.6f))
            listOf(0.18f to 0.3f, 0.3f to 0.75f).forEachIndexed { k, (fy, fx) ->
                val c = Offset(r.left + r.width * fx, r.top + r.height * fy)
                drawOval(NUBE, Offset(c.x - r.width * 0.35f, c.y - r.height * 0.02f), Size(r.width * 0.7f, r.height * 0.045f))
                drawOval(NUBE, Offset(c.x - r.width * 0.18f - k * r.width * 0.05f, c.y - r.height * 0.045f), Size(r.width * 0.36f, r.height * 0.05f))
                drawOval(mez(NUBE, CIELO_DIA, 0.5f), Offset(c.x - r.width * 0.3f, c.y + r.height * 0.012f), Size(r.width * 0.55f, r.height * 0.015f))
            }
        }
        // Los montes del robledal: las copas redondas, más pálidas cuanto más lejos.
        for (capa in 0..1) {
            val base = horizonte + r.height * 0.04f * capa
            val col = if (o) mez(CIELO_NOCHE, ROBLE_HOJA_SOMBRA, 0.35f + 0.25f * capa) else mez(MONTE, ROBLE_HOJA, 0.15f + 0.4f * capa)
            val path = Path().apply {
                moveTo(r.left, r.bottom); lineTo(r.left, base)
                var x = r.left
                var k = 0
                while (x < r.right + 2f * u) {
                    val rr = (1.6f + 1.4f * azar(semilla + capa, k)) * u
                    quadraticTo(x + rr * 0.5f, base - rr * (1.4f + 0.6f * azar(semilla + capa, k + 9)) - (if (capa == 0) 2f * u * sin(x / r.width * 3f) else 0f), x + rr, base - 0.4f * u)
                    x += rr; k++
                }
                lineTo(r.right, r.bottom); close()
            }
            drawPath(path, col)
        }
        // El llano al pie del oppidum: de noche, la línea de las hogueras del campamento romano.
        rect(Rect(r.left, llano, r.right, muralla), if (o) mez(CIELO_NOCHE, ROBLE_HOJA_SOMBRA, 0.3f) else mez(MONTE, PAJA, 0.35f))
        if (o) {
            for (k in 0 until 7) {
                val c = Offset(r.left + r.width * (0.06f + 0.14f * k + 0.04f * azar(semilla, k + 70)), llano + r.height * (0.025f + 0.012f * azar(semilla, k + 80)))
                halo(c, 1.2f * u, HOGUERA_ROMANA, 0.9f, 6)
                drawCircle(HOGUERA_ROMANA, 0.2f * u, c)
            }
            drawLine(HOGUERA_ROMANA.copy(alpha = 0.2f), Offset(r.left, llano + r.height * 0.045f), Offset(r.right, llano + r.height * 0.04f), 0.2f * u)
            drawLine(HOGUERA_ROMANA.copy(alpha = 0.12f), Offset(r.left, llano + r.height * 0.06f), Offset(r.right, llano + r.height * 0.056f), 0.2f * u)
        } else {
            // Los campos del llano, en franjas, y un bosquecillo.
            for (k in 0 until 6) drawLine(mez(MONTE, if (k % 2 == 0) PAJA else ROBLE_HOJA, 0.5f).copy(alpha = 0.5f), Offset(r.left, llano + r.height * 0.018f * (k + 1)), Offset(r.right, llano + r.height * 0.018f * (k + 1) - u), 0.35f * u)
            listOf(0.2f, 0.27f, 0.72f).forEach { fx -> drawCircle(mez(MONTE, ROBLE_HOJA, 0.55f), 1.1f * u, Offset(r.left + r.width * fx, llano + r.height * 0.05f)) }
        }
        // La empalizada del oppidum sobre su terraplén: los troncos de punta.
        val palo = 1.5f * u
        var xp = r.left - palo * 0.5f
        var kp = 0
        while (xp < r.right) {
            val alto = (4.2f + 1f * azar(semilla, kp + 100)) * u
            val tronco = Path().apply {
                moveTo(xp, muralla); lineTo(xp, muralla - alto); lineTo(xp + palo / 2, muralla - alto - palo * 0.8f)
                lineTo(xp + palo, muralla - alto); lineTo(xp + palo, muralla); close()
            }
            drawPath(tronco, if (o) mez(CIELO_NOCHE, MADERA, 0.25f) else MADERA)
            drawLine(if (o) LUNA.copy(alpha = 0.18f) else luzMueble(0.4f), Offset(xp + 0.2f * u, muralla - alto), Offset(xp + 0.2f * u, muralla), 0.25f * u)
            xp += palo; kp++
        }
        drawLine(if (o) mez(CIELO_NOCHE, MADERA, 0.4f) else TABLA_OSC, Offset(r.left, muralla - 3f * u), Offset(r.right, muralla - 3f * u), 0.4f * u)
        // Dentro, el suelo del oppidum y dos casas de paja con su humo (y de noche, su puerta encendida).
        rect(Rect(r.left, muralla, r.right, r.bottom), if (o) mez(CIELO_NOCHE, TIERRA, 0.18f) else mez(TIERRA, PAJA, 0.3f))
        listOf(Triple(0.55f, 0.66f, 0.45f), Triple(0.18f, 0.72f, 0.62f), Triple(0.84f, 0.77f, 0.75f)).forEachIndexed { k, (fx, fy, esc) ->
            val c = Offset(r.left + r.width * fx, r.top + r.height * fy)
            val an = r.width * 0.62f * esc
            casa(c, an, o, u, semilla + k)
        }
        // El camino de tierra hasta la puerta.
        drawPath(Path().apply {
            moveTo(r.left + r.width * 0.42f, r.top + r.height * 0.8f); lineTo(r.left + r.width * 0.58f, r.top + r.height * 0.8f)
            lineTo(r.right, r.bottom); lineTo(r.left + r.width * 0.05f, r.bottom); close()
        }, if (o) mez(CIELO_NOCHE, TIERRA, 0.28f) else mez(TIERRA, BARRO_LUZ, 0.45f))
        if (o) drawRect(LUNA.copy(alpha = 0.04f), r.topLeft, r.size)
    }

    /** Una casa de paja del oppidum, lejos: la pared de barro, el techo cónico de paja, la puerta y el humo que sale. */
    private fun DrawScope.casa(base: Offset, ancho: Float, o: Boolean, u: Float, semilla: Int) {
        val alto = ancho * 0.32f
        val pared = Rect(base.x - ancho * 0.4f, base.y - alto, base.x + ancho * 0.4f, base.y)
        rect(pared, if (o) mez(CIELO_NOCHE, BARRO, 0.22f) else BARRO_LUZ)
        drawRect(if (o) Color.Black.copy(alpha = 0f) else sombraMueble(0.2f), Offset(base.x, pared.top), Size(pared.width / 2, pared.height))
        val puerta = Rect(base.x - ancho * 0.06f, base.y - alto * 0.7f, base.x + ancho * 0.06f, base.y)
        rect(puerta, if (o) FUEGO else ZARZO_HONDO)
        if (o) halo(puerta.center, ancho * 0.35f, FUEGO_HALO, 0.7f, 8)
        val techo = Path().apply {
            moveTo(base.x - ancho * 0.52f, pared.top + alto * 0.12f)
            quadraticTo(base.x - ancho * 0.2f, pared.top - alto * 0.7f, base.x, pared.top - alto * 1.25f)
            quadraticTo(base.x + ancho * 0.2f, pared.top - alto * 0.7f, base.x + ancho * 0.52f, pared.top + alto * 0.12f)
            close()
        }
        drawPath(techo, if (o) mez(CIELO_NOCHE, PAJA, 0.2f) else PAJA)
        clipPath(techo) {
            drawRect(if (o) LUNA.copy(alpha = 0.08f) else luzMueble(0.25f), Offset(base.x - ancho, pared.top - alto * 2), Size(ancho * 0.85f, alto * 3))
            drawRect(sombraMueble(if (o) 0.2f else 0.22f), Offset(base.x + ancho * 0.08f, pared.top - alto * 2), Size(ancho, alto * 3))
            for (k in -6..6) drawLine((if (o) NOCHE else mez(PAJA, TABLA_OSC, 0.4f)).copy(alpha = 0.35f), Offset(base.x, pared.top - alto * 1.25f), Offset(base.x + k * ancho * 0.09f, pared.top + alto * 0.2f), 0.2f * u)
        }
        // El humo, que sale por la paja y se lo lleva el viento.
        val humo = (if (o) LUNA else HUMO).copy(alpha = if (o) 0.12f else 0.4f)
        val viento = 0.09f + 0.06f * azar(semilla, 2)
        for (k in 0 until 4) drawOval(humo.copy(alpha = humo.alpha * (1f - k * 0.2f)), Offset(base.x - ancho * 0.05f + k * ancho * viento, pared.top - alto * (1.45f + 0.35f * k)), Size(ancho * (0.18f + 0.06f * k), alto * 0.32f))
    }

    /** Un manto de lana a cuadros colgado de una clavija en [clavo]: cae en pliegues, se abre abajo y acaba en flecos. */
    private fun DrawScope.manto(clavo: Offset, ancho: Float, alto: Float, o: Boolean, u: Float, semilla: Int, f: Float = 0f) {
        val forma = Path().apply {
            moveTo(clavo.x - ancho * 0.18f, clavo.y)
            quadraticTo(clavo.x - ancho * 0.5f, clavo.y + alto * 0.4f, clavo.x - ancho * 0.56f, clavo.y + alto)
            lineTo(clavo.x + ancho * 0.6f, clavo.y + alto * 0.97f)
            quadraticTo(clavo.x + ancho * 0.48f, clavo.y + alto * 0.4f, clavo.x + ancho * 0.18f, clavo.y)
            close()
        }
        translate(1.2f * u, 1.4f * u) { drawPath(forma, sombraMueble(if (o) 0.4f else 0.28f)) }
        clipPath(forma) {
            cuadros(Rect(clavo.x - ancho, clavo.y, clavo.x + ancho, clavo.y + alto), o, u, 5.5f * u, f, LANA_VERDE)
            // Los pliegues: franjas de luz y de sombra que bajan desde el clavo.
            for (k in 0 until 5) {
                val t = (k + 0.5f) / 5f
                val xb = clavo.x - ancho * 0.56f + ancho * 1.16f * t
                val pl = Path().apply {
                    moveTo(clavo.x + (t - 0.5f) * ancho * 0.2f, clavo.y)
                    lineTo(xb - ancho * 0.07f, clavo.y + alto); lineTo(xb + ancho * 0.07f, clavo.y + alto); close()
                }
                drawPath(pl, if (k % 2 == 0) luzMueble(if (o) 0.03f else 0.16f) else sombraMueble(if (o) 0.3f else 0.26f))
            }
            drawRect(sombraMueble(if (o) 0.25f else 0.18f), Offset(clavo.x + ancho * 0.2f, clavo.y), Size(ancho, alto))
        }
        // Los flecos.
        var x = clavo.x - ancho * 0.54f
        var k = 0
        while (x < clavo.x + ancho * 0.58f) {
            val y = clavo.y + alto - (x - clavo.x + ancho * 0.56f) / (ancho * 1.16f) * alto * 0.03f
            drawLine(lam(if (k % 2 == 0) LANA_VERDE else LANA_OCRE, o, f), Offset(x, y), Offset(x + 0.2f * u, y + 1.4f * u + 0.5f * u * azar(semilla, k)), 0.3f * u, StrokeCap.Round)
            x += 0.75f * u; k++
        }
        clavija(clavo, o, u, f)
    }

    /** Una lanza apoyada de [pie] a [punta]: el asta de fresno, el regatón y la punta de hierro en hoja con su nervio. */
    private fun DrawScope.lanza(pie: Offset, punta: Offset, o: Boolean, u: Float, f: Float = 0f) {
        val dx = punta.x - pie.x; val dy = punta.y - pie.y
        val l = sqrt(dx * dx + dy * dy)
        val ux = dx / l; val uy = dy / l
        val hoja = 8f * u
        val cuello = Offset(punta.x - ux * hoja, punta.y - uy * hoja)
        drawLine(sombraMueble(0.3f), Offset(pie.x + 0.8f * u, pie.y + 0.6f * u), Offset(cuello.x + 0.8f * u, cuello.y + 0.6f * u), 1f * u)
        drawLine(lam(FRESNO, o, f), pie, cuello, 0.85f * u, StrokeCap.Round)
        drawLine(luzMueble(if (o) 0.06f else 0.4f), Offset(pie.x - 0.2f * u, pie.y), Offset(cuello.x - 0.2f * u, cuello.y), 0.25f * u)
        drawLine(lam(HIERRO, o, f), pie, Offset(pie.x + ux * 2.4f * u, pie.y + uy * 2.4f * u), 1f * u, StrokeCap.Round)
        puntaLanza(cuello, atan2(dy, dx) * 180f / PI.toFloat(), hoja, o, u, f)
    }

    /** La punta de hierro de una lanza, en forma de hoja larga con su nervio, que sale de [cuello] en la dirección [angulo]. */
    private fun DrawScope.puntaLanza(cuello: Offset, angulo: Float, largo: Float, o: Boolean, u: Float, f: Float = 0f) {
        rotate(angulo, cuello) {
            val x = cuello.x; val y = cuello.y
            val hoja = Path().apply {
                moveTo(x, y)
                cubicTo(x + largo * 0.25f, y - largo * 0.2f, x + largo * 0.6f, y - largo * 0.16f, x + largo, y)
                cubicTo(x + largo * 0.6f, y + largo * 0.16f, x + largo * 0.25f, y + largo * 0.2f, x, y)
                close()
            }
            translate(0.4f * u, 0.5f * u) { drawPath(hoja, sombraMueble(0.3f)) }
            drawRect(lam(HIERRO, o, f), Offset(x - largo * 0.18f, y - 0.6f * u), Size(largo * 0.22f, 1.2f * u))
            drawPath(hoja, lam(HIERRO, o, f))
            clipPath(hoja) { drawRect(lam(HIERRO_LUZ, o, f), Offset(x, y - largo), Size(largo, largo)) }
            drawLine(lam(mez(HIERRO, Color.Black, 0.35f), o, f), Offset(x, y), Offset(x + largo * 0.92f, y), 0.3f * u)
        }
    }

    /**
     * El escudo largo de los galos: tablas forradas de cuero pintado, con la espina de madera a lo largo, el umbo de hierro
     * de dos aletas clavado en medio, el ribete y dos «S» de ocre pintadas; cuelga de su correa.
     */
    private fun DrawScope.escudo(r: Rect, o: Boolean, u: Float, f: Float = 0f) {
        val radio = CornerRadius(r.width * 0.48f, r.width * 0.6f)
        sombraPieza(r, 2.6f * u, if (o) 0.45f else 0.32f, r.width * 0.45f)
        // La correa, de la clavija al canto de arriba.
        val clavo = Offset(r.center.x, r.top - 2.4f * u)
        drawLine(lam(CORREA, o, f), clavo, Offset(r.left + r.width * 0.25f, r.top + 1.6f * u), 0.6f * u)
        drawLine(lam(CORREA, o, f), clavo, Offset(r.right - r.width * 0.25f, r.top + 1.6f * u), 0.6f * u)
        drawRoundRect(lam(mez(HIERRO, CORREA, 0.4f), o, f), r.topLeft, r.size, radio)
        val cara = r.deflate(0.7f * u)
        val formaCara = Path().apply { addRoundRect(RoundRect(cara, CornerRadius(cara.width * 0.48f, cara.width * 0.6f))) }
        drawPath(formaCara, lam(ESCUDO, o, f))
        clipPath(formaCara) {
            pinceladas(cara, lam(mez(ESCUDO, Color.Black, 0.3f), o, f), 7, 6, u, 0.15f)
            // Las dos «S» pintadas, arriba y abajo del umbo.
            with(TrazosGalia) {
                esse(Offset(cara.center.x, cara.top + cara.height * 0.25f), cara.width * 0.55f, 0.9f * u, lam(LANA_OCRE, o, f).copy(alpha = 0.85f), 90f)
                esse(Offset(cara.center.x, cara.bottom - cara.height * 0.25f), cara.width * 0.55f, 0.9f * u, lam(LANA_OCRE, o, f).copy(alpha = 0.85f), 90f)
            }
            // La luz a la izquierda y la sombra a la derecha: el escudo es un poco abombado.
            drawRect(luzMueble(if (o) 0.04f else 0.2f), cara.topLeft, Size(cara.width * 0.3f, cara.height))
            drawRect(sombraMueble(if (o) 0.3f else 0.24f), Offset(cara.left + cara.width * 0.66f, cara.top), Size(cara.width * 0.34f, cara.height))
        }
        // La espina de madera, a lo largo.
        val esp = Rect(r.center.x - 0.9f * u, r.top + 1.2f * u, r.center.x + 0.9f * u, r.bottom - 1.2f * u)
        drawRect(sombraMueble(0.3f), Offset(esp.right, esp.top), Size(0.6f * u, esp.height))
        drawRoundRect(lam(TABLA, o, f), esp.topLeft, esp.size, CornerRadius(0.9f * u))
        drawLine(luzMueble(if (o) 0.06f else 0.4f), Offset(esp.left + 0.4f * u, esp.top + u), Offset(esp.left + 0.4f * u, esp.bottom - u), 0.3f * u)
        // El umbo de aletas: la banda de hierro, las dos aletas clavadas y el abombado en medio.
        val cy = r.center.y
        val aleta = Path().apply {
            moveTo(r.center.x - r.width * 0.36f, cy - 1.4f * u); lineTo(r.center.x - 1.6f * u, cy - 2.6f * u); lineTo(r.center.x + 1.6f * u, cy - 2.6f * u)
            lineTo(r.center.x + r.width * 0.36f, cy - 1.4f * u); lineTo(r.center.x + r.width * 0.36f, cy + 1.4f * u); lineTo(r.center.x + 1.6f * u, cy + 2.6f * u)
            lineTo(r.center.x - 1.6f * u, cy + 2.6f * u); lineTo(r.center.x - r.width * 0.36f, cy + 1.4f * u); close()
        }
        translate(0.5f * u, 0.6f * u) { drawPath(aleta, sombraMueble(0.35f)) }
        drawPath(aleta, lam(HIERRO, o, f))
        drawOval(lam(mez(HIERRO, Color.Black, 0.2f), o, f), Offset(r.center.x - 2.2f * u, cy - 3.6f * u), Size(4.4f * u, 7.2f * u))
        drawOval(lam(HIERRO_LUZ, o, f), Offset(r.center.x - 1.6f * u, cy - 3.0f * u), Size(1.8f * u, 3.6f * u))
        listOf(-1f, 1f).forEach { s ->
            drawCircle(lam(HIERRO_LUZ, o, f), 0.4f * u, Offset(r.center.x + s * r.width * 0.3f, cy - 0.6f * u))
            drawCircle(lam(HIERRO_LUZ, o, f), 0.4f * u, Offset(r.center.x + s * r.width * 0.3f, cy + 0.6f * u))
        }
        clavija(clavo, o, u, f)
    }

    /**
     * Un ánfora de vino traída de Italia, de pie y apoyada en la pared, girada [giro] grados: el labio grueso, el cuello
     * largo con sus asas rectas, el hombro marcado, el cuerpo largo y el pivote macizo, con el tapón sellado con resina.
     */
    private fun DrawScope.anfora(base: Offset, alto: Float, ancho: Float, giro: Float, o: Boolean, u: Float, f: Float = 0f) {
        sombraContacto(Offset(base.x + ancho * 0.2f, base.y), ancho * 0.9f, ancho * 0.2f, if (o) 0.4f else 0.3f)
        rotate(giro, base) {
            val x = base.x; val y = base.y
            val cuerpo = Path().apply {
                moveTo(x - ancho * 0.08f, y)
                lineTo(x - ancho * 0.1f, y - alto * 0.1f)
                cubicTo(x - ancho * 0.35f, y - alto * 0.2f, x - ancho * 0.5f, y - alto * 0.42f, x - ancho * 0.5f, y - alto * 0.6f)
                lineTo(x - ancho * 0.48f, y - alto * 0.66f)
                lineTo(x - ancho * 0.17f, y - alto * 0.7f)
                lineTo(x - ancho * 0.15f, y - alto * 0.92f)
                lineTo(x + ancho * 0.15f, y - alto * 0.92f)
                lineTo(x + ancho * 0.17f, y - alto * 0.7f)
                lineTo(x + ancho * 0.48f, y - alto * 0.66f)
                lineTo(x + ancho * 0.5f, y - alto * 0.6f)
                cubicTo(x + ancho * 0.5f, y - alto * 0.42f, x + ancho * 0.35f, y - alto * 0.2f, x + ancho * 0.1f, y - alto * 0.1f)
                lineTo(x + ancho * 0.08f, y)
                close()
            }
            translate(1.2f * u, 0.6f * u) { drawPath(cuerpo, sombraMueble(if (o) 0.35f else 0.22f)) }
            // Las asas, rectas, del cuello al hombro.
            listOf(-1f, 1f).forEach { s ->
                val asa = Path().apply {
                    moveTo(x + s * ancho * 0.15f, y - alto * 0.86f); lineTo(x + s * ancho * 0.34f, y - alto * 0.85f)
                    lineTo(x + s * ancho * 0.36f, y - alto * 0.67f)
                }
                drawPath(asa, lam(mez(ANFORA, Color.Black, 0.25f), o, f), style = Stroke(ancho * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
            drawPath(cuerpo, lam(ANFORA, o, f))
            clipPath(cuerpo) {
                drawRect(lam(ANFORA_LUZ, o, f).copy(alpha = 0.6f), Offset(x - ancho * 0.6f, y - alto), Size(ancho * 0.36f, alto))
                drawRect(luzMueble(if (o) 0.03f + 0.1f * f else 0.2f), Offset(x - ancho * 0.34f, y - alto), Size(ancho * 0.12f, alto))
                drawRect(sombraMueble(if (o) 0.35f else 0.24f), Offset(x + ancho * 0.12f, y - alto), Size(ancho * 0.6f, alto))
                drawRect(sombraMueble(if (o) 0.3f else 0.2f), Offset(x + ancho * 0.3f, y - alto), Size(ancho * 0.4f, alto))
                // El hombro marcado y las vueltas del torno.
                drawRect(sombraMueble(0.3f), Offset(x - ancho, y - alto * 0.66f), Size(ancho * 2, alto * 0.012f))
                for (k in 0 until 5) drawLine(lam(mez(ANFORA, Color.Black, 0.2f), o, f).copy(alpha = 0.22f), Offset(x - ancho, y - alto * (0.25f + 0.08f * k)), Offset(x + ancho, y - alto * (0.25f + 0.08f * k)), 0.2f * u)
            }
            // El labio grueso y el tapón sellado con resina.
            val labio = Rect(x - ancho * 0.2f, y - alto * 0.97f, x + ancho * 0.2f, y - alto * 0.91f)
            drawRoundRect(lam(mez(ANFORA, Color.White, 0.15f), o, f), labio.topLeft, labio.size, CornerRadius(ancho * 0.04f))
            drawRect(sombraMueble(0.3f), Offset(labio.left + labio.width * 0.6f, labio.top), Size(labio.width * 0.4f, labio.height))
            drawOval(lam(Color(0xFF3A2A1A), o, f), Offset(x - ancho * 0.15f, y - alto * 0.995f), Size(ancho * 0.3f, alto * 0.05f))
            drawLine(lam(mez(ANFORA, Color.White, 0.3f), o, f), Offset(labio.left, labio.top), Offset(labio.right, labio.top), 0.3f * u)
        }
    }

    /** Una olla de la casa, de barro bruñido y negro, con su cuello corto y el labio vuelto, y una franja pulida. */
    private fun DrawScope.olla(base: Offset, ancho: Float, alto: Float, o: Boolean, u: Float, f: Float = 0f) {
        val x = base.x; val y = base.y
        sombraContacto(Offset(x + ancho * 0.12f, y), ancho * 0.95f, ancho * 0.2f, 0.32f)
        val cuerpo = Path().apply {
            moveTo(x - ancho * 0.22f, y)
            cubicTo(x - ancho * 0.55f, y - alto * 0.2f, x - ancho * 0.56f, y - alto * 0.7f, x - ancho * 0.3f, y - alto * 0.86f)
            lineTo(x - ancho * 0.36f, y - alto); lineTo(x + ancho * 0.36f, y - alto); lineTo(x + ancho * 0.3f, y - alto * 0.86f)
            cubicTo(x + ancho * 0.56f, y - alto * 0.7f, x + ancho * 0.55f, y - alto * 0.2f, x + ancho * 0.22f, y)
            close()
        }
        drawPath(cuerpo, lam(OLLA, o, f))
        clipPath(cuerpo) {
            drawRect(luzMueble(if (o) 0.05f + 0.15f * f else 0.18f), Offset(x - ancho * 0.6f, y - alto), Size(ancho * 0.34f, alto))
            drawRect(luzMueble(if (o) 0.06f + 0.18f * f else 0.3f), Offset(x - ancho * 0.36f, y - alto * 0.75f), Size(ancho * 0.07f, alto * 0.4f))
            drawRect(sombraMueble(0.35f), Offset(x + ancho * 0.15f, y - alto), Size(ancho, alto))
            // Las franjas bruñidas en zigzag, del hombro.
            val zz = Path().apply {
                moveTo(x - ancho * 0.6f, y - alto * 0.64f)
                var k = 0
                var xx = x - ancho * 0.6f
                while (xx < x + ancho * 0.6f) { xx += ancho * 0.1f; lineTo(xx, y - alto * (if (k % 2 == 0) 0.74f else 0.64f)); k++ }
            }
            drawPath(zz, lam(Color(0xFF8A7E70), o, f).copy(alpha = 0.6f), style = Stroke(0.3f * u))
        }
        drawLine(lam(mez(OLLA, Color.White, 0.25f), o, f), Offset(x - ancho * 0.36f, y - alto), Offset(x + ancho * 0.36f, y - alto), 0.4f * u)
    }

    /** Una horquilla de roble clavada en el suelo junto al hogar, que sostiene el travesaño a la altura [barra]. */
    private fun DrawScope.horquilla(pie: Offset, barra: Float, o: Boolean, u: Float, semilla: Int) {
        val g = 1.6f * u
        val tallo = Rect(pie.x - g / 2, barra + 1f * u, pie.x + g / 2, pie.y)
        drawRect(sombraMueble(0.3f), Offset(tallo.right, tallo.top), Size(0.7f * u, tallo.height))
        roble(tallo, o, u, semilla, vertical = true, f = 0.8f)
        drawRect(sombraMueble(0.3f), Offset(tallo.left + g * 0.6f, tallo.top), Size(g * 0.4f, tallo.height))
        // Los dos brazos de la horquilla.
        listOf(-1f, 1f).forEach { s ->
            drawLine(lam(TABLA, o, 0.8f), Offset(pie.x, barra + 1.4f * u), Offset(pie.x + s * 1.6f * u, barra - 1.4f * u), 1f * u, StrokeCap.Round)
        }
        sombraContacto(Offset(pie.x + 0.6f * u, pie.y), 3f * u, 0.9f * u, 0.3f)
    }

    /**
     * Un morillo de hierro del hogar, de los que sostenían los leños: el pie en arco, la barra que entra al fuego y el
     * tallo con el remate en cabeza de buey, con sus cuernos en media luna; [mira] ±1 es el lado del fuego.
     */
    private fun DrawScope.morillo(pie: Offset, alto: Float, mira: Float, o: Boolean, u: Float) {
        val hierro = lam(HIERRO, o, 0.95f)
        val hondo = lam(mez(HIERRO, Color.Black, 0.35f), o, 0.9f)
        val luz = lam(HIERRO_LUZ, o, 1f)
        val x = pie.x; val y = pie.y
        val g = 1.15f * u
        sombraContacto(Offset(x + 0.6f * u, y), alto * 0.6f, alto * 0.08f, 0.35f)
        // La barra que entra al fuego, por detrás.
        drawLine(hondo, Offset(x, y - alto * 0.2f), Offset(x - mira * alto * 0.42f, y - alto * 0.14f), g * 0.8f, StrokeCap.Round)
        // El pie en arco.
        drawPath(Path().apply { moveTo(x - alto * 0.24f, y); quadraticTo(x, y - alto * 0.42f, x + alto * 0.24f, y) }, hierro, style = Stroke(g * 0.85f, cap = StrokeCap.Round))
        listOf(-1f, 1f).forEach { s -> drawLine(hierro, Offset(x + s * alto * 0.24f, y), Offset(x + s * alto * 0.3f, y), g * 0.7f, StrokeCap.Round) }
        // El tallo.
        drawLine(hierro, Offset(x, y - alto * 0.2f), Offset(x, y - alto * 0.7f), g)
        drawLine(luz.copy(alpha = 0.7f), Offset(x - g * 0.3f, y - alto * 0.24f), Offset(x - g * 0.3f, y - alto * 0.68f), g * 0.25f)
        // La cabeza de buey: el testuz ancho, el hocico, las orejas y los cuernos en media luna.
        val c = Offset(x, y - alto * 0.8f)
        val cab = alto * 0.12f
        drawPath(Path().apply {
            moveTo(c.x - cab, c.y - cab * 0.8f); lineTo(c.x + cab, c.y - cab * 0.8f)
            quadraticTo(c.x + cab * 0.9f, c.y + cab * 0.5f, c.x + cab * 0.55f, c.y + cab * 1.3f)
            lineTo(c.x - cab * 0.55f, c.y + cab * 1.3f)
            quadraticTo(c.x - cab * 0.9f, c.y + cab * 0.5f, c.x - cab, c.y - cab * 0.8f); close()
        }, hierro)
        drawOval(hondo, Offset(c.x - cab * 0.55f, c.y + cab * 0.9f), Size(cab * 1.1f, cab * 0.6f))
        listOf(-1f, 1f).forEach { s ->
            drawPath(Path().apply {
                moveTo(c.x + s * cab * 0.7f, c.y - cab * 0.9f)
                quadraticTo(c.x + s * cab * 2.4f, c.y - cab * 0.8f, c.x + s * cab * 1.9f, c.y - cab * 2.6f)
                quadraticTo(c.x + s * cab * 1.8f, c.y - cab * 1.3f, c.x + s * cab * 0.6f, c.y - cab * 0.4f); close()
            }, hierro)
            drawOval(hierro, Offset(c.x + s * cab * 1.25f - cab * 0.4f, c.y - cab * 0.5f), Size(cab * 0.8f, cab * 0.45f))
            drawCircle(lam(FUEGO, o, 1f).copy(alpha = 0.75f), cab * 0.16f, Offset(c.x + s * cab * 0.42f, c.y))
        }
        drawLine(luz.copy(alpha = 0.7f), Offset(c.x - cab * 0.85f, c.y - cab * 0.6f), Offset(c.x - cab * 0.5f, c.y + cab * 1.1f), g * 0.25f)
    }

    /** El hogar en el suelo: la placa de barro cocido, ennegrecida, y el corro de piedras alumbradas por arriba. */
    private fun DrawScope.hogar(c: Offset, radio: Float, o: Boolean, u: Float, semilla: Int) {
        drawOval(lam(Color(0xFF4A3A2A), o, 0.9f), Offset(c.x - radio * 1.15f, c.y - radio * 0.3f), Size(radio * 2.3f, radio * 0.6f))
        drawOval(Color(0xFF1E1812).copy(alpha = 0.5f), Offset(c.x - radio * 0.8f, c.y - radio * 0.2f), Size(radio * 1.6f, radio * 0.4f))
        // Las piedras de atrás, más chicas, y las de delante, más grandes.
        for (pasada in 0..1) {
            for (k in 0 until 9) {
                val a = PI * (k / 9.0 * 2.0)
                val atras = sin(a) < 0
                if (atras != (pasada == 0)) continue
                val p = Offset(c.x + radio * 1.08f * cos(a).toFloat(), c.y + radio * 0.3f * sin(a).toFloat())
                val pw = radio * (0.26f + 0.08f * azar(semilla, k)) * (if (atras) 0.8f else 1f); val ph = pw * 0.62f
                val piedra = mancha(Rect(p.x - pw / 2, p.y - ph / 2, p.x + pw / 2, p.y + ph / 2), semilla + k, 9, 0.12f)
                translate(0.4f * u, 0.5f * u) { drawPath(piedra, sombraMueble(0.35f)) }
                drawPath(piedra, lam(PIEDRA_OSC, o, 0.9f))
                clipPath(piedra) {
                    drawOval(lam(PIEDRA, o, 1f), Offset(p.x - pw * 0.45f, p.y - ph * 0.55f), Size(pw * 0.8f, ph * 0.6f))
                    drawOval(mez(FUEGO, FUEGO_HALO, 0.5f).copy(alpha = if (o) 0.35f else 0.2f), Offset(p.x - pw * 0.3f, p.y - ph * 0.55f), Size(pw * 0.6f, ph * 0.35f))
                }
            }
        }
    }

    /**
     * El caldero de bronce, colgado: la boca en [boca], el cuerpo redondo con la costura de remaches, el verdín y el hollín
     * de abajo, el aro de hierro del borde y sus dos argollas.
     */
    private fun DrawScope.caldero(boca: Offset, ancho: Float, o: Boolean, u: Float, f: Float) {
        val x = boca.x; val y = boca.y
        val hondo = ancho * 0.62f
        val cuerpo = Path().apply {
            moveTo(x - ancho * 0.5f, y)
            cubicTo(x - ancho * 0.56f, y + hondo * 0.55f, x - ancho * 0.3f, y + hondo, x, y + hondo)
            cubicTo(x + ancho * 0.3f, y + hondo, x + ancho * 0.56f, y + hondo * 0.55f, x + ancho * 0.5f, y)
            close()
        }
        drawPath(cuerpo, lam(BRONCE, o, f))
        clipPath(cuerpo) {
            drawRect(lam(mez(BRONCE, ORO_BRILLO, 0.35f), o, f), Offset(x - ancho * 0.42f, y), Size(ancho * 0.2f, hondo))
            drawRect(luzMueble(if (o) 0.1f else 0.3f), Offset(x - ancho * 0.34f, y + hondo * 0.1f), Size(ancho * 0.05f, hondo * 0.5f))
            drawRect(sombraMueble(if (o) 0.3f else 0.28f), Offset(x + ancho * 0.12f, y), Size(ancho, hondo))
            // La costura de remaches, a media altura.
            val cy = y + hondo * 0.42f
            drawLine(lam(mez(BRONCE, Color.Black, 0.35f), o, f), Offset(x - ancho, cy), Offset(x + ancho, cy), 0.3f * u)
            for (k in -5..5) drawCircle(lam(mez(BRONCE, ORO_BRILLO, 0.5f), o, f), 0.28f * u, Offset(x + k * ancho * 0.09f, cy + 0.6f * u))
            // El verdín y el hollín del fuego abajo.
            for (k in 0 until 7) drawCircle(n(VERDIN, o).copy(alpha = 0.25f), ancho * (0.012f + 0.014f * azar(k, 2)), Offset(x + ancho * (0.8f * azar(k, 5) - 0.4f), y + hondo * (0.12f + 0.5f * azar(k, 7))))
            drawRect(Color(0xFF1A1410).copy(alpha = 0.55f), Offset(x - ancho, y + hondo * 0.78f), Size(ancho * 2, hondo))
            drawRect(Color(0xFF1A1410).copy(alpha = 0.3f), Offset(x - ancho, y + hondo * 0.66f), Size(ancho * 2, hondo))
        }
        // El aro de hierro del borde, con su luz arriba, y las argollas.
        val aro = Rect(x - ancho * 0.53f, y - 0.9f * u, x + ancho * 0.53f, y + 0.9f * u)
        drawRoundRect(lam(HIERRO, o, f), aro.topLeft, aro.size, CornerRadius(0.6f * u))
        drawLine(lam(HIERRO_LUZ, o, f), Offset(aro.left + 0.5f * u, aro.top + 0.3f * u), Offset(aro.right - 0.5f * u, aro.top + 0.3f * u), 0.3f * u)
        listOf(-1f, 1f).forEach { s ->
            drawCircle(lam(HIERRO, o, f), 0.9f * u, Offset(x + s * ancho * 0.5f, y - 0.6f * u), style = Stroke(0.4f * u))
        }
        // El vapor del guiso, que sube un poco.
        val vapor = (if (o) FUEGO_HALO else NUBE).copy(alpha = if (o) 0.16f else 0.5f)
        listOf(-0.14f, 0.1f).forEachIndexed { k, dx ->
            val p0 = Offset(x + ancho * dx, y - 0.8f * u)
            drawPath(Path().apply {
                moveTo(p0.x, p0.y)
                cubicTo(p0.x - 1.6f * u, p0.y - 1.6f * u, p0.x + 1.6f * u, p0.y - 3f * u, p0.x + (0.4f - k) * u, p0.y - (4.6f + k) * u)
            }, vapor, style = Stroke(0.5f * u, cap = StrokeCap.Round))
        }
    }

    /**
     * Un banco bajo de roble con su piel de lobo echada encima (los galos comían sentados sobre pieles de lobo o de perro,
     * Diodoro 5.28): el asiento, las patas, la piel que cae por delante con su pelo y, si [doblado], un manto a cuadros.
     */
    private fun DrawScope.banco(r: Rect, o: Boolean, u: Float, semilla: Int, f: Float, doblado: Boolean = false) {
        val asiento = Rect(r.left, r.top, r.right, r.top + 2f * u)
        sombraContacto(Offset(r.center.x + 2f * u, r.bottom), r.width * 1.05f, 1.6f * u, if (o) 0.4f else 0.3f)
        // Las patas, gruesas y abiertas.
        listOf(r.left + r.width * 0.12f, r.right - r.width * 0.12f).forEach { x ->
            val pata = Path().apply { moveTo(x - 0.9f * u, asiento.bottom); lineTo(x + 0.9f * u, asiento.bottom); lineTo(x + 1.3f * u, r.bottom); lineTo(x - 1.3f * u, r.bottom); close() }
            drawPath(pata, lam(TABLA_OSC, o, f))
            drawRect(sombraMueble(0.3f), Offset(x + 0.2f * u, asiento.bottom), Size(1.1f * u, r.bottom - asiento.bottom))
        }
        roble(asiento, o, u, semilla, f = f)
        drawLine(luzMueble(if (o) 0.06f + 0.15f * f else 0.45f), asiento.topLeft, Offset(asiento.right, asiento.top), 0.35f * u)
        // La piel de lobo echada encima, gruesa: abombada sobre el asiento y caída por delante en mechones blandos.
        val x0 = r.left + r.width * 0.04f; val x1 = r.right - r.width * 0.06f
        val yS = asiento.bottom
        val cae = r.height * 0.36f
        val lomo = r.top - 2.4f * u
        val piel = Path().apply {
            moveTo(x0, yS)
            quadraticTo(x0 - 0.6f * u, lomo + 1.4f * u, x0 + r.width * 0.12f, lomo + 0.6f * u)
            quadraticTo((x0 + x1) / 2, lomo - 0.8f * u, x1 - r.width * 0.12f, lomo + 0.5f * u)
            quadraticTo(x1 + 0.6f * u, lomo + 1.2f * u, x1, yS)
            // El borde de abajo, en lóbulos blandos de pelo.
            val lobulos = 7
            for (k in 0 until lobulos) {
                val xa = x1 - (x1 - x0) * k / lobulos
                val xb = x1 - (x1 - x0) * (k + 1) / lobulos
                val hondo = cae * (0.55f + 0.45f * azar(semilla, k)) + (if (k == 1 || k == 5) cae * 0.35f else 0f)
                quadraticTo((xa + xb) / 2, yS + hondo * 1.4f, xb, yS + cae * 0.25f)
            }
            close()
        }
        translate(0.8f * u, 0.9f * u) { drawPath(piel, sombraMueble(0.32f)) }
        drawPath(piel, lam(LOBO, o, f))
        clipPath(piel) {
            // El lomo, más oscuro arriba, y la luz que le da por la izquierda.
            drawOval(lam(LOBO_OSC, o, f).copy(alpha = 0.35f), Offset(x0 + r.width * 0.1f, lomo - 0.6f * u), Size((x1 - x0) * 0.8f, 2.2f * u))
            drawOval(luzMueble(if (o) 0.03f + 0.08f * f else 0.2f), Offset(x0, lomo), Size((x1 - x0) * 0.5f, 3.4f * u))
            // El pelo: trazos cortos y suaves que siguen la caída.
            for (k in 0 until 160) {
                val c = Offset(x0 + (x1 - x0) * azar(semilla, k + 100), lomo + (yS - lomo + cae * 1.2f) * azar(semilla, k + 200))
                val l = (0.6f + 0.7f * azar(semilla, k + 300)) * u
                val col = when (k % 3) { 0 -> LOBO_OSC; 1 -> mez(LOBO, LOBO_LUZ, 0.6f); else -> mez(LOBO, LOBO_OSC, 0.3f) }
                drawLine(lam(col, o, f).copy(alpha = 0.32f), c, Offset(c.x + 0.2f * u, c.y + l), 0.22f * u, StrokeCap.Round)
            }
            drawRect(sombraMueble(if (o) 0.2f else 0.14f), Offset(x0, yS + 0.2f * u), Size(x1 - x0, 0.9f * u))
            drawRect(sombraMueble(if (o) 0.2f else 0.12f), Offset(x1 - r.width * 0.2f, lomo - u), Size(r.width * 0.3f, r.height))
        }
        if (doblado) {
            // El manto doblado encima: un bulto blando de lana a cuadros, con su doblez y los flecos que cuelgan.
            val m = Rect(r.left + r.width * 0.5f, lomoManto(r, u) - 3f * u, r.left + r.width * 0.84f, lomoManto(r, u) + 0.4f * u)
            sombraContacto(Offset(m.center.x + 0.8f * u, m.bottom), m.width * 1.1f, 1.2f * u, 0.35f)
            val forma = Path().apply {
                moveTo(m.left + 1f * u, m.bottom)
                quadraticTo(m.left - 0.6f * u, m.center.y, m.left + 1.4f * u, m.top + 0.4f * u)
                quadraticTo(m.center.x, m.top - 0.8f * u, m.right - 1.2f * u, m.top + 0.3f * u)
                quadraticTo(m.right + 0.6f * u, m.center.y, m.right - 0.6f * u, m.bottom)
                close()
            }
            clipPath(forma) {
                cuadros(m.inflate(u), o, u, 4f * u, f)
                drawOval(luzMueble(if (o) 0.04f else 0.24f), Offset(m.left, m.top - 0.4f * u), Size(m.width * 0.7f, m.height * 0.5f))
                drawPath(Path().apply { moveTo(m.left, m.center.y + 0.3f * u); quadraticTo(m.center.x, m.center.y - 0.4f * u, m.right, m.center.y + 0.2f * u) }, sombraMueble(0.4f), style = Stroke(0.35f * u))
                drawRect(sombraMueble(0.28f), Offset(m.left, m.center.y + 0.4f * u), Size(m.width, m.height))
                drawRect(sombraMueble(0.2f), Offset(m.right - m.width * 0.22f, m.top - u), Size(m.width * 0.3f, m.height + 2f * u))
            }
            var xf = m.left + 1.2f * u
            while (xf < m.left + m.width * 0.45f) {
                drawLine(lam(LANA_ROJA, o, f), Offset(xf, m.bottom - 0.2f * u), Offset(xf + 0.1f * u, m.bottom + 1.3f * u), 0.28f * u, StrokeCap.Round)
                xf += 0.7f * u
            }
        }
    }

    /** Lo alto de la piel de lobo sobre un banco [r]: donde se apoya lo que se pone encima. */
    private fun lomoManto(r: Rect, u: Float): Float = r.top - 2.2f * u

    // ---------------------------------------------------------------- Mapas

    /** Los puntos de una curva de Bézier cúbica de [a] a [d], en [pasos] tramos. */
    private fun bezier(a: Offset, b: Offset, c: Offset, d: Offset, pasos: Int): List<Offset> = List(pasos + 1) { i ->
        val t = i / pasos.toFloat(); val q = 1f - t
        Offset(q * q * q * a.x + 3 * q * q * t * b.x + 3 * q * t * t * c.x + t * t * t * d.x, q * q * q * a.y + 3 * q * q * t * b.y + 3 * q * t * t * c.y + t * t * t * d.y)
    }

    /** Una cinta de ancho variable a lo largo de [pts]: el ancho en cada punto lo da [ancho] (de 0 a 1 a lo largo). */
    private fun cinta(pts: List<Offset>, ancho: (Float) -> Float): Path {
        val nn = pts.size
        val izq = ArrayList<Offset>(nn); val der = ArrayList<Offset>(nn)
        for (i in 0 until nn) {
            val a = pts[maxOf(i - 1, 0)]; val b = pts[minOf(i + 1, nn - 1)]
            val dx = b.x - a.x; val dy = b.y - a.y; val l = sqrt(dx * dx + dy * dy).coerceAtLeast(0.0001f)
            val g = ancho(i / (nn - 1).coerceAtLeast(1).toFloat()) / 2
            izq.add(Offset(pts[i].x - dy / l * g, pts[i].y + dx / l * g)); der.add(Offset(pts[i].x + dy / l * g, pts[i].y - dx / l * g))
        }
        return Path().apply {
            moveTo(izq[0].x, izq[0].y); for (k in 1 until nn) lineTo(izq[k].x, izq[k].y)
            for (k in nn - 1 downTo 0) lineTo(der[k].x, der[k].y)
            close()
        }
    }

    /**
     * El contorno de un cuero de buey tendido alrededor de [r] (con [m] de margen): los lados un poco hundidos, las cuatro
     * patas en las esquinas, el cuello a la izquierda y el rabo a la derecha.
     */
    private fun cueroBuey(r: Rect, m: Float, u: Float): Path {
        val l = r.left - m; val d = r.right + m; val t = r.top - m; val b = r.bottom + m
        val cx = r.center.x; val cy = r.center.y; val an = d - l
        val pts = listOf(
            Offset(l - 6f * u, t - 5f * u), Offset(l + 7f * u, t + 1.4f * u), Offset(cx - an * 0.25f, t - 0.3f * u), Offset(cx, t + 1.2f * u),
            Offset(cx + an * 0.25f, t - 0.3f * u), Offset(d - 7f * u, t + 1.4f * u), Offset(d + 6f * u, t - 5f * u),
            Offset(d + 1.2f * u, t + 7f * u), Offset(d + 0.4f * u, cy - 3f * u), Offset(d + 5f * u, cy - 0.5f * u), Offset(d + 5.4f * u, cy + 0.7f * u), Offset(d + 0.4f * u, cy + 3f * u),
            Offset(d + 1.2f * u, b - 7f * u), Offset(d + 6f * u, b + 5f * u), Offset(d - 7f * u, b - 1.4f * u), Offset(cx + an * 0.25f, b + 0.3f * u),
            Offset(cx, b - 1.2f * u), Offset(cx - an * 0.25f, b + 0.3f * u), Offset(l + 7f * u, b - 1.4f * u), Offset(l - 6f * u, b + 5f * u),
            Offset(l - 1.2f * u, b - 7f * u), Offset(l - 0.8f * u, cy + 6f * u), Offset(l - 5.6f * u, cy + 4f * u), Offset(l - 7.4f * u, cy),
            Offset(l - 5.6f * u, cy - 4f * u), Offset(l - 0.8f * u, cy - 6f * u), Offset(l - 1.2f * u, t + 7f * u))
        return Path().apply {
            val m0 = Offset((pts.last().x + pts[0].x) / 2, (pts.last().y + pts[0].y) / 2)
            moveTo(m0.x, m0.y)
            for (k in pts.indices) {
                val p = pts[k]; val q = pts[(k + 1) % pts.size]
                quadraticTo(p.x, p.y, (p.x + q.x) / 2, (p.y + q.y) / 2)
            }
            close()
        }
    }

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val o = oscuro
        val ni = enLienzo(area(MuebleVida.MAPAS))
        val tabla = Rect(0f, 0f, w, h * 0.8f)
        val canto = h * 0.865f
        val suelo = h * 0.94f

        // El suelo de tierra con su paja, y la sombra de la mesa.
        tierra(Rect(0f, canto, w, h), o, u, e.semilla + 3)
        drawRect(sombraMueble(if (o) 0.5f else 0.36f), Offset(w * 0.03f, canto), Size(w * 0.95f, (suelo - canto) * 0.8f))
        // Los dos caballetes de roble, con sus patas abiertas.
        listOf(w * 0.13f, w * 0.87f).forEachIndexed { i, x ->
            listOf(-1f, 1f).forEach { sg ->
                val pata = Path().apply {
                    moveTo(x + sg * 1.4f * u - 1f * u, canto); lineTo(x + sg * 1.4f * u + 1f * u, canto)
                    lineTo(x + sg * 4.6f * u + 1.2f * u, suelo + 2f * u); lineTo(x + sg * 4.6f * u - 1.2f * u, suelo + 2f * u); close()
                }
                drawPath(pata, n(TABLA_OSC, o))
                clipPath(pata) { drawRect(sombraMueble(0.3f), Offset(x + sg * 1.4f * u, canto), Size(2f * u, suelo)) }
            }
            drawRect(n(TABLA_OSC, o), Offset(x - 4f * u, suelo - 3.2f * u), Size(8f * u, 1.2f * u))
            sombraContacto(Offset(x + 1.5f * u, suelo + 2.2f * u), 12f * u, 1.6f * u, 0.35f)
        }

        // El tablero: tablones de roble a lo largo, con sus juntas y sus clavijas.
        val tablones = 5
        for (k in 0 until tablones) {
            val r = Rect(tabla.left, tabla.top + tabla.height * k / tablones, tabla.right, tabla.top + tabla.height * (k + 1) / tablones)
            roble(r, o, u, e.semilla + 10 + k)
            drawRect(luzMueble(if (o) 0.04f else 0.18f), r.topLeft, Size(r.width, 0.6f * u))
            drawLine(sombraMueble(if (o) 0.55f else 0.42f), Offset(r.left, r.bottom), Offset(r.right, r.bottom), 0.45f * u)
            listOf(w * 0.13f, w * 0.87f).forEach { x ->
                val c = Offset(x + (azar(e.semilla, k) - 0.5f) * 2f * u, r.center.y)
                drawCircle(n(VETA, o), 0.75f * u, c)
                drawCircle(n(TABLA_LUZ, o).copy(alpha = 0.6f), 0.35f * u, Offset(c.x - 0.2f * u, c.y - 0.2f * u))
            }
        }
        // El canto de la mesa, con su luz arriba y su sombra.
        val frente = Rect(0f, tabla.bottom, w, canto)
        roble(frente, o, u, e.semilla + 20, base = TABLA_OSC)
        drawLine(luzMueble(if (o) 0.08f else 0.5f), Offset(0f, frente.top + 0.3f * u), Offset(w, frente.top + 0.3f * u), 0.5f * u)
        drawRect(sombraMueble(0.3f), Offset(0f, frente.bottom - 1f * u), Size(w, 1f * u))
        pinceladas(tabla, n(VETA, o), e.semilla + 4, 12, u, 0.06f)
        if (o) drawRect(NOCHE.copy(alpha = 0.3f), Offset.Zero, size)

        // De noche, el resplandor del hogar entra por la izquierda y abajo, fuera del cuero.
        if (o) fueraDe(ni) { halo(Offset(-w * 0.04f, h * 0.98f), w * 0.5f, mez(FUEGO, FUEGO_HALO, 0.4f), 1f, 44) }

        // El cuero de buey tendido, con su forma de animal, y las piedras que lo sujetan en las patas.
        val cuero = cueroBuey(ni, 3.4f * u, u)
        translate(1.2f * u, 1.6f * u) { drawPath(cuero, sombraMueble(if (o) 0.45f else 0.32f)) }
        drawPath(cuero, n(CUERO, o))
        clipPath(cuero) {
            pinceladas(Rect(ni.left - 10f * u, ni.top - 8f * u, ni.right + 10f * u, ni.bottom + 8f * u), n(CUERO_OSC, o), e.semilla + 6, 16, u, 0.14f)
            // El borde, más oscuro y curvado hacia arriba, con su luz.
            drawPath(cuero, n(CUERO_OSC, o), style = Stroke(3.2f * u))
            translate(0.5f * u, 0.6f * u) { drawPath(cuero, luzMueble(if (o) 0.05f else 0.28f), style = Stroke(1.2f * u)) }
        }
        drawPath(cuero, n(mez(CUERO_OSC, VETA, 0.4f), o), style = Stroke(0.3f * u))
        rect(ni, nicho(e, o, CUERO))
        clipRect(ni.left, ni.top, ni.right, ni.bottom) {
            pinceladas(ni, n(CUERO_OSC, o), e.semilla + 8, 10, u, if (o) 0.03f else 0.035f)
            for (k in 0 until 40) drawCircle(n(CUERO_OSC, o).copy(alpha = if (o) 0.1f else 0.16f), 0.18f * u, Offset(ni.left + ni.width * azar(e.semilla, k + 900), ni.top + ni.height * azar(e.semilla, k + 950)))
        }
        // Las piedras de río en las patas del cuero.
        val l = ni.left - 3.4f * u; val d = ni.right + 3.4f * u; val t = ni.top - 3.4f * u; val b = ni.bottom + 3.4f * u
        listOf(Offset(l - 3.6f * u, t - 2.8f * u), Offset(d + 3.6f * u, t - 2.8f * u), Offset(d + 3.6f * u, b + 2.8f * u), Offset(l - 3.6f * u, b + 2.8f * u)).forEachIndexed { k, c ->
            val rr = (2.2f + 0.6f * azar(e.semilla, k + 40)) * u
            val forma = mancha(Rect(c.x - rr, c.y - rr * 0.8f, c.x + rr, c.y + rr * 0.8f), e.semilla + k, 10, 0.12f)
            translate(0.7f * u, 0.9f * u) { drawPath(forma, sombraMueble(0.4f)) }
            drawPath(forma, lam(PIEDRA, o, if (k == 3) 0.6f else 0f))
            clipPath(forma) {
                drawOval(luzMueble(if (o) 0.06f else 0.35f), Offset(c.x - rr * 0.8f, c.y - rr * 0.7f), Size(rr * 1.1f, rr * 0.8f))
                drawRect(sombraMueble(0.3f), Offset(c.x + rr * 0.2f, c.y - rr), Size(rr, rr * 2))
            }
        }

        // A la izquierda: la bolsa con los estáteros, el cuerno de beber y, delante, la espada en su vaina.
        bolsa(Offset(w * 0.085f, h * 0.16f), 7.2f * u, o, u, 0.15f)
        listOf(Offset(w * 0.15f, h * 0.15f), Offset(w * 0.172f, h * 0.24f), Offset(w * 0.135f, h * 0.27f), Offset(w * 0.19f, h * 0.31f), Offset(w * 0.06f, h * 0.29f)).forEachIndexed { k, c ->
            estatero(c, 1.9f * u, o, u, k * 40f, 0.2f)
        }
        cuerno(Offset(w * 0.035f, h * 0.4f), Offset(w * 0.19f, h * 0.55f), 4.4f * u, o, u, 0.4f)
        copa(Offset(w * 0.07f, h * 0.63f), 3.6f * u, o, u, 0.6f)
        espada(Offset(w * 0.04f, h * 0.775f), Offset(w * 0.33f, h * 0.762f), o, u, 0.6f)

        // A la derecha: el torques, la tablilla de cera abierta con su estilo y el muérdago.
        with(TrazosGalia) {
            val c = Offset(w * 0.885f, h * 0.18f)
            drawCircle(sombraMueble(0.3f), 5.4f * u, Offset(c.x + 1f * u, c.y + 1.4f * u), style = Stroke(1.6f * u))
            torques(c, 5.2f * u, 1.25f * u, lam(ORO, o, 0f), lam(ORO_BRILLO, o, 0f))
        }
        tablilla(Rect(w * 0.81f, h * 0.36f, w * 0.975f, h * 0.66f), o, u, e.semilla + 30)
        // La hoz de oro y el ramo de muérdago que corta (Plinio 16.250).
        hoz(Offset(w * 0.935f, h * 0.73f), 5.6f * u, -20f, o, u)
        with(TrazosGalia) {
            val c = Offset(w * 0.845f, h * 0.73f)
            translate(0.8f * u, 1f * u) { muerdago(c, 11f * u, -70f, sombraMueble(0.3f), sombraMueble(0.3f)) }
            muerdago(c, 11f * u, -70f, n(MUERDAGO, o), n(BAYA, o))
        }

        granoMueble(Rect(0f, 0f, w, h), o)
    }

    /** La hoz de oro de los druidas, tendida: la hoja en media luna con su filo y el mango corto de madera. */
    private fun DrawScope.hoz(c: Offset, r: Float, giro: Float, o: Boolean, u: Float) {
        rotate(giro, c) {
            val hoja = Path().apply {
                moveTo(c.x - r, c.y)
                quadraticTo(c.x - r, c.y - r * 1.1f, c.x + r * 0.2f, c.y - r)
                quadraticTo(c.x + r * 0.9f, c.y - r * 0.85f, c.x + r * 0.95f, c.y - r * 0.2f)
                quadraticTo(c.x + r * 0.55f, c.y - r * 0.68f, c.x + r * 0.1f, c.y - r * 0.68f)
                quadraticTo(c.x - r * 0.65f, c.y - r * 0.65f, c.x - r * 0.6f, c.y)
                close()
            }
            translate(0.7f * u, 0.9f * u) { drawPath(hoja, sombraMueble(0.35f)) }
            drawPath(hoja, n(ORO, o))
            drawPath(Path().apply { moveTo(c.x - r * 0.78f, c.y - r * 0.2f); quadraticTo(c.x - r * 0.75f, c.y - r * 0.85f, c.x + r * 0.15f, c.y - r * 0.86f) }, n(ORO_BRILLO, o), style = Stroke(0.35f * u, cap = StrokeCap.Round))
            drawPath(hoja, n(mez(ORO, Color.Black, 0.3f), o), style = Stroke(0.2f * u))
            val mango = Rect(c.x - r * 0.95f, c.y - 0.2f * u, c.x - r * 0.65f, c.y + r * 0.9f)
            drawRoundRect(sombraMueble(0.35f), Offset(mango.left + 0.6f * u, mango.top + 0.8f * u), mango.size, CornerRadius(0.6f * u))
            drawRoundRect(n(TABLA, o), mango.topLeft, mango.size, CornerRadius(0.6f * u))
            drawLine(n(TABLA_LUZ, o), Offset(mango.left + 0.3f * u, mango.top + 0.5f * u), Offset(mango.left + 0.3f * u, mango.bottom - 0.5f * u), 0.25f * u)
            drawRect(n(ORO, o), Offset(mango.left - 0.2f * u, mango.top), Size(mango.width + 0.4f * u, 0.9f * u))
        }
    }

    /** Un estátero de oro visto desde arriba: el disco con su canto, el caballo de las monedas y el cordón de perlas. */
    private fun DrawScope.estatero(c: Offset, r: Float, o: Boolean, u: Float, giro: Float, f: Float = 0f) {
        drawCircle(sombraMueble(0.35f), r, Offset(c.x + 0.35f * u, c.y + 0.45f * u))
        drawCircle(lam(mez(ORO, Color.Black, 0.2f), o, f), r, c)
        drawCircle(lam(ORO, o, f), r * 0.86f, Offset(c.x - r * 0.05f, c.y - r * 0.05f))
        rotate(giro, c) {
            with(TrazosGalia) { caballo(Offset(c.x, c.y - r * 0.08f), r * 1.35f, lam(mez(ORO, Color.Black, 0.35f), o, f), mira = 1f, recio = 1.6f) }
        }
        drawArc(lam(ORO_BRILLO, o, f), 190f, 80f, false, Offset(c.x - r * 0.75f, c.y - r * 0.75f), Size(r * 1.5f, r * 1.5f), style = Stroke(r * 0.12f))
    }

    /** La bolsa de cuero de las monedas, vista desde arriba: el cuerpo blando, el cuello fruncido y el cordón. */
    private fun DrawScope.bolsa(c: Offset, r: Float, o: Boolean, u: Float, f: Float) {
        val cuerpo = mancha(Rect(c.x - r, c.y - r * 0.8f, c.x + r * 0.7f, c.y + r * 0.85f), 41, 14, 0.12f)
        translate(0.9f * u, 1.2f * u) { drawPath(cuerpo, sombraMueble(0.35f)) }
        drawPath(cuerpo, lam(CUERO_OSC, o, f))
        clipPath(cuerpo) {
            drawOval(lam(CUERO, o, f).copy(alpha = 0.55f), Offset(c.x - r * 0.9f, c.y - r * 0.7f), Size(r * 1.1f, r * 0.9f))
            for (k in 0 until 5) drawLine(sombraMueble(0.25f), Offset(c.x + r * 0.5f, c.y), Offset(c.x - r * (0.2f + 0.2f * k), c.y + r * (-0.7f + 0.35f * k)), 0.3f * u)
            drawRect(sombraMueble(0.25f), Offset(c.x + r * 0.1f, c.y - r), Size(r, r * 2))
        }
        // El cuello fruncido, hacia las monedas, y el cordón suelto.
        val cuello = Offset(c.x + r * 0.85f, c.y - r * 0.05f)
        drawOval(lam(mez(CUERO_OSC, Color.Black, 0.25f), o, f), Offset(cuello.x - r * 0.25f, cuello.y - r * 0.32f), Size(r * 0.5f, r * 0.64f))
        drawLine(lam(CORREA, o, f), Offset(cuello.x - r * 0.1f, cuello.y - r * 0.3f), Offset(cuello.x - r * 0.1f, cuello.y + r * 0.3f), 0.5f * u)
        drawPath(Path().apply { moveTo(cuello.x - r * 0.1f, cuello.y + r * 0.2f); cubicTo(cuello.x + r * 0.2f, cuello.y + r * 0.9f, cuello.x - r * 0.6f, cuello.y + r * 1.2f, cuello.x - r * 0.2f, cuello.y + r * 1.5f) },
            lam(CORREA, o, f), style = Stroke(0.4f * u, cap = StrokeCap.Round))
    }

    /** El cuerno de beber, tendido de [boca] a [punta]: el cuerno que se afila y se curva, el aro de bronce y el remate. */
    private fun DrawScope.cuerno(boca: Offset, punta: Offset, ancho: Float, o: Boolean, u: Float, f: Float) {
        val dx = punta.x - boca.x; val dy = punta.y - boca.y
        val pts = bezier(boca, Offset(boca.x + dx * 0.4f, boca.y - ancho * 0.6f), Offset(boca.x + dx * 0.8f, boca.y + dy * 0.3f), punta, 18)
        val forma = cinta(pts) { t -> ancho * (1f - 0.85f * t) }
        translate(0.9f * u, 1.2f * u) { drawPath(forma, sombraMueble(0.35f)) }
        drawPath(forma, lam(Color(0xFFD8C49C), o, f))
        clipPath(forma) {
            // El cuerno se oscurece hacia la punta, en franjas.
            for (k in 0 until 6) {
                val p = pts[(9 + k * 2).coerceAtMost(pts.size - 1)]
                drawCircle(lam(Color(0xFF5E4630), o, f).copy(alpha = 0.18f + 0.06f * k), ancho * 0.9f, Offset(p.x + ancho * 0.6f, p.y))
            }
            for (k in 2 until pts.size - 2 step 2) drawCircle(lam(Color(0xFF8A6A44), o, f).copy(alpha = 0.25f), ancho * (0.55f - 0.025f * k), pts[k], style = Stroke(0.18f * u))
            drawPath(cinta(pts) { t -> ancho * (1f - 0.85f * t) * 0.3f }, luzMueble(if (o) 0.05f + 0.15f * f else 0.35f))
        }
        // El aro de bronce de la boca y el remate de la punta.
        val a0 = pts[0]; val a1 = pts[1]
        val ang = atan2(a1.y - a0.y, a1.x - a0.x) * 180f / PI.toFloat()
        rotate(ang, a0) {
            drawRoundRect(lam(BRONCE, o, f), Offset(a0.x - 0.4f * u, a0.y - ancho * 0.56f), Size(1.6f * u, ancho * 1.12f), CornerRadius(0.5f * u))
            drawLine(lam(ORO_BRILLO, o, f).copy(alpha = 0.7f), Offset(a0.x, a0.y - ancho * 0.5f), Offset(a0.x, a0.y + ancho * 0.1f), 0.3f * u)
            drawOval(lam(Color(0xFF3A2A1A), o, f), Offset(a0.x - 0.8f * u, a0.y - ancho * 0.45f), Size(0.8f * u, ancho * 0.9f))
        }
        drawCircle(lam(BRONCE, o, f), ancho * 0.22f, punta)
        drawCircle(lam(ORO_BRILLO, o, f).copy(alpha = 0.6f), ancho * 0.08f, Offset(punta.x - ancho * 0.06f, punta.y - ancho * 0.06f))
    }

    /** Una copa de barro negro bruñido, vista desde arriba: el labio, el hondo y un poco de cerveza. */
    private fun DrawScope.copa(c: Offset, r: Float, o: Boolean, u: Float, f: Float) {
        drawCircle(sombraMueble(0.35f), r, Offset(c.x + 0.8f * u, c.y + 1f * u))
        drawCircle(lam(OLLA, o, f), r, c)
        drawCircle(lam(mez(OLLA, Color.White, 0.2f), o, f), r, c, style = Stroke(0.5f * u))
        drawCircle(lam(Color(0xFF8A5A22), o, f), r * 0.72f, c)
        drawArc(lam(ORO_BRILLO, o, f).copy(alpha = 0.5f), 200f, 60f, false, Offset(c.x - r * 0.55f, c.y - r * 0.55f), Size(r * 1.1f, r * 1.1f), style = Stroke(0.3f * u))
        drawArc(luzMueble(if (o) 0.06f + 0.2f * f else 0.4f), 160f, 110f, false, Offset(c.x - r, c.y - r), Size(r * 2, r * 2), style = Stroke(0.4f * u))
    }

    /**
     * La espada larga de los galos en su vaina de hierro, tendida de [puno] a [punta]: la empuñadura de madera con su
     * pomo y su guarda en campana, la vaina con la abrazadera del tahalí, el remate calado de la contera y la cadena de
     * hierro del tahalí, suelta.
     */
    private fun DrawScope.espada(puno: Offset, punta: Offset, o: Boolean, u: Float, f: Float) {
        val dx = punta.x - puno.x; val dy = punta.y - puno.y
        val l = sqrt(dx * dx + dy * dy)
        val ang = atan2(dy, dx) * 180f / PI.toFloat()
        rotate(ang, puno) {
            val x = puno.x; val y = puno.y
            val g = 2.8f * u
            translate(0.8f * u, 1.1f * u) { drawRoundRect(sombraMueble(0.35f), Offset(x, y - g / 2), Size(l, g), CornerRadius(g / 2)) }
            // La empuñadura: el pomo, el puño y la guarda en campana.
            drawCircle(lam(HIERRO, o, f), g * 0.42f, Offset(x + g * 0.3f, y))
            drawRoundRect(lam(Color(0xFF6A4A2C), o, f), Offset(x + g * 0.55f, y - g * 0.3f), Size(l * 0.13f, g * 0.6f), CornerRadius(g * 0.25f))
            for (k in 1..3) drawLine(sombraMueble(0.35f), Offset(x + g * 0.55f + l * 0.13f * k / 4f, y - g * 0.3f), Offset(x + g * 0.55f + l * 0.13f * k / 4f, y + g * 0.3f), 0.25f * u)
            val guarda = x + g * 0.55f + l * 0.13f
            drawPath(Path().apply { moveTo(guarda, y - g * 0.35f); lineTo(guarda + g * 0.7f, y - g * 0.6f); lineTo(guarda + g * 0.7f, y + g * 0.6f); lineTo(guarda, y + g * 0.35f); close() }, lam(HIERRO, o, f))
            // La vaina: dos chapas de hierro con su nervio, la abrazadera y la contera.
            val v0 = guarda + g * 0.7f
            val vaina = Path().apply {
                moveTo(v0, y - g * 0.55f); lineTo(x + l - g * 1.6f, y - g * 0.45f)
                quadraticTo(x + l, y - g * 0.25f, x + l, y); quadraticTo(x + l, y + g * 0.25f, x + l - g * 1.6f, y + g * 0.45f)
                lineTo(v0, y + g * 0.55f); close()
            }
            drawPath(vaina, lam(HIERRO, o, f))
            clipPath(vaina) {
                drawRect(lam(HIERRO_LUZ, o, f).copy(alpha = 0.55f), Offset(v0, y - g), Size(l, g * 0.55f))
                drawRect(sombraMueble(0.3f), Offset(v0, y + g * 0.15f), Size(l, g))
                drawLine(lam(mez(HIERRO, Color.Black, 0.3f), o, f), Offset(v0, y), Offset(x + l, y), 0.25f * u)
                // El óxido aquí y allá.
                for (k in 0 until 6) drawCircle(lam(Color(0xFF7A4A28), o, f).copy(alpha = 0.3f), g * (0.12f + 0.12f * azar(k, 4)), Offset(v0 + (l - g * 3f) * azar(k, 9), y + g * 0.4f * (azar(k, 11) - 0.5f)))
            }
            // La abrazadera del tahalí, cerca de la boca.
            val ab = v0 + l * 0.08f
            drawRoundRect(lam(mez(HIERRO, Color.Black, 0.2f), o, f), Offset(ab, y - g * 0.75f), Size(g * 0.9f, g * 1.5f), CornerRadius(g * 0.3f))
            drawLine(lam(HIERRO_LUZ, o, f), Offset(ab + g * 0.2f, y - g * 0.7f), Offset(ab + g * 0.2f, y + g * 0.6f), 0.25f * u)
            // La contera calada, con sus dos bolas.
            val ct = x + l - g * 2.4f
            drawLine(lam(mez(HIERRO, Color.Black, 0.25f), o, f), Offset(ct, y - g * 0.5f), Offset(x + l - g * 0.2f, y), 0.5f * u)
            drawLine(lam(mez(HIERRO, Color.Black, 0.25f), o, f), Offset(ct, y + g * 0.5f), Offset(x + l - g * 0.2f, y), 0.5f * u)
            drawCircle(lam(HIERRO, o, f), g * 0.32f, Offset(ct + g * 0.2f, y - g * 0.55f))
            drawCircle(lam(HIERRO, o, f), g * 0.32f, Offset(ct + g * 0.2f, y + g * 0.55f))
            // La cadena del tahalí, suelta junto a la boca.
            cadena(Offset(ab + g * 0.45f, y - g * 0.75f), Offset(ab - g * 0.6f, y - g * 3f), 1.3f * u, o, u, f)
            cadena(Offset(ab - g * 0.6f, y - g * 3f), Offset(ab + g * 1.8f, y - g * 4.4f), 1.3f * u, o, u, f)
        }
    }

    /**
     * La tablilla de cera, abierta (recurso: César dice que los galos escribían con letras griegas en sus tabulae, no de
     * qué eran): dos hojas de roble con su cera, unas líneas rayadas en una y la otra lisa, la correa de las bisagras y el
     * estilo con su paleta.
     */
    private fun DrawScope.tablilla(r: Rect, o: Boolean, u: Float, semilla: Int) {
        val mitad = r.width / 2
        translate(1f * u, 1.3f * u) { drawRect(sombraMueble(0.35f), r.topLeft, r.size) }
        for (k in 0..1) {
            val hoja = Rect(r.left + mitad * k, r.top, r.left + mitad * (k + 1), r.bottom)
            roble(hoja, o, u, semilla + k, vertical = true)
            drawRect(luzMueble(if (o) 0.04f else 0.25f), hoja.topLeft, Size(hoja.width, 0.5f * u))
            val cera = hoja.deflate(1.3f * u)
            rect(cera, n(CERA, o))
            drawRect(sombraMueble(0.3f), cera.topLeft, Size(cera.width, 0.6f * u))
            drawRect(sombraMueble(0.2f), cera.topLeft, Size(0.5f * u, cera.height))
            if (k == 0) {
                // Unas líneas rayadas en la cera: letras pequeñas, sin leerse.
                var y = cera.top + 2f * u
                var j = 0
                while (y < cera.bottom - 1.5f * u) {
                    var x = cera.left + 1f * u
                    val fin = cera.right - (1f + 3f * azar(semilla, j)) * u
                    while (x < fin) {
                        val tipo = (azar(semilla, j * 31 + x.toInt()) * 4).toInt()
                        val tinta = n(mez(CERA, VETA, 0.6f), o)
                        when (tipo) {
                            0 -> drawLine(tinta, Offset(x, y - 0.8f * u), Offset(x, y), 0.18f * u)
                            1 -> drawCircle(tinta, 0.4f * u, Offset(x + 0.4f * u, y - 0.4f * u), style = Stroke(0.16f * u))
                            2 -> { drawLine(tinta, Offset(x, y), Offset(x + 0.4f * u, y - 0.8f * u), 0.16f * u); drawLine(tinta, Offset(x + 0.4f * u, y - 0.8f * u), Offset(x + 0.8f * u, y), 0.16f * u) }
                            else -> drawArc(tinta, 60f, 240f, false, Offset(x, y - 0.8f * u), Size(0.8f * u, 0.8f * u), style = Stroke(0.16f * u))
                        }
                        x += 1.15f * u
                    }
                    y += 2f * u; j++
                }
            }
        }
        // Las correas de las bisagras.
        listOf(0.25f, 0.75f).forEach { t ->
            val y = r.top + r.height * t
            drawRect(n(CORREA, o), Offset(r.left + mitad - 1.2f * u, y - 0.6f * u), Size(2.4f * u, 1.2f * u))
        }
        drawLine(sombraMueble(0.5f), Offset(r.left + mitad, r.top), Offset(r.left + mitad, r.bottom), 0.3f * u)
        // El estilo de hierro, cruzado, con su punta y su paleta para borrar.
        val a = Offset(r.left + mitad * 1.15f, r.bottom + 1.4f * u); val b = Offset(r.right + 0.6f * u, r.top + r.height * 0.42f)
        drawLine(sombraMueble(0.35f), Offset(a.x + 0.6f * u, a.y + 0.8f * u), Offset(b.x + 0.6f * u, b.y + 0.8f * u), 0.7f * u, StrokeCap.Round)
        drawLine(n(HIERRO, o), a, b, 0.55f * u, StrokeCap.Round)
        drawLine(n(HIERRO_LUZ, o), Offset(a.x - 0.15f * u, a.y - 0.15f * u), Offset(b.x - 0.15f * u, b.y - 0.15f * u), 0.18f * u)
        rotate(atan2(b.y - a.y, b.x - a.x) * 180f / PI.toFloat(), b) {
            drawPath(Path().apply { moveTo(b.x - 0.4f * u, b.y - 0.3f * u); lineTo(b.x + 1.4f * u, b.y - 0.9f * u); lineTo(b.x + 1.4f * u, b.y + 0.9f * u); lineTo(b.x - 0.4f * u, b.y + 0.3f * u); close() }, n(HIERRO, o))
        }
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val o = oscuro
        val ni = enLienzo(area(MuebleVida.OTRAS_VIDAS))
        val techoB = h * 0.12f; val vigaB = h * 0.19f
        val dintel = Rect(0f, h * 0.205f, w, ni.top)
        val suelo = h * 0.9f

        // La pared de barro detrás, el techo de paja y la viga tirante.
        muroBarro(Rect(0f, techoB, w, suelo), o, u, e.semilla + 6)
        techo(Rect(0f, 0f, w, techoB + u), o, u, e.semilla + 1, 0.62f)
        tierra(Rect(0f, suelo, w, h), o, u, e.semilla + 9)
        drawRect(sombraMueble(if (o) 0.4f else 0.28f), Offset(0f, suelo), Size(w, 1f * u))
        if (o) drawRect(NOCHE.copy(alpha = 0.3f), Offset.Zero, size)
        // De noche, el resplandor del hogar, que está a la izquierda, fuera del cuadro.
        if (o) fueraDe(ni) { halo(Offset(-w * 0.05f, h * 0.9f), w * 0.4f, FUEGO_HALO, 1.2f) }

        // Los seis postes: los de las puntas, enteros; los de en medio, entre los tramos.
        val tramo = ni.width / 5f
        val g = 2.6f * u
        poste(Rect(ni.left - g - 0.2f * u, vigaB - u, ni.left - 0.2f * u, suelo + 0.6f * u), o, u, e.semilla + 30, 0.2f)
        poste(Rect(ni.right + 0.2f * u, vigaB - u, ni.right + g + 0.2f * u, suelo + 0.6f * u), o, u, e.semilla + 31)
        viga(Rect(0f, techoB, w, vigaB), o, u, e.semilla + 2)

        // El dintel tallado: la «S» de las monedas, una tras otra, en bajorrelieve.
        roble(dintel, o, u, e.semilla + 3)
        drawRect(luzMueble(if (o) 0.05f else 0.3f), dintel.topLeft, Size(dintel.width, 0.5f * u))
        drawRect(sombraMueble(if (o) 0.34f else 0.26f), Offset(dintel.left, dintel.bottom - 0.8f * u), Size(dintel.width, 0.8f * u))
        val banda = dintel.deflate(1f * u)
        drawRect(sombraMueble(if (o) 0.2f else 0.12f), banda.topLeft, banda.size)
        var xs = banda.left + 3.6f * u
        var ks = 0
        while (xs < banda.right - 2f * u) {
            with(TrazosGalia) {
                esse(Offset(xs + 0.35f * u, banda.center.y + 0.4f * u), 6f * u, 1.1f * u, luzMueble(if (o) 0.06f else 0.35f), if (ks % 2 == 0) 0f else 180f)
                esse(Offset(xs, banda.center.y), 6f * u, 1.1f * u, n(TABLA_OSC, o), if (ks % 2 == 0) 0f else 180f)
            }
            xs += 7.4f * u; ks++
        }

        // Los cinco tramos de barro alisado (el nicho), con los postes de en medio.
        rect(ni, nicho(e, o, ENLUCIDO))
        clipRect(ni.left, ni.top, ni.right, ni.bottom) {
            pinceladas(ni, n(BARRO_LUZ, o), e.semilla + 14, 18, u, if (o) 0.03f else 0.08f)
            for (k in 0 until 5) {
                val t = Rect(ni.left + tramo * k, ni.top, ni.left + tramo * (k + 1), ni.bottom)
                // Las pasadas de la mano en el barro, y la sombra del dintel arriba.
                for (j in 0 until 3) {
                    val rr = (6f + 4f * azar(e.semilla + k, j)) * u
                    val c = Offset(t.left + t.width * azar(e.semilla + k, j + 5), t.top + t.height * azar(e.semilla + k, j + 9))
                    drawArc(n(BARRO_SOMBRA, o).copy(alpha = if (o) 0.05f else 0.08f), 200f, 80f, false, Offset(c.x - rr, c.y - rr), Size(rr * 2, rr * 2), style = Stroke(0.4f * u))
                }
                drawRect(sombraMueble(if (o) 0.14f else 0.08f), t.topLeft, Size(t.width, 2.2f * u))
            }
        }
        // Abajo, donde el barro se cayó junto al suelo, asoma el zarzo.
        val zocalo = Rect(ni.left, ni.bottom, ni.right, suelo)
        zarzo(zocalo, o, u, e.semilla + 70)
        drawRect(sombraMueble(if (o) 0.4f else 0.3f), zocalo.topLeft, Size(zocalo.width, 0.8f * u))
        drawLine(n(BARRO_SOMBRA, o), Offset(ni.left, ni.bottom), Offset(ni.right, ni.bottom), 0.5f * u)
        for (k in 1 until 5) {
            val x = ni.left + tramo * k
            val f = (0.3f - 0.07f * k).coerceAtLeast(0f)
            poste(Rect(x - g / 2, ni.top - 0.4f * u, x + g / 2, suelo + 0.6f * u), o, u, e.semilla + 40 + k, f)
        }
        // Las zapatas de roble bajo el dintel, sobre cada poste.
        for (k in 0..5) {
            val x = ni.left + tramo * k + (if (k == 0) -g / 2 - 0.2f * u else if (k == 5) g / 2 + 0.2f * u else 0f)
            val z = Path().apply {
                moveTo(x - 4f * u, ni.top); lineTo(x + 4f * u, ni.top)
                quadraticTo(x + 2f * u, ni.top + 0.6f * u, x + g / 2, ni.top + 2.6f * u); lineTo(x - g / 2, ni.top + 2.6f * u)
                quadraticTo(x - 2f * u, ni.top + 0.6f * u, x - 4f * u, ni.top); close()
            }
            translate(0.6f * u, 0.7f * u) { drawPath(z, sombraMueble(0.3f)) }
            drawPath(z, n(TABLA, o))
            clipPath(z) { drawRect(sombraMueble(0.3f), Offset(x, ni.top), Size(5f * u, 3f * u)); drawRect(luzMueble(if (o) 0.05f else 0.3f), Offset(x - 4f * u, ni.top), Size(8f * u, 0.5f * u)) }
        }

        // Colgado de los cabrios, el carnyx tendido en dos correas, con la cabeza de jabalí asomando sobre la viga; en la
        // viga, el jamón, el ramo de muérdago y los manojos de hierbas.
        val eje = h * 0.075f
        val largo = w * 0.235f
        val pie = Offset(w * 0.3f, eje)
        listOf(pie.x + largo * 0.2f, pie.x + largo * 0.6f).forEach { x ->
            drawLine(n(CORREA, o), Offset(x, 0f), Offset(x, eje + 1.6f * u), 0.5f * u)
            drawLine(luzMueble(if (o) 0.03f else 0.2f), Offset(x - 0.15f * u, 0f), Offset(x - 0.15f * u, eje + 1.4f * u), 0.15f * u)
        }
        with(TrazosGalia) {
            rotate(90f, pie) {
                translate(1.2f * u, -1.2f * u) { carnyx(pie, largo, sombraMueble(0.35f), null, 1f) }
                carnyx(pie, largo, n(BRONCE, o), n(mez(BRONCE, Color.Black, 0.6f), o), 1f)
            }
        }
        listOf(pie.x + largo * 0.2f, pie.x + largo * 0.6f).forEach { x ->
            drawRoundRect(n(CORREA, o), Offset(x - 0.7f * u, eje - 0.6f * u), Size(1.4f * u, 3.4f * u), CornerRadius(0.4f * u))
        }
        jamon(Offset(w * 0.17f, vigaB - 0.4f * u), h * 0.13f, o, u)
        manojo(Offset(w * 0.73f, vigaB - 0.4f * u), h * 0.11f, o, u, e.semilla + 1)
        manojo(Offset(w * 0.8f, vigaB - 0.4f * u), h * 0.09f, o, u, e.semilla + 2)
        clavija(Offset(w * 0.9f, vigaB - 1.4f * u), o, u)
        drawLine(n(CORREA, o), Offset(w * 0.9f, vigaB - 1.4f * u), Offset(w * 0.9f, vigaB + 1.6f * u), 0.3f * u)
        with(TrazosGalia) { muerdago(Offset(w * 0.9f, vigaB + 2.4f * u), 9f * u, 180f, n(MUERDAGO, o), n(BAYA, o)) }

        // El banco corrido con su piel y su manta, el balde de duelas a la izquierda y el ánfora a la derecha.
        val asiento = Rect(w * 0.08f, h * 0.875f, w * 0.88f, h * 0.875f + 2f * u)
        listOf(0.12f, 0.36f, 0.6f, 0.84f).forEach { t ->
            val x = w * t
            drawRect(n(TABLA_OSC, o), Offset(x - 1f * u, asiento.bottom), Size(2f * u, h - asiento.bottom - 0.5f * u))
            drawRect(sombraMueble(0.3f), Offset(x, asiento.bottom), Size(1f * u, h - asiento.bottom - 0.5f * u))
        }
        sombraContacto(Offset(asiento.center.x + 2f * u, h - 0.4f * u), asiento.width, 1.6f * u, 0.3f)
        roble(asiento, o, u, e.semilla + 50, f = 0.1f)
        drawLine(luzMueble(if (o) 0.06f else 0.45f), asiento.topLeft, Offset(asiento.right, asiento.top), 0.35f * u)
        drawRect(sombraMueble(0.3f), Offset(asiento.left, asiento.bottom - 0.5f * u), Size(asiento.width, 0.5f * u))
        // Encima, la piel de lobo larga y la manta a cuadros doblada.
        val piel = mancha(Rect(w * 0.3f, asiento.top - 4f * u, w * 0.62f, asiento.top + 2.4f * u), e.semilla + 60, 26, 0.12f)
        translate(0.6f * u, 0.8f * u) { drawPath(piel, sombraMueble(0.3f)) }
        drawPath(piel, n(LOBO, o))
        clipPath(piel) {
            drawOval(n(LOBO_OSC, o).copy(alpha = 0.35f), Offset(w * 0.33f, asiento.top - 3.8f * u), Size(w * 0.26f, 2f * u))
            for (k in 0 until 140) {
                val c = Offset(w * 0.3f + w * 0.32f * azar(e.semilla, k + 100), asiento.top - 4f * u + 6.4f * u * azar(e.semilla, k + 200))
                drawLine(n(if (k % 3 == 0) LOBO_OSC else mez(LOBO, LOBO_LUZ, 0.6f), o).copy(alpha = 0.35f), c, Offset(c.x + 0.3f * u, c.y + 0.8f * u), 0.2f * u, StrokeCap.Round)
            }
            drawOval(luzMueble(if (o) 0.04f else 0.2f), Offset(w * 0.31f, asiento.top - 3.8f * u), Size(w * 0.14f, 2.4f * u))
            drawRect(sombraMueble(if (o) 0.25f else 0.18f), Offset(w * 0.3f, asiento.top + 0.6f * u), Size(w * 0.32f, 2f * u))
        }
        val manta = Rect(w * 0.66f, asiento.top - 2.4f * u, w * 0.8f, asiento.top + 0.2f * u)
        sombraContacto(Offset(manta.center.x + 0.8f * u, manta.bottom), manta.width * 1.05f, 1f * u, 0.35f)
        clipPath(Path().apply { addRoundRect(RoundRect(manta, CornerRadius(manta.height * 0.45f))) }) {
            cuadros(manta, o, u, 4f * u, 0f)
            drawRect(luzMueble(if (o) 0.04f else 0.22f), manta.topLeft, Size(manta.width, manta.height * 0.3f))
            drawLine(sombraMueble(0.35f), Offset(manta.left, manta.center.y), Offset(manta.right, manta.center.y), 0.25f * u)
            drawRect(sombraMueble(0.22f), Offset(manta.right - manta.width * 0.22f, manta.top), Size(manta.width * 0.22f, manta.height))
        }
        balde(Offset(w * 0.04f, h * 0.985f), w * 0.055f, h * 0.17f, o, u, 0.5f)
        anfora(Offset(w * 0.965f, h * 0.99f), h * 0.5f, w * 0.042f, -6f, o, u, 0f)

        granoMueble(Rect(0f, 0f, w, h), o)
    }

    /** Un jamón colgado de su cuerda (Estrabón cuenta que la Galia mandaba sus cerdos salados hasta Roma). */
    private fun DrawScope.jamon(clavo: Offset, alto: Float, o: Boolean, u: Float) {
        val y0 = clavo.y + alto * 0.18f
        drawLine(n(CORREA, o), clavo, Offset(clavo.x, y0), 0.3f * u)
        val forma = Path().apply {
            moveTo(clavo.x - alto * 0.06f, y0)
            lineTo(clavo.x + alto * 0.06f, y0)
            cubicTo(clavo.x + alto * 0.12f, y0 + alto * 0.25f, clavo.x + alto * 0.32f, y0 + alto * 0.5f, clavo.x + alto * 0.18f, y0 + alto * 0.78f)
            quadraticTo(clavo.x, y0 + alto * 0.88f, clavo.x - alto * 0.18f, y0 + alto * 0.76f)
            cubicTo(clavo.x - alto * 0.3f, y0 + alto * 0.5f, clavo.x - alto * 0.1f, y0 + alto * 0.25f, clavo.x - alto * 0.06f, y0)
            close()
        }
        translate(0.8f * u, 0.6f * u) { drawPath(forma, sombraMueble(0.3f)) }
        drawPath(forma, n(Color(0xFF7A4A2E), o))
        clipPath(forma) {
            drawOval(n(Color(0xFFA8683E), o), Offset(clavo.x - alto * 0.24f, y0 + alto * 0.3f), Size(alto * 0.25f, alto * 0.4f))
            drawRect(sombraMueble(0.3f), Offset(clavo.x + alto * 0.05f, y0), Size(alto, alto))
            drawOval(n(Color(0xFFE8D8B8), o), Offset(clavo.x - alto * 0.1f, y0 + alto * 0.66f), Size(alto * 0.22f, alto * 0.12f))
        }
        clavija(clavo, o, u)
    }

    /** Un manojo de hierbas que se secan cabeza abajo, atado con su cordel. */
    private fun DrawScope.manojo(clavo: Offset, alto: Float, o: Boolean, u: Float, semilla: Int) {
        val nudo = Offset(clavo.x, clavo.y + alto * 0.22f)
        drawLine(n(CORREA, o), clavo, nudo, 0.3f * u)
        for (k in 0 until 9) {
            val a = (k - 4) * 0.09f + 0.04f * (azar(semilla, k) - 0.5f)
            val l = alto * (0.6f + 0.2f * azar(semilla, k + 10))
            val fin = Offset(nudo.x + l * sin(a), nudo.y + l * cos(a))
            val col = if (k % 2 == 0) mez(ROBLE_HOJA, PAJA, 0.4f) else mez(MUERDAGO, PAJA, 0.3f)
            drawLine(n(col, o), nudo, fin, 0.3f * u)
            drawOval(n(col, o), Offset(fin.x - 0.7f * u, fin.y - 1.4f * u), Size(1.4f * u, 2.4f * u))
        }
        drawRect(n(LANA_ROJA, o), Offset(nudo.x - 1f * u, nudo.y - 0.6f * u), Size(2f * u, 1.2f * u))
        clavija(clavo, o, u)
    }

    /** Un balde de duelas de madera con sus aros de bronce y el asa de hierro, de pie sobre [base]. */
    private fun DrawScope.balde(base: Offset, ancho: Float, alto: Float, o: Boolean, u: Float, f: Float) {
        val x = base.x; val y = base.y
        sombraContacto(Offset(x + ancho * 0.15f, y), ancho * 1.05f, ancho * 0.18f, 0.35f)
        val cuerpo = Path().apply {
            moveTo(x - ancho * 0.5f, y - alto); lineTo(x + ancho * 0.5f, y - alto); lineTo(x + ancho * 0.44f, y); lineTo(x - ancho * 0.44f, y); close()
        }
        drawPath(cuerpo, lam(TABLA, o, f))
        clipPath(cuerpo) {
            for (k in -3..3) drawLine(lam(VETA, o, f).copy(alpha = 0.5f), Offset(x + k * ancho * 0.14f, y - alto), Offset(x + k * ancho * 0.125f, y), 0.25f * u)
            drawRect(luzMueble(if (o) 0.05f + 0.15f * f else 0.25f), Offset(x - ancho, y - alto), Size(ancho * 0.7f, alto))
            drawRect(sombraMueble(0.32f), Offset(x + ancho * 0.15f, y - alto), Size(ancho, alto))
        }
        listOf(0.12f, 0.5f, 0.88f).forEach { t ->
            val yy = y - alto * (1f - t)
            val med = ancho * (0.5f - 0.06f * t)
            drawRect(lam(BRONCE, o, f), Offset(x - med, yy - 0.7f * u), Size(med * 2, 1.4f * u))
            drawLine(lam(ORO_BRILLO, o, f).copy(alpha = 0.6f), Offset(x - med, yy - 0.5f * u), Offset(x, yy - 0.5f * u), 0.3f * u)
            drawRect(sombraMueble(0.3f), Offset(x + med * 0.3f, yy - 0.7f * u), Size(med * 0.7f, 1.4f * u))
        }
        drawArc(lam(HIERRO, o, f), 180f, 180f, false, Offset(x - ancho * 0.48f, y - alto - ancho * 0.6f), Size(ancho * 0.96f, ancho * 1.2f), style = Stroke(0.45f * u))
        drawOval(lam(mez(TABLA, Color.Black, 0.4f), o, f), Offset(x - ancho * 0.48f, y - alto - 0.8f * u), Size(ancho * 0.96f, 1.6f * u))
    }

    // ---------------------------------------------------------------- Íconos de los mapas

    /** El color de noche de los íconos: en penumbra, pero con la silueta clara sobre la tarjeta. */
    private fun ic(c: Color, o: Boolean): Color = if (o) mez(c, NOCHE, 0.32f) else c

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val c = Offset(w / 2, h / 2)
        val s = h * 0.43f
        val p = s * 0.035f
        val o = oscuro
        val u = s / 30f
        when (id) {
            "unesco" -> puertaOppidum(c, s, p, o, u, e)
            "rascar" -> {
                // Un cuero ahumado: el estátero del caballo raspa el hollín y aparecen los ríos de la tierra arverna.
                val zona = Rect(c.x - s * 1.42f, c.y - s * 0.58f, c.x + s * 0.85f, c.y + s * 0.58f)
                val cuero = cueroBuey(zona, s * 0.1f, s * 0.06f)
                translate(p * 2, p * 3) { drawPath(cuero, sombraMueble(0.35f)) }
                drawPath(cuero, ic(Color(0xFF2E2620), o))
                clipPath(cuero) { for (k in 0 until 9) drawCircle(Color(0xFF4A3E34).copy(alpha = 0.35f), s * (0.15f + 0.2f * azar(k, 3)), Offset(zona.left + zona.width * azar(k, 5), zona.top + zona.height * azar(k, 7))) }
                val raspado = Path().apply {
                    moveTo(zona.left + s * 0.2f, zona.top + s * 0.3f)
                    cubicTo(zona.left + s * 0.9f, zona.top - s * 0.05f, zona.left + s * 1.8f, zona.top + s * 0.05f, zona.left + s * 2.45f, zona.top + s * 0.4f)
                    lineTo(zona.left + s * 2.6f, zona.bottom - s * 0.25f)
                    cubicTo(zona.left + s * 1.8f, zona.bottom + s * 0.05f, zona.left + s * 0.8f, zona.bottom - s * 0.05f, zona.left + s * 0.3f, zona.bottom - s * 0.3f)
                    close()
                }
                clipPath(cuero) {
                    clipPath(raspado) {
                        drawRect(ic(CUERO, o), Offset(zona.left - s, zona.top - s), Size(zona.width + 2 * s, zona.height + 2 * s))
                        // El Allier y sus arroyos, y el oppidum en su colina.
                        val rio = suave(listOf(Offset(zona.left + s * 0.5f, zona.bottom + s * 0.2f), Offset(zona.left + s * 0.9f, c.y + s * 0.1f), Offset(zona.left + s * 1.3f, c.y - s * 0.05f), Offset(zona.left + s * 1.7f, c.y - s * 0.3f), Offset(zona.left + s * 2.4f, zona.top - s * 0.1f)))
                        drawPath(rio, ic(RIO, o), style = Stroke(p * 2.2f, cap = StrokeCap.Round))
                        drawPath(suave(listOf(Offset(zona.left + s * 1.3f, c.y - s * 0.05f), Offset(zona.left + s * 1.2f, c.y - s * 0.4f), Offset(zona.left + s * 1.0f, zona.top))), ic(RIO, o), style = Stroke(p * 1.2f, cap = StrokeCap.Round))
                        drawPath(suave(listOf(Offset(zona.left + s * 1.7f, c.y - s * 0.3f), Offset(zona.left + s * 1.9f, c.y + s * 0.2f), Offset(zona.left + s * 2.1f, zona.bottom))), ic(RIO, o), style = Stroke(p * 1.2f, cap = StrokeCap.Round))
                        val col = Offset(zona.left + s * 1.05f, c.y + s * 0.3f)
                        drawArc(ic(CUERO_OSC, o), 180f, 180f, true, Offset(col.x - s * 0.22f, col.y - s * 0.14f), Size(s * 0.44f, s * 0.28f))
                        drawCircle(e.c1, p * 2.2f, Offset(col.x, col.y - s * 0.06f))
                    }
                }
                drawPath(raspado, ic(Color(0xFF2E2620), o).copy(alpha = 0.5f), style = Stroke(p))
                drawPath(cuero, ic(CUERO_OSC, o).copy(alpha = 0.8f), style = Stroke(p * 1.4f))
                // El estátero, inclinado al final del raspado.
                val mc = Offset(c.x + s * 1.05f, c.y + s * 0.18f)
                val r = s * 0.66f
                drawCircle(sombraMueble(0.35f), r, Offset(mc.x + p * 2, mc.y + p * 3))
                drawOval(ic(mez(ORO, Color.Black, 0.35f), o), Offset(mc.x - r, mc.y - r * 0.92f), Size(r * 2, r * 2))
                val cara = Offset(mc.x, mc.y - r * 0.06f)
                drawCircle(ic(ORO, o), r * 0.94f, cara)
                drawArc(ic(ORO_BRILLO, o), 170f, 100f, false, Offset(cara.x - r * 0.8f, cara.y - r * 0.8f), Size(r * 1.6f, r * 1.6f), style = Stroke(p * 1.4f))
                val g = ic(mez(ORO, Color.Black, 0.45f), o)
                for (k in 0 until 22) {
                    val a = 2 * PI * k / 22
                    drawCircle(g, p * 0.6f, Offset(cara.x + r * 0.82f * cos(a).toFloat(), cara.y + r * 0.82f * sin(a).toFloat()))
                }
                with(TrazosGalia) {
                    caballo(Offset(cara.x, cara.y - r * 0.12f), r * 1.22f, g, ic(ORO, o), mira = 1f, recio = 1.4f)
                    rueda(Offset(cara.x - r * 0.02f, cara.y + r * 0.5f), r * 0.14f, g, Color.Transparent)
                }
            }
            else -> {
                // Tus lugares: un cuero claro con el río y tres alfileres de bronce con su gota de esmalte, unidos por el camino.
                val zona = Rect(c.x - s * 1.4f, c.y - s * 0.6f, c.x + s * 1.4f, c.y + s * 0.6f)
                val cuero = cueroBuey(zona, s * 0.1f, s * 0.06f)
                translate(p * 2, p * 3) { drawPath(cuero, sombraMueble(0.35f)) }
                drawPath(cuero, ic(CUERO, o))
                clipPath(cuero) {
                    drawPath(cuero, ic(CUERO_OSC, o), style = Stroke(p * 4f))
                    val rio = suave(listOf(Offset(zona.left - s * 0.2f, c.y + s * 0.4f), Offset(zona.left + s * 0.8f, c.y + s * 0.2f), Offset(c.x, c.y + s * 0.45f), Offset(zona.right - s * 0.8f, c.y + s * 0.1f), Offset(zona.right + s * 0.2f, c.y + s * 0.3f)))
                    drawPath(rio, ic(RIO, o), style = Stroke(p * 2f, cap = StrokeCap.Round))
                    // El robledal: copas pequeñas.
                    listOf(0.25f to -0.35f, 0.35f to -0.42f, 0.78f to -0.3f, 0.86f to -0.4f).forEach { (fx, fy) ->
                        val q = Offset(zona.left + zona.width * fx, c.y + s * fy)
                        drawCircle(ic(ROBLE_HOJA, o), s * 0.11f, q)
                        drawLine(ic(MADERA, o), q, Offset(q.x, q.y + s * 0.16f), p)
                    }
                }
                val pts = listOf(Offset(c.x - s * 1.05f, c.y - s * 0.02f), Offset(c.x + s * 0.05f, c.y - s * 0.2f), Offset(c.x + s * 1.1f, c.y + s * 0.0f))
                drawPath(Path().apply { moveTo(pts[0].x, pts[0].y); quadraticTo(c.x - s * 0.5f, c.y - s * 0.35f, pts[1].x, pts[1].y); quadraticTo(c.x + s * 0.6f, c.y - s * 0.05f, pts[2].x, pts[2].y) },
                    ic(TINTA, o).copy(alpha = if (o) 0.5f else 0.6f), style = Stroke(p * 1.1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(p * 2.5f, p * 2f))))
                listOf(CORAL, e.c1, CORAL).forEachIndexed { k, col -> alfiler(pts[k], s * 0.62f, if (k == 1) col else ic(col, o), o, p) }
            }
        }
    }

    /** Un alfiler de bronce clavado en [punta], con su cabeza de esmalte [esmalte] y su sombra. */
    private fun DrawScope.alfiler(punta: Offset, alto: Float, esmalte: Color, o: Boolean, p: Float) {
        val cabeza = Offset(punta.x + alto * 0.28f, punta.y - alto * 0.62f)
        drawLine(sombraMueble(0.35f), punta, Offset(punta.x + alto * 0.55f, punta.y - alto * 0.1f), p * 1.2f, StrokeCap.Round)
        drawCircle(sombraMueble(0.3f), alto * 0.18f, Offset(punta.x + alto * 0.62f, punta.y - alto * 0.08f))
        drawLine(ic(BRONCE, o), punta, cabeza, p * 1.3f, StrokeCap.Round)
        drawCircle(ic(BRONCE, o), alto * 0.2f, cabeza)
        drawCircle(esmalte, alto * 0.13f, cabeza)
        drawCircle(luzMueble(if (o) 0.25f else 0.6f), alto * 0.05f, Offset(cabeza.x - alto * 0.05f, cabeza.y - alto * 0.05f))
    }

    /**
     * La puerta del oppidum en su muralla gala (el murus gallicus): el paramento de piedra en hiladas con las cabezas de
     * las vigas, la empalizada encima, el paso de la puerta con sus hojas abiertas y la torre de madera sobre ella; a un
     * lado, un roble.
     */
    private fun DrawScope.puertaOppidum(c: Offset, s: Float, p: Float, o: Boolean, u: Float, e: EstadoIcono) {
        val base = c.y + s * 0.95f
        val cima = c.y + s * 0.02f
        val muro = Rect(c.x - s * 1.8f, cima, c.x + s * 1.2f, base)
        // El terraplén al pie.
        drawPath(Path().apply { moveTo(muro.left - s * 0.3f, base); quadraticTo(c.x, base - s * 0.12f, muro.right + s * 0.4f, base); close() }, ic(mez(TIERRA, ROBLE_HOJA, 0.3f), o))
        translate(p * 2, p * 2.5f) { drawRect(sombraMueble(0.3f), muro.topLeft, muro.size) }
        rect(muro, ic(PIEDRA, o))
        // Las hiladas de piedra y las cabezas de las vigas en cuadrícula.
        val hilada = s * 0.15f
        var y = muro.top + hilada
        var k = 0
        while (y < muro.bottom) {
            drawLine(ic(PIEDRA_OSC, o), Offset(muro.left, y), Offset(muro.right, y), p * 0.6f)
            var x = muro.left + hilada * (if (k % 2 == 0) 0.6f else 1.2f)
            while (x < muro.right) { drawLine(ic(PIEDRA_OSC, o), Offset(x, y - hilada), Offset(x, y), p * 0.5f); x += hilada * 1.6f }
            y += hilada; k++
        }
        var fila = 0
        var yv = muro.top + hilada * 1.5f
        while (yv < muro.bottom - hilada * 0.3f) {
            var xv = muro.left + s * (if (fila % 2 == 0) 0.2f else 0.45f)
            while (xv < muro.right - s * 0.1f) {
                drawRect(ic(MADERA, o), Offset(xv - p * 1.6f, yv - p * 1.6f), Size(p * 3.2f, p * 3.2f))
                drawRect(ic(TABLA_LUZ, o), Offset(xv - p * 1.6f, yv - p * 1.6f), Size(p * 3.2f, p * 0.8f))
                xv += s * 0.5f
            }
            yv += hilada * 2f; fila++
        }
        drawRect(luzMueble(if (o) 0.05f else 0.3f), muro.topLeft, Size(muro.width, p * 1.2f))
        // La empalizada encima de la muralla.
        var xp = muro.left
        while (xp < muro.right - p) {
            val pp = Path().apply { moveTo(xp, cima); lineTo(xp, cima - s * 0.22f); lineTo(xp + p * 1.8f, cima - s * 0.3f); lineTo(xp + p * 3.6f, cima - s * 0.22f); lineTo(xp + p * 3.6f, cima); close() }
            drawPath(pp, ic(MADERA, o))
            drawLine(ic(TABLA_LUZ, o).copy(alpha = 0.6f), Offset(xp + p * 0.6f, cima - s * 0.2f), Offset(xp + p * 0.6f, cima), p * 0.5f)
            xp += p * 4f
        }
        // El paso de la puerta, con sus hojas abiertas.
        val paso = Rect(c.x - s * 0.36f, cima + s * 0.12f, c.x + s * 0.36f, base)
        rect(paso, ic(ZARZO_HONDO, o))
        listOf(-1f, 1f).forEach { sg ->
            val hoja = Path().apply {
                val x0 = if (sg < 0) paso.left else paso.right
                moveTo(x0, paso.top); lineTo(x0 - sg * s * 0.2f, paso.top + s * 0.08f); lineTo(x0 - sg * s * 0.2f, base + s * 0.02f); lineTo(x0, base); close()
            }
            drawPath(hoja, ic(TABLA, o))
            drawPath(hoja, ic(VETA, o), style = Stroke(p * 0.6f))
        }
        drawRect(ic(mez(TIERRA, BARRO_LUZ, 0.4f), o), Offset(paso.left + s * 0.2f, base - s * 0.08f), Size(paso.width - s * 0.4f, s * 0.08f))
        // La torre de madera sobre la puerta: los postes, la galería de tablas y el tejado de paja.
        val torre = Rect(c.x - s * 0.55f, c.y - s * 0.62f, c.x + s * 0.55f, cima + s * 0.1f)
        listOf(torre.left + p, torre.right - p * 3).forEach { x -> drawRect(ic(MADERA, o), Offset(x, torre.top), Size(p * 2.2f, torre.height)) }
        val galeria = Rect(torre.left - s * 0.06f, torre.top + s * 0.18f, torre.right + s * 0.06f, torre.top + s * 0.44f)
        rect(galeria, ic(TABLA, o))
        var xt = galeria.left + p * 2f
        while (xt < galeria.right) { drawLine(ic(VETA, o), Offset(xt, galeria.top), Offset(xt, galeria.bottom), p * 0.5f); xt += p * 3f }
        drawRect(sombraMueble(0.3f), Offset(galeria.left, galeria.bottom), Size(galeria.width, p * 1.6f))
        drawRect(luzMueble(if (o) 0.06f else 0.35f), galeria.topLeft, Size(galeria.width, p))
        val tejado = Path().apply { moveTo(torre.left - s * 0.18f, torre.top + s * 0.08f); lineTo(c.x, torre.top - s * 0.3f); lineTo(torre.right + s * 0.18f, torre.top + s * 0.08f); close() }
        drawPath(tejado, ic(PAJA, o))
        clipPath(tejado) {
            drawRect(sombraMueble(0.25f), Offset(c.x, torre.top - s), Size(s, s * 2))
            for (j in -6..6) drawLine(ic(PAJA_HUMO, o).copy(alpha = 0.5f), Offset(c.x, torre.top - s * 0.3f), Offset(c.x + j * s * 0.1f, torre.top + s * 0.1f), p * 0.4f)
        }
        // El roble a un lado, con el verde del tema.
        val tr = Offset(c.x + s * 1.55f, base)
        drawLine(ic(MADERA, o), tr, Offset(tr.x, tr.y - s * 0.7f), p * 2.4f)
        drawLine(ic(MADERA, o), Offset(tr.x, tr.y - s * 0.5f), Offset(tr.x - s * 0.18f, tr.y - s * 0.8f), p * 1.4f)
        listOf(Offset(-0.2f, -0.95f), Offset(0.18f, -0.98f), Offset(0f, -1.22f), Offset(-0.02f, -0.82f)).forEachIndexed { j, q ->
            drawCircle(ic(if (j == 2) ROBLE_HOJA else ROBLE_HOJA_SOMBRA, o), s * 0.26f, Offset(tr.x + q.x * s, tr.y + q.y * s))
        }
        drawCircle(ic(mez(ROBLE_HOJA, MUERDAGO, 0.4f), o), s * 0.12f, Offset(tr.x - s * 0.1f, tr.y - s * 1.3f))
        // De noche, una antorcha en la torre, con el color del tema.
        if (o) { val t = Offset(c.x + s * 0.42f, c.y - s * 0.5f); halo(t, s * 0.4f, e.c1, 0.8f, 8); drawCircle(e.c1, p * 1.6f, t) }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val o = oscuro
        val ni = enLienzo(area(MuebleVida.NOTAS))
        val vigaB = h * 0.035f
        val suelo = h * 0.965f

        // La pared de barro, la viga de arriba y el suelo.
        muroBarro(Rect(0f, vigaB, w, suelo), o, u, e.semilla + 8, listOf(Rect(w * 0.0f, h * 0.44f, w * 0.03f, h * 0.53f)))
        viga(Rect(0f, 0f, w, vigaB), o, u, e.semilla + 3)
        tierra(Rect(0f, suelo, w, h), o, u, e.semilla + 4)
        if (o) drawRect(NOCHE.copy(alpha = 0.3f), Offset.Zero, size)
        if (o) fueraDe(ni) { halo(Offset(-w * 0.06f, h * 1.02f), w * 0.5f, FUEGO_HALO, 1.15f) }

        // Los dos montantes del telar, apoyados en la pared.
        val g = 3f * u
        val montI = Rect(w * 0.018f, h * 0.02f, w * 0.018f + g, suelo + 0.8f * u)
        val montD = Rect(w * 0.982f - g, h * 0.02f, w * 0.982f, suelo + 0.8f * u)
        poste(montI, o, u, e.semilla + 20, 0.25f)
        poste(montD, o, u, e.semilla + 21)

        // La urdimbre que baja del borde de la tela hasta las pesas, en dos capas, con la vara de la calada.
        val borde = ni.bottom
        val vara = borde + (suelo - borde) * 0.36f
        val pesasA = borde + (suelo - borde) * 0.58f
        val pesasB = borde + (suelo - borde) * 0.8f
        val grupos = 12
        val ancho = ni.width + 2.4f * u
        val x0 = ni.left - 1.2f * u
        for (capa in 0..1) {
            val col = if (capa == 0) mez(LANA_CRUDA_OSC, TIERRA_OSC, 0.35f) else LANA_CRUDA
            for (gk in 0 until grupos) {
                val xa = x0 + ancho * gk / grupos
                val xb = x0 + ancho * (gk + 1) / grupos
                val cx = (xa + xb) / 2 + (if (capa == 0) -ancho / grupos * 0.25f else ancho / grupos * 0.25f)
                val pesa = if ((gk + capa) % 2 == 0) pesasA else pesasB
                val hilos = 9
                for (j in 0 until hilos) {
                    val xh = xa + (xb - xa) * (j + 0.5f) / hilos
                    drawLine(lam(col, o, 0.15f).copy(alpha = if (capa == 0) 0.75f else 0.9f), Offset(xh, borde), Offset(cx + (j - hilos / 2) * 0.12f * u, pesa - 0.8f * u), 0.18f * u)
                }
                if (capa == 0) pesaTelar(Offset(cx, pesa), 4.2f * u, o, u, gk)
            }
            if (capa == 0) {
                // La vara de la calada, entre las dos capas, en sus soportes.
                val v = Rect(montI.left - 1f * u, vara - 0.9f * u, montD.right + 1f * u, vara + 0.9f * u)
                drawRect(sombraMueble(0.3f), Offset(v.left, v.bottom), Size(v.width, 0.9f * u))
                drawRoundRect(lam(TABLA, o, 0.1f), v.topLeft, v.size, CornerRadius(0.9f * u))
                drawLine(luzMueble(if (o) 0.06f else 0.4f), Offset(v.left + u, v.top + 0.3f * u), Offset(v.right - u, v.top + 0.3f * u), 0.25f * u)
            }
        }
        for (gk in 0 until grupos) {
            val xa = x0 + ancho * gk / grupos
            val xb = x0 + ancho * (gk + 1) / grupos
            val cx = (xa + xb) / 2 + ancho / grupos * 0.25f
            val pesa = if ((gk + 1) % 2 == 0) pesasA else pesasB
            pesaTelar(Offset(cx, pesa), 4.4f * u, o, u, gk + 30)
        }

        // El enjullo de arriba, con la tela enrollada, en las horquillas de los montantes.
        val rollo = Rect(montI.left - 1.4f * u, h * 0.045f, montD.right + 1.4f * u, h * 0.1f)
        drawRect(sombraMueble(if (o) 0.35f else 0.26f), Offset(rollo.left, rollo.bottom), Size(rollo.width, 2f * u))
        drawRoundRect(lam(TABLA, o, 0.05f), rollo.topLeft, rollo.size, CornerRadius(rollo.height / 2))
        val enrollada = Rect(ni.left - 1.4f * u, rollo.top - 0.6f * u, ni.right + 1.4f * u, rollo.bottom + 0.6f * u)
        drawRoundRect(n(LANA_CRUDA_OSC, o), enrollada.topLeft, enrollada.size, CornerRadius(enrollada.height / 2))
        for (k in 1..4) drawLine(sombraMueble(0.12f), Offset(enrollada.left, enrollada.top + enrollada.height * k / 5f), Offset(enrollada.right, enrollada.top + enrollada.height * k / 5f), 0.2f * u)
        drawRect(luzMueble(if (o) 0.05f else 0.3f), Offset(enrollada.left + u, enrollada.top + 0.6f * u), Size(enrollada.width - 2f * u, 1f * u))
        drawRect(sombraMueble(if (o) 0.3f else 0.22f), Offset(enrollada.left, enrollada.bottom - enrollada.height * 0.3f), Size(enrollada.width, enrollada.height * 0.3f))
        listOf(rollo.left + 1.8f * u, rollo.right - 1.8f * u).forEach { x -> clavija(Offset(x, rollo.center.y), o, u, 0.05f) }

        // La cabecera tejida a cuadros y la tela de lana cruda (el nicho), con sus orillos.
        val tela = Rect(ni.left - 1.2f * u, rollo.bottom + 0.6f * u, ni.right + 1.2f * u, ni.bottom)
        sombraPieza(tela, 2.4f * u, if (o) 0.4f else 0.26f)
        cuadros(Rect(tela.left, tela.top, tela.right, ni.top), o, u, 6f * u, 0f)
        drawRect(sombraMueble(0.3f), Offset(tela.left, ni.top - 0.5f * u), Size(tela.width, 0.5f * u))
        rect(Rect(tela.left, ni.top, ni.left, ni.bottom), n(LANA_CRUDA_OSC, o))
        rect(Rect(ni.right, ni.top, tela.right, ni.bottom), n(mez(LANA_CRUDA_OSC, TIERRA_OSC, 0.2f), o))
        rect(ni, nicho(e, o, LANA_CRUDA))
        clipRect(ni.left, ni.top, ni.right, ni.bottom) {
            val hebra = (if (o) LANA_CRUDA else LANA_CRUDA_OSC).copy(alpha = if (o) 0.03f else 0.12f)
            var yy = ni.top + 0.7f * u
            while (yy < ni.bottom) { drawLine(hebra, Offset(ni.left, yy), Offset(ni.right, yy), 0.16f * u); yy += 0.9f * u }
            drawRect(sombraMueble(if (o) 0.12f else 0.06f), ni.topLeft, Size(ni.width, 1.6f * u))
        }
        // El borde de la tela, donde se deja de tejer: la última pasada de la trama, un poco desigual.
        val pasada = Path().apply {
            moveTo(tela.left, borde)
            var x = tela.left
            var k = 0
            while (x < tela.right) { x += 2f * u; lineTo(x, borde + 0.5f * u * azar(e.semilla, k)); k++ }
        }
        drawPath(pasada, n(LANA_CRUDA_OSC, o), style = Stroke(0.6f * u))

        // Al pie, el cesto con los ovillos de lana de los tres colores.
        cestoOvillos(Offset(w * 0.9f, h * 0.995f), w * 0.1f, h * 0.07f, o, u)
        // Del montante de la izquierda cuelga el huso con su lana.
        huso(Offset(montI.center.x, h * 0.22f), h * 0.2f, o, u)

        granoMueble(Rect(0f, 0f, w, h), o)
    }

    /** Una pesa de telar de barro cocido, en pirámide, con su agujero arriba por donde pasa la urdimbre. */
    private fun DrawScope.pesaTelar(c: Offset, ancho: Float, o: Boolean, u: Float, semilla: Int) {
        val alto = ancho * 1.25f
        val f = 0.15f
        val barro = mez(Color(0xFFA8764E), Color(0xFF8A6A48), azar(semilla, 3))
        val forma = Path().apply {
            moveTo(c.x - ancho * 0.2f, c.y); lineTo(c.x + ancho * 0.2f, c.y)
            lineTo(c.x + ancho * 0.5f, c.y + alto); lineTo(c.x - ancho * 0.5f, c.y + alto); close()
        }
        translate(0.6f * u, 0.7f * u) { drawPath(forma, sombraMueble(0.35f)) }
        drawPath(forma, lam(barro, o, f))
        clipPath(forma) {
            drawRect(luzMueble(if (o) 0.04f else 0.22f), Offset(c.x - ancho, c.y), Size(ancho * 0.75f, alto))
            drawRect(sombraMueble(if (o) 0.3f else 0.26f), Offset(c.x + ancho * 0.08f, c.y), Size(ancho, alto))
            for (k in 0 until 3) drawCircle(lam(mez(barro, Color.Black, 0.3f), o, f).copy(alpha = 0.4f), 0.25f * u, Offset(c.x + ancho * (0.3f * azar(semilla, k) - 0.15f), c.y + alto * (0.4f + 0.4f * azar(semilla, k + 4))))
        }
        drawCircle(lam(Color(0xFF3A2A1C), o, f), ancho * 0.1f, Offset(c.x, c.y + alto * 0.18f))
    }

    /** El cesto de mimbre con los ovillos de lana roja, verde y ocre. */
    private fun DrawScope.cestoOvillos(base: Offset, ancho: Float, alto: Float, o: Boolean, u: Float) {
        sombraContacto(Offset(base.x + ancho * 0.1f, base.y), ancho * 1.05f, alto * 0.22f, 0.32f)
        listOf(LANA_ROJA to -0.22f, LANA_VERDE to 0.12f, LANA_OCRE to -0.02f).forEachIndexed { k, (col, dx) ->
            val c = Offset(base.x + ancho * dx, base.y - alto * (0.9f + 0.18f * (k % 2)))
            val r = ancho * 0.19f
            drawCircle(n(col, o), r, c)
            for (j in 0 until 4) drawArc(n(mez(col, Color.Black, 0.3f), o), 200f + j * 30f, 120f, false, Offset(c.x - r * (0.9f - 0.15f * j), c.y - r * (0.9f - 0.15f * j)), Size(r * (1.8f - 0.3f * j), r * (1.8f - 0.3f * j)), style = Stroke(0.2f * u))
            drawCircle(luzMueble(if (o) 0.05f else 0.3f), r * 0.35f, Offset(c.x - r * 0.35f, c.y - r * 0.35f))
        }
        val cesto = Path().apply {
            moveTo(base.x - ancho * 0.5f, base.y - alto * 0.85f); lineTo(base.x + ancho * 0.5f, base.y - alto * 0.85f)
            lineTo(base.x + ancho * 0.4f, base.y); lineTo(base.x - ancho * 0.4f, base.y); close()
        }
        drawPath(cesto, n(mez(PAJA, ZARZO, 0.4f), o))
        clipPath(cesto) {
            var y = base.y - alto * 0.85f
            var k = 0
            while (y < base.y) {
                var x = base.x - ancho * 0.55f + (if (k % 2 == 0) 0f else ancho * 0.045f)
                while (x < base.x + ancho * 0.55f) { drawOval(n(PAJA, o).copy(alpha = 0.6f), Offset(x, y + alto * 0.02f), Size(ancho * 0.07f, alto * 0.14f)); x += ancho * 0.09f }
                drawLine(n(ZARZO, o), Offset(base.x - ancho, y), Offset(base.x + ancho, y), 0.25f * u)
                y += alto * 0.17f; k++
            }
            drawRect(sombraMueble(if (o) 0.35f else 0.24f), Offset(base.x + ancho * 0.1f, base.y - alto), Size(ancho, alto))
        }
        drawLine(n(ZARZO, o), Offset(base.x - ancho * 0.52f, base.y - alto * 0.85f), Offset(base.x + ancho * 0.52f, base.y - alto * 0.85f), 0.7f * u, StrokeCap.Round)
    }

    /** El huso colgado de su clavija: la vara, la lana hilada enrollada y la tortera de barro abajo. */
    private fun DrawScope.huso(clavo: Offset, alto: Float, o: Boolean, u: Float) {
        val arriba = Offset(clavo.x, clavo.y + alto * 0.08f)
        drawLine(n(LANA_CRUDA_OSC, o), clavo, arriba, 0.25f * u)
        drawLine(sombraMueble(0.3f), Offset(arriba.x + 0.6f * u, arriba.y + 0.6f * u), Offset(arriba.x + 0.6f * u, arriba.y + alto * 0.92f), 0.7f * u)
        drawLine(n(TABLA_LUZ, o), arriba, Offset(arriba.x, arriba.y + alto * 0.9f), 0.55f * u, StrokeCap.Round)
        val lana = Rect(arriba.x - alto * 0.09f, arriba.y + alto * 0.15f, arriba.x + alto * 0.09f, arriba.y + alto * 0.55f)
        drawOval(n(LANA_OCRE, o), lana.topLeft, lana.size)
        for (k in 1..5) drawLine(n(mez(LANA_OCRE, Color.Black, 0.25f), o), Offset(lana.left + lana.width * 0.1f, lana.top + lana.height * k / 6f), Offset(lana.right - lana.width * 0.1f, lana.top + lana.height * k / 6f + 0.4f * u), 0.2f * u)
        drawOval(luzMueble(if (o) 0.05f else 0.3f), Offset(lana.left + lana.width * 0.15f, lana.top + lana.height * 0.1f), Size(lana.width * 0.3f, lana.height * 0.5f))
        val tortera = Offset(arriba.x, arriba.y + alto * 0.72f)
        drawOval(n(OLLA, o), Offset(tortera.x - alto * 0.07f, tortera.y - alto * 0.03f), Size(alto * 0.14f, alto * 0.06f))
        drawOval(n(mez(OLLA, Color.White, 0.3f), o), Offset(tortera.x - alto * 0.07f, tortera.y - alto * 0.03f), Size(alto * 0.14f, alto * 0.025f))
        clavija(clavo, o, u, 0.2f)
    }

    /** Lo que brilla en las notas de noche: el metal y el esmalte en penumbra, sin perder su color. */
    private fun nm(c: Color, o: Boolean): Color = if (o) mez(c, NOCHE, 0.3f) else c

    /** El cuero de las notas: claro de día; de noche, oscuro y en penumbra, para que se lea la tinta clara. */
    private fun cueroNota(o: Boolean): Color = if (o) Color(0xFF2F2A21) else Color(0xFFD9BD8E)

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        val un = minOf(r.width, r.height) / 100f
        val o = oscuro
        val semilla = e.indice * 131 + e.tipo.length * 7
        val hoja = r.deflate(2.4f * un)
        // El retazo de cuero, cortado a cuchillo: el canto algo ondulado y las esquinas redondeadas.
        val borde = Path()
        val pasos = 40
        val radio = 5f * un
        for (k in 0 until pasos) {
            val t = k / pasos.toFloat()
            val per = 2 * (hoja.width + hoja.height)
            var d = per * t
            var q = when {
                d < hoja.width -> Offset(hoja.left + d, hoja.top)
                d < hoja.width + hoja.height -> { d -= hoja.width; Offset(hoja.right, hoja.top + d) }
                d < 2 * hoja.width + hoja.height -> { d -= hoja.width + hoja.height; Offset(hoja.right - d, hoja.bottom) }
                else -> { d -= 2 * hoja.width + hoja.height; Offset(hoja.left, hoja.bottom - d) }
            }
            // Las esquinas, recortadas en curva.
            q = Offset(q.x.coerceIn(hoja.left, hoja.right), q.y.coerceIn(hoja.top, hoja.bottom))
            val cx = q.x.coerceIn(hoja.left + radio, hoja.right - radio); val cy = q.y.coerceIn(hoja.top + radio, hoja.bottom - radio)
            val ddx = q.x - cx; val ddy = q.y - cy; val dd = sqrt(ddx * ddx + ddy * ddy)
            if (dd > radio) q = Offset(cx + ddx / dd * radio, cy + ddy / dd * radio)
            val j = 1.1f * un * (azar(semilla, k) - 0.5f)
            if (k == 0) borde.moveTo(q.x + j, q.y + j) else borde.lineTo(q.x + j * 0.6f, q.y + j)
        }
        borde.close()
        translate(1.2f * un, 2f * un) { drawPath(borde, sombraMueble(0.32f)) }
        val piel = cueroNota(o)
        drawPath(borde, piel)
        clipPath(borde) {
            // El grano del cuero: poros y alguna marca, y el canto más oscuro y bruñido.
            for (k in 0 until 50) drawCircle((if (o) Color(0xFF453D30) else CUERO_OSC).copy(alpha = 0.16f + 0.12f * azar(semilla, k)), (0.3f + 0.4f * azar(semilla, k + 60)) * un,
                Offset(hoja.left + hoja.width * azar(semilla, k + 120), hoja.top + hoja.height * azar(semilla, k + 180)))
            pinceladas(hoja, if (o) Color(0xFF453D30) else CUERO_OSC, semilla, 5, un * 0.6f, if (o) 0.12f else 0.1f)
            drawPath(borde, (if (o) Color(0xFF1E1A14) else CUERO_OSC).copy(alpha = 0.45f), style = Stroke(3.6f * un))
            drawRect(luzMueble(if (o) 0.025f else 0.12f), hoja.topLeft, Size(hoja.width * 0.35f, hoja.height))
            translate(-1.2f * un, -1.2f * un) { drawPath(borde, sombraMueble(if (o) 0.25f else 0.1f), style = Stroke(1.6f * un)) }
        }
        drawPath(borde, if (o) Color(0xFF4A4134) else mez(CUERO_OSC, VETA, 0.3f), style = Stroke(0.5f * un))
        // El pespunte de correa a lo largo del borde, fuera del texto.
        val costura = hoja.deflate(3.2f * un)
        val hilo = if (o) Color(0xFF6E5E46) else CORREA
        var x = costura.left + 3f * un
        while (x < costura.right - 2f * un) {
            drawLine(hilo, Offset(x, costura.top), Offset(x + 2f * un, costura.top), 0.6f * un, StrokeCap.Round)
            drawLine(hilo, Offset(x, costura.bottom), Offset(x + 2f * un, costura.bottom), 0.6f * un, StrokeCap.Round)
            x += 4f * un
        }
        var y = costura.top + 4f * un
        while (y < costura.bottom - 3f * un) {
            drawLine(hilo, Offset(costura.left, y), Offset(costura.left, y + 2f * un), 0.6f * un, StrokeCap.Round)
            drawLine(hilo, Offset(costura.right, y), Offset(costura.right, y + 2f * un), 0.6f * un, StrokeCap.Round)
            y += 4f * un
        }
        // La señal del tipo, en la esquina de abajo a la derecha (fuera del texto).
        val q = Offset(r.right - r.width * 0.13f, r.bottom - r.height * 0.13f)
        val t = r.height * 0.05f
        val marca = if (o) Color(0xFFC9B48A) else Color(0xFF7E590C)
        when (e.tipo) {
            "idea" -> with(TrazosGalia) {
                // La hoja de roble.
                hojaRoble(Offset(q.x + 0.4f * un, q.y + 0.6f * un), t * 3.2f, -35f, sombraMueble(0.3f), sombraMueble(0.3f))
                hojaRoble(q, t * 3.2f, -35f, if (o) Color(0xFF8DAA6A) else ROBLE_HOJA, if (o) Color(0xFF3E5226) else ROBLE_HOJA_SOMBRA)
            }
            "pensamiento" -> {
                // La luna nueva: la noche, por la que cuentan los galos.
                val luna = Path().apply {
                    addOval(Rect(q.x - t * 1.2f, q.y - t * 1.2f, q.x + t * 1.2f, q.y + t * 1.2f))
                    addOval(Rect(q.x - t * 0.45f, q.y - t * 1.55f, q.x + t * 1.75f, q.y + t * 0.75f))
                    fillType = PathFillType.EvenOdd
                }
                clipPath(Path().apply { addOval(Rect(q.x - t * 1.2f, q.y - t * 1.2f, q.x + t * 1.2f, q.y + t * 1.2f)) }) { drawPath(luna, marca) }
                drawCircle(marca, 0.55f * un, Offset(q.x - t * 1.9f, q.y - t * 1.5f))
                drawCircle(marca, 0.4f * un, Offset(q.x - t * 2.6f, q.y - t * 0.6f))
            }
            "proposito" -> with(TrazosGalia) {
                // La rueda de las monedas.
                rueda(q, t * 1.3f, marca, Color.Transparent)
            }
            else -> {
                // El recordatorio: una tira de lana a cuadros pasada por un agujero de la esquina y anudada.
                val ag = Offset(q.x + 1f * un, q.y - 3.4f * un)
                drawCircle(if (o) Color(0xFF14110C) else Color(0xFF3A2A1A), 1.3f * un, ag)
                val tiras = listOf(Offset(ag.x - 6f * un, ag.y + 9f * un), Offset(ag.x + 2.6f * un, ag.y + 10f * un))
                tiras.forEachIndexed { k, fin ->
                    drawLine(sombraMueble(0.3f), Offset(ag.x + 0.6f * un, ag.y + 0.8f * un), Offset(fin.x + 0.6f * un, fin.y + 0.8f * un), 2.8f * un, StrokeCap.Round)
                    drawLine(nm(if (k == 0) LANA_ROJA else LANA_VERDE, o), ag, fin, 2.6f * un, StrokeCap.Round)
                    drawLine(LANA_OCRE, Offset(ag.x + (fin.x - ag.x) * 0.3f - 0.8f * un, ag.y + (fin.y - ag.y) * 0.3f), Offset(ag.x + (fin.x - ag.x) * 0.3f + 0.8f * un, ag.y + (fin.y - ag.y) * 0.3f), 0.5f * un)
                    drawLine(LANA_OCRE, Offset(ag.x + (fin.x - ag.x) * 0.7f - 0.8f * un, ag.y + (fin.y - ag.y) * 0.7f), Offset(ag.x + (fin.x - ag.x) * 0.7f + 0.8f * un, ag.y + (fin.y - ag.y) * 0.7f), 0.5f * un)
                }
                drawCircle(nm(LANA_ROJA, o), 2.3f * un, Offset(ag.x + 0.3f * un, ag.y + 1.2f * un))
                drawCircle(luzMueble(if (o) 0.1f else 0.35f), 0.6f * un, Offset(ag.x - 0.2f * un, ag.y + 0.6f * un))
            }
        }
        // Arriba, la fíbula que la prende a la tela: de bronce; de oro con su gota de coral si está fijada.
        fibula(Offset(r.center.x, hoja.top + 3f * un), if (e.fijada) 30f * un else 24f * un, e.fijada, o, un)
    }

    /**
     * Una fíbula de La Tène, de lado: el resorte enrollado, el arco, el pie vuelto sobre el arco con su bolita y la aguja
     * que atraviesa el cuero. [largo] es su ancho; [oro] la hace de oro con una gota de coral en el pie.
     */
    private fun DrawScope.fibula(c: Offset, largo: Float, oro: Boolean, o: Boolean, un: Float) {
        val metal = nm(if (oro) ORO else BRONCE, o)
        val luz = nm(if (oro) ORO_BRILLO else mez(BRONCE, ORO_BRILLO, 0.5f), o)
        val hondo = mez(metal, Color.Black, 0.35f)
        val x0 = c.x - largo / 2; val x1 = c.x + largo / 2
        val g = largo * 0.1f
        // La sombra en el cuero.
        translate(0.8f * un, 1.2f * un) {
            drawPath(Path().apply { moveTo(x0, c.y); quadraticTo(c.x, c.y - largo * 0.55f, x1, c.y) }, sombraMueble(0.35f), style = Stroke(g, cap = StrokeCap.Round))
        }
        // La aguja: se ve a los lados, donde entra y sale del cuero.
        drawLine(hondo, Offset(x0, c.y + g * 0.3f), Offset(x0 + largo * 0.2f, c.y + g * 0.3f), g * 0.4f, StrokeCap.Round)
        drawLine(hondo, Offset(x1 - largo * 0.22f, c.y + g * 0.3f), Offset(x1, c.y + g * 0.3f), g * 0.4f, StrokeCap.Round)
        // El arco.
        val arco = Path().apply { moveTo(x0 + g, c.y); quadraticTo(c.x - largo * 0.1f, c.y - largo * 0.6f, x1 - g * 0.6f, c.y) }
        drawPath(arco, hondo, style = Stroke(g * 1.35f, cap = StrokeCap.Round))
        drawPath(arco, metal, style = Stroke(g, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(x0 + g * 1.4f, c.y - g * 0.4f); quadraticTo(c.x - largo * 0.12f, c.y - largo * 0.62f, c.x + largo * 0.1f, c.y - largo * 0.26f) }, luz.copy(alpha = 0.8f), style = Stroke(g * 0.3f, cap = StrokeCap.Round))
        // El pie vuelto sobre el arco, con su bolita (de coral si es de oro).
        drawPath(Path().apply { moveTo(x1 - g * 0.6f, c.y); quadraticTo(x1 + g * 0.4f, c.y - largo * 0.18f, c.x + largo * 0.2f, c.y - largo * 0.22f) }, metal, style = Stroke(g * 0.7f, cap = StrokeCap.Round))
        drawCircle(if (oro) nm(CORAL, o) else metal, g * (if (oro) 0.95f else 0.75f), Offset(c.x + largo * 0.2f, c.y - largo * 0.22f))
        drawCircle(luzMueble(0.5f), g * 0.3f, Offset(c.x + largo * 0.2f - g * 0.25f, c.y - largo * 0.22f - g * 0.25f))
        // El resorte: las vueltas del alambre.
        for (k in 0 until 4) {
            val cx = x0 + g * (0.4f + 0.55f * k)
            drawCircle(hondo, g * 0.75f, Offset(cx, c.y))
            drawCircle(metal, g * 0.6f, Offset(cx - g * 0.1f, c.y - g * 0.1f))
            drawCircle(luz, g * 0.2f, Offset(cx - g * 0.3f, c.y - g * 0.3f))
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFE6E1CF) else TINTA
}
