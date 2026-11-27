package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.TrazosFrente
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

/**
 * El bodegón de arriba del estante de Frente: en la pared, la carta estelar impresa clavada con cinta; colgada del tubo, la
 * lámpara de cúpula blanca (la luz del cuadro); sobre la capota, la radio de maleta abierta con su manipulador delante y el
 * auricular colgado de la tapa, el panel de vidrio grabado en su base oscura, la pila de latas de microfilm con la película que
 * cuelga por el canto, y la taza de lata. Cada cosa con su sombra en la pared y en la capota.
 */
internal object CoronaEstanteFrente {
    private val P = PaletaFrente
    private val M = MuebleEstanteFrente

    /** El color de [c] en [p]: de día con el charco de la cúpula ([dia], cuánto aclara); de noche, con la luz que le llega. */
    private fun DrawScope.en(c: Color, f: Float, p: Offset, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float, dia: Float = 0.3f): Color {
        val w = size.width
        return if (a.oscuro) a.luz(c, cerca(p), frio(p)) else a.cupula(a.dia(c, f, cerca(p)), M.cupula(p.x / w - M.CUPULA_X, p.y / w - M.CUPULA_Y) * dia)
    }

    /** Lo de atrás: la carta estelar clavada en el muro y el panel de vidrio grabado, que pisa la capota al fondo. */
    fun DrawScope.atras(w: Float, apoyo: Float, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        carta(Rect(w * 0.335f, w * 0.07f, w * 0.6f, w * 0.255f), a, cerca, frio)
        vidrio(w, apoyo, a, cerca, frio)
    }

    /** Lo de delante: la radio con su tapa, el manipulador y el auricular; las latas con la película; la taza; la cúpula colgada. */
    fun DrawScope.delante(w: Float, apoyo: Float, ch: Float, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        radio(w, apoyo, a, cerca, frio)
        latas(w, apoyo, ch, a, cerca, frio)
        taza(Offset(w * 0.875f, apoyo + w * 0.004f), w * 0.05f, a, cerca, frio)
        cupula(w, a, cerca)
    }

    // ---------------------------------------------------------------- la carta estelar

