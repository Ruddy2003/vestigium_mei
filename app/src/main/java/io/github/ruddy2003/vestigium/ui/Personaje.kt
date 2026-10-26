package io.github.ruddy2003.vestigium.ui

import io.github.ruddy2003.vestigium.data.esAnimo
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import kotlin.math.PI
import kotlin.math.sin

/**
 * Un personaje del tema que vive en Día: legionario, hoplita, copista, ciudadano, residente del refugio, caballero,
 * científica, colono, granadero, naturalista, duende, caravanero, monje, astronauta, alarife, jinete, pintor, obrero…
 * Se ve cansado si dormiste poco, sonríe con tu ánimo, presume tu racha y se viste para el clima de afuera.
 * Todo dibujado aquí: ningún personaje, logo ni diseño ajeno.
 */
object Personaje {
    class Estado(val cansado: Boolean, val animo: Double?, val racha: Int, val lluvia: Boolean, val frio: Boolean, val calor: Boolean, val noche: Boolean, val frase: String)

    private fun racha(hoy: String): Int {
        var f = if (Repo.dias[hoy]?.let { it.nota.isNotBlank() || it.valencia != null } == true) hoy else Fechas.sumar(hoy, -1)
        var n = 0
        while (Repo.dias[f]?.let { it.nota.isNotBlank() || it.valencia != null } == true) { n++; f = Fechas.sumar(f, -1) }
        return n
    }

    fun estado(hoy: String = Fechas.diaLogico()): Estado {
        val d = Repo.dias[hoy]
        val horas = Repo.salud[hoy]?.sueno_min?.let { it / 60.0 }
        val cansado = (horas != null && horas < 6.0) || (d?.sueno_calidad != null && d.sueno_calidad <= 2)
        val animo = d?.valencia?.toDouble() ?: Repo.momentos.filter { it.fecha == hoy && it.esAnimo }.mapNotNull { it.valencia }.lastOrNull()?.toDouble()
        val e = Repo.entorno[hoy]
        val lluvia = (e?.precip_mm ?: 0.0) >= 1.0 || (e?.codigo?.let { it in 51..67 || it in 80..82 || it >= 95 } == true)
        val frio = (e?.t_max ?: 99.0) < 14.0
        val calor = (e?.t_max ?: -99.0) >= 30.0
        val h = java.time.LocalTime.now().hour
        val noche = h >= 22 || h < 5
        val r = racha(hoy)
        val frase = when {
            noche && d?.nota.isNullOrBlank() -> "Ya es tarde. ¿Cerramos el día antes de dormir?"
            cansado && horas != null -> "Dormiste ${"%.1f".format(horas)} h. Hoy voy despacio, y tú también puedes."
            cansado -> "Dormimos mal. Un día suave también cuenta."
            r > 0 && r % 7 == 0 -> "¡$r días seguidos escribiendo! Sigamos."
            lluvia -> "Llueve afuera. Buen día para leer algo."
            frio -> "Hace frío: bufanda puesta."
            calor -> "Mucho calor hoy. Agua a la mano."
            animo != null && animo >= 6 -> "Hoy se siente bien. Anótalo para recordarlo."
            animo != null && animo <= 2 -> "Día pesado. Aquí sigo contigo."
            r >= 2 -> "Racha de $r días. Vamos por otro."
            else -> "¿Qué haremos hoy?"
        }
        return Estado(cansado, animo, r, lluvia, frio, calor, noche, frase)
    }

    private enum class Sombrero { CRESTA, CORINTIO, YELMO, GORRA, CASCO_OBRA, CAPUCHA_NEON, VISOR, GAFAS, ALA, BICORNIO, SALACOT, GORRO_NAVIDAD, TURBANTE, CAPUCHA, ESCAFANDRA, PUNTA, BOINA, USHANKA, LAUREL, FRIGIO }
    private enum class Objeto { LANZA, ESCUDO, ESPADA, CARPETA, LLAVE, TABLETA, LIBRO, MATRAZ, PALA, CATALEJO, LUPA, REGALO, FAROL, VELA, BANDERA, ARCO, PINCEL, MARTILLO, PERGAMINO, TRICOLOR }

    private class Atuendo(val sombrero: Sombrero, val objeto: Objeto, val capa: Boolean)

