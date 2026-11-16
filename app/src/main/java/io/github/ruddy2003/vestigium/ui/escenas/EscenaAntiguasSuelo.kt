package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.FachadaPosada.p
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.fijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima

/**
 * El suelo del delante de la puerta, con su uso: las losas en tres cuartos con el muro (cada una con su tono, su canto
 * gastado y la hierba de las juntas), la tierra pisada que baja a la plaza con las huellas de los cascos, la paja que
 * dejan las mulas y sus boñigas; con lluvia, mojado y con charcos que devuelven el cielo y la luz de la puerta; con nieve,
 * nieve con espesor y borde, pisada, y en invierno la zanja abierta a pala de la puerta a la plaza.
 */
internal object SueloAntiguas {
    private val C = ColorAntiguas

    /** Hasta dónde llegan las losas, en metros por delante del muro. */
    private const val LOSAS = 2.7f

    /** El borde de arriba del suelo de delante (por debajo, todo es primer plano). */
    private val BORDE = listOf(Offset(-10f, 257f), Offset(60f, 255f), Offset(130f, 251f), Offset(190f, 246f), Offset(240f, 241.5f), Offset(262f, 239f), Offset(392f, 226f))

    fun DrawScope.suelo(v: VistaAntiguas) {
        val az = fijo("suelo")
        val hierba = EscenaAntiguas.hierba(v.m)
        val forma = poligono(BORDE + listOf(Offset(392f, ALTO + 6f), Offset(-10f, ALTO + 6f)))
        val pe = v.pincelEn(Offset(160f, 270f))
        drawPath(forma, Brush.verticalGradient(0f to pe.medio(lerp(hierba, C.TIERRA, 0.45f)), 1f to pe.medio(lerp(hierba, C.TIERRA, 0.62f)), startY = 240f, endY = ALTO))
        clipPath(forma) {
            val puntos = (0 until 240).map { Offset(az.entre(-6f, 200f), az.entre(250f, ALTO)) }
            drawPoints(puntos.filterIndexed { i, _ -> i % 2 == 0 }, PointMode.Points, pe.luz(lerp(hierba, C.ORO, 0.3f)).copy(alpha = 0.5f), 1.6f, StrokeCap.Round)
            drawPoints(puntos.filterIndexed { i, _ -> i % 2 == 1 }, PointMode.Points, pe.sombra(lerp(hierba, C.PINO, 0.4f)).copy(alpha = 0.5f), 1.6f, StrokeCap.Round)
            // La tierra pisada: de la puerta a la plaza y de ahí al puente, más clara, con los bordes comidos de hierba.
            val pisada = poligono(listOf(p(-0.2f, 0f, 0f), p(1.1f, 0f, 0f), p(1.9f, 0f, LOSAS), p(3.4f, 0f, 4.6f), Offset(200f, 244f), Offset(184f, 252f), p(1f, 0f, 5f), p(-0.6f, 0f, 3.2f)))
            drawPath(pisada, pe.por(lerp(C.TIERRA, C.LANA, 0.32f), 0.15f).copy(alpha = 0.75f))
            losas(v, az)
            uso(v, az)
            when {
                v.nieveAbajo > 0.02f -> nieve(v, az)
                v.clima == Clima.LLUVIA || v.clima == Clima.TORMENTA -> mojado(v, az)
                else -> Unit
            }
        }
    }

    /** Las losas de delante de la puerta, en tres cuartos con el muro: cada una con su tono, su canto de luz, sus juntas con hierba. */
    private fun DrawScope.losas(v: VistaAntiguas, az: Azar) {
        val hierba = EscenaAntiguas.hierba(v.m)
        var s = FachadaPosada.S_FIN + 0.3f
        while (s > -1.4f) {
            val ds = az.entre(0.42f, 0.66f); var f = 0f
            while (f < LOSAS) {
                val df = az.entre(0.4f, 0.75f)
                fun j() = az.entre(-0.05f, 0.05f)
                val forma = poligono(p(s - 0.03f + j(), 0f, f + 0.03f + j()), p(s - ds + 0.03f + j(), 0f, f + 0.03f + j()), p(s - ds + 0.03f + j(), 0f, f + df - 0.03f + j()), p(s - 0.03f + j(), 0f, f + df - 0.03f + j()))
                val c = p(s - ds / 2f, 0f, f + df / 2f); val pe = v.pincelEn(c, lumbre = lumbreEn(v, s - ds / 2f, f + df / 2f))
                val tono = lerp(lerp(C.PIEDRA, C.TIERRA, 0.28f), if (az.real() < 0.5f) C.TIERRA else C.ROCA, az.entre(0f, 0.38f))
                val borde = (1f - ((f - 1.8f) / (LOSAS - 1.8f)).coerceIn(0f, 1f) * 0.6f)
                drawPath(forma, pe.por(tono, az.entre(-0.2f, 0.18f)).copy(alpha = borde))
                drawLine(pe.luz(lerp(tono, C.NIEVE, 0.3f)).copy(alpha = 0.45f * borde), p(s - 0.04f, 0f, f + 0.04f), p(s - ds + 0.04f, 0f, f + 0.04f), 0.7f)
                drawLine(pe.sombra(lerp(tono, C.ESCORIA, 0.4f)).copy(alpha = 0.5f * borde), p(s - 0.04f, 0f, f + df - 0.04f), p(s - ds + 0.04f, 0f, f + df - 0.04f), 0.8f)
                if (az.real() < 0.35f) drawLine(pe.por(lerp(hierba, C.PINO, 0.3f), 0f).copy(alpha = 0.7f * borde), p(s - ds, 0f, f), p(s - ds, 0f, f + df * 0.7f), 1.2f)
                f += df
            }
            s -= ds
        }
    }

