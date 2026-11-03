package io.github.ruddy2003.vestigium.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * El Vapor victoriano (tema «vapor»): la fecha como en las guías de ferrocarriles del siglo XIX, la hora del horario (en
 * doce horas, con «m.», «t.» y «n.» para la mañana, la tarde y la noche, como en esas guías), los números leídos en el
 * manómetro de una caldera inglesa (en libras por pulgada cuadrada) y los dibujos que comparten sus superficies: el
 * engrane con el manómetro (su emblema), el remache y la locomotora.
 *
 * Desde 1847 los ferrocarriles británicos llevaban todos la hora de Greenwich («railway time»), aunque los pueblos siguieran
 * con su hora local; por eso el tema dice «hora del ferrocarril».
 */
object Vapor {
    private val DIAS = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    private val MESES = listOf("enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre")

    /** Los días como en las columnas del horario: «LU.», «MA.», «MI.»… */
    val SEMANA = listOf("LU.", "MA.", "MI.", "JU.", "VI.", "SÁ.", "DO.")

    /** La lectura del manómetro: «17 lb» (libras por pulgada cuadrada). Los años y lo que pasa de 999 quedan tal cual. */
    fun numeral(n: Int): String = if (n in 1..999) "$n lb" else "$n"

    /** La hora del horario: «2.32 t.», «6.05 m.», «11.40 n.». De las 5 a las 11, mañana; de las 12 a las 19, tarde; lo demás, noche. */
    fun hora(h: Int, m: Int): String {
        val h12 = if (h % 12 == 0) 12 else h % 12
        val tramo = when (h) { in 5..11 -> "m."; in 12..19 -> "t."; else -> "n." }
        return "%d.%02d %s".format(h12, m, tramo)
    }

    /**
     * «Sábado 17 de octubre de 2026 · tren n.º 290»: cada día es un tren más, contado desde el primer registro ([tren]). El
     * día de hoy lleva además la hora del ferrocarril: «· 2.32 t. hora del ferrocarril».
     */
    fun fecha(d: java.time.LocalDate, tren: Long, hoy: Boolean): String =
        "${DIAS[d.dayOfWeek.value - 1]} ${d.dayOfMonth} de ${MESES[d.monthValue - 1]} de ${d.year} · tren n.º $tren" +
            if (hoy) java.time.LocalTime.now().let { " · " + hora(it.hour, it.minute) + " hora del ferrocarril" } else ""

    /** El mes como la guía mensual de ferrocarriles que se compraba en el andén: «Guía de octubre de 2026». */
    fun mes(n: Int, anio: Int? = null): String = "Guía de " + MESES[(n - 1).coerceIn(0, 11)] + (anio?.let { " de $it" } ?: "")

    // ------------------------------------------------------------ dibujos

    val LATON = Color(0xFFB8862B)
    val LATON_CLARO = Color(0xFFE2BE6A)
    val COBRE = Color(0xFF9A5A2E)
    val CARDENILLO = Color(0xFF4E8F7E)
    val LADRILLO = Color(0xFF9C3D24)
    val HOLLIN = Color(0xFF1E1A16)
    val HIERRO = Color(0xFF3B3A37)
    val GAS = Color(0xFFF2C46A)

    /** Un engrane de [dientes] dientes trapezoidales, con el radio de la punta [r] y el del pie [r] × [pie]. */
    fun engrane(c: Offset, r: Float, dientes: Int, pie: Float = 0.8f, giro: Double = 0.0): Path = Path().apply {
        val n = dientes * 4
        for (k in 0 until n) {
            val a = giro + 2 * PI * k / n
            val rr = if (k % 4 == 1 || k % 4 == 2) r else r * pie
            val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * sin(a)).toFloat()
            if (k == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /** Un punto en el círculo de centro [c] y radio [r], al ángulo [a] (en radianes, 0 a la derecha). */
    fun en(c: Offset, r: Float, a: Double): Offset = Offset(c.x + (r * cos(a)).toFloat(), c.y + (r * sin(a)).toFloat())
}

/** Un remache: la cabeza redonda y su brillo arriba a la izquierda. */
internal fun DrawScope.remacheVapor(c: Offset, r: Float, color: Color, brillo: Color = Color.White.copy(alpha = 0.45f)) {
    drawCircle(color, r, c)
    drawCircle(brillo, r * 0.38f, Offset(c.x - r * 0.3f, c.y - r * 0.3f))
}

/**
 * La esfera de un manómetro: el bisel, la escala de 270° con sus marcas (cada quinta, más larga), la zona roja del final
 * y la aguja en la fracción [lectura] (0 a 1) de la escala.
 */
internal fun DrawScope.manometroVapor(c: Offset, r: Float, bisel: Color, esfera: Color, tinta: Color, rojo: Color, lectura: Float) {
    drawCircle(bisel, r, c)
    drawCircle(esfera, r * 0.84f, c)
    val ini = 135.0; val barrido = 270.0
    drawArc(rojo.copy(alpha = 0.85f), (ini + barrido * 0.8).toFloat(), (barrido * 0.2).toFloat(), false,
        Offset(c.x - r * 0.66f, c.y - r * 0.66f), Size(r * 1.32f, r * 1.32f), style = Stroke(r * 0.1f))
    for (k in 0..20) {
        val a = Math.toRadians(ini + barrido * k / 20)
        val largo = if (k % 5 == 0) 0.2f else 0.1f
        drawLine(tinta, Vapor.en(c, r * 0.78f, a), Vapor.en(c, r * (0.78f - largo), a), if (k % 5 == 0) r * 0.045f else r * 0.025f)
    }
    val aa = Math.toRadians(ini + barrido * lectura.coerceIn(0f, 1f))
    drawLine(rojo, Vapor.en(c, r * 0.16f, aa + PI), Vapor.en(c, r * 0.66f, aa), r * 0.06f, StrokeCap.Round)
    drawCircle(tinta, r * 0.1f, c)
}

/**
 * El emblema del tema: un engrane de latón que gira despacio y, encima, el manómetro con su aguja que tiembla.
 * [t] va de 0 a 1 en el ciclo del emblema.
 */
internal fun DrawScope.engraneManometro(c: Offset, r: Float, laton: Color, bisel: Color, esfera: Color, tinta: Color, rojo: Color, t: Float) {
    drawPath(Vapor.engrane(c, r, 12, 0.82f, giro = t * 2 * PI / 12), laton)
    drawCircle(bisel, r * 0.74f, c, style = Stroke(r * 0.05f))
    for (k in 0 until 6) remacheVapor(Vapor.en(c, r * 0.7f, k * PI / 3 + PI / 6), r * 0.05f, bisel)
    val tiembla = 0.62f + 0.04f * sin(t * 2 * PI * 3).toFloat()
    manometroVapor(c, r * 0.6f, bisel, esfera, tinta, rojo, tiembla)
}