    private fun atuendo(t: TemaId): Atuendo = when (t) {
        TemaId.ROMANO -> Atuendo(Sombrero.CRESTA, Objeto.LANZA, true)
        TemaId.HELENO -> Atuendo(Sombrero.CORINTIO, Objeto.ESCUDO, true)
        TemaId.MEDIEVAL -> Atuendo(Sombrero.CAPUCHA, Objeto.PERGAMINO, false)
        TemaId.DISTOPIA -> Atuendo(Sombrero.GORRA, Objeto.CARPETA, false)
        TemaId.REFUGIO -> Atuendo(Sombrero.CASCO_OBRA, Objeto.LLAVE, false)
        TemaId.CIBER -> Atuendo(Sombrero.CAPUCHA_NEON, Objeto.TABLETA, false)
        TemaId.ORDEN -> Atuendo(Sombrero.VISOR, Objeto.ESPADA, true)
        TemaId.LABORATORIO -> Atuendo(Sombrero.GAFAS, Objeto.MATRAZ, false)
        TemaId.COLONOS -> Atuendo(Sombrero.ALA, Objeto.PALA, false)
        TemaId.NAPOLEON -> Atuendo(Sombrero.BICORNIO, Objeto.CATALEJO, true)
        TemaId.BOTANICO -> Atuendo(Sombrero.SALACOT, Objeto.LUPA, false)
        TemaId.NAVIDAD -> Atuendo(Sombrero.GORRO_NAVIDAD, Objeto.REGALO, false)
        TemaId.SEDA -> Atuendo(Sombrero.TURBANTE, Objeto.FAROL, true)
        TemaId.GOTICO -> Atuendo(Sombrero.CAPUCHA, Objeto.VELA, true)
        TemaId.ESPACIO -> Atuendo(Sombrero.ESCAFANDRA, Objeto.BANDERA, false)
        TemaId.ANDALUS -> Atuendo(Sombrero.TURBANTE, Objeto.LIBRO, true)
        TemaId.MONGOL -> Atuendo(Sombrero.PUNTA, Objeto.ARCO, true)
        TemaId.RENACIMIENTO -> Atuendo(Sombrero.BOINA, Objeto.PINCEL, false)
        TemaId.URSS -> Atuendo(Sombrero.USHANKA, Objeto.MARTILLO, false)
        TemaId.REVOLUCION -> Atuendo(Sombrero.FRIGIO, Objeto.TRICOLOR, false)
        TemaId.ANTIGUAS -> Atuendo(Sombrero.CAPUCHA, Objeto.FAROL, true)
    }

