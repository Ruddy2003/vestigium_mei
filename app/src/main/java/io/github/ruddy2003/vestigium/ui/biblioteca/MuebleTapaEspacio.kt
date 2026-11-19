package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
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
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/*
 * El libro del día de Espacio (lo usa `EstanteEspacio`, en `BibliotecaFuturo.kt`): la bitácora de tela de hoy, la misma clase
 * de libreta de papel que luego se guarda en el estante (allá, alta y delgada; aquí, apaisada y grande), sujeta a la pared de
 * la cabina de la litera. La paleta y la luz son las del estante (`PaletaEstanteEspacio`, `AireEstanteEspacio`): misma nave,
 * otro rincón. Las piezas chicas están en `MuebleTapaEspacioPiezas.kt` y la trama de la lona, en `MuebleEspacioTelas.kt`.
 */

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
    /** El papel del canto de las hojas. */
    private val HOJAS = Color(0xFFE9E0C8)

    /** Los trazos que se rehacen en cada dibujo (en el hilo de la interfaz, de uno en uno). */
    private val trazo = Path()
    private val contorno = Path()
    /** Los mapas de luz de la escena y de la tarjeta, pintados una vez por tamaño y por modo. */
    private val mapaEscena = MapaLuzEspacio(56, 28)
    private val mapaPapel = MapaLuzEspacio(36, 12)

    // ---------------------------------------------------------------- el dibujo

    fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 4f * u || h < 4f * u) return
        val a = AireTapaEspacio(AireEstanteEspacio(oscuro, e.noche))
        val g = GeoTapaEspacio(w, h, u, e.palabras, e.vacio)
        val luz = LuzTapaEspacio(w, h, u, a)
        val ab = e.abierto.coerceIn(0f, 1f)
        val escena = mapaEscena.de(claveMapaEspacio(w, h, luz.modo)) { fx, fy -> luz.colorEscena(fx, fy) }
        // Lo de atrás, con la luz entera; encima, el mapa de luz que lo apaga con la distancia a la lámpara (o a la tira).
        luz.plano = true
        pared(g, a, luz)
        with(PiezasTapaEspacio) { mazo(a, luz) }
        sombraLibro(g, a, luz)
        canto(g, a, luz, e.semilla)
        ponerMapaEspacio(escena, Rect(0f, 0f, w, h), luz.mezclaMapa)
        luz.plano = false
        if (ab > 0.02f) paginaAbierta(g, a, luz, e, medidor)
        val giro = (0.92f * PI.toFloat() * ((ab - 0.12f) / 0.88f).coerceIn(0f, 1f).pow(0.8f))
        cubierta(g, giro, a) {
            lona(g, a, luz, e.semilla, escena)
            parche(Offset(g.lomo + 17f * u, h * 0.175f), 12.5f * u, a, luz)
            rotulo(g, e, medidor, a, luz)
            ventanilla(g, a, luz)
            etiquetaPalabras(e, medidor, a, luz)
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

    // ---------------------------------------------------------------- la pared

    /**
     * La pared de la cabina: chapa pintada de crema con su junta y sus remaches, la pintura gastada y el cable de la lámpara
     * que baja por la derecha sujeto con dos lengüetas de velcro. Se pinta solo donde el libro no la tapa (el resto no se ve y
     * pintarlo costaba). De día, la luz de las luminarias desde arriba a la izquierda; de noche, la de la lámpara o la tira.
     */
    private fun DrawScope.pared(g: GeoTapaEspacio, a: AireTapaEspacio, luz: LuzTapaEspacio) {
        val u = density; val w = size.width; val h = size.height
        val pincel = luz.pincel(PARED, Rect(0f, 0f, w, h))
        val solapa = 6f * u
        val der = g.br + g.fondo.x
        val abajo = g.bb + g.fondo.y
        // Los cuatro márgenes en un solo camino, con un solo pincel.
        trazo.reset()
        trazo.addRect(Rect(0f, 0f, w, g.bt + solapa))
        trazo.addRect(Rect(0f, abajo - solapa, w, h))
        trazo.addRect(Rect(0f, g.bt, g.bl + solapa, abajo))
        trazo.addRect(Rect(der - solapa, g.bt, w, abajo))
        drawPath(trazo, pincel)
        // La junta de la chapa, a la derecha, con su fila de remaches; y el desgaste de la pintura.
        val xj = w * 0.952f
        drawRect(a.sombra(0.3f), Offset(xj, 0f), Size(1.4f * u, h))
        drawRect(a.claro(0.4f), Offset(xj + 1.4f * u, 0f), Size(0.8f * u, h))
        var y = 22f * u
        while (y < h - 6f * u) {
            remacheEspacio(Offset(xj - 5f * u, y), 1.2f * u, a.luz(mezcla(PARED, P.ACERO, 0.45f), luz.en(Offset(xj, y))), a.base)
            y += maxOf(1f, 26f * u)
        }
        pinceladas(Rect(der, 0f, w, h), a.sombra(1f), 1301, 5, u, 0.07f)
        drawLine(a.sombra(0.12f), Offset(w * 0.1f, h - 4f * u), Offset(w * 0.55f, h - 3f * u), 2f * u, StrokeCap.Round)
        // El cable de la lámpara, que baja pegado a la pared con dos lengüetas de velcro.
        val xc = w - 7f * u
        val lc = luz.en(Offset(xc, h * 0.5f))
        drawLine(a.sombra(0.3f), Offset(xc + 1.5f * u, 12f * u), Offset(xc + 1.5f * u, h), 2f * u)
        drawLine(a.luz(P.GOMA, lc), Offset(xc, 10f * u), Offset(xc, h), 1.8f * u)
        listOf(0.5f, 0.84f).forEach { f ->
            val t = Rect(xc - 4.5f * u, h * f - 3f * u, xc + 4.5f * u, h * f + 3f * u)
            drawRect(a.sombra(0.3f), t.topLeft + Offset(0.8f * u, 1f * u), t.size)
            drawRect(a.luz(mezcla(P.ACERO, P.GOMA, 0.45f), luz.en(t.center)), t.topLeft, t.size)
        }
    }

    // ---------------------------------------------------------------- el libro

    /** La sombra del libro en la pared, del color de la sombra del momento y corrida lejos de la luz. */
    private fun DrawScope.sombraLibro(g: GeoTapaEspacio, a: AireTapaEspacio, luz: LuzTapaEspacio) {
        val u = density
        // Solo lo que asoma por fuera del libro (pintar la sombra entera, tapada por el libro, costaba): un marco hueco corrido.
        val d = luz.caida(g.libro.center, 5f * u)
        val r = Rect(g.bl, g.bt, g.br + g.fondo.x, g.bb + g.fondo.y)
        listOf(1.6f to (if (a.oscuro) 0.5f else 0.24f), 0.7f to (if (a.oscuro) 0.4f else 0.2f)).forEach { (k, al) ->
            val s = r.translate(d * k)
            trazo.reset()
            trazo.addRoundRect(androidx.compose.ui.geometry.RoundRect(s, CornerRadius(5f * u)))
            trazo.addRect(r.deflate(3f * u))
            trazo.fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
            drawPath(trazo, a.sombra(al))
        }
        trazo.fillType = androidx.compose.ui.graphics.PathFillType.NonZero
    }

    /**
     * El grueso del libro, visto un poco de lado: la cara de la derecha y la de abajo, con el canto de las hojas (una raya por
     * cada pliego, en un solo camino) entre la tapa y la contratapa, que asoma con su canto de cartón. Engorda con lo escrito.
     */
    private fun DrawScope.canto(g: GeoTapaEspacio, a: AireTapaEspacio, luz: LuzTapaEspacio, semilla: Int) {
        val u = density
        val d = g.fondo
        val ri = 4f * u
        val pincel = luz.pincel(HOJAS, Rect(g.br, g.bt, g.br + d.x, g.bb + d.y))
        // La contratapa: solo se ve su canto de cartón forrado, por fuera del bloque (lo demás lo tapa la tapa).
        val carton = a.luz(mezcla(LOMO, P.GOMA, 0.3f), luz.en(Offset(g.br, g.bb)))
        drawRoundRect(carton, Offset(g.br + d.x - 2f * u, g.bt + d.y + ri * 0.5f), Size(3.4f * u, g.bb - g.bt), CornerRadius(1.5f * u))
        drawRoundRect(carton, Offset(g.bl + d.x + ri * 0.5f, g.bb + d.y - 2f * u), Size(g.br - g.bl, 3.4f * u), CornerRadius(1.5f * u))
        // Las dos caras del bloque de hojas.
        poligonoEnEspacio(trazo, g.br - 1f * u, g.bt + ri, g.br + d.x, g.bt + d.y + ri, g.br + d.x, g.bb + d.y, g.br - 1f * u, g.bb)
        drawPath(trazo, pincel)
        poligonoEnEspacio(trazo, g.bl + ri, g.bb - 1f * u, g.br, g.bb - 1f * u, g.br + d.x, g.bb + d.y, g.bl + d.x + ri, g.bb + d.y)
        drawPath(trazo, pincel)
        // Los pliegos: rayas a lo largo de cada cara, más juntas y desparejas.
        trazo.reset()
        val n = (g.g / (1.05f * u)).toInt().coerceIn(2, 14)
        for (i in 1 until n) {
            val t = i / n.toFloat() + (azar(semilla, i) - 0.5f) * 0.3f / n
            trazo.moveTo(g.br + d.x * t, g.bt + d.y * t + ri + 1f * u); trazo.lineTo(g.br + d.x * t, g.bb + d.y * t)
            trazo.moveTo(g.bl + d.x * t + ri, g.bb + d.y * t); trazo.lineTo(g.br + d.x * t, g.bb + d.y * t)
        }
        drawPath(trazo, a.sombra(0.22f), style = Stroke(0.5f * u))
        // La sombra de la cara de abajo (mira al suelo) y el filo de la de la derecha.
        poligonoEnEspacio(trazo, g.bl + ri, g.bb, g.br, g.bb, g.br + d.x, g.bb + d.y, g.bl + d.x + ri, g.bb + d.y)
        drawPath(trazo, a.sombra(0.16f))
        if (a.lumbre && !a.noche) drawLine(a.filo(0.6f * luz.en(Offset(g.br, g.bt)).k), Offset(g.br + d.x, g.bt + d.y + ri), Offset(g.br + d.x, g.bb + d.y), 1f * u)
    }

    /** Dibuja [bloque] girado sobre el eje vertical en [x0], visto de frente: x' = x0 + sx·(x − x0), y' = y + ky·(x − x0). */
    private inline fun DrawScope.girada(x0: Float, sx: Float, ky: Float, crossinline bloque: DrawScope.() -> Unit) {
        withTransform({ transform(Matrix(floatArrayOf(sx, ky, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, x0 * (1f - sx), -ky * x0, 0f, 1f))) }) { bloque() }
    }

    /**
     * La tapa delantera: cerrada, dibuja [fuera] tal cual; al abrirse gira sobre el lomo (el canto libre se acerca y sube un
     * poco) y, ya vuelta, se ve el forro de dentro a la izquierda del lomo (dibujado ya a la izquierda, sin espejo).
     */
    private inline fun DrawScope.cubierta(g: GeoTapaEspacio, giro: Float, a: AireTapaEspacio, crossinline fuera: DrawScope.() -> Unit) {
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
     * El contorno de la tapa, cosida a mano: esquinas de radios distintos y lados apenas combados, siempre los mismos para la
     * misma bitácora (en [contorno]).
     */
    private fun contornoTapa(r: Rect, s: Int, u: Float): Path {
        val c = contorno
        c.reset()
        val rd = FloatArray(4) { (3f + 2.6f * azar(s, 40 + it)) * u }
        val b = FloatArray(4) { (azar(s, 50 + it) - 0.5f) * 1.6f * u }
        c.moveTo(r.left + rd[0], r.top)
        c.quadraticTo(r.center.x, r.top + b[0], r.right - rd[1], r.top)
        c.quadraticTo(r.right, r.top, r.right, r.top + rd[1])
        c.quadraticTo(r.right + b[1], r.center.y, r.right, r.bottom - rd[2])
        c.quadraticTo(r.right, r.bottom, r.right - rd[2], r.bottom)
        c.quadraticTo(r.center.x, r.bottom + b[2], r.left + rd[3], r.bottom)
        c.quadraticTo(r.left, r.bottom, r.left, r.bottom - rd[3])
        c.quadraticTo(r.left + b[3], r.center.y, r.left, r.top + rd[0])
        c.quadraticTo(r.left, r.top, r.left + rd[0], r.top)
        c.close()
        return c
    }

    /** Las puntadas a mano a lo largo de los cuatro lados de [r] (en [trazo]). */
    private fun puntadas(r: Rect, s: Int, u: Float): Path = puntadasEspacio(trazo, r, s, u)

    /**
     * La lona de la tapa: el color, la trama (una baldosa), el lomo, el desgaste del canto y de las esquinas, la grasa donde se
     * agarra, la costura a mano, la bisagra y las esquineras de aluminio, todo con la luz entera; encima, recortado a la tapa,
     * el mapa de luz ([escena]); y al final el filo de la luz que manda en los cantos que la miran.
     */
    private fun DrawScope.lona(g: GeoTapaEspacio, a: AireTapaEspacio, luz: LuzTapaEspacio, semilla: Int, escena: ImageBitmap) {
        val u = density
        val r = g.libro
        val forma = contornoTapa(r, semilla, u)
        luz.plano = true
        drawPath(forma, a.luz(LONA, luz.en(r.center)))
        with(TelasEspacio) { trama(forma, if (a.oscuro) 0.45f else 0.7f) }
        // El lomo: la lona más oscura, con su trama, gastada a lo largo.
        val lomo = Rect(r.left, r.top, g.lomo, r.bottom)
        drawRoundRect(a.luz(LOMO, luz.en(lomo.center)), lomo.topLeft, lomo.size, CornerRadius(4f * u))
        with(TelasEspacio) { trama(lomo, if (a.oscuro) 0.35f else 0.55f) }
        drawLine(a.claro(0.22f), Offset(lomo.left + 2.2f * u, lomo.top + 5f * u), Offset(lomo.left + 2.6f * u, lomo.bottom - 5f * u), 1.6f * u)
        // El desgaste: el canto de la lona más claro y pelado (sobre todo en las esquinas del lomo), y la grasa de las manos
        // junto al canto, donde se abre.
        drawPath(forma, a.claro(0.2f), style = Stroke(2.4f * u))
        listOf(Offset(r.left + 2f * u, r.top + 2f * u), Offset(r.left + 2f * u, r.bottom - 2f * u)).forEach { c -> drawCircle(a.claro(0.25f), 4.5f * u, c) }
        pinceladas(Rect(r.right - r.width * 0.16f, r.top + r.height * 0.25f, r.right - 2f * u, r.bottom - r.height * 0.15f), a.sombra(1f), semilla + 33, 4, u, 0.12f)
        // La costura a mano, a 4 dp del borde.
        val hilo = a.luz(mezcla(P.TELA, P.TRIGO, 0.25f), luz.en(r.center)).copy(alpha = 0.6f)
        drawPath(puntadas(Rect(g.lomo + 3.5f * u, r.top + 3.5f * u, r.right - 3.5f * u, r.bottom - 3.5f * u), semilla, u), hilo, style = Stroke(0.75f * u))
        // La bisagra del lomo, hundida, con su luz.
        drawRect(a.sombra(0.42f), Offset(g.lomo - 1.4f * u, r.top + 2f * u), Size(1.4f * u, r.height - 4f * u))
        drawRect(a.claro(0.3f), Offset(g.lomo, r.top + 2f * u), Size(0.8f * u, r.height - 4f * u))
        // Las esquineras de aluminio de la derecha, con su remache.
        listOf(true, false).forEach { arriba ->
            val y = if (arriba) r.top else r.bottom
            val s = if (arriba) 1f else -1f
            val q = 11f * u
            poligonoEnEspacio(trazo, r.right + 0.6f * u, y - s * 0.6f * u, r.right - q, y - s * 0.6f * u, r.right + 0.6f * u, y + s * q)
            val lc = luz.en(Offset(r.right, y))
            drawPath(trazo, a.luz(P.ACERO, lc))
            drawLine(a.claro(0.5f), Offset(r.right - q + 1.5f * u, y + s * 0.4f * u), Offset(r.right - 0.6f * u, y + s * 0.4f * u), 0.8f * u)
            drawLine(a.sombra(0.3f), Offset(r.right - q + 1f * u, y + s * 0.2f * u), Offset(r.right, y + s * q), 0.7f * u)
            remacheEspacio(Offset(r.right - q * 0.3f, y + s * q * 0.3f), 1f * u, a.luz(mezcla(P.ACERO, P.CASCO, 0.4f), lc), a.base)
        }
        // La luz: el mismo mapa de la escena, solo sobre la tapa (las esquineras salen apenas de ella: se alumbran a la par).
        val tapa = Rect(r.left, r.top - 1f * u, r.right + 1f * u, r.bottom + 1f * u)
        clipPath(forma) { ponerMapaEspacio(escena, Rect(0f, 0f, size.width, size.height), luz.mezclaMapa) }
        luz.plano = false
        // El filo de la luz que manda en los cantos que la miran.
        if (a.lumbre) {
            val k = luz.en(Offset(r.right, r.top)).k
            if (!a.noche) {
                drawLine(a.filo(0.6f * k), Offset(r.left + r.width * 0.4f, tapa.top + 1.5f * u), Offset(r.right - 4f * u, tapa.top + 1.5f * u), 1f * u)
                drawLine(a.filo(0.55f * k), Offset(r.right - 0.5f * u, r.top + 4f * u), Offset(r.right - 0.5f * u, r.bottom - 4f * u), 1f * u)
            } else drawLine(a.filo(0.8f * luz.en(Offset(r.left, r.center.y)).k), Offset(r.left + 0.5f * u, r.top + 4f * u), Offset(r.left + 0.5f * u, r.bottom - 4f * u), 1.2f * u)
        }
    }

    /**
     * El parche bordado de la nave, cosido arriba a la izquierda y un poco torcido: la orla naranja con su puntada, el azul de
     * fondo, el anillo con el eje que lo atraviesa y, delante, la estrella de destino.
     */
    private fun DrawScope.parche(c: Offset, r: Float, a: AireTapaEspacio, luz: LuzTapaEspacio) {
        val u = density
        val l = luz.en(c)
        val d = luz.caida(c, 1.2f * u)
        drawCircle(a.sombra(0.4f), r, c + d)
        drawCircle(a.luz(P.NARANJA, l), r, c)
        drawCircle(a.luz(mezcla(P.NARANJA, P.TINTA, 0.35f), l).copy(alpha = 0.6f), r * 0.86f, c,
            style = Stroke(0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.2f * u, 0.9f * u))))
        drawCircle(a.luz(Color(0xFF1C2850), l), r * 0.76f, c)
        drawOval(a.luz(P.CASCO, l), Offset(c.x - r * 0.55f, c.y - r * 0.04f), Size(r * 1.1f, r * 0.42f), style = Stroke(1.3f * u))
        drawLine(a.luz(P.CASCO, l), Offset(c.x - r * 0.08f, c.y - r * 0.34f), Offset(c.x + r * 0.08f, c.y + r * 0.52f), 1.1f * u)
        val e = Offset(c.x + r * 0.32f, c.y - r * 0.36f)
        val k = r * 0.24f; val q = k * 0.18f
        poligonoEnEspacio(trazo, e.x, e.y - k, e.x + q, e.y - q, e.x + k, e.y, e.x + q, e.y + q, e.x, e.y + k, e.x - q, e.y + q, e.x - k, e.y, e.x - q, e.y - q)
        drawPath(trazo, a.luz(P.LUZ_FRIA, LuzEstanteEspacio(maxOf(l.k, 0.5f), l.f)))
        drawArc(a.claro(0.45f), 200f, 80f, false, Offset(c.x - r * 0.95f, c.y - r * 0.95f), Size(r * 1.9f, r * 1.9f), style = Stroke(1f * u))
    }

    /** La cinta de tela cosida arriba con la fecha, con los extremos cortados en pico y dos puntadas torcidas. */
    private fun DrawScope.rotulo(g: GeoTapaEspacio, e: EstadoTapa, medidor: TextMeasurer, a: AireTapaEspacio, luz: LuzTapaEspacio) {
        val u = density; val w = size.width; val h = size.height
        val r = Rect(g.lomo + 36f * u, h * 0.108f, maxOf(g.lomo + 60f * u, w * 0.655f), h * 0.232f)
        val l = luz.en(r.center)
        val pico = 3.2f * u
        poligonoEnEspacio(trazo, r.left, r.top + 0.6f * u, r.right, r.top, r.right - pico, r.center.y, r.right, r.bottom, r.left, r.bottom + 0.5f * u, r.left + pico, r.center.y)
        val d = luz.caida(r.center, 1.1f * u)
        translate(d.x, d.y) { drawPath(trazo, a.sombra(0.4f)) }
        drawPath(trazo, a.papel(l))
        drawRect(a.claro(0.4f), Offset(r.left + pico, r.top + 0.6f * u), Size(r.width - 2 * pico, 0.7f * u))
        listOf(r.left + pico + 3f * u to 0.6f, r.right - pico - 3f * u to -0.8f).forEach { (x, t) ->
            drawLine(a.luz(mezcla(P.TELA, P.TINTA, 0.4f), l), Offset(x, r.top + 2f * u), Offset(x + t * u, r.bottom - 2f * u), 0.8f * u)
        }
        textoEn(medidor, e.fecha, Rect(r.left + pico + 6f * u, r.top, r.right - pico - 6f * u, r.bottom), Letra.codigo, P.TINTA, 10.5f, peso = FontWeight.Medium, minimo = 6f)
    }

    /**
     * La ventanilla de vinilo cosida en la tapa, donde va la tarjeta del día (ahí la pantalla escribe el extracto): el ribete
     * oscuro con su costura a mano, la tarjeta de papel alumbrada (casi no se apaga: el texto se lee siempre), el brillo del
     * vinilo en una banda que cruza la esquina de arriba, el canto del vinilo y sus arrugas en la esquina de abajo.
     */
    private fun DrawScope.ventanilla(g: GeoTapaEspacio, a: AireTapaEspacio, luz: LuzTapaEspacio) {
        val u = density
        val t = g.tarjeta
        val rib = t.inflate(3.4f * u)
        drawRoundRect(a.sombra(0.32f), rib.topLeft + Offset(0.6f * u, 1f * u), rib.size, CornerRadius(3f * u))
        drawRoundRect(a.luz(RIBETE, luz.en(rib.center)), rib.topLeft, rib.size, CornerRadius(3f * u))
        drawPath(puntadas(rib.deflate(1.7f * u), 1351, u), a.luz(mezcla(P.TELA, P.TRIGO, 0.2f), luz.en(rib.center)).copy(alpha = 0.55f), style = Stroke(0.65f * u))
        // La tarjeta: el papel lejos de la luz y, encima, su mapa (más claro y cálido junto a la lámpara; frío junto a la tira).
        drawRect(luz.papelLejos(), t.topLeft, t.size)
        if (luz.modo != 0) {
            val clave = claveMapaEspacio(t.width, t.height, luz.modo) xor (claveMapaEspacio(size.width, size.height, 0) shl 3)
            ponerMapaEspacio(mapaPapel.de(clave) { fx, fy -> luz.colorPapel(fx, fy, t) }, t)
        }
        // La sombra que el ribete deja en la tarjeta (arriba y a la izquierda).
        drawRect(a.sombra(0.18f), t.topLeft, Size(t.width, 2.2f * u))
        drawRect(a.sombra(0.12f), t.topLeft, Size(2f * u, t.height))
        // El brillo del vinilo: una banda que cruza la esquina de arriba a la izquierda (de noche, la de la lámpara, arriba a la
        // derecha), el canto de arriba y dos arrugas abajo.
        val bri = if (a.lumbre && !a.noche) P.AMBAR.copy(alpha = 0.13f) else Color.White.copy(alpha = if (a.oscuro) 0.07f else 0.24f)
        if (a.lumbre && !a.noche) poligonoEnEspacio(trazo, t.right - t.width * 0.3f, t.top, t.right - t.width * 0.18f, t.top, t.right, t.top + t.height * 0.55f, t.right, t.top + t.height * 0.85f)
        else poligonoEnEspacio(trazo, t.left + t.width * 0.05f, t.top, t.left + t.width * 0.2f, t.top, t.left, t.top + t.height * 0.75f, t.left, t.top + t.height * 0.3f)
        drawPath(trazo, bri)
        drawLine(Color.White.copy(alpha = if (a.oscuro) 0.12f else 0.4f), Offset(t.left + 3f * u, t.top + 2.6f * u), Offset(t.right - 3f * u, t.top + 2.6f * u), 0.7f * u)
        trazo.reset()
        trazo.moveTo(t.left + 4f * u, t.bottom - 2f * u); trazo.quadraticTo(t.left + 12f * u, t.bottom - 7f * u, t.left + 20f * u, t.bottom - 3f * u)
        trazo.moveTo(t.right - 26f * u, t.bottom - 2.5f * u); trazo.quadraticTo(t.right - 16f * u, t.bottom - 6f * u, t.right - 6f * u, t.bottom - 4f * u)
        drawPath(trazo, Color.White.copy(alpha = if (a.oscuro) 0.1f else 0.35f), style = Stroke(0.7f * u))
    }

    /**
     * La etiqueta de inventario pegada abajo a la derecha: el código de barras (un solo camino) y cuántas palabras lleva; en
     * blanco, solo el código y dos rayas.
     */
    private fun DrawScope.etiquetaPalabras(e: EstadoTapa, medidor: TextMeasurer, a: AireTapaEspacio, luz: LuzTapaEspacio) {
        val u = density; val w = size.width; val h = size.height
        val r = Rect(w * 0.52f, h * 0.72f, w * 0.825f, h * 0.845f)
        val l = luz.en(r.center)
        val d = luz.caida(r.center, 1f * u)
        drawRect(a.sombra(0.32f), r.topLeft + d, r.size)
        drawRect(a.papel(l), r.topLeft, r.size)
        drawRect(a.claro(0.35f), r.topLeft, Size(r.width, 0.7f * u))
        // La esquina de abajo a la izquierda, despegada.
        poligonoEnEspacio(trazo, r.left, r.bottom - 5f * u, r.left + 5f * u, r.bottom, r.left, r.bottom)
        drawPath(trazo, a.sombra(0.3f))
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

    /** Lo que alumbra por sí mismo, encima de todo: en oscuro, la boca de la lámpara; en los sueños, el charco de la tira. */
    private fun DrawScope.brillos(g: GeoTapaEspacio, a: AireTapaEspacio, luz: LuzTapaEspacio) {
        val u = density
        if (!a.lumbre) return
        if (!a.noche) {
            drawCircle(P.AMBAR.copy(alpha = 0.3f), 6f * u, luz.lampara)
            drawCircle(Color(0xFFFFF0D6), 2.4f * u, luz.lampara)
            mancha(luz.guardia, 24f * u, P.GUARDIA.copy(alpha = 0.3f))
        } else {
            mancha(luz.guardia, 40f * u, P.GUARDIA.copy(alpha = if (a.oscuro) 0.4f else 0.3f))
            drawLine(a.filo(0.7f), Offset(g.bl + 0.6f * u, g.bt + 6f * u), Offset(g.bl + 0.6f * u, g.bb - 6f * u), 1.2f * u, StrokeCap.Round)
        }
    }

    // ---------------------------------------------------------------- abierta

    /** La primera hoja, que se ve al levantar la tapa: el papel con sus renglones y la fecha arriba. */
    private fun DrawScope.paginaAbierta(g: GeoTapaEspacio, a: AireTapaEspacio, luz: LuzTapaEspacio, e: EstadoTapa, medidor: TextMeasurer) {
        val u = density
        val r = Rect(g.lomo, g.bt + 1.5f * u, g.br - 1.5f * u, g.bb - 1f * u)
        drawRect(luz.pincel(P.PAPEL, r, papel = true), r.topLeft, r.size)
        trazo.reset()
        var y = r.top + 26f * u
        while (y < r.bottom - 6f * u) { trazo.moveTo(r.left + 8f * u, y); trazo.lineTo(r.right - 8f * u, y); y += maxOf(1f, 13f * u) }
        drawPath(trazo, P.TELA_AZUL.copy(alpha = 0.25f), style = Stroke(0.6f * u))
        drawLine(P.ROJO.copy(alpha = 0.35f), Offset(r.left + 22f * u, r.top), Offset(r.left + 22f * u, r.bottom), 0.7f * u)
        textoEn(medidor, e.fecha, Rect(r.left + 26f * u, r.top + 6f * u, r.right - 10f * u, r.top + 20f * u), Letra.codigo, P.TINTA.copy(alpha = 0.7f), 9.5f, minimo = 6f)
        drawRect(a.sombra(0.3f), Offset(r.left, r.top), Size(6f * u, r.height))
    }
}
