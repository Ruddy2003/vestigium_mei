package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRomano.ALMENA
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRomano.BASE
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRomano.Puesto
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRomano.TORRE
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRomano.TRAMOS
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRomano.VANO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.antorcha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.brasas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.guirnalda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.pano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.volumen
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * El primer plano del cuadro de Romano (`EscenaRomano`): el camino de ronda con su parapeto (de troncos o de piedra,
 * tramo a tramo), la torre con su escalera, su farol y su vexilo, el puesto de guardia de Cayo con sus cosas, cada una
 * con su silueta (para la sombra que arroja), y los doce objetos del día.
 */
internal object CosasRomano {
    private val C = ColorRomano

    /** El ancho de un tramo del parapeto, lo que mide su almena y dónde empieza la cara de la torre. */
    private const val W = TORRE / TRAMOS
    private const val W_ALMENA = 32f
    private const val FRENTE = TORRE + 8f
    private const val PIE_TORRE = BASE + 5f

    /** El brasero del puesto y a qué escala va; el banco y dónde se pone encima el objeto del día. */
    val BRASERO = Offset(170f, 281.6f)
    private const val KB = 1.6f

    /** Dónde está la leña, al lado del brasero. */
    const val LENA_X = 204f
    private val SOBRE = Offset(262f, 254.4f)
    private const val KO = 1.6f
    private val FAROL = Offset(343f, 196f)

    /** En qué vano se sienta el gato a mirar el río: el del cuarto tramo, o el del tercero si ese está en obra. */
    fun miradorEn(m: Momento): Float = if (EscenaRomano.enObra(m) && EscenaRomano.tramosDePiedra(m) == 3) 2f * W + W_ALMENA + (W - W_ALMENA) / 2f else 3f * W + W_ALMENA + (W - W_ALMENA) / 2f

    /** Dónde está el objeto del día: casi todos, sobre el banco; el escudo y la jabalina, apoyados en el parapeto. */
    fun objetoEn(m: Momento): Offset = when (m.ficha.objeto) { "escudo" -> ESCUDO; "pilum" -> Offset(292f, 282f); else -> Offset(SOBRE.x, 282f) }

    private val ESCUDO = Offset(128f, 266f)

    /** El color de una cara plana: [mira] vale −1 si mira a la izquierda, 1 si a la derecha y 0 si de frente. */
    private fun cara(l: Luces, r: Rayo, c: Color, mira: Float): Color = when {
        mira == 0f -> l.tono(c)
        r.lat * mira > 0f -> l.tono(lerp(c, Color(0xFFFFF2D0), 0.34f * r.contraste))
        else -> l.tono(mult(c, r.color, 0.8f * r.contraste))
    }

    private fun mezcla(a: Float, b: Float, k: Float) = a + (b - a) * k

    // ---------------------------------------------------------------- El camino de ronda

    private enum class Tramo { EMPALIZADA, OBRA, PIEDRA }

    private fun tramo(m: Momento, i: Int): Tramo {
        val hechos = EscenaRomano.tramosDePiedra(m)
        return when { i < hechos -> Tramo.PIEDRA; i == hechos && EscenaRomano.enObra(m) -> Tramo.OBRA; else -> Tramo.EMPALIZADA }
    }

    /** Hasta dónde ha subido la piedra del tramo en obra: en el lado de la almena y en el del vano. */
    private fun cimaDeObra(m: Momento): Pair<Float, Float> {
        val hilada = 6.4f; val hiladas = ((BASE - ALMENA) / hilada * EscenaRomano.avanceDelTramo(m)).toInt().coerceAtLeast(1)
        val y = BASE - hiladas * hilada
        return maxOf(ALMENA, y) to maxOf(VANO, y)
    }

    /** La silueta del parapeto entero en este momento: sirve para que las sombras solo caigan donde hay muro. */
    private fun silueta(m: Momento): Path = Path().apply {
        for (i in 0 until TRAMOS) {
            val x0 = i * W; val x1 = x0 + W
            val (ya, yv) = if (tramo(m, i) == Tramo.OBRA) cimaDeObra(m) else ALMENA - 4f to VANO
            addRect(Rect(x0, ya, x0 + W_ALMENA, BASE)); addRect(Rect(x0 + W_ALMENA, yv, x1, BASE))
        }
        addRect(Rect(TORRE, -10f, ANCHO + 10f, ALTO)); addRect(Rect(-10f, BASE, ANCHO + 10f, ALTO + 10f))
    }

    fun DrawScope.caminoDeRonda(m: Momento, l: Luces, r: Rayo, est: Estacion) {
        val az = Azar(Semilla.de("romano", "ronda"))
        val nieve = EscenaRomano.nevado(m)
        // El parapeto, tramo a tramo, y el suelo de tablas.
        for (i in 0 until TRAMOS) {
            val x0 = i * W
            when (tramo(m, i)) {
                Tramo.EMPALIZADA -> empalizada(l, r, x0, x0 + W, Azar(Semilla.de("romano", "troncos", i)), nieve)
                Tramo.PIEDRA -> sillares(l, r, x0, x0 + W, ALMENA, VANO, Azar(Semilla.de("romano", "sillares", i)), nieve, rematado = true)
                Tramo.OBRA -> { val (ya, yv) = cimaDeObra(m); andamio(l, r, x0, x0 + W, nieve); sillares(l, r, x0, x0 + W, ya, yv, Azar(Semilla.de("romano", "sillares", i)), nieve, rematado = false); tajo(l, r, x0, x0 + W, ya, yv, Azar(Semilla.de("romano", "tajo", i))); acopio(l, r, x0) }
            }
        }
        suelo(m, l, r, az, nieve)
        torre(m, l, r, nieve)
        adornos(m, l)
        val cosas = cosasDelPuesto(m, l, r, nieve).sortedBy { it.pie.y }

        // Las sombras sobre el parapeto y el suelo: la de la torre, la de su alero, la de cada cosa.
        val muro = silueta(m)
        capaSombra(r) { c ->
            clipPath(muro) {
                // Bajo el remate de cada almena de piedra.
                for (i in 0 until TRAMOS) if (tramo(m, i) == Tramo.PIEDRA) drawRect(c, Offset(i * W, ALMENA + 2.6f), Size(W_ALMENA, (2.6f * r.tan * 0.5f).coerceIn(0.6f, 3.2f)))
                sombraArrojada(r, Path().apply { addRect(Rect(TORRE, 60f, ANCHO + 30f, PIE_TORRE)) }, Offset(FRENTE + 20f, PIE_TORRE), c, BASE, ALMENA - 4f, 0f, TORRE)
                // La galería y el alero, que vuelan, dan su sombra en la cara de la torre.
                val vuelo = (7f * r.tan * 0.5f).coerceIn(2f, 16f)
                clipRect(TORRE, 46f, ANCHO, PIE_TORRE) { drawRect(c, Offset(TORRE, 109f), Size(ANCHO - TORRE, vuelo)); drawRect(c, Offset(TORRE, 48f), Size(ANCHO - TORRE, vuelo * 0.6f)) }
                cosas.forEach { sombraArrojada(r, it.forma, it.pie, c, BASE, ALMENA - 4f, 0f, ANCHO) }
            }
        }
        cuerdaColgada(l, r)
        if (EscenaRomano.puesto(m) == Puesto.DE_GUARDIA) sago(l, r)
        cosas.forEach { it.pintar(this) }
        // La sombra de la torre también cae sobre las cosas que tiene al lado.
        capaSombra(r, 0.8f) { c ->
            val forma = Path().apply { addRect(Rect(TORRE, 60f, ANCHO + 30f, PIE_TORRE)) }
            cosas.filter { it.bajoElFollaje }.forEach { k -> clipPath(k.forma) { sombraArrojada(r, forma, Offset(FRENTE + 20f, PIE_TORRE), c, 150f, 100f, 0f, ANCHO) } }
        }
        if (nieve) nieveEncima(l, r, m)
    }