    /** Dibuja al personaje en un lienzo de [s] (alto); [t] es la fase de la animación (0 a 1), [parpadeo] cierra los ojos. */
    fun DrawScope.dibujar(tema: TemaId, e: Estado, ropa: Color, acento: Color, piel: Color, tinta: Color, fondo: Color, t: Float, parpadeo: Boolean) {
        val a = atuendo(tema)
        val u = size.height / 120f
        val cx = size.width / 2
        val baja = if (e.cansado) 3 * u else 0f
        val bote = sin(t * 2 * PI).toFloat() * 1.6f * u
        val y0 = 18 * u + baja + bote
        val cabeza = Offset(cx, y0 + 18 * u)
        val r = 14 * u
        // Aura de racha larga.
        if (e.racha >= 30) drawCircle(acento.copy(alpha = 0.16f + 0.06f * sin(t * 2 * PI).toFloat()), 52 * u, Offset(cx, 64 * u))
        // Capa detrás del cuerpo.
        if (a.capa) drawPath(Path().apply { moveTo(cx - 14 * u, y0 + 34 * u); lineTo(cx + 14 * u, y0 + 34 * u); lineTo(cx + 24 * u, 112 * u); lineTo(cx - 24 * u, 112 * u); close() }, acento.copy(alpha = 0.85f))
        // Cuerpo (túnica, saco, bata…) y piernas.
        val cuerpo = Path().apply { moveTo(cx - 12 * u, y0 + 33 * u); lineTo(cx + 12 * u, y0 + 33 * u); lineTo(cx + 18 * u, y0 + 76 * u); lineTo(cx - 18 * u, y0 + 76 * u); close() }
        drawPath(cuerpo, ropa)
        drawLine(tinta, Offset(cx - 7 * u, y0 + 76 * u), Offset(cx - 8 * u, 116 * u), 4.5f * u, StrokeCap.Round)
        drawLine(tinta, Offset(cx + 7 * u, y0 + 76 * u), Offset(cx + 8 * u, 116 * u), 4.5f * u, StrokeCap.Round)
        drawLine(acento, Offset(cx - 16 * u, y0 + 58 * u), Offset(cx + 16 * u, y0 + 58 * u), 2.4f * u)
        // Brazos: el derecho sostiene el objeto; el izquierdo, el paraguas si llueve.
        val manoD = Offset(cx + 24 * u, y0 + 58 * u)
        val manoI = if (e.lluvia) Offset(cx - 22 * u, y0 + 40 * u) else Offset(cx - 22 * u, y0 + 60 * u)
        drawLine(ropa, Offset(cx + 11 * u, y0 + 37 * u), manoD, 5f * u, StrokeCap.Round)
        drawLine(ropa, Offset(cx - 11 * u, y0 + 37 * u), manoI, 5f * u, StrokeCap.Round)
        drawCircle(piel, 3 * u, manoD); drawCircle(piel, 3 * u, manoI)
        objeto(a.objeto, manoD, u, acento, tinta, ropa, fondo)
        // Cuello y bufanda si hace frío.
        if (e.frio) {
            drawRoundRect(acento, Offset(cx - 12 * u, y0 + 30 * u), Size(24 * u, 6 * u), androidx.compose.ui.geometry.CornerRadius(3 * u, 3 * u))
            drawLine(acento, Offset(cx + 6 * u, y0 + 34 * u), Offset(cx + 9 * u, y0 + 48 * u), 4 * u, StrokeCap.Round)
        }
        // Cabeza, ojos y boca.
        val inclina = if (e.cansado) 8f else 0f
        rotate(inclina, cabeza) {
            drawCircle(piel, r, cabeza)
            drawCircle(tinta.copy(alpha = 0.6f), r, cabeza, style = Stroke(1.2f * u))
            val ojoY = cabeza.y - 1 * u
            listOf(-5f, 5f).forEach { dx ->
                val o = Offset(cabeza.x + dx * u, ojoY)
                if (parpadeo || e.cansado) drawLine(tinta, o - Offset(2.4f * u, 0f), o + Offset(2.4f * u, if (e.cansado) 0.8f * u else 0f), 1.4f * u, StrokeCap.Round)
                else drawCircle(tinta, 1.9f * u, o)
            }
            // La boca según el ánimo: de 1 (triste) a 7 (feliz).
            val v = ((e.animo ?: 5.0) - 4.0) / 3.0
            val boca = Path().apply {
                moveTo(cabeza.x - 5 * u, cabeza.y + 6 * u)
                quadraticTo(cabeza.x, cabeza.y + 6 * u + (v * 5 * u).toFloat(), cabeza.x + 5 * u, cabeza.y + 6 * u)
            }
            drawPath(boca, tinta, style = Stroke(1.5f * u, cap = StrokeCap.Round))
            if (e.calor) drawPath(Path().apply { val g = Offset(cabeza.x + 11 * u, cabeza.y - 6 * u); moveTo(g.x, g.y - 3 * u); quadraticTo(g.x + 2.5f * u, g.y + 1 * u, g.x, g.y + 2 * u); quadraticTo(g.x - 2.5f * u, g.y + 1 * u, g.x, g.y - 3 * u); close() }, Color(0xFF7FC8F8))
            sombrero(a.sombrero, cabeza, r, u, acento, ropa, tinta, fondo)
        }
        // Medalla de racha (una semana o más).
        if (e.racha >= 7) {
            val m = Offset(cx - 5 * u, y0 + 44 * u)
            drawLine(acento, m - Offset(2 * u, 6 * u), m, 1.2f * u); drawLine(acento, m + Offset(2 * u, -6 * u), m, 1.2f * u)
            drawCircle(Color(0xFFE3B748), 3.2f * u, m); drawCircle(tinta.copy(alpha = 0.5f), 3.2f * u, m, style = Stroke(0.8f * u))
        }
        // Paraguas y gotas.
        if (e.lluvia) {
            val p = manoI
            drawLine(tinta, p, p - Offset(0f, 26 * u), 1.4f * u)
            val tope = p - Offset(0f, 28 * u)
            drawArc(acento, 180f, 180f, true, Offset(tope.x - 22 * u, tope.y - 12 * u), Size(44 * u, 24 * u))
            for (i in 0 until 7) {
                val x = (i * 17f + t * 40f) % 100f
                val yy = ((i * 29f + t * 120f) % 110f)
                drawLine(Color(0xFF7FB6E8).copy(alpha = 0.7f), Offset(x / 100f * size.width, yy * u), Offset(x / 100f * size.width - 1.5f * u, yy * u + 5 * u), 1f * u)
            }
        } else if (e.calor) {
            val s = Offset(size.width - 12 * u, 12 * u)
            drawCircle(Color(0xFFF2C14E), 6 * u, s)
            for (i in 0 until 8) { val an = i * PI / 4 + t * PI / 4; drawLine(Color(0xFFF2C14E), s + Offset((9 * u * kotlin.math.cos(an)).toFloat(), (9 * u * sin(an)).toFloat()), s + Offset((12 * u * kotlin.math.cos(an)).toFloat(), (12 * u * sin(an)).toFloat()), 1.2f * u) }
        }
        // Zetas de sueño en la noche.
        if (e.noche) {
            val z = Offset(cabeza.x + 16 * u, cabeza.y - 18 * u - t * 6 * u)
            listOf(0f to 1f, 6f to 0.7f).forEach { (d, k) ->
                val o = z + Offset(d * u, -d * u)
                val l = 4 * u * k
                drawPath(Path().apply { moveTo(o.x, o.y); lineTo(o.x + l, o.y); lineTo(o.x, o.y + l); lineTo(o.x + l, o.y + l) }, tinta.copy(alpha = 0.7f), style = Stroke(1.1f * u))
            }
        }
    }

