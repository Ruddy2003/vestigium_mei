package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/*
 * El libro del día de Espacio (lo usa `EstanteEspacio`, en `BibliotecaFuturo.kt`): la bitácora de tela de hoy, la misma clase
 * de libreta de papel que luego se guarda en el estante (allá, alta y delgada; aquí, apaisada y grande), sujeta a la pared de
 * la cabina de la litera. La paleta y la luz son las del estante (`PaletaEstanteEspacio`, `AireEstanteEspacio`): misma nave,
 * otro rincón.
 */

/**
 * La luz del rincón del libro del día. De día no hay luz dibujada (las luminarias del techo quedan fuera, arriba a la
 * izquierda); en oscuro manda la lámpara de lectura, cálida, arriba a la derecha, y el piloto de guardia, azul, abajo a la
 * izquierda, apenas da un filo frío; en los sueños la lámpara se apaga y manda el piloto de guardia.
 */
internal class LuzTapaEspacio(w: Float, h: Float, private val u: Float, private val a: AireEstanteEspacio) {
    /** La boca de la lámpara de lectura y el piloto de guardia, en píxeles del lienzo. */
    val lampara = Offset(w - 21f * u, 40f * u)
    val guardia = Offset(minOf(9f * u, w * 0.025f), h * 0.6f)
    /** Hasta dónde llega cada luz, en píxeles: la lámpara cruza casi toda la tira; el piloto, un charco corto. */
    private val alcanceLampara = 290f * u
    private val alcanceGuardia = 245f * u

    /** Cuánto llega de la lámpara a [p]: se apaga con el cuadrado de la distancia (lo lejano casi se pierde). */
    fun deLampara(p: Offset): Float = (1f - (p - lampara).getDistance() / alcanceLampara).coerceIn(0f, 1f).pow(2f)

    /** Cuánto llega del piloto de guardia a [p]. */
    fun deGuardia(p: Offset): Float = (1f - (p - guardia).getDistance() / alcanceGuardia).coerceIn(0f, 1f).pow(1.7f)

    fun en(p: Offset): LuzEstanteEspacio = when {
        !a.lumbre -> LuzEstanteEspacio(1f, 0f)
        a.noche -> LuzEstanteEspacio(deGuardia(p), 0.05f)
        else -> LuzEstanteEspacio(deLampara(p), 0.5f * deGuardia(p))
    }

    /**
     * El color [c] con la luz que le llega en cada punto, como un pincel radial desde la luz que manda (un solo dibujo y sin
     * escalones); con [papel], el de las etiquetas, que casi no se apaga. De día, el color tal cual.
     */
    fun pincel(c: Color, papel: Boolean = false): Brush {
        if (!a.lumbre) return SolidColor(if (papel) a.etiqueta(LuzEstanteEspacio(1f, 0f)) else c)
        val centro = if (a.noche) guardia else lampara
        val radio = if (a.noche) alcanceGuardia else alcanceLampara
        val paradas = 7
        val colores = List(paradas) { i ->
            val p = centro + Offset(radio * i / (paradas - 1f), 0f)
            val l = en(p)
            if (papel) a.etiqueta(l) else a.luz(c, l)
        }
        return Brush.radialGradient(colores, centro, radio)
    }

    /** Hacia dónde cae la sombra de algo que está en [p], de [largo]: de día, abajo a la derecha; de noche, lejos de la luz. */
    fun caida(p: Offset, largo: Float): Offset {
        if (!a.lumbre) return Offset(largo, largo * 1.15f)
        val v = p - (if (a.noche) guardia else lampara)
        val d = v.getDistance().coerceAtLeast(1f)
        return v / d * (largo * 1.3f)
    }
}

/**
 * Dónde va cada parte del libro, en píxeles: el libro de [bl] a [br] y de [bt] a [bb]; el lomo, de [bl] a [lomo]; el grueso
 * de las hojas que asoma abajo y a la derecha ([g], más con más palabras); la tarjeta del extracto ([tarjeta], la misma caja
 * que [MuebleTapaEspacio.CAJA] con un margen) y la goma ([goma], su x).
 */
internal class GeoTapaEspacio(w: Float, h: Float, u: Float, lleno: Float) {
    val bl = w * 0.05f
    val br = w * 0.885f
    val bt = h * 0.075f
    val bb = h * 0.9f
    val lomo = bl + maxOf(10f * u, w * 0.034f)
    val g = (2.2f + 2.6f * lleno) * u
    val libro = Rect(bl, bt, br, bb)
    val tarjeta = Rect(w * 0.105f, h * 0.27f, w * 0.83f, h * 0.67f)
    val goma = w * 0.857f
}