    /**
     * La carta estelar impresa en [r], clavada con dos tiras de cinta de papel y un poco ladeada: el papel crudo con su
     * cuadrícula, las órbitas, las estrellas, la ruta de salto punteada con sus anillos de llegada y el blanco marcado en rojo; la
     * esquina de abajo doblada. Sin letras ni números.
     */
    private fun DrawScope.carta(r: Rect, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val c = r.center
        val k = cerca(c); val kv = frio(c)
        rotate(-2.5f, c) {
            // La sombra en la piedra, corrida hacia abajo a la derecha.
            drawRect(a.sombra(0.32f), Offset(r.left + 4f * u, r.top + 5f * u), r.size)
            val papel = Path().apply {
                moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom - 12f * u); lineTo(r.right - 12f * u, r.bottom); lineTo(r.left, r.bottom); close()
            }
            drawPath(papel, en(P.PAPEL, 0f, c, a, cerca, frio, 0.35f))
            clipPath(papel) {
                // La luz de la cúpula, de la izquierda: el papel más claro de ese lado.
                if (!a.oscuro) drawRect(Brush.horizontalGradient(listOf(a.claro(0.25f), Color.Transparent), r.left, r.right), r.topLeft, r.size)
                val tinta = a.luz(Color(0xFF2E4A52), k, kv).copy(alpha = 0.32f)
                var x = r.left + 6f * u
                while (x < r.right) { drawLine(tinta, Offset(x, r.top), Offset(x, r.bottom), 0.5f * u); x += 9f * u }
                var y = r.top + 5f * u
                while (y < r.bottom) { drawLine(tinta, Offset(r.left, y), Offset(r.right, y), 0.5f * u); y += 9f * u }
                // Las órbitas de un sistema, cortadas por el borde.
                val sol = Offset(r.left + r.width * 0.26f, r.top + r.height * 0.62f)
                listOf(0.16f, 0.27f, 0.4f, 0.6f).forEach { f -> drawCircle(a.luz(Color(0xFF2E4A52), k, kv).copy(alpha = 0.55f), r.width * f, sol, style = Stroke(0.8f * u)) }
                drawCircle(a.luz(Color(0xFF2E4A52), k, kv), 2.6f * u, sol)
                // Las estrellas impresas.
                val estrellas = ArrayList<Offset>()
                for (j in 0 until 26) estrellas.add(Offset(r.left + r.width * azar(503, j), r.top + r.height * azar(509, j)))
                estrellas.forEachIndexed { j, o -> drawCircle(a.luz(Color(0xFF1E2A30), k, kv).copy(alpha = 0.8f), (0.6f + 0.9f * azar(521, j)) * u, o) }
                // La ruta de salto punteada y sus anillos de llegada; el blanco, en rojo.
                val ruta = listOf(Offset(r.left + r.width * 0.26f, r.top + r.height * 0.62f), Offset(r.left + r.width * 0.47f, r.top + r.height * 0.38f),
                    Offset(r.left + r.width * 0.66f, r.top + r.height * 0.55f), Offset(r.left + r.width * 0.84f, r.top + r.height * 0.24f))
                val pr = Path().apply { moveTo(ruta[0].x, ruta[0].y); ruta.drop(1).forEach { lineTo(it.x, it.y) } }
                drawPath(pr, a.luz(P.QUEMADO_TINTA, k, kv), style = Stroke(1.3f * u, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.3f * u, 3.2f * u))))
                ruta.drop(1).dropLast(1).forEach { o -> drawCircle(a.luz(P.QUEMADO_TINTA, k, kv), 3f * u, o, style = Stroke(1f * u)) }
                val blanco = ruta.last()
                drawCircle(a.luz(P.BOTON, k, kv), 5.2f * u, blanco, style = Stroke(1.4f * u))
                drawCircle(a.luz(P.BOTON, k, kv), 1.6f * u, blanco)
                // Las arrugas del papel y la mancha de humedad de abajo.
                drawLine(a.sombra(0.14f), Offset(r.left + r.width * 0.55f, r.top), Offset(r.left + r.width * 0.5f, r.bottom), 1.4f * u)
                drawLine(a.claro(0.3f), Offset(r.left + r.width * 0.55f + 1.4f * u, r.top), Offset(r.left + r.width * 0.5f + 1.4f * u, r.bottom), 0.8f * u)
                drawOval(Brush.radialGradient(listOf(a.luz(Color(0xFF8C7A4E), k, kv).copy(alpha = 0.35f), Color.Transparent), Offset(r.left + r.width * 0.15f, r.bottom), r.width * 0.3f),
                    Offset(r.left - r.width * 0.15f, r.bottom - r.width * 0.3f), Size(r.width * 0.6f, r.width * 0.6f))
            }
            // La esquina doblada, con el revés del papel.
            val dobla = poligonoFrente(r.right, r.bottom - 12f * u, r.right - 12f * u, r.bottom, r.right - 13f * u, r.bottom - 13f * u)
            drawPath(dobla, en(mezcla(P.PAPEL, P.CINTA_OSC, 0.5f), -0.05f, c, a, cerca, frio, 0.2f))
            drawPath(dobla, a.sombra(0.3f), style = Stroke(0.6f * u))
            // Las dos tiras de cinta de papel arriba, ladeadas.
            listOf(Offset(r.left + 4f * u, r.top), Offset(r.right - 4f * u, r.top)).forEachIndexed { i, o ->
                rotate(if (i == 0) -32f else 28f, o) {
                    val t = Rect(o.x - 9f * u, o.y - 3.6f * u, o.x + 9f * u, o.y + 3.6f * u)
                    drawRect(a.sombra(0.2f), Offset(t.left + 0.8f * u, t.top + 1f * u), t.size)
                    drawRect(a.etiqueta(P.CINTA, k, kv).copy(alpha = 0.94f), t.topLeft, t.size)
                }
            }
        }
    }

    // ---------------------------------------------------------------- el vidrio grabado

    /**
     * El panel de vidrio grabado sobre la capota, al fondo a la derecha: la base de consola oscura con la ranura de luz por donde
     * se ilumina el canto, el vidrio (pálido de día, verde hondo de noche) con sus trazos y órbitas iluminados, los reflejos, las
     * dos pinzas de cromo y el filo encendido.
     */
    private fun DrawScope.vidrio(w: Float, apoyo: Float, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val base = Rect(w * 0.648f, apoyo - w * 0.046f, w * 0.932f, apoyo + w * 0.006f)
        val v = Rect(w * 0.672f, w * 0.162f, w * 0.908f, base.top + 1f * u)
        val k = cerca(v.center)
        val vivo = a.oscuro
        // La sombra del panel en la piedra.
        drawRect(a.sombra(if (a.oscuro) 0.2f else 0.32f), Offset(v.left + w * 0.02f, v.top + w * 0.015f), v.size)
        // El marco de consola oscura, de esquinas redondas, con su canto de luz.
        val marco = v.inflate(3.4f * u)
        drawRoundRect(Brush.horizontalGradient(listOf(en(P.CONSOLA_LUZ, 0f, marco.center, a, cerca, frio), en(P.CONSOLA, 0f, marco.center, a, cerca, frio), en(P.CONSOLA_OSC, 0f, marco.center, a, cerca, frio)), marco.left, marco.right),
            marco.topLeft, marco.size, CornerRadius(4f * u))
        drawRoundRect(a.brillo(0.22f, k), marco.topLeft, marco.size, CornerRadius(4f * u), style = Stroke(0.7f * u))
        // El vidrio: de día, el verde del vidrio a la luz; de noche, hondo, y lo que brilla son sus trazos.
        val vidrio = if (vivo) mezcla(P.VIDRIO, P.NOCHE, if (a.dormida) 0.05f else 0.25f) else en(P.VIDRIO_LUZ, -0.08f, v.center, a, cerca, frio, 0.15f)
        drawRect(vidrio, v.topLeft, v.size)
        clipRect(v.left, v.top, v.right, v.bottom) {
            // La luz que sube del canto de abajo.
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, P.VIDRIO_TRAZO.copy(alpha = if (vivo) 0.3f else 0.22f)), v.bottom - v.height * 0.55f, v.bottom),
                Offset(v.left, v.bottom - v.height * 0.55f), Size(v.width, v.height * 0.55f))
            val trazo = if (vivo) P.VIDRIO_TRAZO else mezcla(P.VIDRIO_TRAZO, P.HUESO_LUZ, 0.3f)
            with(TrazosFrente) { trazosVidrio(v, trazo, 0.7f * u, if (a.dormida) 0.85f else if (vivo) 0.7f else 0.75f, 29, 1) }
            // Los reflejos de la sala en el vidrio.
            listOf(0.1f to 0.06f, 0.24f to 0.035f).forEach { (f, al) ->
                drawLine(Color.White.copy(alpha = if (vivo) al * 0.5f else al * 2.4f), Offset(v.left + v.width * f, v.bottom), Offset(v.left + v.width * (f + 0.34f), v.top), v.width * 0.06f)
            }
        }
        drawRect(P.VIDRIO_TRAZO.copy(alpha = if (vivo) 0.55f else 0.4f), v.topLeft, v.size, style = Stroke(0.6f * u))
        // Los cuatro tornillos del marco.
        listOf(Offset(marco.left + 1.7f * u, marco.top + 1.7f * u), Offset(marco.right - 1.7f * u, marco.top + 1.7f * u)).forEachIndexed { i, o ->
            tornilloFrente(o, 1.2f * u, a, k, 30f + 60f * i)
        }
        // La base de consola oscura, de esquinas redondas, con la ranura de luz y dos tornillos.
        drawRoundRect(a.sombra(0.4f), Offset(base.left + 2f * u, base.top + 2.4f * u), base.size, CornerRadius(4f * u))
        drawRoundRect(Brush.verticalGradient(listOf(en(P.CONSOLA_LUZ, 0f, base.center, a, cerca, frio), en(P.CONSOLA, 0f, base.center, a, cerca, frio), en(P.CONSOLA_OSC, 0f, base.center, a, cerca, frio)), base.top, base.bottom),
            base.topLeft, base.size, CornerRadius(4f * u))
        drawLine(a.brillo(0.3f, k), Offset(base.left + 4f * u, base.top + 0.8f * u), Offset(base.right - 4f * u, base.top + 0.8f * u), 0.8f * u)
        val ranura = Rect(v.left - 1f * u, base.top + 1.5f * u, v.right + 1f * u, base.top + 4f * u)
        drawRect(if (vivo) P.VIDRIO_TRAZO else mezcla(P.VIDRIO_TRAZO, P.HUESO, 0.45f), ranura.topLeft, ranura.size)
        listOf(base.left + 6f * u, base.right - 6f * u).forEachIndexed { i, x -> tornilloFrente(Offset(x, base.center.y + 3f * u), 1.6f * u, a, k, 40f + 50f * i) }
        // De noche, el filo encendido del vidrio y su luz en la capota.
        if (vivo) {
            drawLine(P.VIDRIO_TRAZO.copy(alpha = 0.9f), Offset(v.left, v.bottom - 0.5f * u), Offset(v.right, v.bottom - 0.5f * u), 1.2f * u)
            drawLine(P.VIDRIO_TRAZO.copy(alpha = 0.45f), Offset(v.left, v.top), Offset(v.left, v.bottom), 0.8f * u)
        }
    }

    // ---------------------------------------------------------------- la radio de maleta

    /**
     * La radio de maleta abierta sobre la capota: la caja de baquelita con sus cantoneras de cromo, la tapa levantada (por dentro,
     * el forro, el hilo de la antena enrollado y su pinza), el frente de consola oscura con el dial redondo y su aguja, el vúmetro
     * sin cifras, las perillas, el piloto y la rejilla del altavoz; delante, el manipulador con su cable y, colgado de la esquina de
     * la tapa, el auricular de diadema.
     */
    private fun DrawScope.radio(w: Float, apoyo: Float, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val caja = Rect(w * 0.11f, apoyo - w * 0.165f, w * 0.56f, apoyo + w * 0.004f)
        val tapa = poligonoFrente(caja.left + w * 0.012f, caja.top, caja.right - w * 0.012f, caja.top, caja.right - w * 0.03f, caja.top - w * 0.142f, caja.left + w * 0.03f, caja.top - w * 0.142f)
        val k = cerca(caja.center); val kv = frio(caja.center)
        val kTapa = cerca(Offset(caja.center.x, caja.top - w * 0.07f))
        // La sombra de la radio en la piedra (la luz viene de arriba a la izquierda).
        translate(w * 0.024f, w * 0.012f) { drawPath(tapa, a.sombra(0.3f)) }
        drawRect(a.sombra(0.3f), Offset(caja.right, caja.top + w * 0.02f), Size(w * 0.024f, caja.height - w * 0.02f))
        // La tapa levantada: el canto de baquelita y, por dentro, el forro con la antena enrollada.
        drawPath(tapa, en(P.BAQUELITA, 0f, Offset(caja.center.x, caja.top - w * 0.07f), a, cerca, frio, 0.3f))
        val forro = poligonoFrente(caja.left + w * 0.024f, caja.top - w * 0.004f, caja.right - w * 0.024f, caja.top - w * 0.004f,
            caja.right - w * 0.038f, caja.top - w * 0.132f, caja.left + w * 0.038f, caja.top - w * 0.132f)
        drawPath(forro, en(P.CONSOLA_LUZ, -0.05f, Offset(caja.center.x, caja.top - w * 0.07f), a, cerca, frio, 0.35f))
        clipPath(forro) {
            // La luz de la cúpula entra por la izquierda del forro.
            if (!a.oscuro) drawRect(Brush.horizontalGradient(listOf(a.claro(0.22f), Color.Transparent), caja.left, caja.center.x), Offset(caja.left, caja.top - w * 0.14f), Size(caja.width, w * 0.14f))
            else drawRect(Brush.horizontalGradient(listOf(P.LAMPARA.copy(alpha = 0.22f * kTapa), Color.Transparent), caja.left, caja.right), Offset(caja.left, caja.top - w * 0.14f), Size(caja.width, w * 0.14f))
            // El acolchado del forro, en rombos cosidos.
            val costura = a.sombra(0.22f)
            var x = caja.left - w * 0.1f
            while (x < caja.right + w * 0.1f) {
                drawLine(costura, Offset(x, caja.top), Offset(x + w * 0.1f, caja.top - w * 0.14f), 0.7f * u)
                drawLine(costura, Offset(x + w * 0.1f, caja.top), Offset(x, caja.top - w * 0.14f), 0.7f * u)
                x += w * 0.045f
            }
            // El hilo de la antena enrollado, sujeto con su pinza.
            val ac = Offset(caja.left + caja.width * 0.3f, caja.top - w * 0.07f)
            for (j in 0 until 4) drawOval(en(P.COBRE, 0f, ac, a, cerca, frio), Offset(ac.x - w * 0.045f + j * 0.6f * u, ac.y - w * 0.032f + j * 0.5f * u), Size(w * 0.09f, w * 0.064f), style = Stroke(1.1f * u))
            drawOval(a.brillo(0.35f, kTapa), Offset(ac.x - w * 0.045f, ac.y - w * 0.032f), Size(w * 0.09f, w * 0.064f), style = Stroke(0.5f * u))
            drawRect(en(P.CROMO, 0f, ac, a, cerca, frio), Offset(ac.x - 2f * u, ac.y - w * 0.04f), Size(4f * u, w * 0.018f))
            // La bolsa del forro, con su solapa.
            val bolsa = Rect(caja.left + caja.width * 0.56f, caja.top - w * 0.1f, caja.left + caja.width * 0.86f, caja.top - w * 0.012f)
            drawRect(en(P.CONSOLA, 0f, bolsa.center, a, cerca, frio), bolsa.topLeft, bolsa.size)
            drawRect(en(P.CONSOLA_LUZ, 0.05f, bolsa.center, a, cerca, frio), bolsa.topLeft, Size(bolsa.width, bolsa.height * 0.3f))
            drawLine(a.sombra(0.4f), Offset(bolsa.left, bolsa.top + bolsa.height * 0.3f), Offset(bolsa.right, bolsa.top + bolsa.height * 0.3f), 0.8f * u)
            drawCircle(en(P.CROMO, 0f, bolsa.center, a, cerca, frio), 1.4f * u, Offset(bolsa.center.x, bolsa.top + bolsa.height * 0.3f))
        }
        drawPath(tapa, a.sombra(0.5f), style = Stroke(0.8f * u))
        // El asa de cuero en el canto de arriba de la tapa.
        val asa = Rect(caja.center.x - w * 0.06f, caja.top - w * 0.156f, caja.center.x + w * 0.06f, caja.top - w * 0.138f)
        drawRoundRect(en(P.CUERO, 0f, asa.center, a, cerca, frio), asa.topLeft, asa.size, CornerRadius(asa.height / 2))
        drawLine(a.brillo(0.3f, kTapa), Offset(asa.left + 3f * u, asa.top + 1f * u), Offset(asa.right - 3f * u, asa.top + 1f * u), 0.8f * u)
        // La caja: baquelita con su luz a la izquierda y sus cantoneras de cromo.
        drawRect(Brush.horizontalGradient(listOf(en(P.BAQUELITA_LUZ, 0.06f, Offset(caja.left, caja.center.y), a, cerca, frio, 0.5f), en(P.BAQUELITA, 0f, caja.center, a, cerca, frio),
            en(mezcla(P.BAQUELITA, P.SOMBRA, 0.4f), 0f, Offset(caja.right, caja.center.y), a, cerca, frio, 0.1f)), caja.left, caja.right), caja.topLeft, caja.size)
        drawLine(a.brillo(0.35f, k), Offset(caja.left + 2f * u, caja.top + 1f * u), Offset(caja.right - 2f * u, caja.top + 1f * u), 1f * u)
        listOf(caja.left to caja.top, caja.right to caja.top, caja.left to caja.bottom, caja.right to caja.bottom).forEachIndexed { i, (x, y) ->
            val s = 6f * u
            val esq = Rect(if (i % 2 == 0) x else x - s, if (i < 2) y else y - s, if (i % 2 == 0) x + s else x, if (i < 2) y + s else y)
            drawRect(en(P.CROMO, 0f, esq.center, a, cerca, frio), esq.topLeft, esq.size)
            drawRect(a.brillo(0.45f, k), esq.topLeft, Size(esq.width, 1f * u))
            drawCircle(en(P.CROMO_OSC, 0f, esq.center, a, cerca, frio), 0.9f * u, esq.center)
        }
        // El frente de consola oscura.
        val frente = Rect(caja.left + w * 0.018f, caja.top + w * 0.016f, caja.right - w * 0.018f, caja.bottom - w * 0.016f)
        drawRoundRect(en(P.CONSOLA, 0f, frente.center, a, cerca, frio), frente.topLeft, frente.size, CornerRadius(3f * u))
        drawRect(a.sombra(0.45f), frente.topLeft, Size(frente.width, 2f * u))
        drawRoundRect(a.brillo(0.16f, k), frente.topLeft, frente.size, CornerRadius(3f * u), style = Stroke(0.7f * u))
        // El dial redondo de sintonía, con su escala sin cifras y la aguja; de noche, la luz ámbar de detrás.
        val dc = Offset(frente.left + frente.width * 0.17f, frente.center.y)
        val dr = frente.height * 0.4f
        dial(dc, dr, a, k, kv)
        // El vúmetro: la ventana crema con su arco de marcas y su aguja; las tres perillas debajo.
        val vu = Rect(frente.left + frente.width * 0.37f, frente.top + frente.height * 0.13f, frente.left + frente.width * 0.6f, frente.top + frente.height * 0.5f)
        vumetro(vu, a, k)
        for (j in 0 until 3) {
            val c = Offset(vu.left + vu.width * (0.12f + 0.38f * j), frente.top + frente.height * 0.75f)
            perilla(c, frente.height * (if (j == 1) 0.13f else 0.11f), 200f + 70f * j, a, k)
        }
        // El piloto de encendido, entre el dial y el vúmetro.
        val pil = Offset(frente.left + frente.width * 0.33f, frente.top + frente.height * 0.2f)
        pilotoFrente(pil, 2f * u, P.AMBAR, !a.dormida, a, cerca(pil))
        // La rejilla del altavoz: agujeros en hileras.
        val rej = Rect(frente.left + frente.width * 0.66f, frente.top + frente.height * 0.14f, frente.right - frente.width * 0.05f, frente.bottom - frente.height * 0.14f)
        drawRoundRect(en(P.CONSOLA_OSC, 0f, rej.center, a, cerca, frio), rej.topLeft, rej.size, CornerRadius(4f * u))
        val agujeros = ArrayList<Offset>()
        var yy = rej.top + 3f * u
        var fila = 0
        while (yy < rej.bottom - 2f * u) {
            var xx = rej.left + 3f * u + (if (fila % 2 == 0) 0f else 1.7f * u)
            while (xx < rej.right - 2f * u) { agujeros.add(Offset(xx, yy)); xx += 3.4f * u }
            yy += 3f * u; fila++
        }
        drawPoints(agujeros, androidx.compose.ui.graphics.PointMode.Points, Color.Black.copy(alpha = 0.8f), 1.7f * u, StrokeCap.Round)
        drawRoundRect(a.brillo(0.2f, k), rej.topLeft, rej.size, CornerRadius(4f * u), style = Stroke(0.7f * u))
        // El manipulador delante, a la izquierda, con su cable a la radio.
        manipulador(Rect(w * 0.035f, apoyo - w * 0.036f, w * 0.165f, apoyo + w * 0.006f), caja, a, cerca, frio)
        // El auricular, colgado de la esquina de la tapa: la diadema sobre el canto y las dos copas.
        auricular(Offset(caja.right - w * 0.034f, caja.top - w * 0.142f), w * 0.04f, a, cerca(Offset(caja.right, caja.top - w * 0.1f)), frio(Offset(caja.right, caja.top - w * 0.1f)))
    }

    /** El dial de sintonía en [c], de radio [r]: el bisel de cromo, la escala clara con sus marcas, la aguja roja y la perilla. */
    private fun DrawScope.dial(c: Offset, r: Float, a: AireFrente, k: Float, kv: Float) {
        val u = density
        val vivo = a.lumbre && !a.dormida
        drawCircle(a.sombra(0.4f), r * 1.12f, Offset(c.x + 1f * u, c.y + 1.4f * u))
        drawCircle(a.luz(P.CROMO, k, kv), r * 1.1f, c)
        drawCircle(a.luz(P.CROMO_OSC, k, kv), r * 1.1f, c, style = Stroke(0.7f * u))
        // La escala, crema de día; de noche, encendida por detrás en ámbar.
        val escala = if (a.oscuro) (if (a.dormida) mezcla(P.CINTA, P.NOCHE, 0.7f) else mezcla(P.CINTA, P.AMBAR, 0.35f)) else a.dia(P.CINTA, 0.08f)
        drawCircle(escala, r, c)
        val marca = if (a.oscuro && !a.dormida) Color(0xFF3A1E06) else a.tinta(1f).copy(alpha = 0.75f)
        for (j in 0 until 25) {
            val ang = Math.toRadians(150.0 + j * 10.0)
            val l = if (j % 4 == 0) 0.7f else 0.8f
            drawLine(marca, Offset(c.x + r * 0.92f * cos(ang).toFloat(), c.y + r * 0.92f * sin(ang).toFloat()),
                Offset(c.x + r * l * cos(ang).toFloat(), c.y + r * l * sin(ang).toFloat()), (if (j % 4 == 0) 1.1f else 0.6f) * u)
        }
        drawArc(marca.copy(alpha = 0.5f), 150f, 240f, false, Offset(c.x - r * 0.62f, c.y - r * 0.62f), Size(r * 1.24f, r * 1.24f), style = Stroke(0.6f * u))
        val ang = Math.toRadians(238.0)
        drawLine(P.BOTON, c, Offset(c.x + r * 0.88f * cos(ang).toFloat(), c.y + r * 0.88f * sin(ang).toFloat()), 1.2f * u, StrokeCap.Round)
        perilla(c, r * 0.34f, 120f, a, k)
        drawArc(Color.White.copy(alpha = if (a.oscuro) 0.08f else 0.4f), 200f, 60f, false, Offset(c.x - r * 0.9f, c.y - r * 0.9f), Size(r * 1.8f, r * 1.8f), style = Stroke(r * 0.1f, cap = StrokeCap.Round))
    }

    /** El vúmetro en [r]: la ventana crema (de noche, encendida) con su arco de marcas sin cifras, el tramo rojo y la aguja. */
    private fun DrawScope.vumetro(r: Rect, a: AireFrente, k: Float) {
        val u = density
        val vivo = a.lumbre && !a.dormida
        drawRect(a.luz(P.CROMO_OSC, k), r.inflate(1.2f * u).topLeft, r.inflate(1.2f * u).size)
        val cara = if (a.oscuro) (if (a.dormida) mezcla(P.CINTA, P.NOCHE, 0.72f) else mezcla(P.CINTA, P.AMBAR, 0.3f)) else a.dia(P.CINTA, 0.1f)
        drawRect(cara, r.topLeft, r.size)
        val c = Offset(r.center.x, r.bottom + r.height * 0.55f)
        val rr = r.height * 1.25f
        val marca = if (a.oscuro && !a.dormida) Color(0xFF3A1E06) else a.tinta(1f).copy(alpha = 0.7f)
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawArc(marca, 238f, 64f, false, Offset(c.x - rr, c.y - rr), Size(rr * 2, rr * 2), style = Stroke(0.7f * u))
            drawArc(P.BOTON.copy(alpha = 0.85f), 288f, 14f, false, Offset(c.x - rr, c.y - rr), Size(rr * 2, rr * 2), style = Stroke(1.6f * u))
            for (j in 0..8) {
                val ang = Math.toRadians(238.0 + j * 8.0)
                drawLine(marca, Offset(c.x + rr * cos(ang).toFloat(), c.y + rr * sin(ang).toFloat()), Offset(c.x + rr * 1.1f * cos(ang).toFloat(), c.y + rr * 1.1f * sin(ang).toFloat()), 0.7f * u)
            }
            val ang = Math.toRadians(262.0)
            drawLine(Color(0xFF1A1A18), c, Offset(c.x + rr * 1.15f * cos(ang).toFloat(), c.y + rr * 1.15f * sin(ang).toFloat()), 0.9f * u)
        }
        drawRect(Color.White.copy(alpha = if (a.oscuro) 0.06f else 0.3f), r.topLeft, Size(r.width, r.height * 0.2f))
    }

    /** Una perilla de baquelita en [c], de radio [r], con su falda estriada, su brillo y la raya que marca ([giro], en grados). */
    private fun DrawScope.perilla(c: Offset, r: Float, giro: Float, a: AireFrente, k: Float) {
        val u = density
        drawCircle(a.sombra(0.45f), r * 1.15f, Offset(c.x + r * 0.2f, c.y + r * 0.3f))
        drawCircle(a.luz(P.BAQUELITA, k), r * 1.12f, c)
        drawCircle(a.luz(P.BAQUELITA_LUZ, k), r * 0.8f, Offset(c.x - r * 0.1f, c.y - r * 0.12f))
        drawCircle(a.luz(P.BAQUELITA, k), r * 0.62f, c)
        val ang = Math.toRadians(giro.toDouble())
        drawLine(a.luz(P.HUESO_LUZ, k), Offset(c.x + r * 0.2f * cos(ang).toFloat(), c.y + r * 0.2f * sin(ang).toFloat()),
            Offset(c.x + r * 0.95f * cos(ang).toFloat(), c.y + r * 0.95f * sin(ang).toFloat()), maxOf(0.8f * u, r * 0.16f), StrokeCap.Round)
        drawCircle(a.brillo(0.4f, k), r * 0.2f, Offset(c.x - r * 0.42f, c.y - r * 0.45f))
    }

    /**
     * El manipulador de telegrafía en [r]: la base de baquelita, el brazo de cromo con su resorte y su tope, la perilla negra de la
     * punta y el cable que va a la radio.
     */
    private fun DrawScope.manipulador(r: Rect, caja: Rect, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val k = cerca(r.center); val kv = frio(r.center)
        // El cable, que sube por detrás hasta el costado de la radio.
        val cable = Path().apply { moveTo(r.right - 3f * u, r.center.y); cubicTo(r.right + 6f * u, r.top - 4f * u, caja.left - 2f * u, caja.bottom - 6f * u, caja.left + 4f * u, caja.bottom - 10f * u) }
        drawPath(cable, a.luz(P.BAQUELITA, k, kv), style = Stroke(1.5f * u, cap = StrokeCap.Round))
        contactoFrente(Offset(r.center.x, r.bottom), r.width * 1.05f, 5f * u, a, 0.4f)
        val base = Rect(r.left, r.top + r.height * 0.5f, r.right, r.bottom)
        drawRoundRect(Brush.verticalGradient(listOf(en(P.BAQUELITA_LUZ, 0f, base.center, a, cerca, frio), en(P.BAQUELITA, 0f, base.center, a, cerca, frio)), base.top, base.bottom),
            base.topLeft, base.size, CornerRadius(base.height * 0.4f))
        drawLine(a.brillo(0.3f, k), Offset(base.left + 3f * u, base.top + 1f * u), Offset(base.right - 3f * u, base.top + 1f * u), 0.8f * u)
        // El brazo, el pivote, el resorte y el tope.
        val piv = Offset(r.right - r.width * 0.2f, base.top - 1f * u)
        val punta = Offset(r.left + r.width * 0.2f, base.top - r.height * 0.28f)
        drawRect(en(P.CROMO_OSC, 0f, piv, a, cerca, frio), Offset(piv.x - 3f * u, piv.y - 4f * u), Size(6f * u, 5f * u))
        drawLine(a.sombra(0.4f), Offset(punta.x + 1f * u, punta.y + 1.6f * u), Offset(piv.x + 1f * u, piv.y - 1f * u), 2.4f * u, StrokeCap.Round)
        drawLine(en(P.CROMO, 0f, r.center, a, cerca, frio), punta, Offset(piv.x, piv.y - 2.4f * u), 2.2f * u, StrokeCap.Round)
        drawLine(a.brillo(0.6f, k), Offset(punta.x + 2f * u, punta.y - 0.6f * u), Offset(piv.x - 4f * u, piv.y - 3f * u), 0.7f * u)
        val res = Offset(r.left + r.width * 0.55f, base.top)
        var y = res.y - 1f * u
        while (y > res.y - r.height * 0.22f) { drawLine(en(P.CROMO_OSC, 0f, res, a, cerca, frio), Offset(res.x - 2f * u, y), Offset(res.x + 2f * u, y - 0.6f * u), 0.7f * u); y -= 1.3f * u }
        // La perilla de la punta.
        drawOval(a.sombra(0.4f), Offset(punta.x - 4.6f * u, punta.y - 1f * u), Size(10f * u, 6f * u))
        drawOval(en(P.BAQUELITA, 0f, punta, a, cerca, frio), Offset(punta.x - 5f * u, punta.y - 3.6f * u), Size(10f * u, 6.4f * u))
        drawOval(a.brillo(0.4f, k), Offset(punta.x - 3.4f * u, punta.y - 3f * u), Size(4f * u, 1.6f * u))
    }

    /**
     * El auricular colgado de [g] (el canto de la tapa), con la diadema de radio [r]: el arco de baquelita por encima del canto, las
     * dos copas colgando a los lados con su almohadilla y el cordón que cae por detrás.
     */
    private fun DrawScope.auricular(g: Offset, r: Float, a: AireFrente, k: Float, kv: Float) {
        val u = density
        val arco = Rect(g.x - r, g.y - r * 0.55f, g.x + r, g.y + r * 1.45f)
        drawArc(a.sombra(0.35f), 185f, 170f, false, arco.translate(1.6f * u, 2.2f * u).topLeft, arco.size, style = Stroke(2.8f * u, cap = StrokeCap.Round))
        drawArc(a.luz(P.BAQUELITA, k, kv), 185f, 170f, false, arco.topLeft, arco.size, style = Stroke(2.8f * u, cap = StrokeCap.Round))
        drawArc(a.brillo(0.35f, k), 215f, 50f, false, arco.topLeft, arco.size, style = Stroke(0.8f * u))
        // El cordón en espiral que cae por el costado de la radio.
        val c0 = Offset(g.x + r * 1.05f, g.y + r * 1.6f)
        var y = c0.y
        var j = 0
        while (y < c0.y + r * 2.2f) {
            val x = c0.x + 1.2f * u * sin(j * 0.5f)
            drawOval(a.luz(P.BAQUELITA, k, kv), Offset(x - 2.2f * u, y), Size(4.4f * u, 3.6f * u), style = Stroke(1f * u))
            y += 2.2f * u; j++
        }
        listOf(-1f, 1f).forEach { s ->
            val c = Offset(g.x + s * r * 1.02f, g.y + r * 1.15f)
            val cw = r * 0.62f; val chh = r * 1.0f
            drawRoundRect(a.sombra(0.35f), Offset(c.x - cw / 2 + 1.4f * u, c.y - chh / 2 + 2f * u), Size(cw, chh), CornerRadius(cw * 0.45f))
            drawRoundRect(Brush.horizontalGradient(listOf(a.luz(P.BAQUELITA_LUZ, k, kv), a.luz(P.BAQUELITA, k, kv)), c.x - cw / 2, c.x + cw / 2), Offset(c.x - cw / 2, c.y - chh / 2), Size(cw, chh), CornerRadius(cw * 0.45f))
            drawRoundRect(a.luz(mezcla(P.CUERO, P.BAQUELITA, 0.4f), k, kv), Offset(c.x - cw * 0.3f, c.y - chh * 0.36f), Size(cw * 0.6f, chh * 0.72f), CornerRadius(cw * 0.3f))
            drawCircle(a.luz(P.CROMO, k, kv), cw * 0.13f, Offset(c.x, c.y - chh * 0.5f))
            drawCircle(a.brillo(0.35f, k), cw * 0.1f, Offset(c.x - cw * 0.2f, c.y - chh * 0.3f))
        }
    }

    // ---------------------------------------------------------------- las latas, la película y la taza

    /** Los colores de las latas: aluminio, latón anodizado, rojo óxido y verde gris (los del Ideario). */
    private val COLORES_LATA = listOf(Color(0xFFB9B6AE), Color(0xFFC9A15A), Color(0xFF8E3A2C), Color(0xFF6E7F72))

    /** Las latas de microfilm apiladas sobre la capota, la de arriba con la tapa corrida, y la película que cuelga por el canto. */
    private fun DrawScope.latas(w: Float, apoyo: Float, ch: Float, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val ancho = w * 0.16f
        val alto = w * 0.026f
        val cx = w * 0.655f
        val colores = listOf(COLORES_LATA[2], COLORES_LATA[0], COLORES_LATA[3])
        contactoFrente(Offset(cx, apoyo + 1f * u), ancho * 1.15f, 6f * u, a, 0.42f)
        for (i in 0 until 3) {
            val dx = (azar(611, i) - 0.5f) * w * 0.03f
            val r = Rect(cx - ancho / 2 + dx, apoyo - (i + 1) * alto, cx + ancho / 2 + dx, apoyo - i * alto)
            lataDeLado(r, colores[i], i == 2, a, cerca(r.center), frio(r.center))
        }
        // La película que sale de la lata de arriba, corre por la capota y cuelga por su canto.
        val sale = Offset(cx + ancho * 0.36f, apoyo - 3 * alto - alto * 0.2f)
        tira(sale, Offset(cx + ancho * 0.78f, sale.y + 2f * u), Offset(cx + ancho * 0.6f, apoyo + w * 0.01f), Offset(cx + ancho * 0.5f, ch - w * 0.015f), 5.4f * u, a, cerca, frio)
    }

    /**
     * Una lata de microfilm acostada en [r], vista un poco desde arriba: el canto de metal con su reborde y su etiqueta y, si es la
     * de arriba ([tope]), la tapa con sus anillos prensados, corrida.
     */
    private fun DrawScope.lataDeLado(r: Rect, color: Color, tope: Boolean, a: AireFrente, k: Float, kv: Float) {
        val u = density
        val elipse = r.height * 0.9f
        val c = a.luz(color, k, kv)
        val metal = Brush.horizontalGradient(0f to mezcla(c, Color.Black, 0.4f), 0.22f to mezcla(c, Color.White, if (a.oscuro) 0.1f else 0.3f), 0.5f to c,
            1f to mezcla(c, Color.Black, 0.5f), startX = r.left, endX = r.right)
        drawRect(metal, r.topLeft, r.size)
        drawArc(metal, 0f, 180f, true, Offset(r.left, r.bottom - elipse / 2), Size(r.width, elipse))
        drawArc(a.sombra(0.5f), 0f, 180f, false, Offset(r.left, r.bottom - elipse / 2), Size(r.width, elipse), style = Stroke(0.8f * u))
        drawArc(a.brillo(0.3f, k), 10f, 160f, false, Offset(r.left, r.top - elipse / 2 + r.height * 0.32f), Size(r.width, elipse), style = Stroke(0.7f * u))
        val e = Rect(r.center.x - r.width * 0.16f, r.top + r.height * 0.4f, r.center.x + r.width * 0.16f, r.bottom + elipse * 0.2f)
        drawRect(a.etiqueta(P.CINTA, k, kv), e.topLeft, e.size)
        drawRect(a.luz(P.QUEMADO_TINTA, k, kv), e.topLeft, Size(e.width * 0.16f, e.height))
        if (!tope) return
        val boca = Rect(r.left, r.top - elipse / 2, r.right, r.top + elipse / 2)
        drawOval(Color(0xFF0B0907), boca.topLeft, boca.size)
        val rollo = Rect(boca.left + boca.width * 0.1f, boca.top + boca.height * 0.14f, boca.right - boca.width * 0.1f, boca.bottom - boca.height * 0.06f)
        drawOval(a.luz(P.PELICULA, k, kv), rollo.topLeft, rollo.size)
        drawOval(a.luz(P.MIEL, k, kv).copy(alpha = 0.5f), Offset(rollo.left + rollo.width * 0.1f, rollo.top + rollo.height * 0.1f), Size(rollo.width * 0.8f, rollo.height * 0.8f), style = Stroke(0.7f * u))
        drawOval(a.luz(P.CROMO, k, kv), Offset(rollo.center.x - rollo.width * 0.12f, rollo.center.y - rollo.height * 0.12f), Size(rollo.width * 0.24f, rollo.height * 0.24f))
        // La tapa corrida hacia atrás a la izquierda, apoyada de canto.
        val tp = boca.translate(-r.width * 0.32f, -r.height * 0.55f)
        rotate(-8f, tp.center) {
            drawOval(a.sombra(0.3f), tp.translate(1.4f * u, 2f * u).topLeft, tp.size)
            drawOval(Brush.linearGradient(listOf(mezcla(c, Color.White, if (a.oscuro) 0.12f else 0.4f), c, mezcla(c, Color.Black, 0.25f)), tp.topLeft, tp.bottomRight), tp.topLeft, tp.size)
            drawOval(a.sombra(0.25f), Offset(tp.left + tp.width * 0.16f, tp.top + tp.height * 0.16f), Size(tp.width * 0.68f, tp.height * 0.68f), style = Stroke(0.7f * u))
            drawOval(a.brillo(0.3f, k), Offset(tp.left + tp.width * 0.3f, tp.top + tp.height * 0.3f), Size(tp.width * 0.4f, tp.height * 0.4f), style = Stroke(0.6f * u))
        }
    }

    /** Un punto de la curva de Bézier cúbica de [p0] a [p3], en [t]. */
    private fun bezier(p0: Offset, p1: Offset, p2: Offset, p3: Offset, t: Float): Offset {
        val m = 1 - t
        return p0 * (m * m * m) + p1 * (3 * m * m * t) + p2 * (3 * m * t * t) + p3 * (t * t * t)
    }

    /** Una tira de película a lo largo de una curva: su sombra, la base color miel y las perforaciones de las dos orillas. */
    fun DrawScope.tira(p0: Offset, p1: Offset, p2: Offset, p3: Offset, ancho: Float, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val n = 48
        val pts = List(n + 1) { bezier(p0, p1, p2, p3, it / n.toFloat()) }
        val camino = Path().apply { moveTo(pts[0].x, pts[0].y); for (i in 1..n) lineTo(pts[i].x, pts[i].y) }
        val k = cerca(pts[n / 2]); val kv = frio(pts[n / 2])
        translate(ancho * 0.2f + 1f * u, ancho * 0.3f + 1f * u) { drawPath(camino, a.sombra(0.4f), style = Stroke(ancho)) }
        drawPath(camino, a.luz(P.PELICULA, k, kv), style = Stroke(ancho))
        drawPath(camino, a.luz(P.MIEL, k, kv).copy(alpha = 0.55f), style = Stroke(ancho * 0.56f))
        val hueco = a.luz(P.CONSOLA_OSC, k * 0.4f)
        var acc = 0f
        for (i in 1 until n) {
            acc += (pts[i] - pts[i - 1]).getDistance()
            if (acc < ancho * 0.34f) continue
            acc = 0f
            val tg = pts[i + 1] - pts[i - 1]
            val l = tg.getDistance().coerceAtLeast(0.001f)
            val tx = tg.x / l; val ty = tg.y / l
            listOf(1f, -1f).forEach { s ->
                val o = Offset(pts[i].x - ty * s * ancho * 0.38f, pts[i].y + tx * s * ancho * 0.38f)
                drawLine(hueco, Offset(o.x - tx * ancho * 0.05f, o.y - ty * ancho * 0.05f), Offset(o.x + tx * ancho * 0.05f, o.y + ty * ancho * 0.05f), ancho * 0.12f, StrokeCap.Round)
            }
        }
        drawPath(camino, a.brillo(0.12f, k), style = Stroke(ancho * 0.1f))
    }

    /** La taza de lata esmaltada en [base] (el centro de su fondo), de ancho [an]: el esmalte hueso, el borde oscuro y el asa. */
    private fun DrawScope.taza(base: Offset, an: Float, a: AireFrente, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val alto = an * 1.05f
        val r = Rect(base.x - an / 2, base.y - alto, base.x + an / 2, base.y)
        val k = cerca(r.center); val kv = frio(r.center)
        contactoFrente(Offset(base.x, base.y), an * 1.4f, 5f * u, a, 0.4f)
        // El asa a la derecha.
        drawArc(a.luz(P.HUESO, k, kv), -80f, 170f, false, Offset(r.right - an * 0.22f, r.top + alto * 0.2f), Size(an * 0.5f, alto * 0.5f), style = Stroke(2.2f * u))
        drawArc(a.luz(P.CONSOLA, k, kv), -80f, 170f, false, Offset(r.right - an * 0.22f, r.top + alto * 0.2f), Size(an * 0.5f, alto * 0.5f), style = Stroke(0.6f * u))
        redondoFrente(r, a.luz(P.HUESO, k, kv), a.luz(P.HUESO_LUZ, k, kv), a.luz(P.HUESO_OSC, k * 0.7f, kv), if (a.oscuro && a.dormida) 1f else -1f)
        // El borde de esmalte oscuro, la boca y las saltaduras.
        drawRect(a.luz(P.CONSOLA, k, kv), r.topLeft, Size(r.width, 2.2f * u))
        drawOval(a.luz(P.CONSOLA_OSC, k * 0.6f, kv), Offset(r.left, r.top - 2f * u), Size(r.width, 4f * u))
        drawOval(a.luz(P.CONSOLA, k, kv), Offset(r.left, r.top - 2f * u), Size(r.width, 4f * u), style = Stroke(1f * u))
        drawPath(poligonoFrente(r.left + an * 0.62f, r.top + alto * 0.5f, r.left + an * 0.72f, r.top + alto * 0.46f, r.left + an * 0.7f, r.top + alto * 0.58f), a.luz(P.CONSOLA_OSC, k, kv))
        drawRect(a.luz(P.CONSOLA, k, kv), Offset(r.left, r.bottom - 1.6f * u), Size(r.width, 1.6f * u))
        if (a.oscuro) drawLine(a.filoVidrio(0.7f * kv), Offset(r.right - 0.6f * u, r.top + 2f * u), Offset(r.right - 0.6f * u, r.bottom - 2f * u), 1.2f * u)
    }

    // ---------------------------------------------------------------- la cúpula colgada

    /**
     * La lámpara de cúpula colgada del tubo, arriba a la izquierda (la del Ideario, en grande): el cable, el casquillo de cromo y la
     * cúpula de esmalte blanco con su claro y su sombra; por la boca, la bombilla. De día y de noche, encendida (en la noche, arde
     * con su halo); en los Sueños claros, bajada a ámbar; en los Sueños oscuros, apagada.
     */
    private fun DrawScope.cupula(w: Float, a: AireFrente, cerca: (Offset) -> Float) {
        val u = density
        val cx = w * M.CUPULA_X
        val boca = w * M.CUPULA_Y
        val rx = w * 0.072f
        val alto = w * 0.064f
        val yTubo = w * 0.032f
        val enciende = !a.dormida
        val luz = if (a.noche && !a.oscuro) P.GUARDIA else P.LAMPARA
        // El cable que baja del tubo, enganchado con su abrazadera.
        drawLine(a.sombra(0.3f), Offset(cx + 2f * u, yTubo + 2f * u), Offset(cx + 2f * u, boca - alto), 1.6f * u)
        drawLine(a.luz(P.BAQUELITA, 0.6f), Offset(cx, yTubo), Offset(cx, boca - alto), 1.6f * u)
        drawRoundRect(a.luz(P.CROMO_OSC, 0.6f), Offset(cx - 3f * u, yTubo - 4f * u), Size(6f * u, 8f * u), CornerRadius(1.5f * u))
        // El halo de la bombilla encendida.
        if (enciende) {
            val hr = if (a.oscuro) rx * 3.6f else rx * 2f
            val hc = Offset(cx, boca + rx * 0.5f)
            drawCircle(Brush.radialGradient(0f to luz.copy(alpha = if (a.oscuro) 0.7f else 0.38f), 0.3f to luz.copy(alpha = if (a.oscuro) 0.3f else 0.12f), 1f to luz.copy(alpha = 0f),
                center = hc, radius = hr), hr, hc)
        }
        // El casquillo.
        val casq = Rect(cx - rx * 0.2f, boca - alto - rx * 0.2f, cx + rx * 0.2f, boca - alto + rx * 0.08f)
        drawRect(Brush.horizontalGradient(listOf(a.luz(P.CROMO_LUZ, 0.7f), a.luz(P.CROMO, 0.6f), a.luz(P.CROMO_OSC, 0.4f)), casq.left, casq.right), casq.topLeft, casq.size)
        // La cúpula: esmalte blanco, el claro arriba a la izquierda y la sombra a la derecha; de noche, la luz la tiñe desde dentro.
        val domo = Path().apply {
            moveTo(cx - rx, boca)
            cubicTo(cx - rx * 0.95f, boca - alto * 0.62f, cx - rx * 0.5f, boca - alto * 1.02f, cx, boca - alto)
            cubicTo(cx + rx * 0.5f, boca - alto * 1.02f, cx + rx * 0.95f, boca - alto * 0.62f, cx + rx, boca)
            close()
        }
        val kD = if (a.oscuro) (if (enciende) 0.55f else 0.08f) else 1f
        drawPath(domo, Brush.horizontalGradient(listOf(a.luz(P.HUESO_LUZ, kD), a.luz(P.HUESO, kD * 0.85f), a.luz(P.HUESO_OSC, kD * 0.6f), a.luz(P.CHAPA_SOMBRA, kD * 0.5f)), cx - rx, cx + rx))
        clipPath(domo) {
            if (!a.oscuro) drawCircle(a.claro(0.55f), rx * 0.42f, Offset(cx - rx * 0.4f, boca - alto * 0.7f))
            // El aro de la boca, más hondo.
            drawRect(a.sombra(0.22f), Offset(cx - rx, boca - alto * 0.16f), Size(rx * 2, alto * 0.16f))
        }
        drawPath(domo, a.sombra(0.4f), style = Stroke(0.8f * u))
        // De noche, el borde de la boca encendido por la bombilla.
        if (a.oscuro && enciende) drawArc(P.LAMPARA.copy(alpha = 0.8f), 0f, 180f, false, Offset(cx - rx, boca - rx * 0.16f), Size(rx * 2, rx * 0.32f), style = Stroke(1.6f * u))
        drawArc(a.brillo(0.7f, 0.6f), 205f, 40f, false, Offset(cx - rx * 0.8f, boca - alto * 0.95f), Size(rx * 1.6f, alto * 1.8f), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        // La boca: el aro oscuro, el blanco de dentro y la bombilla.
        drawOval(a.luz(P.CHAPA_SOMBRA, 0.4f), Offset(cx - rx, boca - rx * 0.12f), Size(rx * 2, rx * 0.24f))
        if (enciende) {
            drawOval(mezcla(luz, Color.White, 0.5f), Offset(cx - rx * 0.86f, boca - rx * 0.07f), Size(rx * 1.72f, rx * 0.2f))
            drawOval(P.FUEGO, Offset(cx - rx * 0.32f, boca - rx * 0.02f), Size(rx * 0.64f, rx * 0.26f))
        } else {
            drawOval(a.luz(P.HUESO_OSC, 0.12f), Offset(cx - rx * 0.86f, boca - rx * 0.07f), Size(rx * 1.72f, rx * 0.2f))
            drawOval(a.luz(P.CROMO_OSC, 0.15f), Offset(cx - rx * 0.28f, boca - rx * 0.02f), Size(rx * 0.56f, rx * 0.22f))
        }
    }
}
