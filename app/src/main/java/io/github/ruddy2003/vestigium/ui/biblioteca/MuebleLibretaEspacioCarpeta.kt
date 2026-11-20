package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos

/**
 * La carpeta de anillas de la mesa del comedor de Espacio, la «bitácora de vuelo» de los momentos ([MuebleLibretaEspacio]):
 * tumbada en la mesa, un poco girada y vista desde arriba y de frente. La tapa de lona mostaza con su trama, su costura, el lomo
 * de las anillas con sus remaches y las esquinas gastadas; la cara de delante con la curva del lomo y su anilla de metal, el
 * bloque de hojas (que engorda con ellas) con los cantos de colores de los separadores; las pestañas de los separadores que
 * asoman por la derecha; el nombre y la cuenta en dos tiras de cinta de carrocero. Al abrirse, la tapa gira sobre las anillas
 * y se ve la hoja de arriba, la de la órbita del día.
 */
internal object CarpetaLibretaEspacio {
    private val P = PaletaEstanteEspacio
    /** La lona mostaza de la carpeta, su lomo, el forro de dentro, el papel de las hojas y la cinta de carrocero. */
    private val MOSTAZA = Color(0xFFC9963F)
    private val LOMO = Color(0xFF9C6E2C)
    private val FORRO = Color(0xFFB9AE96)
    private val HOJAS = Color(0xFFEDE5CF)
    private val CARROCERO = Color(0xFFEADFBF)
    /** Los colores de los separadores (los mismos plásticos que las pestañas del índice). */
    private val SEPARADORES = listOf(Color(0xFFE8843C), Color(0xFF5E9A52), Color(0xFF6A8CE8), Color(0xFFD8B45A), Color(0xFFD24A3C), Color(0xFF8C6CC8))

    private val trazo = Path()

    /** Cuántos separadores lleva: uno por cada doce hojas, de 1 a 8. */
    private fun cuantos(hojas: Int) = (1 + hojas / 12).coerceIn(1, 8)

    fun DrawScope.carpeta(g: GeoLibretaEspacio, e: EstadoLibreta, medidor: TextMeasurer, a: AireEstanteEspacio, luz: LuzLibretaEspacio, giro: Float) {
        sombra(g, a, luz)
        separadores(g, e.hojas, a, luz)
        frente(g, e.hojas, a, luz)
        if (giro > 0.001f) hojaDeArriba(g, e, medidor, a, luz)
        tapa(g, e.semilla, a, luz, giro)
    }

    /** La sombra de la carpeta en la mesa, corrida lejos de la luz, y la línea honda donde el canto de delante toca la mesa. */
    private fun DrawScope.sombra(g: GeoLibretaEspacio, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density
        val d = luz.caida(g.p(g.ancho / 2, g.hondo / 2, 0f), 7f * u)
        val p0 = g.p(0f, 0f, 0f); val p1 = g.p(g.ancho, 0f, 0f); val p2 = g.p(g.ancho, g.hondo, 0f); val p3 = g.p(0f, g.hondo, 0f)
        poligonoEnEspacio(trazo, p0.x + d.x, p0.y + d.y, p1.x + d.x, p1.y + d.y, p2.x + d.x * 1.4f, p2.y + d.y * 1.6f, p3.x + d.x, p3.y + d.y * 1.6f)
        drawPath(trazo, a.sombra(if (a.oscuro) 0.5f else 0.3f))
        drawLine(a.sombra(0.55f), p3 + Offset(2f * u, 1f * u), p2 + Offset(0f, 1f * u), 2f * u)
    }

    /** Las pestañas de los separadores que asoman por el canto derecho, entre las tapas: más cuantas más hojas. */
    private fun DrawScope.separadores(g: GeoLibretaEspacio, hojas: Int, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density
        val n = cuantos(hojas)
        withTransform({ transform(g.plano(g.grueso * 0.5f)) }) {
            for (q in 0 until n) {
                val y = g.hondo * (0.08f + 0.78f * q / maxOf(1, n - 1).toFloat() * (if (n == 1) 0f else 1f)) + 2f * u * azar(hojas, q)
                val r = Rect(g.ancho - 4f * u, y, g.ancho + 11f * u, y + 12f * u)
                val l = luz.en(g.p(g.ancho, y, g.grueso * 0.5f))
                drawRoundRect(a.sombra(0.35f), r.topLeft + Offset(1.2f * u, 1.6f * u), r.size, CornerRadius(3f * u))
                drawRoundRect(a.luz(SEPARADORES[q % SEPARADORES.size], l), r.topLeft, r.size, CornerRadius(3f * u))
                drawRect(a.claro(0.45f), Offset(r.left + 4f * u, r.top + 1f * u), Size(r.width - 6f * u, 1f * u))
                drawRect(a.luz(P.PAPEL, l).copy(alpha = 0.85f), Offset(r.left + 6f * u, r.top + 3.5f * u), Size(r.width - 9f * u, r.height - 7f * u))
            }
        }
    }