internal object MuebleTapaEspacio {
    private val P = PaletaEstanteEspacio

    // ---------------------------------------------------------------- las medidas y el texto

    val ALTO: Dp = 188.dp
    /** El extracto va en la tarjeta de la ventanilla de vinilo (un poco más ancha que la caja de antes, no más chica). */
    val CAJA = Rect(0.115f, 0.285f, 0.82f, 0.655f)
    val LETRA: FontFamily = LetraExtra.grotesk
    const val TAM = 13f
    const val LINEAS = 3

    /** La tinta del extracto: azul de noche sobre la tarjeta, que siempre está alumbrada. */
    fun tinta(oscuro: Boolean, noche: Boolean): Color = if (oscuro || noche) Color(0xFF151A2A) else P.TINTA

    fun palabras(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

    // ---------------------------------------------------------------- la paleta propia

    /** La lona azul de la bitácora de hoy (la de los volúmenes sin ánimo del estante), su lomo y el forro de dentro. */
    private val LONA = Color(0xFF46608A)
    private val LOMO = Color(0xFF34466A)
    private val FORRO = Color(0xFF8C93A6)
    /** El ribete de la ventanilla de vinilo. */
    private val RIBETE = Color(0xFF2B3550)
    /** La pared de la cabina: chapa pintada de crema, un poco más clara que el módulo del estante. */
    private val PARED = Color(0xFFE6DABF)

    /** Los trazos que se rehacen en cada dibujo (en el hilo de la interfaz, de uno en uno). */
    private val trazo = Path()
    private val trama = Path()
    private var tramaClave = -1f

    // ---------------------------------------------------------------- el dibujo

    fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 4f * u || h < 4f * u) return
        val a = AireEstanteEspacio(oscuro, e.noche)
        val lleno = if (e.vacio) 0f else (e.palabras / 800f).coerceIn(0f, 1f)
        val g = GeoTapaEspacio(w, h, u, lleno)
        val luz = LuzTapaEspacio(w, h, u, a)
        val ab = e.abierto.coerceIn(0f, 1f)
        pared(a, luz)
        cables(a, luz)
        sombraLibro(g, a, luz)
        hojasYContratapa(g, a, luz, e.semilla)
        if (ab > 0.02f) paginaAbierta(g, a, luz, e, medidor)
        val giro = (0.92f * PI.toFloat() * ((ab - 0.12f) / 0.88f).coerceIn(0f, 1f).pow(0.8f))
        cubierta(g, giro, a) {
            lona(g, a, luz, e.semilla)
            parche(Offset(g.lomo + 17f * u, h * 0.175f), 12.5f * u, a, luz)
            rotulo(g, e, medidor, a, luz)
            ventanilla(g, a, luz)
            etiquetaPalabras(g, e, medidor, a, luz)
            with(PiezasTapaEspacio) { cintaAmericana(g, a, luz) }
        }
        with(PiezasTapaEspacio) {
            cintaMarcar(g, a, luz, e.semilla, ab)
            goma(g, a, luz, ab)
            lapiz(g, a, luz, !e.vacio, ab)
            lampara(a, luz)
            piloto(a, luz)
        }
        brillos(g, a, luz)
        granoMueble(Rect(0f, 0f, w, h), oscuro, 0.8f)
    }

    // ---------------------------------------------------------------- la pared y los cables

    /**
     * La pared de la cabina: chapa pintada de crema con su junta y sus remaches, la pintura gastada y el cable de la lámpara
     * que baja por la derecha sujeto con dos tiras de velcro. De día, la luz de las luminarias que entra desde arriba a la
     * izquierda; de noche, la que le llega de la lámpara (o, en los sueños, del piloto), y lo lejano se pierde.
     */
    private fun DrawScope.pared(a: AireEstanteEspacio, luz: LuzTapaEspacio) {
        val u = density; val w = size.width; val h = size.height
        drawRect(luz.pincel(PARED), Offset.Zero, size)
        if (!a.lumbre) {
            mancha(Offset(w * 0.12f, -h * 0.1f), w * 0.75f, luzMueble(0.42f))
            mancha(Offset(w * 1.02f, h * 1.05f), w * 0.55f, sombraMueble(0.18f))
        } else if (!a.noche) mancha(luz.guardia, 70f * u, P.GUARDIA.copy(alpha = 0.16f))
        // La junta de la chapa, a la derecha, con su fila de remaches; y el desgaste de la pintura.
        val xj = w * 0.952f
        drawRect(a.sombra(0.3f), Offset(xj, 0f), Size(1.4f * u, h))
        drawRect(a.claro(0.4f), Offset(xj + 1.4f * u, 0f), Size(0.8f * u, h))
        var y = 22f * u
        while (y < h - 6f * u) {
            remacheEspacio(Offset(xj - 5f * u, y), 1.2f * u, a.luz(mezcla(PARED, P.ACERO, 0.45f), luz.en(Offset(xj, y))), a)
            y += maxOf(1f, 26f * u)
        }
        pinceladas(Rect(0f, h * 0.55f, w, h), a.sombra(1f), 1301, 7, u, 0.06f)
        pinceladas(Rect(0f, 0f, w * 0.4f, h * 0.5f), a.claro(1f), 1302, 4, u, 0.07f)
        // El cable de la lámpara, que baja pegado a la pared con dos tiras de velcro.
        val xc = w - 7f * u
        val lc = luz.en(Offset(xc, h * 0.5f))
        drawLine(a.sombra(0.3f), Offset(xc + 1.5f * u, 12f * u), Offset(xc + 1.5f * u, h), 2f * u)
        drawLine(a.luz(P.GOMA, lc), Offset(xc, 10f * u), Offset(xc, h), 1.8f * u)
        listOf(0.5f, 0.84f).forEach { f ->
            val t = Rect(xc - 4.5f * u, h * f - 3f * u, xc + 4.5f * u, h * f + 3f * u)
            drawRect(a.sombra(0.3f), t.topLeft + Offset(0.8f * u, 1f * u), t.size); drawRect(a.luz(mezcla(P.ACERO, P.GOMA, 0.45f), luz.en(t.center)), t.topLeft, t.size)
        }
    }

    /**
     * El mazo de cables que cruza arriba, contra el techo, medio cortado por el borde: tres cables redondos (goma, gris y crudo
     * con su raya naranja) atados con bridas a trechos desparejos, con el rabo cortado de cada brida, y sujetos a la pared con
     * dos abrazaderas atornilladas; su sombra en la pared.
     */
    private fun DrawScope.cables(a: AireEstanteEspacio, luz: LuzTapaEspacio) {
        val u = density; val w = size.width
        val gr = 4.2f * u
        val y0 = -1.8f * u
        val abajo = y0 + 3f * gr
        drawRect(a.sombra(if (a.lumbre) 0.42f else 0.24f), Offset(0f, abajo - 0.5f * u), Size(w, 3f * u))
        drawRect(a.sombra(0.12f), Offset(0f, abajo), Size(w, 7f * u))
        val colores = listOf(P.GOMA, mezcla(P.CASCO_OSC, P.CASCO, 0.35f), mezcla(P.TELA, P.PAPEL, 0.3f))
        colores.forEachIndexed { i, c ->
            val yc = y0 + gr * (i + 0.5f)
            drawRect(luz.pincel(c), Offset(0f, yc - gr / 2), Size(w, gr))
            drawRect(a.claro(0.35f), Offset(0f, yc - gr * 0.28f), Size(w, 0.8f * u))
            drawRect(a.sombra(0.3f), Offset(0f, yc + gr / 2 - 0.9f * u), Size(w, 0.9f * u))
        }
        drawLine(a.luz(P.NARANJA, luz.en(Offset(w * 0.7f, 0f))), Offset(0f, y0 + gr * 2.62f), Offset(w, y0 + gr * 2.62f), 1f * u,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(9f * u, 7f * u)))
        // Las bridas: la cinta que abraza el mazo y su rabo cortado que asoma abajo.
        var xb = w * 0.06f
        var k = 0
        while (xb < w - 4f * u) {
            val l = luz.en(Offset(xb, 0f))
            drawRect(a.luz(P.PAPEL, l), Offset(xb, y0), Size(2.2f * u, 3f * gr + 0.6f * u))
            drawRect(a.sombra(0.35f), Offset(xb + 2.2f * u, y0), Size(0.8f * u, 3f * gr))
            drawLine(a.luz(P.PAPEL, l), Offset(xb + 1.1f * u, abajo), Offset(xb + 3.6f * u, abajo + 3.4f * u), 1.5f * u)
            xb += maxOf(1f, (38f + 34f * azar(1311, k)) * u); k++
        }
        // Las dos abrazaderas: el fleje que da la vuelta al mazo y la pestaña atornillada a la pared.
        listOf(w * 0.31f, w * 0.79f).forEach { xa ->
            val l = luz.en(Offset(xa, 6f * u))
            drawRect(a.sombra(0.35f), Offset(xa - 3f * u, abajo + 1f * u), Size(8f * u, 7f * u))
            drawRect(a.luz(P.ACERO, l), Offset(xa - 4f * u, y0), Size(8f * u, 3f * gr + 1.5f * u))
            drawRect(a.luz(P.ACERO, l), Offset(xa - 3.2f * u, abajo), Size(6.4f * u, 6.5f * u))
            drawRect(a.claro(0.5f), Offset(xa - 3.6f * u, y0), Size(1f * u, 3f * gr + 1.5f * u))
            remacheEspacio(Offset(xa, abajo + 3.6f * u), 1.3f * u, a.luz(mezcla(P.ACERO, P.CASCO, 0.4f), l), a)
        }
    }

    // ---------------------------------------------------------------- el libro

    /** La sombra del libro en la pared, del color de la sombra del momento y corrida lejos de la luz. */
    private fun DrawScope.sombraLibro(g: GeoTapaEspacio, a: AireEstanteEspacio, luz: LuzTapaEspacio) {
        val u = density
        val d = luz.caida(g.libro.center, 5f * u)
        val r = Rect(g.bl, g.bt, g.br + g.g, g.bb + g.g)
        drawRoundRect(a.sombra(if (a.oscuro) 0.5f else 0.24f), r.topLeft + d * 1.6f, r.size, CornerRadius(5f * u))
        drawRoundRect(a.sombra(if (a.oscuro) 0.4f else 0.2f), r.topLeft + d * 0.7f, r.size, CornerRadius(4f * u))
    }

    /** La contratapa y el canto de las hojas, que asoman abajo y a la derecha de la tapa: más grueso cuanto más se escribió. */
    private fun DrawScope.hojasYContratapa(g: GeoTapaEspacio, a: AireEstanteEspacio, luz: LuzTapaEspacio, semilla: Int) {
        val u = density
        val atras = Rect(g.bl + g.g, g.bt + g.g, g.br + g.g, g.bb + g.g)
        drawRoundRect(luz.pincel(mezcla(LOMO, P.GOMA, 0.3f)), atras.topLeft, atras.size, CornerRadius(3.5f * u))
        val papel = luz.pincel(mezcla(P.PAPEL, P.TELA, 0.3f))
        // El canto de la derecha y el de abajo, con sus rayitas de hojas.
        drawRect(papel, Offset(g.br - 2f * u, g.bt + g.g * 0.6f), Size(g.g + 0.6f * u, g.bb - g.bt - g.g * 0.2f))
        drawRect(papel, Offset(g.bl + g.g * 0.8f, g.bb - 2f * u), Size(g.br - g.bl - g.g * 0.4f, g.g + 0.6f * u))
        var k = 0
        var y = g.bb + 0.6f * u
        while (y < g.bb + g.g - 0.3f * u) {
            drawLine(a.sombra(0.12f + 0.12f * azar(semilla, k)), Offset(g.bl + g.g, y), Offset(g.br + g.g * 0.6f, y), 0.5f * u)
            y += maxOf(1f, 1.1f * u); k++
        }
        drawRect(a.sombra(0.25f), Offset(g.br + g.g - 0.8f * u, g.bt + g.g), Size(0.8f * u, g.bb - g.bt))
    }

    /**
     * Dibuja [bloque] girado sobre el eje vertical en [x0], visto de frente: x' = x0 + sx·(x − x0), y' = y + ky·(x − x0) (el
     * canto libre se acerca al eje y sube un poco).
     */
    private inline fun DrawScope.girada(x0: Float, sx: Float, ky: Float, crossinline bloque: DrawScope.() -> Unit) {
        withTransform({ transform(Matrix(floatArrayOf(sx, ky, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, x0 * (1f - sx), -ky * x0, 0f, 1f))) }) { bloque() }
    }

    /**
     * La tapa delantera: cerrada, dibuja [fuera] tal cual; al abrirse gira sobre el lomo (el canto libre se acerca y sube un
     * poco) y, ya vuelta, se ve el forro de dentro a la izquierda del lomo (dibujado ya a la izquierda, sin espejo).
     */
    private inline fun DrawScope.cubierta(g: GeoTapaEspacio, giro: Float, a: AireEstanteEspacio, crossinline fuera: DrawScope.() -> Unit) {
        if (giro <= 0.001f) { fuera(); return }
        val u = density
        val x0 = g.lomo
        val co = cos(giro); val si = sin(giro)
        if (co >= 0f) girada(x0, co, -si * 0.12f) {
            fuera()
            drawRect(a.sombra(0.5f * (1f - co)), Offset(g.bl, g.bt), Size(g.br - g.bl, g.bb - g.bt))
        } else girada(x0, -co, si * 0.12f) {
            val r = Rect(x0 - (g.br - x0), g.bt, x0 + 0.5f * u, g.bb)
            drawRoundRect(a.luz(FORRO, LuzEstanteEspacio(0.6f, 0f)), r.topLeft, r.size, CornerRadius(4f * u))
            drawRect(a.sombra(0.4f * (1f + co)), r.topLeft, r.size)
        }
    }

    /**
     * La lona de la tapa: el color con la luz que le llega, la trama (dos caminos que se arman una vez por tamaño), la costura
     * de los bordes, el lomo con su bisagra, el desgaste de las esquinas y las esquineras de aluminio.
     */
    private fun DrawScope.lona(g: GeoTapaEspacio, a: AireEstanteEspacio, luz: LuzTapaEspacio, semilla: Int) {
        val u = density; val w = size.width
        val r = g.libro
        drawRoundRect(luz.pincel(LONA), r.topLeft, r.size, CornerRadius(4f * u))
        clipRect(r.left, r.top, r.right, r.bottom) {
            // La trama de la lona: hilos finos a lo alto y a lo ancho, a trechos desparejos (se arma una vez por tamaño).
            val clave = r.width * 7919f + r.height + r.left
            if (tramaClave != clave) {
                trama.reset()
                var x = r.left + 1.5f * u; var k = 0
                while (x < r.right) { trama.moveTo(x, r.top); trama.lineTo(x, r.bottom); x += maxOf(1f, (1.8f + 1.6f * azar(1341, k)) * u); k++ }
                var y = r.top + 1.2f * u
                while (y < r.bottom) { trama.moveTo(r.left, y); trama.lineTo(r.right, y); y += maxOf(1f, (2.4f + 1.8f * azar(1342, k)) * u); k++ }
                tramaClave = clave
            }
            drawPath(trama, a.claro(0.06f), style = Stroke(0.5f * u))
            translate(0.8f * u, 0.8f * u) { drawPath(trama, a.sombra(0.06f), style = Stroke(0.5f * u)) }
            // De día, la luz de arriba a la izquierda y la sombra del lado derecho.
            if (!a.lumbre) {
                mancha(Offset(r.left + r.width * 0.15f, r.top), r.width * 0.55f, luzMueble(0.2f))
                drawRect(sombraMueble(0.12f), Offset(r.right - r.width * 0.12f, r.top), Size(r.width * 0.12f, r.height))
            }
            // El desgaste: la lona más clara en las esquinas y los cantos, y una mancha de grasa.
            pinceladas(Rect(r.left, r.top, r.right, r.top + 8f * u), a.claro(1f), semilla + 31, 6, u, 0.08f)
            pinceladas(Rect(r.left, r.bottom - 8f * u, r.right, r.bottom), a.claro(1f), semilla + 32, 6, u, 0.08f)
            pinceladas(Rect(r.left + r.width * 0.55f, r.top + r.height * 0.7f, r.right, r.bottom), a.sombra(1f), semilla + 33, 4, u, 0.1f)
        }
        // La costura de los bordes, a 4 dp.
        drawRoundRect(a.luz(mezcla(P.TELA, P.TRIGO, 0.25f), luz.en(r.center)).copy(alpha = 0.55f), Offset(g.lomo + 3f * u, r.top + 3.5f * u),
            Size(r.right - g.lomo - 7f * u, r.height - 7f * u), CornerRadius(2f * u),
            style = Stroke(0.7f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.2f * u, 1.6f * u))))
        // El lomo: la lona más oscura, la bisagra hundida y su costura.
        val ll = luz.en(Offset(g.bl, r.center.y))
        drawRoundRect(a.luz(LOMO, ll), r.topLeft, Size(g.lomo - g.bl, r.height), CornerRadius(4f * u))
        drawRect(a.sombra(0.4f), Offset(g.lomo - 1.4f * u, r.top), Size(1.4f * u, r.height))
        drawRect(a.claro(0.3f), Offset(g.lomo, r.top), Size(0.8f * u, r.height))
        drawRect(a.claro(0.18f), Offset(g.bl + 1.6f * u, r.top + 2f * u), Size(1f * u, r.height - 4f * u))
        // Las esquineras de aluminio de la derecha, con su remache.
        listOf(true, false).forEach { arriba ->
            val y = if (arriba) r.top else r.bottom
            val s = if (arriba) 1f else -1f
            val q = 11f * u
            poligonoEnEspacio(trazo, r.right, y, r.right - q, y, r.right, y + s * q)
            val lc = luz.en(Offset(r.right, y))
            drawPath(trazo, a.luz(P.ACERO, lc))
            drawLine(a.claro(0.5f), Offset(r.right - q + 1.5f * u, y + s * 0.8f * u), Offset(r.right - 0.8f * u, y + s * 0.8f * u), 0.8f * u)
            remacheEspacio(Offset(r.right - q * 0.32f, y + s * q * 0.32f), 1f * u, a.luz(mezcla(P.ACERO, P.CASCO, 0.4f), lc), a)
        }
        // El filo de la luz que manda en el canto de arriba y en el de la derecha (de noche).
        if (a.lumbre) {
            val k = luz.en(Offset(r.right, r.top)).k
            drawRect(a.filo(0.55f * k), Offset(r.left + 4f * u, r.top), Size(r.width - 8f * u, 1f * u))
            if (!a.noche) drawRect(a.filo(0.5f * k), Offset(r.right - 1.1f * u, r.top + 4f * u), Size(1.1f * u, r.height - 8f * u))
            else drawRect(a.filo(0.5f * luz.en(Offset(r.left, r.bottom)).k), Offset(r.left, r.top + 4f * u), Size(1.1f * u, r.height - 8f * u))
        }
        if (w <= 0f) return
    }

    /**
     * El parche bordado de la nave, cosido arriba a la izquierda: la orla naranja con su puntada, el azul de fondo, el anillo
     * con el eje que lo atraviesa y, delante, la estrella de destino.
     */
    private fun DrawScope.parche(c: Offset, r: Float, a: AireEstanteEspacio, luz: LuzTapaEspacio) {
        val u = density
        val l = luz.en(c)
        drawCircle(a.sombra(0.35f), r, c + Offset(0.8f * u, 1.1f * u))
        drawCircle(a.luz(P.NARANJA, l), r, c)
        drawCircle(a.luz(mezcla(P.NARANJA, P.TINTA, 0.35f), l).copy(alpha = 0.6f), r * 0.86f, c,
            style = Stroke(0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.2f * u, 0.9f * u))))
        drawCircle(a.luz(Color(0xFF1C2850), l), r * 0.76f, c)
        // El anillo de la nave, inclinado, con el eje que lo cruza.
        drawOval(a.luz(P.CASCO, l), Offset(c.x - r * 0.55f, c.y - r * 0.16f + r * 0.12f), Size(r * 1.1f, r * 0.42f), style = Stroke(1.3f * u))
        drawLine(a.luz(P.CASCO, l), Offset(c.x - r * 0.08f, c.y - r * 0.34f), Offset(c.x + r * 0.08f, c.y + r * 0.52f), 1.1f * u)
        // La estrella de destino, de cuatro puntas.
        val e = Offset(c.x + r * 0.32f, c.y - r * 0.36f)
        val k = r * 0.24f; val q = k * 0.18f
        poligonoEnEspacio(trazo, e.x, e.y - k, e.x + q, e.y - q, e.x + k, e.y, e.x + q, e.y + q, e.x, e.y + k, e.x - q, e.y + q, e.x - k, e.y, e.x - q, e.y - q)
        drawPath(trazo, a.luz(P.LUZ_FRIA, LuzEstanteEspacio(maxOf(l.k, 0.5f), l.f)))
        drawArc(a.claro(0.45f), 200f, 80f, false, Offset(c.x - r * 0.95f, c.y - r * 0.95f), Size(r * 1.9f, r * 1.9f), style = Stroke(1f * u))
    }

    /** La cinta de tela cosida arriba con la fecha, con los extremos cortados en pico (como la de los volúmenes del estante). */
    private fun DrawScope.rotulo(g: GeoTapaEspacio, e: EstadoTapa, medidor: TextMeasurer, a: AireEstanteEspacio, luz: LuzTapaEspacio) {
        val u = density; val w = size.width; val h = size.height
        val r = Rect(g.lomo + 36f * u, h * 0.108f, maxOf(g.lomo + 60f * u, w * 0.655f), h * 0.232f)
        val l = luz.en(r.center)
        val pico = 3.2f * u
        poligonoEnEspacio(trazo, r.left, r.top, r.right, r.top, r.right - pico, r.center.y, r.right, r.bottom, r.left, r.bottom, r.left + pico, r.center.y)
        translate(0.8f * u, 1.1f * u) { drawPath(trazo, a.sombra(0.35f)) }
        drawPath(trazo, a.etiqueta(l))
        drawRect(a.claro(0.4f), Offset(r.left + pico, r.top), Size(r.width - 2 * pico, 0.7f * u))
        // Las dos puntadas que la sujetan.
        listOf(r.left + pico + 3f * u, r.right - pico - 3f * u).forEach { x ->
            drawLine(a.luz(mezcla(P.TELA, P.TINTA, 0.35f), l), Offset(x, r.top + 2f * u), Offset(x, r.bottom - 2f * u), 0.7f * u)
        }
        textoEn(medidor, e.fecha, Rect(r.left + pico + 6f * u, r.top, r.right - pico - 6f * u, r.bottom), Letra.codigo, P.TINTA, 10.5f, peso = FontWeight.Medium, minimo = 6f)
    }

    /**
     * La ventanilla de vinilo cosida en la tapa, donde va la tarjeta del día (ahí la pantalla escribe el extracto): el ribete
     * oscuro con su costura, la tarjeta de papel alumbrada (casi no se apaga: el texto se lee siempre), su fibra y el brillo
     * del vinilo en la esquina que mira a la luz.
     */
    private fun DrawScope.ventanilla(g: GeoTapaEspacio, a: AireEstanteEspacio, luz: LuzTapaEspacio) {
        val u = density
        val t = g.tarjeta
        val rib = t.inflate(3.2f * u)
        drawRoundRect(a.sombra(0.3f), rib.topLeft + Offset(0.6f * u, 1f * u), rib.size, CornerRadius(3f * u))
        drawRoundRect(luz.pincel(RIBETE), rib.topLeft, rib.size, CornerRadius(3f * u))
        drawRoundRect(a.luz(mezcla(P.TELA, P.TRIGO, 0.2f), luz.en(rib.center)).copy(alpha = 0.5f), rib.deflate(1.6f * u).topLeft, rib.deflate(1.6f * u).size,
            CornerRadius(2f * u), style = Stroke(0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.8f * u, 1.4f * u))))
        drawRect(luz.pincel(P.PAPEL, papel = true), t.topLeft, t.size)
        // La sombra que el ribete deja en la tarjeta (arriba y a la izquierda) y el brillo del vinilo en la esquina.
        drawRect(a.sombra(0.16f), t.topLeft, Size(t.width, 2.2f * u))
        drawRect(a.sombra(0.1f), t.topLeft, Size(2f * u, t.height))
        poligonoEnEspacio(trazo, t.left, t.top, t.left + t.width * 0.16f, t.top, t.left, t.top + t.height * 0.5f)
        drawPath(trazo, Color.White.copy(alpha = if (a.oscuro) 0.06f else 0.28f))
        if (a.lumbre && !a.noche) {
            poligonoEnEspacio(trazo, t.right, t.top, t.right, t.top + t.height * 0.45f, t.right - t.width * 0.12f, t.top)
            drawPath(trazo, P.AMBAR.copy(alpha = 0.14f))
        }
        drawLine(Color.White.copy(alpha = if (a.oscuro) 0.1f else 0.35f), Offset(t.right - t.width * 0.2f, t.bottom - 1.5f * u), Offset(t.right - 2f * u, t.bottom - 1.5f * u), 0.8f * u)
    }

    /**
     * La etiqueta de inventario pegada abajo a la derecha: el código de barras (un solo camino) y cuántas palabras lleva; en
     * blanco, solo el código y dos rayas.
     */
    private fun DrawScope.etiquetaPalabras(g: GeoTapaEspacio, e: EstadoTapa, medidor: TextMeasurer, a: AireEstanteEspacio, luz: LuzTapaEspacio) {
        val u = density; val w = size.width; val h = size.height
        val r = Rect(w * 0.52f, h * 0.72f, w * 0.825f, h * 0.845f)
        val l = luz.en(r.center)
        drawRect(a.sombra(0.3f), r.topLeft + Offset(0.7f * u, 1f * u), r.size)
        drawRect(a.etiqueta(l), r.topLeft, r.size)
        drawRect(a.claro(0.35f), r.topLeft, Size(r.width, 0.7f * u))
        // El código: barras de anchos distintos, en un camino.
        val cod = Rect(r.left + 3f * u, r.top + 3f * u, r.left + 3f * u + minOf(24f * u, r.width * 0.3f), r.bottom - 3f * u)
        trazo.reset()
        var x = cod.left; var k = 0
        while (x < cod.right) {
            val an = (0.5f + 1.2f * azar(1321, k)) * u
            trazo.addRect(Rect(x, cod.top, minOf(x + an, cod.right), cod.bottom))
            x += an + (0.6f + 0.9f * azar(1322, k)) * u; k++
        }
        drawPath(trazo, P.TINTA.copy(alpha = 0.85f))
        val tx = Rect(cod.right + 3f * u, r.top + 1f * u, r.right - 3f * u, r.bottom - 1f * u)
        if (!e.vacio && e.palabras > 0) textoEn(medidor, palabras(e.palabras), tx, Letra.codigo, P.TINTA, 9f, peso = FontWeight.Medium, minimo = 5.5f)
        else for (j in 0 until 2) drawLine(P.TINTA.copy(alpha = 0.4f), Offset(tx.left + j * 10f * u, tx.center.y), Offset(tx.left + 6f * u + j * 10f * u, tx.center.y), 1.2f * u)
    }

    // ---------------------------------------------------------------- las luces

    /**
     * Lo que alumbra encima de todo: en oscuro, el charco cálido de la lámpara sobre la tapa y el punto de su boca; en los
     * sueños, el charco frío del piloto de guardia sobre el lomo y la esquina de la tapa.
     */
    private fun DrawScope.brillos(g: GeoTapaEspacio, a: AireEstanteEspacio, luz: LuzTapaEspacio) {
        val u = density; val w = size.width
        if (!a.lumbre) return
        if (!a.noche) {
            mancha(luz.lampara + Offset(-w * 0.12f, 36f * u), w * 0.42f, P.AMBAR.copy(alpha = 0.16f))
            drawCircle(P.AMBAR.copy(alpha = 0.3f), 6f * u, luz.lampara)
            drawCircle(Color(0xFFFFF0D6), 2.4f * u, luz.lampara)
        } else {
            mancha(luz.guardia + Offset(40f * u, 0f), 120f * u, P.GUARDIA.copy(alpha = if (a.oscuro) 0.22f else 0.16f))
            drawRect(a.filo(0.7f), Offset(g.bl, g.bt + 6f * u), Size(1.2f * u, g.bb - g.bt - 12f * u))
        }
    }

    // ---------------------------------------------------------------- abierta

    /** La primera hoja, que se ve al levantar la tapa: el papel con sus renglones y la fecha arriba. */
    private fun DrawScope.paginaAbierta(g: GeoTapaEspacio, a: AireEstanteEspacio, luz: LuzTapaEspacio, e: EstadoTapa, medidor: TextMeasurer) {
        val u = density
        val r = Rect(g.lomo, g.bt + 1.5f * u, g.br - 1.5f * u, g.bb - 1f * u)
        drawRect(luz.pincel(P.PAPEL, papel = true), r.topLeft, r.size)
        var y = r.top + 26f * u
        while (y < r.bottom - 6f * u) { drawLine(P.TELA_AZUL.copy(alpha = 0.25f), Offset(r.left + 8f * u, y), Offset(r.right - 8f * u, y), 0.6f * u); y += maxOf(1f, 13f * u) }
        drawLine(P.ROJO.copy(alpha = 0.35f), Offset(r.left + 22f * u, r.top), Offset(r.left + 22f * u, r.bottom), 0.7f * u)
        textoEn(medidor, e.fecha, Rect(r.left + 26f * u, r.top + 6f * u, r.right - 10f * u, r.top + 20f * u), Letra.codigo, P.TINTA.copy(alpha = 0.7f), 9.5f, minimo = 6f)
        drawRect(a.sombra(0.3f), Offset(r.left, r.top), Size(6f * u, r.height))
    }
}