    private fun DrawScope.sombrero(s: Sombrero, c: Offset, r: Float, u: Float, acento: Color, ropa: Color, tinta: Color, fondo: Color) {
        val top = c.y - r
        when (s) {
            Sombrero.CRESTA -> { // Casco de legionario con cresta de crin.
                drawArc(Color(0xFFB0B7BE), 180f, 180f, true, Offset(c.x - r * 1.05f, top - 1 * u), Size(r * 2.1f, r * 1.6f))
                drawArc(acento, 200f, 140f, false, Offset(c.x - r * 0.9f, top - 9 * u), Size(r * 1.8f, r * 1.4f), style = Stroke(5 * u, cap = StrokeCap.Round))
                drawLine(Color(0xFF8A9096), Offset(c.x - r, c.y - 2 * u), Offset(c.x - r * 0.8f, c.y + 7 * u), 2.5f * u)
            }
            Sombrero.LAUREL -> for (i in 0 until 7) { val an = PI + i * PI / 6; drawOval(Color(0xFF6E9B4A), Offset((c.x + r * kotlin.math.cos(an)).toFloat() - 3 * u, (c.y - 4 * u + r * 0.6f * sin(an)).toFloat() - 1.5f * u), Size(6 * u, 3 * u)) }
            Sombrero.CORINTIO -> { // Casco griego con penacho y protector nasal.
                drawArc(Color(0xFFC9A04A), 180f, 200f, true, Offset(c.x - r * 1.05f, top - 2 * u), Size(r * 2.1f, r * 2.0f))
                drawRect(fondo.copy(alpha = 0f), Offset.Zero, Size.Zero)
                drawLine(Color(0xFF8C6E2F), Offset(c.x, top), Offset(c.x, c.y + 4 * u), 2 * u)
                drawArc(acento, 190f, 160f, false, Offset(c.x - r * 1.1f, top - 12 * u), Size(r * 2.2f, r * 1.6f), style = Stroke(4 * u, cap = StrokeCap.Round))
            }
            Sombrero.YELMO -> drawArc(Color(0xFF9AA2AA), 180f, 180f, true, Offset(c.x - r * 1.05f, top - 2 * u), Size(r * 2.1f, r * 2.2f))
            Sombrero.GORRA -> { // Gorra gris de funcionario.
                drawRoundRect(Color(0xFF55595D), Offset(c.x - r, top - 3 * u), Size(r * 2, 7 * u), androidx.compose.ui.geometry.CornerRadius(2 * u, 2 * u))
                drawRect(tinta, Offset(c.x - r * 1.15f, top + 3 * u), Size(r * 1.4f, 2.2f * u))
                drawCircle(acento, 2 * u, Offset(c.x, top))
            }
            Sombrero.CASCO_OBRA -> {
                drawArc(Color(0xFFF2C14E), 180f, 180f, true, Offset(c.x - r * 1.05f, top - 2 * u), Size(r * 2.1f, r * 1.5f))
                drawRect(Color(0xFFD9A93A), Offset(c.x - r * 1.25f, top + 4 * u), Size(r * 2.5f, 2 * u))
            }
            Sombrero.CAPUCHA_NEON -> {
                drawArc(ropa, 160f, 220f, false, Offset(c.x - r * 1.25f, top - 3 * u), Size(r * 2.5f, r * 2.6f), style = Stroke(5 * u))
                drawRect(acento.copy(alpha = 0.85f), Offset(c.x - r * 0.75f, c.y - 4 * u), Size(r * 1.5f, 3.5f * u))
            }
            Sombrero.VISOR -> {
                drawArc(Color(0xFF8E979F), 180f, 180f, true, Offset(c.x - r * 1.1f, top - 3 * u), Size(r * 2.2f, r * 2.2f))
                drawRect(tinta, Offset(c.x - r * 0.8f, c.y - 4 * u), Size(r * 1.6f, 2.4f * u))
                drawLine(acento, Offset(c.x, top - 3 * u), Offset(c.x, top - 9 * u), 3 * u, StrokeCap.Round)
            }
            Sombrero.GAFAS -> {
                listOf(-5f, 5f).forEach { dx -> drawCircle(acento, 3.8f * u, Offset(c.x + dx * u, c.y - 1 * u), style = Stroke(1.3f * u)) }
                drawLine(acento, Offset(c.x - 1.2f * u, c.y - 1 * u), Offset(c.x + 1.2f * u, c.y - 1 * u), 1.2f * u)
                drawArc(tinta, 200f, 140f, false, Offset(c.x - r, top - 1 * u), Size(r * 2, r * 1.2f), style = Stroke(3 * u))
            }
            Sombrero.ALA -> {
                drawOval(Color(0xFF8A6A44), Offset(c.x - r * 1.5f, top + 2 * u), Size(r * 3f, 5 * u))
                drawRoundRect(Color(0xFF7A5C38), Offset(c.x - r * 0.8f, top - 7 * u), Size(r * 1.6f, 10 * u), androidx.compose.ui.geometry.CornerRadius(3 * u, 3 * u))
                drawRect(acento, Offset(c.x - r * 0.8f, top), Size(r * 1.6f, 2 * u))
            }
            Sombrero.BICORNIO -> {
                drawPath(Path().apply { moveTo(c.x - r * 1.6f, top + 3 * u); quadraticTo(c.x, top - 16 * u, c.x + r * 1.6f, top + 3 * u); quadraticTo(c.x, top - 3 * u, c.x - r * 1.6f, top + 3 * u); close() }, Color(0xFF1E2230))
                drawCircle(acento, 2.6f * u, Offset(c.x + 3 * u, top - 4 * u))
            }
            Sombrero.SALACOT -> {
                drawArc(Color(0xFFE6D6AE), 180f, 180f, true, Offset(c.x - r * 1.5f, top - 5 * u), Size(r * 3f, r * 1.7f))
                drawRect(acento, Offset(c.x - r * 0.95f, top + 0.5f * u), Size(r * 1.9f, 2 * u))
            }
            Sombrero.GORRO_NAVIDAD -> {
                drawPath(Path().apply { moveTo(c.x - r, top + 3 * u); quadraticTo(c.x + 2 * u, top - 18 * u, c.x + r * 1.5f, top - 4 * u); lineTo(c.x + r, top + 3 * u); close() }, Color(0xFFD7263D))
                drawRoundRect(Color.White, Offset(c.x - r * 1.05f, top + 1 * u), Size(r * 2.1f, 5 * u), androidx.compose.ui.geometry.CornerRadius(2.5f * u, 2.5f * u))
                drawCircle(Color.White, 3 * u, Offset(c.x + r * 1.5f, top - 4 * u))
            }
            Sombrero.TURBANTE -> {
                drawOval(Color(0xFFF1E7D2), Offset(c.x - r * 1.1f, top - 8 * u), Size(r * 2.2f, r * 1.5f))
                drawArc(acento, 200f, 140f, false, Offset(c.x - r, top - 6 * u), Size(r * 2f, r * 1.1f), style = Stroke(1.6f * u))
                drawCircle(acento, 2.2f * u, Offset(c.x, top - 2 * u))
            }
            Sombrero.CAPUCHA -> drawArc(ropa, 150f, 240f, false, Offset(c.x - r * 1.3f, top - 4 * u), Size(r * 2.6f, r * 2.7f), style = Stroke(6 * u))
            Sombrero.ESCAFANDRA -> {
                drawCircle(Color(0xFFBFD7F2).copy(alpha = 0.35f), r * 1.45f, c)
                drawCircle(Color(0xFFE8ECF8), r * 1.45f, c, style = Stroke(2.4f * u))
                drawLine(Color.White.copy(alpha = 0.8f), Offset(c.x - r * 0.9f, c.y - r * 0.6f), Offset(c.x - r * 0.5f, c.y - r * 1.0f), 2 * u, StrokeCap.Round)
                drawLine(acento, Offset(c.x + r, top - 2 * u), Offset(c.x + r * 1.3f, top - 9 * u), 1.4f * u); drawCircle(acento, 1.8f * u, Offset(c.x + r * 1.3f, top - 9 * u))
            }
            Sombrero.PUNTA -> {
                drawPath(Path().apply { moveTo(c.x - r * 1.1f, top + 4 * u); lineTo(c.x, top - 14 * u); lineTo(c.x + r * 1.1f, top + 4 * u); close() }, acento)
                drawRoundRect(Color(0xFF8A6A44), Offset(c.x - r * 1.15f, top + 1 * u), Size(r * 2.3f, 5 * u), androidx.compose.ui.geometry.CornerRadius(2.5f * u, 2.5f * u))
            }
            Sombrero.BOINA -> {
                drawOval(acento, Offset(c.x - r * 1.25f, top - 6 * u), Size(r * 2.4f, r * 0.9f))
                drawLine(acento, Offset(c.x, top - 6 * u), Offset(c.x + 1 * u, top - 9 * u), 1.4f * u)
            }
            Sombrero.USHANKA -> {
                drawRoundRect(Color(0xFF6B5B4B), Offset(c.x - r * 1.15f, top - 6 * u), Size(r * 2.3f, 11 * u), androidx.compose.ui.geometry.CornerRadius(4 * u, 4 * u))
                drawRoundRect(Color(0xFF6B5B4B), Offset(c.x - r * 1.25f, c.y - 4 * u), Size(5 * u, 11 * u), androidx.compose.ui.geometry.CornerRadius(2 * u, 2 * u))
                drawRoundRect(Color(0xFF6B5B4B), Offset(c.x + r * 1.25f - 5 * u, c.y - 4 * u), Size(5 * u, 11 * u), androidx.compose.ui.geometry.CornerRadius(2 * u, 2 * u))
                drawCircle(acento, 2.4f * u, Offset(c.x, top - 1 * u))
            }
            Sombrero.FRIGIO -> { // Gorro frigio rojo con la punta caída hacia delante y su escarapela.
                drawPath(Path().apply {
                    moveTo(c.x - r * 1.1f, top + 4 * u); quadraticTo(c.x - r * 1.2f, top - 10 * u, c.x + 2 * u, top - 13 * u)
                    quadraticTo(c.x + r * 1.5f, top - 13 * u, c.x + r * 1.35f, top - 2 * u); quadraticTo(c.x + r * 0.9f, top - 6 * u, c.x + r * 1.05f, top + 4 * u); close()
                }, Color(0xFFB22234))
                drawRect(Color(0xFFF1E7D2), Offset(c.x - r * 1.12f, top + 1.5f * u), Size(r * 2.2f, 3 * u))
                drawCircle(Color.White, 3.2f * u, Offset(c.x - r * 0.55f, top - 1 * u)); drawCircle(Color(0xFFB22234), 3 * u, Offset(c.x - r * 0.55f, top - 1 * u), style = Stroke(1.2f * u))
                drawCircle(Color(0xFF1F3A6E), 1.1f * u, Offset(c.x - r * 0.55f, top - 1 * u))
            }
        }
    }

