package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import io.github.ruddy2003.vestigium.ui.TemaId
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Las banderitas «Hoy en la historia» (plan de historia y escenas, decisiones 12 y 13; tanda P4a)
// =====================================================================

/**
 * La banderita que cuelga dentro del cuadro, arriba a la izquierda: dice que manteniendo el dedo sobre el cuadro se
 * abre «Hoy en la historia». Cada tema tiene la suya, con su forma. No lleva texto (su nombre lo dice la acción de
 * accesibilidad del cuadro, en P6) y sus colores son fijos: no cambian con la hora de la escena, para que siempre se
 * distinga del paisaje.
 */
interface Banderita {
    /**
     * Dibuja la banderita en su lienzo de [ANCHO] × [ALTO] dp, colgada del borde de arriba. [lleno], de 0 a 1: cuánto
     * se ha llenado mientras se mantiene el dedo (sube desde abajo); en reposo es 0.
     */
    fun DrawScope.dibujar(lleno: Float)

    companion object {
        /** El tamaño de todas las banderitas, en dp. */
        const val ANCHO = 26
        const val ALTO = 40
    }
}

/**
 * La banderita de cada tema, o null si el tema no tiene cuadro (Navidad, Terminal y Básico) o todavía no le llega:
 * va siempre junto con su escena ([escenaDe]).
 */
fun banderitaHistoria(tema: TemaId): Banderita? = when (tema) {
    TemaId.ROMANO -> BanderitaRomano
    TemaId.URBE -> BanderitaUrbe
    TemaId.CIBER -> null            // Terminal: sin cuadro
    TemaId.NEON -> BanderitaNeon
    TemaId.HELENO -> BanderitaHeleno
    TemaId.EGIPTO -> null           // S9
    TemaId.MEDIEVAL -> BanderitaMedieval
    TemaId.TEMPLARIO -> null        // S11
    TemaId.DISTOPIA -> BanderitaDistopia
    TemaId.REFUGIO -> null          // S17
    TemaId.ORDEN -> null            // S18
    TemaId.VAPOR -> BanderitaVapor
    TemaId.LABORATORIO -> null      // S17
    TemaId.COLONOS -> null          // S19
    TemaId.NAPOLEON -> null         // S14
    TemaId.DECO -> null             // S15
    TemaId.BOTANICO -> null         // S13
    TemaId.RUINAS -> BanderitaRuinas
    TemaId.NAVIDAD -> null          // Navidad: sin cuadro
    TemaId.SEDA -> BanderitaSeda
    TemaId.EDO -> null              // S11
    TemaId.GOTICO -> BanderitaGotico
    TemaId.ARCANO -> BanderitaArcano
    TemaId.ESPACIO -> BanderitaEspacio
    TemaId.ABISMO -> null           // S16
    TemaId.ANDALUS -> BanderitaAndalus
    TemaId.MESO -> BanderitaMeso
    TemaId.MONGOL -> null           // S12
    TemaId.RENACIMIENTO -> BanderitaRenacimiento
    TemaId.PIRATA -> BanderitaPirata
    TemaId.URSS -> null             // S15
    TemaId.REVOLUCION -> BanderitaRevolucion
    TemaId.ANTIGUAS -> null         // S18
    TemaId.CUEVA -> null            // S10
    TemaId.BASICO -> null           // Básico: sin cuadro
}

/** Pone el lienzo de la banderita en sus 26 × 40 unidades, sea cual sea la densidad de la pantalla. */
private inline fun DrawScope.enUnidades(dibujo: DrawScope.() -> Unit) {
    val k = size.width / Banderita.ANCHO
    scale(k, k, Offset.Zero) { dibujo() }
}

/**
 * Helénico: una cinta de estela (tenia), como las que se ataban a las estelas y a las ramas de los vencedores: una
 * tira de lana púrpura que cuelga de una varilla de bronce, con cenefa de greca dorada, el sol de Vergina y los dos
 * cabos con flecos. Al mantener el dedo se va tiñendo de oro desde abajo.
 */
