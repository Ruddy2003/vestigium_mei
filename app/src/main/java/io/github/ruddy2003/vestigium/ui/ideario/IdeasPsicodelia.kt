package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
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
import io.github.ruddy2003.vestigium.ui.TrazosPsicodelia
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraContacto
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Psicodelia
// =====================================================================

/**
 * Psicodelia: el mostrador de una tienda de discos del barrio, en San Francisco en 1967 (`docs/diseno/temas/psicodelia.md`;
 * las tiendas de discos son recurso). El mostrador es de madera pintada a mano: violeta, con margaritas amarillas en los
 * costados, una onda naranja en cada travesaño y, abajo, la onda de arcoíris del tintero partido. Cada tema es una caja de
 * sencillos de 45 en su hueco del mostrador: la caja de pino (o pintada), con la tarjeta del rótulo clavada con dos
 * chinchetas, y dentro los sencillos de canto en sus fundas de papel, más cuanto más lleno, con el separador del tema que
 * asoma con su pestaña y un sencillo que alguien sacó a medias, con su etiqueta vista por el agujero de la funda; las ideas
 * sin tema van en una caja de cartón, revueltas. Encima del mostrador, la caja registradora, la lámpara de cera, un frasco de
 * botones de solapa, el sencillo de la semana en su atril y el tocadiscos de maleta con su eje alto y tres sencillos
 * esperando. En la pared, la ventana a la calle con la niebla, las casas victorianas (dos grises y una pintada) y los cables
 * del trolebús, con un arcoíris pintado en el vidrio; un cartel de concierto inventado («baile», en letras de burbuja que se
 * derriten, sobre los rayos naranja y azul y la flor que vibra); tres sencillos colgados de un clavo por su agujero grande y
 * una hoja volante de «paz» en tinta de arcoíris; arriba, el tubo de luz negra. Abajo, el piso de duela con un tapete
 * trenzado, una caja de cartón con discos de larga duración y una maceta de margaritas pintada con la paz. De día, la luz entra por la ventana, de la izquierda. De noche, la misma tienda con la luz negra encendida:
 * todo se hunde en el negro violeta y brilla lo fluorescente (los carteles, la pintura del mostrador, las pestañas, las
 * tarjetas de papel), y la lámpara de cera echa su luz tibia. Por dentro, la caja vista desde arriba con los sencillos de
 * canto. Las fichas son fundas de papel de sencillo: la muesca del pulgar arriba, por donde asoma el disco; el agujero
 * redondo con la etiqueta y su agujero grande; la marca del aro del disco y la onda de arcoíris de la tienda.
 */