    /** Cuánta lumbre de la puerta le llega de noche a un punto del suelo (en metros a lo largo del muro y por delante). */
    private fun lumbreEn(v: VistaAntiguas, s: Float, f: Float): Float {
        if (EscenaAntiguas.puerta(v.m) == EscenaAntiguas.Puerta.CERRADA) return 0f
        val ds = s - 0.5f; val d = kotlin.math.sqrt(ds * ds + f * f)
        return (v.encendido.sala * v.noche * (1f - d / 4.2f)).coerceIn(0f, 0.8f)
    }

    /**
     * El uso: las huellas de los cascos donde se atan las mulas y donde pasan, la paja que se les cae, sus boñigas y el
     * desgaste delante del umbral.
     */
    private fun DrawScope.uso(v: VistaAntiguas, az: Azar) {
        val pe = v.pincelEn(p(1.6f, 0f, 1f))
        val tierra = lerp(C.TIERRA, C.ESCORIA, 0.25f)
        // Las huellas de los cascos: medias lunas oscuras, en hileras que van de la argolla a la plaza.
        repeat(26) {
            val s = az.entre(1.1f, 3.6f); val f = az.entre(0.5f, 2.8f) + (s - 1.1f) * 0.4f
            val q = p(s, 0f, f); val k = FachadaPosada.escala(s, f) / 55f
            drawArc(pe.sombra(tierra).copy(alpha = 0.55f), 0f, 180f, false, Offset(q.x - 1.6f * k, q.y - 0.8f * k), Size(3.2f * k, 1.6f * k), style = Stroke(0.7f * k))
        }
        if (v.nieveAbajo > 0.6f) return
        // La paja, en briznas claras, alrededor de donde comen las mulas.
        val paja = lerp(C.ORO, C.LANA, 0.4f)
        repeat(40) {
            val q = p(az.entre(1.2f, 2.6f), 0f, az.entre(0.2f, 1.6f)); val k = FachadaPosada.escala(1.8f, 0.8f) / 55f
            drawLine(pe.por(paja, 0.3f).copy(alpha = 0.8f), q, q + Offset(az.entre(-3f, 3f) * k, az.entre(-1f, 1f) * k), 0.5f, StrokeCap.Round)
        }
        // Las boñigas, redondas y oscuras, con su lado de luz.
        for ((s, f) in listOf(2.4f to 1.9f, 2.9f to 2.6f, 3.3f to 1.4f)) {
            val q = p(s, 0f, f); val k = FachadaPosada.escala(s, f) / 55f
            for (j in 0..2) drawOval(pe.por(lerp(C.TIERRA, C.ESCORIA, 0.45f), 0.1f - j * 0.1f), Offset(q.x - (2f - j * 0.4f) * k + j * 1.2f * k, q.y - (1.2f + j * 0.4f) * k), Size((3.6f - j * 0.8f) * k, (1.6f - j * 0.2f) * k))
        }
        // El umbral gastado: la piedra más clara delante de la puerta, donde pisa todo el que entra.
        mancha(p(0.5f, 0f, 0.5f), 12f, 2.6f, pe.luz(lerp(C.PIEDRA, C.LANA, 0.3f)), 0.25f)
    }