    private fun DrawScope.objeto(o: Objeto, m: Offset, u: Float, acento: Color, tinta: Color, ropa: Color, fondo: Color) {
        when (o) {
            Objeto.LANZA -> { drawLine(Color(0xFF8A6A44), m + Offset(0f, 12 * u), m - Offset(0f, 46 * u), 2 * u); drawPath(Path().apply { moveTo(m.x, m.y - 54 * u); lineTo(m.x + 3 * u, m.y - 45 * u); lineTo(m.x - 3 * u, m.y - 45 * u); close() }, Color(0xFFB0B7BE)) }
            Objeto.ESCUDO -> { drawCircle(acento, 11 * u, m + Offset(4 * u, 0f)); drawCircle(Color(0xFFC9A04A), 11 * u, m + Offset(4 * u, 0f), style = Stroke(2 * u)); drawCircle(Color(0xFFC9A04A), 3 * u, m + Offset(4 * u, 0f)) }
            Objeto.ESPADA -> { drawLine(Color(0xFFC9CED3), m, m - Offset(-2 * u, 34 * u), 2.6f * u, StrokeCap.Round); drawLine(acento, m - Offset(5 * u, 3 * u), m + Offset(5 * u, -3 * u), 2.4f * u) }
            Objeto.CARPETA -> { drawRect(Color(0xFFD9C9A0), m - Offset(4 * u, 10 * u), Size(14 * u, 18 * u)); drawLine(tinta, m - Offset(1 * u, 5 * u), m + Offset(7 * u, -5 * u), 1 * u); drawLine(tinta, m - Offset(1 * u, 1 * u), m + Offset(7 * u, -1 * u), 1 * u); drawRect(acento, m + Offset(2 * u, 4 * u), Size(6 * u, 2 * u)) }
            Objeto.LLAVE -> { drawLine(Color(0xFFB0B7BE), m, m - Offset(-6 * u, 20 * u), 2.4f * u, StrokeCap.Round); drawCircle(Color(0xFFB0B7BE), 4 * u, m - Offset(-6 * u, 22 * u), style = Stroke(2 * u)) }
            Objeto.TABLETA -> { drawRoundRect(tinta, m - Offset(2 * u, 12 * u), Size(12 * u, 17 * u), androidx.compose.ui.geometry.CornerRadius(2 * u, 2 * u)); drawRect(acento.copy(alpha = 0.8f), m - Offset(0.5f * u, 10 * u), Size(9 * u, 12 * u)) }
            Objeto.LIBRO -> { drawRect(acento, m - Offset(4 * u, 8 * u), Size(13 * u, 16 * u)); drawLine(Color(0xFFF1E7D2), m - Offset(2 * u, -6 * u), m + Offset(8 * u, 6 * u), 1.6f * u) }
            Objeto.MATRAZ -> { drawPath(Path().apply { moveTo(m.x + 2 * u, m.y - 14 * u); lineTo(m.x + 6 * u, m.y - 14 * u); lineTo(m.x + 6 * u, m.y - 8 * u); lineTo(m.x + 12 * u, m.y + 4 * u); lineTo(m.x - 4 * u, m.y + 4 * u); lineTo(m.x + 2 * u, m.y - 8 * u); close() }, Color(0xFFDDEEF5).copy(alpha = 0.7f))
                drawPath(Path().apply { moveTo(m.x - 2 * u, m.y); lineTo(m.x + 10 * u, m.y); lineTo(m.x + 12 * u, m.y + 4 * u); lineTo(m.x - 4 * u, m.y + 4 * u); close() }, acento) }
            Objeto.PALA -> { drawLine(Color(0xFF8A6A44), m + Offset(0f, 10 * u), m - Offset(0f, 30 * u), 2 * u); drawRoundRect(Color(0xFF9AA2AA), m + Offset(-4 * u, 10 * u), Size(8 * u, 9 * u), androidx.compose.ui.geometry.CornerRadius(2 * u, 2 * u)) }
            Objeto.CATALEJO -> rotate(-35f, m) { drawRoundRect(Color(0xFFC9A04A), m - Offset(0f, 2.5f * u), Size(20 * u, 5 * u), androidx.compose.ui.geometry.CornerRadius(1 * u, 1 * u)); drawRect(Color(0xFF8C6E2F), m + Offset(6 * u, -3 * u), Size(2 * u, 6 * u)) }
            Objeto.LUPA -> { drawLine(Color(0xFF8A6A44), m, m - Offset(-4 * u, 10 * u), 2.4f * u, StrokeCap.Round); drawCircle(Color(0xFFBFD7F2).copy(alpha = 0.5f), 6 * u, m - Offset(-8 * u, 16 * u)); drawCircle(acento, 6 * u, m - Offset(-8 * u, 16 * u), style = Stroke(1.8f * u)) }
            Objeto.REGALO -> { drawRect(Color(0xFF2E7D4F), m - Offset(5 * u, 6 * u), Size(14 * u, 12 * u)); drawLine(Color(0xFFF2C94C), m - Offset(-2 * u, 6 * u), m + Offset(2 * u, 6 * u), 2 * u); drawLine(Color(0xFFF2C94C), m - Offset(5 * u, 0f), m + Offset(9 * u, 0f), 2 * u) }
            Objeto.FAROL -> { drawLine(tinta, m, m + Offset(0f, 6 * u), 1 * u); drawRoundRect(Color(0xFFD7263D), m + Offset(-5 * u, 6 * u), Size(10 * u, 13 * u), androidx.compose.ui.geometry.CornerRadius(5 * u, 5 * u)); drawCircle(Color(0xFFF2C14E).copy(alpha = 0.6f), 3 * u, m + Offset(0f, 12.5f * u)) }
            Objeto.VELA -> { drawRect(Color(0xFFF1E7D2), m - Offset(2 * u, 14 * u), Size(5 * u, 14 * u)); drawOval(Color(0xFFF2C14E), m - Offset(1.5f * u, 20 * u), Size(4 * u, 6 * u)) }
            Objeto.BANDERA -> { drawLine(Color(0xFFC9CED3), m + Offset(0f, 10 * u), m - Offset(0f, 34 * u), 1.6f * u); drawRect(acento, m - Offset(0f, 34 * u), Size(14 * u, 9 * u)); drawCircle(fondo, 2 * u, m - Offset(-7 * u, 29.5f * u)) }
            Objeto.ARCO -> { drawArc(Color(0xFF8A6A44), -80f, 160f, false, Offset(m.x - 6 * u, m.y - 22 * u), Size(14 * u, 44 * u), style = Stroke(2 * u)); drawLine(tinta, Offset(m.x + 1 * u, m.y - 21 * u), Offset(m.x + 1 * u, m.y + 21 * u), 0.8f * u) }
            Objeto.PINCEL -> { drawLine(Color(0xFF8A6A44), m + Offset(-2 * u, 6 * u), m - Offset(-6 * u, 20 * u), 2 * u, StrokeCap.Round); drawCircle(acento, 2.6f * u, m - Offset(-7 * u, 22 * u)) }
            Objeto.MARTILLO -> { drawLine(Color(0xFF8A6A44), m + Offset(0f, 8 * u), m - Offset(0f, 18 * u), 2.4f * u); drawRect(Color(0xFF8E979F), m - Offset(6 * u, 22 * u), Size(12 * u, 5 * u)) }
            Objeto.TRICOLOR -> {
                drawLine(Color(0xFF8A6A44), m + Offset(0f, 10 * u), m - Offset(0f, 34 * u), 1.6f * u)
                listOf(Color(0xFF1F3A6E), Color.White, Color(0xFFB22234)).forEachIndexed { i, col -> drawRect(col, m - Offset(-i * 5 * u, 34 * u), Size(5 * u, 11 * u)) }
                drawCircle(Color(0xFFC9A04A), 1.4f * u, m - Offset(0f, 35 * u))
            }
            Objeto.PERGAMINO -> { drawRect(Color(0xFFEDDFC2), m - Offset(3 * u, 12 * u), Size(10 * u, 20 * u)); drawCircle(Color(0xFFD5C29A), 2.2f * u, m - Offset(-2 * u, 12 * u)); drawCircle(Color(0xFFD5C29A), 2.2f * u, m + Offset(2 * u, 8 * u)); drawLine(acento, m - Offset(1 * u, 6 * u), m + Offset(5 * u, -6 * u), 1 * u) }
        }
    }
}

