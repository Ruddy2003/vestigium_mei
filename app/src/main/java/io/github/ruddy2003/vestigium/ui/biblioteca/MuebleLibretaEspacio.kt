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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
import kotlin.math.sqrt

/*
 * La libreta de momentos de Espacio (lo usa `EstanteEspacio`, en `BibliotecaFuturo.kt`): la carpeta de anillas de la mesa
 * del comedor del eje, donde no hay peso y todo va sujeto. Las pestañas del índice y el rótulo de cada mes son de la misma
 * carpeta (los separadores de plástico y la hoja divisoria), en `MuebleLibretaEspacioIndice.kt`. La paleta y la luz son las
 * del estante (`PaletaEstanteEspacio`, `AireEstanteEspacio`).
 */

/**
 * La luz de la mesa del comedor: de día, la de las luminarias del techo, fuera del cuadro arriba a la izquierda; en oscuro (el
 * turno de descanso), la lámpara de pinza sujeta al pasamanos arriba a la derecha, cálida, y el piloto azul de guardia arriba a
 * la izquierda, que apenas da un filo frío.
 */
private class LuzLibretaEspacio(w: Float, h: Float, private val u: Float, private val a: AireEstanteEspacio) {
    val lampara = Offset(w * 0.845f, h * 0.31f)
    val guardia = Offset(w * 0.035f, h * 0.12f)

    private val alcance = 330f * u

    /** Cuánto llega de la lámpara a [p]: se apaga con la distancia (la carpeta y la bandeja, alumbradas; la bolsa, perdida). */
    fun deLampara(p: Offset): Float = (1f - (p - lampara).getDistance() / alcance).coerceIn(0f, 1f).pow(1.8f)

    fun deGuardia(p: Offset): Float {
        val dx = (p.x - guardia.x) / u; val dy = (p.y - guardia.y) / u
        return (1f - sqrt(dx * dx * 0.6f + dy * dy) / 230f).coerceIn(0f, 1f).pow(1.5f)
    }

    fun en(p: Offset): LuzEstanteEspacio = if (!a.lumbre) LuzEstanteEspacio(1f, 0f) else LuzEstanteEspacio(deLampara(p), 0.55f * deGuardia(p))

    /** El color [c] con la luz de la lámpara en cada punto, como un pincel radial (un solo dibujo, sin escalones). De día, tal cual. */
    fun pincel(c: Color): Brush {
        if (!a.lumbre) return SolidColor(c)
        val colores = List(7) { i -> a.luz(c, LuzEstanteEspacio(deLampara(lampara + Offset(alcance * i / 6f, 0f)), 0f)) }
        return Brush.radialGradient(colores, lampara, alcance)
    }

    /** Hacia dónde cae la sombra de algo en [p], de [largo]: de día, abajo a la derecha; de noche, lejos de la lámpara. */
    fun caida(p: Offset, largo: Float): Offset {
        if (!a.lumbre) return Offset(largo, largo * 0.8f)
        val v = p - lampara
        val d = v.getDistance().coerceAtLeast(1f)
        return Offset(v.x / d * largo * 1.4f, maxOf(0.4f, v.y / d) * largo)
    }
}

/**
 * Dónde va cada cosa de la mesa, en píxeles: la pared hasta [mesa] (el borde de atrás de la mesa), la mesa hasta [canto] (su
 * borde de delante); el pasamanos a [pasa]; la carpeta: la tapa de [x0] a [x1] y de [arriba] a [frente] − [g], con el grueso
 * de las hojas [g] (más con más hojas) hasta [frente]; la goma elástica a [goma].
 */
private class GeoLibretaEspacio(w: Float, h: Float, u: Float, hojas: Int) {
    val mesa = h * 0.375f
    val canto = h * 0.865f
    val pasa = h * 0.235f
    val x0 = w * 0.115f
    val x1 = w * 0.615f
    val g = (4f + 1.35f * sqrt(hojas.coerceAtLeast(0).toFloat())).coerceAtMost(19f) * u
    val frente = h * 0.815f
    val arriba = minOf(h * 0.43f, frente - g - h * 0.25f)
    /** La goma pasa siempre por debajo de las cintas: sobre la carpeta, a 6 dp de su canto de delante. */
    val goma = frente - 6f * u
    val tapa = Rect(x0, arriba, x1, frente - g)
}