    /**
     * La nieve del delante: recién caída, un velo blanco con las losas asomando; vieja, en manchas con espesor (su canto en
     * sombra azul y el lomo con luz) que quedan donde no se pisa; en invierno, el manto entero con la zanja abierta a pala
     * de la puerta a la plaza: dos paredes de nieve, la de dentro en sombra azul y la de fuera con su filo de luz, y las
     * losas mojadas al fondo.
     */
    private fun DrawScope.nieve(v: VistaAntiguas, az: Azar) {
        val n = v.nieveAbajo; val pe = v.pincelEn(Offset(200f, 270f))
        val blanca = pe.por(C.NIEVE, 0.3f); val azul = pe.sombra(C.NIEVE_SOMBRA)
        if (!v.invierno) {
            if (v.clima == Clima.NIEVE) drawRect(Brush.verticalGradient(0f to blanca.copy(alpha = 0f), 0.15f to blanca.copy(alpha = 0.55f), 1f to blanca.copy(alpha = 0.7f), startY = 228f, endY = ALTO), Offset(-10f, 228f), Size(410f, 70f))
            // Las manchas: cada una con su espesor (el canto azul abajo) y su lomo, más grandes cerca.
            repeat((14 + 30 * n).toInt()) {
                val q = Offset(az.entre(-6f, 386f), az.entre(244f, ALTO + 4f)); val w = az.entre(4f, 12f) * (0.5f + n) * (0.6f + (q.y - 230f) / 70f)
                val forma = TrazosValle.blando(q, w, w * 0.28f, az, 9, 0.3f)
                drawPath(forma, azul.copy(alpha = 0.8f))
                drawPath(TrazosValle.blando(q + Offset(0f, -w * 0.1f), w * 0.94f, w * 0.24f, az, 9, 0.3f), blanca)
                drawLine(pe.luz(C.NIEVE).copy(alpha = 0.6f), q + Offset(-w * 0.6f, -w * 0.3f), q + Offset(w * 0.5f, -w * 0.32f), 0.6f)
            }
            return
        }
        drawRect(Brush.verticalGradient(0f to blanca.copy(alpha = 0f), 0.06f to blanca, 1f to pe.por(C.NIEVE, 0.05f), startY = 230f, endY = ALTO), Offset(-10f, 230f), Size(410f, 70f))
        // La zanja: de la puerta sale hacia la plaza y baja al puente.
        val dentro = listOf(p(0.0f, 0f, 0.05f), p(0.4f, 0f, 1.2f), p(1.1f, 0f, 2.6f), p(2.4f, 0f, 4.4f), Offset(196f, 250f))
        val fuera = listOf(p(1.0f, 0f, 0.05f), p(1.3f, 0f, 1.0f), p(2.0f, 0f, 2.2f), p(3.4f, 0f, 3.8f), Offset(206f, 246f))
        val zanja = poligono(dentro + fuera.reversed())
        drawPath(zanja, pe.por(lerp(C.PIEDRA, C.ESCORIA, 0.25f), -0.1f))
        clipPath(zanja) { repeat(30) { val q = Offset(az.entre(196f, 340f), az.entre(244f, ALTO)); drawPath(TrazosValle.blando(q, az.entre(3f, 6f), 1f, az, 7, 0.2f), pe.por(lerp(C.PIEDRA, C.TIERRA, 0.3f), az.entre(-0.2f, 0.2f))) } }
        // Las paredes de la zanja: su espesor y su filo.
        for ((lado, luz) in listOf(dentro to false, fuera to true)) {
            val arriba = lado.map { it + Offset(0f, -4.5f) }
            drawPath(poligono(lado + arriba.reversed()), if (luz) azul else pe.por(C.NIEVE, -0.2f))
            drawPath(curva(arriba), (if (luz) pe.luz(C.NIEVE) else azul).copy(alpha = 0.85f), style = Stroke(1.3f))
        }
        // Las huellas que bajan a la plaza.
        repeat(16) { val q = Offset(az.entre(208f, 290f), az.entre(244f, 262f)); drawOval(azul.copy(alpha = 0.55f), Offset(q.x - 1.6f, q.y - 0.7f), Size(3.2f, 1.4f)) }
    }

    /** Con lluvia: la tierra y las losas oscuras y brillantes, y charcos que devuelven el cielo y, de noche, la luz de la puerta. */
    private fun DrawScope.mojado(v: VistaAntiguas, az: Azar) {
        drawRect(C.ESCORIA.copy(alpha = 0.2f), Offset(-10f, 228f), Size(410f, 70f))
        drawRect(Brush.verticalGradient(listOf(lerp(v.l.medio, Color.White, 0.2f).copy(alpha = 0f), lerp(v.l.medio, Color.White, 0.2f).copy(alpha = 0.1f)), 240f, ALTO), Offset(-10f, 240f), Size(410f, 50f))
        for ((s, f) in listOf(0.3f to 1.2f, 1.6f to 1.8f, -0.6f to 2.1f, 2.6f to 2.4f, 0.9f to 2.9f)) {
            val c = p(s, 0f, f); val w = FachadaPosada.escala(s, f) * az.entre(0.3f, 0.5f)
            val forma = TrazosValle.blando(c, w, w * 0.2f, az, 9, 0.2f)
            drawPath(forma, lerp(lerp(v.l.bajo, v.l.medio, 0.4f), C.ESCORIA, 0.3f).copy(alpha = 0.6f))
            drawPath(forma, lerp(v.l.alto, Color.White, 0.2f).copy(alpha = 0.3f), style = Stroke(0.6f))
            clipPath(forma) { if (v.encendido.sala * v.noche > 0.1f) reflejoRoto(c.x - w * 0.5f, c.x + w * 0.3f, c.y - 2f, 6f, C.LUMBRE, 0.65f * v.noche, az) }
        }
    }
}