object IdeasPsicodelia : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta de la tienda
    private val MAGENTA = TrazosPsicodelia.MAGENTA
    private val VERDE = TrazosPsicodelia.VERDE
    private val NARANJA = TrazosPsicodelia.NARANJA
    private val AZUL = TrazosPsicodelia.AZUL
    private val AMARILLO = TrazosPsicodelia.AMARILLO
    private val VIOLETA = TrazosPsicodelia.VIOLETA
    /** La luz negra: lo que brilla sobre el negro violeta. */
    private val LIMON = TrazosPsicodelia.LIMON
    private val FMAGENTA = TrazosPsicodelia.FLUOR_MAGENTA
    private val FVIOLETA = TrazosPsicodelia.FLUOR_VIOLETA
    private val FNARANJA = TrazosPsicodelia.FLUOR_NARANJA
    private val FAZUL = Color(0xFF6CF0C8)
    private val SALA = TrazosPsicodelia.SALA
    private val VINILO = TrazosPsicodelia.VINILO
    /** La tinta de las figuras: un violeta casi negro. */
    private val TINTA = TrazosPsicodelia.TINTA
    /** La pared de la tienda, pintada de crema, y su sombra. */
    private val PARED = Color(0xFFF1E6D3)
    private val PARED_SOMBRA = Color(0xFFCDBDA6)
    /** El mostrador, pintado a mano de violeta: su luz y su sombra. */
    private val MOSTRADOR = Color(0xFF4E2D80)
    private val MOSTRADOR_LUZ = Color(0xFF6C4AA6)
    private val MOSTRADOR_OSC = Color(0xFF2C1752)
    /** El pino de las cajas y de la cubierta, y sus nudos. */
    private val PINO = Color(0xFFCFA673)
    private val PINO_LUZ = Color(0xFFE8CB98)
    private val PINO_OSC = Color(0xFF8C6A42)
    private val NUDO = Color(0xFF7A532E)
    /** La cubierta del mostrador, de pino barnizado, más oscura. */
    private val CUBIERTA = Color(0xFFB98652)
    /** La duela del piso. */
    private val DUELA = Color(0xFF9A6B42)
    /** El papel de las fundas: el del periódico, el blanco, el cartón y la estraza. */
    private val PAPEL = TrazosPsicodelia.PAPEL
    private val BLANCO = Color(0xFFFBF6EC)
    private val CARTON = Color(0xFFC9A877)
    /** La tarjeta del rótulo: cartulina crema; bajo la luz negra, el papel que se enciende de lila. */
    private val CARTULINA = Color(0xFFF7EEDC)
    private val CARTULINA_NOCHE = Color(0xFFD2C4F2)
    /** Las casas pintadas que se ven por la ventana. */
    private val CASA_GRIS = TrazosPsicodelia.CASA_GRIS
    private val CASA_BLANCA = TrazosPsicodelia.CASA_BLANCA
    private val CASA_ROSA = TrazosPsicodelia.CASA_ROSA
    private val CASA_TURQUESA = TrazosPsicodelia.CASA_TURQUESA
    private val CASA_AMARILLA = TrazosPsicodelia.CASA_AMARILLA
    private val NIEBLA = TrazosPsicodelia.NIEBLA
    /** El metal: el cromo de la caja registradora y del tocadiscos. */
    private val CROMO = Color(0xFFC9CDD0)
    private val CROMO_OSC = Color(0xFF7D8388)
    /** El oro del disco de oro. */
    private val ORO = Color(0xFFE2B33C)
    private val ORO_OSC = Color(0xFF9A7419)

    /** Los pares que vibran: un color y su complementario, como en los carteles. */
    private val PARES = listOf(MAGENTA to VERDE, NARANJA to AZUL, AMARILLO to VIOLETA, VERDE to MAGENTA, AZUL to NARANJA, VIOLETA to AMARILLO)

    /** El color de lo que no brilla: de día, tal cual; de noche, hundido en el negro violeta de la luz negra. */
    private fun t(c: Color, oscuro: Boolean, k: Float = 0.8f): Color = if (oscuro) mezcla(c, SALA, k) else c

    /** Lo que se pintó o se imprimió en tinta fluorescente: de día, su color; bajo la luz negra, el que brilla. */
    private fun fluor(c: Color, oscuro: Boolean): Color = if (!oscuro) c else when (c) {
        MAGENTA -> FMAGENTA
        VERDE -> LIMON
        NARANJA -> FNARANJA
        AMARILLO -> LIMON
        AZUL -> FAZUL
        VIOLETA -> FVIOLETA
        else -> mezcla(c, FVIOLETA, 0.45f)
    }

    /** La sombra con color: de día, el violeta hondo de los muebles; de noche, el negro violeta de la sala. */
    private fun sombra(oscuro: Boolean, a: Float) = if (oscuro) SALA.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el lila apagado del tubo de luz negra. */
    private fun brillo(oscuro: Boolean, a: Float) = if (oscuro) FVIOLETA.copy(alpha = (a * 0.35f).coerceIn(0f, 1f)) else luzMueble(a)

    /** El halo de algo que brilla (la luz negra, la lámpara de cera, la tinta fluorescente). */
    private fun DrawScope.resplandor(c: Offset, r: Float, col: Color, fuerza: Float = 1f) {
        mancha(c, r, col.copy(alpha = (0.18f * fuerza).coerceIn(0f, 1f)))
        mancha(c, r * 0.45f, col.copy(alpha = (0.22f * fuerza).coerceIn(0f, 1f)))
    }

    /** Una forma que brilla bajo la luz negra: el halo de su color alrededor y la forma encima. */
    private fun DrawScope.brilla(p: Path, col: Color, g: Float) {
        drawPath(p, col.copy(alpha = 0.1f * col.alpha), style = Stroke(g * 3.2f, join = StrokeJoin.Round))
        drawPath(p, col.copy(alpha = 0.22f * col.alpha), style = Stroke(g * 1.4f, join = StrokeJoin.Round))
        drawPath(p, col)
    }

    override val cols = 2
    override val proporcion = 1.36f
    override val corona = 0.76f
    override val base = 0.3f
    override val lado = 0.075f
    override val hueco = 0.03f

    /** En la corona: donde la pared toca la cubierta, donde pisan las cosas, el canto de la cubierta y su borde de abajo. */
    private const val TOPE = 0.845f
    private const val PIE = 0.875f
    private const val CANTO = 0.895f
    private const val FRENTE = 0.955f
    /** Cuánto sale la caja al abrirla, en fracción del alto del hueco. */
    private const val SALIDA = 0.1f
    /** En la base: el fondo del zócalo pintado y donde empieza el piso, en fracción de su alto. */
    private const val ZOCALO = 0.5f
    private const val PISO = 0.6f
    /** En el hueco: el borde de arriba del frente de la caja y su pie, en fracción del alto. */
    private const val CAJA_ARRIBA = 0.5f
    private const val CAJA_ABAJO = 0.965f

    // ---------------------------------------------------------------- ayudantes de la tienda

    /** Una tabla de pino en [r], con su veta, algún nudo y el canto de arriba a la luz. */
    private fun DrawScope.pino(r: Rect, base: Color, semilla: Int, oscuro: Boolean, vertical: Boolean = false, nudos: Int = 1) {
        val u = density
        drawRect(base, r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            vetas(r, t(NUDO, oscuro, 0.7f).copy(alpha = if (oscuro) 0.4f else 0.26f), semilla, densidad = 0.5f, vertical = vertical, grosor = 0.7f, ondulacion = 0.7f)
            for (k in 0 until nudos) {
                if (azar(semilla, k + 50) < 0.35f) continue
                val p = Offset(r.left + r.width * (0.15f + 0.7f * azar(semilla, k + 51)), r.top + r.height * (0.25f + 0.5f * azar(semilla, k + 52)))
                val a = minOf(r.width, r.height) * 0.16f
                val tam = if (vertical) Size(a * 0.8f, a * 1.6f) else Size(a * 1.6f, a * 0.8f)
                drawOval(t(NUDO, oscuro, 0.7f).copy(alpha = 0.55f), Offset(p.x - tam.width / 2, p.y - tam.height / 2), tam)
                drawOval(t(NUDO, oscuro, 0.7f).copy(alpha = 0.3f), Offset(p.x - tam.width, p.y - tam.height), Size(tam.width * 2, tam.height * 2), style = Stroke(0.6f * u))
            }
        }
    }

    /** Una superficie pintada a brocha en [r]: el [color] con las pinceladas, un poco de veta debajo y la luz de la izquierda. */
    private fun DrawScope.pintado(r: Rect, color: Color, semilla: Int, oscuro: Boolean) {
        val u = density
        drawRect(color, r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            vetas(r, Color.Black.copy(alpha = if (oscuro) 0.12f else 0.07f), semilla, densidad = 0.35f, vertical = r.height > r.width, grosor = 0.6f, ondulacion = 0.5f)
            pinceladas(r, if (oscuro) Color.Black else Color.White, semilla + 3, (r.width * r.height / (u * u * 300f)).toInt().coerceIn(3, 60), u, if (oscuro) 0.08f else 0.07f)
            pinceladas(r, Color.Black, semilla + 5, (r.width * r.height / (u * u * 400f)).toInt().coerceIn(2, 40), u, 0.06f)
        }
    }

    /** Una chincheta de colores, clavada en [c]: la cabeza abombada con su brillo y la sombra que echa. */
    private fun DrawScope.chincheta(c: Offset, r: Float, color: Color, oscuro: Boolean) {
        drawCircle(sombra(oscuro, 0.35f), r, c + Offset(r * 0.45f, r * 0.55f))
        val col = fluor(color, oscuro)
        if (oscuro) mancha(c, r * 2.6f, col.copy(alpha = 0.25f))
        drawCircle(Brush.radialGradient(listOf(mezcla(col, Color.White, 0.45f), col, mezcla(col, Color.Black, 0.35f)), c - Offset(r * 0.35f, r * 0.35f), r * 1.4f), r, c)
        drawCircle(Color.White.copy(alpha = 0.6f), r * 0.25f, c - Offset(r * 0.35f, r * 0.35f))
    }

    /** Un tramo de cinta de papel engomado, de [a] a [b], de [ancho], translúcido. */
    private fun DrawScope.cinta(a: Offset, b: Offset, ancho: Float, oscuro: Boolean) {
        val d = b - a
        val l = d.getDistance().coerceAtLeast(0.01f)
        val n = Offset(-d.y / l, d.x / l) * (ancho / 2)
        val p = Path().apply { moveTo(a.x + n.x, a.y + n.y); lineTo(b.x + n.x, b.y + n.y); lineTo(b.x - n.x, b.y - n.y); lineTo(a.x - n.x, a.y - n.y); close() }
        drawPath(p, t(Color(0xFFE9DDB8), oscuro, 0.7f).copy(alpha = 0.78f))
        drawPath(p, sombra(oscuro, 0.12f), style = Stroke(0.5f * density))
    }

    /** Unas rayas cortas y redondas, como renglones de letra chica impresa, dentro de [r]. */
    private fun DrawScope.renglones(r: Rect, color: Color, n: Int, semilla: Int, centrado: Boolean = true) {
        val paso = r.height / n
        for (k in 0 until n) {
            val largo = r.width * (0.55f + 0.45f * azar(semilla, k))
            val x0 = if (centrado) r.center.x - largo / 2 else r.left
            val y = r.top + paso * (k + 0.5f)
            drawLine(color, Offset(x0, y), Offset(x0 + largo, y), paso * 0.42f, StrokeCap.Round)
        }
    }

    /** Lo que avanza cada letra de burbuja, en fracción de su alto. */
    private fun avance(ch: Char): Float = when (ch) {
        'b', 'p' -> 0.72f
        'a' -> 0.7f
        'e', 'o' -> 0.64f
        'z' -> 0.62f
        'i', 'l' -> 0.34f
        else -> 0.4f
    }

    /** El ancho de [texto] en letras de burbuja de [alto]. */
    private fun anchoBurbuja(texto: String, alto: Float): Float = texto.sumOf { avance(it).toDouble() }.toFloat() * alto

    /**
     * Las letras de burbuja de los carteles (como las de bloque que venían de Roller, infladas): cada letra armada con trazos
     * redondos que, al trazarse gruesos, se juntan en una sola forma blanda. [x0] y [base] son el inicio y el renglón; [alto],
     * el de las astas. [sube] mueve cada letra arriba y abajo, como si la letra ondulara. Devuelve el trazo y, aparte, dónde
     * gotea (las letras que se derriten).
     */
    private fun letrasBurbuja(texto: String, x0: Float, base: Float, alto: Float, sube: Float = 0f): Pair<Path, List<Offset>> {
        val p = Path()
        val gotas = mutableListOf<Offset>()
        var x = x0
        texto.forEachIndexed { i, ch ->
            val dy = sube * alto * sin(i * 1.7f + 0.4f)
            fun q(fx: Float, fy: Float) = Offset(x + fx * alto, base - alto + fy * alto + dy)
            fun linea(a: Offset, b: Offset) { p.moveTo(a.x, a.y); p.lineTo(b.x, b.y) }
            fun aro(fx: Float, fy: Float, r: Float) { p.addOval(Rect(q(fx, fy), r * alto)) }
            when (ch) {
                'b' -> { linea(q(0.14f, 0.02f), q(0.14f, 1f)); aro(0.4f, 0.73f, 0.25f); gotas += q(0.14f, 1.02f) }
                'p' -> { linea(q(0.14f, 0.48f), q(0.14f, 1.32f)); aro(0.4f, 0.73f, 0.25f) }
                'a' -> { aro(0.31f, 0.73f, 0.25f); linea(q(0.56f, 0.48f), q(0.56f, 1f)); gotas += q(0.56f, 1.02f) }
                'e' -> {
                    linea(q(0.07f, 0.74f), q(0.56f, 0.74f))
                    p.arcTo(Rect(q(0.32f, 0.74f), 0.25f * alto), 0f, -312f, true)
                }
                'o' -> aro(0.32f, 0.73f, 0.25f)
                'z' -> { p.moveTo(q(0.06f, 0.5f).x, q(0.06f, 0.5f).y); p.lineTo(q(0.5f, 0.5f).x, q(0.5f, 0.5f).y); p.lineTo(q(0.06f, 0.98f).x, q(0.06f, 0.98f).y); p.lineTo(q(0.52f, 0.98f).x, q(0.52f, 0.98f).y); gotas += q(0.3f, 1f) }
                'i' -> { linea(q(0.17f, 0.5f), q(0.17f, 1f)); linea(q(0.17f, 0.22f), q(0.17f, 0.23f)) }
                'l' -> { linea(q(0.17f, 0.02f), q(0.17f, 1f)); gotas += q(0.17f, 1.02f) }
            }
            x += avance(ch) * alto
        }
        return p to gotas
    }

    /**
     * Pinta letras de burbuja: el contorno grueso de [contorno], el relleno de [relleno] encima y un brillo fino arriba a la
     * izquierda, como una letra inflada; de las [gotas] cuelgan chorros que se derriten, de [largo].
     */
    private fun DrawScope.burbujas(trazo: Pair<Path, List<Offset>>, grueso: Float, relleno: Brush, contorno: Color, largo: Float, semilla: Int, brillo: Float = 0.4f) {
        val (p, gotas) = trazo
        val chorros = Path()
        gotas.forEachIndexed { k, g ->
            if (azar(semilla, k) < 0.4f) return@forEachIndexed
            val l = largo * (0.4f + 0.6f * azar(semilla, k + 10))
            chorros.moveTo(g.x, g.y); chorros.lineTo(g.x, g.y + l)
            chorros.addOval(Rect(Offset(g.x, g.y + l), grueso * 0.42f))
        }
        val redondo = Stroke(grueso, cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawPath(p, contorno, style = Stroke(grueso * 1.6f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(chorros, contorno, style = Stroke(grueso * 0.95f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(p, relleno, style = redondo)
        drawPath(chorros, relleno, style = Stroke(grueso * 0.42f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        translate(-grueso * 0.14f, -grueso * 0.16f) {
            drawPath(p, Color.White.copy(alpha = brillo), style = Stroke(grueso * 0.2f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }

    // ---------------------------------------------------------------- la pared: la ventana y la calle

    /**
     * La ventana a la calle dentro de [r], con su marco de madera pintado de turquesa y el montante de arriba. Detrás, la
     * calle de enfrente: tres casas victorianas (dos grises y una pintada de rosa, como en 1967), la acera, el farol y los
     * cables del trolebús, y la niebla que entra; de noche, el cielo violeta, la niebla encendida por el farol y las ventanas
     * prendidas (una, con la luz negra de una fiesta). En el vidrio, el arcoíris y dos margaritas pintados por la tienda.
     */
    private fun DrawScope.ventana(r: Rect, oscuro: Boolean) {
        val u = density
        val marco = 5.5f * u
        val vidrio = Rect(r.left + marco, r.top + marco, r.right - marco, r.bottom - marco)
        val montante = vidrio.top + vidrio.height * 0.17f
        // El derrame del muro alrededor del marco.
        drawRect(sombra(oscuro, 0.2f), Offset(r.left - 3f * u, r.top - 3f * u), Size(r.width + 6f * u, r.height + 6f * u))
        clipRect(vidrio.left, vidrio.top, vidrio.right, vidrio.bottom) {
            val acera = vidrio.top + vidrio.height * 0.62f
            // El cielo con niebla; de noche, el violeta de la ciudad con la niebla encendida abajo.
            if (!oscuro) drawRect(Brush.verticalGradient(listOf(Color(0xFFD7DEE3), Color(0xFFE9ECEC), Color(0xFFF4EEE4)), vidrio.top, acera), vidrio.topLeft, Size(vidrio.width, acera - vidrio.top))
            else drawRect(Brush.verticalGradient(listOf(Color(0xFF140E26), Color(0xFF221A3C), Color(0xFF3A2D52)), vidrio.top, acera), vidrio.topLeft, Size(vidrio.width, acera - vidrio.top))
            // Los cables del trolebús, cruzando el cielo.
            for (k in 0 until 2) {
                val y = vidrio.top + vidrio.height * (0.24f + 0.05f * k)
                val cable = Path().apply { moveTo(vidrio.left, y); quadraticTo(vidrio.center.x, y + vidrio.height * 0.04f, vidrio.right, y - vidrio.height * 0.02f) }
                drawPath(cable, if (oscuro) Color(0xFF0C0816) else Color(0xFF5B5862).copy(alpha = 0.7f), style = Stroke(0.6f * u))
            }
            // Las casas de enfrente: dos grises y una pintada.
            val alto = vidrio.height * 0.4f
            val casas = listOf(Triple(0.1f, CASA_GRIS, Color(0xFF5E6B78)), Triple(0.47f, CASA_ROSA, Color(0xFF4E5A68)), Triple(0.86f, mezcla(CASA_GRIS, Color(0xFF6E6A62), 0.25f), Color(0xFF5E6B78)))
            casas.forEachIndexed { k, (fx, col, vent) ->
                val ancho = vidrio.width * 0.36f
                val b = Offset(vidrio.left + vidrio.width * fx, acera)
                val ventanas = if (!oscuro) vent else when (k) { 0 -> Color(0xFFFFC86A); 1 -> mezcla(FVIOLETA, Color.White, 0.2f); else -> Color(0xFF15101F) }
                with(TrazosPsicodelia) {
                    casaVictoriana(b, ancho, alto, t(col, oscuro, 0.72f), t(CASA_BLANCA, oscuro, 0.72f), ventanas)
                }
                if (oscuro && k != 2) mancha(Offset(b.x - ancho * 0.15f, acera - alto * 0.45f), ancho * 0.5f, ventanas.copy(alpha = 0.18f))
            }
            // La lejanía: las casas se pierden un poco en la niebla.
            drawRect((if (oscuro) Color(0xFF3A2D52) else NIEBLA).copy(alpha = if (oscuro) 0.25f else 0.38f), Offset(vidrio.left, acera - alto * 1.1f), Size(vidrio.width, alto * 1.1f))
            // La acera, el bordillo y la calle.
            drawRect(t(Color(0xFFB9B4AC), oscuro, 0.75f), Offset(vidrio.left, acera), Size(vidrio.width, vidrio.height * 0.05f))
            drawRect(t(Color(0xFFDAD5CB), oscuro, 0.75f), Offset(vidrio.left, acera + vidrio.height * 0.05f), Size(vidrio.width, 1.2f * u))
            drawRect(Brush.verticalGradient(listOf(t(Color(0xFF8C8A88), oscuro, 0.75f), t(Color(0xFF6E6C6C), oscuro, 0.75f)), acera + vidrio.height * 0.05f, vidrio.bottom),
                Offset(vidrio.left, acera + vidrio.height * 0.05f + 1.2f * u), Size(vidrio.width, vidrio.bottom - acera))
            // La niebla que entra: bandas blandas sobre las casas, más espesas abajo.
            val niebla = if (oscuro) Color(0xFF6B5A8A) else NIEBLA
            drawRect(Brush.verticalGradient(listOf(niebla.copy(alpha = 0f), niebla.copy(alpha = if (oscuro) 0.25f else 0.55f), niebla.copy(alpha = if (oscuro) 0.12f else 0.3f)),
                vidrio.top + vidrio.height * 0.2f, vidrio.bottom), Offset(vidrio.left, vidrio.top + vidrio.height * 0.2f), Size(vidrio.width, vidrio.height * 0.8f))
            for (k in 0 until 5) {
                val c = Offset(vidrio.left + vidrio.width * (0.1f + 0.22f * k), vidrio.top + vidrio.height * (0.42f + 0.1f * azar(611, k)))
                val rx = vidrio.width * (0.22f + 0.1f * azar(612, k))
                drawOval(niebla.copy(alpha = if (oscuro) 0.14f else 0.42f), Offset(c.x - rx, c.y - rx * 0.16f), Size(rx * 2, rx * 0.32f))
            }
            // El farol de la acera de este lado: el poste, la cabeza y, de noche, su luz en la niebla.
            val fx = vidrio.left + vidrio.width * 0.78f
            val ftop = vidrio.top + vidrio.height * 0.36f
            if (oscuro) {
                mancha(Offset(fx, ftop), vidrio.width * 0.55f, Color(0xFFFFD9A0).copy(alpha = 0.22f))
                mancha(Offset(fx, ftop), vidrio.width * 0.18f, Color(0xFFFFE6C0).copy(alpha = 0.5f))
            }
            drawLine(t(Color(0xFF3E4A46), oscuro, 0.6f), Offset(fx, ftop), Offset(fx, vidrio.bottom), 2.2f * u)
            drawLine(t(Color(0xFF3E4A46), oscuro, 0.6f), Offset(fx, ftop + 2f * u), Offset(fx - vidrio.width * 0.08f, ftop - 1f * u), 1.4f * u, StrokeCap.Round)
            drawOval(if (oscuro) Color(0xFFFFEBC8) else t(Color(0xFFD9D6CC), oscuro), Offset(fx - vidrio.width * 0.12f, ftop - 2.6f * u), Size(vidrio.width * 0.08f, 3.6f * u))
            // El reflejo en el vidrio.
            listOf(0.08f to 0.1f, 0.5f to 0.05f).forEach { (f, a) ->
                val x0 = vidrio.left + vidrio.width * f
                drawPath(Path().apply {
                    moveTo(x0, vidrio.bottom); lineTo(x0 + vidrio.width * a, vidrio.bottom)
                    lineTo(x0 + vidrio.width * (a + 0.4f), vidrio.top); lineTo(x0 + vidrio.width * 0.4f, vidrio.top); close()
                }, Color.White.copy(alpha = if (oscuro) 0.025f else 0.12f))
            }
            // El arcoíris pintado en el vidrio por la tienda, con dos margaritas; de noche, la pintura fluorescente brilla.
            val ca = Offset(vidrio.center.x, montante + vidrio.height * 0.48f)
            val bandas = if (oscuro) listOf(FMAGENTA, FNARANJA, LIMON, FAZUL, FVIOLETA) else listOf(MAGENTA, NARANJA, AMARILLO, VERDE, AZUL)
            val gb = vidrio.width * 0.03f
            bandas.forEachIndexed { k, col ->
                val rr = vidrio.width * 0.33f - k * gb
                if (oscuro) drawArc(col.copy(alpha = 0.2f), 180f, 180f, false, Offset(ca.x - rr, ca.y - rr), Size(rr * 2, rr * 2), style = Stroke(gb * 2.2f))
                drawArc(col.copy(alpha = if (oscuro) 0.85f else 0.7f), 180f, 180f, false, Offset(ca.x - rr, ca.y - rr), Size(rr * 2, rr * 2), style = Stroke(gb * 1.02f))
            }
            with(TrazosPsicodelia) {
                margarita(Offset(vidrio.left + vidrio.width * 0.11f, ca.y + gb), vidrio.width * 0.07f, fluor(MAGENTA, oscuro).copy(alpha = 0.85f), fluor(AMARILLO, oscuro))
                margarita(Offset(vidrio.right - vidrio.width * 0.11f, ca.y + gb), vidrio.width * 0.07f, fluor(NARANJA, oscuro).copy(alpha = 0.85f), fluor(AMARILLO, oscuro))
            }
        }
        // El marco de madera pintada de turquesa, con el montante.
        val pintura = t(mezcla(CASA_TURQUESA, Color(0xFF155E58), 0.35f), oscuro, 0.78f)
        val luzM = if (oscuro) FVIOLETA.copy(alpha = 0.12f) else luzMueble(0.45f)
        val marcoP = Path().apply {
            addRect(r)
            addRect(vidrio)
            fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
        }
        drawPath(marcoP, pintura)
        drawRect(pintura, Offset(vidrio.left, montante - 1.6f * u), Size(vidrio.width, 3.2f * u))
        drawLine(luzM, Offset(vidrio.left, montante - 1.6f * u), Offset(vidrio.right, montante - 1.6f * u), 0.7f * u)
        drawLine(pintura, Offset(vidrio.center.x, vidrio.top), Offset(vidrio.center.x, montante), 2.4f * u)
        // La luz en el canto izquierdo y arriba, la sombra a la derecha y abajo; los descascarados de la pintura.
        drawRect(luzM, r.topLeft, Size(r.width, 1f * u))
        drawRect(luzM, r.topLeft, Size(1f * u, r.height))
        drawRect(sombra(oscuro, 0.3f), Offset(vidrio.left, vidrio.top), Size(vidrio.width, 1.4f * u))
        drawRect(sombra(oscuro, 0.25f), Offset(r.right - 1.2f * u, r.top), Size(1.2f * u, r.height))
        for (k in 0 until 5) {
            val p = Offset(r.left + r.width * azar(621, k), if (k % 2 == 0) r.top + marco * 0.3f else r.bottom - marco * 0.3f)
            drawRect(t(PINO_LUZ, oscuro, 0.75f).copy(alpha = 0.45f), Offset(p.x, p.y - 0.4f * u), Size((2f + 4f * azar(622, k)) * u, 0.8f * u))
        }
    }

    // ---------------------------------------------------------------- la pared: los carteles y los discos

    /**
     * El cartel de concierto de la semana, inventado, dentro de [r]: «baile» en letras de burbuja amarillas con su contorno
     * violeta (los complementarios que vibran), que gotean, sobre una banda magenta; debajo, los rayos naranja y azul y la
     * flor que vibra; abajo, la banda violeta con los renglones de la fecha y la sala. Bajo la luz negra, todo en tinta
     * fluorescente, con su halo.
     */
    private fun DrawScope.cartel(r: Rect, oscuro: Boolean) {
        val u = density
        fun f(c: Color) = fluor(c, oscuro)
        // La sombra en la pared: la luz viene de la ventana, a la izquierda.
        if (!oscuro) drawRect(sombraMueble(0.18f), Offset(r.left + 4f * u, r.top + 4f * u), r.size)
        else resplandor(r.center, r.width * 1.05f, FMAGENTA, 0.6f)
        drawRect(if (oscuro) Color(0xFF1A1030) else BLANCO, r.topLeft, r.size)
        val m = 2.6f * u
        val img = Rect(r.left + m, r.top + m, r.right - m, r.bottom - m)
        clipRect(img.left, img.top, img.right, img.bottom) {
            // Los rayos, naranja y azul, desde el centro de la flor.
            val cf = Offset(img.center.x, img.top + img.height * 0.6f)
            drawRect(f(NARANJA), img.topLeft, img.size)
            val n = 18
            for (k in 0 until n step 2) {
                val a0 = 2f * PI.toFloat() * k / n; val a1 = 2f * PI.toFloat() * (k + 1) / n
                val l = img.height * 1.2f
                drawPath(Path().apply { moveTo(cf.x, cf.y); lineTo(cf.x + l * cos(a0), cf.y + l * sin(a0)); lineTo(cf.x + l * cos(a1), cf.y + l * sin(a1)); close() }, f(AZUL))
            }
            // Las ondas de la flor: tres aros que vibran entre los dos colores.
            for (k in 3 downTo 1) drawCircle(if (k % 2 == 1) f(VERDE) else f(MAGENTA), img.width * (0.26f + 0.06f * k), cf, style = Stroke(img.width * 0.03f))
            with(TrazosPsicodelia) { flor(cf, img.width * 0.27f, f(MAGENTA), f(VERDE), 8, 0.6f, 0.2f) }
            drawCircle(f(AMARILLO), img.width * 0.08f, cf)
            // La banda de arriba con «baile», en letras de burbuja que se derriten.
            val banda = img.top + img.height * 0.3f
            drawRect(f(MAGENTA), img.topLeft, Size(img.width, banda - img.top))
            drawPath(TrazosPsicodelia.onda(img.left, img.right, banda, img.height * 0.012f, img.width * 0.25f), f(MAGENTA), style = Stroke(img.height * 0.03f))
            val alto = img.height * 0.17f
            val ancho = anchoBurbuja("baile", alto)
            val esc = minOf(1f, img.width * 0.86f / ancho)
            val altoE = alto * esc
            val tr = letrasBurbuja("baile", img.center.x - anchoBurbuja("baile", altoE) / 2 + altoE * 0.05f, img.top + img.height * 0.24f, altoE, 0.06f)
            burbujas(tr, altoE * 0.2f, SolidColor(f(AMARILLO)), f(VIOLETA), img.height * 0.06f, 11, if (oscuro) 0.25f else 0.45f)
            // La banda de abajo: la fecha y la sala, en renglones.
            val pie = img.bottom - img.height * 0.14f
            drawRect(f(VIOLETA), Offset(img.left, pie), Size(img.width, img.bottom - pie))
            renglones(Rect(img.left + img.width * 0.14f, pie + img.height * 0.025f, img.right - img.width * 0.14f, img.bottom - img.height * 0.025f), f(AMARILLO), 2, 13)
        }
        // El papel un poco arrugado y la esquina de abajo levantada.
        pinceladas(r, if (oscuro) Color.Black else Color.White, 631, 8, u, 0.1f)
        val dob = r.width * 0.12f
        val esquina = Path().apply { moveTo(r.right, r.bottom - dob); lineTo(r.right - dob, r.bottom); lineTo(r.right - dob * 0.2f, r.bottom - dob * 0.2f); close() }
        drawPath(Path().apply { moveTo(r.right, r.bottom - dob); lineTo(r.right - dob, r.bottom); lineTo(r.right, r.bottom); close() }, t(PARED, oscuro))
        drawPath(esquina, t(mezcla(BLANCO, PARED_SOMBRA, 0.4f), oscuro, 0.7f))
        drawPath(esquina, sombra(oscuro, 0.2f), style = Stroke(0.5f * u))
    }

    /**
     * La hoja volante de «paz», dentro de [r]: el papel del periódico con las letras de burbuja en tinta de arcoíris (el
     * tintero partido), el símbolo de la paz de una línea y unos renglones; pegada con dos tiras de cinta.
     */
    private fun DrawScope.hojaPaz(r: Rect, oscuro: Boolean) {
        val u = density
        if (!oscuro) drawRect(sombraMueble(0.15f), Offset(r.left + 3f * u, r.top + 3.5f * u), r.size)
        drawRect(t(PAPEL, oscuro, 0.78f), r.topLeft, r.size)
        val arco = TrazosPsicodelia.arcoiris(Offset(r.left, r.top), Offset(r.right, r.bottom), 1f, oscuro)
        clipRect(r.left, r.top, r.right, r.bottom) {
            pinceladas(r, if (oscuro) Color.Black else PARED_SOMBRA, 641, 6, u, 0.1f)
            val alto = r.height * 0.5f
            val tr = letrasBurbuja("paz", r.left + r.width * 0.08f, r.top + r.height * 0.6f, alto, 0.05f)
            if (oscuro) drawPath(tr.first, FMAGENTA.copy(alpha = 0.18f), style = Stroke(alto * 0.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            burbujas(tr, alto * 0.2f, arco, if (oscuro) Color(0xFF2A1840) else TINTA, r.height * 0.08f, 23, if (oscuro) 0.15f else 0.35f)
            // La paz, a la derecha, en la misma tinta.
            val cp = Offset(r.right - r.width * 0.2f, r.top + r.height * 0.42f)
            val rp = r.height * 0.26f
            if (oscuro) mancha(cp, rp * 2f, LIMON.copy(alpha = 0.18f))
            with(TrazosPsicodelia) { paz(cp, rp, if (oscuro) LIMON else MAGENTA, rp * 0.2f) }
            renglones(Rect(r.left + r.width * 0.1f, r.bottom - r.height * 0.2f, r.right - r.width * 0.1f, r.bottom - r.height * 0.07f), t(TINTA, oscuro, 0.3f).copy(alpha = 0.6f), 2, 29, false)
        }
        cinta(Offset(r.left - r.width * 0.04f, r.top + r.height * 0.1f), Offset(r.left + r.width * 0.1f, r.top - r.height * 0.04f), r.height * 0.12f, oscuro)
        cinta(Offset(r.right - r.width * 0.1f, r.top - r.height * 0.05f), Offset(r.right + r.width * 0.04f, r.top + r.height * 0.1f), r.height * 0.12f, oscuro)
    }

    /** Un sencillo colgado de un clavo por su agujero grande, en [c]: el disco, la sombra en la pared y el clavo. */
    private fun DrawScope.discoColgado(c: Offset, r: Float, etiqueta: Color, oscuro: Boolean, giro: Float) {
        val u = density
        if (!oscuro) drawCircle(sombraMueble(0.2f), r, c + Offset(r * 0.14f, r * 0.2f))
        val et = fluor(etiqueta, oscuro)
        if (oscuro) resplandor(c, r * 1.6f, et, 0.55f)
        with(TrazosPsicodelia) { disco(c, r, et, giro) }
        if (oscuro) drawCircle(SALA.copy(alpha = 0.4f), r * 0.67f, c, style = Stroke(r * 0.6f))
        // El agujero grande del sencillo (sin adaptador) y el clavo en la pared.
        drawCircle(t(PARED, oscuro), r * 0.12f, c)
        drawCircle(sombra(oscuro, 0.25f), r * 0.12f, c, style = Stroke(0.5f * u))
        drawCircle(t(Color(0xFF6A6460), oscuro, 0.6f), 1.5f * u, c + Offset(0f, -r * 0.06f))
        drawCircle(brillo(oscuro, 0.6f), 0.6f * u, c + Offset(-0.5f * u, -r * 0.06f - 0.5f * u))
    }

    /** El tubo de luz negra, de [x0] a [x1] en [y]: la canaleta de metal y el tubo; de noche, encendido de violeta. */
    private fun DrawScope.tuboLuzNegra(x0: Float, x1: Float, y: Float, alto: Float, oscuro: Boolean) {
        val u = density
        if (!oscuro) drawRoundRect(sombraMueble(0.2f), Offset(x0 + 2f * u, y + 3f * u), Size(x1 - x0, alto), CornerRadius(alto / 2))
        drawRoundRect(Brush.verticalGradient(listOf(t(CROMO, oscuro, 0.75f), t(CROMO_OSC, oscuro, 0.75f)), y - alto * 0.35f, y + alto * 0.2f),
            Offset(x0, y - alto * 0.35f), Size(x1 - x0, alto * 0.55f), CornerRadius(1.2f * u))
        listOf(x0 + 2f * u, x1 - 6f * u).forEach { x -> drawRect(t(CROMO_OSC, oscuro, 0.7f), Offset(x, y - alto * 0.2f), Size(4f * u, alto * 1.05f)) }
        val tubo = Rect(x0 + 6f * u, y + alto * 0.2f, x1 - 6f * u, y + alto * 0.85f)
        if (oscuro) {
            resplandor(tubo.center, (x1 - x0) * 0.7f, FVIOLETA, 1.1f)
            drawRoundRect(FVIOLETA.copy(alpha = 0.35f), Offset(tubo.left - 2f * u, tubo.top - 2f * u), Size(tubo.width + 4f * u, tubo.height + 4f * u), CornerRadius(tubo.height))
            drawRoundRect(Color(0xFF8E6BF0), tubo.topLeft, tubo.size, CornerRadius(tubo.height / 2))
            drawRoundRect(Color(0xFFE6DAFF), Offset(tubo.left + 2f * u, tubo.center.y - tubo.height * 0.18f), Size(tubo.width - 4f * u, tubo.height * 0.3f), CornerRadius(tubo.height / 2))
        } else {
            drawRoundRect(Brush.verticalGradient(listOf(Color(0xFF6E5A8E), Color(0xFF3E2E5A)), tubo.top, tubo.bottom), tubo.topLeft, tubo.size, CornerRadius(tubo.height / 2))
            drawLine(Color.White.copy(alpha = 0.4f), Offset(tubo.left + 3f * u, tubo.top + tubo.height * 0.3f), Offset(tubo.right - 3f * u, tubo.top + tubo.height * 0.3f), 0.7f * u, StrokeCap.Round)
        }
    }

    // ---------------------------------------------------------------- lo que hay sobre el mostrador

    /** La sombra blanda que algo de [r] deja en la pared, corrida [d] a la derecha (la luz viene de la ventana). */
    private fun DrawScope.sombraPared(r: Rect, d: Float, redonda: Boolean = false) {
        for (k in 1..3) {
            val e = d * k / 3f
            val rr = Rect(r.left + e, r.top + e * 0.3f, r.right + e, r.bottom)
            if (redonda) drawOval(sombraMueble(0.05f), rr.topLeft, rr.size) else drawRoundRect(sombraMueble(0.05f), rr.topLeft, rr.size, CornerRadius(e))
        }
    }

    /**
     * La caja registradora, de pie en [pie], de [ancho] y [alto]: el cajón del dinero con su tirador, el teclado en pendiente
     * con sus filas de teclas redondas (una fila roja), la caja de arriba con su moldura y las banderitas de la cuenta que
     * asoman, y la manivela al costado. Cromada.
     */
    private fun DrawScope.registradora(pie: Offset, ancho: Float, alto: Float, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, 0.78f)
        val x0 = pie.x - ancho / 2; val x1 = pie.x + ancho / 2
        sombraContacto(Offset(pie.x + 6f * u, pie.y), ancho * 1.15f, 5f * u, if (oscuro) 0.5f else 0.32f)
        fun metal(a: Float, b: Float): Brush = Brush.horizontalGradient(listOf(c(mezcla(CROMO, Color.White, 0.5f)), c(CROMO), c(CROMO_OSC), c(mezcla(CROMO_OSC, Color.Black, 0.3f))), a, b)
        // El cajón del dinero.
        val cajon = Rect(x0, pie.y - alto * 0.24f, x1, pie.y)
        drawRect(metal(x0, x1), cajon.topLeft, cajon.size)
        drawRect(sombra(oscuro, 0.3f), Offset(cajon.left, cajon.top), Size(cajon.width, 1f * u))
        drawRect(c(Color(0xFF3A3236)), Offset(cajon.left + ancho * 0.08f, cajon.top + alto * 0.05f), Size(ancho * 0.84f, alto * 0.14f), style = Stroke(0.7f * u))
        drawRoundRect(c(Color(0xFF2E2A2C)), Offset(pie.x - ancho * 0.12f, cajon.top + alto * 0.1f), Size(ancho * 0.24f, alto * 0.035f), CornerRadius(1.5f * u))
        drawCircle(c(Color(0xFF2E2A2C)), 1.1f * u, Offset(x0 + ancho * 0.16f, cajon.top + alto * 0.12f))
        // El teclado en pendiente.
        val t0 = cajon.top; val t1 = pie.y - alto * 0.62f
        val teclado = Path().apply { moveTo(x0, t0); lineTo(x0 + ancho * 0.06f, t1); lineTo(x1 - ancho * 0.06f, t1); lineTo(x1, t0); close() }
        drawPath(teclado, Brush.verticalGradient(listOf(c(mezcla(CROMO, Color.White, 0.3f)), c(CROMO_OSC)), t1, t0))
        clipPath(teclado) {
            for (fila in 0 until 4) {
                val y = t1 + (t0 - t1) * (0.17f + 0.22f * fila)
                for (k in 0 until 6) {
                    val x = x0 + ancho * (0.15f + 0.14f * k + 0.01f * fila)
                    val r = (t0 - t1) * 0.075f
                    val roja = fila == 3
                    drawCircle(sombra(oscuro, 0.4f), r, Offset(x + 0.5f * u, y + 0.8f * u))
                    drawCircle(c(if (roja) Color(0xFFC6402E) else Color(0xFFF3EEE2)), r, Offset(x, y))
                    drawCircle(c(Color(0xFF2E2A2C)).copy(alpha = 0.6f), r, Offset(x, y), style = Stroke(0.5f * u))
                    drawCircle(Color.White.copy(alpha = if (oscuro) 0.1f else 0.6f), r * 0.35f, Offset(x - r * 0.3f, y - r * 0.3f))
                }
            }
        }
        // La caja de arriba, con su moldura y la ventanita de la cuenta.
        val cab = Rect(x0 + ancho * 0.08f, pie.y - alto * 0.86f, x1 - ancho * 0.08f, t1)
        drawRect(metal(cab.left, cab.right), cab.topLeft, cab.size)
        drawRect(c(CROMO_OSC), Offset(cab.left - ancho * 0.02f, cab.bottom - 1.6f * u), Size(cab.width + ancho * 0.04f, 2.4f * u))
        val ven = Rect(cab.left + cab.width * 0.18f, cab.top + cab.height * 0.22f, cab.right - cab.width * 0.18f, cab.bottom - cab.height * 0.3f)
        drawRect(c(Color(0xFFE9E2CF)), ven.topLeft, ven.size)
        // Los tambores de la cuenta, detrás del vidrio, y el vidrio con su brillo.
        for (k in 0 until 4) {
            val tx = ven.left + ven.width * (0.06f + 0.235f * k)
            drawRect(c(Color(0xFFD8CFB8)), Offset(tx, ven.top + 1f * u), Size(ven.width * 0.19f, ven.height - 2f * u))
            drawLine(c(Color(0xFF6A5E50)), Offset(tx + ven.width * 0.095f, ven.top + ven.height * 0.3f), Offset(tx + ven.width * 0.095f, ven.bottom - ven.height * 0.3f), 0.9f * u, StrokeCap.Round)
        }
        drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.05f else 0.35f), Color.Transparent), ven.top, ven.bottom), ven.topLeft, ven.size)
        drawRect(c(Color(0xFF6E7378)), ven.topLeft, ven.size, style = Stroke(0.8f * u))
        // La cresta de arriba: una moldura redonda con su roseta.
        val cresta = Path().apply {
            moveTo(cab.left + cab.width * 0.1f, cab.top)
            cubicTo(cab.left + cab.width * 0.2f, cab.top - alto * 0.07f, cab.right - cab.width * 0.2f, cab.top - alto * 0.07f, cab.right - cab.width * 0.1f, cab.top)
            close()
        }
        drawPath(cresta, metal(cab.left, cab.right))
        drawPath(cresta, c(CROMO_OSC), style = Stroke(0.6f * u))
        drawCircle(c(CROMO_OSC), alto * 0.022f, Offset(cab.center.x, cab.top - alto * 0.03f))
        drawCircle(brillo(oscuro, 0.7f), alto * 0.009f, Offset(cab.center.x - alto * 0.006f, cab.top - alto * 0.036f))
        // La moldura de la caja, una línea de puntos repujados.
        for (k in 0 until 9) drawCircle(c(CROMO_OSC).copy(alpha = 0.8f), 0.5f * u, Offset(cab.left + cab.width * (0.08f + 0.105f * k), cab.bottom - cab.height * 0.14f))
        // La manivela al costado derecho.
        val eje = Offset(x1 + 1f * u, pie.y - alto * 0.45f)
        drawCircle(c(CROMO_OSC), alto * 0.045f, eje)
        drawLine(c(CROMO), eje, eje + Offset(ancho * 0.1f, alto * 0.1f), 1.6f * u, StrokeCap.Round)
        drawRoundRect(c(Color(0xFF2E2A2C)), Offset(eje.x + ancho * 0.1f - 1.2f * u, eje.y + alto * 0.1f - 1f * u), Size(2.4f * u, alto * 0.08f), CornerRadius(1.2f * u))
        // El brillo del cromo a la izquierda; de noche, el reflejo lila del tubo.
        drawLine(brillo(oscuro, 0.7f), Offset(x0 + 1.2f * u, pie.y - 1f * u), Offset(x0 + 1.2f * u, cajon.top), 1f * u)
        drawLine(brillo(oscuro, 0.6f), Offset(cab.left + 1.2f * u, cab.top), Offset(cab.left + 1.2f * u, cab.bottom), 1f * u)
    }

    /**
     * La lámpara de cera, de pie en [pie] y de [alto] (otra silueta que la de la marca): el globo de vidrio sobre su pie
     * cónico de bronce con agujeritos. De día, apagada, con la cera magenta asentada al fondo del líquido; de noche,
     * encendida: las gotas de cera suben y echa su luz tibia.
     */
    private fun DrawScope.lamparaCera(pie: Offset, alto: Float, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, 0.75f)
        val an = alto * 0.5f
        val bronce = c(Color(0xFFB8893E))
        sombraContacto(Offset(pie.x + 4f * u, pie.y), an * 1.1f, 4f * u, if (oscuro) 0.4f else 0.3f)
        val pieP = Path().apply {
            moveTo(pie.x - an * 0.44f, pie.y); lineTo(pie.x + an * 0.44f, pie.y)
            lineTo(pie.x + an * 0.24f, pie.y - alto * 0.32f); lineTo(pie.x - an * 0.24f, pie.y - alto * 0.32f); close()
        }
        drawPath(pieP, Brush.horizontalGradient(listOf(mezcla(bronce, Color.White, 0.35f), bronce, mezcla(bronce, Color.Black, 0.45f)), pie.x - an * 0.44f, pie.x + an * 0.44f))
        for (i in -1..1) drawCircle(if (oscuro) FNARANJA else Color.Black.copy(alpha = 0.4f), 1.1f * u, Offset(pie.x + i * an * 0.15f, pie.y - alto * 0.13f))
        val gc = Offset(pie.x, pie.y - alto * 0.32f - an * 0.42f)
        val gr = an * 0.44f
        if (oscuro) resplandor(gc, alto * 1.3f, FNARANJA, 1.1f)
        val liquido = if (oscuro) Brush.radialGradient(listOf(Color(0xFFFFE0A0), Color(0xFFFFA24A), Color(0xFFB8501E)), Offset(gc.x, gc.y + gr * 0.5f), gr * 1.3f)
        else Brush.verticalGradient(listOf(Color(0xFFF2C886), Color(0xFFD9944A)), gc.y - gr, gc.y + gr)
        drawCircle(liquido, gr, gc)
        clipPath(Path().apply { addOval(Rect(gc, gr)) }) {
            if (oscuro) {
                drawPath(TrazosPsicodelia.gota(Offset(gc.x - gr * 0.22f, gc.y - gr * 0.38f), gr * 0.28f, 0.2f, 0.18f), FMAGENTA)
                drawPath(TrazosPsicodelia.gota(Offset(gc.x + gr * 0.3f, gc.y + gr * 0.08f), gr * 0.2f, 0.6f, 0.2f), FMAGENTA)
                drawPath(TrazosPsicodelia.gota(Offset(gc.x, gc.y + gr * 1.05f), gr * 0.62f, 0.1f, 0.08f), FMAGENTA)
            } else drawPath(TrazosPsicodelia.gota(Offset(gc.x, gc.y + gr * 1.1f), gr * 0.66f, 0.3f, 0.05f), Color(0xFFC23A7E))
            drawCircle(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.28f)), gc.x - gr, gc.x + gr), gr, gc)
        }
        drawCircle(Color.Black.copy(alpha = 0.25f), gr, gc, style = Stroke(0.6f * u))
        drawArc(Color.White.copy(alpha = if (oscuro) 0.3f else 0.6f), 200f, 70f, false, Offset(gc.x - gr * 0.75f, gc.y - gr * 0.75f), Size(gr * 1.5f, gr * 1.5f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        drawRoundRect(bronce, Offset(pie.x - an * 0.2f, pie.y - alto * 0.34f), Size(an * 0.4f, alto * 0.04f), CornerRadius(1f * u))
        val tope = Offset(gc.x, gc.y - gr)
        drawPath(Path().apply { moveTo(tope.x - an * 0.12f, tope.y + 1f * u); quadraticTo(tope.x, tope.y - alto * 0.08f, tope.x + an * 0.12f, tope.y + 1f * u); close() },
            Brush.horizontalGradient(listOf(mezcla(bronce, Color.White, 0.3f), mezcla(bronce, Color.Black, 0.35f)), tope.x - an * 0.12f, tope.x + an * 0.12f))
    }

    /** Un botón de solapa chico en [c]: el disco de [fondo] con su figura ([tipo]: la paz, una flor o franjas) y su brillo. */
    private fun DrawScope.botoncito(c: Offset, r: Float, fondo: Color, tipo: Int, oscuro: Boolean) {
        val f = fluor(fondo, oscuro)
        drawCircle(f, r, c)
        val otro = fluor(TrazosPsicodelia.complementario(fondo), oscuro)
        when (tipo % 3) {
            0 -> with(TrazosPsicodelia) { paz(c, r * 0.6f, otro, r * 0.16f) }
            1 -> with(TrazosPsicodelia) { margarita(c, r * 0.7f, otro, f) }
            else -> clipPath(Path().apply { addOval(Rect(c, r)) }) { for (k in -2..2) drawLine(otro, Offset(c.x - r, c.y + k * r * 0.4f - r * 0.4f), Offset(c.x + r, c.y + k * r * 0.4f + r * 0.4f), r * 0.18f) }
        }
        drawCircle(Color.Black.copy(alpha = 0.3f), r, c, style = Stroke(r * 0.08f))
        drawCircle(Color.White.copy(alpha = if (oscuro) 0.2f else 0.5f), r * 0.22f, c - Offset(r * 0.38f, r * 0.38f))
    }

    /** El frasco de vidrio con los botones de solapa, de pie en [pie]: los botones amontonados, la tapa de lata y el brillo. */
    private fun DrawScope.frascoBotones(pie: Offset, alto: Float, oscuro: Boolean) {
        val u = density
        val an = alto * 0.36f
        val fr = Rect(pie.x - an, pie.y - alto * 0.86f, pie.x + an, pie.y)
        sombraContacto(Offset(pie.x + 3f * u, pie.y), an * 2.3f, 4f * u, if (oscuro) 0.4f else 0.3f)
        drawRoundRect(t(Color(0xFFCFE3E0), oscuro, 0.75f).copy(alpha = 0.5f), fr.topLeft, fr.size, CornerRadius(an * 0.4f))
        clipPath(Path().apply { addRoundRect(RoundRect(fr, CornerRadius(an * 0.4f))) }) {
            val colores = listOf(MAGENTA, NARANJA, AMARILLO, VERDE, AZUL, VIOLETA)
            for (k in 0 until 11) {
                val fila = k / 4; val col = k % 4
                val r = an * 0.3f
                val c = Offset(fr.left + r + (fr.width - 2 * r) * (col / 3f) + (if (fila % 2 == 1) r * 0.5f else 0f) + (azar(651, k) - 0.5f) * r * 0.4f,
                    fr.bottom - r * 1.05f - fila * r * 1.55f)
                if (c.y - r < fr.top + alto * 0.2f) continue
                botoncito(c, r, colores[k % 6], k, oscuro)
            }
        }
        drawRoundRect(Color.Black.copy(alpha = 0.25f), fr.topLeft, fr.size, CornerRadius(an * 0.4f), style = Stroke(0.6f * u))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.15f else 0.6f), Offset(fr.left + 2.5f * u, fr.top + alto * 0.2f), Offset(fr.left + 2.5f * u, fr.bottom - alto * 0.12f), 1.1f * u, StrokeCap.Round)
        val tapa = Rect(fr.left - 1f * u, fr.top - alto * 0.12f, fr.right + 1f * u, fr.top + alto * 0.04f)
        drawRoundRect(Brush.horizontalGradient(listOf(t(Color(0xFFE2DED6), oscuro, 0.75f), t(Color(0xFF8E8A84), oscuro, 0.75f)), tapa.left, tapa.right), tapa.topLeft, tapa.size, CornerRadius(1.5f * u))
        for (k in 1..5) drawLine(Color.Black.copy(alpha = 0.2f), Offset(tapa.left + tapa.width * k / 6f, tapa.top), Offset(tapa.left + tapa.width * k / 6f, tapa.bottom), 0.4f * u)
    }

    /**
     * El sencillo de la semana en su atril de madera, de pie en [pie]: la funda ilustrada (un sol que sale entre las colinas,
     * con los rayos que vibran) y el disco que asoma por arriba, con su etiqueta.
     */
    private fun DrawScope.atril(pie: Offset, alto: Float, oscuro: Boolean) {
        val u = density
        val lado = alto * 0.72f
        sombraContacto(Offset(pie.x + 5f * u, pie.y), lado * 1.1f, 4f * u, if (oscuro) 0.4f else 0.3f)
        // Las patas del atril.
        val madera = t(PINO_OSC, oscuro, 0.75f)
        drawLine(madera, Offset(pie.x - lado * 0.36f, pie.y), Offset(pie.x - lado * 0.1f, pie.y - alto * 0.98f), 1.6f * u, StrokeCap.Round)
        drawLine(madera, Offset(pie.x + lado * 0.36f, pie.y), Offset(pie.x + lado * 0.1f, pie.y - alto * 0.98f), 1.6f * u, StrokeCap.Round)
        drawLine(t(NUDO, oscuro, 0.75f), Offset(pie.x + lado * 0.05f, pie.y), Offset(pie.x + lado * 0.05f, pie.y - alto * 0.7f), 1.2f * u, StrokeCap.Round)
        // El disco que asoma de la funda, arriba.
        val f = Rect(pie.x - lado / 2, pie.y - alto * 0.9f, pie.x + lado / 2, pie.y - alto * 0.9f + lado)
        val cd = Offset(f.center.x + lado * 0.08f, f.top + lado * 0.1f)
        with(TrazosPsicodelia) { disco(cd, lado * 0.42f, fluor(AMARILLO, oscuro), 0.15f) }
        if (oscuro) drawCircle(SALA.copy(alpha = 0.4f), lado * 0.42f, cd)
        // La funda ilustrada.
        drawRect(sombra(oscuro, 0.3f), f.topLeft + Offset(1.5f * u, 1.5f * u), f.size)
        clipRect(f.left, f.top, f.right, f.bottom) {
            drawRect(fluor(AZUL, oscuro), f.topLeft, f.size)
            val sol = Offset(f.center.x, f.top + lado * 0.62f)
            for (k in 0 until 12 step 2) {
                val a0 = PI.toFloat() + PI.toFloat() * k / 12; val a1 = PI.toFloat() + PI.toFloat() * (k + 1) / 12
                drawPath(Path().apply { moveTo(sol.x, sol.y); lineTo(sol.x + lado * cos(a0), sol.y + lado * sin(a0)); lineTo(sol.x + lado * cos(a1), sol.y + lado * sin(a1)); close() }, fluor(NARANJA, oscuro))
            }
            drawCircle(fluor(AMARILLO, oscuro), lado * 0.2f, sol)
            drawCircle(fluor(MAGENTA, oscuro), lado * 0.2f, sol, style = Stroke(lado * 0.035f))
            drawPath(Path().apply { moveTo(f.left, f.bottom); lineTo(f.left, sol.y); quadraticTo(f.left + lado * 0.3f, sol.y - lado * 0.14f, f.center.x, sol.y + lado * 0.05f); quadraticTo(f.right - lado * 0.2f, sol.y + lado * 0.16f, f.right, sol.y - lado * 0.02f); lineTo(f.right, f.bottom); close() }, fluor(VERDE, oscuro))
            drawPath(TrazosPsicodelia.onda(f.left, f.right, f.bottom - lado * 0.14f, lado * 0.03f, lado * 0.33f), fluor(MAGENTA, oscuro), style = Stroke(lado * 0.05f))
        }
        drawRect(Color.Black.copy(alpha = 0.25f), f.topLeft, f.size, style = Stroke(0.5f * u))
        drawLine(brillo(oscuro, 0.5f), f.topLeft, Offset(f.left, f.bottom), 0.8f * u)
        // El travesaño del atril donde se apoya.
        drawRoundRect(t(PINO, oscuro, 0.75f), Offset(f.left - lado * 0.08f, f.bottom - 1f * u), Size(lado * 1.16f, 2.6f * u), CornerRadius(1f * u))
    }

    /**
     * El tocadiscos de maleta en [r] (con la tapa abierta): la caja forrada de naranja y crema con su rejilla, el plato en
     * perspectiva con un sencillo y, en el eje alto del cambiador, tres sencillos que esperan su turno, sostenidos por el
     * brazo de arriba; el brazo de la aguja y su perilla, y el piloto. En la tapa, una calcomanía de flor.
     */
    private fun DrawScope.tocadiscos(r: Rect, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, 0.78f)
        val forro = c(Color(0xFFD35A2E))
        val crema = c(Color(0xFFEFE4CC))
        val cajaAlto = r.height * 0.27f
        val caja = Rect(r.left, r.bottom - cajaAlto, r.right, r.bottom)
        sombraContacto(Offset(r.center.x + 8f * u, r.bottom), r.width * 1.05f, 6f * u, if (oscuro) 0.5f else 0.32f)
        // La tapa abierta, detrás, con su forro y la calcomanía.
        val tapa = Rect(r.left + r.width * 0.04f, r.top, r.right - r.width * 0.04f, caja.top + cajaAlto * 0.25f)
        if (!oscuro) drawRoundRect(sombraMueble(0.2f), tapa.topLeft + Offset(4f * u, 3f * u), tapa.size, CornerRadius(4f * u))
        drawRoundRect(forro, tapa.topLeft, tapa.size, CornerRadius(4f * u))
        val dentro = tapa.deflate(4f * u)
        drawRoundRect(crema, dentro.topLeft, dentro.size, CornerRadius(2.5f * u))
        clipRect(dentro.left, dentro.top, dentro.right, dentro.bottom) {
            pinceladas(dentro, if (oscuro) Color.Black else PARED_SOMBRA, 661, 10, u, 0.12f)
            drawRect(Brush.verticalGradient(listOf(sombra(oscuro, 0.18f), Color.Transparent), dentro.top, dentro.top + dentro.height * 0.4f), dentro.topLeft, Size(dentro.width, dentro.height * 0.4f))
        }
        val cc = Offset(dentro.left + dentro.width * 0.24f, dentro.top + dentro.height * 0.38f)
        val rc = dentro.height * 0.22f
        if (oscuro) resplandor(cc, rc * 2.4f, FMAGENTA, 0.6f)
        drawCircle(fluor(AMARILLO, oscuro), rc * 1.15f, cc)
        with(TrazosPsicodelia) { flor(cc, rc, fluor(MAGENTA, oscuro), fluor(VERDE, oscuro), 6, 0.5f, 0.3f) }
        // Las bisagras.
        listOf(tapa.left + tapa.width * 0.2f, tapa.right - tapa.width * 0.25f).forEach { x -> drawRect(c(CROMO_OSC), Offset(x, tapa.bottom - 3f * u), Size(tapa.width * 0.05f, 4f * u)) }
        // La caja, de dos colores, con su ribete y la rejilla del parlante.
        drawRoundRect(crema, caja.topLeft, caja.size, CornerRadius(3f * u))
        drawRect(forro, Offset(caja.left, caja.top + cajaAlto * 0.5f), Size(caja.width, cajaAlto * 0.5f - 2f * u))
        drawRoundRect(forro, Offset(caja.left, caja.bottom - cajaAlto * 0.3f), Size(caja.width, cajaAlto * 0.3f), CornerRadius(3f * u))
        drawRoundRect(Color.Black.copy(alpha = 0.3f), caja.topLeft, caja.size, CornerRadius(3f * u), style = Stroke(0.7f * u))
        drawLine(c(Color(0xFF9A8E78)), Offset(caja.left, caja.top + cajaAlto * 0.5f), Offset(caja.right, caja.top + cajaAlto * 0.5f), 1.2f * u)
        val rej = Rect(caja.left + caja.width * 0.08f, caja.top + cajaAlto * 0.58f, caja.left + caja.width * 0.42f, caja.bottom - cajaAlto * 0.12f)
        drawRoundRect(c(Color(0xFF3A2A26)), rej.topLeft, rej.size, CornerRadius(rej.height / 2))
        var xr = rej.left + 3f * u
        while (xr < rej.right - 2f * u) { drawLine(c(Color(0xFFB8A27E)), Offset(xr, rej.top + 1f * u), Offset(xr, rej.bottom - 1f * u), 0.7f * u); xr += 2.4f * u }
        // Las perillas y el piloto.
        val pk = Offset(caja.right - caja.width * 0.12f, caja.top + cajaAlto * 0.72f)
        listOf(pk, pk - Offset(caja.width * 0.12f, 0f)).forEach { k ->
            drawCircle(c(Color(0xFF2E2A2C)), cajaAlto * 0.13f, k)
            drawLine(crema, k, k + Offset(0f, -cajaAlto * 0.1f), 0.7f * u)
        }
        val piloto = Offset(caja.right - caja.width * 0.36f, caja.top + cajaAlto * 0.72f)
        if (oscuro) { resplandor(piloto, 10f * u, Color(0xFFFF6A3A), 0.9f); drawCircle(Color(0xFFFF6A3A), 1.4f * u, piloto) }
        else drawCircle(c(Color(0xFF8A3A2A)), 1.3f * u, piloto)
        // El plato en perspectiva, con el sencillo que suena.
        val pc = Offset(caja.left + caja.width * 0.42f, caja.top + cajaAlto * 0.1f)
        val rx = caja.width * 0.27f; val ry = cajaAlto * 0.34f
        drawOval(c(Color(0xFF8A8478)), Offset(pc.x - rx * 1.06f, pc.y - ry * 1.06f + 1.6f * u), Size(rx * 2.12f, ry * 2.12f))
        fun sencilloPlano(cy: Float, et: Color, k: Float = 1f) {
            drawOval(if (oscuro) VINILO else c(VINILO), Offset(pc.x - rx * k, cy - ry * k), Size(rx * 2 * k, ry * 2 * k))
            drawOval(Color.White.copy(alpha = 0.07f), Offset(pc.x - rx * k * 0.85f, cy - ry * k * 0.85f), Size(rx * 1.7f * k, ry * 1.7f * k), style = Stroke(0.4f * u))
            val e = fluor(et, oscuro).let { if (oscuro) it else c(it) }
            if (oscuro) drawOval(e.copy(alpha = 0.3f), Offset(pc.x - rx * 0.6f, cy - ry * 0.6f), Size(rx * 1.2f, ry * 1.2f))
            drawOval(e, Offset(pc.x - rx * 0.42f, cy - ry * 0.42f), Size(rx * 0.84f, ry * 0.84f))
            drawOval(c(mezcla(CROMO, Color.White, 0.3f)), Offset(pc.x - rx * 0.14f, cy - ry * 0.14f), Size(rx * 0.28f, ry * 0.28f))
        }
        sencilloPlano(pc.y, MAGENTA)
        drawArc(Color.White.copy(alpha = if (oscuro) 0.08f else 0.2f), 200f, 40f, true, Offset(pc.x - rx * 0.95f, pc.y - ry * 0.95f), Size(rx * 1.9f, ry * 1.9f))
        // El eje alto con tres sencillos esperando, y el brazo de arriba que los sostiene.
        val alturaEje = r.height * 0.3f
        drawLine(c(CROMO), pc, pc - Offset(0f, alturaEje), 1.6f * u)
        listOf(NARANJA, VERDE, AZUL).forEachIndexed { k, et ->
            val cy = pc.y - alturaEje * 0.72f - k * 2.4f * u
            drawOval(sombra(oscuro, 0.3f), Offset(pc.x - rx, cy - ry + 1.5f * u), Size(rx * 2, ry * 2))
            sencilloPlano(cy, et)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.18f), Offset(pc.x - rx * 0.9f, cy + ry * 0.4f), Offset(pc.x - rx * 0.4f, cy + ry * 0.9f), 0.6f * u)
        }
        val arriba = pc.y - alturaEje * 0.72f - 3f * 2.4f * u
        drawLine(c(CROMO), Offset(pc.x, arriba), Offset(pc.x, pc.y - alturaEje), 1.6f * u)
        // El brazo de arriba, que baja desde su poste y se apoya sobre la pila.
        val bp = Offset(caja.left + caja.width * 0.06f, caja.top + cajaAlto * 0.05f)
        drawLine(c(CROMO_OSC), bp, Offset(bp.x, arriba - 2f * u), 2.2f * u, StrokeCap.Round)
        drawLine(c(CROMO), Offset(bp.x, arriba - 2f * u), Offset(pc.x - rx * 0.25f, arriba + 1f * u), 1.6f * u, StrokeCap.Round)
        drawCircle(c(Color(0xFF2E2A2C)), 1.6f * u, Offset(pc.x - rx * 0.25f, arriba + 1f * u))
        // El brazo de la aguja, con su cápsula sobre el surco.
        val eje = Offset(caja.right - caja.width * 0.14f, caja.top + cajaAlto * 0.12f)
        drawCircle(c(Color(0xFF6E6A64)), 3f * u, eje)
        val punta = Offset(pc.x + rx * 0.62f, pc.y + ry * 0.3f)
        drawLine(c(CROMO), eje, Offset(eje.x - rx * 0.1f, eje.y + ry * 0.55f), 1.5f * u, StrokeCap.Round)
        drawLine(c(CROMO), Offset(eje.x - rx * 0.1f, eje.y + ry * 0.55f), punta, 1.5f * u, StrokeCap.Round)
        drawRect(c(Color(0xFF2E2A2C)), Offset(punta.x - 3f * u, punta.y - 1.6f * u), Size(4.4f * u, 3.2f * u))
        drawLine(brillo(oscuro, 0.6f), Offset(tapa.left + 1f * u, tapa.top + 4f * u), Offset(tapa.left + 1f * u, tapa.bottom - 4f * u), 1f * u)
    }

    // ---------------------------------------------------------------- el piso

    /** La caja de cartón con discos de larga duración, de pie en [pie]: los discos asoman y uno, apoyado al frente, enseña su portada. */
    private fun DrawScope.cajaLargos(pie: Offset, ancho: Float, alto: Float, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, 0.8f)
        sombraContacto(Offset(pie.x + 6f * u, pie.y), ancho * 1.2f, 5f * u, if (oscuro) 0.55f else 0.35f)
        val caja = Rect(pie.x - ancho / 2, pie.y - alto * 0.6f, pie.x + ancho / 2, pie.y)
        // Los discos dentro, de canto, con sus fundas de colores.
        val portadas = listOf(Color(0xFF3E6E9E), Color(0xFFE9D9B8), MAGENTA, Color(0xFF2E7344), NARANJA, Color(0xFF6A4A8A))
        for (k in portadas.indices) {
            val x = caja.left + ancho * (0.06f + 0.15f * k)
            val top = pie.y - alto * (0.92f - 0.05f * azar(671, k))
            rotate(-6f + 3f * k, Offset(x, caja.top)) {
                drawRect(fluor(portadas[k], oscuro).let { if (oscuro) mezcla(it, SALA, 0.5f) else it }, Offset(x, top), Size(ancho * 0.2f, caja.top - top + 4f * u))
                drawRect(Color.Black.copy(alpha = 0.2f), Offset(x, top), Size(ancho * 0.2f, caja.top - top + 4f * u), style = Stroke(0.5f * u))
            }
        }
        drawRect(Brush.horizontalGradient(listOf(c(mezcla(CARTON, Color.White, 0.15f)), c(CARTON), c(mezcla(CARTON, Color.Black, 0.2f))), caja.left, caja.right), caja.topLeft, caja.size)
        drawLine(c(mezcla(CARTON, Color.Black, 0.3f)), Offset(caja.left, caja.top + 1f * u), Offset(caja.right, caja.top + 1f * u), 1f * u)
        // La solapa doblada de la caja.
        drawPath(Path().apply { moveTo(caja.left, caja.top); lineTo(caja.left - ancho * 0.1f, caja.top - alto * 0.18f); lineTo(caja.left + ancho * 0.3f, caja.top - alto * 0.12f); lineTo(caja.left + ancho * 0.3f, caja.top); close() },
            c(mezcla(CARTON, Color.Black, 0.12f)))
        // El disco apoyado al frente: una portada de ondas en arcoíris.
        val pr = Rect(caja.left + ancho * 0.28f, pie.y - alto * 0.66f, caja.left + ancho * 0.28f + alto * 0.66f, pie.y)
        rotate(-5f, Offset(pr.left, pr.bottom)) {
            drawRect(sombra(oscuro, 0.3f), pr.topLeft + Offset(2f * u, 2f * u), pr.size)
            drawRect(c(Color(0xFF241A35)), pr.topLeft, pr.size)
            clipRect(pr.left, pr.top, pr.right, pr.bottom) {
                val cols = if (oscuro) listOf(FMAGENTA, FNARANJA, LIMON, FAZUL, FVIOLETA) else listOf(MAGENTA, NARANJA, AMARILLO, VERDE, AZUL)
                cols.forEachIndexed { k, col ->
                    drawPath(TrazosPsicodelia.onda(pr.left, pr.right, pr.top + pr.height * (0.3f + 0.1f * k), pr.height * 0.05f, pr.width * 0.5f, k * 0.08f), col, style = Stroke(pr.height * 0.07f))
                }
            }
            drawRect(Color.Black.copy(alpha = 0.25f), pr.topLeft, pr.size, style = Stroke(0.5f * u))
        }
    }

    /**
     * La maceta de barro pintada a mano con el símbolo de la paz, de pie en [pie]: cuatro margaritas de tallo largo, con sus
     * hojas redondas. Bajo la luz negra, los pétalos blancos se encienden de lila.
     */
    private fun DrawScope.maceta(pie: Offset, alto: Float, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, 0.8f)
        val an = alto * 0.26f
        val altoM = alto * 0.36f
        sombraContacto(Offset(pie.x + 5f * u, pie.y), an * 2.6f, 5f * u, if (oscuro) 0.55f else 0.35f)
        val boca = pie.y - altoM
        val tallo = fluor(VERDE, oscuro).let { if (oscuro) mezcla(it, SALA, 0.45f) else mezcla(it, Color.Black, 0.25f) }
        // Los tallos y las hojas, redondas.
        val flores = listOf(Offset(-0.75f, 0.78f), Offset(-0.15f, 1f), Offset(0.45f, 0.86f), Offset(0.95f, 0.66f))
        flores.forEachIndexed { k, o ->
            val cf = Offset(pie.x + o.x * an, pie.y - alto * o.y)
            val desde = Offset(pie.x + (k - 1.5f) * an * 0.2f, boca + 2f * u)
            drawPath(Path().apply { moveTo(desde.x, desde.y); quadraticTo(desde.x + (cf.x - desde.x) * 0.2f, (desde.y + cf.y) / 2, cf.x, cf.y) }, tallo, style = Stroke(1.3f * u, cap = StrokeCap.Round))
            if (k == 1 || k == 2) {
                val y = (desde.y + cf.y) / 2 + alto * 0.08f
                val s = if (k == 1) -1f else 1f
                val hx = desde.x + (cf.x - desde.x) * 0.45f
                drawPath(Path().apply { moveTo(hx, y); quadraticTo(hx + s * an * 0.35f, y - an * 0.35f, hx + s * an * 0.7f, y - an * 0.15f); quadraticTo(hx + s * an * 0.35f, y + an * 0.05f, hx, y); close() }, tallo)
            }
        }
        flores.forEachIndexed { k, o ->
            val cf = Offset(pie.x + o.x * an, pie.y - alto * o.y)
            val rf = an * (0.42f + 0.06f * (k % 2))
            if (oscuro) resplandor(cf, rf * 2.2f, FVIOLETA, 0.5f)
            with(TrazosPsicodelia) { margarita(cf, rf, if (oscuro) CARTULINA_NOCHE else BLANCO, fluor(AMARILLO, oscuro), 10, k * 0.4f) }
            drawCircle(sombra(oscuro, 0.2f), rf, cf, style = Stroke(0.4f * u))
        }
        // La maceta: el cuerpo de barro, el borde y la paz pintada.
        val barro = Color(0xFFC2673E)
        val cuerpo = Path().apply { moveTo(pie.x - an * 0.78f, pie.y); lineTo(pie.x - an, boca + altoM * 0.22f); lineTo(pie.x + an, boca + altoM * 0.22f); lineTo(pie.x + an * 0.78f, pie.y); close() }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(c(mezcla(barro, Color.White, 0.2f)), c(barro), c(mezcla(barro, Color.Black, 0.35f))), pie.x - an, pie.x + an))
        drawRoundRect(Brush.horizontalGradient(listOf(c(mezcla(barro, Color.White, 0.3f)), c(barro), c(mezcla(barro, Color.Black, 0.3f))), pie.x - an * 1.1f, pie.x + an * 1.1f),
            Offset(pie.x - an * 1.1f, boca), Size(an * 2.2f, altoM * 0.24f), CornerRadius(1.2f * u))
        drawOval(c(Color(0xFF4A2E1E)), Offset(pie.x - an, boca - 1f * u), Size(an * 2f, 2.4f * u))
        val cp = Offset(pie.x, pie.y - altoM * 0.4f)
        if (oscuro) resplandor(cp, an * 0.8f, LIMON, 0.6f)
        with(TrazosPsicodelia) { paz(cp, altoM * 0.24f, fluor(AMARILLO, oscuro), altoM * 0.05f) }
    }

    // ---------------------------------------------------------------- el mueble entero

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width; val alto = size.height
        val ch = d.corona.height
        val b = d.base; val hb = b.height
        val tope = ch * TOPE; val pie = ch * PIE; val canto = ch * CANTO; val frente = ch * FRENTE
        val zocalo = b.top + hb * ZOCALO
        val piso = b.top + hb * PISO
        // El tubo de luz negra: de noche, la luz de la tienda.
        val tubo = Offset(w * 0.69f, ch * 0.035f)

        // ---- la pared de la tienda
        if (!oscuro) {
            drawRect(Brush.horizontalGradient(listOf(mezcla(PARED, Color.White, 0.25f), PARED, mezcla(PARED, PARED_SOMBRA, 0.55f)), 0f, w), Offset.Zero, Size(w, tope))
            pinceladas(Rect(0f, 0f, w, tope), PARED_SOMBRA, 701, 30, u, 0.1f)
            pinceladas(Rect(0f, 0f, w, tope), Color.White, 702, 16, u, 0.1f)
        } else {
            drawRect(Brush.radialGradient(listOf(mezcla(PARED, SALA, 0.62f), mezcla(PARED, SALA, 0.78f), mezcla(PARED, SALA, 0.9f)), tubo, w * 0.95f), Offset.Zero, Size(w, tope))
            pinceladas(Rect(0f, 0f, w, tope), Color.Black, 701, 26, u, 0.12f)
        }
        // La ventana a la calle, a la izquierda: de ahí viene la luz del día.
        val vr = Rect(w * 0.035f, ch * 0.075f, w * 0.37f, tope - 2f * u)
        if (!oscuro) mancha(Offset(vr.center.x + w * 0.1f, vr.center.y), w * 0.45f, luzMueble(0.3f))
        ventana(vr, oscuro)
        // El tubo de luz negra, arriba.
        tuboLuzNegra(w * 0.42f, w * 0.965f, ch * 0.035f, ch * 0.03f, oscuro)
        // El cartel del baile, clavado con chinchetas, un poco torcido.
        val cr = Rect(w * 0.405f, ch * 0.105f, w * 0.625f, ch * 0.53f)
        rotate(-2.2f, cr.center) {
            cartel(cr, oscuro)
            chincheta(Offset(cr.left + 3.5f * u, cr.top + 3.5f * u), 2.2f * u, AMARILLO, oscuro)
            chincheta(Offset(cr.right - 3.5f * u, cr.top + 3.5f * u), 2.2f * u, VERDE, oscuro)
            chincheta(Offset(cr.left + 3.5f * u, cr.bottom - 3.5f * u), 2.2f * u, AZUL, oscuro)
        }
        // Los tres sencillos colgados de un clavo, y la hoja volante de «paz».
        val rd = w * 0.044f
        listOf(Triple(0.705f, MAGENTA, 0.1f), Triple(0.815f, NARANJA, 0.45f), Triple(0.922f, VERDE, 0.8f)).forEachIndexed { k, (fx, et, g) ->
            discoColgado(Offset(w * fx, ch * (0.17f + 0.025f * (k % 2))), rd, et, oscuro, g)
        }
        val hp = Rect(w * 0.665f, ch * 0.3f, w * 0.955f, ch * 0.48f)
        rotate(1.5f, hp.center) { hojaPaz(hp, oscuro) }

        // ---- la cubierta del mostrador: el tablero visto un poco desde arriba y su canto
        val tablero = Rect(0f, tope, w, canto)
        drawRect(Brush.verticalGradient(listOf(t(mezcla(CUBIERTA, Color.Black, 0.12f), oscuro), t(mezcla(CUBIERTA, PINO_LUZ, 0.35f), oscuro)), tope, canto), tablero.topLeft, tablero.size)
        clipRect(tablero.left, tablero.top, tablero.right, tablero.bottom) {
            vetas(tablero, t(NUDO, oscuro, 0.7f).copy(alpha = if (oscuro) 0.35f else 0.25f), 711, densidad = 0.8f, grosor = 0.7f, ondulacion = 0.4f)
            if (!oscuro) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.3f), Color.Transparent), 0f, w * 0.5f), tablero.topLeft, Size(w * 0.5f, tablero.height))
        }
        drawRect(sombra(oscuro, 0.3f), Offset(0f, tope), Size(w, 1.2f * u))
        // Lo de encima: la caja registradora, la lámpara de cera, el frasco de botones, el atril y el tocadiscos. De día,
        // cada cosa echa su sombra en la pared, a la derecha.
        if (!oscuro) {
            sombraPared(Rect(w * 0.055f, pie - ch * 0.25f, w * 0.255f, tope), 7f * u)
            sombraPared(Rect(w * 0.285f, pie - ch * 0.26f, w * 0.345f, tope), 6f * u, redonda = true)
            sombraPared(Rect(w * 0.6f, pie - ch * 0.33f, w * 0.95f, tope), 8f * u)
        }
        registradora(Offset(w * 0.155f, pie), w * 0.2f, ch * 0.28f, oscuro)
        lamparaCera(Offset(w * 0.315f, pie), ch * 0.28f, oscuro)
        frascoBotones(Offset(w * 0.42f, pie), ch * 0.13f, oscuro)
        atril(Offset(w * 0.515f, pie), ch * 0.17f, oscuro)
        tocadiscos(Rect(w * 0.585f, pie - ch * 0.33f, w * 0.965f, pie), oscuro)
        // El canto de la cubierta, de frente.
        val cantoR = Rect(0f, canto, w, frente)
        pino(cantoR, t(CUBIERTA, oscuro), 712, oscuro)
        drawLine(brillo(oscuro, 0.7f), Offset(0f, canto + 0.6f * u), Offset(w, canto + 0.6f * u), 1f * u)
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombra(oscuro, 0.35f)), canto, frente), cantoR.topLeft, cantoR.size)

        // ---- el cuerpo del mostrador, pintado a mano de violeta
        val cuerpo = Rect(0f, frente, w, zocalo)
        pintado(cuerpo, t(MOSTRADOR, oscuro, 0.72f), 720, oscuro)
        if (!oscuro) drawRect(Brush.horizontalGradient(listOf(MOSTRADOR_LUZ.copy(alpha = 0.45f), Color.Transparent, MOSTRADOR_OSC.copy(alpha = 0.4f)), 0f, w), cuerpo.topLeft, cuerpo.size)
        drawRect(Brush.verticalGradient(listOf(sombra(oscuro, 0.5f), Color.Transparent), frente, frente + 7f * u), Offset(0f, frente), Size(w, 7f * u))
        // El hueco de cada caja: su sombra alrededor y el canto de la tabla de abajo, a la luz.
        d.huecos.forEach { h ->
            drawRect(sombra(oscuro, if (oscuro) 0.8f else 0.55f), Offset(h.left - 1.6f * u, h.top - 1.6f * u), Size(h.width + 3.2f * u, h.height + 3.2f * u))
            drawLine(brillo(oscuro, 0.4f), Offset(h.left - 1.6f * u, h.bottom + 2f * u), Offset(h.right + 1.6f * u, h.bottom + 2f * u), 0.8f * u)
        }
        val x0 = d.huecos.first().left
        val x1 = d.huecos.last().right
        val pintura = 1.6f * u
        // Las margaritas pintadas en los costados, con su tallo que se enrosca.
        listOf(Rect(0f, frente, x0 - 2f * u, zocalo) to 1f, Rect(x1 + 2f * u, frente, w, zocalo) to -1f).forEachIndexed { i, (pr, s) ->
            val cx = pr.center.x
            val tallo = Path().apply {
                moveTo(cx, pr.bottom - 2f * u)
                var y = pr.bottom - 2f * u; var k = 0
                while (y > pr.top + 10f * u) {
                    val y2 = y - pr.width * 2.2f
                    cubicTo(cx + s * pr.width * 0.35f, y - pr.width * 0.6f, cx - s * pr.width * 0.35f, y2 + pr.width * 0.6f, cx, y2)
                    y = y2; k++
                }
            }
            clipRect(pr.left, pr.top + 4f * u, pr.right, pr.bottom) {
                if (oscuro) drawPath(tallo, LIMON.copy(alpha = 0.2f), style = Stroke(pintura * 3f))
                drawPath(tallo, fluor(VERDE, oscuro), style = Stroke(pintura, cap = StrokeCap.Round))
            }
            var y = pr.top + pr.width * 1.4f; var k = 0
            while (y < pr.bottom - pr.width) {
                val c = Offset(cx + (if (k % 2 == 0) -1f else 1f) * s * pr.width * 0.06f, y)
                val r = pr.width * 0.36f
                // Las hojas, redondas.
                val hoja = Path().apply { addOval(Rect(c + Offset(s * r * 0.9f, r * 1.3f) - Offset(r * 0.45f, r * 0.22f), Size(r * 0.9f, r * 0.44f))) }
                drawPath(hoja, fluor(VERDE, oscuro))
                if (oscuro) resplandor(c, r * 2.2f, LIMON, 0.6f)
                with(TrazosPsicodelia) { margarita(c, r, fluor(AMARILLO, oscuro), fluor(NARANJA, oscuro), 8, k * 0.3f) }
                y += pr.width * 2.4f; k++
            }
        }
        // Una onda naranja pintada en cada travesaño entre filas, y una hilera de puntos en el montante del centro.
        for (i in 0 until d.filas.size - 1) {
            val y = (d.filas[i].bottom + d.filas[i + 1].top) / 2
            val o = TrazosPsicodelia.onda(x0, x1, y, d.separacion * 0.18f, d.separacion * 3f, i * 0.25f)
            if (oscuro) drawPath(o, FNARANJA.copy(alpha = 0.25f), style = Stroke(pintura * 3f))
            drawPath(o, fluor(NARANJA, oscuro), style = Stroke(pintura, cap = StrokeCap.Round))
        }
        if (cols > 1) for (j in 1 until cols) {
            val xm = (d.huecos[j - 1].right + d.huecos[j].left) / 2
            var y = d.filas.first().top + 6f * u
            while (y < d.filas.last().bottom - 4f * u) { drawCircle(fluor(MAGENTA, oscuro), 1.3f * u, Offset(xm, y)); y += 7f * u }
        }
        // El friso bajo el canto: una hilera de festones pintados de amarillo.
        val yf = (frente + d.filas.first().top) / 2
        if (d.filas.first().top - frente > 5f * u) {
            var xf = 4f * u; var k = 0
            while (xf < w - 4f * u) {
                drawArc(fluor(AMARILLO, oscuro), 0f, 180f, false, Offset(xf, yf - 3f * u), Size(7f * u, 5f * u), style = Stroke(pintura * 0.9f, cap = StrokeCap.Round))
                xf += 8f * u; k++
            }
        }

        // ---- la base: el zócalo pintado con la onda de arcoíris, el piso, el tapete y lo del piso
        val zb = Rect(0f, b.top, w, zocalo)
        val cy = b.top + hb * 0.24f
        val bandas = if (oscuro) listOf(FMAGENTA, FNARANJA, LIMON, FAZUL, FVIOLETA) else listOf(MAGENTA, NARANJA, AMARILLO, VERDE, AZUL)
        val ga = hb * 0.045f
        clipRect(zb.left, zb.top, zb.right, zb.bottom) {
            bandas.forEachIndexed { k, col ->
                val o = TrazosPsicodelia.onda(-4f * u, w + 4f * u, cy + (k - 2) * ga * 1.05f, hb * 0.05f, w * 0.36f, 0.1f)
                if (oscuro) drawPath(o, col.copy(alpha = 0.2f), style = Stroke(ga * 2.4f))
                drawPath(o, col, style = Stroke(ga))
            }
            pinceladas(zb, if (oscuro) Color.Black else Color.White, 731, 14, u, 0.08f)
        }
        drawLine(brillo(oscuro, 0.4f), Offset(0f, b.top + 0.6f * u), Offset(w, b.top + 0.6f * u), 0.8f * u)
        // La placa con «y N más», pintada sobre la onda; sin placa, una flor que vibra.
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            val rr = r.height / 2
            if (oscuro) resplandor(r.center, r.width * 0.7f, FVIOLETA, 0.5f)
            drawRoundRect(sombra(oscuro, 0.35f), r.topLeft + Offset(1.5f * u, 2f * u), r.size, CornerRadius(rr))
            drawRoundRect(if (oscuro) CARTULINA_NOCHE else CARTULINA, r.topLeft, r.size, CornerRadius(rr))
            drawRoundRect(fluor(MAGENTA, oscuro), r.topLeft, r.size, CornerRadius(rr), style = Stroke(1.6f * u))
            drawRoundRect(fluor(VERDE, oscuro).copy(alpha = 0.8f), Offset(r.left + 3f * u, r.top + 3f * u), Size(r.width - 6f * u, r.height - 6f * u), CornerRadius(rr - 3f * u), style = Stroke(0.7f * u))
        } else {
            if (oscuro) resplandor(Offset(w / 2, cy), hb * 0.4f, FMAGENTA, 0.7f)
            with(TrazosPsicodelia) { flor(Offset(w / 2, cy), hb * 0.17f, fluor(MAGENTA, oscuro), fluor(VERDE, oscuro), 8, 0.6f) }
        }
        // El hueco del zócalo, en sombra.
        drawRect(Brush.verticalGradient(listOf(sombra(oscuro, 0.9f), sombra(oscuro, 0.6f)), zocalo, piso), Offset(0f, zocalo), Size(w, piso - zocalo))
        // El piso de duela, con sus juntas.
        drawRect(Brush.verticalGradient(listOf(t(mezcla(DUELA, Color.Black, 0.25f), oscuro), t(DUELA, oscuro)), piso, alto), Offset(0f, piso), Size(w, alto - piso))
        var yt = piso; var kt = 0
        while (yt < alto) {
            val paso = (3.4f + 2.4f * kt) * u
            drawLine(sombra(oscuro, 0.35f), Offset(0f, yt), Offset(w, yt), 0.7f * u)
            var xj = w * azar(741, kt) * 0.4f
            while (xj < w) { drawLine(sombra(oscuro, 0.25f), Offset(xj, yt), Offset(xj, yt + paso), 0.6f * u); xj += w * (0.3f + 0.3f * azar(742 + kt, (xj / w * 10).toInt())) }
            yt += paso; kt++
        }
        if (!oscuro) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.22f), Color.Transparent), 0f, w * 0.6f), Offset(0f, piso), Size(w * 0.6f, alto - piso))
        drawRect(Brush.verticalGradient(listOf(sombra(oscuro, 0.5f), Color.Transparent), piso, piso + 6f * u), Offset(0f, piso), Size(w, 6f * u))
        // El tapete trenzado, redondo, de trapos de colores.
        val tc = Offset(w * 0.53f, alto - (alto - piso) * 0.38f)
        val trx = w * 0.3f; val tr = (alto - piso) * 0.42f
        val trenza = listOf(Color(0xFFB0563A), Color(0xFFD9A64A), Color(0xFF4E8A7E), Color(0xFF8A4E7A), Color(0xFFE2C79A))
        drawOval(sombra(oscuro, 0.3f), Offset(tc.x - trx + 2f * u, tc.y - tr + 2.5f * u), Size(trx * 2, tr * 2))
        for (k in 0 until 7) {
            val f = 1f - k * 0.13f
            drawOval(t(trenza[k % trenza.size], oscuro, 0.82f), Offset(tc.x - trx * f, tc.y - tr * f), Size(trx * 2 * f, tr * 2 * f))
            drawOval(sombra(oscuro, 0.25f), Offset(tc.x - trx * f, tc.y - tr * f), Size(trx * 2 * f, tr * 2 * f), style = Stroke(0.6f * u))
        }
        for (k in 0 until 40) {
            val a = 2f * PI.toFloat() * k / 40
            drawLine(sombra(oscuro, 0.18f), Offset(tc.x + trx * 0.95f * cos(a), tc.y + tr * 0.95f * sin(a)), Offset(tc.x + trx * 0.8f * cos(a + 0.05f), tc.y + tr * 0.8f * sin(a + 0.05f)), 0.5f * u)
        }
        // La caja de discos de larga duración y la maceta de margaritas.
        cajaLargos(Offset(w * 0.13f, alto - (alto - piso) * 0.16f), w * 0.2f, hb * 0.5f, oscuro)
        maceta(Offset(w * 0.87f, alto - (alto - piso) * 0.14f), hb * 0.75f, oscuro)

        // ---- la luz: de día, la de la ventana; de noche, la luz negra baña todo de violeta y se hunden las esquinas
        if (oscuro) {
            resplandor(tubo, w * 1.05f, FVIOLETA, 0.75f)
            drawRect(Brush.radialGradient(0f to Color.Transparent, 0.45f to Color.Transparent, 1f to SALA.copy(alpha = 0.5f), center = tubo, radius = alto * 0.95f), Offset.Zero, Size(w, alto))
        } else {
            drawRect(Brush.horizontalGradient(listOf(luzMueble(0.1f), Color.Transparent, sombraMueble(0.07f)), 0f, w), Offset.Zero, Size(w, alto))
        }
        granoMueble(Rect(0f, 0f, w, alto), oscuro)
    }

    // ---------------------------------------------------------------- el compartimento: una caja de sencillos en su hueco

    /**
     * Una funda de sencillo vista de frente en [r] (sin girar), con [estilo]: 0, papel del periódico; 1, papel blanco; 2,
     * cartón; 3, la funda impresa de un color con su franja; 4, la funda ilustrada. Con [agujero], se ve el agujero redondo
     * con la etiqueta de [etiqueta] (para el sencillo que se sacó a medias).
     */
    private fun DrawScope.funda(r: Rect, estilo: Int, color: Color, oscuro: Boolean, agujero: Float = 0f, etiqueta: Color = NARANJA) {
        val u = density
        val base = when (estilo) { 0 -> PAPEL; 1 -> BLANCO; 2 -> CARTON; 3 -> mezcla(color, Color.White, 0.35f); else -> mezcla(color, Color.White, 0.15f) }
        val col = if (oscuro) (if (estilo <= 1) mezcla(base, SALA, 0.68f) else mezcla(fluor(color, true), SALA, 0.72f)) else base
        drawRect(col, r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            when (estilo) {
                3 -> drawRect(fluor(color, oscuro).let { if (oscuro) mezcla(it, SALA, 0.4f) else mezcla(it, Color.Black, 0.12f) }, Offset(r.left, r.top + r.height * 0.1f), Size(r.width, minOf(r.height * 0.12f, 3f * u)))
                4 -> {
                    val c2 = fluor(TrazosPsicodelia.complementario(color), oscuro).let { if (oscuro) mezcla(it, SALA, 0.35f) else it }
                    for (k in 0 until 3) drawPath(TrazosPsicodelia.onda(r.left, r.right, r.top + 3f * u + k * 3.2f * u, 1f * u, r.width * 0.3f, k * 0.2f), c2, style = Stroke(1.2f * u))
                }
            }
            // La muesca del pulgar, arriba, por donde asoma el canto del disco.
            if (estilo <= 2 && r.width > 12f * u) {
                val cm = Offset(r.center.x + r.width * 0.08f, r.top)
                val rm = minOf(r.width * 0.09f, 5f * u)
                drawCircle(VINILO, rm, cm)
                drawArc(Color.White.copy(alpha = 0.15f), 20f, 140f, false, Offset(cm.x - rm * 0.7f, cm.y - rm * 0.7f), Size(rm * 1.4f, rm * 1.4f), style = Stroke(0.4f * u))
            }
            if (agujero > 0f) {
                val ca = Offset(r.center.x, r.top + agujero)
                val ra = r.width * 0.3f
                drawCircle(VINILO, ra, ca)
                val e = fluor(etiqueta, oscuro)
                if (oscuro) drawCircle(e.copy(alpha = 0.35f), ra * 1.2f, ca)
                drawCircle(e, ra * 0.72f, ca)
                drawCircle(mezcla(e, Color.White, 0.35f), ra * 0.58f, ca, style = Stroke(0.5f * u))
                drawCircle(col, ra * 0.26f, ca)
                drawArc(sombra(oscuro, 0.4f), 180f, 150f, false, Offset(ca.x - ra, ca.y - ra), Size(ra * 2, ra * 2), style = Stroke(1.2f * u))
            }
        }
        // El canto de arriba, a la luz; de noche, el papel blanco se enciende un poco con la luz negra.
        drawLine(if (oscuro) FVIOLETA.copy(alpha = if (estilo <= 1) 0.55f else 0.25f) else luzMueble(0.75f), Offset(r.left, r.top + 0.4f * u), Offset(r.right, r.top + 0.4f * u), 0.8f * u)
        drawLine(sombra(oscuro, 0.25f), Offset(r.left, r.top), Offset(r.left, r.bottom), 0.5f * u)
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        val baja = h * SALIDA * ab
        val par = PARES[Math.floorMod(c.semilla, PARES.size)]

        // ---- el fondo del hueco: la pared de atrás y los costados, en sombra
        drawRect(Brush.verticalGradient(listOf(t(mezcla(MOSTRADOR_OSC, Color.Black, 0.45f), oscuro, 0.75f), t(MOSTRADOR_OSC, oscuro, 0.75f)), 0f, h), Offset.Zero, Size(w, h))
        val prof = w * 0.07f
        drawPath(Path().apply { moveTo(0f, 0f); lineTo(prof, prof * 0.8f); lineTo(prof, h); lineTo(0f, h); close() }, sombra(oscuro, 0.35f))
        drawPath(Path().apply { moveTo(w, 0f); lineTo(w - prof, prof * 0.8f); lineTo(w - prof, h); lineTo(w, h); close() }, t(MOSTRADOR, oscuro, 0.78f).copy(alpha = 0.7f))
        drawRect(Brush.verticalGradient(listOf(sombra(oscuro, 0.55f), Color.Transparent), 0f, h * 0.25f), Offset.Zero, Size(w, h * 0.25f))
        // Al abrirla, la luz entra en el hueco.
        if (ab > 0f) mancha(Offset(w * 0.5f, h * 0.4f), w * 0.5f, if (oscuro) FVIOLETA.copy(alpha = 0.12f * ab) else luzMueble(0.25f * ab))

        // ---- la caja: el frente, los costados que se van hacia atrás y los sencillos de canto
        val carton = c.sinTema && !c.vacio
        val y0 = h * CAJA_ARRIBA + baja
        val y1 = h * CAJA_ABAJO + baja
        val e = w * 0.02f * ab
        val fx0 = w * 0.05f - e; val fx1 = w * 0.95f + e
        val grosor = w * 0.035f
        val ix0 = fx0 + grosor; val ix1 = fx1 - grosor
        val dx = w * 0.06f
        val yAtras = h * 0.3f + baja
        val madera = when {
            carton -> CARTON
            else -> when (Math.floorMod(c.semilla / 7, 4)) { 1 -> CASA_TURQUESA; 2 -> CASA_ROSA; 3 -> CASA_AMARILLA; else -> PINO }
        }
        val pintada = !carton && madera != PINO
        // La sombra de la caja en la tabla.
        drawOval(sombra(oscuro, 0.45f), Offset(fx0, y1 - 3f * u), Size(fx1 - fx0 + 4f * u, 6f * u))
        // La tabla de atrás de la caja y los costados por dentro.
        drawRect(t(mezcla(madera, Color.Black, 0.28f), oscuro, 0.8f), Offset(ix0 + dx, yAtras), Size(ix1 - ix0 - 2 * dx, y0 - yAtras))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombra(oscuro, 0.45f)), yAtras, y0), Offset(ix0 + dx, yAtras), Size(ix1 - ix0 - 2 * dx, y0 - yAtras))
        drawPath(Path().apply { moveTo(ix0, y0); lineTo(ix0 + dx, yAtras); lineTo(ix0 + dx, y0); close() }, t(mezcla(madera, Color.Black, 0.4f), oscuro, 0.8f))
        drawPath(Path().apply { moveTo(ix1, y0); lineTo(ix1 - dx, yAtras); lineTo(ix1 - dx, y0); close() }, t(mezcla(madera, Color.Black, 0.12f), oscuro, 0.8f))

        // Los sencillos de canto, del fondo hacia el frente; el separador del tema entre ellos.
        val m = if (c.vacio) 0 else c.n.coerceIn(0, 11)
        val yFrente = y0 - h * 0.055f
        val yFondo = h * 0.17f + baja
        val sacado = if (m >= 3) Math.floorMod(c.semilla / 3, m - 1) + 1 else -1
        val separador = if (c.vacio) -1 else (m / 2).coerceAtLeast(0)
        val colores = listOf(MAGENTA, NARANJA, AMARILLO, VERDE, AZUL, VIOLETA)
        for (j in maxOf(m - 1, separador) downTo 0) {
            val f = j / 11f
            val xa = ix0 + dx * f; val xb = ix1 - dx * f
            val giro = if (c.sinTema) (azar(c.semilla, j + 30) - 0.5f) * 9f else (azar(c.semilla, j + 30) - 0.5f) * 2.5f
            if (j < m) {
                var top = yFrente - (yFrente - yFondo) * f * 1.0f + (azar(c.semilla, j) - 0.5f) * h * 0.025f
                val estilo = when (val a = azar(c.semilla, j + 10)) { in 0f..0.38f -> 0; in 0.38f..0.55f -> 1; in 0.55f..0.7f -> 2; in 0.7f..0.86f -> 3; else -> 4 }
                val color = colores[Math.floorMod(c.semilla + j * 5, colores.size)]
                var agujero = 0f
                if (j == sacado) { top -= h * (0.17f + 0.06f * ab); agujero = h * 0.15f }
                val ancho = (xb - xa) * (0.9f + 0.08f * azar(c.semilla, j + 20))
                val xs = xa + (xb - xa - ancho) * azar(c.semilla, j + 40)
                rotate(giro, Offset((xa + xb) / 2, y0)) {
                    drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombra(oscuro, 0.35f)), top - 3f * u, top), Offset(xs, top - 3f * u), Size(ancho, 3f * u))
                    funda(Rect(xs, top, xs + ancho, y0 + 4f * u), estilo, color, oscuro, agujero, colores[Math.floorMod(c.semilla + j, colores.size)])
                    drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombra(oscuro, 0.3f)), top + 4f * u, y0), Offset(xs, top + 4f * u), Size(ancho, (y0 - top - 4f * u).coerceAtLeast(0f)))
                }
            }
            if (j == separador) {
                // El separador del tema: una cartulina del color del tema con su pestaña y una margarita pintada.
                val topS = yFrente - (yFrente - yFondo) * f - h * 0.025f
                val izq = azar(c.semilla, 5) < 0.5f
                val pw = w * 0.22f
                val px = if (izq) xa + w * 0.04f else xb - w * 0.04f - pw
                val ph = h * 0.11f
                val colS = if (carton) CARTON else mezcla(par.first, Color.White, 0.25f)
                val colSep = if (oscuro) (if (carton) mezcla(CARTON, SALA, 0.7f) else fluor(par.first, true)) else colS
                rotate(giro * 0.5f, Offset((xa + xb) / 2, y0)) {
                    val sep = Path().apply {
                        moveTo(xa, y0 + 4f * u); lineTo(xa, topS); lineTo(px, topS)
                        if (carton) { lineTo(px + pw * 0.1f, topS - ph); lineTo(px + pw * 0.4f, topS - ph * 0.85f); lineTo(px + pw * 0.62f, topS - ph * 1.05f); lineTo(px + pw * 0.95f, topS - ph * 0.9f); lineTo(px + pw, topS) }
                        else { lineTo(px, topS - ph + 3f * u); quadraticTo(px, topS - ph, px + 3f * u, topS - ph); lineTo(px + pw - 3f * u, topS - ph); quadraticTo(px + pw, topS - ph, px + pw, topS - ph + 3f * u); lineTo(px + pw, topS) }
                        lineTo(xb, topS); lineTo(xb, y0 + 4f * u); close()
                    }
                    drawPath(sep, sombra(oscuro, 0.3f), style = Stroke(2f * u))
                    if (oscuro && !carton) brilla(sep, colSep.copy(alpha = 0.9f), 2f * u) else drawPath(sep, colSep)
                    drawLine(if (oscuro) Color.White.copy(alpha = 0.25f) else luzMueble(0.6f), Offset(px, topS - ph + 0.5f * u), Offset(px + pw, topS - ph + 0.5f * u), 0.7f * u)
                    val cf = Offset(px + pw / 2, topS - ph * 0.48f)
                    if (!carton) with(TrazosPsicodelia) { margarita(cf, ph * 0.36f, fluor(par.second, oscuro), fluor(AMARILLO, oscuro), 6) }
                    else drawPath(Path().apply { moveTo(cf.x - pw * 0.3f, cf.y); quadraticTo(cf.x - pw * 0.1f, cf.y - ph * 0.3f, cf.x, cf.y); quadraticTo(cf.x + pw * 0.1f, cf.y + ph * 0.3f, cf.x + pw * 0.3f, cf.y) },
                        t(TINTA, oscuro, 0.2f).copy(alpha = 0.6f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
                    drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombra(oscuro, 0.3f)), topS, y0), Offset(xa, topS), Size(xb - xa, y0 - topS))
                }
            }
        }

        // El frente de la caja: dos tablas con su junta, los clavos y el canto de arriba a la luz; o el cartón de la caja sin tema.
        val fr = Rect(fx0, y0, fx1, y1)
        if (ab > 0f) sombraSuave(Rect(fr.left, fr.bottom - 4f * u, fr.right, fr.bottom + 2f * u), 2f * u, 8f * u * ab, 0.3f * ab)
        if (carton) {
            drawRect(Brush.horizontalGradient(listOf(t(mezcla(CARTON, Color.White, 0.18f), oscuro), t(CARTON, oscuro), t(mezcla(CARTON, Color.Black, 0.15f), oscuro)), fr.left, fr.right), fr.topLeft, fr.size)
            // Las canaletas del cartón corrugado y una tira de cinta.
            drawLine(sombra(oscuro, 0.25f), Offset(fr.left, fr.top + 1.2f * u), Offset(fr.right, fr.top + 1.2f * u), 0.6f * u)
            cinta(Offset(fr.left + fr.width * 0.04f, fr.top - 1f * u), Offset(fr.left + fr.width * 0.04f, fr.bottom - fr.height * 0.4f), 6f * u, oscuro)
        } else {
            val ym = (fr.top + fr.bottom) / 2
            pino(Rect(fr.left, fr.top, fr.right, ym), t(madera, oscuro), c.semilla, oscuro, nudos = 1)
            pino(Rect(fr.left, ym, fr.right, fr.bottom), t(mezcla(madera, Color.Black, 0.05f), oscuro), c.semilla + 1, oscuro, nudos = 1)
            if (pintada) pinceladas(fr, if (oscuro) Color.Black else Color.White, c.semilla + 2, 10, u, 0.08f)
            drawLine(sombra(oscuro, 0.5f), Offset(fr.left, ym), Offset(fr.right, ym), 1f * u)
            drawRect(Brush.horizontalGradient(listOf(brillo(oscuro, 0.2f), Color.Transparent, sombra(oscuro, 0.18f)), fr.left, fr.right), fr.topLeft, fr.size)
            // Los clavos.
            for (x in listOf(fr.left + grosor * 0.5f, fr.right - grosor * 0.5f)) for (y in listOf(fr.top + (ym - fr.top) * 0.5f, ym + (fr.bottom - ym) * 0.5f)) {
                drawCircle(t(Color(0xFF4A4440), oscuro, 0.6f), 0.9f * u, Offset(x, y))
                drawCircle(brillo(oscuro, 0.5f), 0.4f * u, Offset(x - 0.3f * u, y - 0.3f * u))
            }
            // En las pintadas, una onda pintada a mano arriba del rótulo.
            if (pintada) {
                val o = TrazosPsicodelia.onda(fr.left + grosor * 1.4f, fr.right - grosor * 1.4f, fr.top + (h * rotulo.top + baja - fr.top) * 0.5f, 0.7f * u, w * 0.08f)
                drawPath(o, fluor(par.second, oscuro).let { if (oscuro) it else mezcla(it, Color.Black, 0.1f) }, style = Stroke(1.1f * u, cap = StrokeCap.Round))
            }
        }
        // El canto de arriba del frente y los cantos de los costados que se van hacia atrás.
        drawLine(brillo(oscuro, 0.8f), Offset(fr.left, fr.top + 0.5f * u), Offset(fr.right, fr.top + 0.5f * u), 1f * u)
        if (!carton) {
            drawLine(t(mezcla(madera, PINO_LUZ, 0.4f), oscuro), Offset(fx0 + grosor * 0.5f, y0), Offset(ix0 + dx - grosor * 0.2f, yAtras), grosor * 0.7f)
            drawLine(t(mezcla(madera, PINO_LUZ, 0.25f), oscuro), Offset(fx1 - grosor * 0.5f, y0), Offset(ix1 - dx + grosor * 0.2f, yAtras), grosor * 0.7f)
        }
        drawRect(sombra(oscuro, 0.3f), fr.topLeft, fr.size, style = Stroke(0.6f * u))

        // ---- la tarjeta del rótulo, clavada con dos chinchetas (la de la caja sin tema, un pedazo de bolsa de papel)
        val r = Rect(w * rotulo.left, h * rotulo.top + baja, w * rotulo.right, h * rotulo.bottom + baja)
        val tarjeta = r.inflate(1.5f * u)
        if (!c.vacio) {
            val papel = if (oscuro) (if (carton) mezcla(CARTULINA_NOCHE, CARTON, 0.35f) else CARTULINA_NOCHE) else (if (carton) mezcla(CARTULINA, CARTON, 0.4f) else CARTULINA)
            if (oscuro) mancha(tarjeta.center, tarjeta.width * 0.65f, FVIOLETA.copy(alpha = 0.16f))
            val forma = if (carton) contornoIrregular(tarjeta, c.semilla, 40, 0.025f) else Path().apply { addRect(tarjeta) }
            translate(1.2f * u, 1.6f * u) { drawPath(forma, sombra(oscuro, 0.35f)) }
            drawPath(forma, papel)
            if (!carton) {
                // El borde ondulado, a mano, en el color del tema.
                val i = 2.6f * u
                val col = fluor(par.first, oscuro).let { if (oscuro) mezcla(it, SALA, 0.25f) else mezcla(it, Color.Black, 0.1f) }
                drawPath(TrazosPsicodelia.onda(tarjeta.left + i, tarjeta.right - i, tarjeta.top + i, 0.6f * u, 9f * u), col, style = Stroke(0.9f * u))
                drawPath(TrazosPsicodelia.onda(tarjeta.left + i, tarjeta.right - i, tarjeta.bottom - i, 0.6f * u, 9f * u, 0.5f), col, style = Stroke(0.9f * u))
                chincheta(Offset(tarjeta.left + 3.2f * u, tarjeta.top + 3.2f * u), 1.9f * u, par.second, oscuro)
                chincheta(Offset(tarjeta.right - 3.2f * u, tarjeta.top + 3.2f * u), 1.9f * u, par.first, oscuro)
            } else {
                cinta(Offset(tarjeta.center.x - tarjeta.width * 0.1f, tarjeta.top - 2f * u), Offset(tarjeta.center.x + tarjeta.width * 0.1f, tarjeta.top + 3f * u), 5f * u, oscuro)
            }
        } else {
            // Un hueco sin tema: la marca clara donde estuvo una tarjeta y una chincheta olvidada.
            drawRect(if (oscuro) Color.White.copy(alpha = 0.04f) else luzMueble(0.25f), tarjeta.topLeft, tarjeta.size)
            chincheta(Offset(tarjeta.right - 3.2f * u, tarjeta.top + 3.2f * u), 1.9f * u, MAGENTA, oscuro)
        }
    }

    override val rotulo = Rect(0.1f, 0.57f, 0.9f, 0.9f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.fredokaRotulo
    override val salida = SALIDA
    override val placa: Rect = Rect(0.29f, 0.1f, 0.71f, 0.38f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior: la caja vista desde arriba

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val pl = 18f * u; val pa = 30f * u; val pf = 24f * u
        val fondo = Rect(pl, pa, w - pl, h - pf)
        // El fondo de la caja, en sombra.
        drawRect(t(mezcla(PINO, PINO_OSC, 0.6f), oscuro, 0.82f), fondo.topLeft, fondo.size)
        // Los sencillos de canto, del fondo (arriba) hacia el frente (abajo): de cada uno se ve el canto de la funda y un poco
        // de su cara; cada tanto, el separador de un tema con su pestaña, y un hueco donde falta uno.
        val colores = listOf(MAGENTA, NARANJA, AMARILLO, VERDE, AZUL, VIOLETA)
        var y = fondo.top + 6f * u
        var k = 0
        while (y < fondo.bottom) {
            val alto = (8f + 12f * azar(801, k)) * u
            val giro = (azar(802, k) - 0.5f) * 1.6f
            val dxs = (azar(803, k) - 0.5f) * 6f * u
            val r = Rect(fondo.left + 3f * u + dxs, y, fondo.right - 3f * u + dxs, y + alto + 8f * u)
            val hueco = k % 23 == 11
            val divisor = k % 14 == 6
            rotate(giro, r.center) {
                if (hueco) {
                    drawRect(sombra(oscuro, 0.5f), r.topLeft, Size(r.width, alto))
                } else if (divisor) {
                    val par = PARES[Math.floorMod(k / 14, PARES.size)]
                    val col = if (oscuro) fluor(par.first, true) else mezcla(par.first, PAPEL, 0.45f)
                    val izq = k % 28 == 6
                    val pw = r.width * 0.24f
                    val px = if (izq) r.left + r.width * 0.08f else r.right - r.width * 0.08f - pw
                    val tab = Path().apply {
                        addRect(Rect(r.left, r.top, r.right, r.bottom))
                        addRoundRect(RoundRect(Rect(px, r.top - 9f * u, px + pw, r.top + 2f * u), CornerRadius(3f * u)))
                    }
                    drawPath(tab, sombra(oscuro, 0.3f), style = Stroke(2.4f * u))
                    if (oscuro) drawRoundRect(col.copy(alpha = 0.22f), Offset(px - 3f * u, r.top - 12f * u), Size(pw + 6f * u, 14f * u), CornerRadius(5f * u))
                    drawRect(if (oscuro) mezcla(col, SALA, 0.55f) else col, r.topLeft, r.size)
                    drawRoundRect(col, Offset(px, r.top - 9f * u), Size(pw, 11f * u), CornerRadius(3f * u))
                    with(TrazosPsicodelia) { margarita(Offset(px + pw / 2, r.top - 3.5f * u), 3.4f * u, fluor(par.second, oscuro), fluor(AMARILLO, oscuro), 6) }
                } else {
                    val a = azar(804, k)
                    val estilo = when { a < 0.44f -> 0; a < 0.62f -> 1; a < 0.8f -> 2; a < 0.92f -> 3; else -> 4 }
                    funda(r, estilo, mezcla(colores[Math.floorMod(k * 5, colores.size)], PAPEL, 0.45f), oscuro)
                }
                // La sombra que le echa la funda de adelante, abajo.
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombra(oscuro, 0.35f)), r.top + alto - 4f * u, r.top + alto), Offset(r.left, r.top + alto - 4f * u), Size(r.width, 4f * u))
            }
            y += alto; k++
        }
        // De noche, la luz negra cae desde arriba y deja hundidas las esquinas.
        if (oscuro) {
            mancha(Offset(w * 0.6f, h * 0.2f), maxOf(w, h) * 0.7f, FVIOLETA.copy(alpha = 0.08f))
            drawRect(Brush.radialGradient(listOf(Color.Transparent, SALA.copy(alpha = 0.55f)), Offset(w * 0.55f, h * 0.35f), maxOf(w, h) * 0.75f))
        }
        // Las paredes de la caja vistas desde arriba: la de atrás y la de la izquierda en sombra, las otras a la luz.
        val atras = Path().apply { moveTo(0f, 0f); lineTo(w, 0f); lineTo(w - pl, pa); lineTo(pl, pa); close() }
        val izq = Path().apply { moveTo(0f, 0f); lineTo(pl, pa); lineTo(pl, h - pf); lineTo(0f, h); close() }
        val der = Path().apply { moveTo(w, 0f); lineTo(w, h); lineTo(w - pl, h - pf); lineTo(w - pl, pa); close() }
        val delante = Path().apply { moveTo(0f, h); lineTo(pl, h - pf); lineTo(w - pl, h - pf); lineTo(w, h); close() }
        listOf(Triple(atras, Rect(0f, 0f, w, pa), mezcla(PINO_LUZ, Color.Black, 0.12f)), Triple(izq, Rect(0f, 0f, pl, h), mezcla(PINO, Color.Black, 0.22f)),
            Triple(der, Rect(w - pl, 0f, w, h), PINO), Triple(delante, Rect(0f, h - pf, w, h), PINO_LUZ)).forEachIndexed { i, (p, rr, col) ->
            clipPath(p) { pino(rr, t(col, oscuro, 0.78f), 850 + i, oscuro, vertical = i == 1 || i == 2, nudos = 2) }
            drawPath(p, sombra(oscuro, 0.3f), style = Stroke(0.6f * u))
        }
        sombraInterior(fondo, 20f * u, if (oscuro) 0.45f else 0.28f)
        drawRect(brillo(oscuro, 0.5f), Offset.Zero, Size(w, 1.2f * u))
        // Los clavos de las esquinas.
        for ((x, yy) in listOf(pl * 0.5f to pa * 0.4f, w - pl * 0.5f to pa * 0.4f, pl * 0.5f to h - pf * 0.4f, w - pl * 0.5f to h - pf * 0.4f)) {
            drawCircle(t(Color(0xFF4A4440), oscuro, 0.6f), 1.3f * u, Offset(x, yy))
            drawCircle(brillo(oscuro, 0.5f), 0.5f * u, Offset(x - 0.4f * u, yy - 0.4f * u))
        }
        // Una margarita pintada a mano en la tabla de delante.
        val cm = Offset(w * 0.5f, h - pf * 0.5f)
        if (oscuro) resplandor(cm, pf * 1.2f, LIMON, 0.5f)
        with(TrazosPsicodelia) { margarita(cm, pf * 0.32f, fluor(AMARILLO, oscuro), fluor(NARANJA, oscuro), 8) }
        granoMueble(Rect(0f, 0f, w, h), oscuro, 0.7f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) Color(0xFFF2EAF7) else TINTA
    override val pared: Dp = 20.dp
    override val columnas = 2
    override val lineas = 4

    // ---------------------------------------------------------------- las fichas: fundas de papel de sencillo

    override fun forma(semilla: Int): Shape = GenericShape { s, _ ->
        val e = minOf(s.width, s.height) * 0.012f
        addRoundRect(RoundRect(Rect(0f, 0f, s.width, s.height), CornerRadius(e)))
    }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.4f else 1f
        val m = minOf(w, h)
        // La solapa pegada por detrás, que se transparenta a la izquierda y abajo.
        drawRect(sombraMueble(0.045f), Offset.Zero, Size(w * 0.05f, h))
        drawRect(sombraMueble(0.04f), Offset(0f, h - h * 0.045f), Size(w, h * 0.045f))
        drawLine(p.tenue.copy(alpha = 0.16f), Offset(w * 0.05f, 0f), Offset(w * 0.05f, h), 0.6f * u)
        // La marca del aro del disco, que se fue marcando en el papel.
        val cd = Offset(w * 0.5f, h * 0.54f)
        drawCircle(sombraMueble(0.05f), m * 0.44f, cd, style = Stroke(3f * u * g))
        drawCircle(luzMueble(0.35f), m * 0.44f - 2f * u * g, cd, style = Stroke(1f * u * g))
        // El agujero redondo de la funda, con la etiqueta del disco y su agujero grande, tenues para que se lea encima.
        val ra = m * (if (grande) 0.17f else 0.2f)
        val et = PARES[Math.floorMod(semilla, PARES.size)]
        drawCircle(p.tinta.copy(alpha = 0.07f), ra, cd)
        drawCircle(et.first.copy(alpha = 0.18f), ra * 0.8f, cd)
        drawCircle(et.second.copy(alpha = 0.16f), ra * 0.62f, cd, style = Stroke(0.8f * u * g))
        drawCircle(p.color, ra * 0.28f, cd)
        drawCircle(p.tinta.copy(alpha = 0.12f), ra * 0.28f, cd, style = Stroke(0.6f * u))
        // El filo del corte: la sombra del papel arriba a la izquierda y la luz abajo a la derecha.
        drawArc(sombraMueble(0.22f), 160f, 160f, false, Offset(cd.x - ra, cd.y - ra), Size(ra * 2, ra * 2), style = Stroke(1.1f * u * g))
        drawArc(luzMueble(0.7f), -20f, 160f, false, Offset(cd.x - ra, cd.y - ra), Size(ra * 2, ra * 2), style = Stroke(0.8f * u * g))
        // La muesca del pulgar, arriba, por donde asoma el canto negro del disco.
        val rm = m * 0.07f
        val cm = Offset(w * 0.5f, 0f)
        drawCircle(VINILO, rm, cm)
        for (k in 1..3) drawArc(Color.White.copy(alpha = 0.1f), 20f, 140f, false, Offset(cm.x - rm * (1f - 0.22f * k), cm.y - rm * (1f - 0.22f * k)), Size(rm * 2 * (1f - 0.22f * k), rm * 2 * (1f - 0.22f * k)), style = Stroke(0.4f * u))
        drawArc(luzMueble(0.8f), 0f, 180f, false, Offset(cm.x - rm, cm.y - rm), Size(rm * 2, rm * 2), style = Stroke(0.8f * u))
        // La onda de arcoíris que imprime la tienda en sus fundas, abajo.
        val yo = h - 7f * u * g
        drawPath(TrazosPsicodelia.onda(w * 0.1f, w * 0.9f, yo, 1.4f * u * g, 14f * u * g), TrazosPsicodelia.arcoiris(Offset(w * 0.1f, yo), Offset(w * 0.9f, yo), 0.55f), style = Stroke(1.3f * u * g, cap = StrokeCap.Round))
        // En la grande, el sello de goma de la tienda: una margarita en su aro de ondas, magenta, abajo a la izquierda.
        if (grande) {
            val cs = Offset(30f * u, h - 34f * u)
            val rs = 15f * u
            rotate(-14f, cs) {
                val tinta = MAGENTA.copy(alpha = 0.32f)
                drawCircle(tinta, rs, cs, style = Stroke(1.3f * u))
                drawCircle(tinta, rs * 0.78f, cs, style = Stroke(0.6f * u))
                for (k in 0 until 18) {
                    val a = 2f * PI.toFloat() * k / 18
                    drawCircle(tinta, 0.7f * u, Offset(cs.x + rs * 0.89f * cos(a), cs.y + rs * 0.89f * sin(a)))
                }
                with(TrazosPsicodelia) { margarita(cs, rs * 0.55f, tinta, null, 8) }
                drawCircle(tinta, rs * 0.13f, cs)
            }
        }
    }

    override val margenes = MargenesFicha(17.dp, 15.dp, 16.dp, 26.dp, 26.dp, 30.dp)
    override val proporcionFicha = 0.94f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.8f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El disco de oro: ya está hecho.
            "hecho" -> {
                drawCircle(Brush.radialGradient(listOf(mezcla(ORO, Color.White, 0.45f), ORO, ORO_OSC), c - Offset(s * 0.15f, s * 0.15f), s * 0.6f), s * 0.46f, c)
                drawCircle(ORO_OSC, s * 0.36f, c, style = Stroke(s * 0.035f))
                drawCircle(MAGENTA, s * 0.18f, c)
                drawCircle(mezcla(ORO, Color.White, 0.5f), s * 0.06f, c)
                drawCircle(ORO_OSC, s * 0.46f, c, style = Stroke(s * 0.05f))
            }
            // La flor que vibra: convence.
            "convence" -> with(TrazosPsicodelia) { flor(c, s * 0.46f, MAGENTA, VERDE, 6, 0.6f) }
            // El disco que gira y sus dos estelas: le sigue dando vueltas.
            "piensa" -> {
                drawCircle(VINILO, s * 0.3f, c)
                drawCircle(p.acento, s * 0.12f, c)
                drawCircle(p.color, s * 0.04f, c)
                drawArc(p.acento, -150f, 80f, false, Offset(c.x - s * 0.44f, c.y - s * 0.44f), Size(s * 0.88f, s * 0.88f), style = Stroke(s * 0.08f, cap = StrokeCap.Round))
                drawArc(p.acento, 30f, 80f, false, Offset(c.x - s * 0.44f, c.y - s * 0.44f), Size(s * 0.88f, s * 0.88f), style = Stroke(s * 0.08f, cap = StrokeCap.Round))
            }
            // Dos burbujas de aceite que flotan: todavía no se asienta.
            "duda" -> {
                drawCircle(p.tenue, s * 0.27f, Offset(c.x - s * 0.1f, c.y + s * 0.1f), style = Stroke(s * 0.08f))
                drawCircle(p.tenue, s * 0.13f, Offset(c.x + s * 0.27f, c.y - s * 0.25f), style = Stroke(s * 0.07f))
                drawArc(p.tenue, 200f, 60f, false, Offset(c.x - s * 0.28f, c.y - s * 0.08f), Size(s * 0.36f, s * 0.36f), style = Stroke(s * 0.06f, cap = StrokeCap.Round))
            }
            // El disco partido: así no.
            "desacuerdo" -> {
                val r = s * 0.42f
                val disco = Path().apply { moveTo(c.x, c.y); arcTo(Rect(c, r), -40f, 290f, false); close() }
                drawPath(disco, VINILO)
                drawCircle(MAGENTA, r * 0.38f, c)
                drawCircle(p.color, r * 0.1f, c)
                val pedazo = Path().apply { val o = c + Offset(s * 0.1f, -s * 0.08f); moveTo(o.x, o.y); arcTo(Rect(o, r), -95f, 45f, false); close() }
                drawPath(pedazo, VINILO)
            }
            else -> {}
        }
    }

    /** El adaptador amarillo de tres patas, el que fija el sencillo en el eje del tocadiscos: el disco con sus tres ventanas. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        val borde = mezcla(AMARILLO, Color(0xFF8A5A00), 0.6f)
        drawCircle(borde, s * 0.48f, c)
        drawCircle(AMARILLO, s * 0.42f, c)
        // Las tres ventanas entre las patas, y el agujero del eje.
        for (k in 0 until 3) {
            val a0 = -90f + 120f * k + 18f
            val ventana = Path().apply {
                arcTo(Rect(c, s * 0.33f), a0, 84f, true)
                arcTo(Rect(c, s * 0.17f), a0 + 84f, -84f, false)
                close()
            }
            drawPath(ventana, p.color)
            drawPath(ventana, borde, style = Stroke(s * 0.04f))
        }
        drawCircle(p.color, s * 0.06f, c)
        drawCircle(Color.White.copy(alpha = 0.5f), s * 0.07f, c + Offset(-s * 0.26f, -s * 0.26f))
    }

    override val desorden = 2.5f
}