internal object MuebleLibretaEspacio {
    private val P = PaletaEstanteEspacio

    val ALTO: Dp = 266.dp

    /** La lona mostaza de la carpeta, su lomo y el forro de dentro. */
    private val MOSTAZA = Color(0xFFC9963F)
    private val LOMO = Color(0xFF9C6E2C)
    private val FORRO = Color(0xFFB9AE96)
    /** La tela de la pared del comedor (nomex blanco roto) y la mesa de aluminio anodizado gris pizarra. */
    private val NOMEX = Color(0xFFE4E0D6)
    private val MESA = Color(0xFF555C6E)
    /** La cinta de carrocero donde va escrito el nombre. */
    private val CARROCERO = Color(0xFFEADFBF)
    /** Los colores de los separadores que asoman por el canto. */
    private val SEPARADORES = listOf(Color(0xFFE8843C), Color(0xFF5E9A52), Color(0xFF6A8CE8), Color(0xFFD8B45A), Color(0xFFD24A3C), Color(0xFF8C6CC8))

    /** Los trazos que se rehacen en cada dibujo. */
    private val trazo = Path()

    fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 4f * u || h < 4f * u) return
        val a = AireEstanteEspacio(oscuro, false)
        val g = GeoLibretaEspacio(w, h, u, e.hojas)
        val luz = LuzLibretaEspacio(w, h, u, a)
        val ab = e.abierto.coerceIn(0f, 1f)
        pared(g, a, luz)
        with(PiezasLibretaEspacio) { toalla(Offset(w * 0.385f, g.pasa), g.mesa, a, luz::en, luz::caida, ab) }
        pasamanos(g, a, luz)
        with(PiezasLibretaEspacio) {
            toallaDelante(Offset(w * 0.385f, g.pasa), a, luz::en)
            bolsaMalla(Rect(w * 0.025f, g.pasa - 3f * u, w * 0.235f, g.mesa + 16f * u), a, luz::en)
        }
        mesa(g, a, luz)
        with(PiezasLibretaEspacio) { bandeja(Rect(w * 0.665f, h * 0.455f, w * 0.955f, h * 0.745f), a, luz::en, luz::caida) }
        carpeta(g, e, medidor, a, luz, ab)
        cordon(g, a, luz)
        with(PiezasLibretaEspacio) {
            tijeras(Offset(w * 0.665f, h * 0.36f), Offset(w * 0.79f, h * 0.47f), a, luz::en, luz::caida)
            gota(Offset(w * 0.575f, h * 0.345f + ab * 4f * u), 3.4f * u, a, luz::en)
            lampara(luz.lampara, g.pasa, a)
            piloto(luz.guardia, a)
        }
        brillos(g, a, luz)
        granoMueble(Rect(0f, 0f, w, h), oscuro)
    }

    // ---------------------------------------------------------------- la pared

    /**
     * La pared del comedor: paños de nomex blanco roto cosidos a lo alto (la costura doble, la tela que cede entre costuras) y
     * una tira de velcro arriba con lo que la gente deja pegado; de día, la luz de las luminarias desde arriba a la izquierda;
     * de noche, la de la lámpara, que se pierde hacia la izquierda.
     */
    private fun DrawScope.pared(g: GeoLibretaEspacio, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density; val w = size.width
        drawRect(luz.pincel(NOMEX), Offset.Zero, Size(w, g.mesa + 2f * u))
        if (!a.lumbre) {
            mancha(Offset(w * 0.18f, -g.mesa * 0.4f), w * 0.8f, luzMueble(0.4f))
            mancha(Offset(w * 0.95f, g.mesa), w * 0.5f, sombraMueble(0.16f))
        } else mancha(luz.guardia, w * 0.22f, P.GUARDIA.copy(alpha = 0.16f))
        // Los paños: entre costura y costura la tela cede (una sombra suave al lado) y lleva sus pinceladas.
        listOf(0.31f, 0.68f).forEach { fx ->
            val x = w * fx
            val l = luz.en(Offset(x, g.mesa * 0.5f))
            drawRect(a.sombra(0.12f), Offset(x + 1f * u, 0f), Size(9f * u, g.mesa))
            drawRect(a.claro(0.25f), Offset(x - 5f * u, 0f), Size(4f * u, g.mesa))
            drawLine(a.sombra(0.35f), Offset(x, 0f), Offset(x, g.mesa), 1.2f * u)
            listOf(-2.4f, 2.4f).forEach { d ->
                drawLine(a.luz(mezcla(NOMEX, P.TINTA, 0.35f), l).copy(alpha = 0.5f), Offset(x + d * u, 0f), Offset(x + d * u, g.mesa), 0.6f * u,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * u, 1.6f * u)))
            }
        }
        pinceladas(Rect(0f, 0f, w, g.mesa), a.sombra(1f), 1401, 14, u, 0.05f)
        // Un parche de velcro con los dos frascos de gotero de la sal y la pimienta (van disueltas, para que no floten).
        val yv = g.mesa * 0.22f
        val xv = w * 0.6f
        val v = Rect(xv - 3f * u, yv - 4f * u, xv + 35f * u, yv + 4f * u)
        with(PiezasEstanteEspacio) { velcro(v, a, luz.en(v.center), 1402) }
        with(PiezasLibretaEspacio) {
            frasco(Rect(xv, yv - 12f * u, xv + 14f * u, yv + 10f * u), false, 5f, a, luz::en, luz::caida)
            frasco(Rect(xv + 18f * u, yv - 11f * u, xv + 32f * u, yv + 11f * u), true, -4f, a, luz::en, luz::caida)
        }
    }

    /** El pasamanos que cruza la pared de lado a lado, de aluminio anodizado azul, con sus soportes atornillados y su sombra. */
    private fun DrawScope.pasamanos(g: GeoLibretaEspacio, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density; val w = size.width
        val y = g.pasa
        val gr = 6f * u
        val d = luz.caida(Offset(w * 0.5f, y), 4f * u)
        drawRect(a.sombra(if (a.lumbre) 0.42f else 0.24f), Offset(0f, y + gr / 2 + d.y * 0.5f), Size(w, 3.5f * u))
        drawRect(a.sombra(0.12f), Offset(0f, y + gr / 2), Size(w, 9f * u))
        drawRect(luz.pincel(mezcla(P.TELA_AZUL, P.ACERO, 0.35f)), Offset(0f, y - gr / 2), Size(w, gr))
        drawRect(a.claro(0.45f), Offset(0f, y - gr / 2 + 1f * u), Size(w, 1.2f * u))
        drawRect(a.sombra(0.3f), Offset(0f, y + gr / 2 - 1.4f * u), Size(w, 1.4f * u))
        if (a.lumbre) drawRect(a.filo(0.45f), Offset(w * 0.55f, y - gr / 2), Size(w * 0.45f, 1f * u))
        listOf(w * 0.12f, w * 0.5f, w * 0.93f).forEach { x ->
            val l = luz.en(Offset(x, y))
            drawRoundRect(a.sombra(0.3f), Offset(x - 5f * u + d.x, y - 9f * u + d.y), Size(10f * u, 18f * u), CornerRadius(2f * u))
            drawRoundRect(a.luz(P.ACERO, l), Offset(x - 5f * u, y - 9f * u), Size(10f * u, 18f * u), CornerRadius(2f * u))
            drawRect(a.claro(0.45f), Offset(x - 4.4f * u, y - 8.4f * u), Size(1f * u, 16.8f * u))
            remacheEspacio(Offset(x, y - 6f * u), 1.1f * u, a.luz(P.CASCO, l), a)
            remacheEspacio(Offset(x, y + 6f * u), 1.1f * u, a.luz(P.CASCO, l), a)
        }
    }

    // ---------------------------------------------------------------- la mesa

    /**
     * La mesa del comedor: la cara de arriba de aluminio anodizado gris pizarra, rayada, con sus cuadros de velcro donde se
     * pegan las bandejas; la sombra que la pared le deja atrás, el canto de delante con su riel de anclajes y, debajo, la
     * penumbra con el cable que corre bajo la mesa.
     */
    private fun DrawScope.mesa(g: GeoLibretaEspacio, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density; val w = size.width; val h = size.height
        drawRect(luz.pincel(MESA), Offset(0f, g.mesa), Size(w, g.canto - g.mesa))
        if (!a.lumbre) {
            mancha(Offset(w * 0.25f, g.mesa), w * 0.65f, luzMueble(0.16f))
            drawRect(sombraMueble(0.14f), Offset(0f, g.canto - 10f * u), Size(w, 10f * u))
        }
        // La junta con la pared: la sombra honda donde se encuentran.
        drawRect(a.sombra(0.4f), Offset(0f, g.mesa), Size(w, 3f * u))
        drawRect(a.sombra(0.15f), Offset(0f, g.mesa), Size(w, 9f * u))
        // Las rayas y el roce de las bandejas.
        pinceladas(Rect(0f, g.mesa, w, g.canto), a.claro(1f), 1411, 18, u, 0.06f)
        for (k in 0 until 7) {
            val x0 = w * azar(1412, k); val y0 = g.mesa + (g.canto - g.mesa) * (0.2f + 0.7f * azar(1413, k))
            drawLine(a.claro(0.12f), Offset(x0, y0), Offset(x0 + w * (0.04f + 0.05f * azar(1414, k)), y0 + 1.5f * u * (azar(1415, k) - 0.5f)), 0.6f * u)
        }
        // Los cuadros de velcro (aplanados: la mesa se ve desde arriba y de lado).
        listOf(Offset(0.68f, 0.43f), Offset(0.04f, 0.6f), Offset(0.6f, 0.8f), Offset(0.96f, 0.82f)).forEachIndexed { i, f ->
            val c = Offset(w * f.x, h * f.y)
            val r = Rect(c.x - 9f * u, c.y - 4.5f * u, c.x + 9f * u, c.y + 4.5f * u)
            drawRect(a.luz(mezcla(P.ACERO, P.GOMA, 0.55f), luz.en(c)), r.topLeft, r.size)
            for (j in 0 until 6) drawCircle(a.sombra(0.3f), 0.5f * u, Offset(r.left + r.width * azar(1420 + i, j), r.top + r.height * azar(1430 + i, j)))
            drawRect(a.claro(0.2f), r.topLeft, Size(r.width, 0.7f * u))
        }
        // El canto de delante: el perfil de aluminio con su riel de anclajes.
        val c0 = g.canto; val c1 = minOf(h, g.canto + 9f * u)
        drawRect(luz.pincel(P.ACERO), Offset(0f, c0), Size(w, c1 - c0))
        drawRect(a.claro(0.55f), Offset(0f, c0), Size(w, 1f * u))
        if (a.lumbre) drawRect(a.filo(0.5f), Offset(w * 0.4f, c0), Size(w * 0.6f, 1f * u))
        val ry = c0 + (c1 - c0) * 0.5f
        drawRect(a.sombra(0.5f), Offset(0f, ry - 0.8f * u), Size(w, 1.6f * u))
        trazo.reset()
        var x = 5f * u
        while (x < w) { trazo.addOval(Rect(Offset(x, ry), 1.6f * u)); x += maxOf(1f, 11f * u) }
        drawPath(trazo, a.sombra(0.55f))
        // Debajo de la mesa: la penumbra y el cable que corre por debajo, con una brida.
        if (c1 < h) {
            drawRect(a.luz(mezcla(MESA, P.GOMA, 0.6f), LuzEstanteEspacio(0.12f, 0.1f)), Offset(0f, c1), Size(w, h - c1))
            drawRect(a.sombra(0.5f), Offset(0f, c1), Size(w, 3f * u))
            val yc = c1 + (h - c1) * 0.55f
            drawLine(a.luz(P.GOMA, LuzEstanteEspacio(0.3f, 0.1f)), Offset(0f, yc), Offset(w, yc + 1f * u), 2.4f * u)
            drawLine(a.claro(0.2f), Offset(0f, yc - 0.8f * u), Offset(w, yc + 0.2f * u), 0.6f * u)
        }
    }

    // ---------------------------------------------------------------- la carpeta

    /**
     * Dibuja [bloque] girado sobre las anillas, en [x0], visto desde arriba y de frente: x' = x0 + sx·(x − x0),
     * y' = y + ky·(x − x0) (el canto libre se acerca a las anillas y se levanta).
     */
    private inline fun DrawScope.girada(x0: Float, sx: Float, ky: Float, crossinline bloque: DrawScope.() -> Unit) {
        withTransform({ transform(Matrix(floatArrayOf(sx, ky, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, x0 * (1f - sx), -ky * x0, 0f, 1f))) }) { bloque() }
    }

    /**
     * La carpeta de anillas de la mesa, la «bitácora de vuelo» del comedor: tumbada, sujeta por la goma elástica que cruza la
     * mesa. La sombra en la mesa, el canto de delante con sus hojas (más grueso con más hojas), los separadores de plástico que
     * asoman por la derecha (más con más hojas), la tapa de lona mostaza con el lomo de las anillas y, en la tapa, el nombre y
     * la cuenta escritos con rotulador en dos tiras de cinta de carrocero. Al abrirse, la tapa gira sobre las anillas y se ve
     * la hoja de arriba, la de la órbita del día.
     */
    private fun DrawScope.carpeta(g: GeoLibretaEspacio, e: EstadoLibreta, medidor: TextMeasurer, a: AireEstanteEspacio, luz: LuzLibretaEspacio, ab: Float) {
        val u = density
        val t = g.tapa
        val l = luz.en(t.center)
        // La sombra en la mesa.
        val d = luz.caida(t.center, 6f * u)
        drawRoundRect(a.sombra(if (a.oscuro) 0.5f else 0.3f), Offset(t.left + d.x, t.top + d.y + g.g * 0.6f), Size(t.width + 2f * u, t.height + g.g), CornerRadius(5f * u))
        separadores(g, e.hojas, a, luz)
        cantoDelante(g, a, luz, e.semilla)
        if (ab > 0.02f) hojaDeArriba(g, e, medidor, a, luz)
        val giro = 0.92f * PI.toFloat() * ((ab - 0.08f) / 0.92f).coerceIn(0f, 1f).pow(0.75f)
        if (giro <= 0.001f) tapaCarpeta(g, e, medidor, a, luz)
        else {
            val x0 = t.left + 9f * u
            val co = cos(giro); val si = sin(giro)
            if (co >= 0f) girada(x0, co, -si * 0.18f) {
                tapaCarpeta(g, e, medidor, a, luz)
                drawRect(a.sombra(0.45f * (1f - co)), t.topLeft, t.size)
            } else girada(x0, -co, si * 0.18f) {
                // El forro, ya del otro lado de las anillas (se dibuja a la izquierda, sin espejo).
                val r = Rect(x0 - (t.right - x0), t.top, x0, t.bottom)
                drawRoundRect(a.luz(FORRO, l), r.topLeft, r.size, CornerRadius(5f * u))
                drawRect(a.sombra(0.35f * (1f + co)), r.topLeft, r.size)
            }
        }
        anillas(g, a, luz, giro)
    }

    /** Los separadores de plástico de colores que asoman por el canto derecho, tumbados en la mesa: más cuantas más hojas. */
    private fun DrawScope.separadores(g: GeoLibretaEspacio, hojas: Int, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density
        val t = g.tapa
        val cuantos = (1 + hojas / 12).coerceIn(1, 7)
        val alto = (t.height - 8f * u) / 7f
        for (q in 0 until cuantos) {
            val y = t.top + 4f * u + alto * q + g.g * (q.toFloat() / 7f)
            val r = Rect(t.right - 4f * u, y, t.right + 9f * u, y + alto * 0.78f)
            val l = luz.en(r.center)
            drawRoundRect(a.sombra(0.3f), r.topLeft + Offset(1f * u, 1.2f * u), r.size, CornerRadius(2.5f * u))
            drawRoundRect(a.animo(SEPARADORES[q % SEPARADORES.size], l), r.topLeft, r.size, CornerRadius(2.5f * u))
            drawRect(a.claro(0.4f), Offset(r.left + 4f * u, r.top + 0.8f * u), Size(r.width - 6f * u, 0.8f * u))
            drawRect(a.etiqueta(l).copy(alpha = 0.85f), Offset(r.left + 5.5f * u, r.top + r.height * 0.3f), Size(r.width - 8f * u, r.height * 0.4f))
        }
    }

    /** El canto de delante: la contratapa, el bloque de hojas con sus rayitas y la tapa de arriba, de canto. */
    private fun DrawScope.cantoDelante(g: GeoLibretaEspacio, a: AireEstanteEspacio, luz: LuzLibretaEspacio, semilla: Int) {
        val u = density
        val r = Rect(g.x0, g.frente - g.g, g.x1, g.frente)
        val l = luz.en(r.center)
        val lona = a.luz(MOSTAZA, l)
        drawRect(mezcla(lona, a.hondo, 0.35f), Offset(r.left, r.bottom - 2.4f * u), Size(r.width, 2.4f * u))
        val hojas = Rect(r.left + 8f * u, r.top + 1.6f * u, r.right - 2.5f * u, r.bottom - 2.4f * u)
        if (hojas.height > 0.5f) {
            drawRect(a.luz(mezcla(P.PAPEL, P.TELA, 0.25f), l), hojas.topLeft, hojas.size)
            var y = hojas.top + 1f * u; var k = 0
            while (y < hojas.bottom - 0.3f * u) {
                drawLine(a.sombra(0.1f + 0.14f * azar(semilla, k)), Offset(hojas.left + 1.5f * u * azar(semilla, k + 50), y), Offset(hojas.right - 1.5f * u * azar(semilla, k + 90), y), 0.5f * u)
                y += maxOf(1f, (1.05f + 0.5f * azar(semilla, k + 130)) * u); k++
            }
            drawRect(a.sombra(0.2f), Offset(hojas.left, hojas.bottom - 0.8f * u), Size(hojas.width, 0.8f * u))
        }
        // El lomo de las anillas, redondo, a la izquierda.
        drawRoundRect(a.luz(LOMO, l), Offset(r.left, r.top - 2f * u), Size(10f * u, r.height + 2f * u), CornerRadius(4f * u))
        drawRect(a.claro(0.3f), Offset(r.left + 2f * u, r.top), Size(1.2f * u, r.height - 2f * u))
        drawRect(mezcla(lona, a.hondo, 0.15f), Offset(r.left, r.top - 1.6f * u), Size(r.width, 2.4f * u))
    }

    /**
     * La tapa de la carpeta: la lona mostaza con su trama y su desgaste, el lomo de las anillas a la izquierda, la costura, la
     * luz de arriba a la izquierda (de día) o de la lámpara (de noche), y las dos tiras de cinta con el nombre y la cuenta.
     */
    private fun DrawScope.tapaCarpeta(g: GeoLibretaEspacio, e: EstadoLibreta, medidor: TextMeasurer, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density
        val t = g.tapa
        drawRoundRect(luz.pincel(MOSTAZA), t.topLeft, t.size, CornerRadius(5f * u))
        clipRect(t.left, t.top, t.right, t.bottom) {
            // La trama de la lona, en un solo camino.
            trazo.reset()
            var x = t.left + 1.2f * u; var k = 0
            while (x < t.right) { trazo.moveTo(x, t.top); trazo.lineTo(x, t.bottom); x += maxOf(1f, (1.8f + 1.2f * azar(1440, k)) * u); k++ }
            drawPath(trazo, a.sombra(0.07f), style = Stroke(0.5f * u))
            if (!a.lumbre) {
                mancha(Offset(t.left + t.width * 0.2f, t.top), t.width * 0.6f, luzMueble(0.24f))
                drawRect(sombraMueble(0.1f), Offset(t.right - t.width * 0.1f, t.top), Size(t.width * 0.1f, t.height))
            }
            pinceladas(Rect(t.left, t.bottom - 9f * u, t.right, t.bottom), a.claro(1f), e.semilla + 11, 8, u, 0.1f)
            pinceladas(Rect(t.right - 24f * u, t.top, t.right, t.bottom), a.claro(1f), e.semilla + 12, 5, u, 0.08f)
            pinceladas(Rect(t.left + t.width * 0.4f, t.top + t.height * 0.6f, t.right, t.bottom), a.sombra(1f), e.semilla + 13, 4, u, 0.08f)
        }
        val lc = luz.en(t.center)
        drawRoundRect(a.luz(mezcla(P.TELA, P.PAPEL, 0.3f), lc).copy(alpha = 0.55f), Offset(t.left + 13f * u, t.top + 3.5f * u), Size(t.width - 17f * u, t.height - 7f * u),
            CornerRadius(2f * u), style = Stroke(0.7f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.2f * u, 1.6f * u))))
        // El lomo de las anillas: la lona más oscura y abombada.
        val lomo = Rect(t.left, t.top, t.left + 10f * u, t.bottom)
        drawRoundRect(a.luz(LOMO, luz.en(lomo.center)), lomo.topLeft, lomo.size, CornerRadius(5f * u))
        drawRect(a.claro(0.25f), Offset(lomo.left + 2.4f * u, lomo.top + 3f * u), Size(1.2f * u, lomo.height - 6f * u))
        drawRect(a.sombra(0.35f), Offset(lomo.right - 1f * u, lomo.top), Size(1.4f * u, lomo.height))
        // El filo de la lámpara en el canto de arriba y en el de la derecha.
        if (a.lumbre) {
            val kk = luz.en(Offset(t.right, t.top)).k
            drawRect(a.filo(0.6f * kk), Offset(t.left + 6f * u, t.top), Size(t.width - 10f * u, 1f * u))
            drawRect(a.filo(0.55f * kk), Offset(t.right - 1.1f * u, t.top + 5f * u), Size(1.1f * u, t.height - 10f * u))
        }
        rotulos(t, e, medidor, a, luz)
    }

    /** Las dos tiras de cinta de carrocero, rotas a mano en los extremos: el nombre, grande, y debajo la cuenta. */
    private fun DrawScope.rotulos(t: Rect, e: EstadoLibreta, medidor: TextMeasurer, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density
        val izq = t.left + 18f * u
        val der = t.right - 12f * u
        val r1 = Rect(izq, t.top + t.height * 0.17f, der, t.top + t.height * 0.17f + minOf(25f * u, t.height * 0.3f))
        val r2 = Rect(izq + 6f * u, r1.bottom + 6f * u, der - 2f * u, r1.bottom + 6f * u + minOf(18f * u, t.height * 0.22f))
        cinta(r1, -1.6f, a, luz, 1441)
        cinta(r2, 1.1f, a, luz, 1442)
        rotate(-1.6f, r1.center) {
            textoEn(medidor, e.nombre, Rect(r1.left + 7f * u, r1.top + 1f * u, r1.right - 7f * u, r1.bottom - 1f * u), LetraExtra.grotesk, P.TINTA, 14f, peso = FontWeight.SemiBold, minimo = 6f)
        }
        rotate(1.1f, r2.center) {
            textoEn(medidor, e.cuenta, Rect(r2.left + 6f * u, r2.top + 1f * u, r2.right - 6f * u, r2.bottom - 1f * u), LetraExtra.grotesk, P.TINTA.copy(alpha = 0.88f), 10f, peso = FontWeight.Medium, minimo = 5f)
        }
    }

    /** Una tira de cinta de carrocero en [r], girada [giro]°, con los extremos rotos en zigzag, su sombra y su brillo. */
    private fun DrawScope.cinta(r: Rect, giro: Float, a: AireEstanteEspacio, luz: LuzLibretaEspacio, semilla: Int) {
        val u = density
        val l = luz.en(r.center)
        rotate(giro, r.center) {
            trazo.reset()
            trazo.moveTo(r.left, r.top)
            trazo.lineTo(r.right, r.top)
            val dientes = 4
            for (k in 1..dientes) trazo.lineTo(r.right + (if (k % 2 == 1) -2f else 0.5f) * u * (0.6f + azar(semilla, k)), r.top + r.height * k / dientes)
            trazo.lineTo(r.left, r.bottom)
            for (k in dientes - 1 downTo 1) trazo.lineTo(r.left + (if (k % 2 == 1) 2f else -0.5f) * u * (0.6f + azar(semilla, k + 9)), r.top + r.height * k / dientes)
            trazo.close()
            translate(0.8f * u, 1.2f * u) { drawPath(trazo, a.sombra(0.3f)) }
            drawPath(trazo, a.etiqueta(l).let { mezcla(it, a.luz(CARROCERO, l), 0.6f) })
            drawRect(a.claro(0.4f), Offset(r.left + 2f * u, r.top + 0.8f * u), Size(r.width - 6f * u, 0.8f * u))
            pinceladas(r, a.sombra(1f), semilla, 4, u, 0.06f)
        }
    }

    /** Las anillas: los dos remaches del mecanismo en el lomo y la palanca, que siguen en su sitio al abrir. */
    private fun DrawScope.anillas(g: GeoLibretaEspacio, a: AireEstanteEspacio, luz: LuzLibretaEspacio, giro: Float) {
        val u = density
        val t = g.tapa
        listOf(0.22f, 0.78f).forEach { f ->
            val c = Offset(t.left + 5f * u, t.top + t.height * f)
            remacheEspacio(c, 1.9f * u, a.luz(P.ACERO, luz.en(c)), a)
        }
        if (giro > 0.001f) {
            // Al abrir se ven los dos aros sobre el lomo.
            listOf(0.3f, 0.7f).forEach { f ->
                val c = Offset(t.left + 9f * u, t.top + t.height * f)
                drawArc(a.luz(P.ACERO, luz.en(c)), 90f, 180f, false, Offset(c.x - 5f * u, c.y - 7f * u), Size(10f * u, 14f * u), style = Stroke(2.2f * u))
                drawArc(a.claro(0.5f), 150f, 60f, false, Offset(c.x - 5f * u, c.y - 7f * u), Size(10f * u, 14f * u), style = Stroke(0.8f * u))
            }
        }
    }

    /** La hoja de arriba, al abrir: la órbita del día dibujada a mano, el registro de horas y la fecha. */
    private fun DrawScope.hojaDeArriba(g: GeoLibretaEspacio, e: EstadoLibreta, medidor: TextMeasurer, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density
        val t = g.tapa
        val r = Rect(t.left + 10f * u, t.top + 2f * u, t.right - 3f * u, t.bottom - 1f * u)
        val l = luz.en(r.center)
        drawRect(a.etiqueta(l), r.topLeft, r.size)
        listOf(0.25f, 0.5f, 0.75f).forEach { f -> drawCircle(a.sombra(0.5f), 2.4f * u, Offset(r.left + 6f * u, r.top + r.height * f)) }
        var y = r.top + 14f * u
        while (y < r.bottom - 4f * u) { drawLine(P.TELA_AZUL.copy(alpha = 0.25f), Offset(r.left + 14f * u, y), Offset(r.right - 6f * u, y), 0.6f * u); y += maxOf(1f, 9f * u) }
        val pc = Offset(r.left + r.width * 0.3f, r.center.y + 4f * u)
        val pr = minOf(r.height * 0.14f, 10f * u)
        drawCircle(a.luz(P.TELA_AZUL, l), pr, pc)
        drawOval(P.TINTA.copy(alpha = 0.6f), Offset(pc.x - pr * 2.6f, pc.y - pr * 1.1f), Size(pr * 5.2f, pr * 2.2f),
            style = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 1.6f * u))))
        textoEn(medidor, e.cuenta, Rect(r.left + r.width * 0.5f, r.top + 3f * u, r.right - 6f * u, r.top + 13f * u), LetraExtra.grotesk, P.TINTA.copy(alpha = 0.7f), 8f, minimo = 5f)
    }

    /**
     * La goma elástica negra que cruza la mesa de lado a lado y sujeta la carpeta y la bandeja: sobre la mesa va a su altura;
     * sobre la carpeta sube lo que mide su grueso y aprieta la tapa. Trenzada, con su brillo y su sombra.
     */
    private fun DrawScope.cordon(g: GeoLibretaEspacio, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density; val w = size.width
        val y = g.goma
        val sube = g.g
        trazo.reset()
        trazo.moveTo(-2f * u, y)
        trazo.lineTo(g.x0 - 6f * u, y)
        trazo.lineTo(g.x0 + 2f * u, y - sube)
        trazo.lineTo(g.x1 - 2f * u, y - sube)
        trazo.lineTo(g.x1 + 6f * u, y)
        trazo.lineTo(w + 2f * u, y)
        val d = luz.caida(Offset(w * 0.4f, y), 2.5f * u)
        translate(d.x * 0.4f, d.y + 1.2f * u) { drawPath(trazo, a.sombra(0.4f), style = Stroke(3.4f * u)) }
        drawPath(trazo, luz.pincel(P.GOMA), style = Stroke(3.4f * u))
        drawPath(trazo, a.luz(mezcla(P.GOMA, P.ACERO, 0.25f), luz.en(Offset(w * 0.5f, y))).copy(alpha = 0.6f),
            style = Stroke(3.4f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.6f * u, 1.4f * u))))
        translate(0f, -1.1f * u) { drawPath(trazo, a.claro(0.3f), style = Stroke(0.7f * u)) }
        if (a.lumbre) translate(0f, -1.2f * u) { drawPath(trazo, a.filo(0.4f * luz.en(Offset(w * 0.6f, y)).k), style = Stroke(0.8f * u)) }
    }

    /** El charco de la lámpara encima de todo, en oscuro (sin velo: una mancha cálida donde cae). */
    private fun DrawScope.brillos(g: GeoLibretaEspacio, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val w = size.width
        if (!a.lumbre) return
        mancha(Offset(luz.lampara.x - w * 0.22f, (g.mesa + g.canto) * 0.5f), w * 0.42f, P.AMBAR.copy(alpha = 0.12f))
    }
}