internal object BanderitaHeleno : Banderita {
    private val PURPURA = Color(0xFF5B2A6E)
    private val PURPURA_LUZ = Color(0xFF7E4692)
    private val PURPURA_OSC = Color(0xFF3A1848)
    private val ORO = Color(0xFFD9B455)
    private val ORO_LUZ = Color(0xFFF6E3A0)
    private val BRONCE = Color(0xFF8A5E2A)

    override fun DrawScope.dibujar(lleno: Float) = enUnidades {
        // La sombra sobre el cielo, para que se despegue.
        val cuerpo = Path().apply {
            moveTo(3f, 2f); lineTo(23f, 2f); lineTo(23f, 31f)
            lineTo(18f, 37.5f); lineTo(13f, 31.5f); lineTo(8f, 37.5f); lineTo(3f, 31f); close()
        }
        drawPath(Path().apply { addPath(cuerpo, Offset(0.8f, 1.2f)) }, Color.Black.copy(alpha = 0.28f))
        drawPath(cuerpo, Brush.horizontalGradient(listOf(PURPURA_OSC, PURPURA, PURPURA_LUZ, PURPURA), 3f, 23f))
        // Lo que se ha llenado: oro que sube desde las puntas.
        if (lleno > 0f) clipPath(cuerpo) {
            val y = 38f - 36f * lleno.coerceIn(0f, 1f)
            drawRect(Brush.verticalGradient(listOf(ORO_LUZ, ORO), y, 38f), Offset(0f, y), Size(26f, 38f - y))
        }
        val tinta = if (lleno > 0.55f) PURPURA_OSC else ORO
        // Las cenefas: dos filetes y una greca entre ellos.
        drawLine(tinta, Offset(4.5f, 5.2f), Offset(21.5f, 5.2f), 0.7f); drawLine(tinta, Offset(4.5f, 9.8f), Offset(21.5f, 9.8f), 0.7f)
        val greca = Path()
        var x = 5f
        while (x < 20.5f) { greca.moveTo(x, 8.8f); greca.lineTo(x, 6.2f); greca.lineTo(x + 2.6f, 6.2f); greca.lineTo(x + 2.6f, 7.8f); greca.lineTo(x + 1.3f, 7.8f); x += 4f }
        drawPath(greca, tinta, style = Stroke(0.6f))
        drawLine(tinta, Offset(4.5f, 27.6f), Offset(21.5f, 27.6f), 0.7f)
        // El sol de Vergina: ocho rayos largos y ocho cortos.
        val c = Offset(13f, 18.6f)
        for (i in 0 until 16) {
            val a = (2 * PI * i / 16).toFloat(); val largo = if (i % 2 == 0) 6.4f else 4.2f
            drawLine(tinta, c + Offset(cos(a), sin(a)) * 2.2f, c + Offset(cos(a), sin(a)) * largo, if (i % 2 == 0) 0.9f else 0.6f, StrokeCap.Round)
        }
        drawCircle(tinta, 2f, c)
        // Los flecos de los dos cabos.
        for (px in listOf(8f, 18f)) for (dx in listOf(-1.4f, 0f, 1.4f)) drawLine(if (lleno > 0.05f) ORO else PURPURA_LUZ, Offset(px + dx * 0.4f, 36.6f), Offset(px + dx, 39.6f), 0.5f, StrokeCap.Round)
        // La varilla de bronce, con sus dos remates.
        drawRoundRect(Brush.verticalGradient(listOf(ORO_LUZ, ORO, BRONCE), 0f, 3.4f), Offset(1.4f, 0f), Size(23.2f, 3.4f), androidx.compose.ui.geometry.CornerRadius(1.7f))
        drawCircle(ORO, 1.9f, Offset(1.9f, 1.7f)); drawCircle(ORO, 1.9f, Offset(24.1f, 1.7f))
        drawCircle(ORO_LUZ, 0.7f, Offset(1.5f, 1.2f)); drawCircle(ORO_LUZ, 0.7f, Offset(23.7f, 1.2f))
    }
}