    /**
     * La cara de delante: la contratapa abajo y el canto de la tapa arriba, el bloque de hojas entre ellas con sus rayitas y los
     * cantos de colores de los separadores, y a la izquierda la curva del lomo con la anilla de metal que se ve por dentro.
     */
    private fun DrawScope.frente(g: GeoLibretaEspacio, hojas: Int, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density
        val T = g.grueso; val W = g.ancho
        val l = luz.en(g.p(W * 0.5f, g.hondo, T * 0.5f))
        val lona = a.luz(MOSTAZA, l)
        withTransform({ transform(g.frente()) }) {
            // La contratapa y el canto de la tapa, con su grosor de cartón forrado.
            drawRect(mezcla(lona, a.hondo, 0.3f), Offset(0f, T - 1.8f * u), Size(W, 1.8f * u))
            drawRect(lona, Offset(0f, 0f), Size(W, 2f * u))
            drawRect(a.claro(0.4f), Offset(0f, 0f), Size(W, 0.7f * u))
            // El bloque de hojas.
            val hj = Rect(g.lomo * 0.55f, 2f * u, W - 2.5f * u, T - 1.8f * u)
            if (hj.height > 0.5f) {
                drawRect(a.luz(HOJAS, l), hj.topLeft, hj.size)
                trazo.reset()
                var y = hj.top + 1f * u; var k = 0
                while (y < hj.bottom - 0.4f * u) {
                    trazo.moveTo(hj.left + 1.5f * u * azar(hojas, k), y); trazo.lineTo(hj.right - 1.5f * u * azar(hojas, k + 50), y)
                    y += maxOf(0.5f, (0.9f + 0.4f * azar(hojas, k + 90)) * u); k++
                }
                drawPath(trazo, a.sombra(0.2f), style = Stroke(0.5f * u))
                // Los cantos de los separadores: una raya de color a lo largo, a su altura.
                val n = cuantos(hojas)
                for (q in 0 until n) {
                    val yq = hj.top + hj.height * (q + 0.5f) / n
                    drawLine(a.luz(SEPARADORES[q % SEPARADORES.size], l), Offset(hj.left + 2f * u, yq), Offset(hj.right, yq), 0.9f * u)
                }
                drawRect(a.sombra(0.25f), Offset(hj.left, hj.bottom - 1f * u), Size(hj.width, 1f * u))
            }
            // La curva del lomo, que envuelve las hojas, y la anilla de metal que asoma por dentro.
            val rl = minOf(T * 0.5f, g.lomo * 0.6f)
            drawRoundRect(a.luz(LOMO, l), Offset(0f, 0f), Size(g.lomo, T), CornerRadius(rl))
            drawRect(a.claro(0.3f), Offset(rl * 0.4f, 1f * u), Size(1.2f * u, T - 2f * u))
            val ra = (T * 0.32f).coerceAtLeast(2f * u)
            drawArc(a.luz(P.ACERO, l), 90f, 180f, false, Offset(g.lomo * 0.72f - ra, T * 0.5f - ra), Size(ra * 2f, ra * 2f), style = Stroke(1.6f * u))
            drawArc(a.claro(0.6f), 160f, 60f, false, Offset(g.lomo * 0.72f - ra, T * 0.5f - ra), Size(ra * 2f, ra * 2f), style = Stroke(0.6f * u))
            drawRect(a.luz(mezcla(P.ACERO, P.GOMA, 0.3f), l), Offset(g.lomo * 0.72f, T * 0.5f - 1f * u), Size(4f * u, 2f * u))
        }
    }

    /**
     * La tapa: cerrada, en el plano de arriba de la carpeta; al abrirse, girada [giro] radianes sobre las anillas. Por fuera,
     * la lona; ya vuelta, el forro.
     */
    private fun DrawScope.tapa(g: GeoLibretaEspacio, semilla: Int, a: AireEstanteEspacio, luz: LuzLibretaEspacio, giro: Float) {
        val co = cos(giro)
        withTransform({ transform(g.plano(g.grueso, giro)) }) {
            val r = Rect(0f, 0f, g.ancho, g.hondo)
            if (co >= 0f) {
                lona(g, semilla, a, luz)
                if (giro > 0.001f) drawRect(a.sombra(0.45f * (1f - co)), r.topLeft, r.size)
            } else {
                drawRoundRect(a.luz(FORRO, luz.en(g.p(0f, g.hondo / 2, g.grueso))), r.topLeft, r.size, CornerRadius(5f * density))
                drawRect(a.sombra(0.35f * (1f + co)), r.topLeft, r.size)
            }
        }
    }