/** El personaje dibujado, con su pequeña animación (respira y parpadea) si las animaciones están encendidas. */
@Composable
fun FiguraPersonaje(e: Personaje.Estado, modifier: Modifier = Modifier, tam: Dp = 96.dp, tema: TemaId = Estilo.tema) {
    val cs = MaterialTheme.colorScheme
    val animar = Repo.config.animaciones
    val trans = rememberInfiniteTransition(label = "personaje")
    val fase = trans.animateFloat(0f, 1f, infiniteRepeatable(tween(3200, easing = LinearEasing), RepeatMode.Restart), label = "fase")
    val t = if (animar) fase.value else 0.25f
    val parpadeo = animar && t in 0.48f..0.52f
    val piel = if (esOscuro()) Color(0xFFE2BFA0) else Color(0xFFD9AE8A)
    Canvas(modifier.size(width = tam * 0.85f, height = tam)) {
        with(Personaje) { dibujar(tema, e, cs.primary, cs.tertiary, piel, cs.onSurface, cs.surface, t, parpadeo) }
    }
}

/** En Día: el personaje con lo que te dice hoy. Se apaga en Apariencia. */
@Composable
fun TarjetaPersonaje(hoy: String, modifier: Modifier = Modifier) {
    if (!Repo.config.personaje) return
    val e = androidx.compose.runtime.remember(Repo.dias, Repo.salud, Repo.entorno, Repo.momentos, hoy) { Personaje.estado(hoy) }
    val cs = MaterialTheme.colorScheme
    Row(modifier.fillMaxWidth().padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        FiguraPersonaje(e, tam = 92.dp)
        Surface(shape = MaterialTheme.shapes.medium, color = cs.surface, border = androidx.compose.foundation.BorderStroke(1.dp, cs.outline), modifier = Modifier.weight(1f).padding(start = 8.dp)) {
            Column(Modifier.padding(12.dp)) {
                Text(e.frase, style = MaterialTheme.typography.bodyMedium)
                val extra = listOfNotNull(
                    if (e.racha >= 2) "racha ${e.racha}" else null, e.animo?.let { "ánimo ${"%.0f".format(it)}/7" },
                    Repo.entorno[hoy]?.t_max?.let { "${it.toInt()}°" },
                ).joinToString(" · ")
                if (extra.isNotEmpty()) Text(extra, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}