    /** Un tramo de empalizada: troncos hincados, con punta en la almena y cortados a ras en el vano, atados a un larguero. */
    private fun DrawScope.empalizada(l: Luces, r: Rayo, x0: Float, x1: Float, az: Azar, nieve: Boolean) {
        val n = 10; val ancho = (x1 - x0) / n
        for (j in 0 until n) {
            val a = x0 + j * ancho; val b = a + ancho
            val enAlmena = a + ancho / 2f < x0 + W_ALMENA
            val cima = if (enAlmena) ALMENA + az.entre(-2.6f, 2.4f) else VANO + az.entre(-1f, 1f)
            val propio = lerp(lerp(C.MADERA, C.MADERA_VIEJA, az.entre(0.2f, 0.8f)), C.MADERA_OSC, az.entre(0f, 0.25f))
            val luz = l.tono(lerp(propio, Color(0xFFFFF2D0), 0.36f * r.contraste)); val medio = l.tono(propio); val sombra = l.tono(mult(propio, r.color, 0.85f * r.contraste))
            val tronco = Path().apply {
                if (enAlmena) { moveTo(a, BASE); lineTo(a, cima + 5.4f); lineTo(a + ancho * 0.5f, cima); lineTo(b, cima + 5.4f); lineTo(b, BASE) }
                else { moveTo(a, BASE); lineTo(a, cima + 0.8f); lineTo(b, cima); lineTo(b, BASE) }
                close()
            }
            drawPath(tronco, Brush.horizontalGradient(if (r.lat >= 0f) listOf(sombra, medio, luz, medio) else listOf(medio, luz, medio, sombra), a, b))
            clipPath(tronco) {
                // La corteza: vetas, un nudo y, arriba, la madera gris de la intemperie.
                repeat(3) { val x = az.entre(a + 0.6f, b - 0.6f); val y = az.entre(cima + 4f, BASE - 8f); drawLine(l.tono(C.MADERA_OSC).copy(alpha = az.entre(0.25f, 0.5f)), Offset(x, y), Offset(x + az.entre(-0.6f, 0.6f), y + az.entre(6f, 16f)), 0.4f) }
                if (az.real() > 0.6f) { val k = Offset(az.entre(a + 1.4f, b - 1.4f), az.entre(cima + 10f, BASE - 8f)); drawOval(l.tono(C.MADERA_OSC).copy(alpha = 0.7f), Offset(k.x - 1f, k.y - 1.4f), Size(2f, 2.8f)) }
                drawRect(Brush.verticalGradient(listOf(l.tono(Color(0xFFB8B4A8)).copy(alpha = 0.45f), Color.Transparent), cima, cima + 12f), Offset(a, cima), Size(ancho, 12f))
                // El verdín del pie.
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFF6E7A4A)).copy(alpha = 0.4f)), BASE - 9f, BASE), Offset(a, BASE - 9f), Size(ancho, 9f))
            }
            if (!enAlmena) drawLine(l.tono(lerp(propio, Color.White, 0.4f)), Offset(a + 0.4f, cima + 0.6f), Offset(b - 0.4f, cima + 0.2f), 0.7f)     // el corte, claro
            drawLine(l.tono(Color(0xFF2E241C)).copy(alpha = 0.75f), Offset(b, cima + 4f), Offset(b, BASE), 0.55f)
            if (nieve) { if (enAlmena) drawPath(poligono(Offset(a + ancho * 0.18f, cima + 3.6f), Offset(a + ancho * 0.5f, cima - 0.4f), Offset(b - ancho * 0.18f, cima + 3.6f)), l.tono(C.NIEVE)) else drawRect(l.tono(C.NIEVE), Offset(a, cima - 1f), Size(ancho, 1.8f)) }
        }
        // El larguero, medio tronco tendido, y las ataduras de cuerda.
        val ly = 228f
        drawRect(Brush.verticalGradient(listOf(l.tono(lerp(C.MADERA, Color(0xFFFFF2D0), 0.3f)), l.tono(C.MADERA), l.tono(mult(C.MADERA_OSC, r.color, 0.6f))), ly, ly + 4.6f), Offset(x0, ly), Size(x1 - x0, 4.6f))
        drawLine(Color.Black.copy(alpha = 0.22f), Offset(x0, ly + 5.4f), Offset(x1, ly + 5.4f), 1.2f)
        for (j in listOf(1, 4, 7)) { val x = x0 + (j + 0.5f) * ancho; for (d in listOf(-1.2f, 0f, 1.2f)) drawLine(l.tono(C.LINO).copy(alpha = 0.9f), Offset(x + d - 1f, ly - 0.6f), Offset(x + d + 1f, ly + 5.2f), 0.55f) }
        if (nieve) drawLine(l.tono(C.NIEVE), Offset(x0, ly - 0.2f), Offset(x1, ly - 0.2f), 1.2f)
    }

    /**
     * Un tramo de sillares, con la almena hasta [ya] y el vano hasta [yv]. [rematado]: con la albardilla de la almena y
     * el alféizar del vano (el tramo en obra todavía no los tiene).
     */
    private fun DrawScope.sillares(l: Luces, r: Rayo, x0: Float, x1: Float, ya: Float, yv: Float, az: Azar, nieve: Boolean, rematado: Boolean) {
        val forma = Path().apply { addRect(Rect(x0, ya, x0 + W_ALMENA, BASE)); addRect(Rect(x0 + W_ALMENA, yv, x1, BASE)) }
        val piedra = l.tono(C.PIEDRA); val luz = l.tono(lerp(C.PIEDRA, Color.White, 0.5f)); val sombra = l.tono(mult(C.PIEDRA, r.color, 0.8f))
        drawPath(forma, Brush.verticalGradient(listOf(if (rematado) luz else piedra, piedra, lerp(piedra, sombra, 0.4f)), ya, BASE))
        clipPath(forma) {
            // Las hiladas, a soga y tizón: cada sillar con su tono; las juntas, hundidas.
            val alto = 6.4f; var y = BASE - alto; var fila = 0
            while (y > ya - alto) {
                var x = x0 - (if (fila % 2 == 0) 0f else 7f) - az.entre(0f, 3f)
                while (x < x1) {
                    val w = az.entre(11f, 17f)
                    val tinte = when (az.entero(5)) { 0 -> Color(0xFFCDBE9C); 1 -> Color(0xFFE8E0CC); 2 -> Color(0xFFC8C0AA); else -> C.PIEDRA }
                    drawRect(l.tono(tinte).copy(alpha = 0.55f), Offset(x + 0.4f, y + 0.4f), Size(w - 0.8f, alto - 0.8f))
                    drawLine(luz.copy(alpha = 0.6f), Offset(x + 0.5f, y + 0.5f), Offset(x + w - 0.5f, y + 0.5f), 0.4f)
                    drawLine(l.tono(C.PIEDRA_OSC).copy(alpha = 0.8f), Offset(x + w, y), Offset(x + w, y + alto), 0.5f)
                    if (az.real() > 0.75f) drawPath(poligono(Offset(x + w - 2.6f, y + alto), Offset(x + w, y + alto), Offset(x + w, y + alto - 1.8f)), sombra)      // una esquina saltada
                    x += w
                }
                drawLine(l.tono(C.PIEDRA_OSC).copy(alpha = 0.85f), Offset(x0, y + alto), Offset(x1, y + alto), 0.55f)
                y -= alto; fila++
            }
            // Las marcas del pico y la humedad que sube del suelo.
            repeat(46) { val p = Offset(az.entre(x0, x1), az.entre(ya, BASE)); drawLine(sombra.copy(alpha = 0.3f), p, p + Offset(1.6f, 1.2f), 0.35f) }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFF8A7E62)).copy(alpha = 0.3f)), BASE - 10f, BASE), Offset(x0, BASE - 10f), Size(x1 - x0, 10f))
            if (rematado) repeat(3) { val x = az.entre(x0, x1); val h = az.entre(5f, 14f); drawRect(Brush.verticalGradient(listOf(l.tono(Color(0xFF8C8478)).copy(alpha = 0.22f), Color.Transparent), ya, ya + h), Offset(x, if (x < x0 + W_ALMENA) ya else yv), Size(az.entre(0.8f, 2.2f), h)) }
        }
        // El canto del vano, que mira hacia dentro: del lado de la luz o del de la sombra.
        drawRect(cara(l, r, C.PIEDRA, 1f), Offset(x0 + W_ALMENA - 1.6f, ya), Size(1.6f, (yv - ya).coerceAtLeast(0f)))
        if (rematado) {
            // La albardilla de la almena, que vuela un poco, y el alféizar del vano.
            drawRect(luz, Offset(x0 - 0.8f, ya - 3.2f), Size(W_ALMENA + 1.6f, 3.2f)); drawRect(l.tono(mult(C.PIEDRA_OSC, r.color, 0.4f)), Offset(x0 - 0.8f, ya), Size(W_ALMENA + 1.6f, 1f))
            drawPath(poligono(Offset(x0 + W_ALMENA, yv - 1.6f), Offset(x1, yv - 1.6f), Offset(x1, yv + 1.2f), Offset(x0 + W_ALMENA, yv + 1.2f)), luz)
            drawLine(l.tono(C.PIEDRA_OSC), Offset(x0 + W_ALMENA, yv + 1.4f), Offset(x1, yv + 1.4f), 0.5f)
            if (nieve) { drawRect(l.tono(C.NIEVE), Offset(x0 - 1f, ya - 5f), Size(W_ALMENA + 2f, 2.4f)); drawRect(l.tono(C.NIEVE), Offset(x0 + W_ALMENA, yv - 3f), Size(x1 - x0 - W_ALMENA, 1.8f)) }
        }
    }

    /**
     * El tajo, encima del tramo en obra: la hilada que se está poniendo, a medio hacer y en escalera; el relleno de
     * cascote entre las dos caras; la tortada de mortero; la cuerda de nivel, tensa entre dos estacas; la paleta, el
     * cubo de la mezcla y unas cuñas.
     */
    private fun DrawScope.tajo(l: Luces, r: Rayo, x0: Float, x1: Float, ya: Float, yv: Float, az: Azar) {
        val alto = 6.4f
        val piedra = l.tono(C.PIEDRA); val luz = l.tono(lerp(C.PIEDRA, Color.White, 0.45f)); val sombra = l.tono(mult(C.PIEDRA_OSC, r.color, 0.55f * r.contraste))
        val mortero = l.tono(Color(0xFFB8B4A8)); val cascote = l.tono(Color(0xFF8E8676))
        // La cima de lo ya asentado: el relleno de cascote y cal, basto, entre las dos caras de sillar.
        for ((a, b, y) in listOf(Triple(x0, x0 + W_ALMENA, ya), Triple(x0 + W_ALMENA, x1, yv))) {
            if (y <= ALMENA + 0.5f) continue
            drawRect(cascote, Offset(a, y - 1.6f), Size(b - a, 2.2f))
            var x = a + 0.6f
            while (x < b - 1f) { val w = az.entre(1.2f, 3f); drawOval(if (az.real() > 0.5f) luz else sombra, Offset(x, y - az.entre(1.2f, 2.4f)), Size(w, az.entre(0.9f, 1.6f))); x += w + az.entre(0.2f, 1f) }
            // La tortada de mortero, que rebosa y chorrea por la cara.
            drawLine(mortero, Offset(a, y + 0.5f), Offset(b, y + 0.5f), 1.1f)
            repeat(4) { val q = az.entre(a + 1f, b - 1f); drawLine(mortero.copy(alpha = 0.85f), Offset(q, y + 0.6f), Offset(q + az.entre(-0.3f, 0.3f), y + az.entre(2f, 5f)), 0.7f, StrokeCap.Round) }
        }
        // La hilada nueva, a medio poner: unos sillares ya en su sitio, en escalera desde un extremo, y uno torcido, sin asentar.
        fun sillar(x: Float, y: Float, w: Float, giro: Float = 0f) {
            rotate(giro, Offset(x + w / 2f, y)) {
                drawRect(piedra, Offset(x, y - alto), Size(w, alto)); drawRect(luz, Offset(x, y - alto), Size(w, 1.2f))
                drawRect(sombra, Offset(if (r.lat >= 0f) x else x + w - 1.5f, y - alto + 1.2f), Size(1.5f, alto - 1.2f))
                drawLine(l.tono(C.PIEDRA_OSC).copy(alpha = 0.8f), Offset(x, y), Offset(x + w, y), 0.45f)
                repeat(3) { val p = Offset(az.entre(x + 1f, x + w - 2f), az.entre(y - alto + 1.6f, y - 1f)); drawLine(sombra.copy(alpha = 0.4f), p, p + Offset(1.4f, 1f), 0.35f) }
            }
        }
        if (ya > ALMENA + alto) { sillar(x0 + 0.4f, ya - 1.2f, 13f); sillar(x0 + 14f, ya - 1.2f, 10f); if (ya > ALMENA + alto * 2f) sillar(x0 + 2f, ya - 1.2f - alto, 11.6f) }
        if (yv > VANO + alto) sillar(x1 - 13.6f, yv - 1.2f, 12f, giro = -5f)
        // La cuerda de nivel: dos estacas clavadas en los extremos y el cordel teñido de almagre, tenso, a la altura de la hilada que toca.
        val nivel = minOf(ya, yv) - alto - 2.4f
        for (x in listOf(x0 + 1.4f, x1 - 1.6f)) drawLine(l.tono(C.MADERA_OSC), Offset(x, maxOf(ya, yv) + 1f), Offset(x, nivel - 2.4f), 1.2f, StrokeCap.Round)
        drawLine(l.tono(C.ROJO), Offset(x0 + 1.4f, nivel), Offset(x1 - 1.6f, nivel + 0.3f), 0.45f)
        for (x in listOf(x0 + 1.4f, x1 - 1.6f)) drawCircle(l.tono(C.ROJO_OSC), 0.7f, Offset(x, nivel))
        // El cubo de la mezcla y la paleta, sobre lo ya puesto; y dos cuñas de madera.
        val c = Offset(x0 + W_ALMENA + 6f, yv - 1.6f)
        val cubo = poligono(c + Offset(-3.2f, 0f), c + Offset(3.2f, 0f), c + Offset(3.8f, -5.4f), c + Offset(-3.8f, -5.4f))
        volumen(l, r, cubo, c.x - 3.8f, c.x + 3.8f, C.MADERA); drawOval(l.tono(Color(0xFFE6E2D6)), Offset(c.x - 3.6f, c.y - 6.2f), Size(7.2f, 1.7f))
        drawLine(l.tono(C.HIERRO), c + Offset(-3.6f, -3.6f), c + Offset(3.6f, -3.6f), 0.5f)
        drawArc(l.tono(C.HIERRO), 180f, 180f, false, c + Offset(-3.4f, -9.4f), Size(6.8f, 7f), style = Stroke(0.45f))
        val p = Offset(x1 - 9f, yv - 2f)
        drawPath(poligono(p, p + Offset(5f, -1f), p + Offset(1.6f, -2.6f)), l.tono(lerp(C.HIERRO, Color.White, 0.35f))); drawLine(l.tono(C.MADERA_OSC), p + Offset(0.6f, -1.4f), p + Offset(-3.4f, -3f), 1f, StrokeCap.Round)
        for (x in listOf(x0 + 26f, x0 + 28.6f)) drawPath(poligono(Offset(x, ya - 1.6f), Offset(x + 2.2f, ya - 1.6f), Offset(x, ya - 3.2f)), l.tono(C.MADERA))
    }

    /** El andamio del tramo en obra: dos pies derechos, travesaños atados, una riostra y el tablón con la artesa. */
    private fun DrawScope.andamio(l: Luces, r: Rayo, x0: Float, x1: Float, nieve: Boolean) {
        val palo = l.tono(C.MADERA); val osc = l.tono(mult(C.MADERA_OSC, r.color, 0.5f)); val luz = l.tono(lerp(C.MADERA, Color(0xFFFFF2D0), 0.4f))
        val a = x0 + 5f; val b = x1 - 6f; val cima = 178f
        drawLine(osc, Offset(a + 1.4f, 232f), Offset(b - 1.4f, 206f), 1.3f, StrokeCap.Round)                                  // la riostra
        for (x in listOf(a, b)) { drawLine(palo, Offset(x, BASE), Offset(x + 0.6f, cima), 2.4f, StrokeCap.Round); drawLine(if (r.lat >= 0f) luz else osc, Offset(x + 0.8f, BASE), Offset(x + 1.4f, cima), 0.7f) }
        for (y in listOf(232f, 206f)) {
            drawLine(palo, Offset(a - 3f, y), Offset(b + 3f, y - 0.6f), 2f, StrokeCap.Round); drawLine(luz, Offset(a - 3f, y - 0.7f), Offset(b + 3f, y - 1.3f), 0.5f)
            for (x in listOf(a, b)) for (d in listOf(-1f, 0f, 1f)) drawLine(l.tono(C.LINO), Offset(x - 1.4f + d * 0.3f, y - 1.6f + d), Offset(x + 2f, y + 1.2f + d * 0.6f), 0.5f)
        }
        // El tablón de arriba, con la artesa de la mezcla y la paleta.
        drawRect(Brush.verticalGradient(listOf(luz, palo), 201f, 204.4f), Offset(a - 5f, 201.4f), Size(b - a + 10f, 3f))
        drawPath(poligono(Offset(a + 8f, 201.4f), Offset(a + 22f, 201.4f), Offset(a + 20f, 196.6f), Offset(a + 10f, 196.6f)), l.tono(C.MADERA_OSC)); drawLine(l.tono(Color(0xFFEDE8DC)), Offset(a + 10.6f, 197f), Offset(a + 19.4f, 197f), 1f)
        // La plomada, que cuelga del travesaño.
        drawLine(l.tono(C.LINO).copy(alpha = 0.8f), Offset(b - 9f, 206f), Offset(b - 9f, 226f), 0.3f); drawPath(poligono(Offset(b - 10.2f, 226f), Offset(b - 7.8f, 226f), Offset(b - 9f, 229.6f)), l.tono(C.BRONCE))
        if (nieve) drawLine(l.tono(C.NIEVE), Offset(a - 5f, 201f), Offset(b + 5f, 201f), 1.2f)
    }

    /** El acopio al pie del tramo en obra: sillares por subir, uno a medio labrar con sus lascas, y la artesa de la cal. */
    private fun DrawScope.acopio(l: Luces, r: Rayo, x0: Float) {
        val luz = l.tono(lerp(C.PIEDRA, Color.White, 0.5f)); val piedra = l.tono(C.PIEDRA)
        for ((i, p) in listOf(Offset(x0 + 33f, BASE + 4.4f), Offset(x0 + 45f, BASE + 5f), Offset(x0 + 38.4f, BASE - 1.6f)).withIndex()) {
            drawOval(Color.Black.copy(alpha = 0.2f), Offset(p.x - 1f, p.y - 1.2f), Size(13f, 2.4f))
            drawRect(piedra, Offset(p.x, p.y - 6f), Size(11f, 6f)); drawRect(luz, Offset(p.x, p.y - 6f), Size(11f, 1.3f)); drawRect(cara(l, r, C.PIEDRA_OSC, if (i == 1) 1f else -1f), Offset(if (r.lat >= 0f) p.x else p.x + 9.4f, p.y - 4.7f), Size(1.6f, 4.7f))
            drawLine(l.tono(C.PIEDRA_OSC).copy(alpha = 0.6f), Offset(p.x + 2f, p.y - 3f), Offset(p.x + 8f, p.y - 2.4f), 0.35f)
        }
        // Las lascas del labrado y el polvo de piedra.
        val az = Azar(Semilla.de("romano", "lascas", x0.toInt()))
        mancha(Offset(x0 + 42f, BASE + 5.6f), 13f, 2f, l.tono(Color(0xFFEDE8DC)), 0.5f)
        repeat(12) { val q = Offset(x0 + az.entre(28f, 54f), BASE + az.entre(3.6f, 7.4f)); drawPath(poligono(q, q + Offset(az.entre(0.8f, 1.8f), -0.4f), q + Offset(0.5f, -az.entre(0.6f, 1.2f))), if (it % 2 == 0) luz else piedra) }
    }

    /** El suelo del camino de ronda: tablas tendidas a lo largo, en cinco hiladas que se agrandan hacia quien mira. */
    private fun DrawScope.suelo(m: Momento, l: Luces, r: Rayo, az: Azar, nieve: Boolean) {
        val tabla = Color(0xFFC6BAA0)
        drawRect(l.tono(Color(0xFF5A4A3A)), Offset(0f, BASE), Size(ANCHO, ALTO - BASE))
        val filas = listOf(BASE to 253.4f, 253.9f to 260f, 260.6f to 268f, 268.7f to 277.4f, 278.2f to 288f)
        filas.forEachIndexed { f, (y0, y1) ->
            var x = -az.entre(10f, 50f)
            while (x < ANCHO + 10f) {
                val w = az.entre(60f, 120f)
                val propio = lerp(lerp(tabla, C.MADERA, az.entre(0f, 0.4f)), C.MADERA_OSC, az.entre(0f, 0.1f))
                drawRect(Brush.verticalGradient(listOf(l.tono(lerp(propio, Color(0xFF6A5A48), 0.18f)), l.tono(propio)), y0, y1), Offset(x + 0.4f, y0), Size(w - 0.8f, y1 - y0))
                drawLine(l.tono(Color.White).copy(alpha = 0.22f), Offset(x + 0.4f, y0 + 0.3f), Offset(x + w - 0.4f, y0 + 0.3f), 0.45f)
                // Las vetas, y los clavos de cada cabeza.
                clipRect(x + 0.4f, y0, x + w - 0.4f, y1) {
                    repeat(3 + f) { val y = az.entre(y0 + 0.8f, y1 - 0.8f); val a = az.entre(x, x + w * 0.6f); drawPath(Path().apply { moveTo(a, y); cubicTo(a + w * 0.15f, y - 0.6f, a + w * 0.3f, y + 0.6f, a + w * az.entre(0.3f, 0.6f), y) }, l.tono(C.MADERA_OSC).copy(alpha = az.entre(0.18f, 0.4f)), style = Stroke(0.4f)) }
                    if (az.real() > 0.5f) { val k = Offset(az.entre(x + 6f, x + w - 6f), (y0 + y1) / 2f); drawOval(l.tono(C.MADERA_OSC).copy(alpha = 0.55f), Offset(k.x - 1.6f, k.y - 0.9f), Size(3.2f, 1.8f)) }
                }
                for (dx in listOf(2.2f, w - 2.2f)) drawCircle(l.tono(Color(0xFF3A342E)).copy(alpha = 0.8f), 0.45f + 0.08f * f, Offset(x + dx, (y0 + y1) / 2f))
                x += w
            }
        }
        // Por donde se pisa, la tabla está más clara; al pie del parapeto, más oscura y húmeda.
        mancha(Offset(170f, 271f), 190f, 7f, l.tono(Color(0xFFE6DCC4)), 0.3f)
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.28f), Color.Transparent), BASE, BASE + 5f), Offset(0f, BASE), Size(TORRE, 5f))
        // Paja, astillas y barro de las botas.
        repeat(34) { val p = Offset(az.entre(0f, TORRE), az.entre(BASE + 3f, ALTO - 1f)); val a = az.entre(-0.5f, 0.5f); drawLine(l.tono(if (it % 3 == 0) Color(0xFF7A6248) else C.PAJA).copy(alpha = 0.8f), p, p + Offset(cos(a) * az.entre(2f, 5f), sin(a) * 2f), 0.5f, StrokeCap.Round) }
        repeat(8) { mancha(Offset(az.entre(20f, 300f), az.entre(262f, 284f)), az.entre(4f, 10f), az.entre(1f, 2.2f), l.tono(Color(0xFF6A5640)), 0.35f) }
        if (nieve) {
            // Nieve pisada: queda en los bordes y en montones contra el parapeto; por el medio asoma la tabla.
            drawRect(Brush.verticalGradient(listOf(l.tono(C.NIEVE).copy(alpha = 0.95f), l.tono(C.NIEVE).copy(alpha = 0.6f)), BASE, BASE + 9f), Offset(0f, BASE - 1f), Size(TORRE, 9f))
            repeat(16) { mancha(Offset(az.entre(0f, ANCHO), az.entre(BASE + 2f, ALTO)), az.entre(14f, 40f), az.entre(2f, 5f), l.tono(C.NIEVE), 0.7f) }
            repeat(9) { val p = Offset(60f + it * 26f + az.entre(-4f, 4f), 272f + (it % 2) * 5f); drawOval(l.tono(mult(Color(0xFFB8C2D0), r.color, 0.4f)).copy(alpha = 0.7f), Offset(p.x, p.y), Size(5f, 2.2f)) }     // huellas
        }
    }

    // ---------------------------------------------------------------- La torre

    /** La torre de la puerta: de tablas hasta que la muralla se termina, y de piedra después. Arriba, la galería y el alero. */
    private fun DrawScope.torre(m: Momento, l: Luces, r: Rayo, nieve: Boolean) {
        val az = Azar(Semilla.de("romano", "torre"))
        val dePiedra = EscenaRomano.torreDePiedra(m)
        val propio = if (dePiedra) C.PIEDRA else lerp(C.MADERA, C.MADERA_VIEJA, 0.45f)
        // El costado, que mira hacia el parapeto, y la cara de delante.
        val costado = poligono(Offset(TORRE, 44f), Offset(FRENTE, 46f), Offset(FRENTE, PIE_TORRE), Offset(TORRE, BASE))
        drawPath(costado, cara(l, r, propio, -1f))
        val frente = Rect(FRENTE, 46f, ANCHO + 2f, PIE_TORRE)
        drawRect(Brush.verticalGradient(listOf(l.tono(lerp(propio, Color(0xFF4A3E34), 0.25f)), l.tono(propio), l.tono(lerp(propio, Color(0xFFFFF2D0), 0.12f))), 50f, PIE_TORRE), frente.topLeft, frente.size)
        if (dePiedra) {
            clipRect(TORRE, 109f, ANCHO, PIE_TORRE) {
                var y = PIE_TORRE - 7f; var fila = 0
                while (y > 100f) {
                    var x = FRENTE - (if (fila % 2 == 0) 0f else 8f)
                    while (x < ANCHO) {
                        val w = az.entre(13f, 19f)
                        drawRect(l.tono(when (az.entero(4)) { 0 -> Color(0xFFCDBE9C); 1 -> Color(0xFFE8E0CC); else -> C.PIEDRA }).copy(alpha = 0.5f), Offset(x + 0.4f, y + 0.4f), Size(w - 0.8f, 6.2f))
                        drawLine(l.tono(C.PIEDRA_OSC).copy(alpha = 0.8f), Offset(x + w, y), Offset(x + w, y + 7f), 0.5f)
                        x += w
                    }
                    drawLine(l.tono(C.PIEDRA_OSC).copy(alpha = 0.85f), Offset(FRENTE, y + 7f), Offset(ANCHO, y + 7f), 0.55f)
                    // En el costado, las mismas hiladas, en fuga.
                    drawLine(l.tono(C.PIEDRA_OSC).copy(alpha = 0.6f), Offset(TORRE, y + 7f - 5f * ((y - 46f) / (PIE_TORRE - 46f))), Offset(FRENTE, y + 7f), 0.45f)
                    y -= 7f; fila++
                }
                // Las esquinas, de sillares más grandes.
                var q = PIE_TORRE - 7f; var par = 0
                while (q > 104f) { drawRect(l.tono(lerp(C.PIEDRA, Color.White, 0.35f)).copy(alpha = 0.7f), Offset(FRENTE, q + 0.4f), Size(if (par % 2 == 0) 8f else 5f, 6.2f)); q -= 7f; par++ }
            }
        } else {
            clipRect(TORRE, 109f, ANCHO, PIE_TORRE) {
                // Tablas tendidas, cada una con su tono, sus vetas y sus clavos.
                var y = PIE_TORRE - 8f
                while (y > 100f) {
                    val t = lerp(lerp(C.MADERA, C.MADERA_VIEJA, az.entre(0.2f, 0.8f)), C.MADERA_OSC, az.entre(0f, 0.2f))
                    drawRect(l.tono(t).copy(alpha = 0.75f), Offset(FRENTE, y + 0.5f), Size(ANCHO - FRENTE, 7.2f))
                    drawLine(l.tono(Color(0xFF3A2C20)).copy(alpha = 0.7f), Offset(FRENTE, y + 8f), Offset(ANCHO, y + 8f), 0.6f)
                    drawLine(l.tono(Color.White).copy(alpha = 0.18f), Offset(FRENTE, y + 0.9f), Offset(ANCHO, y + 0.9f), 0.4f)
                    repeat(2) { val x = az.entre(FRENTE, ANCHO - 12f); val yy = y + az.entre(2f, 6f); drawLine(l.tono(C.MADERA_OSC).copy(alpha = 0.35f), Offset(x, yy), Offset(x + az.entre(8f, 20f), yy + az.entre(-0.6f, 0.6f)), 0.4f) }
                    drawLine(l.tono(Color(0xFF3A2C20)).copy(alpha = 0.55f), Offset(TORRE, y + 8f - 5f * ((y - 46f) / (PIE_TORRE - 46f))), Offset(FRENTE, y + 8f), 0.5f)
                    for (x in listOf(FRENTE + 9f, ANCHO - 3f)) drawCircle(l.tono(Color(0xFF3A342E)), 0.5f, Offset(x, y + 4f))
                    y -= 8f
                }
            }
            // El pie derecho de la esquina.
            drawRect(Brush.horizontalGradient(listOf(cara(l, r, C.MADERA_OSC, -1f), l.tono(C.MADERA), l.tono(C.MADERA_OSC)), FRENTE - 1f, FRENTE + 6f), Offset(FRENTE - 1f, 109f), Size(7f, PIE_TORRE - 109f))
        }
        // La humedad del pie y el escalón del umbral.
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFF5E5240)).copy(alpha = 0.4f)), PIE_TORRE - 12f, PIE_TORRE), Offset(FRENTE, PIE_TORRE - 12f), Size(ANCHO - FRENTE, 12f))
        // La puerta: el hueco oscuro, el dintel y la hoja de tablas, entornada hacia dentro.
        val p0 = FRENTE + 13f; val p1 = FRENTE + 39f; val dintel = 203f
        drawRect(l.tono(Color(0xFF241C16)), Offset(p0, dintel), Size(p1 - p0, PIE_TORRE - dintel))
        val hoja = poligono(Offset(p1 - 9f, dintel + 2f), Offset(p1, dintel), Offset(p1, PIE_TORRE), Offset(p1 - 9f, PIE_TORRE - 2f))
        drawPath(hoja, l.tono(lerp(C.MADERA, C.MADERA_OSC, 0.45f)))
        clipPath(hoja) { for (i in 1..2) drawLine(l.tono(Color(0xFF2E241C)), Offset(p1 - 9f + i * 3f, dintel), Offset(p1 - 9f + i * 3f, PIE_TORRE), 0.4f); drawLine(l.tono(C.HIERRO), Offset(p1 - 9f, dintel + 12f), Offset(p1, dintel + 10.6f), 1f); drawLine(l.tono(C.HIERRO), Offset(p1 - 9f, PIE_TORRE - 12f), Offset(p1, PIE_TORRE - 10.6f), 1f) }
        if (dePiedra) { drawRect(l.tono(lerp(C.PIEDRA, Color.White, 0.4f)), Offset(p0 - 4f, dintel - 6f), Size(p1 - p0 + 8f, 6f)); drawLine(l.tono(C.PIEDRA_OSC), Offset(p0 - 4f, dintel), Offset(p1 + 4f, dintel), 0.6f) }
        else { drawRect(l.tono(C.MADERA_OSC), Offset(p0 - 4f, dintel - 5f), Size(p1 - p0 + 8f, 5f)); drawRect(l.tono(lerp(C.MADERA, Color(0xFFFFF2D0), 0.3f)), Offset(p0 - 4f, dintel - 5f), Size(p1 - p0 + 8f, 1.2f)); for (x in listOf(p0 - 3f, p1)) drawRect(l.tono(C.MADERA_OSC), Offset(x, dintel), Size(3f, PIE_TORRE - dintel)) }
        drawPath(poligono(Offset(p0 - 5f, PIE_TORRE), Offset(p1 + 5f, PIE_TORRE), Offset(p1 + 8f, PIE_TORRE + 3.6f), Offset(p0 - 8f, PIE_TORRE + 3.6f)), l.tono(if (dePiedra) lerp(C.PIEDRA, Color.White, 0.3f) else lerp(C.MADERA, Color(0xFFFFF2D0), 0.25f)))
        drawRect(l.tono(mult(if (dePiedra) C.PIEDRA_OSC else C.MADERA_OSC, r.color, 0.5f)), Offset(p0 - 8f, PIE_TORRE + 3.6f), Size(p1 - p0 + 16f, 1.8f))
        // El farol de la puerta, de asta y bronce, colgado de su escarpia (la llama va en la parte viva).
        drawLine(l.tono(C.HIERRO), Offset(FAROL.x - 5f, FAROL.y - 12f), Offset(FAROL.x, FAROL.y - 12f), 1f); drawLine(l.tono(C.HIERRO), Offset(FAROL.x, FAROL.y - 12f), Offset(FAROL.x, FAROL.y - 7f), 0.6f)
        val encendido = l.noche > 0.25f
        drawRoundRect(if (encendido) lerp(l.tono(Color(0xFFE8D8A8)), Color(0xFFFFD890), 0.75f) else l.tono(Color(0xFFD8CCA8)), Offset(FAROL.x - 3f, FAROL.y - 6f), Size(6f, 8f), CornerRadius(1.2f))
        drawRect(l.tono(C.BRONCE), Offset(FAROL.x - 3.6f, FAROL.y - 7f), Size(7.2f, 1.6f)); drawRect(l.tono(C.BRONCE), Offset(FAROL.x - 3.6f, FAROL.y + 1.6f), Size(7.2f, 1.4f)); drawLine(l.tono(C.BRONCE), Offset(FAROL.x, FAROL.y - 6f), Offset(FAROL.x, FAROL.y + 2f), 0.5f)

        vexilo(m, l, r)
        galeria(m, l, r, az, nieve)
    }

    /** El vexilo de la cohorte, colgado en la cara de la torre: paño rojo con orla y corona de oro, y sus flecos. */
    private fun DrawScope.vexilo(m: Momento, l: Luces, r: Rayo) {
        val x0 = FRENTE + 15f; val x1 = FRENTE + 39f; val y0 = 122f; val y1 = 154f
        // La sombra que da sobre la torre.
        if (r.fuerza > 0.05f) drawRect(r.color, Offset(x0 - r.lat * 2.4f, y0 + 1.6f), Size(x1 - x0, y1 - y0 + 3f), alpha = r.fuerza * 0.7f, blendMode = BlendMode.Multiply)
        val cx = (x0 + x1) / 2f
        drawLine(l.tono(C.LINO), Offset(cx, 113f), Offset(x0 + 2f, y0 - 1f), 0.4f); drawLine(l.tono(C.LINO), Offset(cx, 113f), Offset(x1 - 2f, y0 - 1f), 0.4f)
        drawCircle(l.tono(C.HIERRO), 1f, Offset(cx, 113f))
        val pano = Path().apply { moveTo(x0, y0); lineTo(x1, y0); lineTo(x1 + 0.6f, y1); cubicTo(cx + 5f, y1 + 1.6f, cx - 5f, y1 - 1.2f, x0 - 0.4f, y1 + 0.6f); close() }
        val rojo = l.tono(C.ROJO); val rojoLuz = l.tono(lerp(C.ROJO, Color(0xFFFFC8A0), 0.32f)); val rojoOsc = l.tono(mult(C.ROJO_OSC, r.color, 0.5f))
        drawPath(pano, Brush.horizontalGradient(if (r.lat >= 0f) listOf(rojoOsc, rojo, rojoLuz, rojo) else listOf(rojo, rojoLuz, rojo, rojoOsc), x0, x1))
        clipPath(pano) {
            // Los pliegues, la orla y la corona.
            for (x in listOf(x0 + 6f, x0 + 15f)) drawLine(rojoOsc.copy(alpha = 0.6f), Offset(x, y0), Offset(x + 1f, y1 + 2f), 1f)
            val oro = l.tono(C.ORO)
            drawPath(pano, oro, style = Stroke(2.2f))
            drawCircle(oro, 6f, Offset(cx, 136f), style = Stroke(1.5f))
            for (i in 0 until 10) { val a = i / 10f * 6.283f; drawLine(l.tono(lerp(C.ORO, Color.White, 0.4f)), Offset(cx + cos(a) * 5f, 136f + sin(a) * 5f), Offset(cx + cos(a + 0.5f) * 7.4f, 136f + sin(a + 0.5f) * 7.4f), 0.6f, StrokeCap.Round) }
            drawCircle(oro, 1.4f, Offset(cx, 136f))
            if (m.con("luto")) drawLine(l.tono(Color(0xFF2A2630)), Offset(x0 - 1f, y0 + 4f), Offset(x1 + 1f, y1 - 6f), 4.4f)                    // el crespón
        }
        for (i in 0..7) { val x = x0 + 1.4f + i * (x1 - x0 - 2.8f) / 7f; drawLine(l.tono(C.ORO), Offset(x, y1 + 0.6f), Offset(x + 0.3f, y1 + 4.4f), 0.6f, StrokeCap.Round) }
        // El travesaño, con sus dos pomos de bronce.
        drawRect(Brush.verticalGradient(listOf(l.tono(lerp(C.MADERA, Color(0xFFFFF2D0), 0.4f)), l.tono(C.MADERA_OSC)), y0 - 2.4f, y0 + 0.6f), Offset(x0 - 2.6f, y0 - 2.4f), Size(x1 - x0 + 5.2f, 3f))
        drawCircle(l.tono(C.BRONCE), 1.7f, Offset(x0 - 2.6f, y0 - 0.9f)); drawCircle(l.tono(C.BRONCE), 1.7f, Offset(x1 + 2.6f, y0 - 0.9f))
        if (m.con("rosas")) guirnalda(l, Offset(x0 - 2f, y0 - 1f), Offset(x1 + 2f, y0 - 1f), 7f, m.azar("rosas", 1), flores = listOf(Color(0xFFD8506A), Color(0xFFF2B8C0), C.ROJO), grueso = 2.4f)
    }

    /** Lo alto de la torre: la galería volada con su barandilla de zarzo, el cuerno de los toques y el alero de tablillas. */
    private fun DrawScope.galeria(m: Momento, l: Luces, r: Rayo, az: Azar, nieve: Boolean) {
        val x0 = TORRE - 9f                                                     // hasta dónde vuela la galería
        val madera = l.tono(C.MADERA); val osc = l.tono(C.MADERA_OSC); val luz = l.tono(lerp(C.MADERA, Color(0xFFFFF2D0), 0.4f))
        // La galería está abierta por los cuatro lados: bajo el alero, en sombra, se ven los pares y la viga; más abajo,
        // el cielo del otro lado entre los pies derechos del fondo; y abajo, el reverso de la barandilla de enfrente, con luz.
        val cieloDeFondo = lerp(l.medio, l.alto, 0.45f)
        drawRect(Brush.verticalGradient(listOf(lerp(cieloDeFondo, l.alto, 0.5f), cieloDeFondo), 60f, 82f), Offset(FRENTE, 50f), Size(ANCHO - FRENTE, 38f))
        drawPath(poligono(Offset(TORRE, 48f), Offset(FRENTE, 50f), Offset(FRENTE, 105f), Offset(TORRE, 103f)), lerp(cieloDeFondo, l.tono(Color(0xFF4A3C30)), 0.35f))
        // El reverso de la barandilla del fondo: zarzo claro, con su pasamanos.
        drawRect(l.tono(lerp(C.PAJA, Color(0xFFFFF2D0), 0.25f)), Offset(FRENTE, 77f), Size(ANCHO - FRENTE, 11f))
        for (i in 0..6) drawLine(l.tono(C.MADERA_OSC).copy(alpha = 0.5f), Offset(FRENTE + 3f + i * 7f, 77f), Offset(FRENTE + 3f + i * 7f, 88f), 0.5f)
        for (y in listOf(79.6f, 82.4f, 85.2f)) drawLine(l.tono(C.MADERA).copy(alpha = 0.6f), Offset(FRENTE, y), Offset(ANCHO, y), 0.5f)
        drawRect(l.tono(lerp(C.MADERA, Color(0xFFFFF2D0), 0.45f)), Offset(FRENTE, 75.4f), Size(ANCHO - FRENTE, 1.8f))
        // Los pies derechos del fondo, a contraluz.
        for (x in listOf(FRENTE + 10f, FRENTE + 34f)) drawRect(l.tono(Color(0xFF4A3A2C)), Offset(x, 50f), Size(2.4f, 26f))
        // Bajo el alero: la sombra, la viga de atado y los pares, que bajan en abanico hacia el alero.
        drawRect(Brush.verticalGradient(listOf(l.tono(Color(0xFF2E241C)), l.tono(Color(0xFF2E241C)).copy(alpha = 0.85f), Color.Transparent), 49f, 68f), Offset(FRENTE, 49f), Size(ANCHO - FRENTE, 19f))
        for (i in 0..5) drawLine(l.tono(Color(0xFF7A6248)), Offset(FRENTE + 4f + i * 9f, 50f), Offset(FRENTE + 12f + i * 9.6f, 61f), 1.1f)
        drawRect(Brush.verticalGradient(listOf(l.tono(C.MADERA), l.tono(C.MADERA_OSC)), 60f, 63f), Offset(FRENTE, 60f), Size(ANCHO - FRENTE, 3f))
        // Lo que hay dentro: el farol de la guardia colgado de la viga, la trompeta recta de los toques, una lanza y el banco.
        val fx = FRENTE + 24f
        drawLine(l.tono(C.HIERRO), Offset(fx, 63f), Offset(fx, 66.4f), 0.5f)
        drawRoundRect(if (l.noche > 0.25f) lerp(l.tono(Color(0xFFE8D8A8)), Color(0xFFFFD890), 0.8f) else l.tono(Color(0xFFD8CCA8)), Offset(fx - 2f, 66.4f), Size(4f, 5.4f), CornerRadius(0.8f))
        drawRect(l.tono(C.BRONCE), Offset(fx - 2.4f, 65.8f), Size(4.8f, 1.1f)); drawRect(l.tono(C.BRONCE), Offset(fx - 2.4f, 71.4f), Size(4.8f, 1f))
        if (l.noche > 0.25f) drawCircle(Brush.radialGradient(listOf(Color(0xFFFFD890).copy(alpha = 0.5f * l.noche), Color.Transparent), Offset(fx, 69f), 16f), 16f, Offset(fx, 69f))
        // La trompeta: un tubo largo de bronce con su pabellón, apoyada en diagonal contra un pie derecho.
        val t0 = Offset(FRENTE + 37f, 87f); val t1 = Offset(FRENTE + 44.6f, 56f)
        drawLine(l.tono(C.BRONCE), t0, t1, 1.1f, StrokeCap.Round); drawLine(l.tono(lerp(C.BRONCE, Color(0xFFFFE9B0), 0.5f)), t0 + Offset(-0.35f, 0f), t1 + Offset(-0.35f, 0f), 0.35f)
        drawPath(poligono(t1 + Offset(-0.6f, 0.6f), t1 + Offset(0.6f, -0.2f), t1 + Offset(2.6f, -4.6f), t1 + Offset(-1.6f, -4f)), l.tono(lerp(C.BRONCE, Color(0xFFFFE9B0), 0.25f)))
        drawCircle(l.tono(C.BRONCE), 0.9f, t0)
        // La lanza de la guardia, arrimada a la barandilla: se recorta contra el cielo.
        val l0 = Offset(FRENTE + 14f, 88f); val l1 = Offset(FRENTE + 17.6f, 53f)
        drawLine(l.tono(C.MADERA_OSC), l0, l1 + Offset(-0.5f, 5f), 1f, StrokeCap.Round)
        drawPath(poligono(l1 + Offset(-1.6f, 5.4f), l1 + Offset(0.7f, 5.2f), l1), l.tono(lerp(C.HIERRO, Color.White, 0.3f)))
        // El banco de la guardia, con un sago doblado encima.
        drawRect(l.tono(C.MADERA), Offset(FRENTE + 3f, 72.6f), Size(9f, 1.6f)); drawRect(l.tono(C.MADERA_OSC), Offset(FRENTE + 4f, 74.2f), Size(1.2f, 3f)); drawRect(l.tono(C.MADERA_OSC), Offset(FRENTE + 9.6f, 74.2f), Size(1.2f, 3f))
        drawRoundRect(l.tono(C.ROJO_OSC), Offset(FRENTE + 4f, 70.6f), Size(6.4f, 2.2f), CornerRadius(0.9f)); drawLine(l.tono(C.ROJO), Offset(FRENTE + 4.4f, 71f), Offset(FRENTE + 10f, 71f), 0.5f)
        // Los pies derechos y las jabalcones que sostienen el vuelo.
        for (x in listOf(x0 + 1.5f, FRENTE + 1f, ANCHO - 2f)) { drawRect(madera, Offset(x - 1.6f, 46f), Size(3.2f, 62f)); drawRect(if (r.lat >= 0f) luz else osc, Offset(x + 0.6f, 46f), Size(1f, 62f)) }
        drawLine(osc, Offset(x0 + 2f, 110f), Offset(TORRE + 1f, 130f), 2.2f, StrokeCap.Round); drawLine(osc, Offset(FRENTE + 6f, 126f), Offset(FRENTE + 0.6f, 110f), 2f, StrokeCap.Round)
        // El piso: la viga del frente, las cabezas de las viguetas y su canto en sombra.
        drawPath(poligono(Offset(x0, 103f), Offset(FRENTE - 2f, 105f), Offset(FRENTE - 2f, 110.4f), Offset(x0, 108f)), cara(l, r, C.MADERA, -1f))
        drawRect(Brush.verticalGradient(listOf(luz, madera, osc), 105f, 110.6f), Offset(FRENTE - 2f, 105f), Size(ANCHO - FRENTE + 4f, 5.6f))
        var x = FRENTE + 3f
        while (x < ANCHO) { drawRect(l.tono(Color(0xFF3A2C20)), Offset(x, 110.6f), Size(2.6f, 2.6f)); x += 9f }
        // La barandilla: el pasamanos y, debajo, el zarzo de varas trenzadas.
        val zarzo = Path().apply { addRect(Rect(FRENTE - 2f, 88f, ANCHO + 2f, 105f)); addPath(poligono(Offset(x0, 86f), Offset(FRENTE - 2f, 88f), Offset(FRENTE - 2f, 105f), Offset(x0, 103f))) }
        drawPath(zarzo, l.tono(lerp(C.PAJA, C.MADERA, 0.5f)))
        clipPath(zarzo) {
            for (i in 0..14) { val xx = x0 + i * 5f; drawLine(l.tono(C.MADERA_OSC).copy(alpha = 0.7f), Offset(xx, 86f), Offset(xx, 106f), 0.7f) }
            for (i in 0..7) { val y = 88.6f + i * 2.2f; drawPath(Path().apply { moveTo(x0, y); var xx = x0; var s = if (i % 2 == 0) 1f else -1f; while (xx < ANCHO) { cubicTo(xx + 1.6f, y + s * 0.9f, xx + 3.4f, y + s * 0.9f, xx + 5f, y); xx += 5f; s = -s } }, l.tono(if (i % 2 == 0) lerp(C.PAJA, Color.White, 0.2f) else C.MADERA).copy(alpha = 0.85f), style = Stroke(0.8f)) }
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.16f), Color.Transparent), 88f, 94f), Offset(x0, 86f), Size(ANCHO - x0, 8f))
        }
        drawPath(poligono(Offset(x0 - 1f, 84f), Offset(FRENTE - 2f, 86f), Offset(FRENTE - 2f, 89f), Offset(x0 - 1f, 87f)), cara(l, r, C.MADERA, -1f))
        drawRect(Brush.verticalGradient(listOf(luz, madera), 86f, 89f), Offset(FRENTE - 2f, 86f), Size(ANCHO - FRENTE + 4f, 3f))
        // Un escudo ovalado de los auxiliares, colgado de la barandilla.
        val e = Offset(FRENTE + 27f, 96f)
        if (r.fuerza > 0.05f) drawOval(r.color, Offset(e.x - 7.4f - r.lat * 2f, e.y - 8.6f), Size(15f, 20f), alpha = r.fuerza * 0.6f, blendMode = BlendMode.Multiply)
        val ovalo = Path().apply { addOval(Rect(e.x - 7f, e.y - 10f, e.x + 7f, e.y + 10f)) }
        volumen(l, r, ovalo, e.x - 7f, e.x + 7f, C.ORO)
        drawPath(ovalo, l.tono(C.ROJO_OSC), style = Stroke(1.2f)); drawLine(l.tono(C.ROJO), Offset(e.x, e.y - 9f), Offset(e.x, e.y + 9f), 1.2f); drawCircle(l.tono(C.HIERRO), 2.6f, e); drawCircle(l.tono(lerp(C.HIERRO, Color.White, 0.5f)), 0.9f, e + Offset(-0.7f, -0.8f))
        if (EscenaRomano.fiesta(m)) guirnalda(l, Offset(FRENTE, 90f), Offset(ANCHO + 4f, 90f), 9f, m.azar("gt", 1), flores = if (m.con("rosas")) listOf(Color(0xFFD8506A), Color(0xFFF2B8C0)) else listOf(C.ROJO, C.ORO), grueso = 3f)

        // El alero: el faldón del frente y el del costado, de tablillas; debajo, su canto y las cabezas de los pares.
        val tablilla = if (nieve) lerp(C.MADERA_VIEJA, C.NIEVE, 0.85f) else C.MADERA_VIEJA
        val lado = poligono(Offset(TORRE - 13f, 44.4f), Offset(TORRE - 9f, 46f), Offset(TORRE + 31f, -6f), Offset(TORRE + 24f, -6f))
        drawPath(lado, cara(l, r, lerp(tablilla, C.MADERA_OSC, 0.25f), -1f))
        val faldon = poligono(Offset(TORRE - 9f, 46f), Offset(ANCHO + 6f, 46f), Offset(ANCHO + 6f, -6f), Offset(TORRE + 31f, -6f))
        drawPath(faldon, Brush.verticalGradient(listOf(l.tono(lerp(tablilla, Color(0xFF4A4038), 0.3f)), l.tono(tablilla), l.tono(lerp(tablilla, Color(0xFFFFF2D0), 0.2f))), -6f, 46f))
        clipPath(faldon) {
            var y = 40f; var fila = 0
            while (y > -8f) {
                drawLine(l.tono(Color(0xFF3A3028)).copy(alpha = 0.6f), Offset(TORRE - 12f, y + 6f), Offset(ANCHO + 6f, y + 6f), 0.7f)
                var xx = TORRE - 12f + (fila % 2) * 3.4f
                while (xx < ANCHO + 6f) {
                    val w = az.entre(5f, 8.4f)
                    if (!nieve) drawRect(l.tono(when (az.entero(4)) { 0 -> C.MADERA; 1 -> Color(0xFF7A7264); else -> tablilla }).copy(alpha = 0.5f), Offset(xx + 0.3f, y), Size(w - 0.6f, 6f))
                    drawLine(l.tono(Color(0xFF3A3028)).copy(alpha = 0.5f), Offset(xx + w, y), Offset(xx + w, y + 6f), 0.4f)
                    xx += w
                }
                y -= 6f; fila++
            }
            if (!nieve) repeat(7) { mancha(Offset(az.entre(TORRE, ANCHO), az.entre(0f, 40f)), az.entre(4f, 9f), az.entre(2f, 4f), l.tono(Color(0xFF7A8452)), 0.3f) }     // musgo
        }
        drawRect(l.tono(mult(C.MADERA_OSC, r.color, 0.5f)), Offset(TORRE - 9f, 46f), Size(ANCHO - TORRE + 12f, 2.4f))
        drawPath(poligono(Offset(TORRE - 13f, 44.4f), Offset(TORRE - 9f, 46f), Offset(TORRE - 9f, 48.4f), Offset(TORRE - 13f, 46.6f)), l.tono(C.MADERA_OSC))
        var px = FRENTE + 5f
        while (px < ANCHO) { drawRect(l.tono(Color(0xFF2E241C)), Offset(px, 48.4f), Size(2.4f, 2.4f)); px += 8f }
        if (nieve) { drawLine(l.tono(C.NIEVE), Offset(x0 - 1f, 84f), Offset(ANCHO, 85.8f), 1.6f); drawLine(l.tono(C.NIEVE), Offset(TORRE - 12f, 45.2f), Offset(ANCHO, 45.6f), 1.8f) }
    }

    // ---------------------------------------------------------------- Adornos y nieve

    /** Lo que cuelga del parapeto: las guirnaldas de las fiestas y el paño del luto. */
    private fun DrawScope.adornos(m: Momento, l: Luces) {
        if (EscenaRomano.fiesta(m)) {
            val flores = if (m.con("rosas")) listOf(Color(0xFFD8506A), Color(0xFFF2B8C0), C.ROJO) else listOf(C.ROJO, C.ORO, C.LINO)
            guirnalda(l, Offset(3f * W + 2f, 222f), Offset(4f * W + W_ALMENA - 2f, 222f), 11f, m.azar("gp", 1), flores = flores, grueso = 3.2f)
            guirnalda(l, Offset(2f, 222f), Offset(W + W_ALMENA - 4f, 222f), 11f, m.azar("gp", 2), flores = flores, grueso = 3.2f)
        }
        if (m.con("luto")) pano(l, Offset(4f * W + 2f, ALMENA + 6f), Offset(4f * W + W_ALMENA - 2f, ALMENA + 6f), 26f, Color(0xFF2C2630))
    }

    /** La nieve posada en lo que mira hacia arriba: la cuba, el banco, el molino y los pétalos de escarcha del suelo. */
    private fun DrawScope.nieveEncima(l: Luces, r: Rayo, m: Momento) {
        val n = l.tono(C.NIEVE)
        drawOval(n, Offset(13.5f, 226.4f), Size(27f, 5f))
        drawRoundRect(n, Offset(223f, 248.8f), Size(30f, 3f), CornerRadius(1.5f)); drawRoundRect(n, Offset(276f, 249.2f), Size(22f, 2.6f), CornerRadius(1.3f))
        drawOval(n, Offset(66f, 267.6f), Size(18f, 3f))
    }

    // ---------------------------------------------------------------- Las cosas del puesto

    private fun cosasDelPuesto(m: Momento, l: Luces, r: Rayo, nieve: Boolean): List<Cosa> = buildList {
        val deGuardia = EscenaRomano.puesto(m) == Puesto.DE_GUARDIA
        add(grande(cubaCosa(l, r), 1.2f))
        add(anforaCosa(l, r, Offset(62f, 275f), 52f, -0.2f))
        add(grande(sacoCosa(l, r), 1.12f))
        add(molinoCosa(l, r))
        if (deGuardia) add(pabellonCosa(m, l, r))
        // De guardia, el escudo va desnudo; con lluvia o nieve lleva su funda, salvo el día que es el objeto del cuento.
        if (deGuardia) add(grande(escudoCosa(l, r, desnudo = m.ficha.objeto == "escudo" || !mojaAlGato(m.ficha.clima)), 1.14f))
        add(braseroCosa(m, l, r))
        add(lenaCosa(l, r))
        add(bancoCosa(m, l, r))
        if (m.ficha.objeto == "pilum") add(pilumCosa(l, r))
        add(escaleraCosa(l, r))
    }

    /** La misma cosa, [k] veces más grande, sin moverla de donde pisa. */
    private fun grande(c: Cosa, k: Float): Cosa {
        val f = Path().apply { addPath(c.forma); transform(androidx.compose.ui.graphics.Matrix().apply { translate(c.pie.x, c.pie.y); scale(k, k); translate(-c.pie.x, -c.pie.y) }) }
        return Cosa(f, c.pie, c.bajoElFollaje) { scale(k, k, c.pie) { c.pintar(this) } }
    }

    /** La cuba del agua, de duelas y aros de mimbre, con su tapa corrida y el cazo colgado. */
    private fun cubaCosa(l: Luces, r: Rayo): Cosa {
        val pie = Offset(27f, 279f)
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx, pie.y + dy)
        val forma = Path().apply { moveTo(p(-12f, 0f)); cubicTo(p(-17f, -14f), p(-17f, -36f), p(-12.5f, -50f)); lineTo(p(12.5f, -50f)); cubicTo(p(17f, -36f), p(17f, -14f), p(12f, 0f)); close() }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.26f), p(-15f, -2.8f), Size(31f, 6f))
            volumen(l, r, forma, pie.x - 16f, pie.x + 16f, C.MADERA)
            clipPath(forma) {
                val az = Azar(Semilla.de("romano", "cuba"))
                for (i in -3..3) drawPath(Path().apply { moveTo(p(i * 3.6f, 0f)); cubicTo(p(i * 5f, -14f), p(i * 5f, -36f), p(i * 3.7f, -50f)) }, l.tono(C.MADERA_OSC).copy(alpha = 0.6f), style = Stroke(0.5f))
                // Los aros de mimbre trenzado y lo que el agua ha ido oscureciendo.
                for (y in listOf(-8f, -25f, -43f)) { drawRect(l.tono(lerp(C.PAJA, C.MADERA_OSC, 0.45f)), p(-18f, y - 1.5f), Size(36f, 3f)); drawLine(l.tono(lerp(C.PAJA, Color.White, 0.3f)).copy(alpha = 0.7f), p(-18f, y - 1.3f), p(18f, y - 1.3f), 0.4f); drawLine(Color.Black.copy(alpha = 0.25f), p(-18f, y + 1.9f), p(18f, y + 1.9f), 0.6f) }
                repeat(7) { mancha(p(az.entre(-12f, 12f), az.entre(-40f, -3f)), az.entre(3f, 7f), az.entre(3f, 8f), l.tono(Color(0xFF4A3A2A)), 0.2f) }
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFF5E6A42)).copy(alpha = 0.45f)), pie.y - 9f, pie.y), p(-18f, -9f), Size(36f, 9f))
            }
            // La tapa, corrida, deja ver el agua; el cazo cuelga del borde.
            drawOval(l.tono(C.MADERA_OSC), p(-12.6f, -52.6f), Size(25.2f, 5.4f)); drawOval(l.tono(lerp(C.RIO_HONDO, l.medio, 0.3f)), p(-11f, -52f), Size(22f, 4f))
            drawPath(Path().apply { addOval(Rect(p(-13.4f, -53.8f), Size(19f, 5f))) }, l.tono(lerp(C.MADERA, Color(0xFFFFF2D0), 0.25f)))
            drawLine(l.tono(C.MADERA_OSC).copy(alpha = 0.6f), p(-10f, -51.4f), p(2f, -51.4f), 0.4f)
            drawLine(l.tono(C.MADERA), p(12f, -50f), p(15f, -36f), 1.2f, StrokeCap.Round); drawOval(l.tono(C.BRONCE), p(12.6f, -37f), Size(5.4f, 4.6f))
        }
    }

    /** Un ánfora de vino agrio, recostada: [ladeo] la inclina. */
    private fun anforaCosa(l: Luces, r: Rayo, pie: Offset, alto: Float, ladeo: Float): Cosa {
        val w = alto * 0.19f
        val cima = pie + Offset(ladeo * alto, -alto)
        fun p(k: Float, dx: Float) = Offset(pie.x + (cima.x - pie.x) * k + dx, pie.y + (cima.y - pie.y) * k)
        val forma = Path().apply {
            moveTo(p(0f, 0f)); cubicTo(p(0.25f, -w * 1.1f), p(0.6f, -w * 1.45f), p(0.76f, -w * 0.42f)); lineTo(p(1f, -w * 0.48f)); lineTo(p(1f, w * 0.48f)); lineTo(p(0.76f, w * 0.42f))
            cubicTo(p(0.6f, w * 1.45f), p(0.25f, w * 1.1f), p(0f, 0f)); close()
        }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.22f), Offset(pie.x - w * 1.3f, pie.y - 1.6f), Size(w * 2.8f, 3.6f))
            val x = pie.x + ladeo * alto * 0.5f
            volumen(l, r, forma, x - w * 1.4f, x + w * 1.4f, C.BARRO)
            val sombra = l.tono(Color(0xFF86502E))
            for (s in listOf(-1f, 1f)) drawPath(Path().apply { moveTo(p(0.95f, s * w * 0.42f)); cubicTo(p(0.95f, s * w * 1.2f), p(0.72f, s * w * 1.26f), p(0.67f, s * w * 0.95f)) }, if (s * r.lat > 0f) l.tono(Color(0xFFD89A68)) else sombra, style = Stroke(alto * 0.045f, cap = StrokeCap.Round))
            drawLine(l.tono(Color(0xFFE8B080)), p(1f, -w * 0.55f), p(1f, w * 0.55f), alto * 0.05f, StrokeCap.Round)
            clipPath(forma) { drawLine(sombra.copy(alpha = 0.6f), p(0.6f, -w * 2f), p(0.6f, w * 2f), 0.6f); drawPath(poligono(p(0.36f, -w * 0.5f), p(0.5f, -w * 0.5f), p(0.5f, w * 0.3f), p(0.36f, w * 0.3f)), l.tono(C.ROJO_OSC).copy(alpha = 0.5f)) }     // el sello del almacén, medio borrado
        }
    }

    /** El saco de grano, de arpillera, vencido contra el parapeto: la boca atada, sus dos orejas y un remiendo. */
    private fun sacoCosa(l: Luces, r: Rayo): Cosa {
        val pie = Offset(104f, 262f)
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx, pie.y + dy)
        val forma = Path().apply {
            moveTo(p(-13f, 0f)); cubicTo(p(-17f, -8f), p(-14f, -19f), p(-7f, -22.6f)); lineTo(p(-10f, -27.6f)); lineTo(p(-5f, -25f)); lineTo(p(-1f, -26.4f)); lineTo(p(3f, -25f)); lineTo(p(8f, -27.6f)); lineTo(p(5.6f, -22.6f))
            cubicTo(p(13f, -19f), p(17f, -8f), p(13f, 0f)); close()
        }
        return Cosa(forma, pie) {
            val arpillera = Color(0xFFB89C6C)
            volumen(l, r, forma, pie.x - 16f, pie.x + 16f, arpillera)
            clipPath(forma) {
                val trama = l.tono(Color(0xFF7E6644))
                for (i in -5..5) drawPath(Path().apply { moveTo(p(i * 2.2f, -28f)); cubicTo(p(i * 3.4f, -18f), p(i * 3.6f, -8f), p(i * 3f, 0f)) }, trama.copy(alpha = 0.3f), style = Stroke(0.35f))
                for (i in 0..7) drawPath(Path().apply { val y = -2.6f - i * 3f; moveTo(p(-17f, y)); cubicTo(p(-6f, y + 1.6f), p(6f, y + 1.6f), p(17f, y)) }, trama.copy(alpha = 0.26f), style = Stroke(0.35f))
                // Las arrugas de la boca, el remiendo y lo sucio del pie.
                for (dx in listOf(-4f, 0f, 4f)) drawLine(trama.copy(alpha = 0.7f), p(dx * 0.5f, -23f), p(dx, -16f), 0.5f)
                drawRect(l.tono(Color(0xFFD2BC90)), p(3f, -13f), Size(6.4f, 6f)); for (k in 0..3) drawLine(trama, p(3f + k * 2f, -13.6f), p(3.6f + k * 2f, -12.4f), 0.35f)
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFF6E5A3E)).copy(alpha = 0.5f)), pie.y - 8f, pie.y), p(-17f, -8f), Size(34f, 8f))
            }
            drawLine(l.tono(C.CUERO_OSC), p(-6.4f, -23f), p(5f, -23f), 1.3f, StrokeCap.Round)
            val az = Azar(Semilla.de("romano", "saco"))
            repeat(9) { drawCircle(l.tono(C.PAJA), 0.5f, p(az.entre(-16f, 16f), az.entre(-0.6f, 2f))) }
        }
    }

    /** El molino de mano: la solera, la volandera con su manija, y la harina alrededor. */
    private fun molinoCosa(l: Luces, r: Rayo): Cosa {
        val pie = Offset(80f, 274f)
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx, pie.y + dy)
        val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(pie.x - 14f, pie.y - 16f, pie.x + 14f, pie.y, CornerRadius(4f))) }
        return Cosa(Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(pie.x - 18f, pie.y - 21f, pie.x + 18f, pie.y, CornerRadius(5f))) }, pie) { scale(1.3f, 1.3f, pie) {
            val piedra = Color(0xFF9A9488)
            mancha(p(2f, 0.6f), 22f, 3f, l.tono(Color(0xFFF2EAD8)), 0.6f)
            drawOval(Color.Black.copy(alpha = 0.24f), p(-15f, -3f), Size(31f, 6f))
            // La solera.
            val baja = Path().apply { addRect(Rect(p(-14f, -8f), Size(28f, 5f))); addOval(Rect(p(-14f, -5.6f), Size(28f, 5.6f))) }
            volumen(l, r, baja, pie.x - 14f, pie.x + 14f, piedra); drawOval(l.tono(lerp(piedra, Color.White, 0.3f)), p(-14f, -10.8f), Size(28f, 5.6f))
            // La volandera, con su ojo y su manija.
            val alta = Path().apply { addRect(Rect(p(-11.6f, -14f), Size(23.2f, 4.6f))); addOval(Rect(p(-11.6f, -11.8f), Size(23.2f, 4.8f))) }
            volumen(l, r, alta, pie.x - 11.6f, pie.x + 11.6f, lerp(piedra, C.PIEDRA, 0.3f)); drawOval(l.tono(lerp(piedra, Color.White, 0.42f)), p(-11.6f, -16.4f), Size(23.2f, 4.8f))
            drawOval(l.tono(Color(0xFF3A342E)), p(-2f, -15f), Size(4f, 1.8f))
            drawLine(l.tono(C.MADERA_OSC), p(8f, -13.6f), p(9f, -22.6f), 1.6f, StrokeCap.Round); drawLine(l.tono(lerp(C.MADERA, Color(0xFFFFF2D0), 0.3f)), p(8.4f, -13.6f), p(9.4f, -22.6f), 0.5f)
            val az = Azar(Semilla.de("romano", "grano del molino"))
            repeat(14) { drawCircle(l.tono(C.PAJA), 0.5f, p(az.entre(-18f, 20f), az.entre(-1.6f, 1.8f))) }
        } }
    }

    /** Las jabalinas del contubernio, en pabellón: tres astas atadas por arriba, con el casco de Cayo colgado. */
    private fun pabellonCosa(m: Momento, l: Luces, r: Rayo): Cosa {
        val nudo = Offset(101f, 196f)
        val pies = listOf(Offset(89f, 268f), Offset(101f, 278.6f), Offset(113.6f, 270f))
        val puntas = pies.map { nudo + (nudo - it) * 0.66f }
        val forma = Path().apply { pies.forEachIndexed { i, a -> addPath(poligono(a + Offset(-1.2f, 0f), a + Offset(1.2f, 0f), puntas[i] + Offset(0.5f, 0f), puntas[i] + Offset(-0.5f, 0f))) }; if (m.ficha.objeto != "casco") addOval(Rect(Offset(104f, 205f), 6.4f)) }
        return Cosa(forma, Offset(101f, 275f)) {
            pies.forEachIndexed { i, a ->
                val b = puntas[i]; val union = a + (b - a) * 0.6f; val hierro = a + (b - a) * 0.66f
                drawLine(l.tono(C.MADERA_OSC), a, union, 3f, StrokeCap.Round); drawLine(l.tono(lerp(C.MADERA, Color(0xFFFFF2D0), 0.35f)), a + Offset(-r.lat * 0.7f, 0f), union + Offset(-r.lat * 0.7f, 0f), 0.9f)
                drawLine(l.tono(C.BRONCE), union, hierro, 4f); drawLine(l.tono(C.HIERRO), hierro, b, 1.4f, StrokeCap.Round)
                val d = (b - a) / 100f
                drawPath(poligono(b + Offset(-d.y * 1.6f, d.x * 1.6f), b + Offset(d.y * 1.6f, -d.x * 1.6f), b + d * 5.6f), l.tono(lerp(C.HIERRO, Color.White, 0.3f)))
                drawLine(l.tono(C.HIERRO), a, a + (a - b) * 0.03f, 1.4f)                                                    // el regatón
            }
            // La correa que las ata, con su cabo suelto.
            for (dy in listOf(-1.4f, 0f, 1.4f)) drawLine(l.tono(C.CUERO_OSC), nudo + Offset(-2.6f, dy), nudo + Offset(2.6f, dy + 0.4f), 0.9f, StrokeCap.Round)
            drawLine(l.tono(C.CUERO_OSC), nudo + Offset(2f, 1f), nudo + Offset(4f, 8f), 0.6f, StrokeCap.Round)
            if (m.ficha.objeto != "casco") { drawLine(l.tono(C.CUERO_OSC), nudo + Offset(1f, 1f), Offset(104f, 200f), 0.5f); scale(0.8f, 0.8f, Offset(104f, 211f)) { casco(l, r, Offset(104f, 211f)) } }
        }
    }

    /** El escudo, recostado en el parapeto: en su funda de cuero o, el día que le toca, desnudo, con sus alas y sus rayos. */
    private fun escudoCosa(l: Luces, r: Rayo, desnudo: Boolean): Cosa {
        val pie = ESCUDO
        val base = Path().apply { moveTo(-15f, 0f); cubicTo(-16f, -18f, -16f, -38f, -15f, -56f); cubicTo(-6f, -58.4f, 6f, -58.4f, 15f, -56f); cubicTo(16f, -38f, 16f, -18f, 15f, 0f); cubicTo(6f, 2.2f, -6f, 2.2f, -15f, 0f); close() }
        val giro = androidx.compose.ui.graphics.Matrix().apply { translate(pie.x, pie.y); rotateZ(-7f) }
        val forma = Path().apply { addPath(base); transform(giro) }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.24f), Offset(pie.x - 17f, pie.y - 2.4f), Size(34f, 5.6f))
            volumen(l, r, forma, pie.x - 20f, pie.x + 14f, if (desnudo) C.ROJO else C.CUERO)
            clipPath(forma) {
                rotate(-7f, pie) {
                    fun p(dx: Float, dy: Float) = Offset(pie.x + dx, pie.y + dy)
                    if (desnudo) {
                        val oro = l.tono(C.ORO)
                        // El canto de bronce, las alas, los rayos y el umbo de hierro.
                        drawRoundRect(l.tono(C.BRONCE), p(-15.4f, -57.4f), Size(30.8f, 58.6f), CornerRadius(3f), style = Stroke(1.6f))
                        for (s in listOf(-1f, 1f)) {
                            // Las alas: tres plumas a cada lado de la tablilla.
                            for (i in -1..1) drawPath(Path().apply { moveTo(p(s * 7f, -28f + i * 2.4f)); cubicTo(p(s * 10f, -28.6f + i * 3.2f), p(s * 12f, -28.4f + i * 4.2f), p(s * 13.6f, -27f + i * 5f)) }, oro, style = Stroke(1.2f, cap = StrokeCap.Round))
                            // Los rayos: uno hacia arriba y otro hacia abajo, en zigzag, con su punta; y los de las esquinas.
                            val y0 = -28f + s * 5f
                            drawPath(Path().apply { moveTo(p(0f, y0)); lineTo(p(-1.8f, y0 + s * 5f)); lineTo(p(1.8f, y0 + s * 10f)); lineTo(p(0f, y0 + s * 15f)) }, oro, style = Stroke(1f, join = StrokeJoin.Round))
                            drawPath(poligono(p(-2.2f, y0 + s * 15f), p(2.2f, y0 + s * 15f), p(0f, y0 + s * 19.6f)), oro)
                            for (t in listOf(-1f, 1f)) { drawLine(oro, p(s * 6f, -28f + t * 5f), p(s * 11.6f, -28f + t * 17f), 0.8f, StrokeCap.Round); drawCircle(oro, 0.9f, p(s * 11.6f, -28f + t * 17f)) }
                        }
                        drawRect(oro, p(-7f, -33f), Size(14f, 10f), style = Stroke(1f))
                        drawCircle(l.tono(C.HIERRO), 4.6f, p(0f, -28f)); drawCircle(l.tono(lerp(C.HIERRO, Color.White, 0.5f)), 1.8f, p(-1.2f * r.lat.let { if (it >= 0f) -1f else 1f }, -29.4f))
                    } else {
                        // La funda: la costura, el cordón de la boca y el parche de cuero claro con su marca.
                        val costura = l.tono(C.CUERO_OSC)
                        drawLine(costura, p(0f, -57f), p(0f, 1f), 0.5f)
                        for (i in 0..13) drawLine(costura, p(-1.2f, -54f + i * 4f), p(1.2f, -53f + i * 4f), 0.4f)
                        drawLine(l.tono(C.LINO).copy(alpha = 0.8f), p(-15f, -51f), p(15f, -51f), 0.7f)
                        drawRoundRect(l.tono(lerp(C.CUERO, C.LINO, 0.5f)), p(-8f, -36f), Size(16f, 9f), CornerRadius(1f)); drawRoundRect(costura, p(-8f, -36f), Size(16f, 9f), CornerRadius(1f), style = Stroke(0.4f))
                        drawPath(poligono(p(-8f, -33.5f), p(-10.6f, -31.5f), p(-8f, -29.5f)), l.tono(lerp(C.CUERO, C.LINO, 0.5f))); drawPath(poligono(p(8f, -33.5f), p(10.6f, -31.5f), p(8f, -29.5f)), l.tono(lerp(C.CUERO, C.LINO, 0.5f)))
                        val az = Azar(Semilla.de("romano", "funda"))
                        repeat(8) { mancha(p(az.entre(-12f, 12f), az.entre(-52f, -4f)), az.entre(3f, 7f), az.entre(2f, 5f), l.tono(if (it % 2 == 0) C.CUERO_OSC else Color(0xFFD8B88A)), 0.22f) }
                        drawCircle(l.tono(C.CUERO_OSC).copy(alpha = 0.35f), 5f, p(0f, -18f), style = Stroke(0.7f))                 // donde abulta el umbo
                    }
                }
            }
        }
    }

    /** El brasero del puesto: un cuenco de hierro sobre tres patas, con su olla de bronce a la hora de comer. */
    private fun braseroCosa(m: Momento, l: Luces, r: Rayo): Cosa {
        val pie = BRASERO; val k = KB
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx * k, pie.y + dy * k)
        val conOlla = EscenaRomano.comiendo(m)
        val forma = Path().apply {
            moveTo(p(-9f, 0f)); lineTo(p(-6f, -9f)); lineTo(p(-12f, -15f)); lineTo(p(-12.6f, -18f))
            if (conOlla) { lineTo(p(-8.4f, -18f)); lineTo(p(-9.4f, -24f)); lineTo(p(-7f, -28.6f)); lineTo(p(7f, -28.6f)); lineTo(p(9.4f, -24f)); lineTo(p(8.4f, -18f)) }
            lineTo(p(12.6f, -18f)); lineTo(p(12f, -15f)); lineTo(p(6f, -9f)); lineTo(p(9f, 0f)); lineTo(p(6.4f, 0f)); lineTo(p(2f, -8f)); lineTo(p(-2f, -8f)); lineTo(p(-6.4f, 0f)); close()
        }
        return Cosa(forma, pie) {
            val az = Azar(Semilla.de("romano", "ceniza"))
            repeat(5) { mancha(p(az.entre(-13f, 15f), az.entre(-1f, 2.2f)), az.entre(4f, 9f) * k, az.entre(1f, 2f) * k, l.tono(Color(0xFF8E8880)), 0.4f) }
            val hierro = l.tono(Color(0xFF4A4A52)); val filo = l.tono(lerp(C.HIERRO, Color(0xFFFFE0B0), 0.3f + 0.3f * l.noche))
            for (dx in listOf(-7.6f, 7.6f)) drawLine(hierro, p(dx * 0.8f, -9f), p(dx * 1.1f, 0f), 1.8f * k, StrokeCap.Round)
            drawLine(hierro, p(0f, -8f), p(0f, -1f), 1.6f * k, StrokeCap.Round)
            val cuenco = Path().apply { moveTo(p(-12.6f, -18f)); lineTo(p(12.6f, -18f)); cubicTo(p(12f, -11f), p(6f, -8f), p(0f, -8f)); cubicTo(p(-6f, -8f), p(-12f, -11f), p(-12.6f, -18f)); close() }
            volumen(l, r, cuenco, pie.x - 13f * k, pie.x + 13f * k, Color(0xFF5A5A62))
            drawLine(filo, p(-12.6f, -18f), p(12.6f, -18f), 0.9f * k, StrokeCap.Round)
            for (dx in listOf(-13.4f, 13.4f)) drawCircle(hierro, 1.8f * k, p(dx, -15f), style = Stroke(0.7f * k))                          // las asas
            if (conOlla) {
                val olla = Path().apply { moveTo(p(-8.4f, -18.4f)); cubicTo(p(-10.6f, -22f), p(-9f, -27f), p(-7f, -28.6f)); lineTo(p(7f, -28.6f)); cubicTo(p(9f, -27f), p(10.6f, -22f), p(8.4f, -18.4f)); close() }
                volumen(l, r, olla, pie.x - 10f * k, pie.x + 10f * k, C.BRONCE)
                drawLine(l.tono(lerp(C.BRONCE, Color(0xFFFFE9B0), 0.5f)), p(-7.4f, -28.6f), p(7.4f, -28.6f), 1f * k, StrokeCap.Round)
                drawArc(l.tono(C.HIERRO), 180f, 180f, false, p(-6f, -34f), Size(12f * k, 9f * k), style = Stroke(0.6f * k))
            }
        }
    }

    /** La leña del brasero: una pila de rajas, con el hacha de mano clavada en un tajo. */
    private fun lenaCosa(l: Luces, r: Rayo): Cosa {
        val pie = Offset(LENA_X, 283f)
        fun p(dx: Float, dy: Float) = Offset(pie.x + dx, pie.y + dy)
        val forma = poligono(listOf(p(-11f, 0f), p(-10f, -7f), p(-6f, -12f), p(5f, -12.6f), p(10f, -7f), p(11f, 0f)))
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.22f), p(-12f, -2f), Size(25f, 4.6f))
            val az = Azar(Semilla.de("romano", "lena"))
            val filas = listOf(listOf(-7.6f, -2.6f, 2.6f, 7.6f) to -2.8f, listOf(-5f, 0f, 5f) to -7.4f, listOf(-2.4f, 2.6f) to -11.6f)
            filas.forEach { (xs, y) -> xs.forEach { x ->
                val c = p(x + az.entre(-0.4f, 0.4f), y); val rad = az.entre(2.5f, 3f)
                drawCircle(l.tono(C.MADERA_OSC), rad, c); drawCircle(l.tono(lerp(C.MADERA, Color(0xFFFFF2D0), 0.35f)), rad * 0.78f, c); drawCircle(l.tono(C.MADERA).copy(alpha = 0.7f), rad * 0.4f, c, style = Stroke(0.35f))
                drawLine(l.tono(C.MADERA_OSC).copy(alpha = 0.6f), c + Offset(-rad * 0.6f, 0f), c + Offset(rad * 0.5f, az.entre(-0.8f, 0.8f)), 0.35f)
            } }
            repeat(6) { val q = p(az.entre(-14f, 14f), az.entre(-0.6f, 1.6f)); drawLine(l.tono(C.MADERA), q, q + Offset(az.entre(1.6f, 3.4f), az.entre(-0.8f, 0.8f)), 0.6f, StrokeCap.Round) }       // astillas
        }
    }

    /** El banco: un tablón grueso sobre dos tocones, con los dados de Barbatio y, encima, el objeto del día. */
    private fun bancoCosa(m: Momento, l: Luces, r: Rayo): Cosa {
        val x0 = 222f; val x1 = 302f; val yt = 250.4f; val yf = 257.6f; val g = 4.4f; val y1 = 280.6f
        val forma = poligono(listOf(Offset(x0 + 4f, yt), Offset(x1 - 4f, yt), Offset(x1, yf), Offset(x1, yf + g), Offset(x1 - 8f, yf + g), Offset(x1 - 8f, y1), Offset(x1 - 22f, y1), Offset(x1 - 22f, yf + g),
            Offset(x0 + 22f, yf + g), Offset(x0 + 22f, y1), Offset(x0 + 8f, y1), Offset(x0 + 8f, yf + g), Offset(x0, yf + g), Offset(x0, yf)))
        return Cosa(forma, Offset((x0 + x1) / 2f, y1)) {
            val madera = l.tono(C.MADERA); val luz = l.tono(lerp(C.MADERA, Color(0xFFFFF2D0), 0.45f)); val sombra = l.tono(mult(C.MADERA_OSC, r.color, 0.6f * r.contraste))
            drawOval(Color.Black.copy(alpha = 0.22f), Offset(x0 + 2f, y1 - 2.4f), Size(x1 - x0 - 4f, 5.4f))
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.34f), Color.Transparent), yf + g, yf + g + 10f), Offset(x0 + 8f, yf + g), Size(x1 - x0 - 16f, 10f))
            // Los tocones, con su corteza.
            for (px in listOf(x0 + 8f, x1 - 22f)) {
                val tocon = Path().apply { addRect(Rect(px, yf + g, px + 14f, y1 - 1.6f)); addOval(Rect(px, y1 - 4f, px + 14f, y1)) }
                volumen(l, r, tocon, px, px + 14f, lerp(C.MADERA_OSC, C.MADERA_VIEJA, 0.35f))
                clipPath(tocon) { val az = Azar(Semilla.de("romano", "tocon", px.toInt())); repeat(7) { val x = az.entre(px, px + 14f); drawLine(l.tono(Color(0xFF2E241C)).copy(alpha = 0.5f), Offset(x, yf + g), Offset(x + az.entre(-1f, 1f), y1), 0.5f) } }
            }
            val tablero = poligono(Offset(x0 + 4f, yt), Offset(x1 - 4f, yt), Offset(x1, yf), Offset(x0, yf))
            drawPath(tablero, Brush.verticalGradient(listOf(madera, luz), yt, yf))
            val az = Azar(Semilla.de("romano", "banco"))
            clipPath(tablero) {
                repeat(9) { val y = az.entre(yt + 0.6f, yf - 0.4f); drawLine(l.tono(C.MADERA_OSC).copy(alpha = 0.4f), Offset(x0, y), Offset(x1, y + az.entre(-1f, 1f)), 0.35f) }
                drawOval(l.tono(Color(0xFF3A2C20)).copy(alpha = 0.5f), Offset(x0 + 12f, yt + 2f), Size(6f, 2.4f), style = Stroke(0.6f))                 // el cerco que dejó una olla
                repeat(5) { val x = az.entre(x0 + 6f, x1 - 8f); drawLine(l.tono(Color(0xFF3A2C20)).copy(alpha = 0.55f), Offset(x, az.entre(yt + 1f, yf - 2f)), Offset(x + az.entre(1.5f, 4f), az.entre(yt + 1f, yf - 1f)), 0.35f) }    // cortes de cuchillo
            }
            drawRect(Brush.verticalGradient(listOf(madera, sombra), yf, yf + g), Offset(x0, yf), Size(x1 - x0, g))
            clipRect(x0, yf, x1, yf + g) { repeat(6) { val x = az.entre(x0, x1); drawLine(l.tono(Color(0xFF2E241C)).copy(alpha = 0.4f), Offset(x, yf + az.entre(0.8f, 3.4f)), Offset(x + az.entre(8f, 20f), yf + az.entre(0.8f, 3.4f)), 0.35f) } }
            drawLine(luz, Offset(x0, yf), Offset(x1, yf), 0.5f)
            // Los dados de hueso, en una punta.
            for ((i, d) in listOf(Offset(x0 + 9f, yf - 3.6f), Offset(x0 + 13.6f, yf - 2.4f)).withIndex()) {
                drawRect(l.tono(Color(0xFFF2EAD8)), d, Size(2.6f, 2.4f)); drawRect(cara(l, r, Color(0xFFD8CEB8), if (r.lat >= 0f) -1f else 1f), Offset(if (r.lat >= 0f) d.x else d.x + 1.9f, d.y), Size(0.7f, 2.4f))
                drawCircle(l.tono(Color(0xFF2E241C)), 0.28f, d + Offset(0.8f, 0.7f)); if (i == 0) drawCircle(l.tono(Color(0xFF2E241C)), 0.28f, d + Offset(1.8f, 1.7f))
            }
            // El objeto del día, encima.
            val sobre = SOBRE
            val enElBanco = m.ficha.objeto != null && m.ficha.objeto != "escudo" && m.ficha.objeto != "pilum"
            capaSombra(r) { c -> if (enElBanco) clipPath(tablero) { drawOval(c, Offset(sobre.x - 20f + r.ax * 4f, sobre.y - 2.4f), Size(40f, 5f)) } }
            scale(KO, KO, sobre) {
                when (m.ficha.objeto) {
                    "gladio" -> gladio(l, r, sobre)
                    "casco" -> casco(l, r, sobre + Offset(0f, 0.4f))
                    "caligas" -> caligas(l, r, sobre)
                    "dolabra" -> dolabra(l, r, sobre)
                    "petate" -> petate(l, r, sobre)
                    "posca" -> posca(l, r, sobre)
                    "pan" -> pan(l, r, sobre)
                    "moneda" -> monedas(l, r, sobre)
                    "tablilla" -> tablilla(l, r, sobre)
                    "lucerna" -> lucerna(l, r, sobre)
                    else -> Unit
                }
                if (EscenaRomano.conGente(m) && m.ficha.objeto != "posca") vaso(l, r, sobre + Offset(21f, 0f))
                if (m.con("luto")) ramaDeCipres(l, sobre + Offset(-22f, 0f))
            }
        }
    }

    private val PILUM_A = Offset(300f, 283f); private val PILUM_B = Offset(238f, 172f)

    /** La jabalina del día, apoyada en el parapeto: el regatón en el suelo y el hierro asomando por encima de la almena. */
    private fun pilumCosa(l: Luces, r: Rayo): Cosa {
        val a = PILUM_A; val b = PILUM_B; val u = (b - a) / 100f
        val forma = poligono(a + Offset(-1.4f, 0.6f), a + Offset(1.4f, -0.6f), b + Offset(0.7f, -0.3f), b + Offset(-0.7f, 0.3f))
        return Cosa(forma, a, bajoElFollaje = false) {
            val union = a + u * 58f; val hierro = a + u * 65f
            drawLine(l.tono(C.MADERA_OSC), a, union, 3f, StrokeCap.Round); drawLine(l.tono(lerp(C.MADERA, Color(0xFFFFF2D0), 0.35f)), a + Offset(-0.7f, -0.5f), union + Offset(-0.7f, -0.5f), 0.9f)
            // La empuñadura de cuerda, el taco de la unión con sus pasadores y el hierro largo con su punta.
            for (i in 0..5) drawLine(l.tono(C.LINO), a + u * (30f + i * 1.4f) + Offset(-1.6f, 0.7f), a + u * (30.6f + i * 1.4f) + Offset(1.6f, -0.7f), 0.6f)
            drawPath(poligono(union + Offset(-2.4f, 1f), union + Offset(2.4f, -1f), hierro + Offset(1.4f, -0.6f), hierro + Offset(-1.4f, 0.6f)), l.tono(C.BRONCE))
            drawCircle(l.tono(C.HIERRO), 0.5f, union + u * 2f); drawCircle(l.tono(C.HIERRO), 0.5f, union + u * 5f)
            drawLine(l.tono(C.HIERRO), hierro, b - u * 5f, 1.3f); drawLine(l.tono(lerp(C.HIERRO, Color.White, 0.4f)), hierro + Offset(-0.3f, -0.2f), b - u * 5f + Offset(-0.3f, -0.2f), 0.4f)
            drawPath(poligono(b - u * 6f + Offset(-2f, 0.9f), b - u * 6f + Offset(2f, -0.9f), b), l.tono(lerp(C.HIERRO, Color.White, 0.3f)))
            drawLine(l.tono(C.HIERRO), a, a - u * 3f, 1.8f, StrokeCap.Round)
        }
    }

    /** La escalera de mano que sube a la galería de la torre. */
    private fun escaleraCosa(l: Luces, r: Rayo): Cosa {
        val a0 = Offset(305f, 284.6f); val a1 = Offset(318f, 284.6f); val b0 = Offset(TORRE - 4f, 109f); val b1 = Offset(TORRE + 6f, 110f)
        val forma = Path().apply { addPath(poligono(a0 + Offset(-1.3f, 0f), a0 + Offset(1.3f, 0f), b0 + Offset(1.1f, 0f), b0 + Offset(-1.1f, 0f))); addPath(poligono(a1 + Offset(-1.3f, 0f), a1 + Offset(1.3f, 0f), b1 + Offset(1.1f, 0f), b1 + Offset(-1.1f, 0f))) }
        return Cosa(forma, Offset(311f, 284.6f), bajoElFollaje = false) {
            val madera = l.tono(C.MADERA); val osc = l.tono(mult(C.MADERA_OSC, r.color, 0.4f)); val luz = l.tono(lerp(C.MADERA, Color(0xFFFFF2D0), 0.45f))
            for (i in 1..13) { val k = i / 14f; val p = a0 + (b0 - a0) * k; val q = a1 + (b1 - a1) * k; drawLine(osc, p + Offset(0f, 0.9f), q + Offset(0f, 0.9f), 1.2f); drawLine(madera, p, q, 1.5f); drawLine(luz, p + Offset(0f, -0.5f), q + Offset(0f, -0.5f), 0.4f) }
            for ((a, b) in listOf(a0 to b0, a1 to b1)) { drawLine(madera, a, b, 2.6f, StrokeCap.Round); drawLine(if (r.lat >= 0f) luz else osc, a + Offset(0.8f, 0f), b + Offset(0.7f, 0f), 0.7f); drawLine(if (r.lat >= 0f) osc else luz, a + Offset(-0.9f, 0f), b + Offset(-0.8f, 0f), 0.6f) }
        }
    }

    /** El sago de Cayo, la capa de lana parda, echado a secar sobre la última almena: cae en pliegues y asoma su fíbula. */
    private fun DrawScope.sago(l: Luces, r: Rayo) {
        val x0 = 5f * W + 4f; val x1 = 5f * W + W_ALMENA - 5f; val y = ALMENA - 3f
        val propio = Color(0xFF8E4A30)
        val forma = Path().apply {
            moveTo(x0 - 1f, y + 0.6f); cubicTo(x0 + 6f, y - 2.2f, x1 - 6f, y - 2.4f, x1 + 1.4f, y + 0.4f)
            lineTo(x1 + 2.6f, y + 21f); lineTo(x1 - 2f, y + 27f); lineTo(x1 - 7f, y + 23f); lineTo(x1 - 11f, y + 31f); lineTo(x0 + 9f, y + 26.4f); lineTo(x0 + 5f, y + 32.6f); lineTo(x0 - 0.6f, y + 25f); close()
        }
        if (r.fuerza > 0.05f) drawPath(Path().apply { addPath(forma, Offset(-r.lat * 2.4f, 1.6f)) }, r.color, alpha = r.fuerza * 0.7f, blendMode = BlendMode.Multiply)
        val luz = l.tono(lerp(propio, Color(0xFFFFE0B0), 0.34f)); val medio = l.tono(propio); val sombra = l.tono(mult(Color(0xFF5E2C20), r.color, 0.5f))
        drawPath(forma, Brush.horizontalGradient(if (r.lat >= 0f) listOf(sombra, medio, luz, medio, sombra, medio, luz) else listOf(luz, medio, sombra, medio, luz, medio, sombra), x0, x1))
        clipPath(forma) {
            for (x in listOf(x0 + 6f, x0 + 13.4f, x1 - 5f)) drawPath(Path().apply { moveTo(x, y + 2f); cubicTo(x - 1.4f, y + 12f, x + 1.4f, y + 20f, x - 0.6f, y + 33f) }, sombra.copy(alpha = 0.7f), style = Stroke(1.1f, cap = StrokeCap.Round))
            drawRect(Brush.verticalGradient(listOf(luz.copy(alpha = 0.8f), Color.Transparent), y - 2f, y + 3.6f), Offset(x0 - 2f, y - 3f), Size(x1 - x0 + 5f, 6.6f))
            // La lana, basta: unas hebras claras; y el remate del borde, más oscuro.
            val az = Azar(Semilla.de("romano", "sago"))
            repeat(26) { val p = Offset(az.entre(x0, x1), az.entre(y + 2f, y + 30f)); drawLine(luz.copy(alpha = 0.3f), p, p + Offset(az.entre(-0.6f, 0.6f), az.entre(1.4f, 3f)), 0.35f) }
        }
        drawCircle(l.tono(C.BRONCE), 1.5f, Offset(x0 + 5.4f, y + 6f)); drawLine(l.tono(lerp(C.BRONCE, Color.White, 0.3f)), Offset(x0 + 3.4f, y + 5.6f), Offset(x0 + 8.4f, y + 6.8f), 0.5f)
    }

    /** El rollo de cuerda colgado de una escarpia en la última almena. */
    private fun DrawScope.cuerdaColgada(l: Luces, r: Rayo) {
        val c = Offset(3f * W + 17f, 216f)
        val soga = l.tono(Color(0xFFC2A470)); val sogaOsc = l.tono(mult(Color(0xFF8A6E44), r.color, 0.4f))
        drawLine(l.tono(C.HIERRO), c + Offset(0f, -10f), c + Offset(0f, -7f), 1.6f, StrokeCap.Round)
        for (i in 0..3) { drawOval(sogaOsc, Offset(c.x - 6.4f + i * 0.5f, c.y - 8f + i * 0.4f), Size(12.8f - i, 17f - i * 0.6f), style = Stroke(1.5f)); drawOval(soga, Offset(c.x - 6.4f + i * 0.5f, c.y - 8.5f + i * 0.4f), Size(12.8f - i, 17f - i * 0.6f), style = Stroke(1f)) }
        for (i in 0..2) drawLine(soga, c + Offset(-3.6f, -4.6f + i * 1.2f), c + Offset(3.6f, -5f + i * 1.2f), 0.8f)
    }

    // ---------------------------------------------------------------- Los objetos del día

    /** El casco de hierro con ribetes de bronce: la calota, la visera, el cubrenuca y la carrillera. */
    private fun DrawScope.casco(l: Luces, r: Rayo, s: Offset) {
        val calota = Path().apply { moveTo(s.x - 6.4f, s.y - 3f); cubicTo(s.x - 6.8f, s.y - 9.4f, s.x - 3f, s.y - 12.4f, s.x + 0.4f, s.y - 12.4f); cubicTo(s.x + 4.4f, s.y - 12.4f, s.x + 6.8f, s.y - 9f, s.x + 6.4f, s.y - 3f); close() }
        volumen(l, r, calota, s.x - 7f, s.x + 7f, C.HIERRO)
        drawLine(l.tono(C.BRONCE), Offset(s.x - 7.2f, s.y - 5.6f), Offset(s.x + 7f, s.y - 5.6f), 1.1f, StrokeCap.Round)                     // la visera
        drawPath(poligono(Offset(s.x + 5.4f, s.y - 3.6f), Offset(s.x + 10.6f, s.y - 1.2f), Offset(s.x + 10.2f, s.y), Offset(s.x + 5f, s.y - 1.4f)), l.tono(C.HIERRO))                   // el cubrenuca
        drawPath(Path().apply { moveTo(s.x - 4.6f, s.y - 3.4f); lineTo(s.x + 1f, s.y - 3.4f); lineTo(s.x + 0.4f, s.y - 0.2f); cubicTo(s.x - 1.6f, s.y + 0.4f, s.x - 4f, s.y, s.x - 4.6f, s.y - 3.4f); close() }, l.tono(lerp(C.HIERRO, Color(0xFF3A3A44), 0.3f)))
        drawCircle(l.tono(C.BRONCE), 0.6f, Offset(s.x - 1.8f, s.y - 2f)); drawCircle(l.tono(C.BRONCE), 1f, Offset(s.x + 0.4f, s.y - 12.8f))
        drawLine(l.tono(lerp(C.HIERRO, Color.White, 0.6f)).copy(alpha = 0.8f), Offset(s.x - 3.6f * (if (r.lat >= 0f) -1f else 1f), s.y - 10f), Offset(s.x - 4.8f * (if (r.lat >= 0f) -1f else 1f), s.y - 7f), 0.7f, StrokeCap.Round)
    }

    /** La espada corta en su vaina, con el cinto enrollado. */
    private fun DrawScope.gladio(l: Luces, r: Rayo, s: Offset) {
        val vaina = l.tono(C.ROJO_OSC); val bronce = l.tono(C.BRONCE)
        drawPath(Path().apply { moveTo(s.x - 16f, s.y - 0.4f); cubicTo(s.x - 20f, s.y - 5f, s.x - 11f, s.y - 6f, s.x - 9f, s.y - 2f) }, l.tono(C.CUERO_OSC), style = Stroke(1.5f, cap = StrokeCap.Round))
        drawRoundRect(vaina, Offset(s.x - 12f, s.y - 3.2f), Size(20f, 3.2f), CornerRadius(0.8f)); drawLine(l.tono(lerp(C.ROJO, Color.White, 0.2f)), Offset(s.x - 11.6f, s.y - 2.8f), Offset(s.x + 7.6f, s.y - 2.8f), 0.4f)
        for (x in listOf(-12f, -6f, 1f)) drawRect(bronce, Offset(s.x + x, s.y - 3.4f), Size(1.5f, 3.6f))
        drawPath(poligono(Offset(s.x - 12f, s.y - 3.2f), Offset(s.x - 12f, s.y), Offset(s.x - 15.4f, s.y - 1.6f)), bronce)                    // la contera
        drawRect(l.tono(lerp(C.HIERRO, Color.White, 0.3f)), Offset(s.x + 8f, s.y - 2.8f), Size(1.8f, 2.4f))                                // un dedo de hoja
        drawRoundRect(l.tono(C.MADERA_OSC), Offset(s.x + 9.6f, s.y - 3.8f), Size(1.6f, 4.4f), CornerRadius(0.5f))                          // la guarda
        drawRect(l.tono(Color(0xFFE8DEC8)), Offset(s.x + 11.2f, s.y - 2.7f), Size(4.4f, 2.2f)); for (i in 0..2) drawLine(l.tono(Color(0xFFB8AC94)), Offset(s.x + 12.2f + i * 1.2f, s.y - 2.7f), Offset(s.x + 12.2f + i * 1.2f, s.y - 0.5f), 0.3f)
        drawCircle(l.tono(C.MADERA), 1.9f, Offset(s.x + 17f, s.y - 1.6f))                                                                 // el pomo
    }

    /** Las botas de clavos: una de pie y otra tumbada, con las tachuelas a la vista. */
    private fun DrawScope.caligas(l: Luces, r: Rayo, s: Offset) {
        val cuero = l.tono(C.CUERO); val osc = l.tono(C.CUERO_OSC)
        val bota = Path().apply { moveTo(s.x - 9f, s.y); lineTo(s.x - 9f, s.y - 7.6f); lineTo(s.x - 4.6f, s.y - 7.6f); lineTo(s.x - 4f, s.y - 3.4f); cubicTo(s.x - 1f, s.y - 3.6f, s.x + 2f, s.y - 2.4f, s.x + 2.6f, s.y); close() }
        volumen(l, r, bota, s.x - 9f, s.x + 2.6f, C.CUERO)
        for (i in 0..3) drawLine(osc, Offset(s.x - 9f, s.y - 6.6f + i * 1.6f), Offset(s.x - 4.4f + i * 1.2f, s.y - 6.2f + i * 1.5f), 0.5f)                  // las tiras
        drawLine(l.tono(Color(0xFF3A2C20)), Offset(s.x - 9.2f, s.y - 0.2f), Offset(s.x + 2.8f, s.y - 0.2f), 0.9f)
        // La otra, tumbada: se le ve la suela claveteada.
        val suela = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(s.x + 4f, s.y - 3.8f, s.x + 16f, s.y, CornerRadius(1.8f))) }
        drawPath(suela, l.tono(Color(0xFF6A4E34))); drawPath(suela, osc, style = Stroke(0.4f))
        for (i in 0..4) for (j in 0..1) drawCircle(l.tono(lerp(C.HIERRO, Color.White, 0.3f)), 0.42f, Offset(s.x + 5.6f + i * 2.2f, s.y - 2.8f + j * 1.7f))
        drawLine(cuero, Offset(s.x + 4.4f, s.y - 3.8f), Offset(s.x + 2.6f, s.y - 6.4f), 0.8f, StrokeCap.Round)
    }

    /** La dolabra, tendida sobre el banco: hacha de un lado, pico del otro. */
    private fun DrawScope.dolabra(l: Luces, r: Rayo, s: Offset) {
        drawLine(l.tono(C.MADERA_OSC), Offset(s.x - 17f, s.y - 1f), Offset(s.x + 10f, s.y - 2.4f), 1.8f, StrokeCap.Round); drawLine(l.tono(lerp(C.MADERA, Color(0xFFFFF2D0), 0.3f)), Offset(s.x - 17f, s.y - 1.5f), Offset(s.x + 10f, s.y - 2.9f), 0.5f)
        val hierro = l.tono(C.HIERRO); val filo = l.tono(lerp(C.HIERRO, Color.White, 0.5f))
        drawPath(poligono(Offset(s.x + 9f, s.y - 3.6f), Offset(s.x + 12f, s.y - 3.6f), Offset(s.x + 13.4f, s.y - 9.6f), Offset(s.x + 7.6f, s.y - 9f)), hierro); drawLine(filo, Offset(s.x + 7.6f, s.y - 9f), Offset(s.x + 13.4f, s.y - 9.6f), 0.6f)
        drawPath(poligono(Offset(s.x + 9.4f, s.y - 1.4f), Offset(s.x + 12f, s.y - 1.4f), Offset(s.x + 11f, s.y + 0.2f), Offset(s.x + 10.4f, s.y + 0.2f)), hierro)
        drawPath(Path().apply { moveTo(s.x + 9f, s.y - 1.4f); cubicTo(s.x + 6f, s.y - 0.4f, s.x + 4.6f, s.y, s.x + 3f, s.y); lineTo(s.x + 9.6f, s.y - 2.6f); close() }, hierro)
        drawRect(hierro, Offset(s.x + 8.8f, s.y - 4f), Size(3.4f, 3f))
    }

    /** El petate de marcha: la capa enrollada con sus correas, la red del pan y la sartén colgando. */
    private fun DrawScope.petate(l: Luces, r: Rayo, s: Offset) {
        val rollo = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(s.x - 15f, s.y - 9f, s.x + 9f, s.y, CornerRadius(4.4f))) }
        volumen(l, r, rollo, s.x - 15f, s.x + 9f, Color(0xFF8A6A4E))
        clipPath(rollo) { for (y in listOf(-6.4f, -3.4f)) drawLine(l.tono(Color(0xFF5E4630)).copy(alpha = 0.5f), Offset(s.x - 15f, s.y + y), Offset(s.x + 9f, s.y + y - 0.4f), 0.4f) }
        for (x in listOf(-9.6f, 3f)) { drawRect(l.tono(C.CUERO_OSC), Offset(s.x + x, s.y - 9.2f), Size(1.6f, 9.4f)); drawRect(l.tono(C.BRONCE), Offset(s.x + x - 0.3f, s.y - 5.6f), Size(2.2f, 1.6f)) }
        drawOval(l.tono(Color(0xFF6E5038)), Offset(s.x - 15.6f, s.y - 8.4f), Size(3f, 7.8f)); drawOval(l.tono(Color(0xFF9A7A5A)), Offset(s.x - 15f, s.y - 7f), Size(1.6f, 5f), style = Stroke(0.4f))
        // La sartén de bronce y la red.
        drawOval(l.tono(C.BRONCE), Offset(s.x + 10f, s.y - 4.6f), Size(9f, 4.6f)); drawOval(l.tono(lerp(C.BRONCE, Color(0xFF3A2C20), 0.4f)), Offset(s.x + 11.2f, s.y - 4f), Size(6.6f, 3f)); drawLine(l.tono(C.BRONCE), Offset(s.x + 18.6f, s.y - 2.4f), Offset(s.x + 23.4f, s.y - 3.6f), 1f, StrokeCap.Round)
    }

    /** La posca: la jarra de barro, el vaso de madera y una mancha de vino. */
    private fun DrawScope.posca(l: Luces, r: Rayo, s: Offset) {
        val jarra = Path().apply { moveTo(s.x - 6f, s.y); cubicTo(s.x - 9.6f, s.y - 4f, s.x - 8.6f, s.y - 9.6f, s.x - 5f, s.y - 11f); lineTo(s.x - 5.6f, s.y - 14.6f); lineTo(s.x + 1.6f, s.y - 14.6f); lineTo(s.x + 1f, s.y - 11f); cubicTo(s.x + 4.6f, s.y - 9.6f, s.x + 5.6f, s.y - 4f, s.x + 2f, s.y); close() }
        volumen(l, r, jarra, s.x - 9f, s.x + 5.4f, C.BARRO)
        drawPath(Path().apply { moveTo(s.x + 1.2f, s.y - 13.6f); cubicTo(s.x + 6.6f, s.y - 13f, s.x + 6.6f, s.y - 7f, s.x + 3.8f, s.y - 6f) }, l.tono(Color(0xFF9A5A34)), style = Stroke(1.3f, cap = StrokeCap.Round))
        drawLine(l.tono(Color(0xFFE8B080)), Offset(s.x - 5.6f, s.y - 14.6f), Offset(s.x + 1.6f, s.y - 14.6f), 0.9f, StrokeCap.Round)
        clipPath(jarra) { drawLine(l.tono(Color(0xFF86502E)).copy(alpha = 0.6f), Offset(s.x - 9f, s.y - 8.4f), Offset(s.x + 6f, s.y - 8.4f), 0.5f) }
        vaso(l, r, s + Offset(12f, 0f))
        mancha(s + Offset(18f, 0.4f), 3.4f, 0.9f, l.tono(C.ROJO_OSC), 0.5f)
    }

    /** Un vaso de madera torneada. */
    private fun DrawScope.vaso(l: Luces, r: Rayo, s: Offset) {
        val forma = poligono(Offset(s.x - 2.6f, s.y), Offset(s.x - 3.4f, s.y - 6.4f), Offset(s.x + 3.4f, s.y - 6.4f), Offset(s.x + 2.6f, s.y))
        volumen(l, r, forma, s.x - 3.4f, s.x + 3.4f, C.MADERA)
        drawOval(l.tono(C.ROJO_OSC), Offset(s.x - 3f, s.y - 7.2f), Size(6f, 1.6f)); drawLine(l.tono(C.MADERA_OSC).copy(alpha = 0.6f), Offset(s.x - 3.2f, s.y - 3.4f), Offset(s.x + 3.2f, s.y - 3.4f), 0.4f)
    }

    /** El pan del contubernio: una hogaza redonda marcada en ocho, con una cuña cortada. */
    private fun DrawScope.pan(l: Luces, r: Rayo, s: Offset) {
        val hogaza = Path().apply { addOval(Rect(s.x - 10f, s.y - 7.6f, s.x + 8f, s.y + 0.4f)) }
        volumen(l, r, hogaza, s.x - 10f, s.x + 8f, Color(0xFFC89A58))
        drawOval(l.tono(Color(0xFFDDB878)), Offset(s.x - 8.6f, s.y - 7.6f), Size(15.2f, 4.6f))
        val c = Offset(s.x - 1f, s.y - 5.3f)
        for (i in 0 until 4) { val a = i * 0.785f; drawLine(l.tono(Color(0xFF8A5E30)), c + Offset(cos(a) * 7.4f, sin(a) * 2.2f), c - Offset(cos(a) * 7.4f, sin(a) * 2.2f), 0.5f) }
        drawPath(poligono(Offset(s.x + 10f, s.y), Offset(s.x + 17f, s.y), Offset(s.x + 15.4f, s.y - 4.6f)), l.tono(Color(0xFFC89A58))); drawPath(poligono(Offset(s.x + 10f, s.y), Offset(s.x + 15.4f, s.y - 4.6f), Offset(s.x + 12.4f, s.y - 3.4f)), l.tono(Color(0xFFF0E0B8)))
        repeat(5) { drawCircle(l.tono(Color(0xFFF0E0B8)), 0.35f, Offset(s.x + 6f + it * 2.3f, s.y + 0.7f - (it % 2) * 0.6f)) }
    }

    /** La paga: un montón de denarios y la bolsa de cuero de la que salieron. */
    private fun DrawScope.monedas(l: Luces, r: Rayo, s: Offset) {
        val bolsa = Path().apply { moveTo(s.x - 13f, s.y); cubicTo(s.x - 16f, s.y - 5f, s.x - 13f, s.y - 9f, s.x - 9.6f, s.y - 9.6f); lineTo(s.x - 11f, s.y - 12.4f); lineTo(s.x - 5f, s.y - 12.4f); lineTo(s.x - 6.4f, s.y - 9.6f); cubicTo(s.x - 3f, s.y - 9f, s.x, s.y - 5f, s.x - 3f, s.y); close() }
        volumen(l, r, bolsa, s.x - 15f, s.x - 1f, C.CUERO)
        drawLine(l.tono(C.CUERO_OSC), Offset(s.x - 10.4f, s.y - 9.8f), Offset(s.x - 5.6f, s.y - 9.8f), 1f, StrokeCap.Round)
        val plata = l.tono(Color(0xFFC8C8CC)); val plataLuz = l.tono(Color(0xFFF2F2F4)); val plataOsc = l.tono(Color(0xFF8A8A92))
        listOf(Offset(3f, 0f), Offset(8f, 0f), Offset(5.4f, -1.4f), Offset(12.6f, 0f), Offset(10f, -1.5f), Offset(7.6f, -2.9f), Offset(16.6f, 0.2f)).forEach { d ->
            drawOval(plataOsc, Offset(s.x + d.x - 2.4f, s.y + d.y - 1.1f), Size(4.8f, 1.9f)); drawOval(plata, Offset(s.x + d.x - 2.4f, s.y + d.y - 1.6f), Size(4.8f, 1.9f)); drawOval(plataLuz, Offset(s.x + d.x - 1.4f, s.y + d.y - 1.4f), Size(2f, 0.8f))
        }
    }

    /** La tablilla de cera: un díptico abierto, con su punzón. Sin letras: solo los surcos. */
    private fun DrawScope.tablilla(l: Luces, r: Rayo, s: Offset) {
        val marco = l.tono(C.MADERA); val cera = l.tono(Color(0xFF3E3428))
        for (x in listOf(-14f, 0.6f)) {
            drawPath(poligono(Offset(s.x + x + 1.4f, s.y - 5.4f), Offset(s.x + x + 13.4f, s.y - 5.4f), Offset(s.x + x + 12.6f, s.y), Offset(s.x + x, s.y)), marco)
            drawPath(poligono(Offset(s.x + x + 2.6f, s.y - 4.6f), Offset(s.x + x + 12.2f, s.y - 4.6f), Offset(s.x + x + 11.4f, s.y - 0.8f), Offset(s.x + x + 1.6f, s.y - 0.8f)), cera)
            for (i in 0..2) drawPath(Path().apply { val y = s.y - 3.8f + i * 1.1f; moveTo(s.x + x + 3f, y); cubicTo(s.x + x + 5f, y - 0.5f, s.x + x + 7.6f, y + 0.5f, s.x + x + (if (i == 2) 7.6f else 10.6f), y) }, l.tono(Color(0xFFB8A888)).copy(alpha = 0.7f), style = Stroke(0.3f))
        }
        drawLine(l.tono(C.CUERO_OSC), Offset(s.x - 0.6f, s.y - 4f), Offset(s.x + 0.8f, s.y - 4f), 0.6f); drawLine(l.tono(C.CUERO_OSC), Offset(s.x - 1f, s.y - 1.4f), Offset(s.x + 0.6f, s.y - 1.4f), 0.6f)
        drawLine(l.tono(C.BRONCE), Offset(s.x + 15f, s.y - 0.4f), Offset(s.x + 23f, s.y - 3f), 0.9f, StrokeCap.Round); drawCircle(l.tono(C.BRONCE), 0.8f, Offset(s.x + 23f, s.y - 3f))
    }

    /** La lucerna de barro (la llama, de noche, va en la parte viva). */
    private fun DrawScope.lucerna(l: Luces, r: Rayo, s: Offset) {
        val cuerpo = Path().apply { moveTo(s.x - 8f, s.y - 1f); cubicTo(s.x - 9f, s.y - 5.6f, s.x + 3f, s.y - 6.4f, s.x + 7f, s.y - 4.4f); lineTo(s.x + 13f, s.y - 4.6f); lineTo(s.x + 13.4f, s.y - 3f); lineTo(s.x + 7f, s.y - 0.6f); cubicTo(s.x + 3f, s.y + 0.6f, s.x - 6f, s.y + 0.4f, s.x - 8f, s.y - 1f); close() }
        volumen(l, r, cuerpo, s.x - 9f, s.x + 13.4f, C.BARRO)
        drawOval(l.tono(Color(0xFF7A4226)), Offset(s.x - 3f, s.y - 5.4f), Size(5f, 1.6f)); drawCircle(l.tono(Color(0xFF2E241C)), 0.8f, Offset(s.x + 12.4f, s.y - 4f))
        drawOval(l.tono(Color(0xFF9A5A34)), Offset(s.x - 12.4f, s.y - 5.4f), Size(5f, 4.4f), style = Stroke(1.1f))
    }

    /** Una rama de ciprés, por el luto. */
    private fun DrawScope.ramaDeCipres(l: Luces, s: Offset) {
        drawLine(l.tono(Color(0xFF4E3C2A)), s + Offset(-8f, -0.6f), s + Offset(8f, -2.4f), 0.6f, StrokeCap.Round)
        for (i in 0..9) { val p = s + Offset(-7f + i * 1.6f, -0.8f - i * 0.2f); drawLine(l.tono(if (i % 2 == 0) Color(0xFF24402E) else Color(0xFF3F6442)), p, p + Offset(1.4f, if (i % 2 == 0) -2.6f else 1.6f), 0.9f, StrokeCap.Round) }
    }

    // ---------------------------------------------------------------- Lo que se mueve en el camino de ronda

    fun DrawScope.caminoVivo(m: Momento, l: Luces, reloj: Float, viento: Float) {
        // El brasero: llama y humo a las horas de comer y de estar juntos; brasas después. Frío mientras él está en el camino.
        val boca = BRASERO + Offset(0f, -18f * KB)
        if (EscenaRomano.comiendo(m)) {
            llama(BRASERO + Offset(-7f * KB, -16f * KB), 6f * KB, reloj, 1); llama(BRASERO + Offset(7f * KB, -16f * KB), 5.4f * KB, reloj, 2)
            resplandor(boca, 34f * KB, 0.25f + 0.5f * l.noche)
            humo(l, BRASERO + Offset(0f, -30f * KB), 64f, 16f, reloj, viento, 0.12f, 0f, 4, 10, 0.8f)
        } else if (EscenaRomano.conGente(m)) {
            llama(boca + Offset(-3f, 1f), 9f * KB, reloj, 1); llama(boca + Offset(4f, 1f), 7f * KB, reloj, 2)
            resplandor(boca, 40f * KB, 0.25f + 0.55f * l.noche)
            humo(l, boca + Offset(0f, -10f), 56f, 14f, reloj, viento, 0.15f, 0f, 4, 9, 0.7f)
        } else if (EscenaRomano.puesto(m) == Puesto.DE_GUARDIA) {
            brasas(l, boca + Offset(0f, 1.6f), 15f * KB, reloj)
            if (l.noche > 0.3f) resplandor(boca, 30f * KB, 0.5f * l.noche, Color(0xFFFF5A1E))
        }
        // El farol de la puerta, encendido toda la noche: es un puesto de guardia.
        if (l.noche > 0.25f) { llama(FAROL + Offset(0f, 0.6f), 4f, reloj, 3); resplandor(FAROL + Offset(0f, -2f), 54f, 0.6f * l.noche) }
        // Las antorchas del parapeto, las noches de fiesta.
        if (EscenaRomano.antorchas(m)) for ((i, x) in listOf(W + 16f, 4f * W + 16f).withIndex()) { antorcha(l, Offset(x, ALMENA - 2f), 11f, reloj, 0f, 6 + i); resplandor(Offset(x, ALMENA - 14f), 30f, 0.5f * l.noche) }
        if (m.ficha.objeto == "lucerna" && l.noche > 0.3f && m.hacer != Hacer.DORMIR) llama(SOBRE + Offset(12.6f * KO, -4.6f * KO), 4f * KO, reloj, 4)
    }
}