    /**
     * La lona de la tapa, en el plano de la tapa: el mostaza con su trama, la costura a mano, el lomo de las anillas con sus dos
     * hendiduras y sus remaches, las esquinas gastadas que dejan ver el cartón y la grasa de las manos junto al canto que se abre.
     */
    private fun DrawScope.lona(g: GeoLibretaEspacio, semilla: Int, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density
        val r = Rect(0f, 0f, g.ancho, g.hondo)
        val l = luz.en(g.p(g.ancho * 0.5f, g.hondo * 0.5f, g.grueso))
        drawRoundRect(a.luz(MOSTAZA, l), r.topLeft, r.size, CornerRadius(5f * u))
        with(TelasEspacio) { trama(r, if (a.oscuro) 0.4f else 0.6f) }
        // El lomo de las anillas: la lona más oscura, abombada, con sus dos hendiduras y los remaches del mecanismo.
        drawRoundRect(a.luz(LOMO, l), Offset.Zero, Size(g.lomo, r.height), CornerRadius(5f * u))
        drawRect(a.claro(0.28f), Offset(3f * u, 3f * u), Size(1.4f * u, r.height - 6f * u))
        listOf(g.lomo, g.lomo + 3.5f * u).forEach { x ->
            drawLine(a.sombra(0.45f), Offset(x, 2f * u), Offset(x, r.height - 2f * u), 1.1f * u)
            drawLine(a.claro(0.3f), Offset(x + 1.1f * u, 2f * u), Offset(x + 1.1f * u, r.height - 2f * u), 0.7f * u)
        }
        listOf(0.2f, 0.8f).forEach { fy -> remacheEspacio(Offset(g.lomo * 0.5f, r.height * fy), 1.7f * u, a.luz(P.ACERO, l), a) }
        // La costura a mano, por dentro del borde.
        drawPath(puntadasEspacio(trazo, Rect(g.lomo + 8f * u, 4f * u, r.right - 4f * u, r.bottom - 4f * u), semilla, u),
            a.luz(mezcla(P.TELA, P.PAPEL, 0.3f), l).copy(alpha = 0.6f), style = Stroke(0.7f * u))
        // Las esquinas de la derecha gastadas: la lona pelada deja ver el cartón gris.
        listOf(Offset(r.right, r.top), Offset(r.right, r.bottom)).forEachIndexed { i, c ->
            val s = if (i == 0) 1f else -1f
            poligonoEnEspacio(trazo, c.x, c.y + s * 0.5f * u, c.x - 9f * u, c.y + s * 0.5f * u, c.x, c.y + s * 8f * u)
            drawPath(trazo, a.luz(mezcla(P.CASCO_OSC, P.TELA, 0.4f), l))
            drawLine(a.claro(0.35f), Offset(c.x - 11f * u, c.y + s * 1.5f * u), Offset(c.x - 1.5f * u, c.y + s * 9.5f * u), 1f * u)
        }
        // La grasa de las manos junto al canto que se abre: rayas tenues a lo largo.
        trazo.reset()
        for (k in 0 until 4) {
            val x = r.right - (6f + 9f * azar(semilla, 70 + k)) * u
            trazo.moveTo(x, r.height * (0.2f + 0.15f * k)); trazo.lineTo(x - 2f * u, r.height * (0.32f + 0.15f * k))
        }
        drawPath(trazo, a.sombra(0.12f), style = Stroke(4f * u, cap = StrokeCap.Round))
    }

    /**
     * Las dos tiras de cinta de carrocero, rotas a mano en los extremos, con el nombre (grande) y debajo la cuenta, en el plano
     * de la tapa: se pintan después del mapa de luz, con la luz que de verdad les llega (casi no se apagan: se leen siempre).
     * Al abrirse, van con la tapa; cuando ya da la vuelta, no se ven.
     */
    fun DrawScope.rotulos(g: GeoLibretaEspacio, e: EstadoLibreta, medidor: TextMeasurer, a: AireEstanteEspacio, luz: LuzLibretaEspacio, giro: Float) {
        val u = density
        if (cos(giro) < 0.25f) return
        val izq = g.lomo + 12f * u
        val der = g.ancho - 14f * u
        val r1 = Rect(izq, g.hondo * 0.14f, der, g.hondo * 0.14f + 27f * u)
        val r2 = Rect(izq + 3f * u, r1.bottom + 6f * u, der, r1.bottom + 26f * u)
        withTransform({ transform(g.plano(g.grueso, giro)) }) {
            cinta(r1, -1.5f, a, luz.real(g.p(r1.center.x, r1.center.y, g.grueso)), 1441)
            cinta(r2, 1f, a, luz.real(g.p(r2.center.x, r2.center.y, g.grueso)), 1442)
            rotate(-1.5f, r1.center) {
                textoEn(medidor, e.nombre, Rect(r1.left + 8f * u, r1.top + 1f * u, r1.right - 8f * u, r1.bottom - 1f * u), LetraExtra.grotesk, P.TINTA, 15.5f, peso = FontWeight.SemiBold, minimo = 6f)
            }
            rotate(1f, r2.center) {
                textoEn(medidor, e.cuenta, Rect(r2.left + 7f * u, r2.top + 1f * u, r2.right - 7f * u, r2.bottom - 1f * u), LetraExtra.grotesk, P.TINTA.copy(alpha = 0.9f), 11.5f, peso = FontWeight.Medium, minimo = 5f)
            }
        }
    }

    /** Una tira de cinta de carrocero en [r], girada [giro]°, con los extremos rotos en zigzag, su sombra y su brillo. */
    private fun DrawScope.cinta(r: Rect, giro: Float, a: AireEstanteEspacio, l: LuzEstanteEspacio, semilla: Int) {
        val u = density
        rotate(giro, r.center) {
            trazo.reset()
            trazo.moveTo(r.left, r.top)
            trazo.lineTo(r.right, r.top)
            val dientes = 4
            for (k in 1..dientes) trazo.lineTo(r.right + (if (k % 2 == 1) -2f else 0.5f) * u * (0.6f + azar(semilla, k)), r.top + r.height * k / dientes)
            trazo.lineTo(r.left, r.bottom)
            for (k in dientes - 1 downTo 1) trazo.lineTo(r.left + (if (k % 2 == 1) 2f else -0.5f) * u * (0.6f + azar(semilla, k + 9)), r.top + r.height * k / dientes)
            trazo.close()
            translate(1f * u, 1.4f * u) { drawPath(trazo, a.sombra(0.32f)) }
            drawPath(trazo, mezcla(a.etiqueta(l), a.luz(CARROCERO, l), 0.6f))
            drawRect(a.claro(0.4f), Offset(r.left + 2f * u, r.top + 0.8f * u), Size(r.width - 6f * u, 0.8f * u))
            drawLine(a.sombra(0.08f), Offset(r.left + r.width * 0.3f, r.top + 2f * u), Offset(r.left + r.width * 0.36f, r.bottom - 2f * u), 1.5f * u)
        }
    }

    /** La hoja de arriba, que se ve al abrir: el papel con sus agujeros, sus renglones, la órbita del día dibujada a mano y la cuenta. */
    private fun DrawScope.hojaDeArriba(g: GeoLibretaEspacio, e: EstadoLibreta, medidor: TextMeasurer, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density
        withTransform({ transform(g.plano(g.grueso - 2f * u)) }) {
            val r = Rect(g.lomo * 0.6f, 2f * u, g.ancho - 3f * u, g.hondo - 2f * u)
            val l = luz.en(g.p(r.center.x, r.center.y, g.grueso))
            drawRect(a.etiqueta(l), r.topLeft, r.size)
            listOf(0.25f, 0.5f, 0.75f).forEach { fy -> drawCircle(a.sombra(0.5f), 2.4f * u, Offset(r.left + 7f * u, r.top + r.height * fy)) }
            trazo.reset()
            var y = r.top + 14f * u
            while (y < r.bottom - 4f * u) { trazo.moveTo(r.left + 16f * u, y); trazo.lineTo(r.right - 6f * u, y); y += maxOf(1f, 9f * u) }
            drawPath(trazo, P.TELA_AZUL.copy(alpha = 0.25f), style = Stroke(0.6f * u))
            val pc = Offset(r.left + r.width * 0.32f, r.center.y + 4f * u)
            val pr = minOf(r.height * 0.14f, 10f * u)
            drawCircle(a.luz(P.TELA_AZUL, l), pr, pc)
            drawOval(P.TINTA.copy(alpha = 0.6f), Offset(pc.x - pr * 2.6f, pc.y - pr * 1.1f), Size(pr * 5.2f, pr * 2.2f),
                style = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 1.6f * u))))
            textoEn(medidor, e.cuenta, Rect(r.left + r.width * 0.45f, r.top + 3f * u, r.right - 6f * u, r.top + 13f * u), LetraExtra.grotesk, P.TINTA.copy(alpha = 0.7f), 8f, minimo = 5f)
        }
    }
}
